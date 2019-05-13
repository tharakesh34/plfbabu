package com.testing;

import java.util.Arrays;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import org.apache.axiom.om.OMElement;

import com.penapp.interfaceexception.PFFInterfaceException;
public class Testing {
	
	public static void main(String[] args) throws Exception {
		
		String[] test = {"91","9666","201248"};
		System.out.println(Arrays.toString(test));
		
/*
		CollateralMarkingRequest collateralReq = new CollateralMarkingRequest();
		collateralReq.setReferenceNum("12345687");
		collateralReq.setAccountDetails("Test");
		collateralReq.setAccNum("8515568646");
		collateralReq.setDescription("Description");
		collateralReq.setInsAmount(new BigDecimal(12365478));
		collateralReq.setBlockingDate(DateUtility.getUtilDate("11/06/2015", "dd/MM/yyyy"));
		collateralReq.setDepositDetails("Test DepositDetails");
		collateralReq.setDepositID("2589");
		collateralReq.setReason("Test");
		collateralReq.setBranchCode("1010");
		collateralReq.setTimeStamp(23659745);
		
		try {
			JAXBContext jc = JAXBContext.newInstance(CollateralMarkingRequest.class);
			Marshaller marshaller = jc.createMarshaller();
			marshaller.marshal(collateralReq, new File("D://abk/Test.xml"));
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		 doMarshalling(collateralReq);
//		OMElement responseElement =  AXIOMUtil.stringToOM(StringUtils.trimToEmpty(getData()));

//		CollateralMarkingRequest response = (CollateralMarkingRequest)doUnMarshalling(responseElement, collateralReq);
//		System.out.println(response.getReason());
	}
	
	private static String getData() throws IOException {
		FileInputStream stream = new FileInputStream("E:/PFF-SIM-Test/CollateralReply.xml");
		
		return IOUtils.toString(stream);*/
	}

	/**
	 * Marshalling OBJECT to XML Element
	 * 
	 * @param request
	 * @return OMElement
	 * @throws PFFInterfaceException
	 */
	public static void doMarshalling(Object request) throws PFFInterfaceException {

	
	}

	/**
	 * UnMarshalling XML Element to Object
	 * 
	 * @param request
	 * @param classType
	 * @return Object
	 * @throws PFFInterfaceException
	 */
	public static Object doUnMarshalling(OMElement request, Object classType) throws PFFInterfaceException {

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
