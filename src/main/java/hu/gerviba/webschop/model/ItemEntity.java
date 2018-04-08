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

@Entity
@Table(name = "items")
public class ItemEntity implements Serializable {

    private static final long serialVersionUID = -8174418379518262439L;

    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    private CircleEntity circle;

    @Lob
    @Column
    private String description;
    
    @Column
    private String detailsConfigJson;
    
    @Column
    private int price; 
    
    @Column
    private boolean orderable;
    
    public ItemEntity() {}

	public ItemEntity(String name, CircleEntity circle, String description, String detailsConfigJson, 
			int price, boolean orderable) {
		this.name = name;
		this.circle = circle;
		this.description = description;
		this.detailsConfigJson = detailsConfigJson;
		this.price = price;
		this.orderable = orderable;
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

	public String getDetailsConfigJson() {
		return detailsConfigJson;
	}

	public int getPrice() {
		return price;
	}

	public boolean isOrderable() {
		return orderable;
	}
    
}
