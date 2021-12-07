package hu.kirdev.schpincer.service

import hu.kirdev.schpincer.dao.CircleRepository
import hu.kirdev.schpincer.dao.OpeningRepository
import hu.kirdev.schpincer.dao.OrderRepository
import hu.kirdev.schpincer.model.OrderEntity
import hu.kirdev.schpincer.model.OrderStatus
import hu.kirdev.schpincer.model.UserEntity
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
open class StatisticsService {

    @Autowired
    internal lateinit var orders: OrderRepository

    @Autowired
    internal lateinit var openings: OpeningRepository

    @Autowired
    internal lateinit var circles: CircleRepository

    val september2021 = 1630447200000L
    val christmas2021 = 1640300400000L
    val topCount = 10

    @Transactional(readOnly = true)
    open fun getDetailsForUser(userEntity: UserEntity): Map<String, Any> {
        val transactions = orders.findAllByDateGreaterThanAndDateLessThanAndStatusIsNot(september2021, christmas2021, status = OrderStatus.CANCELLED)

        val mostLovedItems = transactions.groupBy { realName(it) }
                .values
                .sortedByDescending { it.sumOf { it.count } }
                .take(topCount)
                .map { Pair(realName(it[0]), it.sumOf { it.count }) }

        val mostOrderCountByUsers = transactions.groupBy { it.userId }
                .values
                .sortedByDescending { it.sumOf { it.count } }
                .map { Pair(it[0].userId, it.sumOf { it.count }) }

        val userPositionByOrderCount = mostOrderCountByUsers.map{ it.first }.indexOf(userEntity.uid) + 1
        val userPositionByValue = transactions.groupBy { it.userId }
                .values
                .sortedByDescending { it.sumOf { it.price } }
                .map { it[0].userId }
                .indexOf(userEntity.uid) + 1


        val userTransactions = transactions.filter { it.userId == userEntity.uid }
        val userOrderCount = userTransactions.sumOf { it.count }
        val userOrderValue = userTransactions.sumOf { it.price }
        val userFavorite = userTransactions.groupBy { realName(it) }
                .values
                .sortedByDescending { it.sumOf { it.count } }
                .take(1)
                .map { Pair(realName(it[0]), it.sumOf { it.count }) }

        val userKbAbExtra = userTransactions.map {
            val v1 = if (it.detailsJson.contains("\"name\":\"kb_extra\",\"selected\":[\"")
                    && !it.detailsJson.contains("\"name\":\"kb_extra\",\"selected\":[\"0\"]")) 1 else 0
            val v2 = if (it.detailsJson.contains("\"name\":\"ab_extra\",\"selected\":[\"")
                    && !it.detailsJson.contains("\"name\":\"ab_extra\",\"selected\":[\"0\"]")) 1 else 0
            val v3 = if (it.detailsJson.contains("\"name\":\"ab_kb_extra\",\"selected\":[\"")
                    && !it.detailsJson.contains("\"name\":\"ab_kb_extra\",\"selected\":[\"0\"]")) 1 else 0
            val v4 = if (it.detailsJson.contains("\"name\":\"kb_extra2\",\"selected\":[\"")
                    && !it.detailsJson.contains("\"name\":\"kb_extra2\",\"selected\":[\"0\"]")) 1 else 0
            val v5 = if (it.detailsJson.contains("\"name\":\"ab_extra2\",\"selected\":[\"")
                    && !it.detailsJson.contains("\"name\":\"ab_extra2\",\"selected\":[\"0\"]")) 1 else 0
            val v6 = if (it.detailsJson.contains("\"name\":\"ab_kb_extra2\",\"selected\":[\"")
                    && !it.detailsJson.contains("\"name\":\"ab_kb_extra2\",\"selected\":[\"0\"]")) 1 else 0

            return@map (v1 + v2 + v3 + v4 + v5 + v6) * it.count
        }.sum()

        val heldOpenings = openings.findAllByOrderStartGreaterThanAndOrderStartLessThan(september2021, christmas2021)

//        val ordersByCircles = transactions.groupBy { it.openingId }
//                .map { Pair(heldOpenings.firstOrNull { opening -> opening.id == (it.key ?: 0) }?.circle?.id ?: 0, it.value) }
//                .groupBy { it.first }
//                .values
//                .map { Pair(it[0].first, it.sumOf { group -> group.second.sumOf { ls -> ls.count } }) }
//                .sortedByDescending { it.second }
//                .map { Pair(circles.findById(it.first).orElse(null)?.displayName ?: "", it.second) }

        return mapOf(
                "globalMostLovedItem" to mostLovedItems,
                "globalOrdersByUser" to mostOrderCountByUsers.take(topCount),
                "globalOrderedFoodCount" to transactions.sumOf { it.count },
//                "globalOrdersByCircles" to ordersByCircles.take(topCount),
                "globalHeldOpeningCount" to heldOpenings.size,
                "userTopByOrderCount" to userPositionByOrderCount,
                "userOrderCount" to userOrderCount,
                "userTopByOrderValue" to userPositionByValue,
                "userOrderValue" to userOrderValue,
                "userKbAbExtra" to userKbAbExtra,
                "userFavorite" to userFavorite
        )
    }

    private fun realName(it: OrderEntity): String {
        val index = it.name.lastIndexOf('x')
        return if (index == -1) it.name else it.name.substring(0, index - 1)
    }

}