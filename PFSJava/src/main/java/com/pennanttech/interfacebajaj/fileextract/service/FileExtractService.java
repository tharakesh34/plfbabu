package com.pennanttech.interfacebajaj.fileextract.service;


public interface FileExtractService<T> {

	public T getFileExtract(long userId) throws Exception;

	public void renderPannel(T extractDetails);

}
