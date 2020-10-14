package id.bkkp.general;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Cache;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import mehdi.sakout.fancybuttons.FancyButton;

public class HistoryListAdapter extends BaseAdapter {
	private Activity activity;
	private Context mContext;
	private LayoutInflater inflater;
	private List<GetItem> getItems;
	//ImageLoader imageLoader = AppController.getInstance().getImageLoader();
	private SessionManager session;
	Typeface font1, font2;
	FancyButton delete;
	public static String URL_INPUT = "https://bkkp.dephub.go.id/bkkpapi/appdata_dev.php";
	boolean internetAvailable;
	final Handler handler7 = new Handler();
	private ProgressDialog mProgressDialog;
	TextView btnStatus;
	FancyButton btnCancel;
	AlertDialog alertDialog1;

	public HistoryListAdapter(Activity activity, List<GetItem> getItems) {
		this.activity = activity;
		this.getItems = getItems;

		font1 = Typeface.createFromAsset(activity.getAssets(), "quattrocentobold.ttf");
		font2 = Typeface.createFromAsset(activity.getAssets(), "OpenSansBI.ttf");
		mContext=activity;
	}

	@Override
	public int getCount() {
		return getItems.size();
	}

	@Override
	public Object getItem(int location) {
		return getItems.get(location);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, final ViewGroup parent) {

		if (inflater == null)
			inflater = (LayoutInflater) activity
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		if (convertView == null)
			convertView = inflater.inflate(R.layout.list_history, null);

		session = new SessionManager(activity.getApplicationContext());

		/*if(mContext instanceof NewsFragment){
			((YourActivityName)mContext).yourDesiredMethod();
		}*/

		internetAvailable = session.isInternetAvailable();
		/*if (imageLoader == null)
			imageLoader = AppController.getInstance().getImageLoader();*/

		final View finalConvertView = convertView;

		btnCancel = (FancyButton) convertView.findViewById(R.id.btn_cancel);
		btnStatus = (TextView) convertView.findViewById(R.id.btn_status);
		btnStatus.setTypeface(font2);
		TextView name = (TextView) convertView.findViewById(R.id.title);
		//TextView content = (TextView) convertView.findViewById(R.id.excerpt);
		final TextView content = (TextView) convertView.findViewById(R.id.name);
		TextView timestamp = (TextView) convertView.findViewById(R.id.date);
		name.setTypeface(font1);

		final GetItem item = getItems.get(position);

		name.setText(mContext.getResources().getString(R.string.antrian_no) +item.getName());
		content.setText(item.getContent());
		timestamp.setText(item.getTimeStamp());


		name.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				//Toast.makeText(mContext, "status "+ item.getStatus(), Toast.LENGTH_LONG).show();
				if(Integer.parseInt(item.getStatus()) == 1) {
					Intent intent = new Intent(mContext,
							PendaftaranResultActivity.class);
					intent.putExtra("msg", "MCU tanggal " + item.getTimeStamp() + " di klinik " + item.getContent() + " dengan nomer antrian " + item.getName() + ".");
					intent.putExtra("val1", Integer.parseInt(item.getName()));
					intent.putExtra("val2", item.getTimeStamp());
					intent.putExtra("val3", item.getContent());
					intent.putExtra("val4", item.getCode()+"#"+item.getName());
					mContext.startActivity(intent);
					((Activity) mContext).finish();
				}
			}
		});
		content.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				//Toast.makeText(mContext, "status "+ item.getStatus(), Toast.LENGTH_LONG).show();
				if(Integer.parseInt(item.getStatus()) == 1) {
					Intent intent = new Intent(mContext,
							PendaftaranResultActivity.class);
					intent.putExtra("msg", "MCU tanggal " + item.getTimeStamp() + " di klinik " + item.getContent() + " dengan nomer antrian " + item.getName() + ".");
					intent.putExtra("val1", Integer.parseInt(item.getName()));
					intent.putExtra("val2", item.getTimeStamp());
					intent.putExtra("val3", item.getContent());
					intent.putExtra("val4", item.getCode()+"#"+item.getName());
					mContext.startActivity(intent);
					((Activity) mContext).finish();
				}
			}
		});
		timestamp.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				//Toast.makeText(mContext, "status "+ item.getStatus(), Toast.LENGTH_LONG).show();
				if(Integer.parseInt(item.getStatus()) == 1) {
					Intent intent = new Intent(mContext,
							PendaftaranResultActivity.class);
					intent.putExtra("msg", "MCU tanggal " + item.getTimeStamp() + " di klinik " + item.getContent() + " dengan nomer antrian " + item.getName() + ".");
					intent.putExtra("val1", Integer.parseInt(item.getName()));
					intent.putExtra("val2", item.getTimeStamp());
					intent.putExtra("val3", item.getContent());
					intent.putExtra("val4", item.getCode()+"#"+item.getName());
					mContext.startActivity(intent);
					((Activity) mContext).finish();
				}
			}
		});
		btnStatus.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				//Toast.makeText(mContext, "status "+ item.getStatus(), Toast.LENGTH_LONG).show();
				if(Integer.parseInt(item.getStatus()) == 1) {
					Intent intent = new Intent(mContext,
							PendaftaranResultActivity.class);
					intent.putExtra("msg", "MCU tanggal " + item.getTimeStamp() + " di klinik " + item.getContent() + " dengan nomer antrian " + item.getName() + ".");
					intent.putExtra("val1", Integer.parseInt(item.getName()));
					intent.putExtra("val2", item.getTimeStamp());
					intent.putExtra("val3", item.getContent());
					intent.putExtra("val4", item.getCode()+"#"+item.getName());
					mContext.startActivity(intent);
					((Activity) mContext).finish();
				}
			}
		});

		btnCancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
				builder.setTitle("Batalkan Jadwal Pemeriksaan");
				builder.setMessage("Pemeriksaan tanggal "+ item.getTimeStamp() + " di klinik "+ item.getContent() +" akan dibatalkan?" );
				builder.setPositiveButton("Ya", new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog, int which) {
								new isOnline().execute("");
								handler7.postDelayed(new Runnable() {
									@Override
									public void run() {
										if(internetAvailable){
											showProgressDialog(true);
											cancelQueue(item.getContent2(), item.getContent());
										} else {
											Toast.makeText(mContext, R.string.no_internet2, Toast.LENGTH_LONG).show();
										}
									}
								}, 2000);
							}
						}
				);
				builder.setNegativeButton("Tidak", new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog, int which) {
								alertDialog1.dismiss();
							}
						}
				);
				alertDialog1 = builder.create();
				alertDialog1.setCanceledOnTouchOutside(false);
				alertDialog1.show();
			}
		});

		Calendar cald = Calendar.getInstance(TimeZone.getTimeZone("GMT+7:00"));
		cald.add(Calendar.DATE, -1);
		Date currentLocalTimes = cald.getTime();
		DateFormat dateF2 = new SimpleDateFormat("yyyy-MM-dd");
		dateF2.setTimeZone(TimeZone.getTimeZone("GMT+7:00"));
		//dateF2.format(currentLocalTimes);

		Date date1 = null;
		try {
			date1 = new SimpleDateFormat("yyyy-MM-dd").parse(item.getContent2());
		} catch (ParseException e) {
			e.printStackTrace();
		}

		if(Integer.parseInt(item.getStatus()) == 2) {
			btnStatus.setText(mContext.getResources().getString(R.string.canceled));
			btnStatus.setTextColor(convertView.getResources().getColor(R.color.light_grey));
		} else if(date1.before(currentLocalTimes)) {
			btnStatus.setText(mContext.getResources().getString(R.string.done));
			btnStatus.setTextColor(convertView.getResources().getColor(R.color.lightgreen1));
		} else {
			btnStatus.setText(mContext.getResources().getString(R.string.active));
			btnStatus.setTextColor(convertView.getResources().getColor(R.color.blue_background));
			btnCancel.setVisibility(View.VISIBLE);
		}
		final View finalConvertView1 = convertView;

		return convertView;
	}

	private void showProgressDialog(boolean cancel) {
		if (mProgressDialog == null) {
			mProgressDialog = new ProgressDialog(mContext);
			mProgressDialog.setMessage(mContext.getString(R.string.loading));
			mProgressDialog.setIndeterminate(true);
			mProgressDialog.setCancelable(cancel);
		}

		mProgressDialog.show();
	}

	private void hideProgressDialog() {
		if (mProgressDialog != null && mProgressDialog.isShowing()) {
			mProgressDialog.hide();
		}
	}

	private void cancelQueue(String tgl, String clinic) {
		if(clinic.equals("KU BKKP Gunung Sahari")){
			clinic = "1";
		} else {
			clinic = "2";
		}
		Cache cache2 = AppController.getInstance().getRequestQueue().getCache();
		cache2.clear();
		Cache.Entry entry2 = cache2.get(URL_INPUT+"?cancel=" + session.sUserBST() + "&jwt=" + session.sJWT() + "&tgl_pesan=" + tgl + "&clinic=" + clinic + "&phone=" + session.phone());
		//Log.d("logd",URL_INPUT+"?cancel=" + session.sUserBST() + "&jwt=" + session.sJWT() + "&tgl_pesan=" + tgl + "&clinic=" + clinic);

		if (entry2 != null) {
			try {
				String data = new String(entry2.data, "UTF-8");
				try {
					parseJsonFeed(new JSONObject(data));
				} catch (JSONException e) {
					e.printStackTrace();
				}
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		} else {
			// making fresh volley request and getting json
			JsonObjectRequest jsonReq2 = new JsonObjectRequest(Request.Method.GET,
					URL_INPUT+"?cancel=" + session.sUserBST() + "&jwt=" + session.sJWT() + "&tgl_pesan=" + tgl + "&clinic=" + clinic + "&phone=" + session.phone(), null, new Response.Listener<JSONObject>() {

				@Override
				public void onResponse(JSONObject response) {
					VolleyLog.d("BKKP debug1", "Response: " + response.toString());
					parseJsonFeed(response);
				}
			}, new Response.ErrorListener() {

				@Override
				public void onErrorResponse(VolleyError error) {
					VolleyLog.d("BKKP debug2", "Error: " + error.getMessage());
				}
			});
			jsonReq2.setRetryPolicy(new RetryPolicy() {
				@Override
				public int getCurrentTimeout() {
					return 50000;
				}

				@Override
				public int getCurrentRetryCount() {
					return 50000;
				}

				@Override
				public void retry(VolleyError error) throws VolleyError {

				}
			});
			// Adding request to volley request queue
			AppController.getInstance().addToRequestQueue(jsonReq2);
		}
	}

	private void parseJsonFeed(JSONObject response) {
		try {

			boolean error = response.getBoolean("error");
			if (!error) {
				String message = response.getString("msg");
				//JSONArray feedArray = response.getJSONArray("cancel");
				//int rows = feedArray.length();

				hideProgressDialog();
				Toast.makeText(mContext, message, Toast.LENGTH_LONG).show();
				Log.d("logd",message);
				Intent intent = new Intent(mContext, HistoryActivity.class);
				mContext.startActivity(intent);
				activity.finish();
			} else {
				hideProgressDialog();
				String errmessage = response.getString("error_msg");
				Toast.makeText(mContext, errmessage, Toast.LENGTH_LONG).show();
				Intent intent = new Intent(mContext,
						HistoryActivity.class);
				mContext.startActivity(intent);
				activity.finish();
			}
		} catch (JSONException e) {
			hideProgressDialog();
			e.printStackTrace();
		}
	}

	private class isOnline extends AsyncTask<String, Integer, Boolean> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected Boolean doInBackground(String... arg0) {
			try {
				int timeoutMs = 1500;
				Socket sock = new Socket();
				SocketAddress sockaddr = new InetSocketAddress("8.8.8.8", 53);

				sock.connect(sockaddr, timeoutMs);
				sock.close();

				return true;
			} catch (IOException e) { return false; }
		}

		@Override
		protected void onPostExecute(Boolean online) {
			super.onPostExecute(online);
			internetAvailable = online;
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			super.onProgressUpdate(values);
		}

	}

}
