/**
 * Copyright 2011 - Pennant Technologies
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
 * FileName    		:  ManagerChequeListCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  11-06-2015    														*
 *                                                                  						*
 * Modified Date    :  11-06-2015    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 11-06-2015       Pennant	                 0.1                                            * 
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

package com.pennant.webui.financemanagement.managercheque;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Menu;
import org.zkoss.zul.Menuitem;
import org.zkoss.zul.Menupopup;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.app.util.DateUtility;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.financemanagement.ManagerCheque;
import com.pennant.backend.service.financemanagement.ManagerChequeService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.util.PennantAppUtil;
import com.pennant.webui.financemanagement.managercheque.model.ManagerChequeListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.pennapps.jdbc.search.Filter;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennant.webui.util.searching.SearchOperators;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;
import com.pennanttech.framework.web.components.SearchFilterControl;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the /WEB-INF/pages/FinanceManagement/ManagerCheque/ManagerChequeList.zul file.<br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * 
 */
public class ManagerChequeListCtrl extends GFCBaseListCtrl<ManagerCheque> {

	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(ManagerChequeListCtrl.class);

	protected Window window_ManagerChequeList;
	protected Borderlayout borderLayout_ManagerChequeList;
	protected Paging pagingManagerChequeList;
	protected Listbox listBoxManagerCheque;

	protected Listheader listheader_ChqPurposeCode;
	protected Listheader listheader_ChequeRef;
	protected Listheader listheader_ChequeNo;
	protected Listheader listheader_BeneficiaryName;
	protected Listheader listheader_DraftCcy;
	protected Listheader listheader_ChequeAmount;
	protected Listheader listheader_CustCIF;
	protected Listheader listheader_ValueDate;

	protected Button button_ManagerChequeList_NewManagerCheque;
	protected Button button_ManagerChequeList_ManagerChequeSearch;

	protected Combobox chqPurposeCode;
	protected Textbox chequeRef;
	protected Textbox chequeNo;
	protected Textbox beneficiaryName;
	protected Textbox custCIF;
	protected Datebox valueDate_one;
	protected Datebox valueDate_two;
	protected Label label_valueDate;
	protected int oldVar_sortOperator_ValueDate = -1;
	protected Textbox draftCcy;
	protected Decimalbox chequeAmount;

	protected Listbox sortOperator_ChqPurposeCode;
	protected Listbox sortOperator_ChequeRef;
	protected Listbox sortOperator_ChequeNo;
	protected Listbox sortOperator_BeneficiaryName;
	protected Listbox sortOperator_custCIF;
	protected Listbox sortOperator_ValueDate;
	protected Listbox sortOperator_DraftCcy;
	protected Listbox sortOperator_ChequeAmount;

	private transient ManagerChequeService managerChequeService;

	protected Label label_menu_filter;
	protected Menu menu_filter;
	protected Menupopup menupopup_filter;

	protected FinanceMain financeMain = null;

	private MCType mcType = null;

	public enum MCType {
		MC(Labels.getLabel("window_ManagerChequeDialog.title")), RPMC(Labels
				.getLabel("window_ReprintManagerChequeDialog.title")), CMC(Labels
				.getLabel("window_CancelManagerChequeDialog.title"));

		private String title;

		private MCType(String title) {
			this.title = title;
		}

		public String getTitle() {
			return title;
		}
	}

	/**
	 * default constructor.<br>
	 */
	public ManagerChequeListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "ManagerCheque";
		super.pageRightName = "ManagerChequeList";
		super.tableName = "ManagerCheques_AView";
		super.queueTableName = "ManagerCheques_View";
		super.enquiryTableName = "ManagerCheques_TView";
		
		if (StringUtils.isNotEmpty(getArgument("moduleType"))) {
			this.mcType = MCType.valueOf(getArgument("moduleType"));
		}
	}

	@Override
	protected void doAddFilters() {
		super.doAddFilters();

		if (enqiryModule) {
			if (this.menu_filter.getLabel().equals(Labels.getLabel("label_ManagerCheques"))) {
				super.searchObject.addFilterEqual("Cancel", "0");
				super.searchObject.addFilterEqual("Reprint", "0");
			} else if (this.menu_filter.getLabel().equals(Labels.getLabel("label_Reprinted"))) {
				super.searchObject.addFilterEqual("Reprint", "1");
			} else if (this.menu_filter.getLabel().equals(Labels.getLabel("label_Cancelled"))) {
				super.searchObject.addFilterEqual("Cancel", "1");
			}
			menu_filter.setVisible(true);
		}

		if (MCType.MC == mcType) {
			super.searchObject.addWhereClause("(Reprint = 0 And Cancel = 0 And OldChequeID = 0 )");
			super.searchObject.addTabelName("ManagerCheques_TView");

		} else if (MCType.RPMC == mcType) {
			this.button_ManagerChequeList_NewManagerCheque.setVisible(false);
			this.searchObject
					.addWhereClause("(( RecordType = '' And Reprint = 0 And  Cancel = 0) Or ( RecordType <> '' And Reprint = 1 ))  ");
			this.searchObject.addTabelName("ManagerCheques_View");

		} else if (MCType.CMC == mcType) {
			this.button_ManagerChequeList_NewManagerCheque.setVisible(false);
			this.searchObject
					.addWhereClause("(( RecordType = '' And Cancel = 0) Or (RecordType <> '' And Cancel = 1)) And  Reprint = 0 ");
			this.searchObject.addTabelName("ManagerCheques_View");
		}

		// Value Date
		if (this.valueDate_one.getValue() != null || this.valueDate_two.getValue() != null) {

			// get the search operator
			final Listitem item_ValueDate = this.sortOperator_ValueDate.getSelectedItem();

			if (item_ValueDate != null) {
				final int searchOpId = ((SearchOperators) item_ValueDate.getAttribute("data")).getSearchOperatorId();

				if (searchOpId == -1) {
					// do nothing
				} else if (searchOpId == Filter.OP_BETWEEN) {
					if (this.valueDate_one.getValue() != null) {
						this.searchObject.addFilter(new Filter("ValueDate", DateUtility.formatUtilDate(
								this.valueDate_one.getValue(), PennantConstants.DBDateFormat),
								Filter.OP_GREATER_OR_EQUAL));
					}
					if (this.valueDate_two.getValue() != null) {
						this.searchObject
								.addFilter(new Filter("ValueDate", DateUtility.formatUtilDate(
										this.valueDate_two.getValue(), PennantConstants.DBDateFormat),
										Filter.OP_LESS_OR_EQUAL));
					}
				} else {
					this.searchObject.addFilter(new Filter("ValueDate", DateUtility.formatUtilDate(
							this.valueDate_one.getValue(), PennantConstants.DBDateFormat), searchOpId));
				}
			}
		}
	}

	@Override
	protected void doReset() {
		super.doReset();
		SearchFilterControl.resetFilters(valueDate_one, sortOperator_ValueDate);
		SearchFilterControl.resetFilters(valueDate_two);
		onChangeValueDateOperator();
	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onCreate$window_ManagerChequeList(Event event) {
		// Set the page level components.
		setPageComponents(window_ManagerChequeList, borderLayout_ManagerChequeList, listBoxManagerCheque,
				pagingManagerChequeList);
		setItemRender(new ManagerChequeListModelItemRenderer());

		// Register buttons and fields.
		registerButton(button_ManagerChequeList_NewManagerCheque, "", true);
		registerButton(button_ManagerChequeList_ManagerChequeSearch);

		fillComboBox(this.chqPurposeCode, "", PennantAppUtil.getChqPurposeCodes(true), "");

		registerField("chequeID");
		registerField("chqPurposeCode", listheader_ChqPurposeCode, SortOrder.ASC, chqPurposeCode,
				sortOperator_ChqPurposeCode, Operators.STRING);
		registerField("chequeRef", listheader_ChequeRef, SortOrder.NONE, chequeRef, sortOperator_ChequeRef,
				Operators.STRING);
		registerField("chequeNo", listheader_ChequeNo, SortOrder.NONE, chequeNo, sortOperator_ChequeNo,
				Operators.STRING);
		registerField("beneficiaryName", listheader_BeneficiaryName, SortOrder.NONE, beneficiaryName,
				sortOperator_BeneficiaryName, Operators.STRING);
		registerField("custCIF", listheader_CustCIF, SortOrder.NONE, custCIF, sortOperator_custCIF, Operators.STRING);
		registerField("valueDate", listheader_ValueDate);
		registerField("draftCcy", listheader_DraftCcy, SortOrder.NONE, draftCcy, sortOperator_DraftCcy,
				Operators.STRING);
		registerField("chequeAmount", listheader_ChequeAmount, SortOrder.NONE, chequeAmount, sortOperator_ChequeAmount,
				Operators.NUMERIC);
		registerField("chqPurposeCodeName");
		registerField("DraftCcyName");
		registerField("FundingCcyName");
		registerField("lovDescFundingCcyEditField");

		SearchFilterControl.renderOperators(this.sortOperator_ValueDate, Operators.DATE_RANGE);
		
		if(enqiryModule) {
			doFillFilterList(getMGRCHQEnqFilters());
		}

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
	public void onClick$button_ManagerChequeList_ManagerChequeSearch(Event event) {
		search();
	}

	/**
	 * The framework calls this event handler when user clicks the refresh button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$btnRefresh(Event event) {
		doReset();
		search();
	}

	/**
	 * The framework calls this event handler when user clicks the new button. Show the dialog page with a new entity.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$button_ManagerChequeList_NewManagerCheque(Event event) {
		logger.debug("Entering");

		// Create a new entity.
		ManagerCheque managerCheque = new ManagerCheque();
		managerCheque.setNewRecord(true);
		managerCheque.setWorkflowId(getWorkFlowId());

		// Display the dialog page.
		doShowDialogPage(managerCheque);

		logger.debug("Leaving");
	}

	/**
	 * The framework calls this event handler when user opens a record to view it's details. Show the dialog page with
	 * the selected entity.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onManagerChequeItemDoubleClicked(Event event) {
		logger.debug("Entering");

		// Get the selected record.
		Listitem selectedItem = this.listBoxManagerCheque.getSelectedItem();

		// Get the selected entity.
		long id = (long) selectedItem.getAttribute("id");
		ManagerCheque managerCheque = managerChequeService.getManagerChequeById(id);

		if (managerCheque != null && StringUtils.isNotEmpty(managerCheque.getChequeRef())) {
			financeMain = managerChequeService.getFinanceMainByFinReference(managerCheque.getChequeRef());
		} else {
			financeMain = null;
		}

		if (managerCheque == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}

		// Check whether the user has authority to change/view the record.
		String whereCond = " AND ChequeID ='" + managerCheque.getChequeID() + "'  AND version="
				+ managerCheque.getVersion() + " ";

		if (doCheckAuthority(managerCheque, whereCond)) {
			// Set the latest work-flow id for the new maintenance request.
			if (isWorkFlowEnabled() && managerCheque.getWorkflowId() == 0) {
				managerCheque.setWorkflowId(getWorkFlowId());
			}
			doShowDialogPage(managerCheque);
		} else {
			MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
		}

		logger.debug("Leaving");

	}

	/**
	 * Displays the dialog page with the required parameters as map.
	 * 
	 * @param managerCheque
	 *            The entity that need to be passed to the dialog.
	 */
	private void doShowDialogPage(ManagerCheque managerCheque) {
		logger.debug("Entering");

		Map<String, Object> arg = getDefaultArguments();
		arg.put("managerCheque", managerCheque);
		arg.put("financeMain", financeMain);
		arg.put("managerChequeListCtrl", this);
		arg.put("enqiryModule", enqiryModule);
		arg.put("mcType", mcType);

		try {
			if (managerCheque.isNewRecord()) {
				Executions.createComponents(
						"/WEB-INF/pages/FinanceManagement/ManagerCheque/SelectManagerChequeTypeDialog.zul", null, arg);
			} else {
				Executions.createComponents("/WEB-INF/pages/FinanceManagement/ManagerCheque/ManagerChequeDialog.zul",
						null, arg);
			}
		} catch (Exception e) {
			MessageUtil.showError(e);
		}

		logger.debug("Leaving");
	}

	/**
	 * Method for Rendering Menu item for Filtering
	 * 
	 * @param list
	 */
	private void doFillFilterList(List<ValueLabel> list) {
		logger.debug("Entering");

		this.menupopup_filter.getChildren().clear();
		if (list != null && list.size() > 0) {
			Menuitem menuitem = null;
			for (ValueLabel enquiry : list) {
				menuitem = new Menuitem();
				menuitem.setLabel(enquiry.getLabel());
				menuitem.setValue(enquiry.getValue());
				menuitem.setStyle("font-weight:bold;");
				menuitem.addForward("onClick", this.window_ManagerChequeList, "onFilterMenuItem", enquiry);

				this.menupopup_filter.appendChild(menuitem);
				if ("MGRCHQ".equals(enquiry.getValue())) {
					this.menu_filter.setLabel(enquiry.getLabel());
				}
			}
		}

		logger.debug("Leaving");
	}

	public void onFilterMenuItem(ForwardEvent event) {
		if (event.getData() != null) {
			ValueLabel enquiry = (ValueLabel) event.getData();
			this.menu_filter.setLabel(enquiry.getLabel());
			search();
		}
	}

	public static ArrayList<ValueLabel> getMGRCHQEnqFilters() {

		ArrayList<ValueLabel> mgrChqEnqFilters = new ArrayList<ValueLabel>(4);
		mgrChqEnqFilters.add(new ValueLabel("ALLMGRCHQ", Labels.getLabel("label_AllManagerCheques")));
		mgrChqEnqFilters.add(new ValueLabel("MGRCHQ", Labels.getLabel("label_ManagerCheques")));
		mgrChqEnqFilters.add(new ValueLabel("RPCHQ", Labels.getLabel("label_Reprinted")));
		mgrChqEnqFilters.add(new ValueLabel("CANCHQ", Labels.getLabel("label_Cancelled")));

		return mgrChqEnqFilters;
	}

	public void onSelect$sortOperator_ValueDate(Event event) {
		onChangeValueDateOperator();
	}

	private void onChangeValueDateOperator() {
		final Listitem item = sortOperator_ValueDate.getSelectedItem();
		final String searchOpId = ((ValueLabel)item.getAttribute("data")).getValue();
				
		this.valueDate_two.setText("");
		if (Integer.parseInt(searchOpId) == Filter.OP_BETWEEN) {
			this.valueDate_two.setVisible(true);
		} else {
			this.valueDate_two.setVisible(false);
		}
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

	public void setManagerChequeService(ManagerChequeService managerChequeService) {
		this.managerChequeService = managerChequeService;
	}

}