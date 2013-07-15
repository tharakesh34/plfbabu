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
 * FileName    		:  DirectorDetailSearchCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  01-12-2011    														*
 *                                                                  						*
 * Modified Date    :  01-12-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 01-12-2011       Pennant	                 0.1                                            * 
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

package com.pennant.webui.customermasters.directordetail;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.app.util.DateUtility;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.customermasters.DirectorDetail;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.search.Filter;
import com.pennant.util.PennantAppUtil;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.PTMessageUtils;
import com.pennant.webui.util.pagging.PagedListWrapper;
import com.pennant.webui.util.searching.SearchOperatorListModelItemRenderer;
import com.pennant.webui.util.searching.SearchOperators;


public class DirectorDetailSearchCtrl extends GFCBaseCtrl implements Serializable {

	private static final long serialVersionUID = -642465411101973095L;
	private final static Logger logger = Logger.getLogger(DirectorDetailSearchCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window  window_DirectorDetailSearch; 		// autowired
	
	protected Textbox custID; 							// autowired
	protected Listbox sortOperator_custID; 				// autowired
	protected Textbox firstName; 						// autowired
	protected Listbox sortOperator_firstName; 			// autowired
	protected Textbox middleName; 						// autowired
	protected Listbox sortOperator_middleName; 			// autowired
	protected Textbox lastName; 						// autowired
	protected Listbox sortOperator_lastName; 			// autowired
	protected Textbox shortName; 						// autowired
	protected Listbox sortOperator_shortName; 			// autowired
	protected Textbox custGenderCode; 					// autowired
	protected Listbox sortOperator_custGenderCode; 		// autowired
	protected Textbox custSalutationCode; 				// autowired
	protected Listbox sortOperator_custSalutationCode; 	// autowired
	protected Textbox custAddrHNbr; 					// autowired
	protected Listbox sortOperator_custAddrHNbr;	 	// autowired
	protected Textbox custFlatNbr; 						// autowired
	protected Listbox sortOperator_custFlatNbr; 		// autowired
	protected Textbox custAddrStreet; 					// autowired
	protected Listbox sortOperator_custAddrStreet; 		// autowired
	protected Textbox custAddrLine1; 					// autowired
	protected Listbox sortOperator_custAddrLine1; 		// autowired
	protected Textbox custAddrLine2; 					// autowired
	protected Listbox sortOperator_custAddrLine2; 		// autowired
	protected Textbox custPOBox; 						// autowired
	protected Listbox sortOperator_custPOBox; 			// autowired
	protected Textbox custAddrCity; 					// autowired
	protected Listbox sortOperator_custAddrCity; 		// autowired
	protected Textbox custAddrProvince; 				// autowired
	protected Listbox sortOperator_custAddrProvince; 	// autowired
	protected Textbox custAddrCountry; 					// autowired
	protected Listbox sortOperator_custAddrCountry; 	// autowired
	protected Textbox custAddrZIP; 						// autowired
	protected Listbox sortOperator_custAddrZIP; 		// autowired
	protected Textbox custAddrPhone; 					// autowired
	protected Listbox sortOperator_custAddrPhone; 		// autowired
	protected Datebox custAddrFrom; 					// autowired
	protected Listbox sortOperator_custAddrFrom; 		// autowired
	protected Textbox recordStatus; 					// autowired
	protected Listbox recordType;						// autowired
	protected Listbox sortOperator_recordStatus; 		// autowired
	protected Listbox sortOperator_recordType; 			// autowired
	
	protected Label label_DirectorDetailSearch_RecordStatus; 	// autowired
	protected Label label_DirectorDetailSearch_RecordType; 		// autowired
	protected Label label_DirectorDetailSearchResult; 			// autowired

	// not auto wired vars
	private transient DirectorDetailListCtrl directorDetailCtrl; // overhanded per param
	private transient WorkFlowDetails workFlowDetails=WorkFlowUtil.getWorkFlowDetails("DirectorDetail");
	
	/**
	 * constructor
	 */
	public DirectorDetailSearchCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	
	/**
	 * Before binding the data and calling the Search window we check, if the
	 * ZUL-file is called with a parameter for a selected DirectorDetail object in a
	 * Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public void onCreate$window_DirectorDetailSearch(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		
		if (workFlowDetails==null){
			setWorkFlowEnabled(false);
		}else{
			setWorkFlowEnabled(true);
			setFirstTask(getUserWorkspace().isRoleContains(workFlowDetails.getFirstTaskOwner()));
			setWorkFlowId(workFlowDetails.getId());
		}
	
		// get the params map that are overhanded by creation.
		final Map<String, Object> args = getCreationArgsMap(event);

		if (args.containsKey("directorDetailCtrl")) {
			this.directorDetailCtrl = (DirectorDetailListCtrl) args.get("directorDetailCtrl");
		} else {
			this.directorDetailCtrl = null;
		}

		// +++++++++++++++++++++++ DropDown ListBox ++++++++++++++++++++++ //
	
		this.sortOperator_custID.setModel(new ListModelList(new SearchOperators().getStringOperators()));
		this.sortOperator_custID.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_firstName.setModel(new ListModelList(new SearchOperators().getStringOperators()));
		this.sortOperator_firstName.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_middleName.setModel(new ListModelList(new SearchOperators().getStringOperators()));
		this.sortOperator_middleName.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_lastName.setModel(new ListModelList(new SearchOperators().getStringOperators()));
		this.sortOperator_lastName.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_shortName.setModel(new ListModelList(new SearchOperators().getStringOperators()));
		this.sortOperator_shortName.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_custGenderCode.setModel(new ListModelList(
				new SearchOperators().getStringOperators()));
		this.sortOperator_custGenderCode.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_custSalutationCode.setModel(new ListModelList(
				new SearchOperators().getStringOperators()));
		this.sortOperator_custSalutationCode.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_custAddrHNbr.setModel(new ListModelList(
				new SearchOperators().getStringOperators()));
		this.sortOperator_custAddrHNbr.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_custFlatNbr.setModel(new ListModelList(
				new SearchOperators().getStringOperators()));
		this.sortOperator_custFlatNbr.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_custAddrStreet.setModel(new ListModelList(
				new SearchOperators().getStringOperators()));
		this.sortOperator_custAddrStreet.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_custAddrLine1.setModel(new ListModelList(
				new SearchOperators().getStringOperators()));
		this.sortOperator_custAddrLine1.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_custAddrLine2.setModel(new ListModelList(
				new SearchOperators().getStringOperators()));
		this.sortOperator_custAddrLine2.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_custPOBox.setModel(new ListModelList(
				new SearchOperators().getStringOperators()));
		this.sortOperator_custPOBox.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_custAddrCity.setModel(new ListModelList(
				new SearchOperators().getStringOperators()));
		this.sortOperator_custAddrCity.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_custAddrProvince.setModel(new ListModelList(
				new SearchOperators().getStringOperators()));
		this.sortOperator_custAddrProvince.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_custAddrCountry.setModel(new ListModelList(
				new SearchOperators().getStringOperators()));
		this.sortOperator_custAddrCountry.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_custAddrZIP.setModel(new ListModelList(
				new SearchOperators().getStringOperators()));
		this.sortOperator_custAddrZIP.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_custAddrPhone.setModel(new ListModelList(
				new SearchOperators().getStringOperators()));
		this.sortOperator_custAddrPhone.setItemRenderer(new SearchOperatorListModelItemRenderer());
	
		this.sortOperator_custAddrFrom.setModel(new ListModelList(
				new SearchOperators().getNumericOperators()));
		this.sortOperator_custAddrFrom.setItemRenderer(new SearchOperatorListModelItemRenderer());
		
		if (isWorkFlowEnabled()){
			this.sortOperator_recordStatus.setModel(new ListModelList(
					new SearchOperators().getStringOperators()));
			this.sortOperator_recordStatus.setItemRenderer(new SearchOperatorListModelItemRenderer());
			this.sortOperator_recordType.setModel(new ListModelList(
					new SearchOperators().getStringOperators()));
			this.sortOperator_recordType.setItemRenderer(new SearchOperatorListModelItemRenderer());
			this.recordType=PennantAppUtil.setRecordType(this.recordType);	
		}else{
			this.recordStatus.setVisible(false);
			this.recordType.setVisible(false);
			this.sortOperator_recordStatus.setVisible(false);
			this.sortOperator_recordType.setVisible(false);
			this.label_DirectorDetailSearch_RecordStatus.setVisible(false);
			this.label_DirectorDetailSearch_RecordType.setVisible(false);
		}
		
		// ++++ Restore the search mask input definition ++++ //
		// if exists a searchObject than show formerly inputs of filter values
		if (args.containsKey("searchObject")) {
			final JdbcSearchObject<DirectorDetail> searchObj = (JdbcSearchObject<DirectorDetail>) args
					.get("searchObject");

			// get the filters from the searchObject
			final List<Filter> ft = searchObj.getFilters();

			for (final Filter filter : ft) {

				// restore founded properties
			    if (filter.getProperty().equals("custID")) {
					SearchOperators.restoreStringOperator(this.sortOperator_custID, filter);
					this.custID.setValue(filter.getValue().toString());
			    } else if (filter.getProperty().equals("firstName")) {
					SearchOperators.restoreStringOperator(this.sortOperator_firstName, filter);
					this.firstName.setValue(filter.getValue().toString());
			    } else if (filter.getProperty().equals("middleName")) {
					SearchOperators.restoreStringOperator(this.sortOperator_middleName, filter);
					this.middleName.setValue(filter.getValue().toString());
			    } else if (filter.getProperty().equals("lastName")) {
					SearchOperators.restoreStringOperator(this.sortOperator_lastName, filter);
					this.lastName.setValue(filter.getValue().toString());
			    } else if (filter.getProperty().equals("shortName")) {
					SearchOperators.restoreStringOperator(this.sortOperator_shortName, filter);
					this.shortName.setValue(filter.getValue().toString());
			    } else if (filter.getProperty().equals("custGenderCode")) {
					SearchOperators.restoreStringOperator(this.sortOperator_custGenderCode, filter);
					this.custGenderCode.setValue(filter.getValue().toString());
			    } else if (filter.getProperty().equals("custSalutationCode")) {
					SearchOperators.restoreStringOperator(this.sortOperator_custSalutationCode, filter);
					this.custSalutationCode.setValue(filter.getValue().toString());
			    } else if (filter.getProperty().equals("custAddrHNbr")) {
					SearchOperators.restoreStringOperator(this.sortOperator_custAddrHNbr, filter);
					this.custAddrHNbr.setValue(filter.getValue().toString());
			    } else if (filter.getProperty().equals("custFlatNbr")) {
					SearchOperators.restoreStringOperator(this.sortOperator_custFlatNbr, filter);
					this.custFlatNbr.setValue(filter.getValue().toString());
			    } else if (filter.getProperty().equals("custAddrStreet")) {
					SearchOperators.restoreStringOperator(this.sortOperator_custAddrStreet, filter);
					this.custAddrStreet.setValue(filter.getValue().toString());
			    } else if (filter.getProperty().equals("custAddrLine1")) {
					SearchOperators.restoreStringOperator(this.sortOperator_custAddrLine1, filter);
					this.custAddrLine1.setValue(filter.getValue().toString());
			    } else if (filter.getProperty().equals("custAddrLine2")) {
					SearchOperators.restoreStringOperator(this.sortOperator_custAddrLine2, filter);
					this.custAddrLine2.setValue(filter.getValue().toString());
			    } else if (filter.getProperty().equals("custPOBox")) {
					SearchOperators.restoreStringOperator(this.sortOperator_custPOBox, filter);
					this.custPOBox.setValue(filter.getValue().toString());
			    } else if (filter.getProperty().equals("custAddrCity")) {
					SearchOperators.restoreStringOperator(this.sortOperator_custAddrCity, filter);
					this.custAddrCity.setValue(filter.getValue().toString());
			    } else if (filter.getProperty().equals("custAddrProvince")) {
					SearchOperators.restoreStringOperator(this.sortOperator_custAddrProvince, filter);
					this.custAddrProvince.setValue(filter.getValue().toString());
			    } else if (filter.getProperty().equals("custAddrCountry")) {
					SearchOperators.restoreStringOperator(this.sortOperator_custAddrCountry, filter);
					this.custAddrCountry.setValue(filter.getValue().toString());
			    } else if (filter.getProperty().equals("custAddrZIP")) {
					SearchOperators.restoreStringOperator(this.sortOperator_custAddrZIP, filter);
					this.custAddrZIP.setValue(filter.getValue().toString());
			    } else if (filter.getProperty().equals("custAddrPhone")) {
					SearchOperators.restoreStringOperator(this.sortOperator_custAddrPhone, filter);
					this.custAddrPhone.setValue(filter.getValue().toString());
			    } else if (filter.getProperty().equals("custAddrFrom")) {
			    	SearchOperators.restoreNumericOperator(this.sortOperator_custAddrFrom, filter);
					this.custAddrFrom.setValue(DateUtility.getUtilDate(
							filter.getValue().toString(),PennantConstants.DBDateFormat));
				} else if (filter.getProperty().equals("recordStatus")) {
					SearchOperators.restoreStringOperator(this.sortOperator_recordStatus, filter);
					this.recordStatus.setValue(filter.getValue().toString());
				} else if (filter.getProperty().equals("recordType")) {
					SearchOperators.restoreStringOperator(this.sortOperator_recordType, filter);
					for (int i = 0; i < this.recordType.getItemCount(); i++) {
						if (this.recordType.getItemAtIndex(i).getValue().equals(filter.getValue().toString())){
							this.recordType.setSelectedIndex(i);
						}
					}
				}
			}
		}
		showDirectorDetailSeekDialog();
		logger.debug("Leaving" + event.toString());
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// +++++++++++++++++++++++ Components events +++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * when the "search/filter" button is clicked.
	 * 
	 * @param event
	 */
	public void onClick$btnSearch(Event event) {
		logger.debug("Entering" + event.toString());
		doSearch();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "close" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnClose(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		doClose();
		logger.debug("Leaving" + event.toString());
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// ++++++++++++++++++++++++ GUI operations +++++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * closes the dialog window
	 */
	private void doClose() {
		logger.debug("Entering");
		this.window_DirectorDetailSearch.onClose();
		logger.debug("Leaving");
	}

	/**
	 * Opens the SearchDialog window modal.
	 */
	private void showDirectorDetailSeekDialog() throws InterruptedException {
		logger.debug("Entering");
		try {
			// open the dialog in modal mode
			this.window_DirectorDetailSearch.doModal();
		} catch (final Exception e) {
			logger.error(e);
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving");
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// ++++++++++++++++++++++++ GUI operations +++++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * Search/filter data for the filled out fields<br>
	 * <br>
	 * 1. Checks for each textBox if there are a value. <br>
	 * 2. Checks which operator is selected. <br>
	 * 3. Store the filter and value in the searchObject. <br>
	 * 4. Call the ServiceDAO method with searchObject as parameter. <br>
	 */ 
	@SuppressWarnings("unchecked")
	public void doSearch() {

		final JdbcSearchObject<DirectorDetail> so = new JdbcSearchObject<DirectorDetail>(DirectorDetail.class);

		if (isWorkFlowEnabled()){
			so.addTabelName("CustomerDirectorDetail_View");
			so.addFilterIn("nextRoleCode", getUserWorkspace().getUserRoles(),isFirstTask());	
		}else{
			so.addTabelName("CustomerDirectorDetail_AView");
		}
		
		
		if (StringUtils.isNotEmpty(this.custID.getValue())) {

			// get the search operator
			final Listitem item_CustID = this.sortOperator_custID.getSelectedItem();

			if (item_CustID != null) {
				final int searchOpId = ((SearchOperators) item_CustID.getAttribute(
						"data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("custCIF", "%" + this.custID.getValue().toUpperCase() +
							"%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("custCIF", this.custID.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.firstName.getValue())) {

			// get the search operator
			final Listitem item_FirstName = this.sortOperator_firstName.getSelectedItem();

			if (item_FirstName != null) {
				final int searchOpId = ((SearchOperators) item_FirstName.getAttribute(
						"data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("firstName", "%" + this.firstName.getValue().toUpperCase() 
							+ "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("firstName", this.firstName.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.middleName.getValue())) {

			// get the search operator
			final Listitem item_MiddleName = this.sortOperator_middleName.getSelectedItem();

			if (item_MiddleName != null) {
				final int searchOpId = ((SearchOperators) item_MiddleName.getAttribute(
						"data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("middleName", "%" + this.middleName.getValue().toUpperCase()
							+ "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("middleName", this.middleName.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.lastName.getValue())) {

			// get the search operator
			final Listitem item_LastName = this.sortOperator_lastName.getSelectedItem();

			if (item_LastName != null) {
				final int searchOpId = ((SearchOperators) item_LastName.getAttribute(
						"data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("lastName", "%" + this.lastName.getValue().toUpperCase() 
							+ "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("lastName", this.lastName.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.shortName.getValue())) {

			// get the search operator
			final Listitem item_ShortName = this.sortOperator_shortName.getSelectedItem();

			if (item_ShortName != null) {
				final int searchOpId = ((SearchOperators) item_ShortName.getAttribute(
						"data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("shortName", "%" + this.shortName.getValue().toUpperCase()
							+ "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("shortName", this.shortName.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.custGenderCode.getValue())) {

			// get the search operator
			final Listitem item_CustGenderCode = this.sortOperator_custGenderCode.getSelectedItem();

			if (item_CustGenderCode != null) {
				final int searchOpId = ((SearchOperators) item_CustGenderCode.getAttribute(
						"data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("custGenderCode", "%" + 
							this.custGenderCode.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("custGenderCode", this.custGenderCode.getValue(),
							searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.custSalutationCode.getValue())) {

			// get the search operator
			final Listitem item_CustSalutationCode = this.sortOperator_custSalutationCode.getSelectedItem();

			if (item_CustSalutationCode != null) {
				final int searchOpId = ((SearchOperators) item_CustSalutationCode.getAttribute(
						"data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("custSalutationCode", "%" + 
							this.custSalutationCode.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("custSalutationCode", 
							this.custSalutationCode.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.custAddrHNbr.getValue())) {

			// get the search operator
			final Listitem item_CustAddrHNbr = this.sortOperator_custAddrHNbr.getSelectedItem();

			if (item_CustAddrHNbr != null) {
				final int searchOpId = ((SearchOperators) item_CustAddrHNbr.getAttribute(
						"data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("custAddrHNbr", "%" + 
							this.custAddrHNbr.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("custAddrHNbr", this.custAddrHNbr.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.custFlatNbr.getValue())) {

			// get the search operator
			final Listitem item_CustFlatNbr = this.sortOperator_custFlatNbr.getSelectedItem();

			if (item_CustFlatNbr != null) {
				final int searchOpId = ((SearchOperators) item_CustFlatNbr.getAttribute(
						"data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("custFlatNbr", "%" + 
							this.custFlatNbr.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("custFlatNbr", this.custFlatNbr.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.custAddrStreet.getValue())) {

			// get the search operator
			final Listitem item_CustAddrStreet = this.sortOperator_custAddrStreet.getSelectedItem();

			if (item_CustAddrStreet != null) {
				final int searchOpId = ((SearchOperators) item_CustAddrStreet.getAttribute(
						"data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("custAddrStreet", "%" + 
							this.custAddrStreet.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("custAddrStreet",
							this.custAddrStreet.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.custAddrLine1.getValue())) {

			// get the search operator
			final Listitem item_CustAddrLine1 = this.sortOperator_custAddrLine1.getSelectedItem();

			if (item_CustAddrLine1 != null) {
				final int searchOpId = ((SearchOperators) item_CustAddrLine1.getAttribute(
						"data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("custAddrLine1", "%" +
							this.custAddrLine1.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("custAddrLine1", this.custAddrLine1.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.custAddrLine2.getValue())) {

			// get the search operator
			final Listitem item_CustAddrLine2 = this.sortOperator_custAddrLine2.getSelectedItem();

			if (item_CustAddrLine2 != null) {
				final int searchOpId = ((SearchOperators) item_CustAddrLine2.getAttribute(
						"data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("custAddrLine2", "%" + 
							this.custAddrLine2.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("custAddrLine2", this.custAddrLine2.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.custPOBox.getValue())) {

			// get the search operator
			final Listitem item_CustPOBox = this.sortOperator_custPOBox.getSelectedItem();

			if (item_CustPOBox != null) {
				final int searchOpId = ((SearchOperators) item_CustPOBox.getAttribute(
						"data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("custPOBox", "%" + 
							this.custPOBox.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("custPOBox", this.custPOBox.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.custAddrCity.getValue())) {

			// get the search operator
			final Listitem item_CustAddrCity = this.sortOperator_custAddrCity.getSelectedItem();

			if (item_CustAddrCity != null) {
				final int searchOpId = ((SearchOperators) item_CustAddrCity.getAttribute(
						"data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("custAddrCity", "%" +
							this.custAddrCity.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("custAddrCity", this.custAddrCity.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.custAddrProvince.getValue())) {

			// get the search operator
			final Listitem item_CustAddrProvince = this.sortOperator_custAddrProvince.getSelectedItem();

			if (item_CustAddrProvince != null) {
				final int searchOpId = ((SearchOperators) item_CustAddrProvince.getAttribute(
						"data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("custAddrProvince", "%" + 
							this.custAddrProvince.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("custAddrProvince", 
							this.custAddrProvince.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.custAddrCountry.getValue())) {

			// get the search operator
			final Listitem item_CustAddrCountry = this.sortOperator_custAddrCountry.getSelectedItem();

			if (item_CustAddrCountry != null) {
				final int searchOpId = ((SearchOperators) item_CustAddrCountry.getAttribute(
						"data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("custAddrCountry", "%" +
							this.custAddrCountry.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("custAddrCountry", 
							this.custAddrCountry.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.custAddrZIP.getValue())) {

			// get the search operator
			final Listitem item_CustAddrZIP = this.sortOperator_custAddrZIP.getSelectedItem();

			if (item_CustAddrZIP != null) {
				final int searchOpId = ((SearchOperators) item_CustAddrZIP.getAttribute(
						"data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("custAddrZIP", "%" + 
							this.custAddrZIP.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("custAddrZIP", this.custAddrZIP.getValue(), searchOpId));
				}
			}
		}
		if (StringUtils.isNotEmpty(this.custAddrPhone.getValue())) {

			// get the search operator
			final Listitem item_CustAddrPhone = this.sortOperator_custAddrPhone.getSelectedItem();

			if (item_CustAddrPhone != null) {
				final int searchOpId = ((SearchOperators) item_CustAddrPhone.getAttribute(
						"data")).getSearchOperatorId();

				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("custAddrPhone", "%" + 
							this.custAddrPhone.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("custAddrPhone", this.custAddrPhone.getValue(), searchOpId));
				}
			}
		}
	  if (this.custAddrFrom.getValue()!=null) {	  
	    final Listitem item_CustAddrFrom = this.sortOperator_custAddrFrom.getSelectedItem();
	  	if (item_CustAddrFrom != null) {
	 		final int searchOpId = ((SearchOperators) item_CustAddrFrom.getAttribute(
	 				"data")).getSearchOperatorId();
	 		
	 		if (searchOpId == -1) {
	 			// do nothing
	 		} else {
	 			so.addFilter(new Filter("custAddrFrom",DateUtility.formatUtilDate(
						this.custAddrFrom.getValue(),PennantConstants.DBDateFormat), searchOpId));
	 		}
	 	}
	  }	
		if (StringUtils.isNotEmpty(this.recordStatus.getValue())) {
			// get the search operator
			final Listitem item_RecordStatus = this.sortOperator_recordStatus.getSelectedItem();
			if (item_RecordStatus != null) {
				final int searchOpId = ((SearchOperators) item_RecordStatus.getAttribute(
						"data")).getSearchOperatorId();
	
				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("recordStatus", "%" + 
							this.recordStatus.getValue().toUpperCase() + "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("recordStatus", this.recordStatus.getValue(), searchOpId));
				}
			}
		}
		
		String selectedValue="";
		if (this.recordType.getSelectedItem()!=null){
			selectedValue =this.recordType.getSelectedItem().getValue().toString();
		}

		if (StringUtils.isNotEmpty(selectedValue)) {
			// get the search operator
			final Listitem item_RecordType = this.sortOperator_recordType.getSelectedItem();
			if (item_RecordType!= null) {
				final int searchOpId = ((SearchOperators) item_RecordType.getAttribute(
						"data")).getSearchOperatorId();
	
				if (searchOpId == Filter.OP_LIKE) {
					so.addFilter(new Filter("recordType", "%" + selectedValue.toUpperCase() 
							+ "%", searchOpId));
				} else if (searchOpId == -1) {
					// do nothing
				} else {
					so.addFilter(new Filter("recordType", selectedValue, searchOpId));
				}
			}
		}
		// Default Sort on the table
		so.addSort("DirectorId", false);

		if (logger.isDebugEnabled()) {
			final List<Filter> lf = so.getFilters();
			for (final Filter filter : lf) {
				logger.debug(filter.getProperty().toString() + " / " + filter.getValue().toString());

				if (Filter.OP_ILIKE == filter.getOperator()) {
					logger.debug(filter.getOperator());
				}
			}
		}

		// store the searchObject for reReading
		this.directorDetailCtrl.setSearchObj(so);

		final Listbox listBox = this.directorDetailCtrl.listBoxDirectorDetail;
		final Paging paging = this.directorDetailCtrl.pagingDirectorDetailList;
		

		// set the model to the listBox with the initial resultSet get by the DAO method.
		((PagedListWrapper<DirectorDetail>) listBox.getModel()).init(so, listBox, paging);
		this.directorDetailCtrl.setSearchObj(so);

		this.label_DirectorDetailSearchResult.setValue(Labels.getLabel(
				"label_DirectorDetailSearchResult.value") + " "+ String.valueOf(paging.getTotalSize()));
	}

}