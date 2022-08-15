package com.pennanttech.pff.upload;

import java.io.File;

import org.zkoss.util.media.Media;

public interface FileImport<T> {

	T read(File file, String contentType);

	void validate(T header);

	File create(Media media);

	void backUp(File file);

	void delete(File file);

	boolean isFileExists(String fileName);
}
