package hu.kirdev.schpincer.model

import com.fasterxml.jackson.annotation.JsonIdentityInfo
import com.fasterxml.jackson.annotation.JsonIdentityReference
import com.fasterxml.jackson.annotation.ObjectIdGenerators.PropertyGenerator
import java.io.Serializable
import jakarta.persistence.*

@Entity
@Table(name = "timewindows")
data class TimeWindowEntity(
        @Id
        @GeneratedValue
        @Column
        var id: Long = 0,

        @field:JsonIdentityInfo(generator = PropertyGenerator::class, property = "id")
        @field:JsonIdentityReference(alwaysAsId = true)
        @field:ManyToOne(fetch = FetchType.LAZY)
        @get:JsonIdentityInfo(generator = PropertyGenerator::class, property = "id")
        @get:JsonIdentityReference(alwaysAsId = true)
        @get:ManyToOne(fetch = FetchType.LAZY)
        var opening: OpeningEntity? = null,

        @Column
        var name: String = "",

        @Column
        var date: Long = 0,

        @Column
        var normalItemCount: Int = 0,

        @Column
        var extraItemCount: Int = 0

) : Serializable
