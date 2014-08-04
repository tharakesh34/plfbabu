
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
 *
 * FileName    		: ReportConfigurationService.java								        *                           
 *                                                                    
 * Author      		:  PENNANT TECHONOLOGIES												*
 *                                                                  
 * Creation Date    :  5-09-2012															*
 *                                                                  
 * Modified Date    :  5-09-2012														    *
 *                                                                  
 * Description 		:												 						*                                 
 *                                                                                          
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 5-09-2012	       Pennant	                 0.1                                        * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 ********************************************************************************************
 */package com.pennant.backend.service.reports;

import java.util.List;
import java.util.Map;

import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.reports.ReportConfiguration;
import com.pennant.backend.model.reports.ReportSearchTemplate;



public interface ReportConfigurationService {
	
	public ReportConfiguration getReportConfiguration();
	public ReportConfiguration getNewReportConfiguration();
	public AuditHeader saveOrUpdate(AuditHeader auditHeader);
	public ReportConfiguration getReportConfigurationById(long id);
	public ReportConfiguration getApprovedReportConfigurationById(long id);
	public ReportConfiguration refresh(ReportConfiguration currency);
	public AuditHeader delete(AuditHeader auditHeader);
	public AuditHeader doApprove(AuditHeader auditHeader);
	public AuditHeader doReject(AuditHeader auditHeader);
	public void saveOrUpdateSearchTemplate(List<ReportSearchTemplate> aReportSearchTemplateList, boolean isNew);
	public Map<Object, List<ReportSearchTemplate>> getTemplatesByReportID(long reportId,long usrId);	
	public int getRecordCountByTemplateName(long reportId,long usrId,String templateName);
	public void deleteSearchTemplate(long reportId, long usrId, String templateName);
	
	//Month End Report Queries
	public List<ValueLabel> getMonthEndReportGrpCodes();
	public List<ValueLabel> getReportListByGrpCode(String grpCode);

}
