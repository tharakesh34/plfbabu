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
 * FILE HEADER *
 ********************************************************************************************
 * * FileName : LimitHeaderDialogCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 31-03-2016 * *
 * Modified Date : 31-03-2016 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 31-03-2016 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.webui.limit.limitdetails;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.datatype.DatatypeConfigurationException;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Div;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Row;
import org.zkoss.zul.Space;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.ExtendedCombobox;
import com.pennant.app.constants.ImplementationConstants;
import com.pennant.app.constants.LengthConstants;
import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.applicationmaster.Currency;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.limit.LimitDetails;
import com.pennant.backend.model.limit.LimitHeader;
import com.pennant.backend.model.limit.LimitStructure;
import com.pennant.backend.model.limit.LimitStructureDetail;
import com.pennant.backend.model.rulefactory.LimitFilterQuery;
import com.pennant.backend.service.PagedListService;
import com.pennant.backend.service.limit.LimitDetailService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.LimitConstants;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.backend.util.SMTParameterConstants;
import com.pennant.util.ErrorControl;
import com.pennant.util.PennantAppUtil;
import com.pennant.util.Constraint.PTDateValidator;
import com.pennant.util.Constraint.StaticListValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.ScreenCTL;
import com.pennanttech.pennapps.core.AppException;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.jdbc.search.Filter;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * ************************************************************<br>
 * This is the controller class for the /WEB-INF/pages/Limit/LimitHeader/limitHeaderDialog.zul file. <br>
 * ************************************************************<br>
 */
public class LimitDetailDialogCtrl extends GFCBaseCtrl<LimitHeader> implements Serializable {

	private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getLogger(LimitDetailDialogCtrl.class);

	/*
	 * ************************************************************************ All the components that are defined here
	 * and have a corresponding component with the same 'id' in the zul-file are getting by our 'extends GFCBaseCtrl'
	 * GenericForwardComposer. ************************************************************************
	 */
	protected Window window_LimitHeaderDialog;
	protected Listbox listBoxLimitDetail;
	protected Row row1;

	protected Label customerGroup;
	protected Label customerGroupName;
	protected Label label_CustomerId;

	protected Label customerId;

	protected Label custFullName;
	protected Label custSalutationCode;

	protected Label custDftBranchCode;
	protected Label custDftBranchName;

	protected Row row2;
	protected Label label_ResponsibleBranch;
	protected Label label_Currency;

	protected ExtendedCombobox currency;
	protected Row row3;
	protected Label label_ExpiryDate;
	protected Hbox hlayout_ExpiryDate;
	protected Space space_ExpiryDate;
	protected Listheader listheader_ExpiryDate;

	protected Datebox expiryDate;
	protected Label label_ReviewDate;
	protected Hbox hlayout_ReviewDate;
	protected Space space_ReviewDate;

	protected Datebox reviewDate;
	protected Row row4;
	protected Label label_LimitStructureCode;

	protected ExtendedCombobox limitStructureCode;
	protected Label label_Remarks;
	protected Hbox hlayout_Remarks;
	protected Space space_Remarks;
	protected Checkbox active;
	protected Space space_Active;
	protected Checkbox validateMaturityDate;
	protected Space space_ValidateMaturityDate;

	protected Textbox remarks;
	protected Paging pagingLimitDetailDialog;
	protected ExtendedCombobox limiDialogRule;

	protected Div gb_CustomerDetails;
	protected Div gb_GroupDetails;
	protected Div gb_RuleBased;

	protected Listheader listheader_BankingArrangement;
	protected Listheader listheader_LimitCondition;
	protected Listheader listheader_ExternalReference;
	protected Listheader listheader_ExternalReference1;
	protected Listheader listheader_Tenor;

	private String limitType;
	private Label limiDialogRuleValue;
	private Label window_LimitHeaderDialog_title;

	// not auto wired vars
	private transient LimitHeader limitHeader; // overhanded per param
	private transient LimitDetailListCtrl limitDetailListCtrl; // overhanded per
	// param

	// ServiceDAOs / Domain Classes
	private transient LimitDetailService limitDetailService;
	private transient PagedListService pagedListService;
	int ccyFormat = 0;
	private boolean validationReq = false;

	/**
	 * default constructor.<br>
	 */
	public LimitDetailDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "LimitHeaderDialog";
	}

	// ************************************************* //
	// *************** Component Events **************** //
	// ************************************************* //

	/**
	 * Before binding the data and calling the dialog window we check, if the zul-file is called with a parameter for a
	 * selected LimitHeader object in a Map.
	 * 
	 * @param event
	 */
	public void onCreate$window_LimitHeaderDialog(Event event) {
		logger.debug("Entering" + event.toString());

		// Set the page level components.
		setPageComponents(window_LimitHeaderDialog);
		try {

			setLimitDetailListCtrl((LimitDetailListCtrl) arguments.get("limitDetailListCtrl"));
			this.limitHeader = (LimitHeader) arguments.get("limitHeader");
			limitType = (String) arguments.get("LimitType");
			if (this.limitHeader == null) {
				throw new Exception(Labels.getLabel("error.unhandled"));
			}

			// READ OVERHANDED params !
			if (arguments.containsKey("limitHeader")) {

				LimitHeader befImage = new LimitHeader();
				BeanUtils.copyProperties(this.limitHeader, befImage);
				this.limitHeader.setBefImage(befImage);
				setLimitHeader(this.limitHeader);
			} else {
				setLimitHeader(null);
			}
			doLoadWorkFlow(this.limitHeader.isWorkflow(), this.limitHeader.getWorkflowId(),
					this.limitHeader.getNextTaskId());

			if (isWorkFlowEnabled() && !enqiryModule) {
				this.userAction = setListRecordStatus(this.userAction);
				getUserWorkspace().allocateRoleAuthorities(getRole(), "LimitHeaderDialog");
			} else {
				getUserWorkspace().allocateAuthorities("LimitHeaderDialog");
			}

			/* set components visible dependent of the users rights */
			doCheckRights();

			// READ OVERHANDED params !
			// we get the limitHeaderListWindow controller. So we have access
			// to it and can synchronize the shown data when we do insert, edit
			// or delete limitHeader here.
			if (arguments.containsKey("limitDetailListCtrl")) {

			} else {
				setLimitDetailListCtrl(null);
			}
			// set Field Properties
			doSetFieldProperties();
			doShowDialog(getLimitHeader());
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "edit" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnEdit(Event event) {
		logger.debug("Entering" + event.toString());
		displayComponents(ScreenCTL.SCRN_GNEDT);
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "delete" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 * @throws DatatypeConfigurationException
	 */
	public void onClick$btnDelete(Event event) throws InterruptedException, DatatypeConfigurationException {
		logger.debug("Entering" + event.toString());
		doDelete();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "save" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 * @throws DatatypeConfigurationException
	 */
	public void onClick$btnSave(Event event) throws InterruptedException, DatatypeConfigurationException {
		logger.debug("Entering" + event.toString());
		doSave();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "cancel" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnCancel(Event event) {
		logger.debug("Entering" + event.toString());
		displayComponents(ScreenCTL.SCRN_GNINT);
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
		MessageUtil.showHelpWindow(event, window_LimitHeaderDialog);
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * The Click event is raised when the Close Button control is clicked.
	 * 
	 * @param event An event sent to the event handler of a component.
	 */
	public void onClick$btnClose(Event event) {
		doClose(this.btnSave.isVisible());
	}

	/**
	 * The framework calls this event handler when user clicks the notes button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$btnNotes(Event event) {
		doShowNotes(this.limitHeader);
	}

	/**
	 * OnCheck event
	 * 
	 * @param event
	 * 
	 */
	public void onCheck$active(Event event) {
		logger.debug("Entering");
		Clients.clearWrongValue(this.currency);
		currency.setMandatoryStyle(active.isChecked());
		logger.debug("Leaving");
	}

	public void onFulfill$currency(Event event) {
		logger.debug("Entering");
		Clients.clearWrongValue(this.currency);
		Object dataObject = currency.getObject();
		if (dataObject instanceof String) {
			this.currency.setValue(dataObject.toString());
			this.currency.setDescription("");
		} else {
			Currency currency = (Currency) dataObject;
			if (currency != null) {
				this.currency.setValue(currency.getCcyCode());
				this.currency.setDescription(currency.getCcyDesc());
			}
		}
		logger.debug("Leaving");
	}

	public void onFulfill$limiDialogRule(Event event) {
		Clients.clearWrongValue(this.limiDialogRule);
		logger.debug("Entering");
		Object dataObject = limiDialogRule.getObject();
		if (dataObject instanceof String) {
			this.limiDialogRule.setValue(dataObject.toString());
			this.limiDialogRule.setDescription("");
		} else {
			LimitFilterQuery limitRule = (LimitFilterQuery) dataObject;
			if (limitRule != null) {
				this.limiDialogRule.setValue(limitRule.getQueryCode());
				this.limiDialogRule.setDescription(limitRule.getQueryDesc());
				this.limiDialogRuleValue.setValue(limitRule.getQuerySubCode());
			}
		}
		logger.debug("Leaving");
	}

	public void onFulfill$limitStructureCode(Event event) {
		logger.debug(Literal.ENTERING);
		
		Clients.clearWrongValue(this.limitStructureCode);

		int conf = MessageUtil.YES;

		final String lsCode = StringUtils.trimToNull(getLimitHeader().getLimitStructureCode());

		String msg = null;

		if (lsCode != null) {
			msg = Labels.getLabel("message.Question.Are_you_sure_to_Modify_this_record") + "\n\n --> " + lsCode;
			conf = MessageUtil.confirm(msg);
		}

		if (conf == MessageUtil.YES) {
			processLimitStructure();
		} else {
			this.limitStructureCode.setValue(lsCode);
		}

		logger.debug(Literal.LEAVING);
	}

	private void processLimitStructure() {
		Object dataObject = limitStructureCode.getObject();
		if (dataObject instanceof String) {
			this.limitStructureCode.setValue(dataObject.toString());
			this.limitStructureCode.setDescription("");
			getLimitHeader().setLimitStructureCode("");
			doFillLimitDetailslistbox(new ArrayList<LimitDetails>());
		} else {
			LimitStructure details = (LimitStructure) dataObject;
			if (details != null) {
				this.limitStructureCode.setValue(details.getStructureCode());
				this.limitStructureCode.setDescription(details.getStructureName());
				getLimitHeader().setLimitStructureCode(details.getStructureCode());
				doFillStructureDetails(details.getStructureCode());
			} else {
				// this.limitStructureCode.setValue("");
				this.limitStructureCode.setDescription("");
				getLimitHeader().setLimitStructureCode("");
				doFillLimitDetailslistbox(new ArrayList<LimitDetails>());
			}
		}
	}

	public void onClickExpirydate(ForwardEvent event) {
		logger.debug("Entering" + event.toString());
		Listitem item = (Listitem) event.getOrigin().getTarget().getParent().getParent();
		Datebox expDate = (Datebox) event.getOrigin().getTarget();
		LimitDetails limitDetails = (LimitDetails) item.getAttribute("DATA");
		limitDetails.setExpiryDate(expDate.getValue());
		setLimitstatus(limitDetails);

		logger.debug("Leaving" + event.toString());
	}

	public void onClickSanctionedAmount(ForwardEvent event) {
		logger.debug("Entering" + event.toString());
		Listitem item = (Listitem) event.getOrigin().getTarget().getParent().getParent();
		Decimalbox sanctionAmount = (Decimalbox) event.getOrigin().getTarget();
		LimitDetails limitDetails = (LimitDetails) item.getAttribute("DATA");
		if (sanctionAmount.getValue() == null) {
			sanctionAmount.setValue(BigDecimal.ZERO);
		}
		limitDetails.setLimitSanctioned(sanctionAmount.getValue());
		setLimitstatus(limitDetails);

		logger.debug("Leaving" + event.toString());
	}

	public void onClickLimitCheck(ForwardEvent event) {
		logger.debug("Entering" + event.toString());
		Listitem item = (Listitem) event.getOrigin().getTarget().getParent().getParent();
		Checkbox limitCheck = (Checkbox) event.getOrigin().getTarget();
		LimitDetails limitDetails = (LimitDetails) item.getAttribute("DATA");
		limitDetails.setLimitCheck(limitCheck.isChecked());
		setLimitstatus(limitDetails);
		logger.debug("Leaving" + event.toString());
	}

	public void onClickRevolving(ForwardEvent event) {
		logger.debug("Entering" + event.toString());
		Listitem item = (Listitem) event.getOrigin().getTarget().getParent().getParent();
		Checkbox revolving = (Checkbox) event.getOrigin().getTarget();
		LimitDetails limitDetails = (LimitDetails) item.getAttribute("DATA");
		limitDetails.setRevolving(revolving.isChecked());
		setLimitstatus(limitDetails);
		logger.debug("Leaving" + event.toString());
	}

	public void onClickActulOrReserved(ForwardEvent event) {
		logger.debug("Entering" + event.toString());
		Listitem item = (Listitem) event.getOrigin().getTarget().getParent().getParent();
		Combobox actualOrResevd = (Combobox) event.getOrigin().getTarget();
		LimitDetails limitDetails = (LimitDetails) item.getAttribute("DATA");
		if (actualOrResevd.getSelectedItem() != null && StringUtils.equals(PennantConstants.List_Select,
				actualOrResevd.getSelectedItem().getValue().toString()))
			limitDetails.setLimitChkMethod(actualOrResevd.getSelectedItem().getValue().toString());
		setLimitstatus(limitDetails);
		logger.debug("Leaving" + event.toString());
	}

	// ===================
	public void onClickBankAggrmt(ForwardEvent event) {
		logger.debug("Entering" + event.toString());
		Listitem item = (Listitem) event.getOrigin().getTarget().getParent().getParent();
		Combobox bnkAggrmt = (Combobox) event.getOrigin().getTarget();
		LimitDetails limitDetails = (LimitDetails) item.getAttribute("DATA");
		limitDetails.setBankingArrangement(bnkAggrmt.getSelectedItem().getValue());
		setLimitstatus(limitDetails);
		logger.debug("Leaving" + event.toString());
	}

	public void onClickLimitCondition(ForwardEvent event) {
		logger.debug("Entering" + event.toString());
		Listitem item = (Listitem) event.getOrigin().getTarget().getParent().getParent();
		Combobox bnkAggrmt = (Combobox) event.getOrigin().getTarget();
		LimitDetails limitDetails = (LimitDetails) item.getAttribute("DATA");
		limitDetails.setLimitCondition(bnkAggrmt.getSelectedItem().getValue());
		setLimitstatus(limitDetails);
		logger.debug("Leaving" + event.toString());
	}

	public void onClickReference(ForwardEvent event) {
		logger.debug("Entering" + event.toString());
		Listitem item = (Listitem) event.getOrigin().getTarget().getParent().getParent();
		Textbox reference = (Textbox) event.getOrigin().getTarget();
		LimitDetails limitDetails = (LimitDetails) item.getAttribute("DATA");
		limitDetails.setExternalRef(reference.getValue());
		setLimitstatus(limitDetails);
		logger.debug("Leaving" + event.toString());
	}

	public void onClickReference1(ForwardEvent event) {
		logger.debug("Entering" + event.toString());
		Listitem item = (Listitem) event.getOrigin().getTarget().getParent().getParent();
		Textbox reference1 = (Textbox) event.getOrigin().getTarget();
		LimitDetails limitDetails = (LimitDetails) item.getAttribute("DATA");
		limitDetails.setExternalRef1(reference1.getValue());
		setLimitstatus(limitDetails);
		logger.debug("Leaving" + event.toString());
	}

	public void onClicktenor(ForwardEvent event) {
		logger.debug("Entering" + event.toString());
		Listitem item = (Listitem) event.getOrigin().getTarget().getParent().getParent();
		Intbox tenor = (Intbox) event.getOrigin().getTarget();
		LimitDetails limitDetails = (LimitDetails) item.getAttribute("DATA");
		limitDetails.setTenor(tenor.getValue());
		setLimitstatus(limitDetails);
		logger.debug("Leaving" + event.toString());
	}

	// ****************************************************************+
	// ************************ GUI operations ************************+
	// ****************************************************************+

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the readOnly mode accordingly.
	 * 
	 * @param aLimitHeader
	 * @throws InterruptedException
	 */
	public void doShowDialog(LimitHeader aLimitHeader) throws InterruptedException {
		logger.debug("Entering");
		try {
			// fill the components with the data
			doWriteBeanToComponents(aLimitHeader);
			// set ReadOnly mode accordingly if the object is new or not.
			displayComponents(ScreenCTL.getMode(enqiryModule, isWorkFlowEnabled(), aLimitHeader.isNewRecord()));
			// stores the initial data for comparing if they are changed during
			// user action.
			setDialog(DialogType.EMBEDDED);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving");
	}

	// 1 Enquiry
	// 2 New Record
	// 3 InitEdit
	// 4 EditMode
	// 5 WorkFlow Add
	// 6 WorkFlow Edit
	private void displayComponents(int mode) {
		logger.debug("Entering");

		doReadOnly(ScreenCTL.initButtons(mode, this.btnCtrl, this.btnNotes, isWorkFlowEnabled(), isFirstTask(),
				this.userAction, this.limitStructureCode, this.limitStructureCode));
		if (!getLimitHeader().isNewRecord()) {
			setExtAccess("LimitHeaderDialog_LimitStructureCode", true, this.limiDialogRule, row2);
			setExtAccess("LimitHeaderDialog_LimitStructureCode", true, this.limitStructureCode, row4);
		}
		logger.debug("Leaving");
	}

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly(boolean readOnly) {
		logger.debug("Entering");

		boolean tempReadOnly = readOnly;
		if (readOnly) {
			tempReadOnly = true;
		} else if (PennantConstants.RECORD_TYPE_DEL.equals(this.limitHeader.getRecordType())) {
			tempReadOnly = true;
		}
		/*
		 * setExtAccess("LimitHeaderDialog_ResponsibleBranch", tempReadOnly, this.responsibleBranch, row2);
		 */
		setExtAccess("LimitHeaderDialog_LimitStructureCode", tempReadOnly, this.limiDialogRule, row2);
		setExtAccess("LimitHeaderDialog_Currency", tempReadOnly, this.currency, row2);
		setComponentAccessType("LimitHeaderDialog_ExpiryDate", tempReadOnly, expiryDate, null, label_ExpiryDate);
		setComponentAccessType("LimitHeaderDialog_ReviewDate", tempReadOnly, reviewDate, null, label_ReviewDate);
		setComponentAccessType("LimitHeaderDialog_ReviewDate", tempReadOnly, active, null, label_ReviewDate);
		setComponentAccessType("LimitHeaderDialog_ReviewDate", tempReadOnly, validateMaturityDate, null,
				label_ReviewDate);
		setExtAccess("LimitHeaderDialog_LimitStructureCode", tempReadOnly, this.limitStructureCode, row4);
		readOnlyComponent(getUserWorkspace().isReadOnly("LimitHeaderDialog_Remarks"), remarks);
		btnDelete.setVisible(false);
		this.currency.setMandatoryStyle(active.isChecked());
		logger.debug("Leaving");
	}

	// ****************************************************************+
	// ****************************++ helpers ************************++
	// ****************************************************************+

	/**
	 * User rights check. <br>
	 * Only components are set visible=true if the logged-in <br>
	 * user have the right for it. <br>
	 * 
	 * The rights are get from the spring framework users grantedAuthority(). A right is only a string. <br>
	 */
	private void doCheckRights() {
		logger.debug("Entering");

		if (!enqiryModule) {
			getUserWorkspace().allocateAuthorities("LimitHeaderDialog", getRole());
			this.btnNew.setVisible(getUserWorkspace().isAllowed("button_LimitHeaderDialog_btnNew"));
			this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_LimitHeaderDialog_btnEdit"));
			this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_LimitHeaderDialog_btnDelete"));
			this.btnSave.setVisible(getUserWorkspace().isAllowed("button_LimitHeaderDialog_btnSave"));
		}

		logger.debug("Leaving");
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");
		// Empty sent any required attributes

		this.currency.setMaxlength(LengthConstants.LEN_CURRENCY);
		this.currency.setTextBoxWidth(121);
		this.currency.setMandatoryStyle(active.isChecked());
		this.currency.setModuleName("Currency");
		this.currency.setValueColumn("CcyCode");
		this.currency.setDescColumn("CcyDesc");
		this.currency.setValidateColumns(new String[] { "CcyCode" });

		this.expiryDate.setFormat(PennantConstants.dateFormat);
		this.reviewDate.setFormat(PennantConstants.dateFormat);

		this.limitStructureCode.setMandatoryStyle(true);
		this.limitStructureCode.setModuleName("LimitStructure");
		this.limitStructureCode.setValueColumn("StructureCode");
		this.limitStructureCode.setDescColumn("StructureName");
		this.limitStructureCode.setValidateColumns(new String[] { "StructureCode" });
		Filter[] filters = null;
		if (getLimitHeader().isNewRecord()) {
			filters = new Filter[1];
			// filters[1] = new Filter("Active", "1");
		} else {
			filters = new Filter[1];
		}

		if (StringUtils.equals(LimitConstants.LIMIT_RULE, limitType)) {
			filters[0] = new Filter("LimitCategory", LimitConstants.LIMIT_CATEGORY_BANK);

			this.limiDialogRule.setModuleName("LimitFilterQuery");
			this.limiDialogRule.setValueColumn("QueryCode");
			this.limiDialogRule.setDescColumn("QueryDesc");
			this.limiDialogRule.setValidateColumns(new String[] { "QueryCode" });

			List<String> existingGroups = new ArrayList<>();// PennantappUtil.getLimitHeaderCustomer(false, true);
			Filter[] filtersRulebased = new Filter[1];
			filtersRulebased[0] = new Filter("QueryCode", existingGroups, Filter.OP_NOT_IN);
			if (existingGroups != null && existingGroups.size() > 0) {
				this.limiDialogRule.setFilters(filtersRulebased);
			}
			gb_CustomerDetails.setVisible(false);
			gb_GroupDetails.setVisible(false);
			gb_RuleBased.setVisible(true);
			// row3.setVisible(false);
			listheader_ExpiryDate.setVisible(false);
			window_LimitHeaderDialog_title.setValue(Labels.getLabel("window_LimitHeaderDialogRule.title"));
			this.listBoxLimitDetail.setHeight(getListBoxHeight(9));
		} else if (getLimitHeader().getCustomerId() != 0) {
			filters[0] = new Filter("LimitCategory", LimitConstants.LIMIT_CATEGORY_CUST);
			gb_CustomerDetails.setVisible(true);
			gb_GroupDetails.setVisible(false);
			gb_RuleBased.setVisible(false);
			row3.setVisible(true);
			this.listBoxLimitDetail.setHeight(getListBoxHeight(8));
		} else {
			filters[0] = new Filter("LimitCategory", LimitConstants.LIMIT_CATEGORY_CUST);
			gb_CustomerDetails.setVisible(false);
			gb_GroupDetails.setVisible(true);
			gb_RuleBased.setVisible(false);
			row3.setVisible(true);
			this.listBoxLimitDetail.setHeight(getListBoxHeight(9));
		}
		this.limitStructureCode.setFilters(filters);
		this.remarks.setMaxlength(1000);

		boolean isVisible = SysParamUtil.isAllowed(SMTParameterConstants.LIMIT_ADDTNAL_FIELDS_REQ);
		this.listheader_BankingArrangement.setVisible(isVisible);
		this.listheader_LimitCondition.setVisible(isVisible);
		this.listheader_ExternalReference.setVisible(isVisible);
		this.listheader_ExternalReference1.setVisible(isVisible);
		this.listheader_Tenor.setVisible(isVisible);

		setStatusDetails();
		logger.debug("Leaving");
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aLimitHeader LimitHeader
	 * @throws DatatypeConfigurationException
	 */
	public void doWriteBeanToComponents(LimitHeader aLimitHeader) {
		logger.debug("Entering");
		ccyFormat = CurrencyUtil.getFormat(aLimitHeader.getLimitCcy());
		this.customerGroup.setValue(aLimitHeader.getCustGrpCode());
		this.customerGroupName.setValue(aLimitHeader.getGroupName());
		this.customerId.setValue(aLimitHeader.getCustCIF());
		custFullName.setValue(aLimitHeader.getCustFullName());
		// custSalutationCode.setValue(aLimitHeader.getCustSalutationCode());
		custDftBranchCode.setValue(aLimitHeader.getCustDftBranch());
		aLimitHeader.setResponsibleBranch(aLimitHeader.getCustDftBranch());
		custDftBranchName.setValue(aLimitHeader.getResponsibleBranchName());
		this.limiDialogRule.setValue(aLimitHeader.getRuleCode());
		limiDialogRuleValue.setValue(aLimitHeader.getRuleValue());
		if (aLimitHeader.getLimitExpiryDate() != null) {
			this.expiryDate.setValue(aLimitHeader.getLimitExpiryDate());
		}
		if (aLimitHeader.getLimitRvwDate() != null) {
			this.reviewDate.setValue(aLimitHeader.getLimitRvwDate());
		}
		this.limitStructureCode.setValue(aLimitHeader.getLimitStructureCode());
		this.remarks.setValue(aLimitHeader.getLimitSetupRemarks());
		this.active.setChecked(aLimitHeader.isActive());
		this.validateMaturityDate.setChecked(aLimitHeader.isValidateMaturityDate());
		if (!aLimitHeader.isNewRecord()) {
			this.limitStructureCode.setDescription(aLimitHeader.getStructureName());
			this.limiDialogRule.setDescription(aLimitHeader.getQueryDesc());

		} else {
			aLimitHeader.setLimitCcy(SysParamUtil.getAppCurrency());
			this.active.setChecked(true);
		}

		this.recordStatus.setValue(aLimitHeader.getRecordStatus());
		this.currency.setValue(aLimitHeader.getLimitCcy());
		this.currency.setDescription(aLimitHeader.getCcyDesc());

		doFillLimitDetailslistbox(aLimitHeader.getCustomerLimitDetailsList());
		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aLimitHeader
	 */
	public void doWriteComponentsToBean(LimitHeader aLimitHeader) {
		logger.debug("Entering");

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();
		if (aLimitHeader.isNewRecord()) {
			aLimitHeader.setNewRecord(true);
			aLimitHeader.setCreatedBy(getUserWorkspace().getLoggedInUser().getUserId());
			aLimitHeader.setCreatedOn(new Timestamp(System.currentTimeMillis()));
		}

		if (custDftBranchCode.getValue() != null)
			aLimitHeader.setResponsibleBranch(custDftBranchCode.getValue());
		else {
			aLimitHeader.setResponsibleBranch("");
		}

		if (gb_CustomerDetails.isVisible()) {
			aLimitHeader.setCustomerGroup(0);
		} else if (gb_GroupDetails.isVisible()) {
			aLimitHeader.setCustomerId(0);
		}

		// Currency
		try {
			if (this.currency.getValue().equals("")) {
				if (validationReq && active.isChecked()) {
					throw new WrongValueException(this.currency, Labels.getLabel("FIELD_IS_MAND",
							new String[] { Labels.getLabel("label_LimitHeaderDialog_Currency.value") }));
				} else {
					aLimitHeader.setLimitCcy("");
				}
			} else {
				aLimitHeader.setLimitCcy(this.currency.getValue());
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Expiry Date
		try {
			aLimitHeader.setLimitExpiryDate(expiryDate.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Review Date
		try {
			aLimitHeader.setLimitRvwDate(this.reviewDate.getValue());
			if (validationReq && (reviewDate.getValue() != null && expiryDate.getValue() != null)) {
				if (reviewDate.getValue().after(expiryDate.getValue())) {
					wve.add(new WrongValueException(reviewDate, "Review Date should be before the Expiry Date"));
				}
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Limit Structure Code

		try {

			if (validationReq && this.limitStructureCode.getValue().equals("")) {
				throw new WrongValueException(this.limitStructureCode, Labels.getLabel("FIELD_IS_MAND",
						new String[] { Labels.getLabel("label_LimitHeaderDialog_LimitStructureCode.value") }));
			} else {
				aLimitHeader.setLimitStructureCode(this.limitStructureCode.getValue());
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Limit filter check
		if (gb_RuleBased.isVisible()) {
			try {
				if (validationReq && this.limiDialogRule.getValue().equals("")) {
					throw new WrongValueException(this.limiDialogRule, Labels.getLabel("FIELD_IS_MAND",
							new String[] { Labels.getLabel("label_LimitDetailsList_RuleCode.value") }));
				} else {
					aLimitHeader.setRuleCode(this.limiDialogRule.getValue());
					aLimitHeader.setRuleValue(this.limiDialogRuleValue.getValue());
				}
			} catch (WrongValueException we) {
				wve.add(we);
			}
		}
		// Remarks
		try {
			aLimitHeader.setLimitSetupRemarks(this.remarks.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Active
		try {
			aLimitHeader.setActive(this.active.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// ValidateMaturityDate
		try {
			aLimitHeader.setValidateMaturityDate(this.validateMaturityDate.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		ccyFormat = CurrencyUtil.getFormat(aLimitHeader.getLimitCcy());

		if (validationReq) {
			validateLimitSetup(aLimitHeader, wve);
		}

		if (wve.isEmpty()) {
			setCustomerLimitList(aLimitHeader);
		}

		doRemoveValidation();

		if (!wve.isEmpty()) {
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = (WrongValueException) wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}
		logger.debug("Leaving");
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug("Entering");

		doClearMessage();

		// ExpiryDate
		if (!this.expiryDate.isReadonly() && active.isChecked()) {
			this.expiryDate.setConstraint(new PTDateValidator(
					Labels.getLabel("label_LimitHeaderDialog_ExpiryDate.value"), false, true, null, true));
		}
		// Review Date
		if (!this.reviewDate.isReadonly() && active.isChecked()) {

			Date appDate = SysParamUtil.getAppDate();
			Date nextYear = DateUtil.addYears(appDate, 1);
			this.reviewDate.setConstraint(new PTDateValidator(
					Labels.getLabel("label_LimitHeaderDialog_ReviewDate.value"), false, true, nextYear, true));
		}

		logger.debug("Leaving");
	}

	/**
	 * Remove the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering");
		this.currency.setConstraint("");
		this.expiryDate.setConstraint("");
		this.reviewDate.setConstraint("");
		this.limitStructureCode.setConstraint("");
		this.remarks.setConstraint("");
		logger.debug("Leaving");
	}

	/**
	 * Remove Error Messages for Fields
	 */

	protected void doClearMessage() {
		logger.debug("Entering");
		if (StringUtils.equals(LimitConstants.LIMIT_RULE, limitType))
			this.limiDialogRule.setErrorMessage("");
		this.currency.setErrorMessage("");
		this.expiryDate.setErrorMessage("");
		this.reviewDate.setErrorMessage("");
		this.limitStructureCode.setErrorMessage("");
		this.remarks.setErrorMessage("");
		logger.debug("Leaving");
	}

	/**
	 * Method for Refreshing List after Save/Delete a Record
	 */
	protected void refreshList() {
		final JdbcSearchObject<LimitHeader> soLimitHeader = getLimitDetailListCtrl().getSearchObj();
		getLimitDetailListCtrl().pagingLimitDetailsList.setActivePage(0);
		getLimitDetailListCtrl().getPagedListWrapper().setSearchObject(soLimitHeader);
		if (getLimitDetailListCtrl().listBoxLimitDetails != null) {
			getLimitDetailListCtrl().listBoxLimitDetails.getListModel();
		}
	}

	private void doFillStructureDetails(String limitStructureCode) {
		List<LimitStructureDetail> structureDetails = PennantAppUtil.getLimitstructuredetails(limitStructureCode);
		List<LimitDetails> limitDetailsList = new ArrayList<LimitDetails>();

		for (LimitStructureDetail limitStructureDetail : structureDetails) {
			LimitDetails customerlimits = new LimitDetails();
			customerlimits.setGroupCode(limitStructureDetail.getGroupCode());
			customerlimits.setLimitLine(limitStructureDetail.getLimitLine());
			customerlimits.setDisplayStyle(limitStructureDetail.getDisplayStyle());
			customerlimits.setItemSeq(limitStructureDetail.getItemSeq());
			customerlimits.setItemLevel(limitStructureDetail.getItemLevel());
			customerlimits.setItemPriority(limitStructureDetail.getItemPriority());
			customerlimits.setLimitLineDesc(limitStructureDetail.getLimitLineDesc());
			customerlimits.setGroupName(limitStructureDetail.getGroupName());
			customerlimits.setEditable(limitStructureDetail.isEditable());
			customerlimits.setLimitStructureDetailsID(limitStructureDetail.getLimitStructureDetailsID());
			if (getLimitHeader().getLimitExpiryDate() != null) {
				customerlimits.setExpiryDate(getLimitHeader().getLimitExpiryDate());
			}
			customerlimits.setRecordType(PennantConstants.RECORD_TYPE_NEW);
			customerlimits.setRecordStatus("");
			if (StringUtils.equals(LimitConstants.LIMIT_RULE, limitType)) {
				customerlimits.setLimitCheck(false);
			} else {
				customerlimits.setLimitCheck(limitStructureDetail.isLimitCheck());
			}
			customerlimits.setLimitChkMethod((getLimitHeader().getRuleCode() != null ? "A" : "R"));
			// customerlimits.setBankingArrangement();
			customerlimits.setNewRecord(true);

			limitDetailsList.add(customerlimits);
		}
		doFillLimitDetailslistbox(limitDetailsList);
	}

	private void doFillLimitDetailslistbox(List<LimitDetails> limitDetaillist) {
		logger.debug("Entering");
		this.listBoxLimitDetail.getItems().clear();
		if (limitDetaillist != null) {
			for (LimitDetails limitDetails : limitDetaillist) {

				long strdId = limitDetails.getLimitStructureDetailsID();
				Listitem item = new Listitem();

				if (limitDetails.getDisplayStyle() != null) {
					item.setStyle(PennantStaticListUtil.getLimitDetailStyle(limitDetails.getDisplayStyle()));
				}

				String indent = "";
				for (int i = 0; i < limitDetails.getItemLevel(); i++) {
					if (indent.isEmpty())
						indent = "|___";
					else {
						indent = indent + "___";
					}
				}

				String val = "";
				if (limitDetails.getGroupCode() == null) {

					if (StringUtils.equals(LimitConstants.LIMIT_ITEM_UNCLSFD, limitDetails.getLimitLine())) {
						val = "Unclassified";
					} else {
						val = indent + limitDetails.getLimitLineDesc();
					}
				} else {

					if (StringUtils.equals(LimitConstants.LIMIT_ITEM_TOTAL, limitDetails.getGroupCode())) {
						val = "Total";
					} else
						val = indent + limitDetails.getGroupName();
				}

				Listcell lc = new Listcell(val);
				lc.setParent(item);

				// ===============
				lc = new Listcell();

				Datebox expryDate = new Datebox();
				expryDate.setFormat(PennantConstants.dateFormat);
				expryDate.setId("ExpireDate_" + strdId);
				expryDate.addForward("onChange", self, "onClickExpirydate");
				if (limitDetails.getExpiryDate() == null) {
					expryDate.setValue(expiryDate.getValue());
				} else {
					expryDate.setValue(limitDetails.getExpiryDate());
				}
				expryDate.setParent(lc);

				expryDate.setDisabled(getUserWorkspace().isReadOnly("LimitHeaderDialog_Remarks"));
				if (active.isChecked()) {
					expryDate.setConstraint(new PTDateValidator(
							Labels.getLabel("label_LimitHeaderDialog_ExpiryDate.value"), false, true, null, true));
				}
				lc.setParent(item);
				// =================

				lc = new Listcell();

				Checkbox limitCheck = new Checkbox();
				limitCheck.setId("Limitcheck_" + strdId);
				limitCheck.addForward("onClick", self, "onClickLimitCheck");
				limitCheck.setChecked(limitDetails.isLimitCheck());
				limitCheck.setParent(lc);
				if (StringUtils.equals(LimitConstants.LIMIT_RULE, limitType)) {
					limitCheck.setDisabled(
							!limitDetails.isEditable() || getUserWorkspace().isReadOnly("LimitHeaderDialog_Remarks")
									|| !(ImplementationConstants.ONLINE_IRL_CHECK));
				} else {
					limitCheck.setDisabled(
							!limitDetails.isEditable() || getUserWorkspace().isReadOnly("LimitHeaderDialog_Remarks"));
				}
				lc.setParent(item);
				// =================
				lc = new Listcell();
				Checkbox revolving = new Checkbox();
				revolving.setChecked(limitDetails.isRevolving());
				revolving.addForward("onClick", self, "onClickRevolving");
				readOnlyComponent(!limitDetails.isEditable()
						|| getUserWorkspace().isReadOnly("LimitHeaderDialog_Remarks") || enqiryModule, revolving);
				if (limitDetails.getLimitLine() != null
						&& !(StringUtils.equals(LimitConstants.LIMIT_ITEM_UNCLSFD, limitDetails.getLimitLine()))) {
					lc.appendChild(revolving);
				} else {
					lc.appendChild(new Space());
				}

				revolving.setAttribute("Data", limitDetails);
				lc.setParent(item);
				// =================

				lc = new Listcell();
				Combobox actulOrReserved = new Combobox();
				actulOrReserved.setId("ActulOrReserved_" + strdId);
				actulOrReserved.setReadonly(true);
				actulOrReserved.addForward("onClick", self, "onClickActulOrReserved");
				fillComboBox(actulOrReserved, limitDetails.getLimitChkMethod(),
						PennantStaticListUtil.getLimitcheckTypes(), "");
				actulOrReserved.setConstraint(new StaticListValidator(PennantStaticListUtil.getLimitcheckTypes(),
						Labels.getLabel("listheader_ReservedOrActual.label")));
				actulOrReserved.setStyle("background:none;");
				actulOrReserved.setParent(lc);
				actulOrReserved.setDisabled(getUserWorkspace().isReadOnly("LimitHeaderDialog_Remarks"));

				lc.setParent(item);
				// ====================
				Decimalbox limitSanctioned = new Decimalbox();
				limitSanctioned.setDisabled(getUserWorkspace().isReadOnly("LimitHeaderDialog_Remarks"));

				lc = new Listcell();
				limitSanctioned.setMaxlength(21);
				limitSanctioned.setId("Amount_" + strdId);
				limitSanctioned.addForward("onChange", self, "onClickSanctionedAmount");
				limitSanctioned
						.setValue(PennantApplicationUtil.formateAmount(limitDetails.getLimitSanctioned(), ccyFormat));
				limitSanctioned.setFormat(PennantApplicationUtil.getAmountFormate(0));
				limitSanctioned.setScale(0);

				limitSanctioned.setParent(lc);
				limitSanctioned.setWidth("150px");
				lc.setParent(item);
				// ====================

				BigDecimal reserved = BigDecimal.ZERO;
				BigDecimal actualExpo = BigDecimal.ZERO;
				BigDecimal reservedExpo = BigDecimal.ZERO;
				BigDecimal avilable = BigDecimal.ZERO;
				BigDecimal osPriBal = BigDecimal.ZERO;

				if (limitDetails.getReservedLimit() != null) {
					reserved = limitDetails.getReservedLimit();
				}
				if (limitDetails.getActualexposure() != null) {
					actualExpo = limitDetails.getActualexposure();
				}
				if (limitDetails.getReservedexposure() != null) {
					reservedExpo = limitDetails.getReservedexposure();
				}

				if (limitDetails.getOsPriBal() != null) {
					osPriBal = limitDetails.getOsPriBal();
				}

				avilable = limitDetails.getLimitSanctioned().subtract(limitDetails.getUtilisedLimit());

				lc = new Listcell(PennantApplicationUtil.amountFormate(reserved, ccyFormat));
				lc.setParent(item);

				lc = new Listcell(PennantApplicationUtil.amountFormate(actualExpo, ccyFormat));
				lc.setParent(item);

				lc = new Listcell(PennantApplicationUtil.amountFormate(reservedExpo, ccyFormat));
				lc.setParent(item);

				lc = new Listcell(PennantApplicationUtil.amountFormate(osPriBal, ccyFormat));
				lc.setParent(item);

				lc = new Listcell(PennantApplicationUtil.amountFormate(avilable, ccyFormat));
				lc.setParent(item);

				// Bank Agreement
				lc = new Listcell();
				Combobox bnkAggrmt = new Combobox();
				bnkAggrmt.setId("BankingArrangement_" + strdId);
				bnkAggrmt.addForward("onClick", self, "onClickBankAggrmt");
				fillComboBox(bnkAggrmt, limitDetails.getBankingArrangement(),
						PennantStaticListUtil.getBankingArrangement(), "");
				bnkAggrmt.setStyle("background:none;");
				bnkAggrmt.setParent(lc);
				bnkAggrmt.setDisabled(getUserWorkspace().isReadOnly("LimitHeaderDialog_Remarks"));
				lc.setParent(item);

				// Limit Condition
				lc = new Listcell();
				Combobox lmtCondition = new Combobox();
				lmtCondition.setId("LimitCondition_" + strdId);
				lmtCondition.addForward("onClick", self, "onClickLimitCondition");
				fillComboBox(lmtCondition, limitDetails.getLimitCondition(), PennantStaticListUtil.getLimitCondition(),
						"");

				lmtCondition.setStyle("background:none;");
				lmtCondition.setParent(lc);
				lmtCondition.setDisabled(getUserWorkspace().isReadOnly("LimitHeaderDialog_Remarks"));
				lc.setParent(item);

				// Reference
				Textbox reference = new Textbox();
				lc = new Listcell();
				reference.setId("ExternalReference_" + strdId);
				reference.addForward("onClick", self, "onClickReference");
				reference.setValue(limitDetails.getExternalRef());
				reference.setParent(lc);
				reference.setReadonly(getUserWorkspace().isReadOnly("LimitHeaderDialog_Remarks"));
				reference.setWidth("100px");
				lc.setParent(item);

				// Reference
				Textbox reference1 = new Textbox();
				lc = new Listcell();
				reference1.setId("ExternalReference1_" + strdId);
				reference1.addForward("onClick", self, "onClickReference1");
				reference1.setValue(limitDetails.getExternalRef1());
				reference1.setParent(lc);
				reference1.setReadonly(getUserWorkspace().isReadOnly("LimitHeaderDialog_Remarks"));
				reference1.setWidth("100px");
				lc.setParent(item);

				// Tenor
				Intbox tenor = new Intbox();
				lc = new Listcell();
				tenor.setId("Tenor_" + strdId);
				tenor.setReadonly(true);
				tenor.addForward("onClick", self, "onClicktenor");
				tenor.setValue(limitDetails.getTenor());
				tenor.setParent(lc);
				tenor.setReadonly(getUserWorkspace().isReadOnly("LimitHeaderDialog_Remarks"));
				tenor.setWidth("100px");
				lc.setParent(item);

				item.setAttribute("DATA", limitDetails);
				this.listBoxLimitDetail.appendChild(item);
			}
		}
		logger.debug("Leaving");
	}

	private void doDelete() throws InterruptedException, DatatypeConfigurationException {
		logger.debug(Literal.ENTERING);

		final LimitHeader aLimitHeader = new LimitHeader();
		BeanUtils.copyProperties(getLimitHeader(), aLimitHeader);

		doDelete(String.valueOf(aLimitHeader.getHeaderId()), aLimitHeader);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Clears the components values. <br>
	 */
	public void doClear() {
		logger.debug("Entering");
		// remove validation, if there are a save before
		this.currency.setValue("");
		this.expiryDate.setText("");
		this.reviewDate.setText("");
		this.limitStructureCode.setValue("");
		this.remarks.setValue("");
		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 * @throws DatatypeConfigurationException
	 */
	public void doSave() throws InterruptedException, DatatypeConfigurationException {
		logger.debug("Entering");

		final LimitHeader aLimitHeader = new LimitHeader();
		BeanUtils.copyProperties(getLimitHeader(), aLimitHeader);
		boolean isNew = false;
		validationReq = false;

		if (isWorkFlowEnabled()) {
			aLimitHeader.setRecordStatus(userAction.getSelectedItem().getValue().toString());
			getWorkFlowDetails(userAction.getSelectedItem().getLabel(), aLimitHeader.getNextTaskId(), aLimitHeader);
		}

		// force validation, if on, than execute by component.getValue()
		if (!PennantConstants.RECORD_TYPE_DEL.equals(aLimitHeader.getRecordType()) && isValidation()
				&& !"Cancel".equalsIgnoreCase(this.userAction.getSelectedItem().getLabel())
				&& !"Reject".equalsIgnoreCase(this.userAction.getSelectedItem().getLabel())
				&& !"Resubmit".equalsIgnoreCase(this.userAction.getSelectedItem().getLabel())) {

			validationReq = true;
			doSetValidation();
		}

		// *************************************************************
		// force validation, if on, than execute by component.getValue()
		// *************************************************************
		if (!PennantConstants.RECORD_TYPE_DEL.equals(aLimitHeader.getRecordType()) && isValidation()) {
			// fill the LimitHeader object with the components data
			doWriteComponentsToBean(aLimitHeader);
		}

		isNew = aLimitHeader.isNewRecord();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.trimToEmpty(aLimitHeader.getRecordType()).equals("")) {
				aLimitHeader.setVersion(aLimitHeader.getVersion() + 1);
				if (isNew) {
					aLimitHeader.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aLimitHeader.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aLimitHeader.setNewRecord(true);
				}
			}
		} else {
			aLimitHeader.setVersion(aLimitHeader.getVersion() + 1);
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}

		// save it to database
		try {
			if (doProcess(aLimitHeader, tranType)) {
				refreshList();

				// Confirmation message
				String reference = "";
				if (StringUtils.isNotBlank(aLimitHeader.getCustCIF())) {
					reference = aLimitHeader.getCustCIF();
				} else if (StringUtils.isNotBlank(aLimitHeader.getRuleCode())) {
					reference = aLimitHeader.getRuleCode();
				} else {
					reference = aLimitHeader.getCustGrpCode();
				}

				String msg = PennantApplicationUtil.getSavingStatus(aLimitHeader.getRoleCode(),
						aLimitHeader.getNextRoleCode(), reference, " Limit Setup ", aLimitHeader.getRecordStatus());
				Clients.showNotification(msg, "info", null, null, -1);

				closeDialog();
			}
		} catch (final AppException e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving");
	}

	/**
	 * Set the workFlow Details List to Object
	 * 
	 * @param aAuthorizedSignatoryRepository (AuthorizedSignatoryRepository)
	 * 
	 * @param tranType                       (String)
	 * 
	 * @return boolean
	 * @throws DatatypeConfigurationException
	 * 
	 */
	protected boolean doProcess(LimitHeader aLimitHeader, String tranType) throws DatatypeConfigurationException {
		logger.debug("Entering");
		boolean processCompleted = false;
		aLimitHeader.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		aLimitHeader.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aLimitHeader.setUserDetails(getUserWorkspace().getLoggedInUser());
		if (aLimitHeader.isNewRecord()) {
			aLimitHeader.setCreatedBy(getUserWorkspace().getLoggedInUser().getUserId());
			aLimitHeader.setCreatedOn(new Timestamp(System.currentTimeMillis()));
		}
		if (isWorkFlowEnabled()) {

			if (!"Save".equals(userAction.getSelectedItem().getLabel())) {
				if (auditingReq) {
					if (!notesEntered) {
						MessageUtil.showError(Labels.getLabel("Notes_NotEmpty"));
						return false;
					}
				}
			}
			aLimitHeader.setTaskId(getTaskId());
			aLimitHeader.setNextTaskId(getNextTaskId());
			aLimitHeader.setRoleCode(getRole());
			aLimitHeader.setNextRoleCode(getNextRoleCode());
			if (StringUtils.trimToEmpty(getOperationRefs()).equals("")) {
				processCompleted = doSaveProcess(getAuditHeader(aLimitHeader, tranType), null);
			} else {
				String[] list = getOperationRefs().split(";");
				AuditHeader auditHeader = getAuditHeader(aLimitHeader, PennantConstants.TRAN_WF);
				for (int i = 0; i < list.length; i++) {
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		} else {
			processCompleted = doSaveProcess(getAuditHeader(aLimitHeader, tranType), null);
		}
		logger.debug("return value :" + processCompleted);
		logger.debug("Leaving");
		return processCompleted;
	}

	/**
	 * Get the result after processing DataBase Operations
	 * 
	 * @param AuditHeader auditHeader
	 * @param method      (String)
	 * @return boolean
	 * 
	 */
	private boolean doSaveProcess(AuditHeader auditHeader, String method) {
		logger.debug("Entering");
		boolean processCompleted = false;
		int retValue = PennantConstants.porcessOVERIDE;
		boolean deleteNotes = false;
		LimitHeader aLimitHeader = (LimitHeader) auditHeader.getAuditDetail().getModelData();

		while (retValue == PennantConstants.porcessOVERIDE) {
			if (StringUtils.trimToEmpty(method).equalsIgnoreCase("")) {
				if (PennantConstants.TRAN_DEL.equals(auditHeader.getAuditTranType())) {
					auditHeader = getLimitDetailService().delete(auditHeader);
					deleteNotes = true;
				} else {
					auditHeader = getLimitDetailService().saveOrUpdate(auditHeader);
				}
			} else {
				if (PennantConstants.method_doApprove.equalsIgnoreCase(StringUtils.trimToEmpty(method))) {
					auditHeader = getLimitDetailService().doApprove(auditHeader, true);
					if (PennantConstants.RECORD_TYPE_DEL.equals(aLimitHeader.getRecordType())) {
						deleteNotes = true;
					}
				} else if (PennantConstants.method_doReject.equalsIgnoreCase(StringUtils.trimToEmpty(method))) {
					auditHeader = getLimitDetailService().doReject(auditHeader);
					if (PennantConstants.RECORD_TYPE_NEW.equals(aLimitHeader.getRecordType())) {
						deleteNotes = true;
					}
				} else {
					auditHeader.setErrorDetails(
							new ErrorDetail(PennantConstants.ERR_9999, Labels.getLabel("InvalidWorkFlowMethod"), null));
					retValue = ErrorControl.showErrorControl(this.window_LimitHeaderDialog, auditHeader);
					return processCompleted;
				}
			}
			auditHeader = ErrorControl.showErrorDetails(this.window_LimitHeaderDialog, auditHeader);
			retValue = auditHeader.getProcessStatus();
			if (retValue == PennantConstants.porcessCONTINUE) {
				processCompleted = true;
				if (deleteNotes) {
					deleteNotes(getNotes(this.limitHeader), true);
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
		logger.debug("return Value:" + processCompleted);
		logger.debug("Leaving");
		return processCompleted;
	}

	private void setCustomerLimitList(LimitHeader aLimitHeader) {

		String limitCccy = aLimitHeader.getLimitCcy();
		List<LimitDetails> limitDetails = new ArrayList<LimitDetails>();

		List<Listitem> listtoprocess = this.listBoxLimitDetail.getItems();
		for (Listitem item : listtoprocess) {
			LimitDetails limit = (LimitDetails) item.getAttribute("DATA");
			long strdId = limit.getLimitStructureDetailsID();
			Datebox expireDate = (Datebox) item.getFellowIfAny("ExpireDate_" + strdId);
			Decimalbox amountBox = (Decimalbox) item.getFellowIfAny("Amount_" + strdId);
			Checkbox limitCheck = (Checkbox) item.getFellowIfAny("Limitcheck_" + strdId);
			Combobox actulOrReserved = (Combobox) item.getFellowIfAny("ActulOrReserved_" + strdId);
			Combobox bnkAggrmt = (Combobox) item.getFellowIfAny("BankingArrangement_" + strdId);
			Combobox lmtCondition = (Combobox) item.getFellowIfAny("LimitCondition_" + strdId);
			Textbox reference = (Textbox) item.getFellowIfAny("ExternalReference_" + strdId);
			Textbox reference1 = (Textbox) item.getFellowIfAny("ExternalReference1_" + strdId);
			Intbox tenor = (Intbox) item.getFellowIfAny("Tenor_" + strdId);
			expireDate.setErrorMessage("");
			amountBox.setErrorMessage("");
			actulOrReserved.setErrorMessage("");
			if (expireDate != null) {
				limit.setExpiryDate(expireDate.getValue());
			}

			BigDecimal sactioned = amountBox.getValue();
			if (sactioned != null) {
				limit.setLimitSanctioned(
						PennantApplicationUtil.unFormateAmount(sactioned, CurrencyUtil.getFormat(limitCccy)));
			}

			limit.setLimitCheck(limitCheck.isChecked());

			String checkMethod = "R";
			if (actulOrReserved.getSelectedItem() != null && actulOrReserved.getSelectedItem().getValue() != null) {
				checkMethod = actulOrReserved.getSelectedItem().getValue();
			}
			limit.setLimitChkMethod(checkMethod);

			limit.setBankingArrangement(bnkAggrmt.getSelectedItem().getValue());
			limit.setLimitCondition(lmtCondition.getSelectedItem().getValue());
			limit.setExternalRef(reference.getValue());
			limit.setExternalRef1(reference1.getValue());
			limit.setTenor(tenor.intValue());

			limitDetails.add(limit);
		}
		aLimitHeader.setCustomerLimitDetailsList(limitDetails);
	}

	private void validateLimitSetup(LimitHeader aLimitHeader, ArrayList<WrongValueException> wve) {
		logger.debug(" Entering ");

		Map<String, List<String>> groupLineMap = new HashMap<String, List<String>>();
		List<Listitem> listtoprocess = this.listBoxLimitDetail.getItems();

		Date lineMaxExpDate = SysParamUtil.getAppDate();
		for (Listitem item : listtoprocess) {
			LimitDetails limit = (LimitDetails) item.getAttribute("DATA");
			long strdId = limit.getLimitStructureDetailsID();
			Datebox expireDate = (Datebox) item.getFellowIfAny("ExpireDate_" + strdId);

			if (expireDate != null) {
				Date lineExpDate = expireDate.getValue();
				if (lineExpDate != null && lineMaxExpDate != null) {
					if (lineExpDate.compareTo(lineMaxExpDate) >= 0) {
						lineMaxExpDate = lineExpDate;
					}
				}
			}

			if (!StringUtils.isEmpty(limit.getGroupCode())) {
				if (StringUtils.equals(LimitConstants.LIMIT_ITEM_TOTAL, limit.getGroupCode())) {
					continue;
				}

				if (!groupLineMap.containsKey(limit.getGroupCode())) {
					groupLineMap.put(limit.getGroupCode(),
							getLimitDetailService().getLinesForGroup(limit.getGroupCode()));
				}
			}
		}

		if (this.expiryDate.getValue() != null && lineMaxExpDate != null) {
			if (this.expiryDate.getValue().compareTo(lineMaxExpDate) < 0) {
				wve.add(new WrongValueException(this.expiryDate,
						Labels.getLabel("DATE_ALLOWED_MINDATE_EQUAL",
								new String[] { Labels.getLabel("label_LimitHeaderDialog_ExpiryDate.value"),
										DateUtil.formatToShortDate(lineMaxExpDate) })));
			}
		}

		// validate group and lines
		for (Listitem item : listtoprocess) {
			LimitDetails limit = (LimitDetails) item.getAttribute("DATA");

			long strdId = limit.getLimitStructureDetailsID();
			Decimalbox amountBox = (Decimalbox) item.getFellowIfAny("Amount_" + strdId);
			amountBox.setErrorMessage("");
			BigDecimal sactioned = amountBox.getValue();

			if (StringUtils.equals(LimitConstants.LIMIT_ITEM_TOTAL, limit.getGroupCode())) {
				continue;
			}

			if (!StringUtils.isEmpty(limit.getGroupCode())) {
				List<String> validList = groupLineMap.get(limit.getGroupCode());
				// validateChild
				BigDecimal childMaxAmount = getMaxSactionedAmt(validList, listtoprocess);

				if (sactioned.compareTo(childMaxAmount) < 0) {
					if (!amountBox.isReadonly() && !amountBox.isDisabled()) {
						wve.add(new WrongValueException(amountBox,
								Labels.getLabel("Limit_Group_Total_Max", new String[] { limit.getGroupName() })));
					}
				}
			}
		}

		// validate total
		BigDecimal total = BigDecimal.ZERO;
		BigDecimal maxofGroups = BigDecimal.ZERO;
		Decimalbox totalBox = null;
		for (Listitem item : listtoprocess) {
			LimitDetails limit = (LimitDetails) item.getAttribute("DATA");
			long strdId = limit.getLimitStructureDetailsID();
			Decimalbox amountBox = (Decimalbox) item.getFellowIfAny("Amount_" + strdId);
			amountBox.setErrorMessage("");
			BigDecimal sactioned = amountBox.getValue();

			if (StringUtils.equals(LimitConstants.LIMIT_ITEM_TOTAL, limit.getGroupCode())) {
				total = sactioned;
				totalBox = amountBox;
				continue;
			}

			Set<String> totalGroups = groupLineMap.keySet();
			if (!StringUtils.isEmpty(limit.getGroupCode()) && totalGroups.contains(limit.getGroupCode())) {
				if (maxofGroups.compareTo(sactioned) <= 0) {
					maxofGroups = sactioned;
				}
			}
			if (StringUtils.equals(limit.getLimitLine(), LimitConstants.LIMIT_ITEM_UNCLSFD)) {
				if (maxofGroups.compareTo(sactioned) <= 0) {
					maxofGroups = sactioned;
				}
			}
		}

		if (total.compareTo(maxofGroups) < 0 && totalBox != null) {
			if (!totalBox.isReadonly() && !totalBox.isDisabled()) {
				wve.add(new WrongValueException(totalBox,
						Labels.getLabel("Limit_Group_Total_Max", new String[] { "Total" })));
			}

		}

		logger.debug(" Leaving ");

	}

	private BigDecimal getMaxSactionedAmt(List<String> validList, List<Listitem> listtoprocess) {

		BigDecimal childMaxAmount = BigDecimal.ZERO;

		for (Listitem listitem : listtoprocess) {
			LimitDetails limit = (LimitDetails) listitem.getAttribute("DATA");
			long strdId = limit.getLimitStructureDetailsID();

			String code = "";
			if (!StringUtils.isBlank(limit.getGroupCode())) {
				code = limit.getGroupCode();
			}
			if (!StringUtils.isBlank(limit.getLimitLine())) {
				code = limit.getLimitLine();
			}
			if (!validList.contains(code)) {
				continue;
			}

			Decimalbox amountBox = (Decimalbox) listitem.getFellowIfAny("Amount_" + strdId);
			BigDecimal childAmount = amountBox.getValue();
			if (childMaxAmount.compareTo(childAmount) <= 0) {
				childMaxAmount = childAmount;
			}
		}
		return childMaxAmount;

	}

	/**
	 * @param limitDetails
	 */
	private void setLimitstatus(LimitDetails limitDetails) {
		logger.debug(" Entering ");
		boolean isNew = limitDetails.isNewRecord();
		if (isWorkFlowEnabled()) {
			if (StringUtils.trimToEmpty(limitDetails.getRecordType()).equals("")) {
				limitDetails.setVersion(limitDetails.getVersion() + 1);
				if (isNew) {
					limitDetails.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					limitDetails.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					limitDetails.setNewRecord(true);
				}
			}
		}
		logger.debug(" Leaving ");
	}

	// ******************************************************//
	// ***************** WorkFlow Components*****************//
	// ******************************************************//

	/**
	 * @param aAuthorizedSignatoryRepository
	 * @param tranType
	 * @return
	 */

	private AuditHeader getAuditHeader(LimitHeader aLimitHeader, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aLimitHeader.getBefImage(), aLimitHeader);
		return new AuditHeader(String.valueOf(aLimitHeader.getHeaderId()), null, null, null, auditDetail,
				aLimitHeader.getUserDetails(), getOverideMap());
	}

	@Override
	protected String getReference() {
		return String.valueOf(getLimitHeader().getHeaderId());
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public LimitHeader getLimitHeader() {
		return this.limitHeader;
	}

	public void setLimitHeader(LimitHeader limitHeader) {
		this.limitHeader = limitHeader;
	}

	public PagedListService getPagedListService() {
		return pagedListService;
	}

	public void setPagedListService(PagedListService pagedListService) {
		this.pagedListService = pagedListService;
	}

	public LimitDetailService getLimitDetailService() {
		return limitDetailService;
	}

	public void setLimitDetailService(LimitDetailService limitDetailService) {
		this.limitDetailService = limitDetailService;
	}

	public LimitDetailListCtrl getLimitDetailListCtrl() {
		return limitDetailListCtrl;
	}

	public void setLimitDetailListCtrl(LimitDetailListCtrl limitDetailListCtrl) {
		this.limitDetailListCtrl = limitDetailListCtrl;
	}

}
