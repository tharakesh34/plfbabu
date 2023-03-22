package com.pennanttech.pff.external.disbursement.dao;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.pennanttech.pennapps.core.resource.Literal;

public class DisbursementRequestsQueries {
	private static Logger logger = LogManager.getLogger(DisbursementRequestsQueries.class);

	private static String insertQuery = null;
	private static String selectMovementListQuery = null;
	private static String selectMovementQuery = null;
	private static String insertMovement = null;
	private static String insertLogMovement = null;

	private DisbursementRequestsQueries() {
		super();
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
		sql.append(", CHANNEL, BATCH_ID, AUTO_DOWNLOAD, HEADER_ID, LEI, CITY_NAME, PROVINCE_NAME");
		sql.append(", PARTNERBANK_ID, PARTNERBANK_CODE, PARTNERBANK_ACCOUNT, CHEQUE_NUMBER, DOWNLOADED_ON");
		sql.append(", PRINT_LOC_BRANCH_DESC)");
		sql.append(" Values (");
		sql.append(" ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?");
		sql.append(", ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

		insertQuery = sql.toString();

		logger.debug(Literal.SQL + insertQuery);

		return insertQuery;
	}

	public static String getInsertMovement() {
		if (insertMovement != null) {
			logger.debug(Literal.SQL + insertMovement);

			return insertMovement;
		}

		StringBuilder sql = new StringBuilder("INSERT INTO DISBURSEMENT_MOVEMENTS");
		sql.append("(HEADER_ID, BATCH_ID, TARGET_TYPE, FILE_NAME, FILE_LOCATION, DATA_ENGINE_CONFIG");
		sql.append(", POST_EVENTS, CREATED_ON, CREATED_BY, PROCESS_FLAG, PROCESSED_ON, FAILURE_REASON)");
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
