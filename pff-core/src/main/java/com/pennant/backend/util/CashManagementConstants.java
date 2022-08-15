package com.pennant.backend.util;

public class CashManagementConstants {

	public enum CashRequestType {
		AUTO, ADHOC, C2P, ALL;
	}

	public static final String Request_Init = "I";
	public static final String Request_Init_Desc = "Request Initiation";

	public static final String Request_DownLoad = "P";
	public static final String Request_DownLoad_Desc = "Request Processing";

	public static final String Request_Transit = "T";
	public static final String Request_Transit_Desc = "Cash in Transit";

	public static final String Request_Reject = "R";
	public static final String Request_Reject_Desc = "Request Rejected";

	public static final String Request_Accept = "A";
	public static final String Request_Accept_Desc = "Request Accepted By Branch";

	public static final String REQUEST_MODE_ADHOCREQUEST = "INIT";
	public static final String REQUEST_MODE_CASHINHAD = "ACPT";

	public static final String FILE_STATUS_SUCCESS = "S";
	public static final String FILE_STATUS_ERROR = "E";
	public static final String FILE_STATUS_CANCEL = "C";

	public static final String EOD_DENOMINATION = "EOD";

	public static final String Recon_Status_Tally = "T";
	public static final String Recon_Status_Excess = "E";
	public static final String Recon_Status_Short = "S";

	public static final String Recon_Status_Tally_Desc = "TALLY";
	public static final String Recon_Status_Excess_Desc = "EXCESS";
	public static final String Recon_Status_Short_Desc = "SHORT";

	public static final String Cash_Position_Excess = "E";
	public static final String Cash_Position_Sufficient = "S";
	public static final String Cash_Position_Low = "L";

	public static final String Cash_Position_Excess_Desc = "EXCESS CASH";
	public static final String Cash_Position_Sufficient_Desc = "SUFFCIENT CASH";
	public static final String Cash_Position_Low_Desc = "LOW CASH";

	public static final String Add_Disb_Reserve = "ADR";
	public static final String Cancel_Disb_Reserve = "CDR";
	public static final String Add_CashierPayment = "ACP";
	public static final String Cancel_CashierPayment_AddReserv = "CPR";
	public static final String Add_Receipt_Amount = "ARA";
	public static final String Cancel_Receipt_Amount = "CRA";
	public static final String Add_Cash_To_Pennant = "CTP";

	// Cash To Pennant Transaction Status
	public static final String C2P_STATUS_SUCCESS = "S";
	public static final String C2P_STATUS_FAILURE = "F";
	public static final String C2P_STATUS_HOLD = "H";
	public static final String C2P_STATUS_PENDING = "P";

	// Deposit Movement Transaction Types
	public static final String DEPOSIT_MOVEMENT_CREDIT = "C";
	public static final String DEPOSIT_MOVEMENT_DEBIT = "D";
	public static final String DEPOSIT_MOVEMENT_REVERSE = "R";

	// Cheque/DD process Accounting Status
	public static final String DEPOSIT_CHEQUE_STATUS_APPROVE = "A";
	public static final String DEPOSIT_CHEQUE_STATUS_REVERSE = "R";

	// Deposit Details Events
	public static final String ACCEVENT_DEPOSIT_TYPE_CASH = "CASH";
	public static final String ACCEVENT_DEPOSIT_TYPE_CHEQUE_DD = "CHQDD";

}
