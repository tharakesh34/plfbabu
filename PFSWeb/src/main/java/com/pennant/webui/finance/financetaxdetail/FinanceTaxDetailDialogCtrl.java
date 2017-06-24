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

import java.lang.reflect.InvocationTargetException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataAccessException;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Row;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.ExtendedCombobox;
import com.pennant.app.constants.LengthConstants;
import com.pennant.backend.dao.finance.JountAccountDetailDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.applicationmaster.PinCode;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.GuarantorDetail;
import com.pennant.backend.model.finance.JointAccountDetail;
import com.pennant.backend.model.finance.financetaxdetail.FinanceTaxDetail;
import com.pennant.backend.model.systemmasters.City;
import com.pennant.backend.model.systemmasters.Province;
import com.pennant.backend.service.finance.FinanceTaxDetailService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.component.Uppercasebox;
import com.pennant.search.Filter;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.util.Constraint.StaticListValidator;
import com.pennant.webui.finance.financemain.FinBasicDetailsCtrl;
import com.pennant.webui.finance.financemain.JointAccountDetailDialogCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.MessageUtil;
import com.pennanttech.pff.core.Literal;


/**
 * This is the controller class for the
 * /WEB-INF/pages/tax/FinanceTaxDetail/financeTaxDetailDialog.zul file. <br>
 */
public class FinanceTaxDetailDialogCtrl extends GFCBaseCtrl<FinanceTaxDetail>{

	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(FinanceTaxDetailDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting  by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_FinanceTaxDetailDialog; 
	protected ExtendedCombobox 		finReference; 
	protected Combobox 		applicableFor; 
	protected ExtendedCombobox 	custRef; 
	protected  long 		applicableForCustId; 
	protected Row			row_custID;
	protected Checkbox 		taxExempted; 
	protected Uppercasebox 	taxNumber; 
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

	private List<ValueLabel> listApplicableFor=PennantStaticListUtil.getTaxApplicableFor();
	JointAccountDetailDialogCtrl jntDialogCtrl = null;
	
	private transient String oldCountry;
	private transient String oldProvince;
	private transient String oldCity;
	private boolean fromLoan = false;
	private String panNum ;
	Tab parenttab = null;
	protected Groupbox								finBasicdetails;
	private Object									financeMainDialogCtrl	= null;
	private FinBasicDetailsCtrl						finBasicDetailsCtrl;
	private JountAccountDetailDAO		jountAccountDetailDAO;

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
	@SuppressWarnings("unchecked")
	public void onCreate$window_FinanceTaxDetailDialog(Event event) throws Exception {
		logger.debug(Literal.ENTERING);

		// Set the page level components.
		setPageComponents(window_FinanceTaxDetailDialog);

		try {
			// Get the required arguments.
			this.financeTaxDetail = (FinanceTaxDetail) arguments.get("financeTaxDetail");

			if (arguments.containsKey("fromLoan")) {
				fromLoan = (Boolean) arguments.get("fromLoan");
			}else{
				this.financeTaxDetailListCtrl = (FinanceTaxDetailListCtrl) arguments.get("financeTaxDetailListCtrl");
			}
			
			if(arguments.containsKey("panNum")){
				panNum = (String)arguments.get("panNum");
			}
			
			if (this.financeTaxDetail == null) {
				throw new Exception(Labels.getLabel("error.unhandled"));
			}

			if (fromLoan) {
				if (financeTaxDetail == null) {
					this.financeTaxDetail = new FinanceTaxDetail();
					financeTaxDetail.setNewRecord(true);
				}
				this.financeTaxDetail.setWorkflowId(0);

				if (arguments.containsKey("roleCode")) {
					setRole(arguments.get("roleCode").toString());
					getUserWorkspace().allocateRoleAuthorities(getRole(),this.pageRightName);
				}

				if (arguments.containsKey("tab")) {
					parenttab = (Tab) arguments.get("tab");
				}

				if (arguments.containsKey("finHeaderList")) {
					appendFinBasicDetails((ArrayList<Object>) arguments.get("finHeaderList"));
				} else {
					appendFinBasicDetails(null);
				}

				if (arguments.containsKey("financeMainDialogCtrl")) {
					setFinanceMainDialogCtrl(arguments.get("financeMainDialogCtrl"));
				}
			}

			if(financeMainDialogCtrl!=null){
				try {
					jntDialogCtrl = (JointAccountDetailDialogCtrl) getFinanceMainDialogCtrl().getClass()
							.getMethod("getJointAccountDetailDialogCtrl").invoke(getFinanceMainDialogCtrl());
				} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException
						| SecurityException e) {
					e.printStackTrace();
				}
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
				if(!fromLoan){
					getUserWorkspace().allocateAuthorities(this.pageRightName,null);
				}
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
	 * This method is for append finance basic details to respective parent tabs
	 */
	private void appendFinBasicDetails(ArrayList<Object> finHeaderList) {
		try {
			final HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("parentCtrl", this);
			if (finHeaderList != null) {
				map.put("finHeaderList", finHeaderList);
			}
			Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/FinBasicDetails.zul", this.finBasicdetails,
					map);
		} catch (Exception e) {
			logger.debug(e);
		}
	}

	public void doSetLabels(ArrayList<Object> finHeaderList) {
		getFinBasicDetailsCtrl().doWriteBeanToComponents(finHeaderList);
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug(Literal.ENTERING);
		
		this.finReference.setModuleName("FinanceMain");
		this.finReference.setMandatoryStyle(true);
		this.finReference.setValueColumn("FinReference");
		this.finReference.setDescColumn("FinType");
		this.finReference.setDisplayStyle(2);
		this.finReference.setValidateColumns(new String[] { "FinReference" });
		this.finReference.setMaxlength(LengthConstants.LEN_REF);
		this.finReference.setTextBoxWidth(143);

		this.taxNumber.setMaxlength(15);
		this.addrLine1.setMaxlength(100);
		this.addrLine2.setMaxlength(100);
		this.addrLine3.setMaxlength(100);
		this.addrLine4.setMaxlength(100);
		
		this.country.setModuleName("Country");
		this.country.setValueColumn("CountryCode");
		this.country.setDescColumn("CountryDesc");
		this.country.setValidateColumns(new String[] {"CountryCode"});
		this.country.setMandatoryStyle(true);
		this.country.setTextBoxWidth(143);
		
		this.province.setModuleName("Province");
		this.province.setValueColumn("CPProvince");
		this.province.setDescColumn("CPProvinceName");
		this.province.setValidateColumns(new String[] {"CPProvince"});
		this.province.setMandatoryStyle(true);
		this.province.setTextBoxWidth(143);
		
		this.custRef.setModuleName("Customer");
		this.custRef.setValueColumn("CustCIF");
		this.custRef.setDescColumn("CustShrtName");
		this.custRef.setValidateColumns(new String[] { "CustCIF" });
		this.custRef.setMandatoryStyle(true);
		this.custRef.setTextBoxWidth(143);
		
		List<String> jntDetail = new ArrayList<>();
		if (jntDialogCtrl != null) {
			if (jntDialogCtrl.getJountAccountDetailList().size() > 0) {
				for (JointAccountDetail jntDet : jntDialogCtrl.getJountAccountDetailList()) {
					jntDetail.add(jntDet.getCustCIF());
				}
			} else if (jntDialogCtrl.getGuarantorDetailList().size() > 0) {
				for (GuarantorDetail jntDet : jntDialogCtrl.getGuarantorDetailList()) {
					jntDetail.add(jntDet.getGuarantorCIF());
				}
			}
		}else{
			List<JointAccountDetail> jointDetails = jountAccountDetailDAO.getJountAccountDetailByFinnRef(this.finReference.getValue());
			if(jointDetails!=null){
			for (JointAccountDetail jntDet : jointDetails) {
				jntDetail.add(jntDet.getCustCIF());
			}
			}
		}

		// set CustomerReference as Filter for finLimitRef 
		Filter custRefFilter[] = new Filter[1];
		custRefFilter[0] = new Filter("CustCif", jntDetail, Filter.OP_IN);
		this.custRef.setFilters(custRefFilter);
		
		this.city.setModuleName("City");
		this.city.setValueColumn("PCCity");
		this.city.setDescColumn("PCCityName");
		this.city.setValidateColumns(new String[] {"PCCity"});
		this.city.setMandatoryStyle(true);
		this.city.setTextBoxWidth(143);
		
		this.pinCode.setModuleName("PinCode");
		this.pinCode.setValueColumn("PinCode");
		this.pinCode.setDescColumn("AreaName");
		this.pinCode.setValidateColumns(new String[] {"PinCode"});
		this.pinCode.setMandatoryStyle(true);
		this.pinCode.setTextBoxWidth(143);

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
	public void onChange$applicableFor(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		
		if(!StringUtils.equals("P", getComboboxValue(applicableFor))){
			this.row_custID.setVisible(true);
		}else{
			this.row_custID.setVisible(false);
		}
		
		logger.debug("Leaving" + event.toString());
	}
	
	
	public void onFulfill$custRef(Event event) {
		logger.debug("Entering" + event.toString());

		Object dataObject = custRef.getObject();
		if (dataObject instanceof String) {
			this.custRef.setValue(dataObject.toString());
			this.custRef.setDescription(dataObject.toString());
			this.taxNumber.setValue("");
		} else {
			Customer custtref = (Customer) dataObject;
			if (custtref != null) {
				this.custRef.setValue(custtref.getCustCIF());
				this.custRef.setDescription(custtref.getCustShrtName());
				if(jntDialogCtrl!=null){
					if (jntDialogCtrl.getJountAccountDetailList().size() > 0) {
						for (JointAccountDetail jointDetails : jntDialogCtrl.getJountAccountDetailList()) {
							if (StringUtils.equals(custRef.getValue(), jointDetails.getCustCIF())) {
								this.applicableForCustId = jointDetails.getJointAccountId();
								panNum = custtref.getCustCRCPR();
							}
						}
					} else {
						if (jntDialogCtrl.getGuarantorDetailList().size() > 0) {
							for (GuarantorDetail gurDetails : jntDialogCtrl.getGuarantorDetailList()) {
								if (StringUtils.equals(custRef.getValue(), gurDetails.getGuarantorCIF())) {
									this.applicableForCustId = gurDetails.getGuarantorId();
								}
							}
						}
					}
				}
			}
		}

		logger.debug("Leaving" + event.toString());
	}

	public void onFulfill$finReference(Event event) {
		logger.debug("Entering" + event.toString());
		Object dataObject = finReference.getObject();
		if (dataObject instanceof String) {
			this.finReference.setValue(dataObject.toString(), "");
		} else {
			FinanceMain main = (FinanceMain) dataObject;
			if (main != null) {
				this.finReference.setValue(main.getFinReference(), "");
			}
		}
		logger.debug("Leaving" + event.toString());
	}

	public void taxnumberValidate(){
	
	}
	public void onFulfill$province(Event event) {
		logger.debug("Entering" + event.toString());
		Object dataObject = province.getObject();
		String pcProvince = null;
		String taxNumberValue = this.taxNumber.getValue();
		if (dataObject instanceof String) {
			if (taxNumberValue.length() > 10) {
				String suffix = taxNumberValue.substring(2);
				this.taxNumber.setValue(suffix + panNum);
			} else if(taxNumberValue.length() == 2) {
				this.taxNumber.setValue("");
			} else if (taxNumberValue.length() == 10) {
				//do nothing
			} else {
				this.taxNumber.setValue("");
			}
			fillPindetails(null, null);
		} else {
			Province province = (Province) dataObject;
			if (province == null) {
				fillPindetails(null, null);
			}
			if (province != null) {
				this.province.setErrorMessage("");
				this.taxNumber.setErrorMessage("");
				
				pcProvince = this.province.getValue();
				fillPindetails(null, pcProvince);
				String taxStateCode = province.getTaxStateCode() == null ? "" : province.getTaxStateCode();
				if (taxNumberValue.length() == 10) {
					this.taxNumber.setValue(taxStateCode + taxNumberValue + panNum );
				} else if (taxNumberValue.length() > 10) {
					String suffix = taxNumberValue;
					//String suffix = taxNumberValue.substring(2);
					this.taxNumber.setValue(taxStateCode + suffix + panNum );
				} else {
					this.taxNumber.setValue(taxStateCode + panNum);
				}
			}else{
				if (taxNumberValue.length() > 10) {
					String suffix = taxNumberValue.substring(2);
					this.taxNumber.setValue(suffix + panNum);
				} else if(taxNumberValue.length() == 2) {
					this.taxNumber.setValue("");
				} else if (taxNumberValue.length() == 10) {
					//do nothing
				} else {
					this.taxNumber.setValue("");
				}
			}
		}
		
		this.city.setObject("");
		this.pinCode.setObject("");
		this.city.setValue("");
		this.city.setDescription("");
		this.pinCode.setValue("");
		this.pinCode.setDescription("");
		fillCitydetails(pcProvince);
		logger.debug("Leaving" + event.toString());
	}

	private void fillCitydetails(String state) {
		logger.debug("Entering");

		this.city.setModuleName("City");
		this.city.setValueColumn("PCCity");
		this.city.setDescColumn("PCCityName");
		this.city.setValidateColumns(new String[] { "PCCity" });
		Filter[] filters1 = new Filter[2];
		
		if (state == null) {
			filters1[0] = new Filter("PCProvince", null, Filter.OP_NOT_EQUAL);
		} else {
			filters1[0] = new Filter("PCProvince", state, Filter.OP_EQUAL);
		}
		
		filters1[1] = new Filter("CITYISACTIVE", 1, Filter.OP_EQUAL);
		
		this.city.setFilters(filters1);
	}

	/**
	 * onChanging Branch
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onFulfill$city(Event event) throws InterruptedException {
		logger.debug("Entering");

		Object dataObject = city.getObject();

		String cityValue = null;
		if (dataObject instanceof String) {
			this.city.setValue("");
			this.city.setDescription("");
		} else {
			City city = (City) dataObject;
			if (city != null) {
				
				this.city.setErrorMessage("");
				this.province.setErrorMessage("");
				this.taxNumber.setErrorMessage("");
				
				this.province.setValue(city.getPCProvince());
				this.province.setDescription(city.getLovDescPCProvinceName());
				cityValue = this.city.getValue();
				String taxNumberValue = this.taxNumber.getValue();
				String citytaxStateCode = city.getTaxStateCode() == null ? "" : city.getTaxStateCode();
				if (taxNumberValue.length() == 10) {
					this.taxNumber.setValue(citytaxStateCode + taxNumberValue);
				}else if (taxNumberValue.length() > 10) {
					String suffix = taxNumberValue.substring(2);
					this.taxNumber.setValue(citytaxStateCode + suffix);
				} else {
					this.taxNumber.setValue(citytaxStateCode);
				}
			}
		}
		fillPindetails(cityValue, this.province.getValue());
		
		this.pinCode.setObject("");
		this.pinCode.setValue("");
		this.pinCode.setDescription("");
		
		logger.debug("Leaving");
	}

	private void fillPindetails(String cityValue, String provice) {
		this.pinCode.setModuleName("PinCode");
		this.pinCode.setValueColumn("PinCode");
		this.pinCode.setDescColumn("AreaName");
		this.pinCode.setValidateColumns(new String[] { "PinCode" });
		Filter[] filters1 = new Filter[2];
		
		if (cityValue != null) {
			filters1[0] = new Filter("City", cityValue, Filter.OP_EQUAL);
		} else if(provice != null && !provice.isEmpty()) {
			filters1[0] = new Filter("PCProvince", provice, Filter.OP_EQUAL);
		} else {
			filters1[0] = new Filter("City", null, Filter.OP_NOT_EQUAL);
		}
		
		filters1[1] = new Filter("Active", 1, Filter.OP_EQUAL);
		
		this.pinCode.setFilters(filters1);
	}

	/**
	 * onChanging Branch
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onFulfill$pinCode(Event event) throws InterruptedException {
		logger.debug("Entering");

		Object dataObject = pinCode.getObject();
		if (dataObject instanceof String) {
			this.pinCode.setValue("");
			this.pinCode.setDescription("");
		} else {
			PinCode pinCode = (PinCode) dataObject;
			if (pinCode != null) {
				this.city.setValue(pinCode.getCity());
				this.city.setDescription(pinCode.getPCCityName());
				this.province.setValue(pinCode.getPCProvince());
				this.province.setDescription(pinCode.getLovDescPCProvinceName());
				
				this.city.setErrorMessage("");
				this.province.setErrorMessage("");
				this.pinCode.setErrorMessage("");
				this.taxNumber.setErrorMessage("");
				
				String taxNumberValue = this.taxNumber.getValue();
				String gstInValue = pinCode.getGstin() == null ? "" : pinCode.getGstin();
				if (taxNumberValue.length() == 10) {
					this.taxNumber.setValue(gstInValue + taxNumberValue);
				} else if (taxNumberValue.length() > 10) {
					String suffix = taxNumberValue.substring(2);
					this.taxNumber.setValue(gstInValue + suffix);
				} else {
					this.taxNumber.setValue(gstInValue);
				}
			} 
		}
		
		logger.debug("Leaving");
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
		if(!StringUtils.equals("P", getComboboxValue(applicableFor)) && jntDialogCtrl!=null){
			if (jntDialogCtrl.getJountAccountDetailList().size() > 0) {
				for (JointAccountDetail jntDets : jntDialogCtrl.getJountAccountDetailList()) {
					if (jntDets.getJointAccountId() == aFinanceTaxDetail.getApplicableForCustId()){
						this.custRef.setValue(jntDets.getCustCIF());
					}
				}
			}else {
				for (GuarantorDetail jntDets : jntDialogCtrl.getGuarantorDetailList()) {
					if (jntDets.getGuarantorId() ==  aFinanceTaxDetail.getApplicableForCustId()){
						this.custRef.setValue(jntDets.getGuarantorCIF());
					}
				}
			}
		}
		this.taxExempted.setChecked(aFinanceTaxDetail.isTaxExempted());
		this.taxNumber.setValue(aFinanceTaxDetail.getTaxNumber());
		this.addrLine1.setValue(aFinanceTaxDetail.getAddrLine1());
		this.addrLine2.setValue(aFinanceTaxDetail.getAddrLine2());
		this.addrLine3.setValue(aFinanceTaxDetail.getAddrLine3());
		this.addrLine4.setValue(aFinanceTaxDetail.getAddrLine4());
		//this.country.setValue(aFinanceTaxDetail.getCountry());
		this.province.setValue(aFinanceTaxDetail.getProvince());
		this.city.setValue(aFinanceTaxDetail.getCity());
		this.pinCode.setValue(aFinanceTaxDetail.getPinCode());

		if(!StringUtils.equals("P", aFinanceTaxDetail.getApplicableFor())){
			this.row_custID.setVisible(true);
		}else{
			this.row_custID.setVisible(false);
		}
		
		if (aFinanceTaxDetail.isNewRecord()){
			this.country.setValue("IN");
			this.country.setDescription("INDIAone");
			this.province.setDescription("");
			this.city.setDescription("");
			this.pinCode.setDescription("");
		}else{
			this.country.setValue(aFinanceTaxDetail.getCountry());
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
	 * @return 
	 */
	public ArrayList<WrongValueException> doWriteComponentsToBean(FinanceTaxDetail aFinanceTaxDetail) {
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
		
		try{
			if(!StringUtils.equals("P", getComboboxValue(applicableFor))){
				aFinanceTaxDetail.setApplicableForCustId(applicableForCustId);
			}
		}catch(WrongValueException we){
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

		if(!fromLoan){
			doRemoveValidation();
			doRemoveLOVValidation();

			if (!wve.isEmpty()) {
				WrongValueException [] wvea = new WrongValueException[wve.size()];
				for (int i = 0; i < wve.size(); i++) {
					wvea[i] = (WrongValueException) wve.get(i);
				}
				throw new WrongValuesException(wvea);
			}
		}
		logger.debug(Literal.LEAVING);
		return wve;
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
				if (fromLoan && !enqiryModule) {
					doEdit();
				}
			}
		}

		if (enqiryModule) {
			this.btnCtrl.setBtnStatus_Enquiry();
			this.btnNotes.setVisible(false);
		}

		doWriteBeanToComponents(financeTaxDetail);
		if (fromLoan) {
			try {
				getFinanceMainDialogCtrl().getClass().getMethod("setFinanceTaxDetailDialogCtrl", this.getClass())
						.invoke(getFinanceMainDialogCtrl(), this);
			} catch (Exception e) {
				logger.error("Exception: ", e);
			}
			if (parenttab != null) {
				this.parenttab.setVisible(true);
			}

		} else {
			setDialog(DialogType.EMBEDDED);
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug(Literal.LEAVING);

		if (!this.finReference.isReadonly()){
			this.finReference.setConstraint(new PTStringValidator(Labels.getLabel("label_FinanceTaxDetailDialog_FinReference.value"),null,true, true));
		}
		if (!this.applicableFor.isReadonly()){
			this.applicableFor.setConstraint(new StaticListValidator(listApplicableFor,Labels.getLabel("label_FinanceTaxDetailDialog_ApplicableFor.value")));
		}
		if (!this.taxNumber.isReadonly()){
			this.taxNumber.setConstraint(new PTStringValidator(Labels.getLabel("label_FinanceTaxDetailDialog_TaxNumber.value"),PennantRegularExpressions.REGEX_GSTIN,true));
		}
		if (!this.addrLine1.isReadonly()){
			this.addrLine1.setConstraint(new PTStringValidator(Labels.getLabel("label_FinanceTaxDetailDialog_AddrLine1.value"),PennantRegularExpressions.REGEX_ADDRESS,true));
		}
		if (!this.addrLine2.isReadonly()){
			this.addrLine2.setConstraint(new PTStringValidator(Labels.getLabel("label_FinanceTaxDetailDialog_AddrLine2.value"),PennantRegularExpressions.REGEX_ADDRESS,false));
		}
		if (!this.addrLine3.isReadonly()){
			this.addrLine3.setConstraint(new PTStringValidator(Labels.getLabel("label_FinanceTaxDetailDialog_AddrLine3.value"),PennantRegularExpressions.REGEX_ADDRESS,false));
		}
		if (!this.addrLine4.isReadonly()){
			this.addrLine4.setConstraint(new PTStringValidator(Labels.getLabel("label_FinanceTaxDetailDialog_AddrLine4.value"),PennantRegularExpressions.REGEX_ADDRESS,false));
		}
		if (!this.country.isReadonly()){
			this.country.setConstraint(new PTStringValidator(Labels.getLabel("label_FinanceTaxDetailDialog_Country.value"),null,true, true));
		}
		if (!this.province.isReadonly()){
			this.province.setConstraint(new PTStringValidator(Labels.getLabel("label_FinanceTaxDetailDialog_Province.value"),null,true, true));
		}
		if (!this.city.isReadonly()){
			this.city.setConstraint(new PTStringValidator(Labels.getLabel("label_FinanceTaxDetailDialog_City.value"),null,true, true));
		}
		if (!this.pinCode.isReadonly()){
			this.pinCode.setConstraint(new PTStringValidator(Labels.getLabel("label_FinanceTaxDetailDialog_PinCode.value"),null,true, true));
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
			if(fromLoan){
				readOnlyComponent(true, this.finReference);
			}else{
				readOnlyComponent(false, this.finReference);
			}
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
	
	public void doSave_Tax(FinanceDetail financeDetail, Tab tab, boolean recSave) throws InterruptedException {
		logger.debug("Entering");

		doClearMessage();
		if (!recSave) {
			doSetValidation();
		}

		ArrayList<WrongValueException> wve = doWriteComponentsToBean(financeTaxDetail);
		if (!wve.isEmpty() && parenttab != null) {
			parenttab.setSelected(true);
		}
		showErrorDetails(wve);

		if (StringUtils.isBlank(financeTaxDetail.getRecordType())) {
			financeTaxDetail.setVersion(financeTaxDetail.getVersion() + 1);
			financeTaxDetail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
			financeTaxDetail.setNewRecord(true);
		}
		financeTaxDetail.setLastMntBy(getUserWorkspace().getLoggedInUser().getLoginUsrID());
		financeTaxDetail.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		financeTaxDetail.setUserDetails(getUserWorkspace().getLoggedInUser());
		financeDetail.setTaxDetail(financeTaxDetail);
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
	 * Writes the showErrorDetails method for .<br>
	 * displaying exceptions if occured
	 */
	private void showErrorDetails(ArrayList<WrongValueException> wve) {
		logger.debug("Entering");
		doRemoveValidation();
		if (wve.size() > 0) {
			logger.debug("Throwing occured Errors By using WrongValueException");
			if (parenttab != null) {
				parenttab.setSelected(true);
			}
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}

		logger.debug("Leaving");
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
	
	public boolean isReadOnly(String componentName) {
		if (isWorkFlowEnabled() || fromLoan) {
			return getUserWorkspace().isReadOnly(componentName);
		}
		return false;
	}

	public void setFinanceTaxDetailService(FinanceTaxDetailService financeTaxDetailService) {
		this.financeTaxDetailService = financeTaxDetailService;
	}

	public Object getFinanceMainDialogCtrl() {
		return financeMainDialogCtrl;
	}

	public void setFinanceMainDialogCtrl(Object financeMainDialogCtrl) {
		this.financeMainDialogCtrl = financeMainDialogCtrl;
	}

	public FinBasicDetailsCtrl getFinBasicDetailsCtrl() {
		return finBasicDetailsCtrl;
	}

	public void setFinBasicDetailsCtrl(FinBasicDetailsCtrl finBasicDetailsCtrl) {
		this.finBasicDetailsCtrl = finBasicDetailsCtrl;
	}

	public JountAccountDetailDAO getJountAccountDetailDAO() {
		return jountAccountDetailDAO;
	}

	public void setJountAccountDetailDAO(JountAccountDetailDAO jountAccountDetailDAO) {
		this.jountAccountDetailDAO = jountAccountDetailDAO;
	}

}
