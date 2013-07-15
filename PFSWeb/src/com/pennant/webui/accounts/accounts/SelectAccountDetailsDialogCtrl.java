package com.pennant.webui.accounts.accounts;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.SuspendNotAllowedException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Label;
import org.zkoss.zul.Longbox;
import org.zkoss.zul.Row;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.Interface.model.IAccounts;
import com.pennant.Interface.service.AccountInterfaceService;
import com.pennant.backend.model.accounts.Accounts;
import com.pennant.backend.model.applicationmaster.Branch;
import com.pennant.backend.model.applicationmaster.Currency;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.rmtmasters.AccountType;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.coreinterface.exception.AccountNotFoundException;
import com.pennant.search.Filter;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.MultiLineMessageBox;
import com.pennant.webui.util.PTMessageUtils;
import com.pennant.webui.util.searchdialogs.ExtendedSearchListBox;
/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * /WEB-INF/pages/Account/Accounts/SelectAccountDetails.zul file. <br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 */
public class SelectAccountDetailsDialogCtrl extends GFCBaseCtrl implements Serializable {

	private static final long serialVersionUID = 8556168885363682933L;

	private final static Logger logger = Logger.getLogger(SelectAccountDetailsDialogCtrl.class);
	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWiredd by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window       window_SelectAccountDetailsDialog;         // autoWired
	protected Longbox      custId;                                    // autoWired
	protected Textbox      lovDescCustCIF;                            // autoWired
	protected Textbox      accType;                                   // autoWired
	protected Textbox      lovDescAccType;                            // autoWired
	protected Textbox      currency;                                  // autoWired             
	protected Textbox      lovDescCurrency;                           // autoWired
	protected Textbox      branchCode;                                // autoWired
	protected Textbox      lovDescBranchCodeName;                     // autoWired
	protected Row          row_CustCIF;                               // autoWired
	protected Label        custShortName;                             // autoWired
	protected JdbcSearchObject<Customer> newSearchObject;
	protected Map<String,Object> dataMap=new HashMap<String, Object>();
	private  Accounts accounts;
	private  Customer aCustomer;
	private  Currency aCurrency;
	private  Branch aBranch;
	private  AccountType accountType; 
	private boolean     isInterAcc=true;
	private transient AccountsListCtrl acountsListCtrl; // over handed per parameters
	private AccountInterfaceService accountInterfaceService;
	/**
	 * default constructor.<br>
	 */
	public SelectAccountDetailsDialogCtrl(){
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //


	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * ZUL-file is called with a parameter for a selected FinanceMain object in a
	 * Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_SelectAccountDetailsDialog(Event event) throws Exception {
		logger.debug("Entering " + event.toString());
		// get the params map that are overhanded by creation.
		final Map<String, Object> args = getCreationArgsMap(event);
		if (args.containsKey("acountsListCtrl")) {
			setAcountsListCtrl((AccountsListCtrl) args.get("acountsListCtrl"));
		} else {
			setAcountsListCtrl(null);
		}

		if (args.containsKey("acounts")) {
			this.accounts = (Accounts) args.get("acounts");
		}

		if(!accounts.isInternalAc()){
			this.row_CustCIF.setVisible(true);
			isInterAcc=false;
		}
		this.window_SelectAccountDetailsDialog.doModal();

	}	
	public void onClick$btnClose(Event event) throws Exception {
		logger.debug("Entering " + event.toString());
		doClearErrorMessage();
		this.window_SelectAccountDetailsDialog.onClose();
		logger.debug("Leaving " + event.toString());
	}
	/**
	 * when the "help" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnHelp(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		PTMessageUtils.showHelpWindow(event, window_SelectAccountDetailsDialog);
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * When user clicks on "btnSearchUsrBranchCode" button
	 * This method displays ExtendedSearchListBox with branch details
	 * @param event
	 */
	public void onClick$btnSearchBranch(Event event){
		logger.debug("Entering  "+event.toString());
		Object dataObject = ExtendedSearchListBox.show(this.window_SelectAccountDetailsDialog,"Branch");
		if (dataObject instanceof String){
			this.branchCode.setValue(dataObject.toString());
			this.lovDescBranchCodeName.setValue("");
		}else{
			Branch details= (Branch) dataObject;
			if (details != null) {
				this.aBranch=details;
				this.branchCode.setValue(details.getLovValue());
				this.lovDescBranchCodeName.setValue(details.getBranchCode()
						+"-"+details.getBranchDesc());
			}
		}
		logger.debug("Leaving"+event.toString());
	}
	/**
	 * To get the AccountType LOV List From RMTAccountTypes Table filter is applied to get non internal account and it's
	 * purpose is movement
	 */

	public void onClick$btnSearchAccType(Event event) {
		logger.debug("Entering" + event.toString());
		Filter[] filters = new Filter[1];

		if(accounts.isInternalAc()){
			filters[0] = new Filter("internalAc", "1", Filter.OP_EQUAL);	
		}else{
			filters[0] = new Filter("internalAc", "0", Filter.OP_EQUAL);
		}


		Object dataObject = ExtendedSearchListBox.show(this.window_SelectAccountDetailsDialog, "AccountType",filters);

		if (dataObject instanceof String) {
			this.accType.setValue(dataObject.toString());
			this.lovDescAccType.setValue("");
		} else {
			AccountType details = (AccountType) dataObject;
			if (details != null) {
				accountType=details;
				this.accType.setValue(details.getAcType());
				this.lovDescAccType.setValue(details.getAcType() + "-" + details.getAcTypeDesc());
			}			
		}
		logger.debug("Leaving " + event.toString());
	}
	/**
	 * To get the currency LOV List From RMTCurrencies Table And Amount is formatted based on the currency
	 */

	public void onClick$btnSearchCurrency(Event event) {
		logger.debug("Entering" + event.toString());
		Object dataObject = ExtendedSearchListBox.show(this.window_SelectAccountDetailsDialog, "Currency");
		if (dataObject instanceof String) {
			this.currency.setValue(dataObject.toString());
			this.lovDescCurrency.setValue("");
		} else {
			Currency details = (Currency) dataObject;
			if (details != null) {
				this.currency.setValue(details.getCcyCode());
				this.lovDescCurrency.setValue(details.getCcyCode() + "-" + details.getCcyDesc());
				this.aCurrency=details;
				// To Format Amount based on the currency
				getAccounts().setLovDescFinFormatter(details.getCcyEditField());

			}
		}
		logger.debug("Leaving" + event.toString());
	}


	/**
	 * When user clicks on button "customerId Search" button
	 * @param event
	 */
	public void onClick$btnSearchCustomer(Event event) throws 
	SuspendNotAllowedException, InterruptedException {
		logger.debug("Entering " + event.toString());
		onLoad();
		logger.debug("Leaving " + event.toString());
	}


	/**
	 * To load the customerSelect filter dialog
	 * @throws SuspendNotAllowedException
	 * @throws InterruptedException
	 */
	private void onLoad() throws SuspendNotAllowedException,
	InterruptedException {
		logger.debug("Entering");
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("DialogCtrl", this);
		map.put("filtertype", "Extended");
		map.put("searchObject",this.newSearchObject);
		Executions.createComponents(
				"/WEB-INF/pages/CustomerMasters/Customer/CustomerSelect.zul",
				null, map);
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
		this.custId.setValue(aCustomer.getCustID());
		this.lovDescCustCIF.setValue(aCustomer.getCustCIF());
		this.custShortName.setValue(aCustomer.getCustShrtName());
		this.aCustomer=aCustomer;
		this.newSearchObject=newSearchObject;
	}
	private void doSetLOVValidation() {
		logger.debug("Entering ");
		this.lovDescAccType.setConstraint("NO EMPTY:" + Labels.getLabel("FIELD_NO_EMPTY"
				,new String[]{Labels.getLabel("label_SelectAccountDetailsDialog_AccType.value")}));

		this.lovDescCurrency.setConstraint("NO EMPTY:" + Labels.getLabel("FIELD_NO_EMPTY"
				,new String[]{Labels.getLabel("label_SelectAccountDetailsDialog_Currency.value")}));

		this.lovDescBranchCodeName.setConstraint("NO EMPTY:" + Labels.getLabel("FIELD_NO_EMPTY"
				,new String[]{Labels.getLabel("label_SelectAccountDetailsDialog_Branch.value")}));
		if(!isInterAcc){
			this.lovDescCustCIF.setConstraint("NO EMPTY:" + Labels.getLabel("FIELD_NO_EMPTY"
					,new String[]{Labels.getLabel("label_SelectAccountDetailsDialog_CustCIF.value")}));
		}
		logger.debug("Leaving ");	
	}
	/**
	 * 
	 */
	private void doRemoveLOVValidation(){
		logger.debug("Entering ");
		this.lovDescAccType.setConstraint("");
		this.lovDescCurrency.setConstraint("");
		this.lovDescBranchCodeName.setConstraint("");
		this.lovDescCustCIF.setConstraint("");

		logger.debug("Leaving ");	
	}

	/**
	 * When user clicks on button "customerId Search" button
	 * @param event
	 */
	public void onClick$btnProceed(Event event) throws 
	SuspendNotAllowedException, InterruptedException {
		logger.debug("Entering " + event.toString());
		doSetLOVValidation();
		doWriteComponentsToBean();
		
		try {
			List<IAccounts> iAccountList = new ArrayList<IAccounts>(1);  
			IAccounts newAccount = new IAccounts();
			newAccount.setAcCustCIF(this.accounts.getLovDescCustCIF());
			newAccount.setAcBranch(this.accounts.getAcBranch());
			newAccount.setAcCcy(this.accounts.getAcCcy());
			newAccount.setFlagCreateIfNF(true);
			newAccount.setFlagCreateNew(false);
			newAccount.setInternalAc(false);
			iAccountList.add(newAccount);//TODO
			newAccount = getAccountInterfaceService().fetchExistAccount(iAccountList,"N",true).get(0);

			final HashMap<String, Object> map = new HashMap<String, Object>();
			this.accounts.setNewRecord(true);
			map.put("acounts", this.accounts);
			map.put("customer",this.aCustomer);
			map.put("accountType", this.accountType);
			map.put("acountsListCtrl",  this.acountsListCtrl);
			this.window_SelectAccountDetailsDialog.onClose();
			try {
				Executions.createComponents("/WEB-INF/pages/Account/Accounts/AccountsDialog.zul",null,map);
			} catch (final Exception e) {
				logger.error("onOpenWindow:: error opening window / " + e.getMessage());
				PTMessageUtils.showErrorMessage(e.toString());
			}
		} catch (AccountNotFoundException e1) {
			MultiLineMessageBox.show("Account Number Not Created", 
					Labels.getLabel("message.Error"), MultiLineMessageBox.ABORT, MultiLineMessageBox.ERROR);
		}
		logger.debug("Leaving " + event.toString());
	}
	
	private void doWriteComponentsToBean(){
		logger.debug("Entering ");
		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();
		if(!isInterAcc){
			try{
				this.accounts.setLovDescCustCIF(this.lovDescCustCIF.getValue());
				this.accounts.setAcCustId(this.custId.longValue());
				String fName=this.aCustomer.getCustFName()==null?"":this.aCustomer.getCustFName();
				String mName=this.aCustomer.getCustMName()==null?"":this.aCustomer.getCustMName();
				String lName=this.aCustomer.getCustLName()==null?"":this.aCustomer.getCustLName();

				this.accounts.setAcFullName(fName+" "+mName+" "+lName);
				this.accounts.setAcShortName(this.custShortName.getValue());

			}catch(WrongValueException we){
				wve.add(we);
			}
		}
		try{
			this.accounts.setLovDescBranchCodeName(this.lovDescBranchCodeName.getValue());
			this.accounts.setLovDescBranchCodeName(this.aBranch.getBranchDesc());
			this.accounts.setAcBranch(this.branchCode.getValue());

		}catch(WrongValueException we){
			wve.add(we);
		}
		try{

			this.accounts.setLovDescCurrency(this.lovDescCurrency.getValue());
			this.accounts.setLovDescCurrency(this.aCurrency.getCcyDesc());
			this.accounts.setLovDescCcyNumber(this.aCurrency.getCcyNumber());
			this.accounts.setAcCcy(this.aCurrency.getCcyCode());


		}catch(WrongValueException we){
			wve.add(we);
		}
		try{
			this.accounts.setLovDescAccTypeDesc(this.lovDescAccType.getValue());
			this.accounts.setLovDescAccTypeDesc(this.accountType.getAcTypeDesc());
			this.accounts.setAcType(this.accountType.getAcType());
			this.accounts.setInternalAc(this.accountType.isInternalAc());
			this.accounts.setCustSysAc(this.accountType.isCustSysAc());
			this.accounts.setAcPurpose(this.accountType.getAcPurpose());
			this.accounts.setLovDescAcHeadCode(this.accountType.getAcHeadCode());

		}catch(WrongValueException we){
			wve.add(we);
		}
		doRemoveLOVValidation();
		if (wve.size() > 0) {
			doRemoveLOVValidation();
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = (WrongValueException) wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}
		logger.debug("Leaving ");
	}
	/**
	 * This method clears the error messages
	 */
	private void doClearErrorMessage(){
		logger.debug("Entering ");
		this.lovDescAccType.setErrorMessage("");
		this.lovDescBranchCodeName.setErrorMessage("");
		this.lovDescCurrency.setErrorMessage("");
		this.lovDescCustCIF.setErrorMessage("");

		logger.debug("Leaving ");
	}
	public void setAccounts(Accounts accounts) {
		this.accounts = accounts;
	}
	public Accounts getAccounts() {
		return accounts;
	}

	public void setAcountsListCtrl(AccountsListCtrl acountsListCtrl) {
		this.acountsListCtrl = acountsListCtrl;
	}

	public AccountsListCtrl getAcountsListCtrl() {
		return acountsListCtrl;
	}

	public void setAccountInterfaceService(AccountInterfaceService accountInterfaceService) {
		this.accountInterfaceService = accountInterfaceService;
	}

	public AccountInterfaceService getAccountInterfaceService() {
		return accountInterfaceService;
	}
}
