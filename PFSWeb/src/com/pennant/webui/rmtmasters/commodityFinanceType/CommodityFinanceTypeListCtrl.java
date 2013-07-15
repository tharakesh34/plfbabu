/**
Copyright 2011 - Pennant Technologies
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
 * FileName    		:  FinanceTypeListCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  30-06-2011    														*
 *                                                                  						*
 * Modified Date    :  30-06-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 30-06-2011       Pennant	                 0.1                                            * 
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

package com.pennant.webui.rmtmasters.commodityFinanceType;

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
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.service.rmtmasters.FinanceTypeService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.search.Filter;
import com.pennant.webui.rmtmasters.commodityFinanceType.model.CommodityFinanceTypeListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.PTListReportUtils;
import com.pennant.webui.util.PTMessageUtils;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * /WEB-INF/pages/SolutionFactory/CommodityFinanceType/commodityFinanceTypeList.zul file.<br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * 
 */
public class CommodityFinanceTypeListCtrl extends GFCBaseListCtrl<FinanceType> implements Serializable {

	private static final long serialVersionUID = -1491703348215991538L;
	private final static Logger logger = Logger.getLogger(CommodityFinanceTypeListCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window 		window_CommodityFinanceTypeList; 		// autoWired
	protected Borderlayout	borderLayout_CommodityFinanceTypeList; 	// autoWired
	protected Paging 		pagingFinanceTypeList; 					// autoWired
	protected Listbox 		listBoxFinanceType; 					// autoWired

	// List headers
	protected Listheader listheader_FinType; 				// autoWired
	protected Listheader listheader_FinTypeDesc; 			// autoWired
	protected Listheader listheader_FinCcy; 				// autoWired
	protected Listheader listheader_FinBasicType; 			// autoWired
	protected Listheader listheader_FinAcType; 				// autoWired
	protected Listheader listheader_RecordStatus; 			// autoWired
	protected Listheader listheader_RecordType;

	// checkRights
	protected Button btnHelp; 															// autoWired
	protected Button button_CommodityFinanceTypeList_NewCommodityFinanceType; 			// autoWired
	protected Button button_CommodityFinanceTypeList_CommodityFinanceTypeSearchDialog; 	// autoWired
	protected Button button_CommodityFinanceTypeList_PrintList; 						// autoWired

	// NEEDED for the ReUse in the SearchWindow
	protected JdbcSearchObject<FinanceType> searchObj;

	private transient FinanceTypeService financeTypeService;
	private transient WorkFlowDetails workFlowDetails = null;

	/**
	 * default constructor.<br>
	 */
	public CommodityFinanceTypeListCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the List window we check, if the
	 * ZUL-file is called with a parameter for a selected FinanceType object in
	 * a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_CommodityFinanceTypeList(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		ModuleMapping moduleMapping = PennantJavaUtil.getModuleMap("CommodityFinanceType");
		boolean wfAvailable = true;

		if (moduleMapping.getWorkflowType() != null) {
			workFlowDetails = WorkFlowUtil.getWorkFlowDetails("CommodityFinanceType");

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
		this.borderLayout_CommodityFinanceTypeList.setHeight(getBorderLayoutHeight());

		// set the paging parameters
		this.pagingFinanceTypeList.setPageSize(getListRows());
		this.pagingFinanceTypeList.setDetailed(true);

		this.listheader_FinType.setSortAscending(new FieldComparator("finType", true));
		this.listheader_FinType.setSortDescending(new FieldComparator("finType", false));
		this.listheader_FinTypeDesc.setSortAscending(new FieldComparator("finTypeDesc", true));
		this.listheader_FinTypeDesc.setSortDescending(new FieldComparator("finTypeDesc", false));
		this.listheader_FinCcy.setSortAscending(new FieldComparator("finCcy", true));
		this.listheader_FinCcy.setSortDescending(new FieldComparator("finCcy", false));
		this.listheader_FinBasicType.setSortAscending(new FieldComparator("finDaysCalType", true));
		this.listheader_FinBasicType.setSortDescending(new FieldComparator("finDaysCalType", false));
		this.listheader_FinAcType.setSortAscending(new FieldComparator("finAcType", true));
		this.listheader_FinAcType.setSortDescending(new FieldComparator("finAcType", false));

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
		this.searchObj = new JdbcSearchObject<FinanceType>(FinanceType.class, getListRows());
		this.searchObj.addSort("FinType", false);
		this.searchObj.addFilter(new Filter("FinCategory", "CF", Filter.OP_EQUAL));
		// WorkFlow
		if (isWorkFlowEnabled()) {
			this.searchObj.addTabelName("RMTFinanceTypes_View");
			if (isFirstTask()) {
				button_CommodityFinanceTypeList_NewCommodityFinanceType.setVisible(true);
			} else {
				button_CommodityFinanceTypeList_NewCommodityFinanceType.setVisible(false);
			}

			this.searchObj.addFilterIn("nextRoleCode", getUserWorkspace().getUserRoles(), isFirstTask());
		}else{
			this.searchObj.addTabelName("RMTFinanceTypes_AView");
		}

		setSearchObj(this.searchObj);
		if (!isWorkFlowEnabled() && wfAvailable) {
			this.button_CommodityFinanceTypeList_NewCommodityFinanceType.setVisible(false);
			this.button_CommodityFinanceTypeList_CommodityFinanceTypeSearchDialog.setVisible(false);
			this.button_CommodityFinanceTypeList_PrintList.setVisible(false);
			PTMessageUtils.showErrorMessage(PennantJavaUtil.getLabel("WORKFLOW CONFIG NOT FOUND"));
		} else {
			// Set the ListModel for the articles.
			getPagedListWrapper().init(this.searchObj, this.listBoxFinanceType, this.pagingFinanceTypeList);
			// set the itemRenderer
			this.listBoxFinanceType.setItemRenderer(new CommodityFinanceTypeListModelItemRenderer());
			
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * SetVisible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug("Entering");
		getUserWorkspace().alocateAuthorities("FinanceTypeList");

		this.button_CommodityFinanceTypeList_NewCommodityFinanceType.setVisible(getUserWorkspace()
				.isAllowed("button_CommodityFinanceTypeList_NewCommodityFinanceType"));
		this.button_CommodityFinanceTypeList_CommodityFinanceTypeSearchDialog.setVisible(getUserWorkspace()
				.isAllowed("button_CommodityFinanceTypeList_CommodityFinanceTypeFindDialog"));
		this.button_CommodityFinanceTypeList_PrintList.setVisible(getUserWorkspace()
				.isAllowed("button_CommodityFinanceTypeList_PrintList"));
		logger.debug("Leaving");
	}

	/**
	 * This method is forwarded from the ListBoxes item renderer. <br>
	 * see: com.pennant.webui.rmtmasters.financetype.model.
	 * FinanceTypeListModelItemRenderer.java <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCommodityFinanceTypeItemDoubleClicked(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		// get the selected FinanceType object
		final Listitem item = this.listBoxFinanceType.getSelectedItem();

		if (item != null) {
			// CAST AND STORE THE SELECTED OBJECT
			final FinanceType aFinanceType = (FinanceType) item.getAttribute("data");
			final FinanceType financeType = getFinanceTypeService().getFinanceTypeById(aFinanceType.getId());
			if(financeType==null){
				
				String[] valueParm = new String[2];
				String[] errParm= new String[2];				
				valueParm[0] =	aFinanceType.getFinType();		

				errParm[0] = PennantJavaUtil.getLabel("label_FinType") + ":"+ valueParm[0];

				ErrorDetails errorDetails = ErrorUtil.getErrorDetail
						(new ErrorDetails(PennantConstants.KEY_FIELD,"41005", errParm,valueParm), getUserWorkspace().getUserLanguage());
				PTMessageUtils.showErrorMessage(errorDetails.getErrorMessage());
				
			}else{

			String whereCond = " AND FinType='" + financeType.getFinType()
								+ "' AND version=" + financeType.getVersion() + " ";

			if (isWorkFlowEnabled()) {
				boolean userAcces = validateUserAccess(workFlowDetails.getId(),getUserWorkspace().getLoginUserDetails().getLoginUsrID(),
						"FinanceType", whereCond, financeType.getTaskId(), financeType.getNextTaskId());
				if (userAcces) {
					showDetailView(financeType);
				} else {
					PTMessageUtils.showErrorMessage(Labels.getLabel("RECORD_NOTALLOWED"));
				}
			} else {
				showDetailView(financeType);
			}
			}
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Call the FinanceType dialog with a new empty entry. <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClick$button_CommodityFinanceTypeList_NewCommodityFinanceType(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		
		// create a new FinanceType object, We GET it from the backEnd.
		final FinanceType aFinanceType = getFinanceTypeService().getNewCommodityFinanceType();
		showDetailView(aFinanceType);
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Opens the detail view. <br>
	 * OverHanded some params in a map if needed. <br>
	 * 
	 * @param FinanceType
	 *            (aFinanceType)
	 * @throws Exception
	 */
	private void showDetailView(FinanceType aFinanceType) throws Exception {
		logger.debug("Entering");
		/*
		 * We can call our Dialog ZUL-file with parameters. So we can call them
		 * with a object of the selected item. For handed over these parameter
		 * only a Map is accepted. So we put the object in a HashMap.
		 */
		if (aFinanceType.getWorkflowId() == 0 && isWorkFlowEnabled()) {
			aFinanceType.setWorkflowId(workFlowDetails.getWorkFlowId());
		}

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("financeType", aFinanceType);
		/*
		 * we can additionally handed over the listBox or the controller self,
		 * so we have in the dialog access to the listBox ListModel. This is
		 * fine for synchronizing the data in the FinanceTypeListbox from the
		 * dialog when we do a delete, edit or insert a FinanceType.
		 */
		map.put("commodityFinanceTypeListCtrl", this);

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents(
					"/WEB-INF/pages/SolutionFactory/CommodityFinanceType/CommodityFinanceTypeDialog.zul", null, map);
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
		PTMessageUtils.showHelpWindow(event, window_CommodityFinanceTypeList);
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
		this.pagingFinanceTypeList.setActivePage(0);
		Events.postEvent("onCreate", this.window_CommodityFinanceTypeList, event);
		this.window_CommodityFinanceTypeList.invalidate();
	
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * call the FinanceType dialog
	 * 
	 * @param event
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public void onClick$button_CommodityFinanceTypeList_CommodityFinanceTypeSearchDialog(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		/*
		 * we can call our FinanceTypeDialog ZUL-file with parameters. So we can
		 * call them with a object of the selected FinanceType. For handed over
		 * these parameter only a Map is accepted. So we put the FinanceType
		 * object in a HashMap.
		 */
		@SuppressWarnings("rawtypes")
		final HashMap map = new HashMap();
		map.put("financeTypeList", this);
		map.put("searchObject", this.searchObj);
		map.put("listBoxFinanceType", this.listBoxFinanceType);
		map.put("pagingFinanceTypeList", this.pagingFinanceTypeList);
		
		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents(
					"/WEB-INF/pages/SolutionFactory/FinanceType/FinanceTypeSearchDialog.zul", null, map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / " + e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * When the financeType print button is clicked.
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	@SuppressWarnings("unused")
	public void onClick$button_CommodityFinanceTypeList_PrintList(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		PTListReportUtils reportUtils = new PTListReportUtils("FinanceType", getSearchObj(),this.pagingFinanceTypeList.getTotalSize()+1);
		logger.debug("Leaving" + event.toString());
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public void setFinanceTypeService(FinanceTypeService financeTypeService) {
		this.financeTypeService = financeTypeService;
	}
	public FinanceTypeService getFinanceTypeService() {
		return this.financeTypeService;
	}

	public JdbcSearchObject<FinanceType> getSearchObj() {
		return this.searchObj;
	}
	public void setSearchObj(JdbcSearchObject<FinanceType> searchObj) {
		this.searchObj = searchObj;
	}

}