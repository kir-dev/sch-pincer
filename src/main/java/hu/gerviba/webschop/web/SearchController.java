package hu.gerviba.webschop.web;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import hu.gerviba.webschop.dao.ItemEntityDto;
import hu.gerviba.webschop.service.HibernateSearchService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@Api(value="onlinestore", description="Search RestAPI")
public class SearchController {

    @Autowired
    ControllerUtil util;
    
	@Autowired
    HibernateSearchService searchservice;

    @ApiOperation("Search query")
    @GetMapping(value = "/api/search")
    @ResponseBody
    public ResponseEntity<List<ItemEntityDto>> search(
            HttpServletRequest request, 
            @RequestParam(value = "q") String search) {
        
        boolean loggedIn = util.getUser(request) != null || util.isInInternalNetwork(request);
        try {
        	return new ResponseEntity<List<ItemEntityDto>>(searchservice.fuzzySearchItem(search, loggedIn), HttpStatus.OK);
        } catch (Exception ex) {
        	log.error("Error in search query", ex);
        }
        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
	
}
