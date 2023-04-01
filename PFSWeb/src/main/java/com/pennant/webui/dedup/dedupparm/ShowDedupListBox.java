package com.pennant.webui.dedup.dedupparm;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
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
import org.zkoss.zul.GroupsModelArray;
import org.zkoss.zul.Hbox;
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

import com.pennant.app.util.CurrencyUtil;
import com.pennant.backend.model.finance.FinanceDedup;
import com.pennant.backend.service.PagedListService;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.feature.model.ModuleMapping;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.web.util.MessageUtil;

@SuppressWarnings("rawtypes")
public class ShowDedupListBox extends Window implements Serializable {
	private static final long serialVersionUID = -2854517425413800019L;
	private static final Logger logger = LogManager.getLogger(ShowDedupListBox.class);

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
	private static String curAccessedUser;
	private FinanceDedup financeDedup;

	public ShowDedupListBox() {
		super();
	}

	/**
	 * The Call method.
	 * 
	 * @param parent The parent component
	 * @return a BeanObject from the listBox or null.
	 */
	public static Object show(Component parent, List<?> dedupList, String dedupFields, FinanceDedup dedup,
			String curUser) {
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
	 * Private Constructor. So it can only be created with the static show() method.<br>
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
		int borderLayoutHeight = GFCBaseCtrl.getDesktopHeight();
		int borderLayoutWidth = GFCBaseCtrl.getDesktopWidth();
		this.setWidth((borderLayoutWidth - 170) + "px");
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

		Label title = new Label(Labels.getLabel("label_FinanceDedupAlert"));
		centerToolbar.appendChild(title);

		Toolbar endToolbar = new Toolbar();
		endToolbar.setAlign("end");
		endToolbar.setSclass("toolbar-end");

		// Button for Help
		final Button btnClose = new Button();
		btnClose.setSclass("z-toolbarbutton");
		btnClose.setLabel("Close");
		btnClose.addEventListener("onClick", new OnCloseListener());
		btnClose.setParent(endToolbar);

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

		setFinanceDedup(dedup);
		// Rows Preparation
		Rows rows = new Rows();
		grid.appendChild(rows);

		rows.appendChild(
				prepareRow(new Row(), Labels.getLabel("label_FinanceDeDupList_CustCIF.value"), dedup.getCustCIF(),
						Labels.getLabel("label_FinanceDeDupList_mobileNumber.value"), dedup.getMobileNumber()));

		if (StringUtils.isNotBlank(dedup.getChassisNumber()) || StringUtils.isNotBlank(dedup.getEngineNumber())) {
			rows.appendChild(prepareRow(new Row(), Labels.getLabel("label_FinanceDeDupList_chassisNumber.value"),
					dedup.getChassisNumber(), Labels.getLabel("label_FinanceDeDupList_engineNumber.value"),
					dedup.getEngineNumber()));
		}

		rows.appendChild(prepareRow(new Row(), Labels.getLabel("label_FinanceDeDupList_startDate.value"),
				DateUtil.formatToLongDate(dedup.getStartDate()),
				Labels.getLabel("label_FinanceDeDupList_financeAmount.value"),
				PennantApplicationUtil.amountFormate(dedup.getFinanceAmount(), CurrencyUtil.getFormat(""))));

		rows.appendChild(prepareRow(new Row(), Labels.getLabel("label_FinanceDeDupList_financeType.value"),
				dedup.getFinanceType(), Labels.getLabel("label_FinanceDeDupList_ProfitAmount.value"),
				PennantApplicationUtil.amountFormate(dedup.getProfitAmount(), CurrencyUtil.getFormat(""))));

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
			logger.error("Exception: ", e);
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
	 * Inner Close class.<br>
	 */
	final class OnCloseListener implements EventListener<Event> {

		public OnCloseListener() {

		}

		@Override
		public void onEvent(Event event) {
			setUserAction(0);
			onClose();

		}
	}

	/**
	 * Inner Cancel class.<br>
	 */
	final class OnCancelListener implements EventListener<Event> {

		public OnCancelListener() {

		}

		@Override
		public void onEvent(Event event) {
			setUserAction(0);
			setObject(null);
			onClose();
		}
	}

	/**
	 * Inner OnProceedListener class.<br>
	 */
	final class OnProceedListener implements EventListener<Event> {

		public OnProceedListener() {

		}

		@Override
		public void onEvent(Event event) {
			setUserAction(1);
			if (!isCustomerDedup) {
				List<FinanceDedup> dedupList = new ArrayList<FinanceDedup>();
				FinanceDedup dedup = null;
				for (int i = 0; i < dedupListSize.size(); i++) {
					dedup = (FinanceDedup) dedupListSize.get(i);
					if (!dedup.isOverride()) {
						setUserAction(-1);
						break;
					}
					if (dedup.getOverrideUser() == null) {
						dedup.setOverrideUser(curAccessedUser);
						dedupList.add(dedup);
					}
					if (!dedup.getOverrideUser().contains(StringUtils.trimToEmpty(curAccessedUser))) {
						dedup.setOverrideUser(dedup.getOverrideUser() + "," + curAccessedUser);
						dedupList.add(dedup);
					}

				}
				if (getUserAction() == -1) {
					StringBuilder massValues = new StringBuilder();
					for (Iterator<String> itr = dedup.getOverridenMap().values().iterator(); itr.hasNext();) {
						massValues.append(",");
						massValues.append(itr.next());
					}
					if (dedup.getOverridenMap().keySet().size() == 1) {
						MessageUtil.showError(massValues.deleteCharAt(0) + " Rule not allowed to overriden.");
					} else {
						MessageUtil.showError(massValues.deleteCharAt(0) + " Rules are not allowed to overriden.");
					}

				} else {
					setObject(dedupList);
					onClose();
				}
			} else {
				setObject(String.valueOf("1"));
			}
		}
	}

	/**
	 * Inner ListItemRenderer class.<br>
	 */
	final class CustDedupBoxItemRenderer implements ListitemRenderer<Object> {

		public CustDedupBoxItemRenderer() {

		}

		@Override
		public void render(Listitem item, Object data, int count)
				throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
			if (item instanceof Listgroup) {
				String fieldValue = "";
				Date dateFieldValue = new Date();
				for (int j = 0; j < fieldString.length; j++) {
					final Listcell lc;
					String fieldMethod = "get" + fieldString[j].substring(0, 1).toUpperCase()
							+ fieldString[j].substring(1);
					if (data.getClass().getMethod(fieldMethod).getReturnType().equals(String.class)) {
						fieldValue = (String) data.getClass().getMethod(fieldMethod).invoke(data);
						lc = new Listcell(fieldValue);
					} else if (data.getClass().getMethod(fieldMethod).getReturnType().equals(Date.class)) {
						dateFieldValue = (Date) data.getClass().getMethod(fieldMethod).invoke(data);
						lc = new Listcell(DateUtil.formatToLongDate(dateFieldValue));
					} else {
						fieldValue = data.getClass().getMethod(fieldMethod).invoke(data).toString();
						lc = new Listcell(fieldValue);
					}
					if (j == fieldString.length - 1 || j == fieldString.length - 2) {
						lc.setLabel("");
					}

					if (compareObject(data, fieldMethod)) {
						lc.setStyle("font-weight:bold;color:red");
					}
					lc.setParent(item);

				}

			} else {
				String fieldValue = "";
				Date dateFieldValue = new Date();
				for (int j = 0; j < fieldString.length; j++) {
					final Listcell lc;
					String fieldMethod = "get" + fieldString[j].substring(0, 1).toUpperCase()
							+ fieldString[j].substring(1);
					if (data.getClass().getMethod(fieldMethod).getReturnType().equals(String.class)) {
						fieldValue = (String) data.getClass().getMethod(fieldMethod).invoke(data);
						lc = new Listcell(fieldValue);
					} else if (data.getClass().getMethod(fieldMethod).getReturnType().equals(Date.class)) {
						dateFieldValue = (Date) data.getClass().getMethod(fieldMethod).invoke(data);
						lc = new Listcell(DateUtil.formatToLongDate(dateFieldValue));
					} else {
						fieldValue = data.getClass().getMethod(fieldMethod).invoke(data).toString();
						lc = new Listcell(fieldValue);
					}
					if (j != fieldString.length - 1 && j != fieldString.length - 2) {
						lc.setLabel("");
					}
					if (compareObject(data, fieldMethod)) {
						lc.setStyle("font-weight:bold;color:red");
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

		public DedupBoxItemRenderer() {

		}

		@Override
		public void render(Listitem item, Object data, int count)
				throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
			String fieldValue = "";
			Date dateFieldValue = new Date();
			FinanceDedup dedup = (FinanceDedup) data;
			String ruleFields[] = StringUtils.trimToEmpty(dedup.getRules()).split(",");
			// String OverddenRules[]=(StringUtils.trimToEmpty(dedup.getDedupList()).split(","));

			String currentFieldValue = "";

			for (int j = 0; j < fieldString.length; j++) {
				final Listcell lc;
				String fieldMethod = "get" + fieldString[j].substring(0, 1).toUpperCase() + fieldString[j].substring(1);
				// for String Data type
				if (data.getClass().getMethod(fieldMethod).getReturnType().equals(String.class)) {

					fieldValue = (String) data.getClass().getMethod(fieldMethod).invoke(data);
					currentFieldValue = (String) getFinanceDedup().getClass().getMethod(fieldMethod)
							.invoke(getFinanceDedup());

					if (StringUtils.trimToEmpty(fieldValue).startsWith(",")) {
						fieldValue = fieldValue.substring(1);
					}
					if (StringUtils.trimToEmpty(fieldValue).endsWith(",")) {
						fieldValue = fieldValue.substring(0, fieldValue.length() - 1);
					}
					// Rule is Overridden or not

					// if Stage is Empty then set Stage as Active.
					if (fieldMethod.equals("get" + Labels.getLabel("label_FinanceDeDupListStageDesc"))) {
						if (fieldValue == null || StringUtils.isEmpty(fieldValue)) {
							fieldValue = Labels.getLabel("label_FinanceDeDupList_ActiveStage");
						}
					}
					if (fieldMethod.equals("get" + Labels.getLabel("label_FinanceDeDupListCustCRCPR"))) {
						if (fieldValue != null && currentFieldValue != null) {
							fieldValue = PennantApplicationUtil.formatEIDNumber(fieldValue);
							currentFieldValue = PennantApplicationUtil.formatEIDNumber(currentFieldValue);
						}
					}
					lc = new Listcell(fieldValue);
					if (fieldValue != null) {
						if (fieldValue.equals(Labels.getLabel("label_FinanceDeDupList_ActiveStage"))) {
							lc.setStyle("font-weight:bold;color:green;");
						}
					}
					// Matches fields show with color
					for (int k = 0; k < ruleFields.length; k++) {
						if (fieldMethod.equalsIgnoreCase("get" + ruleFields[k])) {
							if (StringUtils.equalsIgnoreCase(fieldValue, StringUtils.trimToEmpty(currentFieldValue))) {
								lc.setStyle("font-weight:bold;color:red");
							}
						}
					}

					// For Date Data type
				} else if (data.getClass().getMethod(fieldMethod).getReturnType().equals(Date.class)) {
					dateFieldValue = (Date) data.getClass().getMethod(fieldMethod).invoke(data);
					lc = new Listcell(DateUtil.formatToLongDate(dateFieldValue));
					Date curdateFieldValue = (Date) getFinanceDedup().getClass().getMethod(fieldMethod)
							.invoke(getFinanceDedup());

					if (dateFieldValue != null && curdateFieldValue != null) {
						for (int k = 0; k < ruleFields.length; k++) {
							if (fieldMethod.equalsIgnoreCase("get" + ruleFields[k])) {
								if (dateFieldValue.compareTo(curdateFieldValue) == 0) {
									lc.setStyle("font-weight:bold;color:#F20C0C;");
								}
							}
						}
					}
					// For Decimal values
				} else if (data.getClass().getMethod(fieldMethod).getReturnType().equals(BigDecimal.class)) {

					BigDecimal decfieldValue = (BigDecimal) data.getClass().getMethod(fieldMethod).invoke(data);
					lc = new Listcell(CurrencyUtil.format(decfieldValue, CurrencyUtil.getFormat("")));
					lc.setStyle("text-align:right;");
					BigDecimal currBigdeValue = (BigDecimal) getFinanceDedup().getClass().getMethod(fieldMethod)
							.invoke(getFinanceDedup());

					for (int k = 0; k < ruleFields.length; k++) {
						if (fieldMethod.equalsIgnoreCase("get" + ruleFields[k])) {
							if (PennantApplicationUtil.matches(decfieldValue, currBigdeValue)) {
								lc.setStyle("font-weight:bold;color:red");
							}
						}
					}

				} else {
					fieldValue = data.getClass().getMethod(fieldMethod).invoke(data).toString();
					lc = new Listcell(fieldValue);
				}
				lc.setParent(item);
			}

		}

	}

	public final class OnPagingEventListener implements EventListener<Event> {

		public OnPagingEventListener() {

		}

		@Override
		public void onEvent(Event event) {
			final PagingEvent pe = (PagingEvent) event;
			final int pageNo = pe.getActivePage();
			final int start = pageNo * getPageSize();
			final String searchText = ShowDedupListBox.this._textbox.getValue();
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
		if (StringUtils.isBlank(label)) {
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
							String value1 = (String) object.getClass().getMethod("get" + compListFileds[i])
									.invoke(object);
							String value2 = (String) object1.getClass().getMethod("get" + compListFileds[i])
									.invoke(object1);
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

		public CompareCustomer() {

		}

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
	 * 
	 * @param row
	 * @param label1
	 * @param value1
	 * @param label2
	 * @param value2
	 * @return
	 */
	private Row prepareRow(Row row, String label1, String value1, String label2, String value2) {

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
		if (Labels.getLabel("label_FinanceDeDupList_financeAmount.value").equals(label2)
				|| Labels.getLabel("label_FinanceDeDupList_ProfitAmount.value").equals(label2)) {
			textbox.setStyle("text-align:right;");
		}
		textbox.setWidth("150px");
		textbox.setReadonly(true);
		textbox.setValue(value2);
		row.appendChild(textbox);
		return row;
	}

	public FinanceDedup getFinanceDedup() {
		return financeDedup;
	}

	public void setFinanceDedup(FinanceDedup financeDedup) {
		this.financeDedup = financeDedup;
	}

}
