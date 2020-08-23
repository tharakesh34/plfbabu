package com.pennanttech.framework.web;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.zkoss.util.media.AMedia;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.A;
import org.zkoss.zul.Button;
import org.zkoss.zul.Filedownload;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.South;
import org.zkoss.zul.Window;

import com.aspose.words.SaveFormat;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.audit.AuditHeaderDAO;
import com.pennant.backend.model.Notes;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.SMTParameterConstants;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.core.EventManager;
import com.pennant.core.EventManager.Notify;
import com.pennant.webui.util.ButtonStatusCtrl;
import com.pennanttech.pennapps.core.DocType;
import com.pennanttech.pennapps.core.engine.workflow.WorkflowEngine;
import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.resource.Literal;
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
	protected transient AuditHeaderDAO auditHeaderDAO;
	protected EventManager eventManager;

	protected enum DialogType {
		/** Makes the window as normal dialog. */
		EMBEDDED,
		/** Makes this window as a modal dialog. */
		MODAL,
		/** Makes this window as overlapped with other components. */
		OVERLAPPED;
	}

	private DialogType dialogType = DialogType.EMBEDDED;

	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);

		/*
		 * create the Button Controller. Disable not used buttons during working
		 */
		this.btnCtrl = new ButtonStatusCtrl(getUserWorkspace(), "button_" + pageRightName + "_", true, this.btnNew,
				this.btnEdit, this.btnDelete, this.btnSave, this.btnCancel, this.btnClose, this.btnNotes);

		if (arguments.containsKey("enqiryModule")) {
			if (arguments.get("enqiryModule") instanceof Boolean) {
				this.enqiryModule = (Boolean) arguments.get("enqiryModule");
			} else if (StringUtils.equalsIgnoreCase((String) arguments.get("enqiryModule"), "Y")) {
				this.enqiryModule = true;
			}
		}

		if (arguments.containsKey("moduleCode")) {
			this.moduleCode = (String) arguments.get("moduleCode");
		}
	}

	public Radiogroup setListRecordStatus(Radiogroup userAction) {
		return setListRecordStatus(userAction, true, null);
	}

	public Radiogroup setUserActions(Radiogroup userAction, Object object) {
		return setListRecordStatus(userAction, true, object);
	}

	public Radiogroup setRejectRecordStatus(Radiogroup userAction) {
		String sequences = "";

		if (this.role.equals(this.workFlow.allFirstTaskOwners())) {
			sequences = workFlow.getUserActionsAsString(workFlow.firstTaskId(), null);
		} else {
			sequences = workFlow.getUserActionsAsString(getTaskId(getRole()), null);
		}

		String[] list = sequences.split("/");
		for (int i = 0; i < list.length; i++) {
			String[] a = list[i].split("=");
			if (a[1].equalsIgnoreCase(PennantConstants.RCD_STATUS_REJECTED)) {
				userAction.appendItem(a[0], a[1]);
			}
		}
		userAction.setSelectedIndex(0);
		return userAction;
	}

	public Radiogroup setListRecordStatus(Radiogroup userAction, boolean defaultSave, Object object) {
		String sequences = "";

		if (this.role.equals(getFirstTaskOwner())) {
			sequences = workFlow.getUserActionsAsString(workFlow.firstTaskId(), object);
		} else {
			sequences = workFlow.getUserActionsAsString(getTaskId(getRole()), object);
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
					if (a.length > 1) {
						userAction.appendItem(a[0], a[1]);
					}

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
			if (MessageUtil.YES == MessageUtil.confirm(
					"Any changes made will be lost if you close this window. Are you sure you want to continue?")) {
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
		deAllocateAuthorities(pageRightName);
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

	@Deprecated
	protected A getDocumentLink(String label, final String docType, final String documentName, final byte[] content) {
		A agreementLink = new A();
		agreementLink.setStyle("padding:10px; font-weight:bold;");
		agreementLink.setLabel(label);
		agreementLink.addEventListener(Events.ON_CLICK, event -> downloadFile(docType, content, documentName));
		return agreementLink;
	}

	protected A getDocumentLink(String label, final DocType docType, final String documentName, final byte[] content) {
		A agreementLink = new A();
		agreementLink.setStyle("padding:10px; font-weight:bold;");
		agreementLink.setLabel(label);
		agreementLink.addEventListener(Events.ON_CLICK, event -> downloadFile(docType, content, documentName));
		return agreementLink;
	}

	public void downloadFile(DocType docType, byte[] content, String fileName) {
		Filedownload.save(content, docType.getContentType(), fileName);
	}

	@Deprecated
	public void downloadFile(String docType, byte[] content, String fileName) {
		String contentType;
		switch (docType) {
		case PennantConstants.DOC_TYPE_TXT:
			contentType = "text/plain";
			break;
		case PennantConstants.DOC_TYPE_IMAGE:
			contentType = "image/jpeg";
			break;
		case PennantConstants.DOC_TYPE_PDF:
			contentType = "application/pdf";
			break;
		case PennantConstants.DOC_TYPE_WORD:
			contentType = "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
			break;
		case PennantConstants.DOC_TYPE_MSG:
			contentType = "application/octet-stream";
			break;
		case PennantConstants.DOC_TYPE_EXCEL:
			contentType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
			break;
		case PennantConstants.DOC_TYPE_ZIP:
			contentType = "application/x-zip-compressed";
			break;
		case PennantConstants.DOC_TYPE_7Z:
			contentType = "application/octet-stream";
			break;
		case PennantConstants.DOC_TYPE_RAR:
			contentType = "application/x-rar-compressed";
			break;
		default:
			contentType = "application/pdf";
			Filedownload.save(content, "application/pdf", fileName);
			break;
		}

		Filedownload.save(content, contentType, fileName);
	}

	protected void showDocument(byte[] docData, Window window, String reportName, int format) throws Exception {
		logger.debug(Literal.ENTERING);
		if ((SaveFormat.DOCX) == format) {
			Filedownload.save(new AMedia(reportName, "msword", "application/msword", docData));
		} else {
			Map<String, Object> arg = new HashMap<>();
			arg.put("reportBuffer", docData);
			arg.put("parentWindow", window);
			arg.put("reportName", reportName);
			arg.put("isAgreement", true);
			arg.put("docFormat", format);

			Executions.createComponents("/WEB-INF/pages/Reports/ReportView.zul", window, arg);
		}
		logger.debug(Literal.LEAVING);
	}

	protected void publishNotification(Notify notify, String reference, AbstractWorkflowEntity entity) {
		if (!SysParamUtil.isAllowed(SMTParameterConstants.USER_NOTIFICATION_PUBLISH)) {
			return;
		}

		try {
			String usrAction = StringUtils.trimToEmpty(this.userAction.getSelectedItem().getLabel()).toLowerCase();
			String recordStatus = StringUtils.trimToEmpty(entity.getRecordStatus()).toUpperCase();
			String nextRoleCodes = entity.getNextRoleCode();

			if ("save".equals(usrAction) || "cancel".equals(usrAction) || usrAction.contains("reject")) {
				return;
			}

			String[] to = null;

			if (notify == Notify.ROLE) {
				if (StringUtils.isEmpty(entity.getNextRoleCode())) {
					return;
				}
				to = nextRoleCodes.split(",");
			} else {
				to = getNextUsers(entity);
			}

			String messagePrefix = Labels.getLabel("REC_PENDING_MESSAGE");

			if (StringUtils.isBlank(entity.getNextTaskId())) {
				messagePrefix = Labels.getLabel("REC_FINALIZED_MESSAGE");
			}

			if (StringUtils.isNotEmpty(reference)) {
				if (!PennantConstants.RCD_STATUS_CANCELLED.equals(recordStatus)) {
					eventManager.publish(messagePrefix + " with Reference" + ":" + reference, notify, to);
				}
			} else {
				eventManager.publish(messagePrefix, notify, to);
			}
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}
	}

	protected String[] getNextUsers(AbstractWorkflowEntity entity) {
		return new String[0];
	}

	public void setAuditHeaderDAO(AuditHeaderDAO auditHeaderDAO) {
		this.auditHeaderDAO = auditHeaderDAO;
	}

	public void setEventManager(EventManager eventManager) {
		this.eventManager = eventManager;
	}

	protected boolean isBackwardCase(AbstractWorkflowEntity entity) {
		if (entity.getWorkflowId() == 0) {
			return false;
		}

		WorkflowEngine engine = new WorkflowEngine(WorkFlowUtil.getWorkflow(entity.getWorkflowId()).getWorkFlowXml());

		return engine.isBackwardCase(entity.getTaskId(), entity.getNextTaskId().replace(";", ""));
	}

}
