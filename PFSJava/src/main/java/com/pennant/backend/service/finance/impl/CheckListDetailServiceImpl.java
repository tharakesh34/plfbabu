package com.pennant.backend.service.finance.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.applicationmaster.CheckListDetailDAO;
import com.pennant.backend.dao.customermasters.CustomerDocumentDAO;
import com.pennant.backend.dao.lmtmasters.FinanceCheckListReferenceDAO;
import com.pennant.backend.dao.lmtmasters.FinanceReferenceDetailDAO;
import com.pennant.backend.model.applicationmaster.CheckListDetail;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.collateral.CollateralSetup;
import com.pennant.backend.model.commitment.Commitment;
import com.pennant.backend.model.configuration.VASRecording;
import com.pennant.backend.model.configuration.VasCustomer;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerDetails;
import com.pennant.backend.model.documentdetails.DocumentDetails;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.lmtmasters.FinanceCheckListReference;
import com.pennant.backend.model.lmtmasters.FinanceReferenceDetail;
import com.pennant.backend.service.finance.CheckListDetailService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;

public class CheckListDetailServiceImpl implements CheckListDetailService {
	private static final Logger logger = LogManager.getLogger(CheckListDetailServiceImpl.class);

	private FinanceReferenceDetailDAO financeReferenceDetailDAO;
	private FinanceCheckListReferenceDAO financeCheckListReferenceDAO;
	private CheckListDetailDAO checkListDetailDAO;
	private CustomerDocumentDAO customerDocumentDAO;

	public CheckListDetailServiceImpl() {
		super();
	}

	@Override
	public void fetchFinCheckListDetails(FinanceDetail fd, List<FinanceReferenceDetail> financeReferenceList) {
		logger.debug(Literal.ENTERING);

		processCheckListDetails(fd, financeReferenceList);

		logger.debug(Literal.LEAVING);
	}

	@Override
	public void fetchCommitmentCheckLists(Commitment commitment, List<FinanceReferenceDetail> financeReferenceList) {
		logger.debug(Literal.ENTERING);

		getCommitmentCheckList(commitment, financeReferenceList);

		logger.debug(Literal.LEAVING);
	}

	@Override
	public void setFinanceCheckListDetails(FinanceDetail fd, String finType, String finEvent, String userRole) {
		logger.debug(Literal.ENTERING);

		List<FinanceReferenceDetail> referenceList = financeReferenceDetailDAO.getFinanceReferenceDetail(finType,
				finEvent, userRole, "_AQView");

		processCheckListDetails(fd, referenceList);

		logger.debug(Literal.LEAVING);
	}

	private void processCheckListDetails(FinanceDetail fd, List<FinanceReferenceDetail> referenceList) {
		logger.debug(Literal.ENTERING);

		FinScheduleData schdData = fd.getFinScheduleData();

		FinanceMain fm = schdData.getFinanceMain();
		long finID = fm.getFinID();
		long custID = fm.getCustID();

		String showCheckListIds = "";
		StringBuilder showCheckListIdSb = new StringBuilder();
		Set<Long> checkListIdSet = new HashSet<Long>();

		if (referenceList == null) {
			referenceList = new ArrayList<>();
		}

		for (FinanceReferenceDetail financeReferenceDetail : referenceList) {
			showCheckListIdSb.append(financeReferenceDetail.getFinRefId() + ",");
			checkListIdSet.add(financeReferenceDetail.getFinRefId());
		}

		List<CheckListDetail> list = new ArrayList<>();

		if (!checkListIdSet.isEmpty()) {
			list.addAll(checkListDetailDAO.getCheckListDetailByChkList(checkListIdSet, "_AView"));
		}

		long prevCheckListId = 0L;

		List<CheckListDetail> cldList = null;

		for (CheckListDetail cld : list) {
			if (prevCheckListId == 0) {
				cldList = new ArrayList<CheckListDetail>();
				prevCheckListId = cld.getCheckListId();
				cldList.add(cld);
			} else if (prevCheckListId != cld.getCheckListId()) {
				for (FinanceReferenceDetail finRefDtl : referenceList) {
					if (finRefDtl.getFinRefId() == prevCheckListId) {
						finRefDtl.setLovDesccheckListDetail(cldList);
						break;
					}
				}

				cldList = new ArrayList<CheckListDetail>();
				prevCheckListId = cld.getCheckListId();
				cldList.add(cld);
			} else {
				cldList.add(cld);
			}
		}
		// Use the last object.
		for (FinanceReferenceDetail finRefDtl : referenceList) {
			if (finRefDtl.getFinRefId() == prevCheckListId) {
				finRefDtl.setLovDesccheckListDetail(cldList);
				break;
			}
		}

		// Customer Document Details Fetching Depends on Customer & Doc Type List

		List<DocumentDetails> documentList = customerDocumentDAO.getCustDocByCustId(custID, "");

		if (fd.getDocumentDetailsList() != null && !fd.getDocumentDetailsList().isEmpty()) {
			fd.getDocumentDetailsList().addAll(documentList);
		} else {
			fd.setDocumentDetailsList(documentList);
		}

		if (showCheckListIdSb.toString().endsWith(",")) {
			showCheckListIds = showCheckListIdSb.substring(0, showCheckListIdSb.length() - 1);
		}

		List<FinanceCheckListReference> checkListReferences = null;
		if (!fm.isNewRecord()) {
			checkListReferences = financeCheckListReferenceDAO.getCheckListByFinRef(finID, showCheckListIds, "_View");
		} else {
			checkListReferences = new ArrayList<FinanceCheckListReference>();
		}
		fd.setCheckList(referenceList);
		fd.setFinanceCheckList(checkListReferences);

		logger.debug(Literal.LEAVING);
	}

	@Override
	public void fetchCollateralCheckLists(CollateralSetup collateralSetup, List<FinanceReferenceDetail> referenceList) {
		logger.debug(Literal.ENTERING);

		List<FinanceCheckListReference> collateralCheckLists = null;
		List<CheckListDetail> checkListDetailList = null;
		Set<Long> checkListIdSet = new HashSet<>();

		String showCheckListIds = "";
		String collateralRef = collateralSetup.getCollateralRef();

		StringBuilder showCheckListIdSb = new StringBuilder();

		if (referenceList == null) {
			referenceList = new ArrayList<>();
		}

		for (FinanceReferenceDetail financeReferenceDetail : referenceList) {
			showCheckListIdSb.append(financeReferenceDetail.getFinRefId() + ",");
			checkListIdSet.add(financeReferenceDetail.getFinRefId());
		}

		List<CheckListDetail> checkListDetailAllList = checkListDetailDAO.getCheckListDetailByChkList(checkListIdSet,
				"_AView");

		long prevCheckListId = 0L;
		for (CheckListDetail checkListDetail : checkListDetailAllList) {
			if (prevCheckListId == 0) {
				checkListDetailList = new ArrayList<CheckListDetail>();
				prevCheckListId = checkListDetail.getCheckListId();
				checkListDetailList.add(checkListDetail);
			} else if (prevCheckListId != checkListDetail.getCheckListId()) {
				for (FinanceReferenceDetail finRefDtl : referenceList) {
					if (finRefDtl.getFinRefId() == prevCheckListId) {
						finRefDtl.setLovDesccheckListDetail(checkListDetailList);
						break;
					}
				}
				checkListDetailList = new ArrayList<CheckListDetail>();
				prevCheckListId = checkListDetail.getCheckListId();
				checkListDetailList.add(checkListDetail);
			} else {
				checkListDetailList.add(checkListDetail);
			}
		}
		// Use the last object.
		for (FinanceReferenceDetail finRefDtl : referenceList) {
			if (finRefDtl.getFinRefId() == prevCheckListId) {
				finRefDtl.setLovDesccheckListDetail(checkListDetailList);
				break;
			}
		}

		// Customer Document Details Fetching Depends on Customer & Doc Type List
		CustomerDetails customerDetails = collateralSetup.getCustomerDetails();
		Customer customer = customerDetails.getCustomer();
		long custID = customer.getCustID();
		List<DocumentDetails> documentList = customerDocumentDAO.getCustDocByCustId(custID, "");

		if (collateralSetup.getDocuments() != null && !collateralSetup.getDocuments().isEmpty()) {
			collateralSetup.getDocuments().addAll(documentList);
		} else {
			collateralSetup.setDocuments(documentList);
		}
		if (showCheckListIdSb.toString().endsWith(",")) {
			showCheckListIds = showCheckListIdSb.substring(0, showCheckListIdSb.length() - 1);
		}

		if (!collateralSetup.isNewRecord()) {
			collateralCheckLists = financeCheckListReferenceDAO.getCheckListByFinRef(collateralRef, showCheckListIds,
					"_View");
		} else {
			collateralCheckLists = new ArrayList<FinanceCheckListReference>();
		}
		collateralSetup.setCheckLists(referenceList);
		collateralSetup.setCollateralCheckLists(collateralCheckLists);

		logger.debug(Literal.LEAVING);
	}

	private void getCommitmentCheckList(Commitment commitment, List<FinanceReferenceDetail> financeReferenceList) {
		logger.debug(Literal.ENTERING);

		List<FinanceCheckListReference> commitmentCheckLists = null;
		List<CheckListDetail> checkListDetailList = null;
		StringBuilder showCheckListIdSb = new StringBuilder();
		Set<Long> checkListIdSet = new HashSet<>();

		String showCheckListIds = "";
		String cmtReference = commitment.getCmtReference();

		if (CollectionUtils.isNotEmpty(financeReferenceList)) {
			for (FinanceReferenceDetail financeReferenceDetail : financeReferenceList) {
				showCheckListIdSb.append(financeReferenceDetail.getFinRefId() + ",");
				checkListIdSet.add(financeReferenceDetail.getFinRefId());
			}

			List<CheckListDetail> checkListDetailAllList = checkListDetailDAO
					.getCheckListDetailByChkList(checkListIdSet, "_AView");

			long prevCheckListId = 0L;
			for (CheckListDetail checkListDetail : checkListDetailAllList) {
				if (prevCheckListId == 0) {
					checkListDetailList = new ArrayList<CheckListDetail>();
					prevCheckListId = checkListDetail.getCheckListId();
					checkListDetailList.add(checkListDetail);
				} else if (prevCheckListId != checkListDetail.getCheckListId()) {
					for (FinanceReferenceDetail finRefDtl : financeReferenceList) {
						if (finRefDtl.getFinRefId() == prevCheckListId) {
							finRefDtl.setLovDesccheckListDetail(checkListDetailList);
							break;
						}
					}

					checkListDetailList = new ArrayList<CheckListDetail>();
					prevCheckListId = checkListDetail.getCheckListId();
					checkListDetailList.add(checkListDetail);
				} else {
					checkListDetailList.add(checkListDetail);
				}
			}
			// Use the last object.
			for (FinanceReferenceDetail finRefDtl : financeReferenceList) {
				if (finRefDtl.getFinRefId() == prevCheckListId) {
					finRefDtl.setLovDesccheckListDetail(checkListDetailList);
					break;
				}
			}
		}

		// Customer Document Details Fetching Depends on Customer & Doc Type List
		CustomerDetails customerDetails = commitment.getCustomerDetails();
		Customer customer = customerDetails.getCustomer();
		long custID = customer.getCustID();
		List<DocumentDetails> documentList = customerDocumentDAO.getCustDocByCustId(custID, "");

		if (!commitment.isNewRecord() && (commitment.getDocuments() != null && !commitment.getDocuments().isEmpty())) {
			commitment.getDocuments().addAll(documentList);
		} else {
			commitment.setDocuments(documentList);
		}

		if (showCheckListIdSb.toString().endsWith(",")) {
			showCheckListIds = showCheckListIdSb.substring(0, showCheckListIdSb.length() - 1);
		}

		if (commitment.isNewRecord()) {
			commitmentCheckLists = new ArrayList<FinanceCheckListReference>();
		} else {
			commitmentCheckLists = financeCheckListReferenceDAO.getCheckListByFinRef(cmtReference, showCheckListIds,
					"_View");
		}
		commitment.setCheckLists(financeReferenceList);
		commitment.setCommitmentCheckLists(commitmentCheckLists);

		logger.debug(Literal.LEAVING);
	}

	@Override
	public void fetchVASCheckLists(VASRecording vasRecording, List<FinanceReferenceDetail> financeReferenceList) {
		logger.debug(Literal.ENTERING);

		List<FinanceCheckListReference> vasCheckLists = null;
		List<CheckListDetail> checkListDetailList = null;
		StringBuilder showCheckListIdSb = new StringBuilder();
		Set<Long> checkListIdSet = new HashSet<>();

		String showCheckListIds = "";
		String vasReference = vasRecording.getVasReference();

		if (CollectionUtils.isNotEmpty(financeReferenceList)) {
			for (FinanceReferenceDetail financeReferenceDetail : financeReferenceList) {
				showCheckListIdSb.append(financeReferenceDetail.getFinRefId() + ",");
				checkListIdSet.add(financeReferenceDetail.getFinRefId());
			}

			List<CheckListDetail> checkListDetailAllList = checkListDetailDAO
					.getCheckListDetailByChkList(checkListIdSet, "_AView");

			long prevCheckListId = 0L;
			for (CheckListDetail checkListDetail : checkListDetailAllList) {
				if (prevCheckListId == 0) {
					checkListDetailList = new ArrayList<CheckListDetail>();
					prevCheckListId = checkListDetail.getCheckListId();
					checkListDetailList.add(checkListDetail);
				} else if (prevCheckListId != checkListDetail.getCheckListId()) {
					for (FinanceReferenceDetail finRefDtl : financeReferenceList) {
						if (finRefDtl.getFinRefId() == prevCheckListId) {
							finRefDtl.setLovDesccheckListDetail(checkListDetailList);
							break;
						}
					}
					checkListDetailList = new ArrayList<CheckListDetail>();
					prevCheckListId = checkListDetail.getCheckListId();
					checkListDetailList.add(checkListDetail);
				} else {
					checkListDetailList.add(checkListDetail);
				}
			}
			// Use the last object.
			for (FinanceReferenceDetail finRefDtl : financeReferenceList) {
				if (finRefDtl.getFinRefId() == prevCheckListId) {
					finRefDtl.setLovDesccheckListDetail(checkListDetailList);
					break;
				}
			}
		}
		// Customer Document Details Fetching Depends on Customer & Doc Type List
		List<DocumentDetails> documentList = new ArrayList<>();
		VasCustomer vasCustomer = vasRecording.getVasCustomer();
		if (vasCustomer != null) {
			long customerId = vasCustomer.getCustomerId();
			documentList = customerDocumentDAO.getCustDocByCustId(customerId, "");
		}
		if (vasRecording.getDocuments() != null && !vasRecording.getDocuments().isEmpty()) {
			vasRecording.getDocuments().addAll(documentList);
		} else {
			vasRecording.setDocuments(documentList);
		}

		if (showCheckListIdSb.toString().endsWith(",")) {
			showCheckListIds = showCheckListIdSb.substring(0, showCheckListIdSb.length() - 1);
		}

		if (!vasRecording.isNewRecord()) {
			vasCheckLists = financeCheckListReferenceDAO.getCheckListByFinRef(vasReference, showCheckListIds, "_View");
		} else {
			vasCheckLists = new ArrayList<FinanceCheckListReference>();
		}
		vasRecording.setCheckLists(financeReferenceList);
		vasRecording.setVasCheckLists(vasCheckLists);

		logger.debug(Literal.LEAVING);
	}

	@Override
	public List<FinanceCheckListReference> getCheckListByFinRef(String reference, String type) {
		return financeCheckListReferenceDAO.getCheckListByFinRef(reference, null, type);
	}

	@Override
	public List<FinanceCheckListReference> getCheckListByFinRef(long finID, String type) {
		return financeCheckListReferenceDAO.getCheckListByFinRef(finID, null, type);
	}

	@Override
	public List<AuditDetail> delete(FinanceDetail fd, String tableType, String auditTranType) {
		logger.debug(Literal.ENTERING);
		List<AuditDetail> auditList = new ArrayList<AuditDetail>();
		FinanceCheckListReference object = new FinanceCheckListReference();

		String[] fields = PennantJavaUtil.getFieldDetails(object, object.getExcludeFields());

		if (fd.getFinanceCheckList() != null && !fd.getFinanceCheckList().isEmpty()) {
			for (int i = 0; i < fd.getFinanceCheckList().size(); i++) {
				FinanceCheckListReference finCheckListRef = fd.getFinanceCheckList().get(i);
				auditList.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1], finCheckListRef.getBefImage(),
						finCheckListRef));
			}

			String finReference = fd.getFinanceCheckList().get(0).getFinReference();
			financeCheckListReferenceDAO.delete(finReference, tableType);
		}
		logger.debug(Literal.LEAVING);
		return auditList;
	}

	@Override
	public List<AuditDetail> getAuditDetail(Map<String, List<AuditDetail>> auditDetailMap, FinanceDetail financeDetail,
			String auditTranType, String method) {
		logger.debug(Literal.ENTERING);

		FinanceMain financeMain = financeDetail.getFinScheduleData().getFinanceMain();
		FinanceCheckListReference object = new FinanceCheckListReference();
		String[] fields = PennantJavaUtil.getFieldDetails(object, object.getExcludeFields());

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		for (int i = 0; i < financeDetail.getFinanceCheckList().size(); i++) {
			FinanceCheckListReference finChekListRef = financeDetail.getFinanceCheckList().get(i);

			if (StringUtils.isEmpty(finChekListRef.getRecordType())) {
				continue;
			}

			if (!auditTranType.equals(PennantConstants.TRAN_WF)) {
				if (StringUtils.trimToEmpty(finChekListRef.getRecordType()).equals(PennantConstants.RCD_ADD)) {
					auditTranType = PennantConstants.TRAN_ADD;
					// finChekListRef.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else if (StringUtils.trimToEmpty(finChekListRef.getRecordType())
						.equals(PennantConstants.RECORD_TYPE_NEW)) {
					auditTranType = PennantConstants.TRAN_UPD;
				} else {
					auditTranType = PennantConstants.RCD_DEL;
				}
			}
			if ("doApprove".equals(StringUtils.trimToEmpty(method))) {
				finChekListRef.setRecordType(PennantConstants.RCD_ADD);
			}
			finChekListRef.setRecordStatus("");
			finChekListRef.setUserDetails(financeMain.getUserDetails());
			finChekListRef.setLastMntOn(financeMain.getLastMntOn());
			finChekListRef.setLastMntBy(financeMain.getLastMntBy());
			finChekListRef.setWorkflowId(financeMain.getWorkflowId());

			if (StringUtils.isNotEmpty(StringUtils.trimToEmpty(finChekListRef.getRecordType()).trim())) {
				auditDetails.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1],
						finChekListRef.getBefImage(), finChekListRef));
			}
		}

		auditDetailMap.put("checkListDetails", auditDetails);
		logger.debug(Literal.LEAVING);
		return auditDetails;
	}

	@Override
	public List<AuditDetail> saveOrUpdate(FinanceDetail fd, String tableType, long instructionUID) {
		logger.debug(Literal.ENTERING);

		List<AuditDetail> auditDetails = fd.getAuditDetailMap().get("checkListDetails");

		for (int i = 0; i < auditDetails.size(); i++) {
			FinanceCheckListReference finChecklistRef = (FinanceCheckListReference) auditDetails.get(i).getModelData();
			finChecklistRef.setWorkflowId(0);
			finChecklistRef.setInstructionUID(instructionUID);
			if (StringUtils.isEmpty(tableType)) {
				finChecklistRef.setVersion(finChecklistRef.getVersion() + 1);
				finChecklistRef.setRoleCode("");
				finChecklistRef.setNextRoleCode("");
				finChecklistRef.setTaskId("");
				finChecklistRef.setNextTaskId("");
			}
			if (finChecklistRef.getRecordType().equals(PennantConstants.RCD_ADD)) {
				finChecklistRef.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				financeCheckListReferenceDAO.save(finChecklistRef, tableType);
			} else if (finChecklistRef.getRecordType().equals(PennantConstants.RCD_DEL)) {
				financeCheckListReferenceDAO.delete(finChecklistRef, tableType);
			} else if (finChecklistRef.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				finChecklistRef.setRecordType("");
				financeCheckListReferenceDAO.update(finChecklistRef, tableType);
			}

			auditDetails.get(i).setModelData(finChecklistRef);

		}
		logger.debug(Literal.LEAVING);
		return auditDetails;
	}

	@Override
	public List<AuditDetail> doApprove(FinanceDetail financeDetail, String tableType, long instructionUID) {
		logger.debug(Literal.ENTERING);

		List<AuditDetail> auditDetails = financeDetail.getAuditDetailMap().get("checkListDetails");

		for (int i = 0; i < auditDetails.size(); i++) {
			FinanceCheckListReference finChecklistRef = (FinanceCheckListReference) auditDetails.get(i).getModelData();
			finChecklistRef.setWorkflowId(0);
			finChecklistRef.setInstructionUID(instructionUID);
			if (StringUtils.isEmpty(tableType)) {
				finChecklistRef.setVersion(finChecklistRef.getVersion() + 1);
				finChecklistRef.setRoleCode("");
				finChecklistRef.setNextRoleCode("");
				finChecklistRef.setTaskId("");
				finChecklistRef.setNextTaskId("");
				finChecklistRef.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
			}
			String recordType = finChecklistRef.getRecordType();
			if (recordType.equals(PennantConstants.RCD_ADD)) {
				finChecklistRef.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				financeCheckListReferenceDAO.save(finChecklistRef, tableType);
			} else if (recordType.equals(PennantConstants.RCD_DEL)) {
				financeCheckListReferenceDAO.delete(finChecklistRef, tableType);
			} else if (recordType.equals(PennantConstants.RECORD_TYPE_NEW)) {
				finChecklistRef.setRecordType("");
				financeCheckListReferenceDAO.update(finChecklistRef, tableType);
			}

			auditDetails.get(i).setModelData(finChecklistRef);

		}
		logger.debug(Literal.LEAVING);
		return auditDetails;
	}

	public AuditHeader validate(AuditHeader auditHeader, String method) {

		AuditDetail auditDetail = validate(auditHeader.getAuditDetail(), method, auditHeader.getUsrLanguage());
		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());
		return auditHeader;
	}

	@Override
	public List<AuditDetail> validate(List<AuditDetail> auditDetails, String method, String usrLanguage) {
		logger.debug(Literal.ENTERING);
		List<AuditDetail> details = new ArrayList<AuditDetail>();

		if (auditDetails == null || auditDetails.isEmpty()) {
			return details;
		}

		FinanceCheckListReference checkList = (FinanceCheckListReference) auditDetails.get(0).getModelData();
		String finReference = checkList.getFinReference();

		List<FinanceCheckListReference> tempFinCheckListRefList = null;
		if (checkList.isWorkflow()) {
			tempFinCheckListRefList = financeCheckListReferenceDAO.getCheckListByFinRef(finReference, null, "_Temp");
		}
		List<FinanceCheckListReference> befFinCheckListRefList = financeCheckListReferenceDAO
				.getCheckListByFinRef(finReference, null, "");

		for (int i = 0; i < auditDetails.size(); i++) {
			FinanceCheckListReference finCheckListRef = (FinanceCheckListReference) auditDetails.get(i).getModelData();
			FinanceCheckListReference tempFinCheckListRef = null, befFinCheckListRef = null;
			if (tempFinCheckListRefList != null && !tempFinCheckListRefList.isEmpty()) {
				for (FinanceCheckListReference fincListRefTemp : tempFinCheckListRefList) {
					if (finCheckListRef.getQuestionId() == fincListRefTemp.getQuestionId()
							&& finCheckListRef.getAnswer() == fincListRefTemp.getAnswer()) {
						if (finCheckListRef.getInstructionUID() == fincListRefTemp.getInstructionUID()) {
							tempFinCheckListRef = fincListRefTemp;
							break;
						}
					}
				}
			}
			if (befFinCheckListRefList != null && !befFinCheckListRefList.isEmpty()) {
				for (FinanceCheckListReference fincListRefBef : befFinCheckListRefList) {
					if (finCheckListRef.getQuestionId() == fincListRefBef.getQuestionId()
							&& finCheckListRef.getAnswer() == fincListRefBef.getAnswer()) {
						if (finCheckListRef.getInstructionUID() == fincListRefBef.getInstructionUID()) {
							befFinCheckListRef = fincListRefBef;
							break;
						}
					}
				}
			}
			AuditDetail auditDetail = validate(auditDetails.get(i), tempFinCheckListRef, befFinCheckListRef, method,
					usrLanguage);
			details.add(auditDetail);
		}

		logger.debug(Literal.LEAVING);

		return details;

	}

	private AuditDetail validate(AuditDetail auditDetail, FinanceCheckListReference tempCheckList,
			FinanceCheckListReference befCheckList, String usrLanguage, String method) {
		logger.debug(Literal.ENTERING);

		auditDetail.setErrorDetails(new ArrayList<ErrorDetail>());
		FinanceCheckListReference financeCheckListReference = (FinanceCheckListReference) auditDetail.getModelData();
		FinanceCheckListReference oldFinanceCheckListReference = financeCheckListReference.getBefImage();

		String[] errParm = new String[3];
		String[] valueParm = new String[3];
		valueParm[0] = financeCheckListReference.getFinReference();
		valueParm[1] = financeCheckListReference.getLovDescQuesDesc();
		valueParm[2] = financeCheckListReference.getLovDescAnswerDesc();
		errParm[0] = PennantJavaUtil.getLabel("label_CheckListReference") + " : " + valueParm[0];
		errParm[1] = PennantJavaUtil.getLabel("label_CheckList") + " : " + valueParm[1];
		errParm[2] = PennantJavaUtil.getLabel("label_Answer") + " : " + valueParm[2];

		if (financeCheckListReference.isNewRecord()) { // for New record or new record into work flow

			if (!financeCheckListReference.isWorkflow()) {// With out Work flow only new records
				if (befCheckList != null) { // Record Already Exists in the table then error
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD, "41008", errParm, valueParm), usrLanguage));
				}
			} else { // with work flow
				if (financeCheckListReference.getRecordType().equals(PennantConstants.RCD_ADD)) { // if records type is
																									// new
					if (befCheckList != null || tempCheckList != null) { // if
						// records already exists in the main table
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
								new ErrorDetail(PennantConstants.KEY_FIELD, "41008", errParm, valueParm), usrLanguage));
					}
				}
			}
		} else {
			// for work flow process records or (Record to update or Delete with out work flow)
			if (!financeCheckListReference.isWorkflow()) { // With out Work flow for update and delete

				if (befCheckList == null) { // if records not exists in the main table
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD, "41002", errParm, valueParm), usrLanguage));
				} else {
					if (oldFinanceCheckListReference != null
							&& !oldFinanceCheckListReference.getLastMntOn().equals(befCheckList.getLastMntOn())) {
						if (StringUtils.trimToEmpty(auditDetail.getAuditTranType())
								.equalsIgnoreCase(PennantConstants.TRAN_DEL)) {
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
									new ErrorDetail(PennantConstants.KEY_FIELD, "41003", errParm, valueParm),
									usrLanguage));
						} else {
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
									new ErrorDetail(PennantConstants.KEY_FIELD, "41004", errParm, valueParm),
									usrLanguage));
						}
					}
				}
			} else {
				if (tempCheckList == null) { // if records not exists in the Work flow table
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, valueParm), usrLanguage));
				}
				if (tempCheckList != null && oldFinanceCheckListReference != null
						&& !oldFinanceCheckListReference.getLastMntOn().equals(tempCheckList.getLastMntOn())) {
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, valueParm), usrLanguage));
				}
			}
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		if ("doApprove".equals(StringUtils.trimToEmpty(method)) || !financeCheckListReference.isWorkflow()) {
			financeCheckListReference.setBefImage(befCheckList);
		}

		logger.debug(Literal.LEAVING);
		return auditDetail;
	}

	private AuditDetail validate(AuditDetail auditDetail, String usrLanguage, String method) {
		logger.debug(Literal.ENTERING);

		auditDetail.setErrorDetails(new ArrayList<ErrorDetail>());
		FinanceCheckListReference checkList = (FinanceCheckListReference) auditDetail.getModelData();

		String finReference = checkList.getFinReference();

		FinanceCheckListReference tempCheckList = null;
		if (checkList.isWorkflow()) {
			tempCheckList = financeCheckListReferenceDAO.getFinanceCheckListReferenceById(finReference,
					checkList.getQuestionId(), checkList.getAnswer(), "_Temp");
		}
		FinanceCheckListReference befCheckList = financeCheckListReferenceDAO
				.getFinanceCheckListReferenceById(finReference, checkList.getQuestionId(), checkList.getAnswer(), "");

		FinanceCheckListReference oldFinanceCheckListReference = checkList.getBefImage();

		String[] errParm = new String[3];
		String[] valueParm = new String[3];
		valueParm[0] = checkList.getFinReference();
		valueParm[1] = checkList.getLovDescQuesDesc();
		valueParm[2] = checkList.getLovDescAnswerDesc();
		errParm[0] = PennantJavaUtil.getLabel("label_CheckListReference") + " : " + valueParm[0];
		errParm[1] = PennantJavaUtil.getLabel("label_CheckList") + " : " + valueParm[1];
		errParm[2] = PennantJavaUtil.getLabel("label_Answer") + " : " + valueParm[2];

		if (checkList.isNewRecord()) { // for New record or new record into work flow

			if (!checkList.isWorkflow()) {// With out Work flow only new records
				if (befCheckList != null) { // Record Already Exists in the table then error
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD, "41008", errParm, valueParm), usrLanguage));
				}
			} else { // with work flow
				if (checkList.getRecordType().equals(PennantConstants.RCD_ADD)) { // if records type is
																					// new
					if (befCheckList != null || tempCheckList != null) { // if
						// records already exists in the main table
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
								new ErrorDetail(PennantConstants.KEY_FIELD, "41008", errParm, valueParm), usrLanguage));
					}
				}
			}
		} else {
			// for work flow process records or (Record to update or Delete with out work flow)
			if (!checkList.isWorkflow()) { // With out Work flow for update and delete

				if (befCheckList == null) { // if records not exists in the main table
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD, "41002", errParm, valueParm), usrLanguage));
				} else {
					if (oldFinanceCheckListReference != null
							&& !oldFinanceCheckListReference.getLastMntOn().equals(befCheckList.getLastMntOn())) {
						if (StringUtils.trimToEmpty(auditDetail.getAuditTranType())
								.equalsIgnoreCase(PennantConstants.TRAN_DEL)) {
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
									new ErrorDetail(PennantConstants.KEY_FIELD, "41003", errParm, valueParm),
									usrLanguage));
						} else {
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
									new ErrorDetail(PennantConstants.KEY_FIELD, "41004", errParm, valueParm),
									usrLanguage));
						}
					}
				}
			} else {
				if (tempCheckList == null) { // if records not exists in the Work flow table
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, valueParm), usrLanguage));
				}
				if (tempCheckList != null && oldFinanceCheckListReference != null
						&& !oldFinanceCheckListReference.getLastMntOn().equals(tempCheckList.getLastMntOn())) {
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, valueParm), usrLanguage));
				}
			}
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		if ("doApprove".equals(StringUtils.trimToEmpty(method)) || !checkList.isWorkflow()) {
			checkList.setBefImage(befCheckList);
		}

		logger.debug(Literal.LEAVING);
		return auditDetail;
	}

	@Override
	public CheckListDetail getCheckListDetailByDocType(String docType, String finType) {
		return checkListDetailDAO.getCheckListDetailByDocType(docType, finType);
	}

	public void setFinanceReferenceDetailDAO(FinanceReferenceDetailDAO financeReferenceDetailDAO) {
		this.financeReferenceDetailDAO = financeReferenceDetailDAO;
	}

	public void setFinanceCheckListReferenceDAO(FinanceCheckListReferenceDAO financeCheckListReferenceDAO) {
		this.financeCheckListReferenceDAO = financeCheckListReferenceDAO;
	}

	public void setCheckListDetailDAO(CheckListDetailDAO checkListDetailDAO) {
		this.checkListDetailDAO = checkListDetailDAO;
	}

	public void setCustomerDocumentDAO(CustomerDocumentDAO customerDocumentDAO) {
		this.customerDocumentDAO = customerDocumentDAO;
	}

}
