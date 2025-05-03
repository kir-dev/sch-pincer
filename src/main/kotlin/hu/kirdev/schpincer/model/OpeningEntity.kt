package hu.kirdev.schpincer.model

import com.fasterxml.jackson.annotation.JsonBackReference
import com.fasterxml.jackson.annotation.JsonIdentityInfo
import com.fasterxml.jackson.annotation.JsonIdentityReference
import com.fasterxml.jackson.annotation.ObjectIdGenerators.PropertyGenerator
import hu.kirdev.schpincer.service.OpeningService
import java.io.Serializable
import jakarta.persistence.*
import jakarta.validation.constraints.Size

const val MILLIS_TO_MINS: Long = 60000

@Entity
@Table(name = "openings")
data class OpeningEntity(

        @Id
        @Column
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        var id: Long = 0,

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

        @field:JsonIdentityInfo(generator = PropertyGenerator::class, property = "id")
        @field:JsonIdentityReference(alwaysAsId = true)
        @field:ManyToOne(fetch = FetchType.LAZY)
        @get:JsonIdentityInfo(generator = PropertyGenerator::class, property = "id")
        @get:JsonIdentityReference(alwaysAsId = true)
        @get:ManyToOne(fetch = FetchType.LAZY)
        @JsonBackReference
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
        var usedLambda: Int = 0,

        @Column(nullable = false, columnDefinition = "bigint(20) DEFAULT 0")
        var compensationTime: Long = 0

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

        val tw = TimeWindowEntity(0, this, "Túlóra", this.dateEnd, 0, Integer.MIN_VALUE)
        timeWindows.add(tw)
        openings.saveTimeWindow(tw)
    }

    private fun appendTimeWindow(openings: OpeningService, time: Long) {
        val tw = TimeWindowEntity(0, this,
                OpeningService.DATE_FORMATTER_HH_MM.format(time)
                        + " - " + OpeningService.DATE_FORMATTER_HH_MM.format(time + this.intervalLength * MILLIS_TO_MINS),
                time,
                this.maxOrderPerInterval,
                this.maxExtraPerInterval)
        timeWindows.add(tw)
        openings.saveTimeWindow(tw)
    }

    fun isInInterval(timeMillis: Long) = timeMillis in orderStart..orderEnd

}
