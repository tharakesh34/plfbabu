package com.pennant.pff.interfaces.util;

import java.io.StringReader;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.xpath.AXIOMXPath;
import org.apache.commons.lang.StringUtils;
import org.jaxen.JaxenException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.pennant.exception.PFFEngineException;
import com.pennant.interfaces.model.Header;

public class XmlUtil {
	public static Document parse(String xml) {
		try {
			DocumentBuilder builder = DocumentBuilderFactory.newInstance()
					.newDocumentBuilder();
			InputSource source = new InputSource(new StringReader(xml));

			return builder.parse(source);
		} catch (Throwable throwable) {
			throwable.getMessage();
		}

		return null;
	}

	public static Header retrieveHeader(OMElement element)
			throws JaxenException {
		Header vo = new Header();

		try {
			// Header Request
			AXIOMXPath xpath = new AXIOMXPath("/HB_EAI_REQUEST/HB_EAI_HEADER");
			OMElement header = (OMElement) xpath.selectSingleNode(element);

			vo.setMsgFormat((header.getFirstChildWithName(new QName("MsgFormat")).getText()));
			vo.setMsgVersion((header.getFirstChildWithName(new QName("MsgVersion")).getText()));
			vo.setRequestorId((header.getFirstChildWithName(new QName("RequestorId")).getText()));
			vo.setRequestorChannelId((header.getFirstChildWithName(new QName("RequestorChannelId")).getText()));
			vo.setRequestorUserId((header.getFirstChildWithName(new QName("RequestorUserId")).getText()));
			vo.setRequestorLanguage((header.getFirstChildWithName(new QName("RequestorLanguage")).getText()));
			vo.setRequestorSecurityInfo((header.getFirstChildWithName(new QName("RequestorSecurityInfo")).getText()));
			vo.setEaiReference(header.getFirstChildWithName(new QName("EaiReference")).getText());
			vo.setReturnCode((header.getFirstChildWithName(new QName("ReturnCode")).getText()));
			/*vo.setReturnDesc((header.getFirstChildWithName(new QName("ReturnDesc")).getText()));
			vo.setTimeStamp((header.getFirstChildWithName(new QName("TimeStamp")).getText()));*/
		} catch (JaxenException e) {
			throw e;
		}

		return vo;
	}

	public static String getValue(Node node) {
		String nodeValue = null;
		Node child = node.getFirstChild();

		if (child != null) {
			nodeValue = child.getNodeValue();
		}

		return nodeValue;
	}

	public static String getValueByTagName(Element element, String tagName) {
		NodeList nodeList = element.getElementsByTagName(tagName);

		if (nodeList == null || nodeList.getLength() <= 0) {
			return null;
		}

		return getValue(nodeList.item(0));
	}

	public static boolean validItem(String val, String list) {
		for (String item : list.split(",")) {
			if (val.equalsIgnoreCase(item)) {
				return true;
			}
		}

		return false;
	}

	public static String getMessageFormat(String message) {
		String value = "";

		try {
			Document document = parse(message);

			if (document != null) {
				Element element = document.getDocumentElement();

				value = XmlUtil.getValueByTagName(element, "MsgFormat");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return value;
	}
	
	public static OMNode getResponseStatus(OMFactory factory,OMElement responseStatus,String returnCode,String returnText) throws ParseException {

		setOMChildElement(factory, responseStatus, "ReturnCode",returnCode);
		setOMChildElement(factory, responseStatus,"ReturnText", returnText);
		//setOMChildElement(factory, responseStatus,"TimeStamp", DDSXmlUtil.getDateFormat(new Date(System.currentTimeMillis())));
		return responseStatus;
	}
	
	public static OMElement setOMChildElement(OMFactory factory,OMElement omElement,String tagName, Object value){

		OMElement omeEle= factory.createOMElement(tagName, null);
		omElement.addChild(omeEle);
		if(value!=null){
			omeEle.addChild(factory.createOMText(omeEle, value.toString()));
		}
		return omeEle;
	}
	
	public static OMElement addSubElement(OMFactory factory,OMElement omElement,String tagName){
		OMElement omeEle= factory.createOMElement(tagName, null);
		omeEle.addChild(factory.createOMText(omeEle, ""));
		omElement.addChild(omeEle);
		return omElement;
	}
	
	/*
	 * Prepare the request message
	 * 
	 */
	
	public static OMElement generateRequestElement(OMElement requestData) throws Exception {
		String bodyPath= "/HB_EAI_REQUEST/";
		return XmlUtil.getOMElement(bodyPath+"HB_EAI_HEADER", requestData);
	}
	/*
	 * Prepare the response message
	 * 
	 */
	public static OMElement generateReturnElement(Header headerVo,OMFactory factory, OMElement requestBody,OMElement responseBody)  throws Exception  {

		OMElement	returnElement	=	null;
		returnElement = factory.createOMElement("HB_EAI_REPLY",null);

		// Add the request message to the response
		try {
			// Generate Header
			returnElement.addChild(generateHeader(headerVo,factory));
			//returnElement.addChild(generateRequestElement(requestBody));
			returnElement.addChild(responseBody);

		} catch (JaxenException e) {
			throw e;
		}
		//Return mainResponse
		return returnElement;
	}
	
	public static OMElement getOMElement(String nodePath,OMElement requestData) throws Exception{

		AXIOMXPath xpath = new AXIOMXPath(nodePath);

		return (OMElement) xpath.selectSingleNode(requestData);
	}
	
	/*
	 * Generate header as per the standard XML request
	 * 
	 */

	public static OMElement generateHeader(Header headerVo, OMFactory factory) throws JaxenException  {

		// Create header response tag as per XML
		OMElement headerRes=factory.createOMElement("HB_EAI_HEADER",null);
		setOMChildElement(factory, headerRes, "MsgFormat", headerVo.getMsgFormat());
		setOMChildElement(factory, headerRes, "MsgVersion", headerVo.getMsgVersion());
		setOMChildElement(factory, headerRes, "RequestorId", headerVo.getRequestorId());
		setOMChildElement(factory, headerRes, "RequestorChannelId", headerVo.getRequestorChannelId());
		setOMChildElement(factory, headerRes, "RequestorUserId", headerVo.getRequestorUserId());
		setOMChildElement(factory, headerRes, "RequestorLanguage", headerVo.getRequestorLanguage());
		setOMChildElement(factory, headerRes, "RequestorSecurityInfo", headerVo.getRequestorSecurityInfo());
		setOMChildElement(factory, headerRes, "EaiReference", headerVo.getEaiReference());
		setOMChildElement(factory, headerRes, "ReturnCode", headerVo.getReturnCode());
/*		setOMChildElement(factory, headerRes, "MessageId", headerVo.getMessageId());
		setOMChildElement(factory, headerRes, "TimeStamp", headerVo.getTimeStamp());
*/		return headerRes;
	}
	
	/*
	 *  @ OMElement element
	 *  @ boolean isMandatory  
	 *  @ boolean isNotEmpty
	 *  @ String elementTagName
	 *  @String elementXPath 
	 *  If isNotEmpty is false then it will not check isNotEmpty conditions  
	 */
	public static String getStringValue(OMElement requestElement,boolean isMandatory,boolean isNotEmpty,String elementTagName,String nodePath) throws Exception  {
		String tagValue="";
		OMElement element = XmlUtil.getOMElement(nodePath+elementTagName, requestElement);

		if(element==null){
			if(isMandatory){
				throw new PFFEngineException(FinanceConstants.ATTRIBUTE_NOTFOUND, elementTagName+" is Mandatory in the request");
			}else{
				return tagValue;
			}
		}else{
			tagValue = element.getText();
		}

		if(isNotEmpty && StringUtils.trimToEmpty(tagValue).equals("")){
			throw new PFFEngineException(FinanceConstants.ATTRIBUTE_BLANK, elementTagName+" could not be Blank");
		}else{
			return StringUtils.trimToEmpty(tagValue);
		}
	}
	
	public static BigDecimal getFormattedDecimalValue(BigDecimal amount, int dec){
		if(amount == null){
			return BigDecimal.ZERO;
		}
		return amount.divide(new BigDecimal(Math.pow(10, dec))).setScale(2);
	}
	
	public static String formatDate(Date date) throws ParseException {
		if(date == null){
			return "";	
		}
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		return formatter.format(date);
	}
	
	/*
	 * Get String value from OMElement
	 * 
	 *  @ OMElement element
	 *  @ boolean isMandatory  
	 *  @ boolean isNotEmpty
	 *  @ String elementTagName
	 *  @String elementXPath 
	 *  If isNotEmpty is false then it will not check isNotEmpty conditions  
	 */
	public static String getStringValue(OMElement element,String elementTagName){

		if(element == null) {
			return null;
		}
		OMElement dataElement= element.getFirstChildWithName(new QName(elementTagName));

		if(dataElement==null){
			return null;
		}
		return StringUtils.trimToEmpty(dataElement.getText());
	}
	
	public static BigDecimal  getBigDecimalValue(OMElement element,String elementTagName){

		String tagValue = getStringValue(element, elementTagName);

		if(StringUtils.trimToNull(tagValue)==null){
			tagValue="0.00";
		}
		return new BigDecimal(tagValue);
	}
	
	/**
	 * Unformat Mobile Number if contains "-"
	 * 
	 * @param phoneNumber
	 * @return String
	 */
	public static String unFormatPhoneNumber(String phoneNumber) {
		String[] phoneNum = null;
		StringBuffer strMobileNum = new StringBuffer();
		if (!StringUtils.isBlank(phoneNumber) && phoneNumber.contains("-")) {
			phoneNum = phoneNumber.split("-");
			for(String str: phoneNum) {
				strMobileNum.append(str);
			}
		}
		return strMobileNum.toString();
	}
	
	/**
	 * Method for convert given MQ date into required date format
	 * 
	 * @param mqDate
	 * @param format
	 * @return
	 */
	public static java.util.Date convertDateFromMQ(String mqDate, String format) {

		if (mqDate == null) {
			return null;
		}
		try {
			SimpleDateFormat formatter = new SimpleDateFormat(format);
			String formatDate = formatter.format(new SimpleDateFormat("yyyyMMdd").parse(mqDate));
			return formatter.parse(formatDate);
		} catch (ParseException e) {
			
		}

		return null;
	}
}
