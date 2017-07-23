package com.pennanttech.pff.core.engine;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLStreamException;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.pennanttech.pennapps.core.engine.workflow.WorkflowEngine;
import com.pennanttech.pennapps.core.engine.workflow.WorkflowEngine.Element;
import com.pennanttech.pennapps.core.engine.workflow.model.SequenceFlow;
import com.pennanttech.pennapps.core.engine.workflow.model.UserTask;

public class WorkflowEngineTest {
	WorkflowEngine engine;
	WorkflowEngine engine2;

	public class TestEntity {
		private String recordStatus;
		private String approved;

		public TestEntity(String recordStatus, String approved, boolean shariaApprovalReq) {
			this.recordStatus = recordStatus;
			this.approved = approved;
		}

		public String getRecordStatus() {
			return recordStatus;
		}

		public String getApproved() {
			return approved;
		}
	}

	@BeforeClass
	public void setUp() throws IOException, XMLStreamException, FactoryConfigurationError {
		// Load the sample process to test.
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		String bpmn = IOUtils.toString(loader.getResourceAsStream("SampleProcess.xml"));
		String bpmn2 = IOUtils.toString(loader.getResourceAsStream("ComplexProcess.xml"));

		engine = new WorkflowEngine(bpmn);
		engine2 = new WorkflowEngine(bpmn2);
	}

	@Test
	public void getFirstTask() {
		Assert.assertEquals(engine.firstTaskId(), "sid-38272C15-118D-4D4F-80E0-DD3A3FEC8897");
	}

	@Test
	public void getFirstTask2() {
		Assert.assertEquals(engine2.firstTaskId(), "sid-43C4D486-53F4-43EE-8890-6E0B39D9FBEE");
	}

	@Test
	public void firstTaskOwner() {
		Assert.assertEquals(engine.firstTaskOwner(), "MSTGRP1_MAKER");
	}

	@Test
	public void firstTaskOwner2() {
		Assert.assertEquals(engine2.firstTaskOwner(), "RECEIPT_MAKER");
	}

	@Test
	public void allFirstTaskOwners() {
		Assert.assertEquals(engine.allFirstTaskOwners(), "MSTGRP1_MAKER");
	}

	@Test
	public void allFirstTaskOwners2() {
		Assert.assertEquals(engine2.allFirstTaskOwners(), "RECEIPT_MAKER");
	}

	@Test
	public void getActors() {
		Assert.assertEquals(StringUtils.join(engine.getActors(false), ';'), "MSTGRP1_MAKER;MSTGRP1_APPROVER");
	}

	@Test
	public void getActors2() {
		Assert.assertEquals(StringUtils.join(engine2.getActors(false), ';'),
				"RECEIPT_MAKER;RECEIPT_APPROVER;DEPOSIT_MAKER;" + "DEPOSIT_APPROVER;RECEIPTWAIVER_APPROVER;"
						+ "REALIZATION_MAKER;REALIZATION_APPROVER");
	}

	@Test
	public void getDelegators() {
		Assert.assertEquals(StringUtils.join(engine.getActors(true), ';'), "");
	}

	@Test
	public void getDelegators2() {
		Assert.assertEquals(StringUtils.join(engine2.getActors(true), ';'), "");
	}

	@Test
	public void getUserTaskId() {
		Assert.assertEquals(engine.getUserTaskId("MSTGRP1_MAKER"), "sid-38272C15-118D-4D4F-80E0-DD3A3FEC8897");
		Assert.assertEquals(engine.getUserTaskId("MSTGRP1_APPROVER"), "sid-0E4577E1-11E1-450D-A2B6-DBE242378F08");
	}

	@Test
	public void getUserTaskId2() {
		Assert.assertEquals(engine2.getUserTaskId("RECEIPT_MAKER"), "sid-43C4D486-53F4-43EE-8890-6E0B39D9FBEE");
		Assert.assertEquals(engine2.getUserTaskId("RECEIPT_APPROVER"), "sid-0B14EAF0-64A0-4C4E-9F83-A658A77B1421");
		Assert.assertEquals(engine2.getUserTaskId("DEPOSIT_MAKER"), "sid-9C3D9CD0-BD69-4240-BBF7-1E4CF9E303AA");
		Assert.assertEquals(engine2.getUserTaskId("DEPOSIT_APPROVER"), "sid-A1774BE8-3BC9-4249-91CC-70326C5A97FD");
		Assert.assertEquals(engine2.getUserTaskId("RECEIPTWAIVER_APPROVER"),
				"sid-888F5A2E-3E41-49B5-9530-77AA7FC8D402");
		Assert.assertEquals(engine2.getUserTaskId("REALIZATION_MAKER"), "sid-DEB8C0BE-00D7-4F42-BB01-F8B7C348860B");
		Assert.assertEquals(engine2.getUserTaskId("REALIZATION_APPROVER"), "sid-372D9265-9B9D-4DE6-8169-A9CA3778968C");
	}

	@Test
	public void getUserActions() {
		Assert.assertEquals(engine.getUserActions("sid-38272C15-118D-4D4F-80E0-DD3A3FEC8897"),
				"Submit=Submitted/Cancel=Cancelled");
		Assert.assertEquals(engine.getUserActions("sid-0E4577E1-11E1-450D-A2B6-DBE242378F08"),
				"Resubmit=Resubmitted/Approve=Approved/Reject=Rejected");
	}

	@Test
	public void getUserActions2() {
		Assert.assertEquals(engine2.getUserActions("sid-43C4D486-53F4-43EE-8890-6E0B39D9FBEE"), "Submit=Submitted");
		Assert.assertEquals(engine2.getUserActions("sid-0B14EAF0-64A0-4C4E-9F83-A658A77B1421"),
				"Resubmit=Resubmitted/Approve=Approved/Reject=Rejected");
		Assert.assertEquals(engine2.getUserActions("sid-9C3D9CD0-BD69-4240-BBF7-1E4CF9E303AA"),
				"Reject=Rejected/Submit=Submitted/Resubmit=Resubmitted");
		Assert.assertEquals(engine2.getUserActions("sid-A1774BE8-3BC9-4249-91CC-70326C5A97FD"),
				"Approve=Approved/Resubmit=Resubmitted");
		Assert.assertEquals(engine2.getUserActions("sid-888F5A2E-3E41-49B5-9530-77AA7FC8D402"),
				"Submit=Submitted/Resubmit=Resubmitted");
		Assert.assertEquals(engine2.getUserActions("sid-DEB8C0BE-00D7-4F42-BB01-F8B7C348860B"),
				"Reject=Rejected/Submit=Submitted");
		Assert.assertEquals(engine2.getUserActions("sid-372D9265-9B9D-4DE6-8169-A9CA3778968C"),
				"Resubmit=Resubmitted/Approve=Approved/Reject=Rejected");
	}

	@Test
	public void getServiceOperations() {
		Assert.assertEquals(engine.getServiceOperations("sid-38272C15-118D-4D4F-80E0-DD3A3FEC8897",
				new TestEntity("Submitted", null, false)), "");
		Assert.assertEquals(engine.getServiceOperations("sid-38272C15-118D-4D4F-80E0-DD3A3FEC8897",
				new TestEntity("Cancelled", null, false)), "doReject");
		Assert.assertEquals(engine.getServiceOperations("sid-0E4577E1-11E1-450D-A2B6-DBE242378F08",
				new TestEntity("Approved", null, false)), "doApprove");
		Assert.assertEquals(engine.getServiceOperations("sid-0E4577E1-11E1-450D-A2B6-DBE242378F08",
				new TestEntity("Resubmitted", null, false)), "");
		Assert.assertEquals(engine.getServiceOperations("sid-0E4577E1-11E1-450D-A2B6-DBE242378F08",
				new TestEntity("Rejected", null, false)), "doReject");
	}

	@Test
	public void getServiceOperations2() {
		Assert.assertEquals(engine2.getServiceOperations("sid-43C4D486-53F4-43EE-8890-6E0B39D9FBEE",
				new TestEntity("Resubmitted", null, false)), "");
		Assert.assertEquals(engine2.getServiceOperations("sid-0B14EAF0-64A0-4C4E-9F83-A658A77B1421",
				new TestEntity("Approved", null, true)), "doApprove");
		Assert.assertEquals(engine2.getServiceOperations("sid-372D9265-9B9D-4DE6-8169-A9CA3778968C",
				new TestEntity("Approved", null, false)), "doApprove");
	}

	@SuppressWarnings("unchecked")
	@Test
	public void getNextTaskIdsInternal()
			throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
		final Method getNextTaskIds = WorkflowEngine.class.getDeclaredMethod("getNextTaskIds", Element.class,
				String.class, Object.class);
		getNextTaskIds.setAccessible(true);

		// Start Event
		String result = StringUtils
				.join((List<String>) getNextTaskIds.invoke(engine2, Element.startEvent, "startEvent1", null), ";");
		Assert.assertEquals(result, "sid-43C4D486-53F4-43EE-8890-6E0B39D9FBEE");
		// User Task 
		result = StringUtils.join((List<String>) getNextTaskIds.invoke(engine2, Element.userTask,
				"sid-A1774BE8-3BC9-4249-91CC-70326C5A97FD", null), ";");
		Assert.assertEquals(result,
				"sid-DEB8C0BE-00D7-4F42-BB01-F8B7C348860B;sid-9C3D9CD0-BD69-4240-BBF7-1E4CF9E303AA");

		result = StringUtils.join((List<String>) getNextTaskIds.invoke(engine2, Element.userTask,
				"sid-43C4D486-53F4-43EE-8890-6E0B39D9FBEE", new TestEntity("Cancelled", null, false)), ";");
		Assert.assertEquals(result, "");

		result = StringUtils.join((List<String>) getNextTaskIds.invoke(engine2, Element.userTask,
				"sid-43C4D486-53F4-43EE-8890-6E0B39D9FBEE", new TestEntity("Submitted", null, false)), ";");
		Assert.assertEquals(result,
				"sid-0B14EAF0-64A0-4C4E-9F83-A658A77B1421;sid-888F5A2E-3E41-49B5-9530-77AA7FC8D402");
	}

	@Test
	public void getNextTaskIds() {
		Assert.assertEquals(engine.getNextTaskIds("sid-38272C15-118D-4D4F-80E0-DD3A3FEC8897",
				new TestEntity("Submitted", null, false)), "sid-0E4577E1-11E1-450D-A2B6-DBE242378F08");
		Assert.assertEquals(engine.getNextTaskIds("sid-38272C15-118D-4D4F-80E0-DD3A3FEC8897",
				new TestEntity("Cancelled", null, false)), "");
		Assert.assertEquals(engine.getNextTaskIds("sid-0E4577E1-11E1-450D-A2B6-DBE242378F08",
				new TestEntity("Approved", null, false)), "");
		Assert.assertEquals(engine.getNextTaskIds("sid-0E4577E1-11E1-450D-A2B6-DBE242378F08",
				new TestEntity("Resubmitted", null, false)), "sid-38272C15-118D-4D4F-80E0-DD3A3FEC8897");
		Assert.assertEquals(engine.getNextTaskIds("sid-0E4577E1-11E1-450D-A2B6-DBE242378F08",
				new TestEntity("Rejected", null, false)), "");
	}

	@Test
	public void getNextTaskIds2() {
		Assert.assertEquals(engine2.getNextTaskIds("sid-9C3D9CD0-BD69-4240-BBF7-1E4CF9E303AA",
				new TestEntity("Resubmitted", null, false)), "sid-43C4D486-53F4-43EE-8890-6E0B39D9FBEE");
		Assert.assertEquals(engine2.getNextTaskIds("sid-0B14EAF0-64A0-4C4E-9F83-A658A77B1421",
				new TestEntity("Approved", null, true)), "");
		Assert.assertEquals(engine2.getNextTaskIds("sid-A1774BE8-3BC9-4249-91CC-70326C5A97FD",
				new TestEntity("Approved", null, false)), "sid-DEB8C0BE-00D7-4F42-BB01-F8B7C348860B");
	}

	@Test
	public void getAuditingReq() {
		Assert.assertFalse(engine.getAuditingReq("sid-38272C15-118D-4D4F-80E0-DD3A3FEC8897",
				new TestEntity("Submitted", null, false)));
		Assert.assertTrue(engine.getAuditingReq("sid-38272C15-118D-4D4F-80E0-DD3A3FEC8897",
				new TestEntity("Cancelled", null, false)));
		Assert.assertFalse(engine.getAuditingReq("sid-0E4577E1-11E1-450D-A2B6-DBE242378F08",
				new TestEntity("Approved", null, false)));
		Assert.assertTrue(engine.getAuditingReq("sid-0E4577E1-11E1-450D-A2B6-DBE242378F08",
				new TestEntity("Resubmitted", null, false)));
		Assert.assertTrue(engine.getAuditingReq("sid-0E4577E1-11E1-450D-A2B6-DBE242378F08",
				new TestEntity("Rejected", null, false)));
	}

	@Test
	public void getAuditingReq2() {
		Assert.assertTrue(engine2.getAuditingReq("sid-A1774BE8-3BC9-4249-91CC-70326C5A97FD",
				new TestEntity("Resubmitted", null, false)));
		Assert.assertFalse(engine2.getAuditingReq("_B158B71E-1D24-455A-9ADF-B1A0A2EAE064",
				new TestEntity("Approved", null, true)));
		Assert.assertFalse(engine2.getAuditingReq("_B158B71E-1D24-455A-9ADF-B1A0A2EAE064",
				new TestEntity("Approved", null, false)));
	}

	@Test
	public void getUserTask() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
		final Method getUserTask = WorkflowEngine.class.getDeclaredMethod("getUserTask", String.class);
		getUserTask.setAccessible(true);

		// Maker
		UserTask task = (UserTask) getUserTask.invoke(engine, "sid-38272C15-118D-4D4F-80E0-DD3A3FEC8897");
		Assert.assertEquals(task.getActor(), "MSTGRP1_MAKER");

		// Checker
		task = (UserTask) getUserTask.invoke(engine, "sid-0E4577E1-11E1-450D-A2B6-DBE242378F08");
		Assert.assertEquals(task.getActor(), "MSTGRP1_APPROVER");
	}

	@Test
	public void getUserTaskAssignmentLevel() {
		Assert.assertEquals(engine2.getUserTask("sid-888F5A2E-3E41-49B5-9530-77AA7FC8D402").getAssignmentLevel(),
				"Role Queue");
		Assert.assertEquals(engine2.getUserTask("sid-43C4D486-53F4-43EE-8890-6E0B39D9FBEE").getAssignmentLevel(),
				"Role Queue");
	}

	@Test
	public void getUserTaskAdditionalForms() {
		Assert.assertEquals(StringUtils
				.join(engine2.getUserTask("sid-A1774BE8-3BC9-4249-91CC-70326C5A97FD").getAdditionalForms(), ','), "");
		Assert.assertEquals(StringUtils
				.join(engine2.getUserTask("sid-888F5A2E-3E41-49B5-9530-77AA7FC8D402").getAdditionalForms(), ','),
				"Accounting");
	}

	@Test
	public void getUserTaskBaseActor() {
		Assert.assertEquals(engine2.getUserTask("sid-43C4D486-53F4-43EE-8890-6E0B39D9FBEE").getBaseActor(), null);
		Assert.assertEquals(engine2.getUserTask("sid-9C3D9CD0-BD69-4240-BBF7-1E4CF9E303AA").getBaseActor(), null);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void getSequenceFlows() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
		final Method getSequenceFlows = WorkflowEngine.class.getDeclaredMethod("getSequenceFlows", Element.class,
				String.class);
		getSequenceFlows.setAccessible(true);

		// Start Event
		List<SequenceFlow> flows = (List<SequenceFlow>) getSequenceFlows.invoke(engine, Element.startEvent,
				"startEvent1");
		Assert.assertEquals(flows.size(), 1);

		Assert.assertEquals(flows.get(0).getId(), "sid-CB1DEE00-A9A1-4162-BD4A-CC3D4B6E7CC1");
		Assert.assertEquals(flows.get(0).isUserAction(), false);
		Assert.assertEquals(flows.get(0).getConditionExpression(), null);
		Assert.assertEquals(flows.get(0).getAction(), null);
		Assert.assertEquals(flows.get(0).getState(), null);
		Assert.assertEquals(flows.get(0).isNotesMandatory(), false);
		Assert.assertEquals(flows.get(0).getTargetRef(), "sid-38272C15-118D-4D4F-80E0-DD3A3FEC8897");

		// User Task - Maker
		flows = (List<SequenceFlow>) getSequenceFlows.invoke(engine, Element.userTask,
				"sid-38272C15-118D-4D4F-80E0-DD3A3FEC8897");
		Assert.assertEquals(flows.size(), 2);

		Assert.assertEquals(flows.get(0).getId(), "sid-F8AD09C4-9AD3-4E9F-9432-0B753ACDBFF3");
		Assert.assertEquals(flows.get(0).isUserAction(), true);
		Assert.assertEquals(flows.get(0).getConditionExpression(), "vo.recordStatus == 'Submitted'");
		Assert.assertEquals(flows.get(0).getAction(), "Submit");
		Assert.assertEquals(flows.get(0).getState(), "Submitted");
		Assert.assertEquals(flows.get(0).isNotesMandatory(), false);
		Assert.assertEquals(flows.get(0).getTargetRef(), "sid-0E4577E1-11E1-450D-A2B6-DBE242378F08");

		Assert.assertEquals(flows.get(1).getId(), "sid-751124CB-1CC1-40AF-B5DB-360706ED6035");
		Assert.assertEquals(flows.get(1).isUserAction(), true);
		Assert.assertEquals(flows.get(1).getConditionExpression(), "vo.recordStatus == 'Cancelled'");
		Assert.assertEquals(flows.get(1).getAction(), "Cancel");
		Assert.assertEquals(flows.get(1).getState(), "Cancelled");
		Assert.assertEquals(flows.get(1).isNotesMandatory(), true);
		Assert.assertEquals(flows.get(1).getTargetRef(), "sid-0AD002F0-2C5A-4D9F-B0D2-7A03A8025FEB");
	}
}
