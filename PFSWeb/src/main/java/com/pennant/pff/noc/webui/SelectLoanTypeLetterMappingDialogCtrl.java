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
package com.pennant.pff.noc.webui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.pff.noc.dao.LoanTypeLetterMappingDAO;
import com.pennant.pff.noc.model.LoanTypeLetterMapping;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pff.core.TableType;

public class SelectLoanTypeLetterMappingDialogCtrl extends GFCBaseCtrl<LoanTypeLetterMapping> {

	private static final long serialVersionUID = -5898229156972529248L;
	private static final Logger logger = LogManager.getLogger(SelectLoanTypeLetterMappingDialogCtrl.class);

	protected Window windowSelectLoanTypeLetterMappingDialog;
	protected ExtendedCombobox finType;
	protected Button btnProceed;

	private LoanTypeLetterMappingListCtrl loanTypeLetterMappingListCtrl;
	private LoanTypeLetterMapping loanTypeLetterMapping;
	private LoanTypeLetterMappingDAO loanTypeLetterMappingDAO;

	private boolean isCopyProcess;
	private boolean alwCopyOption;

	public SelectLoanTypeLetterMappingDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "LoanTypeLetterMappingDialog";
	}

	public void onCreate$windowSelectLoanTypeLetterMappingDialog(Event event) {
		logger.debug(Literal.ENTERING);

		setPageComponents(windowSelectLoanTypeLetterMappingDialog);

		if (arguments.containsKey("loanTypeLetterMappingListCtrl")) {
			this.loanTypeLetterMappingListCtrl = (LoanTypeLetterMappingListCtrl) arguments
					.get("loanTypeLetterMappingListCtrl");
		}

		if (arguments.containsKey("loanTypeLetterMapping")) {
			this.loanTypeLetterMapping = (LoanTypeLetterMapping) arguments.get("loanTypeLetterMapping");
		}

		if (arguments.containsKey("isCopyProcess")) {
			this.isCopyProcess = (boolean) arguments.get("isCopyProcess");
		}

		if (arguments.containsKey("alwCopyOption")) {
			this.alwCopyOption = (boolean) arguments.get("alwCopyOption");
		}

		doLoadWorkFlow(this.loanTypeLetterMapping.isWorkflow(), this.loanTypeLetterMapping.getWorkflowId(),
				this.loanTypeLetterMapping.getNextTaskId());

		if (isWorkFlowEnabled() && !enqiryModule) {
			getUserWorkspace().allocateRoleAuthorities(getRole(), this.pageRightName);
		}

		doSetFieldProperties();
		doCheckRights();

		this.windowSelectLoanTypeLetterMappingDialog.doModal();

		logger.debug(Literal.LEAVING);
	}

	private void doCheckRights() {
		logger.debug(Literal.ENTERING);

		getUserWorkspace().allocateAuthorities(this.pageRightName, getRole());

		this.btnProceed.setVisible(true);

		logger.debug(Literal.LEAVING);
	}

	private void doSetFieldProperties() {
		logger.debug(Literal.ENTERING);

		this.finType.setModuleName("FinanceType");
		this.finType.setValueColumn("FinType");
		this.finType.setDescColumn("FinTypeDesc");
		this.finType.setValidateColumns(new String[] { "FinType", "FinTypeDesc" });
		this.finType.setMandatoryStyle(true);

		logger.debug(Literal.LEAVING);
	}

	public void onClick$btnProceed(Event event) {
		logger.debug(Literal.ENTERING);

		doSetValidation();
		doWriteComponentsToBean(this.loanTypeLetterMapping);
		doShowDialogPage(this.loanTypeLetterMapping);

		logger.debug(Literal.LEAVING);
	}

	private void doShowDialogPage(LoanTypeLetterMapping ltlm) {
		logger.debug(Literal.ENTERING);

		Map<String, Object> aruments = new HashMap<String, Object>();

		aruments.put("loanTypeLetterMapping", ltlm);
		aruments.put("isCopyProcess", isCopyProcess);
		aruments.put("alwCopyOption", alwCopyOption);
		aruments.put("loanTypeLetterMappingListCtrl", this.loanTypeLetterMappingListCtrl);
		aruments.put("moduleCode", moduleCode);
		aruments.put("enqiryModule", enqiryModule);
		aruments.put("finType", this.finType.getValue());

		try {

			Executions.createComponents("/WEB-INF/pages/NOC/LoanTypeLetterMappingDialog.zul", null, aruments);
			this.windowSelectLoanTypeLetterMappingDialog.onClose();
		} catch (Exception e) {
			MessageUtil.showError(e);
		}

		logger.debug(Literal.LEAVING);
	}

	private void doSetValidation() {
		logger.debug(Literal.ENTERING);

		String label = "label_FinanceTypeDialog_FinType.value";

		if (!this.finType.isReadonly()) {
			this.finType.setConstraint(
					new PTStringValidator(Labels.getLabel(label), PennantRegularExpressions.REGEX_ALPHANUM, true));
		}

		logger.debug(Literal.LEAVING);
	}

	private void doRemoveValidation() {
		logger.debug(Literal.ENTERING);

		this.finType.setConstraint("");

		logger.debug(Literal.LEAVING);
	}

	public void doWriteComponentsToBean(LoanTypeLetterMapping altlm) {
		logger.debug(Literal.ENTERING);

		List<WrongValueException> wve = new ArrayList<>();

		try {
			String finTypeValue = this.finType.getValue().toUpperCase();
			boolean finTypeExist = this.loanTypeLetterMappingDAO.isDuplicateKey(finTypeValue, TableType.BOTH_TAB);
			if (finTypeExist) {
				throw new WrongValueException(this.finType,
						Labels.getLabel("label_SelectFinanceTypeDialog_finTypeExist.value"));
			}
			altlm.setFinType(finTypeValue);
		} catch (WrongValueException we) {
			wve.add(we);
		}

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

	public void setLoanTypeLetterMappingListCtrl(LoanTypeLetterMappingListCtrl loanTypeLetterMappingListCtrl) {
		this.loanTypeLetterMappingListCtrl = loanTypeLetterMappingListCtrl;
	}

	public void setLoanTypeLetterMapping(LoanTypeLetterMapping loanTypeLetterMapping) {
		this.loanTypeLetterMapping = loanTypeLetterMapping;
	}

	@Autowired
	public void setLoanTypeLetterMappingDAO(LoanTypeLetterMappingDAO loanTypeLetterMappingDAO) {
		this.loanTypeLetterMappingDAO = loanTypeLetterMappingDAO;
	}

}