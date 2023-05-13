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
 * * FileName : SelectFinTypeDialogCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 21-03-2017 * *
 * Modified Date : 21-03-2017 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 21-03-2017 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.webui.rmtmasters.financetype;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Button;
import org.zkoss.zul.Window;

import com.pennant.ExtendedCombobox;
import com.pennant.app.constants.LengthConstants;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.systemmasters.DivisionDetailDAO;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.model.smtmasters.PFSParameter;
import com.pennant.backend.service.rmtmasters.FinanceTypeService;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.component.Uppercasebox;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.jdbc.search.Filter;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the /WEB-INF/pages/SolutionFactory/FinanceType/SelectFinTypeDialog.zul file.
 */
public class SelectFinTypeDialogCtrl extends GFCBaseCtrl<FinanceType> {

	private static final long serialVersionUID = -5898229156972529248L;
	private static final Logger logger = LogManager.getLogger(SelectFinTypeDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the ZUL-file
	 * are getting autoWiredd by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_SelectFinTypeDialog;
	protected Uppercasebox finType;
	protected ExtendedCombobox finCcy;
	protected Button btnProceed;

	private FinanceTypeListCtrl financeTypeListCtrl;
	private FinanceType financeType;
	private FinanceTypeService financeTypeService;
	private DivisionDetailDAO divisionDetailDAO;

	private boolean isCopyProcess;
	private boolean isPromotion;
	private boolean alwCopyOption;
	private boolean isOverdraft;
	private boolean consumerDurable;
	protected ExtendedCombobox finDivision;

	/**
	 * default constructor.<br>
	 */
	public SelectFinTypeDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "FinanceTypeDialog";
	}

	/**
	 * Before binding the data and calling the dialog window we check, if the ZUL-file is called with a parameter for a
	 * selected FinanceMain object in a Map.
	 * 
	 * @param event
	 */
	public void onCreate$window_SelectFinTypeDialog(Event event) {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_SelectFinTypeDialog);

		// READ OVERHANDED parameters !
		if (arguments.containsKey("financeTypeListCtrl")) {
			this.financeTypeListCtrl = (FinanceTypeListCtrl) arguments.get("financeTypeListCtrl");
		}

		if (arguments.containsKey("financeType")) {
			this.financeType = (FinanceType) arguments.get("financeType");
		}

		if (arguments.containsKey("isCopyProcess")) {
			this.isCopyProcess = (boolean) arguments.get("isCopyProcess");
		}

		if (arguments.containsKey("isPromotion")) {
			this.isPromotion = (boolean) arguments.get("isPromotion");
		}

		if (arguments.containsKey("alwCopyOption")) {
			this.alwCopyOption = (boolean) arguments.get("alwCopyOption");
		}

		if (arguments.containsKey("isOverdraft")) {
			this.isOverdraft = (boolean) arguments.get("isOverdraft");
		}
		if (arguments.containsKey("consumerDurable")) {
			this.consumerDurable = (boolean) arguments.get("consumerDurable");
		}

		doLoadWorkFlow(this.financeType.isWorkflow(), this.financeType.getWorkflowId(),
				this.financeType.getNextTaskId());

		if (isWorkFlowEnabled() && !enqiryModule) {
			getUserWorkspace().allocateRoleAuthorities(getRole(), this.pageRightName);
		}

		doSetFieldProperties();
		doCheckRights();

		this.window_SelectFinTypeDialog.doModal();

		logger.debug("Leaving " + event.toString());
	}

	/**
	 * Set Visible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug("Entering");

		getUserWorkspace().allocateAuthorities(this.pageRightName, getRole());

		this.btnProceed.setVisible(getUserWorkspace().isAllowed("button_FinanceTypeDialog_btnSave"));

		logger.debug("Leaving");
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");

		this.finType.setMaxlength(8);

		this.finCcy.setMaxlength(LengthConstants.LEN_CURRENCY);
		this.finCcy.setMandatoryStyle(true);
		this.finCcy.setModuleName("Currency");
		this.finCcy.setValueColumn("CcyCode");
		this.finCcy.setDescColumn("CcyDesc");
		this.finCcy.setValidateColumns(new String[] { "CcyCode" });

		if (isPromotion) {
			this.finDivision.setMaxlength(8);
			this.finDivision.setMandatoryStyle(true);
			this.finDivision.setModuleName("DivisionDetail");
			this.finDivision.setValueColumn("DivisionCode");
			this.finDivision.setDescColumn("DivisionCodeDesc");
			this.finDivision.setValidateColumns(new String[] { "DivisionCode" });
			Filter[] finDivisionFilters = new Filter[1];
			finDivisionFilters[0] = new Filter("AlwPromotion", 1, Filter.OP_EQUAL);
			this.finDivision.setFilters(finDivisionFilters);
		}
		this.finDivision.setMaxlength(8);
		this.finDivision.setMandatoryStyle(true);
		this.finDivision.setModuleName("DivisionDetail");
		this.finDivision.setValueColumn("DivisionCode");
		this.finDivision.setDescColumn("DivisionCodeDesc");
		this.finDivision.setValidateColumns(new String[] { "DivisionCode" });

		PFSParameter parameter = SysParamUtil.getSystemParameterObject("APP_DFT_CURR");
		this.finCcy.setValue(parameter.getSysParmValue().trim());
		this.finCcy.setDescription(parameter.getSysParmDescription());

		logger.debug("Leaving");
	}

	/**
	 * When user clicks on button "btnProceed" button
	 * 
	 * @param event
	 */
	public void onClick$btnProceed(Event event) {
		logger.debug("Entering " + event.toString());

		doSetValidation();
		doWriteComponentsToBean(this.financeType);
		doShowDialogPage(this.financeType);

		logger.debug("Leaving " + event.toString());
	}

	/**
	 * Displays the dialog page with the required parameters as map.
	 * 
	 * @param financeType The entity that need to be passed to the dialog.
	 */
	private void doShowDialogPage(FinanceType financeType) {
		logger.debug("Entering");

		Map<String, Object> aruments = new HashMap<String, Object>();

		aruments.put("financeType", financeType);
		aruments.put("isCopyProcess", isCopyProcess);
		aruments.put("isPromotion", isPromotion);
		aruments.put("alwCopyOption", alwCopyOption);
		aruments.put("financeTypeListCtrl", this.financeTypeListCtrl);
		aruments.put("isOverdraft", isOverdraft);
		aruments.put("consumerDurable", consumerDurable);
		aruments.put("moduleCode", moduleCode);
		aruments.put("enqiryModule", enqiryModule);

		// call the ZUL-file with the parameters packed in a map
		try {
			if (isOverdraft) {
				Executions.createComponents("/WEB-INF/pages/SolutionFactory/FinanceType/OverdraftFinanceTypeDialog.zul",
						null, aruments);
			} else if (consumerDurable) {
				Executions.createComponents("/WEB-INF/pages/SolutionFactory/FinanceType/CDFinanceTypeDialog.zul", null,
						aruments);
			} else {
				Executions.createComponents("/WEB-INF/pages/SolutionFactory/FinanceType/FinanceTypeDialog.zul", null,
						aruments);
			}
			this.window_SelectFinTypeDialog.onClose();
		} catch (Exception e) {
			MessageUtil.showError(e);
		}

		logger.debug("Leaving");
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug("Entering");

		String label = "label_FinanceTypeDialog_FinType.value";

		if (isPromotion) {
			label = "label_FinanceTypeDialog_PromoCode.value";
		}

		if (!this.finType.isReadonly()) {
			this.finType.setConstraint(
					new PTStringValidator(Labels.getLabel(label), PennantRegularExpressions.REGEX_ALPHANUM, true));
		}

		this.finCcy.setConstraint(
				new PTStringValidator(Labels.getLabel("label_FinanceTypeDialog_FinCcy.value"), null, true, true));

		this.finDivision.setConstraint(
				new PTStringValidator(Labels.getLabel("label_FinanceTypeDialog_FinDivision.value"), null, true, true));

		logger.debug("Leaving");
	}

	/**
	 * Remove the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering");

		this.finType.setConstraint("");
		this.finCcy.setConstraint("");
		this.finDivision.setConstraint("");

		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aFinanceType
	 */
	public void doWriteComponentsToBean(FinanceType aFinanceType) {
		logger.debug("Entering");

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		// Finance Type
		try {
			String finTypeValue = this.finType.getValue().toUpperCase();
			boolean finTypeExist = this.financeTypeService.getFinTypeExist(finTypeValue, "_View");
			if (finTypeExist) {
				throw new WrongValueException(this.finType,
						Labels.getLabel("label_SelectFinanceTypeDialog_finTypeExist.value"));
			}
			aFinanceType.setFinType(finTypeValue);
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// FinCcy
		try {
			aFinanceType.setFinCcy(this.finCcy.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		// Fin Division
		try {
			aFinanceType.setLovDescFinDivisionName(this.finDivision.getDescription());
			aFinanceType.setFinDivision(this.finDivision.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		if (consumerDurable) {
			aFinanceType.setFinIsGenRef(true);
		}

		aFinanceType.setLovDescEntityCode(divisionDetailDAO.getEntityCodeByDivision(aFinanceType.getFinDivision(), ""));

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

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public FinanceTypeListCtrl getFinanceTypeListCtrl() {
		return financeTypeListCtrl;
	}

	public void setFinanceTypeListCtrl(FinanceTypeListCtrl financeTypeListCtrl) {
		this.financeTypeListCtrl = financeTypeListCtrl;
	}

	public FinanceType getFinanceType() {
		return financeType;
	}

	public void setFinanceType(FinanceType financeType) {
		this.financeType = financeType;
	}

	public FinanceTypeService getFinanceTypeService() {
		return financeTypeService;
	}

	public void setFinanceTypeService(FinanceTypeService financeTypeService) {
		this.financeTypeService = financeTypeService;
	}

	@Autowired
	public void setDivisionDetailDAO(DivisionDetailDAO divisionDetailDAO) {
		this.divisionDetailDAO = divisionDetailDAO;
	}
}
