package hu.gerviba.webschop.service;

import hu.gerviba.webschop.dao.ItemEntityDto;
import hu.gerviba.webschop.model.ItemEntity;
import hu.gerviba.webschop.model.OpeningEntity;
import org.apache.lucene.search.Query;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.FullTextQuery;
import org.hibernate.search.jpa.Search;
import org.hibernate.search.query.dsl.QueryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class HibernateSearchService {

	EntityManager entityManager;

    @Autowired
    ItemService items;
    
    @Autowired
    OpeningService openings;
	
	@Value("${search.distance}")
	private int distance = 1;

	@Value("${search.name-boost}")
	private float nameBoost = 10.0F;

	@Value("${search.ingred-boost}")
	private float ingredBoost = 1.0F;

	@Value("${search.time-limit-sec}")
	private int timeLimitSec = 10;
	

	@Autowired
	public HibernateSearchService(final EntityManagerFactory entityManagerFactory) {
		this.entityManager = entityManagerFactory.createEntityManager();
	}

    @PostConstruct
	public void initializeHibernateSearch() {
		try {
			FullTextEntityManager fullTextEntityManager = Search.getFullTextEntityManager(entityManager);
			fullTextEntityManager.createIndexer().startAndWait();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Transactional
	public List<ItemEntityDto> fuzzySearchItem(String matchingString, boolean loggedIn) {
        Map<Long, OpeningEntity> cache = new HashMap<>();
		matchingString = matchingString.trim();
		if (matchingString.length() < 3)
			return items.findAll().stream()
	                .map(item -> new ItemEntityDto(item, cache.computeIfAbsent(
	                        item.getCircle().getId(), 
	                        (i) -> openings.findNextOf(i)), 
	                        loggedIn))
	                .collect(Collectors.toList());

		FullTextEntityManager fullTextEntityManager = Search.getFullTextEntityManager(entityManager);
		QueryBuilder qb = fullTextEntityManager.getSearchFactory().buildQueryBuilder().forEntity(ItemEntity.class).get();

		Query luceneQuery = qb.keyword().fuzzy().withEditDistanceUpTo(distance).withPrefixLength(1)
				.onFields("name", "keywords").boostedTo(nameBoost)
				.andField("ingredients").boostedTo(ingredBoost)
				.matching(matchingString).createQuery();

		javax.persistence.Query jpaQuery = fullTextEntityManager.createFullTextQuery(luceneQuery, ItemEntity.class);

		List<ItemEntity> results = List.of();
		try {
			results = ((FullTextQuery) jpaQuery)
					.limitExecutionTimeTo(timeLimitSec, TimeUnit.SECONDS)
					.getResultList();
		} catch (NoResultException nre) { }

		return results.stream()
				.map(itemEntity -> new ItemEntityDto(itemEntity,
						cache.computeIfAbsent(itemEntity.getCircle().getId(),
								(i) -> openings.findNextOf(i)),
						loggedIn))
				.collect(Collectors.toList());

	}

}
