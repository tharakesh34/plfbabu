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
 * FileName    		:  CustomerCategoryListCtrl.java                                                   * 	  
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

package com.pennant.webui.applicationmaster.customercategory;

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
import org.zkoss.zul.Panel;
import org.zkoss.zul.Window;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.ModuleMapping;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.applicationmaster.CustomerCategory;
import com.pennant.backend.service.applicationmaster.CustomerCategoryService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.search.Filter;
import com.pennant.webui.applicationmaster.customercategory.model.CustomerCategoryListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.PTListReportUtils;
import com.pennant.webui.util.PTMessageUtils;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * /WEB-INF/pages/ApplicationMaster/CustomerCategory/CustomerCategoryList.zul file.<br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * 
 */
public class CustomerCategoryListCtrl extends GFCBaseListCtrl<CustomerCategory>	implements Serializable {

	private static final long serialVersionUID = -7662342461801640367L;
	private final static Logger logger = Logger.getLogger(CustomerCategoryListCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by
	 * our 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window 		window_CustomerCategoryList; 		// autoWired
	protected Panel 		panel_CustomerCategoryList; 		// autoWired
	protected Borderlayout 	borderLayout_CustomerCategoryList; 	// autoWired
	protected Paging		pagingCustomerCategoryList; 		// autoWired
	protected Listbox 		listBoxCustomerCategory; 			// autoWired

	// List headers
	protected Listheader listheader_CustCtgCode; 		// autoWired
	protected Listheader listheader_CustCtgDesc; 		// autoWired
	protected Listheader listheader_CustCtgType; 		// autoWired
	protected Listheader listheader_CustCtgIsActive; 	// autoWired
	protected Listheader listheader_RecordStatus; 		// autoWired
	protected Listheader listheader_RecordType;

	// checkRights
	protected Button btnHelp; 													// autoWired
	protected Button button_CustomerCategoryList_NewCustomerCategory; 			// autoWired
	protected Button button_CustomerCategoryList_CustomerCategorySearchDialog; 	// autoWired
	protected Button button_CustomerCategoryList_PrintList; 					// autoWired

	// NEEDED for the ReUse in the SearchWindow
	protected JdbcSearchObject<CustomerCategory> searchObj;

	private transient CustomerCategoryService customerCategoryService;
	private transient WorkFlowDetails workFlowDetails = null;

	/**
	 * default constructor.<br>
	 */
	public CustomerCategoryListCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the List window we check, if the
	 * ZUL-file is called with a parameter for a selected CustomerCategory
	 * object in a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_CustomerCategoryList(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		ModuleMapping moduleMapping = PennantJavaUtil.getModuleMap("CustomerCategory");
		boolean wfAvailable = true;

		if (moduleMapping.getWorkflowType() != null) {
			workFlowDetails = WorkFlowUtil.getWorkFlowDetails("CustomerCategory");

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
		 * currentDesktopHeight from a hidden IntBox from the index.zul that are
		 * filled by onClientInfo() in the indexCtroller
		 */
		this.borderLayout_CustomerCategoryList
		.setHeight(getBorderLayoutHeight());

		// set the paging parameters
		this.pagingCustomerCategoryList.setPageSize(getListRows());
		this.pagingCustomerCategoryList.setDetailed(true);

		this.listheader_CustCtgCode.setSortAscending(new FieldComparator("custCtgCode", true));
		this.listheader_CustCtgCode.setSortDescending(new FieldComparator("custCtgCode", false));
		this.listheader_CustCtgDesc.setSortAscending(new FieldComparator("custCtgDesc", true));
		this.listheader_CustCtgDesc.setSortDescending(new FieldComparator("custCtgDesc", false));
		this.listheader_CustCtgType.setSortAscending(new FieldComparator("custCtgType", true));
		this.listheader_CustCtgType.setSortDescending(new FieldComparator("custCtgType", false));
		this.listheader_CustCtgIsActive.setSortAscending(new FieldComparator("custCtgIsActive", true));
		this.listheader_CustCtgIsActive.setSortDescending(new FieldComparator("custCtgIsActive", false));

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
		this.searchObj = new JdbcSearchObject<CustomerCategory>(
				CustomerCategory.class, getListRows());
		this.searchObj.addSort("CustCtgCode", false);
		this.searchObj.addFilter(new Filter("CustCtgCode",PennantConstants.NONE, Filter.OP_NOT_EQUAL));

		// Work flow
		if (isWorkFlowEnabled()) {
			this.searchObj.addTabelName("BMTCustCategories_View");
			if (isFirstTask()) {
				button_CustomerCategoryList_NewCustomerCategory.setVisible(true);
			} else {
				button_CustomerCategoryList_NewCustomerCategory.setVisible(false);
			}

			this.searchObj.addFilterIn("nextRoleCode", getUserWorkspace().getUserRoles(), isFirstTask());
		} else {
			this.searchObj.addTabelName("BMTCustCategories_AView");
		}

		setSearchObj(this.searchObj);
		if (!isWorkFlowEnabled() && wfAvailable) {
			this.button_CustomerCategoryList_NewCustomerCategory.setVisible(false);
			this.button_CustomerCategoryList_CustomerCategorySearchDialog.setVisible(false);
			this.button_CustomerCategoryList_PrintList.setVisible(false);
			PTMessageUtils.showErrorMessage(PennantJavaUtil.getLabel("WORKFLOW CONFIG NOT FOUND"));
		} else {
			// Set the ListModel for the articles.
			getPagedListWrapper().init(this.searchObj, this.listBoxCustomerCategory, this.pagingCustomerCategoryList);
			// set the itemRenderer
			this.listBoxCustomerCategory.setItemRenderer(new CustomerCategoryListModelItemRenderer());
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * SetVisible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug("Entering");

		getUserWorkspace().alocateAuthorities("CustomerCategoryList");

		this.button_CustomerCategoryList_NewCustomerCategory.setVisible(getUserWorkspace()
				.isAllowed("button_CustomerCategoryList_NewCustomerCategory"));
		this.button_CustomerCategoryList_CustomerCategorySearchDialog.setVisible(getUserWorkspace()
				.isAllowed("button_CustomerCategoryList_CustomerCategoryFindDialog"));
		this.button_CustomerCategoryList_PrintList.setVisible(getUserWorkspace()
				.isAllowed("button_CustomerCategoryList_PrintList"));
		logger.debug("Leaving");
	}

	/**
	 * This method is forwarded from the list boxes item renderer. <br>
	 * see: com.pennant.webui.bmtmasters.customercategory.model.
	 * CustomerCategoryListModelItemRenderer.java <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCustomerCategoryItemDoubleClicked(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		// get the selected CustomerCategory object
		final Listitem item = this.listBoxCustomerCategory.getSelectedItem();

		if (item != null) {
			// CAST AND STORE THE SELECTED OBJECT
			final CustomerCategory aCustomerCategory = (CustomerCategory) item.getAttribute("data");
			final CustomerCategory customerCategory = getCustomerCategoryService().getCustomerCategoryById(aCustomerCategory.getId());

			if (customerCategory == null) {

				String[] valueParm = new String[2];
				String[] errorParm = new String[2];

				valueParm[0] = aCustomerCategory.getCustCtgCode();
				errorParm[0] = PennantJavaUtil.getLabel("label_CustCtg_Code") + ":" + aCustomerCategory.getCustCtgCode();

				ErrorDetails errorDetails = ErrorUtil.getErrorDetail(
						new ErrorDetails(PennantConstants.KEY_FIELD, "41005",errorParm, valueParm), getUserWorkspace().getUserLanguage());
				PTMessageUtils.showErrorMessage(errorDetails.getErrorMessage());
			} else {
				String whereCond = " AND CustCtgCode='" + customerCategory.getCustCtgCode() 
				+ "' AND version=" + customerCategory.getVersion() + " ";

				if (isWorkFlowEnabled()) {
					boolean userAcces = validateUserAccess(workFlowDetails.getId(), getUserWorkspace().getLoginUserDetails().getLoginUsrID(),
							"CustomerCategory", whereCond, customerCategory.getTaskId(), customerCategory.getNextTaskId());
					if (userAcces) {
						showDetailView(customerCategory);
					} else {
						PTMessageUtils.showErrorMessage(Labels.getLabel("RECORD_NOTALLOWED"));
					}
				} else {
					showDetailView(customerCategory);
				}
			}
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Call the CustomerCategory dialog with a new empty entry. <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClick$button_CustomerCategoryList_NewCustomerCategory(
			Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		// create a new CustomerCategory object, We GET it from the back end.
		final CustomerCategory aCustomerCategory = getCustomerCategoryService().getNewCustomerCategory();
		showDetailView(aCustomerCategory);
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Opens the detail view. <br>
	 * Over handed some parameters in a map if needed. <br>
	 * 
	 * @param CustomerCategory
	 *            (aCustomerCategory)
	 * @throws Exception
	 */
	private void showDetailView(CustomerCategory aCustomerCategory)	throws Exception {
		logger.debug("Entering");

		/*
		 * We can call our Dialog ZUL-file with parameters. So we can call them
		 * with a object of the selected item. For handed over these parameter
		 * only a Map is accepted. So we put the object in a HashMap.
		 */
		if (aCustomerCategory.getWorkflowId() == 0 && isWorkFlowEnabled()) {
			aCustomerCategory.setWorkflowId(workFlowDetails.getWorkFlowId());
		}

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("customerCategory", aCustomerCategory);
		/*
		 * we can additionally handed over the listBox or the controller self,
		 * so we have in the dialog access to the list box List model. This is
		 * fine for synchronizing the data in the CustomerCategoryListbox from
		 * the dialog when we do a delete, edit or insert a CustomerCategory.
		 */
		map.put("customerCategoryListCtrl", this);

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents(
					"/WEB-INF/pages/ApplicationMaster/CustomerCategory/CustomerCategoryDialog.zul", null, map);
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
		PTMessageUtils.showHelpWindow(event, window_CustomerCategoryList);
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
		this.pagingCustomerCategoryList.setActivePage(0);
		Events.postEvent("onCreate", this.window_CustomerCategoryList, event);
		this.window_CustomerCategoryList.invalidate();
		logger.debug("Leaving");
	}

	/**
	 * call the CustomerCategory dialog
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClick$button_CustomerCategoryList_CustomerCategorySearchDialog(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		/*
		 * we can call our CustomerCategoryDialog ZUL-file with parameters. So
		 * we can call them with a object of the selected CustomerCategory. For
		 * handed over these parameter only a Map is accepted. So we put the
		 * CustomerCategory object in a HashMap.
		 */
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("customerCategoryCtrl", this);
		map.put("searchObject", this.searchObj);

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents(
					"/WEB-INF/pages/ApplicationMaster/CustomerCategory/CustomerCategorySearchDialog.zul", null, map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / " + e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * When the customerCategory print button is clicked.
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	@SuppressWarnings("unused")
	public void onClick$button_CustomerCategoryList_PrintList(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		PTListReportUtils reportUtils = new PTListReportUtils("CustomerCategory", getSearchObj(),this.pagingCustomerCategoryList.getTotalSize()+1);
		logger.debug("Leaving" + event.toString());
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public void setCustomerCategoryService(CustomerCategoryService customerCategoryService) {
		this.customerCategoryService = customerCategoryService;
	}
	public CustomerCategoryService getCustomerCategoryService() {
		return this.customerCategoryService;
	}

	public JdbcSearchObject<CustomerCategory> getSearchObj() {
		return this.searchObj;
	}
	public void setSearchObj(JdbcSearchObject<CustomerCategory> searchObj) {
		this.searchObj = searchObj;
	}
}