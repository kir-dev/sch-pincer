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

import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.TermVector;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

@Indexed
@Entity
@Table(name = "items")
public class ItemEntity implements Serializable {

    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Field(termVector = TermVector.YES)
    @Column
    private String name;
    
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
    @JsonIdentityReference(alwaysAsId = true)
    @ManyToOne(fetch = FetchType.LAZY)
    private CircleEntity circle;

    @Lob
    @Field(termVector = TermVector.YES)
    @Column
    private String description;

    @Lob
    @Column
    private String ingredients;
    
    @Column
    private String detailsConfigJson;

    @JsonIgnore
    @Field(termVector = TermVector.YES)
    @Column
    private String keywords;
    
    @Column
    private int price; 
    
    @Column
    private boolean orderable;
    
    @Column
    private String imageName;
    
    public ItemEntity() {}

	public ItemEntity(String name, CircleEntity circle, String description, String ingredients, 
	        String keywords, String detailsConfigJson, int price, boolean orderable, String imageName) {
		this.name = name;
		this.circle = circle;
		this.description = description;
		this.ingredients = ingredients;
		this.keywords = keywords;
		this.detailsConfigJson = detailsConfigJson;
		this.price = price;
		this.orderable = orderable;
		this.imageName = imageName;
	}

    public Long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public CircleEntity getCircle() {
		return circle;
	}

	public String getDescription() {
		return description;
	}

	public String getIngredients() {
        return ingredients;
    }

    public String getDetailsConfigJson() {
		return detailsConfigJson;
	}

	public String getKeywords() {
		return keywords;
	}

	public int getPrice() {
		return price;
	}

	public boolean isOrderable() {
		return orderable;
	}
	
	public String getImageName() {
	    return imageName;
	}

    private static final long serialVersionUID = -8174418379518262439L;

    public void setDetailsJsonConfig(String json) {
        this.detailsConfigJson = json;
    }
    
    public void setOrderable(boolean orderable) {
        this.orderable = orderable;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCircle(CircleEntity circle) {
        this.circle = circle;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setIngredients(String ingredients) {
        this.ingredients = ingredients;
    }

    public void setDetailsConfigJson(String detailsConfigJson) {
        this.detailsConfigJson = detailsConfigJson;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }
    
}
