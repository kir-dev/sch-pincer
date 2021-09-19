package hu.kirdev.schpincer.service

import hu.kirdev.schpincer.dao.AccelerationRepository
import hu.kirdev.schpincer.dao.CircleRepository
import hu.kirdev.schpincer.dao.OpeningRepository
import hu.kirdev.schpincer.model.AccelerationEntity
import hu.kirdev.schpincer.model.UserEntity
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import javax.transaction.Transactional

@Service
open class AcceleratorService {

    @Autowired
    lateinit var circleRepository: CircleRepository

    @Autowired
    lateinit var accelerationRepository: AccelerationRepository

    @Transactional
    open fun accelerateCircle(circleAlias: String, userEntity: UserEntity) = accelerationRepository.save(
            AccelerationEntity(
                circleRepository.findByAlias(circleAlias),
                userEntity
            )
        )

    @Transactional
    open fun getAcceleratedInOpening(circleAlias: String, timeStart: Long, timeEnd: Long) =
        accelerationRepository.findAllByCircle_IdAndCreatedAtGreaterThanEqualAndCreatedAtLessThanEqual(
            circleRepository.findByAlias(circleAlias).id,
            timeStart,
            timeEnd
        ).count()

}