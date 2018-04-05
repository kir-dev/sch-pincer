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

    @Column(length = 255)
    private String description;
    
    @Column
    private String detailsConfigJson;
    
    @Column
    private int costs; 
    
    @Column
    private boolean orderable;
    
}
