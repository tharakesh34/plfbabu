package com.pennant.webui.commitment.commitment;

import java.io.Serializable;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.zkoss.zk.ui.event.Event;

import com.pennant.backend.model.applicationmaster.CheckListDetail;
import com.pennant.backend.model.bmtmasters.CheckList;
import com.pennant.backend.model.commitment.CommitmentMovement;
import com.pennant.webui.applicationmaster.checklist.CheckListDialogCtrl;
import com.pennant.webui.util.ButtonStatusCtrl;
import com.pennant.webui.util.GFCBaseCtrl;

public class CommitmentMovementDialogCtrl extends GFCBaseCtrl implements Serializable {

	private static final long serialVersionUID = 2164774289694537365L;
	private final static Logger logger = Logger.getLogger(CommitmentMovementDialogCtrl.class);
	private CommitmentMovement commitmentMovement; // overHanded per parameter
	
	/**
	 * default constructor.<br>
	 */
	public CommitmentMovementDialogCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * ZUL-file is called with a parameter for a selected CheckListDetail object in a
	 * Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_CheckListDetailDialog(Event event) throws Exception {
		logger.debug("Entering" +event.toString());

		/* set components visible dependent of the users rights */


		// get the parameters map that are overHanded by creation.
		final Map<String, Object> args = getCreationArgsMap(event);

		// READ OVERHANDED parameters !
		if (args.containsKey("CommitmentMovement")) {
			this.commitmentMovement = (CommitmentMovement) args.get("CommitmentMovement");
			CommitmentMovement befImage =new CommitmentMovement();
			BeanUtils.copyProperties(this.commitmentMovement, befImage);
			this.commitmentMovement.setBefImage(befImage);

			setCommitmentMovement(this.commitmentMovement);
		} else {
			setCommitmentMovement(null);
		}

		doLoadWorkFlow(this.commitmentMovement.isWorkflow(),this.commitmentMovement.getWorkflowId(),this.commitmentMovement.getNextTaskId());

		if (isWorkFlowEnabled()){
			getUserWorkspace().alocateRoleAuthorities(getRole(), "CommitmentMovementDialogCtrl");
		}
		
}

	public CommitmentMovement getCommitmentMovement() {
		return commitmentMovement;
	}

	public void setCommitmentMovement(CommitmentMovement commitmentMovement) {
		this.commitmentMovement = commitmentMovement;
	}
}

	

