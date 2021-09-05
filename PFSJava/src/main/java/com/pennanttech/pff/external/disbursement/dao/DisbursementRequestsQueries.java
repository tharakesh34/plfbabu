package com.pennanttech.pff.external.disbursement.dao;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.pennanttech.pennapps.core.resource.Literal;

public class DisbursementRequestsQueries {
	private static Logger logger = LogManager.getLogger(DisbursementRequestsQueries.class);

	private static String selectQuery = null;
	private static String insertHeaderQuery = null;
	private static String insertQuery = null;

	private static String selectMovementListQuery = null;
	private static String selectMovementQuery = null;
	private static String insertMovement = null;
	private static String insertLogMovement = null;

	private DisbursementRequestsQueries() {
		super();
	}

	public static String getSelectAllQuery() {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" PAYMENTID DISBURSEMENT_ID, CUSTCIF, FINID, FINREFERENCE, AMTTOBERELEASED DISBURSEMENT_AMOUNT");
		sql.append(", DISBURSEMENT_TYPE, LLDATE DISBURSEMENT_DATE, PAYABLELOC DRAWEE_LOCATION");
		sql.append(", PRINTINGLOC PRINT_LOCATION, CUSTSHRTNAME CUSTOMER_NAME, CUSTOMER_MOBILE, CUSTOMER_EMAIL");
		sql.append(", CUSTOMER_STATE, CUSTOMER_CITY, CUSTOMER_ADDRESS1, CUSTOMER_ADDRESS2, CUSTOMER_ADDRESS3");
		sql.append(", CUSTOMER_ADDRESS4, CUSTOMER_ADDRESS5, BANKNAME BENFICIARY_BANK, BRANCHDESC BENFICIARY_BRANCH");
		sql.append(", BENFICIARY_BRANCH_STATE, BENFICIARY_BRANCH_CITY, MICR_CODE, IFSC_CODE");
		sql.append(", BENEFICIARYACCNO BENFICIARY_ACCOUNT, BENEFICIARYNAME BENFICIARY_NAME");
		sql.append(", BENEFICIARY_MOBILE BENFICIARY_MOBILE, BENFICIRY_EMAIL, BENFICIARY_STATE BENFICIRY_STATE");
		sql.append(", BENFICIARY_CITY BENFICIRY_CITY, BENFICIARY_ADDRESS1, BENFICIARY_ADDRESS2, BENFICIARY_ADDRESS3");
		sql.append(", BENFICIARY_ADDRESS4, BENFICIARY_ADDRESS5, PAYMENT_DETAIL1, PAYMENT_DETAIL2, PAYMENT_DETAIL3");
		sql.append(", PAYMENT_DETAIL4, PAYMENT_DETAIL5, PAYMENT_DETAIL6, PAYMENT_DETAIL7, STATUS, REMARKS, CHANNEL");
		sql.append(", PARTNERBANK_ID PARTNER_BANK_ID, PARTNERBANK_CODE PARTNER_BANK_CODE, PARTNERBANK_ACCOUNT");
		sql.append(", ALWFILEDOWNLOAD ALW_FILE_DOWNLOAD, CHEQUE_NUMBER, FINAMOUNT");
		sql.append(" FROM INT_DISBURSEMENT_REQUEST_VIEW");
		sql.append(" WHERE PAYMENTID IN (SELECT PAYMENTID FROM DISBURSEMENT_REQUESTS_HEADER WHERE ID= (?))");

		logger.debug(Literal.SQL + sql.toString());

		return sql.toString();
	}

	public static String getSelectQuery() {
		if (selectQuery != null) {
			logger.debug(Literal.SQL + selectQuery);

			return selectQuery;
		}

		StringBuilder sql = new StringBuilder("SELECT");
		sql.append(" PAYMENTID, PAYMENTTYPE, FA.PARTNERBANKID, PARTNERBANKCODE, ALWFILEDOWNLOAD, CHANNEL FROM (");
		sql.append(" SELECT PAYMENTID, PAYMENTTYPE, PARTNERBANKID, STATUS, 'D' CHANNEL");
		sql.append(" FROM FINADVANCEPAYMENTS");
		sql.append(" UNION ALL");
		sql.append(" SELECT PAYMENTINSTRUCTIONID PAYMENTID, PAYMENTTYPE, PARTNERBANKID, STATUS, 'P' CHANNEL");
		sql.append(" FROM PAYMENTINSTRUCTIONS ) FA");
		sql.append(" INNER JOIN PARTNERBANKS PB ON PB.PARTNERBANKID = FA.PARTNERBANKID");
		sql.append(" WHERE PAYMENTID IN (SELECT PAYMENTID FROM DISBURSEMENT_REQUESTS_HEADER WHERE ID = ?)");
		sql.append(" AND FA.STATUS = ?");

		selectQuery = sql.toString();

		logger.debug(Literal.SQL + selectQuery);

		return selectQuery;
	}

	public static String getInsertQuery() {
		if (insertQuery != null) {
			logger.debug(Literal.SQL + insertQuery);

			return insertQuery;
		}

		StringBuilder sql = new StringBuilder();
		sql.append("INSERT INTO DISBURSEMENT_REQUESTS (");
		sql.append(" DISBURSEMENT_ID, CUSTCIF, FINID, FINREFERENCE, DISBURSEMENT_AMOUNT, DISBURSEMENT_TYPE");
		sql.append(", DISBURSEMENT_DATE, DRAWEE_LOCATION, PRINT_LOCATION, CUSTOMER_NAME, CUSTOMER_MOBILE");
		sql.append(", CUSTOMER_EMAIL, CUSTOMER_STATE, CUSTOMER_CITY, CUSTOMER_ADDRESS1, CUSTOMER_ADDRESS2");
		sql.append(", CUSTOMER_ADDRESS3, CUSTOMER_ADDRESS4, CUSTOMER_ADDRESS5, BENFICIARY_BANK, BENFICIARY_BRANCH");
		sql.append(", BENFICIARY_BRANCH_STATE, BENFICIARY_BRANCH_CITY, MICR_CODE, IFSC_CODE");
		sql.append(", BENFICIARY_ACCOUNT, BENFICIARY_NAME, BENFICIARY_MOBILE, BENFICIRY_EMAIL, BENFICIRY_STATE");
		sql.append(", BENFICIRY_CITY, BENFICIARY_ADDRESS1, BENFICIARY_ADDRESS2, BENFICIARY_ADDRESS3");
		sql.append(", BENFICIARY_ADDRESS4, BENFICIARY_ADDRESS5, PAYMENT_DETAIL1, PAYMENT_DETAIL2, PAYMENT_DETAIL3");
		sql.append(", PAYMENT_DETAIL4, PAYMENT_DETAIL5, PAYMENT_DETAIL6, PAYMENT_DETAIL7, STATUS, REMARKS");
		sql.append(", CHANNEL, BATCH_ID, AUTO_DOWNLOAD, HEADER_ID");
		sql.append(", PARTNERBANK_ID, PARTNERBANK_CODE, PARTNERBANK_ACCOUNT, CHEQUE_NUMBER, DOWNLOADED_ON)");
		sql.append(" Values (");
		sql.append(" ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?");
		sql.append(", ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

		insertQuery = sql.toString();

		logger.debug(Literal.SQL + insertQuery);

		return insertQuery;
	}

	public static String getInsertHeaderQuery() {
		if (insertHeaderQuery != null) {
			logger.debug(Literal.SQL + insertHeaderQuery);

			return insertHeaderQuery;
		}

		StringBuilder sql = new StringBuilder();
		sql.append("INSERT INTO DISBURSEMENT_REQUESTS_HEADER");
		sql.append(" Select ?, Channel, PaymentID, ?, ? From (");
		sql.append(" Select PaymentID, 'D' Channel, Status From FinAdvancePayments");
		sql.append(" Union All");
		sql.append(" Select PaymentInstructionID PaymentID, 'P' Channel, Status From PaymentInstructions");
		sql.append(" Union All");
		sql.append(" Select ID PaymentID, 'I' Channel, Status From InsurancePaymentInstructions) t");
		sql.append(" Where t.PaymentID in (?) and t.Status = ?");
		sql.append(" and T.PaymentID not in (Select PaymentID From Disbursement_Requests_Header)");

		insertHeaderQuery = sql.toString();

		logger.debug(Literal.SQL + insertHeaderQuery);

		return insertHeaderQuery;

	}

	public static String getInsertMovement() {
		if (insertMovement != null) {
			logger.debug(Literal.SQL + insertMovement);

			return insertMovement;
		}

		StringBuilder sql = new StringBuilder("INSERT INTO DISBURSEMENT_MOVEMENTS");
		sql.append("(HEADER_ID, BATCH_ID, TARGET_TYPE, FILE_NAME, FILE_LOCATION");
		sql.append(", DATA_ENGINE_CONFIG, POST_EVENTS, CREATED_ON, CREATED_BY, PROCESSED_ON, FAILURE_REASON)");
		sql.append(" VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

		insertMovement = sql.toString();

		logger.debug(Literal.SQL + insertMovement);

		return insertMovement;

	}

	public static String getInsertLogMovement() {
		if (insertLogMovement != null) {
			logger.debug(Literal.SQL + insertLogMovement);

			return insertLogMovement;
		}

		StringBuilder sql = new StringBuilder("INSERT INTO DISBURSEMENT_MOVEMENTS_LOG");
		sql.append("(HEADER_ID, BATCH_ID, TARGET_TYPE, FILE_NAME, FILE_LOCATION");
		sql.append(", DATA_ENGINE_CONFIG, POST_EVENTS, CREATED_ON, CREATED_BY, PROCESS_FLAG");
		sql.append(", PROCESSED_ON, FAILURE_REASON)");
		sql.append(" VALUES ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

		insertLogMovement = sql.toString();

		logger.debug(Literal.SQL + insertLogMovement);

		return insertLogMovement;

	}

	public static String getMovementListQuery() {
		if (selectMovementListQuery != null) {
			logger.debug(Literal.SQL + selectMovementListQuery);

			return selectMovementListQuery;
		}

		selectMovementListQuery = "SELECT ID FROM DISBURSEMENT_MOVEMENTS WHERE PROCESS_FLAG = ?";

		logger.debug(Literal.SQL + selectMovementListQuery);

		return selectMovementListQuery;
	}

	public static String getMovementQuery() {
		if (selectMovementQuery != null) {
			logger.debug(Literal.SQL + selectMovementQuery);

			return selectMovementQuery;
		}

		selectMovementQuery = "SELECT * FROM DISBURSEMENT_MOVEMENTS WHERE ID = ?";

		logger.debug(Literal.SQL + selectMovementQuery);

		return selectMovementQuery;
	}

}
