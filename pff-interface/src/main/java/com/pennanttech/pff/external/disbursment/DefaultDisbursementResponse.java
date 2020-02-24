package com.pennanttech.pff.external.disbursment;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;
import org.zkoss.util.media.Media;

import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.finance.FinAdvancePayments;
import com.pennant.backend.model.finance.FinAutoApprovalDetails;
import com.pennant.backend.model.finance.PaymentInstruction;
import com.pennant.backend.model.insurance.InsurancePaymentInstructions;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.util.DisbursementConstants;
import com.pennanttech.dataengine.DataEngineImport;
import com.pennanttech.dataengine.ValidateRecord;
import com.pennanttech.dataengine.model.DataEngineStatus;
import com.pennanttech.pennapps.core.App;
import com.pennanttech.pennapps.core.model.LoggedInUser;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.process.DisbursementProcess;
import com.pennanttech.pff.core.process.PaymentProcess;
import com.pennanttech.pff.external.AbstractInterface;
import com.pennanttech.pff.external.DisbursementResponse;
import com.pennanttech.pff.logging.dao.FinAutoApprovalDetailDAO;

public class DefaultDisbursementResponse extends AbstractInterface implements DisbursementResponse {
	protected final Logger logger = Logger.getLogger(getClass());

	private DisbursementProcess disbursementProcess;
	private PaymentProcess paymentProcess;
	private LoggedInUser loggedInUser;
	@Autowired(required = false)
	private ValidateRecord disbursementRespDataValidation;

	@Autowired(required = false)
	private FinAutoApprovalDetailDAO finAutoApprovalDetailDAO;

	public DefaultDisbursementResponse() {
		super();
	}

	/*
	 * @Override public void receiveResponse(Object... params) throws Exception { logger.debug(Literal.ENTERING); long
	 * userId = (Long) params[0]; DataEngineStatus status = (DataEngineStatus) params[1]; File file = (File) params[2];
	 * Media media = (Media) params[3];
	 * 
	 * String configName = status.getName();
	 * 
	 * String name = "";
	 * 
	 * if (file != null) { name = file.getName(); } else if (media != null) { name = media.getName(); }
	 * 
	 * status.reset(); status.setFileName(name); status.setRemarks("initiated disbursement response file [ " + name +
	 * " ] processing..");
	 * 
	 * DataEngineImport dataEngine; dataEngine = new DataEngineImport(dataSource, userId, App.DATABASE.name(), true,
	 * getValueDate(), status); dataEngine.setFile(file); dataEngine.setMedia(media);
	 * dataEngine.setValueDate(getValueDate());
	 * 
	 * Map<String, Object> filterMap = new HashMap<>(); filterMap.put(DisbursementConstants.STATUS_AWAITCON,
	 * DisbursementConstants.STATUS_AWAITCON); dataEngine.setFilterMap(filterMap);
	 * 
	 * dataEngine.importData(configName);
	 * 
	 * do { if ("S".equals(status.getStatus()) || "F".equals(status.getStatus())) { receiveResponse(status.getId());
	 * break; } } while ("S".equals(status.getStatus()) || "F".equals(status.getStatus()));
	 * 
	 * logger.info(name + " file processing completed"); }
	 */

	@Override
	public void processResponseFile(Object... params) throws Exception {
		logger.debug(Literal.ENTERING);
		long userId = (Long) params[0];
		DataEngineStatus status = (DataEngineStatus) params[1];
		File file = (File) params[2];
		Media media = (Media) params[3];
		loggedInUser = (LoggedInUser) params[5];

		String configName = status.getName();

		String name = "";

		if (file != null) {
			name = file.getName();
		} else if (media != null) {
			name = media.getName();
		}

		status.reset();
		status.setFileName(name);
		status.setRemarks("initiated disbursement response file [ " + name + " ] processing..");

		DataEngineImport dataEngine;
		dataEngine = new DataEngineImport(dataSource, userId, App.DATABASE.name(), true, SysParamUtil.getAppValueDate(),
				status);
		dataEngine.setFile(file);
		dataEngine.setMedia(media);
		dataEngine.setValueDate(SysParamUtil.getAppValueDate());
		dataEngine.setValidateRecord(disbursementRespDataValidation);
		Map<String, Object> filterMap = new HashMap<>();
		filterMap.put(DisbursementConstants.STATUS_AWAITCON, DisbursementConstants.STATUS_AWAITCON);
		dataEngine.setFilterMap(filterMap);

		dataEngine.importData(configName);

		do {
			if ("S".equals(status.getStatus()) || "F".equals(status.getStatus())) {
				receiveResponse(status.getId(), status.getStatus(), userId);
				break;
			}
		} while ("S".equals(status.getStatus()) || "F".equals(status.getStatus()));

		logger.info(name + " file processing completed");
	}

	private void receiveResponse(long batchId) throws Exception {
		logger.debug(Literal.ENTERING);

		MapSqlParameterSource paramMap = null;
		StringBuilder sql = null;

		//Disbursements
		List<FinAdvancePayments> disbursements = null;
		RowMapper<FinAdvancePayments> rowMapper = null;
		try {
			sql = new StringBuilder();
			sql.append(
					" SELECT FA.PAYMENTID,FA.FINREFERENCE, FA.LINKEDTRANID, DR.PAYMENT_DATE DISBDATE, FA.PAYMENTTYPE, DR.STATUS,");
			sql.append(" FA.BENEFICIARYACCNO, FA.BENEFICIARYNAME, FA.BANKBRANCHID, FA.BANKCODE,");
			sql.append(" FA.PHONECOUNTRYCODE, FA.PHONENUMBER, FA.PHONEAREACODE,");
			sql.append(" DR.CHEQUE_NUMBER LLREFERENCENO, DR.REJECT_REASON REJECTREASON,");
			sql.append(" DR.PAYMENT_DATE CLEARINGDATE, DR.TRANSACTIONREF, FA.PAYMENTSEQ, FA.AMTTOBERELEASED, ");
			sql.append(" PB.ACTYPE AS PARTNERBANKACTYPE, PB.ACCOUNTNO AS PARTNERBANKAC, FA.DISBCCY, FA.LLDATE");
			sql.append(" FROM DISBURSEMENT_REQUESTS DR");
			sql.append(" INNER JOIN FINADVANCEPAYMENTS FA ON FA.PAYMENTID = DR.DISBURSEMENT_ID");
			sql.append(" LEFT JOIN partnerbanks PB ON PB.partnerbankid = FA.partnerbankid");
			sql.append(" WHERE RESP_BATCH_ID = :RESP_BATCH_ID AND CHANNEL = :CHANNEL");
			paramMap = new MapSqlParameterSource();
			paramMap.addValue("RESP_BATCH_ID", batchId);
			paramMap.addValue("CHANNEL", DisbursementConstants.CHANNEL_DISBURSEMENT);

			rowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinAdvancePayments.class);
			disbursements = namedJdbcTemplate.query(sql.toString(), paramMap, rowMapper);

			for (FinAdvancePayments disbursement : disbursements) {
				try {
					disbursementProcess.process(disbursement);
				} catch (Exception e) {
					logger.error(Literal.EXCEPTION, e);
				}
			}
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}

		//Payments..
		List<PaymentInstruction> instructions = null;
		RowMapper<PaymentInstruction> instructionRowMapper = null;
		try {
			sql = new StringBuilder();
			sql.append(" SELECT PH.FINREFERENCE, PH.LINKEDTRANID, PI.PAYMENTID, PI.BANKBRANCHID, PI.ACCOUNTNO, ");
			sql.append(
					" PI.ACCTHOLDERNAME, PI.PHONECOUNTRYCODE, PI.PHONENUMBER, PI.PAYMENTINSTRUCTIONID, PI.PAYMENTAMOUNT,");
			sql.append(" PI.PAYMENTAMOUNT, PI.PAYMENTTYPE, DR.STATUS, DR.REJECT_REASON REJECTREASON,");
			sql.append(" DR.PAYMENT_DATE CLEARINGDATE, DR.TRANSACTIONREF");
			sql.append(" FROM DISBURSEMENT_REQUESTS DR");
			sql.append(" INNER JOIN PAYMENTINSTRUCTIONS PI ON PI.PAYMENTINSTRUCTIONID = DR.DISBURSEMENT_ID");
			sql.append(" INNER JOIN PAYMENTHEADER PH ON PH.PAYMENTID = PI.PAYMENTID");
			sql.append(" WHERE RESP_BATCH_ID = :RESP_BATCH_ID  AND CHANNEL = :CHANNEL");
			paramMap = new MapSqlParameterSource();
			paramMap.addValue("RESP_BATCH_ID", batchId);
			paramMap.addValue("CHANNEL", DisbursementConstants.CHANNEL_PAYMENT);

			instructionRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(PaymentInstruction.class);
			instructions = namedJdbcTemplate.query(sql.toString(), paramMap, instructionRowMapper);

			for (PaymentInstruction instruction : instructions) {
				try {
					paymentProcess.process(instruction);
				} catch (Exception e) {
					logger.error(Literal.EXCEPTION, e);
				}
			}
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}

		// Insurance payments..
		List<InsurancePaymentInstructions> insPaymentInstructions = null;
		RowMapper<InsurancePaymentInstructions> insPaymentInstructionRowMapper = null;
		try {
			sql = new StringBuilder();
			sql.append(" SELECT PI.LINKEDTRANID, PI.ID, VPA.BANKBRANCHID, VPA.ACCOUNTNUMBER, ");
			sql.append(" AVD.DEALERNAME, PI.PAYMENTAMOUNT, PI.PAYMENTTYPE, DR.STATUS,");
			sql.append(" DR.REJECT_REASON REJECTREASON, PI.PROVIDERID,");
			sql.append(" DR.PAYMENT_DATE RESPDATE, DR.TRANSACTIONREF, DR.FINREFERENCE FROM DISBURSEMENT_REQUESTS DR");
			sql.append(" INNER JOIN INSURANCEPAYMENTINSTRUCTIONS PI ON PI.ID = DR.DISBURSEMENT_ID");
			sql.append(" INNER JOIN VASPROVIDERACCDETAIL VPA ON VPA.PROVIDERID = PI.PROVIDERID");
			sql.append(" INNER JOIN BANKBRANCHES BB ON BB.BANKBRANCHID = VPA.BANKBRANCHID");
			sql.append(" INNER JOIN AMTVEHICLEDEALER AVD ON AVD.DEALERID = VPA.PROVIDERID");
			sql.append(" WHERE RESP_BATCH_ID = :RESP_BATCH_ID  AND CHANNEL = :CHANNEL");
			paramMap = new MapSqlParameterSource();
			paramMap.addValue("RESP_BATCH_ID", batchId);
			paramMap.addValue("CHANNEL", DisbursementConstants.CHANNEL_INSURANCE);

			insPaymentInstructionRowMapper = ParameterizedBeanPropertyRowMapper
					.newInstance(InsurancePaymentInstructions.class);
			insPaymentInstructions = namedJdbcTemplate.query(sql.toString(), paramMap, insPaymentInstructionRowMapper);

			for (InsurancePaymentInstructions instruction : insPaymentInstructions) {
				try {
					// For VAS Account postings
					instruction.setUserDetails(loggedInUser);
					paymentProcess.processInsPayments(instruction);
				} catch (Exception e) {
					logger.error(Literal.EXCEPTION, e);
				}
			}
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}
		logger.debug(Literal.LEAVING);
	}

	@Autowired
	public void setDisbursementProcess(DisbursementProcess disbursementProcess) {
		this.disbursementProcess = disbursementProcess;
	}

	@Autowired
	public void setPaymentProcess(PaymentProcess paymentProcess) {
		this.paymentProcess = paymentProcess;
	}

	@Override
	public void receiveResponse(Object... params) throws Exception {
		logger.debug(Literal.ENTERING);

		MapSqlParameterSource paramMap = null;
		StringBuilder sql = null;
		List<String> channelList = new ArrayList<>();
		channelList.add(DisbursementConstants.CHANNEL_DISBURSEMENT);

		//Disbursements
		//Below fields in select query not to be removed.
		List<FinAdvancePayments> finAdvPayments = null;
		RowMapper<FinAdvancePayments> rowMapper = null;
		try {
			sql = new StringBuilder();
			sql.append(" SELECT FA.PAYMENTID,FA.FINREFERENCE, FA.LINKEDTRANID, DR.PAYMENT_DATE DISBDATE");
			sql.append(", FA.PAYMENTTYPE, DR.STATUS, FA.BENEFICIARYACCNO, FA.BENEFICIARYNAME, FA.BANKBRANCHID");
			sql.append(", FA.BANKCODE, FA.PHONECOUNTRYCODE, FA.PHONENUMBER, FA.PHONEAREACODE, FA.AMTTOBERELEASED");
			sql.append(", FA.RECORDTYPE, DR.CHEQUE_NUMBER LLREFERENCENO, DR.REJECT_REASON REJECTREASON");
			sql.append(", DR.REALIZATION_DATE REALIZATIONDATE, DR.DOWNLOADED_ON DOWNLOADEDON");
			sql.append(", DR.PAYMENT_DATE CLEARINGDATE, DR.TRANSACTIONREF, FA.PAYMENTSEQ");
			sql.append(", PB.ACTYPE AS PARTNERBANKACTYPE, PB.ACCOUNTNO AS PARTNERBANKAC, FA.DISBCCY, FA.LLDATE");
			sql.append(" FROM DISBURSEMENT_REQUESTS DR");
			sql.append(" INNER JOIN FINADVANCEPAYMENTS FA ON FA.PAYMENTID = DR.DISBURSEMENT_ID");
			sql.append(" LEFT JOIN partnerbanks PB ON PB.partnerbankid = FA.partnerbankid");
			sql.append(" WHERE RESP_BATCH_ID = :RESP_BATCH_ID AND CHANNEL IN(:CHANNEL) ");
			paramMap = new MapSqlParameterSource();
			paramMap.addValue("RESP_BATCH_ID", params[0]);
			paramMap.addValue("CHANNEL", channelList);

			rowMapper = BeanPropertyRowMapper.newInstance(FinAdvancePayments.class);
			finAdvPayments = namedJdbcTemplate.query(sql.toString(), paramMap, rowMapper);

			List<FinAutoApprovalDetails> autoAppList = new ArrayList<FinAutoApprovalDetails>();

			for (FinAdvancePayments finAdvPayment : finAdvPayments) {
				try {
					disbursementProcess.process(finAdvPayment);
					boolean autoApprove = false;

					//get the QDP flag from Loan level.
					FinanceType financeType = finAutoApprovalDetailDAO
							.getQDPflagByFinref(finAdvPayment.getFinReference());

					if (financeType != null) {
						//Check for the AutoApprove flag from LoanType.
						if (financeType.isQuickDisb() && financeType.isAutoApprove()) {
							autoApprove = true;
						}
					}
					/*
					 * //FIXME: Need to check Payment Mode based AutoApprove Required or not. if
					 * ((StringUtils.equals(finAdvPayment.getPaymentType(), DisbursementConstants.PAYMENT_TYPE_NEFT) ||
					 * StringUtils.equals(finAdvPayment.getPaymentType(), DisbursementConstants.PAYMENT_TYPE_RTGS)) &&
					 * StringUtils.equals(DisbursementConstants.STATUS_PAID, finAdvPayment.getStatus())) { autoApprove =
					 * true; } else if ((StringUtils.equals(finAdvPayment.getPaymentType(),
					 * DisbursementConstants.PAYMENT_TYPE_CHEQUE) || StringUtils.equals(finAdvPayment.getPaymentType(),
					 * DisbursementConstants.PAYMENT_TYPE_DD)) &&
					 * StringUtils.equals(DisbursementConstants.STATUS_REALIZED, finAdvPayment.getStatus())) {
					 * autoApprove = true; }
					 */

					if (autoApprove) {

						FinAutoApprovalDetails detail = new FinAutoApprovalDetails();
						detail.setFinReference(finAdvPayment.getFinReference());
						detail.setBatchId(Long.valueOf(params[0].toString()));
						detail.setDisbId(finAdvPayment.getPaymentId());
						detail.setRealizedDate(finAdvPayment.getRealizationDate());
						if (detail.getRealizedDate() == null) {
							detail.setRealizedDate(finAdvPayment.getClearingDate());
						}
						detail.setStatus(DisbursementConstants.AUTODISB_STATUS_PENDING);
						detail.setUserId(Long.valueOf(params[2].toString()));
						detail.setDownloadedon(finAdvPayment.getDownloadedon());
						autoAppList.add(detail);
					}

				} catch (Exception e) {
					logger.error(Literal.EXCEPTION, e);
				}
			}
			finAutoApprovalDetailDAO.logFinAutoApprovalDetails(autoAppList);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}

		//Payments..
		List<PaymentInstruction> instructions = null;
		RowMapper<PaymentInstruction> instructionRowMapper = null;
		try {
			sql = new StringBuilder();
			sql.append(" SELECT PH.FINREFERENCE, PH.LINKEDTRANID, PI.PAYMENTID, PI.BANKBRANCHID, PI.ACCOUNTNO, ");
			sql.append(
					" PI.ACCTHOLDERNAME, PI.PHONECOUNTRYCODE, PI.PHONENUMBER, PI.PAYMENTINSTRUCTIONID, PI.PAYMENTAMOUNT,");
			sql.append(
					" PI.PAYMENTAMOUNT, PI.PAYMENTTYPE, DR.STATUS, DR.REJECT_REASON REJECTREASON,DR.REALIZATION_DATE REALIZATIONDATE,");
			sql.append(" DR.PAYMENT_DATE CLEARINGDATE, DR.TRANSACTIONREF");
			sql.append(" FROM DISBURSEMENT_REQUESTS DR");
			sql.append(" INNER JOIN PAYMENTINSTRUCTIONS PI ON PI.PAYMENTINSTRUCTIONID = DR.DISBURSEMENT_ID");
			sql.append(" INNER JOIN PAYMENTHEADER PH ON PH.PAYMENTID = PI.PAYMENTID");
			sql.append(" WHERE RESP_BATCH_ID = :RESP_BATCH_ID AND CHANNEL = :CHANNEL");
			paramMap = new MapSqlParameterSource();
			paramMap.addValue("RESP_BATCH_ID", params[0]);
			paramMap.addValue("CHANNEL", DisbursementConstants.CHANNEL_PAYMENT);

			instructionRowMapper = BeanPropertyRowMapper.newInstance(PaymentInstruction.class);
			instructions = namedJdbcTemplate.query(sql.toString(), paramMap, instructionRowMapper);

			for (PaymentInstruction instruction : instructions) {
				try {
					paymentProcess.process(instruction);
				} catch (Exception e) {
					logger.error(Literal.EXCEPTION, e);
				}
			}
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}

		// Insurance payments..
		List<InsurancePaymentInstructions> insPaymentInstructions = null;
		RowMapper<InsurancePaymentInstructions> insPaymentInstructionRowMapper = null;
		try {
			sql = new StringBuilder();
			sql.append(" SELECT PI.LINKEDTRANID, PI.ID, VPA.BANKBRANCHID, VPA.ACCOUNTNUMBER, ");
			sql.append(" AVD.DEALERNAME, AVD.DEALERTELEPHONE, PI.PAYMENTAMOUNT, PI.PAYMENTTYPE, DR.STATUS,");
			sql.append(" DR.REJECT_REASON REJECTREASON,DR.REALIZATION_DATE REALIZATIONDATE,");
			sql.append(" DR.PAYMENT_DATE RESPDATE, DR.TRANSACTIONREF,");
			sql.append(" PI.PROVIDERID, DR.FINREFERENCE FROM DISBURSEMENT_REQUESTS DR ");
			sql.append(" INNER JOIN INSURANCEPAYMENTINSTRUCTIONS PI ON PI.ID = DR.DISBURSEMENT_ID");
			sql.append(" INNER JOIN VASPROVIDERACCDETAIL VPA ON VPA.PROVIDERID = PI.PROVIDERID");
			sql.append(" INNER JOIN BANKBRANCHES BB ON BB.BANKBRANCHID = VPA.BANKBRANCHID");
			sql.append(" INNER JOIN AMTVEHICLEDEALER AVD ON AVD.DEALERID = VPA.PROVIDERID");
			sql.append(" WHERE RESP_BATCH_ID = :RESP_BATCH_ID AND CHANNEL = :CHANNEL");
			paramMap = new MapSqlParameterSource();
			paramMap.addValue("RESP_BATCH_ID", params[0]);
			paramMap.addValue("CHANNEL", DisbursementConstants.CHANNEL_INSURANCE);

			insPaymentInstructionRowMapper = BeanPropertyRowMapper.newInstance(InsurancePaymentInstructions.class);
			insPaymentInstructions = namedJdbcTemplate.query(sql.toString(), paramMap, insPaymentInstructionRowMapper);

			for (InsurancePaymentInstructions instruction : insPaymentInstructions) {
				try {
					// For VAS Account postings
					instruction.setUserDetails(loggedInUser);
					paymentProcess.processInsPayments(instruction);
				} catch (Exception e) {
					logger.error(Literal.EXCEPTION, e);
				}
			}
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}
		logger.debug(Literal.LEAVING);
	}

}