package com.algaita.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import adapter.GridAdapter1;
import co.ceryle.fitgridview.FitGridView;
import com.algaita.R;

public class MovieSheetActivity extends AppCompatActivity{



    FitGridView gridView1, gridView2, gridView3, gridView4, gridView5, gridView6;
    GridAdapter1 gridAdapter1, gridAdapter2, gridAdapter3, gridAdapter4, gridAdapter5, gridAdapter6;


    int seat[] = {R.drawable.seat, R.drawable.seat, R.drawable.seat, R.drawable.seat};
    int seat1[] = {R.drawable.seat, R.drawable.seat, R.drawable.seat, R.drawable.seat, R.drawable.seat, R.drawable.seat, R.drawable.seat, R.drawable.seat,
            R.drawable.seat, R.drawable.seat, R.drawable.seat, R.drawable.seat};

    int seat4[] = {R.drawable.seat, R.drawable.seat, R.drawable.seat, R.drawable.seat, R.drawable.seat, R.drawable.seat, R.drawable.seat, R.drawable.seat};

    int seat5[] = {R.drawable.seat, R.drawable.seat, R.drawable.seat, R.drawable.seat, R.drawable.seat, R.drawable.seat,
            R.drawable.seat, R.drawable.seat, R.drawable.seat, R.drawable.seat, R.drawable.seat, R.drawable.seat,
            R.drawable.seat, R.drawable.seat, R.drawable.seat, R.drawable.seat, R.drawable.seat, R.drawable.seat,
            R.drawable.seat, R.drawable.seat, R.drawable.seat, R.drawable.seat, R.drawable.seat, R.drawable.seat};

    int seat6[] = {R.drawable.seat, R.drawable.seat, R.drawable.seat, R.drawable.seat, R.drawable.seat, R.drawable.seat, R.drawable.seat, R.drawable.seat};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_sheet);





        gridView1 = (FitGridView) findViewById(R.id.gridView1);
        gridAdapter1 = new GridAdapter1(getApplicationContext(), seat);
        gridView1.setAdapter(gridAdapter1);

        gridView2 = (FitGridView) findViewById(R.id.gridView2);
        gridAdapter2 = new GridAdapter1(getApplicationContext(), seat1);
        gridView2.setAdapter(gridAdapter2);


        gridView3 = (FitGridView) findViewById(R.id.gridView3);
        gridAdapter3 = new GridAdapter1(getApplicationContext(), seat);
        gridView3.setAdapter(gridAdapter3);

        gridView4 = (FitGridView) findViewById(R.id.gridView4);
        gridAdapter4 = new GridAdapter1(getApplicationContext(), seat4);
        gridView4.setAdapter(gridAdapter4);

        gridView5 = (FitGridView) findViewById(R.id.gridView5);
        gridAdapter5 = new GridAdapter1(getApplicationContext(), seat5);
        gridView5.setAdapter(gridAdapter5);

        gridView6 = (FitGridView) findViewById(R.id.gridView6);
        gridAdapter6 = new GridAdapter1(getApplicationContext(), seat6);
        gridView6.setAdapter(gridAdapter6);


    }
}
