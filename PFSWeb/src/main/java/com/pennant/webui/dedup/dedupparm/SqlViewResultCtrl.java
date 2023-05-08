package com.pennant.webui.dedup.dedupparm;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.util.LinkedCaseInsensitiveMap;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Path;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listhead;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Row;
import org.zkoss.zul.Rows;
import org.zkoss.zul.South;
import org.zkoss.zul.Space;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;
import org.zkoss.zul.event.PagingEvent;

import com.pennant.backend.model.applicationmaster.Query;
import com.pennant.backend.model.customermasters.CustomerDedup;
import com.pennant.backend.service.dedup.DedupParmService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.webui.util.GFCBaseCtrl;

public class SqlViewResultCtrl extends GFCBaseCtrl<Query> {

	private static final long serialVersionUID = -446102445582419907L;
	private static final Logger logger = LogManager.getLogger(SqlViewResultCtrl.class);

	protected Window window_SqlViewResult;
	protected Button button_SqlViewResult;
	protected Button btnClose;
	protected Button btnSimulation;
	protected Borderlayout borderLayout_SqlViewResult;
	protected Paging pagingSqlViewResult;
	protected South paging_South;
	protected Listbox listBoxSqlView;
	protected Label resultCount;
	protected Rows rows_Fields;
	protected Grid grid_Fields;
	protected Groupbox gb_resultCount;
	protected Groupbox gb_fields;

	// row count for listBox
	private int countRows;
	boolean isExecuted = true;
	int noOfAttempts = 1;
	String resultQuery = null;
	Datebox datebox;
	Textbox textbox;

	private transient DedupParmService dedupParmService;
	@SuppressWarnings("rawtypes")
	List resultList = new ArrayList();
	@SuppressWarnings("rawtypes")
	List keyList;
	@SuppressWarnings("rawtypes")
	List valueList;
	LinkedHashMap<String, String[]> fieldMap = new LinkedHashMap<String, String[]>();
	List<String> fields = new ArrayList<String>();

	public SqlViewResultCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "";
	}

	@SuppressWarnings("unchecked")
	public void onCreate$window_SqlViewResult(Event event) {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_SqlViewResult);

		if (arguments.containsKey("resultQuery")) {
			resultQuery = (String) arguments.get("resultQuery");
		} else {
			resultQuery = null;
		}

		if (arguments.containsKey("fields")) {
			fieldMap = (LinkedHashMap<String, String[]>) arguments.get("fields");
		}

		if (fieldMap.containsKey(PennantConstants.CUST_DEDUP_LISTFILED1)) {
			String[] array = new String[2];
			array[0] = "nvarchar";
			array[1] = "Customer Document Type";
			fieldMap.remove(PennantConstants.CUST_DEDUP_LISTFILED1);
			fieldMap.put(PennantConstants.CUST_DEDUP_LISTFILED2, array);
			array = new String[2];
			array[0] = "nvarchar";
			array[1] = "Customer Document Title";
			fieldMap.put(PennantConstants.CUST_DEDUP_LISTFILED3, array);
		}

		Label label;
		Row row;
		Space space;
		Iterator<?> it = fieldMap.entrySet().iterator();
		String[] array;
		while (it.hasNext()) {
			@SuppressWarnings("rawtypes")
			Map.Entry pairs = (Map.Entry) it.next();
			row = new Row();
			array = (String[]) pairs.getValue();
			label = new Label(array[1]);
			row.appendChild(label);
			fields.add(pairs.getKey().toString());
			space = new Space();
			space.setStyle("background-color:red;width:2px;");
			row.appendChild(space);

			if ("datetime".equalsIgnoreCase(array[0])) {
				datebox = new Datebox();
				datebox.setWidth("160px");
				datebox.setFormat(PennantConstants.DBDateFormat);
				datebox.setId(pairs.getKey().toString());
				datebox.setConstraint("NO EMPTY :" + Labels.getLabel("FIELD_NO_EMPTY", new String[] { array[1] }));
				row.appendChild(datebox);
			} else {
				textbox = new Textbox();
				textbox.setWidth("160px");
				textbox.setMaxlength(15);
				textbox.setId(pairs.getKey().toString());
				textbox.setConstraint("NO EMPTY :" + Labels.getLabel("FIELD_NO_EMPTY", new String[] { array[1] }));
				row.appendChild(textbox);
			}
			row.setParent(rows_Fields);
		}

		int height = ((Intbox) Path.getComponent("/outerIndexWindow/currentDesktopHeight")).getValue().intValue();
		final int gridHeight = grid_Fields.getRows().getVisibleItemCount() * 20 + 175;
		final int maxListBoxHeight = height - 25;
		this.borderLayout_SqlViewResult.setHeight(String.valueOf(maxListBoxHeight) + "px");
		this.listBoxSqlView.setHeight(maxListBoxHeight - gridHeight + "px");
		this.listBoxSqlView.setSizedByContent(true);
		this.listBoxSqlView.setSpan(true);
		setCountRows(Math.round((maxListBoxHeight - gridHeight) / 22) - 1);

		if (arguments.containsKey("IRFilter")) {
			btnSimulation.setVisible(false);
			gb_fields.setVisible(false);
			this.listBoxSqlView.setHeight(maxListBoxHeight - gridHeight + "px");
			doSimulate();
		}

		this.window_SqlViewResult.doModal();

		logger.debug("Leaving" + event.toString());
	}

	/**
	 * separating List of values
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void separatingList() {
		logger.debug("Entering");
		keyList = new ArrayList();
		valueList = new ArrayList();
		int rowCount = 0;
		for (int i = 0; i < resultList.size(); i++) {
			LinkedCaseInsensitiveMap map = new LinkedCaseInsensitiveMap();
			map = (LinkedCaseInsensitiveMap) resultList.get(i);
			if (rowCount == 0) {
				Set set = (Set) map.keySet();
				keyList.addAll(set);
			}
			Collection newList = (Collection) map.values();
			valueList.addAll(newList);
			rowCount++;
		}
		Listhead listHead = new Listhead();
		Listheader listheader;
		int columnCount = keyList.size();
		if (isExecuted) {
			for (int k = 0; k < columnCount; k++) {
				listheader = new Listheader();
				listheader.setLabel(getLabel(keyList.get(k).toString()));
				listheader.setHflex("min");
				listHead.appendChild(listheader);
				listHead.setSizable(true);
				listBoxSqlView.appendChild(listHead);
				isExecuted = false;
			}
		}

		renderlist(0, 1);
		logger.debug("Leaving");
	}

	public void onClick$btnSimulation(Event event) {
		logger.debug("Entering" + event.toString());
		doSimulate();
		logger.debug("Leaving" + event.toString());
	}

	private void doSimulate() {
		Object object = new CustomerDedup();
		listBoxSqlView.getItems().clear();
		String fieldType = "";

		List<WrongValueException> wve = new ArrayList<WrongValueException>();

		for (int i = 0; i < fields.size(); i++) {
			try {
				if (fieldMap.containsKey(fields.get(i))) {
					fieldType = fieldMap.get(fields.get(i))[0];
				}
				if ("datetime".equalsIgnoreCase(fieldType)) {
					datebox = (Datebox) rows_Fields.getFellowIfAny(fields.get(i));
					object.getClass().getMethod("set" + fields.get(i), Class.forName("java.util.Date")).invoke(object,
							datebox.getValue());
				} else {
					textbox = (Textbox) rows_Fields.getFellowIfAny(fields.get(i));
					object.getClass().getMethod("set" + fields.get(i), Class.forName("java.lang.String")).invoke(object,
							textbox.getValue());
				}
			} catch (WrongValueException we) {
				logger.error("Exception: ", we);
				wve.add(we);
			} catch (Exception e) {
				logger.error("Exception: ", e);
			}
		}

		if (!wve.isEmpty()) {
			logger.debug("Throwing occured Errors By using WrongValueException");
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}

		// query for getting list of values on validation
		resultList = getDedupParmService().validate(resultQuery, (CustomerDedup) object);
		resultCount.setValue("Total number of records are :" + resultList.size());
		this.gb_resultCount.setVisible(true);
		this.listBoxSqlView.setVisible(true);
		this.paging_South.setVisible(true);

		// set the paging params
		this.pagingSqlViewResult.setPageSize(getCountRows());
		this.pagingSqlViewResult.setDetailed(true);
		this.pagingSqlViewResult.setTotalSize(resultList.size());
		this.pagingSqlViewResult.addEventListener("onPaging", new OnPagingEventListener());

		separatingList();

	}

	// paging Event Class for pagination
	public final class OnPagingEventListener implements EventListener<Event> {

		public OnPagingEventListener() {
		    super();
		}

		@Override
		public void onEvent(Event event) {

			final PagingEvent pe = (PagingEvent) event;
			final int pageNo = pe.getActivePage();
			final int start = pageNo * getCountRows();
			pagingSqlViewResult.setTotalSize(resultList.size());

			int incValue = pageNo + 1;
			renderlist(start, incValue);

		}
	}

	// method for rendering values into listBox
	public void renderlist(int start, int incValue) {
		logger.debug("Entering");

		int columnCount = keyList.size();
		Listitem listitem = null;
		Listcell listcell = null;
		listBoxSqlView.getItems().clear();

		int count = 1;

		for (int c = columnCount * start; c < ((incValue * (columnCount * getCountRows())) - 1); c++) {
			if (count == 1) {
				listitem = new Listitem();
			}
			if (c == valueList.size()) {
				break;
			}
			if ((valueList.get(c) != null) && (StringUtils.isNotEmpty(valueList.get(c).toString()))) {
				listcell = new Listcell(valueList.get(c).toString());
			} else {
				listcell = new Listcell("");
			}

			listcell.setParent(listitem);

			if (count == columnCount) {
				listitem.setHeight("20px");
				listBoxSqlView.appendChild(listitem);
				count = 0;
			}
			count++;
		}
		logger.debug("Leaving");
	}

	/**
	 * when the "close" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnClose(Event event) throws InterruptedException {
		logger.debug("Entering");
		this.window_SqlViewResult.onClose();
		logger.debug("Leaving");
	}

	// Getters & Setters

	public void setCountRows(int countRows) {
		this.countRows = countRows;
	}

	public int getCountRows() {
		return countRows;
	}

	public DedupParmService getDedupParmService() {
		return dedupParmService;
	}

	public void setDedupParmService(DedupParmService dedupParmService) {
		this.dedupParmService = dedupParmService;
	}

	private String getLabel(String value) {
		String label = Labels.getLabel(value + "_label");
		if (StringUtils.isBlank(label)) {
			return value;
		}
		return label;
	}
}
