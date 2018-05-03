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
 * FileName    		: SecuritySearchByNameDialogCtrl.java                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  17-09-2011    														*
 *                                                                  						*
 * Modified Date    :  19-09-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 19-09-2011       Pennant	                 0.1                                            * 
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
package com.pennant.webui.administration.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.administration.SecurityGroup;
import com.pennant.backend.model.administration.SecurityOperation;
import com.pennant.backend.model.administration.SecurityRight;
import com.pennant.backend.model.administration.SecurityRole;
import com.pennant.webui.administration.SecurityOperationRoles.SecurityOperationRolesDialogCtrl;
import com.pennant.webui.administration.securitygrouprights.SecurityGroupRightsDialogCtrl;
import com.pennant.webui.administration.securityrolegroups.SecurityRoleGroupsDialogCtrl;
import com.pennant.webui.administration.securityuseroperations.SecurityUserOperationsDialogCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.searching.SearchOperatorListModelItemRenderer;
import com.pennant.webui.util.searching.SearchOperators;

/**
 * This is the controller class for the /WEB-INF/pages/Administration/SecurityUsersroles/SecuritySearchDialog.zul file.
 */
public class SecuritySearchDialogCtrl extends GFCBaseCtrl<Object> {
	private static final long serialVersionUID = -7630878240134112225L;
	private static final Logger logger = Logger.getLogger(SecuritySearchDialogCtrl.class);
	protected Window window_SecuritySearchDialog; // autowired
	protected Listbox sortOperator_Name; // autowired
	protected Listbox sortOperator_rightType; // autowired
	protected Textbox name; // autowired
	protected Label label_Name; // autowired
	protected Label label_SearchResult; // autowired
	private Object object;
	private Map<Object, Object> dataMap = new HashMap<Object, Object>();
	private Map<Object, Object> tempDataMap = new HashMap<Object, Object>();
	private Object filters[] = new Object[2]; /* Store filters for show filters in next invocation of window */

	/**
	 * Default constructor
	 */
	public SecuritySearchDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "";
	}

	@SuppressWarnings("unchecked")
	public void onCreate$window_SecuritySearchDialog(Event event) throws Exception {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_SecuritySearchDialog);
		if (arguments.containsKey("dialogCtrl")) {

			object = (Object) arguments.get("dialogCtrl");
			dataMap = (HashMap<Object, Object>) arguments.get("dataMap");
			filters = (Object[]) arguments.get("prevFilters");
		} else {
			object = null;
		}
		if (arguments.containsKey("SecurityUserOperationsDialogCtrl")) {
			object = (Object) arguments.get("SecurityUserOperationsDialogCtrl");
		}
		if (arguments.containsKey("SecurityOperationRolesDialogCtrl")) {
			object = (Object) arguments.get("SecurityOperationRolesDialogCtrl");
		}

		doPrepareData();
		this.sortOperator_Name.setModel(new ListModelList<SearchOperators>(new SearchOperators().getStringOperators()));
		this.sortOperator_Name.setItemRenderer(new SearchOperatorListModelItemRenderer());
		if (filters[0] != null) {
			this.sortOperator_Name.setSelectedIndex(((Integer) filters[0]).intValue());
		}
		if (filters[1] != null) {
			this.name.setValue(String.valueOf(filters[1]));
		}
		window_SecuritySearchDialog.doModal();
		logger.debug("Leaving " + event.toString());
	}

	/**
	 * when the "search/filter" button is clicked.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClick$btnSearch(Event event) throws Exception {
		logger.debug("Entering " + event.toString());
		doSearch();
		logger.debug("Leaving " + event.toString());

	}

	/**
	 * This method checks object's Class according to instance of class it fills data of dataMap into tempDataMap .
	 */
	public void doPrepareData() {
		logger.debug("Entering ");
		/*
		 * if object is instance of SecurityGroupRightsDialogCtrl fill tempDataMap where key<RightName> and value is
		 * SecRight's Object
		 */

		if (object instanceof SecurityGroupRightsDialogCtrl) {
			this.window_SecuritySearchDialog.setTitle(Labels.getLabel("window_SecuritySearchDialogCtrl_Rights.title"));
			this.label_Name.setValue(Labels.getLabel("label_SecuritySearchDialogCtrl_RightName"));
			for (Object key : dataMap.keySet()) {
				SecurityRight secRight = (SecurityRight) dataMap.get(key);
				tempDataMap.put(String.valueOf(secRight.getRightName()), dataMap.get(key));
			}
		}
		/*
		 * if object is instance of SecurityRoleGroupsDialogCtrl fill tempDataMap where key<GrpCode> and value is
		 * SecurityGroup's Object
		 */
		if (object instanceof SecurityRoleGroupsDialogCtrl) {
			this.window_SecuritySearchDialog.setTitle(Labels.getLabel("window_SecuritySearchDialogCtrl_Groups.title"));
			this.label_Name.setValue(Labels.getLabel("label_SecuritySearchDialogCtrl_GroupCode"));
			for (Object key : dataMap.keySet()) {
				SecurityGroup secGroup = (SecurityGroup) dataMap.get(key);
				tempDataMap.put(String.valueOf(secGroup.getGrpCode()), dataMap.get(key));
			}
		}

		if (object instanceof SecurityOperationRolesDialogCtrl) {
			this.window_SecuritySearchDialog.setTitle(Labels.getLabel("window_SecuritySearchDialogCtrl_Roles.title"));
			this.label_Name.setValue(Labels.getLabel("label_SecuritySearchDialogCtrl_RoleCode"));
			for (Object key : dataMap.keySet()) {
				SecurityRole secSecurityRole = (SecurityRole) dataMap.get(key);
				tempDataMap.put(String.valueOf(secSecurityRole.getRoleCd()), dataMap.get(key));
			}
		}

		if (object instanceof SecurityUserOperationsDialogCtrl) {
			this.window_SecuritySearchDialog.setTitle(Labels
					.getLabel("window_SecuritySearchDialogCtrl_Operations.title"));
			this.label_Name.setValue(Labels.getLabel("label_SecuritySearchDialogCtrl_OprCode"));
			for (Object key : dataMap.keySet()) {
				SecurityOperation aSecurityOperation = (SecurityOperation) dataMap.get(key);
				tempDataMap.put(String.valueOf(aSecurityOperation.getOprCode()), dataMap.get(key));
			}
		}
		logger.debug("Leaving ");
	}

	/**
	 * The Click event is raised when the Close Button control is clicked.
	 * 
	 * @param event
	 *            An event sent to the event handler of a component.
	 */
	public void onClick$btnClose(Event event) {
		doClose(false);
	}

	/**
	 * This method do search operation
	 * 
	 * @throws Exception
	 */
	private void doSearch() throws Exception {
		logger.debug("Entering");

		filters[0] = Integer.valueOf(-1);
		filters[1] = "";
		Object searchResult[] = new Object[2];

		if (StringUtils.isNotEmpty(this.name.getValue())) {
			// get the search operator
			final Listitem itemName = this.sortOperator_Name.getSelectedItem();
			if (itemName != null) {
				final int searchOpId = ((SearchOperators) itemName.getAttribute("data")).getSearchOperatorId();

				if (object instanceof SecurityUserOperationsDialogCtrl) {
					searchResult[0] = searchOpId;
					searchResult[1] = this.name.getValue();
					int count = (Integer) object.getClass()
							.getDeclaredMethod("doShowSearchResult", new Class[] { Object[].class })
							.invoke(object, new Object[] { searchResult });
					this.label_SearchResult.setValue(Labels.getLabel("label_SecuritySearchResults.value") + " "
							+ String.valueOf(count));

				} else if (object instanceof SecurityOperationRolesDialogCtrl) {
					searchResult[0] = searchOpId;
					searchResult[1] = this.name.getValue();
					int count = (Integer) object.getClass()
							.getDeclaredMethod("doShowSearchResult", new Class[] { Object[].class })
							.invoke(object, new Object[] { searchResult });
					this.label_SearchResult.setValue(Labels.getLabel("label_SecuritySearchResults.value") + " "
							+ String.valueOf(count));

				}else if (object instanceof SecurityRoleGroupsDialogCtrl) {
					searchResult[0] = searchOpId;
					searchResult[1] = this.name.getValue();
					int count = (Integer) object.getClass()
							.getDeclaredMethod("doShowSearchResult", new Class[] { Object[].class })
							.invoke(object, new Object[] { searchResult });
					this.label_SearchResult.setValue(Labels.getLabel("label_SecuritySearchResults.value") + " "
							+ String.valueOf(count));
				}else if (object instanceof SecurityGroupRightsDialogCtrl) {
					searchResult[0] = searchOpId;
					searchResult[1] = this.name.getValue();
					int count = (Integer) object.getClass().getDeclaredMethod("doShowSearchResult", new Class[] { Object[].class })
							.invoke(object, new Object[] { searchResult });
					this.label_SearchResult.setValue(Labels.getLabel("label_SecuritySearchResults.value") + " "
							+ String.valueOf(count));
				}

			} else {
				searchResult[0] = getAllResults(tempDataMap);
				searchResult[1] = filters;
				/*
				 * invoking doShowSearchResult() of object eg.SecurityUserDialogCtrl using reflection and passing
				 * searchResultList and filters as arguments
				 */
				object.getClass().getDeclaredMethod("doShowSearchResult", new Class[] { Object[].class })
						.invoke(object, new Object[] { searchResult });
				this.label_SearchResult.setValue(Labels.getLabel("label_SecuritySearchResults.value") + " "
						+ String.valueOf(dataMap.size()));
			}
		} else {
			this.window_SecuritySearchDialog.onClose();
		}

		logger.debug("Leaving");
	}

	public List<Object> getAllResults(Map<Object, Object> tempDataMap) {
		logger.debug("Entering ");

		List<Object> searchResultlist = new ArrayList<Object>();

		if (tempDataMap == null) {
			return searchResultlist;
		}

		for (Object key : tempDataMap.keySet()) {
			searchResultlist.add(tempDataMap.get(key));
		}
		logger.debug("Leaving ");
		return searchResultlist;
	}
}

	