package com.pennanttech.framework.web;

import java.util.HashMap;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zul.Button;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.South;
import org.zkoss.zul.Window;

import com.pennant.backend.model.Notes;
import com.pennant.backend.util.PennantConstants;
import com.pennant.webui.util.ButtonStatusCtrl;
import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.web.util.MessageUtil;

public abstract class AbstractDialogController<T> extends AbstractController<T> {
	private static final long serialVersionUID = 4993596882485929197L;
	private final Logger logger = Logger.getLogger(getClass());
	
	// Button controller for the CRUD buttons
	protected transient ButtonStatusCtrl btnCtrl;
	protected Button btnNew;
	protected Button btnEdit;
	protected Button btnDelete;
	protected Button btnSave;
	protected Button btnCancel;
	protected Button btnClose;
	protected Button btnNotes;
	
	protected South south;
	protected Label recordStatus;
	protected Radiogroup userAction;
	protected Groupbox groupboxWf;
	
	protected boolean notesEntered;
	
	protected enum DialogType {
		/** Makes the window as normal dialog.*/
		EMBEDDED,
		/** Makes this window as a modal dialog.*/
		MODAL,
		/** Makes this window as overlapped with other components.*/
		OVERLAPPED;
	}
	
	private  DialogType dialogType = DialogType.EMBEDDED;
	
	
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		
		/* create the Button Controller. Disable not used buttons during working */
		this.btnCtrl = new ButtonStatusCtrl(getUserWorkspace(), "button_"+pageRightName+"_", true, this.btnNew,
				this.btnEdit, this.btnDelete, this.btnSave, this.btnCancel, this.btnClose, this.btnNotes);
		
		if (arguments.get("enqiryModule") == null || arguments.get("moduleCode") == null ) {
			logger.warn("enqiryModule or moduleCode is not configured as default arguments at the module "+ getClass());
			return;
		}
		
		this.enqiryModule = (Boolean) arguments.get("enqiryModule");
		this.moduleCode = (String) arguments.get("moduleCode");
	}
	

	public Radiogroup setListRecordStatus(Radiogroup userAction) {
		return setListRecordStatus(userAction, true);
	}

	public Radiogroup setListRecordStatus(Radiogroup userAction, boolean defaultSave) {
		String sequences = "";

		if (this.role.equals(getFirstTaskOwner())) {
			sequences = workFlow.getUserActionsAsString(workFlow.firstTaskId());
		} else {
			sequences = workFlow.getUserActionsAsString(getTaskId(getRole()));
		}

		String[] list = sequences.split("/");

		if (defaultSave) {
			boolean isSaveSpecified = false;

			for (int i = 0; i < list.length; i++) {
				String[] a = list[i].split("=");
				if ("Save".equalsIgnoreCase(a[0])) {
					isSaveSpecified = true;
					break;
				}

			}

			if (!isSaveSpecified) {
				userAction.appendItem("Save", "Saved");
			}
		}

		for (int i = 0; i < list.length; i++) {
			String[] a = list[i].split("=");
			if (!"Cancel".equalsIgnoreCase(a[0]) || !isFirstTask()) {
				if (!a[0].equalsIgnoreCase(PennantConstants.RCD_STATUS_REJECTAPPROVAL)) {
					userAction.appendItem(a[0], a[1]);
				}
			}
		}
		userAction.setSelectedIndex(0);
		return userAction;
	}

	/**
	 * Clears validation error messages from all the fields of the dialog controller.
	 */
	protected void doClearMessage() {
		// Should handle in future
	}

	/**
	 * Checks whether the data in the entity was changed from the before image.
	 * 
	 * @return true, if data changed, otherwise false
	 */
	protected boolean isDataChanged() {
		doClearMessage();

		return true;
	}

	protected boolean doClose(boolean askConfirmation) {
		logger.debug("Entering");

		if (askConfirmation) {
			if (MessageUtil.YES == MessageUtil
					.confirm("Any changes made will be lost if you close this window. Are you sure you want to continue?")) {
				closeDialog();
				return true;
			}
		} else {
			closeDialog();
			return true;
		}
		
		logger.debug("Leaving");
		return false;
	}
	
	/**
	 * Makes the window as normal dialog.
	 */
	private void setDialog() {
		menuWest = borderlayoutMain.getWest();
		groupboxMenu = (Groupbox) borderlayoutMain.getFellowIfAny("groupbox_menu");

		if (DialogType.OVERLAPPED == dialogType) {
			for (int i = 1; i < groupboxMenu.getParent().getChildren().size(); i++) {
				Window win = (Window) groupboxMenu.getParent().getChildren().get(i);
				win.setVisible(false);
			}
		}

		menuWest.setVisible(false);
		groupboxMenu.setVisible(false);

		window.setParent(groupboxMenu.getParent());
	}

	/**
	 * <p>
	 * Makes the window as normal dialog when dialogType is EMBEDDED.
	 * <p>
	 * Makes the window as modal dialog when dialogType is MODAL
	 * <p>
	 * Makes this window as overlapped dialog with other dialog when dialogType is OVERLAPPED
	 * 
	 * @param dialogType
	 */
	protected void setDialog(DialogType dialogType) {
		this.dialogType = dialogType;

		if (DialogType.MODAL == dialogType) {
			window.doModal();
		} else {
			setDialog();
		}
	}

	/**
	 * Deallocates the authorities on the dialog window allocated for the user and close the dialog window.
	 */
	public void closeDialog() {

		deAllocateAuthorities(pageRightName);

		if (window.inModal() || DialogType.MODAL == dialogType) {
			closeWindow();
			return;
		}

		closeWindow();
		
		if (menuWest == null && groupboxMenu == null) {
			return;
		}
		
		menuWest.setVisible(true);
		groupboxMenu.setVisible(true);

		if (DialogType.OVERLAPPED == dialogType) {
			if (groupboxMenu.getParent().getChildren().size() > 1) {
				menuWest.setVisible(false);
				groupboxMenu.setVisible(false);
				for (int i = 1; i < groupboxMenu.getParent().getChildren().size(); i++) {
					Window win = (Window) groupboxMenu.getParent().getChildren().get(i);
					win.setVisible(true);
				}
			}
		}
	}
	
	/**
	 * Close the the window.
	 */
	protected void closeWindow() {
		window.onClose();
	}
				
	protected void deAllocateAuthorities(String pageRightName) {
		if (StringUtils.isNotEmpty(pageRightName)) {
			getUserWorkspace().deAllocateAuthorities(pageRightName);
		}
	}
	
	protected void setStatusDetails() {
		if (isWorkFlowEnabled()) {
			this.groupboxWf.setVisible(true);
		} else {
			this.groupboxWf.setVisible(false);
		}
		
		if (enqiryModule) {
			if (south != null) {
				south.setVisible(false);
			}
		}
	}
		
	/**
	 * Sets whether notes entered or not.
	 * 
	 * @param notes
	 */
	public void setNotesEntered(boolean notesEnterd) {
		logger.debug("Entering");
		
		if (!notesEntered) {
			notesEntered = notesEnterd;
		}
		
		logger.debug("Leaving");
	}
	
	protected String getReference() {
		return null;
	}
	
	
	protected Notes getNotes(AbstractWorkflowEntity entity) {
		Notes notes = new Notes();
		notes.setModuleName(moduleCode);
		notes.setReference(getReference());
		notes.setVersion(entity.getVersion());
		return notes;
	}
	
	protected void doShowNotes(AbstractWorkflowEntity entity) {
		logger.debug("Entering");
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("notes", getNotes(entity));
		map.put("control", this);

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/notes/notes.zul", null, map);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving");
	}
}
