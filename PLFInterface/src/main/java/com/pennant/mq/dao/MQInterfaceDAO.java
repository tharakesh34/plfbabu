package com.pennant.mq.dao;

import com.pennanttech.pff.core.InterfaceException;


public interface MQInterfaceDAO {

	String getMDMCode(String code,String tableName) throws InterfaceException;
	String getPFFCode(String code,String tablename) throws InterfaceException;
}
