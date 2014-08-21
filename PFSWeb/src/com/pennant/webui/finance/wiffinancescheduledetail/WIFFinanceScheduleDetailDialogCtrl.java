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

/**
 ********************************************************************************************
 *                                 FILE HEADER                                              *
 ********************************************************************************************
 *																							*
 * FileName    		:  WIFFinanceScheduleDetailDialogCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  12-11-2011    														*
 *                                                                  						*
 * Modified Date    :  12-11-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 12-11-2011       Pennant	                 0.1                                            * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 ********************************************************************************************
*/

package com.pennant.webui.finance.wiffinancescheduledetail;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataAccessException;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Row;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.Notes;
import com.pennant.backend.model.applicationmaster.BaseRate;
import com.pennant.backend.model.applicationmaster.SplRateCode;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.service.PagedListService;
import com.pennant.backend.service.finance.WIFFinanceScheduleDetailService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.util.ErrorControl;
import com.pennant.util.PennantAppUtil;
import com.pennant.util.Constraint.AmountValidator;
import com.pennant.util.Constraint.IntValidator;
import com.pennant.webui.util.ButtonStatusCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.MultiLineMessageBox;
import com.pennant.webui.util.PTMessageUtils;
import com.pennant.webui.util.searchdialogs.ExtendedSearchListBox;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * /WEB-INF/pages/Finance/WIFFinanceScheduleDetail/wIFFinanceScheduleDetailDialog.zul file. <br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 */
public class WIFFinanceScheduleDetailDialogCtrl extends GFCBaseCtrl implements Serializable {

	private static final long serialVersionUID = 1L;
	private final static Logger logger = Logger.getLogger(WIFFinanceScheduleDetailDialogCtrl.class);
	
	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window window_WIFFinanceScheduleDetailDialog; // autowired
	protected Textbox finReference; // autowired
  	protected Datebox schDate; // autowired
   	protected Intbox schSeq; // autowired
	protected Checkbox pftOnSchDate; // autowired
	protected Checkbox cpzOnSchDate; // autowired
	protected Checkbox repayOnSchDate; // autowired
	protected Checkbox rvwOnSchDate; // autowired
	protected Checkbox disbOnSchDate; // autowired
	protected Checkbox downpaymentOnSchDate; // autowired
	protected Decimalbox balanceForPftCal; // autowired
   protected Textbox baseRate; // autowired
   protected Textbox splRate; // autowired
	protected Decimalbox actRate; // autowired
	protected Decimalbox adjRate; // autowired
   	protected Intbox noOfDays; // autowired
	protected Decimalbox dayFactor; // autowired
	protected Decimalbox profitCalc; // autowired
	protected Decimalbox profitSchd; // autowired
	protected Decimalbox principalSchd; // autowired
	protected Decimalbox repayAmount; // autowired
	protected Decimalbox profitBalance; // autowired
	protected Decimalbox disbAmount; // autowired
	protected Decimalbox downPaymentAmount; // autowired
	protected Decimalbox cpzAmount; // autowired
	protected Decimalbox diffProfitSchd; // autowired
	protected Decimalbox dIffPrincipalSchd; // autowired
	protected Decimalbox closingBalance; // autowired
	protected Decimalbox profitFraction; // autowired
	protected Decimalbox prvRepayAmount; // autowired
	protected Decimalbox deffProfitBal; // autowired
	protected Decimalbox diffPrincipalBal; // autowired
	protected Decimalbox schdPriPaid; // autowired
	protected Checkbox isSchdPftPaid; // autowired

	protected Label recordStatus; // autowired
	protected Radiogroup userAction;
	protected Groupbox groupboxWf;
	protected Row statusRow;

	// not auto wired vars
	private FinanceScheduleDetail wIFFinanceScheduleDetail; // overhanded per param
	private FinanceScheduleDetail prvWIFFinanceScheduleDetail; // overhanded per param
	private transient WIFFinanceScheduleDetailListCtrl wIFFinanceScheduleDetailListCtrl; // overhanded per param

	// old value vars for edit mode. that we can check if something
	// on the values are edited since the last init.
	private transient String  		oldVar_finReference;
	private transient Date  		oldVar_schDate;
	private transient int  		oldVar_schSeq;
	private transient boolean  		oldVar_pftOnSchDate;
	private transient boolean  		oldVar_cpzOnSchDate;
	private transient boolean  		oldVar_repayOnSchDate;
	private transient boolean  		oldVar_rvwOnSchDate;
	private transient boolean  		oldVar_disbOnSchDate;
	private transient boolean  		oldVar_downpaymentOnSchDate;
	private transient BigDecimal  		oldVar_balanceForPftCal;
	private transient String  		oldVar_baseRate;
	private transient String  		oldVar_splRate;
	private transient BigDecimal  		oldVar_actRate;
	private transient BigDecimal  		oldVar_adjRate;
	private transient int  		oldVar_noOfDays;
	private transient BigDecimal  		oldVar_dayFactor;
	private transient BigDecimal  		oldVar_profitCalc;
	private transient BigDecimal  		oldVar_profitSchd;
	private transient BigDecimal  		oldVar_principalSchd;
	private transient BigDecimal  		oldVar_repayAmount;
	private transient BigDecimal  		oldVar_profitBalance;
	private transient BigDecimal  		oldVar_disbAmount;
	private transient BigDecimal  		oldVar_downPaymentAmount;
	private transient BigDecimal  		oldVar_cpzAmount;
	private transient BigDecimal  		oldVar_diffProfitSchd;
	private transient BigDecimal  		oldVar_dIffPrincipalSchd;
	private transient BigDecimal  		oldVar_closingBalance;
	private transient BigDecimal  		oldVar_profitFraction;
	private transient BigDecimal  		oldVar_prvRepayAmount;
	private transient BigDecimal  		oldVar_deffProfitBal;
	private transient BigDecimal  		oldVar_diffPrincipalBal;
	private transient BigDecimal  		oldVar_schdPriPaid;
	private transient boolean  		oldVar_isSchdPftPaid;
	private transient String oldVar_recordStatus;

	private transient boolean validationOn;
	private boolean notes_Entered=false;

	// Button controller for the CRUD buttons
	private transient final String btnCtroller_ClassPrefix = "button_WIFFinanceScheduleDetailDialog_";
	private transient ButtonStatusCtrl btnCtrl;
	protected Button btnNew; // autowire
	protected Button btnEdit; // autowire
	protected Button btnDelete; // autowire
	protected Button btnSave; // autowire
	protected Button btnCancel; // autowire
	protected Button btnClose; // autowire
	protected Button btnHelp; // autowire
	protected Button btnNotes; // autowire
	
	protected Button btnSearchBaseRate; // autowire
	protected Textbox lovDescBaseRateName;
	private transient String 		oldVar_lovDescBaseRateName;
	protected Button btnSearchSplRate; // autowire
	protected Textbox lovDescSplRateName;
	private transient String 		oldVar_lovDescSplRateName;
	
	// ServiceDAOs / Domain Classes
	private transient WIFFinanceScheduleDetailService wIFFinanceScheduleDetailService;
	private transient PagedListService pagedListService;
	private HashMap<String, ArrayList<ErrorDetails>> overideMap= new HashMap<String, ArrayList<ErrorDetails>>();
	

	/**
	 * default constructor.<br>
	 */
	public WIFFinanceScheduleDetailDialogCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * zul-file is called with a parameter for a selected WIFFinanceScheduleDetail object in a
	 * Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_WIFFinanceScheduleDetailDialog(Event event) throws Exception {
		logger.debug(event.toString());

		/* set components visible dependent of the users rights */
		doCheckRights();
		
		/* create the Button Controller. Disable not used buttons during working */
		this.btnCtrl = new ButtonStatusCtrl(getUserWorkspace(), this.btnCtroller_ClassPrefix, true, this.btnNew,
				this.btnEdit, this.btnDelete, this.btnSave, this.btnCancel, this.btnClose,this.btnNotes);

		// get the params map that are overhanded by creation.
		final Map<String, Object> args = getCreationArgsMap(event);
		
		// READ OVERHANDED params !
		if (args.containsKey("wIFFinanceScheduleDetail")) {
			this.wIFFinanceScheduleDetail = (FinanceScheduleDetail) args.get("wIFFinanceScheduleDetail");
			FinanceScheduleDetail befImage =new FinanceScheduleDetail();
			BeanUtils.copyProperties(this.wIFFinanceScheduleDetail, befImage);
			this.wIFFinanceScheduleDetail.setBefImage(befImage);
			
			setWIFFinanceScheduleDetail(this.wIFFinanceScheduleDetail);
		} else {
			setWIFFinanceScheduleDetail(null);
		}
	
		doLoadWorkFlow(this.wIFFinanceScheduleDetail.isWorkflow(),this.wIFFinanceScheduleDetail.getWorkflowId(),this.wIFFinanceScheduleDetail.getNextTaskId());

		if (isWorkFlowEnabled()){
			this.userAction	= setListRecordStatus(this.userAction);
			getUserWorkspace().alocateRoleAuthorities(getRole(), "WIFFinanceScheduleDetailDialog");
		}

	
		// READ OVERHANDED params !
		// we get the wIFFinanceScheduleDetailListWindow controller. So we have access
		// to it and can synchronize the shown data when we do insert, edit or
		// delete wIFFinanceScheduleDetail here.
		if (args.containsKey("wIFFinanceScheduleDetailListCtrl")) {
			setWIFFinanceScheduleDetailListCtrl((WIFFinanceScheduleDetailListCtrl) args.get("wIFFinanceScheduleDetailListCtrl"));
		} else {
			setWIFFinanceScheduleDetailListCtrl(null);
		}

		// set Field Properties
		doSetFieldProperties();
		doShowDialog(getWIFFinanceScheduleDetail());
		logger.debug("Leaving");
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering") ;
		//Empty sent any required attributes
		this.finReference.setMaxlength(20);
	  	this.schDate.setFormat(PennantConstants.dateFormat);
		this.schSeq.setMaxlength(10);
	  	this.balanceForPftCal.setMaxlength(18);
	  	this.balanceForPftCal.setFormat(PennantApplicationUtil.getAmountFormate(0));
	  	this.balanceForPftCal.setRoundingMode(BigDecimal.ROUND_DOWN);
	  	this.balanceForPftCal.setScale(0);
		this.baseRate.setMaxlength(8);
		this.splRate.setMaxlength(8);
	  	this.actRate.setMaxlength(13);
	  	this.actRate.setFormat(PennantApplicationUtil.getAmountFormate(9));
	  	this.actRate.setRoundingMode(BigDecimal.ROUND_DOWN);
	  	this.actRate.setScale(9);
	  	this.adjRate.setMaxlength(13);
	  	this.adjRate.setFormat(PennantApplicationUtil.getAmountFormate(9));
	  	this.adjRate.setRoundingMode(BigDecimal.ROUND_DOWN);
	  	this.adjRate.setScale(9);
		this.noOfDays.setMaxlength(10);
	  	this.dayFactor.setMaxlength(18);
	  	this.dayFactor.setFormat(PennantApplicationUtil.getAmountFormate(0));
	  	this.dayFactor.setRoundingMode(BigDecimal.ROUND_DOWN);
	  	this.dayFactor.setScale(0);
	  	this.profitCalc.setMaxlength(18);
	  	this.profitCalc.setFormat(PennantApplicationUtil.getAmountFormate(0));
	  	this.profitCalc.setRoundingMode(BigDecimal.ROUND_DOWN);
	  	this.profitCalc.setScale(0);
	  	this.profitSchd.setMaxlength(18);
	  	this.profitSchd.setFormat(PennantApplicationUtil.getAmountFormate(0));
	  	this.profitSchd.setRoundingMode(BigDecimal.ROUND_DOWN);
	  	this.profitSchd.setScale(0);
	  	this.principalSchd.setMaxlength(18);
	  	this.principalSchd.setFormat(PennantApplicationUtil.getAmountFormate(0));
	  	this.principalSchd.setRoundingMode(BigDecimal.ROUND_DOWN);
	  	this.principalSchd.setScale(0);
	  	this.repayAmount.setMaxlength(18);
	  	this.repayAmount.setFormat(PennantApplicationUtil.getAmountFormate(0));
	  	this.repayAmount.setRoundingMode(BigDecimal.ROUND_DOWN);
	  	this.repayAmount.setScale(0);
	  	this.profitBalance.setMaxlength(18);
	  	this.profitBalance.setFormat(PennantApplicationUtil.getAmountFormate(0));
	  	this.profitBalance.setRoundingMode(BigDecimal.ROUND_DOWN);
	  	this.profitBalance.setScale(0);
	  	this.disbAmount.setMaxlength(18);
	  	this.disbAmount.setFormat(PennantApplicationUtil.getAmountFormate(0));
	  	this.disbAmount.setRoundingMode(BigDecimal.ROUND_DOWN);
	  	this.disbAmount.setScale(0);
	  	this.downPaymentAmount.setMaxlength(18);
	  	this.downPaymentAmount.setFormat(PennantApplicationUtil.getAmountFormate(0));
	  	this.downPaymentAmount.setRoundingMode(BigDecimal.ROUND_DOWN);
	  	this.downPaymentAmount.setScale(0);
	  	this.cpzAmount.setMaxlength(18);
	  	this.cpzAmount.setFormat(PennantApplicationUtil.getAmountFormate(0));
	  	this.cpzAmount.setRoundingMode(BigDecimal.ROUND_DOWN);
	  	this.cpzAmount.setScale(0);
	  	this.diffProfitSchd.setMaxlength(18);
	  	this.diffProfitSchd.setFormat(PennantApplicationUtil.getAmountFormate(0));
	  	this.diffProfitSchd.setRoundingMode(BigDecimal.ROUND_DOWN);
	  	this.diffProfitSchd.setScale(0);
	  	this.dIffPrincipalSchd.setMaxlength(18);
	  	this.dIffPrincipalSchd.setFormat(PennantApplicationUtil.getAmountFormate(0));
	  	this.dIffPrincipalSchd.setRoundingMode(BigDecimal.ROUND_DOWN);
	  	this.dIffPrincipalSchd.setScale(0);
	  	this.closingBalance.setMaxlength(18);
	  	this.closingBalance.setFormat(PennantApplicationUtil.getAmountFormate(0));
	  	this.closingBalance.setRoundingMode(BigDecimal.ROUND_DOWN);
	  	this.closingBalance.setScale(0);
	  	this.profitFraction.setMaxlength(18);
	  	this.profitFraction.setFormat(PennantApplicationUtil.getAmountFormate(0));
	  	this.profitFraction.setRoundingMode(BigDecimal.ROUND_DOWN);
	  	this.profitFraction.setScale(0);
	  	this.prvRepayAmount.setMaxlength(18);
	  	this.prvRepayAmount.setFormat(PennantApplicationUtil.getAmountFormate(0));
	  	this.prvRepayAmount.setRoundingMode(BigDecimal.ROUND_DOWN);
	  	this.prvRepayAmount.setScale(0);
	  	this.deffProfitBal.setMaxlength(18);
	  	this.deffProfitBal.setFormat(PennantApplicationUtil.getAmountFormate(0));
	  	this.deffProfitBal.setRoundingMode(BigDecimal.ROUND_DOWN);
	  	this.deffProfitBal.setScale(0);
	  	this.diffPrincipalBal.setMaxlength(18);
	  	this.diffPrincipalBal.setFormat(PennantApplicationUtil.getAmountFormate(0));
	  	this.diffPrincipalBal.setRoundingMode(BigDecimal.ROUND_DOWN);
	  	this.diffPrincipalBal.setScale(0);
	  	this.schdPriPaid.setMaxlength(18);
	  	this.schdPriPaid.setFormat(PennantApplicationUtil.getAmountFormate(0));
	  	this.schdPriPaid.setRoundingMode(BigDecimal.ROUND_DOWN);
	  	this.schdPriPaid.setScale(0);
		
		if (isWorkFlowEnabled()){
			this.groupboxWf.setVisible(true);
			this.statusRow.setVisible(true);
		}else{
			this.groupboxWf.setVisible(false);
			this.statusRow.setVisible(false);
		}
		
		logger.debug("Leaving") ;
	}

	/**
	 * User rights check. <br>
	 * Only components are set visible=true if the logged-in <br>
	 * user have the right for it. <br>
	 * 
	 * The rights are get from the spring framework users grantedAuthority(). A
	 * right is only a string. <br>
	 */
	private void doCheckRights() {
		logger.debug("Entering") ;
		
		getUserWorkspace().alocateAuthorities("WIFFinanceScheduleDetailDialog");
		
		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_WIFFinanceScheduleDetailDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_WIFFinanceScheduleDetailDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_WIFFinanceScheduleDetailDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_WIFFinanceScheduleDetailDialog_btnSave"));
		this.btnCancel.setVisible(false);
		
		logger.debug("Leaving") ;
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// +++++++++++++++++++++++ Components events +++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * If we close the dialog window. <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClose$window_WIFFinanceScheduleDetailDialog(Event event) throws Exception {
		logger.debug(event.toString());
		doClose();
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
		PTMessageUtils.showHelpWindow(event, window_WIFFinanceScheduleDetailDialog);
		logger.debug("Leaving");
	}

	/**
	 * when the "new" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnNew(Event event) {
		logger.debug(event.toString());
		doNew();
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
	 * when the "close" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnClose(Event event) throws InterruptedException {
		logger.debug(event.toString());

		try {
			doClose();
		} catch (final WrongValuesException e) {
			throw e;
		}
		logger.debug("Leaving");
	}

	// GUI Process

	
	/**
	 * Closes the dialog window. <br>
	 * <br>
	 * Before closing we check if there are unsaved changes in <br>
	 * the components and ask the user if saving the modifications. <br>
	 * 
	 * @throws InterruptedException
	 * 
	 */
	private void doClose() throws InterruptedException {
		logger.debug("Entering");
		boolean close=true;
		if (isDataChanged()) {
			logger.debug("isDataChanged : true");

			// Show a confirm box
			final String msg = Labels.getLabel("message_Data_Modified_Save_Data_YesNo");
			final String title = Labels.getLabel("message.Information");

			MultiLineMessageBox.doSetTemplate();
			int conf = MultiLineMessageBox.show(msg, title, MultiLineMessageBox.YES| MultiLineMessageBox.NO, MultiLineMessageBox.QUESTION,true);

			if (conf==MultiLineMessageBox.YES){
				logger.debug("doClose: Yes");
				doSave();
				close=false;
			}else{
				logger.debug("doClose: No");
			}
		}else{
			logger.debug("isDataChanged : false");
		}
		
		if(close){
			closeDialog(this.window_WIFFinanceScheduleDetailDialog, "WIFFinanceScheduleDetail");	
		}

		logger.debug("Leaving") ;
	}

	/**
	 * Cancel the actual operation. <br>
	 * <br>
	 * Resets to the original status.<br>
	 * 
	 */
	private void doCancel() {
		logger.debug("Entering") ;
		doResetInitValues();
		doReadOnly();
		this.btnCtrl.setInitEdit();
		logger.debug("Leaving") ;
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aWIFFinanceScheduleDetail
	 *            WIFFinanceScheduleDetail
	 */
	public void doWriteBeanToComponents(FinanceScheduleDetail aWIFFinanceScheduleDetail) {
		logger.debug("Entering") ;
		this.finReference.setValue(aWIFFinanceScheduleDetail.getFinReference());
		this.schDate.setValue(aWIFFinanceScheduleDetail.getSchDate());
		this.schSeq.setValue(aWIFFinanceScheduleDetail.getSchSeq());
		this.pftOnSchDate.setChecked(aWIFFinanceScheduleDetail.isPftOnSchDate());
		this.cpzOnSchDate.setChecked(aWIFFinanceScheduleDetail.isCpzOnSchDate());
		this.repayOnSchDate.setChecked(aWIFFinanceScheduleDetail.isRepayOnSchDate());
		this.rvwOnSchDate.setChecked(aWIFFinanceScheduleDetail.isRvwOnSchDate());
		this.disbOnSchDate.setChecked(aWIFFinanceScheduleDetail.isDisbOnSchDate());
		this.downpaymentOnSchDate.setChecked(aWIFFinanceScheduleDetail.isDownpaymentOnSchDate());
  		this.balanceForPftCal.setValue(PennantAppUtil.formateAmount(aWIFFinanceScheduleDetail.getBalanceForPftCal(),0));
	   this.baseRate.setValue(aWIFFinanceScheduleDetail.getBaseRate());
	   this.splRate.setValue(aWIFFinanceScheduleDetail.getSplRate());
  		this.actRate.setValue(PennantAppUtil.formateAmount(aWIFFinanceScheduleDetail.getActRate(),9));
		this.noOfDays.setValue(aWIFFinanceScheduleDetail.getNoOfDays());
  		this.dayFactor.setValue(PennantAppUtil.formateAmount(aWIFFinanceScheduleDetail.getDayFactor(),0));
  		this.profitCalc.setValue(PennantAppUtil.formateAmount(aWIFFinanceScheduleDetail.getProfitCalc(),0));
  		this.profitSchd.setValue(PennantAppUtil.formateAmount(aWIFFinanceScheduleDetail.getProfitSchd(),0));
  		this.principalSchd.setValue(PennantAppUtil.formateAmount(aWIFFinanceScheduleDetail.getPrincipalSchd(),0));
  		this.repayAmount.setValue(PennantAppUtil.formateAmount(aWIFFinanceScheduleDetail.getRepayAmount(),0));
  		this.profitBalance.setValue(PennantAppUtil.formateAmount(aWIFFinanceScheduleDetail.getProfitBalance(),0));
  		this.disbAmount.setValue(PennantAppUtil.formateAmount(aWIFFinanceScheduleDetail.getDisbAmount(),0));
  		this.downPaymentAmount.setValue(PennantAppUtil.formateAmount(aWIFFinanceScheduleDetail.getDownPaymentAmount(),0));
  		this.cpzAmount.setValue(PennantAppUtil.formateAmount(aWIFFinanceScheduleDetail.getCpzAmount(),0));
  		this.diffProfitSchd.setValue(PennantAppUtil.formateAmount(aWIFFinanceScheduleDetail.getDefProfitSchd(),0));
  		this.dIffPrincipalSchd.setValue(PennantAppUtil.formateAmount(aWIFFinanceScheduleDetail.getDefPrincipalSchd(),0));
  		this.closingBalance.setValue(PennantAppUtil.formateAmount(aWIFFinanceScheduleDetail.getClosingBalance(),0));
  		this.profitFraction.setValue(PennantAppUtil.formateAmount(aWIFFinanceScheduleDetail.getProfitFraction(),0));
  		this.prvRepayAmount.setValue(PennantAppUtil.formateAmount(aWIFFinanceScheduleDetail.getPrvRepayAmount(),0));
  		this.deffProfitBal.setValue(PennantAppUtil.formateAmount(aWIFFinanceScheduleDetail.getDefProfitBal(),0));
  		this.diffPrincipalBal.setValue(PennantAppUtil.formateAmount(aWIFFinanceScheduleDetail.getDefPrincipalBal(),0));
  		this.schdPriPaid.setValue(PennantAppUtil.formateAmount(aWIFFinanceScheduleDetail.getSchdPriPaid(),0));
		this.isSchdPftPaid.setChecked(aWIFFinanceScheduleDetail.isSchPftPaid());
	
	if (aWIFFinanceScheduleDetail.isNewRecord()){
		   this.lovDescBaseRateName.setValue("");
		   this.lovDescSplRateName.setValue("");
	}else{
		   //this.lovDescBaseRateName.setValue(aWIFFinanceScheduleDetail.getBaseRate()+"-"+aWIFFinanceScheduleDetail.getLovDescBaseRateName());
		   //this.lovDescSplRateName.setValue(aWIFFinanceScheduleDetail.getSplRate()+"-"+aWIFFinanceScheduleDetail.getLovDescSplRateName());
	}
		this.recordStatus.setValue(aWIFFinanceScheduleDetail.getRecordStatus());
		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aWIFFinanceScheduleDetail
	 */
	public void doWriteComponentsToBean(FinanceScheduleDetail aWIFFinanceScheduleDetail) {
		logger.debug("Entering") ;
		doSetLOVValidation();
		
		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();
		
		try {
		    aWIFFinanceScheduleDetail.setFinReference(this.finReference.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
		    aWIFFinanceScheduleDetail.setSchDate(this.schDate.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
		    aWIFFinanceScheduleDetail.setSchSeq(this.schSeq.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aWIFFinanceScheduleDetail.setPftOnSchDate(this.pftOnSchDate.isChecked());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aWIFFinanceScheduleDetail.setCpzOnSchDate(this.cpzOnSchDate.isChecked());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aWIFFinanceScheduleDetail.setRepayOnSchDate(this.repayOnSchDate.isChecked());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aWIFFinanceScheduleDetail.setRvwOnSchDate(this.rvwOnSchDate.isChecked());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aWIFFinanceScheduleDetail.setDisbOnSchDate(this.disbOnSchDate.isChecked());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aWIFFinanceScheduleDetail.setDownpaymentOnSchDate(this.downpaymentOnSchDate.isChecked());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			if(this.balanceForPftCal.getValue()!=null){
			 	aWIFFinanceScheduleDetail.setBalanceForPftCal(PennantAppUtil.unFormateAmount(this.balanceForPftCal.getValue(), 0));
			}
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
	 		//aWIFFinanceScheduleDetail.setLovDescBaseRateName(this.lovDescBaseRateName.getValue());
	 		aWIFFinanceScheduleDetail.setBaseRate(this.baseRate.getValue());	
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
	 		//aWIFFinanceScheduleDetail.setLovDescSplRateName(this.lovDescSplRateName.getValue());
	 		aWIFFinanceScheduleDetail.setSplRate(this.splRate.getValue());	
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			if(this.actRate.getValue()!=null){
			 	aWIFFinanceScheduleDetail.setActRate(PennantAppUtil.unFormateAmount(this.actRate.getValue(), 9));
			}
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
		    aWIFFinanceScheduleDetail.setNoOfDays(this.noOfDays.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			if(this.dayFactor.getValue()!=null){
			 	aWIFFinanceScheduleDetail.setDayFactor(PennantAppUtil.unFormateAmount(this.dayFactor.getValue(), 0));
			}
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			if(this.profitCalc.getValue()!=null){
			 	aWIFFinanceScheduleDetail.setProfitCalc(PennantAppUtil.unFormateAmount(this.profitCalc.getValue(), 0));
			}
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			if(this.profitSchd.getValue()!=null){
			 	aWIFFinanceScheduleDetail.setProfitSchd(PennantAppUtil.unFormateAmount(this.profitSchd.getValue(), 0));
			}
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			if(this.principalSchd.getValue()!=null){
			 	aWIFFinanceScheduleDetail.setPrincipalSchd(PennantAppUtil.unFormateAmount(this.principalSchd.getValue(), 0));
			}
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			if(this.repayAmount.getValue()!=null){
			 	aWIFFinanceScheduleDetail.setRepayAmount(PennantAppUtil.unFormateAmount(this.repayAmount.getValue(), 0));
			}
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			if(this.profitBalance.getValue()!=null){
			 	aWIFFinanceScheduleDetail.setProfitBalance(PennantAppUtil.unFormateAmount(this.profitBalance.getValue(), 0));
			}
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			if(this.disbAmount.getValue()!=null){
			 	aWIFFinanceScheduleDetail.setDisbAmount(PennantAppUtil.unFormateAmount(this.disbAmount.getValue(), 0));
			}
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			if(this.downPaymentAmount.getValue()!=null){
			 	aWIFFinanceScheduleDetail.setDownPaymentAmount(PennantAppUtil.unFormateAmount(this.downPaymentAmount.getValue(), 0));
			}
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			if(this.cpzAmount.getValue()!=null){
			 	aWIFFinanceScheduleDetail.setCpzAmount(PennantAppUtil.unFormateAmount(this.cpzAmount.getValue(), 0));
			}
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			if(this.diffProfitSchd.getValue()!=null){
			 	aWIFFinanceScheduleDetail.setDefProfitSchd(PennantAppUtil.unFormateAmount(this.diffProfitSchd.getValue(), 0));
			}
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			if(this.dIffPrincipalSchd.getValue()!=null){
			 	aWIFFinanceScheduleDetail.setDefPrincipalSchd(PennantAppUtil.unFormateAmount(this.dIffPrincipalSchd.getValue(), 0));
			}
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			if(this.closingBalance.getValue()!=null){
			 	aWIFFinanceScheduleDetail.setClosingBalance(PennantAppUtil.unFormateAmount(this.closingBalance.getValue(), 0));
			}
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			if(this.profitFraction.getValue()!=null){
			 	aWIFFinanceScheduleDetail.setProfitFraction(PennantAppUtil.unFormateAmount(this.profitFraction.getValue(), 0));
			}
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			if(this.prvRepayAmount.getValue()!=null){
			 	aWIFFinanceScheduleDetail.setPrvRepayAmount(PennantAppUtil.unFormateAmount(this.prvRepayAmount.getValue(), 0));
			}
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			if(this.deffProfitBal.getValue()!=null){
			 	aWIFFinanceScheduleDetail.setDefProfitBal(PennantAppUtil.unFormateAmount(this.deffProfitBal.getValue(), 0));
			}
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			if(this.diffPrincipalBal.getValue()!=null){
			 	aWIFFinanceScheduleDetail.setDefPrincipalBal(PennantAppUtil.unFormateAmount(this.diffPrincipalBal.getValue(), 0));
			}
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			if(this.schdPriPaid.getValue()!=null){
			 	aWIFFinanceScheduleDetail.setSchdPriPaid(PennantAppUtil.unFormateAmount(this.schdPriPaid.getValue(), 0));
			}
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aWIFFinanceScheduleDetail.setSchPftPaid(this.isSchdPftPaid.isChecked());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		
		doRemoveValidation();
		doRemoveLOVValidation();
		
		if (wve.size()>0) {
			WrongValueException [] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = (WrongValueException) wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}
		
		aWIFFinanceScheduleDetail.setRecordStatus(this.recordStatus.getValue());
		logger.debug("Leaving");
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the
	 * readOnly mode accordingly.
	 * 
	 * @param aWIFFinanceScheduleDetail
	 * @throws InterruptedException
	 */
	public void doShowDialog(FinanceScheduleDetail aWIFFinanceScheduleDetail) throws InterruptedException {
		logger.debug("Entering") ;
		
		// if aWIFFinanceScheduleDetail == null then we opened the Dialog without
		// args for a given entity, so we get a new Obj().
		if (aWIFFinanceScheduleDetail == null) {
			/** !!! DO NOT BREAK THE TIERS !!! */
			// We don't create a new DomainObject() in the frontend.
			// We GET it from the backend.
			aWIFFinanceScheduleDetail = getWIFFinanceScheduleDetailService().getNewWIFFinanceScheduleDetail();
			
			setWIFFinanceScheduleDetail(aWIFFinanceScheduleDetail);
		} else {
			setWIFFinanceScheduleDetail(aWIFFinanceScheduleDetail);
		}

		// set Readonly mode accordingly if the object is new or not.
		if (aWIFFinanceScheduleDetail.isNew()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.finReference.focus();
		} else {
			this.pftOnSchDate.focus();
			if (isWorkFlowEnabled()){
				this.btnNotes.setVisible(true);
				doEdit();
			}else{
				this.btnCtrl.setInitEdit();
				doReadOnly();
				btnCancel.setVisible(false);
			}
		}

		try {
			// fill the components with the data
			doWriteBeanToComponents(aWIFFinanceScheduleDetail);

			// stores the initial data for comparing if they are changed
			// during user action.
			doStoreInitValues();
			setDialog(this.window_WIFFinanceScheduleDetailDialog);
		} catch (final Exception e) {
			logger.error(e);
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving") ;
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// ++++++++++++++++++++++++++++++ helpers ++++++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * Stores the init values in mem vars. <br>
	 */
	private void doStoreInitValues() {
		logger.debug("Entering");
		this.oldVar_finReference = this.finReference.getValue();
		this.oldVar_schDate = this.schDate.getValue();
		this.oldVar_schSeq = this.schSeq.intValue();	
		this.oldVar_pftOnSchDate = this.pftOnSchDate.isChecked();
		this.oldVar_cpzOnSchDate = this.cpzOnSchDate.isChecked();
		this.oldVar_repayOnSchDate = this.repayOnSchDate.isChecked();
		this.oldVar_rvwOnSchDate = this.rvwOnSchDate.isChecked();
		this.oldVar_disbOnSchDate = this.disbOnSchDate.isChecked();
		this.oldVar_downpaymentOnSchDate = this.downpaymentOnSchDate.isChecked();
		this.oldVar_balanceForPftCal = this.balanceForPftCal.getValue();
 		this.oldVar_baseRate = this.baseRate.getValue();
 		this.oldVar_lovDescBaseRateName = this.lovDescBaseRateName.getValue();
 		this.oldVar_splRate = this.splRate.getValue();
 		this.oldVar_lovDescSplRateName = this.lovDescSplRateName.getValue();
		this.oldVar_actRate = this.actRate.getValue();
		this.oldVar_adjRate = this.adjRate.getValue();
		this.oldVar_noOfDays = this.noOfDays.intValue();	
		this.oldVar_dayFactor = this.dayFactor.getValue();
		this.oldVar_profitCalc = this.profitCalc.getValue();
		this.oldVar_profitSchd = this.profitSchd.getValue();
		this.oldVar_principalSchd = this.principalSchd.getValue();
		this.oldVar_repayAmount = this.repayAmount.getValue();
		this.oldVar_profitBalance = this.profitBalance.getValue();
		this.oldVar_disbAmount = this.disbAmount.getValue();
		this.oldVar_downPaymentAmount = this.downPaymentAmount.getValue();
		this.oldVar_cpzAmount = this.cpzAmount.getValue();
		this.oldVar_diffProfitSchd = this.diffProfitSchd.getValue();
		this.oldVar_dIffPrincipalSchd = this.dIffPrincipalSchd.getValue();
		this.oldVar_closingBalance = this.closingBalance.getValue();
		this.oldVar_profitFraction = this.profitFraction.getValue();
		this.oldVar_prvRepayAmount = this.prvRepayAmount.getValue();
		this.oldVar_deffProfitBal = this.deffProfitBal.getValue();
		this.oldVar_diffPrincipalBal = this.diffPrincipalBal.getValue();
		this.oldVar_schdPriPaid = this.schdPriPaid.getValue();
		this.oldVar_isSchdPftPaid = this.isSchdPftPaid.isChecked();
		this.oldVar_recordStatus = this.recordStatus.getValue();
		logger.debug("Leaving") ;
	}

	/**
	 * Resets the init values from mem vars. <br>
	 */
	private void doResetInitValues() {
		logger.debug("Entering");
		this.finReference.setValue(this.oldVar_finReference);
		this.schDate.setValue(this.oldVar_schDate);
		this.schSeq.setValue(this.oldVar_schSeq);
		this.pftOnSchDate.setChecked(this.oldVar_pftOnSchDate);
		this.cpzOnSchDate.setChecked(this.oldVar_cpzOnSchDate);
		this.repayOnSchDate.setChecked(this.oldVar_repayOnSchDate);
		this.rvwOnSchDate.setChecked(this.oldVar_rvwOnSchDate);
		this.disbOnSchDate.setChecked(this.oldVar_disbOnSchDate);
		this.downpaymentOnSchDate.setChecked(this.oldVar_downpaymentOnSchDate);
	  	this.balanceForPftCal.setValue(this.oldVar_balanceForPftCal);
 		this.baseRate.setValue(this.oldVar_baseRate);
 		this.lovDescBaseRateName.setValue(this.oldVar_lovDescBaseRateName);
 		this.splRate.setValue(this.oldVar_splRate);
 		this.lovDescSplRateName.setValue(this.oldVar_lovDescSplRateName);
	  	this.actRate.setValue(this.oldVar_actRate);
	  	this.adjRate.setValue(this.oldVar_adjRate);
		this.noOfDays.setValue(this.oldVar_noOfDays);
	  	this.dayFactor.setValue(this.oldVar_dayFactor);
	  	this.profitCalc.setValue(this.oldVar_profitCalc);
	  	this.profitSchd.setValue(this.oldVar_profitSchd);
	  	this.principalSchd.setValue(this.oldVar_principalSchd);
	  	this.repayAmount.setValue(this.oldVar_repayAmount);
	  	this.profitBalance.setValue(this.oldVar_profitBalance);
	  	this.disbAmount.setValue(this.oldVar_disbAmount);
	  	this.downPaymentAmount.setValue(this.oldVar_downPaymentAmount);
	  	this.cpzAmount.setValue(this.oldVar_cpzAmount);
	  	this.diffProfitSchd.setValue(this.oldVar_diffProfitSchd);
	  	this.dIffPrincipalSchd.setValue(this.oldVar_dIffPrincipalSchd);
	  	this.closingBalance.setValue(this.oldVar_closingBalance);
	  	this.profitFraction.setValue(this.oldVar_profitFraction);
	  	this.prvRepayAmount.setValue(this.oldVar_prvRepayAmount);
	  	this.deffProfitBal.setValue(this.oldVar_deffProfitBal);
	  	this.diffPrincipalBal.setValue(this.oldVar_diffPrincipalBal);
	  	this.schdPriPaid.setValue(this.oldVar_schdPriPaid);
		this.isSchdPftPaid.setChecked(this.oldVar_isSchdPftPaid);
		this.recordStatus.setValue(this.oldVar_recordStatus);
		
		if(isWorkFlowEnabled()){
			this.userAction.setSelectedIndex(0);	
		}
		logger.debug("Leaving");
	}

	/**
	 * Checks, if data are changed since the last call of <br>
	 * doStoreInitData() . <br>
	 * 
	 * @return true, if data are changed, otherwise false
	 */
	private boolean isDataChanged() {
		logger.debug("Entering");
		//To clear the Error Messages
		doClearMessage();
		logger.debug("Leaving"); 
		return false;
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug("Entering");
		setValidationOn(true);
		
		if (!this.finReference.isReadonly()){
			this.finReference.setConstraint("NO EMPTY:" + Labels.getLabel("FIELD_NO_EMPTY",new String[]{Labels.getLabel("label_WIFFinanceScheduleDetailDialog_FinReference.value")}));
		}	
		if (!this.schDate.isDisabled()){
			this.schDate.setConstraint("NO EMPTY:" + Labels.getLabel("FIELD_NO_EMPTY",new String[]{Labels.getLabel("label_WIFFinanceScheduleDetailDialog_SchDate.value")}));
		}
		if (!this.schSeq.isReadonly()){
			this.schSeq.setConstraint(new IntValidator(10,Labels.getLabel("label_WIFFinanceScheduleDetailDialog_SchSeq.value")));
		}	
		if (!this.balanceForPftCal.isReadonly()){
			this.balanceForPftCal.setConstraint(new AmountValidator(18,0,Labels.getLabel("label_WIFFinanceScheduleDetailDialog_BalanceForPftCal.value")));
		}	
		if (!this.actRate.isReadonly()){
			this.actRate.setConstraint(new AmountValidator(13,9,Labels.getLabel("label_WIFFinanceScheduleDetailDialog_ActRate.value")));
		}	
		if (!this.adjRate.isReadonly()){
			this.adjRate.setConstraint(new AmountValidator(13,9,Labels.getLabel("label_WIFFinanceScheduleDetailDialog_AdjRate.value")));
		}	
		if (!this.noOfDays.isReadonly()){
			this.noOfDays.setConstraint(new IntValidator(10,Labels.getLabel("label_WIFFinanceScheduleDetailDialog_NoOfDays.value")));
		}	
		if (!this.dayFactor.isReadonly()){
			this.dayFactor.setConstraint(new AmountValidator(18,0,Labels.getLabel("label_WIFFinanceScheduleDetailDialog_DayFactor.value")));
		}	
		if (!this.profitCalc.isReadonly()){
			this.profitCalc.setConstraint(new AmountValidator(18,0,Labels.getLabel("label_WIFFinanceScheduleDetailDialog_ProfitCalc.value")));
		}	
		if (!this.profitSchd.isReadonly()){
			this.profitSchd.setConstraint(new AmountValidator(18,0,Labels.getLabel("label_WIFFinanceScheduleDetailDialog_ProfitSchd.value")));
		}	
		if (!this.principalSchd.isReadonly()){
			this.principalSchd.setConstraint(new AmountValidator(18,0,Labels.getLabel("label_WIFFinanceScheduleDetailDialog_PrincipalSchd.value")));
		}	
		if (!this.repayAmount.isReadonly()){
			this.repayAmount.setConstraint(new AmountValidator(18,0,Labels.getLabel("label_WIFFinanceScheduleDetailDialog_RepayAmount.value")));
		}	
		if (!this.profitBalance.isReadonly()){
			this.profitBalance.setConstraint(new AmountValidator(18,0,Labels.getLabel("label_WIFFinanceScheduleDetailDialog_ProfitBalance.value")));
		}	
		if (!this.disbAmount.isReadonly()){
			this.disbAmount.setConstraint(new AmountValidator(18,0,Labels.getLabel("label_WIFFinanceScheduleDetailDialog_DisbAmount.value")));
		}	
		if (!this.downPaymentAmount.isReadonly()){
			this.downPaymentAmount.setConstraint(new AmountValidator(18,0,Labels.getLabel("label_WIFFinanceScheduleDetailDialog_DownPaymentAmount.value")));
		}	
		if (!this.cpzAmount.isReadonly()){
			this.cpzAmount.setConstraint(new AmountValidator(18,0,Labels.getLabel("label_WIFFinanceScheduleDetailDialog_CpzAmount.value")));
		}	
		if (!this.diffProfitSchd.isReadonly()){
			this.diffProfitSchd.setConstraint(new AmountValidator(18,0,Labels.getLabel("label_WIFFinanceScheduleDetailDialog_DiffProfitSchd.value")));
		}	
		if (!this.dIffPrincipalSchd.isReadonly()){
			this.dIffPrincipalSchd.setConstraint(new AmountValidator(18,0,Labels.getLabel("label_WIFFinanceScheduleDetailDialog_DIffPrincipalSchd.value")));
		}	
		if (!this.closingBalance.isReadonly()){
			this.closingBalance.setConstraint(new AmountValidator(18,0,Labels.getLabel("label_WIFFinanceScheduleDetailDialog_ClosingBalance.value")));
		}	
		if (!this.profitFraction.isReadonly()){
			this.profitFraction.setConstraint(new AmountValidator(18,0,Labels.getLabel("label_WIFFinanceScheduleDetailDialog_ProfitFraction.value")));
		}	
		if (!this.prvRepayAmount.isReadonly()){
			this.prvRepayAmount.setConstraint(new AmountValidator(18,0,Labels.getLabel("label_WIFFinanceScheduleDetailDialog_PrvRepayAmount.value")));
		}	
		if (!this.deffProfitBal.isReadonly()){
			this.deffProfitBal.setConstraint(new AmountValidator(18,0,Labels.getLabel("label_WIFFinanceScheduleDetailDialog_DeffProfitBal.value")));
		}	
		if (!this.diffPrincipalBal.isReadonly()){
			this.diffPrincipalBal.setConstraint(new AmountValidator(18,0,Labels.getLabel("label_WIFFinanceScheduleDetailDialog_DiffPrincipalBal.value")));
		}	
		if (!this.schdPriPaid.isReadonly()){
			this.schdPriPaid.setConstraint(new AmountValidator(18,0,Labels.getLabel("label_WIFFinanceScheduleDetailDialog_SchdPriPaid.value")));
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
		this.schDate.setConstraint("");
		this.schSeq.setConstraint("");
		this.balanceForPftCal.setConstraint("");
		this.actRate.setConstraint("");
		this.adjRate.setConstraint("");
		this.noOfDays.setConstraint("");
		this.dayFactor.setConstraint("");
		this.profitCalc.setConstraint("");
		this.profitSchd.setConstraint("");
		this.principalSchd.setConstraint("");
		this.repayAmount.setConstraint("");
		this.profitBalance.setConstraint("");
		this.disbAmount.setConstraint("");
		this.downPaymentAmount.setConstraint("");
		this.cpzAmount.setConstraint("");
		this.diffProfitSchd.setConstraint("");
		this.dIffPrincipalSchd.setConstraint("");
		this.closingBalance.setConstraint("");
		this.profitFraction.setConstraint("");
		this.prvRepayAmount.setConstraint("");
		this.deffProfitBal.setConstraint("");
		this.diffPrincipalBal.setConstraint("");
		this.schdPriPaid.setConstraint("");
	logger.debug("Leaving");
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// +++++++++++++++++++++++++ crud operations +++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * Deletes a WIFFinanceScheduleDetail object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug("Entering");	
		final FinanceScheduleDetail aWIFFinanceScheduleDetail = new FinanceScheduleDetail();
		BeanUtils.copyProperties(getWIFFinanceScheduleDetail(), aWIFFinanceScheduleDetail);
		String tranType=PennantConstants.TRAN_WF;
		
		// Show a confirm box
		final String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record") + "\n\n --> " + aWIFFinanceScheduleDetail.getFinReference();
		final String title = Labels.getLabel("message.Deleting.Record");
		MultiLineMessageBox.doSetTemplate();
		
		int conf =  (MultiLineMessageBox.show(msg, title, MultiLineMessageBox.YES| MultiLineMessageBox.NO, Messagebox.QUESTION, true));

		if (conf==MultiLineMessageBox.YES){
			logger.debug("doDelete: Yes");

			if (StringUtils.trimToEmpty(aWIFFinanceScheduleDetail.getRecordType()).equals("")){
				aWIFFinanceScheduleDetail.setVersion(aWIFFinanceScheduleDetail.getVersion()+1);
				aWIFFinanceScheduleDetail.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				
				if (isWorkFlowEnabled()){
					aWIFFinanceScheduleDetail.setNewRecord(true);
					tranType=PennantConstants.TRAN_WF;
				}else{
					tranType=PennantConstants.TRAN_DEL;
				}
			}

			try {
				if(doProcess(aWIFFinanceScheduleDetail,tranType)){
					refreshList();
					closeDialog(this.window_WIFFinanceScheduleDetailDialog, "WIFFinanceScheduleDetail"); 
				}

			}catch (DataAccessException e){
				logger.error("doDelete " + e);
				showMessage(e);
			}
			
		}
		logger.debug("Leaving");
	}

	/**
	 * Create a new WIFFinanceScheduleDetail object. <br>
	 */
	private void doNew() {
		logger.debug("Entering");
		
		final FinanceScheduleDetail aWIFFinanceScheduleDetail = getWIFFinanceScheduleDetailService().getNewWIFFinanceScheduleDetail();
		aWIFFinanceScheduleDetail.setNewRecord(true);
		setWIFFinanceScheduleDetail(aWIFFinanceScheduleDetail);
		doClear(); // clear all commponents
		doEdit(); // edit mode
		this.btnCtrl.setBtnStatus_New();

		// remember the old vars
		doStoreInitValues();

		// setFocus
		this.finReference.focus();
	logger.debug("Leaving");
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug("Entering");
		
		if (getWIFFinanceScheduleDetail().isNewRecord()){
		  	this.finReference.setReadonly(false);
			this.btnCancel.setVisible(false);
		}else{
			this.finReference.setReadonly(true);
			this.btnCancel.setVisible(true);
		}
	
	 	this.schDate.setDisabled(isReadOnly("WIFFinanceScheduleDetailDialog_schDate"));
		this.schSeq.setReadonly(isReadOnly("WIFFinanceScheduleDetailDialog_schSeq"));
	 	this.pftOnSchDate.setDisabled(isReadOnly("WIFFinanceScheduleDetailDialog_pftOnSchDate"));
	 	this.cpzOnSchDate.setDisabled(isReadOnly("WIFFinanceScheduleDetailDialog_cpzOnSchDate"));
	 	this.repayOnSchDate.setDisabled(isReadOnly("WIFFinanceScheduleDetailDialog_repayOnSchDate"));
	 	this.rvwOnSchDate.setDisabled(isReadOnly("WIFFinanceScheduleDetailDialog_rvwOnSchDate"));
	 	this.disbOnSchDate.setDisabled(isReadOnly("WIFFinanceScheduleDetailDialog_disbOnSchDate"));
	 	this.downpaymentOnSchDate.setDisabled(isReadOnly("WIFFinanceScheduleDetailDialog_downpaymentOnSchDate"));
		this.balanceForPftCal.setReadonly(isReadOnly("WIFFinanceScheduleDetailDialog_balanceForPftCal"));
	  	this.btnSearchBaseRate.setDisabled(isReadOnly("WIFFinanceScheduleDetailDialog_baseRate"));
	  	this.btnSearchSplRate.setDisabled(isReadOnly("WIFFinanceScheduleDetailDialog_splRate"));
		this.actRate.setReadonly(isReadOnly("WIFFinanceScheduleDetailDialog_actRate"));
		this.adjRate.setReadonly(isReadOnly("WIFFinanceScheduleDetailDialog_adjRate"));
		this.noOfDays.setReadonly(isReadOnly("WIFFinanceScheduleDetailDialog_noOfDays"));
		this.dayFactor.setReadonly(isReadOnly("WIFFinanceScheduleDetailDialog_dayFactor"));
		this.profitCalc.setReadonly(isReadOnly("WIFFinanceScheduleDetailDialog_profitCalc"));
		this.profitSchd.setReadonly(isReadOnly("WIFFinanceScheduleDetailDialog_profitSchd"));
		this.principalSchd.setReadonly(isReadOnly("WIFFinanceScheduleDetailDialog_principalSchd"));
		this.repayAmount.setReadonly(isReadOnly("WIFFinanceScheduleDetailDialog_repayAmount"));
		this.profitBalance.setReadonly(isReadOnly("WIFFinanceScheduleDetailDialog_profitBalance"));
		this.disbAmount.setReadonly(isReadOnly("WIFFinanceScheduleDetailDialog_disbAmount"));
		this.downPaymentAmount.setReadonly(isReadOnly("WIFFinanceScheduleDetailDialog_downPaymentAmount"));
		this.cpzAmount.setReadonly(isReadOnly("WIFFinanceScheduleDetailDialog_cpzAmount"));
		this.diffProfitSchd.setReadonly(isReadOnly("WIFFinanceScheduleDetailDialog_diffProfitSchd"));
		this.dIffPrincipalSchd.setReadonly(isReadOnly("WIFFinanceScheduleDetailDialog_dIffPrincipalSchd"));
		this.closingBalance.setReadonly(isReadOnly("WIFFinanceScheduleDetailDialog_closingBalance"));
		this.profitFraction.setReadonly(isReadOnly("WIFFinanceScheduleDetailDialog_profitFraction"));
		this.prvRepayAmount.setReadonly(isReadOnly("WIFFinanceScheduleDetailDialog_prvRepayAmount"));
		this.deffProfitBal.setReadonly(isReadOnly("WIFFinanceScheduleDetailDialog_deffProfitBal"));
		this.diffPrincipalBal.setReadonly(isReadOnly("WIFFinanceScheduleDetailDialog_diffPrincipalBal"));
		this.schdPriPaid.setReadonly(isReadOnly("WIFFinanceScheduleDetailDialog_schdPriPaid"));
	 	this.isSchdPftPaid.setDisabled(isReadOnly("WIFFinanceScheduleDetailDialog_isSchdPftPaid"));

		if (isWorkFlowEnabled()){
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}
			
			if (this.wIFFinanceScheduleDetail.isNewRecord()){
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(false);
			}else{
				this.btnCtrl.setWFBtnStatus_Edit(isFirstTask());
			}
		}else{
			this.btnCtrl.setBtnStatus_Edit();
			btnCancel.setVisible(true);
		}
		// remember the old vars
		doStoreInitValues();
		logger.debug("Leaving");
	}

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly() {
		logger.debug("Entering");
		this.finReference.setReadonly(true);
		this.schDate.setDisabled(true);
		this.schSeq.setReadonly(true);
		this.pftOnSchDate.setDisabled(true);
		this.cpzOnSchDate.setDisabled(true);
		this.repayOnSchDate.setDisabled(true);
		this.rvwOnSchDate.setDisabled(true);
		this.disbOnSchDate.setDisabled(true);
		this.downpaymentOnSchDate.setDisabled(true);
		this.balanceForPftCal.setReadonly(true);
		this.btnSearchBaseRate.setDisabled(true);
		this.btnSearchSplRate.setDisabled(true);
		this.actRate.setReadonly(true);
		this.adjRate.setReadonly(true);
		this.noOfDays.setReadonly(true);
		this.dayFactor.setReadonly(true);
		this.profitCalc.setReadonly(true);
		this.profitSchd.setReadonly(true);
		this.principalSchd.setReadonly(true);
		this.repayAmount.setReadonly(true);
		this.profitBalance.setReadonly(true);
		this.disbAmount.setReadonly(true);
		this.downPaymentAmount.setReadonly(true);
		this.cpzAmount.setReadonly(true);
		this.diffProfitSchd.setReadonly(true);
		this.dIffPrincipalSchd.setReadonly(true);
		this.closingBalance.setReadonly(true);
		this.profitFraction.setReadonly(true);
		this.prvRepayAmount.setReadonly(true);
		this.deffProfitBal.setReadonly(true);
		this.diffPrincipalBal.setReadonly(true);
		this.schdPriPaid.setReadonly(true);
		this.isSchdPftPaid.setDisabled(true);
		
		if(isWorkFlowEnabled()){
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(true);
			}
		}
		
		if(isWorkFlowEnabled()){
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
		this.schDate.setText("");
		this.schSeq.setText("");
		this.pftOnSchDate.setChecked(false);
		this.cpzOnSchDate.setChecked(false);
		this.repayOnSchDate.setChecked(false);
		this.rvwOnSchDate.setChecked(false);
		this.disbOnSchDate.setChecked(false);
		this.downpaymentOnSchDate.setChecked(false);
		this.balanceForPftCal.setValue("");
	   this.baseRate.setValue("");
		this.lovDescBaseRateName.setValue("");
	   this.splRate.setValue("");
		this.lovDescSplRateName.setValue("");
		this.actRate.setValue("");
		this.adjRate.setValue("");
		this.noOfDays.setText("");
		this.dayFactor.setValue("");
		this.profitCalc.setValue("");
		this.profitSchd.setValue("");
		this.principalSchd.setValue("");
		this.repayAmount.setValue("");
		this.profitBalance.setValue("");
		this.disbAmount.setValue("");
		this.downPaymentAmount.setValue("");
		this.cpzAmount.setValue("");
		this.diffProfitSchd.setValue("");
		this.dIffPrincipalSchd.setValue("");
		this.closingBalance.setValue("");
		this.profitFraction.setValue("");
		this.prvRepayAmount.setValue("");
		this.deffProfitBal.setValue("");
		this.diffPrincipalBal.setValue("");
		this.schdPriPaid.setValue("");
		this.isSchdPftPaid.setChecked(false);
	logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug("Entering");
		final FinanceScheduleDetail aWIFFinanceScheduleDetail = new FinanceScheduleDetail();
		BeanUtils.copyProperties(getWIFFinanceScheduleDetail(), aWIFFinanceScheduleDetail);
		boolean isNew = false;
		
		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		// force validation, if on, than execute by component.getValue()
		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		doSetValidation();
		// fill the WIFFinanceScheduleDetail object with the components data
		doWriteComponentsToBean(aWIFFinanceScheduleDetail);

		// Write the additional validations as per below example
		// get the selected branch object from the listbox
		// Do data level validations here
		
		isNew = aWIFFinanceScheduleDetail.isNew();
		String tranType="";

		if(isWorkFlowEnabled()){
			tranType =PennantConstants.TRAN_WF;
			if (StringUtils.trimToEmpty(aWIFFinanceScheduleDetail.getRecordType()).equals("")){
				aWIFFinanceScheduleDetail.setVersion(aWIFFinanceScheduleDetail.getVersion()+1);
				if(isNew){
					aWIFFinanceScheduleDetail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else{
					aWIFFinanceScheduleDetail.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aWIFFinanceScheduleDetail.setNewRecord(true);
				}
			}
		}else{
			aWIFFinanceScheduleDetail.setVersion(aWIFFinanceScheduleDetail.getVersion()+1);
			if(isNew){
				tranType =PennantConstants.TRAN_ADD;
			}else{
				tranType =PennantConstants.TRAN_UPD;
			}
		}
		
		// save it to database
		try {
			
			if(doProcess(aWIFFinanceScheduleDetail,tranType)){
				doWriteBeanToComponents(aWIFFinanceScheduleDetail);
				refreshList();
				closeDialog(this.window_WIFFinanceScheduleDetailDialog, "WIFFinanceScheduleDetail");
			}

		} catch (final DataAccessException e) {
			logger.error(e);
			showMessage(e);
		}
		logger.debug("Leaving");
	}

	private boolean doProcess(FinanceScheduleDetail aWIFFinanceScheduleDetail,String tranType){
		logger.debug("Entering");
		boolean processCompleted=false;
		AuditHeader auditHeader =  null;
		String nextRoleCode="";
		
		aWIFFinanceScheduleDetail.setLastMntBy(getUserWorkspace().getLoginUserDetails().getLoginUsrID());
		aWIFFinanceScheduleDetail.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aWIFFinanceScheduleDetail.setUserDetails(getUserWorkspace().getLoginUserDetails());
		
		if (isWorkFlowEnabled()) {
			String taskId = getWorkFlow().getTaskId(getRole());
			String nextTaskId = "";
			//Upgraded to ZK-6.5.1.1 Added casting to String 	
			aWIFFinanceScheduleDetail.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aWIFFinanceScheduleDetail.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getWorkFlow().getNextTaskIds(taskId, aWIFFinanceScheduleDetail);
				}

				if (PennantConstants.WF_Audit_Notes.equals(getWorkFlow().getAuditingReq(taskId,aWIFFinanceScheduleDetail))) {
					try {
						if (!isNotes_Entered()){
							PTMessageUtils.showErrorMessage(Labels.getLabel("Notes_NotEmpty"));
							return false;
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
			
			
			if (StringUtils.trimToEmpty(nextTaskId).equals("")) {
				nextRoleCode= getWorkFlow().firstTask.owner;
			} else {
				String[] nextTasks = nextTaskId.split(";");
				
				if (nextTasks!=null && nextTasks.length>0){
					for (int i = 0; i < nextTasks.length; i++) {
						
						if(nextRoleCode.length()>1){
							nextRoleCode =nextRoleCode+",";
						}
						nextRoleCode= getWorkFlow().getTaskOwner(nextTasks[i]);
					}
				}else{
					nextRoleCode= getWorkFlow().getTaskOwner(nextTaskId);
				}
			}

			aWIFFinanceScheduleDetail.setTaskId(taskId);
			aWIFFinanceScheduleDetail.setNextTaskId(nextTaskId);
			aWIFFinanceScheduleDetail.setRoleCode(getRole());
			aWIFFinanceScheduleDetail.setNextRoleCode(nextRoleCode);
			
			auditHeader =  getAuditHeader(aWIFFinanceScheduleDetail, tranType);
			
			String operationRefs = getWorkFlow().getOperationRefs(taskId,aWIFFinanceScheduleDetail);
			
			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader,null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader =  getAuditHeader(aWIFFinanceScheduleDetail, PennantConstants.TRAN_WF);
					processCompleted  = doSaveProcess(auditHeader, list[i]);
					if(!processCompleted){
						break;
					}
				}
			}
		}else{
			
			auditHeader =  getAuditHeader(aWIFFinanceScheduleDetail, tranType);
			processCompleted = doSaveProcess(auditHeader,null);
		}
		logger.debug("return value :"+processCompleted);
		logger.debug("Leaving");
		return processCompleted;
	}
	

	private boolean doSaveProcess(AuditHeader auditHeader,String method){
		logger.debug("Entering");
		boolean processCompleted=false;
		int retValue=PennantConstants.porcessOVERIDE;
		boolean deleteNotes=false;
		
		FinanceScheduleDetail aWIFFinanceScheduleDetail = (FinanceScheduleDetail) auditHeader.getAuditDetail().getModelData();
		
		try {
			
			while(retValue==PennantConstants.porcessOVERIDE){
				
				if (StringUtils.trimToEmpty(method).equalsIgnoreCase("")){
					if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)){
						auditHeader = getWIFFinanceScheduleDetailService().delete(auditHeader);
						deleteNotes=true;
					}else{
						auditHeader = getWIFFinanceScheduleDetailService().saveOrUpdate(auditHeader);	
					}
					
				}else{
					if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)){
						auditHeader = getWIFFinanceScheduleDetailService().doApprove(auditHeader);

						if(aWIFFinanceScheduleDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)){
							deleteNotes=true;
						}

					}else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)){
						auditHeader = getWIFFinanceScheduleDetailService().doReject(auditHeader);
						if(aWIFFinanceScheduleDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){
							deleteNotes=true;
						}

					}else{
						auditHeader.setErrorDetails(new ErrorDetails(PennantConstants.ERR_9999, Labels.getLabel("InvalidWorkFlowMethod"), null));
						retValue = ErrorControl.showErrorControl(this.window_WIFFinanceScheduleDetailDialog, auditHeader);
						return processCompleted; 
					}
				}
				
				auditHeader =	ErrorControl.showErrorDetails(this.window_WIFFinanceScheduleDetailDialog, auditHeader);
				retValue = auditHeader.getProcessStatus();
				
				if (retValue==PennantConstants.porcessCONTINUE){
					processCompleted=true;

					if(deleteNotes){
						deleteNotes(getNotes(),true);
					}
				}
				
				if (retValue==PennantConstants.porcessOVERIDE){
					auditHeader.setOveride(true);
					auditHeader.setErrorMessage(null);
					auditHeader.setInfoMessage(null);
					auditHeader.setOverideMessage(null);
				}
			}
		} catch (InterruptedException e) {
			logger.error(e);
			e.printStackTrace();
		}
		setOverideMap(auditHeader.getOverideMap());
		
		logger.debug("return Value:" + processCompleted);
		logger.debug("Leaving");
		return processCompleted;
	}
	
   public void onClick$btnSearchBaseRate(Event event){

	   Object dataObject = ExtendedSearchListBox.show(this.window_WIFFinanceScheduleDetailDialog,"BaseRate");
	   if (dataObject instanceof String){
		   this.baseRate.setValue(dataObject.toString());
		   this.lovDescBaseRateName.setValue("");
	   }else{
		   BaseRate details= (BaseRate) dataObject;
			if (details != null) {
				this.baseRate.setValue(details.getBRType());
				this.lovDescBaseRateName.setValue(details.getBRType()+"-"+details.getLovDescBRTypeName());
			}
	   }
	}
   public void onClick$btnSearchSplRate(Event event){
	   
	   Object dataObject = ExtendedSearchListBox.show(this.window_WIFFinanceScheduleDetailDialog,"SplRateCode");
	   if (dataObject instanceof String){
		   this.splRate.setValue(dataObject.toString());
		   this.lovDescSplRateName.setValue("");
	   }else{
		   SplRateCode details= (SplRateCode) dataObject;
			if (details != null) {
				this.splRate.setValue(details.getSRType());
				this.lovDescSplRateName.setValue(details.getSRType()+"-"+details.getSRTypeDesc());
			}
	   }
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

	public FinanceScheduleDetail getWIFFinanceScheduleDetail() {
		return this.wIFFinanceScheduleDetail;
	}

	public void setWIFFinanceScheduleDetail(FinanceScheduleDetail wIFFinanceScheduleDetail) {
		this.wIFFinanceScheduleDetail = wIFFinanceScheduleDetail;
	}

	public void setWIFFinanceScheduleDetailService(WIFFinanceScheduleDetailService wIFFinanceScheduleDetailService) {
		this.wIFFinanceScheduleDetailService = wIFFinanceScheduleDetailService;
	}

	public WIFFinanceScheduleDetailService getWIFFinanceScheduleDetailService() {
		return this.wIFFinanceScheduleDetailService;
	}

	public void setWIFFinanceScheduleDetailListCtrl(WIFFinanceScheduleDetailListCtrl wIFFinanceScheduleDetailListCtrl) {
		this.wIFFinanceScheduleDetailListCtrl = wIFFinanceScheduleDetailListCtrl;
	}

	public WIFFinanceScheduleDetailListCtrl getWIFFinanceScheduleDetailListCtrl() {
		return this.wIFFinanceScheduleDetailListCtrl;
	}

	public PagedListService getPagedListService() {
		return pagedListService;
	}

	public void setPagedListService(PagedListService pagedListService) {
		this.pagedListService = pagedListService;
	}

	
	private AuditHeader getAuditHeader(FinanceScheduleDetail aWIFFinanceScheduleDetail, String tranType){
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aWIFFinanceScheduleDetail.getBefImage(), aWIFFinanceScheduleDetail);   
		return new AuditHeader(aWIFFinanceScheduleDetail.getFinReference(),null,null,null,auditDetail,aWIFFinanceScheduleDetail.getUserDetails(),getOverideMap());
	}
	
	private void showMessage(Exception e){
		AuditHeader auditHeader= new AuditHeader();
		try {
			auditHeader.setErrorDetails(new ErrorDetails(PennantConstants.ERR_UNDEF,e.getMessage(),null));
			ErrorControl.showErrorControl(this.window_WIFFinanceScheduleDetailDialog, auditHeader);
		} catch (Exception exp) {
			logger.error(exp);
		}
	}

	public boolean isNotes_Entered() {
		return notes_Entered;
	}

	public void setNotes_Entered(boolean notes_Entered) {
		this.notes_Entered = notes_Entered;
	}
	
	
	public void onClick$btnNotes(Event event) throws Exception {
		logger.debug("Entering");
		// logger.debug(event.toString());
		
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("notes", getNotes());
		map.put("control", this);
		
		// call the zul-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/notes/notes.zul", null, map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / " + e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving");
	}
	
	public void setNotes_entered(String notes) {
		if (!isNotes_Entered()){
			if (org.apache.commons.lang.StringUtils.trimToEmpty(notes).equalsIgnoreCase("Y")){
				setNotes_Entered(true);
			}else{
				setNotes_Entered(false);
			}	
		}
	}	

	private void doSetLOVValidation() {
		this.lovDescBaseRateName.setConstraint("NO EMPTY:" + Labels.getLabel("FIELD_NO_EMPTY",new String[]{Labels.getLabel("label_WIFFinanceScheduleDetailDialog_BaseRate.value")}));
		this.lovDescSplRateName.setConstraint("NO EMPTY:" + Labels.getLabel("FIELD_NO_EMPTY",new String[]{Labels.getLabel("label_WIFFinanceScheduleDetailDialog_SplRate.value")}));
	}
	private void doRemoveLOVValidation() {
		this.lovDescBaseRateName.setConstraint("");
		this.lovDescSplRateName.setConstraint("");
	}
	
	private Notes getNotes(){
		Notes notes = new Notes();
		notes.setModuleName("WIFFinanceScheduleDetail");
		notes.setReference(getWIFFinanceScheduleDetail().getFinReference());
		notes.setVersion(getWIFFinanceScheduleDetail().getVersion());
		return notes;
	}
	
	private void doClearMessage() {
		logger.debug("Entering");
			this.finReference.setErrorMessage("");
			this.schDate.setErrorMessage("");
			this.schSeq.setErrorMessage("");
			this.balanceForPftCal.setErrorMessage("");
			this.lovDescBaseRateName.setErrorMessage("");
			this.lovDescSplRateName.setErrorMessage("");
			this.actRate.setErrorMessage("");
			this.adjRate.setErrorMessage("");
			this.noOfDays.setErrorMessage("");
			this.dayFactor.setErrorMessage("");
			this.profitCalc.setErrorMessage("");
			this.profitSchd.setErrorMessage("");
			this.principalSchd.setErrorMessage("");
			this.repayAmount.setErrorMessage("");
			this.profitBalance.setErrorMessage("");
			this.disbAmount.setErrorMessage("");
			this.downPaymentAmount.setErrorMessage("");
			this.cpzAmount.setErrorMessage("");
			this.diffProfitSchd.setErrorMessage("");
			this.dIffPrincipalSchd.setErrorMessage("");
			this.closingBalance.setErrorMessage("");
			this.profitFraction.setErrorMessage("");
			this.prvRepayAmount.setErrorMessage("");
			this.deffProfitBal.setErrorMessage("");
			this.diffPrincipalBal.setErrorMessage("");
			this.schdPriPaid.setErrorMessage("");
	logger.debug("Leaving");
	}
	

private void refreshList(){
		final JdbcSearchObject<FinanceScheduleDetail> soWIFFinanceScheduleDetail = getWIFFinanceScheduleDetailListCtrl().getSearchObj();
		getWIFFinanceScheduleDetailListCtrl().pagingWIFFinanceScheduleDetailList.setActivePage(0);
		getWIFFinanceScheduleDetailListCtrl().getPagedListWrapper().setSearchObject(soWIFFinanceScheduleDetail);
		if(getWIFFinanceScheduleDetailListCtrl().listBoxWIFFinanceScheduleDetail!=null){
			getWIFFinanceScheduleDetailListCtrl().listBoxWIFFinanceScheduleDetail.getListModel();
		}
	} 

	public void setOverideMap(HashMap<String, ArrayList<ErrorDetails>> overideMap) {
		this.overideMap = overideMap;
	}

	public HashMap<String, ArrayList<ErrorDetails>> getOverideMap() {
		return overideMap;
	}

	public FinanceScheduleDetail getPrvWIFFinanceScheduleDetail() {
		return prvWIFFinanceScheduleDetail;
	}
}
