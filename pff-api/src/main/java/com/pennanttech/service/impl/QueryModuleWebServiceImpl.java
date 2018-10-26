/**
 * 
 */
package com.pennanttech.service.impl;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.pennant.backend.model.WSReturnStatus;
import com.pennant.backend.model.loanquery.QueryDetail;
import com.pennant.backend.service.loanquery.QueryDetailService;
import com.pennant.validation.QueryDetailGroup;
import com.pennant.validation.ValidationUtility;
import com.pennant.ws.exception.ServiceException;
import com.pennanttech.controller.FinServiceInstController;
import com.pennanttech.controller.QueryModuleController;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pffws.QueryModuleRestService;
import com.pennanttech.pffws.QueryModuleSoapService;
import com.pennanttech.ws.service.APIErrorHandlerService;

/**
 * @author srikanth.m
 *
 */
public class QueryModuleWebServiceImpl implements QueryModuleRestService,QueryModuleSoapService{
	private static final Logger logger = Logger.getLogger(FinInstructionServiceImpl.class);
	private QueryModuleController queryModuleController;
	private ValidationUtility validationUtility;
	private QueryDetailService	queryDetailService;

	
	@Override
	public WSReturnStatus updateQueryRequest(QueryDetail queryDetail) throws ServiceException {
		logger.debug(Literal.ENTERING);

		// bean validations
		 validationUtility.validate(queryDetail, QueryDetailGroup.class);
		 WSReturnStatus returnStatus = null;
		 QueryDetail detail = queryDetailService.getQueryDetail(queryDetail.getId());

		 if(detail==null) {
			  returnStatus = new WSReturnStatus();
				String[] paramValue = new String[2];
				paramValue[0] = "Id:"+queryDetail.getId();
				return APIErrorHandlerService.getFailedStatus("90266",paramValue);
			 
		 }

		 if(!StringUtils.equals(detail.getStatus(), "Open")) {
			 returnStatus = new WSReturnStatus();
				String[] paramValue = new String[2];
				paramValue[0] = "Update";
				paramValue[1] = "Open Status";
				return APIErrorHandlerService.getFailedStatus("90298",paramValue);
		 }
		// for logging purpose
		APIErrorHandlerService.logReference(queryDetail.getFinReference());

		if (!StringUtils.equals(queryDetail.getStatus(), "Resolve")) {
			 returnStatus = new WSReturnStatus();
			String[] paramValue = new String[2];
			paramValue[0] = "Status: "+queryDetail.getStatus();
			paramValue[1] = "Update";
			return APIErrorHandlerService.getFailedStatus("90204",paramValue);
		}
		logger.debug(Literal.LEAVING);

		return queryModuleController.doQueryUpdate(queryDetail);

	}
	public QueryModuleController getQueryModuleController() {
		return queryModuleController;
	}
	@Autowired
	public void setQueryModuleController(QueryModuleController queryModuleController) {
		this.queryModuleController = queryModuleController;
	}

	public ValidationUtility getValidationUtility() {
		return validationUtility;
	}
	@Autowired
	public void setValidationUtility(ValidationUtility validationUtility) {
		this.validationUtility = validationUtility;
	}
	public QueryDetailService getQueryDetailService() {
		return queryDetailService;
	}
	@Autowired
	public void setQueryDetailService(QueryDetailService queryDetailService) {
		this.queryDetailService = queryDetailService;
	}
}
