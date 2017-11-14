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
 * FileName    		:  MandateDialogCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  18-10-2016    														*
 *                                                                  						*
 * Modified Date    :  18-10-2016    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 18-10-2016       Pennant	                 0.1                                            * 
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

package com.pennant.webui.finance.enquiry;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.North;
import org.zkoss.zul.Row;
import org.zkoss.zul.Space;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.CurrencyBox;
import com.pennant.ExtendedCombobox;
import com.pennant.FrequencyBox;
import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.DateUtility;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.finance.FinanceEnquiry;
import com.pennant.backend.model.mandate.Mandate;
import com.pennant.backend.service.mandate.MandateService;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.util.PennantAppUtil;
import com.pennant.webui.mandate.mandate.MandateListCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.MessageUtil;
import com.pennanttech.pff.core.util.DateUtil.DateFormat;

/**
 * ************************************************************<br>
 * This is the controller class for the
 * /WEB-INF/pages/Mandate/mandateDialog.zul file. <br>
 * ************************************************************<br>
 */
public class MandateEnquiryDialogCtrl extends GFCBaseCtrl<Mandate> {

	private static final long				serialVersionUID				= 1L;
	private static final Logger				logger							= Logger.getLogger(MandateEnquiryDialogCtrl.class);

	protected Window						window_FinMandateEnquiryDialog;
	protected ExtendedCombobox				custID;
	protected ExtendedCombobox				mandateRef;
	protected Combobox						mandateType;
	protected ExtendedCombobox				bankBranchID;
	protected Textbox						bank;
	protected Textbox						city;
	protected Textbox						micr;
	protected Textbox						ifsc;
	protected Textbox						accNumber;
	protected Textbox						accHolderName;
	protected Datebox						inputDate;
	protected Textbox						jointAccHolderName;
	protected Combobox						accType;
	protected Checkbox						openMandate;
	protected Datebox						startDate;
	protected Datebox						expiryDate;
	protected CurrencyBox					maxLimit;
	protected FrequencyBox					periodicity;
	protected Textbox						phoneCountryCode;
	protected Textbox						phoneAreaCode;
	protected Textbox						phoneNumber;
	protected Combobox						status;
	protected Textbox						approvalID;
	protected Groupbox						gb_basicDetails;
	protected Groupbox						gb_listBoxFinances;
	protected Checkbox						useExisting;
	protected Checkbox						active;
	protected Textbox						reason;
	protected Textbox						umrNumber;
	protected Space							space_Reason;
	protected Space							space_Expirydate;
	protected Textbox						documentName;
	protected Button						btnViewMandateDoc;


	private boolean							fromLoanEnquiry			= false;

	// not auto wired vars
	private Mandate							mandate;
	private transient MandateListCtrl		mandateListCtrl;

	protected Button						btnProcess;
	protected Button						btnView;

	// ServiceDAOs / Domain Classes
	private transient MandateService		mandateService;

	private final List<ValueLabel>			mandateTypeList					= PennantStaticListUtil.getMandateTypeList();
	private final List<ValueLabel>			accTypeList						= PennantStaticListUtil.getAccTypeList();
	private final List<ValueLabel>			statusTypeList					= PennantStaticListUtil.getStatusTypeList();

	protected Listbox						listBoxMandateFinExposure;
	public transient int					ccyFormatter					= 0;
	protected North							north_mandate;

	/* loan related declrations */

	long									mandateID						= 0;
	protected Row							rowStatus;
	private Tabpanel						tabPanel_dialogWindow;
	private FinanceEnquiryHeaderDialogCtrl	financeEnquiryHeaderDialogCtrl	= null;

	/**
	 * default constructor.<br>
	 */
	public MandateEnquiryDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "MandateDialog";
	}

	// ************************************************* //
	// *************** Component Events **************** //
	// ************************************************* //

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * zul-file is called with a parameter for a selected Mandate object in a
	 * Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_FinMandateEnquiryDialog(Event event) throws Exception {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_FinMandateEnquiryDialog);

		try {
			// READ OVERHANDED params !

			if (arguments.containsKey("fromLoanEnquiry")) {
				fromLoanEnquiry = (Boolean) arguments.get("fromLoanEnquiry");
			}

			if (arguments.containsKey("tabPaneldialogWindow")) {
				tabPanel_dialogWindow = (Tabpanel) arguments.get("tabPaneldialogWindow");
			}

			if (arguments.containsKey("financeEnquiryHeaderDialogCtrl")) {
				this.financeEnquiryHeaderDialogCtrl = (FinanceEnquiryHeaderDialogCtrl) arguments.get("financeEnquiryHeaderDialogCtrl");
			}

			// READ OVERHANDED params !
			if (arguments.containsKey("mandate")) {
				this.mandate = (Mandate) arguments.get("mandate");
				Mandate befImage = new Mandate();
				BeanUtils.copyProperties(this.mandate, befImage);
				this.mandate.setBefImage(befImage);
				setMandate(this.mandate);
			} else {
				setMandate(null);
			}
			
			if (getMandate() != null) {
				ccyFormatter = CurrencyUtil.getFormat(getMandate().getMandateCcy());
			}


			getUserWorkspace().allocateAuthorities(super.pageRightName);

			// set Field Properties
			doSetFieldProperties();
			doShowDialog(getMandate());
		} catch (Exception e) {
			MessageUtil.showError(e);
		}

		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");
		// Empty sent any required attributes
		this.accNumber.setMaxlength(50);
		this.accHolderName.setMaxlength(50);
		this.jointAccHolderName.setMaxlength(50);
		this.startDate.setFormat(DateFormat.SHORT_DATE.getPattern());
		this.expiryDate.setFormat(DateFormat.SHORT_DATE.getPattern());
		this.inputDate.setFormat(DateFormat.SHORT_DATE.getPattern());

		this.maxLimit.setFormat(PennantApplicationUtil.getAmountFormate(ccyFormatter));
		this.maxLimit.setScale(ccyFormatter);
		this.maxLimit.setTextBoxWidth(171);
		this.maxLimit.setMandatory(false);

		this.periodicity.setMandatoryStyle(false);
		this.phoneCountryCode.setMaxlength(3);
		this.phoneCountryCode.setWidth("50px");
		this.phoneAreaCode.setMaxlength(3);
		this.phoneAreaCode.setWidth("50px");
		this.phoneNumber.setMaxlength(10);
		this.phoneNumber.setWidth("100px");
		this.approvalID.setMaxlength(50);

		this.custID.setModuleName("Customer");
		this.custID.setMandatoryStyle(false);
		this.custID.setValueColumn("CustCIF");
		this.custID.setDescColumn("CustShrtName");
		this.custID.setDisplayStyle(2);
		this.custID.setValidateColumns(new String[] { "CustID", "CustCIF" });

		this.bankBranchID.setModuleName("BankBranch");
		this.bankBranchID.setMandatoryStyle(false);
		this.bankBranchID.setValueColumn("BranchCode");
		this.bankBranchID.setDescColumn("BranchDesc");
		this.bankBranchID.setDisplayStyle(2);
		this.bankBranchID.setValidateColumns(new String[] { "BranchCode", "BranchDesc" });

		this.mandateRef.setModuleName("Mandate");
		this.mandateRef.setMandatoryStyle(false);
		this.mandateRef.setValueColumn("MandateID");
		this.mandateRef.setDescColumn("CustID");
		this.mandateRef.setDisplayStyle(2);
		this.mandateRef.setValidateColumns(new String[] { "MandateID", "CustID" });
		
		this.umrNumber.setReadonly(true);

		this.active.setChecked(true);
		logger.debug("Leaving");
	}

	/**
	 * when the "close" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnClose(Event event) throws InterruptedException {
		doClose(false);
	}

	// ****************************************************************+
	// ************************ GUI operations ************************+
	// ****************************************************************+

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the
	 * readOnly mode accordingly.
	 * 
	 * @param aMandate
	 * @throws InterruptedException
	 */
	public void doShowDialog(Mandate aMandate) throws InterruptedException {
		logger.debug("Entering");

		// set ReadOnly mode accordingly if the object is new or not.
		if (aMandate.isNew()) {
			this.inputDate.setValue(DateUtility.getAppDate());
			// setFocus
			this.custID.focus();
		} else {
			this.custID.setReadonly(true);
			doReadOnly();
		}
		try {
			// fill the components with the data
			doWriteBeanToComponents(aMandate);
			doDesignByMode();
			
			if (fromLoanEnquiry) {
				if (tabPanel_dialogWindow != null) {
					int rowsHeight = financeEnquiryHeaderDialogCtrl.grid_BasicDetails.getRows().getVisibleItemCount() * 20;
					this.window_FinMandateEnquiryDialog.setHeight(this.borderLayoutHeight - rowsHeight - 30 + "px");
					tabPanel_dialogWindow.appendChild(this.window_FinMandateEnquiryDialog);
				}
			}else{
				setDialog(DialogType.EMBEDDED);
			}


		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving");
	}

	private void doDesignByMode() {
		if (fromLoanEnquiry) {
			this.gb_listBoxFinances.setVisible(false);
			this.north_mandate.setVisible(false);
		}
	}

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly() {
		logger.debug("Entering ");
		readOnlyComponent(true, this.custID);
		readOnlyComponent(true, this.mandateRef);
		readOnlyComponent(true, this.inputDate);
		readOnlyComponent(true, this.mandateType);
		readOnlyComponent(true, this.bankBranchID);
		readOnlyComponent(true, this.accNumber);
		readOnlyComponent(true, this.accHolderName);
		readOnlyComponent(true, this.jointAccHolderName);
		readOnlyComponent(true, this.reason);
		readOnlyComponent(true, this.accType);
		readOnlyComponent(true, this.openMandate);
		readOnlyComponent(true, this.startDate);
		readOnlyComponent(true, this.expiryDate);
		readOnlyComponent(true, this.maxLimit);
		readOnlyComponent(true, this.periodicity);
		readOnlyComponent(true, this.phoneCountryCode);
		readOnlyComponent(true, this.phoneAreaCode);
		readOnlyComponent(true, this.phoneNumber);
		readOnlyComponent(true, this.status);
		readOnlyComponent(true, this.approvalID);
		readOnlyComponent(true, this.umrNumber);

		logger.debug("Leaving ");
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aMandate
	 *            Mandate
	 * @param tab
	 */
	public void doWriteBeanToComponents(Mandate aMandate) {
		logger.debug("Entering");
		mandateID = aMandate.getMandateID();

		if (aMandate.getCustID() != Long.MIN_VALUE && aMandate.getCustID() != 0) {
			this.custID.setAttribute("custID", aMandate.getCustID());
			this.custID.setValue(aMandate.getCustCIF(), aMandate.getCustShrtName());
		}
		fillComboBox(this.status, aMandate.getStatus(), statusTypeList, "");
		this.reason.setValue(aMandate.getReason());
		doWriteData(aMandate);
		if (!fromLoanEnquiry) {
			doFillManFinanceExposureDetails(getMandateService().getMandateFinanceDetailById(aMandate.getMandateID()));
		}

		this.approvalID.setValue(aMandate.getApprovalID());
		this.inputDate.setValue(aMandate.getInputDate());

		logger.debug("Leaving");
	}

	private void doWriteData(Mandate aMandate) {

		if (aMandate.getMandateID() != 0 && aMandate.getMandateID() != Long.MIN_VALUE) {
			this.mandateRef.setAttribute("mandateID", aMandate.getMandateID());
			this.mandateRef.setValue(String.valueOf(aMandate.getMandateID()), StringUtils.trimToEmpty(aMandate.getMandateRef()));
		}
		fillComboBox(this.mandateType, aMandate.getMandateType(), mandateTypeList, "");

		if (aMandate.getBankBranchID() != Long.MIN_VALUE && aMandate.getBankBranchID() != 0) {
			this.bankBranchID.setAttribute("bankBranchID", aMandate.getBankBranchID());
			this.bankBranchID.setValue(StringUtils.trimToEmpty(aMandate.getBranchCode()), StringUtils.trimToEmpty(aMandate.getBranchDesc()));
		}
		this.city.setValue(StringUtils.trimToEmpty(aMandate.getCity()));
		this.bank.setValue(StringUtils.trimToEmpty(aMandate.getBankName()));
		this.micr.setValue(aMandate.getMICR());
		this.ifsc.setValue(aMandate.getIFSC());
		this.accNumber.setValue(StringUtils.trimToEmpty(aMandate.getAccNumber()));
		this.accHolderName.setValue(aMandate.getAccHolderName());
		this.jointAccHolderName.setValue(aMandate.getJointAccHolderName());
		fillComboBox(this.accType, aMandate.getAccType(), accTypeList, "");
		this.openMandate.setChecked(aMandate.isOpenMandate());
		this.active.setChecked(aMandate.isActive());
		this.startDate.setValue(aMandate.getStartDate());
		this.expiryDate.setValue(aMandate.getExpiryDate());
		this.maxLimit.setValue(PennantAppUtil.formateAmount(aMandate.getMaxLimit(), ccyFormatter));
		this.periodicity.setValue(aMandate.getPeriodicity());
		this.phoneCountryCode.setValue(aMandate.getPhoneCountryCode());
		this.phoneAreaCode.setValue(aMandate.getPhoneAreaCode());
		this.phoneNumber.setValue(aMandate.getPhoneNumber());
		this.umrNumber.setValue(aMandate.getMandateRef());
		this.documentName.setValue(aMandate.getDocumentName());
		if (aMandate.getDocumentName() == null || aMandate.getDocumentName().equals("")) {
			this.btnViewMandateDoc.setVisible(false);
		}
	}

	public void doFillManFinanceExposureDetails(List<FinanceEnquiry> manFinanceExposureDetails) {
		this.listBoxMandateFinExposure.getItems().clear();
		if (manFinanceExposureDetails != null) {
			for (FinanceEnquiry finEnquiry : manFinanceExposureDetails) {
				Listitem item = new Listitem();
				Listcell lc = new Listcell(DateUtility.formatToLongDate(finEnquiry.getFinStartDate()));
				lc.setParent(item);
				lc = new Listcell(finEnquiry.getLovDescFinTypeName());
				lc.setParent(item);
				lc = new Listcell(finEnquiry.getFinReference());
				lc.setParent(item);
				
				BigDecimal totAmt = finEnquiry.getFinCurrAssetValue().add(finEnquiry.getFeeChargeAmt().add(finEnquiry.getInsuranceAmt()));
				lc = new Listcell(PennantAppUtil.amountFormate(totAmt, CurrencyUtil.getFormat(finEnquiry.getFinCcy())));
				lc.setStyle("text-align:right;");
				lc.setParent(item);
				lc = new Listcell(PennantApplicationUtil.amountFormate(finEnquiry.getMaxInstAmount(), CurrencyUtil.getFormat(finEnquiry.getFinCcy())));
				lc.setStyle("text-align:right;");
				lc.setParent(item);
				lc = new Listcell(PennantAppUtil.amountFormate(totAmt.subtract(finEnquiry.getFinRepaymentAmount()), CurrencyUtil.getFormat(finEnquiry.getFinCcy())));
				lc.setStyle("text-align:right;");
				lc.setParent(item);
				lc = new Listcell(finEnquiry.getFinStatus());
				lc.setParent(item);
				this.listBoxMandateFinExposure.appendChild(item);

			}
		}
	}

	/**
	 * when the "View" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnReason(Event event) {
		logger.debug("Entering");
		HashMap<String, Object> arg = new HashMap<String, Object>();
		arg.put("mandateId", mandate.getMandateID());

		Executions.createComponents("/WEB-INF/pages/Mandate/MandateStatusList.zul", null, arg);
		logger.debug("Leaving");
	}

	public void onClick$btnViewMandateDoc(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		mandate.setDocImage(getMandateService().getDocumentManImage(getMandate().getDocumentRef().toString()));

		if (StringUtils.isNotBlank(mandate.getDocumentName()) && mandate.getDocImage() != null
				&& StringUtils.isNotBlank(mandate.getDocImage().toString())) {
			try {
				HashMap<String, Object> map = new HashMap<String, Object>();
				map.put("mandate", mandate);
				Executions.createComponents("/WEB-INF/pages/util/ImageView.zul", null, map);

			} catch (Exception e) {
				logger.debug(e);
			}
		}
		logger.debug("Leaving" + event.toString());
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public Mandate getMandate() {
		return this.mandate;
	}

	public void setMandate(Mandate mandate) {
		this.mandate = mandate;
	}

	public void setMandateService(MandateService mandateService) {
		this.mandateService = mandateService;
	}

	public MandateService getMandateService() {
		return this.mandateService;
	}

	public void setMandateListCtrl(MandateListCtrl mandateListCtrl) {
		this.mandateListCtrl = mandateListCtrl;
	}

	public MandateListCtrl getMandateListCtrl() {
		return this.mandateListCtrl;
	}
}
