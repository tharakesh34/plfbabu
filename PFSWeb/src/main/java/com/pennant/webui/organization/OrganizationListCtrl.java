package com.pennant.webui.organization;

import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.webui.organization.model.OrganizationListModelItemRender;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.dataengine.util.DateUtil.DateFormat;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pff.organization.model.Organization;
import com.pennanttech.pff.organization.service.OrganizationService;

public class OrganizationListCtrl extends GFCBaseListCtrl<Organization> {
	private static final long serialVersionUID = 1L;

	private static final Logger logger = Logger.getLogger(OrganizationListCtrl.class);

	protected Window window_OrganizationList;
	protected Borderlayout borderLayout_OrganizationList;
	protected Listbox listBoxOrganization;
	protected Paging pagingOrganizationList;

	// List headers
	protected Listheader listheader_OrgType;
	protected Listheader listheader_CIF;
	protected Listheader listheader_OrgCode;
	protected Listheader listheader_Name;
	protected Listheader listheader_DateOfInc;

	// checkRights
	protected Button button_OrganizationList_NewOrganization;
	protected Button button_OrganizationList_OrganizationSearch;

	// Search Fields
	protected Listbox sortOperator_CIF;
	protected Listbox sortOperator_OrgType;
	protected Listbox sortOperator_OrgCode;
	protected Listbox sortOperator_Name;
	protected Listbox sortOperator_DateOfInc;

	protected Textbox orgType;
	protected Textbox cif;
	protected Textbox orgCode;
	protected Textbox name;
	protected Datebox dateOfInc;

	private String module = "";

	@Autowired
	private transient OrganizationService organizationService;

	/**
	 * default constructor.<br>
	 */
	public OrganizationListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "Organization";
		super.pageRightName = "OrganizationSchoolList";
		super.tableName = "organizations_view";
		super.queueTableName = "organizations_view";
		super.enquiryTableName = "Organizations_view";
		this.module = getArgument("module");
	}

	public void onCreate$window_OrganizationList(Event event) {
		logger.debug(Literal.ENTERING);

		if ("ENQ".equals(this.module)) {
			enqiryModule = true;
		}

		doSetFieldProperties();
		// Set the page level components.
		setPageComponents(window_OrganizationList, borderLayout_OrganizationList, listBoxOrganization,
				pagingOrganizationList);
		setItemRender(new OrganizationListModelItemRender());

		// Register buttons and fields.
		registerButton(button_OrganizationList_NewOrganization, "button_OrganizationSchoolList_btnNew", true);
		registerButton(button_OrganizationList_OrganizationSearch);

		registerField("id");
		registerField("type", listheader_OrgType, SortOrder.ASC, orgType, sortOperator_OrgType, Operators.STRING);
		registerField("cif", listheader_CIF, SortOrder.ASC, cif, sortOperator_CIF, Operators.STRING);
		registerField("code", listheader_OrgCode, SortOrder.ASC, orgCode, sortOperator_OrgCode, Operators.STRING);
		registerField("name", listheader_Name, SortOrder.ASC, name, sortOperator_Name,Operators.STRING);
		registerField("date_Incorporation", listheader_DateOfInc, SortOrder.ASC, dateOfInc, sortOperator_DateOfInc, Operators.STRING);
		
		// Render the page and display the data.
		doRenderPage();
		search();
	}
	
	private void doSetFieldProperties() {
		logger.debug(Literal.ENTERING);
		
		this.dateOfInc.setFormat(DateFormat.SHORT_DATE.getPattern());
		
		logger.debug(Literal.LEAVING);
	}
	
	/*@Override
	protected void doAddFilters() { //TODO
		super.doAddFilters();
		if (!enqiryModule) {
			this.searchObject.addFilter(new Filter("recordType", "", Filter.OP_NOT_EQUAL));
		}
			this.searchObject.removeFiltersOnProperty("agency");
			int id = OrganizationType.SCHOOL.getKey();
			this.searchObject.addFilter(new Filter("type", id, Filter.OP_EQUAL));
		

	}
*/
	/**
	 * The framework calls this event handler when user clicks the search button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$button_OrganizationList_OrganizationSearch(Event event) {
		search();
	}

	/**
	 * The framework calls this event handler when user clicks the refresh button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$btnRefresh(Event event) {
		doReset();
		search();
	}
	
	public void onClick$button_OrganizationList_NewOrganization(Event event) {
		logger.debug("Entering");

		// Create a new entity.
		Organization organization = new Organization();
		organization.setNewRecord(true);
		organization.setWorkflowId(getWorkFlowId());

		// Display the dialog page.
		doShowDialogPage(organization);

		logger.debug("Leaving");
	}
	/**
	 * The framework calls this event handler when user opens a record to view it's details. Show the dialog page with
	 * the selected entity.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */

	public void onOrganizationItemDoubleClicked(Event event) {
		logger.debug("Entering");

		// Get the selected record.
		Listitem selectedItem = this.listBoxOrganization.getSelectedItem();
		final long id = (long) selectedItem.getAttribute("id");
		Organization org = organizationService.getOrganization(id, "_View");

		if (org == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}

		StringBuilder whereCond = new StringBuilder();
		whereCond.append("  AND  Id = ");
		whereCond.append(org.getId());
		whereCond.append(" AND  version=");
		whereCond.append(org.getVersion());

		if (doCheckAuthority(org, whereCond.toString())) {
			// Set the latest work-flow id for the new maintenance request.
			if (isWorkFlowEnabled() && org.getWorkflowId() == 0) {
				org.setWorkflowId(getWorkFlowId());
			}
			doShowDialogPage(org);
		} else {
			MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Displays the dialog page with the required parameters as map.
	 * 
	 * @param fieldinvestigation
	 *            The entity that need to be passed to the dialog.
	 */
	private void doShowDialogPage(Organization organization) {
		logger.debug(Literal.ENTERING);

		Map<String, Object> arg = getDefaultArguments();
		arg.put("organization", organization);
		arg.put("organizationListCtrl", this);
		arg.put("enqiryModule", enqiryModule);
		arg.put("module", module);

		try {
			Executions.createComponents("/WEB-INF/pages/Organization/OrganizationDialog.zul",
					null, arg);
		} catch (Exception e) {
			logger.error("Exception:", e);
			MessageUtil.showError(e);
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the print button to print the results.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$print(Event event) {
		doPrintResults();
	}

	/**
	 * The framework calls this event handler when user clicks the help button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$help(Event event) {
		doShowHelp(event);
	}

	/**
	 * When user clicks on "fromApproved"
	 * 
	 * @param event
	 */
	public void onCheck$fromApproved(Event event) {
		search();
	}

	/**
	 * When user clicks on "fromWorkFlow"
	 * 
	 * @param event
	 */
	public void onCheck$fromWorkFlow(Event event) {
		search();
	}
	
}
