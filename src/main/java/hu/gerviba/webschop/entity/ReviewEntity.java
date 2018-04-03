package hu.gerviba.webschop.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "reviews")
public class ReviewEntity {
    
    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    UserEntity user;
    
    @ManyToOne(fetch = FetchType.LAZY)
    CircleEntity circle;

    @Column
    String review;

    @Column
    int rateOverAll;
    
    @Column
    int rateSpeed;
    
    @Column
    int rateQuality;
    
    @Column
    int ratePrice;
    
}
