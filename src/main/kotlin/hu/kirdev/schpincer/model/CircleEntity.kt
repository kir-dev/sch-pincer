package hu.kirdev.schpincer.model

import java.io.Serializable
import javax.persistence.*
import javax.validation.constraints.Size

@Entity
@Table(name = "circles")
data class CircleEntity(
        @Id
        @Column
        @GeneratedValue(strategy = GenerationType.AUTO)
        var id: Long? = null,

        @Size(min = 2, max = 32)
        @Column(length = 32)
        var displayName: @Size(min = 2, max = 32) String,

        @Lob
        @Size(max = 1000)
        @Column(length = 1000)
        var description: @Size(max = 1000) String = "",

        @Lob
        @Size(max = 1000)
        @Column(length = 1000)
        var homePageDescription: @Size(max = 1000) String? = null,

        @Size(max = 255)
        @Column(length = 255)
        var avgOpening: @Size(max = 255) String? = null,

        @Column
        var founded: Int = 0,

        @Column
        @OrderBy("sort DESC")
        @OneToMany(mappedBy = "circle", fetch = FetchType.LAZY, orphanRemoval = true)
        var members: List<CircleMemberEntity>? = null,

        @Column
        @OrderBy("dateStart ASC")
        @OneToMany(mappedBy = "circle", fetch = FetchType.LAZY, orphanRemoval = true)
        var openings: List<OpeningEntity>? = null,

        @Column
        var homePageOrder: Int = 0,

        @Column(length = 30)
        var cssClassName: @Size(max = 30) String = "",

        @Column(length = 255)
        var facebookUrl: @Size(max = 255) String? = null,

        @Column(length = 255)
        var websiteUrl: @Size(max = 255) String? = null,

        @Column(length = 255)
        var backgroundUrl: @Size(max = 255) String?,

        @Column(length = 255)
        var logoUrl: @Size(max = 255) String? = null,

        @Column
        var webhookNewOrderUrl: String? = null,

        @Column
        var webhookOrderDoneUrl: String? = null,

        @Column
        var alias: String,

        @Column(nullable = false)
        var visible: Boolean = false,

        @Column
        var virGroupId: Long? = null

) : Serializable {

    // TODO: Remove after full refactor
    @Deprecated("Remove after java to kotlin refactor")
    constructor() : this(id = null, displayName = "", cssClassName = "", backgroundUrl = "", alias = "")

    /**
     * This is for testing
     */
    constructor(displayName: String,
                description: String,
                homePageDescription: String,
                cssClassName: String,
                founded: Int,
                backgroundUrl: String,
                logoUrl: String,
                avgOpening: String,
                alias: String,
                facebookUrl: String,
                websiteUrl: String,
                visible: Boolean
    ) : this(displayName = displayName,
            description = description,
            homePageDescription = homePageDescription,
            cssClassName = cssClassName,
            founded = founded,
            backgroundUrl = backgroundUrl,
            logoUrl = logoUrl,
            avgOpening = avgOpening,
            alias = alias,
            facebookUrl = facebookUrl,
            websiteUrl = websiteUrl,
            visible = visible,
            webhookNewOrderUrl = ""
    )


    @Deprecated("Remove after java to kotlin refactor")
    fun copy(): CircleEntity = copy(id = this.id)

}