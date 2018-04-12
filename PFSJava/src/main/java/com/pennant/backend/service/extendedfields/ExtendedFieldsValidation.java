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
import com.pennant.backend.util.ExtendedFieldConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;

public class ExtendedFieldsValidation {

	private static final Logger		logger	= Logger.getLogger(ExtendedFieldsValidation.class);

	private ExtendedFieldDetailDAO	extendedFieldDetailDAO;
	private ExtendedFieldHeaderDAO	extendedFieldHeaderDAO;
	private SecurityRightDAO		securityRightDAO;

	public ExtendedFieldsValidation(ExtendedFieldDetailDAO extendedFieldDetailDAO, ExtendedFieldHeaderDAO extendedFieldHeaderDAO) {
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

	public List<AuditDetail> extendedFieldsListValidation(List<AuditDetail> auditDetails, String method, String usrLanguage) {
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
	 * @param auditDetail
	 * @param usrLanguage
	 * @param method
	 * @return
	 */
	public AuditDetail extendedFieldsHeaderValidation(AuditDetail auditDetail,String usrLanguage,String method){
		logger.debug("Entering");
		
		auditDetail.setErrorDetails(new ArrayList<ErrorDetail>());			
		ExtendedFieldHeader extendedFieldHeader= (ExtendedFieldHeader) auditDetail.getModelData();

		ExtendedFieldHeader tempExtendedFieldHeader= null;
		if (extendedFieldHeader.isWorkflow()){
			tempExtendedFieldHeader = getExtendedFieldHeaderDAO().getExtendedFieldHeaderByModuleName(extendedFieldHeader.getModuleName(),extendedFieldHeader.getSubModuleName(), "_Temp");
		}
		ExtendedFieldHeader befExtendedFieldHeader= getExtendedFieldHeaderDAO().getExtendedFieldHeaderByModuleName(extendedFieldHeader.getModuleName(),extendedFieldHeader.getSubModuleName(), "");

		ExtendedFieldHeader oldExtendedFieldHeader= extendedFieldHeader.getBefImage();

		String[] errParm = new String[2];
		String[] valueParm = new String[2];
		
		valueParm[0] = extendedFieldHeader.getModuleName();
		valueParm[1] = extendedFieldHeader.getSubModuleName();
		
		errParm[0] = PennantJavaUtil.getLabel("label_ModuleName") + ":" + valueParm[0];
		errParm[1] = PennantJavaUtil.getLabel("label_SubModuleName") + ":" + valueParm[1];

		if (extendedFieldHeader.isNew()){ // for New record or new record into work flow

			if (!extendedFieldHeader.isWorkflow()){// With out Work flow only new records  
				if (befExtendedFieldHeader !=null){	// Record Already Exists in the table then error  
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41015", errParm,valueParm), usrLanguage));
				}	
			}else{ // with work flow
				if (extendedFieldHeader.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){ // if records type is new
					if (befExtendedFieldHeader !=null || tempExtendedFieldHeader!=null ){ // if records already exists in the main table
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD,  "41015", errParm,valueParm), usrLanguage));
					}
				}else{ // if records not exists in the Main flow table
					if (befExtendedFieldHeader ==null || tempExtendedFieldHeader!=null ){
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD,  "41005", errParm,valueParm), usrLanguage));
					}
				}
			}
		}else{
			// for work flow process records or (Record to update or Delete with out work flow)
			if (!extendedFieldHeader.isWorkflow()){	// With out Work flow for update and delete

				if (befExtendedFieldHeader ==null){ // if records not exists in the main table
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, 
							"41002", errParm,valueParm), usrLanguage));
				}else{
					if (oldExtendedFieldHeader!=null && !oldExtendedFieldHeader.getLastMntOn().equals(befExtendedFieldHeader.getLastMntOn())){
						if (StringUtils.trimToEmpty(auditDetail.getAuditTranType()).equalsIgnoreCase(PennantConstants.TRAN_DEL)){
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, 
									"41003", errParm,valueParm), usrLanguage));
						}else{
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, 
									"41004", errParm,valueParm), usrLanguage));
						}
					}
				}
			}else{

				if (tempExtendedFieldHeader == null) { // if records not exists in the Work flow table 
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, valueParm), usrLanguage));
				}

				if (oldExtendedFieldHeader != null && tempExtendedFieldHeader != null && !oldExtendedFieldHeader.getLastMntOn().equals(tempExtendedFieldHeader.getLastMntOn())) {
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41005", errParm, valueParm), usrLanguage));
				}
			}
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		if("doApprove".equals(StringUtils.trimToEmpty(method)) || !extendedFieldHeader.isWorkflow()){
			auditDetail.setBefImage(befExtendedFieldHeader);	
		}
		logger.debug("Leaving");
		return auditDetail;
	}
	
	/**
	 * Method for validating Extended Field Details
	 * @param auditDetail
	 * @param usrLanguage
	 * @param method
	 * @return
	 */
	private AuditDetail validate(AuditDetail auditDetail, String method, String usrLanguage) {

		auditDetail.setErrorDetails(new ArrayList<ErrorDetail>());
		ExtendedFieldDetail extendedFieldDetail = (ExtendedFieldDetail) auditDetail.getModelData();

		ExtendedFieldDetail tempExtendedFieldDetail = null;
		if (extendedFieldDetail.isWorkflow()) {
			tempExtendedFieldDetail = getExtendedFieldDetailDAO().getExtendedFieldDetailById(extendedFieldDetail.getId(), extendedFieldDetail.getFieldName(), "_Temp");

		}
		ExtendedFieldDetail befExtendedFieldDetail = getExtendedFieldDetailDAO().getExtendedFieldDetailById(extendedFieldDetail.getId(), extendedFieldDetail.getFieldName(), "");

		ExtendedFieldDetail oldExtendedFieldDetail = extendedFieldDetail.getBefImage();

		String[] errParm = new String[1];
		String[] valueParm = new String[1];
		valueParm[0] = String.valueOf(extendedFieldDetail.getId());
		errParm[0] = PennantJavaUtil.getLabel("label_ModuleId") + ":" + valueParm[0];

		if (extendedFieldDetail.isNew()) { // for New record or new record into work flow

			if (!extendedFieldDetail.isWorkflow()) {// With out Work flow only new records  
				if (befExtendedFieldDetail != null) { // Record Already Exists in the table then error  
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD,
							"41001", errParm, valueParm), usrLanguage));
				}
			} else { // with work flow
				if (extendedFieldDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) { // if records type is new
					if (befExtendedFieldDetail != null || tempExtendedFieldDetail != null) { // if records already exists in the main table
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(
								PennantConstants.KEY_FIELD, "41001", errParm, valueParm), usrLanguage));
					}
				} else { // if records not exists in the Main flow table
					if (befExtendedFieldDetail == null || tempExtendedFieldDetail != null) {
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(
								PennantConstants.KEY_FIELD, "41005", errParm, valueParm), usrLanguage));
					}
				}
			}
		} else {
			// for work flow process records or (Record to update or Delete with out work flow)
			if (!extendedFieldDetail.isWorkflow()) { // With out Work flow for update and delete

				if (befExtendedFieldDetail == null) { // if records not exists in the main table
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD,
							"41002", errParm, valueParm), usrLanguage));
				} else {
					if (oldExtendedFieldDetail != null
							&& !oldExtendedFieldDetail.getLastMntOn().equals(befExtendedFieldDetail.getLastMntOn())) {
						if (StringUtils.trimToEmpty(auditDetail.getAuditTranType()).equalsIgnoreCase(
								PennantConstants.TRAN_DEL)) {
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(
									PennantConstants.KEY_FIELD, "41003", errParm, valueParm), usrLanguage));
						} else {
							auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(
									PennantConstants.KEY_FIELD, "41004", errParm, valueParm), usrLanguage));
						}
					}
				}
			} else {

				if (tempExtendedFieldDetail == null) { // if records not exists in the Work flow table 
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD,
							"41005", errParm, valueParm), usrLanguage));
				}

				if (oldExtendedFieldDetail != null
						&& !oldExtendedFieldDetail.getLastMntOn().equals(tempExtendedFieldDetail.getLastMntOn())) {
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD,
							"41005", errParm, valueParm), usrLanguage));
				}
			}
		}
		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		if ("doApprove".equals(StringUtils.trimToEmpty(method)) || !extendedFieldDetail.isWorkflow()) {
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
	public List<AuditDetail> setExtendedFieldsAuditData(ExtendedFieldHeader extendedFldHeader, String auditTranType, String method) {
		logger.debug("Entering");
		
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		String[] fields = PennantJavaUtil.getFieldDetails(new ExtendedFieldDetail());
		
		for (int i = 0; i < extendedFldHeader.getExtendedFieldDetails().size(); i++) {
			ExtendedFieldDetail extendedFieldDetail = extendedFldHeader.getExtendedFieldDetails().get(i);
			
			if (StringUtils.isEmpty(extendedFieldDetail.getRecordType())) {
				continue;
			}
			
			if (StringUtils.trimToNull(extendedFieldDetail.getFieldName()) != null) {
				extendedFieldDetail.setFieldName(extendedFieldDetail.getFieldName());
			}

			boolean isRcdType= false;

			if (extendedFieldDetail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
				extendedFieldDetail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				isRcdType=true;
			}else if (extendedFieldDetail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
				extendedFieldDetail.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				isRcdType=true;
			}else if (extendedFieldDetail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
				extendedFieldDetail.setRecordType(PennantConstants.RECORD_TYPE_DEL);
			}

			if("saveOrUpdate".equals(method) && isRcdType ){
				extendedFieldDetail.setNewRecord(true);
			}

			if(!auditTranType.equals(PennantConstants.TRAN_WF)){
				if (extendedFieldDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
					auditTranType= PennantConstants.TRAN_ADD;
				} else if (extendedFieldDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)
						|| extendedFieldDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
					auditTranType= PennantConstants.TRAN_DEL;
				}else{
					auditTranType= PennantConstants.TRAN_UPD;
				}
			}

			extendedFieldDetail.setRecordStatus(extendedFldHeader.getRecordStatus());
			extendedFieldDetail.setLastMntOn(extendedFldHeader.getLastMntOn());

			auditDetails.add(new AuditDetail(auditTranType, i+1, fields[0], fields[1], extendedFieldDetail.getBefImage(), extendedFieldDetail));
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
						extendedFieldDetailDAO.alter(extendedFieldDetail, "_Temp", false, true, false);
						extendedFieldDetailDAO.alter(extendedFieldDetail, "", false, true, false);
						extendedFieldDetailDAO.alter(extendedFieldDetail, "", false, true, true);
					} else {
						extendedFieldDetailDAO.alter(extendedFieldDetail, "_Temp", true, false, false);
						extendedFieldDetailDAO.alter(extendedFieldDetail, "", true, false, false);
						extendedFieldDetailDAO.alter(extendedFieldDetail, "", true, false, true);
					}
				}
				//saving secRight for Loan and CustomerModule while approving.
				if (securityRightDAO != null
						&& StringUtils.equals(extendedFieldDetail.getRecordType(), PennantConstants.RECORD_TYPE_NEW)) {
					if (!securityRightDAO.isRightNameExists(getExtendedFieldRightName(extendedFieldDetail))) {
						SecurityRight securityRight = prepareSecRight(extendedFieldDetail);
						securityRightDAO.save(securityRight);
					}
				}
			}
			auditDetails.get(i).setModelData(extendedFieldDetail);
		}
		logger.debug("Leaving");
		return auditDetails;
	}

	/**
	 * Method to prepare SecurityRight based on the given ExtendedField
	 * PageName=ModuleName+"_"+SubModuleName ,
	 * RightName= PageName+"_"+FieldName if it is an InputElement otherwise 
	 * RightName=PageName+"_"FieldType+"_"+FieldName.
	 * 
	 * @param detail
	 * @return securityRight
	 */
	private SecurityRight prepareSecRight(ExtendedFieldDetail detail) {
		logger.debug(Literal.ENTERING);
		SecurityRight securityRight = new SecurityRight();
		int rightType;
		String pageName = detail.getLovDescSubModuleName();
		if (StringUtils.isNotBlank(pageName) && pageName.contains("_ED")) {
			pageName = pageName.replace("_ED", "");
		}
		if (StringUtils.equals(detail.getFieldType(), ExtendedFieldConstants.FIELDTYPE_BUTTON)) {
			rightType = 2;
		} else {
			rightType = 3;
		}
		securityRight.setRightType(rightType);
		securityRight.setRightName(getExtendedFieldRightName(detail));
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
	
	//TODO:Ganesh need to move this method  Common Class.
	public String getExtendedFieldRightName(ExtendedFieldDetail detail) {
		logger.debug(Literal.ENTERING);
		String rightName = null;
		if (detail != null) {
			String pageName = detail.getLovDescSubModuleName();
			if (StringUtils.isNotBlank(pageName) && pageName.contains("_ED")) {
				pageName = pageName.replace("_ED", "");
			}
			if (detail.isInputElement()) {
				rightName = pageName + "_" + detail.getFieldName();
			} else {
				rightName = pageName + "_" + detail.getFieldType() + "_" + detail.getFieldName();
			}
		}
		logger.debug(Literal.LEAVING);
		return rightName;
	}

}
