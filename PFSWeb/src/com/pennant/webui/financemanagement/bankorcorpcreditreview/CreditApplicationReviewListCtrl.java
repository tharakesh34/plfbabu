package com.pennant.webui.financemanagement.bankorcorpcreditreview;

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
	 * FileName    		:  CreditReviewDetailsListCtrl.java                                                   * 	  
	 *                                                                    						*
	 * Author      		:  PENNANT TECHONOLOGIES              									*
	 *                                                                  						*
	 * Creation Date    :  14-12-2011    														*
	 *                                                                  						*
	 * Modified Date    :  14-12-2011    														*
	 *                                                                  						*
	 * Description 		:                                             							*
	 *                                                                                          *
	 ********************************************************************************************
	 * Date             Author                   Version      Comments                          *
	 ********************************************************************************************
	 * 14-12-2011       Pennant	                 0.1                                            * 
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


	import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Path;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.FieldComparator;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Tabbox;
import org.zkoss.zul.Window;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.ModuleMapping;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.financemanagement.bankorcorpcreditreview.FinCreditReviewDetails;
import com.pennant.backend.model.financemanagement.bankorcorpcreditreview.FinCreditReviewSummary;
import com.pennant.backend.service.financemanagement.bankorcorpcreditreview.CreditApplicationReviewService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.webui.financemanagement.bankorcorpcreditreview.model.CreditApplicationReviewListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.PTMessageUtils;
import com.pennant.webui.util.PTReportUtils;

	/**
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
	 * This is the controller class for the
	 * /WEB-INF/pages/RulesFactory/FinCreditReviewDetails/CreditReviewDetailsList.zul file.<br>
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
	 * 
	 */
	public class CreditApplicationReviewListCtrl extends GFCBaseListCtrl<FinCreditReviewDetails> implements Serializable {

		private static final long serialVersionUID	= 4322539879503951300L;
		private final static Logger logger = Logger.getLogger(CreditApplicationReviewListCtrl.class);

		/*
		 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		 * All the components that are defined here and have a corresponding
		 * component with the same 'id' in the ZUL-file are getting autowired by our
		 * 'extends GFCBaseCtrl' GenericForwardComposer.
		 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		 */
		protected Window	    window_CreditApplicationReviewList;	     // autowired
		protected Borderlayout	borderLayout_CreditApplicationReviewList;	 // autowired
		protected Paging	    pagingCreditApplicationReviewList;	     // autowired
		protected Listbox	    listBoxCreditApplicationReview;	         // autowired

		// List headers
		protected Listheader	listheader_DetailId;	         // autowired
		protected Listheader	listheader_CustId;	         // autowired
		protected Listheader	listheader_CreditRevCode;	     // autowired
		protected Listheader	listheader_AuditedYear;	 // autowired
		protected Listheader	listheader_BankName;	 // autowired
		protected Listheader	listheader_RecordStatus;	     // autowired
		protected Listheader	listheader_RecordType;

		// checkRights
		protected Button	    btnHelp;	                                        // autowired
		protected Button	    button_CreditApplicationReviewList_NewCreditApplicationReview;	        // autowired
		protected Button	    button_CreditApplicationReviewList_CreditApplicationReviewSearchDialog;	// autowired
		protected Button	    button_CreditApplicationReviewList_PrintList;	                // autowired

		// NEEDED for the ReUse in the SearchWindow
		protected JdbcSearchObject<FinCreditReviewDetails>	searchObj;
		private transient CreditApplicationReviewService	  creditApplicationReviewService;
		
		private transient WorkFlowDetails	      workFlowDetails	= null;

		/**
		 * default constructor.<br>
		 */
		public CreditApplicationReviewListCtrl() {
			super();
		}

		// +++++++++++++++++++++++++++++++++++++++++++++++++ //
		// +++++++++++++++ Component Events ++++++++++++++++ //
		// +++++++++++++++++++++++++++++++++++++++++++++++++ //

		/**
		 * Before binding the data and calling the List window we check, if the
		 * ZUL-file is called with a parameter for a selected FinCreditReviewDetails object in
		 * a Map.
		 * 
		 * @param event
		 * @throws Exception
		 */
		public void onCreate$window_CreditApplicationReviewList(Event event) throws Exception {
			logger.debug("Entering" + event.toString());

			ModuleMapping moduleMapping = PennantJavaUtil.getModuleMap("FinCreditReviewDetails");
			boolean wfAvailable = true;

			if (moduleMapping.getWorkflowType() != null) {
				workFlowDetails = WorkFlowUtil.getWorkFlowDetails("FinCreditReviewDetails");

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

			/* set components visible dependent on the users rights */
			doCheckRights();

			this.borderLayout_CreditApplicationReviewList.setHeight(getBorderLayoutHeight());

			// set the paging parameters
			this.pagingCreditApplicationReviewList.setPageSize(getListRows());
			this.pagingCreditApplicationReviewList.setDetailed(true);

			this.listheader_DetailId.setSortAscending(new FieldComparator("detailId", true));
			this.listheader_DetailId.setSortDescending(new FieldComparator("detailId", false));

			this.listheader_CustId.setSortAscending(new FieldComparator("customerId", true));
			this.listheader_CustId.setSortDescending(new FieldComparator("customerId", false));

			this.listheader_CreditRevCode.setSortAscending(new FieldComparator("creditRevCode", true));
			this.listheader_CreditRevCode.setSortDescending(new FieldComparator("creditRevCode", false));
			
			this.listheader_BankName.setSortAscending(new FieldComparator("bankName", true));
			this.listheader_BankName.setSortDescending(new FieldComparator("bankName", false));
			
			this.listheader_AuditedYear.setSortAscending(new FieldComparator("auditYear", true));
			this.listheader_AuditedYear.setSortDescending(new FieldComparator("auditYear", false));

			if (isWorkFlowEnabled()) {
				this.listheader_RecordStatus.setSortAscending(new FieldComparator("recordStatus", true));
				this.listheader_RecordStatus.setSortDescending(new FieldComparator("recordStatus", false));
				
				this.listheader_RecordType.setSortAscending(new FieldComparator("recordType", true));
				this.listheader_RecordType.setSortDescending(new FieldComparator("recordType", false));
			} else {
				this.listheader_RecordStatus.setVisible(false);
				this.listheader_RecordType.setVisible(false);
			}

			// ++ create the searchObject and initial sorting ++//
			this.searchObj = new JdbcSearchObject<FinCreditReviewDetails>(FinCreditReviewDetails.class, getListRows());
			this.searchObj.addSort("DetailId", false);

			// WorkFlow
			if (isWorkFlowEnabled()) {
				this.searchObj.addTabelName("FinCreditReviewDetails_View");
				if (isFirstTask()) {
					button_CreditApplicationReviewList_NewCreditApplicationReview.setVisible(true);
				} else {
					button_CreditApplicationReviewList_NewCreditApplicationReview.setVisible(false);
				}
				this.searchObj.addFilterIn("nextRoleCode", getUserWorkspace().getUserRoles(), isFirstTask());
			} else {
				this.searchObj.addTabelName("FinCreditReviewDetails_AView");
			}

			setSearchObj(this.searchObj);
			if (!isWorkFlowEnabled() && wfAvailable) {
				this.button_CreditApplicationReviewList_NewCreditApplicationReview.setVisible(false);
				this.button_CreditApplicationReviewList_CreditApplicationReviewSearchDialog.setVisible(false);
				this.button_CreditApplicationReviewList_PrintList.setVisible(false);
				PTMessageUtils.showErrorMessage(PennantJavaUtil.getLabel("WORKFLOW CONFIG NOT FOUND"));
			} else {
				// Set the ListModel for the articles.
				getPagedListWrapper().init(this.searchObj, this.listBoxCreditApplicationReview, this.pagingCreditApplicationReviewList);
				// set the itemRenderer
				this.listBoxCreditApplicationReview.setItemRenderer(new CreditApplicationReviewListModelItemRenderer());
			}
			logger.debug("Leaving" + event.toString());
		}

		/**
		 * SetVisible for components by checking if there's a right for it.
		 */
		private void doCheckRights() {
			logger.debug("Entering");
			getUserWorkspace().alocateAuthorities("CreditReviewDetailsList");

			this.button_CreditApplicationReviewList_NewCreditApplicationReview.setVisible(getUserWorkspace().
					isAllowed("button_CreditApplicationReviewList_NewCreditApplicationReview"));
			this.button_CreditApplicationReviewList_CreditApplicationReviewSearchDialog.setVisible(getUserWorkspace().
					isAllowed("button_CreditApplicationReviewList_CreditApplicationReviewFindDialog"));
			this.button_CreditApplicationReviewList_PrintList.setVisible(getUserWorkspace().
					isAllowed("button_CreditApplicationReviewList_PrintList"));
			logger.debug("Leaving");
		}

		/**
		 * This method is forwarded from the listBoxes item renderer. <br>
		 * see: com.pennant.webui.rmtmasters.accountingset.model.CreditApplicationReviewListModelItemRenderer.java <br>
		 * 
		 * @param event
		 * @throws Exception
		 */
		public void onCreditApplicationReviewItemDoubleClicked(Event event) throws Exception {
			logger.debug("Entering" + event.toString());

			// get the selected FinCreditReviewDetails object
			final Listitem item = this.listBoxCreditApplicationReview.getSelectedItem();

			if (item != null) {
				// CAST AND STORE THE SELECTED OBJECT
				final FinCreditReviewDetails aCreditReviewDetails = (FinCreditReviewDetails) item.getAttribute("data");
				final FinCreditReviewDetails creditReviewDetails = getCreditApplicationReviewService().getCreditReviewDetailsById(
						aCreditReviewDetails.getDetailId());
				
				
				if (creditReviewDetails == null) {
					String[] errParm = new String[1];
					String[] valueParm = new String[1];
					valueParm[0] = String.valueOf(aCreditReviewDetails.getDetailId());
					errParm[0] = PennantJavaUtil.getLabel("label_CreditReviewId") + ":" + valueParm[0];

					ErrorDetails errorDetails = ErrorUtil.getErrorDetail(new ErrorDetails(
							PennantConstants.KEY_FIELD, "41005", errParm, valueParm), 
							getUserWorkspace().getUserLanguage());
					PTMessageUtils.showErrorMessage(errorDetails.getErrorMessage());
				} else {
					List<FinCreditReviewSummary> listOfFinCreditReviewSummary = getCreditApplicationReviewService().getListCreditReviewSummaryById(aCreditReviewDetails.getDetailId(), "_View", false);
					creditReviewDetails.setCreditReviewSummaryEntries(listOfFinCreditReviewSummary);
					if (isWorkFlowEnabled()) {
						String whereCond = " AND Detailid=" + creditReviewDetails.getDetailId() + 
						" AND version=" + creditReviewDetails.getVersion() + " ";

						boolean userAcces = validateUserAccess(workFlowDetails.getId(), 
								getUserWorkspace().getLoginUserDetails().getLoginUsrID(), "FinCreditReviewDetails", 
								whereCond, creditReviewDetails.getTaskId(), creditReviewDetails.getNextTaskId());
						if (userAcces) {
							showDetailView(creditReviewDetails);
						} else {
							PTMessageUtils.showErrorMessage(Labels.getLabel("RECORD_NOTALLOWED"));
						}
					} else {
						showDetailView(creditReviewDetails);
					}
				}
			}
			logger.debug("Leaving" + event.toString());
		}

		/**
		 * Call the FinCreditReviewDetails dialog with a new empty entry. <br>
		 */
		public void onClick$button_CreditApplicationReviewList_NewCreditApplicationReview(Event event) throws Exception {
			logger.debug("Entering" + event.toString());
			// create a new FinCreditReviewDetails object, We GET it from the backEnd.
			final FinCreditReviewDetails aCreditReviewDetails = getCreditApplicationReviewService().getNewCreditReviewDetails();
		/*	if (event.getData() != null) {
				copyDATA(aCreditReviewDetails, event.getData());
			}*/
			showDetailView(aCreditReviewDetails);
			logger.debug("Leaving" + event.toString());
		}

		/**
		 * Opens the detail view. <br>
		 * Overhanded some params in a map if needed. <br>
		 * 
		 * @param FinCreditReviewDetails (aCreditReviewDetails)
		 * @throws Exception
		 */
		private void showDetailView(FinCreditReviewDetails aCreditReviewDetails) throws Exception {
			logger.debug("Entering");

			/*
			 * We can call our Dialog ZUL-file with parameters. So we can call them with a object of the selected item. For
			 * handed over these parameter only a Map is accepted. So we put the object in a HashMap.
			 */

			if (aCreditReviewDetails.getWorkflowId() == 0 && isWorkFlowEnabled()) {
				aCreditReviewDetails.setWorkflowId(workFlowDetails.getWorkFlowId());
			}

			final HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("creditReviewDetails", aCreditReviewDetails);
			/*
			 * we can additionally handed over the listBox or the controller self, so we have in the dialog access to the
			 * listBox ListModel. This is fine for synchronizing the data in the CreditReviewDetailsListbox from the dialog when
			 * we do a delete, edit or insert a FinCreditReviewDetails.
			 */
			map.put("creditApplicationReviewListCtrl", this);

			// call the ZUL-file with the parameters packed in a map
			try {
				Executions.createComponents("/WEB-INF/pages/FinanceManagement/BankOrCorpCreditReview/CreditApplicationReviewDialog.zul", null, map);
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
			PTMessageUtils.showHelpWindow(event, window_CreditApplicationReviewList);
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
			this.pagingCreditApplicationReviewList.setActivePage(0);
			Events.postEvent("onCreate", this.window_CreditApplicationReviewList, event);
			this.window_CreditApplicationReviewList.invalidate();
			logger.debug("Leaving" + event.toString());
		}

		/**
		 * Method for calling the FinCreditReviewDetails dialog
		 * @param event
		 * @throws Exception
		 */
		public void onClick$button_CreditApplicationReviewList_CreditApplicationReviewSearchDialog(Event event) throws Exception {
			logger.debug("Entering" + event.toString());

			/*
			 * we can call our CreditApplicationReviewDialog ZUL-file with parameters. So we can call them with a object of the
			 * selected FinCreditReviewDetails. For handed over these parameter only a Map is accepted. So we put the FinCreditReviewDetails
			 * object in a HashMap.
			 */
			final HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("accountingSetCtrl", this);
			map.put("searchObject", this.searchObj);

			// call the ZUL-file with the parameters packed in a map
			try {
				Executions.createComponents(
						"/WEB-INF/pages/FinanceManagement/BankOrCorpCredit/CreditApplicationReviewSearchDialog.zul", null, map);
			} catch (final Exception e) {
				logger.error("onOpenWindow:: error opening window / " + e.getMessage());
				PTMessageUtils.showErrorMessage(e.toString());
			}
			logger.debug("Leaving" + event.toString());
		}

		/**
		 * When the creditReviewDetails print button is clicked.
		 * 
		 * @param event
		 * @throws InterruptedException
		 */
		public void onClick$button_CreditApplicationReviewList_PrintList(Event event) throws InterruptedException {
			logger.debug("Entering" + event.toString());
			PTReportUtils.getReport("FinCreditReviewDetails", getSearchObj());
			logger.debug("Leaving" + event.toString());
		}

		// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
		// ++++++++++++++++++ getter / setter +++++++++++++++++++//
		// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//	


		public JdbcSearchObject<FinCreditReviewDetails> getSearchObj() {
			return this.searchObj;
		}
		public void setSearchObj(JdbcSearchObject<FinCreditReviewDetails> searchObj) {
			this.searchObj = searchObj;
		}

		public CreditApplicationReviewService getCreditApplicationReviewService() {
			return creditApplicationReviewService;
		}

		public void setCreditApplicationReviewService(
				CreditApplicationReviewService creditApplicationReviewService) {
			this.creditApplicationReviewService = creditApplicationReviewService;
		}	
	}