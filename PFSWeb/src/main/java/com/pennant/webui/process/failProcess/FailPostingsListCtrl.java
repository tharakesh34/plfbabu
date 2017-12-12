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
 * FileName    		:  LegalExpensesListCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  19-04-2016    														*
 *                                                                  						*
 * Modified Date    :  19-04-2016    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 19-04-2016       Pennant	                 0.1                                            * 
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
package com.pennant.webui.process.failProcess;

import org.apache.log4j.Logger;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.Interface.service.EODFailPostingService;
import com.pennant.backend.model.finance.DDAFTransactionLog;
import com.pennant.backend.model.finance.DDAProcessData;
import com.pennant.backend.service.dda.DDAControllerService;
import com.pennant.backend.service.dda.DDAProcessService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.constants.InterfaceConstants;
import com.pennant.mq.util.PFFXmlUtil;
import com.pennant.search.Filter;
import com.pennant.webui.process.failProcess.model.FailPostingsListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;
import com.pennanttech.pennapps.core.InterfaceException;

/**
 * This is the controller class for the /WEB-INF/pages/Expenses/LegalExpenses/LegalExpensesList.zul file.
 */
public class FailPostingsListCtrl extends GFCBaseListCtrl<DDAFTransactionLog> {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(FailPostingsListCtrl.class);

	protected Window window_DDADDAFailPostingsList;
	protected Borderlayout borderLayout_DDAFailPostingsList;
	protected Paging pagingDDAFailPostingsList;
	protected Listbox listBoxFailPostings;

	protected Listheader listheader_FinReference;
	protected Listheader listheader_DateProcessed;
	protected Listheader listheader_ErrorCode;
	protected Listheader listheader_ErrorDesc;
	protected Listheader listheader_NoofTries;

	protected Button button_DDAFailPostingsList_FailPostingsProcess;
	protected Button button_DDAFailPostingsList_FailPostingsSearch;

	protected JdbcSearchObject<DDAFTransactionLog> searchObj;

	protected Textbox finReference;
	protected Listbox sortOperator_FinReference;

	protected Textbox moduleType;

	private DDAControllerService ddaControllerService;
	private DDAProcessService ddaProcessService;
	private EODFailPostingService eodFailPostingService;

	/**
	 * default constructor.<br>
	 */
	public FailPostingsListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "DDAFTransactionLog";
		super.pageRightName = "LegalExpensesList";
		super.tableName = "DDAFTransactionLog";
		super.queueTableName = "DDAFTransactionLog";
		super.enquiryTableName = "FinLegalExpenses_TView";

	}

	@Override
	protected void doAddFilters() {
		super.doAddFilters();
		searchObject.addFilter(new Filter("errorCode", PFFXmlUtil.SUCCESS, Filter.OP_NOT_EQUAL));

	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onCreate$window_DDADDAFailPostingsList(Event event) throws Exception {
		// Set the page level components.
		setPageComponents(window_DDADDAFailPostingsList, borderLayout_DDAFailPostingsList, listBoxFailPostings,
				pagingDDAFailPostingsList);
		setItemRender(new FailPostingsListModelItemRenderer());

		// Register buttons and fields.
		registerButton(button_DDAFailPostingsList_FailPostingsSearch);

		registerField("finRefence", listheader_FinReference, SortOrder.ASC, finReference, sortOperator_FinReference,
				Operators.STRING);

		registerField("errorCode", listheader_ErrorCode);
		registerField("errorDesc", listheader_ErrorDesc);
		registerField("noofTries", listheader_NoofTries);
		registerField("error");
		registerField("valueDate");

		// Render the page and display the data.
		doRenderPage();
		search();
		// check box to marking to select and proceed
		listBoxFailPostings.setCheckmark(true);
		listBoxFailPostings.setMultiple(true);

	}

	/**
	 * The framework calls this event handler when user clicks the search button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$button_DDAFailPostingsList_FailPostingsSearch(Event event) {
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

	public void onDDAItemChecked(Event event) throws Exception {
		logger.debug("Entering");
		if (this.listBoxFailPostings.getSelectedCount() > 0) {
			this.button_DDAFailPostingsList_FailPostingsProcess.setVisible(true);
		} else {
			this.button_DDAFailPostingsList_FailPostingsProcess.setVisible(false);
		}

		logger.debug("Leaving");
	}

	public void onClick$button_DDAFailPostingsList_FailPostingsProcess(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		DDAProcessData DDAProcessData = null;
		int count = listBoxFailPostings.getSelectedItems().size();
		int successCount = 0;
		int failCount = 0;

		if (this.listBoxFailPostings.getSelectedCount() > 0) {

			for (Listitem itemSelected : listBoxFailPostings.getSelectedItems()) {

				if (itemSelected != null) {
					// CAST AND STORE THE SELECTED OBJECT
					final DDAFTransactionLog ddaFTransactionLog = (DDAFTransactionLog) itemSelected
							.getAttribute("data");
					try {
						getDdaControllerService().cancelDDARegistration(ddaFTransactionLog.getFinRefence());

						DDAProcessData = getDdaProcessService().getDDADetailsByReference(
								ddaFTransactionLog.getFinRefence(), PennantConstants.REQ_TYPE_CAN);

						if (DDAProcessData != null
								&& DDAProcessData.getReturnCode().equals(InterfaceConstants.SUCCESS_CODE)) {

							ddaFTransactionLog.setErrorCode(DDAProcessData.getReturnCode());
							ddaFTransactionLog.setErrorDesc(DDAProcessData.getReturnText());
							ddaFTransactionLog.setNoofTries(ddaFTransactionLog.getNoofTries() + 1);
							getEodFailPostingService().updateFailPostings(ddaFTransactionLog);
							successCount++;

						}
					} catch (InterfaceException e) {

						ddaFTransactionLog.setErrorCode(e.getErrorCode());
						ddaFTransactionLog.setErrorDesc(e.getErrorMessage());
						ddaFTransactionLog.setNoofTries(ddaFTransactionLog.getNoofTries() + 1);
						failCount++;
						getEodFailPostingService().updateFailPostings(ddaFTransactionLog);
					}
					MessageUtil.showMessage("Total Processed  :" + count + "," + "Successfully posted :" + successCount
							+ "," + "failed :" + failCount);
					search();
				}
			}
		}
		logger.debug("Leaving" + event.toString());
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

	public DDAControllerService getDdaControllerService() {
		return ddaControllerService;
	}

	public void setDdaControllerService(DDAControllerService ddaControllerService) {
		this.ddaControllerService = ddaControllerService;
	}

	public DDAProcessService getDdaProcessService() {
		return ddaProcessService;
	}

	public void setDdaProcessService(DDAProcessService ddaProcessService) {
		this.ddaProcessService = ddaProcessService;
	}

	public EODFailPostingService getEodFailPostingService() {
		return eodFailPostingService;
	}

	public void setEodFailPostingService(EODFailPostingService eodFailPostingService) {
		this.eodFailPostingService = eodFailPostingService;
	}

}