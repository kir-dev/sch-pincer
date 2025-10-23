package hu.kirdev.schpincer.model

import jakarta.persistence.*
import jakarta.validation.constraints.Size
import org.hibernate.annotations.ColumnDefault
import java.io.Serializable

@Entity
@Table(name = "orders", indexes = [Index(columnList = "openingId"), Index(columnList = "userId")])
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

    @Column(columnDefinition = "text")
    var detailsJson: String,

    @Column
    var intervalId: Long = 0,

    @Column
    var intervalMessage: String = "",

    @Column
    var date: Long = 0,

    @Column
    var comment: String,

    @ColumnDefault("''")
    @Column(nullable = false, columnDefinition = "varchar(255)")
    var additionalComment: String = "",

    @ColumnDefault("''")
    @Column(nullable = false, columnDefinition = "varchar(255)")
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

    @ColumnDefault("0")
    @Column(nullable = false, columnDefinition = "bigint")
    var createdAt: Long = 0,

    @ManyToMany
    var extras: MutableSet<ExtraEntity> = mutableSetOf()

) : Serializable
