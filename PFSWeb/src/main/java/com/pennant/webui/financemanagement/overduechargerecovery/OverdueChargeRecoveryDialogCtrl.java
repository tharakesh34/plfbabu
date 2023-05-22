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
 * * FileName : OverdueChargeRecoveryDialogCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 11-05-2012 *
 * * Modified Date : 11-05-2012 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 11-05-2012 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.webui.financemanagement.overduechargerecovery;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
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
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Longbox;
import org.zkoss.zul.Row;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.OverDueRecoveryPostingsUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.financemanagement.OverdueChargeRecovery;
import com.pennant.backend.service.PagedListService;
import com.pennant.backend.service.financemanagement.OverdueChargeRecoveryService;
import com.pennant.backend.service.rmtmasters.FinanceTypeService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.util.ErrorControl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.InterfaceException;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * /WEB-INF/pages/FinanceManagement/OverdueChargeRecovery/overdueChargeRecoveryDialog.zul file. <br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 */
public class OverdueChargeRecoveryDialogCtrl extends GFCBaseCtrl<OverdueChargeRecovery> {

	private static final long serialVersionUID = 728436178283801925L;
	private static final Logger logger = LogManager.getLogger(OverdueChargeRecoveryDialogCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ All the components that are defined here
	 * and have a corresponding component with the same 'id' in the zul-file are getting autowired by our 'extends
	 * GFCBaseCtrl' GenericForwardComposer. ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window window_OverdueChargeRecoveryDialog; // autowired
	protected Textbox finReference; // autowired
	protected Datebox finSchdDate; // autowired
	protected Datebox finStartDate; // autowired
	protected Datebox finMaturityDate; // autowired
	protected Decimalbox finAmt; // autowired
	protected Decimalbox curFinAmt; // autowired
	protected Decimalbox curSchPriDue; // autowired
	protected Decimalbox curSchPftDue; // autowired
	protected Decimalbox totOvrDueChrg; // autowired
	protected Decimalbox totOvrDueChrgWaived; // autowired
	protected Decimalbox totOvrDueChrgPaid; // autowired
	protected Decimalbox totOvrDueChrgBal; // autowired
	protected Combobox cbFinODFor; // autowired
	protected Textbox finBrnm; // autowired
	protected Textbox finType; // autowired
	protected Longbox finCustId; // autowired
	protected Textbox lovDescCustCIF; // autowired
	protected Label custShrtName; // autowired
	protected Textbox finCcy; // autowired
	protected Datebox finODDate; // autowired
	protected Decimalbox finODPri; // autowired
	protected Decimalbox finODPft; // autowired
	protected Decimalbox finODTot; // autowired
	protected Textbox finODCRuleCode; // autowired
	protected Textbox finODCPLAc; // autowired
	protected Textbox finODCCAc; // autowired
	protected Decimalbox finODCPLShare; // autowired
	protected Checkbox finODCSweep; // autowired
	protected Textbox finODCCustCtg; // autowired
	protected Combobox cbFinODCType; // autowired
	protected Textbox finODCOn; // autowired
	protected Decimalbox finODC; // autowired
	protected Intbox finODCGraceDays; // autowired
	protected Checkbox finODCAlwWaiver; // autowired
	protected Decimalbox finODCMaxWaiver; // autowired
	protected Decimalbox finODCPenalty; // autowired
	protected Decimalbox finODCWaived; // autowired
	protected Decimalbox finODCPLPenalty; // autowired
	protected Decimalbox finODCCPenalty; // autowired
	protected Decimalbox finODCPaid; // autowired
	protected Datebox finODCLastPaidDate; // autowired
	protected Textbox finODCRecoverySts; // autowired
	protected Button btnRecoverNow; // autowired
	protected Decimalbox balChrgRecovery; // autowired
	protected Row oDCWaivedRow; // autowired
	protected Row oDCAlwWaiverRow; // autowired
	protected Row statusRow;

	// not auto wired vars
	private OverdueChargeRecovery overdueChargeRecovery; // overhanded per param
	private OverdueChargeRecovery prvOverdueChargeRecovery; // overhanded per param
	private transient OverdueChargeRecoveryListCtrl overdueChargeRecoveryListCtrl; // overhanded per param

	private transient boolean validationOn;

	// ServiceDAOs / Domain Classes
	private transient OverdueChargeRecoveryService overdueChargeRecoveryService;
	private transient PagedListService pagedListService;
	private transient FinanceTypeService financeTypeService;
	private Map<String, List<ErrorDetail>> overideMap = new HashMap<String, List<ErrorDetail>>();
	private static List<ValueLabel> finOdForList = PennantStaticListUtil.getODCChargeFor();
	private static List<ValueLabel> finODCTypeList = PennantStaticListUtil.getODCChargeType();
	private transient OverDueRecoveryPostingsUtil recoveryPostingsUtil;
	Date dateValueDate = SysParamUtil.getAppValueDate();
	private boolean isInquiry = false;
	private transient BigDecimal paidAmount = new BigDecimal(0);

	@Autowired
	private FinanceMainDAO financeMainDAO;

	/**
	 * default constructor.<br>
	 */
	public OverdueChargeRecoveryDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "OverdueChargeRecoveryDialog";
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the dialog window we check, if the zul-file is called with a parameter for a
	 * selected OverdueChargeRecovery object in a Map.
	 * 
	 * @param event
	 */
	public void onCreate$window_OverdueChargeRecoveryDialog(Event event) {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_OverdueChargeRecoveryDialog);

		/* set components visible dependent of the users rights */
		doCheckRights();

		if (arguments.containsKey("overdueChargeRecovery")) {
			this.overdueChargeRecovery = (OverdueChargeRecovery) arguments.get("overdueChargeRecovery");
			OverdueChargeRecovery befImage = new OverdueChargeRecovery();
			BeanUtils.copyProperties(this.overdueChargeRecovery, befImage);
			this.overdueChargeRecovery.setBefImage(befImage);

			setOverdueChargeRecovery(this.overdueChargeRecovery);
		} else {
			setOverdueChargeRecovery(null);
		}

		if (arguments.containsKey("inquiry")) {
			this.isInquiry = (Boolean) arguments.get("inquiry");
		}

		doLoadWorkFlow(this.overdueChargeRecovery.isWorkflow(), this.overdueChargeRecovery.getWorkflowId(),
				this.overdueChargeRecovery.getNextTaskId());

		if (isWorkFlowEnabled()) {
			this.userAction = setListRecordStatus(this.userAction);
			getUserWorkspace().allocateRoleAuthorities(getRole(), "OverdueChargeRecoveryDialog");
		}

		// READ OVERHANDED params !
		// we get the overdueChargeRecoveryListWindow controller. So we have access
		// to it and can synchronize the shown data when we do insert, edit or
		// delete overdueChargeRecovery here.
		if (arguments.containsKey("overdueChargeRecoveryListCtrl")) {
			setOverdueChargeRecoveryListCtrl(
					(OverdueChargeRecoveryListCtrl) arguments.get("overdueChargeRecoveryListCtrl"));
		} else {
			setOverdueChargeRecoveryListCtrl(null);
		}

		// set Field Properties
		doSetFieldProperties();
		doShowDialog(getOverdueChargeRecovery());
		logger.debug("Leaving");
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");

		int format = CurrencyUtil.getFormat(getOverdueChargeRecovery().getFinCcy());

		// Empty sent any required attributes
		this.finReference.setMaxlength(20);
		this.finSchdDate.setFormat(PennantConstants.dateFormat);
		this.finStartDate.setFormat(PennantConstants.dateFormat);
		this.finMaturityDate.setFormat(PennantConstants.dateFormat);
		this.finAmt.setMaxlength(18);
		this.finAmt.setFormat(PennantApplicationUtil.getAmountFormate(format));
		this.curFinAmt.setMaxlength(18);
		this.curFinAmt.setFormat(PennantApplicationUtil.getAmountFormate(format));
		this.curSchPriDue.setMaxlength(18);
		this.curSchPriDue.setFormat(PennantApplicationUtil.getAmountFormate(format));
		this.curSchPftDue.setMaxlength(18);
		this.curSchPftDue.setFormat(PennantApplicationUtil.getAmountFormate(format));
		this.totOvrDueChrg.setMaxlength(18);
		this.totOvrDueChrg.setFormat(PennantApplicationUtil.getAmountFormate(format));
		this.totOvrDueChrgWaived.setMaxlength(18);
		this.totOvrDueChrgWaived.setFormat(PennantApplicationUtil.getAmountFormate(format));
		this.totOvrDueChrgPaid.setMaxlength(18);
		this.totOvrDueChrgPaid.setFormat(PennantApplicationUtil.getAmountFormate(format));
		this.totOvrDueChrgBal.setMaxlength(18);
		this.totOvrDueChrgBal.setFormat(PennantApplicationUtil.getAmountFormate(format));
		this.finBrnm.setMaxlength(8);
		this.finType.setMaxlength(8);
		this.finCustId.setMaxlength(19);
		this.finCcy.setMaxlength(3);
		this.finODDate.setFormat(PennantConstants.dateFormat);
		this.finODPri.setMaxlength(18);
		this.finODPri.setFormat(PennantApplicationUtil.getAmountFormate(format));
		this.finODPft.setMaxlength(18);
		this.finODPft.setFormat(PennantApplicationUtil.getAmountFormate(format));
		this.finODTot.setMaxlength(18);
		this.finODTot.setFormat(PennantApplicationUtil.getAmountFormate(format));
		this.finODCRuleCode.setMaxlength(20);
		this.finODCPLAc.setMaxlength(20);
		this.finODCCAc.setMaxlength(20);
		this.finODCPLShare.setMaxlength(5);
		this.finODCPLShare.setFormat(PennantApplicationUtil.getAmountFormate(2));
		this.finODCCustCtg.setMaxlength(8);
		this.finODCOn.setMaxlength(8);
		this.finODC.setMaxlength(18);
		this.finODC.setFormat(PennantApplicationUtil.getAmountFormate(format));
		this.finODCGraceDays.setMaxlength(10);
		this.finODCMaxWaiver.setMaxlength(5);
		this.finODCMaxWaiver.setFormat(PennantApplicationUtil.getAmountFormate(2));
		this.finODCPenalty.setMaxlength(18);
		this.finODCPenalty.setFormat(PennantApplicationUtil.getAmountFormate(format));
		this.finODCWaived.setMaxlength(18);
		this.finODCWaived.setFormat(PennantApplicationUtil.getAmountFormate(format));
		this.finODCPLPenalty.setMaxlength(18);
		this.finODCPLPenalty.setFormat(PennantApplicationUtil.getAmountFormate(format));
		this.finODCCPenalty.setMaxlength(18);
		this.finODCCPenalty.setFormat(PennantApplicationUtil.getAmountFormate(format));
		this.finODCPaid.setMaxlength(18);
		this.finODCPaid.setFormat(PennantApplicationUtil.getAmountFormate(format));
		this.balChrgRecovery.setMaxlength(18);
		this.balChrgRecovery.setFormat(PennantApplicationUtil.getAmountFormate(format));
		this.finODCLastPaidDate.setFormat(PennantConstants.dateFormat);
		this.finODCRecoverySts.setMaxlength(8);

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

		this.btnNew.setVisible(false);
		this.btnEdit.setVisible(false);
		this.btnDelete.setVisible(false);
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_OverdueChargeRecoveryDialog_btnSave"));
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
		MessageUtil.showHelpWindow(event, window_OverdueChargeRecoveryDialog);
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
		if (this.isInquiry) {
			this.window_OverdueChargeRecoveryDialog.onClose();
			this.overdueChargeRecoveryListCtrl.window_OverdueChargeRecoveryList.setVisible(true);
		} else {
			doClose(this.btnSave.isVisible());
		}

	}

	/**
	 * Cancel the actual operation. <br>
	 * <br>
	 * Resets to the original status.<br>
	 * 
	 */
	private void doCancel() {
		logger.debug("Entering");
		doWriteBeanToComponents(this.overdueChargeRecovery.getBefImage());
		doReadOnly();
		this.btnCtrl.setInitEdit();
		logger.debug("Leaving");
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aOverdueChargeRecovery OverdueChargeRecovery
	 */
	public void doWriteBeanToComponents(OverdueChargeRecovery aOverdueChargeRecovery) {
		logger.debug("Entering");

		int format = CurrencyUtil.getFormat(aOverdueChargeRecovery.getFinCcy());
		// Basic Details
		this.finReference.setValue(aOverdueChargeRecovery.getFinReference());
		this.finStartDate.setValue(aOverdueChargeRecovery.getLovDescFinStartDate());
		this.finMaturityDate.setValue(aOverdueChargeRecovery.getLovDescMaturityDate());
		this.finAmt.setValue(CurrencyUtil.parse(aOverdueChargeRecovery.getLovDescFinAmount(), format));
		this.curFinAmt.setValue(CurrencyUtil.parse(aOverdueChargeRecovery.getLovDescCurFinAmt(), format));
		this.curSchPriDue.setValue(CurrencyUtil.parse(aOverdueChargeRecovery.getLovDescCurSchPriDue(), // value1
				format));
		this.curSchPftDue.setValue(CurrencyUtil.parse(aOverdueChargeRecovery.getLovDescCurSchPftDue(), // value2
				format));
		this.totOvrDueChrg.setValue(CurrencyUtil.parse(aOverdueChargeRecovery.getLovDescTotOvrDueChrg(), // value3
				format));
		this.totOvrDueChrgWaived
				.setValue(CurrencyUtil.parse(aOverdueChargeRecovery.getLovDescTotOvrDueChrgWaived(), format));
		this.totOvrDueChrgPaid
				.setValue(CurrencyUtil.parse(aOverdueChargeRecovery.getLovDescTotOvrDueChrgPaid(), format));
		/*
		 * this.totOvrDueChrgBal.setValue(PennantAppUtil.formateAmount(aOverdueChargeRecovery.
		 * getLovDescTotOvrDueChrgBal( ), format));
		 */
		this.totOvrDueChrgBal.setValue(CurrencyUtil.parse(aOverdueChargeRecovery.getLovDescCurSchPriDue()
				.subtract(aOverdueChargeRecovery.getLovDescCurSchPftDue())
				.subtract(aOverdueChargeRecovery.getLovDescTotOvrDueChrg()), format));
		// Overdue Recovery Details
		this.finSchdDate.setValue(aOverdueChargeRecovery.getFinODSchdDate());
		this.finODDate.setValue(aOverdueChargeRecovery.getMovementDate());
		// this.finODCRuleCode.setValue(aOverdueChargeRecovery.getFinODCRuleCode());
		// this.finODCCustCtg.setValue(aOverdueChargeRecovery.getFinODCCustCtg());
		fillComboBox(this.cbFinODFor, aOverdueChargeRecovery.getFinODFor(), finOdForList, "");
		this.finODTot.setValue(CurrencyUtil.parse(aOverdueChargeRecovery.getFinCurODAmt(), format));
		this.finODPri.setValue(CurrencyUtil.parse(aOverdueChargeRecovery.getFinCurODPri(), format));
		this.finODPft.setValue(CurrencyUtil.parse(aOverdueChargeRecovery.getFinCurODPft(), format));
		fillComboBox(this.cbFinODCType, aOverdueChargeRecovery.getPenaltyType(), finODCTypeList, "");
		this.finODCOn.setValue(aOverdueChargeRecovery.getPenaltyCalOn());

		/*
		 * this.finODCPLShare.setValue(aOverdueChargeRecovery.getFinODCPLShare());
		 * this.finODCPLAc.setValue(aOverdueChargeRecovery.getFinODCPLAc());
		 * this.finODCCAc.setValue(aOverdueChargeRecovery.getFinODCCAc());
		 */
		this.finODCPenalty.setValue(CurrencyUtil.parse(aOverdueChargeRecovery.getPenalty(), format));

		// this.finODCAlwWaiver.setChecked(aOverdueChargeRecovery.isFinODCAlwWaiver());
		this.finODCMaxWaiver.setValue(aOverdueChargeRecovery.getMaxWaiver());
		this.finODCWaived.setValue(CurrencyUtil.parse(aOverdueChargeRecovery.getWaivedAmt(), format));
		/*
		 * if(aOverdueChargeRecovery.isFinODCAlwWaiver()) { this.oDCWaivedRow.setVisible(true);
		 * this.oDCAlwWaiverRow.setVisible(true); }else {
		 */
		this.oDCWaivedRow.setVisible(true);// false
		this.oDCAlwWaiverRow.setVisible(true);// false
		// }
		if (this.isInquiry) {
			this.finODCWaived.setReadonly(true);
		}
		/*
		 * this.finODCPLPenalty.setValue(PennantAppUtil.formateAmount(aOverdueChargeRecovery.getFinODCPLPenalty(),
		 * format));
		 * this.finODCCPenalty.setValue(PennantAppUtil.formateAmount(aOverdueChargeRecovery.getFinODCCPenalty(),
		 * format));
		 */
		this.finODCPaid.setValue(CurrencyUtil.parse(aOverdueChargeRecovery.getFinODCPaid(), format));
		// FinODCCPenalty - FinODCPaid - FinODCWaived
		/*
		 * this.balChrgRecovery.setValue(PennantAppUtil.formateAmount(aOverdueChargeRecovery.getFinODCPenalty().
		 * subtract(aOverdueChargeRecovery.getFinODCPaid()).subtract( aOverdueChargeRecovery.getFinODCWaiverPaid()),
		 * format));
		 */
		this.balChrgRecovery.setValue(
				CurrencyUtil.parse(aOverdueChargeRecovery.getPenalty().subtract(aOverdueChargeRecovery.getFinODCPaid())
						.subtract(aOverdueChargeRecovery.getWaivedAmt()), format));

		// Extra fields
		/*
		 * this.finBrnm.setValue(aOverdueChargeRecovery.getFinBranch());
		 * this.finType.setValue(aOverdueChargeRecovery.getFinType());
		 * this.finCustId.setValue(aOverdueChargeRecovery.getFinCustId());
		 */
		this.lovDescCustCIF.setValue(aOverdueChargeRecovery.getLovDescCustCIF());
		/*
		 * this.custShrtName.setValue(aOverdueChargeRecovery.getLovDescCustShrtName());
		 * this.finCcy.setValue(aOverdueChargeRecovery.getFinCcy());
		 * //this.finODCSweep.setChecked(aOverdueChargeRecovery.isFinODCSweep());
		 * this.finODC.setValue(PennantAppUtil.formateAmount(aOverdueChargeRecovery.getFinODC(), format));
		 * this.finODCGraceDays.setValue(aOverdueChargeRecovery.getFinODCGraceDays());
		 * this.finODCLastPaidDate.setValue(aOverdueChargeRecovery.getFinODCLastPaidDate());
		 * this.finODCRecoverySts.setValue(aOverdueChargeRecovery.getFinODCRecoverySts());
		 * this.recordStatus.setValue(aOverdueChargeRecovery.getRecordStatus());
		 */
		if ("R".equals(aOverdueChargeRecovery.getFinODCRecoverySts()) && !this.isInquiry
				&& aOverdueChargeRecovery.getPenaltyBal().compareTo(BigDecimal.ZERO) > 0) {
			// if(aOverdueChargeRecovery.getFinODCRecoverySts().equals("R")&& !this.isInquiry) {
			this.btnRecoverNow.setVisible(true);
		}
		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aOverdueChargeRecovery
	 */
	public void doWriteComponentsToBean(OverdueChargeRecovery aOverdueChargeRecovery) {
		logger.debug("Entering");
		doSetLOVValidation();
		int format = CurrencyUtil.getFormat(aOverdueChargeRecovery.getFinCcy());

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		try {
			aOverdueChargeRecovery.setFinReference(this.finReference.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aOverdueChargeRecovery.setFinODSchdDate(this.finSchdDate.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if ("#".equals(getComboboxValue(this.cbFinODFor))) {
				throw new WrongValueException(this.cbFinODFor, Labels.getLabel("STATIC_INVALID",
						new String[] { Labels.getLabel("label_OverdueChargeRecoveryDialog_FinODFor.value") }));
			}
			aOverdueChargeRecovery.setFinODFor(getComboboxValue(this.cbFinODFor));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		/*
		 * try { aOverdueChargeRecovery.setFinBranch(this.finBrnm.getValue()); }catch (WrongValueException we ) {
		 * wve.add(we); } try { aOverdueChargeRecovery.setFinType(this.finType.getValue()); }catch (WrongValueException
		 * we ) { wve.add(we); } try { aOverdueChargeRecovery.setFinCustId(this.finCustId.getValue()); }catch
		 * (WrongValueException we ) { wve.add(we); } try { aOverdueChargeRecovery.setFinCcy(this.finCcy.getValue());
		 * }catch (WrongValueException we ) { wve.add(we); } try {
		 * aOverdueChargeRecovery.setFinODDate(this.finODDate.getValue()); }catch (WrongValueException we ) {
		 * wve.add(we); } try { if(this.finODPri.getValue()!=null){
		 * aOverdueChargeRecovery.setFinODPri(PennantAppUtil.unFormateAmount(this.finODPri.getValue(), format)); }
		 * }catch (WrongValueException we ) { wve.add(we); } try { if(this.finODPft.getValue()!=null){
		 * aOverdueChargeRecovery.setFinODPft(PennantAppUtil.unFormateAmount(this.finODPft.getValue(), format)); }
		 * }catch (WrongValueException we ) { wve.add(we); } try { if(this.finODTot.getValue()!=null){
		 * aOverdueChargeRecovery.setFinODTot(PennantAppUtil.unFormateAmount(this.finODTot.getValue(), format)); }
		 * }catch (WrongValueException we ) { wve.add(we); } try {
		 * aOverdueChargeRecovery.setFinODCRuleCode(this.finODCRuleCode.getValue()); }catch (WrongValueException we ) {
		 * wve.add(we); }
		 */
		/*
		 * try { aOverdueChargeRecovery.setFinODCPLAc(this.finODCPLAc.getValue()); }catch (WrongValueException we ) {
		 * wve.add(we); } try { aOverdueChargeRecovery.setFinODCCAc(this.finODCCAc.getValue()); }catch
		 * (WrongValueException we ) { wve.add(we); } try { if(this.finODCPLShare.getValue()!=null){
		 * aOverdueChargeRecovery.setFinODCPLShare(this.finODCPLShare.getValue()); } }catch (WrongValueException we ) {
		 * wve.add(we); } try { aOverdueChargeRecovery.setFinODCSweep(this.finODCSweep.isChecked()); }catch
		 * (WrongValueException we ) { wve.add(we); } try {
		 * aOverdueChargeRecovery.setFinODCCustCtg(this.finODCCustCtg.getValue()); }catch (WrongValueException we ) {
		 * wve.add(we); }
		 */
		/*
		 * try { if(getComboboxValue(this.cbFinODCType).equals("#")) { throw new WrongValueException( this.cbFinODCType,
		 * Labels.getLabel( "STATIC_INVALID", new String[] { Labels
		 * .getLabel("label_OverdueChargeRecoveryDialog_FinODCType.value") })); }
		 * aOverdueChargeRecovery.setFinODCType(getComboboxValue(this.cbFinODCType)); } catch (WrongValueException we) {
		 * wve.add(we); } try { aOverdueChargeRecovery.setFinODCOn(this.finODCOn.getValue()); }catch
		 * (WrongValueException we ) { wve.add(we); } try { if(this.finODC.getValue()!=null){
		 * aOverdueChargeRecovery.setFinODC(PennantAppUtil.unFormateAmount(this.finODC.getValue(), format)); } }catch
		 * (WrongValueException we ) { wve.add(we); } try {
		 * aOverdueChargeRecovery.setFinODCGraceDays(this.finODCGraceDays.getValue()); }catch (WrongValueException we )
		 * { wve.add(we); } try { aOverdueChargeRecovery.setFinODCAlwWaiver(this.finODCAlwWaiver.isChecked()); }catch
		 * (WrongValueException we ) { wve.add(we); } try { if(this.finODCMaxWaiver.getValue()!=null){
		 * aOverdueChargeRecovery.setFinODCMaxWaiver(PennantAppUtil.unFormateAmount(this.finODCMaxWaiver.getValue(),
		 * format)); } }catch (WrongValueException we ) { wve.add(we); } try { if(this.finODCPenalty.getValue()!=null){
		 * aOverdueChargeRecovery.setFinODCPenalty(PennantAppUtil.unFormateAmount(this.finODCPenalty.getValue(),
		 * format)); } }catch (WrongValueException we ) { wve.add(we); }
		 */
		try {
			if (this.finODCWaived.getValue() != null) {
				BigDecimal reqWaiver = PennantApplicationUtil.getPercentageValue(this.finODCPenalty.getValue(),
						getOverdueChargeRecovery().getFinODCMaxWaiver());
				if (!this.finODCWaived.isDisabled() && this.finODCWaived.getValue() != null) {
					if (this.finODCWaived.getValue()
							.compareTo(this.finODCPenalty.getValue().subtract(this.finODCPaid.getValue())) > 0
							|| this.finODCWaived.getValue().compareTo(reqWaiver) > 0) {
						throw new WrongValueException(this.finODCWaived,
								Labels.getLabel("FIELD_IS_EQUAL_OR_LESSER",
										new String[] {
												Labels.getLabel("label_OverdueChargeRecoveryDialog_FinODCWaived.value"),
												CurrencyUtil.format(reqWaiver, format) }));
					}
				} else if (this.finODCWaived.getValue() == null) {
					this.finODCWaived.setValue(new BigDecimal(0));
				}
				aOverdueChargeRecovery.setFinODCWaived(CurrencyUtil.unFormat(this.finODCWaived.getValue(), format));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// (FinODCPenalty - FinODCWaived) * FinODCPLShare/100
		/*
		 * try { aOverdueChargeRecovery.setFinODCPLPenalty(PennantAppUtil.unFormateAmount(
		 * this.finODCPLPenalty.getValue(), format)); }catch (WrongValueException we ) { wve.add(we); }
		 * 
		 * //(FinODCPenalty - FinODCWaived) - FinODCPLPenalty try { if(this.finODCCPenalty.getValue()!=null) {
		 * aOverdueChargeRecovery.setFinODCCPenalty(PennantAppUtil.unFormateAmount(this.finODCCPenalty.getValue(),
		 * format)); } }catch (WrongValueException we ) { wve.add(we); }
		 * 
		 * //FinODCPaid + PaidAmount try { if(this.finODCPaid.getValue()!=null) {
		 * aOverdueChargeRecovery.setFinODCPaid(PennantAppUtil.unFormateAmount(this.finODCPaid.getValue(),
		 * format).add(paidAmount)); } }catch (WrongValueException we ) { wve.add(we); }
		 */
		/*
		 * try { aOverdueChargeRecovery.setFinODCLastPaidDate(this.dateValueDate); }catch (WrongValueException we ) {
		 * wve.add(we); }
		 */

		/*
		 * try { if((aOverdueChargeRecovery.getFinODCPenalty().subtract(
		 * aOverdueChargeRecovery.getFinODCPaid())).compareTo(new BigDecimal(0)) == 0){
		 * aOverdueChargeRecovery.setFinODCRecoverySts("C");
		 * aOverdueChargeRecovery.setFinODCWaiverPaid(aOverdueChargeRecovery.getFinODCWaived()); } }catch
		 * (WrongValueException we ) { wve.add(we); }
		 */

		doRemoveValidation();
		doRemoveLOVValidation();

		if (wve.size() > 0) {
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = (WrongValueException) wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}

		aOverdueChargeRecovery.setRecordStatus(this.recordStatus.getValue());
		logger.debug("Leaving");
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the readOnly mode accordingly.
	 * 
	 * @param aOverdueChargeRecovery
	 */
	public void doShowDialog(OverdueChargeRecovery aOverdueChargeRecovery) {
		logger.debug("Entering");

		// set Readonly mode accordingly if the object is new or not.
		if (aOverdueChargeRecovery.isNewRecord()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.finReference.focus();
		} else {
			this.finBrnm.focus();
			if (isWorkFlowEnabled()) {
				this.btnNotes.setVisible(true);
				doEdit();
			} else {
				this.btnCtrl.setInitEdit();
				// Set delete button invisible
				btnDelete.setVisible(false);
				if (!this.isInquiry) {
					btnSave.setVisible(true);
				}
				doEdit();
				btnCancel.setVisible(false);
			}
		}

		try {
			// fill the components with the data
			doWriteBeanToComponents(aOverdueChargeRecovery);

			if (this.isInquiry) {
				this.overdueChargeRecoveryListCtrl.window_OverdueChargeRecoveryList.setVisible(false);
				this.window_OverdueChargeRecoveryDialog.doModal();
			} else {
				setDialog(DialogType.EMBEDDED);
			}

		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving");
	}

	/**
	 * Disables the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering");
		setValidationOn(false);
		this.finReference.setConstraint("");
		this.finSchdDate.setConstraint("");
		this.finBrnm.setConstraint("");
		this.finType.setConstraint("");
		this.finCustId.setConstraint("");
		this.finCcy.setConstraint("");
		this.finODDate.setConstraint("");
		this.finODPri.setConstraint("");
		this.finODPft.setConstraint("");
		this.finODTot.setConstraint("");
		this.finODCRuleCode.setConstraint("");
		this.finODCPLAc.setConstraint("");
		this.finODCCAc.setConstraint("");
		this.finODCPLShare.setConstraint("");
		this.finODCCustCtg.setConstraint("");
		this.finODCOn.setConstraint("");
		this.finODC.setConstraint("");
		this.finODCGraceDays.setConstraint("");
		this.finODCMaxWaiver.setConstraint("");
		this.finODCPenalty.setConstraint("");
		this.finODCWaived.setConstraint("");
		this.finODCPLPenalty.setConstraint("");
		this.finODCCPenalty.setConstraint("");
		this.finODCPaid.setConstraint("");
		this.finODCLastPaidDate.setConstraint("");
		this.finODCRecoverySts.setConstraint("");
		logger.debug("Leaving");
	}

	private void doDelete() throws InterruptedException {
		logger.debug(Literal.ENTERING);

		final OverdueChargeRecovery aOverdueChargeRecovery = new OverdueChargeRecovery();
		BeanUtils.copyProperties(getOverdueChargeRecovery(), aOverdueChargeRecovery);

		doDelete(aOverdueChargeRecovery.getFinReference(), aOverdueChargeRecovery);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug("Entering");

		if (getOverdueChargeRecovery().isNewRecord()) {
			this.finReference.setReadonly(false);
			this.btnCancel.setVisible(false);
		} else {
			this.finReference.setReadonly(true);
			this.btnCancel.setVisible(true);
		}

		readOnlyComponent(true, this.finSchdDate);
		// readOnlyComponent(isReadOnly("OverdueChargeRecoveryDialog_finSchdDate"), this.finSchdDate);
		this.finSchdDate.setButtonVisible(false);
		readOnlyComponent(true, this.cbFinODFor);
		// readOnlyComponent(isReadOnly("OverdueChargeRecoveryDialog_finODFor"), this.cbFinODFor);
		readOnlyComponent(isReadOnly("OverdueChargeRecoveryDialog_finBrnm"), this.finBrnm);
		readOnlyComponent(isReadOnly("OverdueChargeRecoveryDialog_finType"), this.finType);
		readOnlyComponent(isReadOnly("OverdueChargeRecoveryDialog_finCustId"), this.finCustId);
		readOnlyComponent(isReadOnly("OverdueChargeRecoveryDialog_finCcy"), this.finCcy);
		readOnlyComponent(true, this.finODDate);
		// readOnlyComponent(isReadOnly("OverdueChargeRecoveryDialog_finODDate"), this.finODDate);
		this.finODDate.setButtonVisible(false);
		readOnlyComponent(true, this.finODPri);
		readOnlyComponent(true, this.finODPft);
		readOnlyComponent(true, this.finODTot);
		readOnlyComponent(true, this.finODCRuleCode);
		readOnlyComponent(true, this.finODCPLAc);
		readOnlyComponent(true, this.finODCCAc);
		readOnlyComponent(true, this.finODCPLShare);

		/*
		 * readOnlyComponent(isReadOnly("OverdueChargeRecoveryDialog_finODPri"), this.finODPri);
		 * readOnlyComponent(isReadOnly("OverdueChargeRecoveryDialog_finODPft"), this.finODPft);
		 * readOnlyComponent(isReadOnly("OverdueChargeRecoveryDialog_finODTot"), this.finODTot);
		 * readOnlyComponent(isReadOnly("OverdueChargeRecoveryDialog_finODCRuleCode"), this.finODCRuleCode);
		 * readOnlyComponent(isReadOnly("OverdueChargeRecoveryDialog_finODCPLAc"), this.finODCPLAc);
		 * readOnlyComponent(isReadOnly("OverdueChargeRecoveryDialog_finODCCAc"), this.finODCCAc);
		 * readOnlyComponent(isReadOnly("OverdueChargeRecoveryDialog_finODCPLShare"), this.finODCPLShare);
		 */

		readOnlyComponent(isReadOnly("OverdueChargeRecoveryDialog_finODCSweep"), this.finODCSweep);

		readOnlyComponent(true, this.finODCCustCtg);
		readOnlyComponent(true, this.cbFinODCType);
		readOnlyComponent(true, this.finODCOn);

		/*
		 * readOnlyComponent(isReadOnly("OverdueChargeRecoveryDialog_finODCCustCtg"), this.finODCCustCtg);
		 * readOnlyComponent(isReadOnly("OverdueChargeRecoveryDialog_finODCType"), this.cbFinODCType);
		 * readOnlyComponent(isReadOnly("OverdueChargeRecoveryDialog_finODCOn"), this.finODCOn);
		 */

		readOnlyComponent(isReadOnly("OverdueChargeRecoveryDialog_finODC"), this.finODC);
		readOnlyComponent(isReadOnly("OverdueChargeRecoveryDialog_finODCGraceDays"), this.finODCGraceDays);

		readOnlyComponent(true, this.finODCAlwWaiver);
		readOnlyComponent(true, this.finODCMaxWaiver);
		readOnlyComponent(true, this.finODCPenalty);
		/*
		 * readOnlyComponent(isReadOnly("OverdueChargeRecoveryDialog_finODCAlwWaiver"), this.finODCAlwWaiver);
		 * readOnlyComponent(isReadOnly("OverdueChargeRecoveryDialog_finODCMaxWaiver"), this.finODCMaxWaiver);
		 * readOnlyComponent(isReadOnly("OverdueChargeRecoveryDialog_finODCPenalty"), this.finODCPenalty);
		 */

		readOnlyComponent(isReadOnly("OverdueChargeRecoveryDialog_finODCWaived"), this.finODCWaived);

		readOnlyComponent(true, this.finODCPLPenalty);
		readOnlyComponent(true, this.finODCCPenalty);
		readOnlyComponent(true, this.finODCPaid);
		/*
		 * readOnlyComponent(isReadOnly("OverdueChargeRecoveryDialog_finODCPLPenalty"), this.finODCPLPenalty);
		 * readOnlyComponent(isReadOnly("OverdueChargeRecoveryDialog_finODCCPenalty"), this.finODCCPenalty);
		 * readOnlyComponent(isReadOnly("OverdueChargeRecoveryDialog_finODCPaid"), this.finODCPaid);
		 */

		readOnlyComponent(isReadOnly("OverdueChargeRecoveryDialog_finODCLastPaidDate"), this.finODCLastPaidDate);
		readOnlyComponent(isReadOnly("OverdueChargeRecoveryDialog_finODCRecoverySts"), this.finODCRecoverySts);

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}

			if (this.overdueChargeRecovery.isNewRecord()) {
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(false);
			} else {
				this.btnCtrl.setWFBtnStatus_Edit(isFirstTask());
			}
		} /*
			 * else{ this.btnCtrl.setBtnStatus_Edit(); btnCancel.setVisible(true); }
			 */
		logger.debug("Leaving");
	}

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly() {
		logger.debug("Entering");
		this.finReference.setReadonly(true);
		this.finSchdDate.setDisabled(true);
		this.cbFinODFor.setDisabled(true);
		this.finBrnm.setReadonly(true);
		this.finType.setReadonly(true);
		this.finCustId.setReadonly(true);
		this.finCcy.setReadonly(true);
		this.finODDate.setDisabled(true);
		this.finODPri.setReadonly(true);
		this.finODPft.setReadonly(true);
		this.finODTot.setReadonly(true);
		this.finODCRuleCode.setReadonly(true);
		this.finODCPLAc.setReadonly(true);
		this.finODCCAc.setReadonly(true);
		this.finODCPLShare.setReadonly(true);
		this.finODCSweep.setDisabled(true);
		this.finODCCustCtg.setReadonly(true);
		this.cbFinODCType.setDisabled(true);
		this.finODCOn.setReadonly(true);
		this.finODC.setReadonly(true);
		this.finODCGraceDays.setReadonly(true);
		this.finODCAlwWaiver.setDisabled(true);
		this.finODCMaxWaiver.setReadonly(true);
		this.finODCPenalty.setReadonly(true);
		this.finODCWaived.setReadonly(true);
		this.finODCPLPenalty.setReadonly(true);
		this.finODCCPenalty.setReadonly(true);
		this.finODCPaid.setReadonly(true);
		this.finODCLastPaidDate.setDisabled(true);
		this.finODCRecoverySts.setReadonly(true);

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

		this.finReference.setValue("");
		this.finSchdDate.setText("");
		this.finBrnm.setValue("");
		this.finType.setValue("");
		this.finCustId.setText("");
		this.finCcy.setValue("");
		this.finODDate.setText("");
		this.finODPri.setValue("");
		this.finODPft.setValue("");
		this.finODTot.setValue("");
		this.finODCRuleCode.setValue("");
		this.finODCPLAc.setValue("");
		this.finODCCAc.setValue("");
		this.finODCPLShare.setValue("");
		this.finODCSweep.setChecked(false);
		this.finODCCustCtg.setValue("");
		this.finODCOn.setValue("");
		this.finODC.setValue("");
		this.finODCGraceDays.setText("");
		this.finODCAlwWaiver.setChecked(false);
		this.finODCMaxWaiver.setValue("");
		this.finODCPenalty.setValue("");
		this.finODCWaived.setValue("");
		this.finODCPLPenalty.setValue("");
		this.finODCCPenalty.setValue("");
		this.finODCPaid.setValue("");
		this.finODCLastPaidDate.setText("");
		this.finODCRecoverySts.setValue("");
		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table.
	 */
	public void doSave() {
		logger.debug("Entering");
		final OverdueChargeRecovery aOverdueChargeRecovery = new OverdueChargeRecovery();
		BeanUtils.copyProperties(getOverdueChargeRecovery(), aOverdueChargeRecovery);
		boolean isNew = false;

		// fill the OverdueChargeRecovery object with the components data
		doWriteComponentsToBean(aOverdueChargeRecovery);

		// Write the additional validations as per below example
		// get the selected branch object from the listbox
		// Do data level validations here

		isNew = aOverdueChargeRecovery.isNewRecord();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aOverdueChargeRecovery.getRecordType())) {
				aOverdueChargeRecovery.setVersion(aOverdueChargeRecovery.getVersion() + 1);
				if (isNew) {
					aOverdueChargeRecovery.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aOverdueChargeRecovery.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aOverdueChargeRecovery.setNewRecord(true);
				}
			}
		} else {
			aOverdueChargeRecovery.setVersion(aOverdueChargeRecovery.getVersion() + 1);
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}

		// save it to database
		try {

			if (doProcess(aOverdueChargeRecovery, tranType)) {
				refreshList();
				closeDialog();
			}

		} catch (final DataAccessException e) {
			logger.error("Exception: ", e);
			showMessage(e);
		}
		logger.debug("Leaving");
	}

	protected boolean doProcess(OverdueChargeRecovery aOverdueChargeRecovery, String tranType) {
		logger.debug("Entering");
		boolean processCompleted = false;
		AuditHeader auditHeader = null;
		String nextRoleCode = "";

		aOverdueChargeRecovery.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		aOverdueChargeRecovery.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aOverdueChargeRecovery.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			String nextTaskId = "";
			// Upgraded to ZK-6.5.1.1 Added casting to String
			aOverdueChargeRecovery.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aOverdueChargeRecovery.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getNextTaskIds(taskId, aOverdueChargeRecovery);
				}

				if (isNotesMandatory(taskId, aOverdueChargeRecovery)) {
					if (!notesEntered) {
						MessageUtil.showError(Labels.getLabel("Notes_NotEmpty"));
						return false;
					}
				}
			}

			if (StringUtils.isNotBlank(nextTaskId)) {
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

			aOverdueChargeRecovery.setTaskId(taskId);
			aOverdueChargeRecovery.setNextTaskId(nextTaskId);
			aOverdueChargeRecovery.setRoleCode(getRole());
			aOverdueChargeRecovery.setNextRoleCode(nextRoleCode);

			auditHeader = getAuditHeader(aOverdueChargeRecovery, tranType);

			String operationRefs = getServiceOperations(taskId, aOverdueChargeRecovery);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader, null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(aOverdueChargeRecovery, PennantConstants.TRAN_WF);
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		} else {

			auditHeader = getAuditHeader(aOverdueChargeRecovery, tranType);
			processCompleted = doSaveProcess(auditHeader, null);
		}
		logger.debug("return value :" + processCompleted);
		logger.debug("Leaving");
		return processCompleted;
	}

	private boolean doSaveProcess(AuditHeader auditHeader, String method) {
		logger.debug("Entering");
		boolean processCompleted = false;
		int retValue = PennantConstants.porcessOVERIDE;
		boolean deleteNotes = false;

		OverdueChargeRecovery aOverdueChargeRecovery = (OverdueChargeRecovery) auditHeader.getAuditDetail()
				.getModelData();

		while (retValue == PennantConstants.porcessOVERIDE) {

			if (StringUtils.isBlank(method)) {
				if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {
					auditHeader = getOverdueChargeRecoveryService().delete(auditHeader);
					deleteNotes = true;
				} else {
					auditHeader = getOverdueChargeRecoveryService().saveOrUpdate(auditHeader);
				}

			} else {
				if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
					auditHeader = getOverdueChargeRecoveryService().doApprove(auditHeader);

					if (aOverdueChargeRecovery.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
						deleteNotes = true;
					}

				} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
					auditHeader = getOverdueChargeRecoveryService().doReject(auditHeader);
					if (aOverdueChargeRecovery.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
						deleteNotes = true;
					}

				} else {
					auditHeader.setErrorDetails(
							new ErrorDetail(PennantConstants.ERR_9999, Labels.getLabel("InvalidWorkFlowMethod"), null));
					retValue = ErrorControl.showErrorControl(this.window_OverdueChargeRecoveryDialog, auditHeader);
					return processCompleted;
				}
			}

			auditHeader = ErrorControl.showErrorDetails(this.window_OverdueChargeRecoveryDialog, auditHeader);
			retValue = auditHeader.getProcessStatus();

			if (retValue == PennantConstants.porcessCONTINUE) {
				processCompleted = true;

				if (deleteNotes) {
					deleteNotes(getNotes(this.overdueChargeRecovery), true);
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

		logger.debug("return Value:" + processCompleted);
		logger.debug("Leaving");
		return processCompleted;
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public void setValidationOn(boolean validationOn) {
		this.validationOn = validationOn;
	}

	public boolean isValidationOn() {
		return this.validationOn;
	}

	public OverdueChargeRecovery getOverdueChargeRecovery() {
		return this.overdueChargeRecovery;
	}

	public void setOverdueChargeRecovery(OverdueChargeRecovery overdueChargeRecovery) {
		this.overdueChargeRecovery = overdueChargeRecovery;
	}

	public void setOverdueChargeRecoveryService(OverdueChargeRecoveryService overdueChargeRecoveryService) {
		this.overdueChargeRecoveryService = overdueChargeRecoveryService;
	}

	public OverdueChargeRecoveryService getOverdueChargeRecoveryService() {
		return this.overdueChargeRecoveryService;
	}

	public void setOverdueChargeRecoveryListCtrl(OverdueChargeRecoveryListCtrl overdueChargeRecoveryListCtrl) {
		this.overdueChargeRecoveryListCtrl = overdueChargeRecoveryListCtrl;
	}

	public OverdueChargeRecoveryListCtrl getOverdueChargeRecoveryListCtrl() {
		return this.overdueChargeRecoveryListCtrl;
	}

	public PagedListService getPagedListService() {
		return pagedListService;
	}

	public void setPagedListService(PagedListService pagedListService) {
		this.pagedListService = pagedListService;
	}

	private AuditHeader getAuditHeader(OverdueChargeRecovery aOverdueChargeRecovery, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aOverdueChargeRecovery.getBefImage(),
				aOverdueChargeRecovery);
		return new AuditHeader(aOverdueChargeRecovery.getFinReference(), null, null, null, auditDetail,
				aOverdueChargeRecovery.getUserDetails(), getOverideMap());
	}

	private void showMessage(Exception e) {
		AuditHeader auditHeader = new AuditHeader();
		try {
			auditHeader.setErrorDetails(new ErrorDetail(PennantConstants.ERR_UNDEF, e.getMessage(), null));
			ErrorControl.showErrorControl(this.window_OverdueChargeRecoveryDialog, auditHeader);
		} catch (Exception exp) {
			logger.error("Exception: ", exp);
		}
	}

	public void onClick$btnNotes(Event event) {
		doShowNotes(this.overdueChargeRecovery);
	}

	private void doSetLOVValidation() {
	}

	private void doRemoveLOVValidation() {
	}

	@Override
	protected void doClearMessage() {
		logger.debug("Entering");
		this.finODCWaived.setErrorMessage("");
		this.finODCRecoverySts.setErrorMessage("");
		logger.debug("Leaving");
	}

	protected void refreshList() {
		final JdbcSearchObject<OverdueChargeRecovery> soOverdueChargeRecoveryEvent = getOverdueChargeRecoveryListCtrl()
				.getSearchObj();
		getOverdueChargeRecoveryListCtrl().pagingOverdueChargeRecoveryList.setActivePage(0);
		getOverdueChargeRecoveryListCtrl().getPagedListWrapper().setSearchObject(soOverdueChargeRecoveryEvent);
		if (getOverdueChargeRecoveryListCtrl().listBoxOverdueChargeRecovery != null) {
			getOverdueChargeRecoveryListCtrl().listBoxOverdueChargeRecovery.getListModel();
		}
	}

	@Override
	protected String getReference() {
		return String.valueOf(this.overdueChargeRecovery.getFinReference());
	}

	/**
	 * when the "Recover Now" button is clicked. <br>
	 * 
	 * @param event
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	@SuppressWarnings("serial")
	public void onClick$btnRecoverNow(Event event) throws IllegalAccessException, InvocationTargetException {
		logger.debug("Entering" + event.toString());

		int format = CurrencyUtil.getFormat(getOverdueChargeRecovery().getFinCcy());
		this.finODCWaived.clearErrorMessage();
		BigDecimal reqWaiver = PennantApplicationUtil.getPercentageValue(this.finODCPenalty.getValue(),
				getOverdueChargeRecovery().getFinODCMaxWaiver());
		if (!this.finODCWaived.isDisabled() && this.finODCWaived.getValue() != null) {
			if (this.finODCWaived.getValue()
					.compareTo(this.finODCPenalty.getValue().subtract(this.finODCPaid.getValue())) > 0
					|| this.finODCWaived.getValue().compareTo(reqWaiver) > 0) {
				throw new WrongValueException(this.finODCWaived,
						Labels.getLabel("FIELD_IS_EQUAL_OR_LESSER",
								new String[] { Labels.getLabel("label_OverdueChargeRecoveryDialog_FinODCWaived.value"),
										CurrencyUtil.format(reqWaiver, format) }));
			}
		} else if (this.finODCWaived.getValue() == null) {
			this.finODCWaived.setValue(new BigDecimal(0));
		}

		getOverdueChargeRecovery().setFinODCWaived(CurrencyUtil.unFormat(this.finODCWaived.getValue(), format));

		try {

			FinanceMain financeMain = financeMainDAO.getFinanceMainById(getOverdueChargeRecovery().getFinID(), "",
					false);
			Date dateValueDate = SysParamUtil.getAppValueDate();
			Date SchdDate = getOverdueChargeRecovery().getFinODSchdDate();
			String finODFor = getOverdueChargeRecovery().getFinODFor();
			Date movementDate = getOverdueChargeRecovery().getMovementDate();
			BigDecimal penalty = getOverdueChargeRecovery().getPenalty();
			BigDecimal prvPenaltyPaid = getOverdueChargeRecovery().getPenaltyPaid();
			BigDecimal waiverAmt = getOverdueChargeRecovery().getWaivedAmt();
			String chargeType = getOverdueChargeRecovery().getPenaltyType();
			long linkedTranId = Long.MIN_VALUE;
			String finDivision = getFinanceTypeService().getFinanceTypeByFinType(financeMain.getFinType())
					.getFinDivision();// getFinanceTypeService().getFinanceType().getFinDivision();

			List<Object> postingData = getRecoveryPostingsUtil().oDRPostingProcess(financeMain, dateValueDate, SchdDate,
					finODFor, movementDate, penalty, prvPenaltyPaid, waiverAmt, chargeType, linkedTranId, finDivision);
			paidAmount = (BigDecimal) postingData.get(3);

			if (postingData != null && postingData.size() > 2 && postingData.get(2) != null
					&& !("").equals(postingData.get(2))
					&& postingData.get(2).toString().startsWith("Host Connection Failed")) {
				MessageUtil.showError((String) postingData.get(2));
				paidAmount = new BigDecimal(0);
			} else {
				if (paidAmount.compareTo(new BigDecimal(-1)) == 0) {
					MessageUtil.showMessage("Insufficient balance.");
					paidAmount = new BigDecimal(0);
					closeDialog();
				} else {
					this.doSave();
				}
			}
		} catch (InterfaceException e) {
			logger.error("Exception: ", e);
			throw new InterfaceException(e.getErrorCode(), e.getMessage()) {
			};
		} catch (IllegalAccessException e) {
			logger.error("Exception: ", e);
			throw new IllegalAccessException(e.getMessage()) {
			};
		} catch (InvocationTargetException e) {
			logger.error("Exception: ", e);
			throw new InvocationTargetException(e, e.getMessage()) {
			};
		}

		logger.debug("Leaving" + event.toString());
	}

	public void setOverideMap(Map<String, List<ErrorDetail>> overideMap) {
		this.overideMap = overideMap;
	}

	public Map<String, List<ErrorDetail>> getOverideMap() {
		return overideMap;
	}

	public OverdueChargeRecovery getPrvOverdueChargeRecovery() {
		return prvOverdueChargeRecovery;
	}

	public OverDueRecoveryPostingsUtil getRecoveryPostingsUtil() {
		return recoveryPostingsUtil;
	}

	public void setRecoveryPostingsUtil(OverDueRecoveryPostingsUtil recoveryPostingsUtil) {
		this.recoveryPostingsUtil = recoveryPostingsUtil;
	}

	public FinanceTypeService getFinanceTypeService() {
		return financeTypeService;
	}

	public void setFinanceTypeService(FinanceTypeService financeTypeService) {
		this.financeTypeService = financeTypeService;
	}

}
