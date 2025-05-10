package hu.kirdev.schpincer.model

import com.fasterxml.jackson.annotation.JsonIdentityInfo
import com.fasterxml.jackson.annotation.JsonIdentityReference
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.ObjectIdGenerators.PropertyGenerator
import java.io.Serializable
import jakarta.persistence.*

@Entity
@Table(name = "items")
data class ItemEntity(

        @Id
        @Column
        @GeneratedValue(strategy = GenerationType.AUTO)
        var id: Long = 0,

        @Column
        var name: String = "",

        @JsonIdentityInfo(generator = PropertyGenerator::class, property = "id")
        @JsonIdentityReference(alwaysAsId = true)
        @ManyToOne(fetch = FetchType.LAZY)
        var circle: CircleEntity? = null,

        @Column(columnDefinition = "text")
        var description: String = "",

        @Column(columnDefinition = "text")
        var ingredients: String = "",

        @JsonIgnore
        @Column
        var keywords: String? = null,

        @Column(columnDefinition = "text")
        var detailsConfigJson: String = "[]",

        @Column
        var price: Int = 0,

        @Column
        var orderable: Boolean = false,

        @Column
        var visible: Boolean = false,

        @Column
        var service: Boolean = false,

        @Column
        var visibleInAll: Boolean = false,

        @Column
        var visibleWithoutLogin: Boolean = false,

        @Column
        var personallyOrderable: Boolean = false,

        @Column
        var imageName: String? = "",

        @Column
        var flag: Int = 0,

        @Column(nullable = false)
        var precedence: Int = 0,

        @Column(nullable = false)
        var discountPrice: Int = 0,

        @Column(nullable = false)
        var category: Int = 0,

        @Column(nullable = false)
        var manualPrecedence: Int = 0,

        @Column(nullable = false)
        var alias: String = ""

) : Serializable
