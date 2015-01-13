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
 * FileName    		:  TreasuaryFinanceDialogCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  04-11-2013    														*
 *                                                                  						*
 * Modified Date    :  04-11-2013    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 04-11-2013       Pennant	                 0.1                                            * 
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

package com.pennant.webui.finance.treasuaryfinance;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataAccessException;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Div;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Row;
import org.zkoss.zul.Space;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Tabpanels;
import org.zkoss.zul.Tabs;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.CurrencyBox;
import com.pennant.ExtendedCombobox;
import com.pennant.app.util.CalculationUtil;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.MailUtil;
import com.pennant.app.util.SystemParameterDetails;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.Notes;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.applicationmaster.Currency;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.InvestmentFinHeader;
import com.pennant.backend.service.finance.TreasuaryFinanceService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.util.ErrorControl;
import com.pennant.util.PennantAppUtil;
import com.pennant.util.Constraint.AmountValidator;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.finance.financemain.TreasuryFinHeaderListCtrl;
import com.pennant.webui.util.ButtonStatusCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.MultiLineMessageBox;
import com.pennant.webui.util.PTMessageUtils;
import com.rits.cloning.Cloner;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * /WEB-INF/pages/Finance/TreasuaryFinance/treasuaryFinanceDialog.zul file. <br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 */
public class TreasuaryFinHeaderDialogCtrl extends GFCBaseCtrl implements
Serializable {

	private static final long serialVersionUID = 1L;
	private final static Logger logger = Logger.getLogger(TreasuaryFinHeaderDialogCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window window_TreasuaryFinHeaderDialog; 		// autowired
	protected Textbox investmentRef; 						// autowired
	protected CurrencyBox totPrinAmt; 						// autowired
	protected Decimalbox totPrinAmttwo; 					// autowired
	protected Space spaceTotPrinAmt; 						// autowired

	protected ExtendedCombobox finCcy; 								// autowired

	protected Combobox profitDaysBasis; 					// autowired
	protected Space  spaceprofitDaysBasis;					// autowired		
	protected Datebox startDate; 							// autowired
	protected Space spaceStartDate; 						// autowired
	protected Datebox maturityDate; 						// autowired
	protected Space spaceMaturityDate; 						// autowired
	protected Decimalbox prinInvested; 						// autowired
	protected Decimalbox prinMaturity; 						// autowired
	protected Decimalbox prinDueToInvest; 					// autowired
	protected Decimalbox avgPftRate;	 					// autowired

	protected Row counterPartyRow_1;						// autowired
	protected Row counterPartyRow_2;						// autowired

	protected Label recordStatus; 							// autowired
	protected Radiogroup userAction;						// autowired
	protected Groupbox groupboxWf;							// autowired
	
	protected Listbox listBoxAddDealTicket;
	protected Groupbox gb_treasuryBasicasicDetails;
	protected Grid grid_BasicDetails;
	protected Div div_DealTicket;

	// Recommendations Tab
	protected Tabs tabsIndexCenter;
	protected Tabpanels tabpanelsBoxIndexCenter;

	// not auto wired vars
	private InvestmentFinHeader investmentFinHeader; // overhanded per param
	private TreasuryFinHeaderListCtrl treasuryFinHeaderListCtrl = null; // overhanded per param
	private List<InvestmentFinHeader> investmentFinHeaderList = new ArrayList<InvestmentFinHeader>();

	// old value vars for edit mode. that we can check if something
	// on the values are edited since the last init.
	private transient String oldVar_finReference;
	private transient BigDecimal oldVar_totPrinAmt;
	private transient String oldVar_lovDescFinCcyName;
	private transient String oldVar_finCcy;
	private transient String oldVar_profitDaysBasis;
	private transient Date oldVar_startDate;
	private transient Date oldVar_maturityDate;
	private transient String oldVar_recordStatus;

	private transient boolean validationOn;
	private transient Object childWindowDialogCtrl = null;

	private boolean notes_Entered = false;

	// Button controller for the CRUD buttons
	private transient final String btnCtroller_ClassPrefix = "button_InvestmentFinHeaderDialog_";
	private transient ButtonStatusCtrl btnCtrl;
	protected Button btnNew; // autowire
	protected Button btnEdit; // autowire
	protected Button btnDelete; // autowire
	protected Button btnSave; // autowire
	protected Button btnCancel; // autowire
	protected Button btnClose; // autowire
	protected Button btnHelp; // autowire
	protected Button btnNotes; // autowire
	protected Button btnAddDealTicket;

	// ServiceDAOs / Domain Classes
	// private transient FinanceDetailService financeDetailService;
	private transient TreasuaryFinanceService treasuaryFinanceService;
	private FinanceDetail financeDetail = null; 
	private List<ValueLabel> listProfitDaysBasis = PennantAppUtil.getProfitDaysBasis(); // autowired
	private HashMap<String, ArrayList<ErrorDetails>> overideMap = new HashMap<String, ArrayList<ErrorDetails>>();

	private  int ccyFormat = 0;

	private BigDecimal totalPrincipalAmt = BigDecimal.ZERO;
	private BigDecimal principalAmt = BigDecimal.ZERO;
	private BigDecimal prinDueToInvestAmt = BigDecimal.ZERO;
	private BigDecimal maturityAmt = BigDecimal.ZERO;
	private BigDecimal avgPftRateRt = BigDecimal.ZERO;
	private String defaultPftDaysBasis = "A/A_360";

	private MailUtil mailUtil;
	/**
	 * default constructor.<br>
	 */
	public TreasuaryFinHeaderDialogCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * zul-file is called with a parameter for a selected TreasuaryFinance
	 * object in a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_TreasuaryFinHeaderDialog(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		try {

			/*
			 * create the Button Controller. Disable not used buttons during
			 * working
			 */
			this.btnCtrl = new ButtonStatusCtrl(getUserWorkspace(), this.btnCtroller_ClassPrefix, true, 
					this.btnNew, this.btnEdit, this.btnDelete, this.btnSave, this.btnCancel, this.btnClose, this.btnNotes);

			// get the params map that are overhanded by creation.
			final Map<String, Object> args = getCreationArgsMap(event);

			// READ OVERHANDED params !
			if (args.containsKey("investmentFinHeader")) {
				this.investmentFinHeader = (InvestmentFinHeader) args.get("investmentFinHeader");
				InvestmentFinHeader befImage = new InvestmentFinHeader();
				BeanUtils.copyProperties(this.investmentFinHeader, befImage);
				this.investmentFinHeader.setBefImage(befImage);
				setInvestmentFinHeader(this.investmentFinHeader);

				ccyFormat = this.investmentFinHeader.getLovDescFinFormatter();
			}

			doLoadWorkFlow(investmentFinHeader.isWorkflow(), investmentFinHeader.getWorkflowId(), investmentFinHeader.getNextTaskId());

			if (isWorkFlowEnabled()) {
				this.userAction = setListRecordStatus(this.userAction);
				getUserWorkspace().alocateRoleAuthorities(getRole(), "InvestmentFinHeaderDialog");
			}

			/* set components visible dependent of the users rights */
			doCheckRights();

			// READ OVERHANDED params !
			// we get the treasuaryFinanceListWindow controller. So we have
			// access
			// to it and can synchronize the shown data when we do insert, edit
			// or
			// delete treasuaryFinance here.
			if (args.containsKey("treasuryFinHeaderListCtrl")) {
				setTreasuryFinHeaderListCtrl((TreasuryFinHeaderListCtrl) args.get("treasuryFinHeaderListCtrl"));
			} else {
				setTreasuryFinHeaderListCtrl(null);
			}

			// set Field Properties
			doSetFieldProperties();
			doShowDialog(getInvestmentFinHeader());
		} catch (Exception e) {
			PTMessageUtils.showErrorMessage(e.toString());
			e.printStackTrace();
			this.window_TreasuaryFinHeaderDialog.onClose();
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");
		// Empty sent any required attributes
		this.investmentRef.setMaxlength(20);
		
		this.finCcy.setMaxlength(3);
        this.finCcy.setMandatoryStyle(true);
		this.finCcy.setModuleName("Currency");
		this.finCcy.setValueColumn("CcyCode");
		this.finCcy.setDescColumn("CcyDesc");
		this.finCcy.setValidateColumns(new String[] { "CcyCode" });

		this.totPrinAmt.setMandatory(true);
		this.totPrinAmt.setMaxlength(18);
		this.totPrinAmt.setFormat(PennantApplicationUtil.getAmountFormate(ccyFormat));
		this.totPrinAmt.setScale(ccyFormat);

		this.totPrinAmttwo.setMaxlength(18);
		this.totPrinAmttwo.setFormat(PennantApplicationUtil.getAmountFormate(ccyFormat));
		this.totPrinAmttwo.setScale(ccyFormat);

		this.prinDueToInvest.setMaxlength(18);
		this.prinDueToInvest.setFormat(PennantApplicationUtil.getAmountFormate(ccyFormat));
		this.prinDueToInvest.setScale(ccyFormat);

		this.prinInvested.setMaxlength(18);
		this.prinInvested.setFormat(PennantApplicationUtil.getAmountFormate(ccyFormat));
		this.prinInvested.setScale(ccyFormat);

		this.prinMaturity.setMaxlength(18);
		this.prinMaturity.setFormat(PennantApplicationUtil.getAmountFormate(ccyFormat));
		this.prinMaturity.setScale(ccyFormat);

		this.avgPftRate.setMaxlength(13);
		this.avgPftRate.setFormat(PennantApplicationUtil.getRateFormate(9));
		this.totPrinAmttwo.setScale(9);

		this.startDate.setFormat(PennantConstants.dateFormat);
		this.maturityDate.setFormat(PennantConstants.dateFormat);

		if (isWorkFlowEnabled()) {
			this.groupboxWf.setVisible(true);
		} else {
			this.groupboxWf.setVisible(false);
		}

		logger.debug("Leaving");
	}

	/**
	 * User rights check. <br>
	 * Only components are set visible=true if the logged-in <br>
	 * user have the right for it. <br>
	 * 
	 * The rights are get from the spring framework users grantedAuthority(). A
	 * right is only a string. <br>
	 */
	private void doCheckRights() {
		logger.debug("Entering");

		getUserWorkspace().alocateAuthorities("InvestmentFinHeaderDialog",getRole());

		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_InvestmentFinHeaderDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_InvestmentFinHeaderDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_InvestmentFinHeaderDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_InvestmentFinHeaderDialog_btnSave"));
		this.btnCancel.setVisible(false);

		this.btnAddDealTicket.setVisible(getUserWorkspace().isAllowed("button_InvestmentFinHeaderDialog_btnDeal"));
		this.div_DealTicket.setVisible(getUserWorkspace().isAllowed("button_InvestmentFinHeaderDialog_btnDeal"));
		
		if("Approved".equals(getInvestmentFinHeader().getRecordStatus())) {
			this.btnAddDealTicket.setVisible(false);
		}
		
		this.listBoxAddDealTicket.setVisible(getUserWorkspace().isAllowed("InvestmentFinHeaderDialog_DealList"));
		this.finCcy.setReadonly(!getUserWorkspace().isAllowed("InvestmentFinHeaderDialog_finCcy"));
		logger.debug("Leaving");
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// +++++++++++++++++++++++ Components events +++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * If we close the dialog window. <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClose$window_TreasuaryFinHeaderDialog(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		doClose();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "save" button is clicked. <br>
	 * 
	 * @param event
	 * @throws Exception 
	 */
	public void onClick$btnSave(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		doSave();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "edit" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnEdit(Event event) {
		logger.debug("Entering" + event.toString());
		doEdit();

		// remember the old vars
		doStoreInitValues();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "help" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnHelp(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		PTMessageUtils.showHelpWindow(event, window_TreasuaryFinHeaderDialog);
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "new" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnNew(Event event) {
		logger.debug("Entering" + event.toString());
		doNew();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "delete" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnDelete(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		doDelete();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "cancel" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnCancel(Event event) {
		logger.debug("Entering" + event.toString());
		doCancel();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "close" button is clicked. <br>
	 * 
	 * @param event
	 * @throws Exception 
	 */
	public void onClick$btnClose(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		try {
			doClose();
		} catch (final WrongValuesException e) {
			throw e;
		}
		logger.debug("Leaving" + event.toString());
	}

	// GUI Process

	/**
	 * Closes the dialog window. <br>
	 * <br>
	 * Before closing we check if there are unsaved changes in <br>
	 * the components and ask the user if saving the modifications. <br>
	 * @throws Exception 
	 * 
	 */
	private void doClose() throws Exception {
		logger.debug("Entering");
		boolean close = true;
		if (isDataChanged()) {
			logger.debug("isDataChanged : true");

			// Show a confirm box
			final String msg = Labels.getLabel("message_Data_Modified_Save_Data_YesNo");
			final String title = Labels.getLabel("message.Information");

			MultiLineMessageBox.doSetTemplate();
			int conf = MultiLineMessageBox.show(msg, title, MultiLineMessageBox.YES | MultiLineMessageBox.NO,
					MultiLineMessageBox.QUESTION, true);

			if (conf == MultiLineMessageBox.YES) {
				logger.debug("doClose: Yes");
				doSave();
				close = false;
			} else {
				logger.debug("doClose: No");
			}
		} else {
			logger.debug("isDataChanged : false");
		}

		if (close) {
			closeDialog(this.window_TreasuaryFinHeaderDialog, "InvestmentFinHeaderDialog");
		}

		logger.debug("Leaving");
	}

	/**
	 * Cancel the actual operation. <br>
	 * <br>
	 * Resets to the original status.<br>
	 * 
	 */
	private void doCancel() {
		logger.debug("Entering");
		doResetInitValues();
		doReadOnly();
		this.btnCtrl.setInitEdit();
		logger.debug("Leaving");
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aTreasuaryFinance
	 *            TreasuaryFinance
	 */
	public void doWriteBeanToComponents(InvestmentFinHeader aFinHeader) {
		logger.debug("Entering");

		this.investmentRef.setValue(aFinHeader.getInvestmentRef());
		this.totPrinAmt.setValue(PennantAppUtil.formateAmount(aFinHeader.getTotPrincipalAmt(), ccyFormat));
		this.totPrinAmttwo.setValue(PennantAppUtil.formateAmount(aFinHeader.getTotPrincipalAmt(), ccyFormat));
	
		if(aFinHeader.getFinCcy() != null){
			this.finCcy.setDescription(StringUtils.trimToEmpty(aFinHeader.getLovDescFinCcyName()));
		}
		if(aFinHeader.isNewRecord()){
			Currency currency = PennantAppUtil.getCurrencyBycode("BHD");
			this.finCcy.setValue(currency.getCcyCode());
			this.finCcy.setDescription(currency.getCcyDesc());
			ccyFormat = currency.getCcyEditField();
			getInvestmentFinHeader().setLovDescFinFormatter(ccyFormat);
			doSetFieldProperties();
		}else{
			this.finCcy.setValue((aFinHeader.getFinCcy()));
		}
		
		String pftDaysBasis = aFinHeader.getProfitDaysBasis();
		if(StringUtils.trimToEmpty(pftDaysBasis).equals("")){
			pftDaysBasis = defaultPftDaysBasis;
		}
        
		fillComboBox(this.profitDaysBasis, pftDaysBasis, listProfitDaysBasis, "");
		this.startDate.setValue(aFinHeader.getStartDate());
		this.maturityDate.setValue(aFinHeader.getMaturityDate());		
		this.prinMaturity.setValue(PennantAppUtil.formateAmount(aFinHeader.getPrincipalMaturity(), ccyFormat));		
	//	this.approvalRequired.setChecked(approvalRequiredFlag);
	//	approvalRequiredFlag = this.approvalRequired.isChecked();
		this.recordStatus.setValue(aFinHeader.getRecordStatus());

		List<FinanceDetail> finDetailsList = aFinHeader.getFinanceDetailsList();
		if (finDetailsList != null && !finDetailsList.isEmpty()) {
			doFillTicketDetails(finDetailsList);
		}

		this.prinInvested.setValue(PennantAppUtil.formateAmount(principalAmt, ccyFormat));
		prinDueToInvestAmt = aFinHeader.getTotPrincipalAmt().subtract(principalAmt);
		this.prinDueToInvest.setValue(PennantAppUtil.formateAmount(prinDueToInvestAmt, ccyFormat));
		this.prinMaturity.setValue(PennantAppUtil.formateAmount(maturityAmt, ccyFormat));
		this.avgPftRate.setValue(PennantApplicationUtil.formatRate(avgPftRateRt.doubleValue(), 9));
		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aInvestmentFinHeader
	 */
	public void doWriteComponentsToBean(InvestmentFinHeader aInvestmentFinHeader, boolean isSave) {
		logger.debug("Entering");
		doSetLOVValidation();
		doFillTicketDetails(aInvestmentFinHeader.getFinanceDetailsList());
		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		aInvestmentFinHeader.setInvestmentRef(this.investmentRef.getValue());
		try {
			if (this.totPrinAmt.getValue() != null) {
				aInvestmentFinHeader.setTotPrincipalAmt(PennantApplicationUtil.unFormateAmount(this.totPrinAmt.getValue(), ccyFormat));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aInvestmentFinHeader.setLovDescFinCcyName(this.finCcy.getDescription());
			if (this.finCcy.getValue().equals("")) {
				throw new WrongValueException( this.finCcy, Labels.getLabel( "FIELD_NO_INVALID", 
						new String[] { Labels .getLabel("label_TreasuaryFinHeaderDialog_finCcy.value") }));
			} else {
				aInvestmentFinHeader.setFinCcy(this.finCcy.getValue());
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {

			if (getComboboxValue(this.profitDaysBasis).equals("#")) {
				throw new WrongValueException(this.profitDaysBasis, Labels.getLabel("STATIC_INVALID",
						new String[] { Labels.getLabel("label_TreasuaryFinHeaderDialog_ProfitDaysBasis.value") }));
			}

			aInvestmentFinHeader.setProfitDaysBasis(getComboboxValue(this.profitDaysBasis));
			aInvestmentFinHeader.setLovDescProfitDaysBasisName(this.profitDaysBasis.getSelectedItem().getLabel());


		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aInvestmentFinHeader.setStartDate(this.startDate.getValue());
			if(!this.startDate.isReadonly()){
				if (DateUtility.compare(this.startDate.getValue(),
						this.maturityDate.getValue()) >= 0) {
					throw new WrongValueException(this.maturityDate, Labels.getLabel("DATE_ALLOWED_AFTER",
							new String[] {Labels.getLabel("label_TreasuaryFinHeaderDialog_MaturityDate.value"),
							Labels.getLabel("label_TreasuaryFinHeaderDialog_StartDate.value") }));
				}
				/*if (DateUtility.compare(this.startDate.getValue(),
						(Date) SystemParameterDetails.getSystemParameterValue(PennantConstants.APP_DATE_CUR)) == -1) {
					throw new WrongValueException(this.startDate,Labels.getLabel("DATE_PAST",
							new String[] {Labels.getLabel("label_TreasuaryFinHeaderDialog_StartDate.value") }));
				}*/
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aInvestmentFinHeader.setMaturityDate(this.maturityDate.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if(this.counterPartyRow_2.isVisible() && isSave &&
					PennantApplicationUtil.unFormateAmount(this.prinDueToInvest.getValue(), ccyFormat).compareTo(BigDecimal.ZERO) < 0){
			
				throw new WrongValueException(this.prinDueToInvest,Labels.getLabel("AMOUNT_NO_LESS",
						new String[] {Labels.getLabel("label_TreasuaryFinHeaderDialog_PrinDueToInvest.value"), "0" }));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		aInvestmentFinHeader.setPrincipalInvested(PennantApplicationUtil.unFormateAmount(this.prinInvested.getValue(), ccyFormat));
		aInvestmentFinHeader.setPrincipalMaturity(PennantApplicationUtil.unFormateAmount(this.prinMaturity .getValue(), ccyFormat));
		aInvestmentFinHeader.setPrincipalDueToInvest(PennantApplicationUtil.unFormateAmount(this.prinDueToInvest.getValue(), ccyFormat));

		aInvestmentFinHeader.setAvgPftRate(this.avgPftRate.getValue());

		//Validation Errors catching
		showErrorDetails(wve);

		aInvestmentFinHeader.setRecordStatus(this.recordStatus.getValue());
		setInvestmentFinHeader(aInvestmentFinHeader);
		logger.debug("Leaving");
	}

	/**
	 * Method to show error details if occurred
	 * 
	 **/
	private void showErrorDetails(ArrayList<WrongValueException> wve) {
		logger.debug("Entering");

		if (wve.size() > 0) {
			logger.debug("Throwing occured Errors By using WrongValueException");
			doRemoveValidation();
			doRemoveLOVValidation();

			// groupBox.set
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}
		logger.debug("Leaving");
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the
	 * readOnly mode accordingly.
	 * 
	 * @param aTreasuaryFinance
	 * @throws InterruptedException
	 */
	public void doShowDialog(InvestmentFinHeader aInvestmentFinHeader) throws InterruptedException {
		logger.debug("Entering");

		// if aTreasuaryFinance == null then we opened the Dialog without
		// args for a given entity, so we get a new Obj().
		if (aInvestmentFinHeader == null) {
			/** !!! DO NOT BREAK THE TIERS !!! */
			// We don't create a new DomainObject() in the frontend.
			// We GET it from the backend.
			aInvestmentFinHeader = getTreasuaryFinanceService().getTreasuaryFinance();
		} 

		setInvestmentFinHeader(aInvestmentFinHeader);

		// set Readonly mode accordingly if the object is new or not.
		if (aInvestmentFinHeader.isNew()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.finCcy.focus();
		} else {
			this.startDate.focus();
			if (isWorkFlowEnabled()) {
				this.btnNotes.setVisible(false);
				doEdit();
			} else {
				this.btnCtrl.setInitEdit();
				doReadOnly();
				btnCancel.setVisible(false);
			}
		}

		doSetFieldType();	

		try {
			// fill the components with the data
			doWriteBeanToComponents(aInvestmentFinHeader);

			// stores the initial data for comparing if they are changed
			// during user action.
			doStoreInitValues();

			getBorderLayoutHeight();
			this.listBoxAddDealTicket.setHeight(getListBoxHeight(this.grid_BasicDetails.getRows().getVisibleItemCount() +2));

			appendRecommendDetailTab();
			setDialog(this.window_TreasuaryFinHeaderDialog);
		} catch (final Exception e) {
			logger.error(e);
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving");
	}

	private void doSetFieldType() {
		if (this.finCcy.isReadonly()) {
			this.finCcy.setMandatoryStyle(false);
		}

		if (this.totPrinAmt.isReadonly()) {
			this.totPrinAmt.setMandatory(false);
		}

		if (this.profitDaysBasis.isDisabled()) {
			this.spaceprofitDaysBasis.setSclass("");
		}

		if (this.startDate.isReadonly()) {
			this.spaceStartDate.setSclass("");
			this.startDate.setFormat("");
		}

		if (this.maturityDate.isReadonly()) {
			this.spaceMaturityDate.setSclass("");
			this.maturityDate.setFormat("");
		}
	}

	/**
	 * Method for Append Recommend Details Tab
	 * 
	 * @throws InterruptedException
	 */
	private void appendRecommendDetailTab() throws InterruptedException {
		logger.debug("Entering");

		// Memo Tab Details -- Comments or Recommendations
	//	 this.btnNotes.setVisible(false);

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
		map.put("notes", getNotes());
		map.put("control", this);

		try {
			Executions.createComponents("/WEB-INF/pages/notes/notes.zul",
					tabpanel, map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / "
					+ e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving");
	}


	/**
	 * To set the customer id from Customer filter
	 * 
	 * @param nCustomer
	 * @throws InterruptedException
	 */


	public void onChange$startDate(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		if (DateUtility.compare(this.startDate.getValue(),
				this.maturityDate.getValue()) >= 0) {
			throw new WrongValueException(this.maturityDate,Labels.getLabel("DATE_ALLOWED_AFTER",
					new String[] {Labels.getLabel("label_TreasuaryFinHeaderDialog_MaturityDate.value"),
					Labels.getLabel("label_TreasuaryFinHeaderDialog_StartDate.value") }));
		}
		if (DateUtility.compare(this.startDate.getValue(),
				(Date) SystemParameterDetails.getSystemParameterValue(PennantConstants.APP_DATE_CUR)) == -1) {
			throw new WrongValueException(this.startDate,Labels.getLabel("DATE_PAST",
					new String[] {Labels.getLabel("label_TreasuaryFinHeaderDialog_StartDate.value") }));
		}

		doFillTicketDetails(getInvestmentFinHeader().getFinanceDetailsList());
		logger.debug("Leaving" + event.toString());
	}

	public void onChange$maturityDate(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		if (DateUtility.compare(this.startDate.getValue(),
				this.maturityDate.getValue()) >= 0) {
			throw new WrongValueException(this.maturityDate,Labels.getLabel("DATE_ALLOWED_AFTER",
					new String[] {Labels.getLabel("label_TreasuaryFinHeaderDialog_MaturityDate.value"),
					Labels.getLabel("label_TreasuaryFinHeaderDialog_StartDate.value") }));
		}

		doFillTicketDetails(getInvestmentFinHeader().getFinanceDetailsList());
		logger.debug("Leaving" + event.toString());
	}


	public void doFillTicketDetails(List<FinanceDetail> finDetailList) {
		logger.debug("Entering");	

		Date startDate = this.startDate.getValue();
		Date maturityDate = this.maturityDate.getValue();
		String profitDaysBasis = getInvestmentFinHeader().getProfitDaysBasis();

		totalPrincipalAmt = getInvestmentFinHeader().getTotPrincipalAmt();
		principalAmt = BigDecimal.ZERO;
		prinDueToInvestAmt = BigDecimal.ZERO;;
		maturityAmt = BigDecimal.ZERO;
		avgPftRateRt = BigDecimal.ZERO;

		this.listBoxAddDealTicket.getItems().clear();
		getInvestmentFinHeader().setFinanceDetailsList(finDetailList);

		for (FinanceDetail finDetail : finDetailList) {
			FinanceMain financeMain = finDetail.getFinScheduleData().getFinanceMain();

			Listitem listitem = new Listitem();
			Listcell listcell;

			listcell = new Listcell(financeMain.getFinType());
			listitem.appendChild(listcell);

			listcell = new Listcell(financeMain.getFinReference());
			listitem.appendChild(listcell);

			listcell = new Listcell(String.valueOf(financeMain.getLovDescCustCIF())); 
			listitem.appendChild(listcell);

			listcell = new Listcell(String.valueOf(financeMain.getLovDescCustShrtName()));
			listitem.appendChild(listcell);

			listcell = new Listcell(PennantAppUtil.amountFormate(financeMain.getFinAmount(), ccyFormat));
			listcell.setStyle("text-align:right");
			listitem.appendChild(listcell);

			BigDecimal finAmount = financeMain.getFinAmount() ;
			BigDecimal profitRate = financeMain.getRepayProfitRate();
			BigDecimal profitAmt = CalculationUtil.calInterest(financeMain.getFinStartDate(),
					financeMain.getMaturityDate(), finAmount, profitDaysBasis, profitRate);
			profitAmt = profitAmt.setScale(0, RoundingMode.HALF_DOWN);
			financeMain.setTotalRepayAmt(finAmount.add(profitAmt));

			listcell = new Listcell(PennantApplicationUtil.formatRate(financeMain.getRepayProfitRate().doubleValue(), 9));
			listcell.setStyle("text-align:right");
			listitem.appendChild(listcell);

			listcell = new Listcell(PennantAppUtil.amountFormate(financeMain.getTotalRepayAmt(), ccyFormat));
			listcell.setStyle("text-align:right");
			listitem.appendChild(listcell);

			listcell = new Listcell(financeMain.getRecordStatus());
			listcell.setParent(listitem);
			listcell = new Listcell(financeMain.getRecordType());
			listcell.setParent(listitem);

			listitem.setAttribute("data", finDetail);
			ComponentsCtrl.applyForward(listitem, "onDoubleClick=onInvestmentItemDoubleClicked");

			this.listBoxAddDealTicket.appendChild(listitem);

			principalAmt = principalAmt.add(financeMain.getFinAmount());
			maturityAmt = maturityAmt.add(financeMain.getTotalRepayAmt());
		}
		prinDueToInvestAmt = totalPrincipalAmt.subtract(principalAmt);
		avgPftRateRt = CalculationUtil.calcAvgProfitRate(startDate, maturityDate, profitDaysBasis, principalAmt, maturityAmt);

		this.prinInvested.setValue(PennantAppUtil.formateAmount(principalAmt, ccyFormat));	
		this.prinDueToInvest.setValue(PennantAppUtil.formateAmount(prinDueToInvestAmt, ccyFormat));
		this.prinMaturity.setValue(PennantAppUtil.formateAmount(maturityAmt, ccyFormat));
		this.avgPftRate.setValue(PennantApplicationUtil.formatRate(avgPftRateRt.doubleValue(), 9));

		getInvestmentFinHeader().setAvgPftRate(avgPftRateRt);
		logger.debug("Leaving");	
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// ++++++++++++++++++++++++++++++ helpers ++++++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * Stores the init values in mem vars. <br>
	 */
	private void doStoreInitValues() {
		logger.debug("Entering");
		this.oldVar_finReference = this.investmentRef.getValue();
		this.oldVar_totPrinAmt = this.totPrinAmt.getValue();
		this.oldVar_lovDescFinCcyName = this.finCcy.getDescription();
		this.oldVar_finCcy = this.finCcy.getValue();
		this.oldVar_profitDaysBasis = this.profitDaysBasis.getValue();
		this.oldVar_startDate = this.startDate.getValue();
		this.oldVar_maturityDate = this.maturityDate.getValue();
		this.oldVar_recordStatus = this.recordStatus.getValue();
		logger.debug("Leaving");
	}

	/**
	 * Resets the init values from mem vars. <br>
	 */
	private void doResetInitValues() {
		logger.debug("Entering");
		this.investmentRef.setValue(this.oldVar_finReference);
		this.totPrinAmt.setValue(this.oldVar_totPrinAmt);
		this.finCcy.setDescription(this.oldVar_lovDescFinCcyName);
		this.finCcy.setValue(this.oldVar_finCcy);
		this.profitDaysBasis.setValue(this.oldVar_profitDaysBasis);
		this.startDate.setValue(this.oldVar_startDate);
		this.maturityDate.setValue(this.oldVar_maturityDate);
		this.recordStatus.setValue(this.oldVar_recordStatus);

		if (isWorkFlowEnabled()) {
			this.userAction.setSelectedIndex(0);
		}
		logger.debug("Leaving");
	}

	/**
	 * Checks, if data are changed since the last call of <br>
	 * doStoreInitData() . <br>
	 * 
	 * @return true, if data are changed, otherwise false
	 */
	private boolean isDataChanged() {
		logger.debug("Entering");

		// To clear the Error Messages
		doClearMessage();

		if (this.oldVar_finReference != this.investmentRef.getValue()) {
			return true;
		}
		if (this.oldVar_totPrinAmt != this.totPrinAmt.getValue()) {
			return true;
		}
		if (this.oldVar_finCcy != this.finCcy.getValue()) {
			return true;
		}
		if (this.oldVar_profitDaysBasis != this.profitDaysBasis.getValue()) {
			return true;
		}
		String oldStartDate = "";
		String newStartDate = "";
		if (this.oldVar_startDate != null) {
			oldStartDate = DateUtility.formatDate(this.oldVar_startDate,PennantConstants.dateFormat);
		}
		if (this.startDate.getValue() != null) {
			newStartDate = DateUtility.formatDate(this.startDate.getValue(),PennantConstants.dateFormat);
		}
		if (!StringUtils.trimToEmpty(oldStartDate).equals(StringUtils.trimToEmpty(newStartDate))) {
			return true;
		}
		String oldMaturityDate = "";
		String newMaturityDate = "";
		if (this.oldVar_maturityDate != null) {
			oldMaturityDate = DateUtility.formatDate(this.oldVar_maturityDate,PennantConstants.dateFormat);
		}
		if (this.maturityDate.getValue() != null) {
			newMaturityDate = DateUtility.formatDate(this.maturityDate.getValue(), PennantConstants.dateFormat);
		}
		if (!StringUtils.trimToEmpty(oldMaturityDate).equals(StringUtils.trimToEmpty(newMaturityDate))) {
			return true;
		}

		logger.debug("Leaving");
		return false;
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug("Entering");

		setValidationOn(true);

		if (!this.totPrinAmt.isReadonly()) {
			this.totPrinAmt.setConstraint(new AmountValidator(18, ccyFormat, Labels.getLabel("label_TreasuaryFinHeaderDialog_TotPrinAmt.value"), false));
		}

		if (!this.profitDaysBasis.isDisabled()) {			
			this.profitDaysBasis .setConstraint(setNotEmpty("label_TreasuaryFinHeaderDialog_ProfitDaysBasis.value"));
		}

		if (!this.startDate.isDisabled()) {			
			this.startDate.setConstraint(setNotEmpty("label_TreasuaryFinHeaderDialog_StartDate.value"));
		}

		if (!this.maturityDate.isDisabled()) {
			this.maturityDate.setConstraint(setNotEmpty("label_TreasuaryFinHeaderDialog_MaturityDate.value"));
		}

		logger.debug("Leaving");
	}

	private String setNotEmpty(String label) {
		return  "NO EMPTY:" + Labels.getLabel( "FIELD_NO_EMPTY", new String[] { Labels.getLabel(label) });
	}

	/**
	 * Disables the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering");
		setValidationOn(false);
		this.totPrinAmt.setConstraint("");
		this.finCcy.setConstraint("");
		this.profitDaysBasis.setConstraint("");
		this.startDate.setConstraint("");
		this.maturityDate.setConstraint("");
		this.prinDueToInvest.setConstraint("");

		logger.debug("Leaving");
	}

	private void doSetLOVValidation() {		
		if(finCcy.isButtonVisible()) {
			this.finCcy.setConstraint(new PTStringValidator (Labels.getLabel("label_TreasuaryFinHeaderDialog_finCcy.value"),null,true,true));
		}
	}

	private void doRemoveLOVValidation() {
		this.finCcy.setConstraint("");
	}

	private void doClearMessage() {
		logger.debug("Entering");
		this.investmentRef.setErrorMessage("");
		this.totPrinAmt.setErrorMessage("");
		this.finCcy.setErrorMessage("");
		this.finCcy.setErrorMessage("");
		this.profitDaysBasis.setErrorMessage("");
		this.startDate.setErrorMessage("");
		this.maturityDate.setErrorMessage("");
		this.prinDueToInvest.setErrorMessage("");
		logger.debug("Leaving");
	}

	private void refreshList() {
		final JdbcSearchObject<InvestmentFinHeader> aTreasuaryFinHeader = getTreasuryFinHeaderListCtrl().getSearchObj();
		getTreasuryFinHeaderListCtrl().pagingTFinHeaderList.setActivePage(0);
		getTreasuryFinHeaderListCtrl().getPagedListWrapper().setSearchObject(aTreasuaryFinHeader);

		if (getTreasuryFinHeaderListCtrl().listBoxTrFinHeader != null) {
			getTreasuryFinHeaderListCtrl().listBoxTrFinHeader.getListModel();
		}
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// +++++++++++++++++++++++++ crud operations +++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * Deletes a TreasuaryFinance object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug("Entering");
		InvestmentFinHeader aTreasuaryFinHeader = new InvestmentFinHeader();
		BeanUtils.copyProperties(getInvestmentFinHeader(), aTreasuaryFinHeader);
		String tranType = PennantConstants.TRAN_WF;

		aTreasuaryFinHeader.setUserAction(this.userAction.getSelectedItem()
				.getLabel());

		// Show a confirm box
		final String msg = Labels
		.getLabel("message.Question.Are_you_sure_to_delete_this_record")
		+ "\n\n --> " + aTreasuaryFinHeader.getInvestmentRef();
		final String title = Labels.getLabel("message.Deleting.Record");
		MultiLineMessageBox.doSetTemplate();

		int conf = (MultiLineMessageBox.show(msg, title,
				MultiLineMessageBox.YES | MultiLineMessageBox.NO,
				Messagebox.QUESTION, true));

		if (conf == MultiLineMessageBox.YES) {
			logger.debug("doDelete: Yes");

			if (StringUtils.trimToEmpty(aTreasuaryFinHeader.getRecordType()).equals("")) {
				aTreasuaryFinHeader.setVersion(aTreasuaryFinHeader.getVersion() + 1);
				aTreasuaryFinHeader.setRecordType(PennantConstants.RECORD_TYPE_DEL);

				if (isWorkFlowEnabled()) {
					aTreasuaryFinHeader.setNewRecord(true);
					tranType = PennantConstants.TRAN_WF;
				} else {
					tranType = PennantConstants.TRAN_DEL;
				}
			}

			try {
				if (doProcess(aTreasuaryFinHeader, tranType)) {
					refreshList();
					closeDialog(this.window_TreasuaryFinHeaderDialog, "InvestmentFinHeaderDialog");
				}

			} catch (DataAccessException e) {
				logger.error("doDelete " + e);
				showErrorMessage(this.window_TreasuaryFinHeaderDialog, e);
			}

		}
		logger.debug("Leaving");
	}

	/**
	 * Create a new TreasuaryFinance object. <br>
	 */
	private void doNew() {
		logger.debug("Entering");

		// remember the old vars
		doStoreInitValues();

		final InvestmentFinHeader aTreasuaryFinHeader = getTreasuaryFinanceService().getNewTreasuaryFinance();
		aTreasuaryFinHeader.setApprovalRequired(true); // init
		aTreasuaryFinHeader.setNewRecord(true);
		setInvestmentFinHeader(aTreasuaryFinHeader);
		doClear(); // clear all commponents
		doEdit(); // edit mode
		this.btnCtrl.setBtnStatus_New();

		// setFocus
		this.totPrinAmt.focus();
		logger.debug("Leaving");
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug("Entering");

		if (getInvestmentFinHeader().isNewRecord()) {
			this.btnCancel.setVisible(false);
		} else {
			this.btnCancel.setVisible(true);
		}

		if (isReadOnly("InvestmentFinHeaderDialog_totPrinAmt")) {
			this.totPrinAmt.setVisible(false);
			this.totPrinAmttwo.setVisible(true);
			this.spaceTotPrinAmt.setVisible(true);
		}

		this.finCcy.setReadonly(isReadOnly("InvestmentFinHeaderDialog_finCcy"));
		readOnlyComponent(isReadOnly("InvestmentFinHeaderDialog_finCcy"), this.finCcy);
		readOnlyComponent(isReadOnly("InvestmentFinHeaderDialog_profitDaysBasis"), this.profitDaysBasis); 
		readOnlyComponent(isReadOnly("InvestmentFinHeaderDialog_startDate"), this.startDate);
		readOnlyComponent(isReadOnly("InvestmentFinHeaderDialog_maturityDate"), this.maturityDate);
		readOnlyComponent(isReadOnly("InvestmentFinHeaderDialog_prinInvested"), this.prinInvested);
		readOnlyComponent(isReadOnly("InvestmentFinHeaderDialog_prinMaturity"), this.prinMaturity);
		readOnlyComponent(isReadOnly("InvestmentFinHeaderDialog_prinDueToInvest"),this.prinDueToInvest);

		if (isReadOnly("InvestmentFinHeaderDialog_counterParty_Fields")) {
			this.counterPartyRow_1.setVisible(false);
			this.counterPartyRow_2.setVisible(false);
		}


		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}

			if (this.investmentFinHeader.isNewRecord()) {
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(false);
			} else {
				this.btnCtrl.setWFBtnStatus_Edit(isFirstTask());
			}
		} else {
			this.btnCtrl.setBtnStatus_Edit();
			btnCancel.setVisible(true);
		}

		logger.debug("Leaving");
	}

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly() {
		logger.debug("Entering");
		readOnlyComponent(true, this.totPrinAmt);
		readOnlyComponent(true, this.finCcy);
		readOnlyComponent(true, this.profitDaysBasis);
		readOnlyComponent(true, this.startDate);
		readOnlyComponent(true, this.maturityDate);

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(true);
			}
		}

		if (isWorkFlowEnabled()) {
			this.recordStatus.setValue("");
			this.userAction.setSelectedIndex(0);
		}
		logger.debug("Leaving");
	}

	/**
	 * Clears the components values. <br>
	 */
	public void doClear() {
		logger.debug("Entering");
		// remove validation, if there are a save before

		this.totPrinAmt.setValue("");
		this.finCcy.setValue("");
		this.finCcy.setValue("");
		this.profitDaysBasis.setValue("");
		this.startDate.setText("");
		this.maturityDate.setText("");
		logger.debug("Leaving");
	}


	/**
	 * when clicks on button "SearchFinCcy"
	 * 
	 * @param event
	 */
	public void onFulfill$finCcy(Event event) {
		logger.debug("Entering " + event.toString());

		this.finCcy.setConstraint("");
		Object dataObject = finCcy.getObject();

		if (dataObject instanceof String) {
			this.finCcy.setValue(dataObject.toString());
			this.finCcy.setDescription("");

		} else {
			Currency details = (Currency) dataObject;
			if (details != null) {
				this.finCcy.setValue(details.getCcyCode());
				this.finCcy.setDescription(details.getCcyDesc());
				ccyFormat = details.getCcyEditField();
				doSetFieldProperties();
			}
		}

		logger.debug("Leaving " + event.toString());
	}

	// Add Ticket Details
	public void onClick$btnAddDealTicket(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		financeDetail = new FinanceDetail();
		doWriteComponentsToBean(getInvestmentFinHeader(), false);

		ErrorDetails errorDetail = null;
		errorDetail = getTreasuaryFinanceService().treasuryFinHeaderDialogValidations(getInvestmentFinHeader(),  getUserWorkspace().getUserLanguage());

		if (errorDetail != null) {
			try {
				PTMessageUtils.showErrorMessage(errorDetail.getError());
				return ;
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}else{

			FinanceMain financeMain = new FinanceMain();
			financeMain.setNewRecord(true);

			financeDetail.getFinScheduleData().setFinanceMain(financeMain);		
			getInvestmentFinHeader().setFinanceDetail(financeDetail);

			final HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("treasuaryFinHeaderDialogCtrl", this);
			map.put("newRecord", "true");
			map.put("investmentFinHeader", getInvestmentFinHeader());

			try {
				Executions.createComponents(
						"/WEB-INF/pages/Finance/FinanceMain/InvestmentDealDialog.zul",
						window_TreasuaryFinHeaderDialog, map);
			} catch (final Exception e) {
				logger.error("onOpenWindow:: error opening window / " + e.getMessage());
				PTMessageUtils.showErrorMessage(e.toString());
			}
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Method for Double Click Event on Contribution Details
	 * @param event
	 * @throws Exception
	 */
	public void onInvestmentItemDoubleClicked(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		// get the selected invoiceHeader object
		final Listitem item = this.listBoxAddDealTicket.getSelectedItem();
		doWriteComponentsToBean(getInvestmentFinHeader() , false);

		if (item != null) {
			// CAST AND STORE THE SELECTED OBJECT
			final FinanceDetail financeDetail = (FinanceDetail) item.getAttribute("data");

			FinanceMain financeMain = financeDetail.getFinScheduleData().getFinanceMain();
			if(financeMain.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW) && !financeDetail.isDataFetchComplete()){
				getTreasuaryFinanceService().getFinanceDetailById(financeDetail, financeMain.getFinReference());
			}
			getInvestmentFinHeader().setFinanceDetail(financeDetail);

			if (financeMain.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
				PTMessageUtils.showErrorMessage(Labels.getLabel("common_NoMaintainance"));
			} else {
				final HashMap<String, Object> map = new HashMap<String, Object>();
				map.put("treasuaryFinHeaderDialogCtrl", this);
				map.put("investmentFinHeader", getInvestmentFinHeader());

				// call the zul-file with the parameters packed in a map
				try {
					Executions.createComponents(
							"/WEB-INF/pages/Finance/FinanceMain/InvestmentDealDialog.zul",
							window_TreasuaryFinHeaderDialog, map);
				} catch (final Exception e) {
					logger.error("onOpenWindow:: error opening window / "+ e.getMessage());
					PTMessageUtils.showErrorMessage(e.toString());
				}
			}
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Saves the components to table. <br>
	 * @throws Exception 
	 */
	public void doSave() throws Exception {
		logger.debug("Entering");
		InvestmentFinHeader aInvestmentFinHeader = new InvestmentFinHeader();
		Cloner cloner = new Cloner();
		aInvestmentFinHeader = cloner.deepClone(getInvestmentFinHeader());

		boolean isNew = false;

		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		// force validation, if on, than execute by component.getValue()
		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		doSetValidation();
		
		if (this.btnAddDealTicket.isVisible() && this.userAction.getSelectedItem() != null){
			if (!(this.userAction.getSelectedItem().getLabel().equalsIgnoreCase("Save") ||
					this.userAction.getSelectedItem().getLabel().equalsIgnoreCase("Resubmit"))) {
				if(aInvestmentFinHeader.getFinanceDetailsList() == null || aInvestmentFinHeader.getFinanceDetailsList().isEmpty()) {
					throw new WrongValueException( this.btnAddDealTicket, Labels.getLabel("label_TreasuaryFinHeaderDialog_Deals_List.value"));
				}
			}
		}

		// fill the TreasuaryFinance object with the components data
		doWriteComponentsToBean(aInvestmentFinHeader,true);

		// Write the additional validations as per below example
		// get the selected branch object from the listbox
		// Do data level validations here

		isNew = aInvestmentFinHeader.isNew();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.trimToEmpty(aInvestmentFinHeader.getRecordType()).equals("")) {
				aInvestmentFinHeader.setVersion(aInvestmentFinHeader.getVersion() + 1);
				if (isNew) {
					aInvestmentFinHeader.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aInvestmentFinHeader.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aInvestmentFinHeader.setNewRecord(true);
				}
			}
		} else {
			aInvestmentFinHeader.setVersion(aInvestmentFinHeader.getVersion() + 1);
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}

		// save it to database
		try {
			if (doProcess(aInvestmentFinHeader, tranType)) {

				if (getTreasuryFinHeaderListCtrl() != null) {
					refreshList();
				}

				//Mail Alert Notification for User
				if(!StringUtils.trimToEmpty(aInvestmentFinHeader.getNextTaskId()).equals("") && 
						!StringUtils.trimToEmpty(aInvestmentFinHeader.getNextRoleCode()).equals(aInvestmentFinHeader.getRoleCode())){
					getMailUtil().sendMail(PennantConstants.MAIL_MODULE_TREASURY, aInvestmentFinHeader,this);
				}
				
				//Customer Notification for Role Identification
				String msg = PennantApplicationUtil.getSavingStatus(aInvestmentFinHeader.getRoleCode(),
						aInvestmentFinHeader.getNextRoleCode(), aInvestmentFinHeader.getInvestmentRef(), 
						Labels.getLabel("label_TreasuaryFinance_InvestMent"), aInvestmentFinHeader.getRecordStatus());
				Clients.showNotification(msg,  "info", null, null, -1);

				closeDialog(this.window_TreasuaryFinHeaderDialog, "InvestmentFinHeaderDialog");
			}

		} catch (final DataAccessException e) {
			e.printStackTrace();
			logger.error(e);
			showErrorMessage(this.window_TreasuaryFinHeaderDialog, e);
		}
		logger.debug("Leaving");
	}

	private boolean doProcess(InvestmentFinHeader aInvFinHeader, String tranType) {
		logger.debug("Entering");

		boolean processCompleted = false;
		AuditHeader auditHeader = null;
		String nextRoleCode = "";

		aInvFinHeader.setLastMntBy(getUserWorkspace().getLoginUserDetails().getLoginUsrID());
		aInvFinHeader.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aInvFinHeader.setUserDetails(getUserWorkspace().getLoginUserDetails());

		if (isWorkFlowEnabled()) {
			String taskId = getWorkFlow().getTaskId(getRole());
			String nextTaskId = "";
			aInvFinHeader.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aInvFinHeader.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getWorkFlow().getNextTaskIds(taskId, aInvFinHeader);
				}

				if (PennantConstants.WF_Audit_Notes.equals(getWorkFlow()
						.getAuditingReq(taskId, aInvFinHeader))) {
					/*try {
						if (!isNotes_Entered()) {
							PTMessageUtils.showErrorMessage(Labels
									.getLabel("Notes_NotEmpty"));
							return false;
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
					}*/
				}
			}

			if (!StringUtils.trimToEmpty(nextTaskId).equals("")) {
				String[] nextTasks = nextTaskId.split(";");

				if (nextTasks != null && nextTasks.length > 0) {
					for (int i = 0; i < nextTasks.length; i++) {

						if (nextRoleCode.length() > 1) {
							nextRoleCode = nextRoleCode + ",";
						}
						nextRoleCode = getWorkFlow().getTaskOwner(nextTasks[i]);
					}
				} else {
					nextRoleCode = getWorkFlow().getTaskOwner(nextTaskId);
				}
			}

			aInvFinHeader.setTaskId(taskId);
			aInvFinHeader.setNextTaskId(nextTaskId);
			aInvFinHeader.setRoleCode(getRole());
			aInvFinHeader.setNextRoleCode(nextRoleCode);

			auditHeader = getAuditHeader(aInvFinHeader, tranType);

			String operationRefs = getWorkFlow().getOperationRefs(taskId, aInvFinHeader);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader, null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(aInvFinHeader, PennantConstants.TRAN_WF);
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		} else {

			auditHeader = getAuditHeader(aInvFinHeader, tranType);
			processCompleted = doSaveProcess(auditHeader, null);
		}
		logger.debug("return value :" + processCompleted);
		logger.debug("Leaving");
		return processCompleted;
	}

	private boolean doSaveProcess(AuditHeader auditHeader, String method) {
		logger.debug("Entering");
		boolean processCompleted = false;
		int retValue = PennantConstants.porcessOVERIDE;
		boolean deleteNotes = false;

		InvestmentFinHeader aInvFinHeader;		
		aInvFinHeader = (InvestmentFinHeader) auditHeader.getAuditDetail().getModelData();

		try {

			while (retValue == PennantConstants.porcessOVERIDE) {

				if (StringUtils.trimToEmpty(method).equalsIgnoreCase("")) {
					if (auditHeader.getAuditTranType().equals( PennantConstants.TRAN_DEL)) {
						auditHeader = getTreasuaryFinanceService().delete(auditHeader);
						deleteNotes = true;
					} else {
						auditHeader = getTreasuaryFinanceService().saveOrUpdate(auditHeader);
					}

				} else {
					if (StringUtils.trimToEmpty(method).equalsIgnoreCase( PennantConstants.method_doApprove)) {

						if (aInvFinHeader.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
							deleteNotes = true;
						}

					} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
						auditHeader = getTreasuaryFinanceService().doReject(auditHeader);
						if (aInvFinHeader.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
							deleteNotes = true;
						}

					} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doConfirm)) {
						auditHeader = getTreasuaryFinanceService().doConfirm(auditHeader);
					}else {
						auditHeader.setErrorDetails(new ErrorDetails(PennantConstants.ERR_9999, Labels .getLabel("InvalidWorkFlowMethod"), null));
						retValue = ErrorControl.showErrorControl( this.window_TreasuaryFinHeaderDialog, auditHeader);
						return processCompleted;
					}
				}

				auditHeader = ErrorControl.showErrorDetails(
						this.window_TreasuaryFinHeaderDialog, auditHeader);
				retValue = auditHeader.getProcessStatus();

				if (retValue == PennantConstants.porcessCONTINUE) {
					processCompleted = true;

					if (deleteNotes) {
						deleteNotes(getNotes(), true);
					}
				}

				if (retValue == PennantConstants.porcessOVERIDE) {
					auditHeader.setOveride(true);
					auditHeader.setErrorMessage(null);
					auditHeader.setInfoMessage(null);
					auditHeader.setOverideMessage(null);
				}
			}
			setOverideMap(auditHeader.getOverideMap());
		} catch (InterruptedException e) {
			logger.error(e);
			e.printStackTrace();
		}

		logger.debug("return Value:" + processCompleted);
		logger.debug("Leaving");
		return processCompleted;
	}

	public BigDecimal getInvestMaturityAmt(BigDecimal finAmount, BigDecimal profitRate){
		logger.debug("Entering");

		finAmount = PennantAppUtil.unFormateAmount(finAmount, ccyFormat);

		BigDecimal 	maturityAmount = BigDecimal.ZERO;
		BigDecimal 	prfBasis = BigDecimal.ZERO;
		BigDecimal 	calMaturityAmount;
		String pftDaysBasis = null;

		if(finAmount != null && profitRate.compareTo(BigDecimal.ZERO) == 1){

			Date startDate = this.startDate.getValue();
			Date maturityDate = this.maturityDate.getValue();

			if(this.profitDaysBasis.getSelectedItem() != null) {
				pftDaysBasis = this.profitDaysBasis.getSelectedItem().getValue();
			}

			prfBasis = CalculationUtil.getInterestDays(startDate, maturityDate, pftDaysBasis);
			calMaturityAmount = ((finAmount.multiply(profitRate)). divide(new BigDecimal(100))).multiply(prfBasis);

			maturityAmount = finAmount.add(calMaturityAmount);


		}
		logger.debug("Leaving");
		return maturityAmount;
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++ WorkFlow Components +++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	private AuditHeader getAuditHeader(InvestmentFinHeader treasuaryFinHeader, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, treasuaryFinHeader.getBefImage(), treasuaryFinHeader);
		return new AuditHeader(treasuaryFinHeader.getInvestmentRef(), null, null, null, auditDetail, treasuaryFinHeader.getUserDetails(), getOverideMap());
	}

	public void onClick$btnNotes(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("notes", getNotes());
		map.put("control", this);

		// call the zul-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/notes/notes.zul", null,
					map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / "
					+ e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving" + event.toString());
	}

	public void setNotes_entered(String notes) {
		if (!isNotes_Entered()) {
			if (org.apache.commons.lang.StringUtils.trimToEmpty(notes)
					.equalsIgnoreCase("Y")) {
				setNotes_Entered(true);
			} else {
				setNotes_Entered(false);
			}
		}
	}

	private Notes getNotes() {
		Notes notes = new Notes();
		notes.setModuleName("TreasuaryFinance");
		notes.setReference(getInvestmentFinHeader().getInvestmentRef());
		notes.setVersion(getInvestmentFinHeader().getVersion());
		return notes;
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public boolean isNotes_Entered() {
		return notes_Entered;
	}
	public void setNotes_Entered(boolean notesEntered) {
		this.notes_Entered = notesEntered;
	}

	public void setValidationOn(boolean validationOn) {
		this.validationOn = validationOn;
	}
	public boolean isValidationOn() {
		return this.validationOn;
	}

	public TreasuaryFinanceService getTreasuaryFinanceService() {
		return treasuaryFinanceService;
	}
	public void setTreasuaryFinanceService(
			TreasuaryFinanceService treasuaryFinanceService) {
		this.treasuaryFinanceService = treasuaryFinanceService;
	}

	public void setOverideMap(
			HashMap<String, ArrayList<ErrorDetails>> overideMap) {
		this.overideMap = overideMap;
	}
	public HashMap<String, ArrayList<ErrorDetails>> getOverideMap() {
		return overideMap;
	}

	public Object getChildWindowDialogCtrl() {
		return childWindowDialogCtrl;
	}
	public void setChildWindowDialogCtrl(Object childWindowDialogCtrl) {
		this.childWindowDialogCtrl = childWindowDialogCtrl;
	}

	public InvestmentFinHeader getInvestmentFinHeader() {
		return investmentFinHeader;
	}
	public void setInvestmentFinHeader(InvestmentFinHeader investmentFinHeader) {
		this.investmentFinHeader = investmentFinHeader;
	}

	public void setTreasuryFinHeaderListCtrl(
			TreasuryFinHeaderListCtrl treasuryFinHeaderListCtrl) {
		this.treasuryFinHeaderListCtrl = treasuryFinHeaderListCtrl;
	}
	public TreasuryFinHeaderListCtrl getTreasuryFinHeaderListCtrl() {
		return treasuryFinHeaderListCtrl;
	}

	public void setInvestmentFinHeaderList(
			List<InvestmentFinHeader> investmentFinHeader) {
		this.investmentFinHeaderList = investmentFinHeader;
	}
	public List<InvestmentFinHeader> getInvestmentFinHeaderList() {
		return investmentFinHeaderList;
	}

	public FinanceDetail getFinanceDetail() {
		return financeDetail;
	}
	public void setFinanceDetail(FinanceDetail financeDetail) {
		this.financeDetail = financeDetail;
	}

	public MailUtil getMailUtil() {
		return mailUtil;
	}

	public void setMailUtil(MailUtil mailUtil) {
		this.mailUtil = mailUtil;
	}

}
