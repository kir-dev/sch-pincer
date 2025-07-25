package hu.kirdev.schpincer.model

import com.fasterxml.jackson.annotation.JsonBackReference
import com.fasterxml.jackson.annotation.JsonIdentityInfo
import com.fasterxml.jackson.annotation.JsonIdentityReference
import com.fasterxml.jackson.annotation.ObjectIdGenerators.PropertyGenerator
import java.io.Serializable
import jakarta.persistence.*
import jakarta.validation.constraints.Size

@Entity
@Table(name = "circleMembers")
data class CircleMemberEntity(

        @Id
        @Column
        @GeneratedValue(strategy = GenerationType.AUTO)
        var id: Long = 0,

        @param:JsonIdentityInfo(generator = PropertyGenerator::class, property = "id")
        @param:JsonIdentityReference(alwaysAsId = true)
        @ManyToOne(fetch = FetchType.LAZY)
        @JsonBackReference
        var circle: CircleEntity? = null,

        @Column
        var name: @Size(max = 64) String? = null,

        @Column(name = "`rank`")
        var rank: @Size(max = 64) String? = null,

        @Column
        var avatar: String? = null,

        @Column(nullable = false)
        var sort: Int = 0,

        @Column(nullable = false)
        var precedence: Int = 0

) : Serializable
