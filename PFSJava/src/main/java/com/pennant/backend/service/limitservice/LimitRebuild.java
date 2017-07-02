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
	void processCustomerGroupRebuild(long custGroupID) throws DatatypeConfigurationException;

}