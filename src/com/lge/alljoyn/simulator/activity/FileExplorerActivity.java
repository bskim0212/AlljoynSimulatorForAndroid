package com.lge.alljoyn.simulator.activity;

import java.io.File;
import java.util.ArrayList;

import com.lge.alljoyn.simulator.R;
import com.lge.alljoyn.simulator.utils.ReadCVS;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class FileExplorerActivity extends Activity {
	
	private ImageButton ib_back;
	
	private String mFileName;
	private ListView lvFileControl;
	private FileAdapter mFileAdapter;
	private Context mContext = this;
	
	private ArrayList<String> lItem = null;
	private ArrayList<String> lPath = null;
	private String mRoot = Environment.getExternalStorageDirectory().getAbsolutePath();
	
	private TextView tv_Path;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_file);
		
		ib_back = (ImageButton) findViewById(R.id.ib_back);
		ib_back.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		
		tv_Path = (TextView) findViewById(R.id.tv_Path);
		lvFileControl = (ListView)findViewById(R.id.lvFileControl);

		getDir(mRoot);

	}
	
	private void getDir(String dirPath)

	{
		tv_Path.setText("Location: " + dirPath);
		lItem = new ArrayList<String>();
		lPath = new ArrayList<String>();
		

		File f = new File(dirPath);
		File[] files = f.listFiles();

		if (!dirPath.equals(mRoot))
		{
			//item.add(root); //to root.
			//path.add(root);

			lItem.add("../"); //to parent folder
			lPath.add(f.getParent());
		}
		for (int i = 0; i < files.length; i++)
		{
			File file = files[i];
			lPath.add(file.getAbsolutePath());

			if (file.isDirectory())
				lItem.add(file.getName());
			else
				lItem.add(file.getName());
		}
		mFileAdapter = new FileAdapter(lItem);
		lvFileControl.setAdapter(mFileAdapter);
	}
	
	public class FileAdapter extends BaseAdapter {
		private ArrayList<String> items;

		LayoutInflater Inflater;
		
		public FileAdapter(ArrayList<String> file_list) {
			this.items = file_list;
			Inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			if (items == null)
				return 0;
			return items.size();
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			if (items == null)
				return null;
			return items.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			if (convertView == null) {
				convertView = Inflater.inflate(R.layout.item_file, null, true);
			}
			final File file = new File(lPath.get(position));
			
			TextView tv = (TextView) convertView.findViewById(R.id.tv_filename);
			tv.setText(lItem.get(position));
			
			ImageButton btn_icon = (ImageButton) convertView.findViewById(R.id.btn_icon);
			if (file.isDirectory()){
				btn_icon.setImageResource(R.drawable.folder);
			}else{
				btn_icon.setImageResource(R.drawable.file_empty);
			}
			
			convertView.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub

					/* 만약 디렉토리를 클릭한 거라면 */
					if (file.isDirectory())
					{
						/* 디렉토리 클릭시 하위 디렉토리/파일들을 뷰로 구성해주어야 한다 */
						if (file.canRead())
							getDir(lPath.get(position));
						else
						{
							Toast.makeText(mContext, "No files in this folder.", Toast.LENGTH_SHORT).show();
						}
					}
					/* 만약 디렉토리가 아닌 파일을 클릭한 거라면 */
					else
					{
						mFileName = file.getName();  // 파일이름
						String ext = mFileName.substring(mFileName.lastIndexOf('.') + 1, mFileName.length()); //파일 확장자
						if(ext.equalsIgnoreCase("csv")){

							new AlertDialog.Builder(mContext)
							.setTitle("알림")
							.setMessage("[ " + mFileName + " ] 파일을 등록 하시겠습니까?")
							.setPositiveButton("OK", 
									new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int which) {
									// TODO Auto-generated method stub

									ReadCVS obj = new ReadCVS();
									obj.run(lPath.get(position).toString(), mFileName.substring(0, mFileName.lastIndexOf('.')), mContext);

								}
							})
							.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface arg0, int arg1) {
									// TODO Auto-generated method stub

								}
							}).show();
						}else{
							new AlertDialog.Builder(mContext).setTitle("알림").setMessage("파일을 확인해 주세요.")
							.setPositiveButton("확인", null).show();
						}


						Log.e("test", "filename="+lPath.get(position).toString());
					}
				
				}
			});
			
			return convertView;
		}
		
	}

}
