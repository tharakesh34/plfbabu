package com.pennanttech.pennapps.core.lic;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.zkoss.util.media.Media;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.UploadEvent;
import org.zkoss.zul.Button;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.lic.LicenseLoader;
import com.pennanttech.pennapps.lic.exception.LicenseException;
import com.pennanttech.pennapps.lic.model.LicenseFile;
import com.pennanttech.pennapps.web.util.MessageUtil;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class LicenseUploadCtrl extends GFCBaseCtrl<LicenseFile> {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(LicenseUploadCtrl.class);
	
	protected Button upload;
	protected Window window_LicenseUploadList;
	protected Textbox fileName;
	protected Button btnBrowse;

	private Media media = null;
	private LicenseFile licenseFile;
	private LicenseLoader licenseLoader = LicenseLoader.getInstance();

	/**
	 * default constructor.<br>
	 */
	public LicenseUploadCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
	}

	public void onCreate$window_LicenseUploadList(Event event) throws Exception {
		this.window_LicenseUploadList.doModal();
	}

	public void onUpload$btnBrowse(UploadEvent event) throws Exception {
		media = event.getMedia();

		if (!(StringUtils.endsWith(media.getName().toLowerCase(), ".lic"))) {
			MessageUtil.showError("The uploaded file could not be recognized. Please upload a valid lic file.");
			media = null;
			return;
		}
		fileName.setText(media.getName());
	}

	public void onClick$upload(Event event) throws InterruptedException, IOException {
		logger.debug(Literal.ENTERING);

		boolean result = true;
		String oldFileName = "0";
		String newFileName = "0";
		String newName = "1";
		String oldName = "0";
		this.fileName.setErrorMessage("");
		if (this.fileName.getValue() == null || this.fileName.getValue().isEmpty()) {
			MessageUtil.showError("Please select the .lic file to upload.");
			return;
		}
		// get active data from table because checking valid file or not
		licenseFile = licenseLoader.getLicenceFile();
		if (licenseFile != null && licenseFile.getName() !=null) {
			oldFileName = licenseFile.getName();
			newFileName = this.fileName.getValue();
			newName = newFileName.substring(newFileName.lastIndexOf("_") + 1, newFileName.lastIndexOf(".lic"));
			oldName = oldFileName.substring(oldFileName.lastIndexOf("_") + 1, oldFileName.lastIndexOf(".lic"));
		}
		try {
			if (Long.parseLong(newName) > Long.parseLong(oldName)) {
				if (MessageUtil.YES == MessageUtil
						.confirm("Are you sure want to upload this file " + this.fileName.getValue())) {

					LicenseFile alicenseFile = new LicenseFile();
					doWriteComponentsToBean(alicenseFile);
					if (media.getByteData() != null && media.getByteData().length > 0) {
						alicenseFile.setContent((media.getByteData()));
					} else {
						MessageUtil.showError("File content is empty, please upload a valid file.");
						return;
					}

					try {
						licenseLoader.save(alicenseFile);
						result = true;
					} catch (LicenseException e) {
					}

					if (result) {
						MessageUtil.showMessage("File uploaded successfully.");
						this.window_LicenseUploadList.onClose();
						Executions.sendRedirect("loginDialog.zul");
					} else {
						MessageUtil.showMessage("File upload failed.");
					}
				}
			} else {
				MessageUtil.showError("Please select the valid file to upload.");
			}
		} catch (NumberFormatException e) {
			MessageUtil.showError("Please upload a valid file. ");
		} catch (IllegalArgumentException e) {
			MessageUtil.showError("File content is empty, please upload a valid file. ");
		} finally {
			media = null;
			this.fileName.setText("");
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * The Click event is raised when the Close Button control is clicked.
	 * 
	 * @param event
	 *            An event sent to the event handler of a component.
	 */
	public void onClick$btnClose(Event event) {
		getUserWorkspace().doLogout();
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aCity
	 */
	public void doWriteComponentsToBean(LicenseFile licenseFile) {
		logger.debug(Literal.ENTERING);

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		try {
			licenseFile.setName(this.fileName.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			licenseFile.setActive(true);
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			licenseFile.setUploadedOn(new Timestamp(System.currentTimeMillis()));
			licenseFile.setUploadedBy(getUserWorkspace().getLoggedInUser().getUserId());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		if (wve.size() > 0) {
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = (WrongValueException) wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}

		logger.debug(Literal.LEAVING);

	}

}