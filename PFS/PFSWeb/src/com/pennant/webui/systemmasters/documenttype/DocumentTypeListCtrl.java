/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. 
 * All components/modules/functions/classes/logic in this software, unless 
 * otherwise stated, the property of Pennant Technologies. 
 * 
 * Copyright and other intellectual property laws protect these materials. 
 * Reproduction or retransmission of the materials, in whole or in part, in any manner, 
 * without the prior written consent of the copyright holder, is a violation of 
 * copyright law.
 */

/**
 ********************************************************************************************
 *                                 FILE HEADER                                              *
 ********************************************************************************************
 *																							*
 * FileName    		:  DocumentTypeListCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  05-05-2011    														*
 *                                                                  						*
 * Modified Date    :  05-05-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 05-05-2011       Pennant	                 0.1                                            * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 ********************************************************************************************
 */

package com.pennant.webui.systemmasters.documenttype;

import java.io.Serializable;
import java.util.HashMap;

import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.FieldComparator;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Window;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.ModuleMapping;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.systemmasters.DocumentType;
import com.pennant.backend.service.systemmasters.DocumentTypeService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.webui.systemmasters.documenttype.model.DocumentTypeListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.PTListReportUtils;
import com.pennant.webui.util.PTMessageUtils;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * /WEB-INF/pages/SystemMaster/DocumentType/DocumentTypeList.zul file.<br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * 
 */
public class DocumentTypeListCtrl extends GFCBaseListCtrl<DocumentType> implements Serializable {

	private static final long serialVersionUID = -2450046413192453914L;
	private final static Logger logger = Logger.getLogger(DocumentTypeListCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by
	 * our 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window 		window_DocumentTypeList; 		// autoWired
	protected Borderlayout 	borderLayout_DocumentTypeList; 	// autoWired
	protected Paging 		pagingDocumentTypeList; 		// autoWired
	protected Listbox 		listBoxDocumentType; 			// autoWired

	// List headers
	protected Listheader listheader_DocTypeCode; 		// autoWired
	protected Listheader listheader_DocTypeDesc; 		// autoWired
	protected Listheader listheader_DocIsMandatory; 	// autoWired
	protected Listheader listheader_DocTypeIsActive; 	// autoWired
	protected Listheader listheader_RecordStatus; 		// autoWired
	protected Listheader listheader_RecordType;

	// checkRights
	protected Button btnHelp; 											// autoWired
	protected Button button_DocumentTypeList_NewDocumentType; 			// autoWired
	protected Button button_DocumentTypeList_DocumentTypeSearchDialog; 	// autoWired
	protected Button button_DocumentTypeList_PrintList; 				// autoWired

	// NEEDED for the ReUse in the SearchWindow
	protected JdbcSearchObject<DocumentType> searchObj;

	private transient DocumentTypeService documentTypeService;
	private transient WorkFlowDetails workFlowDetails = null;

	/**
	 * default constructor.<br>
	 */
	public DocumentTypeListCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the List window we check, if the
	 * ZUL-file is called with a parameter for a selected DocumentType object in
	 * a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_DocumentTypeList(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		ModuleMapping moduleMapping = PennantJavaUtil.getModuleMap("DocumentType");
		boolean wfAvailable = true;

		if (moduleMapping.getWorkflowType() != null) {
			workFlowDetails = WorkFlowUtil.getWorkFlowDetails("DocumentType");

			if (workFlowDetails == null) {
				setWorkFlowEnabled(false);
			} else {
				setWorkFlowEnabled(true);
				setFirstTask(getUserWorkspace().isRoleContains(workFlowDetails.getFirstTaskOwner()));
				setWorkFlowId(workFlowDetails.getId());
			}
		} else {
			wfAvailable = false;
		}

		/* set components visible dependent of the users rights */
		doCheckRights();

		/**
		 * Calculate how many rows have been place in the list box. Get the
		 * currentDesktopHeight from a hidden IntBox from the index.zul that are
		 * filled by onClientInfo() in the indexCtroller
		 */
		this.borderLayout_DocumentTypeList.setHeight(getBorderLayoutHeight());

		// set the paging parameters
		this.pagingDocumentTypeList.setPageSize(getListRows());
		this.pagingDocumentTypeList.setDetailed(true);

		this.listheader_DocTypeCode.setSortAscending(new FieldComparator("docTypeCode", true));
		this.listheader_DocTypeCode.setSortDescending(new FieldComparator("docTypeCode", false));
		this.listheader_DocTypeDesc.setSortAscending(new FieldComparator("docTypeDesc", true));
		this.listheader_DocTypeDesc.setSortDescending(new FieldComparator("docTypeDesc", false));
		this.listheader_DocIsMandatory.setSortAscending(new FieldComparator("docIsMandatory", true));
		this.listheader_DocIsMandatory.setSortDescending(new FieldComparator("docIsMandatory", false));
		this.listheader_DocTypeIsActive.setSortAscending(new FieldComparator("docTypeIsActive", true));
		this.listheader_DocTypeIsActive.setSortDescending(new FieldComparator("docTypeIsActive", false));

		if (isWorkFlowEnabled()) {
			this.listheader_RecordStatus.setSortAscending(new FieldComparator("recordStatus", true));
			this.listheader_RecordStatus.setSortDescending(new FieldComparator("recordStatus", false));
			this.listheader_RecordType.setSortAscending(new FieldComparator("recordType", true));
			this.listheader_RecordType.setSortDescending(new FieldComparator("recordType", false));
		} else {
			this.listheader_RecordStatus.setVisible(false);
			this.listheader_RecordType.setVisible(false);
		}

		// ++ create the searchObject and initialize sorting ++//
		this.searchObj = new JdbcSearchObject<DocumentType>(DocumentType.class, getListRows());
		this.searchObj.addSort("DocTypeCode", false);

		// Work flow
		if (isWorkFlowEnabled()) {
			this.searchObj.addTabelName("BMTDocumentTypes_View");
			if (isFirstTask()) {
				button_DocumentTypeList_NewDocumentType.setVisible(true);
			} else {
				button_DocumentTypeList_NewDocumentType.setVisible(false);
			}
			this.searchObj.addFilterIn("nextRoleCode", getUserWorkspace().getUserRoles(), isFirstTask());
		} else {
			this.searchObj.addTabelName("BMTDocumentTypes_AView");
		}

		setSearchObj(this.searchObj);
		if (!isWorkFlowEnabled() && wfAvailable) {
			this.button_DocumentTypeList_NewDocumentType.setVisible(false);
			this.button_DocumentTypeList_DocumentTypeSearchDialog.setVisible(false);
			this.button_DocumentTypeList_PrintList.setVisible(false);
			PTMessageUtils.showErrorMessage(PennantJavaUtil.getLabel("WORKFLOW CONFIG NOT FOUND"));
		} else {
			// Set the ListModel for the articles.
			getPagedListWrapper().init(this.searchObj, this.listBoxDocumentType, this.pagingDocumentTypeList);
			// set the itemRenderer
			this.listBoxDocumentType.setItemRenderer(new DocumentTypeListModelItemRenderer());
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * SetVisible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug("Entering");

		getUserWorkspace().alocateAuthorities("DocumentTypeList");
		this.button_DocumentTypeList_NewDocumentType.setVisible(getUserWorkspace()
				.isAllowed("button_DocumentTypeList_NewDocumentType"));
		this.button_DocumentTypeList_DocumentTypeSearchDialog.setVisible(getUserWorkspace()
				.isAllowed("button_DocumentTypeList_DocumentTypeFindDialog"));
		this.button_DocumentTypeList_PrintList.setVisible(getUserWorkspace()
				.isAllowed("button_DocumentTypeList_PrintList"));
		logger.debug("Leaving");
	}

	/**
	 * This method is forwarded from the list boxes item renderer. <br>
	 * see: com.pennant.webui.bmtmasters.documenttype.model.
	 * DocumentTypeListModelItemRenderer.java <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onDocumentTypeItemDoubleClicked(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		// get the selected DocumentType object
		final Listitem item = this.listBoxDocumentType.getSelectedItem();

		if (item != null) {
			// CAST AND STORE THE SELECTED OBJECT
			final DocumentType aDocumentType = (DocumentType) item.getAttribute("data");
			final DocumentType documentType = getDocumentTypeService().getDocumentTypeById(aDocumentType.getId());

			if (documentType == null) {

				String[] valueParm = new String[2];
				String[] errorParm = new String[2];

				valueParm[0] = aDocumentType.getDocTypeCode();
				errorParm[0] = PennantJavaUtil.getLabel("label_DispatchModeCode") + ":"	+ aDocumentType.getDocTypeCode();

				ErrorDetails errorDetails = ErrorUtil.getErrorDetail(
						new ErrorDetails(PennantConstants.KEY_FIELD, "41005", errorParm, valueParm), getUserWorkspace().getUserLanguage());
				PTMessageUtils.showErrorMessage(errorDetails.getErrorMessage());
			} else {
				String whereCond = " AND DocTypeCode='"	+ documentType.getDocTypeCode()
				+ "' AND version=" + documentType.getVersion() + " ";

				if (isWorkFlowEnabled()) {
					boolean userAcces = validateUserAccess(workFlowDetails.getId(), getUserWorkspace().getLoginUserDetails().getLoginUsrID(),
							"DocumentType", whereCond, documentType.getTaskId(), documentType.getNextTaskId());
					if (userAcces) {
						showDetailView(documentType);
					} else {
						PTMessageUtils.showErrorMessage(Labels.getLabel("RECORD_NOTALLOWED"));
					}
				} else {
					showDetailView(documentType);
				}
			}
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Call the DocumentType dialog with a new empty entry. <br>
	 */
	public void onClick$button_DocumentTypeList_NewDocumentType(Event event)
	throws Exception {
		logger.debug("Entering" + event.toString());
		// create a new DocumentType object, We GET it from the back end.
		final DocumentType aDocumentType = getDocumentTypeService().getNewDocumentType();
		showDetailView(aDocumentType);
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Opens the detail view. <br>
	 * Over handed some parameters in a map if needed. <br>
	 * 
	 * @param DocumentType
	 *            (aDocumentType)
	 * @throws Exception
	 */
	private void showDetailView(DocumentType aDocumentType) throws Exception {
		logger.debug("Entering");

		/*
		 * We can call our Dialog ZUL-file with parameters. So we can call them
		 * with a object of the selected item. For handed over these parameter
		 * only a Map is accepted. So we put the object in a HashMap.
		 */
		if (aDocumentType.getWorkflowId() == 0 && isWorkFlowEnabled()) {
			aDocumentType.setWorkflowId(workFlowDetails.getWorkFlowId());
		}

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("documentType", aDocumentType);
		/*
		 * we can additionally handed over the listBox or the controller self,
		 * so we have in the dialog access to the list box List model. This is
		 * fine for synchronizing the data in the DocumentTypeListbox from the
		 * dialog when we do a delete, edit or insert a DocumentType.
		 */
		map.put("documentTypeListCtrl", this);

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents(
					"/WEB-INF/pages/SystemMaster/DocumentType/DocumentTypeDialog.zul", null, map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / " + e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving");
	}

	/**
	 * when the "help" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnHelp(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		PTMessageUtils.showHelpWindow(event, window_DocumentTypeList);
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
		this.pagingDocumentTypeList.setActivePage(0);
		Events.postEvent("onCreate", this.window_DocumentTypeList, event);
		this.window_DocumentTypeList.invalidate();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * call the DocumentType dialog
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClick$button_DocumentTypeList_DocumentTypeSearchDialog(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		/*
		 * we can call our DocumentTypeDialog ZUL-file with parameters. So we
		 * can call them with a object of the selected DocumentType. For handed
		 * over these parameter only a Map is accepted. So we put the
		 * DocumentType object in a HashMap.
		 */
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("documentTypeCtrl", this);
		map.put("searchObject", this.searchObj);

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents(
					"/WEB-INF/pages/SystemMaster/DocumentType/DocumentTypeSearchDialog.zul", null, map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / " + e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * When the documentType print button is clicked.
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	@SuppressWarnings("unused")
	public void onClick$button_DocumentTypeList_PrintList(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		PTListReportUtils reportUtils = new PTListReportUtils("DocumentType", getSearchObj(),this.pagingDocumentTypeList.getTotalSize()+1);
		logger.debug("Leaving" + event.toString());
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public void setDocumentTypeService(DocumentTypeService documentTypeService) {
		this.documentTypeService = documentTypeService;
	}
	public DocumentTypeService getDocumentTypeService() {
		return this.documentTypeService;
	}

	public JdbcSearchObject<DocumentType> getSearchObj() {
		return this.searchObj;
	}
	public void setSearchObj(JdbcSearchObject<DocumentType> searchObj) {
		this.searchObj = searchObj;
	}
}