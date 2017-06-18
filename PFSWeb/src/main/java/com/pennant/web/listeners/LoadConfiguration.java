package com.pennant.web.listeners;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;

import javax.imageio.ImageIO;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

import com.pennant.app.util.PathUtil;

public class LoadConfiguration implements ServletContextListener {
	private static final Logger logger = Logger.getLogger(LoadConfiguration.class);

	public String envVariable;
	public final String log4j = "/log4j.xml";

	public static String CONTEXT_PATH = null;

	public File clientLogo = null;

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		CONTEXT_PATH = sce.getServletContext().getRealPath(File.separator);
		writeOrgLogo();
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {

	}

	public LoadConfiguration() throws IOException {
		try {
			// Head less mode enabled for Jasper Reports
			System.setProperty("java.awt.headless", "true");
			System.setProperty("line.separator","\r\n");
			
			AccessController
					.doPrivileged(new PrivilegedExceptionAction<Object>() {

						public Object run() throws IOException {
							loadEnv();

							loadLog4j();

							loadOrgLogo(false);

							return null;
						}

					});
		} catch (PrivilegedActionException e) {
			logger.error("Exception: ", e);
			throw (IOException) e.getException();
		}
	}

	private void loadEnv() throws IOException {
		System.out.println("loading environment variable..");
		String envVariable = System.getenv("APP_ROOT_PATH");
		File configPath = null;

		if (envVariable == null) {
			throw new IOException("Environment variable is not set for "
					+ PathUtil.ENV_NAME);
		} else {
			configPath = new File(envVariable);
		}

		if (!configPath.isDirectory()) {
			throw new IOException(configPath + " is not a directory.");
		}

		if (System.getenv("APP_ENCRYPTION_PASSWORD") == null) {
			throw new IOException("Environment variable is not set for "
					+ "APP_ENCRYPTION_PASSWORD");
		}

		this.envVariable = envVariable;
		PathUtil.setRootPath(envVariable);
	}

	private void loadLog4j() throws IOException {
		System.out.println("loading " + log4j + " file..");
		File log4jXml = new File(PathUtil.getPath(PathUtil.CONFIG) + log4j);

		if (!log4jXml.exists()) {
			throw new FileNotFoundException(log4jXml.getName()
					+ " not exists in  " + envVariable);
		}

		DOMConfigurator.configure(PathUtil.getPath(PathUtil.CONFIG) + log4j);
	}

	private void loadOrgLogo(boolean write) throws IOException {
		System.out.println("loading Organization logo from " + envVariable
				+ "/images");
		BufferedImage image = null;
		File out = null;

		clientLogo = new File(PathUtil.getPath(PathUtil.REPORTS_IMAGE_CLIENT));

		if (!clientLogo.exists()) {
			throw new FileNotFoundException("Image not exists - " + clientLogo.getPath());
		}

		if (write) {
			image = ImageIO.read(clientLogo);
			out = new File(CONTEXT_PATH + "/images/icons/"
					+ clientLogo.getName());
			ImageIO.write(image, "png", out);
		}
	}

	private void writeOrgLogo() {
		System.out.println("writing Organization logo [ "
				+ clientLogo.getName() + " ] into " + CONTEXT_PATH
				+ "/images/icons");
		BufferedImage image = null;
		File out = null;

		try {
			image = ImageIO.read(clientLogo);
			out = new File(CONTEXT_PATH + "/images/icons/"
					+ clientLogo.getName());
			ImageIO.write(image, "png", out);
		} catch (IOException e) {
			logger.error("Exception: ", e);
		} finally {
			image = null;
			out = null;

		}

	}
}
