package com.pennant.ahb.process;

import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.impl.llom.util.AXIOMUtil;

import com.penapp.interfaceexception.PFFInterfaceException;

public abstract class MQProcess {

	/**
	 * Marshalling OBJECT to XML Element
	 * 
	 * @param request
	 * @return OMElement
	 * @throws PFFInterfaceException
	 */
	public OMElement doMarshalling(Object request) throws PFFInterfaceException {

		StringWriter writer = new StringWriter();
		OMElement element = null;
		try {
			JAXBContext jc = JAXBContext.newInstance(request.getClass());
			Marshaller marshaller = jc.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			marshaller.marshal(request, writer);
			element = AXIOMUtil.stringToOM(writer.toString());
		} catch (Exception e) {
			throw new PFFInterfaceException("PTI5001", e.getMessage());
		}
		return element;
	}

	/**
	 * UnMarshalling XML Element to Object
	 * 
	 * @param request
	 * @param classType
	 * @return Object
	 * @throws PFFInterfaceException
	 */
	public Object doUnMarshalling(OMElement request, Object classType) throws PFFInterfaceException {

		Object resObject = null;
		try {
			JAXBContext jc = JAXBContext.newInstance(classType.getClass());
			Unmarshaller unmarshaller = jc.createUnmarshaller();
			resObject = unmarshaller.unmarshal(request.getXMLStreamReader());
			
		} catch (Exception e) {
			throw new PFFInterfaceException("PTI5001", e.getMessage());
		}
		return resObject;
	}

}
