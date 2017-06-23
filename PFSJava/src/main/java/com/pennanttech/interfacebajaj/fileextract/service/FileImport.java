package com.pennanttech.interfacebajaj.fileextract.service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

import javax.sql.DataSource;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.util.Assert;
import org.zkoss.io.Files;
import org.zkoss.util.media.Media;
import org.zkoss.util.resource.Labels;

import com.northconcepts.datapipeline.core.Record;
import com.pennant.app.util.DateUtility;

public class FileImport {
	private static final Logger logger = LoggerFactory.getLogger(FileImport.class);

	private String contentType = "text/plain";
	private File directory;
	private Media media;
	private File file;
	protected String batchType;
	private String fileExtension;
	private String fileNamePrefix;
	protected long userId;
	private int totalRecords;
	private boolean backUp = true;
	/**
	 * Include the success records in the BatchLog, if BatchFileImport.isLogStatus() is true, default false
	 */
	private boolean logStatus;
	protected NamedParameterJdbcTemplate jdbcTemplate;

	protected String batchReference;

	public FileImport(DataSource datsSource) {
		setDataSource(datsSource);
	}

	public void load(boolean isClientLoaction) throws Exception {
		Assert.notNull(getFile(), Labels.getLabel("FIELD_NOT_BLANK", new String[] { "File Name" }));

		if (isClientLoaction) {
			writeToFile();
		}
		validateFileData();
	}

	private void writeToFile() throws Exception {
		BufferedWriter writer = null;
		File filepath = new File(getFile().getParent());
		if (!filepath.exists()) {
			filepath.mkdirs();
		}
		
		writer = new BufferedWriter(new FileWriter(getFile()));
		Files.copy(writer, getMedia().getReaderData());
		writer.close();
	}
	
	private void validateFileData() throws Exception {
		try {
			BufferedReader br = null;
			String line = null;
			try {
				totalRecords = 0;
				br = new BufferedReader(new FileReader(getFile()));
				while ((line = br.readLine()) != null) {
					if (StringUtils.trimToNull(line) == null) {
						break;
					}
					totalRecords++;
				}
			} catch (Exception e) {
				throw new Exception("The file format is not valid");
			} finally {
				if (br != null)
					br.close();
			}
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}
		logger.debug("Leaving");
	}


	protected void backUpFile() throws IOException {
		File backup = new File(file.getParent() + "/BackUp");

		if (!backup.exists()) {
			backup.mkdir();
		}
		FileUtils.copyFile(file, new File(backup.getPath() + "/" + file.getName()));
		if (file.exists()) {
			if (!file.delete()) {
				file.deleteOnExit();
			}
		}
	}

	public void setMedia(Media media) throws Exception {

		if (!StringUtils.equals(media.getContentType(), getContentType())) {
			throw new Exception(Labels.getLabel("invalid_file"));
		}
		File file = validateFile(media.getName());
		this.media = media;
		if (isEmpty()) {
			throw new Exception(Labels.getLabel("empty_file"));
		}
		setFile(file);
	}

	private File validateFile(String fileName) throws Exception {
		Assert.notNull(getDirectory(), "Default directory should not be blank");
		File file = new File(getDirectory().getPath() + "/" + fileName);

		Assert.notNull(fileExtension, "File extension cannot be blank");
		if (!(file.getName().substring(file.getName().lastIndexOf('.')).equalsIgnoreCase(fileExtension))) {
			throw new Exception(Labels.getLabel("invalid_file"));
		}

		File oldFile = new File(getDirectory().getPath() + "/BackUp/" + file.getName());
		if (oldFile != null && oldFile.exists()) {
			throw new Exception(Labels.getLabel("duplicate_file"));
		}

		return file;
	}

	public void setDefaultDirectory(String filePath) throws Exception {
		File file = new File(filePath);

		if (!file.exists()) {
			file.mkdir();
		}

		setDirectory(file);

		File backup = new File(getDirectory().getPath() + "/BackUp/");

		if (backup != null && !backup.exists()) {
			backup.mkdir();
		}
	}

	protected String getStringValue(Record record, int index) {
		String value = "";

		if (record.getField(index) != null) {
			value = StringUtils.trimToEmpty(record.getField(index).getValueAsString());
		}
		return value;
	}

	protected Date getDateValue(Record record, int index) {
		return DateUtility.getDate(getStringValue(record, index), "yyyyMMdd");
	}

	private void setDataSource(DataSource dataSource) {
		this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public File getDirectory() {
		return directory;
	}

	public void setDirectory(File directory) {
		this.directory = directory;
	}

	public Media getMedia() {
		return media;
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public String getBatchType() {
		return batchType;
	}

	public void setBatchType(String batchType) {
		this.batchType = batchType;
	}

	public String getFileExtension() {
		return fileExtension;
	}

	public void setFileExtension(String fileExtension) {
		this.fileExtension = fileExtension;
	}

	public String getFileNamePrefix() {
		return fileNamePrefix;
	}

	public void setFileNamePrefix(String fileNamePrefix) {
		this.fileNamePrefix = fileNamePrefix;
	}

	public int getTotalRecords() {
		return totalRecords;
	}

	public void setTotalRecords(int totalRecords) {
		this.totalRecords = totalRecords;
	}

	public boolean isEmpty() {
		if (getMedia().getReaderData().equals(null)) {
			return true;
		}

		return false;
	}

	public String getName() {
		if (getMedia() == null) {
			return "";
		} else {
			return getMedia().getName();
		}
	}

	public boolean isLogStatus() {
		return logStatus;
	}

	public void setLogStatus(boolean logStatus) {
		this.logStatus = logStatus;
	}

	public boolean isBackUp() {
		return backUp;
	}

	public void setBackUp(boolean backUp) {
		this.backUp = backUp;
	}

	protected String getFieldValue(String record, int startPos, int length) {
		return StringUtils.trimToEmpty(record.substring(startPos, startPos + length));
	}

	protected Date getDateValue(String record, int startPos, int endPos) {
		if (!getFieldValue(record, startPos, endPos).isEmpty()) {
			return DateUtility.getDate(getFieldValue(record, startPos, endPos), "yyyymmdd");
		}
		return null;
	}
}
