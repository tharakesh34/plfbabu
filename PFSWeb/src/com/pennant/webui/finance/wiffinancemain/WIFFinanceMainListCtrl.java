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
 * FileName    		:  WIFFinanceMainListCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  12-11-2011    														*
 *                                                                  						*
 * Modified Date    :  12-11-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 12-11-2011       Pennant	                 0.1                                            * 
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

package com.pennant.webui.finance.wiffinancemain;

import java.io.Serializable;
import java.util.HashMap;

import org.apache.commons.lang.StringUtils;
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
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.ModuleMapping;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.service.finance.FinanceDetailService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.search.Filter;
import com.pennant.webui.finance.wiffinancemain.model.WIFFinanceMainListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.PTListReportUtils;
import com.pennant.webui.util.PTMessageUtils;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the /WEB-INF/pages/Finance/WIFFinanceMain/WIFFinanceMainList.zul
 * file.<br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * 
 */
public class WIFFinanceMainListCtrl extends GFCBaseListCtrl<FinanceMain> implements Serializable {

	private static final long serialVersionUID = 2808357374960437326L;
	private final static Logger logger = Logger.getLogger(WIFFinanceMainListCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window window_WIFFinanceMainList; 			// autowired
	protected Borderlayout borderLayout_WIFFinanceMainList; // autowired
	protected Paging pagingWIFFinanceMainList; 				// autowired
	protected Listbox listBoxWIFFinanceMain; 				// autowired

	// List headers
	protected Listheader listheader_FinReference; 			// autowired
	protected Listheader listheader_FinType; 				// autowired
	protected Listheader listheader_FinCcy; 				// autowired
	protected Listheader listheader_ScheduleMethod; 		// autowired
	protected Listheader listheader_Amount; 				// autowired
	protected Listheader listheader_NoOfTerms; 				// autowired
	protected Listheader listheader_StartDate; 				// autowired
	protected Listheader listheader_GraceEndDate; 			// autowired
	protected Listheader listheader_MaturityDate; 			// autowired
	protected Listheader listheader_RecordStatus; 			// autowired

	// checkRights
	protected Button btnHelp; 												// autowired
	protected Button button_WIFFinanceMainList_NewWIFFinanceMain; 			// autowired
	protected Button button_WIFFinanceMainList_WIFFinanceMainSearchDialog; 	// autowired
	protected Button button_WIFFinanceMainList_PrintList; 					// autowired

	// NEEDED for the ReUse in the SearchWindow
	protected JdbcSearchObject<FinanceMain> searchObj;
	
	private transient FinanceDetailService financeDetailService;
	private transient WorkFlowDetails workFlowDetails=null;
	
	private Textbox loanType;//Field for Maintain Different Finance Product Types
	private boolean isFacilityWIF = false;
	/**
	 * default constructor.<br>
	 */
	public WIFFinanceMainListCtrl() {
		super();
	}

	public void onCreate$window_WIFFinanceMainList(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		
		ModuleMapping moduleMapping = PennantJavaUtil.getModuleMap("WIFFinanceMain");
		boolean wfAvailable=true;
		isFacilityWIF = StringUtils.trimToEmpty(this.loanType.getValue()).equals(PennantConstants.FIN_DIVISION_FACILITY);

		if (isFacilityWIF && moduleMapping.getWorkflowType() != null){
			workFlowDetails = WorkFlowUtil.getWorkFlowDetails("WIFFinanceMain");
			
			if (workFlowDetails==null){
				setWorkFlowEnabled(false);
			}else{
				setWorkFlowEnabled(true);
				setFirstTask(getUserWorkspace().isRoleContains(workFlowDetails.getFirstTaskOwner()));
				setWorkFlowId(workFlowDetails.getId());
			}	
		}else{
			wfAvailable=false;
		}
		
		/* set components visible dependent on the users rights */
		doCheckRights();
		
		this.borderLayout_WIFFinanceMainList.setHeight(getBorderLayoutHeight());

		// set the paging parameters
		this.pagingWIFFinanceMainList.setPageSize(getListRows());
		this.pagingWIFFinanceMainList.setDetailed(true);

		this.listheader_FinReference.setSortAscending(new FieldComparator("finReference", true));
		this.listheader_FinReference.setSortDescending(new FieldComparator("finReference", false));
		this.listheader_FinType.setSortAscending(new FieldComparator("finType", true));
		this.listheader_FinType.setSortDescending(new FieldComparator("finType", false));
		this.listheader_FinCcy.setSortAscending(new FieldComparator("finCcy", true));
		this.listheader_FinCcy.setSortDescending(new FieldComparator("finCcy", false));
		this.listheader_ScheduleMethod.setSortAscending(new FieldComparator("scheduleMethod", true));
		this.listheader_ScheduleMethod.setSortDescending(new FieldComparator("scheduleMethod", false));
		this.listheader_Amount.setSortAscending(new FieldComparator("finAmount", true));
		this.listheader_Amount.setSortDescending(new FieldComparator("finAmount", false));
		this.listheader_NoOfTerms.setSortAscending(new FieldComparator("numberOfTerms", true));
		this.listheader_NoOfTerms.setSortDescending(new FieldComparator("numberOfTerms", false));
		this.listheader_StartDate.setSortAscending(new FieldComparator("finStartDate", true));
		this.listheader_StartDate.setSortDescending(new FieldComparator("finStartDate", false));
		this.listheader_GraceEndDate.setSortAscending(new FieldComparator("grcPeriodEndDate", true));
		this.listheader_GraceEndDate.setSortDescending(new FieldComparator("grcPeriodEndDate", false));
		this.listheader_MaturityDate.setSortAscending(new FieldComparator("maturityDate", true));
		this.listheader_MaturityDate.setSortDescending(new FieldComparator("maturityDate", false));
		
		if(isFacilityWIF){
			this.listheader_RecordStatus.setSortAscending(new FieldComparator("recordStatus", true));
			this.listheader_RecordStatus.setSortDescending(new FieldComparator("recordStatus", false));
		}
		
		// ++ create the searchObject and init sorting ++//
		this.searchObj = new JdbcSearchObject<FinanceMain>(FinanceMain.class,getListRows());
		this.searchObj.addSort("FinReference", false);
		if(StringUtils.trimToEmpty(this.loanType.getValue()).equals(PennantConstants.FIN_DIVISION_RETAIL)){
			this.searchObj.addFilter(new Filter("LovDescFinDivisionName", PennantConstants.PFF_CUSTCTG_INDIV, Filter.OP_EQUAL));
		}else if(StringUtils.trimToEmpty(this.loanType.getValue()).equals(PennantConstants.FIN_DIVISION_FACILITY)){
			this.searchObj.addFilter(new Filter("LovDescFinDivisionName", PennantConstants.PFF_CUSTCTG_INDIV, Filter.OP_NOT_EQUAL));
		}else if(StringUtils.trimToEmpty(this.loanType.getValue()).equals(PennantConstants.FIN_DIVISION_COMMERCIAL)){
			this.searchObj.addFilter(new Filter("LovDescFinDivisionName", PennantConstants.FIN_DIVISION_COMMERCIAL, Filter.OP_EQUAL));
		}
		if(isFacilityWIF){
			this.searchObj.addFilter(Filter.isNotNull("FacilityType"));
		}else{
			this.searchObj.addFilter(Filter.isNull("FacilityType"));
		}
		
		// Workflow
		if (isWorkFlowEnabled()) {
			this.searchObj.addTabelName("WIFFinanceMain_View");
			if (isFirstTask()) {
				button_WIFFinanceMainList_NewWIFFinanceMain.setVisible(true);
			} else {
				button_WIFFinanceMainList_NewWIFFinanceMain.setVisible(false);
			}

			this.searchObj.addFilterIn("nextRoleCode", getUserWorkspace().getUserRoles(),isFirstTask());
		}else{
			this.searchObj.addTabelName("WIFFinanceMain_View");
		}

		setSearchObj(this.searchObj);
		if (!isWorkFlowEnabled() && wfAvailable){
			this.button_WIFFinanceMainList_NewWIFFinanceMain.setVisible(false);
			this.button_WIFFinanceMainList_WIFFinanceMainSearchDialog.setVisible(false);
			this.button_WIFFinanceMainList_PrintList.setVisible(false);
			PTMessageUtils.showErrorMessage(PennantJavaUtil.getLabel("WORKFLOW CONFIG NOT FOUND"));
		}else{
			// Set the ListModel for the articles.
			getPagedListWrapper().init(this.searchObj,this.listBoxWIFFinanceMain,this.pagingWIFFinanceMainList);
			// set the itemRenderer
			this.listBoxWIFFinanceMain.setItemRenderer(new WIFFinanceMainListModelItemRenderer());
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * SetVisible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug("Entering");
		getUserWorkspace().alocateAuthorities("WIFFinanceMainList");
		this.button_WIFFinanceMainList_NewWIFFinanceMain.setVisible(getUserWorkspace().isAllowed("button_WIFFinanceMainList_NewWIFFinanceMain"));
		this.button_WIFFinanceMainList_WIFFinanceMainSearchDialog.setVisible(getUserWorkspace().isAllowed("button_WIFFinanceMainList_WIFFinanceMainFindDialog"));
		this.button_WIFFinanceMainList_PrintList.setVisible(getUserWorkspace().isAllowed("button_WIFFinanceMainList_PrintList"));
		logger.debug("Leaving");
	}

	/**
	 * This method is forwarded from the listboxes item renderer. <br>
	 * see: com.pennant.webui.finance.wiffinancemain.model.WIFFinanceMainListModelItemRenderer.java <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	
	public void onWIFFinanceMainItemDoubleClicked(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		// get the selected WIFFinanceMain object
		final Listitem item = this.listBoxWIFFinanceMain.getSelectedItem();

		if (item != null) {
			
			boolean reqCustDetails = false;
			if(!this.loanType.getValue().equals("") && !this.loanType.getValue().equals(PennantConstants.FIN_DIVISION_COMMERCIAL)){
				reqCustDetails = true;
			}
			
			// CAST AND STORE THE SELECTED OBJECT
			final FinanceMain aWIFFinanceMain = (FinanceMain) item.getAttribute("data");
			final FinanceDetail financeDetail = getFinanceDetailService().getFinanceDetailById(aWIFFinanceMain.getId(),true, "",reqCustDetails);
			if(!isFacilityWIF){
				financeDetail.getFinScheduleData().getFinanceMain().setWorkflowId(0);
			}
			if(financeDetail==null){
				String[] errParm= new String[1];
				String[] valueParm= new String[1];
				valueParm[0]=aWIFFinanceMain.getId();
				errParm[0]=PennantJavaUtil.getLabel("label_FinReference")+":"+valueParm[0];

				ErrorDetails errorDetails = ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41005", errParm,valueParm), getUserWorkspace().getUserLanguage());
				PTMessageUtils.showErrorMessage(errorDetails.getErrorMessage());
			}else{
				if(isWorkFlowEnabled()){
					String whereCond =  " AND FinReference='"+ aWIFFinanceMain.getFinReference()+"' AND version=" + aWIFFinanceMain.getVersion()+" ";

					boolean userAcces =  validateUserAccess(workFlowDetails.getId(),getUserWorkspace().getLoginUserDetails().getLoginUsrID(),
							"WIFFinanceMain", whereCond, aWIFFinanceMain.getTaskId(), aWIFFinanceMain.getNextTaskId());
					if (userAcces){
						showDetailView(financeDetail);
					}else{
						PTMessageUtils.showErrorMessage(Labels.getLabel("RECORD_NOTALLOWED"));
					}
				}else{
					showDetailView(financeDetail);
				}
			}	
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Call the WIFFinanceMain dialog with a new empty entry. <br>
	 */
	public void onClick$button_WIFFinanceMainList_NewWIFFinanceMain(Event event) throws Exception {
		logger.debug("Entering" +event.toString());
		// create a new WIFFinanceMain object, We GET it from the backend.
		final FinanceDetail aFinanceDetail = getFinanceDetailService().getNewFinanceDetail(true);
		aFinanceDetail.setNewRecord(true);
		if(!isFacilityWIF){
			aFinanceDetail.getFinScheduleData().getFinanceMain().setWorkflowId(0);
		}
		/*
		 * we can call our SelectFinanceType ZUL-file with parameters. So we can
		 * call them with a object of the selected FinanceMain. For handed over
		 * these parameter only a Map is accepted. So we put the FinanceMain object
		 * in a HashMap.
		 */
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("WIFFinanceMainDialogCtrl", new WIFFinanceMainDialogCtrl());
		map.put("searchObject", this.searchObj);
		map.put("financeDetail", aFinanceDetail);
		map.put("WIFFinanceMainListCtrl", this);
		map.put("loanType", this.loanType.getValue());
		
		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/Finance/WIFFinanceMain/WIFinanceTypeSelect.zul",null,map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / " + e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving"+event.toString());
	}

	/**
	 * Opens the detail view. <br>
	 * Overhanded some params in a map if needed. <br>
	 * 
	 * @param WIFFinanceMain (aWIFFinanceMain)
	 * @throws Exception
	 */
	private void showDetailView(FinanceDetail aFinanceDetail) throws Exception {
		logger.debug("Entering");
		/*
		 * We can call our Dialog zul-file with parameters. So we can call them
		 * with a object of the selected item. For handed over these parameter
		 * only a Map is accepted. So we put the object in a HashMap.
		 */
		FinanceMain aWIFFinanceMain = aFinanceDetail.getFinScheduleData().getFinanceMain();
		if(aWIFFinanceMain.getWorkflowId()==0 && isWorkFlowEnabled()){
			aWIFFinanceMain.setWorkflowId(workFlowDetails.getWorkFlowId());
		}
		aWIFFinanceMain.setNewRecord(aFinanceDetail.isNewRecord());
		aFinanceDetail.getFinScheduleData().setFinanceMain(aWIFFinanceMain);
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("financeDetail", aFinanceDetail);
		/*
		 * we can additionally handed over the listBox or the controller self,
		 * so we have in the dialog access to the listbox Listmodel. This is
		 * fine for synchronizing the data in the WIFFinanceMainListbox from the
		 * dialog when we do a delete, edit or insert a WIFFinanceMain.
		 */
		map.put("wIFFinanceMainListCtrl", this);
		map.put("loanType", this.loanType.getValue());

		// call the zul-file with the parameters packed in a map
		try {
			
			String productType = this.loanType.getValue();

			if(!productType.equals(PennantConstants.FIN_DIVISION_RETAIL)){
				productType = "";
			}else{
				productType = (productType.substring(0, 1)).toUpperCase()+(productType.substring(1)).toLowerCase();
			}
			Executions.createComponents("/WEB-INF/pages/Finance/WIFFinanceMain/"+productType+"WIFFinanceMainDialog.zul",null,map);
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
		PTMessageUtils.showHelpWindow(event, window_WIFFinanceMainList);
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
		this.pagingWIFFinanceMainList.setActivePage(0);
		Events.postEvent("onCreate", this.window_WIFFinanceMainList, event);
		this.window_WIFFinanceMainList.invalidate();
		logger.debug("Leaving" + event.toString());
	}

	/*
	 * call the WIFFinanceMain dialog
	 */
	
	public void onClick$button_WIFFinanceMainList_WIFFinanceMainSearchDialog(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		/*
		 * we can call our WIFFinanceMainDialog zul-file with parameters. So we can
		 * call them with a object of the selected WIFFinanceMain. For handed over
		 * these parameter only a Map is accepted. So we put the WIFFinanceMain object
		 * in a HashMap.
		 */
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("wIFFinanceMainCtrl", this);
		map.put("searchObject", this.searchObj);
		map.put("loanType", this.loanType.getValue());

		// call the zul-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/Finance/WIFFinanceMain/WIFFinanceMainSearchDialog.zul",null,map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / " + e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * When the wIFFinanceMain print button is clicked.
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$button_WIFFinanceMainList_PrintList(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		new PTListReportUtils("WIFFinanceMain", getSearchObj(),this.pagingWIFFinanceMainList.getTotalSize()+1);
		logger.debug("Leaving" + event.toString());
	}
	
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public JdbcSearchObject<FinanceMain> getSearchObj() {
		return this.searchObj;
	}
	public void setSearchObj(JdbcSearchObject<FinanceMain> so) {
		this.searchObj = so;
	}
	
	public FinanceDetailService getFinanceDetailService() {
		return financeDetailService;
	}
	public void setFinanceDetailService(FinanceDetailService financeDetailService) {
		this.financeDetailService = financeDetailService;
	}
}