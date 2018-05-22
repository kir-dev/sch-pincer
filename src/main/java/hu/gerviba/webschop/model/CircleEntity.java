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
    
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setHomePageDescription(String homePageDescription) {
        this.homePageDescription = homePageDescription;
    }

    public void setAvgOpening(String avgOpening) {
        this.avgOpening = avgOpening;
    }

    public void setFounded(int founded) {
        this.founded = founded;
    }

    public void setHomePageOrder(int homePageOrder) {
        this.homePageOrder = homePageOrder;
    }

    public void setCssClassName(String cssClassName) {
        this.cssClassName = cssClassName;
    }

    public void setFacebookUrl(String facebookUrl) {
        this.facebookUrl = facebookUrl;
    }

    public void setWebsiteUrl(String websiteUrl) {
        this.websiteUrl = websiteUrl;
    }

    public void setBackgroundUrl(String backgroundUrl) {
        this.backgroundUrl = backgroundUrl;
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }

    public void setRateOverAll(float rateOverAll) {
        this.rateOverAll = rateOverAll;
    }

    public void setRateSpeed(float rateSpeed) {
        this.rateSpeed = rateSpeed;
    }

    public void setRateQuality(float rateQuality) {
        this.rateQuality = rateQuality;
    }

    public void setRatePrice(float ratePrice) {
        this.ratePrice = ratePrice;
    }

    public void setRateingCount(int rateingCount) {
        this.rateingCount = rateingCount;
    }

    private static final long serialVersionUID = 2467081480963002976L;

    @Override
    public String toString() {
        return "CircleEntity [id=" + id + ", displayName=" + displayName + ", description=" + description
                + ", homePageDescription=" + homePageDescription + ", avgOpening=" + avgOpening + ", founded=" + founded
                + ", members=" + members + ", reviews=" + reviews + ", openings=" + openings + ", homePageOrder="
                + homePageOrder + ", cssClassName=" + cssClassName + ", facebookUrl=" + facebookUrl + ", websiteUrl="
                + websiteUrl + ", backgroundUrl=" + backgroundUrl + ", logoUrl=" + logoUrl + ", rateOverAll=" + rateOverAll
                + ", rateSpeed=" + rateSpeed + ", rateQuality=" + rateQuality + ", ratePrice=" + ratePrice
                + ", rateingCount=" + rateingCount + "]";
    }
    
}