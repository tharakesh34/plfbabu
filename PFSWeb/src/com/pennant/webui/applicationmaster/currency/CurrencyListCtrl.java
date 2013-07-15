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
 * FileName    		:  CurrencyListCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  26-05-2011    														*
 *                                                                  						*
 * Modified Date    :  26-05-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 26-05-2011       Pennant	                 0.1                                            * 
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

package com.pennant.webui.applicationmaster.currency;

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
import com.pennant.backend.model.applicationmaster.Currency;
import com.pennant.backend.service.applicationmaster.CurrencyService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.webui.applicationmaster.currency.model.CurrencyListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.PTListReportUtils;
import com.pennant.webui.util.PTMessageUtils;


/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the /WEB-INF/pages/ApplicationMaster/Currency/CurrencyList.zul
 * file.<br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * 
 */
public class CurrencyListCtrl extends GFCBaseListCtrl<Currency> implements Serializable {
	
	private static final long serialVersionUID = -7603242416503761389L;
	private final static Logger logger = Logger.getLogger(CurrencyListCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting auto wired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window 		window_CurrencyList; 				// autoWired
	protected Panel 		panel_CurrencyList; 				// autoWired
	protected Borderlayout 	borderLayout_CurrencyList; 			// autoWired
	protected Paging 		pagingCurrencyList; 				// autoWired
	protected Listbox 		listBoxCurrency; 					// autoWired

	// List headers
	protected Listheader 	listheader_CcyCode; 				// autoWired
	protected Listheader 	listheader_CcyNumber; 				// autoWired
	protected Listheader 	listheader_CcyDesc; 				// autoWired
	protected Listheader 	listheader_CcySwiftCode; 			// autoWired
	protected Listheader 	listheader_CcyIsActive; 			// autoWired
	protected Listheader 	listheader_RecordStatus; 			// autoWired
	protected Listheader 	listheader_RecordType;

	// checkRights
	protected Button btnHelp; 									// autoWired
	protected Button button_CurrencyList_NewCurrency; 			// autoWired
	protected Button button_CurrencyList_CurrencySearchDialog;  // autoWired
	protected Button button_CurrencyList_PrintList; 			// autoWired

	// NEEDED for the ReUse in the SearchWindow
	protected JdbcSearchObject<Currency> searchObj;
	
	private transient CurrencyService currencyService;
	private transient WorkFlowDetails workFlowDetails=null;
	
	/**
	 * default constructor.<br>
	 */
	public CurrencyListCtrl() {
		super();
	}
	
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	
	/**
	 * Before binding the data and calling the List window we check, if the
	 * ZUL-file is called with a parameter for a selected Currency object in a
	 * Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_CurrencyList(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		ModuleMapping moduleMapping = PennantJavaUtil.getModuleMap("Currency");
		boolean wfAvailable=true;
		
		if (moduleMapping.getWorkflowType()!=null){
			workFlowDetails = WorkFlowUtil.getWorkFlowDetails("Currency");
			
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
		
		/* set components visible dependent of the users rights */
		doCheckRights();

		/**
		 * Calculate how many rows have been place in the listBox. Get the
		 * currentDesktopHeight from a hidden IntBox from the index.zul that are
		 * filled by onClientInfo() in the indexCtroller
		 */
		this.borderLayout_CurrencyList.setHeight(getBorderLayoutHeight());

		// set the paging parameters
		this.pagingCurrencyList.setPageSize(getListRows());
		this.pagingCurrencyList.setDetailed(true);

		this.listheader_CcyCode.setSortAscending(new FieldComparator("ccyCode", true));
		this.listheader_CcyCode.setSortDescending(new FieldComparator("ccyCode", false));
		this.listheader_CcyNumber.setSortAscending(new FieldComparator("ccyNumber", true));
		this.listheader_CcyNumber.setSortDescending(new FieldComparator("ccyNumber", false));
		this.listheader_CcyDesc.setSortAscending(new FieldComparator("ccyDesc", true));
		this.listheader_CcyDesc.setSortDescending(new FieldComparator("ccyDesc", false));
		this.listheader_CcySwiftCode.setSortAscending(new FieldComparator("ccySwiftCode", true));
		this.listheader_CcySwiftCode.setSortDescending(new FieldComparator("ccySwiftCode", false));
		this.listheader_CcyIsActive.setSortAscending(new FieldComparator("ccyIsActive", true));
		this.listheader_CcyIsActive.setSortDescending(new FieldComparator("ccyIsActive", false));
		
		if (isWorkFlowEnabled()){
			this.listheader_RecordStatus.setSortAscending(new FieldComparator("recordStatus", true));
			this.listheader_RecordStatus.setSortDescending(new FieldComparator("recordStatus", false));
			this.listheader_RecordType.setSortAscending(new FieldComparator("recordType", true));
			this.listheader_RecordType.setSortDescending(new FieldComparator("recordType", false));
		}else{
			this.listheader_RecordStatus.setVisible(false);
			this.listheader_RecordType.setVisible(false);
		}
		
		// ++ create the searchObject and initialize sorting ++//
		this.searchObj = new JdbcSearchObject<Currency>(Currency.class,getListRows());
		this.searchObj.addSort("CcyCode", false);

		// WorkFlow
		if (isWorkFlowEnabled()) {
			this.searchObj.addTabelName("RMTCurrencies_View");
			if (isFirstTask()) {
				button_CurrencyList_NewCurrency.setVisible(true);
			} else {
				button_CurrencyList_NewCurrency.setVisible(false);
			}
			this.searchObj.addFilterIn("nextRoleCode", getUserWorkspace().getUserRoles(),isFirstTask());
		}else{
			this.searchObj.addTabelName("RMTCurrencies_AView");
		}

		setSearchObj(this.searchObj);
		if (!isWorkFlowEnabled() && wfAvailable){
			this.button_CurrencyList_NewCurrency.setVisible(false);
			this.button_CurrencyList_CurrencySearchDialog.setVisible(false);
			this.button_CurrencyList_PrintList.setVisible(false);
			PTMessageUtils.showErrorMessage(PennantJavaUtil.getLabel("WORKFLOW CONFIG NOT FOUND"));
		}else{
			// Set the ListModel for the articles.
			getPagedListWrapper().init(this.searchObj,
					this.listBoxCurrency,this.pagingCurrencyList);
			// set the itemRenderer
			this.listBoxCurrency.setItemRenderer(new CurrencyListModelItemRenderer());
		}	
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * SetVisible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug("Entering ");
		getUserWorkspace().alocateAuthorities("CurrencyList");
		
		this.button_CurrencyList_NewCurrency.setVisible(getUserWorkspace()
				.isAllowed("button_CurrencyList_NewCurrency"));
		this.button_CurrencyList_CurrencySearchDialog.setVisible(getUserWorkspace()
				.isAllowed("button_CurrencyList_CurrencyFindDialog"));
		this.button_CurrencyList_PrintList.setVisible(getUserWorkspace()
				.isAllowed("button_CurrencyList_PrintList"));
		logger.debug("Leaving ");
	}

	/**
	 * This method is forwarded from the listBoxes item renderer. <br>
	 * see: com.pennant.webui.rmtmasters.currency.model.
	 * CurrencyListModelItemRenderer.java <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCurrencyItemDoubleClicked(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		// get the selected Currency object
		final Listitem item = this.listBoxCurrency.getSelectedItem();

		if (item != null) {
			// CAST AND STORE THE SELECTED OBJECT
			final Currency aCurrency = (Currency) item.getAttribute("data");
			final Currency currency = getCurrencyService().getCurrencyById(aCurrency.getId());
			if(currency==null){

				String[] valueParm = new String[3];
				String[] errParm= new String[3];

				valueParm[0] = aCurrency.getCcyCode();
				valueParm[1] = aCurrency.getCcyNumber();
				valueParm[2] = aCurrency.getCcySwiftCode();

				errParm[0] = PennantJavaUtil.getLabel("label_CcyCode") + ":"+ valueParm[0];
				errParm[1] = PennantJavaUtil.getLabel("label_CcyNumber") + ":"+valueParm[1];
				errParm[2] = PennantJavaUtil.getLabel("label_CcySwiftCode") + ":"+valueParm[2];

				ErrorDetails errorDetails = ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,"41005", errParm,valueParm), getUserWorkspace().getUserLanguage());
				PTMessageUtils.showErrorMessage(errorDetails.getErrorMessage());
			}else{
				String whereCond =  " AND CcyCode='"+ currency.getCcyCode()+"' AND version=" + currency.getVersion()+" ";

				if(isWorkFlowEnabled()){
					boolean userAcces =  validateUserAccess(workFlowDetails.getId(),getUserWorkspace().getLoginUserDetails().getLoginUsrID(), "Currency", whereCond, currency.getTaskId(), currency.getNextTaskId());
					if (userAcces){
						showDetailView(currency);
					}else{
						PTMessageUtils.showErrorMessage(Labels.getLabel("RECORD_NOTALLOWED"));
					}
				}else{
					showDetailView(currency);
				}
			}
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Call the Currency dialog with a new empty entry. <br>
	 * @param event
	 * @throws Exception
	 */
	public void onClick$button_CurrencyList_NewCurrency(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		// create a new Currency object, We GET it from the backEnd.
		final Currency aCurrency = getCurrencyService().getNewCurrency();
		showDetailView(aCurrency);
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Opens the detail view. <br>
	 * OverHanded some parameters in a map if needed. <br>
	 * 
	 * @param Currency (aCurrency)
	 * @throws Exception
	 */
	private void showDetailView(Currency aCurrency) throws Exception {
		logger.debug("Entering ");
		/*
		 * We can call our Dialog ZUL-file with parameters. So we can call them
		 * with a object of the selected item. For handed over these parameter
		 * only a Map is accepted. So we put the object in a HashMap.
		 */
		
		if(aCurrency.getWorkflowId()==0 && isWorkFlowEnabled()){
			aCurrency.setWorkflowId(workFlowDetails.getWorkFlowId());
		}

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("currency", aCurrency);
		/*
		 * we can additionally handed over the listBox or the controller self,
		 * so we have in the dialog access to the listBox ListModel. This is
		 * fine for synchronizing the data in the CurrencyListbox from the
		 * dialog when we do a delete, edit or insert a Currency.
		 */
		map.put("currencyListCtrl", this);

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/ApplicationMaster/Currency/CurrencyDialog.zul",null,map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / " + e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving ");
	}

	/**
	 * when the "help" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnHelp(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		PTMessageUtils.showHelpWindow(event, window_CurrencyList);
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
		logger.debug(event.toString());
		this.pagingCurrencyList.setActivePage(0);
		Events.postEvent("onCreate", this.window_CurrencyList, event);
		this.window_CurrencyList.invalidate();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * call the Currency dialog
	 * @param event
	 * @throws Exception
	 */
	
	public void onClick$button_CurrencyList_CurrencySearchDialog(Event event)
			throws Exception {
		logger.debug("Entering" + event.toString());
		/*
		 * we can call our CurrencyDialog ZUL-file with parameters. So we can
		 * call them with a object of the selected Currency. For handed over
		 * these parameter only a Map is accepted. So we put the Currency object
		 * in a HashMap.
		 */
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("currencyCtrl", this);
		map.put("searchObject", this.searchObj);

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents(
					"/WEB-INF/pages/ApplicationMaster/Currency/CurrencySearchDialog.zul",null,map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / " + e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * When the currency print button is clicked.
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	@SuppressWarnings("unused")
	public void onClick$button_CurrencyList_PrintList(Event event)throws InterruptedException {
		logger.debug("Entering" + event.toString());
		PTListReportUtils reportUtils = new PTListReportUtils("Currency", getSearchObj(),this.pagingCurrencyList.getTotalSize()+1);
		logger.debug("Leaving" + event.toString());
	}
	
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	
	public void setCurrencyService(CurrencyService currencyService) {
		this.currencyService = currencyService;
	}
	public CurrencyService getCurrencyService() {
		return this.currencyService;
	}

	public JdbcSearchObject<Currency> getSearchObj() {
		return this.searchObj;
	}
	public void setSearchObj(JdbcSearchObject<Currency> searchObj) {
		this.searchObj = searchObj;
	}

}