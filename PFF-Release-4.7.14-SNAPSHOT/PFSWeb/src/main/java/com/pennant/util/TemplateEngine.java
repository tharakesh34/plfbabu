package com.pennant.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.zkoss.util.media.AMedia;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zul.Filedownload;
import org.zkoss.zul.Window;

import com.aspose.words.BreakType;
import com.aspose.words.Document;
import com.aspose.words.DocumentBuilder;
import com.aspose.words.ImportFormatMode;
import com.aspose.words.License;
import com.aspose.words.ParagraphAlignment;
import com.aspose.words.SaveFormat;
import com.pennant.app.util.PathUtil;
import com.pennant.document.generator.HandleHtmlMergeField;
import com.pennant.document.generator.Template;

public class  TemplateEngine {
	
	protected String templateSite;
	protected File templateDirectory;
	protected File documentDirectory;
	protected Template template;
	protected Document document;
	protected DocumentBuilder builder;
	protected String documentPath;
	private Document masterDocument;
	private static final Logger logger = Logger.getLogger(TemplateEngine.class);
	
	public TemplateEngine() throws Exception {
		loadLicence();
		String templatePath = getTemplatePath("");
		setDocumentSite(templatePath);
	}
	
	public TemplateEngine(String assetCode) throws Exception {
		logger.debug("Entering ");
		loadLicence();
		String templatePath = getTemplatePath(assetCode);
		setTemplateSite(templatePath);
		setDocumentSite(templatePath);
		logger.debug("Leaving");
	}
	
	public String getTemplatePath(String assetCode){
		
		String templatePath = "";
		if(StringUtils.isBlank(assetCode)){
			templatePath = PathUtil.getPath(PathUtil.FINANCE_AGREEMENTS);
		}else{
			templatePath = PathUtil.getPath(PathUtil.FINANCE_AGREEMENTS)+"/"+assetCode;
		}
		logger.debug("Template Path:"+templatePath);
		return templatePath;
	}

	public TemplateEngine(String templateSite, String documentSite) throws Exception {
		this();
		setTemplateSite(templateSite);
		setDocumentSite(documentSite);
	}

	public void loadTemplate() throws Exception {
		logger.debug("Entering ");
		document = new Document(template.getPath());
		builder = new DocumentBuilder(document);
		masterDocument = new Document();
		masterDocument.removeAllChildren();
		document.getMailMerge().setFieldMergingCallback(new HandleHtmlMergeField());
		logger.debug("Leaving");
	}

	public String saveDocument(String name, boolean overwrite) throws Exception {
		logger.debug("Entering ");
		File file = new File(documentDirectory, name);
		documentPath = file.getAbsolutePath();
		boolean save = true;

		if (file.exists()) {
			if (overwrite) {
				file.delete();
			} else {
				save = false;
			}
		}

		// Save the document
		if (save) {
			document.save(documentPath);
		}

		file = null;
		logger.debug("Leaving");
		return documentPath;
	}

	public String  saveDocument(String name) throws Exception {
		return saveDocument(name, false);
	}

	public void showDocument(Window window,String reportName , int format , boolean saved) throws Exception {
		logger.debug("Entering ");
		ByteArrayOutputStream stream = new ByteArrayOutputStream();

		if (saved) {
			InputStream inputStream = new FileInputStream(documentPath);
			int data;

			while ((data = inputStream.read()) >= 0) {
				stream.write(data);
			}

			inputStream.close();
			inputStream = null;
		} else {
			document.save(stream, format);
		}

		if((SaveFormat.DOCX) == format){
			Filedownload.save(new AMedia(reportName, "msword", "application/msword", stream.toByteArray()));
		}else{
			
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
	
	public byte[] getDocumentInByteArray(String reportName, int format) throws Exception{
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		document.save(stream, format);
  		stream.close();
 		return stream.toByteArray();
	}

	public void showDocument(Window window,String reportName, int format) throws Exception {
		showDocument(window,reportName, format, false);
	}

	public void mapField(String documentFieldName, String sourceFieldName) {
		document.getMailMerge().getMappedDataFields().add(documentFieldName, sourceFieldName);
	}

	public void mapFields(Map<String, String> map) {
		if (null == map) {
			return;
		}

		for (Map.Entry<String, String> entry : map.entrySet()) {
			String key = entry.getKey();
			String value = entry.getValue();

			mapField(key, value);
		}
	}

	public void mergeFields(String[] fields, Object[] values) throws Exception {
		document.getMailMerge().execute(fields, values);
	}

	public void mergeFields(ArrayList<String> fields, ArrayList<Object> values)
	throws Exception {
		String[] fieldNames = new String[fields.size()];

		mergeFields(fields.toArray(fieldNames), values.toArray());
	}

	public void mergeFields(String[] fields, Object bean) throws Exception {
		Object[] values = new Object[fields.length];

		for (int i = 0; i < fields.length; i++) {
			values[i] = BeanUtils.getProperty(bean, fields[i]);
		}

		mergeFields(fields, values);
	}

	public void mergeFields(ArrayList<String> fields, Object bean)
	throws Exception {
		String[] fieldNames = new String[fields.size()];

		mergeFields(fields.toArray(fieldNames), bean);
	}

	public void mergeFields(Object bean) throws Exception {
		Method[] methods = bean.getClass().getDeclaredMethods();
		ArrayList<String> fields = new ArrayList<String>();
		ArrayList<Object> values = new ArrayList<Object>();

		for (Method property : methods) {
			if (property.getName().startsWith("get")) {
				String field = property.getName().substring(3);
				Object value;

				try {
					value = property.invoke(bean);
				} catch (Exception e) {
					continue;
				}

				if (value != null) {
					if (value instanceof Collection<?>) {
						DataCollection collection = null;
					
						collection = new DataCollection((Collection<?>) value,field);
						
						document.getMailMerge().setMergeDuplicateRegions(true);
						document.getMailMerge().executeWithRegions(collection);

						collection = null;
					} else if (value instanceof Map<?, ?>) {
						//
					} else {
						fields.add(field);
						values.add(value);
					}
				}
			}
		}

		mergeFields(fields, values);
	}

	public void close() {
		builder = null;
		document = null;
		template = null;
		documentDirectory = null;
		templateDirectory = null;
		masterDocument=null;
	}

	protected void loadLicence() throws Exception {
		
		License license = new License();
		InputStream stream = TemplateEngine.class.getResourceAsStream("/Aspose.Words.lic");
		license.setLicense(stream);

		stream.close();
		stream = null;
		license = null;
	}

	public void setTemplateSite(String templateSite) {
		templateDirectory = new File(templateSite);
		if (!templateDirectory.exists()) {
			throw new IllegalArgumentException("Template site does not exist.");
		}
	}

	public void setDocumentSite(String documentSite) {
		documentDirectory = new File(documentSite);
		if (!documentDirectory.exists()) {
			throw new IllegalArgumentException("Document site does not exist.");
		}
	}

	public Template getTemplate() {
		return template;
	}

	public void setTemplate(String name) {
		File file = new File(templateDirectory, name);

		if (file.exists()) {
			template = new Template(file);
		} else {
			throw new IllegalArgumentException("Template does not exist.");
		}

		file = null;
	}

	public Document getDocument() {
		return document;
	}

	public void setDocument(Document document) {
		this.document = document;
	}

	public DocumentBuilder getBuilder() {
		return builder;
	}

	public void setBuilder(DocumentBuilder builder) {
		this.builder = builder;
	}

	public String getDocumentPath() {
		return documentPath;
	}

	public void setDocumentPath(String documentPath) {
		this.documentPath = documentPath;
	}

	// TODO: Review
	public void addNewPage() throws Exception {
		builder.insertBreak(BreakType.SECTION_BREAK_NEW_PAGE);
	}

	public void addTable(ResultSet resultSet) throws Exception {
		ResultSetMetaData metaData = resultSet.getMetaData();
		int columnCount = metaData.getColumnCount();

		// Table header
		builder.getFont().setBold(true);
		builder.getFont().setSmallCaps(true);
		builder.getParagraphFormat().setAlignment(ParagraphAlignment.CENTER);

		for (int i = 1; i < columnCount + 1; i++) {
			builder.insertCell();

			builder.writeln(metaData.getColumnName(i));
		}

		builder.endRow();

		// Table data
		builder.getFont().setBold(false);
		builder.getFont().setSmallCaps(false);
		builder.getParagraphFormat().setAlignment(ParagraphAlignment.LEFT);

		while (resultSet.next()) {
			for (int i = 1; i < columnCount + 1; i++) {
				builder.insertCell();

				Object item = resultSet.getObject(metaData.getColumnName(i));
				String typeName = item.getClass().getSimpleName();

				if ("byte[]".equals(typeName)) {
					builder.insertImage((byte[]) item, 50, 50);
				} else if ("Timestamp".equals(typeName)) {
					builder.write(new SimpleDateFormat("MMMM d, yyyy")
					.format((Timestamp) item));
				} else {
					builder.write(item.toString());
				}
			}

			builder.endRow();
		}

		builder.endTable();
	}
	

	public void appendToMasterDocument() throws Exception {
		document.updateFields();
		masterDocument.appendDocument(document,
				ImportFormatMode.USE_DESTINATION_STYLES);
		document = new Document(template.getPath());
	}
	
	public Document getMasterDocument() {
		return masterDocument;
	}

	public void setMasterDocument(Document masterDocument) {
		this.masterDocument = masterDocument;
	}
}