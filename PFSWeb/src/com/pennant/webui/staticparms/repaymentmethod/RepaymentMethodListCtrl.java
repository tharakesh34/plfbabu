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
 * FileName    		:  RepaymentMethodListCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  12-09-2011    														*
 *                                                                  						*
 * Modified Date    :  12-09-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 12-09-2011       Pennant	                 0.1                                            * 
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

package com.pennant.webui.staticparms.repaymentmethod;

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
import com.pennant.backend.model.staticparms.RepaymentMethod;
import com.pennant.backend.service.staticparms.RepaymentMethodService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.webui.staticparms.repaymentmethod.model.RepaymentMethodListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.PTListReportUtils;
import com.pennant.webui.util.PTMessageUtils;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * /WEB-INF/pages/StaticParms/RepaymentMethod/RepaymentMethodList.zul file.<br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * 
 */
public class RepaymentMethodListCtrl extends GFCBaseListCtrl<RepaymentMethod> implements Serializable {

	private static final long serialVersionUID = -5553791000283544547L;
	private final static Logger logger = Logger.getLogger(RepaymentMethodListCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window 		window_RepaymentMethodList; 		// autoWired
	protected Borderlayout 	borderLayout_RepaymentMethodList; 	// autoWired
	protected Paging 		pagingRepaymentMethodList; 			// autoWired
	protected Listbox 		listBoxRepaymentMethod; 			// autoWired

	// List headers
	protected Listheader listheader_RepayMethod; 				// autoWired
	protected Listheader listheader_RepayMethodDesc; 			// autoWired
	protected Listheader listheader_RecordStatus; 				// autoWired
	protected Listheader listheader_RecordType;

	// checkRights
	protected Button btnHelp; 													// autoWired
	protected Button button_RepaymentMethodList_NewRepaymentMethod; 			// autoWired
	protected Button button_RepaymentMethodList_RepaymentMethodSearchDialog; 	// autoWired
	protected Button button_RepaymentMethodList_PrintList; 						// autoWired

	// NEEDED for the ReUse in the SearchWindow
	protected JdbcSearchObject<RepaymentMethod> searchObj;

	private transient RepaymentMethodService repaymentMethodService;
	private transient WorkFlowDetails workFlowDetails = null;

	/**
	 * default constructor.<br>
	 */
	public RepaymentMethodListCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	/**
	 * Before binding the data and calling the List window we check, if the
	 * ZUL-file is called with a parameter for a selected RepaymentMethod object
	 * in a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_RepaymentMethodList(Event event)throws Exception {
		logger.debug("Entering" + event.toString());

		ModuleMapping moduleMapping = PennantJavaUtil.getModuleMap("RepaymentMethod");
		boolean wfAvailable = true;

		if (moduleMapping.getWorkflowType() != null) {
			workFlowDetails = WorkFlowUtil.getWorkFlowDetails("RepaymentMethod");

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

		/* set components visible dependent on the users rights */
		doCheckRights();

		this.borderLayout_RepaymentMethodList.setHeight(getBorderLayoutHeight());

		// set the paging parameters
		this.pagingRepaymentMethodList.setPageSize(getListRows());
		this.pagingRepaymentMethodList.setDetailed(true);

		this.listheader_RepayMethod.setSortAscending(new FieldComparator("repayMethod", true));
		this.listheader_RepayMethod.setSortDescending(new FieldComparator("repayMethod", false));
		this.listheader_RepayMethodDesc.setSortAscending(new FieldComparator("repayMethodDesc", true));
		this.listheader_RepayMethodDesc.setSortDescending(new FieldComparator("repayMethodDesc", false));

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
		this.searchObj = new JdbcSearchObject<RepaymentMethod>(RepaymentMethod.class, getListRows());
		this.searchObj.addSort("RepayMethod", false);

		// WorkFlow
		if (isWorkFlowEnabled()) {
			this.searchObj.addTabelName("BMTRepayMethod_View");
			if (isFirstTask()) {
				button_RepaymentMethodList_NewRepaymentMethod.setVisible(true);
			} else {
				button_RepaymentMethodList_NewRepaymentMethod.setVisible(false);
			}
			this.searchObj.addFilterIn("nextRoleCode", getUserWorkspace().getUserRoles(), isFirstTask());
		}else{
			this.searchObj.addTabelName("BMTRepayMethod_AView");
		}

		setSearchObj(this.searchObj);
		if (!isWorkFlowEnabled() && wfAvailable) {
			this.button_RepaymentMethodList_NewRepaymentMethod.setVisible(false);
			this.button_RepaymentMethodList_RepaymentMethodSearchDialog.setVisible(false);
			this.button_RepaymentMethodList_PrintList.setVisible(false);
			PTMessageUtils.showErrorMessage(PennantJavaUtil.getLabel("WORKFLOW CONFIG NOT FOUND"));
		} else {
			// Set the ListModel for the articles.
			getPagedListWrapper().init(this.searchObj, this.listBoxRepaymentMethod,this.pagingRepaymentMethodList);
			// set the itemRenderer
			this.listBoxRepaymentMethod.setItemRenderer(new RepaymentMethodListModelItemRenderer());
		}
		logger.debug("Leaving");
	}

	/**
	 * SetVisible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug("Entering");
		getUserWorkspace().alocateAuthorities("RepaymentMethodList");

		this.button_RepaymentMethodList_NewRepaymentMethod.setVisible(getUserWorkspace()
				.isAllowed("button_RepaymentMethodList_NewRepaymentMethod"));
		this.button_RepaymentMethodList_RepaymentMethodSearchDialog.setVisible(getUserWorkspace()
				.isAllowed("button_RepaymentMethodList_RepaymentMethodFindDialog"));
		this.button_RepaymentMethodList_PrintList.setVisible(getUserWorkspace()
				.isAllowed("button_RepaymentMethodList_PrintList"));
		logger.debug("Leaving");
	}

	/**
	 * This method is forwarded from the listBoxes item renderer. <br>
	 * see: com.pennant.webui.StaticParms.repaymentmethod.model.
	 * RepaymentMethodListModelItemRenderer.java <br>
	 * 
	 * @param event
	 * @throws Exception
	 */

	public void onRepaymentMethodItemDoubleClicked(Event event)throws Exception {
		logger.debug("Entering" + event.toString());

		// get the selected RepaymentMethod object
		final Listitem item = this.listBoxRepaymentMethod.getSelectedItem();

		if (item != null) {
			// CAST AND STORE THE SELECTED OBJECT
			final RepaymentMethod aRepaymentMethod = (RepaymentMethod) item.getAttribute("data");
			final RepaymentMethod repaymentMethod = getRepaymentMethodService().getRepaymentMethodById(aRepaymentMethod.getId());

			if (repaymentMethod == null) {

				String[] errParm = new String[1];
				String[] valueParm = new String[1];

				valueParm[0] = aRepaymentMethod.getId();
				errParm[0] = PennantJavaUtil.getLabel("label_RepayMethod")+ ":" + valueParm[0];

				ErrorDetails errorDetails = ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41005",
						errParm, valueParm), getUserWorkspace().getUserLanguage());
				PTMessageUtils.showErrorMessage(errorDetails.getErrorMessage());
			} else {
				if (isWorkFlowEnabled()) {
					String whereCond = " AND RepayMethod='"+ repaymentMethod.getRepayMethod()
					+ "' AND version=" + repaymentMethod.getVersion()+ " ";

					boolean userAcces = validateUserAccess(
							workFlowDetails.getId(), getUserWorkspace().getLoginUserDetails().getLoginUsrID(),
							"RepaymentMethod", whereCond,repaymentMethod.getTaskId(),repaymentMethod.getNextTaskId());
					if (userAcces) {
						showDetailView(repaymentMethod);
					} else {
						PTMessageUtils.showErrorMessage(Labels.getLabel("RECORD_NOTALLOWED"));
					}
				} else {
					showDetailView(repaymentMethod);
				}
			}
		}
		logger.debug("Leaving");
	}

	/**
	 * Call the RepaymentMethod dialog with a new empty entry. <br>
	 */
	public void onClick$button_RepaymentMethodList_NewRepaymentMethod(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		// create a new RepaymentMethod object, We GET it from the backEnd.
		final RepaymentMethod aRepaymentMethod = getRepaymentMethodService().getNewRepaymentMethod();
		showDetailView(aRepaymentMethod);
		logger.debug("Leaving");
	}

	/**
	 * Opens the detail view. <br>
	 * OverHanded some parameters in a map if needed. <br>
	 * 
	 * @param RepaymentMethod
	 *            (aRepaymentMethod)
	 * @throws Exception
	 */
	private void showDetailView(RepaymentMethod aRepaymentMethod)throws Exception {
		logger.debug("Entering");

		/*
		 * We can call our Dialog ZUL-file with parameters. So we can call them
		 * with a object of the selected item. For handed over these parameter
		 * only a Map is accepted. So we put the object in a HashMap.
		 */

		if (aRepaymentMethod.getWorkflowId() == 0 && isWorkFlowEnabled()) {
			aRepaymentMethod.setWorkflowId(workFlowDetails.getWorkFlowId());
		}

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("repaymentMethod", aRepaymentMethod);
		/*
		 * we can additionally handed over the listBox or the controller self,
		 * so we have in the dialog access to the listBox ListModel. This is
		 * fine for synchronizing the data in the RepaymentMethodListbox from
		 * the dialog when we do a delete, edit or insert a RepaymentMethod.
		 */
		map.put("repaymentMethodListCtrl", this);

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents(
					"/WEB-INF/pages/StaticParms/RepaymentMethod/RepaymentMethodDialog.zul",null, map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / "+ e.getMessage());
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
		PTMessageUtils.showHelpWindow(event, window_RepaymentMethodList);
		logger.debug("Leaving");
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
		this.pagingRepaymentMethodList.setActivePage(0);
		Events.postEvent("onCreate", this.window_RepaymentMethodList, event);
		this.window_RepaymentMethodList.invalidate();
		logger.debug("Leaving");
	}

	/*
	 * call the RepaymentMethod dialog
	 */

	public void onClick$button_RepaymentMethodList_RepaymentMethodSearchDialog(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		/*
		 * we can call our RepaymentMethodDialog ZUL-file with parameters. So we
		 * can call them with a object of the selected RepaymentMethod. For
		 * handed over these parameter only a Map is accepted. So we put the
		 * RepaymentMethod object in a HashMap.
		 */
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("repaymentMethodCtrl", this);
		map.put("searchObject", this.searchObj);

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents(
					"/WEB-INF/pages/StaticParms/RepaymentMethod/RepaymentMethodSearchDialog.zul",null, map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / "+ e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving");
	}

	/**
	 * When the repaymentMethod print button is clicked.
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$button_RepaymentMethodList_PrintList(Event event)throws InterruptedException {
		logger.debug("Entering" + event.toString());
		new PTListReportUtils("RepaymentMethod", getSearchObj(),this.pagingRepaymentMethodList.getTotalSize()+1);
		logger.debug("Leaving");
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public void setRepaymentMethodService(RepaymentMethodService repaymentMethodService) {
		this.repaymentMethodService = repaymentMethodService;
	}
	public RepaymentMethodService getRepaymentMethodService() {
		return this.repaymentMethodService;
	}

	public JdbcSearchObject<RepaymentMethod> getSearchObj() {
		return this.searchObj;
	}
	public void setSearchObj(JdbcSearchObject<RepaymentMethod> searchObj) {
		this.searchObj = searchObj;
	}
}