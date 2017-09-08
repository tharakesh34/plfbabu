package com.pennanttech.bajaj.services;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import com.pennant.backend.model.finance.FinAdvancePayments;
import com.pennanttech.bajaj.process.DisbursemenIMPSRequestProcess;
import com.pennanttech.dataengine.DataEngineExport;
import com.pennanttech.dataengine.model.DataEngineStatus;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.App;
import com.pennanttech.pff.core.services.DisbursementRequestService;
import com.pennanttech.pff.core.util.DateUtil;
import com.pennanttech.pff.core.util.QueryUtil;

public class DisbursementRequestServiceImpl extends BajajService implements DisbursementRequestService {
	private final Logger	logger	= Logger.getLogger(getClass());

	public enum DisbursementTypes {
		IMPS, RTGS, NEFT, DD, CHEQUE, I;
	}

	public DisbursementRequestServiceImpl() {
		super();
	}

	@SuppressWarnings("unchecked")
	@Override
	public void sendReqest(Object... params) throws Exception {
		String finType = (String) params[0];
		List<FinAdvancePayments> disbusments = (List<FinAdvancePayments>) params[1];
		long userId = (long) params[2];
		String fileNamePrefix = (String) params[3];

		DisbursementProcessThread process = new DisbursementProcessThread(finType, userId, disbusments, fileNamePrefix);
		Thread thread = new Thread(process);
		try {
			DisbursementProcessThread.sleep(5000);
		} catch (InterruptedException e) {
			logger.error(Literal.EXCEPTION, e);
		}
		thread.start();
	}
	
	public class DisbursementProcessThread extends Thread {
		private final Logger				logger	= Logger.getLogger(DisbursementProcessThread.class);

		private String						finType;
		private String						fileNamePrefix;
		private long						userId;
		private List<FinAdvancePayments>	disbursements;

		public DisbursementProcessThread(String finType, long userId, List<FinAdvancePayments> disbursements, String fileNamePrefix) {
			this.finType = finType;
			this.userId = userId;
			this.disbursements = disbursements;
			this.fileNamePrefix = fileNamePrefix;
		}

		@Override
		public void run() {
			try {
				processDisbursements(finType, userId, disbursements, fileNamePrefix);
			} catch (Exception e) {
				logger.error(Literal.EXCEPTION, e);
			}
		}
	}
	
	private void processDisbursements(String finType, long userId, List<FinAdvancePayments> disbursements, String fileNamePrefix) throws Exception {
		logger.debug(Literal.ENTERING);

		StringBuilder list = new StringBuilder();

		for (FinAdvancePayments fa : disbursements) {
			if (list.length() > 0) {
				list.append(",");
			}
			list.append(fa.getPaymentId());
		}

		generateRequest(finType, userId, disbursements, fileNamePrefix);

		logger.debug(Literal.LEAVING);
	}

	private void generateRequest(String finType, long userId, List<FinAdvancePayments> disbusments, String fileNamePrefix) {
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
		
		DisbursementTypes type = null;

		for (FinAdvancePayments disbursment : disbusments) {
			type = DisbursementTypes.valueOf(disbursment.getPaymentType());

			switch (type) {
			case IMPS:
				if (!disbursment.isAlwFileDownload()) {
					stp_IMPS.add(disbursment);
				} else {
					other_IMPS.add(disbursment);
				}
				break;
			case NEFT:
				if (!disbursment.isAlwFileDownload()) {
					stp_NEFT.add(disbursment);
				} else {
					other_NEFT.add(disbursment);
				}
				break;
			case RTGS:
				if (!disbursment.isAlwFileDownload()) {
					stp_RTGS.add(disbursment);
				} else {
					other_RTGS.add(disbursment);
				}
				break;
			case DD:
				if (!disbursment.isAlwFileDownload()) {
					stp_DD.add(disbursment);
				} else {
					other_DD.add(disbursment);
				}
				break;
			case CHEQUE:
				if (!disbursment.isAlwFileDownload()) {
					stp_CHEQUE.add(disbursment);
				} else {
					other_CHEQUE.add(disbursment);
				}
				break;
			default:
				if (!disbursment.isAlwFileDownload()) {
					stp_Other.add(disbursment);
				} else {
					other_Other.add(disbursment);
				}
				break;
			}
		}

		if (!stp_IMPS.isEmpty()) {
			List<String> idList = null;
			try {
				idList = prepareRequest(getPaymentIds(stp_IMPS).split(","), type.name());
			} catch (Exception e) {
				logger.error(Literal.EXCEPTION, e);
			}
			
			if (idList != null && !idList.isEmpty()) {
				List<String> ids = new ArrayList<>();
				for (String id : idList) {
					if (StringUtils.trimToNull(id) != null) {
						ids.add(id);
					}
				}
				if (ids != null && !ids.isEmpty()) {
					sendIMPSRequest("DISB_IMPS_EXPORT", ids, userId);
				}
			}
		}

		if (!other_IMPS.isEmpty()) {
			List<String> idList = null;
			try {
				idList = prepareRequest(getPaymentIds(other_IMPS).split(","), type.name());
			} catch (Exception e) {
				logger.error(Literal.EXCEPTION, e);
			}
			
			if (idList != null && !idList.isEmpty()) {
				List<String> ids = new ArrayList<>();
				for (String id : idList) {
					if (StringUtils.trimToNull(id) != null) {
						ids.add(id);
					}
				}
				if (ids != null && !ids.isEmpty()) {
					sendIMPSRequest("DISB_IMPS_EXPORT", ids, userId);
				}
			}
		}

		generateFile("DISB_HDFC_EXPORT", DisbursementTypes.NEFT.name(), finType, userId, stp_NEFT, fileNamePrefix);
		generateFile("DISB_HDFC_EXPORT", DisbursementTypes.RTGS.name(), finType, userId, stp_RTGS, fileNamePrefix);
		generateFile("DISB_HDFC_EXPORT", DisbursementTypes.CHEQUE.name(), finType, userId, stp_CHEQUE, fileNamePrefix);
		generateFile("DISB_HDFC_EXPORT", DisbursementTypes.DD.name(), finType, userId, stp_DD, fileNamePrefix);
		generateFile("DISB_HDFC_EXPORT", DisbursementTypes.I.name(), finType, userId, stp_Other, fileNamePrefix);

		generateFile("DISB_OTHER_NEFT_RTGS_EXPORT", DisbursementTypes.NEFT.name(), finType, userId, other_NEFT, null);
		generateFile("DISB_OTHER_NEFT_RTGS_EXPORT", DisbursementTypes.RTGS.name(), finType, userId, other_RTGS, null);
		generateFile("DISB_OTHER_CHEQUE_DD_EXPORT", DisbursementTypes.DD.name(), finType, userId, other_DD, null);
		generateFile("DISB_OTHER_CHEQUE_DD_EXPORT", DisbursementTypes.CHEQUE.name(), finType, userId, other_CHEQUE, null);
		generateFile("DISB_OTHER_NEFT_RTGS_EXPORT", DisbursementTypes.I.name(), finType, userId, other_Other, null);

	}

	private void generateFile(String configName, String paymentType, String finType, long userId,
			List<FinAdvancePayments> disbusments, String fileNamePrefix) {
		Map<String, List<FinAdvancePayments>> map = null;
		if (!disbusments.isEmpty()) {
			map = getOtherBankMap(disbusments);

			List<String> parnerBanks = new ArrayList<String>(map.keySet());

			for (String bank : parnerBanks) {
				List<String> idList = null;
				try {
					idList = prepareRequest(getPaymentIds(map.get(bank)).split(","), paymentType);
				} catch (Exception e) {
					logger.error(Literal.EXCEPTION, e);
				}
				generateFile(configName, idList, paymentType, bank, finType, userId, fileNamePrefix);
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

	private void generateFile(String configName, List<String> idList, String paymentType, String partnerbankCode,
			String finType, long userId, String fileNamePrefix) {
		DataEngineStatus status = null;
		DataEngineExport export = new DataEngineExport(dataSource, userId, App.DATABASE.name(), true, getValueDate());

		Map<String, Object> filterMap = new HashMap<>();
		filterMap.put("ID", idList);
		filterMap.put("STATUS", "APPROVED");

		Map<String, Object> parameterMap = new HashMap<>();
		parameterMap.put("PRODUCT_CODE", StringUtils.trimToEmpty(finType));
		parameterMap.put("PAYMENT_TYPE", paymentType);
		parameterMap.put("PARTNER_BANK_CODE", partnerbankCode);

		if ("DISB_HDFC_EXPORT".equals(configName)) {
			parameterMap.put("CLIENT_CODE", fileNamePrefix);
			parameterMap.put("SEQ_DATE_FILE", StringUtils.leftPad(getSTPFileSequence(), 3, "0"));
		}
		try {
			export.setValueDate(getValueDate());
			export.setFilterMap(filterMap);
			export.setParameterMap(parameterMap);
			status = export.exportData(configName);
			
			if (!"S".equals(status.getStatus())) {
				System.out.println("Failed");
			}
			
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		} finally {
			conclude(status, idList);
		}
	}
		
	private String getSTPFileSequence() {
		MapSqlParameterSource parmMap = new MapSqlParameterSource();
		StringBuilder sql = new StringBuilder();
		
		Date date = DateUtil.getSysDate();

		sql.append(" select COALESCE(FILESEQUENCENO, 0) +1 from DATA_ENGINE_CONFIG");
		sql.append(" where Name = :Name");
		sql.append(" and LASTPROCESSEDON >= :TODAY and LASTPROCESSEDON <= :NEXTDAY");

		parmMap.addValue("Name", "DISB_HDFC_EXPORT");
		parmMap.addValue("TODAY", DateUtil.getDatePart(date));
		parmMap.addValue("NEXTDAY", DateUtil.getDatePart(DateUtil.addDays(date, 1)));
		
		try {
			return namedJdbcTemplate.queryForObject(sql.toString(), parmMap, String.class);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}

		return "1";
	}

	private void sendIMPSRequest(String configName, List<String> dibursements, long userId) {
		DisbursemenIMPSRequestProcess impsRequest = new DisbursemenIMPSRequestProcess(dataSource, userId,getValueDate(),getAppDate());

		impsRequest.setDisbursments(dibursements);
		try {
			impsRequest.process(configName);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}

	}

	private List<String> prepareRequest(String[] disbursments, final String type) throws Exception {
		logger.debug(Literal.ENTERING);
		MapSqlParameterSource paramMap;

		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT");
		sql.append(" PAYMENTID DISBURSEMENT_ID,");
		sql.append(" CUSTCIF,");
		sql.append(" FINREFERENCE,");
		sql.append(" AMTTOBERELEASED DISBURSEMENT_AMOUNT,");
		sql.append(" DISBURSEMENT_TYPE,");
		sql.append(" LLDATE DISBURSEMENT_DATE,");
		sql.append(" PAYABLELOC DRAWEE_LOCATION,");
		sql.append(" PRINTINGLOC PRINT_LOCATION,");
		sql.append(" CUSTSHRTNAME CUSTOMER_NAME,");
		sql.append(" CUSTOMER_MOBILE,");
		sql.append(" CUSTOMER_EMAIL,");
		sql.append(" CUSTOMER_STATE,");
		sql.append(" CUSTOMER_CITY,");
		sql.append(" CUSTOMER_ADDRESS1,");
		sql.append(" CUSTOMER_ADDRESS2,");
		sql.append(" CUSTOMER_ADDRESS3,");
		sql.append(" CUSTOMER_ADDRESS4,");
		sql.append(" CUSTOMER_ADDRESS5,");
		sql.append(" BANKNAME BENFICIARY_BANK,");
		sql.append(" BRANCHDESC BENFICIARY_BRANCH,");
		sql.append(" BENFICIARY_BRANCH_STATE,");
		sql.append(" BENFICIARY_BRANCH_CITY,");
		sql.append(" MICR_CODE,");
		sql.append(" IFSC_CODE,");
		sql.append(" BENEFICIARYACCNO BENFICIARY_ACCOUNT,");
		sql.append(" BENEFICIARYNAME BENFICIARY_NAME,");
		sql.append(" BENEFICIARY_MOBILE BENFICIARY_MOBILE,");
		sql.append(" BENFICIRY_EMAIL,");
		sql.append(" BENFICIARY_STATE BENFICIRY_STATE,");
		sql.append(" BENFICIARY_CITY BENFICIRY_CITY,");
		sql.append(" BENFICIARY_ADDRESS1,");
		sql.append(" BENFICIARY_ADDRESS2,");
		sql.append(" BENFICIARY_ADDRESS3,");
		sql.append(" BENFICIARY_ADDRESS4,");
		sql.append(" BENFICIARY_ADDRESS5,");
		sql.append(" PAYMENT_DETAIL1,");
		sql.append(" PAYMENT_DETAIL2,");
		sql.append(" PAYMENT_DETAIL3,");
		sql.append(" PAYMENT_DETAIL4,");
		sql.append(" PAYMENT_DETAIL5,");
		sql.append(" PAYMENT_DETAIL6,");
		sql.append(" PAYMENT_DETAIL7,");
		sql.append(" STATUS,");
		sql.append(" REMARKS,");
		sql.append(" CHANNEL");
		sql.append(" FROM INT_DISBURSEMENT_REQUEST_VIEW ");
		sql.append(" WHERE PAYMENTID IN (:PAYMENTID)");

		paramMap = new MapSqlParameterSource();
		paramMap.addValue("PAYMENTID", Arrays.asList(disbursments));

		final String DISB_FI_EMAIL = (String) getSMTParameter("DISB_FI_EMAIL", String.class);
		final ColumnMapRowMapper rowMapper = new ColumnMapRowMapper();
		try {
			return namedJdbcTemplate.query(sql.toString(), paramMap, new RowMapper<String>() {
				@Override
				public String mapRow(ResultSet rs, int rowNum) throws SQLException {
					String id = null;
					Map<String, Object> rowMap = rowMapper.mapRow(rs, rowNum);
					rowMap.put("BATCH_ID", 0);
					rowMap.put("PAYMENT_DETAIL1", DISB_FI_EMAIL);
					rowMap.put("RESP_BATCH_ID", 0);
					rowMap.put("TRANSACTIONREF", null);
					rowMap.put("CHEQUE_NUMBER", null);
					rowMap.put("DD_CHEQUE_CHARGE", null);
					rowMap.put("PAYMENT_DATE", null);
					rowMap.put("REJECT_REASON", null);

					if (DisbursementTypes.IMPS.name().equals(type)) {
						try {
							//validateImpsRequest(rs);
						} catch (Exception e) {
							logger.error(Literal.EXCEPTION, e);
							return null;
						}
					}
					id = String.valueOf(insertData(rowMap));
					rowMap = null;
					return id;
				}
			});
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		} finally {
			paramMap = null;
			sql = null;
		}

		logger.debug(Literal.ENTERING);
		return null;
	}

	private long insertData(Map<String, Object> rowMap) {
		String sql = QueryUtil.getInsertQuery(rowMap.keySet(), "DISBURSEMENT_REQUESTS");
		final KeyHolder keyHolder = new GeneratedKeyHolder();
		try {
			namedJdbcTemplate.update(sql, getMapSqlParameterSource(rowMap), keyHolder, new String[] { "ID" });
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}
		return keyHolder.getKey().longValue();
	}
	
	private void conclude(DataEngineStatus status, List<String> idList) {
		if (status == null || !"S".equals(status.getStatus())) {
			MapSqlParameterSource paramMap = new MapSqlParameterSource();
			paramMap.addValue("ID", idList);
			paramMap.addValue("STATUS", "APPROVED");
			
			namedJdbcTemplate.update("UPDATE FINADVANCEPAYMENTS SET STATUS = :STATUS where PAYMENTID IN (select DISBURSEMENT_ID from DISBURSEMENT_REQUESTS where ID IN(:ID))", paramMap);
			namedJdbcTemplate.update("UPDATE PAYMENTINSTRUCTIONS SET STATUS = :STATUS where PAYMENTINSTRUCTIONID IN (select DISBURSEMENT_ID from DISBURSEMENT_REQUESTS where ID IN(:ID))", paramMap);
			namedJdbcTemplate.update("delete from DISBURSEMENT_REQUESTS where ID IN(:ID)", paramMap);
		}
	}

	
	private boolean validateImpsRequest(ResultSet rs) throws Exception {

		String mobileNo = rs.getString("BENFICIARY_MOBILE");
		if (StringUtils.isEmpty(mobileNo)) {
			throw new Exception("Customer Mobile Number cannot be blank");
		}

		String emailId = rs.getString("CUSTOMER_EMAIL");
		if (StringUtils.isEmpty(emailId)) {
			throw new Exception("Customer Email cannot be blank");
		}

		String branchState = rs.getString("BENFICIARY_BRANCH_STATE");
		if (StringUtils.isEmpty(branchState)) {
			throw new Exception("Bank State cannot be blank");
		}

		String branchCity = rs.getString("BENFICIARY_BRANCH_CITY");
		if (StringUtils.isEmpty(branchCity)) {
			throw new Exception("Bank City cannot be blank");
		}

		String remarks = rs.getString("REMARKS");
		if (StringUtils.isEmpty(remarks)) {
			throw new Exception("Remarks cannot be blank");
		}
		return true;
	}
}
