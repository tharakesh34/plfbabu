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

import com.pennanttech.pff.core.engine.WorkflowEngine.Element;
import com.pennanttech.pff.core.model.workflow.SequenceFlow;
import com.pennanttech.pff.core.model.workflow.UserTask;

public class WorkflowEngineTest {
	WorkflowEngine	engine;
	WorkflowEngine	engine2;

	public class TestEntity {
		private String	recordStatus;
		private String	approved;
		private String	finReference		= "FIN123";
		private boolean	shariaApprovalReq	= false;

		public TestEntity(String recordStatus, String approved, boolean shariaApprovalReq) {
			this.recordStatus = recordStatus;
			this.approved = approved;
			this.shariaApprovalReq = shariaApprovalReq;
		}

		public String getRecordStatus() {
			return recordStatus;
		}

		public String getApproved() {
			return approved;
		}

		public String getFinReference() {
			return finReference;
		}

		public boolean isShariaApprovalReq() {
			return shariaApprovalReq;
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
		Assert.assertEquals(engine.firstTaskId(), "_7B64C6C5-337C-44C2-98C6-A723EC862BFF");
	}

	@Test
	public void getFirstTask2() {
		Assert.assertEquals(engine2.firstTaskId(), "_7B64C6C5-337C-44C2-98C6-A723EC862BFF");
	}

	@Test
	public void firstTaskOwner() {
		Assert.assertEquals(engine.firstTaskOwner(), "MSTGRP1_MAKER");
	}

	@Test
	public void firstTaskOwner2() {
		Assert.assertEquals(engine2.firstTaskOwner(), "RTL_BRANCH_CSR");
	}

	@Test
	public void allFirstTaskOwners() {
		Assert.assertEquals(engine.allFirstTaskOwners(), "MSTGRP1_MAKER");
	}

	@Test
	public void allFirstTaskOwners2() {
		Assert.assertEquals(engine2.allFirstTaskOwners(), "RTL_BRANCH_CSR,RTL_DEALER");
	}

	@Test
	public void getActors() {
		Assert.assertEquals(StringUtils.join(engine.getActors(false), ';'), "MSTGRP1_MAKER;MSTGRP1_APPROVER");
	}

	@Test
	public void getActors2() {
		Assert.assertEquals(StringUtils.join(engine2.getActors(false), ';'),
				"RTL_BRANCH_CSR;RTL_CR_ANALYST;RTL_CR_MANAGER;RTL_LPO_RECIEPT_OFFICER;RTL_LPO_ISSUE_OFFICER;"
						+ "RTL_CNTRT_GEN_OFFICER;RTL_CNTRT_SIGNING_OFFICER;RTL_CNTRT_VERIFY_APPROVER;"
						+ "RTL_DEALER;RTL_CPV_CHECK;RTL_SHARIA_APPROVAL;RTL_CNTRT_VERIFY_OFFICER;"
						+ "RTL_REJECT_FINANCE_SSO;RTL_CR_DECISION");
	}

	@Test
	public void getDelegators() {
		Assert.assertEquals(StringUtils.join(engine.getActors(true), ';'), "");
	}

	@Test
	public void getDelegators2() {
		Assert.assertEquals(StringUtils.join(engine2.getActors(true), ';'),
				"RTL_AREAHEAD_OF_CR;RTL_HEAD_OF_CR;RTL_HEAD_OF_BUSINESS;RTL_EVP");
	}

	@Test
	public void getUserTaskId() {
		Assert.assertEquals(engine.getUserTaskId("MSTGRP1_MAKER"), "_7B64C6C5-337C-44C2-98C6-A723EC862BFF");
		Assert.assertEquals(engine.getUserTaskId("MSTGRP1_APPROVER"), "_77E97F8E-6071-449B-9BD7-4B0F3A5A657A");
	}

	@Test
	public void getUserTaskId2() {
		Assert.assertEquals(engine2.getUserTaskId("RTL_BRANCH_CSR"), "_7B64C6C5-337C-44C2-98C6-A723EC862BFF");
		Assert.assertEquals(engine2.getUserTaskId("RTL_CR_ANALYST"), "_56EEA3F6-C886-40AA-8253-BED29D71D4C9");
		Assert.assertEquals(engine2.getUserTaskId("RTL_AREAHEAD_OF_CR"), "_D2BFB860-8EE1-41CB-ACCC-5974C83EAE72");
		Assert.assertEquals(engine2.getUserTaskId("RTL_CR_MANAGER"), "_B158B71E-1D24-455A-9ADF-B1A0A2EAE064");
		Assert.assertEquals(engine2.getUserTaskId("RTL_HEAD_OF_CR"), "_868884EE-185C-47B3-86A5-7775B45F5F05");
		Assert.assertEquals(engine2.getUserTaskId("RTL_HEAD_OF_BUSINESS"), "_AAA020D9-E7F1-4DE7-96A2-12E097076D8D");
		Assert.assertEquals(engine2.getUserTaskId("RTL_LPO_RECIEPT_OFFICER"), "_2BB16D88-CDCC-4E0E-B2CE-0F8E0C3937A3");
		Assert.assertEquals(engine2.getUserTaskId("RTL_LPO_ISSUE_OFFICER"), "_BA282A03-59C0-4F67-A7FE-AC5760990322");
		Assert.assertEquals(engine2.getUserTaskId("RTL_CNTRT_GEN_OFFICER"), "_0DD7377B-A88D-4A08-8FAA-EF9FEB5FCA75");
		Assert.assertEquals(engine2.getUserTaskId("RTL_CNTRT_SIGNING_OFFICER"), "_1AD9DA13-C1C9-4A4F-B060-64C3273462F0");
		Assert.assertEquals(engine2.getUserTaskId("RTL_CNTRT_VERIFY_APPROVER"), "_BC08150B-5702-480E-A2A4-867C93DD1F31");
		Assert.assertEquals(engine2.getUserTaskId("RTL_DEALER"), "_5B25FB9F-584D-4E57-9343-472CDD30BF3C");
		Assert.assertEquals(engine2.getUserTaskId("RTL_CPV_CHECK"), "_1BB3BA72-F049-44A2-84D2-018CC89CFEAA");
		Assert.assertEquals(engine2.getUserTaskId("RTL_SHARIA_APPROVAL"), "_D8E2AEA9-04A6-48FD-A784-CBB16B26DB8C");
		Assert.assertEquals(engine2.getUserTaskId("RTL_CNTRT_VERIFY_OFFICER"), "_0AEAC513-D824-4D5E-AFF0-890D0246B290");
		Assert.assertEquals(engine2.getUserTaskId("RTL_REJECT_FINANCE_SSO"), "_34BC23F6-B68A-44D4-B73C-1ADB48836A48");
		Assert.assertEquals(engine2.getUserTaskId("RTL_CR_DECISION"), "_37A43FB4-414E-458A-89A6-5B819E8B245B");
		Assert.assertEquals(engine2.getUserTaskId("RTL_EVP"), "_5EB30BA6-D9EB-4862-999B-98E944C084F9");
	}

	@Test
	public void getUserActions() {
		Assert.assertEquals(engine.getUserActions("_7B64C6C5-337C-44C2-98C6-A723EC862BFF"),
				"Submit=Submitted/Cancel=Cancelled");
		Assert.assertEquals(engine.getUserActions("_77E97F8E-6071-449B-9BD7-4B0F3A5A657A"),
				"Approve=Approved/Resubmit=Resubmitted/Reject=Rejected");
	}

	@Test
	public void getUserActions2() {
		Assert.assertEquals(engine2.getUserActions("_7B64C6C5-337C-44C2-98C6-A723EC862BFF"),
				"Cancel=Cancelled/Submit=Submitted");
		Assert.assertEquals(
				engine2.getUserActions("_56EEA3F6-C886-40AA-8253-BED29D71D4C9"),
				"Reject=Declined/Resubmit=Resubmitted/Approve Subject To=Approved Subject To/Not Recommended=Not Recommended/Return=Returned/Approve=Approved/Submit to Waive CPV Check=Submitted by Waiving CPV Check");
		Assert.assertEquals(engine2.getUserActions("_D2BFB860-8EE1-41CB-ACCC-5974C83EAE72"), "");
		Assert.assertEquals(engine2.getUserActions("_B158B71E-1D24-455A-9ADF-B1A0A2EAE064"),
				"Resubmit=Resubmitted/Approve=Approved");
		Assert.assertEquals(engine2.getUserActions("_868884EE-185C-47B3-86A5-7775B45F5F05"), "");
		Assert.assertEquals(engine2.getUserActions("_AAA020D9-E7F1-4DE7-96A2-12E097076D8D"), "");
		Assert.assertEquals(engine2.getUserActions("_2BB16D88-CDCC-4E0E-B2CE-0F8E0C3937A3"),
				"Submit=Submitted/Resubmit=Resubmitted/Reject=Rejected");
		Assert.assertEquals(engine2.getUserActions("_BA282A03-59C0-4F67-A7FE-AC5760990322"),
				"Reject=Rejected/Submit=Submitted");
		Assert.assertEquals(engine2.getUserActions("_0DD7377B-A88D-4A08-8FAA-EF9FEB5FCA75"),
				"Submit=Submitted/Resubmit=Resubmitted");
		Assert.assertEquals(engine2.getUserActions("_1AD9DA13-C1C9-4A4F-B060-64C3273462F0"),
				"Reject=Rejected/Submit=Submitted");
		Assert.assertEquals(engine2.getUserActions("_BC08150B-5702-480E-A2A4-867C93DD1F31"),
				"Finalize=Finalized/Resubmit=Resubmitted");
		Assert.assertEquals(engine2.getUserActions("_5B25FB9F-584D-4E57-9343-472CDD30BF3C"), "Submit=Submitted");
		Assert.assertEquals(engine2.getUserActions("_1BB3BA72-F049-44A2-84D2-018CC89CFEAA"),
				"Submit=Submitted/Resubmit=Resubmitted");
		Assert.assertEquals(engine2.getUserActions("_D8E2AEA9-04A6-48FD-A784-CBB16B26DB8C"),
				"Decline=Declined/Approve=Approved");
		Assert.assertEquals(engine2.getUserActions("_0AEAC513-D824-4D5E-AFF0-890D0246B290"),
				"Submit=Submitted/Resubmit=Resubmitted");
		Assert.assertEquals(engine2.getUserActions("_34BC23F6-B68A-44D4-B73C-1ADB48836A48"), "Reject=Rejected");
		Assert.assertEquals(
				engine2.getUserActions("_37A43FB4-414E-458A-89A6-5B819E8B245B"),
				"Resubmit=Resubmitted/Cancel=Cancelled/Approve Subject To=Approved Subject To/Not Recommended=Not Recommended/Return=Returned");
		Assert.assertEquals(engine2.getUserActions("_5EB30BA6-D9EB-4862-999B-98E944C084F9"), "");
	}

	@Test
	public void getServiceOperations() {
		Assert.assertEquals(engine.getServiceOperations("_7B64C6C5-337C-44C2-98C6-A723EC862BFF", new TestEntity(
				"Submitted", null, false)), "");
		Assert.assertEquals(engine.getServiceOperations("_7B64C6C5-337C-44C2-98C6-A723EC862BFF", new TestEntity(
				"Cancelled", null, false)), "doReject");
		Assert.assertEquals(engine.getServiceOperations("_77E97F8E-6071-449B-9BD7-4B0F3A5A657A", new TestEntity(
				"Approved", null, false)), "doApprove");
		Assert.assertEquals(engine.getServiceOperations("_77E97F8E-6071-449B-9BD7-4B0F3A5A657A", new TestEntity(
				"Resubmitted", null, false)), "");
		Assert.assertEquals(engine.getServiceOperations("_77E97F8E-6071-449B-9BD7-4B0F3A5A657A", new TestEntity(
				"Rejected", null, false)), "doReject");
	}

	@Test
	public void getServiceOperations2() {
		Assert.assertEquals(engine2.getServiceOperations("_B158B71E-1D24-455A-9ADF-B1A0A2EAE064", new TestEntity(
				"Resubmitted", null, false)), "");
		Assert.assertEquals(engine2.getServiceOperations("_B158B71E-1D24-455A-9ADF-B1A0A2EAE064", new TestEntity(
				"Approved", null, true)), "doCheckDeviations;doCheckShariaRequired");
		Assert.assertEquals(engine2.getServiceOperations("_B158B71E-1D24-455A-9ADF-B1A0A2EAE064", new TestEntity(
				"Approved", null, false)), "doCheckDeviations;doCheckShariaRequired;doCheckProspectCustomer");
	}

	@SuppressWarnings("unchecked")
	@Test
	public void getNextTaskIdsInternal() throws NoSuchMethodException, SecurityException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException {
		final Method getNextTaskIds = WorkflowEngine.class.getDeclaredMethod("getNextTaskIds", Element.class,
				String.class, Object.class);
		getNextTaskIds.setAccessible(true);

		// Start Event
		String result = StringUtils.join((List<String>) getNextTaskIds.invoke(engine2, Element.startEvent,
				"_FF9D4911-C022-42B0-B00A-9C353226F4A7", null), ";");
		Assert.assertEquals(result, "_7B64C6C5-337C-44C2-98C6-A723EC862BFF");

		// Start Event #2
		result = StringUtils.join((List<String>) getNextTaskIds.invoke(engine2, Element.startEvent,
				"_AB642061-5931-4643-A2B4-7BA84B1494B5", null), ";");
		Assert.assertEquals(result, "_5B25FB9F-584D-4E57-9343-472CDD30BF3C");

		// User Task - New Finance Request (SSO - Auto Assignment)
		result = StringUtils.join((List<String>) getNextTaskIds.invoke(engine2, Element.userTask,
				"_7B64C6C5-337C-44C2-98C6-A723EC862BFF", null), ";");
		Assert.assertEquals(result, "_56EEA3F6-C886-40AA-8253-BED29D71D4C9");

		result = StringUtils.join((List<String>) getNextTaskIds.invoke(engine2, Element.userTask,
				"_7B64C6C5-337C-44C2-98C6-A723EC862BFF", new TestEntity("Cancelled", null, false)), ";");
		Assert.assertEquals(result, "");

		result = StringUtils.join((List<String>) getNextTaskIds.invoke(engine2, Element.userTask,
				"_7B64C6C5-337C-44C2-98C6-A723EC862BFF", new TestEntity("Submitted", null, false)), ";");
		Assert.assertEquals(result, "_56EEA3F6-C886-40AA-8253-BED29D71D4C9");
	}

	@Test
	public void getNextTaskIds() {
		Assert.assertEquals(engine.getNextTaskIds("_7B64C6C5-337C-44C2-98C6-A723EC862BFF", new TestEntity("Submitted",
				null, false)), "_77E97F8E-6071-449B-9BD7-4B0F3A5A657A");
		Assert.assertEquals(engine.getNextTaskIds("_7B64C6C5-337C-44C2-98C6-A723EC862BFF", new TestEntity("Cancelled",
				null, false)), "");
		Assert.assertEquals(
				engine.getNextTaskIds("_77E97F8E-6071-449B-9BD7-4B0F3A5A657A", new TestEntity("Approved", null, false)),
				"");
		Assert.assertEquals(engine.getNextTaskIds("_77E97F8E-6071-449B-9BD7-4B0F3A5A657A", new TestEntity(
				"Resubmitted", null, false)), "_7B64C6C5-337C-44C2-98C6-A723EC862BFF");
		Assert.assertEquals(
				engine.getNextTaskIds("_77E97F8E-6071-449B-9BD7-4B0F3A5A657A", new TestEntity("Rejected", null, false)),
				"");
	}

	@Test
	public void getNextTaskIds2() {
		Assert.assertEquals(engine2.getNextTaskIds("_B158B71E-1D24-455A-9ADF-B1A0A2EAE064", new TestEntity(
				"Resubmitted", null, false)), "_56EEA3F6-C886-40AA-8253-BED29D71D4C9");
		Assert.assertEquals(
				engine2.getNextTaskIds("_B158B71E-1D24-455A-9ADF-B1A0A2EAE064", new TestEntity("Approved", null, true)),
				"_D8E2AEA9-04A6-48FD-A784-CBB16B26DB8C");
		Assert.assertEquals(engine2.getNextTaskIds("_B158B71E-1D24-455A-9ADF-B1A0A2EAE064", new TestEntity("Approved",
				null, false)), "_BA282A03-59C0-4F67-A7FE-AC5760990322");
	}

	@Test
	public void getAuditingReq() {
		Assert.assertFalse(engine.getAuditingReq("_7B64C6C5-337C-44C2-98C6-A723EC862BFF", new TestEntity("Submitted",
				null, false)));
		Assert.assertTrue(engine.getAuditingReq("_7B64C6C5-337C-44C2-98C6-A723EC862BFF", new TestEntity("Cancelled",
				null, false)));
		Assert.assertFalse(engine.getAuditingReq("_77E97F8E-6071-449B-9BD7-4B0F3A5A657A", new TestEntity("Approved",
				null, false)));
		Assert.assertTrue(engine.getAuditingReq("_77E97F8E-6071-449B-9BD7-4B0F3A5A657A", new TestEntity("Resubmitted",
				null, false)));
		Assert.assertTrue(engine.getAuditingReq("_77E97F8E-6071-449B-9BD7-4B0F3A5A657A", new TestEntity("Rejected",
				null, false)));
	}

	@Test
	public void getAuditingReq2() {
		Assert.assertTrue(engine2.getAuditingReq("_B158B71E-1D24-455A-9ADF-B1A0A2EAE064", new TestEntity("Resubmitted",
				null, false)));
		Assert.assertFalse(engine2.getAuditingReq("_B158B71E-1D24-455A-9ADF-B1A0A2EAE064", new TestEntity("Approved",
				null, true)));
		Assert.assertFalse(engine2.getAuditingReq("_B158B71E-1D24-455A-9ADF-B1A0A2EAE064", new TestEntity("Approved",
				null, false)));
	}

	@Test
	public void getUserTask() throws NoSuchMethodException, SecurityException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException {
		final Method getUserTask = WorkflowEngine.class.getDeclaredMethod("getUserTask", String.class);
		getUserTask.setAccessible(true);

		// Maker
		UserTask task = (UserTask) getUserTask.invoke(engine, "_7B64C6C5-337C-44C2-98C6-A723EC862BFF");
		Assert.assertEquals(task.getActor(), "MSTGRP1_MAKER");

		// Checker
		task = (UserTask) getUserTask.invoke(engine, "_77E97F8E-6071-449B-9BD7-4B0F3A5A657A");
		Assert.assertEquals(task.getActor(), "MSTGRP1_APPROVER");
	}

	@Test
	public void getUserTaskAssignmentLevel() {
		Assert.assertEquals(engine2.getUserTask("_7B64C6C5-337C-44C2-98C6-A723EC862BFF").getAssignmentLevel(), "Auto");
		Assert.assertEquals(engine2.getUserTask("_56EEA3F6-C886-40AA-8253-BED29D71D4C9").getAssignmentLevel(), null);
		Assert.assertEquals(engine2.getUserTask("_D2BFB860-8EE1-41CB-ACCC-5974C83EAE72").getAssignmentLevel(), null);
		Assert.assertEquals(engine2.getUserTask("_5B25FB9F-584D-4E57-9343-472CDD30BF3C").getAssignmentLevel(), "Auto");
	}

	@Test
	public void getUserTaskAdditionalForms() {
		Assert.assertEquals(StringUtils.join(engine2.getUserTask("_BC08150B-5702-480E-A2A4-867C93DD1F31")
				.getAdditionalForms(), ','), "Accounting");
		Assert.assertEquals(StringUtils.join(engine2.getUserTask("_1AD9DA13-C1C9-4A4F-B060-64C3273462F0")
				.getAdditionalForms(), ','), "");
	}

	@Test
	public void getUserTaskBaseActor() {
		Assert.assertEquals(engine2.getUserTask("_2BB16D88-CDCC-4E0E-B2CE-0F8E0C3937A3").getBaseActor(),
				"RTL_BRANCH_CSR");
		Assert.assertEquals(engine2.getUserTask("_BA282A03-59C0-4F67-A7FE-AC5760990322").getBaseActor(),
				"RTL_BRANCH_CSR");
		Assert.assertEquals(engine2.getUserTask("_AAA020D9-E7F1-4DE7-96A2-12E097076D8D").getBaseActor(), null);
		Assert.assertEquals(engine2.getUserTask("_868884EE-185C-47B3-86A5-7775B45F5F05").getBaseActor(), null);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void getSequenceFlows() throws NoSuchMethodException, SecurityException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException {
		final Method getSequenceFlows = WorkflowEngine.class.getDeclaredMethod("getSequenceFlows", Element.class,
				String.class);
		getSequenceFlows.setAccessible(true);

		// Start Event
		List<SequenceFlow> flows = (List<SequenceFlow>) getSequenceFlows.invoke(engine, Element.startEvent,
				"_FF9D4911-C022-42B0-B00A-9C353226F4A7");
		Assert.assertEquals(flows.size(), 1);

		Assert.assertEquals(flows.get(0).getId(), "_5EE9B2AF-F8EA-432C-BB52-88124AA2A117");
		Assert.assertEquals(flows.get(0).isUserAction(), false);
		Assert.assertEquals(flows.get(0).getConditionExpression(), null);
		Assert.assertEquals(flows.get(0).getAction(), null);
		Assert.assertEquals(flows.get(0).getState(), null);
		Assert.assertEquals(flows.get(0).isNotesMandatory(), false);
		Assert.assertEquals(flows.get(0).getTargetRef(), "_7B64C6C5-337C-44C2-98C6-A723EC862BFF");

		// User Task - Maker
		flows = (List<SequenceFlow>) getSequenceFlows.invoke(engine, Element.userTask,
				"_7B64C6C5-337C-44C2-98C6-A723EC862BFF");
		Assert.assertEquals(flows.size(), 2);

		Assert.assertEquals(flows.get(0).getId(), "_9305AD0C-8EA2-46BB-AD97-8B650D65B67D");
		Assert.assertEquals(flows.get(0).isUserAction(), true);
		Assert.assertEquals(flows.get(0).getConditionExpression(), "vo.recordStatus == 'Submitted'");
		Assert.assertEquals(flows.get(0).getAction(), "Submit");
		Assert.assertEquals(flows.get(0).getState(), "Submitted");
		Assert.assertEquals(flows.get(0).isNotesMandatory(), false);
		Assert.assertEquals(flows.get(0).getTargetRef(), "_77E97F8E-6071-449B-9BD7-4B0F3A5A657A");

		Assert.assertEquals(flows.get(1).getId(), "_14CED7D6-CEC3-49D1-A614-5168AD7F13F6");
		Assert.assertEquals(flows.get(1).isUserAction(), true);
		Assert.assertEquals(flows.get(1).getConditionExpression(), "vo.recordStatus == 'Cancelled'");
		Assert.assertEquals(flows.get(1).getAction(), "Cancel");
		Assert.assertEquals(flows.get(1).getState(), "Cancelled");
		Assert.assertEquals(flows.get(1).isNotesMandatory(), true);
		Assert.assertEquals(flows.get(1).getTargetRef(), "_5F12FFE5-4B96-49AF-A35C-73A4DBEE36B0");
	}
}
