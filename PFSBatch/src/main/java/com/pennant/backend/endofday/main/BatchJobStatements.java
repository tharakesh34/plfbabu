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

/**
 ********************************************************************************************
 * FILE HEADER *
 ********************************************************************************************
 *
 * FileName : BatchJobStatements.java *
 * 
 * Author : PENNANT TECHONOLOGIES *
 * 
 * Creation Date : 26-04-2011 *
 * 
 * Modified Date : 30-07-2011 *
 * 
 * Description : *
 * 
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 26-04-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.endofday.main;

import com.pennanttech.pennapps.core.App;

public class BatchJobStatements {

	/**
	 * Method for prepare SQL query to Job Execution ID for EOD / Amortization Spring Jobs
	 * 
	 */
	public static String prepareJobExecutionQuery() {

		StringBuilder query = new StringBuilder();

		query.append(" SELECT COALESCE(MAX(JOB_EXECUTION_ID), 0) JOBID FROM BATCH_JOB_EXECUTION T1");
		query.append(" INNER JOIN BATCH_JOB_INSTANCE T2 ON T1.JOB_INSTANCE_ID = T2.JOB_INSTANCE_ID");
		query.append(" WHERE T2.JOB_NAME = ? ");

		return query.toString();
	}

	/**
	 * This method returns average time of last 30 success full jobs, the job
	 * 
	 */
	public static String prepareAvgTimeCalQuery() {

		StringBuilder query = new StringBuilder();

		switch (App.DATABASE) {

		case ORACLE:
			query.append(" SELECT CEIL(AVG((END_TIME-START_TIME)*24*60*60*1000))  avg FROM");
			query.append(" (SELECT * FROM BATCH_JOB_EXECUTION T1 ");
			query.append(" INNER JOIN BATCH_JOB_INSTANCE T2 on T1.JOB_INSTANCE_ID = T2.JOB_INSTANCE_ID");
			query.append(" WHERE T2.JOB_NAME = ? AND T1.JOB_INSTANCE_ID NOT IN (SELECT JOB_INSTANCE_ID");
			query.append(" FROM BATCH_JOB_EXECUTION WHERE STATUS IN ('FAILED', 'STARTED', 'STOPPED'))) T");

			break;

		case POSTGRES:
			query.append(
					" SELECT AVG(DATE_PART ('millisecond', START_TIME::timestamp - END_TIME::timestamp))  avg FROM");
			query.append(" (SELECT * FROM BATCH_JOB_EXECUTION T1 ");
			query.append(" INNER JOIN BATCH_JOB_INSTANCE T2 on T1.JOB_INSTANCE_ID = T2.JOB_INSTANCE_ID");
			query.append(" WHERE T2.JOB_NAME = ? AND T1.JOB_INSTANCE_ID NOT IN (SELECT JOB_INSTANCE_ID");
			query.append(" FROM BATCH_JOB_EXECUTION WHERE STATUS IN ('FAILED', 'STARTED', 'STOPPED'))) T");

			break;

		case SQL_SERVER:
			query.append(" SELECT AVG(DATEDIFF (Millisecond, start_time, end_time)) avg FROM");
			query.append(" (SELECT T1.JOB_EXECUTION_ID, T1.JOB_INSTANCE_ID, T1.START_TIME, T1.END_TIME");
			query.append(" FROM BATCH_JOB_EXECUTION T1 ");
			query.append(" INNER JOIN BATCH_JOB_INSTANCE T2 on T1.JOB_INSTANCE_ID = T2.JOB_INSTANCE_ID");
			query.append(" WHERE T2.JOB_NAME = ? AND T1.JOB_INSTANCE_ID NOT IN (SELECT JOB_INSTANCE_ID");
			query.append(" FROM BATCH_JOB_EXECUTION WHERE STATUS IN ('FAILED', 'STARTED', 'STOPPED'))) T");

			break;

		default:
			query.append(
					" SELECT AVG(DATEDIFF ('millisecond', START_TIME::timestamp - END_TIME::timestamp))  avg FROM");
			query.append(" (SELECT * FROM BATCH_JOB_EXECUTION T1 ");
			query.append(" INNER JOIN BATCH_JOB_INSTANCE T2 on T1.JOB_INSTANCE_ID = T2.JOB_INSTANCE_ID");
			query.append(" WHERE T2.JOB_NAME = ? AND T1.JOB_INSTANCE_ID NOT IN (SELECT JOB_INSTANCE_ID");
			query.append(" FROM BATCH_JOB_EXECUTION WHERE STATUS IN ('FAILED', 'STARTED', 'STOPPED'))) T");

		}

		return query.toString();
	}
}