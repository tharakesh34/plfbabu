package com.pennant.backend.service.customermasters.validation;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.zkoss.util.resource.Labels;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.dao.customermasters.CustomerAddresDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.customermasters.CustomerAddres;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;

public class CustomerAddressValidation {

	private CustomerAddresDAO customerAddresDAO;

	public CustomerAddressValidation(CustomerAddresDAO customerAddresDAO) {
		this.customerAddresDAO = customerAddresDAO;
	}

	/**
	 * @return the customerAddresDAO
	 */
	public CustomerAddresDAO getCustomerAddresDAO() {
		return customerAddresDAO;
	}

	public AuditHeader addressValidation(AuditHeader auditHeader, String method) {

		AuditDetail auditDetail = validate(auditHeader.getAuditDetail(),
				method, auditHeader.getUsrLanguage());
		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());
		return auditHeader;
	}

	public List<AuditDetail> addressListValidation(
			List<AuditDetail> auditDetails, String method, String usrLanguage) {

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
	 * For Validating AuditDetals object getting from Audit Header, if any
	 * mismatch conditions Fetch the error details from ErrorControl with Error
	 * ID and language as parameters. if any error/Warnings then assign the to
	 * auditDeail Object
	 * 
	 * @param auditDetail
	 * @param usrLanguage
	 * @param method
	 * @return
	 */
	private AuditDetail validate(AuditDetail auditDetail, String method, String usrLanguage) {

		CustomerAddres customerAddres = (CustomerAddres) auditDetail.getModelData();
		CustomerAddres tempCustomerAddres = null;
		if (customerAddres.isWorkflow()) {
			tempCustomerAddres = getCustomerAddresDAO().getCustomerAddresById(
					customerAddres.getId(), customerAddres.getCustAddrType(), "_Temp");
		}

		CustomerAddres befCustomerAddres = getCustomerAddresDAO()
				.getCustomerAddresById(customerAddres.getId(), customerAddres.getCustAddrType(), "");
		CustomerAddres oldCustomerAddres = customerAddres.getBefImage();

		String[] valueParm = new String[2];
		String[] errParm = new String[2];

		valueParm[0] = StringUtils.trimToEmpty(customerAddres.getLovDescCustCIF());
		valueParm[1] = customerAddres.getCustAddrType();

        errParm[0] = PennantJavaUtil.getLabel("AddressDetails") +" , " + PennantJavaUtil.getLabel("label_CustCIF") + ":" + valueParm[0]+ " and ";
        errParm[1] = PennantJavaUtil.getLabel("label_CustAddrType") + "-" + valueParm[1];
		
		if (customerAddres.isNew()) { // for New record or new record into work
			// flow

			if (!customerAddres.isWorkflow()) {// With out Work flow only new
				// records
				if (befCustomerAddres != null) { // Record Already Exists in the
					// table then error
					auditDetail.setErrorDetail(new ErrorDetails(
									PennantConstants.KEY_FIELD, "41001", errParm, null));
				}
			} else { // with work flow

				if (customerAddres.getRecordType().equals(
						PennantConstants.RECORD_TYPE_NEW)) { // if records type
																// is new
					if (befCustomerAddres != null || tempCustomerAddres != null) {
						// if records already
						// exists in the
						// main table
						auditDetail.setErrorDetail(new ErrorDetails(
								PennantConstants.KEY_FIELD, "41001", errParm, null));
					}
				} else { // if records not exists in the Main flow table
					if (befCustomerAddres == null || tempCustomerAddres != null) {
						auditDetail.setErrorDetail(new ErrorDetails(
								PennantConstants.KEY_FIELD, "41005", errParm, null));
					}
				}
			}
		} else {
			// for work flow process records or (Record to update or Delete with
			// out work flow)
			if (!customerAddres.isWorkflow()) { // With out Work flow for update
				// and delete

				if (befCustomerAddres == null) { // if records not exists in the
					// main table
					auditDetail.setErrorDetail(new ErrorDetails(
									PennantConstants.KEY_FIELD, "41002", errParm, null));
				} else {

					if (oldCustomerAddres != null && !oldCustomerAddres.getLastMntOn().equals(
									befCustomerAddres.getLastMntOn())) {
						if (StringUtils.trimToEmpty(auditDetail.getAuditTranType()).
								equalsIgnoreCase(PennantConstants.TRAN_DEL)) {
							auditDetail.setErrorDetail(new ErrorDetails(
									PennantConstants.KEY_FIELD, "41003", errParm, null));
						} else {
							auditDetail.setErrorDetail(new ErrorDetails(
									PennantConstants.KEY_FIELD, "41004", errParm, null));
						}
					}
				}
			} else {

				if (tempCustomerAddres == null) { // if records not exists in
					// the Work flow table
					auditDetail.setErrorDetail(new ErrorDetails(
							PennantConstants.KEY_FIELD, "41005", errParm, null));
				}

				if (tempCustomerAddres != null && oldCustomerAddres != null
						&& !oldCustomerAddres.getLastMntOn().equals(tempCustomerAddres.getLastMntOn())) {
					auditDetail.setErrorDetail(new ErrorDetails(
									PennantConstants.KEY_FIELD, "41005", errParm, null));
				}
			}
		}

		auditDetail.setErrorDetail(screenValidations(customerAddres));
		
		boolean isServiceable=getCustomerAddresDAO()
				.isServiceable(customerAddres.getCustAddrZIP());
		if(!isServiceable){
			valueParm[0] = StringUtils.trimToEmpty(customerAddres.getCustAddrZIP());

	        errParm[0] = PennantJavaUtil.getLabel("label_PinCode") + "-" + valueParm[0];
			auditDetail.setErrorDetail(new ErrorDetails(
					PennantConstants.KEY_FIELD, "81005", errParm, null));
		}
		
		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));
		
		if ("doApprove".equals(StringUtils.trimToEmpty(method)) || !customerAddres.isWorkflow()) {
			customerAddres.setBefImage(befCustomerAddres);
		}
		return auditDetail;
	}

	/**
	 * Method For Screen Level Validations
	 * 
	 * @param auditHeader
	 * @param usrLanguage
	 * @return
	 */
	public ErrorDetails  screenValidations(CustomerAddres customerAddres){
		
		if(StringUtils.isBlank(customerAddres.getCustAddrHNbr())){
			return	new ErrorDetails(PennantConstants.KEY_FIELD,"30535", 
					new String[] {Labels.getLabel("AddressDetails"),
					Labels.getLabel("label_CustomerAddresDialog_CustAddrHNbr.value"),
					Labels.getLabel("listheader_CustAddrType.label"),
					customerAddres.getCustAddrType()},
					new String[] {});	
		}
		
		if(StringUtils.isBlank(customerAddres.getCustAddrStreet())){
			return	new ErrorDetails(PennantConstants.KEY_FIELD,"30535", 
					new String[] {Labels.getLabel("AddressDetails"),
					Labels.getLabel("label_CustomerAddresDialog_CustAddrStreet.value"),
					Labels.getLabel("listheader_CustAddrType.label"),
					customerAddres.getCustAddrType()},
					new String[] {});	
		}
		
		/*if(StringUtils.trimToEmpty(customerAddres.getCustPOBox()).equals("")){
			return	new ErrorDetails(PennantConstants.KEY_FIELD,"30535", 
					new String[] {Labels.getLabel("AddressDetails"),
					Labels.getLabel("label_CustomerAddresDialog_CustPOBox.value"),
					Labels.getLabel("listheader_CustAddrType.label"),
					customerAddres.getCustAddrType()},
					new String[] {});	
		}*/
		
		if(StringUtils.isBlank(customerAddres.getCustAddrCountry())){
			return	new ErrorDetails(PennantConstants.KEY_FIELD,"30535", 
					new String[] {Labels.getLabel("AddressDetails"),
					Labels.getLabel("label_CustomerAddresDialog_CustAddrCountry.value"),
					Labels.getLabel("listheader_CustAddrType.label"),
					customerAddres.getCustAddrType()},
					new String[] {});	
		}
		
		if(StringUtils.isBlank(customerAddres.getCustAddrProvince())){
			return	new ErrorDetails(PennantConstants.KEY_FIELD,"30535", 
					new String[] {Labels.getLabel("AddressDetails"),
					Labels.getLabel("label_CustomerAddresDialog_CustAddrProvince.value"),
					Labels.getLabel("listheader_CustAddrType.label"),
					customerAddres.getCustAddrType()},
					new String[] {});	
		}
		
	/*	if(StringUtils.trimToEmpty(customerAddres.getCustAddrCity()).equals("")){
			return	new ErrorDetails(PennantConstants.KEY_FIELD,"30535", 
					new String[] {Labels.getLabel("AddressDetails"),
					Labels.getLabel("label_CustomerAddresDialog_CustAddrCity.value"),
					Labels.getLabel("listheader_CustAddrType.label"),
					customerAddres.getCustAddrType()},
					new String[] {});	
		}
	*/
		return null;
	}
}
