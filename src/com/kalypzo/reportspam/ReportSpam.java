package com.kalypzo.reportspam;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import sms.kalypzo.cleansms.R;
import android.app.AlertDialog;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.telephony.TelephonyManager;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ReportSpam extends Fragment implements LoaderCallbacks<Cursor> {

	private static final int LOADER_ID = 1;// identify which loader
	LoaderManager lm;
	ReportSpamAdapter mAdapter;
	ListView lv;
	public Context context;

	private LoaderManager.LoaderCallbacks<Cursor> mCallbacks;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootview = inflater.inflate(R.layout.reportspam_activity,
				container, false);
		Toast.makeText(getActivity(), "Swipe an item to report spam",
				Toast.LENGTH_SHORT).show();
		context = getActivity();
		lv = (ListView) rootview.findViewById(R.id.list);
		mAdapter = new ReportSpamAdapter(getActivity(), null, 0);
		lv.setAdapter(mAdapter);

		ListView listView = lv;
		SwipeDismissListViewTouchListener touchListener = new SwipeDismissListViewTouchListener(
				listView,
				new SwipeDismissListViewTouchListener.DismissCallbacks() {

					@Override
					public boolean canDismiss(int position) {
						return true;
					}

					@Override
					public void onDismiss(final ListView listView,
							int[] reverseSortedPositions) {
						for (final int position : reverseSortedPositions) {

							AlertDialog.Builder adb = new AlertDialog.Builder(
									context);
							adb.setTitle("Report Spam?");
							adb.setPositiveButton("Ok",
									new DialogInterface.OnClickListener() {
										public void onClick(
												DialogInterface dialog, int id) {

											if (isNetworkAvaiable()) {
												Toast.makeText(context,
														"Reporting now..",
														Toast.LENGTH_SHORT)
														.show();
												View v = getViewByPosition(
														position, listView);
												String body = ((TextView) v
														.findViewById(R.id.tv_body))
														.getText().toString();
												String num = ((TextView) v
														.findViewById(R.id.tv_num))
														.getText().toString();
												TelephonyManager info = (TelephonyManager) getActivity()
														.getSystemService(
																Context.TELEPHONY_SERVICE);
												String opname = info
														.getNetworkOperatorName();
												String ph = null;
												ph = info.getLine1Number();
												new MyAsyncTask().execute(body
														+ "||Spammer:" + num
														+ "||User:" + ph
														+ "||Operator:"
														+ opname);
											} else {
												Toast.makeText(
														context,
														"Please check your internet connection",
														Toast.LENGTH_SHORT)
														.show();
											}
										}
									});
							adb.setNegativeButton("Cancel", null);
							adb.show();

							listView.invalidateViews();
						}
						mAdapter.notifyDataSetChanged();
					}
				});
		listView.setOnTouchListener(touchListener);
		listView.setOnScrollListener(touchListener.makeScrollListener());

		mCallbacks = this;
		lm = getLoaderManager();
		// Initiating the loader
		lm.initLoader(LOADER_ID, null, mCallbacks);
		return rootview;
	}

	@Override
	public android.support.v4.content.Loader<Cursor> onCreateLoader(int arg0,
			Bundle arg1) {
		Uri baseUri = Uri.parse("content://sms/inbox/");
		return new CursorLoader(getActivity(), baseUri, null, null, null,
				"date desc");
	}

	@Override
	public void onLoadFinished(android.support.v4.content.Loader<Cursor> arg0,
			Cursor arg1) {
		switch (arg0.getId()) {
		case LOADER_ID:
			mAdapter.swapCursor(arg1);
			break;
		}
	}

	@Override
	public void onLoaderReset(android.support.v4.content.Loader<Cursor> arg0) {
		mAdapter.swapCursor(null);
	}

	public View getViewByPosition(int pos, ListView listView) {
		final int firstListItemPosition = listView.getFirstVisiblePosition();
		final int lastListItemPosition = firstListItemPosition
				+ listView.getChildCount() - 1;

		if (pos < firstListItemPosition || pos > lastListItemPosition) {
			return listView.getAdapter().getView(pos, null, listView);
		} else {
			final int childIndex = pos - firstListItemPosition;
			return listView.getChildAt(childIndex);
		}
	}

	private boolean isNetworkAvaiable() {
		ConnectivityManager conmgr = (ConnectivityManager) getActivity()
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkinfo = conmgr.getActiveNetworkInfo();
		return activeNetworkinfo != null && activeNetworkinfo.isConnected();
	}

	private class MyAsyncTask extends AsyncTask<String, Integer, Double> {

		@Override
		protected Double doInBackground(String... params) {
			postData(params[0]);
			return null;
		}

		protected void onPostExecute(Double result) {
			Toast.makeText(getActivity(), "Spam reported", Toast.LENGTH_SHORT)
					.show();
		}

		protected void onProgressUpdate(Integer... progress) {
			// pb.setProgress(progress[0]);
		}

		public void postData(String valueIWantToSend) {
			// Create a new HttpClient and Post Header
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(
					"http://cleansms.x20.in/api/check.php");
			String Secret = "kalypzo";
			// Pattern pattern = Pattern.compile("\\|\\|");
			// String[] datatosend = pattern.split(valueIWantToSend);

			try {
				// Add your data
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
				nameValuePairs.add(new BasicNameValuePair("myHttpData",
						valueIWantToSend));
				nameValuePairs.add(new BasicNameValuePair("Secret", Secret));
				httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

				// Execute HTTP Post Request
				HttpResponse response = httpclient.execute(httppost);

			} catch (ClientProtocolException e) {

			} catch (IOException e) {

			}
		}

	}
}
