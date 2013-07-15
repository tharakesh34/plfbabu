package com.pennant.equation.util;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Properties;

import javax.naming.InitialContext;

public class Util {

/*----------utility for conversion of date and currency------------*/


	public static char checkBoxToYN(String val){
		
		char newVal;
		
		
		if (val == null || val.charAt(0) =='\u0000')
			newVal ='N';
		else
			newVal = 'Y';
			
		return(newVal);
	}	
		
	public static String checkBoxFromYN(char val){
		
		String newVal;
		
		
		if (val=='\u0000' || val == 'N')
			newVal = "";
		else
			newVal = "checked";
			
		return(newVal);
	}	
	
	public static String cDateFromAS400(BigDecimal as400Date){
		String pcDate = "";
		BigDecimal dateInt = null;
	
		if (as400Date != null){
			if  (new BigDecimal(0).compareTo(as400Date) !=0) {
				dateInt = new BigDecimal(19000000).add(as400Date);
				pcDate = dateInt.toString().substring(6,8) + "/" + dateInt.toString().substring(4,6) + "/" + dateInt.toString().substring(0,4);
			}
			else if (as400Date.equals("9999999")) 	
			{
				pcDate="Open";
			}
			else
				pcDate = "";
		}
		
		return(pcDate);
	}

	public static BigDecimal cDateToAS400(String pcDate){
		BigDecimal as400Date= null;
		BigDecimal dateInt = null;
		
		if(pcDate==null)
			return null;
		
		if (!pcDate.trim().equals("")) {
			dateInt = 	new BigDecimal(pcDate.substring(6,10) + pcDate.substring(3,5) + pcDate.substring(0,2));
			as400Date = new BigDecimal(19000000).subtract(dateInt);
			as400Date = new BigDecimal(-1).multiply(as400Date);
		}
		else
			as400Date = null;
			
		return as400Date;
			
	}
	
	public static java.sql.Date formatDate(String pcDate){

		
		if(pcDate==null && !pcDate.trim().equals(""))
			return null;
		
		return java.sql.Date.valueOf(pcDate.substring(6,10) + "-" + pcDate.substring(3,5) + "-" + pcDate.substring(0,2));
	}

	public static String formatDate(java.sql.Date as400Date){

		
		if(as400Date==null)
			return "";
		
		return as400Date.toString().substring(8,10) + "/" + as400Date.toString().substring(5,7) + "/" + as400Date.toString().substring(0,4);
	}

	public static String formatAmount(String value)
	{

		if(value==null)
			return "0.00";

		if(value=="null" || value.trim().equals(""))
			return "0.00";

		return formatAmount(replace(value));
	}

	public static String formatRate(String value)
	{
		if(value==null)
			return "0.00";

		if(value=="null" || value.trim().equals(""))
			return "0.00";

		return formatRate(replace(value));
	}

	public static String formatRate(BigDecimal value)
	{

		if(value==null)
			return "0.00";

		final String format="###,##0.000000";
		java.text.DecimalFormat df = new java.text.DecimalFormat();
		StringBuffer sb = new StringBuffer(format);
		df.applyPattern(sb.toString());
		return df.format(value).toString();			
	}

	public static String formatAmount(BigDecimal value)
	{

		if(value==null)
			return "0.00";

		final String format="###,##0.00";
		java.text.DecimalFormat df = new java.text.DecimalFormat();
		StringBuffer sb = new StringBuffer(format);
		df.applyPattern(sb.toString());
		return df.format(value).toString();			
	}

	public static String formatAmount(BigDecimal value, int decPos,boolean debitCreditSymbol)
	{
		if(value==null)
			return "0.00";

		if(value.compareTo(new BigDecimal(0))==0)
			return "0.00";

		final String format="###,##0";
		java.text.DecimalFormat df = new java.text.DecimalFormat();
		StringBuffer sb = new StringBuffer(format);

		if(decPos>0)
		{
			sb.append('.');
			for(int i=0;i<decPos;i++)
					sb.append('0');

			String num=value.toString();
			if(num.length()<2)
				num="0"+num;
			int len=num.length();
			value=new BigDecimal(num.substring(0,len-decPos) + '.' +num.substring(len-decPos));

		}
		
		if (debitCreditSymbol) {
			String s = sb.toString();
			sb.append(" 'Cr';").append(s).append(" 'Dr'");
		}

		df.applyPattern(sb.toString());
		return df.format(value).toString();			
	}

	public static String replace(String value, String replVal, String with)
	{
		if(value==null)
			return null;

		if(value=="null" || value.trim().equals(""))
			return null;
		
		StringBuffer sb = new StringBuffer();
		for(int i=0;i<=value.length()-1;i++)
		{
			if(String.valueOf(value.substring(i).charAt(0)).equals(replVal))
				sb.append(with);
			else
				sb.append(value.substring(i).charAt(0));
				
		}
		return sb.toString();
		
	}
	
	public static BigDecimal replace(String value)
	{
		if(replace(value,",","")==null)
			return new BigDecimal("0.00");
		else
			return new BigDecimal(replace(value,",",""));
		
	}
		
	public static String formatParam(String param)
	{
		String value="";
		if(param=="null" || param==null)
			return value;
		else
			return param;
	}

	public static String formatAccountNo(String accountNo) {
		if (accountNo == null || accountNo.length() != 13) {
			throw new IllegalArgumentException("Account Number should be 13 characters");
		}
		StringBuffer sb = new StringBuffer(accountNo);
		sb.insert(4, "-").insert(11, "-");
		return sb.toString();
	}

	public static String getBranch(String accountNo) {
		if (accountNo == null || accountNo.length() != 13) {
			throw new IllegalArgumentException("Account Number should be 13 characters");
		}
		return accountNo.substring(0,4);
	}

	public static String getCustomer(String accountNo) {
		if (accountNo == null || accountNo.length() != 13) {
			throw new IllegalArgumentException("Account Number should be 13 characters");
		}
		return accountNo.substring(4,10);
	}

	public static String getSuffix(String accountNo) {
		if (accountNo == null || accountNo.length() != 13) {
			throw new IllegalArgumentException("Account Number should be 13 characters");
		}
		return accountNo.substring(10,13);
	}

	public static String cCCardDateFromAS400(BigDecimal as400Date){

		String pcDate = "";
	
		if (as400Date != null){

			if(as400Date.toString().length()==4)
			{
				pcDate = as400Date.toString().substring(0,2) + "/20" + as400Date.toString().substring(2);
			}
			else if(as400Date.toString().length()==3)
			{
				pcDate = as400Date.toString().substring(0,1) + "/20" + as400Date.toString().substring(1);
			}
		}
		return(pcDate);
	}
	
	public static String getTS()
	{
		
		return String.valueOf(new java.sql.Date(System.currentTimeMillis()) +":" + new java.sql.Time(System.currentTimeMillis()) + ":" + System.currentTimeMillis());	
	}
	
	public static String removeSplChar(String arg)
	{
		StringBuffer sb = new StringBuffer();
		for(int i=0;i<=arg.length()-1;i++)
		{
			if(String.valueOf(arg.substring(i).charAt(0)).equals("'"))
				sb.append("");
			else
				sb.append(arg.substring(i).charAt(0));
		}
		return sb.toString();
	}
    	
	public static String convertScientificAmount(String strData)
	{
		String str = "0";
		
       if(strData == null || "".equals(strData))
            str = "0";
       else
           str = strData;
            
         
	   java.math.BigDecimal value = new java.math.BigDecimal(str);
	   final String format="################0.0000";
	   java.text.DecimalFormat df = new java.text.DecimalFormat();
	   df.applyPattern(format);
	   System.out.println(df.format(value).toString());			
	   return df.format(value).toString();
	}
	
	public static BigDecimal getTodaysDate()
	{
	   BigDecimal objBigDecimal = null;
	   String dateFormat = "dd/MM/yyyy";
	   java.text.SimpleDateFormat df = new java.text.SimpleDateFormat(dateFormat);
	   Calendar objCalendar = Calendar.getInstance();	
	   String strValue = df.format(objCalendar.getTime());
	   objBigDecimal = Util.cDateToAS400(strValue);
	   return objBigDecimal;
	}
/***************************Method is used get Date *************************/		
	public static String getTodayDate()
	{
	   BigDecimal objBigDecimal = null;
	   String dateFormat = "MM/dd/yyyy";
	   java.text.SimpleDateFormat df = new java.text.SimpleDateFormat(dateFormat);
	   Calendar objCalendar = Calendar.getInstance();	
	   String strValue = df.format(objCalendar.getTime());
	   return strValue;
	}
/*****************Method is used in Audit trail to get Date Time **************/	
	public static String getTodayDateTime()
	{
	   BigDecimal objBigDecimal = null;
	   String dateFormat = "MM/dd/yyyy hh:mm:ss";
	   java.text.SimpleDateFormat df = new java.text.SimpleDateFormat(dateFormat);
	   Calendar objCalendar = Calendar.getInstance();	
	   String strValue = df.format(objCalendar.getTime());
	   return strValue;
	}


	public static String cDateFromAS400Fromat(BigDecimal as400Date){
		String pcDate = "";
		BigDecimal dateInt = null;
	
		if (as400Date != null){
			if  (new BigDecimal(0).compareTo(as400Date) !=0) {
				dateInt = new BigDecimal(19000000).add(as400Date);
				pcDate = dateInt.toString().substring(4,6) + "/" + dateInt.toString().substring(6,8) + "/" + dateInt.toString().substring(0,4);
			}
			else if (as400Date.equals("9999999")) 	
			{
				pcDate="Open";
			}
			else
				pcDate = "";
		}
		return(pcDate);
	}
	
	// Added by Venkatesh for Checking the Dates.
	public static java.sql.Date cDateFromAS400Date(BigDecimal as400Date){
		java.sql.Date sqlDate= null;
		BigDecimal dateInt = null;
	
		if (as400Date != null)
			sqlDate =  formatDate(cDateFromAS400(as400Date));	
		
		return sqlDate;
	}
	
	public static String compareStatus(java.util.Date date1, java.util.Date date2){
		GregorianCalendar gc1, gc2;
		gc1 = new GregorianCalendar(getYear(date1),getMonth(date1)-1, getDay(date1));
		gc2 = new GregorianCalendar(getYear(date2),getMonth(date2)-1, getDay(date2));
		if(gc1.after(gc2)) return "Regular";
		if(gc1.before(gc2)) return "Expiry";
		return "Regular";
	  }
	
	public static int getYear(java.util.Date date){
		if(date == null) return -1;
		return convert(date).get(Calendar.YEAR);
	  }  
	
	 public static int getMonth(java.util.Date date){
			if(date == null) return -1;
			return convert(date).get(Calendar.MONTH) + 1;
		  }  
	 
	 public static int getDay(java.util.Date date){
			if(date == null) return -1;
			return convert(date).get(Calendar.DATE);
		  }
	 
	 public static GregorianCalendar convert(java.util.Date date){
		if(date == null) return null;
		GregorianCalendar gc = new GregorianCalendar();
		gc.setTime(date);
		return gc;
	  }  
	
	// Added by K. Ramesh Babu on 09/11/2008 for AS400 Locking
	public static PreparedStatement closeStatement(PreparedStatement stmt)  {
        try {
            if (stmt != null) {
                stmt.close();
            }
        } catch (SQLException se) {
        	se.printStackTrace();
        } catch (Exception e) {
        	e.printStackTrace();        
        }
        return null;
    }
	
	public static CallableStatement closeStatement(CallableStatement stmt)  {
        try {
            if (stmt != null) {
                stmt.close();
            }
        } catch (SQLException se) {
        	se.printStackTrace();
        } catch (Exception e) {
        	e.printStackTrace();        
        }
        return null;
    }
	
	public static Statement closeStatement(Statement stmt)  {
        try {
            if (stmt != null) {
                stmt.close();
            }
        } catch (SQLException se) {
        	se.printStackTrace();
        } catch (Exception e) {
        	e.printStackTrace();        
        }
        return null;
    }

	//	 Added by K. Ramesh Babu on 09/11/2008 for AS400 Locking
	public static ResultSet closeResultSet(ResultSet result)  {
        try {
            if (result != null) {
                result.close();
            }
        } catch (SQLException se) {
        	se.printStackTrace();        	
        } catch (Exception e) {
        	e.printStackTrace();           
        }
        return null;
    }    
	
    //  Added by K. Ramesh Babu on 09/11/2008 for AS400 Locking
  
	public static Connection closeDBConnection(Connection dbConnection)  {
    	return closeDBConnection(dbConnection,false);        
    }
    
	public static Connection closeDBConnection(Connection dbConnection, boolean isCommit)  {
    	try {
            if (dbConnection != null && !dbConnection.isClosed()) {
            	if(isCommit) {
            		dbConnection.commit();
            	}
                dbConnection.close();
            }
        } catch (SQLException se) {
        	se.printStackTrace();       	
        } catch (Exception e) {
        	e.printStackTrace();          
        }
        return null;
    }
	
	public static Object lookup(String url)  {
		Object object = null;
		try {
			Properties properties = new Properties();
			properties.put(javax.naming.Context.INITIAL_CONTEXT_FACTORY,	"com.ibm.websphere.naming.WsnInitialContextFactory");
			InitialContext initialContext = new InitialContext(properties);
			object = initialContext.lookup(url);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	 	 
		return object; 
	}
	
}
