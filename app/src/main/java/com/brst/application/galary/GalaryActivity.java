package com.brst.application.galary;

import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.brst.application.R;

import java.util.ArrayList;

public class GalaryActivity extends AppCompatActivity {

    ArrayList<String> cameraFiles;
    boolean camaraFront;

    Intent in;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_galary);


        in=getIntent();
        if (in!=null) {
            camaraFront = in.getBooleanExtra("camaraFront", false);
            cameraFiles=in.getStringArrayListExtra("list");

            FragmentManager fragmentManager = getSupportFragmentManager();
            GalaryFragment fragment = new GalaryFragment();
            Bundle bundle = new Bundle();
            bundle.putBoolean("camaraFront",camaraFront);
            bundle.putStringArrayList("list",cameraFiles);
            fragment.setArguments(bundle);
            final FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.add(R.id.main_container, fragment).commit();

        }





    }

}
