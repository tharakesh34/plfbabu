package com.pennant.webui.delegationdeviation;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.pennant.app.util.CalculationUtil;
import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.RuleExecutionUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.collateral.CollateralAssignmentDAO;
import com.pennant.backend.delegationdeviation.DeviationConfigService;
import com.pennant.backend.delegationdeviation.DeviationHelper;
import com.pennant.backend.delegationdeviation.DeviationUtil;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.applicationmaster.CheckListDetail;
import com.pennant.backend.model.customermasters.CustomerDetails;
import com.pennant.backend.model.customermasters.CustomerDocument;
import com.pennant.backend.model.customermasters.CustomerEligibilityCheck;
import com.pennant.backend.model.extendedfield.ExtendedFieldHeader;
import com.pennant.backend.model.extendedfield.ExtendedFieldRender;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceDeviations;
import com.pennant.backend.model.finance.FinanceEligibilityDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.GuarantorDetail;
import com.pennant.backend.model.finance.JointAccountDetail;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.model.rulefactory.FeeRule;
import com.pennant.backend.model.rulefactory.RuleResult;
import com.pennant.backend.model.solutionfactory.DeviationDetail;
import com.pennant.backend.model.solutionfactory.DeviationHeader;
import com.pennant.backend.model.solutionfactory.DeviationParam;
import com.pennant.backend.service.collateral.impl.CollateralSetupFetchingService;
import com.pennant.backend.service.customermasters.impl.CustomerDataService;
import com.pennant.backend.service.finance.CheckListDetailService;
import com.pennant.backend.util.DeviationConstants;
import com.pennant.backend.util.ExtendedFieldConstants;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.RuleReturnType;
import com.pennant.backend.util.SMTParameterConstants;
import com.pennant.component.extendedfields.ExtendedFieldCtrl;
import com.pennant.util.PennantAppUtil;
import com.pennant.webui.finance.financemain.FinanceMainBaseCtrl;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.pff.service.hook.PostDeviationHook;
import com.rits.cloning.Cloner;

public class DeviationExecutionCtrl {
	private static final Logger logger = LogManager.getLogger(DeviationExecutionCtrl.class);

	private int format = 0;
	private String userRole;
	private long userid;
	private List<DeviationParam> deviationParamsList = PennantAppUtil.getDeviationParams();

	private FinanceMainBaseCtrl financeMainBaseCtrl;
	private List<FinanceDeviations> approvedFinanceDeviations;
	@Autowired
	private DeviationConfigService deviationConfigService;
	private CheckListDetailService checkListDetailService;
	@Autowired
	private CustomerDataService customerDataService;
	@Autowired
	private CollateralAssignmentDAO collateralAssignmentDAO;
	@Autowired
	private CollateralSetupFetchingService collateralSetupFetchingService;
	@Autowired(required = false)
	@Qualifier("financePostDeviationHook")
	private PostDeviationHook postDeviationHook;
	@Autowired
	private DeviationHelper deviationHelper;

	/* This list which hold the all deviation across the tab's */
	private List<FinanceDeviations> financeDeviations = new ArrayList<>();
	List<ValueLabel> delegators = new ArrayList<>();
	private ExtendedFieldCtrl extendedFieldCtrl = null;

	public DeviationExecutionCtrl() {
		super();
	}

	public boolean deviationAllowed(String product) {
		return deviationConfigService.deviationAllowed(product);
	}

	/**
	 * To product deviations
	 * 
	 * @param financeDetail
	 * @return
	 */
	public void checkProductDeviations(FinanceDetail financeDetail) {
		logger.debug(Literal.ENTERING);

		// Prepare the script engine with the required bindings.
		FinanceMain financeMain = financeDetail.getFinScheduleData().getFinanceMain();
		FinanceType financeType = financeDetail.getFinScheduleData().getFinanceType();

		Map<String, Object> engine = prepareScriptEngine(financeMain, financeType);

		// Get the product deviations that were defined for the finance type.
		List<DeviationHeader> deviationHeaders = getProductDeviatations(financeType.getFinType());

		for (DeviationHeader deviationHeader : deviationHeaders) {
			List<DeviationDetail> deviationDetails = deviationHeader.getDeviationDetails();

			if (deviationDetails == null || deviationDetails.isEmpty()) {
				continue;
			}

			// Get the deviation definition.
			DeviationParam deviationParam = getDeviationparam(deviationHeader.getModuleCode());

			if (deviationParam == null) {
				continue;
			}

			// Execute the deviation rule and get the deviated value.
			Object result = executeRule(deviationParam.getFormula(), engine);

			if (result != null) {
				processAutoDeviation(DeviationConstants.CAT_AUTO, DeviationConstants.TY_PRODUCT, null, result, null,
						financeMain.getFinReference(), deviationHeader, null);
			}
		}

		fillAutoDeviations();

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Check custom deviations and add to finance deviations, if any.
	 * 
	 * @param financeDetail Finance detail object.
	 */
	@SuppressWarnings("unchecked")
	public void checkCustomDeviations(FinanceDetail financeDetail) {
		logger.debug(Literal.ENTERING);

		// If the post deviation hook not available, skip the process.
		if (postDeviationHook == null) {
			logger.info("Post deviation hook not available.");
			return;
		}

		financeDetail.setAppDate(SysParamUtil.getAppDate());

		delegators = deviationHelper
				.getWorkflowRoles(financeDetail.getFinScheduleData().getFinanceMain().getWorkflowId());

		// Clone the object.
		Cloner cloner = new Cloner();
		FinanceDetail aFinanceDetail = cloner.deepClone(financeDetail);

		// Prepare additional data like co-applicants, collateral details.
		// *** Co-applicant's details. ***
		CustomerDetails customer;

		if (CollectionUtils.isNotEmpty(aFinanceDetail.getJointAccountDetailList())) {
			for (JointAccountDetail coApplicant : aFinanceDetail.getJointAccountDetailList()) {
				CustomerDetails custDeatils = coApplicant.getCustomerDetails();
				if (custDeatils != null) {
					CustomerDetails custdet = setCoappExtendedfields(custDeatils);
					coApplicant.setCustomerDetails(custdet);
				} else {
					customer = customerDataService.getCustomerDetailsbyID(coApplicant.getCustID(), true, "_AView");
					CustomerDetails custdet = setCoappExtendedfields(customer);
					coApplicant.setCustomerDetails(custdet);
				}
			}
		}

		if (CollectionUtils.isNotEmpty(aFinanceDetail.getGurantorsDetailList())) {
			for (GuarantorDetail guarantorDetail : aFinanceDetail.getGurantorsDetailList()) {
				if (PennantConstants.RECORD_TYPE_CAN.equals(guarantorDetail.getRecordType())
						|| PennantConstants.RECORD_TYPE_DEL.equals(guarantorDetail.getRecordType())) {
					continue;
				}
				if (guarantorDetail.isBankCustomer()) {
					customer = customerDataService.getCustomerDetailsbyID(guarantorDetail.getCustID(), true, "_AView");
					guarantorDetail.setCustomerDetails(customer);
				}
			}
		}

		// Setting the collateral setup list
		collateralSetupFetchingService.getCollateralSetupList(aFinanceDetail, true);

		// Call the customization hook.
		String deviationFilePath = SysParamUtil.getValueAsString(SMTParameterConstants.CUSTOM_DEVIATION_FILE_PATH);
		List<FinanceDeviations> deviations = postDeviationHook.raiseDeviations(aFinanceDetail, deviationFilePath);

		// Remove the deviations that are invalid like duplicate code, invalid delegator etc.
		deviations = deviationHelper.getValidCustomDeviations(deviations, delegators);

		// Clear the existing deviations.
		for (FinanceDeviations deviation : getApprovedFinanceDeviations()) {
			if (DeviationConstants.TY_CUSTOM.equals(deviation.getModule())) {
				if (!DeviationUtil.isExists(deviations, deviation.getDeviationCode())) {
					deviationHelper.purgeDeviations(getApprovedFinanceDeviations(), DeviationConstants.TY_CUSTOM,
							deviation.getDeviationCode());
				}
			}
		}

		List<FinanceDeviations> currentDeviations = new ArrayList<>();

		if (!getFinanceDeviations().isEmpty()) {
			currentDeviations = (List<FinanceDeviations>) ((ArrayList<FinanceDeviations>) getFinanceDeviations())
					.clone();
		}

		for (FinanceDeviations deviation : currentDeviations) {
			if (DeviationConstants.TY_CUSTOM.equals(deviation.getModule())) {
				if (!DeviationUtil.isExists(deviations, deviation.getDeviationCode())) {
					deviationHelper.removeDeviations(getFinanceDeviations(), DeviationConstants.TY_CUSTOM,
							deviation.getDeviationCode());
				}
			}
		}

		// Process the new deviations.
		for (FinanceDeviations deviation : deviations) {
			processAutoDeviation(DeviationConstants.CAT_CUSTOM, DeviationConstants.TY_CUSTOM,
					deviation.getDeviationCode(), deviation.getDeviationValue(), deviation.getDelegationRole(),
					financeDetail.getFinScheduleData().getFinReference(), null, deviation.getDeviationDesc());
		}

		fillAutoDeviations();

		logger.debug(Literal.LEAVING);
	}

	private CustomerDetails setCoappExtendedfields(CustomerDetails customer) {
		extendedFieldCtrl = new ExtendedFieldCtrl();
		ExtendedFieldHeader extendedFieldHeader = extendedFieldCtrl.getExtendedFieldHeader(
				ExtendedFieldConstants.MODULE_CUSTOMER, customer.getCustomer().getCustCtgCode());
		customer.setExtendedFieldHeader(extendedFieldHeader);

		if (extendedFieldHeader == null) {
			return customer;
		}
		ExtendedFieldRender extendedFieldRender = extendedFieldCtrl
				.getExtendedFieldRender(customer.getCustomer().getCustCIF());
		customer.setExtendedFieldRender(extendedFieldRender);
		return customer;
	}

	/**
	 * To Eligibility deviations
	 * 
	 * @param finElgDetail
	 * @param financeDetail
	 * @return
	 */
	public FinanceDeviations checkEligibilityDeviations(FinanceEligibilityDetail finElgDetail,
			FinanceDetail financeDetail) {
		logger.debug("Entering");

		FinanceType financeType = financeDetail.getFinScheduleData().getFinanceType();
		FinanceMain financeMain = financeDetail.getFinScheduleData().getFinanceMain();

		String finCcy = financeDetail.getFinScheduleData().getFinanceMain().getFinCcy();

		CustomerEligibilityCheck customerEligibilityCheck = financeDetail.getCustomerEligibilityCheck();

		String returnType = finElgDetail.getRuleResultType();
		RuleReturnType ruleReturnType = null;

		if (StringUtils.equals(returnType, RuleReturnType.BOOLEAN.value())) {
			ruleReturnType = RuleReturnType.BOOLEAN;
		} else if (StringUtils.equals(returnType, RuleReturnType.DECIMAL.value())) {
			ruleReturnType = RuleReturnType.DECIMAL;
		} else if (StringUtils.equals(returnType, RuleReturnType.STRING.value())) {
			ruleReturnType = RuleReturnType.STRING;
		} else if (StringUtils.equals(returnType, RuleReturnType.INTEGER.value())) {
			ruleReturnType = RuleReturnType.INTEGER;
		} else if (StringUtils.equals(returnType, RuleReturnType.OBJECT.value())) {
			ruleReturnType = RuleReturnType.OBJECT;
		}

		Object object = RuleExecutionUtil.executeRule(finElgDetail.getElgRuleValue(),
				customerEligibilityCheck.getDeclaredFieldValues(), finCcy, ruleReturnType);

		String resultValue = null;
		switch (ruleReturnType) {
		case DECIMAL:
			if (object != null && object instanceof BigDecimal) {
				// unFormating object
				int formatter = CurrencyUtil.getFormat(finCcy);
				object = PennantApplicationUtil.unFormateAmount((BigDecimal) object, formatter);
			}
			finElgDetail.setRuleResult(object.toString());
			break;
		case INTEGER:
			finElgDetail.setRuleResult(object.toString());
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

		case OBJECT: // FIXME to discuss with Sathish
			RuleResult ruleResult = (RuleResult) object;
			Object resultval = ruleResult.getValue();
			Object resultvalue = ruleResult.getDeviation();
			List<DeviationHeader> list = deviationConfigService.getDeviationsbyModule(financeType.getFinType(),
					DeviationConstants.TY_ELIGIBILITY);

			if (resultval instanceof Double) {
				BigDecimal tempResult = new BigDecimal(resultval.toString());
				finElgDetail.setRuleResult(tempResult.toString());
			} else if (resultval instanceof Integer) {
				BigDecimal tempResult = new BigDecimal(resultval.toString());
				finElgDetail.setRuleResult(tempResult.toString());
			}

			for (DeviationHeader deviationHeader : list) {
				if (deviationHeader.getModuleCode().equals(String.valueOf(finElgDetail.getElgRuleCode()))) {
					List<DeviationDetail> details = deviationHeader.getDeviationDetails();
					if (details != null && !details.isEmpty()) {
						FinanceDeviations deviations = getNewFindevations(deviationHeader,
								financeMain.getFinReference(), DeviationConstants.TY_ELIGIBILITY);
						return processNewDevaitions(deviationHeader, resultvalue, deviations);
					}
				}
			}

			break;

		default:
			// do-nothing
			break;
		}

		logger.debug("Leaving");
		return null;

	}

	/**
	 * To Check list deviations
	 * 
	 * @param aFinRefDetail
	 * @param financeDetail
	 * @param devType
	 * @param devVal
	 * @return
	 */
	public FinanceDeviations checkCheckListDeviations(long finRefId, FinanceDetail financeDetail, String devType,
			int devVal) {
		logger.debug("Entering");

		FinanceType financeType = financeDetail.getFinScheduleData().getFinanceType();
		FinanceMain financeMain = financeDetail.getFinScheduleData().getFinanceMain();

		String devRef = finRefId + devType;
		Object resultvalue = null;

		if (DeviationConstants.CL_WAIVED.equals(devType)) {
			resultvalue = true;
		} else {
			resultvalue = devVal;
		}

		List<DeviationHeader> list = deviationConfigService.getDeviationsbyModule(financeType.getFinType(),
				DeviationConstants.TY_CHECKLIST);

		for (DeviationHeader deviationHeader : list) {

			if (deviationHeader.getModuleCode().equals(devRef)) {

				List<DeviationDetail> details = deviationHeader.getDeviationDetails();

				if (details != null && !details.isEmpty()) {

					FinanceDeviations deviations = getNewFindevations(deviationHeader, financeMain.getFinReference(),
							DeviationConstants.TY_CHECKLIST);

					String role = getRoleFromValue(resultvalue, deviationHeader);

					deviations.setDelegationRole(role);
					deviations.setDeviationValue(String.valueOf(resultvalue));

					return deviations;
				}
			}
		}

		logger.debug("Leaving");
		return null;

	}

	public boolean checkDeviationForDocument(CustomerDocument aCustomerDocument) {
		logger.debug(" Entering ");
		String docType = aCustomerDocument.getCustDocCategory();
		FinanceType financeType = financeMainBaseCtrl.getFinanceDetail().getFinScheduleData().getFinanceType();
		CheckListDetail checklistDet = checkListDetailService.getCheckListDetailByDocType(docType,
				financeType.getFinType());
		if (checklistDet == null) {
			return false;
		}
		List<DeviationHeader> list = deviationConfigService.getDeviationsbyModule(financeType.getFinType(),
				DeviationConstants.TY_CHECKLIST);

		if (list != null && !list.isEmpty()) {
			for (DeviationHeader deviationHeader : list) {
				String val = StringUtils.trimToEmpty(deviationHeader.getModuleCode());
				String[] array = val.split("_");
				if (array[0].equals(String.valueOf(checklistDet.getCheckListId()))) {
					return true;
				}

			}
		}
		return false;
	}

	/**
	 * To fee deviations
	 * 
	 * @param financeDetail
	 * @return
	 */
	public List<FinanceDeviations> checkFeeDeviations(FinanceDetail financeDetail) {
		logger.debug(" Entering ");

		FinanceMain financeMain = financeDetail.getFinScheduleData().getFinanceMain();
		FinanceType financeType = financeDetail.getFinScheduleData().getFinanceType();

		List<FeeRule> feeruleList = financeDetail.getFinScheduleData().getFeeRules();

		List<DeviationHeader> headerList = deviationConfigService.getDeviationsbyModule(financeType.getFinType(),
				DeviationConstants.TY_FEE);
		List<FinanceDeviations> deviationslist = new ArrayList<FinanceDeviations>();

		for (FeeRule feeRule : feeruleList) {

			for (DeviationHeader deviationHeader : headerList) {

				if (feeRule.getFeeCode().equals(deviationHeader.getModuleCode())
						&& feeRule.getWaiverAmount().compareTo(BigDecimal.ZERO) > 0) {

					List<DeviationDetail> detailsList = deviationHeader.getDeviationDetails();

					if (detailsList != null && !detailsList.isEmpty()) {

						// Calculate Percentage
						Object object = (feeRule.getWaiverAmount().divide(feeRule.getFeeAmount(), 2,
								RoundingMode.HALF_DOWN)).multiply(new BigDecimal(100));

						FinanceDeviations deviations = getNewFindevations(deviationHeader,
								financeMain.getFinReference(), DeviationConstants.TY_FEE);

						deviations = processNewDevaitions(deviationHeader, object, deviations);

						if (deviations != null) {
							deviationslist.add(deviations);

						}
					}
				}
			}
		}

		fillDeviationListbox(deviationslist, userRole, DeviationConstants.TY_FEE);

		logger.debug(" Leaving ");
		return deviationslist;
	}

	/**
	 * To Scoring deviations
	 * 
	 * @param financeDetail
	 * @return
	 */
	public FinanceDeviations checkScoringDeviations(FinanceDetail financeDetail, long GroupId, int minScore,
			int totcalScore) {
		logger.debug(" Entering ");

		FinanceMain financeMain = financeDetail.getFinScheduleData().getFinanceMain();
		FinanceType financeType = financeDetail.getFinScheduleData().getFinanceType();

		List<DeviationHeader> headerList = deviationConfigService.getDeviationsbyModule(financeType.getFinType(),
				DeviationConstants.TY_SCORE);

		int devValue = minScore - totcalScore;

		for (DeviationHeader deviationHeader : headerList) {

			if (String.valueOf(GroupId).equals(deviationHeader.getModuleCode()) && devValue > 0) {

				List<DeviationDetail> detailsList = deviationHeader.getDeviationDetails();

				if (detailsList != null && !detailsList.isEmpty()) {

					FinanceDeviations deviations = getNewFindevations(deviationHeader, financeMain.getFinReference(),
							DeviationConstants.TY_SCORE);

					return processNewDevaitions(deviationHeader, devValue, deviations);

				}
			}
		}

		logger.debug(" Leaving ");
		return null;
	}

	/**
	 * @param fintype
	 * @return
	 */
	public List<DeviationHeader> getProductDeviatations(String fintype) {
		return deviationConfigService.getDeviationsbyModule(fintype, DeviationConstants.TY_PRODUCT);
	}

	public void setFormat(int format) {
		this.format = format;
	}

	public void setUserid(long userid) {
		this.userid = userid;
	}

	public void setUserRole(String userRole) {
		this.userRole = userRole;
	}

	public List<FinanceDeviations> getFinanceDeviations() {
		return financeDeviations;
	}

	public void setFinanceDeviations(List<FinanceDeviations> financeDeviations) {
		this.financeDeviations = financeDeviations;
	}

	private List<FinanceDeviations> getApprovedFinanceDeviations() {
		return approvedFinanceDeviations;
	}

	public void setApprovedFinanceDeviations(List<FinanceDeviations> approvedFinanceDeviations) {
		this.approvedFinanceDeviations = approvedFinanceDeviations;
	}

	public void setFinanceMainBaseCtrl(FinanceMainBaseCtrl financeMainBaseCtrl) {
		this.financeMainBaseCtrl = financeMainBaseCtrl;
	}

	public void setCheckListDetailService(CheckListDetailService checkListDetailService) {
		this.checkListDetailService = checkListDetailService;
	}

	/**
	 * will return new deviation object
	 * 
	 * @param deviationHeader
	 * @param finref
	 * @param module
	 * @return
	 */
	private FinanceDeviations getNewFindevations(DeviationHeader deviationHeader, String finref, String module) {
		logger.debug(" Entering ");
		FinanceDeviations deviations = new FinanceDeviations();
		deviations.setFinReference(finref);
		deviations.setModule(module);
		deviations.setDeviationCode(deviationHeader.getModuleCode());
		deviations.setDeviationType(deviationHeader.getValueType());
		deviations.setUserRole(this.userRole);
		deviations.setApprovalStatus("");
		deviations.setDeviationDate(new Timestamp(System.currentTimeMillis()));
		deviations.setDeviationUserId(String.valueOf(this.userid));
		deviations.setDeviationCategory(DeviationConstants.CAT_AUTO);
		logger.debug(" Leaving ");
		return deviations;
	}

	/**
	 * will return the role based on the exeVal from the deviationDetails
	 * 
	 * @param exeVal
	 * @param deviationDetails
	 * @return
	 */
	private String processBooleanValue(Boolean exeVal, List<DeviationDetail> deviationDetails) {
		logger.debug(" Entering ");
		if (exeVal) {
			for (DeviationDetail deviationDetail : deviationDetails) {
				Integer confValue = Integer.parseInt(deviationDetail.getDeviatedValue());
				if (confValue == 1) {
					logger.debug(" Leaving ");
					return deviationDetail.getUserRole();
				}
			}

			/* means deviation not allowed */
			logger.debug(" Leaving ");
			return "";
		}
		logger.debug(" Leaving ");
		return null;
	}

	/**
	 * will return the role based on the devValue from the list
	 * 
	 * @param devValue
	 * @param list
	 * @return
	 */
	private String processIntegerValue(int devValue, List<DeviationDetail> list) {
		logger.debug(" Entering ");
		if (devValue != 0) {

			int intialVal = 0;

			for (DeviationDetail deviationDetail : list) {

				Integer confValue = Integer.parseInt(deviationDetail.getDeviatedValue());

				if (devValue > intialVal && devValue <= confValue) {
					logger.debug(" Leaving ");
					return deviationDetail.getUserRole();
				} else {
					intialVal = confValue;
				}
			}

			if (devValue > 0) {
				/* means deviation not allowed */
				logger.debug(" Leaving ");
				return "";
			}
		}

		logger.debug(" Leaving ");
		return null;
	}

	/**
	 * will return the role based on the devValue from the list
	 * 
	 * @param devValue
	 * @param list
	 * @return
	 */
	private String processdecimalValue(BigDecimal devValue, List<DeviationDetail> list) {
		logger.debug(" Entering ");

		if (devValue.compareTo(BigDecimal.ZERO) != 0) {

			BigDecimal intialVal = BigDecimal.ZERO;

			for (DeviationDetail deviationDetail : list) {
				BigDecimal confValue = new BigDecimal(deviationDetail.getDeviatedValue());

				if (devValue.compareTo(intialVal) > 0 && devValue.compareTo(confValue) <= 0) {
					logger.debug(" Leaving ");
					return deviationDetail.getUserRole();
				} else {
					intialVal = confValue;
				}
			}

			if (devValue.compareTo(BigDecimal.ZERO) > 0) {
				/* means deviation not allowed */
				logger.debug(" Leaving ");
				return "";
			}
		}

		logger.debug(" Leaving ");
		return null;
	}

	/**
	 * will return the role based on the deviation type
	 * 
	 * @param object
	 * @param deviationHeader
	 * @return
	 */
	private String getRoleFromValue(Object object, DeviationHeader deviationHeader) {
		logger.debug(" Entering ");

		switch (deviationHeader.getValueType()) {

		case DeviationConstants.DT_INTEGER:
			if (object instanceof Double) {
				Double exeVal = (Double) object;
				return processIntegerValue(exeVal.intValue(), deviationHeader.getDeviationDetails());
			} else if (object instanceof Integer) {
				Integer exeVal = (Integer) object;
				return processIntegerValue(exeVal, deviationHeader.getDeviationDetails());
			}
			break;

		case DeviationConstants.DT_BOOLEAN:
			if (object instanceof Boolean) {
				Boolean exeVal = (Boolean) object;
				return processBooleanValue(exeVal, deviationHeader.getDeviationDetails());
			} else if (object instanceof Integer) {
				int iValue = (Integer) object;
				Boolean exeVal = false;
				if (iValue == 1) {
					exeVal = true;
				}
				return processBooleanValue(exeVal, deviationHeader.getDeviationDetails());
			}
			break;
		case DeviationConstants.DT_DECIMAL:
			// in decimal case value is formatted based on currency and then
			// compared
			if (object instanceof Double) {
				Double exeVal = (Double) object;
				exeVal = exeVal / Math.pow(10, format);
				return processdecimalValue(new BigDecimal(exeVal), deviationHeader.getDeviationDetails());
			}
			break;
		case DeviationConstants.DT_PERCENTAGE:
			// in percentage case value is not formatted based on currency
			BigDecimal exeVal = null;
			if (object instanceof BigDecimal) {
				exeVal = (BigDecimal) object;
			} else if (object instanceof Double) {
				Double doubleVal = (Double) object;
				exeVal = new BigDecimal(doubleVal);
			}

			if (exeVal != null) {
				return processdecimalValue(exeVal, deviationHeader.getDeviationDetails());
			}
			break;
		case DeviationConstants.DT_STRING:
			/* Not Implemented */
			break;

		default:
			break;
		}

		logger.debug(" Leaving ");
		return null;

	}

	/**
	 * will return the new deviation if, it is not in the current deviation or previously Rejected
	 * 
	 * @param deviationHeader
	 * @param object
	 * @param deviation
	 * @return
	 */
	private FinanceDeviations processNewDevaitions(DeviationHeader deviationHeader, Object object,
			FinanceDeviations deviation) {
		String role = getRoleFromValue(object, deviationHeader);

		// No deviation available.
		if (role == null) {
			return null;
		}

		deviation.setDelegationRole(role);
		deviation.setDeviationValue(String.valueOf(object));

		if (isInApprovedList(getApprovedFinanceDeviations(), deviation)) {
			return null;
		} else {
			return deviation;
		}
	}

	/**
	 * To fill the deviations, it will remove the deviation with current role and will add the new deviations.
	 * 
	 * @param newList
	 * @param role
	 * @param devModule
	 */
	public void fillDeviationListbox(List<FinanceDeviations> newList, String role, String devModule) {
		logger.debug(" Entering ");

		removeTheOldDeviations(devModule, newList, financeDeviations);
		financeDeviations.addAll(newList);
		if (financeMainBaseCtrl != null && financeMainBaseCtrl.getDeviationDetailDialogCtrl() != null) {
			financeMainBaseCtrl.getDeviationDetailDialogCtrl().doFillAutoDeviationDetails(financeDeviations);
		}

		logger.debug(" Leaving ");
	}

	/**
	 * It will remove the deviation with given role and module.
	 * 
	 * @param devModule
	 * @param newList
	 * @param oldList
	 */
	private void removeTheOldDeviations(String devModule, List<FinanceDeviations> newList,
			List<FinanceDeviations> oldList) {
		logger.debug(Literal.ENTERING);

		Iterator<FinanceDeviations> it = oldList.iterator();
		FinanceDeviations deviation;
		boolean removeDeviation = true;

		while (it.hasNext()) {
			deviation = it.next();
			removeDeviation = true;

			if (deviation.getModule().equals(devModule)) {
				for (FinanceDeviations item : newList) {
					if (StringUtils.equals(deviation.getModule(), item.getModule())
							&& StringUtils.equals(deviation.getDeviationCode(), item.getDeviationCode())
							&& StringUtils.equals(deviation.getDeviationValue(), item.getDeviationValue())) {
						removeDeviation = false;
						break;
					}
				}

				if (removeDeviation) {
					it.remove();
				}
			}
		}

		it = newList.iterator();

		while (it.hasNext()) {
			deviation = it.next();
			removeDeviation = false;

			if (deviation.getModule().equals(devModule)) {
				for (FinanceDeviations item : oldList) {
					if (StringUtils.equals(deviation.getModule(), item.getModule())
							&& StringUtils.equals(deviation.getDeviationCode(), item.getDeviationCode())
							&& StringUtils.equals(deviation.getDeviationValue(), item.getDeviationValue())) {
						removeDeviation = true;
						break;
					}
				}

				if (removeDeviation) {
					it.remove();
				}
			}
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Check whether the deviation is already available in the approved list of deviations.
	 * 
	 * @param list      The list of deviations.
	 * @param deviation The deviation to check.
	 * @return <code>true</code> if the deviation is in approved list of deviations; otherwise <code>false</code>.
	 */
	private boolean isInApprovedList(List<FinanceDeviations> list, FinanceDeviations deviation) {
		logger.trace(Literal.ENTERING);

		for (FinanceDeviations item : list) {
			if (StringUtils.equals(deviation.getModule(), item.getModule())
					&& StringUtils.equals(deviation.getDeviationCode(), item.getDeviationCode())
					&& StringUtils.equals(deviation.getDeviationValue(), item.getDeviationValue())) {
				logger.trace(Literal.LEAVING);
				return true;
			}
		}

		logger.trace(Literal.LEAVING);
		return false;
	}

	/**
	 * to check given deviation not in the current list with different role but same value.
	 * 
	 * @param list
	 * @param deviations
	 * @return
	 */
	private boolean isInCurrentDevaitionList(List<FinanceDeviations> list, FinanceDeviations deviations) {
		logger.debug(" Entering ");

		for (FinanceDeviations financeDeviations : list) {
			/* Both Modules are different no need to compare */
			if (!deviations.getModule().equals(financeDeviations.getModule())) {
				continue;
			}

			if (StringUtils.equals(deviations.getDeviationCode(), financeDeviations.getDeviationCode())
					&& StringUtils.equals(deviations.getDeviationValue(), financeDeviations.getDeviationValue())
					&& !StringUtils.equals(deviations.getUserRole(), financeDeviations.getUserRole())) {
				logger.debug(" Leaving ");
				return true;
			}
		}

		logger.debug(" Leaving ");
		return false;

	}

	/**
	 * will return the false if, it is not in the current deviation or previous deviation
	 * 
	 * @param deviationHeader
	 * @param object
	 * @param deviations
	 * @return
	 */
	public boolean isAlreadyExsists(FinanceDeviations deviations) {

		if (isInApprovedList(getApprovedFinanceDeviations(), deviations)) {
			return true;
		} else if (isInCurrentDevaitionList(financeDeviations, deviations)) {
			return true;
		} else {
			return false;
		}

	}

	private Map<String, Object> prepareScriptEngine(FinanceMain financeMain, FinanceType financeType) {
		logger.debug(" Entering ");

		FinanceMain main = new FinanceMain();
		BeanUtils.copyProperties(financeMain, main);

		FinanceType type = new FinanceType();
		BeanUtils.copyProperties(financeType, type);
		// Convert amount for finance type Currency
		main.setFinAmount(CalculationUtil.getConvertedAmount(main.getFinCcy(), type.getFinCcy(), main.getFinAmount()));
		main.setDownPayment(
				CalculationUtil.getConvertedAmount(main.getFinCcy(), type.getFinCcy(), main.getDownPayment()));

		Map<String, Object> bindings = new HashMap<>();
		bindings.put("fm", main);
		bindings.put("ft", type);

		logger.debug(" Leaving ");
		return bindings;
	}

	/**
	 * To execute the Product deviation rule which does not contain any assignment in the rule to result the value . so
	 * we will assign the defined to result and will execute the rule
	 * 
	 * @param rule
	 * @param engine
	 * @return
	 */
	private Object executeRule(String rule, Map<String, Object> dataMap) {
		return RuleExecutionUtil.executeRule(rule, dataMap, RuleReturnType.OBJECT);
	}

	/**
	 * @param code
	 * @return
	 */
	private DeviationParam getDeviationparam(String code) {
		logger.debug(" Entering ");

		if (deviationParamsList != null && !deviationParamsList.isEmpty()) {
			for (DeviationParam param : deviationParamsList) {
				if (param.getCode().equals(code)) {
					logger.debug(" Leaving ");
					return param;
				}
			}
		}

		logger.debug(" Leaving ");
		return null;
	}

	/**
	 * Process the deviation and consider / ignore based on the result and previous history of the deviation, if
	 * available.
	 * 
	 * @param header    Deviation header object.
	 * @param category  Category of the deviation.
	 * @param module    Type of deviation.
	 * @param reference Finance reference number.
	 * @param result    Deviated value.
	 */
	private void processAutoDeviation(String category, String module, String code, Object result, String approverRole,
			String reference, DeviationHeader header, String desc) {
		logger.trace(Literal.ENTERING);

		String resultType;

		if (header == null) {
			resultType = "S";
		} else {
			code = header.getModuleCode();
			resultType = header.getValueType();

			// Get the deviation role.
			approverRole = getRoleFromValue(result, header);
		}

		// Check the existing approved & current deviations and delete.
		boolean approvedDeviationExists = DeviationUtil.isExists(getApprovedFinanceDeviations(), module, code);
		boolean currentDeviationExists = DeviationUtil.isExists(getFinanceDeviations(), module, code);

		// Two values to be considered 1) Null means No Deviation, 2) Empty means Deviation Not Allowed.
		// So empty check should not be required until unless all cases have been changed.
		if (approverRole == null) {
			/* No deviation found. */
			if (approvedDeviationExists) {
				deviationHelper.purgeDeviations(getApprovedFinanceDeviations(), module, code);
			}

			if (currentDeviationExists) {
				deviationHelper.removeDeviations(getFinanceDeviations(), module, code);
			}
		} else {
			/* Deviation found. */
			// Deviation raised for the first time.
			if (!approvedDeviationExists && !currentDeviationExists) {
				getFinanceDeviations().add(deviationHelper.createDeviation(category, module, reference, code, userRole,
						userid, approverRole, result, resultType, desc));
			} else {
				// Compare the deviation values.
				if (approvedDeviationExists && !currentDeviationExists) {
					if (DeviationUtil.isMatchFound(getApprovedFinanceDeviations(), module, code, result)) {
						deviationHelper.restoreDeviations(getApprovedFinanceDeviations(), module, code, result);
					} else {
						getFinanceDeviations().add(deviationHelper.createDeviation(category, module, reference, code,
								userRole, userid, approverRole, result, resultType, desc));
						deviationHelper.purgeDeviations(getApprovedFinanceDeviations(), module, code);
					}
				} else if (currentDeviationExists && !approvedDeviationExists) {
					if (DeviationUtil.isMatchFound(getFinanceDeviations(), module, code, result)) {
						// SKIP
					} else {
						FinanceDeviations deviation = DeviationUtil.getFirstDeviation(getFinanceDeviations(), module,
								code);

						deviationHelper.updateDeviation(deviation, userRole, userid, approverRole, result);
					}
				} else {
					boolean approvedDeviationMatched = false;
					boolean currentDeviationMatched = false;

					if (DeviationUtil.isMatchFound(getApprovedFinanceDeviations(), module, code, result)) {
						approvedDeviationMatched = true;
					}

					if (DeviationUtil.isMatchFound(getFinanceDeviations(), module, code, result)) {
						currentDeviationMatched = true;
					}

					if (!currentDeviationMatched && !approvedDeviationMatched) {
						FinanceDeviations deviation = DeviationUtil.getFirstDeviation(getFinanceDeviations(), module,
								code);

						deviationHelper.updateDeviation(deviation, userRole, userid, approverRole, result);
						deviationHelper.purgeDeviations(getApprovedFinanceDeviations(), module, code);
					} else if (approvedDeviationMatched) {
						FinanceDeviations deviation = DeviationUtil.getFirstDeviation(getFinanceDeviations(), module,
								code);

						getFinanceDeviations().remove(deviation);
						deviationHelper.restoreDeviations(getApprovedFinanceDeviations(), module, code, result);
					}
				}
			}
		}

		logger.trace(Literal.LEAVING);
	}

	/**
	 * Fill the list box with the auto deviations.
	 */
	private void fillAutoDeviations() {
		logger.trace(Literal.ENTERING);

		if (financeMainBaseCtrl != null && financeMainBaseCtrl.getDeviationDetailDialogCtrl() != null) {
			financeMainBaseCtrl.getDeviationDetailDialogCtrl().doFillAutoDeviationDetails(financeDeviations);
		}

		logger.trace(Literal.LEAVING);
	}

	public CollateralAssignmentDAO getCollateralAssignmentDAO() {
		return collateralAssignmentDAO;
	}

	public void setCollateralAssignmentDAO(CollateralAssignmentDAO collateralAssignmentDAO) {
		this.collateralAssignmentDAO = collateralAssignmentDAO;
	}

	public CollateralSetupFetchingService getCollateralSetupFetchingService() {
		return collateralSetupFetchingService;
	}

	public void setCollateralSetupFetchingService(CollateralSetupFetchingService collateralSetupFetchingService) {
		this.collateralSetupFetchingService = collateralSetupFetchingService;
	}

}
