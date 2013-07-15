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
 * FileName    		:  SalutationListCtrl.java                                                   * 	  
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

package com.pennant.webui.systemmasters.salutation;

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
import com.pennant.backend.model.systemmasters.Salutation;
import com.pennant.backend.service.systemmasters.SalutationService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.webui.systemmasters.salutation.model.SalutationListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.PTListReportUtils;
import com.pennant.webui.util.PTMessageUtils;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * /WEB-INF/pages/SystemMasters/Salutation/SalutationList.zul file.<br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * 
 */
public class SalutationListCtrl extends GFCBaseListCtrl<Salutation> implements Serializable {

	private static final long serialVersionUID = 1690558052025431845L;
	private final static Logger logger = Logger.getLogger(SalutationListCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window 	   window_SalutationList; 				// autoWired
	protected Borderlayout borderLayout_SalutationList; 		// autoWired
	protected Paging 	   pagingSalutationList; 				// autoWired
	protected Listbox 	   listBoxSalutation; 					// autoWired

	// List headers
	protected Listheader   listheader_SalutationCode; 			// autoWired
	protected Listheader   listheader_SaluationDesc; 			// autoWired
	protected Listheader   listheader_SalutationIsActive; 		// autoWired
	protected Listheader   listheader_RecordStatus; 			// autoWired
	protected Listheader   listheader_RecordType;

	// checkRights
	protected Button btnHelp; 										// autoWired
	protected Button button_SalutationList_NewSalutation; 			// autoWired
	protected Button button_SalutationList_SalutationSearchDialog; 	// autoWired
	protected Button button_SalutationList_PrintList; 				// autoWired

	// NEEDED for the ReUse in the SearchWindow
	protected JdbcSearchObject<Salutation> searchObj;

	private transient SalutationService salutationService;
	private transient WorkFlowDetails   workFlowDetails=null;

	/**
	 * default constructor.<br>
	 */
	public SalutationListCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the List window we check, if the
	 * ZUL-file is called with a parameter for a selected SalutationCode object
	 * in a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_SalutationList(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		ModuleMapping moduleMapping = PennantJavaUtil.getModuleMap("Salutation");
		boolean wfAvailable = true;

		if (moduleMapping.getWorkflowType() != null) {
			workFlowDetails = WorkFlowUtil.getWorkFlowDetails("Salutation");

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
		 * Calculate how many rows have been place in the listBox. Get the
		 * currentDesktopHeight from a hidden IntBox from the index.zul that are
		 * filled by onClientInfo() in the indexCtroller
		 */
		this.borderLayout_SalutationList.setHeight(getBorderLayoutHeight());

		// set the paging parameters
		this.pagingSalutationList.setPageSize(getListRows());
		this.pagingSalutationList.setDetailed(true);

		// Apply sorting for getting List in the ListBox
		this.listheader_SalutationCode.setSortAscending(new FieldComparator("salutationCode", true));
		this.listheader_SalutationCode.setSortDescending(new FieldComparator("salutationCode", false));
		this.listheader_SaluationDesc.setSortAscending(new FieldComparator("saluationDesc", true));
		this.listheader_SaluationDesc.setSortDescending(new FieldComparator("saluationDesc", false));
		this.listheader_SalutationIsActive.setSortAscending(new FieldComparator("salutationIsActive", true));
		this.listheader_SalutationIsActive.setSortDescending(new FieldComparator("salutationIsActive", false));

		if (isWorkFlowEnabled()) {
			this.listheader_RecordStatus.setSortAscending(new FieldComparator("recordStatus", true));
			this.listheader_RecordStatus.setSortDescending(new FieldComparator("recordStatus", false));
			this.listheader_RecordType.setSortAscending(new FieldComparator("recordType", true));
			this.listheader_RecordType.setSortDescending(new FieldComparator("recordType", false));
		} else {
			this.listheader_RecordStatus.setVisible(false);
			this.listheader_RecordType.setVisible(false);
		}

		// ++ create the searchObject and initial sorting ++//
		this.searchObj = new JdbcSearchObject<Salutation>(Salutation.class, getListRows());
		this.searchObj.addSort("SalutationCode", false);

		// WorkFlow
		if (isWorkFlowEnabled()) {
			this.searchObj.addTabelName("BMTSalutations_View");
			if (isFirstTask()) {
				button_SalutationList_NewSalutation.setVisible(true);
			} else {
				button_SalutationList_NewSalutation.setVisible(false);
			}

			this.searchObj.addFilterIn("nextRoleCode", getUserWorkspace().getUserRoles(), isFirstTask());
		} else {
			this.searchObj.addTabelName("BMTSalutations_AView");
		}

		setSearchObj(this.searchObj);
		if (!isWorkFlowEnabled() && wfAvailable) {
			this.button_SalutationList_NewSalutation.setVisible(false);
			this.button_SalutationList_SalutationSearchDialog.setVisible(false);
			this.button_SalutationList_PrintList.setVisible(false);
			PTMessageUtils.showErrorMessage(PennantJavaUtil.getLabel("WORKFLOW CONFIG NOT FOUND"));
		} else {
			// Set the ListModel for the articles.
			getPagedListWrapper().init(this.searchObj, this.listBoxSalutation, this.pagingSalutationList);
			// set the itemRenderer
			this.listBoxSalutation.setItemRenderer(new SalutationListModelItemRenderer());
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * SetVisible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug("Entering");
		getUserWorkspace().alocateAuthorities("SalutationList");

		this.button_SalutationList_NewSalutation.setVisible(getUserWorkspace()
				.isAllowed("button_SalutationList_NewSalutation"));
		this.button_SalutationList_SalutationSearchDialog.setVisible(getUserWorkspace()
				.isAllowed("button_SalutationList_SalutationFindDialog"));
		this.button_SalutationList_PrintList.setVisible(getUserWorkspace()
				.isAllowed("button_SalutationList_PrintList"));
		logger.debug("Leaving");
	}

	/**
	 * This method is forwarded from the listBoxes item renderer. <br>
	 * see: com.pennant.webui.bmtmasters.salutation.model.
	 * SalutationListModelItemRenderer.java <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onSalutationItemDoubleClicked(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		// get the selected Salutation object
		final Listitem item = this.listBoxSalutation.getSelectedItem();

		if (item != null) {
			// CAST AND STORE THE SELECTED OBJECT
			final Salutation aSalutation = (Salutation) item.getAttribute("data");
			final Salutation salutation = getSalutationService().getSalutationById(aSalutation.getId());

			if (salutation == null) {

				String[] valueParm = new String[2];
				String[] errorParm = new String[2];

				valueParm[0] = aSalutation.getSalutationCode();
				errorParm[0] = PennantJavaUtil.getLabel("label_SalutationCode")	+ ":" + aSalutation.getSalutationCode();

				ErrorDetails errorDetails = ErrorUtil.getErrorDetail(
						new ErrorDetails(PennantConstants.KEY_FIELD, "41005", errorParm, valueParm), getUserWorkspace().getUserLanguage());
				PTMessageUtils.showErrorMessage(errorDetails.getErrorMessage());
			} else {

				String whereCond = " AND SalutationCode='" + salutation.getSalutationCode() 
				+ "' AND version=" + salutation.getVersion() + " ";

				if (isWorkFlowEnabled()) {
					boolean userAcces = validateUserAccess(workFlowDetails.getId(), getUserWorkspace().getLoginUserDetails().getLoginUsrID(),
							"Salutation", whereCond, salutation.getTaskId(), salutation.getNextTaskId());
					if (userAcces) {
						showDetailView(salutation);
					} else {
						PTMessageUtils.showErrorMessage(Labels.getLabel("RECORD_NOTALLOWED"));
					}
				} else {
					showDetailView(salutation);
				}
			}
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Call the Salutation dialog with a new empty entry. <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClick$button_SalutationList_NewSalutation(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		// create a new Salutation object, We GET it from the backEnd.
		final Salutation aSalutation = getSalutationService().getNewSalutation();
		showDetailView(aSalutation);
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Opens the detail view. <br>
	 * OverHanded some parameters in a map if needed. <br>
	 * 
	 * @param Salutation
	 *            (aSalutation)
	 * @throws Exception
	 */
	private void showDetailView(Salutation aSalutation) throws Exception {
		logger.debug("Entering");

		/*
		 * We can call our Dialog ZUL-file with parameters. So we can call them
		 * with a object of the selected item. For handed over these parameter
		 * only a Map is accepted. So we put the object in a HashMap.
		 */
		if (aSalutation.getWorkflowId() == 0 && isWorkFlowEnabled()) {
			aSalutation.setWorkflowId(workFlowDetails.getWorkFlowId());
		}
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("salutation", aSalutation);
		/*
		 * we can additionally handed over the listBox or the controller self,
		 * so we have in the dialog access to the listBox ListModel. This is
		 * fine for synchronizing the data in the SalutationListbox from the
		 * dialog when we do a delete, edit or insert a Salutation.
		 */
		map.put("salutationListCtrl", this);

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents(
					"/WEB-INF/pages/SystemMaster/Salutation/SalutationDialog.zul", null, map);
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
		PTMessageUtils.showHelpWindow(event, window_SalutationList);
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
		this.pagingSalutationList.setActivePage(0);
		Events.postEvent("onCreate", this.window_SalutationList, event);
		this.window_SalutationList.invalidate();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * call the Salutation dialog
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClick$button_SalutationList_SalutationSearchDialog(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		/*
		 * we can call our SalutationDialog ZUL-file with parameters. So we can
		 * call them with a object of the selected Salutation. For handed over
		 * these parameter only a Map is accepted. So we put the Salutation
		 * object in a HashMap.
		 */
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("salutationCtrl", this);
		map.put("searchObject", this.searchObj);

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents(
					"/WEB-INF/pages/SystemMaster/Salutation/SalutationSearchDialog.zul", null, map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / " + e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * When the salutation print button is clicked.
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	@SuppressWarnings("unused")
	public void onClick$button_SalutationList_PrintList(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		PTListReportUtils reportUtils = new PTListReportUtils("Salutation", getSearchObj(),this.pagingSalutationList.getTotalSize()+1);
		logger.debug("Leaving" + event.toString());
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public void setSalutationService(SalutationService salutationService) {
		this.salutationService = salutationService;
	}
	public SalutationService getSalutationService() {
		return this.salutationService;
	}

	public JdbcSearchObject<Salutation> getSearchObj() {
		return this.searchObj;
	}
	public void setSearchObj(JdbcSearchObject<Salutation> searchObj) {
		this.searchObj = searchObj;
	}
}