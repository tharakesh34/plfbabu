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

package com.pennant.webui.rmtmasters.financetype;

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
import org.zkoss.zul.GroupsModelArray;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.ModuleMapping;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.service.PagedListService;
import com.pennant.backend.service.rmtmasters.FinanceTypeService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.search.Filter;
import com.pennant.search.SearchResult;
import com.pennant.webui.rmtmasters.financetype.model.FinanceTypeComparator;
import com.pennant.webui.rmtmasters.financetype.model.FinanceTypeListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.PTListReportUtils;
import com.pennant.webui.util.PTMessageUtils;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the /WEB-INF/pages/SolutionFactory/FinanceType/FinanceTypeList.zul file.<br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * 
 */
public class FinanceTypeListCtrl extends GFCBaseListCtrl<FinanceType> implements Serializable {

	private static final long serialVersionUID = -1491703348215991538L;
	private final static Logger logger = Logger.getLogger(FinanceTypeListCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ All the components that are defined here
	 * and have a corresponding component with the same 'id' in the ZUL-file are getting autoWired by our 'extends
	 * GFCBaseCtrl' GenericForwardComposer. ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window window_FinanceTypeList; // autoWired
	protected Borderlayout borderLayout_FinanceTypeList; // autoWired
	protected Paging pagingFinanceTypeList; // autoWired
	protected Listbox listBoxFinanceType; // autoWired

	// List headers
	protected Listheader listheader_FinType; // autoWired
	protected Listheader listheader_FinTypeDesc; // autoWired
	protected Listheader listheader_FinCcy; // autoWired
	protected Listheader listheader_FinBasicType; // autoWired
	protected Listheader listheader_FinAcType; // autoWired
	protected Listheader listheader_RecordStatus; // autoWired
	protected Listheader listheader_RecordType;
	
	protected Listheader listheader_ProductType; // autoWired
	protected Listheader listheader_SchdMthd; // autoWired
	protected Listheader listheader_AlwGrace; // autoWired
	protected Listheader listheader_AssetType; // autoWired
	protected Textbox finCategory;

	// checkRights
	protected Button btnHelp; // autoWired
	protected Button button_FinanceTypeList_NewFinanceType; // autoWired
	protected Button button_FinanceTypeList_FinanceTypeSearchDialog; // autoWired
	protected Button button_FinanceTypeList_PrintList; // autoWired

	// NEEDED for the ReUse in the SearchWindow
	protected JdbcSearchObject<FinanceType> searchObj;
	private transient PagedListService pagedListService;
	private transient FinanceTypeService financeTypeService;
	private transient WorkFlowDetails workFlowDetails = null;

	/**
	 * default constructor.<br>
	 */
	public FinanceTypeListCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the List window we check, if the ZUL-file is called with a parameter for a
	 * selected FinanceType object in a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_FinanceTypeList(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		ModuleMapping moduleMapping = PennantJavaUtil.getModuleMap("FinanceType");
		boolean wfAvailable = true;

		if (moduleMapping.getWorkflowType() != null) {
			workFlowDetails = WorkFlowUtil.getWorkFlowDetails("FinanceType");

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
		 * Calculate how many rows have been place in the listBox. Get the currentDesktopHeight from a hidden IntBox
		 * from the index.zul that are filled by onClientInfo() in the indexCtroller
		 */
		this.borderLayout_FinanceTypeList.setHeight(getBorderLayoutHeight());

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
		
		this.listheader_SchdMthd.setSortAscending(new FieldComparator("finSchdMthd", true));
		this.listheader_SchdMthd.setSortDescending(new FieldComparator("finSchdMthd", false));
		
		this.listheader_AlwGrace.setSortAscending(new FieldComparator("fInIsAlwGrace", true));
		this.listheader_AlwGrace.setSortDescending(new FieldComparator("fInIsAlwGrace", false));
		
		this.listheader_AssetType.setSortAscending(new FieldComparator("lovDescAssetCodeName", true));
		this.listheader_AssetType.setSortDescending(new FieldComparator("lovDescAssetCodeName", false));

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
		this.searchObj.addFilter(new Filter("FinCategory", this.finCategory.getValue(), Filter.OP_EQUAL));
		// WorkFlow
		if (isWorkFlowEnabled()) {
			this.searchObj.addTabelName("RMTFinanceTypes_View");
			if (isFirstTask()) {
				button_FinanceTypeList_NewFinanceType.setVisible(true);
			} else {
				button_FinanceTypeList_NewFinanceType.setVisible(false);
			}

			this.searchObj.addFilterIn("nextRoleCode", getUserWorkspace().getUserRoles(), isFirstTask());
		} else {
			this.searchObj.addTabelName("RMTFinanceTypes_AView");
		}

		setSearchObj(this.searchObj);
		if (!isWorkFlowEnabled() && wfAvailable) {
			this.button_FinanceTypeList_NewFinanceType.setVisible(false);
			this.button_FinanceTypeList_FinanceTypeSearchDialog.setVisible(false);
			this.button_FinanceTypeList_PrintList.setVisible(false);
			PTMessageUtils.showErrorMessage(PennantJavaUtil.getLabel("WORKFLOW CONFIG NOT FOUND"));
		} else {
			// Set the ListModel for the articles.
			findSearchObject();
			this.listBoxFinanceType.setItemRenderer(new FinanceTypeListModelItemRenderer());
		}
		logger.debug("Leaving" + event.toString());
	}
	
	/**
	 * Internal Method for Grouping List items
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void findSearchObject(){
		logger.debug("Entering");
		final SearchResult<FinanceType> searchResult = getPagedListService().getSRBySearchObject(
				this.searchObj);
		listBoxFinanceType.setModel(new GroupsModelArray(
				searchResult.getResult().toArray(),new FinanceTypeComparator()));
		logger.debug("Leaving");
	}


	/**
	 * SetVisible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug("Entering");
		getUserWorkspace().alocateAuthorities("FinanceTypeList");

		this.button_FinanceTypeList_NewFinanceType.setVisible(getUserWorkspace().isAllowed("button_FinanceTypeList_NewFinanceType"));
		this.button_FinanceTypeList_FinanceTypeSearchDialog.setVisible(getUserWorkspace().isAllowed("button_FinanceTypeList_FinanceTypeFindDialog"));
		this.button_FinanceTypeList_PrintList.setVisible(getUserWorkspace().isAllowed("button_FinanceTypeList_PrintList"));
		logger.debug("Leaving");
	}

	/**
	 * This method is forwarded from the ListBoxes item renderer. <br>
	 * see: com.pennant.webui.rmtmasters.financetype.model. FinanceTypeListModelItemRenderer.java <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onFinanceTypeItemDoubleClicked(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		// get the selected FinanceType object
		final Listitem item = this.listBoxFinanceType.getSelectedItem();

		if (item != null) {
			// CAST AND STORE THE SELECTED OBJECT
			final FinanceType aFinanceType = (FinanceType) item.getAttribute("data");
			final FinanceType financeType = getFinanceTypeService().getFinanceTypeById(aFinanceType.getId());
			if (financeType == null) {

				String[] valueParm = new String[2];
				String[] errParm = new String[2];
				valueParm[0] = aFinanceType.getFinType();

				errParm[0] = PennantJavaUtil.getLabel("label_FinType") + ":" + valueParm[0];

				ErrorDetails errorDetails = ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41005", errParm, valueParm), getUserWorkspace()
						.getUserLanguage());
				PTMessageUtils.showErrorMessage(errorDetails.getErrorMessage());

			} else {

				String whereCond = " AND FinType='" + financeType.getFinType() + "' AND version=" + financeType.getVersion() + " ";

				if (isWorkFlowEnabled()) {
					boolean userAcces = validateUserAccess(workFlowDetails.getId(), getUserWorkspace().getLoginUserDetails().getLoginUsrID(), "FinanceType", whereCond,
							financeType.getTaskId(), financeType.getNextTaskId());
					if (userAcces) {
						showDetailView(financeType,false);
					} else {
						PTMessageUtils.showErrorMessage(Labels.getLabel("RECORD_NOTALLOWED"));
					}
				} else {
					showDetailView(financeType,false);
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
	public void onClick$button_FinanceTypeList_NewFinanceType(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		// create a new FinanceType object, We GET it from the backEnd.
		final FinanceType aFinanceType = getFinanceTypeService().getNewFinanceType();
		aFinanceType.setFinScheduleOn("");
		boolean isCopyProcess = false;
		if (event.getData() != null) {
			copyDATA(aFinanceType, event.getData());
			isCopyProcess = true;
		}
		showDetailView(aFinanceType,isCopyProcess);
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
	private void showDetailView(FinanceType aFinanceType, boolean isCopyProcess) throws Exception {
		logger.debug("Entering");
		/*
		 * We can call our Dialog ZUL-file with parameters. So we can call them with a object of the selected item. For
		 * handed over these parameter only a Map is accepted. So we put the object in a HashMap.
		 */
		if (aFinanceType.getWorkflowId() == 0 && isWorkFlowEnabled()) {
			aFinanceType.setWorkflowId(workFlowDetails.getWorkFlowId());
		}

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("financeType", aFinanceType);
		map.put("isCopyProcess", isCopyProcess);
		/*
		 * we can additionally handed over the listBox or the controller self, so we have in the dialog access to the
		 * listBox ListModel. This is fine for synchronizing the data in the FinanceTypeListbox from the dialog when we
		 * do a delete, edit or insert a FinanceType.
		 */
		map.put("financeTypeListCtrl", this);

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/SolutionFactory/FinanceType/FinanceTypeDialog.zul", null, map);
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
		PTMessageUtils.showHelpWindow(event, window_FinanceTypeList);
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
		//FinanceRateReviewUtil.recalRateReview();	
		//FinanceDateRollOverUtil.doDateRollOver();
		this.pagingFinanceTypeList.setActivePage(0);
		Events.postEvent("onCreate", this.window_FinanceTypeList, event);
		this.window_FinanceTypeList.invalidate();

		logger.debug("Leaving" + event.toString());
	}

	/**
	 * call the FinanceType dialog
	 * 
	 * @param event
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public void onClick$button_FinanceTypeList_FinanceTypeSearchDialog(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		/*
		 * we can call our FinanceTypeDialog ZUL-file with parameters. So we can call them with a object of the selected
		 * FinanceType. For handed over these parameter only a Map is accepted. So we put the FinanceType object in a
		 * HashMap.
		 */
		@SuppressWarnings("rawtypes")
		final HashMap map = new HashMap();
		map.put("financeTypeList", this);
		map.put("searchObject", this.searchObj);
		map.put("listBoxFinanceType", this.listBoxFinanceType);
		map.put("pagingFinanceTypeList", this.pagingFinanceTypeList);

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/SolutionFactory/FinanceType/FinanceTypeSearchDialog.zul", null, map);
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
	public void onClick$button_FinanceTypeList_PrintList(Event event) throws InterruptedException {
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
	
	public void setPagedListService(PagedListService pagedListService) {
		this.pagedListService = pagedListService;
	}
	public PagedListService getPagedListService() {
		return pagedListService;
	}

	private FinanceType copyDATA(FinanceType newFin, Object data) {
		
		//Basic Details Tab
		FinanceType sourceFin = (FinanceType) data;
		newFin.setFinCcy(sourceFin.getFinCcy());
		newFin.setLovDescFinCcyName(sourceFin.getLovDescFinCcyName());
		newFin.setFinDaysCalType(sourceFin.getFinDaysCalType());
		newFin.setFinAcType(sourceFin.getFinAcType());
		newFin.setLovDescFinAcTypeName(sourceFin.getLovDescFinAcTypeName());
		newFin.setFinContingentAcType(sourceFin.getFinContingentAcType());
		newFin.setLovDescFinContingentAcTypeName(sourceFin.getLovDescFinContingentAcTypeName());
		newFin.setFinIsGenRef(sourceFin.isFinIsGenRef());
		newFin.setFinIsOpenNewFinAc(sourceFin.isFinIsOpenNewFinAc());
		newFin.setFinMinAmount(sourceFin.getFinMinAmount());
		newFin.setFinMaxAmount(sourceFin.getFinMaxAmount());
		newFin.setFinDftStmtFrq(sourceFin.getFinDftStmtFrq());
		newFin.setFinIsAlwMD(sourceFin.isFinIsAlwMD());
		newFin.setFinSchdMthd(sourceFin.getFinSchdMthd());
		newFin.setFInIsAlwGrace(sourceFin.isFInIsAlwGrace());
		newFin.setFinHistRetension(sourceFin.getFinHistRetension());
		newFin.setFinFrEqrepayment(sourceFin.isFinFrEqrepayment());
		newFin.setFinSchCalCodeOnRvw(sourceFin.getFinSchCalCodeOnRvw());
		newFin.setFinIsActive(sourceFin.isFinIsActive());
		newFin.setFinAssetType(sourceFin.getFinAssetType());
		newFin.setPftPayAcType(sourceFin.getPftPayAcType());
		newFin.setFinIsOpenPftPayAcc(sourceFin.isFinIsOpenPftPayAcc());
		newFin.setLovDescProductCodeName(sourceFin.getLovDescProductCodeName());
		newFin.setLovDescPftPayAcTypeName(sourceFin.getLovDescPftPayAcTypeName());	
		newFin.setFinBankContingentAcType(sourceFin.getFinBankContingentAcType());
		newFin.setLovDescFinBankContingentAcTypeName(sourceFin.getLovDescFinBankContingentAcTypeName());
		newFin.setFinProvisionAcType(sourceFin.getFinProvisionAcType());
		newFin.setLovDescFinProvisionAcTypeName(sourceFin.getLovDescFinProvisionAcTypeName());
		newFin.setFinDepreciationReq(sourceFin.isFinDepreciationReq());
		newFin.setFinDepreciationFrq(sourceFin.getFinDepreciationFrq());
		newFin.setFinIsDwPayRequired(sourceFin.isFinIsDwPayRequired());
		newFin.setFinMinDownPayAmount(sourceFin.getFinMinDownPayAmount());
		
		//Grace period Details Tab
		newFin.setFinGrcRateType(sourceFin.getFinGrcRateType());
		newFin.setFinGrcBaseRate(sourceFin.getFinGrcBaseRate());
		newFin.setLovDescFinGrcBaseRateName(sourceFin.getLovDescFinGrcBaseRateName());
		newFin.setFinGrcSplRate(sourceFin.getFinGrcSplRate());
		newFin.setLovDescFinGrcSplRateName(sourceFin.getLovDescFinGrcSplRateName());
		newFin.setFinGrcIntRate(sourceFin.getFinGrcIntRate());
		newFin.setFInGrcMinRate(sourceFin.getFInGrcMinRate());
		newFin.setFinGrcMaxRate(sourceFin.getFinGrcMaxRate());
		newFin.setFinGrcDftIntFrq(sourceFin.getFinGrcDftIntFrq());
		newFin.setFinGrcIsIntCpz(sourceFin.isFinGrcIsIntCpz());
		newFin.setFinGrcCpzFrq(sourceFin.getFinGrcCpzFrq());
		newFin.setFinGrcAlwRateChgAnyDate(sourceFin.isFinGrcAlwRateChgAnyDate());
		newFin.setFinGrcIsRvwAlw(sourceFin.isFinGrcIsRvwAlw());
		newFin.setFinGrcRvwFrq(sourceFin.getFinGrcRvwFrq());
		newFin.setFinGrcScheduleOn(sourceFin.getFinGrcScheduleOn());
		newFin.setFinGrcRvwRateApplFor(sourceFin.getFinGrcRvwRateApplFor());
		newFin.setFinIsIntCpzAtGrcEnd(sourceFin.isFinIsIntCpzAtGrcEnd());
		newFin.setFinIsAlwGrcRepay(sourceFin.isFinIsAlwGrcRepay());
		newFin.setFinGrcSchdMthd(sourceFin.getFinGrcSchdMthd());
		newFin.setFinGrcMargin(sourceFin.getFinGrcMargin());
		
		//Schedule Details Tab
		newFin.setFinRateType(sourceFin.getFinRateType());
		newFin.setFinBaseRate(sourceFin.getFinBaseRate());
		newFin.setLovDescFinBaseRateName(sourceFin.getLovDescFinBaseRateName());
		newFin.setFinSplRate(sourceFin.getFinSplRate());
		newFin.setLovDescFinSplRateName(sourceFin.getLovDescFinSplRateName());
		newFin.setFinIntRate(sourceFin.getFinIntRate());
		newFin.setFInMinRate(sourceFin.getFInMinRate());
		newFin.setFinMaxRate(sourceFin.getFinMaxRate());
		newFin.setFinDftIntFrq(sourceFin.getFinDftIntFrq());
		newFin.setFinIsIntCpz(sourceFin.isFinIsIntCpz());
		newFin.setFinCpzFrq(sourceFin.getFinCpzFrq());
		newFin.setFinAlwRateChangeAnyDate(sourceFin.isFinAlwRateChangeAnyDate());
		newFin.setFinIsRvwAlw(sourceFin.isFinIsRvwAlw());
		newFin.setFinRvwFrq(sourceFin.getFinRvwFrq());
		newFin.setFinRvwRateApplFor(sourceFin.getFinRvwRateApplFor());
		newFin.setFinSchdMthd(sourceFin.getFinSchdMthd());
		newFin.setFinMargin(sourceFin.getFinMargin());
		newFin.setFinScheduleOn(sourceFin.getFinScheduleOn());
		
		//Repay Period Details Tab
		newFin.setFinMinTerm(sourceFin.getFinMinTerm());
		newFin.setFinMaxTerm(sourceFin.getFinMaxTerm());
		newFin.setFinDftTerms(sourceFin.getFinDftTerms());
		newFin.setFinRpyFrq(sourceFin.getFinRpyFrq());
		newFin.setFInRepayMethod(sourceFin.getFInRepayMethod());
		newFin.setFinIsAlwPartialRpy(sourceFin.isFinIsAlwPartialRpy());
		newFin.setFinODRpyTries(sourceFin.getFinODRpyTries());
		newFin.setFinMaxDifferment(sourceFin.getFinMaxDifferment());
		newFin.setFinIsAlwEarlyRpy(sourceFin.isFinIsAlwEarlyRpy());		
		newFin.setFinIsAlwEarlySettle(sourceFin.isFinIsAlwEarlySettle());
		newFin.setFinIsAlwDifferment(sourceFin.isFinIsAlwDifferment());
		newFin.setFinMaxFrqDifferment(sourceFin.getFinMaxFrqDifferment());
		newFin.setFinIsAlwFrqDifferment(sourceFin.isFinIsAlwFrqDifferment());
		
		//Accounting Events Details Tab
		newFin.setFinAEAddDsbOD(sourceFin.getFinAEAddDsbOD());
		newFin.setLovDescFinAEAddDsbODName(sourceFin.getLovDescFinAEAddDsbODName());
		newFin.setLovDescEVFinAEAddDsbODName(sourceFin.getLovDescEVFinAEAddDsbODName());

		newFin.setFinAEAddDsbFD(sourceFin.getFinAEAddDsbFD());
		newFin.setLovDescFinAEAddDsbFDName(sourceFin.getLovDescFinAEAddDsbFDName());
		newFin.setLovDescEVFinAEAddDsbFDName(sourceFin.getLovDescEVFinAEAddDsbFDName());

		newFin.setFinAEAddDsbFDA(sourceFin.getFinAEAddDsbFDA());
		newFin.setLovDescFinAEAddDsbFDAName(sourceFin.getLovDescFinAEAddDsbFDAName());
		newFin.setLovDescEVFinAEAddDsbFDAName(sourceFin.getLovDescEVFinAEAddDsbFDAName());

		newFin.setFinAEAmzNorm(sourceFin.getFinAEAmzNorm());
		newFin.setLovDescFinAEAmzNormName(sourceFin.getLovDescFinAEAmzNormName());
		newFin.setLovDescEVFinAEAmzNormName(sourceFin.getLovDescEVFinAEAmzNormName());

		newFin.setFinAEAmzSusp(sourceFin.getFinAEAmzSusp());
		newFin.setLovDescFinAEAmzSuspName(sourceFin.getLovDescFinAEAmzSuspName());
		newFin.setLovDescEVFinAEAmzSuspName(sourceFin.getLovDescEVFinAEAmzSuspName());

		newFin.setFinAEToNoAmz(sourceFin.getFinAEToNoAmz());
		newFin.setLovDescFinAEToNoAmzName(sourceFin.getLovDescFinAEToNoAmzName());
		newFin.setLovDescEVFinAEToNoAmzName(sourceFin.getLovDescEVFinAEToNoAmzName());

		newFin.setFinToAmz(sourceFin.getFinToAmz());
		newFin.setLovDescFinToAmzName(sourceFin.getLovDescFinToAmzName());
		newFin.setLovDescEVFinToAmzName(sourceFin.getLovDescEVFinToAmzName());

		newFin.setFinAERateChg(sourceFin.getFinAERateChg());
		newFin.setLovDescFinAERateChgName(sourceFin.getLovDescFinAERateChgName());
		newFin.setLovDescEVFinAERateChgName(sourceFin.getLovDescEVFinAERateChgName());

		newFin.setFinAERepay(sourceFin.getFinAERepay());
		newFin.setLovDescFinAERepayName(sourceFin.getLovDescFinAERepayName());
		newFin.setLovDescEVFinAERepayName(sourceFin.getLovDescEVFinAERepayName());

		newFin.setFinAEEarlyPay(sourceFin.getFinAEEarlyPay());
		newFin.setLovDescFinAEEarlyPayName(sourceFin.getLovDescFinAEEarlyPayName());
		newFin.setLovDescEVFinAEEarlyPayName(sourceFin.getLovDescEVFinAEEarlyPayName());

		newFin.setFinAEEarlySettle(sourceFin.getFinAEEarlySettle());
		newFin.setLovDescFinAEEarlySettleName(sourceFin.getLovDescFinAEEarlySettleName());
		newFin.setLovDescEVFinAEEarlySettleName(sourceFin.getLovDescEVFinAEEarlySettleName());
		
		newFin.setFinLatePayRule(sourceFin.getFinLatePayRule());
		newFin.setLovDescFinLatePayRuleName(sourceFin.getLovDescFinLatePayRuleName());
		newFin.setLovDescEVFinLatePayRuleName(sourceFin.getLovDescEVFinLatePayRuleName());

		newFin.setFinAEWriteOff(sourceFin.getFinAEWriteOff());
		newFin.setLovDescFinAEWriteOffName(sourceFin.getLovDescFinAEWriteOffName());
		newFin.setLovDescEVFinAEWriteOffName(sourceFin.getLovDescEVFinAEWriteOffName());
		
		newFin.setFinProvision(sourceFin.getFinProvision());
		newFin.setLovDescFinProvisionName(sourceFin.getLovDescFinProvisionName());
		newFin.setLovDescEVFinProvisionName(sourceFin.getLovDescEVFinProvisionName());

		newFin.setFinSchdChange(sourceFin.getFinSchdChange());
		newFin.setLovDescFinSchdChangeName(sourceFin.getLovDescFinSchdChangeName());
		newFin.setLovDescEVFinSchdChangeName(sourceFin.getLovDescEVFinSchdChangeName());
		
		newFin.setFinAECapitalize(sourceFin.getFinAECapitalize());
		newFin.setLovDescFinAECapitalizeName(sourceFin.getLovDescFinAECapitalizeName());
		newFin.setLovDescEVFinAECapitalizeName(sourceFin.getLovDescEVFinAECapitalizeName());
		
		newFin.setFinDepreciationRule(sourceFin.getFinDepreciationRule());
		newFin.setLovDescFinDepreciationRuleName(sourceFin.getLovDescFinDepreciationRuleName());
		newFin.setLovDescEVFinDepreciationRuleName(sourceFin.getLovDescEVFinDepreciationRuleName());
		
		newFin.setFinProvision(sourceFin.getFinProvision());
		newFin.setLovDescFinProvisionName(sourceFin.getLovDescFinProvisionName());
		
		return newFin;

	}

}