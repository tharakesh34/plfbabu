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
 * FileName    		:  ExpenseMovementDialogCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  05-05-2011    														*
 *                                                                  						*
 * Modified Date    :  05-05-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 05-05-2011       Pennant	                 0.1                                            * 
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
package com.pennant.webui.finance.enquiry;

import java.util.List;

import org.apache.log4j.Logger;
import org.zkoss.spring.SpringUtil;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.expenses.FinExpenseDetails;
import com.pennant.backend.model.expenses.FinExpenseMovements;
import com.pennant.webui.finance.enquiry.model.ExpenseMovementListModelItemRenderer;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.pagging.PagedListWrapper;
import com.pennanttech.pennapps.core.resource.Literal;

/**
 * This is the controller class for the /WEB-INF/pages/Enquiry/ExpenseMovementDialogCtrl.zul file.
 */
public class ExpenseMovementDialogCtrl extends GFCBaseCtrl<FinExpenseMovements> {
	private static final long						serialVersionUID	= 8602015982512929710L;
	private static final Logger						logger				= Logger.getLogger(ExpenseMovementDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the ZUL-file
	 * are getting autowired by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window								window_ExpenseMovementDialog;
	protected Borderlayout							borderLayout_GlFileDownloadList;
	protected Listbox								listBoxExpensiveMovements;
	protected Textbox								expenseTypeCode;
	protected Textbox								expenseTypeDesc;
	protected Paging								pagingExpenseMovementList;
	protected Grid									grid_Basicdetails;
	private PagedListWrapper<FinExpenseMovements>	movementsDetailPagedListWrapper;

	private FinExpenseDetails						finExpenseDetails;
	private List<FinExpenseMovements>				finExpenseMovements;

	int												listRows;
	private int										ccyFormatter		= 0; 

	/**
	 * default constructor.<br>
	 */
	public ExpenseMovementDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "";
	}

	// Component Events

	/**
	 * Before binding the data and calling the dialog window we check, if the ZUL-file is called with a parameter for a
	 * selected AccountingSet object in a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public void onCreate$window_ExpenseMovementDialog(ForwardEvent event) throws Exception {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_ExpenseMovementDialog);
		setMovementsDetailPagedListWrapper();

		if (arguments.containsKey("finExpenseDetails")) {
			this.finExpenseDetails = (FinExpenseDetails) arguments.get("finExpenseDetails");
		} else {
			this.finExpenseDetails = null;
		}

		if (arguments.containsKey("finExpenseMovements")) {
			this.finExpenseMovements = (List<FinExpenseMovements>) arguments.get("finExpenseMovements");
		} else {
			this.finExpenseMovements = null;
		}
		if (arguments.containsKey("ccyFormatter")) {
			this.ccyFormatter = (int) arguments.get("ccyFormatter");
		}
		
		getBorderLayoutHeight();
		int dialogHeight = grid_Basicdetails.getRows().getVisibleItemCount() * 20 + 170;
		int listboxHeight = borderLayoutHeight - dialogHeight;
		listBoxExpensiveMovements.setHeight(listboxHeight + "px");
		listRows = Math.round(listboxHeight / 24) - 1;
		pagingExpenseMovementList.setPageSize(listRows);

		doShowDialog();

		logger.debug("Leaving " + event.toString());
	}

	public void doShowDialog() throws Exception {
		logger.debug("Entering");

		doWriteBeanToComponents(this.finExpenseDetails);

		doFillExpenseMovementList(this.finExpenseMovements);

		try {
			this.window_ExpenseMovementDialog.setHeight("80%");
			this.window_ExpenseMovementDialog.setWidth("90%");
			setDialog(DialogType.MODAL);
		} catch (UiException e) {
			logger.error("Exception: ", e);
			this.window_ExpenseMovementDialog.onClose();
		}
		logger.debug("Leaving");
	}

	private void doFillExpenseMovementList(List<FinExpenseMovements> expenseTypeList) {
		logger.debug(Literal.ENTERING);

		if (expenseTypeList != null) {

			this.pagingExpenseMovementList.setDetailed(true);
			getMovementsDetailPagedListWrapper().initList(expenseTypeList, this.listBoxExpensiveMovements,
					this.pagingExpenseMovementList);
			this.listBoxExpensiveMovements.setItemRenderer(new ExpenseMovementListModelItemRenderer(this.ccyFormatter));
		}
		// Set the first page as the active page.
		if (pagingExpenseMovementList != null) {
			this.pagingExpenseMovementList.setActivePage(0);
		}

		logger.debug(Literal.LEAVING);
	}

	private void doWriteBeanToComponents(FinExpenseDetails finExpenseDetails) {
		logger.debug(Literal.ENTERING);
		this.expenseTypeCode.setValue(finExpenseDetails.getExpenseTypeCode());
		this.expenseTypeDesc.setValue(finExpenseDetails.getExpenseTypeDesc());

		logger.debug(Literal.LEAVING);
	}

	public void onClick$btnClose(Event event) {
		closeDialog();
	}

	@SuppressWarnings("unchecked")
	public void setMovementsDetailPagedListWrapper() {
		if (this.movementsDetailPagedListWrapper == null) {
			this.movementsDetailPagedListWrapper = (PagedListWrapper<FinExpenseMovements>) SpringUtil
					.getBean("pagedListWrapper");
		}
	}

	public PagedListWrapper<FinExpenseMovements> getMovementsDetailPagedListWrapper() {
		return movementsDetailPagedListWrapper;
	}

}
