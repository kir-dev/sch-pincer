package hu.gerviba.webschop.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Size;

@Entity
@Table(name = "circleMembers")
public class CircleMemberEntity {
    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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
}