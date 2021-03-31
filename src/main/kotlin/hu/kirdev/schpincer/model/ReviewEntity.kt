package hu.kirdev.schpincer.model

import com.fasterxml.jackson.annotation.JsonIgnore
import java.io.Serializable
import javax.persistence.*
import javax.validation.constraints.NotBlank

@Entity
@Table(name = "reviews")
@SuppressWarnings("serial")
data class ReviewEntity(

        @Id
        @Column
        @GeneratedValue(strategy = GenerationType.AUTO)
        var id: Long = 0,

        @JsonIgnore
        @ManyToOne(fetch = FetchType.LAZY)
        var circle: CircleEntity? = null,

        @JsonIgnore
        @ManyToOne(fetch = FetchType.LAZY)
        var order: OrderEntity? = null,

        @Column
        var openingFeeling: String? = null,

        @Column
        var userName: String? = null,

        @Lob
        @Column
        var review: @NotBlank String = "",

        @Column
        var date: Long = 0,

        @Column
        var rateOverAll: Int = 0,

        @Column
        var rateSpeed: Int = 0,

        @Column
        var rateQuality: Int = 0,

        @Column
        var ratePrice: Int = 0

) : Serializable