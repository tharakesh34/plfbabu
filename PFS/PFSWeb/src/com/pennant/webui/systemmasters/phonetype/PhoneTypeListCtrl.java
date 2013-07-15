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
 * FileName    		:  PhoneTypeListCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  06-05-2011    														*
 *                                                                  						*
 * Modified Date    :  06-05-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 06-05-2011       Pennant	                 0.1                                            * 
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

package com.pennant.webui.systemmasters.phonetype;

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
import com.pennant.backend.model.systemmasters.PhoneType;
import com.pennant.backend.service.systemmasters.PhoneTypeService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.webui.systemmasters.phonetype.model.PhoneTypeListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.PTListReportUtils;
import com.pennant.webui.util.PTMessageUtils;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * /WEB-INF/pages/SystemMaster/PhoneType/PhoneTypeList.zul file.<br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * 
 */
public class PhoneTypeListCtrl extends GFCBaseListCtrl<PhoneType> implements Serializable {

	private static final long serialVersionUID = 5068208109885923909L;
	private final static Logger logger = Logger.getLogger(PhoneTypeListCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by
	 * our 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window 		window_PhoneTypeList; 				// autoWired
	protected Panel 		panel_PhoneTypeList; 				// autoWired
	protected Borderlayout 	borderLayout_PhoneTypeList; 		// autoWired
	protected Paging 		pagingPhoneTypeList; 				// autoWired
	protected Listbox 		listBoxPhoneType; 					// autoWired

	// List headers
	protected Listheader listheader_PhoneTypeCode; 				// autoWired
	protected Listheader listheader_PhoneTypeDesc; 				// autoWired
	protected Listheader listheader_PhoneTypePriority; 			// autoWired
	protected Listheader listheader_PhoneTypeIsActive; 			// autoWired
	protected Listheader listheader_RecordStatus; 				// autoWired
	protected Listheader listheader_RecordType;

	// checkRights
	protected Button btnHelp; 										// autoWired
	protected Button button_PhoneTypeList_NewPhoneType; 			// autoWired
	protected Button button_PhoneTypeList_PhoneTypeSearchDialog; 	// autoWired
	protected Button button_PhoneTypeList_PrintList; 				// autoWired

	// NEEDED for the ReUse in the SearchWindow
	protected JdbcSearchObject<PhoneType> searchObj;

	private transient PhoneTypeService phoneTypeService;
	private transient WorkFlowDetails  workFlowDetails = null;

	/**
	 * default constructor.<br>
	 */
	public PhoneTypeListCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the List window we check, if the
	 * ZUL-file is called with a parameter for a selected PhoneType object in a
	 * Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_PhoneTypeList(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		ModuleMapping moduleMapping = PennantJavaUtil.getModuleMap("PhoneType");
		boolean wfAvailable = true;

		if (moduleMapping.getWorkflowType() != null) {
			workFlowDetails = WorkFlowUtil.getWorkFlowDetails("PhoneType");

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

		this.borderLayout_PhoneTypeList.setHeight(getBorderLayoutHeight());

		// set the paging parameters
		this.pagingPhoneTypeList.setPageSize(getListRows());
		this.pagingPhoneTypeList.setDetailed(true);

		this.listheader_PhoneTypeCode.setSortAscending(new FieldComparator("phoneTypeCode", true));
		this.listheader_PhoneTypeCode.setSortDescending(new FieldComparator("phoneTypeCode", false));
		this.listheader_PhoneTypeDesc.setSortAscending(new FieldComparator("phoneTypeDesc", true));
		this.listheader_PhoneTypeDesc.setSortDescending(new FieldComparator("phoneTypeDesc", false));
		this.listheader_PhoneTypePriority.setSortAscending(new FieldComparator("phoneTypePriority", true));
		this.listheader_PhoneTypePriority.setSortDescending(new FieldComparator("phoneTypePriority",false));
		this.listheader_PhoneTypeIsActive.setSortAscending(new FieldComparator("phoneTypeIsActive", true));
		this.listheader_PhoneTypeIsActive.setSortDescending(new FieldComparator("phoneTypeIsActive",false));

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
		this.searchObj = new JdbcSearchObject<PhoneType>(PhoneType.class,getListRows());
		this.searchObj.addSort("PhoneTypeCode", false);

		// Work flow
		if (isWorkFlowEnabled()) {
			this.searchObj.addTabelName("BMTPhoneTypes_View");
			if (isFirstTask()) {
				button_PhoneTypeList_NewPhoneType.setVisible(true);
			} else {
				button_PhoneTypeList_NewPhoneType.setVisible(false);
			}
			this.searchObj.addFilterIn("nextRoleCode", getUserWorkspace().getUserRoles(), isFirstTask());
		} else {
			this.searchObj.addTabelName("BMTPhoneTypes_AView");
		}

		setSearchObj(this.searchObj);
		if (!isWorkFlowEnabled() && wfAvailable) {
			this.button_PhoneTypeList_NewPhoneType.setVisible(false);
			this.button_PhoneTypeList_PhoneTypeSearchDialog.setVisible(false);
			this.button_PhoneTypeList_PrintList.setVisible(false);
			PTMessageUtils.showErrorMessage(PennantJavaUtil.getLabel("WORKFLOW CONFIG NOT FOUND"));
		} else {
			// Set the ListModel for the articles.
			getPagedListWrapper().init(this.searchObj, this.listBoxPhoneType,this.pagingPhoneTypeList);
			// set the itemRenderer
			this.listBoxPhoneType.setItemRenderer(new PhoneTypeListModelItemRenderer());
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * SetVisible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug("Entering");
		getUserWorkspace().alocateAuthorities("PhoneTypeList");
		this.button_PhoneTypeList_NewPhoneType.setVisible(getUserWorkspace()
				.isAllowed("button_PhoneTypeList_NewPhoneType"));
		this.button_PhoneTypeList_PhoneTypeSearchDialog.setVisible(getUserWorkspace()
				.isAllowed("button_PhoneTypeList_PhoneTypeFindDialog"));
		this.button_PhoneTypeList_PrintList.setVisible(getUserWorkspace()
				.isAllowed("button_PhoneTypeList_PrintList"));
		logger.debug("Leaving");
	}

	/**
	 * This method is forwarded from the list boxes item renderer. <br>
	 * see: com.pennant.webui.bmtmasters.phonetype.model.
	 * PhoneTypeListModelItemRenderer.java <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onPhoneTypeItemDoubleClicked(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		// get the selected PhoneType object
		final Listitem item = this.listBoxPhoneType.getSelectedItem();

		if (item != null) {
			// CAST AND STORE THE SELECTED OBJECT
			final PhoneType aPhoneType = (PhoneType) item.getAttribute("data");
			final PhoneType phoneType = getPhoneTypeService().getPhoneTypeById(aPhoneType.getId());

			if (phoneType == null) {

				String[] valueParm = new String[2];
				String[] errorParm = new String[2];

				valueParm[0] = aPhoneType.getPhoneTypeCode();
				errorParm[0] = PennantJavaUtil.getLabel("label_PhoneType_Code")+ ":" + aPhoneType.getPhoneTypeCode();

				ErrorDetails errorDetails = ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41005",
						errorParm, valueParm), getUserWorkspace().getUserLanguage());
				PTMessageUtils.showErrorMessage(errorDetails.getErrorMessage());
			} else {
				String whereCond = " AND PhoneTypeCode='"
					+ phoneType.getPhoneTypeCode() + "' AND version="+ phoneType.getVersion() + " ";

				if (isWorkFlowEnabled()) {
					boolean userAcces = validateUserAccess(
							workFlowDetails.getId(), getUserWorkspace().getLoginUserDetails().getLoginUsrID(),
							"PhoneType", whereCond, phoneType.getTaskId(),phoneType.getNextTaskId());
					if (userAcces) {
						showDetailView(phoneType);
					} else {
						PTMessageUtils.showErrorMessage(Labels.getLabel("RECORD_NOTALLOWED"));
					}
				} else {
					showDetailView(phoneType);
				}
			}
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Call the PhoneType dialog with a new empty entry. <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClick$button_PhoneTypeList_NewPhoneType(Event event)throws Exception {
		logger.debug("Entering" + event.toString());
		// create a new PhoneType object, We GET it from the back end.
		final PhoneType aPhoneType = getPhoneTypeService().getNewPhoneType();
		aPhoneType.setPhoneTypePriority(0); // Initialization
		showDetailView(aPhoneType);
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Opens the detail view. <br>
	 * Over handed some parameters in a map if needed. <br>
	 * 
	 * @param PhoneType
	 *            (aPhoneType)
	 * @throws Exception
	 */
	private void showDetailView(PhoneType aPhoneType) throws Exception {
		logger.debug("Entering");

		/*
		 * We can call our Dialog ZUL-file with parameters. So we can call them
		 * with a object of the selected item. For handed over these parameter
		 * only a Map is accepted. So we put the object in a HashMap.
		 */

		if (aPhoneType.getWorkflowId() == 0 && isWorkFlowEnabled()) {
			aPhoneType.setWorkflowId(workFlowDetails.getWorkFlowId());
		}

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("phoneType", aPhoneType);
		/*
		 * we can additionally handed over the listBox or the controller self,
		 * so we have in the dialog access to the list box List model. This is
		 * fine for synchronizing the data in the PhoneTypeListbox from the
		 * dialog when we do a delete, edit or insert a PhoneType.
		 */
		map.put("phoneTypeListCtrl", this);

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents(
					"/WEB-INF/pages/SystemMaster/PhoneType/PhoneTypeDialog.zul",null, map);
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
		PTMessageUtils.showHelpWindow(event, window_PhoneTypeList);
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
		this.pagingPhoneTypeList.setActivePage(0);
		Events.postEvent("onCreate", this.window_PhoneTypeList, event);
		this.window_PhoneTypeList.invalidate();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * call the PhoneType dialog
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClick$button_PhoneTypeList_PhoneTypeSearchDialog(Event event)throws Exception {
		logger.debug("Entering" + event.toString());
		/*
		 * we can call our PhoneTypeDialog ZUL-file with parameters. So we can
		 * call them with a object of the selected PhoneType. For handed over
		 * these parameter only a Map is accepted. So we put the PhoneType
		 * object in a HashMap.
		 */
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("phoneTypeCtrl", this);
		map.put("searchObject", this.searchObj);

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents(
					"/WEB-INF/pages/SystemMaster/PhoneType/PhoneTypeSearchDialog.zul",null, map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / "+ e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * When the phoneType print button is clicked.
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	@SuppressWarnings("unused")
	public void onClick$button_PhoneTypeList_PrintList(Event event)throws InterruptedException {
		logger.debug("Entering" + event.toString());
		PTListReportUtils reportUtils = new PTListReportUtils("PhoneType", getSearchObj(),this.pagingPhoneTypeList.getTotalSize()+1);
		logger.debug("Leaving" + event.toString());
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public void setPhoneTypeService(PhoneTypeService phoneTypeService) {
		this.phoneTypeService = phoneTypeService;
	}
	public PhoneTypeService getPhoneTypeService() {
		return this.phoneTypeService;
	}

	public JdbcSearchObject<PhoneType> getSearchObj() {
		return this.searchObj;
	}
	public void setSearchObj(JdbcSearchObject<PhoneType> searchObj) {
		this.searchObj = searchObj;
	}

}