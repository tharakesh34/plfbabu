package com.pennant.webui.finance.billing;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.SuspendNotAllowedException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Longbox;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.AccountSelectionBox;
import com.pennant.CurrencyBox;
import com.pennant.Interface.service.AccountInterfaceService;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.Notes;
import com.pennant.backend.model.amtmasters.ExpenseType;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceDisbursement;
import com.pennant.backend.model.finance.contractor.ContractorAssetDetail;
import com.pennant.backend.service.accounts.AccountsService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.search.Filter;
import com.pennant.util.ErrorControl;
import com.pennant.util.PennantAppUtil;
import com.pennant.util.Constraint.AmountValidator;
import com.pennant.util.Constraint.PTDateValidator;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.util.Constraint.PercentageValidator;
import com.pennant.webui.finance.financemain.DisbursementDetailDialogCtrl;
import com.pennant.webui.util.ButtonStatusCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.MultiLineMessageBox;
import com.pennant.webui.util.PTMessageUtils;
import com.pennant.webui.util.searchdialogs.ExtendedSearchListBox;
import com.rits.cloning.Cloner;

public class IstisnaDisbursementDetailDialogCtrl extends GFCBaseCtrl implements Serializable {

	private static final long serialVersionUID = -6959194080451993569L;
	private final static Logger logger = Logger.getLogger(IstisnaDisbursementDetailDialogCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window 		window_IstisnaDisbursement;			// autowired
	
	// common fields 
	protected Datebox 		 disbDate; 				         	// autowired
	protected Longbox        disbBenificiary;					// autowired
 	protected Textbox        lovDescCustCIF;					// autowired
	protected Textbox        lovDescDisbExpType;				// autowired
	protected CurrencyBox    disbAmount;						// autowired
	protected Longbox        disbExpType;						// autowired
	
	protected Decimalbox     disbRetPerc;						// autowired
	protected Decimalbox     disbRetAmount;						// autowired
	protected Textbox        disbRemarks;						// autowired
	protected Checkbox       autoDisb;							// autowired
	
	protected Button                     btnSearchBenfCIF;					// autowired
 	protected Button                     btnSearchDisbExpType;				// autowired
	protected AccountSelectionBox        disbAccountId;						// autowired


	protected Label          benfShrtName;						// autowired
	protected Textbox		 moduleName;
	
	protected Label 		recordStatus; 				       	// autowired
	protected Radiogroup 	userAction;
	protected Groupbox 		groupboxWf;

	// not auto wired vars
	private FinanceDisbursement financeDisbursement; 	// overhanded per param
	private FinanceDetail financeDetail; 	// overhanded per param

	// old value vars for edit mode. that we can check if something
	// on the values are edited since the last init.
	
	private transient Date 			oldVar_disbDate;
	private transient long 		    oldVar_disbBenificiary;
 	private transient BigDecimal 	oldVar_disbAmount;
	private transient String 		oldVar_disbAccountId;
	private transient BigDecimal 	oldVar_disbRetPerc;
	private transient BigDecimal 	oldVar_disbRetAmount;
	private transient long 		oldVar_disbExpType;
	private transient String 		oldVar_disbRemarks;
	private transient boolean 		oldVar_autoDisb;
	
	private transient String 		oldVar_recordStatus;

	private transient boolean validationOn;
	private boolean notes_Entered=false;

	// Button controller for the CRUD buttons
	private transient final String btnCtroller_ClassPrefix = "button_FinanceDisbursementDialog_";
	private transient ButtonStatusCtrl btnCtrl;
	protected Button btnNew; 			// autowire
	protected Button btnEdit; 			// autowire
	protected Button btnDelete; 		// autowire
	protected Button btnSave; 			// autowire
	protected Button btnCancel; 		// autowire
	protected Button btnClose; 			// autowire
	protected Button btnHelp; 			// autowire
	protected Button btnNotes; 			// autowire

	private boolean newRecord=false;
	private boolean newDisbursement=false;
	
	private List<FinanceDisbursement> financeDisbursements;
	private DisbursementDetailDialogCtrl  disbursementDetailDialogCtrl;
	protected JdbcSearchObject<FinanceDisbursement> newSearchObject ;
	private  int formatter = 0;
	private String currency = "";
	private Date startDate = null;
	private Date grcEndDate = null;
	private boolean isEnq = false;	
	
	private AccountsService  accountsService;
	private AccountInterfaceService accountInterfaceService;
	private ContractorAssetDetail contractorAssetDetail;
	private List<ContractorAssetDetail> contractorAssetDetails;

	/**
	 * default constructor.<br>
	 */
     public IstisnaDisbursementDetailDialogCtrl() {
	           super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * ZUL-file is called with a parameter for a selected FinanceDisbursement object in a
	 * Map.
	 * 
	 * @param event
	 * @throws Exception
	 */	
	@SuppressWarnings("unchecked")
	public void onCreate$window_IstisnaDisbursement(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		// get the params map that are overhanded by creation.
		final Map<String, Object> args = getCreationArgsMap(event);

		// READ OVERHANDED params !
		if (args.containsKey("financeDisbursement")) {
			this.financeDisbursement = (FinanceDisbursement) args.get("financeDisbursement");
			Cloner cloner = new Cloner();
			FinanceDisbursement befImage = cloner.deepClone(financeDisbursement);
			this.financeDisbursement.setBefImage(befImage);
			setFinanceDisbursement(this.financeDisbursement);
		} else {
			setFinanceDisbursement(null);
		}
		
		if (args.containsKey("currency")) {
			this.currency = (String) args.get("currency");
		} 
		
		if (args.containsKey("isEnq")) {
			this.isEnq = (Boolean) args.get("isEnq");
		} 
		
		if (args.containsKey("startDate")) {
			this.startDate = (Date) args.get("startDate");
		} 
		if (args.containsKey("grcEndDate")) {
			this.grcEndDate = (Date) args.get("grcEndDate");
		} 
		
		if (args.containsKey("FinanceDetail")) {
			this.financeDetail = (FinanceDetail) args.get("FinanceDetail");
		} 
		
		
		if(getFinanceDisbursement().isNewRecord()){
			setNewRecord(true);
		}

		if(args.containsKey("disbursementDetailDialogCtrl")){

			setDisbursementDetailDialogCtrl((DisbursementDetailDialogCtrl) args.get("disbursementDetailDialogCtrl"));
			setNewDisbursement(true);

			if(args.containsKey("newRecord")){
				setNewRecord(true);
			}else{
				setNewRecord(false);
			}
			
			this.financeDisbursement.setWorkflowId(0);
			if(args.containsKey("roleCode")){
				setRole((String) args.get("roleCode"));
				getUserWorkspace().alocateRoleAuthorities(getRole(), "FinanceDisbursementDialog");
			}
			
			formatter = this.disbursementDetailDialogCtrl.getFinanceDetail().getFinScheduleData().getFinanceMain().getLovDescFinFormatter();
		}
		
		if(args.containsKey("formatter")){
			this.formatter = (Integer) args.get("formatter");
		}
		
		if(args.containsKey("ContractorAssetDetail")){
			this.contractorAssetDetail = (ContractorAssetDetail) args.get("ContractorAssetDetail");
		}
		if(args.containsKey("ContractorAssetDetails")){
			this.contractorAssetDetails = (List<ContractorAssetDetail>) args.get("ContractorAssetDetails");
		}
		doLoadWorkFlow(this.financeDisbursement.isWorkflow(),this.financeDisbursement.getWorkflowId(),
				this.financeDisbursement.getNextTaskId());

		if (isWorkFlowEnabled()){
			this.userAction	= setListRecordStatus(this.userAction);
			getUserWorkspace().alocateRoleAuthorities(getRole(), "FinanceDisbursementDialog");
		}
		
		/* set components visible dependent of the users rights */
		doCheckRights();

		// set Field Properties
		doSetFieldProperties();
		doShowDialog(getFinanceDisbursement());
		logger.debug("Leaving" + event.toString());
	}
	
	/**
	 * Set the properties of the fields, like maxLength.<br>
	 * @throws InterruptedException 
	 * @throws SuspendNotAllowedException 
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");
		//Empty sent any required attributes

		this.disbDate.setFormat(PennantConstants.dateFormat);
		this.disbAmount.setMandatory(true);
		this.disbAmount.setFormat(PennantAppUtil.getAmountFormate(formatter));
		this.disbAmount.setMaxlength(18);
		this.disbRetPerc.setFormat(PennantConstants.percentageFormate2);
		this.disbRetPerc.setMaxlength(6);
		this.disbRetAmount.setFormat(PennantAppUtil.getAmountFormate(formatter));
		this.disbRetAmount.setMaxlength(18);
		this.disbRemarks.setMaxlength(100);
		
		String finType = StringUtils.trimToEmpty(getFinanceDetail().getFinScheduleData().getFinanceMain().getFinType());
		String finCCY = StringUtils.trimToEmpty(getFinanceDetail().getFinScheduleData().getFinanceMain().getFinCcy());
		int finFormatter = getFinanceDetail().getFinScheduleData().getFinanceMain().getLovDescFinFormatter();
		
		if(getFinanceDisbursement().getDisbType().equals("E")){
			this.disbAccountId.setFinanceDetails(finType, PennantConstants.ISTISNA_EXPENCE_ACCNT_EVENT_CODE, finCCY);
			this.disbAccountId.setFormatter(finFormatter);
	        this.disbAccountId.setCustCIF("");
		} else if(getFinanceDisbursement().getDisbType().equals("A")) {
			this.disbAccountId.setFinanceDetails(finType, PennantConstants.FinanceAccount_ISCONTADV, finCCY);
			this.disbAccountId.setFormatter(finFormatter);
	        this.disbAccountId.setCustCIF(this.contractorAssetDetail.getLovDescCustCIF());
		} else if(getFinanceDisbursement().getDisbType().equals("C")) {
			this.disbAccountId.setFinanceDetails(finType, PennantConstants.FinanceAccount_ISCNSLTACCT , finCCY);
			this.disbAccountId.setFormatter(finFormatter);
	        this.disbAccountId.setCustCIF(this.contractorAssetDetail.getLovDescCustCIF());
		} else if(getFinanceDisbursement().getDisbType().equals("B")) {
			this.disbAccountId.setFinanceDetails(finType, PennantConstants.FinanceAccount_ISBILLACCT , finCCY);
			this.disbAccountId.setFormatter(finFormatter);
	        this.disbAccountId.setCustCIF(this.contractorAssetDetail.getLovDescCustCIF());
		}
		 this.disbAccountId.setMandatoryStyle(true);
		if (isWorkFlowEnabled()){
			this.groupboxWf.setVisible(true);
		}else{
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
		getUserWorkspace().alocateAuthorities("FinanceDisbursementDialog", getRole());
		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_FinanceDisbursementDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_FinanceDisbursementDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_FinanceDisbursementDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_FinanceDisbursementDialog_btnSave"));
		this.btnCancel.setVisible(false);
		
		/* create the Button Controller. Disable not used buttons during working */
		this.btnCtrl = new ButtonStatusCtrl(getUserWorkspace(), this.btnCtroller_ClassPrefix,
				true, this.btnNew,this.btnEdit, this.btnDelete, this.btnSave,
				this.btnCancel, this.btnClose,this.btnNotes);

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
	public void onClose$window_FinanceDisbursementDialog(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		doClose();
		logger.debug("Leaving");
	}

	/**
	 * when the "save" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 */
	public void onClick$btnSave(Event event) throws InterruptedException, IllegalAccessException, InvocationTargetException {
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
		PTMessageUtils.showHelpWindow(event, window_IstisnaDisbursement);
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
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 */
	public void onClick$btnDelete(Event event) throws InterruptedException, IllegalAccessException, InvocationTargetException {
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
	 * @throws InterruptedException
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 */
	public void onClick$btnClose(Event event) throws InterruptedException, IllegalAccessException, InvocationTargetException {
		logger.debug("Entering" + event.toString());
		try {
			doClose();
		} catch (final WrongValueException e) {
			logger.error(e);
			throw e;
		}
		logger.debug("Leaving" + event.toString());
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// ++++++++++++++++++++++++ GUI operations +++++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++


	/**
	 * Closes the dialog window. <br>
	 * <br>
	 * Before closing we check if there are unsaved changes in <br>
	 * the components and ask the user if saving the modifications. <br>
	 * 
	 * @throws InterruptedException
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 * 
	 */
	private void doClose() throws InterruptedException, IllegalAccessException, InvocationTargetException {
		logger.debug("Entering");
		boolean close = true;

		if (!isEnq && isDataChanged()) {
			logger.debug("Data Changed(): True");

			// Show a confirm box
			final String msg = Labels.getLabel("message_Data_Modified_Save_Data_YesNo");
			final String title = Labels.getLabel("message.Information");

			MultiLineMessageBox.doSetTemplate();
			int conf = MultiLineMessageBox.show(msg, title, 
					MultiLineMessageBox.YES| MultiLineMessageBox.NO, MultiLineMessageBox.QUESTION,true);

			if (conf==MultiLineMessageBox.YES){
				logger.debug("doClose: Yes");
				doSave();
				close = false;
			}else{
				logger.debug("doClose: No");
			}
		}else{
			logger.debug("Data Changed(): false");
		}
		if (close) {
			closeWindow();
		}
		logger.debug("Leaving");		
	}

	/**
	 * Method for closing Customer Selection Window 
	 * @throws InterruptedException
	 */
	public void closeWindow() throws InterruptedException{
		logger.debug("Entering");

		if(isNewDisbursement() || isEnq){
			closePopUpWindow(this.window_IstisnaDisbursement, "FinanceDisbursementDialog");
		}else{
			closeDialog(this.window_IstisnaDisbursement, "FinanceDisbursementDialog");
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
	 * @param aFinanceDisbursement
	 *            FinanceDisbursement
	 */
	public void doWriteBeanToComponents(FinanceDisbursement aFinanceDisbursement) {
		logger.debug("Entering");
		
		this.disbDate.setValue(aFinanceDisbursement.getDisbDate());
		if("B".equals(aFinanceDisbursement.getDisbType())){
			this.disbAmount.setValue(PennantAppUtil.formateAmount(aFinanceDisbursement.getDisbClaim(), formatter));
		}else{
			this.disbAmount.setValue(PennantAppUtil.formateAmount(aFinanceDisbursement.getDisbAmount(), formatter));
		}
		this.disbAccountId.setValue(PennantApplicationUtil.formatAccountNumber(aFinanceDisbursement.getDisbAccountId()));
		this.disbExpType.setValue(aFinanceDisbursement.getDisbExpType());
		this.disbRetAmount.setValue(PennantAppUtil.formateAmount(aFinanceDisbursement.getDisbRetAmount(), formatter));
		this.disbRetPerc.setValue(aFinanceDisbursement.getDisbRetPerc());
		this.disbRemarks.setValue(aFinanceDisbursement.getDisbRemarks());
		this.autoDisb.setChecked(aFinanceDisbursement.isAutoDisb());
		this.lovDescDisbExpType.setValue(aFinanceDisbursement.getLovDescDisbExpType());
		this.benfShrtName.setValue(aFinanceDisbursement.getLovDescDisbBenfShrtName());
		this.disbBenificiary.setValue(aFinanceDisbursement.getDisbBeneficiary());

		if("C".equals(getFinanceDisbursement().getDisbType())) {
			this.lovDescCustCIF.setValue(aFinanceDisbursement.getLovdescDisbBenificiary());
		} else if(contractorAssetDetail != null && StringUtils.trimToNull(contractorAssetDetail.getLovDescCustCIF()) != null){
			this.lovDescCustCIF.setValue(contractorAssetDetail.getLovDescCustCIF() + "-" + contractorAssetDetail.getLovDescCustShrtName());
		} else {
			this.lovDescCustCIF.setValue("");
		}
		this.recordStatus.setValue(aFinanceDisbursement.getRecordStatus());
		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aFinanceDisbursement
	 */
	public void doWriteComponentsToBean(FinanceDisbursement aFinanceDisbursement, ContractorAssetDetail aContractorAssetDetail) {
		logger.debug("Entering");
		doSetLOVValidation();
		
		int formatter = this.disbursementDetailDialogCtrl.getFinanceDetail().getFinScheduleData()
								.getFinanceMain().getLovDescFinFormatter();

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		try {
			aFinanceDisbursement.setDisbDate((this.disbDate.getValue()));
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			if("C".equals(aFinanceDisbursement.getDisbType())) {
				aFinanceDisbursement.setLovdescDisbBenificiary(this.lovDescCustCIF.getValue());
				aFinanceDisbursement.setLovDescDisbBenfShrtName(this.benfShrtName.getValue());
				aFinanceDisbursement.setDisbBeneficiary(this.disbBenificiary.longValue());
			} else {
				aFinanceDisbursement.setLovdescDisbBenificiary(aContractorAssetDetail.getLovDescCustCIF());
				aFinanceDisbursement.setLovDescDisbBenfShrtName(aContractorAssetDetail.getLovDescCustShrtName());
				aFinanceDisbursement.setDisbBeneficiary(aContractorAssetDetail.getCustID());
			}
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aFinanceDisbursement.setLovDescDisbExpType(this.lovDescDisbExpType.getValue());
			aFinanceDisbursement.setDisbExpType(this.disbExpType.longValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aFinanceDisbursement.setDisbAccountId(PennantApplicationUtil.unFormatAccountNumber(this.disbAccountId.getValue()));
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			if("B".equals(aFinanceDisbursement.getDisbType())){
				aFinanceDisbursement.setDisbClaim(PennantAppUtil.unFormateAmount(this.disbAmount.getValue(), formatter));
			}else{
				aFinanceDisbursement.setDisbAmount(PennantAppUtil.unFormateAmount(this.disbAmount.getValue(), formatter));
			}
		}catch (WrongValueException we ) {
			wve.add(we);
		}
	 
		try {
			if("B".equals(aFinanceDisbursement.getDisbType())){
				if(!PennantConstants.RECORD_TYPE_DEL.equals(contractorAssetDetail.getRecordType()) && 
						!PennantConstants.RECORD_TYPE_CAN.equals(contractorAssetDetail.getRecordType())) {
					if(isNewRecord()){
						aContractorAssetDetail.setTotClaimAmt(aContractorAssetDetail.getTotClaimAmt().add(aFinanceDisbursement.getDisbClaim()));
					}else{
						aContractorAssetDetail.setTotClaimAmt(aContractorAssetDetail.getTotClaimAmt().add(aFinanceDisbursement.getDisbClaim()).subtract(PennantAppUtil.unFormateAmount(this.oldVar_disbAmount, formatter)));
					}

					if(aContractorAssetDetail.getAssetValue().compareTo(aContractorAssetDetail.getTotClaimAmt()) < 0){
						throw new WrongValueException(this.disbAmount,Labels.getLabel("label_ContractorBilling_TotBillClaim") + PennantAppUtil.formateAmount(aContractorAssetDetail.getAssetValue(), formatter));
					}
				}
			}
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		
		try {
			aFinanceDisbursement.setDisbRetAmount(PennantAppUtil.unFormateAmount(this.disbRetAmount.getValue(),formatter));
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aFinanceDisbursement.setDisbRetPerc(this.disbRetPerc.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aFinanceDisbursement.setAutoDisb(this.autoDisb.isChecked());
			aFinanceDisbursement.setDisbIsActive(true);
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aFinanceDisbursement.setDisbRemarks(this.disbRemarks.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		
		doRemoveValidation();
		doRemoveLOVValidation();
		doClearMessage();

		if (wve.size()>0) {
			WrongValueException [] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}

		aFinanceDisbursement.setRecordStatus(this.recordStatus.getValue());
		setFinanceDisbursement(aFinanceDisbursement);
		logger.debug("Leaving");

	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the
	 * readOnly mode accordingly.
	 * 
	 * @param aFinanceDisbursement
	 * @throws InterruptedException
	 */
	public void doShowDialog(FinanceDisbursement aFinanceDisbursement) throws InterruptedException {
		logger.debug("Entering");

		// set ReadOnly mode accordingly if the object is new or not.
		if (isNewRecord()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.disbDate.focus();
		} else {
			this.disbDate.focus();
			if (isNewDisbursement()){
				doEdit();
			}else  if (isWorkFlowEnabled()){
				this.btnNotes.setVisible(true);
				doEdit();
			}else{
				this.btnCtrl.setBtnStatus_Enquiry();
				doReadOnly();
				btnCancel.setVisible(false);
			}
		}

		try {
			// fill the components with the data
			doWriteBeanToComponents(aFinanceDisbursement);

			// stores the initial data for comparing if they are changed
			// during user action.
			doStoreInitValues();

			if(isNewDisbursement() || isEnq){
				this.window_IstisnaDisbursement.setHeight("280px");
				this.window_IstisnaDisbursement.setWidth("80%");
				this.groupboxWf.setVisible(false);
				this.window_IstisnaDisbursement.doModal() ;
			}else{
				this.window_IstisnaDisbursement.setWidth("100%");
				this.window_IstisnaDisbursement.setHeight("100%");
				setDialog(this.window_IstisnaDisbursement);
			}

		} catch (final Exception e) {
			logger.error(e);
			PTMessageUtils.showErrorMessage(e.toString());
			this.window_IstisnaDisbursement.onClose();
		}
		logger.debug("Leaving");
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// ++++++++++++++++++++++++++++++ helpers ++++++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * Stores the initial values in member vars. <br>
	 */
	private void doStoreInitValues() {
		logger.debug("Entering");
		
		this.oldVar_disbDate = this.disbDate.getValue();
		if("C".equals(getFinanceDisbursement().getDisbType())) {
			this.oldVar_disbBenificiary = this.disbBenificiary.longValue();
		}
 		this.oldVar_disbAccountId = this.disbAccountId.getValue(); 		
		this.oldVar_disbAmount = this.disbAmount.getValue();
		this.oldVar_disbExpType = this.disbExpType.longValue();
		this.oldVar_disbRetPerc = this.disbRetPerc.getValue();
		this.oldVar_disbRetAmount = this.disbRetAmount.getValue();
		this.oldVar_disbRemarks = this.disbRemarks.getValue();
		this.oldVar_autoDisb = this.autoDisb.isChecked();
		
		this.oldVar_recordStatus = this.recordStatus.getValue();
		logger.debug("Leaving");
	}

	/**
	 * Resets the initial values from member vars. <br>
	 */
	private void doResetInitValues() {
		logger.debug("Entering");
		
		this.disbDate.setValue(this.oldVar_disbDate);
		if("C".equals(getFinanceDisbursement().getDisbType())) {
			this.disbBenificiary.setValue(this.oldVar_disbBenificiary);
		}
 		this.disbAccountId.setValue(PennantApplicationUtil.formatAccountNumber(this.oldVar_disbAccountId));
		this.disbAmount.setValue(this.oldVar_disbAmount);
		this.disbExpType.setValue(this.oldVar_disbExpType);
		this.disbRetPerc.setValue(this.oldVar_disbRetPerc);
		this.disbRetAmount.setValue(this.oldVar_disbRetAmount);
		this.disbRemarks.setValue(this.oldVar_disbRemarks);
		this.autoDisb.setChecked(this.autoDisb.isChecked());
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
	private boolean isDataChanged() {

		// To clear the Error Messages
		doClearMessage();

		if (this.oldVar_disbDate != this.disbDate.getValue()) {
			return true;
		}
		if("C".equals(getFinanceDisbursement().getDisbType())){
			if (this.oldVar_disbBenificiary != this.disbBenificiary.longValue()) {
				return true;
			}
		}
		if (this.oldVar_disbAccountId != this.disbAccountId.getValue()) {
			return true;
		}
		if (this.oldVar_disbAmount != this.disbAmount.getValue()) {
			return true;
		}
		if (this.oldVar_disbExpType != this.disbExpType.longValue()) {
			return true;
		}
		if (this.oldVar_disbRetPerc != this.disbRetPerc.getValue()) {
			return true;
		}
		if (this.oldVar_disbRetAmount != this.disbRetAmount.getValue()) {
			return true;
		}
		if (this.oldVar_disbRemarks != this.disbRemarks.getValue()) {
			return true;
		}
		if (this.oldVar_autoDisb != this.autoDisb.isChecked()) {
			return true;
		}

		return false;
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug("Entering");
		setValidationOn(true);
		
		if (!this.disbDate.isDisabled()) {
			this.disbDate.setConstraint(new PTDateValidator(Labels.getLabel("label_Istisna"+this.moduleName.getValue()+"Dialog_disbDate.value"),
					true,startDate , grcEndDate ,true));
		}
		if (!this.disbAmount.isDisabled()) {
			this.disbAmount.setConstraint(new AmountValidator(18, formatter,
					Labels.getLabel("label_Istisna"+this.moduleName.getValue()+"Dialog_disbAmount.value"), false));
		}
		if (!this.disbAccountId.isReadonly()) {
			this.disbAccountId.setConstraint(new PTStringValidator(Labels.getLabel("label_Istisna"+this.moduleName.getValue()+"Dialog_disbAccountId.value"), null, true));
		}
		if (!this.disbRetPerc.isDisabled()) {
			this.disbRetPerc.setConstraint(new PercentageValidator(5,2,
					Labels.getLabel("label_Istisna"+this.moduleName.getValue()+"Dialog_disbRetPerc.value"), true));
		}
	
		if (!this.btnSearchDisbExpType.isDisabled() && this.btnSearchDisbExpType.isVisible()) {
			this.lovDescDisbExpType.setConstraint(new PTStringValidator(Labels.getLabel("label_Istisna"+this.moduleName.getValue()+"Dialog_disbExpType.value"),null, true));
		}
		
		if (!this.btnSearchBenfCIF.isDisabled() && this.btnSearchBenfCIF.isVisible() && !this.lovDescCustCIF.isReadonly()) {
			this.lovDescCustCIF.setConstraint(new PTStringValidator(Labels.getLabel("label_Istisna"+this.moduleName.getValue()+"Dialog_disbBenificiary.value"),null, true));
		}
		
		logger.debug("Leaving");
	}

	/**
	 * Disables the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering");
		setValidationOn(false);
		
		this.disbDate.setConstraint("");
		this.disbBenificiary.setConstraint("");
 		this.disbAmount.setConstraint("");
		this.disbAccountId.setConstraint("");
		this.lovDescDisbExpType.setConstraint("");
 		this.disbRetAmount.setConstraint("");
		this.disbRetPerc.setConstraint("");
		this.disbRemarks.setConstraint("");
		
		logger.debug("Leaving");
	}

	/**
	 * Set Validations for LOV Fields
	 */
	private void doSetLOVValidation() {
	}

	/**
	 * Remove Validations for LOV Fields
	 */
	private void doRemoveLOVValidation() {
	}

	/**
	 * Remove Error Messages for Fields
	 */
	private void doClearMessage() {
		logger.debug("Entering");
		
		this.disbDate.setErrorMessage("");
		this.disbBenificiary.setErrorMessage("");
		this.lovDescDisbExpType.setErrorMessage("");
 		this.disbAmount.setErrorMessage("");
		this.disbRetPerc.setErrorMessage("");
		this.disbRetAmount.setErrorMessage("");
		this.disbRemarks.setErrorMessage("");
		this.disbExpType.setErrorMessage("");
		this.disbAccountId.setErrorMessage("");
		
		logger.debug("Leaving");
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// +++++++++++++++++++++++++ CRUD operations +++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * Deletes a FinanceDisbursement object from database.<br>
	 * 
	 * @throws InterruptedException
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 */
	private void doDelete() throws InterruptedException, IllegalAccessException, InvocationTargetException {
		logger.debug("Entering");
		Cloner cloner = new Cloner();
		final FinanceDisbursement aFinanceDisbursement = cloner.deepClone(getFinanceDisbursement());
		String tranType=PennantConstants.TRAN_WF;

		// Show a confirm box
		final String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record") 
		+ "\n\n --> " + aFinanceDisbursement.getDisbDate();
		final String title = Labels.getLabel("message.Deleting.Record");
		MultiLineMessageBox.doSetTemplate();

		int conf =  (MultiLineMessageBox.show(msg, title, 
				MultiLineMessageBox.YES| MultiLineMessageBox.NO, Messagebox.QUESTION, true));

		if (conf==MultiLineMessageBox.YES){
			logger.debug("doDelete: Yes");

			if (StringUtils.trimToEmpty(aFinanceDisbursement.getRecordType()).equals("")){
				aFinanceDisbursement.setVersion(aFinanceDisbursement.getVersion()+1);
				aFinanceDisbursement.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				aFinanceDisbursement.setNewRecord(true);

				if (isWorkFlowEnabled()){
					aFinanceDisbursement.setNewRecord(true);
					tranType=PennantConstants.TRAN_WF;
				}else{
					tranType=PennantConstants.TRAN_DEL;
				}
			}
			try {
				if(isNewDisbursement()){
					
					if("B".equals(aFinanceDisbursement.getDisbType())){
						contractorAssetDetail.setTotClaimAmt(contractorAssetDetail.getTotClaimAmt().subtract(aFinanceDisbursement.getDisbClaim()));
					}
					
					tranType=PennantConstants.TRAN_DEL;
					AuditHeader auditHeader =  newBillingProcess(aFinanceDisbursement,tranType);
					auditHeader = ErrorControl.showErrorDetails(this.window_IstisnaDisbursement, auditHeader);
					int retValue = auditHeader.getProcessStatus();
					if (retValue==PennantConstants.porcessCONTINUE || retValue==PennantConstants.porcessOVERIDE){
					getDisbursementDetailDialogCtrl().doFillDisbursementDetails(this.financeDisbursements);
					fillContractorList(aFinanceDisbursement,contractorAssetDetail);
						// send the data back to customer
						closeWindow();
					}	

				}
			}catch (DataAccessException e){
				logger.error(e);
				showMessage(e);
			}
		}
		logger.debug("Leaving");
	}

	public void fillContractorList(FinanceDisbursement aFinanceDisbursement, ContractorAssetDetail bContractorAssetDetail){

		if("B".equals(aFinanceDisbursement.getDisbType())) {
			ContractorAssetDetail acontractorAssetDetail = null ;
			int i=0;
			for (; i < contractorAssetDetails.size() ; i++) {
				acontractorAssetDetail = contractorAssetDetails.get(i);
				if(bContractorAssetDetail.getContractorId() == acontractorAssetDetail.getContractorId()){
					break;
				}
				acontractorAssetDetail= null;
			}

			if(acontractorAssetDetail != null){
				Cloner cloner = new Cloner();
				acontractorAssetDetail = cloner.deepClone(bContractorAssetDetail);


				double assetValue = acontractorAssetDetail.getAssetValue().doubleValue();			
				double amount = 0;

				if(!PennantConstants.RECORD_TYPE_DEL.equals(acontractorAssetDetail.getRecordType()) && !PennantConstants.RECORD_TYPE_CAN.equals(acontractorAssetDetail.getRecordType())) {
					if("B".equals(aFinanceDisbursement.getDisbType())){
						amount = ((acontractorAssetDetail.getTotClaimAmt().doubleValue())
								/assetValue) * 100;
						acontractorAssetDetail.setLovDescClaimPercent(PennantApplicationUtil.unFormateAmount(new BigDecimal(amount), 2));
						contractorAssetDetails.remove(i);
						contractorAssetDetails.add(i, acontractorAssetDetail);
						getDisbursementDetailDialogCtrl().doFillContractorDetails(contractorAssetDetails);
					}
				}

			}
		}
	}
	
	
	
	/**
	 * Create a new FinanceDisbursement object. <br>
	 */
	private void doNew() {
		logger.debug("Entering");
		// remember the old vars
		doStoreInitValues();
		
		// setFocus
		this.disbDate.focus();

		/** !!! DO NOT BREAK THE TIERS !!! */
		// we don't create a new FinanceDisbursement() in the frontEnd.
		// we get it from the backEnd.
		final FinanceDisbursement aFinanceDisbursement = new FinanceDisbursement();
		aFinanceDisbursement.setNewRecord(true);
		setFinanceDisbursement(aFinanceDisbursement);
		doClear(); // clear all components
		doEdit(); // edit mode
		this.btnCtrl.setBtnStatus_New();
		logger.debug("Leaving");
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug("Entering");
		
		if (isNewRecord()){
			if(isNewDisbursement()){
				this.btnCancel.setVisible(false);	
			}
			this.disbDate.setDisabled(isReadOnly("FinanceDisbursementDialog_disbDate"));
			this.btnSearchBenfCIF.setDisabled(isReadOnly("FinanceDisbursementDialog_disbBenificiary"));
 		}else{
			this.btnCancel.setVisible(true);
			this.disbDate.setDisabled(true);
			this.btnSearchBenfCIF.setDisabled(true);
 		}
		this.disbAccountId.setReadonly(isReadOnly("FinanceDisbursementDialog_disbAccountId"));
		this.disbAmount.setDisabled(isReadOnly("FinanceDisbursementDialog_disbAmount"));
		this.disbRetPerc.setDisabled(isReadOnly("FinanceDisbursementDialog_disbRetPerc"));
		this.disbRetAmount.setDisabled(true);
		this.disbRemarks.setReadonly(isReadOnly("FinanceDisbursementDialog_disbRemarks"));
		this.disbExpType.setReadonly(isReadOnly("FinanceDisbursementDialog_disbExpType"));
		this.autoDisb.setDisabled(isReadOnly("FinanceDisbursementDialog_autoDisb"));
		this.disbBenificiary.setReadonly(isReadOnly("FinanceDisbursementDialog_disbBenificiary"));
		this.btnSearchDisbExpType.setDisabled(isReadOnly("FinanceDisbursementDialog_disbExpType"));
		
		
		if (isWorkFlowEnabled()){
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}

			if (this.financeDisbursement.isNewRecord()){
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(false);
			}else{
				this.btnCtrl.setWFBtnStatus_Edit(isFirstTask());
			}
		}else{

			if(newDisbursement){
				if (isNewRecord()){
					this.btnCtrl.setBtnStatus_Edit();
					btnCancel.setVisible(false);
				}else{
					this.btnCtrl.setWFBtnStatus_Edit(newDisbursement);
				}
			}else{
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(true);
			}
		}
		logger.debug("Leaving");
	}

	public boolean isReadOnly(String componentName){
		if (isWorkFlowEnabled() || isNewDisbursement()){
			return getUserWorkspace().isReadOnly(componentName);
		}
		return false;
	}
	
	public void onClick$btnSearchBenfCIF(Event event) {
		logger.debug("Entering" + event.toString());
		Object dataObject = ExtendedSearchListBox.show(this.window_IstisnaDisbursement, "Customer");
		if (dataObject instanceof String) {
			this.disbBenificiary.setText("");
			this.lovDescCustCIF.setValue("");
			this.benfShrtName.setValue("");
		} else {
			Customer details = (Customer) dataObject;
			if (details != null) {
				this.disbBenificiary.setValue(Long.valueOf(details.getCustID()));
				this.lovDescCustCIF.setValue(String.valueOf(details.getCustCIF()));
				this.benfShrtName.setValue(details.getCustShrtName());
			}
		}
		this.disbAccountId.setValue("");
		logger.debug("Leaving" + event.toString());
	}
	
	public void onClick$btnSearchDisbExpType(Event event) {
		logger.debug("Entering" + event.toString());
		Filter[] filter = {new Filter("ExpenseFor", PennantConstants.EXPENSE_FOR_ADVANCEBILLING, Filter.OP_EQUAL)};
		Object dataObject = ExtendedSearchListBox.show(this.window_IstisnaDisbursement, "ExpenseType", filter);
		if (dataObject instanceof String) {
			this.disbExpType.setText("");
			this.lovDescDisbExpType.setValue("");
		} else {
			ExpenseType details = (ExpenseType) dataObject;
			if (details != null) {
				this.disbExpType.setValue(details.getExpenceTypeId());
				this.lovDescDisbExpType.setValue(details.getExpenceTypeName());
			}
		}
		logger.debug("Leaving" + event.toString());
	}
	
	public void onChange$disbRetPerc(Event event){
		logger.debug("Entering" + event.toString());
		if(this.disbAmount.getValue().compareTo(BigDecimal.ZERO) > 0){
			BigDecimal retPerc = BigDecimal.ZERO;
			if(this.disbRetPerc.getValue() != null){
				retPerc = this.disbRetPerc.getValue();
			}
			this.disbRetAmount.setValue((this.disbAmount.getValue().multiply(retPerc)).divide(new BigDecimal(100), 0, RoundingMode.HALF_DOWN));
		}else{
			this.disbRetAmount.setValue(PennantAppUtil.formateAmount(BigDecimal.ZERO,formatter));
		}
		logger.debug("Leaving" + event.toString());
	}
	
	public void onChange$disbAmount(Event event){
		logger.debug("Entering" + event.toString());
		if(this.disbAmount.getValue().compareTo(BigDecimal.ZERO) > 0){
			BigDecimal retPerc = BigDecimal.ZERO;
			if(this.disbRetPerc.getValue() != null){
				retPerc = this.disbRetPerc.getValue();
			}
			this.disbRetAmount.setValue((this.disbAmount.getValue().multiply(retPerc)).divide(new BigDecimal(100), 0, RoundingMode.HALF_DOWN));
		}else{
			this.disbRetAmount.setValue(PennantAppUtil.formateAmount(BigDecimal.ZERO,formatter));
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Method for Fetching Account Balance
	 * @param acId
	 * @return
	 */
	private String getAcBalance(String acId){
		if (!StringUtils.trimToEmpty(acId).equals("")) {
			return PennantAppUtil.amountFormate(getAccountInterfaceService().getAccountAvailableBal(acId), formatter);
		}else{
			return "";
		}
	}

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly() {
		logger.debug("Entering");
		
		this.disbDate.setDisabled(true);
		this.disbAccountId.setReadonly(true);
		this.disbAmount.setDisabled(true);
		this.disbExpType .setReadonly(true);
		this.autoDisb.setDisabled(true);
		this.disbRetPerc.setDisabled(true);
		this.disbRetAmount.setDisabled(true);
		this.disbRemarks.setDisabled(true);
		this.btnSearchDisbExpType.setDisabled(true);

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
		this.disbDate.setText("");
		//this.disbAccountId.setText("");
		this.disbAmount.setValue("");
		this.disbExpType.setText("");
		this.disbRetPerc.setText("");
		this.disbRetAmount.setText("");
		this.disbRemarks.setText("");
		logger.debug("Leaving");		
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 */
	public void doSave() throws InterruptedException, IllegalAccessException, InvocationTargetException {
		logger.debug("Entering");
		Cloner cloner = new Cloner();
		final FinanceDisbursement aFinanceDisbursement = cloner.deepClone(getFinanceDisbursement());
		boolean isNew = false;
		Cloner acloner = new Cloner();
		final ContractorAssetDetail aContractorAssetDetail = acloner.deepClone(contractorAssetDetail);
		
		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		// force validation, if on, than execute by component.getValue()
		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		doSetValidation();
		// fill the FinanceDisbursement object with the components data
		doWriteComponentsToBean(aFinanceDisbursement,aContractorAssetDetail);

		// Write the additional validations as per below example
		// get the selected branch object from the listBox
		// Do data level validations here

		isNew = aFinanceDisbursement.isNew();
		String tranType="";

		if(isWorkFlowEnabled()){
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.trimToEmpty(aFinanceDisbursement.getRecordType()).equals("")){
				aFinanceDisbursement.setVersion(aFinanceDisbursement.getVersion()+1);
				if(isNew){
					aFinanceDisbursement.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else{
					aFinanceDisbursement.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aFinanceDisbursement.setNewRecord(true);
				}
			}
		}else{

			if(isNewDisbursement()){
				if(isNewRecord()){
					aFinanceDisbursement.setVersion(1);
					aFinanceDisbursement.setRecordType(PennantConstants.RCD_ADD);
				}else{
					tranType =PennantConstants.TRAN_UPD;
				}

				if(StringUtils.trimToEmpty(aFinanceDisbursement.getRecordType()).equals("")){
					aFinanceDisbursement.setVersion(aFinanceDisbursement.getVersion()+1);
					aFinanceDisbursement.setRecordType(PennantConstants.RCD_UPD);
				}

				if(aFinanceDisbursement.getRecordType().equals(PennantConstants.RCD_ADD) && isNewRecord()){
					tranType =PennantConstants.TRAN_ADD;
				} else if(aFinanceDisbursement.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){
					tranType =PennantConstants.TRAN_UPD;
				}

			}else{
				aFinanceDisbursement.setVersion(aFinanceDisbursement.getVersion()+1);
				if(isNew){
					tranType =PennantConstants.TRAN_ADD;
				}else{
					tranType =PennantConstants.TRAN_UPD;
				}
			}
		}

		// save it to database
		try {
			if(isNewDisbursement()){
				AuditHeader auditHeader =  newBillingProcess(aFinanceDisbursement,tranType);
				auditHeader = ErrorControl.showErrorDetails(this.window_IstisnaDisbursement, auditHeader);
				int retValue = auditHeader.getProcessStatus();
				if (retValue==PennantConstants.porcessCONTINUE || retValue==PennantConstants.porcessOVERIDE){
					getDisbursementDetailDialogCtrl().doFillDisbursementDetails(this.financeDisbursements);
					fillContractorList(aFinanceDisbursement,aContractorAssetDetail);

					//true;
					// send the data back to customer
					closeWindow();
				}
			}
		} catch (final DataAccessException e) {
			logger.error(e);
			showMessage(e);
		}
		logger.debug("Leaving");
	}


	private AuditHeader newBillingProcess(FinanceDisbursement aFinanceDisbursement,String tranType){
		boolean recordAdded=false;

		AuditHeader auditHeader= getAuditHeader(aFinanceDisbursement, tranType);
		financeDisbursements = new ArrayList<FinanceDisbursement>();

		String[] valueParm = new String[2];
		String[] errParm = new String[2];

		valueParm[0] = aFinanceDisbursement.getFinReference();
		valueParm[1] = DateUtility.formateDate(aFinanceDisbursement.getDisbDate(), PennantConstants.dateFormate);

		errParm[0] = PennantJavaUtil.getLabel("label_FinReference") + ":"+ StringUtils.trimToEmpty(valueParm[0]);
		errParm[1] = PennantJavaUtil.getLabel("label_ProgClaimDate") + ":"+valueParm[1];

		if(getDisbursementDetailDialogCtrl().getDisbursementDetails() != null && getDisbursementDetailDialogCtrl().getDisbursementDetails().size()>0){
			for (int i = 0; i < getDisbursementDetailDialogCtrl().getDisbursementDetails().size(); i++) {
				FinanceDisbursement financeDisbursement = getDisbursementDetailDialogCtrl().getDisbursementDetails().get(i);

				if(DateUtility.compare(financeDisbursement.getDisbDate(),aFinanceDisbursement.getDisbDate()) == 0 &&
						financeDisbursement.getDisbBeneficiary() == aFinanceDisbursement.getDisbBeneficiary()){ // Both Current and Existing list Date same

					if(isNewRecord()){
						auditHeader.setErrorDetails(ErrorUtil.getErrorDetail(
								new ErrorDetails(PennantConstants.KEY_FIELD,"41001",errParm,valueParm), 
								getUserWorkspace().getUserLanguage()));
						return auditHeader;
					}

					if(tranType==PennantConstants.TRAN_DEL){
						if(aFinanceDisbursement.getRecordType().equals(PennantConstants.RECORD_TYPE_UPD)){
							aFinanceDisbursement.setRecordType(PennantConstants.RECORD_TYPE_DEL);
							recordAdded=true;
							financeDisbursements.add(aFinanceDisbursement);
						}else if(aFinanceDisbursement.getRecordType().equals(PennantConstants.RCD_ADD)){
							recordAdded=true;
						}else if(aFinanceDisbursement.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){
							aFinanceDisbursement.setRecordType(PennantConstants.RECORD_TYPE_CAN);
							recordAdded=true;
							financeDisbursements.add(aFinanceDisbursement);
						}else if(aFinanceDisbursement.getRecordType().equals(PennantConstants.RECORD_TYPE_CAN)){
							recordAdded=true;
							//No Such Case
						}
					}else{
						if(tranType!=PennantConstants.TRAN_UPD){
							financeDisbursements.add(financeDisbursement);
						}
					}
				}else{
					financeDisbursements.add(financeDisbursement);
				}
			}
		}
		if(!recordAdded){
			financeDisbursements.add(aFinanceDisbursement);
		}
		return auditHeader;
	} 
	
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++ WorkFlow Components +++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	/**
	 * Get Audit Header Details
	 * 
	 * @param aAcademic
	 * @param tranType
	 * @return AuditHeader
	 */
	private AuditHeader getAuditHeader(FinanceDisbursement aFinanceDisbursement, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1,
				aFinanceDisbursement.getBefImage(), aFinanceDisbursement);

		return new AuditHeader(getReference(),String.valueOf(aFinanceDisbursement.getFinReference()+"-"+aFinanceDisbursement.getDisbDate()), null,
				null, auditDetail, aFinanceDisbursement.getUserDetails(), getOverideMap());
	}

	/**
	 * Display Message in Error Box
	 * 
	 * @param e
	 *            (Exception)
	 */
	private void showMessage(Exception e) {
		logger.debug("Entering");
		AuditHeader auditHeader = new AuditHeader();
		try {
			auditHeader.setErrorDetails(new ErrorDetails(
					PennantConstants.ERR_UNDEF, e.getMessage(), null));
			ErrorControl.showErrorControl(this.window_IstisnaDisbursement, auditHeader);
		} catch (Exception exp) {
			logger.error(exp);
		}
		logger.debug("Leaving");
	}

	/**
	 * Get the window for entering Notes
	 * 
	 * @param event
	 *            (Event)
	 * 
	 * @throws Exception
	 */
	public void onClick$btnNotes(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		final HashMap<String, Serializable> map = new HashMap<String, Serializable>();
		map.put("notes", getNotes());
		map.put("control", this);

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/notes/notes.zul", null, map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / " + e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving" + event.toString());
	}

	// Check notes Entered or not
	public void setNotes_entered(String notes) {
		logger.debug("Entering");
		if (!isNotes_Entered()){
			if (org.apache.commons.lang.StringUtils.trimToEmpty(notes).equalsIgnoreCase("Y")){
				setNotes_Entered(true);
			}else{
				setNotes_Entered(false);
			}	
		}
		logger.debug("Leaving");
	}	

	// Get the notes entered for rejected reason
	private Notes getNotes() {
		logger.debug("Entering");
		Notes notes = new Notes();
		notes.setModuleName("FinanceDisbursement");
		notes.setReference(getReference());
		notes.setVersion(getFinanceDisbursement().getVersion());
		logger.debug("Leaving");
		return notes;
	}
	/** 
	 * Get the Reference value
	 */
	private String getReference(){
		return getFinanceDisbursement().getFinReference()+PennantConstants.KEY_SEPERATOR +
					getFinanceDisbursement().getDisbDate();
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

	public FinanceDisbursement getFinanceDisbursement() {
		return this.financeDisbursement;
	}
	public void setFinanceDisbursement(FinanceDisbursement customerRating) {
		this.financeDisbursement = customerRating;
	}

	public boolean isNotes_Entered() {
		return notes_Entered;
	}
	public void setNotes_Entered(boolean notes_Entered) {
		this.notes_Entered = notes_Entered;
	}

	public boolean isNewRecord() {
		return newRecord;
	}
	public void setNewRecord(boolean newRecord) {
		this.newRecord = newRecord;
	}

	public boolean isNewDisbursement() {
		return newDisbursement;
	}
	public void setNewDisbursement(boolean newDisbursement) {
		this.newDisbursement = newDisbursement;
	}

	public DisbursementDetailDialogCtrl getDisbursementDetailDialogCtrl() {
		return disbursementDetailDialogCtrl;
	}
	public void setDisbursementDetailDialogCtrl(
			DisbursementDetailDialogCtrl disbursementDetailDialogCtrl) {
		this.disbursementDetailDialogCtrl = disbursementDetailDialogCtrl;
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

	public FinanceDetail getFinanceDetail() {
		return financeDetail;
	}

	public void setFinanceDetail(FinanceDetail financeDetail) {
		this.financeDetail = financeDetail;
	}

}
