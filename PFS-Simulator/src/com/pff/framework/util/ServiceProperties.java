package com.pff.framework.util;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;

import com.pff.service.AccountDetailService;
import com.pff.service.AccountPostingsService;
import com.pff.service.AddHoldService;
import com.pff.service.CollateralDeMarkService;
import com.pff.service.CollateralMarkService;
import com.pff.service.CreateAccountService;
import com.pff.service.CreateCIFService;
import com.pff.service.CustomerDedupService;
import com.pff.service.CustomerLimitPositionService;
import com.pff.service.CustomerLimitService;
import com.pff.service.CustomerLimitUtilizationService;
import com.pff.service.CustomerService;
import com.pff.service.DDAAmendmentService;
import com.pff.service.DDACancellService;
import com.pff.service.DDAService;
import com.pff.service.FetchDepositService;
import com.pff.service.ReleaseCIFService;
import com.pff.service.RemoveHoldService;
import com.pff.service.ReserveCIFService;
import com.pff.service.ReversalPostingsService;

public class ServiceProperties {

	private static Properties properties = null;
	private static HashMap<String, MessageProperty> messageMap= null;

	public static String getMQServerIP(){
		String mqServerIp=(String)getProperties().get("SERVERIP");
		if(StringUtils.trimToEmpty(mqServerIp).equals(""))
		{
			mqServerIp="localhost";
		}
		return mqServerIp;
	}
	public static String getServerIP(){
		String mqServerIp=(String)getProperties().get("SERVERIP");
		if(StringUtils.trimToEmpty(mqServerIp).equals(""))
		{
			mqServerIp="localhost";
		}
		return mqServerIp;
	}

	public static String getMQInputQ(){
		return (String) getProperties().get("MQINPUTQ02");
		
	} 
	
	public static String getReqQ(){
		return (String) getProperties().get("REQQUEUE");
	} 
	public static String getResQ(){
		return (String) getProperties().get("RESQUEUE");
	} 

	public static String getMQErrorQ(){
		return (String) getProperties().get("MQERRORQ");
	}


	public static String getEquationIP(){
		return (String) getProperties().get("EQUATIONIP");
	} 

	public static String getEquationUnit(){
		return StringUtils.trimToEmpty((String) getProperties().get("UNITNAME"));
	} 

	public static String getAppLibrary(){
		return StringUtils.trimToEmpty((String) getProperties().get("APPLIBRARY"));
	} 


	public static String getReplayQManager(){
		return (String) getProperties().get("BTRSPQMGRNAME");
	} 

	public static HashMap<String, MessageProperty> getMessageMap(){
		if(messageMap==null || messageMap.size()==0){
			messageMap= new HashMap<String, MessageProperty>();
			loadMessageProperties("MSGFMT_CUST_INFO", 1100,CustomerService.class);
			loadMessageProperties("MSGFMT_RESERVECIF_INFO", 1100,ReserveCIFService.class);
			loadMessageProperties("MSGFMT_RELEASECIF_INFO", 1100,ReleaseCIFService.class);
			loadMessageProperties("MSGFMT_CREATECIF_INFO",  1100,CreateCIFService.class);
			loadMessageProperties("MSGFMT_UPDATECIF_INFO",  1100,CreateCIFService.class);
			loadMessageProperties("MSGFMT_CUSTDEDUP_INFO",  1100,CustomerDedupService.class);
			loadMessageProperties("MSGFMT_ACCOUNT_INQUIRY",    1100,AccountDetailService.class);
			loadMessageProperties("MSGFMT_CREATEACCOUNT_INFO",1100,CreateAccountService.class);
			loadMessageProperties("MSGFMT_CUSTOMERLIMIT_INFO",1100,CustomerLimitService.class);
			loadMessageProperties("MSGFMT_CUSTOMERLIMITSUMMARY_INFO",1100,CustomerLimitPositionService.class);
			loadMessageProperties("MSGFMT_RESERVEUTILIZATION_INFO",1100,CustomerLimitUtilizationService.class);
			loadMessageProperties("MSGFMT_PREDEALCHECK_INFO",1100,CustomerLimitUtilizationService.class);
			loadMessageProperties("MSGFMT_CONFIRMRESERVE_INFO",1100,CustomerLimitUtilizationService.class);
			loadMessageProperties("MSGFMT_OVERRIDE_RESERVE_INFO",1100,CustomerLimitUtilizationService.class);
			loadMessageProperties("MSGFMT_CANCELRESERVE_INFO",1100,CustomerLimitUtilizationService.class);
			loadMessageProperties("MSGFMT_CANCELUTILIZATION_INFO",1100,CustomerLimitUtilizationService.class);
			loadMessageProperties("MSGFMT_UAEDDS_INFO",1100,DDAService.class);//FIXME
			loadMessageProperties("MSGFMT_UAEDDSAMMENDMENT_INFO",1100,DDAAmendmentService.class);//FIXME
			loadMessageProperties("MSGFMT_ADDHOLD_INFO",1100,AddHoldService.class);//FIXME
			loadMessageProperties("MSGFMT_REMOVEHOLD_INFO",1100,RemoveHoldService.class);//FIXME
			loadMessageProperties("MSGFMT_COLLATERALMARK_INFO",1100,CollateralMarkService.class);//FIXME
			loadMessageProperties("MSGFMT_ACCOUNTPOSTINGS_INFO",1100,AccountPostingsService.class);
			loadMessageProperties("MSGFMT_REVERSEPOSTINGS_INFO",1100,ReversalPostingsService.class);
			loadMessageProperties("MSGFMT_ACCOUNTS_INQUIRY",    1100,AccountDetailService.class);//FIXME
			loadMessageProperties("MSGFMT_COLLATERALUNMARK_INFO",    1100,CollateralDeMarkService.class);//FIXME
			loadMessageProperties("MSGFMT_DEPOSIT_DETAILS",    1100,FetchDepositService.class);//FIXME
			loadMessageProperties("MSGFMT_DDA_CANCELLATION",    1100,DDACancellService.class);//FIXME

		}

		return messageMap;
	}
	private static void loadMessageProperties(String propertyKey,int serviceCode,Class<?> serviceClass){
		String messageFomats = (String) getProperties().get(propertyKey);
		if(!StringUtils.trimToEmpty(messageFomats).equals("")){
			String[] formates =  messageFomats.split(",");

			for (String messsageFormat : formates) {
				messageMap.put(messsageFormat, new MessageProperty(messsageFormat, serviceCode, serviceClass) );
			} 
		}
	}
	public static String getAS400PassWordCode(){
		return (String) getProperties().get("AS400PWDENCCODE");
	} 
	public static boolean getAS400PassWordEnc(){
		if(StringUtils.trimToEmpty((String) getProperties().get("AS400PWDENC")).equals("Y")){
			return true;
		}
		return false;
	}
	public static MessageProperty getMessageProperties (String messageType){

		MessageProperty property=null;
		if(getMessageMap().containsKey(messageType)){
			return getMessageMap().get(messageType);
		}

		return property;
	}

	public static int getMQServerPort(){
		String mqServerPort = (String) getProperties().get("MQSERVERPORT");
		if(StringUtils.trimToEmpty(mqServerPort).equals("")){
			mqServerPort = "0000";
		}	
		return Integer.parseInt(mqServerPort);
	}
	public static int getServerPort(){
		String mqServerPort = (String) getProperties().get("SERVERPORT");
		if(StringUtils.trimToEmpty(mqServerPort).equals("")){
			mqServerPort = "0000";
		}	
		return Integer.parseInt(mqServerPort);
	}


	public static String getQManager(){
		return (String) getProperties().get("QMGRNAME");
	} 

	public static String getAS400ServerIP(){
		return (String) getProperties().get("MQSERVERIP");
	} 

	public static String getMQChannel(){
		String mqChannel = (String) getProperties().get("MQCHANNEL");
		if(StringUtils.trimToEmpty(mqChannel).equals("")){
			mqChannel = "SYSTEM.DEF.SVRCONN";
		}	
		return mqChannel;
	}
	public static String getChannel(){
		String mqChannel = (String) getProperties().get("CHANNEL");
		if(StringUtils.trimToEmpty(mqChannel).equals("")){
			mqChannel = "SYSTEM.DEF.SVRCONN";
		}	
		return mqChannel;
	}
	public static int getServerMessageWait(){
		String serverMessageWaitTime = (String) getProperties().get("FX_WAITTIME");
		if(StringUtils.trimToEmpty(serverMessageWaitTime).equals("")){
			serverMessageWaitTime = "0000";
		}	
		return Integer.parseInt(serverMessageWaitTime);
	} 
	
	public static String getAS400UserId(){
		return (String) getProperties().get("AS400USERID");
	} 

	
	public static String getAS400AppLibrary(){
		return (String) getProperties().get("AS400LIBRARY");
	} 
	
	public static Properties getProperties(){

		if(properties==null){
			String configFileName="PFFService.CONFIG";//CONFIG FILE 
			InputStream ioStream=null;
			try {
				ioStream = ServiceProperties.class.getResourceAsStream("/"+ configFileName);
				if(ioStream== null){
					throw new RuntimeException("Failed to load "+ configFileName +" from CLASSPATH.");
				}

				properties = new Properties();
				properties.load(ioStream);
			}catch (RuntimeException e) {
				throw e;	
			}catch (Exception e) {
				properties = null;
			}
		}
		return properties;
	}

		//SQL SqlConnection
	
	public static String getSqlDriverClassName(){

		return (String) getProperties().get("SQLDRIVERCLASSNAME");

	} 
	public static String getSqlUrl(){

		return (String) getProperties().get("SQLURL");

	} 
	public static String getSqlUserName(){

		return (String) getProperties().get("SQLUSERNAME");

	} 
	public static String getSqlPassword(){

		return (String) getProperties().get("SQLPASSWORD");

	} 

		
}
