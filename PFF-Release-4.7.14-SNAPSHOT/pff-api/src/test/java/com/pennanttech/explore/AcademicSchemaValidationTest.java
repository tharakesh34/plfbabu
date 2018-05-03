/*package com.pennanttech.explore;

import static org.junit.Assert.assertEquals;

import java.io.StringReader;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;


 * This is a test program check the validations of XML with the XSD schema.
 * We can configure the XSD to validate the data based on the service. 
 * In this Test the XML validations for academic are different for save and update,
 * While saving the details ID should not required and while update ID is mandatory.
 * similarly the mandatory fields for save and update are different in schema.
 

public class AcademicSchemaValidationTest {

	private StringBuffer xsd = new StringBuffer();

	@Test
	public void saveXMLValid() {
		StringBuffer xml = new StringBuffer();

		xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?> ");
		xml.append("<tns:saveAcademic  xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" ");
		xml.append("          xmlns:tns=\"http://pffws.pennanttech.com/\"> ");
		xml.append("    <academic> ");
		xml.append("		<academicLevel>GRADUATE</academicLevel> ");
		xml.append("		<academicDecipline>B.COM</academicDecipline> ");
		xml.append("		<academicDesc>Bachelor of Commerce - to be approved now</academicDesc>");
		xml.append("	</academic>");
		xml.append("</tns:saveAcademic>");

		assertEquals("Save XML should be valid ...", 
				true, validateXMLSchema(xml.toString(), xsd.toString()));
	}

	@Test
	public void updateXMLValid() {
		StringBuffer xml = new StringBuffer();

		xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?> ");
		xml.append("<tns:updateAcademic  xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" ");
		xml.append("          xmlns:tns=\"http://pffws.pennanttech.com/\"> ");
		xml.append("  <academic> ");
		xml.append("		<academicId>3</academicId> ");
		xml.append("		<academicDesc>Bachelor of Commerce - to be approved now</academicDesc>");
		xml.append("	</academic>");
		xml.append("</tns:updateAcademic>");

		assertEquals("Update XML should be valid ...", 
				true, validateXMLSchema(xml.toString(), xsd.toString()));
	}

	 // academicId should not be there in save. 
	@Test
	public void saveXMLInvalidNewTag() {
		StringBuffer xml = new StringBuffer();

		xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?> ");
		xml.append("<tns:saveAcademic  xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" ");
		xml.append("          xmlns:tns=\"http://pffws.pennanttech.com/\"> ");
		xml.append("    <academic> ");
		xml.append("		<academicId>3</academicId> ");
		xml.append("		<academicLevel>GRADUATE</academicLevel> ");
		xml.append("		<academicDecipline>B.COM</academicDecipline> ");
		xml.append("		<academicDesc>Bachelor of Commerce - to be approved now</academicDesc>");
		xml.append("	</academic>");
		xml.append("</tns:saveAcademic>");

		assertEquals("Save XML should be Invalid additional property academicId ...", 
				false, validateXMLSchema(xml.toString(), xsd.toString()));
	}

	 //academicLevel tag is missing. 
	@Test
	public void saveXMLInvalidMissingTag() {
		StringBuffer xml = new StringBuffer();

		xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?> ");
		xml.append("<tns:saveAcademic  xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" ");
		xml.append("          xmlns:tns=\"http://pffws.pennanttech.com/\"> ");
		xml.append("    <academic> ");
		xml.append("		<academicDecipline>B.COM</academicDecipline> ");
		xml.append("		<academicDesc>Bachelor of Commerce - to be approved now</academicDesc>");
		xml.append("	</academic>");
		xml.append("</tns:saveAcademic>");

		assertEquals("Save XML should be Invalid required property is missing ...", false,
				validateXMLSchema(xml.toString(), xsd.toString()));
	}

	private boolean validateXMLSchema(String xml, String xsd) {
		try {
			SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
			Schema schema = factory.newSchema(new StreamSource(new StringReader(xsd)));
			Validator validator = schema.newValidator();
			validator.validate(new StreamSource(new StringReader(xml)));
		} catch (Exception e1) {
			System.out.println("INFO : " + e1.getMessage());
			return false;
		}
		return true;
	}

	
	 * This method is to set the XSD for this test.
	 
	@BeforeTest
	private void setXSD() {
		xsd.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		xsd.append("");
		xsd.append("<xs:schema attributeFormDefault=\"unqualified\" " + "elementFormDefault=\"unqualified\" "
				+ "targetNamespace=\"http://pffws.pennanttech.com/\" "
				+ "xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" " + "xmlns:tns=\"http://pffws.pennanttech.com/\">");

		xsd.append("    <xs:element name=\"saveAcademic\" type=\"tns:saveAcademic\"/>");
		xsd.append("    <xs:element name=\"updateAcademic\" type=\"tns:updateAcademic\"/>");

		xsd.append("	  <xs:complexType name=\"saveAcademic\">");
		xsd.append("		<xs:sequence>");
		xsd.append("			<xs:element minOccurs=\"0\" name=\"academic\" type=\"tns:academic\"/>");
		xsd.append("		</xs:sequence>");
		xsd.append("	  </xs:complexType>");

		xsd.append("	  <xs:complexType name=\"updateAcademic\">");
		xsd.append("		<xs:sequence>");
		xsd.append("			<xs:element minOccurs=\"0\" name=\"academic\" type=\"tns:academicUpdate\"/>");
		xsd.append("		</xs:sequence>");
		xsd.append("	  </xs:complexType>");

		xsd.append("    <xs:complexType name=\"academic\">");
		xsd.append("    	<xs:sequence>");
		xsd.append("      	<xs:element minOccurs=\"1\" name=\"academicLevel\" type=\"xs:string\"/>");
		xsd.append("          <xs:element minOccurs=\"1\" name=\"academicDecipline\" type=\"xs:string\"/>");
		xsd.append("          <xs:element minOccurs=\"0\" name=\"academicDesc\" type=\"xs:string\"/>");
		xsd.append("       </xs:sequence>");
		xsd.append("    </xs:complexType>");

		xsd.append("    <xs:complexType name=\"academicUpdate\">");
		xsd.append("		<xs:sequence>");
		xsd.append("			<xs:element minOccurs=\"1\" name=\"academicId\" type=\"xs:string\"/>");
		xsd.append("			<xs:element minOccurs=\"1\" name=\"academicDesc\" type=\"xs:string\"/>");
		xsd.append("		</xs:sequence>");
		xsd.append("	  </xs:complexType>");

		xsd.append("</xs:schema>");
	}

}
*/