package com.pennant.webui.dedup.dedupparm;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.SuspendNotAllowedException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Center;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Div;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listhead;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.North;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Rows;
import org.zkoss.zul.South;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Toolbar;
import org.zkoss.zul.Window;
import org.zkoss.zul.event.PagingEvent;

import com.pennant.app.constants.ImplementationConstants;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.customermasters.CustomerDedup;
import com.pennant.backend.service.PagedListService;
import com.pennant.backend.service.dedup.DedupParmService;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.SMTParameterConstants;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.feature.model.ModuleMapping;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pff.InterfaceConstants;

@SuppressWarnings("rawtypes")
public class ShowExtCustomerDedupListBox extends Window implements Serializable {
	private static final long serialVersionUID = -2854517425413800019L;
	private static final Logger logger = LogManager.getLogger(ShowExtCustomerDedupListBox.class);
	private Textbox _textbox;
	private Paging _paging;
	private int pageSize = 10;
	private Listbox listbox;
	private Rows rows;
	private ListModelList listModelList;
	private final int _height = 300;
	private int _width = 800;
	private transient PagedListService pagedListService;
	private String[] fieldString;
	private ModuleMapping moduleMapping = null;
	private List<?> custDedupListSize = null;
	private Object objClass = null;
	private int userAction = 0;
	private String[] listHeaders;
	private String curAccessedUser = null;
	boolean isExtDedup = false;

	private final String MOB_NUM = "MobileNumber";
	private final String CRCPR = "CRCPR";
	private final String COLOR = "font-weight:bold;color:#F20C0C;";

	public CustomerDedup customerDedup;
	public DedupParmService dedupParmService;
	public int triggerCount = 0;

	public ShowExtCustomerDedupListBox() {
		super();
	}

	/**
	 * The Call method.
	 * 
	 * @param parent The parent component
	 * @return a BeanObject from the listBox or null.
	 */
	public static Object show(Component parent, List<?> custDedupList, String dedupFields, CustomerDedup custDedup,
			String curUser, DedupParmService dedupParmService) {
		return new ShowExtCustomerDedupListBox(parent, custDedupList, dedupFields, custDedup, curUser,
				dedupParmService);
	}

	/**
	 * Private Constructor. So it can only be created with the static show() method.<br>
	 * 
	 * @param parent
	 */
	private ShowExtCustomerDedupListBox(Component parent, List<?> custDedupList, String dedupFields,
			CustomerDedup custDedup, String curUser, DedupParmService dedupParmservice) {
		super();
		this.custDedupListSize = (List<?>) custDedupList;
		this.fieldString = dedupFields.split(",");
		curAccessedUser = curUser;
		this.dedupParmService = dedupParmservice;
		setParent(parent);
		createBox(custDedup);
	}

	@SuppressWarnings({ "deprecation", "unchecked" })
	private void createBox(CustomerDedup custDedup) {
		logger.debug("Entering");

		setCustomerDedup(custDedup);
		int borderLayoutHeight = GFCBaseCtrl.getDesktopHeight();
		int borderLayoutWidth = GFCBaseCtrl.getDesktopWidth();
		this.setWidth((borderLayoutWidth - 100) + "px");
		this.setHeight((borderLayoutHeight - 150) + "px");

		// Window
		int listRows = Math.round((borderLayoutHeight - 300) / 25) - 3;
		setPageSize(listRows);
		this.setVisible(true);
		this.setClosable(true);

		// BorderLayout
		final Borderlayout brdLayout = new Borderlayout();
		brdLayout.setHeight("100%");
		brdLayout.setWidth("100%");
		brdLayout.setParent(this);

		// Center for BorderLayout
		final Center center = new Center();
		center.setBorder("none");
		center.setFlex(true);
		center.setParent(brdLayout);

		// Div tag for Center
		Div divCenter = new Div();
		divCenter.setParent(center);

		// North for BorderLayout
		North north = new North();
		north.setParent(brdLayout);

		// Div for North
		Div divNorth = new Div();
		divNorth.setSclass("z-toolbar");
		divNorth.setParent(north);

		// South for BorderLayout
		South south = new South();
		south.setParent(brdLayout);

		// Groupbox for Rows
		Groupbox grpBox = new Groupbox();
		grpBox.setMold("3d");
		grpBox.setParent(divCenter);

		// Groupbox for Listbox
		Groupbox grpBoxForListbox = new Groupbox();
		grpBoxForListbox.setParent(divCenter);

		// Components for buttons alignments
		Hbox hboxForButton = new Hbox();
		hboxForButton.setSclass("hboxRemoveWhiteStrips");
		hboxForButton.setWidth("100%");
		hboxForButton.setWidths("35%,30%,35%");
		hboxForButton.setPack("stretch");
		hboxForButton.setParent(divNorth);

		// Toolbar for start
		Toolbar startToolbar = new Toolbar();
		startToolbar.setAlign("start");
		startToolbar.setSclass("toolbar-start");
		startToolbar.setParent(hboxForButton);

		// Toolbar for Center
		Toolbar centerToolbar = new Toolbar();
		centerToolbar.setAlign("center");
		centerToolbar.setSclass("toolbar-center");
		centerToolbar.setParent(hboxForButton);

		// Toolbar for End
		Toolbar endToolbar = new Toolbar();
		endToolbar.setAlign("end");
		endToolbar.setSclass("toolbar-end");
		endToolbar.setParent(hboxForButton);

		// Button for Help
		final Button btnClose = new Button();
		btnClose.setSclass("z-toolbarbutton");
		btnClose.setLabel("Close");
		btnClose.addEventListener("onClick", new OnCloseListener());
		btnClose.setParent(endToolbar);

		// Label For Title
		Label titleLabel = new Label();
		titleLabel.setValue(Labels.getLabel("window_CustomerExtDedupDialog.title"));
		titleLabel.setSclass("label-heading");
		titleLabel.setParent(centerToolbar);

		String headerList = "";

		if (SysParamUtil.isAllowed(SMTParameterConstants.EXTERNAL_CUSTOMER_DEDUP)) {
			headerList = Labels.getLabel("listHeader_CustomerDedup_label_External");
			isExtDedup = true;
		} else {
			headerList = Labels.getLabel("listHeader_CustomerDedup_label");
			isExtDedup = false;
		}

		// ListBox
		this.listbox = new Listbox();
		listbox.setStyle("border: none;");
		this.listbox.setHeight((borderLayoutHeight - 320) + "px");
		this.listbox.setVisible(true);
		this.listbox.setSizedByContent(true);
		this.listbox.setSpan("true");
		this.listbox.setParent(grpBoxForListbox);

		setListModelList(new ListModelList(custDedupListSize));
		this.listbox.setModel(getListModelList());
		this.listbox.setItemRenderer(new CustomerDedupBoxItemRenderer());
		this.listbox.renderAll();

		final Listhead listhead = new Listhead();
		listhead.setParent(this.listbox);

		/*
		 * if(StringUtils.equals(ImplementationConstants.CLIENT_NAME, ImplementationConstants.CLIENT_BFL)) { headerList
		 * = Labels.getLabel("listHeader_CustomerDedup_label_Bajaj"); }
		 */
		this.listHeaders = headerList.split(",");
		for (int i = 0; i < this.listHeaders.length; i++) {
			if (StringUtils.trimToNull(custDedup.getFinType()) != null) {
				if (listHeaders[i].equalsIgnoreCase("Override")) {
					continue;
				}
			}
			final Listheader listheader = new Listheader();
			listheader.setLabel(getLabel(this.listHeaders[i]));
			listheader.setHflex("min");
			listheader.setParent(listhead);
		}

		try {
			doModal();
		} catch (final SuspendNotAllowedException e) {
			logger.error("Exception: ", e);
			this.detach();
		}
		logger.debug("Leaving");
	}

	void refreshModel(String searchText, int start) {
		logger.debug("Entering");
		// clear old data
		getListModelList().clear();
		this._paging.setTotalSize(custDedupListSize.size());
		// set the model
		setListModelList(new ListModelList());
		this.listbox.setModel(getListModelList());
		logger.debug("Leaving");
	}

	/**
	 * Inner Close class.<br>
	 */
	final class OnCloseListener implements EventListener<Event> {

		public OnCloseListener() {
		    super();
		}

		@Override
		public void onEvent(Event event) throws Exception {
			setUserAction(0);
			onClose();

		}
	}

	/**
	 * Inner Cancel class.<br>
	 */
	final class OnCancelListener implements EventListener<Event> {

		public OnCancelListener() {
		    super();
		}

		@Override
		public void onEvent(Event event) throws Exception {
			setUserAction(0);
			setObject(null);
			boolean iseleceted = false;

			for (int i = 0; i < listbox.getItems().size(); i++) {
				List<Component> componentList = new ArrayList<Component>();

				Listitem listitem = listbox.getItems().get(i);
				if (isExtDedup) {
					componentList = ((Listcell) listitem.getLastChild().getPreviousSibling().getPreviousSibling())
							.getChildren();
				} else {
					componentList = ((Listcell) listitem.getLastChild()).getChildren();
				}
				if (componentList != null && componentList.size() > 0) {
					Component component = componentList.get(0);
					if (((Checkbox) component).isChecked()) {
						iseleceted = true;
					}
				}

			}
			if (!iseleceted) {
				throw new WrongValueException(listbox, "Please Select Atleast one Cutomer to dedup");
			}

			setUserAction(1);
			if (validateUserMultiSelection()) {
				setUserAction(2);
				return;
			}
			List<CustomerDedup> customerDedupList = new ArrayList<CustomerDedup>();
			for (int i = 0; i < listbox.getItems().size(); i++) {
				List<Component> componentList = new ArrayList<Component>();
				Listitem listitem = listbox.getItems().get(i);
				if (isExtDedup) {
					componentList = ((Listcell) listitem.getLastChild().getPreviousSibling().getPreviousSibling())
							.getChildren();
				} else {
					componentList = ((Listcell) listitem.getLastChild()).getChildren();
				}
				if (componentList != null && componentList.size() > 0) {
					Component component = componentList.get(0);
					if (component instanceof Checkbox) {
						if (!((Checkbox) component).isChecked()) {
							setUserAction(-1);
						} else {
							setUserAction(1);
							CustomerDedup customer = (CustomerDedup) listitem.getAttribute("data");
							if (customer.isNewCustDedupRecord()) {
								customer.setOverrideUser(curAccessedUser);
							}
							customerDedupList.add(customer);
							break;
						}
					}
				}
			}

			if (getUserAction() == -1) {
				return;
			} else {
				setObject(customerDedupList);
				onClose();
			}

		}

		private boolean validateUserMultiSelection() throws InterruptedException {
			int userMultiselect = 0;
			for (int i = 0; i < listbox.getItems().size(); i++) {
				List<Component> componentList = new ArrayList<Component>();
				Listitem listitem = listbox.getItems().get(i);
				if (isExtDedup) {
					componentList = ((Listcell) listitem.getLastChild().getPreviousSibling().getPreviousSibling())
							.getChildren();
				} else {
					componentList = ((Listcell) listitem.getLastChild()).getChildren();
				}
				if (componentList != null && componentList.size() > 0) {
					Component component = componentList.get(0);
					if (((Checkbox) component).isChecked()) {
						userMultiselect++;
					}
				}
			}

			if (userMultiselect > 1) {
				return true;
			} else {
				return false;
			}
		}
	}

	/**
	 * Inner OnProceedListener class.<br>
	 */
	final class OnProceedListener implements EventListener<Event> {

		public OnProceedListener() {
		    super();
		}

		@Override
		public void onEvent(Event event) throws Exception {
			logger.debug("Entering : proceed Event");
			setUserAction(1);
			List<CustomerDedup> customerDedupList = new ArrayList<CustomerDedup>();
			for (int i = 0; i < listbox.getItems().size(); i++) {
				Listitem listitem = listbox.getItems().get(i);
				// The rows that were not loaded will be skipped.
				if (listitem.getLastChild().getPreviousSibling() == null) {
					continue;
				}

				List<Component> componentList = ((Listcell) listitem.getLastChild().getPreviousSibling()).getChildren();
				if (componentList != null && componentList.size() > 0) {
					Component component = componentList.get(0);
					if (component instanceof Checkbox) {
						if (!((Checkbox) component).isChecked()) {
							setUserAction(-1);
						} else {
							CustomerDedup customer = (CustomerDedup) listitem.getAttribute("data");
							if (customer.isNewCustDedupRecord()) {
								customer.setOverrideUser(curAccessedUser);
							}
							customerDedupList.add(customer);
						}
					}
				}
			}

			if (getUserAction() == -1) {
				MessageUtil.showError(Labels.getLabel("label_Message_CustomerOverrideAlert"));
			} else {
				setObject(customerDedupList);
				onClose();
			}
		}

	}

	/**
	 * Inner ListItemRenderer class.<br>
	 */
	final class CustomerDedupBoxItemRenderer implements ListitemRenderer<Object> {

		public CustomerDedupBoxItemRenderer() {
		    super();
		}

		@Override
		public void render(Listitem item, Object data, int count) throws Exception {
			CustomerDedup custDedup = (CustomerDedup) data;
			String ruleFields[] = { "" };
			if (custDedup.getStatus().equals("P")) {
				Listcell lc;
				lc = new Listcell();
				lc.setLabel("No Response From Posidex");
				lc.setParent(item);

				lc = new Listcell();
				Button button = new Button(Labels.getLabel("label_ExternalDedupCustomer_ReTrigger"));
				button.addEventListener(Events.ON_CLICK, event -> onClickButtonRetrigger(data));
				button.setParent(lc);
				item.getChildren().size();
				button.setVisible(true);
				lc.setParent(item);

			} else {
				success(item, data, custDedup, ruleFields);
			}
			item.setAttribute("data", data);
		}

	}

	private void onClickButtonView(Object data) {
		logger.debug(Literal.ENTERING);
		if (data == null) {
			return;
		}
		if (data instanceof CustomerDedup) {
			// Get the selected entity.
			CustomerDedup customerDedup = (CustomerDedup) data;
			HashMap<String, Object> arg = new HashMap<>();
			arg.put("enqiryModule", true);
			arg.put("customerDedup", customerDedup);

			try {
				Executions.createComponents("/WEB-INF/pages/CustomerMasters/Customer/CustExternalDedup.zul", null, arg);
			} catch (Exception e) {
				MessageUtil.showError(e);
			}

		}
		logger.debug(Literal.LEAVING);
	}

	@SuppressWarnings("unchecked")
	public void onClickButtonRetrigger(Object data) {
		logger.debug(Literal.ENTERING);
		if (data == null) {
			return;
		}
		if (data instanceof CustomerDedup) {

			if (triggerCount > 3) {
				onClose();
				MessageUtil.showInfo("label_External_Dedup_ErrMsg");
				return;
			}
			triggerCount++;
			// Get the selected entity.
			CustomerDedup customerDedup = (CustomerDedup) data;
			List<CustomerDedup> customerDedupList = dedupParmService.processRetrigger(customerDedup,
					PennantConstants.UCIC_RETRIGGER);
			this.custDedupListSize = (List<?>) customerDedupList;
			setListModelList(new ListModelList(custDedupListSize));
			this.listbox.renderAll();

		}
		logger.debug(Literal.LEAVING);
	}

	public void onClickButtonNew(Object data) {
		logger.debug(Literal.ENTERING);
		if (data == null) {
			return;
		}
		if (data instanceof CustomerDedup) {
			// Get the selected entity.
			CustomerDedup customerDedup = (CustomerDedup) data;
			CustomerDedup dedup = dedupParmService.processNewUCIC(customerDedup, PennantConstants.UCIC_NEW);
			if (dedup.getStatus().equals("C")) {
				setUserAction(1);
				List<CustomerDedup> custDedupList = new ArrayList<CustomerDedup>();
				custDedupList.add(dedup);
				setObject(custDedupList);
				onClose();
			} else if (dedup.getStatus().equals("P")) {
				MessageUtil.showInfo(dedup.getStatusMessage());
				return;
			}
		}
		logger.debug(Literal.LEAVING);
	}

	private void success(Listitem item, Object data, CustomerDedup custDedup, String[] ruleFields) throws Exception {
		String fieldValue = "";
		String currentFieldValue = "";
		Date dateFieldValue = new Date();
		if (custDedup.getQueryField() != null) {
			ruleFields = custDedup.getQueryField().split(",");
		}

		for (int j = 0; j < fieldString.length; j++) {
			final Listcell lc;
			String fieldMethod = null;
			if ("boolean".equals(data.getClass().getDeclaredField(fieldString[j]).getType().toString())) {
				fieldMethod = "is" + fieldString[j].substring(0, 1).toUpperCase() + fieldString[j].substring(1);
			} else {
				fieldMethod = "get" + fieldString[j].substring(0, 1).toUpperCase() + fieldString[j].substring(1);
			}
			if (data.getClass().getMethod(fieldMethod).getReturnType().equals(String.class)) {
				fieldValue = (String) data.getClass().getMethod(fieldMethod).invoke(data);
				currentFieldValue = (String) getCustomerDedup().getClass().getMethod(fieldMethod)
						.invoke(getCustomerDedup());
				// Customer EID Number formating
				if (StringUtils.equals(fieldMethod, "get" + Labels.getLabel("label_FinanceDeDupListCustCRCPR"))) {
					if (fieldValue != null && currentFieldValue != null) {
						fieldValue = PennantApplicationUtil.formatEIDNumber(fieldValue);
						currentFieldValue = PennantApplicationUtil.formatEIDNumber(currentFieldValue);
					}
				}
				lc = new Listcell(fieldValue);
				for (int k = 0; k < ruleFields.length; k++) {
					if (!StringUtils.equals(ruleFields[k], InterfaceConstants.DEDUP_CORE)) {
						if (StringUtils.equals(fieldMethod, "get" + ruleFields[k])) {
							if (StringUtils.equals(ruleFields[k], MOB_NUM)
									|| StringUtils.equals(ruleFields[k], CRCPR)) {
								String dedupValue = StringUtils.trimToEmpty(fieldValue).replace("-", "");
								String currentValue = StringUtils.trimToEmpty(currentFieldValue).replace("-", "");
								if (StringUtils.equalsIgnoreCase(StringUtils.trimToEmpty(dedupValue),
										StringUtils.trimToEmpty(currentValue))) {
									lc.setStyle(COLOR);
								}
							} else if (StringUtils.equalsIgnoreCase(StringUtils.trimToEmpty(fieldValue),
									StringUtils.trimToEmpty(currentFieldValue))) {
								lc.setStyle(COLOR);
							}
						}
					} else {
						lc.setStyle("font-weight:bold;color:#6699ff;");
					}
				}

			} else if (data.getClass().getMethod(fieldMethod).getReturnType().equals(Date.class)) {
				dateFieldValue = (Date) data.getClass().getMethod(fieldMethod).invoke(data);
				Date curdateFieldValue = (Date) getCustomerDedup().getClass().getMethod(fieldMethod)
						.invoke(getCustomerDedup());
				lc = new Listcell(DateUtil.formatToLongDate(dateFieldValue));

				if (dateFieldValue != null && curdateFieldValue != null) {
					for (int k = 0; k < ruleFields.length; k++) {
						if (!StringUtils.equals(ruleFields[k], InterfaceConstants.DEDUP_CORE)) {
							if (StringUtils.equals(fieldMethod, "get" + ruleFields[k])) {
								if (dateFieldValue.compareTo(curdateFieldValue) == 0) {
									lc.setStyle(COLOR);
								}
							}
						} else {
							lc.setStyle("font-weight:bold;color:#6699ff;");
						}
					}
				}
			} else if ("boolean".equals(data.getClass().getMethod(fieldMethod).getReturnType().toString())) {
				Checkbox chk = new Checkbox();

				boolean newRule = (Boolean) data.getClass().getMethod("isNewRule").invoke(data);
				boolean newRecord = (Boolean) data.getClass().getMethod("isNewCustDedupRecord").invoke(data);
				String overrideUser = (String) data.getClass().getMethod("getOverrideUser").invoke(data);

				if (newRule && !newRecord && (Boolean) data.getClass().getMethod(fieldMethod).invoke(data)) {
					chk.setDisabled(false);
					chk.setChecked(false);
				} else if (newRule && !newRecord && !(Boolean) data.getClass().getMethod(fieldMethod).invoke(data)) {
					chk.setDisabled(true);
					chk.setChecked((Boolean) data.getClass().getMethod(fieldMethod).invoke(data));
				} else if (newRule && !(Boolean) data.getClass().getMethod(fieldMethod).invoke(data)) {
					chk.setDisabled(false);
					chk.setChecked((Boolean) data.getClass().getMethod(fieldMethod).invoke(data));
				} else if (overrideUser != null && overrideUser.contains(curAccessedUser)) {
					chk.setDisabled(true);
					chk.setChecked((Boolean) data.getClass().getMethod(fieldMethod).invoke(data));
				} else if (!(Boolean) data.getClass().getMethod(fieldMethod).invoke(data)) {
					chk.setDisabled(true);
				}
				if (StringUtils.equals(ImplementationConstants.CLIENT_NAME, ImplementationConstants.CLIENT_BFL)) {
					chk.setDisabled(false);
				}
				lc = new Listcell();
				chk.setParent(lc);
			} else {
				fieldValue = data.getClass().getMethod(fieldMethod).invoke(data).toString();
				lc = new Listcell(fieldValue);
			}
			lc.setParent(item);
		}

		if (isExtDedup) {
			// adding a button to view the customer details
			Listcell lc;
			lc = new Listcell();
			Button button = new Button(Labels.getLabel("label_ExternalDedupCustomer_View"));
			button.addEventListener(Events.ON_CLICK, event -> onClickButtonView(data));
			button.setParent(lc);
			item.getChildren().size();
			button.setVisible(true);
			lc.setParent(item);

			lc = new Listcell();
			Button existBtn = new Button(Labels.getLabel("label_ExternalDedupCustomer_Existing"));
			existBtn.addEventListener(Events.ON_CLICK, event -> onClickButtonExisting(data));
			existBtn.setParent(lc);
			item.getChildren().size();
			existBtn.setVisible(true);
			lc.setParent(item);

			lc = new Listcell();
			Button newBtn = new Button(Labels.getLabel("label_ExternalDedupCustomer_New"));
			newBtn.addEventListener(Events.ON_CLICK, event -> onClickButtonNew(data));
			newBtn.setParent(lc);
			item.getChildren().size();
			newBtn.setVisible(true);
			lc.setParent(item);

			String score = custDedup.getScore();
			lc = new Listcell(score);
			lc.setParent(item);
		}
	}

	public void onClickButtonExisting(Object data) {
		logger.debug(Literal.ENTERING);
		if (data == null) {
			return;
		}
		if (data instanceof CustomerDedup) {
			// Get the selected entity.
			CustomerDedup customerDedup = (CustomerDedup) data;
			CustomerDedup dedup = dedupParmService.processNewUCIC(customerDedup, PennantConstants.UCIC_EXISTING);
			if (dedup.getStatus().equals("C")) {
				setUserAction(1);
				List<CustomerDedup> custDedupList = new ArrayList<CustomerDedup>();
				custDedupList.add(dedup);
				setObject(custDedupList);
				onClose();
			} else if (dedup.getStatus().equals("P")) {
				MessageUtil.showInfo(dedup.getStatusMessage());
				return;
			}

		}
		logger.debug(Literal.LEAVING);
	}

	public final class OnPagingEventListener implements EventListener<Event> {

		public OnPagingEventListener() {
		    super();
		}

		@Override
		public void onEvent(Event event) throws Exception {
			final PagingEvent pe = (PagingEvent) event;
			final int pageNo = pe.getActivePage();
			final int start = pageNo * getPageSize();
			final String searchText = ShowExtCustomerDedupListBox.this._textbox.getValue();
			refreshModel(searchText, start);
		}
	}

	// Setter/Getter

	public Object getObject() {
		return this.objClass;
	}

	private void setObject(Object objClass) {
		this.objClass = objClass;
	}

	public Textbox get_textbox() {
		return _textbox;
	}

	public void set_textbox(Textbox textbox) {
		this._textbox = textbox;
	}

	public Paging get_paging() {
		return _paging;
	}

	public void set_paging(Paging paging) {
		this._paging = paging;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public Listbox getListbox() {
		return listbox;
	}

	public void setListbox(Listbox listbox) {
		this.listbox = listbox;
	}

	public ListModelList getListModelList() {
		return listModelList;
	}

	public void setListModelList(ListModelList listModelList) {
		this.listModelList = listModelList;
	}

	public int get_width() {
		return _width;
	}

	public void set_width(int width) {
		this._width = width;
	}

	public PagedListService getPagedListService() {
		return pagedListService;
	}

	public void setPagedListService(PagedListService pagedListService) {
		this.pagedListService = pagedListService;
	}

	public String[] getFieldString() {
		return fieldString;
	}

	public void setFieldString(String[] fieldString) {
		this.fieldString = fieldString;
	}

	public ModuleMapping getModuleMapping() {
		return moduleMapping;
	}

	public void setModuleMapping(ModuleMapping moduleMapping) {
		this.moduleMapping = moduleMapping;
	}

	public int get_height() {
		return _height;
	}

	public int getUserAction() {
		return userAction;
	}

	public void setUserAction(int userAction) {
		this.userAction = userAction;
	}

	private String getLabel(String value) {
		String label = Labels.getLabel(value + "_label");
		if (StringUtils.isBlank(label)) {
			return value;
		}
		return label;
	}

	public CustomerDedup getCustomerDedup() {
		return customerDedup;
	}

	public void setCustomerDedup(CustomerDedup customerDedup) {
		this.customerDedup = customerDedup;
	}

	public DedupParmService getDedupParmService() {
		return dedupParmService;
	}

	public void setDedupParmService(DedupParmService dedupParmService) {
		this.dedupParmService = dedupParmService;
	}

}
