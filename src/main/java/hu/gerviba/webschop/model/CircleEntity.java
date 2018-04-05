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
    @OrderBy("name ASC")
    @OneToMany(fetch = FetchType.LAZY)
    private List<CircleMemberEntity> members;

    @Column
    @OneToMany(mappedBy="circle", fetch = FetchType.LAZY)
    private List<ReviewEntity> reviews;

    @Column
    @OrderBy("dateStart ASC")
    @OneToMany(mappedBy="circle", fetch = FetchType.LAZY)
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
    private int rateOverAll;
    
    @Column
    @Min(0)
    @Max(10)
    private int rateSpeed;
    
    @Column
    @Min(0)
    @Max(10)
    private int rateQuality;
    
    @Column
    @Min(0)
    @Max(10)
    private int ratePrice;

    public CircleEntity() {}
    
    public CircleEntity(@Size(min = 2, max = 32) String displayName, @Size(max = 1000) String description,
            @Size(max = 1000) String homePageDescription, @Size(max = 30) String cssClassName, @Size(max = 255) String backgroundUrl, 
            @Size(max = 255) String logoUrl) {
        this.displayName = displayName;
        this.description = description;
        this.homePageDescription = homePageDescription;
        this.avgOpening = "";
        this.homePageOrder = 0;
        this.cssClassName = cssClassName;
        this.facebookUrl = "";
        this.websiteUrl = "";
        this.backgroundUrl = backgroundUrl;
        this.logoUrl = logoUrl;
        this.rateOverAll = 5;
        this.rateSpeed = 3;
        this.rateQuality = 2;
        this.ratePrice = 5;
    }

    public Long getId() {
        return id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    public String getHomePageDescription() {
        return homePageDescription;
    }

    public String getAvgOpening() {
        return avgOpening;
    }

    public List<CircleMemberEntity> getMembers() {
        return members;
    }

    public List<ReviewEntity> getReviews() {
        return reviews;
    }

    public List<OpeningEntity> getOpenings() {
        return openings;
    }

    public int getHomePageOrder() {
        return homePageOrder;
    }

    public String getCssClassName() {
        return cssClassName;
    }

    public String getFacebookUrl() {
        return facebookUrl;
    }

    public String getWebsiteUrl() {
        return websiteUrl;
    }

    public String getBackgroundUrl() {
        return backgroundUrl;
    }

    public String getLogoUrl() {
        return logoUrl;
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
    
}