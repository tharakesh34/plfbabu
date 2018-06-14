package com.pennanttech.niyogin.mandate;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import com.pennant.backend.model.documentdetails.DocumentDetails;
import com.pennant.backend.model.mandate.Mandate;
import com.pennanttech.dataengine.DataEngineExport;
import com.pennanttech.dataengine.model.Configuration;
import com.pennanttech.dataengine.model.DataEngineStatus;
import com.pennanttech.dataengine.model.EventProperties;
import com.pennanttech.dataengine.util.EncryptionUtil;
import com.pennanttech.pennapps.core.ftp.SftpClient;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.jdbc.search.Search;
import com.pennanttech.pennapps.jdbc.search.SearchProcessor;
import com.pennanttech.pff.external.DocumentManagementService;
import com.pennanttech.pff.external.mandate.AbstractMandateProcess;

public class MandateProcess extends AbstractMandateProcess{
	private static final Logger			logger	= Logger.getLogger(MandateProcess.class);
	private static final String MANDATE_POSITIVE_REPSONE = "ACTIVE";
	private static final String SEPARATOR_DOT = ".";

	@Autowired
	private SearchProcessor searchProcessor;
	
	@Autowired(required = false)
	private DocumentManagementService	documentManagementService;

	@Override
	public DataEngineStatus genetare(DataEngineExport dataEngine, String userName, Map<String, Object> filterMap, 
			Map<String, Object> parameterMap) throws Exception {
		DataEngineStatus exportStatus=  super.genetare(dataEngine, userName, filterMap, parameterMap);

		String status = exportStatus.getStatus();

		if (StringUtils.equals(status, "S")) {
			BeanUtils.copyProperties(MandateProcess.MANDATES_IMPORT, status);

			String fileName = exportStatus.getFileName();
			Configuration config = dataEngine.getConfigurationByName(exportStatus.getName());

			String accessKey = null;
			String secretKey = null;
			String bucketName = null;
			EventProperties properties = config.getEventProperties().get("COPY_TO_SFTP");
			if(properties != null) {
				accessKey = EncryptionUtil.decrypt(properties.getAccessKey());
				secretKey = EncryptionUtil.decrypt(properties.getSecretKey());
				bucketName = properties.getBucketName();
			} else {
				Search search = new Search(EventProperties.class);
				search.addTabelName("data_engine_event_properties");
				search.addField("bucket_name");
				search.addField("access_key");
				search.addField("secret_key");
				search.addField("host_Name");
				search.addField("port");
				search.addField("private_key");
				search.addFilterIn("config_id", config.getId());

				List<EventProperties> list = searchProcessor.getResults(search);
				if(list != null) {
					for(EventProperties prop: list) {
						properties = prop;
						accessKey = prop.getAccessKey();
						secretKey = prop.getSecretKey();
						bucketName = prop.getBucketName();
					}
				}
			}

			File file = new File(config.getUploadPath().concat(File.separator).concat(String.valueOf(exportStatus.getId())));
			if (file.exists()) {
				FileUtils.forceDelete(file);
			}
			file.mkdirs();

			File sourceFile = new File(config.getUploadPath().concat(File.separator).concat(fileName));
			File destFile = new File(file.getPath().concat(File.separator).concat(fileName));

			FileUtils.copyFile(sourceFile, destFile);

			@SuppressWarnings("unchecked")
			List<Long> mandates = (List<Long>)filterMap.get("MandateId");

			// Fetch Mandate Document reference by mandateId
			Search search = new Search(Mandate.class);
			search.addTabelName("Mandates");
			search.addField("ExternalRef");
			search.addField("DocumentName");
			search.addField("MandateId");
			search.addFilterIn("MandateId", mandates);

			List<Mandate> list =   searchProcessor.getResults(search);

			for (Mandate mandate : list) {
				// Fetch document from DMS by using document reference
				String reference = String.valueOf(mandate.getMandateID());
				DocumentDetails detail = documentManagementService.getExternalDocument(mandate.getExternalRef(), reference);
				BufferedImage bufferedImage = null;
				String formate[] = null;
		        try {
		        	URL url = new URL(detail.getDocUri());
		        	bufferedImage = ImageIO.read(url);
					if(StringUtils.contains(mandate.getDocumentName(), SEPARATOR_DOT)) {
						formate = StringUtils.split(mandate.getDocumentName(), "//"+SEPARATOR_DOT);
					}
					ImageIO.write(bufferedImage, formate[1], new File(file.getPath().concat(File.separator).concat(reference).concat("."+formate[1])));
		        } catch (IOException e) {
		           logger.error("Exception", e);
		        }
			}
			
			if (file.isDirectory()) {
				for (File item : file.listFiles()) {
					getSFTPClient(accessKey, secretKey, properties).upload(item, bucketName);
				}
			} else {
				getSFTPClient(accessKey, secretKey, properties).upload(file, bucketName);
			}
		}

		return exportStatus;
	}

	private SftpClient getSFTPClient(String accessKey, String secretKey, EventProperties properties) {
		SftpClient client = null;
		if (properties.getPrivateKey() != null) {
			client = new SftpClient(properties.getHostName(), Integer.parseInt(properties.getPort()), accessKey, secretKey, properties.getPrivateKey());
		} else {
			client = new SftpClient(properties.getHostName(), Integer.parseInt(properties.getPort()), accessKey, secretKey);
		}
		return client;
	}

	@Override
	protected void validateMandate(Mandate respMandate, Mandate mandate, StringBuilder remarks) {
		if (!StringUtils.equals(mandate.getCustCIF(), respMandate.getCustCIF())) {
			if (remarks.length() > 0) {
				remarks.append(", ");
			}
			remarks.append("Customer Code");
		}

		if (!StringUtils.equals(mandate.getAccNumber(), respMandate.getAccNumber())) {
			if (remarks.length() > 0) {
				remarks.append(", ");
			}
			remarks.append("Account No.");
		}
	}

	@Override
	protected void updateMandates(Mandate respmandate) {
		MapSqlParameterSource paramMap = new MapSqlParameterSource();

		StringBuilder sql = new StringBuilder();

		sql.append("Update Mandates");
		sql.append(" Set MANDATEREF = :MANDATEREF, STATUS = :STATUS, REASON = :REASON");
		sql.append("  Where MANDATEID = :MANDATEID AND ORGREFERENCE = :FINREFERENCE AND STATUS = :AC");

		paramMap.addValue("MANDATEID", respmandate.getMandateID());

		if (!MANDATE_POSITIVE_REPSONE.equalsIgnoreCase(respmandate.getStatus())) {
			paramMap.addValue("STATUS", "REJECTED");
			paramMap.addValue("AC", "AC");
			paramMap.addValue("MANDATEREF", null);
			paramMap.addValue("FINREFERENCE", respmandate.getFinReference());
		} else {
			paramMap.addValue("STATUS", "APPROVED");
			paramMap.addValue("MANDATEREF", respmandate.getMandateRef());
			paramMap.addValue("AC", "AC");
			paramMap.addValue("FINREFERENCE", respmandate.getFinReference());
		}

		paramMap.addValue("REASON", respmandate.getReason());

		this.namedJdbcTemplate.update(sql.toString(), paramMap);
	}
	
	
	@Override
	protected void logMandateHistory(Mandate respmandate, long requestId) {
		logger.debug(Literal.ENTERING);
		MapSqlParameterSource paramMap = new MapSqlParameterSource();
		
		StringBuilder sql =new StringBuilder("Insert Into MandatesStatus");
		sql.append(" (mandateID, status, reason, changeDate, fileID)");
		sql.append(" Values(:mandateID, :STATUS, :REASON, :changeDate,:fileID)");
		
		paramMap.addValue("mandateID", respmandate.getMandateID());
		
		if (!MANDATE_POSITIVE_REPSONE.equals(respmandate.getStatus())) {
			paramMap.addValue("STATUS", "REJECTED");
		} else {
			paramMap.addValue("STATUS", "APPROVED");
		}
		paramMap.addValue("REASON", respmandate.getReason());
		paramMap.addValue("changeDate", getAppDate());
		paramMap.addValue("fileID", requestId);
		
		this.namedJdbcTemplate.update(sql.toString(), paramMap);
		logger.debug(Literal.LEAVING);
	}
}
