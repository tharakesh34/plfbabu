package com.pennant.webui.dedup.dedupparm;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Path;
import org.zkoss.zk.ui.SuspendNotAllowedException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Center;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Column;
import org.zkoss.zul.Columns;
import org.zkoss.zul.Div;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Intbox;
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
import org.zkoss.zul.Row;
import org.zkoss.zul.Rows;
import org.zkoss.zul.South;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Toolbar;
import org.zkoss.zul.Window;
import org.zkoss.zul.event.PagingEvent;

import com.pennant.app.util.DateUtility;
import com.pennant.backend.model.ModuleMapping;
import com.pennant.backend.model.blacklist.BlackListCustomers;
import com.pennant.backend.service.PagedListService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.util.PennantAppUtil;
import com.pennant.webui.util.PTMessageUtils;

@SuppressWarnings("rawtypes")
public class ShowBlackListDetailBox extends Window implements Serializable {
	private static final long serialVersionUID = -2854517425413800019L;
	private static final Logger logger = Logger
			.getLogger(ShowBlackListDetailBox.class);
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
	private List<?> customerBlackListSize = null;
	private Object objClass = null;
	private int userAction = 0;
	private String[] listHeaders;
	private long curAccessedUser;

	public ShowBlackListDetailBox() {
		super();
	}

	/**
	 * The Call method.
	 * 
	 * @param parent
	 *            The parent component
	 * @return a BeanObject from the listBox or null.
	 */
	public static Object show(Component parent, List<?> dedupList,
			String dedupFields, BlackListCustomers blackListCustomers, long curUser) {
		return new ShowBlackListDetailBox(parent, dedupList, dedupFields,
				blackListCustomers, curUser);
	}

	/**
	 * Private Constructor. So it can only be created with the static show()
	 * method.<br>
	 * 
	 * @param parent
	 */
	private ShowBlackListDetailBox(Component parent, List<?> listCode,
			String dedupFields, BlackListCustomers blackListCustomers, long curUser) {
		super();
		this.customerBlackListSize = (List<?>) listCode;
		this.fieldString = dedupFields.split(",");
		curAccessedUser = curUser;
		setParent(parent);
		createBox(blackListCustomers);
	}

	@SuppressWarnings({ "deprecation", "unchecked" })
	private void createBox(BlackListCustomers blCustomers) {
		logger.debug("Entering");

		int borderLayoutHeight = ((Intbox) Path
				.getComponent("/outerIndexWindow/currentDesktopHeight"))
				.getValue().intValue()
				- PennantConstants.borderlayoutMainNorth;
		int borderLayoutWidth = ((Intbox) Path
				.getComponent("/outerIndexWindow/currentDesktopWidth"))
				.getValue().intValue();
		this.setWidth((borderLayoutWidth -100) + "px");
		this.setHeight((borderLayoutHeight -150) + "px");


		// Window
		int listRows = Math.round((borderLayoutHeight-300) / 25) - 2;
		setPageSize(listRows);
		this.setVisible(true);
		this.setClosable(true);

		// BorderLayout
		final Borderlayout brdLayout = new Borderlayout();
		brdLayout.setHeight("100%");
		brdLayout.setWidth("100%");
		brdLayout.setParent(this);

		//Center for BorderLayout
		final Center center = new Center();
		center.setBorder("none");
		center.setFlex(true);
		center.setParent(brdLayout);

		// Div tag for Center
		Div divCenter = new Div();
		divCenter.setParent(center);

		//North for BorderLayout
		North north = new North();
		north.setParent(brdLayout);

		//Div for North
		Div divNorth = new Div();
		divNorth.setSclass("z-toolbar");
		divNorth.setParent(north);

		//South for BorderLayout
		South south = new South();
		south.setParent(brdLayout);

		//Groupbox for Rows
		Groupbox grpBox = new Groupbox();
		grpBox.setMold("3d");
		grpBox.setParent(divCenter);

		// Groupbox for Listbox
		Groupbox grpBoxForListbox = new Groupbox();
		grpBoxForListbox.setParent(divCenter);

		//Components for buttons alignments
		Hbox hboxForButton = new Hbox();
		hboxForButton.setSclass("hboxRemoveWhiteStrips");
		hboxForButton.setWidth("100%");
		hboxForButton.setWidths("35%,30%,35%");
		hboxForButton.setPack("stretch");
		hboxForButton.setParent(divNorth);

		//Toolbar for start
		Toolbar startToolbar = new Toolbar();
		startToolbar.setAlign("start");
		startToolbar.setSclass("toolbar-start");
		startToolbar.setParent(hboxForButton);

		//Toolbar for Center
		Toolbar centerToolbar = new Toolbar();
		centerToolbar.setAlign("center");
		centerToolbar.setSclass("toolbar-center");
		centerToolbar.setParent(hboxForButton);

		//Toolbar for End
		Toolbar endToolbar = new Toolbar();
		endToolbar.setAlign("end");
		endToolbar.setSclass("toolbar-end");
		endToolbar.setParent(hboxForButton);

		// Button for Clean
		final Button btnProceed = new Button();
		btnProceed.setSclass("z-toolbarbutton");
		btnProceed.setLabel("Clean");
		btnProceed.addEventListener("onClick", new OnProceedListener());
		btnProceed.setParent(startToolbar);

		// Button for BlackListed
		final Button btnCancel = new Button();
		btnCancel.setSclass("z-toolbarbutton");
		btnCancel.setLabel("BlackListed");
		btnCancel.addEventListener("onClick", new OnCancelListener());
		btnCancel.setParent(startToolbar);

		// Button for Help
		final Button btnHelp = new Button();
		btnHelp.setSclass("z-toolbarbutton");
		btnHelp.setLabel("Help");
		btnHelp.setParent(endToolbar);

		//Label For Title
		Label titleLabel = new Label();
		titleLabel.setValue(Labels.getLabel("window_BlackListCheckDialog.title"));
		titleLabel.setSclass("label-heading");
		titleLabel.setParent(centerToolbar);

		// Grid Details for Checking Customer Details
		Grid grid = new Grid();
		//grid.setSizedByContent(true);
		grid.setParent(grpBox);

		Columns columns = new Columns();
		Column column1 = new Column();
		Column column2 = new Column();
		Column column3 = new Column();
		Column column4 = new Column();

		column1.setWidth("15%");
		column2.setWidth("35%");
		column3.setWidth("15%");
		column4.setWidth("35%");

		columns.appendChild(column1);
		columns.appendChild(column2);
		columns.appendChild(column3);
		columns.appendChild(column4); 

		grid.appendChild(columns);

		// Rows Preparation
		rows = new Rows();
		rows.setParent(grid);

		rows.appendChild(prepareRow(new Row(),Labels.getLabel("label_BlackListCheckDialog_CustCIF.value"),
				blCustomers.getCustCIF(),Labels.getLabel("label_BlackListCheckDialog_DOB.value"), DateUtility
				.formateDate(blCustomers.getCustDOB(),PennantConstants.dateFormate)));
		rows.appendChild(prepareRow(new Row(),	Labels.getLabel("label_BlackListCheckDialog_CustFName.value"),
				blCustomers.getCustFName(),	Labels.getLabel("label_BlackListCheckDialog_CustLName.value"),blCustomers.getCustLName()));
		rows.appendChild(prepareRow(new Row(),	Labels.getLabel("label_BlackListCheckDialog_EID.value"),
				blCustomers.getCustCRCPR(),	Labels.getLabel("label_BlackListCheckDialog_Passport.value"),blCustomers.getCustPassportNo()));
		rows.appendChild(prepareRow(new Row(),Labels.getLabel("label_BlackListCheckDialog_MobileNum.value"),
				blCustomers.getPhoneNumber(),Labels.getLabel("label_BlackListCheckDialog_Nationality.value"),blCustomers.getCustNationality()));

		// ListBox
		this.listbox = new Listbox();
		listbox.setStyle("border: none;");
		this.listbox.setHeight(((borderLayoutHeight / 2) + 40) + "px");
		this.listbox.setVisible(true);
		this.listbox.setSizedByContent(true);
		this.listbox.setSpan("true");
		this.listbox.setParent(grpBoxForListbox);
		
		this.listbox.setMold("paging");
		this.listbox.setPageSize(getPageSize());

		setListModelList(new ListModelList(customerBlackListSize));
		this.listbox.setModel(getListModelList());
		this.listbox.setItemRenderer(new BlackListBoxItemRenderer());

		final Listhead listhead = new Listhead();
		listhead.setParent(this.listbox);
		String headerList = Labels.getLabel("listHeader_BlackListCustomer_label");
		this.listHeaders = headerList.split(",");
		for (int i = 0; i < this.listHeaders.length; i++) {
			final Listheader listheader = new Listheader();
			listheader.setLabel(getLabel(this.listHeaders[i]));
			listheader.setHflex("min");
			listheader.setParent(listhead);
		}

		try {
			doModal();
		} catch (final SuspendNotAllowedException e) {
			e.printStackTrace();
			logger.fatal("", e);
			this.detach();
		}
		logger.debug("Leaving");
	}

	private Row prepareRow(Row row, String label1, String value1,
			String label2, String value2) {

		Label label = new Label();
		label.setValue(label1);
		row.appendChild(label);

		Textbox textbox = new Textbox();
		textbox.setWidth("180px");
		textbox.setReadonly(true);
		textbox.setValue(value1);
		row.appendChild(textbox);

		label = new Label();
		label.setValue(label2);
		row.appendChild(label);

		textbox = new Textbox();
		textbox.setWidth("180px");
		textbox.setReadonly(true);
		textbox.setValue(value2);
		row.appendChild(textbox);

		return row;
	}

	void refreshModel(String searchText, int start) {
		logger.debug("Entering");
		// clear old data
		getListModelList().clear();
		this._paging.setTotalSize(customerBlackListSize.size());
		// set the model
		setListModelList(new ListModelList());
		this.listbox.setModel(getListModelList());
		logger.debug("Leaving");
	}

	/**
	 * Inner Cancel class.<br>
	 */
	final class OnCancelListener implements EventListener<Event> {
		@Override
		public void onEvent(Event event) throws Exception {
			setUserAction(0);
			setObject(null);
			onClose();
		}
	}

	/**
	 * Inner OnProceedListener class.<br>
	 */
	final class OnProceedListener implements EventListener<Event> {
		@Override
		public void onEvent(Event event) throws Exception {

			setUserAction(1);
			List<BlackListCustomers> blackListData = new ArrayList<BlackListCustomers>();
			for (int i = 0; i < listbox.getItems().size(); i++) {
				Listitem listitem = listbox.getItems().get(i);
				List<Component> componentList = ((Listcell) listitem.getLastChild())
						.getChildren();
				if(componentList != null && componentList.size() > 0){
					Component component = componentList.get(0);
					if (component instanceof Checkbox) {
						if (!((Checkbox) component).isChecked()) {
							setUserAction(-1);
						}else{
							
							BlackListCustomers customer = (BlackListCustomers) listitem.getAttribute("data");
							if(customer.getOverrideUser() == 0){
								customer.setOverride(true);
								customer.setOverrideUser(curAccessedUser);
								blackListData.add(customer);
							}
						}
					}
				}

			}
			if(getUserAction() == -1){
				PTMessageUtils.showErrorMessage("All BlackListed Data must be overriden to Proceed Further.");
			}else{
				setObject(blackListData);
				onClose();
			}
		}
	}

	/**
	 * Inner ListItemRenderer class.<br>
	 */
	final class BlackListBoxItemRenderer implements ListitemRenderer<Object> {
		@Override
		public void render(Listitem item, Object data, int count)
				throws Exception {
			String fieldValue = "";
			Date dateFieldValue = new Date();
			for (int j = 0; j < fieldString.length; j++) {
				final Listcell lc;
				String fieldMethod = "get"+ fieldString[j].substring(0, 1).toUpperCase()+ fieldString[j].substring(1);
				if (data.getClass().getMethod(fieldMethod).getReturnType().equals(String.class)) {
					fieldValue = (String) data.getClass().getMethod(fieldMethod).invoke(data);
					lc = new Listcell(fieldValue);
				} else if (data.getClass().getMethod(fieldMethod).getReturnType().equals(Date.class)) {
					dateFieldValue = (Date) data.getClass().getMethod(fieldMethod).invoke(data);
					lc = new Listcell(PennantAppUtil.formateDate(dateFieldValue, PennantConstants.dateFormat));
				} else if(data.getClass().getMethod(fieldMethod).getReturnType().toString().equals("boolean")) {
					Checkbox chk = new Checkbox();
					
					long overrideUser = (Long) data.getClass().getMethod("getOverrideUser").invoke(data);
					if(overrideUser != 0 && overrideUser != curAccessedUser){
						chk.setDisabled(true);
						chk.setChecked((Boolean)data.getClass().getMethod(fieldMethod).invoke(data));
					}else if(!(Boolean)data.getClass().getMethod(fieldMethod).invoke(data)) {
						chk.setDisabled(true);
					}
					lc = new Listcell();
					chk.setParent(lc);
				} else {
					fieldValue = data.getClass().getMethod(fieldMethod).invoke(data).toString();
					lc = new Listcell(fieldValue);
				}
				lc.setParent(item);
			}
			
			item.setAttribute("data", data);
		}
	}

	public final class OnPagingEventListener implements EventListener<Event> {
		@Override
		public void onEvent(Event event) throws Exception {
			final PagingEvent pe = (PagingEvent) event;
			final int pageNo = pe.getActivePage();
			final int start = pageNo * getPageSize();
			final String searchText = ShowBlackListDetailBox.this._textbox.getValue();
			refreshModel(searchText, start);
		}
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// ++++++++++++++++ Setter/Getter ++++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
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

	public List<?> getDedupListSize() {
		return customerBlackListSize;
	}

	public void setDedupListSize(List<?> dedupListSize) {
		this.customerBlackListSize = dedupListSize;
	}

	public int getUserAction() {
		return userAction;
	}

	public void setUserAction(int userAction) {
		this.userAction = userAction;
	}

	private String getLabel(String value) {
		String label = Labels.getLabel(value + "_label");
		if (StringUtils.trimToEmpty(label).equals("")) {
			return value;
		}
		return label;
	}

}
