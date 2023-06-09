package com.pennant.backend.util;

public class NOCConstants {

	private NOCConstants() {
		super();
	}

	public static final String MODE_EMAIL = "EMAIL";
	public static final String MODE_COURIER = "COURIER";

	public static final String TYPE_NOC_LTR = "NOCLTR";
	public static final String TYPE_CAN_LTR = "CANCLLTR";
	public static final String TYPE_CLOSE_LTR = "CLOSELTR";

	public static final String BLOCK = "B";
	public static final String REMOVE_BLOCK = "R";

	public static final String TYPE_CANCLTR = "Cancellation Letter";

	// FIXME :: Murthy.y (Need to move this method to seperate util class
	public static String getLetterType(String closureType) {
		String letterType = null;

		switch (closureType) {
		case "NOC": {
			letterType = TYPE_NOC_LTR;
			break;
		}
		case "CLOSURE": {
			letterType = TYPE_CLOSE_LTR;
			break;
		}
		case "CANCELLATION": {
			letterType = TYPE_CAN_LTR;
			break;
		}
		default:
			break;
		}
		return letterType;
	}

}
