/**
 * 
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
 * * FileName : FinanceTypeDialogCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 30-06-2011 * *
 * Modified Date : 30-06-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 30-06-2011 Pennant 0.1 * * 29-04-2018 Raju/Vinay 0.2 To avoid Postgres issue also as it * is primary key no need to
 * check * for null * * * * * * *
 ********************************************************************************************
 */
package com.pennant.pff.noc.webui;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Space;
import org.zkoss.zul.Window;

import com.pennant.ExtendedCombobox;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.applicationmaster.AgreementDefinition;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.mail.MailTemplate;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.pff.letter.LetterUtil;
import com.pennant.pff.noc.model.LoanTypeLetterMapping;
import com.pennant.pff.noc.service.LoanTypeLetterMappingService;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.SpringBeanUtil;
import com.pennanttech.pennapps.jdbc.search.Filter;
import com.pennanttech.pennapps.jdbc.search.Search;
import com.pennanttech.pennapps.jdbc.search.SearchProcessor;
import com.pennanttech.pennapps.web.util.MessageUtil;

public class LoanTypeLetterMappingDialogCtrl extends GFCBaseCtrl<LoanTypeLetterMapping> {
	private static final long serialVersionUID = 4493449538614654801L;
	private static final Logger logger = LogManager.getLogger(LoanTypeLetterMappingDialogCtrl.class);

	protected Window windowLoanTypeLetterMappingDialog;
	protected ExtendedCombobox finType;
	protected Listbox listBoxLoanTypeLetterMapping;
	protected Button btnNewLoanTypeLetterMapping;

	private transient LoanTypeLetterMappingListCtrl loanTypeLetterMappingListCtrl;
	private transient LoanTypeLetterMappingService loanTypeLetterMappingService;

	private LoanTypeLetterMapping loanTypeLetterMapping;
	private boolean isLetterMappingNew = true;
	private String filteremail = "";

	private final List<ValueLabel> letterTypeList = LetterUtil.getLetterTypes();
	private final List<ValueLabel> letterModeList = LetterUtil.getLetterModes();
	private List<LoanTypeLetterMapping> deleteletterMappingList = new ArrayList<>();

	private enum ListFields {
		LETTER_TYPE(1),

		AUTO_GENERATION(2),

		MODE(3),

		EMAIL_TEMPLATE(4),

		AGREEMENT_CODE(5);

		private int index;

		private ListFields(int index) {
			this.index = index;
		}

		public int index() {
			return index;
		}

		public static ListFields getField(String field) {
			List<ListFields> list = Arrays.asList(ListFields.values());

			for (ListFields it : list) {
				if (it.name().equals(field)) {
					return it;
				}
			}

			return null;
		}
	}

	public LoanTypeLetterMappingDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "LoanTypeLetterMappingDialog";
	}

	public void onCreate$windowLoanTypeLetterMappingDialog(Event event) throws Exception {
		logger.debug(Literal.ENTERING);

		setPageComponents(windowLoanTypeLetterMappingDialog);

		try {
			this.loanTypeLetterMapping = (LoanTypeLetterMapping) arguments.get("loanTypeLetterMapping");
			this.loanTypeLetterMappingListCtrl = (LoanTypeLetterMappingListCtrl) arguments
					.get("loanTypeLetterMappingListCtrl");

			if (this.loanTypeLetterMapping == null) {
				throw new Exception(Labels.getLabel("error.unhandled"));
			}

			LoanTypeLetterMapping letterMapping = new LoanTypeLetterMapping();
			BeanUtils.copyProperties(this.loanTypeLetterMapping, letterMapping);
			this.loanTypeLetterMapping.setBefImage(letterMapping);

			doLoadWorkFlow(this.loanTypeLetterMapping.isWorkflow(), this.loanTypeLetterMapping.getWorkflowId(),
					this.loanTypeLetterMapping.getNextTaskId());

			if (isWorkFlowEnabled()) {
				if (!enqiryModule) {
					this.userAction = setListRecordStatus(this.userAction);
				}
				getUserWorkspace().allocateAuthorities(this.pageRightName, getRole());
			} else {
				getUserWorkspace().allocateAuthorities(this.pageRightName, null);
			}

			doSetFieldProperties();
			doCheckRights();
			doShowDialog(this.loanTypeLetterMapping);
		} catch (Exception e) {
			closeDialog();
			MessageUtil.showError(e);
		}

		logger.debug(Literal.LEAVING);
	}

	private void doSetFieldProperties() {
		logger.debug(Literal.ENTERING);

		this.finType.setMaxlength(8);
		this.finType.setMandatoryStyle(true);

		logger.debug(Literal.LEAVING);
	}

	private void doCheckRights() {
		logger.debug(Literal.ENTERING);

		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_LoanTypeLetterMappingDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_LoanTypeLetterMappingDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_LoanTypeLetterMappingDialog_btnDelete"));
		this.btnSave.setVisible(true);
		this.btnCancel.setVisible(false);
		this.btnNewLoanTypeLetterMapping
				.setVisible(getUserWorkspace().isAllowed("button_LoanTypeLetterMappingDialog_NewLetterMapping"));

		logger.debug(Literal.LEAVING);
	}

	public void onClick$btnSave(Event event) {
		logger.debug(Literal.ENTERING);
		doSave();
		logger.debug(Literal.LEAVING);
	}

	public void onClick$btnEdit(Event event) {
		logger.debug(Literal.ENTERING);
		doEdit();
		logger.debug(Literal.LEAVING);
	}

	public void onClick$btnHelp(Event event) {
		logger.debug(Literal.ENTERING);
		MessageUtil.showHelpWindow(event, super.window);
		logger.debug(Literal.LEAVING);
	}

	public void onClick$btnDelete(Event event) {
		logger.debug(Literal.ENTERING);
		doDelete();
		logger.debug(Literal.LEAVING);
	}

	public void onClick$btnCancel(Event event) {
		logger.debug(Literal.ENTERING);
		doCancel();
		logger.debug(Literal.LEAVING);
	}

	public void onClick$btnClose(Event event) {
		logger.debug(Literal.ENTERING);
		doClose(this.btnSave.isVisible());
		logger.debug(Literal.LEAVING);
	}

	public void onClick$btnNotes(Event event) {
		logger.debug(Literal.ENTERING);
		doShowNotes(this.loanTypeLetterMapping);
		logger.debug(Literal.LEAVING);
	}

	public void onClick$btnNewLoanTypeLetterMapping(Event event) throws Exception {
		LoanTypeLetterMapping ltlm = new LoanTypeLetterMapping();
		isLetterMappingNew = true;

		ltlm.setNewRecord(isLetterMappingNew);

		if (StringUtils.isBlank(ltlm.getRecordType())) {
			ltlm.setVersion(1);
			ltlm.setRecordType(PennantConstants.RECORD_TYPE_NEW);
		}

		int keyValue = 0;
		for (Listitem component : listBoxLoanTypeLetterMapping.getItems()) {
			LoanTypeLetterMapping lm = (LoanTypeLetterMapping) component.getAttribute("data");

			if (lm != null && lm.getKeyValue() > keyValue) {
				keyValue = lm.getKeyValue();
			}
		}

		ltlm.setKeyValue(keyValue + 1);
		renderItem(ltlm);
		logger.debug(Literal.LEAVING);
	}

	public void onChangeLetterType(ForwardEvent event) {
		Listitem listitem = (Listitem) event.getData();

		Combobox letterType = (Combobox) (getComponent(listitem, ListFields.LETTER_TYPE.index())).getLastChild();
		this.filteremail = letterType.getSelectedItem().getValue();

		ExtendedCombobox email = (ExtendedCombobox) (getComponent(listitem, ListFields.EMAIL_TEMPLATE.index()))
				.getLastChild();
		email.setValue(null);
		email.setDescription(null);

		if (StringUtils.isNotEmpty(this.filteremail)) {
			Filter[] codeFilter = new Filter[1];
			codeFilter[0] = Filter.in("Event", filteremail);
			email.setFilters(codeFilter);
		}
	}

	public void onClickLetterMappingButtonDelete(ForwardEvent event) {
		logger.debug(Literal.ENTERING);

		Listitem item = (Listitem) event.getData();
		LoanTypeLetterMapping letterMapping = (LoanTypeLetterMapping) item.getAttribute("data");

		if (PennantConstants.RECORD_TYPE_NEW.equals(letterMapping.getRecordType())) {
			letterMapping.setRecordType(PennantConstants.RECORD_TYPE_CAN);
		} else {
			letterMapping.setRecordType(PennantConstants.RECORD_TYPE_DEL);
			letterMapping.setNewRecord(true);
		}

		deleteletterMappingList.add(letterMapping);

		listBoxLoanTypeLetterMapping.removeChild(item);

		logger.debug(Literal.LEAVING);
	}

	private Component getComponent(Listitem row, int index) {
		int i = 1;
		for (Component component : row.getChildren()) {
			if (i == index) {
				return component.getFirstChild();
			}
			i++;
		}

		return null;
	}

	private void doSetValidation(Listitem listItem, int index) {
		Hbox hbox = (Hbox) getComponent(listItem, index);

		if (hbox == null) {
			logger.error("Invalid Index");
			return;
		}

		String id = StringUtils.trimToNull(hbox.getLastChild().getId());
		if (id == null) {
			return;
		}

		ListFields type = ListFields.getField(id.split("\\-")[0]);

		switch (type) {
		case LETTER_TYPE:
			Combobox letterType = (Combobox) hbox.getLastChild();
			letterType.clearErrorMessage();
			String letterTypeVal = letterType.getValue();
			if (StringUtils.isEmpty(letterTypeVal) || PennantConstants.SELECT_LABEL.equals(letterTypeVal)) {
				throw new WrongValueException(letterType,
						Labels.getLabel("FIELD_IS_MAND", new String[] { "Letter Type " }));
			}
			break;

		case AUTO_GENERATION:
			Checkbox autoGeneration = (Checkbox) hbox.getLastChild();
			Clients.clearWrongValue(autoGeneration);
			break;
		case MODE:
			Combobox mode = (Combobox) hbox.getLastChild();
			mode.clearErrorMessage();
			String modeVal = mode.getValue();
			if (PennantConstants.SELECT_LABEL.equals(modeVal)) {
				throw new WrongValueException(mode, Labels.getLabel("FIELD_IS_MAND", new String[] { "Mode " }));
			}
			break;

		case EMAIL_TEMPLATE:
			ExtendedCombobox emailTemplate = (ExtendedCombobox) hbox.getLastChild();
			emailTemplate.clearErrorMessage();
			String emailTemplateVal = emailTemplate.getValue();

			if (StringUtils.isEmpty(emailTemplateVal)) {
				throw new WrongValueException(emailTemplate,
						Labels.getLabel("FIELD_IS_MAND", new String[] { "Email Template " }));
			}
			break;
		case AGREEMENT_CODE:
			ExtendedCombobox agreementCode = (ExtendedCombobox) hbox.getLastChild();
			agreementCode.clearErrorMessage();
			String agreementCodeVal = agreementCode.getValue();

			if (StringUtils.isEmpty(agreementCodeVal)) {
				throw new WrongValueException(agreementCode,
						Labels.getLabel("FIELD_IS_MAND", new String[] { "Agreement Code " }));
			}
			break;
		default:
			break;
		}
	}

	protected void refreshList() {
		logger.debug(Literal.ENTERING);
		loanTypeLetterMappingListCtrl.fillListData();
		logger.debug(Literal.LEAVING);
	}

	private void doCancel() {
		logger.debug(Literal.ENTERING);

		doWriteBeanToComponents(this.loanTypeLetterMapping.getBefImage());
		doReadOnly();
		this.btnCtrl.setInitEdit();
		this.btnCancel.setVisible(false);

		logger.debug(Literal.LEAVING);
	}

	public void doWriteBeanToComponents(LoanTypeLetterMapping letterMapping) {
		logger.debug(Literal.ENTERING);

		this.finType.setValue(letterMapping.getFinType(), letterMapping.getFinTypeDesc());
		this.recordStatus.setValue(letterMapping.getRecordStatus());

		logger.debug(Literal.LEAVING);
	}

	public void doWriteComponentsToBean(LoanTypeLetterMapping ltlm) {
		logger.debug(Literal.ENTERING);

		List<LoanTypeLetterMapping> ltlmList = new ArrayList<>();
		List<String> letterTypes = new ArrayList<>();
		List<WrongValueException> wve = new ArrayList<>();

		for (Listitem component : listBoxLoanTypeLetterMapping.getItems()) {
			Listitem listitem = (Listitem) component;
			LoanTypeLetterMapping letterMapping = (LoanTypeLetterMapping) listitem.getAttribute("data");

			try {
				doSetValidation(listitem, ListFields.LETTER_TYPE.index());
				Combobox letterType = (Combobox) (getComponent(listitem, ListFields.LETTER_TYPE.index()))
						.getLastChild();

				if (letterTypes.contains(letterType.getValue())
						&& !letterMapping.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
					MessageUtil.showError(
							"Duplicate letterType: " + letterType.getValue() + " are not allowed for same loan type ");
					wve.add(new WrongValueException(letterType.getValue()));
				}
				letterTypes.add(letterType.getValue());
				letterMapping.setLetterType(letterType.getValue());

			} catch (WrongValueException we) {
				wve.add(we);
			}

			try {
				doSetValidation(listitem, ListFields.AUTO_GENERATION.index());
				Checkbox autoGeneration = (Checkbox) (getComponent(listitem, ListFields.AUTO_GENERATION.index()))
						.getLastChild();

				letterMapping.setAutoGeneration(autoGeneration.isChecked());
			} catch (WrongValueException we) {
				wve.add(we);
			}

			try {
				doSetValidation(listitem, ListFields.MODE.index());
				Combobox mode = (Combobox) (getComponent(listitem, ListFields.MODE.index())).getLastChild();

				letterMapping.setLetterMode(mode.getValue());

			} catch (WrongValueException we) {
				wve.add(we);
			}

			try {
				doSetValidation(listitem, ListFields.EMAIL_TEMPLATE.index());
				ExtendedCombobox emailTemplate = (ExtendedCombobox) (getComponent(listitem,
						ListFields.EMAIL_TEMPLATE.index())).getLastChild();

				letterMapping.setEmailTemplateId(((MailTemplate) emailTemplate.getObject()).getId());
			} catch (WrongValueException we) {
				wve.add(we);
			}

			try {
				doSetValidation(listitem, ListFields.AGREEMENT_CODE.index());
				ExtendedCombobox agreementCode = (ExtendedCombobox) (getComponent(listitem,
						ListFields.AGREEMENT_CODE.index())).getLastChild();
				letterMapping.setAgreementCodeId(((AgreementDefinition) agreementCode.getObject()).getId());
			} catch (WrongValueException we) {
				wve.add(we);
			}

			boolean isNew = letterMapping.isNewRecord();
			if (isWorkFlowEnabled()) {
				if (StringUtils.isBlank(letterMapping.getRecordType())) {
					letterMapping.setVersion(letterMapping.getVersion() + 1);
					if (isNew) {
						letterMapping.setRecordType(PennantConstants.RECORD_TYPE_NEW);
					} else {
						letterMapping.setRecordType(PennantConstants.RECORD_TYPE_UPD);
						letterMapping.setNewRecord(true);
					}
				}
			} else {
				if (isLetterMappingNew) {
					if (letterMapping.isNewRecord()) {
						letterMapping.setVersion(1);
						letterMapping.setRecordType(PennantConstants.RCD_ADD);
					}

					if (StringUtils.isBlank(letterMapping.getRecordType())) {
						letterMapping.setVersion(letterMapping.getVersion() + 1);
						letterMapping.setRecordType(PennantConstants.RCD_UPD);
					}

					if (letterMapping.getRecordType().equals(PennantConstants.RCD_ADD) && letterMapping.isNewRecord()) {
					} else if (letterMapping.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
						letterMapping.setVersion(letterMapping.getVersion() + 1);
					}
				} else {
					letterMapping.setVersion(letterMapping.getVersion() + 1);
				}
			}

			letterMapping.setFinType(this.finType.getValue());
			letterMapping.setRecordStatus(this.recordStatus.getValue());

			ltlmList.add(letterMapping);
		}

		ltlmList.addAll(deleteletterMappingList);
		ltlm.setLoanTypeLetterMappingList(ltlmList);
		this.loanTypeLetterMapping.setLoanTypeLetterMappingList(ltlmList);

		doRemoveValidation();

		if (!wve.isEmpty()) {
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = (WrongValueException) wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}

		logger.debug(Literal.LEAVING);
	}

	public void doShowDialog(LoanTypeLetterMapping letterMapping) {
		logger.debug(Literal.ENTERING);

		if (letterMapping.isNewRecord()) {
			this.btnCtrl.setInitNew();
			doEdit();
		} else {

			if (isWorkFlowEnabled()) {
				if (StringUtils.isNotBlank(letterMapping.getRecordType())) {
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
			readOnlyComponent(true, this.finType);
		}

		doWriteBeanToComponents(letterMapping);

		int keyValue = 0;
		for (LoanTypeLetterMapping ltlm : letterMapping.getLoanTypeLetterMappingList()) {
			ltlm.setKeyValue(keyValue++);
			renderItem(ltlm);
		}

		setDialog(DialogType.EMBEDDED);

		logger.debug(Literal.LEAVING);
	}

	private void doSetValidation() {
		logger.debug(Literal.ENTERING);

		if (!this.finType.isReadonly()) {
			this.finType.setConstraint(
					new PTStringValidator(Labels.getLabel("label_FinTypeLetterMappingDialog_FinType.value"),
							PennantRegularExpressions.REGEX_UPP_BOX_ALPHANUM, true));
		}

		logger.debug(Literal.LEAVING);
	}

	private void doRemoveValidation() {
		logger.debug(Literal.ENTERING);

		this.finType.setConstraint("");

		logger.debug(Literal.LEAVING);
	}

	@Override
	protected void doClearMessage() {
		logger.debug(Literal.ENTERING);

		this.finType.setErrorMessage("");

		logger.debug(Literal.LEAVING);
	}

	private void doDelete() {
		logger.debug(Literal.ENTERING);

		final LoanTypeLetterMapping aMapping = new LoanTypeLetterMapping();
		BeanUtils.copyProperties(getLoanTypeLetterMapping(), aMapping);

		String tranType = PennantConstants.TRAN_WF;

		final String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record") + "\n\n --> "
				+ aMapping.getId();
		if (MessageUtil.confirm(msg) == MessageUtil.YES) {
			if (StringUtils.trimToEmpty(aMapping.getRecordType()).equals("")) {
				aMapping.setVersion(aMapping.getVersion() + 1);
				aMapping.setRecordType(PennantConstants.RECORD_TYPE_DEL);

				if (isWorkFlowEnabled()) {
					aMapping.setRecordStatus(userAction.getSelectedItem().getValue().toString());
					aMapping.setNewRecord(true);
					tranType = PennantConstants.TRAN_WF;
					getWorkFlowDetails(userAction.getSelectedItem().getLabel(), aMapping.getNextTaskId(), aMapping);
				} else {
					tranType = PennantConstants.TRAN_DEL;
				}
				for (LoanTypeLetterMapping ltlm : aMapping.getLoanTypeLetterMappingList()) {
					ltlm.setVersion(ltlm.getVersion() + 1);
					ltlm.setRecordType(PennantConstants.RECORD_TYPE_DEL);

					if (isWorkFlowEnabled()) {
						ltlm.setRecordStatus(userAction.getSelectedItem().getValue().toString());
						ltlm.setNewRecord(true);
					}
				}
			}

			try {
				if (doProcess(aMapping, tranType)) {
					refreshList();
					closeDialog();
				}
			} catch (DataAccessException e) {
				MessageUtil.showError(e);
			}
		}
		logger.debug(Literal.LEAVING);
	}

	private void doEdit() {
		logger.debug(Literal.ENTERING);

		if (this.loanTypeLetterMapping.isNewRecord()) {
			this.btnCancel.setVisible(false);
			readOnlyComponent(true, this.finType);
		} else {
			this.btnCancel.setVisible(true);
			readOnlyComponent(true, this.finType);
		}

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}
			if (this.loanTypeLetterMapping.isNewRecord()) {
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

	public void doReadOnly() {
		logger.debug(Literal.ENTERING);

		readOnlyComponent(true, this.finType);
		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(true);
			}
			this.recordStatus.setValue("");
			this.userAction.setSelectedIndex(0);
		}

		logger.debug(Literal.LEAVING);
	}

	public void doClear() {
		logger.debug(Literal.ENTERING);

		this.finType.setValue("");

		logger.debug(Literal.LEAVING);
	}

	public void doSave() {
		logger.debug(Literal.ENTERING);

		final LoanTypeLetterMapping letterMapping = new LoanTypeLetterMapping();
		BeanUtils.copyProperties(getLoanTypeLetterMapping(), letterMapping);
		boolean isNew = false;

		doSetValidation();

		if (listBoxLoanTypeLetterMapping.getItems().isEmpty()) {
			MessageUtil.showMessage("Please add atleast one Letter Mapping.");
			return;
		}

		doWriteComponentsToBean(letterMapping);

		isNew = letterMapping.isNewRecord();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(letterMapping.getRecordType())) {
				letterMapping.setVersion(letterMapping.getVersion() + 1);
				if (isNew) {
					letterMapping.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					letterMapping.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					letterMapping.setNewRecord(true);
				}
			}
		} else {
			letterMapping.setVersion(letterMapping.getVersion() + 1);
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}

		try {
			if (doProcess(letterMapping, tranType)) {
				refreshList();
				closeDialog();
			}

		} catch (final DataAccessException e) {
			logger.error(e);
			MessageUtil.showError(e);
		}
		logger.debug(Literal.LEAVING);
	}

	protected boolean doProcess(LoanTypeLetterMapping mapping, String tranType) {
		logger.debug(Literal.ENTERING);

		List<LoanTypeLetterMapping> letterMapping = mapping.getLoanTypeLetterMappingList();
		List<LoanTypeLetterMapping> tempLetterMapping = new ArrayList<>();

		for (LoanTypeLetterMapping lm : letterMapping) {
			if (lm.getRecordType() != null && PennantConstants.RECORD_TYPE_CAN.equals(lm.getRecordType())) {
				tempLetterMapping.add(lm);
			}
		}

		letterMapping.removeAll(tempLetterMapping);
		mapping.setLoanTypeLetterMappingList(letterMapping);

		boolean processCompleted = false;
		AuditHeader auditHeader = null;
		String nextRoleCode = "";

		mapping.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		mapping.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		mapping.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			String nextTaskId = "";
			mapping.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(mapping.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getNextTaskIds(taskId, mapping);
				}

				if (isNotesMandatory(taskId, mapping)) {
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

			mapping.setTaskId(taskId);
			mapping.setNextTaskId(nextTaskId);
			mapping.setRoleCode(getRole());
			mapping.setNextRoleCode(nextRoleCode);

			auditHeader = getAuditHeader(mapping, tranType);
			String operationRefs = getServiceOperations(taskId, mapping);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader, null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(mapping, PennantConstants.TRAN_WF);
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		} else {
			auditHeader = getAuditHeader(mapping, tranType);
			processCompleted = doSaveProcess(auditHeader, null);
		}

		logger.debug(Literal.LEAVING);
		return processCompleted;
	}

	private boolean doSaveProcess(AuditHeader auditHeader, String method) {
		logger.debug(Literal.ENTERING);

		boolean processCompleted = false;
		int retValue = PennantConstants.porcessOVERIDE;
		LoanTypeLetterMapping aLetterMapping = (LoanTypeLetterMapping) auditHeader.getAuditDetail().getModelData();
		boolean deleteNotes = false;

		while (retValue == PennantConstants.porcessOVERIDE) {

			if (StringUtils.isBlank(method)) {
				if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {
					auditHeader = loanTypeLetterMappingService.delete(auditHeader);
					deleteNotes = true;
				} else {
					auditHeader = loanTypeLetterMappingService.saveOrUpdate(auditHeader);
				}

			} else {
				if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
					auditHeader = loanTypeLetterMappingService.doApprove(auditHeader);

					if (aLetterMapping.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
						deleteNotes = true;
					}

				} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
					auditHeader = loanTypeLetterMappingService.doReject(auditHeader);
					if (aLetterMapping.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
						deleteNotes = true;
					}

				} else {
					auditHeader.setErrorDetails(
							new ErrorDetail(PennantConstants.ERR_9999, Labels.getLabel("InvalidWorkFlowMethod"), null));
					retValue = ErrorControl.showErrorControl(this.windowLoanTypeLetterMappingDialog, auditHeader);
					return processCompleted;
				}
			}

			auditHeader = ErrorControl.showErrorDetails(this.windowLoanTypeLetterMappingDialog, auditHeader);
			retValue = auditHeader.getProcessStatus();

			if (retValue == PennantConstants.porcessCONTINUE) {
				processCompleted = true;

				if (deleteNotes) {
					deleteNotes(getNotes(this.loanTypeLetterMapping), true);
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

	private AuditHeader getAuditHeader(LoanTypeLetterMapping mapping, String tranType) {
		AuditDetail ad = new AuditDetail(tranType, 1, mapping.getBefImage(), mapping);
		return new AuditHeader(getReference(), null, null, null, ad, mapping.getUserDetails(), getOverideMap());
	}

	private void renderItem(LoanTypeLetterMapping mapping) {
		logger.debug(Literal.ENTERING);

		boolean isReadOnly = isReadOnly("button_LoanTypeLetterMappingDialog_NewLetterMapping");

		Listitem listItem = new Listitem();
		listItem.setAttribute("data", mapping);

		Combobox letterType = appendLetterType(listItem, mapping, isReadOnly);

		appendAutoGeneration(listItem, mapping, isReadOnly);

		appendMode(listItem, mapping, isReadOnly);

		ExtendedCombobox emailTemplate = appendEmailTemplate(listItem, mapping, isReadOnly);

		ExtendedCombobox agreement = appendAgreement(listItem, mapping, isReadOnly);

		appendDelete(listItem, isReadOnly);

		applyFilters(mapping, letterType, emailTemplate, agreement);

		this.listBoxLoanTypeLetterMapping.appendChild(listItem);

		logger.debug(Literal.LEAVING);
	}

	private Combobox appendLetterType(Listitem listItem, LoanTypeLetterMapping mapping, boolean isReadOnly) {
		Combobox comboBox = new Combobox();

		fillList(comboBox, mapping.getLetterType(), letterTypeList);

		comboBox.addForward(Events.ON_CHANGE, windowLoanTypeLetterMappingDialog, "onChangeLetterType", listItem);

		comboBox.setId(ListFields.LETTER_TYPE.name().concat("-").concat(String.valueOf(mapping.getKeyValue())));

		readOnlyComponent(isReadOnly, comboBox);

		Hbox hbox = new Hbox();
		hbox.appendChild(getSpace(true));
		hbox.appendChild(comboBox);

		Listcell listCell = new Listcell();
		listCell.appendChild(hbox);

		listItem.appendChild(listCell);
		return comboBox;
	}

	private void appendAutoGeneration(Listitem listItem, LoanTypeLetterMapping mapping, boolean isReadOnly) {
		Checkbox checkBox = new Checkbox();
		checkBox.setChecked(mapping.isAutoGeneration());
		checkBox.setId(ListFields.AUTO_GENERATION.name().concat("-").concat(String.valueOf(mapping.getKeyValue())));
		readOnlyComponent(isReadOnly, checkBox);

		Hbox hbox = new Hbox();
		hbox.appendChild(getSpace(false));
		hbox.appendChild(checkBox);

		Listcell listCell = new Listcell();
		listCell.appendChild(hbox);

		listItem.appendChild(listCell);
	}

	private void appendMode(Listitem listItem, LoanTypeLetterMapping mapping, boolean isReadOnly) {
		Combobox comboBox = new Combobox();
		fillComboBox(comboBox, String.valueOf(mapping.getLetterMode()), letterModeList);

		if (mapping.getLetterMode() != null) {
			comboBox.setValue(mapping.getLetterMode());
		}

		comboBox.setId(ListFields.MODE.name().concat("-").concat(String.valueOf(mapping.getKeyValue())));
		readOnlyComponent(isReadOnly, comboBox);

		Hbox hbox = new Hbox();
		hbox.appendChild(getSpace(false));
		hbox.appendChild(comboBox);

		Listcell listCell = new Listcell();
		listCell.appendChild(hbox);

		listItem.appendChild(listCell);
	}

	private ExtendedCombobox appendEmailTemplate(Listitem listItem, LoanTypeLetterMapping mapping, boolean isReadOnly) {
		ExtendedCombobox combobox = new ExtendedCombobox();

		combobox.setModuleName("MailTemplate");
		combobox.setValueColumn("TemplateCode");
		combobox.setDescColumn("TemplateDesc");
		combobox.setValidateColumns(new String[] { "TemplateCode" });
		// combobox.setFilters(new File);

		combobox.setId(ListFields.EMAIL_TEMPLATE.name().concat("-").concat(String.valueOf(mapping.getKeyValue())));
		readOnlyComponent(isReadOnly, combobox);

		Hbox hbox = new Hbox();
		hbox.appendChild(getSpace(true));
		hbox.appendChild(combobox);

		Listcell listCell = new Listcell();
		listCell.appendChild(hbox);

		listItem.appendChild(listCell);

		return combobox;
	}

	private ExtendedCombobox appendAgreement(Listitem listItem, LoanTypeLetterMapping mapping, boolean isReadOnly) {
		ExtendedCombobox agreementCode = new ExtendedCombobox();
		agreementCode.setModuleName("AgreementDefinition");
		agreementCode.setValueColumn("AggCode");
		agreementCode.setDescColumn("AggName");
		agreementCode.setValidateColumns(new String[] { "AggCode" });
		agreementCode.setId(ListFields.AGREEMENT_CODE.name().concat("-").concat(String.valueOf(mapping.getKeyValue())));
		readOnlyComponent(isReadOnly, agreementCode);

		Hbox hbox = new Hbox();
		hbox.appendChild(getSpace(true));
		hbox.appendChild(agreementCode);

		Listcell listCell = new Listcell();
		listCell.appendChild(hbox);

		listItem.appendChild(listCell);

		return agreementCode;
	}

	private void appendDelete(Listitem listItem, boolean isReadOnly) {
		Button button = new Button();
		button.setSclass("z-toolbarbutton");
		button.setLabel(Labels.getLabel("btnDelete.label"));
		button.addForward("onClick", self, "onClickLetterMappingButtonDelete", listItem);
		readOnlyComponent(isReadOnly, button);

		Listcell listCell = new Listcell();
		listCell.appendChild(button);

		listItem.appendChild(listCell);
	}

	private void applyFilters(LoanTypeLetterMapping ltlm, Combobox letterType, ExtendedCombobox emailTemplate,
			ExtendedCombobox agreementCode) {

		Filter[] codeFilter = new Filter[1];
		codeFilter[0] = Filter.in("Event", "");
		emailTemplate.setFilters(codeFilter);

		SearchProcessor searchProcessor = (SearchProcessor) SpringBeanUtil.getBean("searchProcessor");

		if (ltlm.getEmailTemplateId() != Long.MIN_VALUE && ltlm.getEmailTemplateId() != 0) {
			Search search = new Search(MailTemplate.class);
			search.addFilterEqual("TemplateId", ltlm.getEmailTemplateId());

			MailTemplate mailTemplate = (MailTemplate) searchProcessor.getResults(search).get(0);

			if (mailTemplate != null) {
				emailTemplate.setValue(mailTemplate.getTemplateCode());
				emailTemplate.setDescription(mailTemplate.getTemplateDesc());
				emailTemplate.setObject(mailTemplate);

				String type = letterType.getSelectedItem().getValue();
				Filter[] filter = new Filter[1];
				filter[0] = Filter.in("Event", type);
				emailTemplate.setFilters(filter);
			}
		}

		if (ltlm.getAgreementCodeId() != Long.MIN_VALUE && ltlm.getAgreementCodeId() != 0) {
			Search search = new Search(AgreementDefinition.class);
			search.addFilterEqual("AggId", ltlm.getAgreementCodeId());

			AgreementDefinition agree = (AgreementDefinition) searchProcessor.getResults(search).get(0);

			if (agree != null) {
				agreementCode.setValue(agree.getAggCode());
				agreementCode.setDescription(agree.getAggDesc());
				agreementCode.setObject(agree);
			}
		}
	}

	private Space getSpace(boolean mandatory) {
		Space space = new Space();
		space.setSpacing("2px");

		if (mandatory) {
			space.setSclass(PennantConstants.mandateSclass);
		}

		return space;
	}

	@Autowired
	public void setLoanTypeLetterMappingListCtrl(LoanTypeLetterMappingListCtrl loanTypeLetterMappingListCtrl) {
		this.loanTypeLetterMappingListCtrl = loanTypeLetterMappingListCtrl;
	}

	public LoanTypeLetterMapping getLoanTypeLetterMapping() {
		return loanTypeLetterMapping;
	}

	public void setLoanTypeLetterMapping(LoanTypeLetterMapping loanTypeLetterMapping) {
		this.loanTypeLetterMapping = loanTypeLetterMapping;
	}

	@Autowired
	public void setLoanTypeLetterMappingService(LoanTypeLetterMappingService loanTypeLetterMappingService) {
		this.loanTypeLetterMappingService = loanTypeLetterMappingService;
	}
}