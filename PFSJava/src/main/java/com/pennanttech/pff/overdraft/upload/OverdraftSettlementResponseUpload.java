package com.pennanttech.pff.overdraft.upload;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.zkoss.util.media.Media;

import com.pennant.app.constants.ImplementationConstants;
import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.customermasters.CustomerDAO;
import com.pennant.backend.dao.finance.FinAdvancePaymentsDAO;
import com.pennant.backend.dao.finance.FinServiceInstrutionDAO;
import com.pennant.backend.dao.finance.FinanceDisbursementDAO;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.model.finance.FinAdvancePayments;
import com.pennant.backend.model.finance.FinServiceInstruction;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceDisbursement;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennanttech.dataengine.DataEngineExport;
import com.pennanttech.dataengine.DataEngineImport;
import com.pennanttech.dataengine.ProcessRecord;
import com.pennanttech.dataengine.model.DataEngineAttributes;
import com.pennanttech.dataengine.model.DataEngineStatus;
import com.pennanttech.dataengine.model.Table;
import com.pennanttech.pennapps.core.App;
import com.pennanttech.pennapps.core.AppException;
import com.pennanttech.pennapps.core.jdbc.BasicDao;
import com.pennanttech.pennapps.core.jdbc.JdbcUtil;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pff.odsettlementprocess.model.ODSettlementProcess;
import com.pennanttech.pff.overdraft.dao.OverdraftSettlementDAO;

public class OverdraftSettlementResponseUpload extends BasicDao<ODSettlementProcess> implements ProcessRecord {

	private DataSource dataSource;
	private PlatformTransactionManager transactionManager;
	private FinanceMainDAO financeMainDAO;
	private FinAdvancePaymentsDAO finAdvancePaymentsDAO;
	private FinServiceInstrutionDAO finServiceInstructionDAO;
	private FinanceDisbursementDAO financeDisbursementDAO;
	private CustomerDAO customerDAO;
	private OverdraftSettlementDAO overdraftSettlementDAO;

	public OverdraftSettlementResponseUpload() {
		super();
	}

	public void oDSettlementFileUploadProcessResponseFile(Object... params) throws Exception {
		logger.debug(Literal.ENTERING);

		long userId = (Long) params[0];
		DataEngineStatus status = (DataEngineStatus) params[1];
		File file = (File) params[2];
		Media media = (Media) params[3];
		String configName = status.getName();
		String name = "";

		if (file != null) {
			name = file.getName();
		} else if (media != null) {
			name = media.getName();
		}

		status.reset();
		status.setFileName(name);
		status.setRemarks("initiated Settlement upload  file [ " + name + " ] processing..");

		Date appDate = SysParamUtil.getAppDate();
		DataEngineImport dataEngine = new DataEngineImport(dataSource, userId, App.DATABASE.name(), true, appDate,
				status);
		dataEngine.setFile(file);
		dataEngine.setMedia(media);
		dataEngine.setValueDate(appDate);
		Map<String, Object> filterMap = new HashMap<>();
		Map<String, Object> parametersMap = new HashMap<>();
		dataEngine.setParameterMap(parametersMap);
		dataEngine.setFilterMap(filterMap);
		dataEngine.setProcessRecord(this);
		dataEngine.importData(configName);

		do {
			if ("S".equals(status.getStatus()) || "F".equals(status.getStatus())) {
				break;
			}
		} while ("S".equals(status.getStatus()) || "F".equals(status.getStatus()));
		logger.debug(Literal.LEAVING);
	}

	@Override
	public void saveOrUpdate(DataEngineAttributes attributes, MapSqlParameterSource record, Table table) {
		logger.debug(Literal.ENTERING);

		TransactionStatus txStatus = null;

		try {
			MapSqlParameterSource oDSettlementMapdata = new MapSqlParameterSource();

			oDSettlementMapdata.addValue("RequestBatchId", attributes.getStatus().getId());
			oDSettlementMapdata.addValue("TerminalId", (String) record.getValue("TerminalId"));
			oDSettlementMapdata.addValue("MerchantName", (String) record.getValue("MerchantName"));
			oDSettlementMapdata.addValue("CustomerId", Long.valueOf(String.valueOf(record.getValue("CustomerId"))));
			oDSettlementMapdata.addValue("TxnId", Long.valueOf(String.valueOf(record.getValue("TxnId"))));
			oDSettlementMapdata.addValue("TxnType", (String) record.getValue("TxnType"));
			oDSettlementMapdata.addValue("Reference", (String) record.getValue("Reference"));
			oDSettlementMapdata.addValue("Currency", (String) record.getValue("Currency"));

			if (!StringUtils.isEmpty(record.getValue("Amount").toString())) {
				oDSettlementMapdata.addValue("Amount", new BigDecimal((String) record.getValue("Amount")));
			} else {
				oDSettlementMapdata.addValue("Amount", BigDecimal.ZERO);
			}

			oDSettlementMapdata.addValue("ODSettlementRef", (String) record.getValue("ODSettlementRef"));
			oDSettlementMapdata.addValue("TxnDate", (String) record.getValue("TxnDate"));

			DefaultTransactionDefinition txDef = new DefaultTransactionDefinition();
			txDef.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);

			txStatus = this.transactionManager.getTransaction(txDef);

			// Upload Details Validation
			validate(oDSettlementMapdata);

			oDSettlementMapdata.addValue("TxnDate",
					DateUtil.parse((String) record.getValue("TxnDate"), "MMM dd, yyyy  hh:mm:ss"));

			// Saving the Settlement file Details
			overdraftSettlementDAO.saveODSettlementProcessRequest(oDSettlementMapdata);
			FinanceMain finMain = financeMainDAO
					.getFinanceMainByReference(String.valueOf(oDSettlementMapdata.getValue("Reference")), true);

			List<FinAdvancePayments> advPayments = finAdvancePaymentsDAO
					.getFinAdvancePaymentsByFinRef(finMain.getFinID(), "_AView");

			for (FinAdvancePayments finAdvancePayment : advPayments) {

				if (ImplementationConstants.HOLD_DISB_INST_POST) {

					if (finAdvancePayment.getLinkedTranId() != 0) {
						continue;
					}

					finAdvancePayment.setStatus("AC");
					finMain.setLovDescEntityCode(financeMainDAO.getLovDescEntityCode(finMain.getFinID(), "_View"));
					FinanceDetail financeDetail = new FinanceDetail();

					List<FinAdvancePayments> finAdvancePayments = new ArrayList<FinAdvancePayments>();
					finAdvancePayments.add(finAdvancePayment);
					financeDetail.setAdvancePaymentsList(finAdvancePayments);

					List<FinAdvancePayments> advPayList = financeDetail.getAdvancePaymentsList();

					// loop through the disbursements.
					if (CollectionUtils.isNotEmpty(advPayList)) {
						for (int i = 0; i < advPayList.size(); i++) {
							FinAdvancePayments advPayment = advPayList.get(i);
							finAdvancePaymentsDAO.updateLinkedTranId(advPayment);
						}
					}
				}
			}

			logger.debug(Literal.LEAVING);

			this.transactionManager.commit(txStatus);
		} catch (Exception e) {
			this.transactionManager.rollback(txStatus);
			throw new AppException(e.getMessage());
		}

	}

	private void validate(MapSqlParameterSource oDSettlementMapdata) {
		logger.debug(Literal.ENTERING);

		FinanceMain finMain = null;

		String finReference = (String) oDSettlementMapdata.getValue("Reference");
		String serviceReqNo = (String) oDSettlementMapdata.getValue("ODSettlementRef");
		BigDecimal txnAmount = (BigDecimal) oDSettlementMapdata.getValue("Amount");
		String currency = (String) oDSettlementMapdata.getValue("Currency");
		String customerCIF = oDSettlementMapdata.getValue("CustomerId").toString();

		if (oDSettlementMapdata.getValue("Reference") == null || oDSettlementMapdata.getValue("Reference").equals("")) {
			throw new AppException("Reference is mandatory");
		} else {
			finMain = financeMainDAO.getFinanceMainByReference(finReference, true);
			if (finMain == null) {
				throw new AppException("Reference is not avilable in PLF or inactive");
			}
		}

		try {
			DateUtil.parse((String) oDSettlementMapdata.getValue("TxnDate"), "MMM dd, yyyy  hh:mm:ss");
		} catch (Exception e) {
			throw new AppException("TxnDate format is incorrect.It's format should be MMM DD, YYYY  HH:MM:SS.", e);
		}

		String custCIF = customerDAO.getCustomerIdCIF(finMain.getCustID());
		if (!StringUtils.equals(custCIF, customerCIF)) {
			throw new AppException("The combination with ODSettlementRef and CustomerId is Invalid");
		}

		FinServiceInstruction finServInst = finServiceInstructionDAO
				.getFinServiceInstDetailsBySerReqNo(finMain.getFinID(), serviceReqNo);

		if (finServInst == null) {
			throw new AppException("ODSettlementRef is invalid");
		}

		long instId = finServInst.getInstructionUID();

		FinanceDisbursement finDisb = financeDisbursementDAO.getFinanceDisbursementByInstId(instId);
		BigDecimal disbAmount = PennantApplicationUtil.formateAmount(finDisb.getDisbAmount(),
				CurrencyUtil.getFormat(currency));
		if (disbAmount.compareTo(txnAmount) != 0) {
			throw new AppException("Amount should be same as Disbursement Amount");
		}

		Date txnDate = JdbcUtil
				.getDate(DateUtil.parse((String) oDSettlementMapdata.getValue("TxnDate"), "MMM dd, yyyy  hh:mm:ss"));
		Date disbDate = JdbcUtil.getDate(finDisb.getDisbDate());
		if (DateUtil.compare(disbDate, txnDate) != 0) {
			throw new AppException("TxnDate should be match with Disbursement Date");
		}

		boolean isRecordExists = overdraftSettlementDAO.isDuplicateReference(finReference, serviceReqNo);

		if (isRecordExists) {
			throw new AppException("The combination with ODSettlementRef and Reference already exists");
		}
	}

	public DataEngineStatus oDSettlementFileDownload(Object... params) throws Exception {
		long userId = (Long) params[0];
		String userName = (String) params[1];
		Long batchId = (Long) params[2];

		Map<String, Object> filterMap = new HashMap<>();
		filterMap.put("REQUESTBATCHID", batchId);
		Map<String, Object> parameterMap = new HashMap<>();

		DataEngineExport dataEngine = null;
		dataEngine = new DataEngineExport(dataSource, userId, App.DATABASE.name(), true,
				SysParamUtil.getAppValueDate());

		DataEngineStatus status = genetare(dataEngine, userName, filterMap, parameterMap);

		return status;

	}

	protected DataEngineStatus genetare(DataEngineExport dataEngine, String userName, Map<String, Object> filterMap,
			Map<String, Object> parameterMap) throws Exception {
		dataEngine.setFilterMap(filterMap);
		dataEngine.setParameterMap(parameterMap);
		dataEngine.setUserName(userName);
		dataEngine.setValueDate(SysParamUtil.getAppValueDate());
		return dataEngine.exportData("OD_SETTLEMENT_REQUEST_DOWNLOAD");
	}

	@Autowired
	@Override
	public void setDataSource(DataSource dataSource) {
		super.setDataSource(dataSource);
		this.dataSource = dataSource;
	}

	@Autowired
	public void setTransactionManager(PlatformTransactionManager transactionManager) {
		this.transactionManager = transactionManager;
	}

	@Autowired
	public void setFinanceMainDAO(FinanceMainDAO financeMainDAO) {
		this.financeMainDAO = financeMainDAO;
	}

	@Autowired
	public void setFinAdvancePaymentsDAO(FinAdvancePaymentsDAO finAdvancePaymentsDAO) {
		this.finAdvancePaymentsDAO = finAdvancePaymentsDAO;
	}

	@Autowired
	public void setFinServiceInstructionDAO(FinServiceInstrutionDAO finServiceInstructionDAO) {
		this.finServiceInstructionDAO = finServiceInstructionDAO;
	}

	@Autowired
	public void setFinanceDisbursementDAO(FinanceDisbursementDAO financeDisbursementDAO) {
		this.financeDisbursementDAO = financeDisbursementDAO;
	}

	@Autowired
	public void setCustomerDAO(CustomerDAO customerDAO) {
		this.customerDAO = customerDAO;
	}

	@Autowired
	public void setOverdraftSettlementDAO(OverdraftSettlementDAO overdraftSettlementDAO) {
		this.overdraftSettlementDAO = overdraftSettlementDAO;
	}

}
