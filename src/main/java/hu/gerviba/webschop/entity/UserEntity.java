package hu.gerviba.webschop.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "users")
public class UserEntity {
    
    @Id
    @Column
    String uid;
    
    @Column
    String name;
    
    @Column
    String email;
    
    @Column
    String room;
    
}
