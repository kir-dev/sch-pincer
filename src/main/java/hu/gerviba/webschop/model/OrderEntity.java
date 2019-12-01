package hu.gerviba.webschop.model;

import java.io.Serializable;

import javax.persistence.*;
import javax.validation.constraints.Size;

import lombok.*;

@Data
@Entity
@NoArgsConstructor
@Table(name = "orders")
@SuppressWarnings("serial")
public class OrderEntity implements Serializable {

    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    
    @Column
    private Long openingId;
    
    @Column
    private String userId;

    @Column
    private String userName;

    @Deprecated
    @Column(nullable = false)
    private Integer artificialId = 0;

    @Transient
    private int artificialTransientId = Integer.MAX_VALUE;
    
    @Column
    @Enumerated(EnumType.ORDINAL)
    private OrderStatus status;
    
    @Column(length = 255)
    private String name;
    
    @Lob
    @Column
    private String detailsJson;

    @Column
    private long intervalId;
    
    @Column
    private String intervalMessage;
    
    @Column
    private long date;
    
    @Column
    private String comment;
    
    @Column
    @Size(max = 8)
    private String room;
    
    @Column
    private int price;
    
    @Column
    private String extra = "";
    
    @Column
    private boolean extraTag = false;

    @Column
    private long cancelUntil;
    
    @Column
    private String systemComment = "";

    @Column(nullable = false)
    private Integer count = 1;

    @Column(nullable = false)
    private Integer category = 0;

    @Column(nullable = false)
    private Integer priority = 1;

    @Column(nullable = false)
    private String compactName;

    public OrderEntity(String userId, String userName, String comment, String detailsJson, String room) {
        this.userId = userId;
        this.status = OrderStatus.ACCEPTED;
        this.comment = comment;
        this.detailsJson = detailsJson;
        this.room = room;
        this.userName = userName;
    }
    
}
