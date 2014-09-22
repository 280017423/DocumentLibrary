package com.zsq.document.listener;

import java.io.File;

public interface IOperationProgressListener {
	void onFinish();

	void onFileChanged(File file);
}