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
 * * FileName : DeviationConfigCtrl.java * * Author : PENNANT TECHONOLOGIES * * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 05-05-2011 Satish.k 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.webui.delegationdeviation;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.zkoss.spring.SpringUtil;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listgroup;
import org.zkoss.zul.Listhead;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Textbox;

import com.pennant.backend.dao.rmtmasters.FinTypeAccountingDAO;
import com.pennant.backend.delegationdeviation.DeviationConfigService;
import com.pennant.backend.delegationdeviation.DeviationHelper;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.lmtmasters.FinanceReferenceDetail;
import com.pennant.backend.model.rmtmasters.FinTypeFees;
import com.pennant.backend.model.rulefactory.Rule;
import com.pennant.backend.model.solutionfactory.DeviationDetail;
import com.pennant.backend.model.solutionfactory.DeviationHeader;
import com.pennant.backend.model.solutionfactory.DeviationParam;
import com.pennant.backend.service.PagedListService;
import com.pennant.backend.util.DeviationConstants;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.util.PennantAppUtil;
import com.pennanttech.pennapps.core.model.LoggedInUser;

public class DeviationConfigCtrl {

	private static final Logger logger = LogManager.getLogger(DeviationConfigCtrl.class);

	// Variables
	private String fintype;
	private int percnetageFormatter = 2;
	private int finFormatter = 0;

	// Components
	protected Listbox delationDeviation;

	// Objects
	List<ValueLabel> delgationRoles;
	Map<String, Long> delegators = new HashMap<>();
	List<DeviationHeader> deviationHeaderList;
	@Autowired
	private transient DeviationConfigService deviationConfigService;
	private FinTypeAccountingDAO finTypeAccountingDAO;
	@Autowired
	DeviationHelper deviationHelper;

	// Constants
	private static final String styleCenter = "text-align: center;";
	private static final String styleListGroup = "font-weight: bold; background: #808080;  color: white;";
	private static final String styleBold = "font-weight: bold;";
	private static final String ATT_DATA_TYPE = "dataType";
	private static final String ATT_Module = "module";
	private static final String BOOLEAN_TRUE = "1";
	private static final String BOOLEAN_FALSE = "0";

	public DeviationConfigCtrl() {
	    super();
	}

	// Setters & Getters

	public FinTypeAccountingDAO getFinTypeAccountingDAO() {
		return finTypeAccountingDAO;
	}

	public void setFinTypeAccountingDAO(FinTypeAccountingDAO finTypeAccountingDAO) {
		this.finTypeAccountingDAO = finTypeAccountingDAO;
	}

	// Methods

	public void init(Listbox delationDeviation, String fintype, List<String> delegationRole) {
		this.delationDeviation = delationDeviation;
		this.fintype = fintype;
		delgationRoles = deviationHelper.getRoleAndDesc(delegationRole);

		// Specify the delegator ranking.
		long rank = 1;

		for (String delegator : delegationRole) {
			delegators.put(delegator, rank++);
		}

		fillListheaders(delationDeviation);
	}

	/**
	 * To process Delegation and deviation details
	 * 
	 * @param finType
	 * @param user
	 */
	public void processDeviationDelegation(String finType, LoggedInUser user) {
		validateDelegationDeviation();
		deviationConfigService.processDelegationDeviation(readDelegationDeviation(), fintype, user);
	}

	/**
	 * To fill Product deviation.Product Deviation are predefine in deviation params
	 */
	public void fillProductDeviations() {
		logger.debug(" Entering ");

		if (delgationRoles == null || delgationRoles.isEmpty()) {
			return;
		}

		deviationHeaderList = deviationConfigService.getDeviationsByFinType(fintype);

		List<DeviationParam> list = PennantAppUtil.getDeviationParams();

		if (list != null && !list.isEmpty()) {
			for (DeviationParam deviationParam : list) {
				addListItem(deviationParam.getCode(), deviationParam.getDescription(), deviationParam.getDataType(),
						DeviationConstants.TY_PRODUCT, this.delationDeviation);
			}
		}

		logger.debug(" Leaving ");
	}

	/**
	 * To fill eligibility deviation based on the allow deviation flag
	 * 
	 * @param listbox
	 */
	public void fillEligibilityDeviations(Listbox listbox) {
		logger.debug(" Entering ");

		if (delgationRoles == null || delgationRoles.isEmpty()) {
			return;
		}

		List<Listitem> list = listbox.getItems();

		if (list.isEmpty()) {
			removeListItem(DeviationConstants.TY_ELIGIBILITY);
			return;
		}

		for (Listitem listitem : list) {

			FinanceReferenceDetail referenceDetail = (FinanceReferenceDetail) listitem.getAttribute("data");
			Listitem deviationListItem = isAlredyInListbox(referenceDetail, DeviationConstants.TY_ELIGIBILITY);

			if (!listitem.isVisible()) {
				removeFromDeviation(deviationListItem);
				continue;
			}

			if (referenceDetail.isAllowDeviation()) {
				if (deviationListItem == null) {
					String dataType = DeviationConstants.DT_INTEGER;

					Rule rule = getElgRule(referenceDetail.getFinRefId());

					if (rule != null) {
						dataType = rule.getDeviationType();
					}
					addListItem(String.valueOf(referenceDetail.getFinRefId()),
							StringUtils.trimToEmpty(referenceDetail.getLovDescRefDesc()), dataType,
							DeviationConstants.TY_ELIGIBILITY, this.delationDeviation);
				}

			} else {
				removeFromDeviation(deviationListItem);
			}
		}

		logger.debug(" Leaving ");
	}

	/**
	 * To Fill Check List Deviations based on the flag's allow Expire allow postponed allow Waiver
	 * 
	 * @param listbox
	 */
	public void fillCheckListDeviations(Listbox listbox) {
		logger.debug(" Entering ");

		if (delgationRoles == null || delgationRoles.isEmpty()) {
			return;
		}

		List<Listitem> list = listbox.getItems();

		if (list.isEmpty()) {
			removeListItem(DeviationConstants.TY_CHECKLIST);
			return;
		}

		for (Listitem listitem : list) {

			FinanceReferenceDetail refDet = (FinanceReferenceDetail) listitem.getAttribute("data");

			String refExpired = String.valueOf(refDet.getFinRefId()) + DeviationConstants.CL_EXPIRED;
			String refPostponed = String.valueOf(refDet.getFinRefId()) + DeviationConstants.CL_POSTPONED;
			String refWaived = String.valueOf(refDet.getFinRefId()) + DeviationConstants.CL_WAIVED;

			String desc = StringUtils.trimToEmpty(refDet.getLovDescRefDesc());
			String descExpired = desc
					+ Labels.getLabel("deviation_checklist", new String[] { Labels.getLabel("checklist_Expired") });
			String descPostponed = desc
					+ Labels.getLabel("deviation_checklist", new String[] { Labels.getLabel("checklist_Postponed") });
			String descWaived = desc
					+ Labels.getLabel("deviation_checklist", new String[] { Labels.getLabel("checklist_Waived") });

			Listitem listItemExpired = isAlredyInListbox(refExpired, DeviationConstants.TY_CHECKLIST);
			Listitem listItemPostponed = isAlredyInListbox(refPostponed, DeviationConstants.TY_CHECKLIST);
			Listitem listItemWaived = isAlredyInListbox(refWaived, DeviationConstants.TY_CHECKLIST);

			if (!listitem.isVisible()) {
				removeFromDeviation(listItemExpired);
				removeFromDeviation(listItemPostponed);
				removeFromDeviation(listItemWaived);
				continue;
			}

			if (refDet.isAllowDeviation()) {

				addCheckListItem(refDet.isAllowExpire(), listItemExpired, refExpired, descExpired,
						DeviationConstants.DT_INTEGER);
				addCheckListItem(refDet.isAllowPostpone(), listItemPostponed, refPostponed, descPostponed,
						DeviationConstants.DT_INTEGER);
				addCheckListItem(refDet.isAllowWaiver(), listItemWaived, refWaived, descWaived,
						DeviationConstants.DT_BOOLEAN);

			} else {

				removeFromDeviation(listItemExpired);
				removeFromDeviation(listItemPostponed);
				removeFromDeviation(listItemWaived);

			}
		}

		logger.debug(" Leaving ");
	}

	/**
	 * To fill deviations based on the allow deviation flag in fee rule. Here fee rule are captured from two setups.
	 * i.e. Fee's configured in accounting setup in finance type creation Stage accounting configure
	 * 
	 * @param listbox
	 */
	public void fillFeeDeviations(Listbox listbox, String finType) {
		logger.debug(" Entering ");

		if (delgationRoles == null || delgationRoles.isEmpty()) {
			return;
		}

		/*
		 * List<Listitem> list = listbox.getItems();
		 * 
		 * List<Long> accountingsetIds = new ArrayList<Long>(); for (Listitem listitem : list) { FinanceReferenceDetail
		 * refDet = (FinanceReferenceDetail) listitem.getAttribute("data"); accountingsetIds.add(refDet.getFinRefId());
		 * }
		 * 
		 * List<Long> listAcc = getFinanceTypeAccountSetID(); if (listAcc == null || listAcc.isEmpty()) { return; }
		 * accountingsetIds.addAll(listAcc);
		 */
		List<FinTypeFees> feeCodes = deviationConfigService.getFeeCodeList(finType, FinanceConstants.MODULEID_FINTYPE);

		if (!feeCodes.isEmpty()) {

			/*
			 * List<String> actualFeeCodes = new ArrayList<>();
			 * 
			 * for (String singlefeecode : feeCodes) { if (singlefeecode.contains(",")) { String feecodes[] =
			 * singlefeecode.split(","); for (String feecode : feecodes) { if (!actualFeeCodes.contains(feecode)) {
			 * actualFeeCodes.add(feecode); } }
			 * 
			 * } else { if (!actualFeeCodes.contains(singlefeecode)) { actualFeeCodes.add(singlefeecode); } } }
			 * 
			 * List<Rule> listrules = getFeeRules(actualFeeCodes);
			 */
			for (FinTypeFees finTypeFee : feeCodes) {

				Listitem deviationListItem = isAlredyInListbox(finTypeFee.getFeeTypeCode(), DeviationConstants.TY_FEE);

				if (finTypeFee.isAlwDeviation()) {

					if (deviationListItem == null) {
						addListItem(String.valueOf(finTypeFee.getFeeTypeCode()),
								StringUtils.trimToEmpty(finTypeFee.getFeeTypeDesc()), DeviationConstants.DT_PERCENTAGE,
								DeviationConstants.TY_FEE, this.delationDeviation);
					}

				} else {
					removeFromDeviation(deviationListItem);
				}

			}
		}
		logger.debug(" Leaving ");
	}

	/**
	 * To fill Scoring deviations based on the allow deviation flag.
	 * 
	 * @param listbox
	 */
	public void fillScoringDeviations(Listbox listbox) {
		logger.debug(" Entering ");

		if (delgationRoles == null || delgationRoles.isEmpty()) {
			return;
		}

		List<Listitem> list = listbox.getItems();

		if (list.isEmpty()) {
			removeListItem(DeviationConstants.TY_SCORE);
			return;
		}

		for (Listitem listitem : list) {

			FinanceReferenceDetail referenceDetail = (FinanceReferenceDetail) listitem.getAttribute("data");
			Listitem deviationListItem = isAlredyInListbox(referenceDetail, DeviationConstants.TY_SCORE);

			if (!listitem.isVisible()) {
				removeFromDeviation(deviationListItem);
				continue;
			}

			if (referenceDetail.isAllowDeviation()) {
				if (deviationListItem == null) {
					String dataType = DeviationConstants.DT_INTEGER;
					addListItem(String.valueOf(referenceDetail.getFinRefId()),
							StringUtils.trimToEmpty(referenceDetail.getLovDescRefDesc()), dataType,
							DeviationConstants.TY_SCORE, this.delationDeviation);
				}

			} else {
				removeFromDeviation(deviationListItem);
			}
		}

		logger.debug(" Leaving ");
	}

	/**
	 * To Fill deviation details list headers
	 * 
	 * @param listbox
	 */
	private void fillListheaders(Listbox listbox) {
		logger.debug(" Entering ");
		if (delgationRoles == null || delgationRoles.isEmpty()) {
			return;
		}

		Listhead listhead = listbox.getListhead();
		if (listhead != null) {
			listhead.getChildren().clear();
		} else {
			listhead = new Listhead();
		}
		Listheader firstColumn = new Listheader("Deviations");
		firstColumn.setStyle(styleCenter);
		firstColumn.setWidth("20%");
		listhead.appendChild(firstColumn);

		for (ValueLabel valueLabel : delgationRoles) {
			Listheader listheader = new Listheader(valueLabel.getLabel());
			listheader.setStyle(styleCenter);
			// listheader.setHflex("min");
			// ID
			listheader.setId(valueLabel.getValue());

			listhead.appendChild(listheader);
		}
		listbox.appendChild(listhead);

		logger.debug(" Leaving ");
	}

	/**
	 * To add list item to the deviation list box
	 * 
	 * @param code
	 * @param desc
	 * @param dataType
	 * @param module
	 * @param listbox
	 */
	private void addListItem(String code, String desc, String dataType, String module, Listbox listbox) {
		logger.debug(" Entering ");

		addListGroup(module, listbox);

		Listitem listitem = new Listitem();
		// ID
		listitem.setId(code);
		listitem.setAttribute(ATT_DATA_TYPE, dataType);
		listitem.setAttribute(ATT_Module, module);

		Listcell listcell = new Listcell(desc);
		listcell.setStyle(styleBold);
		listcell.setParent(listitem);
		for (ValueLabel valueLabel : delgationRoles) {
			Listcell dlistcell = new Listcell("");
			dlistcell.setStyle(styleCenter);
			Component component = getComponentByType(dataType, getValueFromList(code, valueLabel.getValue()));
			if (component != null) {
				component.setId(getID(code, valueLabel.getValue()));
				dlistcell.appendChild(component);
			}
			dlistcell.setParent(listitem);
		}

		Listitem oldListitem = checkAndReturn(module);

		if (oldListitem == null) {
			listbox.appendChild(listitem);
		} else {
			listbox.insertBefore(listitem, oldListitem);
		}

		logger.debug(" Leaving ");
	}

	/**
	 * Adds the list group to the deviation list box if not found
	 * 
	 * @param module
	 * @param listbox
	 */
	private void addListGroup(String module, Listbox listbox) {
		logger.debug(" Entering ");

		Listitem list = isAlredyInListbox(module, module);
		if (list == null) {
			Listgroup listitem = new Listgroup();
			listitem.setId(module);
			Listcell listcell = new Listcell(Labels.getLabel("listGroup_" + module));
			listcell.setStyle(styleListGroup);
			listitem.setAttribute(ATT_Module, module);
			listcell.setParent(listitem);
			listbox.appendChild(listitem);
		}

		logger.debug(" Leaving ");
	}

	/**
	 * @param module
	 */
	private void removeListItem(String module) {
		logger.debug(" Entering ");

		List<Listitem> itemsRemove = new ArrayList<Listitem>(1);
		List<Listitem> items = this.delationDeviation.getItems();
		for (Listitem listitem : items) {
			String mod = (String) listitem.getAttribute(ATT_Module);
			if (StringUtils.equals(module, mod)) {
				itemsRemove.add(listitem);
			}
		}

		for (Listitem listitem : itemsRemove) {
			removeFromDeviation(listitem);
		}

		logger.debug(" Leaving ");
	}

	private String getID(String string1, String string2) {
		StringBuilder builder = new StringBuilder(string1);
		builder.append("_");
		builder.append(string2);
		return builder.toString();
	}

	/**
	 * 
	 * TO return the Component based on the type and will set the passed value to the component
	 * 
	 * @param dataType
	 * @param value
	 * @return
	 */
	private Component getComponentByType(String dataType, String value) {
		logger.debug(" Entering ");

		switch (StringUtils.trimToEmpty(dataType)) {

		case DeviationConstants.DT_STRING:

			Textbox textbox = new Textbox();
			textbox.setValue(StringUtils.trimToEmpty(value));

			logger.debug(" Leaving ");
			return textbox;

		case DeviationConstants.DT_INTEGER:

			Intbox intbox = new Intbox();
			intbox.setMaxlength(10);
			try {
				if (!StringUtils.isEmpty(value)) {
					intbox.setValue(Integer.parseInt(value));
				}
			} catch (NumberFormatException e) {
				logger.debug("Exception: ", e);
			}

			logger.debug(" Leaving ");
			return intbox;

		case DeviationConstants.DT_BOOLEAN:

			Checkbox checkbox = new Checkbox();
			if (StringUtils.equals(value, BOOLEAN_TRUE)) {
				checkbox.setChecked(true);
			} else {
				checkbox.setChecked(false);
			}

			logger.debug(" Leaving ");
			return checkbox;

		case DeviationConstants.DT_DECIMAL:

			Decimalbox decimalbox = new Decimalbox();
			decimalbox.setWidth("175px");
			decimalbox.setMaxlength(18);
			decimalbox.setFormat(PennantApplicationUtil.getAmountFormate(finFormatter));
			decimalbox.setScale(finFormatter);
			if (!StringUtils.isEmpty(value)) {
				decimalbox.setValue(value);
			}

			logger.debug(" Leaving ");
			return decimalbox;

		case DeviationConstants.DT_PERCENTAGE:

			Decimalbox perdecimalbox = new Decimalbox();
			perdecimalbox.setFormat(PennantApplicationUtil.getAmountFormate(percnetageFormatter));
			perdecimalbox.setScale(percnetageFormatter);
			perdecimalbox.setMaxlength(6);
			if (!StringUtils.isEmpty(value)) {
				perdecimalbox.setValue(value);
			}

			logger.debug(" Leaving ");
			return perdecimalbox;

		default:
			break;
		}

		logger.debug(" Leaving ");
		return null;
	}

	/**
	 * To get the value from the component based on the component type
	 * 
	 * @param compon
	 * @return
	 */
	private String getValueFromComponent(Component compon) {
		logger.debug(" Entering ");

		if (compon instanceof Textbox) {
			Textbox textbox = (Textbox) compon;

			logger.debug(" Leaving ");
			return textbox.getValue();

		} else if (compon instanceof Intbox) {

			Intbox intbox = (Intbox) compon;

			logger.debug(" Leaving ");
			return Integer.toString(intbox.intValue());

		} else if (compon instanceof Checkbox) {

			Checkbox checkbox = (Checkbox) compon;

			if (checkbox.isChecked()) {

				logger.debug(" Leaving ");
				return BOOLEAN_TRUE;
			} else {

				logger.debug(" Leaving ");
				return BOOLEAN_FALSE;
			}

		} else if (compon instanceof Decimalbox) {

			Decimalbox decimalbox = (Decimalbox) compon;

			if (decimalbox.getValue() != null) {

				logger.debug(" Leaving ");
				return decimalbox.getValue().toString();
			}

		}

		logger.debug(" Leaving ");
		return null;
	}

	/**
	 * Will read the Deviation list box and prepared object structure.
	 * 
	 * @return
	 */
	private List<DeviationHeader> readDelegationDeviation() {
		logger.debug(" Entering ");

		List<DeviationHeader> deviationHeaders = new ArrayList<DeviationHeader>();

		List<Listitem> listitems = this.delationDeviation.getItems();

		for (Listitem listitem : listitems) {
			if (listitem instanceof Listgroup) {
				continue;
			}

			String devCode = listitem.getId();
			String dataType = (String) listitem.getAttribute(ATT_DATA_TYPE);
			String module = (String) listitem.getAttribute(ATT_Module);

			DeviationHeader deviationHeader = new DeviationHeader();
			deviationHeader.setFinType(fintype);
			deviationHeader.setModule(module);
			deviationHeader.setModuleCode(devCode);
			deviationHeader.setValueType(dataType);

			List<Listheader> listheads = this.delationDeviation.getListhead().getChildren();

			List<DeviationDetail> deviationDetails = new ArrayList<DeviationDetail>();

			for (Listheader listheader : listheads) {
				String roleCode = listheader.getId();
				if ("".equals(roleCode)) {
					continue;
				}
				Component compon = listitem.getFellowIfAny(getID(devCode, roleCode));
				String value = getValueFromComponent(compon);
				if (value != null && !value.equals(BOOLEAN_FALSE)) {
					DeviationDetail deviationDetail = new DeviationDetail();
					deviationDetail.setUserRole(roleCode);
					deviationDetail.setDeviatedValue(value);
					if (roleCode != null) {
						deviationDetail.setDelegatorGrade(delegators.getOrDefault(roleCode, (long) 0));
					}
					deviationDetails.add(deviationDetail);
				}

			}

			deviationHeader.setDeviationDetails(deviationDetails);
			deviationHeaders.add(deviationHeader);
		}

		logger.debug(" Leaving ");
		return deviationHeaders;
	}

	private void validateDelegationDeviation() {

		logger.debug(" Entering ");
		List<WrongValueException> exceptions = new ArrayList<>();

		List<Listitem> listitems = this.delationDeviation.getItems();

		for (Listitem listitem : listitems) {
			if (listitem instanceof Listgroup) {
				continue;
			}

			String devCode = listitem.getId();
			List<Listheader> listheads = this.delationDeviation.getListhead().getChildren();

			for (Listheader listheader : listheads) {
				String roleCode = listheader.getId();
				if ("".equals(roleCode)) {
					continue;
				}
				Component compon = listitem.getFellowIfAny(getID(devCode, roleCode));
				WrongValueException exp = getErrorByComponent(compon);
				if (exp != null) {
					exceptions.add(exp);
				}
			}

		}

		if (!exceptions.isEmpty()) {
			throw new WrongValuesException(exceptions.toArray(new WrongValueException[exceptions.size()]));
		}

		logger.debug(" Leaving ");
	}

	/**
	 * To get the value from the component based on the component type
	 * 
	 * @param compon
	 * @return
	 * @return
	 */
	private WrongValueException getErrorByComponent(Component compon) {
		logger.debug(" Entering ");

		if (compon instanceof Intbox) {
			Intbox intbox = (Intbox) compon;
			int temp = intbox.intValue();
			if (temp < 0) {
				return new WrongValueException(compon, Labels.getLabel("NUMBER_NOT_NEGATIVE", new String[] { "" }));
			}
			logger.debug(" Leaving ");
		} else if (compon instanceof Decimalbox) {
			Decimalbox decimalbox = (Decimalbox) compon;
			if (decimalbox.getValue() != null) {
				if (decimalbox.getValue().compareTo(BigDecimal.ZERO) < 0) {
					return new WrongValueException(compon, Labels.getLabel("NUMBER_NOT_NEGATIVE", new String[] { "" }));
				}
			}
		}

		logger.debug(" Leaving ");
		return null;
	}

	/**
	 * @param code
	 * @param userRole
	 * @return
	 */
	private String getValueFromList(String code, String userRole) {
		if (deviationHeaderList != null && !deviationHeaderList.isEmpty()) {
			for (DeviationHeader header : deviationHeaderList) {
				if (StringUtils.equals(header.getModuleCode(), code)) {
					List<DeviationDetail> list = header.getDeviationDetails();
					if (list != null && !list.isEmpty()) {
						for (DeviationDetail deviationDetail : list) {
							if (StringUtils.equals(deviationDetail.getUserRole(), userRole)) {
								return deviationDetail.getDeviatedValue();
							}
						}
					}
				}

			}
		}
		return null;
	}

	/**
	 * To add check List Item
	 * 
	 * @param deviated
	 * @param listitem
	 * @param code
	 * @param desc
	 * @param datatype
	 */
	private void addCheckListItem(boolean deviated, Listitem listitem, String code, String desc, String datatype) {
		if (deviated) {
			if (listitem == null) {
				addListItem(code, StringUtils.trimToEmpty(desc), datatype, DeviationConstants.TY_CHECKLIST,
						this.delationDeviation);
			}
		} else {
			removeFromDeviation(listitem);
		}
	}

	/**
	 * To remove the list item from list box
	 * 
	 * @param listitem
	 */
	private void removeFromDeviation(Listitem listitem) {
		if (listitem != null) {
			this.delationDeviation.removeItemAt(listitem.getIndex());
		}
	}

	private Listitem isAlredyInListbox(FinanceReferenceDetail referenceDetail, String module) {
		return isAlredyInListbox(String.valueOf(referenceDetail.getFinRefId()), module);
	}

	/**
	 * will check the given reference is already there in the list box or not
	 * 
	 * @param refeence
	 * @param module
	 * @return
	 */
	private Listitem isAlredyInListbox(String refeence, String module) {
		logger.debug(" Entering ");

		List<Listitem> itemList = this.delationDeviation.getItems();
		for (Listitem listitem : itemList) {
			if (listitem.getId().equals(String.valueOf(refeence))) {
				logger.debug(" Leaving ");
				return listitem;
			}

		}

		logger.debug(" Leaving ");
		return null;
	}

	/**
	 * Will return the immediate list after the given code. used for grouping
	 * 
	 * @param code
	 * @return
	 */
	private Listitem checkAndReturn(String code) {
		int count = 0;
		List<Listitem> itemList = this.delationDeviation.getItems();
		for (Listitem listitem : itemList) {
			if (count == 1) {

				logger.debug(" Leaving ");
				return listitem;
			}

			if (listitem.getId().equals(String.valueOf(code))) {
				count = 1;
			}

		}

		logger.debug(" Leaving ");
		return null;
	}

	/**
	 * To Eligibility Rule
	 * 
	 * @param ruleId
	 * @return
	 */
	private Rule getElgRule(long ruleId) {
		logger.debug(" Entering ");

		PagedListService pagedListService = (PagedListService) SpringUtil.getBean("pagedListService");

		JdbcSearchObject<Rule> searchObject = new JdbcSearchObject<Rule>(Rule.class);
		searchObject.addTabelName("Rules");
		searchObject.addFilterIn("RuleId", ruleId);

		List<Rule> list = pagedListService.getBySearchObject(searchObject);
		if (list != null && !list.isEmpty()) {

			logger.debug(" Leaving ");
			return list.get(0);
		}

		logger.debug(" Leaving ");
		return null;
	}

	public int getFinFormatter() {
		return finFormatter;
	}

	public void setFinFormatter(int finFormatter) {
		this.finFormatter = finFormatter;
	}

}
