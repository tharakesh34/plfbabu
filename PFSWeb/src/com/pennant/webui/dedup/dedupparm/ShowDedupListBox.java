package com.pennant.webui.dedup.dedupparm;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
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
import org.zkoss.zul.Column;
import org.zkoss.zul.Columns;
import org.zkoss.zul.Div;
import org.zkoss.zul.Grid;
import org.zkoss.zul.GroupsModelArray;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listgroup;
import org.zkoss.zul.Listhead;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.North;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Row;
import org.zkoss.zul.Rows;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Toolbar;
import org.zkoss.zul.Window;
import org.zkoss.zul.event.PagingEvent;

import com.pennant.app.util.DateUtility;
import com.pennant.backend.model.ModuleMapping;
import com.pennant.backend.model.finance.FinanceDedup;
import com.pennant.backend.service.PagedListService;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.util.PennantAppUtil;

@SuppressWarnings("rawtypes")
public class ShowDedupListBox extends Window implements Serializable {
	private static final long serialVersionUID = -2854517425413800019L;
	private static final Logger logger = Logger.getLogger(ShowDedupListBox.class);
	private Textbox _textbox;
	private Paging _paging;
	private int pageSize = 10;
	private Listbox listbox;
	private ListModelList listModelList;
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
	private static long curAccessedUser;

	public ShowDedupListBox() {
		super();
	}

	/**
	 * The Call method.
	 * 
	 * @param parent
	 *            The parent component
	 * @return a BeanObject from the listBox or null.
	 */
	public static Object show(Component parent, List<?> dedupList, String dedupFields, FinanceDedup dedup, long curUser) {
		isCustomerDedup = false;
		curAccessedUser = curUser;
		return new ShowDedupListBox(parent, dedupList, dedupFields, dedup);
	}

	@SuppressWarnings("unchecked")
	public static Object show(Component parent, List<?> dedupList, String dedupFields, Object compareObject, 
			List<?> listCompare, String[] listCompareFileds, FinanceDedup dedup) {
		isCustomerDedup = true;
		compObject = compareObject;
		complist = (List<Object>) listCompare;
		compListFileds = listCompareFileds;
		return new ShowDedupListBox(parent, dedupList, dedupFields, dedup);
	}

	/**
	 * Private Constructor. So it can only be created with the static show()
	 * method.<br>
	 * 
	 * @param parent
	 */
	private ShowDedupListBox(Component parent, List<?> listCode, String dedupFields, FinanceDedup dedup) {
		super();
		this.dedupListSize = (List<?>) listCode;
		this.fieldString = dedupFields.split(",");
		setParent(parent);
		createBox(dedup);
	}

	@SuppressWarnings({ "deprecation", "unchecked" })
	private void createBox(FinanceDedup dedup) {
		logger.debug("Entering");
		// Window
		int borderLayoutHeight = ((Intbox) Path.getComponent("/outerIndexWindow/currentDesktopHeight")).getValue().intValue() - PennantConstants.borderlayoutMainNorth;
		int borderLayoutWidth = ((Intbox) Path.getComponent("/outerIndexWindow/currentDesktopWidth")).getValue().intValue();
		this.setWidth((borderLayoutWidth -100) + "px");
		this.setHeight((borderLayoutHeight -150) + "px");
		
		Div toolbardiv = new Div();
		toolbardiv.setSclass("z-toolbar");
		toolbardiv.setStyle("padding:0px;height:28px;");
		Hbox hbox = new Hbox();
		hbox.setPack("stretch");
		hbox.setSclass("hboxRemoveWhiteStrips");
		hbox.setWidth("100%");
		hbox.setWidths("30%,40%,30%");
		hbox.setParent(toolbardiv);
		
		Toolbar startToolbar = new Toolbar();
		startToolbar.setAlign("start");
		startToolbar.setSclass("toolbar-start");
		
		// Button Proceed
		final Button btnProceed = new Button();
		btnProceed.setSclass("z-toolbarbutton");
		btnProceed.setLabel("NOT Duplicate");
		btnProceed.addEventListener("onClick", new OnProceedListener());
		btnProceed.setParent(startToolbar);
		// Button Cancel
		final Button btnCancel = new Button();
		btnCancel.setSclass("z-toolbarbutton");
		btnCancel.setLabel("Duplicate Record");
		btnCancel.addEventListener("onClick", new OnCancelListener());
		btnCancel.setParent(startToolbar);
		
		Toolbar centerToolbar = new Toolbar();
		centerToolbar.setAlign("center");
		centerToolbar.setSclass("toolbar-center");
		
		Label title = new Label("Finance Dedupe Alert");
		centerToolbar.appendChild(title);
		
		Toolbar endToolbar = new Toolbar();
		endToolbar.setAlign("end");
		endToolbar.setSclass("toolbar-end");
		
		 // Button for Help
		final Button btnHelp = new Button();
		btnHelp.setSclass("z-toolbarbutton");
		btnHelp.setLabel("Help");
		btnHelp.setParent(endToolbar);
		
		hbox.appendChild(startToolbar);
		hbox.appendChild(centerToolbar);
		hbox.appendChild(endToolbar);
		toolbardiv.appendChild(hbox);		
		
		// Grid Details for Checking Customer Details
		Grid grid = new Grid();
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
		
		//Rows Preparation
		Rows rows = new Rows();
		grid.appendChild(rows);
		rows.appendChild(prepareRow(new Row(), Labels.getLabel("label_FinanceDeDupList_CustCIF.value"), 
				dedup.getCustCIF(), Labels.getLabel("label_FinanceDeDupList_mobileNumber.value"), 
				dedup.getMobileNumber()));
		rows.appendChild(prepareRow(new Row(), Labels.getLabel("label_FinanceDeDupList_chassisNumber.value"), 
				dedup.getChassisNumber(), Labels.getLabel("label_FinanceDeDupList_engineNumber.value"), dedup.getEngineNumber()));
		rows.appendChild(prepareRow(new Row(), Labels.getLabel("label_FinanceDeDupList_startDate.value"), 
				DateUtility.formateDate(dedup.getStartDate(), PennantConstants.dateFormate),
				Labels.getLabel("label_FinanceDeDupList_financeAmount.value"), PennantApplicationUtil.amountFormate(dedup.getFinanceAmount(), dedup.getFormatter())));
		rows.appendChild(prepareRow(new Row(), Labels.getLabel("label_FinanceDeDupList_financeType.value"), 
				dedup.getFinanceType(), Labels.getLabel("label_FinanceDeDupList_ProfitAmount.value"), PennantApplicationUtil.amountFormate(dedup.getProfitAmount(), dedup.getFormatter())));

		int listRows = Math.round((borderLayoutHeight-300) / 25) - 2;
		setPageSize(listRows);
		this.setVisible(true);
		this.setClosable(true);
		
		// BorderLayout
		final Borderlayout bl = new Borderlayout();
		bl.setHeight("100%");
		bl.setWidth("100%");
		bl.setParent(this);
		final Center center = new Center();
		center.setBorder("none");
		center.setFlex(true);
		center.setParent(bl);
		
		final North north = new North();
		north.setBorder("none");
		north.setFlex(true);
		north.setParent(bl);
		toolbardiv.setParent(north);
		
		// DIV Center area
		final Div divCenter2 = new Div();
		divCenter2.setWidth("100%");
		divCenter2.setHeight("100%");
		divCenter2.setParent(center);
		grid.setParent(divCenter2);
		
		// ListBox
		this.listbox = new Listbox();
		listbox.setStyle("border: none;");
		this.listbox.setHeight((borderLayoutHeight -300) + "px");
		this.listbox.setVisible(true);
		this.listbox.setSizedByContent(true);
		this.listbox.setSpan("true");
		this.listbox.setParent(divCenter2);
		this.listbox.setMold("paging");
		this.listbox.setPageSize(getPageSize());
		
		if (isCustomerDedup) {
			this.listbox.setItemRenderer(new CustDedupBoxItemRenderer());
		} else {
			this.listbox.setItemRenderer(new DedupBoxItemRenderer());
		}
		final Listhead listhead = new Listhead();
		listhead.setParent(this.listbox);
		for (int i = 0; i < this.fieldString.length; i++) {
			final Listheader listheader = new Listheader();
			listheader.setLabel(getLabel(this.fieldString[i]));
			listheader.setHflex("min");
			listheader.setParent(listhead);
		}

		setListModelList(new ListModelList(dedupListSize));
		if (isCustomerDedup) {
			this.listbox.setModel(new GroupsModelArray(dedupListSize.toArray(), new CompareCustomer()));
		} else {
			this.listbox.setModel(getListModelList());
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

	void refreshModel(String searchText, int start) {
		logger.debug("Entering");
		// clear old data
		getListModelList().clear();
		this._paging.setTotalSize(dedupListSize.size());
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
			if(!isCustomerDedup){
				List<FinanceDedup> dedupList = new ArrayList<FinanceDedup>();
				for (int i = 0; i < listbox.getItems().size(); i++) {
					Listitem listitem = listbox.getItems().get(i);
					FinanceDedup dedup = (FinanceDedup) listitem.getAttribute("data");
					if(dedup.getOverrideUser() == 0){
						dedup.setOverrideUser(curAccessedUser);
						dedupList.add(dedup);
					}
				}
				setObject(dedupList);
			}else{
				setObject(String.valueOf("1"));
			}
			onClose();
		}
	}

	/**
	 * Inner ListItemRenderer class.<br>
	 */
	final class CustDedupBoxItemRenderer implements ListitemRenderer<Object> {
		@Override
		public void render(Listitem item, Object data, int count) throws Exception {
			if (item instanceof Listgroup) {
				String fieldValue = "";
				Date dateFieldValue = new Date();
				for (int j = 0; j < fieldString.length; j++) {
					final Listcell lc;
					String fieldMethod = "get" + fieldString[j].substring(0, 1).toUpperCase() + fieldString[j].substring(1);
					if (data.getClass().getMethod(fieldMethod).getReturnType().equals(String.class)) {
						fieldValue = (String) data.getClass().getMethod(fieldMethod).invoke(data);
						lc = new Listcell(fieldValue);
					} else if (data.getClass().getMethod(fieldMethod).getReturnType().equals(Date.class)) {
						dateFieldValue = (Date) data.getClass().getMethod(fieldMethod).invoke(data);
						lc = new Listcell(PennantAppUtil.formateDate(dateFieldValue, PennantConstants.dateFormat));
					} else {
						fieldValue = data.getClass().getMethod(fieldMethod).invoke(data).toString();
						lc = new Listcell(fieldValue);
					}
					if (j == fieldString.length-1 || j == fieldString.length-2) {
						lc.setLabel("");
					}
					
					
					if (compareObject(data, fieldMethod)) {
						lc.setStyle("color:red");
					}
					lc.setParent(item);
					
				}
			
			} else {
				String fieldValue = "";
				Date dateFieldValue = new Date();
				for (int j = 0; j < fieldString.length; j++) {
					final Listcell lc;
					String fieldMethod = "get" + fieldString[j].substring(0, 1).toUpperCase() + fieldString[j].substring(1);
					if (data.getClass().getMethod(fieldMethod).getReturnType().equals(String.class)) {
						fieldValue = (String) data.getClass().getMethod(fieldMethod).invoke(data);
						lc = new Listcell(fieldValue);
					} else if (data.getClass().getMethod(fieldMethod).getReturnType().equals(Date.class)) {
						dateFieldValue = (Date) data.getClass().getMethod(fieldMethod).invoke(data);
						lc = new Listcell(PennantAppUtil.formateDate(dateFieldValue, PennantConstants.dateFormat));
					} else {
						fieldValue = data.getClass().getMethod(fieldMethod).invoke(data).toString();
						lc = new Listcell(fieldValue);
					}
					if (j != fieldString.length-1 && j != fieldString.length-2) {
						lc.setLabel("");
					}
					if (compareObject(data, fieldMethod)) {
						lc.setStyle("color:red");
					}
					lc.setParent(item);
				}
			}
		}
	}

	/**
	 * Inner ListItemRenderer class.<br>
	 */
	final class DedupBoxItemRenderer implements ListitemRenderer<Object> {
		@Override
		public void render(Listitem item, Object data, int count) throws Exception {
			String fieldValue = "";
			Date dateFieldValue = new Date();
			for (int j = 0; j < fieldString.length; j++) {
				final Listcell lc;
				String fieldMethod = "get" + fieldString[j].substring(0, 1).toUpperCase() + fieldString[j].substring(1);
				if (data.getClass().getMethod(fieldMethod).getReturnType().equals(String.class)) {
					fieldValue = (String) data.getClass().getMethod(fieldMethod).invoke(data);
					lc = new Listcell(fieldValue);
				} else if (data.getClass().getMethod(fieldMethod).getReturnType().equals(Date.class)) {
					dateFieldValue = (Date) data.getClass().getMethod(fieldMethod).invoke(data);
					lc = new Listcell(PennantAppUtil.formateDate(dateFieldValue, PennantConstants.dateFormat));
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
			final String searchText = ShowDedupListBox.this._textbox.getValue();
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
		return dedupListSize;
	}

	public void setDedupListSize(List<?> dedupListSize) {
		this.dedupListSize = dedupListSize;
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

	private boolean compareObject(Object object1, String filedMethod) {
		try {
			boolean checkList = false;
			if (compListFileds != null && compListFileds.length > 0) {
				for (int i = 0; i < compListFileds.length; i++) {
					if (filedMethod.equals("get" + compListFileds[i])) {
						checkList = true;
						break;
					}
				}
			}
			if (checkList) {
				if (complist != null && complist.size() > 0 && compListFileds != null && compListFileds.length > 0) {
					for (Object object : complist) {
						boolean equal = false;
						for (int i = 0; i < compListFileds.length; i++) {
								String value1 = (String) object.getClass().getMethod("get" + compListFileds[i]).invoke(object);
								String value2 = (String) object1.getClass().getMethod("get" + compListFileds[i]).invoke(object1);
								if (StringUtils.trimToEmpty(value1).equals(StringUtils.trimToEmpty(value2))) {
									equal = true;
								} else {
									equal = false;
								}
						}
						if (equal) {
							return equal;
						}
					}
				}
			} else {
				if (compObject.getClass().getMethod(filedMethod).getReturnType().equals(String.class)) {
					String fieldValue1 = (String) object1.getClass().getMethod(filedMethod).invoke(object1);
					String fieldValue2 = (String) compObject.getClass().getMethod(filedMethod).invoke(compObject);
					if (StringUtils.trimToEmpty(fieldValue1).equals(StringUtils.trimToEmpty(fieldValue2))) {
						return true;
					}
				}
			}
		} catch (Exception e) {
			logger.debug(e);
		}
		return false;
	}

	class CompareCustomer implements Comparator {
		@Override
		public int compare(Object o1, Object o2) {
			try {
				String fieldValue1 = (String) o1.getClass().getMethod("get" + fieldString[0]).invoke(o1);
				String fieldValue2 = (String) o2.getClass().getMethod("get" + fieldString[0]).invoke(o2);
				if (StringUtils.trimToEmpty(fieldValue1).equals(StringUtils.trimToEmpty(fieldValue2))) {
					return 0;
				} else {
					return 1;
				}
			} catch (Exception e) {
				logger.debug(e);
			}
			return 1;
		}
	}

	/**
	 * Method for Preparation of Row Item
	 * @param row
	 * @param label1
	 * @param value1
	 * @param label2
	 * @param value2
	 * @return
	 */
	private Row prepareRow(Row row, String label1, String value1, String label2, String value2){
		
		Label label = new Label();
		label.setValue(label1);
		row.appendChild(label);
		
		Textbox textbox = new Textbox();
		textbox.setWidth("150px");
		textbox.setReadonly(true);
		textbox.setValue(value1);
		row.appendChild(textbox);
		
		label = new Label();
		label.setValue(label2);
		row.appendChild(label);
		
		textbox = new Textbox();
		textbox.setWidth("150px");
		textbox.setReadonly(true);
		textbox.setValue(value2);
		row.appendChild(textbox);
		
		return row;
	}
	
}
