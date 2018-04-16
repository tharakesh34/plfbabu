/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. 
 * All components/modules/functions/classes/logic in this software, unless 
 * otherwise stated, the property of Pennant Technologies. 
 * 
 * Copyright and other intellectual property laws protect these materials. 
 * Reproduction or retransmission of the materials, in whole or in part, in any manner, 
 * without the prior written consent of the copyright holder, is a violation of 
 * copyright law.
 */

package com.pennanttech.pennapps.pff.verification.service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.dao.systemmasters.AddressTypeDAO;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.customermasters.CustomerAddres;
import com.pennant.backend.model.customermasters.CustomerDetails;
import com.pennant.backend.model.customermasters.CustomerPhoneNumber;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennanttech.pennapps.core.engine.workflow.WorkflowEngine;
import com.pennanttech.pennapps.core.feature.ModuleUtil;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.pff.verification.Decision;
import com.pennanttech.pennapps.pff.verification.RequestType;
import com.pennanttech.pennapps.pff.verification.VerificationType;
import com.pennanttech.pennapps.pff.verification.dao.FieldInvestigationDAO;
import com.pennanttech.pennapps.pff.verification.dao.VerificationDAO;
import com.pennanttech.pennapps.pff.verification.model.FieldInvestigation;
import com.pennanttech.pennapps.pff.verification.model.Verification;
import com.pennanttech.pff.core.TableType;

/**
 * Service implementation for methods that depends on
 * <b>FieldInvestigation</b>.<br>
 */
public class FieldInvestigationServiceImpl extends GenericService<FieldInvestigation>
		implements FieldInvestigationService {
	private static final Logger logger = Logger.getLogger(FieldInvestigationServiceImpl.class);

	private AuditHeaderDAO auditHeaderDAO;
	private FieldInvestigationDAO fieldInvestigationDAO;
	private VerificationDAO verificationDAO;
	
	@Autowired
	private AddressTypeDAO			addressTypeDAO;
	
	
	@Override
	public void save(FieldInvestigation fieldInvestigation, TableType tempTab) {
		setAudit(fieldInvestigation);
		fieldInvestigationDAO.save(fieldInvestigation, tempTab);
	}

	/**
	 * saveOrUpdate method method do the following steps. 1) Do the Business
	 * validation by using businessValidation(auditHeader) method if there is
	 * any error or warning message then return the auditHeader. 2) Do Add or
	 * Update the Record a) Add new Record for the new record in the DB table
	 * verification_fi/verification_fi_Temp by using verification_fiDAO's save
	 * method b) Update the Record in the table. based on the module workFlow
	 * Configuration. by using verification_fiDAO's update method 3) Audit the
	 * record in to AuditHeader and Adtverification_fi by using
	 * auditHeaderDAO.addAudit(auditHeader)
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	public AuditHeader saveOrUpdate(AuditHeader auditHeader) {
		logger.info(Literal.ENTERING);

		auditHeader = businessValidation(auditHeader, "saveOrUpdate");

		if (!auditHeader.isNextProcess()) {
			logger.info(Literal.LEAVING);
			return auditHeader;
		}

		FieldInvestigation fieldInvestigation = (FieldInvestigation) auditHeader.getAuditDetail().getModelData();

		TableType tableType = TableType.MAIN_TAB;
		if (fieldInvestigation.isWorkflow()) {
			tableType = TableType.TEMP_TAB;
		}

		if (fieldInvestigation.isNew()) {
			fieldInvestigation.setId(Long.parseLong(getFieldInvestigationDAO().save(fieldInvestigation, tableType)));
			auditHeader.getAuditDetail().setModelData(fieldInvestigation);
			auditHeader.setAuditReference(String.valueOf(fieldInvestigation.getId()));
		} else {
			getFieldInvestigationDAO().update(fieldInvestigation, tableType);
		}

		getAuditHeaderDAO().addAudit(auditHeader);
		logger.info(Literal.LEAVING);
		return auditHeader;

	}

	/**
	 * delete method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) delete Record for the DB
	 * table verification_fi by using verification_fiDAO's delete method with
	 * type as Blank 3) Audit the record in to AuditHeader and
	 * Adtverification_fi by using auditHeaderDAO.addAudit(auditHeader)
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
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

		FieldInvestigation fieldInvestigation = (FieldInvestigation) auditHeader.getAuditDetail().getModelData();
		getFieldInvestigationDAO().delete(fieldInvestigation, TableType.MAIN_TAB);

		getAuditHeaderDAO().addAudit(auditHeader);

		logger.info(Literal.LEAVING);
		return auditHeader;
	}

	/**
	 * getverification_fi fetch the details by using verification_fiDAO's
	 * getverification_fiById method.
	 * 
	 * @param id
	 *            id of the FieldInvestigation.
	 * @return verification_fi
	 */
	@Override
	public FieldInvestigation getFieldInvestigation(long id) {
		return getFieldInvestigationDAO().getFieldInvestigation(id,"_View");
	}

	/**
	 * getApprovedverification_fiById fetch the details by using
	 * verification_fiDAO's getverification_fiById method . with parameter id
	 * and type as blank. it fetches the approved records from the
	 * verification_fi.
	 * 
	 * @param id
	 *            id of the FieldInvestigation. (String)
	 * @return verification_fi
	 */
	public FieldInvestigation getApprovedFieldInvestigation(long id) {
		return getFieldInvestigationDAO().getFieldInvestigation(id,"");
	}

	/**
	 * doApprove method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) based on the Record type
	 * do following actions a) DELETE Delete the record from the main table by
	 * using getFieldInvestigationDAO().delete with parameters
	 * fieldInvestigation,"" b) NEW Add new record in to main table by using
	 * getFieldInvestigationDAO().save with parameters fieldInvestigation,"" c)
	 * EDIT Update record in the main table by using
	 * getFieldInvestigationDAO().update with parameters fieldInvestigation,""
	 * 3) Delete the record from the workFlow table by using
	 * getFieldInvestigationDAO().delete with parameters
	 * fieldInvestigation,"_Temp" 4) Audit the record in to AuditHeader and
	 * Adtverification_fi by using auditHeaderDAO.addAudit(auditHeader) for Work
	 * flow 5) Audit the record in to AuditHeader and Adtverification_fi by
	 * using auditHeaderDAO.addAudit(auditHeader) based on the transaction Type.
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
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

		FieldInvestigation fi = new FieldInvestigation();
		BeanUtils.copyProperties((FieldInvestigation) auditHeader.getAuditDetail().getModelData(), fi);

		getFieldInvestigationDAO().delete(fi, TableType.TEMP_TAB);

		if (!PennantConstants.RECORD_TYPE_NEW.equals(fi.getRecordType())) {
			auditHeader.getAuditDetail().setBefImage(fieldInvestigationDAO.getFieldInvestigation(fi.getId(),""));
		}

		if (fi.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;
			getFieldInvestigationDAO().delete(fi, TableType.MAIN_TAB);
		} else {
			fi.setRoleCode("");
			fi.setNextRoleCode("");
			fi.setTaskId("");
			fi.setNextTaskId("");
			fi.setWorkflowId(0);

			if (fi.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_ADD;
				fi.setRecordType("");
				getFieldInvestigationDAO().save(fi, TableType.MAIN_TAB);
				getVerificationDAO().updateVerifiaction(fi.getId(), fi.getDate(), fi.getStatus());
			} else {
				tranType = PennantConstants.TRAN_UPD;
				fi.setRecordType("");
				getFieldInvestigationDAO().update(fi, TableType.MAIN_TAB);
				getVerificationDAO().updateVerifiaction(fi.getId(), fi.getDate(), fi.getStatus());
			}
		}

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getAuditHeaderDAO().addAudit(auditHeader);

		auditHeader.setAuditTranType(tranType);
		auditHeader.getAuditDetail().setAuditTranType(tranType);
		auditHeader.getAuditDetail().setModelData(fi);
		getAuditHeaderDAO().addAudit(auditHeader);

		logger.info(Literal.LEAVING);
		return auditHeader;

	}

	/**
	 * doReject method do the following steps. 1) Do the Business validation by
	 * using businessValidation(auditHeader) method if there is any error or
	 * warning message then return the auditHeader. 2) Delete the record from
	 * the workFlow table by using getFieldInvestigationDAO().delete with
	 * parameters fieldInvestigation,"_Temp" 3) Audit the record in to
	 * AuditHeader and Adtverification_fi by using
	 * auditHeaderDAO.addAudit(auditHeader) for Work flow
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
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

		FieldInvestigation fieldInvestigation = (FieldInvestigation) auditHeader.getAuditDetail().getModelData();

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		getFieldInvestigationDAO().delete(fieldInvestigation, TableType.TEMP_TAB);

		getAuditHeaderDAO().addAudit(auditHeader);

		logger.info(Literal.LEAVING);
		return auditHeader;
	}

	/**
	 * businessValidation method do the following steps. 1) get the details from
	 * the auditHeader. 2) fetch the details from the tables 3) Validate the
	 * Record based on the record details. 4) Validate for any business
	 * validation.
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
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
	 * For Validating AuditDetals object getting from Audit Header, if any
	 * mismatch conditions Fetch the error details from
	 * getFieldInvestigationDAO().getErrorDetail with Error ID and language as
	 * parameters. if any error/Warnings then assign the to auditDeail Object
	 * 
	 * @param auditDetail
	 * @param usrLanguage
	 * @return
	 */

	private AuditDetail validation(AuditDetail auditDetail, String usrLanguage) {
		logger.debug(Literal.ENTERING);

		// Write the required validation over hear.

		logger.debug(Literal.LEAVING);
		return auditDetail;
	}
	
	
	
	
	@Override
	public List<Long> getFieldInvestigationIds(List<Verification> verifications,String keyRef) {
		List<Long> fiIds = new ArrayList<>();
		List<FieldInvestigation> fiList = fieldInvestigationDAO.getList(keyRef);
		for (FieldInvestigation fieldInvestigation : fiList) {
			for (Verification Verification : verifications) {
				if (fieldInvestigation.getVerificationId() == Verification.getId()) {
					fiIds.add(Verification.getId());
				}
			}

		}
		return fiIds;
	}
	
	@Override
	public void save(CustomerDetails applicant, List<CustomerPhoneNumber> phoneNumbers, Verification item) {
		for (CustomerAddres address : applicant.getAddressList()) {
			if ((item.getRequestType() == RequestType.INITIATE.getKey()
					|| item.getDecision() == Decision.RE_INITIATE.getKey()) && (item.getCustId() == address.getCustID())
					&& item.getReferenceFor().equals(address.getCustAddrType())) {
				setFiFields(item, address, phoneNumbers);
				fieldInvestigationDAO.save(item.getFieldInvestigation(), TableType.TEMP_TAB);
				break;
			}
		}
	}
	
	private void setFiFields(Verification verification, CustomerAddres address,
			List<CustomerPhoneNumber> phoneNumbers) {
		
		FieldInvestigation fi = new FieldInvestigation();

		fi.setVerificationId(verification.getId());
		fi.setAddressType(address.getCustAddrType());
		fi.setName(verification.getCustomerName());
		fi.setHouseNumber(address.getCustAddrHNbr());
		fi.setFlatNumber(address.getCustFlatNbr());
		fi.setStreet(address.getCustAddrStreet());
		fi.setAddressLine1(address.getCustAddrLine1());
		fi.setAddressLine2(address.getCustAddrLine2());
		fi.setAddressLine3(address.getCustAddrLine3());
		fi.setAddressLine4(address.getCustAddrLine4());
		fi.setAddressLine5(null);
		fi.setCountry(address.getCustAddrCountry());
		fi.setProvince(address.getCustAddrProvince());
		fi.setCity(address.getCustAddrCity());
		fi.setVersion(1);
		fi.setLastMntBy(verification.getLastMntBy());
		fi.setLastMntOn(verification.getLastMntOn());
		setAudit(fi);
		Collections.sort(phoneNumbers, new PhonePriority());
		fi.setContactNumber1((phoneNumbers.get(0)).getPhoneNumber());
		if (phoneNumbers.size() > 1) {
			fi.setContactNumber2((phoneNumbers.get(1)).getPhoneNumber());
		}
		fi.setPoBox(address.getCustPOBox());
		fi.setZipCode(address.getCustAddrZIP());

		verification.setFieldInvestigation(fi);
	}
	
	private void setAudit(FieldInvestigation fi) {
		String workFlowType = ModuleUtil.getWorkflowType("FieldInvestigation");
		WorkFlowDetails workFlowDetails = WorkFlowUtil.getDetailsByType(workFlowType);
		WorkflowEngine engine = new WorkflowEngine(
				WorkFlowUtil.getWorkflow(workFlowDetails.getWorkFlowId()).getWorkFlowXml());

		fi.setRecordStatus(PennantConstants.RCD_STATUS_SAVED);
		fi.setRecordType(PennantConstants.RECORD_TYPE_NEW);
		fi.setWorkflowId(workFlowDetails.getWorkflowId());
		fi.setRoleCode(workFlowDetails.getFirstTaskOwner());
		fi.setNextRoleCode(workFlowDetails.getFirstTaskOwner());
		fi.setTaskId(engine.getUserTaskId(fi.getRoleCode()));
		fi.setNextTaskId(engine.getUserTaskId(fi.getNextRoleCode()) + ";");
	}

	private List<Verification> getScreenVerifications(Verification verification) {
		List<Verification> verifications = new ArrayList<>();
		List<CustomerDetails> customerDetailsList = verification.getCustomerDetailsList();
		List<String> requiredCodes = addressTypeDAO.getFiRequiredCodes();

		for (CustomerDetails customerDetails : customerDetailsList) {
			for (CustomerAddres address : customerDetails.getAddressList()) {
				Verification vrf = new Verification();
				vrf.setNewRecord(true);
				vrf.setVerificationType(verification.getVerificationType());
				vrf.setModule(verification.getModule());
				vrf.setKeyReference(verification.getKeyReference());
				vrf.setVerificationType(VerificationType.FI.getKey());
				vrf.setCustId(customerDetails.getCustomer().getCustID());
				vrf.setCif(customerDetails.getCustomer().getCustCIF());
				vrf.setReference(vrf.getCif());
				vrf.setCustomerName(customerDetails.getCustomer().getCustShrtName());
				if (verification.getCif().equals(vrf.getCif())) {
					vrf.setReferenceType("Primary");
				} else {
					vrf.setReferenceType("Co-applicant");
				}

				if (requiredCodes.contains(address.getCustAddrType())) {
					vrf.setRequestType(RequestType.INITIATE.getKey());
				} else {
					vrf.setRequestType(RequestType.NOT_REQUIRED.getKey());
				}
				vrf.setRecordType(address.getRecordType());
				vrf.setReferenceFor(address.getCustAddrType());
				vrf.setCreatedBy(verification.getCreatedBy());
				vrf.setCreatedOn(new Timestamp(System.currentTimeMillis()));
				setFiFields(vrf, address, customerDetails.getCustomerPhoneNumList());

				verifications.add(vrf);
			}
		}
		return verifications;
	}
	
	@Override
	public Verification getFiVeriFication(Verification verification) {
		logger.info(Literal.ENTERING);
		List<Verification> preVerifications = verificationDAO.getFiVeriFications(verification.getKeyReference());

		if (!preVerifications.isEmpty()) {
			List<FieldInvestigation> fiList = fieldInvestigationDAO.getList(verification.getKeyReference());

			for (Verification pvr : preVerifications) {
				for (FieldInvestigation fi : fiList) {
					if (pvr.getId() == fi.getVerificationId()) {
						pvr.setFieldInvestigation(fi);
					}
				}
			}

		}
		List<Verification> screenVerifications = getScreenVerifications(verification);
		screenVerifications.addAll(getChangedVerifications(preVerifications, screenVerifications,verification.getKeyReference()));
		verification.setVerifications(compareVerifications(screenVerifications, preVerifications,verification.getKeyReference()));

		logger.info(Literal.LEAVING);
		return verification;
	}

	private List<Verification> getChangedVerifications(List<Verification> oldList, List<Verification> newList,String keyReference) {
		List<Verification> verifications = new ArrayList<>();
		List<Long> fiIds=getFieldInvestigationIds(oldList,keyReference);
		for (Verification oldVer : oldList) {
			for (Verification newVer : newList) {
				if (oldVer.getCustId().compareTo(newVer.getCustId()) == 0
						&& oldVer.getReferenceFor().equals(newVer.getReferenceFor())) {
					if (oldVer.getRequestType() == RequestType.INITIATE.getKey()
							&& isAddressChange(oldVer.getFieldInvestigation(), newVer.getFieldInvestigation())
							&& !fiIds.contains(oldVer.getId())) {
						verifications.add(oldVer);
					}
				}
			}
		}
		return verifications;
	}

	private boolean isAddressChange(FieldInvestigation oldAddress, FieldInvestigation newAddress) {

		if (oldAddress == null || newAddress == null) {
			return false;
		}

		if (!StringUtils.equals(oldAddress.getHouseNumber(), newAddress.getHouseNumber())) {
			return true;
		}
		if (!StringUtils.equals(oldAddress.getFlatNumber(), newAddress.getFlatNumber())) {
			return true;
		}
		if (!StringUtils.equals(oldAddress.getStreet(), newAddress.getStreet())) {
			return true;
		}
		if (!StringUtils.equals(oldAddress.getAddressLine1(), newAddress.getAddressLine1())) {
			return true;
		}
		if (!StringUtils.equals(oldAddress.getAddressLine2(), newAddress.getAddressLine2())) {
			return true;
		}
		if (!StringUtils.equals(oldAddress.getAddressLine3(), newAddress.getAddressLine3())) {
			return true;
		}
		if (!StringUtils.equals(oldAddress.getAddressLine4(), newAddress.getAddressLine4())) {
			return true;
		}
		if (!StringUtils.equals(oldAddress.getCountry(), newAddress.getCountry())) {
			return true;
		}
		if (!StringUtils.equals(oldAddress.getProvince(), newAddress.getProvince())) {
			return true;
		}
		if (!StringUtils.equals(oldAddress.getCity(), newAddress.getCity())) {
			return true;
		}
		if (!StringUtils.equals(oldAddress.getZipCode(), newAddress.getZipCode())) {
			return true;
		}
		if (!StringUtils.equals(oldAddress.getPoBox(), newAddress.getPoBox())) {
			return true;
		}
		return false;
	}

	private List<Verification> compareVerifications(List<Verification> screenVerifications,
			List<Verification> preVerifications, String keyReference) {
		List<Verification> tempList = new ArrayList<>();
		tempList.addAll(screenVerifications);
		tempList.addAll(preVerifications);
		List<Long> fiIds=getFieldInvestigationIds(preVerifications,keyReference);
		
		screenVerifications.addAll(preVerifications);

		for (Verification vrf : tempList) {
			for (Verification preVrf : preVerifications) {
				if (vrf.getCustId().compareTo(preVrf.getCustId()) == 0
						&& vrf.getReferenceFor().equals(preVrf.getReferenceFor())
						&& (StringUtils.isEmpty(vrf.getRecordType())
								|| !vrf.getRecordType().equals(PennantConstants.RCD_UPD))
						&& !isAddressChange(preVrf.getFieldInvestigation(), vrf.getFieldInvestigation())
						&& !fiIds.contains(vrf.getId())) {
					screenVerifications.remove(vrf);
					preVerifications.remove(preVrf);
					break;
				}
			}
		}

		return screenVerifications;
	}
	
	@Override
	public boolean isAddressesAdded(List<CustomerAddres> screenCustomerAddresses,
			List<CustomerAddres> savedCustomerAddresses) {
		boolean flag = true;
		for (CustomerAddres screenCustomerAddres : screenCustomerAddresses) {
			for (CustomerAddres savedCustomerAddres : savedCustomerAddresses) {
				if (savedCustomerAddres.getCustAddrType().equals(screenCustomerAddres.getCustAddrType())
						|| (StringUtils.isNotEmpty(screenCustomerAddres.getRecordType())
								&& screenCustomerAddres.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL))) {
					flag = false;
				}
			}
			if (flag) {
				return flag;
			}
			flag = true;
		}
		return false;
	}

	@Override
	public boolean isAddressChanged(CustomerAddres newAddress, CustomerAddres oldAddress) {

		if (!StringUtils.equals(newAddress.getCustAddrHNbr(), oldAddress.getCustAddrHNbr())) {
			return true;
		}
		if (!StringUtils.equals(newAddress.getCustFlatNbr(), oldAddress.getCustFlatNbr())) {
			return true;
		}
		if (!StringUtils.equals(newAddress.getCustAddrStreet(), oldAddress.getCustAddrStreet())) {
			return true;
		}
		if (!StringUtils.equals(newAddress.getCustAddrLine1(), oldAddress.getCustAddrLine1())) {
			return true;
		}
		if (!StringUtils.equals(newAddress.getCustAddrLine2(), oldAddress.getCustAddrLine2())) {
			return true;
		}
		if (!StringUtils.equals(newAddress.getCustAddrLine3(), oldAddress.getCustAddrLine3())) {
			return true;
		}
		if (!StringUtils.equals(newAddress.getCustAddrLine4(), oldAddress.getCustAddrLine4())) {
			return true;
		}
		if (!StringUtils.equals(newAddress.getCustAddrCountry(), oldAddress.getCustAddrCountry())) {
			return true;
		}
		if (!StringUtils.equals(newAddress.getCustAddrProvince(), oldAddress.getCustAddrProvince())) {
			return true;
		}
		if (!StringUtils.equals(newAddress.getCustAddrCity(), oldAddress.getCustAddrCity())) {
			return true;
		}
		if (!StringUtils.equals(newAddress.getCustPOBox(), oldAddress.getCustPOBox())) {
			return true;
		}
		if (!StringUtils.equals(newAddress.getCustAddrZIP(), oldAddress.getCustAddrZIP())) {
			return true;
		}

		return false;
	}
	
	public class PhonePriority implements Comparator<CustomerPhoneNumber> {
		@Override
		public int compare(CustomerPhoneNumber o1, CustomerPhoneNumber o2) {
			return o2.getPhoneTypePriority() - o1.getPhoneTypePriority();
		}

	}
	
	
	
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
	 * @param auditHeaderDAO
	 *            the auditHeaderDAO to set
	 */
	public void setAuditHeaderDAO(AuditHeaderDAO auditHeaderDAO) {
		this.auditHeaderDAO = auditHeaderDAO;
	}

	/**
	 * @return the fieldInvestigationDAO
	 */
	public FieldInvestigationDAO getFieldInvestigationDAO() {
		return fieldInvestigationDAO;
	}

	/**
	 * @param fieldInvestigationDAO
	 *            the fieldInvestigationDAO to set
	 */
	public void setFieldInvestigationDAO(FieldInvestigationDAO fieldInvestigationDAO) {
		this.fieldInvestigationDAO = fieldInvestigationDAO;
	}

	public VerificationDAO getVerificationDAO() {
		return verificationDAO;
	}

	public void setVerificationDAO(VerificationDAO verificationDAO) {
		this.verificationDAO = verificationDAO;
	}

	@Override
	public List<FieldInvestigation> getList(String keyReference) {
		return fieldInvestigationDAO.getList(keyReference);
	}
}