package com.pennant.backend.service.finance.impl;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.zkoss.util.resource.Labels;

import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.applicationmaster.AccountMappingDAO;
import com.pennant.backend.dao.applicationmaster.BranchDAO;
import com.pennant.backend.dao.applicationmaster.EntityDAO;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.customermasters.CustomerDAO;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.dao.finance.MiscPostingUploadDAO;
import com.pennant.backend.dao.others.JVPostingDAO;
import com.pennant.backend.dao.systemmasters.DivisionDetailDAO;
import com.pennant.backend.model.applicationmaster.AccountMapping;
import com.pennant.backend.model.applicationmaster.Branch;
import com.pennant.backend.model.applicationmaster.Entity;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.expenses.UploadHeader;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.miscPostingUpload.MiscPostingUpload;
import com.pennant.backend.model.others.JVPosting;
import com.pennant.backend.model.others.JVPostingEntry;
import com.pennant.backend.model.systemmasters.DivisionDetail;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.finance.MiscPostingUploadService;
import com.pennant.backend.service.others.JVPostingService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pff.core.TableType;

public class MiscPostingUploadServiceImpl extends GenericService<MiscPostingUpload>
		implements MiscPostingUploadService {
	private static final Logger logger = LogManager.getLogger(MiscPostingUploadServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;
	private MiscPostingUploadDAO miscPostingUploadDAO;
	private FinanceMainDAO financeMainDAO;
	private BranchDAO branchDAO;
	private CustomerDAO customerDAO;
	private AccountMappingDAO accountMappingDAO;
	private JVPostingDAO jVPostingDAO;
	private JVPostingService jVPostingService;
	private DivisionDetailDAO divisionDetailDAO;
	private EntityDAO entityDAO;
	private static final String DATE_FORMAT = "HH:mm:ss.SSS";
	private static final String REGIX = "[/:.\\s]";

	private long tempData = 0;

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	/**
	 * @return the auditHeaderDAO
	 */
	public AuditHeaderDAO getAuditHeaderDAO() {
		return auditHeaderDAO;
	}

	/**
	 * @param auditHeaderDAO the auditHeaderDAO to set
	 */
	public void setAuditHeaderDAO(AuditHeaderDAO auditHeaderDAO) {
		this.auditHeaderDAO = auditHeaderDAO;
	}

	/**
	 * @return the miscPostingUploadDAO
	 */
	public MiscPostingUploadDAO getMiscPostingUploadDAO() {
		return miscPostingUploadDAO;
	}

	/**
	 * @param miscPostingUploadDAO the miscPostingUploadDAO to set
	 */
	public void setMiscPostingUploadDAO(MiscPostingUploadDAO miscPostingUploadDAO) {
		this.miscPostingUploadDAO = miscPostingUploadDAO;
	}

	public FinanceMainDAO getFinanceMainDAO() {
		return financeMainDAO;
	}

	public void setFinanceMainDAO(FinanceMainDAO financeMainDAO) {
		this.financeMainDAO = financeMainDAO;
	}

	/**
	 * getMiscPostingUploadsById fetch the details by using MiscPostingUploadsDAO's getMiscPostingUploadByRef method.
	 * 
	 * @param uploadId (long)
	 * @return MiscPostingUploads
	 */
	@Override
	public List<MiscPostingUpload> getMiscPostingUploadsByUploadId(long uploadId) {
		return getMiscPostingUploadDAO().getMiscPostingUploadsByUploadId(uploadId, "");
	}

	@Override
	public List<MiscPostingUpload> validateMiscPostingUploads(UploadHeader uploadHeader) {
		logger.debug("Entering");
		int successCount = 0;
		int failCount = 0;
		List<MiscPostingUpload> miscPostingUploads = uploadHeader.getMiscPostingUploads();

		List<MiscPostingUpload> miscPostingUploadsSuccessList = new ArrayList<>();
		List<MiscPostingUpload> miscPostingUploadsFailedList = new ArrayList<>();
		for (MiscPostingUpload miscPostingUpload : miscPostingUploads) {
			miscPostingUpload = validation(miscPostingUpload);
			if ("SUCCESS".equals(miscPostingUpload.getUploadStatus())) {
				miscPostingUploadsSuccessList.add(miscPostingUpload);
			} else {
				miscPostingUploadsFailedList.add(miscPostingUpload);
			}
		}

		List<MiscPostingUpload> statusSuccessRecords = validateBasedonTransactionId(miscPostingUploadsSuccessList,
				uploadHeader);

		miscPostingUploads = new ArrayList<>();
		miscPostingUploads.addAll(miscPostingUploadsFailedList);
		miscPostingUploads.addAll(statusSuccessRecords);

		for (MiscPostingUpload miscPostingUpload : miscPostingUploads) {
			if ("SUCCESS".equals(miscPostingUpload.getUploadStatus())) {
				successCount++;
			} else {
				failCount++;
			}
		}
		// Success and failed count updation
		uploadHeader.setSuccessCount(successCount);
		uploadHeader.setFailedCount(failCount);
		uploadHeader.setTotalRecords(successCount + failCount);

		logger.debug("Leaving");

		return miscPostingUploads;
	}

	/**
	 * @param miscPostingUploadsSuccessList
	 * @return
	 */
	public List<MiscPostingUpload> validateBasedonTransactionId(List<MiscPostingUpload> miscPostingUploadsSuccessList,
			UploadHeader uploadHeader) {
		// sort by Transaction Id
		Collections.sort(miscPostingUploadsSuccessList, new SortByTransactionId());

		List<MiscPostingUpload> statusSuccessRecords = new ArrayList<>();
		List<MiscPostingUpload> miscPostingUploadList = new ArrayList<>();
		for (int i = 1; i < miscPostingUploadsSuccessList.size(); i++) {
			List<MiscPostingUpload> returnList = new ArrayList<>();

			int j = 0;
			if (i == 1) {
				miscPostingUploadList.add(miscPostingUploadsSuccessList.get(j));
			}

			if (miscPostingUploadsSuccessList.get(i).getTransactionId() == miscPostingUploadList.get(j)
					.getTransactionId()) {
				miscPostingUploadList.add(miscPostingUploadsSuccessList.get(i));
			} else {
				j = i;
				returnList = doProcessSetOfTransactionId(miscPostingUploadList, uploadHeader);
				miscPostingUploadList = new ArrayList<>();
				miscPostingUploadList.add(miscPostingUploadsSuccessList.get(i));
			}

			statusSuccessRecords.addAll(returnList);
		}

		// process when only one transaction is refered
		if (miscPostingUploadList != null && !miscPostingUploadList.isEmpty()) {
			statusSuccessRecords.addAll(doProcessSetOfTransactionId(miscPostingUploadList, uploadHeader));
		}

		return statusSuccessRecords;
	}

	/**
	 * @param miscPostingUploadsSuccessList
	 * @return
	 */
	public void getListBasedOnTransactionId(List<MiscPostingUpload> miscPostingUploadsSuccessList,
			UploadHeader uploadHeader) {
		logger.debug(Literal.ENTERING);

		// sort by Transaction Id
		Collections.sort(miscPostingUploadsSuccessList, new SortByTransactionId());

		List<MiscPostingUpload> miscPostingUploadList = new ArrayList<>();
		for (int i = 1; i < miscPostingUploadsSuccessList.size(); i++) {

			int j = 0;
			if (i == 1) {
				miscPostingUploadList.add(miscPostingUploadsSuccessList.get(j));
			}

			if (miscPostingUploadsSuccessList.get(i).getTransactionId() == miscPostingUploadList.get(j)
					.getTransactionId()) {
				miscPostingUploadList.add(miscPostingUploadsSuccessList.get(i));
			} else {
				j = i;
				doProcessJvPostings(miscPostingUploadList, uploadHeader);
				miscPostingUploadList = new ArrayList<>();
				miscPostingUploadList.add(miscPostingUploadsSuccessList.get(i));
			}
		}

		// for single transaction and for last transaction set
		if (miscPostingUploadList != null && !miscPostingUploadList.isEmpty()) {
			doProcessJvPostings(miscPostingUploadList, uploadHeader);
		}

		logger.debug(Literal.LEAVING);
	}

	private void doProcessJvPostings(List<MiscPostingUpload> miscPostingUploadList, UploadHeader uploadHeader) {
		logger.debug(Literal.ENTERING);

		AuditHeader auditHeaderJVPosting = new AuditHeader();
		AuditDetail auditDetailJVPosting = new AuditDetail();
		String currency = SysParamUtil.getValue("APP_DFT_CURR").toString();
		Date postingDate = SysParamUtil.getAppDate();
		JVPosting jVPosting = new JVPosting();
		AccountMapping accountMapping;
		BigDecimal creditAmount = BigDecimal.ZERO;
		BigDecimal debitAmount = BigDecimal.ZERO;
		int creditCount = 0;
		int debitCount = 0;

		List<JVPostingEntry> jVPostingEntryList = new ArrayList<JVPostingEntry>();
		for (MiscPostingUpload miscPostingUpload : miscPostingUploadList) {
			JVPostingEntry jVPostingEntry = new JVPostingEntry();
			// creating JVPostingEntry Bean
			jVPostingEntry.setBatchReference(Long.valueOf(miscPostingUpload.getMiscPostingId()));
			jVPostingEntry.setAccount(miscPostingUpload.getAccount());
			jVPostingEntry.setAccCCy(currency);
			accountMapping = accountMappingDAO.getAccountMapping(miscPostingUpload.getAccount(), "_AView");
			jVPostingEntry.setAcType(accountMapping.getAccountType());
			jVPostingEntry.setAccountName(accountMapping.getAccountTypeDesc());
			jVPostingEntry.setTxnCCy(currency);
			jVPostingEntry.setTxnAmount(miscPostingUpload.getTxnAmount());
			if (miscPostingUpload.getTxnEntry().equals("D")) {
				BigDecimal bigD = BigDecimal.ZERO.subtract(miscPostingUpload.getTxnAmount());
				jVPostingEntry.setTxnAmount_Ac(bigD);
				jVPostingEntry.setTxnCode("010");
				debitAmount = debitAmount.add(miscPostingUpload.getTxnAmount());
				debitCount++;
			} else {
				jVPostingEntry.setTxnAmount_Ac(miscPostingUpload.getTxnAmount());
				jVPostingEntry.setTxnCode("510");
				creditAmount = creditAmount.add(miscPostingUpload.getTxnAmount());
				creditCount++;
			}
			jVPostingEntry.setTxnEntry(miscPostingUpload.getTxnEntry());
			jVPostingEntry.setValueDate(miscPostingUpload.getValueDate());
			jVPostingEntry.setPostingDate(postingDate);
			jVPostingEntry.setNarrLine1(miscPostingUpload.getNarrLine1());
			jVPostingEntry.setNarrLine2(miscPostingUpload.getNarrLine2());
			jVPostingEntry.setNarrLine3(miscPostingUpload.getNarrLine3());
			jVPostingEntry.setNarrLine4(miscPostingUpload.getNarrLine4());

			jVPostingEntryList.add(jVPostingEntry);
		}

		jVPosting.setJVPostingEntrysList(jVPostingEntryList);
		// creating JVPosting Bean
		jVPosting.setBatchReference(Long.valueOf(miscPostingUploadList.get(0).getMiscPostingId()));
		jVPosting.setReference(miscPostingUploadList.get(0).getReference());
		jVPosting.setBatch(miscPostingUploadList.get(0).getBatch());
		jVPosting.setCurrency(currency);
		jVPosting.setBatchPurpose(miscPostingUploadList.get(0).getBatchPurpose());
		jVPosting.setBranch(miscPostingUploadList.get(0).getBranch());
		jVPosting.setPostingDate(postingDate);
		jVPosting.setPostAgainst(miscPostingUploadList.get(0).getPostAgainst());
		jVPosting.setPostingDivision(miscPostingUploadList.get(0).getPostingDivision());
		jVPosting.setCreditsCount(creditCount);
		jVPosting.setDebitCount(debitCount);
		jVPosting.setTotCreditsByBatchCcy(creditAmount);
		jVPosting.setTotDebitsByBatchCcy(debitAmount);
		jVPosting.setUserDetails(uploadHeader.getUserDetails());
		jVPosting.setLastMntBy(uploadHeader.getLastMntBy());
		jVPosting.setLastMntOn(uploadHeader.getLastMntOn());
		jVPosting.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);

		// creating AuditHeader Details
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		Map<String, List<AuditDetail>> auditDetailMap = new HashMap<String, List<AuditDetail>>();
		String auditTranType = "";
		String method = "doApprove";
		if (jVPosting.getJVPostingEntrysList() != null && jVPosting.getJVPostingEntrysList().size() > 0) {
			auditDetailMap.put("JVPostingEntry", setJVPostingEntryAuditData(jVPosting, auditTranType, method));
			auditDetails.addAll(auditDetailMap.get("JVPostingEntry"));

			jVPosting.setAuditDetailMap(auditDetailMap);
			auditHeaderJVPosting.setAuditDetail(auditDetailJVPosting);
			auditHeaderJVPosting.getAuditDetail().setModelData(jVPosting);
			auditHeaderJVPosting.setAuditModule("MiscPostingUpload");
			auditHeaderJVPosting.setAuditUsrId(uploadHeader.getUserDetails().getLoginLogId());
			auditHeaderJVPosting.setAuditBranchCode(uploadHeader.getUserDetails().getBranchCode());
			auditHeaderJVPosting.setAuditDeptCode(uploadHeader.getUserDetails().getDepartmentCode());
			auditHeaderJVPosting.setAuditReference(uploadHeader.getFileName());
			auditHeaderJVPosting.setAuditSystemIP(uploadHeader.getUserDetails().getIpAddress());
			auditHeaderJVPosting.setAuditSessionID(uploadHeader.getUserDetails().getSessionId());
			jVPostingService.processData(auditHeaderJVPosting, true); // inserting

			logger.debug(Literal.LEAVING);
		}
	}

	private List<MiscPostingUpload> doProcessSetOfTransactionId(List<MiscPostingUpload> miscPostingUploadList,
			UploadHeader uploadHeader) {
		logger.debug(Literal.ENTERING);

		String reason = "";
		for (int i = 1; i < miscPostingUploadList.size(); i++) {
			if (!(StringUtils.equals(miscPostingUploadList.get(i).getBranch(), miscPostingUploadList.get(0).getBranch())
					&& StringUtils.equals(miscPostingUploadList.get(i).getReference(),
							miscPostingUploadList.get(0).getReference())
					&& StringUtils.equals(miscPostingUploadList.get(i).getPostingDivision(),
							miscPostingUploadList.get(0).getPostingDivision())
					&& StringUtils.equals(miscPostingUploadList.get(i).getPostAgainst(),
							miscPostingUploadList.get(0).getPostAgainst()))) {
				reason = "Details Against Transaction Id in not matching";
				break;
			}
		}

		if (uploadHeader.isNewRecord()) {
			if (miscPostingUploadList != null && !miscPostingUploadList.isEmpty()) {

				String date = DateUtil.getSysDate(DATE_FORMAT);
				date = date.replaceAll(REGIX, "");
				long jobid = Long.valueOf(date);
				String stngValId = String.valueOf(jobid);

				String var_ = stngValId;
				if (stngValId.length() > 5) {
					var_ = stngValId.substring(0, 5);
				}

				if (tempData == 0) {
					tempData = Long.valueOf(var_);
				}

				if (tempData == Long.valueOf(var_)) {
					var_ = String.valueOf(tempData + 1);
				}

				tempData = Long.valueOf(var_);

				for (MiscPostingUpload upload : miscPostingUploadList) {
					if (StringUtils.equals(miscPostingUploadList.get(0).getPostAgainst(), "N")) {
						upload.setReference(var_);
					}
					upload.setBatchSeq(var_);
					upload.setBatch(var_);
				}
			}
		}

		if (StringUtils.isEmpty(reason)) {
			BigDecimal totalCreditAmt = BigDecimal.ZERO;
			BigDecimal totalDebitAmt = BigDecimal.ZERO;

			for (MiscPostingUpload miscPostingUpload : miscPostingUploadList) {
				if (miscPostingUpload.getTxnEntry().equalsIgnoreCase("C")) {
					totalCreditAmt = miscPostingUpload.getTxnAmount().add(totalCreditAmt);
				} else {
					totalDebitAmt = miscPostingUpload.getTxnAmount().add(totalDebitAmt);

				}
			}

			if (totalCreditAmt.compareTo(totalDebitAmt) != 0) {
				reason = "Credit and Debit amount not matching";
			}
		}

		if (!StringUtils.isEmpty(reason)) {
			for (MiscPostingUpload miscPostingUpload : miscPostingUploadList) {
				miscPostingUpload.setReason(reason);
				miscPostingUpload.setUploadStatus(PennantConstants.UPLOAD_STATUS_FAIL);
			}
		}

		logger.debug(Literal.LEAVING);
		return miscPostingUploadList;
	}

	class SortByTransactionId implements Comparator<MiscPostingUpload> {
		// Used for sorting in ascending order of
		public int compare(MiscPostingUpload a, MiscPostingUpload b) {
			return (int) (a.getTransactionId() - b.getTransactionId());
		}
	}

	private String getBatchSeq(String date, String format) {
		String uDate = null;

		try {
			if (StringUtils.isBlank(date)) {
				return uDate;
			}

			String[] dateformat = date.split("-");

			if (dateformat.length != 3) {
				throw new ParseException(null, 0);
			}

			String dateValue = dateformat[0];
			String month = dateformat[1];
			String year = dateformat[2];
			uDate = dateValue.concat(month).concat(year);

		} catch (Exception e) {
			logger.error(e);
		}

		return uDate;
	}

	/**
	 * For Validating AuditDetals object getting from Audit Header, if any mismatch conditions Fetch the error details
	 * from getMiscPostingUploadDAO().getErrorDetail with Error ID and language as parameters. if any error/Warnings
	 * then assign the to auditDeail Object
	 * 
	 * @param auditDetail
	 * @param usrLanguage
	 * @param method
	 * @return
	 */
	private MiscPostingUpload validation(MiscPostingUpload miscPostingUpload) {
		logger.debug("Entering");

		// Check the unique keys.
		if (miscPostingUpload.isNewRecord() && !"FAILED".equals(miscPostingUpload.getUploadStatus())) {
			validateLengths(miscPostingUpload);
			if (!"FAILED".equals(miscPostingUpload.getUploadStatus())) {
				validateData(miscPostingUpload);
			}

		}
		logger.debug(Literal.LEAVING);
		return miscPostingUpload;
	}

	@Override
	public void save(List<MiscPostingUpload> miscPostingUploads, long uploadId) {
		logger.debug("Entering");

		for (MiscPostingUpload miscPostingUpload : miscPostingUploads) {
			miscPostingUpload.setUploadId(uploadId);
			getMiscPostingUploadDAO().save(miscPostingUpload);
		}
		logger.debug("Leaving");
	}

	@Override
	public void delete(List<MiscPostingUpload> miscPostingUploadList) {
	}

	private void validateLengths(MiscPostingUpload miscPostingUpload) {
		int errorCount = 0;
		String reason = "";

		// Branch Validation
		if (StringUtils.isNotBlank(miscPostingUpload.getBranch()) && miscPostingUpload.getBranch().length() > 12) {
			errorCount++;
			reason = Labels.getLabel("inValid_Misc_Branch_Length");
			miscPostingUpload.setBranch(miscPostingUpload.getBranch().substring(0, 12));
		}

		// Transaction Id
		if (String.valueOf(miscPostingUpload.getTransactionId()).length() > 10) {
			errorCount++;
			reason = Labels.getLabel("inValid_Misc_TransactionId_Length");
			miscPostingUpload.setTransactionId(
					Long.valueOf(String.valueOf(miscPostingUpload.getTransactionId()).substring(0, 10)));
		}

		// Transaction Amount
		if (String.valueOf(miscPostingUpload.getTxnAmount()).length() > 16) {
			errorCount++;
			reason = Labels.getLabel("inValid_Misc_TxnAmount_Length");
			miscPostingUpload
					.setTxnAmount(new BigDecimal(String.valueOf(miscPostingUpload.getTxnAmount()).substring(0, 16)));
		}

		// Batch Purpose Validation
		if (StringUtils.isNotBlank(miscPostingUpload.getBatchPurpose())
				&& miscPostingUpload.getBatchPurpose().length() > 200) {
			errorCount++;
			reason = Labels.getLabel("inValid_Misc_Branch_Purpose_Lenght");
			miscPostingUpload.setBatchPurpose(miscPostingUpload.getBatchPurpose().substring(0, 200));
		}

		// Post Against Validation
		if (StringUtils.isNotBlank(miscPostingUpload.getPostAgainst())
				&& miscPostingUpload.getPostAgainst().length() > 5) {
			errorCount++;
			reason = Labels.getLabel("inValid_Misc_PostAgaint_Length");
			miscPostingUpload.setPostAgainst(miscPostingUpload.getPostAgainst().substring(0, 1));
		}

		// Fin Reference
		if (StringUtils.isNotBlank(miscPostingUpload.getReference())
				&& miscPostingUpload.getReference().length() > 20) {
			errorCount++;
			reason = Labels.getLabel("inValid_Misc_Reference_Length");
			miscPostingUpload.setReference(miscPostingUpload.getReference().substring(0, 20));
		}

		// Posting Divivsion
		if (StringUtils.isNotBlank(miscPostingUpload.getPostingDivision())
				&& miscPostingUpload.getPostingDivision().length() > 8) {
			errorCount++;
			reason = Labels.getLabel("inValid_Misc_PostingDivision_Length");
			miscPostingUpload.setPostingDivision(miscPostingUpload.getPostingDivision().substring(0, 8));
		}

		// Account Validation
		if (StringUtils.isNotBlank(miscPostingUpload.getAccount()) && miscPostingUpload.getAccount().length() > 50) {
			errorCount++;
			reason = Labels.getLabel("inValid_Misc_Account_Length");
			miscPostingUpload.setAccount(miscPostingUpload.getAccount().substring(0, 50));
		}

		// Transaction Entry Validation
		if (StringUtils.isNotBlank(miscPostingUpload.getTxnEntry()) && miscPostingUpload.getTxnEntry().length() > 1) {
			errorCount++;
			reason = Labels.getLabel("inValid_Misc_TxnEntry_Length");
			miscPostingUpload.setTxnEntry(miscPostingUpload.getTxnEntry().substring(0, 1));
		}

		// Narrline1 Validation
		if (StringUtils.isNotBlank(miscPostingUpload.getNarrLine1())
				&& miscPostingUpload.getNarrLine1().length() > 100) {
			errorCount++;
			reason = Labels.getLabel("inValid_Misc_Narrline1_Length");
			miscPostingUpload.setNarrLine1(miscPostingUpload.getNarrLine1().substring(0, 100));
		}

		// Narrline2 Validation
		if (StringUtils.isNotBlank(miscPostingUpload.getNarrLine2())
				&& miscPostingUpload.getNarrLine2().length() > 100) {
			errorCount++;
			reason = Labels.getLabel("inValid_Misc_Narrline2_Length");
			miscPostingUpload.setNarrLine2(miscPostingUpload.getNarrLine2().substring(0, 100));
		}

		// Narrline3 Validation
		if (StringUtils.isNotBlank(miscPostingUpload.getNarrLine3())
				&& miscPostingUpload.getNarrLine3().length() > 100) {
			errorCount++;
			reason = Labels.getLabel("inValid_Misc_Narrline3_Length");
			miscPostingUpload.setNarrLine3(miscPostingUpload.getNarrLine3().substring(0, 100));
		}

		// Narrline4 Validation
		if (StringUtils.isNotBlank(miscPostingUpload.getNarrLine4())
				&& miscPostingUpload.getNarrLine4().length() > 100) {
			errorCount++;
			reason = Labels.getLabel("inValid_Misc_Narrline4_Length");
			miscPostingUpload.setNarrLine4(miscPostingUpload.getNarrLine4().substring(0, 100));
		}

		if (errorCount > 0) {
			if (errorCount > 1) {
				reason = reason + "Invalid record.";
			}
			miscPostingUpload.setUploadStatus("FAILED");
			miscPostingUpload.setReason(reason);
		}

	}

	private void validateData(MiscPostingUpload miscPostingUpload) {
		int errorCount = 0;
		String reason = "";
		Branch branch = new Branch();
		FinanceMain finMain = new FinanceMain();
		Customer customer = new Customer();
		Entity entity = new Entity();
		AccountMapping account = new AccountMapping();

		// Value Date
		if (miscPostingUpload.getValueDate() == null) {
			errorCount++;
			reason = "Value Date is mandatory.";
		}

		if (errorCount == 0) {
			if (StringUtils.isBlank(miscPostingUpload.getPostAgainst())) {
				errorCount++;
				reason = "Post Against is mandatory, it should not be empty.";
			} else {
				if (String.join("|", "L", "N", "C", "E").contains(miscPostingUpload.getPostAgainst())) {
					if (miscPostingUpload.getPostAgainst().equalsIgnoreCase("N")) {
						if (StringUtils.isBlank(miscPostingUpload.getBranch())) {
							errorCount++;
							reason = " Branch is mandatory, it should not be empty.";
						} else {
							branch = branchDAO.getBranchById(miscPostingUpload.getBranch(), "");
							if (branch != null && branch.isBranchIsActive()) {
								miscPostingUpload.setBranch(branch.getBranchCode());
							} else {
								errorCount++;
								reason = " Branch is invalid/Inactive.";
							}
						}

						// Posting Division
						if (errorCount == 0) {
							if (StringUtils.isBlank(miscPostingUpload.getPostingDivision())) {
								errorCount++;
								reason = " Posting Division is mandatory, it should not be empty.";
							} else {
								DivisionDetail divisionDetails = getDivisionDetailDAO()
										.getDivisionDetailById(miscPostingUpload.getPostingDivision(), "");
								if (divisionDetails != null && divisionDetails.isActive()) {
								} else {
									errorCount++;
									reason = " Posting Division is invalid/Inactive.";
								}
							}
						}

					} else if (miscPostingUpload.getPostAgainst().equalsIgnoreCase("L")) {
						if (StringUtils.isBlank(miscPostingUpload.getReference())) {
							errorCount++;
							reason = " Loan Reference(Reference) is mandatory, it should not be empty.";
						} else {
							finMain = financeMainDAO.getFinanceMain(miscPostingUpload.getReference(),
									TableType.MAIN_TAB);
							if (finMain != null) {
								miscPostingUpload.setBranch(finMain.getFinBranch());
								miscPostingUpload.setPostingDivision(finMain.getLovDescFinDivision());

								// value date validation

								if (finMain.getFinStartDate().compareTo(miscPostingUpload.getValueDate()) > 0) {
									errorCount++;
									reason = " Value Date should greater than or equal to Loan StartDate";
								}

							} else {
								errorCount++;
								reason = " Loan Reference: " + miscPostingUpload.getReference()
										+ " is not available in system.";
								miscPostingUpload.setBranch(null);
								miscPostingUpload.setPostingDivision(null);
							}
						}
					} else if (miscPostingUpload.getPostAgainst().equalsIgnoreCase("E")) {

						if (StringUtils.isBlank(miscPostingUpload.getReference())) {
							errorCount++;
							reason = "Entity(Reference) is mandatory, it should not be empty.";
						} else {
							entity = entityDAO.getEntity(miscPostingUpload.getReference(), "");
							if (entity != null) {
								// miscPostingUpload.setBranch(customer.getCustDftBranch());
							} else {
								errorCount++;
								reason = " Entity(Reference): " + miscPostingUpload.getReference()
										+ " is not available in the system.";
								miscPostingUpload.setBranch(null);
							}
						}

						// Posting Division
						if (errorCount == 0) {
							if (StringUtils.isBlank(miscPostingUpload.getPostingDivision())) {
								errorCount++;
								reason = " Posting Division is mandatory, it should not be empty.";
								miscPostingUpload.setPostingDivision(null);
							} else {
								DivisionDetail divisionDetails = getDivisionDetailDAO()
										.getDivisionDetailById(miscPostingUpload.getPostingDivision(), "");
								if (divisionDetails != null && divisionDetails.isActive()) {
								} else {
									errorCount++;
									reason = " Posting Division is invalid/Inactive.";
								}
							}

							if (errorCount == 0) {
								if (!StringUtils.isBlank(miscPostingUpload.getBranch())) {
									branch = branchDAO.getBranchById(miscPostingUpload.getBranch(), "");
									if (branch != null && branch.isBranchIsActive()) {
										miscPostingUpload.setBranch(branch.getBranchCode());
									} else {
										errorCount++;
										reason = " Branch is invalid/Inactive.";
									}
								}
							}
						}

					} else {
						if (StringUtils.isBlank(miscPostingUpload.getReference())) {
							errorCount++;
							reason = "Customer CIF(Reference) is mandatory, it should not be empty.";
						} else {
							customer = customerDAO.getCustomerByCIF(miscPostingUpload.getReference(), "");
							if (customer != null) {
								miscPostingUpload.setBranch(customer.getCustDftBranch());
							} else {
								errorCount++;
								reason = " Customer CIF(Reference): " + miscPostingUpload.getReference()
										+ " is not available in the system.";
								miscPostingUpload.setBranch(null);
							}
						}

						// Posting Division
						if (StringUtils.isBlank(miscPostingUpload.getPostingDivision())) {
							errorCount++;
							reason = " Posting Division is mandatory, it should not be empty.";
							miscPostingUpload.setPostingDivision(null);
						} else {
							DivisionDetail divisionDetails = getDivisionDetailDAO()
									.getDivisionDetailById(miscPostingUpload.getPostingDivision(), "");
							if (divisionDetails != null && divisionDetails.isActive()) {
							} else {
								errorCount++;
								reason = " Posting Division is invalid/Inactive.";
							}
						}
					}
				} else {
					errorCount++;
					reason = " Post Against allowed values are L,N,C and E.";
				}
			}
		}

		// Account
		if (errorCount == 0) {
			if (StringUtils.isBlank(miscPostingUpload.getAccount())) {
				errorCount++;
				reason = " Account is mandatory, it should not be empty.";
			} else {
				account = accountMappingDAO.getAccountMapping(miscPostingUpload.getAccount(), "");
				if (account == null) {
					errorCount++;
					reason = " Account: " + miscPostingUpload.getAccount() + " is not available in system.";
				}
			}
		}

		// Transaction Entry
		if (errorCount == 0) {
			if (StringUtils.isBlank(miscPostingUpload.getTxnEntry())) {
				errorCount++;
				reason = " Transacton Entry is mandatory, it should not be empty.";
			} else {
				if (miscPostingUpload.getTxnEntry().equals("D") || miscPostingUpload.getTxnEntry().equals("C")) {
					miscPostingUpload.setTxnEntry(miscPostingUpload.getTxnEntry());
				} else {
					errorCount++;
					reason = " Transacton Entry allowed values are 'D' or 'C'.";
				}
			}
		}

		// Payable Amount
		if (errorCount == 0) {
			if (BigDecimal.ZERO.compareTo(miscPostingUpload.getTxnAmount()) >= 0) {
				errorCount++;
				reason = " Transaction Amount should be greater than 0.";
			} else {
				miscPostingUpload.setTxnAmount(miscPostingUpload.getTxnAmount());
			}
		}

		if (errorCount > 0) {
			if (errorCount > 1) {
				reason = reason + " Invalid record.";
			}
			miscPostingUpload.setUploadStatus("FAILED");
			miscPostingUpload.setReason(reason);
		}
	}

	public BranchDAO getBranchDAO() {
		return branchDAO;
	}

	public void setBranchDAO(BranchDAO branchDAO) {
		this.branchDAO = branchDAO;
	}

	public CustomerDAO getCustomerDAO() {
		return customerDAO;
	}

	public void setCustomerDAO(CustomerDAO customerDAO) {
		this.customerDAO = customerDAO;
	}

	public AccountMappingDAO getAccountMappingDAO() {
		return accountMappingDAO;
	}

	public void setAccountMappingDAO(AccountMappingDAO accountMappingDAO) {
		this.accountMappingDAO = accountMappingDAO;
	}

	@Override
	public void update(MiscPostingUpload miscPostingUpload) {
		miscPostingUploadDAO.update(miscPostingUpload);
	}

	@Override
	public MiscPostingUpload getMiscPostingUploadByMiscId(long miscPostingId) {
		return miscPostingUploadDAO.getMiscPostingUploadsByMiscId(miscPostingId, "");
	}

	public EntityDAO getEntityDAO() {
		return entityDAO;
	}

	public void setEntityDAO(EntityDAO entityDAO) {
		this.entityDAO = entityDAO;
	}

	@Override
	public void updateList(List<MiscPostingUpload> miscPostingUploads) {
		logger.debug("Entering");
		getMiscPostingUploadDAO().updateList(miscPostingUploads);
		logger.debug("Leaving");
	}

	public JVPostingDAO getjVPostingDAO() {
		return jVPostingDAO;
	}

	public void setjVPostingDAO(JVPostingDAO jVPostingDAO) {
		this.jVPostingDAO = jVPostingDAO;
	}

	@Override
	public void insertInJVPosting(UploadHeader uploadHeader) {
		logger.debug(Literal.ENTERING);

		List<MiscPostingUpload> successRecordList = new ArrayList<>();
		for (MiscPostingUpload miscPostingUpload : uploadHeader.getMiscPostingUploads()) {
			if (PennantConstants.UPLOAD_STATUS_SUCCESS.equalsIgnoreCase(miscPostingUpload.getUploadStatus())) {
				successRecordList.add(miscPostingUpload);
			}
		}

		getListBasedOnTransactionId(successRecordList, uploadHeader);

		logger.debug(Literal.LEAVING);
	}

	private List<AuditDetail> setJVPostingEntryAuditData(JVPosting jVPosting, String auditTranType, String method) {
		logger.debug("Entering");

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();

		String[] fields = PennantJavaUtil.getFieldDetails(new JVPostingEntry(),
				new JVPostingEntry().getExcludeFields());

		for (int i = 0; i < jVPosting.getJVPostingEntrysList().size(); i++) {

			JVPostingEntry jVPostingEntry = jVPosting.getJVPostingEntrysList().get(i);
			jVPostingEntry.setWorkflowId(jVPosting.getWorkflowId());

			jVPostingEntry.setRecordType(PennantConstants.RECORD_TYPE_NEW);
			auditTranType = PennantConstants.TRAN_ADD;

			jVPostingEntry.setRecordStatus(jVPosting.getRecordStatus());
			jVPostingEntry.setUserDetails(jVPosting.getUserDetails());
			jVPostingEntry.setLastMntOn(jVPosting.getLastMntOn());
			jVPostingEntry.setLastMntBy(jVPosting.getLastMntBy());

			if (StringUtils.isNotBlank(jVPostingEntry.getRecordType())) {
				auditDetails.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1],
						jVPostingEntry.getBefImage(), jVPostingEntry));
			}
		}
		logger.debug("Leaving");
		return auditDetails;
	}

	public JVPostingService getJVPostingService() {
		return jVPostingService;
	}

	public void setJVPostingService(JVPostingService jVPostingService) {
		this.jVPostingService = jVPostingService;
	}

	public DivisionDetailDAO getDivisionDetailDAO() {
		return divisionDetailDAO;
	}

	public void setDivisionDetailDAO(DivisionDetailDAO divisionDetailDAO) {
		this.divisionDetailDAO = divisionDetailDAO;
	}

	@Override
	public void deleteByUploadId(long uploadId) {
		this.miscPostingUploadDAO.deleteByUploadId(uploadId);

	}

}
