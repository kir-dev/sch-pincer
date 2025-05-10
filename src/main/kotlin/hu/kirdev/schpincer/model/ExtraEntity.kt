package hu.kirdev.schpincer.model

import hu.kirdev.schpincer.web.component.CustomComponentType
import jakarta.persistence.*

@Entity
@Table(name = "extras")
data class ExtraEntity (
    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long = 0,

    @ManyToOne
    var circle: CircleEntity,

    @Column
    var category: String,

    @Column
    var selectedIndex: Int,

    @Column
    var displayName: String,

    @Column
    var inputType: CustomComponentType,

    @Column
    var name: String,

    @Column
    var price: Int
)
