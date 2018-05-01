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

@Entity
@Table(name = "orders")
public class OrderEntity implements Serializable {

    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    
    @Column
    private String userId;
    
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
    
    public OrderEntity() {}
    
    public OrderEntity(String userId, String comment, String detailsJson, String room) {
        this.userId = userId;
        this.status = OrderStatus.ACCEPTED;
        this.comment = comment;
        this.detailsJson = detailsJson;
        this.room = room;
    }
    
    public OrderEntity(String userId, OrderStatus status, String name, String detailsJson, int intervalId,
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
    }

    public Long getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public String getName() {
        return name;
    }

    public String getDetailsJson() {
        return detailsJson;
    }

    public int getIntervalId() {
        return intervalId;
    }

    public String getIntervalMessage() {
        return intervalMessage;
    }

    public long getDate() {
        return date;
    }

    public String getComment() {
        return comment;
    }

    public void setIntervalId(int intervalId) {
        this.intervalId = intervalId;
    }

    public void setIntervalMessage(String intervalMessage) {
        this.intervalMessage = intervalMessage;
    }
    
    public String getRoom() {
        return room;
    }
    
    public int getPrice() {
        return price;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public void setPrice(int price) {
        this.price = price;
    }
    
    public void setStatus(OrderStatus status) {
        this.status = status;
    }
    
    public void setDate(long date) {
        this.date = date;
    }

    private static final long serialVersionUID = -6321437469997930825L;
}
