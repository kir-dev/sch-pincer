package hu.kirdev.schpincer.web.comonent;

import com.fasterxml.jackson.databind.ObjectMapper;
import hu.gerviba.webschop.dto.OrderDetailsDto;
import hu.kirdev.schpincer.model.ItemEntity;
import hu.kirdev.schpincer.model.OrderEntity;
import hu.kirdev.schpincer.web.comonent.CustomComponentAnswer.CustomComponentAnswerList;
import hu.kirdev.schpincer.web.comonent.CustomComponentModel.CustomComponentModelList;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
	LANGOSCH_IMAGEDRAWER,
	AMERICANO_SELECT {
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
	
    public static OrderDetailsDto calculateExtra(String detailsJson, OrderEntity order, ItemEntity ie) throws IOException {
        OrderDetailsDto details = new OrderDetailsDto();
        CustomComponentAnswerList answers = mapper.readValue(detailsJson.getBytes(), CustomComponentAnswerList.class);
        CustomComponentModelList models = mapper.readValue(
                ("{\"models\":" + ie.getDetailsConfigJson() + "}").getBytes(),
                CustomComponentModelList.class);
        Map<String, CustomComponentModel> mapped = models.getModels().stream()
                .collect(Collectors.toMap(CustomComponentModel::getName, x -> x));

        for (CustomComponentModel model : models.getModels()) {
            if (model.getType().equals("ITEM_COUNT")) {
                details.setMinCount(model.getMin());
                details.setMaxCount(model.getMax());
            }
        }

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

        order.setPrice((ie.getDiscountPrice() == 0 ? ie.getPrice() : ie.getDiscountPrice()) + extraPrice);
        order.setExtra(extraString.stream().filter(Objects::nonNull).collect(Collectors.joining("; ")));
        return details;
    }

}
