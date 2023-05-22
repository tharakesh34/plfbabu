/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. All
 * components/modules/functions/classes/logic in this software, unless otherwise stated, the property of Pennant
 * Technologies.
 * 
 * Copyright and other intellectual property laws protect these materials. Reproduction or retransmission of the
 * materials, in whole or in part, in any manner, without the prior written consent of the copyright holder, is a
 * violation of copyright law.
 */

/**
 ********************************************************************************************
 * FILE HEADER *
 ********************************************************************************************
 * * FileName : InterfaceServiceListCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 10-08-2019 * *
 * Modified Date : 10-08-2019 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 10-08-2019 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.webui.externalinterface.InterfaceConfiguration;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Path;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;
import org.zkoss.zul.event.PagingEvent;

import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.externalinterface.InterfaceConfiguration;
import com.pennant.backend.model.externalinterface.InterfaceServiceLog;
import com.pennant.backend.service.PagedListService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.util.PennantAppUtil;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.web.components.SearchFilterControl;
import com.pennanttech.pennapps.core.App;
import com.pennanttech.pennapps.core.App.Database;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.jdbc.search.Filter;
import com.pennanttech.pennapps.jdbc.search.SearchResult;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pff.external.ReInitiateProcessService;
import com.pennanttech.pff.model.IDBInterfaceLogDetail;

/**
 * This is the controller class for the
 * /WEB-INF/pages/ExternalInterface/ExternalInterfaceConfiguration/InterfaceServiceList.zul file. <br>
 */
public class InterfaceServiceListCtrl extends GFCBaseCtrl<InterfaceConfiguration> {

	private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getLogger(InterfaceServiceListCtrl.class);

	private static final int SEARCH_ROW_SIZE = 27;
	private static final int LIST_ROW_SIZE = 26;
	private static final int PAGGING_SIZE = 30;
	private JdbcSearchObject<InterfaceServiceLog> searchObject;
	protected Paging paging_interfaceService;
	protected Window window_InterfaceServiceList;
	protected Listheader listheader_InterfaceService_Reference;
	protected Listheader listheader_InterfaceService_InitiateDate;
	protected Listheader listheader_InterfaceService_Status;
	protected Listheader listheader_InterfaceService_Error;
	protected Listheader listheader_InterfaceService_RecordProcessed;
	protected Listheader listheader_InterfaceService_StatusDesc;
	protected Listheader listheader_InterfaceService_ReProcess;
	protected Listbox sortOperator_Status;
	protected Listbox listBoxExternalInterfaceDialog;
	protected Listheader listheader_InterfaceService_ErrorDesc;
	protected Datebox fromDate;
	protected Textbox reference;
	protected Listbox sortOperator_Reference;
	protected Listbox sortOperator_ToDate;
	protected Listbox sortOperator_fromDate;
	private InterfaceConfiguration interfaceDeatilData;
	protected Datebox toDate;
	protected Grid searchGrid;
	protected Combobox status;
	protected Label interfaceCaption;
	private String bussinessDate = null;
	private transient PagedListService pagedListService;
	protected Button btnSearch;

	@Autowired
	private ReInitiateProcessService reInititateService;
	private List<ValueLabel> statusList = PennantStaticListUtil.getInterfaceStatusList();
	protected List<SearchFilterControl> searchControls = new ArrayList<SearchFilterControl>();
	private String reInitiateService = App.getProperty("external.interface.service.reInitiate");

	private static final String INTERFACE = "INTERFACE";

	/**
	 * default constructor.<br>
	 */
	public InterfaceServiceListCtrl() {
		super();
	}

	public void onCreate$window_InterfaceServiceList(Event event) {
		logger.debug(Literal.ENTERING);

		// Set the page level components.
		setPageComponents(window_InterfaceServiceList);

		try {
			// Get the required arguments.
			this.interfaceDeatilData = (InterfaceConfiguration) arguments.get("interfaceConfiguration");
			if (this.interfaceDeatilData == null) {
				throw new Exception(Labels.getLabel("error.unhandled"));
			}
			this.borderLayoutHeight = ((Intbox) Path.getComponent("/outerIndexWindow/currentDesktopHeight")).getValue()
					.intValue() - PennantConstants.borderlayoutMainNorth;
			int dialogHeight = searchGrid.getRows().getVisibleItemCount() * 20;
			this.listBoxExternalInterfaceDialog.setHeight(this.borderLayoutHeight - dialogHeight + "px");
			// Store the before image.
			InterfaceConfiguration interfaceConfiguration = new InterfaceConfiguration();
			BeanUtils.copyProperties(this.interfaceDeatilData, interfaceConfiguration);
			this.interfaceDeatilData.setBefImage(interfaceConfiguration);
			interfaceCaption.setValue(this.interfaceDeatilData.getCode());
			fillComboBox(this.status, "", statusList, "");
			// Render the page and display the data
			addSearchControl();
			doShowDialog(this.interfaceDeatilData);
		} catch (Exception e) {
			closeDialog();
			MessageUtil.showError(e);
		}

		logger.debug(Literal.LEAVING);
	}

	public void doShowDialog(InterfaceConfiguration interfaceConfiguration) {
		logger.debug(Literal.ENTERING);

		paging_interfaceService.setPageSize(getPageSize());
		paging_interfaceService.setDetailed(true);

		this.paging_interfaceService.setTotalSize(this.listBoxExternalInterfaceDialog.getItemCount());
		this.paging_interfaceService.setDetailed(true);
		this.paging_interfaceService.setActivePage(0);

		setDialog(DialogType.EMBEDDED);

		logger.debug(Literal.LEAVING);
	}

	private void addSearchControl() {
		searchControls.add(new SearchFilterControl("status", this.status, sortOperator_Status, Operators.STRING));
		if (StringUtils.equalsIgnoreCase(interfaceDeatilData.getType(), INTERFACE)) {
			searchControls
					.add(new SearchFilterControl("REQSENTON", this.fromDate, sortOperator_fromDate, Operators.DATE));
			searchControls.add(new SearchFilterControl("REQSENTON", this.toDate, sortOperator_ToDate, Operators.DATE));
			sortOperator_fromDate.setSelectedIndex(5);
			sortOperator_ToDate.setSelectedIndex(3);
		} else {
			searchControls
					.add(new SearchFilterControl("START_DATE", this.fromDate, sortOperator_fromDate, Operators.DATE));
			searchControls.add(new SearchFilterControl("START_DATE", this.toDate, sortOperator_ToDate, Operators.DATE));
			sortOperator_fromDate.setSelectedIndex(5);
			sortOperator_ToDate.setSelectedIndex(3);
		}
	}

	public void onClick$btnSearch(Event event) {
		logger.debug(Literal.ENTERING);

		List<InterfaceServiceLog> filteredPOs = searchInterfaceDeatils(interfaceDeatilData);
		doFillInterfaceServiceDetails(filteredPOs);

		logger.debug(Literal.LEAVING);
	}

	public void onInterfaceDetailItemDoubleClicked(Event event) {
		logger.debug(Literal.ENTERING);

		// Get the selected record.
		Listitem selectedItem = this.listBoxExternalInterfaceDialog.getSelectedItem();
		final InterfaceServiceLog interfaceDetail = (InterfaceServiceLog) selectedItem.getAttribute("id");

		StringBuilder whereCond = new StringBuilder();
		whereCond.append("  AND  Id = ");
		whereCond.append(interfaceDetail.getReference());
		doShowDialogPage(interfaceDetail);

		logger.debug(Literal.LEAVING);
	}

	protected Map<String, Object> getDefaultArguments() {
		Map<String, Object> aruments = new HashMap<>();
		aruments.put("moduleCode", moduleCode);
		aruments.put("enqiryModule", enqiryModule);
		return aruments;
	}

	private void doShowDialogPage(InterfaceServiceLog interfaceDetail) {
		logger.debug(Literal.ENTERING);

		Map<String, Object> arg = getDefaultArguments();
		arg.put("interfaceServiceList", interfaceDetail);
		arg.put("interfaceServiceListCtrl", this);

		try {
			Executions.createComponents(
					"/WEB-INF/pages/ExternalInterface/ExternalInterfaceConfiguration/InterfaceServiceDialog.zul", null,
					arg);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
			MessageUtil.showError(e);
		}

		logger.debug(Literal.LEAVING);
	}

	@SuppressWarnings("unchecked")
	private List<InterfaceServiceLog> searchInterfaceDeatils(InterfaceConfiguration interfaceConf) {
		logger.debug(Literal.ENTERING);

		if (this.fromDate.getValue() == null || this.toDate.getValue() == null) {
			MessageUtil.showError("Please Enter From Date and To Date ");
			return new ArrayList<>();
		}

		this.paging_interfaceService.setActivePage(0);

		PagedListService pagedListService = getPagedListService();
		searchObject = new JdbcSearchObject<InterfaceServiceLog>(InterfaceServiceLog.class);
		if (StringUtils.equalsIgnoreCase(interfaceConf.getType(), INTERFACE)) {
			searchObject.addTabelName("InterfaceLogDetails");
			searchObject.addField("REFERENCE");
			searchObject.addField("SERVICENAME");
			searchObject.addField("REQUEST");
			searchObject.addField("RESPONSE");
			searchObject.addField("REQSENTON");
			searchObject.addField("RESPRECEIVEDON");
			searchObject.addField("STATUS");
			searchObject.addField("ERRORCODE");
			searchObject.addField("ERRORDESC");
			searchObject.addFilterAnd(Filter.equalTo("serviceName", interfaceConf.getDescription()));
		} else {
			searchObject.addTabelName("IDB_INTERFACES_LOG");
			searchObject.addField("REF_NUM");
			searchObject.addField("INTERFACE_NAME");
			searchObject.addField("RECORDS_PROCESSED");
			searchObject.addField("START_DATE");
			searchObject.addField("END_DATE");
			searchObject.addField("EodDate");
			searchObject.addField("STATUS");
			searchObject.addField("STATUS_DESC");
			searchObject.addField("INTERFACE_INFO");
			searchObject.addFilterAnd(Filter.equalTo("INTERFACE_NAME", interfaceConf.getDescription()));
		}
		for (SearchFilterControl searchControl : searchControls) {
			Filter filter = searchControl.getFilter();
			if (filter != null) {
				if (App.DATABASE == Database.ORACLE && Filter.OP_NOT_EQUAL == filter.getOperator()) {
					Filter[] filters = new Filter[2];
					filters[0] = Filter.isNull(filter.getProperty());
					filters[1] = filter;

					searchObject.addFilterOr(filters);
				} else {
					if (StringUtils.equalsIgnoreCase(interfaceConf.getType(), INTERFACE)) {
						if (StringUtils.equalsIgnoreCase("REQSENTON", filter.getProperty())) {
							Date date = (Date) filter.getValue();
							if (filter.getOperator() == 4) {
								Calendar calendar = Calendar.getInstance();
								calendar.setTime(date);
								calendar.set(Calendar.HOUR, 23);
								calendar.set(Calendar.MINUTE, 59);
								calendar.set(Calendar.SECOND, 59);
								date = calendar.getTime();
							}
							filter.setValue(date);
						}
					} else {
						if (StringUtils.equalsIgnoreCase("START_DATE", filter.getProperty())) {
							Date date = (Date) filter.getValue();
							if (filter.getOperator() == 4) {
								Calendar calendar = Calendar.getInstance();
								calendar.setTime(date);
								calendar.set(Calendar.HOUR, 23);
								calendar.set(Calendar.MINUTE, 59);
								calendar.set(Calendar.SECOND, 59);
								date = calendar.getTime();
							}
							filter.setValue(date);
						}
					}
					searchObject.addFilter(filter);
				}
			}
		}

		// Code Added for paging
		searchObject.setFirstResult(0);
		searchObject.setMaxResults(this.paging_interfaceService.getPageSize());

		// Add 'onPaging' listener to the paging component
		paging_interfaceService.addEventListener("onPaging", new OnPagingEventListener());

		SearchResult<InterfaceServiceLog> srBySearchObject = pagedListService.getSRBySearchObject(searchObject);
		srBySearchObject.getResult();

		this.paging_interfaceService.setTotalSize(srBySearchObject.getTotalCount());

		logger.debug(Literal.LEAVING);
		return srBySearchObject.getResult();
	}

	@SuppressWarnings("rawtypes")
	public final class OnPagingEventListener implements EventListener {
		@Override
		public void onEvent(Event event) {
			final PagingEvent pe = (PagingEvent) event;
			final int pageNo = pe.getActivePage();
			final int start = pageNo * getPageSize();
			refreshModel(start);
		}
	}

	void refreshModel(int start) {
		searchObject.setFirstResult(start);
		searchObject.setMaxResults(getPageSize());
		doFillInterfaceServiceDetails(getPagedListService().getBySearchObject(this.searchObject));
	}

	public void doFillInterfaceServiceDetails(List<InterfaceServiceLog> serviceLogList) {
		logger.debug(Literal.ENTERING);

		this.listBoxExternalInterfaceDialog.getItems().clear();

		if (CollectionUtils.isNotEmpty(serviceLogList)) {

			if (StringUtils.equalsIgnoreCase(interfaceDeatilData.getType(), INTERFACE)) {
				listheader_InterfaceService_Error.setVisible(true);
				listheader_InterfaceService_ErrorDesc.setVisible(true);
				for (InterfaceServiceLog interfaceDetails : serviceLogList) {

					Listitem item = new Listitem();
					Listcell lc;
					lc = new Listcell(interfaceDetails.getReference());
					lc.setParent(item);

					lc = new Listcell(DateUtil.formatToLongDate(interfaceDetails.getReqSentOn()));
					lc.setParent(item);
					lc = new Listcell(
							PennantAppUtil.formateDate(interfaceDetails.getRespReceivedOn(), "dd-MM-yyyy HH:mm:ss"));
					lc.setParent(item);
					lc = new Listcell(interfaceDetails.getStatus());
					lc.setParent(item);
					lc = new Listcell(interfaceDetails.getErrorCode());
					lc.setParent(item);
					lc = new Listcell(interfaceDetails.getErrorDesc());
					lc.setParent(item);
					this.listBoxExternalInterfaceDialog.appendChild(item);
					item.setAttribute("id", interfaceDetails);
					ComponentsCtrl.applyForward(item, "onDoubleClick=onInterfaceDetailItemDoubleClicked");
				}
			} else {
				boolean suntechFlag = false;
				int i = 0;
				if (CollectionUtils.isNotEmpty(serviceLogList))
					if (StringUtils.isNotEmpty(reInitiateService)) {
						String[] reProcess = reInitiateService.split(",");
						for (String process : reProcess) {
							if (StringUtils.containsIgnoreCase(serviceLogList.get(0).getInterface_Name(), process)) {
								listheader_InterfaceService_ReProcess.setVisible(true);
								if (StringUtils.containsIgnoreCase(process, "SUNTECH")) {
									suntechFlag = true;
								}
								break;
							}
						}
					}

				this.listheader_InterfaceService_RecordProcessed.setVisible(true);
				this.listheader_InterfaceService_StatusDesc.setVisible(true);

				this.listheader_InterfaceService_Error.setVisible(false);
				this.listheader_InterfaceService_ErrorDesc.setVisible(false);

				for (InterfaceServiceLog interfaceServiceLog : serviceLogList) {

					Listitem item = new Listitem();
					Listcell lc;
					if (StringUtils.isNotBlank(interfaceServiceLog.getInterface_Info())) {

						if (StringUtils.containsIgnoreCase(interfaceServiceLog.getInterface_Name(), "GLEMS_Exposure")
								|| StringUtils.containsIgnoreCase(interfaceServiceLog.getInterface_Name(), "OGL")) {
							lc = new Listcell(interfaceServiceLog.getInterface_Info());
							lc.setParent(item);
						} else {
							lc = new Listcell(interfaceServiceLog.getRef_num());
							lc.setParent(item);
						}
					} else {
						lc = new Listcell(interfaceServiceLog.getRef_num());
						lc.setParent(item);
					}

					lc = new Listcell(
							PennantAppUtil.formateDate(interfaceServiceLog.getStart_Date(), "dd-MM-yyyy HH:mm:ss"));
					lc.setParent(item);

					lc = new Listcell(
							PennantAppUtil.formateDate(interfaceServiceLog.getEnd_Date(), "dd-MM-yyyy HH:mm:ss"));
					lc.setParent(item);

					lc = new Listcell(interfaceServiceLog.getStatus());
					lc.setParent(item);

					lc = new Listcell("");
					lc.setParent(item);

					lc = new Listcell("");
					lc.setParent(item);

					lc = new Listcell(interfaceServiceLog.getRecords_Processed());
					lc.setParent(item);

					lc = new Listcell(interfaceServiceLog.getStatus_Desc());
					lc.setParent(item);

					Button reProcess = new Button();
					reProcess.addForward("onClick", self, "onClickReprocess", interfaceServiceLog);
					reProcess.setLabel("Re-Process");

					if (!suntechFlag) {
						reProcess.setDisabled(true);
					} else {
						if (StringUtils.containsIgnoreCase(interfaceServiceLog.getStatus(), "SUCCESS"))
							reProcess.setDisabled(true);
					}

					if (!suntechFlag && StringUtils.containsIgnoreCase(interfaceServiceLog.getStatus(), "FAILED")) {
						try {
							bussinessDate = getBussinessDate();
							Date appDate = DateUtil.addDays(SysParamUtil.getAppDate(), -1);
							if (StringUtils.equalsIgnoreCase(bussinessDate, interfaceServiceLog.getInterface_Info())) {
								reProcess.setDisabled(false);
							} else if (DateUtil.compare(appDate, interfaceServiceLog.getEodDate()) == 0) {
								reProcess.setDisabled(false);
							}
						} catch (Exception e) {
							logger.error(Literal.EXCEPTION, e);
						}
					}

					lc = new Listcell();
					lc.setId("reProcess".concat(String.valueOf(i)));
					lc.appendChild(reProcess);
					lc.setParent(item);
					this.listBoxExternalInterfaceDialog.appendChild(item);
					i++;
				}
			}
		}
		logger.debug(Literal.LEAVING);
	}

	public void onClickReprocess(ForwardEvent event) {
		InterfaceServiceLog interfaceServiceDetails = (InterfaceServiceLog) event.getData();
		try {
			IDBInterfaceLogDetail detail = new IDBInterfaceLogDetail();
			detail.setInterfaceName(interfaceServiceDetails.getInterface_Name());
			detail.setRefNum(interfaceServiceDetails.getRef_num());
			detail.setStartDate(interfaceServiceDetails.getStart_Date());
			detail.setEndDate(interfaceServiceDetails.getEnd_Date());
			detail.setStatus(interfaceServiceDetails.getStatus());
			detail.setInterfaceInfo(interfaceServiceDetails.getInterface_Info());
			reInititateService.processErrorRecords(detail);

		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}
	}

	private int getPageSize() {
		int gridRowCount = 0;

		Component component = window.getFellowIfAny("searchGrid");
		if (component != null) {
			gridRowCount = ((Grid) component).getRows().getVisibleItemCount();
		}

		int height = getContentAreaHeight() - (gridRowCount * SEARCH_ROW_SIZE) - (LIST_ROW_SIZE) - (PAGGING_SIZE);
		return height / LIST_ROW_SIZE;
	}

	public PagedListService getPagedListService() {
		return pagedListService;
	}

	public void setPagedListService(PagedListService pagedListService) {
		this.pagedListService = pagedListService;
	}

	public void onClick$btnClose(Event event) {
		doClose(false);
	}

	protected String getBussinessDate() {
		Calendar cal = Calendar.getInstance();
		cal.setTime(SysParamUtil.getAppDate());
		cal.set(Calendar.DAY_OF_MONTH, cal.get(Calendar.DAY_OF_MONTH) - 1);
		Date dt = cal.getTime();
		if (dt != null) {
			try {
				bussinessDate = new SimpleDateFormat("dd-MM-yyyy").format(dt.getTime());
			} catch (Exception e) {
				logger.error(Literal.EXCEPTION, e);
			}
		}
		return bussinessDate;
	}

}
