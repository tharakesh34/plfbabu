package com.pennant.backend.service.limitservice;

import javax.xml.datatype.DatatypeConfigurationException;

public interface LimitRebuild {

	/**
	 * @param custID
	 * @throws DatatypeConfigurationException
	 */
	void processCustomerRebuild(long custID) throws DatatypeConfigurationException;

	/**
	 * @param custGroupID
	 * @throws DatatypeConfigurationException
	 */
	void processCustomerGroupRebuild(long rebuildGroupID,boolean removedFromGroup,boolean addedNewlyToGroup) ;

	void processCustomerGroupSwap(long rebuildGroupID, long resetGroupID);

}