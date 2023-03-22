package com.pennant.pff.service.subvention;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import com.pennant.app.util.GSTCalculator;
import com.pennant.app.util.PostingsPreparationUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.app.util.TDSCalculator;
import com.pennant.backend.dao.finance.FinanceTaxDetailDAO;
import com.pennant.backend.dao.finance.TaxHeaderDetailsDAO;
import com.pennant.backend.dao.partnerbank.PartnerBankDAO;
import com.pennant.backend.dao.rulefactory.PostingsDAO;
import com.pennant.backend.model.finance.FinFeeDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.TaxHeader;
import com.pennant.backend.model.finance.Taxes;
import com.pennant.backend.model.finance.financetaxdetail.FinanceTaxDetail;
import com.pennant.backend.model.partnerbank.PartnerBank;
import com.pennant.backend.model.rulefactory.AEAmountCodes;
import com.pennant.backend.model.rulefactory.AEEvent;
import com.pennant.backend.model.rulefactory.ReturnDataSet;
import com.pennant.backend.service.finance.FinFeeDetailService;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.SMTParameterConstants;
import com.pennant.cache.util.AccountingConfigCache;
import com.pennant.pff.dao.subvention.SubventionUploadDAO;
import com.pennant.pff.model.subvention.Subvention;
import com.pennant.pff.model.subvention.SubventionHeader;
import com.pennanttech.pennapps.core.App;
import com.pennanttech.pennapps.core.App.Type;
import com.pennanttech.pennapps.core.InterfaceException;
import com.pennanttech.pennapps.core.jdbc.BasicDao;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pff.constants.AccountingEvent;

public class SubventionKnockOffService extends BasicDao<Subvention> {
	private SubventionUploadDAO subventionUploadDAO;
	private PostingsDAO postingsDAO;
	private PostingsPreparationUtil postingsPreparationUtil;
	private PlatformTransactionManager transactionManager;
	private FinanceTaxDetailDAO financeTaxDetailDAO;
	private TaxHeaderDetailsDAO taxHeaderDetailsDAO;
	private FinFeeDetailService finFeeDetailService;
	private PartnerBankDAO partnerBankDAO;

	public boolean isFileExists(String fileName) {
		return subventionUploadDAO.isFileExists(fileName);
	}

	public long saveHeader(String ref, String entityCode) {
		return subventionUploadDAO.saveSubventionHeader(ref, entityCode);
	}

	public void process(SubventionHeader header) throws Exception {
		if (App.TYPE == Type.WEB) {
			header.setStatus(header.getDeStatus().getStatus());
			header.setTotalRecords((int) header.getDeStatus().getTotalRecords());
		}

		List<Subvention> subventions = header.getSubventions();

		int totalRecords = header.getTotalRecords();
		if (totalRecords != subventions.size()) {
			header.setFailureRecords(totalRecords - subventions.size());
		}

		if (subventions == null || subventions.isEmpty()) {
			header.setStatus("F");
			subventionUploadDAO.updateRemarks(header);
			return;
		}

		try {
			setFinancedetails(header);

			validate(header);

			prosesssubvensions(header);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		} finally {
			if (totalRecords == header.getFailureRecords()) {
				header.setStatus("F");
				if (App.TYPE == Type.WEB) {
					header.getDeStatus().setStatus("F");
				}
			} else {
				header.setStatus("S");
			}
			subventionUploadDAO.updateRemarks(header);
			for (Subvention subvention : subventions) {
				if (!subvention.getErrorDetails().isEmpty()) {
					subventionUploadDAO.logSubvention(subvention.getErrorDetails(), subvention.getId());
				}

			}
			if (App.TYPE == Type.WEB) {
				subventionUploadDAO.updateDeRemarks(header, header.getDeStatus());
			}
		}
	}

	private void setFinancedetails(SubventionHeader subvention) {
		Long batchId = subvention.getId();
		String procFeeCode = SysParamUtil.getValueAsString(SMTParameterConstants.FEE_LOS_PROCESSING);

		logger.info("Extracting finance details...");

		List<FinanceMain> finMain = subventionUploadDAO.getFinanceMain(batchId);
		String error = "Loan is Not Active.";
		for (Subvention subv : subvention.getSubventions()) {
			StringBuilder remarks = new StringBuilder(StringUtils.trimToEmpty(subv.getRemarks()));
			for (FinanceMain fm : finMain) {
				if (subv.getFinReference().equals(fm.getFinReference())) {
					subv.setFinanceMain(fm);
					if (!fm.isFinIsActive()) {
						if (remarks.length() > 0) {
							remarks.append(", ");
						}
						remarks.append(error);
						subv.setErrorDetail(getErrorDetail("SUBV002", error));
					}
					break;
				}
			}
			subv.setRemarks(remarks.toString());
		}

		error = "Loan not exists.";
		for (Subvention subv : subvention.getSubventions()) {
			StringBuilder remarks = new StringBuilder(subv.getRemarks());
			if (subv.getFinanceMain() == null) {
				if (remarks.length() > 0) {
					remarks.append(", ");
				}
				remarks.append(error);
				subv.setRemarks(remarks.toString());
				subv.setErrorDetail(getErrorDetail("SUBV002", error));
			}
		}

		logger.info("Extracting subvension fee details...");
		List<FinFeeDetail> subvfeeList = subventionUploadDAO.getFinFeeDetails(batchId,
				PennantConstants.FEETYPE_SUBVENTION);
		for (Subvention subv : subvention.getSubventions()) {
			for (FinFeeDetail fee : subvfeeList) {
				if (subv.getFinReference().equals(fee.getFinReference())) {
					subv.setSubvensionFee(fee);
				}
			}
		}

		error = "Subvension fees not exists.";
		for (Subvention subv : subvention.getSubventions()) {
			StringBuilder remarks = new StringBuilder(subv.getRemarks());
			if (subv.getSubvensionFee() == null) {
				if (remarks.length() > 0) {
					remarks.append(", ");
				}
				remarks.append(error);
				subv.setRemarks(remarks.toString());
				subv.setErrorDetail(getErrorDetail("SUBV001", error));
			}
		}

		logger.info("Extracting Processing fee details...");
		for (Subvention subv : subvention.getSubventions()) {
			int count = subventionUploadDAO.getSucessCount(subv.getFinID(), "S");
			if (count == 0 && procFeeCode != null) {
				List<FinFeeDetail> pffeeList = subventionUploadDAO.getFinFeeDetails(batchId, procFeeCode);
				for (FinFeeDetail fee : pffeeList) {
					if (subv.getFinReference().equals(fee.getFinReference())) {
						subv.setProcessingFee(fee);
					}
				}
			}
		}

		logger.info("Extracting partner bank details...");

		for (Subvention subv : subvention.getSubventions()) {
			if (subv.getPartnerBankId() != null) {
				PartnerBank bankDetails = partnerBankDAO.getPartnerBankById(subv.getPartnerBankId(), "");
				subv.setPartnerBank(bankDetails);
			}
		}
	}

	private void prosesssubvensions(SubventionHeader header) {
		int fail = header.getFailureRecords();
		int sucess = header.getSucessRecords();
		for (Subvention subvention : header.getSubventions()) {
			TransactionStatus txStatus = null;
			String finReference = subvention.getFinReference();
			long finID = subvention.getFinID();
			String status = subvention.getStatus();
			logger.info("Processing subvension fee for >> {}:", finReference);
			if ("F".equals(status)) {
				subventionUploadDAO.updateSubventionDetails(subvention);
				fail++;
				continue;
			}

			FinanceMain fm = subvention.getFinanceMain();
			BigDecimal subvAmt = subvention.getAmount();
			FinFeeDetail subvensionFee = subvention.getSubvensionFee();

			try {
				DefaultTransactionDefinition txDef = new DefaultTransactionDefinition();
				txDef.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);

				txStatus = this.transactionManager.getTransaction(txDef);

				processFinfeeDetails(subvensionFee, subvAmt, fm, header.getUserBranch());

				Subvention gstDetails = subventionUploadDAO.getGstDetails(finID);

				subventionUploadDAO.updateFinFeeDetails(finID, subvensionFee);

				if (gstDetails == null) {
					gstDetails = new Subvention();
				}

				List<Taxes> taxDetails = subvensionFee.getTaxHeader().getTaxDetails();

				for (Taxes taxes : taxDetails) {
					taxHeaderDetailsDAO.update(taxes, "");
					switch (taxes.getTaxType()) {
					case "CGST":
						subvention.setCgstAmt(taxes.getPaidTax().subtract(gstDetails.getCgstAmt()));
						break;
					case "SGST":
						subvention.setSgstAmt(taxes.getPaidTax().subtract(gstDetails.getSgstAmt()));
						break;
					case "UGST":
						subvention.setUgstAmt(taxes.getPaidTax().subtract(gstDetails.getUgstAmt()));
						break;
					case "IGST":
						subvention.setIgstAmt(taxes.getPaidTax().subtract(gstDetails.getIgstAmt()));
						break;
					case "CESS":
						subvention.setCessAmt(taxes.getPaidTax().subtract(gstDetails.getCessAmt()));
						break;
					}
				}

				Long linkedTranId = executeAccountingProcess(subvention, subvAmt);
				subvention.setLinkedTranId(linkedTranId);
				subvention.setStatus("S");
				sucess++;
				subventionUploadDAO.updateSubventionDetails(subvention);

				this.transactionManager.commit(txStatus);
				logger.info("Completed Processing subvension fee  for >> {}:", finReference);
			} catch (Exception e) {
				this.transactionManager.rollback(txStatus);
				logger.info("unable to process fee.");
				subvention.setStatus("F");
				subvention.setErrorDetail(getErrorDetail("SUBV011", "unable to process fee."));
				fail++;
				subventionUploadDAO.updateSubventionDetails(subvention);
			}
		}

		header.setFailureRecords(fail);
		header.setSucessRecords(sucess);
	}

	private void processFinfeeDetails(FinFeeDetail fee, BigDecimal amount, FinanceMain financeMain, String userBranch) {
		logger.info("Calculating Taxes for Fee....");
		fee.setPaidAmount(fee.getPaidAmount().add(amount));

		if (financeMain.isTDSApplicable() && fee.isTdsReq()) {
			fee.setPaidTDS(TDSCalculator.getTDSAmount(amount));
		}

		Map<String, BigDecimal> dealerPercentages = getDealerTaxPercentages(financeMain, userBranch);

		Long headerId = fee.getTaxHeaderId();

		if (headerId != null && headerId > 0) {
			List<Taxes> taxDetails = taxHeaderDetailsDAO.getTaxDetailById(headerId, "");
			TaxHeader taxheader = new TaxHeader();
			taxheader.setTaxDetails(taxDetails);
			taxheader.setHeaderId(headerId);
			fee.setTaxHeader(taxheader);
		}

		fee.setPaidCalcReq(true);
		fee.setTaxComponent(FinanceConstants.FEE_TAXCOMPONENT_INCLUSIVE);
		finFeeDetailService.calculateFees(fee, financeMain, dealerPercentages);

		logger.info("Tax Claculation completed....");
	}

	private Map<String, BigDecimal> getDealerTaxPercentages(FinanceMain finMain, String userBranch) {
		FinanceTaxDetail finTaxDetail = financeTaxDetailDAO.getFinanceTaxDetail(finMain.getFinID(), "");
		String finBranch = finMain.getFinBranch();
		String finCCY = finMain.getFinCcy();

		Map<String, BigDecimal> taxPercentages = GSTCalculator.getDealerTaxPercentages(
				finMain.getManufacturerDealerId(), finCCY, userBranch, finBranch, finTaxDetail);

		return taxPercentages;
	}

	private ErrorDetail getErrorDetail(String code, String message) {
		ErrorDetail ed = new ErrorDetail();
		ed.setCode(code);
		ed.setMessage(message);
		return ed;
	}

	private void validate(SubventionHeader subventiona) {
		logger.info("Validationg the records...");

		Date appDate = SysParamUtil.getAppDate();

		for (Subvention subvention : subventiona.getSubventions()) {
			FinanceMain finMain = subvention.getFinanceMain();
			FinFeeDetail fee = subvention.getSubvensionFee();

			StringBuilder remarks = new StringBuilder(subvention.getRemarks());

			if (finMain.getSubVentionFrom() == null) {
				String error = "Subvention is not applicable.";
				if (remarks.length() > 0) {
					remarks.append(", ");
				}

				remarks.append(error);
				subvention.setRemarks(remarks.toString());
				subvention.setStatus("F");
				subvention.setErrorDetail(getErrorDetail("SUBV010", error));
				continue;
			}

			if (!(finMain.getFinType().equalsIgnoreCase(subvention.getFinType()))) {
				String error = "FinType is not matched with Finreference.";
				if (remarks.length() > 0) {
					remarks.append(", ");
				}
				remarks.append(error);
				subvention.setErrorDetail(getErrorDetail("SUBV004", error));
			}

			if (subvention.getReferenceCode() == null) {
				String error = "Dealer/Manufacturer Code is mandatory";
				if (remarks.length() > 0) {
					remarks.append(", ");
				}
				remarks.append(error);
				subvention.setRemarks(remarks.toString());
				subvention.setStatus("F");
				subvention.setErrorDetail(getErrorDetail("SUBV006", error));
				continue;
			}

			if (!(finMain.getManufacturerDealerCode().equals(subvention.getReferenceCode()))) {
				String error = "Manufacturer/Dealer Code is not matched with FinReference";
				if (remarks.length() > 0) {
					remarks.append(", ");
				}
				remarks.append(error);
				subvention.setRemarks(remarks.toString());
				subvention.setErrorDetail(getErrorDetail("SUBV004", error));
			} else {
				BigDecimal reminingFee = fee.getRemainingFee();
				String rmfee = PennantApplicationUtil.amountFormate(reminingFee, 2);
				BigDecimal subvAmt = subvention.getAmount();

				if (subvAmt.compareTo(BigDecimal.ZERO) == 0) {
					String error = "Amount Should be greater than Zero";
					if (remarks.length() > 0) {
						remarks.append(", ");
					}
					remarks.append(error);
					subvention.setRemarks(remarks.toString());
					subvention.setErrorDetail(getErrorDetail("SUBV003", error));
				}
				if (reminingFee.compareTo(subvAmt) < 0) {
					String error = "Amount Should be Less than/Equal to RemainingFee: " + rmfee;
					if (remarks.length() > 0) {
						remarks.append(", ");
					}
					remarks.append(error);
					subvention.setRemarks(remarks.toString());
					subvention.setErrorDetail(getErrorDetail("SUBV003", error));
				}

			}

			Date valueDate = subvention.getValueDate();
			if (DateUtil.compare(valueDate, appDate) == 1) {
				String error = "Value Date " + DateUtil.formatToShortDate(valueDate)
						+ " Should be Before or Same as  Application Date " + DateUtil.formatToShortDate(appDate);
				if (remarks.length() > 0) {
					remarks.append(", ");
				}
				remarks.append(error);
				subvention.setErrorDetail(getErrorDetail("SUBV009", error));
			}

			Date postDate = subvention.getPostDate();
			if (DateUtil.compare(postDate, appDate) != 0) {
				String error = "Posting Date " + DateUtil.formatToShortDate(postDate)
						+ " Should be Same as Application Date " + DateUtil.formatToShortDate(appDate);
				if (remarks.length() > 0) {
					remarks.append(", ");
				}
				remarks.append(error);
				subvention.setErrorDetail(getErrorDetail("SUBV008", error));
			}

			Long partnerBankId = subvention.getPartnerBankId();
			String partnerAccNo = StringUtils.trimToEmpty(subvention.getPartnerAccNo());

			if (partnerBankId != null && partnerBankId > 0) {
				PartnerBank bankDetails = subvention.getPartnerBank();

				if (bankDetails == null) {
					String error = "PartnerBankId is not avilable";
					if (remarks.length() > 0) {
						remarks.append(", ");
					}
					remarks.append(error);
					subvention.setErrorDetail(getErrorDetail("SUBV004", error));
				}
				if (StringUtils.isEmpty(partnerAccNo)) {
					String error = "Partner bank account number is mandatory";
					if (remarks.length() > 0) {
						remarks.append(", ");
					}
					remarks.append(error);
					subvention.setErrorDetail(getErrorDetail("SUBV006", error));
				}
				if (!partnerAccNo.equals(bankDetails.getAccountNo())) {
					String error = "Partner bank account number is not matched with bank details";
					if (remarks.length() > 0) {
						remarks.append(", ");
					}
					remarks.append("Partner bank account number is not matched with bank details");
					subvention.setErrorDetail(getErrorDetail("SUBV007", error));
				}

			} else {
				if (!partnerAccNo.isEmpty()) {
					String error = "PartnerBankId is Mandatory";
					if (remarks.length() > 0) {
						remarks.append(", ");
					}
					remarks.append(error);
					subvention.setErrorDetail(getErrorDetail("SUBV006", error));
				}
			}

			if (remarks.length() > 0) {
				subvention.setRemarks(remarks.toString());
				subvention.setStatus("F");
			}
		}

	}

	private AEEvent prepareAccSetData(Subvention subvention, BigDecimal amount) {
		AEEvent aeEvent = new AEEvent();

		aeEvent.setAccountingEvent(AccountingEvent.OEMSBV);
		AEAmountCodes amountCodes = aeEvent.getAeAmountCodes();
		if (amountCodes == null) {
			amountCodes = new AEAmountCodes();
		}

		FinanceMain fm = subvention.getFinanceMain();
		// Finance main
		amountCodes.setFinType(fm.getFinType());

		aeEvent.setPostingUserBranch(fm.getFinBranch());
		Date appDate = SysParamUtil.getAppDate();
		aeEvent.setValueDate(appDate);
		aeEvent.setPostDate(appDate);
		aeEvent.setEntityCode(fm.getEntityCode());

		aeEvent.setBranch(fm.getFinBranch());
		aeEvent.setCustID(fm.getCustID());
		aeEvent.setCcy(fm.getFinCcy());

		Long finID = fm.getFinID();
		String finReference = fm.getFinReference();

		aeEvent.setFinID(finID);
		aeEvent.setFinReference(finReference);

		aeEvent.setDataMap(amountCodes.getDeclaredFieldValues());
		Map<String, Object> eventMapping = aeEvent.getDataMap();

		eventMapping.put("ae_oemSbvAmount", amount);
		BigDecimal procAmt = BigDecimal.ZERO;
		FinFeeDetail ProcFee = subvention.getProcessingFee();
		if (ProcFee != null) {
			procAmt = ProcFee.getNetAmount();
		}
		subvention.setProcFeeAmt(procAmt);
		eventMapping.put("ae_oemProcAmount", procAmt);

		aeEvent.setDataMap(eventMapping);
		long accountsetId = AccountingConfigCache.getAccountSetID(fm.getFinType(), AccountingEvent.OEMSBV,
				FinanceConstants.MODULEID_FINTYPE);
		aeEvent.getAcSetIDList().add(accountsetId);

		logger.debug(Literal.LEAVING);
		return aeEvent;
	}

	private long executeAccountingProcess(Subvention subvention, BigDecimal amount) {
		logger.info("Accounting process started...");

		AEEvent aeEvent = prepareAccSetData(subvention, amount);
		aeEvent = postingsPreparationUtil.postAccounting(aeEvent);

		if (!aeEvent.isPostingSucess()) {
			throw new InterfaceException("9998", "Advise Due accounting postings failed. Please ");
		}
		logger.info("Accounting process ended...");
		return aeEvent.getLinkedTranId();
	}

	public AEEvent postAccounting(AEEvent aeEvent) {
		logger.debug(Literal.ENTERING);

		boolean isNewTranID = false;
		if (aeEvent.getLinkedTranId() <= 0) {
			aeEvent.setLinkedTranId(postingsDAO.getLinkedTransId());
			isNewTranID = true;
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

		logger.debug(Literal.LEAVING);
		return aeEvent;
	}

	public void setSubventionUploadDAO(SubventionUploadDAO subventionUploadDAO) {
		this.subventionUploadDAO = subventionUploadDAO;
	}

	public void setPostingsDAO(PostingsDAO postingsDAO) {
		this.postingsDAO = postingsDAO;
	}

	public void setPostingsPreparationUtil(PostingsPreparationUtil postingsPreparationUtil) {
		this.postingsPreparationUtil = postingsPreparationUtil;
	}

	public void setTransactionManager(PlatformTransactionManager transactionManager) {
		this.transactionManager = transactionManager;
	}

	public void setFinanceTaxDetailDAO(FinanceTaxDetailDAO financeTaxDetailDAO) {
		this.financeTaxDetailDAO = financeTaxDetailDAO;
	}

	public void setTaxHeaderDetailsDAO(TaxHeaderDetailsDAO taxHeaderDetailsDAO) {
		this.taxHeaderDetailsDAO = taxHeaderDetailsDAO;
	}

	public void setFinFeeDetailService(FinFeeDetailService finFeeDetailService) {
		this.finFeeDetailService = finFeeDetailService;
	}

	public void setPartnerBankDAO(PartnerBankDAO partnerBankDAO) {
		this.partnerBankDAO = partnerBankDAO;
	}

}
