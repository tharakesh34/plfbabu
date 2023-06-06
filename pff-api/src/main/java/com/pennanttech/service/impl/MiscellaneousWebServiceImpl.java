package com.pennanttech.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.pennant.app.constants.ImplementationConstants;
import com.pennant.backend.dao.applicationmaster.CheckListDetailDAO;
import com.pennant.backend.dao.documentdetails.DocumentDetailsDAO;
import com.pennant.backend.dao.ext.dms.DMSGetLeadsDAO;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.dao.finance.covenant.CovenantsDAO;
import com.pennant.backend.dao.lmtmasters.FinanceReferenceDetailDAO;
import com.pennant.backend.model.WSReturnStatus;
import com.pennant.backend.model.applicationmaster.CheckListDetail;
import com.pennant.backend.model.bre.BREResponse;
import com.pennant.backend.model.customermasters.CustomerEligibilityCheck;
import com.pennant.backend.model.documentdetails.DocumentDetails;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.covenant.Covenant;
import com.pennant.backend.model.finance.covenant.CovenantDocument;
import com.pennant.backend.model.lmtmasters.FinanceReferenceDetail;
import com.pennant.backend.model.others.JVPosting;
import com.pennant.backend.model.rmtmasters.ScoringGroup;
import com.pennant.backend.model.rmtmasters.ScoringMetrics;
import com.pennant.backend.model.rmtmasters.ScoringSlab;
import com.pennant.backend.model.systemmasters.BRERequestDetail;
import com.pennant.backend.model.systemmasters.EmployerDetail;
import com.pennant.backend.service.finance.ScoringDetailService;
import com.pennant.backend.service.finance.covenant.CovenantsService;
import com.pennant.backend.service.others.JVPostingService;
import com.pennant.ws.exception.ServiceException;
import com.pennanttech.controller.ExtendedTestClass;
import com.pennanttech.controller.MiscellaneousServiceController;
import com.pennanttech.model.dms.DMSLeadDetails;
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

public class MiscellaneousWebServiceImpl extends ExtendedTestClass
		implements MiscellaneousRestService, MiscellaneousSoapService {

	private final Logger logger = LogManager.getLogger(getClass());
	private MiscellaneousServiceController miscellaneousController;
	private JVPostingService jVPostingService;
	private FinanceReferenceDetailDAO financeReferenceDetailDAO;
	private CheckListDetailDAO checkListDetailDAO;
	private CovenantsService covenantsService;
	private FinanceMainDAO financeMainDAO;
	private ScoringDetailService scoringDetailService;
	private DMSGetLeadsDAO dmsGetLeadsDAO;
	private CovenantsDAO covenantsDAO;
	private DocumentDetailsDAO documentDetailsDAO;

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

		if (StringUtils.isBlank(finReference)) {
			String[] valueParm = new String[1];
			valueParm[0] = "finReference";
			response.setReturnStatus(APIErrorHandlerService.getFailedStatus("90502", valueParm));
			return response;
		}

		Long finID = financeMainDAO.getActiveFinID(finReference);
		if (finID == null) {
			String[] valueParm = new String[1];
			valueParm[0] = finReference;
			response.setReturnStatus(APIErrorHandlerService.getFailedStatus("90201", valueParm));
			return response;
		}

		if (!ImplementationConstants.COVENANT_MODULE_NEW) {
			return response;
		}

		List<Covenant> covenants = covenantsService.getCovenants(finReference, "Loan", TableType.VIEW);

		if (CollectionUtils.isEmpty(covenants)) {
			String[] valueParm = new String[2];
			valueParm[0] = "Covenant Details";
			valueParm[1] = "with Finreference: " + finReference;
			response.setReturnStatus(APIErrorHandlerService.getFailedStatus("41002", valueParm));
			return response;
		}

		for (Covenant covenant : covenants) {
			List<CovenantDocument> documents = covenantsDAO.getCovenantDocuments(covenant.getId(), TableType.VIEW);

			for (CovenantDocument document : documents) {
				DocumentDetails dd = documentDetailsDAO.getDocumentDetails(document.getDocumentId(), "_View");
				document.setDoctype(dd.getDocCategory());
				document.setDocumentReceivedDate(dd.getDocReceivedDate());
				covenant.setCovenantDocuments(documents);
			}
		}

		response.setCovenants(covenants);
		response.setReturnStatus(APIErrorHandlerService.getSuccessStatus());

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
			finMian = financeMainDAO.getFinanceMain(loanTypeMiscRequest.getFinReference(), TableType.VIEW);
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
	public EligibilityDetailResponse checkEligibility(EligibilityDetail eligibilityDetail) throws ServiceException {
		logger.debug(Literal.ENTERING);

		EligibilityDetailResponse eligibilityResponse = miscellaneousController.checkEligibility(eligibilityDetail);

		logger.debug(Literal.LEAVING);
		return eligibilityResponse;
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
		}

		FinanceMain fm = financeMainDAO.getFinanceMain(loanTypeMiscRequest.getFinReference(), TableType.VIEW);

		if (fm == null) {
			String[] valueParm = new String[1];
			valueParm[0] = loanTypeMiscRequest.getFinReference();
			returnStatus = APIErrorHandlerService.getFailedStatus("90201", valueParm);
			response.setReturnStatus(returnStatus);
			return response;
		}

		if (!StringUtils.equals(loanTypeMiscRequest.getStage(), fm.getNextRoleCode())) {
			String[] valueParm = new String[2];
			valueParm[0] = "CurrentStage: " + loanTypeMiscRequest.getStage();
			valueParm[1] = fm.getNextRoleCode();
			returnStatus = APIErrorHandlerService.getFailedStatus("90337", valueParm);
			response.setReturnStatus(returnStatus);
			return response;
		}
		response = miscellaneousController.getCheckListRule(loanTypeMiscRequest, fm);

		logger.debug(Literal.LEAVING);
		return response;
	}

	@Override
	public EmployerDetail getEmployerDetail(EmployerDetail employerDetail) throws ServiceException {
		logger.debug(Literal.ENTERING);

		EmployerDetail response = new EmployerDetail();
		WSReturnStatus returnStatus = new WSReturnStatus();

		// validation
		if (StringUtils.isBlank(employerDetail.getEmpName())) {
			String[] valueParm = new String[1];
			valueParm[0] = "empName";
			returnStatus = APIErrorHandlerService.getFailedStatus("90502", valueParm);
			response.setReturnStatus(returnStatus);
		}
		if (StringUtils.isBlank(employerDetail.getEmpCategory())) {
			String[] valueParm = new String[1];
			valueParm[0] = "empCategory";
			returnStatus = APIErrorHandlerService.getFailedStatus("90502", valueParm);
			response.setReturnStatus(returnStatus);
		}
		boolean exist = miscellaneousController.isNonTargetEmployee(employerDetail.getEmpName(),
				employerDetail.getEmpCategory());

		if (!exist) {
			response = new EmployerDetail();
			response.setReturnStatus(APIErrorHandlerService.getSuccessStatus());
		} else {
			String[] valueParm = new String[2];
			valueParm[0] = response.getEmpName();
			valueParm[1] = response.getEmpCategory();
			returnStatus.setReturnText("Failed.");
			response.setElgRuleCode(response.getEmpCategory());
			response.setReturnStatus(returnStatus);
		}
		logger.debug(Literal.LEAVING);

		return response;
	}

	@Override
	public BREResponse getScore(BRERequestDetail bRERequestDetail) throws ServiceException {
		BigDecimal totalGrpMaxScore = BigDecimal.ZERO;
		BigDecimal totalGrpExecScore = BigDecimal.ZERO;

		BREResponse breResponse = getDatamap(bRERequestDetail);

		String custType = (String) breResponse.getDataMap().get("custType");

		if (StringUtils.isEmpty(custType)) {
			custType = "R";
		}

		// Get the Scoring Matrics based on Rules and Retail Customer
		List<ScoringMetrics> scoringMetricsList = scoringDetailService
				.getScoreMatricsListByCustType(bRERequestDetail.getScoreRuleCode(), custType);

		// Get the slab based on the scoreGroupId
		List<ScoringSlab> scoringSlabList = scoringDetailService
				.getScoringSlabsByScoreGrpId(scoringMetricsList.get(0).getScoreGroupId(), "_AView");

		CustomerEligibilityCheck customerEligibilityCheck = new CustomerEligibilityCheck();

		customerEligibilityCheck.setDataMap(breResponse.getDataMap());

		// Execute the Matrics
		scoringDetailService.executeScoringMetrics(scoringMetricsList, customerEligibilityCheck);

		for (ScoringMetrics scoringMetric : scoringMetricsList) {

			if (scoringMetric.getLovDescMetricMaxPoints() != null) {
				totalGrpMaxScore = totalGrpMaxScore.add(scoringMetric.getLovDescMetricMaxPoints());
			}
			if (scoringMetric.getLovDescExecutedScore() != null) {
				totalGrpExecScore = totalGrpExecScore.add(scoringMetric.getLovDescExecutedScore());
			}

		}
		breResponse.setRiskScore(totalGrpExecScore);

		// Get the Scoring Group
		String ruleVal = getScrSlab(scoringMetricsList.get(0).getScoreGroupId(), totalGrpExecScore, "", true,
				scoringSlabList);
		breResponse.setScoringGroup(ruleVal);

		return breResponse;
	}

	private String getScrSlab(long refId, BigDecimal grpTotalScore, String execCreditWorth, boolean isRetail,
			List<ScoringSlab> scoringMetricsList) {
		logger.debug("Entering");
		List<ScoringSlab> slabList = scoringMetricsList;
		String creditWorth = "None";
		BigDecimal minScore = new BigDecimal(35);
		List<Long> scoringValues = new ArrayList<>();

		for (ScoringSlab scoringSlab : slabList) {
			scoringValues.add(scoringSlab.getScoringSlab());
		}

		Collections.sort(scoringValues);

		if (slabList != null && !slabList.isEmpty()) {

			for (Long slab : scoringValues) {
				if (isRetail) {
					if (grpTotalScore.compareTo(minScore) >= 0 && grpTotalScore.compareTo(new BigDecimal(slab)) <= 0) {

						for (ScoringSlab scoringSlab : slabList) {
							if (slab.compareTo(scoringSlab.getScoringSlab()) == 0) {
								creditWorth = scoringSlab.getCreditWorthness();
							}
						}
						break;
					}
				}
			}

		} else if (StringUtils.isNotBlank(execCreditWorth)) {
			creditWorth = execCreditWorth;
		}

		logger.debug("Leaving");
		return creditWorth;
	}

	private BREResponse getDatamap(BRERequestDetail bRERequestDetail) {
		logger.debug(Literal.ENTERING);
		BREResponse response = null;
		try {
			response = miscellaneousController.getDatamap(bRERequestDetail);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
			response = new BREResponse();
		}
		logger.debug(Literal.LEAVING);
		return response;
	}

	@Override
	public BREResponse getProductOffers(BRERequestDetail bRERequestDetail) throws ServiceException {
		logger.debug(Literal.ENTERING);

		ScoringGroup group = new ScoringGroup();
		group.setScoreGroupCode(bRERequestDetail.getSegmentRule());

		BREResponse breResponse = miscellaneousController.getProductOffer(bRERequestDetail);

		logger.debug(Literal.LEAVING);

		return breResponse;
	}

	@Override
	public BREResponse calculateEligibility(BRERequestDetail checkEligibilty) throws ServiceException {
		logger.debug(Literal.ENTERING);
		BREResponse response = null;
		try {
			response = miscellaneousController.calculateEligibility(checkEligibilty);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
			response = new BREResponse();
		}
		logger.debug(Literal.LEAVING);
		return response;
	}

	@Override
	public WSReturnStatus pushLeadsForDMS(DMSLeadDetails dmsLeadDetails) throws ServiceException {
		logger.debug(Literal.ENTERING);

		WSReturnStatus returnStatus = new WSReturnStatus();
		String response = dmsGetLeadsDAO.processLeadsForDMSRetrieval(dmsLeadDetails);
		returnStatus.setReturnText(response);

		logger.debug(Literal.LEAVING);
		return returnStatus;
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
	public void setScoringDetailService(ScoringDetailService scoringDetailService) {
		this.scoringDetailService = scoringDetailService;
	}

	@Autowired
	public void setDMSGetLeadsDAO(DMSGetLeadsDAO dmsGetLeadsDAO) {
		this.dmsGetLeadsDAO = dmsGetLeadsDAO;
	}

	@Autowired
	public void setCovenantsDAO(CovenantsDAO covenantsDAO) {
		this.covenantsDAO = covenantsDAO;
	}

	@Autowired
	public void setDocumentDetailsDAO(DocumentDetailsDAO documentDetailsDAO) {
		this.documentDetailsDAO = documentDetailsDAO;
	}
}
