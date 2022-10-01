package com.pennanttech.service.impl;

import java.util.Date;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pennant.app.constants.ImplementationConstants;
import com.pennant.app.util.FrequencyUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.applicationmaster.EntityDAO;
import com.pennant.backend.dao.customermasters.CustomerDAO;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.dao.mandate.MandateDAO;
import com.pennant.backend.dao.partnerbank.PartnerBankDAO;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.WSReturnStatus;
import com.pennant.backend.model.applicationmaster.BankDetail;
import com.pennant.backend.model.bmtmasters.BankBranch;
import com.pennant.backend.model.mandate.Mandate;
import com.pennant.backend.service.applicationmaster.BankDetailService;
import com.pennant.backend.service.bmtmasters.BankBranchService;
import com.pennant.backend.service.customermasters.CustomerDetailsService;
import com.pennant.backend.service.mandate.MandateService;
import com.pennant.backend.service.rmtmasters.FinanceTypeService;
import com.pennant.backend.util.MandateConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.SMTParameterConstants;
import com.pennant.pff.api.service.AbstractService;
import com.pennant.pff.extension.MandateExtension;
import com.pennant.pff.mandate.InstrumentType;
import com.pennant.pff.mandate.MandateStatus;
import com.pennant.pff.mandate.MandateUtil;
import com.pennant.validation.SaveValidationGroup;
import com.pennant.validation.UpdateValidationGroup;
import com.pennant.validation.ValidationUtility;
import com.pennant.ws.exception.ServiceException;
import com.pennanttech.controller.MandateController;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pffws.MandateRestService;
import com.pennanttech.pffws.MandateSoapService;
import com.pennanttech.ws.model.mandate.MandateDetial;

@Service
public class MandateWebServiceImpl extends AbstractService implements MandateRestService, MandateSoapService {
	private ValidationUtility validationUtility;
	private MandateController mandateController;
	private CustomerDetailsService customerDetailsService;
	private BankBranchService bankBranchService;
	private MandateService mandateService;
	private BankDetailService bankDetailService;
	private FinanceTypeService financeTypeService;
	private PartnerBankDAO partnerBankDAO;
	private FinanceMainDAO financeMainDAO;
	private MandateDAO mandateDAO;
	private EntityDAO entityDAO;
	private CustomerDAO customerDAO;

	@Override
	public Mandate createMandate(Mandate mandate) throws ServiceException {
		logger.debug(Literal.ENTERING);

		validationUtility.validate(mandate, SaveValidationGroup.class);

		Mandate response = new Mandate();

		WSReturnStatus returnStatus = doMandateValidation(mandate);

		if (returnStatus != null) {
			response.setReturnStatus(returnStatus);
			return response;
		}

		if (StringUtils.isNotBlank(mandate.getMandateRef()) && !InstrumentType.isEMandate(mandate.getMandateType())) {
			response.setReturnStatus(getFailedStatus("90329", "mandateRef", "createMandate"));
			return response;
		}

		logKeyFields(mandate.getCustCIF(), mandate.getAccNumber(), mandate.getAccHolderName());

		if (!(InstrumentType.isDAS(mandate.getMandateType()) || InstrumentType.isSI(mandate.getMandateType()))) {
			response = mandateController.createMandate(mandate);
		} else {
			response = mandateController.createMandate(prepareMandate(mandate));
		}

		if (response.getMandateID() != Long.MIN_VALUE) {
			logReference(String.valueOf(response.getMandateID()));
		}

		logger.debug(Literal.LEAVING);
		return response;
	}

	@Override
	public Mandate getMandate(long mandateID) throws ServiceException {
		logger.debug(Literal.ENTERING);

		if (mandateID < 0) {
			validationUtility.fieldLevelException();
		}

		logReference(String.valueOf(mandateID));
		return mandateController.getMandate(mandateID);
	}

	@Override
	public WSReturnStatus updateMandate(Mandate mandate) throws ServiceException {
		logger.debug(Literal.ENTERING);

		validationUtility.validate(mandate, UpdateValidationGroup.class);

		logKeyFields(mandate.getCustCIF(), mandate.getAccNumber(), mandate.getAccHolderName());

		long mandateID = mandate.getMandateID();

		Mandate detail = mandateService.getApprovedMandateById(mandateID);

		if (detail == null) {
			return getFailedStatus("90303", String.valueOf(mandateID));
		}

		logReference(String.valueOf(mandateID));

		String mandateStatus = detail.getStatus();

		if (MandateStatus.isApproved(mandateStatus)) {
			return getFailedStatus("90345", "Approved");
		}

		WSReturnStatus returnStatus = doMandateValidation(mandate);

		if (returnStatus != null) {
			return returnStatus;
		}

		if (StringUtils.isNotBlank(mandate.getMandateRef())) {
			return getFailedStatus("90329", "mandateRef", "updateMandate");
		}

		if (MandateStatus.isAwaitingConf(mandateStatus)) {
			return getFailedStatus("90345", "Awaiting Confirmation");
		}

		if (MandateStatus.isHold(mandateStatus)) {
			return getFailedStatus("90345", "Hold");
		}

		return mandateController.updateMandate(mandate);
	}

	@Override
	public WSReturnStatus deleteMandate(long mandateID) throws ServiceException {
		logger.debug(Literal.ENTERING);

		if (mandateID < 0) {
			validationUtility.fieldLevelException();
		}

		logReference(String.valueOf(mandateID));

		if (!mandateDAO.isValidMandate(mandateID)) {
			return getFailedStatus("90303", String.valueOf(mandateID));
		}

		return mandateController.deleteMandate(mandateID);
	}

	@Override
	public MandateDetial getMandates(String cif) throws ServiceException {
		logger.debug(Literal.ENTERING);

		if (StringUtils.isBlank(cif)) {
			validationUtility.fieldLevelException();
		}

		logReference(cif);

		long custID = customerDAO.getCustIDByCIF(cif);
		if (custID <= 0) {
			MandateDetial response = new MandateDetial();
			response.setReturnStatus(getFailedStatus("90101", cif));

			return response;
		}

		return mandateController.getMandates(cif);
	}

	@Override
	public WSReturnStatus loanMandateSwapping(MandateDetial mandate) throws ServiceException {
		logger.debug(Literal.ENTERING);

		WSReturnStatus response = doValidation(mandate);
		logReference(mandate.getFinReference());

		if (response != null) {
			return response;
		}

		return mandateController.loanMandateSwapping(mandate);
	}

	@Override
	public Mandate approveMandate(Mandate mandate) throws ServiceException {
		logger.debug(Literal.ENTERING);

		Mandate response = new Mandate();

		validationUtility.validate(mandate, SaveValidationGroup.class);

		WSReturnStatus returnStatus = doMandateValidation(mandate);

		if (returnStatus != null) {
			response.setReturnStatus(returnStatus);
		}

		logKeyFields(mandate.getCustCIF(), mandate.getAccNumber(), mandate.getAccHolderName());

		if (StringUtils.isBlank(mandate.getMandateRef())) {
			response.setReturnStatus(getFailedStatus("90502", "mandateRef"));
			return response;
		}

		if (mandateService.getMandateByMandateRef(mandate.getMandateRef()) > 0) {
			response.setReturnStatus(getFailedStatus("41001", "mandateRef with ", mandate.getMandateRef()));
			return response;
		}

		if (mandate.isSwapIsActive() && StringUtils.isBlank(mandate.getOrgReference())) {
			response.setReturnStatus(getFailedStatus("90502", "finReference"));
			return response;
		}

		if (mandate.isSwapIsActive()) {
			TableType tableType = TableType.MAIN_TAB;
			if (MandateExtension.APPROVE_ON_LOAN_ORG) {
				tableType = TableType.TEMP_TAB;
			}

			String finType = financeMainDAO.getFinanceType(mandate.getOrgReference(), tableType);

			String alwRepayMthds = StringUtils.trimToEmpty(financeTypeService.getAllowedRepayMethods(finType));
			if (StringUtils.isNotBlank(alwRepayMthds) && !alwRepayMthds.contains(mandate.getMandateType())) {
				response.setReturnStatus(getFailedStatus("90307", mandate.getMandateType()));
				return response;
			}
		}

		if (!(InstrumentType.isDAS(mandate.getMandateType()) || InstrumentType.isSI(mandate.getMandateType()))) {
			response = mandateController.createMandate(mandate);
		} else {
			response = mandateController.createMandate(prepareMandate(mandate));
		}

		if (response.getMandateID() != Long.MIN_VALUE) {
			logReference(String.valueOf(response.getMandateID()));
		}

		logger.debug(Literal.LEAVING);
		return response;
	}

	@Override
	public WSReturnStatus updateMandateStatus(Mandate mandate) throws ServiceException {
		logger.debug(Literal.ENTERING);

		WSReturnStatus returnStatus = validateRequestData(mandate);
		if (returnStatus != null) {
			return returnStatus;
		}

		return mandateController.updateMandateStatus(mandate);
	}

	@Override
	public WSReturnStatus updateApprovedMandate(Mandate mandate) throws ServiceException {
		logger.debug(Literal.ENTERING);

		logKeyFields(String.valueOf(mandate.getMandateID()), mandate.getMandateRef());

		WSReturnStatus returnStatus = validate(mandate);

		if (returnStatus != null) {
			return returnStatus;
		}

		mandateController.updateApprovedMandate(copyBeforeImage(mandate));

		logger.debug(Literal.LEAVING);
		return getSuccessStatus();
	}

	private Mandate copyBeforeImage(Mandate request) {
		Mandate exMandate = mandateService.getApprovedMandateById(request.getMandateID());

		Mandate mandate2 = new Mandate();
		BeanUtils.copyProperties(exMandate, mandate2);
		mandate2.setBefImage(exMandate);

		if (StringUtils.isNotBlank(request.getMandateRef())) {
			mandate2.setMandateRef(request.getMandateRef());
		}

		mandate2.setStatus(request.getStatus());
		return mandate2;
	}

	private WSReturnStatus validate(Mandate request) {
		WSReturnStatus returnStatus = new WSReturnStatus();

		if (request.getMandateID() == Long.MIN_VALUE) {
			return getFailedStatus("90502", "mandateID");
		}

		Mandate aMandate = mandateDAO.getMandateDetail(request.getMandateID());
		if (aMandate == null || !aMandate.isActive()) {
			return getFailedStatus("90303", String.valueOf(request.getMandateID()));
		}

		if (StringUtils.isBlank(request.getStatus())) {
			return getFailedStatus("90502", "status");
		}

		if (!MandateStatus.isApproved(request.getStatus()) && !MandateStatus.isRejected(request.getStatus())) {
			return getFailedStatus("90281", "status", MandateStatus.APPROVED + ", " + MandateStatus.REJECTED);
		} else if (MandateStatus.isApproved(aMandate.getStatus()) || MandateStatus.isRejected(aMandate.getStatus())) {
			return getFailedStatus("90345", "already ", aMandate.getStatus());
		}

		if (StringUtils.isNotEmpty(request.getMandateRef()) && MandateStatus.isRejected(request.getStatus())) {
			return getFailedStatus("RU0039", "For the Status REJECTED mandateRef is");

		} else if (!StringUtils.isBlank(request.getMandateRef()) && request.getMandateRef().length() > 50) {
			return getFailedStatus("90300", "mandateRef", "50");
		}

		return returnStatus;
	}

	private WSReturnStatus doMandateValidation(Mandate mandate) {
		logger.debug(Literal.ENTERING);

		String mandateType = mandate.getMandateType();

		WSReturnStatus returnStatus = basicDetailValidation(mandate);

		if (returnStatus != null) {
			return returnStatus;
		}

		switch (InstrumentType.valueOf(mandateType)) {
		case ECS:
		case DD:
		case NACH:
		case EMANDATE:
			returnStatus = validateBankDetail(mandate);
			break;
		case DAS:
			if (mandate.getEmployeeID() == null) {
				returnStatus = getFailedStatus("90502", "employeeID");
			}
			break;
		default:
			break;
		}

		if (returnStatus != null) {
			return returnStatus;
		}

		if (mandate.getPartnerBankId() <= 0 && StringUtils.isNotBlank(mandate.getPartnerBankCode())) {
			long partnerBankID = partnerBankDAO.getPartnerBankID(mandate.getPartnerBankCode());
			if (partnerBankID <= 0) {
				String pbLabel = PennantJavaUtil.getLabel("label_MandateDialog_PartnerBank.value");
				return getFailedStatus("90224", pbLabel, mandate.getPartnerBankCode());
			} else {
				mandate.setPartnerBankId(partnerBankID);
			}
		}

		logger.debug(Literal.LEAVING);
		return returnStatus;
	}

	private WSReturnStatus basicDetailValidation(Mandate mandate) {
		String custCIF = mandate.getCustCIF();
		String entityCode = mandate.getEntityCode();
		String mandateType = mandate.getMandateType();
		String orgReference = mandate.getOrgReference();

		long custID = customerDetailsService.getCustIDByCIF(custCIF);
		if (custID == 0) {
			return getFailedStatus("90101", custCIF);
		}

		mandate.setCustID(custID);

		if (StringUtils.isBlank(entityCode)) {
			return getFailedStatus("90502", "Entity");
		}

		if (entityDAO.getEntityCount(entityCode) == 0) {
			return getFailedStatus("90701", "Entity", entityCode);
		}

		if (StringUtils.isEmpty(orgReference)) {
			return getFailedStatus("90502", "FinReference");
		}

		Mandate loanInfo = mandateDAO.getLoanInfo(orgReference);

		if (loanInfo == null) {
			return getFailedStatus("90201", orgReference);
		}

		if (StringUtils.isNotEmpty(custCIF) && loanInfo.getCustID() != mandate.getCustID()) {
			return getFailedStatus("90406", custCIF, orgReference);
		} else {
			mandate.setCustID(loanInfo.getCustID());
		}

		if (!loanInfo.getAlwdRpyMethods().contains(mandateType)) {
			return getFailedStatus("90307", mandateType);
		}

		return null;
	}

	private WSReturnStatus validateBankDetail(Mandate mandate) {
		if (StringUtils.isNotBlank(mandate.getAccType())) {
			List<ValueLabel> accType = MandateUtil.getAccountTypes();

			if (accType.stream().noneMatch(ac -> ac.getValue().equals(mandate.getAccType()))) {
				return getFailedStatus("90308", mandate.getAccType());
			}
		}

		String mobileNumber = mandate.getPhoneNumber();
		if (StringUtils.isNotBlank(mobileNumber) && !(mobileNumber.matches("\\d{10}"))) {
			return getFailedStatus("90278", mobileNumber);
		}

		String ifsc = mandate.getIFSC();
		String micr = mandate.getMICR();
		String bankCode = mandate.getBankCode();
		String branchCode = mandate.getBranchCode();

		BankBranch bankBranch = bankBranchService.getBankBranch(ifsc, micr, bankCode, branchCode);

		ErrorDetail error = bankBranch.getError();
		if (error != null) {
			return getFailedStatus(error.getCode(), error.getError());
		}

		mandate.setBankCode(bankBranch.getBankCode());
		mandate.setMICR(bankBranch.getMICR());

		if (!bankBranchService.validateBranchCode(bankBranch, mandate.getMandateType())) {
			return getFailedStatus("90333", mandate.getMandateType());
		}

		if (StringUtils.isNotBlank(mandate.getBankCode()) && StringUtils.isNotBlank(mandate.getAccNumber())) {
			BankDetail bankDetails = bankDetailService.getAccNoLengthByCode(mandate.getBankCode());
			int length = mandate.getAccNumber().length();

			if (bankDetails != null) {
				int maxAccNoLength = bankDetails.getAccNoLength();
				int minAccNolength = bankDetails.getMinAccNoLength();
				if (length < minAccNolength || length > maxAccNoLength) {
					String minMsg = String.valueOf(minAccNolength).concat(" characters");
					String maxMsg = String.valueOf(maxAccNoLength).concat(" characters");

					if (minAccNolength == maxAccNoLength) {
						return getFailedStatus("30570", "AccountNumber", maxMsg);
					} else {
						return getFailedStatus("BNK001", "AccountNumber", minMsg, maxMsg);
					}
				}
			}
		}

		return mandateDetailValidation(mandate);
	}

	private WSReturnStatus mandateDetailValidation(Mandate mandate) {
		String mandateStatus = mandate.getStatus();
		String periodicity = mandate.getPeriodicity();

		if (StringUtils.isNotBlank(mandateStatus)) {
			List<ValueLabel> status = MandateUtil.getMandateStatus();

			if (status.stream().noneMatch(sts -> sts.getValue().equals(mandateStatus))) {
				return getFailedStatus("90309", mandateStatus);
			}
		}

		if (StringUtils.isNotBlank(periodicity)) {
			ErrorDetail error = FrequencyUtil.validateFrequency(periodicity);
			if (error != null && StringUtils.isNotBlank(error.getCode())) {
				return getFailedStatus("90207", periodicity);
			}
		}

		if (ImplementationConstants.ALLOW_BARCODE && StringUtils.isBlank(mandate.getBarCodeNumber())) {
			return getFailedStatus("90502", "barCode");
		}

		WSReturnStatus returnSts = mandateDateValidation(mandate);
		if (returnSts != null) {
			return returnSts;
		}

		if (InstrumentType.isEMandate(mandate.getMandateType())) {
			returnSts = validateEMandate(mandate);
			if (returnSts != null) {
				return returnSts;
			}
		}

		List<ErrorDetail> errors = mandateService.doValidations(mandate);
		if (CollectionUtils.isNotEmpty(errors)) {
			for (ErrorDetail error : errors) {
				if (StringUtils.isNotBlank(error.getCode())) {
					return getFailedStatus(error.getCode(), error.getError());
				}
			}
		}

		return null;
	}

	private WSReturnStatus mandateDateValidation(Mandate mandate) {
		Date expiryDate = mandate.getExpiryDate();

		if (!mandate.isOpenMandate() && expiryDate == null) {
			return getFailedStatus("90502", "expiryDate");
		}

		Date mandateStartDate = mandate.getStartDate();
		Date dftEndDate = null;
		if (expiryDate != null) {
			dftEndDate = SysParamUtil.getValueAsDate(SMTParameterConstants.APP_DFT_END_DATE);
			if (expiryDate.compareTo(mandateStartDate) <= 0 || expiryDate.after(dftEndDate)) {
				String[] valueParm = new String[3];
				valueParm[0] = "ExpiryDate";
				valueParm[1] = DateUtil.formatToLongDate(DateUtil.addDays(mandateStartDate, 1));
				valueParm[2] = DateUtil.formatToLongDate(dftEndDate);
				return getFailedStatus("90318", valueParm);
			}
		}

		if (mandateStartDate != null) {
			Date appDate = SysParamUtil.getAppDate();
			int mandStartDate = SysParamUtil.getValueAsInt(SMTParameterConstants.MANDATE_STARTDATE);
			if (dftEndDate == null) {
				dftEndDate = SysParamUtil.getValueAsDate(SMTParameterConstants.APP_DFT_END_DATE);
			}
			Date mandbackDate = DateUtil.addDays(appDate, -mandStartDate);
			if (mandateStartDate.before(mandbackDate) || mandateStartDate.after(dftEndDate)) {
				String[] valueParm = new String[3];
				valueParm[0] = "mandate start date";
				valueParm[1] = DateUtil.formatToLongDate(mandbackDate);
				valueParm[2] = DateUtil.formatToLongDate(dftEndDate);
				return getFailedStatus("90318", valueParm);
			}
		}

		return null;
	}

	private WSReturnStatus validateEMandate(Mandate mandate) {
		if (StringUtils.isBlank(mandate.geteMandateReferenceNo())) {
			return getFailedStatus("90502", "eMandateReferenceNo");
		}
		if (StringUtils.isBlank(mandate.geteMandateSource())) {
			return getFailedStatus("90502", "eMandateSource");
		}

		if (mandateService.validateEmandateSource(mandate.geteMandateSource()) == 0) {
			return getFailedStatus("90501", "eMandateSource ".concat(mandate.geteMandateSource()));
		}

		return null;
	}

	private Mandate prepareMandate(Mandate mandate) {
		Mandate mndt = new Mandate();
		mndt.setMandateType(mandate.getMandateType());
		mndt.setOrgReference(mandate.getOrgReference());
		mndt.setEntityCode(mandate.getEntityCode());
		mndt.setCustCIF(mandate.getCustCIF());

		if (InstrumentType.isDAS(mandate.getMandateType())) {
			mndt.setSourceId(PennantConstants.FINSOURCE_ID_API);
			mndt.setEmployeeID(mandate.getEmployeeID());
			mndt.setEmployerName(mandate.getEmployerName());
		} else if (InstrumentType.isSI(mandate.getMandateType())) {
			mndt.setBankBranchID(mandate.getBankBranchID());
			mndt.setCity(mandate.getCity());
		}

		return mndt;
	}

	private WSReturnStatus doValidation(MandateDetial md) {
		logger.debug(Literal.ENTERING);

		String finReference = md.getFinReference();
		Long oldMandateId = md.getOldMandateId();
		Long newMandateId = md.getNewMandateId();

		if (finReference == null) {
			return getFailedStatus("90502", "FinReference");
		}

		if (oldMandateId == null || oldMandateId <= 0) {
			return getFailedStatus("90502", "OldMandateId");
		}

		if (newMandateId == null || newMandateId <= 0) {
			return getFailedStatus("90502", "NewMandateId");
		}

		String custCIF = mandateDAO.getCustCIF(oldMandateId);
		if (custCIF == null) {
			return getFailedStatus("90303", String.valueOf(oldMandateId));
		}

		Long finID = financeMainDAO.getFinIDForMandate(finReference, oldMandateId);
		if (finID == null) {
			return getFailedStatus("90201", finReference);
		}

		Mandate newMandate = mandateService.getApprovedMandateById(newMandateId);
		if (newMandate == null) {
			return getFailedStatus("90303", String.valueOf(newMandateId));
		}

		if (!StringUtils.equals(custCIF, newMandate.getCustCIF())) {
			return getFailedStatus("90342");
		}

		if (!MandateConstants.skipRegistration().contains(newMandate.getMandateType())
				&& StringUtils.isBlank(newMandate.getMandateRef())) {
			return getFailedStatus("90305", String.valueOf(newMandateId));
		}

		if (MandateStatus.isRejected(newMandate.getStatus())) {
			return getFailedStatus("90306", newMandate.getStatus());
		}

		if (!newMandate.isOpenMandate() && StringUtils.isNotBlank(newMandate.getOrgReference())) {
			return getFailedStatus("90312", String.valueOf(newMandateId));
		}

		md.setMandateType(newMandate.getMandateType());

		logger.debug(Literal.LEAVING);
		return null;
	}

	private WSReturnStatus validateRequestData(Mandate mandate) {
		logger.debug(Literal.ENTERING);

		WSReturnStatus returnStatus = new WSReturnStatus();

		if (mandate.getMandateID() == Long.MIN_VALUE) {
			return getFailedStatus("90502", "mandateID");
		}

		Mandate aMandate = mandateDAO.getMandateDetail(mandate.getMandateID());
		if (aMandate == null || !aMandate.isActive()) {
			return getFailedStatus("90303", String.valueOf(mandate.getMandateID()));
		}

		String status = mandate.getStatus();

		if (StringUtils.isBlank(status)) {
			return getFailedStatus("90502", "status");
		}

		String mandateStatus = "N";
		String mandateRegStatus = SysParamUtil.getValueAsString(SMTParameterConstants.MANDATE_REGISTRATION_STATUS);
		if (StringUtils.isNotBlank(mandateRegStatus)) {
			mandateStatus = mandateRegStatus;
		}

		if (StringUtils.equalsIgnoreCase(mandateStatus, status)) {
			mandate.setStatus(status.toUpperCase());
		}

		if (!StringUtils.equalsIgnoreCase(mandateStatus, status) && !MandateStatus.isRejected(status)
				&& !MandateStatus.isAcknowledge(status)) {
			String msg = mandateRegStatus.concat(", ").concat(PennantConstants.RCD_STATUS_REJECTED).concat(", ")
					.concat(MandateStatus.ACKNOWLEDGE);
			return getFailedStatus("90281", "status", msg);
		}

		if ((MandateStatus.isApproved(status) || MandateStatus.isAccepted(status))
				&& StringUtils.isBlank(mandate.getMandateRef())) {
			return getFailedStatus("90502", "mandateRef/UMRNNo");
		}

		if ((MandateStatus.isApproved(status) || MandateStatus.isAccepted(status) || MandateStatus.isRejected(status))
				&& StringUtils.isNotBlank(mandate.getMandateRef())
				&& !MandateStatus.isAcknowledge(aMandate.getStatus())) {
			return getFailedStatus("30550", "Mandate will Approve/Reject only when it is Acknowledged.");
		}

		if (MandateStatus.isRejected(status) && StringUtils.isBlank(mandate.getReason())) {
			return getFailedStatus("90502", "reason");
		}

		if (StringUtils.isNotBlank((mandateRegStatus)) && StringUtils.equalsIgnoreCase(status, mandateRegStatus)) {
			mandate.setStatus("APPROVED");
		} else {
			mandate.setStatus(status.toUpperCase());
		}

		if (StringUtils.isNotBlank(mandate.getOrgReference())) {
			Mandate tempMandate = mandateService.getMandateStatusById(mandate.getOrgReference(),
					mandate.getMandateID());
			if (tempMandate == null) {
				StringBuilder msg = new StringBuilder("FinReference ");
				msg.append(mandate.getOrgReference());
				msg.append(" is not assign to mandateId ");
				msg.append(mandate.getMandateID());

				return getFailedStatus("30550", msg.toString());
			}
		}

		if ((MandateStatus.isApproved(status) || MandateStatus.isAccepted(status)) && aMandate.isSwapIsActive()) {
			mandate.setSwapIsActive(aMandate.isSwapIsActive());
			mandate.setMandateType(aMandate.getMandateType());

			TableType tableType = TableType.MAIN_TAB;
			if (MandateExtension.APPROVE_ON_LOAN_ORG) {
				tableType = TableType.TEMP_TAB;
			}

			String finType = financeMainDAO.getFinanceType(mandate.getOrgReference(), tableType);
			String allowedRepayModes = financeTypeService.getAllowedRepayMethods(finType);

			if (!allowedRepayModes.contains(aMandate.getMandateType())) {
				return getFailedStatus("90307", mandate.getMandateType());
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
	public void setBankDetailService(BankDetailService bankDetailService) {
		this.bankDetailService = bankDetailService;
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

	@Autowired
	public void setMandateDAO(MandateDAO mandateDAO) {
		this.mandateDAO = mandateDAO;
	}

	@Autowired
	public void setEntityDAO(EntityDAO entityDAO) {
		this.entityDAO = entityDAO;
	}

	@Autowired
	public void setCustomerDAO(CustomerDAO customerDAO) {
		this.customerDAO = customerDAO;
	}

}
