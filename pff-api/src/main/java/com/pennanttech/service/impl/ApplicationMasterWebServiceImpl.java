package com.pennanttech.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.delegationdeviation.DeviationHelper;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.applicationmaster.ManualDeviation;
import com.pennant.backend.model.applicationmaster.ReasonCode;
import com.pennant.backend.model.applicationmaster.ReasonCodeResponse;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.service.applicationmaster.ManualDeviationService;
import com.pennant.backend.service.applicationmaster.ReasonCodeService;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.ws.exception.ServiceException;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pffws.ApplicationMasterRestService;
import com.pennanttech.pffws.ApplicationMasterSoapService;
import com.pennanttech.ws.model.deviation.ManualDeviationAuthReq;
import com.pennanttech.ws.model.deviation.ManualDeviationAuthorities;
import com.pennanttech.ws.model.deviation.ManualDeviationList;
import com.pennanttech.ws.service.APIErrorHandlerService;

@Service
public class ApplicationMasterWebServiceImpl implements ApplicationMasterRestService, ApplicationMasterSoapService {
	private final Logger logger = LogManager.getLogger(getClass());

	private ReasonCodeService reasonCodeService;
	private ManualDeviationService manualDeviationService;
	private FinanceMainDAO financeMainDAO;
	private DeviationHelper deviationHelper;

	public ApplicationMasterWebServiceImpl() {
		super();
	}

	@Override
	public ReasonCodeResponse getReasonCodeDetails(String reasonTypeCode) throws ServiceException {
		logger.debug(Literal.ENTERING);

		ReasonCodeResponse response = new ReasonCodeResponse();
		List<ReasonCode> reasonDetails = null;

		if (StringUtils.isBlank(reasonTypeCode)) {
			String[] valueParm = new String[1];
			valueParm[0] = "reasonTypeCode";
			response.setReturnStatus(APIErrorHandlerService.getFailedStatus("90502", valueParm));
			return response;
		}
		reasonDetails = reasonCodeService.getReasonDetails(reasonTypeCode);

		if (CollectionUtils.isEmpty(reasonDetails)) {
			String[] valueParm = new String[1];
			valueParm[0] = "reasonTypeCode";
			//no records founds
			response.setReturnStatus(APIErrorHandlerService.getFailedStatus("90266", valueParm));
			return response;
		} else {
			response.setReasonCode(reasonDetails);
			response.setReturnStatus(APIErrorHandlerService.getSuccessStatus());
		}
		logger.debug(Literal.LEAVING);
		return response;
	}

	@Override
	public ManualDeviationList getManualDeviationList(String categorizationCode) throws ServiceException {
		logger.debug(Literal.ENTERING);
		ManualDeviationList response = new ManualDeviationList();

		List<ManualDeviation> deviationList = null;

		if (StringUtils.isBlank(categorizationCode)) {
			String[] valueParm = new String[1];
			valueParm[0] = "categorizationCode";
			response.setReturnStatus(APIErrorHandlerService.getFailedStatus("90502", valueParm));
			return response;
		}
		deviationList = manualDeviationService.getManualDeviation(categorizationCode, "_View");

		if (CollectionUtils.isEmpty(deviationList)) {
			String[] valueParm = new String[1];
			valueParm[0] = "categorizationCode";
			response.setReturnStatus(APIErrorHandlerService.getFailedStatus("90266", valueParm));
			return response;
		} else {
			response.setManualDeviationList(deviationList);
			response.setReturnStatus(APIErrorHandlerService.getSuccessStatus());
		}
		logger.debug(Literal.LEAVING);
		return response;
	}

	@Override
	public ManualDeviationAuthorities getManualDeviationAuthorities(ManualDeviationAuthReq request)
			throws ServiceException {

		logger.debug(Literal.ENTERING);

		ManualDeviationAuthorities response = new ManualDeviationAuthorities();
		FinanceMain financeMain = null;

		List<ValueLabel> delegators = new ArrayList<>();
		// Mandatory validation
		if (StringUtils.isBlank(request.getFinReference())) {
			String[] valueParm = new String[1];
			valueParm[0] = "FinReference";
			response.setReturnStatus(APIErrorHandlerService.getFailedStatus("90502", valueParm));
			return response;
		}
		financeMain = financeMainDAO.getFinanceMainById(request.getFinReference(), "_View", false);
		if (financeMain == null) {
			String[] valueParm = new String[1];
			valueParm[0] = request.getFinReference();
			response.setReturnStatus(APIErrorHandlerService.getFailedStatus("90201", valueParm));
			return response;
		}
		ManualDeviation manualDeviation = manualDeviationService.getManualDeviationByCode(request.getManDevcode(),
				"_View");

		if (manualDeviation == null) {
			String[] valueParm = new String[1];
			valueParm[0] = request.getManDevcode();
			response.setReturnStatus(APIErrorHandlerService.getFailedStatus("90266", valueParm));
			return response;

		} else {
			String delegatorRoles = deviationHelper.getAuthorities(financeMain.getFinType(),
					FinanceConstants.PROCEDT_LIMIT, "MDAAL" + manualDeviation.getSeverity());

			if (StringUtils.isNotBlank(delegatorRoles)) {
				String[] list = delegatorRoles.split(PennantConstants.DELIMITER_COMMA);

				for (String item : list) {
					ValueLabel delegator = new ValueLabel();
					delegator.setLabel(item);
					delegator.setValue(item);
					delegators.add(delegator);
				}
				response.setAuthoritiesList(delegators);
				response.setReturnStatus(APIErrorHandlerService.getSuccessStatus());

			} else {
				String[] valueParm = new String[1];
				valueParm[0] = "ManualDeviation";
				valueParm[0] = "delegatorRoles";
				response.setReturnStatus(APIErrorHandlerService.getFailedStatus("41002", valueParm));
				return response;
			}
		}

		logger.debug(Literal.LEAVING);
		return response;
	}

	@Autowired
	public void setReasonCodeService(ReasonCodeService reasonCodeService) {
		this.reasonCodeService = reasonCodeService;
	}

	@Autowired
	public void setManualDeviationService(ManualDeviationService manualDeviationService) {
		this.manualDeviationService = manualDeviationService;
	}

	@Autowired
	public void setFinanceMainDAO(FinanceMainDAO financeMainDAO) {
		this.financeMainDAO = financeMainDAO;
	}

	@Autowired
	public void setDeviationHelper(DeviationHelper deviationHelper) {
		this.deviationHelper = deviationHelper;
	}

}
