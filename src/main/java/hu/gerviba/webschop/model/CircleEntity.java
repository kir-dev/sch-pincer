package hu.gerviba.webschop.model;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@Table(name = "circles")
@SuppressWarnings("serial")
public class CircleEntity implements Serializable {
    
    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(length = 32)
    @Size(min = 2, max = 32)
    private String displayName;

    @Lob
    @Column(length = 1000)
    @Size(max = 1000)
    private String description;

    @Lob
    @Column(length = 1000)
    @Size(max = 1000)
    private String homePageDescription;
    
    @Column(length = 255)
    @Size(max = 255)
    private String avgOpening;

    @Column
    private int founded;
    
    @Column
    @OrderBy("sort DESC")
    @OneToMany(mappedBy = "circle", fetch = FetchType.LAZY, orphanRemoval = true)
    private List<CircleMemberEntity> members;

    @Column
    @OrderBy("date DESC")
    @OneToMany(mappedBy = "circle", fetch = FetchType.LAZY, orphanRemoval = true)
    private List<ReviewEntity> reviews;

    @Column
    @OrderBy("dateStart ASC")
    @OneToMany(mappedBy = "circle", fetch = FetchType.LAZY, orphanRemoval = true)
    private List<OpeningEntity> openings;

    @Column
    private int homePageOrder;
    
    @Column(length = 30)
    @Size(max = 30)
    private String cssClassName;
    
    @Column(length = 255)
    @Size(max = 255)
    private String facebookUrl;
    
    @Column(length = 255)
    @Size(max = 255)
    private String websiteUrl;
    
    @Column(length = 255)
    @Size(max = 255)
    private String backgroundUrl;
    
    @Column(length = 255)
    @Size(max = 255)
    private String logoUrl;
    
    @Column
    @Min(0)
    @Max(10)
    private float rateOverAll;
    
    @Column
    @Min(0)
    @Max(10)
    private float rateSpeed;
    
    @Column
    @Min(0)
    @Max(10)
    private float rateQuality;
    
    @Column
    @Min(0)
    @Max(10)
    private float ratePrice;
    
    @Column
    @Min(0)
    private int rateingCount;
    
    public CircleEntity(@Size(min = 2, max = 32) String displayName, @Size(max = 1000) String description,
            @Size(max = 1000) String homePageDescription, @Size(max = 30) String cssClassName, int founded,
            @Size(max = 255) String backgroundUrl, @Size(max = 255) String logoUrl, 
            @Size(max = 255) String avgOpening) {
        
        this.displayName = displayName;
        this.description = description;
        this.homePageDescription = homePageDescription;
        this.founded = founded;
        this.homePageOrder = 0;
        this.cssClassName = cssClassName;
        this.facebookUrl = "https://facebook.com/circle";
        this.websiteUrl = "http://website.sch.bme.hu/";
        this.avgOpening = avgOpening;
        this.backgroundUrl = backgroundUrl;
        this.logoUrl = logoUrl;
        this.rateOverAll = 5;
        this.rateSpeed = 3;
        this.rateQuality = 2.35f;
        this.ratePrice = 5;
        this.rateingCount = 105;
    }

    public CircleEntity(CircleEntity copy) {
        this.id = copy.getId();
        this.displayName = copy.getDisplayName();
        this.description = copy.getDescription();
        this.homePageDescription = copy.getHomePageDescription();
        this.avgOpening = copy.getAvgOpening();
        this.founded = copy.getFounded();
        this.homePageOrder = copy.getHomePageOrder();
        this.cssClassName = copy.getCssClassName();
        this.facebookUrl = copy.getFacebookUrl();
        this.websiteUrl = copy.getWebsiteUrl();
        this.backgroundUrl = copy.getBackgroundUrl();
        this.logoUrl = copy.getLogoUrl();
        this.rateOverAll = copy.getRateOverAll();
        this.rateSpeed = copy.getRateSpeed();
        this.rateQuality = copy.getRateQuality();
        this.ratePrice = copy.getRatePrice();
        this.rateingCount = copy.getRateingCount();
    }
    
}