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
 * FileName    		:  OverdueChargeRecoveryDialogCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  11-05-2012    														*
 *                                                                  						*
 * Modified Date    :  11-05-2012    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 11-05-2012       Pennant	                 0.1                                            * 
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

package com.pennant.webui.financemanagement.overduechargerecovery;

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
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Longbox;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Row;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.app.util.DateUtility;
import com.pennant.app.util.OverDueRecoveryPostingsUtil;
import com.pennant.app.util.SystemParameterDetails;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.Notes;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.financemanagement.OverdueChargeRecovery;
import com.pennant.backend.service.PagedListService;
import com.pennant.backend.service.financemanagement.OverdueChargeRecoveryService;
import com.pennant.backend.service.rmtmasters.FinanceTypeService;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.util.ErrorControl;
import com.pennant.util.PennantAppUtil;
import com.pennant.webui.util.ButtonStatusCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.MultiLineMessageBox;
import com.pennant.webui.util.PTMessageUtils;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * /WEB-INF/pages/FinanceManagement/OverdueChargeRecovery/overdueChargeRecoveryDialog.zul file. <br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 */
public class OverdueChargeRecoveryDialogCtrl extends GFCBaseCtrl implements Serializable {

	private static final long serialVersionUID = 728436178283801925L;
	private final static Logger logger = Logger.getLogger(OverdueChargeRecoveryDialogCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window		window_OverdueChargeRecoveryDialog; 	// autowired
	protected Textbox 		finReference; 							// autowired
	protected Datebox 		finSchdDate; 							// autowired
	protected Datebox 		finStartDate; 							// autowired
	protected Datebox 		finMaturityDate; 						// autowired
	protected Decimalbox 	finAmt; 								// autowired
	protected Decimalbox 	curFinAmt; 								// autowired
	protected Decimalbox 	curSchPriDue; 							// autowired
	protected Decimalbox 	curSchPftDue; 							// autowired
	protected Decimalbox 	totOvrDueChrg; 							// autowired
	protected Decimalbox 	totOvrDueChrgWaived; 					// autowired
	protected Decimalbox 	totOvrDueChrgPaid; 						// autowired
	protected Decimalbox 	totOvrDueChrgBal;						// autowired
	protected Combobox 		cbFinODFor; 							// autowired
	protected Textbox 		finBrnm; 								// autowired
	protected Textbox 		finType; 								// autowired
	protected Longbox 		finCustId; 								// autowired
	protected Textbox 		lovDescCustCIF;							// autowired
	protected Label 		custShrtName;							// autowired
	protected Textbox 		finCcy; 								// autowired
	protected Datebox 		finODDate; 								// autowired
	protected Decimalbox 	finODPri;								// autowired
	protected Decimalbox	finODPft; 								// autowired
	protected Decimalbox 	finODTot; 								// autowired
	protected Textbox 		finODCRuleCode; 						// autowired
	protected Textbox 		finODCPLAc; 							// autowired
	protected Textbox 		finODCCAc; 								// autowired
	protected Decimalbox 	finODCPLShare; 							// autowired
	protected Checkbox 		finODCSweep; 							// autowired
	protected Textbox 		finODCCustCtg; 							// autowired
	protected Combobox 		cbFinODCType; 							// autowired
	protected Textbox 		finODCOn; 								// autowired
	protected Decimalbox 	finODC; 								// autowired
	protected Intbox 		finODCGraceDays; 						// autowired
	protected Checkbox 		finODCAlwWaiver; 						// autowired
	protected Decimalbox 	finODCMaxWaiver; 						// autowired
	protected Decimalbox 	finODCPenalty; 							// autowired
	protected Decimalbox 	finODCWaived; 							// autowired
	protected Decimalbox 	finODCPLPenalty;						// autowired
	protected Decimalbox 	finODCCPenalty; 						// autowired
	protected Decimalbox 	finODCPaid; 							// autowired
	protected Datebox 		finODCLastPaidDate; 					// autowired
	protected Textbox 		finODCRecoverySts; 						// autowired
	protected Button 		btnRecoverNow; 							// autowired
	protected Decimalbox 	balChrgRecovery; 						// autowired
	protected Row 			oDCWaivedRow;							// autowired
	protected Row 			oDCAlwWaiverRow;						// autowired

	protected Label recordStatus; 									// autowired
	protected Radiogroup userAction;
	protected Groupbox groupboxWf;
	protected Row statusRow;

	// not auto wired vars
	private OverdueChargeRecovery overdueChargeRecovery; 			// overhanded per param
	private OverdueChargeRecovery prvOverdueChargeRecovery; 		// overhanded per param
	private transient OverdueChargeRecoveryListCtrl overdueChargeRecoveryListCtrl; // overhanded per param

	// old value vars for edit mode. that we can check if something
	// on the values are edited since the last init.
	private transient String  		oldVar_finReference;
	private transient Date  		oldVar_finSchdDate;
	private transient int  			oldVar_finODFor;
	private transient String  		oldVar_finBrnm;
	private transient String  		oldVar_finType;
	private transient long  		oldVar_finCustId;
	private transient String  		oldVar_finCcy;
	private transient Date  		oldVar_finODDate;
	private transient BigDecimal  	oldVar_finODPri;
	private transient BigDecimal  	oldVar_finODPft;
	private transient BigDecimal  	oldVar_finODTot;
	private transient String  		oldVar_finODCRuleCode;
	private transient String  		oldVar_finODCPLAc;
	private transient String  		oldVar_finODCCAc;
	private transient BigDecimal  	oldVar_finODCPLShare;
	private transient boolean  		oldVar_finODCSweep;
	private transient String  		oldVar_finODCCustCtg;
	private transient int	  		oldVar_finODCType;
	private transient String  		oldVar_finODCOn;
	private transient BigDecimal  	oldVar_finODC;
	private transient int  			oldVar_finODCGraceDays;
	private transient boolean  		oldVar_finODCAlwWaiver;
	private transient BigDecimal  	oldVar_finODCMaxWaiver;
	private transient BigDecimal  	oldVar_finODCPenalty;
	private transient BigDecimal  	oldVar_finODCWaived;
	private transient BigDecimal  	oldVar_finODCPLPenalty;
	private transient BigDecimal  	oldVar_finODCCPenalty;
	private transient BigDecimal  	oldVar_finODCPaid;
	private transient Date  		oldVar_finODCLastPaidDate;
	private transient String  		oldVar_finODCRecoverySts;
	private transient String 		oldVar_recordStatus;

	private transient boolean validationOn;
	private boolean notes_Entered=false;

	// Button controller for the CRUD buttons
	private transient final String btnCtroller_ClassPrefix = "button_OverdueChargeRecoveryDialog_";
	private transient ButtonStatusCtrl btnCtrl;
	protected Button btnNew; 	// autowire
	protected Button btnEdit; 	// autowire
	protected Button btnDelete; // autowire
	protected Button btnSave; 	// autowire
	protected Button btnCancel; // autowire
	protected Button btnClose; 	// autowire
	protected Button btnHelp; 	// autowire
	protected Button btnNotes; 	// autowire


	// ServiceDAOs / Domain Classes
	private transient OverdueChargeRecoveryService overdueChargeRecoveryService;
	private transient PagedListService pagedListService;
	private transient FinanceTypeService financeTypeService;
	private HashMap<String, ArrayList<ErrorDetails>> overideMap= new HashMap<String, ArrayList<ErrorDetails>>();
	private static ArrayList<ValueLabel> finOdForList = PennantStaticListUtil.getODCChargeFor();
	private static ArrayList<ValueLabel> finODCTypeList = PennantStaticListUtil.getODCChargeType();
	private transient OverDueRecoveryPostingsUtil recoveryPostingsUtil;
	Date dateValueDate = DateUtility.getDBDate(SystemParameterDetails.getSystemParameterValue("APP_VALUEDATE").toString());
	private boolean isInquiry = false;
	
	/**
	 * default constructor.<br>
	 */
	public OverdueChargeRecoveryDialogCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * zul-file is called with a parameter for a selected OverdueChargeRecovery object in a
	 * Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_OverdueChargeRecoveryDialog(Event event) throws Exception {
		logger.debug(event.toString());

		/* set components visible dependent of the users rights */
		doCheckRights();

		/* create the Button Controller. Disable not used buttons during working */
		this.btnCtrl = new ButtonStatusCtrl(getUserWorkspace(), this.btnCtroller_ClassPrefix, true, this.btnNew,
				this.btnEdit, this.btnDelete, this.btnSave, this.btnCancel, this.btnClose,this.btnNotes);

		// get the params map that are overhanded by creation.
		final Map<String, Object> args = getCreationArgsMap(event);

		// READ OVERHANDED params !
		if (args.containsKey("overdueChargeRecovery")) {
			this.overdueChargeRecovery = (OverdueChargeRecovery) args.get("overdueChargeRecovery");
			OverdueChargeRecovery befImage =new OverdueChargeRecovery();
			BeanUtils.copyProperties(this.overdueChargeRecovery, befImage);
			this.overdueChargeRecovery.setBefImage(befImage);

			setOverdueChargeRecovery(this.overdueChargeRecovery);
		} else {
			setOverdueChargeRecovery(null);
		}
		
		if(args.containsKey("inquiry")) {
			this.isInquiry = (Boolean) args.get("inquiry");
		}

		doLoadWorkFlow(this.overdueChargeRecovery.isWorkflow(),this.overdueChargeRecovery.getWorkflowId(),this.overdueChargeRecovery.getNextTaskId());

		if (isWorkFlowEnabled()){
			this.userAction	= setListRecordStatus(this.userAction);
			getUserWorkspace().alocateRoleAuthorities(getRole(), "OverdueChargeRecoveryDialog");
		}

		// READ OVERHANDED params !
		// we get the overdueChargeRecoveryListWindow controller. So we have access
		// to it and can synchronize the shown data when we do insert, edit or
		// delete overdueChargeRecovery here.
		if (args.containsKey("overdueChargeRecoveryListCtrl")) {
			setOverdueChargeRecoveryListCtrl((OverdueChargeRecoveryListCtrl) args.get("overdueChargeRecoveryListCtrl"));
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
		logger.debug("Entering") ;
		//Empty sent any required attributes
		this.finReference.setMaxlength(20);
		this.finSchdDate.setFormat(PennantConstants.dateFormat);
		this.finStartDate.setFormat(PennantConstants.dateFormat);
		this.finMaturityDate.setFormat(PennantConstants.dateFormat);
		this.finAmt.setMaxlength(18);
		this.finAmt.setFormat(PennantApplicationUtil.getAmountFormate(getOverdueChargeRecovery().getLovDescFinFormatter()));
		this.curFinAmt.setMaxlength(18);
		this.curFinAmt.setFormat(PennantApplicationUtil.getAmountFormate(getOverdueChargeRecovery().getLovDescFinFormatter()));
		this.curSchPriDue.setMaxlength(18);
		this.curSchPriDue.setFormat(PennantApplicationUtil.getAmountFormate(getOverdueChargeRecovery().getLovDescFinFormatter()));
		this.curSchPftDue.setMaxlength(18);
		this.curSchPftDue.setFormat(PennantApplicationUtil.getAmountFormate(getOverdueChargeRecovery().getLovDescFinFormatter()));
		this.totOvrDueChrg.setMaxlength(18);
		this.totOvrDueChrg.setFormat(PennantApplicationUtil.getAmountFormate(getOverdueChargeRecovery().getLovDescFinFormatter()));
		this.totOvrDueChrgWaived.setMaxlength(18);
		this.totOvrDueChrgWaived.setFormat(PennantApplicationUtil.getAmountFormate(getOverdueChargeRecovery().getLovDescFinFormatter()));
		this.totOvrDueChrgPaid.setMaxlength(18);
		this.totOvrDueChrgPaid.setFormat(PennantApplicationUtil.getAmountFormate(getOverdueChargeRecovery().getLovDescFinFormatter()));
		this.totOvrDueChrgBal.setMaxlength(18);
		this.totOvrDueChrgBal.setFormat(PennantApplicationUtil.getAmountFormate(getOverdueChargeRecovery().getLovDescFinFormatter()));
		this.finBrnm.setMaxlength(8);
		this.finType.setMaxlength(8);
		this.finCustId.setMaxlength(19);
		this.finCcy.setMaxlength(3);
		this.finODDate.setFormat(PennantConstants.dateFormat);
		this.finODPri.setMaxlength(18);
		this.finODPri.setFormat(PennantApplicationUtil.getAmountFormate(getOverdueChargeRecovery().getLovDescFinFormatter()));
		this.finODPft.setMaxlength(18);
		this.finODPft.setFormat(PennantApplicationUtil.getAmountFormate(getOverdueChargeRecovery().getLovDescFinFormatter()));
		this.finODTot.setMaxlength(18);
		this.finODTot.setFormat(PennantApplicationUtil.getAmountFormate(getOverdueChargeRecovery().getLovDescFinFormatter()));
		this.finODCRuleCode.setMaxlength(20);
		this.finODCPLAc.setMaxlength(20);
		this.finODCCAc.setMaxlength(20);
		this.finODCPLShare.setMaxlength(5);
		this.finODCPLShare.setFormat(PennantApplicationUtil.getAmountFormate(2));
		this.finODCCustCtg.setMaxlength(8);
		this.finODCOn.setMaxlength(8);
		this.finODC.setMaxlength(18);
		this.finODC.setFormat(PennantApplicationUtil.getAmountFormate(getOverdueChargeRecovery().getLovDescFinFormatter()));
		this.finODCGraceDays.setMaxlength(10);
		this.finODCMaxWaiver.setMaxlength(5);
		this.finODCMaxWaiver.setFormat(PennantApplicationUtil.getAmountFormate(2));
		this.finODCPenalty.setMaxlength(18);
		this.finODCPenalty.setFormat(PennantApplicationUtil.getAmountFormate(getOverdueChargeRecovery().getLovDescFinFormatter()));
		this.finODCWaived.setMaxlength(18);
		this.finODCWaived.setFormat(PennantApplicationUtil.getAmountFormate(getOverdueChargeRecovery().getLovDescFinFormatter()));
		this.finODCPLPenalty.setMaxlength(18);
		this.finODCPLPenalty.setFormat(PennantApplicationUtil.getAmountFormate(getOverdueChargeRecovery().getLovDescFinFormatter()));
		this.finODCCPenalty.setMaxlength(18);
		this.finODCCPenalty.setFormat(PennantApplicationUtil.getAmountFormate(getOverdueChargeRecovery().getLovDescFinFormatter()));
		this.finODCPaid.setMaxlength(18);
		this.finODCPaid.setFormat(PennantApplicationUtil.getAmountFormate(getOverdueChargeRecovery().getLovDescFinFormatter()));
		this.balChrgRecovery.setMaxlength(18);
		this.balChrgRecovery.setFormat(PennantApplicationUtil.getAmountFormate(getOverdueChargeRecovery().getLovDescFinFormatter()));
		this.finODCLastPaidDate.setFormat(PennantConstants.dateFormat);
		this.finODCRecoverySts.setMaxlength(8);

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

		getUserWorkspace().alocateAuthorities("OverdueChargeRecoveryDialog");

		this.btnNew.setVisible(false);
		this.btnEdit.setVisible(false);
		this.btnDelete.setVisible(false);
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_OverdueChargeRecoveryDialog_btnSave"));
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
	public void onClose$window_OverdueChargeRecoveryDialog(Event event) throws Exception {
		logger.debug(event.toString());
		if(this.isInquiry){
			this.window_OverdueChargeRecoveryDialog.onClose();
			this.overdueChargeRecoveryListCtrl.window_OverdueChargeRecoveryList.setVisible(true);
		}else{
			doClose();
		}
		
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
		PTMessageUtils.showHelpWindow(event, window_OverdueChargeRecoveryDialog);
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
			if(this.isInquiry){
				this.window_OverdueChargeRecoveryDialog.onClose();
				this.overdueChargeRecoveryListCtrl.window_OverdueChargeRecoveryList.setVisible(true);
			}else{
				doClose();
			}
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
			closeDialog(this.window_OverdueChargeRecoveryDialog, "OverdueChargeRecovery");	
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
	 * @param aOverdueChargeRecovery
	 *            OverdueChargeRecovery
	 */
	public void doWriteBeanToComponents(OverdueChargeRecovery aOverdueChargeRecovery) {
		logger.debug("Entering") ;
		//Basic Details
		this.finReference.setValue(aOverdueChargeRecovery.getFinReference());
		this.finStartDate.setValue(aOverdueChargeRecovery.getLovDescFinStartDate());
		this.finMaturityDate.setValue(aOverdueChargeRecovery.getLovDescMaturityDate());
		this.finAmt.setValue(PennantAppUtil.formateAmount(aOverdueChargeRecovery.getLovDescFinAmount(),
				aOverdueChargeRecovery.getLovDescFinFormatter()));
		this.curFinAmt.setValue(PennantAppUtil.formateAmount(aOverdueChargeRecovery.getLovDescCurFinAmt(),
				aOverdueChargeRecovery.getLovDescFinFormatter()));
		this.curSchPriDue.setValue(PennantAppUtil.formateAmount(aOverdueChargeRecovery.getLovDescCurSchPriDue(),
				aOverdueChargeRecovery.getLovDescFinFormatter()));
		this.curSchPftDue.setValue(PennantAppUtil.formateAmount(aOverdueChargeRecovery.getLovDescCurSchPftDue(),
				aOverdueChargeRecovery.getLovDescFinFormatter()));
		this.totOvrDueChrg.setValue(PennantAppUtil.formateAmount(aOverdueChargeRecovery.getLovDescTotOvrDueChrg(),
				aOverdueChargeRecovery.getLovDescFinFormatter()));
		this.totOvrDueChrgWaived.setValue(PennantAppUtil.formateAmount(aOverdueChargeRecovery.getLovDescTotOvrDueChrgWaived(),
				aOverdueChargeRecovery.getLovDescFinFormatter()));
		this.totOvrDueChrgPaid.setValue(PennantAppUtil.formateAmount(aOverdueChargeRecovery.getLovDescTotOvrDueChrgPaid(),
				aOverdueChargeRecovery.getLovDescFinFormatter()));
		this.totOvrDueChrgBal.setValue(PennantAppUtil.formateAmount(aOverdueChargeRecovery.getLovDescTotOvrDueChrgBal(),
				aOverdueChargeRecovery.getLovDescFinFormatter()));
		
		//Overdue Recovery Details
		this.finSchdDate.setValue(aOverdueChargeRecovery.getFinODSchdDate());
		this.finODDate.setValue(aOverdueChargeRecovery.getMovementDate());
	//	this.finODCRuleCode.setValue(aOverdueChargeRecovery.getFinODCRuleCode());
	//	this.finODCCustCtg.setValue(aOverdueChargeRecovery.getFinODCCustCtg());
		fillComboBox(this.cbFinODFor, aOverdueChargeRecovery.getFinODFor(),finOdForList,"");
		this.finODTot.setValue(PennantAppUtil.formateAmount(aOverdueChargeRecovery.getFinCurODAmt(),
				aOverdueChargeRecovery.getLovDescFinFormatter()));
		this.finODPri.setValue(PennantAppUtil.formateAmount(aOverdueChargeRecovery.getFinCurODPri(),
				aOverdueChargeRecovery.getLovDescFinFormatter()));
		this.finODPft.setValue(PennantAppUtil.formateAmount(aOverdueChargeRecovery.getFinCurODPft(),
				aOverdueChargeRecovery.getLovDescFinFormatter()));
		fillComboBox(this.cbFinODCType, aOverdueChargeRecovery.getPenaltyType(),finODCTypeList,"");
		this.finODCOn.setValue(aOverdueChargeRecovery.getPenaltyCalOn());
		
		/*this.finODCPLShare.setValue(aOverdueChargeRecovery.getFinODCPLShare());		
		this.finODCPLAc.setValue(aOverdueChargeRecovery.getFinODCPLAc());
		this.finODCCAc.setValue(aOverdueChargeRecovery.getFinODCCAc());*/
		this.finODCPenalty.setValue(PennantAppUtil.formateAmount(aOverdueChargeRecovery.getPenalty(),
				aOverdueChargeRecovery.getLovDescFinFormatter()));
		
		//this.finODCAlwWaiver.setChecked(aOverdueChargeRecovery.isFinODCAlwWaiver());
		this.finODCMaxWaiver.setValue(aOverdueChargeRecovery.getMaxWaiver());
		this.finODCWaived.setValue(PennantAppUtil.formateAmount(aOverdueChargeRecovery.getWaivedAmt(),
				aOverdueChargeRecovery.getLovDescFinFormatter()));
		/*if(aOverdueChargeRecovery.isFinODCAlwWaiver()) {
			this.oDCWaivedRow.setVisible(true);
			this.oDCAlwWaiverRow.setVisible(true);
		}else {*/
			this.oDCWaivedRow.setVisible(false);
			this.oDCAlwWaiverRow.setVisible(false);
	//	}
		if(this.isInquiry) {
			this.finODCWaived.setReadonly(true);
		}
		/*this.finODCPLPenalty.setValue(PennantAppUtil.formateAmount(aOverdueChargeRecovery.getFinODCPLPenalty(),
				aOverdueChargeRecovery.getLovDescFinFormatter()));
		this.finODCCPenalty.setValue(PennantAppUtil.formateAmount(aOverdueChargeRecovery.getFinODCCPenalty(),
				aOverdueChargeRecovery.getLovDescFinFormatter()));
		this.finODCPaid.setValue(PennantAppUtil.formateAmount(aOverdueChargeRecovery.getFinODCPaid(),
				aOverdueChargeRecovery.getLovDescFinFormatter()));
		//FinODCCPenalty - FinODCPaid - FinODCWaived
		this.balChrgRecovery.setValue(PennantAppUtil.formateAmount(aOverdueChargeRecovery.getFinODCPenalty().
				subtract(aOverdueChargeRecovery.getFinODCPaid()).subtract(
						aOverdueChargeRecovery.getFinODCWaiverPaid()),
						aOverdueChargeRecovery.getLovDescFinFormatter()));*/
		
		//Extra fields 
		/*this.finBrnm.setValue(aOverdueChargeRecovery.getFinBranch());
		this.finType.setValue(aOverdueChargeRecovery.getFinType());
		this.finCustId.setValue(aOverdueChargeRecovery.getFinCustId());
		this.lovDescCustCIF.setValue(aOverdueChargeRecovery.getLovDescCustCIF());
		this.custShrtName.setValue(aOverdueChargeRecovery.getLovDescCustShrtName());
		this.finCcy.setValue(aOverdueChargeRecovery.getFinCcy());
		//this.finODCSweep.setChecked(aOverdueChargeRecovery.isFinODCSweep());
		this.finODC.setValue(PennantAppUtil.formateAmount(aOverdueChargeRecovery.getFinODC(),
				aOverdueChargeRecovery.getLovDescFinFormatter()));
		this.finODCGraceDays.setValue(aOverdueChargeRecovery.getFinODCGraceDays());
		this.finODCLastPaidDate.setValue(aOverdueChargeRecovery.getFinODCLastPaidDate());
		this.finODCRecoverySts.setValue(aOverdueChargeRecovery.getFinODCRecoverySts());
		this.recordStatus.setValue(aOverdueChargeRecovery.getRecordStatus());
		if(aOverdueChargeRecovery.getFinODCRecoverySts().equals("R")&& !this.isInquiry) { 
			this.btnRecoverNow.setVisible(true);
		}*/
		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aOverdueChargeRecovery
	 */
	public void doWriteComponentsToBean(OverdueChargeRecovery aOverdueChargeRecovery) {
		logger.debug("Entering") ;
		doSetLOVValidation();

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		try {
			aOverdueChargeRecovery.setFinReference(this.finReference.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aOverdueChargeRecovery.setFinODSchdDate(this.finSchdDate.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			if(getComboboxValue(this.cbFinODFor).equals("#")) {
				throw new WrongValueException(
						this.cbFinODFor,
						Labels.getLabel(
								"STATIC_INVALID",
								new String[] { Labels
										.getLabel("label_OverdueChargeRecoveryDialog_FinODFor.value") }));
			}
			aOverdueChargeRecovery.setFinODFor(getComboboxValue(this.cbFinODFor));
		} catch (WrongValueException we) {
			wve.add(we);
		}
	/*	try {
			aOverdueChargeRecovery.setFinBranch(this.finBrnm.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aOverdueChargeRecovery.setFinType(this.finType.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aOverdueChargeRecovery.setFinCustId(this.finCustId.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aOverdueChargeRecovery.setFinCcy(this.finCcy.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aOverdueChargeRecovery.setFinODDate(this.finODDate.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			if(this.finODPri.getValue()!=null){
				aOverdueChargeRecovery.setFinODPri(PennantAppUtil.unFormateAmount(this.finODPri.getValue(), 
						aOverdueChargeRecovery.getLovDescFinFormatter()));
			}
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			if(this.finODPft.getValue()!=null){
				aOverdueChargeRecovery.setFinODPft(PennantAppUtil.unFormateAmount(this.finODPft.getValue(), 
						aOverdueChargeRecovery.getLovDescFinFormatter()));
			}
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			if(this.finODTot.getValue()!=null){
				aOverdueChargeRecovery.setFinODTot(PennantAppUtil.unFormateAmount(this.finODTot.getValue(), 
						aOverdueChargeRecovery.getLovDescFinFormatter()));
			}
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aOverdueChargeRecovery.setFinODCRuleCode(this.finODCRuleCode.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}*/
		/*try {
			aOverdueChargeRecovery.setFinODCPLAc(this.finODCPLAc.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aOverdueChargeRecovery.setFinODCCAc(this.finODCCAc.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			if(this.finODCPLShare.getValue()!=null){
				aOverdueChargeRecovery.setFinODCPLShare(this.finODCPLShare.getValue());
			}
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aOverdueChargeRecovery.setFinODCSweep(this.finODCSweep.isChecked());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aOverdueChargeRecovery.setFinODCCustCtg(this.finODCCustCtg.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}*/
	/*	try {
			if(getComboboxValue(this.cbFinODCType).equals("#")) {
				throw new WrongValueException(
						this.cbFinODCType,
						Labels.getLabel(
								"STATIC_INVALID",
								new String[] { Labels
										.getLabel("label_OverdueChargeRecoveryDialog_FinODCType.value") }));
			}
			aOverdueChargeRecovery.setFinODCType(getComboboxValue(this.cbFinODCType));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aOverdueChargeRecovery.setFinODCOn(this.finODCOn.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			if(this.finODC.getValue()!=null){
				aOverdueChargeRecovery.setFinODC(PennantAppUtil.unFormateAmount(this.finODC.getValue(), 
						aOverdueChargeRecovery.getLovDescFinFormatter()));
			}
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aOverdueChargeRecovery.setFinODCGraceDays(this.finODCGraceDays.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aOverdueChargeRecovery.setFinODCAlwWaiver(this.finODCAlwWaiver.isChecked());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			if(this.finODCMaxWaiver.getValue()!=null){
				aOverdueChargeRecovery.setFinODCMaxWaiver(PennantAppUtil.unFormateAmount(this.finODCMaxWaiver.getValue(), 
						aOverdueChargeRecovery.getLovDescFinFormatter()));
			}
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			if(this.finODCPenalty.getValue()!=null){
				aOverdueChargeRecovery.setFinODCPenalty(PennantAppUtil.unFormateAmount(this.finODCPenalty.getValue(),
						aOverdueChargeRecovery.getLovDescFinFormatter()));
			}
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			if(this.finODCWaived.getValue()!=null){
				BigDecimal reqWaiver = PennantAppUtil.getPercentageValue(this.finODCPenalty.getValue(),
						getOverdueChargeRecovery().getFinODCMaxWaiver());
				if(!this.finODCWaived.isDisabled() && this.finODCWaived.getValue() != null) {
					if(this.finODCWaived.getValue().compareTo(this.finODCPenalty.getValue().
									subtract(this.finODCPaid.getValue())) > 0 ||
									this.finODCWaived.getValue().compareTo(reqWaiver) > 0) {
						throw new WrongValueException(
								this.finODCWaived, Labels.getLabel("FIELD_IS_EQUAL_OR_LESSER",new String[] {
										Labels.getLabel("label_OverdueChargeRecoveryDialog_FinODCWaived.value"),
										PennantAppUtil.formatAmount(reqWaiver,getOverdueChargeRecovery().
												getLovDescFinFormatter(),false).toString()}));
					}
				}else if(this.finODCWaived.getValue() == null) {
					this.finODCWaived.setValue(new BigDecimal(0));
				}
				aOverdueChargeRecovery.setFinODCWaived(PennantAppUtil.unFormateAmount(this.finODCWaived.getValue(),
						aOverdueChargeRecovery.getLovDescFinFormatter()));
			}
		}catch (WrongValueException we ) {
			wve.add(we);
		}*/
		//(FinODCPenalty - FinODCWaived) * FinODCPLShare/100
		/*try {
			aOverdueChargeRecovery.setFinODCPLPenalty(PennantAppUtil.unFormateAmount(
					this.finODCPLPenalty.getValue(),
					aOverdueChargeRecovery.getLovDescFinFormatter()));
		}catch (WrongValueException we ) {
			wve.add(we);
		}

		//(FinODCPenalty - FinODCWaived) - FinODCPLPenalty
		try {
			if(this.finODCCPenalty.getValue()!=null) {
				aOverdueChargeRecovery.setFinODCCPenalty(PennantAppUtil.unFormateAmount(this.finODCCPenalty.getValue(),
						aOverdueChargeRecovery.getLovDescFinFormatter()));
			}
		}catch (WrongValueException we ) {
			wve.add(we);
		}

		//FinODCPaid + PaidAmount
		try {
			if(this.finODCPaid.getValue()!=null) {				
				aOverdueChargeRecovery.setFinODCPaid(PennantAppUtil.unFormateAmount(this.finODCPaid.getValue(),
						aOverdueChargeRecovery.getLovDescFinFormatter()).add(paidAmount));
			}
		}catch (WrongValueException we ) {
			wve.add(we);
		}*/
		/*
		try {
			aOverdueChargeRecovery.setFinODCLastPaidDate(this.dateValueDate);
		}catch (WrongValueException we ) {
			wve.add(we);
		}*/
		
		/*try {
			if((aOverdueChargeRecovery.getFinODCPenalty().subtract(
					aOverdueChargeRecovery.getFinODCPaid())).compareTo(new BigDecimal(0)) == 0){
				aOverdueChargeRecovery.setFinODCRecoverySts("C");
				aOverdueChargeRecovery.setFinODCWaiverPaid(aOverdueChargeRecovery.getFinODCWaived());
			}
		}catch (WrongValueException we ) {
			wve.add(we);
		}*/

		doRemoveValidation();
		doRemoveLOVValidation();

		if (wve.size()>0) {
			WrongValueException [] wvea = new WrongValueException[wve.size()];
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
	 * It checks if the dialog opens with a new or existing object and set the
	 * readOnly mode accordingly.
	 * 
	 * @param aOverdueChargeRecovery
	 * @throws InterruptedException
	 */
	public void doShowDialog(OverdueChargeRecovery aOverdueChargeRecovery) throws InterruptedException {
		logger.debug("Entering") ;

		// if aOverdueChargeRecovery == null then we opened the Dialog without
		// args for a given entity, so we get a new Obj().
		if (aOverdueChargeRecovery == null) {
			/** !!! DO NOT BREAK THE TIERS !!! */
			// We don't create a new DomainObject() in the frontend.
			// We GET it from the backend.
			aOverdueChargeRecovery = getOverdueChargeRecoveryService().getNewOverdueChargeRecovery();

			setOverdueChargeRecovery(aOverdueChargeRecovery);
		} else {
			setOverdueChargeRecovery(aOverdueChargeRecovery);
		}

		// set Readonly mode accordingly if the object is new or not.
		if (aOverdueChargeRecovery.isNew()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.finReference.focus();
		} else {
			this.finBrnm.focus();
			if (isWorkFlowEnabled()){
				this.btnNotes.setVisible(true);
				doEdit();
			}else{
				this.btnCtrl.setInitEdit();
				//Set delete button invisible
				btnDelete.setVisible(false);
				if(!this.isInquiry) {
					btnSave.setVisible(true);
				}
				doEdit();
				btnCancel.setVisible(false);
			}
		}

		try {
			// fill the components with the data
			doWriteBeanToComponents(aOverdueChargeRecovery);

			// stores the initial data for comparing if they are changed
			// during user action.
			doStoreInitValues();
			if(this.isInquiry){
				this.overdueChargeRecoveryListCtrl.window_OverdueChargeRecoveryList.setVisible(false);
				this.window_OverdueChargeRecoveryDialog.doModal();
			}else{
				setDialog(this.window_OverdueChargeRecoveryDialog);
			}
			
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
		this.oldVar_finSchdDate = this.finSchdDate.getValue();
		this.oldVar_finODFor = this.cbFinODFor.getSelectedIndex();
		this.oldVar_finBrnm = this.finBrnm.getValue();
		this.oldVar_finType = this.finType.getValue();
		this.oldVar_finCustId = this.finCustId.longValue();
		this.oldVar_finCcy = this.finCcy.getValue();
		this.oldVar_finODDate = this.finODDate.getValue();
		this.oldVar_finODPri = this.finODPri.getValue();
		this.oldVar_finODPft = this.finODPft.getValue();
		this.oldVar_finODTot = this.finODTot.getValue();
		this.oldVar_finODCRuleCode = this.finODCRuleCode.getValue();
		this.oldVar_finODCPLAc = this.finODCPLAc.getValue();
		this.oldVar_finODCCAc = this.finODCCAc.getValue();
		this.oldVar_finODCPLShare = this.finODCPLShare.getValue();
		this.oldVar_finODCSweep = this.finODCSweep.isChecked();
		this.oldVar_finODCCustCtg = this.finODCCustCtg.getValue();
		this.oldVar_finODCType = this.cbFinODCType.getSelectedIndex();
		this.oldVar_finODCOn = this.finODCOn.getValue();
		this.oldVar_finODC = this.finODC.getValue();
		this.oldVar_finODCGraceDays = this.finODCGraceDays.intValue();	
		this.oldVar_finODCAlwWaiver = this.finODCAlwWaiver.isChecked();
		this.oldVar_finODCMaxWaiver = this.finODCMaxWaiver.getValue();
		this.oldVar_finODCPenalty = this.finODCPenalty.getValue();
		this.oldVar_finODCWaived = this.finODCWaived.getValue();
		this.oldVar_finODCPLPenalty = this.finODCPLPenalty.getValue();
		this.oldVar_finODCCPenalty = this.finODCCPenalty.getValue();
		this.oldVar_finODCPaid = this.finODCPaid.getValue();
		this.oldVar_finODCLastPaidDate = this.finODCLastPaidDate.getValue();
		this.oldVar_finODCRecoverySts = this.finODCRecoverySts.getValue();
		this.oldVar_recordStatus = this.recordStatus.getValue();
		logger.debug("Leaving") ;
	}

	/**
	 * Resets the init values from mem vars. <br>
	 */
	private void doResetInitValues() {
		logger.debug("Entering");
		this.finReference.setValue(this.oldVar_finReference);
		this.finSchdDate.setValue(this.oldVar_finSchdDate);
		this.cbFinODFor.setSelectedIndex(this.oldVar_finODFor);
		this.finBrnm.setValue(this.oldVar_finBrnm);
		this.finType.setValue(this.oldVar_finType);
		this.finCustId.setValue(this.oldVar_finCustId);
		this.finCcy.setValue(this.oldVar_finCcy);
		this.finODDate.setValue(this.oldVar_finODDate);
		this.finODPri.setValue(this.oldVar_finODPri);
		this.finODPft.setValue(this.oldVar_finODPft);
		this.finODTot.setValue(this.oldVar_finODTot);
		this.finODCRuleCode.setValue(this.oldVar_finODCRuleCode);
		this.finODCPLAc.setValue(this.oldVar_finODCPLAc);
		this.finODCCAc.setValue(this.oldVar_finODCCAc);
		this.finODCPLShare.setValue(this.oldVar_finODCPLShare);
		this.finODCSweep.setChecked(this.oldVar_finODCSweep);
		this.finODCCustCtg.setValue(this.oldVar_finODCCustCtg);
		this.cbFinODCType.setSelectedIndex(this.oldVar_finODCType);
		this.finODCOn.setValue(this.oldVar_finODCOn);
		this.finODC.setValue(this.oldVar_finODC);
		this.finODCGraceDays.setValue(this.oldVar_finODCGraceDays);
		this.finODCAlwWaiver.setChecked(this.oldVar_finODCAlwWaiver);
		this.finODCMaxWaiver.setValue(this.oldVar_finODCMaxWaiver);
		this.finODCPenalty.setValue(this.oldVar_finODCPenalty);
		this.finODCWaived.setValue(this.oldVar_finODCWaived);
		this.finODCPLPenalty.setValue(this.oldVar_finODCPLPenalty);
		this.finODCCPenalty.setValue(this.oldVar_finODCCPenalty);
		this.finODCPaid.setValue(this.oldVar_finODCPaid);
		this.finODCLastPaidDate.setValue(this.oldVar_finODCLastPaidDate);
		this.finODCRecoverySts.setValue(this.oldVar_finODCRecoverySts);
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
		if (this.oldVar_finReference != this.finReference.getValue()) {
			return true;
		}
		String old_finSchdDate = "";
		String new_finSchdDate ="";
		if (this.oldVar_finSchdDate!=null){
			old_finSchdDate=DateUtility.formatDate(this.oldVar_finSchdDate,PennantConstants.dateFormat);
		}
		if (this.finSchdDate.getValue()!=null){
			new_finSchdDate=DateUtility.formatDate(this.finSchdDate.getValue(),PennantConstants.dateFormat);
		}
		if (!StringUtils.trimToEmpty(old_finSchdDate).equals(StringUtils.trimToEmpty(new_finSchdDate))) {
			return true;
		}
		if (this.oldVar_finODFor != this.cbFinODFor.getSelectedIndex()) {
			return true;
		}
		if (this.oldVar_finBrnm != this.finBrnm.getValue()) {
			return true;
		}
		if (this.oldVar_finType != this.finType.getValue()) {
			return true;
		}
		if (this.oldVar_finCustId != this.finCustId.longValue()) {
			return true;
		}
		if (this.oldVar_finCcy != this.finCcy.getValue()) {
			return true;
		}
		String old_finODDate = "";
		String new_finODDate ="";
		if (this.oldVar_finODDate!=null){
			old_finODDate=DateUtility.formatDate(this.oldVar_finODDate,PennantConstants.dateFormat);
		}
		if (this.finODDate.getValue()!=null){
			new_finODDate=DateUtility.formatDate(this.finODDate.getValue(),PennantConstants.dateFormat);
		}
		if (!StringUtils.trimToEmpty(old_finODDate).equals(StringUtils.trimToEmpty(new_finODDate))) {
			return true;
		}
		if (this.oldVar_finODPri != this.finODPri.getValue()) {
			return true;
		}
		if (this.oldVar_finODPft != this.finODPft.getValue()) {
			return true;
		}
		if (this.oldVar_finODTot != this.finODTot.getValue()) {
			return true;
		}
		if (this.oldVar_finODCRuleCode != this.finODCRuleCode.getValue()) {
			return true;
		}
		if (this.oldVar_finODCPLAc != this.finODCPLAc.getValue()) {
			return true;
		}
		if (this.oldVar_finODCCAc != this.finODCCAc.getValue()) {
			return true;
		}
		if (this.oldVar_finODCPLShare != this.finODCPLShare.getValue()) {
			return true;
		}
		if (this.oldVar_finODCSweep != this.finODCSweep.isChecked()) {
			return true;
		}
		if (this.oldVar_finODCCustCtg != this.finODCCustCtg.getValue()) {
			return true;
		}
		if (this.oldVar_finODCType != this.cbFinODCType.getSelectedIndex()) {
			return true;
		}
		if (this.oldVar_finODCOn != this.finODCOn.getValue()) {
			return true;
		}
		if (this.oldVar_finODC != this.finODC.getValue()) {
			return true;
		}
		if (this.oldVar_finODCGraceDays != this.finODCGraceDays.intValue()) {
			return  true;
		}
		if (this.oldVar_finODCAlwWaiver != this.finODCAlwWaiver.isChecked()) {
			return true;
		}
		if (this.oldVar_finODCMaxWaiver != this.finODCMaxWaiver.getValue()) {
			return true;
		}
		if (this.oldVar_finODCPenalty != this.finODCPenalty.getValue()) {
			return true;
		}
		if (this.oldVar_finODCWaived != this.finODCWaived.getValue()) {
			return true;
		}
		if (this.oldVar_finODCPLPenalty != this.finODCPLPenalty.getValue()) {
			return true;
		}
		if (this.oldVar_finODCCPenalty != this.finODCCPenalty.getValue()) {
			return true;
		}
		if (this.oldVar_finODCPaid != this.finODCPaid.getValue()) {
			return true;
		}
		String old_finODCLastPaidDate = "";
		String new_finODCLastPaidDate ="";
		if (this.oldVar_finODCLastPaidDate!=null){
			old_finODCLastPaidDate=DateUtility.formatDate(this.oldVar_finODCLastPaidDate,PennantConstants.dateFormat);
		}
		if (this.finODCLastPaidDate.getValue()!=null){
			new_finODCLastPaidDate=DateUtility.formatDate(this.finODCLastPaidDate.getValue(),PennantConstants.dateFormat);
		}
		if (!StringUtils.trimToEmpty(old_finODCLastPaidDate).equals(StringUtils.trimToEmpty(new_finODCLastPaidDate))) {
			return true;
		}
		if (this.oldVar_finODCRecoverySts != this.finODCRecoverySts.getValue()) {
			return true;
		}
		logger.debug("Leaving"); 
		return false;
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

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// +++++++++++++++++++++++++ crud operations +++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * Deletes a OverdueChargeRecovery object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug("Entering");	
		final OverdueChargeRecovery aOverdueChargeRecovery = new OverdueChargeRecovery();
		BeanUtils.copyProperties(getOverdueChargeRecovery(), aOverdueChargeRecovery);
		String tranType=PennantConstants.TRAN_WF;

		// Show a confirm box
		final String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record") + "\n\n --> " + aOverdueChargeRecovery.getFinReference();
		final String title = Labels.getLabel("message.Deleting.Record");
		MultiLineMessageBox.doSetTemplate();

		int conf =  (MultiLineMessageBox.show(msg, title, MultiLineMessageBox.YES| MultiLineMessageBox.NO, Messagebox.QUESTION, true));

		if (conf==MultiLineMessageBox.YES){
			logger.debug("doDelete: Yes");

			if (StringUtils.trimToEmpty(aOverdueChargeRecovery.getRecordType()).equals("")){
				aOverdueChargeRecovery.setVersion(aOverdueChargeRecovery.getVersion()+1);
				aOverdueChargeRecovery.setRecordType(PennantConstants.RECORD_TYPE_DEL);

				if (isWorkFlowEnabled()){
					aOverdueChargeRecovery.setNewRecord(true);
					tranType=PennantConstants.TRAN_WF;
				}else{
					tranType=PennantConstants.TRAN_DEL;
				}
			}

			try {
				if(doProcess(aOverdueChargeRecovery,tranType)){
					refreshList();
					closeDialog(this.window_OverdueChargeRecoveryDialog, "OverdueChargeRecovery"); 
				}

			}catch (DataAccessException e){
				logger.error("doDelete " + e);
				showMessage(e);
			}

		}
		logger.debug("Leaving");
	}

	/**
	 * Create a new OverdueChargeRecovery object. <br>
	 */
	private void doNew() {
		logger.debug("Entering");

		final OverdueChargeRecovery aOverdueChargeRecovery = getOverdueChargeRecoveryService().getNewOverdueChargeRecovery();
		aOverdueChargeRecovery.setNewRecord(true);
		setOverdueChargeRecovery(aOverdueChargeRecovery);
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

		if (getOverdueChargeRecovery().isNewRecord()){
			this.finReference.setReadonly(false);
			this.btnCancel.setVisible(false);
		}else{
			this.finReference.setReadonly(true);
			this.btnCancel.setVisible(true);
		}

		readOnlyComponent(isReadOnly("OverdueChargeRecoveryDialog_finSchdDate"), this.finSchdDate);
		this.finSchdDate.setButtonVisible(false);
		readOnlyComponent(isReadOnly("OverdueChargeRecoveryDialog_finODFor"), this.cbFinODFor);
		readOnlyComponent(isReadOnly("OverdueChargeRecoveryDialog_finBrnm"), this.finBrnm);
		readOnlyComponent(isReadOnly("OverdueChargeRecoveryDialog_finType"), this.finType);
		readOnlyComponent(isReadOnly("OverdueChargeRecoveryDialog_finCustId"), this.finCustId);
		readOnlyComponent(isReadOnly("OverdueChargeRecoveryDialog_finCcy"), this.finCcy);
		readOnlyComponent(isReadOnly("OverdueChargeRecoveryDialog_finODDate"), this.finODDate);
		this.finODDate.setButtonVisible(false);
		readOnlyComponent(isReadOnly("OverdueChargeRecoveryDialog_finODPri"), this.finODPri);
		readOnlyComponent(isReadOnly("OverdueChargeRecoveryDialog_finODPft"), this.finODPft);
		readOnlyComponent(isReadOnly("OverdueChargeRecoveryDialog_finODTot"), this.finODTot);
		readOnlyComponent(isReadOnly("OverdueChargeRecoveryDialog_finODCRuleCode"), this.finODCRuleCode);
		readOnlyComponent(isReadOnly("OverdueChargeRecoveryDialog_finODCPLAc"), this.finODCPLAc);
		readOnlyComponent(isReadOnly("OverdueChargeRecoveryDialog_finODCCAc"), this.finODCCAc);
		readOnlyComponent(isReadOnly("OverdueChargeRecoveryDialog_finODCPLShare"), this.finODCPLShare);
		readOnlyComponent(isReadOnly("OverdueChargeRecoveryDialog_finODCSweep"), this.finODCSweep);
		readOnlyComponent(isReadOnly("OverdueChargeRecoveryDialog_finODCCustCtg"), this.finODCCustCtg);
		readOnlyComponent(isReadOnly("OverdueChargeRecoveryDialog_finODCType"), this.cbFinODCType);
		readOnlyComponent(isReadOnly("OverdueChargeRecoveryDialog_finODCOn"), this.finODCOn);
		readOnlyComponent(isReadOnly("OverdueChargeRecoveryDialog_finODC"), this.finODC);
		readOnlyComponent(isReadOnly("OverdueChargeRecoveryDialog_finODCGraceDays"), this.finODCGraceDays);
		readOnlyComponent(isReadOnly("OverdueChargeRecoveryDialog_finODCAlwWaiver"), this.finODCAlwWaiver);
		readOnlyComponent(isReadOnly("OverdueChargeRecoveryDialog_finODCMaxWaiver"), this.finODCMaxWaiver);
		readOnlyComponent(isReadOnly("OverdueChargeRecoveryDialog_finODCPenalty"), this.finODCPenalty);
		readOnlyComponent(isReadOnly("OverdueChargeRecoveryDialog_finODCWaived"), this.finODCWaived);
		readOnlyComponent(isReadOnly("OverdueChargeRecoveryDialog_finODCPLPenalty"), this.finODCPLPenalty);
		readOnlyComponent(isReadOnly("OverdueChargeRecoveryDialog_finODCCPenalty"), this.finODCCPenalty);
		readOnlyComponent(isReadOnly("OverdueChargeRecoveryDialog_finODCPaid"), this.finODCPaid);
		readOnlyComponent(isReadOnly("OverdueChargeRecoveryDialog_finODCLastPaidDate"), this.finODCLastPaidDate);
		readOnlyComponent(isReadOnly("OverdueChargeRecoveryDialog_finODCRecoverySts"), this.finODCRecoverySts);

		if (isWorkFlowEnabled()){
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}

			if (this.overdueChargeRecovery.isNewRecord()){
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(false);
			}else{
				this.btnCtrl.setWFBtnStatus_Edit(isFirstTask());
			}
		}/*else{
			this.btnCtrl.setBtnStatus_Edit();
			btnCancel.setVisible(true);
		}*/
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
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug("Entering");
		final OverdueChargeRecovery aOverdueChargeRecovery = new OverdueChargeRecovery();
		BeanUtils.copyProperties(getOverdueChargeRecovery(), aOverdueChargeRecovery);
		boolean isNew = false;

		// fill the OverdueChargeRecovery object with the components data
		doWriteComponentsToBean(aOverdueChargeRecovery);

		// Write the additional validations as per below example
		// get the selected branch object from the listbox
		// Do data level validations here

		isNew = aOverdueChargeRecovery.isNew();
		String tranType="";

		if(isWorkFlowEnabled()){
			tranType =PennantConstants.TRAN_WF;
			if (StringUtils.trimToEmpty(aOverdueChargeRecovery.getRecordType()).equals("")){
				aOverdueChargeRecovery.setVersion(aOverdueChargeRecovery.getVersion()+1);
				if(isNew){
					aOverdueChargeRecovery.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else{
					aOverdueChargeRecovery.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aOverdueChargeRecovery.setNewRecord(true);
				}
			}
		}else{
			aOverdueChargeRecovery.setVersion(aOverdueChargeRecovery.getVersion()+1);
			if(isNew){
				tranType =PennantConstants.TRAN_ADD;
			}else{
				tranType =PennantConstants.TRAN_UPD;
			}
		}

		// save it to database
		try {

			if(doProcess(aOverdueChargeRecovery,tranType)){
				refreshList();
				closeDialog(this.window_OverdueChargeRecoveryDialog, "OverdueChargeRecovery");
			}

		} catch (final DataAccessException e) {
			logger.error(e);
			showMessage(e);
		}
		logger.debug("Leaving");
	}

	private boolean doProcess(OverdueChargeRecovery aOverdueChargeRecovery,String tranType){
		logger.debug("Entering");
		boolean processCompleted=false;
		AuditHeader auditHeader =  null;
		String nextRoleCode="";

		aOverdueChargeRecovery.setLastMntBy(getUserWorkspace().getLoginUserDetails().getLoginUsrID());
		aOverdueChargeRecovery.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aOverdueChargeRecovery.setUserDetails(getUserWorkspace().getLoginUserDetails());

		if (isWorkFlowEnabled()) {
			String taskId = getWorkFlow().getTaskId(getRole());
			String nextTaskId = "";
			//Upgraded to ZK-6.5.1.1 Added casting to String 	
			aOverdueChargeRecovery.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aOverdueChargeRecovery.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getWorkFlow().getNextTaskIds(taskId, aOverdueChargeRecovery);
				}

				if (PennantConstants.WF_Audit_Notes.equals(getWorkFlow().getAuditingReq(taskId,aOverdueChargeRecovery))) {
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


			if (!StringUtils.trimToEmpty(nextTaskId).equals("")) {
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

			aOverdueChargeRecovery.setTaskId(taskId);
			aOverdueChargeRecovery.setNextTaskId(nextTaskId);
			aOverdueChargeRecovery.setRoleCode(getRole());
			aOverdueChargeRecovery.setNextRoleCode(nextRoleCode);

			auditHeader =  getAuditHeader(aOverdueChargeRecovery, tranType);

			String operationRefs = getWorkFlow().getOperationRefs(taskId,aOverdueChargeRecovery);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader,null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader =  getAuditHeader(aOverdueChargeRecovery, PennantConstants.TRAN_WF);
					processCompleted  = doSaveProcess(auditHeader, list[i]);
					if(!processCompleted){
						break;
					}
				}
			}
		}else{

			auditHeader =  getAuditHeader(aOverdueChargeRecovery, tranType);
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

		OverdueChargeRecovery aOverdueChargeRecovery = (OverdueChargeRecovery) auditHeader.getAuditDetail().getModelData();

		try {

			while(retValue==PennantConstants.porcessOVERIDE){

				if (StringUtils.trimToEmpty(method).equalsIgnoreCase("")){
					if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)){
						auditHeader = getOverdueChargeRecoveryService().delete(auditHeader);
						deleteNotes=true;
					}else{
						auditHeader = getOverdueChargeRecoveryService().saveOrUpdate(auditHeader);	
					}

				}else{
					if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)){
						auditHeader = getOverdueChargeRecoveryService().doApprove(auditHeader);

						if(aOverdueChargeRecovery.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)){
							deleteNotes=true;
						}

					}else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)){
						auditHeader = getOverdueChargeRecoveryService().doReject(auditHeader);
						if(aOverdueChargeRecovery.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){
							deleteNotes=true;
						}

					}else{
						auditHeader.setErrorDetails(new ErrorDetails(PennantConstants.ERR_9999, Labels.getLabel("InvalidWorkFlowMethod"), null));
						retValue = ErrorControl.showErrorControl(this.window_OverdueChargeRecoveryDialog, auditHeader);
						return processCompleted; 
					}
				}

				auditHeader =	ErrorControl.showErrorDetails(this.window_OverdueChargeRecoveryDialog, auditHeader);
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


	private AuditHeader getAuditHeader(OverdueChargeRecovery aOverdueChargeRecovery, String tranType){
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aOverdueChargeRecovery.getBefImage(), aOverdueChargeRecovery);   
		return new AuditHeader(aOverdueChargeRecovery.getFinReference(),null,null,null,auditDetail,aOverdueChargeRecovery.getUserDetails(),getOverideMap());
	}

	private void showMessage(Exception e){
		AuditHeader auditHeader= new AuditHeader();
		try {
			auditHeader.setErrorDetails(new ErrorDetails(PennantConstants.ERR_UNDEF,e.getMessage(),null));
			ErrorControl.showErrorControl(this.window_OverdueChargeRecoveryDialog, auditHeader);
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
	}
	private void doRemoveLOVValidation() {
	}

	private Notes getNotes(){
		Notes notes = new Notes();
		notes.setModuleName("OverdueChargeRecovery");
		notes.setReference(getOverdueChargeRecovery().getFinReference());
		notes.setVersion(getOverdueChargeRecovery().getVersion());
		return notes;
	}

	private void doClearMessage() {
		logger.debug("Entering");
		this.finODCWaived.setErrorMessage("");
		this.finODCRecoverySts.setErrorMessage("");
		logger.debug("Leaving");
	}


	private void refreshList(){
		getOverdueChargeRecoveryListCtrl().findSearchObject();
		if(getOverdueChargeRecoveryListCtrl().listBoxOverdueChargeRecovery!=null){
			getOverdueChargeRecoveryListCtrl().listBoxOverdueChargeRecovery.getListModel();
		}
	} 

	/**
	 * when the "Recover Now" button is clicked. <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClick$btnRecoverNow(Event event) throws Exception {
		logger.debug("Entering"+event.toString());

		this.finODCWaived.clearErrorMessage();
	/*	BigDecimal reqWaiver = PennantAppUtil.getPercentageValue(this.finODCPenalty.getValue(),
				getOverdueChargeRecovery().getFinODCMaxWaiver());
		if(!this.finODCWaived.isDisabled() && this.finODCWaived.getValue() != null) {
			if(this.finODCWaived.getValue().compareTo(this.finODCPenalty.getValue().
							subtract(this.finODCPaid.getValue())) > 0 ||
							this.finODCWaived.getValue().compareTo(reqWaiver) > 0) {
				throw new WrongValueException(
						this.finODCWaived, Labels.getLabel("FIELD_IS_EQUAL_OR_LESSER",new String[] {
								Labels.getLabel("label_OverdueChargeRecoveryDialog_FinODCWaived.value"),
								PennantAppUtil.formatAmount(reqWaiver,getOverdueChargeRecovery().
										getLovDescFinFormatter(),false).toString()}));
			}
		}else if(this.finODCWaived.getValue() == null) {
			this.finODCWaived.setValue(new BigDecimal(0));
		}

		getOverdueChargeRecovery().setFinODCWaived(PennantAppUtil.unFormateAmount(this.finODCWaived.getValue(),
				getOverdueChargeRecovery().getLovDescFinFormatter()));
		
		//Check Finance is RIA Finance Type or Not
		boolean isRIAFinance = getFinanceTypeService().checkRIAFinance(this.finType.getValue());
		
		try {
			paidAmount = (BigDecimal) getRecoveryPostingsUtil().oDRPostingProcess(getOverdueChargeRecovery(),
					dateValueDate,isRIAFinance).get(0);
			if(paidAmount.compareTo(new BigDecimal(-1)) == 0) {
				Messagebox.show("\n"+"Insufficient balance.","Overdue Recovery Charge",
						MultiLineMessageBox.OK, MultiLineMessageBox.INFORMATION);
				paidAmount = new BigDecimal(0);
				this.doClose();
			}else {
				this.doSave();
			}
		} catch (AccountNotFoundException e) {
			logger.error(e);
			throw new AccountNotFoundException(e.getMessage()) {};
		} catch (IllegalAccessException e) {
			logger.error(e);
			throw new IllegalAccessException(e.getMessage()) {};
		} catch (InvocationTargetException e) {
			logger.error(e);
			throw new InvocationTargetException(e, e.getMessage()) {};
		}*/

		logger.debug("Leaving"+event.toString());
	}
	
	public void setOverideMap(HashMap<String, ArrayList<ErrorDetails>> overideMap) {
		this.overideMap = overideMap;
	}

	public HashMap<String, ArrayList<ErrorDetails>> getOverideMap() {
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
