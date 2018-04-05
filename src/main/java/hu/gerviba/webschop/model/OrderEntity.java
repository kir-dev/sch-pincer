package hu.gerviba.webschop.model;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "orders")
public class OrderEntity implements Serializable {

    private static final long serialVersionUID = -7845935652478701980L;

    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column
    private int orderPeriodId;

    @OneToMany(mappedBy = "order", fetch = FetchType.EAGER)
    private List<CartItemEntity> items;
    
    @Column
    @Enumerated(EnumType.ORDINAL)
    private OrderStatus status;
    
    @Column
    private int comment;
    
}
