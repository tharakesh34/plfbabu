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
 * FileName    		:  FinanceReferenceDetailListCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  26-11-2011    														*
 *                                                                  						*
 * Modified Date    :  26-11-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 26-11-2011       Pennant	                 0.1                                            * 
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
package com.pennant.webui.lmtmasters.financereferencedetail;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.FieldComparator;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Row;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.model.lmtmasters.FinanceReference;
import com.pennant.backend.model.lmtmasters.FinanceReferenceDetail;
import com.pennant.backend.model.lmtmasters.FinanceWorkFlow;
import com.pennant.backend.service.lmtmasters.FinanceReferenceDetailService;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.webui.lmtmasters.financereferencedetail.model.FinanceReferenceDetailListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.jdbc.search.Filter;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennant.webui.util.PTListReportUtils;
import com.pennant.webui.util.searching.SearchOperatorListModelItemRenderer;
import com.pennant.webui.util.searching.SearchOperators;

/**
 * This is the controller class for the
 * /WEB-INF/pages/SolutionFactory/FinanceReferenceDetail/FinanceReferenceDetailList.zul file.
 */
public class FinanceReferenceDetailListCtrl extends GFCBaseListCtrl<FinanceWorkFlow> {
	private static final long serialVersionUID = 5574042632591594715L;
	private static final Logger logger = Logger.getLogger(FinanceReferenceDetailListCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting auto wired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window 		window_FinanceReferenceDetailList; 			// auto wired
	protected Borderlayout 	borderLayout_FinanceReferenceDetailList; 	// auto wired
	protected Paging 		pagingFinanceReferenceDetailList; 			// auto wired
	protected Listbox 		listBoxFinanceReferenceDetail; 				// auto wired

	protected Textbox  finType;											// auto wired
	protected Listbox  sortOperator_finType;							// auto wired
	protected Textbox  finTypeDesc;										// auto wired
	protected Listbox  sortOperator_finTypeDesc;						// auto wired
	protected Combobox  finEvent;										// auto wired
	protected Listbox  sortOperator_finEvent;							// auto wired

	// List headers
	protected Listheader listheader_FinanceType; 						// auto wired
	protected Listheader listheader_FinanceTypeDesc; 					// auto wired
	protected Listheader listheader_FinEvent; 							// auto wired
	protected Row		row_finevent;									// auto wired
	protected Label 	label_FinanceReferenceDetailList_FinType; 		// auto wired
	
	private String  	moduleName; 									// auto wired
	protected String 	eventName; 										// auto wired

	// checkRights
	protected Button btnHelp; 																// auto wired
	protected Button button_FinanceReferenceDetailList_NewFinanceReferenceDetail; 			// auto wired
	protected Button button_FinanceReferenceDetailList_FinanceReferenceDetailSearchDialog; 	// auto wired
	protected Button button_FinanceReferenceDetailList_PrintList; 							// auto wired

	// NEEDED for the ReUse in the SearchWindow
	protected JdbcSearchObject<FinanceWorkFlow> searchObj;
	private transient FinanceReferenceDetailService financeReferenceDetailService;
	private transient boolean  isPromotion = false;
	private transient boolean  isCollateral = false;
	private transient boolean  isCommitment = false;
	private transient boolean  isVAS = false;

	/**
	 * default constructor.<br>
	 */
	public FinanceReferenceDetailListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		moduleCode = "FinanceReferenceDetail";
	}

	/**
	 * Before binding the data and calling the List window we check, if the
	 * ZUL-file is called with a parameter for a selected FinanceCheckList object in
	 * a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_FinanceReferenceDetailList(Event event) throws Exception {
		logger.debug("Entering");
		
		this.moduleName = getArgument("module");
		this.eventName = getArgument("event");

		this.sortOperator_finType.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_finType.setItemRenderer(new SearchOperatorListModelItemRenderer());

		this.sortOperator_finTypeDesc.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_finTypeDesc.setItemRenderer(new SearchOperatorListModelItemRenderer());
		
		this.sortOperator_finEvent.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_finEvent.setItemRenderer(new SearchOperatorListModelItemRenderer());

		fillComboBox(finEvent, null, PennantStaticListUtil.getFinServiceEvents(true), "");
		
		/* set components visible dependent on the users rights */
		doCheckRights();

		this.borderLayout_FinanceReferenceDetailList.setHeight(getBorderLayoutHeight());
		this.listBoxFinanceReferenceDetail.setHeight(getListBoxHeight(searchGrid.getRows().getVisibleItemCount()));

		// set the paging parameters
		this.pagingFinanceReferenceDetailList.setPageSize(getListRows());
		this.pagingFinanceReferenceDetailList.setDetailed(true);

		this.listheader_FinanceType.setSortAscending(new FieldComparator("FinType", true));
		this.listheader_FinanceType.setSortDescending(new FieldComparator("FinType", false));
		this.listheader_FinanceTypeDesc.setSortAscending(new FieldComparator("LovDescFinTypeName", true));
		this.listheader_FinanceTypeDesc.setSortDescending(new FieldComparator("LovDescFinTypeName", false));
		this.listheader_FinEvent.setSortAscending(new FieldComparator("FinEvent", true));
		this.listheader_FinEvent.setSortDescending(new FieldComparator("FinEvent", false));

		// set the itemRenderer
		if (StringUtils.equals(eventName, FinanceConstants.FINSER_EVENT_ORG)) {
			this.listBoxFinanceReferenceDetail.setItemRenderer(new FinanceReferenceDetailListModelItemRenderer(
					PennantStaticListUtil.getFinServiceEvents(false), this.moduleName));
			this.listheader_FinEvent.setVisible(false);
		}else{
			this.listBoxFinanceReferenceDetail.setItemRenderer(new FinanceReferenceDetailListModelItemRenderer(
					PennantStaticListUtil.getFinServiceEvents(true), this.moduleName));
			this.row_finevent.setVisible(true);
		}

		doSearch();
					
		logger.debug("Leaving");
	}

	/**
	 * SetVisible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug("Entering");

		String listName = "FinanceReferenceDetailList";
		if(StringUtils.equalsIgnoreCase(PennantConstants.WORFLOW_MODULE_PROMOTION, moduleName)){
			isPromotion = true;
			listName = "PromotionReferenceDetailList";
			this.listheader_FinanceType.setLabel(Labels.getLabel("listheader_PromotionCode.label"));
			this.label_FinanceReferenceDetailList_FinType.setValue(Labels.getLabel("label_FinanceReferenceDetailList_PromotionCode.value"));
		}else if(StringUtils.equalsIgnoreCase(PennantConstants.WORFLOW_MODULE_COLLATERAL, moduleName)){
			isCollateral = true;
			listName = "CollateralReferenceDetailList";
			this.listheader_FinanceType.setLabel(Labels.getLabel("listheader_CollateralType.label"));
			this.label_FinanceReferenceDetailList_FinType.setValue(Labels.getLabel("label_FinanceReferenceDetailList_CollateralType.value"));
		}else if(StringUtils.equalsIgnoreCase(PennantConstants.WORFLOW_MODULE_COMMITMENT, moduleName)){
			isCommitment = true;
			listName = "CommitmentReferenceDetailList";
			this.listheader_FinanceType.setLabel(Labels.getLabel("listheader_Commitment.label"));
			this.label_FinanceReferenceDetailList_FinType.setValue(Labels.getLabel("label_FinanceReferenceDetailList_Commitment.value"));
		}else if(StringUtils.equalsIgnoreCase(PennantConstants.WORFLOW_MODULE_VAS, moduleName)){
			isVAS = true;
			listName = "VASReferenceDetailList";
			this.listheader_FinanceType.setLabel(Labels.getLabel("listheader_VASProductCode.label"));
			this.label_FinanceReferenceDetailList_FinType.setValue(Labels.getLabel("label_FinanceReferenceDetailList_VASProduct.value"));
		}

		getUserWorkspace().allocateAuthorities(listName);

		this.button_FinanceReferenceDetailList_NewFinanceReferenceDetail.setVisible(false);
		this.button_FinanceReferenceDetailList_FinanceReferenceDetailSearchDialog.setVisible(true);
		this.button_FinanceReferenceDetailList_PrintList.setVisible(false);
		logger.debug("Leaving");
	}

	/**
	 * This method is forwarded from the listBoxes item renderer. <br>
	 * see: com.pennant.webui.lmtmasters.financereferencedetail.model.
	 * FinanceReferenceDetailListModelItemRenderer.java <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onFinanceReferenceDetailItemDoubleClicked(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		// get the selected FinanceReferenceDetail object
		final Listitem item = this.listBoxFinanceReferenceDetail.getSelectedItem();

		if (item != null) {
			// CAST AND STORE THE SELECTED OBJECT
			final FinanceWorkFlow aFinanceWorkflow = (FinanceWorkFlow) item.getAttribute("data");

			final FinanceReference financeReference = getFinanceReferenceDetailService().getFinanceReference(
					aFinanceWorkflow.getFinType(), aFinanceWorkflow.getFinEvent(), aFinanceWorkflow.getModuleName());
			if(financeReference ==null){
				String[] errParm= new String[1];
				String[] valueParm= new String[1];
				valueParm[0]=String.valueOf(aFinanceWorkflow.getFinType());
				errParm[0]=PennantJavaUtil.getLabel("label_Code")+":"+valueParm[0];

				ErrorDetail errorDetails = ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD,"41005", errParm,valueParm), getUserWorkspace().getUserLanguage());
				MessageUtil.showError(errorDetails.getError());
			}
			if(financeReference.getLovDescWorkFlowRolesName()==null || StringUtils.isEmpty(financeReference.getLovDescWorkFlowRolesName())){
				MessageUtil.showError(PennantJavaUtil.getLabel("WORKFLOW_CONFIG_NOT_FOUND"));
			}else{
				if(isPromotion){
					financeReference.setLovDescFinTypeDescName(aFinanceWorkflow.getLovDescPromoFinTypeDesc());
				}else if(isCollateral){
					financeReference.setLovDescFinTypeDescName(aFinanceWorkflow.getCollateralDesc());
				}else if(isCommitment){
					financeReference.setLovDescFinTypeDescName(aFinanceWorkflow.getCommitmentTypeDesc());
				}else if(isVAS){
					financeReference.setLovDescFinTypeDescName(aFinanceWorkflow.getVasProductDesc());
				}else{
					financeReference.setLovDescFinTypeDescName(aFinanceWorkflow.getLovDescFinTypeName());
				}
				
				boolean isOverDraft = false;
				if(StringUtils.equals(aFinanceWorkflow.getProductCategory(), FinanceConstants.PRODUCT_ODFACILITY)){
					isOverDraft = true;
				}
				
				showDetailView(financeReference, isOverDraft);
			}
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Opens the detail view. <br>
	 * Over handed some parameters in a map if needed. <br>
	 * 
	 * @param FinanceReferenceDetail (aFinanceReferenceDetail)
	 * @throws Exception
	 */
	private void showDetailView(FinanceReference aFinanceReference, boolean isOverDraft) throws Exception {
		logger.debug("Entering");
		/*
		 * We can call our Dialog ZUL-file with parameters. So we can call them
		 * with a object of the selected item. For handed over these parameter
		 * only a Map is accepted. So we put the object in a HashMap.
		 */

		FinanceReferenceDetail financeReferenceDetail = getFinanceReferenceDetailService().getNewFinanceReferenceDetail();
		financeReferenceDetail.setFinEvent(aFinanceReference.getFinEvent());
		financeReferenceDetail.setWorkflowId(0);
		Map<String, Object> map = getDefaultArguments();
		map.put("financeReference", aFinanceReference);
		map.put("financeReferenceDetail", financeReferenceDetail);
		/*
		 * we can additionally handed over the listBox or the controller self,
		 * so we have in the dialog access to the listBox ListModel. This is
		 * fine for synchronizing the data in the FinanceReferenceDetailListbox from the
		 * dialog when we do a delete, edit or insert a FinanceReferenceDetail.
		 */
		map.put("financeReferenceDetailListCtrl", this);
		map.put("eventAction", aFinanceReference.getFinEvent());
		map.put("moduleName", this.moduleName);
		map.put("isOverDraft", isOverDraft);

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents(
					"/WEB-INF/pages/SolutionFactory/FinanceReferenceDetail/FinanceReferenceDetailDialog.zul",null,map);
		} catch (Exception e) {
			MessageUtil.showError(e);
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
		MessageUtil.showHelpWindow(event, window_FinanceReferenceDetailList);
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
		this.pagingFinanceReferenceDetailList.setActivePage(0);
		this.sortOperator_finType.setSelectedIndex(0);
		this.finType.setValue("");
		this.sortOperator_finTypeDesc.setSelectedIndex(0);
		this.sortOperator_finEvent.setSelectedIndex(0);
		this.finTypeDesc.setValue("");
		this.finEvent.setValue("");
		this.pagingFinanceReferenceDetailList.setActivePage(0);
		Events.postEvent("onCreate", this.window_FinanceReferenceDetailList, event);
		this.window_FinanceReferenceDetailList.invalidate();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Method for Call the FinanceReferenceDetail dialog
	 * @param event
	 * @throws Exception
	 */
	public void onClick$button_FinanceReferenceDetailList_FinanceReferenceDetailSearchDialog(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		doSearch();
		logger.debug("Leaving");
	}

	public void doSearch(){
		logger.debug("Entering");
		
		// ++ create the searchObject and initialize sorting ++//
		this.searchObj = new JdbcSearchObject<FinanceWorkFlow>(FinanceWorkFlow.class,getListRows());
		this.searchObj.addField("FinType");
		this.searchObj.addField("FinEvent");
		this.searchObj.addField("LovDescFinTypeName");
		this.searchObj.addField("CollateralDesc");
		this.searchObj.addField("VasProductDesc");
		this.searchObj.addField("CommitmentTypeDesc");
		this.searchObj.addField("LovDescPromotionName");
		this.searchObj.addField("LovDescFacilityTypeName");
		this.searchObj.addField("ModuleName");
		this.searchObj.addField("ProductCategory");
		this.searchObj.addSort("FinType", false);
		
		// Removing Unused Finance Events
		List<String> finEventList = new ArrayList<>();
		finEventList.add(FinanceConstants.FINSER_EVENT_FINFLAGS);
		finEventList.add(FinanceConstants.FINSER_EVENT_REINSTATE);
		this.searchObj.addFilterNotIn("FinEvent", finEventList);

		this.searchObj.addFilter(new Filter("FinIsActive", 1, Filter.OP_EQUAL));
		if(isPromotion){
			this.searchObj.addFilter(new Filter("ModuleName", PennantConstants.WORFLOW_MODULE_PROMOTION, Filter.OP_EQUAL));
		}else if(isCollateral){
			this.searchObj.addFilter(new Filter("ModuleName", PennantConstants.WORFLOW_MODULE_COLLATERAL, Filter.OP_EQUAL));
		}else if(isCommitment){
			this.searchObj.addFilter(new Filter("ModuleName", PennantConstants.WORFLOW_MODULE_COMMITMENT, Filter.OP_EQUAL));
		}else if(isVAS){
			this.searchObj.addFilter(new Filter("ModuleName", PennantConstants.WORFLOW_MODULE_VAS, Filter.OP_EQUAL));
		}else{
			this.searchObj.addFilter(new Filter("ModuleName", PennantConstants.WORFLOW_MODULE_FINANCE, Filter.OP_EQUAL));
		}
		
		if (StringUtils.equals(eventName, FinanceConstants.FINSER_EVENT_ORG)) {
			this.searchObj.addFilter(new Filter("FinEvent", new String[]{FinanceConstants.FINSER_EVENT_ORG,FinanceConstants.FINSER_EVENT_PREAPPROVAL},Filter.OP_IN));
		} else {
			this.searchObj.addFilter(new Filter("FinEvent", new String[]{FinanceConstants.FINSER_EVENT_ORG,FinanceConstants.FINSER_EVENT_PREAPPROVAL},Filter.OP_NOT_IN));
		}

		// WorkFlow
		if (isWorkFlowEnabled()) {
			if(isPromotion){
				this.searchObj.addTabelName("LMTPromotionWorkflowdef_AView");
			}else{
				this.searchObj.addTabelName("LMTFinanceWorkflowdef_AView");
			}
			if (isFirstTask()) {
				button_FinanceReferenceDetailList_NewFinanceReferenceDetail.setVisible(true);
			} else {
				button_FinanceReferenceDetailList_NewFinanceReferenceDetail.setVisible(false);
			}

			this.searchObj.addFilterIn("nextRoleCode", getUserWorkspace().getUserRoles(),isFirstTask());
		}else{
			if(isPromotion){
				this.searchObj.addTabelName("LMTPromotionWorkflowdef_AView");
			}else{
				this.searchObj.addTabelName("LMTFinanceWorkflowdef_AView");
			}
		}

		//Finance Type
		if (StringUtils.isNotBlank(this.finType.getValue())) {
			searchObj = getSearchFilter(searchObj, this.sortOperator_finType.getSelectedItem(), this.finType.getValue(), "FinType");
		}

		// Finance Type Desc
		if (StringUtils.isNotBlank(this.finTypeDesc.getValue())) {
			
			if(isCollateral){
				searchObj = getSearchFilter(searchObj, this.sortOperator_finTypeDesc.getSelectedItem(), this.finTypeDesc.getValue(), "CollateralDesc");
			}else if(isCommitment){
				searchObj = getSearchFilter(searchObj, this.sortOperator_finTypeDesc.getSelectedItem(), this.finTypeDesc.getValue(), "CommitmentTypeDesc");
			}else if(isVAS){
				searchObj = getSearchFilter(searchObj, this.sortOperator_finTypeDesc.getSelectedItem(), this.finTypeDesc.getValue(), "VasProductDesc");
			}else if(isPromotion){
				searchObj = getSearchFilter(searchObj, this.sortOperator_finTypeDesc.getSelectedItem(), this.finTypeDesc.getValue(), "LovDescPromotionName");
			}else{
				searchObj = getSearchFilter(searchObj, this.sortOperator_finTypeDesc.getSelectedItem(), this.finTypeDesc.getValue(), "LovDescFinTypeName");
			}
		}
		//Fin Event
		if (!"#".equals(this.finEvent.getSelectedItem().getValue().toString()) ) {
			searchObj = getSearchFilter(searchObj, this.sortOperator_finEvent.getSelectedItem(), this.finEvent.getSelectedItem().getValue().toString(), "FinEvent");
		}

		// Set the ListModel for the articles.
		getPagedListWrapper().init(this.searchObj,this.listBoxFinanceReferenceDetail,this.pagingFinanceReferenceDetailList);
		logger.debug("Leaving" );
	}

	/**
	 * When the financeReferenceDetail print button is clicked.
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$button_FinanceReferenceDetailList_PrintList(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		new PTListReportUtils("FinanceReferenceDetail", getSearchObj(),this.pagingFinanceReferenceDetailList.getTotalSize()+1);
		logger.debug("Leaving" + event.toString());
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public void setFinanceReferenceDetailService(FinanceReferenceDetailService financeReferenceDetailService) {
		this.financeReferenceDetailService = financeReferenceDetailService;
	}
	public FinanceReferenceDetailService getFinanceReferenceDetailService() {
		return this.financeReferenceDetailService;
	}

	public JdbcSearchObject<FinanceWorkFlow> getSearchObj() {
		return this.searchObj;
	}
	public void setSearchObj(JdbcSearchObject<FinanceWorkFlow> searchObj) {
		this.searchObj = searchObj;
	}

}