package com.pennant.pff.presentment.web;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Window;

import com.pennant.pff.presentment.model.DueExtractionHeader;
import com.pennant.pff.presentment.service.DueExtractionConfigService;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.core.util.DateUtil.DateFormat;
import com.pennanttech.pennapps.web.util.MessageUtil;

public class DueExtractionConfigListCtrl extends GFCBaseListCtrl<DueExtractionHeader> {
	private static final long serialVersionUID = 1L;

	protected Window windowDueExtractionConfigList;
	protected Borderlayout borderLayoutDueExtractionConfigList;
	protected Paging pagingDueExtractionConfigList;
	protected Listbox listBoxDueExtractionConfig;

	private transient DueExtractionConfigService dueExtractionConfigService;

	private List<DueExtractionHeader> headers;

	private static final String HEADER_KEY = "DueExtractionHeader";

	public DueExtractionConfigListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "DueExtractionConfig";
		super.pageRightName = "DueExtractionConfigList";
		super.tableName = "Due_Extraction_Header";
		super.queueTableName = "Due_Extraction_Header";

	}

	public void onCreate$windowDueExtractionConfigList(Event event) {
		logger.debug(Literal.ENTERING.concat(event.toString()));

		setPageComponents(windowDueExtractionConfigList, borderLayoutDueExtractionConfigList,
				listBoxDueExtractionConfig, pagingDueExtractionConfigList);

		headers = new ArrayList<>();
		headers.addAll(dueExtractionConfigService.getDueExtractionHeaders());

		listBoxDueExtractionConfig.setItemRenderer(new AutoPresentationItemRenderer());

		getPagedListWrapper().initList(headers, listBoxDueExtractionConfig, pagingDueExtractionConfigList);

		doRenderPage();

		logger.debug(Literal.LEAVING.concat(event.toString()));
	}

	@Override
	public void search() {
		logger.debug(Literal.ENTERING);

		headers = new ArrayList<>();
		headers.addAll(dueExtractionConfigService.getDueExtractionHeaders());

		getPagedListWrapper().initList(headers, listBoxDueExtractionConfig, pagingDueExtractionConfigList);

		logger.debug(Literal.LEAVING);
	}

	public void onItemDoubleClicked(Event event) {
		logger.debug(Literal.ENTERING.concat(event.toString()));

		Listitem selectedItem = this.listBoxDueExtractionConfig.getSelectedItem();
		if (selectedItem == null) {
			return;
		}

		DueExtractionHeader header = (DueExtractionHeader) selectedItem.getAttribute(HEADER_KEY);

		header.setWorkflowId(getWorkFlowId());
		doShowDialogPage(header);

		logger.debug(Literal.LEAVING.concat(event.toString()));
	}

	private void doShowDialogPage(DueExtractionHeader header) {
		logger.debug(Literal.ENTERING);

		Map<String, Object> arg = getDefaultArguments();
		arg.put(HEADER_KEY, header);
		arg.put("DueExtractionConfigList", this);

		try {
			Executions.createComponents("/WEB-INF/pages/Presentment/DueExtractionConfigDialog.zul", null, arg);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
			MessageUtil.showError(e);
		}

		logger.debug(Literal.LEAVING);
	}

	private class AutoPresentationItemRenderer implements ListitemRenderer<DueExtractionHeader>, Serializable {
		private static final long serialVersionUID = 1L;

		public AutoPresentationItemRenderer() {
			super();
		}

		@Override
		public void render(Listitem item, DueExtractionHeader data, int index) throws Exception {

			Listcell lc;
			lc = new Listcell(data.getExtractionMonth());
			lc.setParent(item);
			lc = new Listcell(DateUtil.format(data.getCreatedOn(), DateFormat.SHORT_DATE));
			lc.setParent(item);
			lc = new Listcell(data.getUsrName());
			lc.setParent(item);
			lc = new Listcell(DateUtil.format(data.getLastMntOn(), DateFormat.SHORT_DATE));
			lc.setParent(item);
			lc = new Listcell(data.getRecordStatus());
			lc.setParent(item);
			lc = new Listcell(data.getRecordType());
			lc.setParent(item);

			item.setAttribute(HEADER_KEY, data);

			ComponentsCtrl.applyForward(item, "onDoubleClick=onItemDoubleClicked");
		}
	}

	@Autowired
	public void setDueExtractionConfigService(DueExtractionConfigService dueExtractionConfigService) {
		this.dueExtractionConfigService = dueExtractionConfigService;
	}

}
