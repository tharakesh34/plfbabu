package com.pennanttech.framework.web;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.access.annotation.Secured;
import org.zkoss.spring.SpringUtil;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Path;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Hlayout;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Longbox;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Row;
import org.zkoss.zul.South;
import org.zkoss.zul.Space;
import org.zkoss.zul.Tabbox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Timebox;
import org.zkoss.zul.West;
import org.zkoss.zul.Window;

import com.pennant.AccountSelectionBox;
import com.pennant.CurrencyBox;
import com.pennant.ExtendedCombobox;
import com.pennant.FrequencyBox;
import com.pennant.QueryBuilder;
import com.pennant.UserWorkspace;
import com.pennant.app.constants.ImplementationConstants;
import com.pennant.backend.model.Notes;
import com.pennant.backend.model.Property;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.service.NotesService;
import com.pennant.backend.service.lmtmasters.FinanceWorkFlowService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.component.Uppercasebox;
import com.pennant.webui.util.pagging.PagedListWrapper;
import com.pennant.webui.util.searching.SearchOperators;
import com.pennanttech.framework.security.core.service.UserService;
import com.pennanttech.framework.web.components.ButtonControl;
import com.pennanttech.pennapps.core.engine.workflow.WorkflowEngine;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.jdbc.search.Filter;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * A skeletal composer that will be extended by the abstract list and dialog controllers. This provides common
 * "auto-wired" accessible variable objects such as embedded objects, components, and external resolvable variables in a
 * ZK zul page.
 */
public abstract class AbstractController<T> extends GenericForwardComposer<Component> implements Serializable {
	private static final long serialVersionUID = -1171206258809472640L;
	private static final Logger logger = LogManager.getLogger(AbstractController.class);

	public static final int LIST_AREA_HEIGHT_OFFSET = 58;

	/**
	 * Returns the pixel width of the client's desktop.
	 * 
	 * @return The pixel width of the client's desktop.
	 */
	public static int getDesktopWidth() {
		Intbox desktopWidth = (Intbox) Path.getComponent("/outerIndexWindow/currentDesktopWidth");

		return desktopWidth.getValue() == null ? 0 : desktopWidth.getValue().intValue();
	}

	/**
	 * Returns the pixel height of the client's desktop.
	 * 
	 * @return The pixel height of the client's desktop.
	 */
	public static int getDesktopHeight() {
		Intbox desktopHeight = (Intbox) Path.getComponent("/outerIndexWindow/currentDesktopHeight");

		return desktopHeight.getValue() == null ? 0 : desktopHeight.getValue().intValue();
	}

	/**
	 * Returns the content area's pixel height excluding the header and footer.
	 * 
	 * @return The content area's pixel height.
	 */
	protected int getContentAreaHeight() {
		return getDesktopHeight() - LIST_AREA_HEIGHT_OFFSET;
	}

	// TODO:

	protected Window window;
	protected final Borderlayout borderlayoutMain = (Borderlayout) Path
			.getComponent("/outerIndexWindow/borderlayoutMain");
	private transient boolean workFlowEnabled;
	private transient long workFlowId = 0;
	private transient boolean firstTask;
	private transient String firstTaskRole;
	protected transient boolean enqiryModule;
	protected transient String moduleCode;

	protected Button print;
	protected Button help;

	public static final String RIGHT_NOT_ACCESSIBLE = "RIGHT_NOT_ACCESSIBLE";

	public boolean isWorkFlowEnabled() {
		return workFlowEnabled;
	}

	public void setWorkFlowEnabled(boolean workFlowEnabled) {
		this.workFlowEnabled = workFlowEnabled;
	}

	public long getWorkFlowId() {
		return this.workFlowId;
	}

	public void setWorkFlowId(long workFlowId) {
		this.workFlowId = workFlowId;
	}

	public boolean isFirstTask() {
		return firstTask;
	}

	public void setFirstTask(boolean firstTask) {
		this.firstTask = firstTask;
	}

	public String getFirstTaskRole() {
		return firstTaskRole;
	}

	public void setFirstTaskRole(String firstTaskRole) {
		this.firstTaskRole = firstTaskRole;
	}

	protected String pageRightName;
	protected List<ButtonControl> buttonControls = new ArrayList<ButtonControl>();

	/**
	 * A map of parameters that is accessible by <code>this</code> variable
	 * 
	 */
	protected transient Map<String, Object> arguments;

	protected void setPageComponents(Window window) {
		this.window = window;
	}

	protected void setWindow(Window window) {
		this.window = window;
	}

	private transient UserService userService;
	protected transient FinanceWorkFlowService financeWorkFlowService;

	protected transient WorkflowEngine workFlow = null;
	protected transient String role = "";
	// Variables that are required for workflow

	protected West menuWest;
	protected Groupbox groupboxMenu;
	private Map<String, List<ErrorDetail>> overideMap = new HashMap<String, List<ErrorDetail>>();
	private String reaOnlyStyle = "#F2F2F2";
	private String generalStyle = "#FFFFFF";

	protected String nextRoleCode = "";
	protected String taskId = "";
	protected String nextTaskId = "";
	protected boolean auditingReq;
	protected String operationRefs = "";
	private boolean validation = true;

	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);

		doLoadArguments();

		doSetProperties();
	}

	/**
	 * Set the module level properties.
	 */
	protected void doSetProperties() {
		// TODO: To be made abstract after implementing in all the list controllers.
	}

	/**
	 * <p>
	 * Load a map of parameters that is accessible by the <code>arguments</code> Map variable.
	 * <p>
	 * 
	 * <p>
	 * This arguments may be configured either in the following.
	 * </p>
	 * 
	 * <pre>
	 * 1. Through <code>mainmenu.xml</code>
	 * zulNavigation = &quot;/WEB-INF/pages/SystemMaster/EmployerDetail/EmployerDetailList.zul?enquiryPage=Y&quot;
	 * 
	 * <pre>
	 * 1. Through Controller classes. 
	 * final HashMap&lt;String, Object&gt; map = new HashMap&lt;String, Object&gt;();
	 * map.put(&quot;DialogCtrl&quot;, this);
	 * map.put(&quot;filtertype&quot;, &quot;Extended&quot;);
	 * map.put(&quot;searchObject&quot;, this.custCIFSearchObject);
	 * Executions.createComponents(&quot;/WEB-INF/pages/CustomerMasters/Customer/CustomerSelect.zul&quot;, null, map);
	 * </pre>
	 * 
	 */
	protected final void doLoadArguments() {
		Map<?, ?> arg = Executions.getCurrent().getArg();

		if (arg != null) {
			arguments = new HashMap<String, Object>();
			for (Object key : arg.keySet()) {
				arguments.put(key.toString(), arg.get(key));
			}
		}
	}

	protected void registerButton(Button button) {
		buttonControls.add(new ButtonControl(button));
	}

	protected void registerButton(Button button, String rightName) {
		buttonControls.add(new ButtonControl(button, rightName));
	}

	protected void registerButton(Button button, String rightName, boolean firstTaskButton) {
		buttonControls.add(new ButtonControl(button, rightName, firstTaskButton));
	}

	// FIXME: Name to be changed to doCheckRights
	/**
	 * Set Visible for components by checking if there's a right for it.
	 */
	protected void checkRights() {
		logger.debug("Entering");

		Button button;

		if (enqiryModule) {
			for (ButtonControl buttonControl : buttonControls) {
				button = buttonControl.getButton();
				if (buttonControl.isfirstTaskButton()) {
					button.setVisible(false);
				}
			}

			return;
		}

		getUserWorkspace().allocateAuthorities(pageRightName);

		String buttonRight;
		for (ButtonControl buttonControl : buttonControls) {
			button = buttonControl.getButton();
			buttonRight = buttonControl.getRightName();

			if (RIGHT_NOT_ACCESSIBLE.equals(buttonRight)) {
				button.setVisible(false);
				continue;
			}

			if (buttonControl.isfirstTaskButton() && workFlowEnabled) {
				if (isFirstTask()) {
					button.setVisible(true); // TODO Need to check the right?
				} else {
					button.setVisible(false);
				}
			} else if (buttonRight != null) {
				button.setVisible(getUserWorkspace().isAllowed(buttonRight));
			}
		}

		logger.debug("Leaving");
	}

	private transient UserWorkspace userWorkspace;

	@Override
	public void onEvent(Event evt) throws Exception {
		final Object controller = getController();
		final Method mtd = ComponentsCtrl.getEventMethod(controller.getClass(), evt.getName());

		if (mtd != null) {
			isAllowed(mtd);
		}
		setWindowStyle(evt);

		borderLayoutHeight = getContentAreaHeight();
		super.onEvent(evt);
	}

	/**
	 * @param evt
	 * 
	 *            Set the style window.setContentStyle("padding:0px") <br>
	 * 
	 *            List zul's window and it's parent window and Dialog zul's window
	 */
	private void setWindowStyle(Event evt) {
		if (evt.getTarget() != null && !"outerIndexWindow".equals(evt.getTarget().getId())) {
			Window window = null;
			if (evt.getTarget() instanceof Window) {
				window = (Window) evt.getTarget();
				if (window.getId().indexOf("List") > 0) {
					window.setContentStyle("padding:0px");
					if (window.getParent() instanceof Window) {
						window = (Window) window.getParent();
						window.setContentStyle("padding:0px");
					}
				} else if (window.getParent() instanceof Window) {
					window = (Window) window.getParent();
					window.setContentStyle("padding:0px");
				}
			}
		}
	}

	/**
	 * With this method we get the @Secured Annotation for a method.<br>
	 * Captured the method call and check if it's allowed. <br>
	 * 
	 * @param mtd
	 */
	private void isAllowed(Method mtd) {
		Annotation[] annotations = mtd.getAnnotations();
		for (Annotation annotation : annotations) {
			if (annotation instanceof Secured) {
				Secured secured = (Secured) annotation;
				for (String rightName : secured.value()) {
					if (!userWorkspace.isAllowed(rightName)) {
						throw new SecurityException("Call of this method is not allowed! Missing right: \n\n"
								+ "needed RightName: " + rightName + "\n\n" + "Method: " + mtd);
					}
				}
				return;
			}
		}
	}

	private int listRows;
	private static final int listRowHeight = 28;
	private static final int rowheight = 26;
	public int borderLayoutHeight = 0;

	public int getGridRows() {
		return Math.round(this.borderLayoutHeight / rowheight) - 1;
	}

	public int getListRows() {
		return listRows;
	}

	public String getBorderLayoutHeight() {
		return getContentAreaHeight() + "px";
	}

	public String getListBoxHeight(int gridRowCount) {
		int listBoxHeight = getContentAreaHeight();
		listBoxHeight = listBoxHeight - (gridRowCount * rowheight) - (rowheight);
		this.listRows = Math.round(listBoxHeight / listRowHeight) - 1;
		return listBoxHeight + "px";
	}

	public String getListBoxWidth(int width) {
		int listBoxWidth = getDesktopWidth();
		listBoxWidth = listBoxWidth - width;

		return listBoxWidth + "px";
	}

	final protected UserWorkspace getUserWorkspace() {
		return userWorkspace;
	}

	public void setUserWorkspace(UserWorkspace userWorkspace) {
		this.userWorkspace = userWorkspace;
	}

	public UserService getUserService() {
		return this.userService;
	}

	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	public WorkflowEngine getWorkFlow() {
		return workFlow;
	}

	public void setWorkFlow(WorkflowEngine workFlow) {
		this.workFlow = workFlow;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public void doLoadWorkFlow(boolean workFlowEnabled, long workFlowId, String nextTaskID) {
		this.workFlowEnabled = workFlowEnabled;
		this.workFlowId = workFlowId;

		if (this.workFlowEnabled) {
			setWorkFlow(new WorkflowEngine(WorkFlowUtil.getWorkflow(workFlowId).getWorkFlowXml()));
			doLoadWorkFlow(nextTaskID);
		}
	}

	public void doLoadWorkFlow(boolean workFlowEnabled, long workFlowId, String nextTaskID, String roleCode) {
		this.workFlowEnabled = workFlowEnabled;
		this.workFlowId = workFlowId;

		if (this.workFlowEnabled) {
			setWorkFlow(new WorkflowEngine(WorkFlowUtil.getWorkflow(workFlowId).getWorkFlowXml()));
			nextTaskID = getTaskId(roleCode);
			doLoadWorkFlow(nextTaskID);
		}
	}

	public void doLoadWorkFlow(String nextTaskID) {

		// set the Role
		boolean firstTaskOwner;

		if (StringUtils.isBlank(nextTaskID)) {
			this.role = this.workFlow.allFirstTaskOwners();

			if (this.workFlow.allFirstTaskOwners().contains(PennantConstants.DELIMITER_COMMA)) {
				String[] firest = this.workFlow.allFirstTaskOwners().split(PennantConstants.DELIMITER_COMMA);
				for (String string : firest) {
					if (userWorkspace.isRoleContains(string)) {
						this.role = string;
						break;
					}
				}
			}

		} else {
			String[] nextTasks = nextTaskID.split(";");

			if (nextTasks != null && nextTasks.length > 0) {
				String currentRole = "";

				for (int i = 0; i < nextTasks.length; i++) {
					currentRole = getTaskOwner(nextTasks[i]);
					if (userWorkspace.isRoleContains(currentRole)) {
						this.role = currentRole;
						break;
					}
				}

				// this.role = this.workFlow.getTaskOwner(nextTasks[0]);
			} else {
				this.role = getTaskOwner(nextTaskID);
			}
		}

		firstTaskOwner = false;
		if (this.workFlow.allFirstTaskOwners().contains(PennantConstants.DELIMITER_COMMA)) {
			String[] froles = this.workFlow.allFirstTaskOwners().split(PennantConstants.DELIMITER_COMMA);
			for (int i = 0; i < froles.length; i++) {
				if (this.role.equals(froles[i])) {
					firstTaskOwner = true;
				}
			}

		} else {
			if (this.role.equals(this.workFlow.allFirstTaskOwners())) {
				firstTaskOwner = true;
			}
		}

		if (firstTaskOwner && StringUtils.isBlank(nextTaskID)) {
			this.firstTask = true;
		} else {
			this.firstTask = false;
		}

	}

	public String getCurrentTab() {
		final Borderlayout borderlayout = (Borderlayout) Path.getComponent("/outerIndexWindow/borderlayoutMain");
		final Tabbox tabbox = (Tabbox) borderlayout.getFellow("center").getFellow("divCenter")
				.getFellow("tabBoxIndexCenter");
		return tabbox.getSelectedTab().getId();
	}

	public boolean isReadOnly(String componentName) {
		if (enqiryModule) {
			return true;
		} else if (isWorkFlowEnabled()) {
			return getUserWorkspace().isReadOnly(componentName);
		}
		return false;
	}

	// Remove notes entered for the version
	public void deleteNotes(Notes notes, boolean allNotes) {
		NotesService notesService = (NotesService) SpringUtil.getBean("notesService");
		if (allNotes) {
			notesService.deleteAllNotes(notes);
		} else {
			notesService.delete(notes);
		}

	}

	public Map<String, List<ErrorDetail>> getOverideMap() {
		return overideMap;
	}

	public void setOverideMap(Map<String, List<ErrorDetail>> overideMap) {
		this.overideMap = overideMap;
	}

	public void clearField(Combobox combobox) {
		int count = combobox.getItemCount();
		for (int i = 0; i < count; i++) {
			combobox.removeItemAt(0);
		}
	}

	/**
	 * Method for Getting Selected value From ComboBox
	 * 
	 * @param combobox
	 * @return
	 */
	public String getComboboxValue(Combobox combobox) {
		String comboValue = "";
		if (combobox.getSelectedItem() != null) {
			comboValue = combobox.getSelectedItem().getValue().toString();
		} else {
			combobox.setSelectedIndex(0);
		}
		return comboValue;
	}

	/**
	 * Method to validate the combo box
	 * 
	 * @param Combobox (combobox) String (label)
	 */
	public boolean isValidComboValue(Combobox combobox, String label) {
		if (!combobox.isDisabled() && combobox.getSelectedIndex() <= 0) {
			throw new WrongValueException(combobox, Labels.getLabel("STATIC_INVALID", new String[] { label }));
		}
		return true;
	}

	public void fillComboBox(Combobox combobox, String value, List<ValueLabel> list, String excludeFields) {
		logger.trace(Literal.ENTERING);

		combobox.getChildren().clear();

		Comboitem comboitem = new Comboitem();
		comboitem.setValue(PennantConstants.List_Select);
		comboitem.setLabel(Labels.getLabel("Combo.Select"));

		combobox.appendChild(comboitem);
		combobox.setSelectedItem(comboitem);
		combobox.setReadonly(true);

		for (ValueLabel valueLabel : list) {
			if (!excludeFields.contains("," + valueLabel.getValue() + ",")) {
				comboitem = new Comboitem();
				comboitem.setValue(valueLabel.getValue());
				comboitem.setLabel(valueLabel.getLabel());
				combobox.appendChild(comboitem);
			}
			if (StringUtils.trimToEmpty(value).equals(StringUtils.trim(valueLabel.getValue()))) {
				combobox.setSelectedItem(comboitem);
			}
		}

		logger.trace(Literal.LEAVING);
	}

	protected void fillComboBox(Combobox combobox, String value, List<ValueLabel> list) {
		fillComboBox(combobox, value, list, "");
	}

	protected List<ValueLabel> excludeComboBox(List<ValueLabel> actualList, Set<String> exludeList) {
		List<ValueLabel> list = new ArrayList<>();

		for (ValueLabel item : actualList) {
			if (!exludeList.contains(item.getValue())) {
				list.add(item);
			}
		}

		return list;
	}

	public void fillList(Combobox combobox, String value, List<ValueLabel> list, String excludeFields) {
		combobox.getChildren().clear();

		for (ValueLabel valueLabel : list) {
			if (!excludeFields.contains("," + valueLabel.getValue() + ",")) {
				Comboitem comboitem = new Comboitem();
				comboitem.setValue(valueLabel.getValue());
				comboitem.setLabel(valueLabel.getLabel());
				combobox.setReadonly(true);
				combobox.appendChild(comboitem);

				if (StringUtils.trimToEmpty(value).equals(StringUtils.trim(valueLabel.getValue()))) {
					combobox.setSelectedItem(comboitem);
				}
			}
		}
	}

	public void fillList(Combobox combobox, String value, List<ValueLabel> list) {
		fillList(combobox, value, list, "");
	}

	public void fillList(Combobox component, List<Property> properties, Object selectedKey) {
		logger.trace(Literal.ENTERING);

		component.setReadonly(true);

		// Clear the existing items.
		component.getChildren().clear();

		// Add the default item.
		Comboitem comboitem = new Comboitem();
		comboitem.setValue(PennantConstants.List_Select);
		comboitem.setLabel(Labels.getLabel("Combo.Select"));

		component.appendChild(comboitem);
		component.setSelectedItem(comboitem);

		// Add the list of items.
		for (Property property : properties) {
			comboitem = new Comboitem();
			comboitem.setValue(property.getKey());
			comboitem.setLabel(property.getValue());

			component.appendChild(comboitem);

			if (property.getKey().equals(selectedKey)) {
				component.setSelectedItem(comboitem);
			}
		}

		logger.trace(Literal.LEAVING);
	}

	/**
	 * Method to fill the combobox with given list of values and will exclude the the values
	 * 
	 * @param combobox
	 * @param value
	 * @param list
	 */
	public void fillComboBox(Combobox combobox, String value, List<ValueLabel> list, List<String> exculdeValues) {
		logger.trace(Literal.ENTERING);

		combobox.getChildren().clear();
		Comboitem comboitem = new Comboitem();
		comboitem.setValue(PennantConstants.List_Select);
		comboitem.setLabel(Labels.getLabel("Combo.Select"));
		combobox.appendChild(comboitem);
		combobox.setSelectedItem(comboitem);
		combobox.setReadonly(true);
		for (ValueLabel valueLabel : list) {
			if (exculdeValues != null && !exculdeValues.isEmpty() && exculdeValues.contains(valueLabel.getValue())) {
				continue;
			}
			comboitem = new Comboitem();
			comboitem.setValue(valueLabel.getValue());
			comboitem.setLabel(valueLabel.getLabel());
			combobox.appendChild(comboitem);
			if (StringUtils.trimToEmpty(value).equals(StringUtils.trim(valueLabel.getValue()))) {
				combobox.setSelectedItem(comboitem);
			}
		}

		logger.trace(Literal.LEAVING);
	}

	public void readOnlyComponent(boolean isReadOnly, Component component) {
		if (isReadOnly) {
			if (component instanceof Combobox) {
				((Combobox) component).setTabindex(-1);
				((Combobox) component).setAutodrop(false);
				((Combobox) component).setReadonly(true);
				((Combobox) component).setStyle(reaOnlyStyle);
				((Combobox) component).setButtonVisible(false);
				((Combobox) component).setAutocomplete(false);
				((Combobox) component).setDisabled(true);
			} else if (component instanceof Datebox) {
				((Datebox) component).setReadonly(true);
				((Datebox) component).setTabindex(-1);
				((Datebox) component).setButtonVisible(false);
				((Datebox) component).setStyle(reaOnlyStyle);
			} else if (component instanceof Timebox) {
				((Timebox) component).setReadonly(true);
				((Timebox) component).setTabindex(-1);
				((Timebox) component).setReadonly(true);
				((Timebox) component).setStyle(reaOnlyStyle);
				((Timebox) component).setButtonVisible(false);
			} else if (component instanceof Intbox) {
				((Intbox) component).setReadonly(true);
				((Intbox) component).setTabindex(-1);
			} else if (component instanceof Decimalbox) {
				((Decimalbox) component).setReadonly(true);
				((Decimalbox) component).setTabindex(-1);
			} else if (component instanceof Listbox) {
				((Listbox) component).setTabindex(-1);
			} else if (component instanceof Textbox) {
				((Textbox) component).setReadonly(true);
				((Textbox) component).setTabindex(-1);
			} else if (component instanceof Checkbox) {
				((Checkbox) component).setDisabled(true);
			} else if (component instanceof Button) {
				((Button) component).setDisabled(true);
				((Button) component).setTabindex(-1);
			} else if (component instanceof Longbox) {
				((Longbox) component).setReadonly(true);
				((Longbox) component).setTabindex(-1);
			} else if (component instanceof ExtendedCombobox) {
				((ExtendedCombobox) component).setReadonly(true);
			} else if (component instanceof CurrencyBox) {
				((CurrencyBox) component).setReadonly(true);
				// ((CurrencyBox) component).setMandatory(false);
			} else if (component instanceof QueryBuilder) {
				((QueryBuilder) component).setEditable(false);
			} else if (component instanceof AccountSelectionBox) {
				((AccountSelectionBox) component).setReadonly(true);
			} else if (component instanceof FrequencyBox) {
				((FrequencyBox) component).setDisabled(true);
			} else if (component instanceof Radio) {
				((Radio) component).setDisabled(true);
			}
		} else {
			if (component instanceof Combobox) {
				((Combobox) component).setTabindex(0);
				((Combobox) component).setAutodrop(true);
				((Combobox) component).setButtonVisible(true);
				((Combobox) component).setStyle(reaOnlyStyle);
				((Combobox) component).setReadonly(true);
				((Combobox) component).setDisabled(false);
			} else if (component instanceof Datebox) {
				((Datebox) component).setTabindex(0);
				((Datebox) component).setButtonVisible(true);
				((Datebox) component).setReadonly(false);
				((Datebox) component).setStyle(reaOnlyStyle);
			} else if (component instanceof Timebox) {
				((Timebox) component).setTabindex(0);
				((Timebox) component).setButtonVisible(true);
				((Timebox) component).setReadonly(false);
				((Timebox) component).setStyle(generalStyle);
			} else if (component instanceof Intbox) {
				((Intbox) component).setTabindex(0);
				((Intbox) component).setReadonly(false);
			} else if (component instanceof Decimalbox) {
				((Decimalbox) component).setTabindex(0);
				((Decimalbox) component).setReadonly(false);
			} else if (component instanceof Listbox) {
				((Listbox) component).setTabindex(0);
			} else if (component instanceof Textbox) {
				((Textbox) component).setReadonly(false);
				((Textbox) component).setTabindex(0);
			} else if (component instanceof Checkbox) {
				((Checkbox) component).setDisabled(false);
			} else if (component instanceof Button) {
				((Button) component).setDisabled(false);
				((Button) component).setTabindex(0);
			} else if (component instanceof Longbox) {
				((Longbox) component).setTabindex(0);
			} else if (component instanceof ExtendedCombobox) {
				((ExtendedCombobox) component).setReadonly(false);
			} else if (component instanceof CurrencyBox) {
				((CurrencyBox) component).setReadonly(false);
				((CurrencyBox) component).setTabindex(0);
			} else if (component instanceof QueryBuilder) {
				((QueryBuilder) component).setEditable(true);
			} else if (component instanceof AccountSelectionBox) {
				((AccountSelectionBox) component).setReadonly(false);
			} else if (component instanceof FrequencyBox) {
				((FrequencyBox) component).setDisabled(false);
			} else if (component instanceof Radio) {
				((Radio) component).setDisabled(false);
			}
		}
	}

	// Set The Component Property like Visibility,ReadOnly.

	public void setComponentAccessType(String rightName, boolean isReadOnly, Component component, Space space,
			Label label, Hlayout hlayout, Row row) {

		int accessType = 1;

		if (isWorkFlowEnabled()) {
			// accessType = userWorkspace.getAccessType(rightName);
			// TODO Temporary Fix to be changed Later
			if (isReadOnly(rightName)) {
				accessType = 0;
			}
		}

		if (accessType == -1) {
			label.setVisible(false);
			hlayout.setVisible(false);
			readOnlyComponent(true, component);
			setRowInvisible(row, hlayout, null);
		} else {
			label.setVisible(true);
			hlayout.setVisible(true);
			if (accessType == 0 || isReadOnly) {
				readOnlyComponent(true, component);
				if (space != null) {
					space.setSclass("");
				}

			} else {
				readOnlyComponent(false, component);
				if (space != null) {
					if (component instanceof Checkbox) {
						space.setSclass("");
					} else {
						space.setSclass("mandatory");
					}
				}
			}
		}
	}

	public void setComponentAccessType(String rightName, boolean isReadOnly, Component component, Space space,
			Label label) {

		int accessType = 1;

		if (isWorkFlowEnabled()) {
			// accessType = userWorkspace.getAccessType(rightName);
			// TODO Temporary Fix to be changed Later
			if (isReadOnly(rightName)) {
				accessType = 0;
			}
		}

		if (accessType == -1) {
			label.setVisible(false);
			// hlayout.setVisible(false);
			readOnlyComponent(true, component);
			// setRowInvisible(row, hlayout, null);
		} else {
			label.setVisible(true);
			// hlayout.setVisible(true);
			if (accessType == 0 || isReadOnly) {
				readOnlyComponent(true, component);
				if (space != null) {
					space.setSclass("");
				}

			} else {
				readOnlyComponent(false, component);
				if (space != null) {
					if (component instanceof Checkbox) {
						space.setSclass("");
					} else {
						space.setSclass("mandatory");
					}
				}
			}
		}
	}

	public void setRowInvisible(Row row, Component comp1, Component comp2) {
		boolean visible = false;

		if (comp1 == null || comp1.isVisible()) {
			visible = true;
		}
		if (!visible && (comp2 == null || comp2.isVisible())) {
			visible = true;
		}
		if (row != null) {
			row.setVisible(visible);
		}
	}

	// ////=======================new ===============
	// Get the notes entered for rejected reason
	public static Notes getNotes(String moduleName, String notesKey, int version) {
		Notes notes = new Notes();
		notes.setModuleName(moduleName);
		notes.setReference(notesKey);
		notes.setVersion(version);
		return notes;
	}

	public void setLovAccess(String rightName, boolean isReadOnly, Button lovButton, Space space, Label label,
			Hlayout hlayout, Row row) {
		setComponentAccessType(rightName, isReadOnly, lovButton, space, label, hlayout, row);

		if (lovButton.isDisabled()) {
			lovButton.setVisible(false);
		} else {
			lovButton.setVisible(true);
		}
	}

	public void setStatusDetails(Groupbox gbStatusDetails, Groupbox groupBoxWf, South south, boolean enqModule) {
		if (isWorkFlowEnabled()) {
			if (gbStatusDetails != null) {
				gbStatusDetails.setVisible(true);
			}
			if (enqModule) {
				groupBoxWf.setVisible(false);
				south.setHeight("60px");
			}
		} else {
			if (gbStatusDetails != null) {
				gbStatusDetails.setVisible(false);
			}
		}
	}

	public void getWorkFlowDetails(String userAction, String nextTaskId, Object beanObject) {

		setTaskId(getTaskId(getRole()));
		setNextTaskId(StringUtils.trimToEmpty(nextTaskId));

		if ("Save".equals(userAction)) {
			setNextTaskId(getTaskId() + ";");
		} else {
			setNextTaskId(getNextTaskId().replaceFirst(getTaskId() + ";", ""));

			if (StringUtils.isBlank(getNextTaskId())) {
				setNextTaskId(getNextTaskIds(getTaskId(), beanObject));
			}
			setAuditingReq(isNotesMandatory(getTaskId(), beanObject));
		}

		if (!StringUtils.isBlank(getNextTaskId())) {
			String[] nextTasks = getNextTaskId().split(";");

			if (nextTasks != null && nextTasks.length > 0) {
				for (int i = 0; i < nextTasks.length; i++) {

					if (getNextRoleCode().length() > 1) {
						setNextRoleCode(getNextRoleCode() + ",");
					}
					setNextRoleCode(getTaskOwner(nextTasks[i]));
				}
			} else {
				setNextRoleCode(getTaskOwner(nextTaskId));
			}
		}
		setOperationRefs(getServiceOperations(getTaskId(), beanObject));
		setValidation(true);
	}

	public void setExtAccess(String rightName, boolean isReadOnly, ExtendedCombobox extendedCombobox, Row row) {
		setExtAccess(rightName, isReadOnly, extendedCombobox, row, true);
	}

	public void setCurrencyBoxAccess(String rightName, boolean isReadOnly, CurrencyBox currencyBox, Row row) {
		setExtAccess(rightName, isReadOnly, currencyBox, row, true);
	}

	public void setAccountBoxAccess(String rightName, boolean isReadOnly, AccountSelectionBox accountSelectionBox,
			Row row) {
		setExtAccess(rightName, isReadOnly, accountSelectionBox, row, true);
	}

	public void setExtAccess(String rightName, boolean isReadOnly, Component component, Row row, boolean mandatory) {

		int accessType = 1;
		if (isWorkFlowEnabled()) {
			// accessType = userWorkspace.getAccessType(rightName);
			// TODO Temporary Fix to be changed Later
			if (isReadOnly(rightName)) {
				accessType = 0;
			}
		}
		if (accessType == -1) {
			readOnlyComponent(true, component);
		} else {
			if (accessType == 0 || isReadOnly) {
				readOnlyComponent(true, component);
			} else {
				readOnlyComponent(false, component);
				if (component instanceof ExtendedCombobox) {
					((ExtendedCombobox) component).setMandatoryStyle(mandatory);
				} else if (component instanceof AccountSelectionBox) {
					((AccountSelectionBox) component).setMandatoryStyle(mandatory);
				} else if (component instanceof CurrencyBox) {
					((CurrencyBox) component).setMandatory(mandatory);
				}
			}
		}
	}

	/**
	 * Set Component Accesss for Query Builder
	 * 
	 * @param rightName
	 * @param isReadOnly
	 * @param queryBuilder
	 * @param row
	 */
	public void setQueryAccess(String rightName, boolean isReadOnly, QueryBuilder queryBuilder, Row row) {

		int accessType = 1;
		if (isWorkFlowEnabled()) {
			// accessType = userWorkspace.getAccessType(rightName);
			// TODO Temporary Fix to be changed Later
			if (isReadOnly(rightName)) {
				accessType = 0;
			}
		}
		if (accessType == -1) {
			readOnlyComponent(true, queryBuilder);
		} else {
			if (accessType == 0 || isReadOnly) {
				readOnlyComponent(true, queryBuilder);
			} else {
				readOnlyComponent(false, queryBuilder);
			}
		}
	}

	public static Listbox setRecordType(Listbox recordType) {
		recordType.appendItem(Labels.getLabel("Combo.All"), "");
		recordType.appendItem(PennantJavaUtil.getLabel(PennantConstants.RECORD_TYPE_NEW),
				PennantConstants.RECORD_TYPE_NEW);

		recordType.appendItem(PennantJavaUtil.getLabel(PennantConstants.RECORD_TYPE_UPD),
				PennantConstants.RECORD_TYPE_UPD);
		recordType.appendItem(PennantJavaUtil.getLabel(PennantConstants.RECORD_TYPE_DEL),
				PennantConstants.RECORD_TYPE_DEL);
		recordType.setSelectedIndex(0);
		return recordType;
	}

	public void setProps(CurrencyBox currencyBox, boolean mandatory, int format, int width) {
		currencyBox.setMandatory(mandatory);
		currencyBox.setFormat(PennantApplicationUtil.getAmountFormate(format));
		currencyBox.setScale(format);
		currencyBox.setTextBoxWidth(width);
	}

	public String getNextRoleCode() {
		return nextRoleCode;
	}

	public void setNextRoleCode(String nextRoleCode) {
		this.nextRoleCode = nextRoleCode;
	}

	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	public String getNextTaskId() {
		return nextTaskId;
	}

	public void setNextTaskId(String nextTaskId) {
		this.nextTaskId = nextTaskId;
	}

	public void setAuditingReq(boolean auditingReq) {
		this.auditingReq = auditingReq;
	}

	public String getOperationRefs() {
		return operationRefs;
	}

	public void setOperationRefs(String operationRefs) {
		this.operationRefs = operationRefs;
	}

	public boolean isValidation() {
		return validation;
	}

	public void setValidation(boolean validation) {
		this.validation = validation;
	}

	public boolean validateUserAccess(long workFlowId, long userID, String modName, String whereCond, String taskID,
			String nextTaskId) {
		return true;
	}

	/*
	 * Method For Getting UsrFinAuthentication By Branch and Division
	 */
	public String getUsrFinAuthenticationQry(boolean isForReports) {
		StringBuilder wherQuery = new StringBuilder();

		String divisionField = "";
		if (isForReports) {
			divisionField = "FinDivision";
		} else {
			divisionField = "lovDescFinDivision";
		}
		wherQuery.append(" FinBranch In( Select distinct UserBranch from SecurityUserDivBranch where userDivision =");
		wherQuery.append(divisionField);
		wherQuery.append(" and usrid =");
		wherQuery.append(getUserWorkspace().getLoggedInUser().getUserId());
		wherQuery.append(")");

		return wherQuery.toString();
	}

	public void setFinanceWorkFlowService(FinanceWorkFlowService financeWorkFlowService) {
		this.financeWorkFlowService = financeWorkFlowService;
	}

	protected PagedListWrapper<T> pagedListWrapper;

	public PagedListWrapper<T> getPagedListWrapper() {
		return pagedListWrapper;
	}

	public void setPagedListWrapper(PagedListWrapper<T> pagedListWrapper) {
		this.pagedListWrapper = pagedListWrapper;
	}

	public JdbcSearchObject<T> getSearchFilter(JdbcSearchObject<T> searchObj, Listitem selectedItem, Object value,
			String fieldName) {

		if (selectedItem != null) {
			int searchOpId = -1;
			if (selectedItem.getAttribute("data") != null) {
				searchOpId = ((SearchOperators) selectedItem.getAttribute("data")).getSearchOperatorId();
			}

			switch (searchOpId) {
			case -1:
				break;
			case Filter.OP_LIKE:
				searchObj.addFilter(new Filter(fieldName, "%" + value + "%", searchOpId));
				break;
			case Filter.OP_IN:
				searchObj.addFilter(new Filter(fieldName, String.valueOf(value).trim().split(","), searchOpId));
				break;
			case Filter.OP_NOT_IN:
				searchObj.addFilter(new Filter(fieldName, String.valueOf(value).trim().split(","), searchOpId));
				break;
			default:
				searchObj.addFilter(new Filter(fieldName, value, searchOpId));
				break;
			}
		}

		return searchObj;
	}

	public JdbcSearchObject<T> getSearchFilter(JdbcSearchObject<T> searchObj, int filter, Object value,
			String fieldName) {
		switch (filter) {
		case -1:
			break;
		case Filter.OP_LIKE:
			searchObj.addFilter(new Filter(fieldName, "%" + value + "%", filter));
			break;
		case Filter.OP_IN:
			searchObj.addFilter(new Filter(fieldName, String.valueOf(value).trim().split(","), filter));
			break;
		case Filter.OP_NOT_IN:
			searchObj.addFilter(new Filter(fieldName, String.valueOf(value).trim().split(","), filter));
			break;
		default:
			searchObj.addFilter(new Filter(fieldName, value, filter));
			break;
		}

		return searchObj;
	}

	protected void doShowErrorMessage(String message, boolean closeWindow) {
		MessageUtil.showError(message);

		if (closeWindow) {
			this.window.onClose();
		}
	}

	// Workflow related methods.
	protected String getTaskOwner(String taskId) {
		return workFlow.getUserTask(taskId).getActor();
	}

	protected String getTaskAssignmentMethod(String taskId) {
		return workFlow.getUserTask(taskId).getAssignmentLevel();
	}

	protected String getTaskBaseRole(String taskId) {
		return workFlow.getUserTask(taskId).getBaseActor();
	}

	protected String getTaskTabs(String taskId) {
		return StringUtils.join(workFlow.getUserTask(taskId).getAdditionalForms(), ',');
	}

	protected String getFirstTaskOwner() {
		return workFlow.allFirstTaskOwners();
	}

	protected String getTaskId(String owner) {
		return workFlow.getUserTaskId(owner);
	}

	protected String getServiceOperations(String taskId, Object object) {
		String result = workFlow.getServiceOperationsAsString(taskId, object);
		if (StringUtils.isNotBlank(result)) {
			result += ";";
		}

		return result;
	}

	protected String getNextTaskIds(String taskId, Object object) {
		String result = workFlow.getNextUserTaskIdsAsString(taskId, object);
		if (StringUtils.isNotBlank(result)) {
			result += ";";
		}

		return result;
	}

	protected boolean isNotesMandatory(String taskId, Object object) {
		return workFlow.isNotesMandatory(taskId, object);
	}

	/**
	 * Returns value of the argument configured as configured as query string for the page in mainmenu.xml
	 * 
	 * @param argumentName Name of the query string argument.
	 * @return argumentValue Value of the query string argument
	 */
	protected String getArgument(String argumentName) {
		String argumentValue = null;
		if (arguments != null) {
			Object object = arguments.get(argumentName);

			if (object != null) {
				if (object instanceof String) {
					argumentValue = (String) arguments.get(argumentName);
				} else if (object instanceof Boolean) {
					argumentValue = String.valueOf(arguments.get(argumentName));
				}
			}
		}
		return argumentValue;
	}

	/**
	 * Shows Workflow window.
	 * 
	 * @param workFlowId  (long)
	 * @param moduleCode  (String)
	 * @param keyValue    (Object)
	 * @param fromAuditId (long)
	 * @param toAuditId   (long).
	 */
	protected void doShowWorkflow(long workFlowId, String moduleCode, Object keyValue, long fromAuditId,
			long toAuditId) {
		Map<String, Object> arg = new HashMap<>();

		arg.put("workFlowId", workFlowId);
		arg.put("moduleCode", moduleCode);
		arg.put("keyValue", keyValue);
		arg.put("fromAuditId", fromAuditId);
		arg.put("toAuditId", toAuditId);

		Executions.createComponents("/WEB-INF/pages/util/ProcessView.zul", window, arg);
	}

	/**
	 * Shows Activity Log Window
	 * 
	 * @param keyValue (Object)
	 * @param map      (Map<String, Object>).
	 **/
	protected void doShowActivityLog(Object keyValue, Map<String, Object> map) {
		Map<String, Object> arg = new HashMap<>();

		arg.put("moduleCode", moduleCode);
		arg.put("keyValue", keyValue);
		arg.put("map", map);

		if (moduleCode == null) {
			arg.put("moduleCode", map.get("moduleCode"));
		}

		Executions.createComponents("/WEB-INF/pages/Enquiry/FinanceInquiry/ActivityLog.zul", window, arg);
	}

	/**
	 * Set Focus Component true when multiple group boxes
	 * 
	 * @param component
	 * @return
	 */
	protected boolean setComponentFocus(Component component) {
		logger.debug("Entering");

		if (!(component instanceof Button)) {
			if (component instanceof CurrencyBox) {
				CurrencyBox ccyBox = (CurrencyBox) component;
				ccyBox.setFocus(true);
				return true;
			}
			if (component instanceof Textbox) {
				Textbox textbox = (Textbox) component;
				textbox.setFocus(true);
				return true;
			}
			if (component instanceof ExtendedCombobox) {
				ExtendedCombobox extendedCombobox = (ExtendedCombobox) component;
				extendedCombobox.setFocus(true);
				return true;
			}
			if (component instanceof Intbox) {
				Intbox intbox = (Intbox) component;
				intbox.setFocus(true);
				return true;
			}
			if (component instanceof Combobox) {
				Combobox combobox = (Combobox) component;
				combobox.setFocus(true);
				return true;
			}
			if (component instanceof Uppercasebox) {
				Uppercasebox uppercasebox = (Uppercasebox) component;
				uppercasebox.setFocus(true);
				return true;
			}
			if (component instanceof Longbox) {
				Longbox longbox = (Longbox) component;
				longbox.setFocus(true);
				return true;
			}
			if (component instanceof Decimalbox) {
				Decimalbox decimalbox = (Decimalbox) component;
				decimalbox.setFocus(true);
				return true;
			}
			if (component instanceof Datebox) {
				Datebox datebox = (Datebox) component;
				datebox.setFocus(true);
				return true;
			}
			if (component instanceof Checkbox) {
				Checkbox checkbox = (Checkbox) component;
				checkbox.setFocus(true);
				return true;
			}
			if (component instanceof Radiogroup) {
				Radiogroup radiogroup = (Radiogroup) component;
				radiogroup.setFocus(true);
				return true;
			}
		}
		logger.debug("Leaving");
		return false;
	}

	/*
	 * Method For Getting UsrFinAuthentication By Branch and Division
	 */
	public String getUsrFinAuthenticationQry(boolean isForReports, String tableName) {
		StringBuilder wherQuery = new StringBuilder();

		long userId = getUserWorkspace().getLoggedInUser().getUserId();

		if (ImplementationConstants.ALLOW_ACCESS_CONTROL_TYPE) {
			// String tableName="";
			wherQuery.append(" exists (select 1 from secuserhierarchydetail t");
			wherQuery.append(" where t.usrid = ");
			wherQuery.append(userId);
			wherQuery.append(" and t.division = ");
			wherQuery.append(tableName);
			wherQuery.append(".lovdescfindivision");
			wherQuery.append(" and t.businessvertical = ");
			wherQuery.append(tableName);
			wherQuery.append(".businessvertical");
			wherQuery.append(" and t.fintype = ");
			wherQuery.append(tableName);
			wherQuery.append(".fintype");
			wherQuery.append(" and t.branch = ");
			wherQuery.append(tableName);
			wherQuery.append(".finbranch");
			wherQuery.append(" )");
		} else {
			String divisionField = "";
			if (isForReports) {
				divisionField = "FinDivision";
			} else {
				divisionField = "lovDescFinDivision";
			}
			wherQuery.append(" FinBranch In(select UserBranch from SecurityUserDivBranch where userDivision =");
			wherQuery.append(divisionField);
			wherQuery.append(" and usrid =");
			wherQuery.append(userId);
			wherQuery.append(")");
		}

		return wherQuery.toString();
	}

	protected List<String> getWorkFlowRoles() {
		List<String> roleCodes = new ArrayList<>();

		List<String> userRoles = getUserWorkspace().getUserRoles();

		WorkFlowDetails workFlowDetails = WorkFlowUtil.getWorkFlowDetails(this.moduleCode);

		List<String> workflowRoles = Arrays.asList(workFlowDetails.getRoles());

		for (String userRole : userRoles) {
			for (String workflowRole : workflowRoles) {
				if (workflowRole.equals(userRole)) {
					roleCodes.add(workflowRole);
				}
			}
		}

		return roleCodes;
	}
}
