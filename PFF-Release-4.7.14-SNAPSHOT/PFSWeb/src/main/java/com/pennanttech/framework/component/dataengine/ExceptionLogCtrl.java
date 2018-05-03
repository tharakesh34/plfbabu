package com.pennanttech.framework.component.dataengine;

import java.util.List;

import org.apache.log4j.Logger;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Button;
import org.zkoss.zul.ListModel;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Window;

import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.dataengine.model.DataEngineLog;

public class ExceptionLogCtrl extends GFCBaseCtrl<DataEngineLog> {
	private static final Logger logger = Logger.getLogger(ExceptionLogCtrl.class);
	private static final long serialVersionUID = 1L;

	protected Window window_ExceptionLog;
	protected Listbox listBox_ExceptionLog;
	protected Button btnClose;
	protected boolean preview;

	protected List<DataEngineLog> list;

	@SuppressWarnings("unchecked")
	public void onCreate$window_ExceptionLog(Event event) throws Exception {
		setPageComponents(window_ExceptionLog);

		list = (List<DataEngineLog>) arguments.get("List");
		preview = (boolean) arguments.get("preview");

		if (list != null) {
			this.listBox_ExceptionLog.setItemRenderer(new ExceptionLogListModelItemRenderer());
			ListModel<DataEngineLog> listModel = new ListModelList<DataEngineLog>(list);
			listBox_ExceptionLog.setModel(listModel);
		}
		setDialog(DialogType.MODAL);
	}

	/**
	 * When user clicks on "Close"
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClick$btnClose(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		if (preview) {
			this.window_ExceptionLog.onClose();
		} else {
			closeDialog();
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Item renderer for listItems in the listBox.
	 * 
	 */
	public class ExceptionLogListModelItemRenderer implements ListitemRenderer<DataEngineLog> {

		public void render(Listitem item, DataEngineLog deLog, int count) throws Exception {
			Listcell lc;
			lc = new Listcell(String.valueOf(deLog.getId()));
			lc.setParent(item);

			lc = new Listcell(String.valueOf(deLog.getKeyId()));
			lc.setParent(item);

			lc = new Listcell(deLog.getStatus());
			lc.setParent(item);

			lc = new Listcell(deLog.getReason());
			lc.setParent(item);
			item.setAttribute("data", deLog);
		}
	}
}
