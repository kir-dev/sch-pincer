package hu.kirdev.schpincer.web

import hu.kirdev.schpincer.dto.ItemEntityDto
import hu.kirdev.schpincer.service.HibernateSearchService
import io.swagger.annotations.ApiOperation
import lombok.extern.slf4j.Slf4j
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import javax.servlet.http.HttpServletRequest

@Slf4j
@Controller
open class SearchController {

    private val log = LoggerFactory.getLogger(javaClass)

    @Autowired
    private lateinit var searchservice: HibernateSearchService

    @ApiOperation("Search query")
    @GetMapping(value = ["/api/search"])
    @ResponseBody
    fun search(
            request: HttpServletRequest,
            @RequestParam(value = "q") search: String
    ): ResponseEntity<List<ItemEntityDto>> {

        val loggedIn = request.hasUser() || request.isInInternalNetwork()
        try {
            return ResponseEntity(searchservice.fuzzySearchItem(search, loggedIn), HttpStatus.OK)
        } catch (ex: Exception) {
            log.error("Error in search query", ex)
        }
        return ResponseEntity<List<ItemEntityDto>>(HttpStatus.INTERNAL_SERVER_ERROR)
    }

}