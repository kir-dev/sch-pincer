package hu.kirdev.schpincer.service

import hu.kirdev.schpincer.dao.ItemRepository
import hu.kirdev.schpincer.model.ItemEntity
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import javax.transaction.Transactional

private const val FLAG_EDITORS_CHOICE = 1010
private const val FLAG_HIDDEN_EDITORS_CHOICE = 1011
private const val FLAG_FUNKY_ITEM = 1069

@Service
open class ItemPrecedenceService {

    @Autowired
    private lateinit var items: ItemRepository

    @Transactional
    open fun reorder() {
        val all = items.findAll()
        all.shuffle()
        val result: MutableList<ItemEntity> = all.asSequence()
                .filter { item: ItemEntity -> item.flag == FLAG_EDITORS_CHOICE || item.flag == FLAG_HIDDEN_EDITORS_CHOICE }
                .toMutableList()

        for (circleId in getShuffledCircleIds(all)) {
            result.addAll(all
                    .filter { item: ItemEntity -> item.circle?.id!! == circleId }
                    .sortedByDescending { a: ItemEntity -> a.flag % 100 }
            )
        }
        result.reverse()
        for (i in result.indices)
            result[i].precedence = i
        items.saveAll(result)
    }

    private fun getShuffledCircleIds(all: List<ItemEntity>): List<Long> {
        val circleIds = all.asSequence()
                .map { item: ItemEntity -> item.circle?.id!! }
                .distinct()
                .toMutableList()
        circleIds.shuffle()
        return circleIds
    }

}