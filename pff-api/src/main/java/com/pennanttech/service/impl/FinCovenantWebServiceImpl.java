package com.pennanttech.service.impl;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pennant.backend.dao.documentdetails.DocumentDetailsDAO;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.model.WSReturnStatus;
import com.pennant.backend.model.documentdetails.DocumentDetails;
import com.pennant.backend.model.finance.FinCovenantType;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.service.finance.FinCovenantTypeService;
import com.pennant.ws.exception.ServiceException;
import com.pennanttech.controller.FinCovenantController;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.pff.document.DocumentCategories;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pffws.FinCovenantRestService;
import com.pennanttech.pffws.FinCovenantSoapService;
import com.pennanttech.ws.model.finance.FinCovenantResponse;
import com.pennanttech.ws.service.APIErrorHandlerService;

@Service
public class FinCovenantWebServiceImpl implements FinCovenantRestService, FinCovenantSoapService {
	private Logger logger = LogManager.getLogger(FinCovenantWebServiceImpl.class);

	private FinCovenantTypeService finCovenantTypeService;
	private FinCovenantController finCovenantController;
	private DocumentDetailsDAO documentDetailsDAO;
	private FinanceMainDAO financeMainDAO;

	@Override
	public WSReturnStatus addFinCovenant(FinanceDetail fd) throws ServiceException {
		logger.debug(Literal.ENTERING);

		String finReference = fd.getFinReference();

		if (StringUtils.isBlank(finReference)) {
			String valueParm[] = new String[1];
			valueParm[0] = "finReference";
			return APIErrorHandlerService.getFailedStatus("90502", valueParm);
		}

		APIErrorHandlerService.logReference(finReference);
		FinanceMain fm = null;

		fm = financeMainDAO.getFinanceMainByRef(finReference, "_View", false);
		if (fm == null) {
			String[] valueParm = new String[1];
			valueParm[0] = finReference;
			return APIErrorHandlerService.getFailedStatus("90201", valueParm);
		}

		if (!fm.isFinIsActive() || StringUtils.isNotEmpty(fm.getRcdMaintainSts())) {
			String[] valueParm = new String[1];
			valueParm[0] = finReference;
			return APIErrorHandlerService.getFailedStatus("90201", valueParm);
		}

		if (CollectionUtils.isEmpty(fd.getCovenantTypeList())) {
			String valueParm[] = new String[1];
			valueParm[0] = "covenants";
			return APIErrorHandlerService.getFailedStatus("90502", valueParm);
		}

		FinScheduleData schdData = fd.getFinScheduleData();
		schdData.setFinanceMain(fm);

		List<ErrorDetail> edList = finCovenantTypeService.doCovenantValidation(fd, false);
		if (CollectionUtils.isNotEmpty(edList)) {
			for (ErrorDetail ed : edList) {
				return APIErrorHandlerService.getFailedStatus(ed.getCode(), ed.getError());
			}
		}

		WSReturnStatus response = null;
		if (CollectionUtils.isEmpty(edList)) {
			response = finCovenantController.addFinCovenant(fd);
		}
		logger.debug(Literal.LEAVING);
		return response;
	}

	@Override
	public WSReturnStatus updateFinCovenant(FinanceDetail financeDetail) throws ServiceException {
		logger.debug(Literal.ENTERING);

		String finReference = financeDetail.getFinReference();
		if (StringUtils.isBlank(finReference)) {
			String valueParm[] = new String[1];
			valueParm[0] = "finReference";
			return APIErrorHandlerService.getFailedStatus("90502", valueParm);
		}
		// for logging purpose
		APIErrorHandlerService.logReference(finReference);
		FinanceMain financeMain = null;
		financeMain = financeMainDAO.getFinanceMain(finReference);
		if (financeMain == null) {
			String[] valueParm = new String[1];
			valueParm[0] = finReference;
			return APIErrorHandlerService.getFailedStatus("90201", valueParm);
		}

		if (!financeMain.isFinIsActive() || StringUtils.isNotEmpty(financeMain.getRcdMaintainSts())) {
			String[] valueParm = new String[1];
			valueParm[0] = finReference;
			return APIErrorHandlerService.getFailedStatus("90201", valueParm);
		}

		if (CollectionUtils.isEmpty(financeDetail.getCovenantTypeList())) {
			String valueParm[] = new String[1];
			valueParm[0] = "covenants";
			return APIErrorHandlerService.getFailedStatus("90502", valueParm);
		}
		List<ErrorDetail> errorDetails = null;
		financeDetail.getFinScheduleData().setFinanceMain(financeMain);
		errorDetails = finCovenantTypeService.doCovenantValidation(financeDetail, true);
		if (CollectionUtils.isNotEmpty(errorDetails)) {
			for (ErrorDetail errorDetail : errorDetails) {
				return APIErrorHandlerService.getFailedStatus(errorDetail.getCode(), errorDetail.getError());
			}
		}

		WSReturnStatus response = null;
		if (CollectionUtils.isEmpty(errorDetails)) {
			response = finCovenantController.updateFinCovenant(financeDetail);
		}
		logger.debug(Literal.LEAVING);
		return response;
	}

	@Override
	public WSReturnStatus deleteFinCovenant(FinanceDetail financeDetail) throws ServiceException {
		logger.debug(Literal.ENTERING);

		String finReference = financeDetail.getFinReference();
		if (StringUtils.isBlank(finReference)) {
			String[] valueParm = new String[1];
			valueParm[0] = "finReference";
			return APIErrorHandlerService.getFailedStatus("90502", valueParm);
		}

		// for logging purpose
		APIErrorHandlerService.logReference(finReference);
		FinanceMain fm = null;
		fm = financeMainDAO.getFinanceMain(finReference);

		if (fm == null) {
			String[] valueParm = new String[1];
			valueParm[0] = finReference;
			return APIErrorHandlerService.getFailedStatus("90201", valueParm);
		}

		if (!fm.isFinIsActive() || StringUtils.isNotEmpty(fm.getRcdMaintainSts())) {
			String[] valueParm = new String[1];
			valueParm[0] = finReference;
			return APIErrorHandlerService.getFailedStatus("90201", valueParm);
		}

		List<FinCovenantType> covenantTypeList = financeDetail.getCovenantTypeList();
		if (CollectionUtils.isEmpty(covenantTypeList)) {
			String valueParm[] = new String[1];
			valueParm[0] = "covenants";
			return APIErrorHandlerService.getFailedStatus("90502", valueParm);
		}

		Long finID = financeMainDAO.getFinID(finReference, TableType.TEMP_TAB);

		boolean referenceExitsinLQ = false;

		if (finID != null) {
			referenceExitsinLQ = true;
		}

		for (FinCovenantType finCovenantType : covenantTypeList) {
			String covenantType = finCovenantType.getCovenantType();
			if (StringUtils.isBlank(covenantType)) {
				String[] valueParm = new String[1];
				valueParm[0] = "covenantType";
				return APIErrorHandlerService.getFailedStatus("90502", valueParm);
			}
			FinCovenantType finCovenant = finCovenantTypeService.getFinCovenantTypeById(finReference, covenantType,
					"_View");
			if (finCovenant == null) {
				String[] valueParm = new String[1];
				valueParm[0] = "covenantType: " + covenantType;
				return APIErrorHandlerService.getFailedStatus("90266", valueParm);
			}
			DocumentDetails documentDetails = documentDetailsDAO.getDocumentDetails(finReference, covenantType,
					DocumentCategories.FINANCE.getKey(), "_View");
			if (!referenceExitsinLQ && documentDetails != null) {
				if (StringUtils.equals(covenantType, documentDetails.getDocCategory())) {
					String[] valueParm = new String[1];
					valueParm[0] = covenantType; // Document Already Captured.Not Allowed to Maintain.
					return APIErrorHandlerService.getFailedStatus("CVN002", valueParm);
				}
			}
		}
		WSReturnStatus response = finCovenantController.deleteFinCovenant(financeDetail, referenceExitsinLQ);
		logger.debug(Literal.LEAVING);
		return response;
	}

	@Override
	public FinCovenantResponse getFinCovenants(String finReference) throws ServiceException {
		logger.debug(Literal.ENTERING);
		FinCovenantResponse response = new FinCovenantResponse();

		// for logging purpose
		APIErrorHandlerService.logReference(finReference);
		if (StringUtils.isBlank(finReference)) {
			String[] valueParm = new String[1];
			valueParm[0] = "finReference";
			response.setReturnStatus(APIErrorHandlerService.getFailedStatus("90502", valueParm));
			return response;
		}
		FinanceMain fm = null;
		fm = financeMainDAO.getFinanceMain(finReference);
		if (fm == null) {
			String[] valueParm = new String[1];
			valueParm[0] = finReference;
			response.setReturnStatus(APIErrorHandlerService.getFailedStatus("90201", valueParm));
			return response;
		}

		if (!fm.isFinIsActive() || StringUtils.isNotEmpty(fm.getRcdMaintainSts())) {
			String[] valueParm = new String[1];
			valueParm[0] = finReference;
			response.setReturnStatus(APIErrorHandlerService.getFailedStatus("90201", valueParm));
			return response;
		}
		List<FinCovenantType> covenantTypeList = null;
		try {
			covenantTypeList = finCovenantTypeService.getFinCovenantTypeById(finReference, "_View", false);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
			APIErrorHandlerService.logUnhandledException(e);
			response.setReturnStatus(APIErrorHandlerService.getFailedStatus());
			return response;
		}
		if (CollectionUtils.isEmpty(covenantTypeList)) {
			String[] valueParm = new String[1];
			valueParm[0] = finReference;
			response.setReturnStatus(APIErrorHandlerService.getFailedStatus("90266", valueParm));
			return response;
		} else {
			response.setFinCovenantList(covenantTypeList);
			response.setReturnStatus(APIErrorHandlerService.getSuccessStatus());
		}
		logger.debug(Literal.LEAVING);
		return response;
	}

	@Autowired
	public void setFinCovenantTypeService(FinCovenantTypeService finCovenantTypeService) {
		this.finCovenantTypeService = finCovenantTypeService;
	}

	@Autowired
	public void setFinCovenantController(FinCovenantController finCovenantController) {
		this.finCovenantController = finCovenantController;
	}

	@Autowired
	public void setDocumentDetailsDAO(DocumentDetailsDAO documentDetailsDAO) {
		this.documentDetailsDAO = documentDetailsDAO;
	}

	@Autowired
	public void setFinanceMainDAO(FinanceMainDAO financeMainDAO) {
		this.financeMainDAO = financeMainDAO;
	}

}
