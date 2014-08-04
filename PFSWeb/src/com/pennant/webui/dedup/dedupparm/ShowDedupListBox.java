package com.pennant.webui.dedup.dedupparm;

import java.io.Serializable;
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
import org.zkoss.zul.Div;
import org.zkoss.zul.GroupsModelArray;
import org.zkoss.zul.Intbox;
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
import org.zkoss.zul.South;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;
import org.zkoss.zul.event.PagingEvent;

import com.pennant.backend.model.ModuleMapping;
import com.pennant.backend.service.PagedListService;
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
	public static Object show(Component parent, List<?> dedupList, String dedupFields) {
		isCustomerDedup = false;
		return new ShowDedupListBox(parent, dedupList, dedupFields);
	}

	@SuppressWarnings("unchecked")
	public static Object show(Component parent, List<?> dedupList, String dedupFields, Object compareObject, List<?> listCompare, String[] listCompareFileds) {
		isCustomerDedup = true;
		compObject = compareObject;
		complist = (List<Object>) listCompare;
		compListFileds = listCompareFileds;
		return new ShowDedupListBox(parent, dedupList, dedupFields);
	}

	/**
	 * Private Constructor. So it can only be created with the static show()
	 * method.<br>
	 * 
	 * @param parent
	 */
	private ShowDedupListBox(Component parent, List<?> listCode, String dedupFields) {
		super();
		this.dedupListSize = (List<?>) listCode;
		this.fieldString = dedupFields.split(",");
		setParent(parent);
		createBox();
	}

	@SuppressWarnings({ "deprecation", "unchecked" })
	private void createBox() {
		logger.debug("Entering");
		// Window
		int borderLayoutHeight = ((Intbox) Path.getComponent("/outerIndexWindow/currentDesktopHeight")).getValue().intValue() - PennantConstants.borderlayoutMainNorth;
		int borderLayoutWidth = ((Intbox) Path.getComponent("/outerIndexWindow/currentDesktopWidth")).getValue().intValue();
		this.setWidth(((borderLayoutWidth * 3) / 5) + "px");
		this.setHeight((borderLayoutHeight / 2) + 150 + "px");
		int listRows = Math.round(borderLayoutHeight / 24) - 1;
		setPageSize(listRows);
		this.setVisible(true);
		this.setClosable(true);
		this.setTitle("DeDup Details List");
		// BorderLayout
		final Borderlayout bl = new Borderlayout();
		bl.setHeight("100%");
		bl.setWidth("100%");
		bl.setParent(this);
		final Center center = new Center();
		center.setBorder("none");
		center.setFlex(true);
		center.setParent(bl);
		// BorderLayout
		final Borderlayout bl2 = new Borderlayout();
		bl2.setHeight("100%");
		bl2.setWidth("100%");
		bl2.setParent(center);
		final North north2 = new North();
		north2.setBorder("none");
		north2.setHeight("35px");
		north2.setParent(bl2);
		// Paging
		this._paging = new Paging();
		this._paging.setDetailed(true);
		this._paging.addEventListener("onPaging", new OnPagingEventListener());
		this._paging.setPageSize(getPageSize());
		this._paging.setParent(north2);
		final Center center2 = new Center();
		center2.setBorder("none");
		center2.setFlex(true);
		center2.setParent(bl2);
		// DIV Center area
		final Div divCenter2 = new Div();
		divCenter2.setWidth("100%");
		divCenter2.setHeight("100%");
		divCenter2.setParent(center2);
		// ListBox
		this.listbox = new Listbox();
		listbox.setStyle("border: none;");
		this.listbox.setHeight(((borderLayoutHeight / 2)+40) + "px");
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
		final South south = new South();
		south.setBorder("none");
		south.setHeight("30px");
		south.setParent(bl);
		final Div divSouth = new Div();
		divSouth.setWidth("100%");
		divSouth.setHeight("100%");
		divSouth.setParent(south);
		// Button Proceed
		final Button btnProceed = new Button();
		btnProceed.setStyle("padding: 2px;font-weight:bold;");
		btnProceed.setLabel("Proceed");
		btnProceed.addEventListener("onClick", new OnProceedListener());
		btnProceed.setParent(divSouth);
		// Button Cancel
		final Button btnCancel = new Button();
		btnCancel.setStyle("padding: 2px;font-weight:bold;");
		btnCancel.setLabel("Cancel");
		btnCancel.addEventListener("onClick", new OnCancelListener());
		btnCancel.setParent(divSouth);
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
		}// Upgraded to ZK-6.5.1.1 Removed catch block for interrupted exception
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
			setObject(String.valueOf("0"));
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
			setObject(String.valueOf("1"));
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
					if (data.getClass().getMethod(fieldMethod, null).getReturnType().equals(String.class)) {
						fieldValue = (String) data.getClass().getMethod(fieldMethod, null).invoke(data, null);
						lc = new Listcell(fieldValue);
					} else if (data.getClass().getMethod(fieldMethod, null).getReturnType().equals(Date.class)) {
						dateFieldValue = (Date) data.getClass().getMethod(fieldMethod, null).invoke(data, null);
						lc = new Listcell(PennantAppUtil.formateDate(dateFieldValue, PennantConstants.dateFormat));
					} else {
						fieldValue = data.getClass().getMethod(fieldMethod, null).invoke(data, null).toString();
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
					if (data.getClass().getMethod(fieldMethod, null).getReturnType().equals(String.class)) {
						fieldValue = (String) data.getClass().getMethod(fieldMethod, null).invoke(data, null);
						lc = new Listcell(fieldValue);
					} else if (data.getClass().getMethod(fieldMethod, null).getReturnType().equals(Date.class)) {
						dateFieldValue = (Date) data.getClass().getMethod(fieldMethod, null).invoke(data, null);
						lc = new Listcell(PennantAppUtil.formateDate(dateFieldValue, PennantConstants.dateFormat));
					} else {
						fieldValue = data.getClass().getMethod(fieldMethod, null).invoke(data, null).toString();
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
				if (data.getClass().getMethod(fieldMethod, null).getReturnType().equals(String.class)) {
					fieldValue = (String) data.getClass().getMethod(fieldMethod, null).invoke(data, null);
					lc = new Listcell(fieldValue);
				} else if (data.getClass().getMethod(fieldMethod, null).getReturnType().equals(Date.class)) {
					dateFieldValue = (Date) data.getClass().getMethod(fieldMethod, null).invoke(data, null);
					lc = new Listcell(PennantAppUtil.formateDate(dateFieldValue, PennantConstants.dateFormat));
				} else {
					fieldValue = data.getClass().getMethod(fieldMethod, null).invoke(data, null).toString();
					lc = new Listcell(fieldValue);
				}
				lc.setParent(item);
			}
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
								String value1 = (String) object.getClass().getMethod("get" + compListFileds[i], null).invoke(object, null);
								String value2 = (String) object1.getClass().getMethod("get" + compListFileds[i], null).invoke(object1, null);
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
				if (compObject.getClass().getMethod(filedMethod, null).getReturnType().equals(String.class)) {
					String fieldValue1 = (String) object1.getClass().getMethod(filedMethod, null).invoke(object1, null);
					String fieldValue2 = (String) compObject.getClass().getMethod(filedMethod, null).invoke(compObject, null);
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
				String fieldValue1 = (String) o1.getClass().getMethod("get" + fieldString[0], null).invoke(o1, null);
				String fieldValue2 = (String) o2.getClass().getMethod("get" + fieldString[0], null).invoke(o2, null);
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
//	Grouping one type 1
//	final class CustDedupBoxItemRenderer implements ListitemRenderer<Object> {
//		@Override
//		public void render(Listitem item, Object data, int count) throws Exception {
//			if (item instanceof Listgroup) {
//				final Listcell lc;
//				String fieldValue = (String) data.getClass().getMethod("get" + fieldString[0], null).invoke(data, null);
//				lc = new Listcell(fieldValue);
//				lc.setSpan(fieldString.length);
//				lc.setParent(item);
//			} else {
//				String fieldValue = "";
//				Date dateFieldValue = new Date();
//				for (int j = 0; j < fieldString.length; j++) {
//					final Listcell lc;
//					String fieldMethod = "get" + fieldString[j].substring(0, 1).toUpperCase() + fieldString[j].substring(1);
//					if (data.getClass().getMethod(fieldMethod, null).getReturnType().equals(String.class)) {
//						fieldValue = (String) data.getClass().getMethod(fieldMethod, null).invoke(data, null);
//						lc = new Listcell(fieldValue);
//					} else if (data.getClass().getMethod(fieldMethod, null).getReturnType().equals(Date.class)) {
//						dateFieldValue = (Date) data.getClass().getMethod(fieldMethod, null).invoke(data, null);
//						lc = new Listcell(PennantAppUtil.formateDate(dateFieldValue, PennantConstants.dateFormat));
//					} else {
//						fieldValue = data.getClass().getMethod(fieldMethod, null).invoke(data, null).toString();
//						lc = new Listcell(fieldValue);
//					}
//					if (j == 0) {
//						lc.setLabel("");
//					}
//					if (compareObject(data, fieldMethod)) {
//						lc.setStyle("color:red");
//					}
//					lc.setParent(item);
//				}
//			}
//		}
//	}
	
	
	
}
