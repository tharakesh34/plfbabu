package com.pennanttech.controller;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.cxf.phase.PhaseInterceptorChain;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.pennant.app.util.APIHeader;
import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.NumberToEnglishWords;
import com.pennant.app.util.SessionUserDetails;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.mandate.MandateDAO;
import com.pennant.backend.model.WSReturnStatus;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.bmtmasters.BankBranch;
import com.pennant.backend.model.mandate.Mandate;
import com.pennant.backend.model.pennydrop.BankAccountValidation;
import com.pennant.backend.service.bmtmasters.BankBranchService;
import com.pennant.backend.service.customermasters.CustomerDetailsService;
import com.pennant.backend.service.finance.FinanceMainService;
import com.pennant.backend.service.mandate.MandateService;
import com.pennant.backend.service.pennydrop.PennyDropService;
import com.pennant.backend.util.MandateConstants;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.pff.api.controller.AbstractController;
import com.pennant.pff.extension.MandateExtension;
import com.pennant.pff.mandate.InstrumentType;
import com.pennant.pff.mandate.MandateStatus;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.model.LoggedInUser;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.util.APIConstants;
import com.pennanttech.ws.model.mandate.MandateDetial;

public class MandateController extends AbstractController {
	private MandateService mandateService;
	private BankBranchService bankBranchService;
	private CustomerDetailsService customerDetailsService;
	private FinanceMainService financeMainService;
	private PennyDropService pennyDropService;
	private MandateDAO mandateDAO;

	public MandateController() {
		super();
	}

	public Mandate createMandate(Mandate mandate) {
		logger.debug(Literal.ENTERING);

		Mandate response = new Mandate();

		prepareRequiredData(mandate);

		if (mandate.getReturnStatus() != null) {
			response.setReturnStatus(mandate.getReturnStatus());

			logger.debug(Literal.LEAVING);
			return response;
		}

		if (mandate.isSecurityMandate()
				&& (InstrumentType.isDAS(mandate.getMandateType()) || InstrumentType.isSI(mandate.getMandateType()))) {
			WSReturnStatus status = new WSReturnStatus();
			String[] valueParm = new String[2];
			valueParm[0] = "Mandate Type,";
			valueParm[1] = "Possible Values are NACH, ECS, EMANDATE ";

			ErrorDetail err = ErrorUtil.getError("STP0012", valueParm);
			status.setReturnCode(err.getCode());

			status.setReturnText(ErrorUtil.getErrorMessage(err.getMessage(), err.getParameters()));
			response.setReturnStatus(status);

			logger.debug(Literal.LEAVING);
			return response;
		}

		BankAccountValidation validation = new BankAccountValidation();
		if (mandate.getPennyDropStatus() != null) {
			validation.setiFSC(mandate.getIFSC());
			validation.setInitiateType("M");
			validation.setAcctNum(mandate.getAccNumber());
			validation.setStatus(mandate.getPennyDropStatus());

			pennyDropService.savePennyDropSts(validation);
		}

		mandate.setCustID(customerDetailsService.getCustIDByCIF(mandate.getCustCIF()));
		mandate.setRecordType(PennantConstants.RECORD_TYPE_NEW);
		mandate.setNewRecord(true);
		mandate.setActive(true);
		mandate.setVersion(1);
		mandate.setMandateCcy(SysParamUtil.getAppCurrency());
		mandate.setStatus(MandateStatus.NEW);

		try {
			AuditHeader ah = mandateService.doApprove(getAuditHeader(mandate, PennantConstants.TRAN_WF));

			List<ErrorDetail> errors = ah.getErrorMessage();
			if (CollectionUtils.isNotEmpty(errors)) {
				ErrorDetail error = errors.get(errors.size() - 1);
				response.setReturnStatus(getFailedStatus(error.getCode(), error.getError()));
				return response;
			}

			response = (Mandate) ah.getAuditDetail().getModelData();
			response.setReturnStatus(getSuccessStatus());
			doEmptyResponseObject(response);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
			logException(e.getMessage());
			response.setReturnStatus(getFailedStatus());
		}

		logger.debug(Literal.LEAVING);
		return response;
	}

	public Mandate getMandate(long mandateId) {
		logger.debug(Literal.ENTERING);

		Mandate response = mandateService.getApprovedMandateById(mandateId);

		if (response == null) {
			response = new Mandate();
			response.setReturnStatus(getFailedStatus("90303", String.valueOf(mandateId)));
			return response;
		}

		int format = CurrencyUtil.getFormat(response.getMandateCcy());

		try {
			BigDecimal maxlimt = PennantApplicationUtil.formateAmount(response.getMaxLimit(), format);

			response.setAmountInWords(NumberToEnglishWords.getNumberToWords(maxlimt.toBigInteger()));
			response.setReturnStatus(getSuccessStatus());
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
			logException(e.getMessage());
			response.setReturnStatus(getFailedStatus());
		}

		logger.debug(Literal.LEAVING);
		return response;
	}

	public WSReturnStatus updateMandate(Mandate mandate) {
		logger.debug(Literal.ENTERING);

		prepareRequiredData(mandate);

		if (mandate.getReturnStatus() != null) {

			logger.debug(Literal.LEAVING);
			return mandate.getReturnStatus();
		}

		Mandate prvMandate = mandateService.getApprovedMandateById(mandate.getMandateID());
		mandate.setCustID(prvMandate.getCustID());
		mandate.setRecordType(PennantConstants.RECORD_TYPE_UPD);
		mandate.setNewRecord(false);
		mandate.setVersion(prvMandate.getVersion() + 1);
		mandate.setActive(true);
		mandate.setMandateCcy(SysParamUtil.getAppCurrency());
		mandate.setStatus(MandateStatus.NEW);

		BeanUtils.copyProperties(mandate, prvMandate);

		try {
			AuditHeader ah = mandateService.doApprove(getAuditHeader(prvMandate, PennantConstants.TRAN_WF));

			List<ErrorDetail> errors = ah.getErrorMessage();
			if (CollectionUtils.isNotEmpty(errors)) {
				ErrorDetail error = errors.get(errors.size() - 1);
				logger.debug(Literal.LEAVING);
				return getFailedStatus(error.getCode(), error.getError());
			}

			logger.debug(Literal.LEAVING);
			return getSuccessStatus();
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
			logException(e.getMessage());
			return getFailedStatus();
		}

	}

	public WSReturnStatus deleteMandate(long mandateID) {
		logger.debug(Literal.ENTERING);

		Mandate mandate = mandateService.getApprovedMandateById(mandateID);

		prepareRequiredData(mandate);

		if (mandate.getReturnStatus() != null) {

			logger.debug(Literal.LEAVING);
			return mandate.getReturnStatus();
		}

		mandate.setRecordType(PennantConstants.RECORD_TYPE_DEL);
		mandate.setNewRecord(false);
		mandate.setVersion(mandate.getVersion() + 1);

		try {
			AuditHeader ah = mandateService.doApprove(getAuditHeader(mandate, PennantConstants.TRAN_WF));

			List<ErrorDetail> errors = ah.getErrorMessage();
			if (CollectionUtils.isNotEmpty(errors)) {
				ErrorDetail error = errors.get(errors.size() - 1);
				logger.debug(Literal.LEAVING);
				return getFailedStatus(error.getCode(), error.getError());
			}

			logger.debug(Literal.LEAVING);
			return getSuccessStatus();
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
			logException(e.getMessage());
			return getFailedStatus();
		}
	}

	public MandateDetial getMandates(String cif) {
		logger.debug(Literal.ENTERING);

		MandateDetial response = new MandateDetial();
		long custID = customerDetailsService.getCustIDByCIF(cif);
		List<Mandate> mandates = mandateService.getApprovedMandatesByCustomerId(custID);

		if (CollectionUtils.isEmpty(mandates)) {
			response.setReturnStatus(getFailedStatus("90304", cif));
			return response;
		}

		try {
			for (Mandate mandate : mandates) {
				BigDecimal maxlimt = PennantApplicationUtil.formateAmount(mandate.getMaxLimit(),
						CurrencyUtil.getFormat(mandate.getMandateCcy()));
				mandate.setAmountInWords(NumberToEnglishWords.getNumberToWords(maxlimt.toBigInteger()));
			}

			response.setMandateList(mandates);
			response.setReturnStatus(getSuccessStatus());
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
			logException(e.getMessage());
			response.setReturnStatus(getFailedStatus());
		}

		logger.debug(Literal.LEAVING);
		return response;
	}

	public WSReturnStatus loanMandateSwapping(MandateDetial md) {
		logger.debug(Literal.ENTERING);

		String finReference = md.getFinReference();
		Long newMandateId = md.getNewMandateId();
		String mandateType = md.getMandateType();
		Long oldMandateId = md.getOldMandateId();

		Mandate mandateById = mandateService.getMandateById(oldMandateId);

		if (mandateById == null) {
			return getFailedStatus("93304", "OldMandateId");
		}

		Mandate newMandateById = mandateService.getMandateById(newMandateId);

		if (newMandateById == null) {
			return getFailedStatus("93304", "NewMandateId");
		}

		boolean securityMandate = mandateById.isSecurityMandate();

		if (!securityMandate || !newMandateById.isSecurityMandate()) {
			return getFailedStatus();
		}

		Long finID = financeMainService.getFinID(finReference, TableType.MAIN_TAB);

		if (financeMainService.loanMandateSwapping(finID, newMandateId, mandateType, "", securityMandate) > 0) {
			logger.debug(Literal.LEAVING);
			return getSuccessStatus();
		}

		logger.debug(Literal.LEAVING);
		return getFailedStatus();
	}

	public Mandate doApproveMandate(Mandate mandate) {
		logger.debug(Literal.ENTERING);

		Mandate response = new Mandate();

		prepareRequiredData(mandate);

		if (mandate.getReturnStatus() != null) {
			response.setReturnStatus(mandate.getReturnStatus());

			logger.debug(Literal.LEAVING);
			return response;
		}

		mandate.setCustID(customerDetailsService.getCustIDByCIF(mandate.getCustCIF()));
		mandate.setRecordType(PennantConstants.RECORD_TYPE_NEW);
		mandate.setNewRecord(true);
		mandate.setActive(true);
		mandate.setVersion(1);
		mandate.setMandateCcy(SysParamUtil.getAppCurrency());
		mandate.setApproveMandate(true);
		mandate.setStatus(MandateStatus.APPROVED);

		try {
			AuditHeader ah = mandateService.doApprove(getAuditHeader(mandate, PennantConstants.TRAN_WF));

			List<ErrorDetail> errors = ah.getErrorMessage();
			if (CollectionUtils.isNotEmpty(errors)) {
				ErrorDetail error = errors.get(errors.size() - 1);
				logger.debug(Literal.LEAVING);
				response.setReturnStatus(getFailedStatus(error.getCode(), error.getError()));

				logger.debug(Literal.LEAVING);
				return response;
			}

			response = (Mandate) ah.getAuditDetail().getModelData();
			response.setReturnStatus(getSuccessStatus());

			if (mandate.isSwapIsActive()) {
				String type = "";
				Long finID = financeMainService.getFinID(mandate.getOrgReference(), TableType.MAIN_TAB);
				if (finID != null && MandateExtension.APPROVE_ON_LOAN_ORG) {
					type = "_Temp";
				}

				financeMainService.loanMandateSwapping(finID, response.getMandateID(), mandate.getMandateType(), type,
						mandate.isSecurityMandate());
			}

			doEmptyResponseObject(response);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
			logException(e.getMessage());
			response.setReturnStatus(getFailedStatus());
		}

		logger.debug(Literal.LEAVING);
		return response;
	}

	public WSReturnStatus updateMandateStatus(Mandate mandate) {
		logger.debug(Literal.ENTERING);

		int count = mandateService.updateMandateStatus(mandate);

		if (count == 0) {
			logger.error(Literal.LEAVING);
			return getFailedStatus();
		}

		long mandateID = mandate.getMandateID();
		String status = mandate.getStatus().toUpperCase();

		com.pennant.backend.model.mandate.MandateStatus mandateStatus = new com.pennant.backend.model.mandate.MandateStatus();

		mandateStatus.setMandateID(mandateID);
		mandateStatus.setStatus(status);
		mandateStatus.setReason(mandate.getReason());
		mandateStatus.setChangeDate(SysParamUtil.getAppDate());

		try {
			mandateService.saveStatus(mandateStatus);
			mandateDAO.updateStatusAfterRegistration(mandate.getMandateID(), status);
			if ((MandateStatus.isApproved(status) || MandateStatus.isAccepted(status)) && mandate.isSwapIsActive()) {
				String type = "";
				Long finID = financeMainService.getFinID(mandate.getOrgReference(), TableType.MAIN_TAB);

				if (finID != null && MandateExtension.APPROVE_ON_LOAN_ORG) {
					type = "_Temp";
				}

				String mandateType = mandate.getMandateType();
				financeMainService.loanMandateSwapping(finID, mandateID, mandateType, type, false);
			}

			logger.debug(Literal.LEAVING);
			return getSuccessStatus();

		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
			logException(e.getMessage());
			return getFailedStatus();
		}
	}

	public WSReturnStatus updateApprovedMandate(Mandate mandate) {
		if (mandateService.updateMandateStatus(mandate) <= 0) {
			return getFailedStatus();
		}

		com.pennant.backend.model.mandate.MandateStatus mandateStatus = new com.pennant.backend.model.mandate.MandateStatus();

		mandateStatus.setMandateID(mandate.getMandateID());
		mandateStatus.setStatus(mandate.getStatus());
		mandateStatus.setChangeDate(SysParamUtil.getAppDate());

		try {
			mandateService.saveStatus(mandateStatus);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
			return getFailedStatus();
		}

		return getSuccessStatus();
	}

	private void prepareRequiredData(Mandate mandate) {
		LoggedInUser loggedInUser = SessionUserDetails.getUserDetails(SessionUserDetails.getLogiedInUser());

		mandate.setUserDetails(loggedInUser);
		mandate.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
		mandate.setInputDate(SysParamUtil.getAppDate());
		mandate.setLastMntBy(loggedInUser.getUserId());
		mandate.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		mandate.setSourceId(APIConstants.FINSOURCE_ID_API);

		if (!InstrumentType.isDAS(mandate.getMandateType())) {
			String ifsc = mandate.getIFSC();
			String micr = mandate.getMICR();
			String bankCode = mandate.getBankCode();
			String branchCode = mandate.getBranchCode();

			BankBranch bankBranch = bankBranchService.getBankBranch(ifsc, micr, bankCode, branchCode);

			ErrorDetail error = bankBranch.getError();
			if (error != null) {
				mandate.setReturnStatus(getFailedStatus(error.getCode(), error.getError()));
			}

			mandate.setIFSC(bankBranch.getIFSC());
			mandate.setBankBranchID(bankBranch.getBankBranchID());

			if (StringUtils.isBlank(mandate.getPeriodicity())) {
				mandate.setPeriodicity(MandateConstants.MANDATE_DEFAULT_FRQ);
			}
		}

	}

	private void doEmptyResponseObject(Mandate response) {
		response.setCustCIF(null);
		response.setMandateType(null);
		response.setBankCode(null);
		response.setBranchCode(null);
		response.setIFSC(null);
		response.setMICR(null);
		response.setAccType(null);
		response.setAccNumber(null);
		response.setAccHolderName(null);
		response.setJointAccHolderName(null);
		response.setStartDate(null);
		response.setExpiryDate(null);
		response.setMaxLimit(null);
		response.setPeriodicity(null);
		response.setPhoneAreaCode(null);
		response.setPhoneCountryCode(null);
		response.setPhoneNumber(null);
		response.setBarCodeNumber(null);
		response.setOrgReference(null);
		response.setAmountInWords(null);
		response.setEntityCode(null);
		response.setDocImage(null);
		response.setDocumentName(null);
		response.setExternalRef(null);
		response.seteMandateReferenceNo(null);
		response.seteMandateSource(null);
	}

	private AuditHeader getAuditHeader(Mandate aMandate, String tranType) {
		AuditDetail ad = new AuditDetail(tranType, 1, aMandate.getBefImage(), aMandate);
		String strManID = String.valueOf(aMandate.getMandateID());
		AuditHeader ah = new AuditHeader(strManID, strManID, null, null, ad, aMandate.getUserDetails(),
				new HashMap<>());

		ah.setApiHeader(PhaseInterceptorChain.getCurrentMessage().getExchange().get(APIHeader.API_HEADER_KEY));
		return ah;
	}

	public void setMandateService(MandateService mandateService) {
		this.mandateService = mandateService;
	}

	public void setBankBranchService(BankBranchService bankBranchService) {
		this.bankBranchService = bankBranchService;
	}

	public void setCustomerDetailsService(CustomerDetailsService customerDetailsService) {
		this.customerDetailsService = customerDetailsService;
	}

	public void setFinanceMainService(FinanceMainService financeMainService) {
		this.financeMainService = financeMainService;
	}

	public void setPennyDropService(PennyDropService pennyDropService) {
		this.pennyDropService = pennyDropService;
	}

	@Autowired
	public void setMandateDAO(MandateDAO mandateDAO) {
		this.mandateDAO = mandateDAO;
	}
}
