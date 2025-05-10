package hu.kirdev.schpincer.service

import hu.kirdev.schpincer.dao.ExtrasRepository
import hu.kirdev.schpincer.dao.ItemRepository
import hu.kirdev.schpincer.dao.TimeWindowRepository
import hu.kirdev.schpincer.dto.OrderDetailsDto
import hu.kirdev.schpincer.getException
import hu.kirdev.schpincer.model.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.whenever
import java.time.Instant
import java.util.*
import kotlin.test.assertEquals

@ExtendWith(MockitoExtension::class)
class MakeOrderTest {

    @Mock
    lateinit var itemsRepo: ItemRepository

    @Mock
    lateinit var timewindowRepo: TimeWindowRepository

    @Mock
    lateinit var user: UserEntity

    @Mock
    lateinit var extrasRepository: ExtrasRepository

    private val usedPerCategory = arrayOf(
            arrayOf(1, 0, 0, 0, 0),
            arrayOf(0, 1, 0, 0, 0),
            arrayOf(0, 0, 1, 0, 0),
            arrayOf(0, 0, 0, 1, 0),
            arrayOf(0, 0, 0, 0, 1)
    )

    private val availablePerCategory = arrayOf(
            arrayOf(3, 0, 0, 0, 0),
            arrayOf(0, 3, 0, 0, 0),
            arrayOf(0, 0, 3, 0, 0),
            arrayOf(0, 0, 0, 3, 0),
            arrayOf(0, 0, 0, 0, 3)
    )

    @ParameterizedTest
    @ValueSource(ints = [0, 1, 2, 3, 4])
    fun `check category limitations`(categoryId: Int) {
        val opening = OpeningEntity(30, dateStart = 0, dateEnd = 0, orderStart = 0, orderEnd = 0)
        opening.usedAlpha = usedPerCategory[categoryId][0]
        opening.usedBeta = usedPerCategory[categoryId][1]
        opening.usedGamma = usedPerCategory[categoryId][2]
        opening.usedDelta = usedPerCategory[categoryId][3]
        opening.usedLambda = usedPerCategory[categoryId][4]

        opening.maxAlpha = availablePerCategory[categoryId][0]
        opening.maxBeta = availablePerCategory[categoryId][1]
        opening.maxGamma = availablePerCategory[categoryId][2]
        opening.maxDelta = availablePerCategory[categoryId][3]
        opening.maxLambda = availablePerCategory[categoryId][4]

        val count = 2
        val item = ItemEntity(20, category = categoryId + 1)
        val procedure = MakeOrderProcedure(user, 0, count, 0, "", "{}",
                itemsRepo = itemsRepo,
                timeWindowRepo = timewindowRepo,
                extrasRepository = extrasRepository)
        procedure.current = opening
        procedure.item = item
        procedure.count = count

        assertDoesNotThrow { procedure.updateCategoryLimitations(true) }
        assertEquals(availablePerCategory[categoryId][0], opening.usedAlpha)
        assertEquals(availablePerCategory[categoryId][1], opening.usedBeta)
        assertEquals(availablePerCategory[categoryId][2], opening.usedGamma)
        assertEquals(availablePerCategory[categoryId][3], opening.usedDelta)
        assertEquals(availablePerCategory[categoryId][4], opening.usedLambda)
    }

    @ParameterizedTest
    @ValueSource(ints = [0, 1, 2, 3, 4])
    fun `check category limitations with too many orders`(categoryId: Int) {
        val opening = OpeningEntity(30, dateStart = 0, dateEnd = 0, orderStart = 0, orderEnd = 0)
        opening.usedAlpha = usedPerCategory[categoryId][0]
        opening.usedBeta = usedPerCategory[categoryId][1]
        opening.usedGamma = usedPerCategory[categoryId][2]
        opening.usedDelta = usedPerCategory[categoryId][3]
        opening.usedLambda = usedPerCategory[categoryId][4]

        opening.maxAlpha = availablePerCategory[categoryId][0]
        opening.maxBeta = availablePerCategory[categoryId][1]
        opening.maxGamma = availablePerCategory[categoryId][2]
        opening.maxDelta = availablePerCategory[categoryId][3]
        opening.maxLambda = availablePerCategory[categoryId][4]

        val count = 3
        val item = ItemEntity(20, category = categoryId + 1)
        val procedure = MakeOrderProcedure(user, 0, count, 0, "", "{}",
                itemsRepo = itemsRepo,
                timeWindowRepo = timewindowRepo,
                extrasRepository = extrasRepository)
        procedure.current = opening
        procedure.item = item
        procedure.count = count

        assertEquals(RESPONSE_CATEGORY_FULL, getException<FailedOrderException> { procedure.updateCategoryLimitations(true) }.response)
        assertEquals(usedPerCategory[categoryId][0], opening.usedAlpha)
        assertEquals(usedPerCategory[categoryId][1], opening.usedBeta)
        assertEquals(usedPerCategory[categoryId][2], opening.usedGamma)
        assertEquals(usedPerCategory[categoryId][3], opening.usedDelta)
        assertEquals(usedPerCategory[categoryId][4], opening.usedLambda)
    }

    @ParameterizedTest
    @ValueSource(ints = [0, 1, 2, 3, 4])
    fun `check category limitations with default only`() {
        val opening = OpeningEntity(30, dateStart = 0, dateEnd = 0, orderStart = 0, orderEnd = 0)
        val count = 100
        val item = ItemEntity(20, category = ItemCategory.DEFAULT.id)
        val procedure = MakeOrderProcedure(user, 0, count, 0, "", "{}",
                itemsRepo = itemsRepo,
                timeWindowRepo = timewindowRepo,
                extrasRepository = extrasRepository)
        procedure.current = opening
        procedure.item = item
        procedure.count = count

        assertDoesNotThrow { procedure.updateCategoryLimitations(true) }
        assertEquals(0, opening.usedAlpha)
        assertEquals(0, opening.usedBeta)
        assertEquals(0, opening.usedGamma)
        assertEquals(0, opening.usedDelta)
        assertEquals(0, opening.usedLambda)
    }

    @Test
    fun `validate user's room number`() {
        whenever(user.room).thenReturn("1620")

        val procedure = MakeOrderProcedure(user, 0, 0, 0, "", "{}",
                itemsRepo = itemsRepo,
                timeWindowRepo = timewindowRepo,
                extrasRepository = extrasRepository)

        assertDoesNotThrow { procedure.validateUserHasRoom() }
    }

    @Test
    fun `validate user's invalid room number`() {
        whenever(user.room).thenReturn("")

        val procedure = MakeOrderProcedure(user, 0, 0, 0, "", "{}",
                itemsRepo = itemsRepo,
                timeWindowRepo = timewindowRepo,
                extrasRepository = extrasRepository)

        assertThrows<FailedOrderException> { procedure.validateUserHasRoom() }
        assertEquals(RESPONSE_NO_ROOM_SET, getException<FailedOrderException> { procedure.validateUserHasRoom() }.response)
    }

    @Test
    fun `create user entity`() {
        whenever(user.uid).thenReturn("unique")
        whenever(user.room).thenReturn("SCH-1620")
        whenever(user.name).thenReturn("Test User")
        whenever(user.cardType).thenReturn(CardType.KB)

        val detailsJson = "{\"details\": \"detail\"}"
        val comment = "comment"
        val procedure = MakeOrderProcedure(user, 0, 0, 0, comment, detailsJson,
                itemsRepo = itemsRepo,
                timeWindowRepo = timewindowRepo,
                extrasRepository = extrasRepository)

        assertDoesNotThrow { procedure.createOrderEntity() }
        assertEquals("unique", procedure.order.userId)
        assertEquals("Test User", procedure.order.userName)
        assertEquals("[KB] $comment", procedure.order.comment)
        assertEquals(detailsJson, procedure.order.detailsJson)
        assertEquals("SCH-1620", procedure.order.room)
    }

    @Test
    fun `validate loading target not orderable item`() {
        val itemEntity = ItemEntity()
        itemEntity.orderable = false
        itemEntity.personallyOrderable = false
        whenever(itemsRepo.getReferenceById(12)).thenReturn(itemEntity)

        val procedure = MakeOrderProcedure(user, 12, 0, 0, "", "{}",
                itemsRepo = itemsRepo,
                timeWindowRepo = timewindowRepo,
                extrasRepository = extrasRepository)

        assertEquals(RESPONSE_INTERNAL_ERROR, getException<FailedOrderException> { procedure.loadTargetItem() }.response)
    }

    @Test
    fun `validate loading target personally orderable item`() {
        val itemEntity = ItemEntity()
        itemEntity.orderable = true
        itemEntity.personallyOrderable = true
        whenever(itemsRepo.getReferenceById(12)).thenReturn(itemEntity)

        val procedure = MakeOrderProcedure(user, 12, 0, 0, "", "{}",
                itemsRepo = itemsRepo,
                timeWindowRepo = timewindowRepo,
                extrasRepository = extrasRepository)

        assertEquals(RESPONSE_INTERNAL_ERROR, getException<FailedOrderException> { procedure.loadTargetItem() }.response)
    }

    @Test
    fun `validate loading target item`() {
        val itemEntity = ItemEntity()
        itemEntity.orderable = true
        itemEntity.personallyOrderable = false
        whenever(itemsRepo.getReferenceById(12)).thenReturn(itemEntity)

        val procedure = MakeOrderProcedure(user, 12, 0, 0, "", "{}",
                itemsRepo = itemsRepo,
                timeWindowRepo = timewindowRepo,
                extrasRepository = extrasRepository)

        assertDoesNotThrow { procedure.loadTargetItem() }
        assertEquals(itemEntity, procedure.item)
    }

    @Test
    fun `validate update basic details`() {
        val itemEntity = ItemEntity()
        itemEntity.name = "name"
        itemEntity.personallyOrderable = false
        itemEntity.circle = CircleEntity(10)
        val opening = OpeningEntity(30, dateStart = 0, dateEnd = 0, orderStart = 0, orderEnd = 0)
        val timeWindow = TimeWindowEntity(40, opening, "test t. w.", 40, 0)
        whenever(timewindowRepo.findById(40)).thenReturn(Optional.of(timeWindow))

        val procedure = MakeOrderProcedure(user, 0, 0, 40, "", "{}",
                itemsRepo = itemsRepo,
                timeWindowRepo = timewindowRepo,
                extrasRepository = extrasRepository)
        procedure.item = itemEntity
        procedure.order = OrderEntity(userId = "", userName = "", comment = "", detailsJson = "", room = "")

        assertEquals(itemEntity, procedure.item)
        assertDoesNotThrow { procedure.updateBasicDetails() }
        assertEquals(40, procedure.order.intervalId)
        assertEquals("name", procedure.order.name)
        assertEquals(30, procedure.order.openingId)
    }

    @Test
    fun `validate orderable at this moment`() {
        val opening = OpeningEntity(30, dateStart = 200, dateEnd = 300, orderStart = 50, orderEnd = 100)

        val procedure = MakeOrderProcedure(user, 0, 0, 0, "", "{}",
                itemsRepo = itemsRepo,
                timeWindowRepo = timewindowRepo,
                extrasRepository = extrasRepository)
        procedure.current = opening

        assertEquals(RESPONSE_NO_ORDERING, getException<FailedOrderException> { procedure.validateOrderable(49) }.response)
        assertDoesNotThrow { procedure.validateOrderable(50) }
        assertDoesNotThrow { procedure.validateOrderable(75) }
        assertDoesNotThrow { procedure.validateOrderable(100) }
        assertEquals(RESPONSE_NO_ORDERING, getException<FailedOrderException> { procedure.validateOrderable(101) }.response)
        assertEquals(RESPONSE_NO_ORDERING, getException<FailedOrderException> { procedure.validateOrderable(201) }.response)
        assertEquals(RESPONSE_NO_ORDERING, getException<FailedOrderException> { procedure.validateOrderable(299) }.response)
        assertEquals(RESPONSE_NO_ORDERING, getException<FailedOrderException> { procedure.validateOrderable(350) }.response)
    }

    @Test
    fun `validate invalid order count check`() {
        val opening = OpeningEntity(30, dateStart = 0, dateEnd = 0, orderStart = 0, orderEnd = 0, orderCount = 5, maxOrder = 13)

        val procedure = MakeOrderProcedure(user, 0, 0, 40, "", "{}",
                itemsRepo = itemsRepo,
                timeWindowRepo = timewindowRepo,
                extrasRepository = extrasRepository)
        procedure.current = opening
        procedure.count = 10

        assertEquals(RESPONSE_OVERALL_MAX_REACHED, getException<FailedOrderException> { procedure.validateOrderCount() }.response)
    }

    @Test
    fun `validate order count check`() {
        val opening = OpeningEntity(30, dateStart = 0, dateEnd = 0, orderStart = 0, orderEnd = 0, orderCount = 5, maxOrder = 15)

        val procedure = MakeOrderProcedure(user, 0, 0, 40, "", "{}",
                itemsRepo = itemsRepo,
                timeWindowRepo = timewindowRepo,
                extrasRepository = extrasRepository)
        procedure.current = opening
        procedure.count = 10

        assertDoesNotThrow { procedure.validateOrderCount() }
    }

    @Test
    fun `validate time windows with inconsistent opening id`() {
        val opening1 = OpeningEntity(30, dateStart = 0, dateEnd = 0, orderStart = 0, orderEnd = 0)
        val opening2 = OpeningEntity(80, dateStart = 0, dateEnd = 0, orderStart = 0, orderEnd = 0)
        val timeWindow = TimeWindowEntity(opening = opening2)

        val procedure = MakeOrderProcedure(user, 0, 0, 40, "", "{}",
                itemsRepo = itemsRepo,
                timeWindowRepo = timewindowRepo,
                extrasRepository = extrasRepository)
        procedure.current = opening1
        procedure.timeWindow = timeWindow

        assertEquals(RESPONSE_INTERNAL_ERROR, getException<FailedOrderException> { procedure.validateTimeWindow() }.response)
    }

    @Test
    fun `validate time windows with not enough items`() {
        val opening = OpeningEntity(30, dateStart = 0, dateEnd = 0, orderStart = 0, orderEnd = 0)
        val timeWindow = TimeWindowEntity(opening = opening, normalItemCount = 0)

        val procedure = MakeOrderProcedure(user, 0, 1, 40, "", "{}",
                itemsRepo = itemsRepo,
                timeWindowRepo = timewindowRepo,
                extrasRepository = extrasRepository)
        procedure.current = opening
        procedure.timeWindow = timeWindow
        procedure.details = OrderDetailsDto(0, 10)
        procedure.clampItemCount()

        assertEquals(RESPONSE_MAX_REACHED, getException<FailedOrderException> { procedure.validateTimeWindow() }.response)
    }

    @Test
    fun `validate time windows with not enough items 2`() {
        val opening = OpeningEntity(30, dateStart = 0, dateEnd = 0, orderStart = 0, orderEnd = 0)
        val timeWindow = TimeWindowEntity(opening = opening, normalItemCount = 3)

        val procedure = MakeOrderProcedure(user, 0, 4, 40, "", "{}",
                itemsRepo = itemsRepo,
                timeWindowRepo = timewindowRepo,
                extrasRepository = extrasRepository)
        procedure.current = opening
        procedure.timeWindow = timeWindow
        procedure.details = OrderDetailsDto(0, 10)
        procedure.clampItemCount()

        assertEquals(RESPONSE_MAX_REACHED, getException<FailedOrderException> { procedure.validateTimeWindow() }.response)
    }

    @Test
    fun `validate time windows with not enough extra`() {
        val opening = OpeningEntity(30, dateStart = 0, dateEnd = 0, orderStart = 0, orderEnd = 0)
        val timeWindow = TimeWindowEntity(opening = opening, normalItemCount = 4, extraItemCount = 3)

        val procedure = MakeOrderProcedure(user, 0, 4, 40, "", "{}",
                itemsRepo = itemsRepo,
                timeWindowRepo = timewindowRepo,
                extrasRepository = extrasRepository)
        procedure.current = opening
        procedure.timeWindow = timeWindow
        procedure.order = OrderEntity(extraTag = true, userId = "", userName = "", comment = "", detailsJson = "", room = "")
        procedure.details = OrderDetailsDto(0, 10)
        procedure.clampItemCount()

        assertEquals(RESPONSE_MAX_REACHED_EXTRA, getException<FailedOrderException> { procedure.validateTimeWindow() }.response)
    }

    @Test
    fun `validate time windows with enough extra`() {
        val opening = OpeningEntity(30, dateStart = 0, dateEnd = 0, orderStart = 0, orderEnd = 0)
        val timeWindow = TimeWindowEntity(opening = opening, normalItemCount = 4, extraItemCount = 4)

        val procedure = MakeOrderProcedure(user, 0, 4, 40, "", "{}",
                itemsRepo = itemsRepo,
                timeWindowRepo = timewindowRepo,
                extrasRepository = extrasRepository)
        procedure.current = opening
        procedure.timeWindow = timeWindow
        procedure.order = OrderEntity(extraTag = true, userId = "", userName = "", comment = "", detailsJson = "", room = "")
        procedure.details = OrderDetailsDto(0, 10)
        procedure.clampItemCount()

        assertDoesNotThrow { procedure.validateTimeWindow() }
    }

    @Test
    fun `validate time windows without extra`() {
        val opening = OpeningEntity(30, dateStart = 0, dateEnd = 0, orderStart = 0, orderEnd = 0)
        val timeWindow = TimeWindowEntity(opening = opening, normalItemCount = 4, extraItemCount = 0)

        val procedure = MakeOrderProcedure(user, 0, 4, 40, "", "{}",
                itemsRepo = itemsRepo,
                timeWindowRepo = timewindowRepo,
                extrasRepository = extrasRepository)
        procedure.current = opening
        procedure.timeWindow = timeWindow
        procedure.order = OrderEntity(extraTag = false, userId = "", userName = "", comment = "", detailsJson = "", room = "")
        procedure.details = OrderDetailsDto(0, 10)
        procedure.clampItemCount()

        assertDoesNotThrow { procedure.validateTimeWindow() }
    }

    @Test
    fun `validate clamped item count with too low value`() {
        val procedure = MakeOrderProcedure(user, 0, -1, 0, "", "{}",
                itemsRepo = itemsRepo,
                timeWindowRepo = timewindowRepo,
                extrasRepository = extrasRepository)
        procedure.order = OrderEntity(extraTag = false, userId = "", userName = "", comment = "", detailsJson = "", room = "")

        procedure.details = OrderDetailsDto(-10, 10)
        assertDoesNotThrow { procedure.clampItemCount() }
        assertEquals(1, procedure.count)

        procedure.details = OrderDetailsDto(0, 10)
        assertDoesNotThrow { procedure.clampItemCount() }
        assertEquals(1, procedure.count)

        procedure.details = OrderDetailsDto(1, 10)
        assertDoesNotThrow { procedure.clampItemCount() }
        assertEquals(1, procedure.count)

        procedure.details = OrderDetailsDto(3, 10)
        assertDoesNotThrow { procedure.clampItemCount() }
        assertEquals(3, procedure.count)
    }

    @Test
    fun `validate clamped item count with too high value`() {
        val procedure = MakeOrderProcedure(user, 0, 100, 0, "", "{}",
                itemsRepo = itemsRepo,
                timeWindowRepo = timewindowRepo,
                extrasRepository = extrasRepository)
        procedure.order = OrderEntity(extraTag = false, userId = "", userName = "", comment = "", detailsJson = "", room = "")

        procedure.details = OrderDetailsDto(-10, 10)
        assertDoesNotThrow { procedure.clampItemCount() }
        assertEquals(10, procedure.count)

        procedure.details = OrderDetailsDto(3, 7)
        assertDoesNotThrow { procedure.clampItemCount() }
        assertEquals(7, procedure.count)
    }

    @Test
    fun `validate remaining item update with no extra tag`() {
        val opening = OpeningEntity(30, dateStart = 0, dateEnd = 0, orderStart = 0, orderEnd = 0)
        val timeWindow = TimeWindowEntity(opening = opening, normalItemCount = 5, extraItemCount = 4)

        val procedure = MakeOrderProcedure(user, 0, 4, 40, "", "{}",
                itemsRepo = itemsRepo,
                timeWindowRepo = timewindowRepo,
                extrasRepository = extrasRepository)
        procedure.current = opening
        procedure.timeWindow = timeWindow
        procedure.order = OrderEntity(extraTag = false, userId = "", userName = "", comment = "", detailsJson = "", room = "")
        procedure.count = 4

        assertDoesNotThrow { procedure.updateRemainingItemCount() }
        assertEquals(1, timeWindow.normalItemCount)
        assertEquals(4, timeWindow.extraItemCount)
        assertEquals(4, opening.orderCount)
    }

    @Test
    fun `validate remaining item update with extra tag`() {
        val opening = OpeningEntity(30, dateStart = 0, dateEnd = 0, orderStart = 0, orderEnd = 0)
        val timeWindow = TimeWindowEntity(opening = opening, normalItemCount = 5, extraItemCount = 4)

        val procedure = MakeOrderProcedure(user, 0, 4, 40, "", "{}",
                itemsRepo = itemsRepo,
                timeWindowRepo = timewindowRepo,
                extrasRepository = extrasRepository)
        procedure.current = opening
        procedure.timeWindow = timeWindow
        procedure.order = OrderEntity(extraTag = true, userId = "", userName = "", comment = "", detailsJson = "", room = "")
        procedure.count = 4

        assertDoesNotThrow { procedure.updateRemainingItemCount() }
        assertEquals(1, timeWindow.normalItemCount)
        assertEquals(0, timeWindow.extraItemCount)
        assertEquals(4, opening.orderCount)
    }

    @Test
    fun `validate final details update 4 items with alias`() {
        val opening = OpeningEntity(30, dateStart = 0, dateEnd = 0, orderStart = 0, orderEnd = 30)
        val timeWindow = TimeWindowEntity(opening = opening, name = "6:00-8:00", date = 12, normalItemCount = 5, extraItemCount = 4)
        val item = ItemEntity()
        item.name = "name"
        item.category = 1
        item.alias = "alias"
        whenever(user.orderingPriority).thenReturn(5)

        val procedure = MakeOrderProcedure(user, 0, 4, 40, "", "{}",
                itemsRepo = itemsRepo,
                timeWindowRepo = timewindowRepo,
                extrasRepository = extrasRepository)
        procedure.current = opening
        procedure.timeWindow = timeWindow
        procedure.item = item
        procedure.order = OrderEntity(extraTag = true, price = 300, userId = "", userName = "", comment = "", detailsJson = "", room = "")
        procedure.count = 4

        assertDoesNotThrow { procedure.updateOrderDetails() }
        assertEquals(12, procedure.order.date)
        assertEquals(300 * 4, procedure.order.price)
        assertEquals("6:00-8:00", procedure.order.intervalMessage)
        assertEquals(30, procedure.order.cancelUntil)
        assertEquals(1, procedure.order.category)
        assertEquals(5, procedure.order.priority)
        assertEquals("alias x 4", procedure.order.compactName)
        assertEquals(4, procedure.order.count)
    }

    @Test
    fun `validate final details update 1 items with alias`() {
        val opening = OpeningEntity(30, dateStart = 0, dateEnd = 0, orderStart = 0, orderEnd = 30)
        val timeWindow = TimeWindowEntity(opening = opening, name = "6:00-8:00", date = 12, normalItemCount = 5, extraItemCount = 4)
        val item = ItemEntity()
        item.name = "name"
        item.category = 1
        item.alias = "alias"
        whenever(user.orderingPriority).thenReturn(0)

        val procedure = MakeOrderProcedure(user, 0, 1, 40, "", "{}",
                itemsRepo = itemsRepo,
                timeWindowRepo = timewindowRepo,
                extrasRepository = extrasRepository)
        procedure.current = opening
        procedure.timeWindow = timeWindow
        procedure.item = item
        procedure.order = OrderEntity(extraTag = true, price = 300, userId = "", userName = "", comment = "", detailsJson = "", room = "")
        procedure.count = 1

        assertDoesNotThrow { procedure.updateOrderDetails() }
        assertEquals(12, procedure.order.date)
        assertEquals(300, procedure.order.price)
        assertEquals("6:00-8:00", procedure.order.intervalMessage)
        assertEquals(30, procedure.order.cancelUntil)
        assertEquals(1, procedure.order.category)
        assertEquals(0, procedure.order.priority)
        assertEquals("alias", procedure.order.compactName)
        assertEquals(1, procedure.order.count)
    }

    @Test
    fun `validate final details update 4 items without alias`() {
        val opening = OpeningEntity(30, dateStart = 0, dateEnd = 0, orderStart = 0, orderEnd = 30)
        val timeWindow = TimeWindowEntity(opening = opening, name = "6:00-8:00", date = 12, normalItemCount = 5, extraItemCount = 4)
        val item = ItemEntity()
        item.name = "name"
        item.category = 2
        item.alias = ""
        whenever(user.orderingPriority).thenReturn(5)

        val procedure = MakeOrderProcedure(user, 0, 4, 40, "", "{}",
                itemsRepo = itemsRepo,
                timeWindowRepo = timewindowRepo,
                extrasRepository = extrasRepository)
        procedure.current = opening
        procedure.timeWindow = timeWindow
        procedure.item = item
        procedure.order = OrderEntity(extraTag = true, price = 300, userId = "", userName = "", comment = "", detailsJson = "", room = "")
        procedure.count = 4

        assertDoesNotThrow { procedure.updateOrderDetails() }
        assertEquals(12, procedure.order.date)
        assertEquals(300 * 4, procedure.order.price)
        assertEquals("6:00-8:00", procedure.order.intervalMessage)
        assertEquals(30, procedure.order.cancelUntil)
        assertEquals(2, procedure.order.category)
        assertEquals(5, procedure.order.priority)
        assertEquals("name x 4", procedure.order.compactName)
        assertEquals(4, procedure.order.count)
    }

    @Test
    fun `validate final details update 1 items without alias`() {
        val opening = OpeningEntity(30, dateStart = 0, dateEnd = 0, orderStart = 0, orderEnd = 30)
        val timeWindow = TimeWindowEntity(opening = opening, name = "6:00-8:00", date = 12, normalItemCount = 5, extraItemCount = 4)
        val item = ItemEntity()
        item.name = "name"
        item.category = 2
        item.alias = ""
        whenever(user.orderingPriority).thenReturn(5)

        val procedure = MakeOrderProcedure(user, 0, 1, 40, "", "{}",
                itemsRepo = itemsRepo,
                timeWindowRepo = timewindowRepo,
                extrasRepository = extrasRepository)
        procedure.current = opening
        procedure.timeWindow = timeWindow
        procedure.item = item
        procedure.order = OrderEntity(extraTag = true, price = 300, userId = "", userName = "", comment = "", detailsJson = "", room = "")
        procedure.count = 1

        assertDoesNotThrow { procedure.updateOrderDetails() }
        assertEquals(12, procedure.order.date)
        assertEquals(300, procedure.order.price)
        assertEquals("6:00-8:00", procedure.order.intervalMessage)
        assertEquals(30, procedure.order.cancelUntil)
        assertEquals(2, procedure.order.category)
        assertEquals(5, procedure.order.priority)
        assertEquals("name", procedure.order.compactName)
        assertEquals(1, procedure.order.count)
    }

    @Test
    fun `composite test`() {
        val opening = OpeningEntity(30, maxOrder = 5, dateStart = 0, dateEnd = 0, orderStart = 0,
                orderEnd = Instant.now().toEpochMilli() * 2, maxBeta = 10)

        val timeWindow = TimeWindowEntity(opening = opening, name = "6:00-8:00", date = 12, normalItemCount = 5, extraItemCount = 4)
        whenever(timewindowRepo.getReferenceById(40)).thenReturn(timeWindow)
        whenever(timewindowRepo.findById(40)).thenReturn(Optional.of(timeWindow))
        val item = ItemEntity(name = "name", category = 2, orderable = true, personallyOrderable = false,
                alias = "", circle = CircleEntity(10), price = 1200)
        whenever(itemsRepo.getReferenceById(12)).thenReturn(item)

        whenever(user.uid).thenReturn("unique")
        whenever(user.room).thenReturn("SCH-1620")
        whenever(user.name).thenReturn("Test User")
        whenever(user.cardType).thenReturn(CardType.KB)
        whenever(user.orderingPriority).thenReturn(5)

        val comment = "comment here"
        val procedure = MakeOrderProcedure(user, 12, 1, 40, comment, "{\"answers\": []}",
                itemsRepo = itemsRepo,
                timeWindowRepo = timewindowRepo,
                extrasRepository = extrasRepository)

        assertDoesNotThrow { procedure.makeOrder() }
        assertEquals(12, procedure.order.date)
        assertEquals(1200, procedure.order.price)
        assertEquals("6:00-8:00", procedure.order.intervalMessage)
        assertEquals(opening.orderEnd, procedure.order.cancelUntil)
        assertEquals(2, procedure.order.category)
        assertEquals(5, procedure.order.priority)
        assertEquals("name", procedure.order.compactName)
        assertEquals(1, procedure.order.count)
        assertEquals("unique", procedure.order.userId)
        assertEquals("Test User", procedure.order.userName)
        assertEquals("[KB] $comment", procedure.order.comment)
        assertEquals("{\"answers\": []}", procedure.order.detailsJson)
        assertEquals("SCH-1620", procedure.order.room)
        assertEquals(item, procedure.item)
        assertEquals(timeWindow, procedure.timeWindow)
    }
}
