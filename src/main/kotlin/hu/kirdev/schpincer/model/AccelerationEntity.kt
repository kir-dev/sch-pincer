package hu.kirdev.schpincer.model

import javax.persistence.*

@Entity
class AccelerationEntity (circleEntity: CircleEntity, userEntity: UserEntity) {

    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long = 0;

    @ManyToOne(fetch = FetchType.LAZY)
    var circle: CircleEntity = circleEntity

    @Column
    var createdAt: Long = System.currentTimeMillis()

    @ManyToOne(fetch = FetchType.LAZY)
    var user: UserEntity = userEntity

}