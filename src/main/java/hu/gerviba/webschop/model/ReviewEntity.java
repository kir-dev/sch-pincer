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
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

@Entity
@Table(name = "reviews")
public class ReviewEntity implements Serializable {
    
    private static final long serialVersionUID = 7823917697337423919L;

    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    private UserEntity user;
    
    @ManyToOne(fetch = FetchType.LAZY)
    private CircleEntity circle;

    @Column
    private String review;

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
    
}
