package com.zsq.document.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DecimalFormat;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

/**
 * 
 * Description the class 文件帮助类
 * 
 * @version 1.0
 * @author zou.sq
 * 
 */
public class FileUtil {
	public static final int BUFSIZE = 256;
	public static final int COUNT = 320;
	private static final String TAG = "FileUtils";
	private static final long SIZE_KB = 1024;
	private static final long SIZE_MB = 1048576;
	private static final long SIZE_GB = 1073741824;

	/**
	 * @Description 检查本地资源目录是否存在
	 * @param context
	 *            上下文对象
	 * @return boolean 是否存在
	 * @Author Administrator
	 * @Date 2014年7月31日 下午4:07:39
	 */
	public static boolean initDir(Context context) {
		if (android.os.Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			File normalDir = new File(Environment.getExternalStorageDirectory(), ConstantSet.DEFAULT_PATH);
			File hiddenDir = new File(Environment.getExternalStorageDirectory(), ConstantSet.HIDDEN_PATH);
			if (normalDir.exists() || hiddenDir.exists()) {
				return true;
			} else {
				normalDir.mkdirs();
			}
		}
		return false;
	}

	/**
	 * 
	 * @Description 获取本地资源目录
	 * @param context
	 *            上下文对象
	 * @return File 本地资源目录
	 * @Author Administrator
	 * @Date 2014年7月31日 下午4:07:39
	 * 
	 */
	public static File getResDir(Context context) {
		File downloadFile = null;
		if (android.os.Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			if (isHiddenDir(context)) {
				downloadFile = new File(Environment.getExternalStorageDirectory(), ConstantSet.HIDDEN_PATH);
			} else {
				downloadFile = new File(Environment.getExternalStorageDirectory(), ConstantSet.DEFAULT_PATH);
			}
		}
		return downloadFile;
	}

	/**
	 * 判断是不是隐藏目录
	 * 
	 * @param context
	 *            上下文对象
	 * @return true 是隐藏目录
	 */
	public static boolean isHiddenDir(Context context) {
		File normalDir = new File(Environment.getExternalStorageDirectory(), ConstantSet.DEFAULT_PATH);
		File hiddenDir = new File(Environment.getExternalStorageDirectory(), ConstantSet.HIDDEN_PATH);
		return !normalDir.exists() && hiddenDir.exists();
	}

	/**
	 * 判断指定的文件是否存在
	 * 
	 * @param filePath
	 *            文件路径
	 * @return 是否存在
	 */
	public static boolean isFileExist(String filePath) {
		File file = new File(filePath);
		return file.exists();
	}

	/**
	 * 准备文件夹，文件夹若不存在，则创建
	 * 
	 * @param filePath
	 *            文件路径
	 */
	public static void prepareFile(String filePath) {
		File file = new File(filePath);
		if (!file.exists()) {
			file.mkdirs();
		}
	}

	/**
	 * 删除指定的文件或目录
	 * 
	 * @param filePath
	 *            文件路径
	 */
	public static void delete(String filePath) {
		if (filePath == null) {
			return;
		}
		try {
			File file = new File(filePath);
			delete(file);
		} catch (Exception e) {
			Log.e(TAG, e.toString());
		}
	}

	/**
	 * 删除指定的文件或目录
	 * 
	 * @param file
	 *            文件
	 */
	public static void delete(File file) {
		if (file == null || !file.exists()) {
			return;
		}
		if (file.isDirectory()) {
			deleteDirRecursive(file);
		} else {
			file.delete();
		}
	}

	/**
	 * 递归删除目录
	 * 
	 * @param dir
	 *            文件路径
	 */
	public static void deleteDirRecursive(File dir) {
		if (dir == null || !dir.exists() || !dir.isDirectory()) {
			return;
		}
		File[] files = dir.listFiles();
		if (files == null) {
			return;
		}
		for (File f : files) {
			if (f.isFile()) {
				f.delete();
			} else {
				deleteDirRecursive(f);
			}
		}
		dir.delete();
	}

	/**
	 * 取得文件大小
	 * 
	 * @param f
	 *            文件
	 * @return long 大小
	 * 
	 */
	public long getFileSizes(File f) {
		long s = 0;
		try {
			if (f.exists()) {
				FileInputStream fis = new FileInputStream(f);
				s = fis.available();
			} else {
				f.createNewFile();
			}
		} catch (Exception e) {
			Log.w(TAG, e.toString());
		}
		return s;
	}

	/**
	 * 递归取得文件夹大小
	 * 
	 * @param filedir
	 *            文件
	 * @return 大小
	 */
	public static long getFileSize(File filedir) {
		long size = 0;
		if (null == filedir) {
			return size;
		}
		File[] files = filedir.listFiles();

		try {
			for (File f : files) {
				if (f.isDirectory()) {
					size += getFileSize(f);
				} else {
					FileInputStream fis = new FileInputStream(f);
					size += fis.available();
					fis.close();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return size;

	}

	/**
	 * 转换文件大小
	 * 
	 * @param fileS
	 *            大小
	 * @return 转换后的文件大小
	 */
	public static String formatFileSize(long fileS) {
		DecimalFormat df = new DecimalFormat("#.0");
		String fileSizeString = "";
		if (fileS == 0) {
			fileSizeString = "0" + "KB";
		} else if (fileS < SIZE_KB) {
			fileSizeString = df.format((double) fileS) + "KB";
		} else if (fileS < SIZE_MB) {
			fileSizeString = df.format((double) fileS / SIZE_KB) + "KB";
		} else if (fileS < SIZE_GB) {
			fileSizeString = df.format((double) fileS / SIZE_MB) + "M";
		} else {
			fileSizeString = df.format((double) fileS / SIZE_GB) + "G";
		}
		return fileSizeString;
	}

	/**
	 * 判断SD卡是否已经准备好
	 * 
	 * @return 是否有SDCARD
	 */
	public static boolean isSDCardReady() {
		return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
	}

	/**
	 * 文件流拷贝到文件
	 * 
	 * @param in
	 *            输入流
	 * @param outFile
	 *            输出文件
	 * @return 操作状态
	 */
	public static int copyStreamToFile(InputStream in, String outFile) {
		if (isFileExist(outFile)) {
			// 文件已经存在；
			return -2;
		}
		try {
			OutputStream fosto = new FileOutputStream(outFile);
			byte bt[] = new byte[1024];
			int c;
			while ((c = in.read(bt)) > 0) {
				fosto.write(bt, 0, c);
			}
			in.close();
			fosto.close();
			return 0;

		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		} finally {
			try {
				if (in != null) {
					in.close();
				}
			} catch (IOException e) {
				Log.e(TAG, e.toString());
			}
		}
	}

	/**
	 * 得到sdcard路径
	 * 
	 * @return
	 */
	public static String getExtPath() {
		String path = "";
		if (isSDCardReady()) {
			path = Environment.getExternalStorageDirectory().getPath();
		}
		return path;
	}

	/**
	 * 获取文件扩展名
	 * 
	 * @param file
	 * @return
	 */
	public static String getFileExtension(File file) {
		if (null == file || !file.exists()) {
			return "";
		}
		String fileName = file.getName();
		if (fileName.lastIndexOf(".") != -1 && fileName.lastIndexOf(".") != 0) {
			return fileName.substring(fileName.lastIndexOf(".") + 1);
		} else {
			return "";
		}
	}

	public static boolean rename(Context context, File file, String newName) {
		if (file == null || StringUtil.isNullOrEmpty(newName)) {
			return false;
		}
		String path = file.getParentFile() + File.separator + newName;
		if (!file.isDirectory()) {
			String extensionName = getExtensionName(file.getName());
			if (StringUtil.isNullOrEmpty(extensionName)) {
				extensionName = "";
			} else {
				extensionName = "." + extensionName;
			}
			path = file.getParentFile() + File.separator + newName + extensionName;
		}
		return file.renameTo(new File(path));
	}

	/*
	 * Java文件操作 获取文件扩展名
	 * 
	 * Created on: 2011-8-2 Author: blueeagle
	 */
	public static String getExtensionName(String filename) {
		if (!StringUtil.isNullOrEmpty(filename)) {
			int dot = filename.lastIndexOf('.');
			if ((dot > -1) && (dot < (filename.length() - 1))) {
				return filename.substring(dot + 1);
			}
		}
		return "";
	}

	/*
	 * Java文件操作 获取不带扩展名的文件名
	 * 
	 * Created on: 2011-8-2 Author: blueeagle
	 */
	public static String getFileNameNoEx(String filename) {
		if (!StringUtil.isNullOrEmpty(filename)) {
			int dot = filename.lastIndexOf('.');
			if ((dot > -1) && (dot < (filename.length()))) {
				return filename.substring(0, dot);
			}
		}
		return filename;
	}

}
