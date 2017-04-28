package com.pennant;

import org.apache.commons.lang.StringUtils;
import org.zkoss.zk.ui.Component;
import org.zkoss.zul.Cell;
import org.zkoss.zul.Column;
import org.zkoss.zul.Columns;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Image;
import org.zkoss.zul.Label;
import org.zkoss.zul.Panel;
import org.zkoss.zul.Panelchildren;
import org.zkoss.zul.Progressmeter;
import org.zkoss.zul.Row;
import org.zkoss.zul.Rows;

import com.pennant.app.util.DateUtility;
import com.pennant.backend.model.ExecutionStatus;
import com.pennant.backend.util.PennantConstants;

public class ProcessExecution extends Panel {
	private static final long serialVersionUID = -1279954047180887070L;

	private String imgSrc;
	private ExecutionStatus process;
	private String progress;

	Panelchildren panelchildren = null;
	Grid grid = null;
	Columns columns = null;

	Rows rows = null;
	Row row1 = null;
	Row row2 = null;
	Row row3 = null;
	Row row4 = null;
	Row row5 = null;
	Row row6 = null;
	Row row7 = null;
	Row row8 = null;
	Progressmeter meter = null;
	Hbox hbox1 = null;
	Hbox hBox6 = null;
	Image icon = null;
	int percentage = 0;

	private String runningStyle = "color:#FF4500;font-weight: bold; font-size:12px;";

	public ProcessExecution() {
		this.setCollapsible(true);
		this.setStyle("padding:2px;font-color:red");
	}

	public void render() {
		panelchildren = (Panelchildren) getChildren(this, "panelchildren");
		if (panelchildren == null) {
			panelchildren = new Panelchildren();
			panelchildren.setId("panelchildren" + this.getId());
			panelchildren.setStyle("overflow:auto;font-color:red");
			this.appendChild(panelchildren);
		}

		grid = (Grid) getChildren(panelchildren, "grid");
		if (grid == null) {
			grid = new Grid();
			grid.setId("grid" + this.getId());
			grid.setSclass("GridLayoutNoBorder");
			panelchildren.appendChild(grid);
		}

		columns = (Columns) getChildren(grid, "columns");
		if (columns == null) {
			columns = new Columns();
			columns.setId("columns" + this.getId());
			columns.appendChild(new Column("", null, "43%"));
			columns.appendChild(new Column("", null, "5%"));
			columns.appendChild(new Column("", null, "45%"));
			columns.setParent(grid);
		}

		rows = (Rows) getChildren(columns, "rows");
		if (rows == null) {
			rows = new Rows();
			rows.setId("rows" + this.getId());
			rows.setParent(grid);
		}

		row1 = (Row) getChildren(rows, "row1");
		if (row1 == null) {
			row1 = new Row();
			row1.setId("row1" + this.getId());
			row1.setParent(rows);
		}

		hbox1 = (Hbox) getChildren(row1, "hbox1");
		if (hbox1 == null) {
			hbox1 = new Hbox();
			hbox1.setId("hbox1" + this.getId());
			Cell row1Cell1 = new Cell();
			row1Cell1.setColspan(3);
			row1Cell1.appendChild(hbox1);
			row1.appendChild(row1Cell1);
		}

		icon = (Image) getChildren(hbox1, "icon");
		if (icon == null) {
			icon = new Image();
			icon.setId("icon" + this.getId());
			icon.setSrc(imgSrc);
			hbox1.appendChild(icon);
		}

		Label label1 = (Label) getChildren(hbox1, "label1");
		if (label1 == null) {
			label1 = new Label();
			label1.setId("label1" + this.getId());
			hbox1.appendChild(label1);
		}

		label1.setValue(DateUtility.timeBetween(getProcess().getEndTime(),
				getProcess().getStartTime()));

		if (!"STARTING".equals(process.getStatus())) {
			row1.setVisible(true);
		} else {
			row1.setVisible(false);
		}

		row2 = (Row) getChildren(rows, "row2");
		if (row2 == null) {
			row2 = new Row();
			row2.setId("row2" + this.getId());
			row2.appendChild(new Label("Total Records"));
			row2.appendChild(new Label(":"));
			rows.appendChild(row2);
		}

		Label label2 = (Label) getChildren(row2, "label2");
		if (label2 == null) {
			label2 = new Label();
			label2.setId("label2" + this.getId());
			row2.appendChild(label2);
		}

		label2.setValue(String.valueOf(getProcess().getActualCount()));

		row3 = (Row) getChildren(rows, "row3");
		if (row3 == null) {
			row3 = new Row();
			row3.setId("row3" + this.getId());
			row3.appendChild(new Label("Processed"));
			row3.appendChild(new Label(":"));
			rows.appendChild(row3);
		}

		Label label3 = (Label) getChildren(row3, "label3");
		if (label3 == null) {
			label3 = new Label();
			label3.setId("label3" + this.getId());
			row3.appendChild(label3);
		}

		label3.setValue(String.valueOf(getProcess().getProcessedCount()));

		//=======
		
		row7 = (Row) getChildren(rows, "row7");
		if (row7 == null) {
			row7 = new Row();
			row7.setId("row7" + this.getId());
			row7.appendChild(new Label("Waiting"));
			row7.appendChild(new Label(":"));
			rows.appendChild(row7);
		}

		Label label7 = (Label) getChildren(row7, "label7");
		if (label7 == null) {
			label7 = new Label();
			label7.setId("label7" + this.getId());
			row7.appendChild(label7);
		}

		label7.setValue(StringUtils.trimToEmpty(getProcess().getWait()));	
		row7.setVisible(false);
		
		//
		
		row4 = (Row) getChildren(rows, "row4");
		if (row4 == null) {
			row4 = new Row();
			row4.setId("row4" + this.getId());
			row4.appendChild(new Label("Start Time"));
			row4.appendChild(new Label(":"));
			rows.appendChild(row4);
		}

		Label label4 = (Label) getChildren(row4, "label4");
		if (label4 == null) {
			label4 = new Label();
			label4.setId("label4" + this.getId());
			row4.appendChild(label4);
		}

		label4.setValue(DateUtility.formatUtilDate(getProcess().getStartTime(),
				PennantConstants.DBTimeFormat));

		row5 = (Row) getChildren(rows, "row5");
		if (row5 == null) {
			row5 = new Row();
			row5.setId("row5" + this.getId());
			row5.appendChild(new Label("End Time"));
			row5.appendChild(new Label(":"));
			rows.appendChild(row5);
		}

		Label label5 = (Label) getChildren(row5, "label5");
		if (label5 == null) {
			label5 = new Label();
			label5.setId("label5" + this.getId());
			row5.appendChild(label5);
		}

		label5.setValue(DateUtility.formatUtilDate(getProcess().getEndTime(),
				PennantConstants.DBTimeFormat));

		if ("EXECUTING".equals(process.getStatus())) {
			row5.setVisible(false);
		} else {
			row5.setVisible(true);
		}

		if (getProcess().getActualCount() > 0
				&& getProcess().getProcessedCount() > 0) {
			percentage = getProcess().getProcessedCount() * 100
					/ getProcess().getActualCount();
		}

		row6 = (Row) getChildren(rows, "row6");
		if (row6 == null) {
			row6 = new Row();
			row6.setId("row6" + this.getId());
			rows.appendChild(row6);
		}

		Label label6 = (Label) getChildren(rows, "label6");
		if (label6 == null) {
			label6 = new Label();
			label6.setId("label6" + this.getId());
		}

		if ("EXECUTING".equals(process.getStatus())) {
			progress = String.valueOf(percentage + "% Completed");
			Cell cell = (Cell) getChildren(row6, "cell");
			if (hBox6 == null) {
				hBox6 = new Hbox();
				cell = new Cell();
				cell.setId("cell" + this.getId());
				cell.setColspan(3);
				cell.setAlign("center");
				meter = new Progressmeter();
				meter.setWidth("160px");
				label6.setStyle(runningStyle);
				hBox6.appendChild(meter);
				hBox6.appendChild(label6);
				cell.appendChild(hBox6);
				row6.appendChild(cell);
			}
			label6.setValue(progress);
			meter.setValue(percentage);
		} else {
			row6.getChildren().clear();
			row6.appendChild(new Label("Status"));
			row6.appendChild(new Label(":"));
			label6.setValue(process.getStatus());
			row6.appendChild(label6);
		}

		if ("EXECUTING".equals(process.getStatus())) {
			label1.setStyle(runningStyle);
			label3.setStyle(runningStyle);
		} else if ("COMPLETED".equals(process.getStatus())) {
			label6.setValue(process.getStatus());
			label1.setStyle("");
			label3.setStyle("");
			label6.setStyle("");
		} else {
			label6.setStyle("color:#FF0000;");
			label1.setStyle("");
			label3.setStyle("");
		}

		if (!"STARTING".equals(process.getStatus())) {
			row6.setVisible(true);
		}

		if ("EXECUTING".equals(process.getStatus()) && StringUtils.isNotBlank(process.getWait())) {
			row2.setVisible(false);
			row3.setVisible(false);
			row7.setVisible(true);
		}else{
			row2.setVisible(true);
			row3.setVisible(true);
			row7.setVisible(false);
		}
		
		if (process.getInfo() == null) {
			this.setTooltiptext(this.getTitle());
		} else {
			this.setTooltiptext(process.getInfo());
		}

	}

	public void setProcess(ExecutionStatus process) {
		this.process = process;
	}

	public ExecutionStatus getProcess() {
		if (process == null) {
			process = new ExecutionStatus();
		}
		return process;
	}

	public String getImgSrc() {
		return imgSrc;
	}

	public void setImgSrc(String imgSrc) {
		this.imgSrc = imgSrc;
	}

	public void setProgress(String progress) {
		this.progress = progress;
	}

	public String getProgress() {
		return " " + progress;
	}

	private Component getChildren(Component component, String componentName) {
		return component.getFellowIfAny(componentName + this.getId());
	}

}
