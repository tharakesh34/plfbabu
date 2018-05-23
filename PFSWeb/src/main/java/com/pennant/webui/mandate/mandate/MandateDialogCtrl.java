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
 * FileName    		:  MandateDialogCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  18-10-2016    														*
 *                                                                  						*
 * Modified Date    :  18-10-2016    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 18-10-2016       Pennant	                 0.1                                            * 
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
package com.pennant.webui.mandate.mandate;

import java.io.ByteArrayInputStream;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataAccessException;
import org.zkoss.text.MessageFormats;
import org.zkoss.util.media.AMedia;
import org.zkoss.util.media.Media;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.HtmlBasedComponent;
import org.zkoss.zk.ui.Path;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.UploadEvent;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Iframe;
import org.zkoss.zul.Label;
import org.zkoss.zul.North;
import org.zkoss.zul.Row;
import org.zkoss.zul.Space;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.CurrencyBox;
import com.pennant.ExtendedCombobox;
import com.pennant.FrequencyBox;
import com.pennant.app.constants.ImplementationConstants;
import com.pennant.app.constants.LengthConstants;
import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.NumberToEnglishWords;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.bmtmasters.BankBranch;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.mandate.Mandate;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.service.applicationmaster.BankDetailService;
import com.pennant.backend.service.mandate.MandateService;
import com.pennant.backend.util.MandateConstants;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.component.Uppercasebox;
import com.pennant.util.ErrorControl;
import com.pennant.util.PennantAppUtil;
import com.pennant.util.Constraint.PTDateValidator;
import com.pennant.util.Constraint.PTDecimalValidator;
import com.pennant.util.Constraint.PTMobileNumberValidator;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.util.Constraint.StaticListValidator;
import com.pennant.webui.finance.financemain.FinBasicDetailsCtrl;
import com.pennant.webui.util.ButtonStatusCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.ScreenCTL;
import com.pennanttech.interfacebajaj.MandateRegistrationListCtrl;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.jdbc.search.Filter;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pff.core.util.DateUtil.DateFormat;
import com.pennanttech.pff.document.external.ExternalDocumentManager;

/**
 * ************************************************************<br>
 * This is the controller class for the /WEB-INF/pages/Mandate/mandateDialog.zul file. <br>
 * ************************************************************<br>
 */
public class MandateDialogCtrl extends GFCBaseCtrl<Mandate> {

	private static final long						serialVersionUID		= 1L;
	private static final Logger						logger					= Logger.getLogger(MandateDialogCtrl.class);

	protected Window								window_MandateDialog;
	protected ExtendedCombobox						custID;
	protected ExtendedCombobox						mandateRef;
	protected Combobox								mandateType;
	protected ExtendedCombobox						bankBranchID;
	protected Textbox								bank;
	protected Textbox								city;
	protected Label								    cityName;
	protected Textbox								micr;
	protected Textbox								ifsc;
	protected Textbox								accNumber;
	protected Textbox								accHolderName;
	protected Datebox								inputDate;
	protected Textbox								jointAccHolderName;
	protected Combobox								accType;
	protected Checkbox								openMandate;
	protected Datebox								startDate;
	protected Datebox								expiryDate;
	protected CurrencyBox							maxLimit;
	protected FrequencyBox							periodicity;
	//protected Textbox								phoneCountryCode;
	//protected Textbox								phoneAreaCode;
	protected Textbox								phoneNumber;
	protected Combobox								status;
	protected Textbox								approvalID;
	protected Groupbox								gb_basicDetails;
	//protected Groupbox								gb_enquiry;
	protected Checkbox								useExisting;
	protected Checkbox								active;
	protected Textbox								reason;
	protected Textbox								umrNumber;
	protected Space									space_Reason;
	protected Space									space_Expirydate;

	protected Row									mandateRow;

	//Added BarCode and Reg Status
	protected Label									label_BarCodeNumber;
	protected Uppercasebox							barCodeNumber;
	protected Label									amountInWords;
	protected Label									regStatus;
	protected ExtendedCombobox						finReference;
	protected Checkbox								swapIsActive;
	protected Label									label_RegStatus;
	
	//Adding Entity reelated to HFC
	private ExtendedCombobox						entityCode;

	private boolean									enqModule				= false;
	private boolean									flag					= false;
	
	protected Button 								btnUploadDoc;
	protected Textbox 								documentName;
	protected Iframe 								mandatedoc;
	private byte[] 								   	imagebyte;

	// not auto wired vars
	private Mandate									mandate;
	private transient MandateListCtrl				mandateListCtrl;
	private transient MandateRegistrationListCtrl	mandateRegistrationListCtrl;

	private boolean									notes_Entered			= false;

	// Button controller for the CRUD buttons
	private transient final String					btnCtroller_ClassPrefix	= "button_MandateDialog_";
	protected Button								btnHelp;
	protected Button								btnProcess;
	protected Button								btnView;

	// ServiceDAOs / Domain Classes
	private transient MandateService				mandateService;

	private final List<ValueLabel>					mandateTypeList			= PennantStaticListUtil
																					.getMandateTypeList();
	private final List<ValueLabel>					accTypeList				= PennantStaticListUtil.getAccTypeList();
	private final List<ValueLabel>					statusTypeList			= PennantStaticListUtil.getStatusTypeList(SysParamUtil.getValueAsString(MandateConstants.MANDATE_CUSTOM_STATUS));

	public transient int							ccyFormatter			= 0;
	private boolean									registration			= false;
	private boolean									maintain				= false;
	/* loan related declrations */
	private boolean									fromLoan				= false;
	protected North									north_mandate;
	protected Groupbox								finBasicdetails;
	private FinBasicDetailsCtrl						finBasicDetailsCtrl;
	private Object									financeMainDialogCtrl	= null;
	FinanceDetail									financeDetail;
	private FinanceMain								financemain;
	Tab												parenttab				= null;
//	long											mandateID				= 0;
	protected Row									rowStatus;
	protected Row									rowReason;
	protected int									accNoLength;
	private transient BankDetailService				bankDetailService;
	private ExternalDocumentManager				externalDocumentManager	= null;


	/**
	 * default constructor.<br>
	 */
	public MandateDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "MandateDialog";
	}

	// ************************************************* //
	// *************** Component Events **************** //
	// ************************************************* //

	/**
	 * Before binding the data and calling the dialog window we check, if the zul-file is called with a parameter for a
	 * selected Mandate object in a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public void onCreate$window_MandateDialog(Event event) throws Exception {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_MandateDialog);

		try {
			// READ OVERHANDED params !
			if (arguments.containsKey("enqModule")) {
				enqModule = (Boolean) arguments.get("enqModule");
			}

			if (arguments.containsKey("fromLoan")) {
				fromLoan = (Boolean) arguments.get("fromLoan");
			}

			if (arguments.containsKey("registration")) {
				registration = (Boolean) arguments.get("registration");
			}
			if (arguments.containsKey("maintain")) {
				maintain = (Boolean) arguments.get("maintain");
			}

			// READ OVERHANDED params !
			if (arguments.containsKey("mandate")) {
				this.mandate = (Mandate) arguments.get("mandate");
				Mandate befImage = new Mandate();
				BeanUtils.copyProperties(this.mandate, befImage);
				this.mandate.setBefImage(befImage);
				setMandate(this.mandate);
			} else {
				setMandate(null);
			}

			// ***** Loan Origination ********//
			this.mandateRow.setVisible(fromLoan);
			if (fromLoan) {
				if (arguments.containsKey("financeDetail")) {
					financeDetail = (FinanceDetail) arguments.get("financeDetail");
					financemain = financeDetail.getFinScheduleData().getFinanceMain();
					if (financeDetail.getMandate() != null) {
						this.mandate = financeDetail.getMandate();
						if(!StringUtils.equals(financemain.getFinReference(),mandate.getOrgReference())){
							mandate.setUseExisting(true);
						}

					} else {
						this.mandate = new Mandate();
						mandate.setNewRecord(true);
						mandate.setCustID(financemain.getCustID());
						mandate.setCustCIF(getCIFForCustomer(financeDetail));
						mandate.setMandateType(financemain.getFinRepayMethod());
						
						FinanceType financeType = financeDetail.getFinScheduleData().getFinanceType();
						mandate.setEntityCode(financeType.getLovDescEntityCode());
						mandate.setEntityDesc(financeType.getLovDescEntityDesc());
					}
					this.mandate.setWorkflowId(0);
				}

				if (arguments.containsKey("roleCode")) {
					//getUserWorkspace().allocateRoleAuthorities(arguments.get("roleCode").toString(), this.pageRightName);
					setRole(arguments.get("roleCode").toString()); //FIXME For Rights Allocation
				}

				if (arguments.containsKey("tab")) {
					parenttab = (Tab) arguments.get("tab");
				}

				if (arguments.containsKey("finHeaderList")) {
					appendFinBasicDetails((ArrayList<Object>) arguments.get("finHeaderList"));
				} else {
					appendFinBasicDetails(null);
				}

				if (arguments.containsKey("financeMainDialogCtrl")) {
					setFinanceMainDialogCtrl(arguments.get("financeMainDialogCtrl"));
				}
			}
			// *****************************//

			doLoadWorkFlow(this.mandate.isWorkflow(), this.mandate.getWorkflowId(), this.mandate.getNextTaskId());

			if (isWorkFlowEnabled() && !enqModule) {
				this.userAction = setListRecordStatus(this.userAction);
				//getUserWorkspace().allocateRoleAuthorities(getRole(), "MandateDialog");
			} else {
				//getUserWorkspace().allocateAuthorities(super.pageRightName);
			}
			getUserWorkspace().allocateAuthorities( super.pageRightName, getRole()); //FIXME For Rights Allocation

			/* set components visible dependent of the users rights */
			doCheckRights();

			// READ OVERHANDED params !
			// we get the flagListWindow controller. So we have access
			// to it and can synchronize the shown data when we do insert, edit
			// or
			// delete flag here.
			if (arguments.containsKey("mandateListCtrl")) {
				setMandateListCtrl((MandateListCtrl) arguments.get("mandateListCtrl"));
			} else if (arguments.containsKey("mandateRegistrationListCtrl")) {
				setMandateRegistrationListCtrl((MandateRegistrationListCtrl) arguments
						.get("mandateRegistrationListCtrl"));
			} else {
				setMandateRegistrationListCtrl(null);
			}
			if (getMandate() != null) {
				ccyFormatter = CurrencyUtil.getFormat(getMandate().getMandateCcy());
			}

			// set Field Properties
			doSetFieldProperties();
			doShowDialog(getMandate());
		} catch (Exception e) {
			closeDialog();
			MessageUtil.showError(e);
		}

		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");
		// Empty sent any required attributes
		this.accHolderName.setMaxlength(50);
		this.jointAccHolderName.setMaxlength(50);
		this.startDate.setFormat(DateFormat.SHORT_DATE.getPattern());
		this.expiryDate.setFormat(DateFormat.SHORT_DATE.getPattern());
		this.inputDate.setFormat(DateFormat.SHORT_DATE.getPattern());

		this.maxLimit.setFormat(PennantApplicationUtil.getAmountFormate(ccyFormatter));
		this.maxLimit.setScale(ccyFormatter);
		this.maxLimit.setTextBoxWidth(171);
		this.maxLimit.setMandatory(true);

		this.periodicity.setMandatoryStyle(true);
		this.phoneNumber.setMaxlength(10);
		this.phoneNumber.setWidth("180px");
		this.approvalID.setMaxlength(50);

		this.custID.setModuleName("Customer");
		this.custID.setMandatoryStyle(true);
		this.custID.setValueColumn("CustCIF");
		this.custID.setDescColumn("CustShrtName");
		this.custID.setDisplayStyle(2);
		this.custID.setValidateColumns(new String[] { "CustCIF" });

		this.bankBranchID.setModuleName("BankBranch");
		this.bankBranchID.setMandatoryStyle(true);
		this.bankBranchID.setValueColumn("BranchCode");
		this.bankBranchID.setDescColumn("BranchDesc");
		this.bankBranchID.setDisplayStyle(2);
		this.bankBranchID.setValidateColumns(new String[] { "BranchCode" });

		this.mandateRef.setModuleName("Mandate");
		this.mandateRef.setMandatoryStyle(true);
		this.mandateRef.setValueColumn("MandateID");
		this.mandateRef.setDescColumn("MandateRef");
		this.mandateRef.setDisplayStyle(2);
		this.mandateRef.setInputAllowed(false);
		this.mandateRef.setValidateColumns(new String[] { "MandateID" });
		addMandateFiletrs(null);
		this.active.setChecked(true);
		this.reason.setMaxlength(60);
		this.umrNumber.setReadonly(true);
		this.documentName.setMaxlength(150);

		//Finance Main
		this.finReference.setMaxlength(20);
		this.finReference.setTextBoxWidth(120);
		this.finReference.setModuleName("FinanceManagement");
		this.finReference.setValueColumn("FinReference");
		this.finReference.setValidateColumns(new String[] { "FinReference" });
		

		if (StringUtils.isNotBlank(this.mandate.getBankCode())) {
			accNoLength = getBankDetailService().getAccNoLengthByCode(this.mandate.getBankCode());
		}
		
		if (accNoLength==0) {
			accNoLength = LengthConstants.LEN_ACCOUNT;
		}
		this.accNumber.setMaxlength(accNoLength);

		this.barCodeNumber.setMaxlength(10);
		
		//Adding Entity 
		this.entityCode.setMaxlength(8);
		this.entityCode.setDisplayStyle(2);
		this.entityCode.setMandatoryStyle(true);
		this.entityCode.setModuleName("Entity");
		this.entityCode.setValueColumn("EntityCode");
		this.entityCode.setDescColumn("EntityDesc");
		this.entityCode.setValidateColumns(new String[] { "EntityCode" });
		Filter[] filter = new Filter[1] ;
		filter[0]= new Filter("Active", 1, Filter.OP_EQUAL);
		this.entityCode.setFilters(filter);
		
		setStatusDetails();
		logger.debug("Leaving");
	}

	/**
	 * User rights check. <br>
	 * Only components are set visible=true if the logged-in <br>
	 * user have the right for it. <br>
	 * 
	 * The rights are get from the spring framework users grantedAuthority(). A right is only a string. <br>
	 */
	private void doCheckRights() {
		logger.debug("Entering");

		if (!enqModule) {
			this.btnNew.setVisible(getUserWorkspace().isAllowed("button_MandateDialog_btnNew"));
			this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_MandateDialog_btnEdit"));
			this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_MandateDialog_btnDelete"));
			this.btnSave.setVisible(getUserWorkspace().isAllowed("button_MandateDialog_btnSave"));
		}

		/* create the Button Controller. Disable not used buttons during working */
		this.btnCtrl = new ButtonStatusCtrl(getUserWorkspace(), this.btnCtroller_ClassPrefix, true, this.btnNew,
				this.btnEdit, this.btnDelete, this.btnSave, this.btnCancel, this.btnClose, this.btnNotes);

		logger.debug("Leaving");
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
	 * when the "save" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnSave(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		doSave();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "cancel" button is clicked. <br>
	 * 
	 * @param event
	 * @throws Exception 
	 * @throws WrongValueException 
	 */
	public void onClick$btnCancel(Event event) throws WrongValueException, Exception {
		logger.debug("Entering" + event.toString());
		doCancel();
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
		MessageUtil.showHelpWindow(event, window_MandateDialog);
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "edit" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnProcess(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		doSave();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "View" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnReason(Event event) {
		logger.debug("Entering");
		HashMap<String, Object> arg = new HashMap<String, Object>();
		arg.put("mandateId", mandate.getMandateID());

		Executions.createComponents("/WEB-INF/pages/Mandate/MandateStatusList.zul", null, arg);
		logger.debug("Leaving");
	}

	/**
	 * when the "close" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnClose(Event event) throws InterruptedException {
		doClose(this.btnSave.isVisible());
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
		try {

			ScreenCTL.displayNotes(
					getNotes("Mandate", String.valueOf(getMandate().getMandateID()), getMandate().getVersion()), this);

		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving" + event.toString());

	}

	public void onCheck$useExisting(Event event) throws WrongValueException, Exception {
		logger.debug("Entering" + event.toString());
		doClearMessage();
		useExisting();

		if (!this.useExisting.isChecked() && financeDetail != null) {
			Mandate man = financeDetail.getMandate();
			if (man != null && !man.isUseExisting()) {
				this.label_RegStatus.setVisible(true);
				doWriteData(man);
			} else{
				this.mandateRef.setAttribute("mandateID", Long.MIN_VALUE);
				this.periodicity.setValue(MandateConstants.MANDATE_DEFAULT_FRQ);
				this.startDate.setValue(DateUtility.getAppDate());
			}
		}
		logger.debug("Leaving" + event.toString());
	}

	public void onCheck$openMandate(Event event) {
		logger.debug("Entering" + event.toString());
		checkOpenMandate();
		logger.debug("Leaving" + event.toString());
	}

	private void checkOpenMandate() {
		if (this.openMandate.isChecked()) {
			readOnlyComponent(true, this.expiryDate);
			this.expiryDate.setValue(null);
			this.space_Expirydate.setSclass("");
		} else {
			if (this.useExisting.isChecked()) {
				readOnlyComponent(true, this.expiryDate);
			} else {
				readOnlyComponent(isReadOnly("MandateDialog_ExpiryDate"), this.expiryDate);
				this.space_Expirydate.setSclass("mandatory");
			}
		}
	}

	private void useExisting() {
		boolean checked = this.useExisting.isChecked();
		if (checked) {
			readOnlyComponent(isReadOnly("MandateDialog_MandateRef"), this.mandateRef);
			readOnlyComponent(true, this.bankBranchID);
			readOnlyComponent(true, this.accNumber);
			readOnlyComponent(true, this.accHolderName);
			readOnlyComponent(true, this.jointAccHolderName);
			readOnlyComponent(true, this.accType);
			readOnlyComponent(true, this.maxLimit);
			readOnlyComponent(true, this.periodicity);
			readOnlyComponent(true, this.phoneNumber);
			readOnlyComponent(true, this.startDate);
			readOnlyComponent(true, this.expiryDate);
			readOnlyComponent(true, this.openMandate);
			readOnlyComponent(true, this.approvalID);
			readOnlyComponent(true, this.btnUploadDoc);
			readOnlyComponent(true, this.barCodeNumber);
			readOnlyComponent(true, swapIsActive);
			readOnlyComponent(true, this.entityCode);
		} else {
			readOnlyComponent(true, this.mandateRef);
			readOnlyComponent(isReadOnly("MandateDialog_BankBranchID"), this.bankBranchID);
			readOnlyComponent(isReadOnly("MandateDialog_AccNumber"), this.accNumber);
			readOnlyComponent(isReadOnly("MandateDialog_AccHolderName"), this.accHolderName);
			readOnlyComponent(isReadOnly("MandateDialog_JointAccHolderName"), this.jointAccHolderName);
			readOnlyComponent(isReadOnly("MandateDialog_AccType"), this.accType);
			readOnlyComponent(isReadOnly("MandateDialog_MaxLimit"), this.maxLimit);
			readOnlyComponent(isReadOnly("MandateDialog_Periodicity"), this.periodicity);
			readOnlyComponent(isReadOnly("MandateDialog_PhoneNumber"), this.phoneNumber);
			readOnlyComponent(isReadOnly("MandateDialog_StartDate"), this.startDate);
			readOnlyComponent(isReadOnly("MandateDialog_ExpiryDate"), this.expiryDate);
			readOnlyComponent(isReadOnly("MandateDialog_OpenMandate"), this.openMandate);
			readOnlyComponent(isReadOnly("MandateDialog_BtnUploadDoc"), this.btnUploadDoc);
			readOnlyComponent(true, this.approvalID);
			this.maxLimit.setMandatory(true);
			readOnlyComponent(isReadOnly("MandateDialog_BarCodeNumber"), this.barCodeNumber);
			readOnlyComponent(isReadOnly("MandateDialog_SwapIsActive"), swapIsActive);
		}
		clearMandatedata();
	}

	private void clearMandatedata() {
		this.mandateRef.setValue("", "");
		this.bankBranchID.setValue("", "");
		this.accNumber.setValue("");
		this.accHolderName.setValue("");
		this.city.setValue("");
		this.bank.setValue("");
		this.micr.setValue("");
		this.ifsc.setValue("");
		this.jointAccHolderName.setValue("");
		this.startDate.setText("");
		this.expiryDate.setText("");
		this.accType.setValue("");
		this.maxLimit.setValue(BigDecimal.ZERO);
		this.periodicity.setValue("");
		this.phoneNumber.setValue("");
		this.umrNumber.setValue("");
		this.openMandate.setChecked(false);
		this.space_Expirydate.setSclass("mandatory");
		//Frequency
		this.approvalID.setValue("");
		this.documentName.setValue("");
		this.mandatedoc.setContent(null);
		this.barCodeNumber.setValue("");
		this.finReference.setValue("");
		this.regStatus.setValue("");
		this.amountInWords.setValue("");
		this.swapIsActive.setChecked(false);
	}

	public void onChange$mandateType(Event event) {
		String str = this.mandateType.getSelectedItem().getValue().toString();
		this.bankBranchID.setValue("");
		this.bankBranchID.setDescription("");
		this.bank.setValue("");
		this.micr.setValue("");
		this.ifsc.setValue("");
		this.city.setValue("");
		this.cityName.setValue("");
		onChangeMandateType(str);

	}

	private void onChangeMandateType(String str) {
		Filter filter[] = new Filter[1];
		switch (str) {
		case MandateConstants.TYPE_ECS:
			filter[0] = new Filter("ECS", 1, Filter.OP_EQUAL);
			this.bankBranchID.setFilters(filter);
			break;
		case MandateConstants.TYPE_DDM:
			filter[0] = new Filter("DDA", 1, Filter.OP_EQUAL);
			this.bankBranchID.setFilters(filter);
			break;
		case MandateConstants.TYPE_NACH:
			filter[0] = new Filter("NACH", 1, Filter.OP_EQUAL);
			this.bankBranchID.setFilters(filter);
			break;

		default:
			break;
		}
	}

	// ****************************************************************+
	// ************************ GUI operations ************************+
	// ****************************************************************+

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the readOnly mode accordingly.
	 * 
	 * @param aMandate
	 * @throws InterruptedException
	 */
	@SuppressWarnings("rawtypes")
	public void doShowDialog(Mandate aMandate) throws InterruptedException {
		logger.debug("Entering");

		// set ReadOnly mode accordingly if the object is new or not.
		if (aMandate.isNew()) {
			this.btnCtrl.setInitNew();
			this.inputDate.setValue(DateUtility.getAppDate());
			doEdit();
			// setFocus
			this.custID.focus();
		} else {
			this.custID.setReadonly(true);
			if (isWorkFlowEnabled()) {
				if (StringUtils.isNotBlank(aMandate.getRecordType())) {
					this.btnNotes.setVisible(true);
				}
				doEdit();
			} else {
				this.btnCtrl.setInitEdit();
				doReadOnly();
				btnCancel.setVisible(false);
				if (fromLoan && !enqModule) {
					doEdit();
				}
			}
		}
		if(StringUtils.equals(ImplementationConstants.CLIENT_BFL,"CORE")){
			this.barCodeNumber.setVisible(true);
			this.label_BarCodeNumber.setVisible(true);
		}else{
			this.barCodeNumber.setVisible(false);
			this.label_BarCodeNumber.setVisible(false);
		}
		try {
			// fill the components with the data
			doWriteBeanToComponents(aMandate);
			doDesignByStatus(aMandate);
			doDesignByMode();
			if (fromLoan) {
				try {
					Class[] paramType = { this.getClass() };
					Object[] stringParameter = { this };
					getFinanceMainDialogCtrl().getClass().getMethod("setMandateDialogCtrl", paramType)
							.invoke(getFinanceMainDialogCtrl(), stringParameter);
				} catch (Exception e) {
					logger.error("Exception: ", e);
				}
				if (parenttab != null) {
					checkTabDisplay(aMandate.getMandateType(), false);
				}

			} else {
				setDialog(DialogType.EMBEDDED);
			}

			// Setting Height for Iframe 
			this.mandatedoc.setHeight((borderLayoutHeight - 50) + "px");
			
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving");
	}

	private void doDesignByStatus(Mandate aMandate) {
		String mandateStatus = StringUtils.trimToEmpty(aMandate.getStatus());

		if (mandateStatus.equals("") || mandateStatus.equals(PennantConstants.List_Select)) {
			this.rowStatus.setVisible(false);
			this.rowReason.setVisible(false);
		} else {
			this.rowStatus.setVisible(true);
			this.rowReason.setVisible(true);
		}

		if (mandateStatus.equals(MandateConstants.STATUS_REJECTED)) {
			readOnlyComponent(true, status);
			readOnlyComponent(true, reason);
		}

		if (mandateStatus.equals(MandateConstants.STATUS_NEW) || mandateStatus.equals(MandateConstants.STATUS_INPROCESS)) {
			readOnlyComponent(true, status);
			readOnlyComponent(true, reason);
			this.reason.setValue("");
			this.rowStatus.setVisible(false);
			this.rowReason.setVisible(false);
		}
		
		if (mandateStatus.equals(MandateConstants.STATUS_APPROVED) || mandateStatus.equals(MandateConstants.STATUS_HOLD)
				|| mandateStatus.equals(MandateConstants.STATUS_RELEASE)) {
			readOnlyComponent(true, this.mandateRef);
			readOnlyComponent(true, this.mandateType);
			readOnlyComponent(true, this.bankBranchID);
			readOnlyComponent(true, this.accNumber);
			readOnlyComponent(true, this.accHolderName);
			readOnlyComponent(true, this.jointAccHolderName);
			readOnlyComponent(true, this.accType);
			readOnlyComponent(true, this.maxLimit);
			readOnlyComponent(true, this.periodicity);
			readOnlyComponent(true, this.phoneNumber);
			readOnlyComponent(true, this.startDate);
			readOnlyComponent(true, this.expiryDate);
			readOnlyComponent(true, this.openMandate);
			readOnlyComponent(true, this.approvalID);
			readOnlyComponent(true, this.barCodeNumber);
			readOnlyComponent(true, this.swapIsActive);
			readOnlyComponent(true, this.entityCode);
		}
		
	}

	private void doDesignByMode() {
		if (enqModule) {
			this.btnCancel.setVisible(false);
			this.btnSave.setVisible(false);
			this.btnDelete.setVisible(false);
			this.btnNotes.setVisible(false);
			return;
		}

		if (registration) {
			this.rowStatus.setVisible(true);
			this.rowReason.setVisible(true);
			readOnlyComponent(false, this.status);
			readOnlyComponent(false, this.reason);
			this.btnCancel.setVisible(false);
			this.btnEdit.setVisible(false);
			this.btnSave.setVisible(false);
			this.btnNotes.setVisible(false);
			this.btnDelete.setVisible(false);
			this.btnProcess.setVisible(true);
		}

		if (fromLoan) {
			this.north_mandate.setVisible(false);
			readOnlyComponent(true, this.custID);
			readOnlyComponent(true, this.mandateType);
			this.rowStatus.setVisible(false);
			this.rowReason.setVisible(false);
			readOnlyComponent(true, this.finReference);
			readOnlyComponent(true, this.entityCode);

		}

		if (StringUtils.isNotEmpty(getMandate().getOrgReference()) && !StringUtils.equals(getMandate().getStatus(),MandateConstants.STATUS_FIN)) {
			readOnlyComponent(true, this.openMandate);
		}
		
		
		if (StringUtils.isEmpty(getMandate().getRecordType()) && StringUtils.isNotEmpty(getMandate().getRecordStatus())) {
			readOnlyComponent(true, this.mandateType);
		}

	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug("Entering");
		if (getMandate().isNewRecord()) {
			this.btnCancel.setVisible(false);
			this.custID.setReadonly(false);
			this.label_RegStatus.setVisible(false);
			//readOnlyComponent(false, finReference);
		} else {
			this.btnCancel.setVisible(true);
			this.custID.setReadonly(true);
			this.label_RegStatus.setVisible(true);
		}
		readOnlyComponent(true, finReference);

		readOnlyComponent(isReadOnly("MandateDialog_MandateRef"), this.useExisting);
		readOnlyComponent(isReadOnly("MandateDialog_MandateRef"), this.mandateRef);
		readOnlyComponent(isReadOnly("MandateDialog_MandateType"), this.mandateType);
		readOnlyComponent(isReadOnly("MandateDialog_BankBranchID"), this.bankBranchID);
		readOnlyComponent(isReadOnly("MandateDialog_AccNumber"), this.accNumber);
		readOnlyComponent(isReadOnly("MandateDialog_AccHolderName"), this.accHolderName);
		readOnlyComponent(isReadOnly("MandateDialog_JointAccHolderName"), this.jointAccHolderName);
		readOnlyComponent(isReadOnly("MandateDialog_AccType"), this.accType);
		readOnlyComponent(isReadOnly("MandateDialog_OpenMandate"), this.openMandate);
		readOnlyComponent(isReadOnly("MandateDialog_StartDate"), this.startDate);
		readOnlyComponent(isReadOnly("MandateDialog_ExpiryDate"), this.expiryDate);
		readOnlyComponent(isReadOnly("MandateDialog_MaxLimit"), this.maxLimit);
		readOnlyComponent(isReadOnly("MandateDialog_Periodicity"), this.periodicity);
		readOnlyComponent(isReadOnly("MandateDialog_PhoneNumber"), this.phoneNumber);
		readOnlyComponent(isReadOnly("MandateDialog_ApprovalID"), this.approvalID);
		readOnlyComponent(isReadOnly("MandateDialog_Status"), this.status);
		readOnlyComponent(isReadOnly("MandateDialog_Status"), this.reason);
		readOnlyComponent(true, this.umrNumber);
		readOnlyComponent(isReadOnly("MandateDialog_BarCodeNumber"), this.barCodeNumber);
		readOnlyComponent(isReadOnly("MandateDialog_SwapIsActive"), this.swapIsActive);
		
		if (!this.mandate.isNew() && StringUtils.isEmpty(this.mandate.getRecordType())
				|| StringUtils.equals(this.mandate.getRecordType(), PennantConstants.RECORD_TYPE_UPD)) {
			readOnlyComponent(true, this.entityCode);
		} else {
			readOnlyComponent(isReadOnly("MandateDialog_EntityCode"), this.entityCode);
		}
		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}
			if (this.mandate.isNewRecord()) {
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(false);
			} else {
				this.btnCtrl.setWFBtnStatus_Edit(isFirstTask());
			}
			
			if(StringUtils.equals(getMandate().getRecordStatus(), PennantConstants.RCD_STATUS_APPROVED)){
				this.btnNotes.setVisible(true);
			}
		} else {
			this.btnCtrl.setBtnStatus_Edit();
		}
		logger.debug("Leaving ");
	}

	public boolean isReadOnly(String componentName) {
		if (isWorkFlowEnabled() || fromLoan) {
			return getUserWorkspace().isReadOnly(componentName);
		}
		return false;
	}

	/**
	 * Cancel the actual operation. <br>
	 * <br>
	 * Resets to the original status.<br>
	 * @throws Exception 
	 * @throws WrongValueException 
	 * 
	 */
	private void doCancel() throws WrongValueException, Exception {
		logger.debug("Entering ");

		doWriteBeanToComponents(this.mandate.getBefImage());
		doReadOnly();
		this.btnCtrl.setInitEdit();
		this.btnCancel.setVisible(false);

		logger.debug("Leaving ");
	}

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly() {
		logger.debug("Entering ");
		readOnlyComponent(true, this.custID);
		readOnlyComponent(true, this.mandateRef);
		readOnlyComponent(true, this.inputDate);
		readOnlyComponent(true, this.mandateType);
		readOnlyComponent(true, this.bankBranchID);
		readOnlyComponent(true, this.accNumber);
		readOnlyComponent(true, this.accHolderName);
		readOnlyComponent(true, this.jointAccHolderName);
		readOnlyComponent(true, this.reason);
		readOnlyComponent(true, this.accType);
		readOnlyComponent(true, this.openMandate);
		readOnlyComponent(true, this.startDate);
		readOnlyComponent(true, this.expiryDate);
		readOnlyComponent(true, this.maxLimit);
		readOnlyComponent(true, this.periodicity);
		readOnlyComponent(true, this.phoneNumber);
		readOnlyComponent(true, this.status);
		readOnlyComponent(true, this.approvalID);
		readOnlyComponent(true, this.umrNumber);
		readOnlyComponent(true, this.finReference);
		readOnlyComponent(true, this.barCodeNumber);
		readOnlyComponent(true, this.swapIsActive);
		readOnlyComponent(true, this.entityCode);

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(true);
			}
		}
		if (isWorkFlowEnabled()) {
			this.recordStatus.setValue("");
			this.userAction.setSelectedIndex(0);
		}
		logger.debug("Leaving ");
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aMandate
	 *            Mandate
	 * @param tab
	 * @throws Exception 
	 * @throws WrongValueException 
	 */
	public void doWriteBeanToComponents(Mandate aMandate) throws WrongValueException, Exception {
		logger.debug("Entering");
		if (fromLoan) {
			this.useExisting.setChecked(aMandate.isUseExisting());
			useExisting();
		} else {
			readOnlyComponent(true, this.mandateRef);
		}
		if (aMandate.getCustID() != Long.MIN_VALUE && aMandate.getCustID() != 0) {
			this.custID.setAttribute("custID", aMandate.getCustID());
			this.custID.setValue(aMandate.getCustCIF(), aMandate.getCustShrtName());
		}

		fillComboBox(this.mandateType, aMandate.getMandateType(), mandateTypeList, "");

		List<String> excludeList = new ArrayList<String>();
		if (registration) {
			excludeList.add(MandateConstants.STATUS_FIN);
			excludeList.add(MandateConstants.STATUS_NEW);
			excludeList.add(MandateConstants.STATUS_APPROVED);
			excludeList.add(MandateConstants.STATUS_AWAITCON);
			excludeList.add(MandateConstants.STATUS_HOLD);
			excludeList.add(MandateConstants.STATUS_RELEASE);
			excludeList.add(MandateConstants.STATUS_CANCEL);
		} else if (maintain) {
			excludeList.add(MandateConstants.STATUS_FIN);
			excludeList.add(MandateConstants.STATUS_NEW);
			//excludeList.add(MandateConstants.STATUS_APPROVED);
			excludeList.add(MandateConstants.STATUS_AWAITCON);
			excludeList.add(MandateConstants.STATUS_REJECTED);
			excludeList.add(MandateConstants.STATUS_CANCEL);
			excludeList.add(MandateConstants.STATUS_INPROCESS);

			// get previous mandate status from main
			Mandate oldMnadate = getMandateService().getApprovedMandateById(aMandate.getMandateID());
			if (StringUtils.trimToEmpty(oldMnadate.getStatus()).equals(MandateConstants.STATUS_HOLD)) {
				excludeList.add(MandateConstants.STATUS_HOLD);
			} else {
				excludeList.add(MandateConstants.STATUS_RELEASE);
			}
		}
		fillComboBox(this.status, aMandate.getStatus(), statusTypeList, excludeList);
		this.reason.setValue(aMandate.getReason());
		doWriteData(aMandate);
		onChangeMandateType(StringUtils.trimToEmpty(aMandate.getMandateType()));

		this.approvalID.setValue(aMandate.getApprovalID());
		this.recordStatus.setValue(aMandate.getRecordStatus());
		if (aMandate.isNew()) {
			this.inputDate.setValue(DateUtility.getAppDate());
			this.active.setChecked(true);
		} else {
			this.inputDate.setValue(aMandate.getInputDate());
		}

		logger.debug("Leaving");
	}

	private void doWriteData(Mandate aMandate) throws WrongValueException, Exception {

		ccyFormatter = CurrencyUtil.getFormat(aMandate.getMandateCcy());

		if (aMandate.isNewRecord()) {
			if (StringUtils.isEmpty(aMandate.getPeriodicity())) {
				aMandate.setPeriodicity(MandateConstants.MANDATE_DEFAULT_FRQ);
			}
			if (aMandate.getStartDate() == null) {
				aMandate.setStartDate(DateUtility.getAppDate());
			}
		}

		if (aMandate.getMandateID() != 0 && aMandate.getMandateID() != Long.MIN_VALUE) {
			this.mandateRef.setAttribute("mandateID", aMandate.getMandateID());
			this.mandateRef.setValue(String.valueOf(aMandate.getMandateID()),
					StringUtils.trimToEmpty(aMandate.getMandateRef()));
		}

		if (aMandate.getBankBranchID() != Long.MIN_VALUE && aMandate.getBankBranchID() != 0) {
			this.bankBranchID.setAttribute("bankBranchID", aMandate.getBankBranchID());
			this.bankBranchID.setValue(StringUtils.trimToEmpty(aMandate.getBranchCode()),
					StringUtils.trimToEmpty(aMandate.getBranchDesc()));
		}
		this.city.setValue(StringUtils.trimToEmpty(aMandate.getCity()));
		this.cityName.setValue(StringUtils.trimToEmpty(aMandate.getPccityName()));
		this.bank.setValue(StringUtils.trimToEmpty(aMandate.getBankName()));
		this.micr.setValue(aMandate.getMICR());
		this.ifsc.setValue(aMandate.getIFSC());
		this.accNumber.setValue(aMandate.getAccNumber());
		this.accHolderName.setValue(aMandate.getAccHolderName());
		this.jointAccHolderName.setValue(aMandate.getJointAccHolderName());
		fillComboBox(this.accType, aMandate.getAccType(), accTypeList, "");
		this.openMandate.setChecked(aMandate.isOpenMandate());
		this.startDate.setValue(aMandate.getStartDate());
		this.expiryDate.setValue(aMandate.getExpiryDate());
		this.maxLimit.setValue(PennantAppUtil.formateAmount(aMandate.getMaxLimit(), ccyFormatter));
		this.periodicity.setValue(aMandate.getPeriodicity());
		this.phoneNumber.setValue(aMandate.getPhoneNumber());
		this.reason.setValue(aMandate.getReason());
		this.umrNumber.setValue(aMandate.getMandateRef());
		this.documentName.setValue(aMandate.getDocumentName());
		
		setMandateDocument(aMandate);

		this.barCodeNumber.setValue(aMandate.getBarCodeNumber());
		this.finReference.setValue(aMandate.getOrgReference());

		if (!StringUtils.equals(aMandate.getStatus(), PennantConstants.List_Select)) {
			this.regStatus.setValue(PennantAppUtil.getlabelDesc(aMandate.getStatus(), PennantStaticListUtil.getStatusTypeList(SysParamUtil.getValueAsString(MandateConstants.MANDATE_CUSTOM_STATUS))));
		} 

		this.amountInWords.setValue(AmtInitialCap());
		this.swapIsActive.setChecked(aMandate.isSwapIsActive());
		if (!enqModule && !registration) {
			checkOpenMandate();
		}
		//Entity Field
		if (StringUtils.isNotBlank(aMandate.getEntityCode())) {
			this.entityCode.setValue(StringUtils.trimToEmpty(aMandate.getEntityCode()),
					StringUtils.trimToEmpty(aMandate.getEntityDesc()));
		}
	}

	/**
	 * 
	 * 
	 * @param aMandate
	 */
	private void setMandateDocument(Mandate aMandate) {
		if(aMandate.getDocImage() == null && StringUtils.isNotBlank(aMandate.getExternalRef())) {
			// Fetch document from interface
			String custCif=aMandate.getCustCIF();
			AMedia media = externalDocumentManager.getDocumentMedia(aMandate.getDocumentName(),aMandate.getExternalRef(),custCif);
			if (media!=null) {
				mandatedoc.setContent(media);
			}
		}
		AMedia amedia = null;
		if (aMandate.getDocImage() != null) {
			amedia = new AMedia(aMandate.getDocumentName(), null, null, aMandate.getDocImage());
			imagebyte = aMandate.getDocImage();
			mandatedoc.setContent(amedia);
		}
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aMandate
	 */
	public ArrayList<WrongValueException> doWriteComponentsToBean(Mandate aMandate, Tab tab) {
		logger.debug("Entering");

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();
		Object mandateID = this.mandateRef.getAttribute("mandateID");
		if (mandateID!=null) {
			aMandate.setMandateID((long) mandateID);
		}else{
			aMandate.setMandateID(Long.MIN_VALUE);
		}
	
		aMandate.setUseExisting(this.useExisting.isChecked());
		// Customer ID
		try {
			this.custID.getValidatedValue();
			Object obj = this.custID.getAttribute("custID");
			if (obj != null) {
				if (!StringUtils.isEmpty(obj.toString())) {
					aMandate.setCustID(Long.valueOf((obj.toString())));
				}
			}

		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Mandate Reference
		try {

			String ref = this.mandateRef.getValue();
			if (fromLoan && this.useExisting.isChecked() && StringUtils.isEmpty(ref)) {
				throw new WrongValueException(this.mandateRef.getTextbox(), Labels.getLabel("FIELD_IS_MAND",
						new String[] { Labels.getLabel("label_MandateDialog_MandateRef.value") }));
			}

			Object obj = this.mandateRef.getAttribute("mandateRef");
			if (obj != null) {
				if (!StringUtils.isEmpty(obj.toString())) {
					aMandate.setMandateRef(obj.toString());
				}
			} else {
				aMandate.setMandateRef(null);
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Mandate Type
		try {
			aMandate.setMandateType(this.mandateType.getSelectedItem().getValue().toString());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Bank Branch ID
		try {
			this.bankBranchID.getValidatedValue();
			Object obj = this.bankBranchID.getAttribute("bankBranchID");
			if (obj != null) {
				aMandate.setBankBranchID(Long.valueOf(String.valueOf(obj)));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Account Number
		try {
			aMandate.setAccNumber(PennantApplicationUtil.unFormatAccountNumber(this.accNumber.getValue()));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Account Holder Name
		try {
			aMandate.setAccHolderName(this.accHolderName.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Joint Account Holder Name
		try {
			aMandate.setJointAccHolderName(this.jointAccHolderName.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Account Type
		try {
			this.accType.getValue();
			if (this.accType.getSelectedItem() != null && this.accType.getSelectedItem().getValue() != null) {
				aMandate.setAccType(this.accType.getSelectedItem().getValue().toString());
			} else {
				aMandate.setAccType("");
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Open Mandate
		try {
			aMandate.setOpenMandate(this.openMandate.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Start Date
		try {
			aMandate.setStartDate(this.startDate.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Expiry Date
		try {
			aMandate.setExpiryDate(this.expiryDate.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Max Limit
		try {
			aMandate.setMaxLimit(PennantAppUtil.unFormateAmount(this.maxLimit.getActualValue(), ccyFormatter));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Periodicity
		try {
			if (this.periodicity.isValidComboValue()) {
				aMandate.setPeriodicity(this.periodicity.getValue() == null ? "" : this.periodicity.getValue());
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		// Phone Country Code
		try {
			aMandate.setPhoneNumber(this.phoneNumber.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Status
		try {

			if (registration) {
				this.status.setConstraint(new StaticListValidator(statusTypeList, Labels
						.getLabel("label_MandateDialog_Status.value")));
			}
			aMandate.setStatus(this.status.getSelectedItem().getValue().toString());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// InputDate
		try {
			aMandate.setInputDate(DateUtility.getAppDate());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		// Open Mandate
		try {
			aMandate.setActive(this.active.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		// Reason
		try {
			aMandate.setReason(this.reason.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		// MandateCcy
		try {
			aMandate.setMandateCcy(SysParamUtil.getAppCurrency());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// For Mandate Registration
        aMandate.setApprovalID(String.valueOf((getUserWorkspace().getLoggedInUser().getUserId())));
		// Bar Code
		try {
			aMandate.setBarCodeNumber(this.barCodeNumber.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		// DocumentName
		try {
			aMandate.setDocumentName(this.documentName.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		// Document Image
		try {
			aMandate.setDocImage(this.imagebyte);
		} catch (WrongValueException we) {
			wve.add(we);
		}

		// Fin Reference
		try {
			aMandate.setOrgReference(StringUtils.trimToNull(this.finReference.getValue()));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		
		// Swap Flag
		try {
			aMandate.setSwapIsActive(this.swapIsActive.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		List<ErrorDetail> errors = mandateService.doValidations(aMandate);
		if (errors != null && errors.size() > 0) {
			for (ErrorDetail error : errors) {
				WrongValueException wa = new WrongValueException(
						Path.getComponent("/outerIndexWindow/window_MandateDialog/" + error.getField()),
						MessageFormats.format(error.getMessage(),
								new String[] { Labels.getLabel("label_MandateDialog_BarCodeNumber.value") }));
				wve.add(wa);
			}
		}
		
		//Entity
		try {
			aMandate.setEntityCode(this.entityCode.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		logger.debug("Leaving");
		return wve;
	}

	/**
	 * Writes the showErrorDetails method for .<br>
	 * displaying exceptions if occured
	 */
	private void showErrorDetails(ArrayList<WrongValueException> wve) {
		logger.debug("Entering");
		doRemoveValidation();
		if (wve.size() > 0) {
			logger.debug("Throwing occured Errors By using WrongValueException");
			if (parenttab != null) {
				parenttab.setSelected(true);
			}
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = wve.get(i);
				if (i == 0) {
					Component comp = wvea[i].getComponent();
					if (comp instanceof HtmlBasedComponent) {
						Clients.scrollIntoView(comp);
					}
				}
				logger.debug(wvea[i]);
			}
			throw new WrongValuesException(wvea);
		}

		logger.debug("Leaving");
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation(boolean validate) {
		logger.debug("Entering");
		// Customer ID
		if (!this.custID.isReadonly()) {
			this.custID.setConstraint(new PTStringValidator(Labels.getLabel("label_MandateDialog_CustID.value"), null,
					validate, true));
		}
		// Mandate Type
		if (!this.mandateType.isDisabled()) {
			this.mandateType.setConstraint(new StaticListValidator(mandateTypeList, Labels
					.getLabel("label_MandateDialog_MandateType.value")));
		}
		// Bank Branch ID
		if (!this.bankBranchID.isReadonly()) {
			this.bankBranchID.setConstraint(new PTStringValidator(Labels
					.getLabel("label_MandateDialog_BankBranchID.value"), null, validate));
		}
		// Account Number
		if (!this.accNumber.isReadonly()) {
			this.accNumber.setConstraint(new PTStringValidator(Labels.getLabel("label_MandateDialog_AccNumber.value"),
					PennantRegularExpressions.REGEX_ACCOUNTNUMBER, validate,accNoLength));
		}
		// Account Holder Name
		if (!this.accHolderName.isReadonly()) {
			this.accHolderName.setConstraint(new PTStringValidator(Labels
					.getLabel("label_MandateDialog_AccHolderName.value"), PennantRegularExpressions.REGEX_ACC_HOLDER_NAME,
					validate));
		}
		// Joint Account Holder Name
		if (!this.jointAccHolderName.isReadonly()) {
			this.jointAccHolderName.setConstraint(new PTStringValidator(Labels
					.getLabel("label_MandateDialog_JointAccHolderName.value"), PennantRegularExpressions.REGEX_ACC_HOLDER_NAME,
					false));
		}
		// Account Type
		if (!this.accType.isDisabled() && validate) {
			this.accType.setConstraint(new StaticListValidator(accTypeList, Labels
					.getLabel("label_MandateDialog_AccType.value")));
		}
		// Start Date
		Date mandbackDate = DateUtility.addDays(DateUtility.getAppDate(),-SysParamUtil.getValueAsInt("MANDATE_STARTDATE"));
		Date appExpiryDate = SysParamUtil.getValueAsDate("APP_DFT_END_DATE");

		if (!this.startDate.isDisabled() && this.startDate.isButtonVisible()) {
			this.startDate.setConstraint(new PTDateValidator(Labels.getLabel("label_MandateDialog_StartDate.value"),
					validate, mandbackDate, appExpiryDate, true));
		}
		// Expiry Date
		if (!this.expiryDate.isDisabled() && this.expiryDate.isButtonVisible()) {
			try {
				this.expiryDate.setConstraint(new PTDateValidator(Labels
						.getLabel("label_MandateDialog_ExpiryDate.value"), validate, this.startDate.getValue(),
						appExpiryDate, this.openMandate.isChecked()));
			} catch (WrongValueException we) {
				this.expiryDate.setConstraint(new PTDateValidator(Labels
						.getLabel("label_MandateDialog_ExpiryDate.value"), validate, true, null, false));
			}
		}
		// Max Limit
		if (!this.maxLimit.isReadonly()) {
			this.maxLimit.setConstraint(new PTDecimalValidator(Labels.getLabel("label_MandateDialog_MaxLimit.value"),
					ccyFormatter, validate, false));
		}
		// Periodicity
		if (!this.phoneNumber.isReadonly()) {
			this.phoneNumber.setConstraint(new PTMobileNumberValidator(Labels.getLabel("label_MandateDialog_PhoneNumber.value"), false));
		}
		// Reason
		if (!this.reason.isReadonly()) {
			this.reason.setConstraint(new PTStringValidator(Labels.getLabel("label_MandateStatusDialog_Reason.value"),
					PennantRegularExpressions.REGEX_DESCRIPTION, flag));
		}

		// Bar Code
		if (!this.barCodeNumber.isReadonly()) {
			
			this.barCodeNumber
					.setConstraint(new PTStringValidator(Labels.getLabel("label_MandateDialog_BarCodeNumber.value"),
							PennantRegularExpressions.REGEX_BARCODE_NUMBER, false));
		}

		// Loan Reference
		if (!this.finReference.isReadonly()) {
			this.finReference.setConstraint(
					new PTStringValidator(Labels.getLabel("label_MandateDialog_FinReference.value"), null,
							ImplementationConstants.CLIENT_NFL ? false : validate));
		}
		//Entity
		if (!this.entityCode.isReadonly()){
			this.entityCode.setConstraint(
					new PTStringValidator(Labels.getLabel("label_MandateDialog_EntityCode.value"), null, validate,true));
		}
		// Status
		//		if (!this.status.isDisabled()) {
		//			this.status.setConstraint(new StaticListValidator(statusTypeList, Labels.getLabel("label_MandateDialog_Status.value")));
		//		}
	}

	/**
	 * Remove the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering");
		this.custID.setConstraint("");
		this.mandateRef.setConstraint("");
		this.inputDate.setConstraint("");
		this.mandateType.setConstraint("");
		this.bankBranchID.setConstraint("");
		this.accNumber.setConstraint("");
		this.accHolderName.setConstraint("");
		this.jointAccHolderName.setConstraint("");
		this.accType.setConstraint("");
		this.startDate.setConstraint("");
		this.expiryDate.setConstraint("");
		this.maxLimit.setConstraint("");
		this.phoneNumber.setConstraint("");
		this.status.setConstraint("");
		this.approvalID.setConstraint("");
		this.reason.setConstraint("");
		this.barCodeNumber.setConstraint("");
		this.finReference.setConstraint("");
		this.entityCode.setConstraint("");
		logger.debug("Leaving");
	}

	/**
	 * Remove Error Messages for Fields
	 */
	@Override
	protected void doClearMessage() {
		logger.debug("Entering");
		this.custID.setErrorMessage("");
		this.mandateRef.setErrorMessage("");
		this.mandateRef.clearErrorMessage();
		this.mandateType.setErrorMessage("");
		this.bankBranchID.setErrorMessage("");
		this.accNumber.setErrorMessage("");
		this.accHolderName.setErrorMessage("");
		this.jointAccHolderName.setErrorMessage("");
		this.accType.setErrorMessage("");
		this.startDate.setErrorMessage("");
		this.expiryDate.setErrorMessage("");
		this.maxLimit.setErrorMessage("");
		this.phoneNumber.setErrorMessage("");
		this.status.setErrorMessage("");
		this.approvalID.setErrorMessage("");
		this.reason.setErrorMessage("");
		this.documentName.setErrorMessage("");
		this.barCodeNumber.setErrorMessage("");
		this.finReference.setErrorMessage("");
		this.entityCode.setErrorMessage("");
		logger.debug("Leaving");
	}

	/**
	 * Refresh the list page with the filters that are applied in list page.
	 */
	private void refreshList() {
		if (registration) {
			getMandateRegistrationListCtrl().search();
		} else {
			getMandateListCtrl().search();
		}
	}

	public void onSelect$status(Event event) {
		logger.debug("Entering" + event.toString());
		this.space_Reason.setSclass("mandatory");
		flag = true;
		logger.debug("Leaving" + event.toString());
	}

	public void onFulfill$bankBranchID(Event event) {
		logger.debug("Entering" + event.toString());

		Object dataObject = this.bankBranchID.getObject();

		if (dataObject == null || dataObject instanceof String) {
			this.bank.setValue("");
			this.city.setValue("");
			this.micr.setValue("");
			this.ifsc.setValue("");
			this.cityName.setValue("");
		} else {
			BankBranch details = (BankBranch) dataObject;
			if (details != null) {
				this.bankBranchID.setAttribute("bankBranchID", details.getBankBranchID());
				this.bank.setValue(details.getBankName());
				this.micr.setValue(details.getMICR());
				this.ifsc.setValue(details.getIFSC());
				this.city.setValue(details.getCity());
				this.cityName.setValue(details.getPCCityName());
				if(StringUtils.isNotBlank(details.getBankCode())){
					accNoLength = getBankDetailService().getAccNoLengthByCode(details.getBankCode());
				}
				if (accNoLength==0) {
					accNoLength = LengthConstants.LEN_ACCOUNT;
				}
				this.accNumber.setMaxlength(accNoLength);
			}
		}

		logger.debug("Leaving" + event.toString());
	}

	public void onFulfill$custID(Event event) {
		logger.debug("Entering" + event.toString());

		Object dataObject = custID.getObject();

		if (dataObject instanceof String) {
			this.custID.setValue(dataObject.toString());
		} else {
			Customer details = (Customer) dataObject;
			if (details != null) {
				this.custID.setAttribute("custID", details.getCustID());
			}
		}
		getFinReferences();
		logger.debug("Leaving" + event.toString());
	}

	public void onFulfill$mandateRef(Event event) throws WrongValueException, Exception {
		logger.debug("Entering" + event.toString());

		Object dataObject = mandateRef.getObject();

		if (dataObject instanceof String) {
			this.mandateRef.setValue(dataObject.toString());
			this.mandateRef.setAttribute("mandateID", Long.MIN_VALUE);
			this.label_RegStatus.setVisible(false);
			clearMandatedata();
		} else {
			Mandate details = (Mandate) dataObject;
			if (details != null) {
				this.mandateRef.setAttribute("mandateID", details.getMandateID());
				mandateService.getDocumentImage(details);
				this.label_RegStatus.setVisible(true);
				doWriteData(details);
			}else{
				this.mandateRef.setValue("");
				this.mandateRef.setAttribute("mandateID", Long.MIN_VALUE);
				this.label_RegStatus.setVisible(false);
				clearMandatedata();
			}
		}

		logger.debug("Leaving" + event.toString());
	}

	public void onFulfill$finReference(Event event) {
		logger.debug("Entering" + event.toString());

		Object dataObject = finReference.getObject();

		if (dataObject instanceof String) {
			this.finReference.setValue(dataObject.toString());
		} else {
			FinanceMain details = (FinanceMain) dataObject;
			if (details != null) {
				this.finReference.setValue(details.getFinReference());
			}else{
				this.finReference.setValue("");
			}
		}

		logger.debug("Leaving" + event.toString());
	}

	public void onFulfill$maxLimit(Event event) throws WrongValueException, Exception {
		logger.debug("Entering" + event.toString());

		this.amountInWords.setValue(AmtInitialCap());

		logger.debug("Leaving" + event.toString());
	}
	private String AmtInitialCap() throws WrongValueException, Exception {
		String amtInWords = NumberToEnglishWords.getNumberToWords(this.maxLimit.getActualValue().toBigInteger());
	    
		String[] words = amtInWords.split(" ");
	    StringBuffer AmtInWord = new StringBuffer();

		for (int i = 0; i < words.length; i++) {
			if (!words[i].isEmpty()) {
				AmtInWord.append(Character.toUpperCase(words[i].charAt(0))).append(words[i].substring(1)).append(" ");

			}

		}
	    return AmtInWord.toString().trim();
	}  
	

	// *****************************************************************
	// ************************+ crud operations ***********************
	// *****************************************************************

	/**
	 * Deletes a Mandate object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug("Entering");
		final Mandate aMandate = new Mandate();
		BeanUtils.copyProperties(getMandate(), aMandate);
		String tranType = PennantConstants.TRAN_WF;
		
		//in delete case if approver approves needs notes 
		if (this.btnNotes.isVisible() && !notesEntered) {
			MessageUtil.showError(Labels.getLabel("Notes_NotEmpty"));
			return ;
		}
		
		// Show a confirm box
		final String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record") + "\n\n --> "
				+ aMandate.getMandateID();
		if (MessageUtil.confirm(msg) == MessageUtil.YES) {
			if (StringUtils.trimToEmpty(aMandate.getRecordType()).equals("")) {
				aMandate.setVersion(aMandate.getVersion() + 1);
				aMandate.setRecordType(PennantConstants.RECORD_TYPE_DEL);

				if (isWorkFlowEnabled()) {
					aMandate.setRecordStatus(userAction.getSelectedItem().getValue().toString());
					aMandate.setNewRecord(true);
					tranType = PennantConstants.TRAN_WF;
					getWorkFlowDetails(userAction.getSelectedItem().getLabel(), aMandate.getNextTaskId(), aMandate);
				} else {
					tranType = PennantConstants.TRAN_DEL;
				}
			}

			try {
				if (doProcess(aMandate, tranType)) {
					refreshList();
					closeDialog();
				}

			} catch (DataAccessException e) {
				MessageUtil.showError(e);
			}
		}
		logger.debug("Leaving");
	}

	/**
	 * Clears the components values. <br>
	 */
	public void doClear() {
		logger.debug("Entering");
		// remove validation, if there are a save before

		this.custID.setValue("");
		this.mandateRef.setValue("");
		this.inputDate.setValue(null);
		this.mandateType.setValue("");
		this.bankBranchID.setValue("");
		this.accNumber.setValue("");
		this.accHolderName.setValue("");
		this.jointAccHolderName.setValue("");
		this.accType.setValue("");
		this.openMandate.setChecked(false);
		this.startDate.setValue(null);
		this.expiryDate.setValue(null);
		this.maxLimit.setValue(BigDecimal.ZERO);
		this.periodicity.setValue("");
		this.phoneNumber.setValue("");
		this.status.setValue("");
		this.approvalID.setValue("");
		this.bank.setValue("");
		this.micr.setValue("");
		this.ifsc.setValue("");
		this.city.setValue("");
		this.reason.setValue("");
		this.documentName.setValue("");
		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug("Entering");
		final Mandate aMandate = new Mandate();
		BeanUtils.copyProperties(getMandate(), aMandate);
		boolean isNew = false;

		if (isWorkFlowEnabled()) {
			aMandate.setRecordStatus(userAction.getSelectedItem().getValue().toString());
			getWorkFlowDetails(userAction.getSelectedItem().getLabel(), aMandate.getNextTaskId(), aMandate);
		}

		// *************************************************************
		// force validation, if on, than execute by component.getValue()
		// *************************************************************
		if (!PennantConstants.RECORD_TYPE_DEL.equals(aMandate.getRecordType()) && isValidation()) {
			doSetValidation(true);
			// fill the Mandate object with the components data
			ArrayList<WrongValueException> wve = doWriteComponentsToBean(aMandate, null);
			showErrorDetails(wve);
		}
		// Write the additional validations as per below example
		// get the selected branch object from the listbox
		// Do data level validations here

		isNew = aMandate.isNew();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.trimToEmpty(aMandate.getRecordType()).equals("")) {
				aMandate.setVersion(aMandate.getVersion() + 1);
				if (isNew) {
					aMandate.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aMandate.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aMandate.setNewRecord(true);
				}
			}
		} else {
			aMandate.setVersion(aMandate.getVersion() + 1);
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}

		// save it to database
		try {
			if (registration) {
				aMandate.setModule(MandateConstants.MODULE_REGISTRATION);
			}

			if (doProcess(aMandate, tranType)) {
				// doWriteBeanToComponents(aMandate);
				refreshList();
				closeDialog();
			}

		} catch (final DataAccessException e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving");
	}

	/**
	 * Set the workFlow Details List to Object
	 * 
	 * @param aAuthorizedSignatoryRepository
	 *            (AuthorizedSignatoryRepository)
	 * 
	 * @param tranType
	 *            (String)
	 * 
	 * @return boolean
	 * 
	 */

	private boolean doProcess(Mandate aMandate, String tranType) {
		logger.debug("Entering");
		boolean processCompleted = false;
		aMandate.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		aMandate.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aMandate.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {

			if (!"Save".equals(userAction.getSelectedItem().getLabel())) {
				if (auditingReq) {
					if (!notesEntered) {
						MessageUtil.showError(Labels.getLabel("Notes_NotEmpty"));
						return false;
					}
				}
			}

		
			aMandate.setTaskId(getTaskId());
			aMandate.setNextTaskId(getNextTaskId());
			aMandate.setRoleCode(getRole());
			aMandate.setNextRoleCode(getNextRoleCode());

			if (StringUtils.isBlank(getOperationRefs())) {
				processCompleted = doSaveProcess(getAuditHeader(aMandate, tranType), null);
			} else {
				String[] list = getOperationRefs().split(";");
				AuditHeader auditHeader = getAuditHeader(aMandate, PennantConstants.TRAN_WF);

				for (int i = 0; i < list.length; i++) {
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		} else {
			processCompleted = doSaveProcess(getAuditHeader(aMandate, tranType), null);
		}
		logger.debug("return value :" + processCompleted);
		logger.debug("Leaving");
		return processCompleted;
	}

	/**
	 * Get the result after processing DataBase Operations
	 * 
	 * @param AuditHeader
	 *            auditHeader
	 * @param method
	 *            (String)
	 * @return boolean
	 * 
	 */

	private boolean doSaveProcess(AuditHeader auditHeader, String method) {
		logger.debug("Entering");
		boolean processCompleted = false;
		int retValue = PennantConstants.porcessOVERIDE;
		boolean deleteNotes = false;

		Mandate aMandate = (Mandate) auditHeader.getAuditDetail().getModelData();

		try {

			while (retValue == PennantConstants.porcessOVERIDE) {

				if (StringUtils.trimToEmpty(method).equalsIgnoreCase("")) {
					if (PennantConstants.TRAN_DEL.equals(auditHeader.getAuditTranType())) {
						auditHeader = getMandateService().delete(auditHeader);
						deleteNotes = true;
					} else {
						auditHeader = getMandateService().saveOrUpdate(auditHeader);
					}

				} else {
					if (PennantConstants.method_doApprove.equalsIgnoreCase(StringUtils.trimToEmpty(method))) {
						auditHeader = getMandateService().doApprove(auditHeader);

						if (PennantConstants.RECORD_TYPE_DEL.equals(aMandate.getRecordType())) {
							deleteNotes = true;
						}

					} else if (PennantConstants.method_doReject.equalsIgnoreCase(StringUtils.trimToEmpty(method))) {
						auditHeader = getMandateService().doReject(auditHeader);
						if (PennantConstants.RECORD_TYPE_NEW.equals(aMandate.getRecordType())) {
							deleteNotes = true;
						}

					} else {
						auditHeader.setErrorDetails(new ErrorDetail(PennantConstants.ERR_9999, Labels
								.getLabel("InvalidWorkFlowMethod"), null));
						retValue = ErrorControl.showErrorControl(this.window_MandateDialog, auditHeader);
						return processCompleted;
					}
				}

				auditHeader = ErrorControl.showErrorDetails(this.window_MandateDialog, auditHeader);
				retValue = auditHeader.getProcessStatus();

				if (retValue == PennantConstants.porcessCONTINUE) {
					processCompleted = true;

					if (deleteNotes) {
						deleteNotes(
								getNotes("Mandate", String.valueOf(aMandate.getMandateID()), aMandate.getVersion()),
								true);
					}
				}

				if (retValue == PennantConstants.porcessOVERIDE) {
					auditHeader.setOveride(true);
					auditHeader.setErrorMessage(null);
					auditHeader.setInfoMessage(null);
					auditHeader.setOverideMessage(null);
				}
			}
		} catch (InterruptedException e) {
			logger.error("Exception: ", e);
		}
		setOverideMap(auditHeader.getOverideMap());

		logger.debug("return Value:" + processCompleted);
		logger.debug("Leaving");
		return processCompleted;
	}

	// ******************************************************//
	// ***************** WorkFlow Components*****************//
	// ******************************************************//

	/**
	 * @param aAuthorizedSignatoryRepository
	 * @param tranType
	 * @return
	 */

	private AuditHeader getAuditHeader(Mandate aMandate, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aMandate.getBefImage(), aMandate);
		return new AuditHeader(String.valueOf(aMandate.getMandateID()), null, null, null, auditDetail,
				aMandate.getUserDetails(), getOverideMap());
	}

	/**
	 * This method is for append finance basic details to respective parent tabs
	 */
	private void appendFinBasicDetails(ArrayList<Object> finHeaderList) {
		try {
			final HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("parentCtrl", this);
			if (finHeaderList != null) {
				map.put("finHeaderList", finHeaderList);
			}
			Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/FinBasicDetails.zul", this.finBasicdetails,
					map);
		} catch (Exception e) {
			logger.debug(e);
		}
	}

	public void doSetLabels(ArrayList<Object> finHeaderList) {
		getFinBasicDetailsCtrl().doWriteBeanToComponents(finHeaderList);
	}

	public void checkTabDisplay(String mandateType, boolean onchange) {
		this.parenttab.setVisible(false);
		String val = StringUtils.trimToEmpty(mandateType);
		for (ValueLabel valueLabel : mandateTypeList) {
			if (val.equals(valueLabel.getValue())) {
				this.parenttab.setVisible(true);
				fillComboBox(this.mandateType, mandateType, mandateTypeList, "");
				break;
			}
		}

		addMandateFiletrs(mandateType);
		if (this.useExisting.isChecked() && onchange) {
			clearMandatedata();
		}
	}

	private void addMandateFiletrs(String repaymethod) {
		if (financemain != null) {
			if (StringUtils.isEmpty(repaymethod)) {
				repaymethod = financemain.getFinRepayMethod();
			}
			Filter[] filters = new Filter[5];
			filters[0] = new Filter("CustID", financemain.getCustID(), Filter.OP_EQUAL);
			filters[1] = new Filter("MandateType", repaymethod, Filter.OP_EQUAL);
			filters[2] = new Filter("Active", 1, Filter.OP_EQUAL);
			filters[3] = new Filter("STATUS", MandateConstants.STATUS_REJECTED, Filter.OP_NOT_EQUAL);
			filters[4] = new Filter("EntityCode", mandate.getEntityCode(),Filter.OP_EQUAL);
			this.mandateRef.setFilters(filters);
			this.mandateRef.setWhereClause("(OpenMandate = 1 or OrgReference is null)");
		}
	}

	public void doSave_Mandate(FinanceDetail financeDetail, Tab tab, boolean recSave) throws InterruptedException {
		logger.debug("Entering");

		doClearMessage();
		boolean validate=false;
		if (!recSave && !isReadOnly("MandateDialog_validate")) {
			validate=true;
		}
		doSetValidation(validate);

		ArrayList<WrongValueException> wve = doWriteComponentsToBean(getMandate(), tab);
		if (!wve.isEmpty() && parenttab != null) {
			parenttab.setSelected(true);
		}
		showErrorDetails(wve);

		if (StringUtils.isBlank(getMandate().getRecordType())) {
			getMandate().setVersion(getMandate().getVersion() + 1);
			getMandate().setRecordType(PennantConstants.RECORD_TYPE_NEW);
			getMandate().setNewRecord(true);
		}
		getMandate().setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		getMandate().setLastMntOn(new Timestamp(System.currentTimeMillis()));
		getMandate().setUserDetails(getUserWorkspace().getLoggedInUser());
		financeDetail.setMandate(getMandate());
		
		logger.debug("Leaving");
	}
	
	private String getCIFForCustomer(FinanceDetail financeDetail){
		if (financeDetail!=null && financeDetail.getCustomerDetails()!=null && financeDetail.getCustomerDetails().getCustomer() !=null) {
			return StringUtils.trimToEmpty(financeDetail.getCustomerDetails().getCustomer().getCustCIF());
		}
		return "";
	}
	
	public void onFulfill$entityCode(Event event) {
		logger.debug("Entering" + event.toString());

		Object dataObject = entityCode.getObject();

		if (dataObject instanceof String) {
			this.entityCode.setValue(dataObject.toString());
		}
		getFinReferences();

		logger.debug("Leaving" + event.toString());
	}

	// Process for Document uploading
	public void onUpload$btnUploadDoc(UploadEvent event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		Media media = event.getMedia();
		browseDoc(media, this.documentName);
		logger.debug("Leaving" + event.toString());
	}

	// Browse for Document uploading
	private void browseDoc(Media media, Textbox textbox) throws InterruptedException {
		logger.debug("Entering");
		try {
			String docType = "";
			if ("application/pdf".equals(media.getContentType())) {
				docType = PennantConstants.DOC_TYPE_PDF;
			} else if ("image/jpeg".equals(media.getContentType()) || "image/png".equals(media.getContentType())) {
				docType = PennantConstants.DOC_TYPE_IMAGE;
			} else {
				MessageUtil.showError(Labels.getLabel("UnSupported_Document"));
				return;
			}

			// Process for Correct Format Document uploading
			String fileName = media.getName();
			byte[] ddaImageData = IOUtils.toByteArray(media.getStreamData());
			// Data Fill by QR Bar Code Reader
			if (docType.equals(PennantConstants.DOC_TYPE_PDF)) {
				this.mandatedoc.setContent(
						new AMedia("document.pdf", "pdf", "application/pdf", new ByteArrayInputStream(ddaImageData)));

			} else if (docType.equals(PennantConstants.DOC_TYPE_IMAGE)) {
				this.mandatedoc.setContent(media);
			}
			this.mandatedoc.setVisible(true);
			textbox.setValue(fileName);
			imagebyte = media.getByteData();

		} catch (Exception ex) {
			logger.error("Exception: ", ex);
		}
		logger.debug("Leaving");
	}

	private void getFinReferences() {
		//Build Where Clause For FinRef
		
		if (StringUtils.isNotBlank(this.custID.getValue()) && StringUtils.isNotBlank(this.entityCode.getValue())) {
			this.finReference.setObject("");
			this.finReference.setValue("");	
			StringBuilder sql = new StringBuilder();
			sql.append(" FinType in(Select FinType from RMTFinanceTypes where FinDivision IN ");
			sql.append(" (Select DivisionCode from SMTDivisionDetail where EntityCode = '"+this.entityCode.getValue()+"'))");
			readOnlyComponent(false, this.finReference);
			this.finReference.setMandatoryStyle(ImplementationConstants.CLIENT_NFL ? false : true);
			this.finReference.setWhereClause(sql.toString());
			
			Filter[] filter = new Filter[2];
			Object obj = this.custID.getAttribute("custID");
			filter[0] = new Filter("FinIsActive", 1, Filter.OP_EQUAL);
			filter[1] = new Filter("CustID", Long.valueOf(obj.toString()), Filter.OP_EQUAL);
			this.finReference.setFilters(filter);
		} else {
			this.finReference.setValue("");
			readOnlyComponent(true, this.finReference);
			this.finReference.setMandatoryStyle(false);
		}
	
	}
	

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public Mandate getMandate() {
		return this.mandate;
	}

	public void setMandate(Mandate mandate) {
		this.mandate = mandate;
	}

	public void setMandateService(MandateService mandateService) {
		this.mandateService = mandateService;
	}

	public MandateService getMandateService() {
		return this.mandateService;
	}

	public void setMandateListCtrl(MandateListCtrl mandateListCtrl) {
		this.mandateListCtrl = mandateListCtrl;
	}

	public MandateListCtrl getMandateListCtrl() {
		return this.mandateListCtrl;
	}

	public boolean isNotes_Entered() {
		return notes_Entered;
	}

	public void setNotes_Entered(boolean notes_Entered) {
		this.notes_Entered = notes_Entered;
	}

	public void setMandateRegistrationListCtrl(MandateRegistrationListCtrl mandateRegistrationListCtrl) {
		this.mandateRegistrationListCtrl = mandateRegistrationListCtrl;
	}

	public MandateRegistrationListCtrl getMandateRegistrationListCtrl() {
		return this.mandateRegistrationListCtrl;
	}

	public FinBasicDetailsCtrl getFinBasicDetailsCtrl() {
		return finBasicDetailsCtrl;
	}

	public void setFinBasicDetailsCtrl(FinBasicDetailsCtrl finBasicDetailsCtrl) {
		this.finBasicDetailsCtrl = finBasicDetailsCtrl;
	}

	public Object getFinanceMainDialogCtrl() {
		return financeMainDialogCtrl;
	}

	public void setFinanceMainDialogCtrl(Object financeMainDialogCtrl) {
		this.financeMainDialogCtrl = financeMainDialogCtrl;
	}

	public BankDetailService getBankDetailService() {
		return bankDetailService;
	}

	public void setBankDetailService(BankDetailService bankDetailService) {
		this.bankDetailService = bankDetailService;
	}
	
	public void setExternalDocumentManager(ExternalDocumentManager externalDocumentManager) {
		this.externalDocumentManager = externalDocumentManager;
	}

}
