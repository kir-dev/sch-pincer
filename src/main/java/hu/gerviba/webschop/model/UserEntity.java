package hu.gerviba.webschop.model;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Proxy;

@Entity
@Table(name = "users")
@Proxy(lazy = false)
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

    @Column
    private CardType cardType = CardType.DO;
    
    @Column
    @ElementCollection
    private List<String> permissions; 
    
    public UserEntity() {}

    public UserEntity(String uid, String name, String email) {
        this.uid = uid;
        this.name = name;
        this.email = email;
        this.sysadmin = false;
        this.cardType = CardType.DO;
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
    
    public List<String> getPermissions() {
        return permissions;
    }
    
    public CardType getCardType() {
        return cardType;
    }
    
    public void setRoom(int room) {
        this.room = "SCH " + room;
    }
    
    public boolean isSysadmin() {
        return sysadmin;
    }

    private static final long serialVersionUID = 796312955720547481L;
    
}
