package com.pennant.mq.processutil;

import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.impl.llom.util.AXIOMUtil;
import org.apache.log4j.Logger;

import com.pennant.mq.model.AHBMQHeader;
import com.pennant.mq.util.PFFXmlUtil;
import com.pennant.mqconnection.InterfacePropertiesUtil;
import com.pennanttech.pennapps.core.InterfaceException;

public abstract class MQProcess {
	private static final Logger logger = Logger.getLogger(MQProcess.class);
	private String[] configDetails = null; 

	public MQProcess() {
		super();
	}
	
	public String getRequestQueue() throws InterfaceException{
		return InterfacePropertiesUtil.getProperty(getServiceConfigKey()+"_"+configDetails[1]+"_"+ "QUEUE");
	}

	public String getResponseQueue() throws InterfaceException{
		return InterfacePropertiesUtil.getProperty(getServiceConfigKey()+"_"+configDetails[2]+"_"+ "QUEUE");
	}

	public int getWaitTime() throws InterfaceException{
		return InterfacePropertiesUtil.getIntProperty(getServiceConfigKey()+"_"+configDetails[2]+"_"+ "QUEUEWTIME");
	}

	@Deprecated
	public AHBMQHeader getReturnStatus(OMElement detailElement,AHBMQHeader header) throws InterfaceException{
		try {
			OMElement returnText = detailElement.getFirstChildWithName(new QName("ReturnText"));
			OMElement timeStamp = detailElement.getFirstChildWithName(new QName("TimeStamp"));
			
			header.setReturnCode(detailElement.getFirstChildWithName(new QName("ReturnCode")).getText());
			header.setReferenceNum(detailElement.getFirstChildWithName(new QName("ReferenceNum")).getText());
			if (returnText != null) {
				header.setReturnText(returnText.getText());
			}
			if(timeStamp!=null){
				header.setReturnTime(timeStamp.getText());	
			}
		} catch(Exception e) {
			logger.debug("Exception: ", e);
			throw new InterfaceException("PTI3003", "Header: mandatory fields are Empty");
		}

		return header;

	}
	
	public AHBMQHeader getReturnStatus(OMElement detailElement,AHBMQHeader header, OMElement responseElement) throws InterfaceException{
		try {
			if (detailElement == null) {
				OMElement returnText = PFFXmlUtil.getOMElement("/HB_EAI_REPLY/Reply/ErrorReply/Detail/Text", responseElement);
				
				if (returnText != null) {
					header.setReturnText(returnText.getText());
				}
				
				if ("0000".equals(header.getReturnCode())) {
					header.setReturnCode("UHEC");
				}
			} else {
				OMElement returnText = detailElement.getFirstChildWithName(new QName("ReturnText"));
				OMElement timeStamp = detailElement.getFirstChildWithName(new QName("TimeStamp"));
				
				header.setReturnCode(detailElement.getFirstChildWithName(new QName("ReturnCode")).getText());
				header.setReferenceNum(detailElement.getFirstChildWithName(new QName("ReferenceNum")).getText());
				if (returnText != null) {
					header.setReturnText(returnText.getText());
				}
				if(timeStamp!=null){
					header.setReturnTime(timeStamp.getText());	
				}
			}
		} catch(Exception e) {
			logger.debug("Exception: ", e);
			throw new InterfaceException("PTI3003", "Header: mandatory fields are Empty");
		}

		return header;

	}

	/**
	 * Marshaling OBJECT to XML Element
	 * 
	 * @param request
	 * @return OMElement
	 * @throws InterfaceException
	 */
	public OMElement doMarshalling(Object request) throws InterfaceException {

		if(request == null) {
			throw new InterfaceException("PTI5002", "Request Element is Empty");
		}
		StringWriter writer = new StringWriter();
		OMElement element = null;
		try {
			JAXBContext jc = JAXBContext.newInstance(request.getClass());
			Marshaller marshaller = jc.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			marshaller.marshal(request, writer);
			element = AXIOMUtil.stringToOM(writer.toString());
		} catch (Exception e) {
			logger.debug("Exception: ", e);
			throw new InterfaceException("PTI5001", e.getMessage());
		}
		return element;
	}

	/**
	 * UnMarshalling XML Element to Object
	 * 
	 * @param request
	 * @param classType
	 * @return Object
	 * @throws InterfaceException
	 */
	public Object doUnMarshalling(OMElement request, Object classType) throws InterfaceException {

		if(request == null) {
			throw new InterfaceException("PTI5002", "Response Element is Empty");
		}
		Object resObject = null;
		try {
			JAXBContext jc = JAXBContext.newInstance(classType.getClass());
			Unmarshaller unmarshaller = jc.createUnmarshaller();
			resObject = unmarshaller.unmarshal(request.getXMLStreamReader());
		} catch (Exception e) {
			logger.debug("Exception: ", e);
			throw new InterfaceException("PTI5001", e.getMessage());
		}
		return resObject;
	}

	public String getServiceConfigKey() {
		return configDetails[0];
	}

	public String[] getConfigDetails() {
		return configDetails;
	}

	public void setConfigDetails(String seriviceKey) throws InterfaceException {
		this.configDetails = InterfacePropertiesUtil.getProperty(seriviceKey).split(",");
	}
}
