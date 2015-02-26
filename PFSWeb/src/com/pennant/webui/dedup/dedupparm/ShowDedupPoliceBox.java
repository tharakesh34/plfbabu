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

import com.pennant.backend.model.ModuleMapping;
import com.pennant.backend.model.policecase.PoliceCase;
import com.pennant.backend.service.PagedListService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.util.PennantAppUtil;
import com.pennant.webui.util.PTMessageUtils;


public class ShowDedupPoliceBox extends Window implements Serializable {

	private static final long serialVersionUID = -2854517425413800019L;
	private static final Logger logger = Logger.getLogger(ShowDedupPoliceBox.class);
	private Textbox _textbox;
	private Paging _paging;
	private int pageSize = 10;
	private Listbox listbox;
	private ListModelList<PoliceCase> listModelList;
	private final int _height = 300;
	private int _width = 800;
	private transient PagedListService pagedListService;
	private String[] fieldString;
	private ModuleMapping moduleMapping = null;
	private List<?> dedupListSize = null;
	private Object objClass = null;
	private int userAction = 0;
	private static List<Object> complist;
	private static String compListFileds[];
	private static Object compObject;
	static boolean isCustomerDedup = false;
	private String[] listHeaders;
	private Rows rows;
	private long curAccessedUser;
	public ShowDedupPoliceBox() {
		super();
	}

	/**
	 * The Call method.
	 * 
	 * @param parent
	 *            The parent component
	 * @return a BeanObject from the listBox or null.
	 */
	public static Object show(Component parent, List<?> dedupList, String dedupFields,PoliceCase policeCase,long curUser) {
		return new ShowDedupPoliceBox(parent, dedupList, dedupFields,policeCase,curUser);
	}


	/**
	 * Private Constructor. So it can only be created with the static show()
	 * method.<br>
	 * 
	 * @param parent
	 */
	private ShowDedupPoliceBox(Component parent, List<?> listCode, String dedupFields,PoliceCase policeCase,long curUser) {
		super();
		this.dedupListSize = (List<?>) listCode;
		this.fieldString = dedupFields.split(",");
		curAccessedUser = curUser;
		setParent(parent);
		createBox(policeCase);
	}

	@SuppressWarnings({ "deprecation", "unchecked" })
	private void createBox(PoliceCase policeCase) {
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
		btnCancel.setLabel("Cases Found");
		btnCancel.addEventListener("onClick", new OnCancelListener());
		btnCancel.setParent(startToolbar);

		// Button for Help
		final Button btnHelp = new Button();
		btnHelp.setSclass("z-toolbarbutton");
		btnHelp.setLabel("Help");
		btnHelp.setParent(endToolbar);

		//Label For Title
		Label titleLabel = new Label();
		titleLabel.setValue(Labels.getLabel("window_PoliceCaseListDialog.title"));
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
		rows.appendChild(prepareRow(new Row(), Labels.getLabel("label_PoliceCaseListDialog_CustCIF.value"), 
				policeCase.getCustCIF(), Labels.getLabel("label_PoliceCaseListDialog_CustDOB.value"), 
				PennantAppUtil.formateDate(policeCase.getCustDOB(),PennantConstants.dateFormat)));
		rows.appendChild(prepareRow(new Row(),  Labels.getLabel("label_PoliceCaseListDialog_CustFName.value"),
				policeCase.getCustFName(),  Labels.getLabel("label_PoliceCaseListDialog_CustLName.value"), policeCase.getCustLName()));
		rows.appendChild(prepareRow(new Row(), Labels.getLabel("label_PoliceCaseListDialog_CustEIDNumber.value"),
				policeCase.getCustCRCPR(), Labels.getLabel("label_PoliceCaseListDialog_CustPassport.value"),policeCase.getCustPassPort()));
		rows.appendChild(prepareRow(new Row(), Labels.getLabel("label_PoliceCaseListDialog_CustMobileNumber.value"),
				policeCase.getCustMobileNumber(), Labels.getLabel("label_PoliceCaseListDialog_CustNationality.value"), policeCase.getCustNationality()));

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

		setListModelList(new ListModelList<PoliceCase>((List<PoliceCase>)dedupListSize));
		this.listbox.setModel(getListModelList());
		this.listbox.setItemRenderer(new PoliceCaseDedupItemRenderer());

		final Listhead listhead = new Listhead();
		listhead.setParent(this.listbox);
		String headerList = Labels.getLabel("listHeader_PoliceCase_label");
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

	/**
	 * Inner OnProceedListener class.<br>
	 */
	final class OnProceedListener implements EventListener<Event> {
		@Override
		public void onEvent(Event event) throws Exception {

			setUserAction(1);
			List<PoliceCase>policeCaseList = new ArrayList<PoliceCase>();
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

							PoliceCase policeCase = (PoliceCase) listitem.getAttribute("data");
							if(policeCase.getOverrideUser() == 0){
								policeCase.setOverride(true);
								policeCase.setOverrideUser(curAccessedUser);
								policeCaseList.add(policeCase);
							}
						}
					}
				}

			}
			setObject(policeCaseList);
			if(getUserAction() == -1){
				PTMessageUtils.showErrorMessage("All Police/Court Cases Data must be overriden to Proceed Further.");
			}else{
				onClose();
			}
		}
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
	 * Inner ListItemRenderer class.<br>
	 */
	final class PoliceCaseDedupItemRenderer implements ListitemRenderer<Object> {
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
					}else if(overrideUser != 0 && overrideUser == curAccessedUser){
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


	private String getLabel(String value) {
		String label = Labels.getLabel(value + "_label");
		if (StringUtils.trimToEmpty(label).equals("")) {
			return value;
		}
		return label;
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


	public final class OnPagingEventListener implements EventListener<Event> {
		@Override
		public void onEvent(Event event) throws Exception {
			final PagingEvent pe = (PagingEvent) event;
			final int pageNo = pe.getActivePage();
			final int start = pageNo * getPageSize();
			final String searchText = ShowDedupPoliceBox.this._textbox.getValue();
			refreshModel(searchText, start);
		}
	}
	void refreshModel(String searchText, int start) {
		logger.debug("Entering");
		// clear old data
		getListModelList().clear();
		this._paging.setTotalSize(dedupListSize.size());
		// set the model
		setListModelList(new ListModelList<PoliceCase>());
		this.listbox.setModel(getListModelList());
		logger.debug("Leaving");
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

	public void set_textbox(Textbox _textbox) {
		this._textbox = _textbox;
	}

	public Paging get_paging() {
		return _paging;
	}

	public void set_paging(Paging _paging) {
		this._paging = _paging;
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

	public ListModelList<PoliceCase> getListModelList() {
		return listModelList;
	}

	public void setListModelList(ListModelList<PoliceCase> listModelList) {
		this.listModelList = listModelList;
	}

	public int get_width() {
		return _width;
	}

	public void set_width(int _width) {
		this._width = _width;
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

	public List<?> getDedupListSize() {
		return dedupListSize;
	}

	public void setDedupListSize(List<?> dedupListSize) {
		this.dedupListSize = dedupListSize;
	}

	public Object getObjClass() {
		return objClass;
	}

	public void setObjClass(Object objClass) {
		this.objClass = objClass;
	}

	public int getUserAction() {
		return userAction;
	}

	public void setUserAction(int userAction) {
		this.userAction = userAction;
	}

	public static List<Object> getComplist() {
		return complist;
	}

	public static void setComplist(List<Object> complist) {
		ShowDedupPoliceBox.complist = complist;
	}

	public static String[] getCompListFileds() {
		return compListFileds;
	}

	public static void setCompListFileds(String[] compListFileds) {
		ShowDedupPoliceBox.compListFileds = compListFileds;
	}

	public static Object getCompObject() {
		return compObject;
	}

	public static void setCompObject(Object compObject) {
		ShowDedupPoliceBox.compObject = compObject;
	}

	public static boolean isCustomerDedup() {
		return isCustomerDedup;
	}

	public static void setCustomerDedup(boolean isCustomerDedup) {
		ShowDedupPoliceBox.isCustomerDedup = isCustomerDedup;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public static Logger getLogger() {
		return logger;
	}

	public int get_height() {
		return _height;
	}





}
