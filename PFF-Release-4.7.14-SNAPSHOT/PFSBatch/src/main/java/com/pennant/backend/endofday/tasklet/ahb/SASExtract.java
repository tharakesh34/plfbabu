package com.pennant.backend.endofday.tasklet.ahb;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.jdbc.datasource.DataSourceUtils;

import com.pennant.app.util.DateUtility;
import com.pennant.app.util.PathUtil;
import com.pennant.eod.BatchFileUtil;

public class SASExtract implements Tasklet {

	private Logger logger = Logger.getLogger(SASExtract.class);

	private DataSource dataSource;

	public RepeatStatus execute(StepContribution contribution,
			ChunkContext chunkContext) throws Exception {
		Date appDate = DateUtility.getAppDate();

		logger.debug("START: SAS Extract for Value Date: " + appDate);
		
		Connection connection = null;
		ResultSet resultSet = null;
		Statement statement=null;
		
		try {
			connection = DataSourceUtils.doGetConnection(getDataSource());
			statement = connection.createStatement();
			String[] fileNames = { "PFF_PastDue_SAS_view", "PFF_PaymentSchedule_SAS_view", "PFF_PaymentDaily_SAS_view", "PFF_FIN_DTL_SAS_view","PFF_TRAND_DTL_SAS_View","PFF_FIN_LMS_SAS_view" };
			for (String fileName : fileNames) {
			
			resultSet = statement.executeQuery(getQuery(fileName));
	       
	        String txtFile = prepareName(fileName);
	        createFile(resultSet,  PathUtil.getPath(PathUtil.SAS_EXTRACTS_LOCATION)+txtFile, true, BatchFileUtil.DELIMITER);
			}
		}catch (SQLException e) {
			logger.error("Finreference :", e);
			throw e;
		} finally {
			if (resultSet != null) {
				resultSet.close();
			}
			
			if (statement != null) {
				statement.close();
			}
			
		}
		return RepeatStatus.FINISHED;
	}
	
	/**
	 * Creating txt file with header and data
	 * 
	 * @param resultSet
	 * @param filename
	 * @param colomnName
	 * @param charSep
	 * @throws SQLException
	 * @throws IOException
	 */
	private void createFile(ResultSet resultSet, String filename,
			Boolean colomnName, String charSep) throws SQLException,IOException {
		logger.debug("Entering");
		FileWriter cname = null;
		try {

			// WRITE COLOMN NAME
			ResultSetMetaData rsmd = resultSet.getMetaData();
			cname = new FileWriter(filename);
			StringBuilder columnNames = new StringBuilder();
			if (colomnName) {
				for (int i = 1; i <= rsmd.getColumnCount(); i++) {
					columnNames.append(rsmd.getColumnName(i));
					columnNames.append(charSep);

				}
				String tempcolumnName = columnNames.toString();
				String finalcolumnName = tempcolumnName.substring(0,
						tempcolumnName.length() - 1);
				columnNames.append(finalcolumnName);
				cname.append(columnNames);
				cname.flush();
				cname.append(System.getProperty("line.separator"));

			}

			// WRITE DATA
			StringBuilder columnData = new StringBuilder();
			while (resultSet.next()) {
				for (int i = 1; i <= rsmd.getColumnCount(); i++) {
					if (resultSet.getObject(i) != null) {
						String data = resultSet.getObject(i).toString()
								.replaceAll(charSep, "");
						columnData.append(data);
						columnData.append(charSep);
					} else {
						String data = "null";
						columnData.append(data);
						columnData.append(charSep);
					}

				}
				String tempcolumnData = columnData.toString();
				String finalcolumnData = tempcolumnData.substring(0,
						tempcolumnData.length() - 1);
				cname.append(finalcolumnData);
				columnData = new StringBuilder();
				cname.append(System.getProperty("line.separator"));
			}

		} catch (Exception e) {
			logger.warn("Exception: ", e);
			throw e;
		} finally {
			if (cname != null) {
				cname.flush();
				cname.close();
			}
			if (resultSet != null) {
				resultSet.close();
			}
		}
		logger.debug("Leaving");
	}

	/**
	 * Preparing Name for .txt file
	 * 
	 * @param fileName
	 * @return
	 */
	private String prepareName(String fileName) {
		logger.debug("Entering");
		StringBuilder builder = new StringBuilder();
		fileName = fileName.substring(0, fileName.length() - 5);
		builder.append(fileName);
		builder.append("_");
		builder.append(DateUtility.getValueDate(BatchFileUtil.DATE_FORMAT_DMYT));
		builder.append(BatchFileUtil.FILE_EXT);
		logger.debug("Leaving");
		return builder.toString();
	}

	/**
	 * Method for fetch records based on SQL query
	 * 
	 */
	private String getQuery(String tableName) {
		StringBuilder selectQuery = new StringBuilder();
		selectQuery.append("select * from " + tableName);
		return selectQuery.toString();
	}
	

	public DataSource getDataSource() {
		return dataSource;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}
}
