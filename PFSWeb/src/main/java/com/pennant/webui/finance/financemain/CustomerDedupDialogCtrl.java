package com.pennant.webui.finance.financemain;

import java.io.Serializable;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.zkoss.spring.SpringUtil;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Button;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerDedup;
import com.pennant.backend.model.customermasters.CustomerDetails;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.service.applicationmaster.BranchService;
import com.pennant.backend.service.applicationmaster.RelationshipOfficerService;
import com.pennant.backend.service.customermasters.CustomerDetailsService;
import com.pennant.backend.service.lmtmasters.FinanceWorkFlowService;
import com.pennant.backend.service.rmtmasters.CustomerTypeService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.webui.customermasters.customer.CoreCustomerSelectCtrl;
import com.pennant.webui.customermasters.customer.CustomerListCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.pagging.PagedListWrapper;
import com.pennanttech.pennapps.core.InterfaceException;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pff.external.CustomerInterfaceService;

public class CustomerDedupDialogCtrl extends GFCBaseCtrl<FinanceDetail> {
	private static final long serialVersionUID = 8556168885363682933L;
	private static final Logger logger = Logger.getLogger(CustomerDedupDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWiredd by
	 * our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_CustomerDedupDialog;
	protected Grid searchGrid;
	protected Button btnNewCustomer;
	protected Button btnExistingCustomer;
	protected Textbox custName;
	protected Textbox mobileNo;
	protected Textbox aadhaarNo;
	protected Textbox pANNo;

	protected transient FinanceType financeType;
	private CustomerDetails customerDetails = null;
	protected JdbcSearchObject<Customer> custCIFSearchObject;
	protected String old_NextRoleCode = "";
	protected Listbox listBoxCustomerDedup;
	private List<CustomerDedup> customerDedupsList = null;
	private PagedListWrapper<CustomerDedup> dedupPagedListWrapper;
	protected Paging pagingCustomerDedupList;
	protected Radiogroup radioButtonGroup = new Radiogroup();
	private transient CustomerDetailsService customerDetailsService;
	private transient BranchService branchService;
	int listRows;
	private RelationshipOfficerService relationshipOfficerService;
	@Autowired(required = false)
	private CustomerInterfaceService customerInterfaceService;
	private CustomerTypeService customerTypeService;
	protected Window parentWindow;
	protected SelectFinanceTypeDialogCtrl selectFinanceTypeDialogCtrl;
	protected CoreCustomerSelectCtrl coreCustomerSelectCtrl;

	protected CustomerListCtrl customerListCtrl;
	
	@Autowired(required = false)	
	private CustomerInterfaceService			customerExternalInterfaceService;


	private String custCIF = "";
	private String custCtgCode = "";
	private Boolean isFromCustomer = false;
	private Boolean isFromLoan = false;

	/**
	 * default constructor.<br>
	 */
	public CustomerDedupDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "";
	}

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * ZUL-file is called with a parameter for a selected FinanceMain object in
	 * a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_CustomerDedupDialog(Event event) throws Exception {
		logger.debug(Literal.ENTERING);

		// Set the page level components.
		setPageComponents(window_CustomerDedupDialog);
		setDedupPagedListWrapper();
		// READ OVERHANDED parameters !
		if (arguments.containsKey("customerDetails")) {
			setCustomerDetails((CustomerDetails) arguments.get("customerDetails"));
			CustomerDetails befImage = new CustomerDetails();
			BeanUtils.copyProperties(getCustomerDetails(), befImage);
			getCustomerDetails().setBefImage(befImage);
			setCustomerDetails(befImage);
		}

		if (arguments.containsKey("parentWindow")) {
			parentWindow = (Window) arguments.get("parentWindow");
		}
		
		
		if (arguments.containsKey("isFromCustomer")) {
			isFromCustomer = true;
		}
		
		if (arguments.containsKey("isFromLoan")) {
			isFromLoan = true;
		}


		if (arguments.containsKey("SelectFinanceTypeDialogCtrl")) {
			selectFinanceTypeDialogCtrl = (SelectFinanceTypeDialogCtrl) arguments.get("SelectFinanceTypeDialogCtrl");
			setSelectFinanceTypeDialogCtrl(this.selectFinanceTypeDialogCtrl);
		} else {
			setSelectFinanceTypeDialogCtrl(null);

		}
		
		if (arguments.containsKey("CoreCustomerSelectCtrl")) {
			coreCustomerSelectCtrl = (CoreCustomerSelectCtrl) arguments.get("CoreCustomerSelectCtrl");
			setCoreCustomerSelectCtrl((this.coreCustomerSelectCtrl));
		} else {
			setCoreCustomerSelectCtrl(null);

		}
		
		
		if (arguments.containsKey("CustomerListCtrl")) {
			customerListCtrl = (CustomerListCtrl) arguments.get("CustomerListCtrl");
			setCustomerListCtrl(this.customerListCtrl);
		} else {
			setCustomerListCtrl(null);

		}
		
		this.btnNewCustomer.setLabel("PROCEED AS NEW CUSTOMER");
		this.btnExistingCustomer.setLabel("PROCEED AS EXISTING CUSTOMER");
		int dialogHeight = searchGrid.getRows().getVisibleItemCount() * 20 + 200;
		int listboxHeight = borderLayoutHeight - dialogHeight;
		listBoxCustomerDedup.setHeight(listboxHeight + "px");
		listRows = Math.round(listboxHeight / 24) - 1;
		pagingCustomerDedupList.setPageSize(listRows);

		// fill the components with the data
		doWriteBeanToComponents(getCustomerDetails());
		showSelectFinanceTypeDialog();
	}

	/**
	 * Method for Selection Of List Item from Customer Dedup List
	 * 
	 * @param event
	 */
	public void onCustDedupItemSelected(ForwardEvent event) {
		logger.debug(Literal.ENTERING);

		Listitem selectedItem = this.listBoxCustomerDedup.getSelectedItem();
		if (selectedItem == null) {
			return;
		}
		Listcell listcell = (Listcell) selectedItem.getChildren().get(0);
		Radio radio = (Radio) listcell.getFirstChild();
		radio.setSelected(true);

		Events.sendEvent(Events.ON_CHECK, radio, null);
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Filling the CustomerDedup details based on checked and unchecked events
	 * of listCellCheckBox.
	 */
	public void onCheck_listCellRadioBtn(ForwardEvent event) throws Exception {
		logger.debug(Literal.ENTERING);
		Radio checkBox = (Radio) event.getOrigin().getTarget();

		for (int i = 1; i < this.listBoxCustomerDedup.getChildren().size(); i++) {

			Listitem item = (Listitem) this.listBoxCustomerDedup.getChildren().get(i);
			Listcell lc = (Listcell) item.getChildren().get(0);

			if (lc.getChildren().size() == 0) {
				continue;
			}
			Radio radio = (Radio) lc.getChildren().get(0);
			if ((!(radio.getUuid().trim().equals(checkBox.getUuid().trim()))) && radio.isChecked()) {
				radio.setChecked(false);
			}
		}
		if (StringUtils.isNotBlank(checkBox.getValue()))  {
			String[] array = checkBox.getValue().toString().split(",");
			custCIF = array[0];
			custCtgCode = array[1];
		}
		logger.debug(Literal.LEAVING);
	}

	private void doWriteBeanToComponents(CustomerDetails customerDetails) {
		logger.debug(Literal.ENTERING);

		this.custName.setValue(customerDetails.getCustomer().getCustShrtName());
		this.mobileNo.setValue(customerDetails.getCustomer().getPhoneNumber());
		
		if (StringUtils.trimToEmpty(customerDetails.getCustomer().getCustCRCPR()).length() == 10) {
			this.pANNo.setValue(customerDetails.getCustomer().getCustCRCPR());
		} else {
			this.aadhaarNo.setValue(PennantApplicationUtil.formatEIDNumber(customerDetails.getCustomer().getCustCRCPR()));
		}
		
		
		doFilllistbox(customerDetails.getCustomerDedupList());

		logger.debug(Literal.LEAVING);
	}

	/**
	 * The Click event is raised when the Close Button control is clicked.
	 * 
	 * @param event
	 *            An event sent to the event handler of a component.
	 */
	public void onClick$btnClose(Event event) {
		doClose(this.btnExistingCustomer.isVisible());
	}

	public void onClick$btnNewCustomer(Event event) throws Exception {
		if (isFromLoan) {
			selectFinanceTypeDialogCtrl.existingCust.setSelected(false);
			selectFinanceTypeDialogCtrl.processCustomer(false);
		} else if (isFromCustomer) {
			coreCustomerSelectCtrl.proceedAsNewCustomer(customerDetails, custCtgCode, customerDetails.getCustomer().getCustCtgCode(), false);
			customerListCtrl.buildDialogWindow(customerDetails, true);

		}
		closeDialog();
	}

	public void onClick$btnExistingCustomer(Event event) throws Exception {
		logger.debug(Literal.ENTERING);

		doRemoveValidation();

		if (StringUtils.isBlank(custCIF)) {
			throw new WrongValueException(this.btnExistingCustomer,
					"Please select Customer CIF to proceed with existing customer  !!!");
		} else if (isFromLoan) {
			selectFinanceTypeDialogCtrl.existingCust.setSelected(true);
			selectFinanceTypeDialogCtrl.custCIF.setValue(custCIF);
			boolean isRetail = false;
			if ("RETAIL".equals(custCtgCode)) {
				isRetail = true;
			}
			boolean flag = selectFinanceTypeDialogCtrl.processCustomer(isRetail);
			if (flag) {
				closeDialog();
			}
		} else if (isFromCustomer) {
			if ("RETAIL".equals(custCtgCode)) {
				customerDetails.getCustomer().setCustCtgCode("RETAIL");
			} else {
				customerDetails.getCustomer().setCustCtgCode("CORP");
			}

			if ("Y".equals(SysParamUtil.getValueAsString("EXT_CRM_INT_ENABLED"))
					&& customerExternalInterfaceService != null) {
				Customer customer = new Customer();
				customer.setCustCoreBank(custCIF);
				customerDetails = customerExternalInterfaceService.getCustomerDetail(customer);
				if (customerDetails == null) {
					throw new InterfaceException("9999", Labels.getLabel("Cust_NotFound"));
				}
				customerDetails.setCustomer(customer);
			}
			coreCustomerSelectCtrl.proceedAsNewCustomer(customerDetails, custCtgCode, customerDetails.getCustomer().getCustCtgCode(), false);
			customerListCtrl.buildDialogWindow(customerDetails, false);
			closeDialog();
		}

		logger.debug(Literal.LEAVING);
	}

	private void doRemoveValidation() {
		logger.debug(Literal.ENTERING);
		Clients.clearWrongValue(this.btnExistingCustomer);
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Method for rendering list of Customer Dedup
	 * 
	 * @param customerDedupList
	 */
	public void doFilllistbox(List<CustomerDedup> custDedupeList) {
		logger.debug(Literal.ENTERING);
		if (custDedupeList != null) {
			this.listBoxCustomerDedup.getItems().clear();
			setCustomerDedupsList(custDedupeList);

			this.pagingCustomerDedupList.setDetailed(true);
			getDedupPagedListWrapper().initList(custDedupeList, this.listBoxCustomerDedup,
					this.pagingCustomerDedupList);

			this.listBoxCustomerDedup.setItemRenderer(new CustDedupListModelItemRenderer());
		}

		// Set the first page as the active page.
		if (pagingCustomerDedupList != null) {
			this.pagingCustomerDedupList.setActivePage(0);
		}
		if (getCustomerDedupsList() == null || getCustomerDedupsList().isEmpty()) {
			this.btnExistingCustomer.setDisabled(true);
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Item renderer for listItems in the listBox.
	 */
	private class CustDedupListModelItemRenderer implements ListitemRenderer<CustomerDedup>, Serializable {
		private static final long serialVersionUID = 1L;

		@Override
		public void render(Listitem item, CustomerDedup customerDedup, int count) throws Exception {

			Listcell lc = new Listcell();
			if (StringUtils.isNotEmpty(customerDedup.getCustCIF())) {
				Radio list_radioButton = new Radio();
				list_radioButton.setValue(customerDedup.getCustCIF()+ "," + customerDedup.getCustCtgCode());
				list_radioButton.addForward("onCheck", self, "onCheck_listCellRadioBtn");
				list_radioButton.setParent(radioButtonGroup);
				lc.appendChild(list_radioButton);
			}
			lc.setParent(item);

			lc = new Listcell(customerDedup.getCustCIF());
			lc.setParent(item);
			
			lc = new Listcell(customerDedup.getCustFName());
			lc.setParent(item);

			lc = new Listcell(customerDedup.getMobileNumber());
			lc.setParent(item);

			lc = new Listcell(PennantApplicationUtil.formatEIDNumber(customerDedup.getAadharNumber()));
			lc.setParent(item);
			
			lc = new Listcell(customerDedup.getPanNumber());
			lc.setParent(item);
			
			
			item.setAttribute("id", customerDedup.getCustId());
			if (StringUtils.isNotEmpty(customerDedup.getCustCIF())) {
				ComponentsCtrl.applyForward(item, "onClick=onCustDedupItemSelected");
			}
		}
	}

	/**
	 * Opens the SelectFinanceTypeDialog window modal.
	 */
	private void showSelectFinanceTypeDialog() throws InterruptedException {
		logger.debug(Literal.ENTERING);
		try {
			// open the dialog in modal mode
			this.window_CustomerDedupDialog.doModal();
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug(Literal.LEAVING);
	}

	// Getters and Setters

	public void setSelectFinanceTypeDialogCtrl(SelectFinanceTypeDialogCtrl selectFinanceTypeDialogCtrl) {
		this.selectFinanceTypeDialogCtrl = selectFinanceTypeDialogCtrl;
	}
	
	public void setCustomerListCtrl(CustomerListCtrl customerListCtrl) {
		this.customerListCtrl = customerListCtrl;
	}
	
	public void setCoreCustomerSelectCtrl(CoreCustomerSelectCtrl coreCustomerSelectCtrl) {
		this.coreCustomerSelectCtrl = coreCustomerSelectCtrl;
	}

	public void setFinanceWorkFlowService(FinanceWorkFlowService financeWorkFlowService) {
		this.financeWorkFlowService = financeWorkFlowService;
	}


	public List<CustomerDedup> getCustomerDedupsList() {
		return customerDedupsList;
	}

	public void setCustomerDedupsList(List<CustomerDedup> customerDedupsList) {
		this.customerDedupsList = customerDedupsList;
	}

	public CustomerDetailsService getCustomerDetailsService() {
		return customerDetailsService;
	}

	public void setCustomerDetailsService(CustomerDetailsService customerDetailsService) {
		this.customerDetailsService = customerDetailsService;
	}

	public BranchService getBranchService() {
		return branchService;
	}

	public void setBranchService(BranchService branchService) {
		this.branchService = branchService;
	}

	public RelationshipOfficerService getRelationshipOfficerService() {
		return relationshipOfficerService;
	}

	public void setRelationshipOfficerService(RelationshipOfficerService relationshipOfficerService) {
		this.relationshipOfficerService = relationshipOfficerService;
	}

	public CustomerInterfaceService getCustomerInterfaceService() {
		return customerInterfaceService;
	}

	public void setCustomerInterfaceService(CustomerInterfaceService customerInterfaceService) {
		this.customerInterfaceService = customerInterfaceService;
	}

	public CustomerTypeService getCustomerTypeService() {
		return customerTypeService;
	}

	public void setCustomerTypeService(CustomerTypeService customerTypeService) {
		this.customerTypeService = customerTypeService;
	}

	public PagedListWrapper<CustomerDedup> getDedupPagedListWrapper() {
		return dedupPagedListWrapper;
	}

	public void setDedupPagedListWrapper(PagedListWrapper<CustomerDedup> dedupPagedListWrapper) {
		this.dedupPagedListWrapper = dedupPagedListWrapper;
	}
	
	@SuppressWarnings("unchecked")
	public void setDedupPagedListWrapper() {
		this.dedupPagedListWrapper = (PagedListWrapper<CustomerDedup>) SpringUtil
				.getBean("pagedListWrapper");
	}

	public CustomerDetails getCustomerDetails() {
		return customerDetails;
	}

	public void setCustomerDetails(CustomerDetails customerDetails) {
		this.customerDetails = customerDetails;
	}

}