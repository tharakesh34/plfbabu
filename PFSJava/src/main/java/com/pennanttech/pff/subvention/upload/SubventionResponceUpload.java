package com.pennanttech.pff.subvention.upload;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.security.auth.login.AccountNotFoundException;
import javax.sql.DataSource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.zkoss.util.media.Media;

import com.pennant.app.util.PostingsPreparationUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.feetype.FeeTypeDAO;
import com.pennant.backend.dao.finance.CashBackDetailDAO;
import com.pennant.backend.dao.finance.FinFeeDetailDAO;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.dao.finance.ManualAdviseDAO;
import com.pennant.backend.dao.rmtmasters.PromotionDAO;
import com.pennant.backend.dao.rulefactory.PostingsDAO;
import com.pennant.backend.model.extendedfield.ExtendedField;
import com.pennant.backend.model.extendedfield.ExtendedFieldData;
import com.pennant.backend.model.finance.CashBackDetail;
import com.pennant.backend.model.finance.FeeType;
import com.pennant.backend.model.finance.FinFeeDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.rmtmasters.Promotion;
import com.pennant.backend.model.rulefactory.AEAmountCodes;
import com.pennant.backend.model.rulefactory.AEEvent;
import com.pennant.backend.model.rulefactory.ReturnDataSet;
import com.pennant.backend.service.extendedfields.ExtendedFieldDetailsService;
import com.pennant.backend.service.finance.CashBackProcessService;
import com.pennant.backend.util.ExtendedFieldConstants;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.pff.core.engine.accounting.AccountingEngine;
import com.pennanttech.dataengine.DataEngineImport;
import com.pennanttech.dataengine.ProcessRecord;
import com.pennanttech.dataengine.model.DataEngineAttributes;
import com.pennanttech.dataengine.model.DataEngineStatus;
import com.pennanttech.dataengine.model.Table;
import com.pennanttech.pennapps.core.App;
import com.pennanttech.pennapps.core.AppException;
import com.pennanttech.pennapps.core.InterfaceException;
import com.pennanttech.pennapps.core.jdbc.BasicDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pff.cd.model.CDSettlementProcess;
import com.pennanttech.pff.constants.AccountingEvent;
import com.pennanttech.pff.constants.FinServiceEvent;
import com.pennanttech.pff.subvention.dao.SubventionProcessDAO;

public class SubventionResponceUpload extends BasicDao<CDSettlementProcess> implements ProcessRecord {
	private SubventionProcessDAO subventionProcessDAO;
	private DataSource dataSource;
	private FinanceMainDAO financeMainDAO;
	private ExtendedFieldDetailsService extendedFieldDetailsService;
	private PlatformTransactionManager transactionManager;
	private FinFeeDetailDAO finFeeDetailDAO;
	private FeeTypeDAO feeTypeDAO;
	private PromotionDAO promotionDAO;
	private ManualAdviseDAO manualAdviseDAO;
	private PostingsDAO postingsDAO;
	private PostingsPreparationUtil postingsPreparationUtil;
	private CashBackDetailDAO cashBackDetailDAO;
	private CashBackProcessService cashBackProcessService;

	public SubventionResponceUpload() {
		super();
	}

	public void subventionFileUploadProcessResponseFile(Object... params) throws Exception {
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

	}

	@Override
	public void saveOrUpdate(DataEngineAttributes attributes, MapSqlParameterSource record, Table table) {
		TransactionStatus txStatus = null;
		// StringBuilder remarks = null;

		try {

			MapSqlParameterSource subventionMapdata = new MapSqlParameterSource();

			subventionMapdata.addValue("HostReference", (String) record.getValue("HOSTREFERENCE"));
			subventionMapdata.addValue("Issuer", record.getValue("ISSUER"));
			subventionMapdata.addValue("Acquirer", record.getValue("ACQUIRER"));
			subventionMapdata.addValue("Merchantusername", record.getValue("MERCHANTUSERNAME"));
			subventionMapdata.addValue("Manufacturername", record.getValue("MANUFACTURERNAME"));
			subventionMapdata.addValue("Storename", record.getValue("STORENAME"));
			subventionMapdata.addValue("Storecity", record.getValue("STORECITY"));
			subventionMapdata.addValue("Storestate", record.getValue("STORESTATE"));
			subventionMapdata.addValue("Manufactureid", record.getValue("MANUFACTUREID"));
			subventionMapdata.addValue("Terminalid", record.getValue("TERMINALID"));
			subventionMapdata.addValue("Transactiondatetime", SysParamUtil.getAppDate());
			subventionMapdata.addValue("Settlementdatetime", SysParamUtil.getAppDate());
			subventionMapdata.addValue("Transactionamount", record.getValue("TRANSACTIONAMOUNT"));
			subventionMapdata.addValue("Txnstatus", record.getValue("TXNSTATUS"));
			subventionMapdata.addValue("Productcategory", record.getValue("PRODUCTCATEGORY"));
			subventionMapdata.addValue("Subcat1", record.getValue("SUBCAT1"));
			subventionMapdata.addValue("Subcat2", record.getValue("SUBCAT2"));
			subventionMapdata.addValue("Subcat3", record.getValue("SUBCAT3"));
			subventionMapdata.addValue("Productsrno", record.getValue("PRODUCTSRNO"));

			try {
				subventionMapdata.addValue("Emioffer", record.getValue("EMIOFFER"));
			} catch (NumberFormatException e) {
				throw new AppException("Invalid Number formate EMIOFFER");
			}
			try {
				subventionMapdata.addValue("Rrn", record.getValue("RRN"));
			} catch (NumberFormatException e) {
				throw new AppException("Invalid Number formate BANKRRN");
			}
			try {
				subventionMapdata.addValue("Bankapprovalcode", record.getValue("BANKAPPROVALCODE"));
			} catch (NumberFormatException e) {
				throw new AppException("Invalid Number formate BANKAPPROVALCODE");
			}
			try {
				subventionMapdata.addValue("Cardhash", record.getValue("CARDHASH"));
			} catch (NumberFormatException e) {
				throw new AppException("Invalid Number formate CARDHASH");
			}
			try {
				subventionMapdata.addValue("Emimodel", record.getValue("EMIMODEL"));
			} catch (NumberFormatException e) {
				throw new AppException("Invalid Number formate EMIMODEL");
			}
			try {
				subventionMapdata.addValue("Posid", record.getValue("POSID"));
			} catch (NumberFormatException e) {
				throw new AppException("Invalid Number formate POSID");
			}
			try {
				subventionMapdata.addValue("Discountrate", record.getValue("DISCOUNTRATE"));
			} catch (NumberFormatException e) {
				throw new AppException("Invalid Number formate DISCOUNTRATE");
			}
			try {
				if (!StringUtils.isEmpty(record.getValue("DISCOUNTAMOUNT").toString())) {
					subventionMapdata.addValue("Discountamount", record.getValue("DISCOUNTAMOUNT"));
				} else {
					subventionMapdata.addValue("Discountamount", BigDecimal.ZERO);
				}
			} catch (NumberFormatException e) {
				throw new AppException("Invalid Number formate DISCOUNTAMOUNT");
			}
			try {
				subventionMapdata.addValue("Cashbackrate", record.getValue("CASHBACKRATE"));
			} catch (NumberFormatException e) {
				throw new AppException("Invalid Number formate CASHBACKRATE");
			}
			try {
				if (!StringUtils.isEmpty(record.getValue("CASHBACKAMOUNT").toString())) {
					subventionMapdata.addValue("Cashbackamount", record.getValue("CASHBACKAMOUNT"));
				} else {
					subventionMapdata.addValue("Cashbackamount", BigDecimal.ZERO);
				}
			} catch (NumberFormatException e) {
				throw new AppException("Invalid Number formate CASHBACKAMOUNT");
			}
			try {
				subventionMapdata.addValue("Nbfccashbackrate", record.getValue("NBFCCASHBACKRATE"));
			} catch (NumberFormatException e) {
				throw new AppException("Invalid Number formate NBFCCASHBACKRATE");
			}

			try {
				if (!StringUtils.isEmpty(record.getValue("NBFCCASHBACKAMOUNT").toString())) {
					subventionMapdata.addValue("Nbfccashbackamount", record.getValue("NBFCCASHBACKAMOUNT"));
				} else {
					subventionMapdata.addValue("Nbfccashbackamount", BigDecimal.ZERO);
				}
			} catch (NumberFormatException e) {
				throw new AppException("Invalid Number formate NBFCCASHBACKAMOUNT");
			}

			DefaultTransactionDefinition txDef = new DefaultTransactionDefinition();
			txDef.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);

			txStatus = this.transactionManager.getTransaction(txDef);

			// Upload Details Validation
			validate(subventionMapdata);

			// MDB Amount Accounting Process
			FinanceMain finMain = financeMainDAO
					.getFinanceMainByHostReference(String.valueOf(subventionMapdata.getValue("HostReference")), true);

			Promotion promotion = promotionDAO.getPromotionByReferenceId(finMain.getPromotionSeqId(), "");
			FeeType feeType = feeTypeDAO.getFeeTypeById(promotion.getMbdFeeTypId(), "");
			BigDecimal mbdAmount = null;
			BigDecimal dbdAmount = null;

			dbdAmount = finMain.getFinAmount().multiply(promotion.getDbdPerc()).divide(new BigDecimal(100), 0,
					RoundingMode.HALF_DOWN);
			mbdAmount = finMain.getSvAmount().subtract(dbdAmount);

			long linkedTranId = 0;
			if (finMain != null) {
				linkedTranId = executeAccountingProcess(finMain.getFinID(), finMain.getFinBranch(), mbdAmount);
			}
			subventionMapdata.addValue("LinkedTranId", linkedTranId);
			subventionProcessDAO.saveSubventionProcessRequest(subventionMapdata);

			// DBD Payment/Receipt Creation

			if (promotion.isDbd() && !promotion.isDbdRtnd()) {

				Date appDate = SysParamUtil.getAppDate();
				Date cbDate = DateUtil.addMonths(finMain.getFinStartDate(), promotion.getDlrCbToCust());

				if (DateUtil.compare(appDate, cbDate) >= 0) {

					feeType = feeTypeDAO.getFeeTypeById(promotion.getDbdFeeTypId(), "");
					CashBackDetail cashBackDetail = cashBackDetailDAO
							.getManualAdviseIdByFinReference(finMain.getFinID(), "DBD");
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

			// Cash Back amount adjustments
			if (promotion.isMbd() && !promotion.isMbdRtnd()) {

				Date appDate = SysParamUtil.getAppDate();
				Date cbDate = DateUtil.addMonths(finMain.getFinStartDate(), promotion.getMnfCbToCust());

				if (DateUtil.compare(appDate, cbDate) >= 0) {

					CashBackDetail cashBackDetail = cashBackDetailDAO
							.getManualAdviseIdByFinReference(finMain.getFinID(), "MBD");
					if (cashBackDetail == null) {
						cashBackDetail = cashBackDetailDAO.getManualAdviseIdByFinReference(finMain.getFinID(), "DBMBD");
						feeType = feeTypeDAO.getFeeTypeById(promotion.getDbdAndMbdFeeTypId(), "");
					}

					if (cashBackDetail != null && cashBackDetail.getAdviseId() > 0) {

						finMain.setLastMntBy(1000);
						finMain.setLastMntOn(new Timestamp(System.currentTimeMillis()));
						finMain.setFinCcy(SysParamUtil.getAppCurrency());
						BigDecimal balAmount = cashBackDetail.getAmount();
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
			this.transactionManager.commit(txStatus);

		} catch (Exception e) {
			this.transactionManager.rollback(txStatus);
			throw new AppException(e.getMessage());
		}

	}

	private void validate(MapSqlParameterSource subventionMapdata) {
		FinanceMain finMain = null;
		if (subventionMapdata.getValue("HostReference") == null
				|| subventionMapdata.getValue("HostReference").equals("")) {
			throw new AppException("HostReference is mandatory");
		} else {
			finMain = financeMainDAO
					.getFinanceMainByHostReference(String.valueOf(subventionMapdata.getValue("HostReference")), true);
			if (finMain == null) {
				throw new AppException("HostReference is not avilable in PLF or inactive");
			}
		}
		List<FinFeeDetail> feeList = finFeeDetailDAO.getDMFinFeeDetailByFinRef(finMain.getFinID(), "");
		BigDecimal feeAmount = BigDecimal.ZERO;

		for (FinFeeDetail finFeeDetail : feeList) {
			if (finFeeDetail.isOriginationFee()) {
				feeAmount = feeAmount.add(finFeeDetail.getActualAmount());
			}
		}
		BigDecimal downPayment = finMain.getDownPayment().add(feeAmount);
		BigDecimal tranAmount = finMain.getFinAmount().subtract(downPayment);
		subventionMapdata.addValue("TransactionAmount", tranAmount);
		List<ExtendedField> extData = new ArrayList<>();

		if (subventionMapdata.getValue("Terminalid") == null) {
			throw new AppException("TID is mandatory");
		} else {

		}
		if (subventionMapdata.getValue("Manufactureid") == null) {
			throw new AppException("MID is mandatory");
		}

		if (subventionMapdata.getValue("HostReference") != null) {
			boolean isDuplicateHostRef = subventionProcessDAO
					.isDuplicateHostReference(subventionMapdata.getValue("HostReference").toString());
			if (isDuplicateHostRef) {
				throw new AppException("RRN already Processed");
			}
		}

		if (subventionMapdata.getValue("Terminalid") != null && subventionMapdata.getValue("Manufactureid") != null) {
			if (finMain != null) {
				extData = extendedFieldDetailsService.getExtndedFieldDetails(ExtendedFieldConstants.MODULE_LOAN,
						finMain.getFinCategory(), FinServiceEvent.ORG, finMain.getFinReference());
			}
			Map<String, Object> mapValues = new HashMap<String, Object>();
			if (extData != null) {
				for (ExtendedField extendedField : extData) {
					for (ExtendedFieldData extFieldData : extendedField.getExtendedFieldDataList()) {
						mapValues.put(extFieldData.getFieldName().toUpperCase(), extFieldData.getFieldValue());
					}
				}
			}

			if (mapValues.get("MID") == null) {
				throw new AppException("MID is null.");
			}

			String mid = (String) mapValues.get("MID");
			if (!StringUtils.equals(mid, subventionMapdata.getValue("Manufactureid").toString())) {
				throw new AppException("In valid MID");
			}

			if (mapValues.get("TID") == null) {
				throw new AppException("TID is null.");
			}

			String tid = (String) mapValues.get("TID");
			if (!StringUtils.equals(tid, subventionMapdata.getValue("Terminalid").toString())) {
				throw new AppException("In valid TID");
			}
		}
	}

	/**
	 * 
	 * @param feeTypeCode
	 * @param mbdAmount
	 * @param manualAdvise
	 * @return
	 */
	private AEEvent prepareAccSetData(long finID, String postBranch, BigDecimal mbdAmount) {
		logger.debug(Literal.ENTERING);

		AEEvent aeEvent = new AEEvent();

		aeEvent.setAccountingEvent(AccountingEvent.OEMSBV);
		AEAmountCodes amountCodes = aeEvent.getAeAmountCodes();
		if (amountCodes == null) {
			amountCodes = new AEAmountCodes();
		}

		// Finance main
		FinanceMain financeMain = manualAdviseDAO.getFinanceDetails(finID);
		amountCodes.setFinType(financeMain.getFinType());

		aeEvent.setPostingUserBranch(postBranch);
		aeEvent.setValueDate(SysParamUtil.getAppDate());
		aeEvent.setPostDate(SysParamUtil.getAppDate());
		aeEvent.setEntityCode(financeMain.getEntityCode());

		aeEvent.setBranch(financeMain.getFinBranch());
		aeEvent.setCustID(financeMain.getCustID());
		aeEvent.setCcy(financeMain.getFinCcy());
		aeEvent.setFinReference(financeMain.getFinReference());
		aeEvent.setDataMap(amountCodes.getDeclaredFieldValues());
		Map<String, Object> eventMapping = aeEvent.getDataMap();

		eventMapping.put("ae_oemSbvAmount", mbdAmount);
		aeEvent.setDataMap(eventMapping);
		Long accountsetId = AccountingEngine.getAccountSetID(financeMain, AccountingEvent.OEMSBV,
				FinanceConstants.MODULEID_FINTYPE);

		if (accountsetId != null && accountsetId > 0) {
			aeEvent.getAcSetIDList().add(accountsetId);
		}

		logger.debug(Literal.LEAVING);
		return aeEvent;
	}

	/**
	 * Method for Execute posting Details on Core Banking Side
	 * 
	 * @param mbdAmount
	 * 
	 * @param feeType
	 * 
	 * @param auditHeader
	 * @param appDate
	 * @return
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 * @throws AccountNotFoundException
	 */
	private long executeAccountingProcess(long finID, String postBranch, BigDecimal mbdAmount) {
		logger.debug(Literal.ENTERING);

		AEEvent aeEvent = prepareAccSetData(finID, postBranch, mbdAmount);
		aeEvent = postingsPreparationUtil.postAccounting(aeEvent);

		if (!aeEvent.isPostingSucess()) {
			throw new InterfaceException("9998", "Advise Due accounting postings failed. Please ");
		}
		return aeEvent.getLinkedTranId();
	}

	/**
	 * Method to Prepare the accounting entries and save the postings to the Postings and accounts table
	 * 
	 * @param aeEvent
	 * @param dataMap
	 * @return
	 */
	public AEEvent postAccounting(AEEvent aeEvent) {
		logger.debug("Entering");

		if (aeEvent.getLinkedTranId() <= 0) {
			aeEvent.setLinkedTranId(postingsDAO.getLinkedTransId());
		}

		postingsPreparationUtil.getEngineExecution().getAccEngineExecResults(aeEvent);

		postingsPreparationUtil.validateCreditandDebitAmounts(aeEvent);

		List<ReturnDataSet> returnDatasetList = aeEvent.getReturnDataSet();
		if (!aeEvent.isPostingSucess()) {
			return aeEvent;
		}

		if (returnDatasetList == null || returnDatasetList.isEmpty()) {
			return aeEvent;
		}

		postingsDAO.saveBatch(returnDatasetList);

		logger.debug("Leaving");
		return aeEvent;
	}

	@Autowired
	public void setSubventionProcessDAO(SubventionProcessDAO subventionProcessDAO) {
		this.subventionProcessDAO = subventionProcessDAO;
	}

	@Override
	@Autowired
	public void setDataSource(DataSource dataSource) {
		super.setDataSource(dataSource);
		this.dataSource = dataSource;
	}

	@Autowired
	public void setFinanceMainDAO(FinanceMainDAO financeMainDAO) {
		this.financeMainDAO = financeMainDAO;
	}

	@Autowired
	public void setExtendedFieldDetailsService(ExtendedFieldDetailsService extendedFieldDetailsService) {
		this.extendedFieldDetailsService = extendedFieldDetailsService;
	}

	@Autowired
	public void setTransactionManager(PlatformTransactionManager transactionManager) {
		this.transactionManager = transactionManager;
	}

	@Autowired
	public void setFinFeeDetailDAO(FinFeeDetailDAO finFeeDetailDAO) {
		this.finFeeDetailDAO = finFeeDetailDAO;
	}

	@Autowired
	public void setFeeTypeDAO(FeeTypeDAO feeTypeDAO) {
		this.feeTypeDAO = feeTypeDAO;
	}

	@Autowired
	public void setPromotionDAO(PromotionDAO promotionDAO) {
		this.promotionDAO = promotionDAO;
	}

	@Autowired
	public void setManualAdviseDAO(ManualAdviseDAO manualAdviseDAO) {
		this.manualAdviseDAO = manualAdviseDAO;
	}

	@Autowired
	public void setPostingsDAO(PostingsDAO postingsDAO) {
		this.postingsDAO = postingsDAO;
	}

	@Autowired
	public void setPostingsPreparationUtil(PostingsPreparationUtil postingsPreparationUtil) {
		this.postingsPreparationUtil = postingsPreparationUtil;
	}

	@Autowired
	public void setCashBackDetailDAO(CashBackDetailDAO cashBackDetailDAO) {
		this.cashBackDetailDAO = cashBackDetailDAO;
	}

	@Autowired
	public void setCashBackProcessService(CashBackProcessService cashBackProcessService) {
		this.cashBackProcessService = cashBackProcessService;
	}
}
