package com.pennanttech.service.impl;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import com.pennant.backend.model.WSReturnStatus;
import com.pennant.backend.model.others.JVPosting;
import com.pennant.backend.service.others.JVPostingService;
import com.pennant.ws.exception.ServiceException;
import com.pennanttech.controller.MiscellaneousServiceController;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pffws.MiscellaneousRestService;
import com.pennanttech.pffws.MiscellaneousSoapService;
import com.pennanttech.ws.model.dashboard.DashBoardRequest;
import com.pennanttech.ws.model.dashboard.DashBoardResponse;
import com.pennanttech.ws.model.eligibility.EligibilityDetail;
import com.pennanttech.ws.model.eligibility.EligibilityDetailResponse;
import com.pennanttech.ws.service.APIErrorHandlerService;

public class MiscellaneousWebServiceImpl implements MiscellaneousRestService, MiscellaneousSoapService {

	private final Logger logger = Logger.getLogger(getClass());
	private MiscellaneousServiceController miscellaneousController;
	private JVPostingService jVPostingService;
	
	// jvposting
	@Override
	public WSReturnStatus createPosting(JVPosting posting) throws ServiceException {
		
		logger.debug(Literal.ENTERING);
		
		WSReturnStatus returnStatus = new WSReturnStatus();
		List<ErrorDetail> validationErrors = jVPostingService.doMiscellaneousValidations(posting);
		if(CollectionUtils.isEmpty(validationErrors))	{
			returnStatus = miscellaneousController.prepareJVPostData(posting);			
		} else	{
			for (ErrorDetail errorDetail : validationErrors) {
				returnStatus = APIErrorHandlerService.getFailedStatus(errorDetail.getCode(), errorDetail.getParameters());
			}
		}
		
		logger.debug(Literal.LEAVING);
		
		return returnStatus;
	}
	
	// dashboard
	@Override
	public DashBoardResponse createDashboard(DashBoardRequest request) throws ServiceException {
		
		logger.debug(Literal.ENTERING);
		
		DashBoardResponse dashboardResponse = miscellaneousController.prepareDashboardConfiguration(request);
		
		logger.debug(Literal.LEAVING);
		
		return dashboardResponse;
	}
	
	@Override
	public EligibilityDetailResponse createEligibilityDetail(EligibilityDetail eligibilityDetail)
			throws ServiceException {
		logger.debug(Literal.ENTERING);

		EligibilityDetailResponse eligibilityResponse = miscellaneousController
				.prepareEligibilityFieldsdata(eligibilityDetail);

		logger.debug(Literal.LEAVING);

		return eligibilityResponse;
	}

	@Autowired
	public void setMiscellaneousController(MiscellaneousServiceController miscellaneousController) {
		this.miscellaneousController = miscellaneousController;
	}
	
	@Autowired
	public void setjVPostingService(JVPostingService jVPostingService) {
		this.jVPostingService = jVPostingService;
	}
	
}
