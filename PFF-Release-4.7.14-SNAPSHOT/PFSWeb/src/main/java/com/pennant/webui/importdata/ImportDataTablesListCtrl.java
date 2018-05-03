package com.pennant.webui.importdata;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Button;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Window;

import com.pennant.Interface.service.DailyDownloadInterfaceService;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;

public class ImportDataTablesListCtrl extends GFCBaseCtrl<Object> {
	private static final long serialVersionUID = -886667223042963669L;
	private static final Logger logger = Logger.getLogger(ImportDataTablesListCtrl.class);

	protected Window     window_ImportData;          // autowired
	protected Listbox 	listBoxcoreBanking;          // autowired
	protected Button     btnImportData;              // autowired
	
	private DailyDownloadInterfaceService dailyDownloadInterfaceService;
	private List<ValueLabel> tablesList = PennantStaticListUtil.getImportTablesList();
	private String allowedDailyDownloadList = SysParamUtil.getValueAsString("DAILY_DOWNLOADS");
	
	public ImportDataTablesListCtrl() {
		super();
	}
	
	/**
	 * onCreate$window_ImportData Event  
	 * @param event
	 */
	public void onCreate$window_ImportData(Event event){
		logger.debug("Entering "+event);
		doFillListBoxcoreBanking();
		logger.debug("Leaving "+event);
	}

	/**
	 * onClick$btnImportData Event  
	 * @param event
	 */
	public void onClick$btnImportData(Event event){
		logger.debug("Entering "+event);
		Label status;
		boolean isExecuted = false;
		try{
			Set<Listitem> selectedItems = listBoxcoreBanking.getSelectedItems();
			List<String> tableNamesList = new ArrayList<String>();

			if(selectedItems.isEmpty()){
				MessageUtil.showError(Labels.getLabel("ImportDataList_NoEmpty"));
				return;
			}
			
			for(Listitem selectedItem : selectedItems){
				tableNamesList.add(((Listcell)selectedItem.getFirstChild()).getId());
			}

			for(String tableName : tableNamesList){
				status = (Label)this.listBoxcoreBanking.getFellowIfAny(tableName+"status");
				if(tableName.equals(PennantConstants.DAILYDOWNLOAD_CURRENCY)){
					isExecuted = getDailyDownloadInterfaceService().processCurrencyDetails();
				}else if(tableName.equalsIgnoreCase(PennantConstants.DAILYDOWNLOAD_RELATIONSHIPOFFICER)){
					isExecuted = getDailyDownloadInterfaceService().processRelationshipOfficerDetails();
				} else if(tableName.equalsIgnoreCase(PennantConstants.DAILYDOWNLOAD_CUSTTYPE)){
					isExecuted = getDailyDownloadInterfaceService().processCustomerTypeDetails();
				} else if(tableName.equalsIgnoreCase(PennantConstants.DAILYDOWNLOAD_DEPARMENT)){
					isExecuted = getDailyDownloadInterfaceService().processDepartmentDetails();
				} else if(tableName.equalsIgnoreCase(PennantConstants.DAILYDOWNLOAD_CUSTGROUP)){
					isExecuted = getDailyDownloadInterfaceService().processCustomerGroupDetails();
				} else if(tableName.equalsIgnoreCase(PennantConstants.DAILYDOWNLOAD_ACCOUNTTYPE)){
					isExecuted = getDailyDownloadInterfaceService().processAccountTypeDetails();
				} else if(tableName.equalsIgnoreCase(PennantConstants.DAILYDOWNLOAD_CUSTRATING)){
					isExecuted = getDailyDownloadInterfaceService().processCustomerRatingDetails();
				} else if(tableName.equalsIgnoreCase(PennantConstants.DAILYDOWNLOAD_ABUSERS)){
					isExecuted = getDailyDownloadInterfaceService().processAbuserDetails();
				} else if(tableName.equalsIgnoreCase(PennantConstants.DAILYDOWNLOAD_CUSTOMERS)){
					isExecuted = getDailyDownloadInterfaceService().processCustomerDetails();
				}else if(tableName.equalsIgnoreCase(PennantConstants.DAILYDOWNLOAD_COUNTRY)){
					isExecuted = getDailyDownloadInterfaceService().processCountryDetails();
				}else if(tableName.equalsIgnoreCase(PennantConstants.DAILYDOWNLOAD_CUSTSTATUSCODES)){
					isExecuted = getDailyDownloadInterfaceService().processCustStatusCodeDetails();
				}else if(tableName.equalsIgnoreCase(PennantConstants.DAILYDOWNLOAD_INDUSTRY)){
					isExecuted = getDailyDownloadInterfaceService().processIndustryDetails();
				}else if(tableName.equalsIgnoreCase(PennantConstants.DAILYDOWNLOAD_BRANCH)){
					isExecuted = getDailyDownloadInterfaceService().processBranchDetails();
				}else if(tableName.equalsIgnoreCase(PennantConstants.DAILYDOWNLOAD_SYSINTACCOUNTDEF)){
					isExecuted = getDailyDownloadInterfaceService().processInternalAccDetails();
				} else if(tableName.equalsIgnoreCase(PennantConstants.DAILYDOWNLOAD_TRANSACTIONCODE)){
				    isExecuted = getDailyDownloadInterfaceService().processTransactionCodeDetails();
			    } else if(tableName.equalsIgnoreCase(PennantConstants.DAILYDOWNLOAD_IDENTITYTYPE)){
				    isExecuted = getDailyDownloadInterfaceService().processIdentityTypeDetails();
			    }
				setStatus(isExecuted,status);
			}
		}catch(Exception e){
			logger.error("Exception: ", e);
		}
		logger.debug("Leaving "+event);
	}


	public void setStatus(boolean isExecuted,Label status){
		logger.debug("Entering ");
		if(isExecuted){
			status.setValue("Completed");
			status.setStyle("color:Green");
		}else{
			status.setValue("Not Completed");
			status.setStyle("color:Red");
		}
		logger.debug("Leaving ");
	}

	/**
	 *  Method for Filling listBoxcoreBanking
	 */
	public void doFillListBoxcoreBanking(){
		logger.debug("Entering");
		
		Listitem item;
		Listcell lc;
		Label label;

		for(int i=0; i <tablesList.size(); i++){

			item = new Listitem();
			item.setDisabled(!allowForDownload(tablesList.get(i).getValue()));	
			
			lc = new Listcell(tablesList.get(i).getValue());
			lc.setId(tablesList.get(i).getValue());
			lc.setParent(item);
			lc = new Listcell(tablesList.get(i).getLabel());
			lc.setParent(item);

			lc = new Listcell();
			label = new Label();
			label.setValue("Not Yet Started");
			label.setStyle("color:#80BFFF");
			label.setId(tablesList.get(i).getValue()+"status");
			lc.appendChild(label);
			lc.setParent(item);
			item.setParent(listBoxcoreBanking);
		}
		logger.debug("Leaving");
	}

	private boolean allowForDownload(String code){
		String[] dailyDownloads = allowedDailyDownloadList.split(",");
		for (String downloadName : dailyDownloads) {
			if(code.equalsIgnoreCase(StringUtils.trimToEmpty(downloadName))){
				return true;
			}
		}
		return false;
	}
	
	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//
		
	public DailyDownloadInterfaceService getDailyDownloadInterfaceService() {
		return dailyDownloadInterfaceService;
	}
	public void setDailyDownloadInterfaceService(
			DailyDownloadInterfaceService dailyDownloadInterfaceService) {
		this.dailyDownloadInterfaceService = dailyDownloadInterfaceService;
	}

}
