/**
 * @Title: DishEmptyAdapter.java
 * @Project DCB
 * @Package com.pdw.dcb.ui.adapter
 * @Description: 沽清列表
 * @author zeng.ww
 * @date 2012-12-10 下午04:37:29
 * @version V1.0
 */
package com.zsq.filemanager.adapter;

import java.io.File;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.zsq.filemanager.R;
import com.zsq.filemanager.util.OpenFileUtil;
import com.zsq.filemanager.util.UIUtil;

/**
 * 文件列表适配器
 * 
 * @author zou.sq
 * @since 2013-03-12 下午04:37:29
 * @version 1.0
 */
public class FolderAdapter extends BaseAdapter {
	private static final int SPACE_VALUE = 10;
	private static final int NUM_COLUMNS = 4;
	private List<File> mFilesList;
	private Context mContext;
	private int mWidth;

	/**
	 * 实例化对象
	 * 
	 * @param context
	 *            上下文
	 * @param dataList
	 *            数据列表
	 */
	public FolderAdapter(Activity context, List<File> dataList) {
		this.mContext = context;
		this.mFilesList = dataList;
		DisplayMetrics metric = new DisplayMetrics();
		context.getWindowManager().getDefaultDisplay().getMetrics(metric);
		mWidth = metric.widthPixels;
	}

	@Override
	public int getCount() {
		if (mFilesList != null && !mFilesList.isEmpty()) {
			return mFilesList.size();
		}
		return 0;
	}

	@Override
	public File getItem(int position) {
		if (mFilesList != null) {
			return mFilesList.get(position);
		}
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		viewHode view = new viewHode();
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(R.layout.view_folder_item, null);
			view.mName = (TextView) convertView.findViewById(R.id.tv_folder_name);
			view.mIcon = (ImageView) convertView.findViewById(R.id.iv_folder_img);
			convertView.setTag(view);
		} else {
			view = (viewHode) convertView.getTag();
		}
		LayoutParams layoutParams = view.mIcon.getLayoutParams();
		layoutParams.width = (mWidth - UIUtil.dip2px(mContext, SPACE_VALUE)
				* (NUM_COLUMNS + 1))
				/ NUM_COLUMNS;
		layoutParams.height = layoutParams.width;
		view.mIcon.setLayoutParams(layoutParams);
		File file = mFilesList.get(position);
		if (null != file) {
			if (file.isDirectory()) {
				view.mIcon.setImageResource(R.drawable.format_folder);
			} else {
				int fileEndingCode = OpenFileUtil.getFileEnding(file, mContext);
				switch (fileEndingCode) {
					case OpenFileUtil.FILE_ENDING_IMAGE:
						view.mIcon.setImageResource(R.drawable.format_picture);
						break;
					case OpenFileUtil.FILE_ENDING_AUDIO:
						view.mIcon.setImageResource(R.drawable.format_music);
						break;
					case OpenFileUtil.FILE_ENDING_VIDEO:
						view.mIcon.setImageResource(R.drawable.format_media);
						break;
					case OpenFileUtil.FILE_ENDING_PACKAGE:
						view.mIcon.setImageResource(R.drawable.format_zip);
						break;
					case OpenFileUtil.FILE_ENDING_WEBTEXT:
						view.mIcon.setImageResource(R.drawable.format_html);
						break;
					case OpenFileUtil.FILE_ENDING_TEXT:
						view.mIcon.setImageResource(R.drawable.format_text);
						break;
					case OpenFileUtil.FILE_ENDING_WORD:
						view.mIcon.setImageResource(R.drawable.format_word);
						break;
					case OpenFileUtil.FILE_ENDING_EXCEL:
						view.mIcon.setImageResource(R.drawable.format_excel);
						break;
					case OpenFileUtil.FILE_ENDING_PPT:
						view.mIcon.setImageResource(R.drawable.format_ppt);
						break;
					case OpenFileUtil.FILE_ENDING_PDF:
						view.mIcon.setImageResource(R.drawable.format_pdf);
						break;
					case OpenFileUtil.FILE_ENDING_APK:
						view.mIcon.setImageResource(R.drawable.format_apk);
						break;
					case OpenFileUtil.FILE_ENDING_CHM:
						view.mIcon.setImageResource(R.drawable.format_chm);
						break;

					default:
						view.mIcon.setImageResource(R.drawable.format_unkown);
						break;
				}
			}
			view.mName.setText(file.getName());
		}
		return convertView;
	}

	class viewHode {
		TextView mName;
		ImageView mIcon;
	}
}
