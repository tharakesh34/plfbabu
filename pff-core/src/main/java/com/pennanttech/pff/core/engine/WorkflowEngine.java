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
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.impl.builder.StAXOMBuilder;
import org.apache.commons.lang.StringUtils;

import com.pennanttech.pff.core.FactoryException;
import com.pennanttech.pff.core.model.workflow.SequenceFlow;
import com.pennanttech.pff.core.model.workflow.ServiceTask;
import com.pennanttech.pff.core.model.workflow.UserTask;
import com.pennanttech.pff.core.util.XmlUtil;

/**
 * Workflow engine that manages the business processes. It is a key component in workflow processing and makes use of
 * BPMN.
 */
public class WorkflowEngine {
	private OMElement		definition;
	private String			namespaceURI;
	private OMElement		process;
	private String			actualFirstTaskId		= "";
	private String			actualFirstTaskActor	= "";
	private List<String>	firstTaskActors			= new ArrayList<>();

	/**
	 * Enumerates the namespaces that were used in the BPMN.
	 */
	private enum Namespace {
		DEFAULT("http://www.omg.org/spec/BPMN/20100524/MODEL"), ACTIVITI("http://activiti.org/bpmn");

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
	 * @throws FactoryException
	 *             If the BPMN cannot be loaded or the BPMN is not well-formed or cannot be processed.
	 */
	public WorkflowEngine(String bpmn) {
		try {
			ByteArrayInputStream stream = new ByteArrayInputStream(bpmn.getBytes(StandardCharsets.UTF_8));
			XMLStreamReader parser = XMLInputFactory.newInstance().createXMLStreamReader(stream);
			StAXOMBuilder builder = new StAXOMBuilder(parser);

			definition = builder.getDocumentElement();
			init();
		} catch (Exception e) {
			throw new FactoryException("workflow", e);
		}
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
		String actor = XmlUtil.getAttribute(element, "actor", Namespace.ACTIVITI.getUri(), "activiti");

		// Get Additional Forms + Assignment Level + Base Actor.
		String assignmentLevel = null;
		String baseActor = null;
		String[] additionalForms = null;

		String forms = XmlUtil.getAttribute(element, "additionalForms", Namespace.ACTIVITI.getUri(), "activiti");
		if (forms != null) {
			additionalForms = forms.split("\\|");
		}

		assignmentLevel = XmlUtil.getAttribute(element, "assignmentLevel", Namespace.ACTIVITI.getUri(), "activiti");
		baseActor = XmlUtil.getAttribute(element, "baseActor", Namespace.ACTIVITI.getUri(), "activiti");

		// Get Delegator
		String delegator = XmlUtil.getAttribute(element, "delegator", Namespace.ACTIVITI.getUri(), "activiti");

		// Get Reinstate Level
		String reinstateLevel = XmlUtil.getAttribute(element, "reinstateLevel", Namespace.ACTIVITI.getUri(),
				"activiti");

		UserTask task = new UserTask();
		task.setId(id);
		task.setActor(actor);
		task.setAssignmentLevel(assignmentLevel);
		task.setBaseActor(baseActor);
		if (additionalForms != null) {
			task.setAdditionalForms(Arrays.asList(additionalForms));
		}
		task.setDelegator(delegator != null && delegator.equals("true") ? true : false);
		task.setReinstateLevel(reinstateLevel != null && reinstateLevel.equals("true") ? true : true);

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
		String operation = XmlUtil.getAttribute(element, "operation", Namespace.ACTIVITI.getUri(), "activiti");

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

		SequenceFlow flow = null;
		OMElement child;
		Iterator<OMElement> iterator = XmlUtil.getChildren(process);

		while (iterator.hasNext()) {
			child = iterator.next();

			if (Element.sequenceFlow.name().equals(child.getLocalName())) {
				if (XmlUtil.getAttribute(child, "sourceRef").equals(id)) {
					flow = new SequenceFlow();
					flow.setId(XmlUtil.getAttribute(child, "id"));
					flow.setUserAction(Boolean.valueOf(XmlUtil.getAttribute(child, "representsUserAction")));
					flow.setAction(XmlUtil.getAttribute(child, "action"));
					flow.setState(XmlUtil.getAttribute(child, "state"));
					flow.setConditionExpression(XmlUtil.getElementText(child, "conditionExpression", namespaceURI));
					flow.setNotesMandatory(Boolean.valueOf(XmlUtil.getAttribute(child, "mandateNotes")));
					flow.setTargetRef(XmlUtil.getAttribute(child, "targetRef"));

					flows.add(flow);
				}
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
		String script = exp;
		script = script.replaceAll("&amp;", "&");
		script = script.replaceAll("&gt;", ">");
		script = script.replaceAll("&lt;", "<");

		ScriptEngineManager factory = new ScriptEngineManager();
		ScriptEngine engine = factory.getEngineByName("JavaScript");

		engine.put("vo", object);

		try {
			return (Boolean) engine.eval(script);
		} catch (Exception e) {
			throw new FactoryException("workflow", e);
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
}
