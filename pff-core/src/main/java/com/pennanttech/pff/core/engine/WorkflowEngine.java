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
package com.pennanttech.pff.core.engine;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringWriter;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.impl.builder.StAXOMBuilder;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.pennanttech.pff.core.model.workflow.SequenceFlow;
import com.pennanttech.pff.core.model.workflow.ServiceTask;
import com.pennanttech.pff.core.model.workflow.UserTask;
import com.pennanttech.pff.core.util.XmlUtil;

/**
 * Workflow engine that manages the business processes. It is a key component in workflow processing and makes use of
 * BPMN.
 */
public class WorkflowEngine {
	private static final Logger	logger					= Logger.getLogger(WorkflowEngine.class);

	private OMElement			definition;
	private String				namespaceURI;
	private OMElement			process;
	private String				actualFirstTaskId		= "";
	private String				actualFirstTaskActor	= "";
	private List<String>		firstTaskActors			= new ArrayList<>();

	/**
	 * Enumerates the namespaces that were used in the BPMN.
	 */
	private enum Namespace {
		DEFAULT("http://www.omg.org/spec/BPMN/20100524/MODEL"), DROOLS("http://www.jboss.org/drools");

		private String uri;

		private Namespace(String uri) {
			this.uri = uri;
		}

		private String getUri() {
			return uri;
		}
	}

	/**
	 * Enumerates the basic elements of the BPMN.
	 */
	public enum Element {
		startEvent, endEvent, userTask, serviceTask, exclusiveGateway, parallelGateway, sequenceFlow;
	}

	/**
	 * Creates the workflow model based on the BPMN process definition.
	 * 
	 * @param bpmn
	 *            The BPMN definition.
	 * @throws XMLStreamException
	 *             If the BPMN is not well-formed or cannot be processed.
	 * @throws FactoryConfigurationError
	 *             If the BPMN cannot be loaded.
	 */
	public WorkflowEngine(String bpmn) throws XMLStreamException, FactoryConfigurationError {
		ByteArrayInputStream stream = new ByteArrayInputStream(bpmn.getBytes(StandardCharsets.UTF_8));
		XMLStreamReader parser = XMLInputFactory.newInstance().createXMLStreamReader(stream);
		StAXOMBuilder builder = new StAXOMBuilder(parser);

		definition = builder.getDocumentElement();
		init();
	}

	/**
	 * Initializes the workflow engine.
	 */
	private void init() {
		// Set the default namespace URI and process element.
		namespaceURI = Namespace.DEFAULT.getUri();
		process = XmlUtil.getElement(definition, "process", namespaceURI);

		// Get the first task details.
		Iterator<OMElement> iterator = XmlUtil.getChildren(process, Element.startEvent.name(), namespaceURI);
		String startEventId;
		UserTask task;

		while (iterator.hasNext()) {
			startEventId = XmlUtil.getAttribute(iterator.next(), "id");

			for (String taskId : getNextTaskIds(Element.startEvent, startEventId, null)) {
				task = getUserTask(taskId);

				// Setting the first task in the document order to ensure when
				// the process (like default, collateral) doesn't have the
				// re-instate level specified.
				if (StringUtils.isBlank(actualFirstTaskId)) {
					actualFirstTaskId = task.getId();
					actualFirstTaskActor = task.getActor();
				}
				firstTaskActors.add(task.getActor());

				if (task.isReinstateLevel()) {
					actualFirstTaskId = task.getId();
					actualFirstTaskActor = task.getActor();
				}
			}
		}
	}

	/**
	 * Get the first user task's id. If multiple first tasks available within the process, the actual first user task
	 * will be considered.
	 * 
	 * @return The actual first user task's id.
	 */
	public String firstTaskId() {
		return actualFirstTaskId;
	}

	/**
	 * Get the first user task's actor. If multiple first tasks available within the process, the actual first user task
	 * will be considered.
	 * 
	 * @return The actual first user task's actor.
	 */
	public String firstTaskOwner() {
		return actualFirstTaskActor;
	}

	/**
	 * Get all the first user tasks' actors as a comma delimited string.
	 * 
	 * @return All the first user tasks' actors as a comma delimited string.
	 */
	public String allFirstTaskOwners() {
		return StringUtils.join(firstTaskActors, ',');
	}

	/**
	 * Get the actors (actual / delegator) those involved in the process.
	 * 
	 * @param delegator
	 *            If <code>true</code>, delegators will be returned; otherwise actual actors will be returned.
	 * @return The actors involved in the process.
	 */
	public List<String> getActors(boolean delegator) {
		List<String> result = new ArrayList<>();

		UserTask task;
		Iterator<OMElement> iterator = XmlUtil.getChildren(process, Element.userTask.name(), namespaceURI);

		while (iterator.hasNext()) {
			task = getUserTask(iterator.next(), null);

			if (delegator && task.isDelegator()) {
				result.add(task.getActor());
			} else if (!delegator && !task.isDelegator()) {
				result.add(task.getActor());
			}
		}

		return result;
	}

	/**
	 * Get the user task.
	 * 
	 * @param id
	 *            Id of the user task.
	 * @return The user task.
	 */
	public UserTask getUserTask(String id) {
		return getUserTask(XmlUtil.getElement(process, "id", id, Element.userTask.name()), id);
	}

	/**
	 * Get the user task id.
	 * 
	 * @param actor
	 *            Actor of the user task.
	 * @return The user task's id.
	 */
	public String getUserTaskId(String actor) {
		String result = null;

		Iterator<OMElement> iterator = XmlUtil.getChildren(process, Element.userTask.name(), namespaceURI);

		while (iterator.hasNext()) {
			UserTask task = getUserTask(iterator.next(), null);

			if (actor.equals(task.getActor())) {
				result = task.getId();
				break;
			}
		}

		return result;
	}

	/**
	 * Get the user actions of a user task.
	 * 
	 * @param taskId
	 *            Id of the user task.
	 * @return The user actions of the task.
	 */
	public String getUserActions(String taskId) {
		return StringUtils.join(getUserActions(Element.userTask, taskId), "/");
	}

	/**
	 * Get the user actions of an element.
	 * 
	 * @param element
	 *            Type of the element.
	 * @param id
	 *            Id of the element.
	 * @return The user actions of the element.
	 */
	protected List<String> getUserActions(Element element, String id) {
		List<String> result = new ArrayList<>();

		for (SequenceFlow flow : getSequenceFlows(element, id)) {
			if (flow.isUserAction()) {
				result.add(flow.getActionAsString());
			}

			OMElement target = XmlUtil.getElement(process, "id", flow.getTargetRef(), null);
			Element targetElement = Element.valueOf(target.getLocalName());

			// Drill down further in the flow.
			switch (targetElement) {
			case serviceTask:
			case exclusiveGateway:
			case parallelGateway:
				result.addAll(getUserActions(targetElement, flow.getTargetRef()));
				break;
			default:
				break;
			}
		}

		return result;
	}

	/**
	 * Get the service operations for the user action.
	 * 
	 * @param taskId
	 *            Id of the user task.
	 * @param object
	 *            Model entity that contains the parameters to find the path.
	 * @return The service operations for the user action.
	 */
	public String getServiceOperations(String taskId, Object object) {
		return StringUtils.join(getServiceOperations(Element.userTask, taskId, object), ";");
	}

	/**
	 * Get the service operations for the user action.
	 * 
	 * @param element
	 *            Type of the element.
	 * @param id
	 *            Id of the element.
	 * @param object
	 *            Model entity that contains the parameters to find the path.
	 * @return The service operations for the user action.
	 */
	protected List<String> getServiceOperations(Element element, String id, Object object) {
		List<String> result = new ArrayList<>();

		for (SequenceFlow flow : getSequenceFlows(element, id)) {
			OMElement target = XmlUtil.getElement(process, "id", flow.getTargetRef(), null);
			Element targetElement = Element.valueOf(target.getLocalName());

			// Add the service operation and drill down further in the flow.
			if (eval(flow.getConditionExpression(), object)) {
				switch (targetElement) {
				case serviceTask:
					result.add(getServiceTask(target, flow.getTargetRef()).getOperation());
				case exclusiveGateway:
				case parallelGateway:
					result.addAll(getServiceOperations(targetElement, flow.getTargetRef(), object));
					break;
				default:
					break;
				}
			}
		}

		return result;
	}

	/**
	 * Get the next task identifiers for the user action.
	 * 
	 * @param taskId
	 *            Id of the user task.
	 * @param object
	 *            Model entity that contains the parameters to find the path.
	 * @return The next task identifiers for the user action.
	 */
	public String getNextTaskIds(String taskId, Object object) {
		return StringUtils.join(getNextTaskIds(Element.userTask, taskId, object), ";");
	}

	/**
	 * Get the next task identifiers for the user action.
	 * 
	 * @param element
	 *            Type of the element.
	 * @param id
	 *            Id of the element.
	 * @param object
	 *            Model entity that contains the parameters to find the path.
	 * @return The next task identifiers for the user action.
	 */
	protected List<String> getNextTaskIds(Element element, String id, Object object) {
		List<String> result = new ArrayList<>();

		for (SequenceFlow flow : getSequenceFlows(element, id)) {
			OMElement target = XmlUtil.getElement(process, "id", flow.getTargetRef(), null);
			Element targetElement = Element.valueOf(target.getLocalName());

			// Add the user task id and drill down further in the flow.
			if (eval(flow.getConditionExpression(), object)) {
				switch (targetElement) {
				case userTask:
					result.add(flow.getTargetRef());
					break;
				case serviceTask:
				case exclusiveGateway:
				case parallelGateway:
					result.addAll(getNextTaskIds(targetElement, flow.getTargetRef(), object));
					break;
				default:
					break;
				}
			}
		}

		return result;
	}

	/**
	 * Gets whether notes is mandatory for the user action.
	 * 
	 * @param taskId
	 *            Id of the user task.
	 * @param object
	 *            Model entity that contains the parameters to find the path.
	 * @return Whether notes is mandatory for the user action.
	 */
	public boolean getAuditingReq(String taskId, Object object) {
		return !getAuditingReq(Element.userTask, taskId, object).isEmpty();
	}

	/**
	 * Gets whether notes is mandatory for the user action.
	 * 
	 * @param element
	 *            Type of the element.
	 * @param id
	 *            Id of the element.
	 * @param object
	 *            Model entity that contains the parameters to find the path.
	 * @return Whether notes is mandatory for the user action.
	 */
	protected List<String> getAuditingReq(Element element, String id, Object object) {
		List<String> result = new ArrayList<>();

		for (SequenceFlow flow : getSequenceFlows(element, id)) {
			OMElement target = XmlUtil.getElement(process, "id", flow.getTargetRef(), null);
			Element targetElement = Element.valueOf(target.getLocalName());

			// Add whether notes mandatory or not and drill down further in the
			// flow.
			if (eval(flow.getConditionExpression(), object)) {
				if (flow.isNotesMandatory()) {
					result.add("Notes");
					break;
				}

				switch (targetElement) {
				case serviceTask:
				case exclusiveGateway:
				case parallelGateway:
					result.addAll(getAuditingReq(targetElement, flow.getTargetRef(), object));
					break;
				default:
					break;
				}
			}
		}

		return result;
	}

	/**
	 * Get the user task.
	 * 
	 * @param element
	 *            Element of the user task.
	 * @return The user task.
	 */
	protected UserTask getUserTask(OMElement element, String id) {
		if (id == null) {
			id = XmlUtil.getAttribute(element, "id");
		}

		// Get Actor
		String actor = XmlUtil.getElementText(element, "potentialOwner/resourceAssignmentExpression/formalExpression",
				namespaceURI);

		// Get Additional Forms + Assignment Level + Base Actor.
		String assignmentLevel = null;
		String baseActor = null;
		String[] additionalForms = null;

		OMElement extensionElements = XmlUtil.getElement(element, "extensionElements", namespaceURI);
		if (extensionElements != null) {
			String onEntryScript = XmlUtil.getElementText(extensionElements, "onEntry-script/script",
					Namespace.DROOLS.getUri());
			if (onEntryScript != null) {
				String value = getValue(onEntryScript, "\\|", "show_tabs");
				if (value != null) {
					additionalForms = StringUtils.split(value, ",");
				}
			}

			String onExitScript = XmlUtil.getElementText(extensionElements, "onExit-script/script",
					Namespace.DROOLS.getUri());
			if (onExitScript != null) {
				assignmentLevel = getValue(onExitScript, "\\|", "assignment");
				baseActor = getValue(onExitScript, "\\|", "baseRole");
			}
		}

		// Get Delegator
		String documentation = XmlUtil.getElementText(element, "documentation", namespaceURI);

		UserTask task = new UserTask();
		task.setId(id);
		task.setActor(actor);
		task.setAssignmentLevel(assignmentLevel);
		task.setBaseActor(baseActor);
		if (additionalForms != null) {
			task.setAdditionalForms(Arrays.asList(additionalForms));
		}
		task.setDelegator(documentation != null && "delegator".equals(documentation) ? true : false);
		task.setReinstateLevel(documentation != null && "additional_participant".equals(documentation) ? false : true);

		return task;
	}

	/**
	 * Get the service task.
	 * 
	 * @param element
	 *            Element of the service task.
	 * @param id
	 *            Id of the service task.
	 * @return The service task.
	 */
	protected ServiceTask getServiceTask(OMElement element, String id) {
		// Get service operation.
		String operation = XmlUtil.getAttribute(element, "servicetaskoperation", "http://www.jboss.org/drools",
				"drools");

		ServiceTask task = new ServiceTask();
		task.setId(id);
		task.setOperation(operation);

		return task;
	}

	/**
	 * Get the sequence flows of the specified element.
	 * 
	 * @param element
	 *            Type of element (1st level child of the process).
	 * @param id
	 *            Id of the element for which the sequence flows to be fetched.
	 * @return The sequence flows of the specified element.
	 */
	protected List<SequenceFlow> getSequenceFlows(Element element, String id) {
		List<SequenceFlow> flows = new ArrayList<>();

		OMElement sourceElement = XmlUtil.getElement(process, "id", id, element.name());

		String sequenceId;
		OMElement sequenceFlow;
		String documentation;
		boolean userAction;
		String action;
		String state;
		OMElement child;
		SequenceFlow flow;
		Iterator<OMElement> iterator = XmlUtil.getChildren(sourceElement);

		while (iterator.hasNext()) {
			child = iterator.next();

			if ("outgoing".equals(child.getLocalName())) {
				// Get the sequence flow.
				sequenceId = StringUtils.trimToEmpty(child.getText());
				sequenceFlow = XmlUtil.getElement(process, "id", sequenceId, Element.sequenceFlow.name());

				// Get the user action attributes.
				documentation = null;
				userAction = false;
				action = null;
				state = null;

				documentation = XmlUtil.getElementText(sequenceFlow, "documentation", namespaceURI);
				if (documentation != null) {
					userAction = true;

					String[] userActionAttributes = documentation.split("=");
					action = userActionAttributes[0];
					state = userActionAttributes[1];
				}

				flow = new SequenceFlow();
				flow.setId(sequenceId);
				flow.setUserAction(userAction);
				flow.setAction(action);
				flow.setState(state);
				flow.setConditionExpression(XmlUtil.getElementText(sequenceFlow, "conditionExpression", namespaceURI));
				flow.setNotesMandatory(StringUtils
						.isNotBlank(XmlUtil.getElementText(sequenceFlow, "auditing/documentation", namespaceURI)));
				flow.setTargetRef(XmlUtil.getAttribute(sequenceFlow, "targetRef"));

				flows.add(flow);
			}
		}

		return flows;
	}

	/**
	 * Executes the specified script.
	 * 
	 * @param exp
	 *            The script language source to be executed.
	 * @param object
	 *            The model to be used by the script.
	 * @return The value returned from the execution of the script. <code>true</code> if the argument is null.
	 */
	protected boolean eval(String exp, Object object) {
		if (StringUtils.isBlank(exp) || object == null) {
			return true;
		}

		// Unescape XML characters.
		exp = exp.replaceAll("&amp;", "&");
		exp = exp.replaceAll("&gt;", ">");
		exp = exp.replaceAll("&lt;", "<");

		ScriptEngineManager factory = new ScriptEngineManager();
		ScriptEngine engine = factory.getEngineByName("JavaScript");

		engine.put("vo", object);

		try {
			return (Boolean) engine.eval(exp);
		} catch (ScriptException e) {
			logger.warn("Exception: ", e);
			return false;
		}
	}

	/**
	 * Returns the value to which the specified key is mapped from the key-value mappings string, or null if no mapping
	 * available for the key.
	 * 
	 * @param source
	 *            The key-value mappings string.
	 * @param regex
	 *            The delimiting regular expression.
	 * @param key
	 *            The key whose associated value to be returned.
	 * @return The value to which the specified key is mapped, or null if no mapping available for the key.
	 */
	protected String getValue(String source, String regex, String key) {
		String result = null;

		for (String item : source.split(regex)) {
			String[] mapping = StringUtils.trimToEmpty(item).split("=");

			if (key.equals(StringUtils.trimToEmpty(mapping[0]))) {
				result = StringUtils.trimToEmpty(mapping[1]);
				break;
			}
		}

		return result;
	}

	// *****************************************************************
	// ************ PBPM Designer ************
	// *****************************************************************
	private static final String	pbpmUrl			= "http://localhost:8080/designer/editor?profile=pbpm";
	private static final String	pbpmRepository	= "C:/pbpm/designer/repository";
	private static final String	pbpmPackage		= "PFS";

	public WorkflowEngine(StAXOMBuilder builder) {
		definition = builder.getDocumentElement();

		init();
	}

	public static String getPbpmUrl() {
		String ipAddress[] = new String[2];
		try {
			ipAddress = InetAddress.getLocalHost().toString().split("/");
		} catch (UnknownHostException e) {
			logger.warn("Exception: ", e);
			return pbpmUrl;
		}
		return "http://" + ipAddress[1] + ":8080/designer/editor?profile=pbpm";
	}

	public static String getPbpmRepository() {
		return pbpmRepository;
	}

	public static String getPbpmPackage() {
		return pbpmPackage;
	}

	private static void createRepository() {
		File file = new File(pbpmRepository);

		if (!file.exists()) {
			file.mkdirs();
		}

		file = null;
	}

	private static void deleteFileFromRepository(String type, String extension) {
		File file = new File(pbpmRepository + "/" + pbpmPackage + "_" + type + "." + extension);

		if (file.exists()) {
			file.delete();
		}

		file = null;
	}

	private static void createJsonFileInRepository(String type, String json) throws IOException {
		File file = new File(pbpmRepository + "/" + pbpmPackage + "_" + type + ".json");

		FileUtils.writeStringToFile(file, json);

		file = null;
	}

	public static void writeJsonToFile(String type, String json) throws IOException {
		createRepository();

		deleteFileFromRepository(type, "json");
		deleteFileFromRepository(type, "bpmn");
		deleteFileFromRepository(type, "svg");

		createJsonFileInRepository(type, json);
	}

	public static boolean bpmnSaved(String type) {
		boolean saved = false;

		File file = new File(pbpmRepository + "/" + pbpmPackage + "_" + type + ".bpmn");

		if (file.exists()) {
			saved = true;
		}

		file = null;
		return saved;
	}

	public static StAXOMBuilder getBpmnBuilder(String type)
			throws FileNotFoundException, IOException, XMLStreamException, FactoryConfigurationError {
		File file = new File(pbpmRepository + "/" + pbpmPackage + "_" + type + ".bpmn");
		StringWriter bpmn = new StringWriter();

		IOUtils.copy(new FileInputStream(file), bpmn);

		ByteArrayInputStream stream = new ByteArrayInputStream(bpmn.toString().getBytes());
		XMLStreamReader reader = XMLInputFactory.newInstance().createXMLStreamReader(stream);
		StAXOMBuilder builder = new StAXOMBuilder(reader);

		file = null;
		return builder;
	}

	public static String getJsonDesign(String type) throws FileNotFoundException, IOException {
		File file = new File(pbpmRepository + "/" + pbpmPackage + "_" + type + ".json");
		StringWriter json = new StringWriter();

		IOUtils.copy(new FileInputStream(file), json);

		file = null;
		return json.toString();
	}
	// *****************************************************************
	// ************ PBPM Designer ************
	// *****************************************************************
}
