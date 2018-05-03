package com.pff.framework.util;

import java.io.StringReader;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

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
import org.xml.sax.InputSource;

import com.pff.vo.PFFMQHeaderVo;

public class PFFXmlUtil {
	/*
	 * Generate header as per the standard XML request
	 * 
	 */


	public static final String ESTPHONENO="ESTPHN";
	public static final String ESTFAXNO="ESTFAX";
	public static final String ESTMOBILENO="ESTMOB";
	public static final String ESTOTHERPHONENO="OTHERPHN";
	public static final String ESTOTHERFAXNO="OTHERFAX";
	public static final String ESTOTHERMOBILENO="OTHERMOB";
	public static final String SMSMOBILENO="SMSMOB";
	public static final String OFFICEPHONENO="OFFICE";
	public static final String OFFICEFAXNO="FAX";
	public static final String OFFICEMOBILENO="OFF_MOB";
	public static final String RESIDENCEPHONENO="HOMEPHN";
	public static final String RESIDENCEFAXNO="HOMEFAX";
	public static final String RESIDENCEMOBILENO="MOBILE";
	public static final String HCPHONENO="HC_PHN";
	public static final String HCFAXNO="HC_FAX";
	public static final String HCMOBILENO="HC_MOBIL";
	public static final String HCCONTACTNUMBE="HC_CONTACT";
	public static final String FAXINDEMITY="INDEMFAX";
	public static final String ESTEMAILADDRESS="ESTMAIL";
	public static final String ESTOTHEREMAILADDRESS="ESTOTHERMAIL";
	public static final String INDEMITYFAXNUMBER="INDEMFAX";
	public static final String EMAILINDEMITY="INDEMAIL";
	public static final String OFFICEEMAILADDRESS="OFFICE";
	public static final String HCMAILADDRESS="HC_MAIL";
	public static final String RESIDENCEEMAILADDRESS="PERSON1";
	public static final String HOME_RC="RESIDENCE";
	public static final String OFFICE="OFFICE";
	public static final String HOME_PC="RESIDENCE";


	public static final String EMIRATE_ID="EMIRATE";
	public static final String PASSPORT_ID="PASSPORT";
	public static final String RESIDENCE_VISA="RESVISA";
	public static final String TRADELICENSE="TRADELICENSE";

	public static OMElement generateHeader(PFFMQHeaderVo headerVo, OMFactory factory) throws JaxenException  {

		// Create header response tag as per XML
		OMElement headerRes=factory.createOMElement("HB_EAI_HEADER",null);
		setOMChildElement(factory, headerRes, "MsgFormat", headerVo.getMessageFormat());
		setOMChildElement(factory, headerRes, "MsgVersion", headerVo.getMessageVersion());
		setOMChildElement(factory, headerRes, "RequestorId", headerVo.getMessageRequestId());
		setOMChildElement(factory, headerRes, "RequestorChannelId", headerVo.getMessageRequestChId());
		setOMChildElement(factory, headerRes, "RequestorUserId", headerVo.getMessageRequestUserId());
		setOMChildElement(factory, headerRes, "RequestorLanguage", headerVo.getMessageRequestLanguage());
		setOMChildElement(factory, headerRes, "RequestorSecurityInfo", headerVo.getMessageSecurityInfo());
		setOMChildElement(factory, headerRes, "EaiReference", headerVo.getEaiReference());
		setOMChildElement(factory, headerRes, "ReturnCode", headerVo.getMessageReturnCode());

		return headerRes;
	}
	public static String getDateFormate(Date date) {
		if(date!=null){
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
			return formatter.format(date);
		}
		return null;
	}
	public static String getDateFormat(Date date) {
		if(date!=null){
			SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
			return formatter.format(date);
		}
		return null;
	}
	public static Date convertFromAS400(BigDecimal as400Date) throws ParseException{

		String pcDate = "";
		BigDecimal dateInt = null;

		if (as400Date != null){
			if  (new BigDecimal(0).compareTo(as400Date) !=0) {
				dateInt = new BigDecimal(19000000).add(as400Date);
				pcDate = dateInt.toString();
			}
			else if (as400Date.equals("9999999")) 	
			{
				pcDate = "";
			}	

		}

		if(!StringUtils.trimToEmpty(pcDate).equals("")){
			return getDate(pcDate);
		}

		return null;
	}

	public static Date getDate(String reqDate) throws ParseException {
		SimpleDateFormat formatter  = new SimpleDateFormat("yyyyMMdd");        
		Date date  = formatter.parse(reqDate);
		return date;

	}

	/*
	 * Retrieve header values
	 * 
	 */

	public static PFFMQHeaderVo retrieveHeader(OMElement requestData) {
		PFFMQHeaderVo headerVo	= new PFFMQHeaderVo();

		try {

			// Header Request
			AXIOMXPath xpath=new AXIOMXPath("/HB_EAI_REQUEST/HB_EAI_HEADER");
			OMElement header = (OMElement) xpath.selectSingleNode(requestData);

			if(header.getFirstChildWithName(new QName("MsgFormat"))!= null ){
				headerVo.setMessageFormat((header.getFirstChildWithName(new QName("MsgFormat")).getText()));
			}
			if(header.getFirstChildWithName(new QName("MsgVersion"))!= null ){
				headerVo.setMessageVersion((header.getFirstChildWithName(new QName("MsgVersion")).getText()));
			}
			if(header.getFirstChildWithName(new QName("RequestorId"))!= null ){
				headerVo.setMessageRequestId((header.getFirstChildWithName(new QName("RequestorId")).getText()));
			}
			if(header.getFirstChildWithName(new QName("RequestorChannelId"))!= null ){
				headerVo.setMessageRequestChId((header.getFirstChildWithName(new QName("RequestorChannelId")).getText()));
			}
			if(header.getFirstChildWithName(new QName("RequestorUserId"))!= null ){
				headerVo.setMessageRequestUserId((header.getFirstChildWithName(new QName("RequestorUserId")).getText()));
			}
			if(header.getFirstChildWithName(new QName("RequestorLanguage"))!=null){
				headerVo.setMessageRequestLanguage((header.getFirstChildWithName(new QName("RequestorLanguage")).getText()));	
			}else{
				headerVo.setMessageRequestLanguage("EN");
			}
			if(header.getFirstChildWithName(new QName("RequestorSecurityInfo"))!= null ){
				headerVo.setMessageSecurityInfo((header.getFirstChildWithName(new QName("RequestorSecurityInfo")).getText()));
			}
			if(header.getFirstChildWithName(new QName("EaiReference"))!= null ){
				headerVo.setEaiReference((header.getFirstChildWithName(new QName("EaiReference")).getText()));
			}
			if(header.getFirstChildWithName(new QName("ReturnCode"))!= null ){
				headerVo.setMessageReturnCode("");
			}
			xpath=new AXIOMXPath("/HB_EAI_REQUEST/Request/getCustomerDetailsRequest");

			header = (OMElement) xpath.selectSingleNode(requestData);		
			headerVo.setMessageReturnDesc("");
			headerVo.setMessageExtra1("");
			headerVo.setMessageExtra2("");


		} catch (JaxenException e) {			
			e.printStackTrace();
		}
		return headerVo;
	}

	/*
	 * Prepare the response message
	 * 
	 */
	public static OMElement generateReturnElement(PFFMQHeaderVo headerVo,OMFactory factory, OMElement responseBody)  throws JaxenException  {

		OMElement	returnElement	=	null;
		returnElement = factory.createOMElement("HB_EAI_REPLY",null);

		// Add the request message to the response
		try {
			// Generate Header
			returnElement.addChild(generateHeader(headerVo,factory));
			returnElement.addChild(responseBody);

		} catch (JaxenException e) {
			throw e;
		}
		//Return mainResponse
		return returnElement;
	}

	/**
	 * @param xmlSource
	 * 
	 * @return
	 *  
	 */
	public static Document parse(String xmlSource) {
		try {
			DocumentBuilder docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			InputSource inputSrc = new InputSource(new StringReader(xmlSource));
			return docBuilder.parse(inputSrc);
		} catch (Throwable throwable) {
			throwable.getMessage();
		}
		return null;
	}

	/**
	 * @param node
	 * 
	 * @return
	 *  
	 */
	/*	public static String getValue(Node node) {
				String nodeValue = null;
				Node child = node.getFirstChild();
				if (child != null){
					nodeValue = child.getNodeValue();            
				}
				return nodeValue;
			}
	 */
	/**
	 * @param e
	 * 
	 * @return
	 *  
	 */
	/*public static String getValue(Element element) {
				String nodeValue = null;
				Node cld = (Node) element.getFirstChild();
				if (cld != null){
					nodeValue=cld.getNodeValue();
				}        
				return nodeValue;
			}
	 */
	/**
	 * @param doc
	 * 
	 * @param tagName
	 * 
	 * @return
	 *  
	 */
	/*public static String getValueByTagName(Element element, String tagName) {
				NodeList nodeList = element.getElementsByTagName(tagName);
				if (nodeList == null || nodeList.getLength() <= 0){
					return null;
				}
				return getValue(nodeList.item(0));
			}*/

	/**
	 * @param node
	 * 
	 * @return
	 *  
	 */
	/*public static OMElement ConvertStringtoXml(String resultValue) throws Exception{

				ByteArrayInputStream xmlStream = new ByteArrayInputStream(resultValue.getBytes());
				XMLStreamReader parser;
				OMElement xmlEnvelope = null;
				try {
					parser = XMLInputFactory.newInstance().createXMLStreamReader(xmlStream);
					StAXOMBuilder builder = new StAXOMBuilder(parser);
					xmlEnvelope = builder.getDocumentElement();
					xmlEnvelope.build();
					xmlEnvelope.detach();

				} catch (Exception e) {
					throw e;
				}
				return xmlEnvelope;
			}
	 */
	/**
	 * This method for validating the prepaid amount
	 * @param prepaidAmt
	 * @return
	 */
	public static boolean isPrepaidValidAmount(BigDecimal prepaidAmt){
		boolean validAmount = false;
		String amount=String.valueOf(prepaidAmt).substring(String.valueOf(prepaidAmt).indexOf('.')+1);
		if(amount.equals("500") || amount.equals("000")){
			validAmount=true;
		}
		return validAmount;
	}

	public static OMElement getOMElement(String nodePath,OMElement requestData) throws Exception{

		AXIOMXPath xpath = new AXIOMXPath(nodePath);

		return (OMElement) xpath.selectSingleNode(requestData);
	}

	public static OMElement getOMElements(String nodePath,OMElement requestData) throws Exception{
		AXIOMXPath xpath1 = new AXIOMXPath(nodePath);

		return (OMElement) xpath1.selectNodes(requestData);
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

		if(isNotEmpty && StringUtils.trimToEmpty(tagValue).equals("")){
			throw new Exception(elementTagName+" could not be Blank");
		}else{
			return StringUtils.trimToEmpty(tagValue);
		}
	}
	/*
	 *  @ OMElement element
	 *  @ boolean isMandatory  
	 *  @ boolean isNotEmpty
	 *  @ String elementTagName
	 *  @String elementXPath 
	 *  If isNotEmpty is false then it will not check isNotEmpty conditions  
	 */

	public static int getIntValue(OMElement requestElement, boolean isMandatory, boolean isNotZero, String elementTagName,String nodePath,boolean flag,int count) throws Exception  {

		int tagValue = 0;
		OMElement element = PFFXmlUtil.getOMElements(nodePath + elementTagName, requestElement,flag,count);

		if (element == null) {
			if (isMandatory) {
				throw new Exception(elementTagName + " is Mandatory in the request");
			} else {
				return tagValue;
			}
		} else {
			try {
				tagValue = Integer.parseInt(element.getText());
			} catch (Exception e) {
				throw new Exception("Unable to parse " + elementTagName	+ " in the request");
			}
		}

		if(isNotZero && tagValue==0){
			throw new Exception(elementTagName+" should Not be Zero");
		}
		return tagValue;
	}

	/**
	 * 
	 * @param requestElement
	 * @param isMandatory
	 * @param isNotZero
	 * @param elementTagName
	 * @param nodePath
	 * @return
	 * @throws Exception
	 */
	public static BigDecimal getBigDecimalValue(OMElement requestElement, boolean isMandatory,boolean isNotZero,String elementTagName, String nodePath) throws Exception  {

		OMElement element = PFFXmlUtil.getOMElement(nodePath+elementTagName, requestElement);

		return getBigDecimalValue(element, isMandatory, isNotZero, elementTagName);
	}

	/**
	 * 
	 * @param requestElement
	 * @param isMandatory
	 * @param isNotZero
	 * @param elementTagName
	 * @return
	 * @throws Exception
	 */
	public static BigDecimal getBigDecimalValue(OMElement requestElement, boolean isMandatory,boolean isNotZero,String elementTagName) throws Exception  {

		BigDecimal tagValue=BigDecimal.ZERO;
		if (requestElement == null) {
			if(isMandatory){
				throw new Exception(elementTagName+" is Mandatory in the request");
			}else{
				return tagValue;
			}
		}else{
			try {
				tagValue = new BigDecimal(requestElement.getText());
			} catch (Exception e) {
				throw new Exception("Unable to parse "+ elementTagName+" in the request");
			}
		}

		if(isNotZero && tagValue.doubleValue()==0){
			throw new Exception(elementTagName+" should Not be Zero");
		}
		return tagValue;
	}


	/*
	 *  @ OMElement element
	 *  @ boolean isMandatory  
	 *  @ boolean isNotEmpty
	 *  @ String elementTagName
	 *  @String nodePath 
	 *  If isNotEmpty is false then it will not check isNotEmpty conditions  
	 */

	public static Date getDateValue(OMElement requestElement, boolean isMandatory, boolean isNotEmpty, String elementTagName, String nodePath) throws Exception {

		Date tagValue= new java.util.Date();

		OMElement element = PFFXmlUtil.getOMElement(nodePath+elementTagName, requestElement);
		if(element == null) {
			if(isMandatory){
				throw new Exception(elementTagName+" is Mandatory in the request");
			}else{
				return tagValue;
			}
		} else {
			try {
				tagValue = PFFXmlUtil.formatDate(element.getText());
			} catch (Exception e) {
				throw new Exception("Unable to parse "+ elementTagName+" in the request");
			}
		}
		return tagValue;
	}


	private static Date formatDate(String dateInString) throws ParseException {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		return formatter.parse(dateInString);
	}

	public static OMElement setOMChildElement(OMFactory factory,OMElement omElement,String tagName, Object value){

		OMElement omeEle= factory.createOMElement(tagName, null);
		omElement.addChild(omeEle);
		if(value!=null){
			omeEle.addChild(factory.createOMText(omeEle, value.toString()));
		}
		return omeEle;
	}


	public static OMElement setOMChildElements(OMFactory factory,OMElement omElement,String tagName, Object value){

		OMElement omeEle= factory.createOMElement(tagName, null);
		omElement.addChild(omeEle);
		if(value!=null){
			omeEle.addChild(factory.createOMText(omeEle, value.toString()));
		}
		return omElement;
	}

	public static OMElement setOMChildElement(OMFactory factory,OMElement omElement,String tagName, Object value,Object defailtValue){

		OMElement omeEle= factory.createOMElement(tagName, null);
		omElement.addChild(omeEle);
		if(value==null){
			omeEle.addChild(factory.createOMText(omeEle, defailtValue.toString()));
		}else{
			omeEle.addChild(factory.createOMText(omeEle, value.toString()));
		}
		return omElement;
	}

	public static OMElement setOMChildElement(OMFactory factory,OMElement omElement,String parentTagName,String chaildTagName, Object value){
		OMElement omeEle= factory.createOMElement(parentTagName, null);
		omeEle.addChild(setOMChildElement(factory, omElement, chaildTagName, value));
		return omeEle;
	}


	public static OMNode getResponseStatus(OMFactory factory,OMElement responseStatus,String returnCode,String returnDesc) throws ParseException {

		if (StringUtils.trimToEmpty(returnCode).equals("0000")) {;
		setOMChildElement(factory, responseStatus, "ReturnCode",returnCode);
		setOMChildElement(factory, responseStatus,"ReturnText", returnDesc);
		setOMChildElement(factory, responseStatus,"TimeStamp",PFFXmlUtil.getDateFormat(new Date(System.currentTimeMillis())));

		} else {
			setOMChildElement(factory, responseStatus, "ReturnCode",returnCode);
			setOMChildElement(factory, responseStatus,"ReturnText", returnDesc);
			setOMChildElement(factory, responseStatus,"TimeStamp", PFFXmlUtil.getDateFormat(new Date(System.currentTimeMillis())));
		}
		return responseStatus;
	}

	public static  OMElement getOMElements(String nodePath,OMElement requestData,boolean flag,int count) throws Exception{
		AXIOMXPath xpath1 = new AXIOMXPath(nodePath);
		@SuppressWarnings("unchecked")
		List<OMElement> ele = xpath1.selectNodes(requestData);
		if (flag) {
			return ele.get(0);
		} else {

			return ele.get(count);
		}
	}

	public static String getStringValue(OMElement requestElement,boolean isMandatory,boolean isNotEmpty,String elementTagName,String nodePath,boolean flag,int count) throws Exception  {
		String tagValue="";
		OMElement	element=PFFXmlUtil.getOMElements(nodePath+elementTagName, requestElement,flag,count);


		if(element==null){
			if(isMandatory){
				throw new Exception(elementTagName+" is Mandatory in the request");
			}else{
				return tagValue;
			}
		}else{
			tagValue = element.getText();
		}

		if(isNotEmpty && StringUtils.trimToEmpty(tagValue).equals("")){
			throw new Exception(elementTagName+" could not be Blank");
		}else{
			return StringUtils.trimToEmpty(tagValue);
		}
	}

	public static String getStringValue(OMElement requestElement,boolean isMandatory,boolean isNotEmpty,String elementTagName,String nodePath,int count) throws Exception  {
		String tagValue="";
		OMElement element = PFFXmlUtil.getOMElement(nodePath+elementTagName, requestElement,count);

		if(element==null){
			if(isMandatory){
				throw new Exception(elementTagName+" is Mandatory in the request");
			}else{
				return tagValue;
			}
		}else{
			tagValue = element.getText();
		}

		if(isNotEmpty && StringUtils.trimToEmpty(tagValue).equals("")){
			throw new Exception(elementTagName+" could not be Blank");
		}else{
			return StringUtils.trimToEmpty(tagValue);
		}
	}

	public static OMElement getOMElement(String nodePath,OMElement requestData,int count) throws Exception{
		AXIOMXPath xpath1 = new AXIOMXPath(nodePath);
		@SuppressWarnings("unchecked")
		List<OMElement> ele = xpath1.selectNodes(requestData);
		return ele.get(count);
	}

	public static OMElement getPhoneNumber(OMFactory factory,String tagName,String countryCode,String areaCode,String number ){
		OMElement phoneElement=factory.createOMElement(tagName,null);
		PFFXmlUtil.setOMChildElement(factory, phoneElement, "CountryCode", countryCode);
		PFFXmlUtil.setOMChildElement(factory, phoneElement, "AreaCode", areaCode);
		PFFXmlUtil.setOMChildElement(factory, phoneElement, "SubsidiaryNumber", number);

		return phoneElement;
	}

	public static String getStringValue(OMElement element,String elementTagName){

		OMElement dataElement= element.getFirstChildWithName(new QName(elementTagName));

		if(dataElement==null){
			return null;
		}
		return StringUtils.trimToEmpty(dataElement.getText());
	}

}

