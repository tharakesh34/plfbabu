package com.pennant.webui.financemanagement.presentmentdetail;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Filedownload;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Timer;
import org.zkoss.zul.Window;

import com.pennant.backend.util.PennantConstants;
import com.pennant.util.PennantAppUtil;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.dataengine.config.DataEngineConfig;
import com.pennanttech.dataengine.constants.ExecutionStatus;
import com.pennanttech.dataengine.model.EventProperties;
import com.pennanttech.dataengine.util.EncryptionUtil;
import com.pennanttech.framework.core.constants.SortOrder;
import com.pennanttech.interfacebajaj.model.FileDownlaod;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.jdbc.search.Filter;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.service.AmazonS3Bucket;

public class PresentmentDetailFileDownloadListCtrl extends GFCBaseListCtrl<FileDownlaod> {
	private static final long serialVersionUID = 1L;

	private Window window_PresentmentDetailFileDownloadList;
	private Borderlayout borderLayout_PresentmentDetailFileDownloadList;
	private Paging pagingFileDownloadList;
	private Listbox listBoxFileDownload;
	protected Button btnRefresh;
	protected Timer timer;
	private Button downlaod;
	String module = null;
	@Autowired
	protected DataEngineConfig dataEngineConfig;

	protected AmazonS3Bucket bucket;

	/**
	 * default constructor.<br>
	 */
	public PresentmentDetailFileDownloadListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "FileDownload";
		super.pageRightName = "FileDownload";

		this.module = getArgument("module");

		super.tableName = "DE_FILE_CONTROL_VIEW";
		super.queueTableName = "DE_FILE_CONTROL_VIEW";

	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	public void onCreate$window_PresentmentDetailFileDownloadList(Event event) {
		logger.debug(Literal.ENTERING);

		// Set the page level components.
		setPageComponents(window_PresentmentDetailFileDownloadList, borderLayout_PresentmentDetailFileDownloadList,
				listBoxFileDownload, pagingFileDownloadList);
		setItemRender(new FileDownloadListModelItemRenderer());

		registerField("ID", SortOrder.DESC);
		registerField("FileLocation");
		registerField("FileName");
		registerField("Status");
		registerField("Name");
		registerField("EndTime");
		registerField("ConfigId");
		registerField("PostEvent");

		doRenderPage();
		search();
		this.borderLayout_PresentmentDetailFileDownloadList.setHeight(borderLayoutHeight + "px");
		logger.debug(Literal.LEAVING);
	}

	protected void doAddFilters() {
		super.doAddFilters();
		List<String> list = new ArrayList<>();

		list.add("PRESENTMENT_REQUEST");
		list.add("PRESENTMENT_REQUEST_PDC");
		Filter[] filter = new Filter[2];
		filter[0] = new Filter("Type", "Presentment", Filter.OP_EQUAL);
		filter[1] = new Filter("RequestType", "Export", Filter.OP_EQUAL);
		list.addAll(PennantAppUtil.getConfigNames(filter));

		this.searchObject.addFilterIn("NAME", list);
	}

	/**
	 * Call the FileDownload dialog with a new empty entry. <br>
	 */
	public void onClick$btnRefresh(Event event) {
		refresh();
	}

	private void refresh() {
		doReset();
		search();
	}

	public void onClick_Downlaod(ForwardEvent event) {
		logger.debug(Literal.ENTERING);
		try {

			Button downloadButton = (Button) event.getOrigin().getTarget();
			FileDownlaod fileDownlaod = (FileDownlaod) downloadButton.getAttribute("object");

			if (com.pennanttech.dataengine.Event.MOVE_TO_S3_BUCKET.name().equals(fileDownlaod.getPostEvent())) {
				String prefix = loadS3Bucket(fileDownlaod.getConfigId());

				downloadFromS3Bucket(prefix, fileDownlaod.getFileName());
			} else {
				downloadFromServer(fileDownlaod);
			}
			dataEngineConfig.saveDowloadHistory(fileDownlaod.getId(), getUserWorkspace().getUserDetails().getUserId());
			refresh();
		} catch (Exception e) {
			MessageUtil.showError(e.getMessage());
		}
		logger.debug(Literal.LEAVING);
	}

	private String loadS3Bucket(long configId) {

		EventProperties eventproperties = dataEngineConfig.getEventProperties(configId, "S3");

		bucket = new AmazonS3Bucket(eventproperties.getRegionName(), eventproperties.getBucketName(),
				EncryptionUtil.decrypt(eventproperties.getAccessKey()),
				EncryptionUtil.decrypt(eventproperties.getSecretKey()));

		return eventproperties.getPrefix();
	}

	private void downloadFromServer(FileDownlaod fileDownlaod) throws IOException {
		String filePath = fileDownlaod.getFileLocation();
		String fileName = fileDownlaod.getFileName();

		if (filePath != null && fileName != null) {
			filePath = filePath.concat("/").concat(fileName);
		}

		ByteArrayOutputStream stream = new ByteArrayOutputStream();

		InputStream inputStream = new FileInputStream(filePath);
		int data;
		while ((data = inputStream.read()) >= 0) {
			stream.write(data);
		}

		inputStream.close();

		Filedownload.save(stream.toByteArray(), "text/plain", fileName);
		stream.close();
	}

	private void downloadFromS3Bucket(String prefix, String fileName) throws Exception {
		String key = prefix.concat("/").concat(fileName);

		byte[] fileData = bucket.getObject(key);
		Filedownload.save(fileData, "text/plain", fileName);
	}

	/**
	 * Item renderer for listitems in the listbox.
	 * 
	 */
	private class FileDownloadListModelItemRenderer implements ListitemRenderer<FileDownlaod>, Serializable {
		private static final long serialVersionUID = 1L;

		@Override
		public void render(Listitem item, FileDownlaod fileDownlaod, int count) {
			Listcell lc;

			lc = new Listcell(fileDownlaod.getName());
			lc.setParent(item);

			lc = new Listcell(fileDownlaod.getFileName());
			lc.setParent(item);

			lc = new Listcell(DateUtil.format(fileDownlaod.getEndTime(), PennantConstants.dateTimeFormat));
			lc.setParent(item);

			lc = new Listcell(ExecutionStatus.getStatus(fileDownlaod.getStatus()).getValue());
			lc.setParent(item);

			lc = new Listcell();
			downlaod = new Button();
			downlaod.addForward("onClick", self, "onClick_Downlaod");
			lc.appendChild(downlaod);
			downlaod.setLabel("Download");
			downlaod.setAttribute("object", fileDownlaod);

			StringBuilder builder = new StringBuilder();
			builder.append(fileDownlaod.getFileLocation());
			builder.append(File.separator);
			builder.append(fileDownlaod.getFileName());

			File file = new File(builder.toString());

			if (fileDownlaod.getAlwFileDownload() != null && !"1".equals(fileDownlaod.getAlwFileDownload())) {
				downlaod.setDisabled(true);
				downlaod.setTooltiptext("Not allowed to download.");
			} else if (!ExecutionStatus.S.name().equals(fileDownlaod.getStatus())) {
				downlaod.setDisabled(true);
				downlaod.setTooltiptext("File generation failed.");
			}

			if (!com.pennanttech.dataengine.Event.MOVE_TO_S3_BUCKET.name().equals(fileDownlaod.getPostEvent())) {
				if (!file.exists()) {
					downlaod.setDisabled(true);
					downlaod.setTooltiptext("File not available.");
				}
			}

			lc.setParent(item);
		}
	}

	public void onTimer$timer(Event event) {
		if (pagingFileDownloadList.getActivePage() == 0) {
			Events.postEvent("onCreate", this.window_PresentmentDetailFileDownloadList, event);
			searchObject.clearFields();
		}
	}
}
