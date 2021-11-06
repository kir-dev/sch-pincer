package hu.kirdev.schpincer.model

import java.io.Serializable
import javax.persistence.*
import javax.validation.constraints.Size

@Entity
@Table(name = "orders")
data class OrderEntity(
        @Id
        @Column
        @GeneratedValue(strategy = GenerationType.AUTO)
        var id: Long = 0,

        @Column
        var userId: String,

        @Column
        var openingId: Long? = null,

        @Column
        var userName: String,

        @Deprecated("")
        @Column(nullable = false)
        var artificialId: Int = 0,

        @Transient
        var artificialTransientId: Int = Int.MAX_VALUE,

        @Column
        @Enumerated(EnumType.ORDINAL)
        var status: OrderStatus = OrderStatus.ACCEPTED,

        @Column(length = 255)
        var name: String = "",

        @ManyToOne(optional = true)
        var orderedItem: ItemEntity? = null,

        @Lob
        @Column
        var detailsJson: String,

        @Column
        var intervalId: Long = 0,

        @Column
        var intervalMessage: String = "",

        @Column
        var date: Long = 0,

        @Column
        var comment: String,

        @Column(nullable = false, columnDefinition = "varchar(255) not null default ''") // FIXME: remove after migration
        var additionalComment: String = "",

        @Column(nullable = false, columnDefinition = "varchar(255) not null default ''")
        var chefComment: String = "",

        @Column
        var room: @Size(max = 8) String = "",

        @Column
        var price: Int = 0,

        @Column
        var extra: String = "",

        @Column
        var extraTag: Boolean = false,

        @Column
        var cancelUntil: Long = 0,

        @Column
        var systemComment: String = "",

        @Column(nullable = false)
        var count: Int = 1,

        @Column(nullable = false)
        var category: Int = 0,

        @Column(nullable = false)
        var priority: Int = 1,

        @Column(nullable = false)
        var compactName: String = "",

        @Column
        var reviewId: Long? = null,

        @Column(nullable = false, columnDefinition = "bigint(20) not null default 0")
        var createdAt: Long = 0,

        @ManyToMany
        var extras: Set<ExtraEntity> = setOf()

) : Serializable