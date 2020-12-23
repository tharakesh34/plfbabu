package com.pennanttech.pff.external.disbursement.dao;

import org.apache.log4j.Logger;

import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.disbursement.model.DisbursementRequest;

public class DisbursementRequestsQueries {
	private static Logger logger = Logger.getLogger(DisbursementRequestsQueries.class);

	private static String selectQuery = null;
	private static String insertHeaderQuery = null;
	private static String insertQuery = null;

	private static String selectMovementListQuery = null;
	private static String selectMovementQuery = null;
	private static String insertMovement = null;
	private static String insertLogMovement = null;
	private static String updateMovementFlag = null;

	private DisbursementRequestsQueries() {
		super();
	}

	public static String getSelectAllQuery(DisbursementRequest requestData) {

		StringBuilder sql = new StringBuilder();
		sql.append("SELECT PAYMENTID DISBURSEMENT_ID, CUSTCIF, FINREFERENCE, AMTTOBERELEASED DISBURSEMENT_AMOUNT");
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
		sql.append(", ALWFILEDOWNLOAD ALW_FILE_DOWNLOAD, CHEQUE_NUMBER");
		sql.append(" FROM INT_DISBURSEMENT_REQUEST_VIEW ");
		sql.append(" WHERE PAYMENTID IN (SELECT PAYMENTID FROM DISBURSEMENT_REQUESTS_HEADER WHERE ID=(:HEADER_ID))");

		logger.debug(Literal.SQL + sql.toString());
		return sql.toString();
	}

	public static String getSelectQuery() {
		if (selectQuery != null) {
			return selectQuery;
		}

		StringBuilder sql = new StringBuilder();
		sql = new StringBuilder();
		sql.append("SELECT PAYMENTID, PAYMENTTYPE, FA.PARTNERBANKID, PARTNERBANKCODE, ALWFILEDOWNLOAD, CHANNEL FROM (");
		sql.append(" SELECT PAYMENTID, PAYMENTTYPE, PARTNERBANKID, STATUS, 'D' CHANNEL");
		sql.append(" FROM FINADVANCEPAYMENTS");// Disbursements
		sql.append(" UNION ALL");
		sql.append(" SELECT PAYMENTINSTRUCTIONID PAYMENTID, PAYMENTTYPE, PARTNERBANKID, STATUS, 'P' CHANNEL");
		sql.append(" FROM PAYMENTINSTRUCTIONS"); // Payments
		sql.append(" ) FA");
		sql.append(" INNER JOIN PARTNERBANKS PB ON PB.PARTNERBANKID = FA.PARTNERBANKID");
		sql.append(" WHERE PAYMENTID IN (SELECT PAYMENTID FROM DISBURSEMENT_REQUESTS_HEADER WHERE ID = :HEADER_ID)");
		sql.append(" AND FA.STATUS = :APPROVED");
		selectQuery = sql.toString();

		return selectQuery;
	}

	public static String getInsertQuery() {
		if (insertQuery != null) {
			return insertQuery;
		}

		StringBuilder sql = new StringBuilder();
		sql.append("INSERT INTO DISBURSEMENT_REQUESTS (");
		sql.append(" DISBURSEMENT_ID, CUSTCIF, FINREFERENCE, DISBURSEMENT_AMOUNT, DISBURSEMENT_TYPE");
		sql.append(", DISBURSEMENT_DATE, DRAWEE_LOCATION, PRINT_LOCATION, CUSTOMER_NAME, CUSTOMER_MOBILE");
		sql.append(", CUSTOMER_EMAIL, CUSTOMER_STATE, CUSTOMER_CITY, CUSTOMER_ADDRESS1, CUSTOMER_ADDRESS2");
		sql.append(", CUSTOMER_ADDRESS3, CUSTOMER_ADDRESS4, CUSTOMER_ADDRESS5, BENFICIARY_BANK, BENFICIARY_BRANCH");
		sql.append(", BENFICIARY_BRANCH_STATE, BENFICIARY_BRANCH_CITY, MICR_CODE, IFSC_CODE");
		sql.append(", BENFICIARY_ACCOUNT, BENFICIARY_NAME, BENFICIARY_MOBILE, BENFICIRY_EMAIL, BENFICIRY_STATE");
		sql.append(", BENFICIRY_CITY, BENFICIARY_ADDRESS1, BENFICIARY_ADDRESS2, BENFICIARY_ADDRESS3");
		sql.append(", BENFICIARY_ADDRESS4, BENFICIARY_ADDRESS5, PAYMENT_DETAIL1, PAYMENT_DETAIL2, PAYMENT_DETAIL3");
		sql.append(", PAYMENT_DETAIL4, PAYMENT_DETAIL5, PAYMENT_DETAIL6, PAYMENT_DETAIL7, STATUS, REMARKS");
		sql.append(", CHANNEL, BATCH_ID, AUTO_DOWNLOAD, HEADER_ID");
		sql.append(", PARTNERBANK_ID, PARTNERBANK_CODE, PARTNERBANK_ACCOUNT, CHEQUE_NUMBER)");
		sql.append(" Values (");
		sql.append(" :DisbursementId, :CustCIF, :FinReference, :DisbursementAmount, :DisbursementType");
		sql.append(", :DisbursementDate, :DraweeLocation, :PrintLocation, :CustomerName, :CustomerMobile");
		sql.append(", :CustomerEmail, :CustomerState, :CustomerCity, :CustomerAddress1, :CustomerAddress2");
		sql.append(", :CustomerAddress3, :CustomerAddress4, :CustomerAddress5, :BenficiaryBank, :BenficiaryBranch");
		sql.append(", :BenficiaryBranchState, :BenficiaryBranchCity, :MicrCode, :IfscCode");
		sql.append(", :BenficiaryAccount, :BenficiaryName, :BenficiaryMobile, :BenficiryEmail, :BenficiryState");
		sql.append(", :BenficiryCity, :BenficiaryAddress1, :BenficiaryAddress2, :BenficiaryAddress3");
		sql.append(", :BenficiaryAddress4, :BenficiaryAddress5, :PaymentDetail1, :PaymentDetail2, :PaymentDetail3");
		sql.append(", :PaymentDetail4, :PaymentDetail5, :PaymentDetail6, :PaymentDetail7, :Status, :Remarks");
		sql.append(", :Channel, :HeaderId, :AutoDownload, :HeaderId");
		sql.append(", :PartnerBankId, :PartnerBankCode, :PartnerBankAccount, :ChequeNumber)");

		insertQuery = sql.toString();
		logger.trace(Literal.SQL + insertQuery.toString());
		return insertQuery;
	}

	public static String getInsertHeaderQuery() {
		if (insertHeaderQuery != null) {
			return insertHeaderQuery;
		}

		StringBuilder sql = new StringBuilder();
		sql.append("INSERT INTO DISBURSEMENT_REQUESTS_HEADER");
		sql.append(" SELECT :HEADER_ID, CHANNEL, PAYMENTID, :CREATEDBY, :CREATEDON FROM (");
		sql.append(" SELECT  PAYMENTID, 'D' CHANNEL, STATUS FROM FINADVANCEPAYMENTS");
		sql.append(" UNION ALL");
		sql.append(" SELECT  PAYMENTINSTRUCTIONID PAYMENTID, 'P' CHANNEL, STATUS FROM PAYMENTINSTRUCTIONS");
		sql.append(" UNION ALL");
		sql.append(" SELECT  ID PAYMENTID, 'I' CHANNEL, STATUS FROM INSURANCEPAYMENTINSTRUCTIONS) T");
		sql.append(" WHERE T.PAYMENTID IN (:PAYMENTID) AND T.STATUS = :APPROVED");
		sql.append(" AND T.PAYMENTID NOT IN (SELECT PAYMENTID FROM DISBURSEMENT_REQUESTS_HEADER)");

		insertHeaderQuery = sql.toString();
		return insertHeaderQuery;

	}

	public static String getInsertMovement() {
		if (insertMovement != null) {
			return insertMovement;
		}

		StringBuilder sql = new StringBuilder("INSERT INTO DISBURSEMENT_MOVEMENTS");
		sql.append("(HEADER_ID, BATCH_ID, TARGET_TYPE, FILE_NAME, FILE_LOCATION");
		sql.append(", DATA_ENGINE_CONFIG, POST_EVENTS, CREATED_ON, CREATED_BY, PROCESSED_ON, FAILURE_REASON)");
		sql.append(" VALUES");
		sql.append("(:HEADER_ID, :BATCH_ID, :TARGET_TYPE, :FILE_NAME, :FILE_LOCATION");
		sql.append(", :DATA_ENGINE_CONFIG, :POST_EVENTS, :CREATED_ON, :CREATED_BY, :PROCESSED_ON, :FAILURE_REASON)");

		insertMovement = sql.toString();
		return insertMovement;

	}

	public static String getInsertLogMovement() {
		if (insertLogMovement != null) {
			return insertLogMovement;
		}

		StringBuilder sql = new StringBuilder("INSERT INTO DISBURSEMENT_MOVEMENTS_LOG");
		sql.append("(HEADER_ID, BATCH_ID, TARGET_TYPE, FILE_NAME, FILE_LOCATION");
		sql.append(", DATA_ENGINE_CONFIG, POST_EVENTS, CREATED_ON, CREATED_BY, PROCESS_FLAG");
		sql.append(", PROCESSED_ON, FAILURE_REASON)");
		sql.append(" VALUES (");
		sql.append(":HEADER_ID, :BATCH_ID, :TARGET_TYPE, :FILE_NAME, :FILE_LOCATION");
		sql.append(", :DATA_ENGINE_CONFIG, :POST_EVENTS, :CREATED_ON, :CREATED_BY, :PROCESS_FLAG");
		sql.append(", :PROCESSED_ON, :FAILURE_REASON)");

		insertLogMovement = sql.toString();
		return insertLogMovement;

	}

	public static String getMovementListQuery() {
		if (selectMovementListQuery != null) {
			return selectMovementListQuery;
		}

		selectMovementListQuery = "SELECT ID FROM DISBURSEMENT_MOVEMENTS WHERE PROCESS_FLAG = :PROCESS_FLAG";

		return selectMovementListQuery;
	}

	public static String getUpdateMovementFlag() {
		if (updateMovementFlag != null) {
			return updateMovementFlag;
		}

		StringBuilder sql = new StringBuilder();
		sql.append("UPDATE DISBURSEMENT_MOVEMENTS SET");
		sql.append(" PROCESS_FLAG = :PROCESS_FLAG");
		sql.append(", FAILURE_REASON = :FAILURE_REASON");
		sql.append(", PROCESSED_ON = :PROCESSED_ON");
		sql.append(" WHERE ID = :ID");

		updateMovementFlag = sql.toString();

		return updateMovementFlag;
	}

	public static String getMovementQuery() {
		if (selectMovementQuery != null) {
			return selectMovementQuery;
		}

		selectMovementQuery = "SELECT * FROM DISBURSEMENT_MOVEMENTS WHERE ID = :ID";

		return selectMovementQuery;
	}

}
