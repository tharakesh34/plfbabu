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
 * FileName    		:  ProfessionListCtrl.java                                                   * 	  
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

package com.pennant.webui.systemmasters.profession;

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
import com.pennant.backend.model.systemmasters.Profession;
import com.pennant.backend.service.systemmasters.ProfessionService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.search.Filter;
import com.pennant.webui.systemmasters.profession.model.ProfessionListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.PTListReportUtils;
import com.pennant.webui.util.PTMessageUtils;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * /WEB-INF/pages/SystemMasters/Profession/ProfessionList.zul file.<br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * 
 */
public class ProfessionListCtrl extends GFCBaseListCtrl<Profession> implements Serializable {

	private static final long serialVersionUID = 269967917185319880L;
	private final static Logger logger = Logger.getLogger(ProfessionListCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by
	 * our 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window 		window_ProfessionList; 			// autoWired
	protected Panel 		panel_ProfessionList; 			// autoWired
	protected Borderlayout 	borderLayout_ProfessionList;	// autoWired
	protected Paging 		pagingProfessionList; 			// autoWired
	protected Listbox 		listBoxProfession; 				// autoWired

	// List headers
	protected Listheader 	listheader_ProfessionCode; 		// autoWired
	protected Listheader 	listheader_ProfessionDesc; 		// autoWired
	protected Listheader 	listheader_ProfessionIsActive; 	// autoWired
	protected Listheader 	listheader_RecordStatus; 		// autoWired
	protected Listheader 	listheader_RecordType;

	// checkRights
	protected Button 		btnHelp; 										// autoWired
	protected Button 		button_ProfessionList_NewProfession; 			// autoWired
	protected Button 		button_ProfessionList_ProfessionSearchDialog;	// autoWired
	protected Button 		button_ProfessionList_PrintList; 				// autoWired

	// NEEDED for the ReUse in the SearchWindow
	protected JdbcSearchObject<Profession> searchObj;

	private transient ProfessionService professionService;
	private transient WorkFlowDetails   workFlowDetails = null;

	/**
	 * default constructor.<br>
	 */
	public ProfessionListCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the List window we check, if the
	 * ZUL-file is called with a parameter for a selected Profession object in a
	 * Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_ProfessionList(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		ModuleMapping moduleMapping = PennantJavaUtil.getModuleMap("Profession");
		boolean wfAvailable = true;

		if (moduleMapping.getWorkflowType() != null) {
			workFlowDetails = WorkFlowUtil.getWorkFlowDetails("Profession");

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
		this.borderLayout_ProfessionList.setHeight(getBorderLayoutHeight());

		// set the paging parameters
		this.pagingProfessionList.setPageSize(getListRows());
		this.pagingProfessionList.setDetailed(true);

		this.listheader_ProfessionCode.setSortAscending(new FieldComparator("professionCode", true));
		this.listheader_ProfessionCode.setSortDescending(new FieldComparator("professionCode", false));
		this.listheader_ProfessionDesc.setSortAscending(new FieldComparator("professionDesc", true));
		this.listheader_ProfessionDesc.setSortDescending(new FieldComparator("professionDesc", false));
		this.listheader_ProfessionIsActive.setSortAscending(new FieldComparator("professionIsActive", true));
		this.listheader_ProfessionIsActive.setSortDescending(new FieldComparator("professionIsActive", false));

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
		this.searchObj = new JdbcSearchObject<Profession>(Profession.class, getListRows());
		this.searchObj.addSort("ProfessionCode", false);
		this.searchObj.addFilter(new Filter("ProfessionCode",PennantConstants.NONE, Filter.OP_NOT_EQUAL));

		// Work flow
		if (isWorkFlowEnabled()) {
			this.searchObj.addTabelName("BMTProfessions_View");
			if (isFirstTask()) {
				button_ProfessionList_NewProfession.setVisible(true);
			} else {
				button_ProfessionList_NewProfession.setVisible(false);
			}
			this.searchObj.addFilterIn("nextRoleCode", getUserWorkspace().getUserRoles(), isFirstTask());
		} else {
			this.searchObj.addTabelName("BMTProfessions_AView");
		}

		setSearchObj(this.searchObj);
		if (!isWorkFlowEnabled() && wfAvailable) {
			this.button_ProfessionList_NewProfession.setVisible(false);
			this.button_ProfessionList_ProfessionSearchDialog.setVisible(false);
			this.button_ProfessionList_PrintList.setVisible(false);
			PTMessageUtils.showErrorMessage(PennantJavaUtil.getLabel("WORKFLOW CONFIG NOT FOUND"));
		} else {
			// Set the ListModel for the articles.
			getPagedListWrapper().init(this.searchObj, this.listBoxProfession, this.pagingProfessionList);
			// set the itemRenderer
			this.listBoxProfession.setItemRenderer(new ProfessionListModelItemRenderer());
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * SetVisible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug("Entering");

		getUserWorkspace().alocateAuthorities("ProfessionList");
		this.button_ProfessionList_NewProfession.setVisible(getUserWorkspace()
				.isAllowed("button_ProfessionList_NewProfession"));
		this.button_ProfessionList_ProfessionSearchDialog.setVisible(getUserWorkspace()
				.isAllowed("button_ProfessionList_ProfessionFindDialog"));
		this.button_ProfessionList_PrintList.setVisible(getUserWorkspace()
				.isAllowed("button_ProfessionList_PrintList"));
		logger.debug("Leaving");
	}

	/**
	 * This method is forwarded from the list boxes item renderer. <br>
	 * see: com.pennant.webui.SystemMasters.profession.model.
	 * ProfessionListModelItemRenderer.java <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onProfessionItemDoubleClicked(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		// get the selected Profession object
		final Listitem item = this.listBoxProfession.getSelectedItem();

		if (item != null) {
			// CAST AND STORE THE SELECTED OBJECT
			final Profession aProfession = (Profession) item.getAttribute("data");
			final Profession profession = getProfessionService().getProfessionById(aProfession.getId());

			if (profession == null) {

				String[] valueParm = new String[2];
				String[] errorParm = new String[2];

				valueParm[0] = aProfession.getProfessionCode();
				errorParm[0] = PennantJavaUtil.getLabel("label_ProfessionCode") + ":" + aProfession.getProfessionCode();

				ErrorDetails errorDetails = ErrorUtil.getErrorDetail(
						new ErrorDetails(PennantConstants.KEY_FIELD, "41005",errorParm, valueParm), getUserWorkspace().getUserLanguage());
				PTMessageUtils.showErrorMessage(errorDetails.getErrorMessage());
			} else {

				String whereCond = " AND ProfessionCode='"+ profession.getProfessionCode() 
				+ "' AND version=" + profession.getVersion() + " ";

				if (isWorkFlowEnabled()) {
					boolean userAcces = validateUserAccess(workFlowDetails.getId(), getUserWorkspace().getLoginUserDetails().getLoginUsrID(),
							"Profession", whereCond, profession.getTaskId(),profession.getNextTaskId());
					if (userAcces) {
						showDetailView(profession);
					} else {
						PTMessageUtils.showErrorMessage(Labels.getLabel("RECORD_NOTALLOWED"));
					}
				} else {
					showDetailView(profession);
				}
			}
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Call the Profession dialog with a new empty entry. <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClick$button_ProfessionList_NewProfession(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		// create a new Profession object, We GET it from the back end.
		final Profession aProfession = getProfessionService().getNewProfession();
		showDetailView(aProfession);
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Opens the detail view. <br>
	 * Over handed some parameters in a map if needed. <br>
	 * 
	 * @param Profession
	 *            (aProfession)
	 * @throws Exception
	 */
	private void showDetailView(Profession aProfession) throws Exception {
		logger.debug("Entering");

		/*
		 * We can call our Dialog ZUL-file with parameters. So we can call them
		 * with a object of the selected item. For handed over these parameter
		 * only a Map is accepted. So we put the object in a HashMap.
		 */

		if (aProfession.getWorkflowId() == 0 && isWorkFlowEnabled()) {
			aProfession.setWorkflowId(workFlowDetails.getWorkFlowId());
		}
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("profession", aProfession);
		/*
		 * we can additionally handed over the listBox or the controller self,
		 * so we have in the dialog access to the list box List model. This is
		 * fine for synchronizing the data in the ProfessionListbox from the
		 * dialog when we do a delete, edit or insert a Profession.
		 */
		map.put("professionListCtrl", this);

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents(
					"/WEB-INF/pages/SystemMaster/Profession/ProfessionDialog.zul", null, map);
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
		PTMessageUtils.showHelpWindow(event, window_ProfessionList);
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
		this.pagingProfessionList.setActivePage(0);
		Events.postEvent("onCreate", this.window_ProfessionList, event);
		this.window_ProfessionList.invalidate();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * call the Profession dialog
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClick$button_ProfessionList_ProfessionSearchDialog(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		/*
		 * we can call our ProfessionDialog ZUL-file with parameters. So we can
		 * call them with a object of the selected Profession. For handed over
		 * these parameter only a Map is accepted. So we put the Profession
		 * object in a HashMap.
		 */
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("professionCtrl", this);
		map.put("searchObject", this.searchObj);

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents(
					"/WEB-INF/pages/SystemMaster/Profession/ProfessionSearchDialog.zul", null, map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / " + e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * When the profession print button is clicked.
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	@SuppressWarnings("unused")
	public void onClick$button_ProfessionList_PrintList(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		PTListReportUtils reportUtils = new PTListReportUtils("Profession", getSearchObj(),this.pagingProfessionList.getTotalSize()+1);
		logger.debug("Leaving" + event.toString());
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public void setProfessionService(ProfessionService professionService) {
		this.professionService = professionService;
	}
	public ProfessionService getProfessionService() {
		return this.professionService;
	}

	public JdbcSearchObject<Profession> getSearchObj() {
		return this.searchObj;
	}
	public void setSearchObj(JdbcSearchObject<Profession> searchObj) {
		this.searchObj = searchObj;
	}

}