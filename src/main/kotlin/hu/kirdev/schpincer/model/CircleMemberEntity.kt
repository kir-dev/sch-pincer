package hu.kirdev.schpincer.model

import com.fasterxml.jackson.annotation.JsonIdentityInfo
import com.fasterxml.jackson.annotation.JsonIdentityReference
import com.fasterxml.jackson.annotation.ObjectIdGenerators.PropertyGenerator
import java.io.Serializable
import javax.persistence.*
import javax.validation.constraints.Size

@Entity
@Table(name = "circleMembers")
data class CircleMemberEntity(

        @Id
        @Column
        @GeneratedValue(strategy = GenerationType.AUTO)
        var id: Long = 0,

        @JsonIdentityInfo(generator = PropertyGenerator::class, property = "id")
        @JsonIdentityReference(alwaysAsId = true)
        @ManyToOne(fetch = FetchType.LAZY)
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