package com.pennanttech.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.cxf.phase.PhaseInterceptorChain;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.zkoss.json.JSONObject;
import org.zkoss.json.parser.JSONParser;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pennant.app.util.APIHeader;
import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.RuleExecutionUtil;
import com.pennant.app.util.SessionUserDetails;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.dao.lmtmasters.FinanceReferenceDetailDAO;
import com.pennant.backend.dao.rulefactory.RuleDAO;
import com.pennant.backend.dao.staticparms.ExtendedFieldHeaderDAO;
import com.pennant.backend.model.WSReturnStatus;
import com.pennant.backend.model.applicationmaster.AccountMapping;
import com.pennant.backend.model.applicationmaster.TransactionCode;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.bre.BREResponse;
import com.pennant.backend.model.customermasters.CustomerDetails;
import com.pennant.backend.model.customermasters.CustomerExtLiability;
import com.pennant.backend.model.customermasters.CustomerIncome;
import com.pennant.backend.model.dashboard.DashboardConfiguration;
import com.pennant.backend.model.extendedfield.ExtendedFieldHeader;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceEligibilityDetail;
import com.pennant.backend.model.finance.FinanceEnquiry;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.JointAccountDetail;
import com.pennant.backend.model.lmtmasters.FinanceReferenceDetail;
import com.pennant.backend.model.others.JVPosting;
import com.pennant.backend.model.others.JVPostingEntry;
import com.pennant.backend.model.rulefactory.Rule;
import com.pennant.backend.model.rulefactory.RuleResult;
import com.pennant.backend.model.systemmasters.ApplicantData;
import com.pennant.backend.model.systemmasters.BRERequestDetail;
import com.pennant.backend.model.systemmasters.FieldDataMap;
import com.pennant.backend.model.systemmasters.Perfois;
import com.pennant.backend.service.administration.SecurityUserService;
import com.pennant.backend.service.applicationmaster.AccountMappingService;
import com.pennant.backend.service.applicationmaster.TransactionCodeService;
import com.pennant.backend.service.customermasters.CustomerDetailsService;
import com.pennant.backend.service.dashboard.DashboardConfigurationService;
import com.pennant.backend.service.extendedfields.ExtendedFieldDetailsService;
import com.pennant.backend.service.finance.FinanceDetailService;
import com.pennant.backend.service.finance.FinanceMainService;
import com.pennant.backend.service.finance.JointAccountDetailService;
import com.pennant.backend.service.finance.impl.FinanceDataValidation;
import com.pennant.backend.service.others.JVPostingService;
import com.pennant.backend.service.rulefactory.RuleService;
import com.pennant.backend.service.systemmasters.EmployerDetailService;
import com.pennant.backend.util.ExtendedFieldConstants;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.RuleConstants;
import com.pennant.backend.util.RuleReturnType;
import com.pennant.fusioncharts.ChartSetElement;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.model.LoggedInUser;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.util.APIConstants;
import com.pennanttech.ws.model.dashboard.DashBoardRequest;
import com.pennanttech.ws.model.dashboard.DashBoardResponse;
import com.pennanttech.ws.model.eligibility.EligibilityDetail;
import com.pennanttech.ws.model.eligibility.EligibilityDetailResponse;
import com.pennanttech.ws.model.eligibility.EligibilityRuleCodeData;
import com.pennanttech.ws.model.eligibility.FieldData;
import com.pennanttech.ws.model.finance.EligibilityRespone;
import com.pennanttech.ws.model.finance.EligibilitySummaryResponse;
import com.pennanttech.ws.model.miscellaneous.CheckListResponse;
import com.pennanttech.ws.model.miscellaneous.LoanTypeMiscRequest;
import com.pennanttech.ws.service.APIErrorHandlerService;

public class MiscellaneousServiceController extends ExtendedTestClass {

	private final Logger logger = LogManager.getLogger(getClass());

	private FinanceMainService financeMainService;
	private TransactionCodeService transactionCodeService;
	private AccountMappingService accountMappingService;
	private JVPostingService jVPostingService;
	private DashboardConfigurationService dashboardConfigurationService;
	private SecurityUserService securityUserService;
	private RuleService ruleService;
	private RuleDAO ruleDAO;
	private FinanceDetailService financeDetailService;
	private FinanceReferenceDetailDAO financeReferenceDetailDAO;
	private JointAccountDetailService jointAccountDetailService;
	private FinanceMainDAO financeMainDAO;
	private CustomerDetailsService customerDetailsService;
	private FinanceDataValidation financeDataValidation;
	private ExtendedFieldDetailsService extendedFieldDetailsService;
	private ExtendedFieldHeaderDAO extendedFieldHeaderDAO;
	private EmployerDetailService employerDetailService;

	public MiscellaneousServiceController() {
		super();
	}

	private List<ErrorDetail> doEligibilityValidations(EligibilityDetail eligibilityDetail) {
		List<ErrorDetail> errorsList = new ArrayList<>();

		List<EligibilityRuleCodeData> eligibilityRuleCodeData = eligibilityDetail.getEligibilityRuleCodeDatas();
		List<FieldData> fieldsData = eligibilityDetail.getFieldDatas();

		if (CollectionUtils.isEmpty(eligibilityRuleCodeData)) {
			String[] param = new String[1];
			param[0] = "eligibilityRuleCodes";
			errorsList.add(ErrorUtil.getErrorDetail(new ErrorDetail("90502", param)));

			return errorsList;
		}

		for (EligibilityRuleCodeData ruleCodeData : eligibilityRuleCodeData) {
			if (StringUtils.isEmpty(ruleCodeData.getElgRuleCode())) {
				String[] param = new String[1];
				param[0] = "elgRuleCode ";
				errorsList.add(ErrorUtil.getErrorDetail(new ErrorDetail("30561", param)));
				return errorsList;
			} else {
				eligibilityDetail.addRuleCode(ruleCodeData.getElgRuleCode());
			}
		}

		if (CollectionUtils.isEmpty(fieldsData)) {
			String[] param = new String[1];
			param[0] = "fieldDatas ";
			errorsList.add(ErrorUtil.getErrorDetail(new ErrorDetail("90502", param)));

			return errorsList;
		}

		for (FieldData fieldData : fieldsData) {
			if (StringUtils.isEmpty(fieldData.getFieldName())) {
				String[] param = new String[1];
				param[0] = "fieldName ";
				errorsList.add(ErrorUtil.getErrorDetail(new ErrorDetail("30561", param)));

				return errorsList;
			}

			if (fieldData.getFieldValue() == null || StringUtils.isBlank(fieldData.getFieldValue().toString())) {
				String[] param = new String[1];
				param[0] = "fieldValue ";
				errorsList.add(ErrorUtil.getErrorDetail(new ErrorDetail("30561", param)));
				return errorsList;
			} else {
				eligibilityDetail.setFieldData(fieldData.getFieldName(), fieldData.getFieldValue());
			}
		}

		return errorsList;
	}

	private List<ErrorDetail> doCheckEligibilityValidations(EligibilityDetail eligibilityDetail) {
		List<ErrorDetail> errorsList = new ArrayList<>();

		List<EligibilityRuleCodeData> eligibilityRuleCodeData = eligibilityDetail.getEligibilityRuleCodeDatas();
		List<String> ruleCodeList = new ArrayList<>();

		Map<String, Map<String, Object>> datamap = new HashMap<>();

		Map<String, Object> map = null;
		if (CollectionUtils.isEmpty(eligibilityRuleCodeData)) {
			String[] param = new String[1];
			param[0] = "eligibilityRuleCodes";
			errorsList.add(ErrorUtil.getErrorDetail(new ErrorDetail("90502", param)));

			return errorsList;
		}

		for (EligibilityRuleCodeData ruleCodeData : eligibilityRuleCodeData) {
			map = new HashMap<>();
			if (StringUtils.isEmpty(ruleCodeData.getElgRuleCode())) {
				String[] param = new String[1];
				param[0] = "elgRuleCode ";
				errorsList.add(ErrorUtil.getErrorDetail(new ErrorDetail("30561", param)));
				return errorsList;
			} else {
				ruleCodeList.add(ruleCodeData.getElgRuleCode());
			}
			if (CollectionUtils.isEmpty(ruleCodeData.getFieldDatas())) {
				String[] param = new String[1];
				param[0] = "fieldDatas ";
				errorsList.add(ErrorUtil.getErrorDetail(new ErrorDetail("90502", param)));

				return errorsList;
			}

			for (FieldData fieldData : ruleCodeData.getFieldDatas()) {
				if (StringUtils.isEmpty(fieldData.getFieldName())) {
					String[] param = new String[1];
					param[0] = "fieldName ";
					errorsList.add(ErrorUtil.getErrorDetail(new ErrorDetail("30561", param)));

					return errorsList;
				}

				if (fieldData.getFieldValue() == null || StringUtils.isBlank(fieldData.getFieldValue().toString())) {
					String[] param = new String[1];
					param[0] = "fieldValue ";
					errorsList.add(ErrorUtil.getErrorDetail(new ErrorDetail("30561", param)));
					return errorsList;
				} else {
					map.put(fieldData.getFieldName(), fieldData.getFieldValue());

					datamap.put(ruleCodeData.getElgRuleCode(), map);
				}
			}
			//ruleCodeData.setMap(map);
			ruleCodeData.setDataMap(datamap);
			ruleCodeData.setRuleCodes(ruleCodeList);
		}
		return errorsList;
	}

	public EligibilityDetailResponse prepareEligibilityFieldsdata(EligibilityDetail eligibilityDetail) {

		logger.debug(Literal.ENTERING);

		EligibilityDetailResponse response = null;
		List<ErrorDetail> eligibilityErrors = doEligibilityValidations(eligibilityDetail);

		if (CollectionUtils.isEmpty(eligibilityErrors)) {
			response = new EligibilityDetailResponse();
			List<Rule> rules = ruleService.getEligibilityRules(eligibilityDetail.getRuleCodes());

			if (CollectionUtils.isEmpty(rules) || eligibilityDetail.getRuleCodes().size() != rules.size()) {
				List<ErrorDetail> errorsList = new ArrayList<>();
				String[] param = new String[1];
				param[0] = "rulecode";
				errorsList.add(ErrorUtil.getErrorDetail(new ErrorDetail("90266", param)));

				for (ErrorDetail errorDetail : errorsList) {
					response.setReturnStatus(
							APIErrorHandlerService.getFailedStatus(errorDetail.getCode(), errorDetail.getParameters()));
				}
			} else {
				List<FinanceEligibilityDetail> eligibilityDetailsList = new ArrayList<>();
				List<FinanceEligibilityDetail> eligibilityDetailsList1 = new ArrayList<>();

				// Validation;
				// Convert Rules to FinanceEligibilityDetail;
				String strCodes = "FOIRAMT,BTOUTSTD,EBOEU,IIRMAX,LCRMAXEL,LIVSTCK,LOANAMT,LTVAMOUN,LTVLCR,"
						+ RuleConstants.ELGRULE_DSRCAL + "," + RuleConstants.ELGRULE_FOIR + ","
						+ RuleConstants.ELGRULE_LTV;

				for (Rule rule : rules) {
					FinanceEligibilityDetail financeEligibilityDetail = new FinanceEligibilityDetail();
					financeEligibilityDetail.setLovDescElgRuleCode(rule.getRuleCode());
					financeEligibilityDetail.setElgRuleValue(rule.getSQLRule());
					financeEligibilityDetail.setRuleResultType(rule.getReturnType());

					if (StringUtils.contains(strCodes, financeEligibilityDetail.getLovDescElgRuleCode())) {
						eligibilityDetailsList.add(financeEligibilityDetail);
					} else {
						eligibilityDetailsList1.add(financeEligibilityDetail);
					}
				}

				// Validation;

				eligibilityDetailsList.addAll(eligibilityDetailsList1);
				List<FinanceEligibilityDetail> responseList = new ArrayList<>();

				// Execute Eligibility
				for (FinanceEligibilityDetail financeEligibilityDetail : eligibilityDetailsList) {
					FinanceEligibilityDetail detail = executeRule(financeEligibilityDetail, eligibilityDetail.getMap(),
							"INR", false);
					eligibilityDetail.setFieldData("RULE_" + detail.getLovDescElgRuleCode(), detail.getRuleResult());
					responseList.add(detail);
				}

				response.setEligibilityDetails(responseList);
				response.setReturnStatus(APIErrorHandlerService.getSuccessStatus());
			}
		} else {
			response = new EligibilityDetailResponse();
			for (ErrorDetail errorDetail : eligibilityErrors) {
				response.setReturnStatus(
						APIErrorHandlerService.getFailedStatus(errorDetail.getCode(), errorDetail.getParameters()));
			}
		}

		logger.debug(Literal.LEAVING);
		return response;
	}

	public EligibilityDetailResponse checkEligibility(EligibilityDetail eligibilityDetail) {
		logger.debug(Literal.ENTERING);

		EligibilityDetailResponse response = null;
		List<ErrorDetail> eligibilityErrors = doCheckEligibilityValidations(eligibilityDetail);

		if (CollectionUtils.isEmpty(eligibilityErrors)) {
			for (EligibilityRuleCodeData eligibilityRuleCodeData : eligibilityDetail.getEligibilityRuleCodeDatas()) {
				response = new EligibilityDetailResponse();
				List<Rule> rules = ruleService.getEligibilityRules(eligibilityRuleCodeData.getRuleCodes());

				if (CollectionUtils.isEmpty(rules)) {
					List<ErrorDetail> errorsList = new ArrayList<>();
					String[] param = new String[1];
					param[0] = "rulecode";
					errorsList.add(ErrorUtil.getErrorDetail(new ErrorDetail("90266", param)));

					for (ErrorDetail errorDetail : errorsList) {
						response.setReturnStatus(APIErrorHandlerService.getFailedStatus(errorDetail.getCode(),
								errorDetail.getParameters()));
					}
				} else {
					List<FinanceEligibilityDetail> eligibilityDetailsList = new ArrayList<>();
					List<FinanceEligibilityDetail> eligibilityDetailsList1 = new ArrayList<>();

					// Validation;
					// Convert Rules to FinanceEligibilityDetail;
					String strCodes = "FOIRAMT,BTOUTSTD,EBOEU,IIRMAX,LCRMAXEL,LIVSTCK,LOANAMT,LTVAMOUN,LTVLCR,"
							+ RuleConstants.ELGRULE_DSRCAL + "," + RuleConstants.ELGRULE_FOIR + ","
							+ RuleConstants.ELGRULE_LTV;

					for (Rule rule : rules) {
						FinanceEligibilityDetail financeEligibilityDetail = new FinanceEligibilityDetail();
						financeEligibilityDetail.setLovDescElgRuleCode(rule.getRuleCode());
						financeEligibilityDetail.setElgRuleValue(rule.getSQLRule());
						financeEligibilityDetail.setRuleResultType(rule.getReturnType());
						financeEligibilityDetail.setSplRuleVal(rule.getSPLRule());

						if (StringUtils.contains(strCodes, financeEligibilityDetail.getLovDescElgRuleCode())) {
							eligibilityDetailsList.add(financeEligibilityDetail);
						} else {
							eligibilityDetailsList1.add(financeEligibilityDetail);
						}
					}

					// Validation;

					eligibilityDetailsList.addAll(eligibilityDetailsList1);
					List<FinanceEligibilityDetail> responseList = new ArrayList<>();

					// Execute Eligibility
					for (FinanceEligibilityDetail financeEligibilityDetail : eligibilityDetailsList) {
						boolean splRule = false;
						Map<String, Object> dataMap = new HashMap<>();
						Map<String, Map<String, Object>> fieldMap = eligibilityRuleCodeData.getDataMap();

						if (fieldMap.containsKey(financeEligibilityDetail.getLovDescElgRuleCode())) {
							dataMap = fieldMap.get(financeEligibilityDetail.getLovDescElgRuleCode());
						}

						if (dataMap.containsKey("empCategory")) {
							boolean isNonTargetEmp = isNonTargetEmployee((String) dataMap.get("empname"),
									(String) dataMap.get("empCategory"));

							FinanceEligibilityDetail detail = new FinanceEligibilityDetail();
							if (!isNonTargetEmp) {
								detail.setRuleResult("1");
								detail.setLovDescElgRuleCode(financeEligibilityDetail.getLovDescElgRuleCode());
								responseList.add(detail);
							} else {
								detail.setRuleResult("0");
								detail.setLovDescElgRuleCode(financeEligibilityDetail.getLovDescElgRuleCode());
								responseList.add(detail);
							}

						} else {

							if (!StringUtils.isEmpty(financeEligibilityDetail.getSplRuleVal())) {
								splRule = true;
							}

							FinanceEligibilityDetail detail = executeRule(financeEligibilityDetail, dataMap, "INR",
									splRule);

							if (splRule && !StringUtils.equals("0", detail.getRuleResult())) {
								String result = detail.getRuleResult();

								JSONParser parser = new JSONParser();
								JSONObject json = (JSONObject) parser.parse(result.toString());
								detail.setJson(json);
							}
							//If Any Rule fails return the control along with executed Rule Result.
							if (StringUtils.equals("0", detail.getRuleResult())) {
								responseList.add(detail);
								response.setEligibilityDetails(responseList);
								return response;
							}
							eligibilityRuleCodeData.setFieldData("RULE_" + detail.getLovDescElgRuleCode(),
									detail.getRuleResult());
							responseList.add(detail);
						}
					}

					response.setEligibilityDetails(responseList);
					response.setReturnStatus(APIErrorHandlerService.getSuccessStatus());
				}
			}
		} else {
			response = new EligibilityDetailResponse();
			for (ErrorDetail errorDetail : eligibilityErrors) {
				response.setReturnStatus(
						APIErrorHandlerService.getFailedStatus(errorDetail.getCode(), errorDetail.getParameters()));
			}
		}

		logger.debug(Literal.LEAVING);
		return response;

	}

	private FinanceEligibilityDetail executeRule(FinanceEligibilityDetail finElgDetail, Map<String, Object> map,
			String finCcy, boolean isSplRule) {

		RuleReturnType ruleReturnType = null;

		if (StringUtils.equals(finElgDetail.getRuleResultType(), RuleReturnType.BOOLEAN.value())) {
			ruleReturnType = RuleReturnType.BOOLEAN;
		} else if (StringUtils.equals(finElgDetail.getRuleResultType(), RuleReturnType.DECIMAL.value())) {
			ruleReturnType = RuleReturnType.DECIMAL;
		} else if (StringUtils.equals(finElgDetail.getRuleResultType(), RuleReturnType.STRING.value())) {
			ruleReturnType = RuleReturnType.STRING;
		} else if (StringUtils.equals(finElgDetail.getRuleResultType(), RuleReturnType.INTEGER.value())) {
			ruleReturnType = RuleReturnType.INTEGER;
		} else if (StringUtils.equals(finElgDetail.getRuleResultType(), RuleReturnType.OBJECT.value())) {
			ruleReturnType = RuleReturnType.OBJECT;
		} else if (StringUtils.equals(finElgDetail.getRuleResultType(), RuleReturnType.CALCSTRING.value())) {
			ruleReturnType = RuleReturnType.STRING;
		}

		if ((null == ruleReturnType) || (StringUtils.isBlank(ruleReturnType.value()))) {
			logger.info("Improper 'ruleReturnType' value");
		} else {
			Object object = RuleExecutionUtil.executeRule(finElgDetail.getElgRuleValue(), map, finCcy, ruleReturnType);

			String resultValue = null;
			switch (ruleReturnType) {
			case DECIMAL:
				if (object instanceof BigDecimal) {
					// unFormating object
					int formatter = CurrencyUtil.getFormat(finCcy);
					object = PennantApplicationUtil.unFormateAmount((BigDecimal) object, formatter);
				}

				if (object != null) {
					finElgDetail.setRuleResult(object.toString());
				}
				break;
			case STRING:
				if (object instanceof String) {
					finElgDetail.setRuleResult(object.toString());
				}
				break;
			case INTEGER:
				if (object != null) {
					finElgDetail.setRuleResult(object.toString());
				}
				break;

			case BOOLEAN:
				boolean tempBoolean = (boolean) object;
				if (tempBoolean) {
					resultValue = "1";
				} else {
					resultValue = "0";
				}
				finElgDetail.setRuleResult(resultValue);
				break;

			case OBJECT:
				RuleResult ruleResult = (RuleResult) object;
				Object resultval = ruleResult.getValue();
				Object resultvalue = ruleResult.getDeviation();

				BigDecimal tempResult = null;
				BigDecimal deviationValue = null;
				if (resultval instanceof Double || resultval instanceof Integer) {
					tempResult = new BigDecimal(resultval.toString());
					deviationValue = new BigDecimal(resultvalue.toString());
					finElgDetail.setRuleResult(tempResult.toString());
					finElgDetail.setDeviation(deviationValue.toString());
				}
				break;

			default:
				// do-nothing
				break;
			}
		}

		return finElgDetail;
	}

	private List<ErrorDetail> doDashboardValidations(DashBoardRequest request, boolean usernameProvided) {

		logger.debug(Literal.ENTERING);

		List<ErrorDetail> errorsList = new ArrayList<>();
		if (usernameProvided) {

			if (StringUtils.isBlank(request.getCode())) {
				String[] param = new String[1];
				param[0] = "code";
				errorsList.add(ErrorUtil.getErrorDetail(new ErrorDetail("30561", param)));

				return errorsList;
			}

			DashboardConfiguration configuration = dashboardConfigurationService
					.getApprovedDashboardDetailById(request.getCode());
			if (null == configuration) {
				String[] param = new String[1];
				param[0] = "dashboardcode: " + request.getCode();
				errorsList.add(new ErrorDetail("90266", param));

				return errorsList;
			}

			long userID = securityUserService.getSecuredUserDetails(request.getUserLogin());
			if (userID <= 0) {
				String[] param = new String[1];
				param[0] = "userLogin: " + request.getUserLogin();
				errorsList.add(ErrorUtil.getErrorDetail(new ErrorDetail("90266", param)));

				return errorsList;
			}
		} else {
			if (StringUtils.isBlank(request.getCode())) {
				String[] param = new String[1];
				param[0] = "code";
				errorsList.add(ErrorUtil.getErrorDetail(new ErrorDetail("30561", param)));

				return errorsList;
			}

			DashboardConfiguration configuration = dashboardConfigurationService
					.getApprovedDashboardDetailById(request.getCode());
			if (null == configuration) {
				String[] param = new String[1];
				param[0] = "dashboardcode: " + request.getCode();
				errorsList.add(new ErrorDetail("90266", param));

				return errorsList;
			}
		}
		logger.debug(Literal.LEAVING);

		return errorsList;
	}

	public DashBoardResponse prepareDashboardConfiguration(DashBoardRequest request) {

		logger.debug(Literal.ENTERING);

		DashBoardResponse response = null;
		DashboardConfiguration config = null;

		if (StringUtils.isBlank(request.getUserLogin())) {
			response = new DashBoardResponse();
			List<ErrorDetail> validationErrors = doDashboardValidations(request, false);
			if (CollectionUtils.isEmpty(validationErrors)) {
				LoggedInUser loggedUser = SessionUserDetails.getUserDetails(SessionUserDetails.getLogiedInUser());
				request.setUserLogin(loggedUser.getUserName());

				config = dashboardConfigurationService.getApprovedDashboardDetailById(request.getCode());
				if (config != null) {
					List<ChartSetElement> chartList = dashboardConfigurationService.getDashboardLabelAndValues(config);
					response.setChartSetElement(chartList);
					response.setDashboardConfiguration(config);
					response.setReturnStatus(APIErrorHandlerService.getSuccessStatus());
				} else {
					String[] valueParm = new String[1];
					valueParm[0] = request.getCode();
					response.setReturnStatus(APIErrorHandlerService.getFailedStatus("90501", valueParm));
				}
			} else {
				for (ErrorDetail errorDetail : validationErrors) {
					response.setReturnStatus(
							APIErrorHandlerService.getFailedStatus(errorDetail.getCode(), errorDetail.getParameters()));
				}
			}
		} else {
			// user login available in request
			response = new DashBoardResponse();
			List<ErrorDetail> validationErrors = doDashboardValidations(request, true);
			if (CollectionUtils.isEmpty(validationErrors)) {
				config = dashboardConfigurationService.getApprovedDashboardDetailById(request.getCode());
				if (config != null) {
					List<ChartSetElement> charSetElementsList = dashboardConfigurationService
							.getDashboardLabelAndValues(config);
					if (!CollectionUtils.isEmpty(charSetElementsList)) {
						response.setChartSetElement(charSetElementsList);
						response.setDashboardConfiguration(config);
						response.setReturnStatus(APIErrorHandlerService.getSuccessStatus());
					} else {
						String[] valueParm = new String[1];
						valueParm[0] = request.getCode();
						response.setReturnStatus(APIErrorHandlerService.getFailedStatus("90501", valueParm));
					}
				}
			} else {
				for (ErrorDetail errorDetail : validationErrors) {
					response.setReturnStatus(
							APIErrorHandlerService.getFailedStatus(errorDetail.getCode(), errorDetail.getParameters()));
				}
			}
		}
		logger.debug(Literal.LEAVING);

		return response;
	}

	private void setJVPostingEntryMandatoryFieldsData(JVPostingEntry postingEntry) {

		logger.debug(Literal.ENTERING);

		LoggedInUser userDetails = SessionUserDetails.getUserDetails(SessionUserDetails.getLogiedInUser());
		postingEntry.setUserDetails(userDetails);
		postingEntry.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
		postingEntry.setLastMntBy(userDetails.getUserId());
		postingEntry.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		postingEntry.setFinSourceID(APIConstants.FINSOURCE_ID_API);

		logger.debug(Literal.LEAVING);
	}

	private void setJVPostingMandatoryFieldsData(JVPosting posting) {

		logger.debug(Literal.ENTERING);

		LoggedInUser userDetails = SessionUserDetails.getUserDetails(SessionUserDetails.getLogiedInUser());
		posting.setUserDetails(userDetails);
		posting.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
		posting.setLastMntBy(userDetails.getUserId());
		posting.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		posting.setFinSourceID(APIConstants.FINSOURCE_ID_API);

		logger.debug(Literal.LEAVING);
	}

	public WSReturnStatus prepareJVPostData(JVPosting jvPosting) {

		logger.debug(Literal.ENTERING);

		WSReturnStatus returnStatus = new WSReturnStatus();
		JVPosting posting = new JVPosting();
		JVPostingEntry postingEntry = null;
		List<JVPostingEntry> creditEntryList = new ArrayList<>();
		List<JVPostingEntry> debitEntryList = new ArrayList<>();
		List<JVPostingEntry> postingEntryList = new ArrayList<>();

		FinanceMain financeMainData = financeMainService.getFinanceByFinReference(jvPosting.getReference(), "_AView");
		if (null != financeMainData) {
			posting.setNewRecord(true);
			posting.setVersion(1);
			posting.setBranch(financeMainData.getFinBranch());
			posting.setBatch(jvPosting.getBatch());
			posting.setBatchReference(0);
			if (StringUtils.isNotBlank(jvPosting.getCurrency())) {
				posting.setCurrency(jvPosting.getCurrency());
			} else {
				posting.setCurrency(financeMainData.getFinCcy());
			}
			posting.setPostingDate(DateUtility.getAppDate());
			posting.setPostAgainst(FinanceConstants.POSTING_AGAINST_LOAN);
			posting.setBatchPurpose("");
			posting.setBatchPostingStatus(financeMainData.getFinPurpose());
			posting.setReference(financeMainData.getFinReference());
			posting.setPostingDivision(financeMainData.getLovDescFinDivision());

			BigDecimal totalDebits = BigDecimal.ZERO;
			BigDecimal totalCredits = BigDecimal.ZERO;
			// JVPostingEntry Fields Data
			for (JVPostingEntry entry : jvPosting.getJVPostingEntrysList()) {
				postingEntry = new JVPostingEntry();
				postingEntry.setNewRecord(true);
				postingEntry.setVersion(1);
				postingEntry.setTxnCCy(entry.getTxnCCy());
				postingEntry.setNarrLine4(entry.getNarrLine4());
				postingEntry.setNarrLine3(entry.getNarrLine3());
				postingEntry.setNarrLine2(entry.getNarrLine2());
				postingEntry.setNarrLine1(entry.getNarrLine1());
				postingEntry.setTxnReference(entry.getTxnReference());
				postingEntry.setValueDate(DateUtility.getAppDate());
				postingEntry.setPostingDate(DateUtility.getAppDate());
				postingEntry.setBatchReference(entry.getBatchReference());
				postingEntry.setAccount(entry.getAccount());

				TransactionCode transactionCode = transactionCodeService
						.getApprovedTransactionCodeById(entry.getTxnCode());
				if (null != transactionCode) {
					AccountMapping accountMapping = accountMappingService.getApprovedAccountMapping(entry.getAccount());

					postingEntry.setRecordType(PennantConstants.RCD_ADD);
					postingEntry.setFinReference(entry.getFinReference());
					postingEntry.setAccountName(accountMapping.getAccountTypeDesc());
					postingEntry.setTxnEntry(transactionCode.getTranType());
					postingEntry.setAcType(accountMapping.getAccountType());
					postingEntry.setAccCCy(financeMainData.getFinCcy());
					postingEntry.setTxnCCy(financeMainData.getFinCcy());

					if (null != accountMapping) {
						switch (transactionCode.getTranType()) {
						case "C":
							postingEntry.setTxnAmount(entry.getTxnAmount());
							postingEntry.setTxnAmount_Ac(entry.getTxnAmount());
							postingEntry.setTxnCode(entry.getTxnCode());
							postingEntry.setAccount(entry.getAccount());
							posting.setCreditsCount(jvPosting.getCreditsCount() + 1);
							totalCredits = totalCredits.add(entry.getTxnAmount());
							creditEntryList.add(postingEntry);
							break;

						case "D":
							postingEntry.setTxnAmount(entry.getTxnAmount());
							postingEntry.setTxnAmount_Ac(entry.getTxnAmount());
							postingEntry.setTxnCode(entry.getTxnCode());
							postingEntry.setDebitAccount(entry.getAccount());
							posting.setDebitCount(jvPosting.getDebitCount() + 1);
							totalDebits = totalDebits.add(entry.getTxnAmount());
							debitEntryList.add(postingEntry);
							break;
						}
					}
				}
			}

			// reorganize all JVPostingEntries in order [C,D]
			for (int i = 0, j = i; i <= creditEntryList.size() && j <= debitEntryList.size(); i++, j++) {
				if (i < creditEntryList.size() && creditEntryList.get(i) != null) {
					if (!postingEntryList.contains(creditEntryList.get(i))) {
						postingEntryList.add(creditEntryList.get(i));
					}
				}

				if (j < debitEntryList.size() && debitEntryList.get(j) != null) {
					if (!postingEntryList.contains(debitEntryList.get(j))) {
						postingEntryList.add(debitEntryList.get(j));
					}
				}
			}
			posting.setJVPostingEntrysList(postingEntryList);
			posting.setTotCreditsByBatchCcy(totalCredits);
			posting.setTotDebitsByBatchCcy(totalDebits);
			setJVPostingMandatoryFieldsData(posting);

			returnStatus = saveJVPostingData(posting);
		} else {
			// financemain data is not available
			String[] valueParm = new String[1];
			valueParm[0] = "reference: " + jvPosting.getReference();

			returnStatus = APIErrorHandlerService.getFailedStatus("90266", valueParm);
		}

		logger.debug(Literal.LEAVING);

		return returnStatus;
	}

	private AuditHeader prepareAuditHeader(JVPosting aJVPosting, String tranType) {

		logger.debug(Literal.ENTERING);

		AuditDetail auditDetail = new AuditDetail(tranType, 1, aJVPosting.getBefImage(), aJVPosting);
		AuditHeader auditHeader = new AuditHeader(aJVPosting.getReference(),
				Long.toString(aJVPosting.getBatchReference()), null, null, auditDetail, aJVPosting.getUserDetails(),
				new HashMap<String, List<ErrorDetail>>());

		APIHeader reqHeaderDetails = (APIHeader) PhaseInterceptorChain.getCurrentMessage().getExchange()
				.get(APIHeader.API_HEADER_KEY);
		auditHeader.setApiHeader(reqHeaderDetails);

		logger.debug(Literal.LEAVING);

		return auditHeader;
	}

	private WSReturnStatus saveJVPostingData(final JVPosting postReadyData) {

		logger.debug(Literal.ENTERING);

		WSReturnStatus returnStatus = new WSReturnStatus();

		AuditHeader auditHeader = prepareAuditHeader(postReadyData, PennantConstants.TRAN_WF);
		AuditHeader savedJVPostData = jVPostingService.doApprove(auditHeader);

		if (savedJVPostData.getAuditError() != null) {
			for (ErrorDetail errorDetail : savedJVPostData.getErrorMessage()) {
				returnStatus = APIErrorHandlerService.getFailedStatus(errorDetail.getCode(), errorDetail.getError());
			}
		} else {
			returnStatus = APIErrorHandlerService.getSuccessStatus();
		}

		logger.debug(Literal.LEAVING);

		return returnStatus;
	}

	public EligibilitySummaryResponse getEligibility(FinanceMain finMian, LoanTypeMiscRequest loanTypeMiscRequest) {
		logger.debug(Literal.ENTERING);

		EligibilitySummaryResponse summaryResponse = new EligibilitySummaryResponse();
		List<EligibilityRespone> list = new ArrayList<>();
		WSReturnStatus returnStatus = new WSReturnStatus();
		FinanceEligibilityDetail finElgDetail = new FinanceEligibilityDetail();
		String result = null;

		List<FinanceReferenceDetail> financeReferenceDetail = financeReferenceDetailDAO.getFinanceReferenceDetail(
				finMian.getFinType(), FinanceConstants.FINSER_EVENT_ORG, loanTypeMiscRequest.getStage(), "_TEView");
		if (!CollectionUtils.isEmpty(financeReferenceDetail)) {
			Map<String, Object> declaredMap = getMapValue(loanTypeMiscRequest);
			for (FinanceReferenceDetail finaReferenceDetail : financeReferenceDetail) {
				if (finaReferenceDetail.isIsActive()) {
					EligibilityRespone response = new EligibilityRespone();
					Rule rule = ruleDAO.getRuleByID(finaReferenceDetail.getLovDescCodelov(),
							RuleConstants.MODULE_ELGRULE, RuleConstants.MODULE_ELGRULE, "");
					if (rule != null) {
						finElgDetail.setRuleResultType(rule.getReturnType());
						finElgDetail.setElgRuleValue(rule.getSQLRule());
						finElgDetail.setLovDescElgRuleCode(rule.getRuleCode());
						finElgDetail.setLovDescElgRuleCodeDesc(rule.getRuleCodeDesc());
						try {
							finElgDetail = executeRule(finElgDetail, declaredMap, finMian.getFinCcy(), false);
						} catch (Exception e) {
							APIErrorHandlerService.logUnhandledException(e);
							returnStatus = APIErrorHandlerService.getFailedStatus();
							logger.error("Exception: ", e);
							summaryResponse.setReturnStatus(returnStatus);
							return summaryResponse;
						}
						response.setResultValue(finElgDetail.getRuleResult());
						response.setRuleCode(finElgDetail.getLovDescElgRuleCode());
						response.setReuleName(finElgDetail.getLovDescElgRuleCodeDesc());
						response.setDeviation(finElgDetail.getDeviation());
						String ruleResult = StringUtils.trimToEmpty(finElgDetail.getRuleResult());
						if (StringUtils.isEmpty(ruleResult) || "0".equals(ruleResult) || "0.0".equals(ruleResult)
								|| "0.00".equals(ruleResult)) {
							result = "Ineligible";
						} else {
							result = "Eligible";
						}
						response.setResult(result);
						list.add(response);
						summaryResponse.setEligibilityResponeList(list);
						summaryResponse.setSummary(getResultSummary(summaryResponse));
						summaryResponse.setReturnStatus(APIErrorHandlerService.getSuccessStatus());
					} else {
						String[] valueParm = new String[1];
						valueParm[0] = "No Rule configure at " + loanTypeMiscRequest.getStage() + " stage";
						summaryResponse.setReturnStatus(APIErrorHandlerService.getFailedStatus("21005", valueParm));
					}
				}
			}
			if (CollectionUtils.isEmpty(summaryResponse.getEligibilityResponeList())) {
				String[] valueParm = new String[1];
				valueParm[0] = "No Rule configure at " + loanTypeMiscRequest.getStage() + " stage";
				summaryResponse.setReturnStatus(APIErrorHandlerService.getFailedStatus("21005", valueParm));
			}
		} else {
			String[] valueParm = new String[1];
			valueParm[0] = "No Rule configure at " + loanTypeMiscRequest.getStage() + " stage";
			summaryResponse.setReturnStatus(APIErrorHandlerService.getFailedStatus("21005", valueParm));
		}
		logger.debug(Literal.ENTERING);
		return summaryResponse;
	}

	public String getResultSummary(EligibilitySummaryResponse summarResponse) {
		String summary = "Eligible";
		if (summarResponse != null) {
			if (!CollectionUtils.isEmpty(summarResponse.getEligibilityResponeList())) {
				for (EligibilityRespone response : summarResponse.getEligibilityResponeList()) {
					if (StringUtils.equalsIgnoreCase(response.getResult(), "Ineligible")) {
						summary = "Ineligible";
						return summary;
					}
				}
			}
		}
		return summary;
	}

	public EligibilitySummaryResponse getCheckListRule(LoanTypeMiscRequest loanTypeMiscRequest, FinanceMain finMian) {
		logger.debug(Literal.ENTERING);

		EligibilitySummaryResponse summaryResponse = new EligibilitySummaryResponse();
		List<EligibilityRespone> list = new ArrayList<>();
		WSReturnStatus returnStatus = new WSReturnStatus();
		Map<String, Object> declaredMap = new HashMap<>();
		FinanceEligibilityDetail finElgDetail = new FinanceEligibilityDetail();
		List<CheckListResponse> checkListResponseList = new ArrayList<>();
		String result = null;

		List<FinanceReferenceDetail> financeReferenceDetail = financeReferenceDetailDAO
				.getFinanceRefListByFinType(finMian.getFinType(), loanTypeMiscRequest.getStage(), "_tqview");
		if (!CollectionUtils.isEmpty(financeReferenceDetail)) {
			for (FinanceReferenceDetail finReferenceDetail : financeReferenceDetail) {
				if (finReferenceDetail.isIsActive()
						&& StringUtils.isNotBlank(finReferenceDetail.getLovDescElgRuleValue())) {
					EligibilityRespone response = new EligibilityRespone();
					Rule rule = ruleDAO.getRuleByID(finReferenceDetail.getLovDescElgRuleValue(),
							RuleConstants.MODULE_CLRULE, RuleConstants.MODULE_CLRULE, "");
					if (rule != null) {
						finElgDetail.setRuleResultType(rule.getReturnType());
						finElgDetail.setElgRuleValue(rule.getSQLRule());
						finElgDetail.setLovDescElgRuleCode(rule.getRuleCode());
						finElgDetail.setLovDescElgRuleCodeDesc(rule.getRuleCodeDesc());
						declaredMap = getMapValue(loanTypeMiscRequest);
						try {
							finElgDetail = executeRule(finElgDetail, declaredMap, finMian.getFinCcy(), false);

						} catch (Exception e) {
							APIErrorHandlerService.logUnhandledException(e);
							returnStatus = APIErrorHandlerService.getFailedStatus();
							logger.error("Exception: ", e);
							summaryResponse.setReturnStatus(returnStatus);
							return summaryResponse;
						}
						response.setResultValue(finElgDetail.getRuleResult());
						response.setRuleCode(finElgDetail.getLovDescElgRuleCode());
						response.setReuleName(finElgDetail.getLovDescElgRuleCodeDesc());
						if (!StringUtils.equalsIgnoreCase(finElgDetail.getRuleResult(), "1")) {
							result = "Ineligible";
						} else {
							result = "Eligible";
						}
						response.setResult(result);
						list.add(response);
						summaryResponse.setEligibilityResponeList(list);
						summaryResponse.setSummary(getResultSummary(summaryResponse));
						summaryResponse.setReturnStatus(APIErrorHandlerService.getSuccessStatus());
					}
				}
				if (finReferenceDetail.isIsActive()) {
					CheckListResponse checkListResponse = new CheckListResponse();
					checkListResponse.setFinRefId(finReferenceDetail.getFinRefId());
					checkListResponse.setLovDescRefDesc(finReferenceDetail.getLovDescRefDesc());
					checkListResponse.setMandInputInStage(finReferenceDetail.getMandInputInStage());
					checkListResponse.setLovDescCheckMinCount(finReferenceDetail.getLovDescCheckMinCount());
					checkListResponse.setLovDescCheckMaxCount(finReferenceDetail.getLovDescCheckMaxCount());
					if (StringUtils.contains(finReferenceDetail.getMandInputInStage(),
							loanTypeMiscRequest.getStage())) {
						checkListResponse.setCheckListMandnetory(true);
					}
					checkListResponseList.add(checkListResponse);
				}

			}
			if (!CollectionUtils.isEmpty(checkListResponseList)) {
				summaryResponse.setCheckListResponse(checkListResponseList);
			}

		} else {
			String[] valueParm = new String[1];
			valueParm[0] = "No Rule or Checklist configure at " + loanTypeMiscRequest.getStage() + " stage";
			summaryResponse.setReturnStatus(APIErrorHandlerService.getFailedStatus("21005", valueParm));
		}

		logger.debug(Literal.LEAVING);
		return summaryResponse;
	}

	public Map<String, Object> getMapValue(LoanTypeMiscRequest loanTypeMiscRequest) {
		logger.debug(Literal.ENTERING);

		Map<String, Object> declaredMap = new HashMap<String, Object>();
		BigDecimal obligation_Internal = BigDecimal.ZERO;
		BigDecimal obligation_external = BigDecimal.ZERO;
		BigDecimal totIncome = BigDecimal.ZERO;
		BigDecimal totExpense = BigDecimal.ZERO;
		BigDecimal internal_Obligation = BigDecimal.ZERO;
		BigDecimal external_Obligation = BigDecimal.ZERO;
		ExtendedFieldHeader extendedFieldHeader = null;
		Map<String, Object> extDetailMap = new HashMap<String, Object>();
		int ccyFormat = 2;

		FinanceDetail finDetils = financeDetailService.getFinanceDetailById(loanTypeMiscRequest.getFinReference(),
				false, FinanceConstants.FINSER_EVENT_ORG, false, "", "");
		FinanceMain financeMain = finDetils.getFinScheduleData().getFinanceMain();
		CustomerDetails customerDetails = finDetils.getCustomerDetails();
		List<JointAccountDetail> jointAccountDetailList = finDetils.getJointAccountDetailList();
		int activeLoanFinType = financeMainDAO.getActiveCount(financeMain.getFinType(), financeMain.getCustID());
		int totalLoanFinType = financeMainDAO.getODLoanCount(financeMain.getFinType(), financeMain.getCustID());

		if (customerDetails == null) {
			customerDetails = customerDetailsService.getCustomerDetailsById(financeMain.getCustID(), true, "_View");
			finDetils.setCustomerDetails(customerDetails);
		}

		// customer extended fields
		if (customerDetails.getExtendedDetails() == null) {
			extendedFieldHeader = extendedFieldHeaderDAO.getExtendedFieldHeaderByModuleName(
					ExtendedFieldConstants.MODULE_CUSTOMER, customerDetails.getCustomer().getCustCtgCode(), "");
			customerDetails.setExtendedFieldHeader(extendedFieldHeader);
			customerDetails.setExtendedFieldRender(extendedFieldDetailsService.getExtendedFieldRender(
					ExtendedFieldConstants.MODULE_CUSTOMER, customerDetails.getCustomer().getCustCtgCode(),
					customerDetails.getCustomer().getCustCIF()));
			customerDetails.setExtendedDetails(extendedFieldDetailsService.getExtndedFieldDetails(
					ExtendedFieldConstants.MODULE_CUSTOMER, customerDetails.getCustomer().getCustCtgCode(), null,
					customerDetails.getCustomer().getCustCIF()));
		}

		// getting co-applicant details
		if (jointAccountDetailList == null || finDetils.getJointAccountDetailList().isEmpty()) {
			jointAccountDetailList = jointAccountDetailService
					.getJointAccountDetailByFinRef(financeMain.getFinReference(), "_View");
			finDetils.setJointAccountDetailList(jointAccountDetailList);
		}

		finDetils = financeDataValidation.prepareCustElgDetail(false, finDetils);
		// format extended field
		extDetailMap = PennantApplicationUtil.getExtendedFieldsDataMap(customerDetails);
		if (extDetailMap != null) {
			setFormatAmount(extDetailMap, ccyFormat);
		}
		// setting extended details for customer and finance
		finDetils.getCustomerEligibilityCheck().setExtendedFields(extDetailMap);
		declaredMap = finDetils.getCustomerEligibilityCheck().getDeclaredFieldValues();

		if (CollectionUtils.isNotEmpty(finDetils.getCustomerDetails().getCustFinanceExposureList())) {
			for (FinanceEnquiry enquiry : finDetils.getCustomerDetails().getCustFinanceExposureList()) {
				internal_Obligation = internal_Obligation.add(enquiry.getMaxInstAmount());
			}
		}

		if (CollectionUtils.isNotEmpty(finDetils.getCustomerDetails().getCustomerExtLiabilityList())) {
			for (CustomerExtLiability liability : finDetils.getCustomerDetails().getCustomerExtLiabilityList()) {
				external_Obligation = external_Obligation.add(liability.getInstalmentAmount());
			}
		}

		for (JointAccountDetail jointAccountDetail : jointAccountDetailList) {
			if (CollectionUtils.isNotEmpty(jointAccountDetail.getCustomerExtLiabilityList())) {
				for (CustomerExtLiability liability : jointAccountDetail.getCustomerExtLiabilityList()) {
					obligation_external = obligation_external.add(liability.getInstalmentAmount());
				}
			}
			if (CollectionUtils.isNotEmpty(jointAccountDetail.getCustFinanceExposureList())) {
				for (FinanceEnquiry enquiry : jointAccountDetail.getCustFinanceExposureList()) {
					obligation_Internal = obligation_Internal.add(enquiry.getMaxInstAmount());
				}
			}
			if (CollectionUtils.isNotEmpty(jointAccountDetail.getCustomerIncomeList())) {
				for (CustomerIncome income : jointAccountDetail.getCustomerIncomeList()) {
					if (income.getIncomeExpense().equals(PennantConstants.INCOME)) {
						totIncome = totIncome.add(income.getCalculatedAmount());
					} else {
						totExpense = totExpense.add(income.getCalculatedAmount());
					}
				}
			}
		}

		if (financeMain != null) {
			declaredMap.put("currentAssetValue", financeMain.getFinAmount());
			declaredMap.put("finPurpose", financeMain.getFinPurpose());
			declaredMap.put("eligibilityMethod", financeMain.getEligibilityMethod());
		}

		if (jointAccountDetailList != null) {
			declaredMap.put("Co_Applicants_Count", jointAccountDetailList.size());
			declaredMap.put("Guarantors_Total_Count", jointAccountDetailList.size());
		}

		declaredMap.put("Total_Co_Applicants_Income", totIncome);
		declaredMap.put("Total_Co_Applicants_Expense", totExpense);
		declaredMap.put("Customer_Obligation_Internal", internal_Obligation);
		declaredMap.put("Co_Applicants_Obligation_Internal", obligation_Internal);
		declaredMap.put("Co_Applicants_Obligation_External", obligation_external);

		declaredMap.put("Customer_Obligation_External", external_Obligation);
		declaredMap.put("activeLoansOnFinType", activeLoanFinType);
		declaredMap.put("totalLoansOnFinType", totalLoanFinType);

		declaredMap.put("CUSTOMER_MARGIN_DEVIATION", customerDetails.getCustomer().isMarginDeviation());

		logger.debug(Literal.LEAVING);
		return declaredMap;
	}

	public void setFormatAmount(Map<String, Object> extDetailMap, int ccyFormat) {
		BigDecimal value = BigDecimal.ZERO;
		for (Map.Entry<String, Object> entry : extDetailMap.entrySet()) {
			if (entry.getValue() instanceof BigDecimal) {
				value = PennantApplicationUtil.formateAmount((BigDecimal) entry.getValue(), ccyFormat);
				entry.setValue(value);
			}
		}
	}

	public BREResponse getProductOffer(BRERequestDetail bRERequestDetail) {

		BREResponse response = new BREResponse();

		List<ErrorDetail> eligibilityErrors = doCheckEligibilityValidationsForProductOffers(bRERequestDetail);

		Map<String, Object> finalMap = new HashMap<>();

		if (CollectionUtils.isEmpty(eligibilityErrors)) {
			boolean splRule = false;

			Rule rule = ruleDAO.getRuleById(bRERequestDetail.getRuleCode(), RuleConstants.MODULE_BRERULE, "");

			FinanceEligibilityDetail financeEligibilityDetail = new FinanceEligibilityDetail();
			financeEligibilityDetail.setLovDescElgRuleCode(rule.getRuleCode());
			financeEligibilityDetail.setElgRuleValue(rule.getSQLRule());
			financeEligibilityDetail.setRuleResultType(rule.getReturnType());
			financeEligibilityDetail.setSplRuleVal(rule.getSPLRule());

			if (!StringUtils.isEmpty(financeEligibilityDetail.getSplRuleVal())) {
				splRule = true;
			}

			//FIXME:shinde.b Need to check for Irrespective of the Rule Execution.
			Map<String, Object> varData = bRERequestDetail.getMap();
			varData.put("splRule", "Y");

			FinanceEligibilityDetail detail = executeRule(financeEligibilityDetail, varData, "INR", splRule);

			if (splRule && !StringUtils.equals("0", detail.getRuleResult())) {
				String result = detail.getRuleResult();

				JSONParser parser = new JSONParser();
				JSONObject json = (JSONObject) parser.parse(result.toString());

				finalMap.putAll(bRERequestDetail.getMap());
				finalMap.putAll(convertStringToMap(json.toString()));

				response.setDataMap(finalMap);

				response.setResult(json);

				if (bRERequestDetail.getMap().containsKey("riskScore")) {

					String val = (String) bRERequestDetail.getMap().get("riskScore");
					response.setRiskScore(new BigDecimal(val));
				}

				if (bRERequestDetail.getMap().containsKey("segmentRule")) {
					response.setScoringGroup((String) bRERequestDetail.getMap().get("segmentRule"));
				}

			}
			//If Any Rule fails return the control along with executed Rule Result.
			if (StringUtils.equals("0", detail.getRuleResult())) {
				return response;
			}
			bRERequestDetail.setFieldData("RULE_" + detail.getLovDescElgRuleCode(), detail.getRuleResult());

		} else {
			response = new BREResponse();
			for (ErrorDetail errorDetail : eligibilityErrors) {
				response.setReturnStatus(
						APIErrorHandlerService.getFailedStatus(errorDetail.getCode(), errorDetail.getParameters()));
			}
		}

		return response;
	}

	public Map<String, Object> convertStringToMap(String payload) {
		logger.debug(Literal.ENTERING);
		ObjectMapper obj = new ObjectMapper();
		Map<String, Object> map = null;
		try {
			map = obj.readValue(payload, Map.class);
		} catch (Exception e) {
			logger.debug(Literal.EXCEPTION, e);
		}
		logger.debug(Literal.LEAVING);
		return map;
	}

	private List<ErrorDetail> doCheckEligibilityValidationsForBRE(BRERequestDetail eligibilityDetail) {
		List<ErrorDetail> errorsList = new ArrayList<>();

		Map<String, Object> map = new HashMap<>();
		if (StringUtils.isEmpty(eligibilityDetail.getRuleCode())) {
			String[] param = new String[1];
			param[0] = "eligibilityRuleCodes";
			errorsList.add(ErrorUtil.getErrorDetail(new ErrorDetail("90502", param)));

			return errorsList;
		}

		if (StringUtils.isEmpty(eligibilityDetail.getScoreRuleCode())) {
			String[] param = new String[1];
			param[0] = "eligibilityRuleCodes";
			errorsList.add(ErrorUtil.getErrorDetail(new ErrorDetail("90502", param)));

			return errorsList;
		}

		if (CollectionUtils.isEmpty(eligibilityDetail.getFieldDatas())) {
			String[] param = new String[1];
			param[0] = "fieldDatas ";
			errorsList.add(ErrorUtil.getErrorDetail(new ErrorDetail("90502", param)));

			return errorsList;
		}

		for (FieldDataMap fieldData : eligibilityDetail.getFieldDatas()) {
			if (StringUtils.isEmpty(fieldData.getFieldName())) {
				String[] param = new String[1];
				param[0] = "fieldName ";
				errorsList.add(ErrorUtil.getErrorDetail(new ErrorDetail("30561", param)));

				return errorsList;
			}

			if (fieldData.getFieldValue() == null || StringUtils.isBlank(fieldData.getFieldValue().toString())) {
				String[] param = new String[1];
				param[0] = "fieldValue ";
				errorsList.add(ErrorUtil.getErrorDetail(new ErrorDetail("30561", param)));
				return errorsList;
			} else {
				map.put(fieldData.getFieldName(), fieldData.getFieldValue());

			}
		}

		if (CollectionUtils.isNotEmpty(eligibilityDetail.getPerfoisData())) {
			int count = 1;
			for (Perfois perfois : eligibilityDetail.getPerfoisData()) {
				String emi = "emi_";
				String salary = "sal_month_";
				String totalAmountofEMIbounces = "emiBounce_";
				String grossReceipts = "grossReceipts_";
				map.put(emi + count, perfois.getEmi());
				map.put(salary + count, perfois.getSalary());
				map.put(totalAmountofEMIbounces + count, perfois.getTotalAmountofEMIbounces());
				map.put(grossReceipts + count, perfois.getGrossReceipts());
				count++;
			}

			map.put("monthCount_p", eligibilityDetail.getPerfoisData().size());
		} else {
			map.put("monthCount_p", 0);
		}

		eligibilityDetail.setMap(map);

		return errorsList;
	}

	private List<ErrorDetail> doCheckEligibilityValidationsForProductOffers(BRERequestDetail eligibilityDetail) {
		List<ErrorDetail> errorsList = new ArrayList<>();

		Map<String, Object> map = new HashMap<>();
		if (StringUtils.isEmpty(eligibilityDetail.getRuleCode())) {
			String[] param = new String[1];
			param[0] = "eligibilityRuleCodes";
			errorsList.add(ErrorUtil.getErrorDetail(new ErrorDetail("90502", param)));

			return errorsList;
		}

		for (FieldDataMap fieldData : eligibilityDetail.getFieldDatas()) {
			map.put(fieldData.getFieldName(), fieldData.getFieldValue());
		}

		eligibilityDetail.setMap(map);

		return errorsList;
	}

	private List<ErrorDetail> doCheckEligibilityValidationsForCheckEligibility(BRERequestDetail eligibilityDetail) {
		List<ErrorDetail> errorsList = new ArrayList<>();

		Map<String, Object> map = new HashMap<>();
		if (StringUtils.isEmpty(eligibilityDetail.getRuleCode())) {
			String[] param = new String[1];
			param[0] = "eligibilityRuleCodes";
			errorsList.add(ErrorUtil.getErrorDetail(new ErrorDetail("90502", param)));

			return errorsList;
		}

		map.put("roi", eligibilityDetail.getRoi());
		map.put("foir", eligibilityDetail.getFoir());
		map.put("tenure", eligibilityDetail.getTenure());
		map.put("propertyValue", eligibilityDetail.getPropertyValue());
		map.put("approvedLtv", eligibilityDetail.getApprovedLtv());

		if (eligibilityDetail.getApplicantData() != null) {
			map.put("income_1", eligibilityDetail.getApplicantData().getFinalIncome());
			map.put("obligation_1", eligibilityDetail.getApplicantData().getFinalObligation());
		}

		if (CollectionUtils.isNotEmpty(eligibilityDetail.getCoApplicantData())) {
			int count = 2;
			for (ApplicantData perfois : eligibilityDetail.getCoApplicantData()) {
				String income = "income_";
				String obligation = "obligation_";
				map.put(income + count, perfois.getFinalIncome());
				map.put(obligation + count, perfois.getFinalObligation());
				count++;
			}
			map.put("CoApplicantCount", eligibilityDetail.getCoApplicantData().size());
		} else {
			map.put("CoApplicantCount", 0);
		}

		eligibilityDetail.setMap(map);

		return errorsList;
	}

	public BREResponse getDatamap(BRERequestDetail bRERequestDetail) {

		BREResponse response = new BREResponse();

		List<ErrorDetail> eligibilityErrors = doCheckEligibilityValidationsForBRE(bRERequestDetail);

		Map<String, Object> finalMap = new HashMap<>();

		if (CollectionUtils.isEmpty(eligibilityErrors)) {
			boolean splRule = false;

			Rule rule = ruleDAO.getRuleById(bRERequestDetail.getRuleCode(), RuleConstants.MODULE_BRERULE, "");

			FinanceEligibilityDetail financeEligibilityDetail = new FinanceEligibilityDetail();
			financeEligibilityDetail.setLovDescElgRuleCode(rule.getRuleCode());
			financeEligibilityDetail.setElgRuleValue(rule.getSQLRule());
			financeEligibilityDetail.setRuleResultType(rule.getReturnType());
			financeEligibilityDetail.setSplRuleVal(rule.getSPLRule());

			if (!StringUtils.isEmpty(financeEligibilityDetail.getSplRuleVal())) {
				splRule = true;
			}

			//FIXME:Shinde.b Need to check for Irrespective of the Rule Execution.
			Map<String, Object> varData = bRERequestDetail.getMap();
			varData.put("splRule", "Y");

			FinanceEligibilityDetail detail = executeRule(financeEligibilityDetail, varData, "INR", splRule);

			if (splRule && !StringUtils.equals("0", detail.getRuleResult())) {
				String result = detail.getRuleResult();

				JSONParser parser = new JSONParser();
				JSONObject json = (JSONObject) parser.parse(result.toString());

				finalMap.putAll(bRERequestDetail.getMap());
				finalMap.putAll(convertStringToMap(json.toString()));
				response.setDataMap(finalMap);
				response.setResult(json);

			}
			//If Any Rule fails return the control along with executed Rule Result.
			if (StringUtils.equals("0", detail.getRuleResult())) {
				return response;
			}
			bRERequestDetail.setFieldData("RULE_" + detail.getLovDescElgRuleCode(), detail.getRuleResult());

		} else {
			response = new BREResponse();
			for (ErrorDetail errorDetail : eligibilityErrors) {
				response.setReturnStatus(
						APIErrorHandlerService.getFailedStatus(errorDetail.getCode(), errorDetail.getParameters()));
			}
		}

		return response;
	}

	public BREResponse calculateEligibility(BRERequestDetail bRERequestDetail) {
		BREResponse response = new BREResponse();

		List<ErrorDetail> eligibilityErrors = doCheckEligibilityValidationsForCheckEligibility(bRERequestDetail);

		Map<String, Object> finalMap = new HashMap<>();

		if (CollectionUtils.isEmpty(eligibilityErrors)) {
			boolean splRule = false;

			Rule rule = ruleDAO.getRuleById(bRERequestDetail.getRuleCode(), RuleConstants.MODULE_BRERULE, "");

			FinanceEligibilityDetail financeEligibilityDetail = new FinanceEligibilityDetail();
			financeEligibilityDetail.setLovDescElgRuleCode(rule.getRuleCode());
			financeEligibilityDetail.setElgRuleValue(rule.getSQLRule());
			financeEligibilityDetail.setRuleResultType(rule.getReturnType());
			financeEligibilityDetail.setSplRuleVal(rule.getSPLRule());

			if (!StringUtils.isEmpty(financeEligibilityDetail.getSplRuleVal())) {
				splRule = true;
			}

			//FIXME:Shinde.b Need to check for Irrespective of the Rule Execution.
			Map<String, Object> varData = bRERequestDetail.getMap();
			varData.put("splRule", "Y");

			FinanceEligibilityDetail detail = executeRule(financeEligibilityDetail, varData, "INR", splRule);

			if (splRule && !StringUtils.equals("0", detail.getRuleResult())) {
				String result = detail.getRuleResult();

				JSONParser parser = new JSONParser();
				JSONObject json = (JSONObject) parser.parse(result.toString());

				finalMap.putAll(bRERequestDetail.getMap());
				finalMap.putAll(convertStringToMap(json.toString()));
				response.setDataMap(finalMap);
				response.setResult(json);

			}
			//If Any Rule fails return the control along with executed Rule Result.
			if (StringUtils.equals("0", detail.getRuleResult())) {
				return response;
			}
			bRERequestDetail.setFieldData("RULE_" + detail.getLovDescElgRuleCode(), detail.getRuleResult());

		} else {
			response = new BREResponse();
			for (ErrorDetail errorDetail : eligibilityErrors) {
				response.setReturnStatus(
						APIErrorHandlerService.getFailedStatus(errorDetail.getCode(), errorDetail.getParameters()));
			}
		}

		return response;
	}

	public boolean isNonTargetEmployee(String name, String category) {
		logger.debug(Literal.ENTERING);
		return employerDetailService.isNonTargetEmployee(name, category, "");
	}

	public void setFinanceMainService(FinanceMainService financeMainService) {
		this.financeMainService = financeMainService;
	}

	public void setTransactionCodeService(TransactionCodeService transactionCodeService) {
		this.transactionCodeService = transactionCodeService;
	}

	public void setAccountMappingService(AccountMappingService accountMappingService) {
		this.accountMappingService = accountMappingService;
	}

	public void setjVPostingService(JVPostingService jVPostingService) {
		this.jVPostingService = jVPostingService;
	}

	public void setDashboardConfigurationService(DashboardConfigurationService dashboardConfigurationService) {
		this.dashboardConfigurationService = dashboardConfigurationService;
	}

	public void setSecurityUserService(SecurityUserService securityUserService) {
		this.securityUserService = securityUserService;
	}

	public void setRuleService(RuleService ruleService) {
		this.ruleService = ruleService;
	}

	public void setRuleDAO(RuleDAO ruleDAO) {
		this.ruleDAO = ruleDAO;
	}

	public void setFinanceDetailService(FinanceDetailService financeDetailService) {
		this.financeDetailService = financeDetailService;
	}

	public void setFinanceReferenceDetailDAO(FinanceReferenceDetailDAO financeReferenceDetailDAO) {
		this.financeReferenceDetailDAO = financeReferenceDetailDAO;
	}

	public void setJointAccountDetailService(JointAccountDetailService jointAccountDetailService) {
		this.jointAccountDetailService = jointAccountDetailService;
	}

	public void setFinanceMainDAO(FinanceMainDAO financeMainDAO) {
		this.financeMainDAO = financeMainDAO;
	}

	public void setCustomerDetailsService(CustomerDetailsService customerDetailsService) {
		this.customerDetailsService = customerDetailsService;
	}

	public void setFinanceDataValidation(FinanceDataValidation financeDataValidation) {
		this.financeDataValidation = financeDataValidation;
	}

	public void setExtendedFieldDetailsService(ExtendedFieldDetailsService extendedFieldDetailsService) {
		this.extendedFieldDetailsService = extendedFieldDetailsService;
	}

	public void setExtendedFieldHeaderDAO(ExtendedFieldHeaderDAO extendedFieldHeaderDAO) {
		this.extendedFieldHeaderDAO = extendedFieldHeaderDAO;
	}

}
