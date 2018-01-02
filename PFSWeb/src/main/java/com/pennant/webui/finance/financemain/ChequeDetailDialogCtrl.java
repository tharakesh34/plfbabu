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
 * FileName    		:  ChequeDetailDialogCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  27-11-2017    														*
 *                                                                  						*
 * Modified Date    :  27-11-2017    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 27-11-2017       PENNANT	                 0.1                                            * 
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
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.ExtendedCombobox;
import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.DateUtility;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.bmtmasters.BankBranch;
import com.pennant.backend.model.finance.ChequeDetail;
import com.pennant.backend.model.finance.ChequeHeader;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.service.applicationmaster.BankDetailService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.util.PennantAppUtil;
import com.pennant.util.Constraint.PTDecimalValidator;
import com.pennant.util.Constraint.PTNumberValidator;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.util.Constraint.StaticListValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the /WEB-INF/pages/pdc/ChequeDetail/chequeDetailDialog.zul file. <br>
 */
public class ChequeDetailDialogCtrl extends GFCBaseCtrl<ChequeHeader> {

	private static final long			serialVersionUID		= 1L;
	private static final Logger			logger					= Logger.getLogger(ChequeDetailDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the zul-file
	 * are getting by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window				window_ChequeDetailDialog;
	protected Combobox				chequeType;
	protected Intbox				noOfCheques;
	protected Decimalbox			amount;
	protected Decimalbox			amountCD;
	protected ExtendedCombobox		bankBranchID;
	protected Textbox				bank;
	protected Textbox				city;
	protected Label					cityName;
	protected Textbox				micr;
	protected Textbox				ifsc;
	protected Textbox				accNumber;
	protected Intbox				chequeSerialNo;
	protected Intbox				noOfChequesCalc;
	protected Groupbox				finBasicdetails;
	protected Listbox				listBoxChequeDetail;
	protected Button				btnGen;
	private ChequeDetail			chequeDetail;
	private boolean					fromLoan				= false;
	private ChequeHeader			chequeHeader;
	private FinBasicDetailsCtrl		finBasicDetailsCtrl;
	private Object					financeMainDialogCtrl	= null;
	private final List<ValueLabel>	chequeTypeList			= PennantStaticListUtil.getChequeTypes();
	private FinanceDetail			financeDetail;
	private List<ChequeDetail>		chequeDetailList;
	private Tab						parenttab				= null;

	private int						accNoLength;

	private BankDetailService		bankDetailService;

	/**
	 * default constructor.<br>
	 */
	public ChequeDetailDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "MandateDialog";
	}

	@Override
	protected String getReference() {
		StringBuffer referenceBuffer = new StringBuffer(String.valueOf(this.chequeHeader.getHeaderID()));
		return referenceBuffer.toString();
	}

	/**
	 * 
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public void onCreate$window_ChequeDetailDialog(Event event) throws Exception {
		logger.debug(Literal.ENTERING);

		// Set the page level components.
		setPageComponents(window_ChequeDetailDialog);

		try {
			// Get the required arguments.
			// this.chequeDetail = (ChequeDetail) arguments.get("chequeDetail");

			if (arguments.containsKey("chequeHeader")) {
				this.chequeHeader = (ChequeHeader) arguments.get("chequeHeader");
				setChequeHeader(chequeHeader);
			}

			// Store the before image.
			/*
			 * ChequeDetail chequeDetail = new ChequeDetail(); BeanUtils.copyProperties(this.chequeDetail,
			 * chequeDetail); this.chequeDetail.setBefImage(chequeDetail);
			 */

			if (arguments.containsKey("fromLoan")) {
				fromLoan = (Boolean) arguments.get("fromLoan");
			}

			if (arguments.containsKey("roleCode")) {
				setRole((String) arguments.get("roleCode"));
			}

			if (arguments.containsKey("financeDetail")) {
				setFinanceDetail((FinanceDetail) arguments.get("financeDetail"));
				if (getFinanceDetail().getChequeHeader() != null) {
					setChequeHeader(getFinanceDetail().getChequeHeader());
				}
			}

			if (arguments.containsKey("finHeaderList")) {
				appendFinBasicDetails((ArrayList<Object>) arguments.get("finHeaderList"));
			} else {
				appendFinBasicDetails(null);
			}

			if (arguments.containsKey("financeMainDialogCtrl")) {
				setFinanceMainDialogCtrl(arguments.get("financeMainDialogCtrl"));
			}

			if (arguments.containsKey("tab")) {
				parenttab = (Tab) arguments.get("tab");
			}

			if (arguments.containsKey("roleCode")) {
				getUserWorkspace().allocateRoleAuthorities(arguments.get("roleCode").toString(), "ChequeHeaderDialog");
			}

			doSetFieldProperties();
			// doCheckRights();
			doShowDialog(this.financeDetail);
		} catch (Exception e) {
			closeDialog();
			MessageUtil.showError(e);
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug(Literal.ENTERING);

		this.bankBranchID.setModuleName("BankBranch");
		this.bankBranchID.setMandatoryStyle(true);
		this.bankBranchID.setValueColumn("BranchCode");
		this.bankBranchID.setDescColumn("BranchDesc");
		this.bankBranchID.setDisplayStyle(2);
		this.bankBranchID.setValidateColumns(new String[] { "BranchCode" });
		this.chequeType.setSclass(PennantConstants.mandateSclass);
		this.chequeSerialNo.setMaxlength(6);
		this.accNumber.setMaxlength(15);
		this.noOfChequesCalc.setMaxlength(2);
		this.amount.setMaxlength(18);

		setStatusDetails();

		logger.debug(Literal.LEAVING);
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
				this.accNoLength = bankDetailService.getAccNoLengthByCode(details.getBankCode());
			}
		}

		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Set Visible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug(Literal.ENTERING);

		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_ChequeDetailDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_ChequeDetailDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_ChequeDetailDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_ChequeDetailDialog_btnSave"));
		this.btnCancel.setVisible(false);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param chequeDetail
	 * 
	 */
	public void doWriteBeanToComponents(FinanceDetail aFinanceDetail) {
		logger.debug(Literal.ENTERING);

		// this.chequeSerialNo.setValue(aChequeDetail.getChequeSerialNo());
		fillComboBox(this.chequeType, getChequeHeader().getChequeType(), chequeTypeList, "");
		this.noOfCheques.setValue(getChequeHeader().getNoOfCheques());
		this.amount.setValue(getChequeHeader().getTotalAmount());
		// fillComboBox(chequeType, aChequeDetail.getChequeType(),
		// chequeTypeList, "");
		doFillChequeDetails(listBoxChequeDetail, getChequeHeader().getChequeDetailList());
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aChequeDetail
	 * @throws ParseException 
	 */
	public ArrayList<WrongValueException> doWriteComponentsToBean(ChequeHeader chequeHeader) throws ParseException {
		logger.debug(Literal.LEAVING);

		doSetLOVValidation();

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		// cheque Type
		try {
			chequeHeader.setChequeType(this.chequeType.getValue().toString());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// noOfCheques
		try {
			chequeHeader.setNoOfCheques(this.noOfCheques.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// amount
		try {
			chequeHeader.setTotalAmount(this.amount.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Active
		try {
			chequeHeader.setActive(true);
		} catch (WrongValueException we) {
			wve.add(we);
		}

		doPrepareList(this.listBoxChequeDetail, chequeHeader);
		
		// validate existing data
		validateChequeDetails(chequeHeader.getChequeDetailList(), true);

		doRemoveValidation();
		doRemoveLOVValidation();

		logger.debug(Literal.LEAVING);
		return wve;
	}

	/**
	 * Displays the dialog page.
	 * 
	 * @param chequeDetail
	 *            The entity that need to be render.
	 */
	public void doShowDialog(FinanceDetail financeDetail) {
		logger.debug(Literal.LEAVING);

		if (financeDetail != null && !financeDetail.isNewRecord()) {
			if (financeDetail.getFinScheduleData().getFinanceMain().getRecordStatus()
					.equals(PennantConstants.RCD_STATUS_SUBMITTED)) {
				readOnlyComponent(true, this.chequeType);
				readOnlyComponent(true, this.amount);
				readOnlyComponent(true, this.noOfCheques);
				readOnlyComponent(true, this.bankBranchID);
				readOnlyComponent(true, this.chequeSerialNo);
				readOnlyComponent(true, this.accNumber);
				readOnlyComponent(true, this.amountCD);
				readOnlyComponent(true, this.amountCD);
				readOnlyComponent(true, this.noOfChequesCalc);
				readOnlyComponent(true, this.btnGen);
			}
		}
		if (financeDetail.getChequeHeader() != null && financeDetail.getChequeHeader().getChequeType() != null) {
			this.chequeType.setDisabled(true);
		}

		doWriteBeanToComponents(financeDetail);
		getBorderLayoutHeight();
		try {
			// fill the components with the data
			if (fromLoan) {
				try {
					Class[] paramType = { this.getClass() };
					Object[] stringParameter = { this };
					getFinanceMainDialogCtrl().getClass().getMethod("setChequeDetailDialogCtrl", paramType)
							.invoke(getFinanceMainDialogCtrl(), stringParameter);
				} catch (Exception e) {
					logger.error("Exception: ", e);
				}
				if (parenttab != null) {
					// checkTabDisplay(chequeDetail.getChequeType(), false);
				}

			} else {
				setDialog(DialogType.EMBEDDED);
			}

			this.window_ChequeDetailDialog.setHeight(this.borderLayoutHeight - 80 + "px");

		} catch (Exception e) {
			MessageUtil.showError(e);
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug(Literal.LEAVING);

		// Mandate Type
		if (!this.chequeType.isDisabled()) {
			this.chequeType.setConstraint(new StaticListValidator(chequeTypeList,
					Labels.getLabel("label_ChequeDetailDialog_ChequeType.value")));
		}
		// Number of cheques
		if (!this.noOfCheques.isReadonly()) {
			this.noOfCheques.setConstraint(
					new PTNumberValidator(Labels.getLabel("label_ChequeDetailDialog_NoOfCheques.value"), true, false));
		}
		// Total Amount
		if (!this.amount.isReadonly()) {
			this.amount.setConstraint(
					new PTDecimalValidator(Labels.getLabel("label_ChequeDetailDialog_Amount.value"), 2, true, false));
		}
		// Bank Branch ID
		if (!this.bankBranchID.isReadonly()) {
			this.bankBranchID.setConstraint(
					new PTStringValidator(Labels.getLabel("label_ChequeDetailDialog_BankBranchID.value"), null, true));
		}
		// Amount Cheque Detail
		if (!this.amountCD.isReadonly()) {
			this.amountCD.setConstraint(
					new PTDecimalValidator(Labels.getLabel("label_ChequeDetailDialog_Amount.value"), 2, true, false));
		}
		// Account Number
		if (!this.accNumber.isReadonly()) {
			this.accNumber
					.setConstraint(new PTStringValidator(Labels.getLabel("label_ChequeDetailDialog_AccNumber.value"),
							PennantRegularExpressions.REGEX_ACCOUNTNUMBER, true, this.accNoLength));
		}
		// Cheque Serial number
		if (!this.chequeSerialNo.isReadonly()) {
			this.chequeSerialNo.setConstraint(new PTNumberValidator(
					Labels.getLabel("label_ChequeDetailDialog_ChequeSerialNo.value"), true, false));
		}
		// Amount Cheque Detail
		if (!this.noOfChequesCalc.isReadonly()) {
			this.noOfChequesCalc.setConstraint(new PTNumberValidator(
					Labels.getLabel("label_ChequeDetailDialog_NoOfChequesCalc.value"), true, false));
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Remove the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug(Literal.LEAVING);

		this.bankBranchID.setConstraint("");
		this.chequeSerialNo.setConstraint("");
		this.amount.setConstraint("");

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set Validations for LOV Fields
	 */

	private void doSetLOVValidation() {
		logger.debug(Literal.LEAVING);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Remove the Validation by setting empty constraints.
	 */

	private void doRemoveLOVValidation() {
		logger.debug(Literal.LEAVING);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Clears validation error messages from all the fields of the dialog controller.
	 */
	@Override
	protected void doClearMessage() {
		logger.debug(Literal.LEAVING);

		logger.debug(Literal.LEAVING);
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

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug(Literal.LEAVING);

		if (getChequeHeader().isNewRecord()) {
			this.btnCancel.setVisible(false);
			readOnlyComponent(false, this.bankBranchID);
			readOnlyComponent(false, this.chequeSerialNo);
		} else {
			this.btnCancel.setVisible(true);
			readOnlyComponent(true, this.bankBranchID);
			readOnlyComponent(true, this.chequeSerialNo);

		}

		readOnlyComponent(isReadOnly("ChequeDetailDialog_Amount"), this.amount);

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}
			if (this.chequeDetail.isNewRecord()) {
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(false);
			} else {
				this.btnCtrl.setWFBtnStatus_Edit(isFirstTask());
			}
		} else {
			this.btnCtrl.setBtnStatus_Edit();
		}

		logger.debug(Literal.LEAVING);
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
			}
			throw new WrongValuesException(wvea);
		}

		logger.debug("Leaving");
	}

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly() {
		logger.debug(Literal.LEAVING);

		readOnlyComponent(true, this.bankBranchID);
		readOnlyComponent(true, this.chequeSerialNo);
		readOnlyComponent(true, this.amount);

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(true);
			}
			this.recordStatus.setValue("");
			this.userAction.setSelectedIndex(0);

		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Clears the components values. <br>
	 */
	public void doClear() {
		logger.debug("Entering");
		this.bankBranchID.setValue("");
		this.amount.setValue("");

		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 * @throws ParseException 
	 */
	public void doSave(FinanceDetail financeDetail, String finReference) throws ParseException {
		logger.debug("Entering");
		final ChequeHeader aChequeHeader = new ChequeHeader();
		BeanUtils.copyProperties(getChequeHeader(), aChequeHeader);
		boolean isNew = false;

		doSetValidation();

		ArrayList<WrongValueException> wve = doWriteComponentsToBean(aChequeHeader);
		if (!wve.isEmpty() && parenttab != null) {
			parenttab.setSelected(true);
		}
		showErrorDetails(wve);

		isNew = aChequeHeader.isNew();

		if (StringUtils.isBlank(aChequeHeader.getRecordType())) {
			aChequeHeader.setVersion(aChequeHeader.getVersion() + 1);
			if (isNew) {
				aChequeHeader.setNewRecord(true);
				aChequeHeader.setRecordType(PennantConstants.RECORD_TYPE_NEW);
			} else {
				aChequeHeader.setRecordType(PennantConstants.RECORD_TYPE_UPD);
			}
		}
		aChequeHeader.setFinReference(finReference);
		aChequeHeader.setLastMntBy(getUserWorkspace().getLoggedInUser().getLoginLogId());
		aChequeHeader.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aChequeHeader.setUserDetails(getUserWorkspace().getLoggedInUser());
		aChequeHeader.setTaskId(getTaskId());
		aChequeHeader.setNextTaskId(getNextTaskId());
		aChequeHeader.setRoleCode(getRole());
		aChequeHeader.setNextRoleCode(getNextRoleCode());
		for (ChequeDetail chequeDetail : aChequeHeader.getChequeDetailList()) {
			chequeDetail.setVersion(aChequeHeader.getVersion() + 1);
			chequeDetail.setLastMntBy(getUserWorkspace().getLoggedInUser().getLoginLogId());
			chequeDetail.setLastMntOn(new Timestamp(System.currentTimeMillis()));
			chequeDetail.setUserDetails(getUserWorkspace().getLoggedInUser());
			chequeDetail.setTaskId(getTaskId());
			chequeDetail.setNextTaskId(getNextTaskId());
			chequeDetail.setRoleCode(getRole());
			chequeDetail.setNextRoleCode(getNextRoleCode());
		}
		logger.debug("Leaving");

		financeDetail.setChequeHeader(aChequeHeader);
	}

	public void onClick$btnGen(Event event) throws ParseException {
		doSetValidation();
		// method to validate
		if (this.bankBranchID.getValidatedValue() != null && !this.bankBranchID.getValidatedValue().isEmpty()) {
			List<ChequeDetail> chequeDetails = new ArrayList<>();
			int numberofC = this.noOfChequesCalc.intValue();
			int chequStaretr = this.chequeSerialNo.intValue();
			this.noOfCheques.setValue(numberofC);
			this.chequeDetail = new ChequeDetail();
			BigDecimal totalChequeAmt = BigDecimal.ZERO;
			for (int i = 0; i < numberofC; i++) {
				ChequeDetail cheqDetails = getNewChequedetails();
				cheqDetails.setChequeSerialNo(chequStaretr);
				chequStaretr++;
				cheqDetails.setBankBranchID(Long.valueOf(this.bankBranchID.getValidatedValue()));
				cheqDetails.setAccountNo(this.accNumber.getValue());
				cheqDetails.setAmount(this.amountCD.getValue());
				totalChequeAmt = totalChequeAmt.add(this.amountCD.getValue());
				cheqDetails.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				cheqDetails.setNewRecord(true);
				chequeDetails.add(cheqDetails);
			}

			this.amount.setValue(totalChequeAmt);

			// validate existing data
			validateChequeDetails(chequeDetails, false);
			
			doFillChequeDetails(this.listBoxChequeDetail, chequeDetails);
		}
	}

	private void doFillChequeDetails(Listbox listbox, List<ChequeDetail> chequeDetails) {
		
		if (chequeDetails != null && chequeDetails.size() > 0) {
			for (ChequeDetail chequeDetail : chequeDetails) {
				boolean readonly = false;

				if (StringUtils.trimToEmpty(chequeDetail.getRecordType()).equals(PennantConstants.RCD_DEL)) {
					readonly = true;
				}

				Listitem listitem = new Listitem();
				listitem.setAttribute("data", chequeDetail);
				Listcell listcell;

				// ChequeSerialNo
				listcell = new Listcell(String.format("%06d", chequeDetail.getChequeSerialNo()));
				listcell.setParent(listitem);

				// Bank branch id
				listcell = new Listcell();
				ExtendedCombobox bankBranchID = new ExtendedCombobox();
				bankBranchID.setModuleName("BankBranch");
				bankBranchID.setReadonly(true);
				bankBranchID.setValueColumn("BranchCode");
				bankBranchID.setDescColumn("BranchDesc");
				bankBranchID.setDisplayStyle(2);
				bankBranchID.setValidateColumns(new String[] { "BranchCode" });
				bankBranchID.setValue(String.valueOf(chequeDetail.getBankBranchID()));
				bankBranchID.setDescription(chequeDetail.getBankBranchIDName());
				listcell.appendChild(bankBranchID);
				listcell.setParent(listitem);

				// AccountNo
				listcell = new Listcell(chequeDetail.getAccountNo());
				listcell.setParent(listitem);

				// Emi ref
				listcell = new Listcell();
				Combobox emiReference = getCombobox("1");
				Combobox emi = getCombobox(chequeDetail.geteMIRefNo());
				emiReference.setValue(emi.getSelectedItem().getLabel());
				readOnlyComponent(readonly, emiReference);
				listcell.appendChild(emiReference);
				listcell.setParent(listitem);

				// Amount
				listcell = new Listcell();
				Decimalbox emiAmount = new Decimalbox();
				emiAmount.setFormat(PennantConstants.in_amountFormate2);
				emiAmount.setValue(chequeDetail.getAmount());
				emiAmount.addForward("onChange", this.window_ChequeDetailDialog, "onChangeEmiAmount", emiAmount);
				readOnlyComponent(readonly, emiAmount);
				listcell.appendChild(emiAmount);
				listcell.setParent(listitem);

				// Bank branch id
				listcell = new Listcell();
				Button delButton = new Button("Delete");
				Object[] objected = new Object[2];
				objected[0] = chequeDetail;
				objected[1] = listitem;
				delButton.addForward("onClick", this.window_ChequeDetailDialog, "onClickDeleteButton", objected);
				readOnlyComponent(readonly, delButton);
				listcell.appendChild(delButton);
				listcell.setParent(listitem);

				// only to avoid the number format exception while setting the
				// value to bean
				listcell = new Listcell(chequeDetail.getAmount().toString());
				listcell.setParent(listitem);
				listcell.setVisible(false);
				// listbox.setStyle("overflow:auto");
				listbox.appendChild(listitem);
			}
		}
	}
	
	/**
	 * 
	 * @param chequeDetails
	 * @param validate 
	 * @throws ParseException 
	 */
	private void validateChequeDetails(List<ChequeDetail> chequeDetails, boolean validate) throws ParseException {
		for (Listitem listitem : listBoxChequeDetail.getItems()) {
			int emiRefCount = 0;
			for (ChequeDetail chequeDetail : chequeDetails) {
				List<Listcell> list = listitem.getChildren();
				Listcell chkSerial = (Listcell) list.get(0);
				Listcell extListCell = (Listcell) list.get(1);
				ExtendedCombobox extendedCombobox = (ExtendedCombobox) extListCell.getFirstChild();

				// validate cheque serial number
				if (!validate && !StringUtils.equals(chequeDetail.getRecordType(), PennantConstants.RECORD_TYPE_DEL)) {
					if (StringUtils.equals(String.valueOf(extendedCombobox.getValue()),
							String.valueOf(chequeDetail.getBankBranchID())) && StringUtils.equals(chkSerial.getLabel().toString(),
									String.format("%06d", chequeDetail.getChequeSerialNo()))) {
						throw new WrongValueException(extendedCombobox,
								Labels.getLabel("ChequeDetailDialog_ChkSerial_Exists"));
					}
				}

				// validate cheque EMI ref no's
				if(validate && !StringUtils.equals(chequeDetail.getRecordType(), PennantConstants.RECORD_TYPE_DEL)) {
					Listcell emiLc = (Listcell) list.get(3);
					Combobox emi = (Combobox) emiLc.getFirstChild();
					if(StringUtils.equals(emi.getSelectedItem().getValue().toString(), chequeDetail.geteMIRefNo())) {
						emiRefCount++;
						if(emiRefCount > 1) {
							throw new WrongValueException(emiLc, Labels.getLabel("ChequeDetailDialog_ChkEMIRef_Exists"));
						}
					}
				}
				
				if(validate) {
					Combobox comboItem = getCombobox(chequeDetail.geteMIRefNo());
					Date emiDate = DateUtility.parse(comboItem.getSelectedItem().getLabel(), PennantConstants.dateFormat);
					if(getFinanceDetail() != null) {
						List<FinanceScheduleDetail> schedules = financeDetail.getFinScheduleData().getFinanceScheduleDetails();
						for(FinanceScheduleDetail detail:schedules) {
							if (DateUtility.compare(emiDate, detail.getSchDate()) == 0) {
								String curField = getFinanceDetail().getFinScheduleData().getFinanceMain().getFinCcy();
								BigDecimal chequeAmt = PennantAppUtil.unFormateAmount(chequeDetail.getAmount(), CurrencyUtil.getFormat(curField));
								if (detail.getRepayAmount().compareTo(chequeAmt) != 0) {
									Listcell emiAmountLc = (Listcell) list.get(4);
									//throw new WrongValueException(emiAmountLc, Labels.getLabel("ChequeDetailDialog_EMI_Amount"));
								} else {
									break;
								}
							}
						}
					}
				}
				
			}
		}
	}

	public void onChangeEmiAmount(Event event) {
		logger.debug(Literal.ENTERING);
		
		BigDecimal totalChequeAmt = BigDecimal.ZERO;
		int noOfCheques = 0;
		for (Listitem listitem : listBoxChequeDetail.getItems()) {
			List<Listcell> list = listitem.getChildren();
			Listcell emiAmtLc = (Listcell) list.get(4);
			Decimalbox emiAmount = (Decimalbox) emiAmtLc.getFirstChild();
			totalChequeAmt = totalChequeAmt.add(emiAmount.getValue());
			noOfCheques++;
		}
		this.amount.setValue(totalChequeAmt);
		this.noOfCheques.setValue(noOfCheques);
		
		logger.debug(Literal.LEAVING);
	}

	private void doPrepareList(Listbox listbox, ChequeHeader chequeHeader) throws ParseException {

		ChequeDetail chequeDetail = null;
		boolean newRecord = false;
		List<ChequeDetail> oldList = chequeHeader.getChequeDetailList();

		for (Listitem listitem : listbox.getItems()) {
			List<Listcell> list = listitem.getChildren();
			Listcell chequeSerialNo = list.get(0);

			chequeDetail = getObject(Integer.valueOf(chequeSerialNo.getLabel()), oldList);
			if (chequeDetail == null) {
				newRecord = true;
				chequeDetail = new ChequeDetail();
				chequeDetail.setNewRecord(true);
				chequeDetail.setRecordType(PennantConstants.RCD_ADD);
			}
			chequeDetail.setChequeSerialNo(Integer.valueOf(chequeSerialNo.getLabel()));
			Listcell bankbranchid = list.get(1);
			ExtendedCombobox bankbrachid = (ExtendedCombobox) bankbranchid.getFirstChild();

			chequeDetail.setNewRecord(false);
			chequeDetail.setBankBranchID(Long.valueOf(bankbrachid.getValue()));
			chequeDetail.setBankBranchIDName(bankbrachid.getDescription());

			Listcell accNo = (Listcell) list.get(2);
			chequeDetail.setAccountNo(accNo.getLabel());

			Listcell emiLc = (Listcell) list.get(3);
			Combobox emi = (Combobox) emiLc.getFirstChild();
			if(!StringUtils.equals(emi.getSelectedItem().getValue().toString(), PennantConstants.List_Select)) {
				chequeDetail.seteMIRefNo(emi.getSelectedItem().getValue().toString());
			} else {
				throw new WrongValueException(emiLc, Labels.getLabel("ChequeDetailDialog_EMI_Mand"));
			}
			Listcell amount = (Listcell) list.get(4);
			Decimalbox emiAmount = (Decimalbox) amount.getFirstChild();
			chequeDetail.setAmount(emiAmount.getValue());
			if (newRecord) {
				oldList.add(chequeDetail);
			}
		}
		chequeHeader.setChequeDetailList(oldList);

	}

	private ChequeDetail getObject(int serialNo, List<ChequeDetail> chequeDetailList) {
		if (chequeDetailList != null && chequeDetailList.size() > 0) {
			for (ChequeDetail chequeDetail : chequeDetailList) {
				if (chequeDetail.getChequeSerialNo() == serialNo) {
					return chequeDetail;
				}
			}
		}
		return null;
	}

	public void onClickDeleteButton(ForwardEvent event) {
		Object data = event.getData();
		Object[] rvddata = (Object[]) data;
		Listitem listitem = (Listitem) rvddata[1];
		ChequeDetail chequeDetail = (ChequeDetail) rvddata[0];
		// not saved in the db
		this.listBoxChequeDetail.removeItemAt(listitem.getIndex());
		if (chequeDetail != null && !chequeDetail.isNew()) {
			chequeDetail.setRecordType(PennantConstants.RECORD_TYPE_DEL);
		}
		
		onChangeEmiAmount(event);
	}

	private Combobox getCombobox(String eminumber) {
		List<FinanceScheduleDetail> list = financeDetail.getFinScheduleData().getFinanceScheduleDetails();
		Combobox combobox = new Combobox();
		combobox.setSclass(PennantConstants.mandateSclass);
		combobox.getChildren().clear();
		Comboitem comboitem = new Comboitem();
		comboitem.setValue(PennantConstants.List_Select);
		comboitem.setLabel(Labels.getLabel("Combo.Select"));
		combobox.appendChild(comboitem);
		combobox.setSelectedItem(comboitem);
		combobox.setReadonly(true);
		for (FinanceScheduleDetail valueLabel : list) {
			if (valueLabel.isRepayOnSchDate() || valueLabel.isPftOnSchDate()) {
				comboitem = new Comboitem();
				comboitem.setValue(valueLabel.getInstNumber());
				comboitem.setLabel(DateUtility.formatToShortDate(valueLabel.getSchDate()));
				combobox.appendChild(comboitem);
				if (String.valueOf(valueLabel.getInstNumber()).equals(String.valueOf(eminumber))) {
					combobox.setSelectedItem(comboitem);
				}
			}
		}
		return combobox;
	}

	private ChequeDetail getNewChequedetails() {
		ChequeDetail chequeDetail = new ChequeDetail();
		chequeDetail.setAccountNo(this.accNumber.getValue());
		return chequeDetail;
	}

	public void doSetLabels(ArrayList<Object> finHeaderList) {
		getFinBasicDetailsCtrl().doWriteBeanToComponents(finHeaderList);
	}

	public FinanceDetail getFinanceDetail() {
		return financeDetail;
	}

	public void setFinanceDetail(FinanceDetail financeDetail) {
		this.financeDetail = financeDetail;
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

	public ChequeDetail getChequeDetail() {
		return chequeDetail;
	}

	public void setChequeDetail(ChequeDetail chequeDetail) {
		this.chequeDetail = chequeDetail;
	}

	public ChequeHeader getChequeHeader() {
		return chequeHeader;
	}

	public void setChequeHeader(ChequeHeader chequeHeader) {
		this.chequeHeader = chequeHeader;
	}

	public List<ChequeDetail> getChequeDetailList() {
		return chequeDetailList;
	}

	public void setChequeDetailList(List<ChequeDetail> chequeDetailList) {
		this.chequeDetailList = chequeDetailList;
	}

	public void setBankDetailService(BankDetailService bankDetailService) {
		this.bankDetailService = bankDetailService;
	}

}
