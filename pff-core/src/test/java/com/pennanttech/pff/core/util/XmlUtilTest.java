package com.pennanttech.pff.core.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Iterator;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.impl.builder.StAXOMBuilder;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.pennanttech.pennapps.core.util.XmlUtil;

public class XmlUtilTest {
	private String		namespaceURI;
	private OMElement	definition;
	private OMElement	process;

	public XmlUtilTest() {
		super();
	}

	@BeforeClass
	public void setUp() throws IOException, URISyntaxException, XMLStreamException, FactoryConfigurationError {
		namespaceURI = "http://www.omg.org/spec/BPMN/20100524/MODEL";

		// Load the sample process to test.
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		byte[] buffer = Files.readAllBytes(Paths.get(loader.getResource("process.xml").toURI()));

		ByteArrayInputStream stream = new ByteArrayInputStream(buffer);
		XMLStreamReader parser = XMLInputFactory.newInstance().createXMLStreamReader(stream);
		StAXOMBuilder builder = new StAXOMBuilder(parser);

		definition = builder.getDocumentElement();
		process = XmlUtil.getElement(definition, "process", namespaceURI);
	}

	@Test
	public void getElement() {
		OMElement startEvent = XmlUtil.getElement(process, "startEvent", namespaceURI);

		Assert.assertEquals(XmlUtil.getAttribute(startEvent, "id"), "_FF9D4911-C022-42B0-B00A-9C353226F4A7");
		Assert.assertNull(XmlUtil.getElement(process, "XYZ", namespaceURI));
	}

	@Test
	public void getAttribute() {
		Assert.assertEquals(XmlUtil.getAttribute(process, "id"), "MSTGRP1");
		Assert.assertNull(XmlUtil.getAttribute(process, "XYZ"));
	}

	@Test
	public void getAttributeWithNamespace() {
		OMElement startEvent = XmlUtil.getElement(process, "startEvent", namespaceURI);

		Assert.assertEquals(XmlUtil.getAttribute(startEvent, "bgcolor", "http://www.jboss.org/drools", "drools"),
				"#C5F970");
		Assert.assertNull(XmlUtil.getAttribute(startEvent, "XYZ"));
	}

	@Test
	public void getElementByAtrributeValue() {
		OMElement element = XmlUtil.getElement(process, "id", "_77E97F8E-6071-449B-9BD7-4B0F3A5A657A", "userTask");
		String attribute = XmlUtil.getAttribute(element, "name");

		Assert.assertEquals(attribute, "Approver");

		element = XmlUtil.getElement(process, "sourceRef", "_FF9D4911-C022-42B0-B00A-9C353226F4A7", "sequenceFlow");
		attribute = XmlUtil.getAttribute(element, "id");

		Assert.assertEquals(attribute, "_5EE9B2AF-F8EA-432C-BB52-88124AA2A117");

		element = XmlUtil.getElement(process, "id", "_JuFQse0XEeak1dHdhEvDig", "potentialOwner");

		Assert.assertNull(element);

		element = XmlUtil.getElement(process, "id", "_87A46993-91DA-47E0-811B-2F196FBAD6AA", null);
		attribute = XmlUtil.getAttribute(element, "name");

		Assert.assertEquals(attribute, "Rejected");
	}

	@Test
	public void getElementText() {
		OMElement element = XmlUtil.getElement(process, "id", "_14CED7D6-CEC3-49D1-A614-5168AD7F13F6", "sequenceFlow");
		String text = XmlUtil.getElementText(element, "documentation", namespaceURI);

		Assert.assertEquals(text, "Cancel=Cancelled");
	}

	@Test
	public void getElementTextHierarichal() {
		OMElement element = XmlUtil.getElement(process, "id", "_14CED7D6-CEC3-49D1-A614-5168AD7F13F6", "sequenceFlow");
		String text = XmlUtil.getElementText(element, "auditing/documentation", namespaceURI);
		Assert.assertEquals(text, "Notes");

		text = XmlUtil.getElementText(element, "auditing/documentation/xyz", namespaceURI);
		Assert.assertEquals(text, null);

		text = XmlUtil.getElementText(element, "auditing/xyz/documentation", namespaceURI);
		Assert.assertEquals(text, null);

		text = XmlUtil.getElementText(element, "xyz/auditing/documentation", namespaceURI);
		Assert.assertEquals(text, null);
	}

	@Test
	public void getChildren() {
		Iterator<OMElement> iterator = XmlUtil.getChildren(process, "userTask", namespaceURI);
		OMElement element;
		int i = 0;

		while (iterator.hasNext()) {
			element = iterator.next();

			switch (i) {
			case 0:
				Assert.assertEquals(XmlUtil.getAttribute(element, "id"), "_7B64C6C5-337C-44C2-98C6-A723EC862BFF");
				break;
			case 1:
				Assert.assertEquals(XmlUtil.getAttribute(element, "id"), "_77E97F8E-6071-449B-9BD7-4B0F3A5A657A");
				break;
			default:
				Assert.assertTrue(i == 0);
			}

			i++;
		}
	}

	@Test
	public void getChildrenAll() {
		Iterator<OMElement> iterator = XmlUtil.getChildren(process);
		int i = 0;

		while (iterator.hasNext()) {
			iterator.next();

			i++;
		}

		Assert.assertEquals(i, 17);
	}
}
