package com.hzy3774.chengyudict;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.hzy3774.chengyudict.R.id;

public class SettingActivity extends Activity {

	ImageButton ibtBack = null;
	ListView lvSettings = null;
	ProgressDialog checkUpdateDialog = null;
	LinearLayout llSettingAd = null;
	Builder aboutDialog = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);
		ibtBack = (ImageButton) findViewById(id.img_setting_back);
		lvSettings = (ListView) findViewById(id.listViewSettings);
		llSettingAd = (LinearLayout) findViewById(id.layoutSettingAd);

		ibtBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				SettingActivity.this.finish();
			}
		});

		lvSettings.setOnItemClickListener(new MyItemClickListener());

		checkUpdateDialog = new ProgressDialog(this);
		checkUpdateDialog.setTitle(R.string.title_tip);
		checkUpdateDialog.setMessage(getText(R.string.msg_check_wait));
		checkUpdateDialog.setCancelable(false);

	}

	class MyItemClickListener implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			switch (position) {
			case 0:
				break;
			case 1:
				break;
			case 2:
				break;
			case 3:
				if (aboutDialog == null) {
					aboutDialog = new Builder(SettingActivity.this);
					aboutDialog.setTitle(R.string.title_about);
					aboutDialog.setIcon(R.drawable.ic_launcher);
					aboutDialog.setMessage(R.string.msg_about_content);
					aboutDialog.setPositiveButton(R.string.msg_yes, null);
				}
				aboutDialog.show();
				break;
			default:
				break;
			}
		}
	}

}
