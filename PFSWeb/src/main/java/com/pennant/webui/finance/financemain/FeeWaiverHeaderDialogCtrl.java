/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. All
 * components/modules/functions/classes/logic in this software, unless otherwise stated, the property of Pennant
 * Technologies.
 * 
 * Copyright and other intellectual property laws protect these materials. Reproduction or retransmission of the
 * materials, in whole or in part, in any manner, without the prior written consent of the copyright holder, is a
 * violation of copyright law.
 */

/**
 ********************************************************************************************
 * FILE HEADER * * FileName : FeeWaiverHeader.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 27-11-2017 * *
 * Modified Date : * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 27-11-2017 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.webui.finance.financemain;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Row;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabpanels;
import org.zkoss.zul.Tabs;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.GSTCalculator;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.extendedfield.ExtendedFieldHeader;
import com.pennant.backend.model.extendedfield.ExtendedFieldRender;
import com.pennant.backend.model.finance.FeeWaiverDetail;
import com.pennant.backend.model.finance.FeeWaiverHeader;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.TaxAmountSplit;
import com.pennant.backend.model.finance.Taxes;
import com.pennant.backend.service.finance.FeeWaiverHeaderService;
import com.pennant.backend.util.ExtendedFieldConstants;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.RuleConstants;
import com.pennant.backend.util.UploadConstants;
import com.pennant.component.extendedfields.ExtendedFieldCtrl;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTDateValidator;
import com.pennant.util.Constraint.PTDecimalValidator;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.financemanagement.receipts.FeeWaiverEnquiryListCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil.DateFormat;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pff.constants.FinServiceEvent;
import com.pennapps.core.util.ObjectUtil;

/**
 * This is the controller class for the /WEB-INF/pages/Finance/financeMain/FeeWaiverHeaderDialog.zul file.
 */
public class FeeWaiverHeaderDialogCtrl extends GFCBaseCtrl<FeeWaiverHeader> {

	private static final long serialVersionUID = -6945930303723518608L;
	private static final Logger logger = LogManager.getLogger(FeeWaiverHeaderDialogCtrl.class);

	protected Window window_feeWaiverHeaderDialog;

	protected Textbox remarks;
	protected Datebox valueDate;
	protected Checkbox select;
	protected Listheader listheader_Select;

	protected Groupbox finBasicdetails;

	protected Listbox listFeeWaiverDetails;

	private FinanceDetail financeDetail;
	private FinanceMain financeMain;
	protected transient FinanceSelectCtrl financeSelectCtrl = null;
	protected transient FeeWaiverEnquiryListCtrl feeWaiverEnquiryListCtrl = null;
	private FeeWaiverHeader feeWaiverHeader;
	private transient FeeWaiverHeaderService feeWaiverHeaderService;
	private FinBasicDetailsCtrl finBasicDetailsCtrl;

	private Object financeMainDialogCtrl;

	private List<FeeWaiverDetail> feeWaiverDetails = new ArrayList<FeeWaiverDetail>();

	// private transient boolean recSave = false;
	private boolean isEnquiry = false;
	protected String moduleDefiner = "";
	protected String menuItemRightName = null;

	private int ccyFormatter = 0;
	private Row row_valueDate;
	private Row row_remarks;

	protected Listbox listFeeWaiverEnqDetails;
	protected Tabs feeWaiverTabs;
	protected Tab feeWaiverTab;
	protected Tabpanels feeWaiverTabPanels;

	private ExtendedFieldCtrl extendedFieldCtrl = null;

	/**
	 * listheader_Select default constructor.<br>
	 */
	public FeeWaiverHeaderDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "FeeWaiverHeaderDialog";
	}

	/**
	 * 
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onCreate$window_feeWaiverHeaderDialog(Event event) {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_feeWaiverHeaderDialog);

		try {

			// READ OVERHANDED parameters !
			if (arguments.containsKey("financeSelectCtrl")) {
				setFinanceSelectCtrl((FinanceSelectCtrl) arguments.get("financeSelectCtrl"));
				this.financeMainDialogCtrl = (Object) arguments.get("financeSelectCtrl");
			}

			if (arguments.containsKey("feeWaiverEnquiryListCtrl")) {
				setFeeWaiverEnquiryListCtrl((FeeWaiverEnquiryListCtrl) arguments.get("feeWaiverEnquiryListCtrl"));
				this.financeMainDialogCtrl = (Object) arguments.get("feeWaiverEnquiryListCtrl");
			}

			if (arguments.containsKey("moduleDefiner")) {
				moduleDefiner = (String) arguments.get("moduleDefiner");
			}

			if (arguments.containsKey("menuItemRightName")) {
				menuItemRightName = (String) arguments.get("menuItemRightName");
			}

			if (arguments.containsKey("financeDetail")) {
				setFinanceDetail((FinanceDetail) arguments.get("financeDetail"));
				this.financeMain = getFinanceDetail().getFinScheduleData().getFinanceMain();
			}

			if (arguments.containsKey("isEnquiry")) {
				isEnquiry = (Boolean) arguments.get("isEnquiry");
			}

			if (arguments.containsKey("feeWaiverHeader")) {
				setFeeWaiverHeader((FeeWaiverHeader) arguments.get("feeWaiverHeader"));
				this.feeWaiverHeader = getFeeWaiverHeader();
			}

			if (this.feeWaiverHeader == null) {
				throw new Exception(Labels.getLabel("error.unhandled"));
			}

			// Store the before image.
			FeeWaiverHeader feeWaiverHeader = new FeeWaiverHeader();
			BeanUtils.copyProperties(this.feeWaiverHeader, feeWaiverHeader);
			this.feeWaiverHeader.setBefImage(feeWaiverHeader);

			// Render the page and display the data.
			doLoadWorkFlow(this.feeWaiverHeader.isWorkflow(), this.feeWaiverHeader.getWorkflowId(),
					this.feeWaiverHeader.getNextTaskId());

			if (isWorkFlowEnabled()) {
				String recStatus = StringUtils.trimToEmpty(feeWaiverHeader.getRecordStatus());
				if (recStatus.equals(PennantConstants.RCD_STATUS_REJECTED)) {
					this.userAction = setRejectRecordStatus(this.userAction);
				} else {
					this.userAction = setListRecordStatus(this.userAction);
					getUserWorkspace().allocateMenuRoleAuthorities(getRole(), this.pageRightName, menuItemRightName);
				}
			} else {
				this.south.setHeight("0px");
			}
			this.listFeeWaiverDetails.setHeight(borderLayoutHeight - 210 + "px");
			ccyFormatter = CurrencyUtil.getFormat(this.financeMain.getFinCcy());

			if (isEnquiry) {
				this.listFeeWaiverDetails.setHeight(borderLayoutHeight - 210 + "px");
				ccyFormatter = CurrencyUtil.getFormat(this.financeMain.getFinCcy());
			}

			doSetFieldProperties();
			doCheckRights();
			doShowDialog(this.feeWaiverHeader);
		} catch (Exception e) {
			closeDialog();
			MessageUtil.showError(e);
		}

		logger.debug("Leaving");
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");

		setStatusDetails();
		this.remarks.setMaxlength(500);
		this.valueDate.setFormat(DateFormat.SHORT_DATE.getPattern());

		logger.debug("Leaving");
	}

	/**
	 * Set Visible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug("Entering");

		getUserWorkspace().allocateAuthorities(this.pageRightName, getRole(), menuItemRightName);

		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_FeeWaiverHeaderDialog_btnEdit"));
		this.btnDelete.setVisible(false);
		this.btnSave.setVisible(!getUserWorkspace().isAllowed("button_FeeWaiverHeaderDialog_btnSave"));
		this.btnCancel.setVisible(false);

		// Schedule related buttons
		logger.debug("Leaving");
	}

	/**
	 * The framework calls this event handler when user clicks the save button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$btnSave(Event event) {
		doSave();
	}

	/**
	 * The framework calls this event handler when user clicks the edit button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$btnEdit(Event event) {
		doEdit();
	}

	/**
	 * The framework calls this event handler when user clicks the help button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$btnHelp(Event event) {
		MessageUtil.showHelpWindow(event, super.window);
	}

	/**
	 * The framework calls this event handler when user clicks the cancel button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$btnCancel(Event event) {
		doCancel();
	}

	/**
	 * The Click event is raised when the Close Button control is clicked.
	 * 
	 * @param event An event sent to the event handler of a component.
	 */
	public void onClick$btnClose(Event event) {
		doClose(this.btnSave.isVisible());
		if (extendedFieldCtrl != null && financeDetail.getExtendedFieldHeader() != null) {
			extendedFieldCtrl.deAllocateAuthorities();
		}
	}

	/**
	 * Cancel the actual operation. <br>
	 * <br>
	 * Resets to the original status.<br>
	 * 
	 */
	private void doCancel() {
		logger.debug("Entering");

		doWriteBeanToComponents(this.feeWaiverHeader.getBefImage());
		doReadOnly();
		this.btnCtrl.setInitEdit();
		this.btnCancel.setVisible(false);

		logger.debug("Leaving");
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aFeeWaiverHeader
	 */
	public void doWriteBeanToComponents(FeeWaiverHeader aFeeWaiverHeader) {
		logger.debug("Entering");

		appendFinBasicDetails(this.financeMain);

		if (aFeeWaiverHeader.isNewRecord()) {
			this.valueDate.setValue(SysParamUtil.getAppDate());
		} else {
			this.valueDate.setValue(aFeeWaiverHeader.getValueDate());
		}

		this.remarks.setValue(aFeeWaiverHeader.getRemarks());

		if (isEnquiry) {
			List<FeeWaiverDetail> list = aFeeWaiverHeader.getFeeWaiverDetails();
			setFeeWaiverDetails(list);

			Map<String, FeeWaiverDetail> map = new HashMap<>();

			list.forEach(wd -> map.put(wd.getFeeTypeCode(), wd));

			doFillFeeWaiverEnqDetails(new ArrayList<>(map.values()));
		} else {
			doFillFeeWaiverDetails(aFeeWaiverHeader);
		}
		this.recordStatus.setValue(aFeeWaiverHeader.getRecordStatus());

		appendExtendedFieldDetails(financeDetail, moduleDefiner);

		logger.debug("Leaving");
	}

	private void appendExtendedFieldDetails(FinanceDetail aFinanceDetail, String finEvent) {
		logger.debug(Literal.ENTERING);
		ExtendedFieldRender extendedFieldRender = null;

		try {
			FinanceMain aFinanceMain = aFinanceDetail.getFinScheduleData().getFinanceMain();

			if (aFinanceMain == null || feeWaiverHeader == null) {
				return;
			}

			if (finEvent.isEmpty()) {
				finEvent = FinServiceEvent.ORG;
			}

			extendedFieldCtrl = new ExtendedFieldCtrl();
			ExtendedFieldHeader extendedFieldHeader = this.extendedFieldCtrl.getExtendedFieldHeader(
					ExtendedFieldConstants.MODULE_LOAN, aFinanceMain.getFinCategory(), finEvent);
			if (extendedFieldHeader == null) {
				return;
			}

			extendedFieldCtrl.setAppendActivityLog(true);
			extendedFieldCtrl.setFinBasicDetails(getFinBasicDetails());
			extendedFieldCtrl
					.setDataLoadReq((PennantConstants.RCD_STATUS_APPROVED.equals(feeWaiverHeader.getRecordStatus())
							|| feeWaiverHeader.getRecordStatus() == null) ? true : false);

			long instructionUID = Long.MIN_VALUE;

			if (CollectionUtils.isNotEmpty(feeWaiverHeader.getFinServiceInstructions())) {
				if (feeWaiverHeader.getFinServiceInstruction().getInstructionUID() != Long.MIN_VALUE) {
					instructionUID = feeWaiverHeader.getFinServiceInstruction().getInstructionUID();
				}
			}
			extendedFieldRender = extendedFieldCtrl.getExtendedFieldRender(aFinanceMain.getFinReference(),
					instructionUID);
			extendedFieldCtrl.createTab(feeWaiverTabs, feeWaiverTabPanels);
			feeWaiverHeader.setExtendedFieldHeader(extendedFieldHeader);
			feeWaiverHeader.setExtendedFieldRender(extendedFieldRender);

			if (feeWaiverHeader.getBefImage() != null) {
				feeWaiverHeader.getBefImage().setExtendedFieldHeader(extendedFieldHeader);
				feeWaiverHeader.getBefImage().setExtendedFieldRender(extendedFieldRender);
			}

			extendedFieldCtrl.setCcyFormat(CurrencyUtil.getFormat(aFinanceMain.getFinCcy()));
			extendedFieldCtrl.setReadOnly(false);
			extendedFieldCtrl.setWindow(window_feeWaiverHeaderDialog);
			extendedFieldCtrl.setTabHeight(this.borderLayoutHeight - 100);
			extendedFieldCtrl.setUserWorkspace(getUserWorkspace());
			extendedFieldCtrl.setUserRole(getRole());
			extendedFieldCtrl.render();

		} catch (Exception e) {
			logger.error(Labels.getLabel("message.error.Invalid_Extended_Field_Config"), e);
			MessageUtil.showError(Labels.getLabel("message.error.Invalid_Extended_Field_Config"));
		}
		logger.debug(Literal.LEAVING);

	}

	/**
	 * fill finance basic details to List
	 * 
	 * @return
	 */
	private ArrayList<Object> getFinBasicDetails() {
		logger.debug(Literal.ENTERING);
		FinanceMain financeMain = financeDetail.getFinScheduleData().getFinanceMain();

		ArrayList<Object> arrayList = new ArrayList<>();
		arrayList.add(0, financeMain.getFinType());
		arrayList.add(1, financeMain.getFinCcy());
		if (StringUtils.isNotEmpty(financeMain.getScheduleMethod())) {
			arrayList.add(2, financeMain.getScheduleMethod());
		} else {
			arrayList.add(2, "");
		}
		arrayList.add(3, financeMain.getFinReference());
		arrayList.add(4, financeMain.getProfitDaysBasis());
		arrayList.add(5, financeMain.getGrcPeriodEndDate());
		arrayList.add(6, financeMain.isAllowGrcPeriod());
		if (StringUtils.isNotEmpty(financeMain.getProduct())) {
			arrayList.add(7, true);
		} else {
			arrayList.add(7, false);
		}
		arrayList.add(8, financeMain.getFinCategory());
		String custShrtName = "";
		if (financeDetail.getCustomerDetails() != null && financeDetail.getCustomerDetails().getCustomer() != null) {
			custShrtName = financeDetail.getCustomerDetails().getCustomer().getCustShrtName();
		}
		arrayList.add(9, custShrtName);
		arrayList.add(10, financeDetail.getFinScheduleData().getFinanceMain().isNewRecord());
		arrayList.add(11, moduleDefiner);

		logger.debug(Literal.LEAVING);
		return arrayList;
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aFeeWaiverHeader
	 */
	public void doWriteComponentsToBean(FeeWaiverHeader aFeeWaiverHeader) {
		logger.debug("Entering");

		List<WrongValueException> wve = new ArrayList<>();

		aFeeWaiverHeader.setFinID(this.financeMain.getFinID());
		aFeeWaiverHeader.setFinReference(this.financeMain.getFinReference());
		aFeeWaiverHeader.setRemarks(this.remarks.getValue());
		aFeeWaiverHeader.setEvent(this.moduleDefiner);
		aFeeWaiverHeader.setFinSourceID(UploadConstants.FINSOURCE_ID_PFF);

		savePaymentDetails(aFeeWaiverHeader);
		aFeeWaiverHeader.setRecordStatus(this.recordStatus.getValue());

		// Value Date
		try {
			if (this.valueDate.getValue() != null) {
				aFeeWaiverHeader.setValueDate(new Timestamp(this.valueDate.getValue().getTime()));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aFeeWaiverHeader.setPostingDate(SysParamUtil.getAppDate());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		if (!wve.isEmpty()) {
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = (WrongValueException) wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}
		logger.debug("Leaving");
	}

	private void savePaymentDetails(FeeWaiverHeader aFeeWaiverHeader) {
		logger.debug("Entering");

		List<FeeWaiverDetail> list = new ArrayList<FeeWaiverDetail>();
		if (aFeeWaiverHeader.isNewRecord()) {
			for (FeeWaiverDetail detail : getFeeWaiverDetails()) {
				/*
				 * if (detail.getCurrWaiverAmount() != null && (BigDecimal.ZERO.compareTo(detail.getCurrWaiverAmount())
				 * == 0)) { continue; }
				 */
				// detail.setRecordStatus(PennantConstants.RCD_STATUS_SAVED);
				detail.setRecordStatus(userAction.getSelectedItem().getValue().toString());
				detail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				detail.setNewRecord(true);
				list.add(detail);
			}
		} else {
			for (FeeWaiverDetail detail : getFeeWaiverDetails()) {
				detail.setRecordStatus(userAction.getSelectedItem().getValue().toString());
				detail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				detail.setNewRecord(false);
				list.add(detail);
			}
		}
		aFeeWaiverHeader.setFeeWaiverDetails(list);
		logger.debug("Leaving");
	}

	private void doSetValidation() {
		logger.debug("Entering ");

		Label totCurrWaived = null;
		// Remarks Validation
		if (!this.remarks.isReadonly()) {
			this.remarks.setConstraint(
					new PTStringValidator(Labels.getLabel("label_FeeWaiverHeaderDialog_Remarks.value"), null, false));
		}
		if (!this.valueDate.isReadonly()) {
			this.valueDate
					.setConstraint(new PTDateValidator(Labels.getLabel("label_feeWaiverHeaderDialog_ValueDate.value"),
							true, null, SysParamUtil.getAppDate(), true));
		}

		if (this.listFeeWaiverDetails != null && this.listFeeWaiverDetails.getItems().size() > 0) {

			for (int i = 0; i < listFeeWaiverDetails.getItems().size(); i++) {
				List<Listcell> listCells = listFeeWaiverDetails.getItems().get(i).getChildren();

				if (listFeeWaiverDetails.getItemCount() - i == 1) {
					Listcell totCurrWaivedCell = listCells.get(8);
					totCurrWaived = (Label) totCurrWaivedCell.getChildren().get(0);
					break;
				}
				Listcell currWaivedAmtCell = listCells.get(8);
				Decimalbox currWaivedAmt = (Decimalbox) currWaivedAmtCell.getChildren().get(0);
				Clients.clearWrongValue(currWaivedAmt);

				if (currWaivedAmt.getValue().compareTo(BigDecimal.ZERO) < 0) {
					throw new WrongValueException(currWaivedAmt,
							Labels.getLabel("label_FeeWaiverHeaderDialog_CurrWaivedAmt_Non_Negative.value"));
				}

				if (PennantApplicationUtil.unFormateAmount(listCells.get(4).getLabel(), ccyFormatter)
						.compareTo(currWaivedAmt.getValue()) == -1) {
					throw new WrongValueException(currWaivedAmt,
							Labels.getLabel("label_FeeWaiverHeaderDialog_currWaiverAmountErrorMsg.value"));
				}
			}
		}

		if (PennantApplicationUtil.unFormateAmount(totCurrWaived.getValue(), ccyFormatter)
				.compareTo(BigDecimal.ZERO) == 0) {
			throw new WrongValueException(totCurrWaived,
					Labels.getLabel("label_FeeWaiverHeaderDialog_TotalCurrWaivedAmt.value"));
		}

		logger.debug("Leaving ");
	}

	/**
	 * Displays the dialog page.
	 * 
	 * @param aFeeWaiverHeader The entity that need to be render.
	 */
	public void doShowDialog(FeeWaiverHeader aFeeWaiverHeader) {
		logger.debug("Entering");

		// set ReadOnly mode accordingly if the object is new or not.
		if (aFeeWaiverHeader.isNewRecord()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
		} else {
			if (isWorkFlowEnabled()) {
				if (StringUtils.isNotBlank(aFeeWaiverHeader.getRecordType())) {
					this.btnNotes.setVisible(true);
				}
				doEdit();
			} else {
				this.btnCtrl.setInitEdit();
				doReadOnly();
				btnCancel.setVisible(false);
			}
		}

		if (enqiryModule) {
			this.row_remarks.setVisible(false);
			this.row_valueDate.setVisible(false);
			this.btnCtrl.setBtnStatus_Enquiry();
		}
		// fill the components with the data
		doWriteBeanToComponents(aFeeWaiverHeader);
		setDialog(DialogType.EMBEDDED);

		logger.debug("Leaving");
	}

	/**
	 * Set the components for edit mode. <br>
	 * 
	 */
	private void doEdit() {
		logger.debug("Entering");

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}
			if (this.feeWaiverHeader.isNewRecord()) {
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(false);
			} else {
				this.btnCtrl.setWFBtnStatus_Edit(isFirstTask());
			}
		} else {
			this.btnCtrl.setBtnStatus_Edit();
		}

		getUserWorkspace().allocateAuthorities(this.pageRightName, getRole());

		readOnlyComponent(isReadOnly("FeeWaiverHeaderDialog_valueDate"), this.valueDate);
		readOnlyComponent(isReadOnly("FeeWaiverHeaderDialog_remarks"), this.remarks);

		logger.debug("Leaving ");
	}

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly() {
		logger.debug("Entering");

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(true);
			}
		}

		if (isWorkFlowEnabled()) {
			this.recordStatus.setValue("");
			this.userAction.setSelectedIndex(0);
		}

		readOnlyComponent(true, this.valueDate);
		readOnlyComponent(true, this.remarks);

		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 */
	public void doSave() {
		logger.debug("Entering");

		FeeWaiverHeader aFeeWaiverHeader = new FeeWaiverHeader();
		aFeeWaiverHeader = ObjectUtil.clone(getFeeWaiverHeader());
		doSetValidation();
		doWriteComponentsToBean(aFeeWaiverHeader);

		boolean isNew;
		isNew = aFeeWaiverHeader.isNewRecord();
		String tranType;

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aFeeWaiverHeader.getRecordType())) {
				aFeeWaiverHeader.setVersion(aFeeWaiverHeader.getVersion() + 1);
				if (isNew) {
					aFeeWaiverHeader.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aFeeWaiverHeader.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aFeeWaiverHeader.setNewRecord(true);
				}
			}
		} else {
			aFeeWaiverHeader.setVersion(aFeeWaiverHeader.getVersion() + 1);
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}

		if (aFeeWaiverHeader.getExtendedFieldHeader() != null && extendedFieldCtrl != null) {
			aFeeWaiverHeader.setExtendedFieldRender(extendedFieldCtrl.save(true));
		}

		// save it to database
		try {
			if (doProcess(aFeeWaiverHeader, tranType)) {
				// List Detail Refreshment
				refreshList();

				// Confirmation message
				String msg = PennantApplicationUtil.getSavingStatus(aFeeWaiverHeader.getRoleCode(),
						aFeeWaiverHeader.getNextRoleCode(), aFeeWaiverHeader.getFinReference() + "",
						" Fee Waiver Details ", aFeeWaiverHeader.getRecordStatus());
				if (StringUtils.equals(aFeeWaiverHeader.getRecordStatus(), PennantConstants.RCD_STATUS_APPROVED)) {
					msg = " Fee Waiver Details with Reference " + aFeeWaiverHeader.getFinReference()
							+ " Approved Succesfully.";
				}
				Clients.showNotification(msg, "info", null, null, -1);

				if (extendedFieldCtrl != null && financeDetail.getExtendedFieldHeader() != null) {
					extendedFieldCtrl.deAllocateAuthorities();
				}

				closeDialog();
			}
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving");
	}

	/**
	 * Set the workFlow Details List to Object
	 * 
	 * @param aFeeWaiverHeader
	 * @param tranType         (String)
	 * @return boolean
	 */
	protected boolean doProcess(FeeWaiverHeader aFeeWaiverHeader, String tranType) {
		logger.debug("Entering");

		boolean processCompleted = false;
		AuditHeader auditHeader;
		String nextRoleCode = "";

		aFeeWaiverHeader.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		aFeeWaiverHeader.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aFeeWaiverHeader.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			String nextTaskId;
			aFeeWaiverHeader.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aFeeWaiverHeader.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getNextTaskIds(taskId, aFeeWaiverHeader);
				}

				if (isNotesMandatory(taskId, aFeeWaiverHeader)) {
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

			aFeeWaiverHeader.setTaskId(taskId);
			aFeeWaiverHeader.setNextTaskId(nextTaskId);
			aFeeWaiverHeader.setRoleCode(getRole());
			aFeeWaiverHeader.setNextRoleCode(nextRoleCode);

			auditHeader = getAuditHeader(aFeeWaiverHeader, tranType);
			String operationRefs = getServiceOperations(taskId, aFeeWaiverHeader);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader, null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(aFeeWaiverHeader, PennantConstants.TRAN_WF);
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		} else {
			auditHeader = getAuditHeader(aFeeWaiverHeader, tranType);
			processCompleted = doSaveProcess(auditHeader, null);
		}
		logger.debug("Leaving");
		return processCompleted;
	}

	/**
	 * Get the result after processing DataBase Operations
	 * 
	 * @param auditHeader (AuditHeader)
	 * @param method
	 * 
	 * @return boolean
	 */
	private boolean doSaveProcess(AuditHeader auditHeader, String method) {
		logger.debug("Entering");

		boolean processCompleted = false;
		int retValue = PennantConstants.porcessOVERIDE;
		AuditHeader aAuditHeader = auditHeader;
		FeeWaiverHeader aFeeWaiverHeader = (FeeWaiverHeader) aAuditHeader.getAuditDetail().getModelData();
		boolean deleteNotes = false;

		if (aFeeWaiverHeader.getExtendedFieldRender() != null) {
			ExtendedFieldRender details = aFeeWaiverHeader.getExtendedFieldRender();
			details.setReference(aFeeWaiverHeader.getFinReference());
			details.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
			details.setLastMntOn(new Timestamp(System.currentTimeMillis()));
			details.setRecordStatus(aFeeWaiverHeader.getRecordStatus());
			details.setRecordType(aFeeWaiverHeader.getRecordType());
			details.setVersion(aFeeWaiverHeader.getVersion());
			details.setWorkflowId(aFeeWaiverHeader.getWorkflowId());
			details.setTaskId(aFeeWaiverHeader.getTaskId());
			details.setNextTaskId(aFeeWaiverHeader.getNextTaskId());
			details.setRoleCode(aFeeWaiverHeader.getRoleCode());
			details.setNextRoleCode(aFeeWaiverHeader.getNextRoleCode());
			details.setNewRecord(aFeeWaiverHeader.isNewRecord());
			if (PennantConstants.RECORD_TYPE_DEL.equals(aFeeWaiverHeader.getRecordType())) {
				if (StringUtils.trimToNull(details.getRecordType()) == null) {
					details.setRecordType(aFeeWaiverHeader.getRecordType());
					details.setNewRecord(true);
				}
			}
		}

		while (retValue == PennantConstants.porcessOVERIDE) {
			if (StringUtils.isBlank(method)) {
				if (aAuditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {
					aAuditHeader = feeWaiverHeaderService.delete(aAuditHeader);
					deleteNotes = true;
				} else {
					aAuditHeader = feeWaiverHeaderService.saveOrUpdate(aAuditHeader);
				}

			} else {
				if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
					aAuditHeader = feeWaiverHeaderService.doApprove(aAuditHeader);

					if (aFeeWaiverHeader.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
						deleteNotes = true;
					}

				} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
					aAuditHeader = feeWaiverHeaderService.doReject(aAuditHeader);

					if (aFeeWaiverHeader.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
						deleteNotes = true;
					}

				} else {
					aAuditHeader.setErrorDetails(
							new ErrorDetail(PennantConstants.ERR_9999, Labels.getLabel("InvalidWorkFlowMethod"), null));
					retValue = ErrorControl.showErrorControl(this.window_feeWaiverHeaderDialog, aAuditHeader);
					return processCompleted;
				}
			}

			aAuditHeader = ErrorControl.showErrorDetails(this.window_feeWaiverHeaderDialog, aAuditHeader);
			retValue = aAuditHeader.getProcessStatus();

			if (retValue == PennantConstants.porcessCONTINUE) {
				processCompleted = true;

				if (deleteNotes) {
					deleteNotes(getNotes(this.feeWaiverHeader), true);
				}
			}

			if (retValue == PennantConstants.porcessOVERIDE) {
				aAuditHeader.setOveride(true);
				aAuditHeader.setErrorMessage(null);
				aAuditHeader.setInfoMessage(null);
				aAuditHeader.setOverideMessage(null);
			}
		}

		setOverideMap(aAuditHeader.getOverideMap());
		logger.debug("Leaving");
		return processCompleted;
	}

	/**
	 * Get Audit Header Details
	 * 
	 * @param aFeeWaiverHeader
	 * @param tranType
	 * @return
	 */
	private AuditHeader getAuditHeader(FeeWaiverHeader aFeeWaiverHeader, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aFeeWaiverHeader.getBefImage(), aFeeWaiverHeader);
		return new AuditHeader(getReference(), null, null, null, auditDetail, aFeeWaiverHeader.getUserDetails(),
				getOverideMap());
	}

	private void doFillFeeWaiverDetails(FeeWaiverHeader feeWaiverHeader) {
		logger.debug(Literal.ENTERING);

		if (feeWaiverHeader.isNewRecord()) {
			for (FeeWaiverDetail feeWaiverDetail : feeWaiverHeader.getFeeWaiverDetails()) {
				if (feeWaiverDetail.getBalanceAmount() != null
						&& feeWaiverDetail.getBalanceAmount().compareTo(BigDecimal.ZERO) > 0) {
					getFeeWaiverDetails().add(feeWaiverDetail);
				}
			}
		} else {
			updatePaybleAmounts(feeWaiverHeader.getFeeWaiverDetails());
		}

		doFillFeeWaiverDetails(getFeeWaiverDetails());

		logger.debug(Literal.LEAVING);
	}

	// Update the latest balance amount..
	private void updatePaybleAmounts(List<FeeWaiverDetail> feeWaiversList) {
		logger.debug(Literal.ENTERING);

		for (FeeWaiverDetail feeWaiver : feeWaiversList) {
			if (feeWaiver.getReceivableAmount().compareTo(BigDecimal.ZERO) > 0) {
				getFeeWaiverDetails().add(feeWaiver);
			}
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Fill Fee Waiver Details To list
	 * 
	 * @param feeWaiverDetails
	 */
	public void doFillFeeWaiverDetails(List<FeeWaiverDetail> feeWaiverDetails) {
		logger.debug("Entering");

		this.listFeeWaiverDetails.getItems().clear();
		boolean isReadOnly = true;

		if (!isEnquiry && getWorkFlow().firstTaskOwner().equals(getRole())) {
			isReadOnly = false;
		}

		BigDecimal totReceivableAmt = BigDecimal.ZERO;
		BigDecimal totGSTAmt = BigDecimal.ZERO;
		BigDecimal totTotalAmt = BigDecimal.ZERO;

		BigDecimal receivedAmt = BigDecimal.ZERO;
		BigDecimal totReceivedAmt = BigDecimal.ZERO;
		BigDecimal totWaivedAmt = BigDecimal.ZERO;
		BigDecimal totBalanceAmt = BigDecimal.ZERO;

		BigDecimal totCurrWaivedAmt = BigDecimal.ZERO;
		BigDecimal totDueWaiver = BigDecimal.ZERO;
		BigDecimal totGSTWaiver = BigDecimal.ZERO;

		BigDecimal totNetBal = BigDecimal.ZERO;

		setFeeWaiverDetails(feeWaiverDetails);

		if (CollectionUtils.isNotEmpty(feeWaiverDetails)) {
			Decimalbox crrWaivedAmt = null;

			for (FeeWaiverDetail detail : feeWaiverDetails) {
				Listitem item = null;
				Listcell lc;// 1
				item = new Listitem();
				lc = new Listcell();
				Checkbox selected = new Checkbox();
				selected.setTabindex(-1);
				selected.setChecked(false);
				ComponentsCtrl.applyForward(selected, "onCheck=onChecklistItemSelect");
				selected.setParent(lc);
				lc.setParent(item);

				// Fee TypeDesc
				lc = new Listcell(detail.getFeeTypeDesc());// 2
				lc.setParent(item);
				lc.setStyle("font-weight:bold;color:##FF4500;");

				// Due amount//receivable amount//3
				lc = new Listcell(PennantApplicationUtil.amountFormate(detail.getActualReceivable(), ccyFormatter));
				totReceivableAmt = totReceivableAmt.add(detail.getActualReceivable());
				lc.setStyle("text-align:right;");
				lc.setParent(item);

				// GST amount n//4
				lc = new Listcell(PennantApplicationUtil.amountFormate(detail.getReceivableGST(), ccyFormatter));
				totGSTAmt = totGSTAmt.add(detail.getReceivableGST());
				lc.setStyle("text-align:right;");
				lc.setParent(item);

				// Total Amount(Receivable + GST) n//5
				BigDecimal totAmt = detail.getReceivableAmount();
				lc = new Listcell(PennantApplicationUtil.amountFormate(totAmt, ccyFormatter));
				totTotalAmt = totAmt.add(totTotalAmt);
				lc.setStyle("text-align:right;");
				lc.setParent(item);

				// Received amount//6
				if (detail.getBalanceAmount().subtract(detail.getReceivedAmount()).compareTo(BigDecimal.ZERO) < 0) {
					lc = new Listcell(PennantApplicationUtil.amountFormate(BigDecimal.ZERO, ccyFormatter));
					totReceivedAmt = totReceivedAmt.add(BigDecimal.ZERO);
				} else {
					receivedAmt = detail.getReceivedAmount();
					lc = new Listcell(PennantApplicationUtil.amountFormate(receivedAmt, ccyFormatter));
					totReceivedAmt = receivedAmt.add(totReceivedAmt);
				}
				lc.setStyle("text-align:right;");
				lc.setParent(item);

				// Waived amount//7
				lc = new Listcell(PennantApplicationUtil.amountFormate(detail.getWaivedAmount(), ccyFormatter));
				totWaivedAmt = totWaivedAmt.add(detail.getWaivedAmount());
				lc.setStyle("text-align:right;");
				lc.setParent(item);

				// Receivable amount//8
				lc = new Listcell();
				Label balance = new Label();
				balance.setValue(PennantApplicationUtil.amountFormate(detail.getBalanceAmount(), ccyFormatter));
				totBalanceAmt = totBalanceAmt.add(detail.getBalanceAmount());
				lc.appendChild(balance);
				lc.setStyle("text-align:right;");
				lc.setParent(item);

				// Waived amount//9
				lc = new Listcell();
				crrWaivedAmt = new Decimalbox();
				crrWaivedAmt.setReadonly(isReadOnly);
				crrWaivedAmt.setFormat(PennantApplicationUtil.getAmountFormate(ccyFormatter));
				crrWaivedAmt.setStyle("text-align:right; ");
				crrWaivedAmt.setValue(PennantApplicationUtil.formateAmount(detail.getCurrWaiverAmount(), ccyFormatter));
				crrWaivedAmt.addForward("onChange", self, "onChangeCurrWaivedAmount");
				crrWaivedAmt.setAttribute("object", detail);
				BigDecimal raminingFee = PennantApplicationUtil.formateAmount(
						detail.getReceivableAmount().subtract(detail.getReceivedAmount()),
						PennantConstants.defaultCCYDecPos);
				if (raminingFee.compareTo(BigDecimal.ZERO) > 0) {
					crrWaivedAmt.setConstraint(new PTDecimalValidator(
							Labels.getLabel("label_FeeWaiverHeaderDialog_currWaiverAmountErrorMsg.value"), ccyFormatter,
							false, false, 0, raminingFee.doubleValue()));
				} else {
					crrWaivedAmt.setReadonly(true);
				}
				totCurrWaivedAmt = totCurrWaivedAmt.add(detail.getCurrWaiverAmount());
				lc.appendChild(crrWaivedAmt);
				lc.setStyle("text-align:right;");
				lc.setParent(item);

				// Due Waiver n//10
				lc = new Listcell(PennantApplicationUtil.amountFormate(detail.getCurrActualWaiver(), ccyFormatter));
				totDueWaiver = totDueWaiver.add(detail.getCurrActualWaiver());
				lc.setStyle("text-align:right;");
				lc.setParent(item);

				// GST Waiver n//11
				lc = new Listcell(PennantApplicationUtil.amountFormate(detail.getCurrWaiverGST(), ccyFormatter));
				totGSTWaiver = totGSTWaiver.add(detail.getCurrWaiverGST());
				lc.setStyle("text-align:right;");
				lc.setParent(item);

				// Net balance
				lc = new Listcell();// 12
				Label netBal = new Label();
				if (StringUtils.isNotEmpty(detail.getTaxComponent())
						&& FinanceConstants.FEE_TAXCOMPONENT_EXCLUSIVE.equals(detail.getTaxComponent())) {

					BigDecimal netBalance = detail.getActualReceivable().subtract(detail.getReceivedAmount());

					BigDecimal netAmt = netBalance.add(detail.getReceivableGST())
							.subtract(detail.getCurrWaiverAmount());
					netBal.setValue(PennantApplicationUtil.amountFormate(netAmt, ccyFormatter));

					totNetBal = totNetBal.add(netAmt);

				} else {
					netBal.setValue(PennantApplicationUtil.amountFormate(
							detail.getBalanceAmount().subtract(detail.getReceivedAmount()), ccyFormatter));

					totNetBal = totNetBal.add(detail.getBalanceAmount().subtract(detail.getReceivedAmount()));
				}
				lc.appendChild(netBal);
				lc.setStyle("text-align:right;");
				lc.setParent(item);

				crrWaivedAmt.setAttribute("NetBal", lc);

				this.listFeeWaiverDetails.appendChild(item);
			}

			Listitem item = new Listitem();
			Listcell lc;
			lc = new Listcell();
			lc.setParent(item);
			lc = new Listcell(" Total ");
			lc.setStyle("font-weight:bold;");
			item.appendChild(lc);

			lc = new Listcell(PennantApplicationUtil.amountFormate(totReceivableAmt, ccyFormatter));
			lc.setStyle("text-align:right;font-weight:bold;");
			lc.setParent(item);

			lc = new Listcell(PennantApplicationUtil.amountFormate(totGSTAmt, ccyFormatter));
			lc.setStyle("text-align:right;font-weight:bold;");
			lc.setParent(item);

			lc = new Listcell(PennantApplicationUtil.amountFormate(totTotalAmt, ccyFormatter));
			lc.setStyle("text-align:right;font-weight:bold;");
			lc.setParent(item);

			lc = new Listcell(PennantApplicationUtil.amountFormate(totReceivedAmt, ccyFormatter));
			lc.setStyle("text-align:right;font-weight:bold;");
			lc.setParent(item);

			lc = new Listcell(PennantApplicationUtil.amountFormate(totWaivedAmt, ccyFormatter));
			lc.setStyle("text-align:right;font-weight:bold;");
			lc.setParent(item);

			lc = new Listcell(PennantApplicationUtil.amountFormate(totBalanceAmt, ccyFormatter));
			lc.setStyle("text-align:right;font-weight:bold;");
			lc.setParent(item);

			lc = new Listcell();
			Label totCurrWaived = new Label(PennantApplicationUtil.amountFormate(totCurrWaivedAmt, ccyFormatter));
			lc.appendChild(totCurrWaived);
			lc.setStyle("text-align:right;font-weight:bold;");
			lc.setParent(item);

			lc = new Listcell(PennantApplicationUtil.amountFormate(totDueWaiver, ccyFormatter));
			lc.setStyle("text-align:right;font-weight:bold;");
			lc.setParent(item);

			lc = new Listcell(PennantApplicationUtil.amountFormate(totGSTWaiver, ccyFormatter));
			lc.setStyle("text-align:right;font-weight:bold;");
			lc.setParent(item);

			lc = new Listcell(PennantApplicationUtil.amountFormate(totNetBal, ccyFormatter));
			lc.setStyle("text-align:right;font-weight:bold;");
			lc.setParent(item);

			this.listFeeWaiverDetails.appendChild(item);
		}
		logger.debug("Leaving");
	}

	/**
	 * Fill Fee Waiver Details To list
	 * 
	 * @param feeWaiverDetails
	 */
	public void doFillFeeWaiverEnqDetails(List<FeeWaiverDetail> feeWaiverDetails) {
		this.listFeeWaiverDetails.getItems().clear();
		boolean isReadOnly = true;

		if (!isEnquiry && getWorkFlow().firstTaskOwner().equals(getRole())) {
			isReadOnly = false;
		}
		BigDecimal totReceivableAmt = BigDecimal.ZERO;
		BigDecimal totGSTAmt = BigDecimal.ZERO;
		BigDecimal totTotalAmt = BigDecimal.ZERO;

		BigDecimal totReceivedAmt = BigDecimal.ZERO;
		BigDecimal totWaivedAmt = BigDecimal.ZERO;
		BigDecimal totBalanceAmt = BigDecimal.ZERO;

		BigDecimal totCurrWaivedAmt = BigDecimal.ZERO;
		BigDecimal totDueWaiver = BigDecimal.ZERO;
		BigDecimal totGSTWaiver = BigDecimal.ZERO;

		BigDecimal totNetBal = BigDecimal.ZERO;

		setFeeWaiverDetails(feeWaiverDetails);

		if (CollectionUtils.isNotEmpty(feeWaiverDetails)) {
			Decimalbox crrWaivedAmt = null;

			for (FeeWaiverDetail detail : feeWaiverDetails) {
				Listitem item = null;
				Listcell lc;// 1
				item = new Listitem();
				lc = new Listcell();
				Checkbox selected = new Checkbox();
				selected.setTabindex(-1);
				selected.setChecked(false);
				ComponentsCtrl.applyForward(selected, "onCheck=onChecklistItemSelect");
				selected.setParent(lc);
				lc.setParent(item);

				// Fee TypeDesc
				lc = new Listcell(detail.getFeeTypeDesc());// 2
				lc.setParent(item);
				lc.setStyle("font-weight:bold;color:##FF4500;");

				// Due amount//receivable amount//3
				lc = new Listcell(PennantApplicationUtil.amountFormate(detail.getActualReceivable(), ccyFormatter));
				totReceivableAmt = totReceivableAmt.add(detail.getActualReceivable());
				lc.setStyle("text-align:right;");
				lc.setParent(item);

				// GST amount n//4
				lc = new Listcell(PennantApplicationUtil.amountFormate(detail.getReceivableGST(), ccyFormatter));
				totGSTAmt = totGSTAmt.add(detail.getReceivableGST());
				lc.setStyle("text-align:right;");
				lc.setParent(item);

				// Total Amount(Receivable + GST) n//5
				BigDecimal totAmt = detail.getReceivableAmount();
				lc = new Listcell(PennantApplicationUtil.amountFormate(totAmt, ccyFormatter));
				totTotalAmt = totAmt.add(totTotalAmt);
				lc.setStyle("text-align:right;");
				lc.setParent(item);

				// Received amount//6
				lc = new Listcell(PennantApplicationUtil.amountFormate(detail.getReceivedAmount(), ccyFormatter));
				totReceivedAmt = totReceivedAmt.add(detail.getReceivedAmount());
				lc.setStyle("text-align:right;");
				lc.setParent(item);

				// Waived amount//7
				lc = new Listcell(PennantApplicationUtil.amountFormate(detail.getWaivedAmount(), ccyFormatter));
				totWaivedAmt = totWaivedAmt.add(detail.getWaivedAmount());
				lc.setStyle("text-align:right;");
				lc.setParent(item);

				// Balance amount//8
				lc = new Listcell();
				Label balance = new Label();
				balance.setValue(PennantApplicationUtil.amountFormate(detail.getBalanceAmount(), ccyFormatter));
				totBalanceAmt = totBalanceAmt.add(detail.getBalanceAmount());
				lc.appendChild(balance);
				lc.setStyle("text-align:right;");
				lc.setParent(item);

				// Waived amount//9
				lc = new Listcell();
				crrWaivedAmt = new Decimalbox();
				crrWaivedAmt.setReadonly(isReadOnly);
				crrWaivedAmt.setFormat(PennantApplicationUtil.getAmountFormate(ccyFormatter));
				crrWaivedAmt.setStyle("text-align:right; ");
				crrWaivedAmt.setValue(PennantApplicationUtil.formateAmount(detail.getCurrWaiverAmount(), ccyFormatter));
				crrWaivedAmt.addForward("onChange", self, "onChangeCurrWaivedAmount");
				crrWaivedAmt.setAttribute("object", detail);
				totCurrWaivedAmt = totCurrWaivedAmt.add(detail.getCurrWaiverAmount());
				lc.appendChild(crrWaivedAmt);
				lc.setStyle("text-align:right;");
				lc.setParent(item);

				// Due Waiver n//10
				lc = new Listcell(PennantApplicationUtil.amountFormate(detail.getCurrActualWaiver(), ccyFormatter));
				totDueWaiver = totDueWaiver.add(detail.getCurrActualWaiver());
				lc.setStyle("text-align:right;");
				lc.setParent(item);

				// GST Waiver n//11
				lc = new Listcell(PennantApplicationUtil.amountFormate(detail.getCurrWaiverGST(), ccyFormatter));
				totGSTWaiver = totGSTWaiver.add(detail.getCurrWaiverGST());
				lc.setStyle("text-align:right;");
				lc.setParent(item);

				// Net balance
				lc = new Listcell();// 12
				Label netBal = new Label();
				if (StringUtils.isNotEmpty(detail.getTaxComponent())
						&& FinanceConstants.FEE_TAXCOMPONENT_EXCLUSIVE.equals(detail.getTaxComponent())) {

					BigDecimal netBalance = detail.getActualReceivable().subtract(detail.getCurrActualWaiver());
					netBalance = netBalance.subtract(detail.getReceivedAmount());
					BigDecimal gstOnNetBal = GSTCalculator.getTotalGST(detail.getFinReference(), netBalance,
							detail.getTaxComponent());

					netBal.setValue(PennantApplicationUtil.amountFormate((netBalance.add(gstOnNetBal)), ccyFormatter));

					if ((netBalance.add(gstOnNetBal)).compareTo(BigDecimal.ZERO) < 0) {
						netBal.setValue(PennantApplicationUtil.amountFormate(BigDecimal.ZERO, ccyFormatter));
					} else {
						totNetBal = totNetBal.add((netBalance.add(gstOnNetBal)));
					}

				} else {
					netBal.setValue(PennantApplicationUtil.amountFormate(
							detail.getBalanceAmount().subtract(detail.getReceivedAmount()), ccyFormatter));

					if (detail.getBalanceAmount().subtract(detail.getReceivedAmount()).compareTo(BigDecimal.ZERO) < 0) {
						netBal.setValue(PennantApplicationUtil.amountFormate(BigDecimal.ZERO, ccyFormatter));
					}
					totNetBal = totNetBal.add(detail.getBalanceAmount().subtract(detail.getReceivedAmount()));
				}
				lc.appendChild(netBal);
				lc.setStyle("text-align:right;");
				lc.setParent(item);

				crrWaivedAmt.setAttribute("NetBal", lc);

				this.listFeeWaiverDetails.appendChild(item);
			}

			Listitem item = new Listitem();
			Listcell lc;
			lc = new Listcell();
			lc.setParent(item);
			lc = new Listcell(" Total ");
			lc.setStyle("font-weight:bold;");
			item.appendChild(lc);

			lc = new Listcell(PennantApplicationUtil.amountFormate(totReceivableAmt, ccyFormatter));
			lc.setStyle("text-align:right;font-weight:bold;");
			lc.setParent(item);

			lc = new Listcell(PennantApplicationUtil.amountFormate(totGSTAmt, ccyFormatter));
			lc.setStyle("text-align:right;font-weight:bold;");
			lc.setParent(item);

			lc = new Listcell(PennantApplicationUtil.amountFormate(totTotalAmt, ccyFormatter));
			lc.setStyle("text-align:right;font-weight:bold;");
			lc.setParent(item);

			lc = new Listcell(PennantApplicationUtil.amountFormate(totReceivedAmt, ccyFormatter));
			lc.setStyle("text-align:right;font-weight:bold;");
			lc.setParent(item);

			lc = new Listcell(PennantApplicationUtil.amountFormate(totWaivedAmt, ccyFormatter));
			lc.setStyle("text-align:right;font-weight:bold;");
			lc.setParent(item);

			lc = new Listcell(PennantApplicationUtil.amountFormate(totBalanceAmt, ccyFormatter));
			lc.setStyle("text-align:right;font-weight:bold;");
			lc.setParent(item);

			lc = new Listcell();
			Label totCurrWaived = new Label(PennantApplicationUtil.amountFormate(totCurrWaivedAmt, ccyFormatter));
			lc.appendChild(totCurrWaived);
			lc.setStyle("text-align:right;font-weight:bold;");
			lc.setParent(item);

			lc = new Listcell(PennantApplicationUtil.amountFormate(totDueWaiver, ccyFormatter));
			lc.setStyle("text-align:right;font-weight:bold;");
			lc.setParent(item);

			lc = new Listcell(PennantApplicationUtil.amountFormate(totGSTWaiver, ccyFormatter));
			lc.setStyle("text-align:right;font-weight:bold;");
			lc.setParent(item);

			lc = new Listcell(PennantApplicationUtil.amountFormate(totNetBal, ccyFormatter));
			lc.setStyle("text-align:right;font-weight:bold;");
			lc.setParent(item);

			this.listFeeWaiverDetails.appendChild(item);
		}
		logger.debug("Leaving");

	}

	public void onChangeCurrWaivedAmount(ForwardEvent event) {
		logger.debug("Entering");

		Decimalbox currWaivedAmt = (Decimalbox) event.getOrigin().getTarget();
		Clients.clearWrongValue(currWaivedAmt);

		BigDecimal amount = PennantApplicationUtil.unFormateAmount(currWaivedAmt.getValue(), ccyFormatter);
		FeeWaiverDetail feeWaiverDetail = (FeeWaiverDetail) currWaivedAmt.getAttribute("object");

		for (FeeWaiverDetail detail : getFeeWaiverDetails()) {

			if (feeWaiverDetail.getAdviseId() == detail.getAdviseId()) {
				// PSD#:145831
				BigDecimal balanceAmount = detail.getReceivableAmount().subtract(detail.getReceivedAmount());
				if (balanceAmount.compareTo(amount) == -1) {

					throw new WrongValueException(currWaivedAmt,
							Labels.getLabel("label_FeeWaiverHeaderDialog_currWaiverAmountErrorMsg.value"));

				} else if (amount.compareTo(BigDecimal.ZERO) == 0) {
					currWaivedAmt.setValue(BigDecimal.ZERO);
					detail.setCurrWaiverAmount(amount);
					detail.setCurrWaiverGST(BigDecimal.ZERO);
					detail.setCurrActualWaiver(BigDecimal.ZERO);
					// Preparing GST
					prepareGST(detail, amount);
					detail.setBalanceAmount(detail.getReceivableAmount().subtract(detail.getCurrWaiverAmount()));
					break;
				} else {
					detail.setCurrWaiverAmount(amount);
					detail.setWaivedAmount(amount);
					// Preparing GST
					prepareGST(detail, amount);
					detail.setBalanceAmount(detail.getReceivableAmount().subtract(detail.getCurrWaiverAmount()));
					break;
				}
			}
		}

		doFillFeeWaiverDetails(getFeeWaiverDetails());

		logger.debug("Leaving");
	}

	private void prepareGST(FeeWaiverDetail wd, BigDecimal waiverAmount) {
		logger.debug(Literal.ENTERING);

		FinanceMain fm = new FinanceMain();
		fm.setFinID(wd.getFinID());
		Map<String, BigDecimal> gstPercentages = GSTCalculator.getTaxPercentages(fm);

		if (wd.isTaxApplicable()) {
			/* Always taking as Inclusive case here */
			TaxAmountSplit taxSplit = GSTCalculator.getInclusiveGST(waiverAmount, gstPercentages);
			GSTCalculator.calculateActualGST(wd, taxSplit, gstPercentages);

			wd.setCurrActualWaiver(waiverAmount.subtract(taxSplit.gettGST()));
			wd.setCurrWaiverGST(taxSplit.gettGST());

			if (wd.getTaxHeader() != null && CollectionUtils.isNotEmpty(wd.getTaxHeader().getTaxDetails())) {
				for (Taxes tax : wd.getTaxHeader().getTaxDetails()) {
					if (RuleConstants.CODE_CGST.equals(tax.getTaxType())) {
						tax.setWaivedTax(taxSplit.getcGST());
					} else if (RuleConstants.CODE_SGST.equals(tax.getTaxType())) {
						tax.setWaivedTax(taxSplit.getsGST());
					} else if (RuleConstants.CODE_IGST.equals(tax.getTaxType())) {
						tax.setWaivedTax(taxSplit.getiGST());
					} else if (RuleConstants.CODE_UGST.equals(tax.getTaxType())) {
						tax.setWaivedTax(taxSplit.getuGST());
					} else if (RuleConstants.CODE_CESS.equals(tax.getTaxType())) {
						tax.setWaivedTax(taxSplit.getCess());
					} else {
						continue;
					}
					tax.setNetTax(tax.getActualTax().subtract(tax.getWaivedTax()));
					tax.setRemFeeTax(tax.getNetTax().subtract(tax.getPaidTax()));
				}
			}
		} else {
			wd.setWaivedAmount(waiverAmount);
			wd.setCurrWaiverAmount(waiverAmount);
			wd.setCurrActualWaiver(waiverAmount);
			wd.setCurrWaiverGST(BigDecimal.ZERO);
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the notes button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$btnNotes(Event event) {
		doShowNotes(this.feeWaiverHeader);
	}

	/**
	 * Refresh the list page with the filters that are applied in list page.
	 */
	protected void refreshList() {
		final JdbcSearchObject<FinanceMain> soFinanceMain = getFinanceSelectCtrl().getSearchObj(true);
		getFinanceSelectCtrl().getPagingFinanceList().setActivePage(0);
		getFinanceSelectCtrl().getPagedListWrapper().setSearchObject(soFinanceMain);
		if (getFinanceSelectCtrl().getListBoxFinance() != null) {
			getFinanceSelectCtrl().getListBoxFinance().getListModel();
		}
	}

	/**
	 * This method is for append finance basic details to respective parent tabs
	 */
	private void appendFinBasicDetails(FinanceMain aFinanceMain) {
		try {
			final Map<String, Object> map = new HashMap<String, Object>();
			map.put("parentCtrl", this);
			map.put("finHeaderList", getHeaderBasicDetails(this.financeMain));
			Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/FinBasicDetails.zul", this.finBasicdetails,
					map);
		} catch (Exception e) {
			logger.debug(e);
		}

	}

	/**
	 * fill finance basic details to List
	 * 
	 * @return
	 */
	private ArrayList<Object> getHeaderBasicDetails(FinanceMain aFinanceMain) {

		ArrayList<Object> arrayList = new ArrayList<Object>();
		Customer customer = getFinanceDetail().getCustomerDetails().getCustomer();
		arrayList.add(0, aFinanceMain.getFinType());
		arrayList.add(1, aFinanceMain.getFinCcy());
		arrayList.add(2, aFinanceMain.getScheduleMethod());
		arrayList.add(3, aFinanceMain.getFinReference());
		arrayList.add(4, aFinanceMain.getProfitDaysBasis());
		arrayList.add(5, null);
		arrayList.add(6, false);
		arrayList.add(7, false);
		arrayList.add(8, null);
		arrayList.add(9, customer == null ? "" : customer.getCustShrtName());
		arrayList.add(10, true);
		arrayList.add(11, null);
		return arrayList;
	}

	@Override
	protected String getReference() {
		return String.valueOf(this.feeWaiverHeader.getWaiverId());
	}

	public FinanceSelectCtrl getFinanceSelectCtrl() {
		return financeSelectCtrl;
	}

	public void setFinanceSelectCtrl(FinanceSelectCtrl financeSelectCtrl) {
		this.financeSelectCtrl = financeSelectCtrl;
	}

	public FinanceMain getFinanceMain() {
		return financeMain;
	}

	public void setFinanceMain(FinanceMain financeMain) {
		this.financeMain = financeMain;
	}

	public FinBasicDetailsCtrl getFinBasicDetailsCtrl() {
		return finBasicDetailsCtrl;
	}

	public void setFinBasicDetailsCtrl(FinBasicDetailsCtrl finBasicDetailsCtrl) {
		this.finBasicDetailsCtrl = finBasicDetailsCtrl;
	}

	public FinanceDetail getFinanceDetail() {
		return financeDetail;
	}

	public void setFinanceDetail(FinanceDetail financeDetail) {
		this.financeDetail = financeDetail;
	}

	public void setFinanceMainDialogCtrl(Object financeMainDialogCtrl) {
		this.financeMainDialogCtrl = financeMainDialogCtrl;
	}

	public Object getFinanceMainDialogCtrl() {
		return financeMainDialogCtrl;
	}

	public FeeWaiverHeader getFeeWaiverHeader() {
		return feeWaiverHeader;
	}

	public void setFeeWaiverHeader(FeeWaiverHeader feeWaiverHeader) {
		this.feeWaiverHeader = feeWaiverHeader;
	}

	public List<FeeWaiverDetail> getFeeWaiverDetails() {
		return feeWaiverDetails;
	}

	public void setFeeWaiverDetails(List<FeeWaiverDetail> feeWaiverDetails) {
		this.feeWaiverDetails = feeWaiverDetails;
	}

	public FeeWaiverHeaderService getFeeWaiverHeaderService() {
		return feeWaiverHeaderService;
	}

	public void setFeeWaiverHeaderService(FeeWaiverHeaderService feeWaiverHeaderService) {
		this.feeWaiverHeaderService = feeWaiverHeaderService;
	}

	public FeeWaiverEnquiryListCtrl getFeeWaiverEnquiryListCtrl() {
		return feeWaiverEnquiryListCtrl;
	}

	public void setFeeWaiverEnquiryListCtrl(FeeWaiverEnquiryListCtrl feeWaiverEnquiryListCtrl) {
		this.feeWaiverEnquiryListCtrl = feeWaiverEnquiryListCtrl;
	}

}
