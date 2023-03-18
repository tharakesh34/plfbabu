package com.pennanttech.pff.external.disbursment;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import com.pennant.app.constants.ImplementationConstants;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.finance.FinAdvancePayments;
import com.pennanttech.dataengine.DataEngineExport;
import com.pennanttech.dataengine.model.DataEngineStatus;
import com.pennanttech.pennapps.core.App;
import com.pennanttech.pennapps.core.AppException;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.model.LoggedInUser;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pff.core.util.QueryUtil;
import com.pennanttech.pff.external.AbstractInterface;
import com.pennanttech.pff.external.DisbursementRequest;
import com.pennanttech.pff.model.disbursment.DisbursementData;

public class DefaultDisbursementRequest extends AbstractInterface implements DisbursementRequest {
	protected final Logger logger = LogManager.getLogger(getClass());

	public enum DisbursementTypes {
		IMPS, RTGS, NEFT, DD, CHEQUE, I, IFT;
	}

	public DefaultDisbursementRequest() {
		super();
	}

	@Deprecated
	@Override
	public void sendReqest(Object... params) throws Exception {
		DisbursementData disbursementDatasb = new DisbursementData();
		disbursementDatasb.setFinType((String) params[0]);
		disbursementDatasb.setDisbursements((List<FinAdvancePayments>) params[1]);
		disbursementDatasb.setUserId((long) params[2]);
		disbursementDatasb.setFileNamePrefix((String) params[3]);

		sendReqest(disbursementDatasb);
	}

	@Override
	public void sendReqest(DisbursementData disbursementDatasb) throws Exception {
		generateRequest(disbursementDatasb, disbursementDatasb.getUserDetails());
	}

	private void generateRequest(DisbursementData disbursementData, LoggedInUser userDetails) throws Exception {
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

		String finType = disbursementData.getFinType();
		List<FinAdvancePayments> disbursements = disbursementData.getDisbursements();
		long userId = disbursementData.getUserId();
		String fileNamePrefix = disbursementData.getFileNamePrefix();

		String configName = disbursementData.getDataEngineConfigName();

		for (FinAdvancePayments disbursment : disbursements) {
			type = DisbursementTypes.valueOf(disbursment.getPaymentType());

			switch (type) {
			case IMPS:
				if (!StringUtils.isBlank(configName)) {
					stp_IMPS.add(disbursment);
				} else {
					other_IMPS.add(disbursment);
				}
				break;
			case NEFT:
				if (!StringUtils.isBlank(configName)) {
					stp_NEFT.add(disbursment);
				} else {
					other_NEFT.add(disbursment);
				}
				break;
			case RTGS:
				if (!StringUtils.isBlank(configName)) {
					stp_RTGS.add(disbursment);
				} else {
					other_RTGS.add(disbursment);
				}
				break;
			case DD:
				if (!StringUtils.isBlank(configName)) {
					stp_DD.add(disbursment);
				} else {
					other_DD.add(disbursment);
				}
				break;
			case CHEQUE:
				if (!StringUtils.isBlank(configName)) {
					stp_CHEQUE.add(disbursment);
				} else {
					other_CHEQUE.add(disbursment);
				}
				break;
			default:
				if (!StringUtils.isBlank(configName)) {
					stp_Other.add(disbursment);
				} else {
					other_Other.add(disbursment);
				}
				break;
			}
		}

		/*
		 * if (!stp_IMPS.isEmpty()) { List<Long> idList = null; try { idList = prepareRequest(getPaymentIds(stp_IMPS),
		 * type.name()); } catch (Exception e) { logger.error(Literal.EXCEPTION, e); }
		 * 
		 * sendIMPSRequest("DISB_EXPORT_IMPS", idList, userId); }
		 * 
		 * if (!other_IMPS.isEmpty()) { List<Long> idList = null; try { idList =
		 * prepareRequest(getPaymentIds(other_IMPS), type.name()); } catch (Exception e) {
		 * logger.error(Literal.EXCEPTION, e); }
		 * 
		 * sendIMPSRequest("DISB_EXPORT_IMPS", idList, userId); }
		 */

		generateFile(configName, DisbursementTypes.NEFT.name(), finType, userId, stp_NEFT, fileNamePrefix, userDetails);
		generateFile(configName, DisbursementTypes.RTGS.name(), finType, userId, stp_RTGS, fileNamePrefix, userDetails);
		generateFile(configName, DisbursementTypes.CHEQUE.name(), finType, userId, stp_CHEQUE, fileNamePrefix,
				userDetails);
		generateFile(configName, DisbursementTypes.DD.name(), finType, userId, stp_DD, fileNamePrefix, userDetails);
		generateFile(configName, DisbursementTypes.I.name(), finType, userId, stp_Other, fileNamePrefix, userDetails);
		generateFile(configName, DisbursementTypes.IMPS.name(), finType, userId, stp_IMPS, fileNamePrefix, userDetails);

		// FIXME-MUR check me where i am using.
		generateFile("DISB_EXPORT_OTHER_NEFT_RTGS", DisbursementTypes.NEFT.name(), finType, userId, other_NEFT, null,
				userDetails);
		generateFile("DISB_EXPORT_OTHER_NEFT_RTGS", DisbursementTypes.RTGS.name(), finType, userId, other_RTGS, null,
				userDetails);
		generateFile("DISB_EXPORT_OTHER_CHEQUE_DD", DisbursementTypes.DD.name(), finType, userId, other_DD, null,
				userDetails);
		generateFile("DISB_EXPORT_OTHER_CHEQUE_DD", DisbursementTypes.CHEQUE.name(), finType, userId, other_CHEQUE,
				null, userDetails);
		generateFile("DISB_EXPORT_OTHER_NEFT_RTGS", DisbursementTypes.I.name(), finType, userId, other_Other, null,
				userDetails);
		generateFile("DISB_EXPORT_OTHER_NEFT_RTGS", DisbursementTypes.IMPS.name(), finType, userId, other_IMPS, null,
				userDetails);

	}

	private void generateFile(String configName, String paymentType, String finType, long userId,
			List<FinAdvancePayments> disbusments, String fileNamePrefix, LoggedInUser userDetails) throws Exception {
		if (CollectionUtils.isEmpty(disbusments)) {
			return;
		}

		Map<String, List<FinAdvancePayments>> map = getOtherBankMap(disbusments);

		List<String> parnerBanks = new ArrayList<String>(map.keySet());

		for (String bank : parnerBanks) {
			List<Long> idList = null;
			try {
				idList = prepareRequest(getPaymentIds(map.get(bank)), paymentType);

				if (idList == null || idList.isEmpty() || (idList.size() != disbusments.size())) {
					throw new ConcurrencyException();
				}

				if (!isConfigExists(configName)) {
					throw new AppException("Data engine configuration for " + configName
							+ "not found. Please contact the system administrator..");
				}

				generateFile(configName, idList, paymentType, bank, finType, userId, fileNamePrefix, userDetails);

			} catch (Exception e) {
				conclude(null, idList);
				throw e;
			}
		}

	}

	// config name getting empty show message
	public boolean isConfigExists(String configName) {
		try {
			String sql = "select Name from data_engine_config  where Name= :Name";
			MapSqlParameterSource paramSource = new MapSqlParameterSource();
			paramSource.addValue("Name", configName);
			if (namedJdbcTemplate.queryForObject(sql.toString(), paramSource, String.class) == null) {
				return false;
			}
		} catch (DataAccessException e) {
			return false;
		}
		return true;
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

	private Long[] getPaymentIds(List<FinAdvancePayments> finAdvancePayments) {
		Long[] builder = new Long[finAdvancePayments.size()];

		for (int i = 0; i < builder.length; i++) {
			builder[i] = finAdvancePayments.get(i).getPaymentId();
		}
		return builder;
	}

	private void generateFile(String configName, List<Long> idList, String paymentType, String partnerbankCode,
			String finType, long userId, String fileNamePrefix, LoggedInUser userDetails) throws Exception {
		DataEngineStatus status = null;
		DataEngineExport export = new DataEngineExport(dataSource, userId, App.DATABASE.name(), true,
				SysParamUtil.getAppValueDate());

		Map<String, Object> filterMap = new HashMap<>();
		filterMap.put("ID", idList);
		filterMap.put("STATUS", "APPROVED");

		Map<String, Object> parameterMap = new HashMap<>();
		parameterMap.put("PRODUCT_CODE", StringUtils.trimToEmpty(finType));
		parameterMap.put("PAYMENT_TYPE", paymentType);
		parameterMap.put("PARTNER_BANK_CODE", partnerbankCode);
		parameterMap.put("SYS_DATE", DateUtil.getSysDate());
		parameterMap.put("USER_ID", userDetails.getUserId());
		parameterMap.put("USER_NAME", userDetails.getUserName());
		parameterMap.put("USER_DEPT_CODE", userDetails.getDepartmentCode());
		parameterMap.put("USER_BRANCH_CODE", userDetails.getBranchCode());
		parameterMap.put("USER_BRANCH_NAME", userDetails.getBranchName());
		parameterMap.put("ddMMYY", DateUtil.getSysDate("ddMMyy"));

		try {
			if ("DISB_EXPORT_DEFAULT".equals(configName) || "DISB_EXPORT_HDFC".equals(configName)) {
				parameterMap.put("CLIENT_CODE", fileNamePrefix);
				parameterMap.put("SEQ_LPAD_SIZE", 3);
				parameterMap.put("SEQ_LPAD_VALUE", "0");
			}

			export.setValueDate(SysParamUtil.getAppValueDate());
			export.setFilterMap(filterMap);
			export.setParameterMap(parameterMap);
			status = export.exportData(configName, false);

			if (status == null || !"S".equals(status.getStatus())) {
				if (status != null) {
					throw new AppException(status.getRemarks());
				} else {
					throw new Exception();
				}
			}

		} catch (Exception e) {
			throw e;
		} finally {
			conclude(status, idList);
		}
	}

	private void sendIMPSRequest(String configName, List<Long> dibursements, long userId) {
		DisbursemenIMPSRequestProcess impsRequest = new DisbursemenIMPSRequestProcess(dataSource, userId,
				SysParamUtil.getAppValueDate(), SysParamUtil.getAppDate());

		impsRequest.setDisbursments(dibursements);
		try {
			impsRequest.process(configName);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}

	}

	private synchronized List<Long> prepareRequest(Long[] disbursments, final String type) throws Exception {
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
		sql.append(" CHEQUE_NUMBER,");
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
		sql.append(" CHANNEL,");
		sql.append(" PARTNERBANK_ID,");
		sql.append(" PARTNERBANK_CODE,");
		sql.append(" PARTNERBANK_ACCOUNT,");
		sql.append(" TRANSACTION_TYPE_CODE");
		sql.append(" FROM INT_DISBURSEMENT_REQUEST_VIEW ");
		sql.append(" WHERE PAYMENTID IN (:PAYMENTID)");

		paramMap = new MapSqlParameterSource();
		paramMap.addValue("PAYMENTID", Arrays.asList(disbursments));

		// It should come from Data engine default value
		// final String DISB_FI_EMAIL = (String) getSMTParameter("DISB_FI_EMAIL", String.class);
		final ColumnMapRowMapper rowMapper = new ColumnMapRowMapper();
		try {
			return namedJdbcTemplate.query(sql.toString(), paramMap, new RowMapper<Long>() {
				@Override
				public Long mapRow(ResultSet rs, int rowNum) throws SQLException {
					Map<String, Object> rowMap = rowMapper.mapRow(rs, rowNum);
					rowMap.put("BATCH_ID", 0);
					// rowMap.put("PAYMENT_DETAIL1", DISB_FI_EMAIL);
					rowMap.put("RESP_BATCH_ID", 0);
					rowMap.put("TRANSACTIONREF", null);
					// rowMap.put("CHEQUE_NUMBER", null);
					rowMap.put("DD_CHEQUE_CHARGE", null);
					rowMap.put("PAYMENT_DATE", null);
					rowMap.put("REJECT_REASON", null);

					Date appDate = DateUtil.parse(SysParamUtil.getAppDate("yyyy-MM-dd"), "yyyy-MM-dd");
					rowMap.put("DOWNLOADED_ON", appDate);

					BigDecimal disbAmount = (BigDecimal) rowMap.get("DISBURSEMENT_AMOUNT");
					disbAmount = disbAmount.divide(new BigDecimal(100), 2, RoundingMode.HALF_DOWN);
					rowMap.put("DISBURSEMENT_AMOUNT", disbAmount);

					if (DisbursementTypes.IMPS.name().equals(type)) {
						try {
						} catch (Exception e) {
							logger.error(Literal.EXCEPTION, e);
							return null;
						}
					}
					long id = insertData(rowMap);
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
			namedJdbcTemplate.update(sql, getMapSqlParameterSource(rowMap), keyHolder, new String[] { "id" });
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}
		return keyHolder.getKey().longValue();
	}

	private void conclude(DataEngineStatus status, List<Long> idList) {

		if (CollectionUtils.isEmpty(idList)) {
			return;
		}

		if (status == null || !"S".equals(status.getStatus())) {
			MapSqlParameterSource paramMap = new MapSqlParameterSource();
			paramMap.addValue("ID", idList);
			paramMap.addValue("STATUS", "APPROVED");

			namedJdbcTemplate.update(
					"UPDATE FINADVANCEPAYMENTS SET STATUS = :STATUS where PAYMENTID IN (select DISBURSEMENT_ID from DISBURSEMENT_REQUESTS where ID IN(:ID))",
					paramMap);
			namedJdbcTemplate.update(
					"UPDATE PAYMENTINSTRUCTIONS SET STATUS = :STATUS where PAYMENTINSTRUCTIONID IN (select DISBURSEMENT_ID from DISBURSEMENT_REQUESTS where ID IN(:ID))",
					paramMap);
			namedJdbcTemplate.update(
					"UPDATE INSURANCEPAYMENTINSTRUCTIONS SET STATUS = :STATUS where ID IN (select DISBURSEMENT_ID from DISBURSEMENT_REQUESTS where ID IN(:ID))",
					paramMap);
			namedJdbcTemplate.update("delete from DISBURSEMENT_REQUESTS where ID IN(:ID)", paramMap);
		}
	}

	public static String amountFormate(BigDecimal amount, int dec) {
		BigDecimal bigDecimal = BigDecimal.ZERO;
		if (amount != null) {
			bigDecimal = amount.divide(BigDecimal.valueOf(Math.pow(10, dec)));
		}

		return formatAmount(bigDecimal, dec, false);
	}

	public static String formatAmount(BigDecimal value, int decPos, boolean debitCreditSymbol) {

		if (value != null && value.compareTo(BigDecimal.ZERO) != 0) {
			DecimalFormat df = new DecimalFormat();

			String format = "";

			if (ImplementationConstants.INDIAN_IMPLEMENTATION) {
				format = "###,###,###,###";// Can be modified for Local Currency
											// format indication
			} else {
				format = "###,###,###,###";
			}

			StringBuilder sb = new StringBuilder(format);
			boolean negSign = false;

			if (decPos > 0) {
				sb.append('.');
				for (int i = 0; i < decPos; i++) {
					sb.append('0');
				}

				if (value.compareTo(BigDecimal.ZERO) == -1) {
					negSign = true;
					value = value.multiply(new BigDecimal("-1"));
				}

				if (negSign) {
					value = value.multiply(new BigDecimal("-1"));
				}
			}

			if (debitCreditSymbol) {
				String s = sb.toString();
				sb.append(" 'Cr';").append(s).append(" 'Dr'");
			}

			df.applyPattern(sb.toString());
			return df.format(value);
		} else {
			String string = "0.";
			for (int i = 0; i < decPos; i++) {
				string = string.concat("0");
			}
			return string;
		}
	}
}
