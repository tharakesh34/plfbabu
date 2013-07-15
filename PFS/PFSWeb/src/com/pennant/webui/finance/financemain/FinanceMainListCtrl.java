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
 * FileName    		:  FinanceMainListCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  15-11-2011    														*
 *                                                                  						*
 * Modified Date    :  15-11-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 15-11-2011       Pennant	                 0.1                                            * 
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

package com.pennant.webui.finance.financemain;


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
import com.pennant.backend.model.customermasters.CustomerDetails;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.service.customermasters.CustomerDetailsService;
import com.pennant.backend.service.finance.FinanceDetailService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.search.Filter;
import com.pennant.webui.finance.financemain.model.FinanceMainListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.MultiLineMessageBox;
import com.pennant.webui.util.PTMessageUtils;
import com.pennant.webui.util.PTReportUtils;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the /WEB-INF/pages/Finance/FinanceMain/FinanceMainList.zul
 * file.<br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * 
 */
public class FinanceMainListCtrl extends GFCBaseListCtrl<FinanceMain> implements Serializable {


	private static final long serialVersionUID = -5901195042041627750L;
	private final static Logger logger = Logger.getLogger(FinanceMainListCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window       window_FinanceMainList;                    // autoWired
	protected Borderlayout borderLayout_FinanceMainList;              // autoWired
	protected Paging       pagingFinanceMainList;                     // autoWired
	protected Listbox      listBoxFinanceMain;                        // autoWired

	// List headers
	protected Listheader listheader_CustomerCIF;                      // autoWired
	protected Listheader listheader_FinReference;                     // autoWired
	protected Listheader listheader_FinType;                          // autoWired
	protected Listheader listheader_FinCcy;                           // autoWired
	protected Listheader listheader_ScheduleMethod;                   // autoWired
	protected Listheader listheader_FinAmount;                   	  // autoWired
	protected Listheader listheader_RecordStatus;                     // autoWired
	protected Listheader listheader_RecordType;                       // autoWired

	// checkRights
	protected Button btnHelp;                                         // autoWired
	protected Button button_FinanceMainList_NewFinanceMain;           // autoWired
	protected Button button_FinanceMainList_FinanceMainSearchDialog;  // autoWired
	protected Button button_FinanceMainList_PrintList;                // autoWired

	// NEEDED for the ReUse in the SearchWindow
	protected JdbcSearchObject<FinanceMain> searchObj;

	private transient FinanceDetailService financeDetailService;
	private transient CustomerDetailsService customerDetailsService;
	private transient WorkFlowDetails workFlowDetails=null;

	private Textbox loanType;//Field for Maintain Different Finance Product Types
	
	/**
	 * default constructor.<br>
	 */
	public FinanceMainListCtrl() {
		super();
	}

	public void onCreate$window_FinanceMainList(Event event) throws Exception {
		logger.debug("Entering " + event.toString());
		
		ModuleMapping moduleMapping = PennantJavaUtil.getModuleMap("FinanceMain");
		//moduleMapping = PennantJavaUtil.getModuleMap("FinanceMaintenance");//TODO
		
		boolean wfAvailable=true;

		if (moduleMapping.getWorkflowType()!=null){
			workFlowDetails = WorkFlowUtil.getWorkFlowDetails("FinanceMain");

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

		this.borderLayout_FinanceMainList.setHeight(getBorderLayoutHeight());

		// set the paging parameters
		this.pagingFinanceMainList.setPageSize(getListRows());
		this.pagingFinanceMainList.setDetailed(true);

		this.listheader_CustomerCIF.setSortAscending(new FieldComparator("custID", true));
		this.listheader_CustomerCIF.setSortDescending(new FieldComparator("custID", false));
		this.listheader_FinReference.setSortAscending(new FieldComparator("finReference", true));
		this.listheader_FinReference.setSortDescending(new FieldComparator("finReference", false));
		this.listheader_FinType.setSortAscending(new FieldComparator("finType", true));
		this.listheader_FinType.setSortDescending(new FieldComparator("finType", false));
		this.listheader_FinCcy.setSortAscending(new FieldComparator("finCcy", true));
		this.listheader_FinCcy.setSortDescending(new FieldComparator("finCcy", false));
		this.listheader_FinAmount.setSortAscending(new FieldComparator("finAmount", true));
		this.listheader_FinAmount.setSortDescending(new FieldComparator("finAmount", false));
		this.listheader_ScheduleMethod.setSortAscending(new FieldComparator("scheduleMethod", true));
		this.listheader_ScheduleMethod.setSortDescending(new FieldComparator("scheduleMethod", false));

		if (isWorkFlowEnabled()){
			this.listheader_RecordStatus.setSortAscending(new FieldComparator("recordStatus", true));
			this.listheader_RecordStatus.setSortDescending(new FieldComparator("recordStatus", false));
			this.listheader_RecordType.setSortAscending(new FieldComparator("recordType", true));
			this.listheader_RecordType.setSortDescending(new FieldComparator("recordType", false));
		}else{
			this.listheader_RecordStatus.setVisible(false);
			this.listheader_RecordType.setVisible(false);
		}

		// ++ create the searchObject and initial sorting ++//
		this.searchObj = new JdbcSearchObject<FinanceMain>(FinanceMain.class,getListRows());
		this.searchObj.addSort("FinReference", false);
		if(!StringUtils.trimToEmpty(this.loanType.getValue()).equals("")){
			this.searchObj.addFilter(new Filter("lovDescProductCodeName", this.loanType.getValue().trim(), Filter.OP_EQUAL));
		}
		// Work flow
		if (isWorkFlowEnabled()) {
			this.searchObj.addTabelName("FinanceMain_View");
			if (isFirstTask()) {
				button_FinanceMainList_NewFinanceMain.setVisible(true);
			} else {
				button_FinanceMainList_NewFinanceMain.setVisible(false);
			}

			this.searchObj.addFilterOrLike("nextRoleCode", getUserWorkspace().getUserRoles(),false);
		}else{
			this.searchObj.addTabelName("FinanceMain_AView");
		}

		setSearchObj(this.searchObj);
		if (!isWorkFlowEnabled() && wfAvailable){
			this.button_FinanceMainList_NewFinanceMain.setVisible(false);
			this.button_FinanceMainList_FinanceMainSearchDialog.setVisible(false);
			this.button_FinanceMainList_PrintList.setVisible(false);
			PTMessageUtils.showErrorMessage(PennantJavaUtil.getLabel("WORKFLOW CONFIG NOT FOUND"));
		}else{
			// Set the ListModel for the articles.
			getPagedListWrapper().init(this.searchObj,this.listBoxFinanceMain,this.pagingFinanceMainList);
			// set the itemRenderer
			this.listBoxFinanceMain.setItemRenderer(new FinanceMainListModelItemRenderer());
		}
		logger.debug("Leaving " + event.toString());
	}

	/**
	 * SetVisible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug("Entering");
		getUserWorkspace().alocateAuthorities("FinanceMainList");

		this.button_FinanceMainList_NewFinanceMain.setVisible(getUserWorkspace().isAllowed("button_FinanceMainList_NewFinanceMain"));
		this.button_FinanceMainList_FinanceMainSearchDialog.setVisible(getUserWorkspace().isAllowed("button_FinanceMainList_FinanceMainFindDialog"));
		this.button_FinanceMainList_PrintList.setVisible(getUserWorkspace().isAllowed("button_FinanceMainList_PrintList"));
		logger.debug("Leaving");
	}

	/**
	 * This method is forwarded from the list boxes item renderer. <br>
	 * see: com.pennant.webui.finance.financemain.model.FinanceMainListModelItemRenderer.java <br>
	 * 
	 * @param event
	 * @throws Exception
	 */

	public void onFinanceMainItemDoubleClicked(Event event) throws Exception {
		logger.debug("Leaving " + event.toString());

		// get the selected FinanceMain object
		final Listitem item = this.listBoxFinanceMain.getSelectedItem();
		if (item != null) {
			// CAST AND STORE THE SELECTED OBJECT
			final FinanceMain aFinanceMain = (FinanceMain) item.getAttribute("data");
			final FinanceDetail financeDetail = getFinanceDetailService().getFinanceDetailById(aFinanceMain.getId(),false,"");
	
			if(financeDetail==null){
				String[] errParm= new String[1];
				String[] valueParm= new String[1];
				valueParm[0]=aFinanceMain.getId();
				errParm[0]=PennantJavaUtil.getLabel("label_FinReference")+":"+valueParm[0];

				ErrorDetails errorDetails = ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41005", errParm,valueParm)
				, getUserWorkspace().getUserLanguage());
				PTMessageUtils.showErrorMessage(errorDetails.getError());
			}else{
				//Setting Notes Count from the list View
				financeDetail.getFinScheduleData().getFinanceMain().setLovDescNotes(aFinanceMain.getLovDescNotes());
				//
				if(isWorkFlowEnabled()){
					String whereCond =  " AND FinReference='"+ aFinanceMain.getFinReference()+"' AND version=" + aFinanceMain.getVersion()+" ";

					boolean userAcces =  validateUserAccess(workFlowDetails.getId(),getUserWorkspace().getLoginUserDetails().getLoginUsrID()
							, "FinanceMain", whereCond, aFinanceMain.getTaskId(), aFinanceMain.getNextTaskId());
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
		logger.debug("Leaving " + event.toString());
	}

	/**
	 * Call the FinanceMain dialog with a new empty entry. <br>
	 */
	public void onClick$button_FinanceMainList_NewFinanceMain(Event event) throws Exception {
		logger.debug("Entering " + event.toString());


		// create a new FinanceMain object, We GET it from the back end.
		final FinanceDetail aFinanceDetail = getFinanceDetailService().getNewFinanceDetail(false);
		aFinanceDetail.setWorkflowId(workFlowDetails.getId());
		aFinanceDetail.setNewRecord(true);
		
		/*
		 * we can call our SelectFinanceType ZUL-file with parameters. So we can
		 * call them with a object of the selected FinanceMain. For handed over
		 * these parameter only a Map is accepted. So we put the FinanceMain object
		 * in a HashMap.
		 */
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("financeMainListCtrl", this);
		map.put("searchObject", this.searchObj);
		map.put("financeDetail", aFinanceDetail);
		map.put("loanType", this.loanType.getValue());
		map.put("role", getUserWorkspace().getUserRoles());
		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/SelectFinanceTypeDialog.zul",null,map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / " + e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving " + event.toString());
	}

	/**
	 * Opens the detail view. <br>
	 * Over handed some parameters in a map if needed. <br>
	 * 
	 * @param FinanceMain (aFinanceMain)
	 * @throws Exception
	 */
	protected void showDetailView(FinanceDetail aFinanceDetail) throws Exception {
		logger.debug("Entering");
		/*
		 * We can call our Dialog ZUL-file with parameters. So we can call them
		 * with a object of the selected item. For handed over these parameter
		 * only a Map is accepted. So we put the object in a HashMap.
		 */
		FinanceMain aFinanceMain = aFinanceDetail.getFinScheduleData().getFinanceMain();
		if(aFinanceMain.getWorkflowId()==0 && isWorkFlowEnabled()){
			aFinanceMain.setWorkflowId(workFlowDetails.getWorkFlowId());
		}
		aFinanceDetail.getFinScheduleData().setFinanceMain(aFinanceMain);
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("financeDetail", aFinanceDetail);
		/*
		 * we can additionally handed over the listBox or the controller self,
		 * so we have in the dialog access to the list box List model. This is
		 * fine for synchronizing the data in the FinanceMainListbox from the
		 * dialog when we do a delete, edit or insert a FinanceMain.
		 */
		map.put("financeMainListCtrl", this);

		// call the ZUL-file with the parameters packed in a map
		try {
			String screenPath="";
			if(aFinanceMain.getNextRoleCode().startsWith("FINANCE_CRD_")){//TODO - Hard COde need to give a second look
				screenPath="/WEB-INF/pages/Finance/FinanceMain/FinanceMainDialog.zul";
			}else{
				if(aFinanceMain.getCustID() == 0 || aFinanceMain.getCustID() == Long.MIN_VALUE){

					// Show a confirm box
					final String msg = Labels.getLabel("message_Data_Create_New_CustCIF_YesNo");
					final String title = Labels.getLabel("message.Conformation");

					MultiLineMessageBox.doSetTemplate();
					int conf = MultiLineMessageBox.show(msg, title,
							MultiLineMessageBox.YES | MultiLineMessageBox.NO,
							MultiLineMessageBox.QUESTION, true);

					if (conf == MultiLineMessageBox.YES) {
						CustomerDetails customerDetail = getCustomerDetailsService().getNewCustomer(true);
						map.put("customerDetails", customerDetail);								
						map.put("roleCode", getUserWorkspace().getUserRoles().get(0));								
						screenPath="/WEB-INF/pages/CustomerMasters/Customer/CustomerQDEDialog.zul";
					} else {
						screenPath="/WEB-INF/pages/Finance/FinanceMain/FinanceMainDialog.zul";
					}
					
				}else if(aFinanceMain.getCustID() != 0 && aFinanceMain.getCustID() != Long.MIN_VALUE &&
						StringUtils.trimToEmpty(aFinanceMain.getLovDescCustCIF()).equals("")){

					// Show a confirm box
					final String msg = Labels.getLabel("message_Data_CustCIF_Request_Wait_Process");
					final String title = Labels.getLabel("message.Information");

					MultiLineMessageBox.doSetTemplate();
					MultiLineMessageBox.show(msg, title,MultiLineMessageBox.OK,
							MultiLineMessageBox.INFORMATION, true);
					
				}else{
					screenPath="/WEB-INF/pages/Finance/FinanceMain/FinanceMainDialog.zul";
				}
			}
			
			if(!StringUtils.trimToEmpty(screenPath).equals("")){
				Executions.createComponents(screenPath,null,map);
			}
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
		logger.debug("Entering " + event.toString());
		PTMessageUtils.showHelpWindow(event, window_FinanceMainList);
		logger.debug("Leaving " + event.toString());
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
		logger.debug("Entering " + event.toString());
		this.pagingFinanceMainList.setActivePage(0);
		Events.postEvent("onCreate", this.window_FinanceMainList, event);
		this.window_FinanceMainList.invalidate();
		logger.debug("Leaving " + event.toString());
	}

	/**
	 * Method for call the FinanceMain dialog
	 */
	public void onClick$button_FinanceMainList_FinanceMainSearchDialog(Event event) throws Exception {
		logger.debug("Entering " + event.toString());
		/*
		 * we can call our FinanceMainDialog ZUL-file with parameters. So we can
		 * call them with a object of the selected FinanceMain. For handed over
		 * these parameter only a Map is accepted. So we put the FinanceMain object
		 * in a HashMap.
		 */
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("financeMainCtrl", this);
		map.put("searchObject", this.searchObj);


		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/FinanceMainSearchDialog.zul",null,map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / " + e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving " + event.toString());
	}

	/**
	 * When the financeMain print button is clicked.
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$button_FinanceMainList_PrintList(Event event) throws InterruptedException {
		logger.debug("Entering " + event.toString());
		PTReportUtils.getReport("FinanceMain", getSearchObj());
		logger.debug("Leaving " + event.toString());
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public FinanceDetailService getFinanceDetailService() {
		return financeDetailService;
	}
	public void setFinanceDetailService(FinanceDetailService financeDetailService) {
		this.financeDetailService = financeDetailService;
	}

	public JdbcSearchObject<FinanceMain> getSearchObj() {
		return this.searchObj;
	}
	public void setSearchObj(JdbcSearchObject<FinanceMain> searchObj) {
		this.searchObj = searchObj;
	}
	
	public CustomerDetailsService getCustomerDetailsService() {
		return customerDetailsService;
	}
	public void setCustomerDetailsService(
			CustomerDetailsService customerDetailsService) {
		this.customerDetailsService = customerDetailsService;
	}
}