package com.pennant.webui.delegationdeviation;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.script.Bindings;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.pennant.app.util.CalculationUtil;
import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.RuleExecutionUtil;
import com.pennant.backend.model.applicationmaster.CheckListDetail;
import com.pennant.backend.model.customermasters.CustomerDocument;
import com.pennant.backend.model.customermasters.CustomerEligibilityCheck;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceDeviations;
import com.pennant.backend.model.finance.FinanceEligibilityDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.model.rulefactory.FeeRule;
import com.pennant.backend.model.rulefactory.RuleResult;
import com.pennant.backend.model.solutionfactory.DeviationDetail;
import com.pennant.backend.model.solutionfactory.DeviationHeader;
import com.pennant.backend.model.solutionfactory.DeviationParam;
import com.pennant.backend.service.finance.CheckListDetailService;
import com.pennant.backend.delegationdeviation.DeviationConfigService;
import com.pennant.backend.util.DeviationConstants;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.RuleReturnType;
import com.pennant.util.PennantAppUtil;
import com.pennant.webui.finance.financemain.FinanceMainBaseCtrl;

public class DeviationExecutionCtrl {
	private static final Logger		logger				= Logger.getLogger(DeviationExecutionCtrl.class);

	private int						format				= 0;
	private String					userRole;
	private long					userid;
	private List<DeviationParam>	deviationParamsList	= PennantAppUtil.getDeviationParams();

	private FinanceMainBaseCtrl		financeMainBaseCtrl;
	private List<FinanceDeviations>	approvedFinanceDeviations;
	@Autowired
	private DeviationConfigService	deviationConfigService;
	private RuleExecutionUtil		ruleExecutionUtil;
	private CheckListDetailService	checkListDetailService;

	/* This list which hold the all deviation across the tab's */
	private List<FinanceDeviations>	financeDeviations	= new ArrayList<>();

	public DeviationExecutionCtrl() {
		super();
	}

	public boolean deviationAllowed(String product){
		return deviationConfigService.deviationAllowed(product);
	}
	/**
	 * To product deviations
	 * 
	 * @param financeDetail
	 * @return
	 * @throws ScriptException
	 */
	public List<FinanceDeviations> checkProductDeviations(FinanceDetail financeDetail) throws ScriptException {
		logger.debug(" Entering ");

		FinanceMain financeMain = financeDetail.getFinScheduleData().getFinanceMain();
		FinanceType financeType = financeDetail.getFinScheduleData().getFinanceType();

		ScriptEngine engine = prepareScriptEngine(financeMain, financeType);

		List<FinanceDeviations> deviationslist = new ArrayList<FinanceDeviations>();

		List<DeviationHeader> headerList = getProductDeviatations(financeType.getFinType());
		for (DeviationHeader deviationHeader : headerList) {
			List<DeviationDetail> detailsList = deviationHeader.getDeviationDetails();
			if (detailsList != null && !detailsList.isEmpty()) {

				DeviationParam deviationParam = getDeviationparam(deviationHeader.getModuleCode());
				if (deviationParam != null) {

					String formula = deviationParam.getFormula();

					Object object = executeRule(formula, engine);

					if (object != null) {
						FinanceDeviations deviations = getNewFindevations(deviationHeader,
								financeMain.getFinReference(), DeviationConstants.TY_PRODUCT);

						deviations = processNewDevaitions(deviationHeader, object, deviations);

						if (deviations != null) {
							deviationslist.add(deviations);
						}
					}
				}
			}
		}

		fillDeviationListbox(deviationslist, userRole, DeviationConstants.TY_PRODUCT);

		logger.debug(" Leaving ");
		return deviationslist;
	}

	/**
	 * To Eligibility deviations
	 * 
	 * @param finElgDetail
	 * @param financeDetail
	 * @return
	 * @throws ScriptException
	 */
	public FinanceDeviations checkEligibilityDeviations(FinanceEligibilityDetail finElgDetail,
			FinanceDetail financeDetail) throws ScriptException {
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

		Object object = this.ruleExecutionUtil.executeRule(finElgDetail.getElgRuleValue(),
				customerEligibilityCheck.getDeclaredFieldValues(), finCcy, ruleReturnType);

		String resultValue = null;
		switch (ruleReturnType) {
		case DECIMAL:
			if (object != null && object instanceof BigDecimal) {
				//unFormating object
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

		case OBJECT: //FIXME to discuss with Sathish
			RuleResult ruleResult = (RuleResult) object;
			Object resultval = ruleResult.getValue();
			Object resultvalue = ruleResult.getDeviation();
			List<DeviationHeader> list = deviationConfigService.getDeviationsbyModule(financeType.getFinType(),
					DeviationConstants.TY_ELIGIBILITY);

			if (resultval instanceof Double) {
				BigDecimal tempResult = new BigDecimal(resultval.toString());
				finElgDetail.setRuleResult(tempResult.toString());
			}else  if (resultval instanceof Integer) {
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
			//do-nothing
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

	public void setRuleExecutionUtil(RuleExecutionUtil ruleExecutionUtil) {
		this.ruleExecutionUtil = ruleExecutionUtil;
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
	 * @param deviations
	 * @return
	 */
	private FinanceDeviations processNewDevaitions(DeviationHeader deviationHeader, Object object,
			FinanceDeviations deviations) {

		String role = getRoleFromValue(object, deviationHeader);
		/*
		 * After Execution will add it to List. here two value to consider 1.Null means No Deviation , 2.Empty Means
		 * deviation Not Allowed So empty check should not be required until unless all cases have been changed.
		 */

		if (role != null) {

			deviations.setDelegationRole(role);
			deviations.setDeviationValue(String.valueOf(object));

			if (isInApprovedList(getApprovedFinanceDeviations(), deviations)) {
				return null;
			} else if (isInCurrentDevaitionList(financeDeviations, deviations)) {
				return null;
			} else {
				return deviations;
			}

		}

		return null;
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

		removeTheOldDeviation(devModule, role, financeDeviations);
		financeDeviations.addAll(newList);
		if (financeMainBaseCtrl != null && financeMainBaseCtrl.getDeviationDetailDialogCtrl() != null) {
			financeMainBaseCtrl.getDeviationDetailDialogCtrl().doFillAutoDeviationDetails(financeDeviations);
		}

		logger.debug(" Leaving ");
	}

	/**
	 * it will remove the deviation with given role and module
	 * 
	 * @param devModule
	 * @param role
	 * @param oldList
	 */
	private void removeTheOldDeviation(String devModule, String role, List<FinanceDeviations> oldList) {
		logger.debug(" Entering ");

		Iterator<FinanceDeviations> it = oldList.iterator();
		while (it.hasNext()) {
			FinanceDeviations financeDeviations = (FinanceDeviations) it.next();
			if (financeDeviations.getModule().equals(devModule) && financeDeviations.getUserRole().equals(role)) {
				it.remove();
			}

		}

		logger.debug(" Leaving ");
	}

	/**
	 * To check given records not already in approved list and not rejected previously
	 * 
	 * @param list
	 * @param deviations
	 * @return
	 */
	private boolean isInApprovedList(List<FinanceDeviations> list, FinanceDeviations deviations) {
		logger.debug(" Entering ");

		for (FinanceDeviations financeDeviations : list) {
			/* Both Modules are different no need to compare */
			if (!deviations.getModule().equals(financeDeviations.getModule())) {
				continue;
			}

			if (StringUtils.equals(deviations.getDeviationCode(), financeDeviations.getDeviationCode())
					&& StringUtils.equals(deviations.getDeviationValue(), financeDeviations.getDeviationValue())) {
				if (!PennantConstants.RCD_STATUS_REJECTED
						.equals(StringUtils.trim(financeDeviations.getApprovalStatus()))) {
					logger.debug(" Leaving ");
					return true;
				}
			}
		}

		logger.debug(" Leaving ");
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

	/* Rule Execution */

	/**
	 * To execute he rule
	 * 
	 * @param financeMain
	 * @param financeType
	 * @return
	 */
	private ScriptEngine prepareScriptEngine(FinanceMain financeMain, FinanceType financeType) {
		logger.debug(" Entering ");

		ScriptEngineManager factory = new ScriptEngineManager();

		FinanceMain main = new FinanceMain();
		BeanUtils.copyProperties(financeMain, main);

		FinanceType type = new FinanceType();
		BeanUtils.copyProperties(financeType, type);
		// Convert amount for finance type Currency
		main.setFinAmount(CalculationUtil.getConvertedAmount(main.getFinCcy(), type.getFinCcy(), main.getFinAmount()));
		main.setDownPayment(
				CalculationUtil.getConvertedAmount(main.getFinCcy(), type.getFinCcy(), main.getDownPayment()));

		Bindings bindings = factory.getBindings();
		bindings.put("fm", main);
		bindings.put("ft", type);
		ScriptEngine engine = factory.getEngineByName("JavaScript");

		logger.debug(" Leaving ");
		return engine;
	}

	/**
	 * To execute the Product deviation rule which does not contain any assignment in the rule to result the value . so
	 * we will assign the defined to result and will execute the rule
	 * 
	 * @param rule
	 * @param engine
	 * @return
	 * @throws ScriptException
	 */
	private Object executeRule(String rule, ScriptEngine engine) throws ScriptException {
		logger.debug(" Entering ");

		try {
			String jsfunction = "function Rule(){ Result = " + rule + "}Rule();";
			engine.eval(jsfunction);
			logger.debug(" Leaving ");
			return engine.get("Result");
		} catch (ScriptException scriptException) {
			throw scriptException;
		} catch (Exception e) {
			logger.debug(e);
		}

		logger.debug(" Leaving ");
		return null;

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

}
