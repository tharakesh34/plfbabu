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
 * FileName    		:  PFSParameterListCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  12-07-2011    														*
 *                                                                  						*
 * Modified Date    :  12-07-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 12-07-2011       Pennant	                 0.1                                            * 
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

package com.pennant.webui.smtmasters.pfsparameter;

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
import com.pennant.backend.model.smtmasters.PFSParameter;
import com.pennant.backend.service.smtmasters.PFSParameterService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.webui.smtmasters.pfsparameter.model.PFSParameterListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.PTListReportUtils;
import com.pennant.webui.util.PTMessageUtils;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * /WEB-INF/pages/SolutionFactory/PFSParameter/PFSParameterList.zul file.<br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * 
 */
public class PFSParameterListCtrl extends GFCBaseListCtrl<PFSParameter> implements Serializable {

	private static final long serialVersionUID = 8002179731510010018L;

	private final static Logger logger = Logger.getLogger(PFSParameterListCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window 		window_PFSParameterList; 				// autowired
	protected Borderlayout 	borderLayout_PFSParameterList;   		// autowired
	protected Paging 		pagingPFSParameterList; 				// autowired
	protected Listbox 		listBoxPFSParameter; 					// autowired

	// List headers
	protected Listheader listheader_SysParmCode;  // autowired
	protected Listheader listheader_SysParmDesc;  // autowired
	protected Listheader listheader_SysParmValue; // autowired
	protected Listheader listheader_RecordStatus; // autowired
	protected Listheader listheader_RecordType;

	// checkRights
	protected Button btnHelp; 											// autowired
	protected Button button_PFSParameterList_NewPFSParameter; 			// autowired
	protected Button button_PFSParameterList_PFSParameterSearchDialog;  // autowired
	protected Button button_PFSParameterList_PrintList; 				// autowired

	// NEEDED for the ReUse in the SearchWindow
	protected JdbcSearchObject<PFSParameter> searchObj;
	private transient PFSParameterService pFSParameterService;
	private transient WorkFlowDetails workFlowDetails = null;

	/**
	 * default constructor.<br>
	 */
	public PFSParameterListCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the List window we check, if the
	 * ZUL-file is called with a parameter for a selected PFSParameter object in
	 * a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_PFSParameterList(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		ModuleMapping moduleMapping = PennantJavaUtil.getModuleMap("PFSParameter");
		boolean wfAvailable = true;

		if (moduleMapping.getWorkflowType() != null) {
			workFlowDetails = WorkFlowUtil.getWorkFlowDetails("PFSParameter");

			if (workFlowDetails == null) {
				setWorkFlowEnabled(false);
			} else {
				setWorkFlowEnabled(true);
				setFirstTask(getUserWorkspace().isRoleContains(
						workFlowDetails.getFirstTaskOwner()));
				setWorkFlowId(workFlowDetails.getId());
			}
		} else {
			wfAvailable = false;
		}

		/* set components visible dependent of the users rights */
		doCheckRights();

		/**
		 * Calculate how many rows have been place in the listbox. Get the
		 * currentDesktopHeight from a hidden Intbox from the index.zul that are
		 * filled by onClientInfo() in the indexCtroller
		 */

		this.borderLayout_PFSParameterList.setHeight(getBorderLayoutHeight());

		// set the paging parameters
		this.pagingPFSParameterList.setPageSize(getListRows());
		this.pagingPFSParameterList.setDetailed(true);

		this.listheader_SysParmCode.setSortAscending(new FieldComparator("sysParmCode", true));
		this.listheader_SysParmCode.setSortDescending(new FieldComparator("sysParmCode", false));
		this.listheader_SysParmDesc.setSortAscending(new FieldComparator("sysParmDesc", true));
		this.listheader_SysParmDesc.setSortDescending(new FieldComparator("sysParmDesc", false));
		this.listheader_SysParmValue.setSortAscending(new FieldComparator("sysParmValue", true));
		this.listheader_SysParmValue.setSortDescending(new FieldComparator("sysParmValue", false));

		if (isWorkFlowEnabled()) {
			this.listheader_RecordStatus.setSortAscending(new FieldComparator("recordStatus", true));
			this.listheader_RecordStatus.setSortDescending(new FieldComparator("recordStatus", false));
			this.listheader_RecordType.setSortAscending(new FieldComparator("recordType", true));
			this.listheader_RecordType.setSortDescending(new FieldComparator("recordType", false));
		} else {
			this.listheader_RecordStatus.setVisible(false);
			this.listheader_RecordType.setVisible(false);
		}

		// ++ create the searchObject and init sorting ++//
		this.searchObj = new JdbcSearchObject<PFSParameter>(PFSParameter.class,getListRows());
		this.searchObj.addSort("SysParmCode", false);

		this.searchObj.addTabelName("SMTparameters_View");

		// Workflow
		if (isWorkFlowEnabled()) {
			if (isFirstTask()) {
				button_PFSParameterList_NewPFSParameter.setVisible(true);
			} else {
				button_PFSParameterList_NewPFSParameter.setVisible(false);
			}

			this.searchObj.addFilterIn("nextRoleCode", getUserWorkspace()
					.getUserRoles(), isFirstTask());
		}

		setSearchObj(this.searchObj);
		if (!isWorkFlowEnabled() && wfAvailable) {
			this.button_PFSParameterList_NewPFSParameter.setVisible(false);
			this.button_PFSParameterList_PFSParameterSearchDialog.setVisible(false);
			this.button_PFSParameterList_PrintList.setVisible(false);
			PTMessageUtils.showErrorMessage(PennantJavaUtil.getLabel("WORKFLOW CONFIG NOT FOUND"));
		} else {
			// Set the ListModel for the articles.
			getPagedListWrapper().init(this.searchObj, this.listBoxPFSParameter, this.pagingPFSParameterList);
			// set the itemRenderer
			this.listBoxPFSParameter.setItemRenderer(new PFSParameterListModelItemRenderer());
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * SetVisible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug("Entering ");
		getUserWorkspace().alocateAuthorities("PFSParameterList");
		this.button_PFSParameterList_NewPFSParameter.setVisible(getUserWorkspace().
				isAllowed("button_PFSParameterList_NewPFSParameter"));
		this.button_PFSParameterList_PFSParameterSearchDialog.setVisible(getUserWorkspace().
				isAllowed("button_PFSParameterList_PFSParameterFindDialog"));
		this.button_PFSParameterList_PrintList.setVisible(getUserWorkspace().
				isAllowed("button_PFSParameterList_PrintList"));
		logger.debug("Leaving ");
	}

	/**
	 * This method is forwarded from the listboxes item renderer. <br>
	 * see: com.pennant.webui.smtmasters.pfsparameter.model.
	 * PFSParameterListModelItemRenderer.java <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onPFSParameterItemDoubleClicked(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		// get the selected PFSParameter object
		final Listitem item = this.listBoxPFSParameter.getSelectedItem();

		if (item != null) {
			// CAST AND STORE THE SELECTED OBJECT
			final PFSParameter aPFSParameter = (PFSParameter) item.getAttribute("data");
			final PFSParameter pFSParameter = getPFSParameterService().getPFSParameterById(aPFSParameter.getId());

			if (pFSParameter == null) {

				String[] valueParm = new String[1];
				String[] errParm = new String[1];

				valueParm[0] = aPFSParameter.getSysParmCode();
				errParm[0] = PennantJavaUtil.getLabel("label_PFSParameterDialog_SysParmCode")+ ":"+ valueParm[0];

				ErrorDetails errorDetails = ErrorUtil.getErrorDetail(new ErrorDetails(
						PennantConstants.KEY_FIELD, "41005",errParm, valueParm), getUserWorkspace().getUserLanguage());
				PTMessageUtils.showErrorMessage(errorDetails.getErrorMessage());
			} else {
				String whereCond = " AND SysParmCode='"+ pFSParameter.getSysParmCode() + 
									"' AND version="+ pFSParameter.getVersion() + " ";

				if (isWorkFlowEnabled()) {
					boolean userAcces = validateUserAccess(workFlowDetails.getId(), getUserWorkspace()
							.getLoginUserDetails().getLoginUsrID(),"PFSParameter", whereCond,
							pFSParameter.getTaskId(),pFSParameter.getNextTaskId());
					if (userAcces) {
						showDetailView(pFSParameter);
					} else {
						PTMessageUtils.showErrorMessage(Labels.getLabel("RECORD_NOTALLOWED"));
					}
				} else if (pFSParameter.isSysParmMaint()) {
					showDetailView(pFSParameter);
				} else {
					PTMessageUtils.showErrorMessage(Labels.getLabel("RECORD_NOTALLOWED"));
				}
			}
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Call the PFSParameter dialog with a new empty entry. <br>
	 */
	public void onClick$button_PFSParameterList_NewPFSParameter(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		// create a new PFSParameter object, We GET it from the backend.
		final PFSParameter aPFSParameter = getPFSParameterService().getNewPFSParameter();

		showDetailView(aPFSParameter);
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Opens the detail view. <br>
	 * Overhanded some params in a map if needed. <br>
	 * 
	 * @param PFSParameter
	 *            (aPFSParameter)
	 * @throws Exception
	 */
	private void showDetailView(PFSParameter aPFSParameter) throws Exception {
		logger.debug("Entering ");
		/*
		 * We can call our Dialog zul-file with parameters. So we can call them
		 * with a object of the selected item. For handed over these parameter
		 * only a Map is accepted. So we put the object in a HashMap.
		 */

		if (aPFSParameter.getWorkflowId() == 0 && isWorkFlowEnabled()) {
			aPFSParameter.setWorkflowId(workFlowDetails.getWorkFlowId());
		}

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("pFSParameter", aPFSParameter);
		/*
		 * we can additionally handed over the listBox or the controller self,
		 * so we have in the dialog access to the listbox Listmodel. This is
		 * fine for synchronizing the data in the PFSParameterListbox from the
		 * dialog when we do a delete, edit or insert a PFSParameter.
		 */
		map.put("pFSParameterListCtrl", this);

		// call the zul-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/SolutionFactory/PFSParameter/PFSParameterDialog.zul",
					null, map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / " + e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving ");
	}

	/**
	 * when the "help" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnHelp(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		PTMessageUtils.showHelpWindow(event, window_PFSParameterList);
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
		this.pagingPFSParameterList.setActivePage(0);
		Events.postEvent("onCreate", this.window_PFSParameterList, event);
		this.window_PFSParameterList.invalidate();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Method for call the PFSParameter dialog
	 * @param event
	 * @throws Exception
	 */
	public void onClick$button_PFSParameterList_PFSParameterSearchDialog(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		/*
		 * we can call our PFSParameterDialog zul-file with parameters. So we
		 * can call them with a object of the selected PFSParameter. For handed
		 * over these parameter only a Map is accepted. So we put the
		 * PFSParameter object in a HashMap.
		 */
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("pFSParameterCtrl", this);
		map.put("searchObject", this.searchObj);

		// call the zul-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/SolutionFactory/PFSParameter/PFSParameterSearchDialog.zul",
					null, map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / "+ e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * When the pFSParameter print button is clicked.
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$button_PFSParameterList_PrintList(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		new PTListReportUtils("PFSParameter", getSearchObj(),this.pagingPFSParameterList.getTotalSize()+1);
		logger.debug("Leaving" + event.toString());
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	public void setPFSParameterService(PFSParameterService pFSParameterService) {
		this.pFSParameterService = pFSParameterService;
	}
	public PFSParameterService getPFSParameterService() {
		return this.pFSParameterService;
	}

	public JdbcSearchObject<PFSParameter> getSearchObj() {
		return this.searchObj;
	}
	public void setSearchObj(JdbcSearchObject<PFSParameter> searchObj) {
		this.searchObj = searchObj;
	}
}