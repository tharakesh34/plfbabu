package com.pennant.webui.cersai.cersaidownload;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;

import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Filedownload;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Window;

import com.pennant.backend.util.CersaiConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;
import com.pennanttech.interfacebajaj.model.FileDownlaod;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pff.cersai.CERSAIDownloadProcess;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the file.<br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * 
 */
public class CERSAIFileDownloadListctrl extends GFCBaseListCtrl<FileDownlaod> {
	private static final long serialVersionUID = 1L;

	protected Window window_CERSAIFileDownloadList;
	protected Borderlayout borderLayout_CERSAIDownloadList;
	protected Paging pagingFileDownloadList;
	protected Listbox listBoxFileDownload;
	protected Button btnRefresh;
	protected Button btnexecute;
	protected Listbox sortOperatorFileType;
	protected Combobox fileType;

	private Button downlaod;

	private CERSAIDownloadProcess cersaiDownloadProcess;

	/**
	 * default constructor.<br>
	 */
	public CERSAIFileDownloadListctrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "FileDownload";
		super.pageRightName = "FileDownload";
		super.tableName = "CERSAI_FILE_INFO_VIEW";
		super.queueTableName = "CERSAI_FILE_INFO_VIEW";

	}

	public void onCreate$window_CERSAIFileDownloadList(Event event) throws Exception {
		logger.debug(Literal.ENTERING);

		// Set the page level components.
		setPageComponents(window_CERSAIFileDownloadList, borderLayout_CERSAIDownloadList, listBoxFileDownload,
				pagingFileDownloadList);

		setItemRender(new FileDownloadListModelItemRenderer());

		registerField("ID", SortOrder.DESC);
		registerField("segmentType", fileType, null, sortOperatorFileType, Operators.STRING);
		registerField("FileName");
		registerField("CreatedOn EndTime");
		registerField("Status");
		registerField("FileLocation");

		fillComboBox(this.fileType, PennantConstants.List_Select, PennantStaticListUtil.getCersaiTypeList(), "");

		doRenderPage();
		search();

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Call the FileDownload dialog with a new empty entry. <br>
	 */
	public void onClick$btnRefresh(Event event) {
		refresh();
	}

	public void onChange$fileType(Event event) throws Exception {
		search();
	}

	public void onClick_Downlaod(ForwardEvent event) throws Exception {
		logger.debug(Literal.ENTERING);
		try {

			Button downloadButt = (Button) event.getOrigin().getTarget();
			FileDownlaod fileDownlaod = (FileDownlaod) downloadButt.getAttribute("object");

			downloadFromServer(fileDownlaod);

			search();
		} catch (Exception e) {
			MessageUtil.showError(e.getMessage());
		}
		logger.debug(Literal.LEAVING);
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
		try {
			while ((data = inputStream.read()) >= 0) {
				stream.write(data);
			}
			Filedownload.save(stream.toByteArray(), "text/plain", fileName);
		} catch (IOException e) {
			logger.error(Literal.EXCEPTION, e);
		} finally {
			inputStream.close();
			stream.close();
		}
	}

	private void refresh() {
		doReset();
		search();
	}

	/**
	 * Item renderer for listitems in the listbox.
	 * 
	 */
	private class FileDownloadListModelItemRenderer implements ListitemRenderer<FileDownlaod>, Serializable {
		private static final long serialVersionUID = 1L;

		@Override
		public void render(Listitem item, FileDownlaod fileDownlaod, int count) throws Exception {
			Listcell lc;
			lc = new Listcell(fileDownlaod.getSegmentType());
			lc.setParent(item);

			lc = new Listcell(fileDownlaod.getFileName());
			lc.setParent(item);

			lc = new Listcell(DateUtil.formatToLongDate(fileDownlaod.getEndTime()));
			lc.setParent(item);

			lc = new Listcell(fileDownlaod.getStatus());
			lc.setParent(item);

			lc = new Listcell();
			downlaod = new Button();
			downlaod.addForward("onClick", self, "onClick_Downlaod");
			lc.appendChild(downlaod);
			downlaod.setLabel("Download");
			downlaod.setTooltiptext("Download");

			downlaod.setAttribute("object", fileDownlaod);

			StringBuilder builder = new StringBuilder();
			builder.append(fileDownlaod.getFileLocation());
			builder.append(File.separator);
			builder.append(fileDownlaod.getFileName());

			File file = new File(builder.toString());

			if (!"C".equals(fileDownlaod.getStatus())) {
				downlaod.setDisabled(true);
				downlaod.setTooltiptext("CERSAI request for file generation failed.");
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

	/**
	 * Call the FileDownload dialog with a new empty entry. <br>
	 */
	public void onClick$btnexecute(Event event) throws Exception {
		String downloadType = fileType.getSelectedItem().getValue();

		try {
			if (CersaiConstants.ADD.equals(downloadType)) {
				cersaiDownloadProcess.generateAddReport();
			} else if (CersaiConstants.MODIFY.equals(downloadType)) {
				cersaiDownloadProcess.generateModifyReport();
			} else if (CersaiConstants.SATISFY.equals(downloadType)) {
				cersaiDownloadProcess.generateSatisfactionReport();
			} else {
				MessageUtil.showError("File Type cannot be blank.");
				return;
			}
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
			MessageUtil.showError(e);
		}

		search();
	}

	public CERSAIDownloadProcess getCersaiDownloadProcess() {
		return cersaiDownloadProcess;
	}

	public void setCersaiDownloadProcess(CERSAIDownloadProcess cersaiDownloadProcess) {
		this.cersaiDownloadProcess = cersaiDownloadProcess;
	}

}