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
 * FileName    		:  FinanceTypeListCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  30-06-2011    														*
 *                                                                  						*
 * Modified Date    :  30-06-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 30-06-2011       Pennant	                 0.1                                            * 
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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.converters.BigDecimalConverter;
import org.apache.commons.beanutils.converters.DateConverter;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.app.constants.ImplementationConstants;
import com.pennant.backend.model.rmtmasters.FinTypeAccount;
import com.pennant.backend.model.rmtmasters.FinTypeAccounting;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.service.rmtmasters.FinanceTypeService;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.search.Filter;
import com.pennant.webui.rmtmasters.financetype.model.FinanceTypeComparator;
import com.pennant.webui.rmtmasters.financetype.model.FinanceTypeListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.MessageUtil;
import com.pennant.webui.util.PTListReportUtils;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;

/**
 * This is the controller class for the following zul files <li>
 * /WEB-INF/pages/SolutionFactory/FinanceType/FinanceTypeList.zul.</li> <li>
 * /WEB-INF/pages/SolutionFactory/FinanceType/PromotionList.zul.</li>
 */
public class FinanceTypeListCtrl extends GFCBaseListCtrl<FinanceType> {
	private static final long serialVersionUID = -1491703348215991538L;
	private static final Logger logger = Logger.getLogger(FinanceTypeListCtrl.class);

	protected Window window_FinanceTypeList;
	protected Borderlayout borderLayout_FinanceTypeList;
	protected Listbox listBoxFinanceType;

	protected Listheader listheader_FinType;
	protected Listheader listheader_FinTypeDesc;
	protected Listheader listheader_FinCcy;
	protected Listheader listheader_FinBasicType;
	protected Listheader listheader_FinAcType;
	protected Listheader listheader_ProductType;
	protected Listheader listheader_SchdMthd;
	protected Listheader listheader_AlwGrace;
	protected Listheader listheader_FinDivision;

	protected Button button_FinanceTypeList_NewFinanceType;
	protected Button button_FinanceTypeList_FinanceTypeSearchDialog;

	protected Textbox finCategory;
	protected Textbox finType;
	protected Textbox finTypeDesc;
	protected Textbox finCcy;
	protected Combobox finDaysCalType;
	protected Combobox finSchdMthd;
	protected Checkbox finIsAlwGrace;
	protected Textbox finDivision;

	protected Listbox sortOperator_finType;
	protected Listbox sortOperator_finTypeDesc;
	protected Listbox sortOperator_finCcy;
	protected Listbox sortOperator_finDaysCalType;
	protected Listbox sortOperator_finSchdMthd;
	protected Listbox sortOperator_finIsAlwGrace;
	protected Listbox sortOperator_finDivision;

	private transient boolean isPromotion = false;
	private transient boolean isOverdraft = false;
	private transient FinanceTypeService financeTypeService;

	/**
	 * default constructor.<br>
	 */
	public FinanceTypeListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "FinanceType";
		super.pageRightName = "FinanceTypeList";
		super.tableName = "RMTFinanceTypes_AView";
		super.queueTableName = "RMTFinanceTypes_View";
		super.enquiryTableName = "RMTFinanceTypes_View";
	}

	@Override
	protected void doAddFilters() {
		super.doAddFilters();
		if (isPromotion) {
			this.searchObject.addFilterNotEqual("Product", "");
		} else {
			this.searchObject.addFilterEqual("Product", "");
			if(isOverdraft){
				this.searchObject.addFilterEqual("ProductCategory", FinanceConstants.PRODUCT_ODFACILITY);
			}else{
				//FIXME: Changed by Pradeep. Not sure about the previous condition correctness. Remove the comment after testing.
				if (ImplementationConstants.IMPLEMENTATION_CONVENTIONAL) {
					Filter[] filters = new Filter[2];
					filters[0] = new Filter("ProductCategory", FinanceConstants.PRODUCT_CONVENTIONAL, Filter.OP_EQUAL);
					filters[1] = new Filter("ProductCategory", FinanceConstants.PRODUCT_DISCOUNT, Filter.OP_EQUAL);
					this.searchObject.addFilterOr(filters);
				}else{
					this.searchObject.addFilterNotEqual("ProductCategory", FinanceConstants.PRODUCT_ODFACILITY);
					this.searchObject.addFilterNotEqual("ProductCategory", FinanceConstants.PRODUCT_CONVENTIONAL);
				}
			}
		}
	}

	@Override
	protected void doPrintResults() {
		String code = this.finCategory.getValue().charAt(0) + this.finCategory.getValue().substring(1).toLowerCase() + "Type";
		try {
			new PTListReportUtils(code, searchObject, -1);
		} catch (InterruptedException e) {
			logger.error("Exception:", e);
		}
	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onCreate$window_FinanceTypeList(Event event) {
		
		// Set the page level components.
		setPageComponents(window_FinanceTypeList, borderLayout_FinanceTypeList, listBoxFinanceType, null);
		
		if (StringUtils.trimToEmpty(finCategory.getValue()).equals(PennantConstants.WORFLOW_MODULE_PROMOTION)) {
			isPromotion = true;
		}
		
		if (StringUtils.trimToEmpty(finCategory.getValue()).equals(PennantConstants.WORFLOW_MODULE_OVERDRAFT)) {
			isOverdraft = true;
		}

		setItemRender(new FinanceTypeListModelItemRenderer(isOverdraft));
		setComparator(new FinanceTypeComparator());
		
		// Register buttons and fields.
		registerButton(button_FinanceTypeList_NewFinanceType, "button_FinanceTypeList_NewFinanceType", true);
		registerButton(button_FinanceTypeList_FinanceTypeSearchDialog);

		fillComboBox(this.finDaysCalType, "", PennantStaticListUtil.getProfitDaysBasis(), "");
		
		String ecldSchdmethods = ",NO_PAY,GRCNDPAY,";
		if(isOverdraft){
			ecldSchdmethods = ",EQUAL,GRCNDPAY,MAN_PRI,MANUAL,PRI,PRI_PFT,NO_PAY,";
		}
		
		fillComboBox(this.finSchdMthd, "", PennantStaticListUtil.getScheduleMethods(), ecldSchdmethods);

		registerField("product");
		registerField("finType", listheader_FinType, SortOrder.NONE, finType, sortOperator_finType, Operators.STRING);
		registerField("finTypeDesc", listheader_FinTypeDesc, SortOrder.NONE, finTypeDesc, sortOperator_finTypeDesc,
				Operators.STRING);
		registerField("finCcy", listheader_FinCcy, SortOrder.NONE, finCcy, sortOperator_finCcy, Operators.STRING);
		registerField("finDaysCalType", listheader_FinBasicType, SortOrder.NONE, finDaysCalType,
				sortOperator_finDaysCalType, Operators.STRING);
		/*registerField("finAcType", listheader_FinAcType, SortOrder.NONE);*/
		registerField("finSchdMthd", listheader_SchdMthd, SortOrder.NONE, finSchdMthd, sortOperator_finSchdMthd, Operators.STRING);
		if(!isOverdraft){
			registerField("fInIsAlwGrace", listheader_AlwGrace, SortOrder.NONE, finIsAlwGrace, sortOperator_finIsAlwGrace, Operators.BOOLEAN);
		}
		registerField("finDivision", listheader_FinDivision, SortOrder.NONE, finDivision, sortOperator_finDivision, Operators.STRING);
		registerField("FinCategoryDesc", SortOrder.DESC);
		registerField("FinCategory");
		
		// Render the page and display the data.
		doRenderPage();
		search();
	}

	/**
	 * The framework calls this event handler when user clicks the search button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$button_FinanceTypeList_FinanceTypeSearchDialog(Event event) {
		search();
	}

	/**
	 * The framework calls this event handler when user clicks the refresh button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$btnRefresh(Event event) throws InterruptedException {
		doReset();
		search();
	}

	/**
	 * The framework calls this event handler when user clicks the new button. Show the dialog page with a new entity.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */

	public void onClick$button_FinanceTypeList_NewFinanceType(Event event) throws Exception {
		logger.debug("Entering");
		// create a new FinanceType object, We GET it from the backEnd.
		final FinanceType aFinanceType = financeTypeService.getNewFinanceType();

		aFinanceType.setNewRecord(true);
		aFinanceType.setWorkflowId(getWorkFlowId());

		// aFinanceType.setFinScheduleOn("");
		boolean isCopyProcess = false;
		if (event.getData() != null) {
			BigDecimalConverter bigDecimalConverter = new BigDecimalConverter(null);
			ConvertUtils.register(bigDecimalConverter, BigDecimal.class);
			DateConverter dateConverter = new DateConverter(null);
			ConvertUtils.register(dateConverter, Date.class);
			FinanceType sourceFin = (FinanceType) event.getData();
			BeanUtils.copyProperties(aFinanceType, sourceFin);
			aFinanceType.setFinType("");
			aFinanceType.setFinTypeDesc("");
			aFinanceType.setNewRecord(true);
			aFinanceType.setRecordStatus("");
			isCopyProcess = true;
			List<FinTypeAccount> list = sourceFin.getFinTypeAccounts();
			if (list != null && !list.isEmpty()) {
				aFinanceType.setFinTypeAccounts(new ArrayList<FinTypeAccount>());
				for (FinTypeAccount finTypeAccount : list) {
					FinTypeAccount aFinTypeAccount = financeTypeService.getNewFinTypeAccount();
					aFinTypeAccount.setFinType(finTypeAccount.getFinType());
					aFinTypeAccount.setFinCcy(finTypeAccount.getFinCcy());
					aFinTypeAccount.setEvent(finTypeAccount.getEvent());
					aFinTypeAccount.setAlwManualEntry(finTypeAccount.isAlwManualEntry());
					aFinTypeAccount.setAlwCustomerAccount(finTypeAccount.isAlwCustomerAccount());
					aFinTypeAccount.setAccountReceivable(finTypeAccount.getAccountReceivable());
					aFinTypeAccount.setCustAccountTypes(finTypeAccount.getCustAccountTypes());
					aFinTypeAccount.setVersion(1);
					aFinTypeAccount.setRecordType(PennantConstants.RCD_ADD);
					aFinanceType.getFinTypeAccounts().add(aFinTypeAccount);
				}
			}
			
			List<FinTypeAccounting> fintypeAccountingList = sourceFin.getFinTypeAccountingList();
			if (fintypeAccountingList != null && !fintypeAccountingList.isEmpty()) {
				aFinanceType.setFinTypeAccountingList(new ArrayList<FinTypeAccounting>());
				for (FinTypeAccounting finTypeAccounting : fintypeAccountingList) {
					finTypeAccounting.setVersion(1);
					finTypeAccounting.setRecordType(PennantConstants.RCD_ADD);
					aFinanceType.getFinTypeAccountingList().add(finTypeAccounting);
				}
			}
		}

		Map<String, Object> map = getDefaultArguments();
		map.put("financeType", aFinanceType);
		map.put("isCopyProcess", isCopyProcess);
		map.put("isPromotion", isPromotion);
		map.put("alwCopyOption", this.button_FinanceTypeList_NewFinanceType.isVisible());
		map.put("financeTypeListCtrl", this);
		map.put("isOverdraft", isOverdraft);

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/SolutionFactory/FinanceType/SelectFinTypeDialog.zul", null, map);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}

		logger.debug("Leaving");
	}

	public void onOK() {
		logger.debug("Entering");
		search();
		logger.debug("Leaving");
	}

	/**
	 * The framework calls this event handler when user opens a record to view it's details. Show the dialog page with
	 * the selected entity.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onFinanceTypeItemDoubleClicked(Event event) {
		logger.debug("Entering");

		// get the selected FinanceType object
		final Listitem item = this.listBoxFinanceType.getSelectedItem();

		// CAST AND STORE THE SELECTED OBJECT
		String finType = (String) item.getAttribute("finType");
		FinanceType financeType = financeTypeService.getFinanceTypeById(finType);
		if (financeType == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}

		String whereCond = " AND FinType='" + financeType.getFinType() + "' AND version=" + financeType.getVersion() + " ";

		if (doCheckAuthority(financeType, whereCond)) {
			// Set the latest work-flow id for the new maintenance request.
			if (isWorkFlowEnabled() && financeType.getWorkflowId() == 0) {
				financeType.setWorkflowId(getWorkFlowId());
			}
			doShowDialogPage(financeType, false);
		} else {
			MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
		}

		logger.debug("Leaving");
	}

	/**
	 * Displays the dialog page with the required parameters as map.
	 * 
	 * @param aFinanceType
	 *            The entity that need to be passed to the dialog.
	 * @param isCopyProcess
	 */
	private void doShowDialogPage(FinanceType aFinanceType, boolean isCopyProcess) {
		logger.debug("Entering");

		Map<String, Object> map = getDefaultArguments();
		map.put("financeType", aFinanceType);
		map.put("isCopyProcess", isCopyProcess);
		map.put("isPromotion", isPromotion);
		map.put("alwCopyOption", this.button_FinanceTypeList_NewFinanceType.isVisible());
		map.put("financeTypeListCtrl", this);
		map.put("isOverdraft", isOverdraft);

		// call the ZUL-file with the parameters packed in a map
		try {
			if(isOverdraft){
				Executions.createComponents("/WEB-INF/pages/SolutionFactory/FinanceType/OverdraftFinanceTypeDialog.zul", null, map);
			}else{
				Executions.createComponents("/WEB-INF/pages/SolutionFactory/FinanceType/FinanceTypeDialog.zul", null, map);
			}
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving");
	}

	/**
	 * The framework calls this event handler when user clicks the print button to print the results.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$print(Event event) {
		doPrintResults();
	}

	/**
	 * The framework calls this event handler when user clicks the help button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$help(Event event) {
		doShowHelp(event);
	}

	public void setFinanceTypeService(FinanceTypeService financeTypeService) {
		this.financeTypeService = financeTypeService;
	}

}