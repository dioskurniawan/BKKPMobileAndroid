package id.bkkp.general;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import mehdi.sakout.fancybuttons.FancyButton;

public class BeritaListAdapter extends BaseAdapter {
	private Activity activity;
	private Context mContext;
	private LayoutInflater inflater;
	private List<GetItem> getItems;
	//ImageLoader imageLoader = AppController.getInstance().getImageLoader();
	private SessionManager session;
	Typeface font1;
	FancyButton delete;
	boolean internetAvailable;
	final Handler handler7 = new Handler();
	private ProgressDialog mProgressDialog;

	public BeritaListAdapter(Activity activity, List<GetItem> getItems) {
		this.activity = activity;
		this.getItems = getItems;

		font1 = Typeface.createFromAsset(activity.getAssets(), "quattrocentobold.ttf");
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
			convertView = inflater.inflate(R.layout.list_berita, null);

		session = new SessionManager(activity.getApplicationContext());

		/*if(mContext instanceof NewsFragment){
			((YourActivityName)mContext).yourDesiredMethod();
		}*/

		internetAvailable = session.isInternetAvailable();
		/*if (imageLoader == null)
			imageLoader = AppController.getInstance().getImageLoader();*/

		final View finalConvertView = convertView;

		TextView name = (TextView) convertView.findViewById(R.id.title);
		//TextView content = (TextView) convertView.findViewById(R.id.excerpt);
		final TextView content2 = (TextView) convertView.findViewById(R.id.storycontent);
		TextView content3 = (TextView) convertView.findViewById(R.id.uri);
		TextView timestamp = (TextView) convertView.findViewById(R.id.postdate);
		name.setTypeface(font1);

		final GetItem item = getItems.get(position);

		String judul = item.getName();
		if (judul.length() >= 45) {
			String lastWord = judul.substring(0,45);//
			//String lastWord2 = lastWord.substring(lastWord.lastIndexOf(" ") + 1);
			//name.setText(judul.replace(" " + lastWord, "")+"...");
			name.setText(lastWord+"...");
		} else {
			name.setText(item.getName());
		}
		content2.setText(item.getContent2());
		content3.setText(item.getContent3());
		timestamp.setText(item.getTimeStamp());

		final View finalConvertView1 = convertView;
		name.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				try {
					//long nDID = parent.getItemIdAtPosition(position);
					String sURL = ((TextView) finalConvertView1.findViewById(R.id.uri)).getText().toString();
					Uri url = Uri.parse(sURL);
					Intent intent = new Intent(Intent.ACTION_VIEW, url);
					mContext.startActivity(intent);
				} catch (Exception e) { }
			}
		});
		content2.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				try {
					//long nDID = parent.getItemIdAtPosition(position);
					String sURL = ((TextView) finalConvertView1.findViewById(R.id.uri)).getText().toString();
					Uri url = Uri.parse(sURL);
					Intent intent = new Intent(Intent.ACTION_VIEW, url);
					mContext.startActivity(intent);
				} catch (Exception e) { }
			}
		});
		content3.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				try {
					//long nDID = parent.getItemIdAtPosition(position);
					String sURL = ((TextView) finalConvertView1.findViewById(R.id.uri)).getText().toString();
					Uri url = Uri.parse(sURL);
					Intent intent = new Intent(Intent.ACTION_VIEW, url);
					mContext.startActivity(intent);
				} catch (Exception e) { }
			}
		});

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

}
