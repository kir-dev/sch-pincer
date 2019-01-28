package hu.gerviba.webschop.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@Table(name = "items")
@SuppressWarnings("serial")
public class IntervalEntity implements Serializable {

    @Id
    @GeneratedValue
    @Column
    private Long id;
    
    @Column
    private OpeningEntity opening;
    
    @Column
    private String name;
    
    @Column
    private int normalItemCount;
    
    @Column
    private int extraItemCount;
    
}
