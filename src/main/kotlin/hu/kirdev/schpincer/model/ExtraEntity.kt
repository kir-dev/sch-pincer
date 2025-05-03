package hu.kirdev.schpincer.model

import hu.kirdev.schpincer.web.component.CustomComponentType
import javax.persistence.*

@Entity
@Table(name = "extras")
data class ExtraEntity(
        @Id
        @Column
        @GeneratedValue(strategy = GenerationType.AUTO)
        var id: Long = 0,

        @Column
        val category: String,

        @Column
        val selectedIndex: Int,

        @Column
        val displayName: String,

        @Column
        val inputType: CustomComponentType,

        @Column
        val name: String,

        @Column
        val price: Int,

        @Column
        var active: Boolean? = true) {

    @ManyToOne
    @JoinColumn(name = "itemId", columnDefinition = "DEFAULT NULL")
    var item: ItemEntity? = null

}