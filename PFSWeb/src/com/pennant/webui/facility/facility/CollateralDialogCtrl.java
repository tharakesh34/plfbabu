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
 * FileName    		:  CollateralDialogCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  04-12-2013    														*
 *                                                                  						*
 * Modified Date    :  04-12-2013    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 04-12-2013       Pennant	                 0.1                                            * 
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
package com.pennant.webui.facility.facility;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
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
import org.zkoss.zul.Button;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Row;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.CurrencyBox;
import com.pennant.ExtendedCombobox;
import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.Notes;
import com.pennant.backend.model.applicationmaster.Currency;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.collateral.Collateral;
import com.pennant.backend.service.PagedListService;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.util.ErrorControl;
import com.pennant.util.PennantAppUtil;
import com.pennant.util.Constraint.PTDecimalValidator;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.ButtonStatusCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.MultiLineMessageBox;
import com.pennant.webui.util.PTMessageUtils;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * /WEB-INF/pages/Collateral/Collateral/collateralDialog.zul file. <br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 */
public class CollateralDialogCtrl extends GFCBaseCtrl implements Serializable {
	private static final long serialVersionUID = 1L;
	private final static Logger logger = Logger.getLogger(CollateralDialogCtrl.class);
	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window window_CollateralDialog; // autowired
	protected Textbox cAFReference; // autowired
	protected Textbox reference; // autowired
	protected Textbox lastReview; // autowired
	protected ExtendedCombobox currency; // autowired
	protected CurrencyBox value; // autowired
	protected CurrencyBox bankvaluation; // autowired
	protected Decimalbox bankmargin; // autowired
	protected Decimalbox actualCoverage; // autowired
	protected Decimalbox proposedCoverage; // autowired
	protected Textbox description; // autowired
	protected Label recordStatus; // autowired
	protected Radiogroup userAction;
	protected Groupbox groupboxWf;
	protected Row statusRow;
	// not auto wired vars
	private Collateral collateral; // overhanded per param
	private Collateral prvCollateral; // overhanded per param
	private transient FacilityDialogCtrl facilityDialogCtrl; // overhanded per
																// param
	// old value vars for edit mode. that we can check if something
	// on the values are edited since the last init.
	private transient String oldVar_cAFReference;
	private transient String oldVar_reference;
	private transient String oldVar_lastReview;
	private transient String oldVar_currency;
	private transient BigDecimal oldVar_value;
	private transient BigDecimal oldVar_bankvaluation;
	private transient BigDecimal oldVar_bankmargin;
	private transient BigDecimal oldVar_actualCoverage;
	private transient BigDecimal oldVar_proposedCoverage;
	private transient String oldVar_description;
	private transient String oldVar_recordStatus;
	private transient boolean validationOn;
	private boolean notes_Entered = false;
	// Button controller for the CRUD buttons
	private transient final String btnCtroller_ClassPrefix = "button_CollateralDialog_";
	private transient ButtonStatusCtrl btnCtrl;
	protected Button btnNew; // autowire
	protected Button btnEdit; // autowire
	protected Button btnDelete; // autowire
	protected Button btnSave; // autowire
	protected Button btnCancel; // autowire
	protected Button btnClose; // autowire
	protected Button btnHelp; // autowire
	protected Button btnNotes; // autowire
	private transient String oldVar_lovDescCurrencyName;
	// ServiceDAOs / Domain Classes
	private transient PagedListService pagedListService;
	private HashMap<String, ArrayList<ErrorDetails>> overideMap = new HashMap<String, ArrayList<ErrorDetails>>();
	private List<Collateral> collateralsList;

	private int ccyFormat = 2; //Need to set from bean
	private String userRole="";
	/**
	 * default constructor.<br>
	 */
	public CollateralDialogCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * zul-file is called with a parameter for a selected Collateral object in a
	 * Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_CollateralDialog(Event event) throws Exception {
		logger.debug(event.toString());
		try {
			/* set components visible dependent of the users rights */
		
			/*
			 * create the Button Controller. Disable not used buttons during
			 * working
			 */
			this.btnCtrl = new ButtonStatusCtrl(getUserWorkspace(), this.btnCtroller_ClassPrefix, true, this.btnNew, this.btnEdit, this.btnDelete, this.btnSave, this.btnCancel, this.btnClose, this.btnNotes);
			// get the params map that are overhanded by creation.
			final Map<String, Object> args = getCreationArgsMap(event);
			// READ OVERHANDED params !
			if (args.containsKey("collateral")) {
				this.collateral = (Collateral) args.get("collateral");
				Collateral befImage = new Collateral();
				BeanUtils.copyProperties(this.collateral, befImage);
				this.collateral.setBefImage(befImage);
				setCollateral(this.collateral);
				ccyFormat=getCollateral().getCcyFormat();
			} else {
				setCollateral(null);
			}
			this.collateral.setWorkflowId(0);
			doLoadWorkFlow(this.collateral.isWorkflow(), this.collateral.getWorkflowId(), this.collateral.getNextTaskId());
			if (isWorkFlowEnabled()) {
				this.userAction = setListRecordStatus(this.userAction);
				getUserWorkspace().alocateRoleAuthorities(getRole(), "CollateralDialog");
			}
			if (args.containsKey("role")) {
				userRole=args.get("role").toString();
				getUserWorkspace().alocateRoleAuthorities(userRole, "CollateralDialog");
			}
			doCheckRights();
			// READ OVERHANDED params !
			// we get the collateralListWindow controller. So we have access
			// to it and can synchronize the shown data when we do insert, edit
			// or
			// delete collateral here.
			if (args.containsKey("facilityDialogCtrl")) {
				setFacilityDialogCtrl((FacilityDialogCtrl) args.get("facilityDialogCtrl"));
			} else {
				setFacilityDialogCtrl(null);
			}
			// set Field Properties
			doSetFieldProperties();
			doShowDialog(getCollateral());
		} catch (Exception e) {
			PTMessageUtils.showErrorMessage(e.toString());
			e.printStackTrace();
			window_CollateralDialog.onClose();
		}
		logger.debug("Leaving");
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");
		// Empty sent any required attributes
		this.cAFReference.setMaxlength(50);
		this.reference.setMaxlength(50);
		this.lastReview.setMaxlength(50);
		this.currency.setMaxlength(3);
        this.currency.setMandatoryStyle(true);
		this.currency.setModuleName("Currency");
		this.currency.setValueColumn("CcyCode");
		this.currency.setDescColumn("CcyDesc");
		this.currency.setValidateColumns(new String[] { "CcyCode" });
		this.value.setMandatory(false);
		this.value.setMaxlength(18);
		this.value.setFormat(PennantApplicationUtil.getAmountFormate(ccyFormat));
		this.value.setScale(ccyFormat);
		this.bankvaluation.setMandatory(false);
		this.bankvaluation.setMaxlength(18);
		this.bankvaluation.setFormat(PennantApplicationUtil.getAmountFormate(ccyFormat));
		this.bankvaluation.setScale(ccyFormat);
		
		this.bankmargin.setMaxlength(6);
		this.bankmargin.setFormat(PennantApplicationUtil.getAmountFormate(2));
		this.bankmargin.setScale(2);
		this.actualCoverage.setMaxlength(6);
		this.actualCoverage.setFormat(PennantApplicationUtil.getAmountFormate(2));
		this.actualCoverage.setScale(2);
		this.proposedCoverage.setMaxlength(6);
		this.proposedCoverage.setFormat(PennantApplicationUtil.getAmountFormate(2));
		this.proposedCoverage.setScale(2);
		this.description.setMaxlength(2000);
		if (isWorkFlowEnabled()) {
			this.groupboxWf.setVisible(true);
			this.statusRow.setVisible(true);
		} else {
			this.groupboxWf.setVisible(false);
			this.statusRow.setVisible(false);
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
		getUserWorkspace().alocateAuthorities("CollateralDialog",userRole);
		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_CollateralDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_CollateralDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_CollateralDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_CollateralDialog_btnSave"));
		this.btnCancel.setVisible(false);
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
	public void onClose$window_CollateralDialog(Event event) throws Exception {
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
		PTMessageUtils.showHelpWindow(event, window_CollateralDialog);
		logger.debug("Leaving");
	}


	/**
	 * when the "delete" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnDelete(Event event) throws InterruptedException {
		logger.debug(event.toString());
		doDelete();
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
	 * when clicks on button "SearchFinCcy"
	 * 
	 * @param event
	 */
	public void onFulfill$currency(Event event) {
		logger.debug("Entering " + event.toString());

		this.currency.setConstraint("");
		Object dataObject = currency.getObject();

		if (dataObject instanceof String) {
			//
		} else {
			Currency details = (Currency) dataObject;
			if (details != null) {
				ccyFormat = details.getCcyEditField();
				doSetFieldProperties();
			}
		}
	}
	// GUI Process
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
		boolean close = true;
		if (isDataChanged()) {
			logger.debug("isDataChanged : true");
			// Show a confirm box
			final String msg = Labels.getLabel("message_Data_Modified_Save_Data_YesNo");
			final String title = Labels.getLabel("message.Information");
			MultiLineMessageBox.doSetTemplate();
			int conf = MultiLineMessageBox.show(msg, title, MultiLineMessageBox.YES | MultiLineMessageBox.NO, MultiLineMessageBox.QUESTION, true);
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
			closePopUpWindow(this.window_CollateralDialog,"CollateralDialog");
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
	 * @param aCollateral
	 *            Collateral
	 */
	public void doWriteBeanToComponents(Collateral aCollateral) {
		logger.debug("Entering");
		this.cAFReference.setValue(aCollateral.getCAFReference());
		this.reference.setValue(aCollateral.getReference());
		this.lastReview.setValue(aCollateral.getLastReview());
		this.currency.setValue(aCollateral.getCurrency());
		this.value.setValue(PennantAppUtil.formateAmount(aCollateral.getValue(), ccyFormat));
		this.bankvaluation.setValue(PennantAppUtil.formateAmount(aCollateral.getBankvaluation(), ccyFormat));
		
		this.bankmargin.setValue(aCollateral.getBankmargin());
		this.actualCoverage.setValue(aCollateral.getActualCoverage());
		this.proposedCoverage.setValue(aCollateral.getProposedCoverage());
		this.description.setValue(aCollateral.getDescription());
		if (aCollateral.isNewRecord()) {
			this.currency.setDescription("");
		} else {
			this.currency.setDescription(aCollateral.getLovDescCurrencyName());
		}
		this.recordStatus.setValue(aCollateral.getRecordStatus());
		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aCollateral
	 */
	public void doWriteComponentsToBean(Collateral aCollateral) {
		logger.debug("Entering");
		doSetLOVValidation();
		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();
		try {
			aCollateral.setCAFReference(this.cAFReference.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCollateral.setReference(this.reference.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCollateral.setLastReview(this.lastReview.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCollateral.setLovDescCurrencyName(this.currency.getDescription());
			aCollateral.setCurrency(this.currency.getValidatedValue());
			aCollateral.setCcyFormat(ccyFormat);
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if (this.value.getValue() != null) {
				aCollateral.setValue(PennantApplicationUtil.unFormateAmount(this.value.getValue(), ccyFormat));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if (this.bankvaluation.getValue() != null) {
				aCollateral.setBankvaluation(PennantApplicationUtil.unFormateAmount(this.bankvaluation.getValue(), ccyFormat));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if (this.bankmargin.getValue() != null) {
				aCollateral.setBankmargin(this.bankmargin.getValue());
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if (this.actualCoverage.getValue() != null) {
				aCollateral.setActualCoverage(this.actualCoverage.getValue());
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if (this.proposedCoverage.getValue() != null) {
				aCollateral.setProposedCoverage(this.proposedCoverage.getValue());
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCollateral.setDescription(this.description.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		doRemoveValidation();
		doRemoveLOVValidation();
		if (wve.size() > 0) {
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = (WrongValueException) wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}
		aCollateral.setRecordStatus(this.recordStatus.getValue());
		logger.debug("Leaving");
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the
	 * readOnly mode accordingly.
	 * 
	 * @param aCollateral
	 * @throws InterruptedException
	 */
	public void doShowDialog(Collateral aCollateral) throws InterruptedException {
		logger.debug("Entering");
		// if aCollateral == null then we opened the Dialog without
		// args for a given entity, so we get a new Obj().
		if (aCollateral == null) {
			/** !!! DO NOT BREAK THE TIERS !!! */
			// We don't create a new DomainObject() in the frontend.
			// We GET it from the backend.
			aCollateral = null;// getCollateralService().getNewCollateral();
			setCollateral(aCollateral);
		} else {
			setCollateral(aCollateral);
		}
		// set Readonly mode accordingly if the object is new or not.
		if (aCollateral.isNew()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.cAFReference.focus();
		} else {
			this.currency.focus();
			doEdit();
			btnCancel.setVisible(false);
			this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_CollateralDialog_btnDelete"));
		}
		try {
			// fill the components with the data
			doWriteBeanToComponents(aCollateral);
			// stores the initial data for comparing if they are changed
			// during user action.
			doStoreInitValues();
			this.window_CollateralDialog.doModal();
		} catch (final Exception e) {
			logger.error(e);
			PTMessageUtils.showErrorMessage(e.toString());
		}
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
		this.oldVar_cAFReference = this.cAFReference.getValue();
		this.oldVar_reference = this.reference.getValue();
		this.oldVar_lastReview = this.lastReview.getValue();
		this.oldVar_currency = this.currency.getValue();
		this.oldVar_lovDescCurrencyName = this.currency.getDescription();
		this.oldVar_value = this.value.getValue();
		this.oldVar_bankvaluation = this.bankvaluation.getValue();
		this.oldVar_bankmargin = this.bankmargin.getValue();
		this.oldVar_actualCoverage = this.actualCoverage.getValue();
		this.oldVar_proposedCoverage = this.proposedCoverage.getValue();
		this.oldVar_description = this.description.getValue();
		this.oldVar_recordStatus = this.recordStatus.getValue();
		logger.debug("Leaving");
	}

	/**
	 * Resets the init values from mem vars. <br>
	 */
	private void doResetInitValues() {
		logger.debug("Entering");
		this.cAFReference.setValue(this.oldVar_cAFReference);
		this.reference.setValue(this.oldVar_reference);
		this.lastReview.setValue(this.oldVar_lastReview);
		this.currency.setValue(this.oldVar_currency);
		this.currency.setDescription(this.oldVar_lovDescCurrencyName);
		this.value.setValue(this.oldVar_value);
		this.bankvaluation.setValue(this.oldVar_bankvaluation);
		this.bankmargin.setValue(this.oldVar_bankmargin);
		this.actualCoverage.setValue(this.oldVar_actualCoverage);
		this.proposedCoverage.setValue(this.oldVar_proposedCoverage);
		this.description.setValue(this.oldVar_description);
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
		if (this.oldVar_cAFReference != this.cAFReference.getValue()) {
			return true;
		}
		if (this.oldVar_reference != this.reference.getValue()) {
			return true;
		}
		if (this.oldVar_lastReview != this.lastReview.getValue()) {
			return true;
		}
		if (this.oldVar_currency != this.currency.getValue()) {
			return true;
		}
		if (this.oldVar_value != this.value.getValue()) {
			return true;
		}
		if (this.oldVar_bankvaluation != this.bankvaluation.getValue()) {
			return true;
		}
		if (this.oldVar_bankmargin != this.bankmargin.getValue()) {
			return true;
		}
		if (this.oldVar_actualCoverage != this.actualCoverage.getValue()) {
			return true;
		}
		if (this.oldVar_proposedCoverage != this.proposedCoverage.getValue()) {
			return true;
		}
		if (this.oldVar_description != this.description.getValue()) {
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
		if (!this.reference.isReadonly()) {
			this.reference.setConstraint("NO EMPTY:" + Labels.getLabel("FIELD_NO_EMPTY", new String[] { Labels.getLabel("label_CollateralDialog_Reference.value") }));
		}
		if (!this.lastReview.isReadonly()) {
			this.lastReview.setConstraint("NO EMPTY:" + Labels.getLabel("FIELD_NO_EMPTY", new String[] { Labels.getLabel("label_CollateralDialog_LastReview.value") }));
		}
		if (!this.value.isReadonly()) {
			this.value.setConstraint(new PTDecimalValidator(Labels.getLabel("label_CollateralDialog_Value.value"), ccyFormat, false, false));
		}
		if (!this.bankvaluation.isReadonly()) {
			this.bankvaluation.setConstraint(new PTDecimalValidator(Labels.getLabel("label_CollateralDialog_Bankvaluation.value"), ccyFormat, false, false));
		}
		if (!this.bankmargin.isReadonly()) {
			this.bankmargin.setConstraint(new PTDecimalValidator(Labels.getLabel("label_CollateralDialog_Bankmargin.value"),
					2, false, false, 0, 100));
		}
		if (!this.actualCoverage.isReadonly()) {
			this.actualCoverage.setConstraint(new PTDecimalValidator(Labels.getLabel("label_CollateralDialog_ActualCoverage.value"),
				2, false, false, 0, 100));
		}
		if (!this.proposedCoverage.isReadonly()) {
			this.proposedCoverage.setConstraint(new PTDecimalValidator(Labels.getLabel("label_CollateralDialog_ProposedCoverage.value"),
					2, false, false, 0, 100));
		}
		
		logger.debug("Leaving");
	}

	/**
	 * Disables the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering");
		setValidationOn(false);
		this.cAFReference.setConstraint("");
		this.reference.setConstraint("");
		this.lastReview.setConstraint("");
		this.value.setConstraint("");
		this.bankvaluation.setConstraint("");
		this.bankmargin.setConstraint("");
		this.actualCoverage.setConstraint("");
		this.proposedCoverage.setConstraint("");
		this.description.setConstraint("");
		logger.debug("Leaving");
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// +++++++++++++++++++++++++ crud operations +++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	/**
	 * Deletes a Collateral object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug("Entering");
		final Collateral aCollateral = new Collateral();
		BeanUtils.copyProperties(getCollateral(), aCollateral);
		String tranType = PennantConstants.TRAN_WF;
		// Show a confirm box
		final String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record") + "\n\n --> " + aCollateral.getCAFReference();
		final String title = Labels.getLabel("message.Deleting.Record");
		MultiLineMessageBox.doSetTemplate();
		int conf = (MultiLineMessageBox.show(msg, title, MultiLineMessageBox.YES | MultiLineMessageBox.NO, Messagebox.QUESTION, true));
		if (conf == MultiLineMessageBox.YES) {
			logger.debug("doDelete: Yes");
			if (StringUtils.trimToEmpty(aCollateral.getRecordType()).equals("")) {
				aCollateral.setVersion(aCollateral.getVersion() + 1);
				aCollateral.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				if (isWorkFlowEnabled()) {
					aCollateral.setNewRecord(true);
					tranType = PennantConstants.TRAN_WF;
				} else {
					tranType = PennantConstants.TRAN_DEL;
				}
			} else if (StringUtils.trimToEmpty(aCollateral.getRecordType()).equals(PennantConstants.RCD_UPD)) {
				aCollateral.setVersion(aCollateral.getVersion() + 1);
				aCollateral.setRecordType(PennantConstants.RECORD_TYPE_DEL);
			}
			try {
				tranType = PennantConstants.TRAN_DEL;
				AuditHeader auditHeader = newFeeProcess(aCollateral, tranType);
				auditHeader = ErrorControl.showErrorDetails(this.window_CollateralDialog, auditHeader);
				int retValue = auditHeader.getProcessStatus();
				if (retValue == PennantConstants.porcessCONTINUE || retValue == PennantConstants.porcessOVERIDE) {
					getFacilityDialogCtrl().doFillCollaterals(this.collateralsList);
					closePopUpWindow(this.window_CollateralDialog,"CollateralDialog");
				}
			} catch (DataAccessException e) {
				logger.error("doDelete " + e);
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
		if (getCollateral().isNewRecord()) {
			this.reference.setReadonly(false);
			this.cAFReference.setReadonly(false);
		} else {
			this.reference.setReadonly(true);
			this.cAFReference.setReadonly(true);
		}
		//readOnlyComponent(isReadOnly("CollateralDialog_reference"), this.reference);
		readOnlyComponent(isReadOnly("CollateralDialog_lastReview"), this.lastReview);
		readOnlyComponent(isReadOnly("CollateralDialog_currency"), this.currency);
		readOnlyComponent(isReadOnly("CollateralDialog_value"), this.value);	
		readOnlyComponent(isReadOnly("CollateralDialog_bankvaluation"), this.bankvaluation);
		readOnlyComponent(isReadOnly("CollateralDialog_bankmargin"), this.bankmargin);
		readOnlyComponent(isReadOnly("CollateralDialog_actualCoverage"), this.actualCoverage);
		readOnlyComponent(isReadOnly("CollateralDialog_proposedCoverage"), this.proposedCoverage);
		readOnlyComponent(isReadOnly("CollateralDialog_description"), this.description);
		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}
			if (this.collateral.isNewRecord()) {
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(false);
			} else {
				this.btnCtrl.setWFBtnStatus_Edit(isFirstTask());
			}
		} else {
			this.btnCtrl.setBtnStatus_Edit();
		}
		// remember the old vars
		doStoreInitValues();
		logger.debug("Leaving");
	}
	public boolean isReadOnly(String componentName){
		return getUserWorkspace().isReadOnly(componentName);
	}
	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly() {
		logger.debug("Entering");
		this.cAFReference.setReadonly(true);
		this.reference.setReadonly(true);
		this.lastReview.setReadonly(true);
		this.currency.setReadonly(true);
		this.value.setReadonly(true);
		this.bankvaluation.setReadonly(true);
		this.bankmargin.setReadonly(true);
		this.actualCoverage.setReadonly(true);
		this.proposedCoverage.setReadonly(true);
		this.description.setReadonly(true);
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
		this.cAFReference.setValue("");
		this.reference.setValue("");
		this.lastReview.setValue("");
		this.currency.setValue("");
		this.currency.setDescription("");
		this.value.setValue("");
		this.bankvaluation.setValue("");
		this.bankmargin.setValue("");
		this.actualCoverage.setValue("");
		this.proposedCoverage.setValue("");
		this.description.setValue("");
		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug("Entering");
		final Collateral aCollateral = new Collateral();
		BeanUtils.copyProperties(getCollateral(), aCollateral);
		boolean isNew = false;
		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		// force validation, if on, than execute by component.getValue()
		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		doSetValidation();
		// fill the Collateral object with the components data
		doWriteComponentsToBean(aCollateral);
		// Write the additional validations as per below example
		// get the selected branch object from the listbox
		// Do data level validations here
		isNew = aCollateral.isNew();
		String tranType = "";
		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.trimToEmpty(aCollateral.getRecordType()).equals("")) {
				aCollateral.setVersion(aCollateral.getVersion() + 1);
				if (isNew) {
					aCollateral.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aCollateral.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aCollateral.setNewRecord(true);
				}
			}
		} else {
			if (isNew) {
				aCollateral.setVersion(1);
				aCollateral.setRecordType(PennantConstants.RCD_ADD);
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
			if (StringUtils.trimToEmpty(aCollateral.getRecordType()).equals("")) {
				aCollateral.setVersion(aCollateral.getVersion() + 1);
				aCollateral.setRecordType(PennantConstants.RCD_UPD);
			}
			if (aCollateral.getRecordType().equals(PennantConstants.RCD_ADD) && isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else if (aCollateral.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_UPD;
			}
		}
		// save it to database
		try {
			AuditHeader auditHeader = newFeeProcess(aCollateral, tranType);
			auditHeader = ErrorControl.showErrorDetails(this.window_CollateralDialog, auditHeader);
			int retValue = auditHeader.getProcessStatus();
			if (retValue == PennantConstants.porcessCONTINUE || retValue == PennantConstants.porcessOVERIDE) {
				getFacilityDialogCtrl().doFillCollaterals(this.collateralsList);
				closePopUpWindow(this.window_CollateralDialog,"CollateralDialog");
			}
		} catch (final DataAccessException e) {
			logger.error(e);
			showMessage(e);
		}
		logger.debug("Leaving");
	}

	private AuditHeader newFeeProcess(Collateral aCollateral, String tranType) {
		boolean recordAdded = false;
		AuditHeader auditHeader = getAuditHeader(aCollateral, tranType);
		collateralsList = new ArrayList<Collateral>();
		String[] valueParm = new String[2];
		String[] errParm = new String[2];
		valueParm[0] = aCollateral.getCAFReference();
		valueParm[1] = aCollateral.getReference();
		errParm[0] = PennantJavaUtil.getLabel("label_CAFReference") + ":" + valueParm[0];
		errParm[1] = PennantJavaUtil.getLabel("label_Reference") + ":" + valueParm[1];
		List<Collateral> list = getFacilityDialogCtrl().getCollateralsList();
		if (list != null && list.size() > 0) {
			for (int i = 0; i < list.size(); i++) {
				Collateral collateral = list.get(i);
				if (collateral.getReference().equals(aCollateral.getReference())) {
					// Both Current and Existing list rating same
					if (aCollateral.isNew()) {
						auditHeader.setErrorDetails(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41008", errParm, valueParm), getUserWorkspace().getUserLanguage()));
						return auditHeader;
					}
					if (tranType == PennantConstants.TRAN_DEL) {
						if (aCollateral.getRecordType().equals(PennantConstants.RECORD_TYPE_UPD)) {
							aCollateral.setRecordType(PennantConstants.RECORD_TYPE_DEL);
							recordAdded = true;
							collateralsList.add(aCollateral);
						} else if (aCollateral.getRecordType().equals(PennantConstants.RCD_ADD)) {
							recordAdded = true;
						} else if (aCollateral.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
							aCollateral.setRecordType(PennantConstants.RECORD_TYPE_CAN);
							recordAdded = true;
							collateralsList.add(aCollateral);
						} else if (aCollateral.getRecordType().equals(PennantConstants.RECORD_TYPE_CAN)) {
							recordAdded = true;
							List<Collateral> savedList = getFacilityDialogCtrl().getFacility().getCollaterals();
							for (int j = 0; j < savedList.size(); j++) {
								Collateral fee = savedList.get(j);
								if (fee.getReference().equals(aCollateral.getReference())) {
									collateralsList.add(fee);
								}
							}
						} else if (aCollateral.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
							aCollateral.setNewRecord(true);
						}
					} else {
						if (tranType != PennantConstants.TRAN_UPD) {
							collateralsList.add(collateral);
						}
					}
				} else {
					collateralsList.add(collateral);
				}
			}
		}
		if (!recordAdded) {
			collateralsList.add(aCollateral);
		}
		logger.debug("Leaving");
		return auditHeader;
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

	public Collateral getCollateral() {
		return this.collateral;
	}

	public void setCollateral(Collateral collateral) {
		this.collateral = collateral;
	}

	public void setFacilityDialogCtrl(FacilityDialogCtrl facilityDialogCtrl) {
		this.facilityDialogCtrl = facilityDialogCtrl;
	}

	public FacilityDialogCtrl getFacilityDialogCtrl() {
		return this.facilityDialogCtrl;
	}

	public PagedListService getPagedListService() {
		return pagedListService;
	}

	public void setPagedListService(PagedListService pagedListService) {
		this.pagedListService = pagedListService;
	}

	private AuditHeader getAuditHeader(Collateral aCollateral, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aCollateral.getBefImage(), aCollateral);
		return new AuditHeader(aCollateral.getCAFReference(), null, null, null, auditDetail, aCollateral.getUserDetails(), getOverideMap());
	}

	private void showMessage(Exception e) {
		AuditHeader auditHeader = new AuditHeader();
		try {
			auditHeader.setErrorDetails(new ErrorDetails(PennantConstants.ERR_UNDEF, e.getMessage(), null));
			ErrorControl.showErrorControl(this.window_CollateralDialog, auditHeader);
		} catch (Exception exp) {
			logger.error(exp);
		}
	}

	public boolean isNotes_Entered() {
		return notes_Entered;
	}

	public void setNotes_Entered(boolean notesEntered) {
		this.notes_Entered = notesEntered;
	}

	public void onClick$btnNotes(Event event) throws Exception {
		logger.debug("Entering");
		// logger.debug(event.toString());
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("notes", getNotes());
		map.put("control", this);
		// call the zul-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/notes/notes.zul", null, map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / " + e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving");
	}

	public void setNotes_entered(String notes) {
		if (!isNotes_Entered()) {
			if (org.apache.commons.lang.StringUtils.trimToEmpty(notes).equalsIgnoreCase("Y")) {
				setNotes_Entered(true);
			} else {
				setNotes_Entered(false);
			}
		}
	}

	private void doSetLOVValidation() {
		if(this.currency.isButtonVisible()){
			this.currency.setConstraint(new PTStringValidator(Labels.getLabel("label_CollateralDialog_Currency.value"),null,true,true));
		}
	}
	private void doRemoveLOVValidation() {
		this.currency.setConstraint("");
	}

	private Notes getNotes() {
		Notes notes = new Notes();
		notes.setModuleName("Collateral");
		notes.setReference(getCollateral().getCAFReference());
		notes.setVersion(getCollateral().getVersion());
		return notes;
	}

	private void doClearMessage() {
		logger.debug("Entering");
		this.cAFReference.setErrorMessage("");
		this.reference.setErrorMessage("");
		this.lastReview.setErrorMessage("");
		this.currency.setErrorMessage("");
		this.value.setErrorMessage("");
		this.bankvaluation.setErrorMessage("");
		this.bankmargin.setErrorMessage("");
		this.actualCoverage.setErrorMessage("");
		this.proposedCoverage.setErrorMessage("");
		this.description.setErrorMessage("");
		logger.debug("Leaving");
	}

	public void setOverideMap(HashMap<String, ArrayList<ErrorDetails>> overideMap) {
		this.overideMap = overideMap;
	}

	public HashMap<String, ArrayList<ErrorDetails>> getOverideMap() {
		return overideMap;
	}

	public Collateral getPrvCollateral() {
		return prvCollateral;
	}
}
