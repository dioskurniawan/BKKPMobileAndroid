package id.bkkp.general;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import mehdi.sakout.fancybuttons.FancyButton;

public class PendaftaranResultActivity extends AppCompatActivity {
    Toolbar toolbar;
    private SessionManager session;
    final Handler handler7 = new Handler();
    boolean internetAvailable = false;
    TextView result, noantrian, tgl, klinik;
    FancyButton btnOk;
    Typeface font1, font2;
    String txtMsg;
    String intentVal, intentVal3, intentVal4, intentVal5;
    int intentVal2;
    ImageView qrcode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pendaftaran_result);
        Bundle bundle = getIntent().getExtras();
        if (bundle!= null) {// to avoid the NullPointerException
            intentVal = bundle.getString("msg");
            intentVal2 = bundle.getInt("val1");
            intentVal3 = bundle.getString("val2");
            intentVal4 = bundle.getString("val3");
            intentVal5 = bundle.getString("val4");
        }
        font1 = Typeface.createFromAsset(getResources().getAssets(), "bahnschrift.ttf");
        font2 = Typeface.createFromAsset(getResources().getAssets(), "OpenSansBI.ttf");
        session = new SessionManager(getApplicationContext());

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        TextView title = (TextView) findViewById(R.id.toolbar_title);
        title.setTypeface(font1);

        noantrian = findViewById(R.id.no_antrian);
        tgl = findViewById(R.id.tgl);
        klinik = findViewById(R.id.txtklinik);
        noantrian.setTypeface(font1);
        tgl.setTypeface(font2);
        klinik.setTypeface(font1);
        result = (TextView) findViewById(R.id.result);
        result.setTypeface(font1);
        noantrian.setText(String.valueOf(intentVal2));
        tgl.setText(intentVal3);
        klinik.setText(intentVal4);
        qrcode = findViewById(R.id.qrcode);

        /*if(intentVal2 > 0) {
            txtMsg = "MCU tanggal xx-xxx-xxxx di klinik xxx dengan nomer antrian xx. SMS notifikasi dikirim ke 08xxx.";
        } else {
            txtMsg = "Anda tidak mendapat nomer antrian untuk MCU tanggal xx-xxx-xxxx di klinik xxx. Silakan pilih tanggal dan/atau klinik lain.";
        }*/
        //txtMsg = intentVal;
        if(intentVal2 > 0) {
            result.setText("");
            qrcode.setVisibility(View.VISIBLE);
            MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
            BitMatrix bitMatrix = null;
            try {
                bitMatrix = multiFormatWriter.encode(String.valueOf(intentVal5), BarcodeFormat.QR_CODE, 300, 300);
            } catch (WriterException e) {
                e.printStackTrace();
            }
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
            qrcode.setImageBitmap(bitmap);
        } else {
            result.setText(intentVal);
            qrcode.setVisibility(View.GONE);
            noantrian.setVisibility(View.GONE);
            klinik.setVisibility(View.GONE);
            tgl.setVisibility(View.GONE);
        }

        btnOk = (FancyButton) findViewById(R.id.btn_ok);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                if(intentVal2 > 0) {
                    Intent intent = new Intent(PendaftaranResultActivity.this, HistoryActivity.class);
                    //intent.putExtra("fragment", 3);
                    startActivity(intent);
                    finish();
                } else {
                    if(intentVal.charAt(0) == 'A') {
                        Intent intent = new Intent(PendaftaranResultActivity.this, HistoryActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Intent intent = new Intent(PendaftaranResultActivity.this, PendaftaranActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_BACK:
                    Intent i = new Intent(getApplicationContext(),
                            HistoryActivity.class);
                    startActivity(i);
                    finish();
                    return true;
            }

        }
        return super.onKeyDown(keyCode, event);
    }
}
