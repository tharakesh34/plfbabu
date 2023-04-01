package com.pennant.webui.customermasters.customer;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.zkoss.util.media.AMedia;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Button;
import org.zkoss.zul.Image;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Menuitem;
import org.zkoss.zul.North;
import org.zkoss.zul.Progressmeter;
import org.zkoss.zul.South;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Window;

import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.collateral.CollateralSetup;
import com.pennant.backend.model.configuration.VASRecording;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerDetails;
import com.pennant.backend.model.customermasters.CustomerDocument;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.util.PennantConstants;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.dms.service.DMSService;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the /customer.zul file.
 */
public class CustomerSummaryViewCtrl extends GFCBaseCtrl<CustomerDetails> {
	private static final long serialVersionUID = 9031340167587772517L;
	private static final Logger logger = LogManager.getLogger(CustomerSummaryViewCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the ZUL-file
	 * are getting autowired by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_CustomerSummaryDialog;
	protected North north;
	protected South south;
	protected Label custMiddleName;
	protected Label custLastName;
	protected Label custShrtName;
	protected Label custShrtName2;
	protected Label recordStatus1;
	private Progressmeter basicProgress;
	protected Image salariedCustomer;
	protected Image customerPic;
	protected Image customerPic1;
	protected Image customerPic2;
	protected Image customerPic3;
	protected Image customerPic4;
	protected Image customerPic5;
	protected Image leftBar;
	protected Menuitem custDetailss;
	protected Menuitem custSummaryy;
	private CustomerDetails customerDetails;

	// Declaration of Service(s) & DAO(s)
	private int ccyFormatter = 0;

	protected Tabpanel directorDetails;
	// Customer Directory details List
	protected Button btnNew_DirectorDetail;
	protected Listbox listBoxCustomerDirectory;
	protected Listheader listheader_CustDirector_RecordStatus;
	protected Listheader listheader_CustDirector_RecordType;
	Date appDate = SysParamUtil.getAppDate();
	Date startDate = SysParamUtil.getValueAsDate("APP_DFT_START_DATE");
	protected Listbox listBoxCustomerLoanDetails;
	protected Listbox listBoxCustomerVasDetails;
	protected Listbox listBoxCustomerCollateralDetails;
	private CustomerViewDialogCtrl customerViewDialogCtrl;
	private boolean isCustPhotoAvail = false;
	private DMSService dMSService;

	/**
	 * default constructor.<br>
	 */
	public CustomerSummaryViewCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "CustomerDialog";
	}

	// Component Events

	/**
	 * Before binding the data and calling the dialog window we check, if the zul-file is called with a parameter for a
	 * selected Customer object in a Map.
	 * 
	 * @param event
	 */
	public void onCreate$window_CustomerSummaryDialog(Event event) {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_CustomerSummaryDialog);

		try {

			if (arguments.containsKey("customerDetails")) {
				customerDetails = (CustomerDetails) arguments.get("customerDetails");
				CustomerDetails befImage = new CustomerDetails();
				BeanUtils.copyProperties(customerDetails, befImage);
				customerDetails.setBefImage(befImage);
			}

			Customer customer = customerDetails.getCustomer();
			ccyFormatter = CurrencyUtil.getFormat(customer.getCustBaseCcy());

			if (arguments.containsKey("customerViewDialogCtrl")) {
				setCustomerViewDialogCtrl((CustomerViewDialogCtrl) arguments.get("customerViewDialogCtrl"));
			}

			doShowDialog(customerDetails);
			if (arguments.containsKey("ProspectCustomerEnq")) {
				window_CustomerSummaryDialog.doModal();
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
	 * It checks if the dialog opens with a new or existing object and set the readOnly mode accordingly.
	 * 
	 * @param aCustomer
	 */
	public void doShowDialog(CustomerDetails aCustomerDetails) {
		logger.debug("Entering");

		// set Readonly mode accordingly if the object is new or not.
		if (aCustomerDetails.isNewRecord()) {
			btnCtrl.setInitNew();
		} else {
			if (isWorkFlowEnabled()) {
			} else {
				btnCtrl.setInitNew();
			}
		}
		try {
			doWriteBeanToComponents(aCustomerDetails);
			this.window_CustomerSummaryDialog.doModal();
			// setDialog(DialogType.EMBEDDED);
		} catch (UiException | IOException e) {
			logger.error("Exception: ", e);
			window_CustomerSummaryDialog.onClose();
		}

		logger.debug("Leaving");
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aCustomer Customer
	 * @throws IOException
	 */

	public void doWriteBeanToComponents(CustomerDetails aCustomerDetails) throws IOException {
		logger.debug("Entering");
		int i = 0;
		Customer aCustomer = aCustomerDetails.getCustomer();
		if (aCustomer.getCustShrtName() != null) {
			i++;
		}
		custShrtName2.setValue(aCustomer.getCustShrtName());
		String s = StringUtils.isNotBlank(aCustomer.getRecordType()) ? " for " + aCustomer.getRecordType() : "";
		recordStatus1.setValue(aCustomer.getRecordStatus() + s);
		basicProgress.setValue((i * 100) / 15);
		basicProgress.setStyle("image-height: 5px;");

		AMedia amedia = null;
		for (CustomerDocument customerDocument : aCustomerDetails.getCustomerDocumentsList()) {
			if (customerDocument.getCustDocCategory().equalsIgnoreCase(PennantConstants.DOC_TYPE_CODE_PHOTO)) {
				if (customerDocument.getCustDocImage() == null) {
					if (customerDocument.getDocRefId() != Long.MIN_VALUE) {
						customerDocument.setCustDocImage(dMSService.getById(customerDocument.getDocRefId()));
					}
				}
				amedia = new AMedia(customerDocument.getCustDocName(), null, null, customerDocument.getCustDocImage());
				BufferedImage img = ImageIO.read(new ByteArrayInputStream(customerDocument.getCustDocImage()));
				customerPic.setContent(img);
				isCustPhotoAvail = true;
				break;
			}
		}

		if (!isCustPhotoAvail) {
			if (aCustomer.getLovDescCustGenderCodeName() != null
					&& !aCustomer.getLovDescCustGenderCodeName().isEmpty()) {
				if (aCustomer.getLovDescCustGenderCodeName().equalsIgnoreCase("male")) {
					customerPic.setSrc("images/icons/customerenquiry/male.png");
				}
				if (aCustomer.getLovDescCustGenderCodeName().equalsIgnoreCase("female")) {
					customerPic.setSrc("images/icons/customerenquiry/female.png");
				}
			}
		}

		if (aCustomer.getLovDescCustGenderCodeName() != null && !aCustomer.getLovDescCustGenderCodeName().isEmpty()) {
			if (aCustomer.getLovDescCustGenderCodeName().equalsIgnoreCase("male")) {
				customerPic.setSrc("images/icons/customerenquiry/male.png");
			}
			if (aCustomer.getLovDescCustGenderCodeName().equalsIgnoreCase("female")) {
				customerPic.setSrc("images/icons/customerenquiry/female.png");
			}
		}
		doFillCustomerLoanDetails(aCustomerDetails.getFinanceMainList());
		doFillCustomerVASDetails(aCustomerDetails.getVasRecordingList());
		doFillCustomerCollateralDetails(aCustomerDetails.getCollateraldetailList());

		logger.debug("Leaving");
	}

	public void doFillCustomerLoanDetails(List<FinanceMain> customerLoanDetails) {
		logger.debug("Entering");
		this.listBoxCustomerLoanDetails.getItems().clear();
		if (customerLoanDetails != null) {
			for (FinanceMain financeMain : customerLoanDetails) {
				Listitem item = new Listitem();
				item.setHeight("50px");
				Listcell lc;
				lc = new Listcell(financeMain.getFinReference());
				lc.setStyle("font-size:15px");
				lc.setParent(item);
				lc = new Listcell(financeMain.getFinType());
				lc.setStyle("font-size:15px");
				lc.setParent(item);
				if (financeMain.getProductCategory() == null || financeMain.getProductCategory().isEmpty()) {
					lc = new Listcell("------------");
					lc.setStyle("color: #f39a36; font-size: 15px;");
				} else {
					lc = new Listcell(financeMain.getProductCategory().toString());
					lc.setStyle("font-size: 15px");
				}
				lc.setParent(item);
				lc = new Listcell(financeMain.getFinCcy());
				lc.setStyle("font-size:15px");
				lc.setParent(item);
				lc = new Listcell(DateUtil.formatToLongDate(financeMain.getMaturityDate()));
				lc.setStyle("font-size:15px");
				lc.setParent(item);
				if (financeMain.getFinBranch() == null || financeMain.getFinBranch().isEmpty()) {
					lc = new Listcell("------------");
					lc.setStyle("color: #f39a36; font-size: 15px;");
				} else {
					lc = new Listcell(financeMain.getFinBranch().toString());
					lc.setStyle("font-size: 15px");
				}
				lc.setParent(item);
				if (financeMain.getFinAmount() == null || financeMain.getFinAmount() == BigDecimal.ZERO) {
					lc = new Listcell("------------");
					lc.setStyle("color: #f39a36; font-size: 15px;");
				} else {
					lc = new Listcell(CurrencyUtil.format(financeMain.getFinAmount(), ccyFormatter));
					lc.setStyle("font-size: 15px");
				}
				lc.setParent(item);

				item.setAttribute("data", financeMain);
				ComponentsCtrl.applyForward(item, "onDoubleClick=onCustomerAddressItemDoubleClicked");
				this.listBoxCustomerLoanDetails.appendChild(item);

			}
		}

		if (this.listBoxCustomerLoanDetails.getItemCount() == 0) {

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

			this.listBoxCustomerLoanDetails.appendChild(listitem);
		}
	}

	public void doFillCustomerCollateralDetails(List<CollateralSetup> customercollateralDetails) {
		logger.debug("Entering");
		this.listBoxCustomerCollateralDetails.getItems().clear();
		if (customercollateralDetails != null) {
			for (CollateralSetup collateralSetup : customercollateralDetails) {
				Listitem item = new Listitem();
				item.setHeight("50px");
				Listcell lc;
				lc = new Listcell(collateralSetup.getCollateralRef());
				lc.setStyle("font-size:15px");
				lc.setParent(item);
				lc = new Listcell(collateralSetup.getCollateralType());
				lc.setStyle("font-size:15px");
				lc.setParent(item);
				lc = new Listcell(collateralSetup.getCollateralCcy());
				lc.setStyle("font-size:15px");
				lc.setParent(item);
				lc = new Listcell(DateUtil.formatToLongDate(collateralSetup.getExpiryDate()));
				lc.setStyle("font-size:15px");
				lc.setParent(item);
				lc = new Listcell(DateUtil.formatToLongDate(collateralSetup.getNextReviewDate()));
				lc.setStyle("font-size:15px");
				lc.setParent(item);
				lc = new Listcell(CurrencyUtil.format(collateralSetup.getCollateralValue(), ccyFormatter));
				lc.setStyle("font-size:15px");
				lc.setParent(item);
				lc = new Listcell(CurrencyUtil.format(collateralSetup.getBankValuation(), ccyFormatter));
				lc.setStyle("font-size:15px");
				lc.setParent(item);
				item.setAttribute("data", collateralSetup);
				ComponentsCtrl.applyForward(item, "onDoubleClick=onCustomerAddressItemDoubleClicked");
				this.listBoxCustomerCollateralDetails.appendChild(item);

			}
		}

		if (this.listBoxCustomerCollateralDetails.getItemCount() == 0) {

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

			this.listBoxCustomerCollateralDetails.appendChild(listitem);
		}

	}

	public void doFillCustomerVASDetails(List<VASRecording> customerVASDetails) {
		logger.debug("Entering");
		this.listBoxCustomerVasDetails.getItems().clear();
		if (customerVASDetails != null) {
			for (VASRecording vasRecording : customerVASDetails) {
				Listitem item = new Listitem();
				item.setHeight("50px");
				Listcell lc;
				lc = new Listcell(vasRecording.getProductCode());
				lc.setStyle("font-size:15px");
				lc.setParent(item);
				lc = new Listcell(vasRecording.getPostingAgainst());
				lc.setStyle("font-size:15px");
				lc.setParent(item);
				lc = new Listcell(vasRecording.getVasReference());
				lc.setStyle("font-size:15px");
				lc.setParent(item);
				lc = new Listcell(vasRecording.getFeePaymentMode());
				lc.setStyle("font-size:15px");
				lc.setParent(item);
				lc = new Listcell(vasRecording.getVasStatus());
				lc.setStyle("font-size:15px");
				lc.setParent(item);
				item.setAttribute("data", vasRecording);
				ComponentsCtrl.applyForward(item, "onDoubleClick=onCustomerAddressItemDoubleClicked");
				this.listBoxCustomerVasDetails.appendChild(item);

			}
		}

		if (this.listBoxCustomerVasDetails.getItemCount() == 0) {

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

			this.listBoxCustomerVasDetails.appendChild(listitem);
		}

	}

	/**
	 * The Click event is raised when the Close Button control is clicked.
	 * 
	 * @param event An event sent to the event handler of a component.
	 */
	public void onClick$btnClose(Event event) {
		doClose(false);
	}

	public void onClick$custSummaryy(Event event) throws InterruptedException, ParseException {
		logger.debug("Entering" + event.toString());

		Map<String, Object> arg = new HashMap<>();
		arg.put("customerDetails", customerDetails);
		arg.put("customerViewDialogCtrl", this);
		if (!this.window_CustomerSummaryDialog.isVisible()) {
			try {
				Executions.createComponents("/WEB-INF/pages/CustomerMasters/Customer/CustomerSummaryView.zul", null,
						arg);
			} catch (Exception e) {
				MessageUtil.showError(e);
			}
		}

		logger.debug("Leaving");
	}

	public void onClick$custDetailss(Event event) throws InterruptedException, ParseException {
		logger.debug("Entering" + event.toString());

		Map<String, Object> arg = new HashMap<>();
		arg.put("customerDetails", customerDetails);
		arg.put("customerViewDialogCtrl", this);
		this.window_CustomerSummaryDialog.onClose();

		try {
			Executions.createComponents("/WEB-INF/pages/CustomerMasters/Customer/customerView.zul", null, arg);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}

		logger.debug("Leaving");
	}

	public CustomerDetails getCustomerDetails() {
		return customerDetails;
	}

	public CustomerViewDialogCtrl getCustomerViewDialogCtrl() {
		return customerViewDialogCtrl;
	}

	public void setCustomerViewDialogCtrl(CustomerViewDialogCtrl customerViewDialogCtrl) {
		this.customerViewDialogCtrl = customerViewDialogCtrl;
	}

	public void setDMSService(DMSService dMSService) {
		this.dMSService = dMSService;
	}
}
