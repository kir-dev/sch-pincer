package hu.kirdev.schpincer.model

import com.fasterxml.jackson.annotation.JsonIdentityInfo
import com.fasterxml.jackson.annotation.JsonIdentityReference
import com.fasterxml.jackson.annotation.ObjectIdGenerators.PropertyGenerator
import java.io.Serializable
import javax.persistence.*

@Entity
@Table(name = "timewindows")
data class TimeWindowEntity(
    @Id
    @GeneratedValue
    @Column
    var id: Long? = null,

    @JsonIdentityInfo(generator = PropertyGenerator::class, property = "id")
    @JsonIdentityReference(alwaysAsId = true)
    @ManyToOne(fetch = FetchType.LAZY)
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