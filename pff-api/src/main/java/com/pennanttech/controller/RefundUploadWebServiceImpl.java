package com.pennanttech.controller;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pennant.backend.model.WSReturnStatus;
import com.pennant.backend.model.refundupload.RefundUpload;
import com.pennant.validation.SaveValidationGroup;
import com.pennant.validation.ValidationUtility;
import com.pennant.ws.exception.ServiceException;
import com.pennanttech.pffws.RefundUploadRestService;
import com.pennanttech.pffws.RefundUploadSoapService;

@Service
public class RefundUploadWebServiceImpl extends ExtendedTestClass
		implements RefundUploadRestService, RefundUploadSoapService {
	private static final Logger logger = LogManager.getLogger(RefundUploadWebServiceImpl.class);

	private ValidationUtility validationUtility;
	private RefundUploadController refundUploadController;

	/**
	 * Method for create Mandate in PLF system.
	 * 
	 * @param refundUpload
	 * @throws ServiceException
	 */
	@Override
	public RefundUpload createRefundUpload(RefundUpload refundUpload) throws ServiceException {
		logger.debug("Entering");

		// bean validations
		validationUtility.validate(refundUpload, SaveValidationGroup.class);
		WSReturnStatus returnStatus = doRefundUploadValidation(refundUpload);

		RefundUpload response = null;
		if (StringUtils.isBlank(returnStatus.getReturnCode())) {
			response = refundUploadController.createRefundUpload(refundUpload);
		} else {
			response = new RefundUpload();
			response.setReturnStatus(returnStatus);
		}

		logger.debug("Leaving");
		return response;
	}

	/**
	 * Validate the mandatory fields in the request object
	 * 
	 * @param refundUpload
	 * @return returnStatus
	 */
	private WSReturnStatus doRefundUploadValidation(RefundUpload refundUpload) {
		logger.debug("Entering");

		WSReturnStatus returnStatus = new WSReturnStatus();
		// validate customer
		// String custCIF = refundUpload.getCustCIF();
		// Customer customer = null;
		// if (StringUtils.isNotBlank(custCIF)) {
		// customer = customerDetailsService.getCustomerByCIF(custCIF);
		// if (customer == null) {
		// String[] valueParm = new String[1];
		// valueParm[0] = custCIF;
		// return getErrorDetails("90101", valueParm);
		// }
		// }
		//
		// // validate the entityCode
		// if (StringUtils.isBlank(refundUpload.getEntityCode())) {
		// String[] valueParm = new String[1];
		// valueParm[0] = "Entity";
		// return getErrorDetails("90502", valueParm);
		// }
		// Entity entity = entityService.getApprovedEntity(refundUpload.getEntityCode());
		// if (entity == null || !entity.isActive()) {
		// String[] valueParm = new String[2];
		// valueParm[0] = "Entity";
		// valueParm[1] = refundUpload.getEntityCode();
		// return getErrorDetails("90701", valueParm);
		//
		// }
		//
		// //validate finance reference
		// if (StringUtils.isBlank(refundUpload.getOrgReference())) {
		// String[] valueParm = new String[1];
		// valueParm[0] = "finReference";
		// return getErrorDetails("90502", valueParm);
		// }
		// if (StringUtils.isNotBlank(refundUpload.getOrgReference())) {
		// int count = financeMainService.getFinanceCountById(refundUpload.getOrgReference(), false);
		// if (count <= 0) {
		// String[] valueParm = new String[1];
		// valueParm[0] = refundUpload.getOrgReference();
		// return getErrorDetails("90201", valueParm);
		// }
		// List<String> finRefList = financeMainService.getFinanceMainbyCustId(customer.getCustID());
		// boolean validFinrefernce = true;
		// for (String finReference : finRefList) {
		// if (StringUtils.equals(finReference, refundUpload.getOrgReference())) {
		// validFinrefernce = false;
		// }
		// }
		// if (validFinrefernce) {
		// returnStatus = new WSReturnStatus();
		// String[] valueParm = new String[2];
		// valueParm[0] = refundUpload.getCustCIF();
		// valueParm[1] = refundUpload.getOrgReference();
		// return APIErrorHandlerService.getFailedStatus("90406", valueParm);
		// }
		// }
		//
		// // validate MandateType
		// if (StringUtils.isNotBlank(refundUpload.getMandateType())) {
		// List<ValueLabel> mandateType = PennantStaticListUtil.getMandateTypeList();
		// boolean mandateTypeSts = false;
		// for (ValueLabel value : mandateType) {
		// if (StringUtils.equals(value.getValue(), refundUpload.getMandateType())) {
		// mandateTypeSts = true;
		// break;
		// }
		// }
		// if (!mandateTypeSts) {
		// String[] valueParm = new String[1];
		// valueParm[0] = refundUpload.getMandateType();
		// return getErrorDetails("90307", valueParm);
		// }
		// }
		//
		// // validate AccType
		// if (StringUtils.isNotBlank(refundUpload.getAccType())) {
		// List<ValueLabel> accType = PennantStaticListUtil.getAccTypeList();
		// boolean accTypeSts = false;
		// for (ValueLabel value : accType) {
		// if (StringUtils.equals(value.getValue(), refundUpload.getAccType())) {
		// accTypeSts = true;
		// break;
		// }
		// }
		// if (!accTypeSts) {
		// String[] valueParm = new String[1];
		// valueParm[0] = refundUpload.getAccType();
		// return getErrorDetails("90308", valueParm);
		// }
		// }
		// //validate Phone number
		// String mobileNumber = refundUpload.getPhoneNumber();
		// if (StringUtils.isNotBlank(mobileNumber)) {
		// if (!(mobileNumber.matches("\\d{10}"))) {
		// return getErrorDetails("90278", null);
		// }
		// }
		// // validate status
		// if (StringUtils.isNotBlank(refundUpload.getStatus())) {
		// List<ValueLabel> status = PennantStaticListUtil.getStatusTypeList();
		// boolean sts = false;
		// for (ValueLabel value : status) {
		// if (StringUtils.equals(value.getValue(), refundUpload.getStatus())) {
		// sts = true;
		// break;
		// }
		// }
		// if (!sts) {
		// String[] valueParm = new String[1];
		// valueParm[0] = refundUpload.getStatus();
		// return getErrorDetails("90309", valueParm);
		// }
		// }
		//
		// // validate periodicity
		// if (StringUtils.isNotBlank(refundUpload.getPeriodicity())) {
		// ErrorDetail errorDetail = FrequencyUtil.validateFrequency(refundUpload.getPeriodicity());
		// if (errorDetail != null && StringUtils.isNotBlank(errorDetail.getCode())) {
		// String[] valueParm = new String[1];
		// valueParm[0] = refundUpload.getPeriodicity();
		// return getErrorDetails("90207", valueParm);
		// }
		// }
		// boolean isValidBranch = true;
		// // validate Mandate fields
		// if (StringUtils.isNotBlank(refundUpload.getIFSC())) {
		// BankBranch bankBranch = bankBranchService.getBankBrachByIFSC(refundUpload.getIFSC());
		// if (bankBranch == null) {
		// String[] valueParm = new String[1];
		// valueParm[0] = refundUpload.getIFSC();
		// return getErrorDetails("90301", valueParm);
		// } else {
		// isValidBranch = validateBranchCode(refundUpload, isValidBranch, bankBranch);
		// if (returnStatus.getReturnCode() != null) {
		// return returnStatus;
		// }
		// refundUpload.setBankCode(bankBranch.getBankCode());
		// if (StringUtils.isBlank(refundUpload.getMICR())) {
		// refundUpload.setMICR(bankBranch.getMICR());
		// } else {
		// if (!StringUtils.equals(bankBranch.getMICR(), refundUpload.getMICR())) {
		// String[] valueParm = new String[2];
		// valueParm[0] = "MICR";
		// valueParm[1] = refundUpload.getMICR();
		// return getErrorDetails("90701", valueParm);
		// }
		// }
		// }
		// } else if (StringUtils.isNotBlank(refundUpload.getBankCode())
		// && StringUtils.isNotBlank(refundUpload.getBranchCode())) {
		// BankBranch bankBranch = bankBranchService.getBankBrachByCode(refundUpload.getBankCode(),
		// refundUpload.getBranchCode());
		// if (bankBranch == null) {
		// String[] valueParm = new String[2];
		// valueParm[0] = refundUpload.getBankCode();
		// valueParm[1] = refundUpload.getBranchCode();
		// return getErrorDetails("90302", valueParm);
		// } else {
		// isValidBranch = validateBranchCode(refundUpload, isValidBranch, bankBranch);
		// if (returnStatus.getReturnCode() != null) {
		// return returnStatus;
		// }
		// refundUpload.setBankCode(bankBranch.getBankCode());
		// if (StringUtils.isBlank(refundUpload.getMICR())) {
		// refundUpload.setMICR(bankBranch.getMICR());
		// } else {
		// if (!StringUtils.equals(bankBranch.getMICR(), refundUpload.getMICR())) {
		// String[] valueParm = new String[2];
		// valueParm[0] = "MICR";
		// valueParm[1] = refundUpload.getMICR();
		// return getErrorDetails("90701", valueParm);
		// }
		// }
		// }
		// }
		// if (!isValidBranch) {
		// String[] valueParm = new String[1];
		// valueParm[0] = refundUpload.getMandateType();
		// return getErrorDetails("90333", valueParm);
		// }
		// //validate AccNumber length
		// if (StringUtils.isNotBlank(refundUpload.getBankCode())) {
		// int accNoLength = bankDetailService.getAccNoLengthByCode(refundUpload.getBankCode());
		// if (accNoLength != 0) {
		// if (refundUpload.getAccNumber().length() != accNoLength) {
		// String[] valueParm = new String[2];
		// valueParm[0] = "AccountNumber";
		// valueParm[1] = String.valueOf(accNoLength) + " characters";
		// return getErrorDetails("30570", valueParm);
		// }
		// }
		// }
		//
		// if (refundUpload.getExpiryDate() == null) {
		// String[] valueParm = new String[1];
		// valueParm[0] = "expiryDate";
		// return getErrorDetails("90502", valueParm);
		// }
		//
		// //validate Dates
		// if (refundUpload.getExpiryDate() != null) {
		// if (refundUpload.getExpiryDate().compareTo(refundUpload.getStartDate()) <= 0
		// || refundUpload.getExpiryDate().after(SysParamUtil.getValueAsDate("APP_DFT_END_DATE"))) {
		// String[] valueParm = new String[3];
		// valueParm[0] = "ExpiryDate";
		// valueParm[1] = DateUtility.formatToLongDate(DateUtility.addDays(refundUpload.getStartDate(), 1));
		// valueParm[2] = DateUtility.formatToLongDate(SysParamUtil.getValueAsDate("APP_DFT_END_DATE"));
		// return getErrorDetails("90318", valueParm);
		// }
		// }
		// if (refundUpload.getStartDate() != null) {
		// Date mandbackDate = DateUtility.addDays(DateUtility.getAppDate(),
		// -SysParamUtil.getValueAsInt("MANDATE_STARTDATE"));
		// if (refundUpload.getStartDate().before(mandbackDate)
		// || refundUpload.getStartDate().after(SysParamUtil.getValueAsDate("APP_DFT_END_DATE"))) {
		// String[] valueParm = new String[3];
		// valueParm[0] = "mandate start date";
		// valueParm[1] = DateUtility.formatToLongDate(mandbackDate);
		// valueParm[2] = DateUtility.formatToLongDate(SysParamUtil.getValueAsDate("APP_DFT_END_DATE"));
		// return getErrorDetails("90318", valueParm);
		// }
		// }
		//
		// //QDP: Based External Mandate Validate the BarCode and MandateRef
		// if (refundUpload.isExternalMandate()) {
		// if (StringUtils.isEmpty(refundUpload.getMandateRef())) {
		// String[] valueParm = new String[1];
		// valueParm[0] = "MandateRef";
		// return getErrorDetails("90502", valueParm);
		// }
		// } else {
		// if ("Y".equals(SysParamUtil.getValueAsString("Mandate_BarCode_Control"))) {
		//
		// //barCode validation
		// if (StringUtils.isBlank(refundUpload.getBarCodeNumber())) {
		// String[] valueParm = new String[1];
		// valueParm[0] = "barCode";
		// return getErrorDetails("90502", valueParm);
		// }
		//
		// List<ErrorDetail> errors = mandateService.doValidations(refundUpload);
		//
		// if (errors != null && !errors.isEmpty()) {
		// for (ErrorDetail errorDetails : errors) {
		// if (StringUtils.isNotBlank(errorDetails.getCode())) {
		// returnStatus = (APIErrorHandlerService.getFailedStatus(errorDetails.getCode(),
		// errorDetails.getError()));
		// return returnStatus;
		// }
		// }
		// }
		// }
		// }

		logger.debug("Leaving");
		return returnStatus;
	}

	@Autowired
	public void setValidationUtility(ValidationUtility validationUtility) {
		this.validationUtility = validationUtility;
	}

	@Autowired
	public void setRefundUploadController(RefundUploadController refundUploadController) {
		this.refundUploadController = refundUploadController;
	}
}
