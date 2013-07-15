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
 * FileName    		:  FrequencyListCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  05-05-2011    														*
 *                                                                  						*
 * Modified Date    :  05-05-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 05-05-2011       Pennant	                 0.1                                            * 
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

package com.pennant.webui.staticparms.frequency;

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
import com.pennant.backend.model.staticparms.Frequency;
import com.pennant.backend.service.staticparms.FrequencyService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.webui.staticparms.frequency.model.FrequencyListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.PTMessageUtils;
import com.pennant.webui.util.PTReportUtils;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * /WEB-INF/pages/StaticParms/Frequency/FrequencyList.zul file.<br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * 
 */
public class FrequencyListCtrl extends GFCBaseListCtrl<Frequency> implements Serializable {

	private static final long serialVersionUID = -2254447125626598370L;
	private final static Logger logger = Logger.getLogger(FrequencyListCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window 		window_FrequencyList; 		// autoWired
	protected Borderlayout 	borderLayout_FrequencyList; // autoWired
	protected Paging 		pagingFrequencyList; 		// autoWired
	protected Listbox 		listBoxFrequency; 			// autoWired

	// List headers
	protected Listheader listheader_FrqCode; 		// autoWired
	protected Listheader listheader_FrqDesc; 		// autoWired
	protected Listheader listheader_FrqIsActive; 	// autoWired
	protected Listheader listheader_RecordStatus; 	// autoWired
	protected Listheader listheader_RecordType;

	// checkRights
	protected Button btnHelp; 										// autoWired
	protected Button button_FrequencyList_NewFrequency; 			// autoWired
	protected Button button_FrequencyList_FrequencySearchDialog; 	// autoWired
	protected Button button_FrequencyList_PrintList; 				// autoWired

	// NEEDED for the ReUse in the SearchWindow
	protected JdbcSearchObject<Frequency> searchObj;

	private transient FrequencyService frequencyService;
	private transient WorkFlowDetails workFlowDetails=null;

	/**
	 * default constructor.<br>
	 */
	public FrequencyListCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the List window we check, if the
	 * ZUL-file is called with a parameter for a selected Frequency object in a
	 * Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_FrequencyList(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		ModuleMapping moduleMapping = PennantJavaUtil.getModuleMap("Frequency");
		boolean wfAvailable = true;

		if (moduleMapping.getWorkflowType() != null) {
			workFlowDetails = WorkFlowUtil.getWorkFlowDetails("Frequency");

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
		this.borderLayout_FrequencyList.setHeight(getBorderLayoutHeight());

		// set the paging parameters
		this.pagingFrequencyList.setPageSize(getListRows());
		this.pagingFrequencyList.setDetailed(true);

		this.listheader_FrqCode.setSortAscending(new FieldComparator("frqCode",	true));
		this.listheader_FrqCode.setSortDescending(new FieldComparator("frqCode", false));
		this.listheader_FrqDesc.setSortAscending(new FieldComparator("frqDesc",	true));
		this.listheader_FrqDesc.setSortDescending(new FieldComparator("frqDesc", false));
		this.listheader_FrqIsActive.setSortAscending(new FieldComparator("frqIsActive", true));
		this.listheader_FrqIsActive.setSortDescending(new FieldComparator("frqIsActive", false));

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
		this.searchObj = new JdbcSearchObject<Frequency>(Frequency.class, getListRows());
		this.searchObj.addSort("FrqCode", false);

		// Work flow
		if (isWorkFlowEnabled()) {
			this.searchObj.addTabelName("BMTFrequencies_View");
			if (isFirstTask()) {
				button_FrequencyList_NewFrequency.setVisible(true);
			} else {
				button_FrequencyList_NewFrequency.setVisible(false);
			}
			this.searchObj.addFilterIn("nextRoleCode", getUserWorkspace().getUserRoles(), isFirstTask());
		} else {
			this.searchObj.addTabelName("BMTFrequencies_AView");
		}

		setSearchObj(this.searchObj);
		if (!isWorkFlowEnabled() && wfAvailable) {
			this.button_FrequencyList_NewFrequency.setVisible(false);
			this.button_FrequencyList_FrequencySearchDialog.setVisible(false);
			this.button_FrequencyList_PrintList.setVisible(false);
			PTMessageUtils.showErrorMessage(PennantJavaUtil.getLabel("WORKFLOW CONFIG NOT FOUND"));
		} else {
			// Set the ListModel for the articles.
			getPagedListWrapper().init(this.searchObj, this.listBoxFrequency, this.pagingFrequencyList);
			// set the itemRenderer
			this.listBoxFrequency.setItemRenderer(new FrequencyListModelItemRenderer());
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * SetVisible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug("Entering");

		getUserWorkspace().alocateAuthorities("FrequencyList");
		this.button_FrequencyList_NewFrequency.setVisible(getUserWorkspace()
				.isAllowed("button_FrequencyList_NewFrequency"));
		this.button_FrequencyList_FrequencySearchDialog.setVisible(getUserWorkspace()
				.isAllowed("button_FrequencyList_FrequencyFindDialog"));
		this.button_FrequencyList_PrintList.setVisible(getUserWorkspace()
				.isAllowed("button_FrequencyList_PrintList"));
		logger.debug("Leaving");
	}

	/**
	 * This method is forwarded from the list boxes item renderer. <br>
	 * see: com.pennant.webui.StaticParms.frequency.model.
	 * FrequencyListModelItemRenderer.java <br>
	 * 
	 * @param event
	 * @throws Exception
	 */

	public void onFrequencyItemDoubleClicked(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		// get the selected Frequency object
		final Listitem item = this.listBoxFrequency.getSelectedItem();

		if (item != null) {
			// CAST AND STORE THE SELECTED OBJECT
			final Frequency aFrequency = (Frequency) item.getAttribute("data");
			final Frequency frequency = getFrequencyService().getFrequencyById(aFrequency.getId());

			if (frequency == null) {

				String[] valueParm = new String[2];
				String[] errParm = new String[2];

				valueParm[0] = aFrequency.getFrqCode();
				errParm[0] = PennantJavaUtil.getLabel("label_Frq_Code") + ":" + valueParm[0];

				ErrorDetails errorDetails = ErrorUtil.getErrorDetail(
						new ErrorDetails(PennantConstants.KEY_FIELD, "41005", errParm, valueParm), getUserWorkspace().getUserLanguage());
				PTMessageUtils.showErrorMessage(errorDetails.getErrorMessage());
			} else {
				String whereCond = " AND FrqCode='" + frequency.getFrqCode()
				+ "' AND version=" + frequency.getVersion() + " ";

				if (isWorkFlowEnabled()) {
					boolean userAcces = validateUserAccess(workFlowDetails.getId(), getUserWorkspace().getLoginUserDetails().getLoginUsrID(),
							"Frequency", whereCond, frequency.getTaskId(), frequency.getNextTaskId());
					if (userAcces) {
						showDetailView(frequency);
					} else {
						PTMessageUtils.showErrorMessage(Labels.getLabel("RECORD_NOTALLOWED"));
					}
				} else {
					showDetailView(frequency);
				}
			}
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Call the Frequency dialog with a new empty entry. <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClick$button_FrequencyList_NewFrequency(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		// create a new Frequency object, We GET it from the back end.
		final Frequency aFrequency = getFrequencyService().getNewFrequency();
		showDetailView(aFrequency);
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Opens the detail view. <br>
	 * Over handed some parameters in a map if needed. <br>
	 * 
	 * @param Frequency
	 *            (aFrequency)
	 * @throws Exception
	 */
	private void showDetailView(Frequency aFrequency) throws Exception {
		logger.debug("Entering");

		/*
		 * We can call our Dialog ZUL-file with parameters. So we can call them
		 * with a object of the selected item. For handed over these parameter
		 * only a Map is accepted. So we put the object in a HashMap.
		 */
		if (aFrequency.getWorkflowId() == 0 && isWorkFlowEnabled()) {
			aFrequency.setWorkflowId(workFlowDetails.getWorkFlowId());
		}

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("frequency", aFrequency);
		/*
		 * we can additionally handed over the listBox or the controller self,
		 * so we have in the dialog access to the list box List model. This is
		 * fine for synchronizing the data in the FrequencyListbox from the
		 * dialog when we do a delete, edit or insert a Frequency.
		 */
		map.put("frequencyListCtrl", this);

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents(
					"/WEB-INF/pages/StaticParms/Frequency/FrequencyDialog.zul", null, map);
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
		PTMessageUtils.showHelpWindow(event, window_FrequencyList);
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
		this.pagingFrequencyList.setActivePage(0);
		Events.postEvent("onCreate", this.window_FrequencyList, event);
		this.window_FrequencyList.invalidate();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * call the Frequency dialog
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClick$button_FrequencyList_FrequencySearchDialog(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		/*
		 * we can call our FrequencyDialog ZUL-file with parameters. So we can
		 * call them with a object of the selected Frequency. For handed over
		 * these parameter only a Map is accepted. So we put the Frequency
		 * object in a HashMap.
		 */
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("frequencyCtrl", this);
		map.put("searchObject", this.searchObj);

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents(
					"/WEB-INF/pages/StaticParms/Frequency/FrequencySearchDialog.zul", null, map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / " + e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * When the frequency print button is clicked.
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$button_FrequencyList_PrintList(Event event)	throws InterruptedException {
		logger.debug("Entering" + event.toString());
		PTReportUtils.getReport("Frequency", getSearchObj());
		logger.debug("Leaving" + event.toString());
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public void setFrequencyService(FrequencyService frequencyService) {
		this.frequencyService = frequencyService;
	}
	public FrequencyService getFrequencyService() {
		return this.frequencyService;
	}

	public JdbcSearchObject<Frequency> getSearchObj() {
		return this.searchObj;
	}
	public void setSearchObj(JdbcSearchObject<Frequency> searchObj) {
		this.searchObj = searchObj;
	}
}