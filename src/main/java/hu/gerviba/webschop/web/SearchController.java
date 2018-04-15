package hu.gerviba.webschop.web;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import hu.gerviba.webschop.dao.ItemEntityDao;
import hu.gerviba.webschop.model.ItemEntity;
import hu.gerviba.webschop.service.HibernateSearchService;

@Controller
public class SearchController {

	@Autowired
    private HibernateSearchService searchservice;

    @GetMapping(value = "/search")
    public ResponseEntity<List<ItemEntityDao>> search(@RequestParam(value = "search", required = false) String q, Model model) {
        List<ItemEntity> searchResults = null;
        try {
            searchResults = searchservice.fuzzySearch(q);

        } catch (Exception ex) {
        	ex.printStackTrace();
        }
        model.addAttribute("search", searchResults);
        
        return new ResponseEntity<List<ItemEntityDao>>(searchResults.stream()
        		.map(x -> new ItemEntityDao(x))
        		.collect(Collectors.toList()), HttpStatus.OK);
    }
	
}
