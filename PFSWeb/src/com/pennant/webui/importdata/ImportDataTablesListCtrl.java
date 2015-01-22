package com.pennant.webui.importdata;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

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
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.SystemParameterDetails;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.PTMessageUtils;

public class ImportDataTablesListCtrl extends GFCBaseCtrl implements Serializable {

	private static final long serialVersionUID = -886667223042963669L;
	private final static Logger logger = Logger.getLogger(ImportDataTablesListCtrl.class);

	protected Window     window_ImportData;          // autowired
	protected Listbox 	listBoxcoreBanking;          // autowired
	protected Button     btnImportData;              // autowired
	
	private DailyDownloadInterfaceService dailyDownloadInterfaceService;
	private List<ValueLabel> tablesList = PennantStaticListUtil.getImportTablesList();
	
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
		Date dateValueDate = DateUtility.getDBDate(SystemParameterDetails.getSystemParameterValue("APP_DATE").toString());
		Label status;
		boolean isExecuted = false;
		try{
			Set<Listitem> selectedItems = listBoxcoreBanking.getSelectedItems();
			List<String> tableNamesList = new ArrayList<String>();

			if(selectedItems.isEmpty()){
				PTMessageUtils.showErrorMessage(Labels.getLabel("ImportDataList_NoEmpty"));
				return;
			}
			
			for(Listitem selectedItem : selectedItems){
				tableNamesList.add(((Listcell)selectedItem.getFirstChild()).getId());
			}

			for(String tableName : tableNamesList){
				status = (Label)this.listBoxcoreBanking.getFellowIfAny(tableName+"status");
				if(tableName.equals("Currencies")){
					isExecuted = getDailyDownloadInterfaceService().processCurrencyDetails();
				}else if(tableName.equalsIgnoreCase("RelationshipOfficer")){
					isExecuted = getDailyDownloadInterfaceService().processRelationshipOfficerDetails();
				} else if(tableName.equalsIgnoreCase("CustomerType")){
					isExecuted = getDailyDownloadInterfaceService().processCustomerTypeDetails();
				} else if(tableName.equalsIgnoreCase("Deparment")){
					isExecuted = getDailyDownloadInterfaceService().processDepartmentDetails();
				} else if(tableName.equalsIgnoreCase("CustomerGroup")){
					isExecuted = getDailyDownloadInterfaceService().processCustomerGroupDetails();
				} else if(tableName.equalsIgnoreCase("RMTAccountTypes")){
					isExecuted = getDailyDownloadInterfaceService().processAccountTypeDetails();
				} else if(tableName.equalsIgnoreCase("CustomerRatings")){
					isExecuted = getDailyDownloadInterfaceService().processCustomerRatingDetails(dateValueDate);
				} else if(tableName.equalsIgnoreCase("EQNAbuserList")){
					isExecuted = getDailyDownloadInterfaceService().processAbuserDetails();
				} else if(tableName.equalsIgnoreCase("Customers")){
					isExecuted = getDailyDownloadInterfaceService().processCustomerDetails(dateValueDate);
				}else if(tableName.equalsIgnoreCase("BMTCountries")){
					isExecuted = getDailyDownloadInterfaceService().processCountryDetails();
				}else if(tableName.equalsIgnoreCase("BMTCustStatusCodes")){
					isExecuted = getDailyDownloadInterfaceService().processCustStatusCodeDetails();
				}else if(tableName.equalsIgnoreCase("BMTIndustries")){
					isExecuted = getDailyDownloadInterfaceService().processIndustryDetails();
				}else if(tableName.equalsIgnoreCase("RMTBranches")){
					isExecuted = getDailyDownloadInterfaceService().processBranchDetails();
				}else if(tableName.equalsIgnoreCase("SystemInternalAccountDef")){
					isExecuted = getDailyDownloadInterfaceService().processInternalAccDetails(dateValueDate);
				} else if(tableName.equalsIgnoreCase("BMTTransactionCode")){
				    isExecuted = getDailyDownloadInterfaceService().processTransactionCodeDetails();
			    } else if(tableName.equalsIgnoreCase("BMTIdentityType")){
				    isExecuted = getDailyDownloadInterfaceService().processIdentityTypeDetails();
			    }
				setStatus(isExecuted,status);
			}
		}catch(Exception e){
			logger.error(e);
			e.printStackTrace();
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

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
		
	public DailyDownloadInterfaceService getDailyDownloadInterfaceService() {
		return dailyDownloadInterfaceService;
	}
	public void setDailyDownloadInterfaceService(
			DailyDownloadInterfaceService dailyDownloadInterfaceService) {
		this.dailyDownloadInterfaceService = dailyDownloadInterfaceService;
	}

}
