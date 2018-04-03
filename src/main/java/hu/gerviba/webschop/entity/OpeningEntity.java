package hu.gerviba.webschop.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "openings")
public class OpeningEntity {
    
    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column
    Long dateStart;
    @Column
    Long dateEnd;

    @Column
    Long orderStart;
    @Column
    Long orderEnd;

    @Column
    String place;
    
    @ManyToOne(fetch = FetchType.LAZY)
    CircleEntity circle;

    @Column
    int maxOrder;

    @Column
    int maxOrderPerHalfHour;
    
}
