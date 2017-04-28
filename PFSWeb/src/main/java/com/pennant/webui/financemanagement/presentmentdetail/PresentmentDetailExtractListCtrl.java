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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
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
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.financemanagement.PresentmentDetail;
import com.pennant.backend.model.financemanagement.PresentmentHeader;
import com.pennant.backend.service.financemanagement.PresentmentDetailService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.component.Uppercasebox;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.MessageUtil;
import com.pennant.webui.util.searchdialogs.MultiSelectionSearchListBox;
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
	protected Uppercasebox loanType;
	protected Button btnloanType;
	protected Datebox fromdate;
	protected Datebox toDate;
	protected ExtendedCombobox branches;

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

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set the component level properties.
	 */
	private void doSetFieldProperties() {
		logger.debug(Literal.ENTERING);

		fillComboBox(this.mandateType, "", PennantStaticListUtil.getMandateTypeList(), "");
		this.fromdate.setFormat(PennantConstants.dateFormat);
		this.toDate.setFormat(PennantConstants.dateFormat);

		this.branches.setMaxlength(LengthConstants.LEN_MASTER_CODE);
		this.branches.setModuleName("Branch");
		this.branches.setValueColumn("BranchCode");
		this.branches.setDescColumn("BranchDesc");
		this.branches.setValidateColumns(new String[] { "BranchCode" });

		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the search button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$button_PresentmentDetailList_Extract(Event event) {
		logger.debug(Literal.ENTERING);

		String errorMsg = null;
		doSetValidations();
		try {
			errorMsg = extractDetails();
			MessageUtil.showError(errorMsg);
			return;
		} catch (Exception e) {
			MessageUtil.showError(e.getMessage());
		}

		logger.debug(Literal.LEAVING);
	}

	private void doSetValidations() {
		logger.debug(Literal.ENTERING);

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

		if (this.toDate != null && DateUtility.compare(this.toDate.getValue(), this.fromdate.getValue()) < 0) {
			throw new WrongValueException(this.toDate, "To Date should be greater than From Date");
		}

		int diffentDays = SysParamUtil.getValueAsInt("PRESENTMENT_DAYS_DEF");
		if (DateUtility.getDaysBetween(this.fromdate.getValue(), this.toDate.getValue()) > diffentDays) {
			throw new WrongValueException(this.toDate,
					" From Date and To Date difference should be less than are equal to " + diffentDays);
		}
		logger.debug(Literal.LEAVING);
	}

	private String extractDetails() throws Exception {
		logger.debug(Literal.ENTERING);

		PresentmentHeader detailHeader = new PresentmentHeader();

		detailHeader.setFromDate(this.fromdate.getValue());
		detailHeader.setToDate(this.toDate.getValue());
		if (!"#".equals(this.mandateType.getSelectedItem().getValue().toString())) {
			detailHeader.setMandateType(this.mandateType.getSelectedItem().getValue().toString());
		}
		detailHeader.setLoanType(this.loanType.getValue());
		detailHeader.setFinBranch(this.branches.getValue());
		detailHeader.setLastMntBy(getUserWorkspace().getLoggedInUser().getLoginUsrID());
		detailHeader.setLastMntOn(new Timestamp(System.currentTimeMillis()));

		logger.debug(Literal.LEAVING);

		return presentmentDetailService.savePresentmentDetails(detailHeader);
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
		this.fromdate.setValue(null);
		this.toDate.setValue(null);
		this.branches.setValue("");
		this.branches.setDescription("");

		logger.debug(Literal.LEAVING);
	}

	public void onClick$btnloanType(Event event) {
		logger.debug(Literal.ENTERING);

		Object dataObject = MultiSelectionSearchListBox.show(this.window_PresentmentExtractDetailList, "FinanceType",
				this.loanType.getValue(), null);
		if (dataObject instanceof String) {
			this.loanType.setValue(dataObject.toString());
		} else {
			@SuppressWarnings("unchecked")
			HashMap<String, Object> details = (HashMap<String, Object>) dataObject;
			if (details != null) {
				String tempflagcode = "";
				List<String> flagKeys = new ArrayList<>(details.keySet());
				for (int i = 0; i < flagKeys.size(); i++) {
					if (StringUtils.isEmpty(flagKeys.get(i))) {
						continue;
					}
					if (i == 0) {
						tempflagcode = flagKeys.get(i);
					} else {
						tempflagcode = tempflagcode + "," + flagKeys.get(i);
					}
				}
				this.loanType.setValue(tempflagcode);
			}
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