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
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

@Entity
@Table(name = "circleMembers")
public class CircleMemberEntity implements Serializable {

    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
    @JsonIdentityReference(alwaysAsId = true)
    @ManyToOne(fetch = FetchType.LAZY)
    private CircleEntity circle;
    
    @Column
    @Size(max = 64)
    private String name;

    @Column
    @Size(max = 64)
    private String rank;

    @Column
    private String avatar;
    
    @Column
    private int sort;

    public CircleMemberEntity() {}
    
    public CircleMemberEntity(CircleEntity circle, @Size(max = 64) String name, @Size(max = 64) String rank, 
            String avatar, int sort) {
        
        this.circle = circle;
        this.name = name;
        this.rank = rank;
        this.avatar = avatar;
        this.sort = sort;
    }

    public Long getId() {
        return id;
    }

    public CircleEntity getCircle() {
        return circle;
    }

    public String getName() {
        return name;
    }

    public String getRank() {
        return rank;
    }

    public String getAvatar() {
        return avatar;
    }

    public int getSort() {
        return sort;
    }
    
    private static final long serialVersionUID = 3999912885341511614L;
    
}