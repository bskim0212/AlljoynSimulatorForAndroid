package com.lge.alljoyn.simulator.activity;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import com.lge.alljoyn.simulator.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;

public class IntroActivity extends Activity {

	private String DB_NAME = "AJDatabase.sqlite";
	private final Context mContext = IntroActivity.this;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_intro);
		
		
		try {
			boolean bResult1 = isCheckDB(); // DB가 있는지?
			if (!bResult1) { // DB가 없으면 복사
				Log.e("test", "database copy");
				copyDB();
			} else {
				Log.e("test", "database exist");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		
		myTimer timer = new myTimer(300, 1);
		timer.start();
	}
	
	private class myTimer extends CountDownTimer{
		public myTimer(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);
			// TODO Auto-generated constructor stub
		}

		@Override
		public void onFinish() {
			// TODO Auto-generated method stub
			
			Intent intent = new Intent(IntroActivity.this, MainActivity.class);
			startActivity(intent);
			finish();
			
		}

		@Override
		public void onTick(long millisUntilFinished) {
			// TODO Auto-generated method stub
			
		}
	}
	
	// DB가 있나 체크하기
		public boolean isCheckDB() {
			String filePath = mContext.getDatabasePath(DB_NAME).getPath();
			File file = new File(filePath);

			if (file.exists()) {
				return true;
			}

			return false;

		}

		// DB를 복사하기
		public void copyDB() {
			AssetManager manager = mContext.getAssets();
			String folderPath = mContext.getDatabasePath(DB_NAME).getPath().replace("/"+DB_NAME, "");
			String filePath = mContext.getDatabasePath(DB_NAME).getPath();
			//Log.e("intro", "folderPath=" + folderPath + " filePath=" + filePath);
			File folder = new File(folderPath);
			File file = new File(filePath);

			FileOutputStream fos = null;
			BufferedOutputStream bos = null;
			try {
				InputStream is = manager.open(DB_NAME);
				BufferedInputStream bis = new BufferedInputStream(is);

				if (folder.exists()) {
				} else {
					folder.mkdirs();
				}

				if (file.exists()) {
					file.delete();
					file.createNewFile();
				}

				fos = new FileOutputStream(file);
				bos = new BufferedOutputStream(fos);
				int read = -1;
				byte[] buffer = new byte[1024];
				while ((read = bis.read(buffer, 0, 1024)) != -1) {
					bos.write(buffer, 0, read);
				}

				bos.flush();

				bos.close();
				fos.close();
				bis.close();
				is.close();

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
}
