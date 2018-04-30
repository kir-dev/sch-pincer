package hu.gerviba.webschop.model;

import java.io.Serializable;
import java.util.List;

import javax.persistence.CascadeType;
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

@Entity
@Table(name = "circles")
public class CircleEntity implements Serializable {
    
    private static final long serialVersionUID = 2467081480963002976L;

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
    @OneToMany(mappedBy = "circle", fetch = FetchType.LAZY)
    private List<CircleMemberEntity> members;

    @Column
    @OrderBy("date DESC")
    @OneToMany(mappedBy = "circle", fetch = FetchType.LAZY)
    private List<ReviewEntity> reviews;

    @Column
    @OrderBy("dateStart ASC")
    @OneToMany(mappedBy = "circle", fetch = FetchType.LAZY)
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

    public CircleEntity() {}
    
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

    public String getAvgOpening() {
        return avgOpening;
    }

    public String getBackgroundUrl() {
        return backgroundUrl;
    }

    public String getCssClassName() {
        return cssClassName;
    }

    public String getDescription() {
        return description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getFacebookUrl() {
        return facebookUrl;
    }

    public int getFounded() {
        return founded;
    }

    public String getHomePageDescription() {
        return homePageDescription;
    }

    public int getHomePageOrder() {
        return homePageOrder;
    }

    public Long getId() {
        return id;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public List<CircleMemberEntity> getMembers() {
        return members;
    }

    public List<OpeningEntity> getOpenings() {
        return openings;
    }

    public int getRateingCount() {
        return rateingCount;
    }

    public float getRateOverAll() {
        return rateOverAll;
    }

    public float getRatePrice() {
        return ratePrice;
    }

    public float getRateQuality() {
        return rateQuality;
    }

    public float getRateSpeed() {
        return rateSpeed;
    }

    public List<ReviewEntity> getReviews() {
        return reviews;
    }

    public String getWebsiteUrl() {
        return websiteUrl;
    }
    
}