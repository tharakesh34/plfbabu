/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. 
 * All components/modules/functions/classes/logic in this software, unless 
 * otherwise stated, the property of Pennant Technologies. 
 * 
 * Copyright and other intellectual property laws protect these materials. 
 * Reproduction or retransmission of the materials, in whole or in part, in any manner, 
 * without the prior written consent of the copyright holder, is a violation of 
 * copyright law.
 */

/**
 ********************************************************************************************
 *                                 FILE HEADER                                              *
 ********************************************************************************************
 *																							*
 * FileName    		:  PaymentHeaderListCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  27-05-2017    														*
 *                                                                  						*
 * Modified Date    :  27-05-2017    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 27-05-2017       PENNANT	                 0.1                                            * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 ********************************************************************************************
*/

package com.pennant.webui.externalupload;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.zkoss.util.media.Media;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.UploadEvent;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Filedownload;
import org.zkoss.zul.Row;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.util.PennantConstants;
import com.pennant.batchupload.fileprocessor.BatchUploadProcessor;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.MessageUtil;
import com.pennanttech.pennapps.core.resource.Literal;


/**
 * This is the controller class for the
 * /WEB-INF/pages/com.pennant.payment/PaymentHeader/PaymentHeaderList.zul file.
 * 
 */
public class ExternalUploadListCtrl extends GFCBaseListCtrl<Object> {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(ExternalUploadListCtrl.class);

	protected Window window_ExternalUploadsList;
	protected Button btnImport;
	protected Row row1;
	protected Textbox fileName;
	protected Button btnFileUpload;
	protected Combobox apiType;

	private final String uploadLoaction = "/opt/external";
	private File file;
	private List<Map<String, String>> allApiDetails;
	@Value("${api.authkey}")
	private String authorization;
	private String extraHeader = null;

	/**
	 * default constructor.<br>
	 */
	public ExternalUploadListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
	}

	/**
	 * The framework calls this event handler when an application requests that
	 * the window to be created.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onCreate$window_ExternalUploadsList(Event event) {
		setPageComponents(window_ExternalUploadsList);
		List<ValueLabel> list = new ArrayList<ValueLabel>();

		allApiDetails = getDropDownList();
		String flag = "";
		for (Map<String, String> currentData : allApiDetails) {
			if (!flag.equals(currentData.get("apiModuleName")) && currentData.get("apiUrl")
					.substring(currentData.get("apiUrl").lastIndexOf("/") + 1).contains("create")) {
				list.add(new ValueLabel(currentData.get("apiModuleName"), currentData.get("apiModuleName")));
				flag = currentData.get("apiModuleName");
			}

		}
		fillComboBox(apiType, "", list, "");
	}

	public void onClick$btnImport(Event event) throws InterruptedException {
		try {
			if (file == null) {
				MessageUtil.showError("Please select the file to upload.");
				return;
			}

			doFileProcess();

			MessageUtil.showError("File is processed successfully.");

			downloadResponseFile();
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
			if (e.getLocalizedMessage() != null) {
				MessageUtil.showError(e.getLocalizedMessage());
			} else {
				MessageUtil.showError("Something Went Wrong Please Try Again Later.");
			}
			return;
		} finally {
			file = null;
			fileName.setText("");
		}
	}

	private void downloadResponseFile() {
		ByteArrayOutputStream stream = null;
		InputStream inputStream = null;
		try {
			stream = new ByteArrayOutputStream();
			inputStream = new FileInputStream(file.getPath());
			int data;
			while ((data = inputStream.read()) >= 0) {
				stream.write(data);
			}
			inputStream.close();
			inputStream = null;
			Filedownload.save(stream.toByteArray(), "text/plain", FilenameUtils.removeExtension(file.getName())
					+ "_Response." + FilenameUtils.getExtension(file.getName()));
			stream.close();
			stream = null;
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
			MessageUtil.showError("Something Went Wrong Please Contact To System Administrator.");
		} finally {

			try {
				if (stream != null) {
					stream.close();
					stream = null;
				}
				if (inputStream != null) {
					inputStream.close();
					inputStream = null;
				}
			} catch (IOException e) {
				logger.error(Literal.EXCEPTION, e);
			}
		}
	}

	/**
	 * when the Source type is changed. <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	private String apiUrl = "";

	public void onChange$apiType(Event event) throws Exception {
		logger.debug(Literal.ENTERING);
		try {
			apiUrl = getUrlByModuleName(this.apiType.getSelectedItem().getValue());
			extraHeader = this.apiType.getSelectedItem().getValue() + "Id";
			logger.info(apiUrl);
			fileName.setValue("");
			file = null;

			if (!StringUtils.equals("#", apiUrl)) {
				row1.setVisible(true);
			} else {
				this.btnImport.setDisabled(true);
				row1.setVisible(false);
				return;
			}

		} catch (Exception e) {
			row1.setVisible(true);
			apiType.setValue(PennantConstants.List_Select);
			apiType.setSelectedIndex(0);
			this.btnImport.setDisabled(true);
			MessageUtil.showError(e);
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * @param event
	 * @throws Exception
	 */
	public void onUpload$btnFileUpload(UploadEvent event) throws Exception {
		fileName.setText("");
		Media media = event.getMedia();

		if (!(StringUtils.endsWith(media.getName().toLowerCase(), ".xls")
				|| StringUtils.endsWith(media.getName().toLowerCase(), ".xlsx"))) {
			MessageUtil.showError("The uploaded file could not be recognized. Please upload a valid excel file.");
			this.btnImport.setDisabled(true);
			media = null;
			return;
		}

		this.btnImport.setDisabled(false);
		fileName.setText(media.getName());

		writeFile(media);
	}

	private void writeFile(Media media) throws IOException {
		File parent = new File(uploadLoaction);

		if (!parent.exists()) {
			parent.mkdirs();
		}
		file = new File(parent.getPath().concat(File.separator).concat(media.getName()));
		if (file.exists()) {
			file.delete();
		}
		file.createNewFile();
		FileUtils.writeByteArrayToFile(file, media.getByteData());
	}

	private void doFileProcess() throws Exception {
		// String envUri = "http://bfl2.pennanttech.com/pff-api-demo/services";
		if (!"".equals(apiUrl)) {
			BatchUploadProcessor batchProcessor = new BatchUploadProcessor(file, authorization, apiUrl, extraHeader);
			batchProcessor.process();
			fileName.setValue("");
		}
	}

	public String getUrlByModuleName(String moduleName) {
		for (Map<String, String> m : allApiDetails) {
			String mapValue = m.get("apiModuleName");
			if (mapValue.equals(moduleName)
					&& m.get("apiUrl").substring(m.get("apiUrl").lastIndexOf("/") + 1).contains("create")) {
				return m.get("apiUrl");
			}
		}
		return "#";
	}

	public List<Map<String, String>> getDropDownList() {
		RestTemplate restTemplate = new RestTemplate();
		String baseurl = SysParamUtil.getValueAsString("PFFAPI_SERVICE_URL");
		//String baseurl = "http://192.168.1.160:8080/pff-api/services";
		ResponseEntity<String> response = null;

		if (StringUtils.isBlank(baseurl)) {
			MessageUtil.showError("Could not find configuration in system parameters.");
			return new ArrayList<>();
		}

		try {
			response = restTemplate.getForEntity(baseurl, String.class);
		} catch (ResourceAccessException rae) {
			MessageUtil.showError("Please check, pff-api service is not available.");
			return new ArrayList<>();
		} catch (HttpClientErrorException hcee) {
			MessageUtil.showError("Please check, pff-api service is not available.");
			return new ArrayList<>();
		}
		org.jsoup.nodes.Document doc = Jsoup.parse(response.getBody());
		org.jsoup.select.Elements table = doc.select("table");
		org.jsoup.select.Elements value = table.get(0).getElementsByClass("value");
		List<Map<String, String>> allApiDetails = new ArrayList<Map<String, String>>();
		for (int i = 0, l = value.size(); i < l; i++) {
			String restAPiUrl = value.get(i).text() + "?_wadl";

			restTemplate = new RestTemplate();
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Arrays.asList(org.springframework.http.MediaType.APPLICATION_XML));

			HttpEntity<String> entity = new HttpEntity<String>(headers);

			response = restTemplate.exchange(restAPiUrl, HttpMethod.GET, entity, String.class);

			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder;
			try {

				builder = factory.newDocumentBuilder();
				Document document = builder.parse(new InputSource(new java.io.StringReader(response.getBody())));

				NodeList nodeList = document.getElementsByTagName("*");

				String base = "";
				String path = "";
				String methodType = "";
				Map<String, String> currentApiDetail = new ConcurrentHashMap<String, String>();

				for (int k = 0; k < nodeList.getLength(); k++) {

					Element element = (Element) nodeList.item(k);

					NamedNodeMap baseElmnt_gold_attr = element.getAttributes();

					for (int j = 0; j < baseElmnt_gold_attr.getLength(); ++j) {
						Node attr = baseElmnt_gold_attr.item(j);

						if (attr.getNodeName().equals("base") && attr.getNodeValue() != null
								&& element.getNodeName().equals("resources")) {
							base = attr.getNodeValue();
						}

						if (attr.getNodeName().equals("path") && "resource".equals(element.getNodeName())) {
							if (!attr.getNodeValue().equals("/")) {
								path = "/" + attr.getNodeValue();
							}
						}

						if (attr.getNodeName().equals("name") && "method".equals(element.getNodeName())) {
							methodType = attr.getNodeValue();
						}
						if (!"".equals(path) && !"".equals(methodType)) {
							String apiModuleName = base.trim();
							if (apiModuleName.endsWith("Rest")) {
								apiModuleName = apiModuleName.substring(apiModuleName.lastIndexOf("/") + 1)
										.replace("Rest", "");
								apiModuleName = apiModuleName.substring(0, 1).toUpperCase()
										+ apiModuleName.substring(1);
							} else {
								apiModuleName = apiModuleName.substring(apiModuleName.lastIndexOf("/") + 1);
							}

							currentApiDetail.put("apiUrl", base + path);
							currentApiDetail.put("methodType", methodType);
							currentApiDetail.put("apiModuleName", apiModuleName);

							allApiDetails.add(currentApiDetail);
							path = "";
							methodType = "";
							apiModuleName = "";
							currentApiDetail = new ConcurrentHashMap<String, String>();
						}
					}
				}
			} catch (Exception e) {
				logger.error(Literal.EXCEPTION, e);
			}
		}
		return allApiDetails;
	}
}