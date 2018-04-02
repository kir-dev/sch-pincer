package hu.gerviba.webschop.entity;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

//@Entity
//@Table(name = "circles")
public class CircleEntity {
    
    @Id
    @Column
    private Long id;

    @Column
    private String name;

    @Column
    private String displayName;

    @Column
    private String description;

    @Column
    private String homePageDescription;
    
    private List<CircleMemberEntity> members;
    
    private List<ReviewEntity> reviews;

    @Column
    private int homePageOrder;
    
    @Column
    private boolean acceptOrders;
    
    @Column
    private String cssClassName;
    
    class CircleMemberEntity {
        @Id
        @Column
        private Long id;

        @Column
        private String name;

        @Column
        private String rank;

        @Column
        private String avatar;
    }
}
