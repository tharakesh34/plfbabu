/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. All
 * components/modules/functions/classes/logic in this software, unless otherwise stated, the property of Pennant
 * Technologies.
 * 
 * Copyright and other intellectual property laws protect these materials. Reproduction or retransmission of the
 * materials, in whole or in part, in any manner, without the prior written consent of the copyright holder, is a
 * violation of copyright law.
 */

/**
 ********************************************************************************************
 * FILE HEADER *
 ********************************************************************************************
 * * FileName : DirectorDetailServiceImpl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 01-12-2011 * *
 * Modified Date : 01-12-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 01-12-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.backend.service.customermasters.impl;

import java.math.BigDecimal;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.customermasters.CustomerDocumentDAO;
import com.pennant.backend.dao.customermasters.DirectorDetailDAO;
import com.pennant.backend.dao.systemmasters.CityDAO;
import com.pennant.backend.dao.systemmasters.DesignationDAO;
import com.pennant.backend.dao.systemmasters.GenderDAO;
import com.pennant.backend.dao.systemmasters.NationalityCodeDAO;
import com.pennant.backend.dao.systemmasters.SalutationDAO;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.DirectorDetail;
import com.pennant.backend.model.systemmasters.City;
import com.pennant.backend.model.systemmasters.Designation;
import com.pennant.backend.model.systemmasters.NationalityCode;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.customermasters.DirectorDetailService;
import com.pennant.backend.service.customermasters.validation.CustomerDirectorValidation;
import com.pennant.backend.util.PennantConstants;
import com.pennanttech.pennapps.core.model.ErrorDetail;

/**
 * Service implementation for methods that depends on <b>DirectorDetail</b>.<br>
 * 
 */
public class DirectorDetailServiceImpl extends GenericService<DirectorDetail> implements DirectorDetailService {
	private static final Logger logger = LogManager.getLogger(DirectorDetailServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;
	private DirectorDetailDAO directorDetailDAO;
	private CityDAO cityDAO;
	private GenderDAO genderDAO;
	private SalutationDAO salutationDAO;
	private CustomerDocumentDAO customerDocumentDAO;
	private NationalityCodeDAO nationalityCodeDAO;

	private CustomerDirectorValidation customerDirectorValidation;
	private DesignationDAO designationDAO;

	public DirectorDetailServiceImpl() {
		super();
	}

	public AuditHeaderDAO getAuditHeaderDAO() {
		return auditHeaderDAO;
	}

	public void setAuditHeaderDAO(AuditHeaderDAO auditHeaderDAO) {
		this.auditHeaderDAO = auditHeaderDAO;
	}

	public DirectorDetailDAO getDirectorDetailDAO() {
		return directorDetailDAO;
	}

	public void setDirectorDetailDAO(DirectorDetailDAO directorDetailDAO) {
		this.directorDetailDAO = directorDetailDAO;
	}

	public CityDAO getCityDAO() {
		return cityDAO;
	}

	public void setCityDAO(CityDAO cityDAO) {
		this.cityDAO = cityDAO;
	}

	public NationalityCodeDAO getNationalityCodeDAO() {
		return nationalityCodeDAO;
	}

	public void setNationalityCodeDAO(NationalityCodeDAO nationalityCodeDAO) {
		this.nationalityCodeDAO = nationalityCodeDAO;
	}

	public GenderDAO getGenderDAO() {
		return genderDAO;
	}

	public void setGenderDAO(GenderDAO genderDAO) {
		this.genderDAO = genderDAO;
	}

	public void setSalutationDAO(SalutationDAO salutationDAO) {
		this.salutationDAO = salutationDAO;
	}

	public void setCustomerDocumentDAO(CustomerDocumentDAO customerDocumentDAO) {
		this.customerDocumentDAO = customerDocumentDAO;
	}

	public void setDesignationDAO(DesignationDAO designationDAO) {
		this.designationDAO = designationDAO;
	}

	public CustomerDirectorValidation getDirectorValidation() {
		if (customerDirectorValidation == null) {
			this.customerDirectorValidation = new CustomerDirectorValidation(directorDetailDAO);
		}
		return this.customerDirectorValidation;
	}

	@Override
	public DirectorDetail getDirectorDetail() {
		return getDirectorDetailDAO().getDirectorDetail();
	}

	@Override
	public DirectorDetail getNewDirectorDetail() {
		return getDirectorDetailDAO().getNewDirectorDetail();
	}

	/**
	 * saveOrUpdate method method do the following steps. 1) Do the Business validation by using
	 * businessValidation(auditHeader) method if there is any error or warning message then return the auditHeader. 2)
	 * Do Add or Update the Record a) Add new Record for the new record in the DB table
	 * CustomerDirectorDetail/CustomerDirectorDetail_Temp by using DirectorDetailDAO's save method b) Update the Record
	 * in the table. based on the module workFlow Configuration. by using DirectorDetailDAO's update method 3) Audit the
	 * record in to AuditHeader and AdtCustomerDirectorDetail by using auditHeaderDAO.addAudit(auditHeader)
	 * 
	 * @param AuditHeader (auditHeader)
	 * @return auditHeader
	 */
	@Override
	public AuditHeader saveOrUpdate(AuditHeader auditHeader) {
		logger.debug("Entering");
		auditHeader = businessValidation(auditHeader, "saveOrUpdate");
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}
		String tableType = "";
		DirectorDetail directorDetail = (DirectorDetail) auditHeader.getAuditDetail().getModelData();

		if (directorDetail.isWorkflow()) {
			tableType = "_Temp";
		}

		if (directorDetail.isNewRecord()) {
			directorDetail.setId(getDirectorDetailDAO().save(directorDetail, tableType));
			auditHeader.getAuditDetail().setModelData(directorDetail);
			auditHeader.setAuditReference(String.valueOf(directorDetail.getDirectorId()));
		} else {
			getDirectorDetailDAO().update(directorDetail, tableType);
		}

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;

	}

	/**
	 * delete method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) delete Record for the DB table
	 * CustomerDirectorDetail by using DirectorDetailDAO's delete method with type as Blank 3) Audit the record in to
	 * AuditHeader and AdtCustomerDirectorDetail by using auditHeaderDAO.addAudit(auditHeader)
	 * 
	 * @param AuditHeader (auditHeader)
	 * @return auditHeader
	 */
	@Override
	public AuditHeader delete(AuditHeader auditHeader) {
		logger.debug("Entering");
		auditHeader = businessValidation(auditHeader, "delete");
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}

		DirectorDetail directorDetail = (DirectorDetail) auditHeader.getAuditDetail().getModelData();
		getDirectorDetailDAO().delete(directorDetail, "");

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * getDirectorDetailById fetch the details by using DirectorDetailDAO's getDirectorDetailById method.
	 * 
	 * @param id   (int)
	 * @param type (String) ""/_Temp/_View
	 * @return DirectorDetail
	 */
	@Override
	public DirectorDetail getDirectorDetailById(long id, long custID) {
		return directorDetailDAO.getDirectorDetailById(id, custID, "_View");
	}

	/**
	 * getApprovedDirectorDetailById fetch the details by using DirectorDetailDAO's getDirectorDetailById method . with
	 * parameter id and type as blank. it fetches the approved records from the CustomerDirectorDetail.
	 * 
	 * @param id (int)
	 * @return DirectorDetail
	 */
	public DirectorDetail getApprovedDirectorDetailById(long id, long custID) {
		return directorDetailDAO.getDirectorDetailById(id, custID, "_AView");
	}

	/**
	 * doApprove method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) based on the Record type do
	 * following actions a) DELETE Delete the record from the main table by using getDirectorDetailDAO().delete with
	 * parameters directorDetail,"" b) NEW Add new record in to main table by using getDirectorDetailDAO().save with
	 * parameters directorDetail,"" c) EDIT Update record in the main table by using getDirectorDetailDAO().update with
	 * parameters directorDetail,"" 3) Delete the record from the workFlow table by using getDirectorDetailDAO().delete
	 * with parameters directorDetail,"_Temp" 4) Audit the record in to AuditHeader and AdtCustomerDirectorDetail by
	 * using auditHeaderDAO.addAudit(auditHeader) for Work flow 5) Audit the record in to AuditHeader and
	 * AdtCustomerDirectorDetail by using auditHeaderDAO.addAudit(auditHeader) based on the transaction Type.
	 * 
	 */

	@Override
	public DirectorDetail getApprovedDirectorDetailByDirectorId(long directorId, long custId) {
		return directorDetailDAO.getDirectorDetailByDirectorId(directorId, custId, "_View");
	}

	/**
	 * 
	 */

	@Override
	public int getVersion(long custID, long directorId) {
		return directorDetailDAO.getVersion(custID, directorId);
	}

	/**
	 * doApprove method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) based on the Record type do
	 * following actions a) DELETE Delete the record from the main table by using getDirectorDetailDAO().delete with
	 * parameters directorDetail,"" b) NEW Add new record in to main table by using getDirectorDetailDAO().save with
	 * parameters directorDetail,"" c) EDIT Update record in the main table by using getDirectorDetailDAO().update with
	 * parameters directorDetail,"" 3) Delete the record from the workFlow table by using getDirectorDetailDAO().delete
	 * with parameters directorDetail,"_Temp" 4) Audit the record in to AuditHeader and AdtCustomerDirectorDetail by
	 * using auditHeaderDAO.addAudit(auditHeader) for Work flow 5) Audit the record in to AuditHeader and
	 * AdtCustomerDirectorDetail by using auditHeaderDAO.addAudit(auditHeader) based on the transaction Type.
	 * 
	 * @param AuditHeader (auditHeader)
	 * @return auditHeader
	 */
	public AuditHeader doApprove(AuditHeader auditHeader) {
		logger.debug("Entering");
		String tranType = "";
		auditHeader = businessValidation(auditHeader, "doApprove");
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}

		DirectorDetail directorDetail = new DirectorDetail();
		BeanUtils.copyProperties((DirectorDetail) auditHeader.getAuditDetail().getModelData(), directorDetail);

		if (directorDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;
			getDirectorDetailDAO().delete(directorDetail, "");
		} else {
			directorDetail.setRoleCode("");
			directorDetail.setNextRoleCode("");
			directorDetail.setTaskId("");
			directorDetail.setNextTaskId("");
			directorDetail.setWorkflowId(0);

			if (directorDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				directorDetail.setRecordType("");
				getDirectorDetailDAO().save(directorDetail, "");
			} else {
				tranType = PennantConstants.TRAN_UPD;
				directorDetail.setRecordType("");
				getDirectorDetailDAO().update(directorDetail, "");
			}
		}

		if (!StringUtils.equals(directorDetail.getSourceId(), PennantConstants.FINSOURCE_ID_API)) {
			getDirectorDetailDAO().delete(directorDetail, "_Temp");
			auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
			getAuditHeaderDAO().addAudit(auditHeader);
		}

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(directorDetail);

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");

		return auditHeader;
	}

	/**
	 * doReject method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) Delete the record from the
	 * workFlow table by using getDirectorDetailDAO().delete with parameters directorDetail,"_Temp" 3) Audit the record
	 * in to AuditHeader and AdtCustomerDirectorDetail by using auditHeaderDAO.addAudit(auditHeader) for Work flow
	 * 
	 * @param AuditHeader (auditHeader)
	 * @return auditHeader
	 */

	public AuditHeader doReject(AuditHeader auditHeader) {
		logger.debug("Entering");
		auditHeader = businessValidation(auditHeader, "doReject");
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}

		DirectorDetail directorDetail = (DirectorDetail) auditHeader.getAuditDetail().getModelData();

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getDirectorDetailDAO().delete(directorDetail, "_Temp");

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * businessValidation method do the following steps. 1) get the details from the auditHeader. 2) fetch the details
	 * from the tables 3) Validate the Record based on the record details. 4) Validate for any business validation. 5)
	 * for any mismatch conditions Fetch the error details from getDirectorDetailDAO().getErrorDetail with Error ID and
	 * language as parameters. 6) if any error/Warnings then assign the to auditHeader
	 * 
	 * @param AuditHeader (auditHeader)
	 * @return auditHeader
	 */
	private AuditHeader businessValidation(AuditHeader auditHeader, String method) {
		logger.debug("Entering");
		auditHeader = getDirectorValidation().directorValidation(auditHeader, method);
		auditHeader = nextProcess(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	@Override
	public AuditDetail doValidations(DirectorDetail directorDetail, Customer customerDetails) {

		AuditDetail auditDetail = new AuditDetail();

		// gender code Validation

		if (!genderDAO.isValidGenderCode(directorDetail.getCustGenderCode())) {
			String[] valueParm = new String[1];
			valueParm[0] = directorDetail.getCustGenderCode();
			auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("90701", valueParm)));
		}
		if (directorDetail.isShareholder()) {
			if (directorDetail.getSharePerc() == null
					|| directorDetail.getSharePerc().compareTo(BigDecimal.ZERO) <= 0) {
				String[] valueParm = new String[1];
				valueParm[0] = "sharePerc";
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("90502", "", valueParm)));
			}
		}
		if (directorDetail.isDirector()) {
			if (StringUtils.isNotBlank(directorDetail.getDesignation())) {
				Designation designationById = designationDAO.getDesignationById(directorDetail.getDesignation(), "");
				if (designationById == null) {
					String[] valueParm = new String[1];
					valueParm[0] = directorDetail.getDesignation();
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("90501", "", valueParm)));
				}
			} else {
				String[] valueParm = new String[1];
				valueParm[0] = "designation";
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("90502", "", valueParm)));
			}
		}
		if (StringUtils.isNotBlank(directorDetail.getCustSalutationCode())
				&& StringUtils.isNotBlank(directorDetail.getCustGenderCode())) {
			// salutation validation
			int salutationByCount = salutationDAO.getSalutationByCount(directorDetail.getCustSalutationCode(),
					directorDetail.getCustGenderCode());
			if (salutationByCount <= 0) {
				String[] valueParm = new String[2];
				valueParm[0] = directorDetail.getCustSalutationCode();
				valueParm[1] = directorDetail.getCustGenderCode();
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("90701", "", valueParm)));
			}
		}
		// Id type validation
		if (StringUtils.isNotBlank(directorDetail.getIdType())) {
			int docTypeCount = customerDocumentDAO.getDocTypeCount(directorDetail.getIdType());
			if (docTypeCount <= 0) {
				String[] valueParm = new String[1];
				valueParm[0] = directorDetail.getIdType();
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("90701", "", valueParm)));
			}

		}

		if (StringUtils.isNotBlank(directorDetail.getNationality())) {
			// Nationality
			NationalityCode nationalityCodeById = nationalityCodeDAO
					.getNationalityCodeById(directorDetail.getNationality(), "");
			if (nationalityCodeById == null) {
				String[] valueParm = new String[2];
				valueParm[0] = directorDetail.getCustAddrCountry();
				valueParm[1] = directorDetail.getCustAddrProvince();
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("90701", "", valueParm)));
			}
		}

		if (StringUtils.isNotBlank(directorDetail.getCustAddrCountry())
				&& StringUtils.isNotBlank(directorDetail.getCustAddrProvince())
				&& StringUtils.isNotBlank(directorDetail.getCustAddrCity())) {
			// country,state,city validation
			City city = cityDAO.getCityById(directorDetail.getCustAddrCountry(), directorDetail.getCustAddrProvince(),
					directorDetail.getCustAddrCity(), "");
			if (city == null) {
				String[] valueParm = new String[2];
				valueParm[0] = directorDetail.getCustAddrCountry();
				valueParm[1] = directorDetail.getCustAddrProvince();
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("90701", "", valueParm)));
			}
		} else {
			String[] valueParm = new String[1];
			valueParm[0] = "custAddrCountry , custAddrCity, custAddrProvince";
			auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("90502", "", valueParm)));
		}
		return auditDetail;
	}

}