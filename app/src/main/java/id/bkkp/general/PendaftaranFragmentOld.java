package id.bkkp.general;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import mehdi.sakout.fancybuttons.FancyButton;

public class PendaftaranFragmentOld extends Fragment {

  TextView nama, nip, email, jabatan, panggol, profile;
  Button btnLogout;
  SessionManager session;
  private Context mContext;
  private EditText inputDate;
  Calendar myCalendar;
  int dateNo;
  private ProgressDialog mProgressDialog;
  AlertDialog alertDialog1;
  final Handler handler7 = new Handler();

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
    Calendar cald = Calendar.getInstance(TimeZone.getTimeZone("GMT+7:00"));
    Date currentLocalTimes = cald.getTime();
    DateFormat dateF = new SimpleDateFormat("dd-MMM-yyyy");
    DateFormat dateF2 = new SimpleDateFormat("yyyy-MM-dd");
    dateF.setTimeZone(TimeZone.getTimeZone("GMT+7:00"));

    inputDate = (EditText) rootView.findViewById(R.id.seafarer_dob);
    inputDate.setText(dateF.format(currentLocalTimes));
    //serverDate1 = dateF2.format(currentLocalTimes);

    myCalendar = Calendar.getInstance();

    final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
      @Override
      public void onDateSet(DatePicker view, int year, int monthOfYear,
                            int dayOfMonth) {
        // TODO Auto-generated method stub
        myCalendar.set(Calendar.YEAR, year);
        myCalendar.set(Calendar.MONTH, monthOfYear);
        myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        updateLabel(dateNo);
      }
    };

    inputDate.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        // TODO Auto-generated method stub
        DatePickerDialog datePickerDialog = new DatePickerDialog(mContext, /*android.R.style.Theme_Holo_Light_Dialog,*/ date, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        datePickerDialog.show();
        dateNo = 1;
      }
    });

    final FancyButton btnSearch = (FancyButton) rootView.findViewById(R.id.btn_cari);
    btnSearch.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {

        showProgressDialog(true);
        handler7.postDelayed(new Runnable() {
          @Override
          public void run() {
            hideProgressDialog();
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setMessage("Selamat, data Anda ditemukan");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

                      @Override
                      public void onClick(DialogInterface dialog, int which) {
                        Intent i = new Intent(mContext,
                                PendaftaranActivity.class);
                        startActivity(i);
                      }
                    }
            );
            alertDialog1 = builder.create();
            alertDialog1.setCanceledOnTouchOutside(false);
            alertDialog1.show();
          }
        }, 3000);

        //getActivity().finish();
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
        getActivity().finish();
      }
    });

    return rootView;
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

  private void updateLabel(final int numDate) {
    String startdate = null;
    String enddate = null;
    DateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");
    DateFormat newFormat = new SimpleDateFormat("yyyy-MM-dd");
    if(numDate == 1) {
      inputDate.setText(sdf.format(myCalendar.getTime()));
      //serverDate1 = newFormat.format(myCalendar.getTime());
      startdate = newFormat.format(myCalendar.getTime());
    } else {
    }
  }

  private void exitApp() {
    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
    builder.setTitle("Logout");
    builder.setMessage("Anda yakin ingin keluar dari akun ini ?");
    builder.setPositiveButton("Ya", new DialogInterface.OnClickListener() {

              @Override
              public void onClick(DialogInterface dialog, int which) {
                session.clearAllData();
                Intent intent = new Intent(mContext, LoginActivity.class);
                startActivity(intent);
                getActivity().finish();
              }
            }
    );
    builder.setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
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