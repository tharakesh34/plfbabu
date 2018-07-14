package com.pennant.util;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.zkoss.util.media.AMedia;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zul.Filedownload;
import org.zkoss.zul.Window;

import com.aspose.words.Document;
import com.aspose.words.SaveFormat;
import com.pennant.app.util.PathUtil;
import com.pennant.document.generator.TemplateEngine;

public class AgreementEngine {

	private TemplateEngine templateEngine;
	private static final Logger logger = Logger.getLogger(AgreementEngine.class);

	public AgreementEngine(String assetCode) throws Exception {
		logger.debug("Entering ");
		String templatePath = getTemplatePath(assetCode);
		templateEngine=new TemplateEngine(templatePath,templatePath);
		logger.debug("Leaving");
	}

	public String getTemplatePath(String assetCode) {

		String templatePath = "";
		/**
		 * Disabling the assetCode functionality as assetCode is no longer considered in loan process. As discussed with
		 * Raju. This functionality is moved to collateral and associated at customer side.
		 * 
		 */
		/*
		 * if(StringUtils.isBlank(assetCode)){ templatePath = PathUtil.getPath(PathUtil.FINANCE_AGREEMENTS); }else{
		 * templatePath = PathUtil.getPath(PathUtil.FINANCE_AGREEMENTS)+"/"+assetCode; }
		 */
		templatePath = PathUtil.getPath(PathUtil.FINANCE_AGREEMENTS);
		logger.debug("Template Path:" + templatePath);
		return templatePath;
	}

	public AgreementEngine(String templateSite, String documentSite) throws Exception {
		templateEngine=new TemplateEngine(templateSite,documentSite);
	}

	public void loadTemplate() throws Exception {
		templateEngine.loadTemplate();
	}

	private void showDocument(Window window, String reportName, int format, boolean saved) throws Exception {
		logger.debug("Entering ");
		ByteArrayOutputStream stream = new ByteArrayOutputStream();

		if (saved) {
			InputStream inputStream = new FileInputStream(templateEngine.getDocumentPath());
			int data;

			while ((data = inputStream.read()) >= 0) {
				stream.write(data);
			}

			inputStream.close();
			inputStream = null;
		} else {
			templateEngine.getDocument().save(stream, format);
		}

		if ((SaveFormat.DOCX) == format) {
			Filedownload.save(new AMedia(reportName, "msword", "application/msword", stream.toByteArray()));
		} else {

			Map<String, Object> arg = new HashMap<String, Object>();
			arg.put("reportBuffer", stream.toByteArray());
			arg.put("parentWindow", window);
			arg.put("reportName", reportName);
			arg.put("isAgreement", true);
			arg.put("docFormat", format);

			Executions.createComponents("/WEB-INF/pages/Reports/ReportView.zul", window, arg);
		}
		stream.close();
		stream = null;
		logger.debug("Leaving");
	}

	public byte[] getDocumentInByteArray(String reportName, int format) throws Exception {
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		templateEngine.getDocument().save(stream, format);
		stream.close();
		return stream.toByteArray();
	}

	public void showDocument(Window window, String reportName, int format) throws Exception {
		showDocument(window, reportName, format, false);
	}

	public void mergeFields(String[] fields, Object[] values) throws Exception {
		templateEngine.mergeFields(fields, values);
	}

	public void mergeFields(Object bean) throws Exception {
		templateEngine.mergeFields(bean);
	}

	public void close() {
		templateEngine.close();
	}

	public void setTemplate(String name) {
		templateEngine.setTemplate(name);
	}

	public Document getDocument() {
		return templateEngine.getDocument();
	}

	// TODO: Review
	public void appendToMasterDocument() throws Exception {
		templateEngine.appendToMasterDocument();
		//document = new Document(template.getPath());
	}

	public Document getMasterDocument() {
		return templateEngine.getMasterDocument();
	}
}