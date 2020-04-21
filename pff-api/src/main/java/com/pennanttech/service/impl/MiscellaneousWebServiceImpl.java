package com.pennanttech.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.applicationmaster.CheckListDetailDAO;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.dao.finance.covenant.CovenantTypeDAO;
import com.pennant.backend.dao.lmtmasters.FinanceReferenceDetailDAO;
import com.pennant.backend.model.WSReturnStatus;
import com.pennant.backend.model.applicationmaster.CheckListDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.covenant.Covenant;
import com.pennant.backend.model.finance.covenant.CovenantType;
import com.pennant.backend.model.lmtmasters.FinanceReferenceDetail;
import com.pennant.backend.model.others.JVPosting;
import com.pennant.backend.service.finance.covenant.CovenantsService;
import com.pennant.backend.service.others.JVPostingService;
import com.pennant.backend.util.SMTParameterConstants;
import com.pennant.ws.exception.ServiceException;
import com.pennanttech.controller.MiscellaneousServiceController;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pffws.MiscellaneousRestService;
import com.pennanttech.pffws.MiscellaneousSoapService;
import com.pennanttech.ws.model.dashboard.DashBoardRequest;
import com.pennanttech.ws.model.dashboard.DashBoardResponse;
import com.pennanttech.ws.model.eligibility.EligibilityDetail;
import com.pennanttech.ws.model.eligibility.EligibilityDetailResponse;
import com.pennanttech.ws.model.finance.EligibilitySummaryResponse;
import com.pennanttech.ws.model.miscellaneous.CheckListDetailsRespons;
import com.pennanttech.ws.model.miscellaneous.CheckListResponse;
import com.pennanttech.ws.model.miscellaneous.CovenantResponse;
import com.pennanttech.ws.model.miscellaneous.LoanTypeMiscRequest;
import com.pennanttech.ws.service.APIErrorHandlerService;

public class MiscellaneousWebServiceImpl implements MiscellaneousRestService, MiscellaneousSoapService {

	private final Logger logger = Logger.getLogger(getClass());
	private MiscellaneousServiceController miscellaneousController;
	private JVPostingService jVPostingService;
	private FinanceReferenceDetailDAO financeReferenceDetailDAO;
	private CheckListDetailDAO checkListDetailDAO;
	private CovenantsService covenantsService;
	private FinanceMainDAO financeMainDAO;
	private CovenantTypeDAO covenantTypeDAO;

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
			response.setReturnStatus(status);
			listResponse.add(response);
			return listResponse;
		}

	}

	// Covenants Docs
	@Override
	public CovenantResponse getCovenantDocs(String finReference) throws ServiceException {
		logger.debug(Literal.ENTERING);

		CovenantResponse response = new CovenantResponse();
		List<Covenant> covenantList = null;
		if (StringUtils.isBlank(finReference)) {
			String[] valueParm = new String[1];
			valueParm[0] = "finReference";
			response.setReturnStatus(APIErrorHandlerService.getFailedStatus("90502", valueParm));
			return response;
		} else {
			int count = financeMainDAO.getFinanceCountById(finReference, "_View", false);
			if (count <= 0) {
				String[] valueParm = new String[1];
				valueParm[0] = finReference;
				response.setReturnStatus(APIErrorHandlerService.getFailedStatus("90201", valueParm));
				return response;
			}
		}
		if (StringUtils.equals(SysParamUtil.getValueAsString(SMTParameterConstants.NEW_COVENANT_MODULE), "Y")) {
			covenantList = covenantsService.getCovenants(finReference, "Loan", TableType.VIEW);
			List<CovenantType> covenantTypeList = new ArrayList<>();
			for (Covenant covenant : covenantList) {
				if (!covenant.isDocumentReceived()) {

					CovenantType covType = new CovenantType();
					covType = covenantTypeDAO.getCovenantType(covenant.getCovenantTypeId(), "");
					if (covType != null) {
						covenantTypeList.add(covType);
					}

				}
			}
			response.setCovenantDocuments(covenantTypeList);
		} else {
			/*
			 * List<FinCovenantType> finCovenantTypeById = finCovenantTypeService.getFinCovenantTypeById(finReference,
			 * "_View", false);
			 */
		}

		if (CollectionUtils.isEmpty(response.geCovenantTypes())) {
			String[] valueParm = new String[1];
			valueParm[0] = finReference;
			response.setReturnStatus(APIErrorHandlerService.getFailedStatus("90266", valueParm));
			return response;
		}
		return response;
	}

	@Override
	public EligibilitySummaryResponse getEligibility(LoanTypeMiscRequest loanTypeMiscRequest) throws ServiceException {
		logger.debug(Literal.ENTERING);

		EligibilitySummaryResponse summaryReponse = new EligibilitySummaryResponse();
		WSReturnStatus returnStatus = new WSReturnStatus();
		FinanceMain finMian = null;
		if (StringUtils.isBlank(loanTypeMiscRequest.getFinReference())) {
			String[] valueParm = new String[1];
			valueParm[0] = "FinReference";
			returnStatus = APIErrorHandlerService.getFailedStatus("90502", valueParm);
			summaryReponse.setReturnStatus(returnStatus);
			return summaryReponse;
		} else {
			finMian = financeMainDAO.getFinanceMainById(loanTypeMiscRequest.getFinReference(), "_View", false);
			if (finMian == null) {
				String[] valueParm = new String[1];
				valueParm[0] = loanTypeMiscRequest.getFinReference();
				returnStatus = APIErrorHandlerService.getFailedStatus("90201", valueParm);
				summaryReponse.setReturnStatus(returnStatus);
				return summaryReponse;
			} else {
				if (!StringUtils.equals(loanTypeMiscRequest.getStage(), finMian.getNextRoleCode())) {
					String[] valueParm = new String[2];
					valueParm[0] = "CurrentStage: " + loanTypeMiscRequest.getStage();
					valueParm[1] = finMian.getNextRoleCode();
					returnStatus = APIErrorHandlerService.getFailedStatus("90337", valueParm);
					summaryReponse.setReturnStatus(returnStatus);
					return summaryReponse;
				}
			}
		}
		if (StringUtils.isBlank(loanTypeMiscRequest.getStage())) {
			String[] valueParm = new String[1];
			valueParm[0] = "currentStage";
			returnStatus = APIErrorHandlerService.getFailedStatus("90502", valueParm);
			summaryReponse.setReturnStatus(returnStatus);
			return summaryReponse;
		}
		summaryReponse = miscellaneousController.getEligibility(finMian, loanTypeMiscRequest);

		logger.debug(Literal.LEAVING);
		return summaryReponse;
	}

	@Override
	public EligibilitySummaryResponse getCheckListRule(LoanTypeMiscRequest loanTypeMiscRequest)
			throws ServiceException {
		logger.debug(Literal.ENTERING);

		EligibilitySummaryResponse response = new EligibilitySummaryResponse();
		WSReturnStatus returnStatus = new WSReturnStatus();

		if (StringUtils.isEmpty(loanTypeMiscRequest.getStage())) {
			String[] valueParm = new String[1];
			valueParm[0] = "stage";
			returnStatus = APIErrorHandlerService.getFailedStatus("90502", valueParm);
			response.setReturnStatus(returnStatus);
			return response;

		}
		if (StringUtils.isEmpty(loanTypeMiscRequest.getFinReference())) {

			String[] valueParm = new String[1];
			valueParm[0] = "finReference";
			returnStatus = APIErrorHandlerService.getFailedStatus("90502", valueParm);
			response.setReturnStatus(returnStatus);
			return response;
		} else {
			FinanceMain finMian = financeMainDAO.getFinanceMainById(loanTypeMiscRequest.getFinReference(), "_View",
					false);
			if (finMian == null) {
				String[] valueParm = new String[1];
				valueParm[0] = loanTypeMiscRequest.getFinReference();
				returnStatus = APIErrorHandlerService.getFailedStatus("90201", valueParm);
				response.setReturnStatus(returnStatus);
				return response;
			} else {
				if (!StringUtils.equals(loanTypeMiscRequest.getStage(), finMian.getNextRoleCode())) {
					String[] valueParm = new String[2];
					valueParm[0] = "CurrentStage: " + loanTypeMiscRequest.getStage();
					valueParm[1] = finMian.getNextRoleCode();
					returnStatus = APIErrorHandlerService.getFailedStatus("90337", valueParm);
					response.setReturnStatus(returnStatus);
					return response;
				}
				response = miscellaneousController.getCheckListRule(loanTypeMiscRequest, finMian);
			}
		}

		logger.debug(Literal.LEAVING);
		return response;
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

	@Autowired
	public void setFinanceMainDAO(FinanceMainDAO financeMainDAO) {
		this.financeMainDAO = financeMainDAO;
	}

	@Autowired
	public void setCovenantsService(CovenantsService covenantsService) {
		this.covenantsService = covenantsService;
	}

	@Autowired
	public void setCovenantTypeDAO(CovenantTypeDAO covenantTypeDAO) {
		this.covenantTypeDAO = covenantTypeDAO;
	}

}
