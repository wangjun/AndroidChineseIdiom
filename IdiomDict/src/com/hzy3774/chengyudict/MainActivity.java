package com.hzy3774.chengyudict;

import java.io.File;
import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Filter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hzy3774.chengyudict.DataHelper.QueryType;
import com.hzy3774.chengyudict.R.id;

public class MainActivity extends Activity {

	private DbUnZiper prepareWork = null;
	private Handler handler = null;
	private ProgressDialog progressDialog = null;
	private String appPath = null;
	ArrayAdapter<String> adapter = null;
	ArrayList<String> retArr = null;
	DataHelper dbHelper = null;
	MyFilter myFilter = null;
	Builder exitBuilder = null;
	boolean isQuerying = false;
	int appOpenTime = 0;

	AutoCompleteTextView atvWord;
	ImageButton btQuery, btMarket, btSet;
	WebView wvExplain;
	LinearLayout llAdBottom;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initEnvironment();
		initLayoutAndViews();
		prepareWork = new DbUnZiper(handler, this, appPath);
		prepareWork.start();
	}

	private void initEnvironment() {
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			appPath = Environment.getExternalStorageDirectory() + File.separator + Consts.sdDir + File.separator;
			File dir = new File(appPath);
			if (!dir.exists()) {
				dir.mkdirs();
			}
		} else {
			appPath = this.getFilesDir().getAbsolutePath() + File.separator;
		}
		this.handler = new Handler(new MyCallback());
	}
	
	private void initLayoutAndViews() {
		setContentView(R.layout.activity_main);
		atvWord = (AutoCompleteTextView) findViewById(id.autoCompleteWord);
		btQuery = (ImageButton) findViewById(id.buttonQuery);
		btMarket = (ImageButton) findViewById(id.img_market);
		btSet = (ImageButton) findViewById(id.img_set);
		wvExplain = (WebView) findViewById(id.webViewExplain);
		llAdBottom = (LinearLayout) findViewById(id.layoutAdBottom);
		
		atvWord.addTextChangedListener(new MyTextWatcher());
		MyOnClickListener listener = new MyOnClickListener();
		btQuery.setOnClickListener(listener);
		btMarket.setOnClickListener(listener);
		btSet.setOnClickListener(listener);
	}

	private void startQuery() {
		dbHelper = new DataHelper(appPath + Consts.fileName, handler);
		myFilter = new MyFilter();
		retArr = new ArrayList<String>();
		adapter = new ArrayAdapter<String>(MainActivity.this, R.layout.auto_list_item, retArr) {
			@Override
			public Filter getFilter() {
				return myFilter;
			}
		};
		atvWord.setAdapter(adapter);
		atvWord.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				String str = ((TextView) view).getText().toString();
				dbHelper.query(str, QueryType.queryWord);
			}
		});
	}

	class MyFilter extends Filter {
		@Override
		protected FilterResults performFiltering(CharSequence constraint) {
			if (isQuerying) {
				return null;
			}
			FilterResults results = new FilterResults();
			results.values = retArr;
			results.count = retArr.size();
			return results;
		}

		@Override
		protected void publishResults(CharSequence constraint, FilterResults results) {
		}
	}

	class MyCallback implements Handler.Callback {
		@Override
		public boolean handleMessage(Message msg) {
			switch (msg.what) {
			case Consts.msgFileOk:
				startQuery();
				break;
			case Consts.msgHandleFileStart:
				progressDialog = new ProgressDialog(MainActivity.this);
				progressDialog.setTitle(R.string.title_tip);
				progressDialog.setMessage(getText(R.string.msg_load_wait));
				progressDialog.setCancelable(false);
				progressDialog.show();
				break;
			case Consts.msgHandleFileEnd:
				progressDialog.dismiss();
				prepareWork.start();
				break;
			case Consts.msgHandleFileError:
				Toast.makeText(MainActivity.this, R.string.msg_file_error, Toast.LENGTH_SHORT).show();
				MainActivity.this.finish();
				break;
			case Consts.msgQueryWord:
				String retStr = msg.getData().getString("ret");
				wvExplain.loadDataWithBaseURL(null, retStr, "text/html", "utf-8", null);
				wvExplain.scrollTo(0, 0);
				break;
			case Consts.msgQueryRelated:
				retArr.clear();
				retArr.addAll(msg.getData().getStringArrayList("key"));
				isQuerying = false;
				adapter.notifyDataSetChanged();
				break;
			default:
				break;
			}
			return false;
		}
	}

	class MyOnClickListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case id.buttonQuery:
				String word = atvWord.getText().toString();
				dbHelper.query(word, QueryType.queryWord);
				break;
			case id.img_market:
				break;
			case id.img_set:
				startActivity(new Intent(MainActivity.this, SettingActivity.class));
				break;
			default:
				break;
			}
		}
	}

	class MyTextWatcher implements TextWatcher {
		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			isQuerying = true;
			dbHelper.query(s.toString(), QueryType.queryRelated);
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {
		}

		@Override
		public void afterTextChanged(Editable s) {
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	protected void onDestroy() {
		dbHelper.close();
		super.onDestroy();
	}

	@Override
	public void onBackPressed() {
		this.alertOnExit();
	}

	private void alertOnExit() {
		if (exitBuilder == null) {
			exitBuilder = new Builder(MainActivity.this);
			exitBuilder.setNegativeButton(R.string.msg_cancel, null);
			exitBuilder.setTitle(R.string.title_tip);
			exitBuilder.setMessage(R.string.msg_confirm_exit);
			exitBuilder.setIcon(R.drawable.ic_launcher);
			exitBuilder.setPositiveButton(R.string.msg_yes, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					MainActivity.this.finish();
				}
			});
		}
		exitBuilder.show();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case id.action_exit:
			alertOnExit();
			break;
		case id.action_settings:
			startActivity(new Intent(this, SettingActivity.class));
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}
}
