package com.pennant.webui.reports;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.zkoss.util.media.AMedia;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Path;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Filedownload;
import org.zkoss.zul.Tabbox;
import org.zkoss.zul.Window;

import com.aspose.words.SaveFormat;
import com.pennant.ExtendedCombobox;
import com.pennant.app.constants.LengthConstants;
import com.pennant.app.util.PathUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.systemmasters.BalanceConfirmation;
import com.pennant.backend.service.reports.BalanceConfirmationService;
import com.pennant.document.generator.TemplateEngine;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.App;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.web.util.MessageUtil;

public class BalanceConfirmationDialogCtrl extends GFCBaseCtrl<BalanceConfirmation> {
	private static final long serialVersionUID = 4678287540046204660L;
	private final static Logger logger = LogManager.getLogger(BalanceConfirmationDialogCtrl.class);

	protected Window window_BalanceConfirmationDialog;
	protected ExtendedCombobox finReference;

	protected Window dialogWindow;

	private BalanceConfirmation balanceConfirmation = new BalanceConfirmation();
	private transient BalanceConfirmationService balanceConfirmationService;

	public BalanceConfirmationDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "";
	}

	public void onCreate$window_BalanceConfirmationDialog(Event event) throws Exception {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_BalanceConfirmationDialog);

		try {
			doSetFieldProperties();
			// this.window_BalanceConfirmationDialog.doModal();
			setDialog(DialogType.MODAL);
		} catch (Exception e) {
			logger.error("Exception: ", e);
			MessageUtil.showError(Labels.getLabel("label_ReportConfiguredError.error"));
			closeDialog();
		}

		logger.debug("Leaving" + event.toString());
	}

	private void doSetFieldProperties() {
		logger.debug("Entering");

		// Finance Reference
		this.finReference.setModuleName("FinanceMain");
		this.finReference.setValueColumn("FinReference");
		this.finReference.setDisplayStyle(2);
		this.finReference.setValidateColumns(new String[] { "FinReference" });
		this.finReference.setMandatoryStyle(true);
		this.finReference.setMaxlength(LengthConstants.LEN_REF);
		this.finReference.setTextBoxWidth(140);

		logger.debug("Leaving");
	}

	public void onClick$btnClose(Event event) {
		logger.debug(Literal.ENTERING);

		// Close the current window
		this.window_BalanceConfirmationDialog.onClose();

		// Close the current menu item
		final Borderlayout borderlayout = (Borderlayout) Path.getComponent("/outerIndexWindow/borderlayoutMain");
		final Tabbox tabbox = (Tabbox) borderlayout.getFellow("center").getFellow("divCenter")
				.getFellow("tabBoxIndexCenter");
		tabbox.getSelectedTab().close();

		logger.debug(Literal.LEAVING);
	}

	@SuppressWarnings("resource")
	public void onClick$btnGenereate(Event event) throws IllegalAccessException, InvocationTargetException {
		logger.debug(Literal.ENTERING);

		doSetValidation();
		doWriteComponentsToBean(this.balanceConfirmation);

		try {
			String path = App.getResourcePath(PathUtil.BalanceConfirmation);

			Date appldate = SysParamUtil.getAppDate();
			String appDate = DateUtil.formatToLongDate(appldate);
			balanceConfirmation.setAppDate(appDate);

			String reportName = "BalanceConfirmation.docx";
			ByteArrayOutputStream stream = new ByteArrayOutputStream();

			Method[] methods = balanceConfirmation.getClass().getDeclaredMethods();

			for (Method property : methods) {
				if (property.getName().startsWith("get")) {
					String field = property.getName().substring(3);
					Object value;

					try {
						value = property.invoke(balanceConfirmation);
					} catch (Exception e) {
						continue;
					}

					if (value == null) {
						try {
							String stringParameter = "";
							balanceConfirmation.getClass().getMethod("set" + field, new Class[] { String.class })
									.invoke(balanceConfirmation, stringParameter);
						} catch (Exception e) {
							logger.error("Exception: ", e);
						}
					}
				}
			}

			TemplateEngine engine = null;
			try {
				engine = new TemplateEngine(path, path);
			} catch (Exception e) {
				MessageUtil.showError("Path Not Found");
				return;
			}
			try {
				engine.setTemplate("BalanceConfirmation.docx");
			} catch (Exception e) {
				MessageUtil.showError(balanceConfirmation + " Not Found");
				return;
			}

			engine.loadTemplate();
			engine.mergeFields(balanceConfirmation);
			engine.getDocument().save(stream, SaveFormat.DOCX);
			showDocument(this.window_BalanceConfirmationDialog, reportName, SaveFormat.DOCX, false, null, stream);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
			MessageUtil.showError("Template does not exist.");
		}
	}

	public void showDocument(Window window, String reportName, int format, boolean saved, Tabbox tabbox,
			ByteArrayOutputStream stream) throws Exception {
		logger.debug("Entering ");

		if ((SaveFormat.DOCX) == format) {
			Filedownload.save(new AMedia(reportName, "msword", "application/msword", stream.toByteArray()));
		}

		stream = null;
		logger.debug("Leaving");
	}

	public void onFulfill$finReference(Event event) {
		logger.debug("Entering" + event.toString());

		Object dataObject = finReference.getObject();

		if (dataObject instanceof String) {
			this.finReference.setValue(dataObject.toString());
		} else {
			FinanceMain details = (FinanceMain) dataObject;
			if (details != null) {
				this.finReference.setValue(details.getFinReference());
			} else {
				this.finReference.setValue("");
			}
		}

		logger.debug("Leaving" + event.toString());
	}

	public void doWriteComponentsToBean(BalanceConfirmation balanceConfirmation)
			throws IllegalAccessException, InvocationTargetException {
		logger.debug("Entering");

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();
		String reference = "";

		try {
			reference = this.finReference.getValue();
		} catch (WrongValueException we) {
			wve.add(we);
		}

		doRemoveValidation();

		if (wve.isEmpty()) {
			setBalanceConfirmation(balanceConfirmationService.getBalanceConfirmation(reference));
		} else {
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = (WrongValueException) wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}

		logger.debug("Leaving");
	}

	private void doRemoveValidation() {
		logger.debug("Entering");

		this.finReference.setConstraint("");

		logger.debug("Leaving");
	}

	protected void doClearMessage() {
		logger.debug("Entering");

		this.finReference.setErrorMessage("");

		logger.debug("Leaving");
	}

	private void doSetValidation() {
		logger.debug("Entering");

		doClearMessage();
		doRemoveValidation();

		// Finance Type
		this.finReference.setConstraint(new PTStringValidator(
				Labels.getLabel("label_BalanceConfirmationDialog_FinReference.value"), null, true, true));

		logger.debug("Leaving");
	}

	public BalanceConfirmation getBalanceConfirmation() {
		return balanceConfirmation;
	}

	public void setBalanceConfirmation(BalanceConfirmation balanceConfirmation) {
		this.balanceConfirmation = balanceConfirmation;
	}

	public void setBalanceConfirmationService(BalanceConfirmationService balanceConfirmationService) {
		this.balanceConfirmationService = balanceConfirmationService;
	}

}
