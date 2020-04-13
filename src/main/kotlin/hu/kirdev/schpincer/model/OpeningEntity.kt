package hu.kirdev.schpincer.model

import com.fasterxml.jackson.annotation.JsonIdentityInfo
import com.fasterxml.jackson.annotation.JsonIdentityReference
import com.fasterxml.jackson.annotation.ObjectIdGenerators.PropertyGenerator
import hu.kirdev.schpincer.service.OpeningService
import java.io.Serializable
import javax.persistence.*
import javax.validation.constraints.Size

const val MILLIS_TO_MINS: Long = 60000

@Entity
@Table(name = "openings")
data class OpeningEntity(

        @Id
        @Column
        @GeneratedValue(strategy = GenerationType.AUTO)
        var id: Long? = null,

        @Column
        var timeIntervals: Int = 0,

        @Column
        var dateStart: Long,

        @Column
        var dateEnd: Long,

        @Column
        var orderStart: Long,

        @Column
        var orderEnd: Long,

        @Column
        var prUrl: String? = null,

        @Column
        var eventDescription: String? = null,

        @Column(length = 255)
        var feeling: @Size(max = 255) String? = null,

        @JsonIdentityInfo(generator = PropertyGenerator::class, property = "id")
        @JsonIdentityReference(alwaysAsId = true)
        @ManyToOne(fetch = FetchType.LAZY)
        var circle: CircleEntity? = null,

        @Column
        @OrderBy("id ASC")
        @OneToMany(mappedBy = "opening", fetch = FetchType.LAZY, orphanRemoval = true)
        var timeWindows: MutableList<TimeWindowEntity> = mutableListOf(),

        @Column
        var maxOrder: Int = 0,

        @Column
        var maxOrderPerInterval: Int = 0,

        @Column
        var maxExtraPerInterval: Int = 0,

        @Column
        var intervalLength: Int = 0,

        @Column
        var orderCount: Int = 0,

        @Column(nullable = false)
        var maxAlpha: Int = 0,

        @Column(nullable = false)
        var maxBeta: Int = 0,

        @Column(nullable = false)
        var maxGamma: Int = 0,

        @Column(nullable = false)
        var maxDelta: Int = 0,

        @Column(nullable = false)
        var maxLambda: Int = 0,

        @Column(nullable = false)
        var usedAlpha: Int = 0,

        @Column(nullable = false)
        var usedBeta: Int = 0,

        @Column(nullable = false)
        var usedGamma: Int = 0,

        @Column(nullable = false)
        var usedDelta: Int = 0,

        @Column(nullable = false)
        var usedLambda: Int = 0

) : Serializable {

    fun generateTimeWindows(openings: OpeningService) {
        if (this.intervalLength <= 0) {
            appendTimeWindow(openings, this.dateStart)
            return
        }
        var time: Long = this.dateStart
        while (time < this.dateEnd) {
            appendTimeWindow(openings, time)
            time += this.intervalLength * MILLIS_TO_MINS
        }
    }

    fun appendTimeWindow(openings: OpeningService, time: Long) {
        val tw = TimeWindowEntity(null, this,
                OpeningService.DATE_FORMATTER_HH_MM.format(time)
                        + " - " + OpeningService.DATE_FORMATTER_HH_MM.format(time + this.intervalLength * MILLIS_TO_MINS),
                time,
                this.maxOrderPerInterval,
                this.maxExtraPerInterval)
        timeWindows.add(tw)
        openings.saveTimeWindow(tw)
    }

    fun isInInterval(timeMillis: Long) = orderStart <= timeMillis && orderEnd >= timeMillis

}
