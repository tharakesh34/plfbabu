package com.pennant.webui.dedup.dedupparm;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
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
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Toolbar;
import org.zkoss.zul.Window;
import org.zkoss.zul.event.PagingEvent;

import com.pennant.app.util.CurrencyUtil;
import com.pennant.backend.model.applicationmaster.Currency;
import com.pennant.backend.model.returnedcheques.ReturnedCheques;
import com.pennant.backend.service.PagedListService;
import com.pennant.util.PennantAppUtil;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.feature.model.ModuleMapping;
import com.pennanttech.pennapps.core.util.DateUtil;

/**
 * @author sreeravali.s
 *
 */
@SuppressWarnings("rawtypes")
public class ShowReturnedCheques extends Window implements Serializable {
	private static final long serialVersionUID = -2854517425413800019L;
	private static final Logger logger = LogManager.getLogger(ShowReturnedCheques.class);
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
	private List<ReturnedCheques> dedupListSize = null;
	private Object objClass = null;
	private int userAction = 0;
	private static String curAccessedUser;
	private ReturnedCheques returnedCheques;
	private String[] listHeaders;

	public ShowReturnedCheques() {
		super();
	}

	/**
	 * The Call method.
	 * 
	 * @param parent The parent component
	 * @return a BeanObject from the listBox or null.
	 */
	public static Object show(Component parent, List<ReturnedCheques> returnChequeList, String dedupFields,
			ReturnedCheques returnedCheque) {

		return new ShowReturnedCheques(parent, returnChequeList, dedupFields, returnedCheque);
	}

	public ShowReturnedCheques(Component parent, List<ReturnedCheques> dedupList, String dedupFields,
			ReturnedCheques returnedCheque) {
		super();
		this.dedupListSize = (List<ReturnedCheques>) dedupList;
		int count = 0;
		returnedCheque.setCount(dedupListSize.size());
		Calendar now = Calendar.getInstance();
		now.add(Calendar.MONTH, -12);
		now.getTime();

		String dd = DateUtil.formatToShortDate(now.getTime());

		Date date = DateUtil.getDate(dd);
		for (int i = 0; i < dedupListSize.size(); i++) {
			if (date.before(dedupListSize.get(i).getReturnDate())
					|| date.equals(dedupListSize.get(i).getReturnDate())) {
				count++;
			}

		}

		returnedCheque.setCounting(count);

		this.fieldString = dedupFields.split(",");
		setParent(parent);
		createBox(returnedCheque);
	}

	@SuppressWarnings({ "deprecation", "unchecked" })
	private void createBox(ReturnedCheques returnedCheque) {
		logger.debug("Entering");
		// Window
		int borderLayoutHeight = GFCBaseCtrl.getDesktopHeight();
		int borderLayoutWidth = GFCBaseCtrl.getDesktopWidth();
		this.setWidth((borderLayoutWidth - 100) + "px");
		this.setHeight((borderLayoutHeight - 150) + "px");

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
		btnProceed.setLabel("Override");
		btnProceed.addEventListener("onClick", new OnProceedListener());
		btnProceed.setParent(startToolbar);
		// Button Cancel
		final Button btnCancel = new Button();
		btnCancel.setSclass("z-toolbarbutton");
		btnCancel.setLabel("Cases Found");
		btnCancel.addEventListener("onClick", new OnCancelListener());
		btnCancel.setParent(startToolbar);

		Toolbar centerToolbar = new Toolbar();
		centerToolbar.setAlign("center");
		centerToolbar.setSclass("toolbar-center");
		Label title = new Label("Returned Cheques");
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
		column1.setWidth("30%");
		column2.setWidth("20%");
		column3.setWidth("30%");
		column4.setWidth("20%");

		columns.appendChild(column1);
		columns.appendChild(column2);
		columns.appendChild(column3);
		columns.appendChild(column4);
		grid.appendChild(columns);

		// Rows Preparation
		Rows rows = new Rows();
		rows.setParent(grid);
		grid.appendChild(rows);

		rows.appendChild(prepareRow(new Row(), Labels.getLabel("label_ReturnedCheque_NoOfCheques.value"),
				returnedCheque.getCount(), Labels.getLabel("label_ReturnedCheque_ReturnedCheque.value"),
				returnedCheque.getCounting()));

		int listRows = Math.round((borderLayoutHeight - 300) / 25) - 3;
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
		this.listbox.setHeight((borderLayoutHeight - 300) + "px");
		this.listbox.setVisible(true);
		this.listbox.setSizedByContent(true);
		this.listbox.setSpan("true");
		this.listbox.setParent(divCenter2);

		setListModelList(new ListModelList(dedupListSize));
		this.listbox.setModel(getListModelList());
		this.listbox.setItemRenderer(new ReturnedChequeItemRenderer());

		final Listhead listhead = new Listhead();
		listhead.setParent(this.listbox);
		String headerList = Labels.getLabel("listHeader_ReturnedCheque_label");
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
			logger.error("Exception: ", e);
			this.detach();
		}
		logger.debug("Leaving");
	}

	/**
	 * Inner ListItemRenderer class.<br>
	 */
	final class ReturnedChequeItemRenderer implements ListitemRenderer<Object> {

		public ReturnedChequeItemRenderer() {
		    super();
		}

		@Override
		public void render(Listitem item, Object data, int count)
				throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
			String fieldValue = "";
			Date dateFieldValue = new Date();
			ReturnedCheques returnedCheques = (ReturnedCheques) data;
			for (int j = 0; j < fieldString.length; j++) {
				final Listcell lc;
				String fieldMethod = "get" + fieldString[j].substring(0, 1).toUpperCase() + fieldString[j].substring(1);
				if (data.getClass().getMethod(fieldMethod).getReturnType().equals(String.class)) {
					fieldValue = (String) data.getClass().getMethod(fieldMethod).invoke(data);
					lc = new Listcell(fieldValue);
				} else if (data.getClass().getMethod(fieldMethod).getReturnType().equals(Date.class)) {
					dateFieldValue = (Date) data.getClass().getMethod(fieldMethod).invoke(data);
					lc = new Listcell(DateUtil.formatToLongDate(dateFieldValue));
				} else if (data.getClass().getMethod(fieldMethod).getReturnType().equals(BigDecimal.class)) {
					BigDecimal decfieldValue = (BigDecimal) data.getClass().getMethod(fieldMethod).invoke(data);
					if (StringUtils.isNotEmpty(returnedCheques.getCurrency())) {
						Currency currency = PennantAppUtil.getCurrencyBycode(returnedCheques.getCurrency());
						if (currency != null) {
							lc = new Listcell(CurrencyUtil.format(decfieldValue, currency.getCcyEditField()));
							lc.setStyle("text-align:right;");
						} else {
							lc = new Listcell();
							lc.setStyle("text-align:right;");
						}
					} else {
						return;
					}
				} else {
					fieldValue = data.getClass().getMethod(fieldMethod).invoke(data).toString();
					lc = new Listcell(fieldValue);
				}
				lc.setParent(item);
			}

		}
	}

	private Row prepareRow(Row row, String label1, int value1, String label2, int value2) {

		Label label = new Label();
		label.setValue(label1);
		row.appendChild(label);
		row.setHeight("40px");

		Textbox textbox = new Textbox();
		textbox.setWidth("70px");
		textbox.setReadonly(true);
		textbox.setValue(String.valueOf(value1));
		row.appendChild(textbox);

		label = new Label();
		label.setValue(label2);
		row.appendChild(label);

		textbox = new Textbox();
		textbox.setWidth("70px");
		textbox.setReadonly(true);
		textbox.setValue(String.valueOf(value2));
		row.appendChild(textbox);

		return row;
	}

	/**
	 * Inner OnProceedListener class.<br>
	 */
	final class OnProceedListener implements EventListener<Event> {

		public OnProceedListener() {
		    super();
		}

		@Override
		public void onEvent(Event event) {
			setUserAction(1);
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
		public void onEvent(Event event) {
			setUserAction(0);
			setObject(null);
			onClose();
		}
	}

	public final class OnPagingEventListener implements EventListener<Event> {

		public OnPagingEventListener() {
		    super();
		}

		@Override
		public void onEvent(Event event) {
			final PagingEvent pe = (PagingEvent) event;
			final int pageNo = pe.getActivePage();
			final int start = pageNo * getPageSize();
			final String searchText = ShowReturnedCheques.this._textbox.getValue();
			refreshModel(searchText, start);
		}
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

	private String getLabel(String value) {
		String label = Labels.getLabel(value + "_label");
		if (StringUtils.isBlank(label)) {
			return value;
		}
		return label;
	}

	public List<ReturnedCheques> getDedupListSize() {
		return dedupListSize;
	}

	public void setDedupListSize(List<ReturnedCheques> dedupListSize) {
		this.dedupListSize = dedupListSize;
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

	public ListModelList getListModelList() {
		return listModelList;
	}

	public void setListModelList(ListModelList listModelList) {
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

	public Object getObjClass() {
		return objClass;
	}

	public void setObjClass(Object objClass) {
		this.objClass = objClass;
	}

	public Object getObject() {
		return this.objClass;
	}

	private void setObject(Object objClass) {
		this.objClass = objClass;
	}

	public int getUserAction() {
		return userAction;
	}

	public void setUserAction(int userAction) {
		this.userAction = userAction;
	}

	public static String getCurAccessedUser() {
		return curAccessedUser;
	}

	public static void setCurAccessedUser(String curAccessedUser) {
		ShowReturnedCheques.curAccessedUser = curAccessedUser;
	}

	public ReturnedCheques getReturnedCheques() {
		return returnedCheques;
	}

	public void setReturnedCheques(ReturnedCheques returnedCheques) {
		this.returnedCheques = returnedCheques;
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
