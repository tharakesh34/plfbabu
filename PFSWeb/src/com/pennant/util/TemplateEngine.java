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
import org.zkoss.util.media.AMedia;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zul.Filedownload;
import org.zkoss.zul.Window;

import com.aspose.words.BreakType;
import com.aspose.words.Document;
import com.aspose.words.DocumentBuilder;
import com.aspose.words.Font;
import com.aspose.words.INodeChangingCallback;
import com.aspose.words.License;
import com.aspose.words.NodeChangingArgs;
import com.aspose.words.NodeType;
import com.aspose.words.ParagraphAlignment;
import com.aspose.words.Run;
import com.aspose.words.SaveFormat;
import com.pennant.app.util.SystemParameterDetails;
import com.pennant.backend.util.PennantConstants;
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
	
	public TemplateEngine() throws Exception {
		loadLicence();
		
		String templatePath = "";
		if(PennantConstants.server_OperatingSystem.equals("WINDOWS")){
			templatePath=SystemParameterDetails.getSystemParameterValue("FINANCE_AGREEMENTS_PATH").toString();
		}else{
			templatePath = SystemParameterDetails.getSystemParameterValue("LINUX_AGGREMENTS_TEMPLATES_PATH").toString();
		}
		setDocumentSite(templatePath);
	}
	
	public TemplateEngine(String assetCode) throws Exception {
		
		loadLicence();

		String templatePath = "";
		if(PennantConstants.server_OperatingSystem.equals("WINDOWS")){
			templatePath=SystemParameterDetails.getSystemParameterValue("FINANCE_AGREEMENTS_PATH").toString()+"/"+assetCode;
		}else{
			templatePath = SystemParameterDetails.getSystemParameterValue("LINUX_AGGREMENTS_TEMPLATES_PATH").toString();
		}
		setTemplateSite(templatePath);
		setDocumentSite(templatePath);
		
	}

	public TemplateEngine(String templateSite, String documentSite) throws Exception {
		this();
		setTemplateSite(templateSite);
		setDocumentSite(documentSite);
	}

	public void loadTemplate() throws Exception {
		document = new Document(template.getPath());
		builder = new DocumentBuilder(document);
		document.setNodeChangingCallback(new HandleNodeChanging_FontChanger());
		document.getMailMerge().setFieldMergingCallback(new HandleHtmlMergeField());
	}
	public void loadTemplateWithFontSize(int fontSize) throws Exception {
		document = new Document(template.getPath());
		builder = new DocumentBuilder(document);
		HandleNodeChanging_FontChanger changer=new HandleNodeChanging_FontChanger();
		changer.setFontsize(fontSize);
		document.setNodeChangingCallback(changer);
		document.getMailMerge().setFieldMergingCallback(new HandleHtmlMergeField());
	}

	public String saveDocument(String name, boolean overwrite) throws Exception {
		File file = new File(documentDirectory, name);
		documentPath = file.getAbsolutePath();
		boolean save = true;

		if (file.exists()) {
			if (overwrite) {
				file.delete();
			} else {
				save = false;
				// throw new IOException("Document already exists.");
			}
		}

		// Save the document
		if (save) {
			document.save(documentPath);
		}

		file = null;
		return documentPath;
	}

	public String  saveDocument(String name) throws Exception {
		return saveDocument(name, false);
	}

	public void showDocument(Window window,String reportName , int format , boolean saved) throws Exception {
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

			Executions.createComponents("/WEB-INF/pages/Reports/reports.zul", window, arg);
		}
		stream.close();
		stream = null;
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

				if (typeName.equals("byte[]")) {
					builder.insertImage((byte[]) item, 50, 50);
				} else if (typeName.equals("Timestamp")) {
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
	
	public class HandleNodeChanging_FontChanger implements INodeChangingCallback{
		
		private int fontsize=8;

		public int getFontsize() {
			return fontsize;
		}

		public void setFontsize(int fontsize) {
			this.fontsize = fontsize;
		}

		@Override
		public void nodeInserted(NodeChangingArgs arg0) throws Exception {
			if (arg0.getNode().getNodeType() == NodeType.RUN) {
				Font font = ((Run) arg0.getNode()).getFont();
				font.setSize(fontsize);
				font.setName("Trebuchet MS");
			}
		}

		@Override
		public void nodeInserting(NodeChangingArgs arg0) throws Exception {
			
			
		}

		@Override
		public void nodeRemoved(NodeChangingArgs arg0) throws Exception {
		
			
		}

		@Override
		public void nodeRemoving(NodeChangingArgs arg0) throws Exception {
		
			
		}
		
	}
	
	
}