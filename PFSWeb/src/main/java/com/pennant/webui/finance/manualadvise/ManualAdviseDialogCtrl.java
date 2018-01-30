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
 * FileName    		:  ManualAdviseDialogCtrl.java                                          * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  23-04-2017    														*
 *                                                                  						*
 * Modified Date    :  23-04-2017    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 23-04-2017       PENNANT	                 0.1                                            * 
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
package com.pennant.webui.finance.manualadvise;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataAccessException;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.CurrencyBox;
import com.pennant.ExtendedCombobox;
import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.DateUtility;
import com.pennant.backend.model.ErrorDetail;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.feetype.FeeType;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.ManualAdvise;
import com.pennant.backend.model.finance.ManualAdviseMovements;
import com.pennant.backend.service.finance.ManualAdviseService;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.core.EventManager;
import com.pennant.core.EventManager.Notify;
import com.pennant.search.Filter;
import com.pennant.util.ErrorControl;
import com.pennant.util.PennantAppUtil;
import com.pennant.util.Constraint.PTDateValidator;
import com.pennant.util.Constraint.PTDecimalValidator;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.util.Constraint.StaticListValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.util.DateUtil.DateFormat;

/**
 * This is the controller class for the /WEB-INF/pages/finance/ManualAdvise/manualAdviseDialog.zul file. <br>
 */
public class ManualAdviseDialogCtrl extends GFCBaseCtrl<ManualAdvise> {

	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(ManualAdviseDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the zul-file
	 * are getting by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_ManualAdviseDialog;
	protected Combobox adviseType;
	protected ExtendedCombobox finReference;
	protected ExtendedCombobox feeTypeID;
	protected Intbox sequence;
	protected CurrencyBox adviseAmount;
	protected CurrencyBox paidAmount;
	protected CurrencyBox waivedAmount;
	protected Textbox remarks;
	private ManualAdvise manualAdvise;
	protected Listbox listBoxAdviseMovements;
	protected Datebox valueDate;
	protected Datebox postDate;

	protected Hbox hbox_Sequence;
	protected Groupbox adviseMovements;
	protected Label	   label_FeeTypeID;

	private transient ManualAdviseListCtrl manualAdviseListCtrl;
	private transient ManualAdviseService manualAdviseService;
	private EventManager eventManager;

	private List<ValueLabel> listAdviseType = PennantStaticListUtil.getManualAdviseTypes();
	
	public static final int DEFAULT_ADVISETYPE = FinanceConstants.MANUAL_ADVISE_RECEIVABLE;

	//FinanceDetails Fields
	protected Label										lbl_LoanReference;
	protected Label										lbl_LoanType;
	protected Label										lbl_CustCIF;
	protected Label										lbl_FinAmount;
	protected Label										lbl_startDate;
	protected Label										lbl_MaturityDate;
	protected Groupbox									finBasicdetails;

	private FinanceMain financeMain;
	/**
	 * default constructor.<br>
	 */
	public ManualAdviseDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "ManualAdviseDialog";
	}

	@Override
	protected String getReference() {
		StringBuffer referenceBuffer = new StringBuffer(String.valueOf(this.manualAdvise.getAdviseID()));
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
	public void onCreate$window_ManualAdviseDialog(Event event) throws Exception {
		logger.debug(Literal.ENTERING);

		// Set the page level components.
		setPageComponents(window_ManualAdviseDialog);

		try {
			// Get the required arguments.
			this.manualAdvise = (ManualAdvise) arguments.get("manualAdvise");
			this.manualAdviseListCtrl = (ManualAdviseListCtrl) arguments.get("manualAdviseListCtrl");

			if (this.manualAdvise == null) {
				throw new Exception(Labels.getLabel("error.unhandled"));
			}
			this.financeMain = (FinanceMain) arguments.get("financeMain");
			if (this.financeMain == null) {
				throw new Exception(Labels.getLabel("error.unhandled"));
			}
			// Store the before image.
			ManualAdvise manualAdvise = new ManualAdvise();
			BeanUtils.copyProperties(this.manualAdvise, manualAdvise);
			this.manualAdvise.setBefImage(manualAdvise);

			// Render the page and display the data.
			doLoadWorkFlow(this.manualAdvise.isWorkflow(), this.manualAdvise.getWorkflowId(),
					this.manualAdvise.getNextTaskId());

			if (isWorkFlowEnabled() && !enqiryModule) {
				this.userAction = setListRecordStatus(this.userAction);
				getUserWorkspace().allocateAuthorities(this.pageRightName, getRole());
			} else {
				getUserWorkspace().allocateAuthorities(this.pageRightName, null);
			}
			this.listBoxAdviseMovements.setHeight(borderLayoutHeight - 210 + "px");
			doSetFieldProperties();
			doCheckRights();
			doShowDialog(this.manualAdvise);
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

		/*
		 * this.finReference.setModuleName("FinanceMain"); this.finReference.setValueColumn("FinReference");
		 * this.finReference.setValidateColumns(new String[] { "FinReference" });
		 * this.finReference.setMandatoryStyle(true);
		 */
		this.feeTypeID.setModuleName("FeeType");
		this.feeTypeID.setValueColumn("FeeTypeCode");
		this.feeTypeID.setDescColumn("FeeTypeDesc");
		this.feeTypeID.setValidateColumns(new String[] { "FeeTypeCode" });
		this.feeTypeID.setMandatoryStyle(true);
		
		this.adviseAmount.setFormat(PennantApplicationUtil.getAmountFormate(PennantConstants.defaultCCYDecPos));
		this.adviseAmount.setRoundingMode(BigDecimal.ROUND_DOWN);
		this.adviseAmount.setScale(PennantConstants.defaultCCYDecPos);
		this.adviseAmount.setMandatory(true);

		this.paidAmount.setFormat(PennantApplicationUtil.getAmountFormate(PennantConstants.defaultCCYDecPos));
		this.paidAmount.setRoundingMode(BigDecimal.ROUND_DOWN);
		this.paidAmount.setScale(PennantConstants.defaultCCYDecPos);

		this.waivedAmount.setFormat(PennantApplicationUtil.getAmountFormate(PennantConstants.defaultCCYDecPos));
		this.waivedAmount.setRoundingMode(BigDecimal.ROUND_DOWN);
		this.waivedAmount.setScale(PennantConstants.defaultCCYDecPos);

		this.sequence.setMaxlength(10);
		this.remarks.setMaxlength(100);
		this.hbox_Sequence.setVisible(false);
		this.valueDate.setFormat(DateFormat.SHORT_DATE.getPattern());
		this.postDate.setFormat(DateFormat.SHORT_DATE.getPattern());

		if (enqiryModule) {
			this.groupboxWf.setVisible(false);
		} else {
			setStatusDetails();
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set Visible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug(Literal.ENTERING);

		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_ManualAdviseDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_ManualAdviseDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_ManualAdviseDialog_btnEdit"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_ManualAdviseDialog_btnDelete"));
		this.btnCancel.setVisible(false);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the save button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$btnSave(Event event) {
		logger.debug(Literal.ENTERING);
		doSave();
		logger.debug(Literal.LEAVING);

	}

	/**
	 * The framework calls this event handler when user clicks the edit button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$btnEdit(Event event) {
		logger.debug(Literal.ENTERING);
		doEdit();
		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the help button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$btnHelp(Event event) {
		logger.debug(Literal.ENTERING);
		MessageUtil.showHelpWindow(event, super.window);
		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the delete button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$btnDelete(Event event) throws InterruptedException {
		logger.debug(Literal.ENTERING);
		doDelete();
		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the cancel button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$btnCancel(Event event) {
		logger.debug(Literal.ENTERING);
		doCancel();
		logger.debug(Literal.LEAVING);
	}

	/**
	 * The Click event is raised when the Close Button control is clicked.
	 * 
	 * @param event
	 *            An event sent to the event handler of a component.
	 */
	public void onClick$btnClose(Event event) {
		logger.debug(Literal.ENTERING);
		doClose(this.btnSave.isVisible());
		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the notes button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$btnNotes(Event event) {
		logger.debug(Literal.ENTERING);
		doShowNotes(this.manualAdvise);
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Refresh the list page with the filters that are applied in list page.
	 */
	private void refreshList() {
		logger.debug(Literal.ENTERING);
		manualAdviseListCtrl.search();
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Cancel the actual operation and Resets to the original status
	 */
	private void doCancel() {
		logger.debug(Literal.ENTERING);

		doWriteBeanToComponents(this.manualAdvise.getBefImage());
		doReadOnly();
		this.btnCtrl.setInitEdit();
		this.btnCancel.setVisible(false);

		logger.debug(Literal.LEAVING);
	}

	public void onFulfillFeeTypeID(Event event) {
		logger.debug(Literal.ENTERING);
		Object dataObject = feeTypeID.getObject();
		if (dataObject instanceof String) {
			this.feeTypeID.setValue(dataObject.toString());
			this.feeTypeID.setDescription("");
		} else {
			FeeType details = (FeeType) dataObject;
			if (details != null) {
				this.feeTypeID.setAttribute("FeeTypeID", details.getFeeTypeID());
			}
		}
		logger.debug(Literal.LEAVING);
	}
	
	public void onChange$adviseType(Event event) {
		logger.debug(Literal.ENTERING);
		
		setFeeTypeFilters();
		
		logger.debug(Literal.LEAVING);
	}
	
	public void setFeeTypeFilters() {
		logger.debug(Literal.ENTERING);
		
		String adviseTypeValue= this.adviseType.getSelectedItem().getValue();
		
		Filter filter[] = null;
		
		if(StringUtils.equals(adviseTypeValue, PennantConstants.List_Select)) {
			filter = new Filter[1];
			filter[0] = new Filter("ManualAdvice", 1, Filter.OP_EQUAL);
		} else {
			filter = new Filter[2];
			filter[0] = new Filter("ManualAdvice", 1, Filter.OP_EQUAL);
			filter[1] = new Filter("AdviseType", adviseTypeValue, Filter.OP_EQUAL);
		}
		this.feeTypeID.setFilters(filter);
		this.feeTypeID.setValue("");
		this.feeTypeID.setDescription("");
		
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param manualAdvise
	 * 
	 */
	public void doWriteBeanToComponents(ManualAdvise aManualAdvise) {
		logger.debug(Literal.ENTERING);
		
		//FIXME to be changed on adding the payable advises. As of now Advise type is not visible
	//	this.adviseType.setValue(String.valueOf(FinanceConstants.MANUAL_ADVISE_RECEIVABLE));
		
		this.lbl_LoanReference.setValue(financeMain.getFinReference());
		this.lbl_LoanType.setValue(financeMain.getFinType() + " - " + financeMain.getLovDescFinTypeName());
		this.lbl_CustCIF.setValue(financeMain.getLovDescCustCIF() + " - " + financeMain.getLovDescCustShrtName());
		this.lbl_FinAmount.setValue(PennantApplicationUtil.amountFormate(financeMain.getFinAssetValue(),
				CurrencyUtil.getFormat(financeMain.getFinCcy())));
		this.lbl_startDate.setValue(DateUtility.formateDate(financeMain.getFinStartDate(), DateFormat.LONG_DATE.getPattern()));
		this.lbl_MaturityDate.setValue(DateUtility.formateDate(financeMain.getMaturityDate(), DateFormat.LONG_DATE.getPattern()));
		 
		fillComboBox(this.adviseType, String.valueOf(aManualAdvise.getAdviseType()), listAdviseType, "");
		setFeeTypeFilters();
		//this.finReference.setValue(aManualAdvise.getFinReference());
		this.feeTypeID.setAttribute("FeeTypeID", aManualAdvise.getFeeTypeID());
		this.feeTypeID.setValue(aManualAdvise.getFeeTypeCode(), aManualAdvise.getFeeTypeDesc());
		this.sequence.setValue(aManualAdvise.getSequence());
		this.adviseAmount.setValue(PennantApplicationUtil.formateAmount(aManualAdvise.getAdviseAmount(),
				PennantConstants.defaultCCYDecPos));
		this.paidAmount.setValue(
				PennantApplicationUtil.formateAmount(aManualAdvise.getPaidAmount(), PennantConstants.defaultCCYDecPos));
		this.waivedAmount.setValue(PennantApplicationUtil.formateAmount(aManualAdvise.getWaivedAmount(),
				PennantConstants.defaultCCYDecPos));
		this.remarks.setValue(aManualAdvise.getRemarks());

		if (aManualAdvise.isNewRecord()) {
			//this.finReference.setDescription("");
			this.feeTypeID.setDescription("");
			this.valueDate.setValue(DateUtility.getAppDate());
			this.postDate.setValue(DateUtility.getAppDate());
			
		} else {
			if (aManualAdvise.getFeeTypeCode() != null) {
				this.feeTypeID.setValue(aManualAdvise.getFeeTypeCode(), aManualAdvise.getFeeTypeDesc());
				this.feeTypeID.setObject(new FeeType(aManualAdvise.getFeeTypeID()));
			} else {
				this.label_FeeTypeID.setValue(Labels.getLabel("label_ManualAdviseDialog_BounceID.value"));
				this.feeTypeID.setAttribute("BounceID", aManualAdvise.getBounceID());
				this.feeTypeID.setValue(String.valueOf(aManualAdvise.getBounceID()), "");
			}
			this.valueDate.setValue(aManualAdvise.getValueDate());
			this.postDate.setValue(aManualAdvise.getPostDate());
		}
		if (enqiryModule) {
			this.adviseMovements.setVisible(true);
			List<ManualAdviseMovements> advisemovementList = manualAdviseService.getAdivseMovements(this.manualAdvise
					.getAdviseID());
			doFillMovementDetails(advisemovementList);
		} else {
			this.adviseMovements.setVisible(false);
		}
		this.recordStatus.setValue(manualAdvise.getRecordStatus());
		

		logger.debug(Literal.LEAVING);
	}
	
	/**
	 * Method for Rendering 
	 * @param advisemovementList
	 */
	private void doFillMovementDetails(List<ManualAdviseMovements> movementList) {
		logger.debug("Entering");
		
		this.listBoxAdviseMovements.getItems().clear();
		if(movementList != null && !movementList.isEmpty()){
			for (ManualAdviseMovements movement : movementList) {
				Listitem item = new Listitem();
				Listcell lc;
				
				lc = new Listcell(DateUtility.formatDate(movement.getMovementDate(),DateFormat.LONG_DATE.getPattern()));
				item.appendChild(lc);

				lc = new Listcell(PennantApplicationUtil.amountFormate(movement.getMovementAmount(),PennantConstants.defaultCCYDecPos));
				lc.setStyle("text-align:right;");
				item.appendChild(lc);
				
				lc = new Listcell(PennantApplicationUtil.amountFormate(movement.getPaidAmount(),PennantConstants.defaultCCYDecPos));
				lc.setStyle("text-align:right;");
				item.appendChild(lc);
				
				lc = new Listcell(PennantApplicationUtil.amountFormate(movement.getWaivedAmount(),PennantConstants.defaultCCYDecPos));
				lc.setStyle("text-align:right;");
				item.appendChild(lc);
				
				lc = new Listcell(movement.getStatus());
				item.appendChild(lc);
				
				lc = new Listcell(PennantAppUtil.getlabelDesc(movement.getReceiptMode(), PennantStaticListUtil.getReceiptModes()));
				item.appendChild(lc);

				this.listBoxAdviseMovements.appendChild(item);
			}
		}
		
		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aManualAdvise
	 */
	public void doWriteComponentsToBean(ManualAdvise aManualAdvise) {
		logger.debug(Literal.LEAVING);

		doSetLOVValidation();

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		// Advise Type
		try {
			String strAdviseType = null;
			if (this.adviseType.getSelectedItem() != null) {
				strAdviseType = this.adviseType.getSelectedItem().getValue().toString();
			}
			if (strAdviseType != null && !PennantConstants.List_Select.equals(strAdviseType)) {
				aManualAdvise.setAdviseType(Integer.parseInt(strAdviseType));

			} else {
				aManualAdvise.setAdviseType(DEFAULT_ADVISETYPE);
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Loan Reference
		try {
			//this.finReference.getValidatedValue();
			aManualAdvise.setFinReference(this.lbl_LoanReference.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Fee Type ID
		try {
			this.feeTypeID.getValidatedValue();
			FeeType feeType = (FeeType) this.feeTypeID.getObject();
			if (feeType != null) {
				aManualAdvise.setFeeTypeID(feeType.getFeeTypeID());
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Sequence
		try {
			aManualAdvise.setSequence(this.sequence.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Advise Amount
		try {
			if (this.adviseAmount.getActualValue() != null) {
				aManualAdvise.setAdviseAmount(PennantApplicationUtil.unFormateAmount(this.adviseAmount.getActualValue(),
						PennantConstants.defaultCCYDecPos));
				
				if (StringUtils.equals(this.adviseType.getSelectedItem().getValue().toString(),
						String.valueOf(FinanceConstants.MANUAL_ADVISE_PAYABLE))) {
					aManualAdvise.setBalanceAmt(PennantApplicationUtil.unFormateAmount(
							this.adviseAmount.getActualValue(), PennantConstants.defaultCCYDecPos));
				}
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Paid Amount
		try {
			if (this.paidAmount.getActualValue() != null) {
				aManualAdvise.setPaidAmount(PennantApplicationUtil.unFormateAmount(this.paidAmount.getActualValue(),
						PennantConstants.defaultCCYDecPos));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Waived Amount
		try {
			if (this.waivedAmount.getActualValue() != null) {
				aManualAdvise.setWaivedAmount(PennantApplicationUtil.unFormateAmount(this.waivedAmount.getActualValue(),
						PennantConstants.defaultCCYDecPos));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Remarks
		try {
			aManualAdvise.setRemarks(this.remarks.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		
		try {
			aManualAdvise.setValueDate(this.valueDate.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		
		try {
			aManualAdvise.setPostDate(this.postDate.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		doRemoveValidation();
		doRemoveLOVValidation();

		if (!wve.isEmpty()) {
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = (WrongValueException) wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Displays the dialog page.
	 * 
	 * @param manualAdvise
	 *            The entity that need to be render.
	 */
	public void doShowDialog(ManualAdvise manualAdvise) {
		logger.debug(Literal.LEAVING);

		if (manualAdvise.isNew()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.adviseType.focus();
		} else {
			if (isWorkFlowEnabled()) {
				if (StringUtils.isNotBlank(manualAdvise.getRecordType())) {
					this.btnNotes.setVisible(true);
				}
				// setFocus
				this.adviseType.focus();
				doEdit();
			} else {
				this.btnCtrl.setInitEdit();
				doReadOnly();
				btnCancel.setVisible(false);
			}
		}

		if (enqiryModule) {
			this.btnCtrl.setBtnStatus_Enquiry();
			this.btnNotes.setVisible(false);
		}

		doWriteBeanToComponents(manualAdvise);
		this.btnDelete.setVisible(false);
		
		setDialog(DialogType.EMBEDDED);
		
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug(Literal.LEAVING);
		
		
		//this.finReference.setConstraint(new PTStringValidator(Labels.getLabel("label_ManualAdviseDialog_FinReference.value"), null, true, true));

		this.feeTypeID.setConstraint(new PTStringValidator(Labels.getLabel("label_ManualAdviseDialog_FeeTypeID.value"), null, true, true));


		if (!this.adviseType.isDisabled()) {
			this.adviseType.setConstraint(new StaticListValidator(listAdviseType,
					Labels.getLabel("label_ManualAdviseDialog_AdviseType.value")));
		}
 
		if (!this.adviseAmount.isReadonly()) {
			this.adviseAmount.setConstraint(
					new PTDecimalValidator(Labels.getLabel("label_ManualAdviseDialog_AdviseAmount.value"),
							PennantConstants.defaultCCYDecPos, true, false, 0));
		}
		if (!this.valueDate.isDisabled() ) {
			this.valueDate.setConstraint(new PTDateValidator(Labels.getLabel("label_ManualAdviseDialog_ValueDate.value"),
					true, null, null, true));
		}
		

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Remove the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug(Literal.LEAVING);

		this.adviseType.setConstraint("");
		//this.finReference.setConstraint("");
		this.feeTypeID.setConstraint("");
		this.sequence.setConstraint("");
		this.adviseAmount.setConstraint("");
		this.paidAmount.setConstraint("");
		this.waivedAmount.setConstraint("");
		this.remarks.setConstraint("");
		this.valueDate.setConstraint("");
		

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set Validations for LOV Fields
	 */

	private void doSetLOVValidation() {
		logger.debug(Literal.LEAVING);

		// Advise ID
		// Advise Type
		// Loan Reference
		// Fee Type ID
		// Sequence
		// Advise Amount
		// Paid Amount
		// Waived Amount
		// Remarks

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
		this.valueDate.setErrorMessage("");
		this.postDate.setErrorMessage("");
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Deletes a ManualAdvise object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug(Literal.LEAVING);

		final ManualAdvise aManualAdvise = new ManualAdvise();
		BeanUtils.copyProperties(this.manualAdvise, aManualAdvise);
		String tranType = PennantConstants.TRAN_WF;

		// Show a confirm box
		final String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record") + "\n\n --> "
				+ aManualAdvise.getAdviseID();
		if (MessageUtil.confirm(msg) == MessageUtil.YES) {
			if (StringUtils.trimToEmpty(aManualAdvise.getRecordType()).equals("")) {
				aManualAdvise.setVersion(aManualAdvise.getVersion() + 1);
				aManualAdvise.setRecordType(PennantConstants.RECORD_TYPE_DEL);

				if (isWorkFlowEnabled()) {
					aManualAdvise.setRecordStatus(userAction.getSelectedItem().getValue().toString());
					aManualAdvise.setNewRecord(true);
					tranType = PennantConstants.TRAN_WF;
					getWorkFlowDetails(userAction.getSelectedItem().getLabel(), aManualAdvise.getNextTaskId(),
							aManualAdvise);
				} else {
					tranType = PennantConstants.TRAN_DEL;
				}
			}

			try {
				if (doProcess(aManualAdvise, tranType)) {
					refreshList();
					closeDialog();
				}

			} catch (DataAccessException e) {
				MessageUtil.showError(e);
			}

		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug(Literal.LEAVING);

		if (this.manualAdvise.isNewRecord()) {
			this.btnCancel.setVisible(false);
			//readOnlyComponent(isReadOnly("ManualAdviseDialog_FinReference"), this.finReference);
			readOnlyComponent(isReadOnly("ManualAdviseDialog_FeeTypeID"), this.feeTypeID);
			readOnlyComponent(isReadOnly("ManualAdviseDialog_AdviseType"), this.adviseType);
			readOnlyComponent(isReadOnly("ManualAdviseDialog_AdviseAmount"), this.adviseAmount);
			readOnlyComponent(isReadOnly("ManualAdviseDialog_PaidAmount"), this.paidAmount);
			readOnlyComponent(isReadOnly("ManualAdviseDialog_WaivedAmount"), this.waivedAmount);
			readOnlyComponent(isReadOnly("ManualAdviseDialog_Sequence"), this.sequence);
			readOnlyComponent(isReadOnly("ManualAdviseDialog_ValueDate"), this.valueDate);
		} else {
			this.btnCancel.setVisible(true);
			//readOnlyComponent(true, this.finReference);
			readOnlyComponent(true, this.feeTypeID);
			//readOnlyComponent(true, this.adviseAmount);
			readOnlyComponent(true, this.paidAmount);
			readOnlyComponent(true, this.waivedAmount);
			readOnlyComponent(true, this.adviseType);
			readOnlyComponent(true, this.sequence);
			readOnlyComponent(true, this.postDate);
		}
		readOnlyComponent(true, this.postDate);
		if (!enqiryModule) {
			readOnlyComponent(isReadOnly("ManualAdviseDialog_Remarks"), this.remarks);
			readOnlyComponent(isReadOnly("ManualAdviseDialog_AdviseAmount"), this.adviseAmount);
			readOnlyComponent(isReadOnly("ManualAdviseDialog_ValueDate"), this.valueDate);
			//readOnlyComponent(isReadOnly("ManualAdviseDialog_AdviseType"), this.adviseType);
		} else {
			readOnlyComponent(true, this.adviseAmount);
			readOnlyComponent(true, this.remarks);
			readOnlyComponent(true, this.valueDate);
			//readOnlyComponent(true, this.adviseType);
		}

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}
			if (this.manualAdvise.isNewRecord()) {
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
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly() {
		logger.debug(Literal.LEAVING);
		readOnlyComponent(true, this.adviseType);
		readOnlyComponent(true, this.finReference);
		readOnlyComponent(true, this.feeTypeID);
		readOnlyComponent(true, this.sequence);
		readOnlyComponent(true, this.adviseAmount);
		readOnlyComponent(true, this.paidAmount);
		readOnlyComponent(true, this.waivedAmount);
		readOnlyComponent(true, this.remarks);
		readOnlyComponent(true, this.valueDate);
		readOnlyComponent(true, this.postDate);
		

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
		this.adviseType.setSelectedIndex(0);
		this.finReference.setValue("");
		this.finReference.setDescription("");
		this.feeTypeID.setValue("");
		this.feeTypeID.setDescription("");
		this.sequence.setText("");
		this.adviseAmount.setValue("");
		this.paidAmount.setValue("");
		this.waivedAmount.setValue("");
		this.remarks.setValue("");

		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 */
	public void doSave() {
		logger.debug("Entering");
		final ManualAdvise aManualAdvise = new ManualAdvise();
		BeanUtils.copyProperties(this.manualAdvise, aManualAdvise);
		boolean isNew = false;

		doSetValidation();
		doWriteComponentsToBean(aManualAdvise);

		isNew = aManualAdvise.isNew();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aManualAdvise.getRecordType())) {
				aManualAdvise.setVersion(aManualAdvise.getVersion() + 1);
				if (isNew) {
					aManualAdvise.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aManualAdvise.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aManualAdvise.setNewRecord(true);
				}
			}
		} else {
			aManualAdvise.setVersion(aManualAdvise.getVersion() + 1);
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}

		try {
			if (doProcess(aManualAdvise, tranType)) {
				refreshList();
				
				//Customer Notification for Role Identification
				if (StringUtils.isBlank(aManualAdvise.getNextTaskId())) {
					aManualAdvise.setNextRoleCode("");
				}
				String msg = PennantApplicationUtil.getSavingStatus(aManualAdvise.getRoleCode(),
						aManualAdvise.getNextRoleCode(), aManualAdvise.getFinReference(), " Manual Advise ",
						aManualAdvise.getRecordStatus());
				Clients.showNotification(msg, "info", null, null, -1);
				
				// User Notifications Message/Alert
				try {
					if (!"Save".equalsIgnoreCase(this.userAction.getSelectedItem().getLabel())
							&& !"Cancel".equalsIgnoreCase(this.userAction.getSelectedItem().getLabel())
							&& !this.userAction.getSelectedItem().getLabel().contains("Reject")) {

						String reference = aManualAdvise.getFinReference();
						if (StringUtils.isNotEmpty(aManualAdvise.getNextRoleCode())) {
							if (!PennantConstants.RCD_STATUS_CANCELLED.equals(aManualAdvise.getRecordStatus())) {
								Notify notify = Notify.valueOf("ROLE");
								String[] to = aManualAdvise.getNextRoleCode().split(",");
								String message;

								if (StringUtils.isBlank(aManualAdvise.getNextTaskId())) {
									message = Labels.getLabel("REC_FINALIZED_MESSAGE");
								} else {
									message = Labels.getLabel("REC_PENDING_MESSAGE");
								}
								message += " with Manual Advise Reference" + ":" + reference;

								getEventManager().publish(message, notify, to);
							}
						}
					}
				} catch (Exception e) {
					logger.error("Exception: ", e);
				}
				
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
	private boolean doProcess(ManualAdvise aManualAdvise, String tranType) {
		logger.debug("Entering");
		boolean processCompleted = false;
		AuditHeader auditHeader = null;
		String nextRoleCode = "";

		aManualAdvise.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		aManualAdvise.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aManualAdvise.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			String nextTaskId = "";
			aManualAdvise.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aManualAdvise.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getNextTaskIds(taskId, aManualAdvise);
				}

				if (isNotesMandatory(taskId, aManualAdvise)) {
					if (!notesEntered) {
						MessageUtil.showError(Labels.getLabel("Notes_NotEmpty"));
						return false;
					}

				}
			}
			if (!StringUtils.isBlank(nextTaskId)) {
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

			aManualAdvise.setTaskId(taskId);
			aManualAdvise.setNextTaskId(nextTaskId);
			aManualAdvise.setRoleCode(getRole());
			aManualAdvise.setNextRoleCode(nextRoleCode);

			auditHeader = getAuditHeader(aManualAdvise, tranType);
			String operationRefs = getServiceOperations(taskId, aManualAdvise);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader, null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(aManualAdvise, PennantConstants.TRAN_WF);
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		} else {
			auditHeader = getAuditHeader(aManualAdvise, tranType);
			processCompleted = doSaveProcess(auditHeader, null);
		}

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
		ManualAdvise aManualAdvise = (ManualAdvise) auditHeader.getAuditDetail().getModelData();
		boolean deleteNotes = false;

		try {

			while (retValue == PennantConstants.porcessOVERIDE) {

				if (StringUtils.isBlank(method)) {
					if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {
						auditHeader = manualAdviseService.delete(auditHeader);
						deleteNotes = true;
					} else {
						auditHeader = manualAdviseService.saveOrUpdate(auditHeader);
					}

				} else {
					if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
						auditHeader = manualAdviseService.doApprove(auditHeader);

						if (aManualAdvise.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
							deleteNotes = true;
						}

					} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
						auditHeader = manualAdviseService.doReject(auditHeader);
						if (aManualAdvise.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
							deleteNotes = true;
						}

					} else {
						auditHeader.setErrorDetails(new ErrorDetail(PennantConstants.ERR_9999,
								Labels.getLabel("InvalidWorkFlowMethod"), null));
						retValue = ErrorControl.showErrorControl(this.window_ManualAdviseDialog, auditHeader);
						return processCompleted;
					}
				}

				auditHeader = ErrorControl.showErrorDetails(this.window_ManualAdviseDialog, auditHeader);
				retValue = auditHeader.getProcessStatus();

				if (retValue == PennantConstants.porcessCONTINUE) {
					processCompleted = true;

					if (deleteNotes) {
						deleteNotes(getNotes(this.manualAdvise), true);
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

		logger.debug("Leaving");
		return processCompleted;
	}

	/**
	 * @param aAuthorizedSignatoryRepository
	 * @param tranType
	 * @return
	 */

	private AuditHeader getAuditHeader(ManualAdvise aManualAdvise, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aManualAdvise.getBefImage(), aManualAdvise);
		return new AuditHeader(getReference(), null, null, null, auditDetail, aManualAdvise.getUserDetails(),
				getOverideMap());
	}

	public void setManualAdviseService(ManualAdviseService manualAdviseService) {
		this.manualAdviseService = manualAdviseService;
	}

	public EventManager getEventManager() {
		return eventManager;
	}

	public void setEventManager(EventManager eventManager) {
		this.eventManager = eventManager;
	}

	public FinanceMain getFinanceMain() {
		return financeMain;
	}

	public void setFinanceMain(FinanceMain financeMain) {
		this.financeMain = financeMain;
	}

}
