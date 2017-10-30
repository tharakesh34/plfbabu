/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. All
 * components/modules/functions/classes/logic in this software, unless otherwise stated, the property of Pennant
 * Technologies.
 * 
 * Copyright and other intellectual property laws protect these materials. Reproduction or retransmission of the
 * materials, in whole or in part, in any manner, without the prior written consent of the copyright holder, is a
 * violation of copyright law.
 */
package com.pennanttech.pff.core;

/**
 * The class that defines the constants that are used to specify the environment of the system.
 */
public final class App {
	/**
	 * Enumerates the supported databases.
	 */
	public enum Database {
		SQL_SERVER, ORACLE, DB2, MYSQL, PSQL
	}

	public enum AuthenticationType {
		DAO, LDAP, SSO
	}

	public static final long			ID			= 1;
	public static final String			CODE		= "PFF";
	public static final String			NAME		= "pennApps Lending Factory";

	public static final Database	DATABASE	= Database.PSQL;
	
	private App() {
		super();
	}
}
