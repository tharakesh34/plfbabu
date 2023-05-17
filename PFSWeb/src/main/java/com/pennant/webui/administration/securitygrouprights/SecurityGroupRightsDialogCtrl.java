package com.pennant.webui.administration.securitygrouprights;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.beanutils.BeanComparator;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listhead;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Window;

import com.pennant.backend.model.administration.SecurityGroup;
import com.pennant.backend.model.administration.SecurityGroupRights;
import com.pennant.backend.model.administration.SecurityRight;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.service.administration.SecurityGroupRightsService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.util.ErrorControl;
import com.pennant.webui.administration.securityuserroles.model.SecurityGroupRightModelItemRenderer;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.jdbc.search.Filter;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the /WEB-INF/pages/Administration/SecurityGroupRights /SecurityGroupRightsDialog.zul
 * file.
 */
public class SecurityGroupRightsDialogCtrl extends GFCBaseCtrl<SecurityRight> {
	private static final long serialVersionUID = -7625144242180775016L;
	private static final Logger logger = LogManager.getLogger(SecurityGroupRightsDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the ZUL-file
	 * are getting autoWired by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window win_SecGroupRightsDialog; // autoWired
	protected Borderlayout borderLayout_SecurityGroupRights; // autoWired
	protected Button btnSelectRights; // autoWired
	protected Button btnUnSelectRights; // autoWired
	protected Button btnUnSelectAllRights; // autoWired
	protected Button btnSearchRights; // autoWired
	protected Listbox listbox_UnAssignedRights; // autoWired
	protected Listbox listbox_AssignedRights; // autoWired
	protected Label label_GroupCode; // autoWired
	protected Label label_GroupDesc; // autoWired
	protected Listhead listheader_SelectRight; // autoWired
	protected Listhead listheader_RightDesc; // autoWired

	private SecurityGroupRights secGroupRights;
	private List<SecurityRight> secRightsList = new ArrayList<SecurityRight>();
	private SecurityGroup securityGroup;
	private transient SecurityGroupRightsService securityGroupRightsService;
	private List<SecurityRight> assignedRights = new ArrayList<SecurityRight>();
	private List<SecurityRight> unAssignedRights = new ArrayList<SecurityRight>();
	private Map<Long, SecurityRight> newAssignedMap = new HashMap<Long, SecurityRight>();
	private Map<Long, SecurityRight> oldAssignedMap = new HashMap<Long, SecurityRight>();
	private Map<Long, SecurityGroupRights> selectedMap;
	private Map<Long, SecurityGroupRights> deletedMap;
	private Map<String, SecurityRight> tempUnAsgnRightsMap = new HashMap<String, SecurityRight>();
	private Object[] filters = new Object[2];

	/**
	 * onClick$btnSelectRights default constructor.<br>
	 */
	public SecurityGroupRightsDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "SecurityGroupRightsDialog";
	}

	// Components events

	/**
	 * Before binding the data and calling the dialog window we check, if the ZUL-file is called with a parameter for a
	 * selected SecurityGroup object in a Map.
	 * 
	 * @param event
	 */
	public void onCreate$win_SecGroupRightsDialog(Event event) {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(win_SecGroupRightsDialog);

		try {

			logger.debug("Entering " + event.toString());
			/* set components visible dependent of the users rights */
			doCheckRights();

			if (arguments.containsKey("securityGroup")) {
				setSecurityGroup((SecurityGroup) arguments.get("securityGroup"));

			} else {
				setSecurityGroup(null);
			}

			this.borderLayout_SecurityGroupRights.setHeight(getBorderLayoutHeight());
			this.label_GroupCode.setValue(getSecurityGroup().getGrpCode());
			this.label_GroupDesc.setValue(getSecurityGroup().getGrpDesc());
			this.listbox_UnAssignedRights.setItemRenderer(new SecurityGroupRightModelItemRenderer());
			/* get all assigned rights */
			assignedRights = getSecurityGroupRightsService().getRightsByGroupId(getSecurityGroup().getGrpID(), true);
			/* get all unassigned rights */
			unAssignedRights = getSecurityGroupRightsService().getRightsByGroupId(getSecurityGroup().getGrpID(), false);

			doShowDialog();
			setDialog(DialogType.EMBEDDED);
		} catch (Exception e) {
			MessageUtil.showError(e);
			this.win_SecGroupRightsDialog.onClose();
		}

		logger.debug("Leaving " + event.toString());
	}

	/**
	 * When user clicks on "cancel" button
	 * 
	 * @param event
	 */
	public void onClick$btnCancel(Event event) {
		logger.debug("Entering " + event.toString());
		doCancel();
		logger.debug("Leaving " + event.toString());
	}

	/**
	 * When user clicks on "save" button
	 * 
	 * @throws InterruptedException
	 */
	public void onClick$btnSave(Event event) throws InterruptedException {
		logger.debug("Entering " + event.toString());
		doSave();
		logger.debug("Leaving " + event.toString());

	}

	/**
	 * When clicks on "Close" button
	 */
	public void onClick$btnRefresh(Event event) {
		logger.debug("Entering " + event.toString());
		doShowUnAssignedRightsList();
		logger.debug("Leaving " + event.toString());
	}

	/**
	 * The Click event is raised when the Close Button control is clicked.
	 * 
	 * @param event An event sent to the event handler of a component.
	 */
	public void onClick$btnClose(Event event) {
		doClose(this.btnSave.isVisible());
	}

	/**
	 * when clicks on "btnSearchRights"
	 * 
	 * @param event
	 */
	public void onClick$btnSearchRights(Event event) {

		logger.debug("Entering " + event.toString());

		final Map<String, Object> map = new HashMap<String, Object>();
		map.put("dialogCtrl", this);
		map.put("dataMap", tempUnAsgnRightsMap);
		map.put("prevFilters", filters);
		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/Administration/SecuritySearchDialog.zul", null, map);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving " + event.toString());

	}

	/**
	 * when clicks on "btnSelectRights"
	 * 
	 * @param event
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void onClick$btnSelectRights(Event event) {
		logger.debug("Entering" + event.toString());

		if (this.listbox_UnAssignedRights.getSelectedCount() != 0) {

			Listitem li = new Listitem(); // To read List Item
			Set seletedSet = new HashSet(); // To get Selected Items
			seletedSet = this.listbox_UnAssignedRights.getSelectedItems();
			List list = new ArrayList(seletedSet); // Converting Set to ArrayList to Make Concurrent operations
			Iterator iterator = list.iterator();
			while (iterator.hasNext()) {
				li = (Listitem) iterator.next();
				final SecurityRight aSecRight = (SecurityRight) li.getAttribute("data");
				Listcell slecteditem = new Listcell();
				List selectedRowValues = new ArrayList(); // TO get each row Details
				selectedRowValues = li.getChildren();
				slecteditem = (Listcell) selectedRowValues.get(0);
				tempUnAsgnRightsMap.remove(String.valueOf(aSecRight.getRightID()));
				getNewAssignedMap().put(Long.valueOf(aSecRight.getRightID()), aSecRight);
				doFillListbox(this.listbox_AssignedRights, slecteditem.getLabel(), aSecRight);
				this.listbox_UnAssignedRights.removeItemAt(li.getIndex());

			}
		}
	}

	/**
	 * when clicks on "btnUnSelectRights"
	 * 
	 * @param event
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void onClick$btnUnSelectRights(Event event) {
		logger.debug(event.toString());

		if (this.listbox_AssignedRights.getSelectedCount() != 0) {
			// To Remove Selected item from the List Box
			Listitem li = new Listitem(); // To read List Item
			Set seletedSet = new HashSet(); // To get Selected Items
			seletedSet = this.listbox_AssignedRights.getSelectedItems();
			List list = new ArrayList(seletedSet); // Converting Set to ArrayList to Make Concurrent operations
			Iterator iterator = list.iterator();
			while (iterator.hasNext()) {
				li = (Listitem) iterator.next();
				final SecurityRight aSecRight = (SecurityRight) li.getAttribute("data");
				Listcell slecteditem = new Listcell();
				List selectedRowValues = new ArrayList(); // TO get each row Details
				selectedRowValues = li.getChildren();
				slecteditem = (Listcell) selectedRowValues.get(0);
				tempUnAsgnRightsMap.put(String.valueOf(aSecRight.getRightID()), aSecRight);
				getNewAssignedMap().remove(Long.valueOf(aSecRight.getRightID()));
				doFillListbox(this.listbox_UnAssignedRights, slecteditem.getLabel(), aSecRight);
				if (true) {
					this.listbox_AssignedRights.removeItemAt(li.getIndex());
				}
			}
		}
	}

	/**
	 * when clicks on "btnUnSelectAllRights"
	 * 
	 * @param event
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void onClick$btnUnSelectAllRights(Event event) {
		logger.debug(event.toString());
		this.listbox_AssignedRights.selectAll();
		if (this.listbox_AssignedRights.getSelectedCount() != 0) {
			//////// To Remove Selected item from the List Box
			Listitem li = new Listitem();// To read List Item
			Set seletedSet = new HashSet();// To get Selected Items
			seletedSet = this.listbox_AssignedRights.getSelectedItems();
			List list = new ArrayList(seletedSet); // Converting Set to ArrayList to Make Concurrent operations
			java.util.Iterator it = list.iterator();
			while (it.hasNext()) {
				li = (Listitem) it.next();
				final SecurityRight aSecRight = (SecurityRight) li.getAttribute("data");
				Listcell slecteditem = new Listcell();
				List selectedRowValues = new ArrayList();// TO get each row Details
				selectedRowValues = li.getChildren();
				slecteditem = (Listcell) selectedRowValues.get(0);
				tempUnAsgnRightsMap.put(String.valueOf(aSecRight.getRightID()), aSecRight);
				getNewAssignedMap().remove(Long.valueOf(aSecRight.getRightID()));
				doFillListbox(this.listbox_UnAssignedRights, slecteditem.getLabel(), aSecRight);
				this.listbox_AssignedRights.removeItemAt(li.getIndex());

			}
		}
	}

	/**
	 * when the "help" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnHelp(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		MessageUtil.showHelpWindow(event, this.win_SecGroupRightsDialog);
		logger.debug("Leaving" + event.toString());
	}

	// GUI operations

	/**
	 * User rights check. <br>
	 * Only components are set visible=true if the logged-in <br>
	 * user have the right for it. <br>
	 * 
	 * The rights are get from the spring framework users grantedAuthority(). A right is only a string. <br>
	 */
	private void doCheckRights() {
		logger.debug("Entering ");
		getUserWorkspace().allocateAuthorities(super.pageRightName);
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_SecurityGroupRightsDialog_btnSave"));
		this.btnCancel.setVisible(getUserWorkspace().isAllowed("button_SecurityGroupRightsDialog_btnCancel"));
		this.btnSelectRights
				.setVisible(getUserWorkspace().isAllowed("button_SecurityGroupRightsDialog_btnSelectRights"));
		this.btnUnSelectRights
				.setVisible(getUserWorkspace().isAllowed("button_SecurityGroupRightsDialog_btnUnSelectRights"));
		this.btnUnSelectAllRights
				.setVisible(getUserWorkspace().isAllowed("button_SecurityGroupRightsDialog_btnUnSelectAllRights"));
		logger.debug("Leaving ");
	}

	/**
	 * Opens the Dialog window modal.
	 */
	public void doShowDialog() {
		logger.debug("Entering ");

		for (SecurityRight secRight : unAssignedRights) {
			tempUnAsgnRightsMap.put(String.valueOf(secRight.getRightID()), secRight);
		}
		try {
			doShowUnAssignedRightsList();
			doShowAssignedRightsList();
		} catch (UiException e) {
			logger.error("Exception: ", e);
			this.win_SecGroupRightsDialog.onClose();
		} catch (Exception e) {
			throw e;
		}
		logger.debug("Leaving ");
	}

	/**
	 * This method do the following 1) Gets assigned rights list by calling SecurityGroupRightsService's
	 * getRightsByGroupId()method 2) render all the list by calling doFillListbox()
	 */
	public void doShowAssignedRightsList() {

		logger.debug("Entering");
		this.listbox_AssignedRights.getItems().clear();
		SecurityRight secRight = new SecurityRight();
		Comparator<SecurityRight> comp = new BeanComparator<SecurityRight>("rightName");
		Collections.sort(assignedRights, comp);
		for (int i = 0; i < assignedRights.size(); i++) {
			secRight = (SecurityRight) assignedRights.get(i);
			oldAssignedMap.put(Long.valueOf(secRight.getRightID()), secRight);
			doFillListbox(this.listbox_AssignedRights, secRight.getRightName(), secRight);
		}
		setOldAssignedMap(oldAssignedMap);
		getNewAssignedMap().putAll(oldAssignedMap);
	}

	/**
	 * This method do the following 1) Gets unassigned rights list by calling SecurityGroupRightsService's
	 * getRightsByGroupId()method 2) render all the list by calling doFillListbox()
	 */
	public void doShowUnAssignedRightsList() {

		logger.debug("Entering ");
		this.listbox_UnAssignedRights.getItems().clear();

		unAssignedRights = new ArrayList<SecurityRight>(tempUnAsgnRightsMap.values());

		SecurityRight secRight = new SecurityRight();
		Comparator<SecurityRight> comp = new BeanComparator<SecurityRight>("rightName");
		Collections.sort(unAssignedRights, comp);
		for (int i = 0; i < unAssignedRights.size(); i++) {
			secRight = (SecurityRight) unAssignedRights.get(i);
			doFillListbox(this.listbox_UnAssignedRights, secRight.getRightName(), secRight);

		}

		logger.debug("Leaving ");
	}

	/**
	 * This method do the following 1)compare oldAssigned map and newAssigned map a)if rightId not in oldselectedMap and
	 * in new selectedMap creates new SecurityGroupRights Object, sets data and add it to SecurityGroup
	 * LovDescAssignedRights b)if rightId in oldselectedMap and not in new selectedMap gets the SecurityGroupRights from
	 * back end , sets RecordStatus DELETE and add it to SecurityGroup LovDescAssignedRights
	 */

	public void doWriteComponentsToBean() throws InterruptedException {
		logger.debug("Entering");

		selectedMap = new HashMap<Long, SecurityGroupRights>();
		deletedMap = new HashMap<Long, SecurityGroupRights>();
		// for insert
		for (Object rightId : getNewAssignedMap().keySet()) {
			if (!getOldAssignedMap().containsKey(rightId)) {

				SecurityGroupRights aSecGroupRights = getSecurityGroupRightsService().getSecurityGroupRights();
				aSecGroupRights.setGrpID(getSecurityGroup().getGrpID());
				aSecGroupRights.setLovDescGrpCode(getSecurityGroup().getGrpCode());
				aSecGroupRights.setRightID(getNewAssignedMap().get(rightId).getRightID());
				aSecGroupRights.setLovDescRightName(getNewAssignedMap().get(rightId).getRightName());
				aSecGroupRights.setLastMntOn(new Timestamp(System.currentTimeMillis()));
				aSecGroupRights.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
				aSecGroupRights.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				aSecGroupRights.setNextRoleCode("");
				aSecGroupRights.setNextTaskId("");
				aSecGroupRights.setTaskId("");
				aSecGroupRights.setRoleCode("");
				aSecGroupRights.setRecordStatus("");
				selectedMap.put(Long.valueOf(getNewAssignedMap().get(rightId).getRightID()), aSecGroupRights);
			}
		}
		// for Delete
		for (Object rightId : getOldAssignedMap().keySet()) {
			if (!getNewAssignedMap().containsKey(rightId)) {

				SecurityGroupRights aSecGroupRights = getSecurityGroupRightsService().getSecurityGroupRights();
				aSecGroupRights.setGrpID(getSecurityGroup().getGrpID());
				aSecGroupRights.setRightID(getOldAssignedMap().get(rightId).getRightID());
				aSecGroupRights.setLovDescGrpCode(getSecurityGroup().getGrpCode());
				aSecGroupRights.setLovDescRightName(getOldAssignedMap().get(rightId).getRightName());
				aSecGroupRights.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				aSecGroupRights.setLastMntOn(new Timestamp(System.currentTimeMillis()));
				aSecGroupRights.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
				aSecGroupRights.setNextRoleCode("");
				aSecGroupRights.setNextTaskId("");
				aSecGroupRights.setTaskId("");
				aSecGroupRights.setRoleCode("");
				aSecGroupRights.setRecordStatus("");
				deletedMap.put(aSecGroupRights.getRightID(), aSecGroupRights);
			}
		}
		logger.debug("Leaving");
	}

	// CRUD operations

	/**
	 * This method inserts or deletes SecurityGroupRights records to database by calling SecurityGroupRightsService's
	 * saveOrDelete() method
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug("Entering ");
		doWriteComponentsToBean();
		try {
			AuditHeader auditHeader = getAuditHeader(getSecurityGroup(), "");
			auditHeader.setAuditDetails(getAuditDetails());
			if (doSaveProcess(auditHeader)) {
				closeDialog();
			}

		} catch (DataAccessException error) {
			showMessage(error);
		}
		logger.debug("Leaving ");
	}

	/**
	 * Get the result after processing DataBase Operations
	 * 
	 * @param auditHeader (AuditHeader)
	 * 
	 * @param method      (String)
	 * 
	 * @return boolean
	 * 
	 */
	private boolean doSaveProcess(AuditHeader auditHeader) throws InterruptedException {
		boolean processCompleted = false;
		int retValue = PennantConstants.porcessOVERIDE;

		while (retValue == PennantConstants.porcessOVERIDE) {
			auditHeader = getSecurityGroupRightsService().save(auditHeader);
			retValue = ErrorControl.showErrorControl(this.win_SecGroupRightsDialog, auditHeader);

			if (retValue == PennantConstants.porcessCONTINUE) {
				processCompleted = true;
			}
			if (retValue == PennantConstants.porcessOVERIDE) {
				auditHeader.setOveride(true);
				auditHeader.setErrorMessage(null);
				auditHeader.setInfoMessage(null);
				auditHeader.setOverideMessage(null);
			}
			setOverideMap(auditHeader.getOverideMap());
		}
		return processCompleted;
	}

	// Helpers

	private void doCancel() {
		getNewAssignedMap().clear();
		doShowDialogPage(this.securityGroup);
	}

	private void doShowDialogPage(SecurityGroup securityGroup) {
		logger.debug("Entering");

		Map<String, Object> aruments = new HashMap<String, Object>();
		aruments.put("securityGroup", securityGroup);
		aruments.put("securityGroupRightsDialog", this);
		aruments.put("newRecord", securityGroup.isNewRecord());
		closeDialog();

		try {

			Executions.createComponents(
					"/WEB-INF/pages/Administration/SecurityGroupRights" + "/SecurityGroupRightsDialog.zul", null,
					aruments);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}

		logger.debug("Leaving");
	}

	/**
	 * This method shows message box with error message
	 * 
	 * @param e
	 */
	private void showMessage(Exception e) {
		logger.debug("Entering ");
		AuditHeader auditHeader = new AuditHeader();
		try {
			auditHeader.setErrorDetails(new ErrorDetail(PennantConstants.ERR_UNDEF, e.getMessage(), null));
			ErrorControl.showErrorControl(this.win_SecGroupRightsDialog, auditHeader);
		} catch (Exception exp) {
			logger.error("Exception: ", exp);
		}
		logger.debug("Leaving ");
	}

	/**
	 * This method creates and returns AuditHeader Object
	 * 
	 * @param SecurityGroup
	 * @param tranType
	 * @return
	 */
	private AuditHeader getAuditHeader(SecurityGroup aSecurityGroup, String tranType) {

		logger.debug("Entering ");
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aSecurityGroup.getBefImage(), aSecurityGroup);
		return new AuditHeader(String.valueOf(aSecurityGroup.getId()), null, null, null, auditDetail,
				getUserWorkspace().getLoggedInUser(), getOverideMap());
	}

	/**
	 * This method works as item renderer
	 * 
	 * @param listbox
	 * @param value1
	 * @param securityRight
	 */
	private void doFillListbox(Listbox listbox, String value1, SecurityRight securityRight) {
		Listitem item = new Listitem(); // To Create List item
		Listcell lc;
		lc = new Listcell();
		lc.setLabel(value1);
		lc.setParent(item);
		item.setAttribute("data", securityRight);
		listbox.appendChild(item);
	}

	/**
	 * This method displays the filtered data in unAssigned Groups panel .
	 * 
	 * @param searchResult
	 */
	public int doShowSearchResult(Object[] searchResult) {

		logger.debug("Entering");

		int searchOperator = -1;
		String searchValue = "";
		if (searchResult != null && searchResult.length > 0) {
			searchOperator = (Integer) searchResult[0];
			searchValue = (String) searchResult[1];
		}
		filterRights(searchOperator, searchValue);

		logger.debug("Leaving");
		return listbox_UnAssignedRights.getItemCount();

	}

	/**
	 * This method used when search button is clicked
	 */
	public void filterRights(int filterCode, String filterValue) {

		filterValue = StringUtils.trimToEmpty(filterValue).toUpperCase();

		List<SecurityRight> unassignedList = new ArrayList<SecurityRight>();

		for (SecurityRight right : tempUnAsgnRightsMap.values()) {

			switch (filterCode) {
			case Filter.OP_EQUAL:
				if (right.getRightName().toUpperCase().equals(filterValue)) {
					unassignedList.add(right);
				}
				break;
			case Filter.OP_NOT_EQUAL:
				if (!right.getRightName().toUpperCase().equals(filterValue)) {
					unassignedList.add(right);
				}
				break;
			case Filter.OP_LIKE:

				if (right.getRightName().toUpperCase().contains(filterValue)) {
					unassignedList.add(right);
				}
				break;
			default:

			}
		}

		if (unassignedList.size() == 0) {
			this.listbox_UnAssignedRights.getItems().clear();
		} else {
			this.listbox_UnAssignedRights.getItems().clear();
			for (int i = 0; i < unassignedList.size(); i++) {
				SecurityRight securityRight = unassignedList.get(i);
				doFillListbox(listbox_UnAssignedRights, securityRight.getRightName(), securityRight);
			}
		}
	}

	/**
	 * This method prepares the audit details list and sets different auditSequence for newly inserted records and
	 * deleted records
	 * 
	 * @return
	 */
	private List<AuditDetail> getAuditDetails() {
		logger.debug("Entering ");
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();

		int count = 1;
		String[] fields = PennantJavaUtil.getFieldDetails(new SecurityGroupRights());

		if (selectedMap != null && selectedMap.size() > 0) {
			Collection<SecurityGroupRights> collection = selectedMap.values();

			for (final SecurityGroupRights securityGroupRights : collection) {
				AuditDetail auditDetail = getAuditDetail(securityGroupRights, count, fields);
				if (auditDetail != null) {
					auditDetails.add(auditDetail);
					count++;
				}
			}
		}

		if (deletedMap != null && deletedMap.size() > 0) {
			count = 1;
			Collection<SecurityGroupRights> collection = deletedMap.values();
			for (final SecurityGroupRights securityGroupRights : collection) {
				AuditDetail auditDetail = getAuditDetail(securityGroupRights, count, fields);
				if (auditDetail != null) {
					auditDetails.add(auditDetail);
					count++;
				}
			}
		}
		logger.debug("Leaving ");
		return auditDetails;
	}

	/**
	 * 
	 * @param securityGroupRights
	 * @param auditSeq
	 * @param fields
	 * @return AuditDetail
	 */
	private AuditDetail getAuditDetail(SecurityGroupRights securityGroupRights, int auditSeq, String[] fields) {
		logger.debug("Entering ");

		if (securityGroupRights == null) {
			return null;
		}
		String auditImage = "";
		Object befImage = null;
		if (securityGroupRights.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
			auditImage = PennantConstants.TRAN_ADD;
		}
		if (securityGroupRights.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			auditImage = PennantConstants.TRAN_DEL;
			befImage = securityGroupRights;
		}
		logger.debug("Leaving ");
		return new AuditDetail(auditImage, auditSeq, fields[0], fields[1], befImage, securityGroupRights);
	}
	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public SecurityGroup getSecurityGroup() {
		return securityGroup;
	}

	public void setSecurityGroup(SecurityGroup securityGroup) {
		this.securityGroup = securityGroup;
	}

	public SecurityGroupRights getSecGroupRights() {
		return secGroupRights;
	}

	public void setSecGroupRights(SecurityGroupRights secGroupRights) {
		this.secGroupRights = secGroupRights;
	}

	public List<SecurityRight> getSecgroupsList() {
		return secRightsList;
	}

	public void setSecgroupsList(List<SecurityRight> secRightsList) {
		this.secRightsList = secRightsList;
	}

	public SecurityGroupRightsService getSecurityGroupRightsService() {
		return securityGroupRightsService;
	}

	public void setSecurityGroupRightsService(SecurityGroupRightsService securityGroupRightsService) {
		this.securityGroupRightsService = securityGroupRightsService;
	}

	public Map<Long, SecurityRight> getNewAssignedMap() {
		return newAssignedMap;
	}

	public void setNewAssignedMap(Map<Long, SecurityRight> newAssignedMap) {
		this.newAssignedMap = newAssignedMap;
	}

	public Map<Long, SecurityRight> getOldAssignedMap() {
		return oldAssignedMap;
	}

	public void setOldAssignedMap(Map<Long, SecurityRight> oldAssignedMap) {
		this.oldAssignedMap = oldAssignedMap;
	}
}
