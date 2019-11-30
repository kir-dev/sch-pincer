package hu.gerviba.webschop.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import hu.gerviba.webschop.service.OpeningService;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@Table(name = "openings")
@SuppressWarnings("serial")
public class OpeningEntity implements Serializable {
    
    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    
    @Column
    private int timeIntervals;
    
    @Column
    private Long dateStart;
    
    @Column
    private Long dateEnd;

    @Column
    private Long orderStart;
    
    @Column
    private Long orderEnd;

    @Column
    private String prUrl;
    
    @Column
    private String eventDescription;

    @Column(length = 255)
    @Size(max = 255)
    private String feeling;
    
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
    @JsonIdentityReference(alwaysAsId = true)
    @ManyToOne(fetch = FetchType.LAZY)
    private CircleEntity circle;

    @Column
    @OrderBy("id ASC")
    @OneToMany(mappedBy = "opening", fetch = FetchType.LAZY, orphanRemoval = true)
    private List<TimeWindowEntity> timeWindows;
    
    @Column
    @Min(0)
    private int maxOrder;

    @Column
    @Min(0)
    private int maxOrderPerInterval;

    @Column
    @Min(0)
    private int maxExtraPerInterval;
    
    @Column
    @Min(0)
    private int intervalLength;
    
    @Column
    @Min(0)
    private int orderCount;

    @Column(nullable = false)
    private Integer maxAlpha = 0;

    @Column(nullable = false)
    private Integer maxBeta = 0;

    @Column(nullable = false)
    private Integer maxGamma = 0;

    @Column(nullable = false)
    private Integer maxDelta = 0;

    @Column(nullable = false)
    private Integer maxLambda = 0;

    @Column(nullable = false)
    private Integer usedAlpha = 0;

    @Column(nullable = false)
    private Integer usedBeta = 0;

    @Column(nullable = false)
    private Integer usedGamma = 0;

    @Column(nullable = false)
    private Integer usedDelta = 0;

    @Column(nullable = false)
    private Integer usedLambda = 0;

    private static final long MILLIS_TO_MINS = 60000;
    
    /**
     * This method is for testing purposes
     */
    public OpeningEntity(Long dateStart, Long dateEnd, Long orderStart, Long orderEnd, String prUrl, String eventDescription,
            String feeling, CircleEntity circle, int maxOrder, int maxOrderPerInterval, int maxExtraPerInterval,
            int intervalLength) {
        this.dateStart = dateStart;
        this.dateEnd = dateEnd;
        this.orderStart = orderStart;
        this.orderEnd = orderEnd;
        this.prUrl = prUrl;
        this.eventDescription = eventDescription;
        this.feeling = feeling;
        this.circle = circle;
        this.maxOrder = maxOrder;
        this.maxOrderPerInterval = maxOrderPerInterval;
        this.maxExtraPerInterval = maxExtraPerInterval;
        this.intervalLength = intervalLength;
        this.orderCount = 0;
    }

    public void generateTimeWindows(OpeningService openings) {
        timeWindows = new ArrayList<>();
        if (this.getIntervalLength() <= 0) {
            appendTimeWindow(openings, this.getDateStart());
            return;
        }
        
        for (long time = this.getDateStart(); time < this.getDateEnd(); time += this.getIntervalLength() * MILLIS_TO_MINS)
            appendTimeWindow(openings, time);
    }

    private void appendTimeWindow(OpeningService openings, long time) {
        TimeWindowEntity tw = new TimeWindowEntity(null, this, 
                OpeningService.DATE_FORMATTER_HH_MM.format(time)
                    + " - " + OpeningService.DATE_FORMATTER_HH_MM.format(time + this.getIntervalLength() * MILLIS_TO_MINS),
                time,
                this.getMaxOrderPerInterval(),
                this.getMaxExtraPerInterval());
        timeWindows.add(tw);
        openings.saveTimeWindow(tw);
    }

    public boolean isInInterval(long timeMillis) {
        return orderStart <= timeMillis && orderEnd >= timeMillis;
    }
    
}
