package com.pennant.backend.service.cersai.impl;

import org.apache.logging.log4j.LogManager;import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.cersai.ProvinceMappingDAO;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.cersai.ProvinceMapping;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.cersai.ProvinceMappingService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;

/**
 * Service implementation for methods that depends on <b>ProvinceMapping</b>.<br>
 */
public class ProvinceMappingServiceImpl extends GenericService<ProvinceMapping> implements ProvinceMappingService {
	private static final Logger logger = LogManager.getLogger(ProvinceMappingServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;
	private ProvinceMappingDAO provinceMappingDAO;

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
	 * @return the provinceMappingDAO
	 */
	public ProvinceMappingDAO getProvinceMappingDAO() {
		return provinceMappingDAO;
	}

	/**
	 * @param provinceMappingDAO the provinceMappingDAO to set
	 */
	public void setProvinceMappingDAO(ProvinceMappingDAO provinceMappingDAO) {
		this.provinceMappingDAO = provinceMappingDAO;
	}

	/**
	 * saveOrUpdate method method do the following steps. 1) Do the Business validation by using
	 * businessValidation(auditHeader) method if there is any error or warning message then return the auditHeader. 2)
	 * Do Add or Update the Record a) Add new Record for the new record in the DB table
	 * ProvinceMapping/ProvinceMapping_Temp by using ProvinceMappingDAO's save method b) Update the Record in the table.
	 * based on the module workFlow Configuration. by using ProvinceMappingDAO's update method 3) Audit the record in to
	 * AuditHeader and AdtProvinceMapping by using auditHeaderDAO.addAudit(auditHeader)
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

		ProvinceMapping provinceMapping = (ProvinceMapping) auditHeader.getAuditDetail().getModelData();

		TableType tableType = TableType.MAIN_TAB;
		if (provinceMapping.isWorkflow()) {
			tableType = TableType.TEMP_TAB;
		}

		if (provinceMapping.isNew()) {
			getProvinceMappingDAO().save(provinceMapping, tableType);
		} else {
			getProvinceMappingDAO().update(provinceMapping, tableType);
		}

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.info(Literal.LEAVING);
		return auditHeader;

	}

	/**
	 * delete method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) delete Record for the DB table
	 * ProvinceMapping by using ProvinceMappingDAO's delete method with type as Blank 3) Audit the record in to
	 * AuditHeader and AdtProvinceMapping by using auditHeaderDAO.addAudit(auditHeader)
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

		ProvinceMapping provinceMapping = (ProvinceMapping) auditHeader.getAuditDetail().getModelData();
		getProvinceMappingDAO().delete(provinceMapping, TableType.MAIN_TAB);

		getAuditHeaderDAO().addAudit(auditHeader);

		logger.info(Literal.LEAVING);
		return auditHeader;
	}

	/**
	 * getProvinceMapping fetch the details by using ProvinceMappingDAO's getProvinceMappingById method.
	 * 
	 * @param mappingType  mappingType of the ProvinceMapping.
	 * @param province     province of the ProvinceMapping.
	 * @param mappingValue mappingValue of the ProvinceMapping.
	 * @return ProvinceMapping
	 */
	@Override
	public ProvinceMapping getProvinceMapping(int mappingType, String province, String mappingValue) {
		return getProvinceMappingDAO().getProvinceMapping(mappingType, province, mappingValue, "_View");
	}

	/**
	 * getApprovedProvinceMappingById fetch the details by using ProvinceMappingDAO's getProvinceMappingById method .
	 * with parameter id and type as blank. it fetches the approved records from the ProvinceMapping.
	 * 
	 * @param mappingType  mappingType of the ProvinceMapping.
	 * @param province     province of the ProvinceMapping.
	 * @param mappingValue mappingValue of the ProvinceMapping. (String)
	 * @return ProvinceMapping
	 */
	public ProvinceMapping getApprovedProvinceMapping(int mappingType, String province, String mappingValue) {
		return getProvinceMappingDAO().getProvinceMapping(mappingType, province, mappingValue, "_AView");
	}

	/**
	 * doApprove method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) based on the Record type do
	 * following actions a) DELETE Delete the record from the main table by using getProvinceMappingDAO().delete with
	 * parameters provinceMapping,"" b) NEW Add new record in to main table by using getProvinceMappingDAO().save with
	 * parameters provinceMapping,"" c) EDIT Update record in the main table by using getProvinceMappingDAO().update
	 * with parameters provinceMapping,"" 3) Delete the record from the workFlow table by using
	 * getProvinceMappingDAO().delete with parameters provinceMapping,"_Temp" 4) Audit the record in to AuditHeader and
	 * AdtProvinceMapping by using auditHeaderDAO.addAudit(auditHeader) for Work flow 5) Audit the record in to
	 * AuditHeader and AdtProvinceMapping by using auditHeaderDAO.addAudit(auditHeader) based on the transaction Type.
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

		ProvinceMapping provinceMapping = new ProvinceMapping();
		BeanUtils.copyProperties((ProvinceMapping) auditHeader.getAuditDetail().getModelData(), provinceMapping);

		getProvinceMappingDAO().delete(provinceMapping, TableType.TEMP_TAB);

		if (!PennantConstants.RECORD_TYPE_NEW.equals(provinceMapping.getRecordType())) {
			auditHeader.getAuditDetail()
					.setBefImage(provinceMappingDAO.getProvinceMapping(provinceMapping.getMappingType(),
							provinceMapping.getProvince(), provinceMapping.getMappingValue(), ""));
		}

		if (provinceMapping.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;
			getProvinceMappingDAO().delete(provinceMapping, TableType.MAIN_TAB);
		} else {
			provinceMapping.setRoleCode("");
			provinceMapping.setNextRoleCode("");
			provinceMapping.setTaskId("");
			provinceMapping.setNextTaskId("");
			provinceMapping.setWorkflowId(0);

			if (provinceMapping.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				provinceMapping.setRecordType("");
				getProvinceMappingDAO().save(provinceMapping, TableType.MAIN_TAB);
			} else {
				tranType = PennantConstants.TRAN_UPD;
				provinceMapping.setRecordType("");
				getProvinceMappingDAO().update(provinceMapping, TableType.MAIN_TAB);
			}
		}

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(provinceMapping);
		getAuditHeaderDAO().addAudit(auditHeader);

		logger.info(Literal.LEAVING);
		return auditHeader;

	}

	/**
	 * doReject method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) Delete the record from the
	 * workFlow table by using getProvinceMappingDAO().delete with parameters provinceMapping,"_Temp" 3) Audit the
	 * record in to AuditHeader and AdtProvinceMapping by using auditHeaderDAO.addAudit(auditHeader) for Work flow
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

		ProvinceMapping provinceMapping = (ProvinceMapping) auditHeader.getAuditDetail().getModelData();

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getProvinceMappingDAO().delete(provinceMapping, TableType.TEMP_TAB);

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
	 * from getProvinceMappingDAO().getErrorDetail with Error ID and language as parameters. if any error/Warnings then
	 * assign the to auditDeail Object
	 * 
	 * @param auditDetail
	 * @param usrLanguage
	 * @return
	 */

	private AuditDetail validation(AuditDetail auditDetail, String usrLanguage) {
		logger.debug(Literal.ENTERING);

		// Write the required validation over hear.
		ProvinceMapping provinceMapping = (ProvinceMapping) auditDetail.getModelData();
		String[] parameters = new String[2];
		parameters[0] = PennantJavaUtil.getLabel("label_ProvinceMappingList_MappingType.value") + ": "
				+ provinceMapping.getMappingType();
		parameters[1] = PennantJavaUtil.getLabel("label_ProvinceMappingList_Province.value") + ": "
				+ provinceMapping.getProvince();

		// Check the unique keys.
		if (provinceMapping.isNew() && PennantConstants.RECORD_TYPE_NEW.equals(provinceMapping.getRecordType())
				&& provinceMappingDAO.isDuplicateKey(provinceMapping.getMappingType(), provinceMapping.getProvince(),
						provinceMapping.isWorkflow() ? TableType.BOTH_TAB : TableType.MAIN_TAB)) {

			auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "41001", parameters, null));
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		logger.debug(Literal.LEAVING);
		return auditDetail;
	}

}