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
 * * FileName : CollateralSetupDialogCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 13-12-2016 * *
 * Modified Date : 13-12-2016 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 13-12-2016 PENNANT 0.1 * * 13-12-2016 Srinivasa Varma 0.2 Development Item 82 * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.webui.collateral.collateralsetup;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataAccessException;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.HtmlBasedComponent;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Longbox;
import org.zkoss.zul.Space;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabbox;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Tabpanels;
import org.zkoss.zul.Tabs;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.CurrencyBox;
import com.pennant.ExtendedCombobox;
import com.pennant.FrequencyBox;
import com.pennant.app.constants.HolidayHandlerTypes;
import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.FrequencyUtil;
import com.pennant.app.util.ReferenceUtil;
import com.pennant.app.util.RuleExecutionUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.applicationmaster.Currency;
import com.pennant.backend.model.applicationmasters.Flag;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.collateral.AssignmentDetails;
import com.pennant.backend.model.collateral.CoOwnerDetail;
import com.pennant.backend.model.collateral.CollateralMovement;
import com.pennant.backend.model.collateral.CollateralSetup;
import com.pennant.backend.model.collateral.CollateralStructure;
import com.pennant.backend.model.collateral.CollateralThirdParty;
import com.pennant.backend.model.documentdetails.DocumentDetails;
import com.pennant.backend.model.extendedfield.ExtendedFieldHeader;
import com.pennant.backend.model.extendedfield.ExtendedFieldRender;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.financemanagement.FinFlagsDetail;
import com.pennant.backend.model.lmtmasters.FinanceCheckListReference;
import com.pennant.backend.model.lmtmasters.FinanceReferenceDetail;
import com.pennant.backend.model.solutionfactory.ExtendedFieldDetail;
import com.pennant.backend.service.collateral.CollateralSetupService;
import com.pennant.backend.service.lmtmasters.FinanceReferenceDetailService;
import com.pennant.backend.util.AssetConstants;
import com.pennant.backend.util.CollateralConstants;
import com.pennant.backend.util.CommitmentConstants;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.NotificationConstants;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.backend.util.RuleReturnType;
import com.pennant.component.Uppercasebox;
import com.pennant.core.EventManager.Notify;
import com.pennant.util.ErrorControl;
import com.pennant.util.PennantAppUtil;
import com.pennant.util.Constraint.PTDateValidator;
import com.pennant.util.Constraint.PTDecimalValidator;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.collateral.collateralassignment.CollateralAssignmentDialogCtrl;
import com.pennant.webui.customermasters.customer.CustomerDialogCtrl;
import com.pennant.webui.finance.financemain.AgreementDetailDialogCtrl;
import com.pennant.webui.finance.financemain.DocumentDetailDialogCtrl;
import com.pennant.webui.lmtmasters.financechecklistreference.FinanceCheckListReferenceDialogCtrl;
import com.pennant.webui.solutionfactory.extendedfielddetail.ExtendedFieldRenderDialogCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.searchdialogs.ExtendedMultipleSearchListBox;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.core.util.DateUtil.DateFormat;
import com.pennanttech.pennapps.notification.Notification;
import com.pennanttech.pennapps.pff.document.DocumentCategories;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pff.notifications.service.NotificationService;

/**
 * This is the controller class for the /WEB-INF/pages/collateral/CollateralSetup/collateralSetupDialog.zul file. <br>
 */
public class CollateralSetupDialogCtrl extends GFCBaseCtrl<CollateralSetup> {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getLogger(CollateralSetupDialogCtrl.class);

	protected Window window_CollateralSetupDialog;

	protected Uppercasebox collateralRef;
	protected Textbox depositorCif;
	protected Label depositerName;
	protected Longbox depositorId;
	protected Textbox collateralType;
	protected Label collateralTypeName;
	protected ExtendedCombobox collateralCcy;
	protected Intbox numberOfUnits;
	protected CurrencyBox unitPrice;
	protected CurrencyBox collateralValue;
	protected CurrencyBox maxCollateralValue;
	protected Decimalbox bankLtv;
	protected Decimalbox specialLTV;
	protected CurrencyBox bankValuation;
	protected Textbox collateralLoc;
	protected Textbox valuator;
	protected Datebox expiryDate;
	protected FrequencyBox reviewFrequency;
	protected Datebox nextReviewDate;
	protected Checkbox multiLoanAssignment;
	protected Checkbox thirdPartyAssignment;
	protected Intbox assignedLoansCount;
	protected CurrencyBox assignedCollateralValue;
	protected Decimalbox assignedPercToCollateralValue;
	protected Decimalbox assignedPercToBankValuation;
	protected Textbox remarks;
	protected Tabbox tabBoxIndexCenter;
	protected Tabs tabsIndexCenter;
	protected Tabpanels tabpanelsBoxIndexCenter;
	protected Tab basicDetailsTab;
	protected Tab extendedDetailsTab;
	protected Tabpanel extendedFieldTabpanel;
	protected Tab assignmentDetailTab;
	protected Tab movementsTab;
	protected Listbox listBoxCoownerDetail;
	protected Button btnAddCoownerDetails;
	protected Button btnAddThirdPartyDetail;
	protected Listbox listBoxAssignmentDetail;
	protected Listbox listBoxMovements;
	protected Listbox listBoxThirdParty;
	protected Component checkListChildWindow;
	protected Button viewInfo;
	protected Groupbox gb_ThirdPartyAssignDetails;
	protected Button btnFlagDetails;
	protected Uppercasebox flagDetails;
	protected Datebox cersaiRegDate;
	protected Datebox cersaiModificationDate;
	protected Datebox cersaiSatisfactionDate;
	protected Combobox regStatus;
	protected Textbox assetId;
	protected Textbox siId;

	protected Label label_DepositorCif;
	protected Label label_CollateralRef;
	protected Label label_DepositorName;
	protected Label label_Currency;
	protected Label label_CollateralType;
	protected Label label_CollateralLoc;
	protected Decimalbox label_assignedBankValuation;
	protected Decimalbox label_assignedColllValue;
	protected Decimalbox label_assignedPercBankValuation;
	protected Decimalbox label_availableForAssignment;

	protected Label label_Movement_DepositorCif;
	protected Label label_Movement_CollateralRef;
	protected Label label_Movement_DepositorName;
	protected Label label_Movement_Currency;
	protected Label label_Movement_CollateralType;
	protected Label label_Movement_CollateralLoc;

	protected Space space_SpecialLTV;
	protected Space space_CollateralLoc;
	protected Space space_CollateralValuator;

	private CollateralSetup collateralSetup;
	private transient CollateralSetupListCtrl collateralSetupListCtrl;
	private transient CollateralSetupService collateralSetupService;
	private NotificationService notificationService;
	private FinanceReferenceDetailService financeReferenceDetailService;

	private transient CustomerDialogCtrl customerDialogCtrl;
	private transient ExtendedFieldRenderDialogCtrl extendedFieldRenderDialogCtrl;
	private transient DocumentDetailDialogCtrl documentDetailDialogCtrl;
	private transient AgreementDetailDialogCtrl agreementDetailDialogCtrl;
	private transient FinanceCheckListReferenceDialogCtrl financeCheckListReferenceDialogCtrl;
	private List<CoOwnerDetail> coOwnerDetailList = null;
	private List<CollateralThirdParty> collateralThirdPartyList = null;
	private List<FinFlagsDetail> finFlagsDetailList = null;

	protected String selectMethodName = "onSelectTab";
	private String moduleType = "";
	private String module = "";

	private List<ExtendedFieldRender> extendedFieldRenderList = new ArrayList<ExtendedFieldRender>();
	private List<FinanceCheckListReference> collateralChecklists = null;
	private Map<Long, Long> selectedAnsCountMap = null;
	protected Map<String, Object> flagTypeDataMap = new HashMap<String, Object>();

	private transient CollateralAssignmentDialogCtrl collateralAssignmentDialogCtrl;
	private transient FinanceDetail financeDetail;
	private boolean fromLoan = false;
	private boolean newRecord = false;

	/**
	 * default constructor.<br>
	 */
	public CollateralSetupDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "CollateralSetupDialog";
		super.moduleCode = "CollateralSetup";
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the dialog window we check, if the zul-file is called with a parameter for a
	 * selected CollateralSetup object in a Map.
	 * 
	 * @param event
	 */
	public void onCreate$window_CollateralSetupDialog(Event event) {
		logger.debug("Entring" + event.toString());

		// Set the page level components.
		setPageComponents(window_CollateralSetupDialog);

		try {
			// Get the required arguments.
			this.collateralSetup = (CollateralSetup) arguments.get("collateralSetup");

			if (this.collateralSetup == null) {
				throw new Exception(Labels.getLabel("error.unhandled"));
			}

			if (arguments.containsKey("module")) {
				this.module = (String) arguments.get("module");
			}

			if (arguments.containsKey("moduleType")) {
				this.moduleType = (String) arguments.get("moduleType");
			}

			if (PennantConstants.MODULETYPE_ENQ.equals(moduleType)) {
				enqiryModule = true;
			}
			// Store the before image.
			CollateralSetup collateralSetup = new CollateralSetup();
			BeanUtils.copyProperties(this.collateralSetup, collateralSetup);
			this.collateralSetup.setBefImage(collateralSetup);

			if (arguments.containsKey("fromLoan")) {
				this.fromLoan = (boolean) arguments.get("fromLoan");
				if (arguments.containsKey("newRecord")) {
					this.newRecord = (boolean) arguments.get("newRecord");
				}
			}

			if (fromLoan) {
				setCollateralAssignmentDialogCtrl((CollateralAssignmentDialogCtrl) arguments.get("dialogCtrl"));
			} else {
				this.collateralSetupListCtrl = (CollateralSetupListCtrl) arguments.get("collateralSetupListCtrl");
			}

			if (arguments.containsKey("financeDetail")) {
				setFinanceDetail((FinanceDetail) arguments.get("financeDetail"));
			}
			if (arguments.containsKey("roleCode")) {
				setRole((String) arguments.get("roleCode"));
				getUserWorkspace().allocateRoleAuthorities(getRole(), this.pageRightName);
			}

			if (!"E".equals(module) && !fromLoan) {
				// Render the page and display the data.
				doLoadWorkFlow(this.collateralSetup.isWorkflow(), this.collateralSetup.getWorkflowId(),
						this.collateralSetup.getNextTaskId());
			}

			if (isWorkFlowEnabled() && !enqiryModule) {
				if (!fromLoan) {
					this.userAction = setListRecordStatus(this.userAction);
				}
				getUserWorkspace().allocateRoleAuthorities(getRole(), this.pageRightName);
			}

			// Set Listbox heights...
			this.listBoxAssignmentDetail.setHeight(borderLayoutHeight - 210 + "px");
			this.listBoxMovements.setHeight(borderLayoutHeight - 210 + "px");

			if ("E".equals(module)) {
				moduleType = PennantConstants.MODULETYPE_ENQ;
				enqiryModule = true;
			}

			doSetFieldProperties();
			doCheckRights();
			doShowDialog(this.collateralSetup);

		} catch (Exception e) {
			closeDialog();
			MessageUtil.showError(e);
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

		if (!enqiryModule) {
			getUserWorkspace().allocateAuthorities(this.pageRightName, getRole());
			this.btnNew.setVisible(getUserWorkspace().isAllowed("button_CollateralSetupDialog_btnNew"));
			this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_CollateralSetupDialog_btnDelete"));
			this.btnSave.setVisible(getUserWorkspace().isAllowed("button_CollateralSetupDialog_btnSave"));

			this.btnAddCoownerDetails
					.setVisible(getUserWorkspace().isAllowed("button_CollateralSetupDialog_btnAddCoownerDetails"));
			this.btnAddThirdPartyDetail
					.setVisible(getUserWorkspace().isAllowed("button_CollateralSetupDialog_btnAddThirdPartyDetail"));
			this.btnFlagDetails.setVisible(getUserWorkspace().isAllowed("button_CollateralSetupDialog_btnFlagDetails"));
		}

		logger.debug("Leaving");
	}

	/**
	 * Method for Adding Flags into Multi Selection Extended box
	 * 
	 * @param event
	 */
	@SuppressWarnings("unchecked")
	public void onClick$btnFlagDetails(Event event) {
		logger.debug("Entering  " + event.toString());

		String[] flagCodes = this.flagDetails.getValue().split(",");
		for (int i = 0; i < flagCodes.length; i++) {
			this.flagTypeDataMap.put(flagCodes[i], null);
		}
		Object dataObject = ExtendedMultipleSearchListBox.show(this.window_CollateralSetupDialog, "Flag",
				this.flagTypeDataMap);
		if (dataObject instanceof String) {
			this.flagDetails.setValue(dataObject.toString());
			this.flagDetails.setTooltiptext("");
		} else {
			Map<String, Object> details = (Map<String, Object>) dataObject;
			if (details != null) {
				String tempflagcode = details.keySet().toString();
				tempflagcode = tempflagcode.replace("[", " ").replace("]", "").replace(" ", "");
				if (tempflagcode.startsWith(",")) {
					tempflagcode = tempflagcode.substring(1);
				}
				if (tempflagcode.endsWith(",")) {
					tempflagcode = tempflagcode.substring(0, tempflagcode.length() - 1);
				}
				this.flagDetails.setValue(tempflagcode);
			}

			// Setting tooltip with Descriptions
			String toolTipDesc = "";
			for (String key : details.keySet()) {
				Object obj = (Object) details.get(key);
				if (obj instanceof String) {
					// Do Nothing
				} else {
					Flag flagType = (Flag) obj;
					if (flagType != null) {
						toolTipDesc = toolTipDesc.concat(flagType.getFlagDesc() + " , ");
					}
				}
			}
			if (StringUtils.isNotBlank(toolTipDesc) && toolTipDesc.endsWith(", ")) {
				toolTipDesc = toolTipDesc.substring(0, toolTipDesc.length() - 2);
			}
			this.flagDetails.setTooltiptext(toolTipDesc);
		}

		logger.debug("Leaving " + event.toString());
	}

	/**
	 * The framework calls this event handler when user clicks the save button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$btnSave(Event event) {
		if (fromLoan) {
			doSaveFinCollaterals();
		} else {
			doSave();
		}
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
	 * The framework calls this event handler when user clicks the delete button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$btnDelete(Event event) {
		doDelete();
	}

	/**
	 * The framework calls this event handler when user clicks the cancel button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 * @throws InterruptedException
	 */
	public void onClick$btnCancel(Event event) throws InterruptedException {
		doCancel();
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
	 * when the "edit" button is clicked. <br>
	 * 
	 * @param event
	 */

	/**
	 * Cancel the actual operation. <br>
	 * <br>
	 * Resets to the original status.<br>
	 * 
	 * @throws InterruptedException
	 * 
	 */
	private void doCancel() throws InterruptedException {
		logger.debug("Entering");
		doWriteBeanToComponents(this.collateralSetup.getBefImage());
		doReadOnly();
		this.btnCtrl.setInitEdit();
		this.btnCancel.setVisible(false);
		logger.debug("Leaving");
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the readOnly mode accordingly.
	 * 
	 * @param aCollateralSetup
	 * @throws InterruptedException
	 */
	public void doShowDialog(CollateralSetup collateralSetup) throws InterruptedException {
		logger.debug("Entering");

		// set ReadOnly mode accordingly if the object is new or not.
		if (collateralSetup.isNewRecord()) {
			this.btnCtrl.setInitNew();
			doEdit();
			this.collateralRef.focus();
		} else {
			if (isWorkFlowEnabled()) {
				this.maxCollateralValue.focus();
				if (StringUtils.isNotBlank(collateralSetup.getRecordType())) {
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
			this.btnCtrl.setBtnStatus_Enquiry();
			this.btnNotes.setVisible(false);
		}
		// fill the components with the data
		doWriteBeanToComponents(collateralSetup);
		if (fromLoan || enqiryModule) {
			this.btnDelete.setVisible(false);
			this.btnNotes.setVisible(false);
			this.window_CollateralSetupDialog.setHeight("85%");
			this.window_CollateralSetupDialog.setWidth("100%");
			this.groupboxWf.setVisible(false);
			this.window_CollateralSetupDialog.doModal();
		} else if ("E".equals(module)) {
			this.window_CollateralSetupDialog.setWidth("100%");
			this.window_CollateralSetupDialog.setHeight("100%");
			this.groupboxWf.setVisible(false);
			setDialog(DialogType.EMBEDDED);
		} else {
			this.window_CollateralSetupDialog.setWidth("100%");
			this.window_CollateralSetupDialog.setHeight("100%");
			setDialog(DialogType.EMBEDDED);
		}
		logger.debug("Leaving");
	}

	/**
	 * Method Used for set list of values been class to components finance flags list
	 * 
	 * @param Collateral
	 */
	private void doFillFinFlagsList(List<FinFlagsDetail> finFlagsDetailList) {
		logger.debug("Entering");
		setFinFlagsDetailList(finFlagsDetailList);
		if (finFlagsDetailList == null || finFlagsDetailList.isEmpty()) {
			return;
		}

		String tempflagcode = "";
		for (FinFlagsDetail finFlagsDetail : finFlagsDetailList) {
			if (!StringUtils.equals(finFlagsDetail.getRecordType(), PennantConstants.RECORD_TYPE_DEL)) {
				if (StringUtils.isEmpty(tempflagcode)) {
					tempflagcode = finFlagsDetail.getFlagCode();
				} else {
					tempflagcode = tempflagcode.concat(",").concat(finFlagsDetail.getFlagCode());
				}
			}
		}
		this.flagDetails.setValue(tempflagcode);
		logger.debug("Entering");
	}

	/**
	 * Method for Used for render the Data from List
	 * 
	 * @param finFlagsDetailList
	 */
	private void fetchFlagDetals() {
		logger.debug("Entering");

		List<String> finFlagList = Arrays.asList(this.flagDetails.getValue().split(","));

		if (this.finFlagsDetailList == null) {
			this.finFlagsDetailList = new ArrayList<>();
		}

		Map<String, FinFlagsDetail> flagMap = new HashMap<>();
		for (int i = 0; i < finFlagsDetailList.size(); i++) {
			FinFlagsDetail finFlagsDetail = finFlagsDetailList.get(i);
			flagMap.put(finFlagsDetail.getFlagCode(), finFlagsDetail);
		}

		for (String flagCode : finFlagList) {

			if (StringUtils.isEmpty(flagCode)) {
				continue;
			}

			// Check object is already exists in saved list or not
			if (flagMap.containsKey(flagCode)) {
				// Removing from map to identify existing modifications
				boolean isDelete = false;
				if (this.userAction.getSelectedItem() != null) {
					if ("Cancel".equalsIgnoreCase(this.userAction.getSelectedItem().getLabel())
							|| this.userAction.getSelectedItem().getLabel().contains("Reject")
							|| this.userAction.getSelectedItem().getLabel().contains("Decline")) {
						isDelete = true;
					}
				}
				if (!isDelete) {
					flagMap.remove(flagCode);
				}
			} else {
				FinFlagsDetail afinFlagsDetail = new FinFlagsDetail();
				afinFlagsDetail.setFlagCode(flagCode);
				afinFlagsDetail.setModuleName(CollateralConstants.MODULE_NAME);
				afinFlagsDetail.setNewRecord(true);
				afinFlagsDetail.setVersion(1);
				afinFlagsDetail.setRecordType(PennantConstants.RCD_ADD);

				this.finFlagsDetailList.add(afinFlagsDetail);
			}
		}

		// Removing unavailable records from DB by using Workflow details
		if (flagMap.size() > 0) {
			for (int i = 0; i < finFlagsDetailList.size(); i++) {
				FinFlagsDetail finFlagsDetail = finFlagsDetailList.get(i);
				if (flagMap.containsKey(finFlagsDetail.getFlagCode())) {

					if (StringUtils.isBlank(finFlagsDetail.getRecordType())) {
						finFlagsDetail.setNewRecord(true);
						finFlagsDetail.setVersion(finFlagsDetail.getVersion() + 1);
						finFlagsDetail.setRecordType(PennantConstants.RECORD_TYPE_DEL);
					} else {
						if (!StringUtils.equals(finFlagsDetail.getRecordType(), PennantConstants.RECORD_TYPE_DEL)) {
							finFlagsDetail.setRecordType(PennantConstants.RECORD_TYPE_CAN);
						}
					}
				}
			}
		}

		logger.debug("Leaving");
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aCollateralSetup CollateralSetup
	 * @throws InterruptedException
	 */
	public void doWriteBeanToComponents(CollateralSetup aCollateralSetup) throws InterruptedException {
		logger.debug("Entering");

		this.collateralRef.setValue(aCollateralSetup.getCollateralRef());
		this.depositorCif.setValue(aCollateralSetup.getDepositorCif());
		this.depositorId.setValue(aCollateralSetup.getDepositorId());
		this.depositerName.setValue(aCollateralSetup.getDepositorName());
		this.collateralType.setValue(aCollateralSetup.getCollateralType());
		this.collateralTypeName.setValue(aCollateralSetup.getCollateralTypeName());
		this.collateralCcy.setValue(aCollateralSetup.getCollateralCcy());
		this.collateralCcy
				.setDescription(CurrencyUtil.getCurrencyObject(aCollateralSetup.getCollateralCcy()).getCcyDesc());
		this.specialLTV.setValue(aCollateralSetup.getSpecialLTV());
		this.collateralValue
				.setValue(PennantApplicationUtil.formateAmount(aCollateralSetup.getCollateralValue(), getCcyFormat()));
		this.maxCollateralValue.setValue(
				PennantApplicationUtil.formateAmount(aCollateralSetup.getMaxCollateralValue(), getCcyFormat()));
		this.bankValuation
				.setValue(PennantApplicationUtil.formateAmount(aCollateralSetup.getBankValuation(), getCcyFormat()));
		this.collateralLoc.setValue(aCollateralSetup.getCollateralLoc());
		this.valuator.setValue(aCollateralSetup.getValuator());
		this.expiryDate.setValue(aCollateralSetup.getExpiryDate());
		this.reviewFrequency.setValue(aCollateralSetup.getReviewFrequency());
		this.nextReviewDate.setValue(aCollateralSetup.getNextReviewDate());
		this.multiLoanAssignment.setChecked(aCollateralSetup.isMultiLoanAssignment());
		this.remarks.setValue(aCollateralSetup.getRemarks());
		this.cersaiRegDate.setValue(aCollateralSetup.getRegistrationDate());
		this.cersaiModificationDate.setValue(aCollateralSetup.getModificationDate());
		this.cersaiSatisfactionDate.setValue(aCollateralSetup.getSatisfactionDate());
		this.regStatus.setValue(aCollateralSetup.getRegStatus());
		this.assetId
				.setValue(String.valueOf(aCollateralSetup.getAssetId() == null ? "" : aCollateralSetup.getAssetId()));
		this.siId.setValue(String.valueOf(aCollateralSetup.getSiId() == null ? "" : aCollateralSetup.getSiId()));
		this.thirdPartyAssignment.setChecked(aCollateralSetup.isThirdPartyAssignment());

		if (aCollateralSetup.isNewRecord()) {
			CollateralStructure structure = aCollateralSetup.getCollateralStructure();
			if (StringUtils.equals(structure.getLtvType(), CollateralConstants.FIXED_LTV)) {
				this.bankLtv.setValue(structure.getLtvPercentage());
			} else if (StringUtils.equals(structure.getLtvType(), CollateralConstants.VARIABLE_LTV)) {
				setBankLTV();
			}
		} else {
			this.bankLtv.setValue(aCollateralSetup.getBankLTV());
		}

		// ThirdPartyAssignDetails groupbox
		this.gb_ThirdPartyAssignDetails.setVisible(this.thirdPartyAssignment.isChecked());

		// Reneder CoOwnerDetails
		doFillCoOwnerDetails(aCollateralSetup.getCoOwnerDetailList());

		// Collateral third party details list
		doFillCollateralThirdPartyDetails(aCollateralSetup.getCollateralThirdPartyList());

		// fill the components with the Finance Flags Data and Display
		doFillFinFlagsList(aCollateralSetup.getFinFlagsDetailsList());

		// Extended Field Details
		appendExtendedFieldDetails();

		// Assignment Details
		renderAssignmentDetails();

		// Movement Details
		renderMovementDetails();

		if (!enqiryModule) {

			// Customer Details
			if (!fromLoan) {
				appendCustomerDetailTab(true);
			}

			// Agreements Detail Tab Addition
			appendAgreementsDetailTab(true);

			// CheckList Details Tab Addition
			appendCheckListDetailTab(aCollateralSetup);
		}

		// Document Detail Tab Addition
		appendDocumentDetailTab();

		if (!enqiryModule) {
			// Recommend & Comments Details Tab Addition
			appendRecommendDetailTab(true);
		}

		this.recordStatus.setValue(aCollateralSetup.getRecordStatus());
		logger.debug("Leaving");
	}

	private void setBankLTV() {

		Object ruleResult = null;
		CollateralSetup aCollateralSetup = getCollateralSetup();
		CollateralStructure structure = aCollateralSetup.getCollateralStructure();
		Map<String, Object> declaredMap = new HashMap<>();
		if (aCollateralSetup.getCustomerDetails() != null) {
			declaredMap = aCollateralSetup.getCustomerDetails().getCustomer().getDeclaredFieldValues();
		}
		declaredMap.put("ct_collateralType", aCollateralSetup.getCollateralType());
		declaredMap.put("ct_collateralCcy", aCollateralSetup.getCollateralCcy());

		if (this.financeDetail != null && this.financeDetail.getFinScheduleData().getFinanceMain() != null) {
			FinanceMain fm = this.financeDetail.getFinScheduleData().getFinanceMain();
			int pos = CurrencyUtil.getFormat(fm.getFinCcy());
			declaredMap.put("fm_finCurrentAssetValue",
					PennantApplicationUtil.formateAmount(fm.getFinCurrAssetValue(), pos));
			declaredMap.put("fm_finAssetValue", PennantApplicationUtil.formateAmount(fm.getFinAssetValue(), pos));
			declaredMap.put("fm_finRequestedAmount", PennantApplicationUtil.formateAmount(fm.getReqLoanAmt(), pos));
		} else {
			declaredMap.put("fm_finCurrentAssetValue", 0);
			declaredMap.put("fm_finAssetValue", 0);
			declaredMap.put("fm_finRequestedAmount", 0);
		}
		if (this.extendedFieldRenderList != null && this.extendedFieldRenderList.size() > 0) {

			ExtendedFieldRender fieldValueDetail = extendedFieldRenderList.get(0);
			Map<String, Object> detail = fieldValueDetail.getMapValues();
			String fieldValue = "";
			if (detail.containsKey("PROPERTYTYPE")) {
				fieldValue = (String) detail.get("PROPERTYTYPE");
			}
			declaredMap.put("ct_propertyType", fieldValue);
			// ### 16-05-2018 - Development Item 82
			declaredMap.putAll(
					prepareExtendedData(detail, getCollateralSetup().getCollateralStructure().getExtendedFieldHeader(),
							aCollateralSetup.getCollateralCcy()));
		} else {
			declaredMap.put("ct_propertyType", "");
			// ### 16-05-2018 - Development Item 82
			declaredMap.putAll(prepareExtendedData(new HashMap<>(),
					getCollateralSetup().getCollateralStructure().getExtendedFieldHeader(),
					aCollateralSetup.getCollateralCcy()));
		}

		try {
			ruleResult = RuleExecutionUtil.executeRule(structure.getSQLRule(), declaredMap,
					aCollateralSetup.getCollateralCcy(), RuleReturnType.DECIMAL);
		} catch (Exception e) {
			logger.error("Exception: ", e);
			ruleResult = "0";
		}
		this.bankLtv.setValue(ruleResult == null ? "0" : ruleResult.toString());
	}

	// ### 16-05-2018 - Start- Development Item 82
	private Map<String, Object> prepareExtendedData(Map<String, Object> detail, ExtendedFieldHeader header,
			String ccy) {

		Map<String, Object> declaredMap = new HashMap<>();
		// declaredMap.put("COLLATERAL_GLAP_PROPTYPE", detail.get("PROPTYPE"));
		for (ExtendedFieldDetail fieldDetail : header.getExtendedFieldDetails()) {
			if (fieldDetail.isAllowInRule()) {
				Object value = detail.get(fieldDetail.getFieldName());
				if (StringUtils.equals("CURRENCY", fieldDetail.getFieldType())) {
					value = CurrencyUtil.parse((BigDecimal) value, CurrencyUtil.getFormat(ccy));
				}
				declaredMap.put(fieldDetail.getLovDescModuleName() + "_" + fieldDetail.getLovDescSubModuleName() + "_"
						+ fieldDetail.getFieldName(), value);
			}
		}
		return declaredMap;
	}

	// ### 16-05-2018 - End- Development Item 82
	/**
	 * Creates a page from a zul-file in a tab in the center area of the border layout.
	 */
	private void appendCustomerDetailTab(boolean onLoad) {
		logger.debug("Entering");
		try {
			if (onLoad) {
				createTab(AssetConstants.UNIQUE_ID_CUSTOMERS, true);
			} else {
				Map<String, Object> map = getDefaultArguments();
				map.put("customerDetails", getCollateralSetup().getCustomerDetails());
				map.put("moduleType", moduleType);

				String pageName = PennantAppUtil.getCustomerPageName();
				Executions.createComponents(pageName, getTabpanel(AssetConstants.UNIQUE_ID_CUSTOMERS), map);
			}
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving");
	}

	/**
	 * Method for Rendering Joint account and guaranteer Details Data in finance
	 */
	private void appendAgreementsDetailTab(boolean onLoadProcess) {
		logger.debug("Entering");
		boolean createTab = false;
		if (getCollateralSetup().getAggrements() == null || getCollateralSetup().getAggrements().isEmpty()) {
			createTab = false;
		} else if (onLoadProcess) {
			createTab = true;
		} else if (getTab(AssetConstants.UNIQUE_ID_AGREEMENT) == null) {
			createTab = true;
		}
		if (createTab) {
			createTab(AssetConstants.UNIQUE_ID_AGREEMENT, true);
		} else {
			clearTabpanelChildren(AssetConstants.UNIQUE_ID_AGREEMENT);
			if (getCollateralSetup().getAggrements() != null && !getCollateralSetup().getAggrements().isEmpty()) {
				final Map<String, Object> map = getDefaultArguments();
				map.put("agreementList", getCollateralSetup().getAggrements());
				Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/AgreementDetailDialog.zul",
						getTabpanel(AssetConstants.UNIQUE_ID_AGREEMENT), map);
			}
		}
		logger.debug("Leaving");
	}

	/**
	 * Method for Preparation of Check List Details Window
	 * 
	 * @param collateralSetup
	 * @param finIsNewRecord
	 * @param map
	 */
	private void appendCheckListDetailTab(CollateralSetup collateralSetup) {
		logger.debug("Entering");

		boolean createTab = false;
		if (collateralSetup.getCheckLists() != null && !collateralSetup.getCheckLists().isEmpty()) {
			if (getTab(AssetConstants.UNIQUE_ID_CHECKLIST) == null) {
				createTab = true;
			}
		}

		if (createTab) {
			createTab(AssetConstants.UNIQUE_ID_CHECKLIST, false);
		}

		if (collateralSetup.getCheckLists() != null && !collateralSetup.getCheckLists().isEmpty()) {
			boolean createcheckLsitTab = false;
			for (FinanceReferenceDetail chkList : collateralSetup.getCheckLists()) {
				if (chkList.getShowInStage().contains(getRole())) {
					createcheckLsitTab = true;
					break;
				}
				if (chkList.getAllowInputInStage().contains(getRole())) {
					createcheckLsitTab = true;
					break;
				}
			}
			if (createcheckLsitTab) {
				clearTabpanelChildren(AssetConstants.UNIQUE_ID_CHECKLIST);
				final Map<String, Object> map = getDefaultArguments();
				map.put("checkList", getCollateralSetup().getCheckLists());
				map.put("finCheckRefList", getCollateralSetup().getCollateralCheckLists());

				checkListChildWindow = Executions.createComponents(
						"/WEB-INF/pages/LMTMasters/FinanceCheckListReference/FinanceCheckListReferenceDialog.zul",
						getTabpanel(AssetConstants.UNIQUE_ID_CHECKLIST), map);
				Tab tab = getTab(AssetConstants.UNIQUE_ID_CHECKLIST);
				if (tab != null) {
					tab.setVisible(true);
				}
			}
		}

		logger.debug("Leaving");
	}

	/**
	 * Method for Rendering Document Details Data in finance
	 */
	private void appendDocumentDetailTab() {
		logger.debug("Entering");
		createTab(AssetConstants.UNIQUE_ID_DOCUMENTDETAIL, true);
		final Map<String, Object> map = getDefaultArguments();
		map.put("documentDetails", getCollateralSetup().getDocuments());
		map.put("module", DocumentCategories.COLLATERAL.getKey());
		if (enqiryModule) {
			map.put("enqModule", enqiryModule);
		} else {
			map.put("enqModule", isReadOnly("CollateralSetupDialog_DocumentDetails"));
		}

		Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/DocumentDetailDialog.zul",
				getTabpanel(AssetConstants.UNIQUE_ID_DOCUMENTDETAIL), map);
		logger.debug("Leaving");
	}

	/**
	 * Method for Append Recommend Details Tab
	 */
	private void appendRecommendDetailTab(boolean onLoadProcess) {
		logger.debug("Entering");
		if (onLoadProcess) {
			createTab(AssetConstants.UNIQUE_ID_RECOMMENDATIONS, true);
		} else {
			clearTabpanelChildren(AssetConstants.UNIQUE_ID_RECOMMENDATIONS);
			Map<String, Object> map = getDefaultArguments();
			map.put("isFinanceNotes", true);
			map.put("isRecommendMand", false);
			map.put("control", this);
			map.put("notes", getNotes(this.collateralSetup));
			try {
				Executions.createComponents("/WEB-INF/pages/notes/notes.zul",
						getTabpanel(AssetConstants.UNIQUE_ID_RECOMMENDATIONS), map);
			} catch (Exception e) {
				MessageUtil.showError(e);
			}
		}
		logger.debug("Leaving");
	}

	/**
	 * This method is for append extended field details
	 */
	private void appendExtendedFieldDetails() {
		logger.debug("Entering");
		try {

			// Set Tab Name
			this.extendedDetailsTab
					.setLabel(getCollateralSetup().getCollateralStructure().getExtendedFieldHeader().getTabHeading());

			final Map<String, Object> map = getDefaultArguments();
			map.put("dialogCtrl", this);
			map.put("extendedFieldHeader", getCollateralSetup().getCollateralStructure().getExtendedFieldHeader());
			map.put("finHeaderList", getHeaderBasicDetails());
			map.put("fieldRenderList", getCollateralSetup().getExtendedFieldRenderList());
			map.put("preValidationScript", getCollateralSetup().getCollateralStructure().getPreValidation());
			map.put("postValidationScript", getCollateralSetup().getCollateralStructure().getPostValidation());
			map.put("roleCode", getRole());
			map.put("moduleType", moduleType);
			map.put("queryId", getCollateralSetup().getCollateralStructure().getQueryId());
			map.put("querySubCode", getCollateralSetup().getCollateralStructure().getQuerySubCode());
			map.put("queryCode", getCollateralSetup().getCollateralStructure().getQueryCode());
			if (getCollateralSetup().getCollateralStructure().isMarketableSecurities()) {
				map.put("isCommodity", true);
			}

			Executions.createComponents(
					"/WEB-INF/pages/SolutionFactory/ExtendedFieldDetail/ExtendedFieldRenderDialog.zul",
					extendedFieldTabpanel, map);
		} catch (Exception e) {
			logger.debug(Literal.EXCEPTION, e);
		}
		logger.debug("Leaving");
	}

	@Override
	public String getReference() {
		return this.collateralRef.getValue();
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aCollateralSetup
	 */
	private void doWriteComponentsToBean(CollateralSetup aCollateralSetup) {
		logger.debug("Entering");

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		// Depositor Cif
		try {
			aCollateralSetup.setDepositorCif(this.depositorCif.getValue());
			aCollateralSetup.setDepositorId(this.depositorId.longValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		// Collateral Ref
		try {
			if (StringUtils.trimToNull(this.collateralRef.getValue()) == null) {
				this.collateralRef.setValue(ReferenceUtil.generateCollateralRef());
			}
			aCollateralSetup.setCollateralRef(this.collateralRef.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		// Collateral Type
		try {
			aCollateralSetup.setCollateralType(this.collateralType.getValue());
			aCollateralSetup.setCollateralTypeName(this.collateralTypeName.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		// Collateral Ccy
		try {
			aCollateralSetup.setCollateralCcy(this.collateralCcy.getValidatedValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		// Collateral Value
		try {
			aCollateralSetup.setCollateralValue(
					PennantApplicationUtil.unFormateAmount(this.collateralValue.getActualValue(), getCcyFormat()));
		} catch (WrongValueException we) {
			wve.add(we);
		}

		// Max Collateral value
		try {
			aCollateralSetup.setMaxCollateralValue(
					PennantApplicationUtil.unFormateAmount(this.maxCollateralValue.getActualValue(), getCcyFormat()));
		} catch (WrongValueException we) {
			wve.add(we);
		}

		// Bank LTV
		try {
			if (this.bankLtv.getValue() == null) {
				this.bankLtv.setValue(BigDecimal.ZERO);
			}
			aCollateralSetup.setBankLTV(this.bankLtv.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Special LTV
		try {
			aCollateralSetup.setSpecialLTV(this.specialLTV.getValue());
			BigDecimal maxTtvWaiver = getCollateralSetup().getCollateralStructure().getMaxLtvWaiver() == null
					? BigDecimal.ZERO
					: getCollateralSetup().getCollateralStructure().getMaxLtvWaiver();
			BigDecimal maxLTV = this.bankLtv.getValue().add(maxTtvWaiver);
			if (this.specialLTV.getValue() != null && this.specialLTV.getValue().compareTo(maxLTV) > 0) {
				new WrongValueException(this.specialLTV,
						Labels.getLabel("FIELD_IS_EQUAL_OR_LESSER",
								new String[] { Labels.getLabel("label_CollateralSetupDialog_SpecialLTV.value"),
										PennantApplicationUtil.formatRate(maxLTV.doubleValue(), 2) }));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Bank valuation
		try {
			aCollateralSetup.setBankValuation(
					PennantApplicationUtil.unFormateAmount(this.bankValuation.getActualValue(), getCcyFormat()));
		} catch (WrongValueException we) {
			wve.add(we);
		}

		// Collateral Loc
		try {
			aCollateralSetup.setCollateralLoc(this.collateralLoc.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		// Valuator
		try {
			aCollateralSetup.setValuator(this.valuator.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		// Expiry Date
		try {
			aCollateralSetup.setExpiryDate(this.expiryDate.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		// Review Frequency
		try {
			aCollateralSetup.setReviewFrequency(this.reviewFrequency.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		// Next Review Date
		try {
			if (StringUtils.isNotEmpty(this.reviewFrequency.getValue())) {
				if (this.nextReviewDate.getValue() == null) {
					Date nextRDate = FrequencyUtil.getNextDate(this.reviewFrequency.getValue(), 1,
							SysParamUtil.getAppDate(), HolidayHandlerTypes.MOVE_NONE, false).getNextFrequencyDate();
					this.nextReviewDate.setValue(nextRDate);
				} else {
					if (DateUtil.compare(this.expiryDate.getValue(), this.nextReviewDate.getValue()) != 0
							&& !FrequencyUtil.isFrqDate(this.reviewFrequency.getValue(),
									this.nextReviewDate.getValue())) {
						throw new WrongValueException(this.nextReviewDate,
								Labels.getLabel("FRQ_DATE_MISMATCH", new String[] {
										Labels.getLabel("label_CollateralSetupDialog_NextReviewDate.value"),
										Labels.getLabel("label_CollateralSetupDialog_ReviewFrequency.value") }));
					}

				}
				if (this.expiryDate.getValue() != null) {
					if (DateUtil.compare(this.nextReviewDate.getValue(), this.expiryDate.getValue()) > 0) {
						this.nextReviewDate.setValue(this.expiryDate.getValue());
					}
				}
			}
			aCollateralSetup.setNextReviewDate(this.nextReviewDate.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		// Multi Loan Assignment
		try {
			aCollateralSetup.setMultiLoanAssignment(this.multiLoanAssignment.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		// Third Party Assignment
		try {
			aCollateralSetup.setThirdPartyAssignment(this.thirdPartyAssignment.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		// Remarks
		try {
			aCollateralSetup.setRemarks(this.remarks.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		if (aCollateralSetup.isNewRecord()) {
			aCollateralSetup.setCreatedBy(getUserWorkspace().getLoggedInUser().getUserId());
			aCollateralSetup.setCreatedOn(new Timestamp(System.currentTimeMillis()));
		}
		// Basic Details Error Detail
		showErrorDetails(wve, this.basicDetailsTab);

		logger.debug("Leaving");
	}

	/**
	 * This method set the check list details to aFinanceDetail
	 * 
	 * @param aFinanceDetail
	 */
	private boolean doSave_CheckList(CollateralSetup aCollateralSetup, boolean isForAgreementGen) {
		logger.debug("Entering ");

		boolean validationSuccess = true;
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("financeMainDialogCtrl", this);
		if (fromLoan) {
			map.put("userAction", PennantConstants.RCD_STATUS_SAVED);
		} else {
			map.put("userAction", this.userAction.getSelectedItem().getLabel());
		}
		map.put("moduleName", CollateralConstants.MODULE_NAME);
		if (isForAgreementGen) {
			map.put("agreement", isForAgreementGen);
		}
		try {
			financeCheckListReferenceDialogCtrl.doSetLabels(getHeaderBasicDetails());
			Events.sendEvent("onChkListValidation", checkListChildWindow, map);
		} catch (Exception e) {
			validationSuccess = false;
			if (e instanceof WrongValuesException) {
				throw e;
			}
		}

		Map<Long, Long> selAnsCountMap = new HashMap<Long, Long>();
		List<FinanceCheckListReference> chkList = getCollateralChecklists();
		selAnsCountMap = getSelectedAnsCountMap();

		if (chkList != null && chkList.size() >= 0) {
			aCollateralSetup.setCollateralCheckLists(chkList);
			aCollateralSetup.setSelAnsCountMap(selAnsCountMap);
		}
		logger.debug("Leaving ");
		return validationSuccess;

	}

	/**
	 * Method to show error details if occurred
	 * 
	 **/
	private void showErrorDetails(ArrayList<WrongValueException> wve, Tab tab) {
		logger.debug("Entering");

		doRemoveValidation();

		if (wve.size() > 0) {
			logger.debug("Throwing occured Errors By using WrongValueException");
			tab.setSelected(true);
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
	private void doSetValidation() {
		logger.debug("Entering");

		Date appEndDate = SysParamUtil.getValueAsDate("APP_DFT_END_DATE");

		// Collateral Ref reg
		if (!this.collateralRef.isReadonly()) {
			this.collateralRef.setConstraint(
					new PTStringValidator(Labels.getLabel("label_CollateralSetupDialog_CollateralRef.value"),
							PennantRegularExpressions.REGEX_ALPHANUM, false));
		}

		// Collateral Ccy
		if (!this.collateralCcy.isReadonly()) {
			this.collateralCcy.setConstraint(
					new PTStringValidator(Labels.getLabel("label_CollateralSetupDialog_CollateralCcy.value"),
							PennantRegularExpressions.REGEX_NAME, true, true));
		}
		// Max Collateral Value reg
		if (!this.maxCollateralValue.isReadonly()) {
			this.maxCollateralValue.setConstraint(new PTDecimalValidator(
					Labels.getLabel("label_CollateralSetupDialog_MaxCollateralValue.value"), 2, false, false));
		}
		// Special LTV
		if (!this.specialLTV.isDisabled()) {
			this.specialLTV.setConstraint(new PTDecimalValidator(
					Labels.getLabel("label_CollateralSetupDialog_SpecialLTV.value"), 2, true, false, 100));
		}
		// Collateral Loc
		/*
		 * if (!this.collateralLoc.isReadonly()) { this.collateralLoc.setConstraint(new
		 * PTStringValidator(Labels.getLabel("label_CollateralSetupDialog_CollateralLoc.value"),
		 * PennantRegularExpressions.REGEX_NAME, true)); }
		 */
		// Valuator
		if (!this.valuator.isReadonly()) {
			this.valuator
					.setConstraint(new PTStringValidator(Labels.getLabel("label_CollateralSetupDialog_Valuator.value"),
							PennantRegularExpressions.REGEX_COMPANY_NAME, true));
		}
		// Expiry Date
		if (!this.expiryDate.isDisabled()) {
			this.expiryDate.setConstraint(new PTDateValidator(
					Labels.getLabel("label_CollateralSetupDialog_ExpiryDate.value"), false, true, appEndDate, false));
		}

		// Next Review Date
		if (!this.nextReviewDate.isDisabled()) {
			this.nextReviewDate.setConstraint(
					new PTDateValidator(Labels.getLabel("label_CollateralSetupDialog_NextReviewDate.value"), false,
							true, appEndDate, false));
		}
		// Remarks
		/*
		 * if (!this.remarks.isReadonly()) { this.remarks.setConstraint(new
		 * PTStringValidator(Labels.getLabel("label_CollateralSetupDialog_Remarks.value"),
		 * PennantRegularExpressions.REGEX_NAME, false)); }
		 */

		/* Display fields validation */

		logger.debug("Leaving");
	}

	/**
	 * Remove the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering");

		this.collateralRef.setConstraint("");
		this.depositorCif.setConstraint("");
		this.collateralType.setConstraint("");
		this.collateralCcy.setConstraint("");
		this.maxCollateralValue.setConstraint("");
		this.specialLTV.setConstraint("");
		this.collateralLoc.setConstraint("");
		this.valuator.setConstraint("");
		this.expiryDate.setConstraint("");
		this.nextReviewDate.setConstraint("");
		this.flagDetails.setConstraint("");
		this.remarks.setConstraint("");

		logger.debug("Leaving");
	}

	/**
	 * Clears validation error messages from all the fields of the dialog controller.
	 */
	@Override
	protected void doClearMessage() {
		logger.debug("Entering");
		this.collateralRef.setErrorMessage("");
		this.depositorCif.setErrorMessage("");
		this.collateralType.setErrorMessage("");
		this.collateralCcy.setErrorMessage("");
		this.maxCollateralValue.setErrorMessage("");
		this.specialLTV.setErrorMessage("");
		this.collateralLoc.setErrorMessage("");
		this.valuator.setErrorMessage("");
		this.expiryDate.setErrorMessage("");
		this.reviewFrequency.setErrorMessage("");
		this.nextReviewDate.setErrorMessage("");
		this.flagDetails.setErrorMessage("");
		this.remarks.setErrorMessage("");
		logger.debug("Leaving");
	}

	// View customer information
	public void onClick$viewInfo(Event event) {
		logger.debug("Entering");
		if (StringUtils.trimToNull(this.depositorCif.getValue()) == null) {
			throw new WrongValueException(this.depositorCif, Labels.getLabel("FIELD_NO_INVALID",
					new String[] { Labels.getLabel("label_CollateralSetupDialog_DepositorCif.value") }));
		}
		try {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("custCIF", this.depositorCif.getValue());
			map.put("custid", this.depositorId.getValue());
			map.put("finance", true);
			Executions.createComponents("/WEB-INF/pages/CustomerMasters/Customer/fincustomerdetailsenq.zul",
					window_CollateralSetupDialog, map);
		} catch (Exception e) {
			logger.error("Exception :", e);
		}
		logger.debug("Leaving");
	}

	/**
	 * Method for Calculation bank Valuation based on Special LTV Change
	 * 
	 * @param event
	 */
	public void onChange$specialLTV(Event event) {
		logger.trace(Literal.ENTERING);
		calBankValuation();
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Method for Calculation bank Valuation based on Max Collateral value Change
	 * 
	 * @param event
	 */
	public void onFulfill$maxCollateralValue(Event event) {
		logger.debug("Entering");
		calBankValuation();
		logger.debug("Leaving");
	}

	/**
	 * Method for Calculating Bank Valuation based on LTV & Max Collateral value
	 */
	private void calBankValuation() {
		BigDecimal ltvValue = this.bankLtv.getValue() == null ? BigDecimal.ZERO : this.bankLtv.getValue();
		if (this.specialLTV.getValue() != null && this.specialLTV.getValue().compareTo(BigDecimal.ZERO) > 0) {
			ltvValue = this.specialLTV.getValue();
		}

		BigDecimal colValue = this.collateralValue.getActualValue().multiply(ltvValue).divide(new BigDecimal(100), 0,
				RoundingMode.HALF_DOWN);
		if (this.maxCollateralValue.getActualValue().compareTo(BigDecimal.ZERO) > 0
				&& colValue.compareTo(this.maxCollateralValue.getActualValue()) > 0) {
			colValue = this.maxCollateralValue.getActualValue();
		}

		this.bankValuation.setValue(colValue);
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// ++++++++++++++++++++++++ GUI operations +++++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * Set the components to ReadOnly. <br>
	 */
	private void doEdit() {
		logger.debug("Entering");

		if (PennantConstants.MODULETYPE_ENQ.equals(moduleType)) {
			doReadOnly();
		} else {
			if (this.collateralSetup.isNewRecord()) {
				this.collateralRef.setReadonly(isReadOnly("CollateralSetupDialog_CollateralRef"));
				this.collateralCcy.setReadonly(isReadOnly("CollateralSetupDialog_CollateralCcy"));
			} else {
				this.collateralRef.setReadonly(true);
				this.collateralCcy.setReadonly(true);
			}
			this.depositorCif.setReadonly(true);
			this.collateralType.setReadonly(true);
			this.maxCollateralValue.setReadonly(isReadOnly("CollateralSetupDialog_MaxCollateralValue"));
			this.specialLTV.setDisabled(isReadOnly("CollateralSetupDialog_SpecialLTV"));
			this.collateralLoc.setReadonly(isReadOnly("CollateralSetupDialog_CollateralLoc"));
			this.valuator.setReadonly(isReadOnly("CollateralSetupDialog_Valuator"));
			this.expiryDate.setDisabled(isReadOnly("CollateralSetupDialog_ExpiryDate"));
			this.reviewFrequency.setDisabled(isReadOnly("CollateralSetupDialog_ReviewFrequency"));
			this.nextReviewDate.setDisabled(isReadOnly("CollateralSetupDialog_NextReviewDate"));
			this.multiLoanAssignment.setDisabled(isReadOnly("CollateralSetupDialog_MultiLoanAssignment"));
			this.thirdPartyAssignment.setDisabled(isReadOnly("CollateralSetupDialog_ThirdPartyAssignment"));
			this.remarks.setReadonly(isReadOnly("CollateralSetupDialog_Remarks"));
			this.flagDetails.setReadonly(true);

			// Fields setting based on Configuration parameters
			CollateralStructure cs = getCollateralSetup().getCollateralStructure();
			/*
			 * if (!cs.isAllowLtvWaiver()) { this.specialLTV.setDisabled(true); }
			 */
			if (cs.isCollateralLocReq()) {
				this.space_CollateralLoc.setSclass(PennantConstants.mandateSclass);
			} else {
				this.collateralLoc.setReadonly(true);
			}
			if (cs.isCollateralValuatorReq()) {
				this.space_CollateralValuator.setSclass(PennantConstants.mandateSclass);
			} else {
				this.valuator.setReadonly(true);
			}

			if (isWorkFlowEnabled()) {
				for (int i = 0; i < userAction.getItemCount(); i++) {
					userAction.getItemAtIndex(i).setDisabled(false);
				}
				if (this.collateralSetup.isNewRecord()) {
					this.btnCtrl.setBtnStatus_Edit();
					btnCancel.setVisible(false);
				} else {
					this.btnCtrl.setWFBtnStatus_Edit(isFirstTask());
				}
			} else {
				this.btnCtrl.setBtnStatus_Edit();
			}
		}

		logger.debug("Leaving ");
	}

	/**
	 * Set the components to ReadOnly. <br>
	 */
	private void doReadOnly() {
		logger.debug("Entering");

		this.collateralRef.setReadonly(true);
		this.depositorCif.setReadonly(true);
		this.collateralType.setReadonly(true);
		this.collateralCcy.setReadonly(true);
		this.maxCollateralValue.setReadonly(true);
		this.specialLTV.setReadonly(true);
		this.collateralLoc.setReadonly(true);
		this.valuator.setReadonly(true);
		this.expiryDate.setDisabled(true);
		this.reviewFrequency.setDisabled(true);
		this.nextReviewDate.setDisabled(true);
		this.multiLoanAssignment.setDisabled(true);
		this.thirdPartyAssignment.setDisabled(true);
		this.flagDetails.setReadonly(true);
		this.remarks.setReadonly(true);

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(true);
			}
			if (userAction.getItemCount() > 0) {
				this.recordStatus.setValue("");
				this.userAction.setSelectedIndex(0);
			}
		}
		logger.debug("Leaving");
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// ++++++++++++++++++++++++++++++ helpers ++++++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");

		int formatter = CurrencyUtil.getFormat(getCollateralSetup().getCollateralCcy());

		this.collateralRef.setMaxlength(20);
		this.depositorCif.setMaxlength(12);

		this.collateralCcy.setMandatoryStyle(true);
		this.collateralCcy.setModuleName("Currency");
		this.collateralCcy.setValueColumn("CcyCode");
		this.collateralCcy.setDescColumn("CcyDesc");
		this.collateralCcy.setValidateColumns(new String[] { "CcyCode" });

		this.collateralRef.setMaxlength(20);
		this.maxCollateralValue.setProperties(false, formatter);
		this.unitPrice.setProperties(false, formatter);
		this.collateralValue.setProperties(false, formatter);
		this.bankValuation.setProperties(false, formatter);
		this.assignedCollateralValue.setProperties(false, formatter);

		this.specialLTV.setMaxlength(6);
		this.specialLTV.setFormat(PennantConstants.percentageFormate2);

		this.collateralLoc.setMaxlength(100);
		this.valuator.setMaxlength(100);
		this.expiryDate.setFormat(PennantConstants.dateFormat);
		this.nextReviewDate.setFormat(PennantConstants.dateFormat);
		this.remarks.setMaxlength(1000);

		setStatusDetails();
		logger.debug("Leaving");
	}

	/**
	 * Setting the amount formats based on currency
	 * 
	 * @param event
	 */
	public void onFulfill$collateralCcy(Event event) {
		logger.debug("Entering " + event.toString());

		this.collateralCcy.setConstraint("");
		Object dataObject = collateralCcy.getObject();
		if (dataObject instanceof String) {
			this.collateralCcy.setValue(dataObject.toString());
		} else {
			Currency details = (Currency) dataObject;
			if (details != null) {
				this.maxCollateralValue.setProperties(false, details.getCcyEditField());
				this.unitPrice.setProperties(false, details.getCcyEditField());
				this.collateralValue.setProperties(false, details.getCcyEditField());
				this.bankValuation.setProperties(false, details.getCcyEditField());
				this.assignedCollateralValue.setProperties(false, details.getCcyEditField());
			}
		}

		logger.debug("Leaving " + event.toString());
	}

	/**
	 * Method for fetching Currency format of selected currency
	 * 
	 * @return
	 */
	public int getCcyFormat() {
		return CurrencyUtil.getFormat(this.collateralCcy.getValue());
	}

	private void doDelete() {
		logger.debug(Literal.ENTERING);

		final CollateralSetup aCollateralSetup = new CollateralSetup();
		BeanUtils.copyProperties(getCollateralSetup(), aCollateralSetup);

		doDelete(aCollateralSetup.getCollateralRef(), aCollateralSetup);

		logger.debug(Literal.LEAVING);
	}

	@Override
	public void closeDialog() {
		super.closeDialog();

		// Close sub dialog windows
		if (getCustomerDialogCtrl() != null) {
			getCustomerDialogCtrl().closeDialog();
		}
		if (getExtendedFieldRenderDialogCtrl() != null) {
			getExtendedFieldRenderDialogCtrl().closeDialog();
		}
		if (getDocumentDetailDialogCtrl() != null) {
			getDocumentDetailDialogCtrl().closeDialog();
		}
		if (getAgreementDetailDialogCtrl() != null) {
			getAgreementDetailDialogCtrl().closeDialog();
		}
		if (getFinanceCheckListReferenceDialogCtrl() != null) {
			getFinanceCheckListReferenceDialogCtrl().closeDialog();
		}
	}

	/**
	 * Clears the components values. <br>
	 */
	protected void doClear() {
		logger.debug("Entering");
		// remove validation, if there are a save before
		this.collateralRef.setValue("");
		this.depositorCif.setValue("");
		this.collateralType.setValue("");
		this.collateralType.setValue("");
		this.collateralCcy.setValue("");
		this.collateralCcy.setDescription("");
		this.maxCollateralValue.setValue("");
		this.specialLTV.setValue("");
		this.collateralLoc.setValue("");
		this.valuator.setValue("");
		this.expiryDate.setText("");
		this.reviewFrequency.setValue("");
		this.nextReviewDate.setText("");
		this.multiLoanAssignment.setChecked(false);
		this.thirdPartyAssignment.setChecked(false);
		this.flagDetails.setValue("");
		this.remarks.setValue("");
		// Display fields
		this.numberOfUnits.setValue(0);
		this.unitPrice.setValue("");
		this.collateralValue.setValue("");
		this.bankLtv.setValue("");
		this.bankValuation.setValue("");
		this.assignedLoansCount.setValue(0);
		this.assignedCollateralValue.setValue("");
		this.assignedPercToCollateralValue.setValue("");
		this.assignedPercToBankValuation.setValue("");

		logger.debug("Leaving");
	}

	// Co--owner details like new button , list diaplay and double click.....

	// Reneder the list
	protected void doFillCoOwnerDetails(List<CoOwnerDetail> coOwnerDetailList) {
		logger.debug("Entering");
		this.listBoxCoownerDetail.getItems().clear();
		setCoOwnerDetailList(coOwnerDetailList);
		if (coOwnerDetailList != null && !coOwnerDetailList.isEmpty()) {
			for (CoOwnerDetail coOwnerDetail : coOwnerDetailList) {
				Listitem item = new Listitem();
				Listcell lc;

				lc = new Listcell(coOwnerDetail.getCoOwnerCIFName());
				item.appendChild(lc);

				lc = new Listcell(String.valueOf(coOwnerDetail.getCoOwnerPercentage()));
				item.appendChild(lc);

				lc = new Listcell();
				final Checkbox isBankCustomer = new Checkbox();
				isBankCustomer.setDisabled(true);
				isBankCustomer.setChecked(coOwnerDetail.isBankCustomer());
				lc.appendChild(isBankCustomer);
				lc.setParent(item);

				lc = new Listcell(coOwnerDetail.getCoOwnerCIF());
				item.appendChild(lc);

				lc = new Listcell(PennantApplicationUtil.getLabelDesc(coOwnerDetail.getCoOwnerIDType(),
						PennantAppUtil.getIdentityType()));
				item.appendChild(lc);

				lc = new Listcell(coOwnerDetail.getMobileNo());
				item.appendChild(lc);

				lc = new Listcell(coOwnerDetail.getRecordStatus());
				lc.setParent(item);

				lc = new Listcell(PennantJavaUtil.getLabel(coOwnerDetail.getRecordType()));
				lc.setParent(item);

				item.setAttribute("data", coOwnerDetail);
				ComponentsCtrl.applyForward(item, "onDoubleClick=onCoOwnerItemDoubleClicked");
				this.listBoxCoownerDetail.appendChild(item);
			}
		}
		logger.debug("Leaving");
	}

	// Double click the list
	public void onCoOwnerItemDoubleClicked(Event event) {
		logger.debug("Entering" + event.toString());

		final Listitem item = this.listBoxCoownerDetail.getSelectedItem();
		if (item != null) {
			int index = item.getIndex();
			final CoOwnerDetail coOwnerDetail = (CoOwnerDetail) item.getAttribute("data");
			if (StringUtils.equalsIgnoreCase(coOwnerDetail.getRecordType(), PennantConstants.RECORD_TYPE_CAN)) {
				MessageUtil.showError("Not Allowed to maintain This Record");
			} else {
				final Map<String, Object> map = new HashMap<String, Object>();
				map.put("coOwnerDetail", coOwnerDetail);
				map.put("collateralSetupCtrl", this);
				map.put("roleCode", getRole());
				map.put("index", index);
				map.put("filter", getCustomerCIF());
				map.put("primaryCustCif", this.depositorCif.getValue());
				if (PennantConstants.MODULETYPE_ENQ.equals(moduleType)) {
					map.put("enqModule", true);
				} else {
					map.put("enqModule", false);
				}
				map.put("primaryCustCif", this.depositorCif.getValue());
				// call the zul-file with the parameters packed in a map
				try {
					Executions.createComponents("/WEB-INF/pages/Collateral/CollateralSetup/CoOwnerDetailDialog.zul",
							window_CollateralSetupDialog, map);
				} catch (Exception e) {
					MessageUtil.showError(e);
				}
			}
		}
		logger.debug("Leaving" + event.toString());
	}

	// ================Coowner Details
	public void onClick$btnAddCoownerDetails(Event event) {
		logger.debug("Entering" + event.toString());

		if (StringUtils.isEmpty(this.depositorCif.getValue())) {
			throw new WrongValueException(this.depositorCif, Labels.getLabel("FIELD_NO_INVALID",
					new String[] { Labels.getLabel("label_CollateralSetupList_DepositorCif.value") }));
		}

		CoOwnerDetail coOwnerDetail = new CoOwnerDetail();
		coOwnerDetail.setNewRecord(true);
		coOwnerDetail.setWorkflowId(0);
		final Map<String, Object> map = new HashMap<String, Object>();
		coOwnerDetail.setCoOwnerId(getCoOwnerId());
		map.put("coOwnerDetail", coOwnerDetail);
		map.put("collateralSetupCtrl", this);
		map.put("newRecord", true);
		map.put("roleCode", getRole());
		map.put("filter", getCustomerCIF());
		map.put("primaryCustCif", this.depositorCif.getValue());
		try {
			Executions.createComponents("/WEB-INF/pages/Collateral/CollateralSetup/CoOwnerDetailDialog.zul",
					window_CollateralSetupDialog, map);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving" + event.toString());
	}

	private String[] getCustomerCIF() {
		String cif[] = new String[1];
		cif[0] = this.depositorCif.getValue();
		return cif;
	}

	private int getCoOwnerId() {
		int seqNo = 0;
		if (getCoOwnerDetailList() != null && !getCoOwnerDetailList().isEmpty()) {
			for (CoOwnerDetail coOwnerDetail : getCoOwnerDetailList()) {
				if (coOwnerDetail.getCoOwnerId() > seqNo) {
					seqNo = coOwnerDetail.getCoOwnerId();
				}
			}
		}
		return seqNo + 1;
	}

	// Collateral Thirdparty Assignment details.....

	/**
	 * Method for Checking Allowed collateral assignment details....
	 * 
	 * @param event
	 */
	public void onCheck$thirdPartyAssignment(Event event) {
		logger.debug("Entering");
		this.gb_ThirdPartyAssignDetails.setVisible(this.thirdPartyAssignment.isChecked());
		logger.debug("Leaving");
	}

	// Collateral third party details rendering
	protected void doFillCollateralThirdPartyDetails(List<CollateralThirdParty> list) {
		logger.debug("Entering");

		this.listBoxThirdParty.getItems().clear();
		setCollateralThirdPartyList(list);
		if (!enqiryModule) {
			this.thirdPartyAssignment.setDisabled(isReadOnly("CollateralSetupDialog_ThirdPartyAssignment"));
		}
		if (list != null && !list.isEmpty()) {
			this.thirdPartyAssignment.setDisabled(true);
			for (CollateralThirdParty details : list) {
				Listitem item = new Listitem();
				Listcell lc;

				lc = new Listcell(details.getCustCIF());
				item.appendChild(lc);
				lc = new Listcell(details.getCustShrtName());
				lc.setParent(item);
				lc = new Listcell(PennantApplicationUtil.formatEIDNumber(details.getCustCRCPR()));
				lc.setParent(item);
				lc = new Listcell(details.getCustPassportNo());
				lc.setParent(item);
				lc = new Listcell(details.getCustNationality());
				lc.setParent(item);
				lc = new Listcell(details.getCustCtgCode());
				lc.setParent(item);
				lc = new Listcell(details.getRecordStatus());
				lc.setParent(item);
				lc = new Listcell(PennantJavaUtil.getLabel(details.getRecordType()));
				lc.setParent(item);
				item.setAttribute("data", details);
				ComponentsCtrl.applyForward(item, "onDoubleClick=onCollateralThirdPartyItemDoubleClicked");
				this.listBoxThirdParty.appendChild(item);
			}
		}
		logger.debug("Leaving");
	}

	// Double click Third Party Assignment Deatils list
	public void onCollateralThirdPartyItemDoubleClicked(Event event) {
		logger.debug("Entering" + event.toString());

		final Listitem item = this.listBoxThirdParty.getSelectedItem();
		if (item != null) {
			int index = item.getIndex();
			final CollateralThirdParty collateralThirdParty = (CollateralThirdParty) item.getAttribute("data");
			if (StringUtils.equalsIgnoreCase(collateralThirdParty.getRecordType(), PennantConstants.RECORD_TYPE_CAN)) {
				MessageUtil.showError("Not Allowed to maintain This Record");
			} else {
				final Map<String, Object> map = new HashMap<String, Object>();
				map.put("collateralThirdParty", collateralThirdParty);
				map.put("collateralSetupCtrl", this);
				map.put("roleCode", getRole());
				map.put("index", index);
				map.put("filter", getCustomerCIF());
				map.put("primaryCustCif", this.depositorCif.getValue());
				if (PennantConstants.MODULETYPE_ENQ.equals(moduleType)) {
					map.put("enqModule", true);
				} else {
					map.put("enqModule", false);
				}
				// call the zul-file with the parameters packed in a map
				try {
					Executions.createComponents(
							"/WEB-INF/pages/Collateral/CollateralSetup/CollateralThirdPartyDialog.zul",
							window_CollateralSetupDialog, map);
				} catch (Exception e) {
					MessageUtil.showError(e);
				}
			}
		}
		logger.debug("Leaving" + event.toString());
	}

	// ================Add Third Party Assignment Deatils
	public void onClick$btnAddThirdPartyDetail(Event event) {
		logger.debug("Entering" + event.toString());

		if (StringUtils.isEmpty(this.depositorCif.getValue())) {
			throw new WrongValueException(this.depositorCif, Labels.getLabel("FIELD_NO_INVALID",
					new String[] { Labels.getLabel("label_CollateralSetupList_DepositorCif.value") }));
		}

		CollateralThirdParty collaThirdParty = new CollateralThirdParty();
		collaThirdParty.setCollateralRef(this.collateralRef.getValue());
		collaThirdParty.setNewRecord(true);
		collaThirdParty.setWorkflowId(0);
		final Map<String, Object> map = new HashMap<String, Object>();
		map.put("collateralThirdParty", collaThirdParty);
		map.put("collateralSetupCtrl", this);
		map.put("newRecord", "true");
		map.put("roleCode", getRole());
		map.put("filter", getCustomerCIF());
		map.put("primaryCustCif", this.depositorCif.getValue());
		map.put("enqModule", false);
		try {
			Executions.createComponents("/WEB-INF/pages/Collateral/CollateralSetup/CollateralThirdPartyDialog.zul",
					window_CollateralSetupDialog, map);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Saves the components to table. from financemain
	 */
	private void doSaveFinCollaterals() {
		logger.debug("Entering");

		final CollateralSetup aCollateralSetup = new CollateralSetup();
		BeanUtils.copyProperties(getCollateralSetup(), aCollateralSetup);

		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		// force validation, if on, than execute by component.getValue()
		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		if (!PennantConstants.RECORD_TYPE_DEL.equals(aCollateralSetup.getRecordType()) && isValidation()) {
			doClearMessage();
			doSetValidation();
			// fill the CollateralSetup object with the components data
			doWriteComponentsToBean(aCollateralSetup);

			// Add third party details list
			if (this.thirdPartyAssignment.isChecked()) {

				// Add the Extended Field Detail list
				if (getCollateralThirdPartyList() == null || getCollateralThirdPartyList().isEmpty()) {
					this.basicDetailsTab.setSelected(true);
					MessageUtil.showError(Labels.getLabel("label_Colateral_ThirdParty_Validation"));
					return;
				}
				aCollateralSetup.setCollateralThirdPartyList(getCollateralThirdPartyList());
			} else {
				aCollateralSetup.setCollateralThirdPartyList(getCollateralThirdPartyList());
			}

			// Co-Owner Detail list
			aCollateralSetup.setCoOwnerDetailList(getCoOwnerDetailList());

			// Collateral Flags
			fetchFlagDetals();
			if (getFinFlagsDetailList() != null && !getFinFlagsDetailList().isEmpty()) {
				aCollateralSetup.setFinFlagsDetailsList(getFinFlagsDetailList());
			} else {
				aCollateralSetup.setFinFlagsDetailsList(getCollateralSetup().getFinFlagsDetailsList());
			}

			// Add the Extended Field Detail list
			if (getExtendedFieldRenderList() == null || getExtendedFieldRenderList().isEmpty()) {
				this.extendedDetailsTab.setSelected(true);
				MessageUtil.showError(Labels.getLabel("label_Colateral_ExtendedField_Validation"));
				return;
			}
			aCollateralSetup.setExtendedFieldRenderList(getExtendedFieldRenderList());

			// Finance CheckList Details Saving
			if (checkListChildWindow != null) {
				boolean validationSuccess = doSave_CheckList(aCollateralSetup, false);
				if (!validationSuccess) {
					return;
				}
			} else {
				aCollateralSetup.setCheckLists(getCollateralSetup().getCheckLists());
			}

			// Document Details Saving
			if (documentDetailDialogCtrl != null) {
				aCollateralSetup.setDocuments(documentDetailDialogCtrl.getDocumentDetailsList());
			} else {
				aCollateralSetup.setDocuments(getCollateralSetup().getDocuments());
			}
		}

		boolean isNew = aCollateralSetup.isNewRecord();
		String tranType = "";
		if (this.newRecord) {
			if (isNew) {
				aCollateralSetup.setVersion(1);
				aCollateralSetup.setRecordType(PennantConstants.RECORD_TYPE_NEW);
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
			if (StringUtils.isBlank(aCollateralSetup.getRecordType())) {
				aCollateralSetup.setVersion(aCollateralSetup.getVersion() + 1);
				aCollateralSetup.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				aCollateralSetup.setNewRecord(true);
			}
			if (aCollateralSetup.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW) && this.newRecord) {
				tranType = PennantConstants.TRAN_ADD;
			} else if (aCollateralSetup.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_UPD;
			}
		} else {
			aCollateralSetup.setVersion(aCollateralSetup.getVersion() + 1);
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}

		aCollateralSetup.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		aCollateralSetup.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aCollateralSetup.setUserDetails(getUserWorkspace().getLoggedInUser());
		aCollateralSetup.setRoleCode(getRole());
		aCollateralSetup.setFromLoan(true);

		// save it to database
		try {
			AuditHeader auditHeader = newCollateralProcess(aCollateralSetup, tranType);
			auditHeader = ErrorControl.showErrorDetails(this.window_CollateralSetupDialog, auditHeader);
			int retValue = auditHeader.getProcessStatus();
			if (retValue == PennantConstants.porcessCONTINUE || retValue == PennantConstants.porcessOVERIDE) {
				if (getCollateralAssignmentDialogCtrl() != null) {
					getCollateralAssignmentDialogCtrl().doFillAssignment(aCollateralSetup);
				}
				closeDialog();
			}
		} catch (final DataAccessException e) {
			logger.error("Exception: ", e);
			showMessage(e);
		}
		logger.debug("Leaving");
	}

	private AuditHeader newCollateralProcess(CollateralSetup aCollateralSetup, String tranType) {
		return getAuditHeader(aCollateralSetup, tranType);
	}

	/**
	 * Display Message in Error Box
	 * 
	 * @param e (Exception)
	 */
	private void showMessage(Exception e) {
		logger.debug("Entering");
		AuditHeader auditHeader = new AuditHeader();
		try {
			auditHeader.setErrorDetails(new ErrorDetail(PennantConstants.ERR_UNDEF, e.getMessage(), null));
			ErrorControl.showErrorControl(this.window_CollateralSetupDialog, auditHeader);
		} catch (Exception exp) {
			logger.error("Exception: ", exp);
		}
		logger.debug("Leaving");
	}

	protected void doSave() {
		logger.debug("Entering");

		final CollateralSetup aCollateralSetup = new CollateralSetup();
		BeanUtils.copyProperties(getCollateralSetup(), aCollateralSetup);
		boolean isNew = false;

		if (isWorkFlowEnabled()) {
			aCollateralSetup.setRecordStatus(userAction.getSelectedItem().getValue().toString());
			getWorkFlowDetails(userAction.getSelectedItem().getLabel(), aCollateralSetup.getNextTaskId(),
					aCollateralSetup);
		}

		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		// force validation, if on, than execute by component.getValue()
		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		if (!PennantConstants.RECORD_TYPE_DEL.equals(aCollateralSetup.getRecordType()) && isValidation()) {
			doClearMessage();
			doSetValidation();
			// fill the CollateralSetup object with the components data
			doWriteComponentsToBean(aCollateralSetup);

			// Add third party details list
			if (this.thirdPartyAssignment.isChecked()) {

				// Add the Extended Field Detail list
				if (getCollateralThirdPartyList() == null || getCollateralThirdPartyList().isEmpty()) {
					this.basicDetailsTab.setSelected(true);
					MessageUtil.showError(Labels.getLabel("label_Colateral_ThirdParty_Validation"));
					return;
				}
				aCollateralSetup.setCollateralThirdPartyList(getCollateralThirdPartyList());
			} else {
				aCollateralSetup.setCollateralThirdPartyList(getCollateralThirdPartyList());
			}

			// Co-Owner Detail list
			aCollateralSetup.setCoOwnerDetailList(getCoOwnerDetailList());

			// Collateral Flags
			fetchFlagDetals();
			if (getFinFlagsDetailList() != null && !getFinFlagsDetailList().isEmpty()) {
				aCollateralSetup.setFinFlagsDetailsList(getFinFlagsDetailList());
			} else {
				aCollateralSetup.setFinFlagsDetailsList(getCollateralSetup().getFinFlagsDetailsList());
			}

			// Add the Extended Field Detail list
			if (getExtendedFieldRenderList() == null || getExtendedFieldRenderList().isEmpty()) {
				this.extendedDetailsTab.setSelected(true);
				MessageUtil.showError(Labels.getLabel("label_Colateral_ExtendedField_Validation"));
				return;
			}
			aCollateralSetup.setExtendedFieldRenderList(getExtendedFieldRenderList());

			// Finance CheckList Details Saving
			if (checkListChildWindow != null) {
				boolean validationSuccess = doSave_CheckList(aCollateralSetup, false);
				if (!validationSuccess) {
					return;
				}
			} else {
				aCollateralSetup.setCheckLists(getCollateralSetup().getCheckLists());
			}

			// Document Details Saving
			if (documentDetailDialogCtrl != null) {
				aCollateralSetup.setDocuments(documentDetailDialogCtrl.getDocumentDetailsList());
			} else {
				aCollateralSetup.setDocuments(getCollateralSetup().getDocuments());
			}
		}

		// Write the additional validations as per below example
		// get the selected branch object from the listbox
		// Do data level validations here

		isNew = aCollateralSetup.isNewRecord();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.trimToEmpty(aCollateralSetup.getRecordType()).equals("")) {
				aCollateralSetup.setVersion(aCollateralSetup.getVersion() + 1);
				if (isNew) {
					aCollateralSetup.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aCollateralSetup.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aCollateralSetup.setNewRecord(true);
				}
			}
		} else {
			aCollateralSetup.setVersion(aCollateralSetup.getVersion() + 1);
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}
		// save it to database
		try {
			if (doProcess(aCollateralSetup, tranType)) {
				// Mail Alert Notification for Customer/Dealer/Provider...etc
				if (!"Save".equalsIgnoreCase(this.userAction.getSelectedItem().getLabel())) {

					Notification notification = new Notification();
					notification.getTemplates().add(NotificationConstants.TEMPLATE_FOR_AE);
					notification.getTemplates().add(NotificationConstants.TEMPLATE_FOR_CN);

					notification.setModule("COLLATERAL");
					notification.setSubModule(aCollateralSetup.getCollateralType());
					notification.setKeyReference(aCollateralSetup.getCollateralRef());
					notification.setStage(aCollateralSetup.getRoleCode());
					notification.setReceivedBy(getUserWorkspace().getUserId());

					notificationService.sendNotifications(notification, aCollateralSetup.getCustomerDetails(),
							aCollateralSetup.getCollateralType(), null);
				}

				// User Notifications Message/Alert
				publishNotification(Notify.ROLE, aCollateralSetup.getCollateralRef(), aCollateralSetup);

				// List Detail Refreshment
				refreshList();

				// Confirmation message
				String msg = PennantApplicationUtil.getSavingStatus(aCollateralSetup.getRoleCode(),
						aCollateralSetup.getNextRoleCode(), aCollateralSetup.getCollateralRef(), " Collateral ",
						aCollateralSetup.getRecordStatus());
				Clients.showNotification(msg, "info", null, null, -1);
				closeDialog();
			}
		} catch (final DataAccessException e) {
			MessageUtil.showError(e);
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set the workFlow Details List to Object
	 * 
	 * @param aAuthorizedSignatoryRepository (AuthorizedSignatoryRepository)
	 * 
	 * @param tranType                       (String)
	 * 
	 * @return boolean
	 * 
	 */
	protected boolean doProcess(CollateralSetup aCollateralSetup, String tranType) {
		logger.debug("Entering");

		boolean processCompleted = false;
		AuditHeader auditHeader = null;
		String nextRoleCode = "";

		aCollateralSetup.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		aCollateralSetup.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aCollateralSetup.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			String nextTaskId = "";
			aCollateralSetup.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aCollateralSetup.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getNextTaskIds(taskId, aCollateralSetup);
				}

				if (isNotesMandatory(taskId, aCollateralSetup)) {
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

			aCollateralSetup.setTaskId(taskId);
			aCollateralSetup.setNextTaskId(nextTaskId);
			aCollateralSetup.setRoleCode(getRole());
			aCollateralSetup.setNextRoleCode(nextRoleCode);

			// Coowner details
			if (aCollateralSetup.getCoOwnerDetailList() != null && !aCollateralSetup.getCoOwnerDetailList().isEmpty()) {
				for (CoOwnerDetail details : aCollateralSetup.getCoOwnerDetailList()) {
					details.setCollateralRef(aCollateralSetup.getCollateralRef());
					details.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
					details.setLastMntOn(new Timestamp(System.currentTimeMillis()));
					details.setUserDetails(getUserWorkspace().getLoggedInUser());
					details.setRecordStatus(aCollateralSetup.getRecordStatus());
					details.setWorkflowId(aCollateralSetup.getWorkflowId());
					details.setTaskId(taskId);
					details.setNextTaskId(nextTaskId);
					details.setRoleCode(getRole());
					details.setNextRoleCode(nextRoleCode);
					if (PennantConstants.RECORD_TYPE_DEL.equals(aCollateralSetup.getRecordType())) {
						if (StringUtils.trimToNull(details.getRecordType()) == null) {
							details.setRecordType(aCollateralSetup.getRecordType());
							details.setNewRecord(true);
						}
					}
				}
			}

			// Third Party details
			if (aCollateralSetup.getCollateralThirdPartyList() != null
					&& !aCollateralSetup.getCollateralThirdPartyList().isEmpty()) {
				for (CollateralThirdParty details : aCollateralSetup.getCollateralThirdPartyList()) {
					details.setCollateralRef(aCollateralSetup.getCollateralRef());
					details.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
					details.setLastMntOn(new Timestamp(System.currentTimeMillis()));
					details.setUserDetails(getUserWorkspace().getLoggedInUser());
					details.setRecordStatus(aCollateralSetup.getRecordStatus());
					details.setWorkflowId(aCollateralSetup.getWorkflowId());
					details.setTaskId(taskId);
					details.setNextTaskId(nextTaskId);
					details.setRoleCode(getRole());
					details.setNextRoleCode(nextRoleCode);
					if (PennantConstants.RECORD_TYPE_DEL.equals(aCollateralSetup.getRecordType())) {
						if (StringUtils.trimToNull(details.getRecordType()) == null) {
							details.setRecordType(aCollateralSetup.getRecordType());
							details.setNewRecord(true);
						}
					}
				}
			}

			// Extended Field details
			if (extendedFieldRenderList != null && !extendedFieldRenderList.isEmpty()) {
				for (ExtendedFieldRender details : extendedFieldRenderList) {
					details.setReference(aCollateralSetup.getCollateralRef());
					details.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
					details.setLastMntOn(new Timestamp(System.currentTimeMillis()));
					details.setRecordStatus(aCollateralSetup.getRecordStatus());
					details.setWorkflowId(aCollateralSetup.getWorkflowId());
					details.setTaskId(taskId);
					details.setNextTaskId(nextTaskId);
					details.setRoleCode(getRole());
					details.setNextRoleCode(nextRoleCode);
					details.setVersion(aCollateralSetup.getVersion());
					details.setNewRecord(aCollateralSetup.isNewRecord());
					if (PennantConstants.RECORD_TYPE_DEL.equals(aCollateralSetup.getRecordType())) {
						if (StringUtils.trimToNull(details.getRecordType()) == null) {
							details.setRecordType(aCollateralSetup.getRecordType());
							details.setNewRecord(true);
						}
					}
				}
			}

			// FinFlags Details
			if (aCollateralSetup.getFinFlagsDetailsList() != null
					&& !aCollateralSetup.getFinFlagsDetailsList().isEmpty()) {
				for (FinFlagsDetail details : aCollateralSetup.getFinFlagsDetailsList()) {
					if (StringUtils.isNotBlank(details.getRecordType())) {
						details.setReference(aCollateralSetup.getCollateralRef());
						details.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
						details.setLastMntOn(new Timestamp(System.currentTimeMillis()));
						details.setRecordStatus(aCollateralSetup.getRecordStatus());
						details.setWorkflowId(aCollateralSetup.getWorkflowId());
						details.setTaskId(taskId);
						details.setNextTaskId(nextTaskId);
						details.setRoleCode(getRole());
						details.setNextRoleCode(nextRoleCode);
						if (PennantConstants.RECORD_TYPE_DEL.equals(aCollateralSetup.getRecordType())) {
							if (StringUtils.trimToNull(details.getRecordType()) == null) {
								details.setRecordType(aCollateralSetup.getRecordType());
								details.setNewRecord(true);
							}
						}
					}
				}
			}

			// Document Details
			if (aCollateralSetup.getDocuments() != null && !aCollateralSetup.getDocuments().isEmpty()) {
				for (DocumentDetails details : aCollateralSetup.getDocuments()) {

					if (StringUtils.isEmpty(StringUtils.trimToEmpty(details.getRecordType()))) {
						continue;
					}
					details.setReferenceId(aCollateralSetup.getCollateralRef());
					details.setDocModule(CollateralConstants.MODULE_NAME);
					details.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
					details.setLastMntOn(new Timestamp(System.currentTimeMillis()));
					details.setRecordStatus(aCollateralSetup.getRecordStatus());
					details.setWorkflowId(aCollateralSetup.getWorkflowId());
					details.setTaskId(taskId);
					details.setNextTaskId(nextTaskId);
					details.setRoleCode(getRole());
					details.setNextRoleCode(nextRoleCode);
					details.setCustomerCif(aCollateralSetup.getDepositorCif());
					details.setCustId(aCollateralSetup.getCustomerDetails().getCustID());
					details.setFinReference(aCollateralSetup.getFinReference());
					if (PennantConstants.RECORD_TYPE_DEL.equals(aCollateralSetup.getRecordType())) {
						if (StringUtils.trimToNull(details.getRecordType()) == null) {
							details.setRecordType(aCollateralSetup.getRecordType());
							details.setNewRecord(true);
						}
					}
				}
			}

			// CheckList details
			if (aCollateralSetup.getCollateralCheckLists() != null
					&& !aCollateralSetup.getCollateralCheckLists().isEmpty()) {
				for (FinanceCheckListReference details : aCollateralSetup.getCollateralCheckLists()) {
					details.setFinReference(aCollateralSetup.getCollateralRef());
					details.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
					details.setLastMntOn(new Timestamp(System.currentTimeMillis()));
					details.setRecordStatus(aCollateralSetup.getRecordStatus());
					details.setTaskId(taskId);
					details.setNextTaskId(nextTaskId);
					details.setRoleCode(getRole());
					details.setNextRoleCode(nextRoleCode);
					if (PennantConstants.RECORD_TYPE_DEL.equals(aCollateralSetup.getRecordType())) {
						if (StringUtils.trimToNull(details.getRecordType()) == null) {
							details.setRecordType(aCollateralSetup.getRecordType());
							details.setNewRecord(true);
						}
					}
				}
			}

			auditHeader = getAuditHeader(aCollateralSetup, tranType);
			String operationRefs = getServiceOperations(taskId, aCollateralSetup);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader, null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(aCollateralSetup, PennantConstants.TRAN_WF);
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		} else {
			auditHeader = getAuditHeader(aCollateralSetup, tranType);
			processCompleted = doSaveProcess(auditHeader, null);
		}
		logger.debug(Literal.LEAVING);
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
		CollateralSetup collateralSetup = (CollateralSetup) auditHeader.getAuditDetail().getModelData();
		boolean deleteNotes = false;

		while (retValue == PennantConstants.porcessOVERIDE) {
			if (StringUtils.isBlank(method)) {
				if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {
					auditHeader = collateralSetupService.delete(auditHeader);
					deleteNotes = true;
				} else {
					auditHeader = collateralSetupService.saveOrUpdate(auditHeader);
				}
			} else {
				if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
					auditHeader = collateralSetupService.doApprove(auditHeader);

					if (collateralSetup.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
						deleteNotes = true;
					}
				} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
					auditHeader = collateralSetupService.doReject(auditHeader);

					if (collateralSetup.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
						deleteNotes = true;
					}
				} else {
					auditHeader.setErrorDetails(
							new ErrorDetail(PennantConstants.ERR_9999, Labels.getLabel("InvalidWorkFlowMethod"), null));
					retValue = ErrorControl.showErrorControl(this.window_CollateralSetupDialog, auditHeader);
					return processCompleted;
				}
			}
			auditHeader = ErrorControl.showErrorDetails(this.window_CollateralSetupDialog, auditHeader);
			retValue = auditHeader.getProcessStatus();

			if (retValue == PennantConstants.porcessCONTINUE) {
				processCompleted = true;

				if (deleteNotes) {
					deleteNotes(getNotes(this.collateralSetup), true);
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
		logger.debug(Literal.LEAVING);
		return processCompleted;
	}

	/**
	 * Method for setting Basic Details on Selecting Assignment Details Tab and render the assignment details.
	 * 
	 * @param event
	 */
	private void renderAssignmentDetails() {
		logger.debug("Entering");

		if (!getCollateralSetup().isNewRecord()
				&& !StringUtils.equals(getCollateralSetup().getRecordType(), PennantConstants.RECORD_TYPE_NEW)) {

			this.label_DepositorCif.setValue(this.depositorCif.getValue());
			this.label_CollateralRef.setValue(this.collateralRef.getValue());
			this.label_DepositorName.setValue(this.depositerName.getValue());
			this.label_Currency.setValue(this.collateralCcy.getValue());
			this.label_CollateralType.setValue(this.collateralType.getValue());
			this.label_CollateralLoc.setValue(this.collateralLoc.getValue());
			this.label_assignedBankValuation.setValue(BigDecimal.ZERO);
			this.label_assignedColllValue.setValue(BigDecimal.ZERO);
			this.label_assignedPercBankValuation.setValue(BigDecimal.ZERO);
			this.label_availableForAssignment.setValue(BigDecimal.ZERO);

			// Only once is enough for rendering details in Maintenance
			doFillAssignmentDetails(getCollateralSetup().getAssignmentDetails());
		} else {
			this.assignmentDetailTab.setVisible(false);
		}

		logger.debug("Leaving");
	}

	/**
	 * Method for setting Basic Details on Selecting Movement Details Tab and render the Movement details.
	 * 
	 * @param event
	 */
	private void renderMovementDetails() {
		logger.debug("Entering");

		if (!getCollateralSetup().isNewRecord()
				&& !StringUtils.equals(getCollateralSetup().getRecordType(), PennantConstants.RECORD_TYPE_NEW)) {
			this.movementsTab.setVisible(true);
		} else {
			this.movementsTab.setVisible(false);
		}

		logger.debug("Leaving");
	}

	/**
	 * Method for Rendering Collateral Assignments from loan or Commitment
	 * 
	 * @param assignmentDetailList
	 */
	private void doFillAssignmentDetails(List<AssignmentDetails> assignmentDetailList) {
		logger.debug("Entering");

		this.listBoxAssignmentDetail.getItems().clear();
		BigDecimal totalAssignColValue = BigDecimal.ZERO;

		int assignedCount = 0;
		if (getCollateralSetup().getAssignmentDetails() != null
				&& !getCollateralSetup().getAssignmentDetails().isEmpty()) {
			for (AssignmentDetails assignmentDetail : assignmentDetailList) {
				Listitem item = new Listitem();
				Listcell lc;

				String moduleName = assignmentDetail.getModule();
				if (FinanceConstants.MODULE_NAME.equals(moduleName)) {
					moduleName = Labels.getLabel("label_Finance");
				}
				lc = new Listcell(moduleName);
				item.appendChild(lc);

				lc = new Listcell(assignmentDetail.getReference());
				item.appendChild(lc);

				lc = new Listcell(assignmentDetail.getCurrency());
				item.appendChild(lc);

				lc = new Listcell(
						PennantApplicationUtil.formatRate(assignmentDetail.getAssignedPerc().doubleValue(), 2));
				lc.setStyle("text-align:right;");
				item.appendChild(lc);

				BigDecimal assignedvalue = (assignmentDetail.getCollateralValue()
						.multiply(assignmentDetail.getAssignedPerc())).divide(new BigDecimal(100), 0,
								RoundingMode.HALF_DOWN);

				lc = new Listcell(PennantApplicationUtil.amountFormate(assignedvalue, getCcyFormat()));
				totalAssignColValue = totalAssignColValue.add(assignedvalue);
				lc.setStyle("text-align:right;");
				item.appendChild(lc);

				BigDecimal utilizedAmt = BigDecimal.ZERO;
				BigDecimal finAssetValue = assignmentDetail.getFinAssetValue();
				BigDecimal totalUtilized = assignmentDetail.getTotalUtilized();
				BigDecimal finCurrentAssetValue = assignmentDetail.getFinCurrAssetValue();

				if (finAssetValue == null) {
					finAssetValue = BigDecimal.ZERO;
				}

				if (totalUtilized == null) {
					totalUtilized = BigDecimal.ZERO;
				}

				if (finCurrentAssetValue == null) {
					finCurrentAssetValue = BigDecimal.ZERO;
				}

				if (totalUtilized.compareTo(BigDecimal.ZERO) > 0) {
					if (PennantConstants.COLLATERAL_LTV_CHECK_FINAMT.equals(assignmentDetail.getFinLTVCheck())) {
						utilizedAmt = assignedvalue.multiply(finAssetValue).divide(totalUtilized, 0,
								RoundingMode.HALF_DOWN);
					} else {
						utilizedAmt = assignedvalue.multiply(finCurrentAssetValue).divide(totalUtilized, 0,
								RoundingMode.HALF_DOWN);
					}
				}

				lc = new Listcell(PennantApplicationUtil.amountFormate(utilizedAmt, getCcyFormat()));
				lc.setStyle("text-align:right;");
				item.appendChild(lc);

				BigDecimal avalUtiAmt = assignedvalue.subtract(utilizedAmt);
				boolean expired = false;
				if (StringUtils.equals(assignmentDetail.getModule(), FinanceConstants.MODULE_NAME)
						&& !assignmentDetail.isFinIsActive()) {
					avalUtiAmt = assignedvalue;
					expired = true;
				} else if (StringUtils.equals(assignmentDetail.getModule(), CommitmentConstants.MODULE_NAME)
						&& DateUtil.compare(assignmentDetail.getCmtExpDate(), SysParamUtil.getAppDate()) < 0) {
					avalUtiAmt = assignedvalue;
					expired = true;
				}

				lc = new Listcell(PennantApplicationUtil.amountFormate(avalUtiAmt, getCcyFormat()));
				lc.setStyle("text-align:right;");
				item.appendChild(lc);

				lc = new Listcell(expired ? "True" : "False");
				if (expired) {
					lc.setStyle("color:red;");
				} else {
					assignedCount = assignedCount + 1;
				}
				item.appendChild(lc);

				this.listBoxAssignmentDetail.appendChild(item);
			}
		}

		// Assignment Details Resetting
		this.label_assignedBankValuation.setValue(this.bankValuation.getActualValue());
		this.label_assignedColllValue
				.setValue(PennantApplicationUtil.formateAmount(totalAssignColValue, getCcyFormat()));

		BigDecimal bankValPerc = BigDecimal.ZERO;
		BigDecimal bankVal = BigDecimal.ZERO;
		if (this.bankValuation.getActualValue().compareTo(BigDecimal.ZERO) > 0) {
			bankVal = PennantApplicationUtil.unFormateAmount(this.bankValuation.getActualValue(), getCcyFormat());
			if (bankVal.compareTo(BigDecimal.ZERO) > 0) {
				bankValPerc = totalAssignColValue.multiply(new BigDecimal(100)).divide(bankVal, 0,
						RoundingMode.HALF_DOWN);
			}
		}

		this.label_assignedPercBankValuation.setValue(PennantApplicationUtil.formatRate(bankValPerc.doubleValue(), 2));
		this.label_availableForAssignment
				.setValue(PennantApplicationUtil.formateAmount(bankVal.subtract(totalAssignColValue), getCcyFormat()));

		// Basic Details populations for assignments
		this.assignedLoansCount.setValue(assignedCount);
		this.assignedCollateralValue
				.setValue(PennantApplicationUtil.formateAmount(totalAssignColValue, getCcyFormat()));
		BigDecimal assignPercToColValue = BigDecimal.ZERO;
		if (this.collateralValue.getActualValue().compareTo(BigDecimal.ZERO) > 0) {
			BigDecimal colValue = PennantApplicationUtil.unFormateAmount(this.collateralValue.getActualValue(),
					getCcyFormat());
			assignPercToColValue = totalAssignColValue.multiply(new BigDecimal(100)).divide(colValue, 0,
					RoundingMode.HALF_DOWN);
		}
		this.assignedPercToCollateralValue
				.setValue(PennantApplicationUtil.formatRate(assignPercToColValue.doubleValue(), 2));
		this.assignedPercToBankValuation.setValue(PennantApplicationUtil.formatRate(bankValPerc.doubleValue(), 2));

		logger.debug("Leaving");
	}

	/**
	 * Method for Rendering Collateral Assignments from loan or Commitment
	 * 
	 * @param movementList
	 */
	private void doFillMovementDetails(List<CollateralMovement> movementList) {
		logger.debug("Entering");

		this.listBoxMovements.getItems().clear();
		if (movementList != null && !movementList.isEmpty()) {
			for (CollateralMovement movement : movementList) {
				Listitem item = new Listitem();
				Listcell lc;

				String moduleName = movement.getModule();
				if (FinanceConstants.MODULE_NAME.equals(moduleName)) {
					moduleName = Labels.getLabel("label_Finance");
				}
				lc = new Listcell(moduleName);
				item.appendChild(lc);

				lc = new Listcell(movement.getReference());
				item.appendChild(lc);

				lc = new Listcell(PennantApplicationUtil.formatRate(movement.getAssignPerc().doubleValue(), 2));
				lc.setStyle("text-align:right;");
				item.appendChild(lc);

				lc = new Listcell(DateUtil.format(movement.getValueDate(), DateFormat.LONG_DATE.getPattern()));
				item.appendChild(lc);
				this.listBoxMovements.appendChild(item);
			}
		}

		logger.debug("Leaving");
	}

	/**
	 * Method for resetting Default header details
	 * 
	 * @param event
	 */
	public void onSelect$extendedDetailsTab(Event event) {
		if (getExtendedFieldRenderDialogCtrl() != null) {
			getExtendedFieldRenderDialogCtrl().doSetLabels(getHeaderBasicDetails());
		}
	}

	/**
	 * Method for resetting Default header details
	 * 
	 * @param event
	 */
	public void onSelect$assignmentDetailTab(Event event) {
		doFillAssignmentDetails(getCollateralSetup().getAssignmentDetails());
	}

	/**
	 * Method for resetting Default header details
	 * 
	 * @param event
	 */
	public void onSelect$movementsTab(Event event) {
		logger.debug("Entering");
		this.label_Movement_DepositorCif.setValue(this.depositorCif.getValue());
		this.label_Movement_CollateralRef.setValue(this.collateralRef.getValue());
		this.label_Movement_DepositorName.setValue(this.depositerName.getValue());
		this.label_Movement_Currency.setValue(this.collateralCcy.getValue());
		this.label_Movement_CollateralType.setValue(this.collateralType.getValue());
		this.label_Movement_CollateralLoc.setValue(this.collateralLoc.getValue());

		List<CollateralMovement> movementList = getCollateralSetupService()
				.getCollateralMovements(this.collateralRef.getValue());
		doFillMovementDetails(movementList);

		movementsTab.removeForward(Events.ON_SELECT, this.window, "onSelect$movementsTab");
		logger.debug("Leaving");
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// +++++++++++++++++ WorkFlow Components+++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	/**
	 * @param aAuthorizedSignatoryRepository
	 * @param tranType
	 * @return
	 */

	private AuditHeader getAuditHeader(CollateralSetup aCollateralSetup, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aCollateralSetup.getBefImage(), aCollateralSetup);
		return new AuditHeader(aCollateralSetup.getCollateralRef(), null, null, null, auditDetail,
				aCollateralSetup.getUserDetails(), getOverideMap());
	}

	/**
	 * The framework calls this event handler when user clicks the notes button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$btnNotes(Event event) {
		doShowNotes(this.collateralSetup);
	}

	/**
	 * Refresh the list page with the filters that are applied in list page.
	 */
	protected void refreshList() {
		collateralSetupListCtrl.refreshList();
	}

	private String getTabID(String id) {
		return "TAB" + StringUtils.trimToEmpty(id);
	}

	private String getTabpanelID(String id) {
		return "TABPANEL" + StringUtils.trimToEmpty(id);
	}

	public Map<String, Object> getDefaultArguments() {
		final Map<String, Object> map = new HashMap<String, Object>();
		map.put("roleCode", getRole());
		map.put("financeMainDialogCtrl", this);
		map.put("finHeaderList", getHeaderBasicDetails());
		map.put("isNotFinanceProcess", true);
		map.put("moduleName", CollateralConstants.MODULE_NAME);
		return map;
	}

	private Tabpanel getTabpanel(String id) {
		return (Tabpanel) tabpanelsBoxIndexCenter.getFellowIfAny(getTabpanelID(id));
	}

	private Tab getTab(String id) {
		return (Tab) tabsIndexCenter.getFellowIfAny(getTabID(id));
	}

	private String getIDbyTab(String tabID) {
		return tabID.replace("TAB", "");
	}

	private void clearTabpanelChildren(String id) {
		Tabpanel tabpanel = getTabpanel(id);
		if (tabpanel != null) {
			tabpanel.setStyle("overflow:auto;");
			tabpanel.getChildren().clear();
		}
	}

	/**
	 * This method will create tab and will assign corresponding tab selection method and makes tab visibility based on
	 * parameter
	 * 
	 * @param moduleID
	 * @param tabVisible
	 */
	public void createTab(String moduleID, boolean tabVisible) {
		logger.debug("Entering");
		String tabName = Labels.getLabel("tab_label_" + moduleID);
		Tab tab = new Tab(tabName);
		tab.setId(getTabID(moduleID));
		tab.setVisible(tabVisible);
		tabsIndexCenter.appendChild(tab);
		Tabpanel tabpanel = new Tabpanel();
		tabpanel.setId(getTabpanelID(moduleID));
		tabpanel.setStyle("overflow:auto;");
		tabpanel.setParent(tabpanelsBoxIndexCenter);
		tabpanel.setHeight("100%");
		ComponentsCtrl.applyForward(tab, ("onSelect=" + selectMethodName));
		logger.debug("Leaving");
	}

	public void onSelectTab(ForwardEvent event) {

		Tab tab = (Tab) event.getOrigin().getTarget();
		logger.debug(tab.getId() + " --> " + "Entering");
		String module = getIDbyTab(tab.getId());
		doRemoveValidation();
		doClearMessage();

		switch (module) {
		case AssetConstants.UNIQUE_ID_CUSTOMERS:
			if (customerDialogCtrl != null) {
				customerDialogCtrl.doSetLabels(getHeaderBasicDetails());
			} else {
				appendCustomerDetailTab(false);
			}
			break;
		case AssetConstants.UNIQUE_ID_ADDITIONALFIELDS:
			if (customerDialogCtrl != null) {
				customerDialogCtrl.doSetLabels(getHeaderBasicDetails());
			} else {
				appendCustomerDetailTab(false);
			}
			break;
		case AssetConstants.UNIQUE_ID_AGREEMENT:
			this.doWriteComponentsToBean(getCollateralSetup());

			if (agreementDetailDialogCtrl != null) {
				agreementDetailDialogCtrl.doSetLabels(getHeaderBasicDetails());
				agreementDetailDialogCtrl.doShowDialog(false);
			} else {
				appendAgreementsDetailTab(false);
			}
			break;
		case AssetConstants.UNIQUE_ID_CHECKLIST:
			this.doWriteComponentsToBean(getCollateralSetup());
			if (financeCheckListReferenceDialogCtrl != null) {
				financeCheckListReferenceDialogCtrl.doSetLabels(getHeaderBasicDetails());
				financeCheckListReferenceDialogCtrl.doWriteBeanToComponents(getCollateralSetup().getCheckLists(),
						getCollateralSetup().getCollateralCheckLists(), false);
			}
			break;
		case AssetConstants.UNIQUE_ID_DOCUMENTDETAIL:
			if (documentDetailDialogCtrl != null) {
				documentDetailDialogCtrl.doSetLabels(getHeaderBasicDetails());
			}
			break;
		case AssetConstants.UNIQUE_ID_RECOMMENDATIONS:
			tab.removeForward(Events.ON_SELECT, (Tab) null, selectMethodName);
			appendRecommendDetailTab(false);
			break;
		default:
			break;
		}

		logger.debug(tab.getId() + " --> " + "Leaving");
	}

	/**
	 * fill finance basic details to List
	 * 
	 * @return
	 */
	private ArrayList<Object> getHeaderBasicDetails() {
		ArrayList<Object> arrayList = new ArrayList<Object>();
		arrayList.add(0, this.depositorCif.getValue());
		arrayList.add(1, this.collateralRef.getValue());
		arrayList.add(2, this.depositerName.getValue());
		arrayList.add(3, this.collateralCcy.getValue());
		arrayList.add(4, this.collateralType.getValue());
		arrayList.add(5, this.collateralLoc.getValue());
		return arrayList;
	}

	/**
	 * Method for Setting default values based on Extended field List entries
	 * 
	 * @param noOfUnits
	 * @param collateralValue
	 */
	public void setDeafultValues(int noOfUnits, BigDecimal collateralValue) {

		int ccyFormat = CurrencyUtil.getFormat(this.collateralCcy.getValue());

		this.numberOfUnits.setValue(noOfUnits);
		this.collateralValue.setValue(PennantApplicationUtil.formateAmount(collateralValue, ccyFormat));
		calBankValuation();
		if (noOfUnits > 0) {
			this.unitPrice.setValue(PennantApplicationUtil.formateAmount(
					collateralValue.divide(new BigDecimal(noOfUnits), ccyFormat, RoundingMode.HALF_DOWN), ccyFormat));
		}

		if (StringUtils.equals(getCollateralSetup().getCollateralStructure().getLtvType(),
				CollateralConstants.VARIABLE_LTV)) {
			setBankLTV();
		}
	}

	/**
	 * Method for Fetching Document Details for Checklist processing
	 * 
	 * @return
	 */
	public List<DocumentDetails> getDocumentDetails() {
		if (documentDetailDialogCtrl != null) {
			return documentDetailDialogCtrl.getDocumentDetailsList();
		}
		return new ArrayList<DocumentDetails>();
	}

	/**
	 * Method for fetching Customer Basic Details for Document Details processing
	 * 
	 * @return
	 */
	public List<Object> getCustomerBasicDetails() {

		List<Object> custBasicDetails = null;
		if (collateralSetup.getCustomerDetails() != null
				&& collateralSetup.getCustomerDetails().getCustomer() != null) {
			custBasicDetails = new ArrayList<>();
			custBasicDetails.add(collateralSetup.getCustomerDetails().getCustomer().getCustID());
			custBasicDetails.add(collateralSetup.getCustomerDetails().getCustomer().getCustCIF());
			custBasicDetails.add(collateralSetup.getCustomerDetails().getCustomer().getCustShrtName());
		}
		return custBasicDetails;
	}

	/**
	 * Method for fetching Customer Id number for Document Details processing
	 * 
	 * @return
	 */
	public long getCustomerIDNumber() {
		if (collateralSetup.getCustomerDetails() != null
				&& collateralSetup.getCustomerDetails().getCustomer() != null) {
			return collateralSetup.getCustomerDetails().getCustomer().getCustID();
		}
		return 0;
	}

	public boolean isReadOnly(String componentName) {
		boolean collateralAssignmentWorkFlow = false;
		if (getCollateralAssignmentDialogCtrl() != null && getFinanceDetail() != null) {
			FinanceMain financemain = getFinanceDetail().getFinScheduleData().getFinanceMain();
			collateralAssignmentWorkFlow = financemain.isWorkflow();
		}
		if (isWorkFlowEnabled() || collateralAssignmentWorkFlow) {
			return getUserWorkspace().isReadOnly(componentName);
		}
		return false;
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public CollateralSetup getCollateralSetup() {
		return this.collateralSetup;
	}

	public void setCollateralSetup(CollateralSetup collateralSetup) {
		this.collateralSetup = collateralSetup;
	}

	public void setCollateralSetupService(CollateralSetupService collateralSetupService) {
		this.collateralSetupService = collateralSetupService;
	}

	public CollateralSetupService getCollateralSetupService() {
		return this.collateralSetupService;
	}

	public void setCollateralSetupListCtrl(CollateralSetupListCtrl collateralSetupListCtrl) {
		this.collateralSetupListCtrl = collateralSetupListCtrl;
	}

	public CollateralSetupListCtrl getCollateralSetupListCtrl() {
		return this.collateralSetupListCtrl;
	}

	public List<CoOwnerDetail> getCoOwnerDetailList() {
		return coOwnerDetailList;
	}

	public void setCoOwnerDetailList(List<CoOwnerDetail> coOwnerDetailList) {
		this.coOwnerDetailList = coOwnerDetailList;
	}

	public List<CollateralThirdParty> getCollateralThirdPartyList() {
		return collateralThirdPartyList;
	}

	public void setCollateralThirdPartyList(List<CollateralThirdParty> collateralThirdPartyList) {
		this.collateralThirdPartyList = collateralThirdPartyList;
	}

	public DocumentDetailDialogCtrl getDocumentDetailDialogCtrl() {
		return documentDetailDialogCtrl;
	}

	public void setDocumentDetailDialogCtrl(DocumentDetailDialogCtrl documentDetailDialogCtrl) {
		this.documentDetailDialogCtrl = documentDetailDialogCtrl;
	}

	public CustomerDialogCtrl getCustomerDialogCtrl() {
		return customerDialogCtrl;
	}

	public void setCustomerDialogCtrl(CustomerDialogCtrl customerDialogCtrl) {
		this.customerDialogCtrl = customerDialogCtrl;
	}

	public AgreementDetailDialogCtrl getAgreementDetailDialogCtrl() {
		return agreementDetailDialogCtrl;
	}

	public void setAgreementDetailDialogCtrl(AgreementDetailDialogCtrl agreementDetailDialogCtrl) {
		this.agreementDetailDialogCtrl = agreementDetailDialogCtrl;
	}

	public FinanceCheckListReferenceDialogCtrl getFinanceCheckListReferenceDialogCtrl() {
		return financeCheckListReferenceDialogCtrl;
	}

	public void setFinanceCheckListReferenceDialogCtrl(
			FinanceCheckListReferenceDialogCtrl financeCheckListReferenceDialogCtrl) {
		this.financeCheckListReferenceDialogCtrl = financeCheckListReferenceDialogCtrl;
	}

	public List<ExtendedFieldRender> getExtendedFieldRenderList() {
		return extendedFieldRenderList;
	}

	public void setExtendedFieldRenderList(List<ExtendedFieldRender> extendedFieldRenderList) {
		this.extendedFieldRenderList = extendedFieldRenderList;
	}

	public List<FinanceCheckListReference> getCollateralChecklists() {
		return collateralChecklists;
	}

	public void setCollateralChecklists(List<FinanceCheckListReference> collateralChecklists) {
		this.collateralChecklists = collateralChecklists;
	}

	public Map<Long, Long> getSelectedAnsCountMap() {
		return selectedAnsCountMap;
	}

	public void setSelectedAnsCountMap(Map<Long, Long> selectedAnsCountMap) {
		this.selectedAnsCountMap = selectedAnsCountMap;
	}

	public List<FinFlagsDetail> getFinFlagsDetailList() {
		return finFlagsDetailList;
	}

	public void setFinFlagsDetailList(List<FinFlagsDetail> finFlagsDetailList) {
		this.finFlagsDetailList = finFlagsDetailList;
	}

	public ExtendedFieldRenderDialogCtrl getExtendedFieldRenderDialogCtrl() {
		return extendedFieldRenderDialogCtrl;
	}

	public void setExtendedFieldRenderDialogCtrl(ExtendedFieldRenderDialogCtrl extendedFieldRenderDialogCtrl) {
		this.extendedFieldRenderDialogCtrl = extendedFieldRenderDialogCtrl;
	}

	public void setNotificationService(NotificationService notificationService) {
		this.notificationService = notificationService;
	}

	public FinanceReferenceDetailService getFinanceReferenceDetailService() {
		return financeReferenceDetailService;
	}

	public void setFinanceReferenceDetailService(FinanceReferenceDetailService financeReferenceDetailService) {
		this.financeReferenceDetailService = financeReferenceDetailService;
	}

	public Map<String, Object> getFlagTypeDataMap() {
		return flagTypeDataMap;
	}

	public void setFlagTypeDataMap(Map<String, Object> flagTypeDataMap) {
		this.flagTypeDataMap = flagTypeDataMap;
	}

	public CollateralAssignmentDialogCtrl getCollateralAssignmentDialogCtrl() {
		return collateralAssignmentDialogCtrl;
	}

	public void setCollateralAssignmentDialogCtrl(CollateralAssignmentDialogCtrl collateralAssignmentDialogCtrl) {
		this.collateralAssignmentDialogCtrl = collateralAssignmentDialogCtrl;
	}

	public FinanceDetail getFinanceDetail() {
		return financeDetail;
	}

	public void setFinanceDetail(FinanceDetail financeDetail) {
		this.financeDetail = financeDetail;
	}

}
