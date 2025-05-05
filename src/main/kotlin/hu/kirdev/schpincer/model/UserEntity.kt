package hu.kirdev.schpincer.model

import java.io.Serializable
import org.hibernate.annotations.BatchSize
import jakarta.persistence.*

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

        @Column
        var cardType: CardType = CardType.DO,

        @Column
        @ElementCollection(fetch = FetchType.EAGER)
        @BatchSize(size = 1000)
        var permissions: Set<String> = mutableSetOf(),

        @Column(nullable = false)
        var orderingPriority: Int = 1,

        @Column
        var forceGrantLoginAccess: Boolean = false

) : Serializable
