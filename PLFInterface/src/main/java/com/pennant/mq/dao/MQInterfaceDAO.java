package com.pennant.mq.dao;

import com.pennant.exception.PFFInterfaceException;


public interface MQInterfaceDAO {

	String getMDMCode(String code,String tableName) throws PFFInterfaceException;
	String getPFFCode(String code,String tablename) throws PFFInterfaceException;
}
