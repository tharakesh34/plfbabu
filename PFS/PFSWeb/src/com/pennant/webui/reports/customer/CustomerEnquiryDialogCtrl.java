package com.pennant.webui.reports.customer;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.zkoss.spring.SpringUtil;
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
import org.zkoss.zul.GroupsModelArray;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
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

import com.pennant.UserWorkspace;
import com.pennant.app.util.ReportGenerationUtil;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.finance.FinanceEnquiry;
import com.pennant.backend.service.PagedListService;
import com.pennant.backend.service.finance.FinanceMainService;
import com.pennant.webui.reports.customer.model.CustomerEnquiryComparator;
import com.pennant.webui.reports.customer.model.CustomerEnquiryListModelItemRender;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.PTMessageUtils;
import com.pennant.webui.util.pagging.PagedListWrapper;
import com.pennant.webui.util.searchdialogs.ExtendedSearchListBox;

public class CustomerEnquiryDialogCtrl extends GFCBaseListCtrl<FinanceEnquiry> implements Serializable {
	
	private static final long serialVersionUID = -6646226859133636932L;
	private final static Logger logger = Logger.getLogger(CustomerEnquiryDialogCtrl.class);
	
	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */

	protected   Window     			window_CustomerEnquiryDialog;// autoWired
	
	protected 	Borderlayout 		borderlayout_Enquiry;  		 // autoWired
	protected 	Label    			custShrtName;	             // autoWired
	protected 	Textbox    			custCIF;	         		 // autoWired
	protected 	Textbox				dftBranch;		 		 	 // autoWired
	
	protected 	Button    			button_Print;	             // autoWired
	protected 	Button				btnSearchCustCIF;			 // autoWired
	
	protected 	Listbox    			listBoxEnquiryResult;	     // autoWired
	protected 	Paging     			pagingEnquiryList;	         // autoWired
	protected 	Grid      			grid_enquiryDetails;         // autoWired
	protected 	Menu		 		menu_filter;				 // autoWired
	protected 	Menupopup  			menupopup_filter;			 // autoWired
	protected 	Menuitem 			menuitem;
	private 	Tab 				tab;
	private 	Tabbox				tabbox;
	
	// not auto wired variables
	List<FinanceEnquiry> financeEnqList = null;
	private FinanceMainService financeMainService;
	private transient PagedListService pagedListService;
	private PagedListWrapper<FinanceEnquiry> finEnqDetailsPagedListWrapper;
	private int 		listRows;
	private long		custId;
	
	/**
	 * default constructor.<br>
	 */
	public CustomerEnquiryDialogCtrl() {
		super();
	}
	
	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * ZUL-file is called with a parameter for a selected Academic object in a
	 * Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_CustomerEnquiryDialog(Event event) throws Exception {
		logger.debug("Entering"+event.toString());
		
		final Map<String, Object> args = getCreationArgsMap(event);
		if (event.getTarget() != null && event.getTarget().getParent() != null
				&& event.getTarget().getParent().getParent()!=null && 
				event.getTarget().getParent().getParent().getParent() != null) {
			tabbox = (Tabbox) event.getTarget().getParent().getParent().getParent().getParent();
			if (tabbox != null) {			
				tab = (Tab)tabbox.getFellowIfAny("tab_CustomerEnquiry");
			}
	    }

		doSetFieldProperties();
		doFillFilterList();
		setFinEnqDetailsPagedListWrapper();
		this.borderlayout_Enquiry.setHeight(getBorderLayoutHeight());
		int dialogHeight =  grid_enquiryDetails.getRows().getVisibleItemCount()* 20 + 52 ; 
		int listboxHeight = borderLayoutHeight-dialogHeight;
		listBoxEnquiryResult.setHeight(listboxHeight+"px");
		listRows = Math.round(listboxHeight/ 22);
		this.pagingEnquiryList.setDetailed(true);
		this.pagingEnquiryList.setPageSize(listRows);
		
		// READ OVERHANDED parameters !
		if (args.containsKey("custId")) {
			this.custId = (Long) args.get("custId");
			doFillFinDetails(this.custId);
		}
		
		if (args.containsKey("custCIF")) {
			this.custCIF.setValue(String.valueOf(args.get("custCIF")));
		}
		
		if (args.containsKey("custShrtName")) {
			this.custShrtName.setValue(String.valueOf(args.get("custShrtName")));
		}
		
		if (args.containsKey("dftBranch")) {
			this.dftBranch.setValue(String.valueOf(args.get("dftBranch")));
		}
		
		logger.debug("Leaving");
	}
	
	/**
	 * SetVisible for components by checking if there's a right for it.
	 */
	@SuppressWarnings("unused")
	private void doCheckRights() {
		logger.debug("Entering");
		getUserWorkspace().alocateAuthorities("CustomerEnquiryDialog");
		this.button_Print.setVisible(getUserWorkspace()
				.isAllowed("button_CustomerEnquiryDialog_PrintList"));
		logger.debug("Leaving");
	}
	
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// ++++++++++++++++++++++++++++++ helpers ++++++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	
	/** Set the properties of the fields, like maxLength.<br> */
	private void doSetFieldProperties() {
		this.dftBranch.setMaxlength(20);
	}
	
	/**
	 * Method to fill menu items 
	 */
	private void doFillFilterList() {
		logger.debug("Entering");
		
		this.menupopup_filter.getChildren().clear();
		menuitem = new Menuitem();
		menuitem.setLabel(Labels.getLabel("label_CustomerAccountSummary"));
		menuitem.setValue("CASENQ");
		menuitem.setStyle("font-weight:bold;");
		menuitem.addForward("onClick", this.window_CustomerEnquiryDialog, "onFilterMenuItem", menuitem);
		this.menupopup_filter.appendChild(menuitem);
		this.menu_filter.setLabel(Labels.getLabel("label_CustomerFinanceSummary"));
		
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
		map.put("custShrtName",this.custShrtName.getValue());
		map.put("dftBranch",this.dftBranch.getValue());
		
		// call the ZUL-file with the parameters packed in a map
		try {
			if (tab != null) {
				
				Tabbox tabbox = (Tabbox) tab.getParent().getParent();
				Tab custEnqtab = (Tab) tabbox.getFellowIfAny("tab_CustomerEnquiry");
				Tab custAcctab = (Tab) tabbox.getFellowIfAny("tab_CustomerAccount");

				if (custEnqtab != null && custAcctab == null) {
					this.window_CustomerEnquiryDialog.onClose();
					custEnqtab.close();
				}
				
				if (custAcctab != null) {
					custAcctab.close();
				}
				
				final Tab tab = new Tab();
				tab.setId("menu_Item_CustomerAccount".trim().replace("menu_Item_", "tab_"));
				tab.setLabel(Labels.getLabel("menu_Item_CustomerAccount"));
				tab.setClosable(true);

				tab.addEventListener(Events.ON_CLOSE, new EventListener<Event>() {
					public void onEvent(Event event) throws UiException {
						String pageName = event.getTarget().getId().replace("tab_", "");
						@SuppressWarnings("deprecation")
						UserWorkspace workspace = UserWorkspace.getInstance();
						workspace.deAlocateAuthorities(pageName);
					}
				});

				tab.setParent(tabbox.getFellow("tabsIndexCenter"));

				final Tabpanels tabpanels = (Tabpanels) tabbox.getFellow("tabsIndexCenter").getFellow("tabpanelsBoxIndexCenter");
				final Tabpanel tabpanel = new Tabpanel();
				tabpanel.setHeight("100%");
				tabpanel.setStyle("padding: 0px;");
				tabpanel.setParent(tabpanels);

				Executions.createComponents("/WEB-INF/pages/Reports/CustomerAccountList.zul", tabpanel, map);
				tab.setSelected(true);
			}
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / " + e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
		}
		
		logger.debug("Leaving");
	}
	
	/**
	 * This method fill the CustomerName and CIF values by using Search box. <br>
	 * @throws Exception 
	 **/
	public void onClick$btnSearchCustCIF(Event event)  {
		logger.debug("Entering");
		Customer custDetails = null;
		
		Object dataObject = ExtendedSearchListBox.show(
				this.window_CustomerEnquiryDialog, "Customer");
		if (dataObject instanceof String) {
			this.custCIF.setValue(dataObject.toString());
			this.custShrtName.setValue("");
			this.dftBranch.setValue("");
		} else {
			custDetails = (Customer) dataObject;
			if (custDetails != null) {
				this.custCIF.setValue(String.valueOf(custDetails.getCustCIF()));
				this.custShrtName.setValue(custDetails.getCustShrtName());
				this.dftBranch.setValue(custDetails.getCustDftBranch());
				this.custId = custDetails.getCustID();
				doFillFinDetails(this.custId);
			}
		}
		
		logger.debug("Leaving");
	}
	
	/**
	 * This method fill the Finance Details by Clicking on Search box. <br>
	 * @throws Exception 
	 **/
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void doFillFinDetails(long custId) {
		logger.debug("Entering");
		financeEnqList = getFinanceMainService().getFinanceDetailsByCustId(custId);
		listBoxEnquiryResult.setModel(new GroupsModelArray(financeEnqList.toArray(),new CustomerEnquiryComparator()));
		this.listBoxEnquiryResult.setItemRenderer(new CustomerEnquiryListModelItemRender());
		logger.debug("Leaving");
	}
	
	/**
	 * When user clicks on button "button_Print" button
	 * @param event
	 * @throws InterruptedException 
	 */
	public void onClick$button_Print(Event event) throws InterruptedException{
		logger.debug("Entering " + event.toString());
		if(getFinanceEnqList()!=null && getFinanceEnqList().size()>0){
			ReportGenerationUtil.generateReport("Sample", getFinanceEnqList(),
					getFinanceEnqList(),true, 1, getUserWorkspace().getUserDetails().getUsername(),null);
		}
		logger.debug("Leaving " + event.toString());
	}
	
	/**
	 * Methodd for fetching Finance Record
	 * @param event
	 * @throws Exception
	 */
	public void onLoanItemDoubleClicked(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		
		final Listitem item = this.listBoxEnquiryResult.getSelectedItem();
		if (item != null) {
			
			final FinanceEnquiry aFinanceEnquiry = (FinanceEnquiry) item.getAttribute("data");
			
			final HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("financeEnquiry", aFinanceEnquiry);
			map.put("enquiryType", "FINENQ");

			// call the ZUL-file with the parameters packed in a map
			try {
				Executions.createComponents("/WEB-INF/pages/Enquiry/FinanceInquiry/FinanceEnquiryHeaderDialog.zul",null,map);
			} catch (final Exception e) {
				logger.error("onOpenWindow:: error opening window / " + e.getMessage());
				PTMessageUtils.showErrorMessage(e.toString());
			}
			
		}
		logger.debug("Leaving" + event.toString());
	}
	
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	
	public FinanceMainService getFinanceMainService() {
		return financeMainService;
	}
	public void setFinanceMainService(FinanceMainService financeMainService) {
		this.financeMainService = financeMainService;
	}
	
	public PagedListService getPagedListService() {
		return pagedListService;
	}
	public void setPagedListService(PagedListService pagedListService) {
		this.pagedListService = pagedListService;
	}

	public PagedListWrapper<FinanceEnquiry> getFinEnqDetailsPagedListWrapper() {
		return finEnqDetailsPagedListWrapper;
	}

	@SuppressWarnings("unchecked")
	public void setFinEnqDetailsPagedListWrapper() {
		if(this.finEnqDetailsPagedListWrapper == null){
			this.finEnqDetailsPagedListWrapper =(PagedListWrapper<FinanceEnquiry>) SpringUtil.getBean("pagedListWrapper");
		}
	}

	public List<FinanceEnquiry> getFinanceEnqList() {
		return financeEnqList;
	}
	public void setFinanceEnqList(List<FinanceEnquiry> financeEnqList) {
		this.financeEnqList = financeEnqList;
	}
}
