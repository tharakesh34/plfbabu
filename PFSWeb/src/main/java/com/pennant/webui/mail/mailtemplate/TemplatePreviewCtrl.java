package com.pennant.webui.mail.mailtemplate;

import java.io.IOException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Button;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Div;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Html;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Row;
import org.zkoss.zul.Rows;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.app.util.DateUtility;
import com.pennant.backend.model.mail.MailTemplateData;
import com.pennant.backend.util.NotificationConstants;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.util.DateUtil.DateFormat;
import com.pennanttech.pennapps.web.util.MessageUtil;

import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

public class TemplatePreviewCtrl extends GFCBaseCtrl<Object> {
	private static final long serialVersionUID = 8221803565044061531L;
	private static final Logger logger = LogManager.getLogger(TemplatePreviewCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the ZUL-file
	 * are getting autowired by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_TemplatePreview; // autowired
	protected Rows rows_Fields; // autowired
	protected Textbox textbox;
	protected Datebox datebox;
	protected Decimalbox decimalbox;
	protected Intbox intbox;
	protected Button btnPreview;
	protected Grid grid_Preview;
	// protected Groupbox gb_ckEditor;
	protected Tab fieldTab;
	protected Tab previewTab;
	protected Div previewTabDiv;

	protected MailTemplateDialogCtrl mailTemplateDialogCtrl;
	private String mailContent = "";
	private String module;
	private int dialogHeight = 0;
	private boolean isSelected = true;

	public TemplatePreviewCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "";
	}

	// Component Events

	/**
	 * Before binding the data and calling the dialog window we check, if the ZUL-file is called with a parameter for a
	 * selected TransactionEntry object in a Map.
	 * 
	 * @param event
	 */
	@SuppressWarnings("unchecked")
	public void onCreate$window_TemplatePreview(Event event) {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_TemplatePreview);

		Map<String, String> fieldsMap = new HashMap<String, String>();
		// READ OVERHANDED parameters !

		if (arguments.containsKey("fieldsMap")) {
			fieldsMap = (Map<String, String>) arguments.get("fieldsMap");
		}
		if (arguments.containsKey("mailContent")) {
			mailContent = (String) arguments.get("mailContent");
		}

		if (arguments.containsKey("module")) {
			module = (String) arguments.get("module");
		}
		// READ OVERHANDED parameters !
		if (arguments.containsKey("mailTemplateDialogCtrl")) {
			this.mailTemplateDialogCtrl = (MailTemplateDialogCtrl) arguments.get("mailTemplateDialogCtrl");
		}
		Label label;
		Row row = null;
		Object[] componentArray = (Object[]) fieldsMap.keySet().toArray();
		for (int k = 0; k < componentArray.length; k++) {
			String field = "";
			isSelected = false;
			if (componentArray[k].toString().contains("?")) {
				field = field.substring(0, field.lastIndexOf('?')) + "}";
			} else {
				field = componentArray[k].toString();
			}
			String[] descFormat = fieldsMap.get(field).split(":");
			if (k % 2 == 0) {
				row = new Row();
				row.setHeight("20px");
			}
			label = new Label(descFormat[0]);
			row.appendChild(label);
			label = new Label(":");
			row.appendChild(label);
			if ("D".equals(descFormat[1])) {
				datebox = new Datebox();
				datebox.setFormat(DateFormat.SHORT_DATE.getPattern());
				datebox.setId(componentArray[k].toString());
				row.appendChild(datebox);
			} else if ("T".equals(descFormat[1])) {
				datebox = new Datebox();
				datebox.setFormat(PennantConstants.dateTimeFormat);
				datebox.setId(componentArray[k].toString());
				row.appendChild(datebox);
			} else if ("AM2".equals(descFormat[1])) {
				decimalbox = new Decimalbox();
				decimalbox.setFormat(PennantApplicationUtil.getAmountFormate(2));
				decimalbox.setId(componentArray[k].toString());
				row.appendChild(decimalbox);
			} else if ("AM3".equals(descFormat[1])) {
				decimalbox = new Decimalbox();
				decimalbox.setFormat(PennantApplicationUtil.getAmountFormate(3));
				decimalbox.setId(componentArray[k].toString());
				row.appendChild(decimalbox);
			} else if ("N".equals(descFormat[1])) {
				intbox = new Intbox();
				intbox.setId(componentArray[k].toString());
				row.appendChild(intbox);
			} else {
				textbox = new Textbox();
				textbox.setId(componentArray[k].toString());
				row.appendChild(textbox);
			}
			row.setParent(rows_Fields);
		}

		if (isSelected) {
			setContent(new HashMap<String, Object>());
			this.fieldTab.setVisible(false);
			this.btnPreview.setVisible(false);
			this.previewTab.setSelected(true);
		}

		showDialog();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Opens the SearchDialog window modal.
	 */
	private void showDialog() {
		logger.debug("Entering");
		try {

			// open the dialog in modal mode
			if (isSelected) {
				this.dialogHeight = grid_Preview.getRows().getVisibleItemCount() * 20 + 800;
			} else {
				this.dialogHeight = grid_Preview.getRows().getVisibleItemCount() * 20 + 400;
			}
			this.window_TemplatePreview.setHeight(this.dialogHeight + "px");
			this.previewTabDiv.setHeight(this.dialogHeight - 100 + "px");
			this.window_TemplatePreview.doModal();
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving");
	}

	/**
	 * when the "btnPreview" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnPreview(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		if (this.previewTabDiv.getChildren().size() > 0) {
			this.previewTabDiv.removeChild((Html) this.previewTabDiv.getChildren().get(0));
		}
		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();
		try {
			Method method = null;
			MailTemplateData data = new MailTemplateData();

			Map<String, Object> model = new HashMap<String, Object>();
			if ("FIN".equals(module) || "CRD".equals(module)) {
				model.put("vo", data);
			}

			for (int i = 0; i < this.rows_Fields.getChildren().size(); i++) {
				Row row = (Row) this.rows_Fields.getChildren().get(i);
				Label lbl = null;
				for (int k = 0; k < row.getChildren().size(); k++) {
					if (k == 2 || k == 5) {
						if (k == 2) {
							lbl = (Label) row.getChildren().get(0);
						} else if (k == 5) {
							lbl = (Label) row.getChildren().get(3);
						}

						if (row.getChildren().get(k) instanceof Datebox) {
							Datebox db = (Datebox) row.getChildren().get(k);
							Timestamp ts = null;
							Date date = null;
							try {
								if (db.getValue() == null) {
									throw new WrongValueException(db, Labels.getLabel("FIELD_NO_EMPTY",
											new String[] { lbl.getValue().replaceAll(":", "") }));
								}
								if (db.getFormat().equals(PennantConstants.dateTimeFormat)) {
									method = data.getClass().getDeclaredMethod("set" + getFieldValue(db.getId()),
											Timestamp.class);
									ts = new Timestamp(DateUtility
											.parse(DateUtility.format(db.getValue(), PennantConstants.DBDateTimeFormat),
													PennantConstants.DBDateTimeFormat)
											.getTime());
									method.invoke(data, ts);
								} else {
									method = data.getClass().getDeclaredMethod("set" + getFieldValue(db.getId()),
											Date.class);
									date = DateUtility.getDatePart(db.getValue());
									method.invoke(data, date);
								}

							} catch (WrongValueException e) {
								wve.add(e);
							}

						} else if (row.getChildren().get(k) instanceof Decimalbox) {
							Decimalbox dcb = (Decimalbox) row.getChildren().get(k);
							try {
								if (dcb.getValue() == null) {
									throw new WrongValueException(dcb, Labels.getLabel("FIELD_NO_EMPTY",
											new String[] { lbl.getValue().replaceAll(":", "") }));
								}
								method = data.getClass().getDeclaredMethod("set" + getFieldValue(dcb.getId()),
										BigDecimal.class);
								method.invoke(data, PennantApplicationUtil
										.formateAmount(PennantApplicationUtil.unFormateAmount(dcb.getValue(), 2), 2));
							} catch (WrongValueException e) {
								wve.add(e);
							}
						} else if (row.getChildren().get(k) instanceof Intbox) {
							Intbox intb = (Intbox) row.getChildren().get(k);
							try {
								if (intb.getValue() == null) {
									throw new WrongValueException(intb, Labels.getLabel("FIELD_NO_EMPTY",
											new String[] { lbl.getValue().replaceAll(":", "") }));
								}
								method = data.getClass().getDeclaredMethod("set" + getFieldValue(intb.getId()),
										Integer.TYPE);
								method.invoke(data, intb.getValue());
							} catch (WrongValueException e) {
								wve.add(e);
							}
						} else {
							Textbox tb = (Textbox) row.getChildren().get(k);
							try {
								if (StringUtils.isBlank(tb.getValue())) {
									throw new WrongValueException(tb, Labels.getLabel("FIELD_NO_EMPTY",
											new String[] { lbl.getValue().replaceAll(":", "") }));
								}
								method = data.getClass().getDeclaredMethod("set" + getFieldValue(tb.getId()),
										String.class);
								method.invoke(data, tb.getValue());
							} catch (WrongValueException e) {
								wve.add(e);
							}
						}
					}
				}
			}

			if (wve.size() > 0) {
				MessageUtil.showMessage("Please Enter Some Values ");
				this.fieldTab.setSelected(true);
			} else {
				setContent(model);
			}
		} catch (Exception e) {
			logger.error("Exception: ", e);
		}

		logger.debug("Leaving" + event.toString());
	}

	private String getFieldValue(String field) {
		String fieldValue = field.substring(0, 1).toUpperCase();
		fieldValue = fieldValue + field.substring(1);
		return fieldValue;
	}

	private void setContent(Map<String, Object> model) {
		logger.debug("Entering");
		try {
			StringTemplateLoader loader = new StringTemplateLoader();
			String content = mailContent;
			loader.putTemplate("Template", content);

			Configuration configuration = new Configuration(Configuration.DEFAULT_INCOMPATIBLE_IMPROVEMENTS);
			configuration.setTemplateLoader(loader);
			Template template = configuration.getTemplate("Template");
			this.previewTabDiv
					.appendChild(
							new Html(new String(
									FreeMarkerTemplateUtils.processTemplateIntoString(template, model)
											.getBytes(NotificationConstants.DEFAULT_CHARSET),
									NotificationConstants.DEFAULT_CHARSET)));
			this.previewTab.setSelected(true);

		} catch (TemplateException | IOException e) {
			MessageUtil.showError(e);
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
		logger.debug("Entering" + event.toString());
		try {
			this.window_TemplatePreview.onClose();
		} catch (Exception e) {
			logger.error("Exception: ", e);
			// close anyway
			this.window_TemplatePreview.onClose();
		}
		logger.debug("Leaving" + event.toString());
	}
}
