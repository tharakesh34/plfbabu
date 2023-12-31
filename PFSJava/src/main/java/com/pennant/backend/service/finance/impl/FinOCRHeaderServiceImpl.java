package com.pennant.backend.service.finance.impl;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.zkoss.util.resource.Labels;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.finance.FinOCRCaptureDAO;
import com.pennant.backend.dao.finance.FinOCRDetailDAO;
import com.pennant.backend.dao.finance.FinOCRHeaderDAO;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.collateral.CollateralSetup;
import com.pennant.backend.model.documentdetails.DocumentDetails;
import com.pennant.backend.model.finance.FinOCRCapture;
import com.pennant.backend.model.finance.FinOCRDetail;
import com.pennant.backend.model.finance.FinOCRHeader;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.finance.FinOCRHeaderService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.model.dms.DMSModule;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;

/**
 * Service implementation for methods that depends on <b>FinOCRHeader</b>.<br>
 * 
 */
public class FinOCRHeaderServiceImpl extends GenericService<FinOCRHeader> implements FinOCRHeaderService {
	private static final Logger logger = LogManager.getLogger(FinOCRHeaderServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;
	private FinOCRHeaderDAO finOCRHeaderDAO;
	private FinOCRDetailDAO finOCRDetailDAO;
	private FinOCRCaptureDAO finOCRCaptureDAO;

	public FinOCRHeaderServiceImpl() {
		super();
	}

	@Override
	public FinOCRHeader getFinOCRHeaderByRef(String parentRefr, String type) {
		FinOCRHeader ocrHeader = finOCRHeaderDAO.getFinOCRHeaderByRef(parentRefr, type);

		if (ocrHeader != null) {
			ocrHeader.setOcrDetailList(finOCRDetailDAO.getFinOCRDetailsByHeaderID(ocrHeader.getHeaderID(), type));
			ocrHeader.setFinOCRCapturesList(finOCRCaptureDAO.getFinOCRCaptureDetailsByRef(ocrHeader.getFinID(), type));
		}

		return ocrHeader;
	}

	@Override
	public FinOCRHeader getFinOCRHeaderByRef(long finID, String type) {
		FinOCRHeader finOCRHeader = finOCRHeaderDAO.getFinOCRHeaderByRef(finID, type);

		if (finOCRHeader != null) {
			finOCRHeader.setOcrDetailList(finOCRDetailDAO.getFinOCRDetailsByHeaderID(finOCRHeader.getHeaderID(), type));
			finOCRHeader.setFinOCRCapturesList(finOCRCaptureDAO.getFinOCRCaptureDetailsByRef(finID, type));
		}

		return finOCRHeader;
	}

	@Override
	public FinOCRHeader getApprovedFinOCRHeaderByRef(long finID, String type) {
		FinOCRHeader finOCRHeader = finOCRHeaderDAO.getFinOCRHeaderByRef(finID, type);

		if (finOCRHeader != null) {
			finOCRHeader.setDefinitionApproved(true);
			finOCRHeader.setOcrDetailList(finOCRDetailDAO.getFinOCRDetailsByHeaderID(finOCRHeader.getHeaderID(), type));
			finOCRHeader.setFinOCRCapturesList(finOCRCaptureDAO.getFinOCRCaptureDetailsByRef(finID, type));
		}

		return finOCRHeader;
	}

	@Override
	public FinOCRHeader getFinOCRHeaderById(long headerId, String type) {
		FinOCRHeader finOCRHeader = finOCRHeaderDAO.getFinOCRHeaderById(headerId, type);

		if (finOCRHeader != null) {
			finOCRHeader.setOcrDetailList(finOCRDetailDAO.getFinOCRDetailsByHeaderID(finOCRHeader.getHeaderID(), type));
		}

		return finOCRHeader;
	}

	@Override
	public AuditHeader saveOrUpdate(AuditHeader auditHeader, FinanceDetail financeDetail, boolean fromLoan) {
		logger.debug(Literal.ENTERING);

		boolean ocrRequired = true;
		String parentRef = "";
		if (financeDetail != null) {
			ocrRequired = financeDetail.getFinScheduleData().getFinanceMain().isFinOcrRequired();
			FinanceMain financeMain = financeDetail.getFinScheduleData().getFinanceMain();
			parentRef = financeMain.getParentRef();
		}
		FinOCRHeader finOCRHeader = (FinOCRHeader) auditHeader.getAuditDetail().getModelData();

		if (fromLoan && !ocrRequired && finOCRHeader.getHeaderID() > 0) {
			auditHeader = deleteOCR(finOCRHeader, auditHeader);
			auditHeaderDAO.addAudit(auditHeader);
		} else {
			List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
			auditHeader = businessValidation(auditHeader, "saveOrUpdate");
			if (!auditHeader.isNextProcess()) {
				logger.debug(Literal.LEAVING);
				return auditHeader;
			}
			String tableType = "";

			if (finOCRHeader.isWorkflow()) {
				tableType = "_Temp";
			}

			if (!finOCRHeader.isDefinitionApproved()) {
				if (finOCRHeader.isNewRecord()) {
					finOCRHeader.setHeaderID(finOCRHeaderDAO.save(finOCRHeader, tableType));
					auditHeader.getAuditDetail().setModelData(finOCRHeader);
					auditHeader.setAuditReference(String.valueOf(finOCRHeader.getHeaderID()));
				} else {
					finOCRHeaderDAO.update(finOCRHeader, tableType);
				}

				// Fin OCR Step Details
				if (StringUtils.isEmpty(parentRef) && CollectionUtils.isNotEmpty(finOCRHeader.getOcrDetailList())) {
					List<AuditDetail> details = finOCRHeader.getAuditDetailMap().get("FinOCRStepDetails");
					details = processingFinOCRStepList(details, tableType, finOCRHeader);
					auditDetails.addAll(details);
				}
			}

			// Fin OCR Capture Details
			if (CollectionUtils.isNotEmpty(finOCRHeader.getFinOCRCapturesList())) {
				List<AuditDetail> details = finOCRHeader.getAuditDetailMap().get("FinOCRCaptureDetails");
				details = processingFinCaptureList(details, tableType, finOCRHeader);
				auditDetails.addAll(details);
			}

			String[] fields = PennantJavaUtil.getFieldDetails(new FinOCRHeader(), finOCRHeader.getExcludeFields());
			auditHeader.setAuditDetail(new AuditDetail(auditHeader.getAuditTranType(), 1, fields[0], fields[1],
					finOCRHeader.getBefImage(), finOCRHeader));
			auditHeader.setAuditDetails(auditDetails);
			auditHeaderDAO.addAudit(auditHeader);
		}
		logger.debug(Literal.LEAVING);
		return auditHeader;
	}

	private AuditHeader deleteOCR(FinOCRHeader finOCRHeader, AuditHeader auditHeader) {
		long finID = finOCRHeader.getFinID();
		FinOCRHeader existingOCR = finOCRHeaderDAO.getFinOCRHeaderByRef(finID, TableType.TEMP_TAB.getSuffix());
		if (existingOCR != null) {
			String[] fields = null;
			finOCRHeader.setBefImage(finOCRHeader);
			finOCRHeader.setRecordType(PennantConstants.RECORD_TYPE_DEL);
			fields = PennantJavaUtil.getFieldDetails(new FinOCRHeader(), finOCRHeader.getExcludeFields());
			auditHeader.setAuditDetail(new AuditDetail(PennantConstants.TRAN_DEL, 1, fields[0], fields[1],
					finOCRHeader.getBefImage(), finOCRHeader));
			List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
			List<FinOCRDetail> finOCRStepDetails = finOCRHeader.getOcrDetailList();
			if (CollectionUtils.isNotEmpty(finOCRHeader.getOcrDetailList())) {
				FinOCRDetail ocrDetail = new FinOCRDetail();
				FinOCRDetail finOCRDetail = null;
				fields = PennantJavaUtil.getFieldDetails(ocrDetail, ocrDetail.getExcludeFields());
				for (int i = 0; i < finOCRStepDetails.size(); i++) {
					finOCRDetail = finOCRStepDetails.get(i);
					finOCRDetail.setBefImage(finOCRDetail);
					finOCRDetail.setRecordType(PennantConstants.RECORD_TYPE_DEL);
					auditDetails.add(new AuditDetail(PennantConstants.TRAN_DEL, i + 1, fields[0], fields[1],
							finOCRDetail.getBefImage(), finOCRDetail));
				}
				finOCRDetailDAO.deleteList(finOCRHeader.getHeaderID(), TableType.TEMP_TAB.getSuffix());
			}

			// Fin OCR Capture Details.
			List<FinOCRCapture> finOCRCaptureDetails = finOCRHeader.getFinOCRCapturesList();
			if (CollectionUtils.isNotEmpty(finOCRCaptureDetails)) {
				FinOCRCapture ocrCapture = new FinOCRCapture();
				FinOCRCapture finOCRCapture = null;
				fields = PennantJavaUtil.getFieldDetails(ocrCapture, ocrCapture.getExcludeFields());
				for (int i = 0; i < finOCRCaptureDetails.size(); i++) {
					finOCRCapture = finOCRCaptureDetails.get(i);
					finOCRCapture.setBefImage(finOCRCapture);
					finOCRCapture.setRecordType(PennantConstants.RECORD_TYPE_DEL);
					auditDetails.add(new AuditDetail(PennantConstants.TRAN_DEL, i + 1, fields[0], fields[1],
							finOCRCapture.getBefImage(), finOCRCapture));
				}
				finOCRCaptureDAO.deleteList(finID, TableType.TEMP_TAB.getSuffix());
			}

			finOCRHeaderDAO.delete(finOCRHeader, TableType.TEMP_TAB.getSuffix());
			auditHeader.setAuditDetails(auditDetails);
		}
		return auditHeader;
	}

	@Override
	public AuditHeader delete(AuditHeader auditHeader) {
		logger.debug(Literal.ENTERING);
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		auditHeader = businessValidation(auditHeader, "delete");
		if (!auditHeader.isNextProcess()) {
			logger.debug(Literal.LEAVING);
			return auditHeader;
		}

		FinOCRHeader finOCRHeader = (FinOCRHeader) auditHeader.getAuditDetail().getModelData();

		auditDetails.addAll(listDeletion(finOCRHeader, TableType.MAIN_TAB.getSuffix(), auditHeader.getAuditTranType()));
		finOCRHeaderDAO.delete(finOCRHeader, TableType.MAIN_TAB.getSuffix());

		String[] fields = PennantJavaUtil.getFieldDetails(new CollateralSetup(), finOCRHeader.getExcludeFields());

		auditHeader.setAuditDetail(new AuditDetail(auditHeader.getAuditTranType(), 1, fields[0], fields[1],
				finOCRHeader.getBefImage(), finOCRHeader));

		auditHeader.setAuditDetails(auditDetails);
		auditHeaderDAO.addAudit(auditHeader);
		logger.debug(Literal.LEAVING);
		return auditHeader;
	}

	@Override
	public AuditHeader doApprove(AuditHeader auditHeader, FinanceDetail financeDetail, boolean fromLoan) {
		logger.debug(Literal.ENTERING);

		FinOCRHeader finOCRHeader = (FinOCRHeader) auditHeader.getAuditDetail().getModelData();
		String parentRef = "";
		boolean ocrRequired = true;
		if (financeDetail != null) {
			ocrRequired = financeDetail.getFinScheduleData().getFinanceMain().isFinOcrRequired();
			FinanceMain financeMain = financeDetail.getFinScheduleData().getFinanceMain();
			parentRef = financeMain.getParentRef();
		}

		if (fromLoan && !ocrRequired && finOCRHeader.getHeaderID() > 0) {
			auditHeader = deleteOCR(finOCRHeader, auditHeader);
			auditHeaderDAO.addAudit(auditHeader);
		} else {
			List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
			String tranType = "";
			auditHeader = businessValidation(auditHeader, "doApprove");
			if (!auditHeader.isNextProcess()) {
				logger.debug(Literal.LEAVING);
				return auditHeader;
			}

			FinOCRHeader aFinOCRHeader = new FinOCRHeader();
			BeanUtils.copyProperties((FinOCRHeader) auditHeader.getAuditDetail().getModelData(), aFinOCRHeader);

			if (!PennantConstants.RECORD_TYPE_NEW.equals(aFinOCRHeader.getRecordType())) {
				auditHeader.getAuditDetail().setBefImage(finOCRHeaderDAO
						.getFinOCRHeaderById(aFinOCRHeader.getHeaderID(), TableType.MAIN_TAB.getSuffix()));
			}

			if (PennantConstants.RECORD_TYPE_DEL.equals(aFinOCRHeader.getRecordType())) {
				tranType = PennantConstants.TRAN_DEL;
				auditDetails.addAll(listDeletion(aFinOCRHeader, TableType.MAIN_TAB.getSuffix(), tranType));
				finOCRHeaderDAO.delete(aFinOCRHeader, TableType.MAIN_TAB.getSuffix());

			} else {
				aFinOCRHeader.setRoleCode("");
				aFinOCRHeader.setNextRoleCode("");
				aFinOCRHeader.setTaskId("");
				aFinOCRHeader.setNextTaskId("");
				aFinOCRHeader.setWorkflowId(0);

				if (PennantConstants.RECORD_TYPE_NEW.equals(aFinOCRHeader.getRecordType())) {
					tranType = PennantConstants.TRAN_ADD;
					aFinOCRHeader.setRecordType("");
					finOCRHeaderDAO.save(aFinOCRHeader, TableType.MAIN_TAB.getSuffix());
				} else {
					tranType = PennantConstants.TRAN_UPD;
					aFinOCRHeader.setRecordType("");
					finOCRHeaderDAO.update(aFinOCRHeader, TableType.MAIN_TAB.getSuffix());
				}

				// Fin OCR Step Details
				if (StringUtils.isEmpty(parentRef) && CollectionUtils.isNotEmpty(aFinOCRHeader.getOcrDetailList())) {
					List<AuditDetail> details = aFinOCRHeader.getAuditDetailMap().get("FinOCRStepDetails");
					details = processingFinOCRStepList(details, TableType.MAIN_TAB.getSuffix(), aFinOCRHeader);
					auditDetails.addAll(details);
				}

				// Fin OCR Capture Details
				if (CollectionUtils.isNotEmpty(aFinOCRHeader.getFinOCRCapturesList())) {
					List<AuditDetail> details = aFinOCRHeader.getAuditDetailMap().get("FinOCRCaptureDetails");
					details = processingFinCaptureList(details, TableType.MAIN_TAB.getSuffix(), aFinOCRHeader);
					auditDetails.addAll(details);
				}
			}

			if (!aFinOCRHeader.isNewRecord()) {
				auditHeader.setAuditDetails(
						listDeletion(aFinOCRHeader, TableType.TEMP_TAB.getSuffix(), auditHeader.getAuditTranType()));
				finOCRHeaderDAO.delete(aFinOCRHeader, TableType.TEMP_TAB.getSuffix());
				auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
				auditHeaderDAO.addAudit(auditHeader);
			}

			auditHeader.setAuditTranType(tranType);
			auditHeader.getAuditDetail().setAuditTranType(tranType);
			auditHeader.setAuditDetails(auditDetails);
			auditHeader.getAuditDetail().setModelData(aFinOCRHeader);
			auditHeaderDAO.addAudit(auditHeader);
		}
		logger.debug(Literal.LEAVING);
		return auditHeader;
	}

	@Override
	public AuditHeader doReject(AuditHeader auditHeader) {
		logger.debug(Literal.ENTERING);
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		auditHeader = businessValidation(auditHeader, "doReject");
		if (!auditHeader.isNextProcess()) {
			logger.debug(Literal.LEAVING);
			return auditHeader;
		}

		FinOCRHeader finOCRHeader = (FinOCRHeader) auditHeader.getAuditDetail().getModelData();
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);

		String[] fields = PennantJavaUtil.getFieldDetails(new FinOCRHeader(), finOCRHeader.getExcludeFields());
		auditHeader.setAuditDetail(new AuditDetail(auditHeader.getAuditTranType(), 1, fields[0], fields[1],
				finOCRHeader.getBefImage(), finOCRHeader));
		// Fin OCR Capture details deletion
		auditDetails.addAll(listDeletion(finOCRHeader, TableType.TEMP_TAB.getSuffix(), auditHeader.getAuditTranType()));
		finOCRHeaderDAO.delete(finOCRHeader, TableType.TEMP_TAB.getSuffix());

		auditHeader.setAuditDetails(auditDetails);
		auditHeaderDAO.addAudit(auditHeader);
		logger.debug(Literal.LEAVING);
		return auditHeader;
	}

	private AuditHeader businessValidation(AuditHeader auditHeader, String method) {
		logger.debug(Literal.ENTERING);
		auditHeader = doValidation(auditHeader, method);
		auditHeader = nextProcess(auditHeader);
		logger.debug(Literal.LEAVING);
		return auditHeader;
	}

	public AuditHeader doValidation(AuditHeader auditHeader, String method) {
		logger.debug(Literal.ENTERING);

		AuditDetail auditDetail = validate(auditHeader.getAuditDetail(), method, auditHeader.getUsrLanguage());
		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());
		auditHeader = getAuditDetails(auditHeader, method);
		logger.debug(Literal.LEAVING);
		return auditHeader;
	}

	public AuditDetail validate(AuditDetail auditDetail, String method, String usrLanguage) {
		logger.debug(Literal.ENTERING);
		// Get the model object.
		FinOCRHeader finOCRHeader = (FinOCRHeader) auditDetail.getModelData();
		BigDecimal custPortionHeader = BigDecimal.ZERO;
		BigDecimal finPortionHeader = BigDecimal.ZERO;
		BigDecimal totalCustPortion = BigDecimal.ZERO;
		BigDecimal totalFinPortion = BigDecimal.ZERO;
		if (StringUtils.equals(PennantConstants.SEGMENTED_VALUE, finOCRHeader.getOcrType())) {
			custPortionHeader = finOCRHeader.getCustomerPortion();
			finPortionHeader = new BigDecimal(100).subtract(custPortionHeader);
			// checking ocr step details are available or not
			if (CollectionUtils.isEmpty(finOCRHeader.getOcrDetailList())) {
				String[] valueParm = new String[1];
				valueParm[0] = Labels.getLabel("window_FinOCRDialog_title");
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("OCR001", valueParm)));
				return auditDetail;
			} else {
				// checking weather customer, finance portions are equal with header
				for (FinOCRDetail finOCRDetail : finOCRHeader.getOcrDetailList()) {
					if (PennantConstants.RCD_DEL.equalsIgnoreCase(finOCRDetail.getRecordType())
							|| PennantConstants.RECORD_TYPE_CAN.equalsIgnoreCase(finOCRDetail.getRecordType())) {
						continue;
					}
					totalCustPortion = totalCustPortion.add(finOCRDetail.getCustomerContribution());
					totalFinPortion = totalFinPortion.add(finOCRDetail.getFinancerContribution());
				}

				String[] valueParm = new String[2];
				String message = "Total ";
				// check header customer portion value is equal with total payable by customer step's value
				if ((custPortionHeader.compareTo(totalCustPortion)) != 0) {
					valueParm[0] = message.concat(Labels.getLabel("listheader_FinOCRDialog_PayableByCustomer.label"));
					valueParm[1] = String.valueOf(custPortionHeader);
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("90277", valueParm)));
					return auditDetail;
				} else if ((finPortionHeader.compareTo(totalFinPortion)) != 0) { // check header customer portion value
																					// is equal with total payable by
																					// customer step's value
					valueParm[0] = message.concat(Labels.getLabel("listheader_FinOCRDialog_PayableByFinancer.label"));
					valueParm[1] = String.valueOf(finPortionHeader);
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("90277", valueParm)));
					return auditDetail;
				}

				// both cust and financer contributions should equal to 100
				BigDecimal total = totalCustPortion.add(totalFinPortion);
				if (total.compareTo(new BigDecimal(100)) != 0) {
					valueParm[0] = message.concat(Labels.getLabel("listheader_FinOCRDialog_PayableByCustomer.label")
							.concat(", ").concat(Labels.getLabel("listheader_FinOCRDialog_PayableByFinancer.label")));
					valueParm[1] = String.valueOf(total);
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("90277", valueParm)));
					return auditDetail;
				}
			}

			// API check, by passing OCR steps with out split applicable
		} else {
			if (!CollectionUtils.isEmpty(finOCRHeader.getOcrDetailList())) {
				for (FinOCRDetail finOCRDetail : finOCRHeader.getOcrDetailList()) {
					if (!PennantConstants.RCD_DEL.equalsIgnoreCase(finOCRDetail.getRecordType())
							&& !PennantConstants.RECORD_TYPE_CAN.equalsIgnoreCase(finOCRDetail.getRecordType())) {
						String[] valueParm = new String[1];
						valueParm[0] = Labels.getLabel("label_FinOCRDialog_SplitApplicable.value");
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("30561", valueParm)));
						return auditDetail;
					}
				}
			}
		}
		if (finOCRHeader.getTotalDemand().compareTo(BigDecimal.ZERO) == 0) {
			String[] valueParm = new String[1];
			valueParm[0] = "Total Demand  Field should be more than 0 (zero) ";
			auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("OCR001", valueParm)));
			return auditDetail;
		}

		logger.debug(Literal.LEAVING);
		return auditDetail;
	}

	/**
	 * Processing FinOCRHeader and Child details
	 */
	@Override
	public List<AuditDetail> processFinOCRHeader(AuditHeader aAuditHeader, String method) {
		logger.debug(Literal.ENTERING);

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();

		FinanceDetail financeDetail = (FinanceDetail) aAuditHeader.getAuditDetail().getModelData();
		FinanceMain financeMain = financeDetail.getFinScheduleData().getFinanceMain();
		String auditTranType = "";

		if ("saveOrUpdate".equals(method) || "doApprove".equals(method) || "doReject".equals(method)) {
			if (financeMain.isWorkflow()) {
				auditTranType = PennantConstants.TRAN_WF;
			}
		}
		FinOCRHeader finOCRHeader = financeDetail.getFinOCRHeader();

		if (finOCRHeader != null) {
			finOCRHeader = setWorkFlowValues(financeDetail, finOCRHeader, method);
			String[] fields = PennantJavaUtil.getFieldDetails(finOCRHeader, finOCRHeader.getExcludeFields());
			AuditHeader auditHeader = getAuditHeader(finOCRHeader, auditTranType);

			switch (method) {
			case "saveOrUpdate":
				saveOrUpdate(auditHeader, financeDetail, true);
				break;
			case "doApprove":
				doApprove(auditHeader, financeDetail, true);
				break;
			case "doReject":
				doReject(auditHeader);
				break;
			case "delete":
				delete(auditHeader);
				break;
			default:
				break;
			}
			auditDetails.add(new AuditDetail(aAuditHeader.getAuditTranType(), 1, fields[0], fields[1],
					finOCRHeader.getBefImage(), finOCRHeader));
		}
		logger.debug(Literal.LEAVING);
		return auditDetails;
	}

	/**
	 * Setting the workflow values
	 * 
	 * @param financeDetail
	 * @param finOCRHeader
	 * @param method
	 * @return
	 */
	private FinOCRHeader setWorkFlowValues(FinanceDetail financeDetail, FinOCRHeader finOCRHeader, String method) {
		FinanceMain financeMain = financeDetail.getFinScheduleData().getFinanceMain();
		finOCRHeader.setUserDetails(financeMain.getUserDetails());
		finOCRHeader.setWorkflowId(financeMain.getWorkflowId());
		finOCRHeader.setFinID(financeMain.getFinID());
		finOCRHeader.setFinReference(financeMain.getFinReference());
		finOCRHeader.setLastMntBy(financeMain.getLastMntBy());
		finOCRHeader.setLastMntOn(financeMain.getLastMntOn());
		finOCRHeader.setRecordStatus(financeMain.getRecordStatus());
		finOCRHeader.setWorkflowId(financeMain.getWorkflowId());
		finOCRHeader.setTaskId(financeMain.getTaskId());
		finOCRHeader.setNextTaskId(financeMain.getNextTaskId());
		finOCRHeader.setRoleCode(financeMain.getRoleCode());

		// Fin OCR Step details
		if (!CollectionUtils.isEmpty(finOCRHeader.getOcrDetailList())) {
			for (FinOCRDetail details : finOCRHeader.getOcrDetailList()) {
				details.setHeaderID(finOCRHeader.getHeaderID());
				details.setLastMntBy(finOCRHeader.getLastMntBy());
				details.setLastMntOn(new Timestamp(System.currentTimeMillis()));
				details.setUserDetails(finOCRHeader.getUserDetails());
				details.setRecordStatus(finOCRHeader.getRecordStatus());
				details.setWorkflowId(finOCRHeader.getWorkflowId());
				details.setTaskId(finOCRHeader.getTaskId());
				details.setNextTaskId(finOCRHeader.getNextTaskId());
				details.setRoleCode(finOCRHeader.getRoleCode());
				details.setNextRoleCode(finOCRHeader.getNextRoleCode());
				if (PennantConstants.RECORD_TYPE_DEL.equals(finOCRHeader.getRecordType())) {
					if (StringUtils.trimToNull(details.getRecordType()) == null) {
						details.setRecordType(finOCRHeader.getRecordType());
						details.setNewRecord(true);
					}
				}
			}
		}

		// Fin OCR Step details
		if (CollectionUtils.isNotEmpty(finOCRHeader.getFinOCRCapturesList())) {
			for (FinOCRCapture finOCRCapture : finOCRHeader.getFinOCRCapturesList()) {
				finOCRCapture.setLastMntBy(finOCRHeader.getLastMntBy());
				finOCRCapture.setLastMntOn(new Timestamp(System.currentTimeMillis()));
				finOCRCapture.setUserDetails(finOCRHeader.getUserDetails());
				finOCRCapture.setRecordStatus(finOCRHeader.getRecordStatus());
				finOCRCapture.setWorkflowId(finOCRHeader.getWorkflowId());
				finOCRCapture.setTaskId(finOCRHeader.getTaskId());
				finOCRCapture.setNextTaskId(finOCRHeader.getNextTaskId());
				finOCRCapture.setRoleCode(finOCRHeader.getRoleCode());
				finOCRCapture.setNextRoleCode(finOCRHeader.getNextRoleCode());
				if (PennantConstants.RECORD_TYPE_DEL.equals(finOCRHeader.getRecordType())) {
					if (StringUtils.trimToNull(finOCRCapture.getRecordType()) == null) {
						finOCRCapture.setRecordType(finOCRHeader.getRecordType());
						finOCRCapture.setNewRecord(true);
					}
				}
			}
		}
		return finOCRHeader;
	}

	/**
	 * Preparing the audit header
	 * 
	 * @param finOCRHeader
	 * @param tranType
	 * @return
	 */
	private AuditHeader getAuditHeader(FinOCRHeader finOCRHeader, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, finOCRHeader.getBefImage(), finOCRHeader);
		return new AuditHeader(String.valueOf(finOCRHeader.getHeaderID()), null, null, null, auditDetail,
				finOCRHeader.getUserDetails(), new HashMap<String, List<ErrorDetail>>());
	}

	/**
	 * Common Method for Retrieving AuditDetails List
	 * 
	 * @param auditHeader
	 * @param method
	 * @return
	 */
	private AuditHeader getAuditDetails(AuditHeader auditHeader, String method) {
		logger.debug(Literal.ENTERING);
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		HashMap<String, List<AuditDetail>> auditDetailMap = new HashMap<String, List<AuditDetail>>();

		FinOCRHeader finOCRHeader = (FinOCRHeader) auditHeader.getAuditDetail().getModelData();

		String auditTranType = "";

		if ("saveOrUpdate".equals(method) || "doApprove".equals(method) || "doReject".equals(method)) {
			if (finOCRHeader.isWorkflow()) {
				auditTranType = PennantConstants.TRAN_WF;
			}
		}

		// FinOCR Step details
		if (!CollectionUtils.isEmpty(finOCRHeader.getOcrDetailList())) {
			auditDetailMap.put("FinOCRStepDetails", setFinOCRStepDetailsAuditData(finOCRHeader, auditTranType, method));
			auditDetails.addAll(auditDetailMap.get("FinOCRStepDetails"));
		}

		// FinOCR Capture details
		if (CollectionUtils.isNotEmpty(finOCRHeader.getFinOCRCapturesList())) {
			auditDetailMap.put("FinOCRCaptureDetails",
					setFinOCRCaptureDetailsAuditData(finOCRHeader, auditTranType, method));
			auditDetails.addAll(auditDetailMap.get("FinOCRCaptureDetails"));
		}

		finOCRHeader.setAuditDetailMap(auditDetailMap);
		auditHeader.getAuditDetail().setModelData(finOCRHeader);
		auditHeader.setAuditDetails(auditDetails);

		logger.debug(Literal.LEAVING);
		return auditHeader;
	}

	/**
	 * Methods for Creating List of FinOCRSteps Audit Details with detailed fields
	 * 
	 * @param finOCRHeader
	 * @param auditTranType
	 * @param method
	 * @return
	 */
	private List<AuditDetail> setFinOCRStepDetailsAuditData(FinOCRHeader finOCRHeader, String auditTranType,
			String method) {
		logger.debug(Literal.ENTERING);

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();

		FinOCRDetail finOCRDetail = new FinOCRDetail();
		String[] fields = PennantJavaUtil.getFieldDetails(finOCRDetail, finOCRDetail.getExcludeFields());

		for (int i = 0; i < finOCRHeader.getOcrDetailList().size(); i++) {

			FinOCRDetail ocrDetail = finOCRHeader.getOcrDetailList().get(i);
			if (StringUtils.isEmpty(StringUtils.trimToEmpty(ocrDetail.getRecordType()))) {
				continue;
			}
			boolean isRcdType = false;

			if (PennantConstants.RCD_ADD.equalsIgnoreCase(ocrDetail.getRecordType())) {
				ocrDetail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				isRcdType = true;
			} else if (PennantConstants.RCD_UPD.equalsIgnoreCase(ocrDetail.getRecordType())) {
				ocrDetail.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				if (finOCRHeader.isWorkflow()) {
					isRcdType = true;
				}
			} else if (PennantConstants.RCD_DEL.equalsIgnoreCase(ocrDetail.getRecordType())) {
				ocrDetail.setRecordType(PennantConstants.RECORD_TYPE_DEL);
			}

			if ("saveOrUpdate".equals(method) && (isRcdType)) {
				ocrDetail.setNewRecord(true);
			}

			if (!auditTranType.equals(PennantConstants.TRAN_WF)) {
				if (PennantConstants.RECORD_TYPE_NEW.equalsIgnoreCase(ocrDetail.getRecordType())) {
					auditTranType = PennantConstants.TRAN_ADD;
				} else if (PennantConstants.RECORD_TYPE_DEL.equalsIgnoreCase(ocrDetail.getRecordType())
						|| PennantConstants.RECORD_TYPE_CAN.equalsIgnoreCase(ocrDetail.getRecordType())) {
					auditTranType = PennantConstants.TRAN_DEL;
				} else {
					auditTranType = PennantConstants.TRAN_UPD;
				}
			}

			auditDetails.add(
					new AuditDetail(auditTranType, i + 1, fields[0], fields[1], ocrDetail.getBefImage(), ocrDetail));
		}
		logger.debug(Literal.LEAVING);
		return auditDetails;
	}

	/**
	 * Method For Preparing List of AuditDetails for CoOwner Details
	 * 
	 * @param auditDetails
	 * @param type
	 * @return
	 */
	private List<AuditDetail> processingFinOCRStepList(List<AuditDetail> auditDetails, String type,
			FinOCRHeader finOCRHeader) {
		logger.debug(Literal.ENTERING);

		boolean saveRecord = false;
		boolean updateRecord = false;
		boolean deleteRecord = false;
		boolean approveRec = false;

		for (int i = 0; i < auditDetails.size(); i++) {
			FinOCRDetail finOCRDetail = (FinOCRDetail) auditDetails.get(i).getModelData();
			saveRecord = false;
			updateRecord = false;
			deleteRecord = false;
			approveRec = false;
			String rcdType = "";
			String recordStatus = "";
			if (StringUtils.isEmpty(type)) {
				approveRec = true;
				finOCRDetail.setRoleCode("");
				finOCRDetail.setNextRoleCode("");
				finOCRDetail.setTaskId("");
				finOCRDetail.setNextTaskId("");
			}

			finOCRDetail.setWorkflowId(0);
			finOCRDetail.setHeaderID(finOCRHeader.getHeaderID());
			if (PennantConstants.RECORD_TYPE_CAN.equalsIgnoreCase(finOCRDetail.getRecordType())) {
				deleteRecord = true;
			} else if (finOCRDetail.isNewRecord()) {
				saveRecord = true;
				if (PennantConstants.RCD_ADD.equalsIgnoreCase(finOCRDetail.getRecordType())) {
					finOCRDetail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else if (PennantConstants.RCD_DEL.equalsIgnoreCase(finOCRDetail.getRecordType())) {
					finOCRDetail.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				} else if (PennantConstants.RCD_UPD.equalsIgnoreCase(finOCRDetail.getRecordType())) {
					finOCRDetail.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				}

			} else if (PennantConstants.RECORD_TYPE_NEW.equalsIgnoreCase(finOCRDetail.getRecordType())) {
				if (approveRec) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			} else if (PennantConstants.RECORD_TYPE_UPD.equalsIgnoreCase(finOCRDetail.getRecordType())) {
				updateRecord = true;
			} else if (PennantConstants.RECORD_TYPE_DEL.equalsIgnoreCase(finOCRDetail.getRecordType())) {
				if (approveRec) {
					deleteRecord = true;
				} else if (finOCRDetail.isNewRecord()) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			}
			if (approveRec) {
				rcdType = finOCRDetail.getRecordType();
				recordStatus = finOCRDetail.getRecordStatus();
				finOCRDetail.setRecordType("");
				finOCRDetail.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
			}
			if (saveRecord) {
				finOCRDetailDAO.save(finOCRDetail, type);
			}

			if (updateRecord) {
				finOCRDetailDAO.update(finOCRDetail, type);
			}

			if (deleteRecord) {
				finOCRDetailDAO.delete(finOCRDetail, type);
			}

			if (approveRec) {
				finOCRDetail.setRecordType(rcdType);
				finOCRDetail.setRecordStatus(recordStatus);
			}
			auditDetails.get(i).setModelData(finOCRDetail);
		}

		logger.debug(Literal.LEAVING);
		return auditDetails;
	}

	// Method for Deleting all records related to Fin OCR Details in _Temp/Main tables depend on method type
	public List<AuditDetail> listDeletion(FinOCRHeader finOCRHeader, String tableType, String auditTranType) {
		logger.debug(Literal.ENTERING);

		List<AuditDetail> auditList = new ArrayList<AuditDetail>();

		// Fin OCr Step Details.
		List<AuditDetail> finOCRStepDetails = finOCRHeader.getAuditDetailMap().get("FinOCRStepDetails");
		if (finOCRStepDetails != null && finOCRStepDetails.size() > 0) {
			FinOCRDetail ocrDetail = new FinOCRDetail();
			FinOCRDetail finOCRDetail = null;
			String[] fields = PennantJavaUtil.getFieldDetails(ocrDetail, ocrDetail.getExcludeFields());
			for (int i = 0; i < finOCRStepDetails.size(); i++) {
				finOCRDetail = (FinOCRDetail) finOCRStepDetails.get(i).getModelData();
				auditList.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1], finOCRDetail.getBefImage(),
						finOCRDetail));
			}
			finOCRDetailDAO.deleteList(finOCRHeader.getHeaderID(), tableType);
		}

		// Fin OCR Capture Details.
		List<AuditDetail> finOCRCaptureDetails = finOCRHeader.getAuditDetailMap().get("FinOCRCaptureDetails");
		if (CollectionUtils.isNotEmpty(finOCRCaptureDetails)) {
			FinOCRCapture ocrCapture = new FinOCRCapture();
			FinOCRCapture finOCRCapture = null;
			String[] fields = PennantJavaUtil.getFieldDetails(ocrCapture, ocrCapture.getExcludeFields());
			for (int i = 0; i < finOCRCaptureDetails.size(); i++) {
				finOCRCapture = (FinOCRCapture) finOCRCaptureDetails.get(i).getModelData();
				auditList.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1], finOCRCapture.getBefImage(),
						finOCRCapture));
			}
			finOCRCaptureDAO.deleteList(finOCRHeader.getFinID(), tableType);
		}

		logger.debug(Literal.LEAVING);
		return auditList;
	}

	/**
	 * Methods for Creating List of FinOCRSteps Audit Details with detailed fields
	 * 
	 * @param finOCRHeader
	 * @param auditTranType
	 * @param method
	 * @return
	 */
	private List<AuditDetail> setFinOCRCaptureDetailsAuditData(FinOCRHeader finOCRHeader, String auditTranType,
			String method) {
		logger.debug(Literal.ENTERING);

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();

		FinOCRCapture finOCRCapture = new FinOCRCapture();
		String[] fields = PennantJavaUtil.getFieldDetails(finOCRCapture, finOCRCapture.getExcludeFields());

		for (int i = 0; i < finOCRHeader.getFinOCRCapturesList().size(); i++) {

			FinOCRCapture ocrCapture = finOCRHeader.getFinOCRCapturesList().get(i);
			if (StringUtils.isEmpty(StringUtils.trimToEmpty(ocrCapture.getRecordType()))) {
				continue;
			}
			boolean isRcdType = false;

			if (PennantConstants.RCD_ADD.equalsIgnoreCase(ocrCapture.getRecordType())) {
				ocrCapture.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				isRcdType = true;
			} else if (PennantConstants.RCD_UPD.equalsIgnoreCase(ocrCapture.getRecordType())) {
				ocrCapture.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				if (finOCRHeader.isWorkflow()) {
					isRcdType = true;
				}
			} else if (PennantConstants.RCD_DEL.equalsIgnoreCase(ocrCapture.getRecordType())) {
				ocrCapture.setRecordType(PennantConstants.RECORD_TYPE_DEL);
			}

			if ("saveOrUpdate".equals(method) && (isRcdType)) {
				ocrCapture.setNewRecord(true);
			}

			if (!auditTranType.equals(PennantConstants.TRAN_WF)) {
				if (PennantConstants.RECORD_TYPE_NEW.equalsIgnoreCase(ocrCapture.getRecordType())) {
					auditTranType = PennantConstants.TRAN_ADD;
				} else if (PennantConstants.RECORD_TYPE_DEL.equalsIgnoreCase(ocrCapture.getRecordType())
						|| PennantConstants.RECORD_TYPE_CAN.equalsIgnoreCase(ocrCapture.getRecordType())) {
					auditTranType = PennantConstants.TRAN_DEL;
				} else {
					auditTranType = PennantConstants.TRAN_UPD;
				}
			}

			auditDetails.add(
					new AuditDetail(auditTranType, i + 1, fields[0], fields[1], ocrCapture.getBefImage(), ocrCapture));
		}
		logger.debug(Literal.LEAVING);
		return auditDetails;
	}

	/**
	 * Method For Preparing List of AuditDetails for CoOwner Details
	 * 
	 * @param auditDetails
	 * @param type
	 * @return
	 */
	private List<AuditDetail> processingFinCaptureList(List<AuditDetail> auditDetails, String type,
			FinOCRHeader finOCRHeader) {
		logger.debug(Literal.ENTERING);

		boolean saveRecord = false;
		boolean updateRecord = false;
		boolean deleteRecord = false;
		boolean approveRec = false;

		for (int i = 0; i < auditDetails.size(); i++) {
			FinOCRCapture finOCRCapture = (FinOCRCapture) auditDetails.get(i).getModelData();
			finOCRCapture.setFinID(finOCRHeader.getFinID());
			finOCRCapture.setFinReference(finOCRHeader.getFinReference());
			saveRecord = false;
			updateRecord = false;
			deleteRecord = false;
			approveRec = false;
			String rcdType = "";
			String recordStatus = "";
			if (StringUtils.isEmpty(type)) {
				approveRec = true;
				finOCRCapture.setRoleCode("");
				finOCRCapture.setNextRoleCode("");
				finOCRCapture.setTaskId("");
				finOCRCapture.setNextTaskId("");
			}

			finOCRCapture.setWorkflowId(0);
			if (PennantConstants.RECORD_TYPE_CAN.equalsIgnoreCase(finOCRCapture.getRecordType())) {
				deleteRecord = true;
			} else if (finOCRCapture.isNewRecord()) {
				saveRecord = true;
				if (PennantConstants.RCD_ADD.equalsIgnoreCase(finOCRCapture.getRecordType())) {
					finOCRCapture.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else if (PennantConstants.RCD_DEL.equalsIgnoreCase(finOCRCapture.getRecordType())) {
					finOCRCapture.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				} else if (PennantConstants.RCD_UPD.equalsIgnoreCase(finOCRCapture.getRecordType())) {
					finOCRCapture.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				}

			} else if (PennantConstants.RECORD_TYPE_NEW.equalsIgnoreCase(finOCRCapture.getRecordType())) {
				if (approveRec) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			} else if (PennantConstants.RECORD_TYPE_UPD.equalsIgnoreCase(finOCRCapture.getRecordType())) {
				updateRecord = true;
			} else if (PennantConstants.RECORD_TYPE_DEL.equalsIgnoreCase(finOCRCapture.getRecordType())) {
				if (approveRec) {
					deleteRecord = true;
				} else if (finOCRCapture.isNewRecord()) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			}
			if (approveRec) {
				rcdType = finOCRCapture.getRecordType();
				recordStatus = finOCRCapture.getRecordStatus();
				finOCRCapture.setRecordType("");
				finOCRCapture.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
			}
			if (saveRecord) {
				getDocument(finOCRCapture);
				finOCRCaptureDAO.save(finOCRCapture, type);
			}

			if (updateRecord) {
				getDocument(finOCRCapture);
				finOCRCaptureDAO.update(finOCRCapture, type);
			}

			if (deleteRecord) {
				finOCRCaptureDAO.delete(finOCRCapture, type);
			}

			if (approveRec) {
				finOCRCapture.setRecordType(rcdType);
				finOCRCapture.setRecordStatus(recordStatus);
			}
			auditDetails.get(i).setModelData(finOCRCapture);
		}

		logger.debug(Literal.LEAVING);
		return auditDetails;
	}

	@Override
	public byte[] getDocumentManImage(long docRef) {
		return getDocumentImage(docRef);
	}

	private void getDocument(FinOCRCapture finOCRCapture) {
		DocumentDetails dd = new DocumentDetails();
		dd.setFinReference(finOCRCapture.getFinReference());
		dd.setDocName(finOCRCapture.getFileName());
		dd.setDocImage(finOCRCapture.getDocImage());
		if (finOCRCapture.getDocumentRef() != null && finOCRCapture.getDocumentRef() > 0
				&& !finOCRCapture.isNewRecord()) {
			byte[] olddocumentManager = getDocumentImage(finOCRCapture.getDocumentRef());
			if (olddocumentManager != null) {
				byte[] arr1 = olddocumentManager;
				byte[] arr2 = finOCRCapture.getDocImage();
				if (!Arrays.equals(arr1, arr2)) {

					dd.setDocImage(finOCRCapture.getDocImage());
					saveDocument(DMSModule.FINANCE, DMSModule.OCR, dd);
					finOCRCapture.setDocumentRef(dd.getDocRefId());
				}
			} else {
				if (finOCRCapture.getDocImage() != null) {
					saveDocument(DMSModule.FINANCE, DMSModule.OCR, dd);
					finOCRCapture.setDocumentRef(dd.getDocRefId());
				}
			}
		} else {
			dd.setDocImage(finOCRCapture.getDocImage());
			dd.setUserDetails(finOCRCapture.getUserDetails());
			saveDocument(DMSModule.FINANCE, DMSModule.OCR, dd);
			finOCRCapture.setDocumentRef(dd.getDocRefId());
		}
	}

	public void setAuditHeaderDAO(AuditHeaderDAO auditHeaderDAO) {
		this.auditHeaderDAO = auditHeaderDAO;
	}

	public void setFinOCRHeaderDAO(FinOCRHeaderDAO finOCRHeaderDAO) {
		this.finOCRHeaderDAO = finOCRHeaderDAO;
	}

	public void setFinOCRDetailDAO(FinOCRDetailDAO finOCRDetailDAO) {
		this.finOCRDetailDAO = finOCRDetailDAO;
	}

	public FinOCRCaptureDAO getFinOCRCaptureDAO() {
		return finOCRCaptureDAO;
	}

	public void setFinOCRCaptureDAO(FinOCRCaptureDAO finOCRCaptureDAO) {
		this.finOCRCaptureDAO = finOCRCaptureDAO;
	}

}
