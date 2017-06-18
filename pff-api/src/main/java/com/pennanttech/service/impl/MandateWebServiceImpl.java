package com.pennanttech.service.impl;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pennant.app.util.DateUtility;
import com.pennant.app.util.FrequencyUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.WSReturnStatus;
import com.pennant.backend.model.bmtmasters.BankBranch;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.mandate.Mandate;
import com.pennant.backend.service.applicationmaster.BankDetailService;
import com.pennant.backend.service.bmtmasters.BankBranchService;
import com.pennant.backend.service.customermasters.CustomerDetailsService;
import com.pennant.backend.service.finance.FinanceMainService;
import com.pennant.backend.service.mandate.MandateService;
import com.pennant.backend.util.MandateConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.validation.SaveValidationGroup;
import com.pennant.validation.UpdateValidationGroup;
import com.pennant.validation.ValidationUtility;
import com.pennant.ws.exception.ServiceException;
import com.pennanttech.controller.MandateController;
import com.pennanttech.pffws.MandateRestService;
import com.pennanttech.pffws.MandateSoapService;
import com.pennanttech.util.APIConstants;
import com.pennanttech.ws.model.mandate.MandateDetial;
import com.pennanttech.ws.service.APIErrorHandlerService;

@Service
public class MandateWebServiceImpl implements MandateRestService,MandateSoapService {
	private static final Logger logger = Logger.getLogger(MandateWebServiceImpl.class);

	private ValidationUtility validationUtility;
	private MandateController mandateController;
	private CustomerDetailsService customerDetailsService;
	private BankBranchService bankBranchService;
	private MandateService mandateService;
	private FinanceMainService financeMainService;
	private BankDetailService bankDetailService;

	/**
	 * Method for create Mandate in PLF system.
	 * 
	 * @param mandate
	 * @throws ServiceException
	 */
	@Override
	public Mandate createMandate(Mandate mandate) throws ServiceException {
		logger.debug("Entering");

		// bean validations
		validationUtility.validate(mandate, SaveValidationGroup.class);
		WSReturnStatus returnStatus = doMandateValidation(mandate);

		Mandate response = null;
		if (StringUtils.isBlank(returnStatus.getReturnCode())) {
			response = mandateController.createMandate(mandate);
		} else {
			response = new Mandate();
			response.setReturnStatus(returnStatus);
		}

		logger.debug("Leaving");
		return response;
	}

	/**
	 * get the mandate Details by the given mandate Id.
	 * 
	 * @param mandateID
	 * @return MandateDetails
	 * @throws ServiceException
	 */
	@Override
	public Mandate getMandate(long mandateID) throws ServiceException {
		logger.debug("Entering");
		// Mandatory validation
		if (mandateID < 0) {
			validationUtility.fieldLevelException();
		}
		Mandate response = mandateController.getMandate(mandateID);
		logger.debug("Leaving");
		return response;
	}

	/**
	 * Method for update Mandate in PLF system.
	 * 
	 * @param mandate
	 * @throws ServiceException
	 */

	@Override
	public WSReturnStatus updateMandate(Mandate mandate) throws ServiceException {
		logger.debug("Entering");
		// beanValidation
		validationUtility.validate(mandate, UpdateValidationGroup.class);
		Mandate mandateDetails = mandateService.getApprovedMandateById(mandate.getMandateID());
		WSReturnStatus returnStatus = null;
		if (mandateDetails != null) {
			returnStatus = doMandateValidation(mandate);
			if (StringUtils.isBlank(returnStatus.getReturnCode())) {
				returnStatus = mandateController.updateMandate(mandate);
			} else {
				return returnStatus;
			}
		} else {
			returnStatus = new WSReturnStatus();
			String[] valueParm = new String[1];
			valueParm[0] = String.valueOf(mandate.getMandateID());
			returnStatus = APIErrorHandlerService.getFailedStatus("90303", valueParm);
		}
		logger.debug("Leaving");
		return returnStatus;
	}

	/**
	 * delete the mandate by the given mandate Id.
	 * 
	 * @param mandateID
	 * @return WSReturnStatus
	 * @throws ServiceException
	 */

	@Override
	public WSReturnStatus deleteMandate(long mandateID) throws ServiceException {
		logger.debug("Entering");
		// Mandatory validation
		if (mandateID < 0) {
			validationUtility.fieldLevelException();
		}
		// Mandate Id is Available or not in PLF
		WSReturnStatus response = new WSReturnStatus();
		Mandate mandate = mandateService.getApprovedMandateById(mandateID);
		if (mandate != null) {
			response = mandateController.deleteMandate(mandateID);
		} else {

			String[] valueParm = new String[1];
			valueParm[0] = String.valueOf(mandateID);
			response = APIErrorHandlerService.getFailedStatus("90303", valueParm);
		}
		logger.debug("Leaving");
		return response;
	}

	/**
	 * get the mandate Details by the given cif.
	 * 
	 * @param cif
	 * @return MandatesDetails
	 * @throws ServiceException
	 */
	@Override
	public MandateDetial getMandates(String cif) throws ServiceException {
		logger.debug("Entering");
		// Mandatory validation
		if (StringUtils.isBlank(cif)) {
			validationUtility.fieldLevelException();
		}
		MandateDetial response = new MandateDetial();
		// validation
		Customer customer = customerDetailsService.getCustomerByCIF(cif);
		if (customer == null) {
			String[] valueParm = new String[1];
			valueParm[0] = cif;
			response.setReturnStatus(getErrorDetails("90101", valueParm));
		} else {
			response = mandateController.getMandates(cif);
		}
		logger.debug("Leaving");

		return response;
	}

	/**
	 * loanMandateSwapping the mandate Details.
	 * 
	 * @param mandate
	 * @return WSReturnStatus
	 * @throws ServiceException
	 */

	@Override
	public WSReturnStatus loanMandateSwapping(MandateDetial mandate) throws ServiceException {
		logger.debug("Entering");
		// beanValidation
		validationUtility.validate(mandate, SaveValidationGroup.class);
		// validate customer details as per the API specification

		WSReturnStatus response = doValidation(mandate);
		if (response == null) {
			return response = mandateController.loanMandateSwapping(mandate);
		} else {
			return response;
		}

	}

	/**
	 * Validate the mandatory fields in the request object
	 * 
	 * @param mandate
	 * @return returnStatus
	 */
	private WSReturnStatus doValidation(MandateDetial mandateDetail) {
		logger.debug("Entering");

		WSReturnStatus returnStatus = null;
		// validate newMandateId
		Mandate mandate = mandateService.getApprovedMandateById(mandateDetail.getNewMandateId());
		if (mandate == null) {

			String[] valueParm = new String[1];
			valueParm[0] = String.valueOf(mandateDetail.getNewMandateId());
			returnStatus = APIErrorHandlerService.getFailedStatus("90303", valueParm);

			return returnStatus;
		}
		// validations for MandateRef

		if (StringUtils.isBlank(mandate.getMandateRef())) {

			String[] valueParm = new String[1];
			valueParm[0] = String.valueOf(mandateDetail.getNewMandateId());
			returnStatus = APIErrorHandlerService.getFailedStatus("90305", valueParm);

			return returnStatus;
		}
		// validations for Status

		if (!StringUtils.equals(MandateConstants.STATUS_APPROVED, mandate.getStatus())) {

			String[] valueParm = new String[1];
			valueParm[0] = String.valueOf(mandateDetail.getNewMandateId());
			returnStatus = APIErrorHandlerService.getFailedStatus("90306", valueParm);

			return returnStatus;
		}
		// validate FinanceReference
		int count = financeMainService.getFinanceCountById(mandateDetail.getFinReference(),
				mandateDetail.getOldMandateId());
		if (count <= 0) {
			String[] valueParm = new String[1];
			valueParm[0] = mandateDetail.getFinReference();
			return getErrorDetails("90201", valueParm);
		}

		logger.debug("Leaving");
		return returnStatus;
	}

	/**
	 * Validate the mandatory fields in the request object
	 * 
	 * @param mandate
	 * @return returnStatus
	 */
	private WSReturnStatus doMandateValidation(Mandate mandate) {
		logger.debug("Entering");

		WSReturnStatus returnStatus = new WSReturnStatus();
		// validate customer
		String custCIF = mandate.getCustCIF();
		if (StringUtils.isNotBlank(custCIF)) {
			Customer customer = customerDetailsService.getCustomerByCIF(custCIF);
			if (customer == null) {
				String[] valueParm = new String[1];
				valueParm[0] = custCIF;
				return getErrorDetails("90101", valueParm);
			}
		}

		// validate MandateType
		if (StringUtils.isNotBlank(mandate.getMandateType())) {
			List<ValueLabel> mandateType = PennantStaticListUtil.getMandateTypeList();
			boolean mandateTypeSts = false;
			for (ValueLabel value : mandateType) {
				if (StringUtils.equals(value.getValue(), mandate.getMandateType())) {
					mandateTypeSts = true;
					break;
				}
			}
			if (!mandateTypeSts) {
				String[] valueParm = new String[1];
				valueParm[0] = mandate.getMandateType();
				return getErrorDetails("90307", valueParm);
			}
		}

		// validate AccType
		if (StringUtils.isNotBlank(mandate.getAccType())) {
			List<ValueLabel> accType = PennantStaticListUtil.getAccTypeList();
			boolean accTypeSts = false;
			for (ValueLabel value : accType) {
				if (StringUtils.equals(value.getValue(), mandate.getAccType())) {
					accTypeSts = true;
					break;
				}
			}
			if (!accTypeSts) {
				String[] valueParm = new String[1];
				valueParm[0] = mandate.getAccType();
				return getErrorDetails("90308", valueParm);
			}
		}
		//validate Phone number
		String mobileNumber = mandate.getPhoneNumber();
		if (StringUtils.isNotBlank(mobileNumber)) {
			if (!(mobileNumber.matches("\\d{10}"))) {
				return getErrorDetails("90278", null);
			}
		}
		// validate status
		if (StringUtils.isNotBlank(mandate.getStatus())) {
			List<ValueLabel> status = PennantStaticListUtil.getStatusTypeList();
			boolean sts = false;
			for (ValueLabel value : status) {
				if (StringUtils.equals(value.getValue(), mandate.getStatus())) {
					sts = true;
					break;
				}
			}
			if (!sts) {
				String[] valueParm = new String[1];
				valueParm[0] = mandate.getStatus();
				return getErrorDetails("90309", valueParm);
			}
		}

		// validate periodicity
		if (StringUtils.isNotBlank(mandate.getPeriodicity())) {
			ErrorDetails errorDetail = FrequencyUtil.validateFrequency(mandate.getPeriodicity());
			if (errorDetail != null && StringUtils.isNotBlank(errorDetail.getErrorCode())) {
				String[] valueParm = new String[1];
				valueParm[0] = mandate.getPeriodicity();
				return getErrorDetails("90207", valueParm);
			}
		} 
		boolean isValidBranch=true;
		// validate Mandate fields
		if (StringUtils.isNotBlank(mandate.getIFSC())) {
			BankBranch bankBranch = bankBranchService.getBankBrachByIFSC(mandate.getIFSC());
			if (bankBranch == null) {
				String[] valueParm = new String[1];
				valueParm[0] = mandate.getIFSC();
				return getErrorDetails("90301", valueParm);
			} else{
				isValidBranch = validateBranchCode(mandate, isValidBranch, bankBranch);
				 if(returnStatus.getReturnCode()!=null){
					 return returnStatus;
				 }
				mandate.setBankCode(bankBranch.getBankCode());
				if(StringUtils.isBlank(mandate.getMICR())){
					mandate.setMICR(bankBranch.getMICR());
				} else {
					if(!StringUtils.equals(bankBranch.getMICR(), mandate.getMICR())){
						String[] valueParm = new String[2];
						valueParm[0] = "MICR";
						valueParm[1] = mandate.getMICR();
						return getErrorDetails("90701", valueParm);
					}
				}
			}
		} else if (StringUtils.isNotBlank(mandate.getBankCode()) && StringUtils.isNotBlank(mandate.getBranchCode())) {
			BankBranch bankBranch = bankBranchService
					.getBankBrachByCode(mandate.getBankCode(), mandate.getBranchCode());
			if (bankBranch == null) {
				String[] valueParm = new String[2];
				valueParm[0] = mandate.getBankCode();
				valueParm[1] = mandate.getBranchCode();
				return getErrorDetails("90302", valueParm);
			} else {
				isValidBranch = validateBranchCode(mandate, isValidBranch, bankBranch);
				 if(returnStatus.getReturnCode()!=null){
					 return returnStatus;
				 }
				mandate.setBankCode(bankBranch.getBankCode());
				if(StringUtils.isBlank(mandate.getMICR())){
					mandate.setMICR(bankBranch.getMICR());
				} else {
					if(!StringUtils.equals(bankBranch.getMICR(), mandate.getMICR())){
						String[] valueParm = new String[2];
						valueParm[0] = "MICR";
						valueParm[1] = mandate.getMICR();
						return getErrorDetails("90701", valueParm);
					}
				}
			}
		}
		if(!isValidBranch){
			String[] valueParm = new String[1];
			valueParm[0] = mandate.getMandateType();
			 return getErrorDetails("90333", valueParm);
		}
		//validate AccNumber length
		if(StringUtils.isNotBlank(mandate.getBankCode())){
			int accNoLength = bankDetailService.getAccNoLengthByCode(mandate.getBankCode());
			if(mandate.getAccNumber().length()!=accNoLength){
				String[] valueParm = new String[2];
				valueParm[0] = "AccountNumber";
				valueParm[1] = String.valueOf(accNoLength)+" characters";
				return getErrorDetails("30570", valueParm);
			}
		}
		//validate Dates
		if(mandate.getStartDate().compareTo(mandate.getExpiryDate())>0) {
			String[] valueParm = new String[2];
			valueParm[0] = DateUtility.formatDate(mandate.getExpiryDate(), PennantConstants.XMLDateFormat);
			valueParm[1] = DateUtility.formatDate(mandate.getStartDate(), PennantConstants.XMLDateFormat);
			return getErrorDetails("90205", valueParm);
		}
		
		if(mandate.getStartDate() != null){
			Date mandbackDate = DateUtility.addDays(DateUtility.getAppDate(),-SysParamUtil.getValueAsInt("MANDATE_STARTDATE"));
			if (mandate.getStartDate().before(mandbackDate)
					|| mandate.getStartDate().after(SysParamUtil.getValueAsDate("APP_DFT_END_DATE"))) {
				String[] valueParm = new String[3];
				valueParm[0] = "mandate start date";
				valueParm[1] = DateUtility.formatToLongDate(mandbackDate);
				valueParm[2] = DateUtility.formatToLongDate(SysParamUtil.getValueAsDate("APP_DFT_END_DATE"));
				return getErrorDetails("90318", valueParm);
			}	
		}
		logger.debug("Leaving");
		
		
		
		return returnStatus;
	}

	private boolean validateBranchCode(Mandate mandate, boolean isValidBranch, BankBranch bankBranch) {
		if(StringUtils.equals(MandateConstants.TYPE_ECS, mandate.getMandateType())){
			if(!bankBranch.isEcs()){
				isValidBranch = false;
			}
		} else if(StringUtils.equals(MandateConstants.TYPE_DDM, mandate.getMandateType())){
			if(!bankBranch.isDda()){
				isValidBranch = false;
			}
		}else if(StringUtils.equals(MandateConstants.TYPE_NACH, mandate.getMandateType())){
			if(!bankBranch.isNach()){
				isValidBranch = false;
			}
		}
		return isValidBranch;
	}

	/**
	 * Method for prepare response object with errorDetails.
	 * 
	 * @param errorCode
	 * @param valueParm
	 * @return
	 */
	public WSReturnStatus getErrorDetails(String errorCode, String[] valueParm) {
		logger.debug("Entering");

		WSReturnStatus response = new WSReturnStatus();
		response = APIErrorHandlerService.getFailedStatus(errorCode, valueParm);

		// set default error code and description in case of Error code does not exists.
		if (StringUtils.isBlank(response.getReturnCode())) {
			response = APIErrorHandlerService.getFailedStatus(APIConstants.RES_FAILED_CODE,
					APIConstants.RES_FAILED_DESC);
		}

		logger.debug("Leaving");
		return response;
	}

	@Autowired
	public void setValidationUtility(ValidationUtility validationUtility) {
		this.validationUtility = validationUtility;
	}

	@Autowired
	public void setMandateController(MandateController mandateController) {
		this.mandateController = mandateController;
	}

	@Autowired
	public void setCustomerDetailsService(CustomerDetailsService customerDetailsService) {
		this.customerDetailsService = customerDetailsService;
	}

	@Autowired
	public void setBankBranchService(BankBranchService bankBranchService) {
		this.bankBranchService = bankBranchService;
	}

	@Autowired
	public void setMandateService(MandateService mandateService) {
		this.mandateService = mandateService;
	}

	@Autowired
	public void setFinanceMainService(FinanceMainService financeMainService) {
		this.financeMainService = financeMainService;
	}
	@Autowired
	public void setBankDetailService(BankDetailService bankDetailService) {
		this.bankDetailService = bankDetailService;
	}

}
