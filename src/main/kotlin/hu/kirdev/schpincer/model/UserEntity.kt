package hu.kirdev.schpincer.model

import jakarta.persistence.*
import org.hibernate.annotations.BatchSize
import org.hibernate.annotations.ColumnDefault
import java.io.Serializable

@Entity
@Table(name = "users")
data class UserEntity(

    @Id
    @Column(unique = true)
    var uid: String = "",

    @Column
    var name: String,

    @Column
    var email: String? = null,

    @Column
    var room: String = "",

    @Column
    var sysadmin: Boolean = false,

    @Column(name = "card_type")
    var pekCardType: CardType = CardType.DO,

    @Column(nullable = false)
    @ColumnDefault("0")
    var alwaysGrantAb: Boolean = false,

    @Column
    @ElementCollection(fetch = FetchType.EAGER)
    @BatchSize(size = 1000)
    var permissions: Set<String> = mutableSetOf(),

    @Column(nullable = false)
    var orderingPriority: Int = 1,

    @Column
    var forceGrantLoginAccess: Boolean = false

) : Serializable {
    @get:Transient
    val grantedCardType: CardType
        get() = if (alwaysGrantAb) CardType.AB else pekCardType

}
