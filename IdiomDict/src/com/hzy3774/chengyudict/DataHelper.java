package com.hzy3774.chengyudict;

import java.util.ArrayList;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

public class DataHelper {

	public enum QueryType {
		queryWord, queryRelated
	}

	String dbPath = null;
	SQLiteDatabase database = null;
	Handler handler = null;
	QueryType queryType = QueryType.queryWord;
	String keyWord = null;
	ArrayList<String> ret = null;

	public DataHelper(String dbPath, Handler handler) {
		this.dbPath = dbPath;
		this.handler = handler;
		database = SQLiteDatabase.openDatabase(dbPath, null, SQLiteDatabase.OPEN_READONLY);
		ret = new ArrayList<String>();
	}

	class QueryRunnable implements Runnable {
		@Override
		public void run() {
			doQueryWork();
		}
	}

	private synchronized void doQueryWork() {
		String sql = null;
		Cursor cursor = null;
		Message msg = null;
		Bundle data = null;

		switch (this.queryType) {
		case queryWord:
			sql = "SELECT dContent FROM words WHERE dName = ? LIMIT 1";
			cursor = database.rawQuery(sql, new String[] { keyWord });
			if (cursor.getCount() > 0) {
				cursor.moveToFirst();
				String ret = cursor.getString(0);
				cursor.close();
				msg = new Message();
				data = new Bundle();
				data.putString("ret", ret);
				msg.setData(data);
				msg.what = Consts.msgQueryWord;
				handler.sendMessage(msg);
			}
			break;
		case queryRelated:
			ret.clear();
			if (keyWord.matches("^[a-zA-Z]*")) {
				sql = "SELECT dName FROM words WHERE dPinyin LIKE ? LIMIT 30";
				cursor = database.rawQuery(sql, new String[] { keyWord + "%" });
				if (cursor.getCount() < 10) {
					while (cursor.moveToNext()) {
						ret.add(cursor.getString(0));
					}
					cursor.close();
					sql = "SELECT dName FROM words WHERE dShen LIKE ? LIMIT 30";
				}
			} else {
				sql = "SELECT dName FROM words WHERE dName LIKE ? LIMIT 30";
			}
			cursor = database.rawQuery(sql, new String[] { keyWord + "%" });
			while (cursor.moveToNext()) {
				ret.add(cursor.getString(0));
			}
			cursor.close();
			msg = handler.obtainMessage();
			data = new Bundle();
			data.putStringArrayList("key", ret);
			msg.setData(data);
			msg.what = Consts.msgQueryRelated;
			handler.sendMessage(msg);
			break;
		default:
			break;
		}
	}

	public void query(String word, QueryType type) {
		if (word.trim().isEmpty()) {
			return;
		} else {
			this.queryType = type;
			this.keyWord = word;
			new Thread(new QueryRunnable()).start();
		}
	}

	public void close() {
		database.close();
	}

}
