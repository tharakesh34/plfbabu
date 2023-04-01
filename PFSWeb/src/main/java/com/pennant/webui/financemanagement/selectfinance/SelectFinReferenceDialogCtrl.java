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
package com.pennant.webui.financemanagement.selectfinance;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Button;
import org.zkoss.zul.Label;
import org.zkoss.zul.Row;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.ExtendedCombobox;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.customermasters.CustomerDetails;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceSuspHead;
import com.pennant.backend.model.finance.PMAY;
import com.pennant.backend.model.finance.ReinstateFinance;
import com.pennant.backend.model.finance.liability.LiabilityRequest;
import com.pennant.backend.model.financemanagement.FinanceFlag;
import com.pennant.backend.model.financemanagement.Provision;
import com.pennant.backend.service.customermasters.impl.CustomerDataService;
import com.pennant.backend.service.finance.FinanceDetailService;
import com.pennant.backend.service.finance.FinanceFlagsService;
import com.pennant.backend.service.lmtmasters.FinanceWorkFlowService;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.webui.finance.reinstatefinance.ReinstateFinanceListCtrl;
import com.pennant.webui.financemanagement.financeFlags.FinanceFlagsListCtrl;
import com.pennant.webui.financemanagement.liability.LiabilityRequestListCtrl;
import com.pennant.webui.financemanagement.provision.ProvisionListCtrl;
import com.pennant.webui.financemanagement.suspense.SuspenseListCtrl;
import com.pennant.webui.systemmasters.pmay.PMAYListCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.jdbc.search.Filter;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pff.constants.AccountingEvent;
import com.pennanttech.pff.constants.FinServiceEvent;
import com.pennanttech.pff.web.util.ComponentUtil;

/**
 * This is the controller class for the /WEB-INF/pages/Finance/FinanceMain/ SelectFinanceTypeDialog.zul file.
 */
public class SelectFinReferenceDialogCtrl extends GFCBaseCtrl<FinanceDetail> {
	private static final long serialVersionUID = 8556168885363682933L;
	private static final Logger logger = LogManager.getLogger(SelectFinReferenceDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the ZUL-file
	 * are getting autoWiredd by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_SelectFinReferenceDialog; // autoWired
	protected ExtendedCombobox finReference; // autoWired
	protected Button btnProceed; // autoWired
	protected Textbox custCIF;
	protected Label label_SelectFinReferenceDialog_CustShrtName;
	protected Row customerRow;

	private FinanceDetail financeDetail = null;
	private LiabilityRequest liabilityRequest;
	private LiabilityRequestListCtrl liabilityRequestListCtrl;
	private String moduleDefiner = "";
	protected String eventCode = "";
	private CustomerDataService customerDataService;
	private FinanceDetailService financeDetailService;
	private FinanceFlag financeFlag;
	private transient FinanceFlagsListCtrl financeFlagsListCtrl;
	private transient FinanceFlagsService financeFlagsService;

	private ReinstateFinance reinstateFinance;
	private Provision provision;
	private transient ReinstateFinanceListCtrl reinstateFinanceListCtrl;
	private transient ProvisionListCtrl provisionListCtrl; // overhanded per
															// param
	private String menuItemRightName = null;
	private transient SuspenseListCtrl suspenseListCtrl; // overhanded per param
	private FinanceSuspHead suspHead;
	private FinanceMain financeMain = new FinanceMain();
	private CustomerDetails custDetail;
	private transient PMAYListCtrl pmayListCtrl;
	private PMAY pmay = null;

	/**
	 * default constructor.<br>
	 */
	public SelectFinReferenceDialogCtrl() {
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
	public void onCreate$window_SelectFinReferenceDialog(Event event) {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_SelectFinReferenceDialog);

		// READ OVERHANDED parameters !
		if (arguments.containsKey("financeDetail")) {
			this.financeDetail = (FinanceDetail) arguments.get("financeDetail");
			FinanceDetail befImage = new FinanceDetail();
			if (this.financeDetail != null) {
				BeanUtils.copyProperties(this.financeDetail, befImage);
				this.financeDetail.setBefImage(befImage);
				setFinanceDetail(this.financeDetail);
			} else {
				setFinanceDetail(null);
			}

		} else {
			setFinanceDetail(null);
		}

		if (arguments.containsKey("moduleDefiner")) {
			moduleDefiner = (String) arguments.get("moduleDefiner");
		}
		// LiabilityRequest parms
		if (arguments.containsKey("liabilityRequest")) {
			setLiabilityRequest((LiabilityRequest) arguments.get("liabilityRequest"));
		}
		// LiabilityRequest List controller object
		if (arguments.containsKey("liabilityRequestListCtrl")) {
			setLiabilityRequestListCtrl((LiabilityRequestListCtrl) arguments.get("liabilityRequestListCtrl"));
		}

		if (arguments.containsKey("eventCode")) {
			eventCode = (String) arguments.get("eventCode");
		}
		// Finance Flags parms
		if (arguments.containsKey("financeFlag")) {
			this.financeFlag = (FinanceFlag) arguments.get("financeFlag");
		}

		if (arguments.containsKey("financeFlagsListCtrl")) {
			setFinanceFlagsListCtrl((FinanceFlagsListCtrl) arguments.get("financeFlagsListCtrl"));
		} else {
			setFinanceFlagsListCtrl(null);
		}

		// reinstateFinance parms
		if (arguments.containsKey("reinstateFinance")) {
			setReinstateFinance((ReinstateFinance) arguments.get("reinstateFinance"));
		}

		if (arguments.containsKey("reinstateFinanceListCtrl")) {
			setReinstateFinanceListCtrl((ReinstateFinanceListCtrl) arguments.get("reinstateFinanceListCtrl"));
		} else {
			setReinstateFinanceListCtrl(null);
		}
		// Provision parms
		if (arguments.containsKey("provision")) {
			provision = (Provision) arguments.get("provision");
		}
		if (arguments.containsKey("provisionListCtrl")) {
			setProvisionListCtrl((ProvisionListCtrl) arguments.get("provisionListCtrl"));
		}
		if (arguments.containsKey("menuItemRightName")) {
			menuItemRightName = (String) arguments.get("menuItemRightName");
		}
		if (arguments.containsKey("moduleDefiner")) {
			moduleDefiner = (String) arguments.get("moduleDefiner");
		}

		// suspense parms

		if (arguments.containsKey("suspenseListCtrl")) {
			setSuspenseListCtrl((SuspenseListCtrl) arguments.get("suspenseListCtrl"));
		}

		if (arguments.containsKey("suspHead")) {
			suspHead = (FinanceSuspHead) arguments.get("suspHead");
		} else {
			setSuspenseListCtrl(null);
		}

		// pmay

		if (arguments.containsKey("pmayListCtrl")) {
			setPmayListCtrl((PMAYListCtrl) arguments.get("pmayListCtrl"));
		} else {
			setPmayListCtrl(null);
		}

		if (arguments.containsKey("pmay")) {
			pmay = (PMAY) arguments.get("pmay");
		}

		doSetFieldProperties();

		showSelectFinanceTypeDialog();
		logger.debug("Leaving " + event.toString());
	}

	private void doSetFieldProperties() {
		this.finReference.setMaxlength(20);
		this.finReference.setTextBoxWidth(120);
		this.finReference.setMandatoryStyle(true);
		this.finReference.setModuleName("FinanceManagement");
		this.finReference.setValueColumn("FinReference");

		this.finReference.setValidateColumns(new String[] { "FinReference" });
		if (StringUtils.equals(eventCode, FinServiceEvent.REINSTATE)) {
			this.finReference.setModuleName("RejectFinanceMain");

		}

		String rolecodeList = "";
		if (getUserWorkspace().getUserRoles() != null && !getUserWorkspace().getUserRoles().isEmpty()) {
			for (String role : getUserWorkspace().getUserRoles()) {
				rolecodeList = rolecodeList.concat(role).concat("','");
			}

			if (StringUtils.isNotEmpty(rolecodeList)) {
				rolecodeList = rolecodeList.substring(0, rolecodeList.length() - 2);
				rolecodeList = "'".concat(rolecodeList);
			}
		} else {
			rolecodeList = "' '";
		}

		String buildedWhereCondition = " FinType IN(SELECT FinType FROM LMTFInanceworkflowdef WD JOIN WorkFlowDetails WF ";
		buildedWhereCondition = buildedWhereCondition
				.concat(" ON WD.WorkFlowType = WF.WorkFlowType AND WF.WorkFlowActive = 1 WHERE WD.FinEvent = '");
		if (StringUtils.equals(eventCode, FinServiceEvent.FINFLAGS)) {
			buildedWhereCondition = buildedWhereCondition.concat(FinServiceEvent.FINFLAGS);
		} else if (StringUtils.equals(eventCode, FinServiceEvent.REINSTATE)) {
			buildedWhereCondition = buildedWhereCondition.concat(FinServiceEvent.REINSTATE);

		} else if (StringUtils.equals(eventCode, AccountingEvent.PROVSN)) {
			buildedWhereCondition = buildedWhereCondition.concat(moduleDefiner);

		} else if (StringUtils.equals(eventCode, AccountingEvent.NORM_PIS)) {
			buildedWhereCondition = buildedWhereCondition.concat(moduleDefiner);
		} else {
			buildedWhereCondition = buildedWhereCondition.concat(moduleDefiner);
		}
		buildedWhereCondition = buildedWhereCondition.concat("' AND WF.FirstTaskOwner IN(");
		buildedWhereCondition = buildedWhereCondition.concat(rolecodeList);
		buildedWhereCondition = buildedWhereCondition.concat("))");

		this.finReference.setWhereClause(buildedWhereCondition);

		if (StringUtils.equals(eventCode, FinServiceEvent.REINSTATE)) {
			int allowedDays = SysParamUtil.getValueAsInt("REINSTATE_FINANCE_ALLOWEDDAYS");
			Date appDate = SysParamUtil.getAppDate();
			Date allowedDate = DateUtil.addDays(appDate, -allowedDays);
			this.finReference.setFilters(new Filter[] { new Filter("LastMntOn", allowedDate, Filter.OP_GREATER_THAN),
					new Filter("RcdMaintainSts", "", Filter.OP_EQUAL) });
		}
		if (StringUtils.equals(moduleDefiner, FinServiceEvent.NOCISSUANCE)) {
			this.finReference.setFilters(new Filter[] { new Filter("FinIsActive", 0, Filter.OP_EQUAL) });
		}

		if (StringUtils.equals(eventCode, FinanceConstants.PMAY)) {
			Filter[] filter = new Filter[1];
			filter[0] = new Filter("FinIsActive", 1, Filter.OP_EQUAL);
			this.finReference.setWhereClause(null);
			this.finReference.setFilters(filter);
		}

	}

	/**
	 * Opens the SelectFinanceTypeDialog window modal.
	 */
	private void showSelectFinanceTypeDialog() {
		logger.debug("Entering");
		try {
			// open the dialog in modal mode
			this.window_SelectFinReferenceDialog.doModal();
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving");
	}

	public void onFulfill$finReference(Event event) {

		Clients.clearWrongValue(this.finReference);
		Object dataObject = finReference.getObject();

		if (dataObject instanceof String) {
			this.finReference.setValue(dataObject.toString());
			this.finReference.setDescription("");
		} else {
			FinanceMain finMain = (FinanceMain) dataObject;
			if (finMain != null) {
				this.finReference.setValue(finMain.getFinReference());
				custDetail = customerDataService.getCustomerDetailsbyID(finMain.getCustID(), true, "_View");
				this.custCIF.setValue(String.valueOf(custDetail.getCustomer().getCustCIF()));

				if (custDetail != null && custDetail.getCustomer() != null) {
					this.label_SelectFinReferenceDialog_CustShrtName
							.setValue(custDetail.getCustomer().getCustShrtName());
				}

				this.customerRow.setVisible(true);
				if (StringUtils.equals(eventCode, FinServiceEvent.REINSTATE)) {
					BeanUtils.copyProperties(finMain, financeMain);
				} else {
					long finID = ComponentUtil.getFinID(this.finReference);
					financeMain = getFinanceDetailService().getFinanceMain(finID, "_AView");
				}
			}

		}
		if (StringUtils.isEmpty(this.finReference.getValue())) {
			this.customerRow.setVisible(false);
		}

	}

	public void onClick$btnProceed(Event event) {
		logger.debug("Entering");
		doClearMessage();
		this.finReference.setErrorMessage("");
		/*
		 * We can call our Dialog zul-file with parameters. So we can call them with a object of the selected item. For
		 * handed over these parameter only a Map is accepted. So we put the object in a HashMap.
		 */
		if (StringUtils.isBlank(this.finReference.getValue())) {

			throw new WrongValueException(this.finReference, Labels.getLabel("FIELD_IS_MAND",
					new String[] { Labels.getLabel("label_SelectFinReferenceDialog_FinReference.value") }));
		}

		final Map<String, Object> map = new HashMap<String, Object>();

		if (StringUtils.equals(eventCode, AccountingEvent.LIABILITY)
				|| StringUtils.equals(eventCode, AccountingEvent.NOCISSUANCE)) {
			// Fetch Total Finance Details Object
			this.customerRow.setVisible(true);
			map.put("liabilityRequest", getLiabilityRequest());
			map.put("financeDetail", getFinacneDetail());
			map.put("liabilityRequestListCtrl", getLiabilityRequestListCtrl());
			map.put("moduleDefiner", moduleDefiner);
			map.put("eventCode", eventCode);
			// call the ZUL-file with the parameters packed in a map
			try {
				Executions.createComponents("/WEB-INF/pages/Finance/LiabilityRequest/LiabilityRequestDialog.zul", null,
						map);

			} catch (Exception e) {
				MessageUtil.showError(e);
			}

		}

		if (StringUtils.equals(eventCode, FinServiceEvent.FINFLAGS)) {

			map.put("financeFlag", this.financeFlag);
			map.put("financeFlagsListCtrl", getFinanceFlagsListCtrl());
			map.put("financeMain", financeMain);
			// call the ZUL-file with the parameters packed in a map
			try {
				// call the zul-file with the parameters packed in a map
				Executions.createComponents("/WEB-INF/pages/FinanceManagement/FinanceFlags/FinanceFlagsDialog.zul",
						null, map);
			} catch (Exception e) {
				MessageUtil.showError(e);
			}
		}
		if (StringUtils.equals(eventCode, FinServiceEvent.REINSTATE)) {

			map.put("reinstateFinance", getReinstateFinance());
			map.put("reinstateFinanceListCtrl", getReinstateFinanceListCtrl());
			map.put("financeMain", financeMain);
			map.put("eventCode", eventCode);
			// call the ZUL-file with the parameters packed in a map
			try {

				Executions.createComponents("/WEB-INF/pages/Finance/ReinstateFinance/ReinstateFinanceDialog.zul", null,
						map);
			} catch (Exception e) {
				MessageUtil.showError(e);
			}
		}

		if (StringUtils.equals(eventCode, AccountingEvent.PROVSN)) {

			map.put("provision", provision);
			map.put("provisionListCtrl", getProvisionListCtrl());
			map.put("menuItemRightName", menuItemRightName);
			map.put("moduleDefiner", moduleDefiner);
			map.put("financeMain", financeMain);
			map.put("financeDetail", getFinacneDetail());
			// call the ZUL-file with the parameters packed in a map
			try {
				Executions.createComponents("/WEB-INF/pages/FinanceManagement/Provision/ProvisionDialog.zul", null,
						map);
			} catch (Exception e) {
				MessageUtil.showError(e);
			}
		}

		if (StringUtils.equals(eventCode, AccountingEvent.NORM_PIS)) {

			map.put("suspHead", suspHead);
			map.put("suspenseListCtrl", getSuspenseListCtrl());
			map.put("menuItemRightName", menuItemRightName);
			map.put("moduleDefiner", moduleDefiner);
			map.put("financeMain", financeMain);
			// call the ZUL-file with the parameters packed in a map
			try {
				Executions.createComponents("/WEB-INF/pages/FinanceManagement/Suspense/SuspenseDialog.zul", null, map);
			} catch (Exception e) {
				MessageUtil.showError(e);
			}
		}

		if (StringUtils.equals(eventCode, FinanceConstants.PMAY)) {
			long finID = ComponentUtil.getFinID(finReference);

			FinanceDetail fd = getFinanceDetailService().getFinanceDetailsForPmay(finID);
			if (fd.getPmay() != null) {
				pmay = fd.getPmay();
			}
			if (pmay == null) {
				pmay = new PMAY();
				pmay.setNewRecord(true);
			}

			pmay.setFinID(finID);
			pmay.setFinReference(fd.getFinReference());

			map.put("pmay", pmay);
			map.put("pmayListCtrl", getPmayListCtrl());
			map.put("financeDetail", fd);
			// call the ZUL-file with the parameters packed in a map
			try {
				Executions.createComponents("/WEB-INF/pages/SystemMaster/PMAY/PMAYDialog.zul", null, map);
			} catch (Exception e) {
				MessageUtil.showError(e);
			}
		}
		logger.debug("Leaving");
		this.window_SelectFinReferenceDialog.onClose();
	}

	private FinanceDetail getFinacneDetail() {
		long finID = ComponentUtil.getFinID(finReference);
		FinanceDetail fd = getFinanceDetailService().getFinSchdDetailById(finID, "_View", false);
		fd.getFinScheduleData().getFinanceMain().setNewRecord(true);
		fd.setCustomerDetails(custDetail);
		fd = financeDetailService.getFinanceReferenceDetails(fd, getRole(), "DDE", eventCode, moduleDefiner, false);
		return fd;
	}

	// Getters and Setters
	public FinanceDetail getFinanceDetail() {
		return financeDetail;
	}

	public void setFinanceDetail(FinanceDetail financeDetail) {
		this.financeDetail = financeDetail;
	}

	public void setFinanceWorkFlowService(FinanceWorkFlowService financeWorkFlowService) {
		this.financeWorkFlowService = financeWorkFlowService;
	}

	public FinanceWorkFlowService getFinanceWorkFlowService() {
		return financeWorkFlowService;
	}

	public LiabilityRequest getLiabilityRequest() {
		return liabilityRequest;
	}

	public void setLiabilityRequest(LiabilityRequest liabilityRequest) {
		this.liabilityRequest = liabilityRequest;
	}

	public LiabilityRequestListCtrl getLiabilityRequestListCtrl() {
		return liabilityRequestListCtrl;
	}

	public void setLiabilityRequestListCtrl(LiabilityRequestListCtrl liabilityRequestListCtrl) {
		this.liabilityRequestListCtrl = liabilityRequestListCtrl;
	}

	public FinanceDetailService getFinanceDetailService() {
		return financeDetailService;
	}

	public void setFinanceDetailService(FinanceDetailService financeDetailService) {
		this.financeDetailService = financeDetailService;
	}

	public FinanceFlagsListCtrl getFinanceFlagsListCtrl() {
		return financeFlagsListCtrl;
	}

	public void setFinanceFlagsListCtrl(FinanceFlagsListCtrl financeFlagsListCtrl) {
		this.financeFlagsListCtrl = financeFlagsListCtrl;
	}

	public FinanceFlagsService getFinanceFlagsService() {
		return financeFlagsService;
	}

	public void setFinanceFlagsService(FinanceFlagsService financeFlagsService) {
		this.financeFlagsService = financeFlagsService;
	}

	public ReinstateFinance getReinstateFinance() {
		return reinstateFinance;
	}

	public void setReinstateFinance(ReinstateFinance ReinstateFinance) {
		this.reinstateFinance = ReinstateFinance;
	}

	public ReinstateFinanceListCtrl getReinstateFinanceListCtrl() {
		return reinstateFinanceListCtrl;
	}

	public void setReinstateFinanceListCtrl(ReinstateFinanceListCtrl reinstateFinanceListCtrl) {
		this.reinstateFinanceListCtrl = reinstateFinanceListCtrl;
	}

	public ProvisionListCtrl getProvisionListCtrl() {
		return provisionListCtrl;
	}

	public void setProvisionListCtrl(ProvisionListCtrl provisionListCtrl) {
		this.provisionListCtrl = provisionListCtrl;
	}

	public SuspenseListCtrl getSuspenseListCtrl() {
		return suspenseListCtrl;
	}

	public void setSuspenseListCtrl(SuspenseListCtrl suspenseListCtrl) {
		this.suspenseListCtrl = suspenseListCtrl;
	}

	public PMAYListCtrl getPmayListCtrl() {
		return pmayListCtrl;
	}

	public void setPmayListCtrl(PMAYListCtrl pmayListCtrl) {
		this.pmayListCtrl = pmayListCtrl;
	}

	@Autowired
	public void setCustomerDataService(CustomerDataService customerDataService) {
		this.customerDataService = customerDataService;
	}

}
