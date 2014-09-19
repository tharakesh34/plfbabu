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
 * FileName    		:  CollateralitemDialogCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  20-02-2013    														*
 *                                                                  						*
 * Modified Date    :  20-02-2013    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 20-02-2013       Pennant	                 0.1                                            * 
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

package com.pennant.webui.coremasters.collateralitem;

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
import org.zkoss.zk.ui.SuspendNotAllowedException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Longbox;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Row;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.app.util.DateUtility;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.Notes;
import com.pennant.backend.model.applicationmaster.Currency;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.coremasters.CollateralLocation;
import com.pennant.backend.model.coremasters.CollateralType;
import com.pennant.backend.model.coremasters.Collateralitem;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.systemmasters.Country;
import com.pennant.backend.model.systemmasters.Department;
import com.pennant.backend.service.PagedListService;
import com.pennant.backend.service.coremasters.CollateralitemService;
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
 * /WEB-INF/pages/CoreMasters/Collateralitem/collateralitemDialog.zul file. <br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 */
public class CollateralitemDialogCtrl extends GFCBaseCtrl implements Serializable {

	private static final long serialVersionUID = 1L;
	private final static Logger logger = Logger.getLogger(CollateralitemDialogCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window window_CollateralitemDialog; // autowired
	protected Textbox hYCUS; // autowired
	protected Textbox hYCLC; // autowired
	protected Textbox hYDLP; // autowired
	protected Textbox hYDLR; // autowired
	protected Textbox hYDBNM; // autowired
	protected Textbox hYAB; // autowired
	protected Textbox hYAN; // autowired
	protected Textbox hYAS; // autowired
	protected Textbox hYCLP; // autowired
	protected Textbox hYCLR; // autowired
	protected Checkbox hYCCM; // autowired
	protected Label lhYCCM; // autowired
	protected Textbox hYCNA; // autowired
	protected Textbox hYCLO; // autowired
	protected Textbox hYDPC; // autowired
	protected Checkbox hYCPI; // autowired
	protected Label lhYCPI; // autowired
	protected Datebox hYCXD; // autowired
	protected Datebox hYLRD; // autowired
	protected Textbox hYFRQ; // autowired
	protected Combobox hYFRQCode; // autoWired
	protected Combobox hYFRQMth; // autoWired
	protected Combobox hYFRQDay; // autoWired
	protected Datebox hYNRD; // autowired
	protected Decimalbox hYNOU; // autowired
	protected Decimalbox hYUNP; // autowired
	protected Textbox hYCCY; // autowired
	protected Decimalbox hYCLV; // autowired
	protected Decimalbox hYSVM; // autowired
	protected Decimalbox hYMCV; // autowired
	protected Decimalbox hYBKV; // autowired
	protected Decimalbox hYTOTA; // autowired
	protected Decimalbox hYISV; // autowired
	protected Datebox hYIXD; // autowired
	protected Textbox hYNR1; // autowired
	protected Textbox hYNR2; // autowired
	protected Textbox hYNR3; // autowired
	protected Textbox hYNR4; // autowired
	protected Datebox hYDLM; // autowired
	protected Longbox custID; // autowired
	protected Textbox custShrtName; // autowired

	protected Label recordStatus; // autowired
	protected Radiogroup userAction;
	protected Groupbox groupboxWf;
	protected Row statusRow;

	protected Button 	btnSearchCollateralCountry; 					// autoWired
	protected Textbox 	lovDescCollateralCountryName;
	private transient String 	oldVar_lovDescCollateralCountryName;
	
	protected Button 	btnSearchCollateralType; 					// autoWired
	protected Textbox 	lovDescCollateralTypeName;
	private transient String 	oldVar_lovDescCollateralTypeName;

	protected Button 	btnSearchCollateralDept; 					// autoWired
	protected Textbox 	lovDescCollateralDeptName;
	private transient String 	oldVar_lovDescCollateralDeptName;
	
	protected Button 	btnSearchCollateralLocation; 					// autoWired
	protected Textbox 	lovDescCollateralLocationName;
	private transient String 	oldVar_lovDescCollateralLocationName;

	// not auto wired vars
	private Collateralitem collateralitem; // overhanded per param
	private Collateralitem prvCollateralitem; // overhanded per param
	private transient CollateralitemListCtrl collateralitemListCtrl; // overhanded per param

	// old value vars for edit mode. that we can check if something
	// on the values are edited since the last init.
	private transient String  		oldVar_hYCUS;
	private transient String  		oldVar_hYCLC;
	private transient String  		oldVar_hYDLP;
	private transient String  		oldVar_hYDLR;
	private transient String  		oldVar_hYDBNM;
	private transient String  		oldVar_hYAB;
	private transient String  		oldVar_hYAN;
	private transient String  		oldVar_hYAS;
	private transient String  		oldVar_hYCLP;
	private transient String  		oldVar_hYCLR;
	private transient boolean  		oldVar_hYCCM;
	private transient String  		oldVar_hYCNA;
	private transient String  		oldVar_hYCLO;
	private transient String  		oldVar_hYDPC;
	private transient boolean  		oldVar_hYCPI;
	private transient Date  		oldVar_hYCXD;
	private transient Date  		oldVar_hYLRD;
	private transient String  		oldVar_hYFRQ;
	private transient Date  		oldVar_hYNRD;
	private transient BigDecimal  		oldVar_hYNOU;
	private transient BigDecimal  		oldVar_hYUNP;
	private transient String  		oldVar_hYCCY;
	private transient BigDecimal  		oldVar_hYCLV;
	private transient BigDecimal  		oldVar_hYSVM;
	private transient BigDecimal  		oldVar_hYMCV;
	private transient BigDecimal  		oldVar_hYBKV;
	private transient BigDecimal  		oldVar_hYTOTA;
	private transient BigDecimal  		oldVar_hYISV;
	private transient Date  		oldVar_hYIXD;
	private transient String  		oldVar_hYNR1;
	private transient String  		oldVar_hYNR2;
	private transient String  		oldVar_hYNR3;
	private transient String  		oldVar_hYNR4;
	private transient Datebox  		oldVar_hYDLM;
	private transient String oldVar_recordStatus;

	private transient boolean validationOn;
	private boolean notes_Entered=false;

	// Button controller for the CRUD buttons
	private transient final String btnCtroller_ClassPrefix = "button_CollateralitemDialog_";
	private transient ButtonStatusCtrl btnCtrl;
	protected Button btnNew; // autowire
	protected Button btnEdit; // autowire
	protected Button btnDelete; // autowire
	protected Button btnSave; // autowire
	protected Button btnCancel; // autowire
	protected Button btnClose; // autowire
	protected Button btnHelp; // autowire
	protected Button btnNotes; // autowire

	protected Button btnSearchPRCustid; // autowire
	protected Button btnSearchHYCCY; // autowire
	protected Textbox lovDescHYCCYName;
	private transient String 		oldVar_lovDescHYCCYName;

	// ServiceDAOs / Domain Classes
	private transient CollateralitemService collateralitemService;
	private transient PagedListService pagedListService;
	private HashMap<String, ArrayList<ErrorDetails>> overideMap= new HashMap<String, ArrayList<ErrorDetails>>();

	protected JdbcSearchObject<Customer> newSearchObject ;
	
	
	protected Row maintainDate;
	protected Row rHYCLR;
	protected Row rHYDLP;
	protected Row rHYAB;
	protected Label or1;
	protected Label or2;
	
	protected Groupbox gb_basicDetails;
	/**
	 * default constructor.<br>
	 */
	public CollateralitemDialogCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * zul-file is called with a parameter for a selected Collateralitem object in a
	 * Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_CollateralitemDialog(Event event) throws Exception {
		logger.debug(event.toString());
		try {
			/* set components visible dependent of the users rights */
			//doCheckRights();

			/* create the Button Controller. Disable not used buttons during working */
			this.btnCtrl = new ButtonStatusCtrl(getUserWorkspace(), this.btnCtroller_ClassPrefix, true, this.btnNew,
					this.btnEdit, this.btnDelete, this.btnSave, this.btnCancel, this.btnClose,this.btnNotes);

			// get the params map that are overhanded by creation.
			final Map<String, Object> args = getCreationArgsMap(event);

			// READ OVERHANDED params !
			if (args.containsKey("collateralitem")) {
				this.collateralitem = (Collateralitem) args.get("collateralitem");
				Collateralitem befImage =new Collateralitem();
				BeanUtils.copyProperties(this.collateralitem, befImage);
				this.collateralitem.setBefImage(befImage);

				setCollateralitem(this.collateralitem);
			} else {
				setCollateralitem(null);
			}

			doLoadWorkFlow(this.collateralitem.isWorkflow(),this.collateralitem.getWorkflowId(),this.collateralitem.getNextTaskId());

			if (isWorkFlowEnabled()){
				this.userAction	= setListRecordStatus(this.userAction);
				getUserWorkspace().alocateRoleAuthorities(getRole(), "CollateralitemDialog");
			}

			// READ OVERHANDED params !
			// we get the collateralitemListWindow controller. So we have access
			// to it and can synchronize the shown data when we do insert, edit or
			// delete collateralitem here.
			if (args.containsKey("collateralitemListCtrl")) {
				setCollateralitemListCtrl((CollateralitemListCtrl) args.get("collateralitemListCtrl"));
			} else {
				setCollateralitemListCtrl(null);
			}

			// set Field Properties
			doSetFieldProperties();
			doShowDialog(getCollateralitem());
		} catch (Exception e) {
			PTMessageUtils.showErrorMessage(e.toString());
			e.printStackTrace();
			window_CollateralitemDialog.onClose();
		}
		logger.debug("Leaving");
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering") ;
		//Empty sent any required attributes
		this.hYCUS.setMaxlength(6);
		this.hYCLC.setMaxlength(3);
		this.hYDLP.setMaxlength(3);
		this.hYDLR.setMaxlength(13);
		this.hYDBNM.setMaxlength(4);
		this.hYAB.setMaxlength(4);
		this.hYAN.setMaxlength(6);
		this.hYAS.setMaxlength(3);
		this.hYCLP.setMaxlength(3);
		this.hYCLR.setMaxlength(35);
		this.hYCNA.setMaxlength(2);
		this.hYDLM.setFormat(PennantConstants.dateFormat);
		this.hYCXD.setFormat(PennantConstants.dateFormat);
		this.hYLRD.setFormat(PennantConstants.dateFormat);
		this.hYNRD.setFormat(PennantConstants.dateFormat);
		this.hYNOU.setMaxlength(9);
		this.hYNOU.setFormat(PennantApplicationUtil.getAmountFormate(0));
		this.hYNOU.setValue(new BigDecimal(1));
		this.hYUNP.setMaxlength(15);
		this.hYUNP.setFormat(PennantApplicationUtil.getAmountFormate(0));
		this.hYUNP.setRoundingMode(BigDecimal.ROUND_DOWN);
		this.hYUNP.setScale(0);
		this.hYCCY.setMaxlength(3);
		this.hYCLV.setMaxlength(15);
		this.hYCLV.setFormat(PennantApplicationUtil.getAmountFormate(0));
		this.hYCLV.setRoundingMode(BigDecimal.ROUND_DOWN);
		this.hYCLV.setScale(0);
		this.hYSVM.setMaxlength(5);
		this.hYSVM.setFormat(PennantApplicationUtil.getAmountFormate(2));
		this.hYSVM.setRoundingMode(BigDecimal.ROUND_DOWN);
		this.hYSVM.setScale(3);
		this.hYMCV.setMaxlength(15);
		this.hYMCV.setFormat(PennantApplicationUtil.getAmountFormate(0));
		this.hYMCV.setRoundingMode(BigDecimal.ROUND_DOWN);
		this.hYMCV.setScale(0);
		this.hYBKV.setMaxlength(15);
		this.hYBKV.setFormat(PennantApplicationUtil.getAmountFormate(2));
		this.hYBKV.setRoundingMode(BigDecimal.ROUND_DOWN);
		this.hYBKV.setScale(2);
		this.hYTOTA.setMaxlength(15);
		this.hYTOTA.setFormat(PennantApplicationUtil.getAmountFormate(0));
		this.hYTOTA.setRoundingMode(BigDecimal.ROUND_DOWN);
		this.hYTOTA.setScale(0);
		this.hYISV.setMaxlength(15);
		this.hYISV.setFormat(PennantApplicationUtil.getAmountFormate(0));
		this.hYISV.setRoundingMode(BigDecimal.ROUND_DOWN);
		this.hYISV.setScale(0);
		this.hYIXD.setFormat(PennantConstants.dateFormat);
		this.hYNR1.setMaxlength(35);
		this.hYNR2.setMaxlength(35);
		this.hYNR3.setMaxlength(35);
		this.hYNR4.setMaxlength(35);
		

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

		getUserWorkspace().alocateAuthorities("CollateralitemDialog");

		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_CollateralitemDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_CollateralitemDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_CollateralitemDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_CollateralitemDialog_btnSave"));
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
	public void onClose$window_CollateralitemDialog(Event event) throws Exception {
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
		PTMessageUtils.showHelpWindow(event, window_CollateralitemDialog);
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
			closeDialog(this.window_CollateralitemDialog, "Collateralitem");	
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
	 * @param aCollateralitem
	 *            Collateralitem
	 */
	public void doWriteBeanToComponents(Collateralitem aCollateralitem) {
		logger.debug("Entering") ;
		this.hYCUS.setValue(aCollateralitem.getHYCUS());
		this.hYCLC.setValue(aCollateralitem.getHYCLC());
		this.hYDLP.setValue(aCollateralitem.getHYDLP());
		this.hYDLR.setValue(aCollateralitem.getHYDLR());
		this.hYDBNM.setValue(aCollateralitem.getHYDBNM());
		this.hYAB.setValue(aCollateralitem.getHYAB());
		this.hYAN.setValue(aCollateralitem.getHYAN());
		this.hYAS.setValue(aCollateralitem.getHYAS());
		this.hYCLP.setValue(aCollateralitem.getHYCLP());
		this.hYCLR.setValue(aCollateralitem.getHYCLR());
		this.hYCCM.setChecked(aCollateralitem.ishYCCM());
		this.hYCNA.setValue(aCollateralitem.getHYCNA());
		this.hYCLO.setValue(aCollateralitem.getHYCLO());
		this.hYDPC.setValue(aCollateralitem.getHYDPC());
		this.hYCPI.setChecked(aCollateralitem.isHYCPI());
		this.hYCXD.setValue(aCollateralitem.getHYCXD());
		this.hYLRD.setValue(aCollateralitem.getHYLRD());
		this.hYFRQ.setValue(aCollateralitem.getHYFRQ());
		
		fillFrqCode(this.hYFRQCode, aCollateralitem.getHYFRQ(),false);
		fillFrqMth(this.hYFRQMth, aCollateralitem.getHYFRQ(), false);
		fillFrqDay(this.hYFRQDay, aCollateralitem.getHYFRQ(),false);
		
		this.hYNRD.setValue(aCollateralitem.getHYNRD());
		this.hYNOU.setValue(aCollateralitem.getHYNOU());
		this.hYUNP.setValue(PennantApplicationUtil.formateAmount(aCollateralitem.getHYUNP(),0));
		this.hYCCY.setValue(aCollateralitem.getHYCCY());
		this.hYCLV.setValue(PennantApplicationUtil.formateAmount(aCollateralitem.getHYCLV(),0));
		this.hYSVM.setValue(PennantApplicationUtil.amountFormate(aCollateralitem.getHYSVM(),0));
		this.hYMCV.setValue(PennantApplicationUtil.formateAmount(aCollateralitem.getHYMCV(),0));
		this.hYBKV.setValue(PennantApplicationUtil.amountFormate(aCollateralitem.getHYBKV(),2));
		this.hYTOTA.setValue(PennantApplicationUtil.formateAmount(aCollateralitem.getHYTOTA(),0));
		this.hYISV.setValue(PennantApplicationUtil.formateAmount(aCollateralitem.getHYISV(),0));
		this.hYIXD.setValue(aCollateralitem.getHYIXD());
		this.hYNR1.setValue(aCollateralitem.getHYNR1());
		this.hYNR2.setValue(aCollateralitem.getHYNR2());
		this.hYNR3.setValue(aCollateralitem.getHYNR3());
		this.hYNR4.setValue(aCollateralitem.getHYNR4());
		this.hYDLM.setValue(aCollateralitem.getHYDLM());

		if(StringUtils.trimToNull(aCollateralitem.getHYCCY()) != null) {
			this.lovDescHYCCYName.setValue(aCollateralitem.getHYCCY()+"-"+aCollateralitem.getLovDescHYCCYName());
		}
		if (aCollateralitem.isNewRecord()){
			this.lovDescCollateralCountryName.setValue("");
			this.lovDescCollateralDeptName.setValue("");
			this.lovDescCollateralLocationName.setValue("");
			this.lovDescCollateralTypeName.setValue("");
		}else{
			
			if(StringUtils.trimToNull(aCollateralitem.getHYCNA()) != null) {
				this.lovDescCollateralCountryName.setValue(aCollateralitem.getHYCNA()+"-"+aCollateralitem.getLovDescHYCNAName());
			}
			if(StringUtils.trimToNull(aCollateralitem.getHYDPC()) != null) {
				this.lovDescCollateralDeptName.setValue(aCollateralitem.getHYDPC()+"-"+aCollateralitem.getLovDescHYDPCName());
			}
			if(StringUtils.trimToNull(aCollateralitem.getHYCLO()) != null) {
				this.lovDescCollateralLocationName.setValue(aCollateralitem.getHYCLO()+"-"+aCollateralitem.getLovDescHYCLOName());
			}
			if(StringUtils.trimToNull(aCollateralitem.getHYCLP()) != null) {
				this.lovDescCollateralTypeName.setValue(aCollateralitem.getHYCLP()+"-"+aCollateralitem.getLovDescHYCLPName());
			}
						
		}
		this.recordStatus.setValue(aCollateralitem.getRecordStatus());
		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aCollateralitem
	 */
	public void doWriteComponentsToBean(Collateralitem aCollateralitem) {
		logger.debug("Entering") ;
		doSetLOVValidation();
		
		if(this.collateralitem.isNewRecord()) {
			doBasicValidation();
		}
	
			ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

			try {
				aCollateralitem.setHYCUS(this.hYCUS.getValue());
			}catch (WrongValueException we ) {
				wve.add(we);
			}
			try {
				aCollateralitem.setHYCLC(this.hYCLC.getValue());
			}catch (WrongValueException we ) {
				wve.add(we);
			}
			try {
				aCollateralitem.setHYDLP(this.hYDLP.getValue());
			}catch (WrongValueException we ) {
				wve.add(we);
			}
			try {
				aCollateralitem.setHYDLR(this.hYDLR.getValue());
			}catch (WrongValueException we ) {
				wve.add(we);
			}
			try {
				aCollateralitem.setHYDBNM(this.hYDBNM.getValue());
			}catch (WrongValueException we ) {
				wve.add(we);
			}
			try {
				aCollateralitem.setHYAB(this.hYAB.getValue());
			}catch (WrongValueException we ) {
				wve.add(we);
			}
			try {
				aCollateralitem.setHYAN(this.hYAN.getValue());
			}catch (WrongValueException we ) {
				wve.add(we);
			}
			try {
				aCollateralitem.setHYAS(this.hYAS.getValue());
			}catch (WrongValueException we ) {
				wve.add(we);
			}
			try {
				aCollateralitem.setHYCLP(this.hYCLP.getValue());
			}catch (WrongValueException we ) {
				wve.add(we);
			}
			try {
				aCollateralitem.setHYCLR(this.hYCLR.getValue());
			}catch (WrongValueException we ) {
				wve.add(we);
			}
			try {
				aCollateralitem.sethYCCM(this.hYCCM.isChecked());
			}catch (WrongValueException we ) {
				wve.add(we);
			}
			try {
				aCollateralitem.setHYCNA(this.hYCNA.getValue());
			}catch (WrongValueException we ) {
				wve.add(we);
			}
			try {
				aCollateralitem.setHYCLO(this.hYCLO.getValue());
			}catch (WrongValueException we ) {
				wve.add(we);
			}
			try {
				aCollateralitem.setHYDPC(this.hYDPC.getValue());
			}catch (WrongValueException we ) {
				wve.add(we);
			}
			try {
				aCollateralitem.setHYCPI(this.hYCPI.isChecked());
			}catch (WrongValueException we ) {
				wve.add(we);
			}
			try {
				aCollateralitem.setHYCXD(this.hYCXD.getValue());
			}catch (WrongValueException we ) {
				wve.add(we);
			}
			try {
				aCollateralitem.setHYLRD(this.hYLRD.getValue());
			}catch (WrongValueException we ) {
				wve.add(we);
			}

			try {
				if (getComboboxValue(this.hYFRQCode).equals("#")) {
					throw new WrongValueException(this.hYFRQCode, Labels.getLabel("STATIC_INVALID",
							new String[] { Labels.getLabel("label_CollateralitemDialog_HYFRQCode.value") }));
				}
			} catch (WrongValueException we) {
				wve.add(we);
			}
			try {
				// to Check frequency code and frequency month
				if ((!getComboboxValue(this.hYFRQCode).equals("#")) && (getComboboxValue(this.hYFRQMth).equals("#"))) {
					throw new WrongValueException(this.hYFRQMth, Labels.getLabel("STATIC_INVALID",
							new String[] { Labels.getLabel("label_CollateralitemDialog_HYFRQMnth.value") }));
				}
			} catch (WrongValueException we) {
				wve.add(we);
			}
			try {
				// to Check frequency month and frequency day
				if ((!getComboboxValue(this.hYFRQMth).equals("#")) && (getComboboxValue(this.hYFRQDay).equals("#"))) {
					throw new WrongValueException(this.hYFRQDay, Labels.getLabel("STATIC_INVALID",
							new String[] { Labels.getLabel("label_CollateralitemDialog_HYFRQDay.value") }));
				}
			} catch (WrongValueException we) {
				wve.add(we);
			}
			try {
				aCollateralitem.setHYFRQ(this.hYFRQ.getValue() == null? "":this.hYFRQ.getValue() );
			} catch (WrongValueException we) {
				wve.add(we);
			}
			try {
				aCollateralitem.setHYNRD(this.hYNRD.getValue());
			}catch (WrongValueException we ) {
				wve.add(we);
			}
			try {
				aCollateralitem.setHYNOU(this.hYNOU.getValue());
			}catch (WrongValueException we ) {
				wve.add(we);
			}
			try {
				if(this.hYUNP.getValue()!=null){
					aCollateralitem.setHYUNP(PennantApplicationUtil.unFormateAmount(this.hYUNP.getValue(), 0));
				}
			}catch (WrongValueException we ) {
				wve.add(we);
			}
			try {
				aCollateralitem.setLovDescHYCCYName(this.lovDescHYCCYName.getValue());
				aCollateralitem.setHYCCY(this.hYCCY.getValue());	
			}catch (WrongValueException we ) {
				wve.add(we);
			}
			try {
				if(this.hYCLV.getValue()!=null){
					aCollateralitem.setHYCLV(PennantApplicationUtil.unFormateAmount(this.hYCLV.getValue(), 0));
				}
			}catch (WrongValueException we ) {
				wve.add(we);
			}
			try {
				if(this.hYSVM.getValue()!=null){
					aCollateralitem.setHYSVM(this.hYSVM.getValue());
				}
			}catch (WrongValueException we ) {
				wve.add(we);
			}
			try {
				if(this.hYMCV.getValue()!=null){
					aCollateralitem.setHYMCV(PennantApplicationUtil.unFormateAmount(this.hYMCV.getValue(), 0));
				}
			}catch (WrongValueException we ) {
				wve.add(we);
			}
			try {
				if(this.hYBKV.getValue()!=null){
					aCollateralitem.setHYBKV(PennantApplicationUtil.unFormateAmount(this.hYBKV.getValue(), 2));
				}
			}catch (WrongValueException we ) {
				wve.add(we);
			}
			try {
				if(this.hYTOTA.getValue()!=null){
					aCollateralitem.setHYTOTA(PennantApplicationUtil.unFormateAmount(this.hYTOTA.getValue(), 0));
				}
			}catch (WrongValueException we ) {
				wve.add(we);
			}
			try {
				if(this.hYISV.getValue()!=null){
					aCollateralitem.setHYISV(PennantApplicationUtil.unFormateAmount(this.hYISV.getValue(), 0));
				}
			}catch (WrongValueException we ) {
				wve.add(we);
			}
			try {
				aCollateralitem.setHYIXD(this.hYIXD.getValue());
			}catch (WrongValueException we ) {
				wve.add(we);
			}
			try {
				aCollateralitem.setHYNR1(this.hYNR1.getValue());
			}catch (WrongValueException we ) {
				wve.add(we);
			}
			try {
				aCollateralitem.setHYNR2(this.hYNR2.getValue());
			}catch (WrongValueException we ) {
				wve.add(we);
			}
			try {
				aCollateralitem.setHYNR3(this.hYNR3.getValue());
			}catch (WrongValueException we ) {
				wve.add(we);
			}
			try {
				aCollateralitem.setHYNR4(this.hYNR4.getValue());
			}catch (WrongValueException we ) {
				wve.add(we);
			}
			try {
				aCollateralitem.setHYDLM(this.hYDLM.getValue());
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

			aCollateralitem.setHYDLM(DateUtility.getUtilDate());
			aCollateralitem.setRecordStatus(this.recordStatus.getValue());
		
		logger.debug("Leaving");
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the
	 * readOnly mode accordingly.
	 * 
	 * @param aCollateralitem
	 * @throws InterruptedException
	 */
	public void doShowDialog(Collateralitem aCollateralitem) throws InterruptedException {
		logger.debug("Entering") ;

		// if aCollateralitem == null then we opened the Dialog without
		// args for a given entity, so we get a new Obj().
		if (aCollateralitem == null) {
			/** !!! DO NOT BREAK THE TIERS !!! */
			// We don't create a new DomainObject() in the frontend.
			// We GET it from the backend.
			aCollateralitem = getCollateralitemService().getNewCollateralitem();

			setCollateralitem(aCollateralitem);
		} else {
			setCollateralitem(aCollateralitem);
		}

		// set Readonly mode accordingly if the object is new or not.
		if (aCollateralitem.isNew()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.hYCUS.focus();
		} else {
			//this.hYCLC.focus();
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
			doWriteBeanToComponents(aCollateralitem);

			// stores the initial data for comparing if they are changed
			// during user action.
			doStoreInitValues();
			setDialog(this.window_CollateralitemDialog);
		} catch (final Exception e) {
			logger.error(e);
			PTMessageUtils.showErrorMessage(e.toString());
		}
		
		
		if (!this.collateralitem.isNewRecord()) {
			if ("Approved".equals(this.collateralitem.getRecordStatus())) {
				this.maintainDate.setVisible(true);
				
				this.hYCCM.setVisible(false);
				if (this.hYCCM.isChecked()) {
					this.lhYCCM.setValue("Yes");
				} else {
					this.lhYCCM.setValue("No");
				}
				this.lhYCCM.setVisible(true);
				
				this.hYCPI.setVisible(false);
				if (this.hYCPI.isChecked()) {
					this.lhYCPI.setValue("Yes");
				} else {
					this.lhYCPI.setValue("No");
				}
				this.lhYCPI.setVisible(true);
			}	
			
			if (StringUtils.trimToNull(aCollateralitem.getHYCLR()) != null) {
				this.rHYCLR.setVisible(true);
				this.hYCLR.setReadonly(true);				
			} else {
				this.rHYCLR.setVisible(false);
				this.hYCLR.setReadonly(false);	
			}
			if (StringUtils.trimToNull(aCollateralitem.getHYDLP()) != null) {
				this.rHYDLP.setVisible(true);
				this.hYDLP.setReadonly(true);
				this.hYDBNM.setReadonly(true);
				this.hYDLR.setReadonly(true);
			} else {
				this.rHYDLP.setVisible(false);
				this.hYDLP.setReadonly(false);
				this.hYDBNM.setReadonly(false);
				this.hYDLR.setReadonly(false);
			}
			if (StringUtils.trimToNull(aCollateralitem.getHYAB()) != null) {
				this.rHYAB.setVisible(true);
				this.hYAB.setVisible(true);
				this.hYAN.setVisible(true);
				this.hYAS.setVisible(true);
			} else {
				this.rHYAB.setVisible(false);
				this.hYAB.setVisible(false);
				this.hYAN.setVisible(false);
				this.hYAS.setVisible(false);
			}
			
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
		this.oldVar_hYCUS = this.hYCUS.getValue();
		this.oldVar_hYCLC = this.hYCLC.getValue();
		this.oldVar_hYDLP = this.hYDLP.getValue();
		this.oldVar_hYDLR = this.hYDLR.getValue();
		this.oldVar_hYDBNM = this.hYDBNM.getValue();
		this.oldVar_hYAB = this.hYAB.getValue();
		this.oldVar_hYAN = this.hYAN.getValue();
		this.oldVar_hYAS = this.hYAS.getValue();
		this.oldVar_hYCLP = this.hYCLP.getValue();
		this.oldVar_hYCLR = this.hYCLR.getValue();
		this.oldVar_hYCCM = this.hYCCM.isChecked();
		this.oldVar_hYCNA = this.hYCNA.getValue();
		this.oldVar_hYCLO = this.hYCLO.getValue();
		this.oldVar_hYDPC = this.hYDPC.getValue();
		this.oldVar_hYCPI = this.hYCPI.isChecked();
		this.oldVar_hYCXD = this.hYCXD.getValue();
		this.oldVar_hYLRD = this.hYLRD.getValue();
		this.oldVar_hYFRQ = this.hYFRQ.getValue();
		this.oldVar_hYNRD = this.hYNRD.getValue();
		this.oldVar_hYNOU = this.hYNOU.getValue();	
		this.oldVar_hYUNP = this.hYUNP.getValue();
		this.oldVar_hYCCY = this.hYCCY.getValue();
		this.oldVar_lovDescHYCCYName = this.lovDescHYCCYName.getValue();
		this.oldVar_hYCLV = this.hYCLV.getValue();
		this.oldVar_hYSVM = this.hYSVM.getValue();
		this.oldVar_hYMCV = this.hYMCV.getValue();
		this.oldVar_hYBKV = this.hYBKV.getValue();
		this.oldVar_hYTOTA = this.hYTOTA.getValue();
		this.oldVar_hYISV = this.hYISV.getValue();
		this.oldVar_hYIXD = this.hYIXD.getValue();
		this.oldVar_hYNR1 = this.hYNR1.getValue();
		this.oldVar_hYNR2 = this.hYNR2.getValue();
		this.oldVar_hYNR3 = this.hYNR3.getValue();
		this.oldVar_hYNR4 = this.hYNR4.getValue();
		this.oldVar_recordStatus = this.recordStatus.getValue();
		logger.debug("Leaving") ;
	}

	/**
	 * Resets the init values from mem vars. <br>
	 */
	private void doResetInitValues() {
		logger.debug("Entering");
		this.hYCUS.setValue(this.oldVar_hYCUS);
		this.hYCLC.setValue(this.oldVar_hYCLC);
		this.hYDLP.setValue(this.oldVar_hYDLP);
		this.hYDLR.setValue(this.oldVar_hYDLR);
		this.hYDBNM.setValue(this.oldVar_hYDBNM);
		this.hYAB.setValue(this.oldVar_hYAB);
		this.hYAN.setValue(this.oldVar_hYAN);
		this.hYAS.setValue(this.oldVar_hYAS);
		this.hYCLP.setValue(this.oldVar_hYCLP);
		this.hYCLR.setValue(this.oldVar_hYCLR);
		this.hYCCM.setChecked(this.oldVar_hYCCM);
		this.hYCNA.setValue(this.oldVar_hYCNA);
		this.hYCLO.setValue(this.oldVar_hYCLO);
		this.hYDPC.setValue(this.oldVar_hYDPC);
		this.hYCPI.setChecked(this.oldVar_hYCPI);
		this.hYCXD.setValue(this.oldVar_hYCXD);
		this.hYLRD.setValue(this.oldVar_hYLRD);
		this.hYFRQ.setValue(this.oldVar_hYFRQ);
		this.hYNRD.setValue(this.oldVar_hYNRD);
		this.hYNOU.setValue(this.oldVar_hYNOU);
		this.hYUNP.setValue(this.oldVar_hYUNP);
		this.hYCCY.setValue(this.oldVar_hYCCY);
		this.lovDescHYCCYName.setValue(this.oldVar_lovDescHYCCYName);
		this.hYCLV.setValue(this.oldVar_hYCLV);
		this.hYSVM.setValue(this.oldVar_hYSVM);
		this.hYMCV.setValue(this.oldVar_hYMCV);
		this.hYBKV.setValue(this.oldVar_hYBKV);
		this.hYTOTA.setValue(this.oldVar_hYTOTA);
		this.hYISV.setValue(this.oldVar_hYISV);
		this.hYIXD.setValue(this.oldVar_hYIXD);
		this.hYNR1.setValue(this.oldVar_hYNR1);
		this.hYNR2.setValue(this.oldVar_hYNR2);
		this.hYNR3.setValue(this.oldVar_hYNR3);
		this.hYNR4.setValue(this.oldVar_hYNR4);
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
		if (this.oldVar_hYCUS != this.hYCUS.getValue()) {
			return true;
		}
		if (this.oldVar_hYCLC != this.hYCLC.getValue()) {
			return true;
		}
		if (this.oldVar_hYDLP != this.hYDLP.getValue()) {
			return true;
		}
		if (this.oldVar_hYDLR != this.hYDLR.getValue()) {
			return true;
		}
		if (this.oldVar_hYDBNM != this.hYDBNM.getValue()) {
			return true;
		}
		if (this.oldVar_hYAB != this.hYAB.getValue()) {
			return true;
		}
		if (this.oldVar_hYAN != this.hYAN.getValue()) {
			return true;
		}
		if (this.oldVar_hYAS != this.hYAS.getValue()) {
			return true;
		}
		if (this.oldVar_hYCLP != this.hYCLP.getValue()) {
			return true;
		}
		if (this.oldVar_hYCLR != this.hYCLR.getValue()) {
			return true;
		}
		if (this.oldVar_hYCCM != this.hYCCM.isChecked()) {
			return true;
		}
		if (this.oldVar_hYCNA != this.hYCNA.getValue()) {
			return true;
		}
		if (this.oldVar_hYCLO != this.hYCLO.getValue()) {
			return true;
		}
		if (this.oldVar_hYDPC != this.hYDPC.getValue()) {
			return true;
		}
		if (this.oldVar_hYCPI != this.hYCPI.isChecked()) {
			return true;
		}
		String oldHYCXD = "";
		String newHYCXD ="";
		if (this.oldVar_hYCXD!=null){
			oldHYCXD=DateUtility.formatDate(this.oldVar_hYCXD,PennantConstants.dateFormat);
		}
		if (this.hYCXD.getValue()!=null){
			newHYCXD=DateUtility.formatDate(this.hYCXD.getValue(),PennantConstants.dateFormat);
		}
		if (!StringUtils.trimToEmpty(oldHYCXD).equals(StringUtils.trimToEmpty(newHYCXD))) {
			return true;
		}
		String oldHYLRD = "";
		String newHYLRD ="";
		if (this.oldVar_hYLRD!=null){
			oldHYLRD=DateUtility.formatDate(this.oldVar_hYLRD,PennantConstants.dateFormat);
		}
		if (this.hYLRD.getValue()!=null){
			newHYLRD=DateUtility.formatDate(this.hYLRD.getValue(),PennantConstants.dateFormat);
		}
		if (!StringUtils.trimToEmpty(oldHYLRD).equals(StringUtils.trimToEmpty(newHYLRD))) {
			return true;
		}
		if (this.oldVar_hYFRQ != this.hYFRQ.getValue()) {
			return true;
		}
		String oldHYNRD = "";
		String newHYNRD ="";
		if (this.oldVar_hYNRD!=null){
			oldHYNRD=DateUtility.formatDate(this.oldVar_hYNRD,PennantConstants.dateFormat);
		}
		if (this.hYNRD.getValue()!=null){
			newHYNRD=DateUtility.formatDate(this.hYNRD.getValue(),PennantConstants.dateFormat);
		}
		if (!StringUtils.trimToEmpty(oldHYNRD).equals(StringUtils.trimToEmpty(newHYNRD))) {
			return true;
		}
		if (this.oldVar_hYNOU != this.hYNOU.getValue()) {
			return  true;
		}
		if (this.oldVar_hYUNP != this.hYUNP.getValue()) {
			return true;
		}
		if (this.oldVar_hYCCY != this.hYCCY.getValue()) {
			return true;
		}
		if (this.oldVar_hYCLV != this.hYCLV.getValue()) {
			return true;
		}
		if (this.oldVar_hYSVM != this.hYSVM.getValue()) {
			return true;
		}
		if (this.oldVar_hYMCV != this.hYMCV.getValue()) {
			return true;
		}
		if (this.oldVar_hYBKV != this.hYBKV.getValue()) {
			return true;
		}
		if (this.oldVar_hYTOTA != this.hYTOTA.getValue()) {
			return true;
		}
		if (this.oldVar_hYISV != this.hYISV.getValue()) {
			return true;
		}
		String oldHYIXD = "";
		String newHYIXD ="";
		if (this.oldVar_hYIXD!=null){
			oldHYIXD=DateUtility.formatDate(this.oldVar_hYIXD,PennantConstants.dateFormat);
		}
		if (this.hYIXD.getValue()!=null){
			newHYIXD=DateUtility.formatDate(this.hYIXD.getValue(),PennantConstants.dateFormat);
		}
		if (!StringUtils.trimToEmpty(oldHYIXD).equals(StringUtils.trimToEmpty(newHYIXD))) {
			return true;
		}
		if (this.oldVar_hYNR1 != this.hYNR1.getValue()) {
			return true;
		}
		if (this.oldVar_hYNR2 != this.hYNR2.getValue()) {
			return true;
		}
		if (this.oldVar_hYNR3 != this.hYNR3.getValue()) {
			return true;
		}
		if (this.oldVar_hYNR4 != this.hYNR4.getValue()) {
			return true;
		}
		
		logger.debug("Leaving"); 
		return false;
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug("Entering");
		setValidationOn(true);		
		
		
		
		/*if(!StringUtils.trimToEmpty(getCollateralitem().getRecordStatus()).equals(PennantConstants.RCD_STATUS_APPROVED) && 
				!StringUtils.trimToEmpty(getCollateralitem().getRecordType()).equals(PennantConstants.RECORD_TYPE_UPD)){
			
			
			if (!this.hYCLR.isReadonly()){
				this.hYCLR.setConstraint("NO EMPTY:" + Labels.getLabel("FIELD_NO_EMPTY",new String[]{Labels.getLabel("label_CollateralitemDialog_HYCLR.value")}));
			}
			if (!this.hYDLP.isReadonly()){
				this.hYDLP.setConstraint("NO EMPTY:" + Labels.getLabel("FIELD_NO_EMPTY",new String[]{Labels.getLabel("label_CollateralitemDialog_HYDLP.value")}));
			}	
			if (!this.hYDLR.isReadonly()){
				this.hYDLR.setConstraint("NO EMPTY:" + Labels.getLabel("FIELD_NO_EMPTY",new String[]{Labels.getLabel("label_CollateralitemDialog_HYDLR.value")}));
			}	
			if (!this.hYDBNM.isReadonly()){
				this.hYDBNM.setConstraint("NO EMPTY:" + Labels.getLabel("FIELD_NO_EMPTY",new String[]{Labels.getLabel("label_CollateralitemDialog_HYDBNM.value")}));
			}	
			if (!this.hYAB.isReadonly()){
				this.hYAB.setConstraint("NO EMPTY:" + Labels.getLabel("FIELD_NO_EMPTY",new String[]{Labels.getLabel("label_CollateralitemDialog_HYAB.value")}));
			}	
			if (!this.hYAN.isReadonly()){
				this.hYAN.setConstraint("NO EMPTY:" + Labels.getLabel("FIELD_NO_EMPTY",new String[]{Labels.getLabel("label_CollateralitemDialog_HYAN.value")}));
			}	
			if (!this.hYAS.isReadonly()){
				this.hYAS.setConstraint("NO EMPTY:" + Labels.getLabel("FIELD_NO_EMPTY",new String[]{Labels.getLabel("label_CollateralitemDialog_HYAS.value")}));
			}	
		}*/

		if (!this.hYCUS.isReadonly()){
			this.hYCUS.setConstraint("NO EMPTY:" + Labels.getLabel("FIELD_NO_EMPTY",new String[]{Labels.getLabel("label_CollateralitemDialog_HYCUS.value")}));
		}	
//		if (!this.hYCLC.isReadonly()){
//			this.hYCLC.setConstraint("NO EMPTY:" + Labels.getLabel("FIELD_NO_EMPTY",new String[]{Labels.getLabel("label_CollateralitemDialog_HYCLC.value")}));
//		}	
//		if (!this.hYCNA.isReadonly()){
//			this.hYCNA.setConstraint("NO EMPTY:" + Labels.getLabel("FIELD_NO_EMPTY",new String[]{Labels.getLabel("label_CollateralitemDialog_HYCNA.value")}));
//		}	
//		if (!this.hYCLO.isDisabled()){
//			this.hYCLO.setConstraint("NO EMPTY:" + Labels.getLabel("FIELD_NO_EMPTY",new String[]{Labels.getLabel("label_CollateralitemDialog_HYCLO.value")}));
//		}	
//		if (!this.hYDPC.isDisabled()){
//			this.hYDPC.setConstraint("NO EMPTY:" + Labels.getLabel("FIELD_NO_EMPTY",new String[]{Labels.getLabel("label_CollateralitemDialog_HYDPC.value")}));
//		}	
//		if (!this.hYCXD.isDisabled()){
//			this.hYCXD.setConstraint("NO EMPTY:" + Labels.getLabel("FIELD_NO_EMPTY",new String[]{Labels.getLabel("label_CollateralitemDialog_HYCXD.value")}));
//		}
//		if (!this.hYLRD.isDisabled()){
//			this.hYLRD.setConstraint("NO EMPTY:" + Labels.getLabel("FIELD_NO_EMPTY",new String[]{Labels.getLabel("label_CollateralitemDialog_HYLRD.value")}));
//		}
//		if (!this.hYNRD.isDisabled()){
//			this.hYNRD.setConstraint("NO EMPTY:" + Labels.getLabel("FIELD_NO_EMPTY",new String[]{Labels.getLabel("label_CollateralitemDialog_HYNRD.value")}));
//		}
		if (!this.hYNOU.isReadonly()){
			this.hYNOU.setConstraint(new IntValidator(9,Labels.getLabel("label_CollateralitemDialog_HYNOU.value")));
		}	
		if (!this.hYUNP.isReadonly()){
			this.hYUNP.setConstraint(new AmountValidator(15,0,Labels.getLabel("label_CollateralitemDialog_HYUNP.value")));
		}	
		if (!this.hYCLV.isReadonly()){
			this.hYCLV.setConstraint(new AmountValidator(15,0,Labels.getLabel("label_CollateralitemDialog_HYCLV.value")));
		}	
		if (!this.hYSVM.isReadonly()){
			this.hYSVM.setConstraint(new AmountValidator(5,3,Labels.getLabel("label_CollateralitemDialog_HYSVM.value")));
		}	
		/*if (!this.hYMCV.isReadonly()){
			this.hYMCV.setConstraint(new AmountValidator(15,0,Labels.getLabel("label_CollateralitemDialog_HYMCV.value")));
		}	
		if (!this.hYBKV.isReadonly()){
			this.hYBKV.setConstraint(new AmountValidator(15,0,Labels.getLabel("label_CollateralitemDialog_HYBKV.value")));
		}	*/
		if (!this.hYTOTA.isReadonly()){
			this.hYTOTA.setConstraint(new AmountValidator(15,0,Labels.getLabel("label_CollateralitemDialog_HYTOTA.value")));
		}	
		if (!this.hYISV.isReadonly()){
			this.hYISV.setConstraint(new AmountValidator(15,0,Labels.getLabel("label_CollateralitemDialog_HYISV.value")));
		}	
		if (!this.hYIXD.isDisabled()){
			this.hYIXD.setConstraint("NO EMPTY:" + Labels.getLabel("FIELD_NO_EMPTY",new String[]{Labels.getLabel("label_CollateralitemDialog_HYIXD.value")}));
		}
		if (!this.hYNR1.isReadonly()){
			this.hYNR1.setConstraint("NO EMPTY:" + Labels.getLabel("FIELD_NO_EMPTY",new String[]{Labels.getLabel("label_CollateralitemDialog_HYNR1.value")}));
		}	
		/*if (!this.hYNR2.isReadonly()){
			this.hYNR2.setConstraint("NO EMPTY:" + Labels.getLabel("FIELD_NO_EMPTY",new String[]{Labels.getLabel("label_CollateralitemDialog_HYNR2.value")}));
		}	
		if (!this.hYNR3.isReadonly()){
			this.hYNR3.setConstraint("NO EMPTY:" + Labels.getLabel("FIELD_NO_EMPTY",new String[]{Labels.getLabel("label_CollateralitemDialog_HYNR3.value")}));
		}	
		if (!this.hYNR4.isReadonly()){
			this.hYNR4.setConstraint("NO EMPTY:" + Labels.getLabel("FIELD_NO_EMPTY",new String[]{Labels.getLabel("label_CollateralitemDialog_HYNR4.value")}));
		}*/	
		/*if (!this.hYDLM.isDisabled()){
			this.hYDLM.setConstraint("NO EMPTY:" + Labels.getLabel("FIELD_NO_EMPTY",new String[]{Labels.getLabel("label_CollateralitemDialog_HYDLM.value")}));
		}*/
		logger.debug("Leaving");
	}

	/**
	 * Disables the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering");
		setValidationOn(false);
		this.hYCUS.setConstraint("");
		this.hYCLC.setConstraint("");
		this.hYDLP.setConstraint("");
		this.hYDLR.setConstraint("");
		this.hYDBNM.setConstraint("");
		this.hYAB.setConstraint("");
		this.hYAN.setConstraint("");
		this.hYAS.setConstraint("");
		this.hYCLP.setConstraint("");
		this.hYCLR.setConstraint("");
		this.hYCNA.setConstraint("");
		this.hYCLO.setConstraint("");
		this.hYDPC.setConstraint("");
		this.hYCXD.setConstraint("");
		this.hYLRD.setConstraint("");
		this.hYFRQ.setConstraint("");
		this.hYNRD.setConstraint("");
		this.hYNOU.setConstraint("");
		this.hYUNP.setConstraint("");
		this.hYCLV.setConstraint("");
		this.hYSVM.setConstraint("");
		this.hYMCV.setConstraint("");
		this.hYBKV.setConstraint("");
		this.hYTOTA.setConstraint("");
		this.hYISV.setConstraint("");
		this.hYIXD.setConstraint("");
		this.hYNR1.setConstraint("");
		this.hYNR2.setConstraint("");
		this.hYNR3.setConstraint("");
		this.hYNR4.setConstraint("");
		this.hYDLM.setConstraint("");
		logger.debug("Leaving");
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// +++++++++++++++++++++++++ crud operations +++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * Deletes a Collateralitem object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug("Entering");	
		final Collateralitem aCollateralitem = new Collateralitem();
		BeanUtils.copyProperties(getCollateralitem(), aCollateralitem);
		String tranType=PennantConstants.TRAN_WF;

		// Show a confirm box
		final String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record") + "\n\n --> " + aCollateralitem.getHYCUS();
		final String title = Labels.getLabel("message.Deleting.Record");
		MultiLineMessageBox.doSetTemplate();

		int conf =  (MultiLineMessageBox.show(msg, title, MultiLineMessageBox.YES| MultiLineMessageBox.NO, Messagebox.QUESTION, true));

		if (conf==MultiLineMessageBox.YES){
			logger.debug("doDelete: Yes");

			if (StringUtils.trimToEmpty(aCollateralitem.getRecordType()).equals("")){
				aCollateralitem.setVersion(aCollateralitem.getVersion()+1);
				aCollateralitem.setRecordType(PennantConstants.RECORD_TYPE_DEL);

				if (isWorkFlowEnabled()){
					aCollateralitem.setNewRecord(true);
					tranType=PennantConstants.TRAN_WF;
				}else{
					tranType=PennantConstants.TRAN_DEL;
				}
			}

			try {
				if(doProcess(aCollateralitem,tranType)){
					refreshList();
					closeDialog(this.window_CollateralitemDialog, "Collateralitem"); 
				}

			}catch (DataAccessException e){
				logger.error("doDelete " + e);
				showMessage(e);
			}

		}
		logger.debug("Leaving");
	}

	/**
	 * Create a new Collateralitem object. <br>
	 */
	private void doNew() {
		logger.debug("Entering");

		final Collateralitem aCollateralitem = getCollateralitemService().getNewCollateralitem();
		aCollateralitem.setNewRecord(true);
		setCollateralitem(aCollateralitem);
		doClear(); // clear all commponents
		doEdit(); // edit mode
		this.btnCtrl.setBtnStatus_New();

		// remember the old vars
		doStoreInitValues();

		// setFocus
		this.hYCUS.focus();
		logger.debug("Leaving");
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug("Entering");
		// Last Review Date & Next Review Date
		this.hYNRD.setReadonly(false);
		this.hYNRD.setDisabled(true);
		this.hYLRD.setReadonly(false);
		this.hYLRD.setDisabled(true);

		if (getCollateralitem().isNewRecord()){
			this.hYCUS.setReadonly(false);
			this.btnCancel.setVisible(false);
		}else{
			or1.setVisible(false);
			or2.setVisible(false);
			
			this.hYCUS.setReadonly(true);
			this.btnCancel.setVisible(true);
			
			// Customer
			this.hYCUS.setReadonly(true);
			this.hYCLC.setReadonly(true);
			this.btnSearchPRCustid.setDisabled(true);
			
			// Deal
			this.hYDLP.setReadonly(true);
			this.hYDBNM.setReadonly(true);
			this.hYDLR.setReadonly(true);
			
			// Account Number
			this.hYAB.setReadonly(true);
			this.hYAN.setReadonly(true);
			this.hYAS.setReadonly(true);
			
		}
		
		

	/*	this.hYCLC.setReadonly(isReadOnly("CollateralitemDialog_hYCLC"));
		this.hYDLP.setReadonly(isReadOnly("CollateralitemDialog_hYDLP"));
		this.hYDLR.setReadonly(isReadOnly("CollateralitemDialog_hYDLR"));
		this.hYDBNM.setReadonly(isReadOnly("CollateralitemDialog_hYDBNM"));
		this.hYAB.setReadonly(isReadOnly("CollateralitemDialog_hYAB"));
		this.hYAN.setReadonly(isReadOnly("CollateralitemDialog_hYAN"));
		this.hYAS.setReadonly(isReadOnly("CollateralitemDialog_hYAS"));
		this.hYCLP.setReadonly(isReadOnly("CollateralitemDialog_hYCLP"));
		this.hYCLR.setReadonly(isReadOnly("CollateralitemDialog_hYCLR"));
		this.hYCCM.setDisabled(isReadOnly("CollateralitemDialog_hYCCM"));
		this.hYCNA.setReadonly(isReadOnly("CollateralitemDialog_hYCNA"));
		this.hYCLO.setReadonly(isReadOnly("CollateralitemDialog_hYCLO"));
		this.hYDPC.setReadonly(isReadOnly("CollateralitemDialog_hYDPC"));
		this.hYCPI.setDisabled(isReadOnly("CollateralitemDialog_hYCPI"));
		this.hYCXD.setDisabled(isReadOnly("CollateralitemDialog_hYCXD"));
		this.hYLRD.setDisabled(isReadOnly("CollateralitemDialog_hYLRD"));
		this.hYFRQ.setDisabled(isReadOnly("CollateralitemDialog_hYFRQ"));
		this.hYNRD.setDisabled(isReadOnly("CollateralitemDialog_hYNRD"));
		this.hYNOU.setDisabled(isReadOnly("CollateralitemDialog_hYNOU"));
		this.hYUNP.setDisabled(isReadOnly("CollateralitemDialog_hYUNP"));
		this.btnSearchHYCCY.setDisabled(isReadOnly("CollateralitemDialog_hYCCY"));
		this.hYCLV.setDisabled(isReadOnly("CollateralitemDialog_hYCLV"));
		this.hYSVM.setDisabled(isReadOnly("CollateralitemDialog_hYSVM"));
		this.hYMCV.setDisabled(isReadOnly("CollateralitemDialog_hYMCV"));
		this.hYBKV.setDisabled(isReadOnly("CollateralitemDialog_hYBKV"));
		this.hYTOTA.setDisabled(isReadOnly("CollateralitemDialog_hYTOTA"));
		this.hYISV.setDisabled(isReadOnly("CollateralitemDialog_hYISV"));
		this.hYIXD.setDisabled(isReadOnly("CollateralitemDialog_hYIXD"));
		this.hYNR1.setReadonly(isReadOnly("CollateralitemDialog_hYNR1"));
		this.hYNR2.setReadonly(isReadOnly("CollateralitemDialog_hYNR2"));
		this.hYNR3.setReadonly(isReadOnly("CollateralitemDialog_hYNR3"));
		this.hYNR4.setReadonly(isReadOnly("CollateralitemDialog_hYNR4"));
		this.hYDLM.setDisabled(isReadOnly("CollateralitemDialog_hYDLM"));
*/
		if (isWorkFlowEnabled()){
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}

			if (this.collateralitem.isNewRecord()){
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
		this.hYCUS.setReadonly(true);
		this.hYCLC.setReadonly(true);
		this.hYDLP.setReadonly(true);
		this.hYDLR.setReadonly(true);
		this.hYDBNM.setReadonly(true);
		this.hYAB.setReadonly(true);
		this.hYAN.setReadonly(true);
		this.hYAS.setReadonly(true);
		this.hYCLP.setReadonly(true);
		this.hYCLR.setReadonly(true);
		this.hYCCM.setDisabled(true);
		this.hYCNA.setReadonly(true);
		this.hYCLO.setDisabled(true);
		this.hYDPC.setDisabled(true);
		this.hYCPI.setDisabled(true);
		this.hYCXD.setDisabled(true);
		this.hYLRD.setDisabled(true);
		this.hYFRQ.setDisabled(true);
		this.hYNRD.setDisabled(true);
		this.hYNOU.setReadonly(true);
		this.hYUNP.setReadonly(true);
		this.btnSearchHYCCY.setDisabled(true);
		this.hYCLV.setReadonly(true);
		this.hYSVM.setReadonly(true);
		this.hYMCV.setReadonly(true);
		this.hYBKV.setReadonly(true);
		this.hYTOTA.setReadonly(true);
		this.hYISV.setReadonly(true);
		this.hYIXD.setDisabled(true);
		this.hYNR1.setReadonly(true);
		this.hYNR2.setReadonly(true);
		this.hYNR3.setReadonly(true);
		this.hYNR4.setReadonly(true);
		this.hYDLM.setDisabled(true);

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

		this.hYCUS.setValue("");
		this.hYCLC.setValue("");
		this.hYDLP.setValue("");
		this.hYDLR.setValue("");
		this.hYDBNM.setValue("");
		this.hYAB.setValue("");
		this.hYAN.setValue("");
		this.hYAS.setValue("");
		this.hYCLP.setValue("");
		this.hYCLR.setValue("");
		this.hYCCM.setValue("");
		this.hYCNA.setValue("");
		this.hYCLO.setValue("");
		this.hYDPC.setValue("");
		this.hYCPI.setValue("");
		this.hYCXD.setText("");
		this.hYLRD.setText("");
		this.hYFRQ.setValue("");
		this.hYNRD.setText("");
		this.hYNOU.setText("");
		this.hYUNP.setValue("");
		this.hYCCY.setValue("");
		this.lovDescHYCCYName.setValue("");
		this.hYCLV.setValue("");
		this.hYSVM.setValue("");
		this.hYMCV.setValue("");
		this.hYBKV.setValue("");
		this.hYTOTA.setValue("");
		this.hYISV.setValue("");
		this.hYIXD.setText("");
		this.hYNR1.setValue("");
		this.hYNR2.setValue("");
		this.hYNR3.setValue("");
		this.hYNR4.setValue("");
		this.hYDLM.setText("");
		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug("Entering");
		final Collateralitem aCollateralitem = new Collateralitem();
		BeanUtils.copyProperties(getCollateralitem(), aCollateralitem);
		boolean isNew = false;

		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		// force validation, if on, than execute by component.getValue()
		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		doSetValidation();
		// fill the Collateralitem object with the components data
		doWriteComponentsToBean(aCollateralitem);

		// Write the additional validations as per below example
		// get the selected branch object from the listbox
		// Do data level validations here

		isNew = aCollateralitem.isNew();
		String tranType="";

		if(isWorkFlowEnabled()){
			tranType =PennantConstants.TRAN_WF;
			if (StringUtils.trimToEmpty(aCollateralitem.getRecordType()).equals("")){
				aCollateralitem.setVersion(aCollateralitem.getVersion()+1);
				if(isNew){
					aCollateralitem.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else{
					aCollateralitem.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aCollateralitem.setNewRecord(true);
				}
			}
		}else{
			aCollateralitem.setVersion(aCollateralitem.getVersion()+1);
			if(isNew){
				tranType =PennantConstants.TRAN_ADD;
			}else{
				tranType =PennantConstants.TRAN_UPD;
			}
		}

		// save it to database
		try {

			if(doProcess(aCollateralitem,tranType)){
				doWriteBeanToComponents(aCollateralitem);
				refreshList();
				closeDialog(this.window_CollateralitemDialog, "Collateralitem");
			}

		} catch (final DataAccessException e) {
			logger.error(e);
			showMessage(e);
		}
		logger.debug("Leaving");
	}

	private boolean doProcess(Collateralitem aCollateralitem,String tranType){
		logger.debug("Entering");
		boolean processCompleted=false;
		AuditHeader auditHeader =  null;
		String nextRoleCode="";

		aCollateralitem.setLastMntBy(getUserWorkspace().getLoginUserDetails().getLoginUsrID());
		aCollateralitem.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aCollateralitem.setUserDetails(getUserWorkspace().getLoginUserDetails());

		if (isWorkFlowEnabled()) {
			String taskId = getWorkFlow().getTaskId(getRole());
			String nextTaskId = "";
			//Upgraded to ZK-6.5.1.1 Added casting to String 	
			aCollateralitem.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aCollateralitem.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getWorkFlow().getNextTaskIds(taskId, aCollateralitem);
				}

				if (PennantConstants.WF_Audit_Notes.equals(getWorkFlow().getAuditingReq(taskId,aCollateralitem))) {
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

			aCollateralitem.setTaskId(taskId);
			aCollateralitem.setNextTaskId(nextTaskId);
			aCollateralitem.setRoleCode(getRole());
			aCollateralitem.setNextRoleCode(nextRoleCode);

			auditHeader =  getAuditHeader(aCollateralitem, tranType);

			String operationRefs = getWorkFlow().getOperationRefs(taskId,aCollateralitem);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader,null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader =  getAuditHeader(aCollateralitem, PennantConstants.TRAN_WF);
					processCompleted  = doSaveProcess(auditHeader, list[i]);
					if(!processCompleted){
						break;
					}
				}
			}
		}else{

			auditHeader =  getAuditHeader(aCollateralitem, tranType);
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

		Collateralitem aCollateralitem = (Collateralitem) auditHeader.getAuditDetail().getModelData();

		try {

			while(retValue==PennantConstants.porcessOVERIDE){

				if (StringUtils.trimToEmpty(method).equalsIgnoreCase("")){
					if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)){
						auditHeader = getCollateralitemService().delete(auditHeader);
						deleteNotes=true;
					}else{
						auditHeader = getCollateralitemService().saveOrUpdate(auditHeader);	
					}

				}else{
					if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)){
						auditHeader = getCollateralitemService().doApprove(auditHeader);

						if(aCollateralitem.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)){
							deleteNotes=true;
						}

					}else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)){
						auditHeader = getCollateralitemService().doReject(auditHeader);
						if(aCollateralitem.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){
							deleteNotes=true;
						}

					}else{
						auditHeader.setErrorDetails(new ErrorDetails(PennantConstants.ERR_9999, Labels.getLabel("InvalidWorkFlowMethod"), null));
						retValue = ErrorControl.showErrorControl(this.window_CollateralitemDialog, auditHeader);
						return processCompleted; 
					}
				}

				auditHeader =	ErrorControl.showErrorDetails(this.window_CollateralitemDialog, auditHeader);
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

	public void onClick$btnSearchHYCCY(Event event){	   
		Object dataObject = ExtendedSearchListBox.show(this.window_CollateralitemDialog,"Currency");
		if (dataObject instanceof String){
			this.hYCCY.setValue(dataObject.toString());
			this.lovDescHYCCYName.setValue("");
		}else{
			Currency details= (Currency) dataObject;
			if (details != null) {
				this.hYCCY.setValue(details.getLovValue());
				this.lovDescHYCCYName.setValue(details.getLovValue()+"-"+details.getCcyDesc());
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

	public Collateralitem getCollateralitem() {
		return this.collateralitem;
	}

	public void setCollateralitem(Collateralitem collateralitem) {
		this.collateralitem = collateralitem;
	}

	public void setCollateralitemService(CollateralitemService collateralitemService) {
		this.collateralitemService = collateralitemService;
	}

	public CollateralitemService getCollateralitemService() {
		return this.collateralitemService;
	}

	public void setCollateralitemListCtrl(CollateralitemListCtrl collateralitemListCtrl) {
		this.collateralitemListCtrl = collateralitemListCtrl;
	}

	public CollateralitemListCtrl getCollateralitemListCtrl() {
		return this.collateralitemListCtrl;
	}

	public PagedListService getPagedListService() {
		return pagedListService;
	}

	public void setPagedListService(PagedListService pagedListService) {
		this.pagedListService = pagedListService;
	}


	private AuditHeader getAuditHeader(Collateralitem aCollateralitem, String tranType){
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aCollateralitem.getBefImage(), aCollateralitem);   
		return new AuditHeader(aCollateralitem.getHYCUS(),null,null,null,auditDetail,aCollateralitem.getUserDetails(),getOverideMap());
	}

	private void showMessage(Exception e){
		AuditHeader auditHeader= new AuditHeader();
		try {
			auditHeader.setErrorDetails(new ErrorDetails(PennantConstants.ERR_UNDEF,e.getMessage(),null));
			ErrorControl.showErrorControl(this.window_CollateralitemDialog, auditHeader);
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
		this.lovDescHYCCYName.setConstraint("NO EMPTY:" + Labels.getLabel("FIELD_NO_EMPTY",new String[]{Labels.getLabel("label_CollateralitemDialog_HYCCY.value")}));
	}
	private void doRemoveLOVValidation() {
		this.lovDescHYCCYName.setConstraint("");
	}
	
	
	private void doBasicValidation() {

		if(StringUtils.trimToNull(this.hYCLR.getValue()) == null && 
				(StringUtils.trimToNull(this.hYDLP.getValue()) == null || StringUtils.trimToNull(this.hYDBNM.getValue())== null || StringUtils.trimToNull(this.hYDLR.getValue()) == null) &&
				(StringUtils.trimToNull(this.hYAB.getValue()) == null || StringUtils.trimToNull(this.hYAN.getValue()) == null || StringUtils.trimToNull(this.hYAS.getValue()) == null)
		) { 
			try {
				PTMessageUtils.showErrorMessage("Please enter valid Collateral Reference (or) Deal (or) Account Number.");
			}catch (Exception e) {
				e.printStackTrace();
			}
		} 
	} 


	private Notes getNotes(){
		Notes notes = new Notes();
		notes.setModuleName("Collateralitem");
		notes.setReference(getCollateralitem().getHYCUS());
		notes.setVersion(getCollateralitem().getVersion());
		return notes;
	}

	private void doClearMessage() {
		logger.debug("Entering");
		this.hYCUS.setErrorMessage("");
		this.hYCLC.setErrorMessage("");
		this.hYDLP.setErrorMessage("");
		this.hYDLR.setErrorMessage("");
		this.hYDBNM.setErrorMessage("");
		this.hYAB.setErrorMessage("");
		this.hYAN.setErrorMessage("");
		this.hYAS.setErrorMessage("");
		this.hYCLP.setErrorMessage("");
		this.hYCLR.setErrorMessage("");
		this.hYCNA.setErrorMessage("");
		this.hYCLO.setErrorMessage("");
		this.hYDPC.setErrorMessage("");
		this.hYCXD.setErrorMessage("");
		this.hYLRD.setErrorMessage("");
		this.hYFRQ.setErrorMessage("");
		this.hYNRD.setErrorMessage("");
		this.hYNOU.setErrorMessage("");
		this.hYUNP.setErrorMessage("");
		this.lovDescHYCCYName.setErrorMessage("");
		this.hYCLV.setErrorMessage("");
		this.hYSVM.setErrorMessage("");
		this.hYMCV.setErrorMessage("");
		this.hYBKV.setErrorMessage("");
		this.hYTOTA.setErrorMessage("");
		this.hYISV.setErrorMessage("");
		this.hYIXD.setErrorMessage("");
		this.hYNR1.setErrorMessage("");
		this.hYNR2.setErrorMessage("");
		this.hYNR3.setErrorMessage("");
		this.hYNR4.setErrorMessage("");
		this.hYDLM.setErrorMessage("");
		logger.debug("Leaving");
	}


	private void refreshList(){
		final JdbcSearchObject<Collateralitem> soCollateralitem = getCollateralitemListCtrl().getSearchObj();
		getCollateralitemListCtrl().pagingCollateralitemList.setActivePage(0);
		getCollateralitemListCtrl().getPagedListWrapper().setSearchObject(soCollateralitem);
		if(getCollateralitemListCtrl().listBoxCollateralitem!=null){
			getCollateralitemListCtrl().listBoxCollateralitem.getListModel();
		}
	} 

	public void setOverideMap(HashMap<String, ArrayList<ErrorDetails>> overideMap) {
		this.overideMap = overideMap;
	}

	public HashMap<String, ArrayList<ErrorDetails>> getOverideMap() {
		return overideMap;
	}

	public Collateralitem getPrvCollateralitem() {
		return prvCollateralitem;
	}


	/**
	 * Method for Calling list Of existed Customers
	 * @param event
	 * @throws SuspendNotAllowedException
	 * @throws InterruptedException
	 */
	public void onClick$btnSearchPRCustid(Event event) throws SuspendNotAllowedException, InterruptedException{
		logger.debug("Entering" + event.toString());
		onload();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * To load the customerSelect filter dialog
	 * @throws SuspendNotAllowedException
	 * @throws InterruptedException
	 */
	private void onload() throws SuspendNotAllowedException, InterruptedException{
		logger.debug("Entering");
		final HashMap<String, Object> map = new HashMap<String, Object>();	
		map.put("DialogCtrl", this);
		map.put("filtertype","Extended");
		map.put("searchObject",this.newSearchObject);
		Executions.createComponents("/WEB-INF/pages/CustomerMasters/Customer/CustomerSelect.zul",null,map);
		logger.debug("Leaving");
	}

	/**
	 * To set the customer id from Customer filter
	 * @param nCustomer
	 * @throws InterruptedException
	 */
	public void doSetCustomer(Object nCustomer,JdbcSearchObject<Customer> newSearchObject) throws InterruptedException{
		logger.debug("Entering"); 
		final Customer aCustomer = (Customer)nCustomer; 		
		this.custID.setValue(aCustomer.getCustID());
		this.hYCUS.setValue(aCustomer.getCustCIF().trim());
		this.custShrtName.setValue(aCustomer.getCustShrtName());
		this.newSearchObject = newSearchObject;
		logger.debug("Leaving");
	}

	public void onClick$btnSearchCollateralCountry(Event event) {
		logger.debug("Entering" + event.toString());
		Object dataObject = ExtendedSearchListBox.show(
				this.window_CollateralitemDialog, "Country");
		if (dataObject instanceof String) {
			this.hYCNA.setValue(dataObject.toString());
			this.lovDescCollateralCountryName.setValue("");
		} else {
			Country details = (Country) dataObject;
			if (details != null) {
				this.hYCNA.setValue(details.getLovValue());
				this.lovDescCollateralCountryName.setValue(details.getLovValue()
						+ "-" + details.getCountryDesc());
			}
		}
		logger.debug("Leaving" + event.toString());
	}
	
	
	public void onClick$btnSearchCollateralType(Event event) {
		logger.debug("Entering" + event.toString());
		Object dataObject = ExtendedSearchListBox.show(
				this.window_CollateralitemDialog, "CollateralType");
		if (dataObject instanceof String) {
			this.hYCLP.setValue(dataObject.toString());
			this.lovDescCollateralTypeName.setValue("");
		} else {
			CollateralType details = (CollateralType) dataObject;
			if (details != null) {
				this.hYCLP.setValue(details.getLovValue());
				this.hYBKV.setValue(PennantAppUtil.amountFormate(details.getHWBVM(),0));
				this.lovDescCollateralTypeName.setValue(details.getLovValue() + "-" + details.getHWCPD());
			}
		}
		logger.debug("Leaving" + event.toString());
	}

	public void onClick$btnSearchCollateralDept(Event event) {
		logger.debug("Entering");
		Object dataObject = ExtendedSearchListBox.show(this.window_CollateralitemDialog, "Department");
		if (dataObject instanceof String) {
			this.hYDPC.setValue(dataObject.toString());
			this.lovDescCollateralDeptName.setValue("");
		} else {
			Department details = (Department) dataObject;
			if (details != null) {
				this.hYDPC.setValue(details.getDeptCode());
				this.lovDescCollateralDeptName.setValue(details.getDeptCode()
						+ "-" + details.getDeptDesc());
			}
		}
		logger.debug("Leaving");
	}
	
	public void onClick$btnSearchCollateralLocation(Event event) {
		logger.debug("Entering");
		Object dataObject = ExtendedSearchListBox.show(this.window_CollateralitemDialog, "CollateralLocation");
		if (dataObject instanceof String) {
			this.hYCLO.setValue(dataObject.toString());
			this.lovDescCollateralLocationName.setValue("");
		} else {
			CollateralLocation details = (CollateralLocation) dataObject;
			if (details != null) {
				this.hYCLO.setValue(details.getHZCLO());
				this.lovDescCollateralLocationName.setValue(details.getHZCLO()
						+ "-" + details.getHZCLC());
			}
		}
		logger.debug("Leaving");
	}
	
	
	
	// Default Frequency Code comboBox change
	public void onSelect$hYFRQCode(Event event) {
		logger.debug("Entering" + event.toString());
		String frqCode = getComboboxValue(this.hYFRQCode);
		onSelectFrqCode(frqCode, this.hYFRQCode, this.hYFRQMth, this.hYFRQDay, this.hYFRQ, false);
		logger.debug("Leaving" + event.toString());
	}

	public void onSelect$hYFRQMth(Event event) {
		logger.debug("Entering" + event.toString());
		String frqCode = getComboboxValue(this.hYFRQCode);
		String frqMth = getComboboxValue(this.hYFRQMth);
		onSelectFrqMth(frqCode, frqMth, this.hYFRQMth, this.hYFRQDay, this.hYFRQ, false);
		logger.debug("Leaving" + event.toString());
	}

	public void onSelect$hYFRQDay(Event event) {
		logger.debug("Entering" + event.toString());
 		onSelectFrqDay(hYFRQCode, hYFRQMth, hYFRQDay, this.hYFRQ);
		//this.nextGrcPftDate.setText("");
		logger.debug("Leaving" + event.toString());
	}
 
	
	public void onChange$hYNOU(Event event) {
		logger.debug("Entering" + event.toString());
		BigDecimal price = this.hYUNP.getValue();
		BigDecimal  units= this.hYNOU.getValue();
		
		this.hYCLV.setValue(units.multiply(price));
		
		logger.debug("Leaving" + event.toString());
	}
	
	public void onChange$hYUNP(Event event) {
		logger.debug("Entering" + event.toString());
		BigDecimal price = this.hYUNP.getValue();
		BigDecimal  units= this.hYNOU.getValue();
		
		this.hYCLV.setValue(units.multiply(price));
		
		logger.debug("Leaving" + event.toString());
	}
}
