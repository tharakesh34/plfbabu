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
 * * FileName : PresentmentDetailListCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 22-04-2017 * *
 * Modified Date : 22-04-2017 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 22-04-2017 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.webui.financemanagement.presentmentdetail;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Row;
import org.zkoss.zul.Space;
import org.zkoss.zul.Window;

import com.pennant.ExtendedCombobox;
import com.pennant.app.constants.ImplementationConstants;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.backend.util.SMTParameterConstants;
import com.pennant.component.Uppercasebox;
import com.pennant.pff.mandate.InstrumentType;
import com.pennant.pff.mandate.MandateUtil;
import com.pennant.pff.presentment.service.ExtractionService;
import com.pennant.util.Constraint.PTDateValidator;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.constraint.PTListValidator;
import com.pennant.webui.util.searchdialogs.MultiSelectionSearchListBox;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pff.presentment.model.PresentmentDetail;
import com.pennanttech.pff.presentment.model.PresentmentHeader;

/**
 * This is the controller class for the
 * /WEB-INF/pages/com.pennant.financemanagement/PresentmentDetail/PresentmentDetailList.zul file.
 * 
 */
public class PresentmentDetailExtractListCtrl extends GFCBaseListCtrl<PresentmentDetail> {
	private static final long serialVersionUID = 1L;

	protected Window window_PresentmentExtractDetailList;
	protected Borderlayout borderLayout_PresentmentExtractDetailList;
	protected Paging pagingPresentmentExtractDetailList;
	protected Listbox listBoxPresentmentExtractDetail;

	protected Button button_PresentmentDetailList_Extract;

	protected Combobox mandateType;
	protected ExtendedCombobox loanType;
	protected Button btnloanType;
	protected Datebox fromdate;
	protected Datebox toDate;
	protected Uppercasebox branches;
	protected Button btnBranches;
	protected ExtendedCombobox entity;
	protected Combobox presentmentType;
	protected Label label_EmandateSource;
	protected ExtendedCombobox emandateSource;
	protected Space space_LoanType;
	protected Row row_lppAndBounceRequited;
	protected Checkbox lppRequired;
	protected Checkbox bounceRequired;
	protected Space space_mandateType;

	@Autowired
	private ExtractionService extractionService;

	/**
	 * default constructor.<br>
	 */
	public PresentmentDetailExtractListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "PresentmentDetail";
		super.pageRightName = "PresentmentDetailList";
		super.tableName = "PRESENTMENTDETAIL_EXTRACT_VIEW";
		super.queueTableName = "PRESENTMENTDETAIL_EXTRACT_VIEW";
	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onCreate$window_PresentmentExtractDetailList(Event event) {
		logger.debug(Literal.ENTERING);

		setPageComponents(window_PresentmentExtractDetailList, borderLayout_PresentmentExtractDetailList,
				listBoxPresentmentExtractDetail, pagingPresentmentExtractDetailList);

		// Render the page and display the data.
		doRenderPage();
		doSetFieldProperties();

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set the component level properties.
	 */
	private void doSetFieldProperties() {
		logger.debug(Literal.ENTERING);

		fillComboBox(this.mandateType, "", MandateUtil.getInstrumentTypesForBE(), "");
		fillComboBox(this.presentmentType, PennantConstants.PROCESS_PRESENTMENT,
				PennantStaticListUtil.getPresetmentTypeList(), "");

		this.fromdate.setFormat(PennantConstants.dateFormat);
		this.toDate.setFormat(PennantConstants.dateFormat);
		if (ImplementationConstants.LOANTYPE_REQ_FOR_PRESENTMENT_PROCESS) {
			this.loanType.setMandatoryStyle(true);
		}

		if (ImplementationConstants.INSTRUMENTTYPE_REQ_FOR_PRESENTMENT_PROCESS) {
			this.space_mandateType.setSclass("mandatory");
		}

		this.entity.setModuleName("Entity");
		this.entity.setMandatoryStyle(true);
		this.entity.setDisplayStyle(2);
		this.entity.setValueColumn("EntityCode");
		this.entity.setDescColumn("EntityDesc");
		this.entity.setValidateColumns(new String[] { "EntityCode" });

		this.loanType.setModuleName("FinanceType");
		this.loanType.setDisplayStyle(2);
		this.loanType.setValueColumn("FinType");
		this.loanType.setDescColumn("FinTypeDesc");
		this.loanType.setValidateColumns(new String[] { "FinType" });

		this.emandateSource.setModuleName("Mandate_Sources");
		this.emandateSource.setDisplayStyle(2);
		this.emandateSource.setValueColumn("Code");
		this.emandateSource.setDescColumn("Description");
		this.emandateSource.setValidateColumns(new String[] { "Code" });

		if (ImplementationConstants.PRESENTMENT_AUTO_DOWNLOAD
				&& SysParamUtil.isAllowed(SMTParameterConstants.PRESENTMENT_AUTO_EXTRACT_JOB_ENABLED)) {
			this.button_PresentmentDetailList_Extract.setDisabled(true);
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the search button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$button_PresentmentDetailList_Extract(Event event) {
		logger.debug(Literal.ENTERING);

		String errorMsg = null;
		PresentmentHeader detailHeader = new PresentmentHeader();

		doSetValidation();
		doWriteComponentsToBean(detailHeader);
		try {
			errorMsg = extractDetails(detailHeader);
			MessageUtil.showMessage(errorMsg);
			return;
		} catch (Exception e) {
			MessageUtil.showError(e);
		}

		logger.debug(Literal.LEAVING);
	}

	private void doSetValidation() {
		logger.debug(Literal.ENTERING);

		doRemoveValidation();
		if (ImplementationConstants.LOANTYPE_REQ_FOR_PRESENTMENT_PROCESS
				&& !InstrumentType.isNACH(mandateType.getSelectedItem().getValue())) {
			this.loanType
					.setConstraint(new PTStringValidator(Labels.getLabel("label_PresentmentDetailList_Product.value"),
							PennantRegularExpressions.REGEX_ALPHANUM_SPACE_SPL_COMMAHIPHEN, true));
		}
		if (ImplementationConstants.INSTRUMENTTYPE_REQ_FOR_PRESENTMENT_PROCESS) {
			this.mandateType.setConstraint(
					new PTListValidator<>(Labels.getLabel("label_PresentmentDetailList_MandateType.value"),
							MandateUtil.getInstrumentTypesForBE(), true));
		}
		this.presentmentType.setConstraint(
				new PTListValidator<>(Labels.getLabel("label_PresentmentDetailList_PresentmentType.value"),
						PennantStaticListUtil.getPresetmentTypeList(), true));
		this.fromdate.setConstraint(
				new PTDateValidator(Labels.getLabel("label_PresentmentDetailList_Fromdate.value"), true));
		this.toDate
				.setConstraint(new PTDateValidator(Labels.getLabel("label_PresentmentDetailList_ToDate.value"), true));
		this.entity.setConstraint(new PTStringValidator(Labels.getLabel("label_DisbursementList_Entity.value"),
				PennantRegularExpressions.REGEX_ALPHANUM, true));
		this.emandateSource.setConstraint(new PTStringValidator(
				Labels.getLabel("label_PresentmentDetailList_EmandateSource.value"), null, true, true));

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aAcademic
	 */
	public void doWriteComponentsToBean(PresentmentHeader detailHeader) {
		logger.debug(Literal.ENTERING);

		ArrayList<WrongValueException> wve = new ArrayList<>();

		try {
			detailHeader.setFromDate(this.fromdate.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			detailHeader.setToDate(this.toDate.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			detailHeader.setMandateType(this.mandateType.getSelectedItem().getValue().toString());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if (this.toDate != null && DateUtil.compare(this.toDate.getValue(), this.fromdate.getValue()) < 0) {
				throw new WrongValueException(this.toDate,
						Labels.getLabel("NUMBER_MINVALUE",
								new String[] { Labels.getLabel("label_PresentmentDetailList_ToDate.value"),
										Labels.getLabel("label_PresentmentDetailList_Fromdate.value") }));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if (InstrumentType.isEMandate(mandateType.getSelectedItem().getValue())) {
				detailHeader.setEmandateSource(StringUtils.trimToNull(this.emandateSource.getValidatedValue()));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			int diffentDays = SysParamUtil.getValueAsInt("PRESENTMENT_DAYS_DEF");
			if (DateUtil.getDaysBetween(this.fromdate.getValue(), this.toDate.getValue()) >= diffentDays) {
				throw new WrongValueException(this.toDate,
						Labels.getLabel("label_Difference_between_days") + " " + diffentDays);
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			int alwdDaysFromAppDate = SysParamUtil.getValueAsInt("PRESENTMENT_EXTRACT_ALW_DAYS_FROM_APP_DATE");
			if (alwdDaysFromAppDate > 0 && DateUtil.getDaysBetween(this.toDate.getValue(),
					SysParamUtil.getAppDate()) >= alwdDaysFromAppDate) {
				throw new WrongValueException(this.toDate,
						Labels.getLabel("label_Diff_btwn_To_and_App_date") + " " + alwdDaysFromAppDate);
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			detailHeader.setEntityCode(this.entity.getValidatedValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			detailHeader.setPresentmentType(this.presentmentType.getSelectedItem().getValue().toString());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		if (ImplementationConstants.LOANTYPE_REQ_FOR_PRESENTMENT_PROCESS
				&& !InstrumentType.isNACH(mandateType.getSelectedItem().getValue())) {
			try {
				detailHeader.setLoanType(this.loanType.getValue());
			} catch (WrongValueException we) {
				wve.add(we);
			}
		}

		try {
			detailHeader.setLppReq(this.lppRequired.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			detailHeader.setBounceReq(this.bounceRequired.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		doRemoveValidation();

		if (!wve.isEmpty()) {
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Method for onChange event of presentmentType.
	 * 
	 * @param event
	 */
	public void onChange$presentmentType(Event event) {
		logger.debug(Literal.ENTERING);
		String presentmentType = getComboboxValue(this.presentmentType);

		if (PennantConstants.PROCESS_REPRESENTMENT.equalsIgnoreCase(presentmentType)) {
			this.row_lppAndBounceRequited.setVisible(true);
		} else {
			this.row_lppAndBounceRequited.setVisible(false);
			this.lppRequired.setChecked(false);
			this.bounceRequired.setChecked(false);
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Disables the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug(Literal.ENTERING);

		Clients.clearWrongValue(fromdate);
		Clients.clearWrongValue(toDate);
		this.fromdate.setConstraint("");
		this.toDate.setConstraint("");
		this.fromdate.setErrorMessage("");
		this.toDate.setErrorMessage("");
		this.mandateType.setConstraint("");
		this.presentmentType.setConstraint("");
		this.loanType.setConstraint("");
		this.entity.setConstraint("");
		this.emandateSource.setConstraint("");
		logger.debug(Literal.LEAVING);
	}

	private String extractDetails(PresentmentHeader detailHeader) {
		logger.debug(Literal.ENTERING);

		if (ImplementationConstants.LOANTYPE_REQ_FOR_PRESENTMENT_PROCESS
				&& !InstrumentType.isNACH(mandateType.getSelectedItem().getValue())) {
			detailHeader.setLoanType(this.loanType.getValue());
		}
		detailHeader.setFinBranch(this.branches.getValue());
		detailHeader.setApprovedDate(SysParamUtil.getAppDate());
		detailHeader.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		detailHeader.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		detailHeader.setEmandateSource(emandateSource.getValidatedValue());

		int extractPresentment = extractionService.preparePresentment(detailHeader);

		logger.debug(Literal.LEAVING);

		if (extractPresentment == 0) {
			return Labels.getLabel("label_PresentmentSearchMessage");
		} else {
			return Labels.getLabel("label_PresentmentExtractedMessage");
		}
	}

	/**
	 * The framework calls this event handler when user clicks the refresh button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$btnRefresh(Event event) {
		doResetInitValues();
	}

	private void doResetInitValues() {
		logger.debug(Literal.ENTERING);

		fillComboBox(this.mandateType, "", MandateUtil.getInstrumentTypesForBE(), "");
		fillComboBox(this.presentmentType, "", PennantStaticListUtil.getPresetmentTypeList(), "");
		this.loanType.setErrorMessage("");
		this.loanType.setValue("");
		this.loanType.setDescColumn("");
		this.fromdate.setValue(null);
		this.toDate.setValue(null);
		this.branches.setValue("");
		this.entity.setErrorMessage("");
		this.entity.setValue("");
		this.entity.setDescColumn("");
		this.emandateSource.setValue("");
		this.emandateSource.setDescColumn("");
		logger.debug(Literal.LEAVING);
	}

	public void onClick$btnBranches(Event event) {
		logger.debug(Literal.ENTERING);

		Object dataObject = MultiSelectionSearchListBox.show(this.window_PresentmentExtractDetailList, "Branch",
				this.branches.getValue(), null);
		if (dataObject instanceof String) {
			this.branches.setValue(dataObject.toString());
		} else {
			@SuppressWarnings("unchecked")
			Map<String, Object> details = (Map<String, Object>) dataObject;
			if (details != null) {
				String tempflagcode = "";
				List<String> flagKeys = new ArrayList<>(details.keySet());
				for (int i = 0; i < flagKeys.size(); i++) {
					if (StringUtils.isEmpty(flagKeys.get(i))) {
						continue;
					}
					if (i == 0) {
						tempflagcode = flagKeys.get(i);
					} else {
						tempflagcode = tempflagcode + "," + flagKeys.get(i);
					}
				}
				this.branches.setValue(tempflagcode);
			}
		}
		logger.debug(Literal.LEAVING);
	}

	public void onSelect$mandateType(Event event) {
		logger.debug(Literal.ENTERING);

		String code = mandateType.getSelectedItem().getValue();
		if (ImplementationConstants.LOANTYPE_REQ_FOR_PRESENTMENT_PROCESS && InstrumentType.isNACH(code)) {
			this.loanType.setMandatoryStyle(false);
			Clients.clearWrongValue(loanType);
		}

		if (PennantConstants.List_Select.equals(code)) {
			return;
		}

		if (InstrumentType.isEMandate(code)) {
			this.emandateSource.setValue("");
			this.emandateSource.setDescColumn("");
			emandateSource.setVisible(true);
			this.emandateSource.setMandatoryStyle(true);
			label_EmandateSource.setVisible(true);
		} else {
			emandateSource.setVisible(false);
			label_EmandateSource.setVisible(false);
			this.emandateSource.setMandatoryStyle(false);
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the print button to print the results.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$print(Event event) {
		doPrintResults();
	}

	/**
	 * The framework calls this event handler when user clicks the help button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$help(Event event) {
		doShowHelp(event);
	}
}