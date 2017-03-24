package com.pff.framework.util;

import java.sql.DriverManager;

public class SqlConnection {
	

	public java.sql.Connection getConnection()
	{
		java.sql.Connection con=null;
		if(con==null)
		{
			try{
				Class.forName(ServiceProperties.getSqlDriverClassName());  
				con=DriverManager.getConnection( ServiceProperties.getSqlUrl(),ServiceProperties.getSqlUserName(),ServiceProperties.getSqlPassword());
			}
			catch(Exception e)
			{
				System.out.println(e);
			}
		}
		return con;  
	}
}


