package hu.gerviba.webschop.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "cart")
public class CartItemEntity implements Serializable {

    private static final long serialVersionUID = -6321437469997930825L;

    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    
    @ManyToOne
    private OrderEntity order;

    @Column(length = 255)
    private String icon;
    
    @Column(length = 255)
    private String name;
    
    @Column
    private int count;

    @Column
    private String detailsJson;
    
    
}
