package com.pennant.util;

import java.io.FileNotFoundException;
import java.util.Set;

import javax.xml.stream.XMLStreamException;

import org.apache.commons.lang.StringUtils;

public class WorkflowLoad {
	private Workflow workFlow = null;
	private transient String role = "";
	private transient boolean firstTask = false;
	private Set<String> userRoleSet = null;

	public WorkflowLoad(long workFlowId, String nextTaskID,
			Set<String> userRoleSet) throws FileNotFoundException,
			XMLStreamException {
		super();
		setUserRoleSet(userRoleSet);
		setWorkFlow(new Workflow(workFlowId));

		// set the Role
		if (StringUtils.trimToEmpty(nextTaskID).equals("")) {
			setRole(getWorkFlow().firstTask.owner);
		} else {
			String[] nextTasks = nextTaskID.split(";");

			if (nextTasks != null && nextTasks.length > 0) {
				String currentRole = "";

				for (int i = 0; i < nextTasks.length; i++) {
					currentRole = this.workFlow.getTaskOwner(nextTasks[i]);
					if (isRoleContains(currentRole)) {
						setRole(currentRole);
						break;
					}
				}
			} else {
				setRole(this.workFlow.getTaskOwner(nextTaskID));
			}
		}

		if (getRole().equals(this.workFlow.firstTask.owner)
				&& StringUtils.trimToEmpty(nextTaskID).equals("")) {
			setFirstTask(true);
		} else {
			setFirstTask(false);
		}
	}

	private boolean isRoleContains(String roleName) {
		return getUserRoleSet().contains(roleName);
	}

	/**
	 * @return the workFlow
	 */
	public Workflow getWorkFlow() {
		return workFlow;
	}

	/**
	 * @param workFlow
	 *            the workFlow to set
	 */
	public void setWorkFlow(Workflow workFlow) {
		this.workFlow = workFlow;
	}

	/**
	 * @return the role
	 */
	public String getRole() {
		return role;
	}

	/**
	 * @param role
	 *            the role to set
	 */
	public void setRole(String role) {
		this.role = role;
	}

	/**
	 * @return the firstTask
	 */
	public boolean isFirstTask() {
		return firstTask;
	}

	/**
	 * @param firstTask
	 *            the firstTask to set
	 */
	public void setFirstTask(boolean firstTask) {
		this.firstTask = firstTask;
	}

	/**
	 * @return the userRoleSet
	 */
	public Set<String> getUserRoleSet() {
		return userRoleSet;
	}

	/**
	 * @param userRoleSet
	 *            the userRoleSet to set
	 */
	public void setUserRoleSet(Set<String> userRoleSet) {
		this.userRoleSet = userRoleSet;
	}

}
