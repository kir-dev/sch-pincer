package hu.gerviba.webschop.web.comonent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Objects;

import hu.gerviba.webschop.model.ItemEntity;
import hu.gerviba.webschop.model.OrderEntity;
import hu.gerviba.webschop.web.comonent.CustomComponentAnswer.CustomComponentAnswerList;
import hu.gerviba.webschop.web.comonent.CustomComponentModel.CustomComponentModelList;

public enum CustomComponentType {
	EXTRA_SELECT {
	    @Override
        public int processPrices(CustomComponentAnswer cca, OrderEntity oe, CustomComponentModel ccm) {
            return ccm.getPrices().get(cca.getSelected().get(0));
        }

        @Override
        public List<String> processMessage(CustomComponentAnswer cca, OrderEntity oe, CustomComponentModel ccm) {
            return Arrays.asList(ccm.getValues().get(cca.getSelected().get(0)), null);
        }
	},
	EXTRA_CHECKBOX {
        @Override
        public int processPrices(CustomComponentAnswer cca, OrderEntity oe, CustomComponentModel ccm) {
            int result = 0;
            for (int index : cca.getSelected())
                result += ccm.getPrices().get(index);
            return result;
        }
        
        @Override
        public List<String> processMessage(CustomComponentAnswer cca, OrderEntity oe, CustomComponentModel ccm) {
            List<String> result = new ArrayList<>();
            for (int index : cca.getSelected())
                result.add(ccm.getValues().get(index));
            return result;
        }
	},
	LANGOSCH_IMAGEDRAWER, // Not sure hogyan fog működni
	AMERICANO_EXTRA {
        @Override
        public int processPrices(CustomComponentAnswer cca, OrderEntity oe, CustomComponentModel ccm) {
            int result = 0;
            for (int index : cca.getSelected())
                result += ccm.getPrices().get(index);
            return result;
        }
        
        @Override
        public List<String> processMessage(CustomComponentAnswer cca, OrderEntity oe, CustomComponentModel ccm) {
            List<String> result = new ArrayList<>();
            for (int index : cca.getSelected())
                result.add(ccm.getValues().get(index));
            return result;
        }
	}, // Checkbox (a negáltja jelenik meg a pdf-en)
	UNKNOWN {
	    @Override
	    public int processPrices(CustomComponentAnswer cca, OrderEntity oe, CustomComponentModel ccm) {
	        throw new RuntimeException("Unknown type: " + cca.getType());
	    }
	},
	;
	
	public void applyChanges(OrderEntity order, String sentValue) {}

	public int processPrices(CustomComponentAnswer cca, OrderEntity oe, CustomComponentModel ccm) {
	    return 0;
	}

    public List<String> processMessage(CustomComponentAnswer cca, OrderEntity oe, CustomComponentModel ccm) {
        return null;
    }
    
	private static ObjectMapper mapper = new ObjectMapper();
	private static Map<String, CustomComponentType> types = Stream.of(values())
	        .collect(Collectors.toMap(CustomComponentType::name, x -> x));
	
    public static void calculateExtra(String detailsJson, OrderEntity order, ItemEntity ie) throws JsonParseException, JsonMappingException, IOException {
        CustomComponentAnswerList answers = mapper.readValue(detailsJson.getBytes(), CustomComponentAnswerList.class);
        CustomComponentModelList models = mapper.readValue(ie.getDetailsConfigJson().getBytes(), CustomComponentModelList.class);
        Map<String, CustomComponentModel> mapped = models.getModels().stream()
                .collect(Collectors.toMap(CustomComponentModel::getName, x -> x));
        
        int extraPrice = 0;
        List<String> extraString = new ArrayList<>();
        for (CustomComponentAnswer answer : answers.getAnswers()) {
            extraPrice += types.getOrDefault(answer.getType(), UNKNOWN).processPrices(answer, order, mapped.get(answer.getName()));
            extraString.addAll(types.getOrDefault(answer.getType(), UNKNOWN).processMessage(answer, order, mapped.get(answer.getName())));
        }
        
        order.setPrice(extraPrice);
        order.setExtra(extraString.stream().filter(x -> x != null).collect(Collectors.joining(", ")));
    }

    public static String calculateExtraMessages(String detailsJson) {
        // TODO Auto-generated method stub
        return null;
    };
	
}
