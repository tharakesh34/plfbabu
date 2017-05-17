package com.pennanttech.framework.web.util;

import java.io.File;

import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.WebApp;
import org.zkoss.zk.ui.util.WebAppInit;

import com.pennant.app.util.PathUtil;
import com.pennanttech.framework.web.util.resource.LabelLocator;
import com.pennanttech.pff.core.util.RegexPatternUtil;

public class ResourceLoader implements WebAppInit {
	private final static Logger	logger	= Logger.getLogger(ResourceLoader.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.zkoss.zk.ui.util.WebAppInit#init(org.zkoss.zk.ui.WebApp)
	 */
	@Override
	public void init(WebApp webApp) throws Exception {
		logger.info("Loading resources / locale-dependent labels...");
		String filePath = PathUtil.getPath(PathUtil.CONFIG) + File.separator + "i3-label.properties";
		Labels.register(new LabelLocator(LabelLocator.ResourceType.File, filePath));

		loadRegex();
	}

	private void loadRegex() {
		logger.info("Loading resources / regular expresions...");
		String defaultFilePath = ResourceLoader.class.getClassLoader().getResource("regex.properties").getPath();
		loadCustomRegex(defaultFilePath);
	}

	public static void loadCustomRegex(String defaultFilePath) {
		String customFilePath = PathUtil.getPath(PathUtil.CONFIG) + File.separator + "regex.properties";

		RegexPatternUtil.load(defaultFilePath);

		if (new File(customFilePath).exists()) {
			RegexPatternUtil.load(customFilePath);
		}
	}
}
