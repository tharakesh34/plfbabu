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
 * FileName    		:  CorporateCustomerDetailListCtrl.java                                                   * 	  
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
package com.pennant.webui.customermasters.corporatecustomerdetail;

import java.util.Map;

import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Window;

import com.pennant.backend.model.customermasters.CorporateCustomerDetail;
import com.pennant.backend.service.customermasters.CorporateCustomerDetailService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.webui.customermasters.corporatecustomerdetail.model.CorporateCustomerDetailListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.framework.core.constants.SortOrder;

/**
 * This is the controller class for the /WEB-INF/pages/CustomerMasters/CorporateCustomerDetail
 * /CorporateCustomerDetailList.zul file.
 */
public class CorporateCustomerDetailListCtrl extends GFCBaseListCtrl<CorporateCustomerDetail> {
	private static final long serialVersionUID = 3149018047814219584L;
	private static final Logger logger = Logger.getLogger(CorporateCustomerDetailListCtrl.class);

	protected Window window_CorporateCustomerDetailList; 
	protected Borderlayout borderLayout_CorporateCustomerDetailList; 
	protected Paging pagingCorporateCustomerDetailList; 
	protected Listbox listBoxCorporateCustomerDetail; 

	protected Listheader listheader_CustId; 
	protected Listheader listheader_Name; 
	protected Listheader listheader_PhoneNumber; 
	protected Listheader listheader_EmailId; 

	protected Button button_CorporateCustomerDetailList_NewCorporateCustomerDetail; 
	protected Button button_CorporateCustomerDetailList_CorporateCustomerDetailSearchDialog; 

	//protected JdbcSearchObject<CorporateCustomerDetail> searchObj;
	private transient CorporateCustomerDetailService corporateCustomerDetailService;

	/**
	 * default constructor.<br>
	 */
	public CorporateCustomerDetailListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "CorporateCustomerDetail";
		super.pageRightName = "CorporateCustomerDetailList";
		super.tableName = "CustomerCorporateDetail_AView";
		super.queueTableName = "CustomerCorporateDetail_View";
	}
	
	@Override
	protected void doAddFilters() {
		super.doAddFilters();
		this.searchObject.addFilterNotEqual("lovDescCustRecordType", PennantConstants.RECORD_TYPE_NEW);
	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onCreate$window_CorporateCustomerDetailList(Event event) throws Exception {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_CorporateCustomerDetailList, borderLayout_CorporateCustomerDetailList,
				listBoxCorporateCustomerDetail, pagingCorporateCustomerDetailList);
		setItemRender(new CorporateCustomerDetailListModelItemRenderer());

		// Register buttons and fields.
		registerButton(button_CorporateCustomerDetailList_NewCorporateCustomerDetail, null, true);
		registerButton(button_CorporateCustomerDetailList_CorporateCustomerDetailSearchDialog,
				null);
		registerField("CustId", SortOrder.ASC);

		// Render the page and display the data.
		doRenderPage();
		search();

		logger.debug("Leaving");
	}

	/**
	 * Method for calling the CorporateCustomerDetail dialog
	 * 
	 * @param event
	 * @throws Exception
	 */
	/*public void onClick$button_CorporateCustomerDetailList_CorporateCustomerDetailSearchDialog(Event event)
			throws Exception {
		search();
	}*/

	/**
	 * The framework calls this event handler when user clicks the refresh
	 * button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$btnRefresh(Event event) {
		doReset();
		search();
	}

	/**
	 * This method is forwarded from the listBoxes item renderer. <br>
	 * see: com.pennant.webui.customermasters.corporatecustomerdetail.model.
	 * CorporateCustomerDetailListModelItemRenderer.java <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCorporateCustomerDetailItemDoubleClicked(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		// get the selected CorporateCustomerDetail object
		final Listitem item = this.listBoxCorporateCustomerDetail.getSelectedItem();

		if (item != null) {
			// CAST AND STORE THE SELECTED OBJECT
			long id = (long) item.getAttribute("data");
			CorporateCustomerDetail corporateCustomerDetail = corporateCustomerDetailService
					.getCorporateCustomerDetailById(id);

			if (corporateCustomerDetail == null) {
				MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
				return;
			}

			// Check whether the user has authority to change/view the record.
			String whereCond = " AND custid='" + corporateCustomerDetail.getId() + "' AND version="
					+ corporateCustomerDetail.getVersion() + " ";

			if (doCheckAuthority(corporateCustomerDetail, whereCond)) {
				// Set the latest work-flow id for the new maintenance request.
				if (isWorkFlowEnabled() && corporateCustomerDetail.getWorkflowId() == 0) {
					corporateCustomerDetail.setWorkflowId(getWorkFlowId());
				}
				doShowDialogPage(corporateCustomerDetail);
			} else {
				MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
			}
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Displays the dialog page with the required parameters as map.
	 * 
	 * @param academic
	 *            The entity that need to be passed to the dialog.
	 */
	private void doShowDialogPage(CorporateCustomerDetail corporateCustomerDetail) {
		logger.debug("Entering");

		Map<String, Object> arg = getDefaultArguments();
		arg.put("corporateCustomerDetail", corporateCustomerDetail);
		arg.put("corporateCustomerDetailListCtrl", this);
		arg.put("newRecord", corporateCustomerDetail.isNew());

		try {
			Executions.createComponents(
					"/WEB-INF/pages/CustomerMasters/CorporateCustomerDetail/CorporateCustomerDetailDialog.zul", null,
					arg);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}

		logger.debug("Leaving");
	}

	

	/**
	 * Method for calling the CorporateCustomerDetail dialog
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClick$button_CorporateCustomerDetailList_CorporateCustomerDetailSearchDialog(Event event)
										throws Exception {
		logger.debug("Entering" + event.toString());
		/*
		 * we can call our CorporateCustomerDetailDialog ZUL-file with parameters. So we can
		 * call them with a object of the selected CorporateCustomerDetail. For handed over
		 * these parameter only a Map is accepted. So we put the CorporateCustomerDetail object
		 * in a HashMap.
		 */
		Map<String, Object> map = getDefaultArguments();
		map.put("corporateCustomerDetailCtrl", this);
		map.put("searchObject", super.searchObject);

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents(
					"/WEB-INF/pages/CustomerMasters/CorporateCustomerDetail/CorporateCustomerDetailSearchDialog.zul",
							null,map);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving" + event.toString());
	}
	/**
	 * The framework calls this event handler when user clicks the print button
	 * to print the results.
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
	
	public void setCorporateCustomerDetailService(CorporateCustomerDetailService corporateCustomerDetailService) {
		this.corporateCustomerDetailService = corporateCustomerDetailService;
	}

}