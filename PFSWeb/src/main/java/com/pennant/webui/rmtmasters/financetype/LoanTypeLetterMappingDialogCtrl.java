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
package com.pennant.webui.rmtmasters.financetype;

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
import org.zkoss.zul.Groupbox;
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
import com.pennant.backend.model.rmtmasters.LoanTypeLetterMapping;
import com.pennant.backend.service.rmtmasters.LoanTypeLetterMappingService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.backend.util.PennantStaticListUtil;
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
	private Listbox listBoxLoanTypeLetterMapping;
	protected Button btnNewLoanTypeLetterMapping;
	private transient LoanTypeLetterMappingListCtrl loanTypeLetterMappingListCtrl;
	private LoanTypeLetterMapping loanTypeLetterMapping;
	protected Groupbox gb_basicDetails;
	protected Groupbox gb_LoanTypeLetterMapping;
	boolean isLetterMappingNew = true;
	private String filteremail = "";
	boolean autoGenerationVal = false;

	private transient LoanTypeLetterMappingService loanTypeLetterMappingService;

	private final List<ValueLabel> letterTypeList = PennantStaticListUtil.getFinTypeLetterType();
	private final List<ValueLabel> letterModeList = PennantStaticListUtil.getFinTypeLetterMappingMode();
	private List<LoanTypeLetterMapping> letterMappingList = new ArrayList<>();
	private List<LoanTypeLetterMapping> deleteletterMappingList = new ArrayList<>();

	private enum ListFields {
		LETTER_TYPE,

		AUTO_GENERATION,

		MODE,

		EMAIL_TEMPLATE,

		AGREEMENT_CODE;

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

	public void onClick$btnDelete(Event event) throws InterruptedException {
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
		ltlm.setNewRecord(true);

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

	private void renderItem(LoanTypeLetterMapping ltlm) {
		logger.debug(Literal.ENTERING);

		Listitem listItem = new Listitem();
		Hbox hbox;
		Space space;
		boolean isReadOnly = isReadOnly("button_LoanTypeLetterMappingDialog_NewLetterMapping");
		listItem.setAttribute("loanTypeLetterMapping", ltlm);
		Listcell listCell;

		// ********************************** LetterType
		listCell = new Listcell();
		Combobox letterType = new Combobox();
		hbox = new Hbox();
		space = new Space();
		space.setSpacing("2px");
		space.setSclass(PennantConstants.mandateSclass);
		fillComboBox(letterType, String.valueOf(ltlm.getLetterType()), letterTypeList, "");
		letterType.addForward(Events.ON_CHANGE, windowLoanTypeLetterMappingDialog, "onChangeLetterType", listItem);

		if (ltlm.getLetterType() != null) {
			letterType.setValue(ltlm.getLetterType());
		}
		letterType.setId(ListFields.LETTER_TYPE.name().concat("-").concat(String.valueOf(ltlm.getKeyValue())));
		readOnlyComponent(isReadOnly, letterType);
		listCell.setParent(listItem);
		hbox.appendChild(space);
		hbox.appendChild(letterType);
		listCell.appendChild(hbox);

		// ********************************** AutoGeneration
		listCell = new Listcell();
		Checkbox autoGeneration = new Checkbox();
		hbox = new Hbox();
		space = new Space();
		space.setSpacing("2px");
		autoGeneration.setChecked(ltlm.isAutoGeneration());
		hbox.appendChild(space);
		hbox.appendChild(autoGeneration);
		autoGeneration.setId(ListFields.AUTO_GENERATION.name().concat("-").concat(String.valueOf(ltlm.getKeyValue())));
		readOnlyComponent(isReadOnly, autoGeneration);
		listCell.appendChild(hbox);
		listCell.setParent(listItem);

		// ********************************** Mode
		listCell = new Listcell();
		Combobox mode = new Combobox();
		hbox = new Hbox();
		space = new Space();
		space.setSpacing("2px");
		mode.setMaxlength(30);
		fillComboBox(mode, String.valueOf(ltlm.getLetterMode()), letterModeList, "");

		if (ltlm.getLetterMode() != null) {
			mode.setValue(ltlm.getLetterMode());
		}

		hbox.appendChild(space);
		hbox.appendChild(mode);
		mode.setId(ListFields.MODE.name().concat("-").concat(String.valueOf(ltlm.getKeyValue())));
		readOnlyComponent(isReadOnly, mode);
		listCell.appendChild(hbox);
		listCell.setParent(listItem);

		// **************** Email Template
		hbox = new Hbox();
		ExtendedCombobox emailTemplate = new ExtendedCombobox();
		emailTemplate.setModuleName("MailTemplate");
		emailTemplate.setValueColumn("TemplateCode");
		emailTemplate.setDescColumn("TemplateDesc");

		emailTemplate.setValidateColumns(new String[] { "TemplateCode" });
		hbox = new Hbox();
		space = new Space();
		space.setSpacing("2px");
		space.setSclass("mandatory");
		hbox.appendChild(space);
		hbox.appendChild(emailTemplate);

		emailTemplate.setId(ListFields.EMAIL_TEMPLATE.name().concat("-").concat(String.valueOf(ltlm.getKeyValue())));
		readOnlyComponent(isReadOnly, emailTemplate);

		Filter[] codeFilter = new Filter[1];
		codeFilter[0] = Filter.in("Event", "");
		emailTemplate.setFilters(codeFilter);

		listCell = new Listcell();
		listCell.appendChild(hbox);
		listCell.setParent(listItem);
		if (ltlm.getEmailTemplateId() != Long.MIN_VALUE && ltlm.getEmailTemplateId() != 0) {
			Search search = new Search(MailTemplate.class);
			search.addFilterEqual("TemplateId", ltlm.getEmailTemplateId());

			SearchProcessor searchProcessor = (SearchProcessor) SpringBeanUtil.getBean("searchProcessor");
			MailTemplate mailTemplate = (MailTemplate) searchProcessor.getResults(search).get(0);
			if (mailTemplate != null) {
				emailTemplate.setValue(mailTemplate.getTemplateCode());
				emailTemplate.setDescription(mailTemplate.getTemplateDesc());
				emailTemplate.setObject(mailTemplate);

				String selectedLetterType = letterType.getSelectedItem().getValue();
				Filter[] mailFilter = new Filter[1];
				mailFilter[0] = Filter.in("Event", selectedLetterType);
				emailTemplate.setFilters(mailFilter);
			}
		}

		// **************** AgreementCode
		hbox = new Hbox();
		ExtendedCombobox agreementCode = new ExtendedCombobox();
		agreementCode.setModuleName("AgreementDefinition");
		agreementCode.setValueColumn("AggCode");
		agreementCode.setDescColumn("AggName");

		agreementCode.setValidateColumns(new String[] { "AggCode" });
		hbox = new Hbox();
		space = new Space();
		space.setSpacing("2px");
		space.setSclass("mandatory");
		hbox.appendChild(space);
		hbox.appendChild(agreementCode);

		agreementCode.setId(ListFields.AGREEMENT_CODE.name().concat("-").concat(String.valueOf(ltlm.getKeyValue())));
		readOnlyComponent(isReadOnly, agreementCode);
		listCell = new Listcell();
		listCell.appendChild(hbox);
		listCell.setParent(listItem);
		if (ltlm.getAgreementCodeId() != Long.MIN_VALUE && ltlm.getAgreementCodeId() != 0) {
			Search search = new Search(AgreementDefinition.class);
			search.addFilterEqual("AggId", ltlm.getAgreementCodeId());

			SearchProcessor searchProcessor = (SearchProcessor) SpringBeanUtil.getBean("searchProcessor");
			AgreementDefinition agreementDefinition = (AgreementDefinition) searchProcessor.getResults(search).get(0);

			if (agreementDefinition != null) {
				agreementCode.setValue(agreementDefinition.getAggCode());
				agreementCode.setDescription(agreementDefinition.getAggDesc());
				agreementCode.setObject(agreementDefinition);
			}
		}

		// Delete action
		listCell = new Listcell();
		hbox = new Hbox();
		space = new Space();
		space.setSpacing("2px");
		Button button = new Button();
		button.setSclass("z-toolbarbutton");
		button.setLabel(Labels.getLabel("btnDelete.label"));
		button.addForward("onClick", self, "onClickLetterMappingButtonDelete", listItem);
		listCell.appendChild(button);
		listCell.setParent(listItem);
		readOnlyComponent(isReadOnly, button);

		listItem.setAttribute("data", ltlm);
		this.listBoxLoanTypeLetterMapping.appendChild(listItem);

		logger.debug(Literal.LEAVING);
	}

	public void onChangeLetterType(ForwardEvent event) {
		Listitem listitem = (Listitem) event.getData();
		Hbox hbox = (Hbox) getComponent(listitem, 1);
		Combobox letterType = (Combobox) hbox.getLastChild();

		this.filteremail = letterType.getSelectedItem().getValue();

		Hbox hbox1 = (Hbox) getComponent(listitem, 4);
		ExtendedCombobox emailTemplate = (ExtendedCombobox) hbox1.getLastChild();

		if (!"".equals(this.filteremail)) {
			String code = filteremail;
			Filter[] codeFilter = new Filter[1];
			codeFilter[0] = Filter.in("Event", code);
			emailTemplate.setFilters(codeFilter);
		}
	}

	public void onClickLetterMappingButtonDelete(ForwardEvent event) {
		logger.debug(Literal.ENTERING);

		Listitem item = (Listitem) event.getData();
		LoanTypeLetterMapping letterMapping = (LoanTypeLetterMapping) item.getAttribute("loanTypeLetterMapping");

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

	private void getCompValuetoBean(Listitem listItem, int index) {
		int i = 1;
		Hbox hbox = null;
		for (Component component : listItem.getChildren()) {
			if (i == index) {
				hbox = (Hbox) component.getFirstChild();
			}
			i++;
		}

		String id = StringUtils.trimToNull(hbox.getLastChild().getId());
		if (id == null) {
			return;
		}

		ListFields type = ListFields.getField(id.split("\\-")[0]);

		switch (type) {
		case LETTER_TYPE:
			Combobox letterType = (Combobox) hbox.getLastChild();
			Clients.clearWrongValue(letterType);
			String letterTypeVal = letterType.getValue();
			if (StringUtils.isEmpty(letterTypeVal) || PennantConstants.SELECT_LABEL.equals(letterTypeVal)) {
				throw new WrongValueException(letterType,
						Labels.getLabel("FIELD_IS_MAND", new String[] { "Letter Type " }));
			}
			break;

		case AUTO_GENERATION:
			Checkbox autoGeneration = (Checkbox) hbox.getLastChild();
			Clients.clearWrongValue(autoGeneration);
			autoGenerationVal = autoGeneration.isChecked();
			break;
		case MODE:
			Combobox mode = (Combobox) hbox.getLastChild();
			Clients.clearWrongValue(mode);
			String modeVal = mode.getValue();
			if (autoGenerationVal && PennantConstants.SELECT_LABEL.equals(modeVal)) {
				throw new WrongValueException(mode, Labels.getLabel("FIELD_IS_MAND", new String[] { "Mode " }));
			}
			break;

		case EMAIL_TEMPLATE:
			ExtendedCombobox emailTemplate = (ExtendedCombobox) hbox.getLastChild();
			Clients.clearWrongValue(emailTemplate);
			String emailTemplateVal = emailTemplate.getValue();

			if (StringUtils.isEmpty(emailTemplateVal)) {
				throw new WrongValueException(emailTemplate,
						Labels.getLabel("FIELD_IS_MAND", new String[] { "Email Template " }));
			}
			break;
		case AGREEMENT_CODE:
			ExtendedCombobox agreementCode = (ExtendedCombobox) hbox.getLastChild();
			Clients.clearWrongValue(agreementCode);
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
		ArrayList<WrongValueException> wve = new ArrayList<>();

		for (Listitem component : listBoxLoanTypeLetterMapping.getItems()) {
			Listitem listitem = (Listitem) component;
			LoanTypeLetterMapping letterMapping = (LoanTypeLetterMapping) listitem.getAttribute("data");

			try {
				Hbox hbox = (Hbox) getComponent(listitem, 1);
				getCompValuetoBean(listitem, 1);
				Combobox letterType = (Combobox) hbox.getLastChild();

				if (letterTypes.contains(letterType.getValue()) && !letterMapping.getRecordType().equals("DELETE")) {
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
				Hbox hbox = (Hbox) getComponent(listitem, 2);
				getCompValuetoBean(listitem, 2);
				Checkbox autoGeneration = (Checkbox) hbox.getLastChild();

				letterMapping.setAutoGeneration(autoGeneration.isChecked());

			} catch (WrongValueException we) {
				wve.add(we);
			}

			try {
				Hbox hbox = (Hbox) getComponent(listitem, 3);
				getCompValuetoBean(listitem, 3);
				Combobox mode = (Combobox) hbox.getLastChild();

				letterMapping.setLetterMode(mode.getValue());

			} catch (WrongValueException we) {
				wve.add(we);
			}

			try {
				Hbox hbox = (Hbox) getComponent(listitem, 4);
				getCompValuetoBean(listitem, 4);
				ExtendedCombobox emailTemplate = (ExtendedCombobox) hbox.getLastChild();
				MailTemplate object = (MailTemplate) emailTemplate.getObject();

				letterMapping.setEmailTemplateId(object.getId());
			} catch (WrongValueException we) {
				wve.add(we);
			}

			try {
				Hbox hbox = (Hbox) getComponent(listitem, 5);
				getCompValuetoBean(listitem, 5);
				ExtendedCombobox agreementCode = (ExtendedCombobox) hbox.getLastChild();
				AgreementDefinition object = (AgreementDefinition) agreementCode.getObject();

				letterMapping.setAgreementCodeId(object.getId());
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
					} else {
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
					if (isNew) {
					} else {
					}
				}
			}

			letterMapping.setFinType(this.finType.getValue());

			letterMapping.setRecordStatus(this.recordStatus.getValue());

			ltlmList.add(letterMapping);
		}

		ltlmList.addAll(deleteletterMappingList);
		ltlm.setLoanTypeLetterMappingList(ltlmList);
		this.loanTypeLetterMapping.setLoanTypeLetterMappingList(ltlmList);

		logger.debug(Literal.LEAVING);

		doSetLOVValidation();

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
			keyValue = keyValue + 1;
			ltlm.setKeyValue(keyValue);
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

	private void doSetLOVValidation() {
		logger.debug(Literal.ENTERING);

		logger.debug(Literal.LEAVING);
	}

	private void doRemoveLOVValidation() {
		logger.debug(Literal.ENTERING);

		logger.debug(Literal.LEAVING);
	}

	@Override
	protected void doClearMessage() {
		logger.debug(Literal.ENTERING);

		this.finType.setErrorMessage("");

		logger.debug(Literal.LEAVING);
	}

	private void doDelete() throws InterruptedException {
		logger.debug(Literal.ENTERING);

		final LoanTypeLetterMapping aLetterMapping = new LoanTypeLetterMapping();
		BeanUtils.copyProperties(getLoanTypeLetterMapping(), aLetterMapping);

		doDelete(String.valueOf(aLetterMapping.getId()), aLetterMapping);

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

	public void doFillCheckListDetailsList(List<LoanTypeLetterMapping> letterMappingList) {
		logger.debug("Entering");

		this.listBoxLoanTypeLetterMapping.getItems().clear();
		int keyValue = 0;
		for (LoanTypeLetterMapping letterMapping : letterMappingList) {
			letterMapping.setKeyValue(keyValue + 1);
			renderItem(letterMapping);
			keyValue = letterMapping.getKeyValue();
		}

		logger.debug("Leaving ");
	}

	public void doSave() {
		logger.debug(Literal.ENTERING);

		final LoanTypeLetterMapping letterMapping = new LoanTypeLetterMapping();
		BeanUtils.copyProperties(getLoanTypeLetterMapping(), letterMapping);
		boolean isNew = false;

		doSetValidation();

		if (listBoxLoanTypeLetterMapping.getItems().isEmpty()) {
			MessageUtil.showMessage("Please add atleast one Letter Mapping details.");
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

	protected boolean doProcess(LoanTypeLetterMapping aLetterMapping, String tranType) {
		logger.debug(Literal.ENTERING);

		List<LoanTypeLetterMapping> letterMapping = aLetterMapping.getLoanTypeLetterMappingList();
		List<LoanTypeLetterMapping> tempLetterMapping = new ArrayList<>();

		for (LoanTypeLetterMapping lm : letterMapping) {
			if (lm.getRecordType() != null && PennantConstants.RECORD_TYPE_CAN.equals(lm.getRecordType())) {
				tempLetterMapping.add(lm);
			}
		}

		letterMapping.removeAll(tempLetterMapping);
		aLetterMapping.setLoanTypeLetterMappingList(letterMapping);

		boolean processCompleted = false;
		AuditHeader auditHeader = null;
		String nextRoleCode = "";

		aLetterMapping.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		aLetterMapping.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aLetterMapping.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			String nextTaskId = "";
			aLetterMapping.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aLetterMapping.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getNextTaskIds(taskId, aLetterMapping);
				}

				if (isNotesMandatory(taskId, aLetterMapping)) {
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

			aLetterMapping.setTaskId(taskId);
			aLetterMapping.setNextTaskId(nextTaskId);
			aLetterMapping.setRoleCode(getRole());
			aLetterMapping.setNextRoleCode(nextRoleCode);

			auditHeader = getAuditHeader(aLetterMapping, tranType);
			String operationRefs = getServiceOperations(taskId, aLetterMapping);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader, null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(aLetterMapping, PennantConstants.TRAN_WF);
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		} else {
			auditHeader = getAuditHeader(aLetterMapping, tranType);
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

	private AuditHeader getAuditHeader(LoanTypeLetterMapping aFinTypeLetterMapping, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aFinTypeLetterMapping.getBefImage(),
				aFinTypeLetterMapping);
		return new AuditHeader(getReference(), null, null, null, auditDetail, aFinTypeLetterMapping.getUserDetails(),
				getOverideMap());
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

	public void setLetterMappingList(List<LoanTypeLetterMapping> letterMappingList) {
		this.letterMappingList = letterMappingList;
	}
}