package com.example.ta_ecogarden;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.MenuItem;

import com.example.ta_ecogarden.fragment.fragmentChart;
import com.example.ta_ecogarden.fragment.fragmentDataLogger;
import com.example.ta_ecogarden.fragment.fragmentHome;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    BottomNavigationView bottomNavigationView;
    private BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment pilih = null;
            switch(item.getItemId()) {
                case R.id.home:
                    pilih = new fragmentHome();
                    setTitle("Dashboard");
                    break;
                case R.id.datas:
                    pilih = new fragmentDataLogger();
                    setTitle("Data Logger");
                    break;
                case R.id.grafik:
                    pilih = new fragmentChart();
                    setTitle("Chart");
                    break;
            }
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, pilih).commit();
            return true;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.setTitle("Dashboard");

        bottomNavigationView = findViewById(R.id.menunya);
        bottomNavigationView.setOnNavigationItemSelectedListener(navigationItemSelectedListener);

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new fragmentHome()).commit();
    }
}