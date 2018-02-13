package com.pennant.webui.finance.billing;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.SuspendNotAllowedException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Row;
import org.zkoss.zul.Space;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.AccountSelectionBox;
import com.pennant.CurrencyBox;
import com.pennant.ExtendedCombobox;
import com.pennant.FrequencyBox;
import com.pennant.Interface.service.AccountInterfaceService;
import com.pennant.app.constants.AccountConstants;
import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceDisbursement;
import com.pennant.backend.model.finance.contractor.ContractorAssetDetail;
import com.pennant.backend.service.accounts.AccountsService;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.search.Filter;
import com.pennant.util.ErrorControl;
import com.pennant.util.PennantAppUtil;
import com.pennant.util.Constraint.PTDateValidator;
import com.pennant.util.Constraint.PTDecimalValidator;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.finance.financemain.DisbursementDetailDialogCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pff.core.util.DateUtil.DateFormat;
import com.rits.cloning.Cloner;

public class IstisnaDisbursementDetailDialogCtrl extends GFCBaseCtrl<FinanceDisbursement> {
	private static final long serialVersionUID = -6959194080451993569L;
	private static final Logger logger = Logger.getLogger(IstisnaDisbursementDetailDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window 		window_IstisnaDisbursement;			// autowired
	
	// common fields 
	protected Datebox 		 disbDate; 				         	// autowired
	protected Textbox        contractorName;					// autowired
	protected CurrencyBox    disbAmount;						// autowired
	protected ExtendedCombobox  disbExpType;					// autowired
	
	protected Decimalbox     disbRetPerc;						// autowired
	protected Decimalbox     disbRetAmount;						// autowired
	protected Textbox        disbRemarks;						// autowired
	protected Checkbox       autoDisb;							// autowired
	protected FrequencyBox   consultFeeFrq;						// autowired
	protected Datebox 		 consultFeeStartDate; 				// autowired
	protected Datebox 		 consultFeeEndDate;		         	// autowired
	protected Space 		 space_FeeStartDate;
	protected Space			 space_FeeEndDate;	
	
	protected AccountSelectionBox        disbAccountId;						// autowired


	protected Textbox		 moduleName;
	protected Row			 row_DisbRet;	 

	// not auto wired vars
	private FinanceDisbursement financeDisbursement; 	// overhanded per param
	private FinanceDetail financeDetail; 	// overhanded per param

	private transient BigDecimal 	oldVar_disbAmount;
	private transient boolean validationOn;
	
	private boolean newRecord=false;
	private boolean newDisbursement=false;
	
	private List<FinanceDisbursement> financeDisbursements;
	private DisbursementDetailDialogCtrl  disbursementDetailDialogCtrl;
	protected JdbcSearchObject<FinanceDisbursement> newSearchObject ;
	private  int formatter = 0;
	//private String currency = "";
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

	@Override
	protected void doSetProperties() {
		super.pageRightName = "FinanceDisbursementDialog";
	}

	// Component Events

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
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_IstisnaDisbursement);

		// READ OVERHANDED params !
		if (arguments.containsKey("financeDisbursement")) {
			this.financeDisbursement = (FinanceDisbursement) arguments.get("financeDisbursement");
			Cloner cloner = new Cloner();
			FinanceDisbursement befImage = cloner.deepClone(financeDisbursement);
			this.financeDisbursement.setBefImage(befImage);
			setFinanceDisbursement(this.financeDisbursement);
		} else {
			setFinanceDisbursement(null);
		}
		
		if (arguments.containsKey("currency")) {
			//this.currency = (String) arguments.get("currency");
		} 
		
		if (arguments.containsKey("isEnq")) {
			this.isEnq = (Boolean) arguments.get("isEnq");
		} 
		
		if (arguments.containsKey("startDate")) {
			this.startDate = (Date) arguments.get("startDate");
		} 
		if (arguments.containsKey("grcEndDate")) {
			this.grcEndDate = (Date) arguments.get("grcEndDate");
		} 
		
		if (arguments.containsKey("FinanceDetail")) {
			this.financeDetail = (FinanceDetail) arguments.get("FinanceDetail");
		} 
		
		if(getFinanceDisbursement().isNewRecord()){
			setNewRecord(true);
		}

		if(arguments.containsKey("disbursementDetailDialogCtrl")){

			setDisbursementDetailDialogCtrl((DisbursementDetailDialogCtrl) arguments.get("disbursementDetailDialogCtrl"));
			setNewDisbursement(true);

			if(arguments.containsKey("newRecord")){
				setNewRecord(true);
			}else{
				setNewRecord(false);
			}
			
			this.financeDisbursement.setWorkflowId(0);
			if(arguments.containsKey("roleCode")){
				setRole((String) arguments.get("roleCode"));
				getUserWorkspace().allocateRoleAuthorities(getRole(), "FinanceDisbursementDialog");
			}
			
			formatter=CurrencyUtil.getFormat(this.disbursementDetailDialogCtrl.getFinanceDetail().getFinScheduleData().getFinanceMain().getFinCcy());
		}
		
		if(arguments.containsKey("formatter")){
			this.formatter = (Integer) arguments.get("formatter");
		}
		
		if(arguments.containsKey("ContractorAssetDetail")){
			this.contractorAssetDetail = (ContractorAssetDetail) arguments.get("ContractorAssetDetail");
		}
		if(arguments.containsKey("ContractorAssetDetails")){
			this.contractorAssetDetails = (List<ContractorAssetDetail>) arguments.get("ContractorAssetDetails");
		}
		doLoadWorkFlow(this.financeDisbursement.isWorkflow(),this.financeDisbursement.getWorkflowId(),
				this.financeDisbursement.getNextTaskId());

		if (isWorkFlowEnabled()){
			this.userAction	= setListRecordStatus(this.userAction);
			getUserWorkspace().allocateRoleAuthorities(getRole(), "FinanceDisbursementDialog");
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

		this.disbDate.setFormat(DateFormat.SHORT_DATE.getPattern());
		this.disbAmount.setMandatory(true);
		this.disbAmount.setFormat(PennantApplicationUtil.getAmountFormate(formatter));
		this.disbAmount.setScale(formatter);
		this.disbRetPerc.setFormat(PennantConstants.percentageFormate2);
		this.disbRetPerc.setScale(2);
		this.disbRetPerc.setMaxlength(6);
		this.disbRetAmount.setFormat(PennantApplicationUtil.getAmountFormate(formatter));
		this.disbRetAmount.setScale(formatter);
		this.disbRetAmount.setMaxlength(18);
		this.disbRemarks.setMaxlength(100);
		
		if("E".equals(getFinanceDisbursement().getDisbType())){
			this.disbExpType.setMandatoryStyle(true);
		}else{
			this.disbExpType.setMandatoryStyle(false);
		}
		this.disbExpType.setInputAllowed(false);
		this.disbExpType.setTextBoxWidth(90);
		this.disbExpType.setModuleName("ExpenseType");
		this.disbExpType.setValueColumn("ExpenceTypeId");
		this.disbExpType.setDescColumn("ExpenceTypeName");
		this.disbExpType.setValidateColumns(new String[] { "ExpenceTypeId" });
		Filter[] filter =  new Filter[1];
		filter[0] = new Filter("ExpenseFor", FinanceConstants.EXPENSE_FOR_ADVANCEBILLING, Filter.OP_EQUAL);
		this.disbExpType.setFilters(filter);
		
		this.consultFeeStartDate.setFormat(DateFormat.SHORT_DATE.getPattern());
		this.consultFeeEndDate.setFormat(DateFormat.SHORT_DATE.getPattern());
		
		String finType = StringUtils.trimToEmpty(getFinanceDetail().getFinScheduleData().getFinanceMain().getFinType());
		String finCCY = StringUtils.trimToEmpty(getFinanceDetail().getFinScheduleData().getFinanceMain().getFinCcy());
		int format =CurrencyUtil.getFormat(getFinanceDetail().getFinScheduleData().getFinanceMain().getFinCcy());
		
		if("E".equals(getFinanceDisbursement().getDisbType())){
			this.disbAccountId.setAccountDetails(finType, AccountConstants.FinanceAccount_ISEXPACCT, finCCY);
			this.disbAccountId.setFormatter(format);
	        this.disbAccountId.setCustCIF("");
		} else if("A".equals(getFinanceDisbursement().getDisbType())) {
			this.disbAccountId.setAccountDetails(finType, AccountConstants.FinanceAccount_ISCONTADV, finCCY);
			this.disbAccountId.setFormatter(format);
	        this.disbAccountId.setCustCIF(this.contractorAssetDetail.getLovDescCustCIF());
		} else if("C".equals(getFinanceDisbursement().getDisbType())) {
			this.disbAccountId.setAccountDetails(finType, AccountConstants.FinanceAccount_ISCNSLTACCT , finCCY);
			this.disbAccountId.setFormatter(format);
	        this.disbAccountId.setCustCIF(this.contractorAssetDetail.getLovDescCustCIF());
		} else if("B".equals(getFinanceDisbursement().getDisbType())) {
			this.disbAccountId.setAccountDetails(finType, AccountConstants.FinanceAccount_ISBILLACCT , finCCY);
			this.disbAccountId.setFormatter(format);
	        this.disbAccountId.setCustCIF(this.contractorAssetDetail.getLovDescCustCIF());
		}
		if("E".equals(getFinanceDisbursement().getDisbType())){
			this.disbAccountId.setMandatoryStyle(false);
		}else{
			this.disbAccountId.setMandatoryStyle(true);
		}
		 
		if (isWorkFlowEnabled()){
			this.groupboxWf.setVisible(true);
		}else{
			this.groupboxWf.setVisible(false);
		}
		
		logger.debug("Leaving");
	}

	public  void onCheck$autoDisb(Event event){
		if("E".equals(getFinanceDisbursement().getDisbType())){
			if(this.autoDisb.isChecked()){
				this.disbAccountId.setMandatoryStyle(true);
			}else{
				this.disbAccountId.setMandatoryStyle(false);
			}
		}
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
		getUserWorkspace().allocateAuthorities("FinanceDisbursementDialog", getRole());
		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_FinanceDisbursementDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_FinanceDisbursementDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_FinanceDisbursementDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_FinanceDisbursementDialog_btnSave"));
		this.btnCancel.setVisible(false);

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
		MessageUtil.showHelpWindow(event, window_IstisnaDisbursement);
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
	 * The Click event is raised when the Close Button control is clicked.
	 * 
	 * @param event
	 *            An event sent to the event handler of a component.
	 */
	public void onClick$btnClose(Event event) {
		doClose(this.btnSave.isVisible());
	}

	/**
	 * Cancel the actual operation. <br>
	 * <br>
	 * Resets to the original status.<br>
	 * 
	 */
	private void doCancel() {
		logger.debug("Entering");
		doWriteBeanToComponents(this.financeDisbursement.getBefImage());
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
		
		if(contractorAssetDetail != null){
			this.contractorName.setValue(contractorAssetDetail.getContractorName());
		}
		
		this.disbDate.setValue(aFinanceDisbursement.getDisbDate());
		if("B".equals(aFinanceDisbursement.getDisbType())){
			this.disbAmount.setValue(PennantAppUtil.formateAmount(aFinanceDisbursement.getDisbClaim(), formatter));
		}else{
			this.disbAmount.setValue(PennantAppUtil.formateAmount(aFinanceDisbursement.getDisbAmount(), formatter));
		}
		this.disbAccountId.setValue(PennantApplicationUtil.formatAccountNumber(aFinanceDisbursement.getDisbAccountId()));
		this.disbExpType.setValue(String.valueOf(aFinanceDisbursement.getDisbExpType() == 0 ? "" : 
			aFinanceDisbursement.getDisbExpType()), aFinanceDisbursement.getLovDescDisbExpType());
		this.disbRetAmount.setValue(PennantAppUtil.formateAmount(aFinanceDisbursement.getDisbRetAmount(), formatter));
		this.disbRetPerc.setValue(aFinanceDisbursement.getDisbRetPerc());
		this.disbRemarks.setValue(aFinanceDisbursement.getDisbRemarks());
		this.autoDisb.setChecked(aFinanceDisbursement.isAutoDisb());
		
		if("B".equals(getFinanceDisbursement().getDisbType()) && isNewRecord()) {
			this.disbRetPerc.setValue(contractorAssetDetail.getDftRetentionPerc());
		}

		this.consultFeeFrq.setValue(aFinanceDisbursement.getConsultFeeFrq());
		this.consultFeeStartDate.setValue(aFinanceDisbursement.getConsultFeeStartDate());
		this.consultFeeEndDate.setValue(aFinanceDisbursement.getConsultFeeEndDate());
		
		if(StringUtils.isEmpty(aFinanceDisbursement.getConsultFeeFrq())){
			this.consultFeeStartDate.setDisabled(true);
			this.consultFeeEndDate.setDisabled(true);
		}
		
		this.recordStatus.setValue(aFinanceDisbursement.getRecordStatus());
		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aFinanceDisbursement
	 * @throws InterruptedException 
	 */
	public void doWriteComponentsToBean(FinanceDisbursement aFinanceDisbursement, ContractorAssetDetail aContractorAssetDetail) throws InterruptedException {
		logger.debug("Entering");
		doSetLOVValidation();
		int formatter=CurrencyUtil.getFormat(this.disbursementDetailDialogCtrl.getFinanceDetail().getFinScheduleData().getFinanceMain().getFinCcy());
		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		try {
			if("E".equals(aFinanceDisbursement.getDisbType())){
				aFinanceDisbursement.setContractorId(0);
			}else{
				aFinanceDisbursement.setContractorId(aContractorAssetDetail.getContractorId());
			}
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aFinanceDisbursement.setDisbDate(this.disbDate.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aFinanceDisbursement.setLovDescDisbExpType(this.disbExpType.getDescription());
			aFinanceDisbursement.setDisbExpType(Long.valueOf(StringUtils.isEmpty(this.disbExpType.getValue()) ? "0" : this.disbExpType.getValue()));
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			this.disbAccountId.getValidatedValue();
			aFinanceDisbursement.setDisbAccountId(PennantApplicationUtil.unFormatAccountNumber(this.disbAccountId.getValue()));
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			if("B".equals(aFinanceDisbursement.getDisbType())){
				aFinanceDisbursement.setDisbClaim(PennantAppUtil.unFormateAmount(this.disbAmount.getValidateValue(), formatter));
			}else{
				aFinanceDisbursement.setDisbAmount(PennantAppUtil.unFormateAmount(this.disbAmount.getValidateValue(), formatter));
			}
		}catch (WrongValueException we ) {
			wve.add(we);
		}
	 
		try {
			if("B".equals(aFinanceDisbursement.getDisbType())){
				if(!PennantConstants.RECORD_TYPE_DEL.equals(aContractorAssetDetail.getRecordType()) && 
						!PennantConstants.RECORD_TYPE_CAN.equals(aContractorAssetDetail.getRecordType())) {
					if(isNewRecord()){
						aContractorAssetDetail.setTotClaimAmt(aContractorAssetDetail.getTotClaimAmt().add(aFinanceDisbursement.getDisbClaim()));
					}else{
						aContractorAssetDetail.setTotClaimAmt(aContractorAssetDetail.getTotClaimAmt().add(aFinanceDisbursement.getDisbClaim()).subtract(PennantAppUtil.unFormateAmount(this.oldVar_disbAmount, formatter)));
					}

					if(aContractorAssetDetail.getAssetValue().compareTo(aContractorAssetDetail.getTotClaimAmt()) < 0){
						throw new WrongValueException(this.disbAmount,Labels.getLabel("label_ContractorBilling_TotBillClaim") +
								PennantAppUtil.amountFormate(aContractorAssetDetail.getAssetValue(), formatter));
					}
				}
			}
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		
		try {
			if("A".equals(aFinanceDisbursement.getDisbType())){
				if(!PennantConstants.RECORD_TYPE_DEL.equals(aContractorAssetDetail.getRecordType()) && 
						!PennantConstants.RECORD_TYPE_CAN.equals(aContractorAssetDetail.getRecordType())) {
					if(isNewRecord()){
						aContractorAssetDetail.setTotAdvanceAmt(aContractorAssetDetail.getTotAdvanceAmt().add(aFinanceDisbursement.getDisbAmount()));
					}else{
						aContractorAssetDetail.setTotAdvanceAmt(aContractorAssetDetail.getTotAdvanceAmt().add(aFinanceDisbursement.getDisbAmount()).subtract(PennantAppUtil.unFormateAmount(this.oldVar_disbAmount, formatter)));
					}
					
					if(aContractorAssetDetail.getAssetValue().compareTo(aContractorAssetDetail.getTotAdvanceAmt()) < 0){
						throw new WrongValueException(this.disbAmount,Labels.getLabel("label_ContractorAdvance_TotAdvAmt") + 
								PennantAppUtil.amountFormate(aContractorAssetDetail.getAssetValue(), formatter));
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
		try {

			aFinanceDisbursement.setConsultFeeFrq("");
			if("C".equals(aFinanceDisbursement.getDisbType())){
				if (this.consultFeeFrq.isValidComboValue()) {
					aFinanceDisbursement.setConsultFeeFrq(this.consultFeeFrq.getValue() == null ? "" : this.consultFeeFrq.getValue());
				}
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aFinanceDisbursement.setConsultFeeStartDate(this.consultFeeStartDate.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aFinanceDisbursement.setConsultFeeEndDate(this.consultFeeEndDate.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		
		Date curBDay = DateUtility.getAppDate();
		aFinanceDisbursement.setDisbReqDate(curBDay);
		
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

			if(isNewDisbursement() || isEnq){
				this.window_IstisnaDisbursement.setHeight("280px");
				this.window_IstisnaDisbursement.setWidth("90%");
				this.groupboxWf.setVisible(false);
				this.window_IstisnaDisbursement.doModal() ;
			}else{
				this.window_IstisnaDisbursement.setWidth("100%");
				this.window_IstisnaDisbursement.setHeight("100%");
				setDialog(DialogType.EMBEDDED);
			}

		} catch (Exception e) {
			MessageUtil.showError(e);
			this.window_IstisnaDisbursement.onClose();
		}
		logger.debug("Leaving");
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug("Entering");
		doClearMessage();
		setValidationOn(true);
		
		String schDate = DateUtility.formatToLongDate(this.disbDate.getValue());
		
		if (!this.disbDate.isDisabled()) {
			this.disbDate.setConstraint(new PTDateValidator(Labels.getLabel("label_Istisna"+this.moduleName.getValue()+"Dialog_disbDate.value"),
					true,startDate , grcEndDate ,true));
		}
		if (!this.disbAmount.isDisabled()) {
			this.disbAmount.setConstraint(new PTDecimalValidator(Labels.getLabel(
					"label_Istisna"+this.moduleName.getValue()+"Dialog_disbAmount.value"), formatter, true, false));
		}
		if (!this.disbAccountId.isReadonly()){
			if(!StringUtils.equals("E", getFinanceDisbursement().getDisbType())||this.autoDisb.isChecked()) {
				this.disbAccountId.setConstraint(new PTStringValidator(Labels.getLabel("label_Istisna"+this.moduleName.getValue()+"Dialog_disbAccountId.value"), null, true));
			}
		}
		if (!this.disbRetPerc.isDisabled() && row_DisbRet.isVisible()) {
			this.disbRetPerc.setConstraint(new PTDecimalValidator(Labels.getLabel(
					"label_Istisna"+this.moduleName.getValue()+"Dialog_disbRetPerc.value"), 2, true, false));
		}
		String consFeeFrq = this.consultFeeFrq.getValue();
		if(!StringUtils.isEmpty(consFeeFrq) && schDate != null){
			
			Date strtDate = this.consultFeeStartDate.getValue();
			if (!this.consultFeeStartDate.isDisabled()) {
				this.consultFeeStartDate.setConstraint(new PTDateValidator(Labels.getLabel("label_Istisna"+this.moduleName.getValue()+"Dialog_consultFeeStartDate.value"),
						true,this.disbDate.getValue() , grcEndDate ,true));
			}
			if (!this.consultFeeEndDate.isDisabled() && strtDate!= null) {
				this.consultFeeEndDate.setConstraint(new PTDateValidator(Labels.getLabel("label_Istisna"+this.moduleName.getValue()+"Dialog_consultFeeEndDate.value"),
						true,strtDate , grcEndDate ,true));
			}
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
 		this.disbAmount.setConstraint("");
		this.disbAccountId.setConstraint("");
 		this.disbRetAmount.setConstraint("");
		this.disbRetPerc.setConstraint("");
		this.disbRemarks.setConstraint("");
		this.consultFeeStartDate.setConstraint("");
		this.consultFeeEndDate.setConstraint("");
		
		logger.debug("Leaving");
	}

	/**
	 * Set Validations for LOV Fields
	 */
	private void doSetLOVValidation() {
		
		boolean isMand = false;
		if("E".equals(getFinanceDisbursement().getDisbType())){
			isMand = true;
		}
		this.disbExpType.setConstraint(new PTStringValidator(Labels.getLabel("label_Istisna"+this.moduleName.getValue()+"Dialog_disbExpType.value"),
				null, isMand,true));
	}

	/**
	 * Remove Validations for LOV Fields
	 */
	private void doRemoveLOVValidation() {
		this.disbExpType.setConstraint("");
	}

	/**
	 * Remove Error Messages for Fields
	 */
	@Override
	protected void doClearMessage() {
		logger.debug("Entering");
		
		this.disbDate.setErrorMessage("");
 		this.disbAmount.setErrorMessage("");
		this.disbRetPerc.setErrorMessage("");
		this.disbRetAmount.setErrorMessage("");
		this.disbRemarks.setErrorMessage("");
		this.disbExpType.setErrorMessage("");
		this.disbAccountId.setErrorMessage("");
		this.consultFeeFrq.setErrorMessage("");
		this.consultFeeStartDate.setErrorMessage("");
		this.consultFeeEndDate.setErrorMessage("");
		
		logger.debug("Leaving");
	}

	// CRUD operations

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
		final String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record") + "\n\n --> " + 
		      Labels.getLabel("label_IstisnaBillingDialog_disbDate.value")+" : "+ DateUtility.formatToLongDate(aFinanceDisbursement.getDisbDate());
		if (MessageUtil.confirm(msg) == MessageUtil.YES) {
			if (StringUtils.isBlank(aFinanceDisbursement.getRecordType())){
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
					
					if("A".equals(aFinanceDisbursement.getDisbType())){
						contractorAssetDetail.setTotAdvanceAmt(contractorAssetDetail.getTotAdvanceAmt().subtract(aFinanceDisbursement.getDisbAmount()));
					}
					
					tranType=PennantConstants.TRAN_DEL;
					AuditHeader auditHeader =  newBillingProcess(aFinanceDisbursement,tranType);
					auditHeader = ErrorControl.showErrorDetails(this.window_IstisnaDisbursement, auditHeader);
					int retValue = auditHeader.getProcessStatus();
					if (retValue==PennantConstants.porcessCONTINUE || retValue==PennantConstants.porcessOVERIDE){
					getDisbursementDetailDialogCtrl().doFillDisbursementDetails(this.financeDisbursements);
					fillContractorList(aFinanceDisbursement,contractorAssetDetail);
						closeDialog();
					}	

				}
			}catch (DataAccessException e){
				logger.error("Exception: ", e);
				showMessage(e);
			}
		}
		logger.debug("Leaving");
	}

	/**
	 * Method for Resetting Contractor Totals Details
	 * @param aFinanceDisbursement
	 * @param bContractorAssetDetail
	 */
	private void fillContractorList(FinanceDisbursement aFinanceDisbursement, ContractorAssetDetail bContractorAssetDetail){

		//Billing case && Advance
		if("B".equals(aFinanceDisbursement.getDisbType()) || "A".equals(aFinanceDisbursement.getDisbType())) {
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

				double assetValue = bContractorAssetDetail.getAssetValue().doubleValue();			
				double amount = 0;

				if(!PennantConstants.RECORD_TYPE_DEL.equals(bContractorAssetDetail.getRecordType()) && 
						!PennantConstants.RECORD_TYPE_CAN.equals(bContractorAssetDetail.getRecordType())) {
					if("B".equals(aFinanceDisbursement.getDisbType())){
						amount = ((bContractorAssetDetail.getTotClaimAmt().doubleValue())
								/assetValue) * 100;
						bContractorAssetDetail.setLovDescClaimPercent(PennantApplicationUtil.unFormateAmount(BigDecimal.valueOf(amount), 2));
						contractorAssetDetails.remove(i);
						contractorAssetDetails.add(i, bContractorAssetDetail);
						getDisbursementDetailDialogCtrl().doFillContractorDetails(contractorAssetDetails);
					}
					if(!PennantConstants.RECORD_TYPE_DEL.equals(bContractorAssetDetail.getRecordType()) && 
							!PennantConstants.RECORD_TYPE_CAN.equals(bContractorAssetDetail.getRecordType())) {
						if("A".equals(aFinanceDisbursement.getDisbType())){
							contractorAssetDetails.remove(i);
							contractorAssetDetails.add(i, bContractorAssetDetail);
							getDisbursementDetailDialogCtrl().doFillContractorDetails(contractorAssetDetails);
						}
					}
				}
			}
		}
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
 		}else{
			this.btnCancel.setVisible(true);
			this.disbDate.setDisabled(true);
 		}
		this.contractorName.setReadonly(true);
		this.disbAccountId.setReadonly(isReadOnly("FinanceDisbursementDialog_disbAccountId"));
		this.disbAmount.setDisabled(isReadOnly("FinanceDisbursementDialog_disbAmount"));
		this.disbRetPerc.setDisabled(isReadOnly("FinanceDisbursementDialog_disbRetPerc"));
		this.disbRetAmount.setDisabled(true);
		this.disbRemarks.setReadonly(isReadOnly("FinanceDisbursementDialog_disbRemarks"));
		this.disbExpType.setReadonly(isReadOnly("FinanceDisbursementDialog_disbExpType"));
		this.autoDisb.setDisabled(isReadOnly("FinanceDisbursementDialog_autoDisb"));
		this.consultFeeFrq.setDisabled(isReadOnly("FinanceDisbursementDialog_consultFeeFrq"));
		this.consultFeeStartDate.setDisabled(isReadOnly("FinanceDisbursementDialog_consultFeeStartDate"));
		this.consultFeeEndDate.setDisabled(isReadOnly("FinanceDisbursementDialog_consultFeeEndDate"));
		
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
	
	public void onChange$disbRetPerc(Event event){
		logger.debug("Entering" + event.toString());
		if(this.disbAmount.getActualValue().compareTo(BigDecimal.ZERO) > 0){
			BigDecimal retPerc = BigDecimal.ZERO;
			if(this.disbRetPerc.getValue() != null){
				retPerc = this.disbRetPerc.getValue();
			}
			this.disbRetAmount.setValue((this.disbAmount.getActualValue().multiply(retPerc)).divide(new BigDecimal(100), 0, RoundingMode.HALF_DOWN));
		}else{
			this.disbRetAmount.setValue(PennantAppUtil.formateAmount(BigDecimal.ZERO,formatter));
		}
		logger.debug("Leaving" + event.toString());
	}
	
	public void onFulfill$disbAmount(Event event){
		logger.debug("Entering" + event.toString());
		if(this.disbAmount.getActualValue().compareTo(BigDecimal.ZERO) > 0 && "B".equals(getFinanceDisbursement().getDisbType())){
			BigDecimal retPerc = BigDecimal.ZERO;
			if(this.disbRetPerc.getValue() != null){
				retPerc = this.disbRetPerc.getValue();
			}
			this.disbRetAmount.setValue((this.disbAmount.getActualValue().multiply(retPerc)).divide(new BigDecimal(100), 0, RoundingMode.HALF_DOWN));
		}else{
			this.disbRetAmount.setValue(PennantAppUtil.formateAmount(BigDecimal.ZERO,formatter));
		}
		logger.debug("Leaving" + event.toString());
	}

	public void onChange$disbDate(Event event){
		logger.debug("Entering" + event.toString());
		
		Date retentionTillDate = null;
		if(contractorAssetDetail != null){
			retentionTillDate = contractorAssetDetail.getRetentionTillDate();
		}
		
		if(this.disbDate.getValue() != null && retentionTillDate != null){
			if(this.disbDate.getValue().compareTo(retentionTillDate) <= 0){
				this.disbRetPerc.setValue(contractorAssetDetail.getDftRetentionPerc());
			}else{
				this.disbRetPerc.setValue(BigDecimal.ZERO);
			}
		}else{
			if(this.disbRetPerc.getValue() == null){
				this.disbRetPerc.setValue(BigDecimal.ZERO);
			}
		}
		
		if(this.disbDate.getValue() == null){
			this.disbRetPerc.setValue(BigDecimal.ZERO);
		}
		
		if(this.disbAmount.getActualValue().compareTo(BigDecimal.ZERO) > 0 && "B".equals(getFinanceDisbursement().getDisbType())){
			BigDecimal retPerc = BigDecimal.ZERO;
			if(this.disbRetPerc.getValue() != null){
				retPerc = this.disbRetPerc.getValue();
			}
			this.disbRetAmount.setValue((this.disbAmount.getActualValue().multiply(retPerc)).divide(new BigDecimal(100), 0, RoundingMode.HALF_DOWN));
		}else{
			this.disbRetAmount.setValue(PennantAppUtil.formateAmount(BigDecimal.ZERO,formatter));
		}
		
		logger.debug("Leaving" + event.toString());
	}
	
	/**
	 * On Selecting GracePeriod Profit Frequency Code
	 * @param event 
	 */
	public void onSelectCode$consultFeeFrq(Event event) {
		logger.debug("Entering" + event.toString());
		consultancyDatesReset();
		logger.debug("Leaving" + event.toString());
	}
	
	/**
	 * On Selecting GracePeriod Profit Frequency Month
	 * @param event
	 */
	public void onSelectMonth$consultFeeFrq(Event event) {
		logger.debug("Entering" + event.toString());
		consultancyDatesReset();
		logger.debug("Leaving" + event.toString());
	}
	
	/**
	 * On Selecting GracePeriod Profit Frequency Day
	 * @param event
	 */
	public void onSelectDay$consultFeeFrq(Event event) {
		logger.debug("Entering" + event.toString());
		consultancyDatesReset();
		logger.debug("Leaving" + event.toString());
	}
	
	private void consultancyDatesReset(){
		if(StringUtils.isNotBlank(this.consultFeeFrq.getValue()) && (this.consultFeeFrq.getValue().length() == 5)){
			this.consultFeeStartDate.setDisabled(isReadOnly("FinanceDisbursementDialog_consultFeeStartDate"));
			this.consultFeeEndDate.setDisabled(isReadOnly("FinanceDisbursementDialog_consultFeeEndDate"));
			this.space_FeeEndDate.setSclass(PennantConstants.mandateSclass);
			this.space_FeeStartDate.setSclass(PennantConstants.mandateSclass);
			this.disbDate.setConstraint("");
			this.disbDate.setErrorMessage("");
			this.consultFeeStartDate.setValue(this.disbDate.getValue());
			this.consultFeeEndDate.setValue(grcEndDate);
		}else{
			this.consultFeeStartDate.setDisabled(true);
			this.consultFeeEndDate.setDisabled(true);
			this.consultFeeStartDate.setText("");
			this.consultFeeEndDate.setText("");
			this.space_FeeEndDate.setSclass("");
			this.space_FeeStartDate.setSclass("");
		}
	}

	/**
	 * Method for Fetching Account Balance
	 * @param acId
	 * @return
	 */
	@SuppressWarnings("unused")
	private String getAcBalance(String acId){
		if (StringUtils.isNotBlank(acId)) {
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
		this.disbAmount.setValue("");
		this.disbExpType.setValue("0");
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
		
		// force validation, if on, than execute by component.getValue()
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
			if (StringUtils.isBlank(aFinanceDisbursement.getRecordType())){
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

				if(StringUtils.isBlank(aFinanceDisbursement.getRecordType())){
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
					closeDialog();
				}
			}
		} catch (final DataAccessException e) {
			logger.error("Exception: ", e);
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
		valueParm[1] = DateUtility.formatToLongDate(aFinanceDisbursement.getDisbDate());

		errParm[0] = PennantJavaUtil.getLabel("label_FinReference") + ":"+ StringUtils.trimToEmpty(valueParm[0]);
		errParm[1] = PennantJavaUtil.getLabel("label_SchDate") + ":"+valueParm[1];

		if(getDisbursementDetailDialogCtrl().getDisbursementDetails() != null && getDisbursementDetailDialogCtrl().getDisbursementDetails().size()>0){
			for (int i = 0; i < getDisbursementDetailDialogCtrl().getDisbursementDetails().size(); i++) {
				FinanceDisbursement financeDisbursement = getDisbursementDetailDialogCtrl().getDisbursementDetails().get(i);

				if(DateUtility.compare(financeDisbursement.getDisbDate(),aFinanceDisbursement.getDisbDate()) == 0 &&
						financeDisbursement.getContractorId() == aFinanceDisbursement.getContractorId() &&
						financeDisbursement.getDisbType().equals(aFinanceDisbursement.getDisbType())){ // Both Current and Existing list Date same

					if(isNewRecord()){
						auditHeader.setErrorDetails(ErrorUtil.getErrorDetail(
								new ErrorDetail(PennantConstants.KEY_FIELD,"41001",errParm,valueParm), 
								getUserWorkspace().getUserLanguage()));
						return auditHeader;
					}

					if(PennantConstants.TRAN_DEL.equals(tranType)){
						if(PennantConstants.RECORD_TYPE_UPD.equals(aFinanceDisbursement.getRecordType())){
							aFinanceDisbursement.setRecordType(PennantConstants.RECORD_TYPE_DEL);
							recordAdded=true;
							financeDisbursements.add(aFinanceDisbursement);
						}else if(PennantConstants.RCD_ADD.equals(aFinanceDisbursement.getRecordType())){
							recordAdded=true;
						}else if(PennantConstants.RECORD_TYPE_NEW.equals(aFinanceDisbursement.getRecordType())){
							aFinanceDisbursement.setRecordType(PennantConstants.RECORD_TYPE_CAN);
							recordAdded=true;
							financeDisbursements.add(aFinanceDisbursement);
						}else if(PennantConstants.RECORD_TYPE_CAN.equals(aFinanceDisbursement.getRecordType())){
							recordAdded=true;
							//No Such Case
						}
					}else{
						if(!PennantConstants.TRAN_UPD.equals(tranType)){
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
	
	// WorkFlow Components

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
			auditHeader.setErrorDetails(new ErrorDetail(
					PennantConstants.ERR_UNDEF, e.getMessage(), null));
			ErrorControl.showErrorControl(this.window_IstisnaDisbursement, auditHeader);
		} catch (Exception exp) {
			logger.error("Exception: ", exp);
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
		doShowNotes(this.financeDisbursement);
	}

	/** 
	 * Get the Reference value
	 */
	@Override
	protected String getReference() {
		return getFinanceDisbursement().getFinReference()+PennantConstants.KEY_SEPERATOR +
					getFinanceDisbursement().getDisbDate();
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

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
