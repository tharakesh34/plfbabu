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
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.converters.BigDecimalConverter;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.FieldComparator;
import org.zkoss.zul.Grid;
import org.zkoss.zul.GroupsModelArray;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Row;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.ModuleMapping;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.rmtmasters.FinTypeAccount;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.service.PagedListService;
import com.pennant.backend.service.rmtmasters.FinanceTypeService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.search.Filter;
import com.pennant.search.SearchResult;
import com.pennant.util.PennantAppUtil;
import com.pennant.webui.rmtmasters.financetype.model.FinanceTypeComparator;
import com.pennant.webui.rmtmasters.financetype.model.FinanceTypeListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.PTListReportUtils;
import com.pennant.webui.util.PTMessageUtils;
import com.pennant.webui.util.searching.SearchOperatorListModelItemRenderer;
import com.pennant.webui.util.searching.SearchOperators;

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
	protected Listheader listheader_FinDivision;
	protected Textbox finCategory;

	// Filtering Fields
	protected  Listbox   sortOperator_finType;
	protected  Textbox   finType;
	protected  Listbox   sortOperator_finTypeDesc;
	protected  Textbox   finTypeDesc;
	protected  Listbox   sortOperator_finCcy;
	protected  Textbox   finCcy;
	protected  Listbox   sortOperator_finDaysCalType;
	protected  Combobox   finDaysCalType;
	protected  Listbox   sortOperator_finSchdMthd;
	protected  Combobox   finSchdMthd;
	protected  Listbox  sortOperator_finIsAlwGrace;
	protected  Checkbox finIsAlwGrace;
	protected  Listbox  sortOperator_finDivision;
	protected  Textbox  finDivision;
	protected  Listbox  sortOperator_recordStatus;
	protected  Textbox  recordStatus;
	protected  Listbox  sortOperator_recordType;
	protected  Listbox  recordType;

	protected Label label_FinanceTypeSearch_RecordType; 	// autowired
	protected Label label_FinanceTypeSearch_RecordStatus; 		// autowired

	private Grid 			searchGrid;							// autowired
	protected Textbox 		moduleType; 						// autowired
	protected Radio			fromApproved;
	protected Radio			fromWorkFlow;
	protected Row			workFlowFrom;

	private transient boolean  approvedList=false; 
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

	private List<ValueLabel>           listProfitDaysBasis=PennantAppUtil.getProfitDaysBasis();
	private List<ValueLabel>           listScheduleMethod=PennantAppUtil.getScheduleMethod();

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

		// +++++++++++++++++++++++ DropDown ListBox ++++++++++++++++++++++ //

		this.sortOperator_finType.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_finType.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_finTypeDesc.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_finTypeDesc.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_finCcy.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_finCcy.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_finDaysCalType.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_finDaysCalType.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_finSchdMthd.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_finSchdMthd.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_finIsAlwGrace.setModel(new ListModelList<SearchOperators>(new SearchOperators().getBooleanOperators()));
		this.sortOperator_finIsAlwGrace.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_finDivision.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_finDivision.setItemRenderer(new SearchOperatorListModelItemRenderer());

		if (isWorkFlowEnabled()) {
			this.sortOperator_recordStatus.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
			this.sortOperator_recordStatus.setItemRenderer(new SearchOperatorListModelItemRenderer());
			this.sortOperator_recordType.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
			this.sortOperator_recordType.setItemRenderer(new SearchOperatorListModelItemRenderer());
			this.recordType = PennantAppUtil.setRecordType(this.recordType);
		} else {
			this.recordStatus.setVisible(false);
			this.recordType.setVisible(false);
			this.sortOperator_recordStatus.setVisible(false);
			this.sortOperator_recordType.setVisible(false);
			this.label_FinanceTypeSearch_RecordStatus.setVisible(false);
			this.label_FinanceTypeSearch_RecordType.setVisible(false);
		}


		/* set components visible dependent of the users rights */
		doCheckRights();

		/**
		 * Calculate how many rows have been place in the listBox. Get the currentDesktopHeight from a hidden IntBox
		 * from the index.zul that are filled by onClientInfo() in the indexCtroller
		 */
		this.borderLayout_FinanceTypeList.setHeight(getBorderLayoutHeight());
		this.listBoxFinanceType.setHeight(getListBoxHeight(searchGrid.getRows().getVisibleItemCount()));

		// set the paging parameters

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

		this.listheader_FinDivision.setSortAscending(new FieldComparator("finDivision", true));
		this.listheader_FinDivision.setSortDescending(new FieldComparator("finDivision", false));

		if (isWorkFlowEnabled()) {
			if (isFirstTask()) {
				button_FinanceTypeList_NewFinanceType.setVisible(true);
			} else {
				button_FinanceTypeList_NewFinanceType.setVisible(false);
			}
			this.listheader_RecordStatus.setSortAscending(new FieldComparator("recordStatus", true));
			this.listheader_RecordStatus.setSortDescending(new FieldComparator("recordStatus", false));
			this.listheader_RecordType.setSortAscending(new FieldComparator("recordType", true));
			this.listheader_RecordType.setSortDescending(new FieldComparator("recordType", false));
		} else {
			this.listheader_RecordStatus.setVisible(false);
			this.listheader_RecordType.setVisible(false);
		}

		// set the itemRenderer
		this.listBoxFinanceType.setItemRenderer(new FinanceTypeListModelItemRenderer());

		if (!isWorkFlowEnabled() && wfAvailable) {
			this.button_FinanceTypeList_NewFinanceType.setVisible(false);
			this.button_FinanceTypeList_FinanceTypeSearchDialog.setVisible(false);
			this.button_FinanceTypeList_PrintList.setVisible(false);
			PTMessageUtils.showErrorMessage(PennantJavaUtil.getLabel("WORKFLOW CONFIG NOT FOUND"));
		} else{
			doSearch();
			if(this.workFlowFrom!=null && !isWorkFlowEnabled()){
				this.workFlowFrom.setVisible(false);
				this.fromApproved.setSelected(true);
			}
		}
		setProfitDaysCalType();
		fillComboBox(this.finSchdMthd, "", listScheduleMethod, ",NO_PAY,GRCNDPAY,");
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * This method sets all rightsTypes as ComboItems for ComboBox
	 */
	private void setProfitDaysCalType() {
		logger.debug("Entering ");
		Comboitem comboitem;
		for (int i = 0; i < listProfitDaysBasis.size(); i++) {
			comboitem = new Comboitem();
			comboitem.setLabel(listProfitDaysBasis.get(i).getLabel());
			comboitem.setValue(listProfitDaysBasis.get(i).getValue());
			this.finDaysCalType.appendChild(comboitem);
		}
		logger.debug("Leaving ");
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
		//aFinanceType.setFinScheduleOn("");
		boolean isCopyProcess = false;
		if (event.getData() != null) {
			BigDecimalConverter bigDecimalConverter = new BigDecimalConverter(null);
			ConvertUtils.register(bigDecimalConverter, BigDecimal.class);
			FinanceType sourceFin = (FinanceType) event.getData();
			BeanUtils.copyProperties(aFinanceType, sourceFin);
			aFinanceType.setFinType("");
			aFinanceType.setFinTypeDesc("");
			aFinanceType.setNewRecord(true);
			isCopyProcess = true;
			List<FinTypeAccount> list = sourceFin.getFinTypeAccounts();
			if (list!=null && !list.isEmpty()) {
				aFinanceType.setFinTypeAccounts(new ArrayList<FinTypeAccount>());
				for (FinTypeAccount finTypeAccount : list) {
				 FinTypeAccount aFinTypeAccount = getFinanceTypeService().getNewFinTypeAccount();
				 aFinTypeAccount.setFinType(finTypeAccount.getFinType());
				 aFinTypeAccount.setFinCcy(finTypeAccount.getFinCcy());
				 aFinTypeAccount.setFinCcyName(finTypeAccount.getFinCcyName());
				 aFinTypeAccount.setFinFormatter(finTypeAccount.getFinFormatter());
				 aFinTypeAccount.setEvent(finTypeAccount.getEvent());
				 aFinTypeAccount.setAlwManualEntry(finTypeAccount.isAlwManualEntry());
				 aFinTypeAccount.setAlwCustomerAccount(finTypeAccount.isAlwCustomerAccount());
				 aFinTypeAccount.setAccountReceivable(finTypeAccount.getAccountReceivable());
				 aFinTypeAccount.setCustAccountTypes(finTypeAccount.getCustAccountTypes());
				 aFinTypeAccount.setVersion(1);
				 aFinTypeAccount.setRecordType(PennantConstants.RCD_ADD);
				 aFinanceType.getFinTypeAccounts().add(aFinTypeAccount);
				}
			}
			
			
			
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
		this.sortOperator_finDivision.setSelectedIndex(0);
		this.finDivision.setValue("");
		this.sortOperator_finCcy.setSelectedIndex(0);
		this.finCcy.setValue("");
		this.sortOperator_finDaysCalType.setSelectedIndex(0);
		this.finDaysCalType.setValue("");
		this.sortOperator_finIsAlwGrace.setSelectedIndex(0);
		this.finIsAlwGrace.setChecked(false);
		this.sortOperator_finSchdMthd.setSelectedIndex(0);
		this.finSchdMthd.setValue("");
		this.sortOperator_finType.setSelectedIndex(0);
		this.finType.setValue("");
		this.sortOperator_finTypeDesc.setSelectedIndex(0);
		this.finTypeDesc.setValue("");
		if (isWorkFlowEnabled()) {
			this.sortOperator_recordStatus.setSelectedIndex(0);
			this.recordStatus.setValue("");

			this.sortOperator_recordType.setSelectedIndex(0);
			this.recordType.setSelectedIndex(0);
		}
		doSearch();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * call the FinanceType dialog
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClick$button_FinanceTypeList_FinanceTypeSearchDialog(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		doSearch();
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
		PTListReportUtils reportUtils = new PTListReportUtils("FinanceType", getSearchObj(),-1);
		logger.debug("Leaving" + event.toString());
	}

	public void doSearch(){

		logger.debug("Entering");
		// ++ create the searchObject and init sorting ++//
		this.searchObj = new JdbcSearchObject<FinanceType>(FinanceType.class,-1);

		// Defualt Sort on the table
		this.searchObj.addSort("FinType", false);

		this.searchObj.addField("FinType");
		this.searchObj.addField("FinTypeDesc");
		this.searchObj.addField("FinCcy");
		this.searchObj.addField("FinDaysCalType");
		this.searchObj.addField("FinAcType");
		this.searchObj.addField("FinSchdMthd");
		this.searchObj.addField("FInIsAlwGrace");
		this.searchObj.addField("FinDivision");
		this.searchObj.addField("LovDescProductCodeDesc");
		this.searchObj.addField("LovDescProductCodeName");
		this.searchObj.addField("RecordStatus");
		this.searchObj.addField("RecordType");
		this.searchObj.addField("FinCategory");
		
		// Workflow
		if (isWorkFlowEnabled()) {
			this.searchObj.addTabelName("RMTFinanceTypes_View");

			if(this.moduleType==null){
				this.searchObj.addFilterIn("nextRoleCode", getUserWorkspace().getUserRoles(),isFirstTask());
				approvedList=false;

			}else{
				if(this.fromApproved.isSelected()){
					approvedList=true;
				}else{
					approvedList=false;
				}
			}
		}else{
			approvedList=true;
		}
		if(approvedList){
			this.searchObj.addTabelName("RMTFinanceTypes_AView");
		}else{
			this.searchObj.addTabelName("RMTFinanceTypes_View");
		}

		//Finance Assets 
		if (!StringUtils.trimToEmpty(this.finDivision.getValue()).equals("")) {
			searchObj = getSearchFilter(searchObj,this.sortOperator_finDivision.getSelectedItem(), this.finDivision.getValue() , "finDivision");
		}
		//Finance Category
		if (!StringUtils.trimToEmpty(this.finCcy.getValue()).equals("")) {
			searchObj = getSearchFilter(searchObj,this.sortOperator_finCcy.getSelectedItem(), this.finCategory.getValue() , "finCategory");
		}
		//Finance Days Calculation Type
		if (null !=this.finDaysCalType.getSelectedItem() && !StringUtils.trimToEmpty(this.finDaysCalType.getSelectedItem().getValue().toString()).equals("")){
			searchObj = getSearchFilter(searchObj, this.sortOperator_finDaysCalType.getSelectedItem(), this.finDaysCalType.getSelectedItem().getValue().toString(), "finDaysCalType");
		}

		//Finance is General Reference
		if (this.finIsAlwGrace.isChecked()) {
			searchObj = getSearchFilter(searchObj,this.sortOperator_finIsAlwGrace.getSelectedItem(), 1 , "finIsAlwGrace");
		}else{
			searchObj = getSearchFilter(searchObj,this.sortOperator_finIsAlwGrace.getSelectedItem(), 0 , "finIsAlwGrace");
		}

		//Finance Schedule Method
		if (null !=this.finSchdMthd.getSelectedItem() && !StringUtils.trimToEmpty(this.finSchdMthd.getSelectedItem().getValue().toString()).equals("")){
			searchObj = getSearchFilter(searchObj, this.sortOperator_finSchdMthd.getSelectedItem(), this.finSchdMthd.getSelectedItem().getValue().toString(), "finSchdMthd");
		}
		//Finance Finance Type
		if (!StringUtils.trimToEmpty(this.finType.getValue()).equals("")) {
			searchObj = getSearchFilter(searchObj,this.sortOperator_finType.getSelectedItem(), this.finType.getValue() , "finType");
		}
		//Finance Type Description
		if (!StringUtils.trimToEmpty(this.finTypeDesc.getValue()).equals("")) {
			searchObj = getSearchFilter(searchObj,this.sortOperator_finTypeDesc.getSelectedItem(), this.finTypeDesc.getValue() , "finTypeDesc");
		}

		// Record Status
		if (!StringUtils.trimToEmpty(recordStatus.getValue()).equals("")) {
			searchObj = getSearchFilter(searchObj,this.sortOperator_recordStatus.getSelectedItem(),this.recordStatus.getValue(), "RecordStatus");
		}

		// Record Type
		if (this.recordType.getSelectedItem() != null
				&& !StringUtils.trimToEmpty(this.recordType.getSelectedItem().getValue().toString()).equals("")) {
			searchObj = getSearchFilter(searchObj,this.sortOperator_recordType.getSelectedItem(),
					this.recordType.getSelectedItem().getValue().toString(),"RecordType");
		}

		if (logger.isDebugEnabled()) {
			final List<Filter> lf = this.searchObj.getFilters();
			for (final Filter filter : lf) {
				logger.debug(filter.getProperty().toString() + " / "+ filter.getValue().toString());

				if (Filter.OP_ILIKE == filter.getOperator()) {
					logger.debug(filter.getOperator());
				}
			}
		}
		
		// Set the ListModel for the articles.
		findSearchObject();

		logger.debug("Leaving");

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

}