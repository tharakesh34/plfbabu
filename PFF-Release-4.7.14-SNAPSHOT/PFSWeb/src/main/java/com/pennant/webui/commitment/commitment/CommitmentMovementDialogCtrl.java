package com.pennant.webui.commitment.commitment;

import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.zkoss.zk.ui.event.Event;

import com.pennant.backend.model.commitment.CommitmentMovement;
import com.pennant.webui.util.GFCBaseCtrl;

public class CommitmentMovementDialogCtrl extends GFCBaseCtrl<CommitmentMovement> {

	private static final long serialVersionUID = 2164774289694537365L;
	private static final Logger logger = Logger.getLogger(CommitmentMovementDialogCtrl.class);
	private CommitmentMovement commitmentMovement; // overHanded per parameter
	
	/**
	 * default constructor.<br>
	 */
	public CommitmentMovementDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "CommitmentMovementDialogCtrl";
	}

	// Component Events

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * ZUL-file is called with a parameter for a selected CheckListDetail object in a
	 * Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_CheckListDetailDialog(Event event) throws Exception {
		logger.debug("Entering");

		// Set the page level components.
		//setPageComponents(window_CheckListDetailDialog);

		/* set components visible dependent of the users rights */

		if (arguments.containsKey("CommitmentMovement")) {
			this.commitmentMovement = (CommitmentMovement) arguments.get("CommitmentMovement");
			CommitmentMovement befImage =new CommitmentMovement();
			BeanUtils.copyProperties(this.commitmentMovement, befImage);
			this.commitmentMovement.setBefImage(befImage);

			setCommitmentMovement(this.commitmentMovement);
		} else {
			setCommitmentMovement(null);
		}

		doLoadWorkFlow(this.commitmentMovement.isWorkflow(),this.commitmentMovement.getWorkflowId(),this.commitmentMovement.getNextTaskId());

		if (isWorkFlowEnabled()){
			getUserWorkspace().allocateRoleAuthorities(getRole(), super.pageRightName);
		}
		
}

	public CommitmentMovement getCommitmentMovement() {
		return commitmentMovement;
	}

	public void setCommitmentMovement(CommitmentMovement commitmentMovement) {
		this.commitmentMovement = commitmentMovement;
	}
}

	

