package hu.gerviba.webschop.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "openings")
@Data
@NoArgsConstructor
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

    @Column(length = 255)
    @Size(max = 255)
    private String feeling;
    
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
    @JsonIdentityReference(alwaysAsId = true)
    @ManyToOne(fetch = FetchType.LAZY)
    private CircleEntity circle;

    @Column
    @Min(0)
    private int maxOrder;

    @Column
    @Min(0)
    private int maxOrderPerHalfHour;

    public OpeningEntity(Long dateStart, Long dateEnd, Long orderStart, Long orderEnd, String prUrl,
            String feeling, CircleEntity circle, int maxOrder, int maxOrderPerHalfHour) {
        this.dateStart = dateStart;
        this.dateEnd = dateEnd;
        this.orderStart = orderStart;
        this.orderEnd = orderEnd;
        this.prUrl = prUrl;
        this.feeling = feeling;
        this.circle = circle;
        this.maxOrder = maxOrder;
        this.maxOrderPerHalfHour = maxOrderPerHalfHour;
    }
    
}
