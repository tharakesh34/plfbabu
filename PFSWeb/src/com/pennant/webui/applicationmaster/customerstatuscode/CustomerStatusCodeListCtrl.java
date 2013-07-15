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
 * FileName    		:  CustomerStatusCodeListCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  03-05-2011    														*
 *                                                                  						*
 * Modified Date    :  03-05-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 03-05-2011       Pennant	                 0.1                                            * 
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

package com.pennant.webui.applicationmaster.customerstatuscode;

import java.io.Serializable;
import java.util.HashMap;

import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.FieldComparator;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Window;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.ModuleMapping;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.applicationmaster.CustomerStatusCode;
import com.pennant.backend.service.applicationmaster.CustomerStatusCodeService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.search.Filter;
import com.pennant.webui.applicationmaster.customerstatuscode.model.CustomerStatusCodeListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.PTListReportUtils;
import com.pennant.webui.util.PTMessageUtils;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * /WEB-INF/pages/ApplicationMaster/CustomerStatusCode/CustomerStatusCodeList.zul file.<br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * 
 */
public class CustomerStatusCodeListCtrl extends GFCBaseListCtrl<CustomerStatusCode>	implements Serializable {

	private static final long serialVersionUID = -3727071843922740401L;
	private final static Logger logger = Logger.getLogger(CustomerStatusCodeListCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by
	 * our 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window 		window_CustomerStatusCodeList; 			// autoWired
	protected Borderlayout 	borderLayout_CustomerStatusCodeList; 	// autoWired
	protected Paging 		pagingCustomerStatusCodeList; 			// autoWired
	protected Listbox 		listBoxCustomerStatusCode; 				// autoWired

	// List headers
	protected Listheader listheader_CustStsCode; 		// autoWired
	protected Listheader listheader_CustStsDescription; // autoWired
	protected Listheader listheader_CustStsIsActive; 	// autoWired
	protected Listheader listheader_RecordStatus; 		// autoWired
	protected Listheader listheader_RecordType;

	// checkRights
	protected Button btnHelp; 														// autoWired
	protected Button button_CustomerStatusCodeList_NewCustomerStatusCode; 			// autoWired
	protected Button button_CustomerStatusCodeList_CustomerStatusCodeSearchDialog; 	// autoWired
	protected Button button_CustomerStatusCodeList_PrintList; 						// autoWired

	// NEEDED for the ReUse in the SearchWindow
	protected JdbcSearchObject<CustomerStatusCode> searchObj;

	private transient CustomerStatusCodeService customerStatusCodeService;
	private transient WorkFlowDetails workFlowDetails = null;

	/**
	 * default constructor.<br>
	 */
	public CustomerStatusCodeListCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the List window we check, if the
	 * ZUL-file is called with a parameter for a selected CustomerStatusCode object
	 * in a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_CustomerStatusCodeList(Event event)	throws Exception {
		logger.debug("Entering" + event.toString());

		ModuleMapping moduleMapping = PennantJavaUtil.getModuleMap("CustomerStatusCode");
		boolean wfAvailable = true;

		if (moduleMapping.getWorkflowType() != null) {
			workFlowDetails = WorkFlowUtil.getWorkFlowDetails("CustomerStatusCode");

			if (workFlowDetails == null) {
				setWorkFlowEnabled(false);
			} else {
				setWorkFlowEnabled(true);
				setFirstTask(getUserWorkspace().isRoleContains(workFlowDetails.getFirstTaskOwner()));
				setWorkFlowId(workFlowDetails.getId());
			}
		} else {
			wfAvailable = false;
		}

		/* set components visible dependent of the users rights */
		doCheckRights();

		/**
		 * Calculate how many rows have been place in the list box. Get the
		 * currentDesktopHeight from a hidden IntBox from the index.zul that
		 * are filled by onClientInfo() in the indexCtroller
		 */
		this.borderLayout_CustomerStatusCodeList.setHeight(getBorderLayoutHeight());

		// set the paging parameters
		this.pagingCustomerStatusCodeList.setPageSize(getListRows());
		this.pagingCustomerStatusCodeList.setDetailed(true);

		this.listheader_CustStsCode.setSortAscending(new FieldComparator("custStsCode", true));
		this.listheader_CustStsCode.setSortDescending(new FieldComparator("custStsCode", false));
		this.listheader_CustStsDescription.setSortAscending(new FieldComparator("custStsDescription", true));
		this.listheader_CustStsDescription.setSortDescending(new FieldComparator("custStsDescription", false));
		this.listheader_CustStsIsActive.setSortAscending(new FieldComparator("custStsIsActive", true));
		this.listheader_CustStsIsActive.setSortDescending(new FieldComparator("custStsIsActive", false));

		if (isWorkFlowEnabled()) {
			this.listheader_RecordStatus.setSortAscending(new FieldComparator("recordStatus", true));
			this.listheader_RecordStatus.setSortDescending(new FieldComparator("recordStatus", false));
			this.listheader_RecordType.setSortAscending(new FieldComparator("recordType", true));
			this.listheader_RecordType.setSortDescending(new FieldComparator("recordType", false));
		} else {
			this.listheader_RecordStatus.setVisible(false);
			this.listheader_RecordType.setVisible(false);
		}

		// ++ create the searchObject and initialize sorting ++//
		this.searchObj = new JdbcSearchObject<CustomerStatusCode>(CustomerStatusCode.class, getListRows());
		this.searchObj.addSort("CustStsCode", false);
		this.searchObj.addFilter(new Filter("CustStsCode", PennantConstants.NONE, Filter.OP_NOT_EQUAL));

		// Work flow
		if (isWorkFlowEnabled()) {
			this.searchObj.addTabelName("BMTCustStatusCodes_View");
			if (isFirstTask()) {
				button_CustomerStatusCodeList_NewCustomerStatusCode.setVisible(true);
			} else {
				button_CustomerStatusCodeList_NewCustomerStatusCode.setVisible(false);
			}

			this.searchObj.addFilterIn("nextRoleCode", getUserWorkspace().getUserRoles(), isFirstTask());
		} else {
			this.searchObj.addTabelName("BMTCustStatusCodes_AView");
		}

		setSearchObj(this.searchObj);
		if (!isWorkFlowEnabled() && wfAvailable) {
			this.button_CustomerStatusCodeList_NewCustomerStatusCode.setVisible(false);
			this.button_CustomerStatusCodeList_CustomerStatusCodeSearchDialog.setVisible(false);
			this.button_CustomerStatusCodeList_PrintList.setVisible(false);
			PTMessageUtils.showErrorMessage(PennantJavaUtil.getLabel("WORKFLOW CONFIG NOT FOUND"));
		} else {
			// Set the ListModel for the articles.
			getPagedListWrapper().init(this.searchObj,this.listBoxCustomerStatusCode, this.pagingCustomerStatusCodeList);
			// set the itemRenderer
			this.listBoxCustomerStatusCode.setItemRenderer(new CustomerStatusCodeListModelItemRenderer());
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * SetVisible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug("Entering");

		getUserWorkspace().alocateAuthorities("CustomerStatusCodeList");
		this.button_CustomerStatusCodeList_NewCustomerStatusCode.setVisible(getUserWorkspace()
				.isAllowed("button_CustomerStatusCodeList_NewCustomerStatusCode"));
		this.button_CustomerStatusCodeList_CustomerStatusCodeSearchDialog.setVisible(getUserWorkspace()
				.isAllowed("button_CustomerStatusCodeList_CustomerStatusCodeFindDialog"));
		this.button_CustomerStatusCodeList_PrintList.setVisible(getUserWorkspace()
				.isAllowed("button_CustomerStatusCodeList_PrintList"));
		logger.debug("Leaving");
	}

	/**
	 * This method is forwarded from the list boxes item renderer. <br>
	 * see: com.pennant.webui.bmtmasters.custstatuscode.model.
	 * CustomerStatusCodeListModelItemRenderer.java <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCustomerStatusCodeItemDoubleClicked(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		// get the selected CustomerStatusCode object
		final Listitem item = this.listBoxCustomerStatusCode.getSelectedItem();

		if (item != null) {
			// CAST AND STORE THE SELECTED OBJECT
			final CustomerStatusCode aCustomerStatusCode = (CustomerStatusCode) item.getAttribute("data");
			final CustomerStatusCode customerStatusCode = getCustomerStatusCodeService().getCustomerStatusCodeById(aCustomerStatusCode.getId());

			if (customerStatusCode == null) {

				String[] valueParm = new String[2];
				String[] errorParm = new String[2];

				valueParm[0] = aCustomerStatusCode.getCustStsCode();
				errorParm[0] = PennantJavaUtil.getLabel("label_CustStsCode") + ":" + aCustomerStatusCode.getCustStsCode();

				ErrorDetails errorDetails = ErrorUtil.getErrorDetail(
						new ErrorDetails(PennantConstants.KEY_FIELD, "41005",errorParm, valueParm), getUserWorkspace().getUserLanguage());
				PTMessageUtils.showErrorMessage(errorDetails.getErrorMessage());
			} else {
				String whereCond = " AND CustStsCode='"	+ customerStatusCode.getCustStsCode() 
						+ "' AND version="	+ customerStatusCode.getVersion() + " ";

				if (isWorkFlowEnabled()) {
					boolean userAcces = validateUserAccess(workFlowDetails.getId(), getUserWorkspace().getLoginUserDetails().getLoginUsrID(),
							"CustomerStatusCode", whereCond, customerStatusCode.getTaskId(), customerStatusCode.getNextTaskId());
					if (userAcces) {
						showDetailView(customerStatusCode);
					} else {
						PTMessageUtils.showErrorMessage(Labels.getLabel("RECORD_NOTALLOWED"));
					}
				} else {
					showDetailView(customerStatusCode);
				}
			}
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Call the CustomerStatusCode dialog with a new empty entry. <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClick$button_CustomerStatusCodeList_NewCustomerStatusCode(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		// create a new CustomerStatusCode object, We GET it from the back end.
		final CustomerStatusCode aCustomerStatusCode = getCustomerStatusCodeService().getNewCustomerStatusCode();
		showDetailView(aCustomerStatusCode);
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Opens the detail view. <br>
	 * Over handed some parameters in a map if needed. <br>
	 * 
	 * @param CustomerStatusCode
	 *            (aCustomerStatusCode)
	 * @throws Exception
	 */
	private void showDetailView(CustomerStatusCode aCustomerStatusCode)	throws Exception {
		logger.debug("Entering");

		/*
		 * We can call our Dialog ZUL-file with parameters. So we can call them
		 * with a object of the selected item. For handed over these parameter
		 * only a Map is accepted. So we put the object in a HashMap.
		 */
		if (aCustomerStatusCode.getWorkflowId() == 0 && isWorkFlowEnabled()) {
			aCustomerStatusCode.setWorkflowId(workFlowDetails.getWorkFlowId());
		}

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("customerStatusCode", aCustomerStatusCode);
		/*
		 * we can additionally handed over the listBox or the controller self,
		 * so we have in the dialog access to the list box List model. This is
		 * fine for synchronizing the data in the CustomerStatusCodeListbox from the
		 * dialog when we do a delete, edit or insert a CustomerStatusCode.
		 */
		map.put("customerStatusCodeListCtrl", this);

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents(
					"/WEB-INF/pages/ApplicationMaster/CustomerStatusCode/CustomerStatusCodeDialog.zul", null, map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / " + e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving");
	}

	/**
	 * when the "help" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnHelp(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		PTMessageUtils.showHelpWindow(event, window_CustomerStatusCodeList);
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "refresh" button is clicked. <br>
	 * <br>
	 * Refreshes the view by calling the onCreate event manually.
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnRefresh(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		this.pagingCustomerStatusCodeList.setActivePage(0);
		Events.postEvent("onCreate", this.window_CustomerStatusCodeList, event);
		this.window_CustomerStatusCodeList.invalidate();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * call the CustomerStatusCode dialog
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClick$button_CustomerStatusCodeList_CustomerStatusCodeSearchDialog(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		/*
		 * we can call our CustomerStatusCodeDialog ZUL-file with parameters. So we
		 * can call them with a object of the selected CustomerStatusCode. For
		 * handed over these parameter only a Map is accepted. So we put the
		 * CustomerStatusCode object in a HashMap.
		 */
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("customerStatusCodeCtrl", this);
		map.put("searchObject", this.searchObj);

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents(
				"/WEB-INF/pages/ApplicationMaster/CustomerStatusCode/CustomerStatusCodeSearchDialog.zul", null, map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / " + e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * When the customerStatusCode print button is clicked.
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	@SuppressWarnings("unused")
	public void onClick$button_CustomerStatusCodeList_PrintList(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		PTListReportUtils reportUtils = new PTListReportUtils("CustomerStatusCode", getSearchObj(),this.pagingCustomerStatusCodeList.getTotalSize()+1);
		logger.debug("Leaving" + event.toString());
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public void setCustomerStatusCodeService(CustomerStatusCodeService customerStatusCodeService) {
		this.customerStatusCodeService = customerStatusCodeService;
	}
	public CustomerStatusCodeService getCustomerStatusCodeService() {
		return this.customerStatusCodeService;
	}

	public JdbcSearchObject<CustomerStatusCode> getSearchObj() {
		return this.searchObj;
	}
	public void setSearchObj(JdbcSearchObject<CustomerStatusCode> searchObj) {
		this.searchObj = searchObj;
	}

}