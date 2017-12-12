package com.pennant.webui.reports.customer;

import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Menu;
import org.zkoss.zul.Menuitem;
import org.zkoss.zul.Menupopup;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabbox;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Tabpanels;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.ExtendedCombobox;
import com.pennant.app.constants.LengthConstants;
import com.pennant.backend.model.accounts.Accounts;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.service.PagedListService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.search.Filter;
import com.pennant.util.ReportGenerationUtil;
import com.pennant.webui.reports.customer.model.CustomerAccountListModelItemRender;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;

public class CustomerAccountDialogCtrl extends GFCBaseCtrl<Accounts> {
	private static final long serialVersionUID = -6646226859133636932L;
	private static final Logger logger = Logger.getLogger(CustomerAccountDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected  	Window    			window_CustomerAccountDialog; // autoWired

	protected 	Borderlayout 		borderlayout_Account;  		  // autoWired
	protected 	ExtendedCombobox    			custCIF;	          		  // autoWired
	protected 	Textbox				dftBranch;		  			  // autoWired

	protected 	Button    			button_Print;	              // autoWired

	protected 	Listbox				listBoxEnquiryResult;		  // autoWired
	protected 	Paging     			pagingEnquiryList;	          // autoWired
	protected 	Grid      			grid_accountDetails;          // autoWired
	protected 	Menu		 		menu_filter;				 // autoWired
	protected 	Menupopup  			menupopup_filter;			 // autoWired
	protected 	Menuitem 			menuitem;
	private 	Tab 				tab;
	private 	Tabbox				tabbox;

	// not auto wired variables
	protected List<Accounts> accounts;
	protected JdbcSearchObject<Accounts> searchObjAc;
	protected Customer customer = null;
	private transient PagedListService pagedListService;
	private int 		listRows;
	private long		custId;

	/**
	 * default constructor.<br>
	 */
	public CustomerAccountDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "";
	}

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * ZUL-file is called with a parameter for a selected Academic object in a
	 * Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_CustomerAccountDialog(Event event) throws Exception {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_CustomerAccountDialog);

		try {
			if (event.getTarget() != null
					&& event.getTarget().getParent() != null
					&& event.getTarget().getParent().getParent() != null
					&& event.getTarget().getParent().getParent().getParent() != null) {
				tabbox = (Tabbox) event.getTarget().getParent().getParent()
						.getParent().getParent();
				if (tabbox != null) {
					tab = (Tab) tabbox.getFellowIfAny("tab_CustomerAccount");
				}
			}

			doSetFieldProperties();
			doFillFilterList();
			this.borderlayout_Account.setHeight(getBorderLayoutHeight());
			int dialogHeight = grid_accountDetails.getRows()
					.getVisibleItemCount() * 20 + 52;
			int listboxHeight = borderLayoutHeight - dialogHeight;
			listBoxEnquiryResult.setHeight(listboxHeight + "px");
			listRows = Math.round(listboxHeight / 22);
			this.pagingEnquiryList.setDetailed(true);
			this.pagingEnquiryList.setPageSize(listRows);

			// READ OVERHANDED parameters !
			if (arguments.containsKey("custId")) {
				this.custId = (Long) arguments.get("custId");
				doFillAccountDetail(this.custId);
			}

			if (arguments.containsKey("custCIF")) {
				this.custCIF.setValue(String.valueOf(arguments.get("custCIF")));
			}

			if (arguments.containsKey("custShrtName")) {
				this.custCIF.setDescription(String.valueOf(arguments
						.get("custShrtName")));
			}

			if (arguments.containsKey("dftBranch")) {
				this.dftBranch.setValue(String.valueOf(arguments.get("dftBranch")));
			}
		} catch (Exception e) {
			MessageUtil.showError(e);
			this.window_CustomerAccountDialog.onClose();
		}
		logger.debug("Leaving");
	}

	// Helpers

	/** Set the properties of the fields, like maxLength.<br> */
	private void doSetFieldProperties() {
		this.dftBranch.setMaxlength(LengthConstants.LEN_BRANCH);
		this.custCIF.setMaxlength(12);
		this.custCIF.setModuleName("Customer");
		this.custCIF.setValueColumn("CustCIF");
		this.custCIF.setDescColumn("CustShrtName");
		this.custCIF.setValidateColumns(new String[] { "CustCIF" });
	}

	/**
	 * Method to fill menu items 
	 */
	private void doFillFilterList() {
		logger.debug("Entering");
		
		this.menupopup_filter.getChildren().clear();
		menuitem = new Menuitem();
		menuitem.setLabel(Labels.getLabel("label_CustomerFinanceSummary"));
		menuitem.setValue("CFSENQ");
		menuitem.setStyle("font-weight:bold;");
		menuitem.addForward("onClick", this.window_CustomerAccountDialog, "onFilterMenuItem", menuitem);
		this.menupopup_filter.appendChild(menuitem);
		this.menu_filter.setLabel(Labels.getLabel("label_CustomerAccountSummary"));
		
		logger.debug("Leaving");
	}

	/**
	 * Method for OnClick Event on Menu Item Enqiries 
	 * @param event
	 * @throws InterruptedException 
	 */
	public void onFilterMenuItem(ForwardEvent event) throws InterruptedException {
		logger.debug("Entering");

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("custId",this.custId);
		map.put("custCIF",this.custCIF.getValue());
		map.put("custShrtName",this.custCIF.getDescription());
		map.put("dftBranch",this.dftBranch.getValue());

		// call the ZUL-file with the parameters packed in a map
		try {
			if (tab != null) {

				Tabbox tabbox = (Tabbox) tab.getParent().getParent();
				Tab custAcctab = (Tab) tabbox.getFellowIfAny("tab_CustomerAccount");
				Tab custEnqtab = (Tab) tabbox.getFellowIfAny("tab_CustomerEnquiry");

				if (custAcctab != null && custEnqtab == null) {
					this.window_CustomerAccountDialog.onClose();
					custAcctab.close();
				}
				
				if(custEnqtab != null){
					custEnqtab.close();
				}

				final Tab tab = new Tab();
				tab.setId("menu_Item_CustomerEnquiry".trim().replace("menu_Item_", "tab_"));
				tab.setLabel(Labels.getLabel("menu_Item_CustomerEnquiry"));
				tab.setClosable(true);

				tab.addEventListener(Events.ON_CLOSE, new EventListener<Event>() {
					public void onEvent(Event event) throws UiException {
						String pageName = event.getTarget().getId().replace("tab_", "");

						getUserWorkspace().deAllocateAuthorities(pageName);
					}
				});

				tab.setParent(tabbox.getFellow("tabsIndexCenter"));

				final Tabpanels tabpanels = (Tabpanels) tabbox.getFellow("tabsIndexCenter").getFellow("tabpanelsBoxIndexCenter");
				final Tabpanel tabpanel = new Tabpanel();
				tabpanel.setHeight("100%");
				tabpanel.setStyle("padding: 0px;");
				tabpanel.setParent(tabpanels);

				Executions.createComponents("/WEB-INF/pages/Reports/CustomerEnquiryList.zul", tabpanel, map);
				tab.setSelected(true);
			}
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving");
	}

	/**
	 * When user clicks on button "button_Print" button
	 * @param event
	 * @throws InterruptedException 
	 */
	public void onClick$button_Print(Event event) throws InterruptedException{
		logger.debug("Entering " + event.toString());
		if(getAccounts()!=null && getAccounts().size()>0){
			ReportGenerationUtil.generateReport("Sample", customer,
					getAccounts(),true, 1, getUserWorkspace().getLoggedInUser().getUserName(),null);
		}
		logger.debug("Leaving ");
	}
	

	/**
	 * This method fill the CustomerName, Branch and CIF values by using Search box. <br>
	 * @throws Exception 
	 **/
	public void onFulfill$custCIF(Event event)  {
		logger.debug("Entering");

		Object dataObject = custCIF.getObject();
		if (dataObject instanceof String) {
			this.custCIF.setValue(dataObject.toString());
			this.custCIF.setDescription("");
			this.dftBranch.setValue("");
		} else {
			customer = (Customer) dataObject;
			if (customer != null) {
				this.custCIF.setValue(String.valueOf(customer.getCustCIF()));
				this.custCIF.setDescription(customer.getCustShrtName());
				this.dftBranch.setValue(customer.getCustDftBranch());
				this.custId = customer.getCustID();
				doFillAccountDetail(customer.getCustID());
			}
		}
		logger.debug("Leaving");
	}

	/**
	 * This method fill the Account Details by using Search box. <br>
	 * @throws Exception 
	 **/
	public void doFillAccountDetail(long custId) {
		if(custId != Long.MIN_VALUE || custId != 0) {
			pagingEnquiryList.setDetailed(true);
			this.searchObjAc = new JdbcSearchObject<Accounts>(Accounts.class);
			this.searchObjAc.addFilter(new Filter("acCustId", custId,Filter.OP_EQUAL));
			this.searchObjAc.addTabelName("Accounts_AView");
			this.accounts = getPagedListService().getSRBySearchObject(this.searchObjAc).getResult();
			getPagedListWrapper().initList(this.accounts, this.listBoxEnquiryResult, this.pagingEnquiryList);
			this.listBoxEnquiryResult.setItemRenderer(new CustomerAccountListModelItemRender());
		}
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public List<Accounts> getAccounts() {
		return this.accounts;
	}
	public void setAccounts(List<Accounts> accounts) {
		this.accounts = accounts;
	}

	public JdbcSearchObject<Accounts> getSearchObjAc() {
		return searchObjAc;
	}
	public void setSearchObjAc(JdbcSearchObject<Accounts> searchObjAc) {
		this.searchObjAc = searchObjAc;
	}

	public PagedListService getPagedListService() {
		return pagedListService;
	}

	public void setPagedListService(PagedListService pagedListService) {
		this.pagedListService = pagedListService;
	}
}
