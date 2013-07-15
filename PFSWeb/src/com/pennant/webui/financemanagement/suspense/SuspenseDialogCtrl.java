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
 * FileName    		:  SuspenseDialogCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  31-05-2012    														*
 *                                                                  						*
 * Modified Date    :  31-05-2012    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 31-05-2012       Pennant	                 0.1                                            * 
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

package com.pennant.webui.financemanagement.suspense;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Longbox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.finance.FinanceSuspHead;
import com.pennant.backend.service.financemanagement.SuspenseService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.util.PennantAppUtil;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.MultiLineMessageBox;
import com.pennant.webui.util.PTMessageUtils;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * /WEB-INF/pages/FinanceManagement/Suspense/SusoenseDialog.zul file. <br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 */
public class SuspenseDialogCtrl extends GFCBaseCtrl implements Serializable {

	private static final long serialVersionUID = 7798200490595650451L;
	private final static Logger logger = Logger.getLogger(SuspenseDialogCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window 		window_SuspenseDialog; 	// autowired
	protected Textbox 		finReference; 			// autowired
	protected Textbox 		finBranch; 				// autowired
	protected Textbox 		finType; 				// autowired
	protected Longbox 		custID; 				// autowired
	protected Textbox 		lovDescCustCIF; 		// autowired
	protected Label   		custShrtName;			// autowired
	protected Intbox   		finSuspSeq;				// autowired
	protected Checkbox 		finIsInSusp; 			// autowired
	protected Checkbox 		manualSusp; 			// autowired
	protected Decimalbox 	finSuspAmt; 			// autowired
	protected Decimalbox 	finCurSuspAmt; 			// autowired
	protected Datebox 		finSuspDate; 			// autowired

	// not auto wired vars
	private FinanceSuspHead suspHead; // overhanded per param
	private transient SuspenseListCtrl suspenseListCtrl; // overhanded per param

	// old value vars for edit mode. that we can check if something
	// on the values are edited since the last init.
	private transient boolean  		oldVar_manualSusp;

	// Button controller for the CRUD buttons
	protected Button btnSave; // autowire
	protected Button btnClose; // autowire
	protected Button btnHelp; // autowire

	// ServiceDAOs / Domain Classes
	private transient SuspenseService suspenseService;

	/**
	 * default constructor.<br>
	 */
	public SuspenseDialogCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * zul-file is called with a parameter for a selected Suspense object in a
	 * Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_SuspenseDialog(Event event) throws Exception {
		logger.debug(event.toString());

		// get the params map that are overhanded by creation.
		final Map<String, Object> args = getCreationArgsMap(event);

		// READ OVERHANDED params !
		if (args.containsKey("suspHead")) {
			this.suspHead = (FinanceSuspHead) args.get("suspHead");
			setSuspHead(this.suspHead);
		} else {
			setSuspHead(null);
		}

		// READ OVERHANDED params !
		// we get the provisionListWindow controller. So we have access
		// to it and can synchronize the shown data when we do insert, edit or
		// delete provision here.
		if (args.containsKey("suspenseListCtrl")) {
			setSuspenseListCtrl((SuspenseListCtrl) args.get("suspenseListCtrl"));
		} else {
			setSuspenseListCtrl(null);
		}

		// set Field Properties
		doSetFieldProperties();
		doShowDialog(getSuspHead());
		logger.debug("Leaving");
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering") ;
		//Empty sent any required attributes
		this.finReference.setMaxlength(20);
		this.finBranch.setMaxlength(8);
		this.finType.setMaxlength(8);
		this.custID.setMaxlength(19);
		this.finSuspAmt.setMaxlength(18);
		this.finSuspAmt.setFormat(PennantAppUtil.getAmountFormate(getSuspHead().getLovDescFinFormatter()));
		this.finCurSuspAmt.setMaxlength(18);
		this.finCurSuspAmt.setFormat(PennantAppUtil.getAmountFormate(getSuspHead().getLovDescFinFormatter()));
		this.finSuspDate.setFormat(PennantConstants.dateFormat);

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
	public void onClose$window_SuspenseDialog(Event event) throws Exception {
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
	 * when the "help" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnHelp(Event event) throws InterruptedException {
		logger.debug(event.toString());
		PTMessageUtils.showHelpWindow(event, window_SuspenseDialog);
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
			closeDialog(this.window_SuspenseDialog, "Suspense");	
		}

		logger.debug("Leaving") ;
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aSuspense
	 *            Suspense
	 */
	public void doWriteBeanToComponents(FinanceSuspHead aSuspHead) {
		logger.debug("Entering") ;
		this.finReference.setValue(aSuspHead.getFinReference());
		this.finBranch.setValue(aSuspHead.getFinBranch());
		this.finType.setValue(aSuspHead.getFinType());
		this.custID.setValue(aSuspHead.getCustId());
		this.lovDescCustCIF.setValue(aSuspHead.getLovDescCustCIFName());
		this.custShrtName.setValue(aSuspHead.getLovDescCustShrtName());
		this.finSuspSeq.setValue(aSuspHead.getFinSuspSeq());
		this.finIsInSusp.setChecked(aSuspHead.isFinIsInSusp());
		this.manualSusp.setChecked(aSuspHead.isManualSusp());
		this.finSuspAmt.setValue(PennantAppUtil.formateAmount(aSuspHead.getFinSuspAmt(),
				aSuspHead.getLovDescFinFormatter()));
		this.finCurSuspAmt.setValue(PennantAppUtil.formateAmount(aSuspHead.getFinCurSuspAmt(),
				aSuspHead.getLovDescFinFormatter()));
		this.finSuspDate.setValue(aSuspHead.getFinSuspDate());
		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aSuspHead
	 */
	public void doWriteComponentsToBean(FinanceSuspHead aSuspHead) {
		logger.debug("Entering") ;

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		try {
			aSuspHead.setFinReference(this.finReference.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aSuspHead.setFinBranch(this.finBranch.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aSuspHead.setFinType(this.finType.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aSuspHead.setCustId(this.custID.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aSuspHead.setFinIsInSusp(this.finIsInSusp.isChecked());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aSuspHead.setManualSusp(this.manualSusp.isChecked());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			if(this.finSuspAmt.getValue()!=null){
				aSuspHead.setFinSuspAmt(PennantAppUtil.unFormateAmount(this.finSuspAmt.getValue(),
						aSuspHead.getLovDescFinFormatter()));
			}
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			if(this.finCurSuspAmt.getValue()!=null){
				aSuspHead.setFinCurSuspAmt(PennantAppUtil.unFormateAmount(this.finCurSuspAmt.getValue(), 
						aSuspHead.getLovDescFinFormatter()));
			}
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		
		try {
			aSuspHead.setFinSuspDate(this.finSuspDate.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aSuspHead.setFinSuspSeq(this.finSuspSeq.intValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}

		logger.debug("Leaving");
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the
	 * readOnly mode accordingly.
	 * 
	 * @param aSuspHead
	 * @throws InterruptedException
	 */
	public void doShowDialog(FinanceSuspHead aSuspHead) throws InterruptedException {
		logger.debug("Entering") ;

		// if aSuspense == null then we opened the Dialog without
		// args for a given entity, so we get a new Obj().
		if (aSuspHead == null) {
			/** !!! DO NOT BREAK THE TIERS !!! */
			// We don't create a new DomainObject() in the frontend.
			// We GET it from the backend.
			aSuspHead = getSuspenseService().getNewFinanceSuspHead();
			setSuspHead(aSuspHead);
		} else {
			setSuspHead(aSuspHead);
		}

		try {
			// fill the components with the data
			doWriteBeanToComponents(aSuspHead);

			// stores the initial data for comparing if they are changed
			// during user action.
			doStoreInitValues();
			setDialog(this.window_SuspenseDialog);
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
		this.oldVar_manualSusp = this.manualSusp.isChecked();
		logger.debug("Leaving") ;
	}

	/**
	 * Checks, if data are changed since the last call of <br>
	 * doStoreInitData() . <br>
	 * 
	 * @return true, if data are changed, otherwise false
	 */
	private boolean isDataChanged() {
		if (this.oldVar_manualSusp != this.manualSusp.isChecked()) {
			return true;
		}
		return false;
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// +++++++++++++++++++++++++ crud operations +++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug("Entering");
		final FinanceSuspHead suspHead = new FinanceSuspHead();
		BeanUtils.copyProperties(getSuspHead(), suspHead);
		//boolean isNew = false;

		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		// force validation, if on, than execute by component.getValue()
		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		// fill the Suspense object with the components data
		doWriteComponentsToBean(suspHead);
		
		if(isDataChanged()){
			getSuspenseService().updateSuspense(suspHead);
			refreshList();
			closeDialog(this.window_SuspenseDialog, "Suspense");	
		}
		
		logger.debug("Leaving");
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	private void refreshList(){
		final JdbcSearchObject<FinanceSuspHead> soSuspense = getSuspenseListCtrl().getSearchObj();
		getSuspenseListCtrl().pagingSuspenseList.setActivePage(0);
		getSuspenseListCtrl().getPagedListWrapper().setSearchObject(soSuspense);
		if(getSuspenseListCtrl().listBoxSuspense!=null){
			getSuspenseListCtrl().listBoxSuspense.getListModel();
		}
	} 
	
	public SuspenseListCtrl getSuspenseListCtrl() {
		return suspenseListCtrl;
	}
	public void setSuspenseListCtrl(SuspenseListCtrl suspenseListCtrl) {
		this.suspenseListCtrl = suspenseListCtrl;
	}

	public SuspenseService getSuspenseService() {
		return suspenseService;
	}
	public void setSuspenseService(SuspenseService suspenseService) {
		this.suspenseService = suspenseService;
	}

	public FinanceSuspHead getSuspHead() {
		return suspHead;
	}
	public void setSuspHead(FinanceSuspHead suspHead) {
		this.suspHead = suspHead;
	}
	
}
