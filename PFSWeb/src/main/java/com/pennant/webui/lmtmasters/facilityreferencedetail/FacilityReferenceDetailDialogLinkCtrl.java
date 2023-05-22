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
 * * FileName : FacilityReferenceDetailDialogCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 26-11-2011
 * * * Modified Date : 26-11-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 26-11-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.webui.lmtmasters.facilityreferencedetail;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataAccessException;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Longbox;
import org.zkoss.zul.Row;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.applicationmaster.AgreementDefinition;
import com.pennant.backend.model.bmtmasters.CheckList;
import com.pennant.backend.model.lmtmasters.FacilityReferenceDetail;
import com.pennant.backend.model.mail.MailTemplate;
import com.pennant.backend.model.rmtmasters.AccountingSet;
import com.pennant.backend.model.rmtmasters.ScoringGroup;
import com.pennant.backend.model.rulefactory.Rule;
import com.pennant.backend.service.PagedListService;
import com.pennant.backend.service.lmtmasters.FacilityReferenceDetailService;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.NotificationConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.RuleConstants;
import com.pennant.util.Constraint.PTNumberValidator;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.searchdialogs.ExtendedSearchListBox;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.jdbc.search.Filter;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pff.constants.AccountingEvent;

/**
 * This is the controller class for the /WEB-INF/pages/LMTMasters/FacilityReferenceDetail
 * /facilityReferenceDetailDialog.zul file.
 */
public class FacilityReferenceDetailDialogLinkCtrl extends GFCBaseCtrl<FacilityReferenceDetail> {
	private static final long serialVersionUID = -2872130825329784644L;
	private static final Logger logger = LogManager.getLogger(FacilityReferenceDetailDialogLinkCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the zul-file
	 * are getting auto wired by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_FacilityReferenceDetailDialogLink; // auto wired
	protected Textbox finType; // auto wired
	protected Intbox finRefType; // auto wired
	protected Longbox finRefId; // auto wired
	protected Checkbox isActive; // auto wired
	protected Textbox showInStage; // auto wired
	protected Textbox mandInputInStage; // auto wired
	protected Textbox allowInputInStage; // auto wired
	protected Checkbox overRide;
	protected Intbox overRideValue;
	protected Textbox lovDescRefDesc;
	protected Row statusRow;

	// not auto wired variables
	private FacilityReferenceDetail facilityReferenceDetail; // over handed per parameter
	private FacilityReferenceDetail prvFacilityReferenceDetail; // over handed per parameter
	private transient FacilityReferenceDetailDialogCtrl facilityReferenceDetailDialogCtrl; // over handed per parameter

	private transient boolean validationOn;

	// ServiceDAOs / Domain Classes
	private transient FacilityReferenceDetailService facilityReferenceDetailService;
	private transient PagedListService pagedListService;
	private Map<String, List<ErrorDetail>> overideMap = new HashMap<String, List<ErrorDetail>>();
	private String roleCodes;

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

	protected Label label_FacilityReferenceDetailDialog_FinRefId;
	protected Row rowSingleListbox;
	protected Row rowDoubleListbox;
	protected Row rowOverRide;

	protected Label label_FacilityReferenceDetailDialogLink;

	protected Label label_FacilityReferenceDetailDialog_ShowInStage;
	protected Label label_FacilityReferenceDetailDialog_AllowInputInStage;
	protected Label label_FacilityReferenceDetailDialog_MandInputInStage;

	protected Listheader listheadShowInStage;
	protected Listheader listheadAllowInputInStage;
	protected Listheader listheadMandInputInStage;

	/**
	 * default constructor.<br>
	 */
	public FacilityReferenceDetailDialogLinkCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "FacilityReferenceDetailDialog";
	}

	// Component Events

	/**
	 * Before binding the data and calling the dialog window we check, if the zul-file is called with a parameter for a
	 * selected FacilityReferenceDetail object in a Map.
	 * 
	 * @param event
	 */
	public void onCreate$window_FacilityReferenceDetailDialogLink(Event event) {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_FacilityReferenceDetailDialogLink);

		try {

			/* set components visible dependent of the users rights */
			doCheckRights();

			// READ OVERHANDED parameters !
			if (arguments.containsKey("facilityReferenceDetail")) {
				this.facilityReferenceDetail = (FacilityReferenceDetail) arguments.get("facilityReferenceDetail");

				boolean addBefImage = true;
				if (PennantConstants.RECORD_TYPE_NEW.equals(this.facilityReferenceDetail.getRecordType())
						|| PennantConstants.RECORD_TYPE_UPD.equals(this.facilityReferenceDetail.getRecordType())) {
					if (!this.facilityReferenceDetail.isNewRecord()) {
						addBefImage = false;
					}
				}
				if (addBefImage) {
					FacilityReferenceDetail befImage = new FacilityReferenceDetail();
					BeanUtils.copyProperties(this.facilityReferenceDetail, befImage);
					this.facilityReferenceDetail.setBefImage(befImage);
				}
				setFacilityReferenceDetail(this.facilityReferenceDetail);
			} else {
				setFacilityReferenceDetail(null);
			}

			if (arguments.containsKey("facilityReferenceDetailDialogCtrl")) {
				setFacilityReferenceDetailDialogCtrl(
						(FacilityReferenceDetailDialogCtrl) arguments.get("facilityReferenceDetailDialogCtrl"));
			} else {
				setFacilityReferenceDetailDialogCtrl(null);
			}

			if (arguments.containsKey("roleCodeList")) {
				roleCodes = arguments.get("roleCodeList").toString();
			}

			doLoadWorkFlow(this.facilityReferenceDetail.isWorkflow(), this.facilityReferenceDetail.getWorkflowId(),
					this.facilityReferenceDetail.getNextTaskId());

			if (isWorkFlowEnabled()) {
				this.userAction = setListRecordStatus(this.userAction);
				getUserWorkspace().allocateRoleAuthorities(getRole(), "FacilityReferenceDetailDialog");
			}

			// set Field Properties
			doSetFieldProperties();
			doShowDialog(getFacilityReferenceDetail());
		} catch (Exception e) {
			MessageUtil.showError(e);
			this.window_FacilityReferenceDetailDialogLink.onClose();
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
	 * The rights are get from the spring framework users grantedAuthority(). A right is only a string. <br>
	 */
	private void doCheckRights() {
		logger.debug("Entering");
		getUserWorkspace().allocateAuthorities(super.pageRightName);
		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_FacilityReferenceDetailDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_FacilityReferenceDetailDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_FacilityReferenceDetailDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_FacilityReferenceDetailDialog_btnSave"));
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
		MessageUtil.showHelpWindow(event, window_FacilityReferenceDetailDialogLink);
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
	 * The Click event is raised when the Close Button control is clicked.
	 * 
	 * @param event An event sent to the event handler of a component.
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
		doWriteBeanToComponents(this.facilityReferenceDetail.getBefImage());
		doReadOnly();
		this.btnCtrl.setInitEdit();
		this.btnEdit.setVisible(true);
		this.btnCancel.setVisible(false);
		logger.debug("Leaving");
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aFacilityReferenceDetail FacilityReferenceDetail
	 */
	public void doWriteBeanToComponents(FacilityReferenceDetail aFacilityReferenceDetail) {
		logger.debug("Entering");
		this.finType.setValue(aFacilityReferenceDetail.getFinType());
		this.finRefType.setValue(aFacilityReferenceDetail.getFinRefType());
		this.finRefId.setValue(aFacilityReferenceDetail.getFinRefId());
		this.isActive.setChecked(aFacilityReferenceDetail.isIsActive());
		this.showInStage.setValue(aFacilityReferenceDetail.getShowInStage());
		this.mandInputInStage.setValue(aFacilityReferenceDetail.getMandInputInStage());
		this.allowInputInStage.setValue(aFacilityReferenceDetail.getAllowInputInStage());
		this.overRide.setChecked(aFacilityReferenceDetail.isOverRide());
		this.overRideValue.setValue(aFacilityReferenceDetail.getOverRideValue());
		this.lovDescRefDesc.setValue(aFacilityReferenceDetail.getLovDescRefDesc());

		if (aFacilityReferenceDetail.getShowInStage() != null
				&& StringUtils.isNotEmpty(aFacilityReferenceDetail.getShowInStage())) {
			String[] roles = aFacilityReferenceDetail.getShowInStage().split(",");
			for (int i = 0; i < roles.length; i++) {
				checkShowInStageMap.put(roles[i], roles[i]);
			}
		}
		if (aFacilityReferenceDetail.getAllowInputInStage() != null
				&& StringUtils.isNotEmpty(aFacilityReferenceDetail.getAllowInputInStage())) {
			String[] roles = aFacilityReferenceDetail.getAllowInputInStage().split(",");
			for (int i = 0; i < roles.length; i++) {
				checkAllowInputInStage.put(roles[i], roles[i]);
			}
		}
		if (aFacilityReferenceDetail.getMandInputInStage() != null
				&& StringUtils.isNotEmpty(aFacilityReferenceDetail.getMandInputInStage())) {
			String[] roles = aFacilityReferenceDetail.getMandInputInStage().split(",");
			for (int i = 0; i < roles.length; i++) {
				checkMandInputInStageMap.put(roles[i], roles[i]);
			}
		}

		fillListBox(this.listboxshowInStage, roleCodes, checkShowInStageMap, FinanceConstants.PROCEDT_SHOWINSTAGE);
		fillListBox(this.listboxallowInputInStage, roleCodes, checkAllowInputInStage,
				FinanceConstants.PROCEDT_ALWINPUTSTAGE);
		fillListBox(this.listboxmandInputInStage, roleCodes, checkMandInputInStageMap,
				FinanceConstants.PROCEDT_MANDINPUTSTAGE);

		doDesignByType(getFacilityReferenceDetail());
		this.recordStatus.setValue(aFacilityReferenceDetail.getRecordStatus());
		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aFacilityReferenceDetail
	 */
	public void doWriteComponentsToBean(FacilityReferenceDetail aFacilityReferenceDetail) {
		logger.debug("Entering");

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		try {
			if (this.finType.getValue() == null || StringUtils.isEmpty(this.finType.getValue())) {
				throw new WrongValueException(this.finType, Labels.getLabel("FIELD_NO_EMPTY",
						new String[] { Labels.getLabel("label_FacilityReferenceDetailDialog_FinType.value") }));
			}
			aFacilityReferenceDetail.setFinType(this.finType.getValue());

		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aFacilityReferenceDetail.setFinRefType(this.finRefType.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aFacilityReferenceDetail.setFinRefId(this.finRefId.getValue());

		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aFacilityReferenceDetail.setIsActive(this.isActive.isChecked());
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
			aFacilityReferenceDetail.setShowInStage(this.showInStage.getValue());
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
			aFacilityReferenceDetail.setAllowInputInStage(this.allowInputInStage.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if (!this.mandInputInStage.isReadonly()) {
				// Set checked values
				this.mandInputInStage.setValue(getCheckedValues(listboxmandInputInStage));
				// then check for empty
				if (this.mandInputInStage.getValue() == null || StringUtils.isEmpty(this.mandInputInStage.getValue())) {
					throw new WrongValueException(this.listboxmandInputInStage,
							Labels.getLabel("FIELD_NO_EMPTY", new String[] { this.mandInputInStage.getLeft() }));
				}
			}
			aFacilityReferenceDetail.setMandInputInStage(this.mandInputInStage.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if (this.lovDescRefDesc.getValue() == null || StringUtils.isEmpty(this.lovDescRefDesc.getValue())) {
				throw new WrongValueException(this.lovDescRefDesc, Labels.getLabel("FIELD_NO_EMPTY",
						new String[] { Labels.getLabel("label_FacilityReferenceDetailDialog_FinType.value") }));
			}
			aFacilityReferenceDetail.setLovDescRefDesc(this.lovDescRefDesc.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {

			aFacilityReferenceDetail.setOverRide(this.overRide.isChecked());
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
			aFacilityReferenceDetail.setOverRideValue(this.overRideValue.getValue());
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

		aFacilityReferenceDetail.setRecordStatus(this.recordStatus.getValue());
		logger.debug("Leaving");
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the readOnly mode accordingly.
	 * 
	 * @param aFacilityReferenceDetail
	 */
	public void doShowDialog(FacilityReferenceDetail aFacilityReferenceDetail) {
		logger.debug("Entering");

		/* fill the components with the data */
		doWriteBeanToComponents(aFacilityReferenceDetail);

		/* set Read only mode accordingly if the object is new or not. */
		if (aFacilityReferenceDetail.isNewRecord()) {
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

		try {

			getFacilityReferenceDetailDialogCtrl().window_FacilityReferenceDetailDialog.setVisible(false);
			getFacilityReferenceDetailDialogCtrl().window_FacilityReferenceDetailDialog.getParent()
					.appendChild(window_FacilityReferenceDetailDialogLink);

		} catch (UiException e) {
			logger.error("Exception: ", e);
			this.window_FacilityReferenceDetailDialogLink.onClose();
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
					Labels.getLabel("label_FacilityReferenceDetailDialog_FinType.value"), null, true));
		}
		if (!this.finRefType.isReadonly()) {
			this.finRefType.setConstraint(new PTNumberValidator(
					Labels.getLabel("label_FacilityReferenceDetailDialog_FinRefType.value"), true));
		}
		if (!this.finRefId.isReadonly()) {
			this.finRefId.setConstraint(
					new PTNumberValidator(Labels.getLabel("label_FacilityReferenceDetailDialog_FinRefId.value"), true));
		}
		if (!this.showInStage.isReadonly()) {
			this.showInStage.setConstraint(new PTStringValidator(
					Labels.getLabel("label_FacilityReferenceDetailDialog_ShowInStage.value"), null, true));
		}
		if (!this.mandInputInStage.isReadonly()) {
			this.mandInputInStage.setConstraint(new PTStringValidator(
					Labels.getLabel("label_FacilityReferenceDetailDialog_MandInputInStage.value"), null, true));
		}
		if (!this.allowInputInStage.isReadonly()) {
			this.allowInputInStage.setConstraint(new PTStringValidator(
					Labels.getLabel("label_FacilityReferenceDetailDialog_AllowInputInStage.value"), null, true));
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
		logger.debug("Leaving");
	}

	private void doDelete() throws InterruptedException {
		logger.debug(Literal.ENTERING);

		final FacilityReferenceDetail aFacilityReferenceDetail = new FacilityReferenceDetail();
		BeanUtils.copyProperties(getFacilityReferenceDetail(), aFacilityReferenceDetail);

		final String keyReference = this.label_FacilityReferenceDetailDialog_FinRefId.getValue() + " : "
				+ aFacilityReferenceDetail.getLovDescRefDesc();
		doDelete(keyReference, aFacilityReferenceDetail);

		logger.debug(Literal.LEAVING);
	}

	protected void onDoDelete(final FacilityReferenceDetail aFacilityReferenceDetail) {
		aFacilityReferenceDetail.setRecordType(PennantConstants.RCD_DEL);
		try {
			deleteFinRrefDetails(aFacilityReferenceDetail);
			this.window_FacilityReferenceDetailDialogLink.onClose();
			getFacilityReferenceDetailDialogCtrl().window_FacilityReferenceDetailDialog.setVisible(true);
		} catch (DataAccessException e) {
			MessageUtil.showError(e);
		}

	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug("Entering");

		if (getFacilityReferenceDetail().isNewRecord()) {
			this.btnCancel.setVisible(false);
			this.isActive.setChecked(true);
			this.isActive.setDisabled(true);
		} else {
			this.btnCancel.setVisible(true);
			this.btnSearchAggCode.setDisabled(true);
			this.btnSearchElgRule.setDisabled(true);
			this.btnSearchQuestionId.setDisabled(true);
			this.btnSearchScoringGroup.setDisabled(true);
			this.btnSearchCorpScoringGroup.setDisabled(true);
			this.isActive.setDisabled(isReadOnly("FacilityReferenceDetailDialog_isActive"));
		}
		if (getFacilityReferenceDetail().getFinRefType() == FinanceConstants.PROCEDT_RTLSCORE
				|| getFacilityReferenceDetail().getFinRefType() == FinanceConstants.PROCEDT_CORPSCORE
				|| getFacilityReferenceDetail().getFinRefType() == FinanceConstants.PROCEDT_STAGEACC
				|| getFacilityReferenceDetail().getFinRefType() == FinanceConstants.PROCEDT_TEMPLATE) {
			doToggleInReadOnlyMode(this.listboxmandInputInStage, isReadOnly("FacilityReferenceDetailDialog_isActive"));
		} else {
			doToggleInReadOnlyMode(this.listboxshowInStage, isReadOnly("FacilityReferenceDetailDialog_isActive"));
			doEnableByChecked(this.listboxallowInputInStage);
			doEnableByOtherChecked(this.listboxshowInStage, this.listboxallowInputInStage);
			if (getFacilityReferenceDetail().getFinRefType() == FinanceConstants.PROCEDT_CHECKLIST) {
				doEnableByChecked(this.listboxmandInputInStage);
				doEnableByOtherChecked(this.listboxallowInputInStage, this.listboxmandInputInStage);
			}
		}
		this.finType.setReadonly(true);
		this.finRefType.setReadonly(isReadOnly("FacilityReferenceDetailDialog_finRefType"));
		this.finRefId.setReadonly(isReadOnly("FacilityReferenceDetailDialog_finRefId"));
		this.overRide.setDisabled(false);
		this.overRideValue.setReadonly(false);
		/*
		 * this.showInStage.setReadonly(isReadOnly( "FacilityReferenceDetailDialog_showInStage"));
		 * this.mandInputInStage.setReadonly (isReadOnly("FacilityReferenceDetailDialog_mandInputInStage"));
		 * this.allowInputInStage .setReadonly(isReadOnly("FacilityReferenceDetailDialog_allowInputInStage" ));
		 */

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}

			if (this.facilityReferenceDetail.isNewRecord()) {
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
		this.btnSearchQuestionId.setDisabled(true);
		this.btnSearchScoringGroup.setDisabled(true);
		this.btnSearchCorpScoringGroup.setDisabled(true);
		this.overRide.setDisabled(true);
		this.overRideValue.setReadonly(true);
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
		final FacilityReferenceDetail aFacilityReferenceDetail = new FacilityReferenceDetail();
		BeanUtils.copyProperties(getFacilityReferenceDetail(), aFacilityReferenceDetail);
		doWriteComponentsToBean(aFacilityReferenceDetail);
		// save it to database
		try {
			processFinRefDetails(aFacilityReferenceDetail);
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

	public FacilityReferenceDetail getFacilityReferenceDetail() {
		return this.facilityReferenceDetail;
	}

	public void setFacilityReferenceDetail(FacilityReferenceDetail facilityReferenceDetail) {
		this.facilityReferenceDetail = facilityReferenceDetail;
	}

	public void setFacilityReferenceDetailService(FacilityReferenceDetailService facilityReferenceDetailService) {
		this.facilityReferenceDetailService = facilityReferenceDetailService;
	}

	public FacilityReferenceDetailService getFacilityReferenceDetailService() {
		return this.facilityReferenceDetailService;
	}

	public FacilityReferenceDetailDialogCtrl getFacilityReferenceDetailDialogCtrl() {
		return facilityReferenceDetailDialogCtrl;
	}

	public void setFacilityReferenceDetailDialogCtrl(
			FacilityReferenceDetailDialogCtrl facilityReferenceDetailDialogCtrl) {
		this.facilityReferenceDetailDialogCtrl = facilityReferenceDetailDialogCtrl;
	}

	public PagedListService getPagedListService() {
		return pagedListService;
	}

	public void setPagedListService(PagedListService pagedListService) {
		this.pagedListService = pagedListService;
	}

	public void onClick$btnNotes(Event event) {
		doShowNotes(this.facilityReferenceDetail);
	}

	@Override
	protected String getReference() {
		return String.valueOf(this.facilityReferenceDetail.getFinRefDetailId());
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

	public FacilityReferenceDetail getPrvFacilityReferenceDetail() {
		return prvFacilityReferenceDetail;
	}

	// =======================================//
	public void onClick$btnSearchQuestionId(Event event) {
		logger.debug("Entering" + event.toString());
		Object dataObject = ExtendedSearchListBox.show(this.window_FacilityReferenceDetailDialogLink, "CheckList");
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
		logger.debug("Entering");
		Object dataObject = ExtendedSearchListBox.show(this.window_FacilityReferenceDetailDialogLink,
				"AgreementDefinition");
		if (dataObject instanceof String) {
			this.lovDescRefDesc.setValue("");
		} else {
			AgreementDefinition details = (AgreementDefinition) dataObject;
			if (details != null) {
				this.finRefId.setValue(details.getAggId());
				this.lovDescRefDesc.setValue(details.getAggCode() + "-" + details.getAggName());
				getFacilityReferenceDetail().setLovDescCodelov(details.getAggCode());
				getFacilityReferenceDetail().setLovDescNamelov(details.getAggName());
			}
		}
		logger.debug("Leaving");
	}

	public void onClick$btnSearchScoringGroup(Event event) {
		logger.debug("Entering");
		Filter[] filters = new Filter[1];
		filters[0] = new Filter("CategoryType", PennantConstants.PFF_CUSTCTG_INDIV, Filter.OP_EQUAL);
		Object dataObject = ExtendedSearchListBox.show(this.window_FacilityReferenceDetailDialogLink, "ScoringGroup",
				filters);
		if (dataObject instanceof String) {
			this.lovDescRefDesc.setValue("");
		} else {
			ScoringGroup details = (ScoringGroup) dataObject;
			if (details != null) {
				this.finRefId.setValue(details.getScoreGroupId());
				this.lovDescRefDesc.setValue(details.getScoreGroupCode() + "-" + details.getScoreGroupName());
				getFacilityReferenceDetail().setLovDescCodelov(details.getScoreGroupCode());
				getFacilityReferenceDetail().setLovDescNamelov(details.getScoreGroupName());
			}
		}
		logger.debug("Leaving");
	}

	public void onClick$btnSearchCorpScoringGroup(Event event) {
		logger.debug("Entering");
		Filter[] filters = new Filter[1];
		filters[0] = new Filter("CategoryType", PennantConstants.PFF_CUSTCTG_CORP, Filter.OP_EQUAL);
		Object dataObject = ExtendedSearchListBox.show(this.window_FacilityReferenceDetailDialogLink, "ScoringGroup",
				filters);
		if (dataObject instanceof String) {
			this.lovDescRefDesc.setValue("");
		} else {
			ScoringGroup details = (ScoringGroup) dataObject;
			if (details != null) {
				this.finRefId.setValue(details.getScoreGroupId());
				this.lovDescRefDesc.setValue(details.getScoreGroupCode() + "-" + details.getScoreGroupName());
				getFacilityReferenceDetail().setLovDescCodelov(details.getScoreGroupCode());
				getFacilityReferenceDetail().setLovDescNamelov(details.getScoreGroupName());
			}
		}
		logger.debug("Leaving");
	}

	public void onClick$btnSearchAccounting(Event event) {
		logger.debug("Entering");
		Filter[] filter = new Filter[1];
		filter[0] = new Filter("EventCode", AccountingEvent.STAGE, Filter.OP_EQUAL);
		Object dataObject = ExtendedSearchListBox.show(this.window_FacilityReferenceDetailDialogLink, "AccountingSet",
				filter);
		if (dataObject instanceof String) {
			this.lovDescRefDesc.setValue("");
		} else {
			AccountingSet details = (AccountingSet) dataObject;
			if (details != null) {
				this.finRefId.setValue(details.getAccountSetid());
				this.lovDescRefDesc.setValue(details.getEventCode() + "-" + details.getAccountSetCodeName());
				getFacilityReferenceDetail().setLovDescNamelov(details.getEventCode());
				getFacilityReferenceDetail().setLovDescRefDesc(details.getAccountSetCodeName());
			}
		}
		logger.debug("Leaving");
	}

	public void onClick$btnSearchTemplate(Event event) {
		logger.debug("Entering");
		Filter[] filters = new Filter[2];
		filters[0] = new Filter("TemplateFor", NotificationConstants.TEMPLATE_FOR_CN, Filter.OP_EQUAL);
		filters[1] = new Filter("Module", NotificationConstants.MAIL_MODULE_CAF, Filter.OP_EQUAL);
		Object dataObject = ExtendedSearchListBox.show(this.window_FacilityReferenceDetailDialogLink, "MailTemplate",
				filters);
		if (dataObject instanceof String) {
			this.lovDescRefDesc.setValue("");
		} else {
			MailTemplate details = (MailTemplate) dataObject;
			if (details != null) {
				this.finRefId.setValue(details.getTemplateId());
				this.lovDescRefDesc.setValue(details.getTemplateCode() + "-" + details.getTemplateDesc());
				getFacilityReferenceDetail().setLovDescNamelov(details.getTemplateDesc());
				getFacilityReferenceDetail().setLovDescRefDesc(details.getTemplateDesc());
			}
		}
		logger.debug("Leaving");
	}

	public void onClick$btnSearchElgRule(Event event) {
		logger.debug("Entering");
		// RuleModule
		Filter[] filter = new Filter[1];
		filter[0] = new Filter("RuleModule", RuleConstants.MODULE_ELGRULE, Filter.OP_EQUAL);
		Object dataObject = ExtendedSearchListBox.show(this.window_FacilityReferenceDetailDialogLink, "Rule", filter);
		if (dataObject instanceof String) {
			this.lovDescRefDesc.setValue("");
		} else {
			Rule details = (Rule) dataObject;
			if (details != null) {
				this.finRefId.setValue(details.getRuleId());
				this.lovDescRefDesc.setValue(details.getRuleCode() + "-" + details.getRuleCodeDesc());
				getFacilityReferenceDetail().setLovDescCodelov(details.getRuleCode());
				getFacilityReferenceDetail().setLovDescNamelov(details.getRuleCode());

			}
		}
		logger.debug("Leaving");
	}

	public void fillListBox(Listbox listbox, String roleCodes, Map<String, String> checkedlist, int type) {
		logger.debug("Entering");
		listbox.getItems().clear();
		String[] roles = roleCodes.split(";");
		for (int i = 0; i < roles.length; i++) {
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
		logger.debug("Leaving");

	}

	public final class onCheckBoxCheked implements EventListener<Event> {

		public onCheckBoxCheked() {
		    super();
		}

		public void onEvent(Event event) {
			logger.debug("onEvent()");
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

			logger.debug("Leaving onEvent()");
		}

	}

	// ============Design the zul file===========//
	private void doDesignByType(FacilityReferenceDetail finRefDetail) {
		logger.debug("Entering");
		switch (finRefDetail.getFinRefType()) {

		case FinanceConstants.PROCEDT_CHECKLIST:

			// For validations
			this.showInStage.setReadonly(false);
			this.allowInputInStage.setReadonly(false);
			this.mandInputInStage.setReadonly(false);
			this.overRide.setDisabled(false);
			this.overRideValue.setReadonly(false);
			this.rowOverRide.setVisible(true);
			this.overRideValue.setLeft(Labels.getLabel("label_FacilityReferenceDetailDialog_OverRideValue.value"));

			// error labels
			this.showInStage.setLeft(Labels.getLabel("label_FinRefDialogLink_ShowInStage.value"));
			this.allowInputInStage.setLeft(Labels.getLabel("label_FinReferDialogLink_AllowInputInStage.value"));
			this.mandInputInStage.setLeft(Labels.getLabel("label_FinRefDialogLink_MandInputInStage.value"));

			// LOV List
			this.btnSearchQuestionId.setVisible(true);// show

			// LOV Label
			this.label_FacilityReferenceDetailDialog_FinRefId
					.setValue(Labels.getLabel("label_FinRefDialogLink_Question.value"));

			// ROWS WITH LIST Boxes
			this.rowDoubleListbox.setVisible(true);// Show
			this.rowSingleListbox.setVisible(true);// Show

			// labels of list boxes
			this.label_FacilityReferenceDetailDialog_ShowInStage
					.setValue(Labels.getLabel("label_FinRefDialogLink_ShowInStage.value"));
			this.label_FacilityReferenceDetailDialog_AllowInputInStage
					.setValue(Labels.getLabel("label_FinReferDialogLink_AllowInputInStage.value"));
			this.label_FacilityReferenceDetailDialog_MandInputInStage
					.setValue(Labels.getLabel("label_FinRefDialogLink_MandInputInStage.value"));

			// List headers of list boxes
			this.listheadShowInStage.setLabel(Labels.getLabel("label_FinRefDialogLink_ShowInStage.value"));
			this.listheadAllowInputInStage
					.setLabel(Labels.getLabel("label_FinReferDialogLink_AllowInputInStage.value"));
			this.listheadMandInputInStage.setLabel(Labels.getLabel("label_FinRefDialogLink_MandInputInStage.value"));

			doEnableByChecked(this.listboxallowInputInStage);
			doEnableByChecked(this.listboxmandInputInStage);
			this.label_FacilityReferenceDetailDialogLink
					.setValue(Labels.getLabel("label_Window_FacilityCheckListList.title"));
			break;

		case FinanceConstants.PROCEDT_AGREEMENT:

			// For validations
			this.showInStage.setReadonly(false);
			this.allowInputInStage.setReadonly(false);
			this.mandInputInStage.setReadonly(true);// not required

			// error labels
			this.showInStage.setLeft(Labels.getLabel("label_FinRefDialogLink_MandInputInStage.value"));
			this.allowInputInStage.setLeft(Labels.getLabel("label_FinRefDialogLink_ShowInStage.value"));
			this.mandInputInStage.setLeft("");

			// LOV List
			this.btnSearchAggCode.setVisible(true);// show

			// LOV Label
			this.label_FacilityReferenceDetailDialog_FinRefId
					.setValue(Labels.getLabel("label_FinRefDialogLink_Agreement.value"));

			// ROWS WITH LIST Boxes
			this.rowDoubleListbox.setVisible(true);// Show

			// labels of list boxes
			this.label_FacilityReferenceDetailDialog_ShowInStage
					.setValue(Labels.getLabel("label_FinRefDialogLink_ShowInStage.value"));
			this.label_FacilityReferenceDetailDialog_AllowInputInStage
					.setValue(Labels.getLabel("label_FinRefDialogLink_ReGenerateInStage.value"));
			this.label_FacilityReferenceDetailDialog_MandInputInStage.setValue("");// not required

			// List headers of list boxes
			this.listheadShowInStage.setLabel(Labels.getLabel("label_FinRefDialogLink_ShowInStage.value"));
			this.listheadAllowInputInStage.setLabel(Labels.getLabel("label_FinRefDialogLink_ReGenerateInStage.value"));
			this.listheadMandInputInStage.setLabel("");// not required

			doEnableByChecked(this.listboxallowInputInStage);
			this.label_FacilityReferenceDetailDialogLink
					.setValue(Labels.getLabel("label_Window_FacilityAgreementList.title"));
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
			this.rowOverRide.setVisible(true);
			this.overRideValue.setLeft(Labels.getLabel("label_FacilityReferenceDetailDialog_OverRideValue.value"));

			// error labels
			// error labels
			this.showInStage.setLeft(Labels.getLabel("label_FinRefDialogLink_MandInputInStage.value"));
			this.allowInputInStage.setLeft(Labels.getLabel("label_FinRefDialogLink_ShowInStage.value"));
			this.mandInputInStage.setLeft("");

			// LOV List
			this.btnSearchElgRule.setVisible(true);// show

			// LOV Label
			this.label_FacilityReferenceDetailDialog_FinRefId
					.setValue(Labels.getLabel("label_FinReferDialogLink_Eligibility.value"));

			// ROWS WITH LIST Boxes
			this.rowDoubleListbox.setVisible(true);// Show

			// labels of list boxes
			this.label_FacilityReferenceDetailDialog_ShowInStage
					.setValue(Labels.getLabel("label_FinRefDialogLink_ShowInStage.value"));
			this.label_FacilityReferenceDetailDialog_AllowInputInStage
					.setValue(Labels.getLabel("label_FinRefDialogLink_ReGenerateInStage.value"));
			this.label_FacilityReferenceDetailDialog_MandInputInStage.setValue("");// not required

			// List headers of list boxes
			this.listheadShowInStage.setLabel(Labels.getLabel("label_FinRefDialogLink_ShowInStage.value"));
			this.listheadAllowInputInStage.setLabel(Labels.getLabel("label_FinRefDialogLink_ReGenerateInStage.value"));
			this.listheadMandInputInStage.setLabel("");// not required

			doEnableByChecked(this.listboxallowInputInStage);
			this.label_FacilityReferenceDetailDialogLink
					.setValue(Labels.getLabel("label_Window_FacilityAgreementList.title"));
			break;

		case FinanceConstants.PROCEDT_RTLSCORE:

			// For validations
			this.showInStage.setReadonly(true);// not required
			this.allowInputInStage.setReadonly(true);// not required
			this.mandInputInStage.setReadonly(false);

			// error labels
			this.showInStage.setLeft("");
			this.allowInputInStage.setLeft("");
			this.mandInputInStage.setLeft(Labels.getLabel("label_FinRefDialogLink_ExecuteInStage.value"));

			// LOV List
			this.btnSearchScoringGroup.setVisible(true);// show

			// LOV Label
			this.label_FacilityReferenceDetailDialog_FinRefId
					.setValue(Labels.getLabel("label_FinReferDialogLink_ScoringGroup.value"));

			// ROWS WITH LIST Boxes
			this.rowSingleListbox.setVisible(true);// Show

			// labels of list boxes
			this.label_FacilityReferenceDetailDialog_ShowInStage.setValue("");// not required
			this.label_FacilityReferenceDetailDialog_AllowInputInStage.setValue("");// not required
			this.label_FacilityReferenceDetailDialog_MandInputInStage
					.setValue(Labels.getLabel("label_FinRefDialogLink_ExecuteInStage.value"));

			// List headers of list boxes
			this.listheadShowInStage.setLabel("");// not required
			this.listheadAllowInputInStage.setLabel("");// not required
			this.listheadMandInputInStage.setLabel(Labels.getLabel("label_FinRefDialogLink_ExecuteInStage.value"));
			doEnableByChecked(this.listboxmandInputInStage);
			this.label_FacilityReferenceDetailDialogLink
					.setValue(Labels.getLabel("label_Window_FacilityScoringList.title"));
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
			this.label_FacilityReferenceDetailDialog_FinRefId
					.setValue(Labels.getLabel("label_FinReferDialogLink_ScoringGroup.value"));

			// ROWS WITH LIST Boxes
			this.rowSingleListbox.setVisible(true);// Show

			// labels of list boxes
			this.label_FacilityReferenceDetailDialog_ShowInStage.setValue("");// not required
			this.label_FacilityReferenceDetailDialog_AllowInputInStage.setValue("");// not required
			this.label_FacilityReferenceDetailDialog_MandInputInStage
					.setValue(Labels.getLabel("label_FinRefDialogLink_ExecuteInStage.value"));

			// List headers of list boxes
			this.listheadShowInStage.setLabel("");// not required
			this.listheadAllowInputInStage.setLabel("");// not required
			this.listheadMandInputInStage.setLabel(Labels.getLabel("label_FinRefDialogLink_ExecuteInStage.value"));
			doEnableByChecked(this.listboxmandInputInStage);
			this.label_FacilityReferenceDetailDialogLink
					.setValue(Labels.getLabel("label_Window_FacilityCorpScoringList.title"));
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
			this.label_FacilityReferenceDetailDialog_FinRefId
					.setValue(Labels.getLabel("label_FinRefDialogLink_Accounting.value"));

			// ROWS WITH LIST Boxes
			this.rowSingleListbox.setVisible(true);// Show

			// labels of list boxes
			this.label_FacilityReferenceDetailDialog_ShowInStage.setValue("");// not required
			this.label_FacilityReferenceDetailDialog_AllowInputInStage.setValue("");// not required
			this.label_FacilityReferenceDetailDialog_MandInputInStage
					.setValue(Labels.getLabel("label_FinRefDialogLink_ExecuteInStage.value"));

			// List headers of list boxes
			this.listheadShowInStage.setLabel("");// not required
			this.listheadAllowInputInStage.setLabel("");// not required
			this.listheadMandInputInStage.setLabel(Labels.getLabel("label_FinRefDialogLink_ExecuteInStage.value"));

			doEnableByChecked(this.listboxmandInputInStage);
			this.label_FacilityReferenceDetailDialogLink
					.setValue(Labels.getLabel("label_Window_FacilityAccountingList.title"));
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
			this.label_FacilityReferenceDetailDialog_FinRefId
					.setValue(Labels.getLabel("label_FinRefDialogLink_Template.value"));

			// ROWS WITH LIST Boxes
			this.rowSingleListbox.setVisible(true);// Show

			// labels of list boxes
			this.label_FacilityReferenceDetailDialog_ShowInStage.setValue("");// not required
			this.label_FacilityReferenceDetailDialog_AllowInputInStage.setValue("");// not required
			this.label_FacilityReferenceDetailDialog_MandInputInStage
					.setValue(Labels.getLabel("label_FinRefDialogLink_ExecuteInStage.value"));

			// List headers of list boxes
			this.listheadShowInStage.setLabel("");// not required
			this.listheadAllowInputInStage.setLabel("");// not required
			this.listheadMandInputInStage.setLabel(Labels.getLabel("label_FinRefDialogLink_ExecuteInStage.value"));

			doEnableByChecked(this.listboxmandInputInStage);
			this.label_FacilityReferenceDetailDialogLink
					.setValue(Labels.getLabel("label_Window_FacilityMailTemplateList.title"));
			CheckOverride();
			break;

		default:
			break;
		}
		logger.debug("Leaving");
	}

	// =====ADD or Update========//
	private void processFinRefDetails(FacilityReferenceDetail facilityReferenceDetail) throws InterruptedException {
		logger.debug("Entering");
		if (facilityReferenceDetail.getRecordType() != null) {
			if (facilityReferenceDetail.getRecordType().equals(PennantConstants.RCD_ADD)
					|| facilityReferenceDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)
					|| facilityReferenceDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_UPD)) {
				if ("Save".equals(facilityReferenceDetail.getUserAction())) {
					facilityReferenceDetail.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				}
			} else {
				if (facilityReferenceDetail.getRecordType().equals(PennantConstants.RCD_DEL)) {
					facilityReferenceDetail.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				}
			}
		} else {
			facilityReferenceDetail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
			facilityReferenceDetail.setNewRecord(true);
		}
		switch (facilityReferenceDetail.getFinRefType()) {
		case FinanceConstants.PROCEDT_CHECKLIST:
			processAddOrUpdate(facilityReferenceDetail, getFacilityReferenceDetailDialogCtrl().listBoxFinanceCheckList);
			break;
		case FinanceConstants.PROCEDT_AGREEMENT:
			processAddOrUpdate(facilityReferenceDetail,
					getFacilityReferenceDetailDialogCtrl().listboxFinanceAgreementLink);
			break;
		case FinanceConstants.PROCEDT_ELIGIBILITY:
			processAddOrUpdate(facilityReferenceDetail, getFacilityReferenceDetailDialogCtrl().listBoxEligibilityRules);
			break;
		case FinanceConstants.PROCEDT_RTLSCORE:
			processAddOrUpdate(facilityReferenceDetail, getFacilityReferenceDetailDialogCtrl().listBoxScoringGroup);
			break;
		case FinanceConstants.PROCEDT_CORPSCORE:
			processAddOrUpdate(facilityReferenceDetail, getFacilityReferenceDetailDialogCtrl().listBoxCorpScoringGroup);
			break;
		case FinanceConstants.PROCEDT_STAGEACC:
			processAddOrUpdate(facilityReferenceDetail, getFacilityReferenceDetailDialogCtrl().listBoxAccounts);
			break;
		case FinanceConstants.PROCEDT_TEMPLATE:
			processAddOrUpdate(facilityReferenceDetail, getFacilityReferenceDetailDialogCtrl().listBoxTemplates);
			break;
		default:
			break;
		}
		logger.debug("Leaving");
	}

	public void processAddOrUpdate(FacilityReferenceDetail newFinrefDet, Listbox listbox) throws InterruptedException {
		logger.debug("Entering");
		boolean contains = false;
		List<Listitem> avlFinRef = listbox.getItems();
		for (int i = 0; i < avlFinRef.size(); i++) {
			FacilityReferenceDetail finRefDet = (FacilityReferenceDetail) avlFinRef.get(i).getAttribute("data");
			if (finRefDet.getFinRefId() == newFinrefDet.getFinRefId()) {
				if (newFinrefDet.getRecordType().equals(PennantConstants.RECORD_TYPE_UPD)) {
					listbox.removeItemAt(avlFinRef.get(i).getIndex());
				} else if (finRefDet.getRecordType().equals(PennantConstants.RCD_DEL)) {
					listbox.removeItemAt(avlFinRef.get(i).getIndex());
					newFinrefDet.setRecordType(PennantConstants.RECORD_TYPE_NEW);
					newFinrefDet.setNewRecord(false);
					newFinrefDet.setFinRefDetailId(finRefDet.getFinRefDetailId());
					newFinrefDet.setVersion(finRefDet.getVersion());

					FacilityReferenceDetail befImage = new FacilityReferenceDetail();
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
			List<FacilityReferenceDetail> finRefDetailList = new ArrayList<FacilityReferenceDetail>();
			finRefDetailList.add(newFinrefDet);
			getFacilityReferenceDetailDialogCtrl().dofillListbox(finRefDetailList, listbox);
			this.window_FacilityReferenceDetailDialogLink.onClose();
			getFacilityReferenceDetailDialogCtrl().window_FacilityReferenceDetailDialog.setVisible(true);
		}

		if ("listBoxCorpScoringGroup".equals(listbox.getId()) && listbox.getVisibleItemCount() == 1) {
			getFacilityReferenceDetailDialogCtrl().btnNew_FinCorpScoringGroup.setVisible(false);
		}
		logger.debug("Leaving");
	}

	// ====== Delete ===============//
	private void deleteFinRrefDetails(FacilityReferenceDetail finRefDetail) {
		logger.debug("Entering");
		switch (finRefDetail.getFinRefType()) {
		case FinanceConstants.PROCEDT_CHECKLIST:
			processDelet(finRefDetail, getFacilityReferenceDetailDialogCtrl().listBoxFinanceCheckList);
			break;
		case FinanceConstants.PROCEDT_AGREEMENT:
			processDelet(finRefDetail, getFacilityReferenceDetailDialogCtrl().listboxFinanceAgreementLink);
			break;
		case FinanceConstants.PROCEDT_ELIGIBILITY:
			processDelet(finRefDetail, getFacilityReferenceDetailDialogCtrl().listBoxEligibilityRules);
			break;
		case FinanceConstants.PROCEDT_RTLSCORE:
			processDelet(finRefDetail, getFacilityReferenceDetailDialogCtrl().listBoxScoringGroup);
			break;
		case FinanceConstants.PROCEDT_CORPSCORE:
			processDelet(finRefDetail, getFacilityReferenceDetailDialogCtrl().listBoxCorpScoringGroup);
			break;
		case FinanceConstants.PROCEDT_STAGEACC:
			processDelet(finRefDetail, getFacilityReferenceDetailDialogCtrl().listBoxAccounts);
			break;
		case FinanceConstants.PROCEDT_TEMPLATE:
			processDelet(finRefDetail, getFacilityReferenceDetailDialogCtrl().listBoxTemplates);
			break;
		default:
			break;
		}
		logger.debug("Leaving");

	}

	public void processDelet(FacilityReferenceDetail newFinrefDet, Listbox listbox) {
		logger.debug("Entering");
		List<Listitem> avlFinRef = listbox.getItems();
		for (int i = 0; i < avlFinRef.size(); i++) {
			FacilityReferenceDetail finRefDet = (FacilityReferenceDetail) avlFinRef.get(i).getAttribute("data");
			if (finRefDet.getFinRefId() == newFinrefDet.getFinRefId()) {
				if (finRefDet.getRecordStatus().equals(PennantConstants.RCD_STATUS_APPROVED)
						|| (finRefDet.getRecordType().equals(PennantConstants.RECORD_TYPE_UPD)
								&& !finRefDet.isNewRecord())) {
					listbox.removeItemAt(avlFinRef.get(i).getIndex());
					List<FacilityReferenceDetail> finRefDetailList = new ArrayList<FacilityReferenceDetail>();
					finRefDetailList.add(newFinrefDet);
					getFacilityReferenceDetailDialogCtrl().dofillListbox(finRefDetailList, listbox);
					if ("listBoxCorpScoringGroup".equals(listbox.getId()) && listbox.getVisibleItemCount() == 1) {
						getFacilityReferenceDetailDialogCtrl().btnNew_FinCorpScoringGroup.setVisible(false);
					} else {
						getFacilityReferenceDetailDialogCtrl().btnNew_FinCorpScoringGroup.setVisible(true);
					}
				} else {
					listbox.removeItemAt(avlFinRef.get(i).getIndex());
					if ("listBoxCorpScoringGroup".equals(listbox.getId()) && listbox.getItemCount() == 0) {
						getFacilityReferenceDetailDialogCtrl().btnNew_FinCorpScoringGroup.setVisible(true);
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
		} else {
			this.overRideValue.setValue(0);
			this.overRideValue.setReadonly(true);
		}
	}

}
