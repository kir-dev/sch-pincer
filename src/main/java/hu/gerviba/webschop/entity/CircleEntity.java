package hu.gerviba.webschop.entity;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.validation.constraints.Size;

@Entity
@Table(name = "circles")
public class CircleEntity {
    
    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    @Size(min = 2, max = 32)
    private String displayName;

    @Column
    @Size(max = 255)
    private String description;

    @Column
    @Size(max = 255)
    private String homePageDescription;
    
    @Column
    @Size(max = 255)
    private String avgOpening;
    
    @Column
    @OneToMany(fetch = FetchType.LAZY)
    @OrderBy("sort DESC")
    private List<CircleMemberEntity> members;

    @Column
    @OneToMany(mappedBy="circle", fetch = FetchType.LAZY)
    private List<ReviewEntity> reviews;

    @Column
    @OneToMany(mappedBy="circle", fetch = FetchType.LAZY)
    private List<OpeningEntity> openings;

    @Column
    private int homePageOrder;
    
    @Column
    @Size(max = 255)
    private String cssClassName;
    
    @Column
    @Size(max = 255)
    private String facebookUrl;
    
    @Column
    @Size(max = 255)
    private String websiteUrl;
    
    @Column
    int rateOverAll;
    
    @Column
    int rateSpeed;
    
    @Column
    int rateQuality;
    
    @Column
    int ratePrice;
    
}