package com.pennanttech.service.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pennant.app.constants.ImplementationConstants;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.FrequencyUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.dao.finance.FinanceScheduleDetailDAO;
import com.pennant.backend.dao.partnerbank.PartnerBankDAO;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.WSReturnStatus;
import com.pennant.backend.model.applicationmaster.BankDetail;
import com.pennant.backend.model.applicationmaster.Entity;
import com.pennant.backend.model.bmtmasters.BankBranch;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.mandate.Mandate;
import com.pennant.backend.model.partnerbank.PartnerBank;
import com.pennant.backend.service.applicationmaster.BankDetailService;
import com.pennant.backend.service.applicationmaster.EntityService;
import com.pennant.backend.service.bmtmasters.BankBranchService;
import com.pennant.backend.service.customermasters.CustomerDetailsService;
import com.pennant.backend.service.finance.FinanceMainService;
import com.pennant.backend.service.mandate.MandateService;
import com.pennant.backend.service.rmtmasters.FinanceTypeService;
import com.pennant.backend.util.MandateConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.backend.util.SMTParameterConstants;
import com.pennant.validation.SaveValidationGroup;
import com.pennant.validation.UpdateValidationGroup;
import com.pennant.validation.ValidationUtility;
import com.pennant.ws.exception.ServiceException;
import com.pennanttech.controller.ExtendedTestClass;
import com.pennanttech.controller.MandateController;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pffws.MandateRestService;
import com.pennanttech.pffws.MandateSoapService;
import com.pennanttech.util.APIConstants;
import com.pennanttech.ws.model.mandate.MandateDetial;
import com.pennanttech.ws.service.APIErrorHandlerService;

@Service
public class MandateWebServiceImpl extends ExtendedTestClass implements MandateRestService, MandateSoapService {
	private static final Logger logger = LogManager.getLogger(MandateWebServiceImpl.class);

	private ValidationUtility validationUtility;
	private MandateController mandateController;
	private CustomerDetailsService customerDetailsService;
	private BankBranchService bankBranchService;
	private MandateService mandateService;
	private BankDetailService bankDetailService;
	private FinanceMainService financeMainService;
	private FinanceScheduleDetailDAO financeScheduleDetailDAO;
	private EntityService entityService;
	private FinanceTypeService financeTypeService;
	private PartnerBankDAO partnerBankDAO;
	private FinanceMainDAO financeMainDAO;

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
		Mandate response = null;

		WSReturnStatus returnStatus = doMandateValidation(mandate);
		if (StringUtils.isNotBlank(mandate.getMandateRef())) {
			if (!MandateConstants.TYPE_EMANDATE.equals(mandate.getMandateType())) {
				response = new Mandate();
				String[] paramValue = new String[2];
				paramValue[0] = "mandateRef";
				paramValue[1] = "createMandate";
				response.setReturnStatus(APIErrorHandlerService.getFailedStatus("90329", paramValue));
				return response;
			}
		}
		// for logging purpose
		String[] logFields = new String[3];
		logFields[0] = mandate.getCustCIF();
		logFields[1] = mandate.getAccNumber();
		logFields[2] = mandate.getAccHolderName();
		APIErrorHandlerService.logKeyFields(logFields);

		if (StringUtils.isBlank(returnStatus.getReturnCode())) {
			response = mandateController.createMandate(mandate);
		} else {
			response = new Mandate();
			response.setReturnStatus(returnStatus);
		}
		// for logging purpose
		if (response.getMandateID() != Long.MIN_VALUE) {
			APIErrorHandlerService.logReference(String.valueOf(response.getMandateID()));
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
		// for logging purpose
		APIErrorHandlerService.logReference(String.valueOf(mandateID));
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

		// for logging purpose
		String[] logFields = new String[3];
		logFields[0] = mandate.getCustCIF();
		logFields[1] = mandate.getAccNumber();
		logFields[2] = mandate.getAccHolderName();
		APIErrorHandlerService.logKeyFields(logFields);

		Mandate mandateDetails = mandateService.getApprovedMandateById(mandate.getMandateID());
		WSReturnStatus returnStatus = null;
		if (mandateDetails != null) {
			// for logging purpose
			APIErrorHandlerService.logReference(String.valueOf(mandate.getMandateID()));

			returnStatus = doMandateValidation(mandate);
			if (StringUtils.isNotBlank(mandate.getMandateRef())) {
				String[] paramValue = new String[2];
				paramValue[0] = "mandateRef";
				paramValue[1] = "updateMandate";
				returnStatus = APIErrorHandlerService.getFailedStatus("90329", paramValue);
				return returnStatus;
			}
			if (StringUtils.isBlank(returnStatus.getReturnCode())) {
				if (StringUtils.equals(MandateConstants.STATUS_AWAITCON, mandateDetails.getStatus())) {
					String[] valueParm = new String[1];
					valueParm[0] = "Awaiting Confirmation";
					returnStatus = APIErrorHandlerService.getFailedStatus("90345", valueParm);
					return returnStatus;
				}
				if (StringUtils.equals(MandateConstants.STATUS_HOLD, mandateDetails.getStatus())) {
					String[] valueParm = new String[1];
					valueParm[0] = "Hold";
					returnStatus = APIErrorHandlerService.getFailedStatus("90345", valueParm);
					return returnStatus;
				}
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
		// for logging purpose
		APIErrorHandlerService.logReference(String.valueOf(mandateID));

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
		// for logging purpose
		APIErrorHandlerService.logReference(cif);

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

		// validate customer details as per the API specification
		WSReturnStatus response = doValidation(mandate);
		// for logging purpose
		APIErrorHandlerService.logReference(mandate.getFinReference());
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
	private WSReturnStatus doValidation(MandateDetial md) {
		logger.debug("Entering");

		WSReturnStatus returnStatus = null;

		String finReference = md.getFinReference();
		Long oldMandateId = md.getOldMandateId();
		Long newMandateId = md.getNewMandateId();
		Date appDate = SysParamUtil.getAppDate();

		if (finReference == null) {
			String[] valueParm = new String[1];
			valueParm[0] = "FinReference";
			returnStatus = APIErrorHandlerService.getFailedStatus("90502", valueParm);
			return returnStatus;
		}

		if (oldMandateId == null || oldMandateId <= 0) {
			String[] valueParm = new String[1];
			valueParm[0] = "OldMandateId";
			returnStatus = APIErrorHandlerService.getFailedStatus("90502", valueParm);
			return returnStatus;
		}

		if (newMandateId == null || newMandateId <= 0) {
			String[] valueParm = new String[1];
			valueParm[0] = "NewMandateId";
			returnStatus = APIErrorHandlerService.getFailedStatus("90502", valueParm);
			return returnStatus;
		}

		// validate oldMandate
		Mandate oldMandate = mandateService.getApprovedMandateById(oldMandateId);
		if (oldMandate == null) {
			String[] valueParm = new String[1];
			valueParm[0] = String.valueOf(oldMandateId);
			returnStatus = APIErrorHandlerService.getFailedStatus("90303", valueParm);
			return returnStatus;
		}
		// validate FinanceReference
		Long finID = financeMainDAO.getFinIDForMandate(finReference, oldMandateId);
		if (finID == null) {
			String[] valueParm = new String[1];
			valueParm[0] = finReference;
			return getErrorDetails("90201", valueParm);
		}
		// validate newMandateId
		Mandate newMandate = mandateService.getApprovedMandateById(newMandateId);
		if (newMandate == null) {
			String[] valueParm = new String[1];
			valueParm[0] = String.valueOf(newMandateId);
			returnStatus = APIErrorHandlerService.getFailedStatus("90303", valueParm);
			return returnStatus;
		}

		List<FinanceScheduleDetail> schdeules = financeScheduleDetailDAO.getFinScheduleDetails(finID, "", false);
		BigDecimal repayAmt = BigDecimal.ZERO;

		for (FinanceScheduleDetail curSchd : schdeules) {
			if (DateUtility.compare(curSchd.getSchDate(), appDate) >= 0 && curSchd.isRepayOnSchDate()) {
				repayAmt = curSchd.getProfitSchd().add(curSchd.getPrincipalSchd()).add(curSchd.getFeeSchd());
				break;
			}
		}
		BigDecimal maxLimit = newMandate.getMaxLimit();
		if (repayAmt.compareTo(maxLimit) > 0) {
			returnStatus = APIErrorHandlerService.getFailedStatus("90320");
			return returnStatus;
		}
		// validate cif
		if (!StringUtils.equals(oldMandate.getCustCIF(), newMandate.getCustCIF())) {
			returnStatus = APIErrorHandlerService.getFailedStatus("90342");
			return returnStatus;
		}
		// validations for MandateRef
		if (!MandateConstants.skipRegistration().contains(newMandate.getMandateType()))
			if (StringUtils.isBlank(newMandate.getMandateRef())) {
				String[] valueParm = new String[1];
				valueParm[0] = String.valueOf(newMandateId);
				returnStatus = APIErrorHandlerService.getFailedStatus("90305", valueParm);
				return returnStatus;
			}
		// validations for Status
		if (StringUtils.equals(MandateConstants.STATUS_REJECTED, newMandate.getStatus())) {
			String[] valueParm = new String[1];
			valueParm[0] = String.valueOf(newMandateId);
			returnStatus = APIErrorHandlerService.getFailedStatus("90306", valueParm);
			return returnStatus;
		}
		// openMandate
		if (!newMandate.isOpenMandate()) {
			if (StringUtils.isNotBlank(newMandate.getOrgReference())) {
				String[] valueParm = new String[1];
				valueParm[0] = String.valueOf(newMandateId);
				returnStatus = APIErrorHandlerService.getFailedStatus("90312", valueParm);
				return returnStatus;
			}
		}

		// set mandate type
		md.setMandateType(newMandate.getMandateType());

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
		Customer customer = null;
		if (StringUtils.isNotBlank(custCIF)) {
			customer = customerDetailsService.getCustomerByCIF(custCIF);
			if (customer == null) {
				String[] valueParm = new String[1];
				valueParm[0] = custCIF;
				return getErrorDetails("90101", valueParm);
			}
		}

		// validate the entityCode
		if (StringUtils.isBlank(mandate.getEntityCode())) {
			String[] valueParm = new String[1];
			valueParm[0] = "Entity";
			return getErrorDetails("90502", valueParm);
		}
		Entity entity = entityService.getApprovedEntity(mandate.getEntityCode());
		if (entity == null || !entity.isActive()) {
			String[] valueParm = new String[2];
			valueParm[0] = "Entity";
			valueParm[1] = mandate.getEntityCode();
			return getErrorDetails("90701", valueParm);

		}

		// validate finance reference
		/*
		 * if(StringUtils.isBlank(mandate.getOrgReference())){ String[] valueParm = new String[1]; valueParm[0] =
		 * "finReference"; return getErrorDetails("90502", valueParm); }
		 */

		Long finID = null;
		boolean alwmandate = ImplementationConstants.ALW_APPROVED_MANDATE_IN_ORG;
		if (StringUtils.isNotBlank(mandate.getOrgReference())) {
			if (!alwmandate) {
				finID = financeMainDAO.getActiveFinID(mandate.getOrgReference(), TableType.MAIN_TAB);
				if (finID == null) {
					String[] valueParm = new String[1];
					valueParm[0] = mandate.getOrgReference();
					return getErrorDetails("90201", valueParm);
				}
			} else {
				finID = financeMainDAO.getFinID(mandate.getOrgReference(), TableType.TEMP_TAB);
				if (finID == null) {
					String[] valueParm = new String[1];
					valueParm[0] = mandate.getOrgReference();
					return getErrorDetails("90201", valueParm);
				}
			}
			String type = "";
			if (alwmandate) {
				type = "_View";
			}
			List<Long> finRefList = financeMainService.getFinanceMainbyCustId(customer.getCustID(), type);
			boolean validFinrefernce = true;
			for (Long id : finRefList) {
				if (id == finID) {
					validFinrefernce = false;
					break;
				}
			}
			if (validFinrefernce) {
				returnStatus = new WSReturnStatus();
				String[] valueParm = new String[2];
				valueParm[0] = mandate.getCustCIF();
				valueParm[1] = mandate.getOrgReference();
				return APIErrorHandlerService.getFailedStatus("90406", valueParm);
			}

			List<FinanceScheduleDetail> schedules = financeScheduleDetailDAO.getFinScheduleDetails(finID, type, false);
			BigDecimal repayAmt = BigDecimal.ZERO;

			Date appDate = SysParamUtil.getAppDate();
			for (FinanceScheduleDetail curSchd : schedules) {
				if (DateUtility.compare(curSchd.getSchDate(), appDate) >= 0 && curSchd.isRepayOnSchDate()) {
					repayAmt = curSchd.getProfitSchd().add(curSchd.getPrincipalSchd()).add(curSchd.getFeeSchd());
					break;
				}
			}
			if (repayAmt.compareTo(mandate.getMaxLimit()) > 0) {
				String[] valueParm = new String[1];
				returnStatus = APIErrorHandlerService.getFailedStatus("90320");
				return getErrorDetails("90320", valueParm);
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
		// validate Phone number
		String mobileNumber = mandate.getPhoneNumber();
		if (StringUtils.isNotBlank(mobileNumber)) {
			if (!(mobileNumber.matches("\\d{10}"))) {
				return getErrorDetails("90278", null);
			}
		}
		// validate status
		if (StringUtils.isNotBlank(mandate.getStatus())) {
			List<ValueLabel> status = PennantStaticListUtil
					.getStatusTypeList(SysParamUtil.getValueAsString(MandateConstants.MANDATE_CUSTOM_STATUS));
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
			ErrorDetail errorDetail = FrequencyUtil.validateFrequency(mandate.getPeriodicity());
			if (errorDetail != null && StringUtils.isNotBlank(errorDetail.getCode())) {
				String[] valueParm = new String[1];
				valueParm[0] = mandate.getPeriodicity();
				return getErrorDetails("90207", valueParm);
			}
		}
		boolean isValidBranch = true;
		// validate Mandate fields
		if (StringUtils.isNotBlank(mandate.getIFSC())) {
			BankBranch bankBranch = bankBranchService.getBankBrachByIFSC(mandate.getIFSC());
			if (bankBranch == null) {
				String[] valueParm = new String[1];
				valueParm[0] = mandate.getIFSC();
				return getErrorDetails("90301", valueParm);
			} else {
				isValidBranch = validateBranchCode(mandate, isValidBranch, bankBranch);
				if (returnStatus.getReturnCode() != null) {
					return returnStatus;
				}
				if (StringUtils.equals(mandate.getMandateType(), MandateConstants.TYPE_EMANDATE)
						&& !bankBranch.isEmandate()) {
					String[] valueParm = new String[2];
					valueParm[0] = "EMANDATE";
					valueParm[1] = "bankCode " + mandate.getBankCode();
					return getErrorDetails("API002", valueParm);
				}
				mandate.setBankCode(bankBranch.getBankCode());
				if (StringUtils.isBlank(mandate.getMICR())) {
					mandate.setMICR(bankBranch.getMICR());
				} else {
					if (!StringUtils.equals(bankBranch.getMICR(), mandate.getMICR())) {
						String[] valueParm = new String[2];
						valueParm[0] = "MICR";
						valueParm[1] = mandate.getMICR();
						return getErrorDetails("90701", valueParm);
					}
				}
			}
		} else if (StringUtils.isNotBlank(mandate.getBankCode()) && StringUtils.isNotBlank(mandate.getBranchCode())) {
			BankBranch bankBranch = bankBranchService.getBankBrachByCode(mandate.getBankCode(),
					mandate.getBranchCode());
			if (bankBranch == null) {
				String[] valueParm = new String[2];
				valueParm[0] = mandate.getBankCode();
				valueParm[1] = mandate.getBranchCode();
				return getErrorDetails("90302", valueParm);
			} else {
				isValidBranch = validateBranchCode(mandate, isValidBranch, bankBranch);
				if (returnStatus.getReturnCode() != null) {
					return returnStatus;
				}
				if (StringUtils.equals(mandate.getMandateType(), MandateConstants.TYPE_EMANDATE)
						&& !bankBranch.isEmandate()) {
					String[] valueParm = new String[2];
					valueParm[0] = "EMANDATE";
					valueParm[1] = "bankCode " + mandate.getBankCode();
					return getErrorDetails("API002", valueParm);
				}
				mandate.setBankCode(bankBranch.getBankCode());
				if (StringUtils.isBlank(mandate.getMICR())) {
					mandate.setMICR(bankBranch.getMICR());
				} else {
					if (!StringUtils.equals(bankBranch.getMICR(), mandate.getMICR())) {
						String[] valueParm = new String[2];
						valueParm[0] = "MICR";
						valueParm[1] = mandate.getMICR();
						return getErrorDetails("90701", valueParm);
					}
				}
			}
		}
		if (!isValidBranch) {
			String[] valueParm = new String[1];
			valueParm[0] = mandate.getMandateType();
			return getErrorDetails("90333", valueParm);
		}
		// validate AccNumber length
		if (StringUtils.isNotBlank(mandate.getBankCode()) && StringUtils.isNotBlank(mandate.getAccNumber())) {
			BankDetail bankDetails = bankDetailService.getAccNoLengthByCode(mandate.getBankCode());
			int length = mandate.getAccNumber().length();
			if (bankDetails != null) {
				int maxAccNoLength = bankDetails.getAccNoLength();
				int minAccNolength = bankDetails.getMinAccNoLength();
				if (length < minAccNolength || length > maxAccNoLength) {
					String[] valueParm = new String[3];
					valueParm[0] = "AccountNumber";
					valueParm[1] = String.valueOf(minAccNolength) + " characters";
					valueParm[2] = String.valueOf(maxAccNoLength) + " characters";
					return getErrorDetails("BNK001", valueParm);
				}
			}
		}

		if (!mandate.isOpenMandate()) {
			if (mandate.getExpiryDate() == null) {
				String[] valueParm = new String[1];
				valueParm[0] = "expiryDate";
				return getErrorDetails("90502", valueParm);
			}
		}
		// validate Dates
		if (mandate.getExpiryDate() != null) {
			if (mandate.getExpiryDate().compareTo(mandate.getStartDate()) <= 0
					|| mandate.getExpiryDate().after(SysParamUtil.getValueAsDate("APP_DFT_END_DATE"))) {
				String[] valueParm = new String[3];
				valueParm[0] = "ExpiryDate";
				valueParm[1] = DateUtility.formatToLongDate(DateUtility.addDays(mandate.getStartDate(), 1));
				valueParm[2] = DateUtility.formatToLongDate(SysParamUtil.getValueAsDate("APP_DFT_END_DATE"));
				return getErrorDetails("90318", valueParm);
			}
		}
		if (mandate.getStartDate() != null) {
			Date appDate = SysParamUtil.getAppDate();
			Date mandbackDate = DateUtility.addDays(appDate, -SysParamUtil.getValueAsInt("MANDATE_STARTDATE"));
			if (mandate.getStartDate().before(mandbackDate)
					|| mandate.getStartDate().after(SysParamUtil.getValueAsDate("APP_DFT_END_DATE"))) {
				String[] valueParm = new String[3];
				valueParm[0] = "mandate start date";
				valueParm[1] = DateUtility.formatToLongDate(mandbackDate);
				valueParm[2] = DateUtility.formatToLongDate(SysParamUtil.getValueAsDate("APP_DFT_END_DATE"));
				return getErrorDetails("90318", valueParm);
			}
		}
		// barCode validation
		if (ImplementationConstants.ALLOW_BARCODE) {
			if (StringUtils.isBlank(mandate.getBarCodeNumber())) {
				String[] valueParm = new String[1];
				valueParm[0] = "barCode";
				return getErrorDetails("90502", valueParm);
			}
		}
		List<ErrorDetail> errors = mandateService.doValidations(mandate);
		if (errors != null && !errors.isEmpty()) {
			for (ErrorDetail errorDetails : errors) {
				if (StringUtils.isNotBlank(errorDetails.getCode())) {
					returnStatus = (APIErrorHandlerService.getFailedStatus(errorDetails.getCode(),
							errorDetails.getError()));
					return returnStatus;
				}
			}
		}
		if (mandate.getPartnerBankId() <= 0 && StringUtils.isNotBlank(mandate.getPartnerBankCode())) {
			PartnerBank partnerBank = partnerBankDAO.getPartnerBankByCode(mandate.getPartnerBankCode(), "");
			if (partnerBank == null) {
				String[] valueParm1 = new String[2];
				valueParm1[0] = PennantJavaUtil.getLabel("label_MandateDialog_PartnerBank.value");
				valueParm1[1] = mandate.getPartnerBankCode();
				return getErrorDetails("90224", valueParm1);
			} else {
				mandate.setPartnerBankId(partnerBank.getPartnerBankId());
			}
		}

		if (StringUtils.equals(mandate.getMandateType(), MandateConstants.TYPE_EMANDATE)) {
			if (StringUtils.isBlank(mandate.geteMandateReferenceNo())) {
				String[] valueParm1 = new String[1];
				valueParm1[0] = "eMandateReferenceNo";
				return getErrorDetails("90502", valueParm1);
			}
			if (StringUtils.isBlank(mandate.geteMandateSource())) {
				String[] valueParm1 = new String[1];
				valueParm1[0] = "eMandateSource";
				return getErrorDetails("90502", valueParm1);
			} else {
				int count = mandateService.validateEmandateSource(mandate.geteMandateSource());
				if (count == 0) {
					String[] valueParm1 = new String[1];
					valueParm1[0] = "eMandateSource " + mandate.geteMandateSource();
					return getErrorDetails("90501", valueParm1);
				}
			}
		}
		logger.debug("Leaving");

		return returnStatus;
	}

	private boolean validateBranchCode(Mandate mandate, boolean isValidBranch, BankBranch bankBranch) {
		if (StringUtils.equals(MandateConstants.TYPE_ECS, mandate.getMandateType())) {
			if (!bankBranch.isEcs()) {
				isValidBranch = false;
			}
		} else if (StringUtils.equals(MandateConstants.TYPE_DDM, mandate.getMandateType())) {
			if (!bankBranch.isDda()) {
				isValidBranch = false;
			}
		} else if (StringUtils.equals(MandateConstants.TYPE_NACH, mandate.getMandateType())) {
			if (!bankBranch.isNach()) {
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

	@Override
	public Mandate approveMandate(Mandate mandate) throws ServiceException {
		logger.debug("Entering");

		Mandate response = null;

		// bean validations
		validationUtility.validate(mandate, SaveValidationGroup.class);

		WSReturnStatus returnStatus = doMandateValidation(mandate);

		// for logging purpose
		String[] logFields = new String[3];
		logFields[0] = mandate.getCustCIF();
		logFields[1] = mandate.getAccNumber();
		logFields[2] = mandate.getAccHolderName();
		APIErrorHandlerService.logKeyFields(logFields);

		if (StringUtils.isBlank(returnStatus.getReturnCode())) {
			if (StringUtils.isBlank(mandate.getMandateRef())) {
				response = new Mandate();
				String[] paramValue = new String[1];
				paramValue[0] = "mandateRef";
				response.setReturnStatus(APIErrorHandlerService.getFailedStatus("90502", paramValue));
				return response;
			} else {
				// Validating duplicate UMRN
				int count = mandateService.getMandateByMandateRef(mandate.getMandateRef());
				if (count > 0) {
					response = new Mandate();
					String[] paramValue = new String[2];
					paramValue[0] = "mandateRef with ";
					paramValue[1] = mandate.getMandateRef();
					response.setReturnStatus(APIErrorHandlerService.getFailedStatus("41001", paramValue));
					return response;
				}
			}
			if (mandate.isSwapIsActive() && StringUtils.isBlank(mandate.getOrgReference())) {
				response = new Mandate();
				String[] paramValue = new String[1];
				paramValue[0] = "finReference";
				response.setReturnStatus(APIErrorHandlerService.getFailedStatus("90502", paramValue));
				return response;
			}
			if (mandate.isSwapIsActive()) {
				TableType tableType = TableType.MAIN_TAB;
				if (ImplementationConstants.ALW_APPROVED_MANDATE_IN_ORG) {
					tableType = TableType.TEMP_TAB;
				}

				String finType = financeMainDAO.getFinanceType(mandate.getOrgReference(), tableType);

				String allowedRepayModes = StringUtils.trimToEmpty(financeTypeService.getAllowedRepayMethods(finType));
				if (StringUtils.isNotBlank(allowedRepayModes)) {
					boolean isTypeFound = false;
					String[] types = allowedRepayModes.split(PennantConstants.DELIMITER_COMMA);
					for (String type : types) {
						if (StringUtils.equals(type, mandate.getMandateType())) {
							isTypeFound = true;
							break;
						}
					}
					if (!isTypeFound) {
						String[] valueParm = new String[2];
						valueParm[0] = mandate.getMandateType();
						returnStatus = APIErrorHandlerService.getFailedStatus("90307", valueParm);
						response = new Mandate();
						response.setReturnStatus(returnStatus);
						return response;
					}
				}
			}

			response = mandateController.doApproveMandate(mandate);
		} else {
			response = new Mandate();
			response.setReturnStatus(returnStatus);
		}
		// for logging purpose
		if (response.getMandateID() != Long.MIN_VALUE) {
			APIErrorHandlerService.logReference(String.valueOf(response.getMandateID()));
		}
		logger.debug("Leaving");
		return response;
	}

	/**
	 * Method for update Mandate status in PLF system.
	 * 
	 * @param mandate
	 * @throws ServiceException
	 */
	@Override
	public WSReturnStatus updateMandateStatus(Mandate mandate) throws ServiceException {
		logger.debug(Literal.ENTERING);

		WSReturnStatus returnStatus = validateRequestData(mandate);
		if (StringUtils.isNotBlank(returnStatus.getReturnCode())) {
			return returnStatus;
		} else {
			returnStatus = mandateController.updateMandateStatus(mandate);
		}

		logger.debug(Literal.LEAVING);
		return returnStatus;
	}

	public WSReturnStatus validateRequestData(Mandate mandate) {
		logger.debug(Literal.ENTERING);

		WSReturnStatus returnStatus = new WSReturnStatus();
		Mandate aMandate = null;
		if (mandate.getMandateID() == Long.MIN_VALUE) {
			String[] valueParm = new String[1];
			valueParm[0] = "mandateID";
			returnStatus = APIErrorHandlerService.getFailedStatus("90502", valueParm);
			return returnStatus;
		} else {
			aMandate = mandateService.getApprovedMandateById(mandate.getMandateID());
			if (aMandate == null || !aMandate.isActive()) {
				String[] valueParm = new String[1];
				valueParm[0] = String.valueOf(mandate.getMandateID());
				returnStatus = APIErrorHandlerService.getFailedStatus("90303", valueParm);
				return returnStatus;
			}
		}

		if (StringUtils.isBlank(mandate.getStatus())) {
			String[] valueParm = new String[1];
			valueParm[0] = "status";
			returnStatus = APIErrorHandlerService.getFailedStatus("90502", valueParm);
			return returnStatus;
		} else {
			String mandateStatus = "N";
			String mandateRegStatus = SysParamUtil.getValueAsString(SMTParameterConstants.MANDATE_REGISTRATION_STATUS);
			if (StringUtils.isNotBlank(mandateRegStatus)) {
				mandateStatus = mandateRegStatus;
			}

			if (StringUtils.equalsIgnoreCase(mandateStatus, mandate.getStatus())) {
				mandate.setStatus(mandate.getStatus().toUpperCase());
				if (StringUtils.isNotBlank(aMandate.getMandateRef())) {
					// String[] valueParm = new String[1];
					// valueParm[0] = "mandateRef is already exist";
					// returnStatus = APIErrorHandlerService.getFailedStatus("30550", valueParm);
					// return returnStatus;
				}
			}

			if (!StringUtils.equalsIgnoreCase(mandateStatus, mandate.getStatus())
					&& !StringUtils.equalsIgnoreCase(MandateConstants.STATUS_REJECTED, mandate.getStatus())
					&& !StringUtils.equalsIgnoreCase(MandateConstants.MANDATE_STATUS_ACKNOWLEDGE,
							mandate.getStatus())) {
				String[] valueParm = new String[2];
				valueParm[0] = "status";
				valueParm[1] = mandateRegStatus + ", " + PennantConstants.RCD_STATUS_REJECTED + ", "
						+ MandateConstants.MANDATE_STATUS_ACKNOWLEDGE;
				returnStatus = APIErrorHandlerService.getFailedStatus("90281", valueParm);
				return returnStatus;
			}

			if (StringUtils.equalsIgnoreCase(MandateConstants.MANDATE_STATUS_ACKNOWLEDGE, mandate.getStatus())
					&& !StringUtils.equals(MandateConstants.STATUS_AWAITCON, aMandate.getStatus())) {
				// String[] valueParm = new String[2];
				// valueParm[0] = "Mandate current status is " + aMandate.getStatus();
				// valueParm[1] = " not allowed to pass " + MandateConstants.MANDATE_STATUS_ACKNOWLEDGE;
				// returnStatus = APIErrorHandlerService.getFailedStatus("30550", valueParm);
				// return returnStatus;
			}

			if ((StringUtils.equalsIgnoreCase(MandateConstants.STATUS_APPROVED, mandate.getStatus())
					|| StringUtils.equalsIgnoreCase("Accepted", mandate.getStatus()))
					&& StringUtils.isBlank(mandate.getMandateRef())) {
				String[] valueParm = new String[1];
				valueParm[0] = "mandateRef/UMRNNo";
				returnStatus = APIErrorHandlerService.getFailedStatus("90502", valueParm);
				return returnStatus;
			}

			if ((StringUtils.equalsIgnoreCase(MandateConstants.STATUS_APPROVED, mandate.getStatus())
					|| StringUtils.equalsIgnoreCase("Accepted", mandate.getStatus())
					|| StringUtils.equalsIgnoreCase(MandateConstants.STATUS_REJECTED, mandate.getStatus()))
					&& StringUtils.isNotBlank(mandate.getMandateRef()) && !StringUtils
							.equalsIgnoreCase(MandateConstants.MANDATE_STATUS_ACKNOWLEDGE, aMandate.getStatus())) {
				String[] valueParm = new String[1];
				valueParm[0] = "Mandate will Approve/Reject only when it is Acknowledged.";
				returnStatus = APIErrorHandlerService.getFailedStatus("30550", valueParm);
				return returnStatus;
			}
			if (StringUtils.equalsIgnoreCase(MandateConstants.STATUS_REJECTED, mandate.getStatus())
					&& StringUtils.isBlank(mandate.getReason())) {
				String[] valueParm = new String[1];
				valueParm[0] = "reason";
				returnStatus = APIErrorHandlerService.getFailedStatus("90502", valueParm);
				return returnStatus;
			}

			if (StringUtils.isNotBlank((mandateRegStatus))
					&& StringUtils.equalsIgnoreCase(mandate.getStatus(), mandateRegStatus)) {
				mandate.setStatus("APPROVED");
			} else {
				mandate.setStatus(mandate.getStatus().toUpperCase());
			}
		}

		if (StringUtils.isNotBlank(mandate.getOrgReference())) {
			Mandate tempMandate = mandateService.getMandateStatusById(mandate.getOrgReference(),
					mandate.getMandateID());
			if (tempMandate == null) {
				String[] valueParm = new String[1];
				valueParm[0] = "finReference " + mandate.getOrgReference() + " is not assign to mandateId "
						+ mandate.getMandateID();
				returnStatus = APIErrorHandlerService.getFailedStatus("30550", valueParm);
				return returnStatus;

			}
		} else {
			mandate.setOrgReference(aMandate.getOrgReference());
		}

		if ((StringUtils.equalsIgnoreCase(MandateConstants.STATUS_APPROVED, mandate.getStatus())
				|| StringUtils.equalsIgnoreCase("Accepted", mandate.getStatus())) && aMandate.isSwapIsActive()) {
			mandate.setSwapIsActive(aMandate.isSwapIsActive());
			mandate.setMandateType(aMandate.getMandateType());
			// FinanceMain finMain =
			// financeMainService.getFinanceMainByFinRef(mandate.getOrgReference());
			TableType tableType = TableType.MAIN_TAB;
			if (ImplementationConstants.ALW_APPROVED_MANDATE_IN_ORG) {
				tableType = TableType.TEMP_TAB;
			}

			String finType = financeMainDAO.getFinanceType(mandate.getOrgReference(), tableType);
			String allowedRepayModes = financeTypeService.getAllowedRepayMethods(finType);
			if (StringUtils.isNotBlank(allowedRepayModes)) {
				boolean isTypeFound = false;
				String[] types = allowedRepayModes.split(PennantConstants.DELIMITER_COMMA);
				for (String type : types) {
					if (StringUtils.equals(type, aMandate.getMandateType())) {
						isTypeFound = true;
						break;
					}
				}
				if (!isTypeFound) {
					String[] valueParm = new String[2];
					valueParm[0] = mandate.getMandateType();
					returnStatus = APIErrorHandlerService.getFailedStatus("90307", valueParm);
					return returnStatus;
				}
			}
		}

		logger.debug(Literal.LEAVING);
		return returnStatus;
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

	@Autowired
	public void setFinanceScheduleDetailDAO(FinanceScheduleDetailDAO financeScheduleDetailDAO) {
		this.financeScheduleDetailDAO = financeScheduleDetailDAO;
	}

	@Autowired
	public void setEntityService(EntityService entityService) {
		this.entityService = entityService;
	}

	@Autowired
	public void setFinanceTypeService(FinanceTypeService financeTypeService) {
		this.financeTypeService = financeTypeService;
	}

	@Autowired
	public void setPartnerBankDAO(PartnerBankDAO partnerBankDAO) {
		this.partnerBankDAO = partnerBankDAO;
	}

	@Autowired
	public void setFinanceMainDAO(FinanceMainDAO financeMainDAO) {
		this.financeMainDAO = financeMainDAO;
	}

}
