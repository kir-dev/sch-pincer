package hu.gerviba.webschop.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "users")
public class UserEntity implements Serializable {
    
    private static final long serialVersionUID = 796312955720547481L;

    @Id
    @Column(unique = true)
    private String uid;
    
    @Column
    private String name;
    
    @Column
    private String email;
    
    @Column
    private String room;
    
}
