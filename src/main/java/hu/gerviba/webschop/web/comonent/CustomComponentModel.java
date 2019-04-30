package hu.gerviba.webschop.web.comonent;

import java.util.List;

import lombok.Data;

@Data
public class CustomComponentModel {

	private String type;
	private String name;
	private List<String> values;
	private List<Integer> prices;
    private List<String> aliases;
	private String _display;
	private boolean _hide;
	private String _comment;
	
    @Data
    public static class CustomComponentModelList {
        List<CustomComponentModel> models;
    }	
}
