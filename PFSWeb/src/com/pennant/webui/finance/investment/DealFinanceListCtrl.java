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

package com.pennant.webui.finance.investment;


import java.io.Serializable;
import java.util.HashMap;

import org.apache.log4j.Logger;
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

import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.InvestmentFinHeader;
import com.pennant.backend.service.finance.FinanceDetailService;
import com.pennant.backend.service.finance.TreasuaryFinanceService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.search.Filter;
import com.pennant.webui.finance.financemain.model.InvestMentFinanceMainListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.PTListReportUtils;
import com.pennant.webui.util.PTMessageUtils;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the /WEB-INF/pages/Finance/FinanceMain/FinanceMainList.zul
 * file.<br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * 
 */
public class DealFinanceListCtrl extends GFCBaseListCtrl<FinanceMain> implements Serializable {


	private static final long serialVersionUID = -5901195042041627750L;
	private final static Logger logger = Logger.getLogger(DealFinanceListCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window       window_DealFinanceList;                    // autoWired
	protected Borderlayout borderLayout_FinanceMainList;              // autoWired
	protected Paging       pagingFinanceMainList;                     // autoWired
	protected Listbox      listBoxFinanceMain;                        // autoWired

	// List headers
	protected Listheader listheader_CustomerCIF;                      // autoWired
	protected Listheader listheader_CustomerName;                      // autoWired
	protected Listheader listheader_FinReference;                     // autoWired
	protected Listheader listheader_INVFinReference;                   // autoWired
	protected Listheader listheader_ProductName;                     // autoWired
	protected Listheader listheader_FinType;                          // autoWired
	protected Listheader listheader_FinCcy;                           // autoWired
	protected Listheader listheader_ScheduleMethod;                   // autoWired
	protected Listheader listheader_FinAmount;                   	  // autoWired
	protected Listheader listheader_FinancingAmount;                  // autoWired
	protected Listheader listheader_RecordStatus;                     // autoWired
	protected Listheader listheader_RecordType;                       // autoWired

	// checkRights
	protected Button btnHelp;                                         // autoWired
	protected Button button_FinanceMainList_NewFinanceMain;           // autoWired
	protected Button button_DealFinanceList_SearchDialog;  // autoWired
	protected Button button_DealFinanceList_PrintList;                // autoWired

	// NEEDED for the ReUse in the SearchWindow
	protected JdbcSearchObject<FinanceMain> searchObj;

	private transient FinanceDetailService financeDetailService;
	private transient WorkFlowDetails workFlowDetails=null;
	private transient TreasuaryFinanceService treasuaryFinanceService;
	private Textbox loanType;//Field for Maintain Different Finance Product Types
	
	private InvestmentFinHeader investmentFinHeader;
	
	/**
	 * default constructor.<br>
	 */
	public DealFinanceListCtrl() {
		super();
	}

	public void onCreate$window_DealFinanceList(Event event) throws Exception {
		logger.debug("Entering " + event.toString());
		
		/* set components visible dependent on the users rights */
		doCheckRights();

		this.borderLayout_FinanceMainList.setHeight(getBorderLayoutHeight());

		// set the paging parameters
		this.pagingFinanceMainList.setPageSize(getListRows());
		this.pagingFinanceMainList.setDetailed(true);

		this.listheader_CustomerCIF.setSortAscending(new FieldComparator("lovDescCustCIF", true));
		this.listheader_CustomerCIF.setSortDescending(new FieldComparator("lovDescCustCIF", false));
		
		this.listheader_CustomerName.setSortDescending(new FieldComparator("LovDescCustShrtName", true));
		this.listheader_CustomerName.setSortAscending(new FieldComparator("LovDescCustShrtName", true));
		
		this.listheader_FinReference.setSortAscending(new FieldComparator("finReference", true));
		this.listheader_FinReference.setSortDescending(new FieldComparator("finReference", false));
		
		this.listheader_INVFinReference.setSortAscending(new FieldComparator("InvestmentRef", true));
		this.listheader_INVFinReference.setSortDescending(new FieldComparator("InvestmentRef", false));
		
		this.listheader_ProductName.setSortAscending(new FieldComparator("lovDescProductCodeName", true));
		this.listheader_ProductName.setSortDescending(new FieldComparator("lovDescProductCodeName", false));
		
		this.listheader_FinType.setSortAscending(new FieldComparator("finType", true));
		this.listheader_FinType.setSortDescending(new FieldComparator("finType", false));
		
		this.listheader_FinCcy.setSortAscending(new FieldComparator("finCcy", true));
		this.listheader_FinCcy.setSortDescending(new FieldComparator("finCcy", false));
		
		this.listheader_FinAmount.setSortAscending(new FieldComparator("finAmount", true));
		this.listheader_FinAmount.setSortDescending(new FieldComparator("finAmount", false));
		
/*		this.listheader_FinancingAmount.setSortAscending(new FieldComparator("lovDescFinancingAmount", true));
		this.listheader_FinancingAmount.setSortDescending(new FieldComparator("lovDescFinancingAmount", false));
		
*/		this.listheader_ScheduleMethod.setSortAscending(new FieldComparator("scheduleMethod", true));
		this.listheader_ScheduleMethod.setSortDescending(new FieldComparator("scheduleMethod", false));

		/*this.listheader_RecordStatus.setSortAscending(new FieldComparator("recordStatus", true));
		this.listheader_RecordStatus.setSortDescending(new FieldComparator("recordStatus", false));
		this.listheader_RecordType.setSortAscending(new FieldComparator("recordType", true));
		this.listheader_RecordType.setSortDescending(new FieldComparator("recordType", false));*/

		// ++ create the searchObject and initial sorting ++//
		this.searchObj = new JdbcSearchObject<FinanceMain>(FinanceMain.class,getListRows());
		this.searchObj.addSort("FinReference", false);
		
		//Field Declarations for Fetching List Data
		this.searchObj.addField("FinReference");
		this.searchObj.addField("InvestmentRef");
		this.searchObj.addField("FinType");
		this.searchObj.addField("FinCcy");
		this.searchObj.addField("ScheduleMethod");
		this.searchObj.addField("FinAmount");
		this.searchObj.addField("LovDescCustCIF");
		this.searchObj.addField("LovDescCustShrtName");
		this.searchObj.addField("LovDescProductCodeName");
		this.searchObj.addField("LovDescFinFormatter");
		this.searchObj.addField("RecordStatus");
		this.searchObj.addField("RecordType");
		
		this.searchObj.addSort("LovDescProductCodeName",false);
		this.searchObj.addFilter(new Filter("InvestmentRef", "", Filter.OP_NOT_EQUAL));
		this.searchObj.addTabelName("FinanceMain_DView");
		
		if (getUserWorkspace().getUserRoles() != null
				&& getUserWorkspace().getUserRoles().size() > 0) {
			String whereClause = "";
			
			for (int i = 0; i < getUserWorkspace().getUserRoles().size(); i++) {
				if (i > 0) {
					whereClause += " OR ";
				}
				
				whereClause += "(',' + nextRoleCode + ',' LIKE '%," + getUserWorkspace().getUserRoles().get(i) + ",%')";
			}
			
			// Filtering added based on user branch and division
			whereClause += " ) AND ( " +getUsrFinAuthenticationQry(false);
			
			if (!"".equals(whereClause)) {
				this.searchObj.addWhereClause(whereClause);
			}
		}

		setSearchObj(this.searchObj);
		
		// Set the ListModel for the articles.
		getPagedListWrapper().init(this.searchObj,this.listBoxFinanceMain,this.pagingFinanceMainList);
		// set the itemRenderer
		this.listBoxFinanceMain.setItemRenderer(new InvestMentFinanceMainListModelItemRenderer());
		logger.debug("Leaving " + event.toString());
	}

	/**
	 * SetVisible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug("Entering");
		getUserWorkspace().alocateAuthorities("FinanceMainList");

		this.button_FinanceMainList_NewFinanceMain.setVisible(false);
		//this.button_DealFinanceList_SearchDialog.setVisible(getUserWorkspace().isAllowed("button_DealFinanceList_SearchDialog"));
		this.button_DealFinanceList_PrintList.setVisible(getUserWorkspace().isAllowed("button_DealFinanceList_PrintList"));
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
		logger.debug("Entering " + event.toString());

		// get the selected FinanceMain object
		final Listitem item = this.listBoxFinanceMain.getSelectedItem();
		if (item != null) {

			// CAST AND STORE THE SELECTED OBJECT
			final FinanceMain aFinanceMain = (FinanceMain) item.getAttribute("data");

			String finReference = aFinanceMain.getFinReference();
			final FinanceDetail financeDetail = new FinanceDetail();
			financeDetail.getFinScheduleData().setFinReference(finReference);
			financeDetail.getFinScheduleData().setFinanceMain(aFinanceMain);

			FinanceDetail afinanceDetail = getTreasuaryFinanceService().getFinanceDetailById(financeDetail, finReference);
			this.investmentFinHeader = getTreasuaryFinanceService().getTreasuaryFinHeader(finReference, "_AView");
			this.investmentFinHeader.setFinanceDetail(afinanceDetail);			
			aFinanceMain.setInvestmentRef(investmentFinHeader.getInvestmentRef());
			
			if (aFinanceMain.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
				PTMessageUtils.showErrorMessage("Not Allowed to maintain This Record");
			} else {
				final HashMap<String, Object> map = new HashMap<String, Object>();
				map.put("role", getUserWorkspace().getUserRoles());
				map.put("investmentFinHeader", investmentFinHeader);
				map.put("DealFinanceListCtrl", this);

				// call the zul-file with the parameters packed in a map
				try {
					Executions.createComponents(
							"/WEB-INF/pages/Finance/TreasuaryFinance/DealFinanceDetailDialog.zul",
							window_DealFinanceList, map);
				}	catch (final Exception e) {
					logger.error("onOpenWindow:: error opening window / "+ e.getMessage());
					PTMessageUtils.showErrorMessage(e.toString());
				}
			}}
		logger.debug("Leaving " + event.toString());
	}

	/**
	 * Call the FinanceMain dialog with a new empty entry. <br>
	 */
	public void onClick$button_FinanceMainList_NewFinanceMain(Event event) throws Exception {
		logger.debug("Entering " + event.toString());

		// create a new FinanceMain object, We GET it from the back end.
		final FinanceDetail aFinanceDetail = getFinanceDetailService().getNewFinanceDetail(false);
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
			String productType = aFinanceMain.getLovDescProductCodeName();
			productType = (productType.substring(0, 1)).toUpperCase()+(productType.substring(1)).toLowerCase();
			Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/"+productType+"FinanceMainDialog.zul",
					this.window_DealFinanceList,map);
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
		PTMessageUtils.showHelpWindow(event, window_DealFinanceList);
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
		Events.postEvent("onCreate", this.window_DealFinanceList, event);
		this.window_DealFinanceList.invalidate();
		logger.debug("Leaving " + event.toString());
	}

	/**
	 * Method for call the FinanceMain dialog
	 */
	public void onClick$button_DealFinanceList_SearchDialog(Event event) throws Exception {
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
			Executions.createComponents("/WEB-INF/pages/Finance/TreasuaryFinance/DealFinanceSearchDialog.zul", null, map);
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
	public void onClick$button_DealFinanceList_PrintList(Event event) throws InterruptedException {
		logger.debug("Entering " + event.toString());
		new PTListReportUtils("FinanceMain", getSearchObj(),this.pagingFinanceMainList.getTotalSize()+1);
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
	
	public TreasuaryFinanceService getTreasuaryFinanceService() {
		return treasuaryFinanceService;
	}
	public void setTreasuaryFinanceService(
			TreasuaryFinanceService treasuaryFinanceService) {
		this.treasuaryFinanceService = treasuaryFinanceService;
	}

	public void setInvestmentFinHeader(InvestmentFinHeader investmentFinHeader) {
		this.investmentFinHeader = investmentFinHeader;
	}

	public InvestmentFinHeader getInvestmentFinHeader() {
		return investmentFinHeader;
	}
	
}