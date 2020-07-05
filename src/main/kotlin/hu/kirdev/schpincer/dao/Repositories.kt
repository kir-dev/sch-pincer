package hu.kirdev.schpincer.dao

import hu.kirdev.schpincer.model.*
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface CircleMemberRepository : JpaRepository<CircleMemberEntity, Long>

@Repository
interface CircleRepository : JpaRepository<CircleEntity, Long> {
    fun findAllByOrderByHomePageOrder(): List<CircleEntity>
    fun findByAlias(alias: String): CircleEntity
    fun findAllByVisibleTrueOrderByHomePageOrder(): List<CircleEntity>
    fun findAllByAlias(alias: String): List<CircleEntity>
}

@Repository
interface ItemRepository : JpaRepository<ItemEntity, Long> {
    fun findAllByCircle_Id(circleId: Long): List<ItemEntity>
    fun findAllByVisibleTrue(): List<ItemEntity>
    fun findAllByVisibleTrueAndVisibleInAllTrue(): List<ItemEntity>
    fun findAllByVisibleTrueAndVisibleInAllTrue(page: Pageable): Page<ItemEntity>
    fun deleteByCircle_Id(circleId: Long)
    fun findAllByCircle_IdIn(circles: List<Long>): List<ItemEntity>
    fun findAllByVisibleTrueOrderByPrecedenceDesc(): List<ItemEntity>
    fun findAllByVisibleTrueAndVisibleInAllTrueOrderByPrecedenceDesc(page: Pageable): Page<ItemEntity>
    fun findAllByCircle_IdOrderByPrecedenceDesc(circleId: Long): List<ItemEntity>
    fun findAllByCircle_IdInOrderByPrecedenceDesc(circles: List<Long>): List<ItemEntity>
    fun findAllByCircle_IdOrderByManualPrecedenceDesc(circleId: Long): List<ItemEntity>
}

@Repository
interface OpeningRepository : JpaRepository<OpeningEntity, Long> {
    fun findAllByOrderByDateStart(): List<OpeningEntity>
    fun findAllByDateEndGreaterThanAndDateEndLessThanOrderByDateStart(now: Long, weekFromNow: Long): List<OpeningEntity>
    fun findAllByOrderStartGreaterThanAndOrderStartLessThan(time1: Long, time2: Long): List<OpeningEntity>
    fun findFirstByCircle_IdOrderByDateStart(circle: Long): Optional<OpeningEntity>
    fun findFirstByCircle_IdAndDateEndGreaterThanOrderByDateStart(id: Long, time: Long): Optional<OpeningEntity>
    fun findAllByOrderStartLessThanAndOrderEndGreaterThan(currentTime1: Long, currentTime2: Long): List<OpeningEntity>
    fun findAllByOrderEndGreaterThanOrderByDateStart(currentTimeMillis: Long): List<OpeningEntity>
}

@Repository
interface OrderRepository : JpaRepository<OrderEntity, Long> {
    fun findAllByUserIdOrderByDateDesc(userId: String): List<OrderEntity>
    fun findAllByOpeningId(openingId: Long): List<OrderEntity>
    fun findAllByOpeningIdOrderByIntervalIdAscPriorityDescDateAsc(openingId: Long): List<OrderEntity>
    fun findAllByOpeningIdOrderByPriorityDescDateAsc(openingId: Long): List<OrderEntity>
    fun findAllByOpeningIdAndStatusNotOrderByPriorityDescDateAsc(openingId: Long, status: OrderStatus): List<OrderEntity>
    fun findAllByOpeningIdAndStatusNotOrderByIntervalIdAscPriorityDescDateAsc(openingId: Long, status: OrderStatus): List<OrderEntity>
    fun findAllByOpeningIdAndStatusNot(openingId: Long, status: OrderStatus): List<OrderEntity>
}

@Repository
interface ReviewRepository : JpaRepository<ReviewEntity, Long> {
    fun findAllByCircle_IdOrderByDateDesc(circleId: Long): List<ReviewEntity>
}

@Repository
interface TimeWindowRepository : JpaRepository<TimeWindowEntity, Long>

@Repository
interface UserRepository : JpaRepository<UserEntity, String>
