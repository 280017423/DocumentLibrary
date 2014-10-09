package com.zsq.document.activity;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.zsq.document.R;
import com.zsq.document.adapter.FolderAdapter;
import com.zsq.document.util.ConstantSet;
import com.zsq.document.util.FileUtil;
import com.zsq.document.util.OpenFileUtil;
import com.zsq.document.util.StringUtil;
import com.zsq.document.widget.LongPressTextView;

public class MainActivity extends ActivityBase implements OnClickListener, OnItemClickListener {
	private static final long WAIT_TIME = 2000;
	private static final int REQUEST_CODE = 1;
	private static final int DIALOG_HIDE_DISPLAY = 1;
	private GridView mGvRootFolder;
	private ArrayList<File> mFileList;
	private FolderAdapter mFilAdapter;
	private File mResDir;
	private File mCurrentFile;
	private LinearLayout mLlBack;
	private long mTouchTime;
	private LongPressTextView mTitleTextView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initVariables();
		initView();
		setListener();
		getFileList();
	}

	private void initVariables() {
		FileUtil.initDir(this);
		mFileList = new ArrayList<File>();
		mFilAdapter = new FolderAdapter(this, mFileList);
		mResDir = FileUtil.getResDir(this);
		mCurrentFile = mResDir;
	}

	private void initView() {
		mGvRootFolder = (GridView) findViewById(R.id.gv_root_folder);
		mGvRootFolder.setAdapter(mFilAdapter);
		mTitleTextView = (LongPressTextView) findViewById(R.id.title_with_back_title_btn_mid);
		mTitleTextView.setText(R.string.app_name);
		mLlBack = (LinearLayout) findViewById(R.id.title_with_back_title_btn_left);
		TextView mTvBack = (TextView) findViewById(R.id.tv_title_with_back_left);
		mTvBack.setText(R.string.title_back_text);
		mTvBack.setBackgroundResource(R.drawable.btn_back_bg);
	}

	private void setListener() {
		mGvRootFolder.setOnItemClickListener(this);
		mLlBack.setOnClickListener(this);
		mTitleTextView.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View view) {
				showHideDialog(DIALOG_HIDE_DISPLAY);
				return true;
			}
		});
	}

	private void getFileList() {
		if (null == mCurrentFile) {
			return;
		}
		if (null != mResDir && mCurrentFile.getAbsolutePath().equals(mResDir.getAbsolutePath())) {
			mLlBack.setVisibility(View.GONE);
		} else {
			mLlBack.setVisibility(View.VISIBLE);
		}
		File[] files = mCurrentFile.listFiles(new FileFilter() {

			@Override
			public boolean accept(File file) {
				if (null != file && !StringUtil.isNullOrEmpty(file.getName()) && !file.getName().startsWith(".")) {
					return true;
				}
				return false;
			}
		});
		if (files == null) {
			return;
		} else {
			mFileList.clear();
			mFileList.addAll(Arrays.asList(files));
			Collections.sort(mFileList, new Comparator<File>() {
				@Override
				public int compare(File o1, File o2) {
					if (o1.isDirectory() && o2.isFile()) {
						return -1;
					}
					if (o1.isFile() && o2.isDirectory()) {
						return 1;
					}
					return o1.getName().compareTo(o2.getName());
				}
			});
		}
		mFilAdapter.notifyDataSetChanged();
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		File tempFile = (File) parent.getAdapter().getItem(position);
		if (null == tempFile || !tempFile.exists()) {
			return;
		}
		if (tempFile.isDirectory()) {
			mCurrentFile = tempFile;
			getFileList();
		} else if (tempFile.isFile()) {
			Intent intent = OpenFileUtil.openFile(tempFile, this);
			if (null != intent) {
				startActivity(intent);
			} else {
				Toast.makeText(this, getString(R.string.no_app_found), Toast.LENGTH_LONG).show();
			}
		}

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.title_with_back_title_btn_left:
				if (null != mCurrentFile) {
					mCurrentFile = mCurrentFile.getParentFile();
					getFileList();
				}
				break;
			default:
				break;
		}
	}

	private void showHideDialog(int id) {
		String message = getString(R.string.title_hide_message);
		if (FileUtil.isHiddenDir(this)) {
			message = getString(R.string.title_display_message);
		}
		createDialogBuilder(this, getString(R.string.button_text_tips), message, getString(R.string.cancel),
				getString(R.string.ensure)).create(id).show();
	}

	@Override
	public void onNegativeBtnClick(int id, DialogInterface dialog, int which) {
		if (DIALOG_HIDE_DISPLAY == id) {
			if (FileUtil.isHiddenDir(this)) {
				FileUtil.rename(MainActivity.this, mResDir, ConstantSet.DEFAULT_PATH);
			} else {
				FileUtil.rename(MainActivity.this, mResDir, ConstantSet.HIDDEN_PATH);
			}
			mResDir = FileUtil.getResDir(this);
			mCurrentFile = mResDir;
			getFileList();
			if (FileUtil.isHiddenDir(this)) {
				Toast.makeText(MainActivity.this, getString(R.string.toast_hide), Toast.LENGTH_LONG).show();
			} else {
				Toast.makeText(MainActivity.this, getString(R.string.toast_display), Toast.LENGTH_LONG).show();
			}
		}
		super.onNegativeBtnClick(id, dialog, which);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (RESULT_OK == resultCode && REQUEST_CODE == requestCode) {
			mResDir = FileUtil.getResDir(this);
			mCurrentFile = mResDir;
			getFileList();
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onBackPressed() {
		if (null != mCurrentFile && null != mResDir
				&& !mCurrentFile.getAbsolutePath().equals(mResDir.getAbsolutePath())) {
			mCurrentFile = mCurrentFile.getParentFile();
			getFileList();
			return;
		} else {
			long currentTime = System.currentTimeMillis();
			if ((currentTime - mTouchTime) >= WAIT_TIME) {
				Toast.makeText(this, getString(R.string.once_press_quit), Toast.LENGTH_SHORT).show();
				mTouchTime = currentTime;
				return;
			} else {
				finish();
			}
		}
		super.onBackPressed();
	}

}
