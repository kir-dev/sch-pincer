package hu.kirdev.schpincer.web.comonent;

import java.util.List;

import lombok.Data;

/**
 * Variables starting with _ (underscore) are meant to be optional.
 * I know this is a bad practice, but it is part of the API, so changing it is not recommended.
 */
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
	private int min;
	private int max;

    @Data
    public static class CustomComponentModelList {
        List<CustomComponentModel> models;
    }	
}
