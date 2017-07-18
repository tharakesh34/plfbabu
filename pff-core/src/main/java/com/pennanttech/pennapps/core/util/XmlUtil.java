/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. All
 * components/modules/functions/classes/logic in this software, unless otherwise stated, the property of Pennant
 * Technologies.
 * 
 * Copyright and other intellectual property laws protect these materials. Reproduction or retransmission of the
 * materials, in whole or in part, in any manner, without the prior written consent of the copyright holder, is a
 * violation of copyright law.
 */
package com.pennanttech.pennapps.core.util;

import java.util.Iterator;

import javax.xml.namespace.QName;

import org.apache.axiom.om.OMElement;
import org.apache.commons.lang.StringUtils;

/**
 * A suite of utilities surrounding the use of the XML nodes.
 */
public final class XmlUtil {
	private XmlUtil() {
		super();
	}

	/**
	 * Returns a named attribute's value of the element, if present.
	 * 
	 * @param element
	 *            The parent element within which the attribute to be found.
	 * @param name
	 *            The name of attribute to search for.
	 * @return Returns the attribute value of the parent, or null if none was found.
	 */
	public static String getAttribute(OMElement element, String name) {
		return element.getAttributeValue(new QName(name));
	}

	/**
	 * Returns a named attribute's value of the element, if present.
	 * 
	 * @param element
	 *            The parent element within which the attribute to be found.
	 * @param name
	 *            The name of attribute to search for.
	 * @param namespaceURI
	 *            The namespace URI of the name.
	 * @param prefix
	 *            The namespace prefix of the name.
	 * @return Returns the attribute value of the parent, or null if none was found.
	 */
	public static String getAttribute(OMElement element, String name, String namespaceURI, String prefix) {
		return element.getAttributeValue(new QName(namespaceURI, name, prefix));
	}

	/**
	 * Returns the first child of the parent in document order matching the given name.
	 * 
	 * @param parent
	 *            The parent element within which the match to be found.
	 * @param name
	 *            The name to search for.
	 * @param namespaceURI
	 *            The namespace URI of the name.
	 * @return Returns the first child element of the parent, or null if none was found.
	 */
	public static OMElement getElement(OMElement parent, String name, String namespaceURI) {
		return parent.getFirstChildWithName(new QName(namespaceURI, name));
	}

	/**
	 * Returns the first child of the parent in document order matching with the criteria.
	 * 
	 * @param parent
	 *            The parent element within which the match to be found.
	 * @param attributeName
	 *            The name of attribute to search for.
	 * @param attributeValue
	 *            The value of the attribute to match.
	 * @param elementName
	 *            The element name to search for. null to search any element.
	 * @return Returns the first child element of the parent, or null if none was found.
	 */
	public static OMElement getElement(OMElement parent, String attributeName, String attributeValue, String elementName) {
		@SuppressWarnings("unchecked")
		Iterator<OMElement> iterator = parent.getChildElements();

		while (iterator.hasNext()) {
			OMElement element = iterator.next();

			if (elementName == null || StringUtils.equals(element.getLocalName(), elementName)) {
				if (StringUtils.equals(getAttribute(element, attributeName), attributeValue)) {
					return element;
				}
			}
		}

		return null;
	}

	/**
	 * Returns the text children of first child of the parent in document order matching the given name.
	 * 
	 * @param parent
	 *            The parent element within which the match to be found.
	 * @param name
	 *            The name to search for.
	 * @param namespaceURI
	 *            The namespace URI of the name.
	 * @return Returns the first child element's text nodes of the parent, or null if none was found.
	 */
	public static String getElementText(OMElement parent, String name, String namespaceURI) {
		OMElement element = null;

		if (StringUtils.contains(name, '/')) {
			String[] nameParts = StringUtils.split(name, '/');

			for (int i = 0; i < nameParts.length; i++) {
				if (i == 0) {
					element = parent.getFirstChildWithName(new QName(namespaceURI, nameParts[i]));
				} else if (element != null) {
					element = element.getFirstChildWithName(new QName(namespaceURI, nameParts[i]));
				}

				if (element == null) {
					break;
				}
			}
		} else {
			element = parent.getFirstChildWithName(new QName(namespaceURI, name));
		}

		if (element == null) {
			return null;
		}

		String text = StringUtils.trimToEmpty(element.getText());
		text = text.replaceAll("\\s+", " ");

		return StringUtils.trimToNull(text);
	}

	/**
	 * Returns an iterator for child nodes matching the given name.
	 * 
	 * @param parent
	 *            The parent element within which the child nodes to be found.
	 * @param name
	 *            The name to search for.
	 * @param namespaceURI
	 *            The namespace URI of the name.
	 * @return Returns an iterator of OMElement items that match the given name.
	 */
	@SuppressWarnings("unchecked")
	public static Iterator<OMElement> getChildren(OMElement parent, String name, String namespaceURI) {
		return parent.getChildrenWithName(new QName(namespaceURI, name));
	}

	/**
	 * Returns an iterator for child nodes.
	 * 
	 * @param parent
	 *            The parent element within which the child nodes to be found.
	 * @return Returns an iterator of OMElement items.
	 */
	@SuppressWarnings("unchecked")
	public static Iterator<OMElement> getChildren(OMElement parent) {
		return parent.getChildElements();
	}
}
