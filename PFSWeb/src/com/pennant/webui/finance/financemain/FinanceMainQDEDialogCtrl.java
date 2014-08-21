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
 * FileName    		:  FinanceMainQDEDialogCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  16-11-2011    														*
 *                                                                  						*
 * Modified Date    :  16-11-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 16-11-2011       Pennant	                 0.1                                            * 
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
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.SuspendNotAllowedException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Div;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Longbox;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Row;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.app.util.DateUtility;
import com.pennant.app.util.PennantReferenceIDUtil;
import com.pennant.app.util.RuleExecutionUtil;
import com.pennant.app.util.SystemParameterDetails;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.applicationmaster.Currency;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.lmtmasters.FinanceCheckListReference;
import com.pennant.backend.model.lmtmasters.FinanceReferenceDetail;
import com.pennant.backend.service.PagedListService;
import com.pennant.backend.service.customermasters.CustomerIncomeService;
import com.pennant.backend.service.finance.FinanceDetailService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.coreinterface.exception.AccountNotFoundException;
import com.pennant.util.ErrorControl;
import com.pennant.util.PennantAppUtil;
import com.pennant.util.Constraint.AmountValidator;
import com.pennant.webui.dedup.dedupparm.FetchDedupDetails;
import com.pennant.webui.util.ButtonStatusCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.MultiLineMessageBox;
import com.pennant.webui.util.PTMessageUtils;
import com.pennant.webui.util.searchdialogs.ExtendedSearchListBox;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * /WEB-INF/pages/Finance/FinanceMain/financeMainQDEDialog.zul file. <br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 */
public class FinanceMainQDEDialogCtrl extends GFCBaseCtrl implements Serializable {


	private static final long serialVersionUID = 8556168885363682933L;
	private final static Logger logger = Logger.getLogger(FinanceMainQDEDialogCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWiredd by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window       window_FinanceMainQDEDialog; // autoWired
	protected Textbox      finReference;                // autoWired
	protected Textbox      finType;                     // autoWired
	protected Decimalbox   finAmount;                   // autoWired
	protected Textbox      finCcy;                      // autoWired
	protected Decimalbox   downPayment;                 // autoWired
	protected Datebox      finStartDate;                // autoWired
	protected Intbox       numberOfTerms;               // autoWired
	protected Longbox      custID;                      // autoWired
	protected Label        recordStatus;                // autoWired
	protected Textbox      lovDescCustCIF;              // autoWired
	protected Label        finTypeName;                 // autoWired
	protected Textbox      lovDescFinCcyName;           // autoWired
	protected Tab 		   financeElgreferenceTab;      // autoWired
	protected Tab 		   finMainbasicDetailsTab;      // autoWired
	protected Listbox 	   listBoxFinElgRef;			// autoWired
	protected Label 	   label_ElgRuleSummaryVal; 	// autoWired
	protected Checkbox 	   cbElgOverride;				// autoWired

	//buttons
	protected Button       btnNew;                      // autoWired
	protected Button       btnEdit;                     // autoWired
	protected Button       btnDelete;                   // autoWired
	protected Button       btnSave;                     // autoWired
	protected Button       btnCancel;                   // autoWired
	protected Button       btnClose;                    // autoWired
	protected Button       btnHelp;                     // autoWired
	protected Button       btnNotes;                    // autoWired
	protected Button       btnSearchFinCcy;             // autoWired
	protected Button       btnSearchCustCIF;            // autoWired
	protected Tabpanel     checkListTabPanel ;          // autoWired
	protected Radiogroup   userAction;					// autoWired
	protected Groupbox     groupboxWf;					// autoWired
	protected Row          statusRow;					// autoWired
	protected Div 		   basicDetailsDiv;				// autoWired
	protected Div 		   checkListTabDiv;				// autoWired
	protected Component    checkListChildWindow;		// autoWired
	protected Tab 		   checkListTab;				// autoWired

	// not auto wired variables
	private FinanceDetail financeDetail;                       // over handed per parameters
	private FinanceMain prvFinanceMain;                        // over handed per parameters
	private transient FinanceMainListCtrl financeMainListCtrl; // over handed per parameters

	// old value variables for edit mode. that we can check if something
	// on the values are edited since the last initialization.
	private transient String  		oldVar_finReference;
	private transient String  		oldVar_finType;
	private transient BigDecimal  	oldVar_finAmount;
	private transient String  		oldVar_finCcy;
	private transient BigDecimal    oldVar_downPayment;
	private transient Date  		oldVar_finStartDate;
	private transient int  		    oldVar_numberOfTerms;
	private transient long  		oldVar_custID;
	private transient String        oldVar_recordStatus;
	private transient String        oldVar_lovDescCustCIF;
	private transient String 		oldVar_lovDescFinCcyName;
	private transient boolean       validationOn;
	private boolean                 notes_Entered=false;

	// Button controller for the CRUD buttons
	private transient final String btnCtroller_ClassPrefix = "button_FinanceMainDialog_";
	private transient ButtonStatusCtrl btnCtrl;

	// ServiceDAOs / Domain Classes
	private transient FinanceDetailService financeDetailService;
	private transient PagedListService pagedListService;
	private CustomerIncomeService customerIncomeService;
	private HashMap<String, ArrayList<ErrorDetails>> overideMap= new HashMap<String, ArrayList<ErrorDetails>>();
	protected JdbcSearchObject<Customer> newSearchObject;
	private transient RuleExecutionUtil ruleExecutionUtil;

	private transient boolean 		checkListDataChanged;
	private transient int 			canOverrideRuleCount;
	private transient int 			overriddenRuleCount;
	private transient BigDecimal 	finMinElgAmt = null;
	private transient BigDecimal 	finMinOvrElgAmt = null;
	private transient boolean 		eligible = true;
	private transient boolean 		elgRlsExecuted;

	/**
	 * default constructor.<br>
	 */
	public FinanceMainQDEDialogCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * ZUL-file is called with a parameter for a selected FinanceMain object in a
	 * Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_FinanceMainQDEDialog(Event event) throws Exception {
		logger.debug(event.toString());

		/* set components visible dependent of the users rights */
		doCheckRights();

		/* create the Button Controller. Disable not used buttons during working */
		this.btnCtrl = new ButtonStatusCtrl(getUserWorkspace(),
				this.btnCtroller_ClassPrefix, true, this.btnNew,this.btnEdit,
				this.btnDelete, this.btnSave, this.btnCancel, this.btnClose,
				this.btnNotes);

		// get the parameters map that are over handed by creation.
		final Map<String, Object> args = getCreationArgsMap(event);

		// READ OVERHANDED parameters !
		if (args.containsKey("financeDetail")) {
			this.financeDetail = (FinanceDetail) args.get("financeDetail");
			FinanceDetail befImage =new FinanceDetail();
			BeanUtils.copyProperties(this.financeDetail, befImage);
			this.financeDetail.setBefImage(befImage);
			setFinanceDetail(this.financeDetail);
		} else {
			setFinanceDetail(null);
		}
		FinanceMain financeMain = getFinanceDetail().getFinScheduleData().getFinanceMain();
		doLoadWorkFlow(financeMain.isWorkflow(),financeMain.getWorkflowId(),financeMain.getNextTaskId());

		if (isWorkFlowEnabled()){
			this.userAction	= setListRecordStatus(this.userAction,false);
			getUserWorkspace().alocateRoleAuthorities(getRole(), "FinanceMainDialog");
		}

		// READ OVERHANDED parameters !
		// we get the financeMainListWindow controller. So we have access
		// to it and can synchronize the shown data when we do insert, edit or
		// delete financeMain here.
		if (args.containsKey("financeMainListCtrl")) {
			setFinanceMainListCtrl((FinanceMainListCtrl) args.get("financeMainListCtrl"));
		} else {
			setFinanceMainListCtrl(null);
		}

		// set Field Properties
		doSetFieldProperties();
		showCheckList();
		doShowDialog(this.financeDetail);
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
	public void onClose$window_FinanceMainQDEDialog(Event event) throws Exception {
		logger.debug(event.toString());
		doClose();
		logger.debug("Leaving");
	}

	/**
	 * when the "save" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnSave(Event event) throws InterruptedException {
		logger.debug(event.toString());
		doSave();
		logger.debug("Leaving");
	}

	/**
	 * when the "edit" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnEdit(Event event) {
		logger.debug(event.toString());
		doEdit();
		// remember the old variables
		doStoreInitValues();
		logger.debug("Leaving");
	}

	/**
	 * when the "help" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnHelp(Event event) throws InterruptedException {
		logger.debug(event.toString());
		PTMessageUtils.showHelpWindow(event, window_FinanceMainQDEDialog);
		logger.debug("Leaving");
	}

	/**
	 * when the "new" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnNew(Event event) {
		logger.debug(event.toString());
		doNew();
		logger.debug("Leaving");
	}


	/**
	 * when the "cancel" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnCancel(Event event) {
		logger.debug(event.toString());
		doCancel();
		logger.debug("Leaving");
	}

	/**
	 * when the "close" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnClose(Event event) throws InterruptedException {
		logger.debug(event.toString());
		try {
			doClose();
		} catch (final WrongValuesException e) {
			throw e;
		}
		logger.debug("Leaving");
	}

	/**
	 * When user clicks on button "SearchFinCcy" button
	 */
	public void onClick$btnSearchFinCcy(Event event){
		logger.debug("Entering " + event.toString());
		Object dataObject = ExtendedSearchListBox.show(this.window_FinanceMainQDEDialog,"Currency");
		if (dataObject instanceof String){
			this.finCcy.setValue(dataObject.toString());
			this.lovDescFinCcyName.setValue("");
		}else{
			Currency details= (Currency) dataObject;
			if (details != null) {
				this.finCcy.setValue(details.getLovValue());
				this.lovDescFinCcyName.setValue(details.getCcyCode()+"-"+details.getCcyDesc());
			}
		}
		logger.debug("Leaving " + event.toString());
	}

	/**
	 * When user clicks on button "customerId Search" button
	 * @param event
	 */
	public void onClick$btnSearchCustCIF(Event event) throws 
	SuspendNotAllowedException, InterruptedException {
		logger.debug("Entering " + event.toString());
		this.label_ElgRuleSummaryVal.setValue("");
		onLoad();
		logger.debug("Leaving " + event.toString());
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ GUI Process++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * To load the customerSelect filter dialog
	 * @throws SuspendNotAllowedException
	 * @throws InterruptedException
	 */
	private void onLoad() throws SuspendNotAllowedException,
	InterruptedException {
		logger.debug("Entering");
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("DialogCtrl", this);
		map.put("filtertype", "Extended");
		map.put("searchObject",this.newSearchObject);
		Executions.createComponents(
				"/WEB-INF/pages/CustomerMasters/Customer/CustomerSelect.zul",
				null, map);
		logger.debug("Leaving");
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering") ;
		//Empty sent any required attributes
		this.finReference.setMaxlength(20);
		this.numberOfTerms.setMaxlength(10);
		this.downPayment.setMaxlength(18);
		this.downPayment.setFormat(PennantApplicationUtil.getAmountFormate(getFinanceDetail().getFinScheduleData().getFinanceMain().getLovDescFinFormatter()));
		this.finType.setMaxlength(8);
		this.finCcy.setMaxlength(3);
		this.finStartDate.setFormat(PennantConstants.dateFormat);
		this.finAmount.setMaxlength(18);
		this.finAmount.setFormat(PennantApplicationUtil.getAmountFormate(getFinanceDetail().getFinScheduleData().getFinanceMain().getLovDescFinFormatter()));

		if (isWorkFlowEnabled()){
			this.groupboxWf.setVisible(true);
			this.statusRow.setVisible(true);
		}else{
			this.groupboxWf.setVisible(false);
			this.statusRow.setVisible(false);
		}

		logger.debug("Leaving") ;
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
		logger.debug("Entering") ;
		getUserWorkspace().alocateAuthorities("FinanceMainDialog");
		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_FinanceMainDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_FinanceMainDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_FinanceMainDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_FinanceMainDialog_btnSave"));
		this.btnCancel.setVisible(false);
		logger.debug("Leaving") ;
	}
	/**
	 * Closes the dialog window. <br>
	 * <br>
	 * Before closing we check if there are unsaved changes in <br>
	 * the components and ask the user if saving the modifications. <br>
	 * 
	 * @throws InterruptedException
	 * 
	 */
	private void doClose() throws InterruptedException {
		logger.debug("Entering");
		boolean close=true;
		if (isDataChanged(true)) {
			logger.debug("isDataChanged : true");

			// Show a confirm box
			final String msg = Labels.getLabel("message_Data_Modified_Save_Data_YesNo");
			final String title = Labels.getLabel("message.Information");

			MultiLineMessageBox.doSetTemplate();
			int conf = MultiLineMessageBox.show(msg, title, MultiLineMessageBox.YES| MultiLineMessageBox.NO, MultiLineMessageBox.QUESTION,true);

			if (conf==MultiLineMessageBox.YES){
				logger.debug("doClose: Yes");
				doSave();
				close=false;
			}else{
				logger.debug("doClose: No");
			}
		}else{
			logger.debug("isDataChanged : false");
		}

		if(close){
			closeDialog(this.window_FinanceMainQDEDialog, "FinanceMain");	
		}

		logger.debug("Leaving") ;
	}

	/**
	 * Cancel the actual operation. <br>
	 * <br>
	 * Resets to the original status.<br>
	 * 
	 */
	private void doCancel() {
		logger.debug("Entering") ;
		doResetInitValues();
		doReadOnly();
		this.btnCtrl.setInitEdit();
		logger.debug("Leaving") ;
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aFinanceMain
	 *            FinanceMain
	 */
	public void doWriteBeanToComponents(FinanceDetail aFinanceDetail) {
		logger.debug("Entering") ;
		FinanceMain aFinanceMain = aFinanceDetail.getFinScheduleData().getFinanceMain();
		this.finReference.setValue(aFinanceMain.getFinReference());
		this.numberOfTerms.setValue(aFinanceMain.getNumberOfTerms());
		if (aFinanceMain.isLovDescDwnPayReq()) {
			this.downPayment.setDisabled(false);
			this.downPayment.setValue(PennantAppUtil.formateAmount(aFinanceMain.getDownPayment()
					,aFinanceMain.getLovDescFinFormatter()));
		}
		this.finType.setValue(aFinanceMain.getFinType());
		this.finCcy.setValue(aFinanceMain.getFinCcy());
		this.lovDescFinCcyName.setValue(aFinanceMain.getLovDescFinCcyName());
		this.finStartDate.setValue(aFinanceMain.getFinStartDate());
		this.finAmount.setValue(PennantAppUtil.formateAmount(aFinanceMain.getFinAmount()
				,aFinanceMain.getLovDescFinFormatter()));
		this.custID.setValue(aFinanceMain.getCustID());
		this.lovDescCustCIF.setValue(aFinanceMain.getLovDescCustCIF());
		this.finTypeName.setValue(aFinanceMain.getLovDescFinTypeName());
		this.finType.setValue(aFinanceMain.getFinType());
		this.recordStatus.setValue(aFinanceMain.getRecordStatus());
		if (aFinanceDetail.getEligibilityRuleList().size() != 0) {
			dofillEligibilityListbox(aFinanceDetail.getEligibilityRuleList(), this.listBoxFinElgRef, false);
			this.financeElgreferenceTab.setVisible(true);
		} else {
			this.financeElgreferenceTab.setVisible(false);
		}
		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aFinanceMain
	 */
	public void doWriteComponentsToBean(FinanceDetail aFinanceDetail) {
		logger.debug("Entering") ;

		FinanceMain aFinanceMain = aFinanceDetail.getFinScheduleData().getFinanceMain();
		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		try{
			if( this.finReference.getValue().equals("")){
				aFinanceMain.setFinReference(String.valueOf(PennantReferenceIDUtil.genNewWhatIfRef(false)));
			}
			else{
				aFinanceMain.setFinReference(this.finReference.getValue());		
			}
		}catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aFinanceMain.setNumberOfTerms(this.numberOfTerms.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			if((this.finAmount.getValue().compareTo(PennantAppUtil.formateAmount(getFinanceDetail()
				.getFinScheduleData().getFinanceType().getFinMinAmount(), getFinanceDetail()
				.getFinScheduleData().getFinanceMain().getLovDescFinFormatter())) < 0) || 
				(this.finAmount.getValue().compareTo(PennantAppUtil.formateAmount(getFinanceDetail()
				.getFinScheduleData().getFinanceType().getFinMaxAmount(), getFinanceDetail()
			    .getFinScheduleData().getFinanceMain().getLovDescFinFormatter())) > 0)) {
				throw new WrongValueException(this.finAmount,Labels.getLabel("FIELD_RANGE", new String[] {
						Labels.getLabel("label_FinanceMainQDEDialog_FinAmount.value"),
						PennantAppUtil.amountFormate(getFinanceDetail()
								.getFinScheduleData().getFinanceType().getFinMinAmount(),getFinanceDetail()
						.getFinScheduleData().getFinanceMain().getLovDescFinFormatter()),
						PennantAppUtil.amountFormate(getFinanceDetail()
								.getFinScheduleData().getFinanceType().getFinMaxAmount(),getFinanceDetail()
								.getFinScheduleData().getFinanceMain().getLovDescFinFormatter()),
				}) );
				
			}
			aFinanceMain.setFinAmount(PennantAppUtil.unFormateAmount(this.finAmount.getValue(),
					getFinanceDetail().getFinScheduleData().getFinanceMain().getLovDescFinFormatter()));
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			if(!this.downPayment.isDisabled() && this.downPayment.getValue()!=null) {
				this.downPayment.clearErrorMessage();
				BigDecimal reqDwnPay = PennantAppUtil.getPercentageValue(this.finAmount.getValue(),
						getFinanceDetail().getFinScheduleData().getFinanceMain().getLovDescMinDwnPayPercent());
				if (this.downPayment.getValue().compareTo(
						this.finAmount.getValue()) > 0) {
					throw new WrongValueException(
							this.downPayment,
							Labels.getLabel("MAND_FIELD_MIN",new String[] {
									Labels.getLabel("label_FinanceMainQDEDialog_DownPayment.value"),
									reqDwnPay.toString(),PennantAppUtil.formatAmount(
											this.finAmount.getValue(),getFinanceDetail()
											.getFinScheduleData().getFinanceMain()
											.getLovDescFinFormatter(),false).toString() }));
				}
				if (this.downPayment.getValue().compareTo(reqDwnPay) == -1) {
					throw new WrongValueException(
							this.downPayment,Labels.getLabel("PERC_MIN",new String[] {
								Labels.getLabel("label_FinanceMainQDEDialog_DownPayment.value"),
								PennantAppUtil.formatAmount(reqDwnPay,
								getFinanceDetail().getFinScheduleData().getFinanceMain().getLovDescFinFormatter(), false).toString()}));
				}
				aFinanceMain.setDownPayment(PennantAppUtil.unFormateAmount(this.downPayment.getValue(),
						getFinanceDetail().getFinScheduleData().getFinanceMain().getLovDescFinFormatter()));
			}
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aFinanceMain.setLovDescFinCcyName(this.lovDescFinCcyName.getValue());
			aFinanceMain.setFinCcy(this.finCcy.getValue());	
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aFinanceMain.setCustID(this.custID.getValue());
			aFinanceMain.setLovDescCustCIF(this.lovDescCustCIF.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aFinanceMain.setFinStartDate(this.finStartDate.getValue());

		}catch (WrongValueException we ) {
			wve.add(we);
		}
		
		try {
			aFinanceMain.setFinType(this.finType.getValue());
			aFinanceMain.setLovDescFinTypeName(this.finTypeName.getValue());

		}catch (WrongValueException we ) {
			wve.add(we);
		}
		doRemoveValidation();	
		doRemoveLOVValidation();

		showErrorDetails(wve);

		aFinanceMain.setRecordStatus(this.recordStatus.getValue());
		aFinanceDetail.getFinScheduleData().setFinanceMain(aFinanceMain);
		if(aFinanceDetail.getFinScheduleData().getFinanceScheduleDetails().size()>0){
			aFinanceDetail.getFinScheduleData().setSchduleGenerated(true);
		}

		logger.debug("Leaving");
	}

	private void showErrorDetails(ArrayList<WrongValueException> wve) {
		logger.debug("Entering");
		if (wve.size() > 0) {
			logger.debug("Throwing occured Errors By using WrongValueException");
			//groupBox.set
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = wve.get(i);
			}
			this.finMainbasicDetailsTab.setSelected(true);
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
	 * @param aFinanceMain
	 * @throws InterruptedException
	 */
	public void doShowDialog(FinanceDetail afinanceDetail) throws InterruptedException {
		logger.debug("Entering") ;

		// if aFinanceMain == null then we opened the Dialog without
		// arguments for a given entity, so we get a new Object().
		if (afinanceDetail == null) {
			/** !!! DO NOT BREAK THE TIERS !!! */
			// We don't create a new DomainObject() in the front end.
			// We GET it from the back end.
			afinanceDetail = getFinanceDetailService().getNewFinanceDetail(false);
			setFinanceDetail(afinanceDetail);
		} else {
			setFinanceDetail(afinanceDetail);
		}

		// set Read only mode accordingly if the object is new or not.
		if (afinanceDetail.isNewRecord()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.finReference.focus();
		} else {
			this.numberOfTerms.focus();
			if (isWorkFlowEnabled()){
				this.btnNotes.setVisible(true);
				doEdit();
			}else{
				this.btnCtrl.setInitEdit();
				doReadOnly();
				btnCancel.setVisible(false);
			}
		}

		try {
			// fill the components with the data
			doWriteBeanToComponents(afinanceDetail);

			// stores the initial data for comparing if they are changed
			// during user action.
			doStoreInitValues();
			setDialog(this.window_FinanceMainQDEDialog);
		} catch (final Exception e) {
			logger.error(e);
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving") ;
	}
	/**
	 * This method shows to cheklist 
	 * @throws InterruptedException
	 */
	private void showCheckList() throws InterruptedException {
		logger.debug("Entering ");
		if (getFinanceDetail().getCheckList() != null && getFinanceDetail().getCheckList().size() > 0 ) {
			final HashMap<String, Object> map = new HashMap<String, Object>();
			boolean showcheckLsitTab=false;
			for(FinanceReferenceDetail chkList:getFinanceDetail().getCheckList()){
				if(chkList.getShowInStage().contains(getRole())){
					showcheckLsitTab=true;
					break;
				}
				if(chkList.getAllowInputInStage().contains(getRole())){
					showcheckLsitTab=true;
					break;
				}
			}
			if(showcheckLsitTab) {
				checkListTab.setVisible(true);
				basicDetailsDiv = new Div();
				checkListTabPanel.appendChild(basicDetailsDiv);
				map.put("financeMainQDEDialogCtrl", this);
				map.put("financeDetail", getFinanceDetail());
				map.put("userRole", getRole());
				String  zulFilePathName = "/WEB-INF/pages/LMTMasters/FinanceCheckListReference/FinanceCheckListReferenceDialog.zul";  
				checkListChildWindow = Executions.createComponents(zulFilePathName, checkListTabPanel, map);
				logger.debug("Leaving ");
			}
		}
		logger.debug("Leaving ");
	}
	/**
	 * To set the customer id from Customer filter
	 * @param nCustomer
	 * @throws InterruptedException
	 */
	public void doSetCustomer(Object nCustomer,JdbcSearchObject<Customer> newSearchObject) throws InterruptedException{
		logger.debug("Entering"); 
		final Customer aCustomer = (Customer)nCustomer; 		
		this.custID.setValue(aCustomer.getCustID());
		this.lovDescCustCIF.setValue(String.valueOf(aCustomer.getCustCIF()));
		this.newSearchObject=newSearchObject;
		getFinanceDetail().getFinScheduleData().getFinanceMain().setLovDescCustFName(StringUtils.trimToEmpty(aCustomer.getCustFName()));
		getFinanceDetail().getFinScheduleData().getFinanceMain().setLovDescCustLName(StringUtils.trimToEmpty(aCustomer.getCustLName()));
		// Set Customer Data to check the eligibility
		
		//Current Finance Monthly Installment Calculation
		BigDecimal totalRepayAmount = getFinanceDetail().getFinScheduleData().getFinanceMain().getTotalRepayAmt();
		int installmentMnts = DateUtility.getMonthsBetween(getFinanceDetail().getFinScheduleData().getFinanceMain().getFinStartDate(),
				getFinanceDetail().getFinScheduleData().getFinanceMain().getMaturityDate(), true);
		
		BigDecimal curFinRepayAmt = totalRepayAmount.divide(new BigDecimal(installmentMnts), 0, RoundingMode.HALF_DOWN);
		int months = DateUtility.getMonthsBetween(financeDetail.getFinScheduleData().getFinanceMain().getFinStartDate(), 
				financeDetail.getFinScheduleData().getFinanceMain().getMaturityDate());
		
		getFinanceDetail().setCustomerEligibilityCheck(getFinanceDetailService().getCustEligibilityDetail(aCustomer,"",
				getFinanceDetail().getFinScheduleData().getFinanceMain().getFinCcy(), curFinRepayAmt,
				months, financeDetail.getFinScheduleData().getFinanceMain().getFinAmount(),
				getFinanceDetail().getFinScheduleData().getFinanceMain().getCustDSR()));
		
		// Set Customer Data to Calculate the Score
		//setCustomerScoringData(aCustomer);
		// Execute Eligibility Rule and Display Result
		dofillEligibilityListbox(getFinanceDetail().getEligibilityRuleList(), this.listBoxFinElgRef, false);
		// Execute Scoring metrics and Display Total Score
		//dofillScoringListbox(getFinanceDetail().getScoringGroupList(), this.listBoxFinScoRef, true);
		logger.debug("Leaving ");
	}
	/**
	 * This method shows error message
	 * @param e
	 */

	private void showMessage(Exception e){
		logger.debug("Entering ");
		AuditHeader auditHeader= new AuditHeader();
		try {
			auditHeader.setErrorDetails(new ErrorDetails(PennantConstants.ERR_UNDEF,e.getMessage(),null));
			ErrorControl.showErrorControl(this.window_FinanceMainQDEDialog, auditHeader);
		} catch (Exception exp) {
			logger.error(exp);
		}
		logger.debug("Leaving ");
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// ++++++++++++++++++++++++++++++ helpers ++++++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * Stores the initial values in memory variables. <br>
	 */
	private void doStoreInitValues() {
		logger.debug("Entering");
		this.oldVar_finReference = this.finReference.getValue();
		this.oldVar_numberOfTerms = this.numberOfTerms.intValue();	
		this.oldVar_downPayment = this.downPayment.getValue();
		this.oldVar_finType = this.finType.getValue();
		this.oldVar_finCcy = this.finCcy.getValue();
		this.oldVar_lovDescFinCcyName = this.lovDescFinCcyName.getValue();
		this.oldVar_finStartDate = this.finStartDate.getValue();
		this.oldVar_finAmount = this.finAmount.getValue();
		this.oldVar_custID = this.custID.longValue();
		this.oldVar_recordStatus = this.recordStatus.getValue();
		this.oldVar_lovDescCustCIF=this.lovDescCustCIF.getValue();
		logger.debug("Leaving") ;
	}

	/**
	 * Resets the initial values from memory variables. <br>
	 */
	private void doResetInitValues() {
		logger.debug("Entering");
		this.finReference.setValue(this.oldVar_finReference);
		this.numberOfTerms.setValue(this.oldVar_numberOfTerms);
		this.downPayment.setValue(this.oldVar_downPayment);
		this.finType.setValue(this.oldVar_finType);
		this.finCcy.setValue(this.oldVar_finCcy);
		this.lovDescFinCcyName.setValue(this.oldVar_lovDescFinCcyName);
		this.finStartDate.setValue(this.oldVar_finStartDate);
		this.finAmount.setValue(this.oldVar_finAmount);
		this.custID.setValue(this.oldVar_custID);
		this.lovDescCustCIF.setValue(this.oldVar_lovDescCustCIF);
		this.recordStatus.setValue(this.oldVar_recordStatus);
		if(isWorkFlowEnabled()){
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
	private boolean isDataChanged(boolean check) {
		logger.debug("Entering");
		//To clear the Error Messages
		doClearMessage();
		if(check && checkListChildWindow != null){
			Events.sendEvent("onCheckListClose", checkListChildWindow, null);
			if(isCheckListDataChanged()){
				return true;
			}
		}
		if (this.oldVar_finReference != this.finReference.getValue()) {
			return true;
		}
		if (this.oldVar_custID != this.custID.getValue()) {
			return true;
		}
		if (this.oldVar_lovDescCustCIF != this.lovDescCustCIF.getValue()) {
			return true;
		}
		if (this.oldVar_lovDescFinCcyName!= this.lovDescFinCcyName.getValue()) {
			return true;
		}
		if (this.oldVar_finAmount!= this.finAmount.getValue()) {
			return true;
		}
		if (this.oldVar_downPayment!= this.downPayment.getValue()) {
			return true;
		}
		if (this.oldVar_finStartDate!= this.finStartDate.getValue()) {
			return true;
		}
		if (this.oldVar_numberOfTerms!= this.numberOfTerms.intValue()) {
			return true;
		}
		logger.debug("Leaving"); 
		return false;
	}

	/**
	 * Disables the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering");
		setValidationOn(false);
		this.finReference.setConstraint("");
		this.numberOfTerms.setConstraint("");
		this.downPayment.setConstraint("");
		this.finStartDate.setConstraint("");
		this.finAmount.setConstraint("");
		this.custID.setConstraint("");
		logger.debug("Leaving");
	}

	/**
	 *  Disables the Validation by setting empty constraints to LOVFields.
	 */
	private void doRemoveLOVValidation() {
		logger.debug("Entering ");
		this.lovDescCustCIF.setConstraint("");
		this.lovDescFinCcyName.setConstraint("");
		logger.debug("Leaving ");
	}

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly() {
		logger.debug("Entering");
		this.finReference.setReadonly(true);
		this.numberOfTerms.setReadonly(true);
		this.downPayment.setReadonly(true);
		this.btnSearchFinCcy.setDisabled(true);
		this.finStartDate.setDisabled(true);
		this.finAmount.setReadonly(true);
		this.custID.setReadonly(true);

		if(isWorkFlowEnabled()){
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(true);
			}
		}

		if(isWorkFlowEnabled()){
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
		this.finReference.setValue("");
		this.numberOfTerms.setText("");
		this.downPayment.setValue("");
		this.finType.setValue("");
		this.finCcy.setValue("");
		this.lovDescFinCcyName.setValue("");
		this.finStartDate.setText("");
		this.finAmount.setValue("");
		this.custID.setText("");
		logger.debug("Leaving");
	}
	/**
	 *  Remove Error Messages for Fields
	 */
	private void doClearMessage() {
		logger.debug("Entering");
		this.finReference.setErrorMessage("");
		this.numberOfTerms.setErrorMessage("");
		this.downPayment.setErrorMessage("");
		this.lovDescFinCcyName.setErrorMessage("");
		this.finStartDate.setErrorMessage("");
		this.finAmount.setErrorMessage("");
		this.custID.setErrorMessage("");
		this.lovDescCustCIF.setErrorMessage("");
		logger.debug("Leaving");
	}
	/**
	 * This method refreshes the list 
	 */

	private void refreshList(){
		logger.debug("Entering ");
		final JdbcSearchObject<FinanceMain> soFinanceMain = getFinanceMainListCtrl().getSearchObj();
		getFinanceMainListCtrl().pagingFinanceMainList.setActivePage(0);
		getFinanceMainListCtrl().getPagedListWrapper().setSearchObject(soFinanceMain);
		if(getFinanceMainListCtrl().listBoxFinanceMain!=null){
			getFinanceMainListCtrl().listBoxFinanceMain.getListModel();
		}
		logger.debug("Leaving ");
	} 
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// +++++++++++++++++++++++++ CRUD operations +++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++


	/**
	 * Create a new FinanceMain object. <br>
	 */
	private void doNew() {
		logger.debug("Entering");

		final FinanceDetail aFinanceDetail = getFinanceDetailService().getNewFinanceDetail(false);
		aFinanceDetail.setNewRecord(true);
		aFinanceDetail.setNewRecord(true);

		setFinanceDetail(aFinanceDetail);
		doClear(); // clear all components
		doEdit(); // edit mode
		this.btnCtrl.setBtnStatus_New();

		// remember the old variables
		doStoreInitValues();

		// setFocus
		this.finReference.focus();
		logger.debug("Leaving");
	}

	/**
	 * Set the components for edit mode. <br>s
	 */
	private void doEdit() {
		logger.debug("Entering");

		if (getFinanceDetail().isNewRecord()){
			this.finReference.setReadonly(false);
			this.btnCancel.setVisible(false);
		}else{
			this.finReference.setReadonly(true);
			this.btnCancel.setVisible(false);
		}
		this.numberOfTerms.setReadonly(isReadOnly("FinanceMainDialog_numberOfTerms"));
		this.downPayment.setReadonly(isReadOnly("FinanceMainDialog_downPayment"));
		this.btnSearchFinCcy.setDisabled(isReadOnly("FinanceMainDialog_finCcy"));
		this.finStartDate.setDisabled(isReadOnly("FinanceMainDialog_finStartDate"));
		this.finAmount.setReadonly(isReadOnly("FinanceMainDialog_finAmount"));

		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug("Entering");
		final FinanceDetail aFinanceDetail = new FinanceDetail();
		BeanUtils.copyProperties(getFinanceDetail(), aFinanceDetail);
		boolean isNew = false;
		FinanceMain aFinanceMain = aFinanceDetail.getFinScheduleData().getFinanceMain();

		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		// force validation, if on, than execute by component.getValue()
		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		doClearMessage();
		doSetValidation();
		
		//FinanceMainValidation.doCheckValidation(this.window_FinanceMainQDEDialog, aFinanceDetail.getFinScheduleData());

		// fill the FinanceMain object with the components data
		doWriteComponentsToBean(aFinanceDetail);
		aFinanceDetail.setUserAction(this.userAction.getSelectedItem().getLabel());

		if (this.userAction.getSelectedItem().getLabel().equalsIgnoreCase("Submit")){
			if(financeElgreferenceTab.isVisible()) {
				if(isDataChanged(false)) {
					elgRlsExecuted = false;
					PTMessageUtils.showErrorMessage("Finance Details has been changed. Eligibility criteria should be verified before submitting.");
					return;
				}
				// check if any overrides exits then the overridden rule count is same or not
				if(elgRlsExecuted) {
					if(canOverrideRuleCount == overriddenRuleCount){
						this.elgRlsExecuted = true;
					}else { 
						//this.elgRlsExecuted = false;
						PTMessageUtils.showErrorMessage("Customer is ineligible. Eligibility rules can be overridden.");
						return;
					}
				}else {
					PTMessageUtils.showErrorMessage("Eligibility criteria should be verified before submitting.");
					return;
				}
				if(!isEligible()){
					PTMessageUtils.showErrorMessage("Customer is ineligible.");
					return;
				}
			}
		}

		aFinanceMain = aFinanceDetail.getFinScheduleData().getFinanceMain();
		// Write the additional validations as per below example
		// get the selected branch object from the list box
		// Do data level validations here

		isNew = aFinanceDetail.isNew();
		String tranType="";
		aFinanceDetail.setLovDescIsQDE(true);

		if(isWorkFlowEnabled()){
			tranType =PennantConstants.TRAN_WF;
			if (StringUtils.trimToEmpty(aFinanceMain.getRecordType()).equals("")){
				aFinanceMain.setVersion(aFinanceMain.getVersion()+1);
				if(isNew){
					aFinanceMain.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else{
					aFinanceMain.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aFinanceDetail.setNewRecord(true);
				}
			}
		}else{
			aFinanceMain.setVersion(aFinanceMain.getVersion()+1);
			if(isNew){
				tranType =PennantConstants.TRAN_ADD;
			}else{
				tranType =PennantConstants.TRAN_UPD;
			}
		}
		if (this.checkListTab.isVisible()) {
			doSave_checkList(aFinanceDetail);
		}else {
			aFinanceDetail.setFinanceCheckList(null);
		}
		
		// save it to database
		try {
			aFinanceDetail.getFinScheduleData().setFinanceMain(aFinanceMain);
			if(doProcess(aFinanceDetail,tranType)){
				refreshList();
				closeDialog(this.window_FinanceMainQDEDialog, "FinanceMain");
			}

		} catch (final DataAccessException e) {
			logger.error(e);
			showMessage(e);
		}
		logger.debug("Leaving");
	}
	/**
	 * This method set the checklist details to aFinanceDetail
	 * @param aFinanceDetail
	 */
	private void doSave_checkList(FinanceDetail aFinanceDetail) {
		logger.debug("Entering ");
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("financeMainQDEDialogCtrl", this);
		map.put("userAction", this.userAction.getSelectedItem().getLabel());
		Events.sendEvent("onChkListValidation", checkListChildWindow, map);

		List<FinanceCheckListReference> chkList = aFinanceDetail.getFinanceCheckList();
		Map<Long,Long> selAnsCountMap = new HashMap<Long, Long>();
		chkList = getFinanceDetail().getFinanceCheckList();
		selAnsCountMap =getFinanceDetail().getLovDescSelAnsCountMap();

		if(chkList!=null){
			aFinanceDetail.setFinanceCheckList(chkList);
			aFinanceDetail.setLovDescSelAnsCountMap(selAnsCountMap);
		}
		logger.debug("Leaving ");
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++ WorkFlow Creations ++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	private String getServiceTasks(String taskId, FinanceMain financeMain, String finishedTasks) {
		logger.debug("Entering");

		String serviceTasks = getWorkFlow().getOperationRefs(taskId, financeMain);

		if (!"".equals(finishedTasks)) {
			String[] list = finishedTasks.split(";");

			for (int i = 0; i < list.length; i++) {
				serviceTasks = serviceTasks.replace(list[i] + ";", "");
			}
		}
		logger.debug("Leaving");
		return serviceTasks;
	}

	private void setNextTaskDetails(String taskId, FinanceMain financeMain) {
		logger.debug("Entering");

		// Set the next task id
		String action = userAction.getSelectedItem().getLabel();
		String nextTaskId = StringUtils.trimToEmpty(financeMain.getNextTaskId());

		if ("".equals(nextTaskId)) {
			if ("Save".equals(action)) {
				nextTaskId = taskId + ";";
			}
		} else {
			if (!"Save".equals(action)) {
				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
			}
		}

		if ("".equals(nextTaskId)) {
			nextTaskId = getWorkFlow().getNextTaskIds(taskId, financeMain);
		}

		// Set the role codes for the next tasks
		String nextRoleCode = "";

		if ("".equals(nextTaskId)) {
			nextRoleCode = getWorkFlow().firstTask.owner;
		} else {
			String[] nextTasks = nextTaskId.split(";");

			if (nextTasks.length > 0) {
				for (int i = 0; i < nextTasks.length; i++) {
					if (nextRoleCode.length() > 1) {
						nextRoleCode = nextRoleCode + ",";
					}
					nextRoleCode = getWorkFlow().getTaskOwner(nextTasks[i]);
				}
			}
		}

		financeMain.setTaskId(taskId);
		financeMain.setNextTaskId(nextTaskId);
		financeMain.setRoleCode(getRole());
		financeMain.setNextRoleCode(nextRoleCode);
		logger.debug("Leaving");

	}	


	/**
	 * Set the workFlow Details List to Object
	 * 
	 * @param aFinanceMain
	 *            (FinanceMain)
	 * 
	 * @param tranType
	 *            (String)
	 * 
	 * @return boolean
	 * 
	 */
	private boolean doProcess(FinanceDetail aFinanceDetail,String tranType){
		logger.debug("Entering");
		boolean processCompleted=true;
		AuditHeader auditHeader =  null;

		FinanceMain aFinanceMain = aFinanceDetail.getFinScheduleData().getFinanceMain(); 
		aFinanceMain.setLastMntBy(getUserWorkspace().getLoginUserDetails().getLoginUsrID());
		aFinanceMain.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aFinanceMain.setUserDetails(getUserWorkspace().getLoginUserDetails());

		aFinanceDetail.getFinScheduleData().setFinReference(aFinanceMain.getFinReference());
		aFinanceDetail.getFinScheduleData().setFinanceMain(aFinanceMain);
		aFinanceDetail.setUserDetails(getUserWorkspace().getLoginUserDetails());

		if (isWorkFlowEnabled()) {
			String taskId = getWorkFlow().getTaskId(getRole());
			//Upgraded to ZK-6.5.1.1 Added casting to String 	
			aFinanceMain.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			// Check for service tasks. If one exists perform the task(s)
			String finishedTasks = "";
			String serviceTasks = getServiceTasks(taskId, aFinanceMain,finishedTasks);

			while (!"".equals(serviceTasks)) {
				String method = serviceTasks.split(";")[0];

				if (StringUtils.trimToEmpty(method).contains("Dedup")) {
					aFinanceDetail = FetchDedupDetails.getLoanDedup(getRole(),
							aFinanceDetail, this.window_FinanceMainQDEDialog);
					if(aFinanceDetail.getFinScheduleData().getFinanceMain().isDedupFound() && !aFinanceDetail.getFinScheduleData().getFinanceMain().isSkipDedup()){
						processCompleted =false;						
					}else{
						processCompleted =true;	
					}

				}else if (StringUtils.trimToEmpty(method).contains("Blacklist")) {
					aFinanceDetail.getFinScheduleData().getFinanceMain().setBlacklisted(false);
					processCompleted =true;	

				} else {
					setNextTaskDetails(taskId, aFinanceMain);
					aFinanceDetail.getFinScheduleData().setFinanceMain(aFinanceMain);
					auditHeader = getAuditHeader(aFinanceDetail, PennantConstants.TRAN_WF);
					processCompleted = doSaveProcess(auditHeader, method);
					System.out.println("Testing");
				}

				if (!processCompleted) {
					break;
				}

				finishedTasks += (method + ";");
				serviceTasks = getServiceTasks(taskId,aFinanceMain, finishedTasks);
			}

			// Check whether to proceed further or not
			String nextTaskId = getWorkFlow().getNextTaskIds(taskId, aFinanceMain);

			if (processCompleted && nextTaskId.equals(taskId + ";")) {
				processCompleted = false;
			}

			// Proceed further to save the details in WorkFlow
			if (processCompleted) {
				if (!"".equals(nextTaskId) || "Save".equals(userAction.getSelectedItem().getLabel())) {
					setNextTaskDetails(taskId, aFinanceMain);
					aFinanceDetail.getFinScheduleData().setFinanceMain(aFinanceMain);
					auditHeader = getAuditHeader(aFinanceDetail, tranType);
					processCompleted = doSaveProcess(auditHeader, null);
				}
			}
		} else {
			auditHeader = getAuditHeader(aFinanceDetail, tranType);
			processCompleted = doSaveProcess(auditHeader, null);
		}
		logger.debug("Leaving");
		return processCompleted;
	}

	/**
	 * Get the result after processing DataBase Operations
	 * 
	 * @param auditHeader
	 *            (AuditHeader)
	 * 
	 * @param method
	 *            (String)
	 * 
	 * @return boolean
	 * 
	 */
	private boolean doSaveProcess(AuditHeader auditHeader,String method){
		logger.debug("Entering");
		boolean processCompleted=false;
		int retValue=PennantConstants.porcessOVERIDE;

		FinanceDetail afinanceDetail = (FinanceDetail) auditHeader.getAuditDetail().getModelData();
		FinanceMain aFinanceMain = afinanceDetail.getFinScheduleData().getFinanceMain();

		try {

			while(retValue==PennantConstants.porcessOVERIDE){

				if (StringUtils.trimToEmpty(method).equalsIgnoreCase("")){
					if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)){
						auditHeader = getFinanceDetailService().delete(auditHeader,false);

					}else{
						auditHeader = getFinanceDetailService().saveOrUpdate(auditHeader,false);	
					}

				}else{
					if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)){
						auditHeader = getFinanceDetailService().doApprove(auditHeader,false);

						if(aFinanceMain.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)){

						}

					}else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)){
						auditHeader = getFinanceDetailService().doReject(auditHeader,false);
						if(aFinanceMain.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){

						}

					}else{
						auditHeader.setErrorDetails(new ErrorDetails(PennantConstants.ERR_9999, Labels.getLabel("InvalidWorkFlowMethod"), null));
						retValue = ErrorControl.showErrorControl(this.window_FinanceMainQDEDialog, auditHeader);
						return processCompleted; 
					}
				}

				auditHeader =	ErrorControl.showErrorDetails(this.window_FinanceMainQDEDialog, auditHeader);
				retValue = auditHeader.getProcessStatus();

				if (retValue==PennantConstants.porcessCONTINUE){
					processCompleted=true;
				}
				if (retValue==PennantConstants.porcessOVERIDE){
					auditHeader.setOveride(true);
					auditHeader.setErrorMessage(null);
					auditHeader.setInfoMessage(null);
					auditHeader.setOverideMessage(null);
				}
			}
		} catch (InterruptedException e) {
			logger.error(e);
			e.printStackTrace();
		} catch (AccountNotFoundException e) {
			logger.error(e);
			e.printStackTrace();
		}
		setOverideMap(auditHeader.getOverideMap());

		logger.debug("return Value:" + processCompleted);
		logger.debug("Leaving");
		return processCompleted;
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public void setValidationOn(boolean validationOn) {
		this.validationOn = validationOn;
	}

	public boolean isValidationOn() {
		return this.validationOn;
	}

	/**
	 * @return the financeDetail
	 */
	public FinanceDetail getFinanceDetail() {
		return financeDetail;
	}

	/**
	 * @param financeDetail the financeDetail to set
	 */
	public void setFinanceDetail(FinanceDetail financeDetail) {
		this.financeDetail = financeDetail;
	}

	/**
	 * @return the financeDetailService
	 */
	public FinanceDetailService getFinanceDetailService() {
		return financeDetailService;
	}

	/**
	 * @param financeDetailService the financeDetailService to set
	 */
	public void setFinanceDetailService(FinanceDetailService financeDetailService) {
		this.financeDetailService = financeDetailService;
	}

	public void setFinanceMainListCtrl(FinanceMainListCtrl financeMainListCtrl) {
		this.financeMainListCtrl = financeMainListCtrl;
	}

	public FinanceMainListCtrl getFinanceMainListCtrl() {
		return this.financeMainListCtrl;
	}

	public PagedListService getPagedListService() {
		return pagedListService;
	}

	public void setPagedListService(PagedListService pagedListService) {
		this.pagedListService = pagedListService;
	}

	private AuditHeader getAuditHeader(FinanceDetail afinanceDetail, String tranType){
		AuditDetail auditDetail = new AuditDetail(tranType, 1, afinanceDetail.getBefImage(), afinanceDetail);   
		return new AuditHeader(afinanceDetail.getFinScheduleData().getFinReference(),null,null,null,auditDetail,afinanceDetail.getUserDetails(),getOverideMap());
	}

	public boolean isNotes_Entered() {
		return notes_Entered;
	}

	public void setNotes_Entered(boolean notes_Entered) {
		this.notes_Entered = notes_Entered;
	}

	public void setOverideMap(HashMap<String, ArrayList<ErrorDetails>> overideMap) {
		this.overideMap = overideMap;
	}

	public HashMap<String, ArrayList<ErrorDetails>> getOverideMap() {
		return overideMap;
	}

	public FinanceMain getPrvFinanceMain() {
		return prvFinanceMain;
	}

	public void setCheckListDataChanged(boolean checkListDataChanged) {
		this.checkListDataChanged = checkListDataChanged;
	}

	public boolean isCheckListDataChanged() {
		return checkListDataChanged;
	}

	public boolean isEligible() {
		return eligible;
	}

	public void setEligible(boolean eligible) {
		this.eligible = eligible;
	}
	
	public CustomerIncomeService getCustomerIncomeService() {
		return customerIncomeService;
	}
	public void setCustomerIncomeService(CustomerIncomeService customerIncomeService) {
		this.customerIncomeService = customerIncomeService;
	}

	/**
	 * to fill list box in eligibility rule Tab <br>
	 * IN FinanceMainDialogCtrl.java
	 * 
	 * @param financeReferenceDetail
	 * @param listbox
	 *            void
	 */
	public void dofillEligibilityListbox(List<FinanceReferenceDetail> financeReferenceDetail,
			Listbox listbox, boolean execute) {
		logger.debug("Entering ");
		listbox.getItems().clear();
		canOverrideRuleCount = 0 ;
		overriddenRuleCount = 0;
		finMinElgAmt = null;
		finMinOvrElgAmt = null;
		String finccy = getFinanceDetail().getFinScheduleData().getFinanceMain().getFinCcy();
		
		if (financeReferenceDetail != null) {
			this.financeElgreferenceTab.setVisible(false);
			for (int i = 0; i < financeReferenceDetail.size(); i++) {
				FinanceReferenceDetail finrefdet = financeReferenceDetail.get(i);
				if (finrefdet.getMandInputInStage().contains(getRole()) && finrefdet.isIsActive()) {
					this.financeElgreferenceTab.setVisible(true);
					Listitem item = new Listitem();
					Listcell lc;
					lc = new Listcell(finrefdet.getLovDescCodelov());
					lc.setParent(item);
					lc = new Listcell(finrefdet.getLovDescRefDesc());
					lc.setParent(item);
					lc = new Listcell(finrefdet.getLovDescNamelov());
					lc.setParent(item);

					final Checkbox cbCanOverride = new Checkbox();
					cbCanOverride.setDisabled(true);
					cbCanOverride.setChecked(finrefdet.isOverRide());
					if(finrefdet.isOverRide()) {
						canOverrideRuleCount = canOverrideRuleCount+1;
					}
					lc = new Listcell();
					lc.appendChild(cbCanOverride);
					lc.setParent(item);

					lc = new Listcell(String.valueOf(finrefdet.getOverRideValue())+" %");
					lc.setParent(item);

					BigDecimal originalVal = null;
					if (execute) {
						lc = new Listcell();
						originalVal =  getElgResult(finrefdet, finccy).getLovDescRuleResult();
						if(originalVal != null) {
							if(finMinElgAmt == null) {
								finMinElgAmt = originalVal;
							} else if(finMinElgAmt.compareTo(originalVal) > 0) {
								finMinElgAmt = originalVal;
							}
							lc.setLabel(PennantAppUtil.amountFormate(originalVal.multiply(new BigDecimal(100)),
									getFinanceDetail().getFinScheduleData().getFinanceMain().getLovDescFinFormatter()));
						}
						else { 
							lc.setLabel("");
						}
					} else {
						lc = new Listcell("");
					}
					lc.setParent(item);

					BigDecimal overriddenVal = null;
					if(finMinElgAmt != null) {
						//Calculate value if rule result is not equal -1
						if(finMinElgAmt.compareTo(new BigDecimal(-1)) != 0) {
							if(originalVal != null) {
								overriddenVal = new BigDecimal(finrefdet.getOverRideValue()).multiply(
										originalVal.divide(new BigDecimal(100)));
								overriddenVal = overriddenVal.add(originalVal);
								if(finMinOvrElgAmt == null) {
									finMinOvrElgAmt = overriddenVal;
								} else if(finMinOvrElgAmt.compareTo(overriddenVal) > 0) {
									finMinOvrElgAmt = overriddenVal;
								}
							}
						}else {
							setEligible(false);
						}
					}
					lc = new Listcell();
					if(overriddenVal != null){
						lc.setLabel(PennantAppUtil.amountFormate(overriddenVal.multiply(new BigDecimal(100)),
								getFinanceDetail().getFinScheduleData().getFinanceMain().getLovDescFinFormatter()));
					}else {
						lc.setLabel("");
					}
					lc.setParent(item);
					item.setAttribute("data", finrefdet);
					if(finrefdet.isOverRide() && execute) {
						lc = new Listcell();
						//Check whether finamount is greaterthan ruleresult value
						// then add checkbox otherwise increment override count
						if((originalVal != null && overriddenVal != null ) && (this.finAmount.getValue().compareTo(originalVal) > 0 && 
								this.finAmount.getValue().compareTo(overriddenVal) <= 0 )) {
							cbElgOverride = new Checkbox();
							lc.appendChild(cbElgOverride);
							item.setTooltiptext(Labels.getLabel("listitem_ElgRule_tooltiptext"));
							cbElgOverride.addForward("onCheck", window_FinanceMainQDEDialog, "onElgRuleItemChecked", item);
						} else {
							lc.setLabel("");
							overriddenRuleCount = overriddenRuleCount+1;
						}
						lc.setParent(item);
					} else {
						lc = new Listcell("");
						lc.setParent(item);
					}
					listbox.appendChild(item);
				}
			}
			if(execute) {
				if(finMinElgAmt != null && this.finAmount.getValue().compareTo(finMinElgAmt) > 0) {
					setEligible(false);
				}
				if(isEligible()) {	
					if(finMinElgAmt == null) {
						finMinElgAmt = this.finAmount.getValue();
					}
					this.label_ElgRuleSummaryVal.setStyle("font-weight:bold;font-size:11px;color:green;");
					this.label_ElgRuleSummaryVal.setValue(Labels.getLabel("label_Elg.value",
							new String[] {PennantAppUtil.amountFormate(finMinElgAmt.multiply(
									new BigDecimal(100)),getFinanceDetail().getFinScheduleData().getFinanceMain().getLovDescFinFormatter())}));
				} else { 
					this.label_ElgRuleSummaryVal.setStyle("font-weight:bold;font-size:11px;color:red;");
					this.label_ElgRuleSummaryVal.setValue(Labels.getLabel("label_InElg.value"));
				}
			}
		}
		logger.debug("Leaving ");
	}

	/**
	 * Mehtod to capture event when eligible rule item double clicked
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onElgRuleItemChecked(ForwardEvent event) throws Exception {
		logger.debug("Entering" + event.toString());
		Listitem item = (Listitem) event.getData();
		Listcell lc = null;
		Checkbox cb = null;
		if (item != null && isEligible()) {
			FinanceReferenceDetail data = (FinanceReferenceDetail) item.getAttribute("data");
			final String msg = data.getLovDescNamelov();
			final String title = Labels.getLabel("message.Overide");

			MultiLineMessageBox.doErrorTemplate();
			int conf = MultiLineMessageBox.show(msg, title,
					MultiLineMessageBox.CANCEL | MultiLineMessageBox.IGNORE,
					MultiLineMessageBox.EXCLAMATION, true);

			if (conf == MultiLineMessageBox.IGNORE) {
				logger.debug("doClose: Yes");
				if(item.getChildren().get(7) instanceof Listcell){
					lc = (Listcell)item.getChildren().get(7);
					cb = (Checkbox)lc.getChildren().get(0);
					cb.setChecked(true);
					cb.setDisabled(true);
					overriddenRuleCount = overriddenRuleCount+1;
					this.elgRlsExecuted = true;
				}
			}else if(conf == MultiLineMessageBox.CANCEL){
				if(item.getChildren().get(7) instanceof Listcell){
					logger.debug("doClose: No");
					lc = (Listcell)item.getChildren().get(7);
					cb = (Checkbox)lc.getChildren().get(0);
					cb.setChecked(false);
				}
			}
			if(canOverrideRuleCount==overriddenRuleCount){
				this.label_ElgRuleSummaryVal.setStyle("font-weight:bold;font-size:11px;color:green;");
				this.label_ElgRuleSummaryVal.setValue(Labels.getLabel("label_Elg.value",
						new String[] { finMinOvrElgAmt == null?"":String.valueOf(finMinOvrElgAmt) }));
				setEligible(true);
			}
		}
		logger.debug("Leaving" + event.toString());
	}	

	/**
	 * TO Show the Eligibility rule Result in Eligibility Tab <br>
	 * IN FinanceMainDialogCtrl.java
	 * 
	 * @param financeReferenceDetail
	 * @return String
	 */
	private FinanceReferenceDetail getElgResult(FinanceReferenceDetail financeReferenceDetail, String finccy) {
		logger.debug("Entering ");
		try {
			getFinanceDetail().getCustomerEligibilityCheck().setCustTotalIncome(
					getCustomerIncomeService().getTotalIncomeByCustomer(this.custID.longValue()));
			String result = getRuleExecutionUtil().executeRule(financeReferenceDetail.getLovDescElgRuleValue(), 
					getFinanceDetail().getCustomerEligibilityCheck(),SystemParameterDetails.getGlobaVariableList(), finccy).toString();
			if(new BigDecimal(result).compareTo(new BigDecimal(-1))==0 && !financeReferenceDetail.isOverRide()){
				setEligible(false);
			}
			financeReferenceDetail.setLovDescRuleResult(new BigDecimal(result));

		} catch (Exception e) {
			logger.debug(e);
			financeReferenceDetail.setLovDescRuleResult(null);	
		}
		logger.debug("Entering ");
		return financeReferenceDetail;
	}

	/**
	 * when user clicks on execute eligibility rule button
	 * @param event
	 * @throws Exception
	 */
	public void onClick$btnElgRule(Event event) throws Exception {
		logger.debug("Entering "+event.toString());
		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();
		doStoreInitValues();
		doSetValidation();
		doSetLOVValidation();
		try {
			this.finAmount.getValue();
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			this.custID.getValue();
			this.lovDescCustCIF.getValue();
		} catch (WrongValueException we) {
			wve.add(we);
		}
		this.elgRlsExecuted = true;
		setEligible(true);
		this.label_ElgRuleSummaryVal.setValue("");
		this.finMinElgAmt = null;
		this.finMinOvrElgAmt = null;
		showErrorDetails(wve, finMainbasicDetailsTab);
		dofillEligibilityListbox(getFinanceDetail().getEligibilityRuleList(), this.listBoxFinElgRef, true);
		logger.debug("Leaving "+event.toString());
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug("Entering");
		setValidationOn(true);
		if (!this.finAmount.isReadonly()) {
			this.finAmount.setConstraint(new AmountValidator(18, 0,
					Labels.getLabel("label_FinanceMainQDEDialog_FinAmount.value"),false));
		}
		if (!this.downPayment.isDisabled()) {
			this.downPayment.setConstraint(new AmountValidator(18, 0, Labels
					.getLabel("label_FinanceMainQDEDialog_DownPayment.value"),
					false));
		}
	}

	/**
	 * Method to set LOV field validation
	 */
	private void doSetLOVValidation() {
		logger.debug("Entering");
		if (this.btnSearchCustCIF.isVisible()) {
			this.lovDescCustCIF.setConstraint("NO EMPTY:" + Labels.getLabel("FIELD_NO_EMPTY", new String[] { Labels.getLabel("label_FinanceMainDialog_CustID.value") }));
		}
		logger.debug("Leaving");
	}

	/**
	 * Writes the showErrorDetails method for .<br>
	 * displaying exceptions if occurred
	 */
	private void showErrorDetails(ArrayList<WrongValueException> wve, Tab tab) {
		logger.debug("Entering");
		if (wve.size() > 0) {
			logger.debug("Throwing occured Errors By using WrongValueException");
			tab.setSelected(true);
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

	public void setRuleExecutionUtil(RuleExecutionUtil ruleExecutionUtil) {
		this.ruleExecutionUtil = ruleExecutionUtil;
	}
	public RuleExecutionUtil getRuleExecutionUtil() {
		return ruleExecutionUtil;
	}

}
