package com.pennant.backend.service.systemmasters.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
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
import com.pennant.backend.dao.rmtmasters.FinanceTypeDAO;
import com.pennant.backend.dao.systemmasters.OCRDetailDAO;
import com.pennant.backend.dao.systemmasters.OCRHeaderDAO;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.ocrmaster.OCRDetail;
import com.pennant.backend.model.ocrmaster.OCRHeader;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.systemmasters.OCRHeaderService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;

public class OCRHeaderServiceImpl extends GenericService<OCRHeader> implements OCRHeaderService {
	private static final Logger logger = LogManager.getLogger(OCRHeaderServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;
	private OCRHeaderDAO ocrHeaderDAO;
	private OCRDetailDAO ocrDetailDAO;
	private FinanceTypeDAO financeTypeDAO;

	/**
	 * saveOrUpdate method method do the following steps. 1) Do the Business validation by using
	 * businessValidation(auditHeader) method if there is any error or warning message then return the auditHeader. 2)
	 * Do Add or Update the Record a) Add new Record for the new record in the DB table OCRHEADER/OCRHEADER_Temp by
	 * using OCRHEADERDAO's save method b) Update the Record in the table. based on the module workFlow Configuration.
	 * by using OCRHEADERDAO's update method 3) Audit the record in to AuditHeader and AdtOCRHEADER by using
	 * auditHeaderDAO.addAudit(auditHeader)
	 * 
	 * @param AuditHeader (auditHeader)
	 * @return auditHeader
	 */

	public AuditHeader saveOrUpdate(AuditHeader auditHeader) {
		logger.info(Literal.ENTERING);

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		auditHeader = businessValidation(auditHeader, "saveOrUpdate");
		if (!auditHeader.isNextProcess()) {
			logger.info(Literal.LEAVING);
			return auditHeader;
		}
		OCRHeader ocrHeader = (OCRHeader) auditHeader.getAuditDetail().getModelData();

		TableType tableType = TableType.MAIN_TAB;
		if (ocrHeader.isWorkflow()) {
			tableType = TableType.TEMP_TAB;
		}

		if (ocrHeader.isNewRecord()) {
			ocrHeader.setOcrID((ocrHeaderDAO.save(ocrHeader, tableType)));
			auditHeader.getAuditDetail().setModelData(ocrHeader);
			auditHeader.setAuditReference(String.valueOf(ocrHeader.getHeaderID()));
		} else {
			ocrHeaderDAO.update(ocrHeader, tableType);
		}
		// OCR Details Processing
		if (ocrHeader.getOcrDetailList() != null && !ocrHeader.getOcrDetailList().isEmpty()) {
			List<AuditDetail> details = ocrHeader.getAuditDetailMap().get("OCRDetail");
			details = processingOCRDetailList(details, ocrHeader, tableType);
			auditDetails.addAll(details);
		}
		auditHeader.setAuditDetails(auditDetails);
		auditHeaderDAO.addAudit(auditHeader);

		logger.info(Literal.LEAVING);
		return auditHeader;

	}

	private List<AuditDetail> processingOCRDetailList(List<AuditDetail> auditDetails, OCRHeader ocrHeader,
			TableType type) {
		logger.debug(Literal.ENTERING);

		boolean saveRecord = false;
		boolean updateRecord = false;
		boolean deleteRecord = false;
		boolean approveRec = false;

		for (int i = 0; i < auditDetails.size(); i++) {
			OCRDetail ocrDetail = (OCRDetail) auditDetails.get(i).getModelData();
			saveRecord = false;
			updateRecord = false;
			deleteRecord = false;
			approveRec = false;
			String rcdType = "";
			String recordStatus = "";
			if (StringUtils.isEmpty(type.getSuffix())) {
				approveRec = true;
				ocrDetail.setRoleCode("");
				ocrDetail.setNextRoleCode("");
				ocrDetail.setTaskId("");
				ocrDetail.setNextTaskId("");
			}

			if (!StringUtils.isEmpty(ocrDetail.getRecordType())) {
				ocrDetail.setRecordStatus(ocrHeader.getRecordStatus());
			}

			ocrDetail.setWorkflowId(0);
			ocrDetail.setHeaderID(ocrHeader.getHeaderID());
			if (PennantConstants.RECORD_TYPE_CAN.equalsIgnoreCase(ocrDetail.getRecordType())) {
				deleteRecord = true;
			} else if (ocrHeader.isNewRecord()) {
				saveRecord = true;
				if (PennantConstants.RCD_ADD.equalsIgnoreCase(ocrDetail.getRecordType())) {
					ocrDetail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else if (PennantConstants.RCD_DEL.equalsIgnoreCase(ocrDetail.getRecordType())) {
					ocrDetail.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				} else if (PennantConstants.RCD_UPD.equalsIgnoreCase(ocrDetail.getRecordType())) {
					ocrDetail.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				}

			} else if (PennantConstants.RECORD_TYPE_NEW.equalsIgnoreCase(ocrDetail.getRecordType())) {
				if (approveRec) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			} else if (PennantConstants.RECORD_TYPE_UPD.equalsIgnoreCase(ocrDetail.getRecordType())) {
				updateRecord = true;
			} else if (PennantConstants.RECORD_TYPE_DEL.equalsIgnoreCase(ocrDetail.getRecordType())) {
				if (approveRec) {
					deleteRecord = true;
				} else if (ocrDetail.isNewRecord()) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			}
			if (approveRec) {
				rcdType = ocrDetail.getRecordType();
				recordStatus = ocrDetail.getRecordStatus();
				ocrDetail.setRecordType("");
				ocrDetail.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
			}
			if (saveRecord) {
				ocrDetailDAO.save(ocrDetail, type);
			}

			if (updateRecord) {
				ocrDetailDAO.update(ocrDetail, type);
			}

			if (deleteRecord) {
				ocrDetailDAO.delete(ocrDetail, type);
			}

			if (approveRec) {
				ocrDetail.setRecordType(rcdType);
				ocrDetail.setRecordStatus(recordStatus);
			}
			auditDetails.get(i).setModelData(ocrDetail);
		}

		logger.debug(Literal.LEAVING);
		return auditDetails;

	}

	private AuditHeader getAuditDetails(AuditHeader auditHeader, String method) {
		logger.debug(Literal.ENTERING);

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		HashMap<String, List<AuditDetail>> auditDetailMap = new HashMap<String, List<AuditDetail>>();

		OCRHeader ocrHeader = (OCRHeader) auditHeader.getAuditDetail().getModelData();
		String auditTranType = "";

		if ("saveOrUpdate".equals(method) || "doApprove".equals(method) || "doReject".equals(method)) {
			if (ocrHeader.isWorkflow()) {
				auditTranType = PennantConstants.TRAN_WF;
			}
		}
		// ocrHeader details
		if (ocrHeader.getOcrDetailList() != null && ocrHeader.getOcrDetailList().size() > 0) {
			auditDetailMap.put("OCRDetail", setOCRDetailAuditData(ocrHeader, auditTranType, method));
			auditDetails.addAll(auditDetailMap.get("OCRDetail"));
		}
		ocrHeader.setAuditDetailMap(auditDetailMap);
		auditHeader.getAuditDetail().setModelData(ocrHeader);
		auditHeader.setAuditDetails(auditDetails);

		logger.debug(Literal.LEAVING);
		return auditHeader;
	}

	private List<AuditDetail> setOCRDetailAuditData(OCRHeader ocrHeader, String auditTranType, String method) {
		logger.debug(Literal.ENTERING);

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();

		OCRDetail ocrDetail = new OCRDetail();
		String[] fields = PennantJavaUtil.getFieldDetails(ocrDetail);
		OCRDetail detail = null;

		for (int i = 0; i < ocrHeader.getOcrDetailList().size(); i++) {

			detail = ocrHeader.getOcrDetailList().get(i);
			if (StringUtils.isEmpty(detail.getRecordType())) {
				continue;
			}

			detail.setWorkflowId(ocrHeader.getWorkflowId());
			detail.setHeaderID(ocrHeader.getHeaderID());

			boolean isRcdType = false;

			if (detail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
				detail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				isRcdType = true;
			} else if (detail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
				detail.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				if (ocrHeader.isWorkflow()) {
					isRcdType = true;
				}
			} else if (detail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
				detail.setRecordType(PennantConstants.RECORD_TYPE_DEL);
			}

			if ("saveOrUpdate".equals(method) && (isRcdType)) {
				detail.setNewRecord(true);
			}

			if (!auditTranType.equals(PennantConstants.TRAN_WF)) {
				if (detail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
					auditTranType = PennantConstants.TRAN_ADD;
				} else if (detail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)
						|| detail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
					auditTranType = PennantConstants.TRAN_DEL;
				} else {
					auditTranType = PennantConstants.TRAN_UPD;
				}
			}

			detail.setUserDetails(ocrHeader.getUserDetails());
			detail.setLastMntOn(ocrHeader.getLastMntOn());
			detail.setLastMntBy(ocrHeader.getLastMntBy());
			auditDetails.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1], detail.getBefImage(), detail));
		}
		logger.debug(Literal.LEAVING);
		return auditDetails;
	}

	/**
	 * delete method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) delete Record for the DB table
	 * OCRHEADER by using OCRHeaderDAO's delete method with type as Blank 3) Audit the record in to AuditHeader and
	 * AdtOCRHEADER by using auditHeaderDAO.addAudit(auditHeader)
	 * 
	 * @param AuditHeader (auditHeader)
	 * @return auditHeader
	 */

	@Override
	public AuditHeader delete(AuditHeader auditHeader) {
		logger.info(Literal.ENTERING);

		auditHeader = businessValidation(auditHeader, "delete");
		if (!auditHeader.isNextProcess()) {
			logger.info(Literal.LEAVING);
			return auditHeader;
		}

		OCRHeader ocrHeader = (OCRHeader) auditHeader.getAuditDetail().getModelData();
		ocrHeaderDAO.delete(ocrHeader, TableType.MAIN_TAB);
		auditHeaderDAO.addAudit(auditHeader);

		logger.info(Literal.LEAVING);
		return auditHeader;
	}

	/**
	 * getOCRHeader fetch the details by using OCRHEADERDAO getOCRHeader method.
	 * 
	 * @param id   (String)
	 * @param type (String) ""/_Temp/_View
	 * @return ocrHeader
	 */

	@Override
	public OCRHeader getOCRHeader(long headerId) {
		OCRHeader ocrHeader = ocrHeaderDAO.getOCRHeaderById(headerId, "_View");
		if (ocrHeader != null) {
			ocrHeader.setOcrDetailList(ocrDetailDAO.getOCRDetailList(headerId, "_View"));
		}
		return ocrHeader;
	}

	/**
	 * getApprovedOCRHeader fetch the details by using ocrHeaderDAO's getApprovedOCRHeader method . with parameter id
	 * and type as blank. it fetches the approved records from the OCRHEADER.
	 * 
	 * @param id (String)
	 * @return Academic
	 */
	@Override
	public OCRHeader getApprovedOCRHeader(long headerId) {
		OCRHeader ocrHeader = ocrHeaderDAO.getOCRHeaderById(headerId, "_AView");
		if (ocrHeader != null) {
			ocrHeader.setOcrDetailList(ocrDetailDAO.getOCRDetailList(ocrHeader.getHeaderID(), "_AView"));
		}
		return ocrHeader;
	}

	/**
	 * doApprove method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) based on the Record type do
	 * following actions a) DELETE Delete the record from the main table by using ocrHeaderDAO.delete with parameters
	 * ocrheader,"" b) NEW Add new record in to main table by using ocrHeaderDAO.save with parameters ocrheader,"" c)
	 * EDIT Update record in the main table by using ocrHeaderDAO.update with parameters ocrheader,"" 3) Delete the
	 * record from the workFlow table by using ocrHeaderDAO.delete with parameters ocrheader,"_Temp" 4) Audit the record
	 * in to AuditHeader and Adtocrheader by using auditHeaderDAO.addAudit(auditHeader) for Work flow 5) Audit the
	 * record in to AuditHeader and Adtocrheader by using auditHeaderDAO.addAudit(auditHeader) based on the transaction
	 * Type.
	 * 
	 * @param AuditHeader (auditHeader)
	 * @return auditHeader
	 */
	@Override
	public AuditHeader doApprove(AuditHeader auditHeader) {
		logger.info(Literal.ENTERING);

		String tranType = "";
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		auditHeader = businessValidation(auditHeader, "doApprove");

		if (!auditHeader.isNextProcess()) {
			return auditHeader;
		}

		OCRHeader ocrHeader = new OCRHeader();
		BeanUtils.copyProperties((OCRHeader) auditHeader.getAuditDetail().getModelData(), ocrHeader);

		if (!PennantConstants.RECORD_TYPE_NEW.equals(ocrHeader.getRecordType())) {
			auditHeader.getAuditDetail().setBefImage(ocrHeaderDAO.getOCRHeaderById(ocrHeader.getHeaderID(), ""));
		}

		if (ocrHeader.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;
			if (ocrHeader.getOcrDetailList() != null && !ocrHeader.getOcrDetailList().isEmpty()) {
				List<AuditDetail> details = ocrHeader.getAuditDetailMap().get("OCRDetail");
				details = processingOCRDetailList(details, ocrHeader, TableType.MAIN_TAB);
				auditDetails.addAll(details);
			}
			ocrHeaderDAO.delete(ocrHeader, TableType.MAIN_TAB);

		} else {
			ocrHeader.setRoleCode("");
			ocrHeader.setNextRoleCode("");
			ocrHeader.setTaskId("");
			ocrHeader.setNextTaskId("");
			ocrHeader.setWorkflowId(0);

			if (ocrHeader.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				ocrHeader.setRecordType("");
				ocrHeaderDAO.save(ocrHeader, TableType.MAIN_TAB);
			} else {
				tranType = PennantConstants.TRAN_UPD;
				ocrHeader.setRecordType("");
				ocrHeaderDAO.update(ocrHeader, TableType.MAIN_TAB);
			}
			// OCR Details
			if (ocrHeader.getOcrDetailList() != null && !ocrHeader.getOcrDetailList().isEmpty()) {
				List<AuditDetail> details = ocrHeader.getAuditDetailMap().get("OCRDetail");
				details = processingOCRDetailList(details, ocrHeader, TableType.MAIN_TAB);
				auditDetails.addAll(details);
			}
		}
		auditHeader.setAuditDetails(listDeletion(ocrHeader, TableType.TEMP_TAB, PennantConstants.TRAN_WF));
		ocrHeaderDAO.delete(ocrHeader, TableType.TEMP_TAB);
		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		auditHeaderDAO.addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(ocrHeader);
		auditHeaderDAO.addAudit(auditHeader);

		logger.info(Literal.LEAVING);
		return auditHeader;

	}

	/**
	 * doReject method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) Delete the record from the
	 * workFlow table by using ocrHeaderDAO.delete with parameters ocrheader,"_Temp" 3) Audit the record in to
	 * AuditHeader and Adtocrheader by using auditHeaderDAO.addAudit(auditHeader) for Work flow
	 * 
	 * @param AuditHeader (auditHeader)
	 * @return auditHeader
	 */
	@Override
	public AuditHeader doReject(AuditHeader auditHeader) {
		logger.info(Literal.ENTERING);

		auditHeader = businessValidation(auditHeader, "doApprove");
		if (!auditHeader.isNextProcess()) {
			logger.info(Literal.LEAVING);
			return auditHeader;
		}

		OCRHeader ocrHeader = (OCRHeader) auditHeader.getAuditDetail().getModelData();
		auditHeader.setAuditDetails(listDeletion(ocrHeader, TableType.TEMP_TAB, PennantConstants.TRAN_WF));

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		ocrHeaderDAO.delete(ocrHeader, TableType.TEMP_TAB);
		auditHeaderDAO.addAudit(auditHeader);

		logger.info(Literal.LEAVING);
		return auditHeader;
	}

	/**
	 * businessValidation method do the following steps. 1) get the details from the auditHeader. 2) fetch the details
	 * from the tables 3) Validate the Record based on the record details. 4) Validate for any business validation.
	 * 
	 * @param AuditHeader (auditHeader)
	 * @return auditHeader
	 */
	private AuditHeader businessValidation(AuditHeader auditHeader, String method) {
		logger.debug(Literal.ENTERING);

		AuditDetail auditDetail = validation(auditHeader.getAuditDetail(), auditHeader.getUsrLanguage());
		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());
		auditHeader = nextProcess(auditHeader);
		auditHeader = getAuditDetails(auditHeader, method);
		logger.debug(Literal.LEAVING);
		return auditHeader;
	}

	/**
	 * For Validating AuditDetals object getting from Audit Header, if any mismatch conditions Fetch the error details
	 * from ocrHeaderDAO.getErrorDetail with Error ID and language as parameters. if any error/Warnings then assign the
	 * to auditDeail Object
	 * 
	 * @param auditDetail
	 * @param usrLanguage
	 * @return
	 */
	private AuditDetail validation(AuditDetail auditDetail, String usrLanguage) {
		logger.debug(Literal.ENTERING);
		BigDecimal custPortionHeader = BigDecimal.ZERO;
		BigDecimal finPortionHeader = BigDecimal.ZERO;
		BigDecimal totalCustPortion = BigDecimal.ZERO;
		BigDecimal totalFinPortion = BigDecimal.ZERO;

		// Get the model object.
		OCRHeader ocrHeader = (OCRHeader) auditDetail.getModelData();
		// Check the unique keys.
		if (ocrHeader.isNewRecord() && PennantConstants.RECORD_TYPE_NEW.equals(ocrHeader.getRecordType())
				&& ocrHeaderDAO.isDuplicateKey(ocrHeader.getOcrID(),
						ocrHeader.isWorkflow() ? TableType.BOTH_TAB : TableType.MAIN_TAB)) {
			String[] parameters = new String[1];
			parameters[0] = PennantJavaUtil.getLabel("label_OcrID") + ": " + ocrHeader.getOcrID();

			auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("41001", parameters)));
			return auditDetail;
		}

		if (!PennantConstants.RECORD_TYPE_DEL.equalsIgnoreCase(ocrHeader.getRecordType())
				&& !PennantConstants.RECORD_TYPE_CAN.equalsIgnoreCase(ocrHeader.getRecordType())) {
			if (StringUtils.equals(PennantConstants.SEGMENTED_VALUE, ocrHeader.getOcrType())) {
				custPortionHeader = ocrHeader.getCustomerPortion();
				finPortionHeader = new BigDecimal(100).subtract(custPortionHeader);
				// checking ocr step details are available or not
				if (CollectionUtils.isEmpty(ocrHeader.getOcrDetailList())) {
					String[] valueParm = new String[1];
					valueParm[0] = Labels.getLabel("window_OCRStep_Details.title");
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("30561", valueParm)));
					return auditDetail;
				} else {
					// checking weather customer, OCR portions are equal with header
					for (OCRDetail ocrDetail : ocrHeader.getOcrDetailList()) {
						if (PennantConstants.RECORD_TYPE_DEL.equalsIgnoreCase(ocrDetail.getRecordType())
								|| PennantConstants.RECORD_TYPE_CAN.equalsIgnoreCase(ocrDetail.getRecordType())) {
							continue;
						}
						totalCustPortion = totalCustPortion.add(ocrDetail.getCustomerContribution());
						totalFinPortion = totalFinPortion.add(ocrDetail.getFinancerContribution());
					}

					String[] valueParm = new String[2];
					String message = "Total ";
					// check header customer portion value is equal with total payable by customer step's value
					if (!(custPortionHeader.compareTo(totalCustPortion) == 0)) {
						valueParm[0] = message.concat(Labels.getLabel("listheader_OCRSteps_PayableByCustomer.label"));
						valueParm[1] = String.valueOf(custPortionHeader);
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("90277", valueParm)));
						return auditDetail;
					} else if (!(finPortionHeader.compareTo(totalFinPortion) == 0)) { // check header customer portion
																						// value is equal with total
																						// payable by customer step's
																						// value
						valueParm[0] = message.concat(Labels.getLabel("listheader_OCRSteps_PayableByFinancier.label"));
						valueParm[1] = String.valueOf(finPortionHeader);
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("90277", valueParm)));
						return auditDetail;
					}

					// both cust and financer contributions should equal to 100
					BigDecimal total = totalCustPortion.add(totalFinPortion);
					if (total.compareTo(new BigDecimal(100)) != 0) {
						valueParm[0] = message.concat(Labels.getLabel("listheader_OCRSteps_PayableByCustomer.label")
								.concat(", ").concat(Labels.getLabel("listheader_OCRSteps_PayableByFinancier.label")));
						valueParm[1] = String.valueOf(total);
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("90277", valueParm)));
						return auditDetail;
					}
				}

			}
		}

		if (ocrHeader.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			List<String> allowOCRList = financeTypeDAO.getAllowedOCRList();
			for (String ocr : allowOCRList) {
				if (StringUtils.isNotEmpty(ocr)) {
					String[] ocrs = ocr.split(",");
					for (int i = 0; i < ocrs.length; i++) {
						if (StringUtils.equals(ocrs[i], ocrHeader.getOcrID())) {
							String[] valueParm = new String[1];
							valueParm[0] = ocrHeader.getOcrID();
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("41006", valueParm)));
							return auditDetail;
						}
					}
				}
			}

		}

		logger.debug(Literal.LEAVING);
		return auditDetail;

	}

	@Override
	public OCRHeader getOCRHeaderByOCRId(String ocrID, String type) {
		// getting OCR Header
		OCRHeader ocrHeader = ocrHeaderDAO.getOCRHeaderByOCRId(ocrID, type);
		// getting OCR Details
		if (ocrHeader != null) {
			ocrHeader.setOcrDetailList(ocrDetailDAO.getOCRDetailList(ocrHeader.getHeaderID(), type));
		}
		return ocrHeader;
	}

	// Method for Deleting all records related to OCRHeader in _Temp/Main tables
	// depend on method type
	private List<AuditDetail> listDeletion(OCRHeader ocrHeader, TableType tableType, String auditTranType) {
		logger.debug(Literal.ENTERING);

		List<AuditDetail> auditList = new ArrayList<AuditDetail>();

		// OCR Details
		List<AuditDetail> auditDetails = ocrHeader.getAuditDetailMap().get("OCRDetail");
		OCRDetail aOcrDetail = new OCRDetail();
		OCRDetail ocrDetail = null;
		if (auditDetails != null && auditDetails.size() > 0) {
			String[] fields = PennantJavaUtil.getFieldDetails(aOcrDetail);
			for (int i = 0; i < auditDetails.size(); i++) {
				ocrDetail = (OCRDetail) auditDetails.get(i).getModelData();
				ocrDetail.setRecordType(PennantConstants.RECORD_TYPE_CAN);
				auditList.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1], ocrDetail.getBefImage(),
						ocrDetail));
			}
			ocrDetailDAO.deleteList(ocrDetail, tableType);
		}

		logger.debug(Literal.LEAVING);
		return auditList;
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public void setAuditHeaderDAO(AuditHeaderDAO auditHeaderDAO) {
		this.auditHeaderDAO = auditHeaderDAO;
	}

	public void setOcrHeaderDAO(OCRHeaderDAO ocrHeaderDAO) {
		this.ocrHeaderDAO = ocrHeaderDAO;
	}

	public void setOcrDetailDAO(OCRDetailDAO ocrDetailDAO) {
		this.ocrDetailDAO = ocrDetailDAO;
	}

	public FinanceTypeDAO getFinanceTypeDAO() {
		return financeTypeDAO;
	}

	public void setFinanceTypeDAO(FinanceTypeDAO financeTypeDAO) {
		this.financeTypeDAO = financeTypeDAO;
	}

}