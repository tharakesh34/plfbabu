package com.pennanttech.service.impl;

import java.math.BigDecimal;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.NumberToEnglishWords;
import com.pennant.app.util.SessionUserDetails;
import com.pennant.backend.dao.customermasters.CustomerDAO;
import com.pennant.backend.model.WSReturnStatus;
import com.pennant.backend.model.mandate.Mandate;
import com.pennant.backend.service.mandate.MandateService;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.pff.api.service.AbstractService;
import com.pennant.validation.SaveValidationGroup;
import com.pennant.validation.UpdateValidationGroup;
import com.pennant.validation.ValidationUtility;
import com.pennant.ws.exception.ServiceException;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.model.LoggedInUser;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pffws.MandateRestService;
import com.pennanttech.pffws.MandateSoapService;
import com.pennanttech.util.APIConstants;
import com.pennanttech.ws.model.mandate.MandateDetial;

@Service
public class MandateWebServiceImpl extends AbstractService implements MandateRestService, MandateSoapService {
	private ValidationUtility validationUtility;
	private MandateService mandateService;
	private CustomerDAO customerDAO;

	@Override
	public Mandate createMandate(Mandate mandate) throws ServiceException {
		logger.debug(Literal.ENTERING);

		validationUtility.validate(mandate, SaveValidationGroup.class);
		mandate.setSourceId(APIConstants.FINSOURCE_ID_API);

		logKeyFields(mandate.getCustCIF(), mandate.getAccNumber(), mandate.getAccHolderName());

		LoggedInUser loggedInUser = SessionUserDetails.getUserDetails(SessionUserDetails.getLogiedInUser());
		mandate.setUserDetails(loggedInUser);

		Mandate response = mandateService.createMandates(mandate);

		ErrorDetail error = response.getError();
		if (error != null) {
			response.setReturnStatus(getFailedReturnStatus(error.getCode(), error.getError()));
		} else {
			response.setReturnStatus(getSuccessStatus());
		}

		doEmptyResponseObject(response);

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
		return mandateService.getMandate(mandateID);
	}

	@Override
	public WSReturnStatus updateMandate(Mandate mandate) throws ServiceException {
		logger.debug(Literal.ENTERING);

		validationUtility.validate(mandate, UpdateValidationGroup.class);
		mandate.setSourceId(APIConstants.FINSOURCE_ID_API);

		logKeyFields(mandate.getCustCIF(), mandate.getAccNumber(), mandate.getAccHolderName());

		logReference(String.valueOf(mandate.getMandateID()));

		LoggedInUser loggedInUser = SessionUserDetails.getUserDetails(SessionUserDetails.getLogiedInUser());
		mandate.setUserDetails(loggedInUser);

		ErrorDetail error = mandateService.updateMandate(mandate);

		if (error != null) {
			return getFailedReturnStatus(error.getCode(), error.getError());
		}

		return getSuccessStatus();
	}

	@Override
	public WSReturnStatus deleteMandate(long mandateID) throws ServiceException {
		logger.debug(Literal.ENTERING);

		if (mandateID < 0) {
			validationUtility.fieldLevelException();
		}

		logReference(String.valueOf(mandateID));

		LoggedInUser loggedInUser = SessionUserDetails.getUserDetails(SessionUserDetails.getLogiedInUser());

		ErrorDetail error = mandateService.deleteMandate(mandateID, loggedInUser);

		if (error != null) {
			return getFailedReturnStatus(error.getCode(), error.getError());
		}

		return getSuccessStatus();
	}

	@Override
	public MandateDetial getMandates(String cif) throws ServiceException {
		logger.debug(Literal.ENTERING);

		if (StringUtils.isBlank(cif)) {
			validationUtility.fieldLevelException();
		}

		logReference(cif);

		return getMandateDetails(cif);
	}

	@Override
	public WSReturnStatus loanMandateSwapping(MandateDetial mandate) throws ServiceException {
		logger.debug(Literal.ENTERING);

		logReference(mandate.getFinReference());

		String finReference = mandate.getFinReference();
		Long oldMandateId = mandate.getOldMandateId();
		Long newMandateId = mandate.getNewMandateId();

		ErrorDetail error = mandateService.loanMandateSwapping(finReference, oldMandateId, newMandateId);

		if (error != null) {
			return getFailedReturnStatus(error.getCode(), error.getError());
		}

		return getSuccessStatus();
	}

	@Override
	public Mandate approveMandate(Mandate mandate) throws ServiceException {
		logger.debug(Literal.ENTERING);

		validationUtility.validate(mandate, SaveValidationGroup.class);
		mandate.setSourceId(APIConstants.FINSOURCE_ID_API);

		logKeyFields(mandate.getCustCIF(), mandate.getAccNumber(), mandate.getAccHolderName());

		LoggedInUser loggedInUser = SessionUserDetails.getUserDetails(SessionUserDetails.getLogiedInUser());
		mandate.setUserDetails(loggedInUser);

		Mandate response = mandateService.approveMandate(mandate);

		ErrorDetail error = response.getError();
		if (error != null) {
			mandate.setReturnStatus(getFailedReturnStatus(error.getCode(), error.getError()));
		} else {
			response.setReturnStatus(getSuccessStatus());
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

		mandate.setSourceId(APIConstants.FINSOURCE_ID_API);

		LoggedInUser loggedInUser = SessionUserDetails.getUserDetails(SessionUserDetails.getLogiedInUser());
		mandate.setUserDetails(loggedInUser);
		ErrorDetail error = mandateService.updateStatus(mandate);

		if (error != null) {
			return getFailedReturnStatus(error.getCode(), error.getError());
		}

		return getSuccessStatus();
	}

	@Override
	public WSReturnStatus updateApprovedMandate(Mandate mandate) throws ServiceException {
		logger.debug(Literal.ENTERING);

		logKeyFields(String.valueOf(mandate.getMandateID()), mandate.getMandateRef());
		mandate.setSourceId(APIConstants.FINSOURCE_ID_API);

		LoggedInUser loggedInUser = SessionUserDetails.getUserDetails(SessionUserDetails.getLogiedInUser());
		mandate.setUserDetails(loggedInUser);

		ErrorDetail error = mandateService.updateApprovedMandate(mandate);

		if (error != null) {
			return getFailedReturnStatus(error.getCode(), error.getError());
		}

		logger.debug(Literal.LEAVING);
		return getSuccessStatus();
	}

	private MandateDetial getMandateDetails(String cif) {
		logger.debug(Literal.ENTERING);

		long custID = customerDAO.getCustIDByCIF(cif);
		if (custID <= 0) {
			MandateDetial response = new MandateDetial();
			response.setReturnStatus(getFailedStatus("90101", cif));

			return response;
		}

		MandateDetial response = new MandateDetial();
		List<Mandate> mandates = mandateService.getMandatesByCif(cif);

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
			response.setReturnStatus(getFailedStatus());
		}

		logger.debug(Literal.LEAVING);
		return response;
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

	@Autowired
	public void setValidationUtility(ValidationUtility validationUtility) {
		this.validationUtility = validationUtility;
	}

	@Autowired
	public void setMandateService(MandateService mandateService) {
		this.mandateService = mandateService;
	}

	@Autowired
	public void setCustomerDAO(CustomerDAO customerDAO) {
		this.customerDAO = customerDAO;
	}
}
