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
 * FileName    		:  DirectorDetailListCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  01-12-2011    														*
 *                                                                  						*
 * Modified Date    :  01-12-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 01-12-2011       Pennant	                 0.1                                            * 
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
package com.pennant.webui.customermasters.directordetail;

import java.util.Map;

import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.customermasters.DirectorDetail;
import com.pennant.backend.service.customermasters.DirectorDetailService;
import com.pennant.util.PennantAppUtil;
import com.pennant.webui.customermasters.directordetail.model.DirectorDetailListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;

/**
 * This is the controller class for the
 * /WEB-INF/pages/CustomerMasters/DirectorDetail/DirectorDetailList.zul file.
 */
public class DirectorDetailListCtrl extends GFCBaseListCtrl<DirectorDetail> {
	private static final long serialVersionUID = -5634641691791820344L;
	private static final Logger logger = Logger.getLogger(DirectorDetailListCtrl.class);

	protected Window window_DirectorDetailList; 
	protected Borderlayout borderLayout_DirectorDetailList; 
	protected Paging pagingDirectorDetailList; 
	protected Listbox listBoxDirectorDetail; 

	protected Listheader listheader_CustCIF; 
	protected Listheader listheader_FirstName; 
	protected Listheader listheader_ShortName; 
	protected Listheader listheader_CustGenderCode; 
	protected Listheader listheader_CustSalutationCode; 

	protected Textbox custCIF; 
	protected Listbox sortOperator_custCIF; 
	protected Textbox firstName; 
	protected Listbox sortOperator_firstName; 
	protected Textbox shortName; 
	protected Listbox sortOperator_shortName; 
	protected Textbox custGenderCode; 
	protected Listbox sortOperator_custGenderCode; 
	protected Textbox custSalutationCode; 
	protected Listbox sortOperator_custSalutationCode; 

	protected Label label_DirectorDetailSearch_RecordStatus; 
	protected Label label_DirectorDetailSearch_RecordType; 
	protected Label label_DirectorDetailSearchResult; 

	protected Button button_DirectorDetailList_NewDirectorDetail; 
	protected Button button_DirectorDetailList_DirectorDetailSearchDialog; 

	private transient DirectorDetailService directorDetailService;

	/**
	 * default constructor.<br>
	 */
	public DirectorDetailListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "DirectorDetail";
		super.pageRightName = "DirectorDetailList";
		super.tableName = "CustomerDirectorDetail_AView";
		super.queueTableName = "CustomerDirectorDetail_View";
	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onCreate$window_DirectorDetailList(Event event) {
		// Set the page level components.
		setPageComponents(window_DirectorDetailList, borderLayout_DirectorDetailList, listBoxDirectorDetail,
				pagingDirectorDetailList);
		setItemRender(new DirectorDetailListModelItemRenderer(PennantAppUtil.getCustomerDocumentTypesList(),
				PennantAppUtil.getCustomerCountryTypesList()));

		// Register buttons and fields.
		registerButton(button_DirectorDetailList_NewDirectorDetail, "button_DirectorDetailList_NewDirectorDetail", true);
		registerButton(button_DirectorDetailList_DirectorDetailSearchDialog);

		registerField("directorId");
		registerField("lovDescCustCIF", listheader_CustCIF, SortOrder.ASC, custCIF, sortOperator_custCIF,
				Operators.STRING);
		registerField("shortName", listheader_ShortName, SortOrder.NONE, shortName, sortOperator_shortName,
				Operators.STRING);
		registerField("firstName", listheader_FirstName, SortOrder.NONE, firstName, sortOperator_firstName,
				Operators.STRING);
		registerField("lastName");
		registerField("lovDescCustAddrCountryName");
		registerField("custAddrCountry");
		registerField("sharePerc");
		registerField("idType");
		registerField("lovDescCustDocCategoryName");
		registerField("idReference");
		registerField("nationality");
		registerField("lovDescNationalityName");

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
	public void onClick$button_DirectorDetailList_DirectorDetailSearchDialog(Event event) {
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
	public void onClick$button_DirectorDetailList_NewDirectorDetail(Event event){
		logger.debug("Entering" + event.toString());
		// create a new DirectorDetail object, We GET it from the backEnd.
		final DirectorDetail directorDetail = new DirectorDetail();
		directorDetail.setNewRecord(true);
		directorDetail.setWorkflowId(getWorkFlowId());

		// Display the dialog page.
		doShowDialogPage(directorDetail);
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * The framework calls this event handler when user opens a record to view it's details. Show the dialog page with
	 * the selected entity.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onDirectorDetailItemDoubleClicked(Event event) {
		logger.debug("Entering" + event.toString());

		// get the selected DirectorDetail object
		final Listitem item = this.listBoxDirectorDetail.getSelectedItem();

		if (item != null) {
			// CAST AND STORE THE SELECTED OBJECT
			long directorId = (long) item.getAttribute("directorId");
			long custID = (long) item.getAttribute("custID");
			final DirectorDetail directorDetail = directorDetailService.getDirectorDetailById(directorId, custID);

			if (directorDetail == null) {
				MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
				return;
			}

			// Check whether the user has authority to change/view the record.
			String whereCond = " AND DirectorId='" + directorDetail.getDirectorId() + "' AND version="
					+ directorDetail.getVersion() + " ";

			if (doCheckAuthority(directorDetail, whereCond)) {
				// Set the latest work-flow id for the new maintenance request.
				if (isWorkFlowEnabled() && directorDetail.getWorkflowId() == 0) {
					directorDetail.setWorkflowId(getWorkFlowId());
				}
				doShowDialogPage(directorDetail);
			} else {
				MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
			}

		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Displays the dialog page with the required parameters as map.
	 * 
	 * @param directorDetail
	 *            The entity that need to be passed to the dialog.
	 */
	private void doShowDialogPage(DirectorDetail directorDetail) {
		logger.debug("Entering");

		Map<String, Object> arg = getDefaultArguments();
		arg.put("directorDetail", directorDetail);
		arg.put("directorDetailListCtrl", this);
		arg.put("newRecord", directorDetail.isNew());

		try {
			Executions.createComponents("/WEB-INF/pages/CustomerMasters/DirectorDetail/DirectorDetailDialog.zul", null,
					arg);
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

	public void setDirectorDetailService(DirectorDetailService directorDetailService) {
		this.directorDetailService = directorDetailService;
	}
}