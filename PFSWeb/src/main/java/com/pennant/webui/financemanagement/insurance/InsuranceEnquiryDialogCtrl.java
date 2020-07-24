package com.pennant.webui.financemanagement.insurance;

import java.util.HashMap;
import java.util.Map;

import javax.script.ScriptException;

import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.CurrencyBox;
import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.configuration.VASConfiguration;
import com.pennant.backend.model.configuration.VASRecording;
import com.pennant.backend.model.configuration.VasCustomer;
import com.pennant.backend.model.extendedfield.ExtendedFieldHeader;
import com.pennant.backend.model.extendedfield.ExtendedFieldRender;
import com.pennant.backend.model.finance.FinReceiptHeader;
import com.pennant.backend.model.insurance.InsuranceDetails;
import com.pennant.backend.service.configuration.VASRecordingService;
import com.pennant.backend.service.insurance.InsuranceDetailService;
import com.pennant.backend.util.InsuranceConstants;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.backend.util.VASConsatnts;
import com.pennant.component.extendedfields.ExtendedFieldsGenerator;
import com.pennant.util.PennantAppUtil;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil.DateFormat;
import com.pennanttech.pennapps.web.util.MessageUtil;

public class InsuranceEnquiryDialogCtrl extends GFCBaseCtrl<FinReceiptHeader> {
	private static final long serialVersionUID = 966281186831332116L;
	private static final Logger logger = Logger.getLogger(InsuranceEnquiryDialogCtrl.class);

	protected Window window_InsuranceEnquiryDialog;
	protected Borderlayout borderlayout_Receipt;

	//Window title
	protected Label window_title;

	//Vas Details
	protected Tab insuranceDetailsTab;
	protected Textbox vasProductCode;
	protected Textbox postingAgainst;
	protected Textbox loanReference;
	protected Textbox vasReference;
	protected CurrencyBox vasFee;
	protected Textbox paymentMode;

	protected Tabpanel extendedFieldTabPanel;
	//Status Details
	protected Textbox status;
	protected Checkbox reconciled;
	protected CurrencyBox surrenderAmount;
	protected CurrencyBox claimAmt;
	protected Textbox bflStatus;

	//Tab2
	protected Tab insurancePartnerDetailsTab;

	//Key Details
	protected Textbox finReference;
	protected Textbox insurenceReference;
	protected Textbox custCif;
	protected Textbox policyNumber;
	protected Textbox loanType;
	protected Textbox companyName;

	//Insurance Partner Details
	protected Datebox insStartDate;
	protected Textbox insPendencyRsn;
	protected Datebox insEndDate;
	protected Checkbox insPendencyResReq;
	protected Datebox issDate;
	protected Textbox fpr;
	protected Textbox IssStatus;
	protected Textbox policyStatus;
	protected CurrencyBox insPartnerPremium;
	protected Datebox handOverDate;
	protected Datebox partnerReceviedDate;
	protected Textbox insPendencyRsnCatgry;

	//Tab3
	protected Tab dispatchDetailsTab;
	//Key Details
	protected Textbox finReference1;
	protected Textbox insurenceReference1;
	protected Textbox custCif1;
	protected Textbox policyNumber1;
	protected Textbox loanType1;
	protected Textbox coverNote;
	//Dispatch Details
	protected Textbox podNo;
	protected Textbox dispatchStatus;
	protected Textbox rsnOfReturn;
	protected Textbox podNo1;
	protected Textbox dispatchStatus1;
	protected Textbox rsnOfReturn1;
	protected Textbox podNo2;
	protected Textbox dispatchStatus2;
	protected Textbox rsnOfReturn2;

	protected Datebox dispatchDateAttemt1;
	protected Datebox dispatchDateAttemt2;
	protected Datebox dispatchDateAttemt3;

	//Tab4
	protected Tab discrepancyDetailsTab;
	//Key Details
	protected Textbox finReference2;
	protected Textbox insurenceReference2;
	protected Textbox custCif2;
	protected Textbox policyNumber2;
	protected Textbox loanType2;
	protected Textbox coverNote1;

	//Discrepancy Details
	protected Datebox discrepancyDate;
	protected Datebox pendencyReportDate;
	protected Textbox discrepancyRsn;
	protected Textbox pendencyStatus;
	protected Textbox fpr1;

	private VASRecording vasRecording;
	private InsuranceDetails insuranceDetails;
	private InsuranceEnquiryListCtrl insuranceEnquiryListCtrl;
	private VASRecordingService vasRecordingService;
	private InsuranceDetailService insuranceDetailService;

	public InsuranceEnquiryDialogCtrl() {
		super();
	}

	/**
	 * Before binding the data and calling the dialog window we check, if the ZUL-file is called with a parameter for a
	 * selected Rule object in a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_InsuranceEnquiryDialog(Event event) throws Exception {
		logger.debug(Literal.ENTERING);

		setPageComponents(window_InsuranceEnquiryDialog);

		try {
			if (arguments.containsKey("insuranceDetails")) {
				setInsuranceDetails((InsuranceDetails) arguments.get("insuranceDetails"));
			}

			if (arguments.containsKey("listCtrl")) {
				this.insuranceEnquiryListCtrl = (InsuranceEnquiryListCtrl) arguments.get("listCtrl");
			}

			InsuranceDetails insurenceDetailsByRef = insuranceDetailService
					.getInsurenceDetailsByRef(getInsuranceDetails().getReference(), "_View");

			if (insurenceDetailsByRef != null) {
				insurenceDetailsByRef.setFinReference(this.insuranceDetails.getFinReference());
				insurenceDetailsByRef.setReference(this.insuranceDetails.getReference());
				insurenceDetailsByRef.setPolicyNumber(this.insuranceDetails.getPolicyNumber());
				insurenceDetailsByRef.setvASProviderId(this.insuranceDetails.getvASProviderId());
				insurenceDetailsByRef.setVasProviderDesc(this.insuranceDetails.getVasProviderDesc());
				insurenceDetailsByRef.setPaymentMode(this.insuranceDetails.getPaymentMode());
				BeanUtils.copyProperties(insurenceDetailsByRef, getInsuranceDetails());
			}

			setVasRecording(getVasRecordingService().getVASRecordingForInsurance(getInsuranceDetails().getReference(),
					"", "enquiry", true));
			doSetFieldProperties();
			doWriteBeanToComponents();

			setDialog(DialogType.EMBEDDED);
		} catch (Exception e) {
			MessageUtil.showError(e);
			this.window_InsuranceEnquiryDialog.onClose();
		}

		logger.debug(Literal.LEAVING + event.toString());
	}

	private void doSetFieldProperties() {
		logger.debug(Literal.ENTERING);
		this.insStartDate.setFormat(DateFormat.SHORT_DATE.getPattern());
		this.insEndDate.setFormat(DateFormat.SHORT_DATE.getPattern());
		this.issDate.setFormat(DateFormat.SHORT_DATE.getPattern());
		this.handOverDate.setFormat(DateFormat.SHORT_DATE.getPattern());
		this.partnerReceviedDate.setFormat(DateFormat.SHORT_DATE.getPattern());
		this.discrepancyDate.setFormat(DateFormat.SHORT_DATE.getPattern());
		this.pendencyReportDate.setFormat(DateFormat.SHORT_DATE.getPattern());
		this.dispatchDateAttemt1.setFormat(DateFormat.SHORT_DATE.getPattern());
		this.dispatchDateAttemt2.setFormat(DateFormat.SHORT_DATE.getPattern());
		this.dispatchDateAttemt3.setFormat(DateFormat.SHORT_DATE.getPattern());
		this.vasFee.setProperties(false, getCcyFormat());
		this.insPartnerPremium.setProperties(false, getCcyFormat());
		this.claimAmt.setProperties(false, getCcyFormat());
		this.surrenderAmount.setProperties(false, getCcyFormat());
		logger.debug(Literal.LEAVING);
	}

	private void doWriteBeanToComponents() {
		logger.debug(Literal.ENTERING);

		//Vas Details
		this.vasProductCode.setValue(vasRecording.getProductCode());
		this.postingAgainst.setValue(insuranceDetails.getPostingAgainst());
		this.loanReference.setValue(insuranceDetails.getFinReference());
		this.vasReference.setValue(vasRecording.getVasReference());
		this.vasFee.setValue(PennantApplicationUtil.formateAmount(vasRecording.getFee(), getCcyFormat()));
		this.paymentMode.setValue(insuranceDetails.getPaymentMode());

		appendExtendedFieldDetails(getVasRecording());

		// Status Details
		String key = vasRecording.getVasStatus();
		if (key != null) {
			switch (key) {
			case VASConsatnts.STATUS_REBOOKING:
				this.status.setValue("REBOOKING");
				break;
			case VASConsatnts.STATUS_MAINTAINCE:
				this.status.setValue("MAINTAINCE");
				break;
			case VASConsatnts.STATUS_CANCEL:
				this.status.setValue("CANCEL");
				break;
			case VASConsatnts.STATUS_NORMAL:
				this.status.setValue("ACTIVE");
				break;
			case VASConsatnts.STATUS_SURRENDER:
				this.status.setValue("SURRENDER");
				break;
			default:
				break;
			}
		} else {
			//Nothing to do
		}

		// BFL Status
		this.bflStatus.setValue(vasRecording.getStatus());

		if (InsuranceConstants.RECON_STATUS_AUTO.equals(insuranceDetails.getReconStatus())) {
			this.reconciled.setChecked(true);
		}
		this.claimAmt.setValue(PennantAppUtil.formateAmount(insuranceDetails.getPartnerPremium(), getCcyFormat()));
		this.surrenderAmount.setValue(PennantAppUtil.formateAmount(getVasRecording().getCancelAmt(), getCcyFormat()));

		// Key Details
		this.finReference.setValue(vasRecording.getPrimaryLinkRef());
		this.insurenceReference.setValue(vasRecording.getVasReference());
		VasCustomer vasCustomer = vasRecording.getVasCustomer();
		if (vasCustomer != null) {
			this.custCif.setValue(vasCustomer.getCustCIF().concat("-").concat(vasCustomer.getCustShrtName()));
		}
		this.policyNumber.setValue(insuranceDetails.getPolicyNumber());
		this.loanType.setValue(vasRecording.getFinType());
		this.companyName.setValue(insuranceDetails.getCompanyName());

		// Key Details
		this.finReference1.setValue(vasRecording.getPrimaryLinkRef());
		this.insurenceReference1.setValue(vasRecording.getVasReference());
		if (vasCustomer != null) {
			this.custCif1.setValue(vasCustomer.getCustCIF().concat("-").concat(vasCustomer.getCustShrtName()));
		}
		this.policyNumber1.setValue(insuranceDetails.getPolicyNumber());
		this.loanType1.setValue(vasRecording.getFinType());

		// Key Details
		this.finReference2.setValue(vasRecording.getPrimaryLinkRef());
		this.insurenceReference2.setValue(vasRecording.getVasReference());
		if (vasCustomer != null) {
			this.custCif2.setValue(vasCustomer.getCustCIF().concat("-").concat(vasCustomer.getCustShrtName()));
		}
		this.policyNumber2.setValue(insuranceDetails.getPolicyNumber());
		this.loanType2.setValue(vasRecording.getFinType());

		//Insurance Partner Details
		this.insStartDate.setValue(insuranceDetails.getStartDate());
		this.insEndDate.setValue(insuranceDetails.getEndDate());
		this.issDate.setValue(insuranceDetails.getIssuanceDate());
		this.IssStatus.setValue(insuranceDetails.getIssuanceStatus());
		this.insPartnerPremium
				.setValue(PennantApplicationUtil.formateAmount(insuranceDetails.getInsurancePremium(), getCcyFormat()));
		this.partnerReceviedDate.setValue(insuranceDetails.getPartnerReceivedDate());
		this.insPendencyRsnCatgry.setValue(PennantStaticListUtil.getlabelDesc(
				insuranceDetails.getPendencyReasonCategory(), PennantStaticListUtil.getReconReasonCategory()));

		this.insPendencyRsn.setValue(insuranceDetails.getPendencyReason());
		this.insPendencyResReq.setChecked(insuranceDetails.isInsPendencyResReq());
		this.fpr.setValue(insuranceDetails.getfPR());
		this.policyStatus.setValue(insuranceDetails.getPolicyStatus());
		this.handOverDate.setValue(insuranceDetails.getFormHandoverDate());

		this.coverNote.setValue("");

		//Dispatch Details
		this.dispatchStatus.setValue(insuranceDetails.getDispatchStatus1());
		this.dispatchStatus1.setValue(insuranceDetails.getDispatchStatus2());
		this.dispatchStatus2.setValue(insuranceDetails.getDispatchStatus3());
		this.podNo.setValue(insuranceDetails.getaWBNo1());//FIXME
		this.podNo1.setValue(insuranceDetails.getaWBNo2());
		this.podNo2.setValue(insuranceDetails.getaWBNo3());
		this.rsnOfReturn.setValue(insuranceDetails.getReasonOfRTO1());
		this.rsnOfReturn1.setValue(insuranceDetails.getReasonOfRTO2());
		this.rsnOfReturn2.setValue(insuranceDetails.getReasonOfRTO3());
		this.dispatchDateAttemt1.setValue(insuranceDetails.getDispatchDateAttempt1());
		this.dispatchDateAttemt2.setValue(insuranceDetails.getDispatchDateAttempt2());
		this.dispatchDateAttemt3.setValue(insuranceDetails.getDispatchDateAttempt3());

		//Discrepancy Details
		this.discrepancyDate.setValue(null);
		this.pendencyReportDate.setValue(null);
		this.discrepancyRsn.setValue("");
		this.pendencyStatus.setValue("");
		this.fpr1.setValue("");

		logger.debug(Literal.LEAVING);
	}

	/**
	 * This method is for append extended field details
	 * 
	 * @throws ScriptException
	 */
	private void appendExtendedFieldDetails(VASRecording aVASRecording) {
		logger.debug("Entering");

		// Extended Field Details auto population / Rendering into Screen
		ExtendedFieldsGenerator generator = new ExtendedFieldsGenerator();
		generator.setWindow(this.window_InsuranceEnquiryDialog);
		generator.setTabpanel(extendedFieldTabPanel);
		generator.setRowWidth(220);
		generator.setCcyFormat(getCcyFormat());
		generator.setReadOnly(true);
		VASConfiguration vasConfiguration = aVASRecording.getVasConfiguration();
		ExtendedFieldHeader extendedFieldHeader = vasConfiguration.getExtendedFieldHeader();

		ExtendedFieldRender extendedFieldRender = aVASRecording.getExtendedFieldRender();
		Map<String, Object> fieldValuesMap = null;
		if (extendedFieldRender != null && extendedFieldRender.getMapValues() != null) {
			fieldValuesMap = extendedFieldRender.getMapValues();
		}

		if (fieldValuesMap != null) {
			generator.setFieldValueMap((HashMap<String, Object>) fieldValuesMap);
		}
		try {
			generator.renderWindow(extendedFieldHeader, false);
		} catch (Exception e) {
			logger.error("Exception: ", e);
		}
		logger.debug("Leaving");
	}

	public void onClick$btnClose(Event event) {
		closeDialog();
	}

	public int getCcyFormat() {
		return CurrencyUtil.getFormat(SysParamUtil.getAppCurrency());
	}

	public InsuranceEnquiryListCtrl getInsuranceEnquiryListCtrl() {
		return insuranceEnquiryListCtrl;
	}

	public InsuranceDetails getInsuranceDetails() {
		return insuranceDetails;
	}

	public void setInsuranceDetails(InsuranceDetails insuranceDetails) {
		this.insuranceDetails = insuranceDetails;
	}

	public void setInsuranceEnquiryListCtrl(InsuranceEnquiryListCtrl insuranceEnquiryListCtrl) {
		this.insuranceEnquiryListCtrl = insuranceEnquiryListCtrl;
	}

	public VASRecordingService getVasRecordingService() {
		return vasRecordingService;
	}

	public void setVasRecordingService(VASRecordingService vasRecordingService) {
		this.vasRecordingService = vasRecordingService;
	}

	public InsuranceDetailService getInsuranceDetailService() {
		return insuranceDetailService;
	}

	public void setInsuranceDetailService(InsuranceDetailService insuranceDetailService) {
		this.insuranceDetailService = insuranceDetailService;
	}

	public VASRecording getVasRecording() {
		return vasRecording;
	}

	public void setVasRecording(VASRecording vasRecording) {
		this.vasRecording = vasRecording;
	}

}
