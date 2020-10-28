package id.bkkp.general;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.appcompat.view.ContextThemeWrapper;

import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.android.volley.Cache;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.auth.api.phone.SmsRetrieverClient;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import mehdi.sakout.fancybuttons.FancyButton;

import static java.lang.Integer.TYPE;
import static java.lang.Integer.parseInt;

public class PendaftaranFragment extends Fragment implements GoogleApiClient.ConnectionCallbacks,
        OtpReceivedInterface, GoogleApiClient.OnConnectionFailedListener {

  TextView txt_no_antrian, no_antrian, tgl_antrian, klinik_antrian;
  ImageView qrcode;
  Button btnLogout, btnOrder, btnCancel, btnProfile, btnEN, btnID;
  FancyButton btnHistory, btnEdit, btnEditBST;
  SessionManager session;
  private Context mContext;
  private EditText inputDate;
  Calendar myCalendar;
  int dateNo;
  private ProgressDialog mProgressDialog;
  AlertDialog alertDialog1;
  final Handler handler7 = new Handler();
  public static String URL_INPUT = "https://bkkp.dephub.go.id/bkkpapi/appdata_dev.php";
    public static String URL_LOGIN = "https://bkkp.dephub.go.id/bkkpapi/loginapp_dev.php";
  boolean internetAvailable;
  Typeface font1, font2;
  CardView card_antrian;
  EditText inputPhone, inputOtp;
  GoogleApiClient mGoogleApiClient;
  SmsBroadcastReceiver mSmsBroadcastReceiver;
  AlertDialog dialogOtp;

  @Override
  public void onAttach(final Activity activity) {
    super.onAttach(activity);
    mContext = activity;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

  }

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    View rootView = inflater.inflate(R.layout.fragment_pendaftaran, container, false);

    session = new SessionManager(mContext);
    font1 = Typeface.createFromAsset(getResources().getAssets(), "bahnschrift.ttf");
    font2 = Typeface.createFromAsset(getResources().getAssets(), "OpenSansBI.ttf");
    TextView toolbar = rootView.findViewById(R.id.toolbar_title);
    toolbar.setTypeface(font1);

    LinearLayout loggedIn = rootView.findViewById(R.id.loggedInLayout);
    LinearLayout loggedOut = rootView.findViewById(R.id.loggedOutLayout);

    card_antrian = rootView.findViewById(R.id.card_antrian);
    txt_no_antrian = rootView.findViewById(R.id.txt_no_antrian);
    no_antrian = rootView.findViewById(R.id.no_antrian);
    tgl_antrian = rootView.findViewById(R.id.tgl_antrian);
    klinik_antrian = rootView.findViewById(R.id.klinik_antrian);
    qrcode = rootView.findViewById(R.id.qrcode);
    txt_no_antrian.setTypeface(font1);
    no_antrian.setTypeface(font1);
    tgl_antrian.setTypeface(font2);
    klinik_antrian.setTypeface(font1);
    TextView txtHome = rootView.findViewById(R.id.txt_home);
    txtHome.setTypeface(font1);
    txtHome.setText(getResources().getString(R.string.welcome));
    Button btnAccount = rootView.findViewById(R.id.btn_account);
    btnAccount.setTypeface(font2);

    //Toast.makeText(mContext, Locale.getDefault().getLanguage(), Toast.LENGTH_LONG).show();
    btnID = rootView.findViewById(R.id.btn_id);
    btnEN = rootView.findViewById(R.id.btn_en);
    /*if(Locale.getDefault().getLanguage().equals("in")){
      btnID.setVisibility(View.GONE);
      btnEN.setVisibility(View.VISIBLE);
    } else if(Locale.getDefault().getLanguage().equals("en")){
      btnEN.setVisibility(View.GONE);
      btnID.setVisibility(View.VISIBLE);
    } else*/ if(session.sLang().equals("en")){
      btnEN.setVisibility(View.GONE);
      btnID.setVisibility(View.VISIBLE);
    } else if(session.sLang().equals("in")){
      btnID.setVisibility(View.GONE);
      btnEN.setVisibility(View.VISIBLE);
    }
    btnProfile = rootView.findViewById(R.id.btn_account);
    btnOrder = rootView.findViewById(R.id.btn_order);
    btnOrder.setTypeface(font1);
    //btnCancel = rootView.findViewById(R.id.btn_cancel);
    //btnCancel.setTypeface(font1);
    if(session.isLoggedIn()){
      loggedIn.setVisibility(View.VISIBLE);
      btnOrder.setBackgroundColor(getResources().getColor(R.color.lightgreen1));
      //btnOrder.setVisibility(View.VISIBLE);
      //btnCancel.setBackgroundColor(getResources().getColor(R.color.red_background));
    } else {
      btnOrder.setVisibility(View.VISIBLE);
      loggedOut.setVisibility(View.VISIBLE);
      btnOrder.setBackgroundColor(getResources().getColor(R.color.light_grey));
      //btnCancel.setBackgroundColor(getResources().getColor(R.color.light_grey));
    }
    //btnCancel.setVisibility(View.GONE);
    TextView bst = rootView.findViewById(R.id.bst);
    TextView txtbst = rootView.findViewById(R.id.txt_bst);
    bst.setTypeface(font1);
    if(!session.sUserBST().equals(String.valueOf(-999))) {
        bst.setText(session.sUserBST());
    } else {
        bst.setText("-");
    }
    txtbst.setTypeface(font1);
    TextView phone = rootView.findViewById(R.id.phone);
    TextView txtphone = rootView.findViewById(R.id.txt_phone);
    phone.setTypeface(font1);
    phone.setText(session.phone());
    txtphone.setTypeface(font1);

    btnID.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        session.setLang("in");
        Intent intent = new Intent(mContext, Localize.class);
        //intent.putExtra("lang", "in");
        startActivity(intent);
        ((Activity) mContext).finish();
      }
    });
    btnEN.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        session.setLang("en");
        Intent intent = new Intent(mContext, Localize.class);
        //intent.putExtra("lang", "en");
        startActivity(intent);
        ((Activity) mContext).finish();
      }
    });
    btnProfile.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        if(!session.sUserBST().equals(String.valueOf(-999))) {
            Intent intent = new Intent(mContext, ProfileActivity.class);
            startActivity(intent);
            ((Activity) mContext).finish();
        } else {
            Intent i = new Intent(mContext, PopActivity.class);
            i.putExtra("num", 1);
            startActivity(i);
        }
      }
    });
    btnOrder.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        //Toast.makeText(mContext, session.sUserBST16(), Toast.LENGTH_LONG).show();
        if(!session.isLoggedIn()){
          Snackbar.make(view, R.string.login_required, Snackbar.LENGTH_SHORT)
                  .setAction("Action", null).show();
        } else {
          if(session.sUserBST16().equals("62")) {

              Intent i = new Intent(mContext, PopActivity.class);
              i.putExtra("num", 1);
              startActivity(i);
            /*Snackbar snackbar = Snackbar.make(view, R.string.complete_data, Snackbar.LENGTH_INDEFINITE);
            snackbar.setAction("OK", new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                Intent intent = new Intent(mContext, ProfileActivity.class);
                startActivity(intent);
                ((Activity) mContext).finish();
              }
            });
            snackbar.show();*/
          } else {
            /*Intent intent = new Intent(mContext,
                    PendaftaranResultActivity.class);
            intent.putExtra("msg","MCU tanggal 22 Juni 2020 di klinik BKKP Ancol dengan nomer antrian 1. SMS notifikasi dikirim ke 081519213344.");
            intent.putExtra("val",1);
            startActivity(intent);*/
            Intent intent = new Intent(mContext, PendaftaranActivity.class);
            startActivity(intent);
            ((Activity) mContext).finish();
          }
        }
      }
    });
    /*btnCancel.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        if(!session.isLoggedIn()){
          Snackbar.make(view, "Silakan login terlebih dahulu", Snackbar.LENGTH_SHORT)
                  .setAction("Action", null).show();
        } else {

        }
      }
    });*/

    btnHistory = (FancyButton) rootView.findViewById(R.id.btn_history);
    //btnHistory.setVisibility(View.GONE);
    /*if(!session.isLoggedIn()){
      btnHistory.setVisibility(View.GONE);
    }*/
    btnHistory.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        Intent intent = new Intent(mContext, HistoryActivity.class);
        startActivity(intent);
        ((Activity) mContext).finish();
      }
    });
    btnEditBST = (FancyButton) rootView.findViewById(R.id.btn_edit_bst);
      btnEditBST.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
              Intent i = new Intent(mContext, PopActivity.class);
              i.putExtra("num", 1);
              startActivity(i);
          }
      });
    btnEdit = (FancyButton) rootView.findViewById(R.id.btn_edit);
      btnEdit.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
              LinearLayout container = new LinearLayout(mContext);
              container.setOrientation(LinearLayout.VERTICAL);
              LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                      LinearLayout.LayoutParams.MATCH_PARENT,
                      LinearLayout.LayoutParams.WRAP_CONTENT);
              lp.setMargins(43,0,43,0);
              inputPhone = new EditText(mContext);
              inputPhone.setInputType(InputType.TYPE_CLASS_PHONE);
              inputPhone.setLayoutParams(lp);
              inputPhone.setGravity(android.view.Gravity.TOP|android.view.Gravity.LEFT);
              inputPhone.setInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES|InputType.TYPE_TEXT_FLAG_MULTI_LINE);
              inputPhone.setLines(1);
              inputPhone.setMaxLines(1);
              inputPhone.setText(session.phone());
              container.addView(inputPhone, lp);
              final AlertDialog dialog = new AlertDialog.Builder(mContext)
                      .setPositiveButton(R.string.btn_save, null)
                      .setNegativeButton(R.string.btn_cancel2, null)
                      .setTitle(R.string.confirm)
                      .setMessage(R.string.confirm_txt2)
                      .setCancelable(false)
                      .setView(container) // uncomment this line
                      //alertDialog.setIcon(getResources().getDrawable(R.drawable.logo_dephub));
                      .show();
              Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
              positiveButton.setOnClickListener(new View.OnClickListener() {
                  @Override
                  public void onClick(View v) {
                      //if (!phone.isEmpty()) {
                      if (!validatePhone()) {
                          return;
                      }
                      final String phone = inputPhone.getText().toString();
                      new isOnline().execute("");
                      handler7.postDelayed(new Runnable() {
                          @Override
                          public void run() {
                              if(internetAvailable){
                                  // init broadcast receiver
                                  mSmsBroadcastReceiver = new SmsBroadcastReceiver();

                                      /*set google api client for hint request
                                  mGoogleApiClient = new GoogleApiClient.Builder(mContext)
                                          .addConnectionCallbacks((GoogleApiClient.ConnectionCallbacks) mContext)
                                          .enableAutoManage(mContext, this)
                                          .addApi(Auth.CREDENTIALS_API)
                                          .build();*/

                                          mSmsBroadcastReceiver.setOnOtpListeners(PendaftaranFragment.this);
                                  IntentFilter intentFilter = new IntentFilter();
                                  intentFilter.addAction(SmsRetriever.SMS_RETRIEVED_ACTION);
                                  mContext.registerReceiver(mSmsBroadcastReceiver, intentFilter);
                                  //showMessage("bst",last6_bst);
                                  startSMSListener();
                                  session.setTempPhone(phone);
                                  showProgressDialog(false);
                                  changePhone(session.sHash(), phone);
                                  dialog.dismiss();
                              } else {
                                  Toast.makeText(mContext, R.string.no_internet2, Toast.LENGTH_LONG).show();
                              }
                          }
                      }, 2000);
                  }
              });
          }
      });

    final FancyButton btnLogout = (FancyButton) rootView.findViewById(R.id.btn_logout);
    if(!session.isLoggedIn()){
      btnLogout.setVisibility(View.GONE);
    }
    btnLogout.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        exitApp();
        //getActivity().finish();
      }
    });

    final FancyButton btnLogin = (FancyButton) rootView.findViewById(R.id.btn_login);
    if(session.isLoggedIn()){
      btnLogin.setVisibility(View.GONE);
    }
    btnLogin.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        Intent intent = new Intent(mContext, LoginActivity.class);
        startActivity(intent);
        ((Activity) mContext).finish();
      }
    });
    if(session.isLoggedIn()) {
      new isOnline().execute("");
      handler7.postDelayed(new Runnable() {
        @Override
        public void run() {
          if (internetAvailable) {
            showProgressDialog(false);
            if(!session.sUserBST().equals(String.valueOf(-999))) {
                //showMessage("debug1",session.sUserBST());
                getQueue();
            } else {
                hideProgressDialog();
                //showMessage("debug2",session.sUserBST());
            }
          } else {
            Toast.makeText(mContext, R.string.no_internet2, Toast.LENGTH_LONG).show();
          }
        }
      }, 2000);
    }
    if(session.isLoggedIn() && session.sUserBST().equals(String.valueOf(-999))) {
        Intent i = new Intent(mContext, PopActivity.class);
        i.putExtra("num", 1);
        startActivity(i);
    }
    Log.d("bst = "+session.sUserBST()+"jwt token =",session.sJWT());
    return rootView;
  }

    @Override public void onConnected(@Nullable Bundle bundle) {

    }

    @Override public void onConnectionSuspended(int i) {

    }

    @Override public void onOtpReceived(String otp) {
        //Toast.makeText(this, "Otp Received " + otp, Toast.LENGTH_LONG).show();
        String otpCode = otp.replace("<#> ", "");
        inputOtp.setText(otpCode.substring(0, 4));
    }

    @Override public void onOtpTimeout() {
        Toast.makeText(mContext, "Time out, please resend", Toast.LENGTH_LONG).show();
    }

    @Override public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    public void startSMSListener() {
        SmsRetrieverClient mClient = SmsRetriever.getClient(mContext);
        Task<Void> mTask = mClient.startSmsRetriever();
        mTask.addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override public void onSuccess(Void aVoid) {
                //Toast.makeText(RegisterActivity.this, "SMS Retriever starts", Toast.LENGTH_LONG).show();
            }
        });
        mTask.addOnFailureListener(new OnFailureListener() {
            @Override public void onFailure(@NonNull Exception e) {
                Toast.makeText(mContext, "Error", Toast.LENGTH_LONG).show();
            }
        });
    }

  private void showOtpBox(){
      LinearLayout container = new LinearLayout(mContext);
      container.setOrientation(LinearLayout.VERTICAL);
      LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
              LinearLayout.LayoutParams.MATCH_PARENT,
              LinearLayout.LayoutParams.WRAP_CONTENT);
      lp.setMargins(43,0,150,0);
      inputOtp = new EditText(mContext);
      inputOtp.setInputType(InputType.TYPE_CLASS_PHONE);
      inputOtp.setLayoutParams(lp);
      inputOtp.setGravity(android.view.Gravity.TOP|android.view.Gravity.LEFT);
      inputOtp.setInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES|InputType.TYPE_TEXT_FLAG_MULTI_LINE);
      inputOtp.setLines(1);
      inputOtp.setMaxLines(1);
      container.addView(inputOtp, lp);
      dialogOtp = new AlertDialog.Builder(mContext)
              .setPositiveButton(R.string.btn_save, null)
              .setNegativeButton(R.string.btn_cancel2, null)
              .setTitle(R.string.confirmotp)
              .setMessage(R.string.confirm_txt3)
              .setCancelable(false)
              .setView(container) // uncomment this line
              //alertDialog.setIcon(getResources().getDrawable(R.drawable.logo_dephub));
              .show();
      Button positiveButton = dialogOtp.getButton(AlertDialog.BUTTON_POSITIVE);
      positiveButton.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {

              //if (!phone.isEmpty()) {

              if (!validateOtp()) {
                  return;
              }
              final String otp = inputOtp.getText().toString();
              new isOnline().execute("");
              handler7.postDelayed(new Runnable() {
                  @Override
                  public void run() {
                      if(internetAvailable){
                          //showMessage("bst",last6_bst);
                          showProgressDialog(false);
                          confirmOTP(otp);
                      } else {
                          Toast.makeText(mContext, R.string.no_internet2, Toast.LENGTH_LONG).show();
                      }
                  }
              }, 2000);
          }
      });
      /*AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);
      //alertDialog.setTitle(getResources().getString(R.string.confirm));
      alertDialog.setMessage(getResources().getString(R.string.confirm_txt3));
      alertDialog.setCancelable(false);

      LinearLayout container = new LinearLayout(mContext);
      container.setOrientation(LinearLayout.VERTICAL);
      LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
              LinearLayout.LayoutParams.MATCH_PARENT,
              LinearLayout.LayoutParams.WRAP_CONTENT);
      lp.setMargins(43,0,150,0);
      inputOtp = new EditText(mContext);
      inputOtp.setInputType(InputType.TYPE_CLASS_PHONE);
      inputOtp.setLayoutParams(lp);
      inputOtp.setGravity(android.view.Gravity.TOP|android.view.Gravity.LEFT);
      inputOtp.setInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES|InputType.TYPE_TEXT_FLAG_MULTI_LINE);
      inputOtp.setLines(1);
      inputOtp.setMaxLines(1);
      //inputOtp.setText(session.phone());
      container.addView(inputOtp, lp);
      alertDialog.setView(container); // uncomment this line
      //alertDialog.setIcon(getResources().getDrawable(R.drawable.logo_dephub));

      alertDialog.setPositiveButton(getResources().getString(R.string.btn_save),
              new DialogInterface.OnClickListener() {
                  public void onClick(DialogInterface dialog, int which) {
                      //if (!phone.isEmpty()) {

                      if (!validatePhone()) {
                          return;
                      }
                      final String otp = inputOtp.getText().toString();
                      new isOnline().execute("");
                      handler7.postDelayed(new Runnable() {
                          @Override
                          public void run() {
                              if(internetAvailable){
                                  //showMessage("bst",last6_bst);
                                  showProgressDialog(false);
                                  confirmOTP(otp);
                              } else {
                                  Toast.makeText(mContext, R.string.no_internet2, Toast.LENGTH_LONG).show();
                              }
                          }
                      }, 2000);
                      //}
                      /-*} else {
                          // Prompt user to enter credentials
                          Toast.makeText(mContext,
                                  "Nomor Ponsel harus diisi", Toast.LENGTH_LONG)
                                  .show();
                      }*-/
                  }
              });

      alertDialog.setNegativeButton(getResources().getString(R.string.btn_cancel2),
              new DialogInterface.OnClickListener() {
                  public void onClick(DialogInterface dialog, int which) {
                      dialog.cancel();
                  }
              });

      alertDialog.show();*/
  }

  private void getQueue() {
    Cache cache2 = AppController.getInstance().getRequestQueue().getCache();
    cache2.clear();
    Cache.Entry entry2 = cache2.get(URL_INPUT+"?get_queue=" + session.sUserBST() + "&jwt=" + session.sJWT() + "&phone=" + session.phone());

    if (entry2 != null) {
      // fetch the data from cache
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
              URL_INPUT+"?get_queue=" + session.sUserBST() + "&jwt=" + session.sJWT() + "&phone=" + session.phone(), null, new Response.Listener<JSONObject>() {

        @Override
        public void onResponse(JSONObject response) {
          //Log.d("logd",URL_INPUT+"?get_queue=" + session.sUserBST() + "&jwt=" + session.sJWT());
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
        int count1 = response.getInt("count1"); //active queue
        int count2 = response.getInt("count2"); //all queue
        String returnmessage = response.getString("msg");
        //Toast.makeText(mContext, "COUNT2 = "+count2, Toast.LENGTH_LONG).show();
        hideProgressDialog();
        if(count2 > 0){
          btnHistory.setVisibility(View.VISIBLE);
        } else {
          btnHistory.setVisibility(View.GONE);
        }
        if(count1 > 0){
          JSONArray feedArray = response.getJSONArray("count_queue");
          int rows = feedArray.length();

          for (int i = 0; i < feedArray.length(); i++) {
            JSONObject feedObj = (JSONObject) feedArray.get(i);

            String tgl = feedObj.getString("tgl");
            String tgl_server = feedObj.getString("tgl_server");
            String klinik = feedObj.getString("klinik");
            int noantrian = feedObj.getInt("no_antrian");
            btnOrder.setVisibility(View.GONE);
            card_antrian.setVisibility(View.VISIBLE);
            no_antrian.setText(String.valueOf(noantrian));
            tgl_antrian.setText(tgl);
            klinik_antrian.setText(klinik);
            MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
            BitMatrix bitMatrix = null;
            try {
              bitMatrix = multiFormatWriter.encode(tgl_server+"#"+String.valueOf(noantrian), BarcodeFormat.QR_CODE, 130, 130);
            } catch (WriterException e) {
              e.printStackTrace();
            }
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
            qrcode.setImageBitmap(bitmap);
          }
        } else {
          btnOrder.setVisibility(View.VISIBLE);
        }
      } else {
        hideProgressDialog();
        String errmessage = response.getString("error_msg");

      }
    } catch (JSONException e) {
      hideProgressDialog();
      e.printStackTrace();
    }
  }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    private boolean validatePhone() {
        if (inputPhone.getText().toString().trim().isEmpty()) {
            requestFocus(inputPhone);
            Toast.makeText(mContext, getResources().getString(R.string.phone_mandatory), Toast.LENGTH_LONG).show();
            return false;
        } else if (inputPhone.getText().toString().trim().length() < 10) {
            requestFocus(inputPhone);
            Toast.makeText(mContext, getResources().getString(R.string.phone_valid), Toast.LENGTH_LONG).show();
            return false;
        } else if (inputPhone.getText().toString().trim().equals(session.phone())) {
            requestFocus(inputPhone);
            Toast.makeText(mContext, getResources().getString(R.string.phone_same), Toast.LENGTH_LONG).show();
            return false;
        } else if (inputPhone.getText().toString().trim().substring(0,3).equals("+62")) {
            inputPhone.setText(inputPhone.getText().toString().trim().replace("+62","0"));
            //return true;
        } else if (!inputPhone.getText().toString().trim().substring(0,2).equals("08")) {
            requestFocus(inputPhone);
            Toast.makeText(mContext, getResources().getString(R.string.phone_valid), Toast.LENGTH_LONG).show();
            return false;
        } else {
            //inputLayoutPhone.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validateOtp() {
        if (inputOtp.getText().toString().trim().isEmpty()) {
            requestFocus(inputOtp);
            Toast.makeText(mContext, getResources().getString(R.string.confirm_txt3), Toast.LENGTH_LONG).show();
            return false;
        } else if (inputOtp.getText().toString().trim().length() < 4) {
            requestFocus(inputOtp);
            Toast.makeText(mContext, getResources().getString(R.string.otp_4digit), Toast.LENGTH_LONG).show();
            return false;
        } else {
            //inputLayoutPhone.setErrorEnabled(false);
        }

        return true;
    }

    private void changePhone(String hashString, String phone) {
        Cache cache2 = AppController.getInstance().getRequestQueue().getCache();
        cache2.clear();
        Cache.Entry entry2 = cache2.get(URL_LOGIN+"?change_number=" + hashString + "&bst=" + session.sUserBST() + "&old_phone=" + session.phone() + "&phone=" + phone + "&email=" + session.email());

        //Toast.makeText(mContext, "page: " + URL_INPUT+"?pelaut=", Toast.LENGTH_SHORT).show();
        if (entry2 != null) {
            try {
                String data = new String(entry2.data, "UTF-8");
                try {
                    parseJsonFeedHash(new JSONObject(data));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        } else {
            // making fresh volley request and getting json
            JsonObjectRequest jsonReq2 = new JsonObjectRequest(Request.Method.GET,
                    URL_LOGIN+"?change_number=" + hashString + "&bst=" + session.sUserBST() + "&old_phone=" + session.phone() + "&phone=" + phone + "&email=" + session.email(), null, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {
                    VolleyLog.d("BKKP debug1", "Response: " + response.toString());
                    parseJsonFeedHash(response);
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

    private void parseJsonFeedHash(JSONObject response) {
        try {
            hideProgressDialog();
            boolean error = response.getBoolean("error");
            if (!error) {
                JSONArray feedArray = response.getJSONArray("hash");
                String returnmessage = response.getString("msg");
                //Toast.makeText(RegisterActivity.this, returnmessage, Toast.LENGTH_LONG).show();
                Log.d("BKKP", "BKKP success register: " + feedArray.toString());
                showOtpBox();
            } else {
                String errorMsg = response.getString("error_msg");
                Toast.makeText(mContext,
                        errorMsg, Toast.LENGTH_LONG).show();
            }

        } catch (JSONException e) {
            hideProgressDialog();
            e.printStackTrace();
        }
    }

    private void confirmOTP(String otp) {
        Cache cache2 = AppController.getInstance().getRequestQueue().getCache();
        cache2.clear();
        Cache.Entry entry2 = cache2.get(URL_LOGIN+"?otp=" + otp + "&bst=" + session.sUserBST() + "&phone=" + session.tempPhone());

        //Toast.makeText(mContext, "page: " + URL_INPUT+"?pelaut=", Toast.LENGTH_SHORT).show();
        if (entry2 != null) {
            try {
                String data = new String(entry2.data, "UTF-8");
                try {
                    parseJsonFeedOtp(new JSONObject(data));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        } else {
            // making fresh volley request and getting json
            JsonObjectRequest jsonReq2 = new JsonObjectRequest(Request.Method.GET,
                    URL_LOGIN+"?otp=" + otp + "&bst=" + session.sUserBST() + "&phone=" + session.tempPhone(), null, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {
                    VolleyLog.d("BKKP debug1", "Response: " + response.toString());
                    parseJsonFeedOtp(response);
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

    private void parseJsonFeedOtp(JSONObject response) {

        try {
            hideProgressDialog();
            boolean error = response.getBoolean("error");

            if (!error) {
                hideProgressDialog();
                //response.getString("msg");
                Toast.makeText(mContext, R.string.msg_txt, Toast.LENGTH_LONG).show();
                session.setTempPhone("021");
                session.setPhone(response.getString("phone"));
                session.setJWT(response.getString("token"));
                dialogOtp.dismiss();
                Intent intent = new Intent(mContext, MainActivity.class);
                intent.putExtra("fragment", 3);
                startActivity(intent);
                ((Activity) mContext).finish();
            } else {
                // Error in login. Get the error message
                hideProgressDialog();
                String errorMsg = response.getString("error_msg");
                Toast.makeText(mContext,
                        errorMsg, Toast.LENGTH_LONG).show();
            }
        } catch (JSONException e) {
            // JSON error
            e.printStackTrace();
            //Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    public void showMessage(String title, String message)
    {
        AlertDialog.Builder builder=new AlertDialog.Builder(mContext);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.show();
    }

  private void showProgressDialog(boolean cancel) {
    if (mProgressDialog == null) {
      mProgressDialog = new ProgressDialog(mContext);
      mProgressDialog.setMessage(mContext.getString(R.string.loading2));
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

  private class isOnline extends AsyncTask<String, Integer, Boolean> {
    @Override
    protected void onPreExecute() {
      super.onPreExecute();
    }

    @Override
    protected Boolean doInBackground(String... arg0) {
      try {
        int timeoutMs = 5000;
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
  private void exitApp() {
    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
    builder.setTitle(getResources().getString(R.string.exit1));
    builder.setMessage(getResources().getString(R.string.exit2));
    builder.setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {

              @Override
              public void onClick(DialogInterface dialog, int which) {
                session.clearAllData();
                Intent intent = new Intent(mContext, MainActivity.class);
                intent.putExtra("fragment", 0);
                startActivity(intent);
                ((Activity) mContext).finish();
              }
            }
    );
    builder.setNegativeButton(getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
              @Override
              public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
              }
            }

    );
    AlertDialog alertdialog = builder.create();
    alertdialog.setCanceledOnTouchOutside(false);
    alertdialog.show();
  }
}