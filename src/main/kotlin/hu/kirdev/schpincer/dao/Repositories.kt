package hu.kirdev.schpincer.dao

import hu.kirdev.schpincer.model.*
import hu.kirdev.schpincer.web.component.CustomComponentType
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface CircleMemberRepository : JpaRepository<CircleMemberEntity, Long>

@Repository
interface CircleRepository : JpaRepository<CircleEntity, Long> {
    fun findAllByVisibleTrueOrderByHomePageOrder(): List<CircleEntity>
    fun findAllByAlias(alias: String): List<CircleEntity>
    fun findOneByVirGroupId(virGroupId: Long): CircleEntity?
}

@SuppressWarnings(
    "kotlin:S100", // ignore underscores in queries
    "kotlin:S1192" // ignore duplicated strings
)
@Repository
interface ItemRepository : JpaRepository<ItemEntity, Long> {
    fun findAllByCircle_Id(circleId: Long): List<ItemEntity>
    fun deleteByCircle_Id(circleId: Long)
    fun findAllByVisibleTrueOrderByPrecedenceDesc(): List<ItemEntity>
    fun findAllByVisibleTrueAndVisibleInAllTrueOrderByPrecedenceDesc(page: Pageable): Page<ItemEntity>
    fun findAllByCircle_IdInOrderByPrecedenceDesc(circles: List<Long>): List<ItemEntity>
    fun findAllByCircle_IdOrderByManualPrecedenceDesc(circleId: Long): List<ItemEntity>
}

@SuppressWarnings(
    "kotlin:S100" // ignore underscores in queries
)
@Repository
interface OpeningRepository : JpaRepository<OpeningEntity, Long> {
    fun findAllByOrderByDateStart(): List<OpeningEntity>

    @Query(
        "select * from openings o where o.date_end < ?1 and o.date_start < ?2 order by o.date_start desc limit ?3",
        nativeQuery = true
    )
    fun findAllEndedOpeningsBefore(now: Long, before: Long, limit: Long): List<OpeningEntity>

    fun findAllByDateEndGreaterThanAndDateEndLessThanOrderByDateStart(now: Long, weekFromNow: Long): List<OpeningEntity>
    fun findAllByOrderStartGreaterThanAndOrderStartLessThan(time1: Long, time2: Long): List<OpeningEntity>
    fun findFirstByCircle_IdAndDateEndGreaterThanOrderByDateStart(id: Long, time: Long): Optional<OpeningEntity>
    fun findAllByOrderStartLessThanAndOrderEndGreaterThan(currentTime1: Long, currentTime2: Long): List<OpeningEntity>
    fun findAllByOrderEndGreaterThanOrderByDateStart(currentTimeMillis: Long): List<OpeningEntity>
}

@Repository
interface OrderRepository : JpaRepository<OrderEntity, Long> {
    fun findAllByUserIdOrderByDateDesc(userId: String): List<OrderEntity>
    fun findAllByOpeningId(openingId: Long): List<OrderEntity>
    fun findAllByOpeningIdAndStatusNotOrderByPriorityDescDateAsc(openingId: Long, status: OrderStatus): List<OrderEntity>
    fun findAllByOpeningIdAndStatusNotOrderByIntervalIdAscPriorityDescDateAsc(openingId: Long, status: OrderStatus): List<OrderEntity>
    fun findAllByOpeningIdAndStatusNot(openingId: Long, status: OrderStatus): List<OrderEntity>
    fun findAllByDateGreaterThanAndDateLessThanAndStatusIsNot(dateFrom: Long, dateTo: Long, status: OrderStatus): List<OrderEntity>
}

@SuppressWarnings(
    "kotlin:S100"// ignore underscores in queries
)
@Repository
interface ReviewRepository : JpaRepository<ReviewEntity, Long> {
    fun findAllByCircle_IdOrderByDateDesc(circleId: Long): List<ReviewEntity>
}

@Repository
interface TimeWindowRepository : JpaRepository<TimeWindowEntity, Long> {
    override fun findById(id: Long): Optional<TimeWindowEntity>
}

@Repository
interface UserRepository : JpaRepository<UserEntity, String> {
    fun findTop10AllByNameContainsIgnoreCase(name: String): List<UserEntity>
    fun findTop10AllByRoomContainsIgnoreCase(room: String): List<UserEntity>
}

@Repository
interface ExtrasRepository : JpaRepository<ExtraEntity, Long> {
    fun findByCircleAndNameAndInputTypeAndSelectedIndex(circle: CircleEntity, name: String, inputType: CustomComponentType, selectedIndex: Int): Optional<ExtraEntity>
}
