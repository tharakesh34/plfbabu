package com.pennant.webui.finance.financemain;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Button;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Row;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.Interface.service.AccountInterfaceService;
import com.pennant.Interface.service.CustomerInterfaceService;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerDetails;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMainExt;
import com.pennant.backend.service.customermasters.CustomerDetailsService;
import com.pennant.backend.service.finance.FinanceMainExtService;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.coreinterface.model.account.InterfaceAccount;
import com.pennant.mq.util.PFFXmlUtil;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pennapps.core.InterfaceException;

public class ReserveCIFDialogCtrl extends GFCBaseCtrl<FinanceDetail> {

	private static final long serialVersionUID = 6162919410852377636L;

	private static final Logger logger = Logger.getLogger(ReserveCIFDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window 			window_ReserveCIFDialog; 		// autoWired

	protected Textbox 			custCIF; 						// autoWired
	protected Label 			label_ReserveCIFDialog_Error; 	// autoWired
	protected Button 			btnProceed; 					// autoWired
	protected Button 			btnReserveCIFClose; 			// autoWire
	protected Button 			btnRetry; 						// autoWire
	
	protected Groupbox 			gb_CreateCIF; 					// autoWire
	protected Row    			row_createCIF1; 				// autoWire
	protected Row 			    row_createCIF2; 				// autoWire
	protected Row 				row_createCIF3; 				// autoWire
	protected Row 				row_CreateAcc1; 				// autoWire
	protected Row 				row_CreateAcc2; 				// autoWire
	protected Label 			label_WindowTitle; 				// autoWire
	

	private String RESERVE_REFNUM = null;
	private String RESERVE_CIF = null;
	private String OLD_CUSTCIF = null;
	private String CREATE_FLAG = null;
	private final String CUST_CIF_EXISTS = "3924";
	/**
	 * default constructor.<br>
	 */
	public ReserveCIFDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "";
	}

	private FinanceMainListCtrl 		financeMainListCtrl;

	private FinanceDetail 				financeDetail;
	private CustomerInterfaceService 	customerInterfaceService;
	private CustomerDetailsService  	customerDetailsService;
	private AccountInterfaceService     accountInterfaceService;
	private FinanceMainExtService       financeMainExtService;

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * ZUL-file is called with a parameter for a selected Currency object in a
	 * Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_ReserveCIFDialog(Event event) throws Exception {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_ReserveCIFDialog);

		try {

		// READ OVERHANDED parameters !
			if (arguments.containsKey("financeDetail")) {
				setFinanceDetail((FinanceDetail) arguments.get("financeDetail"));
			} else {
				setFinanceDetail(null);
			}
			
			// READ OVERHANDED parameters !
			if (arguments.containsKey("financeMainListCtrl")) {
				setFinanceMainListCtrl((FinanceMainListCtrl) arguments.get("financeMainListCtrl"));
			} else {
				setFinanceDetail(null);
			}
			
			// READ OVERHANDED parameters !
			if (arguments.containsKey("CreateFlag")) {
				CREATE_FLAG = (String) arguments.get("CreateFlag");
			}
			
			if(StringUtils.equals(CREATE_FLAG, "CREATECIF")) {
				this.row_createCIF1.setVisible(true);
				this.row_createCIF2.setVisible(true);
				this.row_createCIF3.setVisible(true);
				
				this.row_CreateAcc1.setVisible(false);
				this.row_CreateAcc2.setVisible(false);
				this.label_WindowTitle.setValue(Labels.getLabel("TITLE_CUSTOMER_CREATE"));
			} else {
				this.row_createCIF1.setVisible(false);
				this.row_createCIF2.setVisible(false);
				this.row_createCIF3.setVisible(false);
				this.row_CreateAcc1.setVisible(true);
				this.row_CreateAcc2.setVisible(true);
				this.label_WindowTitle.setValue(Labels.getLabel("TITLE_ACCOUNT_CREATE"));
			}
			this.window_ReserveCIFDialog.doModal();
		} catch (Exception e) {
			MessageUtil.showError(e);
			this.window_ReserveCIFDialog.onClose();
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * If we close the dialog window. <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClick$btnReserveCIFClose(Event event)	throws Exception {
		logger.debug("Entering" + event.toString());
		this.window_ReserveCIFDialog.detach();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "Retry" button is clicked. <br>
	 * 
	 * @param event
	 * @throws Exception 
	 */
	public void onClick$btnRetry(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		
		// create new customer Account in T24
		try {
			createAccount();
		} catch(InterfaceException pfe) {
			MessageUtil.showError(pfe);
			return;
		}
		logger.debug("Leaving" + event.toString());
	}
	
	/**
	 * when the "Proceed" button is clicked. <br>
	 * 
	 * @param event
	 * @throws Exception 
	 */
	public void onClick$btnProceed(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		try {
			
			this.custCIF.setConstraint(new PTStringValidator(Labels.getLabel("label_ReserveCIFDialog_CustCIF.value"),
					PennantRegularExpressions.REGEX_NUMERIC, true, 7));
			String newCustCIF = null;
			try {
				if(!StringUtils.isBlank(this.custCIF.getValue())) {
					newCustCIF = this.custCIF.getValue();
					RESERVE_CIF = newCustCIF;
				}
			} catch(WrongValueException we) {
				this.custCIF.setConstraint("");
				this.label_ReserveCIFDialog_Error.setValue(we.getMessage());
				return;
			}
			
			// create CIF process
			boolean isCreateCIF = doCreateCIFProcess(newCustCIF);
			if(isCreateCIF) {
				MessageUtil.showMessage(Labels.getLabel("CUSTOMER_CREATE"));
				
				// check whether customer account created or not
				validateCreateAccount();
				
			} else {
				return;
			}
		} catch(InterfaceException pfe) {
			MessageUtil.showError(pfe);
			return;
		}
		logger.debug("Leaving" + event.toString());
	}

	private void validateCreateAccount() throws Exception {
		logger.debug("Entering");
		
		String finReference = getFinanceDetail().getFinScheduleData().getFinanceMain().getFinReference();
		boolean processFlag = true;
		FinanceMainExt financeMainExt = getFinanceMainExtService().getNstlAccNumber(finReference, processFlag);
		if(StringUtils.isBlank(financeMainExt.getNstlAccNum())) {
			this.row_createCIF1.setVisible(false);
			this.row_createCIF2.setVisible(false);
			this.row_createCIF3.setVisible(false);
			this.row_CreateAcc1.setVisible(true);
			this.row_CreateAcc2.setVisible(true);
			this.label_WindowTitle.setValue(Labels.getLabel("TITLE_ACCOUNT_CREATE"));
			return;
		}
		this.window_ReserveCIFDialog.detach();
		getFinanceMainListCtrl().showDetailView(getFinanceDetail());
		
		logger.debug("Leaving");
	}

	/**
	 * Method for send Create Account request to MDM interface and update in FinanceMainExt table
	 * 
	 * @throws InterfaceException
	 * @throws InterruptedException
	 */
	private void createAccount() throws InterfaceException, InterruptedException {
		logger.debug("Entering");
		
		Customer customer = getFinanceDetail().getCustomerDetails().getCustomer();
		InterfaceAccount interfaceAccount = getAccountInterfaceService().createAccount(customer);

		if(!StringUtils.isBlank(interfaceAccount.getAccountNumber())) {
			updateExtensionDetail(interfaceAccount);
			MessageUtil.showMessage(Labels.getLabel("ACCOUNT_CREATED"));
			try {
				this.window_ReserveCIFDialog.detach();
				getFinanceMainListCtrl().showDetailView(getFinanceDetail());
			} catch (Exception e) {
				
			}
		}
		
		logger.debug("Leaving");
	}

	/**
	 * Process Reserve CIF, Create CIF and Release CIF services
	 * @param newCustCIF 
	 * 
	 * @return
	 * @throws Exception
	 */
	private boolean doCreateCIFProcess(String newCustCIF) throws Exception {
		logger.debug("Entering");
		
		// Send Reserve CIF request to MDM interface
		String resReturnCode = doReserveCIF(newCustCIF);
		
		if(StringUtils.equals(resReturnCode, PFFXmlUtil.CUST_CIF_EXISTS)) {
			this.label_ReserveCIFDialog_Error.setValue(Labels.getLabel("CUSTOMER_CIF_EXISTS"));
			return false;
		} else if(!StringUtils.isBlank(resReturnCode)){
			String createReturnCode = null;
			
			// Send Create CIF request to MDM interface
			createReturnCode = doCreateCIF(resReturnCode);
			
			if(!StringUtils.equals(createReturnCode, PFFXmlUtil.SUCCESS)) {
				doReleaseCIF();
				return false;
			}

			// Setting Customer Core bank ID
			Customer customer = getFinanceDetail().getCustomerDetails().getCustomer();
			customer.setCustCoreBank(customer.getCustCIF());
			getFinanceDetail().getFinScheduleData().getFinanceMain().setLovDescCustCIF(customer.getCustCIF());

			//update customer CIF related tables
			getCustomerDetailsService().updateProspectCustCIF(OLD_CUSTCIF, customer.getCustCIF());

			//save the process details in FinanceMainExt
			FinanceMainExt financeMainExt = new FinanceMainExt();
			financeMainExt.setFinReference(getFinanceDetail().getFinScheduleData().getFinReference());
			financeMainExt.setProcessFlag(true);
			getFinanceMainExtService().saveFinanceMainExtDetails(financeMainExt);

			// create new customer Account in T24
			Customer custDetails = getFinanceDetail().getCustomerDetails().getCustomer();
			InterfaceAccount interfaceAccount = getAccountInterfaceService().createAccount(custDetails);
			if(!StringUtils.isBlank(interfaceAccount.getAccountNumber())) {
				updateExtensionDetail(interfaceAccount);
			}

			return true;
		}
		logger.debug("Leaving");
		return false;
	}

	private void updateExtensionDetail(InterfaceAccount interfaceAccount) {
		logger.debug("Entering");
		
		FinanceMainExt financeMainExt = new FinanceMainExt();
		financeMainExt.setNstlAccNum(interfaceAccount.getAccountNumber());
		financeMainExt.setProcessFlag(true);
		financeMainExt.setFinReference(getFinanceDetail().getFinScheduleData().getFinanceMain().getFinReference());
		
		// save or update the Nstl Account number
		getFinanceMainExtService().saveFinanceMainExtDetails(financeMainExt);
		
		logger.debug("Leaving");
	}

	/**
	 * Send Release CIF request to MDM interface
	 * 
	 * @throws InterfaceException 
	 */
	private void doReleaseCIF() throws InterfaceException {
		logger.debug("Entering");
		
		Customer customer = getFinanceDetail().getCustomerDetails().getCustomer();
		customer.setCustCIF(RESERVE_CIF);
		
		try {
			getCustomerInterfaceService().releaseCIF(customer, RESERVE_REFNUM);
		} catch(InterfaceException pfe) {
			throw new InterfaceException("PTI3001", Labels.getLabel("FAILED_RELEASE_CIF"));
		}
		logger.debug("Leaving");
	}

	/**
	 * Send Create CIF request to MDM interface
	 * 
	 * @param resReturnCode 
	 * @throws InterfaceException 
	 */
	private String doCreateCIF(String resReturnCode) throws InterfaceException {
		logger.debug("Entering");
		CustomerDetails customerDetails = getFinanceDetail().getCustomerDetails();
		customerDetails.setCoreReferenceNum(resReturnCode);
		logger.debug("Leaving");
		
		String returnCode = null;
		try {
			returnCode = getCustomerInterfaceService().createNewCustomer(customerDetails);
		} catch(InterfaceException pfe) {
			String createCIFErrMsg = pfe.getErrorMessage() == null?"":pfe.getErrorMessage();
			try {
				doReleaseCIF();
			} catch(InterfaceException pfe2) {
				throw new InterfaceException(pfe.getErrorCode(), createCIFErrMsg+":"+pfe2.getErrorMessage());
			}
			throw pfe;
		}
		
		return returnCode;
	}

	/**
	 * Send Reserve CIF request to MDM interface
	 * @param newCustCIF 
	 * 
	 * @throws InterfaceException 
	 */
	private String doReserveCIF(String newCustCIF) throws InterfaceException {
		logger.debug("Entering");

		if(!isCustomerExists(newCustCIF)) {
			Customer customer = getFinanceDetail().getCustomerDetails().getCustomer();
			OLD_CUSTCIF = customer.getCustCIF();
			customer.setCustCIF(newCustCIF);
			RESERVE_REFNUM = getCustomerInterfaceService().reserveCIF(customer);
		} else {
			return CUST_CIF_EXISTS;
		}
		
		logger.debug("Leaving");
		
		return RESERVE_REFNUM;
		
	}

	/**
	 * Method for validate user entered CIF is Exists in PFF or not
	 * 
	 */
	private boolean isCustomerExists(String newCIF) {
		logger.debug("Entering");
		
		String custCoreBankId = getCustomerDetailsService().getCustCoreBankIdByCIF(newCIF);
		if(StringUtils.isBlank(custCoreBankId)) {
			return false;
		}
		
		logger.debug("Leaving");
		return true;
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//
	
	public FinanceMainListCtrl getFinanceMainListCtrl() {
		return financeMainListCtrl;
	}

	public void setFinanceMainListCtrl(FinanceMainListCtrl financeMainListCtrl) {
		this.financeMainListCtrl = financeMainListCtrl;
	}

	public FinanceDetail getFinanceDetail() {
		return financeDetail;
	}

	public void setFinanceDetail(FinanceDetail financeDetail) {
		this.financeDetail = financeDetail;
	}
	
	public void setCustomerInterfaceService(CustomerInterfaceService customerInterfaceService) {
		this.customerInterfaceService = customerInterfaceService;
	}

	public CustomerInterfaceService getCustomerInterfaceService() {
		return customerInterfaceService;
	}
	
	public CustomerDetailsService getCustomerDetailsService() {
		return customerDetailsService;
	}
	public void setCustomerDetailsService(CustomerDetailsService customerDetailsService) {
		this.customerDetailsService = customerDetailsService;
	}
	
	public AccountInterfaceService getAccountInterfaceService() {
		return accountInterfaceService;
	}
	public void setAccountInterfaceService(AccountInterfaceService accountInterfaceService) {
		this.accountInterfaceService = accountInterfaceService;
	}

	public FinanceMainExtService getFinanceMainExtService() {
		return financeMainExtService;
	}

	public void setFinanceMainExtService(FinanceMainExtService financeMainExtService) {
		this.financeMainExtService = financeMainExtService;
	}
}
