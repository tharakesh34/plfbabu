package com.pennant.app.util;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import javax.xml.datatype.DatatypeConfigurationException;

import org.apache.commons.lang.StringUtils;

import com.pennant.Interface.service.HostStatusEnquiryService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.coreinterface.model.HostEnquiry;
import com.pennant.equation.util.DateUtility;

public class HostStatusUtil  {

	private static HashMap<String, HostStatusDetail> hashMap= new HashMap<String, HostStatusUtil.HostStatusDetail>();
	
	//private static HostStatusEnquiry hostStatusEnquiry;
    private static HostStatusEnquiryService  hostStatusEnquiryService;
 
	private static HostStatusDetail fetchHostStatus(String entityCode){	
	  
		if(hashMap==null){
			hashMap= new HashMap<String, HostStatusUtil.HostStatusDetail>();
		}
		HostStatusDetail statusDetail;
		
		if(hashMap.containsKey(entityCode)){
			statusDetail =hashMap.get(entityCode); 
		}else{
			statusDetail = new HostStatusDetail();
			statusDetail.entityCode=entityCode;
			statusDetail.lastCalendar= Calendar.getInstance();
		}
		if(statusDetail.hostStatus==null || statusDetail.lastCalendar.compareTo(Calendar.getInstance())<=0 ){

			if(statusDetail.fetchStatus){
				return statusDetail;
			}
			statusDetail.fetchStatus=true;		
			
			try {
				
				//HostEnquiry hostEnquiry = hostStatusEnquiry.getHostStatus();
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
		
		if(hashMap.containsKey(entityCode)){
			hashMap.remove(entityCode); 
		}
		
		hashMap.put(entityCode, statusDetail);
		return statusDetail;
	}

	public static Date getFormattedDate(int cyymmdd)
			throws DatatypeConfigurationException, ParseException {
		int value = 19000000 + cyymmdd;
		SimpleDateFormat df = new SimpleDateFormat(PennantConstants.dateFormat);
		StringBuffer strValue = new StringBuffer(String.valueOf(value));
		
		return df.parse(strValue.substring(0, 4)+"/"+strValue.substring(4, 6)+"/"+strValue.substring(6, 8));
	}
	
	public static  boolean getHostStatus(String entityCode){
		if(StringUtils.trimToEmpty(fetchHostStatus(entityCode).hostStatus).equals("NORM")){
			return true;
		}
		return false;
	}


	public  Date getPreviousBusinessDate(String entityCode) {
    	return fetchHostStatus(entityCode).previousBusinessDate;
    }

	public  Date getCurrentBusinessDate(String entityCode) {
    	return fetchHostStatus(entityCode).currentBusinessDate;
    }

	public  Date getNextBusinessDate(String entityCode) {
    	return fetchHostStatus(entityCode).nextBusinessDate;
    }
	
	public static HostStatusEnquiryService getHostStatusEnquiryService() {
		return hostStatusEnquiryService;
	}
	public void setHostStatusEnquiryService(HostStatusEnquiryService hostStatusEnquiryService) {
		HostStatusUtil.hostStatusEnquiryService = hostStatusEnquiryService;
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
}
