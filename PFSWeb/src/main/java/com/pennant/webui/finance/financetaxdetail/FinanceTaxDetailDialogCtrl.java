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
 * FileName    		:  FinanceTaxDetailDialogCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  17-06-2017    														*
 *                                                                  						*
 * Modified Date    :  17-06-2017    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 17-06-2017       PENNANT	                 0.1                                            * 
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
package com.pennant.webui.finance.financetaxdetail;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataAccessException;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.ExtendedCombobox;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.finance.financetaxdetail.FinanceTaxDetail;
import com.pennant.backend.service.finance.FinanceTaxDetailService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.search.Filter;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.util.Constraint.StaticListValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.MessageUtil;
import com.pennanttech.pff.core.Literal;
	

/**
 * This is the controller class for the
 * /WEB-INF/pages/tax/FinanceTaxDetail/financeTaxDetailDialog.zul file. <br>
 */
public class FinanceTaxDetailDialogCtrl extends GFCBaseCtrl<FinanceTaxDetail>{

	private static final long serialVersionUID = 1L;
	private final static Logger logger = Logger.getLogger(FinanceTaxDetailDialogCtrl.class);
	
	/*
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting  by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_FinanceTaxDetailDialog; 
	protected Textbox 		finReference; 
 	protected Combobox 		applicableFor; 
  protected Checkbox 		taxExempted; 
	protected Textbox 		taxNumber; 
	protected Textbox 		addrLine1; 
	protected Textbox 		addrLine2; 
	protected Textbox 		addrLine3; 
	protected Textbox 		addrLine4; 
    protected ExtendedCombobox 		country; 
    protected ExtendedCombobox 		province; 
    protected ExtendedCombobox 		city; 
    protected ExtendedCombobox 		pinCode; 
	private FinanceTaxDetail financeTaxDetail; // overhanded per param

	private transient FinanceTaxDetailListCtrl financeTaxDetailListCtrl; // overhanded per param
	private transient FinanceTaxDetailService financeTaxDetailService;
	
	private List<ValueLabel> listApplicableFor=PennantStaticListUtil.getRecAgainstTypes();

	/**
	 * default constructor.<br>
	 */
	public FinanceTaxDetailDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "FinanceTaxDetailDialog";
	}
	
	@Override
	protected String getReference() {
		StringBuffer referenceBuffer= new StringBuffer(this.financeTaxDetail.getFinReference());
		return referenceBuffer.toString();
	}

	
	/**
	 * 
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 * @throws Exception
	 */
	public void onCreate$window_FinanceTaxDetailDialog(Event event) throws Exception {
		logger.debug(Literal.ENTERING);
		
		// Set the page level components.
		setPageComponents(window_FinanceTaxDetailDialog);

		
		try {
			// Get the required arguments.
			this.financeTaxDetail = (FinanceTaxDetail) arguments.get("financeTaxDetail");
			this.financeTaxDetailListCtrl = (FinanceTaxDetailListCtrl) arguments.get("financeTaxDetailListCtrl");

			if (this.financeTaxDetail == null) {
				throw new Exception(Labels.getLabel("error.unhandled"));
			}

			// Store the before image.
			FinanceTaxDetail financeTaxDetail = new FinanceTaxDetail();
			BeanUtils.copyProperties(this.financeTaxDetail, financeTaxDetail);
			this.financeTaxDetail.setBefImage(financeTaxDetail);
			
			// Render the page and display the data.
			doLoadWorkFlow(this.financeTaxDetail.isWorkflow(), this.financeTaxDetail.getWorkflowId(),
					this.financeTaxDetail.getNextTaskId());

			if (isWorkFlowEnabled()) {
				if(!enqiryModule){
					this.userAction = setListRecordStatus(this.userAction);
				}
				getUserWorkspace().allocateAuthorities(this.pageRightName,getRole());
			}else{
				getUserWorkspace().allocateAuthorities(this.pageRightName,null);
			}

			doSetFieldProperties();
			doCheckRights();
			doShowDialog(this.financeTaxDetail);
		} catch (Exception e) {
			closeDialog();
			MessageUtil.showError(e);
		}
		
		logger.debug(Literal.LEAVING);
	}


	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug(Literal.ENTERING);
		
			this.finReference.setMaxlength(20);
			this.taxNumber.setMaxlength(100);
			this.addrLine1.setMaxlength(100);
			this.addrLine2.setMaxlength(100);
			this.addrLine3.setMaxlength(100);
			this.addrLine4.setMaxlength(100);
			this.country.setModuleName("Currency");
			this.country.setValueColumn("country");
			this.country.setDescColumn("countryName");
			this.country.setValidateColumns(new String[] {"country"});
			Filter[] filters0 = new Filter[1] ;
			this.country.setFilters(filters0);
			this.province.setModuleName("Academic");
			this.province.setValueColumn("province");
			this.province.setDescColumn("provinceName");
			this.province.setValidateColumns(new String[] {"province"});
			Filter[] filters1 = new Filter[1] ;
			this.province.setFilters(filters1);
			this.city.setModuleName("Academic");
			this.city.setValueColumn("city");
			this.city.setDescColumn("cityName");
			this.city.setValidateColumns(new String[] {"city"});
			Filter[] filters2 = new Filter[1] ;
			this.city.setFilters(filters2);
			this.pinCode.setModuleName("Academic");
			this.pinCode.setValueColumn("pinCode");
			this.pinCode.setDescColumn("pinCodeName");
			this.pinCode.setValidateColumns(new String[] {"pinCode"});
			Filter[] filters3 = new Filter[1] ;
			this.pinCode.setFilters(filters3);
		
		setStatusDetails();
		
		logger.debug(Literal.LEAVING);
	}
	
	/**
	 * Set Visible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug(Literal.ENTERING);

		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_FinanceTaxDetailDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_FinanceTaxDetailDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_FinanceTaxDetailDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_FinanceTaxDetailDialog_btnSave"));
		this.btnCancel.setVisible(false);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the save button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$btnSave(Event event) {
		logger.debug(Literal.ENTERING);
		doSave();
		logger.debug(Literal.LEAVING);
		
	}

	/**
	 * The framework calls this event handler when user clicks the edit button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$btnEdit(Event event) {
		logger.debug(Literal.ENTERING);
		doEdit();
		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the help button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$btnHelp(Event event) {
		logger.debug(Literal.ENTERING);
		MessageUtil.showHelpWindow(event, super.window);
		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the delete button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$btnDelete(Event event)  throws InterruptedException {
		logger.debug(Literal.ENTERING);
		doDelete();
		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the cancel button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$btnCancel(Event event) {
		logger.debug(Literal.ENTERING);
		doCancel();
		logger.debug(Literal.LEAVING);
	}

	/**
	 * The Click event is raised when the Close Button control is clicked.
	 * 
	 * @param event
	 *            An event sent to the event handler of a component.
	 */
	public void onClick$btnClose(Event event) {
		logger.debug(Literal.ENTERING);
		doClose(this.btnSave.isVisible());
		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the notes button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$btnNotes(Event event) {
		logger.debug(Literal.ENTERING);
		doShowNotes(this.financeTaxDetail);
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Refresh the list page with the filters that are applied in list page.
	 */
	private void refreshList() {
		logger.debug(Literal.ENTERING);
		financeTaxDetailListCtrl.search();
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Cancel the actual operation and Resets to the original status
	 */
	private void doCancel() {
		logger.debug(Literal.ENTERING);

		doWriteBeanToComponents(this.financeTaxDetail.getBefImage());
		doReadOnly();
		this.btnCtrl.setInitEdit();
		this.btnCancel.setVisible(false);

		logger.debug(Literal.LEAVING);
	}
	








      public void onFulfillCountry(Event event){
    	  logger.debug(Literal.ENTERING);
    	  
    	if(!this.country.getDescription().equals("")){
    	
    	}else{
    		
    	
    	}
    	
    	logger.debug(Literal.LEAVING);
	}	


      public void onFulfillProvince(Event event){
    	  logger.debug(Literal.ENTERING);
    	  
    	if(!this.province.getDescription().equals("")){
    	
    	}else{
    		
    	
    	}
    	
    	logger.debug(Literal.LEAVING);
	}	


      public void onFulfillCity(Event event){
    	  logger.debug(Literal.ENTERING);
    	  
    	if(!this.city.getDescription().equals("")){
    	
    	}else{
    		
    	
    	}
    	
    	logger.debug(Literal.LEAVING);
	}	


      public void onFulfillPinCode(Event event){
    	  logger.debug(Literal.ENTERING);
    	  
    	if(!this.pinCode.getDescription().equals("")){
    	
    	}else{
    		
    	
    	}
    	
    	logger.debug(Literal.LEAVING);
	}	




	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param financeTaxDetail
	 * 
	 */
	public void doWriteBeanToComponents(FinanceTaxDetail aFinanceTaxDetail) {
		logger.debug(Literal.ENTERING);
	
			this.finReference.setValue(aFinanceTaxDetail.getFinReference());
			fillComboBox(this.applicableFor, aFinanceTaxDetail.getApplicableFor(), listApplicableFor,"");
			this.taxExempted.setChecked(aFinanceTaxDetail.isTaxExempted());
			this.taxNumber.setValue(aFinanceTaxDetail.getTaxNumber());
			this.addrLine1.setValue(aFinanceTaxDetail.getAddrLine1());
			this.addrLine2.setValue(aFinanceTaxDetail.getAddrLine2());
			this.addrLine3.setValue(aFinanceTaxDetail.getAddrLine3());
			this.addrLine4.setValue(aFinanceTaxDetail.getAddrLine4());
		   this.country.setValue(aFinanceTaxDetail.getCountry());
		   this.province.setValue(aFinanceTaxDetail.getProvince());
		   this.city.setValue(aFinanceTaxDetail.getCity());
		   this.pinCode.setValue(aFinanceTaxDetail.getPinCode());
		
		if (aFinanceTaxDetail.isNewRecord()){
			   this.country.setDescription("");
			   this.province.setDescription("");
			   this.city.setDescription("");
			   this.pinCode.setDescription("");
		}else{
			   this.country.setDescription(aFinanceTaxDetail.getCountryName());
			   this.province.setDescription(aFinanceTaxDetail.getProvinceName());
			   this.city.setDescription(aFinanceTaxDetail.getCityName());
			   this.pinCode.setDescription(aFinanceTaxDetail.getPinCodeName());
		}
		
		logger.debug(Literal.LEAVING);
	}
	
	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aFinanceTaxDetail
	 */
	public void doWriteComponentsToBean(FinanceTaxDetail aFinanceTaxDetail) {
		logger.debug(Literal.LEAVING);
		
		doSetLOVValidation();
		
		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();
		
		//Finance Reference
		try {
		    aFinanceTaxDetail.setFinReference(this.finReference.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		//Applicable For
		try {
			String strApplicableFor =null; 
			if(this.applicableFor.getSelectedItem()!=null){
				strApplicableFor = this.applicableFor.getSelectedItem().getValue().toString();
			}
			if(strApplicableFor!= null && !PennantConstants.List_Select.equals(strApplicableFor)){
				aFinanceTaxDetail.setApplicableFor(strApplicableFor);
	
			}else{
				aFinanceTaxDetail.setApplicableFor(null);
			}
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		//Tax Exempted
		try {
			aFinanceTaxDetail.setTaxExempted(this.taxExempted.isChecked());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		//Tax Number
		try {
		    aFinanceTaxDetail.setTaxNumber(this.taxNumber.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		//Address Line 1
		try {
		    aFinanceTaxDetail.setAddrLine1(this.addrLine1.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		//Address Line 2
		try {
		    aFinanceTaxDetail.setAddrLine2(this.addrLine2.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		//Address Line 3
		try {
		    aFinanceTaxDetail.setAddrLine3(this.addrLine3.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		//Address Line 4
		try {
		    aFinanceTaxDetail.setAddrLine4(this.addrLine4.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		//Country
		try {
			aFinanceTaxDetail.setCountry(this.country.getValidatedValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		//Province
		try {
			aFinanceTaxDetail.setProvince(this.province.getValidatedValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		//City
		try {
			aFinanceTaxDetail.setCity(this.city.getValidatedValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		//Pin Code
		try {
			aFinanceTaxDetail.setPinCode(this.pinCode.getValidatedValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		
		doRemoveValidation();
		doRemoveLOVValidation();
		
		if (!wve.isEmpty()) {
			WrongValueException [] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = (WrongValueException) wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}
		
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Displays the dialog page.
	 * 
	 * @param financeTaxDetail
	 *            The entity that need to be render.
	 */
	public void doShowDialog(FinanceTaxDetail financeTaxDetail) {
		logger.debug(Literal.LEAVING);

		if (financeTaxDetail.isNew()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.finReference.focus();
		} else {
				this.finReference.setReadonly(true);

			if (isWorkFlowEnabled()) {
				if (StringUtils.isNotBlank(financeTaxDetail.getRecordType())) {
					this.btnNotes.setVisible(true);
				}
				// setFocus
				this.applicableFor.focus();
				doEdit();
			} else {
				this.btnCtrl.setInitEdit();
				doReadOnly();
				btnCancel.setVisible(false);
			}
		}

		if (enqiryModule) {
			this.btnCtrl.setBtnStatus_Enquiry();
			this.btnNotes.setVisible(false);
		}

		doWriteBeanToComponents(financeTaxDetail);
		setDialog(DialogType.EMBEDDED);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug(Literal.LEAVING);

		if (!this.finReference.isReadonly()){
			this.finReference.setConstraint(new PTStringValidator(Labels.getLabel("label_FinanceTaxDetailDialog_FinReference.value"),PennantRegularExpressions.REGEX_NAME,false));
		}
		if (!this.applicableFor.isReadonly()){
			this.applicableFor.setConstraint(new StaticListValidator(listApplicableFor,Labels.getLabel("label_FinanceTaxDetailDialog_ApplicableFor.value")));
		}
		if (!this.taxNumber.isReadonly()){
			this.taxNumber.setConstraint(new PTStringValidator(Labels.getLabel("label_FinanceTaxDetailDialog_TaxNumber.value"),PennantRegularExpressions.REGEX_NAME,false));
		}
		if (!this.addrLine1.isReadonly()){
			this.addrLine1.setConstraint(new PTStringValidator(Labels.getLabel("label_FinanceTaxDetailDialog_AddrLine1.value"),PennantRegularExpressions.REGEX_NAME,false));
		}
		if (!this.addrLine2.isReadonly()){
			this.addrLine2.setConstraint(new PTStringValidator(Labels.getLabel("label_FinanceTaxDetailDialog_AddrLine2.value"),PennantRegularExpressions.REGEX_NAME,false));
		}
		if (!this.addrLine3.isReadonly()){
			this.addrLine3.setConstraint(new PTStringValidator(Labels.getLabel("label_FinanceTaxDetailDialog_AddrLine3.value"),PennantRegularExpressions.REGEX_NAME,false));
		}
		if (!this.addrLine4.isReadonly()){
			this.addrLine4.setConstraint(new PTStringValidator(Labels.getLabel("label_FinanceTaxDetailDialog_AddrLine4.value"),PennantRegularExpressions.REGEX_NAME,false));
		}
		if (!this.country.isReadonly()){
			this.country.setConstraint(new PTStringValidator(Labels.getLabel("label_FinanceTaxDetailDialog_Country.value"),PennantRegularExpressions.REGEX_NAME,false));
		}
		if (!this.province.isReadonly()){
			this.province.setConstraint(new PTStringValidator(Labels.getLabel("label_FinanceTaxDetailDialog_Province.value"),PennantRegularExpressions.REGEX_NAME,false));
		}
		if (!this.city.isReadonly()){
			this.city.setConstraint(new PTStringValidator(Labels.getLabel("label_FinanceTaxDetailDialog_City.value"),PennantRegularExpressions.REGEX_NAME,false));
		}
		if (!this.pinCode.isReadonly()){
			this.pinCode.setConstraint(new PTStringValidator(Labels.getLabel("label_FinanceTaxDetailDialog_PinCode.value"),PennantRegularExpressions.REGEX_NAME,false));
		}
	
		logger.debug(Literal.LEAVING);
	}
	
	/**
	 * Remove the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug(Literal.LEAVING);
		
		this.finReference.setConstraint("");
		this.applicableFor.setConstraint("");
		this.taxNumber.setConstraint("");
		this.addrLine1.setConstraint("");
		this.addrLine2.setConstraint("");
		this.addrLine3.setConstraint("");
		this.addrLine4.setConstraint("");
		this.country.setConstraint("");
		this.province.setConstraint("");
		this.city.setConstraint("");
		this.pinCode.setConstraint("");
	
	logger.debug(Literal.LEAVING);
	}


	/**
	 * Set Validations for LOV Fields
	 */

	private void doSetLOVValidation() {
		logger.debug(Literal.LEAVING);
		
		//Finance Reference
		//Applicable For
		//Tax Exempted
		//Tax Number
		//Address Line 1
		//Address Line 2
		//Address Line 3
		//Address Line 4
		//Country
		//Province
		//City
		//Pin Code
		
		logger.debug(Literal.LEAVING);
	}
	
	/**
	 * Remove the Validation by setting empty constraints.
	 */

	private void doRemoveLOVValidation() {
		logger.debug(Literal.LEAVING);
		
		
		logger.debug(Literal.LEAVING);
	}
	
	/**
	 * Clears validation error messages from all the fields of the dialog controller.
	 */
	@Override
	protected void doClearMessage() {
		logger.debug(Literal.LEAVING);
		
	
	logger.debug(Literal.LEAVING);
	}

	/**
	 * Deletes a FinanceTaxDetail object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug(Literal.LEAVING);
		
		final FinanceTaxDetail aFinanceTaxDetail = new FinanceTaxDetail();
		BeanUtils.copyProperties(this.financeTaxDetail, aFinanceTaxDetail);
		String tranType=PennantConstants.TRAN_WF;
		
		// Show a confirm box
		final String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record") + "\n\n --> " + aFinanceTaxDetail.getFinReference();
		if (MessageUtil.confirm(msg) == MessageUtil.YES) {
			if (StringUtils.trimToEmpty(aFinanceTaxDetail.getRecordType()).equals("")){
				aFinanceTaxDetail.setVersion(aFinanceTaxDetail.getVersion()+1);
				aFinanceTaxDetail.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				
				if (isWorkFlowEnabled()){
					aFinanceTaxDetail.setRecordStatus(userAction.getSelectedItem().getValue().toString());
					aFinanceTaxDetail.setNewRecord(true);
					tranType=PennantConstants.TRAN_WF;
					getWorkFlowDetails(userAction.getSelectedItem().getLabel(), aFinanceTaxDetail.getNextTaskId(), aFinanceTaxDetail);
				}else{
					tranType=PennantConstants.TRAN_DEL;
				}
			}

			try {
				if(doProcess(aFinanceTaxDetail,tranType)){
					refreshList();
					closeDialog(); 
				}

			}catch (DataAccessException e){
				MessageUtil.showError(e);
			}
		}
		
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug(Literal.LEAVING);
		
		if (this.financeTaxDetail.isNewRecord()) {
			this.btnCancel.setVisible(false);
			readOnlyComponent(false, this.finReference);
		} else {
			this.btnCancel.setVisible(true);
			readOnlyComponent(true, this.finReference);
			
		}
	
			readOnlyComponent(isReadOnly("FinanceTaxDetailDialog_ApplicableFor"), this.applicableFor);
			readOnlyComponent(isReadOnly("FinanceTaxDetailDialog_TaxExempted"), this.taxExempted);
			readOnlyComponent(isReadOnly("FinanceTaxDetailDialog_TaxNumber"), this.taxNumber);
			readOnlyComponent(isReadOnly("FinanceTaxDetailDialog_AddrLine1"), this.addrLine1);
			readOnlyComponent(isReadOnly("FinanceTaxDetailDialog_AddrLine2"), this.addrLine2);
			readOnlyComponent(isReadOnly("FinanceTaxDetailDialog_AddrLine3"), this.addrLine3);
			readOnlyComponent(isReadOnly("FinanceTaxDetailDialog_AddrLine4"), this.addrLine4);
			readOnlyComponent(isReadOnly("FinanceTaxDetailDialog_Country"), this.country);
			readOnlyComponent(isReadOnly("FinanceTaxDetailDialog_Province"), this.province);
			readOnlyComponent(isReadOnly("FinanceTaxDetailDialog_City"), this.city);
			readOnlyComponent(isReadOnly("FinanceTaxDetailDialog_PinCode"), this.pinCode);
			
			if (isWorkFlowEnabled()) {
				for (int i = 0; i < userAction.getItemCount(); i++) {
					userAction.getItemAtIndex(i).setDisabled(false);
				}
				if (this.financeTaxDetail.isNewRecord()) {
					this.btnCtrl.setBtnStatus_Edit();
					btnCancel.setVisible(false);
				} else {
					this.btnCtrl.setWFBtnStatus_Edit(isFirstTask());
				}
			} else {
				this.btnCtrl.setBtnStatus_Edit();
			}

			
		logger.debug(Literal.LEAVING);
	}	
			
		/**
		 * Set the components to ReadOnly. <br>
		 */
		public void doReadOnly() {
			logger.debug(Literal.LEAVING);
			
	
			readOnlyComponent(true, this.finReference);
			readOnlyComponent(true, this.applicableFor);
			readOnlyComponent(true, this.taxExempted);
			readOnlyComponent(true, this.taxNumber);
			readOnlyComponent(true, this.addrLine1);
			readOnlyComponent(true, this.addrLine2);
			readOnlyComponent(true, this.addrLine3);
			readOnlyComponent(true, this.addrLine4);
			readOnlyComponent(true, this.country);
			readOnlyComponent(true, this.province);
			readOnlyComponent(true, this.city);
			readOnlyComponent(true, this.pinCode);

			if (isWorkFlowEnabled()) {
				for (int i = 0; i < userAction.getItemCount(); i++) {
					userAction.getItemAtIndex(i).setDisabled(true);
				}
				this.recordStatus.setValue("");
				this.userAction.setSelectedIndex(0);
	
			}

			logger.debug(Literal.LEAVING);
		}

		
		/**
		 * Clears the components values. <br>
		 */
		public void doClear() {
			logger.debug("Entering");
				this.finReference.setValue("");
			 	this.applicableFor.setSelectedIndex(0);
				this.taxExempted.setChecked(false);
				this.taxNumber.setValue("");
				this.addrLine1.setValue("");
				this.addrLine2.setValue("");
				this.addrLine3.setValue("");
				this.addrLine4.setValue("");
			  	this.country.setValue("");
			  	this.country.setDescription("");
			  	this.province.setValue("");
			  	this.province.setDescription("");
			  	this.city.setValue("");
			  	this.city.setDescription("");
			  	this.pinCode.setValue("");
			  	this.pinCode.setDescription("");

			logger.debug("Leaving");
		}

		/**
		 * Saves the components to table. <br>
		 */
		public void doSave() {
			logger.debug("Entering");
			final FinanceTaxDetail aFinanceTaxDetail = new FinanceTaxDetail();
			BeanUtils.copyProperties(this.financeTaxDetail, aFinanceTaxDetail);
			boolean isNew = false;

			doSetValidation();
			doWriteComponentsToBean(aFinanceTaxDetail);

			isNew = aFinanceTaxDetail.isNew();
			String tranType = "";

			if (isWorkFlowEnabled()) {
				tranType = PennantConstants.TRAN_WF;
				if (StringUtils.isBlank(aFinanceTaxDetail.getRecordType())) {
					aFinanceTaxDetail.setVersion(aFinanceTaxDetail.getVersion() + 1);
					if (isNew) {
						aFinanceTaxDetail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
					} else {
						aFinanceTaxDetail.setRecordType(PennantConstants.RECORD_TYPE_UPD);
						aFinanceTaxDetail.setNewRecord(true);
					}
				}
			} else {
				aFinanceTaxDetail.setVersion(aFinanceTaxDetail.getVersion() + 1);
				if (isNew) {
					tranType = PennantConstants.TRAN_ADD;
				} else {
					tranType = PennantConstants.TRAN_UPD;
				}
			}

			try {
				if (doProcess(aFinanceTaxDetail, tranType)) {
					refreshList();
					closeDialog();
				}

			} catch (final DataAccessException e) {
				logger.error(e);
				MessageUtil.showError(e);
			}
			logger.debug("Leaving");
		}

		/**
		 * Set the workFlow Details List to Object
		 * 
		 * @param aAuthorizedSignatoryRepository
		 *            (AuthorizedSignatoryRepository)
		 * 
		 * @param tranType
		 *            (String)
		 * 
		 * @return boolean
		 * 
		 */
		private boolean doProcess(FinanceTaxDetail aFinanceTaxDetail, String tranType) {
			logger.debug("Entering");
			boolean processCompleted = false;
			AuditHeader auditHeader = null;
			String nextRoleCode = "";

			aFinanceTaxDetail.setLastMntBy(getUserWorkspace().getLoggedInUser().getLoginUsrID());
			aFinanceTaxDetail.setLastMntOn(new Timestamp(System.currentTimeMillis()));
			aFinanceTaxDetail.setUserDetails(getUserWorkspace().getLoggedInUser());

			if (isWorkFlowEnabled()) {
				String taskId = getTaskId(getRole());
				String nextTaskId = "";
				aFinanceTaxDetail.setRecordStatus(userAction.getSelectedItem().getValue().toString());

				if ("Save".equals(userAction.getSelectedItem().getLabel())) {
					nextTaskId = taskId + ";";
				} else {
					nextTaskId = StringUtils.trimToEmpty(aFinanceTaxDetail.getNextTaskId());

					nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
					if ("".equals(nextTaskId)) {
						nextTaskId = getNextTaskIds(taskId, aFinanceTaxDetail);
					}

					if (isNotesMandatory(taskId, aFinanceTaxDetail)) {
						if (!notesEntered) {
							MessageUtil.showError(Labels.getLabel("Notes_NotEmpty"));
							return false;
						}

					}
				}
				if (!StringUtils.isBlank(nextTaskId)) {
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

				aFinanceTaxDetail.setTaskId(taskId);
				aFinanceTaxDetail.setNextTaskId(nextTaskId);
				aFinanceTaxDetail.setRoleCode(getRole());
				aFinanceTaxDetail.setNextRoleCode(nextRoleCode);

				auditHeader = getAuditHeader(aFinanceTaxDetail, tranType);
				String operationRefs = getServiceOperations(taskId, aFinanceTaxDetail);

				if ("".equals(operationRefs)) {
					processCompleted = doSaveProcess(auditHeader, null);
				} else {
					String[] list = operationRefs.split(";");

					for (int i = 0; i < list.length; i++) {
						auditHeader = getAuditHeader(aFinanceTaxDetail, PennantConstants.TRAN_WF);
						processCompleted = doSaveProcess(auditHeader, list[i]);
						if (!processCompleted) {
							break;
						}
					}
				}
			} else {
				auditHeader = getAuditHeader(aFinanceTaxDetail, tranType);
				processCompleted = doSaveProcess(auditHeader, null);
			}

			logger.debug("Leaving");
			return processCompleted;
		}

		/**
		 * Get the result after processing DataBase Operations
		 * 
		 * @param AuditHeader
		 *            auditHeader
		 * @param method
		 *            (String)
		 * @return boolean
		 * 
		 */

		private boolean doSaveProcess(AuditHeader auditHeader, String method) {
			logger.debug("Entering");
			boolean processCompleted = false;
			int retValue = PennantConstants.porcessOVERIDE;
			FinanceTaxDetail aFinanceTaxDetail = (FinanceTaxDetail) auditHeader.getAuditDetail().getModelData();
			boolean deleteNotes = false;

			try {

				while (retValue == PennantConstants.porcessOVERIDE) {

					if (StringUtils.isBlank(method)) {
						if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {
							auditHeader = financeTaxDetailService.delete(auditHeader);
							deleteNotes = true;
						} else {
							auditHeader = financeTaxDetailService.saveOrUpdate(auditHeader);
						}

					} else {
						if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
							auditHeader = financeTaxDetailService.doApprove(auditHeader);

							if (aFinanceTaxDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
								deleteNotes = true;
							}

						} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
							auditHeader = financeTaxDetailService.doReject(auditHeader);
							if (aFinanceTaxDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
								deleteNotes = true;
							}

						} else {
							auditHeader.setErrorDetails(new ErrorDetails(PennantConstants.ERR_9999, Labels
									.getLabel("InvalidWorkFlowMethod"), null));
							retValue = ErrorControl.showErrorControl(this.window_FinanceTaxDetailDialog, auditHeader);
							return processCompleted;
						}
					}

					auditHeader = ErrorControl.showErrorDetails(this.window_FinanceTaxDetailDialog, auditHeader);
					retValue = auditHeader.getProcessStatus();

					if (retValue == PennantConstants.porcessCONTINUE) {
						processCompleted = true;

						if (deleteNotes) {
							deleteNotes(getNotes(this.financeTaxDetail), true);
						}
					}

					if (retValue == PennantConstants.porcessOVERIDE) {
						auditHeader.setOveride(true);
						auditHeader.setErrorMessage(null);
						auditHeader.setInfoMessage(null);
						auditHeader.setOverideMessage(null);
					}
				}
			} catch (InterruptedException e) {
				logger.error("Exception: ", e);
			}
			setOverideMap(auditHeader.getOverideMap());

			logger.debug("Leaving");
			return processCompleted;
		}

		/**
		 * @param aAuthorizedSignatoryRepository
		 * @param tranType
		 * @return
		 */

		private AuditHeader getAuditHeader(FinanceTaxDetail aFinanceTaxDetail, String tranType) {
			AuditDetail auditDetail = new AuditDetail(tranType, 1, aFinanceTaxDetail.getBefImage(), aFinanceTaxDetail);
			return new AuditHeader(getReference(), null, null, null, auditDetail, aFinanceTaxDetail.getUserDetails(),
					getOverideMap());
		}

		public void setFinanceTaxDetailService(FinanceTaxDetailService financeTaxDetailService) {
			this.financeTaxDetailService = financeTaxDetailService;
		}
			
}
