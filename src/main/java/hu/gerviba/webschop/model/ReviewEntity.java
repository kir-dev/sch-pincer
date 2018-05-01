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

import hu.gerviba.webschop.WebschopUtils;

@Entity
@Table(name = "reviews")
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

    public ReviewEntity() {}
    
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

    public Long getId() {
        return id;
    }

    public CircleEntity getCircle() {
        return circle;
    }

    public String getUserName() {
        return userName;
    }

    public String getReview() {
        return review;
    }

    public long getDate() {
        return date;
    }

    public int getRateOverAll() {
        return rateOverAll;
    }

    public int getRateSpeed() {
        return rateSpeed;
    }

    public int getRateQuality() {
        return rateQuality;
    }

    public int getRatePrice() {
        return ratePrice;
    }

    private static final long serialVersionUID = 7823917697337423919L;

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setCircle(CircleEntity circle) {
        this.circle = circle;
    }

    public void setReview(String review) {
        this.review = WebschopUtils.htmlEscape(review);
    }

    public void setDate(long date) {
        this.date = date;
    }

    public void setRateOverAll(int rateOverAll) {
        this.rateOverAll = rateOverAll;
    }

    public void setRateSpeed(int rateSpeed) {
        this.rateSpeed = rateSpeed;
    }

    public void setRateQuality(int rateQuality) {
        this.rateQuality = rateQuality;
    }

    public void setRatePrice(int ratePrice) {
        this.ratePrice = ratePrice;
    }
    
    
}
