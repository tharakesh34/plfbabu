package com.pennanttech.pff.upload.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.OfficeXmlFileException;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.zkoss.util.media.Media;
import org.zkoss.util.resource.Labels;

import com.northconcepts.datapipeline.core.Record;
import com.northconcepts.datapipeline.csv.CSVReader;
import com.pennant.app.util.PathUtil;
import com.pennant.backend.model.finance.manual.schedule.ManualScheduleDetail;
import com.pennant.backend.model.finance.manual.schedule.ManualScheduleHeader;
import com.pennant.backend.service.finance.manual.schedule.ManualScheduleService;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.UploadConstants;
import com.pennanttech.pennapps.core.App;
import com.pennanttech.pennapps.core.AppException;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.core.util.DateUtil.DateFormat;
import com.pennanttech.pff.constants.FinServiceEvent;
import com.pennanttech.pff.upload.FileImport;

public class ManualScheduleFileImport implements FileImport<ManualScheduleHeader> {
	private static final Logger logger = LogManager.getLogger(ManualScheduleFileImport.class);

	private ManualScheduleService manualScheduleService;

	@Override
	public ManualScheduleHeader read(File file, String contentType) {
		if ((file.getName().toLowerCase().endsWith(".csv"))
				|| ("text/plain".equalsIgnoreCase(contentType) || "text/csv".equalsIgnoreCase(contentType))) {
			return readCSVData(file);
		} else if ("application/octet-stream".equals(contentType) || "application/vnd.ms-excel".equals(contentType)
				|| "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet".equals(contentType)
				|| "application/kset".equals(contentType)) {
			return readExcelData(file);
		} else {
			throw new AppException(String.format("Invalid file format, file content type : %s", contentType));
		}
	}

	@Override
	public void validate(ManualScheduleHeader header) {
		Date prvSchdDate = header.getPrvSchdDate();
		Date maturityDate = header.getMaturityDate();

		String moduleDefiner = header.getModuleDefiner();
		Integer noOfInstallments = header.getNumberOfTerms();
		List<ManualScheduleDetail> list = header.getManualSchedules();

		String dateFormat = DateFormat.LONG_DATE.getPattern();
		String format = DateUtil.format(prvSchdDate, dateFormat);
		prvSchdDate = DateUtil.parse(format, dateFormat);

		boolean interestSchdFlag = false;

		int indexCnt = list.size();
		ManualScheduleDetail lastSchedule = list.get(indexCnt - 1);

		for (ManualScheduleDetail msd : list) {
			Date schDate = msd.getSchDate();
			BigDecimal principalSchd = msd.getPrincipalSchd();
			boolean pftOnSchDate = msd.isPftOnSchDate();
			boolean rvwOnSchDate = msd.isRvwOnSchDate();

			StringBuilder reason = new StringBuilder();

			// RePayment Date
			if (schDate == null) {
				reason.append(" Repayment Date is mandatory, it should be in").append(dateFormat).append(" format.");
				reason.append(" |");
			}

			if (schDate != null) {
				if (schDate.compareTo(prvSchdDate) <= 0) {
					reason.append(" Repayment Date  should be greater than ");
					reason.append(DateUtil.format(prvSchdDate, dateFormat));
					reason.append(" |");
				}
			}

			// Principle Amount
			if (principalSchd == null) {
				reason.append(" Principle Amount is mandatory. ");
				reason.append(" |");
			}

			if (BigDecimal.ZERO.compareTo(principalSchd) == 0 && (!pftOnSchDate && !rvwOnSchDate)) {
				reason.append(" Principle will not be Zero when Interst Scheduled Flag and Rate Review Flag are N ");
				reason.append(" |");
			}

			if (lastSchedule.equals(msd)) {
				if (schDate != null) {
					if (maturityDate != null && schDate.compareTo(maturityDate) > 0) {
						reason.append(" Repayment Date  should be less than ");
						reason.append(DateUtil.format(maturityDate, dateFormat));
						reason.append(" |");
					}
				}
			}

			if (StringUtils.isEmpty(reason.toString())) {
				msd.setStatus(UploadConstants.UPLOAD_STATUS_SUCCESS);
				prvSchdDate = msd.getSchDate();
			} else {
				msd.setStatus(UploadConstants.UPLOAD_STATUS_FAIL);
			}

			if (StringUtils.isNotEmpty(reason.toString())) {
				msd.setReason(reason.deleteCharAt(reason.length() - 1).toString());
			}
			interestSchdFlag = msd.isPftOnSchDate();
		}

		if (!interestSchdFlag) {
			throw new AppException(Labels.getLabel("PFTSCHD_FLAG_LASTROW"));
		}

		if (CollectionUtils.isNotEmpty(list) && indexCnt > 1) {
			ManualScheduleDetail prvDetails = list.get(indexCnt - 2);
			ManualScheduleDetail curDetails = list.get(indexCnt - 1);
			if (curDetails.getPrincipalSchd().compareTo(BigDecimal.ZERO) == 0 && prvDetails.isPftOnSchDate()) {
				throw new AppException(Labels.getLabel("MANUAL_SCHD_LASTRCD"));
			}
		}

		if (StringUtils.equals(moduleDefiner, FinServiceEvent.RECALCULATE)) {
			if (noOfInstallments.compareTo(indexCnt) != 0)
				throw new AppException(Labels.getLabel("NOOFINSTL_RECALCULATE"));
		}

	}

	@Override
	public File create(Media media) {
		String fileName = media.getName();
		String contentType = media.getContentType();

		if (isFileExists(fileName)) {
			throw new AppException(String.format("File name {%s} already Exist.", fileName));
		}

		if (!fileName.contains(Labels.getLabel("label_ManualSchedule.label"))) {
			throw new AppException(String.format(" Invalid File name {%s} :: ex. ManualSchedule", fileName));
		}

		String filePath = App.getResourcePath(PathUtil.FILE_UPLOADS_PATH, PathUtil.MANUAL_SCHEDULES);
		File parent = new File(filePath);

		if (!parent.exists()) {
			parent.mkdirs();
		}

		File file = new File(parent.getPath().concat(File.separator).concat(fileName));

		delete(file);

		try {
			if ("application/octet-stream".equals(contentType) || "application/vnd.ms-excel".equals(contentType)
					|| "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet".equals(contentType)
					|| "application/kset".equals(contentType)) {
				byte[] data = media.getByteData();
				if (file.exists()) {
					file.deleteOnExit();
				}
				FileUtils.writeByteArrayToFile(file, data);
			} else {
				throw new AppException(String.format("Invalid file format, file content type : %s", contentType));
			}
		} catch (IOException e) {
			logger.error(Literal.EXCEPTION, e);

			String errorMessage = "Unable to create/write the %s file in the  %s location, Please contact system administrator";
			throw new AppException(String.format(errorMessage, file.getName(), file.getParent()));
		}

		return file;
	}

	@Override
	public void backUp(File file) {
		if (file != null) {
			File backupFile = new File(file.getParent().concat(File.separator).concat("BackUp"));

			if (!backupFile.exists()) {
				backupFile.mkdir();
			}

			try {
				FileUtils.copyFile(file, new File(backupFile.getPath().concat(File.separator).concat(file.getName())));
			} catch (IOException e) {
				logger.error(Literal.EXCEPTION, e);

				String errorMessage = "Unable to backup the %s file in the  %s location, Please contact system administrator";
				throw new AppException(String.format(errorMessage, backupFile.getName(), backupFile.getParent()));
			}

			delete(file);
		}
	}

	@Override
	public void delete(File file) {
		if (file.exists()) {
			file.deleteOnExit();
		}
	}

	@Override
	public boolean isFileExists(String fileName) {
		return this.manualScheduleService.isFileNameExist(fileName);
	}

	private ManualScheduleHeader readCSVData(File file) {
		ManualScheduleHeader header = new ManualScheduleHeader();
		List<ManualScheduleDetail> schedules = new ArrayList<>();

		String delimiter = ",";
		int rowNum = 0;

		CSVReader reader = null;

		try {
			reader = new CSVReader(file);

			reader.setFieldNamesInFirstRow(true);
			reader.setStartingRow(0);

			if (String.valueOf(delimiter.charAt(0)).equals("	")) {
				reader.setTrimFields(false);
			} else {
				reader.setTrimFields(true);
			}

			reader.setFieldSeparator(delimiter.charAt(0));
			reader.open();

			Record record = null;

			while ((record = reader.read()) != null) {
				ManualScheduleDetail schedule = new ManualScheduleDetail();
				rowNum++;

				schedule.setSchDate(getSchuleDate(record.getField(0).getValue(), rowNum));
				schedule.setPrincipalSchd(getPriSchdAmount(record.getField(1).getValue(), rowNum));
				schedule.setPftOnSchDate(getIntSchdFlag(record.getField(2).getValue(), rowNum));
				schedule.setRvwOnSchDate(getRateRvwFlag(record.getField(3).getValue(), rowNum));

				schedules.add(schedule);
			}
		} catch (AppException e) {
			logger.error(Literal.EXCEPTION, e);
			throw e;
		} finally {
			if (reader != null) {
				reader.close();
			}
		}

		header.setManualSchedules(schedules);
		return header;
	}

	private ManualScheduleHeader readExcelData(File file) {
		ManualScheduleHeader header = new ManualScheduleHeader();
		List<ManualScheduleDetail> schedules = new ArrayList<>();

		Workbook workBook = null;
		try (FileInputStream fis = new FileInputStream(file)) {
			if (file.getName().toLowerCase().endsWith(".xls")) {
				try {
					workBook = new HSSFWorkbook(fis);
				} catch (OfficeXmlFileException e) {
					workBook = new XSSFWorkbook(fis);
				}
			} else if (file.getName().toLowerCase().endsWith(".xlsx")) {
				workBook = new XSSFWorkbook(fis);
			}
		} catch (Exception e) {
			throw new AppException(Labels.getLabel("label_ValidatedUploadFile"));
		}

		Sheet myExcelSheet = workBook.getSheetAt(0);

		if (myExcelSheet == null) {
			throw new AppException(Labels.getLabel("label_ValidatedUploadFile"));
		}

		if (!myExcelSheet.getSheetName().contains(Labels.getLabel("label_ManualSchedule.label"))) {
			throw new AppException(Labels.getLabel("label_ValidatedUploadFile"));
		}

		int rowCount = myExcelSheet.getPhysicalNumberOfRows();
		if (rowCount <= 1) {
			throw new AppException(Labels.getLabel("label_ValidatedUploadFile"));
		}

		Iterator<Row> rows = myExcelSheet.iterator();
		int rowNum = 0;
		while (rows.hasNext()) {
			Row row = rows.next();

			if (row.getRowNum() == 0) {
				if (row.getLastCellNum() != 4) {
					throw new AppException(Labels.getLabel("label_ValidatedUploadFile"));
				}
				continue;
			}

			ManualScheduleDetail schedule = new ManualScheduleDetail();
			rowNum++;

			schedule.setSchDate(getSchuleDate(row.getCell(0), rowNum));
			schedule.setPrincipalSchd(getPriSchdAmount(row.getCell(1), rowNum));
			schedule.setPftOnSchDate(getIntSchdFlag(row.getCell(2), rowNum));
			schedule.setRvwOnSchDate(getRateRvwFlag(row.getCell(3), rowNum));

			schedules.add(schedule);
		}

		if (workBook != null) {
			try {
				workBook.close();
			} catch (IOException e) {
				//
			}
		}

		header.setManualSchedules(schedules);
		return header;
	}

	private Date getSchuleDate(Object schDate, int rowNum) {
		Date schdDate = null;

		if (schDate == null || StringUtils.isBlank(schDate.toString())) {
			throw new AppException("Repayment Date cannot be blank.");
		}

		try {
			schdDate = DateUtil.parse(schDate.toString(), "dd-MM-yyyy");
		} catch (IllegalArgumentException e) {

			try {
				schdDate = DateUtil.parse(schDate.toString(), DateFormat.LONG_DATE);
			} catch (IllegalArgumentException ex) {
				throw new AppException(String.format("Repayment Date is inValid in %d schedule", rowNum));
			}
		}

		return schdDate;
	}

	private BigDecimal getPriSchdAmount(Object amount, int rowNum) {
		if (amount == null || StringUtils.isBlank(amount.toString())) {
			return BigDecimal.ZERO;
		}

		BigDecimal priSchd = BigDecimal.ZERO;

		try {
			priSchd = PennantApplicationUtil.unFormateAmount(amount.toString(), 2);
			if (priSchd.compareTo(BigDecimal.ZERO) < 0) {
				throw new AppException(String.format("Principal Amount is inValid in %d schedule", rowNum));
			}

		} catch (NumberFormatException e) {
			throw new AppException(String.format("Principal Amount is inValid in %d schedule", rowNum));
		}

		return priSchd;
	}

	private boolean getIntSchdFlag(Object schdFlag, int rowNum) {
		if (schdFlag == null || StringUtils.isBlank(schdFlag.toString())) {
			throw new AppException("Interest Scheduled Flag cannot be blank.");
		}

		String flag = schdFlag.toString();

		if (!("Y".equals(flag) || "N".equals(flag))) {
			throw new AppException(String.format("Interest Scheduled Flag is inValid in %d schedule", rowNum));
		}

		return flag.equalsIgnoreCase("Y") ? true : false;
	}

	private boolean getRateRvwFlag(Object schdFlag, int rowNum) {
		if (schdFlag == null || StringUtils.isBlank(schdFlag.toString())) {
			throw new AppException("Rate Review Flag cannot be blank.");
		}

		String flag = schdFlag.toString();

		if (!("Y".equals(flag) || "N".equals(flag))) {
			throw new AppException(String.format("Rate Review Flag is inValid in %d schedule", rowNum));
		}

		return flag.equalsIgnoreCase("Y") ? true : false;
	}

	public void setManualScheduleService(ManualScheduleService manualScheduleService) {
		this.manualScheduleService = manualScheduleService;
	}
}
