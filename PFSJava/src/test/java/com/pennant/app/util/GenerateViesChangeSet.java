package com.pennant.app.util;

public class GenerateViesChangeSet {
	public String getChangeSet(String tableName, int id, String viewDef) throws Exception {
		StringBuilder builder = new StringBuilder();

		builder.append("<changeSet id=").append("\"").append(id).append("_pre\" ")
				.append("author=\"murthy.y\" dbms=\"postgresql\">");
		builder.append("\n\t<sql>");
		builder.append("\n\t\t\t<![CDATA[");
		builder.append("\n\t\t\t\tselect deps_save_and_drop_dependencies ('plf', '" + tableName + "');");
		builder.append("\n\t\t\t]]>");
		builder.append("\n\t</sql>");
		builder.append("</changeSet>");
		builder.append("<changeSet id=").append("\"").append(id).append("\" ").append("author=\"murthy.y\">");
		builder.append("\n\t<createView viewName=\"" + tableName + "\" replaceIfExists=\"true\">");
		builder.append("\n\t\t\t<![CDATA[");
		builder.append("\n\t\t\t\t").append(viewDef);
		builder.append("\n\t\t\t]]>");
		builder.append("\n\t\t</createView>");
		builder.append("</changeSet>");

		builder.append("<changeSet id=").append("\"").append(id).append("_post\" ")
				.append("author=\"murthy.y\" dbms=\"postgresql\">");
		builder.append("\n\t<sql>");
		builder.append("\n\t\t\t<![CDATA[");
		builder.append("\n\t\t\t\tselect deps_restore_dependencies ('plf', '" + tableName + "');");
		builder.append("\n\t\t\t]]>");
		builder.append("\n\t</sql>");
		builder.append("</changeSet>");
		builder.append("\n");

		return builder.toString();
	}

}
