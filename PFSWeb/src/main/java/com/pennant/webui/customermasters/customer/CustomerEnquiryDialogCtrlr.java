package com.pennant.webui.customermasters.customer;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Div;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Image;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listgroup;
import org.zkoss.zul.Listgroupfoot;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.North;
import org.zkoss.zul.Progressmeter;
import org.zkoss.zul.Row;
import org.zkoss.zul.South;
import org.zkoss.zul.Space;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.ExtendedCombobox;
import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerAddres;
import com.pennant.backend.model.customermasters.CustomerBankInfo;
import com.pennant.backend.model.customermasters.CustomerChequeInfo;
import com.pennant.backend.model.customermasters.CustomerDetails;
import com.pennant.backend.model.customermasters.CustomerDocument;
import com.pennant.backend.model.customermasters.CustomerEMail;
import com.pennant.backend.model.customermasters.CustomerEmploymentDetail;
import com.pennant.backend.model.customermasters.CustomerExtLiability;
import com.pennant.backend.model.customermasters.CustomerIncome;
import com.pennant.backend.model.customermasters.CustomerPhoneNumber;
import com.pennant.backend.model.customermasters.DirectorDetail;
import com.pennant.backend.model.finance.FinanceEnquiry;
import com.pennant.backend.model.systemmasters.Designation;
import com.pennant.backend.service.customermasters.CustomerDetailsService;
import com.pennant.backend.service.customermasters.DirectorDetailService;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.component.Uppercasebox;
import com.pennant.util.PennantAppUtil;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.MessageUtil;

/**
 * This is the controller class for the /customer.zul file.
 */
public class CustomerEnquiryDialogCtrlr extends GFCBaseCtrl<CustomerDetails> {
	private static final long serialVersionUID = 9031340167587772517L;
	private static final Logger logger = Logger.getLogger(CustomerEnquiryDialogCtrlr.class);

	/*
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_CustomerDialog; // autowired

	protected North north; // autowired
	protected South south; // autowired
	protected Label custCIF; // autowired
	protected Label custCIF2; // autowired
	protected Label custMiddleName; // autowired
	protected Label custLastName; // autowired
	protected Label custFirstName; // autowired
	private Progressmeter basicProgress;
	private Progressmeter kYCProgress;
	private Progressmeter financialProgress;
	private Progressmeter shareHolderProgress;
	private Progressmeter bankingProgress;
	protected Label custDOB; // autowired
	protected Label custDOBDOI; // autowired
	protected Label custSegment; // autowired
	protected Label custDftBranch; // autowired
	protected Label custDftBranchDesc; // autowired
	protected Label custDftBranchDesc2; // autowired
	protected Label custGroupId; // autowired
	protected Label custGroupIdDesc; // autowired
	protected Label custCRCPR;
	protected Label custCRCPR2;
	protected Label address1;
	protected Label custSectorDesc;

	protected Label custSubSegment; // autowired
	protected Label motherMaidenName; // autowired
	protected Label custSts; // autowired
	protected Label custMaritalStsDesc; // autowired
	protected Label target; // autowired
	protected Label custCoreBank; // autowired
	protected Label custSalutationCode; // autowired
	protected Label custShrtName; // autowired
	protected Label custShrtName1; // autowired
	protected Label custShrtName2; // autowired
	protected Label custShrtName3; // autowired
	protected Label custShrtName4; // autowired
	protected Label custShrtName5; // autowired
	protected Label custPhoneNumber; // autowired
	protected Label recordStatus1; // autowired
	protected Label recordStatus2; // autowired
	protected Label recordStatus3; // autowired
	protected Label recordStatus4; // autowired
	protected Label recordStatus5; // autowired
	protected Label recordType1; // autowired
	protected Hbox hbox_empDetails; // autowired
	protected Groupbox gb_information; // autowired

	protected Label custArabicName; // autowired
	protected Space space_CustArabicName; // autowired
	protected Label custLng; // autowired
	protected Label custSector; // autowired
	protected Label custIndustryDesc; // autowired
	protected Label custCOB; // autowired
	protected Label custCOBDesc; // autowired
	protected ExtendedCombobox custNationality; // autowired
	protected Label custGenderCodeDesc; // autowired
	protected Label noOfDependents; // autowired
	protected Label custCtgCode; // autowired
	protected Checkbox salaryTransferred; // autowiredOdoed
	protected Label custTypeCode; // autowired
	protected Label custTypeCodeDesc; // autowired
	protected Label custBaseCcy; // autowired
	protected Label custBaseCcyDesc; // autowired
	protected Image salariedCustomer; // autowired
	protected Image customerPic; // autowired
	protected Image customerPic1; // autowired
	protected Image customerPic2; // autowired
	protected Image customerPic3; // autowired
	protected Image customerPic4; // autowired
	protected Image customerPic5; // autowired
	protected Label custRO1; // autowired
	protected Label custRO1Desc; // autowired
	protected Uppercasebox eidNumber; // autowired
	protected Label label_CustomerDialog_EIDNumber; // autowired
	protected Textbox custTradeLicenceNum; // autowired
	protected Label custRelatedParty; // autowired
	protected Image custIsStaff; // autowired
	protected Label custStaffID; // autowired
	protected Label custDSACode; // autowired
	protected Label custDSADept; // autowired
	protected Label custRiskCountry; // autowired
	protected Label custRiskCountryDesc; // autowired
	protected Label custParentCountry; // autowired
	protected Label custParentCountryDesc; // autowired

	/** Customer Employer Fields **/
	protected ExtendedCombobox empName; // autowired
	protected Label age; // autowired
	protected Label exp; // autowired

	protected Tab tabBasicDetails;
	protected Tab tabKYCDetails;
	protected Tab tabFinancial;
	protected Tab tabShareHoleder;
	protected Tab tabBankDetails;

	protected Listbox listBoxCustomerDocuments;
	private List<CustomerDocument> customerDocumentDetailList = new ArrayList<CustomerDocument>();

	protected Button btnNew_CustomerAddress;
	protected Listbox listBoxCustomerAddress;
	private List<CustomerAddres> customerAddressDetailList = new ArrayList<CustomerAddres>();

	protected Button btnNew_CustomerPhoneNumber;
	protected Listbox listBoxCustomerPhoneNumbers;
	private List<CustomerPhoneNumber> customerPhoneNumberDetailList = new ArrayList<CustomerPhoneNumber>();
	protected Listheader listheader_CustPhone_RecordStatus;
	protected Listheader listheader_CustPhone_RecordType;
	private List<CustomerPhoneNumber> phoneNumberList = new ArrayList<CustomerPhoneNumber>();

	protected Listbox listBoxCustomerEmails;
	private List<CustomerEMail> customerEmailDetailList = new ArrayList<CustomerEMail>();

	protected Listbox listBoxCustomerBankInformation;
	private List<CustomerBankInfo> customerBankInfoDetailList = new ArrayList<CustomerBankInfo>();

	protected Listbox listBoxCustomerChequeInformation;
	private List<CustomerChequeInfo> customerChequeInfoDetailList = new ArrayList<CustomerChequeInfo>();

	protected Listbox listBoxCustomerFinExposure;

	private List<ValueLabel> countryList;
	private List<ValueLabel> docTypeList;

	protected Listbox listBoxCustomerExternalLiability;
	private List<CustomerExtLiability> customerExtLiabilityDetailList = new ArrayList<CustomerExtLiability>();

	protected Listheader listheader_JointCust;

	// Customer Employment List
	protected Row row_EmploymentDetails;
	protected Button btnNew_CustomerEmploymentDetail;
	protected Listbox listBoxCustomerEmploymentDetail;
	protected Listheader listheader_CustEmp_RecordStatus;
	protected Listheader listheader_CustEmp_RecordType;
	private List<CustomerEmploymentDetail> customerEmploymentDetailList = new ArrayList<CustomerEmploymentDetail>();

	// Customer Income details List
	protected Listbox listBoxCustomerIncome;
	protected Listheader listheader_CustInc_RecordStatus;
	protected Listheader listheader_CustInc_RecordType;
	private List<CustomerIncome> incomeList = new ArrayList<CustomerIncome>();

	private CustomerDetails customerDetails; // overhanded per param
	private transient CustomerListCtrl customerListCtrl; // overhanded per param

	// Declaration of Service(s) & DAO(s)
	private transient CustomerDetailsService customerDetailsService;
	private int ccyFormatter = 0;
	private int old_ccyFormatter = 0;
	private String moduleType = "";
	protected Div divKeyDetails;
	protected Grid grid_KYCDetails;
	protected Grid grid_BankDetails;

	private boolean isRetailCustomer = false;

	protected Tabpanel directorDetails;
	// Customer Directory details List
	protected Button btnNew_DirectorDetail;
	protected Listbox listBoxCustomerDirectory;
	protected Listheader listheader_CustDirector_RecordStatus;
	protected Listheader listheader_CustDirector_RecordType;
	private List<DirectorDetail> directorList = new ArrayList<DirectorDetail>();
	protected Label label_CustomerDialog_CustNationality;
	private transient DirectorDetailService directorDetailService;
	Date appDate = DateUtility.getAppDate();
	Date startDate = SysParamUtil.getValueAsDate("APP_DFT_START_DATE");

	private Tabpanel panel = null;
	private Groupbox groupbox = null;
	private boolean isFinanceProcess = false;
	private boolean isNotFinanceProcess = false;
	private boolean isEnqProcess = false;

	private List<CustomerBankInfo> CustomerBankInfoList;

	/**
	 * default constructor.<br>
	 */
	public CustomerEnquiryDialogCtrlr() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "CustomerDialog";
	}

	// Component Events

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * zul-file is called with a parameter for a selected Customer object in a
	 * Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public void onCreate$window_CustomerDialog(Event event) throws Exception {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_CustomerDialog);

		try {

			if (event.getTarget().getParent() != null) {
				if (event.getTarget().getParent() instanceof Tabpanel) {
					panel = (Tabpanel) event.getTarget().getParent();
				} else if (event.getTarget().getParent() instanceof Groupbox) {
					groupbox = (Groupbox) event.getTarget().getParent();
				}
			}

			if (arguments.containsKey("customerDetails")) {
				customerDetails = (CustomerDetails) arguments.get("customerDetails");
				CustomerDetails befImage = new CustomerDetails();
				BeanUtils.copyProperties(customerDetails, befImage);
				customerDetails.setBefImage(befImage);
			}

			if (enqiryModule) {
				moduleType = PennantConstants.MODULETYPE_ENQ;
			}
			Customer customer = customerDetails.getCustomer();
			ccyFormatter = CurrencyUtil.getFormat(customer.getCustBaseCcy());
			old_ccyFormatter = ccyFormatter;

			if (isFinanceProcess || isEnqProcess) {
				if (arguments.containsKey("roleCode")) {
					setRole((String) arguments.get("roleCode"));
					getUserWorkspace().allocateRoleAuthorities((String) arguments.get("roleCode"), "CustomerDialog");
				}
			} else {
				if (PennantConstants.MODULETYPE_ENQ.equals(moduleType) || isNotFinanceProcess) {
					doLoadWorkFlow(false, customer.getWorkflowId(), customer.getNextTaskId());
				} else {
					doLoadWorkFlow(customer.isWorkflow(), customer.getWorkflowId(), customer.getNextTaskId());
				}
			}

			// READ OVERHANDED params !
			// we get the customerListWindow controller. So we have access
			// to it and can synchronize the shown data when we do insert, edit
			// or delete customer here.
			if (arguments.containsKey("customerListCtrl")) {
				customerListCtrl = (CustomerListCtrl) arguments.get("customerListCtrl");
			}

			if (StringUtils.isNotEmpty(customerDetails.getCustomer().getCustCtgCode()) && StringUtils
					.equals(customerDetails.getCustomer().getCustCtgCode(), PennantConstants.PFF_CUSTCTG_INDIV)) {
				isRetailCustomer = true;
			}

			doShowDialog(customerDetails);
			if (arguments.containsKey("ProspectCustomerEnq")) {
				window_CustomerDialog.doModal();
			}
		} catch (Exception e) {
			MessageUtil.showError(e);
			closeDialog();
		}

		logger.debug("Leaving");
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the
	 * readOnly mode accordingly.
	 * 
	 * @param aCustomer
	 * @throws Exception
	 */
	public void doShowDialog(CustomerDetails aCustomerDetails) throws Exception {
		logger.debug("Entering");

		// set Readonly mode accordingly if the object is new or not.
		if (aCustomerDetails.isNewRecord()) {
			btnCtrl.setInitNew();
			// doEdit();
		} else {
			if (isWorkFlowEnabled()) {
				// btnNotes.setVisible(true);
			} else {
				btnCtrl.setInitNew();
				// btnDelete.setVisible(true);
			}
			// doEdit();
		}
		// custCIF.focus();

		try {
			doWriteBeanToComponents(aCustomerDetails);

			setDialog(DialogType.EMBEDDED);
		} catch (UiException e) {
			logger.error("Exception: ", e);
			window_CustomerDialog.onClose();
		} catch (Exception e) {
			throw e;
		}
		logger.debug("Leaving");
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aCustomer
	 *            Customer
	 */

	public void doWriteBeanToComponents(CustomerDetails aCustomerDetails) {
		logger.debug("Entering");
		int i = 0;
		Customer aCustomer = aCustomerDetails.getCustomer();
		custCIF.setValue(aCustomer.getCustCIF());
		custCIF2.setValue(aCustomer.getCustCIF());
		custShrtName.setValue(aCustomer.getCustShrtName());
		if (aCustomer.getCustShrtName() != null) {
			i++;
		}
		custShrtName1.setValue(aCustomer.getCustShrtName());
		custShrtName2.setValue(aCustomer.getCustShrtName());
		custShrtName3.setValue(aCustomer.getCustShrtName());
		custShrtName4.setValue(aCustomer.getCustShrtName());
		custShrtName5.setValue(aCustomer.getCustShrtName());
		if (isRetailCustomer) {
			custDOBDOI.setValue(Labels.getLabel("label_CustomerDialog_CustDOB.value"));
		} else {
			custDOBDOI.setValue(Labels.getLabel("label_CustomerDialog_CustDateOfIncorporation.value"));
		}
		custDOB.setValue(DateUtility.formatDate(aCustomer.getCustDOB(), "dd/MM/yyyy"));
		if (aCustomer.getCustDOB() != null) {
			i++;
		}
		custCtgCode.setValue(aCustomer.getCustCtgCode());
		if (aCustomer.getCustSegment() != null) {
			i++;
		}
		custTypeCode.setValue(aCustomer.getCustTypeCode() + ", ");
		if (aCustomer.getCustSegment() != null) {
			i++;
		}
		custTypeCodeDesc.setValue(aCustomer.getLovDescCustTypeCodeName());
		custDftBranch.setValue(aCustomer.getCustDftBranch());
		if (aCustomer.getCustDftBranch() != null) {
			i++;
		}
		custDftBranch.setValue(aCustomer.getCustDftBranch() + ", ");
		custDftBranchDesc.setValue(aCustomer.getLovDescCustDftBranchName());
		custDftBranchDesc2.setValue(aCustomer.getLovDescCustDftBranchName());
		custSts.setValue(aCustomer.getCustSts());
		custSector.setValue(aCustomer.getCustSector() + ", ");
		if (aCustomer.getCustSector() != null) {
			i++;
		}
		custSectorDesc.setValue(aCustomer.getLovDescCustSectorName());
		custIndustryDesc.setValue(aCustomer.getLovDescCustIndustryName());
		if (aCustomer.getLovDescCustIndustryName() != null) {
			i++;
		}
		motherMaidenName.setValue(aCustomer.getCustMotherMaiden());
		if (aCustomer.getCustSubSegment() == null) {
			motherMaidenName.setStyle("color:orange; font:12px");
			motherMaidenName.setValue("- - - - - - - - -");
		}
		if (aCustomer.getCustMotherMaiden() != null) {
			i++;
		}
		custSegment.setValue(aCustomer.getCustSegment());
		if (aCustomer.getCustSegment() == null) {
			custSegment.setStyle("color:orange; font:12px");
			custSegment.setValue("- - - - - - - - -");
		}
		custSubSegment.setValue(aCustomer.getCustSubSegment());
		if (aCustomer.getCustSubSegment() == null) {
			custSubSegment.setStyle("color:orange; font:12px");
			custSubSegment.setValue("- - - - - - - - -");
		}
		custStaffID.setValue(aCustomer.getCustStaffID());
		if (aCustomer.getCustStaffID() == null) {
			custStaffID.setStyle("color:orange; font:12px");
			custStaffID.setValue("- - - - - - - - -");
		}
		if (aCustomer.isCustIsStaff() == true) {
			custIsStaff.setSrc("images/icons/customerenquiry/activecheck.png");
		} else {
			custIsStaff.setSrc("images/icons/customerenquiry/inactivecheck.png");
		}
		custDSACode.setValue(aCustomer.getCustDSA());
		if (aCustomer.getCustDSA() == null) {
			custDSACode.setStyle("color:orange; font:12px");
			custDSACode.setValue("- - - - - - - - -");
		}
		custDSADept.setValue(aCustomer.getLovDescCustDSADeptName());
		if (aCustomer.getLovDescCustDSADeptName() == null) {
			custDSADept.setStyle("color:orange; font:12px");
			custDSADept.setValue("- - - - - - - - -");
		}
		custPhoneNumber.setValue(aCustomer.getPhoneNumber());
		if (aCustomer.getPhoneNumber() == null) {
			custPhoneNumber.setStyle("color:orange; font:12px");
			custPhoneNumber.setValue("- - - - - - - - -");
		}
		custRelatedParty.setValue(aCustomer.getCustAddlVar83());
		if (aCustomer.getCustAddlVar83() == null) {
			custRelatedParty.setStyle("color:orange; font:12px");
			custRelatedParty.setValue("- - - - - - - - -");
		}
		custMaritalStsDesc.setValue(aCustomer.getLovDescCustMaritalStsName());
		if (aCustomer.getLovDescCustMaritalStsName() != null) {
			i++;
		}
		if (aCustomer.getLovDescCustMaritalStsName() == null) {
			custMaritalStsDesc.setStyle("color:orange; font:12px");
			custMaritalStsDesc.setValue("- - - - - - - - -");
		}
		custSalutationCode.setValue(aCustomer.getCustSalutationCode());
		if (aCustomer.getCustSalutationCode() != null) {
			i++;
		}
		if (aCustomer.getCustSalutationCode() == null) {
			custSalutationCode.setStyle("color:orange; font:12px");
			custSalutationCode.setValue("- - - - - - - - -");
		}
		if (aCustomer.isSalariedCustomer() == true) {
			salariedCustomer.setSrc("images/icons/customerenquiry/activecheck.png");
		} else {
			salariedCustomer.setSrc("images/icons/customerenquiry/inactivecheck.png");
		}
		custRiskCountry.setValue(aCustomer.getCustRiskCountry() + ", ");
		custRiskCountryDesc.setValue(aCustomer.getLovDescCustRiskCountryName());
		custCRCPR.setValue(aCustomer.getCustCRCPR());
		if (aCustomer.getCustCRCPR() != null) {
			i++;
		}
		custCRCPR2.setValue(aCustomer.getCustCRCPR());
		custBaseCcy.setValue(aCustomer.getCustBaseCcy() + ", ");
		custBaseCcyDesc.setValue(CurrencyUtil.getCcyDesc(aCustomer.getCustBaseCcy()));
		if (aCustomer.getCustBaseCcy() != null) {
			i++;
		}
		custRO1.setValue(aCustomer.getCustRO1() + ", ");
		if (aCustomer.getCustRO1() != null) {
			i++;
		}
		custRO1Desc.setValue(aCustomer.getLovDescCustRO1Name());
		custParentCountry.setValue(aCustomer.getCustParentCountry() + ", ");
		custParentCountryDesc.setValue(aCustomer.getLovDescCustParentCountryName());
		if (aCustomer.getCustGroupID() != 0) {
			custGroupId.setValue(String.valueOf(aCustomer.getCustGroupID()) + ", ");
			custGroupIdDesc.setValue(StringUtils.trimToEmpty(aCustomer.getLovDesccustGroupIDName()));
		} else {
			custGroupId.setStyle("color:orange; font:12px");
			custGroupId.setValue("- - - - - - - - -");
		}
		custLng.setValue(aCustomer.getLovDescCustLngName());
		if (aCustomer.getLovDescCustLngName() != null) {
			i++;
		}
		custGenderCodeDesc.setValue(aCustomer.getLovDescCustGenderCodeName());
		if (aCustomer.getLovDescCustGenderCodeName() != null) {
			i++;
		}
		custCoreBank.setValue(aCustomer.getCustCoreBank());
		if (aCustomer.getCustCoreBank() == null) {
			custCoreBank.setStyle("color:orange; font:12px");
			custCoreBank.setValue("- - - - - - - - -");
		}
		noOfDependents.setValue(String.valueOf(aCustomer.getNoOfDependents()));
		custArabicName.setValue(aCustomer.getCustShrtNameLclLng());
		if (aCustomer.getCustShrtNameLclLng() == null) {
			custArabicName.setStyle("color:orange; font:12px");
			custArabicName.setValue("- - - - - - - - -");
		}
		target.setValue(aCustomer.getLovDescTargetName());
		if (aCustomer.getLovDescTargetName() == null) {
			target.setStyle("color:orange; font:12px");
			target.setValue("- - - - - - - - -");
		}
		custCOB.setValue(aCustomer.getCustCOB() + ", ");
		custCOBDesc.setValue(aCustomer.getLovDescCustCOBName());
		getAddressDetails(aCustomerDetails.getAddressList());
		doFillCustomerPhoneNumberDetails(aCustomerDetails.getCustomerPhoneNumList());

		String s = StringUtils.isNotBlank(aCustomer.getRecordType()) ? " for " + aCustomer.getRecordType() : "";
		recordStatus1.setValue(aCustomer.getRecordStatus() + s);
		recordStatus2.setValue(aCustomer.getRecordStatus() + s);
		recordStatus3.setValue(aCustomer.getRecordStatus() + s);
		recordStatus4.setValue(aCustomer.getRecordStatus() + s);
		recordStatus5.setValue(aCustomer.getRecordStatus() + s);

		basicProgress.setValue((i * 100) / 15);
		basicProgress.setStyle("image-height: 5px;");
		kYCProgress.setValue((i * 100) / 15);
		kYCProgress.setStyle("image-height: 5px;");
		financialProgress.setValue((i * 100) / 15);
		financialProgress.setStyle("image-height: 5px;");
		shareHolderProgress.setValue((i * 100) / 15);
		shareHolderProgress.setStyle("image-height: 5px;");
		bankingProgress.setValue((i * 100) / 15);
		bankingProgress.setStyle("image-height: 5px;");

		if (aCustomer.getLovDescCustGenderCodeName() != null && !aCustomer.getLovDescCustGenderCodeName().isEmpty()) {
			if (aCustomer.getLovDescCustGenderCodeName().equalsIgnoreCase("male")) {
				customerPic.setSrc("images/icons/customerenquiry/male.png");
				customerPic1.setSrc("images/icons/customerenquiry/male.png");
				customerPic2.setSrc("images/icons/customerenquiry/male.png");
				customerPic3.setSrc("images/icons/customerenquiry/male.png");
				customerPic4.setSrc("images/icons/customerenquiry/male.png");
				customerPic5.setSrc("images/icons/customerenquiry/male.png");
			}
			if (aCustomer.getLovDescCustGenderCodeName().equalsIgnoreCase("female")) {
				customerPic.setSrc("images/icons/customerenquiry/female.png");
				customerPic1.setSrc("images/icons/customerenquiry/female.png");
				customerPic2.setSrc("images/icons/customerenquiry/female.png");
				customerPic3.setSrc("images/icons/customerenquiry/female.png");
				customerPic4.setSrc("images/icons/customerenquiry/female.png");
				customerPic5.setSrc("images/icons/customerenquiry/female.png");
			}
		}
		if (isRetailCustomer) {
			tabShareHoleder.setVisible(false);
			tabFinancial.setVisible(true);
			hbox_empDetails.setVisible(true);
			listBoxCustomerEmploymentDetail.setVisible(true);
		} else {
			tabShareHoleder.setVisible(true);
			tabFinancial.setVisible(false);
			hbox_empDetails.setVisible(false);
			listBoxCustomerEmploymentDetail.setVisible(false);
		}

		doFillDocumentDetails(aCustomerDetails.getCustomerDocumentsList());
		doFillCustomerAddressDetails(aCustomerDetails.getAddressList());
		doFillCustomerEmploymentDetail(aCustomerDetails.getEmploymentDetailsList());
		doFillCustomerEmailDetails(aCustomerDetails.getCustomerEMailList());
		doFillCustomerIncome(aCustomerDetails.getCustomerIncomeList());

		doFillCustomerBankInfoDetails(aCustomerDetails.getCustomerBankInfoList());
		doFillCustomerChequeInfoDetails(aCustomerDetails.getCustomerChequeInfoList());
		doFillCustomerExtLiabilityDetails(aCustomerDetails.getCustomerExtLiabilityList());
		doFillCustFinanceExposureDetails(aCustomerDetails.getCustFinanceExposureList());
		doFillCustomerDirectory(aCustomerDetails.getCustomerDirectorList());
		doSetShareHoldersDesignationCode(aCustomerDetails.getCustomerDirectorList());

		if (listBoxCustomerIncome.getItemCount() == 0) {

			Listitem listitem = new Listitem();
			listitem.setHeight("50px");
			Listcell lc = new Listcell("--------");
			lc.setStyle("color: #f39a36;");
			listitem.appendChild(lc);
			lc = new Listcell("--------");
			lc.setStyle("color: #f39a36;");
			listitem.appendChild(lc);
			lc = new Listcell("-------");
			lc.setStyle("color: #f39a36;");
			listitem.appendChild(lc);
			lc = new Listcell("--------");
			lc.setStyle("color: #f39a36;");
			listitem.appendChild(lc);
			lc = new Listcell("--------");
			lc.setStyle("color: #f39a36; text-align: right;");
			listitem.appendChild(lc);
			lc = new Listcell("--------");
			lc.setStyle("color: #f39a36; text-align: center;");
			listitem.appendChild(lc);

			listBoxCustomerIncome.appendChild(listitem);
		}

		logger.debug("Leaving");
	}

	public void doFillCustomerEmploymentDetail(List<CustomerEmploymentDetail> custEmploymentDetails) {
		logger.debug("Entering");
		if (custEmploymentDetails != null && !custEmploymentDetails.isEmpty()) {
			customerEmploymentDetailList = custEmploymentDetails;
			for (CustomerEmploymentDetail customerEmploymentDetail : custEmploymentDetails) {
				customerEmploymentDetail.setLovDescCustCIF(getCustomerDetails().getCustomer().getCustCIF());
				Listitem item = new Listitem();
				item.setHeight("50px");
				Listcell lc;
				lc = new Listcell(customerEmploymentDetail.getLovDescCustCIF());
				lc.setStyle("font-size:15px");
				lc.setParent(item);
				lc = new Listcell(customerEmploymentDetail.getLovDesccustEmpName());
				lc.setStyle("font-size:15px");
				lc.setParent(item);
				lc = new Listcell(customerEmploymentDetail.getLovDescCustEmpDesgName());
				lc.setStyle("font-size:15px");
				lc.setParent(item);
				lc = new Listcell(customerEmploymentDetail.getLovDescCustEmpDeptName());
				lc.setStyle("font-size:15px");
				lc.setParent(item);
				lc = new Listcell(customerEmploymentDetail.getLovDescCustEmpTypeName());
				lc.setStyle("font-size:15px");
				lc.setParent(item);
				lc = new Listcell(customerEmploymentDetail.getRecordStatus());
				lc.setStyle("font-size:15px");
				lc.setParent(item);
				lc = new Listcell(PennantJavaUtil.getLabel(customerEmploymentDetail.getRecordType()));
				lc.setStyle("font-size:15px");
				if (customerEmploymentDetail.getRecordType() == null
						|| customerEmploymentDetail.getRecordType().isEmpty()) {
					lc = new Listcell("------------");
					lc.setStyle("color: #f39a36; font-size: 15px;");
				}
				lc.setParent(item);

				item.setAttribute("id", customerEmploymentDetail.getCustID());
				item.setAttribute("empName", customerEmploymentDetail.getCustEmpName());

				item.setAttribute("custEmpId", customerEmploymentDetail.getCustEmpId());
				item.setAttribute("data", customerEmploymentDetail);

				ComponentsCtrl.applyForward(item, "onDoubleClick=onCustomerEmploymentDetailItemDoubleClicked");
				this.listBoxCustomerEmploymentDetail.appendChild(item);
			}
			customerEmploymentDetailList = custEmploymentDetails;
		}

		if (this.listBoxCustomerEmploymentDetail.getItemCount() == 0) {

			Listitem listitem = new Listitem();
			listitem.setHeight("50px");
			Listcell lc = new Listcell("--------");
			lc.setStyle("color: #f39a36;");
			listitem.appendChild(lc);
			lc = new Listcell("--------");
			lc.setStyle("color: #f39a36;");
			listitem.appendChild(lc);
			lc = new Listcell("-------");
			lc.setStyle("color: #f39a36;");
			listitem.appendChild(lc);
			lc = new Listcell("--------");
			lc.setStyle("color: #f39a36;");
			listitem.appendChild(lc);
			lc = new Listcell("--------");
			lc.setStyle("color: #f39a36;");
			listitem.appendChild(lc);
			lc = new Listcell("--------");
			lc.setStyle("color: #f39a36;");
			listitem.appendChild(lc);
			lc = new Listcell("--------");
			lc.setStyle("color: #f39a36;");
			listitem.appendChild(lc);

			this.listBoxCustomerEmploymentDetail.appendChild(listitem);
		}
	}

	public void doFillCustomerIncome(List<CustomerIncome> incomes) {
		logger.debug("Entering");
		setIncomeList(incomes);
		createIncomeGroupList(incomes);
		logger.debug("Leaving");
	}

	private void createIncomeGroupList(List<CustomerIncome> incomes) {
		if (incomes != null && !incomes.isEmpty()) {
			BigDecimal totIncome = BigDecimal.ZERO;
			BigDecimal totExpense = BigDecimal.ZERO;
			Map<String, List<CustomerIncome>> incomeMap = new HashMap<String, List<CustomerIncome>>();
			Map<String, List<CustomerIncome>> expenseMap = new HashMap<String, List<CustomerIncome>>();
			for (CustomerIncome customerIncome : incomes) {
				String category = StringUtils.trimToEmpty(customerIncome.getCategory());
				if (customerIncome.getIncomeExpense().equals(PennantConstants.INCOME)) {
					totIncome = totIncome.add(customerIncome.getCustIncome());
					if (incomeMap.containsKey(category)) {
						incomeMap.get(category).add(customerIncome);
					} else {
						ArrayList<CustomerIncome> list = new ArrayList<CustomerIncome>();
						list.add(customerIncome);
						incomeMap.put(category, list);
					}
				} else {
					totExpense = totExpense.add(customerIncome.getCustIncome());
					if (expenseMap.containsKey(category)) {
						expenseMap.get(category).add(customerIncome);
					} else {
						ArrayList<CustomerIncome> list = new ArrayList<CustomerIncome>();
						list.add(customerIncome);
						expenseMap.put(category, list);
					}
				}
			}
			renderIncomeExpense(incomeMap, totIncome, expenseMap, totExpense, ccyFormatter);
		}
	}

	private void renderIncomeExpense(Map<String, List<CustomerIncome>> incomeMap, BigDecimal totIncome,
			Map<String, List<CustomerIncome>> expenseMap, BigDecimal totExpense, int ccyFormatter) {
		this.listBoxCustomerIncome.getItems().clear();
		Listitem item;
		Listcell cell;
		Listgroup group;
		Checkbox cb;
		if (incomeMap != null) {
			for (String category : incomeMap.keySet()) {
				List<CustomerIncome> list = incomeMap.get(category);
				if (list != null && list.size() > 0) {
					group = new Listgroup();
					group.setHeight("50px");
					cell = new Listcell(list.get(0).getIncomeExpense() + "-" + list.get(0).getLovDescCategoryName());
					cell.setStyle("font-size: 15px;");
					cell.setParent(group);
					this.listBoxCustomerIncome.appendChild(group);
					BigDecimal total = BigDecimal.ZERO;
					for (CustomerIncome customerIncome : list) {
						item = new Listitem();
						item.setHeight("50px");
						cell = new Listcell("");
						cell.setParent(item);
						cell = new Listcell(customerIncome.getLovDescCustIncomeTypeName());
						cell.setStyle("font-size:15px");
						cell.setParent(item);
						total = total.add(customerIncome.getCustIncome());
						cell = new Listcell(PennantAppUtil.amountFormate(customerIncome.getCustIncome(), ccyFormatter));
						cell.setStyle("text-align:right; font-size: 15px;");
						cell.setParent(item);
						cell = new Listcell();
						cb = new Checkbox();
						cb.setDisabled(true);
						cb.setChecked(customerIncome.isJointCust());
						cb.setParent(cell);
						cell.setParent(item);
						cell = new Listcell(customerIncome.getRecordStatus());
						cell.setStyle("text-align:center; font-size: 15px;");
						cell.setParent(item);
						cell = new Listcell(PennantJavaUtil.getLabel(customerIncome.getRecordType()));
						cell.setStyle("font-size:15px");
						if (customerIncome.getRecordType() == null || customerIncome.getRecordType().isEmpty()) {
							cell = new Listcell("------------");
							cell.setStyle("color: #f39a36; font-size: 15px;");
						}
						cell.setParent(item);
						item.setAttribute("data", customerIncome);
						ComponentsCtrl.applyForward(item, "onDoubleClick=onCustomerIncomeItemDoubleClicked");
						this.listBoxCustomerIncome.appendChild(item);
					}
					item = new Listitem();
					item.setHeight("40px");
					cell = new Listcell("Total");
					cell.setStyle("cursor:default;font-size:15px;");
					cell.setParent(item);
					cell = new Listcell(PennantAppUtil.amountFormate(total, ccyFormatter));
					cell.setSpan(2);
					cell.setStyle("font-size:15px; text-align:right;cursor:default");
					cell.setParent(item);
					cell = new Listcell();
					cell.setSpan(3);
					cell.setStyle("cursor:default: font-size:15px;");
					cell.setParent(item);
					this.listBoxCustomerIncome.appendChild(item);
				}
			}
			item = new Listitem();
			item.setHeight("50px");
			cell = new Listcell("Gross Income");
			cell.setStyle("cursor:default; font-size:15px;");
			cell.setParent(item);
			cell = new Listcell(PennantAppUtil.amountFormate(totIncome, ccyFormatter));
			cell.setSpan(2);
			cell.setStyle("font-size:15px; text-align:right;cursor:default");
			cell.setParent(item);
			cell = new Listcell();
			cell.setSpan(3);
			cell.setStyle("cursor:default; font-size:15px;");
			cell.setParent(item);
			this.listBoxCustomerIncome.appendChild(item);
		}
		if (expenseMap != null) {
			for (String category : expenseMap.keySet()) {
				List<CustomerIncome> list = expenseMap.get(category);
				if (list != null) {
					group = new Listgroup();
					group.setHeight("50px");
					cell = new Listcell(list.get(0).getIncomeExpense() + "-" + list.get(0).getLovDescCategoryName());
					cell.setParent(group);
					this.listBoxCustomerIncome.appendChild(group);
					BigDecimal total = BigDecimal.ZERO;
					for (CustomerIncome customerIncome : list) {
						item = new Listitem();
						item.setHeight("50px");
						cell = new Listcell("");
						cell.setParent(item);
						cell = new Listcell(customerIncome.getLovDescCustIncomeTypeName());
						cell.setStyle("font-size:15px;");
						cell.setParent(item);
						total = total.add(customerIncome.getCustIncome());
						cell = new Listcell(PennantAppUtil.amountFormate(customerIncome.getCustIncome(), ccyFormatter));
						cell.setStyle("text-align:right; font-size:15px;");
						cell.setParent(item);
						cell = new Listcell();
						cb = new Checkbox();
						cb.setDisabled(true);
						cb.setChecked(customerIncome.isJointCust());
						cb.setParent(cell);
						cell.setParent(item);
						cell = new Listcell(customerIncome.getRecordStatus());
						cell.setStyle("font-size:15px;");
						cell.setParent(item);
						cell = new Listcell(PennantJavaUtil.getLabel(customerIncome.getRecordType()));
						cell.setStyle("font-size:15px;");
						cell.setParent(item);
						item.setAttribute("data", customerIncome);
						ComponentsCtrl.applyForward(item, "onDoubleClick=onCustomerIncomeItemDoubleClicked");
						this.listBoxCustomerIncome.appendChild(item);
					}
					item = new Listitem();
					item.setHeight("50px");
					cell = new Listcell("Total");
					cell.setStyle("cursor:default; font-size:15px;");
					cell.setParent(item);
					cell = new Listcell(PennantAppUtil.amountFormate(total, ccyFormatter));
					cell.setSpan(2);
					cell.setStyle("font-size:15px;text-align:right;cursor:default");
					cell.setParent(item);
					cell = new Listcell();
					cell.setSpan(2);
					cell.setParent(item);
					cell = new Listcell();
					cell.setStyle("cursor:default");
					cell.setParent(item);
					this.listBoxCustomerIncome.appendChild(item);
				}
			}
			item = new Listitem();
			item.setHeight("50px");
			cell = new Listcell("Gross Expense");
			cell.setStyle("cursor:default; font-size:15px;");
			cell.setParent(item);
			cell = new Listcell(PennantAppUtil.amountFormate(totExpense, ccyFormatter));
			cell.setSpan(2);
			cell.setStyle("text-align:right;cursor:default;  font-size:15px;");
			cell.setParent(item);
			cell = new Listcell();
			cell.setSpan(3);
			cell.setStyle("cursor:default");
			cell.setParent(item);
			this.listBoxCustomerIncome.appendChild(item);
		}
		item = new Listitem();
		item.setHeight("50px");
		cell = new Listcell("Net Income");
		cell.setStyle("font-size:15px;");
		cell.setParent(item);
		cell = new Listcell(PennantAppUtil.amountFormate(totIncome.subtract(totExpense), ccyFormatter));
		cell.setSpan(2);
		cell.setStyle("text-align:right; font-size:15px;");
		cell.setParent(item);
		cell = new Listcell();
		cell.setSpan(3);
		cell.setStyle("cursor:default");
		cell.setParent(item);
		this.listBoxCustomerIncome.appendChild(item);

	}

	public void doFillDocumentDetails(List<CustomerDocument> custDocumentDetails) {
		logger.debug("Entering");
		listBoxCustomerDocuments.getItems().clear();
		if (custDocumentDetails != null) {
			if (!custDocumentDetails.isEmpty()) {
				List<ValueLabel> docTypeList = null;
				for (CustomerDocument customerDocument : custDocumentDetails) {
					Listitem item = new Listitem();
					item.setHeight("50px");
					Listcell lc;
					if (StringUtils.equals(customerDocument.getCustDocCategory(),
							customerDocument.getLovDescCustDocCategory())) {
						if (docTypeList == null) {
							docTypeList = PennantAppUtil.getCustomerDocumentTypesList();
						}
						String desc = PennantAppUtil.getlabelDesc(customerDocument.getCustDocCategory(), docTypeList);
						customerDocument.setLovDescCustDocCategory(desc);
						lc = new Listcell(desc);
					} else {
						lc = new Listcell(customerDocument.getLovDescCustDocCategory());
					}
					lc.setStyle("font-size: 15px");
					lc.setParent(item);
					if (StringUtils.equals(customerDocument.getCustDocCategory(), PennantConstants.CPRCODE)) {
						lc = new Listcell(PennantApplicationUtil.formatEIDNumber(customerDocument.getCustDocTitle()));
					} else {
						lc = new Listcell(customerDocument.getCustDocTitle());
					}
					lc.setStyle("font-size: 15px");
					lc.setParent(item);
					lc = new Listcell(customerDocument.getLovDescCustDocIssuedCountry());
					lc.setStyle("font-size: 15px");
					lc.setParent(item);
					lc = new Listcell(customerDocument.getCustDocSysName());
					lc.setStyle("font-size: 15px");
					lc.setParent(item);
					lc = new Listcell(DateUtility.formatToLongDate(customerDocument.getCustDocIssuedOn()));
					lc.setStyle("font-size: 15px");
					lc.setParent(item);
					lc = new Listcell(DateUtility.formatToLongDate(customerDocument.getCustDocExpDate()));
					lc.setStyle("font-size: 15px");
					lc.setParent(item);
					lc = new Listcell(customerDocument.getRecordStatus());
					lc.setStyle("font-size: 15px");
					lc.setParent(item);
					lc = new Listcell(PennantJavaUtil.getLabel(customerDocument.getRecordType()));
					lc.setStyle("font-size: 15px");
					if (customerDocument.getRecordType() == null || customerDocument.getRecordType().isEmpty()) {
						lc = new Listcell("------------");
						lc.setStyle("color: #f39a36;");
					}
					lc.setParent(item);
					item.setAttribute("data", customerDocument);
					ComponentsCtrl.applyForward(item, "onDoubleClick=onCustomerDocumentItemDoubleClicked");
					listBoxCustomerDocuments.appendChild(item);
				}
			}
			customerDocumentDetailList = custDocumentDetails;
		}
		logger.debug("Leaving");
	}

	public void getAddressDetails(List<CustomerAddres> customerAddresDetails) {
		logger.debug("Entering");
		if (customerAddresDetails != null && !customerAddresDetails.isEmpty()) {
			for (CustomerAddres customerAddress : customerAddresDetails) {
				if (customerAddress.getCustAddrPriority() == 5) {
					this.address1.setValue(customerAddress.getLovDescCustAddrCityName());
				}
			}
		}
		customerAddressDetailList = customerAddresDetails;
	}

	public void doFillCustomerAddressDetails(List<CustomerAddres> customerAddresDetails) {
		logger.debug("Entering");
		this.listBoxCustomerAddress.getItems().clear();
		if (customerAddresDetails != null) {
			for (CustomerAddres customerAddress : customerAddresDetails) {
				Listitem item = new Listitem();
				item.setHeight("50px");
				Listcell lc;
				lc = new Listcell(customerAddress.getLovDescCustAddrTypeName());
				lc.setStyle("font-size:15px");
				lc.setParent(item);
				if (PennantConstants.CITY_FREETEXT) {
					lc = new Listcell(customerAddress.getCustAddrCity());
					lc.setStyle("font-size:15px");
					lc.setParent(item);
				} else {
					lc = new Listcell(customerAddress.getLovDescCustAddrCityName());
					lc.setStyle("font-size:15px");
					lc.setParent(item);
				}
				lc = new Listcell(customerAddress.getRecordStatus());
				lc.setStyle("font-size:15px");
				lc.setParent(item);
				lc = new Listcell(PennantJavaUtil.getLabel(customerAddress.getRecordType()));
				lc.setStyle("font-size:15px");
				if (customerAddress.getRecordType() == null || customerAddress.getRecordType().isEmpty()) {
					lc = new Listcell("------------");
					lc.setStyle("color: #f39a36; font-size: 15px;");
				}
				lc.setParent(item);
				item.setAttribute("data", customerAddress);
				ComponentsCtrl.applyForward(item, "onDoubleClick=onCustomerAddressItemDoubleClicked");
				this.listBoxCustomerAddress.appendChild(item);

			}
			customerAddressDetailList = customerAddresDetails;
		}
		if (this.listBoxCustomerAddress.getItemCount() == 0) {

			Listitem listitem = new Listitem();
			listitem.setHeight("50px");
			Listcell lc = new Listcell("--------");
			lc.setStyle("color: #f39a36;");
			listitem.appendChild(lc);
			lc = new Listcell("--------");
			lc.setStyle("color: #f39a36;");
			listitem.appendChild(lc);
			lc = new Listcell("-------");
			lc.setStyle("color: #f39a36;");
			listitem.appendChild(lc);
			lc = new Listcell("--------");
			lc.setStyle("color: #f39a36;");
			listitem.appendChild(lc);
			lc = new Listcell("--------");
			lc.setStyle("color: #f39a36;");
			listitem.appendChild(lc);

			this.listBoxCustomerAddress.appendChild(listitem);
		}
		logger.debug("Leaving");
	}

	public void doFillCustomerEmailDetails(List<CustomerEMail> customerEmailDetails) {
		logger.debug("Entering");
		this.listBoxCustomerEmails.getItems().clear();
		if (customerEmailDetails != null) {
			for (CustomerEMail customerEMail : customerEmailDetails) {
				Listitem item = new Listitem();
				item.setHeight("50px");
				Listcell lc;
				lc = new Listcell(customerEMail.getLovDescCustCIF());
				lc.setStyle("font-size:15px");
				lc.setParent(item);
				lc = new Listcell(customerEMail.getLovDescCustEMailTypeCode());
				lc.setStyle("font-size:15px");
				lc.setParent(item);
				lc = new Listcell(PennantAppUtil.formateInt(customerEMail.getCustEMailPriority()));
				lc.setStyle("font-size:15px");
				lc.setParent(item);
				lc = new Listcell(customerEMail.getCustEMail());
				lc.setStyle("font-size:15px");
				lc.setParent(item);
				lc = new Listcell(customerEMail.getRecordStatus());
				lc.setStyle("font-size:15px");
				lc.setParent(item);
				lc = new Listcell(PennantJavaUtil.getLabel(customerEMail.getRecordType()));
				lc.setStyle("font-size:15px");
				if (customerEMail.getRecordType() == null || customerEMail.getRecordType().isEmpty()) {
					lc = new Listcell("------------");
					lc.setStyle("color: #f39a36; font-size: 15px;");
				}
				lc.setParent(item);
				item.setAttribute("data", customerEMail);
				ComponentsCtrl.applyForward(item, "onDoubleClick=onCustomerEmailAddressItemDoubleClicked");
				this.listBoxCustomerEmails.appendChild(item);
			}
			customerEmailDetailList = customerEmailDetails;
		}
		logger.debug("Leaving");
	}

	public void doFillCustomerPhoneNumberDetails(List<CustomerPhoneNumber> customerPhoneNumDetails) {
		logger.debug("Entering");
		this.listBoxCustomerPhoneNumbers.getItems().clear();
		if (customerPhoneNumDetails != null) {
			for (CustomerPhoneNumber customerPhoneNumber : customerPhoneNumDetails) {
				Listitem item = new Listitem();
				item.setHeight("50px");
				Listcell lc;
				lc = new Listcell(StringUtils.trimToEmpty(customerPhoneNumber.getPhoneTypeCode()));
				lc.setStyle("font-size:15px");
				lc.setParent(item);
				lc = new Listcell(customerPhoneNumber.getPhoneNumber());
				lc.setStyle("font-size:15px");
				lc.setParent(item);
				lc = new Listcell(customerPhoneNumber.getRecordStatus());
				lc.setStyle("font-size:15px");
				lc.setParent(item);
				lc = new Listcell(PennantJavaUtil.getLabel(customerPhoneNumber.getRecordType()));
				lc.setStyle("font-size:15px");
				if (customerPhoneNumber.getRecordType() == null || customerPhoneNumber.getRecordType().isEmpty()) {
					lc = new Listcell("------------");
					lc.setStyle("color: #f39a36; font-size: 15px;");
				}
				lc.setParent(item);
				item.setAttribute("data", customerPhoneNumber);
				ComponentsCtrl.applyForward(item, "onDoubleClick=onCustomerPhoneNumberItemDoubleClicked");
				this.listBoxCustomerPhoneNumbers.appendChild(item);
			}
			customerPhoneNumberDetailList = customerPhoneNumDetails;
		}
		logger.debug("Leaving");
	}

	public void doFillCustomerBankInfoDetails(List<CustomerBankInfo> customerBankInfoDetails) {
		logger.debug("Entering");
		CustomerBankInfoList = customerBankInfoDetails;
		this.listBoxCustomerBankInformation.getItems().clear();
		if (customerBankInfoDetails != null) {
			for (CustomerBankInfo custBankInfo : customerBankInfoDetails) {
				Listitem item = new Listitem();
				item.setHeight("50px");
				Listcell lc;
				lc = new Listcell(custBankInfo.getLovDescBankName());
				lc.setStyle("font-size:15px");
				lc.setParent(item);
				lc = new Listcell(custBankInfo.getAccountNumber());
				lc.setStyle("font-size:15px");
				lc.setParent(item);
				lc = new Listcell(custBankInfo.getLovDescAccountType());
				lc.setStyle("font-size:15px");
				lc.setParent(item);
				lc = new Listcell(custBankInfo.getRecordStatus());
				lc.setStyle("font-size:15px");
				lc.setParent(item);
				lc = new Listcell(PennantJavaUtil.getLabel(custBankInfo.getRecordType()));
				lc.setStyle("font-size:15px");
				if (custBankInfo.getRecordType() == null || custBankInfo.getRecordType().isEmpty()) {
					lc = new Listcell("------------");
					lc.setStyle("color: #f39a36; font-size: 15px;");
				}
				lc.setParent(item);
				item.setAttribute("data", custBankInfo);
				ComponentsCtrl.applyForward(item, "onDoubleClick=onCustomerBankInfoItemDoubleClicked");
				this.listBoxCustomerBankInformation.appendChild(item);
			}
			customerBankInfoDetailList = customerBankInfoDetails;
		}
		if (this.listBoxCustomerBankInformation.getItemCount() == 0) {

			Listitem listitem = new Listitem();
			listitem.setHeight("50px");
			Listcell lc = new Listcell("----------");
			lc.setStyle("color: #f39a36;");
			listitem.appendChild(lc);
			lc = new Listcell("------------");
			lc.setStyle("color: #f39a36;");
			listitem.appendChild(lc);
			lc = new Listcell("-----------");
			lc.setStyle("color: #f39a36;");
			listitem.appendChild(lc);
			lc = new Listcell("------------");
			lc.setStyle("color: #f39a36;");
			listitem.appendChild(lc);
			lc = new Listcell("------------");
			lc.setStyle("color: #f39a36;");
			listitem.appendChild(lc);

			this.listBoxCustomerBankInformation.appendChild(listitem);
		}
		logger.debug("Leaving");
	}

	public void doFillCustomerChequeInfoDetails(List<CustomerChequeInfo> customerChequeInfoDetails) {
		logger.debug("Entering");
		this.listBoxCustomerChequeInformation.getItems().clear();
		if (customerChequeInfoDetails != null) {
			for (CustomerChequeInfo custChequeInfo : customerChequeInfoDetails) {
				Listitem item = new Listitem();
				item.setHeight("50px");
				Listcell lc;
				lc = new Listcell(
						DateUtility.formateDate(custChequeInfo.getMonthYear(), PennantConstants.monthYearFormat));
				lc.setStyle("font-size:15px");
				lc.setParent(item);
				lc = new Listcell(PennantAppUtil.amountFormate(custChequeInfo.getTotChequePayment(), ccyFormatter));
				lc.setStyle("font-size:15px; text-align:center;");
				lc.setParent(item);
				lc = new Listcell(PennantAppUtil.amountFormate(custChequeInfo.getSalary(), ccyFormatter));
				lc.setStyle("font-size:15px; text-align:center;");
				lc.setParent(item);
				lc = new Listcell(PennantAppUtil.amountFormate(custChequeInfo.getReturnChequeAmt(), ccyFormatter));
				lc.setStyle("font-size:15px; text-align:center;");
				lc.setParent(item);
				lc = new Listcell(String.valueOf(custChequeInfo.getReturnChequeCount()));
				lc.setStyle("font-size:15px");
				lc.setParent(item);
				lc = new Listcell(custChequeInfo.getRecordStatus());
				lc.setStyle("font-size:15px");
				lc.setParent(item);
				lc = new Listcell(PennantJavaUtil.getLabel(custChequeInfo.getRecordType()));
				lc.setStyle("font-size:15px");
				if (custChequeInfo.getRecordType() == null || custChequeInfo.getRecordType().isEmpty()) {
					lc = new Listcell("------------");
					lc.setStyle("color: #f39a36; font-size: 15px;");
				}
				lc.setParent(item);
				item.setAttribute("data", custChequeInfo);
				ComponentsCtrl.applyForward(item, "onDoubleClick=onCustomerChequeInfoItemDoubleClicked");
				this.listBoxCustomerChequeInformation.appendChild(item);

			}
			customerChequeInfoDetailList = customerChequeInfoDetails;
		}
		if (this.listBoxCustomerChequeInformation.getItemCount() == 0) {

			Listitem listitem = new Listitem();
			listitem.setHeight("50px");
			Listcell lc = new Listcell("------------");
			lc.setStyle("color: #f39a36;");
			listitem.appendChild(lc);
			lc = new Listcell("------------");
			lc.setStyle("color: #f39a36;");
			listitem.appendChild(lc);
			lc = new Listcell("-----------");
			lc.setStyle("color: #f39a36;");
			listitem.appendChild(lc);
			lc = new Listcell("------------");
			lc.setStyle("color: #f39a36;");
			listitem.appendChild(lc);
			lc = new Listcell("------------");
			lc.setStyle("color: #f39a36;");
			listitem.appendChild(lc);
			lc = new Listcell("------------");
			lc.setStyle("color: #f39a36;");
			listitem.appendChild(lc);
			lc = new Listcell("--------");
			lc.setStyle("color: #f39a36;");
			listitem.appendChild(lc);

			this.listBoxCustomerChequeInformation.appendChild(listitem);
		}

		logger.debug("Leaving");
	}

	public void doFillCustomerExtLiabilityDetails(List<CustomerExtLiability> customerExtLiabilityDetails) {
		logger.debug("Entering");
		this.listBoxCustomerExternalLiability.getItems().clear();

		BigDecimal originalAmount = BigDecimal.ZERO;
		BigDecimal instalmentAmount = BigDecimal.ZERO;
		BigDecimal outStandingBal = BigDecimal.ZERO;

		if (customerExtLiabilityDetails != null) {
			for (CustomerExtLiability custExtLiability : customerExtLiabilityDetails) {
				Listitem item = new Listitem();
				item.setHeight("50px");
				Listcell lc;
				if (custExtLiability.getFinDate() == null) {
					lc = new Listcell();
					lc.setStyle("font-size: 15px");
				} else {
					lc = new Listcell(DateUtility.formatToLongDate(custExtLiability.getFinDate()));
					lc.setStyle("font-size: 15px");
				}
				lc.setParent(item);
				lc = new Listcell(custExtLiability.getLovDescFinType());
				lc.setStyle("font-size: 15px");
				lc.setParent(item);
				lc = new Listcell(custExtLiability.getLovDescBankName());
				lc.setStyle("font-size: 15px");
				lc.setParent(item);
				originalAmount = originalAmount.add(custExtLiability.getOriginalAmount() == null ? BigDecimal.ZERO
						: custExtLiability.getOriginalAmount());
				lc = new Listcell(PennantAppUtil.amountFormate(custExtLiability.getOriginalAmount(), ccyFormatter));
				lc.setStyle("font-size: 15px; text-align:left;");
				lc.setParent(item);
				instalmentAmount = instalmentAmount.add(custExtLiability.getInstalmentAmount() == null ? BigDecimal.ZERO
						: custExtLiability.getInstalmentAmount());
				lc = new Listcell(PennantAppUtil.amountFormate(custExtLiability.getInstalmentAmount(), ccyFormatter));
				lc.setStyle("font-size: 15px; text-align:left;");
				lc.setParent(item);
				outStandingBal = outStandingBal.add(custExtLiability.getOutStandingBal() == null ? BigDecimal.ZERO
						: custExtLiability.getOutStandingBal());
				lc = new Listcell(PennantAppUtil.amountFormate(custExtLiability.getOutStandingBal(), ccyFormatter));
				lc.setStyle("font-size: 15px; text-align:left;");
				lc.setParent(item);
				lc = new Listcell(custExtLiability.getLovDescFinStatus());
				lc.setStyle("font-size: 15px");
				lc.setParent(item);
				lc = new Listcell(custExtLiability.getRecordStatus());
				lc.setStyle("font-size: 15px");
				lc.setParent(item);
				lc = new Listcell(PennantJavaUtil.getLabel(custExtLiability.getRecordType()));
				lc.setStyle("font-size: 15px");
				if (custExtLiability.getRecordType() == null || custExtLiability.getRecordType().isEmpty()) {
					lc = new Listcell("------------");
					lc.setStyle("color: #f39a36; font-size: 15px;");
				}
				lc.setParent(item);
				item.setAttribute("data", custExtLiability);
				ComponentsCtrl.applyForward(item, "onDoubleClick=onCustomerExtLiabilityItemDoubleClicked");
				this.listBoxCustomerExternalLiability.appendChild(item);

			}
			// add summary list item
			if (this.listBoxCustomerExternalLiability.getItems() != null
					&& !this.listBoxCustomerExternalLiability.getItems().isEmpty()) {
				Listitem item = new Listitem();
				item.setHeight("50px");
				Listcell lc;
				lc = new Listcell(Labels.getLabel("label_CustomerExtLiabilityDialog_Totals.value"));
				lc.setStyle("font-size: 15px");
				lc.setParent(item);
				lc = new Listcell("");
				lc.setParent(item);
				lc = new Listcell("");
				lc.setParent(item);
				lc = new Listcell(PennantAppUtil.amountFormate(originalAmount, ccyFormatter));
				lc.setStyle("font-size: 15px; text-align:left;");
				lc.setParent(item);
				lc = new Listcell(PennantAppUtil.amountFormate(instalmentAmount, ccyFormatter));
				lc.setStyle("font-size: 15px; text-align:left;");
				lc.setParent(item);
				lc = new Listcell(PennantAppUtil.amountFormate(outStandingBal, ccyFormatter));
				lc.setStyle("font-size: 15px; text-align:left;");
				lc.setParent(item);
				lc = new Listcell("");
				lc.setParent(item);
				lc = new Listcell("");
				lc.setParent(item);
				lc = new Listcell("");
				lc.setParent(item);
				item.setAttribute("data", "");
				this.listBoxCustomerExternalLiability.appendChild(item);
			}
			customerExtLiabilityDetailList = customerExtLiabilityDetails;
		}

		if (this.listBoxCustomerExternalLiability.getItemCount() == 0) {

			Listitem listitem = new Listitem();
			listitem.setHeight("50px");
			Listcell lc = new Listcell("------------");
			lc.setStyle("color: #f39a36;");
			listitem.appendChild(lc);
			lc = new Listcell("------------");
			lc.setStyle("color: #f39a36;");
			listitem.appendChild(lc);
			lc = new Listcell("-----------");
			lc.setStyle("color: #f39a36;");
			listitem.appendChild(lc);
			lc = new Listcell("------------");
			lc.setStyle("color: #f39a36;");
			listitem.appendChild(lc);
			lc = new Listcell("------------");
			lc.setStyle("color: #f39a36;");
			listitem.appendChild(lc);
			lc = new Listcell("--------");
			lc.setStyle("color: #f39a36;");
			listitem.appendChild(lc);
			lc = new Listcell("------------");
			lc.setStyle("color: #f39a36;");
			listitem.appendChild(lc);
			lc = new Listcell("------------");
			lc.setStyle("color: #f39a36;");
			listitem.appendChild(lc);
			lc = new Listcell("------------");
			lc.setStyle("color: #f39a36;");
			listitem.appendChild(lc);

			this.listBoxCustomerExternalLiability.appendChild(listitem);
		}
		logger.debug("Leaving");
	}

	public void doFillCustFinanceExposureDetails(List<FinanceEnquiry> custFinanceExposureDetails) {
		logger.debug("Entering");
		this.listBoxCustomerFinExposure.getItems().clear();
		if (custFinanceExposureDetails != null) {
			for (FinanceEnquiry finEnquiry : custFinanceExposureDetails) {

				int format = CurrencyUtil.getFormat(finEnquiry.getFinCcy());
				Listitem item = new Listitem();
				item.setHeight("50px");
				Listcell lc = new Listcell(DateUtility.formatToLongDate(finEnquiry.getFinStartDate()));
				lc.setStyle("font-size:15px");
				lc.setParent(item);
				lc = new Listcell(finEnquiry.getLovDescFinTypeName());
				lc.setStyle("font-size:15px");
				lc.setParent(item);
				lc = new Listcell(finEnquiry.getFinReference());
				lc.setStyle("font-size:15px");
				lc.setParent(item);

				BigDecimal totAmt = finEnquiry.getFinCurrAssetValue()
						.add(finEnquiry.getFeeChargeAmt().add(finEnquiry.getInsuranceAmt()));
				lc = new Listcell(PennantAppUtil.amountFormate(totAmt, format));
				lc.setStyle("font-size:15px; text-align:left;");
				lc.setParent(item);

				lc = new Listcell(PennantApplicationUtil.amountFormate(finEnquiry.getMaxInstAmount(), format));
				lc.setStyle("font-size:15px; text-align:left;");
				lc.setParent(item);
				lc = new Listcell(
						PennantAppUtil.amountFormate(totAmt.subtract(finEnquiry.getFinRepaymentAmount()), format));
				lc.setStyle("font-size:15px; text-align:left;");
				lc.setParent(item);
				lc = new Listcell(finEnquiry.getFinStatus());
				lc.setStyle("font-size:15px");
				lc.setParent(item);
				this.listBoxCustomerFinExposure.appendChild(item);

			}
		}

		if (listBoxCustomerFinExposure.getItemCount() == 0) {

			Listitem listitem = new Listitem();
			listitem.setHeight("50px");
			Listcell lc = new Listcell("------------");
			lc.setStyle("color: #f39a36;");
			listitem.appendChild(lc);
			lc = new Listcell("------------");
			lc.setStyle("color: #f39a36;");
			listitem.appendChild(lc);
			lc = new Listcell("-----------");
			lc.setStyle("color: #f39a36;");
			listitem.appendChild(lc);
			lc = new Listcell("------------");
			lc.setStyle("color: #f39a36;");
			listitem.appendChild(lc);
			lc = new Listcell("------------");
			lc.setStyle("color: #f39a36;");
			listitem.appendChild(lc);
			lc = new Listcell("------------");
			lc.setStyle("color: #f39a36;");
			listitem.appendChild(lc);
			lc = new Listcell("------------");
			lc.setStyle("color: #f39a36;");
			listitem.appendChild(lc);

			this.listBoxCustomerFinExposure.appendChild(listitem);
		}
		logger.debug("Leaving");
	}

	public void doFillCustomerDirectory(List<DirectorDetail> customerDirectory) {
		logger.debug("Entering");

		this.listBoxCustomerDirectory.getItems().clear();
		if (customerDirectory != null && customerDirectory.size() > 0) {
			directorList = customerDirectory;
			Listitem item = new Listitem();
			for (DirectorDetail directorDetail : customerDirectory) {
				if (item instanceof Listgroup) {
					item.appendChild(new Listcell(String.valueOf(directorDetail.getLovDescCustCIF())));
				} else if (item instanceof Listgroupfoot) {
					Listcell cell = new Listcell("");
					cell.setSpan(6);
					item.appendChild(cell);
				} else {
					String name = "";
					if (StringUtils.isNotBlank(directorDetail.getShortName())) {
						name = directorDetail.getShortName();
					} else if (StringUtils.isNotBlank(directorDetail.getFirstName())
							|| StringUtils.isNotBlank(directorDetail.getLastName())) {
						name = (directorDetail.getFirstName() == null ? " " : directorDetail.getFirstName()) + "  "
								+ (directorDetail.getLastName() == null ? " " : directorDetail.getLastName());
					}
					if (StringUtils.trimToEmpty(directorDetail.getCustAddrCountry())
							.equals(StringUtils.trimToEmpty(directorDetail.getLovDescCustAddrCountryName()))) {
						String desc = PennantAppUtil.getlabelDesc(directorDetail.getCustAddrCountry(), countryList);
						directorDetail.setLovDescCustAddrCountryName(desc);
					}
					Listcell lc = new Listcell(name);
					lc.setHeight("50px");
					lc.setStyle("font-size: 15px");
					lc.setParent(item);
					if (StringUtils.isNotBlank(directorDetail.getLovDescCustAddrCountryName())) {
						lc = new Listcell(directorDetail.getCustAddrCountry() + " - "
								+ directorDetail.getLovDescCustAddrCountryName());
					} else {
						lc = new Listcell(directorDetail.getCustAddrCountry());
					}
					lc.setStyle("font-size: 15px");
					lc.setParent(item);
					if (directorDetail.getSharePerc() != null) {
						lc = new Listcell(String.valueOf(directorDetail.getSharePerc().doubleValue()));
						lc.setStyle("font-size: 15px");
						lc.setParent(item);
					}
					if (StringUtils.trimToEmpty(directorDetail.getIdType())
							.equals(StringUtils.trimToEmpty(directorDetail.getLovDescCustDocCategoryName()))) {
						String desc = PennantAppUtil.getlabelDesc(directorDetail.getIdType(), docTypeList);
						directorDetail.setLovDescCustDocCategoryName(desc);
					}
					if (StringUtils.isNotBlank(directorDetail.getLovDescCustDocCategoryName())) {
						lc = new Listcell(
								directorDetail.getIdType() + " - " + directorDetail.getLovDescCustDocCategoryName());
					} else {
						lc = new Listcell(directorDetail.getIdType());
					}
					lc.setStyle("font-size: 15px");
					lc.setParent(item);
					lc = new Listcell(directorDetail.getIdReference());
					lc.setStyle("font-size: 15px");
					lc.setParent(item);
					if (StringUtils.isNotBlank(directorDetail.getLovDescNationalityName())) {
						lc = new Listcell(
								directorDetail.getNationality() + " - " + directorDetail.getLovDescNationalityName());
					} else {
						lc = new Listcell(directorDetail.getNationality());
					}
					lc.setStyle("font-size: 15px");
					lc.setParent(item);
					lc = new Listcell(directorDetail.getRecordStatus());
					lc.setStyle("font-size: 15px");
					lc.setParent(item);
					lc = new Listcell(PennantJavaUtil.getLabel(directorDetail.getRecordType()));
					lc.setStyle("font-size: 15px");
					if (directorDetail.getRecordType() == null || directorDetail.getRecordType().isEmpty()) {
						lc = new Listcell("------------");
						lc.setStyle("color: #f39a36; font-size: 15px;");
					}
					lc.setParent(item);

					item.setAttribute("directorId", directorDetail.getDirectorId());
					item.setAttribute("custID", directorDetail.getCustID());
					item.setAttribute("data", directorDetail);
					ComponentsCtrl.applyForward(item, "onDoubleClick=onDirectorDetailItemDoubleClicked");
				}
				this.listBoxCustomerDirectory.appendChild(item);
			}
		}
		if (this.listBoxCustomerDirectory.getItemCount() == 0) {

			Listitem listitem = new Listitem();
			listitem.setHeight("50px");
			Listcell lc = new Listcell("--------");
			lc.setStyle("color: #f39a36;");
			listitem.appendChild(lc);
			lc = new Listcell("--------");
			lc.setStyle("color: #f39a36;");
			listitem.appendChild(lc);
			lc = new Listcell("-------");
			lc.setStyle("color: #f39a36;");
			listitem.appendChild(lc);
			lc = new Listcell("--------");
			lc.setStyle("color: #f39a36;");
			listitem.appendChild(lc);
			lc = new Listcell("--------");
			lc.setStyle("color: #f39a36;");
			listitem.appendChild(lc);
			lc = new Listcell("--------");
			lc.setStyle("color: #f39a36;");
			listitem.appendChild(lc);
			lc = new Listcell("--------");
			lc.setStyle("color: #f39a36;");
			listitem.appendChild(lc);
			lc = new Listcell("--------");
			lc.setStyle("color: #f39a36;");
			listitem.appendChild(lc);

			this.listBoxCustomerDirectory.appendChild(listitem);
		}

		logger.debug("Leaving");

	}

	public void doSetShareHoldersDesignationCode(List<DirectorDetail> directorList) {
		logger.debug("Entering");
		if (directorList != null) {
			for (DirectorDetail directorDetail : directorList) {
				if (StringUtils.isBlank(directorDetail.getDesignation())
						&& StringUtils.isNotBlank(directorDetail.getLovDescDesignationName())) {
					Designation designation = PennantAppUtil
							.getDesignationDetails(directorDetail.getLovDescDesignationName());
					if (designation != null) {
						directorDetail.setDesignation(designation.getDesgCode());
					}
				}
			}
		}
		logger.debug("Leaving");
	}

	public void onSelect$tabBasicDetails(Event event) {
		this.tabBasicDetails.setImage("images/icons/customerenquiry/basicdetailsorg.png");
		this.tabKYCDetails.setImage("images/icons/customerenquiry/kycdetails.png");
		this.tabFinancial.setImage("images/icons/customerenquiry/financial.png");
		this.tabShareHoleder.setImage("images/icons/customerenquiry/financial.png");
		this.tabBankDetails.setImage("images/icons/customerenquiry/bankingdetails.png");
	}

	public void onSelect$tabKYCDetails(Event event) {
		this.tabKYCDetails.setImage("images/icons/customerenquiry/kycdetailsorg.png");
		this.tabBasicDetails.setImage("images/icons/customerenquiry/basicdetails.png");
		this.tabFinancial.setImage("images/icons/customerenquiry/financial.png");
		this.tabShareHoleder.setImage("images/icons/customerenquiry/financial.png");
		this.tabBankDetails.setImage("images/icons/customerenquiry/bankingdetails.png");
	}

	public void onSelect$tabFinancial(Event event) {
		this.tabFinancial.setImage("images/icons/customerenquiry/financialorg.png");
		this.tabKYCDetails.setImage("images/icons/customerenquiry/kycdetails.png");
		this.tabBasicDetails.setImage("images/icons/customerenquiry/basicdetails.png");
		this.tabShareHoleder.setImage("images/icons/customerenquiry/financial.png");
		this.tabBankDetails.setImage("images/icons/customerenquiry/bankingdetails.png");
	}

	public void onSelect$tabShareHoleder(Event event) {
		this.tabShareHoleder.setImage("images/icons/customerenquiry/financialorg.png");
		this.tabBasicDetails.setImage("images/icons/customerenquiry/basicdetails.png");
		this.tabKYCDetails.setImage("images/icons/customerenquiry/kycdetails.png");
		this.tabFinancial.setImage("images/icons/customerenquiry/financial.png");
		this.tabBankDetails.setImage("images/icons/customerenquiry/bankingdetails.png");
	}

	public void onSelect$tabBankDetails(Event event) {
		this.tabBankDetails.setImage("images/icons/customerenquiry/bankingdetailsorg.png");
		this.tabKYCDetails.setImage("images/icons/customerenquiry/kycdetails.png");
		this.tabBasicDetails.setImage("images/icons/customerenquiry/basicdetails.png");
		this.tabFinancial.setImage("images/icons/customerenquiry/financial.png");
		this.tabShareHoleder.setImage("images/icons/customerenquiry/financial.png");
	}

	/**
	 * The Click event is raised when the Close Button control is clicked.
	 * 
	 * @param event
	 *            An event sent to the event handler of a component.
	 */
	public void onClick$btnClose(Event event) {
		doClose(false);
	}

	public void setCustomerDetailsService(CustomerDetailsService customerDetailsService) {
		this.customerDetailsService = customerDetailsService;
	}

	public void setDirectorDetailService(DirectorDetailService directorDetailService) {
		this.directorDetailService = directorDetailService;
	}

	public List<CustomerIncome> getIncomeList() {
		return incomeList;
	}

	public void setIncomeList(List<CustomerIncome> incomeList) {
		this.incomeList = incomeList;
	}

	public CustomerDetails getCustomerDetails() {
		return customerDetails;
	}

}