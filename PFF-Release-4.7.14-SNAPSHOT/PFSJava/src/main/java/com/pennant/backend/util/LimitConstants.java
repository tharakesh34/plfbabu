package com.pennant.backend.util;

import java.util.ArrayList;

import com.pennant.backend.model.ValueLabel;

public class LimitConstants {

	private LimitConstants() {
		super();
	}

	// LIMIT STRUCTURE TYPES
	public static final String				LIMIT_STRUCTURE_CUSTOMER				= "C";
	public static final String				LIMIT_STRUCTURE_GROUP					= "G";
	public static final String				LIMIT_ITEM_RULE							= "LMTLINE";
	public static final String				LIMIT_STRUCTURE_RULE_BASED				= "R";
	public static final String				LIMIT_STRUCTURE_DIALOG_BTN_REMOVE_LABEL	= "REMOVE";
	public static final String				LIMIT_CATEGORY_CUST						= "CUSTOMER";
	public static final String				LIMIT_CATEGORY_BANK						= "BANK";

	public static final String				LIMIT_TRAN_BLOCK						= "BLOCKAMOUNT";
	public static final String				LIMIT_TRAN_APPROVE						= "APPVAMOUNT";
	public static final String				LIMIT_TRAN_PRNPAY						= "PRNPAYMENT";

	public static final String				LIMIT_REFCOD_FINANCE					= "FIN";

	public static final String				LIMIT_CUST								= "CUST";
	public static final String				LIMIT_RULE								= "RULE";
	public static final String				LIMIT_UTILIZATION						= "LTUZ";

	public static final String				LIMIT_ITEM_UNCLSFD						= "UNCLSFD";
	public static final String				LIMIT_ITEM_TOTAL						= "TOTAL";
	public static final String				LIMIT_RULE_FIXED						= "F";
	public static final String				LIMIT_RULE_VARIABLE						= "V";
	public static final String				LIMIT_GROUP_LINE						= "LMTLINE";
	public static final String				LIMIT_GROUP_GROUP						= "GROUP";

	public static final String				LIMIT_CHECK_ACTUAL						= "A";
	public static final String				LIMIT_CHECK_RESERVED					= "R";

	// Currency Units
	public static final String				CCY_UNITS_DEFAULT						= "D";
	public static final String				CCY_UNITS_THOUSANDS						= "T";
	public static final String				CCY_UNITS_LAKHS							= "L";
	public static final String				CCY_UNITS_MILLIONS						= "M";
	public static final String				CCY_UNITS_CRORES						= "C";
	public static final String				CCY_UNITS_BILLIONS						= "B";

	// Limit Institution filter
	public static final String				LIMIT_FILTER_GLOBAL						= "global";

	//reserver
	public static final String				BLOCK									= "B";
	//Loan reject
	public static final String				UNBLOCK									= "U";
	//utilised
	public static final String				APPROVE									= "A";
	//loan cancel
	public static final String				CANCIL									= "C";
	public static final String				PRINPAY									= "P";
	public static final String				REPAY									= "R";

	public static final String				FINANCE									= "F";
	public static final String				COMMITMENT								= "C";

	private static ArrayList<ValueLabel>	transactionType;

	public static ArrayList<ValueLabel> getTransactionTypeList() {

		if (transactionType != null) {
			return transactionType;
		}

		transactionType = new ArrayList<ValueLabel>(4);
		transactionType.add(new ValueLabel(BLOCK, "Reserved"));
		transactionType.add(new ValueLabel(APPROVE, "Utilised"));
		transactionType.add(new ValueLabel(UNBLOCK, "Rejected"));
		transactionType.add(new ValueLabel(CANCIL, "Cancelled"));
		transactionType.add(new ValueLabel(REPAY, "Payment"));
		transactionType.add(new ValueLabel(PRINPAY, "Payment"));

		return transactionType;
	}

}
