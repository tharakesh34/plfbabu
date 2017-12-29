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

package com.pennant.webui.reason.detail;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Button;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.ExtendedCombobox;
import com.pennant.backend.model.applicationmaster.ReasonCategory;
import com.pennant.backend.model.applicationmaster.ReasonCode;
import com.pennant.backend.model.reason.details.ReasonDetails;
import com.pennant.backend.model.reason.details.ReasonHeader;
import com.pennant.component.Uppercasebox;
import com.pennant.search.Filter;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.searchdialogs.ExtendedMultipleSearchListBox;
import com.pennanttech.pennapps.core.resource.Literal;

public class ReasonDetailsCtrl extends GFCBaseCtrl<ReasonHeader> {

	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(ReasonDetailsCtrl.class);

	protected Window window_ReasonDetailsDialog;
	protected ExtendedCombobox reasonCategory;
	protected Uppercasebox reasons;
	protected Button btnReasons;
	protected Textbox remarks;

	private Object financeMainDialogCtrl = null;
	private int reason = 0;

	public ReasonDetailsCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "";
	}

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * ZUL-file is called with a parameter for a selected Reject object in a
	 * Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_ReasonDetailsDialog(Event event) throws Exception {
		logger.debug(Literal.ENTERING);

		// Set the page level components.
		setPageComponents(window_ReasonDetailsDialog);

		if (arguments.containsKey("financeMainDialogCtrl")) {
			this.financeMainDialogCtrl = (Object) arguments.get("financeMainDialogCtrl");
		}
		
		if (arguments.containsKey("reason")) {
			this.reason = Integer.parseInt(arguments.get("reason").toString());
		}

		doSetFieldProperties();
		doShowDialog();

		logger.debug(Literal.LEAVING);
	}

	private void doSetFieldProperties() {
		logger.debug(Literal.LEAVING);

		this.reasonCategory.setDisplayStyle(4);
		this.reasonCategory.setModuleName("ReasonCategory");
		this.reasonCategory.setValueColumn("Code");
		this.reasonCategory.setDescColumn("Description");
		this.reasonCategory.setValidateColumns(new String[] { "Code", "Description" });
		
		StringBuilder whereClause = new StringBuilder();
		whereClause.append(" Id in (Select ReasonCategoryId from Reasons Where ReasonTypeId = (Select Id from ReasonTypes Where Id  = ");
		whereClause.append(reason).append(" ))");
		
		this.reasonCategory.setWhereClause(whereClause.toString());
		this.reasonCategory.setMandatoryStyle(true);
		this.remarks.setMaxlength(1000);
		
		readOnlyComponent(true, this.reasons);
		readOnlyComponent(true, this.btnReasons);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Method for Adding Flags into Multi Selection Extended box
	 * 
	 * @param event
	 */
	@SuppressWarnings("unchecked")
	public void onClick$btnReasons(Event event) {
		logger.debug("Entering  " + event.toString());
		// FIXME
		Map<String, Object> resonTypesMap = new HashMap<String, Object>();
		Object dataObject = null;

		String[] resonTypeS = this.reasons.getValue().split(",");
		for (int i = 0; i < resonTypeS.length; i++) {
			resonTypesMap.put(resonTypeS[i], null);
		}
		
		if (StringUtils.trimToNull(this.reasonCategory.getValue()) != null) {
			ReasonCategory reasonCategory = (ReasonCategory) this.reasonCategory.getObject();
			Filter[] filter = new Filter[1];
			filter[0] = new Filter("ReasonCategoryId", reasonCategory.getId(), Filter.OP_EQUAL);		
			dataObject = ExtendedMultipleSearchListBox.show(this.window_ReasonDetailsDialog, "ReasonCode", resonTypesMap, filter);
		} else {
			dataObject = ExtendedMultipleSearchListBox.show(this.window_ReasonDetailsDialog, "ReasonCode", resonTypesMap);
		}
		
		
		if (dataObject instanceof String) {
			this.reasons.setValue(dataObject.toString());
			this.reasons.setTooltiptext("");
		} else {
			HashMap<String, Object> details = (HashMap<String, Object>) dataObject;
			if (details != null) {
				String tempReasons = details.keySet().toString();
				tempReasons = tempReasons.replace("[", " ").replace("]", "").replace(" ", "");
				if (tempReasons.startsWith(",")) {
					tempReasons = tempReasons.substring(1);
				}
				if (tempReasons.endsWith(",")) {
					tempReasons = tempReasons.substring(0, tempReasons.length() - 1);
				}
				this.reasons.setValue(tempReasons);
			}

			// Setting tooltip with Descriptions
			String toolTipDesc = "";
			for (String key : details.keySet()) {
				Object obj = (Object) details.get(key);
				if (obj instanceof String) {
					// Do Nothing
				} else {
					ReasonCode type = (ReasonCode) obj;
					if (type != null) {
						toolTipDesc = toolTipDesc.concat(type.getCode().concat(" , "));
					}
				}
			}
			if (StringUtils.isNotBlank(toolTipDesc) && toolTipDesc.endsWith(", ")) {
				toolTipDesc = toolTipDesc.substring(0, toolTipDesc.length() - 2);
			}
			this.reasons.setTooltiptext(toolTipDesc);
		}

		logger.debug("Leaving " + event.toString());
	}
	
	/**
	 * when the "save" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnSave(Event event) throws InterruptedException {
		logger.debug(Literal.ENTERING + event.toString());

		doSave();

		logger.debug(Literal.LEAVING + event.toString());
	}

	/**
	 * when the "close" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnClose(Event event) throws InterruptedException {
		logger.debug(Literal.ENTERING + event.toString());
		doClose();
		logger.debug(Literal.LEAVING + event.toString());
	}

	public void doShowDialog() throws InterruptedException {
		logger.debug(Literal.ENTERING);

		this.window_ReasonDetailsDialog.setHeight(Integer.parseInt(getBorderLayoutHeight().substring(0,getBorderLayoutHeight().indexOf("px")))- 150 + "px");
		this.window_ReasonDetailsDialog.doModal();

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param
	 */
	public void doWriteComponentsToBean(ReasonHeader reasonHeader) {
		logger.debug(Literal.ENTERING);

		
		List<ReasonDetails> list = new ArrayList<ReasonDetails>();	
		ReasonDetails details  = null;

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		try {
			this.reasonCategory.getValue();
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			reasonHeader.setRemarks(this.remarks.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if (StringUtils.trimToNull(this.reasons.getValue()) != null) {
				for (String val : reasons.getValue().split(",")) {
					details = new ReasonDetails();
					details.setReasonId(Long.valueOf(val));
					list.add(details);
				}
				reasonHeader.setDetailsList(list);
			}
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

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug(Literal.ENTERING);

		this.reasonCategory.setConstraint(new PTStringValidator(Labels.getLabel("label_ReasonDetailsDialog_ReasonCategory.value"), null, true, true));
		if (!this.reasons.isReadonly()) {
			this.reasons.setConstraint(new PTStringValidator(Labels.getLabel("label_ReasonDetailsDialog_Reasons.value"), null, true));
		}
		if (!this.remarks.isReadonly()) {
			this.remarks.setConstraint(new PTStringValidator(Labels.getLabel("label_ReasonDetailsDialog_Remarks.value"), null, true));
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Removes the Validation by setting the constraints to the empty.
	 */
	private void doRemoveValidation() {
		logger.debug(Literal.ENTERING);

		this.reasonCategory.setConstraint("");
		this.reasons.setConstraint("");
		this.remarks.setConstraint("");

		logger.debug(Literal.LEAVING);
	}
	
	public void onFulfill$reasonCategory(Event event) {
		logger.debug(Literal.ENTERING);

		Object dataObject = reasonCategory.getObject();

		if (dataObject == null) {
			this.reasonCategory.setValue("");
			this.reasons.setValue("");
			readOnlyComponent(true, this.reasons);
			readOnlyComponent(true, this.btnReasons);
		} else if (dataObject instanceof String) {
			this.reasonCategory.setValue(dataObject.toString());
			this.reasons.setValue("");
			readOnlyComponent(true, this.reasons);
			readOnlyComponent(true, this.btnReasons);
		} else {
			readOnlyComponent(false, this.reasons);
			readOnlyComponent(false, this.btnReasons);
		}
		
		logger.debug(Literal.LEAVING);
	}
	
	public void doSave() throws InterruptedException {
		logger.debug(Literal.ENTERING);

		doClearMessage();
		
		ReasonHeader reasonHeader = new ReasonHeader();
		doSetValidation();
		doWriteComponentsToBean(reasonHeader);
		try {
			getFinanceMainDialogCtrl().getClass().getMethod("setReasonDetails", ReasonHeader.class).invoke(financeMainDialogCtrl, reasonHeader);
			getFinanceMainDialogCtrl().getClass().getMethod("doSave").invoke(getFinanceMainDialogCtrl());
		} catch (Exception e) {
			logger.error("Exception: ", e);
		}
		this.window_ReasonDetailsDialog.onClose();

		logger.debug(Literal.LEAVING);
	}

	public void doClose() {
		logger.debug(Literal.ENTERING);

		this.window_ReasonDetailsDialog.onClose();

		logger.debug(Literal.LEAVING);
	}

	@Override
	protected void doClearMessage() {
		logger.debug("Entering");

		this.reasonCategory.setErrorMessage("");
		this.reasons.setErrorMessage("");
		this.remarks.setErrorMessage("");

		logger.debug("Leaving");
	}

	public Object getFinanceMainDialogCtrl() {
		return financeMainDialogCtrl;
	}

	public void setFinanceMainDialogCtrl(Object financeMainDialogCtrl) {
		this.financeMainDialogCtrl = financeMainDialogCtrl;
	}

	 
}
