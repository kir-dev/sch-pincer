package hu.kirdev.schpincer.web

import hu.kirdev.schpincer.service.CircleService
import hu.kirdev.schpincer.service.OpeningService
import hu.kirdev.schpincer.service.OrderService
import hu.kirdev.schpincer.service.RealtimeConfigService
import hu.kirdev.schpincer.service.StatisticsService
import hu.kirdev.schpincer.service.TimeService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.whenever
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.model
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.view
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.servlet.View
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse

@ExtendWith(MockitoExtension::class)
class ItemsPageFilterTest {

    @Mock
    lateinit var circles: CircleService

    @Mock
    lateinit var openings: OpeningService

    @Mock
    lateinit var orders: OrderService

    @Mock
    lateinit var timeService: TimeService

    @Mock
    lateinit var statService: StatisticsService

    @Mock
    lateinit var config: RealtimeConfigService

    @InjectMocks
    lateinit var controller: MainController

    private lateinit var mockMvc: MockMvc

    @BeforeEach
    fun setup() {
        whenever(circles.findAllForMenu()).thenReturn(emptyList())
        val noopView = object : View {
            override fun getContentType(): String? = null
            override fun render(
                model: MutableMap<String, *>?,
                request: HttpServletRequest,
                response: HttpServletResponse
            ) {
            }
        }
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
            .setViewResolvers({ _, _ -> noopView })
            .build()
    }

    @Test
    fun `plain items page has no filter`() {
        mockMvc.perform(get("/items"))
            .andExpect(status().isOk)
            .andExpect(view().name("items"))
            .andExpect(model().attribute("itemFilter", ""))
            .andExpect(model().attribute("searchMode", false))
    }

    @Test
    fun `valueless now marks the page as orderable now`() {
        // Tomcat binds a flag without "=" to an empty string. MockMvc's URI parser
        // turns that same flag into a null value, so the test sets the servlet parameter directly.
        mockMvc.perform(get("/items").param("now", ""))
            .andExpect(status().isOk)
            .andExpect(model().attribute("itemFilter", "now"))
    }

    @Test
    fun `valueless tomorrow marks the page as orderable tomorrow`() {
        mockMvc.perform(get("/items").param("tomorrow", ""))
            .andExpect(status().isOk)
            .andExpect(model().attribute("itemFilter", "tomorrow"))
    }

    @Test
    fun `now wins when both flags are present and search is kept`() {
        mockMvc.perform(get("/items").param("q", "pizza").param("tomorrow", "").param("now", ""))
            .andExpect(status().isOk)
            .andExpect(model().attribute("itemFilter", "now"))
            .andExpect(model().attribute("keyword", "pizza"))
            .andExpect(model().attribute("searchMode", true))
    }
}
