package com.pennant.mq.util;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.xml.namespace.QName;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.xpath.AXIOMXPath;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jaxen.JaxenException;

import com.pennant.mq.model.AHBMQHeader;
import com.pennanttech.pennapps.core.InterfaceException;

public class PFFXmlUtil {
	private static final Logger logger = Logger.getLogger(PFFXmlUtil.class);
	
	private static int curdate=0;
	private static int seqNumber=0;
	public static final String SUCCESS="0000";
	public static final String DEDUP_NOTFOUND="0000";
	public static final String DEDUP_FOUND="3952";
	public static final String CUST_CIF_EXISTS="3924";
	public static final String NOGO="NOGO";
	

	public static String getReferenceNumber() {

		Calendar calendar = Calendar.getInstance();
		String curTime = "";
		if (seqNumber == 0) {
			curdate = calendar.get(Calendar.DAY_OF_YEAR);
			curTime = getStringPadValue(calendar.get(Calendar.HOUR_OF_DAY), 2)
					+ getStringPadValue(calendar.get(Calendar.MINUTE), 2)
					+ getStringPadValue(calendar.get(Calendar.SECOND), 2)
					+ getStringPadValue(calendar.get(Calendar.MILLISECOND), 3);
			seqNumber = Integer.parseInt(curTime);
		} else {
			if (curdate != calendar.get(Calendar.DAY_OF_YEAR)) {
				curdate = calendar.get(Calendar.DAY_OF_YEAR);
				seqNumber = 1;
			} else {
				seqNumber++;
			}
		}
		return StringUtils.leftPad(String.valueOf(curdate), 3, "0")
				+ StringUtils.leftPad(String.valueOf(seqNumber), 9, "0");
	}

	private static String getStringPadValue(int intvalue, int size) {

		return StringUtils.leftPad(String.valueOf(intvalue), size, "0");
	}

	/**
	 * Generate request Element
	 * @param header
	 * @param factory
	 * @param requesteBody
	 * @return
	 * @throws InterfaceException
	 */
	public static OMElement generateRequest(AHBMQHeader header,	OMFactory factory, OMElement requesteBody)
			throws InterfaceException {

		OMElement requestElement = factory.createOMElement("HB_EAI_REQUEST", null);
		requestElement.addChild(generateHeader(header, factory));
		requestElement.addChild(requesteBody);

		return requestElement;
	}

	/*
	 * Generate header as per the standard XML request
	 */
	private static OMElement generateHeader(AHBMQHeader header, OMFactory factory){

		// Create header response tag as per XML
		OMElement headerRequest = null; 
		headerRequest = factory.createOMElement("HB_EAI_HEADER",null);
		PFFXmlUtil.setOMChildElement(factory, headerRequest, "MsgFormat",header.getMsgFormat());
		PFFXmlUtil.setOMChildElement(factory, headerRequest, "MsgVersion",header.getMsgVersion());
		PFFXmlUtil.setOMChildElement(factory, headerRequest, "RequestorId",header.getRequestorId());
		PFFXmlUtil.setOMChildElement(factory, headerRequest, "RequestorChannelId",header.getRequestorChannelId());
		PFFXmlUtil.setOMChildElement(factory, headerRequest, "RequestorUserId",header.getRequestorUserId());
		PFFXmlUtil.setOMChildElement(factory, headerRequest, "RequestorLanguage",header.getRequestorLanguage());
		PFFXmlUtil.setOMChildElement(factory, headerRequest, "RequestorSecurityInfo",header.getRequestorSecurityInfo());
		PFFXmlUtil.setOMChildElement(factory, headerRequest, "EaiReference",header.getEaiReference());
		PFFXmlUtil.setOMChildElement(factory, headerRequest, "ReturnCode",header.getReturnCode());
		return headerRequest;
	}

	/**
	 *  Generate  header Object from Response Element
	 *  
	 * @param responseElement
	 * @param header
	 * @return
	 * @throws InterfaceException
	 */
	public static AHBMQHeader parseHeader(OMElement responseElement, AHBMQHeader header) throws InterfaceException{
		OMElement headerElement = getOMElement("/HB_EAI_REPLY/HB_EAI_HEADER", responseElement);
		
		if (headerElement.getFirstChildWithName(new QName("MsgFormat")).getText() != null) {
			header.setMsgFormat(headerElement.getFirstChildWithName(new QName("MsgFormat")).getText());
		}
		if (headerElement.getFirstChildWithName(new QName("MsgVersion")).getText() != null) {
			header.setMsgVersion(headerElement.getFirstChildWithName(new QName("MsgVersion")).getText());
		}
		if (headerElement.getFirstChildWithName(new QName("RequestorId")).getText() != null) {
			header.setRequestorId(headerElement.getFirstChildWithName(new QName("RequestorId")).getText());
		}
		if (headerElement.getFirstChildWithName(new QName("RequestorChannelId")).getText() != null) {
			header.setRequestorChannelId(headerElement.getFirstChildWithName(new QName("RequestorChannelId")).getText());
		}
		if (headerElement.getFirstChildWithName(new QName("RequestorUserId")).getText() != null) {
			header.setRequestorUserId(headerElement.getFirstChildWithName(new QName("RequestorUserId")).getText());
		}
		if (headerElement.getFirstChildWithName(new QName("RequestorLanguage")).getText() != null) {
			header.setRequestorLanguage(headerElement.getFirstChildWithName(new QName("RequestorLanguage")).getText());
		}
		if (headerElement.getFirstChildWithName(new QName("RequestorSecurityInfo")).getText() != null) {
			header.setRequestorSecurityInfo(headerElement.getFirstChildWithName(new QName("RequestorSecurityInfo")).getText());
		}
		if (headerElement.getFirstChildWithName(new QName("EaiReference")).getText() != null) {
			header.setEaiReference(headerElement.getFirstChildWithName(new QName("EaiReference")).getText());
		}
		header.setReturnCode(headerElement.getFirstChildWithName(new QName("ReturnCode")).getText());
		return header;
	}

	public static OMElement setOMChildElement(OMFactory factory,OMElement omElement,String tagName, Object value){

		OMElement omeEle= factory.createOMElement(tagName, null);
		omElement.addChild(omeEle);
		if(value!=null){
			omeEle.addChild(factory.createOMText(omeEle, value.toString()));
		}
		return omeEle;
	}

	public static OMElement getOMElement(String nodePath,OMElement requestData) throws InterfaceException{

		AXIOMXPath xpath;
		OMElement elementData=null;

		try {
			xpath = new AXIOMXPath(nodePath);
			elementData = (OMElement) xpath.selectSingleNode(requestData);
		} catch (JaxenException e) {
			logger.debug("Exception: ", e);
			throw new InterfaceException("PTI9999", e.getMessage());
		}

		return elementData;
	}

	public static OMElement getOMElements(String nodePath,OMElement requestData) throws InterfaceException{

		AXIOMXPath xpath;
		OMElement elementData=null;
		try {
			xpath = new AXIOMXPath(nodePath);
			elementData = (OMElement) xpath.selectNodes(requestData);
		} catch (JaxenException e) {
			logger.debug("Exception: ", e);
			throw new InterfaceException("PTI9999", e.getMessage());
		}

		return elementData;
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
		OMElement element = PFFXmlUtil.getOMElement(nodePath+elementTagName, requestElement);

		if(element==null){
			if(isMandatory){
				throw new Exception(elementTagName+" is Mandatory in the request");
			}else{
				return tagValue;
			}
		}else{
			tagValue = element.getText();
		}

		if(isNotEmpty && StringUtils.isEmpty(tagValue)){
			throw new Exception(elementTagName+" could not be Blank");
		}else{
			return StringUtils.trimToEmpty(tagValue);
		}
	}


	/**
	 * Get BigDecimal Value from OMElement
	 * 
	 * @param requestElement
	 * @param isMandatory
	 * @param isNotZero
	 * @param elementTagName
	 * @param nodePath
	 * @return
	 * @throws Exception
	 */
	public static BigDecimal getBigDecimalValue(OMElement requestElement, boolean isMandatory,boolean isNotZero,String elementTagName, String nodePath) throws InterfaceException  {
		OMElement element = PFFXmlUtil.getOMElement(nodePath+elementTagName, requestElement);
		return getBigDecimalValue(element, isMandatory, isNotZero, elementTagName);
	}

	/**
	 * Get BigDecimal Value from OMElement
	 * 
	 * @param requestElement
	 * @param isMandatory
	 * @param isNotZero
	 * @param elementTagName
	 * @return
	 * @throws Exception
	 */
	public static BigDecimal getBigDecimalValue(OMElement requestElement, boolean isMandatory,boolean isNotZero,String elementTagName) throws InterfaceException  {
		String[] parmString =  new String[]{elementTagName};

		BigDecimal tagValue=BigDecimal.ZERO;
		if (requestElement == null) {
			if(isMandatory){
				throw new InterfaceException("PTI2002", parmString + " is Mandatory in the request");
			}else{
				return tagValue;
			}
		}else{
			try {
				tagValue = new BigDecimal(requestElement.getText());
			} catch (Exception e) {
				logger.debug("Exception: ", e);
				throw new InterfaceException("PTI2003", "Unable to parse " + parmString + " in the request");
			}
		}

		if(isNotZero && tagValue.doubleValue()==0){
			throw new InterfaceException("PTI2004", parmString + " should Not be Zero");
		}

		return tagValue;
	}


	/*
	 *  Get Date value from OMElement
	 *  @ OMElement element
	 *  @ boolean isMandatory  
	 *  @ boolean isNotEmpty
	 *  @ String elementTagName
	 *  @String nodePath 
	 *  If isNotEmpty is false then it will not check isNotEmpty conditions  
	 */
	public static Date getDateValue(OMElement requestElement, boolean isMandatory, boolean isNotEmpty, String elementTagName, String nodePath) throws InterfaceException {
		String[] parmString =  new String[]{elementTagName};
		Date tagValue= new java.util.Date();

		OMElement element = PFFXmlUtil.getOMElement(nodePath+elementTagName, requestElement);
		if(element == null) {
			if(isMandatory){
				throw new InterfaceException("PTI2002", parmString + " is Mandatory in the request");
			}else{
				return tagValue;
			}
		} else {
			try {
				tagValue = PFFXmlUtil.formatDate(element.getText());
			} catch (Exception e) {
				logger.debug("Exception: ", e);
				throw new InterfaceException("PTI2003", "Unable to parse " + parmString + " in the request");
			}
		}
		return tagValue;
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

	public static int getIntValue(OMElement element,String elementTagName){

		String tagValue = getStringValue(element, elementTagName);

		if(StringUtils.trimToNull(tagValue)==null){
			tagValue="0";
		}

		return Integer.parseInt(tagValue);
	}

	public static BigDecimal  getBigDecimalValue(OMElement element,String elementTagName){

		String tagValue = getStringValue(element, elementTagName);

		if(StringUtils.trimToNull(tagValue)==null){
			tagValue="0.00";
		}
		return new BigDecimal(tagValue);
	}


	private static Date formatDate(String dateInString) throws InterfaceException {
		SimpleDateFormat formatter = new SimpleDateFormat(InterfaceMasterConfigUtil.DBDateFormat);

		try {
			return  formatter.parse(dateInString);
		} catch (Exception e) {
			logger.debug("Exception: ", e);
			throw new InterfaceException("PTI2004", "Unable to parse " + dateInString + " Field");
		}
	}

	public static String getTodayDateTime(String formate){
		if(StringUtils.trimToNull(formate)==null){
			formate = InterfaceMasterConfigUtil.MQDATETIME_FORMAT; 
		}
		java.text.SimpleDateFormat df = new java.text.SimpleDateFormat(formate);
		Calendar objCalendar = Calendar.getInstance();	
		return df.format(objCalendar.getTime());
	}
	
	public static String getTodayDate(String formate){
		if(StringUtils.trimToNull(formate)==null){
			formate = InterfaceMasterConfigUtil.MQDATE_FORMAT; 
		}
		java.text.SimpleDateFormat df = new java.text.SimpleDateFormat(formate);
		Calendar objCalendar = Calendar.getInstance();	
		return df.format(objCalendar.getTime());
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
		return String.valueOf(strMobileNum);
	}
}


