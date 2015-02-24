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
 *
 * FileName    		:  WorkFlow.java														*                           
 *                                                                    
 * Author      		:  PENNANT TECHONOLOGIES												*
 *                                                                  
 * Creation Date    :  26-04-2011															*
 *                                                                  
 * Modified Date    :  26-04-2011															*
 *                                                                  
 * Description 		:												 						*                                 
 *                                                                                          
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 26-04-2011       Pennant	                 0.1                                            * 
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

package com.pennant.util;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringWriter;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.xml.namespace.QName;
import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.impl.builder.StAXOMBuilder;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.zkoss.spring.SpringUtil;

import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.service.WorkFlowDetailsService;

public class Workflow {
	private OMElement definition;
	private String defaultNamespaceURI;
	private OMElement process;
	private OMElement startEvent;
	public Task firstTask = null; // TODO

	public class Task {
		public String id = "";
		public String owner = "";
	}

	private enum Namespace {
		DEFAULT("http://www.omg.org/bpmn20"), BPMN2(
				"http://www.omg.org/spec/BPMN/20100524/MODEL"), DROOLS(
				"http://www.jboss.org/drools");

		private String uri;

		private Namespace(String uri) {
			this.uri = uri;
		}

		public String getUri() {
			return uri;
		}
	}

	private enum Element {
		process, startEvent, outgoing;
	}

	private enum Attribute {
		id, targetRef;
	}

	private enum UserTask {
		OWNER("potentialOwner/resourceAssignmentExpression/formalExpression"), SCOPE(
				"extensionElements/onEntry-script/script/scope="), SHOW_TABS(
				"extensionElements/onEntry-script/script/show_tabs"),ASSIGNMENT(
						"extensionElements/onExit-script/script/assignment");

		private String element;

		private UserTask(String element) {
			this.element = element;
		}

		public String getElement() {
			return element;
		}
	}

	public Workflow(StAXOMBuilder builder) {
		setDefinition(builder.getDocumentElement());

		init();
	}

	private void init() {
		setDefaultNamespaceURI(Namespace.BPMN2.getUri());

		setProcess(getElement(getDefinition(), Element.process));
		setStartEvent(getElement(getProcess(), Element.startEvent));

		// Set the first task of the process
		Task task = new Task();
		String taskId = "";

		// Assuming single first task
		taskId = getNextTaskIds(getAttribute(getStartEvent(), Attribute.id),
				null); // TODO:
		taskId = taskId.replaceAll(";", "");

		task.id = taskId;
		task.owner = getTaskOwner(taskId);

		setFirstTask(task);
	}

	// User task and their properties
	public String getTaskOwner(String taskId) {
		return getTaskOwner(getElementById(taskId));
	}

	public String getTaskOwner(OMElement task) {
		String[] elements = UserTask.OWNER.getElement().split("/");

		OMElement element = getElement(task, elements[0]);

		if (null == element) {
			return "";
		}

		element = getElement(element, elements[1]);

		if (null == element) {
			return "";
		}

		return getElementContent(element, elements[2]);
	}

	public String getTaskScope(String taskId) {
		return getTaskScope(getElementById(taskId));
	}

	@SuppressWarnings("unchecked")
	public String getTaskScope(OMElement task) {
		String[] elements = UserTask.SCOPE.getElement().split("/");

		OMElement element = getElement(task, elements[0]);

		if (null == element) {
			return "";
		}

		Iterator<OMElement> iterator = element.getChildElements();
		String result = "";
		String value = "";

		while (iterator.hasNext()) {
			element = iterator.next();

			if (elements[1].equalsIgnoreCase(element.getLocalName())) {
				value = getElementContent(element, elements[2],
						Namespace.DROOLS.getUri());

				if (StringUtils.startsWith(value, elements[3])) {
					result = StringUtils.substring(value, elements[3].length());
					break;
				}
			}
		}

		return result;
	}

	public String getTaskTabs(String taskId) {
		return getTaskTabs(getElementById(taskId));
	}

	@SuppressWarnings("unchecked")
	public String getTaskTabs(OMElement task) {
		String[] elements = UserTask.SHOW_TABS.getElement().split("/");

		OMElement element = getElement(task, elements[0]);

		if (null == element) {
			return "";
		}

		Iterator<OMElement> iterator = element.getChildElements();
		String result = "";
		String value = "";

		while (iterator.hasNext()) {
			element = iterator.next();

			if (elements[1].equalsIgnoreCase(element.getLocalName())) {
				value = getElementContent(element, elements[2],
						Namespace.DROOLS.getUri());

				String[] actions = value.split("\\|");

				for (String action : actions) {
					String[] pair = action.split("=");

					if (elements[3].equals(pair[0])) {
						result = pair[1];
						break;
					}
				}
			}
		}

		return result;
	}

	// Sequence flows and their properties
	@SuppressWarnings("unchecked")
	private String getSequenceFlows(OMElement element) {
		String result = "";

		Iterator<OMElement> iterator = element.getChildElements();
		OMElement child = null;

		while (iterator.hasNext()) {
			child = iterator.next();

			if (Element.outgoing.name().equalsIgnoreCase(child.getLocalName())) {
				result += child.getText() + ";";
			}
		}

		return result;
	}

	@SuppressWarnings("unchecked")
	private String getSequenceFlowTarget(String sequenceId) {
		Iterator<OMElement> iterator = getProcess().getChildElements();
		OMElement element = null;

		while (iterator.hasNext()) {
			element = iterator.next();

			if (sequenceId.equals(getAttribute(element, Attribute.id))) {
				return getAttribute(element, Attribute.targetRef);
			}
		}

		return "";
	}

	// Basic methods to parse XML
	private OMElement getElement(OMElement parent, Element element) {
		return getElement(parent, element.name());
	}

	private OMElement getElement(OMElement parent, String name) {
		return getElement(parent, name, getDefaultNamespaceURI());
	}

	private OMElement getElement(OMElement parent, String name,
			String namespaceURI) {
		return parent.getFirstChildWithName(new QName(namespaceURI, name));
	}

	private String getAttribute(OMElement element, Attribute attribute) {
		return getAttribute(element, attribute.name());
	}

	private String getAttribute(OMElement element, String name) {
		return element.getAttributeValue(new QName(name));
	}

	private OMElement getElementById(String id) {
		return getElementById(getProcess(), id);
	}

	@SuppressWarnings("unchecked")
	private OMElement getElementById(OMElement parent, String id) {
		Iterator<OMElement> iterator = parent.getChildElements();

		while (iterator.hasNext()) {
			OMElement element = iterator.next();

			if (id.equals(getAttribute(element, Attribute.id))) {
				return element;
			}

			if (element.getChildElements().hasNext()) {
				OMElement element2 = getElementById(element, id);

				if (null != element2) {
					return element2;
				}
			}
		}

		return null;
	}

	private String getElementContent(String parentId, String name) {
		return getElementContent(getElementById(parentId), name);
	}

	private String getElementContent(OMElement parent, String name) {
		return getElementContent(parent, name, getDefaultNamespaceURI());
	}

	private String getElementContent(OMElement parent, String name,
			String namespaceURI) {
		OMElement element = getElement(parent, name, namespaceURI);

		if (element != null) {
			return element.getText();
		}

		return "";
	}

	// Methods to access member variables
	public OMElement getDefinition() {
		return definition;
	}

	private void setDefinition(OMElement definition) {
		this.definition = definition;
	}

	public String getDefaultNamespaceURI() {
		return defaultNamespaceURI;
	}

	private void setDefaultNamespaceURI(String defaultNamespaceURI) {
		this.defaultNamespaceURI = defaultNamespaceURI;
	}

	public OMElement getProcess() {
		return process;
	}

	private void setProcess(OMElement process) {
		this.process = process;
	}

	public OMElement getStartEvent() {
		return startEvent;
	}

	private void setStartEvent(OMElement startEvent) {
		this.startEvent = startEvent;
	}

	public Task getFirstTask() {
		return firstTask;
	}

	private void setFirstTask(Task firstTask) {
		this.firstTask = firstTask;
	}

	// TODO
	public String getNextTaskIds(String taskId, Object object) {
		String result = "";

		OMElement element = getElementById(taskId);
		String sequences = getSequenceFlows(element);
		String[] list = sequences.split(";");

		for (int i = 0; i < list.length; i++) {
			String targetId = getSequenceFlowTarget(list[i]);
			OMElement target = getElementById(targetId);
			boolean proceedFurther = true;
			String exp = getElementContent(list[i], "conditionExpression");
			if ("".equals(exp)) {
				//
			} else {
				try {
					if (!eval(exp, object)) {
						proceedFurther = false;
					}
				} catch (ScriptException e) {
					e.printStackTrace();
				}
			}

			if (proceedFurther) {
				if ("endEvent".equals(target.getLocalName())) {
					// Process completed
				} else if ("exclusiveGateway".equals(target.getLocalName())
						|| "parallelGateway".equals(target.getLocalName())) {
					// Drill down
					if ("Diverging".equals(getAttribute(target,
							"gatewayDirection"))) {
						result += getNextTaskIds(targetId, object);
					} else {
						result += getNextTaskIds(targetId, object);
					}
				} else if ("serviceTask".equals(target.getLocalName())) {
					result += getNextTaskIds(targetId, object);
				} else {
					result += targetId + ";";
				}
			}
		}

		return result;
	}

	private transient WorkFlowDetailsService workFlowDetailsService;

	public Workflow(long workFlowId) throws FileNotFoundException,
			XMLStreamException {
		WorkFlowDetails workFlowDetails = getWorkFlowDetailsService()
				.getWorkFlowDetailsByID(workFlowId);
		ByteArrayInputStream xmlStream = new ByteArrayInputStream(
				workFlowDetails.getWorkFlowXml().getBytes());
		XMLStreamReader parser = XMLInputFactory.newInstance()
				.createXMLStreamReader(xmlStream);
		StAXOMBuilder builder = new StAXOMBuilder(parser);

		definition = builder.getDocumentElement();
		init();
	}

	private String getAttribute(OMElement element, String namespace,
			String name, String prefix) {
		return element.getAttributeValue(new QName(namespace, name, prefix));
	}

	@SuppressWarnings("unused")
	private String getAttribute(String elementId, String name) {
		OMElement element = getElementById(elementId);

		return element.getAttributeValue(new QName(name));
	}

	public String getNextRoleCode(String nextTaskId) {
		String nextRoleCode = "";

		if (StringUtils.trimToEmpty(nextTaskId).equals("")) {
			nextRoleCode = getFirstTask().owner;
		} else {
			String[] nextTasks = nextTaskId.split(";");

			if (nextTasks != null && nextTasks.length > 0) {
				for (int i = 0; i < nextTasks.length; i++) {

					if (nextRoleCode.length() > 1) {
						nextRoleCode = nextRoleCode + ",";
					}
					nextRoleCode = getTaskOwner(nextTasks[i]);
				}
			} else {
				nextRoleCode = getTaskOwner(nextTaskId);
			}
		}

		return nextRoleCode;
	}

	public String getAllSequenceFlows(String taskId) {
		String result = "";

		OMElement element = getElementById(taskId);
		String sequences = getSequenceFlows(element);
		String[] list = sequences.split(";");

		for (int i = 0; i < list.length; i++) {
			String targetId = getSequenceFlowTarget(list[i]);
			OMElement target = getElementById(targetId);

			String documentation = getElementContent(list[i], "documentation");
			if ("".equals(documentation)) {
				//
			} else {
				if (!"".equals(result)) {
					result += "/";
				}

				result += documentation;
			}

			if ("endEvent".equals(target.getLocalName())) {
				//
			} else if ("exclusiveGateway".equals(target.getLocalName())
					|| "parallelGateway".equals(target.getLocalName())) {
				if ("Diverging"
						.equals(getAttribute(target, "gatewayDirection"))) {
					// Drill down
					result += getAllSequenceFlows(targetId);
				} else {
					// result += getAllSequenceFlows(targetId);
					System.out.println("Coverging skipped.....");
				}
			} else {
				//
			}
		}

		return result;
	}

	@SuppressWarnings("unchecked")
	public ArrayList<String> getTaskId(Set<String> userRoleSet) {

		ArrayList<String> arrayTaskID = new ArrayList<String>();
		Iterator<OMElement> iterator = getProcess().getChildElements();

		while (iterator.hasNext()) {
			OMElement element = iterator.next();

			if ("userTask".equalsIgnoreCase(element.getLocalName())) {
				String id = getAttribute(element, "id");
				String name = getTaskOwner(id);

				if (userRoleSet.contains(name)) {
					arrayTaskID.add(getAttribute(element, "id"));
				}
			}
		}
		return arrayTaskID;
	}

	@SuppressWarnings("unchecked")
	public String getTaskId(String owner) {
		String result = "";

		Iterator<OMElement> iterator = getProcess().getChildElements();

		while (iterator.hasNext()) {
			OMElement element = iterator.next();

			if ("userTask".equalsIgnoreCase(element.getLocalName())) {
				String id = getAttribute(element, "id");
				String name = getTaskOwner(id);

				if (owner.equals(name)) {
					result = getAttribute(element, "id");
					break;
				}
			}
		}

		return result;
	}

	public String getOperationRefs(String taskId, Object object) {
		String result = "";

		OMElement element = getElementById(taskId);
		String sequences = getSequenceFlows(element);
		String[] list = sequences.split(";");

		for (int i = 0; i < list.length; i++) {
			String targetId = getSequenceFlowTarget(list[i]);
			OMElement target = getElementById(targetId);

			boolean proceedFurther = true;
			String exp = getElementContent(list[i], "conditionExpression");
			if ("".equals(exp)) {
				//
			} else {
				try {
					if (!eval(exp, object)) {
						proceedFurther = false;
					}
				} catch (ScriptException e) {
					e.printStackTrace();
				}
			}

			if (proceedFurther) {
				if ("endEvent".equals(target.getLocalName())) {
					//
				} else if ("exclusiveGateway".equals(target.getLocalName())
						|| "parallelGateway".equals(target.getLocalName())) {
					if ("Diverging".equals(getAttribute(target,
							"gatewayDirection"))) {
						// Drill down
						result += getOperationRefs(targetId, object);
					} else {
						result += getOperationRefs(targetId, object);
					}
				} else if ("serviceTask".equals(target.getLocalName())) {
					// result += getAttribute(target, "operationRef") + ";";
					result += getAttribute(target,
							"http://www.jboss.org/drools",
							"servicetaskoperation", "drools")
							+ ";";
					result += getOperationRefs(targetId, object);
				} else {
					// result += targetId + ";";
				}
			}
		}

		return result;
	}

	public String getAuditingReq(String taskId, Object object) {
		String result = "";

		OMElement element = getElementById(taskId);
		String sequences = getSequenceFlows(element);
		String[] list = sequences.split(";");

		for (int i = 0; i < list.length; i++) {
			String targetId = getSequenceFlowTarget(list[i]);
			OMElement target = getElementById(targetId);

			boolean proceedFurther = true;
			String exp = getElementContent(list[i], "conditionExpression");
			if ("".equals(exp)) {
				//
			} else {
				try {
					if (!eval(exp, object)) {
						proceedFurther = false;
					}
				} catch (ScriptException e) {
					e.printStackTrace();
				}
			}

			if (proceedFurther) {
				if ("endEvent".equals(target.getLocalName())) {
					//
				} else if ("exclusiveGateway".equals(target.getLocalName())
						|| "parallelGateway".equals(target.getLocalName())) {
					if ("Diverging".equals(getAttribute(target,
							"gatewayDirection"))) {
						// Drill down
						result += getAuditingReq(targetId, object);
					} else {
						result += getAuditingReq(targetId, object);
					}
				} else if ("serviceTask".equals(target.getLocalName())) {
					result += getAuditingReq(targetId, object);
				} else {
					if (getElement(getElementById(list[i]), "auditing") != null) {
						result += getElementContent(
								getElement(getElementById(list[i]), "auditing"),
								"documentation")
								+ ";";
					}
				}
			}
		}

		return result;
	}

	private boolean eval(String exp, Object object) throws ScriptException {
		exp = exp.replaceAll("&amp;", "&");
		exp = exp.replaceAll("&gt;", ">");
		exp = exp.replaceAll("&lt;", "<");
		System.out.println(exp);
		ScriptEngineManager factory = new ScriptEngineManager();
		ScriptEngine engine = factory.getEngineByName("JavaScript");

		engine.put("vo", object);

		Boolean result = (Boolean) engine.eval(exp);

		return result;
	}

	public WorkFlowDetailsService getWorkFlowDetailsService() {

		if (this.workFlowDetailsService == null) {
			this.workFlowDetailsService = (WorkFlowDetailsService) SpringUtil
					.getBean("workFlowDetailsService");
		}
		return workFlowDetailsService;
	}

	@SuppressWarnings("unchecked")
	public String getRoles() {
		StringBuffer result = new StringBuffer();

		System.out.println(result.toString());

		Iterator<OMElement> userTasks = getProcess().getChildrenWithName(
				new QName("userTask"));

		while (userTasks.hasNext()) {
			OMElement task = userTasks.next();

			if (result.length() > 0) {
				result.append(";");
			}

			result.append(getTaskOwner(task));
		}

		// Added for Batch processes on 24-Nov-2012
		Iterator<OMElement> receiveTasks = getProcess().getChildrenWithName(
				new QName("receiveTask"));

		while (receiveTasks.hasNext()) {
			OMElement task = receiveTasks.next();

			if (result.length() > 0) {
				result.append(";");
			}

			result.append(getTaskOwner(task));
		}
		// Added for Batch processes on 24-Nov-2012

		return result.toString();
	}

	public String getWorkFlowRole() {
		return getRoles();
	}

	// PBPM Designer
	private static final String pbpmUrl = "http://localhost:8080/designer/editor?profile=pbpm";
	private static final String pbpmRepository = "C:/pbpm/designer/repository";
	private static final String pbpmPackage = "PFS";

	public static String getPbpmUrl() {
		String ipAddress[] = new String[2];
		try {
			 ipAddress = InetAddress.getLocalHost().toString().split("/");
		} catch (UnknownHostException e) {
			e.printStackTrace();
			return pbpmUrl;
		}
	     return "http://"+ipAddress[1]+":8080/designer/editor?profile=pbpm";
	}

	public static String getPbpmRepository() {
		return pbpmRepository;
	}

	public static String getPbpmPackage() {
		return pbpmPackage;
	}

	public static void createRepository() {
		File file = new File(pbpmRepository);

		if (!file.exists()) {
			file.mkdirs();
		}

		file = null;
	}

	public static void deleteFileFromRepository(String type, String extension) {
		File file = new File(pbpmRepository + "/" + pbpmPackage + "_" + type
				+ "." + extension);

		if (file.exists()) {
			file.delete();
		}

		file = null;
	}

	public static void createJsonFileInRepository(String type, String json)
			throws IOException {
		File file = new File(pbpmRepository + "/" + pbpmPackage + "_" + type
				+ ".json");

		FileUtils.writeStringToFile(file, json);

		file = null;
	}

	public static void writeJsonToFile(String type, String json)
			throws IOException {
		createRepository();

		deleteFileFromRepository(type, "json");
		deleteFileFromRepository(type, "bpmn");
		deleteFileFromRepository(type, "svg");

		createJsonFileInRepository(type, json);
	}

	public static boolean bpmnSaved(String type) {
		boolean saved = false;

		File file = new File(pbpmRepository + "/" + pbpmPackage + "_" + type
				+ ".bpmn");

		if (file.exists()) {
			saved = true;
		}

		file = null;
		return saved;
	}

	public static StAXOMBuilder getBpmnBuilder(String type)
			throws FileNotFoundException, IOException, XMLStreamException,
			FactoryConfigurationError {
		File file = new File(pbpmRepository + "/" + pbpmPackage + "_" + type
				+ ".bpmn");
		StringWriter bpmn = new StringWriter();

		IOUtils.copy(new FileInputStream(file), bpmn);

		ByteArrayInputStream stream = new ByteArrayInputStream(bpmn.toString()
				.getBytes());
		XMLStreamReader reader = XMLInputFactory.newInstance()
				.createXMLStreamReader(stream);
		StAXOMBuilder builder = new StAXOMBuilder(reader);

		file = null;
		return builder;
	}

	public static String getJsonDesign(String type)
			throws FileNotFoundException, IOException {
		File file = new File(pbpmRepository + "/" + pbpmPackage + "_" + type
				+ ".json");
		StringWriter json = new StringWriter();

		IOUtils.copy(new FileInputStream(file), json);

		file = null;
		return json.toString();
	}
	
	public String getAssignmentMethod(String taskId) {
		return getAssignmentMethod(getElementById(taskId));
	}

	@SuppressWarnings("unchecked")
	public String getAssignmentMethod(OMElement task) {
		String[] elements = UserTask.ASSIGNMENT.getElement().split("/");

		OMElement element = getElement(task, elements[0]);

		if (null == element) {
			return "";
		}

		Iterator<OMElement> iterator = element.getChildElements();
		String result = "";
		String value = "";

		while (iterator.hasNext()) {
			element = iterator.next();

			if (elements[1].equalsIgnoreCase(element.getLocalName())) {
				value = getElementContent(element, elements[2],
						Namespace.DROOLS.getUri());

				String[] actions = value.split("\\|");

				for (String action : actions) {
					String[] pair = action.split("=");

					if (elements[3].equals(pair[0])) {
						result = pair[1];
						break;
					}
				}
			}
		}

		return result;
	}

}