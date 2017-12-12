package com.pennant.webui.finance.financemain;

import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.GroupsModelArray;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.QueueAssignmentHeader;
import com.pennant.backend.service.PagedListService;
import com.pennant.backend.service.QueueAssignmentService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.search.SearchResult;
import com.pennant.webui.finance.financemain.model.QueueAssignmentListComparator;
import com.pennant.webui.finance.financemain.model.QueueAssignmentListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;

public class QueueAssignmentListCtrl extends GFCBaseListCtrl<QueueAssignmentHeader> {
	private static final long serialVersionUID = -727353070679277569L;
	private static final Logger logger = Logger
			.getLogger(QueueAssignmentListCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_QueueAssignmentList; // autoWired
	protected Borderlayout borderLayout_QueueAssignmentList; // autoWired
	protected Paging pagingQueueAssignmentList; // autoWired
	protected Listbox listBoxQueueAssignment; // autoWired
	protected Textbox assignmentType;
	
	// List headers
	protected Listheader listheader_UserLevel; // autoWired
	protected Listheader listheader_RecordCount; // autoWired

	// checkRights
	protected Button btnHelp; // autoWired
	protected Button button_QueueAssignmentList_NewAssignment; // autoWired
	protected Button button_QueueAssignmentList_QueueAssignmentSearchDialog; // autoWired
	protected Button button_QueueAssignmentList_PrintList; // autoWired

	// NEEDED for the ReUse in the SearchWindow
	protected JdbcSearchObject<QueueAssignmentHeader> searchObj;
	
	private transient QueueAssignmentService queueAssignmentService;
	private transient boolean isManual = false;

	/**
	 * default constructor.<br>
	 */
	public QueueAssignmentListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		moduleCode = "QueueAssignment";
	}

	/**
	 * Before binding the data and calling the List window we check, if the
	 * ZUL-file is called with a parameter for a selected QueueAssingment object in
	 * a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_QueueAssignmentList(Event event) throws Exception {
		logger.debug("Entering");
		
		this.borderLayout_QueueAssignmentList.setHeight(getBorderLayoutHeight());
		this.listBoxQueueAssignment.setHeight(getListBoxHeight(0));

		// set the paging parameters
		this.pagingQueueAssignmentList.setPageSize(getListRows());
		this.pagingQueueAssignmentList.setDetailed(true);
		
		if(StringUtils.trimToEmpty(assignmentType.getValue()).equals(PennantConstants.MANUAL_ASSIGNMENT)){
			isManual = true;
		}
		
		doSetFieldProperties();
		
		// ++ create the searchObject and initial sorting ++//
		this.searchObj = new JdbcSearchObject<QueueAssignmentHeader>(QueueAssignmentHeader.class, getListRows());
		this.searchObj.addSort("UserRoleCode", false);
		this.searchObj.addSort("AssignedCount", false);
		this.searchObj.addField("UserRoleCode");
		this.searchObj.addField("AssignedCount");
		this.searchObj.addField("RoleDesc");
		if(isManual){
			this.searchObj.addTabelName("Task_Assignments_MView");	
		}else {
			this.searchObj.addField("UserId");
			this.searchObj.addField("LovDescUserName");
			this.searchObj.addTabelName("Task_Assignments_View");
		}

		setSearchObj(this.searchObj);

		// set the itemRenderer
		findSearchObject();
		this.listBoxQueueAssignment.setItemRenderer(new QueueAssignmentListModelItemRenderer());
		logger.debug("Leaving" + event.toString());
	}
	
	/**
	 * Internal Method for Grouping List items
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void findSearchObject(){
		logger.debug("Entering");
		final SearchResult<QueueAssignmentHeader> searchResult = getPagedListService().getSRBySearchObject(
				this.searchObj);
		if(isManual){
			getPagedListWrapper().init(this.searchObj, this.listBoxQueueAssignment,this.pagingQueueAssignmentList);
		}else {
			this.listBoxQueueAssignment.setModel(new GroupsModelArray(
					searchResult.getResult().toArray(),new QueueAssignmentListComparator()));
		}
		logger.debug("Leaving");
	}
	
	private void doSetFieldProperties(){
		logger.debug("Entering");
		logger.debug("Leaving");
	}
	
	/**
	 * This method is forwarded from the listBoxes item renderer. <br>
	 * see: com.pennant.webui.finance.financemain.model.
	 * AssignmentListModelItemRenderer.java <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onQueueAssignmentItemDoubleClicked(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		// get the selected QueueAssignmentHeader object
		final Listitem item = this.listBoxQueueAssignment.getSelectedItem();

		if (item != null) {
			// CAST AND STORE THE SELECTED OBJECT
			QueueAssignmentHeader aQueueAssignmentHeader = (QueueAssignmentHeader) item.getAttribute("data");
			aQueueAssignmentHeader.setModule(PennantConstants.WORFLOW_MODULE_FINANCE);
			aQueueAssignmentHeader.setManualAssign(isManual);
			
			aQueueAssignmentHeader = getQueueAssignmentService().getFinances(aQueueAssignmentHeader);
			if(aQueueAssignmentHeader.isNewRecord()){
				aQueueAssignmentHeader.setWorkflowId(getWorkFlowId());
				aQueueAssignmentHeader.setNextTaskId("");
			}
			
			Map<String, Object> map = getDefaultArguments();
			map.put("aQueueAssignmentHeader", aQueueAssignmentHeader);
			/*
			 * we can additionally handed over the listBox or the controller self,
			 * so we have in the dialog access to the listBox ListModel. This is
			 * fine for synchronizing the data in the QueueAssignmentListbox from the
			 * dialog when we do a delete, edit or insert a QueueAssignment.
			 */
			map.put("assignmentListCtrl", this);

			// call the ZUL-file with the parameters packed in a map
			try {
				Executions.createComponents(
						"/WEB-INF/pages/Finance/QueueAssignment/QueueAssignmentDialog.zul", null, map);
			} catch (Exception e) {
				MessageUtil.showError(e);
			}
			}
		logger.debug("Leaving" + event.toString());
	}
	
	/**
	 * when the "refresh" button is clicked. <br>
	 * <br>
	 * Refreshes the view by calling the onCreate event manually.
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnRefresh(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		findSearchObject();
		logger.debug("Leaving" + event.toString());
	}
	
	public JdbcSearchObject<QueueAssignmentHeader> getSearchObj() {
		return searchObj;
	}
	public void setSearchObj(JdbcSearchObject<QueueAssignmentHeader> searchObj) {
		this.searchObj = searchObj;
	}
	
	public Listbox getListBoxAssignment() {
		return listBoxQueueAssignment;
	}
	public void setListBoxAssignment(Listbox listBoxAssignment) {
		this.listBoxQueueAssignment = listBoxAssignment;
	}

	public QueueAssignmentService getQueueAssignmentService() {
		return queueAssignmentService;
	}

	public void setQueueAssignmentService(
			QueueAssignmentService queueAssignmentService) {
		this.queueAssignmentService = queueAssignmentService;
	}

	public PagedListService getPagedListService() {
		return pagedListService;
	}

	public void setPagedListService(PagedListService pagedListService) {
		this.pagedListService = pagedListService;
	}
	
}
