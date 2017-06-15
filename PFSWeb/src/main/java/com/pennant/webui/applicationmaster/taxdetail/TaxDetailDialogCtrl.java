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
 * FileName    		:  TaxDetailDialogCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  14-06-2017    														*
 *                                                                  						*
 * Modified Date    :  14-06-2017    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 14-06-2017       PENNANT	                 0.1                                            * 
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
package com.pennant.webui.applicationmaster.taxdetail;

import java.sql.Timestamp;
import java.util.ArrayList;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataAccessException;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.ExtendedCombobox;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.applicationmaster.TaxDetail;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.service.applicationmaster.TaxDetailService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.search.Filter;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.MessageUtil;
import com.pennanttech.pff.core.Literal;
	

/**
 * This is the controller class for the
 * /WEB-INF/pages/applicationmaster/TaxDetail/taxDetailDialog.zul file. <br>
 */
public class TaxDetailDialogCtrl extends GFCBaseCtrl<TaxDetail>{

	private static final long serialVersionUID = 1L;
	private final static Logger logger = Logger.getLogger(TaxDetailDialogCtrl.class);
	
	/*
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting  by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_TaxDetailDialog; 
    protected ExtendedCombobox 		country; 
    protected ExtendedCombobox 		stateCode; 
	protected ExtendedCombobox 		entityCode; 
	protected Textbox 		taxCode; 
	protected Textbox 		addressLine1; 
	protected Textbox 		addressLine2; 
	protected Textbox 		addressLine3; 
	protected Textbox 		addressLine4; 
	protected ExtendedCombobox 		pinCode; 
    protected ExtendedCombobox 		cityCode; 
	private TaxDetail taxDetail; // overhanded per param
	private transient String cityCountryTemp;


	private transient TaxDetailListCtrl taxDetailListCtrl; // overhanded per param
	private transient TaxDetailService taxDetailService;
	

	/**
	 * default constructor.<br>
	 */
	public TaxDetailDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "TaxDetailDialog";
	}
	
	@Override
	protected String getReference() {
		StringBuffer referenceBuffer= new StringBuffer(String.valueOf(this.taxDetail.getId()));
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
	public void onCreate$window_TaxDetailDialog(Event event) throws Exception {
		logger.debug(Literal.ENTERING);
		
		// Set the page level components.
		setPageComponents(window_TaxDetailDialog);

		
		try {
			// Get the required arguments.
			this.taxDetail = (TaxDetail) arguments.get("taxdetail");
			this.taxDetailListCtrl = (TaxDetailListCtrl) arguments.get("taxdetailListCtrl");

			if (this.taxDetail == null) {
				throw new Exception(Labels.getLabel("error.unhandled"));
			}

			// Store the before image.
			TaxDetail taxDetail = new TaxDetail();
			BeanUtils.copyProperties(this.taxDetail, taxDetail);
			this.taxDetail.setBefImage(taxDetail);
			
			// Render the page and display the data.
			doLoadWorkFlow(this.taxDetail.isWorkflow(), this.taxDetail.getWorkflowId(),
					this.taxDetail.getNextTaskId());

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
			doShowDialog(this.taxDetail);
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

		this.country.setMandatoryStyle(true);
		this.country.setModuleName("Country");
		this.country.setValueColumn("CountryCode");
		this.country.setDescColumn("CountryDesc");
		this.country.setValidateColumns(new String[]{"CountryCode"});


		this.stateCode.setMandatoryStyle(true);
		this.stateCode.setModuleName("Province");
		this.stateCode.setValueColumn("CPProvince");
		this.stateCode.setDescColumn("CPProvinceName");
		this.stateCode.setValidateColumns(new String[]{"CPProvince"});

		this.entityCode.setMandatoryStyle(true);
		this.entityCode.setModuleName("EntityCodes");
		this.entityCode.setValueColumn("EntityCode");
		this.entityCode.setDescColumn("EntityDesc");
		this.entityCode.setValidateColumns(new String[] {"EntityCode"});

		this.pinCode.setMandatoryStyle(true);
		this.pinCode.setModuleName("PinCode");
		this.pinCode.setValueColumn("PinCode");
		this.pinCode.setDescColumn("City");
		this.pinCode.setValidateColumns(new String[] {"PinCode"});

		this.cityCode.setMandatoryStyle(true);
		this.cityCode.setModuleName("City");
		this.cityCode.setValueColumn("PCCity");
		this.cityCode.setDescColumn("PCCityName");
		this.cityCode.setValidateColumns(new String[] {"PCCity"});

		this.entityCode.setMaxlength(8);
		this.pinCode.setMaxlength(10);
		this.taxCode.setMaxlength(15);
		this.cityCode.setMaxlength(8);
		this.country.setMaxlength(8);
		this.stateCode.setMaxlength(8);
		this.addressLine1.setMaxlength(100);
		this.addressLine2.setMaxlength(100);
		this.addressLine3.setMaxlength(100);
		this.addressLine4.setMaxlength(100);


		setStatusDetails();

		logger.debug(Literal.LEAVING);
	}
	
	/**
	 * Set Visible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug(Literal.ENTERING);

		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_TaxDetailDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_TaxDetailDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_TaxDetailDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_TaxDetailDialog_btnSave"));
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

	public void onFulfill$country(Event event){
		logger.debug("Entering" + event.toString());
		doSetProvProp();
		logger.debug("Leaving" + event.toString());

	}
	private void doSetProvProp(){
		if (!StringUtils.trimToEmpty(cityCountryTemp).equals(this.country.getValue())){
			this.stateCode.setObject("");
			this.stateCode.setValue("");
			this.stateCode.setDescription("");
		}
		cityCountryTemp = this.country.getValue();
		Filter[] filtersProvince = new Filter[1] ;
		filtersProvince[0]= new Filter("CPCountry", this.country.getValue(), Filter.OP_EQUAL);
		this.stateCode.setFilters(filtersProvince);
	}
	
	public void onFulfill$stateCode(Event event){
		logger.debug("Entering" + event.toString());
		fillModuledetails(this.stateCode.getValue());
		logger.debug("Leaving" + event.toString());

	}
	
	private void fillModuledetails(String  id) {
		logger.debug("Entering");

		if (id != null) {
			this.cityCode.setModuleName("City");
			this.cityCode.setValueColumn("PCCity");
			this.cityCode.setDescColumn("PCCityName");
			this.cityCode.setValidateColumns(new String[] {"PCCity"});
			Filter[] filters1 = new Filter[1];
			filters1[0] = new Filter("PCProvince", id, Filter.OP_EQUAL);
			this.cityCode.setFilters(filters1);
		}
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
		doShowNotes(this.taxDetail);
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Refresh the list page with the filters that are applied in list page.
	 */
	private void refreshList() {
		logger.debug(Literal.ENTERING);
		taxDetailListCtrl.search();
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Cancel the actual operation and Resets to the original status
	 */
	private void doCancel() {
		logger.debug(Literal.ENTERING);

		doWriteBeanToComponents(this.taxDetail.getBefImage());
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


      public void onFulfillStateCode(Event event){
    	  logger.debug(Literal.ENTERING);
    	  
    	if(!this.stateCode.getDescription().equals("")){
    	
    	}else{
    		
    	
    	}
    	
    	logger.debug(Literal.LEAVING);
	}	









      public void onFulfillCityCode(Event event){
    	  logger.debug(Literal.ENTERING);
    	  
    	if(!this.cityCode.getDescription().equals("")){
    	
    	}else{
    		
    	
    	}
    	
    	logger.debug(Literal.LEAVING);
	}	




	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param taxDetail
	 * 
	 */
	public void doWriteBeanToComponents(TaxDetail aTaxDetail) {
		logger.debug(Literal.ENTERING);
	
		   this.country.setValue(aTaxDetail.getCountry());
		   this.stateCode.setValue(aTaxDetail.getStateCode());
			this.entityCode.setValue(aTaxDetail.getEntityCode());
			this.taxCode.setValue(aTaxDetail.getTaxCode());
			this.addressLine1.setValue(aTaxDetail.getAddressLine1());
			this.addressLine2.setValue(aTaxDetail.getAddressLine2());
			this.addressLine3.setValue(aTaxDetail.getAddressLine3());
			this.addressLine4.setValue(aTaxDetail.getAddressLine4());
			this.pinCode.setValue(aTaxDetail.getPinCode());
		   this.cityCode.setValue(aTaxDetail.getCityCode());
		
		if (aTaxDetail.isNewRecord()){
			   this.country.setDescription("");
			   this.stateCode.setDescription("");
			   this.cityCode.setDescription("");
			   this.entityCode.setDescription("");
			   this.pinCode.setDescription("");
		}else{
			   this.country.setDescription(aTaxDetail.getCountryName());
			   this.stateCode.setDescription(aTaxDetail.getProvinceName());
			   this.cityCode.setDescription(aTaxDetail.getCityName());
			   this.entityCode.setDescription(aTaxDetail.getEntityDesc());
			   this.pinCode.setDescription(aTaxDetail.getCityCode());
		}
		this.recordStatus.setValue(aTaxDetail.getRecordStatus());
		cityCountryTemp = this.country.getValue();

		logger.debug(Literal.LEAVING);
	}
	
	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aTaxDetail
	 */
	public void doWriteComponentsToBean(TaxDetail aTaxDetail) {
		logger.debug(Literal.LEAVING);
		
		doSetLOVValidation();
		
		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		//Country
		try {
			aTaxDetail.setCountry(this.country.getValidatedValue());
			aTaxDetail.setCountryName(this.country.getDescription());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		//State Code
		try {
			aTaxDetail.setStateCode(this.stateCode.getValidatedValue());
			aTaxDetail.setProvinceName(this.stateCode.getDescription());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		//Entity Code
		try {
			aTaxDetail.setEntityCode(this.entityCode.getValidatedValue());
			aTaxDetail.setEntityDesc(this.entityCode.getDescription());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		/*try {
		    aTaxDetail.setEntityCode(this.entityCode.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}*/
		//Tax Code
		try {
			aTaxDetail.setTaxCode(this.taxCode.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		//Address Line 1
		try {
			aTaxDetail.setAddressLine1(this.addressLine1.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		//Address Line 2
		try {
			aTaxDetail.setAddressLine2(this.addressLine2.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		//Address Line 3
		try {
			aTaxDetail.setAddressLine3(this.addressLine3.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		//Address Line 4
		try {
			aTaxDetail.setAddressLine4(this.addressLine4.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		//Pin Code
		try {
			aTaxDetail.setPinCode(this.pinCode.getValidatedValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		/*try {
		    aTaxDetail.setPinCode(this.pinCode.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}*/
		//City Code
		try {
			aTaxDetail.setCityCode(this.cityCode.getValidatedValue());
			aTaxDetail.setCityName(this.cityCode.getDescription());
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
	 * @param taxDetail
	 *            The entity that need to be render.
	 */
	public void doShowDialog(TaxDetail taxDetail) {
		logger.debug(Literal.LEAVING);

		if (taxDetail.isNew()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.country.focus();
		} else {

			if (isWorkFlowEnabled()) {
				if (StringUtils.isNotBlank(taxDetail.getRecordType())) {
					this.btnNotes.setVisible(true);
				}
				// setFocus
				this.country.focus();
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

		doWriteBeanToComponents(taxDetail);
		setDialog(DialogType.EMBEDDED);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug(Literal.LEAVING);

		if (!this.country.isReadonly()){
			this.country.setConstraint(new PTStringValidator(Labels.getLabel("label_TaxDetailDialog_Country.value"),null,true,true));
		}
		if (!this.stateCode.isReadonly()){
			this.stateCode.setConstraint(new PTStringValidator(Labels.getLabel("label_TaxDetailDialog_StateCode.value"),null,true,true));
		}
		if (!this.entityCode.isReadonly()){
			this.entityCode.setConstraint(new PTStringValidator(Labels.getLabel("label_TaxDetailDialog_EntityCode.value"),null,true,true));
		}
		if (!this.taxCode.isReadonly()){
			this.taxCode.setConstraint(new PTStringValidator(Labels.getLabel("label_TaxDetailDialog_TaxCode.value"),PennantRegularExpressions.REGEX_ALPHANUM,true));
		}
		if (!this.addressLine1.isReadonly()){
			this.addressLine1.setConstraint(new PTStringValidator(Labels.getLabel("label_TaxDetailDialog_AddressLine1.value"),PennantRegularExpressions.REGEX_ADDRESS,true));
		}
		if (!this.addressLine2.isReadonly()){
			this.addressLine2.setConstraint(new PTStringValidator(Labels.getLabel("label_TaxDetailDialog_AddressLine2.value"),PennantRegularExpressions.REGEX_ADDRESS,false));
		}
		if (!this.addressLine3.isReadonly()){
			this.addressLine3.setConstraint(new PTStringValidator(Labels.getLabel("label_TaxDetailDialog_AddressLine3.value"),PennantRegularExpressions.REGEX_ADDRESS,false));
		}
		if (!this.addressLine4.isReadonly()){
			this.addressLine4.setConstraint(new PTStringValidator(Labels.getLabel("label_TaxDetailDialog_AddressLine4.value"),PennantRegularExpressions.REGEX_ADDRESS,false));
		}
		if (!this.pinCode.isReadonly()){
			this.pinCode.setConstraint(new PTStringValidator(Labels.getLabel("label_TaxDetailDialog_PinCode.value"),null,true,true));
		}
		if (!this.cityCode.isReadonly()){
			this.cityCode.setConstraint(new PTStringValidator(Labels.getLabel("label_TaxDetailDialog_CityCode.value"),null,true,true));
		}
	
		logger.debug(Literal.LEAVING);
	}
	
	/**
	 * Remove the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug(Literal.LEAVING);
		
		this.country.setConstraint("");
		this.stateCode.setConstraint("");
		this.entityCode.setConstraint("");
		this.taxCode.setConstraint("");
		this.addressLine1.setConstraint("");
		this.addressLine2.setConstraint("");
		this.addressLine3.setConstraint("");
		this.addressLine4.setConstraint("");
		this.pinCode.setConstraint("");
		this.cityCode.setConstraint("");
	
	logger.debug(Literal.LEAVING);
	}


	/**
	 * Set Validations for LOV Fields
	 */

	private void doSetLOVValidation() {
		logger.debug(Literal.LEAVING);
		
		//Id
		//Country
		//State Code
		//Entity Code
		//Tax Code
		//Address Line 1
		//Address Line 2
		//Address Line 3
		//Address Line 4
		//Pin Code
		//City Code
		
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
	 * Deletes a TaxDetail object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug(Literal.LEAVING);
		
		final TaxDetail aTaxDetail = new TaxDetail();
		BeanUtils.copyProperties(this.taxDetail, aTaxDetail);
		String tranType=PennantConstants.TRAN_WF;
		
		// Show a confirm box
		final String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record") + "\n\n --> " + aTaxDetail.getId();
		if (MessageUtil.confirm(msg) == MessageUtil.YES) {
			if (StringUtils.trimToEmpty(aTaxDetail.getRecordType()).equals("")){
				aTaxDetail.setVersion(aTaxDetail.getVersion()+1);
				aTaxDetail.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				
				if (isWorkFlowEnabled()){
					aTaxDetail.setRecordStatus(userAction.getSelectedItem().getValue().toString());
					aTaxDetail.setNewRecord(true);
					tranType=PennantConstants.TRAN_WF;
					getWorkFlowDetails(userAction.getSelectedItem().getLabel(), aTaxDetail.getNextTaskId(), aTaxDetail);
				}else{
					tranType=PennantConstants.TRAN_DEL;
				}
			}

			try {
				if(doProcess(aTaxDetail,tranType)){
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
		
		if (this.taxDetail.isNewRecord()) {
			this.btnCancel.setVisible(false);
			readOnlyComponent(false, this.taxCode);
		} else {
			this.btnCancel.setVisible(true);
			readOnlyComponent(true, this.taxCode);
			
		}
	
			readOnlyComponent(isReadOnly("TaxDetailDialog_Country"), this.country);
			readOnlyComponent(isReadOnly("TaxDetailDialog_StateCode"), this.stateCode);
			readOnlyComponent(isReadOnly("TaxDetailDialog_EntityCode"), this.entityCode);
			readOnlyComponent(isReadOnly("TaxDetailDialog_AddressLine1"), this.addressLine1);
			readOnlyComponent(isReadOnly("TaxDetailDialog_AddressLine2"), this.addressLine2);
			readOnlyComponent(isReadOnly("TaxDetailDialog_AddressLine3"), this.addressLine3);
			readOnlyComponent(isReadOnly("TaxDetailDialog_AddressLine4"), this.addressLine4);
			readOnlyComponent(isReadOnly("TaxDetailDialog_PinCode"), this.pinCode);
			readOnlyComponent(isReadOnly("TaxDetailDialog_CityCode"), this.cityCode);
			
			if (isWorkFlowEnabled()) {
				for (int i = 0; i < userAction.getItemCount(); i++) {
					userAction.getItemAtIndex(i).setDisabled(false);
				}
				if (this.taxDetail.isNewRecord()) {
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
			
	
			readOnlyComponent(true, this.country);
			readOnlyComponent(true, this.stateCode);
			readOnlyComponent(true, this.entityCode);
			readOnlyComponent(true, this.taxCode);
			readOnlyComponent(true, this.addressLine1);
			readOnlyComponent(true, this.addressLine2);
			readOnlyComponent(true, this.addressLine3);
			readOnlyComponent(true, this.addressLine4);
			readOnlyComponent(true, this.pinCode);
			readOnlyComponent(true, this.cityCode);

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
			  	this.country.setValue("");
			  	this.country.setDescription("");
			  	this.stateCode.setValue("");
			  	this.stateCode.setDescription("");
				this.entityCode.setValue("");
				this.taxCode.setValue("");
				this.addressLine1.setValue("");
				this.addressLine2.setValue("");
				this.addressLine3.setValue("");
				this.addressLine4.setValue("");
				this.pinCode.setValue("");
			  	this.cityCode.setValue("");
			  	this.cityCode.setDescription("");

			logger.debug("Leaving");
		}

		/**
		 * Saves the components to table. <br>
		 */
		public void doSave() {
			logger.debug("Entering");
			final TaxDetail aTaxDetail = new TaxDetail();
			BeanUtils.copyProperties(this.taxDetail, aTaxDetail);
			boolean isNew = false;

			doSetValidation();
			doWriteComponentsToBean(aTaxDetail);

			isNew = aTaxDetail.isNew();
			String tranType = "";

			if (isWorkFlowEnabled()) {
				tranType = PennantConstants.TRAN_WF;
				if (StringUtils.isBlank(aTaxDetail.getRecordType())) {
					aTaxDetail.setVersion(aTaxDetail.getVersion() + 1);
					if (isNew) {
						aTaxDetail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
					} else {
						aTaxDetail.setRecordType(PennantConstants.RECORD_TYPE_UPD);
						aTaxDetail.setNewRecord(true);
					}
				}
			} else {
				aTaxDetail.setVersion(aTaxDetail.getVersion() + 1);
				if (isNew) {
					tranType = PennantConstants.TRAN_ADD;
				} else {
					tranType = PennantConstants.TRAN_UPD;
				}
			}

			try {
				if (doProcess(aTaxDetail, tranType)) {
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
		private boolean doProcess(TaxDetail aTaxDetail, String tranType) {
			logger.debug("Entering");
			boolean processCompleted = false;
			AuditHeader auditHeader = null;
			String nextRoleCode = "";

			aTaxDetail.setLastMntBy(getUserWorkspace().getLoggedInUser().getLoginUsrID());
			aTaxDetail.setLastMntOn(new Timestamp(System.currentTimeMillis()));
			aTaxDetail.setUserDetails(getUserWorkspace().getLoggedInUser());

			if (isWorkFlowEnabled()) {
				String taskId = getTaskId(getRole());
				String nextTaskId = "";
				aTaxDetail.setRecordStatus(userAction.getSelectedItem().getValue().toString());

				if ("Save".equals(userAction.getSelectedItem().getLabel())) {
					nextTaskId = taskId + ";";
				} else {
					nextTaskId = StringUtils.trimToEmpty(aTaxDetail.getNextTaskId());

					nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
					if ("".equals(nextTaskId)) {
						nextTaskId = getNextTaskIds(taskId, aTaxDetail);
					}

					if (isNotesMandatory(taskId, aTaxDetail)) {
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

				aTaxDetail.setTaskId(taskId);
				aTaxDetail.setNextTaskId(nextTaskId);
				aTaxDetail.setRoleCode(getRole());
				aTaxDetail.setNextRoleCode(nextRoleCode);

				auditHeader = getAuditHeader(aTaxDetail, tranType);
				String operationRefs = getServiceOperations(taskId, aTaxDetail);

				if ("".equals(operationRefs)) {
					processCompleted = doSaveProcess(auditHeader, null);
				} else {
					String[] list = operationRefs.split(";");

					for (int i = 0; i < list.length; i++) {
						auditHeader = getAuditHeader(aTaxDetail, PennantConstants.TRAN_WF);
						processCompleted = doSaveProcess(auditHeader, list[i]);
						if (!processCompleted) {
							break;
						}
					}
				}
			} else {
				auditHeader = getAuditHeader(aTaxDetail, tranType);
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
			TaxDetail aTaxDetail = (TaxDetail) auditHeader.getAuditDetail().getModelData();
			boolean deleteNotes = false;

			try {

				while (retValue == PennantConstants.porcessOVERIDE) {

					if (StringUtils.isBlank(method)) {
						if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {
							auditHeader = taxDetailService.delete(auditHeader);
							deleteNotes = true;
						} else {
							auditHeader = taxDetailService.saveOrUpdate(auditHeader);
						}

					} else {
						if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
							auditHeader = taxDetailService.doApprove(auditHeader);

							if (aTaxDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
								deleteNotes = true;
							}

						} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
							auditHeader = taxDetailService.doReject(auditHeader);
							if (aTaxDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
								deleteNotes = true;
							}

						} else {
							auditHeader.setErrorDetails(new ErrorDetails(PennantConstants.ERR_9999, Labels
									.getLabel("InvalidWorkFlowMethod"), null));
							retValue = ErrorControl.showErrorControl(this.window_TaxDetailDialog, auditHeader);
							return processCompleted;
						}
					}

					auditHeader = ErrorControl.showErrorDetails(this.window_TaxDetailDialog, auditHeader);
					retValue = auditHeader.getProcessStatus();

					if (retValue == PennantConstants.porcessCONTINUE) {
						processCompleted = true;

						if (deleteNotes) {
							deleteNotes(getNotes(this.taxDetail), true);
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

		private AuditHeader getAuditHeader(TaxDetail aTaxDetail, String tranType) {
			AuditDetail auditDetail = new AuditDetail(tranType, 1, aTaxDetail.getBefImage(), aTaxDetail);
			return new AuditHeader(getReference(), null, null, null, auditDetail, aTaxDetail.getUserDetails(),
					getOverideMap());
		}

		public void setTaxDetailService(TaxDetailService taxDetailService) {
			this.taxDetailService = taxDetailService;
		}
			
}
