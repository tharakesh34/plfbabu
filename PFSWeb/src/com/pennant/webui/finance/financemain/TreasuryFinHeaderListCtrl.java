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
 * FileName    		:  FinanceMainListCtrl.java                                             * 	  
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
import java.util.Date;
import java.util.HashMap;
import java.util.List;

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
import org.zkoss.zul.Window;

import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.PennantReferenceIDUtil;
import com.pennant.app.util.SystemParameterDetails;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.InvestmentFinHeader;
import com.pennant.backend.service.finance.TreasuaryFinanceService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.webui.finance.treasuaryfinance.model.TreasuaryFinHeaderListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.PTListReportUtils;
import com.pennant.webui.util.PTMessageUtils;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the /WEB-INF/pages/Finance/TreasuaryFinance/TreasuaryFinHeaderList.zul
 * file.<br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * 
 */
public class TreasuryFinHeaderListCtrl extends GFCBaseListCtrl<InvestmentFinHeader> implements Serializable {

	private static final long serialVersionUID = -5901195042041627750L;
	private final static Logger logger = Logger.getLogger(TreasuryFinHeaderListCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window       	window_TreasuaryFinHeaderList;              		// autoWired
	protected Borderlayout 	borderLayout_TreasuaryFinHeaderList;             	// autoWired
	public Paging       	pagingTFinHeaderList;                     			// autoWired
	public Listbox      	listBoxTrFinHeader;                        			// autoWired

	// List headers
	protected Listheader listheader_InvReqRef;                    				// autoWired
	protected Listheader listheader_TotPrincipal;                         	 	// autoWired
	protected Listheader listheader_FinCcy;                           			// autoWired
	protected Listheader listheader_StartDate;                   	  			// autoWired
	protected Listheader listheader_MaturityDate;                 	 			// autoWired
	protected Listheader listheader_RecordStatus;                     			// autoWired
	protected Listheader listheader_RecordType;                       			// autoWired

	// checkRights
	protected Button btnHelp;                                         			// autoWired
	protected Button button_InvestmentFinHeaderList_NewFinance;           		// autoWired
	protected Button button_InvestmentFinHeaderList_SearchDialog;  				// autoWired
	protected Button button_InvestmentFinHeaderList_PrintList;                	// autoWired

	// NEEDED for the ReUse in the SearchWindow
	protected JdbcSearchObject<InvestmentFinHeader> searchObj;
	private transient TreasuaryFinanceService treasuaryFinanceService;
	private WorkFlowDetails workFlowDetails = null;

	/**
	 * default constructor.<br>
	 */
	public TreasuryFinHeaderListCtrl() {
		super();
	}

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * zul-file is called with a parameter for a selected TreasuaryFinance
	 * object in a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_TreasuaryFinHeaderList(Event event) throws Exception {
		logger.debug("Entering " + event.toString());

		/* set components visible dependent on the users rights */
		doCheckRights();

		this.borderLayout_TreasuaryFinHeaderList.setHeight(getBorderLayoutHeight());

		// set the paging parameters
		this.pagingTFinHeaderList.setPageSize(getListRows());
		this.pagingTFinHeaderList.setDetailed(true);

		this.listheader_InvReqRef.setSortAscending(new FieldComparator("InvestmentRef", true));
		this.listheader_InvReqRef.setSortDescending(new FieldComparator("InvestmentRef", false));
		this.listheader_TotPrincipal.setSortAscending(new FieldComparator("TotPrincipalAmt", true));
		this.listheader_TotPrincipal.setSortDescending(new FieldComparator("TotPrincipalAmt", false));
		this.listheader_FinCcy.setSortAscending(new FieldComparator("FinCcy", true));
		this.listheader_FinCcy.setSortDescending(new FieldComparator("FinCcy", false));
		this.listheader_StartDate.setSortAscending(new FieldComparator("StartDate", true));
		this.listheader_StartDate.setSortDescending(new FieldComparator("StartDate", false));
		this.listheader_MaturityDate.setSortAscending(new FieldComparator("MaturityDate", true));
		this.listheader_MaturityDate.setSortDescending(new FieldComparator("MaturityDate", false));

		this.listheader_RecordStatus.setSortAscending(new FieldComparator("recordStatus", true));
		this.listheader_RecordStatus.setSortDescending(new FieldComparator("recordStatus", false));
		this.listheader_RecordType.setSortAscending(new FieldComparator("recordType", true));
		this.listheader_RecordType.setSortDescending(new FieldComparator("recordType", false));

		// ++ create the searchObject and initial sorting ++//
		this.searchObj = new JdbcSearchObject<InvestmentFinHeader>(InvestmentFinHeader.class,getListRows());
		this.searchObj.addSort("InvestmentRef", false);
		
		//Field Declarations for Fetching List Data
		this.searchObj.addField("InvestmentRef");
		this.searchObj.addField("TotPrincipalAmt");
		this.searchObj.addField("FinCcy");
		this.searchObj.addField("LovDescFinFormatter");
		this.searchObj.addField("StartDate");
		this.searchObj.addField("MaturityDate");
		this.searchObj.addField("RecordStatus");
		this.searchObj.addField("RecordType");

		this.searchObj.addSort("InvestmentRef", false);
		this.searchObj.addTabelName("InvestmentFinHeader_View");
		
		// WorkFlow
		if (isWorkFlowEnabled()) {
			if (isFirstTask()) {
				button_InvestmentFinHeaderList_NewFinance.setVisible(true);
			} else {
				button_InvestmentFinHeaderList_NewFinance.setVisible(false);
			}
		} 

		if (getUserWorkspace().getUserRoles() != null
				&& getUserWorkspace().getUserRoles().size() > 0) {
			String whereClause = "";

			for (int i = 0; i < getUserWorkspace().getUserRoles().size(); i++) {
				if (i > 0) {
					whereClause += " OR ";
				}

				whereClause += "(',' + nextRoleCode + ',' LIKE '%," + getUserWorkspace().getUserRoles().get(i) + ",%')";
			}

			if (!"".equals(whereClause)) {
				this.searchObj.addWhereClause(whereClause);
			}
		}

		setSearchObj(this.searchObj);

		// Set the ListModel for the articles.
		getPagedListWrapper().init(this.searchObj, this.listBoxTrFinHeader, this.pagingTFinHeaderList);
		// set the itemRenderer
		this.listBoxTrFinHeader.setItemRenderer(new TreasuaryFinHeaderListModelItemRenderer());
		logger.debug("Leaving " + event.toString());
	}

	/**
	 * SetVisible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug("Entering");
		getUserWorkspace().alocateAuthorities("InvestmentFinHeaderList", getRole());
		this.button_InvestmentFinHeaderList_NewFinance.setVisible(getUserWorkspace().isAllowed("button_InvestmentFinHeaderList_btnNew"));
		this.button_InvestmentFinHeaderList_SearchDialog.setVisible(getUserWorkspace().isAllowed("button_InvestmentFinHeaderList_SearchDialog"));
		this.button_InvestmentFinHeaderList_PrintList.setVisible(getUserWorkspace().isAllowed("button_InvestmentFinHeaderList_PrintList"));
		logger.debug("Leaving");
	}

	/**
	 * This method is forwarded from the list boxes item renderer. <br>
	 * see: com.pennant.webui.finance.financemain.model.FinanceMainListModelItemRenderer.java <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onTreasuaryFinanceItemDoubleClicked(Event event) throws Exception {
		logger.debug("Entering " + event.toString());

		// get the selected FinanceMain object
		final Listitem item = this.listBoxTrFinHeader.getSelectedItem();
		if (item != null) {
			// CAST AND STORE THE SELECTED OBJECT
			final InvestmentFinHeader aTreasuaryFinHeader = (InvestmentFinHeader) item.getAttribute("data");
			InvestmentFinHeader treasuaryFinHeader = getTreasuaryFinanceService().getTreasuaryFinanceById(aTreasuaryFinHeader.getId());

			if (treasuaryFinHeader == null) {
				String[] errParm = new String[1];
				String[] valueParm = new String[1];
				valueParm[0] = aTreasuaryFinHeader.getId();
				errParm[0] = PennantJavaUtil.getLabel("label_FinReference") + ":" + valueParm[0];

				ErrorDetails errorDetails;
				errorDetails = ErrorUtil.getErrorDetail( new ErrorDetails(PennantConstants.KEY_FIELD, "41005",
						errParm, valueParm), getUserWorkspace().getUserLanguage());
				PTMessageUtils.showErrorMessage(errorDetails.getError());
			} else {
				List<FinanceDetail> financeDetails = null;				
				financeDetails = getTreasuaryFinanceService().getFinanceDetails(treasuaryFinHeader);
				treasuaryFinHeader.setFinanceDetailsList(financeDetails);
				showDetailView(treasuaryFinHeader);
			}
		}
		logger.debug("Leaving " + event.toString());
	}

	/**
	 * Call the FinanceMain dialog with a new empty entry. <br>
	 */
	public void onClick$button_InvestmentFinHeaderList_NewFinance(Event event) throws Exception {
		logger.debug("Entering " + event.toString());
		// create a new FinanceMain object, We GET it from the back end.
		final InvestmentFinHeader aInvestmentFinHeader = getTreasuaryFinanceService().getNewTreasuaryFinance();	
		showDetailView(getTrasuryFinHeader(aInvestmentFinHeader));
		logger.debug("Leaving " + event.toString());
	}

	/**
	 * Opens the detail view. <br>
	 * Over handed some parameters in a map if needed. <br>
	 * 
	 * @param FinanceMain (aFinanceMain)
	 * @throws Exception
	 */
	protected void showDetailView(InvestmentFinHeader aInvestmentFinHeader) throws Exception {
		logger.debug("Entering");
		/*
		 * We can call our Dialog ZUL-file with parameters. So we can call them
		 * with a object of the selected item. For handed over these parameter
		 * only a Map is accepted. So we put the object in a HashMap.
		 */


		if (aInvestmentFinHeader.getWorkflowId() == 0 && isWorkFlowEnabled()) {
			aInvestmentFinHeader.setWorkflowId(workFlowDetails.getWorkFlowId());
		}

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("investmentFinHeader", aInvestmentFinHeader);
		/*
		 * we can additionally handed over the listBox or the controller self,
		 * so we have in the dialog access to the list box List model. This is
		 * fine for synchronizing the data in the FinanceMainListbox from the
		 * dialog when we do a delete, edit or insert a FinanceMain.
		 */
		//map.put("financeMainListCtrl", this);
		map.put("treasuryFinHeaderListCtrl", this);

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/Finance/TreasuaryFinance/TreasuaryFinHeaderDialog.zul", this.window_TreasuaryFinHeaderList,map);
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
		PTMessageUtils.showHelpWindow(event, window_TreasuaryFinHeaderList);
		logger.debug("Leaving " + event.toString());
	}

	/**
	 * when the "filter" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$button_InvestmentFinHeaderList_SearchDialog(Event event) throws InterruptedException {
		logger.debug("Entering " + event.toString());

		/*
		 * we can call our SecurityGroupDialog ZUL-file with parameters. So we can
		 * call them with a object of the selected SecurityGroup. For handed over
		 * these parameter only a Map is accepted. So we put the SecurityGroup object
		 * in a HashMap.
		 */
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("treasuryFinHeaderListCtrl", this);
		map.put("searchObject", this.searchObj);
		map.put("listBoxTreasuryFinance", this.listBoxTrFinHeader);
		map.put("pagingSecurityGroupList", this.pagingTFinHeaderList);

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/Finance/TreasuaryFinance/TreasuaryFinanceSearchDialog.zul" 
					, null, map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / " + e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
		}

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
		this.pagingTFinHeaderList.setActivePage(0);
		Events.postEvent("onCreate", this.window_TreasuaryFinHeaderList, event);
		this.window_TreasuaryFinHeaderList.invalidate();
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
		map.put("treasuryFinHeaderListCtrl", this);
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
	public void onClick$button_InvestmentFinHeaderList_PrintList(Event event) throws InterruptedException {
		logger.debug("Entering " + event.toString());
		new PTListReportUtils("InvestmentFinHeader", getSearchObj(),this.pagingTFinHeaderList.getTotalSize()+1);
		logger.debug("Leaving " + event.toString());
	}

	private InvestmentFinHeader getTrasuryFinHeader(InvestmentFinHeader treasuaryFinHeader) {
		treasuaryFinHeader.setInvestmentRef(String.valueOf(PennantReferenceIDUtil.genInvetmentNewRef()));
		treasuaryFinHeader.setStartDate((Date) SystemParameterDetails.getSystemParameterValue(PennantConstants.APP_DATE_CUR));
		return treasuaryFinHeader;
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public JdbcSearchObject<InvestmentFinHeader> getSearchObj() {
		return this.searchObj;
	}
	public void setSearchObj(JdbcSearchObject<InvestmentFinHeader> searchObj) {
		this.searchObj = searchObj;
	}

	public void setTreasuaryFinanceService(TreasuaryFinanceService treasuaryFinanceService) {
		this.treasuaryFinanceService = treasuaryFinanceService;
	}
	public TreasuaryFinanceService getTreasuaryFinanceService() {
		return treasuaryFinanceService;
	}

}