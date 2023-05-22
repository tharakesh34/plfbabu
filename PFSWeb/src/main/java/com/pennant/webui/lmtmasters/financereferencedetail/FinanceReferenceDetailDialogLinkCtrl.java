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
 * * FileName : FinanceReferenceDetailDialogCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 26-11-2011
 * * * Modified Date : 26-11-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 26-11-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.webui.lmtmasters.financereferencedetail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Longbox;
import org.zkoss.zul.Row;
import org.zkoss.zul.Space;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.backend.delegationdeviation.DeviationHelper;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.applicationmaster.AgreementDefinition;
import com.pennant.backend.model.applicationmaster.StageTabDetail;
import com.pennant.backend.model.bmtmasters.CheckList;
import com.pennant.backend.model.dedup.DedupParm;
import com.pennant.backend.model.finance.TATNotificationCode;
import com.pennant.backend.model.limits.LimitCodeDetail;
import com.pennant.backend.model.lmtmasters.FinanceReferenceDetail;
import com.pennant.backend.model.lmtmasters.ProcessEditorDetail;
import com.pennant.backend.model.rmtmasters.ScoringGroup;
import com.pennant.backend.model.rulefactory.Notifications;
import com.pennant.backend.model.rulefactory.Rule;
import com.pennant.backend.service.PagedListService;
import com.pennant.backend.service.lmtmasters.FinanceReferenceDetailService;
import com.pennant.backend.service.lmtmasters.FinanceWorkFlowService;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.NotificationConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.backend.util.RuleConstants;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.util.Constraint.PTNumberValidator;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.searchdialogs.ExtendedSearchListBox;
import com.pennanttech.pennapps.core.engine.workflow.WorkflowEngine;
import com.pennanttech.pennapps.core.engine.workflow.WorkflowEngine.Flow;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.jdbc.search.Filter;
import com.pennanttech.pennapps.pff.verification.VerificationType;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pff.constants.FinServiceEvent;

/**
 * This is the controller class for the /WEB-INF/pages/LMTMasters/FinanceReferenceDetail
 * /financeReferenceDetailDialog.zul file.
 */
public class FinanceReferenceDetailDialogLinkCtrl extends GFCBaseCtrl<FinanceReferenceDetail> {
	private static final long serialVersionUID = -2872130825329784644L;
	private static final Logger logger = LogManager.getLogger(FinanceReferenceDetailDialogLinkCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the zul-file
	 * are getting auto wired by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_FinanceReferenceDetailDialogLink; // auto wired
	protected Textbox finType; // auto wired
	protected Intbox finRefType; // auto wired
	protected Longbox finRefId; // auto wired
	protected Checkbox isActive; // auto wired
	protected Textbox showInStage; // auto wired
	protected Textbox mandInputInStage; // auto wired
	protected Textbox allowInputInStage; // auto wired
	protected Checkbox overRide;
	protected Intbox overRideValue;
	protected Space space_Override;
	protected Textbox lovDescRefDesc;
	protected Checkbox allowDeviation;
	protected Checkbox allowWaiver;
	protected Checkbox allowPostpone;
	protected Checkbox allowExpire;
	protected Hbox hboxWaiver;
	protected Row row_AlertType;
	protected Combobox alertType;
	protected Checkbox reSend;

	// not auto wired variables
	private FinanceReferenceDetail financeReferenceDetail; // over handed per parameter
	private transient FinanceReferenceDetailDialogCtrl financeReferenceDetailDialogCtrl; // over handed per parameter

	private transient boolean validationOn;

	// ServiceDAOs / Domain Classes
	private transient FinanceReferenceDetailService financeReferenceDetailService;
	private transient PagedListService pagedListService;
	private Map<String, List<ErrorDetail>> overideMap = new HashMap<String, List<ErrorDetail>>();
	private String roleCodes;
	private String delegatorRoles;
	private String moduleName;
	private String eventAction;

	protected Listbox listboxshowInStage; // auto wired
	protected Listbox listboxmandInputInStage; // auto wired
	protected Listbox listboxallowInputInStage;

	private Map<String, String> checkShowInStageMap = new HashMap<String, String>();
	private Map<String, String> checkMandInputInStageMap = new HashMap<String, String>();
	private Map<String, String> checkAllowInputInStage = new HashMap<String, String>();

	// List od values
	protected Button btnSearchElgRule;
	protected Button btnSearchAggCode;
	protected Button btnSearchQuestionId;
	protected Button btnSearchScoringGroup;
	protected Button btnSearchCorpScoringGroup;
	protected Button btnSearchAccounting;
	protected Button btnSearchTemplate;
	protected Button btnSearchFinanceDedupe;
	protected Button btnSearchCustomerDedupe;
	protected Button btnSearchBlackListDedupe;
	protected Button btnSearchReturnCheque;
	protected Button btnSearchLimitService;
	protected Button btnSearchTatNotification;
	protected Button btnFinanceTabs;
	protected Label label_FinanceReferenceDetailDialog_FinRefId;
	protected Row rowSingleListbox;
	protected Row rowDoubleListbox;
	protected Row rowOverRide;
	protected Row rowDeviation;
	protected Row rowPostpone;
	protected Row row_Resend;

	protected Label label_FinanceReferenceDetailDialogLink;

	protected Label label_FinanceReferenceDetailDialog_ShowInStage;
	protected Label label_FinanceReferenceDetailDialog_AllowInputInStage;
	protected Label label_FinanceReferenceDetailDialog_MandInputInStage;
	protected Label label_FinanceReferenceDetailDialog_AllowWaiver;

	protected Listheader listheadShowInStage;
	protected Listheader listheadAllowInputInStage;
	protected Listheader listheadMandInputInStage;

	private transient FinanceWorkFlowService financeWorkFlowService;
	boolean canRaiseManualDeviation = true;
	@Autowired
	private DeviationHelper deviationHelper;
	private int referenceType = 0;
	private boolean isChecked_ROA = false;

	/**
	 * default constructor.<br>
	 */
	public FinanceReferenceDetailDialogLinkCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "FinanceReferenceDetailDialog";
	}

	// Component Events

	/**
	 * Before binding the data and calling the dialog window we check, if the zul-file is called with a parameter for a
	 * selected FinanceReferenceDetail object in a Map.
	 * 
	 * @param event
	 */
	public void onCreate$window_FinanceReferenceDetailDialogLink(Event event) {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_FinanceReferenceDetailDialogLink);

		try {

			/* set components visible dependent of the users rights */
			doCheckRights();
			if (arguments.containsKey("financeReferenceDetail")) {
				this.financeReferenceDetail = (FinanceReferenceDetail) arguments.get("financeReferenceDetail");
				referenceType = financeReferenceDetail.getFinRefType();

				boolean addBefImage = true;
				if (PennantConstants.RECORD_TYPE_NEW.equals(this.financeReferenceDetail.getRecordType())
						|| PennantConstants.RECORD_TYPE_UPD.equals(this.financeReferenceDetail.getRecordType())) {
					if (!this.financeReferenceDetail.isNewRecord()) {
						addBefImage = false;
					}
				}
				if (addBefImage) {
					FinanceReferenceDetail befImage = new FinanceReferenceDetail();
					BeanUtils.copyProperties(this.financeReferenceDetail, befImage);
					this.financeReferenceDetail.setBefImage(befImage);
				}
				setFinanceReferenceDetail(this.financeReferenceDetail);
			} else {
				setFinanceReferenceDetail(null);
			}

			if (arguments.containsKey("financeReferenceDetailDialogCtrl")) {
				setFinanceReferenceDetailDialogCtrl(
						(FinanceReferenceDetailDialogCtrl) arguments.get("financeReferenceDetailDialogCtrl"));
			} else {
				setFinanceReferenceDetailDialogCtrl(null);
			}

			if (arguments.containsKey("roleCodeList")) {
				roleCodes = arguments.get("roleCodeList").toString();
			}

			if (arguments.containsKey("moduleName")) {
				moduleName = (String) arguments.get("moduleName");
			}
			if (arguments.containsKey("eventAction")) {
				eventAction = (String) arguments.get("eventAction");
			}

			// ### 06-05-2018 - story #361(Tuleap server) Manual Deviations
			if (referenceType == FinanceConstants.PROCEDT_LIMIT) {
				delegatorRoles = getDelegatorRoles();
			}

			doLoadWorkFlow(this.financeReferenceDetail.isWorkflow(), this.financeReferenceDetail.getWorkflowId(),
					this.financeReferenceDetail.getNextTaskId());

			if (isWorkFlowEnabled()) {
				this.userAction = setListRecordStatus(this.userAction);
				getUserWorkspace().allocateRoleAuthorities(getRole(), "FinanceReferenceDetailDialog");
			}

			// set Field Properties
			doSetFieldProperties();
			doShowDialog(getFinanceReferenceDetail());
		} catch (Exception e) {
			MessageUtil.showError(e);
			this.window_FinanceReferenceDetailDialogLink.onClose();
		}
		logger.debug("Leaving");
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");
		// Empty sent any required attributes

		this.finType.setMaxlength(8);
		this.finRefType.setMaxlength(10);
		this.overRideValue.setMaxlength(4);

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
		getUserWorkspace().allocateAuthorities(super.pageRightName);
		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_FinanceReferenceDetailDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_FinanceReferenceDetailDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_FinanceReferenceDetailDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_FinanceReferenceDetailDialog_btnSave"));
		this.btnCancel.setVisible(false);
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
		MessageUtil.showHelpWindow(event, window_FinanceReferenceDetailDialogLink);
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
	 * @param event An event sent to the event handler of a component.
	 */
	public void onClick$btnClose(Event event) {
		doClose(this.btnSave.isVisible());
	}

	protected void doPostClose() {
		getFinanceReferenceDetailDialogCtrl().window_FinanceReferenceDetailDialog.setVisible(true);
	}

	/**
	 * Cancel the actual operation. <br>
	 * <br>
	 * Resets to the original status.<br>
	 * 
	 */
	private void doCancel() {
		logger.debug("Entering");
		doWriteBeanToComponents(this.financeReferenceDetail.getBefImage());
		doReadOnly();
		this.btnCtrl.setInitEdit();
		this.btnEdit.setVisible(true);
		this.btnCancel.setVisible(false);
		this.btnDelete.setVisible(true);
		logger.debug("Leaving");
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aFinanceReferenceDetail FinanceReferenceDetail
	 */
	public void doWriteBeanToComponents(FinanceReferenceDetail aFinanceReferenceDetail) {
		logger.debug("Entering");

		this.finType.setValue(aFinanceReferenceDetail.getFinType());
		this.finRefType.setValue(aFinanceReferenceDetail.getFinRefType());
		this.finRefId.setValue(aFinanceReferenceDetail.getFinRefId());
		this.isActive.setChecked(aFinanceReferenceDetail.isIsActive());
		this.showInStage.setValue(aFinanceReferenceDetail.getShowInStage());
		this.mandInputInStage.setValue(aFinanceReferenceDetail.getMandInputInStage());
		this.allowInputInStage.setValue(aFinanceReferenceDetail.getAllowInputInStage());
		this.overRide.setChecked(aFinanceReferenceDetail.isOverRide());
		this.overRideValue.setValue(aFinanceReferenceDetail.getOverRideValue());
		this.lovDescRefDesc.setValue(aFinanceReferenceDetail.getLovDescRefDesc());
		this.allowDeviation.setChecked(aFinanceReferenceDetail.isAllowDeviation());
		this.allowWaiver.setChecked(aFinanceReferenceDetail.isAllowWaiver());
		this.allowPostpone.setChecked(aFinanceReferenceDetail.isAllowPostpone());
		this.allowExpire.setChecked(aFinanceReferenceDetail.isAllowExpire());
		this.reSend.setChecked(aFinanceReferenceDetail.isResendReq());

		if (aFinanceReferenceDetail.getShowInStage() != null
				&& StringUtils.isNotEmpty(aFinanceReferenceDetail.getShowInStage())) {
			String[] roles = aFinanceReferenceDetail.getShowInStage().split(",");
			for (int i = 0; i < roles.length; i++) {
				checkShowInStageMap.put(roles[i], roles[i]);
			}
		}
		if (aFinanceReferenceDetail.getAllowInputInStage() != null
				&& StringUtils.isNotEmpty(aFinanceReferenceDetail.getAllowInputInStage())) {
			String[] roles = aFinanceReferenceDetail.getAllowInputInStage().split(",");
			for (int i = 0; i < roles.length; i++) {
				checkAllowInputInStage.put(roles[i], roles[i]);
			}
		}
		if (aFinanceReferenceDetail.getMandInputInStage() != null
				&& StringUtils.isNotEmpty(aFinanceReferenceDetail.getMandInputInStage())) {
			String[] roles = aFinanceReferenceDetail.getMandInputInStage().split(",");
			for (int i = 0; i < roles.length; i++) {
				checkMandInputInStageMap.put(roles[i], roles[i]);
			}
			if (Arrays.asList(roles).contains("REC_ON_APPROVAL")) {
				isChecked_ROA = true;
			}
		}

		fillListBox(this.listboxshowInStage, roleCodes, checkShowInStageMap, FinanceConstants.PROCEDT_SHOWINSTAGE);
		fillListBox(this.listboxallowInputInStage, roleCodes, checkAllowInputInStage,
				FinanceConstants.PROCEDT_ALWINPUTSTAGE);
		// ### 06-05-2018 - Start - story #361(Tuleap server) Manual Deviations

		if (StringUtils.isNotEmpty(aFinanceReferenceDetail.getLovDescNamelov())
				&& aFinanceReferenceDetail.getLovDescNamelov().startsWith("MDAAL")) {
			fillListBox(this.listboxmandInputInStage, delegatorRoles, checkMandInputInStageMap,
					FinanceConstants.PROCEDT_MANDINPUTSTAGE);
		} else {
			fillListBox(this.listboxmandInputInStage, roleCodes, checkMandInputInStageMap,
					FinanceConstants.PROCEDT_MANDINPUTSTAGE);
		}
		// ### 06-05-2018 - End
		fillComboBox(this.alertType, aFinanceReferenceDetail.getAlertType(),
				PennantStaticListUtil.getNotificationTypes(), "");
		doDesignByType(getFinanceReferenceDetail());
		CheckOverride();
		this.recordStatus.setValue(aFinanceReferenceDetail.getRecordStatus());

		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aFinanceReferenceDetail
	 */
	public void doWriteComponentsToBean(FinanceReferenceDetail aFinanceReferenceDetail) {
		logger.debug("Entering");

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		try {
			if (this.finType.getValue() == null || StringUtils.isEmpty(this.finType.getValue())) {
				throw new WrongValueException(this.finType, Labels.getLabel("FIELD_NO_EMPTY",
						new String[] { Labels.getLabel("label_FinanceReferenceDetailDialog_FinType.value") }));
			}
			aFinanceReferenceDetail.setFinType(this.finType.getValue());

		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aFinanceReferenceDetail.setFinRefType(this.finRefType.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aFinanceReferenceDetail.setFinRefId(this.finRefId.getValue());

		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aFinanceReferenceDetail.setIsActive(this.isActive.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aFinanceReferenceDetail.setResendReq(this.reSend.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {

			if (this.row_AlertType.isVisible()) {
				if ("#".equals(getComboboxValue(this.alertType))) {
					if (!this.alertType.isDisabled()) {
						throw new WrongValueException(this.alertType, Labels.getLabel("STATIC_INVALID", new String[] {
								Labels.getLabel("label_FinanceReferenceDetailDialog_AlertType.value") }));
					}

				} else {
					aFinanceReferenceDetail.setAlertType(getComboboxValue(this.alertType));
				}
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if (!this.allowDeviation.isDisabled() && this.allowDeviation.isChecked()) {
				if (!this.allowWaiver.isDisabled() && !this.allowWaiver.isChecked() && !this.allowPostpone.isDisabled()
						&& !this.allowPostpone.isChecked() && !this.allowExpire.isDisabled()
						&& !this.allowExpire.isChecked()) {
					throw new WrongValueException(this.allowDeviation, Labels.getLabel("message_Deviation_Mandatory"));
				}
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if (!this.showInStage.isReadonly()) {
				// Set checked values
				this.showInStage.setValue(getCheckedValues(listboxshowInStage));
				// then check for empty
				if (this.showInStage.getValue() == null || StringUtils.isEmpty(this.showInStage.getValue())) {
					throw new WrongValueException(this.listboxshowInStage,
							Labels.getLabel("FIELD_NO_EMPTY", new String[] { this.showInStage.getLeft() }));
				}
			}
			aFinanceReferenceDetail.setShowInStage(this.showInStage.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if (!this.allowInputInStage.isReadonly()) {
				// Set checked values
				this.allowInputInStage.setValue(getCheckedValues(listboxallowInputInStage));
				// then check for empty
				if (this.allowInputInStage.getValue() == null
						|| StringUtils.isEmpty(this.allowInputInStage.getValue())) {
					throw new WrongValueException(this.listboxallowInputInStage,
							Labels.getLabel("FIELD_NO_EMPTY", new String[] { this.allowInputInStage.getLeft() }));
				}
			}
			aFinanceReferenceDetail.setAllowInputInStage(this.allowInputInStage.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if (!this.mandInputInStage.isReadonly()) {

				hasSingleApprStage(aFinanceReferenceDetail);
				// Set checked values
				this.mandInputInStage.setValue(getCheckedValues(listboxmandInputInStage));
				// then check for empty
				if ((this.mandInputInStage.getValue() == null || StringUtils.isEmpty(this.mandInputInStage.getValue()))
						&& financeReferenceDetail.getFinRefType() != FinanceConstants.PROCEDT_FINANCETABS) {
					throw new WrongValueException(this.listboxmandInputInStage,
							Labels.getLabel("FIELD_NO_EMPTY", new String[] { this.mandInputInStage.getLeft() }));
				}
			}
			aFinanceReferenceDetail.setMandInputInStage(this.mandInputInStage.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if (this.lovDescRefDesc.getValue() == null || StringUtils.isEmpty(this.lovDescRefDesc.getValue())) {
				throw new WrongValueException(this.lovDescRefDesc, Labels.getLabel("FIELD_NO_EMPTY",
						new String[] { Labels.getLabel("label_FinanceReferenceDetailDialog_FinType.value") }));
			}
			aFinanceReferenceDetail.setLovDescRefDesc(this.lovDescRefDesc.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Manual deviations triggering validation.
		if ("MDEVTR".equals(aFinanceReferenceDetail.getLovDescNamelov())) {
			// Load the respective workflow.
			String workflowType = financeWorkFlowService.getFinanceWorkFlowType(aFinanceReferenceDetail.getFinType(),
					aFinanceReferenceDetail.getFinEvent(), PennantConstants.WORFLOW_MODULE_FINANCE);
			WorkFlowDetails workflow = WorkFlowUtil.getDetailsByType(workflowType);
			WorkflowEngine workflowEngine = new WorkflowEngine(workflow.getWorkFlowXml());
			// Validate whether the selected stages are before the deviation approval authorities.
			if (StringUtils.isNotEmpty(mandInputInStage.getValue())) {
				String[] stages = mandInputInStage.getValue().split(PennantConstants.DELIMITER_COMMA);
				List<String> delegators = workflowEngine.getActors(true);
				for (String stage : stages) {
					for (String delegator : delegators) {
						if (workflowEngine.compareRoles(stage, delegator) == Flow.PREDECESSOR) {
							canRaiseManualDeviation = false;
							break;
						} else {
							continue;
						}
					}
				}
			}
			if (!canRaiseManualDeviation) {
				return;
			}
		}
		// Manual Deviation Raise validation
		try {

			aFinanceReferenceDetail.setOverRide(this.overRide.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if (!this.overRideValue.isReadonly() && this.overRide.isChecked()) {
				if (this.overRideValue.getValue() == null || this.overRideValue.getValue() <= 0) {
					throw new WrongValueException(this.overRideValue,
							Labels.getLabel("FIELD_NO_EMPTY", new String[] { this.overRideValue.getLeft() }));
				}
			}
			aFinanceReferenceDetail.setOverRideValue(this.overRideValue.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aFinanceReferenceDetail.setAllowDeviation(this.allowDeviation.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aFinanceReferenceDetail.setAllowWaiver(this.allowWaiver.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aFinanceReferenceDetail.setAllowPostpone(this.allowPostpone.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aFinanceReferenceDetail.setAllowExpire(this.allowExpire.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		doRemoveValidation();

		if (wve.size() > 0) {
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = (WrongValueException) wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}

		aFinanceReferenceDetail.setRecordStatus(this.recordStatus.getValue());
		logger.debug("Leaving");
	}

	private void hasSingleApprStage(FinanceReferenceDetail aFinanceReferenceDetail) {
		// Verification Approval Event validations in Miscellaneous Tab
		String reference = aFinanceReferenceDetail.getLovDescNamelov();

		/**
		 * Allow sampling approval more than 1 role.
		 */
		if (FinanceConstants.PROCEDT_SAMPLING_APPR.equals(reference)) {
			return;
		}

		int length = getCheckedValues(listboxmandInputInStage).split(",").length;
		if (aFinanceReferenceDetail.getFinRefType() == FinanceConstants.PROCEDT_LIMIT && length > 1) {
			String[] message = new String[1];

			switch (reference) {
			case FinanceConstants.PROCEDT_VERIFICATION_FI_APPR:
				message[0] = VerificationType.FI.toString();
				break;
			case FinanceConstants.PROCEDT_VERIFICATION_TV_APPR:
				message[0] = VerificationType.TV.toString();
				break;
			case FinanceConstants.PROCEDT_VERIFICATION_LV_APPR:
				message[0] = VerificationType.LV.toString();
				break;
			case FinanceConstants.PROCEDT_VERIFICATION_RCU_APPR:
				message[0] = VerificationType.RCU.toString();
				break;
			case FinanceConstants.PROCEDT_SAMPLING_APPR:
				message[0] = "sampling";
				break;
			default:
				break;
			}
			if (StringUtils.isNotEmpty(message[0])) {
				throw new WrongValueException(this.listboxmandInputInStage,
						Labels.getLabel("message.error.onlyOneStage", message));
			}
		}
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the readOnly mode accordingly.
	 * 
	 * @param aFinanceReferenceDetail
	 */
	public void doShowDialog(FinanceReferenceDetail aFinanceReferenceDetail) {
		logger.debug("Entering");

		/* fill the components with the data */
		doWriteBeanToComponents(aFinanceReferenceDetail);

		/* set Read only mode accordingly if the object is new or not. */
		if (aFinanceReferenceDetail.isNewRecord()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.isActive.focus();
			this.btnCancel.setVisible(false);
		} else {
			this.isActive.focus();
			if (isWorkFlowEnabled()) {
				this.btnNotes.setVisible(true);
				doEdit();
			} else {
				this.btnCtrl.setInitEdit();
				doReadOnly();
				btnCancel.setVisible(false);
				btnEdit.setVisible(true);
				this.btnDelete.setVisible(true);
			}
		}

		if (StringUtils.equals(moduleName, PennantConstants.WORFLOW_MODULE_COLLATERAL)
				|| StringUtils.equals(moduleName, PennantConstants.WORFLOW_MODULE_VAS)
				|| StringUtils.equals(moduleName, PennantConstants.WORFLOW_MODULE_COMMITMENT)) {
			this.rowDeviation.setVisible(false);
			this.rowPostpone.setVisible(false);
		}

		if (StringUtils.equals(moduleName, PennantConstants.WORFLOW_MODULE_FINANCE)
				&& FinanceConstants.PROCEDT_TEMPLATE == financeReferenceDetail.getFinRefType()) {
			this.row_Resend.setVisible(true);
		}

		try {

			getFinanceReferenceDetailDialogCtrl().window_FinanceReferenceDetailDialog.setVisible(false);
			getFinanceReferenceDetailDialogCtrl().window_FinanceReferenceDetailDialog.getParent()
					.appendChild(window_FinanceReferenceDetailDialogLink);

		} catch (UiException e) {
			logger.error("Exception: ", e);
			this.window_FinanceReferenceDetailDialogLink.onClose();
		} catch (Exception e) {
			throw e;
		}
		logger.debug("Leaving");
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */

	@SuppressWarnings("unused")
	private void doSetValidation() {
		logger.debug("Entering");

		setValidationOn(true);

		if (!this.finType.isReadonly()) {
			this.finType.setConstraint(new PTStringValidator(
					Labels.getLabel("label_FinanceReferenceDetailDialog_FinType.value"), null, true));
		}
		if (!this.finRefType.isReadonly()) {
			this.finRefType.setConstraint(new PTNumberValidator(
					Labels.getLabel("label_FinanceReferenceDetailDialog_FinRefType.value"), true));
		}
		if (!this.finRefId.isReadonly()) {
			this.finRefId.setConstraint(
					new PTNumberValidator(Labels.getLabel("label_FinanceReferenceDetailDialog_FinRefId.value"), true));
		}
		if (!this.showInStage.isReadonly()) {
			this.showInStage.setConstraint(new PTStringValidator(
					Labels.getLabel("label_FinanceReferenceDetailDialog_ShowInStage.value"), null, true));
		}
		if (!this.mandInputInStage.isReadonly()) {
			this.mandInputInStage.setConstraint(new PTStringValidator(
					Labels.getLabel("label_FinanceReferenceDetailDialog_MandInputInStage.value"), null, true));
		}
		if (!this.allowInputInStage.isReadonly()) {
			this.allowInputInStage.setConstraint(new PTStringValidator(
					Labels.getLabel("label_FinanceReferenceDetailDialog_AllowInputInStage.value"), null, true));
		}
		logger.debug("Leaving");
	}

	/**
	 * Disables the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering");
		setValidationOn(false);
		this.finType.setConstraint("");
		this.finRefType.setConstraint("");
		this.finRefId.setConstraint("");
		this.showInStage.setConstraint("");
		this.mandInputInStage.setConstraint("");
		this.allowInputInStage.setConstraint("");
		this.alertType.setConstraint("");
		logger.debug("Leaving");
	}

	private void doDelete() throws InterruptedException {
		logger.debug(Literal.ENTERING);

		final FinanceReferenceDetail aFinanceReferenceDetail = new FinanceReferenceDetail();
		BeanUtils.copyProperties(getFinanceReferenceDetail(), aFinanceReferenceDetail);

		final String keyReference = this.label_FinanceReferenceDetailDialog_FinRefId.getValue() + " : "
				+ aFinanceReferenceDetail.getLovDescRefDesc();

		doDelete(keyReference, aFinanceReferenceDetail);

		logger.debug(Literal.LEAVING);
	}

	protected void onDoDelete(final FinanceReferenceDetail aFinanceReferenceDetail) {
		aFinanceReferenceDetail.setRecordType(PennantConstants.RCD_DEL);
		try {
			deleteFinRrefDetails(aFinanceReferenceDetail);
			this.window_FinanceReferenceDetailDialogLink.onClose();
			getFinanceReferenceDetailDialogCtrl().window_FinanceReferenceDetailDialog.setVisible(true);
		} catch (DataAccessException e) {
			MessageUtil.showError(e);
		}
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug("Entering");

		if (getFinanceReferenceDetail().isNewRecord()) {
			this.btnCancel.setVisible(false);
			this.isActive.setChecked(true);
			this.isActive.setDisabled(true);
		} else {
			this.btnCancel.setVisible(true);
			this.btnSearchAggCode.setDisabled(true);
			this.btnSearchElgRule.setDisabled(true);
			this.btnSearchAccounting.setDisabled(true);
			this.btnSearchQuestionId.setDisabled(true);
			this.btnSearchScoringGroup.setDisabled(true);
			this.btnSearchCorpScoringGroup.setDisabled(true);
			this.alertType.setDisabled(false);
			this.isActive.setDisabled(isReadOnly("FinanceReferenceDetailDialog_isActive"));
		}
		if (getFinanceReferenceDetail().getFinRefType() == FinanceConstants.PROCEDT_RTLSCORE
				|| getFinanceReferenceDetail().getFinRefType() == FinanceConstants.PROCEDT_CORPSCORE
				|| getFinanceReferenceDetail().getFinRefType() == FinanceConstants.PROCEDT_STAGEACC
				|| getFinanceReferenceDetail().getFinRefType() == FinanceConstants.PROCEDT_TEMPLATE
				|| getFinanceReferenceDetail().getFinRefType() == FinanceConstants.PROCEDT_FINDEDUP
				|| getFinanceReferenceDetail().getFinRefType() == FinanceConstants.PROCEDT_CUSTDEDUP
				|| getFinanceReferenceDetail().getFinRefType() == FinanceConstants.PROCEDT_BLACKLIST
				|| getFinanceReferenceDetail().getFinRefType() == FinanceConstants.PROCEDT_RETURNCHQ
				|| getFinanceReferenceDetail().getFinRefType() == FinanceConstants.PROCEDT_AGREEMENT
				|| getFinanceReferenceDetail().getFinRefType() == FinanceConstants.PROCEDT_LIMIT
				|| getFinanceReferenceDetail().getFinRefType() == FinanceConstants.PROCEDT_TATNOTIFICATION
				|| getFinanceReferenceDetail().getFinRefType() == FinanceConstants.PROCEDT_FINANCETABS) {
			doToggleInReadOnlyMode(this.listboxmandInputInStage, false);
		} else {
			doToggleInReadOnlyMode(this.listboxshowInStage, isReadOnly("FinanceReferenceDetailDialog_isActive"));
			doEnableByChecked(this.listboxallowInputInStage);
			doEnableByOtherChecked(this.listboxshowInStage, this.listboxallowInputInStage);
			if (getFinanceReferenceDetail().getFinRefType() == FinanceConstants.PROCEDT_CHECKLIST) {
				doEnableByChecked(this.listboxmandInputInStage);
				doEnableByOtherChecked(this.listboxallowInputInStage, this.listboxmandInputInStage);
			}
		}
		this.finType.setReadonly(true);
		this.finRefType.setReadonly(isReadOnly("FinanceReferenceDetailDialog_finRefType"));
		this.finRefId.setReadonly(isReadOnly("FinanceReferenceDetailDialog_finRefId"));
		this.overRide.setDisabled(false);
		this.overRideValue.setReadonly(false);
		this.allowDeviation.setDisabled(false);
		CheckOverride();
		checkAllowDeviation();
		/*
		 * this.showInStage.setReadonly(isReadOnly( "FinanceReferenceDetailDialog_showInStage"));
		 * this.mandInputInStage.setReadonly (isReadOnly("FinanceReferenceDetailDialog_mandInputInStage"));
		 * this.allowInputInStage .setReadonly(isReadOnly("FinanceReferenceDetailDialog_allowInputInStage" ));
		 */

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}

			if (this.financeReferenceDetail.isNewRecord()) {
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

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly() {
		logger.debug("Entering");

		this.finType.setReadonly(true);
		this.finRefType.setReadonly(true);
		this.finRefId.setReadonly(true);
		this.isActive.setDisabled(true);
		this.btnSearchAggCode.setDisabled(true);
		this.btnSearchElgRule.setDisabled(true);
		this.btnSearchAccounting.setDisabled(true);
		this.btnSearchQuestionId.setDisabled(true);
		this.btnSearchScoringGroup.setDisabled(true);
		this.btnSearchCorpScoringGroup.setDisabled(true);
		this.btnSearchLimitService.setDisabled(true);
		this.btnSearchTatNotification.setDisabled(true);
		this.btnSearchReturnCheque.setDisabled(true);
		this.overRide.setDisabled(true);
		this.overRideValue.setReadonly(true);
		this.allowDeviation.setDisabled(true);
		this.allowWaiver.setDisabled(true);
		this.allowPostpone.setDisabled(true);
		this.allowExpire.setDisabled(true);
		this.alertType.setDisabled(true);
		// this.row_AlertType.setVisible(false);
		doToggleInReadOnlyMode(this.listboxshowInStage, true);
		doToggleInReadOnlyMode(this.listboxmandInputInStage, true);
		doToggleInReadOnlyMode(this.listboxallowInputInStage, true);
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
		this.finType.setValue("");
		this.finRefType.setText("");
		this.finRefId.setText("");
		this.isActive.setChecked(false);
		this.showInStage.setValue("");
		this.mandInputInStage.setValue("");
		this.allowInputInStage.setValue("");
		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */

	public void doSave() throws InterruptedException {
		logger.debug("Entering");
		final FinanceReferenceDetail aFinanceReferenceDetail = new FinanceReferenceDetail();
		BeanUtils.copyProperties(getFinanceReferenceDetail(), aFinanceReferenceDetail);
		doWriteComponentsToBean(aFinanceReferenceDetail);
		if (!canRaiseManualDeviation) {
			MessageUtil.showError("Select Privileged Users alone to raise Manual Deviations");
			canRaiseManualDeviation = true;
			return;
		}
		// save it to database
		try {
			processFinRefDetails(aFinanceReferenceDetail);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving");
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

	public FinanceReferenceDetail getFinanceReferenceDetail() {
		return this.financeReferenceDetail;
	}

	public void setFinanceReferenceDetail(FinanceReferenceDetail financeReferenceDetail) {
		this.financeReferenceDetail = financeReferenceDetail;
	}

	public void setFinanceReferenceDetailService(FinanceReferenceDetailService financeReferenceDetailService) {
		this.financeReferenceDetailService = financeReferenceDetailService;
	}

	public FinanceReferenceDetailService getFinanceReferenceDetailService() {
		return this.financeReferenceDetailService;
	}

	public void setFinanceWorkFlowService(FinanceWorkFlowService financeWorkFlowService) {
		this.financeWorkFlowService = financeWorkFlowService;
	}

	public FinanceReferenceDetailDialogCtrl getFinanceReferenceDetailDialogCtrl() {
		return financeReferenceDetailDialogCtrl;
	}

	public void setFinanceReferenceDetailDialogCtrl(FinanceReferenceDetailDialogCtrl financeReferenceDetailDialogCtrl) {
		this.financeReferenceDetailDialogCtrl = financeReferenceDetailDialogCtrl;
	}

	public PagedListService getPagedListService() {
		return pagedListService;
	}

	public void setPagedListService(PagedListService pagedListService) {
		this.pagedListService = pagedListService;
	}

	public void onClick$btnNotes(Event event) {
		doShowNotes(this.financeReferenceDetail);
	}

	@Override
	protected String getReference() {
		return String.valueOf(this.financeReferenceDetail.getFinRefDetailId());
	}

	@Override
	protected void doClearMessage() {
		logger.debug("Entering");
		this.finType.setErrorMessage("");
		this.finRefType.setErrorMessage("");
		this.finRefId.setErrorMessage("");
		this.showInStage.setErrorMessage("");
		this.mandInputInStage.setErrorMessage("");
		this.allowInputInStage.setErrorMessage("");
		logger.debug("Leaving");
	}

	public void setOverideMap(Map<String, List<ErrorDetail>> overideMap) {
		this.overideMap = overideMap;
	}

	public Map<String, List<ErrorDetail>> getOverideMap() {
		return overideMap;
	}

	// =======================================//
	public void onClick$btnSearchQuestionId(Event event) {
		logger.debug("Entering" + event.toString());

		Filter[] filters = new Filter[2];
		filters[0] = new Filter("ModuleName", moduleName, Filter.OP_EQUAL);
		filters[1] = new Filter("Active", 1, Filter.OP_EQUAL);

		Object dataObject = ExtendedSearchListBox.show(this.window_FinanceReferenceDetailDialogLink, "CheckList",
				filters);
		if (dataObject instanceof String) {
			this.lovDescRefDesc.setValue("");
		} else {
			CheckList details = (CheckList) dataObject;
			if (details != null) {
				this.finRefId.setValue(details.getCheckListId());
				this.lovDescRefDesc.setValue(details.getCheckListDesc());
			}
		}
		logger.debug("Leaving" + event.toString());
	}

	public void onClick$btnSearchAggCode(Event event) {
		logger.debug("Entering" + event.toString());

		Filter[] filters = new Filter[2];
		filters[0] = new Filter("ModuleName", moduleName, Filter.OP_EQUAL);
		filters[1] = new Filter("AGGISACTIVE", 1, Filter.OP_EQUAL);

		Object dataObject = ExtendedSearchListBox.show(this.window_FinanceReferenceDetailDialogLink,
				"AgreementDefinition", filters);
		if (dataObject instanceof String) {
			this.lovDescRefDesc.setValue("");
		} else {
			AgreementDefinition details = (AgreementDefinition) dataObject;
			if (details != null) {
				this.finRefId.setValue(details.getAggId());
				this.lovDescRefDesc.setValue(details.getAggCode() + "-" + details.getAggName());
				getFinanceReferenceDetail().setLovDescCodelov(details.getAggCode());
				getFinanceReferenceDetail().setLovDescNamelov(details.getAggName());
			}
		}
		logger.debug("Leaving" + event.toString());
	}

	public void onClick$btnSearchScoringGroup(Event event) {
		logger.debug("Entering" + event.toString());

		Filter[] filters = new Filter[1];
		filters[0] = new Filter("CategoryType", PennantConstants.PFF_CUSTCTG_INDIV, Filter.OP_EQUAL);

		Object dataObject = ExtendedSearchListBox.show(this.window_FinanceReferenceDetailDialogLink, "ScoringGroup",
				filters);
		if (dataObject instanceof String) {
			this.lovDescRefDesc.setValue("");
		} else {
			ScoringGroup details = (ScoringGroup) dataObject;
			if (details != null) {
				this.finRefId.setValue(details.getScoreGroupId());
				this.lovDescRefDesc.setValue(details.getScoreGroupCode() + "-" + details.getScoreGroupName());
				getFinanceReferenceDetail().setLovDescCodelov(details.getScoreGroupCode());
				getFinanceReferenceDetail().setLovDescNamelov(details.getScoreGroupName());
			}
		}
		logger.debug("Leaving" + event.toString());
	}

	public void onClick$btnSearchCorpScoringGroup(Event event) {
		logger.debug("Entering" + event.toString());

		Filter[] filters = new Filter[1];
		filters[0] = new Filter("CategoryType", PennantConstants.PFF_CUSTCTG_CORP, Filter.OP_EQUAL);

		Object dataObject = ExtendedSearchListBox.show(this.window_FinanceReferenceDetailDialogLink, "ScoringGroup",
				filters);
		if (dataObject instanceof String) {
			this.lovDescRefDesc.setValue("");
		} else {
			ScoringGroup details = (ScoringGroup) dataObject;
			if (details != null) {
				this.finRefId.setValue(details.getScoreGroupId());
				this.lovDescRefDesc.setValue(details.getScoreGroupCode() + "-" + details.getScoreGroupName());
				getFinanceReferenceDetail().setLovDescCodelov(details.getScoreGroupCode());
				getFinanceReferenceDetail().setLovDescNamelov(details.getScoreGroupName());
			}
		}
		logger.debug("Leaving" + event.toString());
	}

	public void onClick$btnSearchAccounting(Event event) {
		logger.debug("Entering" + event.toString());

		////////////////// Stage Accounting with Stage Accounting Rules change///////////
		/*
		 * Filter[] filter = new Filter[1]; filter[0] = new Filter("EventCode", AccountEventConstants.ACCEVENT_STAGE,
		 * Filter.OP_EQUAL);
		 * 
		 * Object dataObject = ExtendedSearchListBox.show(this.window_FinanceReferenceDetailDialogLink, "AccountingSet",
		 * filter); if (dataObject instanceof String) { this.lovDescRefDesc.setValue(""); } else { AccountingSet details
		 * = (AccountingSet) dataObject; if (details != null) { this.finRefId.setValue(details.getAccountSetid());
		 * this.lovDescRefDesc.setValue(details.getEventCode() + "-" + details.getAccountSetCodeName());
		 * getFinanceReferenceDetail().setLovDescNamelov(details.getEventCode());
		 * getFinanceReferenceDetail().setLovDescRefDesc(details.getAccountSetCodeName()); } }
		 */

		Filter[] filter = new Filter[1];
		filter[0] = new Filter("RuleModule", RuleConstants.MODULE_STGACRULE, Filter.OP_EQUAL);

		Object dataObject = ExtendedSearchListBox.show(this.window_FinanceReferenceDetailDialogLink, "Rule", filter);
		if (dataObject instanceof String) {
			this.lovDescRefDesc.setValue("");
		} else {
			Rule details = (Rule) dataObject;
			if (details != null) {
				this.finRefId.setValue(details.getRuleId());
				this.lovDescRefDesc.setValue(details.getRuleCode() + "-" + details.getRuleCodeDesc());
				getFinanceReferenceDetail().setLovDescCodelov(details.getRuleCode());
				getFinanceReferenceDetail().setLovDescNamelov(details.getRuleCode());

			}
		}

		logger.debug("Leaving" + event.toString());
	}

	public void onClick$btnSearchTemplate(Event event) {
		logger.debug("Entering" + event.toString());

		Filter[] filters = new Filter[2];
		filters[0] = new Filter("RuleModule", NotificationConstants.MAIL_MODULE_FIN, Filter.OP_EQUAL);
		filters[1] = new Filter("RuleEvent", eventAction, Filter.OP_EQUAL);

		Object dataObject = ExtendedSearchListBox.show(this.window_FinanceReferenceDetailDialogLink, "Notifications",
				filters);
		if (dataObject instanceof String) {
			this.lovDescRefDesc.setValue("");
		} else {
			Notifications details = (Notifications) dataObject;
			if (details != null) {
				this.finRefId.setValue(details.getRuleId());
				this.lovDescRefDesc.setValue(details.getRuleCode() + "-" + details.getRuleCodeDesc());
				getFinanceReferenceDetail().setLovDescNamelov(details.getRuleCodeDesc());
				getFinanceReferenceDetail().setLovDescRefDesc(details.getRuleCodeDesc());
			}
		}
		logger.debug("Leaving" + event.toString());
	}

	public void onClick$btnSearchFinanceDedupe(Event event) {
		logger.debug("Entering" + event.toString());

		Filter[] filters = new Filter[1];
		filters[0] = new Filter("QueryModule", FinanceConstants.DEDUP_FINANCE, Filter.OP_EQUAL);

		Object dataObject = ExtendedSearchListBox.show(this.window_FinanceReferenceDetailDialogLink, "DedupParm",
				filters);
		if (dataObject instanceof String) {
			this.lovDescRefDesc.setValue("");
		} else {
			DedupParm details = (DedupParm) dataObject;
			if (details != null) {
				this.finRefId.setValue(details.getQueryId());
				this.lovDescRefDesc.setValue(details.getQueryCode() + "-" + details.getQueryDesc());
				getFinanceReferenceDetail().setLovDescNamelov(details.getQueryCode());
				getFinanceReferenceDetail().setLovDescRefDesc(details.getQueryDesc());
			}
		}
		logger.debug("Leaving" + event.toString());
	}

	public void onClick$btnSearchCustomerDedupe(Event event) {
		logger.debug("Entering" + event.toString());

		Filter[] filters = new Filter[1];
		filters[0] = new Filter("QueryModule", FinanceConstants.DEDUP_CUSTOMER, Filter.OP_EQUAL);

		Object dataObject = ExtendedSearchListBox.show(this.window_FinanceReferenceDetailDialogLink, "DedupParm",
				filters);
		if (dataObject instanceof String) {
			this.lovDescRefDesc.setValue("");
		} else {
			DedupParm details = (DedupParm) dataObject;
			if (details != null) {
				this.finRefId.setValue(details.getQueryId());
				this.lovDescRefDesc.setValue(details.getQueryCode() + "-" + details.getQueryDesc());
				getFinanceReferenceDetail().setLovDescNamelov(details.getQueryCode());
				getFinanceReferenceDetail().setLovDescRefDesc(details.getQueryDesc());
			}
		}
		logger.debug("Leaving" + event.toString());
	}

	public void onClick$btnSearchBlackListDedupe(Event event) {
		logger.debug("Entering" + event.toString());

		Filter[] filters = new Filter[1];
		filters[0] = new Filter("QueryModule", FinanceConstants.DEDUP_BLACKLIST, Filter.OP_EQUAL);

		Object dataObject = ExtendedSearchListBox.show(this.window_FinanceReferenceDetailDialogLink, "DedupParm",
				filters);
		if (dataObject instanceof String) {
			this.lovDescRefDesc.setValue("");
		} else {
			DedupParm details = (DedupParm) dataObject;
			if (details != null) {
				this.finRefId.setValue(details.getQueryId());
				this.lovDescRefDesc.setValue(details.getQueryCode() + "-" + details.getQueryDesc());
				getFinanceReferenceDetail().setLovDescNamelov(details.getQueryCode());
				getFinanceReferenceDetail().setLovDescRefDesc(details.getQueryDesc());
			}
		}
		logger.debug("Leaving" + event.toString());
	}

	public void onClick$btnSearchReturnCheque(Event event) {
		logger.debug("Entering" + event.toString());

		Filter[] filters = new Filter[1];
		filters[0] = new Filter("ModuleName", FinanceConstants.DEDUP_RETCHQ, Filter.OP_EQUAL);

		Object dataObject = ExtendedSearchListBox.show(this.window_FinanceReferenceDetailDialogLink, "ProcessEditor",
				filters);
		if (dataObject instanceof String) {
			this.lovDescRefDesc.setValue("");
		} else {
			ProcessEditorDetail details = (ProcessEditorDetail) dataObject;
			if (details != null) {
				this.finRefId.setValue(details.getModuleId());
				this.lovDescRefDesc.setValue(details.getModuleName() + "-" + details.getModuleDesc());
				getFinanceReferenceDetail().setLovDescNamelov(details.getModuleName());
				getFinanceReferenceDetail().setLovDescRefDesc(details.getModuleDesc());
			}
		}
		logger.debug("Leaving" + event.toString());
	}

	public void onClick$btnSearchElgRule(Event event) {
		logger.debug("Entering" + event.toString());

		Filter[] filter = new Filter[1];
		filter[0] = new Filter("RuleModule", RuleConstants.MODULE_ELGRULE, Filter.OP_EQUAL);

		Object dataObject = ExtendedSearchListBox.show(this.window_FinanceReferenceDetailDialogLink, "Rule", filter);
		if (dataObject instanceof String) {
			this.lovDescRefDesc.setValue("");
		} else {
			Rule details = (Rule) dataObject;
			if (details != null) {
				this.finRefId.setValue(details.getRuleId());
				this.lovDescRefDesc.setValue(details.getRuleCode() + "-" + details.getRuleCodeDesc());
				getFinanceReferenceDetail().setLovDescCodelov(details.getRuleCode());
				getFinanceReferenceDetail().setLovDescNamelov(details.getRuleCode());

			}
		}
		logger.debug("Leaving" + event.toString());
	}

	public void onClick$btnSearchLimitService(Event event) {
		logger.debug("Entering" + event.toString());

		Object dataObject = null;
		if (StringUtils.equals(eventAction, FinServiceEvent.ADDDISB)) {
			Filter[] filter = new Filter[1];
			filter[0] = new Filter("LimitCode", FinanceConstants.QUICK_DISBURSEMENT, Filter.OP_NOT_EQUAL);

			dataObject = ExtendedSearchListBox.show(this.window_FinanceReferenceDetailDialogLink, "LimitCodeDetail",
					filter);
		} else {
			dataObject = ExtendedSearchListBox.show(this.window_FinanceReferenceDetailDialogLink, "LimitCodeDetail");
		}
		if (dataObject instanceof String) {
			this.lovDescRefDesc.setValue("");
		} else {
			LimitCodeDetail details = (LimitCodeDetail) dataObject;
			if (details != null) {
				this.finRefId.setValue(details.getId());
				this.lovDescRefDesc.setValue(details.getLimitCode() + "-" + details.getLimitDesc());
				getFinanceReferenceDetail().setLovDescNamelov(details.getLimitCode());
				getFinanceReferenceDetail().setLovDescRefDesc(details.getLimitDesc());
				// ### 06-05-2018 - Start - story #361(Tuleap server) Manual Deviations
				if (details.getLimitCode().startsWith("MDAAL")) {
					fillListBox(this.listboxmandInputInStage, delegatorRoles, checkMandInputInStageMap,
							FinanceConstants.PROCEDT_MANDINPUTSTAGE);
				} else {
					fillListBox(this.listboxmandInputInStage, roleCodes, checkMandInputInStageMap,
							FinanceConstants.PROCEDT_MANDINPUTSTAGE);
				}
				// ### 06-05-2018 - End
			}
		}

		logger.debug("Leaving" + event.toString());
	}
	// ### 06-05-2018 - Start - story #361(Tuleap server) Manual Deviations

	private String getDelegatorRoles() {
		List<ValueLabel> delegators = deviationHelper.getRoleAndDesc(getFinanceReferenceDetail().getFinType(),
				getFinanceReferenceDetail().getFinEvent(), moduleName);

		String roles = "";
		for (ValueLabel valueLabel : delegators) {
			if (StringUtils.isNotEmpty(roles)) {
				roles = roles.concat(";");
			}

			roles = roles.concat(valueLabel.getValue());
		}
		return roles;
	}
	// ### 06-05-2018 - End

	public void onClick$btnSearchTatNotification(Event event) {
		logger.debug("Entering" + event.toString());

		Object dataObject = ExtendedSearchListBox.show(this.window_FinanceReferenceDetailDialogLink,
				"TATNotificationCode");
		if (dataObject instanceof String) {
			this.lovDescRefDesc.setValue("");
		} else {
			TATNotificationCode details = (TATNotificationCode) dataObject;
			if (details != null) {
				this.finRefId.setValue(details.getId());
				this.lovDescRefDesc.setValue(details.getTatNotificationDesc());
				getFinanceReferenceDetail().setLovDescNamelov(details.getTatNotificationCode());
				getFinanceReferenceDetail().setLovDescRefDesc(details.getTatNotificationDesc());
			}
		}
		logger.debug("Leaving" + event.toString());
	}

	public void onClick$btnFinanceTabs(Event event) {
		logger.debug("Entering" + event.toString());

		Object dataObject = ExtendedSearchListBox.show(this.window_FinanceReferenceDetailDialogLink, "StageTabDetail");
		if (dataObject instanceof String) {
			this.lovDescRefDesc.setValue("");
		} else {
			StageTabDetail details = (StageTabDetail) dataObject;
			if (details != null) {
				this.finRefId.setValue(Long.valueOf(details.getTabId()));
				this.lovDescRefDesc.setValue(details.getTabDescription());
				getFinanceReferenceDetail().setLovDescCodelov(String.valueOf(details.getTabId()));
				getFinanceReferenceDetail().setLovDescNamelov(details.getTabDescription());
			}
		}
		logger.debug("Leaving" + event.toString());
	}

	public void fillListBox(Listbox listbox, String roleCodes, Map<String, String> checkedlist, int type) {
		logger.debug("Entering");

		listbox.getItems().clear();
		String[] roles = roleCodes.split(";");

		for (int i = 0; i < roles.length; i++) {

			if (StringUtils.isBlank(roles[i])) {
				continue;
			}
			Listitem item = new Listitem();
			Listcell lc;
			Checkbox checkbox = new Checkbox();
			checkbox.setTabindex(type);
			checkbox.setValue(roles[i]);
			checkbox.setLabel(roles[i]);
			checkbox.setChecked(checkedlist.containsKey(roles[i]));
			checkbox.addEventListener("onCheck", new onCheckBoxCheked());
			lc = new Listcell();
			lc.appendChild(checkbox);
			lc.setParent(item);
			listbox.appendChild(item);
		}

		if ("FINANCE".equals(moduleName) && (getFinanceReferenceDetailDialogCtrl().selectedTab == 6
				|| getFinanceReferenceDetail().getFinRefType() == 6)) {
			Listitem it = new Listitem();
			Listcell lcell;
			Checkbox cb = new Checkbox();
			cb.setTabindex(type);
			cb.setValue(Labels.getLabel("label_FinanceReferenceDetailDialog_REC_ON_APPROVAL.value"));
			cb.setLabel(Labels.getLabel("label_FinanceReferenceDetailDialog_REC_ON_APPROVAL.value"));
			cb.setChecked(isChecked_ROA);
			cb.addEventListener("onCheck", new onCheckBoxCheked());
			lcell = new Listcell();
			lcell.appendChild(cb);
			lcell.setParent(it);
			listbox.appendChild(it);
		}

		logger.debug("Leaving");

	}

	public final class onCheckBoxCheked implements EventListener<Event> {

		public onCheckBoxCheked() {
		    super();
		}

		public void onEvent(Event event) {
			logger.debug("Entering" + event.toString());

			Checkbox checkbox = (Checkbox) event.getTarget();
			switch (checkbox.getTabindex()) {
			case FinanceConstants.PROCEDT_SHOWINSTAGE:
				if (checkbox.isChecked()) {
					doToggleDisableByChkVal(listboxallowInputInStage, checkbox.getValue().toString(), false);
				} else {
					doToggleDisableByChkVal(listboxallowInputInStage, checkbox.getValue().toString(), true);
					doToggleDisableByChkVal(listboxmandInputInStage, checkbox.getValue().toString(), true);
				}
				break;
			case FinanceConstants.PROCEDT_ALWINPUTSTAGE:
				if (checkbox.isChecked()) {
					doToggleDisableByChkVal(listboxmandInputInStage, checkbox.getValue().toString(), false);
				} else {
					doToggleDisableByChkVal(listboxmandInputInStage, checkbox.getValue().toString(), true);
				}
				break;
			default:
				break;
			}

			logger.debug("Leaving" + event.toString());
		}

	}

	// ============Design the zul file===========//
	private void doDesignByType(FinanceReferenceDetail finRefDetail) {
		logger.debug("Entering");

		switch (finRefDetail.getFinRefType()) {

		case FinanceConstants.PROCEDT_CHECKLIST:

			// For validations
			this.showInStage.setReadonly(false);
			this.allowInputInStage.setReadonly(false);
			this.mandInputInStage.setReadonly(false);
			this.overRide.setDisabled(true);
			this.overRideValue.setReadonly(true);
			this.rowOverRide.setVisible(false);
			this.rowDeviation.setVisible(true);
			this.rowPostpone.setVisible(true);
			this.label_FinanceReferenceDetailDialog_AllowWaiver.setVisible(true);
			this.hboxWaiver.setVisible(true);

			this.overRideValue.setLeft(Labels.getLabel("label_FinanceReferenceDetailDialog_OverRideValue.value"));

			// error labels
			this.showInStage.setLeft(Labels.getLabel("label_FinRefDialogLink_ShowInStage.value"));
			this.allowInputInStage.setLeft(Labels.getLabel("label_FinReferDialogLink_AllowInputInStage.value"));
			this.mandInputInStage.setLeft(Labels.getLabel("label_FinRefDialogLink_MandInputInStage.value"));

			// LOV List
			this.btnSearchQuestionId.setVisible(true);// show

			// LOV Label
			this.label_FinanceReferenceDetailDialog_FinRefId
					.setValue(Labels.getLabel("label_FinRefDialogLink_Question.value"));

			// ROWS WITH LIST Boxes
			this.rowDoubleListbox.setVisible(true);// Show
			this.rowSingleListbox.setVisible(true);// Show

			// labels of list boxes
			this.label_FinanceReferenceDetailDialog_ShowInStage
					.setValue(Labels.getLabel("label_FinRefDialogLink_ShowInStage.value"));
			this.label_FinanceReferenceDetailDialog_AllowInputInStage
					.setValue(Labels.getLabel("label_FinReferDialogLink_AllowInputInStage.value"));
			this.label_FinanceReferenceDetailDialog_MandInputInStage
					.setValue(Labels.getLabel("label_FinRefDialogLink_MandInputInStage.value"));

			// List headers of list boxes
			this.listheadShowInStage.setLabel(Labels.getLabel("label_FinRefDialogLink_ShowInStage.value"));
			this.listheadAllowInputInStage
					.setLabel(Labels.getLabel("label_FinReferDialogLink_AllowInputInStage.value"));
			this.listheadMandInputInStage.setLabel(Labels.getLabel("label_FinRefDialogLink_MandInputInStage.value"));

			doEnableByChecked(this.listboxallowInputInStage);
			doEnableByChecked(this.listboxmandInputInStage);
			this.label_FinanceReferenceDetailDialogLink
					.setValue(Labels.getLabel("label_Window_FinanceCheckListList.title"));
			break;

		case FinanceConstants.PROCEDT_AGREEMENT:

			// For validations
			this.showInStage.setReadonly(true);// not required
			this.allowInputInStage.setReadonly(true);// not required
			this.mandInputInStage.setReadonly(false);

			// error labels
			this.showInStage.setLeft("");
			this.allowInputInStage.setLeft("");
			this.mandInputInStage.setLeft(Labels.getLabel("label_FinRefDialogLink_MandInputInStage.value"));

			// LOV List
			this.btnSearchAggCode.setVisible(true);// show

			// LOV Label
			this.label_FinanceReferenceDetailDialog_FinRefId
					.setValue(Labels.getLabel("label_FinRefDialogLink_Agreement.value"));

			// ROWS WITH LIST Boxes
			this.rowSingleListbox.setVisible(true);// Show

			// labels of list boxes
			this.label_FinanceReferenceDetailDialog_ShowInStage.setValue("");// not required
			this.label_FinanceReferenceDetailDialog_AllowInputInStage.setValue("");// not required
			this.label_FinanceReferenceDetailDialog_MandInputInStage
					.setValue(Labels.getLabel("label_FinRefDialogLink_ShowInStage.value"));

			// List headers of list boxes
			this.listheadShowInStage.setLabel("");// not required
			this.listheadAllowInputInStage.setLabel("");// not required
			this.listheadMandInputInStage.setLabel(Labels.getLabel("label_FinRefDialogLink_ShowInStage.value"));

			doEnableByChecked(this.listboxmandInputInStage);
			this.label_FinanceReferenceDetailDialogLink
					.setValue(Labels.getLabel("label_Window_FinanceAgreementList.title"));
			break;

		case FinanceConstants.PROCEDT_ELIGIBILITY:

			// error labels
			this.showInStage.setLeft(Labels.getLabel("label_FinRefDialogLink_MandInputInStage.value"));
			this.allowInputInStage.setLeft(Labels.getLabel("label_FinRefDialogLink_ShowInStage.value"));
			this.mandInputInStage.setLeft("");
			// For validations
			this.showInStage.setReadonly(false);
			this.allowInputInStage.setReadonly(false);
			this.mandInputInStage.setReadonly(true);

			this.overRide.setDisabled(false);
			this.overRideValue.setReadonly(false);
			this.rowOverRide.setVisible(false);
			this.rowDeviation.setVisible(true);
			this.overRideValue.setLeft(Labels.getLabel("label_FinanceReferenceDetailDialog_OverRideValue.value"));

			// error labels
			// error labels
			this.showInStage.setLeft(Labels.getLabel("label_FinRefDialogLink_MandInputInStage.value"));
			this.allowInputInStage.setLeft(Labels.getLabel("label_FinRefDialogLink_ShowInStage.value"));
			this.mandInputInStage.setLeft("");

			// LOV List
			this.btnSearchElgRule.setVisible(true);// show

			// LOV Label
			this.label_FinanceReferenceDetailDialog_FinRefId
					.setValue(Labels.getLabel("label_FinReferDialogLink_Eligibility.value"));

			// ROWS WITH LIST Boxes
			this.rowDoubleListbox.setVisible(true);// Show

			// labels of list boxes
			this.label_FinanceReferenceDetailDialog_ShowInStage
					.setValue(Labels.getLabel("label_FinRefDialogLink_ShowInStage.value"));
			this.label_FinanceReferenceDetailDialog_AllowInputInStage
					.setValue(Labels.getLabel("label_FinRefDialogLink_ReGenerateInStage.value"));
			this.label_FinanceReferenceDetailDialog_MandInputInStage.setValue("");// not required

			// List headers of list boxes
			this.listheadShowInStage.setLabel(Labels.getLabel("label_FinRefDialogLink_ShowInStage.value"));
			this.listheadAllowInputInStage.setLabel(Labels.getLabel("label_FinRefDialogLink_ReGenerateInStage.value"));
			this.listheadMandInputInStage.setLabel("");// not required

			doEnableByChecked(this.listboxallowInputInStage);
			this.label_FinanceReferenceDetailDialogLink
					.setValue(Labels.getLabel("label_Window_FinanceEligibilityList.title"));
			break;

		case FinanceConstants.PROCEDT_RTLSCORE:

			// For validations
			this.showInStage.setReadonly(true);// not required
			this.allowInputInStage.setReadonly(true);// not required
			this.mandInputInStage.setReadonly(false);

			// Deviation
			this.rowDeviation.setVisible(true);

			// error labels
			this.showInStage.setLeft("");
			this.allowInputInStage.setLeft("");
			this.mandInputInStage.setLeft(Labels.getLabel("label_FinRefDialogLink_ExecuteInStage.value"));

			// LOV List
			this.btnSearchScoringGroup.setVisible(true);// show

			// LOV Label
			this.label_FinanceReferenceDetailDialog_FinRefId
					.setValue(Labels.getLabel("label_FinReferDialogLink_ScoringGroup.value"));

			// ROWS WITH LIST Boxes
			this.rowSingleListbox.setVisible(true);// Show

			// labels of list boxes
			this.label_FinanceReferenceDetailDialog_ShowInStage.setValue("");// not required
			this.label_FinanceReferenceDetailDialog_AllowInputInStage.setValue("");// not required
			this.label_FinanceReferenceDetailDialog_MandInputInStage
					.setValue(Labels.getLabel("label_FinRefDialogLink_ExecuteInStage.value"));

			// List headers of list boxes
			this.listheadShowInStage.setLabel("");// not required
			this.listheadAllowInputInStage.setLabel("");// not required
			this.listheadMandInputInStage.setLabel(Labels.getLabel("label_FinRefDialogLink_ExecuteInStage.value"));
			doEnableByChecked(this.listboxmandInputInStage);
			this.label_FinanceReferenceDetailDialogLink
					.setValue(Labels.getLabel("label_Window_FinanceScoringList.title"));
			break;

		case FinanceConstants.PROCEDT_CORPSCORE:

			// For validations
			this.showInStage.setReadonly(true);// not required
			this.allowInputInStage.setReadonly(true);// not required
			this.mandInputInStage.setReadonly(false);

			// error labels
			this.showInStage.setLeft("");
			this.allowInputInStage.setLeft("");
			this.mandInputInStage.setLeft(Labels.getLabel("label_FinRefDialogLink_ExecuteInStage.value"));

			// LOV List
			this.btnSearchCorpScoringGroup.setVisible(true);// show

			// LOV Label
			this.label_FinanceReferenceDetailDialog_FinRefId
					.setValue(Labels.getLabel("label_FinReferDialogLink_ScoringGroup.value"));

			// ROWS WITH LIST Boxes
			this.rowSingleListbox.setVisible(true);// Show

			// labels of list boxes
			this.label_FinanceReferenceDetailDialog_ShowInStage.setValue("");// not required
			this.label_FinanceReferenceDetailDialog_AllowInputInStage.setValue("");// not required
			this.label_FinanceReferenceDetailDialog_MandInputInStage
					.setValue(Labels.getLabel("label_FinRefDialogLink_ExecuteInStage.value"));

			// List headers of list boxes
			this.listheadShowInStage.setLabel("");// not required
			this.listheadAllowInputInStage.setLabel("");// not required
			this.listheadMandInputInStage.setLabel(Labels.getLabel("label_FinRefDialogLink_ExecuteInStage.value"));
			doEnableByChecked(this.listboxmandInputInStage);
			this.label_FinanceReferenceDetailDialogLink
					.setValue(Labels.getLabel("label_Window_FinanceCorpScoringList.title"));
			break;

		case FinanceConstants.PROCEDT_STAGEACC:

			// For validations
			this.showInStage.setReadonly(true);// not required
			this.allowInputInStage.setReadonly(true);// not required
			this.mandInputInStage.setReadonly(false);

			// error labels
			this.showInStage.setLeft("");
			this.allowInputInStage.setLeft("");
			this.mandInputInStage.setLeft(Labels.getLabel("label_FinRefDialogLink_ExecuteInStage.value"));

			// LOV List
			this.btnSearchAccounting.setVisible(true);

			// LOV Label
			this.label_FinanceReferenceDetailDialog_FinRefId
					.setValue(Labels.getLabel("label_FinRefDialogLink_Accounting.value"));

			// ROWS WITH LIST Boxes
			this.rowSingleListbox.setVisible(true);// Show

			// labels of list boxes
			this.label_FinanceReferenceDetailDialog_ShowInStage.setValue("");// not required
			this.label_FinanceReferenceDetailDialog_AllowInputInStage.setValue("");// not required
			this.label_FinanceReferenceDetailDialog_MandInputInStage
					.setValue(Labels.getLabel("label_FinRefDialogLink_ExecuteInStage.value"));

			// List headers of list boxes
			this.listheadShowInStage.setLabel("");// not required
			this.listheadAllowInputInStage.setLabel("");// not required
			this.listheadMandInputInStage.setLabel(Labels.getLabel("label_FinRefDialogLink_ExecuteInStage.value"));

			doEnableByChecked(this.listboxmandInputInStage);
			this.label_FinanceReferenceDetailDialogLink
					.setValue(Labels.getLabel("label_Window_FinanceAccountingList.title"));
			CheckOverride();
			break;

		case FinanceConstants.PROCEDT_TEMPLATE:

			// For validations
			this.showInStage.setReadonly(true);// not required
			this.allowInputInStage.setReadonly(true);// not required
			this.mandInputInStage.setReadonly(false);

			// error labels
			this.showInStage.setLeft("");
			this.allowInputInStage.setLeft("");
			this.mandInputInStage.setLeft(Labels.getLabel("label_FinRefDialogLink_ExecuteInStage.value"));

			// LOV List
			this.btnSearchTemplate.setVisible(true);

			// LOV Label
			this.label_FinanceReferenceDetailDialog_FinRefId
					.setValue(Labels.getLabel("label_FinRefDialogLink_Template.value"));

			// ROWS WITH LIST Boxes
			this.rowSingleListbox.setVisible(true);// Show

			// labels of list boxes
			this.label_FinanceReferenceDetailDialog_ShowInStage.setValue("");// not required
			this.label_FinanceReferenceDetailDialog_AllowInputInStage.setValue("");// not required
			this.label_FinanceReferenceDetailDialog_MandInputInStage
					.setValue(Labels.getLabel("label_FinRefDialogLink_ExecuteInStage.value"));

			// List headers of list boxes
			this.listheadShowInStage.setLabel("");// not required
			this.listheadAllowInputInStage.setLabel("");// not required
			this.listheadMandInputInStage.setLabel(Labels.getLabel("label_FinRefDialogLink_ExecuteInStage.value"));

			doEnableByChecked(this.listboxmandInputInStage);
			this.label_FinanceReferenceDetailDialogLink
					.setValue(Labels.getLabel("label_Window_FinanceMailTemplateList.title"));
			CheckOverride();
			break;

		case FinanceConstants.PROCEDT_FINDEDUP:

			// For validations
			this.showInStage.setReadonly(true);// not required
			this.allowInputInStage.setReadonly(true);// not required
			this.mandInputInStage.setReadonly(false);
			this.overRide.setDisabled(false);
			this.overRideValue.setReadonly(false);
			this.rowOverRide.setVisible(true);
			this.overRideValue.setLeft(Labels.getLabel("label_FinanceReferenceDetailDialog_OverRideValue.value"));

			// error labels
			this.showInStage.setLeft("");
			this.allowInputInStage.setLeft("");
			this.mandInputInStage.setLeft(Labels.getLabel("label_FinRefDialogLink_ExecuteInStage.value"));

			// LOV List
			this.btnSearchFinanceDedupe.setVisible(true);

			// LOV Label
			this.label_FinanceReferenceDetailDialog_FinRefId
					.setValue(Labels.getLabel("label_FinRefDialogLink_FinanceDedupe.value"));

			// ROWS WITH LIST Boxes
			this.rowSingleListbox.setVisible(true);// Show

			// labels of list boxes
			this.label_FinanceReferenceDetailDialog_ShowInStage.setValue("");// not required
			this.label_FinanceReferenceDetailDialog_AllowInputInStage.setValue("");// not required
			this.label_FinanceReferenceDetailDialog_MandInputInStage
					.setValue(Labels.getLabel("label_FinRefDialogLink_ExecuteInStage.value"));

			// List headers of list boxes
			this.listheadShowInStage.setLabel("");// not required
			this.listheadAllowInputInStage.setLabel("");// not required
			this.listheadMandInputInStage.setLabel(Labels.getLabel("label_FinRefDialogLink_ExecuteInStage.value"));

			doEnableByChecked(this.listboxmandInputInStage);
			this.label_FinanceReferenceDetailDialogLink
					.setValue(Labels.getLabel("label_Window_FinanceDedupeList.title"));
			CheckOverride();
			break;

		case FinanceConstants.PROCEDT_CUSTDEDUP:

			// For validations
			this.showInStage.setReadonly(true);// not required
			this.allowInputInStage.setReadonly(true);// not required
			this.mandInputInStage.setReadonly(false);
			this.overRide.setDisabled(false);
			this.overRideValue.setReadonly(false);
			this.rowOverRide.setVisible(true);
			this.overRideValue.setLeft(Labels.getLabel("label_FinanceReferenceDetailDialog_OverRideValue.value"));

			// error labels
			this.showInStage.setLeft("");
			this.allowInputInStage.setLeft("");
			this.mandInputInStage.setLeft(Labels.getLabel("label_FinRefDialogLink_ExecuteInStage.value"));

			// LOV List
			this.btnSearchCustomerDedupe.setVisible(true);

			// LOV Label
			this.label_FinanceReferenceDetailDialog_FinRefId
					.setValue(Labels.getLabel("label_FinRefDialogLink_CustomerDedupe.value"));

			// ROWS WITH LIST Boxes
			this.rowSingleListbox.setVisible(true);// Show

			// labels of list boxes
			this.label_FinanceReferenceDetailDialog_ShowInStage.setValue("");// not required
			this.label_FinanceReferenceDetailDialog_AllowInputInStage.setValue("");// not required
			this.label_FinanceReferenceDetailDialog_MandInputInStage
					.setValue(Labels.getLabel("label_FinRefDialogLink_ExecuteInStage.value"));

			// List headers of list boxes
			this.listheadShowInStage.setLabel("");// not required
			this.listheadAllowInputInStage.setLabel("");// not required
			this.listheadMandInputInStage.setLabel(Labels.getLabel("label_FinRefDialogLink_ExecuteInStage.value"));

			doEnableByChecked(this.listboxmandInputInStage);
			this.label_FinanceReferenceDetailDialogLink
					.setValue(Labels.getLabel("label_Window_CustomerDedupeList.title"));
			CheckOverride();
			break;

		case FinanceConstants.PROCEDT_BLACKLIST:

			// For validations
			this.showInStage.setReadonly(true);// not required
			this.allowInputInStage.setReadonly(true);// not required
			this.mandInputInStage.setReadonly(false);
			this.overRide.setDisabled(false);
			this.overRideValue.setReadonly(false);
			this.rowOverRide.setVisible(true);
			this.overRideValue.setLeft(Labels.getLabel("label_FinanceReferenceDetailDialog_OverRideValue.value"));

			// error labels
			this.showInStage.setLeft("");
			this.allowInputInStage.setLeft("");
			this.mandInputInStage.setLeft(Labels.getLabel("label_FinRefDialogLink_ExecuteInStage.value"));

			// LOV List
			this.btnSearchBlackListDedupe.setVisible(true);

			// LOV Label
			this.label_FinanceReferenceDetailDialog_FinRefId
					.setValue(Labels.getLabel("label_FinRefDialogLink_CustBlackList.value"));

			// ROWS WITH LIST Boxes
			this.rowSingleListbox.setVisible(true);// Show

			// labels of list boxes
			this.label_FinanceReferenceDetailDialog_ShowInStage.setValue("");// not required
			this.label_FinanceReferenceDetailDialog_AllowInputInStage.setValue("");// not required
			this.label_FinanceReferenceDetailDialog_MandInputInStage
					.setValue(Labels.getLabel("label_FinRefDialogLink_ExecuteInStage.value"));

			// List headers of list boxes
			this.listheadShowInStage.setLabel("");// not required
			this.listheadAllowInputInStage.setLabel("");// not required
			this.listheadMandInputInStage.setLabel(Labels.getLabel("label_FinRefDialogLink_ExecuteInStage.value"));

			doEnableByChecked(this.listboxmandInputInStage);
			this.label_FinanceReferenceDetailDialogLink.setValue(Labels.getLabel("label_Window_CustBlackList.title"));
			CheckOverride();
			break;

		case FinanceConstants.PROCEDT_RETURNCHQ:

			// For validations
			this.showInStage.setReadonly(true);// not required
			this.allowInputInStage.setReadonly(true);// not required
			this.mandInputInStage.setReadonly(false);
			this.overRide.setDisabled(false);
			this.overRideValue.setReadonly(false);
			this.rowOverRide.setVisible(false);
			this.overRideValue.setLeft(Labels.getLabel("label_FinanceReferenceDetailDialog_OverRideValue.value"));

			// error labels
			this.showInStage.setLeft("");
			this.allowInputInStage.setLeft("");
			this.mandInputInStage.setLeft(Labels.getLabel("label_FinRefDialogLink_ExecuteInStage.value"));

			// LOV List
			this.btnSearchReturnCheque.setVisible(true);

			// LOV Label
			this.label_FinanceReferenceDetailDialog_FinRefId
					.setValue(Labels.getLabel("label_FinRefDialogLink_CustReturnChq.value"));

			// ROWS WITH LIST Boxes
			this.rowSingleListbox.setVisible(true);// Show

			// labels of list boxes
			this.label_FinanceReferenceDetailDialog_ShowInStage.setValue("");// not required
			this.label_FinanceReferenceDetailDialog_AllowInputInStage.setValue("");// not required
			this.label_FinanceReferenceDetailDialog_MandInputInStage
					.setValue(Labels.getLabel("label_FinRefDialogLink_ExecuteInStage.value"));

			// List headers of list boxes
			this.listheadShowInStage.setLabel("");// not required
			this.listheadAllowInputInStage.setLabel("");// not required
			this.listheadMandInputInStage.setLabel(Labels.getLabel("label_FinRefDialogLink_ExecuteInStage.value"));

			doEnableByChecked(this.listboxmandInputInStage);
			this.label_FinanceReferenceDetailDialogLink.setValue(Labels.getLabel("label_Window_CustReturnChq.title"));
			CheckOverride();
			break;

		case FinanceConstants.PROCEDT_LIMIT:

			// For validations
			this.showInStage.setReadonly(true);// not required
			this.allowInputInStage.setReadonly(true);// not required
			this.mandInputInStage.setReadonly(false);
			this.overRide.setDisabled(false);
			this.overRideValue.setReadonly(false);
			this.rowOverRide.setVisible(false);
			this.overRideValue.setLeft(Labels.getLabel("label_FinanceReferenceDetailDialog_OverRideValue.value"));

			// error labels
			this.showInStage.setLeft("");
			this.allowInputInStage.setLeft("");
			this.mandInputInStage.setLeft(Labels.getLabel("label_FinRefDialogLink_ExecuteInStage.value"));

			// LOV List
			this.btnSearchLimitService.setVisible(true);

			// LOV Label
			this.label_FinanceReferenceDetailDialog_FinRefId
					.setValue(Labels.getLabel("label_FinRefDialogLink_LimitService.value"));

			// ROWS WITH LIST Boxes
			this.rowSingleListbox.setVisible(true);// Show

			// labels of list boxes
			this.label_FinanceReferenceDetailDialog_ShowInStage.setValue("");// not required
			this.label_FinanceReferenceDetailDialog_AllowInputInStage.setValue("");// not required
			this.label_FinanceReferenceDetailDialog_MandInputInStage
					.setValue(Labels.getLabel("label_FinRefDialogLink_ExecuteInStage.value"));

			// List headers of list boxes
			this.listheadShowInStage.setLabel("");// not required
			this.listheadAllowInputInStage.setLabel("");// not required
			this.listheadMandInputInStage.setLabel(Labels.getLabel("label_FinRefDialogLink_ExecuteInStage.value"));

			doEnableByChecked(this.listboxmandInputInStage);
			this.label_FinanceReferenceDetailDialogLink.setValue(Labels.getLabel("label_Window_LimitService.title"));
			CheckOverride();
			break;

		case FinanceConstants.PROCEDT_TATNOTIFICATION:

			// For validations
			this.showInStage.setReadonly(true);// not required
			this.allowInputInStage.setReadonly(true);// not required
			this.mandInputInStage.setReadonly(false);
			this.overRide.setDisabled(false);
			this.overRideValue.setReadonly(false);
			this.rowOverRide.setVisible(false);
			this.overRideValue.setLeft(Labels.getLabel("label_FinanceReferenceDetailDialog_OverRideValue.value"));

			// error labels
			this.showInStage.setLeft("");
			this.allowInputInStage.setLeft("");
			this.mandInputInStage.setLeft(Labels.getLabel("label_FinRefDialogLink_ExecuteInStage.value"));

			// LOV List
			this.btnSearchTatNotification.setVisible(true);
			this.row_AlertType.setVisible(true);

			// LOV Label
			this.label_FinanceReferenceDetailDialog_FinRefId
					.setValue(Labels.getLabel("label_FinRefDialogLink_TATNotification.value"));

			// ROWS WITH LIST Boxes
			this.rowSingleListbox.setVisible(true);// Show

			// labels of list boxes
			this.label_FinanceReferenceDetailDialog_ShowInStage.setValue("");// not required
			this.label_FinanceReferenceDetailDialog_AllowInputInStage.setValue("");// not required
			this.label_FinanceReferenceDetailDialog_MandInputInStage
					.setValue(Labels.getLabel("label_FinRefDialogLink_ExecuteInStage.value"));

			// List headers of list boxes
			this.listheadShowInStage.setLabel("");// not required
			this.listheadAllowInputInStage.setLabel("");// not required
			this.listheadMandInputInStage.setLabel(Labels.getLabel("label_FinRefDialogLink_ExecuteInStage.value"));

			doEnableByChecked(this.listboxmandInputInStage);
			this.label_FinanceReferenceDetailDialogLink
					.setValue(Labels.getLabel("label_FinRefDialogLink_TATNotification.value"));
			CheckOverride();
			break;
		case FinanceConstants.PROCEDT_FINANCETABS:
			// For validations
			this.showInStage.setReadonly(true);// not required
			this.allowInputInStage.setReadonly(true);// not required
			this.mandInputInStage.setReadonly(false);

			// error labels
			this.showInStage.setLeft("");
			this.allowInputInStage.setLeft("");
			this.mandInputInStage.setLeft(Labels.getLabel("label_FinRefDialogLink_StageTabInStage.value"));

			// LOV List
			this.btnFinanceTabs.setVisible(true);// show

			// LOV Label
			this.label_FinanceReferenceDetailDialog_FinRefId
					.setValue(Labels.getLabel("label_FinRefDialogLink_FinanceTabs.value"));

			// ROWS WITH LIST Boxes
			this.rowSingleListbox.setVisible(true);// Show

			// labels of list boxes
			this.label_FinanceReferenceDetailDialog_ShowInStage.setValue("");// not required
			this.label_FinanceReferenceDetailDialog_AllowInputInStage.setValue("");// not required
			this.label_FinanceReferenceDetailDialog_MandInputInStage
					.setValue(Labels.getLabel("label_FinRefDialogLink_ShowInStage.value"));

			// List headers of list boxes
			this.listheadShowInStage.setLabel("");// not required
			this.listheadAllowInputInStage.setLabel("");// not required
			this.listheadMandInputInStage.setLabel(Labels.getLabel("label_FinRefDialogLink_ShowInStage.value"));

			doEnableByChecked(this.listboxmandInputInStage);
			this.label_FinanceReferenceDetailDialogLink.setValue(Labels.getLabel("label_Window_FinanceTabsList.title"));

		default:
			break;
		}
		logger.debug("Leaving");
	}

	// =====ADD or Update========//
	private void processFinRefDetails(FinanceReferenceDetail frd) throws InterruptedException {
		logger.debug("Entering");

		if (frd.getRecordType() != null) {
			if (frd.getRecordType().equals(PennantConstants.RCD_ADD)
					|| frd.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)
					|| frd.getRecordType().equals(PennantConstants.RECORD_TYPE_UPD)) {
				if ("Save".equals(frd.getUserAction())) {
					frd.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				}
			} else {
				if (frd.getRecordType().equals(PennantConstants.RCD_DEL)) {
					frd.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				} else if (StringUtils.isEmpty(frd.getRecordType())) {
					// setting record type as Update while changing approved record,since record type is empty ""
					frd.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				}
			}
		} else {
			frd.setRecordType(PennantConstants.RECORD_TYPE_NEW);
			frd.setNewRecord(true);
		}

		switch (frd.getFinRefType()) {
		case FinanceConstants.PROCEDT_CHECKLIST:
			processAddOrUpdate(frd, getFinanceReferenceDetailDialogCtrl().listBoxFinanceCheckList);
			break;
		case FinanceConstants.PROCEDT_AGREEMENT:
			processAddOrUpdate(frd, getFinanceReferenceDetailDialogCtrl().listboxFinanceAgreementLink);
			break;
		case FinanceConstants.PROCEDT_ELIGIBILITY:
			processAddOrUpdate(frd, getFinanceReferenceDetailDialogCtrl().listBoxEligibilityRules);
			break;
		case FinanceConstants.PROCEDT_RTLSCORE:
			processAddOrUpdate(frd, getFinanceReferenceDetailDialogCtrl().listBoxScoringGroup);
			break;
		case FinanceConstants.PROCEDT_CORPSCORE:
			processAddOrUpdate(frd, getFinanceReferenceDetailDialogCtrl().listBoxCorpScoringGroup);
			break;
		case FinanceConstants.PROCEDT_STAGEACC:
			processAddOrUpdate(frd, getFinanceReferenceDetailDialogCtrl().listBoxAccounts);
			break;
		case FinanceConstants.PROCEDT_TEMPLATE:
			processAddOrUpdate(frd, getFinanceReferenceDetailDialogCtrl().listBoxTemplates);
			break;
		case FinanceConstants.PROCEDT_FINDEDUP:
			processAddOrUpdate(frd, getFinanceReferenceDetailDialogCtrl().listBoxDedupRules);
			break;
		case FinanceConstants.PROCEDT_CUSTDEDUP:
			processAddOrUpdate(frd, getFinanceReferenceDetailDialogCtrl().listBoxCustDedupRules);
			break;
		case FinanceConstants.PROCEDT_BLACKLIST:
			processAddOrUpdate(frd, getFinanceReferenceDetailDialogCtrl().listBoxBlackListRules);
			break;
		case FinanceConstants.PROCEDT_RETURNCHQ:
			processAddOrUpdate(frd, getFinanceReferenceDetailDialogCtrl().listBoxReturnCheques);
			break;
		case FinanceConstants.PROCEDT_LIMIT:
			processAddOrUpdate(frd, getFinanceReferenceDetailDialogCtrl().listBoxLimitService);
			break;
		case FinanceConstants.PROCEDT_TATNOTIFICATION:
			processAddOrUpdate(frd, getFinanceReferenceDetailDialogCtrl().listBoxTatNotification);
			break;
		case FinanceConstants.PROCEDT_FINANCETABS:
			processAddOrUpdate(frd, getFinanceReferenceDetailDialogCtrl().listboxFinanceTabs);
			break;
		default:
			break;
		}
		logger.debug("Leaving");
	}

	public void processAddOrUpdate(FinanceReferenceDetail newFinrefDet, Listbox listbox) throws InterruptedException {
		logger.debug("Entering");

		boolean contains = false;
		List<Listitem> avlFinRef = listbox.getItems();

		for (int i = 0; i < avlFinRef.size(); i++) {
			FinanceReferenceDetail finRefDet = (FinanceReferenceDetail) avlFinRef.get(i).getAttribute("data");
			if (finRefDet.getFinRefId() == newFinrefDet.getFinRefId()) {
				if (newFinrefDet.getRecordType().equals(PennantConstants.RECORD_TYPE_UPD)) {
					listbox.removeItemAt(avlFinRef.get(i).getIndex());
				} else if (PennantConstants.RCD_DEL.equals(finRefDet.getRecordType())) {
					listbox.removeItemAt(avlFinRef.get(i).getIndex());
					newFinrefDet.setRecordType(PennantConstants.RECORD_TYPE_NEW);
					newFinrefDet.setNewRecord(false);
					newFinrefDet.setFinRefDetailId(finRefDet.getFinRefDetailId());
					newFinrefDet.setVersion(finRefDet.getVersion());

					FinanceReferenceDetail befImage = new FinanceReferenceDetail();
					BeanUtils.copyProperties(finRefDet.getBefImage(), befImage);
					newFinrefDet.setBefImage(befImage);
				} else {
					MessageUtil.showError("30542:" + newFinrefDet.getLovDescRefDesc() + " already linked.");
					contains = true;
				}
				break;
			}
		}

		if (!contains) {
			List<FinanceReferenceDetail> finRefDetailList = new ArrayList<FinanceReferenceDetail>();
			finRefDetailList.add(newFinrefDet);
			getFinanceReferenceDetailDialogCtrl().dofillListbox(finRefDetailList, listbox);
			this.window_FinanceReferenceDetailDialogLink.onClose();
			getFinanceReferenceDetailDialogCtrl().window_FinanceReferenceDetailDialog.setVisible(true);
		}

		if ("listBoxCorpScoringGroup".equals(listbox.getId()) && listbox.getVisibleItemCount() == 1) {
			getFinanceReferenceDetailDialogCtrl().btnNew_FinCorpScoringGroup.setVisible(false);
		}
		logger.debug("Leaving");
	}

	// ====== Delete ===============//
	private void deleteFinRrefDetails(FinanceReferenceDetail finRefDetail) {
		logger.debug("Entering");

		switch (finRefDetail.getFinRefType()) {
		case FinanceConstants.PROCEDT_CHECKLIST:
			processDelet(finRefDetail, getFinanceReferenceDetailDialogCtrl().listBoxFinanceCheckList);
			break;
		case FinanceConstants.PROCEDT_AGREEMENT:
			processDelet(finRefDetail, getFinanceReferenceDetailDialogCtrl().listboxFinanceAgreementLink);
			break;
		case FinanceConstants.PROCEDT_ELIGIBILITY:
			processDelet(finRefDetail, getFinanceReferenceDetailDialogCtrl().listBoxEligibilityRules);
			break;
		case FinanceConstants.PROCEDT_RTLSCORE:
			processDelet(finRefDetail, getFinanceReferenceDetailDialogCtrl().listBoxScoringGroup);
			break;
		case FinanceConstants.PROCEDT_CORPSCORE:
			processDelet(finRefDetail, getFinanceReferenceDetailDialogCtrl().listBoxCorpScoringGroup);
			break;
		case FinanceConstants.PROCEDT_STAGEACC:
			processDelet(finRefDetail, getFinanceReferenceDetailDialogCtrl().listBoxAccounts);
			break;
		case FinanceConstants.PROCEDT_TEMPLATE:
			processDelet(finRefDetail, getFinanceReferenceDetailDialogCtrl().listBoxTemplates);
			break;
		case FinanceConstants.PROCEDT_FINDEDUP:
			processDelet(finRefDetail, getFinanceReferenceDetailDialogCtrl().listBoxDedupRules);
			break;
		case FinanceConstants.PROCEDT_CUSTDEDUP:
			processDelet(finRefDetail, getFinanceReferenceDetailDialogCtrl().listBoxCustDedupRules);
			break;
		case FinanceConstants.PROCEDT_BLACKLIST:
			processDelet(finRefDetail, getFinanceReferenceDetailDialogCtrl().listBoxBlackListRules);
			break;
		case FinanceConstants.PROCEDT_RETURNCHQ:
			processDelet(finRefDetail, getFinanceReferenceDetailDialogCtrl().listBoxReturnCheques);
			break;
		case FinanceConstants.PROCEDT_LIMIT:
			processDelet(finRefDetail, getFinanceReferenceDetailDialogCtrl().listBoxLimitService);
			break;
		case FinanceConstants.PROCEDT_TATNOTIFICATION:
			processDelet(finRefDetail, getFinanceReferenceDetailDialogCtrl().listBoxTatNotification);
			break;
		case FinanceConstants.PROCEDT_FINANCETABS:
			processDelet(finRefDetail, getFinanceReferenceDetailDialogCtrl().listboxFinanceTabs);
		default:
			break;
		}
		logger.debug("Leaving");

	}

	public void processDelet(FinanceReferenceDetail newFinrefDet, Listbox listbox) {
		logger.debug("Entering");

		List<Listitem> avlFinRef = listbox.getItems();

		for (int i = 0; i < avlFinRef.size(); i++) {
			FinanceReferenceDetail finRefDet = (FinanceReferenceDetail) avlFinRef.get(i).getAttribute("data");
			if (finRefDet.getFinRefId() == newFinrefDet.getFinRefId()) {
				if (finRefDet.getRecordStatus().equals(PennantConstants.RCD_STATUS_APPROVED)
						|| (finRefDet.getRecordType().equals(PennantConstants.RECORD_TYPE_UPD)
								&& !finRefDet.isNewRecord())) {
					listbox.removeItemAt(avlFinRef.get(i).getIndex());
					List<FinanceReferenceDetail> finRefDetailList = new ArrayList<FinanceReferenceDetail>();
					finRefDetailList.add(newFinrefDet);
					getFinanceReferenceDetailDialogCtrl().dofillListbox(finRefDetailList, listbox);
					if ("listBoxCorpScoringGroup".equals(listbox.getId()) && listbox.getVisibleItemCount() == 1) {
						getFinanceReferenceDetailDialogCtrl().btnNew_FinCorpScoringGroup.setVisible(false);
					} else {
						getFinanceReferenceDetailDialogCtrl().btnNew_FinCorpScoringGroup.setVisible(true);
					}
				} else {
					listbox.removeItemAt(avlFinRef.get(i).getIndex());
					if ("listBoxCorpScoringGroup".equals(listbox.getId()) && listbox.getItemCount() == 0) {
						getFinanceReferenceDetailDialogCtrl().btnNew_FinCorpScoringGroup.setVisible(true);
					}
				}

				break;
			}
		}
		logger.debug("Leaving");
	}

	// ===========Helpers========//

	public void doToggleInReadOnlyMode(Listbox listbox, boolean enableOrDisable) {
		logger.debug("Entering");
		for (int i = 0; i < listbox.getItems().size(); i++) {
			Listitem item = (Listitem) listbox.getItems().get(i);
			Listcell lc = (Listcell) item.getChildren().get(0);
			Checkbox ck = (Checkbox) lc.getChildren().get(0);
			ck.setDisabled(enableOrDisable);

		}
		logger.debug("Leaving");
	}

	public void doToggleDisableByChkVal(Listbox listbox, String id, boolean enableOrDisable) {
		logger.debug("Entering");
		for (int i = 0; i < listbox.getItems().size(); i++) {
			Listitem item = (Listitem) listbox.getItems().get(i);
			Listcell lc = (Listcell) item.getChildren().get(0);
			Checkbox ck = (Checkbox) lc.getChildren().get(0);
			if (ck.getValue().equals(id)) {
				if (enableOrDisable) {
					ck.setChecked(!enableOrDisable);
				}
				ck.setDisabled(enableOrDisable);
				break;
			}
		}
		logger.debug("Leaving");
	}

	public void doEnableByChecked(Listbox listbox) {
		logger.debug("Entering");
		for (int i = 0; i < listbox.getItems().size(); i++) {
			Listitem item = (Listitem) listbox.getItems().get(i);
			Listcell lc = (Listcell) item.getChildren().get(0);
			Checkbox ck = (Checkbox) lc.getChildren().get(0);
			if (ck.isChecked()) {
				ck.setDisabled(false);
			} else {
				ck.setDisabled(true);
			}
		}
		logger.debug("Leaving");
	}

	public void doEnableByOtherChecked(Listbox fromlistbox, Listbox tolistbox) {
		logger.debug("Entering");
		for (int i = 0; i < fromlistbox.getItems().size(); i++) {
			Listitem item = (Listitem) fromlistbox.getItems().get(i);
			Listcell lc = (Listcell) item.getChildren().get(0);
			Checkbox ck = (Checkbox) lc.getChildren().get(0);
			if (ck.isChecked()) {
				doToggleDisableByChkVal(tolistbox, ck.getValue().toString(), false);

			}
		}
		logger.debug("Leaving");
	}

	public String getCheckedValues(Listbox listbox) {
		logger.debug("Entering");
		String value = "";
		for (int i = 0; i < listbox.getItems().size(); i++) {
			Listitem item = (Listitem) listbox.getItems().get(i);
			Listcell lc = (Listcell) item.getChildren().get(0);
			Checkbox ck = (Checkbox) lc.getChildren().get(0);
			if (ck.isChecked()) {
				value = value + ck.getValue() + ",";
			}
		}
		logger.debug("Leaving");
		return value;
	}

	public void onCheck$overRide(Event event) {
		logger.debug("Entering" + event.toString());
		CheckOverride();
		logger.debug("Leaving" + event.toString());
	}

	private void CheckOverride() {
		if (this.overRide.isChecked()) {
			this.overRideValue.setReadonly(false);
			this.space_Override.setSclass(PennantConstants.mandateSclass);
		} else {
			this.overRideValue.setValue(0);
			this.overRideValue.setReadonly(true);
			this.space_Override.setSclass("");
		}
	}

	/**
	 * On Deviation Checked
	 * 
	 * @param event
	 */
	public void onCheck$allowDeviation(Event event) {
		logger.debug("Entering" + event.toString());
		checkAllowDeviation();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * To enable and Disable the fields
	 */
	private void checkAllowDeviation() {
		logger.debug("Entering");
		int refType = getFinanceReferenceDetail().getFinRefType();
		if (refType == FinanceConstants.PROCEDT_CHECKLIST) {
			if (allowDeviation.isChecked()) {
				this.allowWaiver.setDisabled(false);
				this.allowPostpone.setDisabled(false);
				this.allowExpire.setDisabled(false);
			} else {
				this.allowWaiver.setChecked(false);
				this.allowPostpone.setChecked(false);
				this.allowExpire.setChecked(false);
				this.allowWaiver.setDisabled(true);
				this.allowPostpone.setDisabled(true);
				this.allowExpire.setDisabled(true);
			}
		}
		logger.debug("Leaving");
	}

}
