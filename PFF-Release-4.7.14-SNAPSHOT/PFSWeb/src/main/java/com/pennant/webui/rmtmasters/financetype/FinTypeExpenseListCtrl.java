/**
Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. 
 * All components/modules/functions/classes/logic in this software, unless 
 * otherwise stated, the property of Pennant Technologies. 
 * 
 * Copyright and other intellectual property laws protect these materials. 
 * Reproduction or retransmission of the materials, in whole or in part, in any manner, 
 * without the prior written consent of the copyright holder, is a violation of 
 * copyright law.
 */

/**
 ********************************************************************************************
 *                                 FILE HEADER                                              *
 ********************************************************************************************
 *																							*
 * FileName    		:  FinTypeExpenseListCtrl.java                                          * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  20-12-2017    														*
 *                                                                  						*
 * Modified Date    :  20-12-2017    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 20-12-2017       Pennant	                 0.1                                            * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 ********************************************************************************************
 */
package com.pennant.webui.rmtmasters.financetype;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Window;

import com.pennant.app.util.CurrencyUtil;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.rmtmasters.FinTypeExpense;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;

public class FinTypeExpenseListCtrl extends GFCBaseCtrl<FinTypeExpense> {

	private static final long		serialVersionUID	= 1L;
	private static final Logger		logger				= Logger.getLogger(FinTypeExpenseListCtrl.class);

	protected Window				window_FinTypeExpenseList;
	private Component				parent				= null;
	protected Listbox				listBoxFinTypeExpenseList;

	protected Button				btnNew_FinTypeExpenseList_ExpenseType;
	
	private Object					mainController;
	private String					roleCode			= "";
	private String					finCcy				= "";
	private String					finType				= "";
	protected int					moduleId			= 0;
	private boolean					isCompReadonly		= false;

	private List<FinTypeExpense>	finTypeExpenseList	= new ArrayList<FinTypeExpense>();

	/**
	 * default constructor.<br>
	 */
	public FinTypeExpenseListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "FinTypeExpenseList";
	}

	@SuppressWarnings("unchecked")
	public void onCreate$window_FinTypeExpenseList(Event event) throws Exception {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_FinTypeExpenseList);

		try {
			if (event.getTarget().getParent() != null) {
				parent = event.getTarget().getParent();
			}

			if (arguments.containsKey("roleCode")) {
				roleCode = (String) arguments.get("roleCode");
				getUserWorkspace().allocateRoleAuthorities((String) arguments.get("roleCode"), super.pageRightName);
			}

			if (arguments.containsKey("finType")) {
				finType = (String) arguments.get("finType");
			}

			if (arguments.containsKey("finCcy")) {
				finCcy = (String) arguments.get("finCcy");
			}
			
			if (arguments.containsKey("mainController")) {
				this.mainController = (Object) arguments.get("mainController");
			}

			if (arguments.containsKey("isCompReadonly")) {
				this.isCompReadonly = (boolean) arguments.get("isCompReadonly");
			}
			if (arguments.containsKey("finTypeExpenseList")) {
				this.finTypeExpenseList = (List<FinTypeExpense>) arguments.get("finTypeExpenseList");
			}
			doCheckRights();
			doShowDialog();
		} catch (Exception e) {
			MessageUtil.showError(e);
			window_FinTypeExpenseList.onClose();
		}

		logger.debug("Leaving");
	}

	private void doCheckRights() {
		logger.debug("Entering");

		this.btnNew_FinTypeExpenseList_ExpenseType.setVisible(!isCompReadonly);

		logger.debug("leaving");
	}

	private void doShowDialog()
			throws IllegalAccessException, IllegalArgumentException, NoSuchMethodException, SecurityException {
		logger.debug("Entering");

		doFillFinTypeExpenseType(getFinTypeExpenseList());

		if (parent != null) {
			this.listBoxFinTypeExpenseList.setHeight(borderLayoutHeight - 120 + "px");
			window_FinTypeExpenseList.appendChild(listBoxFinTypeExpenseList);
			parent.appendChild(this.window_FinTypeExpenseList);

			try {
				getMainController().getClass().getMethod("setFinTypeExpenseListCtrl", this.getClass())
						.invoke(mainController, this);
			} catch (InvocationTargetException e) {
				logger.error("Exception: ", e);
			}
		}

		logger.debug("leaving");
	}

	public void onClick$btnNew_FinTypeExpenseList_ExpenseType(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());

		Clients.clearWrongValue(this.listBoxFinTypeExpenseList);

		final FinTypeExpense finTypeExpense = new FinTypeExpense("");
		finTypeExpense.setFinType(this.finType);
		finTypeExpense.setNewRecord(true);
		showDetailFinTypeExpenseView(finTypeExpense);

		logger.debug("Leaving" + event.toString());
	}

	public void onFinTypeExpenseOrgItemDoubleClicked(ForwardEvent event) throws InterruptedException {
		logger.debug("Entering" + event.toString());

		Listitem item = (Listitem) event.getOrigin().getTarget();
		FinTypeExpense finTypeExpense = (FinTypeExpense) item.getAttribute("data");

		if (StringUtils.equals(PennantConstants.RECORD_TYPE_CAN, finTypeExpense.getRecordType())
				|| StringUtils.trimToEmpty(finTypeExpense.getRecordType()).equals(PennantConstants.RECORD_TYPE_DEL)) {
			MessageUtil.showError(Labels.getLabel("common_NoMaintainance"));

		} else {
			finTypeExpense.setNewRecord(false);
			
			showDetailFinTypeExpenseView(finTypeExpense);
		}

		logger.debug("Leaving" + event.toString());
	}

	private void showDetailFinTypeExpenseView(FinTypeExpense finTypeExpense) throws InterruptedException {

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("finTypeExpense", finTypeExpense);
		map.put("finTypeExpenseListCtrl", this);
		map.put("role", roleCode);
		map.put("ccyFormat", CurrencyUtil.getFormat(this.finCcy));

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/SolutionFactory/FinanceType/FinTypeExpenseDialog.zul", null,
					map);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
	}

	public void doFillFinTypeExpenseType(List<FinTypeExpense> finTypeExpenseList) {
		logger.debug("Entering");
		try {
			if (finTypeExpenseList != null) {
				setFinTypeExpenseList(finTypeExpenseList);
				this.listBoxFinTypeExpenseList.getItems().clear();

				Listcell lc;
				Listitem item;
				List<ValueLabel> feeCalTypeList = PennantStaticListUtil.getFeeCalculationTypes();
				for (FinTypeExpense finTypeExpense : finTypeExpenseList) {

					item = new Listitem();
					lc = new Listcell(finTypeExpense.getExpenseTypeCode());
					lc.setParent(item);
					lc = new Listcell(finTypeExpense.getExpenseTypeDesc());
					lc.setParent(item);
					lc = new Listcell(
							PennantStaticListUtil.getlabelDesc(finTypeExpense.getCalculationType(), feeCalTypeList));
					lc.setParent(item);

					if (!finTypeExpense.getAmount().equals(BigDecimal.ZERO)) {
						lc = new Listcell(PennantApplicationUtil.amountFormate(finTypeExpense.getAmount(),
								CurrencyUtil.getFormat(this.finCcy)));
					} else {
						lc = new Listcell(
								PennantApplicationUtil.formatRate(finTypeExpense.getPercentage().doubleValue(), 2));
					}
					lc.setStyle("text-align:right;");
					lc.setParent(item);
					
					lc = new Listcell();
					Checkbox modifyFeeCB = new Checkbox();
					modifyFeeCB.setChecked(finTypeExpense.isActive());
					modifyFeeCB.setDisabled(true);
					modifyFeeCB.setParent(lc);
					lc.setParent(item);
					item.setAttribute("data", finTypeExpense);
					ComponentsCtrl.applyForward(item, "onDoubleClick=onFinTypeExpenseOrgItemDoubleClicked");
					this.listBoxFinTypeExpenseList.appendChild(item);
				}
			}
		} catch (Exception e) {
			logger.debug(e);
		}
		logger.debug("Leaving");
	}

	public List<FinTypeExpense> doSave() {
		logger.debug("Entering");

		List<FinTypeExpense> finTypeExpensesList = new ArrayList<>();
		finTypeExpensesList.addAll(finTypeExpenseList);

		logger.debug("Leaving");
		return finTypeExpensesList;
	}

	public Object getMainController() {
		return mainController;
	}

	public void setMainController(Object mainController) {
		this.mainController = mainController;
	}

	public List<FinTypeExpense> getFinTypeExpenseList() {
		return finTypeExpenseList;
	}

	public void setFinTypeExpenseList(List<FinTypeExpense> finTypeExpenseList) {
		this.finTypeExpenseList = finTypeExpenseList;
	}

}
