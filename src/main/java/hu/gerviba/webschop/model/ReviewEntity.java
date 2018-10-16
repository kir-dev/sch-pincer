package hu.gerviba.webschop.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "reviews")
@Data
@NoArgsConstructor
@SuppressWarnings("serial")
public class ReviewEntity implements Serializable {

    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    private CircleEntity circle;

    @Column
    private String userName;
    
    @Lob
    @NotBlank
    @Column
    private String review;
    
    @Column
    private long date;

    @Column
    @Min(1)
    @Max(5)
    private int rateOverAll;
    
    @Column
    @Min(1)
    @Max(5)
    private int rateSpeed;
    
    @Column
    @Min(1)
    @Max(5)
    private int rateQuality;
    
    @Column
    @Min(1)
    @Max(5)
    private int ratePrice;

    public ReviewEntity(CircleEntity circle) {
        this.circle = circle;
        this.rateOverAll = 5;
        this.rateSpeed = 5;
        this.rateQuality = 5;
        this.ratePrice = 5;
    }
    
    public ReviewEntity(CircleEntity circle, String userName, String review, long date,
            @Min(1) @Max(5) int rateOverAll, @Min(1) @Max(5) int rateSpeed, @Min(1) @Max(5) int rateQuality,
            @Min(1) @Max(5) int ratePrice) {
        this.circle = circle;
        this.userName = userName;
        this.review = review;
        this.date = date;
        this.rateOverAll = rateOverAll;
        this.rateSpeed = rateSpeed;
        this.rateQuality = rateQuality;
        this.ratePrice = ratePrice;
    }
    
}
