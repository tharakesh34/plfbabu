package com.pennant.backend.model.systemmasters;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import com.pennant.backend.model.Entity;
import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

public class ProjectUnits extends AbstractWorkflowEntity implements Entity {

	private static final long serialVersionUID = 1L;
	//Project Units
	private long unitId = Long.MIN_VALUE;
	private long projectId;
	private String unitType;
	private String tower;
	private String floorNumber;
	private String unitNumber;
	private int unitArea;
	private int rate;
	private BigDecimal price = BigDecimal.ZERO;
	private BigDecimal otherCharges = BigDecimal.ZERO;
	private BigDecimal totalPrice = BigDecimal.ZERO;
	private BigDecimal unitRpsf = BigDecimal.ZERO;
	private BigDecimal unitPlotArea = BigDecimal.ZERO;
	private BigDecimal unitSuperBuiltUp = BigDecimal.ZERO;
	private String unitAreaConsidered;
	private BigDecimal carpetArea = BigDecimal.ZERO;
	private BigDecimal unitBuiltUpArea = BigDecimal.ZERO;
	private String rateConsidered;
	private BigDecimal rateAsPerCarpetArea = BigDecimal.ZERO;
	private BigDecimal rateAsPerBuiltUpArea = BigDecimal.ZERO;
	private BigDecimal rateAsPerSuperBuiltUpArea = BigDecimal.ZERO;
	private BigDecimal rateAsPerBranchAPF = BigDecimal.ZERO;
	private BigDecimal rateAsPerCostSheet = BigDecimal.ZERO;
	private BigDecimal floorRiseCharges = BigDecimal.ZERO;
	private BigDecimal openCarParkingCharges = BigDecimal.ZERO;
	private BigDecimal closedCarParkingCharges = BigDecimal.ZERO;
	private BigDecimal gst = BigDecimal.ZERO;
	private String remarks;

	private LoggedInUser userDetails;
	private boolean newRecord = false;
	private ProjectUnits befImage;

	public ProjectUnits() {
		super();
	}

	public ProjectUnits(long id) {
		super();
		this.setId(id);
	}

	public boolean isNewRecord() {
		return newRecord;
	}

	public void setNewRecord(boolean newRecord) {
		this.newRecord = newRecord;
	}

	@Override
	public boolean isNew() {
		return isNewRecord();
	}

	@Override
	public long getId() {
		return unitId;
	}

	@Override
	public void setId(long id) {
		this.unitId = id;
	}

	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<String>();
		return excludeFields;
	}

	public long getProjectId() {
		return projectId;
	}

	public void setProjectId(long projectId) {
		this.projectId = projectId;
	}

	public String getUnitType() {
		return unitType;
	}

	public void setUnitType(String unitType) {
		this.unitType = unitType;
	}

	public String getTower() {
		return tower;
	}

	public void setTower(String tower) {
		this.tower = tower;
	}

	public String getFloorNumber() {
		return floorNumber;
	}

	public void setFloorNumber(String floorNumber) {
		this.floorNumber = floorNumber;
	}

	public String getUnitNumber() {
		return unitNumber;
	}

	public void setUnitNumber(String unitNumber) {
		this.unitNumber = unitNumber;
	}

	public int getUnitArea() {
		return unitArea;
	}

	public void setUnitArea(int unitArea) {
		this.unitArea = unitArea;
	}

	public int getRate() {
		return rate;
	}

	public void setRate(int rate) {
		this.rate = rate;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public BigDecimal getOtherCharges() {
		return otherCharges;
	}

	public void setOtherCharges(BigDecimal otherCharges) {
		this.otherCharges = otherCharges;
	}

	public BigDecimal getTotalPrice() {
		return totalPrice;
	}

	public void setTotalPrice(BigDecimal totalPrice) {
		this.totalPrice = totalPrice;
	}

	public BigDecimal getUnitRpsf() {
		return unitRpsf;
	}

	public void setUnitRpsf(BigDecimal unitRpsf) {
		this.unitRpsf = unitRpsf;
	}

	public BigDecimal getUnitPlotArea() {
		return unitPlotArea;
	}

	public void setUnitPlotArea(BigDecimal unitPlotArea) {
		this.unitPlotArea = unitPlotArea;
	}

	public BigDecimal getUnitSuperBuiltUp() {
		return unitSuperBuiltUp;
	}

	public void setUnitSuperBuiltUp(BigDecimal unitSuperBuiltUp) {
		this.unitSuperBuiltUp = unitSuperBuiltUp;
	}

	public String getUnitAreaConsidered() {
		return unitAreaConsidered;
	}

	public void setUnitAreaConsidered(String unitAreaConsidered) {
		this.unitAreaConsidered = unitAreaConsidered;
	}

	public BigDecimal getCarpetArea() {
		return carpetArea;
	}

	public void setCarpetArea(BigDecimal carpetArea) {
		this.carpetArea = carpetArea;
	}

	public BigDecimal getUnitBuiltUpArea() {
		return unitBuiltUpArea;
	}

	public void setUnitBuiltUpArea(BigDecimal unitBuiltUpArea) {
		this.unitBuiltUpArea = unitBuiltUpArea;
	}

	public String getRateConsidered() {
		return rateConsidered;
	}

	public void setRateConsidered(String rateConsidered) {
		this.rateConsidered = rateConsidered;
	}

	public BigDecimal getRateAsPerCarpetArea() {
		return rateAsPerCarpetArea;
	}

	public void setRateAsPerCarpetArea(BigDecimal rateAsPerCarpetArea) {
		this.rateAsPerCarpetArea = rateAsPerCarpetArea;
	}

	public BigDecimal getRateAsPerBuiltUpArea() {
		return rateAsPerBuiltUpArea;
	}

	public void setRateAsPerBuiltUpArea(BigDecimal rateAsPerBuiltUpArea) {
		this.rateAsPerBuiltUpArea = rateAsPerBuiltUpArea;
	}

	public BigDecimal getRateAsPerSuperBuiltUpArea() {
		return rateAsPerSuperBuiltUpArea;
	}

	public void setRateAsPerSuperBuiltUpArea(BigDecimal rateAsPerSuperBuiltUpArea) {
		this.rateAsPerSuperBuiltUpArea = rateAsPerSuperBuiltUpArea;
	}

	public BigDecimal getRateAsPerBranchAPF() {
		return rateAsPerBranchAPF;
	}

	public void setRateAsPerBranchAPF(BigDecimal rateAsPerBranchAPF) {
		this.rateAsPerBranchAPF = rateAsPerBranchAPF;
	}

	public BigDecimal getRateAsPerCostSheet() {
		return rateAsPerCostSheet;
	}

	public void setRateAsPerCostSheet(BigDecimal rateAsPerCostSheet) {
		this.rateAsPerCostSheet = rateAsPerCostSheet;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public BigDecimal getFloorRiseCharges() {
		return floorRiseCharges;
	}

	public void setFloorRiseCharges(BigDecimal floorRiseCharges) {
		this.floorRiseCharges = floorRiseCharges;
	}

	public BigDecimal getOpenCarParkingCharges() {
		return openCarParkingCharges;
	}

	public void setOpenCarParkingCharges(BigDecimal openCarParkingCharges) {
		this.openCarParkingCharges = openCarParkingCharges;
	}

	public BigDecimal getClosedCarParkingCharges() {
		return closedCarParkingCharges;
	}

	public void setClosedCarParkingCharges(BigDecimal closedCarParkingCharges) {
		this.closedCarParkingCharges = closedCarParkingCharges;
	}

	public BigDecimal getGst() {
		return gst;
	}

	public void setGst(BigDecimal gst) {
		this.gst = gst;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}

	public ProjectUnits getBefImage() {
		return befImage;
	}

	public void setBefImage(ProjectUnits befImage) {
		this.befImage = befImage;
	}

	public long getUnitId() {
		return unitId;
	}

	public void setUnitId(long unitId) {
		this.unitId = unitId;
	}
}
