package hu.gerviba.webschop.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.validation.constraints.Size;

import lombok.Data;
import lombok.NoArgsConstructor;

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

    @Column
    @Enumerated(EnumType.ORDINAL)
    private OrderStatus status;
    
    @Column(length = 255)
    private String name;
    
    @Lob
    @Column
    private String detailsJson;

    @Column
    private int intervalId;
    
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
    
    public OrderEntity(String userId, String userName, String comment, String detailsJson, String room) {
        this.userId = userId;
        this.status = OrderStatus.ACCEPTED;
        this.comment = comment;
        this.detailsJson = detailsJson;
        this.room = room;
        this.userName = userName;
    }
    
    public OrderEntity(String userId, String userName, OrderStatus status, String name, String detailsJson, int intervalId,
            String intervalMessage, long date, String room, int price) {

        this.userId = userId;
        this.status = status;
        this.name = name;
        this.detailsJson = detailsJson;
        this.intervalId = intervalId;
        this.intervalMessage = intervalMessage;
        this.date = date;
        this.room = room;
        this.price = price;
        this.userName = userName;
    }

}
