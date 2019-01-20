package hu.gerviba.webschop.web.comonent;

import hu.gerviba.webschop.model.OrderEntity;

public enum CustomComponentType {
	EXTRA_SELECT {
		
	},
	EXTRA_CHECKBOX {
		
	},
	LANGOSCH_IMAGEDRAWER, // Not sure hogyan fog működni
	AMERICANO_EXTRA, // Checkbox (a negáltja jelenik meg a pdf-en)
	;
	
	private String typeName;
	
	public void applyChanges(OrderEntity order, String sentValue) {};
	
}
