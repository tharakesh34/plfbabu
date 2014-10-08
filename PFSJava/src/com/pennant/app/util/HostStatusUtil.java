package com.pennant.app.util;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.StatefulJob;
import org.quartz.utils.Key;
import org.springframework.scheduling.quartz.QuartzJobBean;

import com.pennant.Interface.service.HostStatusEnquiryService;
import com.pennant.coreinterface.model.HostEnquiry;
import com.pennant.equation.util.DateUtility;

public class HostStatusUtil  extends QuartzJobBean implements StatefulJob, Serializable {

    private static final long serialVersionUID = -9200852008893081678L;
	private static HostStatusEnquiryService  hostStatusEnquiryService;
    static String status = "";
    
    @Override
	public void executeInternal(JobExecutionContext context) throws JobExecutionException {
    	
     if(SystemParameterDetails.getSystemParameterValue("HOSTSTATUS_REQUIRED").toString().equals("Y")){
    	 
    	Key jobKey = context.getJobDetail().getKey();
		HostStatusDetail statusDetail = new HostStatusDetail();
		statusDetail.entityCode = jobKey.getName();
		statusDetail.lastCalendar= Calendar.getInstance();
		if(statusDetail.hostStatus==null || statusDetail.lastCalendar.compareTo(Calendar.getInstance())<=0 ){

			if(!statusDetail.fetchStatus){
				statusDetail.fetchStatus=true;		

				try {

					HostEnquiry hostEnquiry = getHostStatusEnquiryService().getHostStatus();
					if(hostEnquiry.getStatusCode() != null){
						statusDetail.hostStatus=hostEnquiry.getStatusCode();
					} else{
						statusDetail.hostStatus="ERR";
					}		

					statusDetail.hostStatusDesc=hostEnquiry.getStatusDesc();
					statusDetail.previousBusinessDate = DateUtility.convertDateFromAS400(new BigDecimal(hostEnquiry.getPrevBusDate()));
					statusDetail.currentBusinessDate = DateUtility.convertDateFromAS400(new BigDecimal(hostEnquiry.getCurBusDate()));
					statusDetail.nextBusinessDate = DateUtility.convertDateFromAS400(new BigDecimal(hostEnquiry.getNextBusDate()));

				} catch (Exception e) {
					statusDetail.hostStatus="ERR";
				}
				statusDetail.lastCalendar= Calendar.getInstance();
				statusDetail.lastCalendar.add(Calendar.SECOND, 30);
				statusDetail.fetchStatus=false;
			}
		}
		
		//Reset System Connection Status
		status = statusDetail.hostStatus;
      }
    }
    
    static class HostStatusDetail{
		String entityCode;
		String hostStatus;
		String hostStatusDesc;
		Calendar lastCalendar;
		boolean fetchStatus=false;
		Date previousBusinessDate;
		Date currentBusinessDate;
		Date nextBusinessDate;
	}
	
	public static  boolean getHostStatus(String entityCode){
		if(StringUtils.trimToEmpty(status).equals("NORM")){
			return true;
		}
		return false;
	}
	
	public static HostStatusEnquiryService getHostStatusEnquiryService() {
		return hostStatusEnquiryService;
	}
	public void setHostStatusEnquiryService(HostStatusEnquiryService hostStatusEnquiryService) {
		HostStatusUtil.hostStatusEnquiryService = hostStatusEnquiryService;
	}
	
}
