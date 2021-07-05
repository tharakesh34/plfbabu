package com.pennant.pff.service.paymentmethodupload;

import java.math.BigDecimal;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.finance.FinanceScheduleDetailDAO;
import com.pennant.backend.dao.finance.PaymentMethodUploadDAO;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.mandate.Mandate;
import com.pennant.backend.service.mandate.MandateService;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.MandateConstants;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.pff.model.paymentmethodupload.PaymentMethodUpload;
import com.pennant.pff.model.paymentmethodupload.PaymentMethodUploadHeader;
import com.pennanttech.dataengine.constants.ExecutionStatus;
import com.pennanttech.dataengine.model.DataEngineLog;
import com.pennanttech.dataengine.model.DataEngineStatus;
import com.pennanttech.pennapps.core.App;
import com.pennanttech.pennapps.core.App.Type;
import com.pennanttech.pennapps.core.jdbc.BasicDao;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;

public class PaymentMethodUploadProcess extends BasicDao<PaymentMethodUpload> {
	private static final Logger logger = LogManager.getLogger(PaymentMethodUploadProcess.class);

	private PaymentMethodUploadDAO paymentMethodUploadDAO;
	private PlatformTransactionManager transactionManager;
	private FinanceScheduleDetailDAO financeScheduleDetailDAO;
	private MandateService mandateService;

	/**
	 * Process
	 * 
	 * @param header
	 * @throws Exception
	 */
	public void process(PaymentMethodUploadHeader header) throws Exception {
		logger.debug(Literal.ENTERING);

		DataEngineStatus deStatus = header.getDeStatus();
		header.setStatus(deStatus.getStatus());
		header.setTotalRecords((int) header.getDeStatus().getTotalRecords());

		List<PaymentMethodUpload> uploads = header.getPaymentmethodUpload();

		int totalRecords = header.getTotalRecords();
		if (totalRecords != uploads.size()) {
			header.setFailureRecords(totalRecords - uploads.size());
		}
		if (header == null || uploads.isEmpty()) {
			header.setStatus(ExecutionStatus.F.name());
			if (App.TYPE == Type.WEB) {
				header.getDeStatus().setStatus(header.getStatus());
				paymentMethodUploadDAO.updateDeRemarks(header.getDeStatus());
			}
			paymentMethodUploadDAO.updateRemarks(header);
			return;
		}

		deStatus.setStatus(ExecutionStatus.I.name());
		deStatus.setRemarks("File Reading completed, Initiated the data processing...");

		try {
			deStatus.setStatus(ExecutionStatus.E.name());
			doValidate(header);
			processPaymentUpload(header);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		} finally {
			StringBuilder remarks = new StringBuilder();
			if (header.getTotalRecords() > 0) {
				if (header.getFailureRecords() > 0) {
					remarks.append("Completed with exceptions, total Records: ");
					remarks.append(header.getTotalRecords() > 0);
					remarks.append(", Success: ");
					remarks.append(header.getSucessRecords());
					remarks.append(", Failure: ");
					remarks.append(header.getFailureRecords());
					deStatus.setSuccessRecords(header.getSucessRecords());
					deStatus.setFailedRecords(header.getFailureRecords());
					deStatus.setRemarks(remarks.toString());
				}
				setExceptionLog(deStatus);
			}

			for (PaymentMethodUpload cpu : uploads) {
				paymentMethodUploadDAO.logRcUpload(cpu.getErrorDetails(), cpu.getId());
			}

			if (App.TYPE == Type.WEB) {
				paymentMethodUploadDAO.updateDeRemarks(header.getDeStatus());
			}

			if (totalRecords == header.getFailureRecords()) {
				header.setStatus(ExecutionStatus.F.name());
				if (App.TYPE == Type.WEB) {
					deStatus.setStatus(ExecutionStatus.F.name());
				}
			} else {
				deStatus.setStatus(ExecutionStatus.S.name());
				header.setStatus(ExecutionStatus.S.name());
			}

			paymentMethodUploadDAO.updateRemarks(header);
		}
		logger.debug(Literal.LEAVING);
	}

	private void doValidate(PaymentMethodUploadHeader header) {
		logger.debug(Literal.ENTERING);

		List<FinanceMain> finMainsList = paymentMethodUploadDAO.getFinanceMain(header.getId());

		logger.info("Validationg the records...");
		for (PaymentMethodUpload pmu : header.getPaymentmethodUpload()) {

			// Required variables declarations
			String error = "";
			StringBuilder remarks = new StringBuilder(StringUtils.trimToEmpty(pmu.getUploadStatusRemarks()));
			String rpyMethod = pmu.getFinRepayMethod();
			int ccyFormat = 0;
			boolean mandateCheck = FinanceConstants.REPAYMTH_MANUAL.equalsIgnoreCase(rpyMethod)
					|| (FinanceConstants.REPAYMTH_PDC.equalsIgnoreCase(rpyMethod));

			// Loan Status Checking
			boolean isError = false;
			for (FinanceMain fm : finMainsList) {
				if (pmu.getFinReference().equals(fm.getFinReference())) {
					pmu.setFinanceMain(fm);
					if (!fm.isFinIsActive()) {
						isError = true;
						error = "Loan is Not Active.";
						setErrorDeatils(pmu, remarks, error, "RCU002");
					}
					break;
				}
			}
			if (isError) {
				continue;
			}

			// Loan Avilable or not
			if (pmu.getFinanceMain() == null) {
				error = "Loan Details not available for the Loan Reference: " + pmu.getFinReference();
				setErrorDeatils(pmu, remarks, error, "CPU001");
				continue;
			} else {
				ccyFormat = CurrencyUtil.getFormat(pmu.getFinanceMain().getFinCcy());
			}

			// Mandate id +ve or not
			if (pmu.getMandateId() < 0) {
				error = "Mandate Id should be Positive : " + pmu.getMandateId();
				setErrorDeatils(pmu, remarks, error, "CPU001");
				continue;
			}

			// Mandate Details checking
			Mandate mandate = mandateService.getApprovedMandateById(pmu.getMandateId());
			if (mandate == null && !(mandateCheck)) {
				error = "Mandate details are not available for the mandate id: " + pmu.getMandateId();
				setErrorDeatils(pmu, remarks, error, "CPU001");
				continue;
			} else if (mandate != null && (mandateCheck)) {
				error = "For Manual/PDC payment methods Mandate id is not required. Mandate Id: " + pmu.getMandateId();
				setErrorDeatils(pmu, remarks, error, "CPU001");
				continue;
			}

			if (mandate != null) {

				if (!mandate.isActive()) {
					error = "Mandate should be active, Mandate id : " + mandate.getMandateID();
					setErrorDeatils(pmu, remarks, error, "CPU001");
					continue;
				}

				if (mandate.getCustID() != pmu.getFinanceMain().getCustID()) {
					error = "Customer CIF : " + mandate.getCustCIF() + ", for the mandate id :" + mandate.getMandateID()
							+ ", is not match with loan reference : " + pmu.getFinanceMain().getFinReference()
							+ ", Cust Cif :" + pmu.getFinanceMain().getLovDescCustCIF();
					setErrorDeatils(pmu, remarks, error, "CPU001");
					continue;
				}

				if (!StringUtils.equals(mandate.getMandateType(), pmu.getFinRepayMethod())) {
					error = "For the Mandate Id :" + pmu.getMandateId() + ", Mandate Type :" + mandate.getMandateType()
							+ " is not matched with the given Repaymethod :" + pmu.getFinRepayMethod();
					setErrorDeatils(pmu, remarks, error, "CPU001");
					continue;
				}
			}

			if (mandate != null) {
				// Repayamount check with mandates.
				BigDecimal repayAmt = getMaxRepayAmt(pmu);

				error = "The installment amount : " + PennantApplicationUtil.amountFormate(repayAmt, ccyFormat)
						+ ", against the mandate : " + mandate.getMandateID() + ", is more than the max limit : "
						+ PennantApplicationUtil.amountFormate(mandate.getMaxLimit(), ccyFormat);

				if (repayAmt.compareTo(mandate.getMaxLimit()) > 0) {
					setErrorDeatils(pmu, remarks, error, "CPU001");
					continue;
				}

				// validations for MandateRef()
				error = "Mandate reference not available with this Mandate Id : " + mandate.getMandateID();
				if (!MandateConstants.skipRegistration().contains(mandate.getMandateType()))
					if (StringUtils.isBlank(mandate.getMandateRef())) {
						setErrorDeatils(pmu, remarks, error, "CPU002");
						continue;
					}

				// validations for Status()
				error = "Mandate status is not approved with mandate Id :" + mandate.getMandateID();
				if (StringUtils.equals(MandateConstants.STATUS_REJECTED, mandate.getStatus())) {
					setErrorDeatils(pmu, remarks, error, "CPU002");
					continue;
				}

				// OpenMandate
				error = "Mandate Id : " + pmu.getMandateId()
						+ ", is not a open mandate and  already assigned to another loan.";
				if (!mandate.isOpenMandate() && paymentMethodUploadDAO.isMandateIdExists(pmu.getMandateId())) {
					setErrorDeatils(pmu, remarks, error, "CPU002");
					continue;
				}
			}
		}
	}

	private BigDecimal getMaxRepayAmt(PaymentMethodUpload pmu) {
		List<FinanceScheduleDetail> financeScheduleDetails = financeScheduleDetailDAO
				.getFinScheduleDetails(pmu.getFinReference(), "", false);
		BigDecimal repayAmt = BigDecimal.ZERO;
		if (CollectionUtils.isNotEmpty(financeScheduleDetails)) {
			for (int i = 0; i < financeScheduleDetails.size(); i++) {
				FinanceScheduleDetail curSchd = financeScheduleDetails.get(i);
				if (DateUtility.compare(curSchd.getSchDate(), SysParamUtil.getAppDate()) >= 0
						&& curSchd.isRepayOnSchDate()) {
					repayAmt = curSchd.getProfitSchd().add(curSchd.getPrincipalSchd()).add(curSchd.getFeeSchd());
					continue;
				}
			}
		}
		return repayAmt;
	}

	private void setErrorDeatils(PaymentMethodUpload pmu, StringBuilder remarks, String error, String errorCode) {
		if (remarks.length() > 0) {
			remarks.append(", ");
		}
		remarks.append(error);
		pmu.setUploadStatusRemarks(remarks.toString());
		pmu.setStatus("F");
		pmu.setErrorDetail(getErrorDetail(errorCode, error));
	}

	/**
	 * Process Paymnet methods.
	 * 
	 * @param header
	 */
	private void processPaymentUpload(PaymentMethodUploadHeader header) {
		logger.error(Literal.ENTERING);

		int fail = header.getFailureRecords();
		int sucess = header.getSucessRecords();

		for (PaymentMethodUpload changePayment : header.getPaymentmethodUpload()) {

			TransactionStatus txStatus = null;
			String finReference = changePayment.getFinReference();
			String status = changePayment.getStatus();

			logger.info("Processing Payment Method upload>> {}:", finReference);

			if ("F".equals(status)) {
				paymentMethodUploadDAO.updateChangePaymentDetails(changePayment);
				updateLog(header.getDeStatus().getId(), changePayment.getFinReference(), "F",
						changePayment.getUploadStatusRemarks());
				fail++;
				continue;
			} else {
				changePayment.setStatus("S");
				changePayment.setUploadStatusRemarks("");
				sucess = sucess + 1;
			}

			DefaultTransactionDefinition txDef = new DefaultTransactionDefinition();
			txDef.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);

			txStatus = this.transactionManager.getTransaction(txDef);

			paymentMethodUploadDAO.updateFinRepaymethod(changePayment);
			paymentMethodUploadDAO.updateChangePaymentDetails(changePayment);

			this.transactionManager.commit(txStatus);
			logger.info("Completed Payment Method Change upload >> {}:", finReference);

		}
		header.setFailureRecords(fail);
		header.setSucessRecords(sucess);

		logger.error(Literal.ENTERING);
	}

	private ErrorDetail getErrorDetail(String code, String message) {
		ErrorDetail ed = new ErrorDetail();
		ed.setCode(code);
		ed.setMessage(message);
		return ed;
	}

	// Setting the exception log data engine status.
	private void setExceptionLog(DataEngineStatus status) {
		List<DataEngineLog> engineLogs = getExceptions(status.getId());
		if (CollectionUtils.isNotEmpty(engineLogs)) {
			status.setDataEngineLogList(engineLogs);
		}
	}

	private void updateLog(long id, String keyId, String status, String reason) {

		StringBuilder query = null;
		MapSqlParameterSource source = null;

		query = new StringBuilder("Update DATA_ENGINE_LOG");
		query.append(" Set Status = :Status, Reason =:Reason Where StatusId = :Id and KeyId = :KeyId");

		source = new MapSqlParameterSource();
		source.addValue("Id", id);
		source.addValue("KeyId", keyId);
		source.addValue("Status", status);
		source.addValue("Reason", reason = reason.length() > 2000 ? reason.substring(0, 1995) : reason);

		int count = this.jdbcTemplate.update(query.toString(), source);

		if (count == 0) {
			query = new StringBuilder();
			query.append(" INSERT INTO DATA_ENGINE_LOG");
			query.append(" (StatusId, KeyId, Status, Reason)");
			query.append(" VALUES(:Id, :KeyId, :Status, :Reason)");
			this.jdbcTemplate.update(query.toString(), source);
		}
		query = null;
		source = null;
	}

	// Getting the exception log
	public List<DataEngineLog> getExceptions(long batchId) {
		RowMapper<DataEngineLog> rowMapper = null;
		MapSqlParameterSource parameterMap = null;
		StringBuilder sql = null;

		try {
			sql = new StringBuilder("Select * from DATA_ENGINE_LOG where StatusId = :ID");
			parameterMap = new MapSqlParameterSource();
			parameterMap.addValue("ID", batchId);
			rowMapper = BeanPropertyRowMapper.newInstance(DataEngineLog.class);
			return jdbcTemplate.query(sql.toString(), parameterMap, rowMapper);
		} catch (Exception e) {
		} finally {
			rowMapper = null;
			sql = null;
		}
		return null;
	}

	public FinanceScheduleDetailDAO getFinanceScheduleDetailDAO() {
		return financeScheduleDetailDAO;
	}

	public void setFinanceScheduleDetailDAO(FinanceScheduleDetailDAO financeScheduleDetailDAO) {
		this.financeScheduleDetailDAO = financeScheduleDetailDAO;
	}

	public MandateService getMandateService() {
		return mandateService;
	}

	public void setMandateService(MandateService mandateService) {
		this.mandateService = mandateService;
	}

	public void setTransactionManager(PlatformTransactionManager transactionManager) {
		this.transactionManager = transactionManager;
	}

	public void setPaymentMethodUploadDAO(PaymentMethodUploadDAO paymentMethodUploadDAO) {
		this.paymentMethodUploadDAO = paymentMethodUploadDAO;
	}

}
