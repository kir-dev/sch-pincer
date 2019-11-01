package hu.gerviba.webschop.web.comonent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

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
            List<String> list = new ArrayList<>();
            list.add(ccm.getAliases().get(cca.getSelected().get(0)));
            return list;
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
                result.add(ccm.getAliases().get(index));
            return result;
        }
	},
	PIZZASCH_SELECT {
        @Override
        public int processPrices(CustomComponentAnswer cca, OrderEntity oe, CustomComponentModel ccm) {
            return ccm.getPrices().get(cca.getSelected().get(0));
        }

        @Override
        public List<String> processMessage(CustomComponentAnswer cca, OrderEntity oe, CustomComponentModel ccm) {
            List<String> list = new ArrayList<>();
            list.add(ccm.getAliases().get(cca.getSelected().get(0)));
            return list;
        }
        
        @Override
        public boolean isExtra(CustomComponentAnswer cca) {
            return cca.getSelected().get(0) > 0;
        }
    },
	LANGOSCH_IMAGEDRAWER, // Not sure hogyan fog működni
	AMERICANO_SELECT { // Checkbox (a negáltja jelenik meg a pdf-en)
        @Override
        public int processPrices(CustomComponentAnswer cca, OrderEntity oe, CustomComponentModel ccm) {
            int result = 0;
            for (int index : cca.getSelected())
                result += ccm.getPrices().get(index);
            return result;
        }
        
        @Override
        public List<String> processMessage(CustomComponentAnswer cca, OrderEntity oe, CustomComponentModel ccm) {
            List<String> result = new ArrayList<>(ccm.getValues());
            for (int index : cca.getSelected())
                result.removeIf(x -> x.equals(ccm.getAliases().get(index)));
            return result;
        }
	},
	UNKNOWN {
	    @Override
	    public int processPrices(CustomComponentAnswer cca, OrderEntity oe, CustomComponentModel ccm) {
	        throw new RuntimeException("Unknown type: " + cca.getType());
	    }
	},
	;
	
	public int processPrices(CustomComponentAnswer cca, OrderEntity oe, CustomComponentModel ccm) {
	    return 0;
	}

    public List<String> processMessage(CustomComponentAnswer cca, OrderEntity oe, CustomComponentModel ccm) {
        return new ArrayList<>(0);
    }
    
    public boolean isExtra(CustomComponentAnswer cca) {
        return false;
    }
    
	private static ObjectMapper mapper = new ObjectMapper();
	private static Map<String, CustomComponentType> types = Stream.of(values())
	        .collect(Collectors.toMap(CustomComponentType::name, x -> x));
	
    public static void calculateExtra(String detailsJson, OrderEntity order, ItemEntity ie) throws IOException {
        
        CustomComponentAnswerList answers = mapper.readValue(detailsJson.getBytes(), CustomComponentAnswerList.class);
        CustomComponentModelList models = mapper.readValue(
                ("{\"models\":" + ie.getDetailsConfigJson() + "}").getBytes(), 
                CustomComponentModelList.class);
        Map<String, CustomComponentModel> mapped = models.getModels().stream()
                .collect(Collectors.toMap(CustomComponentModel::getName, x -> x));
        
        int extraPrice = 0;
        List<String> extraString = new ArrayList<>();
        for (CustomComponentAnswer answer : answers.getAnswers()) {
            extraPrice += types.getOrDefault(answer.getType(), UNKNOWN)
                    .processPrices(answer, order, mapped.get(answer.getName()));
            extraString.add(String.join(", ", types.getOrDefault(answer.getType(), UNKNOWN)
                    .processMessage(answer, order, mapped.get(answer.getName()))));
            if (types.getOrDefault(answer.getType(), UNKNOWN).isExtra(answer))
                order.setExtraTag(true);
        }
        
        order.setPrice(ie.getPrice() + extraPrice);
        order.setExtra(extraString.stream().filter(Objects::nonNull).collect(Collectors.joining("; ")));
    }

}
