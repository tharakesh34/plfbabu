package com.pennant.backend.service.dashboard.impl;
import java.sql.Timestamp;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.pennant.backend.dao.dashboard.DetailStatisticsDAO;
import com.pennant.backend.dao.dashboard.DetailStatisticsHeaderDAO;
import com.pennant.backend.model.dashboard.DetailStatistics;
import com.pennant.backend.model.dashboard.DetailStatisticsHeader;
import com.pennant.backend.service.dashboard.DetailStatisticsService;

public class DetailStatisticsServiceImpl implements DetailStatisticsService {

	private static final Logger logger = Logger.getLogger(DetailStatisticsServiceImpl.class);

	DetailStatisticsDAO detailStatisticsDAO;
	DetailStatisticsHeaderDAO detailStatisticsHeaderDAO;

	public DetailStatisticsServiceImpl() {
		super();
	}
	
	public DetailStatisticsDAO getDetailStatisticsDAO() {
		return detailStatisticsDAO;
	}

	public void setDetailStatisticsDAO(DetailStatisticsDAO detailStatisticsDAO) {
		this.detailStatisticsDAO = detailStatisticsDAO;
	}
	public DetailStatisticsHeaderDAO getDetailStatisticsHeaderDAO() {
		return detailStatisticsHeaderDAO;
	}

	public void setDetailStatisticsHeaderDAO(
			DetailStatisticsHeaderDAO detailStatisticsHeaderDAO) {
		this.detailStatisticsHeaderDAO = detailStatisticsHeaderDAO;
	}


	/**
	 * This method fetches records from AuditHeader table and process the each record by calling processStatistics() method
	 * @throws Exception 
	 */
	@Override
	public synchronized void saveOrUpdate() throws Exception {
		logger.debug("Entering ");
		/*Fetching all records from AuditHeader Table where AuditId and AuditDate greater
		  than DetailStaticAudit table*/
		List<DetailStatistics> list = getDetailStatisticsDAO().getAuditDetails();

		if(list!=null && list.size()>0){

			for (int i = 0; i < list.size(); i++) {
				
					DetailStatistics statistics=list.get(i);
					try{
						// Fetch the Audit Details for the module(e.g if module name is Academic fetch from AdtAcedemics table 
						DetailStatistics  auditStatistics =getDetailStatisticsDAO().getAuditDetail(statistics);
						//Fetch statistics from DetailStatistics table 
						List<DetailStatistics> activeStatistics  = getDetailStatisticsDAO().getDetailStatisticsList(auditStatistics);

						if(activeStatistics ==null || activeStatistics.isEmpty()){
							saveDetailStatistics(auditStatistics.getModuleName(), auditStatistics.getNextRoleCode()
									, auditStatistics.getAuditReference(), 0, auditStatistics.getLastMntOn(), true);


							DetailStatisticsHeader detailStatisticsHeader = new DetailStatisticsHeader(auditStatistics.getModuleName()
									,auditStatistics.getNextRoleCode(),1);
							if(getDetailStatisticsHeaderDAO().isExists(detailStatisticsHeader)){
								getDetailStatisticsHeaderDAO().update(detailStatisticsHeader, false);
							}else{
								getDetailStatisticsHeaderDAO().save(detailStatisticsHeader);	
							}

						}else{

							DetailStatistics prevStatistics = activeStatistics.get(0);
							//if record approved or canceled
							if(StringUtils.isEmpty(auditStatistics.getNextRoleCode())){ 

								saveDetailStatistics(auditStatistics.getModuleName(), auditStatistics.getCurrentRoleCode()
										, auditStatistics.getAuditReference(), auditStatistics.getLastMntOn()
										.getTime()-prevStatistics.getLastMntOn().getTime(), auditStatistics.getLastMntOn(), true);
								getDetailStatisticsDAO().updateCompleteStatus(auditStatistics);
								DetailStatisticsHeader detailStatisticsHeader = new DetailStatisticsHeader(auditStatistics.getModuleName(
								),auditStatistics.getCurrentRoleCode(),1);

								if(getDetailStatisticsHeaderDAO().isExists(detailStatisticsHeader)){
									getDetailStatisticsHeaderDAO().update(detailStatisticsHeader, true);
								}

							}else{
								saveDetailStatistics(auditStatistics.getModuleName(), auditStatistics.getNextRoleCode()
										, auditStatistics.getAuditReference(), auditStatistics.getLastMntOn().getTime()
										-prevStatistics.getLastMntOn().getTime(), auditStatistics.getLastMntOn(), true);
								logger.debug(prevStatistics.getRoleCode()+"--"+auditStatistics.getNextRoleCode());
								//if RoleCode  and NextRoleCode of record are not same
								if(!prevStatistics.getRoleCode().equalsIgnoreCase(auditStatistics.getNextRoleCode())){
									DetailStatisticsHeader detailStatisticsHeader = new DetailStatisticsHeader(auditStatistics.getModuleName()
											,auditStatistics.getNextRoleCode(),1);

									if(getDetailStatisticsHeaderDAO().isExists(detailStatisticsHeader)){
										getDetailStatisticsHeaderDAO().update(detailStatisticsHeader, false);
									}else{
										getDetailStatisticsHeaderDAO().save(detailStatisticsHeader);
									}

									detailStatisticsHeader = new DetailStatisticsHeader(auditStatistics.getModuleName()
											,auditStatistics.getCurrentRoleCode(),1);
									getDetailStatisticsHeaderDAO().update(detailStatisticsHeader, true);
								}
							}
						}
						logger.debug(auditStatistics.getModuleName()+"-"+auditStatistics.getCurrentRoleCode());
					}catch(Exception e){
						logger.error("Exception: Module - " + statistics.getModuleName() + "AuditID :" + statistics.getAuditId(), e);
						throw e;
					}
				}
			getDetailStatisticsDAO().updateDetailStaticAudit(list.get(list.size()-1));
		}
		logger.debug("Leaving ");
	}
	/**
	 * This method inserts data into DetailStatistics table
	 * @param moduleName
	 * @param roleCode
	 * @param auditReference
	 * @param timeInMS
	 * @param lastMntOn
	 * @param recordStatus
	 */
	private void saveDetailStatistics(String moduleName, String roleCode,String auditReference
			, long timeInMS, Timestamp lastMntOn,boolean recordStatus){
		getDetailStatisticsDAO().save(new DetailStatistics(moduleName, roleCode, auditReference, timeInMS
				, lastMntOn, recordStatus) );

	}	
}
