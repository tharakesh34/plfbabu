package com.pennant.backend.service.cersai.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.cersai.DistrictMappingDAO;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.cersai.DistrictMapping;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.cersai.DistrictMappingService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;

/**
 * Service implementation for methods that depends on <b>DistrictMapping</b>.<br>
 */
public class DistrictMappingServiceImpl extends GenericService<DistrictMapping> implements DistrictMappingService {
	private static final Logger logger = LogManager.getLogger(DistrictMappingServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;
	private DistrictMappingDAO districtMappingDAO;

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
	 * @return the districtMappingDAO
	 */
	public DistrictMappingDAO getDistrictMappingDAO() {
		return districtMappingDAO;
	}

	/**
	 * @param districtMappingDAO the districtMappingDAO to set
	 */
	public void setDistrictMappingDAO(DistrictMappingDAO districtMappingDAO) {
		this.districtMappingDAO = districtMappingDAO;
	}

	/**
	 * saveOrUpdate method method do the following steps. 1) Do the Business validation by using
	 * businessValidation(auditHeader) method if there is any error or warning message then return the auditHeader. 2)
	 * Do Add or Update the Record a) Add new Record for the new record in the DB table
	 * DistrictsMapping/DistrictsMapping_Temp by using DistrictsMappingDAO's save method b) Update the Record in the
	 * table. based on the module workFlow Configuration. by using DistrictsMappingDAO's update method 3) Audit the
	 * record in to AuditHeader and AdtDistrictsMapping by using auditHeaderDAO.addAudit(auditHeader)
	 * 
	 * @param AuditHeader (auditHeader)
	 * @return auditHeader
	 */
	public AuditHeader saveOrUpdate(AuditHeader auditHeader) {
		logger.info(Literal.ENTERING);

		auditHeader = businessValidation(auditHeader, "saveOrUpdate");

		if (!auditHeader.isNextProcess()) {
			logger.info(Literal.LEAVING);
			return auditHeader;
		}

		DistrictMapping districtMapping = (DistrictMapping) auditHeader.getAuditDetail().getModelData();

		TableType tableType = TableType.MAIN_TAB;
		if (districtMapping.isWorkflow()) {
			tableType = TableType.TEMP_TAB;
		}

		if (districtMapping.isNew()) {
			getDistrictMappingDAO().save(districtMapping, tableType);
		} else {
			getDistrictMappingDAO().update(districtMapping, tableType);
		}

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.info(Literal.LEAVING);
		return auditHeader;

	}

	/**
	 * delete method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) delete Record for the DB table
	 * DistrictsMapping by using DistrictsMappingDAO's delete method with type as Blank 3) Audit the record in to
	 * AuditHeader and AdtDistrictsMapping by using auditHeaderDAO.addAudit(auditHeader)
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

		DistrictMapping districtMapping = (DistrictMapping) auditHeader.getAuditDetail().getModelData();
		getDistrictMappingDAO().delete(districtMapping, TableType.MAIN_TAB);

		getAuditHeaderDAO().addAudit(auditHeader);

		logger.info(Literal.LEAVING);
		return auditHeader;
	}

	/**
	 * getDistrictsMapping fetch the details by using DistrictsMappingDAO's getDistrictsMappingById method.
	 * 
	 * @param mappingType  mappingType of the DistrictMapping.
	 * @param district     district of the DistrictMapping.
	 * @param mappingValue mappingValue of the DistrictMapping.
	 * @return DistrictsMapping
	 */
	@Override
	public DistrictMapping getDistrictMapping(int mappingType, String district, String mappingValue) {
		return getDistrictMappingDAO().getDistrictMapping(mappingType, district, mappingValue, "_View");
	}

	/**
	 * getApprovedDistrictsMappingById fetch the details by using DistrictsMappingDAO's getDistrictsMappingById method .
	 * with parameter id and type as blank. it fetches the approved records from the DistrictsMapping.
	 * 
	 * @param mappingType  mappingType of the DistrictMapping.
	 * @param district     district of the DistrictMapping.
	 * @param mappingValue mappingValue of the DistrictMapping. (String)
	 * @return DistrictsMapping
	 */
	public DistrictMapping getApprovedDistrictMapping(int mappingType, String district, String mappingValue) {
		return getDistrictMappingDAO().getDistrictMapping(mappingType, district, mappingValue, "_AView");
	}

	/**
	 * doApprove method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) based on the Record type do
	 * following actions a) DELETE Delete the record from the main table by using getDistrictMappingDAO().delete with
	 * parameters districtMapping,"" b) NEW Add new record in to main table by using getDistrictMappingDAO().save with
	 * parameters districtMapping,"" c) EDIT Update record in the main table by using getDistrictMappingDAO().update
	 * with parameters districtMapping,"" 3) Delete the record from the workFlow table by using
	 * getDistrictMappingDAO().delete with parameters districtMapping,"_Temp" 4) Audit the record in to AuditHeader and
	 * AdtDistrictsMapping by using auditHeaderDAO.addAudit(auditHeader) for Work flow 5) Audit the record in to
	 * AuditHeader and AdtDistrictsMapping by using auditHeaderDAO.addAudit(auditHeader) based on the transaction Type.
	 * 
	 * @param AuditHeader (auditHeader)
	 * @return auditHeader
	 */
	@Override
	public AuditHeader doApprove(AuditHeader auditHeader) {
		logger.info(Literal.ENTERING);

		String tranType = "";
		auditHeader = businessValidation(auditHeader, "doApprove");

		if (!auditHeader.isNextProcess()) {
			logger.info(Literal.LEAVING);
			return auditHeader;
		}

		DistrictMapping districtMapping = new DistrictMapping();
		BeanUtils.copyProperties((DistrictMapping) auditHeader.getAuditDetail().getModelData(), districtMapping);

		getDistrictMappingDAO().delete(districtMapping, TableType.TEMP_TAB);

		if (!PennantConstants.RECORD_TYPE_NEW.equals(districtMapping.getRecordType())) {
			auditHeader.getAuditDetail().setBefImage(
					districtMappingDAO.getDistrictMapping(Integer.valueOf(districtMapping.getMappingType()),
							districtMapping.getDistrict(), districtMapping.getMappingValue(), ""));
		}

		if (districtMapping.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;
			getDistrictMappingDAO().delete(districtMapping, TableType.MAIN_TAB);
		} else {
			districtMapping.setRoleCode("");
			districtMapping.setNextRoleCode("");
			districtMapping.setTaskId("");
			districtMapping.setNextTaskId("");
			districtMapping.setWorkflowId(0);

			if (districtMapping.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				districtMapping.setRecordType("");
				getDistrictMappingDAO().save(districtMapping, TableType.MAIN_TAB);
			} else {
				tranType = PennantConstants.TRAN_UPD;
				districtMapping.setRecordType("");
				getDistrictMappingDAO().update(districtMapping, TableType.MAIN_TAB);
			}
		}

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(districtMapping);
		getAuditHeaderDAO().addAudit(auditHeader);

		logger.info(Literal.LEAVING);
		return auditHeader;

	}

	/**
	 * doReject method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) Delete the record from the
	 * workFlow table by using getDistrictMappingDAO().delete with parameters districtMapping,"_Temp" 3) Audit the
	 * record in to AuditHeader and AdtDistrictsMapping by using auditHeaderDAO.addAudit(auditHeader) for Work flow
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

		DistrictMapping districtMapping = (DistrictMapping) auditHeader.getAuditDetail().getModelData();

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getDistrictMappingDAO().delete(districtMapping, TableType.TEMP_TAB);

		getAuditHeaderDAO().addAudit(auditHeader);

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

		logger.debug(Literal.LEAVING);
		return auditHeader;
	}

	/**
	 * For Validating AuditDetals object getting from Audit Header, if any mismatch conditions Fetch the error details
	 * from getDistrictMappingDAO().getErrorDetail with Error ID and language as parameters. if any error/Warnings then
	 * assign the to auditDeail Object
	 * 
	 * @param auditDetail
	 * @param usrLanguage
	 * @return
	 */

	private AuditDetail validation(AuditDetail auditDetail, String usrLanguage) {
		logger.debug(Literal.ENTERING);
		DistrictMapping districtMapping = (DistrictMapping) auditDetail.getModelData();
		String[] parameters = new String[2];
		parameters[0] = PennantJavaUtil.getLabel("label_DistrictMappingList_MappingType.value") + ": "
				+ districtMapping.getMappingType();
		parameters[1] = PennantJavaUtil.getLabel("label_DistrictMappingList_District.value") + ": "
				+ districtMapping.getDistrict();

		// Check the unique keys.
		if (districtMapping.isNew() && PennantConstants.RECORD_TYPE_NEW.equals(districtMapping.getRecordType())
				&& districtMappingDAO.isDuplicateKey(Integer.valueOf(districtMapping.getMappingType()),
						districtMapping.getDistrict(),
						districtMapping.isWorkflow() ? TableType.BOTH_TAB : TableType.MAIN_TAB)) {

			auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", parameters, null));
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));
		logger.debug(Literal.LEAVING);
		return auditDetail;
	}

}