package com.pennanttech.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import com.pennant.backend.dao.applicationmaster.CheckListDetailDAO;
import com.pennant.backend.dao.lmtmasters.FinanceReferenceDetailDAO;
import com.pennant.backend.model.WSReturnStatus;
import com.pennant.backend.model.applicationmaster.CheckListDetail;
import com.pennant.backend.model.lmtmasters.FinanceReferenceDetail;
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
import com.pennanttech.ws.model.miscellaneous.CheckListDetailsRespons;
import com.pennanttech.ws.model.miscellaneous.CheckListResponse;
import com.pennanttech.ws.model.miscellaneous.LoanTypeMiscRequest;
import com.pennanttech.ws.service.APIErrorHandlerService;

public class MiscellaneousWebServiceImpl implements MiscellaneousRestService, MiscellaneousSoapService {

	private final Logger logger = Logger.getLogger(getClass());
	private MiscellaneousServiceController miscellaneousController;
	private JVPostingService jVPostingService;
	private FinanceReferenceDetailDAO financeReferenceDetailDAO;
	private CheckListDetailDAO checkListDetailDAO;

	public MiscellaneousWebServiceImpl() {
		super();
	}

	// jvposting
	@Override
	public WSReturnStatus createPosting(JVPosting posting) throws ServiceException {

		logger.debug(Literal.ENTERING);

		WSReturnStatus returnStatus = new WSReturnStatus();
		List<ErrorDetail> validationErrors = jVPostingService.doMiscellaneousValidations(posting);
		if (CollectionUtils.isEmpty(validationErrors)) {
			returnStatus = miscellaneousController.prepareJVPostData(posting);
		} else {
			for (ErrorDetail errorDetail : validationErrors) {
				returnStatus = APIErrorHandlerService.getFailedStatus(errorDetail.getCode(),
						errorDetail.getParameters());
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

	@Override
	public List<CheckListResponse> getCheckList(LoanTypeMiscRequest loanTypeMiscRequest) throws ServiceException {
		logger.debug(Literal.ENTERING);
		List<CheckListResponse> listResponse = new ArrayList<>();
		CheckListResponse response = null;
		List<CheckListDetailsRespons> listCheckListResponse = new ArrayList<>();
		List<CheckListDetail> listCheckListDetail = null;
		if (StringUtils.isEmpty(loanTypeMiscRequest.getFinType())) {
			response = new CheckListResponse();
			String[] valueParm = new String[1];
			valueParm[0] = "finType";
			response.setReturnStatus(APIErrorHandlerService.getFailedStatus("90502", valueParm));
			listResponse.add(response);
			return listResponse;

		}
		if (StringUtils.isEmpty(loanTypeMiscRequest.getStage())) {
			response = new CheckListResponse();
			String[] valueParm = new String[1];
			valueParm[0] = "stage";
			response.setReturnStatus(APIErrorHandlerService.getFailedStatus("90502", valueParm));
			listResponse.add(response);
			return listResponse;

		}
		List<FinanceReferenceDetail> FinanceReferenceDetail = financeReferenceDetailDAO.getFinanceRefListByFinType(
				loanTypeMiscRequest.getFinType(), loanTypeMiscRequest.getStage(), "_tqview");
		if (FinanceReferenceDetail.size() > 0 && FinanceReferenceDetail != null) {
			for (FinanceReferenceDetail finRefDetail : FinanceReferenceDetail) {
				listCheckListDetail = checkListDetailDAO.getCheckListDetailByChkList(finRefDetail.getFinRefId(),
						"_View");
				if (listCheckListDetail != null) {
					finRefDetail.setLovDesccheckListDetail(listCheckListDetail);
				}
			}
			for (FinanceReferenceDetail financeReferenceDetailList : FinanceReferenceDetail) {
				response = new CheckListResponse();
				response.setFinRefId(financeReferenceDetailList.getFinRefId());
				response.setLovDescRefDesc(financeReferenceDetailList.getLovDescRefDesc());
				response.setMandInputInStage(financeReferenceDetailList.getMandInputInStage());
				response.setLovDescCheckMinCount(financeReferenceDetailList.getLovDescCheckMinCount());
				response.setLovDescCheckMaxCount(financeReferenceDetailList.getLovDescCheckMaxCount());

				for (CheckListDetail checkListDetail : financeReferenceDetailList.getLovDesccheckListDetail()) {
					CheckListDetailsRespons checkListResponse = new CheckListDetailsRespons();
					checkListResponse.setAnsDesc(checkListDetail.getAnsDesc());
					checkListResponse.setDocRequired(checkListDetail.isDocRequired());
					checkListResponse.setDocType(checkListDetail.getDocType());
					checkListResponse.setRemarksMand(checkListDetail.isRemarksMand());
					checkListResponse.setCheckListId(checkListDetail.getCheckListId());
					listCheckListResponse.add(checkListResponse);
				}
				response.setLovDesccheckListDetail(listCheckListResponse);
				response.setReturnStatus(APIErrorHandlerService.getSuccessStatus());
				listResponse.add(response);

			}
			return listResponse;
		} else {

			response = new CheckListResponse();
			String[] valueParm = new String[2];
			valueParm[0] = loanTypeMiscRequest.getFinType();
			valueParm[1] = loanTypeMiscRequest.getStage();
			WSReturnStatus status = APIErrorHandlerService.getFailedStatus("90501", valueParm);
			// status.setReturnText(status.getReturnText().replace("Invalid",
			// "Required"));
			response.setReturnStatus(status);
			listResponse.add(response);
			return listResponse;
		}

	}
	@Autowired
	public void setMiscellaneousController(MiscellaneousServiceController miscellaneousController) {
		this.miscellaneousController = miscellaneousController;
	}

	@Autowired
	public void setjVPostingService(JVPostingService jVPostingService) {
		this.jVPostingService = jVPostingService;
	}
	
	
	@Autowired
	public void setFinanceReferenceDetailDAO(FinanceReferenceDetailDAO financeReferenceDetailDAO) {
		this.financeReferenceDetailDAO = financeReferenceDetailDAO;
	}

	@Autowired
	public void setCheckListDetailDAO(CheckListDetailDAO checkListDetailDAO) {
		this.checkListDetailDAO = checkListDetailDAO;
	}

	

}
