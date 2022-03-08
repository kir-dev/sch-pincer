package hu.kirdev.schpincer.service

import com.fasterxml.jackson.databind.ObjectMapper
import hu.kirdev.schpincer.dao.CircleRepository
import hu.kirdev.schpincer.dao.ExtrasRepository
import hu.kirdev.schpincer.dao.ItemRepository
import hu.kirdev.schpincer.model.CircleEntity
import hu.kirdev.schpincer.model.ExtraEntity
import hu.kirdev.schpincer.model.ItemEntity
import hu.kirdev.schpincer.web.component.CustomComponentModel
import hu.kirdev.schpincer.web.component.CustomComponentModelList
import hu.kirdev.schpincer.web.component.CustomComponentType
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import javax.annotation.PostConstruct

@Service
class ExtrasService {

    @Autowired
    lateinit var extrasRepository: ExtrasRepository

    @Autowired
    lateinit var itemRepository: ItemRepository

    @Autowired
    lateinit var circleRepository: CircleRepository

    @Autowired
    lateinit var mapper: ObjectMapper

    fun createExtrasForCircle(circle: CircleEntity) {

        val circleItems = itemRepository.findAllByCircle_Id(circle.id)
        circleItems.forEach {

            /**
             * @see CustomComponentType
             * */
            val models = mapper.readValue(("{\"models\":${it.detailsConfigJson}}").toByteArray(), CustomComponentModelList::class.java)
            val mapped: Map<String, CustomComponentModel> = models.models.associateBy({ it.name }, { it })

            mapped.forEach { that ->
                val mappedExtra = that.value
                for (i in 0 until mappedExtra.values.size) {
                    createOrUpdateExtra(mappedExtra, i, circle, it)
                }
            }

        }

    }

    private fun createOrUpdateExtra(mappedExtra: CustomComponentModel, i: Int, circle: CircleEntity, item: ItemEntity) {
        val optionalExtra = extrasRepository.findByItemAndNameAndInputTypeAndSelectedIndex(
                item,
                mappedExtra.name,
                CustomComponentType.valueOf(mappedExtra.type),
                i
        )
        optionalExtra.ifPresentOrElse({
            it.price = mappedExtra.prices[i]
            extrasRepository.save(it)
        }) {
            val extra = ExtraEntity(
                    circle = circle,
                    category = mappedExtra.aliases.getOrElse(i) { i.toString() },
                    selectedIndex = i,
                    inputType = CustomComponentType.valueOf(mappedExtra.type),
                    name = mappedExtra.name,
                    displayName = mappedExtra.values[i],
                    price = mappedExtra.prices[i]
            )
            extra.item = item
            extrasRepository.save(extra)
        }
    }

    @PostConstruct
    fun generateAllExtrasForAllCircles() {

        for (circle in circleRepository.findAll()) {
            createExtrasForCircle(circle)
        }

    }

}