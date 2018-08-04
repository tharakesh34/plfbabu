/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. All
 * components/modules/functions/classes/logic in this software, unless otherwise stated, the property of Pennant
 * Technologies.
 * 
 * Copyright and other intellectual property laws protect these materials. Reproduction or retransmission of the
 * materials, in whole or in part, in any manner, without the prior written consent of the copyright holder, is a
 * violation of copyright law.
 */

/**
 ******************************************************************************************** 
 * FILE HEADER *
 ******************************************************************************************** 
 * * FileName : DeviationHeaderServiceImpl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 22-06-2015 * *
 * Modified Date : 22-06-2015 * * Description : * *
 ******************************************************************************************** 
 * Date Author Version Comments *
 ******************************************************************************************** 
 * 22-06-2015 Pennant 0.1 * * * * * * * * *
 ******************************************************************************************** 
 */

package com.pennant.backend.delegationdeviation;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.zkoss.util.resource.Labels;
import org.zkoss.zkplus.spring.SpringUtil;

import com.pennant.backend.dao.administration.SecurityRoleDAO;
import com.pennant.backend.dao.applicationmaster.ManualDeviationDAO;
import com.pennant.backend.dao.lmtmasters.FinanceReferenceDetailDAO;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.administration.SecurityRole;
import com.pennant.backend.model.applicationmaster.ManualDeviation;
import com.pennant.backend.model.bmtmasters.CheckList;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceDeviations;
import com.pennant.backend.model.rmtmasters.ScoringGroup;
import com.pennant.backend.model.rulefactory.Rule;
import com.pennant.backend.model.solutionfactory.DeviationParam;
import com.pennant.backend.service.PagedListService;
import com.pennant.backend.util.DeviationConstants;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.backend.util.RuleConstants;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennanttech.pennapps.core.engine.workflow.WorkflowEngine;

public class DeviationHelper {
	private static final Logger			logger	= Logger.getLogger(DeviationHelper.class);

	@Autowired
	private SecurityRoleDAO				securityRoleDAO;
	@Autowired
	private ManualDeviationDAO			manualDeviationDAO;

	@Autowired
	private FinanceReferenceDetailDAO	financeReferenceDetailDAO;

	public List<ValueLabel> getRoleAndDesc(long workflowid) {
		WorkflowEngine workflow = new WorkflowEngine(WorkFlowUtil.getWorkflow(workflowid).getWorkFlowXml());
		List<String> list = workflow.getActors(true);
		return getRoleAndDesc(list);

	}

	public void setDeviationDetails(FinanceDetail financeDetail, List<FinanceDeviations> finDeviations,
			List<FinanceDeviations> apprFinDeviations) {

		financeDetail.setFinanceDeviations(getDeviationDetais(finDeviations, false));
		financeDetail.setManualDeviations(getDeviationDetais(finDeviations, true));

		financeDetail.setApprovedFinanceDeviations(getDeviationDetais(apprFinDeviations, false));
		financeDetail.setApprovedManualDeviations(getDeviationDetais(apprFinDeviations, true));
	}

	//FIXME should not use value label in DAO layer
	public List<ValueLabel> getRoleAndDesc(List<String> delegationRole) {
		logger.debug(" Entering ");
		List<ValueLabel> delgationRoles = new ArrayList<>();

		if (delegationRole == null || delegationRole.isEmpty()) {
			return delgationRoles;
		}

		List<SecurityRole> list = securityRoleDAO.getSecurityRolesByRoleCodes(delegationRole);
		//to maintain actual order
		if (list != null && !list.isEmpty()) {
			for (String degator : delegationRole) {
				ValueLabel valueLabel = new ValueLabel();
				valueLabel.setValue(degator);
				for (SecurityRole securityRole : list) {
					if (degator.equals(securityRole.getRoleCd())) {
						valueLabel.setLabel(securityRole.getRoleDesc());
						break;
					}
				}
				delgationRoles.add(valueLabel);
			}
		}

		logger.debug(" Leaving ");
		return delgationRoles;
	}

	public List<FinanceDeviations> mergeList(List<FinanceDeviations> list1, List<FinanceDeviations> list2) {

		List<FinanceDeviations> renderList = new ArrayList<FinanceDeviations>();

		if (list1 != null && !list1.isEmpty()) {
			renderList.addAll(list1);
		}

		if (list2 != null && !list2.isEmpty()) {
			renderList.addAll(list2);
		}

		return renderList;
	}

	public List<FinanceDeviations> getDeviationDetais(List<FinanceDeviations> list, boolean manual) {
		List<FinanceDeviations> deviations = new ArrayList<>();
		if (list == null || list.isEmpty()) {
			return deviations;
		}

		for (FinanceDeviations financeDeviations : list) {
			if (manual) {
				if (StringUtils.equals(DeviationConstants.CAT_MANUAL, financeDeviations.getDeviationCategory())) {
					deviations.add(financeDeviations);
				}
			} else {
				if (!StringUtils.equals(DeviationConstants.CAT_MANUAL, financeDeviations.getDeviationCategory())) {
					deviations.add(financeDeviations);
				}
			}
		}

		return deviations;

	}

	public String getDeviationDesc(FinanceDeviations deviationDetail, List<DeviationParam> deviationParams) {
		String devCode = deviationDetail.getDeviationCode();

		if (DeviationConstants.TY_PRODUCT.equals(deviationDetail.getModule())) {

			return getProlabelDesc(devCode, deviationParams);

		} else if (DeviationConstants.TY_ELIGIBILITY.equals(deviationDetail.getModule())) {

			return getRuleDesc(devCode, RuleConstants.MODULE_ELGRULE, null);

		} else if (DeviationConstants.TY_CHECKLIST.equals(deviationDetail.getModule())) {

			String temp = getChklabelDesc(devCode.substring(0, devCode.indexOf('_')));
			String cskDevType = devCode.substring(devCode.indexOf('_'));
			return temp + Labels.getLabel("deviation_checklist", new String[] { PennantStaticListUtil
					.getlabelDesc(cskDevType, PennantStaticListUtil.getCheckListDeviationType()) });

		} else if (DeviationConstants.TY_FEE.equals(deviationDetail.getModule())) {

			return getRuleDesc(null, RuleConstants.MODULE_FEES, devCode);

		} else if (DeviationConstants.TY_SCORE.equals(deviationDetail.getModule())) {

			return getScoreinglabelDesc(devCode);
		}

		return "";
	}

	/**
	 * @param value
	 * @param deviationParamsList
	 * @return
	 */
	private String getProlabelDesc(String value, List<DeviationParam> deviationParamsList) {

		if (deviationParamsList != null && !deviationParamsList.isEmpty()) {
			for (DeviationParam param : deviationParamsList) {
				if (param.getCode().equals(value)) {
					return param.getDescription();
				}

			}
		}
		return "";
	}

	/**
	 * @param ruleid
	 * @param ruleModule
	 * @param rulecode
	 * @return
	 */
	private String getRuleDesc(String ruleid, String ruleModule, String rulecode) {

		logger.debug(" Entering ");

		PagedListService pagedListService = (PagedListService) SpringUtil.getBean("pagedListService");

		JdbcSearchObject<Rule> searchObject = new JdbcSearchObject<Rule>(Rule.class);
		searchObject.addTabelName("Rules");
		if (!StringUtils.isEmpty(ruleid)) {
			searchObject.addFilterEqual("RuleId", Long.valueOf(ruleid));
		}
		if (!StringUtils.isEmpty(ruleModule)) {
			searchObject.addFilterEqual("RuleModule", ruleModule);
		}
		if (!StringUtils.isEmpty(rulecode)) {
			searchObject.addFilterEqual("RuleCode", rulecode);
		}

		List<Rule> list = pagedListService.getBySearchObject(searchObject);
		if (list != null && !list.isEmpty()) {
			logger.debug(" Leaving ");
			return list.get(0).getRuleCodeDesc();
		}

		logger.debug(" Leaving ");
		return "";
	}

	/**
	 * @param value
	 * @return
	 */
	private String getChklabelDesc(String value) {

		logger.debug(" Entering ");

		PagedListService pagedListService = (PagedListService) SpringUtil.getBean("pagedListService");

		JdbcSearchObject<CheckList> searchObject = new JdbcSearchObject<CheckList>(CheckList.class);
		searchObject.addTabelName("BMTCheckList");
		searchObject.addFilterIn("CheckListId", value);

		List<CheckList> list = pagedListService.getBySearchObject(searchObject);
		if (list != null && !list.isEmpty()) {

			logger.debug(" Leaving ");
			return list.get(0).getCheckListDesc();
		}

		logger.debug(" Leaving ");
		return "";
	}

	/**
	 * @param value
	 * @return
	 */
	private String getScoreinglabelDesc(String value) {

		logger.debug(" Entering ");

		PagedListService pagedListService = (PagedListService) SpringUtil.getBean("pagedListService");

		JdbcSearchObject<ScoringGroup> searchObject = new JdbcSearchObject<ScoringGroup>(ScoringGroup.class);
		searchObject.addTabelName("RMTScoringGroup");
		searchObject.addFilterIn("ScoreGroupId", value);

		List<ScoringGroup> list = pagedListService.getBySearchObject(searchObject);
		if (list != null && !list.isEmpty()) {

			logger.debug(" Leaving ");
			return list.get(0).getScoreGroupName();
		}

		logger.debug(" Leaving ");
		return "";
	}

	public String getDeviationValue(FinanceDeviations deviationDetail, int ccyformat) {

		String devType = deviationDetail.getDeviationType();
		String devValue = deviationDetail.getDeviationValue();

		if (DeviationConstants.DT_BOOLEAN.equals(devType)) {

			return devValue;

		} else if (DeviationConstants.DT_PERCENTAGE.equals(devType)) {

			return devValue + " % ";

		} else if (DeviationConstants.DT_DECIMAL.equals(devType)) {

			BigDecimal amount = new BigDecimal(devValue);
			return PennantApplicationUtil.amountFormate(amount, ccyformat);

		} else if (DeviationConstants.DT_INTEGER.equals(devType)) {

			BigDecimal amount = new BigDecimal(devValue);
			return Integer.toString(amount.intValue());
		}
		return "";
	}

	public static boolean isApproved(FinanceDeviations aFinanceDeviations) {
		if (StringUtils.isEmpty(aFinanceDeviations.getApprovalStatus())
				|| StringUtils.equals(aFinanceDeviations.getApprovalStatus(), PennantConstants.List_Select)) {
			return false;
		} else {
			return true;

		}
	}

	public ManualDeviation getManualDeviationDesc(long deviationID) {
		return manualDeviationDAO.getManualDeviationDesc(deviationID);
	}

	/**
	 * Method for checking the Limit Details
	 * 
	 * @param role
	 * @param finType
	 * @return
	 */
	public boolean checkInputAllowed(String finType, String role) {
		logger.debug("Entering");

		boolean allowed = false;
		int finRefType = FinanceConstants.PROCEDT_LIMIT;
		String quickDisbCode = FinanceConstants.MANUAL_DEVIATION;
		String roles = financeReferenceDetailDAO.getAllowedRolesByCode(finType, finRefType, quickDisbCode);

		if (StringUtils.isNotBlank(roles)) {
			String[] roleCodes = roles.split(PennantConstants.DELIMITER_COMMA);
			for (String roleCod : roleCodes) {
				if (StringUtils.equals(role, roleCod)) {
					allowed = true;
					break;
				}
			}
		}

		logger.debug("Leaving");

		return allowed;
	}
	// ### 06-05-2018 - Start - story #361(Tuleap server) Manual Deviations

	public List<ValueLabel> getRoleAndDesc(String finType, String finEvent, String module) {
		String workflowType = financeReferenceDetailDAO.getWorkflowType(finType, finEvent, module);
		long workflowid = financeReferenceDetailDAO.getWorkflowIdByType(workflowType);
		WorkflowEngine workflow = new WorkflowEngine(WorkFlowUtil.getWorkflow(workflowid).getWorkFlowXml());
		List<String> list = workflow.getActors(true);
		return getRoleAndDesc(list);

	}

	public String getAuthorities(String finType, int procedtLimit, String limitCode) {
		long limitid = financeReferenceDetailDAO.getLimitIdByLimitCode(limitCode);

		if (limitid == 0) {
			return null;
		}

		String authorities = financeReferenceDetailDAO.authorities(finType, procedtLimit, limitid);
		return authorities;
	}
	// ### 06-05-2018 - End

	public FinanceDeviations createDeviation(String category, String module, String reference, String code,
			String role, long userId, String approverRole, Object result, String resultType, String desc) {
		FinanceDeviations deviation = new FinanceDeviations();

		deviation.setFinReference(reference);
		deviation.setModule(module);
		deviation.setDeviationCode(code);
		deviation.setDeviationType(resultType);
		deviation.setUserRole(role);
		deviation.setApprovalStatus("");
		deviation.setDeviationDate(new Timestamp(System.currentTimeMillis()));
		deviation.setDeviationUserId(String.valueOf(userId));
		deviation.setDeviationCategory(category);
		deviation.setDelegationRole(approverRole);
		deviation.setDeviationValue(String.valueOf(result));
		deviation.setDeviationDesc(desc);

		return deviation;
	}

	public void updateDeviation(FinanceDeviations deviation, String role, long userId, String approverRole,
			Object result) {
		deviation.setUserRole(role);
		deviation.setDeviationDate(new Timestamp(System.currentTimeMillis()));
		deviation.setDeviationUserId(String.valueOf(userId));
		deviation.setDelegationRole(approverRole);
		deviation.setDeviationValue(String.valueOf(result));
	}

	public void purgeDeviations(List<FinanceDeviations> list, String module, String code) {
		for (FinanceDeviations item : list) {
			if (StringUtils.equals(module, item.getModule()) && StringUtils.equals(code, item.getDeviationCode())) {
				if (!item.isMarkDeleted()) {
					item.setMarkDeleted(true);
					item.setRecordType(PennantConstants.RCD_UPD);
				}
			}
		}
	}

	public void restoreDeviations(List<FinanceDeviations> list, String module, String code, Object result) {
		for (FinanceDeviations item : list) {
			if (StringUtils.equals(module, item.getModule()) && StringUtils.equals(code, item.getDeviationCode())) {
				if (StringUtils.equals(String.valueOf(result), item.getDeviationValue())) {
					if (item.isMarkDeleted()) {
						item.setMarkDeleted(false);
						item.setRecordType(PennantConstants.RCD_UPD);
					}
				} else {
					if (!item.isMarkDeleted()) {
						item.setMarkDeleted(true);
						item.setRecordType(PennantConstants.RCD_UPD);
					}
				}
			}
		}
	}

	public void removeDeviations(List<FinanceDeviations> list, String module, String code) {
		if (list == null || list.isEmpty()) {
			return;
		}

		@SuppressWarnings("unchecked")
		List<FinanceDeviations> result = (List<FinanceDeviations>) ((ArrayList<FinanceDeviations>) list).clone();

		for (FinanceDeviations item : result) {
			if (StringUtils.equals(module, item.getModule()) && StringUtils.equals(code, item.getDeviationCode())) {
				list.remove(item);
			}
		}
	}

	public List<FinanceDeviations> getValidCustomDeviations(List<FinanceDeviations> list, List<ValueLabel> delegators) {
		List<FinanceDeviations> deviations = new ArrayList<>();
		List<String> codes = new ArrayList<>();

		for (FinanceDeviations item : list) {
			if (item.getDeviationCode().length() <= 50 && !codes.contains(item.getDeviationCode())) {
				for (ValueLabel delegator : delegators) {
					if (item.getDelegationRole().contains(delegator.getValue())) {
						deviations.add(item);
						codes.add(item.getDeviationCode());

						break;
					}
				}
			}
		}

		return deviations;
	}
}
