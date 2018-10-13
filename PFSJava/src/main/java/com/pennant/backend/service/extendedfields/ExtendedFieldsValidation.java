package com.pennant.backend.service.extendedfields;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.administration.SecurityRightDAO;
import com.pennant.backend.dao.solutionfactory.ExtendedFieldDetailDAO;
import com.pennant.backend.dao.staticparms.ExtendedFieldHeaderDAO;
import com.pennant.backend.model.administration.SecurityRight;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.extendedfield.ExtendedFieldHeader;
import com.pennant.backend.model.solutionfactory.ExtendedFieldDetail;
import com.pennant.backend.util.CollateralConstants;
import com.pennant.backend.util.ExtendedFieldConstants;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;

public class ExtendedFieldsValidation {
	private static final Logger logger = Logger.getLogger(ExtendedFieldsValidation.class);

	private ExtendedFieldDetailDAO extendedFieldDetailDAO;
	private ExtendedFieldHeaderDAO extendedFieldHeaderDAO;
	private SecurityRightDAO securityRightDAO;

	public ExtendedFieldsValidation(ExtendedFieldDetailDAO extendedFieldDetailDAO,
			ExtendedFieldHeaderDAO extendedFieldHeaderDAO) {
		this.extendedFieldDetailDAO = extendedFieldDetailDAO;
		this.extendedFieldHeaderDAO = extendedFieldHeaderDAO;
	}

	//using in ExtFieldConfigServiceImpl for Loan and Customer modules
	public ExtendedFieldsValidation(ExtendedFieldDetailDAO extendedFieldDetailDAO,
			ExtendedFieldHeaderDAO extendedFieldHeaderDAO, SecurityRightDAO securityRightDAO) {
		this.extendedFieldDetailDAO = extendedFieldDetailDAO;
		this.extendedFieldHeaderDAO = extendedFieldHeaderDAO;
		this.securityRightDAO = securityRightDAO;
	}

	public ExtendedFieldDetailDAO getExtendedFieldDetailDAO() {
		return extendedFieldDetailDAO;
	}

	public void setExtendedFieldDetailDAO(ExtendedFieldDetailDAO extendedFieldDetailDAO) {
		this.extendedFieldDetailDAO = extendedFieldDetailDAO;
	}

	public ExtendedFieldHeaderDAO getExtendedFieldHeaderDAO() {
		return extendedFieldHeaderDAO;
	}

	public void setExtendedFieldHeaderDAO(ExtendedFieldHeaderDAO extendedFieldHeaderDAO) {
		this.extendedFieldHeaderDAO = extendedFieldHeaderDAO;
	}

	public List<AuditDetail> extendedFieldsListValidation(List<AuditDetail> auditDetails, String method,
			String usrLanguage) {
		if (auditDetails != null && auditDetails.size() > 0) {
			List<AuditDetail> details = new ArrayList<AuditDetail>();
			for (int i = 0; i < auditDetails.size(); i++) {
				AuditDetail auditDetail = validate(auditDetails.get(i), method, usrLanguage);
				details.add(auditDetail);
			}
			return details;
		}
		return new ArrayList<AuditDetail>();
	}

	/**
	 * Method for Validating Extended Field header Details
	 * 
	 * @param auditDetail
	 * @param usrLanguage
	 * @param method
	 * @return
	 */
	public AuditDetail extendedFieldsHeaderValidation(AuditDetail auditDetail, String usrLanguage, String method) {
		logger.debug("Entering");

		auditDetail.setErrorDetails(new ArrayList<ErrorDetail>());
		ExtendedFieldHeader extendedFieldHeader = (ExtendedFieldHeader) auditDetail.getModelData();

		ExtendedFieldHeader tempExtendedFieldHeader = null;
		if (extendedFieldHeader.isWorkflow()) {
			tempExtendedFieldHeader = getExtendedFieldHeaderDAO().getExtendedFieldHeaderByModuleName(
					extendedFieldHeader.getModuleName(), extendedFieldHeader.getSubModuleName(),
					extendedFieldHeader.getEvent(), "_Temp");
		}
		ExtendedFieldHeader befExtendedFieldHeader = getExtendedFieldHeaderDAO().getExtendedFieldHeaderByModuleName(
				extendedFieldHeader.getModuleName(), extendedFieldHeader.getSubModuleName(),
				extendedFieldHeader.getEvent(), "");

		ExtendedFieldHeader oldExtendedFieldHeader = extendedFieldHeader.getBefImage();

		String[] errParm = new String[3];
		String[] valueParm = new String[3];

		valueParm[0] = extendedFieldHeader.getModuleName();
		valueParm[1] = extendedFieldHeader.getSubModuleName();

		errParm[0] = PennantJavaUtil.getLabel("label_ModuleName") + ":" + valueParm[0];
		errParm[1] = PennantJavaUtil.getLabel("label_SubModuleName") + ":" + valueParm[1];

		if (extendedFieldHeader.getEvent() != null) {
			valueParm[2] = StringUtils.trimToEmpty(extendedFieldHeader.getEvent());
			errParm[2] = PennantJavaUtil.getLabel("label_FinEvent") + ":" + valueParm[2];
		}

		if (extendedFieldHeader.isNew()) { // for New record or new record into work flow

			if (!extendedFieldHeader.isWorkflow()) {// With out Work flow only new records  
				if (befExtendedFieldHeader != null) { // Record Already Exists in the table then error  
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD, "41015", errParm, valueParm), usrLanguage));
				}
			} else { // with work flow
				if (extendedFieldHeader.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) { // if records type is new
					if (befExtendedFieldHeader != null || tempExtendedFieldHeader != null) { // if records already exists in the main table
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
								new ErrorDetail(PennantConstants.KEY_FIELD, "41008", errParm, valueParm), usrLanguage));
					}
				} else { // if records not exists in the Main flow table
					if (befExtendedFieldHeader == null || tempExtendedFieldHeader != null) {
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
								new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, valueParm), usrLanguage));
					}
				}
			}
		} else {
			// for work flow process records or (Record to update or Delete with out work flow)
			if (!extendedFieldHeader.isWorkflow()) { // With out Work flow for update and delete

				if (befExtendedFieldHeader == null) { // if records not exists in the main table
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD, "41002", errParm, valueParm), usrLanguage));
				} else {
					if (oldExtendedFieldHeader != null
							&& !oldExtendedFieldHeader.getLastMntOn().equals(befExtendedFieldHeader.getLastMntOn())) {
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

				if (tempExtendedFieldHeader == null) { // if records not exists in the Work flow table 
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, valueParm), usrLanguage));
				}

				if (oldExtendedFieldHeader != null && tempExtendedFieldHeader != null
						&& !oldExtendedFieldHeader.getLastMntOn().equals(tempExtendedFieldHeader.getLastMntOn())) {
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, valueParm), usrLanguage));
				}
			}
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		if ("doApprove".equals(StringUtils.trimToEmpty(method)) || !extendedFieldHeader.isWorkflow()) {
			auditDetail.setBefImage(befExtendedFieldHeader);
		}
		logger.debug("Leaving");
		return auditDetail;
	}

	/**
	 * Method for validating Extended Field Details
	 * 
	 * @param auditDetail
	 * @param usrLanguage
	 * @param method
	 * @return
	 */
	private AuditDetail validate(AuditDetail auditDetail, String method, String usrLanguage) {

		auditDetail.setErrorDetails(new ArrayList<ErrorDetail>());
		ExtendedFieldDetail details = (ExtendedFieldDetail) auditDetail.getModelData();

		ExtendedFieldDetail tempExtendedFieldDetail = null;
		if (details.isWorkflow()) {
			tempExtendedFieldDetail = getExtendedFieldDetailDAO().getExtendedFieldDetailById(details.getId(),
					details.getFieldName(), details.getExtendedType(), "_Temp");

		}
		ExtendedFieldDetail befExtendedFieldDetail = getExtendedFieldDetailDAO()
				.getExtendedFieldDetailById(details.getId(), details.getFieldName(), details.getExtendedType(), "");

		ExtendedFieldDetail oldExtendedFieldDetail = details.getBefImage();

		String[] errParm = new String[1];
		String[] valueParm = new String[1];
		valueParm[0] = String.valueOf(details.getId());
		errParm[0] = PennantJavaUtil.getLabel("label_ModuleId") + ":" + valueParm[0];

		if (details.isNew()) { // for New record or new record into work flow

			if (!details.isWorkflow()) {// With out Work flow only new records  
				if (befExtendedFieldDetail != null) { // Record Already Exists in the table then error  
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm, valueParm), usrLanguage));
				}
			} else { // with work flow
				if (details.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) { // if records type is new
					if (befExtendedFieldDetail != null || tempExtendedFieldDetail != null) { // if records already exists in the main table
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
								new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm, valueParm), usrLanguage));
					}
				} else { // if records not exists in the Main flow table
					if (befExtendedFieldDetail == null || tempExtendedFieldDetail != null) {
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
								new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, valueParm), usrLanguage));
					}
				}
			}
		} else {
			// for work flow process records or (Record to update or Delete with out work flow)
			if (!details.isWorkflow()) { // With out Work flow for update and delete

				if (befExtendedFieldDetail == null) { // if records not exists in the main table
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD, "41002", errParm, valueParm), usrLanguage));
				} else {
					if (oldExtendedFieldDetail != null
							&& !oldExtendedFieldDetail.getLastMntOn().equals(befExtendedFieldDetail.getLastMntOn())) {
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

				if (tempExtendedFieldDetail == null) { // if records not exists in the Work flow table 
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, valueParm), usrLanguage));
				}

				if (oldExtendedFieldDetail != null
						&& !oldExtendedFieldDetail.getLastMntOn().equals(tempExtendedFieldDetail.getLastMntOn())) {
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, valueParm), usrLanguage));
				}
			}
		}
		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		if ("doApprove".equals(StringUtils.trimToEmpty(method)) || !details.isWorkflow()) {
			auditDetail.setBefImage(befExtendedFieldDetail);
		}
		return auditDetail;
	}

	/**
	 * Methods for Creating List of Audit Details with detailed fields
	 * 
	 * @param customerDetails
	 * @param auditTranType
	 * @param method
	 * @return
	 */
	public List<AuditDetail> setExtendedFieldsAuditData(ExtendedFieldHeader extendedFldHeader, String auditTranType,
			String method) {
		logger.debug("Entering");

		int count = 0;
		List<AuditDetail> auditDetails = prepareAuditData(extendedFldHeader,
				extendedFldHeader.getExtendedFieldDetails(), auditTranType, method, count);

		logger.debug("Leaving");
		return auditDetails;
	}

	public List<AuditDetail> setTechValuationFieldsAuditData(ExtendedFieldHeader extendedFldHeader,
			String auditTranType, String method, int count) {
		logger.debug("Entering");

		List<AuditDetail> auditDetails = prepareAuditData(extendedFldHeader,
				extendedFldHeader.getTechnicalValuationDetailList(), auditTranType, method, count);

		logger.debug("Leaving");
		return auditDetails;
	}

	private List<AuditDetail> prepareAuditData(ExtendedFieldHeader extendedFldHeader,
			List<ExtendedFieldDetail> extendedFieldDetailList, String auditTranType, String method, int count) {
		logger.debug("Entering");

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		String[] fields = PennantJavaUtil.getFieldDetails(new ExtendedFieldDetail());

		for (int i = 0; i < extendedFieldDetailList.size(); i++) {
			ExtendedFieldDetail extendedFieldDetail = extendedFieldDetailList.get(i);

			if (StringUtils.isEmpty(extendedFieldDetail.getRecordType())) {
				continue;
			}

			if (StringUtils.trimToNull(extendedFieldDetail.getFieldName()) != null) {
				extendedFieldDetail.setFieldName(extendedFieldDetail.getFieldName());
			}

			boolean isRcdType = false;

			if (extendedFieldDetail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
				extendedFieldDetail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				isRcdType = true;
			} else if (extendedFieldDetail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
				extendedFieldDetail.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				isRcdType = true;
			} else if (extendedFieldDetail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
				extendedFieldDetail.setRecordType(PennantConstants.RECORD_TYPE_DEL);
			}

			if ("saveOrUpdate".equals(method) && isRcdType) {
				extendedFieldDetail.setNewRecord(true);
			}

			if (!auditTranType.equals(PennantConstants.TRAN_WF)) {
				if (extendedFieldDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
					auditTranType = PennantConstants.TRAN_ADD;
				} else if (extendedFieldDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)
						|| extendedFieldDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
					auditTranType = PennantConstants.TRAN_DEL;
				} else {
					auditTranType = PennantConstants.TRAN_UPD;
				}
			}

			extendedFieldDetail.setRecordStatus(extendedFldHeader.getRecordStatus());
			extendedFieldDetail.setLastMntOn(extendedFldHeader.getLastMntOn());

			auditDetails.add(new AuditDetail(auditTranType, count++, fields[0], fields[1],
					extendedFieldDetail.getBefImage(), extendedFieldDetail));
		}
		logger.debug("Leaving");
		return auditDetails;
	}

	/**
	 * Method For Preparing List of AuditDetails for Customer Ratings
	 * 
	 * @param auditDetails
	 * @param type
	 * @param custId
	 * @return
	 */
	public List<AuditDetail> processingExtendeFieldList(List<AuditDetail> auditDetails, String type) {
		logger.debug("Entering");

		boolean saveRecord = false;
		boolean updateRecord = false;
		boolean deleteRecord = false;
		boolean approveRec = false;
		boolean isSeqSecRightsUpdated = false;

		for (int i = 0; i < auditDetails.size(); i++) {

			ExtendedFieldDetail extendedFieldDetail = (ExtendedFieldDetail) auditDetails.get(i).getModelData();
			saveRecord = false;
			updateRecord = false;
			deleteRecord = false;
			approveRec = false;
			String rcdType = "";
			String recordStatus = "";
			if (StringUtils.isEmpty(type)) {
				approveRec = true;
				extendedFieldDetail.setRoleCode("");
				extendedFieldDetail.setNextRoleCode("");
				extendedFieldDetail.setTaskId("");
				extendedFieldDetail.setNextTaskId("");
			}

			extendedFieldDetail.setWorkflowId(0);

			if (extendedFieldDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
				deleteRecord = true;
			} else if (extendedFieldDetail.isNewRecord()) {
				saveRecord = true;
				if (extendedFieldDetail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
					extendedFieldDetail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else if (extendedFieldDetail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
					extendedFieldDetail.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				} else if (extendedFieldDetail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
					extendedFieldDetail.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				}
			} else if (extendedFieldDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
				if (approveRec) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			} else if (extendedFieldDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_UPD)) {
				updateRecord = true;
			} else if (extendedFieldDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)) {
				if (approveRec) {
					deleteRecord = true;
				} else if (extendedFieldDetail.isNew()) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			}
			if (approveRec) {
				rcdType = extendedFieldDetail.getRecordType();
				recordStatus = extendedFieldDetail.getRecordStatus();
				extendedFieldDetail.setRecordType("");
				extendedFieldDetail.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
			}
			if (saveRecord) {
				extendedFieldDetailDAO.save(extendedFieldDetail, type);
			}

			if (updateRecord) {
				extendedFieldDetailDAO.update(extendedFieldDetail, type);
			}

			if (deleteRecord) {
				extendedFieldDetailDAO.delete(extendedFieldDetail, type);
			}

			if (approveRec) {
				extendedFieldDetail.setRecordType(rcdType);
				extendedFieldDetail.setRecordStatus(recordStatus);

				//if it is an input element added column in ED table.
				if (extendedFieldDetail.isInputElement()) {
					if (!deleteRecord) {
						if (!StringUtils.equals(PennantConstants.RECORD_TYPE_UPD , extendedFieldDetail.getRecordType())) {
							extendedFieldDetailDAO.alter(extendedFieldDetail, "_Temp", false, true, false);
							extendedFieldDetailDAO.alter(extendedFieldDetail, "", false, true, false);
							if (StringUtils.equals(extendedFieldDetail.getLovDescModuleName(),
									CollateralConstants.MODULE_NAME)) {
								extendedFieldDetailDAO.alter(extendedFieldDetail, "_TV", false, true, false);
							}
							extendedFieldDetailDAO.alter(extendedFieldDetail, "", false, true, true);
						}
					} else {
						extendedFieldDetailDAO.alter(extendedFieldDetail, "_Temp", true, false, false);
						extendedFieldDetailDAO.alter(extendedFieldDetail, "", true, false, false);
						if (StringUtils.equals(extendedFieldDetail.getLovDescModuleName(),
								CollateralConstants.MODULE_NAME)) {
							extendedFieldDetailDAO.alter(extendedFieldDetail, "_TV", true, false, false);
						}
						extendedFieldDetailDAO.alter(extendedFieldDetail, "", true, false, true);
					}
				}
				//saving secRight for Loan and CustomerModule while approving.
				if (securityRightDAO != null
						&& StringUtils.equals(extendedFieldDetail.getRecordType(), PennantConstants.RECORD_TYPE_NEW)) {
					String rightName = PennantApplicationUtil.getExtendedFieldRightName(extendedFieldDetail);
					if (!securityRightDAO.isRightNameExists(rightName)) {
						SecurityRight securityRight = prepareSecRight(extendedFieldDetail, rightName);
						if (!isSeqSecRightsUpdated) {
							securityRightDAO.updateSeqSecRights();
							isSeqSecRightsUpdated = true;
						}
						securityRightDAO.save(securityRight);
					}
				}
				auditDetails.get(i).setModelData(extendedFieldDetail);
			}
		}
		logger.debug("Leaving");
		return auditDetails;

	}

	public List<AuditDetail> processingTechValuationFieldsList(List<AuditDetail> auditDetails, String type) {
		logger.debug("Entering");

		boolean saveRecord = false;
		boolean updateRecord = false;
		boolean deleteRecord = false;
		boolean approveRec = false;

		for (int i = 0; i < auditDetails.size(); i++) {

			ExtendedFieldDetail techValuationFieldDetail = (ExtendedFieldDetail) auditDetails.get(i).getModelData();
			saveRecord = false;
			updateRecord = false;
			deleteRecord = false;
			approveRec = false;
			String rcdType = "";
			String recordStatus = "";
			if (StringUtils.isEmpty(type)) {
				approveRec = true;
				techValuationFieldDetail.setRoleCode("");
				techValuationFieldDetail.setNextRoleCode("");
				techValuationFieldDetail.setTaskId("");
				techValuationFieldDetail.setNextTaskId("");
			}

			techValuationFieldDetail.setWorkflowId(0);

			if (techValuationFieldDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
				deleteRecord = true;
			} else if (techValuationFieldDetail.isNewRecord()) {
				saveRecord = true;
				if (techValuationFieldDetail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
					techValuationFieldDetail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else if (techValuationFieldDetail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
					techValuationFieldDetail.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				} else if (techValuationFieldDetail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
					techValuationFieldDetail.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				}
			} else if (techValuationFieldDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
				if (approveRec) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			} else if (techValuationFieldDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_UPD)) {
				updateRecord = true;
			} else if (techValuationFieldDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)) {
				if (approveRec) {
					deleteRecord = true;
				} else if (techValuationFieldDetail.isNew()) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			}
			if (approveRec) {
				rcdType = techValuationFieldDetail.getRecordType();
				recordStatus = techValuationFieldDetail.getRecordStatus();
				techValuationFieldDetail.setRecordType("");
				techValuationFieldDetail.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
			}
			if (saveRecord) {
				extendedFieldDetailDAO.save(techValuationFieldDetail, type);
			}

			if (updateRecord) {
				extendedFieldDetailDAO.update(techValuationFieldDetail, type);
			}

			if (deleteRecord) {
				extendedFieldDetailDAO.delete(techValuationFieldDetail, type);
			}

			if (approveRec && techValuationFieldDetail.isInputElement()) {
				techValuationFieldDetail.setRecordType(rcdType);
				techValuationFieldDetail.setRecordStatus(recordStatus);
				if (!deleteRecord) {
					extendedFieldDetailDAO.alter(techValuationFieldDetail, "_Temp", false, true, false);
					extendedFieldDetailDAO.alter(techValuationFieldDetail, "", false, true, false);
					extendedFieldDetailDAO.alter(techValuationFieldDetail, "", false, true, true);

				} else {
					extendedFieldDetailDAO.alter(techValuationFieldDetail, "_Temp", false, true, false);
					extendedFieldDetailDAO.alter(techValuationFieldDetail, "", false, true, false);
					extendedFieldDetailDAO.alter(techValuationFieldDetail, "", false, true, true);
				}
			}
			auditDetails.get(i).setModelData(techValuationFieldDetail);
		}
		logger.debug("Leaving");
		return auditDetails;
	}

	/**
	 * Method to prepare SecurityRight based on the given ExtendedField<BR>
	 * PageName=ModuleName+"_"+SubModuleName,<BR>
	 * RightName= PageName+"_"+FieldName if it is an InputElement,<BR>
	 * otherwise RightName=PageName+"_"FieldType+"_"+FieldName.
	 * 
	 * @param detail
	 * @param rightName
	 * @return securityRight
	 */
	private SecurityRight prepareSecRight(ExtendedFieldDetail detail, String rightName) {
		logger.debug(Literal.ENTERING);
		SecurityRight securityRight = new SecurityRight();
		int rightType;
		String pageName = detail.getLovDescModuleName() + "_" + detail.getLovDescSubModuleName();
		if (StringUtils.equals(detail.getFieldType(), ExtendedFieldConstants.FIELDTYPE_BUTTON)) {
			rightType = 2;
		} else {
			rightType = 3;
		}
		securityRight.setRightType(rightType);
		securityRight.setRightName(rightName);
		securityRight.setPage(pageName);
		securityRight.setVersion(1);
		securityRight.setLastMntBy(1000);
		securityRight.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		securityRight.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
		securityRight.setRoleCode("");
		securityRight.setNextRoleCode("");
		securityRight.setTaskId("");
		securityRight.setNextTaskId("");
		securityRight.setRecordType("");
		securityRight.setWorkflowId(0);
		logger.debug(Literal.LEAVING);
		return securityRight;
	}

}
