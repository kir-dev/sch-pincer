package hu.gerviba.webschop.web;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import hu.gerviba.webschop.dao.ItemEntityDao;
import hu.gerviba.webschop.service.HibernateSearchService;

@Controller
public class SearchController {

	@Autowired
    private HibernateSearchService searchservice;

    @GetMapping(value = "/api/search")
    @ResponseBody
    public ResponseEntity<List<ItemEntityDao>> search(@RequestParam(value = "q") String search) {
        try {
        	return new ResponseEntity<List<ItemEntityDao>>(searchservice.fuzzySearchItem(search), HttpStatus.OK);
        } catch (Exception ex) {
        	ex.printStackTrace();
        }
        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
	
}
