
package com.pennant.webui.applicationmaster.download;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.zkoss.util.media.Media;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.event.UploadEvent;
import org.zkoss.zul.Button;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Row;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.backend.service.ckyc.CKYCService;
import com.pennant.util.PennantAppUtil;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.App;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.web.util.MessageUtil;

public class CKYCProcessCtrl extends GFCBaseCtrl {
	private static final long serialVersionUID = 223801324705386693L;
	private static final Logger logger = LogManager.getLogger(CKYCProcessCtrl.class);

	protected Window window_Download; // autoWired
	protected Textbox fileName;
	protected Radio radioDownload; // autoWired
	protected Radio radioUpload;
	protected Button btnStartCkycFile;
	protected Button btnUpload;
	protected Row rowDownload;
	protected Radiogroup radiogroupCKYC;
	protected Row rowUpload;
	private CKYCService ckycService;
	private String fileNameLead = null;
	private File file;

	public CKYCProcessCtrl() {
		super();
	}

	public void onCreate$window_Download(Event event) throws Exception {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_Download);

		logger.debug("Leaving" + event.toString());
	}

	public void onCheck$radiogroupCKYC(Event event) throws Exception {

		if (radioUpload.isChecked()) {
			rowDownload.setVisible(false);
			rowUpload.setVisible(true);
		} else if (radioDownload.isChecked()) {

			rowUpload.setVisible(false);
			rowDownload.setVisible(true);
		}
	}

	public void onUpload$btnUpload(UploadEvent event) throws Exception {
		logger.debug(Literal.ENTERING);
		fileName.setText("");
		Media media = event.getMedia();

		if (!PennantAppUtil.uploadDocFormatValidation(media)) {
			return;
		}
		if (!(StringUtils.endsWith(media.getName().toLowerCase(), ".txt"))) {
			MessageUtil.showError("The uploaded file could not be recognized. Please upload a valid Text file.");
			media = null;
			return;
		}

		fileName.setText(media.getName());
		String fName = media.getName();
		writeFile(media, fName);
		logger.debug(Literal.LEAVING);
	}

	private void writeFile(Media media, String fName) throws IOException {
		logger.debug(Literal.ENTERING);
		File parent = new File(App.getProperty("external.interface.cKYC.UploadLoaction"));

		if (!parent.exists()) {
			parent.mkdirs();
		}
		file = new File(parent.getPath().concat(File.separator).concat(media.getName()));
		if (file.exists()) {
			file.delete();
		}
		file.createNewFile();
		FileUtils.writeStringToFile(file, media.getStringData());
		FileInputStream fis = null;

		fis = new FileInputStream(file);
		InputStreamReader isr = new InputStreamReader(fis, Charset.forName("UTF-8"));
		BufferedReader br = new BufferedReader(isr);
		String line;
		String batchNo = null;
		String rowNo = null;
		String ckycNo = null;
		while ((line = br.readLine()) != null) {

			String[] words = line.split("\\|");
			if (StringUtils.equalsIgnoreCase(words[0], "10")) {
				batchNo = words[1];
				break;
			}
		}
		if (batchNo != null) {
			while ((line = br.readLine()) != null) {
				// Do your thing with line
				String[] words = line.split("\\|");
				if (StringUtils.equalsIgnoreCase(words[0], "20")) {
					rowNo = words[1];
					ckycNo = words[18];
					if (rowNo != null && ckycNo != null && batchNo != null && ckycNo != null && !ckycNo.isEmpty()) {
						ckycService.updateCkycNo(ckycNo, batchNo, rowNo);
						int custId = ckycService.getCustId(ckycNo);
						ckycService.updateCustomerWithCKycNo(custId, ckycNo);

					}

				}
			}
		}

		logger.debug(Literal.LEAVING);
	}

	public void btndownload(Event event) throws Exception {
		rowUpload.setVisible(false);
		rowDownload.setVisible(true);

	}

	public void onClick$btnStartCkycFile(ForwardEvent event) throws Exception {
		boolean flag = false;
		List<Long> id = ckycService.getId();
		if (!flag) {
			flag = ckycService.prepareData(id);
		}
		if (flag) {
			btnStartCkycFile.setVisible(true);

		}
	}

	public void setCkycService(CKYCService ckycService) {
		this.ckycService = ckycService;
	}

}
