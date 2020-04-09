package hu.kirdev.schpincer.web.comonent;

import java.util.List;

import lombok.Data;

@Data
public class CustomComponentAnswer {

    private String type;
    private String name;
    private List<Integer> selected;
    
    @Data
    public static class CustomComponentAnswerList {
        List<CustomComponentAnswer> answers;
    }
    
}
