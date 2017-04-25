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
 * FileName    		:  PresentmentDetailListCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  22-04-2017    														*
 *                                                                  						*
 * Modified Date    :  22-04-2017    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 22-04-2017       PENNANT	                 0.1                                            * 
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

package com.pennant.webui.financemanagement.presentmentdetail;

import java.sql.Timestamp;

import org.apache.log4j.Logger;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Window;

import com.pennant.ExtendedCombobox;
import com.pennant.app.constants.LengthConstants;
import com.pennant.backend.model.financemanagement.PresentmentDetail;
import com.pennant.backend.model.financemanagement.PresentmentDetailHeader;
import com.pennant.backend.service.financemanagement.PresentmentDetailService;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.MessageUtil;
import com.pennanttech.pff.core.Literal;

/**
 * This is the controller class for the
 * /WEB-INF/pages/com.pennant.financemanagement/PresentmentDetail/PresentmentDetailList.zul file.
 * 
 */
public class PresentmentDetailExtractListCtrl extends GFCBaseListCtrl<PresentmentDetail> {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(PresentmentDetailExtractListCtrl.class);

	protected Window window_PresentmentExtractDetailList;
	protected Borderlayout borderLayout_PresentmentExtractDetailList;
	protected Paging pagingPresentmentExtractDetailList;
	protected Listbox listBoxPresentmentExtractDetail;

	protected Button button_PresentmentDetailList_Extract;

	protected Combobox mandateType;
	protected ExtendedCombobox loanType;
	protected Datebox fromdate;
	protected Datebox toDate;

	private transient PresentmentDetailService presentmentDetailService;

	/**
	 * default constructor.<br>
	 */
	public PresentmentDetailExtractListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "PresentmentDetail";
		super.pageRightName = "PresentmentDetailList";
		super.tableName = "PRESENTMENTDETAIL_EXTRACT_VIEW";
		super.queueTableName = "PRESENTMENTDETAIL_EXTRACT_VIEW";
	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onCreate$window_PresentmentExtractDetailList(Event event) {
		logger.debug(Literal.ENTERING);

		setPageComponents(window_PresentmentExtractDetailList, borderLayout_PresentmentExtractDetailList,
				listBoxPresentmentExtractDetail, pagingPresentmentExtractDetailList);

		// Render the page and display the data.
		doRenderPage();
		doSetFieldProperties();
		
		registerField("FromDt");;

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set the component level properties.
	 */
	private void doSetFieldProperties() {
		logger.debug(Literal.ENTERING);

		fillComboBox(this.mandateType, "", PennantStaticListUtil.getMandateTypeList(), "");

		this.loanType.setMaxlength(LengthConstants.LEN_MASTER_CODE);
		this.loanType.setModuleName("FinanceType");
		this.loanType.setValueColumn("FinType");
		this.loanType.setDescColumn("FinTypeDesc");
		this.loanType.setValidateColumns(new String[] { "FinType" });

		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the search button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$button_PresentmentDetailList_Extract(Event event) {
		String errorMsg = null;
		doSetValidations();
		try {
			errorMsg = extractDetails();
		} catch (Exception e) {
			MessageUtil.showError(e.getMessage());
		}
		if (errorMsg != null) {
			MessageUtil.showError(errorMsg);
			return;
		}
	}

	private void doSetValidations() {
		Clients.clearWrongValue(this.fromdate);
		Clients.clearWrongValue(this.toDate);
		this.fromdate.setErrorMessage("");
		this.toDate.setErrorMessage("");

		if (this.fromdate.getValue() == null) {
			throw new WrongValueException(this.fromdate, "From Date should be mandatory. ");
		}

		if (this.toDate.getValue() == null) {
			throw new WrongValueException(this.toDate, "To Date should be mandatory. ");
		}
	}

	private String extractDetails() throws Exception {

		PresentmentDetailHeader detailHeader = new PresentmentDetailHeader();
		detailHeader.setFromDate(this.fromdate.getValue());
		detailHeader.setToDate(this.toDate.getValue());
		detailHeader.setMandateType(this.mandateType.getValue());
		detailHeader.setMandateType(this.loanType.getValue());
		detailHeader.setLastMntBy(getUserWorkspace().getLoggedInUser().getLoginUsrID());
		detailHeader.setLastMntOn(new Timestamp(System.currentTimeMillis()));

		return presentmentDetailService.processPresentmentDetails(detailHeader);
	}

	/**
	 * The framework calls this event handler when user clicks the refresh button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$btnRefresh(Event event) {
		doResetInitValues();
	}

	private void doResetInitValues() {
		logger.debug(Literal.ENTERING);

		fillComboBox(this.mandateType, "", PennantStaticListUtil.getMandateTypeList(), "");

		this.loanType.setValue("");
		this.loanType.setDescription("");

		this.fromdate.setValue(null);
		this.toDate.setValue(null);

		if (listBoxPresentmentExtractDetail.getItems() != null) {
			this.listBoxPresentmentExtractDetail.getItems().clear();

		}

		logger.debug(Literal.LEAVING);

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

	public void setPresentmentDetailService(PresentmentDetailService presentmentDetailService) {
		this.presentmentDetailService = presentmentDetailService;
	}

}