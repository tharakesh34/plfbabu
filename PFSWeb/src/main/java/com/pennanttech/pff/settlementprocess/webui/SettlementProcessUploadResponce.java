package com.pennanttech.pff.settlementprocess.webui;

import java.io.File;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.zkoss.util.media.Media;

import com.pennant.app.util.DateUtility;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.finance.CashBackDetailDAO;
import com.pennant.backend.dao.finance.FinAdvancePaymentsDAO;
import com.pennant.backend.dao.finance.FinFeeDetailDAO;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.dao.finance.FinanceProfitDetailDAO;
import com.pennant.backend.dao.rmtmasters.PromotionDAO;
import com.pennant.backend.model.extendedfield.ExtendedField;
import com.pennant.backend.model.extendedfield.ExtendedFieldData;
import com.pennant.backend.model.finance.CashBackDetail;
import com.pennant.backend.model.finance.FeeType;
import com.pennant.backend.model.finance.FinAdvancePayments;
import com.pennant.backend.model.finance.FinFeeDetail;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.rmtmasters.Promotion;
import com.pennant.backend.service.extendedfields.ExtendedFieldDetailsService;
import com.pennant.backend.service.finance.CashBackProcessService;
import com.pennant.backend.service.payorderissue.impl.DisbursementPostings;
import com.pennant.backend.util.ExtendedFieldConstants;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.SMTParameterConstants;
import com.pennanttech.dataengine.DataEngineExport;
import com.pennanttech.dataengine.DataEngineImport;
import com.pennanttech.dataengine.ProcessRecord;
import com.pennanttech.dataengine.model.DataEngineAttributes;
import com.pennanttech.dataengine.model.DataEngineStatus;
import com.pennanttech.dataengine.model.Table;
import com.pennanttech.pennapps.core.App;
import com.pennanttech.pennapps.core.AppException;
import com.pennanttech.pennapps.core.jdbc.BasicDao;
import com.pennanttech.pennapps.core.util.SpringBeanUtil;
import com.pennanttech.pennapps.jdbc.search.Search;
import com.pennanttech.pennapps.jdbc.search.SearchProcessor;
import com.pennanttech.pff.settlement.dao.SettlementProcessDAO;
import com.pennanttech.pff.settlementprocess.model.SettlementProcess;

public class SettlementProcessUploadResponce extends BasicDao<SettlementProcess> implements ProcessRecord {
	private SettlementProcessDAO settlementProcessDAO;
	private DataSource dataSource;
	private FinanceMainDAO financeMainDAO;
	private ExtendedFieldDetailsService extendedFieldDetailsService;
	private FinAdvancePaymentsDAO finAdvancePaymentsDAO;
	private DisbursementPostings disbursementPostings;
	private PlatformTransactionManager transactionManager;
	private FinFeeDetailDAO finFeeDetailDAO;
	private PromotionDAO promotionDAO;
	private FinanceProfitDetailDAO profitDetailsDAO;
	private CashBackDetailDAO cashBackDetailDAO;
	private CashBackProcessService cashBackProcessService;

	public PromotionDAO getPromotionDAO() {
		return promotionDAO;
	}

	public void setPromotionDAO(PromotionDAO promotionDAO) {
		this.promotionDAO = promotionDAO;
	}

	public FinFeeDetailDAO getFinFeeDetailDAO() {
		return finFeeDetailDAO;
	}

	public void setFinFeeDetailDAO(FinFeeDetailDAO finFeeDetailDAO) {
		this.finFeeDetailDAO = finFeeDetailDAO;
	}

	public void setDisbursementPostings(DisbursementPostings disbursementPostings) {
		this.disbursementPostings = disbursementPostings;
	}

	public void setFinAdvancePaymentsDAO(FinAdvancePaymentsDAO finAdvancePaymentsDAO) {
		this.finAdvancePaymentsDAO = finAdvancePaymentsDAO;
	}

	public void setExtendedFieldDetailsService(ExtendedFieldDetailsService extendedFieldDetailsService) {
		this.extendedFieldDetailsService = extendedFieldDetailsService;
	}

	public SettlementProcessUploadResponce() {
		super();
	}

	public void settlementFileUploadProcessResponseFile(Object... params) throws Exception {
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

		DataEngineImport dataEngine = new DataEngineImport(dataSource, userId, App.DATABASE.name(), true,
				DateUtility.getAppDate(), status);
		dataEngine.setFile(file);
		dataEngine.setMedia(media);
		dataEngine.setValueDate(DateUtility.getAppDate());
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

	}

	@Override
	public void saveOrUpdate(DataEngineAttributes attributes, MapSqlParameterSource record, Table table) {
		TransactionStatus txStatus = null;

		try {
			MapSqlParameterSource settlementMapdata = new MapSqlParameterSource();

			settlementMapdata.addValue("RequestBatchId", attributes.getStatus().getId());
			settlementMapdata.addValue("SettlementRef", (String) record.getValue("SettlementRef"));
			settlementMapdata.addValue("CustomerRef", (String) record.getValue("CustomerRef"));
			settlementMapdata.addValue("EMIOffer", (String) record.getValue("EMIOffer"));

			String subPayByManufacturer = ((String) record.getValue("SubPayByManfacturer")).replace(" %", "");
			// 144663,144664 ticket issues 			
			if (!StringUtils.isEmpty(record.getValue("SubPayByManfacturer").toString())) {
				settlementMapdata.addValue("SubPayByManfacturer", new BigDecimal(subPayByManufacturer));
			} else {
				settlementMapdata.addValue("SubPayByManfacturer", BigDecimal.ZERO);
			}
			if (!StringUtils.isEmpty(record.getValue("SubvensionAmount").toString())) {
				settlementMapdata.addValue("SubvensionAmount",
						new BigDecimal((String) record.getValue("SubvensionAmount")));
			} else {
				settlementMapdata.addValue("SubvensionAmount", BigDecimal.ZERO);
			}

			settlementMapdata.addValue("CustName", (String) record.getValue("CustName"));
			settlementMapdata.addValue("CustMobile", (String) record.getValue("CustMobile"));
			settlementMapdata.addValue("CustAddress", (String) record.getValue("CustAddress"));
			settlementMapdata.addValue("CustEmail", (String) record.getValue("CustEmail"));
			settlementMapdata.addValue("StoreName", (String) record.getValue("StoreName"));
			settlementMapdata.addValue("StoreAddress", (String) record.getValue("StoreAddress"));
			settlementMapdata.addValue("StoreCity", (String) record.getValue("StoreCity"));
			settlementMapdata.addValue("StoreCountry", "");
			settlementMapdata.addValue("StoreState", (String) record.getValue("StoreState"));
			settlementMapdata.addValue("Issuer", (String) record.getValue("Issuer"));
			settlementMapdata.addValue("Category", (String) record.getValue("Category"));
			settlementMapdata.addValue("Description", (String) record.getValue("Description"));
			settlementMapdata.addValue("Serial", (String) record.getValue("Serial"));
			settlementMapdata.addValue("Manufacturer", (String) record.getValue("Manufacturer"));
			settlementMapdata.addValue("Acquirer", (String) record.getValue("Acquirer"));
			settlementMapdata.addValue("ManufactureId", (String) record.getValue("ManufactureId"));
			settlementMapdata.addValue("TerminalId", (String) record.getValue("TerminalId"));
			settlementMapdata.addValue("SettlementBatch", (String) record.getValue("SettlementBatch"));
			settlementMapdata.addValue("BankInvoice", (String) record.getValue("BankInvoice"));
			settlementMapdata.addValue("AuthCode", (String) record.getValue("AuthCode"));
			settlementMapdata.addValue("HostReference", (String) record.getValue("HostReference"));
			settlementMapdata.addValue("TransactionDateTime",
					DateUtility.getDate((String) record.getValue("TransactionDateTime"), "MMM dd, yyyy  hh:mm:ss"));
			settlementMapdata.addValue("SettlementDateTime",
					DateUtility.getDate((String) record.getValue("SettlementDateTime"), "MMM dd, yyyy  hh:mm:ss"));
			settlementMapdata.addValue("BillingInvoice", (String) record.getValue("BillingInvoice"));
			settlementMapdata.addValue("TransactionStatus", (String) record.getValue("TransactionStatus"));
			settlementMapdata.addValue("Reason", (String) record.getValue("Reason"));
			settlementMapdata.addValue("ProductCategory", (String) record.getValue("ProductCategory"));
			settlementMapdata.addValue("ProductSubCategory1", (String) record.getValue("ProductSubCategory1"));
			settlementMapdata.addValue("ProductSubCategory2", (String) record.getValue("ProductSubCategory2"));
			settlementMapdata.addValue("ModelName", (String) record.getValue("ModelName"));
			if (!StringUtils.isEmpty(record.getValue("MaxValueOfProduct").toString())) {
				settlementMapdata.addValue("MaxValueOfProduct",
						new BigDecimal((String) record.getValue("MaxValueOfProduct")));
			} else {
				settlementMapdata.addValue("MaxValueOfProduct", BigDecimal.ZERO);
			}

			settlementMapdata.addValue("MerchantName", (String) record.getValue("MerchantName"));

			DefaultTransactionDefinition txDef = new DefaultTransactionDefinition();
			txDef.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);

			txStatus = this.transactionManager.getTransaction(txDef);

			// Upload Details Validation
			validate(settlementMapdata);
			//Saving the Settlement file Details
			settlementProcessDAO.saveSettlementProcessRequest(settlementMapdata);

			FinanceMain finMain = financeMainDAO
					.getFinanceMainByHostReference(String.valueOf(settlementMapdata.getValue("HostReference")), true);

			// DBD Amount Accounting Process
			Promotion promotion = promotionDAO.getPromotionByReferenceId(finMain.getPromotionSeqId(), "");

			if (promotion.isDbd() && !promotion.isDbdRtnd()) {

				Date appDate = SysParamUtil.getAppDate();
				Date cbDate = DateUtility.addMonths(finMain.getFinStartDate(), promotion.getDlrCbToCust());

				if (DateUtility.compare(appDate, cbDate) >= 0) {

					FeeType feeType = setFeeTypeData(promotion.getDbdFeeTypId());
					CashBackDetail cashBackDetail = cashBackDetailDAO
							.getManualAdviseIdByFinReference(finMain.getFinReference(), "DBD");
					finMain.setLastMntBy(1000);
					finMain.setLastMntOn(new Timestamp(System.currentTimeMillis()));
					finMain.setFinCcy(SysParamUtil.getAppCurrency());

					if (cashBackDetail != null) {
						BigDecimal balAmount = cashBackDetail.getAmount();
						// Cash Back amount adjustments
						if (promotion.isKnckOffDueAmt()) {
							try {
								balAmount = cashBackProcessService.createReceiptOnCashBack(cashBackDetail);
							} catch (Exception e) {
								logger.error("Exception", e);
							}
						}
						if (balAmount.compareTo(BigDecimal.ZERO) > 0) {
							cashBackProcessService.createPaymentInstruction(finMain, feeType.getFeeTypeCode(),
									cashBackDetail.getAdviseId(), balAmount);
						} else {
							cashBackDetailDAO.updateCashBackDetail(cashBackDetail.getAdviseId());
						}
					}

				}

			}

			List<FinAdvancePayments> advPayments = finAdvancePaymentsDAO
					.getFinAdvancePaymentsByFinRef(finMain.getFinReference(), "_AView");

			for (FinAdvancePayments finAdvancePayment : advPayments) {

				if (SysParamUtil.isAllowed(SMTParameterConstants.HOLD_DISB_INST_POST)) {

					finAdvancePayment.setStatus("AC");
					finMain.setLovDescEntityCode(
							financeMainDAO.getLovDescEntityCode(finMain.getFinReference(), "_View"));
					FinanceDetail financeDetail = new FinanceDetail();

					List<FinAdvancePayments> finAdvancePayments = new ArrayList<FinAdvancePayments>();
					finAdvancePayments.add(finAdvancePayment);
					financeDetail.setAdvancePaymentsList(finAdvancePayments);

					Map<Integer, Long> finAdvanceMap = disbursementPostings.prepareDisbPostingApproval(
							financeDetail.getAdvancePaymentsList(), finMain, finMain.getFinBranch());

					List<FinAdvancePayments> advPayList = financeDetail.getAdvancePaymentsList();

					// loop through the disbursements.
					if (CollectionUtils.isNotEmpty(advPayList)) {
						for (int i = 0; i < advPayList.size(); i++) {
							FinAdvancePayments advPayment = advPayList.get(i);
							if (finAdvanceMap.containsKey(advPayment.getPaymentSeq())) {
								advPayment.setLinkedTranId(finAdvanceMap.get(advPayment.getPaymentSeq()));
								finAdvancePaymentsDAO.updateLinkedTranId(advPayment);
							}
						}
					}
				}
			}
			this.transactionManager.commit(txStatus);
		} catch (Exception e) {
			this.transactionManager.rollback(txStatus);
			throw new AppException(e.getMessage());
		}

	}

	public FeeType setFeeTypeData(long feeTypeId) {

		if (feeTypeId == 0) {
			return null;
		}

		FeeType feeType;

		Search search = new Search(FeeType.class);
		search.addFilterEqual("FeeTypeId", feeTypeId);

		SearchProcessor searchProcessor = (SearchProcessor) SpringBeanUtil.getBean("searchProcessor");
		feeType = (FeeType) searchProcessor.getResults(search).get(0);

		return feeType;
	}

	private void validate(MapSqlParameterSource settlementMapdata) {
		FinanceMain finMain = null;
		if (settlementMapdata.getValue("HostReference") == null
				|| settlementMapdata.getValue("HostReference").equals("")) {
			throw new AppException("HostReference is mandatory");
		} else {
			finMain = financeMainDAO
					.getFinanceMainByHostReference(String.valueOf(settlementMapdata.getValue("HostReference")), true);
			if (finMain == null) {
				throw new AppException("HostReference is not avilable in PLF or inactive");
			}
		}
		List<FinFeeDetail> feeList = finFeeDetailDAO.getDMFinFeeDetailByFinRef(finMain.getFinReference(), "");
		BigDecimal feeAmount = BigDecimal.ZERO;

		for (FinFeeDetail finFeeDetail : feeList) {
			if (finFeeDetail.isOriginationFee()) {
				feeAmount = feeAmount.add(finFeeDetail.getActualAmount());
			}
		}
		List<FinAdvancePayments> approvedList = finAdvancePaymentsDAO
				.getFinAdvancePaymentsByFinRef(finMain.getFinReference(), "");

		BigDecimal tranAmount = BigDecimal.ZERO;
		for (FinAdvancePayments detail : approvedList) {
			tranAmount = tranAmount.add(detail.getAmtToBeReleased());
		}
		settlementMapdata.addValue("TransactionAmount", tranAmount);
		List<ExtendedField> extData = new ArrayList<>();

		if (settlementMapdata.getValue("TerminalId") == null) {
			throw new AppException("TID is mandatory");
		} else {

		}
		if (settlementMapdata.getValue("ManufactureId") == null) {
			throw new AppException("MID is mandatory");
		}

		if (settlementMapdata.getValue("HostReference") != null) {
			boolean isDuplicateHostRef = settlementProcessDAO
					.isDuplicateHostReference(settlementMapdata.getValue("HostReference").toString());
			if (isDuplicateHostRef) {
				throw new AppException("RRN already Processed");
			}
		}

		if (settlementMapdata.getValue("SettlementRef") != null) {
			boolean isDuplicateSettlementRef = settlementProcessDAO
					.isDuplicateSettlementRef(settlementMapdata.getValue("SettlementRef").toString());
			if (isDuplicateSettlementRef) {
				throw new AppException("EMI Id already exist");
			}
		} else {
			throw new AppException("EMI Id mandatory");
		}

		if (settlementMapdata.getValue("TerminalId") != null && settlementMapdata.getValue("ManufactureId") != null) {
			if (finMain != null) {
				extData = extendedFieldDetailsService.getExtndedFieldDetails(ExtendedFieldConstants.MODULE_LOAN,
						finMain.getFinCategory(), FinanceConstants.FINSER_EVENT_ORG, finMain.getFinReference());
			}
			Map<String, Object> mapValues = new HashMap<String, Object>();
			if (extData != null) {
				for (ExtendedField extendedField : extData) {
					for (ExtendedFieldData extFieldData : extendedField.getExtendedFieldDataList()) {
						mapValues.put(extFieldData.getFieldName(), extFieldData.getFieldValue());
					}
				}
			}

			if (mapValues.get("MID") == null) {
				throw new AppException("MID is null.");
			}

			String mid = (String) mapValues.get("MID");
			if (!StringUtils.equals(mid, settlementMapdata.getValue("ManufactureId").toString())) {
				throw new AppException("In valid MID");
			}

			if (mapValues.get("TID") == null) {
				throw new AppException("TID is null.");
			}

			String tid = (String) mapValues.get("TID");
			if (!StringUtils.equals(tid, settlementMapdata.getValue("TerminalId").toString())) {
				throw new AppException("In valid TID");
			}
		}
	}

	public DataEngineStatus settlementFileDownload(Object... params) throws Exception {
		long userId = (Long) params[0];
		String userName = (String) params[1];
		String batchId = (String) params[2];

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
		return dataEngine.exportData("SETTLEMENT_REQUEST_DOWNLOAD");
	}

	@Override
	public void setDataSource(DataSource dataSource) {
		super.setDataSource(dataSource);
		this.dataSource = dataSource;
	}

	public void setSettlementProcessDAO(SettlementProcessDAO settlementProcessDAO) {
		this.settlementProcessDAO = settlementProcessDAO;
	}

	public FinanceMainDAO getFinanceMainDAO() {
		return financeMainDAO;
	}

	public void setFinanceMainDAO(FinanceMainDAO financeMainDAO) {
		this.financeMainDAO = financeMainDAO;
	}

	public PlatformTransactionManager getTransactionManager() {
		return transactionManager;
	}

	public void setTransactionManager(PlatformTransactionManager transactionManager) {
		this.transactionManager = transactionManager;
	}

	public CashBackDetailDAO getCashBackDetailDAO() {
		return cashBackDetailDAO;
	}

	public void setCashBackDetailDAO(CashBackDetailDAO cashBackDetailDAO) {
		this.cashBackDetailDAO = cashBackDetailDAO;
	}

	public FinanceProfitDetailDAO getProfitDetailsDAO() {
		return profitDetailsDAO;
	}

	public void setProfitDetailsDAO(FinanceProfitDetailDAO profitDetailsDAO) {
		this.profitDetailsDAO = profitDetailsDAO;
	}

	public CashBackProcessService getCashBackProcessService() {
		return cashBackProcessService;
	}

	public void setCashBackProcessService(CashBackProcessService cashBackProcessService) {
		this.cashBackProcessService = cashBackProcessService;
	}

}
