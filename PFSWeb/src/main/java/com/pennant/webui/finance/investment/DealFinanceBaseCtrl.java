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
 * FileName    		:  MurabahaFinanceMainDialogCtrl.java                                                   * 	  
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
package com.pennant.webui.finance.investment;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Longbox;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Row;
import org.zkoss.zul.Space;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Tabpanels;
import org.zkoss.zul.Tabs;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.AccountSelectionBox;
import com.pennant.CurrencyBox;
import com.pennant.Interface.service.AccountInterfaceService;
import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.MailUtil;
import com.pennant.app.util.ScheduleCalculator;
import com.pennant.app.util.ScheduleGenerator;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceDisbursement;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.InvestmentFinHeader;
import com.pennant.backend.model.lmtmasters.FinanceCheckListReference;
import com.pennant.backend.model.lmtmasters.FinanceReferenceDetail;
import com.pennant.backend.model.rulefactory.FeeRule;
import com.pennant.backend.model.rulefactory.Rule;
import com.pennant.backend.service.accounts.AccountsService;
import com.pennant.backend.service.finance.AgreementDetailService;
import com.pennant.backend.service.finance.TreasuaryFinanceService;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.webui.finance.financemain.AgreementDetailDialogCtrl;
import com.pennant.webui.finance.financemain.DocumentDetailDialogCtrl;
import com.pennant.webui.finance.treasuaryfinance.TreasuaryFinHeaderDialogCtrl;
import com.pennant.webui.lmtmasters.financechecklistreference.FinanceCheckListReferenceDialogCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.MessageUtil;
import com.pennanttech.pff.core.App;
import com.rits.cloning.Cloner;

/**
 * This is the controller class for the
 * /WEB-INF/pages/Finance/FinanceMain/InvestmentDealDialog.zul file.
 */
public class DealFinanceBaseCtrl<T> extends GFCBaseCtrl<FinanceDetail> {
	private static final long serialVersionUID = 6004939933729664895L;
	private final static Logger logger = Logger.getLogger(DealFinanceBaseCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Textbox 		investmentRef; 				// autoWired
	protected Decimalbox 	totPrinAmt; 			    // autoWired
	protected Textbox       finCcy;                     // autowired

	protected Combobox      profitDaysBasis;            // autowired
	protected Datebox		invStartDate;				// autoWired
	protected Datebox		invMaturityDate;			// autoWired
	protected Decimalbox	prinInvested; 			    // autoWired
	protected Decimalbox	prinMaturity; 			    // autoWired
	protected Decimalbox	prinDueToInvest; 			// autoWired
	protected Decimalbox 	avgPftRate; 			    // autoWired

	protected Longbox   	custID; 			        // autoWired
	protected Textbox   	lovDescCustCIF; 			// autoWired
	protected Button        btnSearchCustCIF;           // autoWired
	protected Label   		custShrtName; 			    // autoWired
	protected Space   		space_custCIF; 			    // autoWired

	protected Textbox   	finBranch; 					// autoWired
	protected Textbox   	lovDescFinBranchName; 		// autoWired
	
	protected Textbox 	    finType;             		// autoWired
	protected Textbox       lovDescFinTypeName;         // autoWired

	protected Datebox		startDate;					// autoWired
	protected Datebox		maturityDate;				// autoWired

	protected CurrencyBox 	finAmount;               	// autoWired
	protected Decimalbox 	finAmount_Two;              // autoWired
	protected Space		 	space_finAmount_Two;        // autoWired

	protected Textbox 	    dealTcktRef;                // autoWired

	protected Decimalbox 	repayProfitRate;           	// autoWired
	protected Space 		space_repayProfitRate;      // autoWired

	protected Decimalbox 	totalRepayAmt;              // autoWired
	protected Combobox 		finRepayMethod; 			// autoWired

	protected Row      row_AccountFields;
	protected AccountSelectionBox  disbAcctId;
	protected AccountSelectionBox  repayAcctId;

	protected Component 	childWindow = null;
	protected Component 	checkListChildWindow = null;

	// old value vars for edit mode. that we can check if something
	// on the values are edited since the last init.
	private transient boolean validationOn;
	// Button controller for the CRUD button

	// not auto wired variables
	public InvestmentFinHeader  investmentFinHeader; 
	public FinanceDetail 		financeDetail = null; 

	private TreasuaryFinHeaderDialogCtrl treasuaryFinHeaderDialogCtrl=null;
	private AgreementDetailDialogCtrl agreementDetailDialogCtrl = null; 
	private DocumentDetailDialogCtrl documentDetailDialogCtrl = null;
	private FinanceCheckListReferenceDialogCtrl  financeCheckListReferenceDialogCtrl = null;

	private transient TreasuaryFinanceService treasuaryFinanceService;
	private transient AgreementDetailService agreementDetailService;
	private transient AccountsService accountsService;
	private transient AccountInterfaceService accountInterfaceService;
	private MailUtil mailUtil;

	protected boolean newRecord=false;
	protected boolean newInvestment = false;
	protected boolean recSave = false;
	private transient Boolean assetDataChanged;

	//Main Tab Details
	protected Tabs 			tabsIndexCenter;
	protected Tabpanels 	tabpanelsBoxIndexCenter;
	protected Tab 			financeTypeDetailsTab;	

	/**
	 * default constructor.<br>
	 */
	public DealFinanceBaseCtrl() {
		super();
	}

	public void onSelectAccountingDetail(ForwardEvent event) throws IllegalAccessException, InvocationTargetException, InterruptedException {
		getTreasuaryFinanceService().setFinanceDetails(getFinanceDetail(), "ACCOUNTING", getRole());
		Tab tab = (Tab)event.getOrigin().getTarget();
		tab.removeForward(Events.ON_SELECT ,  (Tab)null,  "onSelectAccountingDetail");
		appendAccountingDetailTab(false);
	}

	public void onSelectAgreements(ForwardEvent event) {
		getTreasuaryFinanceService().setFinanceDetails(getFinanceDetail(), "AGGREMENTS", getRole());
		Tab tab = (Tab)event.getOrigin().getTarget();
		tab.removeForward(Events.ON_SELECT , (Tab)null,  "onSelectAgreements");
		appendtAgreementsDetailTab(false);
	}

	/**
	 * setCheckListDetails method checks if checkList tab exist or not and sets tab
	 * visibility according to checkList details availability
	 * 
	 * @param window
	 * 
	 */
	public void setCheckListDetails(Window window) {
		appendCheckListDetailTab(false);
	}

	public void setAgreementDetails(Window window) {

		Tab tab = (Tab) window.getFellowIfAny("agreementsTab");
		if (tab != null) {
			if (!getFinanceDetail().getAggrementList().isEmpty()) {
				tab.setVisible(true);
				ComponentsCtrl.applyForward(tab, "onSelect=onSelectAgreements");
			} else {
				tab.setVisible(false);
			}
		}
	}

	/**
	 * Method for Append Recommend Details Tab
	 * 
	 * @throws InterruptedException
	 */
	public void appendRecommendDetailTab() throws InterruptedException {
		logger.debug("Entering");

		// Memo Tab Details -- Comments or Recommendations
		// this.btnNotes.setVisible(false);

		Tab tab = new Tab("Recommendations");
		tab.setId("memoDetailTab");
		tabsIndexCenter.appendChild(tab);

		Tabpanel tabpanel = new Tabpanel();
		tabpanel.setId("memoDetailTabPanel");
		tabpanel.setHeight(this.borderLayoutHeight + "px");
		tabpanel.setStyle("overflow:auto");
		tabpanel.setParent(tabpanelsBoxIndexCenter);

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("isFinanceNotes", true);
		map.put("notes", getNotes(this.financeDetail.getFinScheduleData().getFinanceMain()));
		map.put("control", this);

		try {
			Executions.createComponents("/WEB-INF/pages/notes/notes.zul",
					tabpanel, map);
		} catch (Exception e) {
			logger.error("Exception: ", e);
			MessageUtil.showErrorMessage(e);
		}
		logger.debug("Leaving");
	}

	/**
	 * Method for Rendering Document Details Data in finance
	 */
	public void appendDocumentDetailTab(){
		logger.debug("Entering");

		Tab tab = new Tab("Documents");
		tab.setId("documentDetailsTab");
		tabsIndexCenter.appendChild(tab);
		ComponentsCtrl.applyForward(tab, "onSelect=onSelectDocumentDetail");

		Tabpanel documentTabPanel = new Tabpanel();
		documentTabPanel.setId("documentsTabPanelID");
		documentTabPanel.setStyle("overflow:auto;");
		documentTabPanel.setParent(tabpanelsBoxIndexCenter);
		documentTabPanel.setHeight(this.borderLayoutHeight - 100 + "px");

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("roleCode", getRole());
		map.put("financeMainDialogCtrl", this);
		map.put("financeDetail", getFinanceDetail());
		map.put("profitDaysBasisList", PennantStaticListUtil.getProfitDaysBasis());
		map.put("schMethodList", PennantStaticListUtil.getScheduleMethods());

		Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/DocumentDetailDialog.zul", documentTabPanel, map);

		logger.debug("Leaving");
	}

	/**
	 * Method for Preparation of Check List Details Window
	 * 
	 * @param financeDetail
	 * @param finIsNewRecord
	 * @param map
	 */
	public void appendCheckListDetailTab(boolean loadTab){
		logger.debug("Entering");
		getTreasuaryFinanceService().setFinanceDetails(getFinanceDetail(), "DOCUMENTS", getRole());
		getTreasuaryFinanceService().setFinanceDetails(getFinanceDetail(), "CHECKLIST", getRole());

		boolean createTab = false;
		if (getFinanceDetail().getCheckList() != null && getFinanceDetail().getCheckList().size() > 0) {

			if(tabsIndexCenter.getFellowIfAny("checkListTab") == null){
				createTab = true;
			}
		}else if(loadTab && !isReadOnly("FinanceMainDialog_custID")){
			createTab = true;
		}

		if(createTab){
			Tab tab = new Tab("CheckList");
			tab.setId("checkListTab");
			tabsIndexCenter.appendChild(tab);
			tab.setVisible(false);

			Tabpanel tabpanel = new Tabpanel();
			tabpanel.setId("checkListTabPanel");
			tabpanel.setStyle("overflow:auto;");
			tabpanelsBoxIndexCenter.appendChild(tabpanel);
		}

		if (financeDetail.getCheckList() != null && financeDetail.getCheckList().size() > 0) {
			boolean finIsNewRecord = getFinanceDetail().getFinScheduleData().getFinanceMain().isNewRecord();
			String rcdType = getFinanceDetail().getFinScheduleData().getFinanceMain().getRecordType();
			rcdType = StringUtils.trimToEmpty(rcdType);
			if (!rcdType.equals(PennantConstants.RECORD_TYPE_UPD)) {
				if (finIsNewRecord || StringUtils.isNotEmpty(rcdType)) {

					boolean createcheckLsitTab = false;
					for (FinanceReferenceDetail chkList : financeDetail.getCheckList()) {
						if (chkList.getShowInStage().contains(getRole())) {
							createcheckLsitTab = true;
							break;
						}
						if (chkList.getAllowInputInStage().contains(getRole())) {
							createcheckLsitTab = true;
							break;
						}
					}

					if (createcheckLsitTab) {

						Tabpanel tabpanel = null;
						if(tabpanelsBoxIndexCenter.getFellowIfAny("checkListTabPanel") != null){
							tabpanel = (Tabpanel) tabpanelsBoxIndexCenter.getFellowIfAny("checkListTabPanel");
							tabpanel.setStyle("overflow:auto;");
							tabpanel.getChildren().clear();
						}
						tabpanel.setHeight(this.borderLayoutHeight - 100 - 20 + "px");

						final HashMap<String, Object> map = new HashMap<String, Object>();
						map.put("financeMainDialogCtrl", this);
						map.put("ccyFormatter", CurrencyUtil.getFormat(getFinanceDetail().getFinScheduleData().getFinanceType().getFinCcy()));
						map.put("financeDetail", financeDetail);
						map.put("roleCode", getRole());

						checkListChildWindow = Executions.createComponents(
								"/WEB-INF/pages/LMTMasters/FinanceCheckListReference/FinanceCheckListReferenceDialog.zul", tabpanel, map);

						Tab tab = null;
						if(tabsIndexCenter.getFellowIfAny("checkListTab") != null){
							tab = (Tab) tabsIndexCenter.getFellowIfAny("checkListTab");
							tab.setVisible(true);
						}
					} 
				}
			}
		} 

		logger.debug("Leaving");
	}


	/**
	 * Method for Rendering Joint account and guaranteer Details Data in finance
	 */
	public void appendtAgreementsDetailTab(boolean onLoad){
		logger.debug("Entering");
		getTreasuaryFinanceService().setFinanceDetails(getFinanceDetail(), "AGGREMENTS", getRole());
		List<FinanceReferenceDetail> aggrementList = getFinanceDetail().getAggrementList();

		String finType = getFinanceDetail().getFinScheduleData().getFinanceMain().getFinType();
		long custId = getFinanceDetail().getFinScheduleData().getFinanceMain().getCustID();
		
		boolean isDataEmpty = StringUtils.trimToNull(finType) == null
				|| (custId == 0 || custId == Long.MIN_VALUE);

		boolean createTab = false;
		if (aggrementList != null && !aggrementList.isEmpty()) {

			if(tabsIndexCenter.getFellowIfAny("agreementsTab") == null){
				createTab = true;
			}
		}
		Tabpanel tabpanel = null;
		if (createTab) {
			Tab tab = new Tab("Agreements");
			tab.setId("agreementsTab");
			tabsIndexCenter.appendChild(tab);
			ComponentsCtrl.applyForward(tab, "onSelect=onSelectAgreements");
			tabpanel = new Tabpanel();
			tabpanel.setId("agreementsTabPanelID");
			tabpanel.setStyle("overflow:auto;");
			tabpanel.setParent(tabpanelsBoxIndexCenter);
			tabpanel.setHeight(this.borderLayoutHeight - 100 - 30 + "px");
			tab.setVisible(true);
		} else {
			if(tabpanelsBoxIndexCenter.getFellowIfAny("agreementsTabPanelID") != null){
				tabpanel = (Tabpanel) tabpanelsBoxIndexCenter.getFellowIfAny("agreementsTabPanelID");
				tabpanel.setStyle("overflow:auto;");
				tabpanel.getChildren().clear();
			}
		}

		if((aggrementList != null && !aggrementList.isEmpty())  || (!isDataEmpty && tabpanel != null)){
			final HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("financeMainDialogCtrl", this);
			map.put("financeDetail", getFinanceDetail());
			map.put("profitDaysBasisList", PennantStaticListUtil.getProfitDaysBasis());
			map.put("schMethodList", PennantStaticListUtil.getScheduleMethods());

			Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/AgreementDetailDialog.zul", tabpanel, map);
		}

		logger.debug("Leaving");
	}


	/**
	 * Creates a page from a zul-file in a tab in the center area of the
	 * borderlayout.
	 * 
	 * @throws InterruptedException
	 */
	public void appendAssetDetailTab(){
		logger.debug("Entering");
		
		String zulFilePathName = "";		
		String    tabLabel = "Commodity";

		try {

			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("roleCode", getRole());
			map.put("financeMainDialogCtrl", this);
			map.put("ccyFormatter", CurrencyUtil.getFormat(this.finCcy.getValue()));

			String finReference = this.dealTcktRef.getValue();
			getFinanceDetail().getFinScheduleData().setFinReference(finReference);
			String assetCode = getFinanceDetail().getFinScheduleData().getFinanceMain().getFinPurpose();
			assetCode = StringUtils.trimToEmpty(assetCode);

			tabLabel = Labels.getLabel("CommidityLoanDetail");
			map.put("financedetail", getFinanceDetail());
			zulFilePathName = "/WEB-INF/pages/LMTMasters/CommidityLoanDetail/FinCommidityLoanDetailList.zul";

			Tab tab = new Tab(tabLabel);
			tab.setId("loanAssetTab");
			tabsIndexCenter.appendChild(tab);
			ComponentsCtrl.applyForward(tab, "onSelect=onSelectAssetDetail"); 

			Tabpanel assetTabPanel = new Tabpanel();
			assetTabPanel.setId("assetTabPanelID");
			assetTabPanel.setStyle("overflow:auto;");
			assetTabPanel.setParent(tabpanelsBoxIndexCenter);
			assetTabPanel.setHeight(this.borderLayoutHeight - 100 - 20 + "px");

			childWindow = Executions.createComponents(zulFilePathName, assetTabPanel, map);

		} catch (Exception e) {
			logger.error("Exception: ", e);
			Messagebox.show(e.toString());
		}
		logger.debug("Leaving");
	}

	/**
	 * Method for Rendering Fee Details 
	 */
	public void appendFeeDetailsTab(){
		logger.debug("Entering");
		getTreasuaryFinanceService().setFinanceDetails(getFinanceDetail(), "FEE", getRole());

		List<FeeRule> feeRules = getFinanceDetail().getFinScheduleData().getFeeRules();
		List<Rule> feeCharges = getFinanceDetail().getFeeCharges();

		boolean createTab = false;
		if (feeRules != null && !feeRules.isEmpty()) {

			if(tabsIndexCenter.getFellowIfAny("feeDetailTab") == null){
				createTab = true;
			}
		}

		Tabpanel tabpanel = null;
		if(createTab){

			Tab tab = new Tab("Fees");
			tab.setId("feeDetailTab");
			tabsIndexCenter.appendChild(tab);

			tabpanel = new Tabpanel();
			tabpanel.setId("feeDetailTabPanel");
			tabpanel.setStyle("overflow:auto;");
			tabpanel.setParent(tabpanelsBoxIndexCenter);
			tabpanel.setHeight(this.borderLayoutHeight - 100 + "px");
			tab.setVisible(false);
		}else{

			if(tabpanelsBoxIndexCenter.getFellowIfAny("feeDetailTabPanel") != null){
				tabpanel = (Tabpanel) tabpanelsBoxIndexCenter.getFellowIfAny("feeDetailTabPanel");
				tabpanel.setStyle("overflow:auto;");
				tabpanel.getChildren().clear();
			}
		}

		if((feeRules != null && !feeRules.isEmpty())  || feeCharges != null && !feeCharges.isEmpty()){

			//Fee Detail Tab
			final HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("financeMainDialogCtrl", this);
			map.put("financeDetail", getFinanceDetail());
			map.put("profitDaysBasisList", PennantStaticListUtil.getProfitDaysBasis());
			map.put("schMethodList", PennantStaticListUtil.getScheduleMethods());

			if (isWorkFlowEnabled()) {
				map.put("isModify", !getUserWorkspace().isReadOnly("FinanceMainDialog_feeCharge"));
			}else{
				map.put("isModify", true);
			}

			Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/FeeDetailDialog.zul", tabpanel, map);
			Tab tab = null;
			if(tabsIndexCenter.getFellowIfAny("feeDetailTab") != null){
				tab = (Tab) tabsIndexCenter.getFellowIfAny("feeDetailTab");
				tab.setVisible(true);
			}
		}
		logger.debug("Leaving");
	}

	/**
	 * Method for Rendering Schedule Details Data in finance
	 */
	public void appendAccountingDetailTab(boolean onLoad){
		logger.debug("Entering");

		//Accounting Detail Tab

		if(onLoad){
			Tab tab = new Tab("Accounting");
			tab.setId("accountingTab");
			tabsIndexCenter.appendChild(tab);
			ComponentsCtrl.applyForward(tab, "onSelect=onSelectAccountingDetail");

			Tabpanel accountingTabPanel = new Tabpanel();
			accountingTabPanel.setId("accountingTabPanel");
			accountingTabPanel.setStyle("overflow:auto;");
			accountingTabPanel.setParent(tabpanelsBoxIndexCenter);
			accountingTabPanel.setHeight(this.borderLayoutHeight - 100 + "px");
		}else{
			final HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("financeMainDialogCtrl", this);
			map.put("financeDetail", getFinanceDetail());
			map.put("profitDaysBasisList", PennantStaticListUtil.getProfitDaysBasis());
			map.put("schMethodList", PennantStaticListUtil.getScheduleMethods());

			Tabpanel tabpanel = null;
			if(tabpanelsBoxIndexCenter.getFellowIfAny("accountingTabPanel") != null){
				tabpanel = (Tabpanel) tabpanelsBoxIndexCenter.getFellowIfAny("accountingTabPanel");
				tabpanel.setStyle("overflow:auto;");
				tabpanel.getChildren().clear();
			}

			Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/AccountingDetailDialog.zul", tabpanel, map);
		}	

		logger.debug("Leaving");
	}
	
	public void doResetFinProcessDetail(){
		logger.debug("Entering");

		Tabpanel tabpanel = null;
		if(tabpanelsBoxIndexCenter.getFellowIfAny("checkListTabPanel") != null){
			tabpanel = (Tabpanel) tabpanelsBoxIndexCenter.getFellowIfAny("checkListTabPanel");
			tabpanel.setStyle("overflow:auto;");
			tabpanel.getChildren().clear();
		}

		if(tabpanelsBoxIndexCenter.getFellowIfAny("agreementsTabPanelID") != null){
			tabpanel = (Tabpanel) tabpanelsBoxIndexCenter.getFellowIfAny("agreementsTabPanelID");
			tabpanel.setStyle("overflow:auto;");
			tabpanel.getChildren().clear();
		}
		
		Tab tab = null;
		if(tabsIndexCenter.getFellowIfAny("checkListTab") != null){
			tab = (Tab) tabsIndexCenter.getFellowIfAny("checkListTab");
			tab.setVisible(false);
		}
		
		if(tabsIndexCenter.getFellowIfAny("agreementsTab") != null){
			tab = (Tab) tabsIndexCenter.getFellowIfAny("agreementsTab");
			tab.setVisible(false);
		}
		
		logger.debug("Leaving");
	}

	/**
	 * This method set the check list details to aFinanceDetail
	 * 
	 * @param aFinanceDetail
	 * @throws Exception 
	 */
	public boolean doSave_CheckList(FinanceDetail aFinanceDetail) throws Exception {
		logger.debug("Entering ");

		//setFinanceDetail(aFinanceDetail);
		boolean validationSuccess = true;
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("financeMainDialogCtrl", this);
		map.put("financeDetail", aFinanceDetail);
		map.put("userAction", this.userAction.getSelectedItem().getLabel());
		
		try {
			Events.sendEvent("onChkListValidation", checkListChildWindow, map);
		} catch (Exception e) {  
			validationSuccess = false;
			if (e instanceof WrongValuesException) {
				throw e;
			}
		}

		Map<Long, Long> selAnsCountMap = new HashMap<Long, Long>();
		List<FinanceCheckListReference> chkList = getFinanceDetail().getFinanceCheckList();
		selAnsCountMap = getFinanceDetail().getLovDescSelAnsCountMap();

		if (chkList != null && !chkList.isEmpty()) {
			aFinanceDetail.setFinanceCheckList(chkList);
			aFinanceDetail.setLovDescSelAnsCountMap(selAnsCountMap);
		}

		for (FinanceCheckListReference item : chkList) {
			item.setFinReference(this.dealTcktRef.getValue());
			String rcdStatus = aFinanceDetail.getFinScheduleData().getFinanceMain().getRecordStatus();
			if (isWorkFlowEnabled()) {
				if (StringUtils.isBlank(rcdStatus)) {
					if (aFinanceDetail.isNew()) {
						item.setRecordType(PennantConstants.RECORD_TYPE_NEW);
					} else {
						item.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					}
				}
				item.setRecordStatus(userAction.getSelectedItem().getValue().toString());
			} else {
				item.setVersion(item.getVersion() + 1);
			}
			item.setLastMntBy(getUserWorkspace().getLoggedInUser().getLoginUsrID());
			item.setLastMntOn(new Timestamp(System.currentTimeMillis()));
			item.setUserDetails(getUserWorkspace().getLoggedInUser());
		}

		logger.debug("Leaving ");
		return validationSuccess;

	}

	protected FinanceMain doWriteDefaultsToBean(FinanceDetail aFinanceDetail) {
		FinScheduleData finScheduleData = aFinanceDetail.getFinScheduleData();
		FinanceMain aFinanceMain = finScheduleData.getFinanceMain();		
		finScheduleData.setFinReference(aFinanceMain.getFinReference());

		//Setting Default Values
		aFinanceMain.setIncreaseTerms(false);
		aFinanceMain.setRecordStatus(this.recordStatus.getValue());
		aFinanceMain.setFinSourceID(App.CODE);
		aFinanceMain.setFinIsActive(true);

		aFinanceMain.setLastRepayDate(this.startDate.getValue());
		aFinanceMain.setLastRepayPftDate(this.startDate.getValue());
		aFinanceMain.setLastRepayRvwDate(this.startDate.getValue());
		aFinanceMain.setLastRepayCpzDate(this.startDate.getValue());
		aFinanceMain.setLastDepDate(this.startDate.getValue());
		aFinanceMain.setNextRepayDate(this.maturityDate.getValue());
		aFinanceMain.setNextRepayPftDate(this.maturityDate.getValue());
		aFinanceMain.setFinContractDate(this.startDate.getValue());		
		aFinanceMain.setFinPurpose("");

		aFinanceMain.setNumberOfTerms(0);
		aFinanceMain.setAllowGrcPeriod(false);
		aFinanceMain.setAllowGrcPftRvw(false);
		aFinanceMain.setAllowGrcCpz(false);
		aFinanceMain.setAllowRepayRvw(false);
		aFinanceMain.setAllowRepayCpz(false);
		aFinanceMain.setCpzAtGraceEnd(false);
		aFinanceMain.setCalTerms(0);
		aFinanceMain.setDefferments(0);
		aFinanceMain.setPlanDeferCount(0);
		aFinanceMain.setAllowedDefRpyChange(0);
		aFinanceMain.setAvailedDefRpyChange(0);
		aFinanceMain.setAllowedDefFrqChange(0);
		aFinanceMain.setAvailedDefFrqChange(0);
		aFinanceMain.setAllowGrcRepay(false); 
		aFinanceMain.setFinRepayPftOnFrq(false);
		aFinanceMain.setMigratedFinance(false);
		aFinanceMain.setScheduleMaintained(false); 
		aFinanceMain.setScheduleRegenerated(true);
		aFinanceMain.setBlacklisted(false);

		return aFinanceMain;
	}

	/**
	 * Method to add version and record type values to assets
	 * 
	 * @param aFinanceDetail
	 *            (FinanceDetail)
	 * @param isNew
	 *            (boolean)
	 * **/
	public void doSave_Assets(FinanceDetail aFinanceDetail, boolean isNew, String tempRecordStatus,boolean agreement) {
		logger.debug("Entering");

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("financeMainDialogCtrl", this);
		map.put("financeMain", aFinanceDetail.getFinScheduleData().getFinanceMain());
		map.put("userAction", "Confirm");
		if (agreement) {
			map.put("agreement", true);
		}

		Events.sendEvent("onAssetValidation", childWindow, map);

		
		logger.debug("Leaving");
	}


	/** To pass Data For Agreement Child Windows
	 * Used in reflection
	 * @return
	 * @throws Exception 
	 */
	public FinanceDetail getAgrFinanceDetails() throws Exception {
		logger.debug("Entering");

		FinanceDetail aFinanceDetail = new FinanceDetail();
		Cloner cloner = new Cloner();
		aFinanceDetail = cloner.deepClone(getFinanceDetail());

		boolean isNew = false;
		FinanceMain aFinanceMain = aFinanceDetail.getFinScheduleData().getFinanceMain();

		// doWriteComponentsToBean(aFinanceDetail);

		String tempRecordStatus = aFinanceMain.getRecordType();
		isNew = aFinanceDetail.isNew();
		//Finance Asset Loan Details Tab
		if (childWindow != null) {
			doSave_Assets(aFinanceDetail, isNew, tempRecordStatus, true);
		}

		aFinanceMain = aFinanceDetail.getFinScheduleData().getFinanceMain();
		// Write the additional validations as per below example
		// get the selected branch object from the box
		// Do data level validations here

		aFinanceDetail.getFinScheduleData().setFinanceMain(aFinanceMain);
		logger.debug("Leaving");
		return aFinanceDetail;
	}

	public void setCustomerData(Window window) {
		
		String finType = getFinanceDetail().getFinScheduleData().getFinanceMain().getFinType();
		long custId = getFinanceDetail().getFinScheduleData().getFinanceMain().getCustID();
		
		boolean isDataFilled = StringUtils.trimToNull(finType) != null 
				&& (custId != 0) && (custId != Long.MIN_VALUE);

		if (window.getFellowIfAny("agreementsTab") != null && isDataFilled) {
			getFinanceDetail().setAggrementList(getAgreementDetailService().getAggrementDetailList(
					getFinanceDetail().getFinScheduleData().getFinanceMain().getFinType(), FinanceConstants.FINSER_EVENT_ORG,getRole()));
			setAgreementDetails(window);
		}
		if(isDataFilled) {
			appendCheckListDetailTab(false);
		}
	}

	public void showErrorDetails(ArrayList<WrongValueException> wve,Tab tab) {
		logger.debug("Entering");

		if (wve.size() > 0) {
			logger.debug("Throwing occured Errors By using WrongValueException");
			tab.setSelected(true);
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}
		logger.debug("Leaving");
	}

	public String setNotEmpty(String label) {
		return  "NO EMPTY:" + Labels.getLabel( "FIELD_NO_EMPTY", new String[] { Labels.getLabel(label) });
	}
	
	@Override
	protected String getReference() {
		return String.valueOf(this.financeDetail.getFinScheduleData().getFinanceMain().getFinReference());
	}


	/**
	 * when the "buildSchedule" button is clicked. <br>
	 * Stores the default values, sets the validation, validates the given
	 * finance details, builds the schedule.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public FinanceDetail buildSchedule(FinanceDetail financeDetail) {
		logger.debug("Entering");
		FinScheduleData scheduleData = financeDetail.getFinScheduleData();
		FinanceMain financeMain = scheduleData.getFinanceMain();
		scheduleData.setFinReference(financeMain.getFinReference());

		scheduleData.getDisbursementDetails().clear();	
		FinanceDisbursement disbursementDetails = new FinanceDisbursement();
		disbursementDetails.setDisbDate(financeMain.getFinStartDate());
		disbursementDetails.setDisbAmount(financeMain.getFinAmount());
		disbursementDetails.setFeeChargeAmt(financeMain.getFeeChargeAmt());	
		disbursementDetails.setInsuranceAmt(financeMain.getInsuranceAmt());
		
		Date curBDay = DateUtility.getAppDate();
		disbursementDetails.setDisbReqDate(curBDay);

		if(this.disbAcctId != null) {
			disbursementDetails.setDisbAccountId(PennantApplicationUtil.unFormatAccountNumber(this.disbAcctId.getValue()));		
		}

		scheduleData.getDisbursementDetails().add(disbursementDetails);

		financeDetail.setFinScheduleData(ScheduleGenerator.getNewSchd(scheduleData));

		// Prepare Finance Schedule Generator Details List
		financeMain.setScheduleMaintained(false);
		financeMain.setMigratedFinance(false);
		financeMain.setScheduleRegenerated(false);

		// Build Finance Schedule Details List
		if (scheduleData.getFinanceScheduleDetails() != null && !scheduleData.getFinanceScheduleDetails().isEmpty()) {
			financeDetail.setFinScheduleData(ScheduleCalculator.getCalSchd(scheduleData, BigDecimal.ZERO));
			financeMain.setLovDescIsSchdGenerated(true);
		}
		logger.debug("Leaving");
		return financeDetail;
	}
	

	public FinanceDetail  doProcess_Assets(FinanceDetail aFinanceDetail) {
		logger.debug("Entering");
		logger.debug("Leaving");
		return aFinanceDetail;
	}
	



	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//


	public InvestmentFinHeader getInvestmentFinHeader() {
		return investmentFinHeader;
	}
	public void setInvestmentFinHeader(InvestmentFinHeader investmentFinHeader) {
		this.investmentFinHeader = investmentFinHeader;
	}

	public TreasuaryFinHeaderDialogCtrl getTreasuaryFinHeaderDialogCtrl() {
		return treasuaryFinHeaderDialogCtrl;
	}
	public void setTreasuaryFinHeaderDialogCtrl(
			TreasuaryFinHeaderDialogCtrl treasuaryFinHeaderDialogCtrl) {
		this.treasuaryFinHeaderDialogCtrl = treasuaryFinHeaderDialogCtrl;
	}

	public boolean isNewRecord() {
		return newRecord;
	}
	public void setNewRecord(boolean newRecord) {
		this.newRecord = newRecord;
	}

	public boolean isNewInvestment() {
		return newInvestment;
	}
	public void setNewInvestment(boolean newInvestment) {
		this.newInvestment = newInvestment;
	}
	
	public boolean isAssetDataChanged() {
		return assetDataChanged;
	}
	public void setAssetDataChanged(Boolean assetDataChanged) {
		this.assetDataChanged = assetDataChanged;
	}

	public boolean isValidationOn() {
		return validationOn;
	}
	public void setValidationOn(boolean validationOn) {
		this.validationOn = validationOn;
	}

	public TreasuaryFinanceService getTreasuaryFinanceService() {
		return this.treasuaryFinanceService;
	}
	public void setTreasuaryFinanceService(
			TreasuaryFinanceService treasuaryFinanceService) {
		this.treasuaryFinanceService = treasuaryFinanceService;
	}

	public FinanceDetail getFinanceDetail() {
		return financeDetail;
	}
	public void setFinanceDetail(FinanceDetail financeDetail) {
		this.financeDetail = financeDetail;
	}

	public DocumentDetailDialogCtrl getDocumentDetailDialogCtrl() {
		return documentDetailDialogCtrl;
	}
	public void setDocumentDetailDialogCtrl(DocumentDetailDialogCtrl documentDetailDialogCtrl) {
		this.documentDetailDialogCtrl = documentDetailDialogCtrl;
	}

	public AgreementDetailService getAgreementDetailService() {
		return agreementDetailService;
	}
	public void setAgreementDetailService(
			AgreementDetailService agreementDetailService) {
		this.agreementDetailService = agreementDetailService;
	}

	public AccountsService getAccountsService() {
		return accountsService;
	}
	public void setAccountsService(AccountsService accountsService) {
		this.accountsService = accountsService;
	}

	public AccountInterfaceService getAccountInterfaceService() {
		return accountInterfaceService;
	}
	public void setAccountInterfaceService(
			AccountInterfaceService accountInterfaceService) {
		this.accountInterfaceService = accountInterfaceService;
	}

	public void setAgreementDetailDialogCtrl(AgreementDetailDialogCtrl agreementDetailDialogCtrl) {
		this.agreementDetailDialogCtrl = agreementDetailDialogCtrl;
	}
	public AgreementDetailDialogCtrl getAgreementDetailDialogCtrl() {
		return agreementDetailDialogCtrl;
	}
	
	public FinanceCheckListReferenceDialogCtrl getFinanceCheckListReferenceDialogCtrl() {
		return financeCheckListReferenceDialogCtrl;
	}
	public void setFinanceCheckListReferenceDialogCtrl(
			FinanceCheckListReferenceDialogCtrl financeCheckListReferenceDialogCtrl) {
		this.financeCheckListReferenceDialogCtrl = financeCheckListReferenceDialogCtrl;
	}

	public MailUtil getMailUtil() {
		return mailUtil;
	}
	public void setMailUtil(MailUtil mailUtil) {
		this.mailUtil = mailUtil;
	}

}