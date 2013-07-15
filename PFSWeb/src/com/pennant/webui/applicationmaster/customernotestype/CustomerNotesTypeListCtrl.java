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
 * FileName    		:  CustomerNotesTypeListCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  26-05-2011    														*
 *                                                                  						*
 * Modified Date    :  26-05-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 26-05-2011       Pennant	                 0.1                                            * 
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

package com.pennant.webui.applicationmaster.customernotestype;

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
import com.pennant.backend.model.applicationmaster.CustomerNotesType;
import com.pennant.backend.service.applicationmaster.CustomerNotesTypeService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.webui.applicationmaster.customernotestype.model.CustomerNotesTypeListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.PTListReportUtils;
import com.pennant.webui.util.PTMessageUtils;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * /WEB-INF/pages/ApplicationMaster/CustomerNotesType/CustomerNotesTypeList.zul file.<br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * 
 */
public class CustomerNotesTypeListCtrl extends GFCBaseListCtrl<CustomerNotesType> implements Serializable {

	private static final long serialVersionUID = -9149300436300750011L;
	private final static Logger logger = Logger.getLogger(CustomerNotesTypeListCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by
	 * our 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window 		window_CustomerNotesTypeList; 				// autoWired
	protected Borderlayout 	borderLayout_CustomerNotesTypeList; 		// autoWired
	protected Paging 		pagingCustomerNotesTypeList; 				// autoWired
	protected Listbox 		listBoxCustomerNotesType; 					// autoWired

	// List headers
	protected Listheader listheader_CustNotesTypeCode; 				// autoWired
	protected Listheader listheader_CustNotesTypeDesc; 				// autoWired
	protected Listheader listheader_CustNotesTypeIsPerminent; 		// autoWired
	protected Listheader listheader_CustNotesTypeArchiveFrq; 		// autoWired
	protected Listheader listheader_RecordStatus; 					// autoWired
	protected Listheader listheader_RecordType;

	// checkRights
	protected Button btnHelp; 														// autoWired
	protected Button button_CustomerNotesTypeList_NewCustomerNotesType; 			// autoWired
	protected Button button_CustomerNotesTypeList_CustomerNotesTypeSearchDialog; 	// autoWired
	protected Button button_CustomerNotesTypeList_PrintList; 						// autoWired

	// NEEDED for the ReUse in the SearchWindow
	protected JdbcSearchObject<CustomerNotesType> searchObj;

	private transient CustomerNotesTypeService customerNotesTypeService;
	private transient WorkFlowDetails workFlowDetails = null;

	/**
	 * default constructor.<br>
	 */
	public CustomerNotesTypeListCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the List window we check, if the
	 * ZUL-file is called with a parameter for a selected CustomerNotesType
	 * object in a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_CustomerNotesTypeList(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		ModuleMapping moduleMapping = PennantJavaUtil.getModuleMap("CustomerNotesType");
		boolean wfAvailable = true;

		if (moduleMapping.getWorkflowType() != null) {
			workFlowDetails = WorkFlowUtil.getWorkFlowDetails("CustomerNotesType");

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
		this.borderLayout_CustomerNotesTypeList.setHeight(getBorderLayoutHeight());

		// set the paging parameters
		this.pagingCustomerNotesTypeList.setPageSize(getListRows());
		this.pagingCustomerNotesTypeList.setDetailed(true);

		this.listheader_CustNotesTypeCode.setSortAscending(new FieldComparator("custNotesTypeCode", true));
		this.listheader_CustNotesTypeCode.setSortDescending(new FieldComparator("custNotesTypeCode", false));
		this.listheader_CustNotesTypeDesc.setSortAscending(new FieldComparator("custNotesTypeDesc", true));
		this.listheader_CustNotesTypeDesc.setSortDescending(new FieldComparator("custNotesTypeDesc", false));
		this.listheader_CustNotesTypeIsPerminent.setSortAscending(new FieldComparator("custNotesTypeIsPerminent", true));
		this.listheader_CustNotesTypeIsPerminent.setSortDescending(new FieldComparator("custNotesTypeIsPerminent", false));
		this.listheader_CustNotesTypeArchiveFrq.setSortAscending(new FieldComparator("custNotesTypeArchiveFrq", true));
		this.listheader_CustNotesTypeArchiveFrq.setSortDescending(new FieldComparator("custNotesTypeArchiveFrq", false));

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
		this.searchObj = new JdbcSearchObject<CustomerNotesType>(CustomerNotesType.class, getListRows());
		this.searchObj.addSort("CustNotesTypeCode", false);

		// Work flow
		if (isWorkFlowEnabled()) {
			this.searchObj.addTabelName("BMTCustNotesTypes_View");
			if (isFirstTask()) {
				button_CustomerNotesTypeList_NewCustomerNotesType.setVisible(true);
			} else {
				button_CustomerNotesTypeList_NewCustomerNotesType.setVisible(false);
			}

			this.searchObj.addFilterIn("nextRoleCode", getUserWorkspace().getUserRoles(), isFirstTask());
		} else {
			this.searchObj.addTabelName("BMTCustNotesTypes_AView");
		}

		setSearchObj(this.searchObj);
		if (!isWorkFlowEnabled() && wfAvailable) {
			this.button_CustomerNotesTypeList_NewCustomerNotesType.setVisible(false);
			this.button_CustomerNotesTypeList_CustomerNotesTypeSearchDialog.setVisible(false);
			this.button_CustomerNotesTypeList_PrintList.setVisible(false);
			PTMessageUtils.showErrorMessage(PennantJavaUtil.getLabel("WORKFLOW CONFIG NOT FOUND"));
		} else {
			// Set the ListModel for the articles.
			getPagedListWrapper().init(this.searchObj, this.listBoxCustomerNotesType, this.pagingCustomerNotesTypeList);
			// set the itemRenderer
			this.listBoxCustomerNotesType.setItemRenderer(new CustomerNotesTypeListModelItemRenderer());
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * SetVisible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug("Entering");

		getUserWorkspace().alocateAuthorities("CustomerNotesTypeList");

		this.button_CustomerNotesTypeList_NewCustomerNotesType.setVisible(getUserWorkspace()
				.isAllowed("button_CustomerNotesTypeList_NewCustomerNotesType"));
		this.button_CustomerNotesTypeList_CustomerNotesTypeSearchDialog.setVisible(getUserWorkspace()
				.isAllowed("button_CustomerNotesTypeList_CustomerNotesTypeFindDialog"));
		this.button_CustomerNotesTypeList_PrintList.setVisible(getUserWorkspace()
				.isAllowed("button_CustomerNotesTypeList_PrintList"));
		logger.debug("Leaving");
	}

	/**
	 * This method is forwarded from the list boxes item renderer. <br>
	 * see: com.pennant.webui.bmtmasters.customernotestype.model.
	 * CustomerNotesTypeListModelItemRenderer.java <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCustomerNotesTypeItemDoubleClicked(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		// get the selected CustomerNotesType object
		final Listitem item = this.listBoxCustomerNotesType.getSelectedItem();

		if (item != null) {
			// CAST AND STORE THE SELECTED OBJECT
			final CustomerNotesType aCustomerNotesType = (CustomerNotesType) item.getAttribute("data");
			final CustomerNotesType customerNotesType = getCustomerNotesTypeService()
			.getCustomerNotesTypeById(aCustomerNotesType.getId());

			if (customerNotesType == null) {

				String[] valueParm = new String[2];
				String[] errorParm = new String[2];

				valueParm[0] = aCustomerNotesType.getCustNotesTypeCode();
				errorParm[0] = PennantJavaUtil.getLabel("label_CustNotesTypeCode") + ":" + aCustomerNotesType.getCustNotesTypeCode();

				ErrorDetails errorDetails = ErrorUtil.getErrorDetail(
						new ErrorDetails(PennantConstants.KEY_FIELD, "41005", errorParm, valueParm), getUserWorkspace().getUserLanguage());
				PTMessageUtils.showErrorMessage(errorDetails.getErrorMessage());
			} else {
				String whereCond = " AND CustNotesTypeCode='" + customerNotesType.getCustNotesTypeCode()
				+ "' AND version=" + customerNotesType.getVersion()	+ " ";

				if (isWorkFlowEnabled()) {
					boolean userAcces = validateUserAccess(workFlowDetails.getId(), getUserWorkspace().getLoginUserDetails().getLoginUsrID(),
							"CustomerNotesType", whereCond, customerNotesType.getTaskId(), customerNotesType.getNextTaskId());
					if (userAcces) {
						showDetailView(customerNotesType);
					} else {
						PTMessageUtils.showErrorMessage(Labels.getLabel("RECORD_NOTALLOWED"));
					}
				} else {
					showDetailView(customerNotesType);
				}
			}
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Call the CustomerNotesType dialog with a new empty entry. <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClick$button_CustomerNotesTypeList_NewCustomerNotesType(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		// create a new CustomerNotesType object, We GET it from the back end.
		final CustomerNotesType aCustomerNotesType = getCustomerNotesTypeService().getNewCustomerNotesType();
		showDetailView(aCustomerNotesType);
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Opens the detail view. <br>
	 * Over handed some parameters in a map if needed. <br>
	 * 
	 * @param CustomerNotesType
	 *            (aCustomerNotesType)
	 * @throws Exception
	 */
	private void showDetailView(CustomerNotesType aCustomerNotesType) throws Exception {
		logger.debug("Entering");

		/*
		 * We can call our Dialog ZUL-file with parameters. So we can call them
		 * with a object of the selected item. For handed over these parameter
		 * only a Map is accepted. So we put the object in a HashMap.
		 */
		if (aCustomerNotesType.getWorkflowId() == 0 && isWorkFlowEnabled()) {
			aCustomerNotesType.setWorkflowId(workFlowDetails.getWorkFlowId());
		}

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("customerNotesType", aCustomerNotesType);
		/*
		 * we can additionally handed over the listBox or the controller self,
		 * so we have in the dialog access to the list box List model. This is
		 * fine for synchronizing the data in the CustomerNotesTypeListbox from
		 * the dialog when we do a delete, edit or insert a CustomerNotesType.
		 */
		map.put("customerNotesTypeListCtrl", this);

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents(
					"/WEB-INF/pages/ApplicationMaster/CustomerNotesType/CustomerNotesTypeDialog.zul", null, map);
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
		PTMessageUtils.showHelpWindow(event, window_CustomerNotesTypeList);
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
		this.pagingCustomerNotesTypeList.setActivePage(0);
		Events.postEvent("onCreate", this.window_CustomerNotesTypeList, event);
		this.window_CustomerNotesTypeList.invalidate();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * call the CustomerNotesType dialog
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClick$button_CustomerNotesTypeList_CustomerNotesTypeSearchDialog(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		/*
		 * we can call our CustomerNotesTypeDialog ZUL-file with parameters. So
		 * we can call them with a object of the selected CustomerNotesType. For
		 * handed over these parameter only a Map is accepted. So we put the
		 * CustomerNotesType object in a HashMap.
		 */
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("customerNotesTypeCtrl", this);
		map.put("searchObject", this.searchObj);

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents(
					"/WEB-INF/pages/ApplicationMaster/CustomerNotesType/CustomerNotesTypeSearchDialog.zul", null, map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / " + e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * When the customerNotesType print button is clicked.
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	@SuppressWarnings("unused")
	public void onClick$button_CustomerNotesTypeList_PrintList(Event event)	throws InterruptedException {
		logger.debug("Entering" + event.toString());
		PTListReportUtils reportUtils = new PTListReportUtils("CustomerNotesType", getSearchObj(),this.pagingCustomerNotesTypeList.getTotalSize()+1);
		logger.debug("Leaving" + event.toString());
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public void setCustomerNotesTypeService(CustomerNotesTypeService customerNotesTypeService) {
		this.customerNotesTypeService = customerNotesTypeService;
	}
	public CustomerNotesTypeService getCustomerNotesTypeService() {
		return this.customerNotesTypeService;
	}

	public JdbcSearchObject<CustomerNotesType> getSearchObj() {
		return this.searchObj;
	}
	public void setSearchObj(JdbcSearchObject<CustomerNotesType> searchObj) {
		this.searchObj = searchObj;
	}
}