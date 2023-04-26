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
 * * FileName : SelectFinReferenceDialogCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 30-08-2016 * *
 * Modified Date : 30-08-2016 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 16-11-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.webui.financemanagement.selectmanualadvise;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Button;
import org.zkoss.zul.Window;

import com.pennant.ExtendedCombobox;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.finance.FinanceWriteoffDAO;
import com.pennant.backend.model.finance.FinChangeCustomer;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.HoldDisbursement;
import com.pennant.backend.model.finance.JointAccountDetail;
import com.pennant.backend.model.finance.ManualAdvise;
import com.pennant.backend.service.finance.FinChangeCustomerService;
import com.pennant.backend.service.finance.FinanceDetailService;
import com.pennant.backend.service.finance.HoldDisbursementService;
import com.pennant.backend.service.finance.JointAccountDetailService;
import com.pennant.backend.service.finance.ManualAdviseService;
import com.pennant.backend.util.SMTParameterConstants;
import com.pennant.webui.finance.finchangecustomer.FinChangeCustomerListCtrl;
import com.pennant.webui.finance.holddisbursement.HoldDisbursementListCtrl;
import com.pennant.webui.finance.manualadvise.ManualAdviseListCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.App;
import com.pennanttech.pennapps.core.App.Database;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.jdbc.search.Filter;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pff.constants.FinServiceEvent;
import com.pennanttech.pff.web.util.ComponentUtil;

/**
 * This is the controller class for the /WEB-INF/pages/Finance/FinanceMain/ SelectFinanceTypeDialog.zul file.
 */
public class SelectManualAdviseFinReferenceDialogCtrl extends GFCBaseCtrl<FinanceMain> {
	private static final long serialVersionUID = 8556168885363682933L;
	private static final Logger logger = LogManager.getLogger(SelectManualAdviseFinReferenceDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the ZUL-file
	 * are getting autoWiredd by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_SelectFinanceReferenceDialog; // autoWired
	protected ExtendedCombobox finReference; // autoWired
	protected Button btnProceed; // autoWireddialogCtrl
	private ManualAdvise manualAdvise = null;
	private ManualAdviseListCtrl manualAdviseListCtrl;
	private transient ManualAdviseService manualAdviseService;
	private HoldDisbursement holdDisbursement;
	private HoldDisbursementListCtrl holdDisbursementListCtrl;
	private String moduleDefiner = "";
	private HoldDisbursementService holdDisbursementService;
	private FinChangeCustomer finChangeCustomer;
	private FinChangeCustomerListCtrl finChangeCustomerListCtrl;
	private JointAccountDetailService jointAccountDetailService;
	private FinChangeCustomerService finChangeCustomerService;
	List<JointAccountDetail> joinAccountDetail = null;
	private String custChangeRoles = SysParamUtil.getValueAsString(SMTParameterConstants.CUST_CHANGE_ROLES);
	private static final String FINCHANGECUSTOMER = "FinChangeCustomer";
	private FinanceWriteoffDAO financeWriteoffDAO;
	private FinanceDetailService financeDetailService;
	private String module = null;

	/**
	 * default constructor.<br>
	 */
	public SelectManualAdviseFinReferenceDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "";
	}

	// Component Events

	/**
	 * Before binding the data and calling the dialog window we check, if the ZUL-file is called with a parameter for a
	 * selected FinanceMain object in a Map.
	 * 
	 * @param event
	 */
	public void onCreate$window_SelectFinanceReferenceDialog(Event event) {
		logger.debug(Literal.ENTERING);
		setPageComponents(window_SelectFinanceReferenceDialog);
		try {
			if (arguments.get("moduleDefiner") != null && arguments.get("moduleDefiner").equals("holdDisbursement")) {
				moduleDefiner = arguments.get("moduleDefiner").toString();
				this.holdDisbursement = (HoldDisbursement) arguments.get("holdDisbursement");
				this.holdDisbursementListCtrl = (HoldDisbursementListCtrl) arguments.get("holdDisbursementListCtrl");
				if (this.holdDisbursement == null) {
					throw new Exception(Labels.getLabel("error.unhandled"));
				}
			} else if (arguments.get("moduleDefiner") != null
					&& arguments.get("moduleDefiner").equals(FINCHANGECUSTOMER)) {
				moduleDefiner = arguments.get("moduleDefiner").toString();
				this.finChangeCustomer = (FinChangeCustomer) arguments.get("finChangeCustomer");
				this.finChangeCustomerListCtrl = (FinChangeCustomerListCtrl) arguments.get("finChangeCustomerListCtrl");
				if (this.finChangeCustomer == null) {
					throw new Exception(Labels.getLabel("error.unhandled"));
				}
			} else {
				this.manualAdvise = (ManualAdvise) arguments.get("manualAdvise");
				this.manualAdviseListCtrl = (ManualAdviseListCtrl) arguments.get("manualAdviseListCtrl");

				if (this.manualAdvise == null) {
					throw new Exception(Labels.getLabel("error.unhandled"));
				}
			}
			if (arguments.containsKey("module")) {
				this.module = (String) arguments.get("module");
			}
			doSetFieldProperties();
		} catch (Exception e) {
			closeDialog();
			MessageUtil.showError(e);
		}
		showSelectPaymentHeaderDialog();

		logger.debug(Literal.LEAVING + event.toString());
	}

	/**
	 * Opens the SelectPaymentHeaderDialog window modal.
	 */
	private void showSelectPaymentHeaderDialog() {
		logger.debug("Entering");
		try {
			this.window_SelectFinanceReferenceDialog.doModal();
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving");
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");
		String moduleName = "FinanceMain";

		if (moduleDefiner.equals(FINCHANGECUSTOMER)) {
			moduleName = "FinCustomerChange";
			Filter rolesFilter[] = new Filter[1];
			List<String> roleCodes = Arrays.asList(custChangeRoles.split(","));
			rolesFilter[0] = Filter.in("NextRoleCode", roleCodes);
			this.finReference.setFilters(rolesFilter);
		} else if (moduleDefiner.equals("holdDisbursement")) {
			Filter[] filtersFin = new Filter[2];
			filtersFin[0] = new Filter("finisactive", true, Filter.OP_EQUAL);
			if (App.DATABASE == Database.POSTGRES) {
				filtersFin[1] = new Filter("CLOSINGSTATUS", "", Filter.OP_NULL);
			} else {
				filtersFin[1] = new Filter("CLOSINGSTATUS", null, Filter.OP_NULL);

			}

			this.finReference.setFilters(filtersFin);
		}
		this.finReference.setMaxlength(20);
		this.finReference.setTextBoxWidth(120);
		this.finReference.setMandatoryStyle(true);
		this.finReference.setModuleName(moduleName);
		this.finReference.setValueColumn("FinReference");
		this.finReference.setValidateColumns(new String[] { "FinReference" });
		logger.debug("Leaving");
	}

	/**
	 * When user clicks on button "btnProceed" button
	 * 
	 * @param event
	 */
	public void onClick$btnProceed(Event event) {
		logger.debug(Literal.ENTERING + event.toString());

		if (!doFieldValidation()) {
			return;
		}

		long finID = ComponentUtil.getFinID(this.finReference);

		// Validate Loan is INPROGRESS in any Other Servicing option or NOT ?
		String rcdMntnSts = financeDetailService.getFinanceMainByRcdMaintenance(finID);
		if (StringUtils.isNotEmpty(rcdMntnSts)
				&& (!FinServiceEvent.MANUALADVISE.equals(rcdMntnSts) || !FinServiceEvent.HOLDDISB.equals(rcdMntnSts))) {
			MessageUtil.showError(Labels.getLabel("Finance_Inprogresss_" + rcdMntnSts));
			return;
		}

		Map<String, Object> arg = new HashMap<String, Object>();
		if (StringUtils.equals(moduleDefiner, "holdDisbursement")) {
			arg.put("holdDisbursement", holdDisbursement);
			arg.put("holdDisbursementListCtrl", holdDisbursementListCtrl);
			// arg.put("financeMain", financeMain);
			try {
				// FinanceMain financeMain = (FinanceMain) this.finReference.getObject();
				// holdDisbursement.setDisbursedAmount(financeMain.getFinCurrAssetValue());
				// holdDisbursement.setTotalLoanAmt(financeMain.getFinAssetValue());
				holdDisbursement.setFinID(finID);
				holdDisbursement.setFinReference(StringUtils.trimToEmpty(this.finReference.getValue()));
				Executions.createComponents("/WEB-INF/pages/Finance/HoldDisbursement/HoldDisbursementDialog.zul", null,
						arg);
				this.window_SelectFinanceReferenceDialog.onClose();
			} catch (Exception e) {
				logger.error("Exception:", e);
				MessageUtil.showError(e);
			}
		} else if (StringUtils.equals(moduleDefiner, FINCHANGECUSTOMER)) {
			arg.put("finChangeCustomerListCtrl", finChangeCustomerListCtrl);
			arg.put("jointAccountDetails", joinAccountDetail);
			try {
				FinChangeCustomer dfinChangeCustomer = (FinChangeCustomer) this.finReference.getObject();
				finChangeCustomer.setCustCategory(dfinChangeCustomer.getCustCategory());
				finChangeCustomer.setFinID(finID);
				finChangeCustomer.setFinReference(dfinChangeCustomer.getFinReference());
				finChangeCustomer.setOldCustId(dfinChangeCustomer.getOldCustId());
				finChangeCustomer.setFinType(dfinChangeCustomer.getFinType());
				finChangeCustomer.setCustCif(dfinChangeCustomer.getCustCif());
				arg.put("finChangeCustomer", finChangeCustomer);

				Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/ChangeCustomerDialog.zul", null, arg);
				this.window_SelectFinanceReferenceDialog.onClose();
			} catch (Exception e) {
				logger.error("Exception:", e);
				MessageUtil.showError(e);
			}

		} else {
			FinanceMain fm = manualAdviseService.getFinanceDetails(finID);

			manualAdvise.setFinID(finID);
			manualAdvise.setFinReference(fm.getFinReference());

			arg.put("manualAdvise", manualAdvise);
			arg.put("manualAdviseListCtrl", manualAdviseListCtrl);
			arg.put("financeMain", fm);
			arg.put("module", this.module);
			try {
				Executions.createComponents("/WEB-INF/pages/FinanceManagement/ManualAdvise/ManualAdviseDialog.zul",
						null, arg);
				this.window_SelectFinanceReferenceDialog.onClose();
			} catch (Exception e) {
				logger.error("Exception:", e);
				MessageUtil.showError(e);
			}
		}

		logger.debug(Literal.LEAVING + event.toString());
	}

	/**
	 * Method for checking /validating fields before proceed.
	 * 
	 * @return
	 */
	private boolean doFieldValidation() {

		doClearMessage();
		doRemoveValidation();

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();
		try {
			long finID = ComponentUtil.getFinID(this.finReference);

			if (StringUtils.trimToNull(this.finReference.getValue()) == null) {
				throw new WrongValueException(this.finReference, Labels.getLabel("CHECK_NO_EMPTY",
						new String[] { Labels.getLabel("label_SelectPaymentHeaderDialog_FinaType.value") }));
			}
			if (StringUtils.equals(moduleDefiner, FINCHANGECUSTOMER)) {
				boolean count = getFinChangeCustomerService().isFinReferenceProcess(finID);

				if (count) {
					throw new WrongValueException(this.finReference, Labels.getLabel("DATA_ALREADY_EXISTS",
							new String[] { Labels.getLabel("label_SelectPaymentHeaderDialog_FinaType.value") }));
				}

				joinAccountDetail = getJointAccountDetailService().getJoinAccountDetail(finID, "_View");

				if (joinAccountDetail != null && joinAccountDetail.size() == 0) {
					throw new WrongValueException(this.finReference, Labels.getLabel("NO_COAPPLICANTS",
							new String[] { Labels.getLabel("label_SelectPaymentHeaderDialog_FinaType.value") }));
				}
			}

			if (moduleDefiner.equals("holdDisbursement")) {
				if (getHoldDisbursementService().isFinServiceInstructionExist(finID, "_temp", "AddDisbursement")) {
					throw new WrongValueException(this.finReference, "Not Allowed for Hold Disbursement");
				}
			}
		} catch (WrongValueException e) {
			wve.add(e);
		}
		if (wve.size() > 0) {
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}
		return true;
	}

	@Override
	protected void doClearMessage() {
		logger.debug("Entering");
		this.finReference.setErrorMessage("");
		logger.debug("Leaving");
	}

	private void doRemoveValidation() {
		logger.debug("Entering");
		this.finReference.setConstraint("");
		logger.debug("Leaving");
	}

	/**
	 * Setting the amount formats based on currency
	 * 
	 * @param event
	 */
	public void onFulfill$finReference(Event event) {
		logger.debug("Entering " + event.toString());

		Clients.clearWrongValue(this.finReference);
		this.finReference.setConstraint("");
		this.finReference.setErrorMessage("");

		logger.debug("Leaving " + event.toString());
	}

	// Getters and Setters
	public ManualAdviseListCtrl getManualAdviseListCtrl() {
		return manualAdviseListCtrl;
	}

	public void setManualAdviseListCtrl(ManualAdviseListCtrl manualAdviseListCtrl) {
		this.manualAdviseListCtrl = manualAdviseListCtrl;
	}

	public ManualAdvise getManualAdvise() {
		return manualAdvise;
	}

	public void setManualAdvise(ManualAdvise manualAdvise) {
	}

	public ManualAdviseService getManualAdviseService() {
		return manualAdviseService;
	}

	public void setManualAdviseService(ManualAdviseService manualAdviseService) {
		this.manualAdviseService = manualAdviseService;
	}

	public HoldDisbursementService getHoldDisbursementService() {
		return holdDisbursementService;
	}

	public void setHoldDisbursementService(HoldDisbursementService holdDisbursementService) {
		this.holdDisbursementService = holdDisbursementService;
	}

	public JointAccountDetailService getJointAccountDetailService() {
		return jointAccountDetailService;
	}

	public void setJointAccountDetailService(JointAccountDetailService jointAccountDetailService) {
		this.jointAccountDetailService = jointAccountDetailService;
	}

	public FinChangeCustomerService getFinChangeCustomerService() {
		return finChangeCustomerService;
	}

	public void setFinChangeCustomerService(FinChangeCustomerService finChangeCustomerService) {
		this.finChangeCustomerService = finChangeCustomerService;
	}

	public void setFinanceWriteoffDAO(FinanceWriteoffDAO financeWriteoffDAO) {
		this.financeWriteoffDAO = financeWriteoffDAO;
	}

	public void setFinanceDetailService(FinanceDetailService financeDetailService) {
		this.financeDetailService = financeDetailService;
	}

}
