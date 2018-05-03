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
 * FileName    		:  BundledProductsDetailDialogCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  13-10-2011    														*
 *                                                                  						*
 * Modified Date    :  13-10-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 13-10-2011       Pennant	                 0.1                                            * 
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

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataAccessException;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Div;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Space;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.AccountSelectionBox;
import com.pennant.CurrencyBox;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.finance.BundledProductsDetail;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.service.finance.BundledProductsDetailService;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.util.ErrorControl;
import com.pennant.util.PennantAppUtil;
import com.pennant.util.Constraint.PTDecimalValidator;
import com.pennant.util.Constraint.PTEmailValidator;
import com.pennant.util.Constraint.PTPhoneNumberValidator;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.util.Constraint.RateValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the /WEB-INF/pages/LMTMasters/BundledProductsDetail/bundledProductsDetailDialog.zul
 * file.
 */
public class BundledProductsDetailDialogCtrl extends GFCBaseCtrl<BundledProductsDetail> {
	private static final long serialVersionUID = 3141943554064485540L;
	private static final Logger logger = Logger.getLogger(BundledProductsDetailDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the zul-file
	 * are getting autowired by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_BundledProductsDetailDialog;// autowired
	protected Groupbox gb_basicDetails; // autowired
	protected Textbox finReference; // autowired
	protected Textbox cardProduct; // autowired
	protected Textbox salesStaff; // autowired
	protected Textbox embossingName; // autowired
	protected Textbox stmtEmail; // autowired
	protected Textbox physicalAddress; // autowired
	protected CurrencyBox chequeAmt; // autowired
	protected CurrencyBox limitRecommended; // autowired
	protected CurrencyBox limitApproved; // autowired
	protected Textbox stmtAddress; // autowired
	protected Textbox ref1Name; // autowired
	protected Textbox ref1Email; // autowired
	protected Textbox ref2Name; // autowired
	protected Textbox ref2Email; // autowired
	protected Textbox bankName; // autowired
	protected Textbox chequeNo; // autowired
	protected Combobox statusOfCust; // autowired
	protected Combobox cardType; // autowired
	protected Combobox classType; // autowired
	protected Decimalbox minRepay; // autowired
	protected AccountSelectionBox billingAcc; // autowired
	protected Decimalbox profitRate; // autowired
	protected Checkbox crossSellCard; // autowired
	protected Checkbox urgentIssuance; // autowired
	protected Textbox ref1PhoneCountryCode; // autowired
	protected Textbox ref1PhoneAreaCode; // autowired
	protected Textbox ref1PhoneNumber; // autowired
	protected Textbox ref2PhoneCountryCode; // autowired
	protected Textbox ref2PhoneAreaCode; // autowired
	protected Textbox ref2PhoneNumber; // autowired
	protected Textbox contactNumCountryCode; // autowired
	protected Textbox contactNumAreaCode; // autowired
	protected Textbox contactPhoneNumber; // autowired

	protected Space space_CardProduct; // autowired
	protected Space space_SalesStaff; // autowired
	protected Space space_EmbossingName; // autowired
	protected Space space_StmtAddress; // autowired
	protected Space space_StmtEmail; // autowired
	protected Space space_PhysicalAddress; // autowired
	protected Space space_Ref1Name; // autowired
	protected Space space_Ref1Email; // autowired
	protected Space space_Ref2Name; // autowired
	protected Space space_Ref2Email; // autowired
	protected Space space_BankName; // autowired
	protected Space space_ChequeNo; // autowired
	protected Space space_StatusOfCust; // autowired
	protected Space space_CardType; // autowired
	protected Space space_ClassType; // autowired
	protected Space space_MinRepay; // autowired
	protected Space space_ProfitRate; // autowired
	protected Space space_ContactNumber; // autowired
	protected Space space_Ref1PhoneNumber; // autowired
	protected Space space_Ref2PhoneNumber; // autowired

	// not auto wired vars
	private BundledProductsDetail bundledProductsDetail; // overhanded per param

	private transient boolean validationOn;
	
	// ServiceDAOs / Domain Classes
	private transient BundledProductsDetailService bundledProductsDetailService;
	private HashMap<String, ArrayList<ErrorDetail>> overideMap = new HashMap<String, ArrayList<ErrorDetail>>();

	// For Dynamically calling of this Controller
	private Div toolbar;
	private FinanceDetail financedetail;
	private Object financeMainDialogCtrl;
	private Component parent = null;
	private Tab parentTab = null;
	private Grid grid_basicDetails;
	private Grid grid_Reference1;
	private Grid grid_Reference2;

	private transient boolean recSave = false;
	private transient boolean newFinance;
	public transient int ccyFormatter = 0;
	public ArrayList<Object> finBasicDetailList = null;
	protected Groupbox finBasicdetails;
	private FinBasicDetailsCtrl finBasicDetailsCtrl;

	/**
	 * default constructor.<br>
	 */
	public BundledProductsDetailDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "BundledProductsDetailDialog";
	}

	// Component Events

	/**
	 * Before binding the data and calling the dialog window we check, if the ZUL-file is called with a parameter for a
	 * selected BundledProductsDetail object in a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public void onCreate$window_BundledProductsDetailDialog(Event event) throws Exception {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_BundledProductsDetailDialog);

		if (event.getTarget().getParent() != null) {
			parent = event.getTarget().getParent();
		}

		// READ OVERHANDED params !
		if (arguments.containsKey("bundledProductsDetail")) {
			this.bundledProductsDetail = (BundledProductsDetail) arguments.get("bundledProductsDetail");
			BundledProductsDetail befImage = new BundledProductsDetail();
			BeanUtils.copyProperties(this.bundledProductsDetail, befImage);
			this.bundledProductsDetail.setBefImage(befImage);
			setBundledProductsDetail(this.bundledProductsDetail);
		} else {
			setBundledProductsDetail(null);
		}

		if (arguments.containsKey("financeDetail")) {
			setFinancedetail((FinanceDetail) arguments.get("financeDetail"));
			if (getFinancedetail() != null) {
				getFinancedetail().setBundledProductsDetail(
						getBundledProductsDetailService().getBundledProductsDetailById(
								getFinancedetail().getFinScheduleData().getFinanceMain().getFinReference(), "_TView"));
				setBundledProductsDetail(getFinancedetail().getBundledProductsDetail());
			}
		}

		if (arguments.containsKey("financeMainDialogCtrl")) {
			this.financeMainDialogCtrl = (Object) arguments.get("financeMainDialogCtrl");
			try {
				financeMainDialogCtrl.getClass().getMethod("setBundledProductsDetailDialogCtrl", this.getClass())
						.invoke(getFinanceMainDialogCtrl(), this);
			} catch (Exception e) {
				logger.error("Exception: ", e);
			}
			setNewFinance(true);
			this.window_BundledProductsDetailDialog.setTitle("");
		}

		if (arguments.containsKey("roleCode")) {
			setRole((String) arguments.get("roleCode"));
			getUserWorkspace().allocateRoleAuthorities(getRole(), "BundledProductsDetailDialog");
		}

		if (arguments.containsKey("ccyFormatter")) {
			this.ccyFormatter = (Integer) arguments.get("ccyFormatter");
		}

		if (arguments.containsKey("finBasicDetails")) {
			finBasicDetailList = (ArrayList<Object>) arguments.get("finBasicDetails");
		}

		if (arguments.containsKey("parentTab")) {
			parentTab = (Tab) arguments.get("parentTab");
		}

		if (isWorkFlowEnabled() && !isNewFinance()) {
			this.userAction = setListRecordStatus(this.userAction);
			getUserWorkspace().allocateRoleAuthorities(getRole(), "BundledProductsDetailDialog");
		}

		/* set components visible dependent of the users rights */
		doCheckRights();

		// set Field Properties
		doSetFieldProperties();
		doShowDialog(getBundledProductsDetail());
		logger.debug("Leaving");
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	public void doSetFieldProperties() {
		logger.debug("Entering");
		// Empty sent any required attributes
		this.cardProduct.setWidth("171px");
		this.chequeAmt.setWidth("171px");
		this.limitRecommended.setWidth("171px");
		this.limitApproved.setWidth("171px");
		this.salesStaff.setWidth("171px");
		this.embossingName.setWidth("171px");
		this.stmtAddress.setWidth("270px");
		this.physicalAddress.setWidth("270px");
		this.stmtEmail.setWidth("171px");
		this.ref1Name.setWidth("171px");
		this.ref1Email.setWidth("171px");
		this.ref2Name.setWidth("171px");
		this.ref2Email.setWidth("171px");
		this.bankName.setWidth("171px");
		this.chequeNo.setWidth("171px");
		this.statusOfCust.setWidth("171px");
		this.cardType.setWidth("171px");
		this.classType.setWidth("171px");

		this.ref1PhoneCountryCode.setMaxlength(4);
		this.ref1PhoneCountryCode.setWidth("50px");
		this.ref1PhoneAreaCode.setMaxlength(4);
		this.ref1PhoneAreaCode.setWidth("50px");
		this.ref1PhoneNumber.setMaxlength(8);
		this.ref1PhoneNumber.setWidth("100px");

		this.ref2PhoneCountryCode.setMaxlength(4);
		this.ref2PhoneCountryCode.setWidth("50px");
		this.ref2PhoneAreaCode.setMaxlength(4);
		this.ref2PhoneAreaCode.setWidth("50px");
		this.ref2PhoneNumber.setMaxlength(8);
		this.ref2PhoneNumber.setWidth("100px");

		this.contactNumCountryCode.setMaxlength(4);
		this.contactNumCountryCode.setWidth("50px");
		this.contactNumAreaCode.setMaxlength(4);
		this.contactNumAreaCode.setWidth("50px");
		this.contactPhoneNumber.setMaxlength(8);
		this.contactPhoneNumber.setWidth("100px");

		this.cardProduct.setMaxlength(20);
		this.salesStaff.setMaxlength(20);
		this.embossingName.setMaxlength(50);
		this.minRepay.setMaxlength(6);
		this.stmtAddress.setMaxlength(500);
		this.stmtEmail.setMaxlength(100);
		this.physicalAddress.setMaxlength(500);
		this.ref1Name.setMaxlength(50);
		this.ref1Email.setMaxlength(100);
		this.ref2Name.setMaxlength(50);
		this.ref2Email.setMaxlength(100);
		this.bankName.setMaxlength(50);
		this.chequeNo.setMaxlength(50);

		this.chequeAmt.setFormat(PennantApplicationUtil.getAmountFormate(ccyFormatter));
		this.chequeAmt.setScale(ccyFormatter);
		this.chequeAmt.setTextBoxWidth(171);

		this.limitRecommended.setFormat(PennantApplicationUtil.getAmountFormate(ccyFormatter));
		this.limitRecommended.setScale(ccyFormatter);
		this.limitRecommended.setTextBoxWidth(171);

		this.limitApproved.setFormat(PennantApplicationUtil.getAmountFormate(ccyFormatter));
		this.limitApproved.setScale(ccyFormatter);
		this.limitApproved.setTextBoxWidth(171);

		this.billingAcc.setAcountDetails(null, "", true);
		this.billingAcc.setMandatoryStyle(false);
		this.billingAcc.setTextBoxWidth(161);
		this.billingAcc.setButtonVisible(false);

		this.profitRate.setMaxlength(13);
		this.profitRate.setFormat(PennantConstants.rateFormate9);
		this.profitRate.setRoundingMode(BigDecimal.ROUND_DOWN);
		this.profitRate.setScale(9);

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
	 * The rights are get from the spring framework users grantedAuthority(). A right is only a string. <br>
	 */
	private void doCheckRights() {
		logger.debug("Entering");

		getUserWorkspace().allocateAuthorities("BundledProductsDetailDialog", getRole());

		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_BundledProductsDetailDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_BundledProductsDetailDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_BundledProductsDetailDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_BundledProductsDetailDialog_btnSave"));
		this.btnCancel.setVisible(false);

		logger.debug("Leaving");
	}

	public void doSave_BundledProductsDetail(FinanceDetail financeDetail, Tab etihadTab, boolean recSave)
			throws InterruptedException {
		logger.debug("Entering");

		doClearMessage();
		boolean isMandatory = (!recSave && allowValidation());
		doSetValidation(isMandatory);
		doSetLOVValidation();
		BundledProductsDetail bundledProductsDetail = getBundledProductsDetail();
		doWriteComponentsToBean(bundledProductsDetail, etihadTab);
		if (StringUtils.isBlank(getBundledProductsDetail().getRecordType())) {
			bundledProductsDetail.setVersion(getBundledProductsDetail().getVersion() + 1);
			bundledProductsDetail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
			bundledProductsDetail.setNewRecord(true);
		}
		bundledProductsDetail.setFinReference(financeDetail.getFinScheduleData().getFinanceMain().getFinReference());
		bundledProductsDetail.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		bundledProductsDetail.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		bundledProductsDetail.setUserDetails(getUserWorkspace().getLoggedInUser());
		financeDetail.setBundledProductsDetail(bundledProductsDetail);
		logger.debug("Leaving");
	}

	private boolean allowValidation() {
		return false;
		// return !isReadOnly("BundledProductsDetailDialog_allowValidation");
	}

	private void setMandatoryStyle() {
		logger.debug("Entering");
		if (allowValidation()) {
			this.space_Ref2Email.setSclass("mandatory");
			this.space_BankName.setSclass("mandatory");
			this.space_ChequeNo.setSclass("mandatory");
			this.space_StatusOfCust.setSclass("mandatory");
			this.space_CardType.setSclass("mandatory");
			this.space_ClassType.setSclass("mandatory");
			this.space_MinRepay.setSclass("mandatory");
			this.space_ProfitRate.setSclass("mandatory");
			this.space_ContactNumber.setSclass("mandatory");
			this.space_Ref1PhoneNumber.setSclass("mandatory");
			this.space_Ref2PhoneNumber.setSclass("mandatory");
			this.space_Ref1Email.setSclass("mandatory");
			this.space_Ref2Name.setSclass("mandatory");
			this.space_Ref1Name.setSclass("mandatory");
			this.space_StmtAddress.setSclass("mandatory");
			this.space_StmtEmail.setSclass("mandatory");
			this.space_PhysicalAddress.setSclass("mandatory");
			this.space_CardProduct.setSclass("mandatory");
			this.space_SalesStaff.setSclass("mandatory");
			this.space_EmbossingName.setSclass("mandatory");
			this.chequeAmt.setMandatory(true);
			this.limitRecommended.setMandatory(true);
			this.limitApproved.setMandatory(true);
		} else {
			this.space_Ref2Email.setSclass("");
			this.space_BankName.setSclass("");
			this.space_ChequeNo.setSclass("");
			this.space_StatusOfCust.setSclass("");
			this.space_CardType.setSclass("");
			this.space_ContactNumber.setSclass("");
			this.space_Ref1PhoneNumber.setSclass("");
			this.space_Ref2PhoneNumber.setSclass("");
			this.space_ClassType.setSclass("");
			this.space_MinRepay.setSclass("");
			this.space_ProfitRate.setSclass("");
			this.space_Ref1Email.setSclass("");
			this.space_Ref2Name.setSclass("");
			this.space_Ref1Name.setSclass("");
			this.space_StmtAddress.setSclass("");
			this.space_StmtEmail.setSclass("");
			this.space_PhysicalAddress.setSclass("");
			this.space_CardProduct.setSclass("");
			this.space_SalesStaff.setSclass("");
			this.space_EmbossingName.setSclass("");
			this.chequeAmt.setMandatory(false);
			this.limitRecommended.setMandatory(false);
			this.limitApproved.setMandatory(false);
		}
		logger.debug("Leaving");
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
		MessageUtil.showHelpWindow(event, window_BundledProductsDetailDialog);
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
		doWriteBeanToComponents(this.bundledProductsDetail.getBefImage());
		doReadOnly();
		this.btnCtrl.setInitEdit();
		logger.debug("Leaving");
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aBundledProductsDetail
	 *            BundledProductsDetail
	 */
	public void doWriteBeanToComponents(BundledProductsDetail aBundledProductsDetail) {
		logger.debug("Entering");
		this.finReference.setValue(aBundledProductsDetail.getFinReference());
		this.cardProduct.setValue(aBundledProductsDetail.getCardProduct());
		fillComboBox(this.statusOfCust, aBundledProductsDetail.getStatusOfCust(),
				PennantStaticListUtil.getCustomerStatusTypes(), "");
		fillComboBox(this.cardType, aBundledProductsDetail.getCardType(), PennantStaticListUtil.getCardTypes(), "");
		fillComboBox(this.classType, aBundledProductsDetail.getClassType(), PennantStaticListUtil.getCardClassTypes(),
				"");
		this.salesStaff.setValue(aBundledProductsDetail.getSalesStaff());
		this.embossingName.setValue(aBundledProductsDetail.getEmbossingName());
		this.minRepay.setValue(aBundledProductsDetail.getMinRepay());
		this.billingAcc.setValue(aBundledProductsDetail.getBillingAcc());
		this.stmtAddress.setValue(aBundledProductsDetail.getStmtAddress());
		this.physicalAddress.setValue(aBundledProductsDetail.getPhysicalAddress());
		this.stmtEmail.setValue(aBundledProductsDetail.getStmtEmail());
		String[] contactNumber = PennantApplicationUtil.unFormatPhoneNumber(aBundledProductsDetail.getContactNumber());
		this.contactNumCountryCode.setValue(contactNumber[0]);
		this.contactNumAreaCode.setValue(contactNumber[1]);
		this.contactPhoneNumber.setValue(contactNumber[2]);
		this.ref1Name.setValue(aBundledProductsDetail.getRef1Name());
		String[] Phonenumber1 = PennantApplicationUtil.unFormatPhoneNumber(aBundledProductsDetail.getRef1PhoneNum());
		this.ref1PhoneCountryCode.setValue(Phonenumber1[0]);
		this.ref1PhoneAreaCode.setValue(Phonenumber1[1]);
		this.ref1PhoneNumber.setValue(Phonenumber1[2]);
		this.ref1Email.setValue(aBundledProductsDetail.getRef1Email());
		this.ref2Name.setValue(aBundledProductsDetail.getRef2Name());
		String[] Phonenumber2 = PennantApplicationUtil.unFormatPhoneNumber(aBundledProductsDetail.getRef2PhoneNum());
		this.ref2PhoneCountryCode.setValue(Phonenumber2[0]);
		this.ref2PhoneAreaCode.setValue(Phonenumber2[1]);
		this.ref2PhoneNumber.setValue(Phonenumber2[2]);
		this.ref2Email.setValue(aBundledProductsDetail.getRef2Email());
		this.bankName.setValue(aBundledProductsDetail.getBankName());
		this.chequeNo.setValue(aBundledProductsDetail.getChequeNo());
		this.chequeAmt.setValue(PennantAppUtil.formateAmount(aBundledProductsDetail.getChequeAmt(), ccyFormatter));
		this.limitRecommended.setValue(PennantAppUtil.formateAmount(aBundledProductsDetail.getLimitRecommended(),
				ccyFormatter));
		this.limitApproved.setValue(PennantAppUtil.formateAmount(aBundledProductsDetail.getLimitApproved(),
				ccyFormatter));
		this.profitRate.setValue(aBundledProductsDetail.getProfitRate());
		this.crossSellCard.setChecked(aBundledProductsDetail.isCrossSellCard());
		this.urgentIssuance.setChecked(aBundledProductsDetail.isUrgentIssuance());

		this.recordStatus.setValue(aBundledProductsDetail.getRecordStatus());
		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aBundledProductsDetail
	 * @throws InterruptedException
	 */
	public void doWriteComponentsToBean(BundledProductsDetail aBundledProductsDetail, Tab etihadTab)
			throws InterruptedException {
		logger.debug("Entering");

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		try {
			aBundledProductsDetail.setFinReference(this.finReference.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aBundledProductsDetail.setCardProduct(this.cardProduct.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if (StringUtils.equals(getComboboxValue(this.statusOfCust), "#")) {
				aBundledProductsDetail.setStatusOfCust(null);
			} else {
				aBundledProductsDetail.setStatusOfCust(getComboboxValue(this.statusOfCust));
			}

		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if (StringUtils.equals(getComboboxValue(this.cardType), "#")) {
				aBundledProductsDetail.setCardType(null);
			} else {
				aBundledProductsDetail.setCardType(getComboboxValue(this.cardType));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if (StringUtils.equals(getComboboxValue(this.classType), "#")) {
				aBundledProductsDetail.setClassType(null);
			} else {
				aBundledProductsDetail.setClassType(getComboboxValue(this.classType));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aBundledProductsDetail.setSalesStaff(this.salesStaff.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aBundledProductsDetail.setEmbossingName(this.embossingName.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aBundledProductsDetail.setMinRepay(this.minRepay.intValue() == 0 ? BigDecimal.ZERO : new BigDecimal(
					PennantApplicationUtil.formatRate(this.minRepay.getValue().doubleValue(), 2)));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			this.billingAcc.validateValue();
			aBundledProductsDetail.setBillingAcc(PennantApplicationUtil.unFormatAccountNumber(this.billingAcc
					.getValue()));

		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aBundledProductsDetail.setStmtAddress(this.stmtAddress.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aBundledProductsDetail.setPhysicalAddress(this.physicalAddress.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aBundledProductsDetail.setStmtEmail(this.stmtEmail.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aBundledProductsDetail.setContactNumber(PennantApplicationUtil.formatPhoneNumber(
					this.contactNumCountryCode.getValue(), this.contactNumAreaCode.getValue(),
					this.contactPhoneNumber.getValue()));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aBundledProductsDetail.setBankName(this.bankName.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aBundledProductsDetail.setChequeNo(this.chequeNo.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aBundledProductsDetail.setChequeAmt(PennantAppUtil.unFormateAmount(this.chequeAmt.getValidateValue(),
					ccyFormatter));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aBundledProductsDetail.setLimitRecommended(PennantAppUtil.unFormateAmount(
					this.limitRecommended.getValidateValue(), ccyFormatter));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aBundledProductsDetail.setLimitApproved(PennantAppUtil.unFormateAmount(
					this.limitApproved.getValidateValue(), ccyFormatter));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aBundledProductsDetail.setProfitRate(this.profitRate.getValue() == null ? BigDecimal.ZERO : this.profitRate
					.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aBundledProductsDetail.setCrossSellCard(this.crossSellCard.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aBundledProductsDetail.setUrgentIssuance(this.urgentIssuance.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aBundledProductsDetail.setRef1Name(this.ref1Name.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aBundledProductsDetail.setRef1PhoneNum(PennantApplicationUtil.formatPhoneNumber(
					this.ref1PhoneCountryCode.getValue(), this.ref1PhoneAreaCode.getValue(),
					this.ref1PhoneNumber.getValue()));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aBundledProductsDetail.setRef1Email(this.ref1Email.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aBundledProductsDetail.setRef2Name(this.ref2Name.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aBundledProductsDetail.setRef2PhoneNum(PennantApplicationUtil.formatPhoneNumber(
					this.ref2PhoneCountryCode.getValue(), this.ref2PhoneAreaCode.getValue(),
					this.ref2PhoneNumber.getValue()));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aBundledProductsDetail.setRef2Email(this.ref2Email.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		showErrorDetails(wve, etihadTab);
		aBundledProductsDetail.setRecordStatus(this.recordStatus.getValue());
		logger.debug("Leaving");
	}

	/**
	 * Writes the showErrorDetails method for .<br>
	 * displaying exceptions if occured
	 */
	private void showErrorDetails(ArrayList<WrongValueException> wve, Tab etihadTab) {
		logger.debug("Entering");
		doRemoveValidation();
		doRemoveLOVValidation();
		if (!recSave) {
			if (wve.size() > 0) {
				logger.debug("Throwing occured Errors By using WrongValueException");
				if (parentTab != null) {
					parentTab.setSelected(true);
				}
				if (etihadTab != null) {
					etihadTab.setSelected(true);
				}
				WrongValueException[] wvea = new WrongValueException[wve.size()];
				for (int i = 0; i < wve.size(); i++) {
					wvea[i] = wve.get(i);
				}
				throw new WrongValuesException(wvea);
			}
		}
		logger.debug("Leaving");
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the readOnly mode accordingly.
	 * 
	 * @param aBundledProductsDetail
	 * @throws InterruptedException
	 */
	public void doShowDialog(BundledProductsDetail aBundledProductsDetail) throws InterruptedException {
		logger.debug("Entering");
		getBorderLayoutHeight();
		// append finance basic details
		appendFinBasicDetails();
		// if aAgreementFieldDetails == null then we opened the Dialog without
		// arguments for a given entity, so we get a new Object().
		if (aBundledProductsDetail == null) {
			/** !!! DO NOT BREAK THE TIERS !!! */
			aBundledProductsDetail = new BundledProductsDetail();
			aBundledProductsDetail.setNewRecord(true);
			setBundledProductsDetail(aBundledProductsDetail);
		} 

		// set ReadOnly mode accordingly if the object is new or not.
		if (aBundledProductsDetail.isNew()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.cardProduct.focus();
		} else {
			this.cardProduct.focus();
			if (isNewFinance()) {
				doEdit();
			} else if (isWorkFlowEnabled()) {
				this.btnNotes.setVisible(true);
				doEdit();
			} else {
				this.btnCtrl.setInitEdit();
				doReadOnly();
				btnCancel.setVisible(false);
			}
		}

		try {
			setMandatoryStyle();
			// fill the components with the data
			doWriteBeanToComponents(aBundledProductsDetail);
			if (parent != null) {
				this.toolbar.setVisible(false);
				this.groupboxWf.setVisible(false);
				this.gb_basicDetails.setHeight("100%");
				int visibleRows = grid_basicDetails.getRows().getVisibleItemCount()
						+ grid_Reference1.getRows().getVisibleItemCount()
						+ grid_Reference2.getRows().getVisibleItemCount();
				this.window_BundledProductsDetailDialog.setHeight(visibleRows * 22 + 350 + "px");
				parent.appendChild(this.window_BundledProductsDetailDialog);
			} else {
				setDialog(DialogType.EMBEDDED);
			}
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving");
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation(boolean isMandatory) {
		logger.debug("Entering");
		setValidationOn(true);

		if (!this.cardProduct.isReadonly()) {
			this.cardProduct.setConstraint(new PTStringValidator(Labels
					.getLabel("label_BundledProductsDetailDialog_CardProduct.value"), null, isMandatory));
		}
		if (!this.statusOfCust.isDisabled()) {
			this.statusOfCust.setConstraint(new PTStringValidator(Labels
					.getLabel("label_BundledProductsDetailDialog_StatusOfCust.value"), null, isMandatory));
		}
		if (!this.cardType.isDisabled()) {
			this.cardType.setConstraint(new PTStringValidator(Labels
					.getLabel("label_BundledProductsDetailDialog_CardType.value"), null, isMandatory));
		}
		if (!this.classType.isDisabled()) {
			this.classType.setConstraint(new PTStringValidator(Labels
					.getLabel("label_BundledProductsDetailDialog_ClassType.value"), null, isMandatory));
		}
		if (!this.salesStaff.isReadonly()) {
			this.salesStaff.setConstraint(new PTStringValidator(Labels
					.getLabel("label_BundledProductsDetailDialog_SalesStaff.value"), null, isMandatory));
		}
		if (!this.embossingName.isReadonly()) {
			this.embossingName.setConstraint(new PTStringValidator(Labels
					.getLabel("label_BundledProductsDetailDialog_EmbossingName.value"),
					PennantRegularExpressions.REGEX_CUST_NAME, false));
		}
		if (!this.billingAcc.isReadonly()) {
			this.billingAcc.setConstraint(new PTStringValidator(Labels
					.getLabel("label_BundledProductsDetailDialog_BillingAcc.value"), null, isMandatory));
		}
		if (!this.stmtAddress.isReadonly()) {
			this.stmtAddress.setConstraint(new PTStringValidator(Labels
					.getLabel("label_BundledProductsDetailDialog_StmtAddress.value"),
					PennantRegularExpressions.REGEX_ADDRESS, isMandatory));
		}
		if (!this.physicalAddress.isReadonly()) {
			this.physicalAddress.setConstraint(new PTStringValidator(Labels
					.getLabel("label_BundledProductsDetailDialog_PhysicalAddress.value"),
					PennantRegularExpressions.REGEX_ADDRESS, isMandatory));
		}
		if (!this.stmtEmail.isReadonly()) {
			this.stmtEmail.setConstraint(new PTEmailValidator(Labels
					.getLabel("label_BundledProductsDetailDialog_StmtEmail.value"), isMandatory));
		}
		if (!this.contactNumCountryCode.isReadonly()) {
			this.contactNumCountryCode.setConstraint(new PTPhoneNumberValidator(Labels
					.getLabel("label_BundledProductsDetailDialog_ContactNumCountryCode.value"), isMandatory, 1));
		}
		if (!this.contactNumAreaCode.isReadonly()) {
			this.contactNumAreaCode.setConstraint(new PTPhoneNumberValidator(Labels
					.getLabel("label_BundledProductsDetailDialog_ContactNumAreaCode.value"), isMandatory, 2));
		}
		if (!this.contactPhoneNumber.isReadonly()) {
			this.contactPhoneNumber.setConstraint(new PTPhoneNumberValidator(Labels
					.getLabel("label_BundledProductsDetailDialog_ContactPhoneNumber.value"), isMandatory, 3));
		}
		if (!this.ref1Name.isReadonly()) {
			this.ref1Name.setConstraint(new PTStringValidator(Labels
					.getLabel("label_BundledProductsDetailDialog_Ref1Name.value"),
					PennantRegularExpressions.REGEX_CUST_NAME, isMandatory));
		}
		if (!this.ref1PhoneCountryCode.isReadonly()) {
			this.ref1PhoneCountryCode.setConstraint(new PTPhoneNumberValidator(Labels
					.getLabel("label_BundledProductsDetailDialog_Ref1PhCountryCode.value"), isMandatory, 1));
		}
		if (!this.ref1PhoneAreaCode.isReadonly()) {
			this.ref1PhoneAreaCode.setConstraint(new PTPhoneNumberValidator(Labels
					.getLabel("label_BundledProductsDetailDialog_Ref1PhAreaCode.value"), isMandatory, 2));
		}
		if (!this.ref1PhoneNumber.isReadonly()) {
			this.ref1PhoneNumber.setConstraint(new PTPhoneNumberValidator(Labels
					.getLabel("label_BundledProductsDetailDialog_Ref1PhoneNumber.value"), isMandatory, 3));
		}
		if (!this.ref1Email.isReadonly()) {
			this.ref1Email.setConstraint(new PTEmailValidator(Labels
					.getLabel("label_BundledProductsDetailDialog_Ref1Email.value"), isMandatory));
		}
		if (!this.ref2Name.isReadonly()) {
			this.ref2Name.setConstraint(new PTStringValidator(Labels
					.getLabel("label_BundledProductsDetailDialog_Ref2Name.value"),
					PennantRegularExpressions.REGEX_CUST_NAME, isMandatory));
		}
		if (!this.ref2PhoneCountryCode.isReadonly()) {
			this.ref2PhoneCountryCode.setConstraint(new PTPhoneNumberValidator(Labels
					.getLabel("label_BundledProductsDetailDialog_Ref2PhCountryCode.value"), isMandatory, 1));
		}
		if (!this.ref2PhoneAreaCode.isReadonly()) {
			this.ref2PhoneAreaCode.setConstraint(new PTPhoneNumberValidator(Labels
					.getLabel("label_BundledProductsDetailDialog_Ref2PhAreaCode.value"), isMandatory, 2));
		}
		if (!this.ref2PhoneNumber.isReadonly()) {
			this.ref2PhoneNumber.setConstraint(new PTPhoneNumberValidator(Labels
					.getLabel("label_BundledProductsDetailDialog_Ref2PhoneNumber.value"), isMandatory, 3));
		}
		if (!this.ref2Email.isReadonly()) {
			this.ref2Email.setConstraint(new PTEmailValidator(Labels
					.getLabel("label_BundledProductsDetailDialog_Ref2Email.value"), isMandatory));
		}
		if (!this.bankName.isReadonly()) {
			this.bankName.setConstraint(new PTStringValidator(Labels
					.getLabel("label_BundledProductsDetailDialog_BankName.value"), null, isMandatory));
		}
		if (!this.chequeNo.isReadonly()) {
			this.chequeNo.setConstraint(new PTStringValidator(Labels
					.getLabel("label_BundledProductsDetailDialog_ChequeNo.value"), null, isMandatory));
		}
		if (!this.chequeAmt.isReadonly()) {
			this.chequeAmt.setConstraint(new PTDecimalValidator(Labels
					.getLabel("label_BundledProductsDetailDialog_ChequeAmt.value"), ccyFormatter, isMandatory, false));
		}
		if (!this.limitRecommended.isReadonly()) {
			this.limitRecommended.setConstraint(new PTDecimalValidator(Labels
					.getLabel("label_BundledProductsDetailDialog_LimitRecommended.value"), ccyFormatter, isMandatory,
					false));
		}
		if (!this.limitApproved.isReadonly()) {
			this.limitApproved.setConstraint(new PTDecimalValidator(Labels
					.getLabel("label_BundledProductsDetailDialog_LimitApproved.value"), ccyFormatter, isMandatory,
					false));
		}
		if (!this.profitRate.isReadonly()) {
			this.profitRate.setConstraint(new RateValidator(13, 9, Labels
					.getLabel("label_BundledProductsDetailDialog_ProfitRate.value")));
		}
		logger.debug("Leaving");
	}

	/**
	 * Disables the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering");
		setValidationOn(false);
		this.finReference.setConstraint("");
		this.cardProduct.setConstraint("");
		this.statusOfCust.setConstraint("");
		this.cardType.setConstraint("");
		this.classType.setConstraint("");
		this.chequeAmt.setConstraint("");
		this.limitRecommended.setConstraint("");
		this.limitApproved.setConstraint("");
		this.salesStaff.setConstraint("");
		this.embossingName.setConstraint("");
		this.stmtEmail.setConstraint("");
		this.physicalAddress.setConstraint("");
		this.stmtAddress.setConstraint("");
		this.ref1Name.setConstraint("");
		this.ref1Email.setConstraint("");
		this.ref2Name.setConstraint("");
		this.ref2Email.setConstraint("");
		this.bankName.setConstraint("");
		this.chequeNo.setConstraint("");
		this.minRepay.setConstraint("");
		this.profitRate.setConstraint("");
		this.ref1PhoneCountryCode.setConstraint("");
		this.ref1PhoneAreaCode.setConstraint("");
		this.ref1PhoneNumber.setConstraint("");
		this.ref2PhoneCountryCode.setConstraint("");
		this.ref2PhoneAreaCode.setConstraint("");
		this.ref2PhoneNumber.setConstraint("");
		this.contactNumCountryCode.setConstraint("");
		this.contactNumAreaCode.setConstraint("");
		this.contactPhoneNumber.setConstraint("");
		logger.debug("Leaving");
	}

	/**
	 * Method for Set the constraints to LOV fields
	 */
	private void doSetLOVValidation() {
		logger.debug("Entering");

		logger.debug("Leaving");
	}

	/**
	 * Method for remove constraints to LOV fields
	 */
	private void doRemoveLOVValidation() {
		logger.debug("Entering");
		logger.debug("Leaving");
	}

	/**
	 * Method for clear the error Messages
	 */
	@Override
	protected void doClearMessage() {
		logger.debug("Entering");
		this.finReference.setErrorMessage("");
		this.cardProduct.setErrorMessage("");
		this.chequeAmt.setErrorMessage("");
		this.limitRecommended.setErrorMessage("");
		this.limitApproved.setErrorMessage("");
		this.salesStaff.setErrorMessage("");
		this.embossingName.setErrorMessage("");
		this.stmtEmail.setErrorMessage("");
		this.physicalAddress.setErrorMessage("");
		this.stmtAddress.setErrorMessage("");
		this.ref1Name.setErrorMessage("");
		this.ref1Email.setErrorMessage("");
		this.ref2Name.setErrorMessage("");
		this.ref2Email.setErrorMessage("");
		this.bankName.setErrorMessage("");
		this.chequeNo.setErrorMessage("");
		this.statusOfCust.setErrorMessage("");
		this.cardType.setErrorMessage("");
		this.classType.setErrorMessage("");
		this.minRepay.setErrorMessage("");
		this.billingAcc.setErrorMessage("");
		this.profitRate.setErrorMessage("");
		this.ref1PhoneCountryCode.setErrorMessage("");
		this.ref1PhoneAreaCode.setErrorMessage("");
		this.ref1PhoneNumber.setErrorMessage("");
		this.ref2PhoneCountryCode.setErrorMessage("");
		this.ref2PhoneAreaCode.setErrorMessage("");
		this.ref2PhoneNumber.setErrorMessage("");
		this.contactNumCountryCode.setErrorMessage("");
		this.contactNumAreaCode.setErrorMessage("");
		this.contactPhoneNumber.setErrorMessage("");
		logger.debug("Leaving");
	}

	private void refreshList() {
		logger.debug("Entering");

		logger.debug("Leaving");
	}

	// CRUD operations

	/**
	 * Deletes a BundledProductsDetail object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug("Entering");
		final BundledProductsDetail aBundledProductsDetail = new BundledProductsDetail();
		BeanUtils.copyProperties(getBundledProductsDetail(), aBundledProductsDetail);
		String tranType = PennantConstants.TRAN_WF;

		// Show a confirm box
		final String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record") + "\n\n --> "
				+ aBundledProductsDetail.getId();
		if (MessageUtil.confirm(msg) == MessageUtil.YES) {
			if (StringUtils.isBlank(aBundledProductsDetail.getRecordType())) {
				aBundledProductsDetail.setVersion(aBundledProductsDetail.getVersion() + 1);
				aBundledProductsDetail.setRecordType(PennantConstants.RECORD_TYPE_DEL);

				if (isWorkFlowEnabled()) {
					aBundledProductsDetail.setNewRecord(true);
					tranType = PennantConstants.TRAN_WF;
				} else {
					tranType = PennantConstants.TRAN_DEL;
				}
			}

			try {
				if (doProcess(aBundledProductsDetail, tranType)) {
					refreshList();
					closeDialog();
				}
			} catch (DataAccessException e) {
				logger.error("Exception: ", e);
				showMessage(e);
			}
		}
		logger.debug("Leaving");
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug("Entering");

		if (getBundledProductsDetail().isNewRecord()) {
			this.btnCancel.setVisible(false);
		} else {
			this.btnCancel.setVisible(true);
		}

		this.finReference.setReadonly(true);
		this.cardProduct.setReadonly(isReadOnly("BundledProductsDetailDialog_cardProduct"));
		this.cardType.setDisabled(isReadOnly("BundledProductsDetailDialog_cardType"));
		this.classType.setDisabled(isReadOnly("BundledProductsDetailDialog_classType"));
		this.statusOfCust.setDisabled(isReadOnly("BundledProductsDetailDialog_statusOfCust"));
		this.salesStaff.setReadonly(isReadOnly("BundledProductsDetailDialog_salesStaff"));
		this.embossingName.setReadonly(isReadOnly("BundledProductsDetailDialog_embossingName"));
		this.stmtAddress.setReadonly(isReadOnly("BundledProductsDetailDialog_stmtAddress"));
		this.physicalAddress.setReadonly(isReadOnly("BundledProductsDetailDialog_physicalAddress"));
		this.stmtEmail.setReadonly(isReadOnly("BundledProductsDetailDialog_stmtEmail"));
		this.contactPhoneNumber.setReadonly(isReadOnly("BundledProductsDetailDialog_contactPhoneNumber"));
		this.bankName.setReadonly(isReadOnly("BundledProductsDetailDialog_bankName"));
		this.chequeNo.setReadonly(isReadOnly("BundledProductsDetailDialog_chequeNo"));
		this.chequeAmt.setReadonly(isReadOnly("BundledProductsDetailDialog_chequeAmt"));
		this.limitRecommended.setReadonly(isReadOnly("BundledProductsDetailDialog_limitRecommended"));
		this.limitApproved.setReadonly(isReadOnly("BundledProductsDetailDialog_limitApproved"));
		this.ref1Name.setReadonly(isReadOnly("BundledProductsDetailDialog_ref1Name"));
		this.ref1Email.setReadonly(isReadOnly("BundledProductsDetailDialog_ref1Email"));
		this.ref2Name.setReadonly(isReadOnly("BundledProductsDetailDialog_ref2Name"));
		this.ref2Email.setReadonly(isReadOnly("BundledProductsDetailDialog_ref2Email"));
		this.minRepay.setDisabled(isReadOnly("BundledProductsDetailDialog_minRepay"));
		this.billingAcc.setReadonly(isReadOnly("BundledProductsDetailDialog_billingAcc"));
		this.profitRate.setReadonly(isReadOnly("BundledProductsDetailDialog_profitRate"));
		this.crossSellCard.setDisabled(isReadOnly("BundledProductsDetailDialog_crossSellCard"));
		this.urgentIssuance.setDisabled(isReadOnly("BundledProductsDetailDialog_urgentIssuance"));
		this.ref1PhoneCountryCode.setReadonly(isReadOnly("BundledProductsDetailDialog_ref1PhoneCountryCode"));
		this.ref1PhoneAreaCode.setReadonly(isReadOnly("BundledProductsDetailDialog_ref1PhoneAreaCode"));
		this.ref1PhoneNumber.setReadonly(isReadOnly("BundledProductsDetailDialog_ref1PhoneNumber"));
		this.ref2PhoneCountryCode.setReadonly(isReadOnly("BundledProductsDetailDialog_ref2PhoneCountryCode"));
		this.ref2PhoneAreaCode.setReadonly(isReadOnly("BundledProductsDetailDialog_ref2PhoneAreaCode"));
		this.ref2PhoneNumber.setReadonly(isReadOnly("BundledProductsDetailDialog_ref2PhoneNumber"));
		this.contactNumCountryCode.setReadonly(isReadOnly("BundledProductsDetailDialog_contactNumCountryCode"));
		this.contactNumAreaCode.setReadonly(isReadOnly("BundledProductsDetailDialog_contactNumAreaCode"));

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}

			if (this.bundledProductsDetail.isNewRecord()) {
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

	public boolean isReadOnly(String componentName) {
		return false;
	}

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly() {
		logger.debug("Entering");
		this.finReference.setReadonly(true);
		this.cardProduct.setReadonly(true);
		this.chequeAmt.setReadonly(true);
		this.limitRecommended.setReadonly(true);
		this.limitApproved.setReadonly(true);
		this.salesStaff.setReadonly(true);
		this.embossingName.setReadonly(true);
		this.stmtEmail.setReadonly(true);
		this.physicalAddress.setReadonly(true);
		this.stmtAddress.setReadonly(true);
		this.ref1Name.setReadonly(true);
		this.ref1Email.setReadonly(true);
		this.ref2Name.setReadonly(true);
		this.ref2Email.setReadonly(true);
		this.bankName.setReadonly(true);
		this.chequeNo.setReadonly(true);
		this.statusOfCust.setDisabled(true);
		this.cardType.setDisabled(true);
		this.classType.setDisabled(true);
		this.minRepay.setDisabled(true);
		this.billingAcc.setReadonly(true);
		this.profitRate.setReadonly(true);
		this.crossSellCard.setDisabled(true);
		this.urgentIssuance.setDisabled(true);
		this.ref1PhoneCountryCode.setDisabled(true);
		this.ref1PhoneAreaCode.setDisabled(true);
		this.ref1PhoneNumber.setDisabled(true);
		this.ref2PhoneCountryCode.setDisabled(true);
		this.ref2PhoneAreaCode.setDisabled(true);
		this.ref2PhoneNumber.setDisabled(true);
		this.contactNumCountryCode.setDisabled(true);
		this.contactNumAreaCode.setDisabled(true);
		this.contactPhoneNumber.setDisabled(true);

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
		this.finReference.setText("");
		this.cardProduct.setValue("");
		this.chequeAmt.setValue("");
		this.limitRecommended.setValue("");
		this.limitApproved.setValue("");
		this.salesStaff.setValue("");
		this.embossingName.setValue("");
		this.stmtEmail.setValue("");
		this.physicalAddress.setValue("");
		this.stmtAddress.setValue("");
		this.ref1Name.setValue("");
		this.ref1Email.setValue("");
		this.ref2Name.setValue("");
		this.ref2Email.setValue("");
		this.bankName.setValue("");
		this.chequeNo.setValue("");
		this.statusOfCust.setValue("");
		this.cardType.setValue("");
		this.classType.setValue("");
		this.minRepay.setValue("");
		this.profitRate.setValue("");
		this.ref1PhoneCountryCode.setValue("");
		this.ref1PhoneAreaCode.setValue("");
		this.ref1PhoneNumber.setValue("");
		this.ref2PhoneCountryCode.setValue("");
		this.ref2PhoneAreaCode.setValue("");
		this.ref2PhoneNumber.setValue("");
		this.contactNumCountryCode.setValue("");
		this.contactNumAreaCode.setValue("");
		this.contactPhoneNumber.setValue("");
		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug("Entering");
		final BundledProductsDetail aBundledProductsDetail = new BundledProductsDetail();
		BeanUtils.copyProperties(getBundledProductsDetail(), aBundledProductsDetail);
		boolean isNew = false;

		// force validation, if on, than execute by component.getValue()
		doSetValidation(true);
		doSetLOVValidation();
		// fill the BundledProductsDetail object with the components data
		doWriteComponentsToBean(aBundledProductsDetail, null);

		// Write the additional validations as per below example
		// get the selected branch object from the listBox
		// Do data level validations here

		isNew = aBundledProductsDetail.isNew();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aBundledProductsDetail.getRecordType())) {
				aBundledProductsDetail.setVersion(aBundledProductsDetail.getVersion() + 1);
				if (isNew) {
					aBundledProductsDetail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aBundledProductsDetail.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aBundledProductsDetail.setNewRecord(true);
				}
			}
		} else {
			aBundledProductsDetail.setVersion(aBundledProductsDetail.getVersion() + 1);
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}

		// save it to database
		try {
			if (doProcess(aBundledProductsDetail, tranType)) {
				refreshList();
				closeDialog();
			}
		} catch (final DataAccessException e) {
			logger.error("Exception: ", e);
			showMessage(e);
		}
		logger.debug("Leaving");
	}

	/**
	 * Set the workFlow Details List to Object
	 * 
	 * @param aBundledProductsDetail
	 *            (BundledProductsDetail)
	 * @param tranType
	 *            (String)
	 * 
	 * @return boolean
	 */
	private boolean doProcess(BundledProductsDetail aBundledProductsDetail, String tranType) {
		logger.debug("Entering");
		boolean processCompleted = false;
		AuditHeader auditHeader = null;
		String nextRoleCode = "";

		aBundledProductsDetail.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		aBundledProductsDetail.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aBundledProductsDetail.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			String nextTaskId = "";
			aBundledProductsDetail.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aBundledProductsDetail.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getNextTaskIds(taskId, aBundledProductsDetail);
				}

				if (isNotesMandatory(taskId, aBundledProductsDetail)) {
					if (!notesEntered) {
						MessageUtil.showError(Labels.getLabel("Notes_NotEmpty"));
						return false;
					}
				}
			}

			if (StringUtils.isBlank(nextTaskId)) {
				nextRoleCode = getFirstTaskOwner();
			} else {
				String[] nextTasks = nextTaskId.split(";");

				if (nextTasks != null && nextTasks.length > 0) {
					for (int i = 0; i < nextTasks.length; i++) {

						if (nextRoleCode.length() > 1) {
							nextRoleCode = nextRoleCode.concat(",");
						}
						nextRoleCode = getTaskOwner(nextTasks[i]);
					}
				} else {
					nextRoleCode = getTaskOwner(nextTaskId);
				}
			}

			aBundledProductsDetail.setTaskId(taskId);
			aBundledProductsDetail.setNextTaskId(nextTaskId);
			aBundledProductsDetail.setRoleCode(getRole());
			aBundledProductsDetail.setNextRoleCode(nextRoleCode);

			auditHeader = getAuditHeader(aBundledProductsDetail, tranType);

			String operationRefs = getServiceOperations(taskId, aBundledProductsDetail);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader, null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(aBundledProductsDetail, PennantConstants.TRAN_WF);
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		} else {
			auditHeader = getAuditHeader(aBundledProductsDetail, tranType);
			processCompleted = doSaveProcess(auditHeader, null);
		}
		logger.debug("return value :" + processCompleted);
		logger.debug("Leaving");
		return processCompleted;
	}

	/**
	 * Get the result after processing DataBase Operations
	 * 
	 * @param auditHeader
	 *            (AuditHeader)
	 * @param method
	 *            (String)
	 * 
	 * @return boolean
	 */
	private boolean doSaveProcess(AuditHeader auditHeader, String method) {
		logger.debug("Entering");
		boolean processCompleted = false;
		int retValue = PennantConstants.porcessOVERIDE;
		boolean deleteNotes = false;

		BundledProductsDetail aBundledProductsDetail = (BundledProductsDetail) auditHeader.getAuditDetail()
				.getModelData();

		try {
			while (retValue == PennantConstants.porcessOVERIDE) {
				if (StringUtils.isBlank(method)) {
					if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {
						auditHeader = getBundledProductsDetailService().delete(auditHeader);
						deleteNotes = true;
					} else {
						auditHeader = getBundledProductsDetailService().saveOrUpdate(auditHeader);
					}

				} else {
					if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
						auditHeader = getBundledProductsDetailService().doApprove(auditHeader);

						if (aBundledProductsDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
							deleteNotes = true;
						}

					} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
						auditHeader = getBundledProductsDetailService().doReject(auditHeader);
						if (aBundledProductsDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
							deleteNotes = true;
						}

					} else {
						auditHeader.setErrorDetails(new ErrorDetail(PennantConstants.ERR_9999, Labels
								.getLabel("InvalidWorkFlowMethod"), null));
						retValue = ErrorControl.showErrorControl(this.window_BundledProductsDetailDialog, auditHeader);
						return processCompleted;
					}
				}

				auditHeader = ErrorControl.showErrorDetails(this.window_BundledProductsDetailDialog, auditHeader);
				retValue = auditHeader.getProcessStatus();

				if (retValue == PennantConstants.porcessCONTINUE) {
					processCompleted = true;

					if (deleteNotes) {
						deleteNotes(getNotes(this.bundledProductsDetail), true);
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
			logger.error("Exception: ", e);
		}
		logger.debug("return Value:" + processCompleted);
		logger.debug("Leaving");
		return processCompleted;
	}

	// WorkFlow Components

	/**
	 * Get Audit Header Details
	 * 
	 * @param aBundledProductsDetail
	 * @param tranType
	 * @return AuditHeader
	 */
	private AuditHeader getAuditHeader(BundledProductsDetail aBundledProductsDetail, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aBundledProductsDetail.getBefImage(),
				aBundledProductsDetail);
		return new AuditHeader(String.valueOf(aBundledProductsDetail.getId()), null, null, null, auditDetail,
				aBundledProductsDetail.getUserDetails(), getOverideMap());
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
			auditHeader.setErrorDetails(new ErrorDetail(PennantConstants.ERR_UNDEF, e.getMessage(), null));
			ErrorControl.showErrorControl(this.window_BundledProductsDetailDialog, auditHeader);
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
		doShowNotes(this.bundledProductsDetail);
	}
	
	
	@Override
	protected String getReference() {
		return String.valueOf(this.bundledProductsDetail.getId());
	}


	/**
	 * This method is for append finance basic details to respective parent tabs
	 */
	private void appendFinBasicDetails() {
		try {
			final HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("parentCtrl", this);
			map.put("finHeaderList", this.finBasicDetailList);
			Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/FinBasicDetails.zul", this.finBasicdetails,
					map);
		} catch (Exception e) {
			logger.debug(e);
		}

	}

	public void doSetLabels(ArrayList<Object> finHeaderList) {
		getFinBasicDetailsCtrl().doWriteBeanToComponents(finHeaderList);
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

	public BundledProductsDetail getBundledProductsDetail() {
		return this.bundledProductsDetail;
	}

	public void setBundledProductsDetail(BundledProductsDetail bundledProductsDetail) {
		this.bundledProductsDetail = bundledProductsDetail;
	}

	public void setBundledProductsDetailService(BundledProductsDetailService bundledProductsDetailService) {
		this.bundledProductsDetailService = bundledProductsDetailService;
	}

	public BundledProductsDetailService getBundledProductsDetailService() {
		return this.bundledProductsDetailService;
	}

	public void setOverideMap(HashMap<String, ArrayList<ErrorDetail>> overideMap) {
		this.overideMap = overideMap;
	}

	public HashMap<String, ArrayList<ErrorDetail>> getOverideMap() {
		return overideMap;
	}

	public boolean isNewFinance() {
		return newFinance;
	}

	public void setNewFinance(boolean newFinance) {
		this.newFinance = newFinance;
	}

	public FinanceDetail getFinancedetail() {
		return financedetail;
	}

	public void setFinancedetail(FinanceDetail financedetail) {
		this.financedetail = financedetail;
	}

	public Object getFinanceMainDialogCtrl() {
		return financeMainDialogCtrl;
	}

	public void setFinanceMainDialogCtrl(Object financeMainDialogCtrl) {
		this.financeMainDialogCtrl = financeMainDialogCtrl;
	}

	public FinBasicDetailsCtrl getFinBasicDetailsCtrl() {
		return finBasicDetailsCtrl;
	}

	public void setFinBasicDetailsCtrl(FinBasicDetailsCtrl finBasicDetailsCtrl) {
		this.finBasicDetailsCtrl = finBasicDetailsCtrl;
	}

}
