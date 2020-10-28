package id.bkkp.general;

import androidx.annotation.NonNull;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.Locale;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    int intentFragment;
    SessionManager session;
    private static final int TIME_INTERVAL = 2000;
    private long mBackPressed;
<<<<<<< HEAD
    public static MainActivity activityMain;
=======
>>>>>>> bf25cb751f4cc992e6de58b8b17607974d00cca2

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        session = new SessionManager(getApplicationContext());
        // kita set default nya Home Fragment
        //intentFragment = getIntent().getExtras().getInt("fragment");
<<<<<<< HEAD
        activityMain = this;
=======
>>>>>>> bf25cb751f4cc992e6de58b8b17607974d00cca2
        Bundle bundle = getIntent().getExtras();
        if (bundle!= null) {// to avoid the NullPointerException
            intentFragment = bundle.getInt("fragment");
        }
        if(intentFragment == 0) {
            loadFragment(new BeritaFragment());
        } else if(intentFragment == 1) {
            loadFragment(new PelautFragment());
        } else if(intentFragment == 2) {
            loadFragment(new RumahSakitFragment());
        } else if(intentFragment == 3) {
            loadFragment(new PendaftaranFragment());
        }
        // inisialisasi BottomNavigaionView
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        // beri listener pada saat item/menu bottomnavigation terpilih
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        //if(session.nProfileID() > 1) {
        //    bottomNavigationView.getMenu().removeItem(R.id.action_surat);
        //}
        //session.setLang(Locale.getDefault().getLanguage());
    }
<<<<<<< HEAD

=======
>>>>>>> bf25cb751f4cc992e6de58b8b17607974d00cca2
    private boolean loadFragment(Fragment fragment) {
        if (fragment != null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fl_container, fragment)
                    .commit();
            return true;
        }
        return false;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment fragment = null;
        switch (item.getItemId()){
            case R.id.action_home:
                fragment = new BeritaFragment();
                break;
            case R.id.action_pelaut:
                fragment = new PelautFragment();
                break;
            case R.id.action_rsku:
                fragment = new RumahSakitFragment();
                break;
            case R.id.action_pendaftaran:
                fragment = new PendaftaranFragment();
                break;
        }
        return loadFragment(fragment);
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    @Override
    public void onBackPressed()
    {
        if (mBackPressed + TIME_INTERVAL > System.currentTimeMillis())
        {
            super.onBackPressed();
            return;
        }
        else { Toast.makeText(getApplicationContext(), R.string.exitapp, Toast.LENGTH_SHORT).show(); }

        mBackPressed = System.currentTimeMillis();
    }

}
