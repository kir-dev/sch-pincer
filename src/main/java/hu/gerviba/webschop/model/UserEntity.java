package hu.gerviba.webschop.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Proxy;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@Table(name = "users")
@Proxy(lazy = false)
@SuppressWarnings("serial")
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
    @ElementCollection(fetch = FetchType.EAGER)
    private Set<String> permissions = new HashSet<>(); 

    public UserEntity(String uid, String name, String email) {
        this.uid = uid;
        this.name = name;
        this.email = email;
        this.sysadmin = false;
    }
    
    public void setRoom(String room) {
        this.room = room;
    }
    
}
