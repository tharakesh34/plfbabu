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
 * FileName    		:  FinanceCampaignDialogCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  30-12-2011    														*
 *                                                                  						*
 * Modified Date    :  30-12-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 30-12-2011       Pennant	                 0.1                                            * 
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

package com.pennant.webui.solutionfactory.financecampaign;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
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
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.Notes;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.solutionfactory.FinanceCampaign;
import com.pennant.backend.service.PagedListService;
import com.pennant.backend.service.solutionfactory.FinanceCampaignService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.util.ErrorControl;
import com.pennant.util.PennantAppUtil;
import com.pennant.util.Constraint.AmountValidator;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.ButtonStatusCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.MultiLineMessageBox;
import com.pennant.webui.util.PTMessageUtils;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * /WEB-INF/pages/SolutionFactory/FinanceCampaign/financeCampaignDialog.zul file. <br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 */
public class FinanceCampaignDialogCtrl extends GFCBaseCtrl implements Serializable {

	private static final long serialVersionUID = 1L;
	private final static Logger logger = Logger.getLogger(FinanceCampaignDialogCtrl.class);
	
	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window window_FinanceCampaignDialog; // autowired
	protected Textbox fCCode; // autowired
	protected Textbox fCDesc; // autowired
	protected Textbox fCFinType; // autowired
	protected Checkbox fCIsAlwMD; // autowired
	protected Checkbox fCIsAlwGrace; // autowired
	protected Checkbox fCOrgPrfUnchanged; // autowired
	protected Textbox fCRateType; // autowired
	protected Textbox fCBaseRate; // autowired
	protected Textbox fCSplRate; // autowired
	protected Decimalbox fCIntRate; // autowired
	protected Textbox fCDftIntFrq; // autowired
	protected Checkbox fCIsIntCpz; // autowired
	protected Textbox fCCpzFrq; // autowired
	protected Checkbox fCIsRvwAlw; // autowired
	protected Textbox fCRvwFrq; // autowired
	protected Textbox fCGrcRateType; // autowired
	protected Textbox fCGrcBaseRate; // autowired
	protected Textbox fCGrcSplRate; // autowired
	protected Decimalbox fCGrcIntRate; // autowired
	protected Textbox fCGrcDftIntFrq; // autowired
	protected Checkbox fCGrcIsIntCpz; // autowired
	protected Textbox fCGrcCpzFrq; // autowired
	protected Checkbox fCGrcIsRvwAlw; // autowired
	protected Textbox fCGrcRvwFrq; // autowired
	protected Decimalbox fCMinTerm; // autowired
	protected Decimalbox fCMaxTerm; // autowired
	protected Decimalbox fCDftTerms; // autowired
	protected Textbox fCRpyFrq; // autowired
	protected Textbox fCRepayMethod; // autowired
	protected Checkbox fCIsAlwPartialRpy; // autowired
	protected Checkbox fCIsAlwDifferment; // autowired
	protected Decimalbox fCMaxDifferment; // autowired
	protected Checkbox fCIsAlwFrqDifferment; // autowired
	protected Decimalbox fCMaxFrqDifferment; // autowired
	protected Checkbox fCIsAlwEarlyRpy; // autowired
	protected Checkbox fCIsAlwEarlySettle; // autowired
	protected Checkbox fCIsDwPayRequired; // autowired
	protected Textbox fCRvwRateApplFor; // autowired
	protected Checkbox fCAlwRateChangeAnyDate; // autowired
	protected Textbox fCGrcRvwRateApplFor; // autowired
	protected Checkbox fCIsIntCpzAtGrcEnd; // autowired
	protected Checkbox fCGrcAlwRateChgAnyDate; // autowired
	protected Decimalbox fCMinDownPayAmount; // autowired
	protected Textbox fCSchCalCodeOnRvw; // autowired

	protected Label recordStatus; // autowired
	protected Radiogroup userAction;
	protected Groupbox groupboxWf;


	// not auto wired vars
	private FinanceCampaign financeCampaign; // overhanded per param
	private FinanceCampaign prvFinanceCampaign; // overhanded per param
	private transient FinanceCampaignListCtrl financeCampaignListCtrl; // overhanded per param

	// old value vars for edit mode. that we can check if something
	// on the values are edited since the last init.
	private transient String  		oldVar_fCCode;
	private transient String  		oldVar_fCDesc;
	private transient String  		oldVar_fCFinType;
	private transient boolean  		oldVar_fCIsAlwMD;
	private transient boolean  		oldVar_fCIsAlwGrace;
	private transient boolean  		oldVar_fCOrgPrfUnchanged;
	private transient String  		oldVar_fCRateType;
	private transient String  		oldVar_fCBaseRate;
	private transient String  		oldVar_fCSplRate;
	private transient BigDecimal  		oldVar_fCIntRate;
	private transient String  		oldVar_fCDftIntFrq;
	private transient boolean  		oldVar_fCIsIntCpz;
	private transient String  		oldVar_fCCpzFrq;
	private transient boolean  		oldVar_fCIsRvwAlw;
	private transient String  		oldVar_fCRvwFrq;
	private transient String  		oldVar_fCGrcRateType;
	private transient String  		oldVar_fCGrcBaseRate;
	private transient String  		oldVar_fCGrcSplRate;
	private transient BigDecimal  		oldVar_fCGrcIntRate;
	private transient String  		oldVar_fCGrcDftIntFrq;
	private transient boolean  		oldVar_fCGrcIsIntCpz;
	private transient String  		oldVar_fCGrcCpzFrq;
	private transient boolean  		oldVar_fCGrcIsRvwAlw;
	private transient String  		oldVar_fCGrcRvwFrq;
	private transient BigDecimal  		oldVar_fCMinTerm;
	private transient BigDecimal  		oldVar_fCMaxTerm;
	private transient BigDecimal  		oldVar_fCDftTerms;
	private transient String  		oldVar_fCRpyFrq;
	private transient String  		oldVar_fCRepayMethod;
	private transient boolean  		oldVar_fCIsAlwPartialRpy;
	private transient boolean  		oldVar_fCIsAlwDifferment;
	private transient BigDecimal  		oldVar_fCMaxDifferment;
	private transient boolean  		oldVar_fCIsAlwFrqDifferment;
	private transient BigDecimal  		oldVar_fCMaxFrqDifferment;
	private transient boolean  		oldVar_fCIsAlwEarlyRpy;
	private transient boolean  		oldVar_fCIsAlwEarlySettle;
	private transient boolean  		oldVar_fCIsDwPayRequired;
	private transient String  		oldVar_fCRvwRateApplFor;
	private transient boolean  		oldVar_fCAlwRateChangeAnyDate;
	private transient String  		oldVar_fCGrcRvwRateApplFor;
	private transient boolean  		oldVar_fCIsIntCpzAtGrcEnd;
	private transient boolean  		oldVar_fCGrcAlwRateChgAnyDate;
	private transient BigDecimal  		oldVar_fCMinDownPayAmount;
	private transient String  		oldVar_fCSchCalCodeOnRvw;
	private transient String oldVar_recordStatus;

	private transient boolean validationOn;
	private boolean notes_Entered=false;

	// Button controller for the CRUD buttons
	private transient final String btnCtroller_ClassPrefix = "button_FinanceCampaignDialog_";
	private transient ButtonStatusCtrl btnCtrl;
	protected Button btnNew; // autowire
	protected Button btnEdit; // autowire
	protected Button btnDelete; // autowire
	protected Button btnSave; // autowire
	protected Button btnCancel; // autowire
	protected Button btnClose; // autowire
	protected Button btnHelp; // autowire
	protected Button btnNotes; // autowire
	
	
	// ServiceDAOs / Domain Classes
	private transient FinanceCampaignService financeCampaignService;
	private transient PagedListService pagedListService;
	private HashMap<String, ArrayList<ErrorDetails>> overideMap= new HashMap<String, ArrayList<ErrorDetails>>();
	

	/**
	 * default constructor.<br>
	 */
	public FinanceCampaignDialogCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * zul-file is called with a parameter for a selected FinanceCampaign object in a
	 * Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_FinanceCampaignDialog(Event event) throws Exception {
		logger.debug(event.toString());

		/* set components visible dependent of the users rights */
		doCheckRights();
		
		/* create the Button Controller. Disable not used buttons during working */
		this.btnCtrl = new ButtonStatusCtrl(getUserWorkspace(), this.btnCtroller_ClassPrefix, true, this.btnNew,
				this.btnEdit, this.btnDelete, this.btnSave, this.btnCancel, this.btnClose,this.btnNotes);

		// get the params map that are overhanded by creation.
		final Map<String, Object> args = getCreationArgsMap(event);
		
		// READ OVERHANDED params !
		if (args.containsKey("financeCampaign")) {
			this.financeCampaign = (FinanceCampaign) args.get("financeCampaign");
			FinanceCampaign befImage =new FinanceCampaign();
			BeanUtils.copyProperties(this.financeCampaign, befImage);
			this.financeCampaign.setBefImage(befImage);
			
			setFinanceCampaign(this.financeCampaign);
		} else {
			setFinanceCampaign(null);
		}
	
		doLoadWorkFlow(this.financeCampaign.isWorkflow(),this.financeCampaign.getWorkflowId(),this.financeCampaign.getNextTaskId());

		if (isWorkFlowEnabled()){
			this.userAction	= setListRecordStatus(this.userAction);
			getUserWorkspace().alocateRoleAuthorities(getRole(), "FinanceCampaignDialog");
		}

	
		// READ OVERHANDED params !
		// we get the financeCampaignListWindow controller. So we have access
		// to it and can synchronize the shown data when we do insert, edit or
		// delete financeCampaign here.
		if (args.containsKey("financeCampaignListCtrl")) {
			setFinanceCampaignListCtrl((FinanceCampaignListCtrl) args.get("financeCampaignListCtrl"));
		} else {
			setFinanceCampaignListCtrl(null);
		}

		// set Field Properties
		doSetFieldProperties();
		doShowDialog(getFinanceCampaign());
		logger.debug("Leaving");
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering") ;
		//Empty sent any required attributes
		this.fCCode.setMaxlength(8);
		this.fCDesc.setMaxlength(50);
		this.fCFinType.setMaxlength(8);
		this.fCRateType.setMaxlength(8);
		this.fCBaseRate.setMaxlength(8);
		this.fCSplRate.setMaxlength(8);
	  	this.fCIntRate.setMaxlength(13);
	  	this.fCIntRate.setFormat(PennantApplicationUtil.getAmountFormate(9));
	  	this.fCIntRate.setRoundingMode(BigDecimal.ROUND_DOWN);
	  	this.fCIntRate.setScale(9);
		this.fCDftIntFrq.setMaxlength(5);
		this.fCCpzFrq.setMaxlength(5);
		this.fCRvwFrq.setMaxlength(5);
		this.fCGrcRateType.setMaxlength(8);
		this.fCGrcBaseRate.setMaxlength(8);
		this.fCGrcSplRate.setMaxlength(8);
	  	this.fCGrcIntRate.setMaxlength(13);
	  	this.fCGrcIntRate.setFormat(PennantApplicationUtil.getAmountFormate(9));
	  	this.fCGrcIntRate.setRoundingMode(BigDecimal.ROUND_DOWN);
	  	this.fCGrcIntRate.setScale(9);
		this.fCGrcDftIntFrq.setMaxlength(5);
		this.fCGrcCpzFrq.setMaxlength(5);
		this.fCGrcRvwFrq.setMaxlength(5);
	  	this.fCMinTerm.setMaxlength(3);
	  	this.fCMinTerm.setFormat(PennantApplicationUtil.getAmountFormate(0));
	  	this.fCMinTerm.setRoundingMode(BigDecimal.ROUND_DOWN);
	  	this.fCMinTerm.setScale(0);
	  	this.fCMaxTerm.setMaxlength(3);
	  	this.fCMaxTerm.setFormat(PennantApplicationUtil.getAmountFormate(0));
	  	this.fCMaxTerm.setRoundingMode(BigDecimal.ROUND_DOWN);
	  	this.fCMaxTerm.setScale(0);
	  	this.fCDftTerms.setMaxlength(3);
	  	this.fCDftTerms.setFormat(PennantApplicationUtil.getAmountFormate(0));
	  	this.fCDftTerms.setRoundingMode(BigDecimal.ROUND_DOWN);
	  	this.fCDftTerms.setScale(0);
		this.fCRpyFrq.setMaxlength(5);
		this.fCRepayMethod.setMaxlength(8);
	  	this.fCMaxDifferment.setMaxlength(3);
	  	this.fCMaxDifferment.setFormat(PennantApplicationUtil.getAmountFormate(0));
	  	this.fCMaxDifferment.setRoundingMode(BigDecimal.ROUND_DOWN);
	  	this.fCMaxDifferment.setScale(0);
	  	this.fCMaxFrqDifferment.setMaxlength(3);
	  	this.fCMaxFrqDifferment.setFormat(PennantApplicationUtil.getAmountFormate(0));
	  	this.fCMaxFrqDifferment.setRoundingMode(BigDecimal.ROUND_DOWN);
	  	this.fCMaxFrqDifferment.setScale(0);
		this.fCRvwRateApplFor.setMaxlength(8);
		this.fCGrcRvwRateApplFor.setMaxlength(8);
	  	this.fCMinDownPayAmount.setMaxlength(4);
	  	this.fCMinDownPayAmount.setFormat(PennantApplicationUtil.getAmountFormate(2));
	  	this.fCMinDownPayAmount.setRoundingMode(BigDecimal.ROUND_DOWN);
	  	this.fCMinDownPayAmount.setScale(2);
		this.fCSchCalCodeOnRvw.setMaxlength(8);
		
		if (isWorkFlowEnabled()){
			this.groupboxWf.setVisible(true);
			
		}else{
			this.groupboxWf.setVisible(false);
			
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
		
		getUserWorkspace().alocateAuthorities("FinanceCampaignDialog");
		
		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_FinanceCampaignDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_FinanceCampaignDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_FinanceCampaignDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_FinanceCampaignDialog_btnSave"));
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
	public void onClose$window_FinanceCampaignDialog(Event event) throws Exception {
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
		PTMessageUtils.showHelpWindow(event, window_FinanceCampaignDialog);
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
			closeDialog(this.window_FinanceCampaignDialog, "FinanceCampaign");	
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
	 * @param aFinanceCampaign
	 *            FinanceCampaign
	 */
	public void doWriteBeanToComponents(FinanceCampaign aFinanceCampaign) {
		logger.debug("Entering") ;
		this.fCCode.setValue(aFinanceCampaign.getFCCode());
		this.fCDesc.setValue(aFinanceCampaign.getFCDesc());
		this.fCFinType.setValue(aFinanceCampaign.getFCFinType());
		this.fCIsAlwMD.setChecked(aFinanceCampaign.isFCIsAlwMD());
		this.fCIsAlwGrace.setChecked(aFinanceCampaign.isFCIsAlwGrace());
		this.fCOrgPrfUnchanged.setChecked(aFinanceCampaign.isFCOrgPrfUnchanged());
		this.fCRateType.setValue(aFinanceCampaign.getFCRateType());
		this.fCBaseRate.setValue(aFinanceCampaign.getFCBaseRate());
		this.fCSplRate.setValue(aFinanceCampaign.getFCSplRate());
  		this.fCIntRate.setValue(PennantAppUtil.formateAmount(aFinanceCampaign.getFCIntRate(),9));
		this.fCDftIntFrq.setValue(aFinanceCampaign.getFCDftIntFrq());
		this.fCIsIntCpz.setChecked(aFinanceCampaign.isFCIsIntCpz());
		this.fCCpzFrq.setValue(aFinanceCampaign.getFCCpzFrq());
		this.fCIsRvwAlw.setChecked(aFinanceCampaign.isFCIsRvwAlw());
		this.fCRvwFrq.setValue(aFinanceCampaign.getFCRvwFrq());
		this.fCGrcRateType.setValue(aFinanceCampaign.getFCGrcRateType());
		this.fCGrcBaseRate.setValue(aFinanceCampaign.getFCGrcBaseRate());
		this.fCGrcSplRate.setValue(aFinanceCampaign.getFCGrcSplRate());
  		this.fCGrcIntRate.setValue(PennantAppUtil.formateAmount(aFinanceCampaign.getFCGrcIntRate(),9));
		this.fCGrcDftIntFrq.setValue(aFinanceCampaign.getFCGrcDftIntFrq());
		this.fCGrcIsIntCpz.setChecked(aFinanceCampaign.isFCGrcIsIntCpz());
		this.fCGrcCpzFrq.setValue(aFinanceCampaign.getFCGrcCpzFrq());
		this.fCGrcIsRvwAlw.setChecked(aFinanceCampaign.isFCGrcIsRvwAlw());
		this.fCGrcRvwFrq.setValue(aFinanceCampaign.getFCGrcRvwFrq());
  		this.fCMinTerm.setValue(PennantAppUtil.formateAmount(aFinanceCampaign.getFCMinTerm(),0));
  		this.fCMaxTerm.setValue(PennantAppUtil.formateAmount(aFinanceCampaign.getFCMaxTerm(),0));
  		this.fCDftTerms.setValue(PennantAppUtil.formateAmount(aFinanceCampaign.getFCDftTerms(),0));
		this.fCRpyFrq.setValue(aFinanceCampaign.getFCRpyFrq());
		this.fCRepayMethod.setValue(aFinanceCampaign.getFCRepayMethod());
		this.fCIsAlwPartialRpy.setChecked(aFinanceCampaign.isFCIsAlwPartialRpy());
		this.fCIsAlwDifferment.setChecked(aFinanceCampaign.isFCIsAlwDifferment());
  		this.fCMaxDifferment.setValue(PennantAppUtil.formateAmount(aFinanceCampaign.getFCMaxDifferment(),0));
		this.fCIsAlwFrqDifferment.setChecked(aFinanceCampaign.isFCIsAlwFrqDifferment());
  		this.fCMaxFrqDifferment.setValue(PennantAppUtil.formateAmount(aFinanceCampaign.getFCMaxFrqDifferment(),0));
		this.fCIsAlwEarlyRpy.setChecked(aFinanceCampaign.isFCIsAlwEarlyRpy());
		this.fCIsAlwEarlySettle.setChecked(aFinanceCampaign.isFCIsAlwEarlySettle());
		this.fCIsDwPayRequired.setChecked(aFinanceCampaign.isFCIsDwPayRequired());
		this.fCRvwRateApplFor.setValue(aFinanceCampaign.getFCRvwRateApplFor());
		this.fCAlwRateChangeAnyDate.setChecked(aFinanceCampaign.isFCAlwRateChangeAnyDate());
		this.fCGrcRvwRateApplFor.setValue(aFinanceCampaign.getFCGrcRvwRateApplFor());
		this.fCIsIntCpzAtGrcEnd.setChecked(aFinanceCampaign.isFCIsIntCpzAtGrcEnd());
		this.fCGrcAlwRateChgAnyDate.setChecked(aFinanceCampaign.isFCGrcAlwRateChgAnyDate());
  		this.fCMinDownPayAmount.setValue(PennantAppUtil.formateAmount(aFinanceCampaign.getFCMinDownPayAmount(),2));
		this.fCSchCalCodeOnRvw.setValue(aFinanceCampaign.getFCSchCalCodeOnRvw());
	
		this.recordStatus.setValue(aFinanceCampaign.getRecordStatus());
		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aFinanceCampaign
	 */
	public void doWriteComponentsToBean(FinanceCampaign aFinanceCampaign) {
		logger.debug("Entering") ;
		doSetLOVValidation();
		
		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();
		
		try {
		    aFinanceCampaign.setFCCode(this.fCCode.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
		    aFinanceCampaign.setFCDesc(this.fCDesc.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
		    aFinanceCampaign.setFCFinType(this.fCFinType.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aFinanceCampaign.setFCIsAlwMD(this.fCIsAlwMD.isChecked());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aFinanceCampaign.setFCIsAlwGrace(this.fCIsAlwGrace.isChecked());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aFinanceCampaign.setFCOrgPrfUnchanged(this.fCOrgPrfUnchanged.isChecked());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
		    aFinanceCampaign.setFCRateType(this.fCRateType.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
		    aFinanceCampaign.setFCBaseRate(this.fCBaseRate.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
		    aFinanceCampaign.setFCSplRate(this.fCSplRate.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			if(this.fCIntRate.getValue()!=null){
			 	aFinanceCampaign.setFCIntRate(PennantAppUtil.unFormateAmount(this.fCIntRate.getValue(), 9));
			}
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
		    aFinanceCampaign.setFCDftIntFrq(this.fCDftIntFrq.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aFinanceCampaign.setFCIsIntCpz(this.fCIsIntCpz.isChecked());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
		    aFinanceCampaign.setFCCpzFrq(this.fCCpzFrq.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aFinanceCampaign.setFCIsRvwAlw(this.fCIsRvwAlw.isChecked());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
		    aFinanceCampaign.setFCRvwFrq(this.fCRvwFrq.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
		    aFinanceCampaign.setFCGrcRateType(this.fCGrcRateType.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
		    aFinanceCampaign.setFCGrcBaseRate(this.fCGrcBaseRate.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
		    aFinanceCampaign.setFCGrcSplRate(this.fCGrcSplRate.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			if(this.fCGrcIntRate.getValue()!=null){
			 	aFinanceCampaign.setFCGrcIntRate(PennantAppUtil.unFormateAmount(this.fCGrcIntRate.getValue(), 9));
			}
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
		    aFinanceCampaign.setFCGrcDftIntFrq(this.fCGrcDftIntFrq.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aFinanceCampaign.setFCGrcIsIntCpz(this.fCGrcIsIntCpz.isChecked());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
		    aFinanceCampaign.setFCGrcCpzFrq(this.fCGrcCpzFrq.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aFinanceCampaign.setFCGrcIsRvwAlw(this.fCGrcIsRvwAlw.isChecked());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
		    aFinanceCampaign.setFCGrcRvwFrq(this.fCGrcRvwFrq.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			if(this.fCMinTerm.getValue()!=null){
			 	aFinanceCampaign.setFCMinTerm(PennantAppUtil.unFormateAmount(this.fCMinTerm.getValue(), 0));
			}
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			if(this.fCMaxTerm.getValue()!=null){
			 	aFinanceCampaign.setFCMaxTerm(PennantAppUtil.unFormateAmount(this.fCMaxTerm.getValue(), 0));
			}
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			if(this.fCDftTerms.getValue()!=null){
			 	aFinanceCampaign.setFCDftTerms(PennantAppUtil.unFormateAmount(this.fCDftTerms.getValue(), 0));
			}
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
		    aFinanceCampaign.setFCRpyFrq(this.fCRpyFrq.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
		    aFinanceCampaign.setFCRepayMethod(this.fCRepayMethod.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aFinanceCampaign.setFCIsAlwPartialRpy(this.fCIsAlwPartialRpy.isChecked());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aFinanceCampaign.setFCIsAlwDifferment(this.fCIsAlwDifferment.isChecked());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			if(this.fCMaxDifferment.getValue()!=null){
			 	aFinanceCampaign.setFCMaxDifferment(PennantAppUtil.unFormateAmount(this.fCMaxDifferment.getValue(), 0));
			}
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aFinanceCampaign.setFCIsAlwFrqDifferment(this.fCIsAlwFrqDifferment.isChecked());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			if(this.fCMaxFrqDifferment.getValue()!=null){
			 	aFinanceCampaign.setFCMaxFrqDifferment(PennantAppUtil.unFormateAmount(this.fCMaxFrqDifferment.getValue(), 0));
			}
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aFinanceCampaign.setFCIsAlwEarlyRpy(this.fCIsAlwEarlyRpy.isChecked());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aFinanceCampaign.setFCIsAlwEarlySettle(this.fCIsAlwEarlySettle.isChecked());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aFinanceCampaign.setFCIsDwPayRequired(this.fCIsDwPayRequired.isChecked());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
		    aFinanceCampaign.setFCRvwRateApplFor(this.fCRvwRateApplFor.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aFinanceCampaign.setFCAlwRateChangeAnyDate(this.fCAlwRateChangeAnyDate.isChecked());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
		    aFinanceCampaign.setFCGrcRvwRateApplFor(this.fCGrcRvwRateApplFor.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aFinanceCampaign.setFCIsIntCpzAtGrcEnd(this.fCIsIntCpzAtGrcEnd.isChecked());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aFinanceCampaign.setFCGrcAlwRateChgAnyDate(this.fCGrcAlwRateChgAnyDate.isChecked());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			if(this.fCMinDownPayAmount.getValue()!=null){
			 	aFinanceCampaign.setFCMinDownPayAmount(PennantAppUtil.unFormateAmount(this.fCMinDownPayAmount.getValue(), 2));
			}
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
		    aFinanceCampaign.setFCSchCalCodeOnRvw(this.fCSchCalCodeOnRvw.getValue());
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
		
		aFinanceCampaign.setRecordStatus(this.recordStatus.getValue());
		logger.debug("Leaving");
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the
	 * readOnly mode accordingly.
	 * 
	 * @param aFinanceCampaign
	 * @throws InterruptedException
	 */
	public void doShowDialog(FinanceCampaign aFinanceCampaign) throws InterruptedException {
		logger.debug("Entering") ;
		
		// if aFinanceCampaign == null then we opened the Dialog without
		// args for a given entity, so we get a new Obj().
		if (aFinanceCampaign == null) {
			/** !!! DO NOT BREAK THE TIERS !!! */
			// We don't create a new DomainObject() in the frontend.
			// We GET it from the backend.
			aFinanceCampaign = getFinanceCampaignService().getNewFinanceCampaign();
			
			setFinanceCampaign(aFinanceCampaign);
		} else {
			setFinanceCampaign(aFinanceCampaign);
		}

		// set Readonly mode accordingly if the object is new or not.
		if (aFinanceCampaign.isNew()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.fCCode.focus();
		} else {
			this.fCDesc.focus();
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
			doWriteBeanToComponents(aFinanceCampaign);

			// stores the initial data for comparing if they are changed
			// during user action.
			doStoreInitValues();
			setDialog(this.window_FinanceCampaignDialog);
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
		this.oldVar_fCCode = this.fCCode.getValue();
		this.oldVar_fCDesc = this.fCDesc.getValue();
		this.oldVar_fCFinType = this.fCFinType.getValue();
		this.oldVar_fCIsAlwMD = this.fCIsAlwMD.isChecked();
		this.oldVar_fCIsAlwGrace = this.fCIsAlwGrace.isChecked();
		this.oldVar_fCOrgPrfUnchanged = this.fCOrgPrfUnchanged.isChecked();
		this.oldVar_fCRateType = this.fCRateType.getValue();
		this.oldVar_fCBaseRate = this.fCBaseRate.getValue();
		this.oldVar_fCSplRate = this.fCSplRate.getValue();
		this.oldVar_fCIntRate = this.fCIntRate.getValue();
		this.oldVar_fCDftIntFrq = this.fCDftIntFrq.getValue();
		this.oldVar_fCIsIntCpz = this.fCIsIntCpz.isChecked();
		this.oldVar_fCCpzFrq = this.fCCpzFrq.getValue();
		this.oldVar_fCIsRvwAlw = this.fCIsRvwAlw.isChecked();
		this.oldVar_fCRvwFrq = this.fCRvwFrq.getValue();
		this.oldVar_fCGrcRateType = this.fCGrcRateType.getValue();
		this.oldVar_fCGrcBaseRate = this.fCGrcBaseRate.getValue();
		this.oldVar_fCGrcSplRate = this.fCGrcSplRate.getValue();
		this.oldVar_fCGrcIntRate = this.fCGrcIntRate.getValue();
		this.oldVar_fCGrcDftIntFrq = this.fCGrcDftIntFrq.getValue();
		this.oldVar_fCGrcIsIntCpz = this.fCGrcIsIntCpz.isChecked();
		this.oldVar_fCGrcCpzFrq = this.fCGrcCpzFrq.getValue();
		this.oldVar_fCGrcIsRvwAlw = this.fCGrcIsRvwAlw.isChecked();
		this.oldVar_fCGrcRvwFrq = this.fCGrcRvwFrq.getValue();
		this.oldVar_fCMinTerm = this.fCMinTerm.getValue();
		this.oldVar_fCMaxTerm = this.fCMaxTerm.getValue();
		this.oldVar_fCDftTerms = this.fCDftTerms.getValue();
		this.oldVar_fCRpyFrq = this.fCRpyFrq.getValue();
		this.oldVar_fCRepayMethod = this.fCRepayMethod.getValue();
		this.oldVar_fCIsAlwPartialRpy = this.fCIsAlwPartialRpy.isChecked();
		this.oldVar_fCIsAlwDifferment = this.fCIsAlwDifferment.isChecked();
		this.oldVar_fCMaxDifferment = this.fCMaxDifferment.getValue();
		this.oldVar_fCIsAlwFrqDifferment = this.fCIsAlwFrqDifferment.isChecked();
		this.oldVar_fCMaxFrqDifferment = this.fCMaxFrqDifferment.getValue();
		this.oldVar_fCIsAlwEarlyRpy = this.fCIsAlwEarlyRpy.isChecked();
		this.oldVar_fCIsAlwEarlySettle = this.fCIsAlwEarlySettle.isChecked();
		this.oldVar_fCIsDwPayRequired = this.fCIsDwPayRequired.isChecked();
		this.oldVar_fCRvwRateApplFor = this.fCRvwRateApplFor.getValue();
		this.oldVar_fCAlwRateChangeAnyDate = this.fCAlwRateChangeAnyDate.isChecked();
		this.oldVar_fCGrcRvwRateApplFor = this.fCGrcRvwRateApplFor.getValue();
		this.oldVar_fCIsIntCpzAtGrcEnd = this.fCIsIntCpzAtGrcEnd.isChecked();
		this.oldVar_fCGrcAlwRateChgAnyDate = this.fCGrcAlwRateChgAnyDate.isChecked();
		this.oldVar_fCMinDownPayAmount = this.fCMinDownPayAmount.getValue();
		this.oldVar_fCSchCalCodeOnRvw = this.fCSchCalCodeOnRvw.getValue();
		this.oldVar_recordStatus = this.recordStatus.getValue();
		logger.debug("Leaving") ;
	}

	/**
	 * Resets the init values from mem vars. <br>
	 */
	private void doResetInitValues() {
		logger.debug("Entering");
		this.fCCode.setValue(this.oldVar_fCCode);
		this.fCDesc.setValue(this.oldVar_fCDesc);
		this.fCFinType.setValue(this.oldVar_fCFinType);
		this.fCIsAlwMD.setChecked(this.oldVar_fCIsAlwMD);
		this.fCIsAlwGrace.setChecked(this.oldVar_fCIsAlwGrace);
		this.fCOrgPrfUnchanged.setChecked(this.oldVar_fCOrgPrfUnchanged);
		this.fCRateType.setValue(this.oldVar_fCRateType);
		this.fCBaseRate.setValue(this.oldVar_fCBaseRate);
		this.fCSplRate.setValue(this.oldVar_fCSplRate);
	  	this.fCIntRate.setValue(this.oldVar_fCIntRate);
		this.fCDftIntFrq.setValue(this.oldVar_fCDftIntFrq);
		this.fCIsIntCpz.setChecked(this.oldVar_fCIsIntCpz);
		this.fCCpzFrq.setValue(this.oldVar_fCCpzFrq);
		this.fCIsRvwAlw.setChecked(this.oldVar_fCIsRvwAlw);
		this.fCRvwFrq.setValue(this.oldVar_fCRvwFrq);
		this.fCGrcRateType.setValue(this.oldVar_fCGrcRateType);
		this.fCGrcBaseRate.setValue(this.oldVar_fCGrcBaseRate);
		this.fCGrcSplRate.setValue(this.oldVar_fCGrcSplRate);
	  	this.fCGrcIntRate.setValue(this.oldVar_fCGrcIntRate);
		this.fCGrcDftIntFrq.setValue(this.oldVar_fCGrcDftIntFrq);
		this.fCGrcIsIntCpz.setChecked(this.oldVar_fCGrcIsIntCpz);
		this.fCGrcCpzFrq.setValue(this.oldVar_fCGrcCpzFrq);
		this.fCGrcIsRvwAlw.setChecked(this.oldVar_fCGrcIsRvwAlw);
		this.fCGrcRvwFrq.setValue(this.oldVar_fCGrcRvwFrq);
	  	this.fCMinTerm.setValue(this.oldVar_fCMinTerm);
	  	this.fCMaxTerm.setValue(this.oldVar_fCMaxTerm);
	  	this.fCDftTerms.setValue(this.oldVar_fCDftTerms);
		this.fCRpyFrq.setValue(this.oldVar_fCRpyFrq);
		this.fCRepayMethod.setValue(this.oldVar_fCRepayMethod);
		this.fCIsAlwPartialRpy.setChecked(this.oldVar_fCIsAlwPartialRpy);
		this.fCIsAlwDifferment.setChecked(this.oldVar_fCIsAlwDifferment);
	  	this.fCMaxDifferment.setValue(this.oldVar_fCMaxDifferment);
		this.fCIsAlwFrqDifferment.setChecked(this.oldVar_fCIsAlwFrqDifferment);
	  	this.fCMaxFrqDifferment.setValue(this.oldVar_fCMaxFrqDifferment);
		this.fCIsAlwEarlyRpy.setChecked(this.oldVar_fCIsAlwEarlyRpy);
		this.fCIsAlwEarlySettle.setChecked(this.oldVar_fCIsAlwEarlySettle);
		this.fCIsDwPayRequired.setChecked(this.oldVar_fCIsDwPayRequired);
		this.fCRvwRateApplFor.setValue(this.oldVar_fCRvwRateApplFor);
		this.fCAlwRateChangeAnyDate.setChecked(this.oldVar_fCAlwRateChangeAnyDate);
		this.fCGrcRvwRateApplFor.setValue(this.oldVar_fCGrcRvwRateApplFor);
		this.fCIsIntCpzAtGrcEnd.setChecked(this.oldVar_fCIsIntCpzAtGrcEnd);
		this.fCGrcAlwRateChgAnyDate.setChecked(this.oldVar_fCGrcAlwRateChgAnyDate);
	  	this.fCMinDownPayAmount.setValue(this.oldVar_fCMinDownPayAmount);
		this.fCSchCalCodeOnRvw.setValue(this.oldVar_fCSchCalCodeOnRvw);
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
		
		if (!this.fCCode.isReadonly()){
			this.fCCode.setConstraint(new PTStringValidator(Labels.getLabel("label_FinanceCampaignDialog_FCCode.value"), PennantRegularExpressions.REGEX_ALPHANUM_CODE, true));
		}	
		if (!this.fCDesc.isReadonly()){
			this.fCDesc.setConstraint(new PTStringValidator(Labels.getLabel("label_FinanceCampaignDialog_FCDesc.value"), PennantRegularExpressions.REGEX_DESCRIPTION, true));
		}	
		if (!this.fCFinType.isReadonly()){
			this.fCFinType.setConstraint(new PTStringValidator(Labels.getLabel("label_FinanceCampaignDialog_FCFinType.value"), PennantRegularExpressions.REGEX_ALPHA, true));
		}	
		if (!this.fCRateType.isReadonly()){
			this.fCRateType.setConstraint(new PTStringValidator(Labels.getLabel("label_FinanceCampaignDialog_FCRateType.value"), null, true));
		}	
		if (!this.fCBaseRate.isReadonly()){
			this.fCBaseRate.setConstraint(new PTStringValidator(Labels.getLabel("label_FinanceCampaignDialog_FCBaseRate.value"), null, true));
		}	
		if (!this.fCSplRate.isReadonly()){
			this.fCSplRate.setConstraint(new PTStringValidator(Labels.getLabel("label_FinanceCampaignDialog_FCSplRate.value"), null, true));
		}	
		if (!this.fCIntRate.isReadonly()){
			this.fCIntRate.setConstraint(new AmountValidator(13,9,Labels.getLabel("label_FinanceCampaignDialog_FCIntRate.value")));
		}	
		if (!this.fCDftIntFrq.isReadonly()){
			this.fCDftIntFrq.setConstraint(new PTStringValidator(Labels.getLabel("label_FinanceCampaignDialog_FCDftIntFrq.value"), null, true));
		}	
		if (!this.fCCpzFrq.isReadonly()){
			this.fCCpzFrq.setConstraint(new PTStringValidator(Labels.getLabel("label_FinanceCampaignDialog_FCCpzFrq.value"), null, true));
		}	
		if (!this.fCRvwFrq.isReadonly()){
			this.fCRvwFrq.setConstraint(new PTStringValidator(Labels.getLabel("label_FinanceCampaignDialog_FCRvwFrq.value"), null, true));
		}	
		if (!this.fCGrcRateType.isReadonly()){
			this.fCGrcRateType.setConstraint(new PTStringValidator(Labels.getLabel("label_FinanceCampaignDialog_FCGrcRateType.value"), null, true));
		}	
		if (!this.fCGrcBaseRate.isReadonly()){
			this.fCGrcBaseRate.setConstraint(new PTStringValidator(Labels.getLabel("label_FinanceCampaignDialog_FCGrcBaseRate.value"), null, true));
		}	
		if (!this.fCGrcSplRate.isReadonly()){
			this.fCGrcSplRate.setConstraint(new PTStringValidator(Labels.getLabel("label_FinanceCampaignDialog_FCGrcSplRate.value"), null, true));
		}	
		if (!this.fCGrcIntRate.isReadonly()){
			this.fCGrcIntRate.setConstraint(new AmountValidator(13,9,Labels.getLabel("label_FinanceCampaignDialog_FCGrcIntRate.value")));
		}	
		if (!this.fCGrcDftIntFrq.isReadonly()){
			this.fCGrcDftIntFrq.setConstraint(new PTStringValidator(Labels.getLabel("label_FinanceCampaignDialog_FCGrcDftIntFrq.value"), null, true));
		}	
		if (!this.fCGrcCpzFrq.isReadonly()){
			this.fCGrcCpzFrq.setConstraint(new PTStringValidator(Labels.getLabel("label_FinanceCampaignDialog_FCGrcCpzFrq.value"), null, true));
		}	
		if (!this.fCGrcRvwFrq.isReadonly()){
			this.fCGrcRvwFrq.setConstraint(new PTStringValidator(Labels.getLabel("label_FinanceCampaignDialog_FCGrcRvwFrq.value"), null, true));
		}	
		if (!this.fCMinTerm.isReadonly()){
			this.fCMinTerm.setConstraint(new AmountValidator(3,0,Labels.getLabel("label_FinanceCampaignDialog_FCMinTerm.value")));
		}	
		if (!this.fCMaxTerm.isReadonly()){
			this.fCMaxTerm.setConstraint(new AmountValidator(3,0,Labels.getLabel("label_FinanceCampaignDialog_FCMaxTerm.value")));
		}	
		if (!this.fCDftTerms.isReadonly()){
			this.fCDftTerms.setConstraint(new AmountValidator(3,0,Labels.getLabel("label_FinanceCampaignDialog_FCDftTerms.value")));
		}	
		if (!this.fCRpyFrq.isReadonly()){
			this.fCRpyFrq.setConstraint(new PTStringValidator(Labels.getLabel("label_FinanceCampaignDialog_FCRpyFrq.value"), null, true));
		}	
		if (!this.fCRepayMethod.isReadonly()){
			this.fCRepayMethod.setConstraint(new PTStringValidator(Labels.getLabel("label_FinanceCampaignDialog_FCRepayMethod.value"), null, true));
		}	
		if (!this.fCMaxDifferment.isReadonly()){
			this.fCMaxDifferment.setConstraint(new AmountValidator(3,0,Labels.getLabel("label_FinanceCampaignDialog_FCMaxDifferment.value")));
		}	
		if (!this.fCMaxFrqDifferment.isReadonly()){
			this.fCMaxFrqDifferment.setConstraint(new AmountValidator(3,0,Labels.getLabel("label_FinanceCampaignDialog_FCMaxFrqDifferment.value")));
		}	
		if (!this.fCRvwRateApplFor.isReadonly()){
			this.fCRvwRateApplFor.setConstraint(new PTStringValidator(Labels.getLabel("label_FinanceCampaignDialog_FCRvwRateApplFor.value"), null, true));
		}	
		if (!this.fCGrcRvwRateApplFor.isReadonly()){
			this.fCGrcRvwRateApplFor.setConstraint(new PTStringValidator(Labels.getLabel("label_FinanceCampaignDialog_FCGrcRvwRateApplFor.value"), null, true));
		}	
		if (!this.fCMinDownPayAmount.isReadonly()){
			this.fCMinDownPayAmount.setConstraint(new AmountValidator(4,2,Labels.getLabel("label_FinanceCampaignDialog_FCMinDownPayAmount.value")));
		}	
		if (!this.fCSchCalCodeOnRvw.isReadonly()){
			this.fCSchCalCodeOnRvw.setConstraint(new PTStringValidator(Labels.getLabel("label_FinanceCampaignDialog_FCSchCalCodeOnRvw.value"), null, true));
		}	
	logger.debug("Leaving");
	}

	/**
	 * Disables the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering");
		setValidationOn(false);
		this.fCCode.setConstraint("");
		this.fCDesc.setConstraint("");
		this.fCFinType.setConstraint("");
		this.fCRateType.setConstraint("");
		this.fCBaseRate.setConstraint("");
		this.fCSplRate.setConstraint("");
		this.fCIntRate.setConstraint("");
		this.fCDftIntFrq.setConstraint("");
		this.fCCpzFrq.setConstraint("");
		this.fCRvwFrq.setConstraint("");
		this.fCGrcRateType.setConstraint("");
		this.fCGrcBaseRate.setConstraint("");
		this.fCGrcSplRate.setConstraint("");
		this.fCGrcIntRate.setConstraint("");
		this.fCGrcDftIntFrq.setConstraint("");
		this.fCGrcCpzFrq.setConstraint("");
		this.fCGrcRvwFrq.setConstraint("");
		this.fCMinTerm.setConstraint("");
		this.fCMaxTerm.setConstraint("");
		this.fCDftTerms.setConstraint("");
		this.fCRpyFrq.setConstraint("");
		this.fCRepayMethod.setConstraint("");
		this.fCMaxDifferment.setConstraint("");
		this.fCMaxFrqDifferment.setConstraint("");
		this.fCRvwRateApplFor.setConstraint("");
		this.fCGrcRvwRateApplFor.setConstraint("");
		this.fCMinDownPayAmount.setConstraint("");
		this.fCSchCalCodeOnRvw.setConstraint("");
	logger.debug("Leaving");
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// +++++++++++++++++++++++++ crud operations +++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * Deletes a FinanceCampaign object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug("Entering");	
		final FinanceCampaign aFinanceCampaign = new FinanceCampaign();
		BeanUtils.copyProperties(getFinanceCampaign(), aFinanceCampaign);
		String tranType=PennantConstants.TRAN_WF;
		
		// Show a confirm box
		final String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record") + "\n\n --> " + aFinanceCampaign.getFCCode();
		final String title = Labels.getLabel("message.Deleting.Record");
		MultiLineMessageBox.doSetTemplate();
		
		int conf =  (MultiLineMessageBox.show(msg, title, MultiLineMessageBox.YES| MultiLineMessageBox.NO, Messagebox.QUESTION, true));

		if (conf==MultiLineMessageBox.YES){
			logger.debug("doDelete: Yes");

			if (StringUtils.trimToEmpty(aFinanceCampaign.getRecordType()).equals("")){
				aFinanceCampaign.setVersion(aFinanceCampaign.getVersion()+1);
				aFinanceCampaign.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				
				if (isWorkFlowEnabled()){
					aFinanceCampaign.setNewRecord(true);
					tranType=PennantConstants.TRAN_WF;
				}else{
					tranType=PennantConstants.TRAN_DEL;
				}
			}

			try {
				if(doProcess(aFinanceCampaign,tranType)){
					refreshList();
					closeDialog(this.window_FinanceCampaignDialog, "FinanceCampaign"); 
				}

			}catch (DataAccessException e){
				logger.error("doDelete " + e);
				showMessage(e);
			}
			
		}
		logger.debug("Leaving");
	}

	/**
	 * Create a new FinanceCampaign object. <br>
	 */
	private void doNew() {
		logger.debug("Entering");
		
		final FinanceCampaign aFinanceCampaign = getFinanceCampaignService().getNewFinanceCampaign();
		aFinanceCampaign.setNewRecord(true);
		setFinanceCampaign(aFinanceCampaign);
		doClear(); // clear all commponents
		doEdit(); // edit mode
		this.btnCtrl.setBtnStatus_New();

		// remember the old vars
		doStoreInitValues();

		// setFocus
		this.fCCode.focus();
	logger.debug("Leaving");
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug("Entering");
		
		if (getFinanceCampaign().isNewRecord()){
		  	this.fCCode.setReadonly(false);
			this.btnCancel.setVisible(false);
		}else{
			this.fCCode.setReadonly(true);
			this.btnCancel.setVisible(true);
		}
	
		this.fCDesc.setReadonly(isReadOnly("FinanceCampaignDialog_fCDesc"));
		this.fCFinType.setReadonly(isReadOnly("FinanceCampaignDialog_fCFinType"));
	 	this.fCIsAlwMD.setDisabled(isReadOnly("FinanceCampaignDialog_fCIsAlwMD"));
	 	this.fCIsAlwGrace.setDisabled(isReadOnly("FinanceCampaignDialog_fCIsAlwGrace"));
	 	this.fCOrgPrfUnchanged.setDisabled(isReadOnly("FinanceCampaignDialog_fCOrgPrfUnchanged"));
		this.fCRateType.setReadonly(isReadOnly("FinanceCampaignDialog_fCRateType"));
		this.fCBaseRate.setReadonly(isReadOnly("FinanceCampaignDialog_fCBaseRate"));
		this.fCSplRate.setReadonly(isReadOnly("FinanceCampaignDialog_fCSplRate"));
		this.fCIntRate.setReadonly(isReadOnly("FinanceCampaignDialog_fCIntRate"));
		this.fCDftIntFrq.setReadonly(isReadOnly("FinanceCampaignDialog_fCDftIntFrq"));
	 	this.fCIsIntCpz.setDisabled(isReadOnly("FinanceCampaignDialog_fCIsIntCpz"));
		this.fCCpzFrq.setReadonly(isReadOnly("FinanceCampaignDialog_fCCpzFrq"));
	 	this.fCIsRvwAlw.setDisabled(isReadOnly("FinanceCampaignDialog_fCIsRvwAlw"));
		this.fCRvwFrq.setReadonly(isReadOnly("FinanceCampaignDialog_fCRvwFrq"));
		this.fCGrcRateType.setReadonly(isReadOnly("FinanceCampaignDialog_fCGrcRateType"));
		this.fCGrcBaseRate.setReadonly(isReadOnly("FinanceCampaignDialog_fCGrcBaseRate"));
		this.fCGrcSplRate.setReadonly(isReadOnly("FinanceCampaignDialog_fCGrcSplRate"));
		this.fCGrcIntRate.setReadonly(isReadOnly("FinanceCampaignDialog_fCGrcIntRate"));
		this.fCGrcDftIntFrq.setReadonly(isReadOnly("FinanceCampaignDialog_fCGrcDftIntFrq"));
	 	this.fCGrcIsIntCpz.setDisabled(isReadOnly("FinanceCampaignDialog_fCGrcIsIntCpz"));
		this.fCGrcCpzFrq.setReadonly(isReadOnly("FinanceCampaignDialog_fCGrcCpzFrq"));
	 	this.fCGrcIsRvwAlw.setDisabled(isReadOnly("FinanceCampaignDialog_fCGrcIsRvwAlw"));
		this.fCGrcRvwFrq.setReadonly(isReadOnly("FinanceCampaignDialog_fCGrcRvwFrq"));
		this.fCMinTerm.setReadonly(isReadOnly("FinanceCampaignDialog_fCMinTerm"));
		this.fCMaxTerm.setReadonly(isReadOnly("FinanceCampaignDialog_fCMaxTerm"));
		this.fCDftTerms.setReadonly(isReadOnly("FinanceCampaignDialog_fCDftTerms"));
		this.fCRpyFrq.setReadonly(isReadOnly("FinanceCampaignDialog_fCRpyFrq"));
		this.fCRepayMethod.setReadonly(isReadOnly("FinanceCampaignDialog_fCRepayMethod"));
	 	this.fCIsAlwPartialRpy.setDisabled(isReadOnly("FinanceCampaignDialog_fCIsAlwPartialRpy"));
	 	this.fCIsAlwDifferment.setDisabled(isReadOnly("FinanceCampaignDialog_fCIsAlwDifferment"));
		this.fCMaxDifferment.setReadonly(isReadOnly("FinanceCampaignDialog_fCMaxDifferment"));
	 	this.fCIsAlwFrqDifferment.setDisabled(isReadOnly("FinanceCampaignDialog_fCIsAlwFrqDifferment"));
		this.fCMaxFrqDifferment.setReadonly(isReadOnly("FinanceCampaignDialog_fCMaxFrqDifferment"));
	 	this.fCIsAlwEarlyRpy.setDisabled(isReadOnly("FinanceCampaignDialog_fCIsAlwEarlyRpy"));
	 	this.fCIsAlwEarlySettle.setDisabled(isReadOnly("FinanceCampaignDialog_fCIsAlwEarlySettle"));
	 	this.fCIsDwPayRequired.setDisabled(isReadOnly("FinanceCampaignDialog_fCIsDwPayRequired"));
		this.fCRvwRateApplFor.setReadonly(isReadOnly("FinanceCampaignDialog_fCRvwRateApplFor"));
	 	this.fCAlwRateChangeAnyDate.setDisabled(isReadOnly("FinanceCampaignDialog_fCAlwRateChangeAnyDate"));
		this.fCGrcRvwRateApplFor.setReadonly(isReadOnly("FinanceCampaignDialog_fCGrcRvwRateApplFor"));
	 	this.fCIsIntCpzAtGrcEnd.setDisabled(isReadOnly("FinanceCampaignDialog_fCIsIntCpzAtGrcEnd"));
	 	this.fCGrcAlwRateChgAnyDate.setDisabled(isReadOnly("FinanceCampaignDialog_fCGrcAlwRateChgAnyDate"));
		this.fCMinDownPayAmount.setReadonly(isReadOnly("FinanceCampaignDialog_fCMinDownPayAmount"));
		this.fCSchCalCodeOnRvw.setReadonly(isReadOnly("FinanceCampaignDialog_fCSchCalCodeOnRvw"));

		if (isWorkFlowEnabled()){
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}
			
			if (this.financeCampaign.isNewRecord()){
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
		this.fCCode.setReadonly(true);
		this.fCDesc.setReadonly(true);
		this.fCFinType.setReadonly(true);
		this.fCIsAlwMD.setDisabled(true);
		this.fCIsAlwGrace.setDisabled(true);
		this.fCOrgPrfUnchanged.setDisabled(true);
		this.fCRateType.setReadonly(true);
		this.fCBaseRate.setReadonly(true);
		this.fCSplRate.setReadonly(true);
		this.fCIntRate.setReadonly(true);
		this.fCDftIntFrq.setReadonly(true);
		this.fCIsIntCpz.setDisabled(true);
		this.fCCpzFrq.setReadonly(true);
		this.fCIsRvwAlw.setDisabled(true);
		this.fCRvwFrq.setReadonly(true);
		this.fCGrcRateType.setReadonly(true);
		this.fCGrcBaseRate.setReadonly(true);
		this.fCGrcSplRate.setReadonly(true);
		this.fCGrcIntRate.setReadonly(true);
		this.fCGrcDftIntFrq.setReadonly(true);
		this.fCGrcIsIntCpz.setDisabled(true);
		this.fCGrcCpzFrq.setReadonly(true);
		this.fCGrcIsRvwAlw.setDisabled(true);
		this.fCGrcRvwFrq.setReadonly(true);
		this.fCMinTerm.setReadonly(true);
		this.fCMaxTerm.setReadonly(true);
		this.fCDftTerms.setReadonly(true);
		this.fCRpyFrq.setReadonly(true);
		this.fCRepayMethod.setReadonly(true);
		this.fCIsAlwPartialRpy.setDisabled(true);
		this.fCIsAlwDifferment.setDisabled(true);
		this.fCMaxDifferment.setReadonly(true);
		this.fCIsAlwFrqDifferment.setDisabled(true);
		this.fCMaxFrqDifferment.setReadonly(true);
		this.fCIsAlwEarlyRpy.setDisabled(true);
		this.fCIsAlwEarlySettle.setDisabled(true);
		this.fCIsDwPayRequired.setDisabled(true);
		this.fCRvwRateApplFor.setReadonly(true);
		this.fCAlwRateChangeAnyDate.setDisabled(true);
		this.fCGrcRvwRateApplFor.setReadonly(true);
		this.fCIsIntCpzAtGrcEnd.setDisabled(true);
		this.fCGrcAlwRateChgAnyDate.setDisabled(true);
		this.fCMinDownPayAmount.setReadonly(true);
		this.fCSchCalCodeOnRvw.setReadonly(true);
		
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
		
		this.fCCode.setValue("");
		this.fCDesc.setValue("");
		this.fCFinType.setValue("");
		this.fCIsAlwMD.setChecked(false);
		this.fCIsAlwGrace.setChecked(false);
		this.fCOrgPrfUnchanged.setChecked(false);
		this.fCRateType.setValue("");
		this.fCBaseRate.setValue("");
		this.fCSplRate.setValue("");
		this.fCIntRate.setValue("");
		this.fCDftIntFrq.setValue("");
		this.fCIsIntCpz.setChecked(false);
		this.fCCpzFrq.setValue("");
		this.fCIsRvwAlw.setChecked(false);
		this.fCRvwFrq.setValue("");
		this.fCGrcRateType.setValue("");
		this.fCGrcBaseRate.setValue("");
		this.fCGrcSplRate.setValue("");
		this.fCGrcIntRate.setValue("");
		this.fCGrcDftIntFrq.setValue("");
		this.fCGrcIsIntCpz.setChecked(false);
		this.fCGrcCpzFrq.setValue("");
		this.fCGrcIsRvwAlw.setChecked(false);
		this.fCGrcRvwFrq.setValue("");
		this.fCMinTerm.setValue("");
		this.fCMaxTerm.setValue("");
		this.fCDftTerms.setValue("");
		this.fCRpyFrq.setValue("");
		this.fCRepayMethod.setValue("");
		this.fCIsAlwPartialRpy.setChecked(false);
		this.fCIsAlwDifferment.setChecked(false);
		this.fCMaxDifferment.setValue("");
		this.fCIsAlwFrqDifferment.setChecked(false);
		this.fCMaxFrqDifferment.setValue("");
		this.fCIsAlwEarlyRpy.setChecked(false);
		this.fCIsAlwEarlySettle.setChecked(false);
		this.fCIsDwPayRequired.setChecked(false);
		this.fCRvwRateApplFor.setValue("");
		this.fCAlwRateChangeAnyDate.setChecked(false);
		this.fCGrcRvwRateApplFor.setValue("");
		this.fCIsIntCpzAtGrcEnd.setChecked(false);
		this.fCGrcAlwRateChgAnyDate.setChecked(false);
		this.fCMinDownPayAmount.setValue("");
		this.fCSchCalCodeOnRvw.setValue("");
	logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug("Entering");
		final FinanceCampaign aFinanceCampaign = new FinanceCampaign();
		BeanUtils.copyProperties(getFinanceCampaign(), aFinanceCampaign);
		boolean isNew = false;
		
		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		// force validation, if on, than execute by component.getValue()
		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		doSetValidation();
		// fill the FinanceCampaign object with the components data
		doWriteComponentsToBean(aFinanceCampaign);

		// Write the additional validations as per below example
		// get the selected branch object from the listbox
		// Do data level validations here
		
		isNew = aFinanceCampaign.isNew();
		String tranType="";

		if(isWorkFlowEnabled()){
			tranType =PennantConstants.TRAN_WF;
			if (StringUtils.trimToEmpty(aFinanceCampaign.getRecordType()).equals("")){
				aFinanceCampaign.setVersion(aFinanceCampaign.getVersion()+1);
				if(isNew){
					aFinanceCampaign.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else{
					aFinanceCampaign.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aFinanceCampaign.setNewRecord(true);
				}
			}
		}else{
			aFinanceCampaign.setVersion(aFinanceCampaign.getVersion()+1);
			if(isNew){
				tranType =PennantConstants.TRAN_ADD;
			}else{
				tranType =PennantConstants.TRAN_UPD;
			}
		}
		
		// save it to database
		try {
			
			if(doProcess(aFinanceCampaign,tranType)){
				doWriteBeanToComponents(aFinanceCampaign);
				refreshList();
				closeDialog(this.window_FinanceCampaignDialog, "FinanceCampaign");
			}

		} catch (final DataAccessException e) {
			logger.error(e);
			showMessage(e);
		}
		logger.debug("Leaving");
	}

	private boolean doProcess(FinanceCampaign aFinanceCampaign,String tranType){
		logger.debug("Entering");
		boolean processCompleted=false;
		AuditHeader auditHeader =  null;
		String nextRoleCode="";
		
		aFinanceCampaign.setLastMntBy(getUserWorkspace().getLoginUserDetails().getLoginUsrID());
		aFinanceCampaign.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aFinanceCampaign.setUserDetails(getUserWorkspace().getLoginUserDetails());
		
		if (isWorkFlowEnabled()) {
			String taskId = getWorkFlow().getTaskId(getRole());
			String nextTaskId = "";
			//Upgraded to ZK-6.5.1.1 Added casting to String 	
			aFinanceCampaign.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aFinanceCampaign.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getWorkFlow().getNextTaskIds(taskId, aFinanceCampaign);
				}

				if (PennantConstants.WF_Audit_Notes.equals(getWorkFlow().getAuditingReq(taskId,aFinanceCampaign))) {
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

			aFinanceCampaign.setTaskId(taskId);
			aFinanceCampaign.setNextTaskId(nextTaskId);
			aFinanceCampaign.setRoleCode(getRole());
			aFinanceCampaign.setNextRoleCode(nextRoleCode);
			
			auditHeader =  getAuditHeader(aFinanceCampaign, tranType);
			
			String operationRefs = getWorkFlow().getOperationRefs(taskId,aFinanceCampaign);
			
			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader,null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader =  getAuditHeader(aFinanceCampaign, PennantConstants.TRAN_WF);
					processCompleted  = doSaveProcess(auditHeader, list[i]);
					if(!processCompleted){
						break;
					}
				}
			}
		}else{
			
			auditHeader =  getAuditHeader(aFinanceCampaign, tranType);
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
		
		FinanceCampaign aFinanceCampaign = (FinanceCampaign) auditHeader.getAuditDetail().getModelData();
		
		try {
			
			while(retValue==PennantConstants.porcessOVERIDE){
				
				if (StringUtils.trimToEmpty(method).equalsIgnoreCase("")){
					if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)){
						auditHeader = getFinanceCampaignService().delete(auditHeader);
						deleteNotes=true;
					}else{
						auditHeader = getFinanceCampaignService().saveOrUpdate(auditHeader);	
					}
					
				}else{
					if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)){
						auditHeader = getFinanceCampaignService().doApprove(auditHeader);

						if(aFinanceCampaign.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)){
							deleteNotes=true;
						}

					}else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)){
						auditHeader = getFinanceCampaignService().doReject(auditHeader);
						if(aFinanceCampaign.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){
							deleteNotes=true;
						}

					}else{
						auditHeader.setErrorDetails(new ErrorDetails(PennantConstants.ERR_9999, Labels.getLabel("InvalidWorkFlowMethod"), null));
						retValue = ErrorControl.showErrorControl(this.window_FinanceCampaignDialog, auditHeader);
						return processCompleted; 
					}
				}
				
				auditHeader =	ErrorControl.showErrorDetails(this.window_FinanceCampaignDialog, auditHeader);
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

	public FinanceCampaign getFinanceCampaign() {
		return this.financeCampaign;
	}

	public void setFinanceCampaign(FinanceCampaign financeCampaign) {
		this.financeCampaign = financeCampaign;
	}

	public void setFinanceCampaignService(FinanceCampaignService financeCampaignService) {
		this.financeCampaignService = financeCampaignService;
	}

	public FinanceCampaignService getFinanceCampaignService() {
		return this.financeCampaignService;
	}

	public void setFinanceCampaignListCtrl(FinanceCampaignListCtrl financeCampaignListCtrl) {
		this.financeCampaignListCtrl = financeCampaignListCtrl;
	}

	public FinanceCampaignListCtrl getFinanceCampaignListCtrl() {
		return this.financeCampaignListCtrl;
	}

	public PagedListService getPagedListService() {
		return pagedListService;
	}

	public void setPagedListService(PagedListService pagedListService) {
		this.pagedListService = pagedListService;
	}

	
	private AuditHeader getAuditHeader(FinanceCampaign aFinanceCampaign, String tranType){
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aFinanceCampaign.getBefImage(), aFinanceCampaign);   
		return new AuditHeader(aFinanceCampaign.getFCCode(),null,null,null,auditDetail,aFinanceCampaign.getUserDetails(),getOverideMap());
	}
	
	private void showMessage(Exception e){
		AuditHeader auditHeader= new AuditHeader();
		try {
			auditHeader.setErrorDetails(new ErrorDetails(PennantConstants.ERR_UNDEF,e.getMessage(),null));
			ErrorControl.showErrorControl(this.window_FinanceCampaignDialog, auditHeader);
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
		notes.setModuleName("FinanceCampaign");
		notes.setReference(getFinanceCampaign().getFCCode());
		notes.setVersion(getFinanceCampaign().getVersion());
		return notes;
	}
	
	private void doClearMessage() {
		logger.debug("Entering");
			this.fCCode.setErrorMessage("");
			this.fCDesc.setErrorMessage("");
			this.fCFinType.setErrorMessage("");
			this.fCRateType.setErrorMessage("");
			this.fCBaseRate.setErrorMessage("");
			this.fCSplRate.setErrorMessage("");
			this.fCIntRate.setErrorMessage("");
			this.fCDftIntFrq.setErrorMessage("");
			this.fCCpzFrq.setErrorMessage("");
			this.fCRvwFrq.setErrorMessage("");
			this.fCGrcRateType.setErrorMessage("");
			this.fCGrcBaseRate.setErrorMessage("");
			this.fCGrcSplRate.setErrorMessage("");
			this.fCGrcIntRate.setErrorMessage("");
			this.fCGrcDftIntFrq.setErrorMessage("");
			this.fCGrcCpzFrq.setErrorMessage("");
			this.fCGrcRvwFrq.setErrorMessage("");
			this.fCMinTerm.setErrorMessage("");
			this.fCMaxTerm.setErrorMessage("");
			this.fCDftTerms.setErrorMessage("");
			this.fCRpyFrq.setErrorMessage("");
			this.fCRepayMethod.setErrorMessage("");
			this.fCMaxDifferment.setErrorMessage("");
			this.fCMaxFrqDifferment.setErrorMessage("");
			this.fCRvwRateApplFor.setErrorMessage("");
			this.fCGrcRvwRateApplFor.setErrorMessage("");
			this.fCMinDownPayAmount.setErrorMessage("");
			this.fCSchCalCodeOnRvw.setErrorMessage("");
	logger.debug("Leaving");
	}
	

private void refreshList(){
		final JdbcSearchObject<FinanceCampaign> soFinanceCampaign = getFinanceCampaignListCtrl().getSearchObj();
		getFinanceCampaignListCtrl().pagingFinanceCampaignList.setActivePage(0);
		getFinanceCampaignListCtrl().getPagedListWrapper().setSearchObject(soFinanceCampaign);
		if(getFinanceCampaignListCtrl().listBoxFinanceCampaign!=null){
			getFinanceCampaignListCtrl().listBoxFinanceCampaign.getListModel();
		}
	} 

	public void setOverideMap(HashMap<String, ArrayList<ErrorDetails>> overideMap) {
		this.overideMap = overideMap;
	}

	public HashMap<String, ArrayList<ErrorDetails>> getOverideMap() {
		return overideMap;
	}

	public FinanceCampaign getPrvFinanceCampaign() {
		return prvFinanceCampaign;
	}
}
