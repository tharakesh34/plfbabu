package com.pennant.webui.reports.customer;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listgroup;
import org.zkoss.zul.Listgroupfoot;
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

import com.pennant.ExtendedCombobox;
import com.pennant.app.constants.LengthConstants;
import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.ReportsUtil;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.finance.FinanceEnquiry;
import com.pennant.backend.service.finance.FinanceMainService;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.web.util.MessageUtil;

public class CustomerEnquiryDialogCtrl extends GFCBaseCtrl<FinanceEnquiry> {
	private static final long serialVersionUID = -6646226859133636932L;
	private static final Logger logger = LogManager.getLogger(CustomerEnquiryDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the ZUL-file
	 * are getting autoWired by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_CustomerEnquiryDialog;// autoWired

	protected Borderlayout borderlayout_Enquiry; // autoWired
	protected ExtendedCombobox custCIF; // autoWired
	protected Textbox dftBranch; // autoWired

	protected Button button_Print; // autoWired

	protected Listbox listBoxEnquiryResult; // autoWired
	protected Paging pagingEnquiryList; // autoWired
	protected Grid grid_enquiryDetails; // autoWired
	protected Menu menu_filter; // autoWired
	protected Menupopup menupopup_filter; // autoWired
	protected Menuitem menuitem;
	private Tab tab;
	private Tabbox tabbox;

	// not auto wired variables
	List<FinanceEnquiry> financeEnqList = null;
	private FinanceMainService financeMainService;
	private int listRows;
	private long custId;

	/**
	 * default constructor.<br>
	 */
	public CustomerEnquiryDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "CustomerEnquiryDialog";
	}

	/**
	 * Before binding the data and calling the dialog window we check, if the ZUL-file is called with a parameter for a
	 * selected Academic object in a Map.
	 * 
	 * @param event
	 */
	public void onCreate$window_CustomerEnquiryDialog(Event event) {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_CustomerEnquiryDialog);

		if (event.getTarget() != null && event.getTarget().getParent() != null
				&& event.getTarget().getParent().getParent() != null
				&& event.getTarget().getParent().getParent().getParent() != null) {
			tabbox = (Tabbox) event.getTarget().getParent().getParent().getParent().getParent();
			if (tabbox != null) {
				tab = (Tab) tabbox.getFellowIfAny("tab_CustomerEnquiry");
			}
		}

		doSetFieldProperties();
		doFillFilterList();
		this.borderlayout_Enquiry.setHeight(getBorderLayoutHeight());
		int dialogHeight = grid_enquiryDetails.getRows().getVisibleItemCount() * 20 + 52;
		int listboxHeight = borderLayoutHeight - dialogHeight;
		listBoxEnquiryResult.setHeight(listboxHeight + "px");
		listRows = Math.round(listboxHeight / 22);
		this.pagingEnquiryList.setDetailed(true);
		this.pagingEnquiryList.setPageSize(listRows);

		// READ OVERHANDED parameters !
		if (arguments.containsKey("custId")) {
			this.custId = (Long) arguments.get("custId");
			doFillFinDetails(this.custId);
		}

		if (arguments.containsKey("custCIF")) {
			this.custCIF.setValue(String.valueOf(arguments.get("custCIF")));
		}

		if (arguments.containsKey("custShrtName")) {
			this.custCIF.setDescription(String.valueOf(arguments.get("custShrtName")));
		}

		if (arguments.containsKey("dftBranch")) {
			this.dftBranch.setValue(String.valueOf(arguments.get("dftBranch")));
		}

		logger.debug("Leaving");
	}

	/**
	 * SetVisible for components by checking if there's a right for it.
	 */
	@SuppressWarnings("unused")
	private void doCheckRights() {
		logger.debug("Entering");
		getUserWorkspace().allocateAuthorities(super.pageRightName);
		this.button_Print.setVisible(getUserWorkspace().isAllowed("button_CustomerEnquiryDialog_PrintList"));
		logger.debug("Leaving");
	}

	// Helpers

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		this.dftBranch.setMaxlength(LengthConstants.LEN_BRANCH);
		this.custCIF.setModuleName("Customer");
		this.custCIF.setValueColumn("CustCIF");
		this.custCIF.setDescColumn("CustShrtName");
		this.custCIF.setValidateColumns(new String[] { "CustCIF" });
		this.custCIF.setMandatoryStyle(true);
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
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onFilterMenuItem(ForwardEvent event) throws InterruptedException {
		logger.debug("Entering");

		final Map<String, Object> map = new HashMap<String, Object>();
		map.put("custId", this.custId);
		map.put("custCIF", this.custCIF.getValue());
		map.put("custShrtName", this.custCIF.getDescription());
		map.put("dftBranch", this.dftBranch.getValue());

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

						getUserWorkspace().deAllocateAuthorities(pageName);
					}
				});

				tab.setParent(tabbox.getFellow("tabsIndexCenter"));

				final Tabpanels tabpanels = (Tabpanels) tabbox.getFellow("tabsIndexCenter")
						.getFellow("tabpanelsBoxIndexCenter");
				final Tabpanel tabpanel = new Tabpanel();
				tabpanel.setHeight("100%");
				tabpanel.setStyle("padding: 0px;");
				tabpanel.setParent(tabpanels);

				Executions.createComponents("/WEB-INF/pages/Reports/CustomerAccountList.zul", tabpanel, map);
				tab.setSelected(true);
			}
		} catch (Exception e) {
			MessageUtil.showError(e);
		}

		logger.debug("Leaving");
	}

	/**
	 * This method fill the CustomerName and CIF values by using Search box.
	 **/
	public void onFulfill$custCIF(Event event) {
		logger.debug("Entering");
		Customer custDetails = null;

		Object dataObject = custCIF.getObject();
		if (dataObject instanceof String) {
			this.custCIF.setValue(dataObject.toString());
			this.custCIF.setDescription("");
			this.dftBranch.setValue("");
		} else {
			custDetails = (Customer) dataObject;
			if (custDetails != null) {
				this.custCIF.setValue(String.valueOf(custDetails.getCustCIF()));
				this.custCIF.setDescription(custDetails.getCustShrtName());
				this.dftBranch.setValue(custDetails.getCustDftBranch());
				this.custId = custDetails.getCustID();
				doFillFinDetails(this.custId);
			}
		}

		logger.debug("Leaving");
	}

	/**
	 * This method fill the Finance Details by Clicking on Search box.
	 **/
	public void doFillFinDetails(long custId) {
		logger.debug("Entering");
		financeEnqList = getFinanceMainService().getFinanceDetailsByCustId(custId);
		this.listBoxEnquiryResult.getItems().clear();
		/*
		 * listBoxEnquiryResult.setModel(new GroupsModelArray(financeEnqList.toArray(),new
		 * CustomerEnquiryComparator())); this.listBoxEnquiryResult.setItemRenderer(new
		 * CustomerEnquiryListModelItemRender());
		 */

		// Adding to Listbox
		String fintype = "";
		String finCcy = "";
		BigDecimal grpTotal = BigDecimal.ZERO;

		for (int i = 0; i < financeEnqList.size(); i++) {

			FinanceEnquiry aFinanceEnq = financeEnqList.get(i);

			// Adding List Group
			if (!fintype.equals(aFinanceEnq.getFinType())) {
				fintype = aFinanceEnq.getFinType();
				finCcy = aFinanceEnq.getFinCcy();
				addListGroup(aFinanceEnq);
				grpTotal = BigDecimal.ZERO;
			}
			if (!finCcy.equals(aFinanceEnq.getFinCcy())) {
				finCcy = aFinanceEnq.getFinCcy();
				addListGroup(aFinanceEnq);
				grpTotal = BigDecimal.ZERO;
			}

			// Adding List Item
			addListItem(aFinanceEnq);
			grpTotal = grpTotal.add(aFinanceEnq.getFinAmount().add(aFinanceEnq.getFeeChargeAmt())
					.subtract(aFinanceEnq.getFinRepaymentAmount()));

			// Adding List Footer
			if (i == financeEnqList.size() - 1) {
				addListFooter(grpTotal, CurrencyUtil.getFormat(aFinanceEnq.getFinCcy()));
			} else {
				FinanceEnquiry nextTerm = financeEnqList.get(i + 1);
				if (!fintype.equals(nextTerm.getFinType()) || !finCcy.equals(nextTerm.getFinCcy())) {
					addListFooter(grpTotal, CurrencyUtil.getFormat(aFinanceEnq.getFinCcy()));
				}
			}
		}

		logger.debug("Leaving");
	}

	private void addListItem(FinanceEnquiry aFinanceEnq) {
		Listitem item = new Listitem();
		Listcell lc;
		lc = new Listcell(aFinanceEnq.getFinReference());
		lc.setParent(item);
		lc = new Listcell(aFinanceEnq.getFinBranch());
		lc.setParent(item);
		lc = new Listcell(DateUtil.formatToLongDate(aFinanceEnq.getFinStartDate()));
		lc.setParent(item);
		lc = new Listcell(DateUtil.formatToLongDate(aFinanceEnq.getGrcPeriodEndDate()));
		lc.setParent(item);
		lc = new Listcell(String.valueOf(aFinanceEnq.getNumberOfTerms()));
		lc.setParent(item);
		lc = new Listcell(DateUtil.formatToLongDate(aFinanceEnq.getMaturityDate()));
		lc.setParent(item);
		lc = new Listcell(
				CurrencyUtil.format(aFinanceEnq.getFinAmount(), CurrencyUtil.getFormat(aFinanceEnq.getFinCcy())));
		lc.setStyle("text-align:right;");
		lc.setParent(item);
		lc = new Listcell(CurrencyUtil.format(aFinanceEnq.getFinAmount().add(aFinanceEnq.getFeeChargeAmt())
				.subtract(aFinanceEnq.getFinRepaymentAmount()), CurrencyUtil.getFormat(aFinanceEnq.getFinCcy())));
		lc.setStyle("text-align:right");
		lc.setParent(item);
		lc = new Listcell(DateUtil.formatToLongDate(aFinanceEnq.getNextDueDate()));
		lc.setParent(item);
		lc = new Listcell(
				CurrencyUtil.format(aFinanceEnq.getNextDueAmount(), CurrencyUtil.getFormat(aFinanceEnq.getFinCcy())));
		lc.setStyle("text-align:right");
		lc.setParent(item);
		item.setAttribute("data", aFinanceEnq);
		ComponentsCtrl.applyForward(item, "onDoubleClick=onLoanItemDoubleClicked");
		this.listBoxEnquiryResult.appendChild(item);
	}

	/**
	 * Method for Adding List Group to listBox in Corporation
	 * 
	 * @param scoringMetric
	 * @param listbox
	 */
	private void addListGroup(FinanceEnquiry aFinanceEnq) {
		logger.debug("Entering");

		Listgroup listgroup = new Listgroup();
		Listcell cell = new Listcell(aFinanceEnq.getFinType() + " - " + aFinanceEnq.getLovDescFinTypeName());
		cell.setSpan(3);
		listgroup.appendChild(cell);

		cell = new Listcell(aFinanceEnq.getFinCcy());
		cell.setSpan(5);
		cell.setStyle("text-align:right;");
		listgroup.appendChild(cell);
		this.listBoxEnquiryResult.appendChild(listgroup);

		logger.debug("Leaving");
	}

	/**
	 * Method for Adding List Group to listBox in Corporation
	 * 
	 * @param scoringMetric
	 * @param listbox
	 */
	private void addListFooter(BigDecimal grpTotalAmt, int formatter) {
		logger.debug("Entering");

		Listgroupfoot listgroupfoot = new Listgroupfoot();

		Listcell cell = new Listcell("Group Total");
		cell.setStyle("font-weight:bold;text-align:right;");
		cell.setSpan(7);
		listgroupfoot.appendChild(cell);

		cell = new Listcell(CurrencyUtil.format(grpTotalAmt, formatter));
		cell.setStyle("font-weight:bold;text-align:right;");
		listgroupfoot.appendChild(cell);

		this.listBoxEnquiryResult.appendChild(listgroupfoot);
		logger.debug("Leaving");
	}

	/**
	 * When user clicks on button "button_Print" button
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$button_Print(Event event) throws InterruptedException {
		logger.debug("Entering " + event.toString());

		if (getFinanceEnqList() != null && getFinanceEnqList().size() > 0) {
			String userName = getUserWorkspace().getLoggedInUser().getUserName();
			ReportsUtil.generatePDF("Sample", getFinanceEnqList(), getFinanceEnqList(), userName, null);
		}
		logger.debug("Leaving " + event.toString());
	}

	/**
	 * Methodd for fetching Finance Record
	 * 
	 * @param event
	 */
	public void onLoanItemDoubleClicked(Event event) {
		logger.debug("Entering" + event.toString());

		final Listitem item = this.listBoxEnquiryResult.getSelectedItem();
		if (item != null) {

			final FinanceEnquiry aFinanceEnquiry = (FinanceEnquiry) item.getAttribute("data");

			final Map<String, Object> map = new HashMap<String, Object>();
			map.put("financeEnquiry", aFinanceEnquiry);
			map.put("enquiryType", "FINENQ");

			// call the ZUL-file with the parameters packed in a map
			try {
				Executions.createComponents("/WEB-INF/pages/Enquiry/FinanceInquiry/FinanceEnquiryHeaderDialog.zul",
						null, map);
			} catch (Exception e) {
				MessageUtil.showError(e);
			}

		}
		logger.debug("Leaving" + event.toString());
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public FinanceMainService getFinanceMainService() {
		return financeMainService;
	}

	public void setFinanceMainService(FinanceMainService financeMainService) {
		this.financeMainService = financeMainService;
	}

	public List<FinanceEnquiry> getFinanceEnqList() {
		return financeEnqList;
	}

	public void setFinanceEnqList(List<FinanceEnquiry> financeEnqList) {
		this.financeEnqList = financeEnqList;
	}
}
