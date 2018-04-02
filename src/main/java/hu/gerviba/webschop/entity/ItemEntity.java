package hu.gerviba.webschop.entity;

import javax.persistence.Column;
import javax.persistence.Id;

//@Entity
//@Table(name = "items")
public class ItemEntity {

    @Id
    @Column
    private Long id;

    @Column
    private String name;

    @Column
    private CircleEntity circle;
    
    String detailsJson;
    
    int costs; 
    
}
