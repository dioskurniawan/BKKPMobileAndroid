package id.bkkp.general;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import mehdi.sakout.fancybuttons.FancyButton;

public class PopActivity extends Activity {

    int intentFragment;
    private SessionManager session;
    private EditText inputBST;
    String bst10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pop);

        session = new SessionManager(getApplicationContext());
        Bundle bundle = getIntent().getExtras();
        if (bundle!= null) {// to avoid the NullPointerException
            intentFragment = bundle.getInt("num");
        }
        LinearLayout layout1 = (LinearLayout) findViewById(R.id.layout1);
        inputBST = (EditText) findViewById(R.id.bst10);
        if(!session.sUserBST().equals(String.valueOf(-999))) {
            inputBST.setText(session.sUserBST());
        }

        FancyButton btnCancel = (FancyButton) findViewById(R.id.btn_cancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                MainActivity.activityMain.finish();
                Intent intent = new Intent(PopActivity.this, MainActivity.class);
                intent.putExtra("fragment", 3);
                startActivity(intent);
            }
        });
        FancyButton btnSave = (FancyButton) findViewById(R.id.btn_save);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!validateBST()) {
                    return;
                }

                final AlertDialog.Builder builder = new AlertDialog.Builder(PopActivity.this);
                builder.setMessage(R.string.confirm_txt);
                builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        bst10 = inputBST.getText().toString().trim();
                        session.setUserBST(bst10);
                        finish();
                        Intent i = new Intent(getApplicationContext(), ProfileActivity.class);
                        startActivity(i);
                    }
                });
                builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.setCanceledOnTouchOutside(true);
                dialog.show();
            }
        });
        if(intentFragment == 1) {
            layout1.setVisibility(View.VISIBLE);
        }
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int)(width*.8), (int)(height*.4));

        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.gravity = Gravity.CENTER;
        params.x = 0;
        params.y = -17;

        getWindow().setAttributes(params);
    }

    private boolean validateBST() {
        if (inputBST.getText().toString().trim().isEmpty()) {
            Toast.makeText(getApplicationContext(), R.string.err_msg_bst, Toast.LENGTH_LONG).show();
            //inputLayoutBST.setError(getString(R.string.err_msg_bst));
            requestFocus(inputBST);
            return false;
        } else if (inputBST.getText().toString().trim().length() < 10) {
            Toast.makeText(getApplicationContext(), R.string.enter_bst10, Toast.LENGTH_LONG).show();
            //inputLayoutBST.setError(R.string.enter_bst10);
            requestFocus(inputBST);
            return false;
        } else if (!inputBST.getText().toString().trim().substring(0,2).equals("62")) {
            Toast.makeText(getApplicationContext(), R.string.err_msg_bst2, Toast.LENGTH_LONG).show();
            //inputLayoutBST.setError(R.string.err_msg_bst2);
            requestFocus(inputBST);
            return false;
        } else {
            //inputLayoutBST.setErrorEnabled(false);
        }
        return true;
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }
    /*private boolean validateBST() {
        if (inputBST.getText().toString().trim().isEmpty()) {
            Toast.makeText(getApplicationContext(), R.string.enter_bst10, Toast.LENGTH_LONG).show();
            return false;
        } else if (inputBST.getText().toString().trim().length() < 10) {
            Toast.makeText(getApplicationContext(), R.string.enter_bst10, Toast.LENGTH_LONG).show();
            return false;
        } else {

        }

        return true;
    }*/
}
