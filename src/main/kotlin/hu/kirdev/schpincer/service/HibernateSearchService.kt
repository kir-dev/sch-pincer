package hu.kirdev.schpincer.service

import hu.gerviba.webschop.dto.ItemEntityDto
import hu.kirdev.schpincer.model.ItemEntity
import hu.kirdev.schpincer.model.OpeningEntity
import org.hibernate.search.jpa.FullTextQuery
import org.hibernate.search.jpa.Search
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.concurrent.TimeUnit
import javax.annotation.PostConstruct
import javax.persistence.EntityManager
import javax.persistence.EntityManagerFactory
import javax.persistence.NoResultException

@Service
open class HibernateSearchService(entityManagerFactory: EntityManagerFactory) {

	private val log = LoggerFactory.getLogger(javaClass)

    val entityManager: EntityManager

    @Autowired
    private lateinit var items: ItemService

    @Autowired
    private lateinit var openings: OpeningService

    @Value("\${search.distance:1}")
    private var distance: Int = 1

    @Value("\${search.name-boost:10.0}")
    private var nameBoost: Float = 10.0f

    @Value("\${search.ingred-boost:1.0}")
    private var ingredBoost: Float = 1.0f

    @Value("\${search.time-limit-sec:10}")
    private var timeLimitSec: Long = 10

    init {
        entityManager = entityManagerFactory.createEntityManager()
    }

    @PostConstruct
	fun initializeHibernateSearch() {
		try {
			val fullTextEntityManager = Search.getFullTextEntityManager(entityManager)
			fullTextEntityManager.createIndexer().startAndWait()
		} catch (e: InterruptedException) {
			log.error("Failed to initialize Hibernate Search", e)
		}
	}

	@Transactional
	open fun fuzzySearchItem(matching: String, loggedIn: Boolean): List<ItemEntityDto> {
        val cache: HashMap<Long, OpeningEntity?> = HashMap()
		val matchingString = matching.trim()
		if (matchingString.length < 3) {
			return items.findAll()
	                .map{ item -> ItemEntityDto(
							base = item,
							opening = cache.computeIfAbsent(item.circle?.id!!)
									{ i -> openings.findNextOf(i) },
	                        loggedin = loggedIn)
					}
		}

		val fullTextEntityManager = Search.getFullTextEntityManager(entityManager)
		val qb = fullTextEntityManager.getSearchFactory().buildQueryBuilder().forEntity(ItemEntity::class.java).get()

		val luceneQuery = qb.keyword().fuzzy().withEditDistanceUpTo(distance).withPrefixLength(1)
				.onFields("name", "keywords").boostedTo(nameBoost)
				.andField("ingredients").boostedTo(ingredBoost)
				.matching(matchingString).createQuery()

		val jpaQuery: javax.persistence.Query = fullTextEntityManager.createFullTextQuery(luceneQuery, ItemEntity::class.java)

		val results = try {
			(jpaQuery as FullTextQuery)
					.limitExecutionTimeTo(timeLimitSec, TimeUnit.SECONDS)
					.getResultList()
					.filter { it != null }
		} catch (nre: NoResultException) {
			return listOf()
		}

		return results.map { itemEntity ->
			ItemEntityDto(itemEntity as ItemEntity,
					cache.computeIfAbsent(itemEntity.circle?.id!!,
							{ i -> openings.findNextOf(i) }),
					loggedIn)
		}

	}

}