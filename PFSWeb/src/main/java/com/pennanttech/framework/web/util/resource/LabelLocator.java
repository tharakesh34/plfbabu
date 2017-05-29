package com.pennanttech.framework.web.util.resource;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Locale;

import org.apache.log4j.Logger;
import org.zkoss.util.resource.LabelLocator2;
import org.zkoss.util.resource.Labels;

/**
 * A {@link LabelLocator} used to locate extra resource (from file or database table) for {@link Labels} into an input
 * stream.
 */
public class LabelLocator implements LabelLocator2 {
	private final Logger	logger	= Logger.getLogger(LabelLocator.class);

	public enum ResourceType {
		File, Database;
	}

	private ResourceType	resourceType;
	private String			location;

	public LabelLocator(ResourceType resourceType, String location) {
		this.resourceType = resourceType;
		this.location = location;
	}

	@Override
	public InputStream locate(Locale locale) {
		if (resourceType == ResourceType.File) {
			return loadFile();
		} else {
			return loadTable();
		}

	}

	private InputStream loadFile() {
		InputStream inputStream = null;
		File i3Labels = new File(location);

		try {
			inputStream = new FileInputStream(i3Labels);
		} catch (FileNotFoundException e) {
			logger.warn("Custom i3-label.properties file not availabe.");
		}

		return inputStream;
	}

	private InputStream loadTable() {

		return null;
	}

	@Override
	public String getCharset() {
		return "UTF-8";
	}

}
