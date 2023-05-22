package com.pennant.backend.model.smtmasters;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;

public class HolidayDetail implements java.io.Serializable {

	private static final long serialVersionUID = -5603069581319836829L;

	private String holidayCode = null;
	private String holidayCodeDesc = null;
	private BigDecimal holidayYear;
	private String holidayType;
	private Calendar holiday;
	private String holidayDescription;
	private boolean newRecord = false;

	public HolidayDetail() {
	    super();
	}

	public HolidayDetail(String holidayCode, String holidayCodeDesc, BigDecimal holidayYear, String holidayType,
			Calendar holiday, String holidayDescription) {
		this.holidayCode = holidayCode;
		this.holidayCodeDesc = holidayCodeDesc;
		this.holidayYear = holidayYear;
		this.holidayType = holidayType;
		this.holiday = holiday;
		this.holidayDescription = holidayDescription;
	}

	public HolidayDetail(String holidayCode, String holidayCodeDesc, BigDecimal holidayYear, String holidayType,
			int day, String holidayDescription) {
		this.holidayCode = holidayCode;
		this.holidayCodeDesc = holidayCodeDesc;
		this.holidayYear = holidayYear;
		this.holidayType = holidayType;

		this.holiday = Calendar.getInstance();
		holiday.set(holidayYear.intValue(), 00, 01);
		holiday.set(Calendar.DATE, day);

		this.holidayDescription = holidayDescription;
	}

	public HolidayDetail(String holidayCode, String holidayCodeDesc, BigDecimal holidayYear, int day,
			String holidayDescription) {
		this.holidayCode = holidayCode;
		this.holidayCodeDesc = holidayCodeDesc;
		this.holidayYear = holidayYear;

		this.holiday = Calendar.getInstance();
		holiday.set(holidayYear.intValue(), 00, 01);
		holiday.set(Calendar.DATE, day);

		this.holidayDescription = holidayDescription;
	}

	public boolean isNewRecord() {
		return newRecord;
	}

	public void setNewRecord(boolean newRecord) {
		this.newRecord = newRecord;
	}

	public String getHolidayCode() {
		return holidayCode;
	}

	public void setHolidayCode(String holidayCode) {
		this.holidayCode = holidayCode;
	}

	public String getHolidayCodeDesc() {
		return holidayCodeDesc;
	}

	public void setHolidayCodeDesc(String holidayCodeDesc) {
		this.holidayCodeDesc = holidayCodeDesc;
	}

	public BigDecimal getHolidayYear() {
		return holidayYear;
	}

	public void setHolidayYear(BigDecimal holidayYear) {
		this.holidayYear = holidayYear;
	}

	public String getHolidayType() {
		return holidayType;
	}

	public void setHolidayType(String holidayType) {
		this.holidayType = holidayType;
	}

	public Calendar getHoliday() {
		return holiday;
	}

	public Date getHoliDayDate() {
		return holiday.getTime();
	}

	public int getJulionDate() {
		return holiday.get(Calendar.DAY_OF_YEAR);
	}

	public void setHoliday(Calendar holiday) {
		this.holiday = holiday;
	}

	public String getHolidayDescription() {
		return holidayDescription;
	}

	public void setHolidayDescription(String holidayDescription) {
		this.holidayDescription = holidayDescription;
	}
}
