package com.pennanttech.service.impl;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pennant.backend.dao.bmtmasters.ProductDAO;
import com.pennant.backend.model.bmtmasters.Product;
import com.pennant.backend.model.solutionfactory.StepPolicyHeader;
import com.pennant.backend.service.rmtmasters.FinanceTypeService;
import com.pennant.backend.service.solutionfactory.StepPolicyService;
import com.pennant.validation.ValidationUtility;
import com.pennant.ws.exception.ServiceException;
import com.pennanttech.controller.FinanceTypeController;
import com.pennanttech.pffws.FinanceTypeRestService;
import com.pennanttech.pffws.FinanceTypeSoapService;
import com.pennanttech.ws.model.financetype.FinanceTypeRequest;
import com.pennanttech.ws.model.financetype.FinanceTypeResponse;
import com.pennanttech.ws.model.financetype.ProductType;
import com.pennanttech.ws.service.APIErrorHandlerService;

@Service
public class FinanceTypeWebServiceImpl implements FinanceTypeSoapService,FinanceTypeRestService {

	private static final Logger logger = Logger.getLogger(FinanceTypeWebServiceImpl.class);

	private FinanceTypeController financeTypeController;
	private ValidationUtility validationUtility;
	private FinanceTypeService financeTypeService;
	private StepPolicyService stepPolicyService;
	private ProductDAO productDAO;

	@Override
	public FinanceTypeResponse getFinanceTypeDetails(FinanceTypeRequest finTypeReq) throws ServiceException {
		logger.debug("Entering");

		if (finTypeReq != null) {
			// Mandatory validation
			if (StringUtils.isBlank(finTypeReq.getFinType())) {
				validationUtility.fieldLevelException();
			}
			// for logging purpose
			APIErrorHandlerService.logReference(finTypeReq.getFinType());
			// validate financeType
			String finType = finTypeReq.getFinType();
			int count = financeTypeService.getFinanceTypeCountById(finType);

			FinanceTypeResponse response = new FinanceTypeResponse();
			if (count > 0) {
				response = financeTypeController.getFinanceTypeDetails(finTypeReq,false);
				response.setStartDate(null);
				response.setEndDate(null);
			} else {
				String[] valueParm = new String[1];
				valueParm[0] = finType;
				response.setReturnStatus(APIErrorHandlerService.getFailedStatus("90202", valueParm));
			}
			
			logger.debug("Leaving");
			return response;
		} else {
			FinanceTypeResponse response = new FinanceTypeResponse();
			response.setReturnStatus(APIErrorHandlerService.getFailedStatus());
			return response;
		}
	}

	/**
	 * Fetch Step Policy details from system.
	 * 
	 * @param policyCode
	 */
	@Override
	public StepPolicyHeader getStepPolicyDetails(String policyCode) throws ServiceException {
		logger.debug("Entering");
		
		// Mandatory validation
		if (StringUtils.isBlank(policyCode)) {
			validationUtility.fieldLevelException();
		}

		// for logging purpose
		APIErrorHandlerService.logReference(policyCode);

		StepPolicyHeader response = new StepPolicyHeader();
		
		// validate policyCode
		StepPolicyHeader header = stepPolicyService.getStepPolicyHeaderById(policyCode);
		if(header != null) {
			response = financeTypeController.getStepPolicyDetails(policyCode);
		} else {
			String[] valueParm = new String[1];
			valueParm[0] = policyCode;
			response.setReturnStatus(APIErrorHandlerService.getFailedStatus("90501", valueParm));
		}
		
		logger.debug("Leaving");
		return response;
	}
	/**
	 * Fetch Product details from system.
	 * 
	 * @param productCode
	 */
	@Override
	public ProductType getLoanTypes(String productCode) throws ServiceException {
			logger.debug("Entering");
		
		// Mandatory validation
		if (StringUtils.isBlank(productCode)) {
			validationUtility.fieldLevelException();
		}

		// for logging purpose
		APIErrorHandlerService.logReference(productCode);

		ProductType response = new ProductType();
		
		// validate productCode
		Product product = productDAO.getProductByID(productCode, productCode, "_AView");
		if(product != null) {
			response = financeTypeController.getLoanTypes(productCode);
		} else {
			String[] valueParm = new String[1];
			valueParm[0] = productCode;
			response.setReturnStatus(APIErrorHandlerService.getFailedStatus("90501", valueParm));
		}
		
		logger.debug("Leaving");
		return response;
	}
	@Autowired
	public void setFinanceTypeController(FinanceTypeController financeTypeController) {
		this.financeTypeController = financeTypeController;
	}

	@Autowired
	public void setValidationUtility(ValidationUtility validationUtility) {
		this.validationUtility = validationUtility;
	}

	@Autowired
	public void setFinanceTypeService(FinanceTypeService financeTypeService) {
		this.financeTypeService = financeTypeService;
	}
	
	@Autowired
	public void setStepPolicyService(StepPolicyService stepPolicyService) {
		this.stepPolicyService = stepPolicyService;
	}
	@Autowired
	public void setProductDAO(ProductDAO productDAO) {
		this.productDAO = productDAO;
	}
}
