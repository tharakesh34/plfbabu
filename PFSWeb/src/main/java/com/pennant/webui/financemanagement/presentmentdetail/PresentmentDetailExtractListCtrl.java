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
import com.pennant.backend.model.financemanagement.PresentmentDetail;
import com.pennant.backend.model.financemanagement.PresentmentDetailHeader;
import com.pennant.backend.service.financemanagement.PresentmentDetailService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.search.Filter;
import com.pennant.util.PennantAppUtil;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.MessageUtil;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;
import com.pennanttech.framework.web.components.SearchFilterControl;
import com.pennanttech.pff.core.App;
import com.pennanttech.pff.core.App.Database;
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

	protected Listbox sortOperator_MandateType;
	protected Listbox sortOperator_Product;

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

		setPageComponents(window_PresentmentExtractDetailList, borderLayout_PresentmentExtractDetailList, listBoxPresentmentExtractDetail, pagingPresentmentExtractDetailList);
		
		registerField("SCHDATE");
		registerField("DEFSCHDDATE");
		registerField("FinReference");
		registerField("SchSeq");
		registerField("MandateType", mandateType, SortOrder.NONE, sortOperator_MandateType, Operators.STRING);
		registerField("LoanType",loanType, SortOrder.NONE, sortOperator_Product, Operators.STRING);

		doSetFieldProperties();

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
		doSetValidations();
		search();
	}

	@Override
	public void search() {
		logger.debug("Entering");

		 List<PresentmentDetail> presentmentDetailList = null;
		 
		if (paging != null) {
			this.paging.setActivePage(0);
		}

		if (enqiryModule) {
			if (fromApproved != null && fromWorkFlow != null) {
				if (fromApproved.isChecked()) {
					this.searchObject.addTabelName(tableName);
				} else if (fromWorkFlow.isChecked()) {
					this.searchObject.addTabelName(enquiryTableName);
				} else {
					this.searchObject.addTabelName(tableName);
				}
			}
		}

		doAddFilters();
		
		String fromDate = PennantAppUtil.formateDate(this.fromdate.getValue(), PennantConstants.DBDateFormat);
		String toDate = PennantAppUtil.formateDate(this.toDate.getValue(), PennantConstants.DBDateFormat);

		StringBuilder whereClause = new StringBuilder();
		whereClause.append("(SCHDATE >= ").append("'").append(fromDate).append("'").append(" AND SCHDATE <= ").append("'").append(toDate).append("'").append(") OR (DEFSCHDDATE >= ");
		whereClause.append("'").append(fromDate).append("'").append(" AND DEFSCHDDATE <= ").append("'").append(toDate).append("'").append(")");

		this.searchObject.addWhereClause(whereClause.toString());

		
		presentmentDetailList  = getPagedListWrapper().getPagedListService().getBySearchObject(searchObject);
		
		if (presentmentDetailList == null || presentmentDetailList.isEmpty()) {
			MessageUtil.showError(" No records are available to extract, please change the search criteria.");
			return;
		}
		
		extractDetails(presentmentDetailList);
		
		logger.debug("Leaving");
	}

	@Override
	protected void doAddFilters() {
		logger.debug("Entering");

		for (SearchFilterControl searchControl : searchControls) {
			Filter filter = searchControl.getFilter();
			if (filter != null) {
				if (App.DATABASE == Database.ORACLE && "recordType".equals(filter.getProperty())
						&& Filter.OP_NOT_EQUAL == filter.getOperator()) {
					Filter[] filters = new Filter[2];
					filters[0] = Filter.isNull(filter.getProperty());
					filters[1] = filter;

					this.searchObject.addFilterOr(filters);
				} else {
					this.searchObject.addFilter(filter);
				}
			}
		}

		logger.debug("Leaving");
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

	private void extractDetails(List<PresentmentDetail> presentmentDetailList) {

		long reference = presentmentDetailService.getPresentmentDetailRef("SeqPresentmentDetailRef");
		String strReference = StringUtils.leftPad(String.valueOf(reference), 10, "0");
		strReference = "PRE".concat(strReference);

		PresentmentDetailHeader detailHeader = new PresentmentDetailHeader();
		detailHeader.setExtractId(reference);
		detailHeader.setExtractReference(strReference);
		detailHeader.setFromDate(this.fromdate.getValue());
		detailHeader.setToDate(this.toDate.getValue());
		detailHeader.setMandateType(this.mandateType.getValue());
		detailHeader.setMandateType(this.loanType.getValue());
		detailHeader.setLastMntBy(getUserWorkspace().getLoggedInUser().getLoginUsrID());
		detailHeader.setLastMntOn(new Timestamp(System.currentTimeMillis()));

		presentmentDetailService.savePresentmentHeaderDetails(detailHeader);

		for (PresentmentDetail item : presentmentDetailList) {
			item = presentmentDetailService.getPresentmentDetails(item.getFinReference(), item.getSchDate(), item.getSchSeq());
			item = doCalculations(item);
			// item.setDetailRef(detailRefStr);
			presentmentDetailService.savePresentmentDetails(item);
		}
	}

	private PresentmentDetail doCalculations(PresentmentDetail item) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * The framework calls this event handler when user clicks the refresh button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$btnRefresh(Event event) {
		doReset();
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