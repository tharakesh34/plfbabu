package com.pennant.eod;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.pennant.backend.model.eodsnapshot.SnapShotCondition;
import com.pennant.backend.model.eodsnapshot.SnapShotConfiguration;
import com.pennanttech.pennapps.core.AppException;
import com.pennanttech.pennapps.core.resource.Literal;

public class SnapShotService extends SnapShotDataExecution {

	private static final Logger logger = LogManager.getLogger(SnapShotService.class);
	private boolean snapCleared = false;

	public boolean intialiseSanpshot() {
		logger.info(Literal.ENTERING);

		customerConfigurations = new ArrayList<SnapShotConfiguration>();
		incDownLoadConfigurations = new ArrayList<SnapShotConfiguration>();

		List<SnapShotConfiguration> snapShotConfigurations = getActiveConfigurationList();

		for (SnapShotConfiguration snapShotConfiguration : snapShotConfigurations) {
			switch (snapShotConfiguration.getType()) {
			case 1:
				customerConfigurations.add(snapShotConfiguration);
				break;
			case 2:
				incDownLoadConfigurations.add(snapShotConfiguration);
				break;

			default:
				break;
			}

			if (!snapCleared) {
				switch (snapShotConfiguration.getClearingType()) {
				case 0:
					break;
				case 1:
					clearData(snapShotConfiguration.getToTable(), false);
					break;
				default:
					clearData(snapShotConfiguration.getToTable(), true);
				}
			}
		}
		logger.info(Literal.LEAVING);

		return true;
	}

	public boolean addCustomerSnapShot(long custID, boolean snapCleared) {
		logger.info(Literal.ENTERING);

		this.snapCleared = snapCleared;
		if (!snapCleared) {
			this.snapCleared = intialiseSanpshot();

		}

		if (!snapCleared) {
			return snapCleared;
		}

		Map<String, Object> param = new HashMap<>();
		param.put("CustomerId", custID);

		for (SnapShotConfiguration configuration : customerConfigurations) {
			Timestamp runDate = new Timestamp(System.currentTimeMillis());

			switch (configuration.getExecutionType()) {
			case 1:
				String columns = StringUtils.join(configuration.getColumns(), ",");
				int recordCount = 0;

				for (SnapShotCondition condition : configuration.getConditions()) {
					String sqlQry = StringUtils.replace(getSqlQry(configuration.getFromSchema(),
							configuration.getFromTable(), configuration.getToTable(), columns, condition), "?",
							" :CustomerId ");
					recordCount = recordCount + generateSnapShotData(sqlQry, param, configuration.getFromTable());
				}

				updateLastRunDate(configuration.getId(), runDate);

				// Logging Information
				StringBuilder buffer = new StringBuilder("Snap Shot Completed For Customer ID ");
				buffer.append(custID);
				buffer.append(" From ");
				buffer.append(configuration.getFromTable());

				buffer.append(" To ");
				buffer.append(configuration.getToTable());

				buffer.append(" Number of Records ");
				buffer.append(recordCount);

				logger.debug(buffer);

				break;

			default:
				logger.error("In valid Execution Type for " + configuration.getFromTable());
				throw new AppException("SNAP001", "In valid Execution Type for " + configuration.getFromTable());
			}

		}

		logger.info(Literal.LEAVING);
		return true;
	}

	public boolean incDownLoad(boolean fullDownLoad) {

		Map<String, Object> param = new HashMap<>();

		for (SnapShotConfiguration configuration : incDownLoadConfigurations) {

			if (fullDownLoad) {
				clearData(configuration.getToTable(), true);
			}

			Timestamp startDate = configuration.getLastRunDate();
			Timestamp endDate = new Timestamp(System.currentTimeMillis());

			switch (configuration.getExecutionType()) {
			case 1:
				String columns = StringUtils.join(configuration.getColumns(), ",");
				int recordCount = 0;

				for (SnapShotCondition condition : configuration.getConditions()) {

					String sqlQry = getSqlQry(configuration.getFromSchema(), configuration.getFromTable(),
							configuration.getToTable(), columns, condition, fullDownLoad);

					if (!fullDownLoad) {
						param.put("ENDDATE", endDate);
						if (startDate != null) {
							param.put("STARTDATE", startDate);
							sqlQry = StringUtils.replace(sqlQry, "{LASTMNTON}",
									"  LASTMNTON >= :STARTDATE AND LASTMNTON <= :ENDDATE ");
						} else {
							sqlQry = StringUtils.replace(sqlQry, "{LASTMNTON}", "  LASTMNTON <= :ENDDATE ");
						}
					}

					recordCount = recordCount + generateSnapShotData(sqlQry, param, configuration.getFromTable());
				}

				updateLastRunDate(configuration.getId(), endDate);

				// Logging Information
				StringBuilder buffer = new StringBuilder("Snap Shot Completed For Table");
				buffer.append(configuration.getFromTable());
				buffer.append(" Number of Records ");
				buffer.append(recordCount);
				logger.debug(buffer);

				break;

			default:
				logger.error("In valid Execution Type for " + configuration.getFromTable());
				throw new AppException("SNAP001", "In valid Execution Type for " + configuration.getFromTable());
			}

		}
		return true;
	}
}
