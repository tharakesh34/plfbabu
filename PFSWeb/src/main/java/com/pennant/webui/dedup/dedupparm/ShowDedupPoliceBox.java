package com.pennant.webui.dedup.dedupparm;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
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
import com.pennant.backend.model.applicationmaster.PoliceCaseDetail;
import com.pennant.backend.model.policecase.PoliceCase;
import com.pennant.backend.service.PagedListService;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pennapps.core.feature.model.ModuleMapping;

public class ShowDedupPoliceBox extends Window implements Serializable {
	private static final long serialVersionUID = -2854517425413800019L;
	private static final Logger logger = Logger.getLogger(ShowDedupPoliceBox.class);

	private Textbox _textbox;
	private Paging _paging;
	private int pageSize = 10;
	private Listbox listbox;
	private ListModelList<PoliceCaseDetail> listModelList;
	private final int _height = 300;
	private int _width = 800;
	private transient PagedListService pagedListService;
	private String[] fieldString;
	private ModuleMapping moduleMapping = null;
	private List<PoliceCaseDetail> dedupListSize = null;
	private Object objClass = null;
	private int userAction = 0;
	private static List<Object> complist;
	private static String compListFileds[];
	private static Object compObject;
	static boolean isCustomerDedup = false;
	private String[] listHeaders;
	private Rows rows;
	private String curAccessedUser;
	public PoliceCaseDetail curCustomer;
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
	public static Object show(Component parent, List<PoliceCaseDetail> dedupList, String dedupFields,PoliceCaseDetail policeCase,String curUser) {
		return new ShowDedupPoliceBox(parent, dedupList, dedupFields,policeCase,curUser);
	}


	/**
	 * Private Constructor. So it can only be created with the static show()
	 * method.<br>
	 * 
	 * @param parent
	 */
	private ShowDedupPoliceBox(Component parent, List<PoliceCaseDetail> listCode, String dedupFields,PoliceCaseDetail policeCase,String curUser) {
		super();
		this.dedupListSize = (List<PoliceCaseDetail>) listCode;
		this.fieldString = dedupFields.split(",");
		curAccessedUser = curUser;
		setParent(parent);
		createBox(policeCase);
	}

	@SuppressWarnings("deprecation")
	private void createBox(PoliceCaseDetail policeCase) {
		logger.debug("Entering");

		setCurCustomer(policeCase);
		int borderLayoutHeight = GFCBaseCtrl.getDesktopHeight();
		int borderLayoutWidth = GFCBaseCtrl.getDesktopWidth();
		this.setWidth((borderLayoutWidth -100) + "px");
		this.setHeight((borderLayoutHeight -150) + "px");


		// Window
		int listRows = Math.round((borderLayoutHeight-300) / 25) - 3;
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
				DateUtility.formatToLongDate(policeCase.getCustDOB())));
		rows.appendChild(prepareRow(new Row(),  Labels.getLabel("label_PoliceCaseListDialog_CustFName.value"),
				policeCase.getCustFName(),  Labels.getLabel("label_PoliceCaseListDialog_CustLName.value"), policeCase.getCustLName()));
		rows.appendChild(prepareRow(new Row(), Labels.getLabel("label_PoliceCaseListDialog_CustEIDNumber.value"),
				PennantApplicationUtil.formatEIDNumber(policeCase.getCustCRCPR()), Labels.getLabel("label_PoliceCaseListDialog_CustPassport.value"),policeCase.getCustPassportNo()));
		rows.appendChild(prepareRow(new Row(), Labels.getLabel("label_PoliceCaseListDialog_CustMobileNumber.value"),
				policeCase.getMobileNumber(), Labels.getLabel("label_PoliceCaseListDialog_CustNationality.value"), policeCase.getCustNationality()));

		// ListBox
		this.listbox = new Listbox();
		listbox.setStyle("border: none;");
		this.listbox.setHeight((borderLayoutHeight-310 ) + "px");
		this.listbox.setVisible(true);
		this.listbox.setSizedByContent(true);
		this.listbox.setSpan("true");
		this.listbox.setParent(grpBoxForListbox);

		setListModelList(new ListModelList<PoliceCaseDetail>((List<PoliceCaseDetail>)dedupListSize));
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
			if("Product".equals(this.listHeaders[i])){
				listheader.setVisible(false);
			}
		}

		try {
			doModal();
		} catch (final SuspendNotAllowedException e) {
			logger.error("Exception: ", e);
			this.detach();
		}
		logger.debug("Leaving");
	}

	/**
	 * Inner OnProceedListener class.<br>
	 */
	final class OnProceedListener implements EventListener<Event> {
		
		public OnProceedListener() {
			
		}
		
		@Override
		public void onEvent(Event event) throws Exception {
			setUserAction(1);
			List<PoliceCase> policeCaseList = new ArrayList<PoliceCase>();
			
			for (int i = 0; i < listbox.getItems().size(); i++) {
				Listitem listitem = listbox.getItems().get(i);
				List<Component> componentList = ((Listcell) listitem.getLastChild().getPreviousSibling())
						.getChildren();
				if(componentList != null && componentList.size() > 0){
					Component component = componentList.get(0);
					if (component instanceof Checkbox) {
						if (!((Checkbox) component).isChecked()) {
							setUserAction(-1);
						}else{
							PoliceCaseDetail policeCase = (PoliceCaseDetail) listitem.getAttribute("data");
							
								if(policeCase.isNewPolicecaseRecord()){
								policeCase.setOverrideUser(curAccessedUser);
								}
								PoliceCase policeCheck = dosetGrouping(policeCase);
								policeCaseList.add(policeCheck);
							}
						
					}
				}

			}
			
			if(getUserAction() == -1){
				MessageUtil.showError(Labels.getLabel("label_OverrideMessage"));
			}else{
				setObject(policeCaseList);
				onClose();
			}
		}

		private PoliceCase dosetGrouping(PoliceCaseDetail policeCase) {
			logger.debug("Entering");
			PoliceCase policeCheck = new PoliceCase();
			policeCheck.setFinReference(policeCase.getFinReference());
			policeCheck.setCustCIF(policeCase.getCustCIF());
			policeCheck.setCustCRCPR(policeCase.getCustCRCPR());
			policeCheck.setCustDOB(policeCase.getCustDOB());
			policeCheck.setCustFName(policeCase.getCustFName());
			policeCheck.setCustLName(policeCase.getCustLName());
			policeCheck.setMobileNumber(policeCase.getMobileNumber());
			policeCheck.setCustNationality(policeCase.getCustNationality());
			policeCheck.setCustPassportNo(policeCase.getCustPassportNo());
			policeCheck.setOverrideUser(policeCase.getOverrideUser());
			policeCheck.setOverride(policeCase.isOverride());
			policeCheck.setPoliceCaseRule(policeCase.getPoliceCaseRule());
			policeCheck.setCustProduct(policeCase.getCustProduct());
			policeCheck.setNewPolicecaseRecord(policeCase.isNewPolicecaseRecord());
			logger.debug("Leaving");
			return policeCheck;
			
		}
	}

	/**
	 * Inner Cancel class.<br>
	 */
	final class OnCancelListener implements EventListener<Event> {
		
		public OnCancelListener() {
			
		}
		
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
		
		public PoliceCaseDedupItemRenderer() {
			
		}
		
		@Override
		public void render(Listitem item, Object data, int count)
				throws Exception {
			String fieldValue = "";
			Date dateFieldValue = new Date();
			String currentFieldValue = "";
			PoliceCaseDetail policeCaselst = (PoliceCaseDetail)data;
			String ruleFields[] = {""};
			if(policeCaselst.getPoliceCaseRule()!=null){
			 ruleFields = StringUtils.trimToEmpty(policeCaselst.getRules())
					.split(",");
			}
			for (int j = 0; j < fieldString.length; j++) {
				final Listcell lc;
				String fieldMethod = null;
				if("boolean".equals(data.getClass().getDeclaredField(fieldString[j]).getType().toString())){
					fieldMethod = "is"
							+ fieldString[j].substring(0, 1).toUpperCase()
							+ fieldString[j].substring(1);
				} else {
					fieldMethod = "get"
							+ fieldString[j].substring(0, 1).toUpperCase()
							+ fieldString[j].substring(1);
				}
				if (data.getClass().getMethod(fieldMethod).getReturnType().equals(String.class)) {
					fieldValue = (String) data.getClass().getMethod(fieldMethod).invoke(data);
					currentFieldValue = (String) getCurCustomer().getClass().getMethod(fieldMethod).invoke(getCurCustomer());
					if(fieldMethod.equals("get"+ Labels.getLabel("label_FinanceDeDupListCustCRCPR"))){
						if(fieldValue != null && currentFieldValue != null){
							fieldValue =PennantApplicationUtil.formatEIDNumber(fieldValue);
							currentFieldValue=PennantApplicationUtil.formatEIDNumber(currentFieldValue);
						}
					}
					lc = new Listcell(fieldValue);
					for(int k= 0;k<ruleFields.length;k++){
						if(fieldMethod.equals("get"+ruleFields[k])){
							if(StringUtils.equalsIgnoreCase(StringUtils.trimToEmpty(fieldValue), StringUtils.trimToEmpty(currentFieldValue))){
								lc.setStyle("font-weight:bold;color:#F20C0C;");
							}
						}
					}
				} else if (data.getClass().getMethod(fieldMethod).getReturnType().equals(Date.class)) {
					dateFieldValue = (Date) data.getClass().getMethod(fieldMethod).invoke(data);
					Date curdateFieldValue = (Date) getCurCustomer().getClass().getMethod(fieldMethod).invoke(getCurCustomer());
					lc = new Listcell(DateUtility.formatToLongDate(dateFieldValue));
					
					if(dateFieldValue != null && curdateFieldValue != null) {
						for (int k = 0; k< ruleFields.length; k++) {
							if(fieldMethod.equals("get"+ruleFields[k])) {
								if (dateFieldValue.compareTo(curdateFieldValue) == 0) {
									lc.setStyle("font-weight:bold;color:#F20C0C;");
								}
							}
						}
					}
				}else if("boolean".equals(data.getClass().getMethod(fieldMethod).getReturnType().toString())) {
					Checkbox chk = new Checkbox();
					boolean newRule = (Boolean) data.getClass().getMethod("isNewRule").invoke(data);
					boolean newRecord = (Boolean) data.getClass().getMethod("isNewPolicecaseRecord").invoke(data);
					String overrideUser = (String) data.getClass().getMethod("getOverrideUser").invoke(data);
					if (newRule	&& !newRecord && (Boolean) data.getClass().getMethod(fieldMethod).invoke(data)) {
						chk.setDisabled(false);
						chk.setChecked(false);
					} else if (newRule && !newRecord &&  !(Boolean) data.getClass().getMethod(fieldMethod).invoke(data)) {
						chk.setDisabled(true);
						chk.setChecked((Boolean) data.getClass().getMethod(fieldMethod).invoke(data));
					} else if (newRule	&& !(Boolean) data.getClass().getMethod(fieldMethod).invoke(data)) {
						chk.setDisabled(false);
						chk.setChecked((Boolean) data.getClass().getMethod(fieldMethod).invoke(data));
					}else if(overrideUser !=null && overrideUser.contains(curAccessedUser)){
						chk.setDisabled(true);
						chk.setChecked((Boolean)data.getClass().getMethod(fieldMethod).invoke(data));
					}else if(!(Boolean) data.getClass().getMethod(fieldMethod).invoke(data)){
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
		if (StringUtils.isBlank(label)) {
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
		
		public OnPagingEventListener() {
			
		}
		
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
		setListModelList(new ListModelList<PoliceCaseDetail>());
		this.listbox.setModel(getListModelList());
		logger.debug("Leaving");
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

	public ListModelList<PoliceCaseDetail> getListModelList() {
		return listModelList;
	}

	public void setListModelList(ListModelList<PoliceCaseDetail> listModelList) {
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

	public List<PoliceCaseDetail> getDedupListSize() {
		return dedupListSize;
	}

	public void setDedupListSize(List<PoliceCaseDetail> dedupListSize) {
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

	public PoliceCaseDetail getCurCustomer() {
		return curCustomer;
	}

	public void setCurCustomer(PoliceCaseDetail curCustomer) {
		this.curCustomer = curCustomer;
	}




}
