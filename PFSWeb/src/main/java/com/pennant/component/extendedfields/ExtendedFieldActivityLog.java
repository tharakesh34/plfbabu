package com.pennant.component.extendedfields;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DurationFormatUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.A;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Html;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listgroup;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Row;
import org.zkoss.zul.Rows;
import org.zkoss.zul.Window;

import com.pennant.backend.model.Notes;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.webui.ActivityLogConstants;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.activity.log.Activity;
import com.pennanttech.activity.log.ActivityLogService;
import com.pennanttech.pennapps.core.engine.workflow.WorkflowEngine;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.web.util.MessageUtil;

public class ExtendedFieldActivityLog extends GFCBaseCtrl<Object> implements Comparator<Notes> {

	private static final Logger logger = LogManager.getLogger(ExtendedFieldActivityLog.class);

	private static final long serialVersionUID = 1L;

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the ZUL-file
	 * are getting autoWired by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	private Window window_ExtendedFieldActivityLog; // autoWired
	private Borderlayout borderlayoutActivityLog; // autoWired
	private Rows activityLog_Rows; // autoWired
	private Listbox listBoxActivityLog; // autoWired

	private ActivityLogService activityLogService;

	// private String moduleCode;
	private int seqNo;
	private long instructionUID;
	private String tableName;
	private String keyValue;
	List<Notes> notesList;

	/**
	 * Before binding the data and calling the dialog window we check, if the ZUL-file is called with a parameter for a
	 * selected record data like moduleCode,keyValue,label and value in a Map.
	 * 
	 * @param event
	 */
	public void onCreate$window_ExtendedFieldActivityLog(ForwardEvent event) {
		logger.debug(Literal.ENTERING);
		try {
			// Get the arguments.
			tableName = (String) arguments.get("tableName");

			seqNo = (Integer) arguments.get("seqNo");

			instructionUID = (Long) arguments.get("instructionUID");

			keyValue = (String) arguments.get("keyValue");

			moduleCode = (String) arguments.get("moduleCode");

			Map<?, ?> map = (Map<?, ?>) arguments.get("map");

			doWriteBeanToComponents(map);

			// get activities
			List<Activity> activities = activityLogService.getExtendedFieldActivitiyLog(tableName, keyValue, seqNo,
					instructionUID);

			// Prepare Module Names

			List<String> moduleNames = new ArrayList<>();
			moduleNames.add(moduleCode);
			for (Activity activity : activities) {
				if (StringUtils.isNotEmpty(activity.getRcdMaintainSts())
						&& !moduleNames.contains(activity.getRcdMaintainSts())) {
					moduleNames.add(activity.getRcdMaintainSts());
				}
			}

			// get notesList
			notesList = activityLogService.getNotesList(keyValue, moduleNames);

			// Display the audit log.
			fillActivityLog(activities);
			Collections.sort(notesList, new ExtendedFieldActivityLog());

			doSetFieldProperties();
		} catch (Exception e) {
			MessageUtil.showError(e);
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param moduleCode (String)
	 * @param map        (LinkedHashMap<?, ?>)
	 */
	public void doWriteBeanToComponents(Map<?, ?> map) {
		logger.debug(Literal.ENTERING);
		Row row = null;
		Iterator<?> iterator = map.entrySet().iterator();
		int i = 0;
		try {
			while (iterator.hasNext()) {
				Map.Entry<?, ?> mapEntry = ((Map.Entry<?, ?>) iterator.next());

				if (i % 2 == 0) {
					row = new Row();
				}

				Label nameLabel = new Label();
				nameLabel.setValue(Labels.getLabel(String.valueOf(mapEntry.getKey())));
				row.appendChild(nameLabel);

				Label valueLabel = new Label();
				valueLabel.setValue(String.valueOf(mapEntry.getValue()));
				valueLabel.setStyle("font-weight:bold;");
				row.appendChild(valueLabel);

				row.setStyle("padding-top:5px;padding-bottom:5px");
				activityLog_Rows.appendChild(row);
				i++;

			}

		} catch (Exception e) {
			logger.error(e);
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Notes Item Clicked
	 * 
	 * @param event
	 */
	public void onNotesItemClicked(ForwardEvent event) {
		logger.debug(Literal.ENTERING);
		Button btnNotes = (Button) event.getOrigin().getTarget();
		List<Notes> userNotesList = new ArrayList<Notes>();
		final Activity activity = (Activity) btnNotes.getAttribute("data");
		int i = 0;

		while (i < notesList.size() && notesList.get(i).getInputDate().before(activity.getAuditDate())) {
			if (notesList.get(i).getInputBy() == activity.getLastMntBy()) {
				userNotesList.add(notesList.get(i));
			}
			i++;
		}
		try {
			Collections.reverse(userNotesList);
			Listbox listboxNotes = renderNotes(userNotesList);
			Window window = new Window();
			window.setTitle(Labels.getLabel("activityLog_Notes"));
			window.setClosable(true);
			listboxNotes.setParent(window);
			window.setParent(window_ExtendedFieldActivityLog);

			window.setHeight("65%");
			window.setWidth("65%");

			listboxNotes.setHeight("100%");
			listboxNotes.setStyle("overflow:auto");

			window.doModal();
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Render Notes
	 * 
	 * @param userNotesList
	 * @return
	 */
	public Listbox renderNotes(List<Notes> userNotesList) {
		logger.debug(Literal.ENTERING);

		// Retrieve Notes List By Module Reference
		Listbox listboxNotes = new Listbox();
		Listitem item = null;
		Listcell lc = null;
		String alignSide = "right";
		for (int i = 0; i < userNotesList.size(); i++) {

			Notes note = userNotesList.get(i);
			if (note != null) {

				item = new Listitem();
				lc = new Listcell();
				lc.setStyle("border:0px");
				Html html = new Html();

				if ("right".equals(alignSide)) {
					alignSide = "left";
				} else {
					alignSide = "right";
				}

				String content = "<p class='triangle-right " + alignSide + "'> <font style='font-weight:bold;'> "
						+ note.getRemarks() + " </font> <br>  ";
				String date = DateUtil.format(note.getInputDate(), PennantConstants.dateTimeAMPMFormat);
				if ("I".equals(note.getRemarkType())) {
					content = content + "<font style='color:#FF0000;float:" + alignSide + ";'>"
							+ note.getUsrLogin().toLowerCase() + " : " + date + "</font></p>";
				} else {
					content = content + "<font style='color:black;float:" + alignSide + ";'>"
							+ note.getUsrLogin().toLowerCase() + " : " + date + "</font></p>";
				}
				html.setContent(content);
				lc.appendChild(html);
				lc.setParent(item);
				listboxNotes.appendChild(item);
			}
		}
		logger.debug(Literal.LEAVING);
		return listboxNotes;
	}

	/**
	 * Fill Activities
	 * 
	 */
	private void fillActivityLog(List<Activity> activities) {
		logger.debug(Literal.ENTERING);

		if (notesList == null) {
			notesList = new ArrayList<Notes>();
		}
		listBoxActivityLog.getItems().clear();

		if (activities.size() > 0) {
			activities = filterActivities(activities, ActivityLogConstants.DISPLAY_PURGED_ACTIVITY,
					ActivityLogConstants.DISPLAY_LATEST_VERSION_ONLY);
		}

		int j = 0;
		boolean addHeader = true;
		Date prvAuditDate = null;
		long millis = 0L;
		Listgroup group;
		Listitem item;
		Listcell cell;
		A link = null;
		long fromAuditId = 0L;
		Button btn_Notes;
		WorkFlowDetails workFlow = null;
		Date prvActivityDate = null;

		for (Activity activity : activities) {
			if (addHeader) {
				// Creating list group per each process.
				group = new Listgroup();

				if (StringUtils.equals(moduleCode, "FinanceMain")) {
					if (activity.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
						cell = new Listcell("Loan Origination");
					} else {
						cell = new Listcell("Loan Maintenance (" + activity.getRcdMaintainSts() + ")");
					}
				} else {
					cell = new Listcell(moduleCode + " Extended Fields");
				}
				cell.setStyle("font-weight:bold;");
				group.appendChild(cell);

				link = new A("[FLOW]");
				link.setStyle("text-decoration:none");
				link.setTooltiptext(Labels.getLabel("btn_Worflow_Image.tooltiptext"));
				cell = new Listcell();
				cell.setStyle("text-align:right");
				cell.appendChild(link);
				group.appendChild(cell);

				listBoxActivityLog.appendChild(group);

				addHeader = false;
				prvAuditDate = null;
				fromAuditId = activity.getAuditId();
				workFlow = WorkFlowUtil.getWorkflow(activity.getWorkflowId());
			}

			item = new Listitem();

			WorkflowEngine we = new WorkflowEngine(workFlow.getWorkFlowXml());

			if (activity.getTaskId() != null) {
				cell = new Listcell(we.getUserTask(activity.getTaskId()).getName());
				cell.setParent(item);
			} else {
				cell = new Listcell("Approved");
				cell.setParent(item);
			}

			cell = new Listcell(activity.getRecordStatus());
			cell.setParent(item);

			cell = new Listcell(activity.getUserLogin());
			cell.setParent(item);

			cell = new Listcell(DateUtil.format(activity.getAuditDate(), "dd-MMM-yyyy HH:mm"));
			cell.setParent(item);

			if (prvAuditDate == null) {
				cell = new Listcell("");
			} else {
				millis = Math.abs(activity.getAuditDate().getTime() - prvAuditDate.getTime());
				cell = new Listcell(DurationFormatUtils.formatDuration(millis, "HH:mm:ss"));
			}
			cell.setParent(item);

			// Display Notes Button
			cell = new Listcell();
			btn_Notes = new Button("Notes");
			btn_Notes.setParent(cell);
			btn_Notes.setVisible(false);
			j = 0;
			while (j < notesList.size()) {
				if (notesList.get(j).getInputDate().before(activity.getAuditDate())
						&& notesList.get(j).getInputBy() == activity.getLastMntBy()
						&& notesList.get(j).getVersion() == activity.getVersion()
						&& (prvActivityDate != null && notesList.get(j).getInputDate().after(prvActivityDate))) {

					btn_Notes.setVisible(true);
					break;
				}
				j++;
			}
			cell.setParent(item);

			btn_Notes.setAttribute("data", activity);
			ComponentsCtrl.applyForward(btn_Notes, "onClick=onNotesItemClicked");

			listBoxActivityLog.appendChild(item);
			prvActivityDate = prvAuditDate = activity.getAuditDate();

			if (StringUtils.isBlank(activity.getNextTaskId())
					|| activities.indexOf(activity) == activities.size() - 1) {
				List<Long> list = new ArrayList<>();
				list.add(activity.getWorkflowId());
				list.add(fromAuditId);
				list.add(activity.getAuditId());
				link.addForward("onClick", self, "onClickProcessFlow", list);
				addHeader = true;
			}
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * gives the Latest Audit data of the Record.
	 * 
	 * @param activities (List<Activity>)
	 * 
	 * @return The list of filtered Activities.
	 */
	private List<Activity> filterActivities(List<Activity> activities, boolean displayPurgedActivities,
			boolean displayLatestVersionsOnly) {
		logger.debug(Literal.ENTERING);

		/*
		 * if (displayPurgedActivities && !displayLatestVersionsOnly) { return activities; }
		 */

		List<Activity> result = new ArrayList<>();
		List<List<Activity>> activityLists = new ArrayList<>();
		List<Activity> currVersion = new ArrayList<>();
		boolean eol = false;
		int prevVersion = 0;
		String prevAction = null;

		for (Activity activity : activities) {

			if (eol) {
				if (displayPurgedActivities && !displayLatestVersionsOnly) {

					activityLists.add(currVersion);
					currVersion = new ArrayList<>();

				} else {

					if (displayLatestVersionsOnly && activity.getVersion() == prevVersion) {
						currVersion.clear();
					}

					if (!displayPurgedActivities && "DELETE".equals(prevAction)) {
						currVersion.clear();
						activityLists.clear();
					}

					if (!currVersion.isEmpty()) {
						activityLists.add(currVersion);
						currVersion = new ArrayList<>();
					}
				}
				eol = false;
			}

			currVersion.add(activity);

			if (StringUtils.isBlank(activity.getNextTaskId())) {
				eol = true;
				prevVersion = activity.getVersion();
				prevAction = activity.getRecordType();
			}

		}

		if (!currVersion.isEmpty()) {
			activityLists.add(currVersion);
		}

		Activity latestActivity = activityLists.get(activityLists.size() - 1)
				.get(activityLists.get(activityLists.size() - 1).size() - 1);
		if (!StringUtils.isBlank(latestActivity.getNextRoleCode())) {
			latestActivity.setNextRoleCode("");
			latestActivity.setNextTaskId("");
		}

		for (int k = activityLists.size() - 1; k >= 0; k--) {
			result.addAll(activityLists.get(k));
		}

		logger.debug(Literal.LEAVING);
		return result;
	}

	public void onClickProcessFlow(ForwardEvent event) {
		logger.debug(Literal.ENTERING + event.toString());

		@SuppressWarnings("unchecked")
		List<Long> list = (List<Long>) event.getData();
		doShowWorkflow(list.get(0), moduleCode, keyValue, list.get(1), list.get(2));

		logger.debug(Literal.LEAVING + event.toString());
	}

	/**
	 * when the "close" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnClose(Event event) {
		logger.debug(Literal.ENTERING + event.toString());
		this.window_ExtendedFieldActivityLog.onClose();
		logger.debug(Literal.LEAVING + event.toString());
	}

	/**
	 * Set the properties of the fields, like width and height.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug(Literal.ENTERING);

		int groupBoxHeight = (activityLog_Rows.getVisibleItemCount() * 26);

		this.window_ExtendedFieldActivityLog.setWidth(getDesktopWidth() + "px");
		this.window_ExtendedFieldActivityLog.setHeight(getDesktopHeight() + "px");
		this.borderlayoutActivityLog.setHeight(getDesktopHeight() + "px");
		this.listBoxActivityLog.setHeight((getDesktopHeight() - 65 - groupBoxHeight) + "px");

		this.window_ExtendedFieldActivityLog.doModal();

		logger.debug(Literal.LEAVING);
	}

	public void setActivityLogService(ActivityLogService activityLogService) {
		this.activityLogService = activityLogService;
	}

	@Override
	public int compare(Notes o1, Notes o2) {
		return o1.getInputDate().compareTo(o2.getInputDate());
	}
}
