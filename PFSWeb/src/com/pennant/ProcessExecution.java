package com.pennant;

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

public class ProcessExecution extends Panel{
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
	Hbox hbox1 = null;
	Progressmeter meter = null;
	Hbox meterBox = null;	
	Image icon = null;
	int percentage = 0;
	
	private String runningStyle = "color:#FF4500;font-weight: bold; font-size:12px;";

	public ProcessExecution() {
		this.setCollapsible(true);
		this.setStyle("padding:2px;font-color:red");
	}

	public void render() {
		if(this.getFellowIfAny(getString(this.getId(), "panelchildren")) == null) {
			panelchildren = new Panelchildren();
			panelchildren.setId(getString(this.getId(),"panelchildren"));
			panelchildren.setStyle("overflow:auto;font-color:red");
		}
		
		if (panelchildren.getFellowIfAny(getString(this.getId(), "grid")) == null) {
			grid = new Grid();
			grid.setId(getString(this.getId(), "grid"));
			grid.setSclass("GridLayoutNoBorder");
		}

		if (grid.getFellowIfAny(getString(this.getId(),"columns")) == null) {
			columns = new Columns();
			columns.setId(getString(this.getId(), "columns"));
			columns.appendChild(new Column("", null, "43%"));
			columns.appendChild(new Column("", null, "5%"));
			columns.appendChild(new Column("", null, "45%"));
			grid.appendChild(columns);
		}

		if (grid.getFellowIfAny(getString(this.getId(),"rows")) == null) {
			rows = new Rows();
			rows.setId(getString(this.getId(),"rows"));
			grid.appendChild(rows);
		}

		if(panelchildren.getFellowIfAny(getString(this.getId(), "grid")) == null) {
			panelchildren.appendChild(grid);
		}

		if(this.getFellowIfAny(getString(this.getId(), "panelchildren")) == null) {
			this.appendChild(panelchildren);
		}


		Label label1 = null;
		if(rows.getFellowIfAny(getString(this.getId(), "row1")) == null) {
			row1 = new Row();
			row1.setId(getString(this.getId(), "row1"));
			row1.setSpans("3");
			hbox1  = new Hbox();
			hbox1.setId(getString(this.getId(), "hbox1"));	

			if(hbox1.getFellowIfAny(getString(this.getId(),"icon")) == null){
				icon = new Image();
				icon.setId(getString(this.getId() ,"icon"));		
				icon.setSrc(imgSrc);
				hbox1.appendChild(icon);		
			}

			label1 = new Label();
			label1.setId(getString(this.getId(), "label1"));
			hbox1.appendChild(label1);
			row1.appendChild(hbox1);
			rows.appendChild(row1);
		}

		label1 = (Label)row1.getFellowIfAny(getString(this.getId(), "hbox1")).getFellowIfAny(getString(this.getId(), "label1"));
		label1.setValue(DateUtility.timeBetween(getProcess().getEndTime(), getProcess().getStartTime()));



		if(!"STARTING".equals(process.getStatus())) {
			row1.setVisible(true);
		} else {
			row1.setVisible(false);
		}

		Label label2 = null;
		if(rows.getFellowIfAny(getString(this.getId(), "row2")) == null) {
			row2 = new Row();
			row2.setId(getString(this.getId(), "row2"));
			row2.appendChild(new Label("Total Records"));
			row2.appendChild(new Label(":"));
			label2 = new Label();
			label2.setId(getString(this.getId(), "label2"));
			row2.appendChild(label2);
			rows.appendChild(row2);
		}

		label2 = (Label)row2.getFellowIfAny(getString(this.getId(), "label2"));
		label2.setValue(String.valueOf(getProcess().getActualCount()));

		Label label3 = null;
		if(rows.getFellowIfAny(getString(this.getId(), "row3")) == null) {
			row3 = new Row();
			row3.setId(getString(this.getId(), "row3"));
			row3.appendChild(new Label("Processed"));
			row3.appendChild(new Label(":"));
			label3 = new Label();
			label3.setId(getString(this.getId(), "label3"));
			row3.appendChild(label3);
			rows.appendChild(row3);
		}

		label3 = (Label)row3.getFellowIfAny(getString(this.getId(), "label3"));
		label3.setValue(String.valueOf(getProcess().getProcessedCount()));

		Label label4 = null;
		if(rows.getFellowIfAny(getString(this.getId(), "row4")) == null) {
			row4 = new Row();
			row4.setId(getString(this.getId(), "row4"));
			row4.appendChild(new Label("Start Time"));
			row4.appendChild(new Label(":"));
			label4 = new Label();
			label4.setId(getString(this.getId(), "label4"));
			row4.appendChild(label4);
			rows.appendChild(row4);
		}

		label4 = (Label)row4.getFellowIfAny(getString(this.getId(), "label4"));
		label4.setValue(DateUtility.formatUtilDate(getProcess().getStartTime(), PennantConstants.DBTimeFormat));

		Label label5 = null;
		if(rows.getFellowIfAny(getString(this.getId(), "row5")) == null) {
			row5 = new Row();
			row5.setId(getString(this.getId() ,"row5"));
			row5.appendChild(new Label("End Time"));
			row5.appendChild(new Label(":"));
			label5 = new Label();
			label5.setId(getString(this.getId(), "label5"));
			row5.appendChild(label5);
			rows.appendChild(row5);
		}

		label5 = (Label)row5.getFellowIfAny(getString(this.getId(), "label5"));
		label5.setValue(DateUtility.formatUtilDate(getProcess().getEndTime(), PennantConstants.DBTimeFormat));
		
		if("EXECUTING".equals(process.getStatus())) {
			row5.setVisible(false);
		} else {
			row5.setVisible(true);
		}

		if(getProcess().getActualCount() > 0 && getProcess().getProcessedCount() > 0 ) {
			percentage = getProcess().getProcessedCount() * 100 / getProcess().getActualCount();
		}

		Label label6 = null;
		if(rows.getFellowIfAny(getString(this.getId(),"row6")) == null) {
			row6 = new Row();
			row6.setId(getString(this.getId(),"row6"));
			row6.appendChild(new Label("Status"));
			row6.appendChild(new Label(":"));
			label6 = new Label();
			label6.setId(getString(this.getId(), "label6"));
			row6.appendChild(label6);
			rows.appendChild(row6);
		}

		label6 = (Label)row6.getFellowIfAny(getString(this.getId(), "label6"));
		if("EXECUTING".equals(process.getStatus())) {
			row6.setVisible(true);
			this.progress = getString(String.valueOf(percentage), "% Completed");
			label6.setValue(this.progress);
			label6.setStyle(runningStyle);
			label1.setStyle(runningStyle);
			label3.setStyle(runningStyle);
		} else if("COMPLETED".equals(process.getStatus())){
			label1.setStyle("");
			label3.setStyle("");
			label6.setStyle("");
			label6.setValue(process.getStatus());
		} else {
			label6.setValue(process.getStatus());
			label6.setStyle("color:#FF0000;");
			label1.setStyle("");
			label3.setStyle("");
		}
		
		if(!"STARTING".equals(process.getStatus())) {
			row6.setVisible(true);
		}
		
		
		if(rows.getFellowIfAny(getString(this.getId(), "row7")) == null) {
			row7 = new Row();
			row7.setSpans("3");
			row7.setId(getString(this.getId(), "row7"));
			row7.setAlign("center");
			
			if(row7.getFellowIfAny(getString(this.getId(), "meterBox")) == null) { 
				meterBox = new Hbox();
				meterBox.setId(getString(this.getId(), "meterBox"));
				meterBox.setAlign("center");
			}
			
			if(meterBox.getFellowIfAny(getString(this.getId(), "meter")) == null) { 
				meter = new Progressmeter();
				meter.setId(getString(this.getId(), "meter"));
				meter.setWidth("250px");
				meterBox.appendChild(meter);
			}
			
			row7.appendChild(meterBox);		
			rows.appendChild(row7);
		}
		
		
		if("EXECUTING".equals(process.getStatus())){
			if (percentage <= 100) {
				meter.setValue(percentage);
			}			
			row7.setVisible(true);
		}  else {
			row7.setVisible(false);
		}
		
		if(process.getInfo() == null) {
			this.setTooltiptext(this.getTitle());
		} else {
			this.setTooltiptext(process.getInfo());
		}

	}

	public void setProcess(ExecutionStatus process) {
		this.process = process;
	}

	public ExecutionStatus getProcess() {
		if(process == null) {
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
		return getString(" ", progress);
	}
	
	
	private String getString(String id, String componentName) {
		StringBuilder builder = new StringBuilder();
		builder.append(String.valueOf(id)).append(componentName);
		return builder.toString();
	}

}
