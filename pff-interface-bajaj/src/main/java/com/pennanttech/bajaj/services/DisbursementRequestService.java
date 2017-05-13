package com.pennanttech.bajaj.services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import com.pennant.backend.model.finance.FinAdvancePayments;
import com.pennanttech.dataengine.DataEngineExport;
import com.pennanttech.dbengine.process.IMPSDisbursementRequest;
import com.pennanttech.pff.core.App;
import com.pennanttech.pff.core.Literal;
import com.pennanttech.pff.core.services.RequestService;

public class DisbursementRequestService extends BajajService implements RequestService {
	private final Logger	logger	= Logger.getLogger(getClass());

	public enum DisbursementTypes {
		IMPS, RTGS, NEFT, DD, CHEQUE, I;
	}

	public DisbursementRequestService() {
		super();
	}

	@SuppressWarnings("unchecked")
	@Override
	public void sendReqest(Object... params) throws Exception {
		String finType = (String) params[0];
		List<FinAdvancePayments> disbusments = (List<FinAdvancePayments>) params[1];
		long userId = (long) params[2];

		DisbursementProcessThread process = new DisbursementProcessThread(finType, userId, disbusments);
		Thread thread = new Thread(process);
		try {
			DisbursementProcessThread.sleep(5000);
		} catch (InterruptedException e) {
			logger.error(Literal.EXCEPTION, e);
		}

		thread.start();
	}

	private void processDisbursements(String finType, long userId, List<FinAdvancePayments> disbursements)
			throws Exception {
		logger.debug(Literal.ENTERING);

		StringBuilder list = new StringBuilder();

		for (FinAdvancePayments fa : disbursements) {
			if (list.length() > 0) {
				list.append(",");
			}
			list.append(fa.getPaymentId());
		}

		int count = prepareRequest(list.toString().split(","));

		if (count == 0) {
			return;
		}

		generateRequest(finType, userId, disbursements);

		logger.debug(Literal.LEAVING);
	}

	private void generateRequest(String finType, long userId, List<FinAdvancePayments> disbusments) {
		List<FinAdvancePayments> stp_IMPS = new ArrayList<>();
		List<FinAdvancePayments> other_IMPS = new ArrayList<>();

		List<FinAdvancePayments> stp_RTGS = new ArrayList<>();
		List<FinAdvancePayments> other_RTGS = new ArrayList<>();

		List<FinAdvancePayments> stp_NEFT = new ArrayList<>();
		List<FinAdvancePayments> other_NEFT = new ArrayList<>();

		List<FinAdvancePayments> stp_DD = new ArrayList<>();
		List<FinAdvancePayments> other_DD = new ArrayList<>();

		List<FinAdvancePayments> stp_CHEQUE = new ArrayList<>();
		List<FinAdvancePayments> other_CHEQUE = new ArrayList<>();

		List<FinAdvancePayments> stp_Other = new ArrayList<>();
		List<FinAdvancePayments> other_Other = new ArrayList<>();

		for (FinAdvancePayments disbursment : disbusments) {
			DisbursementTypes type = DisbursementTypes.valueOf(disbursment.getPaymentType());

			switch (type) {
			case IMPS:
				if (disbursment.isAlwFileDownload()) {
					stp_IMPS.add(disbursment);
				} else {
					other_IMPS.add(disbursment);
				}
				break;
			case NEFT:
				if (disbursment.isAlwFileDownload()) {
					stp_NEFT.add(disbursment);
				} else {
					other_NEFT.add(disbursment);
				}
				break;
			case RTGS:
				if (disbursment.isAlwFileDownload()) {
					stp_RTGS.add(disbursment);
				} else {
					other_RTGS.add(disbursment);
				}
				break;
			case DD:
				if (disbursment.isAlwFileDownload()) {
					stp_DD.add(disbursment);
				} else {
					other_DD.add(disbursment);
				}
				break;
			case CHEQUE:
				if (disbursment.isAlwFileDownload()) {
					stp_CHEQUE.add(disbursment);
				} else {
					other_CHEQUE.add(disbursment);
				}
				break;
			default:
				if (disbursment.isAlwFileDownload()) {
					stp_Other.add(disbursment);
				} else {
					other_Other.add(disbursment);
				}
				break;
			}
		}

		if (!stp_IMPS.isEmpty()) {
			sendIMPSRequest("DISB_IMPS_EXPORT", getPaymentIds(stp_IMPS), userId);
		}

		if (!other_IMPS.isEmpty()) {
			sendIMPSRequest("DISB_IMPS_EXPORT", getPaymentIds(other_IMPS), userId);
		}

		generateFile("DISB_HDFC_EXPORT", DisbursementTypes.NEFT.name(), finType, userId, stp_NEFT);
		generateFile("DISB_HDFC_EXPORT", DisbursementTypes.RTGS.name(), finType, userId, stp_RTGS);
		generateFile("DISB_HDFC_EXPORT", DisbursementTypes.CHEQUE.name(), finType, userId, stp_CHEQUE);
		generateFile("DISB_HDFC_EXPORT", DisbursementTypes.DD.name(), finType, userId, stp_DD);
		generateFile("DISB_HDFC_EXPORT", DisbursementTypes.I.name(), finType, userId, stp_Other);

		generateFile("DISB_OTHER_NEFT_RTGS_EXPORT", DisbursementTypes.NEFT.name(), finType, userId, other_NEFT);
		generateFile("DISB_OTHER_NEFT_RTGS_EXPORT", DisbursementTypes.RTGS.name(), finType, userId, other_RTGS);
		generateFile("DISB_OTHER_CHEQUE_DD_EXPORT", DisbursementTypes.DD.name(), finType, userId, other_DD);
		generateFile("DISB_OTHER_CHEQUE_DD_EXPORT", DisbursementTypes.CHEQUE.name(), finType, userId, other_CHEQUE);
		generateFile("DISB_OTHER_NEFT_RTGS_EXPORT", DisbursementTypes.I.name(), finType, userId, other_Other);

	}

	private void generateFile(String configName, String paymentType, String finType, long userId,
			List<FinAdvancePayments> disbusments) {
		Map<String, List<FinAdvancePayments>> map = null;
		if (!disbusments.isEmpty()) {
			map = getOtherBankMap(disbusments);

			List<String> parnerBanks = new ArrayList<String>(map.keySet());

			for (String bank : parnerBanks) {
				generateFile(configName, getPaymentIds(map.get(bank)), paymentType, bank, finType, userId);
			}
		}
	}

	private Map<String, List<FinAdvancePayments>> getOtherBankMap(List<FinAdvancePayments> disbursemens) {
		Map<String, List<FinAdvancePayments>> map = new HashMap<>();

		for (FinAdvancePayments disbursement : disbursemens) {
			if (map.get(disbursement.getPartnerbankCode()) == null) {
				map.put(disbursement.getPartnerbankCode(), new ArrayList<FinAdvancePayments>());
			}

			map.get(disbursement.getPartnerbankCode()).add(disbursement);
		}
		return map;
	}

	private String getPaymentIds(List<FinAdvancePayments> finAdvancePayments) {
		StringBuilder builder = new StringBuilder();

		for (FinAdvancePayments disbursement : finAdvancePayments) {

			if (builder.length() > 0) {
				builder.append(",");
			}
			builder.append(disbursement.getPaymentId());

		}

		return builder.toString();
	}

	private void generateFile(String configName, String paymentIds, String paymentType, String partnerbankCode,
			String finType, long userId) {
		DataEngineExport export = new DataEngineExport(dataSource, userId, App.DATABASE.name());

		Map<String, Object> filterMap = new HashMap<>();
		filterMap.put("PAYMENTID", paymentIds);
		filterMap.put("STATUS", "APPROVED");

		Map<String, Object> parameterMap = new HashMap<>();
		parameterMap.put("PRODUCT_CODE", StringUtils.trimToEmpty(finType));
		parameterMap.put("PAYMENT_TYPE", paymentType);
		parameterMap.put("PARTNER_BANK_CODE", partnerbankCode);

		try {
			export.setValueDate(getAppDate());
			export.setFilterMap(filterMap);
			export.setParameterMap(parameterMap);
			export.exportData(configName);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		} finally {
			export = null;
			parameterMap = null;
		}
	}

	private void sendIMPSRequest(String configName, String paymentIds, long userId) {
		IMPSDisbursementRequest impsRequest = new IMPSDisbursementRequest(dataSource, App.DATABASE.name());

		impsRequest.setPaymentIds(paymentIds);
		impsRequest.process(userId, configName);

		try {
			impsRequest.process(userId, configName);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}

	}

	private int prepareRequest(String[] disbursments) throws Exception {
		logger.debug(Literal.ENTERING);
		MapSqlParameterSource paramMap;

		StringBuilder sql = new StringBuilder();
		sql.append(" INSERT INTO DISBURSEMENT_REQUESTS SELECT");
		sql.append(" SEQ_DISBURSEMENT_REQUESTS.NEXTVAL,");
		sql.append(" :BATCH_ID,");
		sql.append(" PAYMENTID,");
		sql.append(" CUSTCIF,");
		sql.append(" FINREFERENCE,");
		sql.append(" AMTTOBERELEASED,");
		sql.append(" DISBURSEMENT_TYPE,");
		sql.append(" DISBDATE,");
		sql.append(" PAYABLELOC,");
		sql.append(" PRINTINGLOC,");
		sql.append(" CUSTSHRTNAME,");
		sql.append(" CUSTOMER_MOBILE,");
		sql.append(" CUSTOMER_EMAIL,");
		sql.append(" CUSTOMER_STATE,");
		sql.append(" CUSTOMER_CITY,");
		sql.append(" CUSTOMER_ADDRESS1,");
		sql.append(" CUSTOMER_ADDRESS2,");
		sql.append(" CUSTOMER_ADDRESS3,");
		sql.append(" CUSTOMER_ADDRESS4,");
		sql.append(" CUSTOMER_ADDRESS5,");
		sql.append(" BANKNAME,");
		sql.append(" BRANCHDESC,");
		sql.append(" BENFICIARY_BRANCH_STATE,");
		sql.append(" BENFICIARY_BRANCH_CITY,");
		sql.append(" MICR_CODE,");
		sql.append(" IFSC_CODE,");
		sql.append(" BENEFICIARYACCNO,");
		sql.append(" BENEFICIARYNAME,");
		sql.append(" BENEFICIARY_MOBILE,");
		sql.append(" BENFICIRY_EMAIL,");
		sql.append(" BENFICIARY_STATE,");
		sql.append(" BENFICIARY_CITY,");
		sql.append(" BENFICIARY_ADDRESS1,");
		sql.append(" BENFICIARY_ADDRESS2,");
		sql.append(" BENFICIARY_ADDRESS3,");
		sql.append(" BENFICIARY_ADDRESS4,");
		sql.append(" BENFICIARY_ADDRESS5,");
		sql.append(" :PAYMENT_DETAIL1,");
		sql.append(" PAYMENT_DETAIL2,");
		sql.append(" PAYMENT_DETAIL3,");
		sql.append(" PAYMENT_DETAIL4,");
		sql.append(" PAYMENT_DETAIL5,");
		sql.append(" PAYMENT_DETAIL6,");
		sql.append(" PAYMENT_DETAIL7,");
		sql.append(" :RESP_BATCH_ID,");
		sql.append(" :TRANSACTIONREF,");
		sql.append(" :CHEQUE_NUMBER,");
		sql.append(" :DD_CHEQUE_CHARGE,");
		sql.append(" :PAYMENT_DATE,");
		sql.append(" STATUS,");
		sql.append(" REMARKS,");
		sql.append(" :REJECT_REASON");
		sql.append(" FROM INT_DISBURSEMENT_REQUEST_VIEW ");
		sql.append(" WHERE PAYMENTID IN (:PAYMENTID)");

		paramMap = new MapSqlParameterSource();
		paramMap.addValue("BATCH_ID", 0);
		paramMap.addValue("PAYMENT_DETAIL1", getSMTParameter("DISB_FI_EMAIL", String.class));
		paramMap.addValue("RESP_BATCH_ID", 0);
		paramMap.addValue("TRANSACTIONREF", null);
		paramMap.addValue("CHEQUE_NUMBER", null);
		paramMap.addValue("DD_CHEQUE_CHARGE", null);
		paramMap.addValue("PAYMENT_DATE", null);
		paramMap.addValue("REJECT_REASON", null);
		paramMap.addValue("PAYMENTID", Arrays.asList(disbursments));

		try {
			return namedJdbcTemplate.update(sql.toString(), paramMap);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		} finally {
			paramMap = null;
			sql = null;
		}

		logger.debug(Literal.ENTERING);

		return 0;
	}

	public class DisbursementProcessThread extends Thread {
		private final Logger				logger	= Logger.getLogger(DisbursementProcessThread.class);

		private String						finType;
		private long						userId;
		private List<FinAdvancePayments>	disbursements;

		public DisbursementProcessThread(String finType, long userId, List<FinAdvancePayments> disbursements) {
			this.finType = finType;
			this.userId = userId;
			this.disbursements = disbursements;
		}

		@Override
		public void run() {
			try {
				processDisbursements(finType, userId, disbursements);
			} catch (Exception e) {
				logger.error(Literal.EXCEPTION, e);
			}
		}
	}
}
