package hu.gerviba.webschop.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "users")
public class UserEntity implements Serializable {

    @Id
    @Column(unique = true)
    private String uid;
    
    @Column
    private String name;
    
    @Column
    private String email;
    
    @Column
    private String room;
    
    @Column
    private boolean sysadmin = false;
    
    public UserEntity() {}

    public UserEntity(String uid, String name, String email) {
        this.uid = uid;
        this.name = name;
        this.email = email;
        this.sysadmin = false;
    }

    public String getUid() {
        return uid;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getRoom() {
        return room;
    }
    
    public void setSysadmin(boolean sysadmin) {
        this.sysadmin = sysadmin;
    }
    
    private static final long serialVersionUID = 796312955720547481L;
    
}
