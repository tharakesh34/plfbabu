package com.pennant.pff.upload.model;

import java.io.Serializable;

import javax.sql.DataSource;

import org.zkoss.zul.Window;

import com.pennant.pff.upload.service.UploadService;
import com.pennant.webui.util.pagging.PagedListWrapper;

public class FieUploadDTO implements Serializable {
	private static final long serialVersionUID = 8811585738219395608L;

	private Window window;
	private PagedListWrapper<FileUploadHeader> listWrapper;
	private transient DataSource dataSource;
	private transient UploadService<FileUploadHeader> service;
	private FileUploadHeader header;
	private String configName;
	private String stage;
	private long userId;

	public FieUploadDTO() {
		super();
	}

	public Window getWindow() {
		return window;
	}

	public void setWindow(Window window) {
		this.window = window;
	}

	public PagedListWrapper<FileUploadHeader> getListWrapper() {
		return listWrapper;
	}

	public void setListWrapper(PagedListWrapper<FileUploadHeader> listWrapper) {
		this.listWrapper = listWrapper;
	}

	public DataSource getDataSource() {
		return dataSource;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public UploadService<FileUploadHeader> getService() {
		return service;
	}

	public void setService(UploadService<FileUploadHeader> uploadService) {
		this.service = uploadService;
	}

	public FileUploadHeader getHeader() {
		return header;
	}

	public void setHeader(FileUploadHeader header) {
		this.header = header;
	}

	public String getConfigName() {
		return configName;
	}

	public void setConfigName(String configName) {
		this.configName = configName;
	}

	public String getStage() {
		return stage;
	}

	public void setStage(String stage) {
		this.stage = stage;
	}

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

}
