package com.algaita.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;

import adapter.Showtiming_RecycleviewAdapter;
import com.algaita.R;
import devs.mulham.horizontalcalendar.HorizontalCalendar;
import devs.mulham.horizontalcalendar.HorizontalCalendarView;
import devs.mulham.horizontalcalendar.utils.HorizontalCalendarListener;
import modalclass.ShowTimeModalClass;

public class ShowTimeActivity extends AppCompatActivity {

    HorizontalCalendar horizontalCalendar;
    TextView booktickets;
    RecyclerView showtime_recycleview;
    Showtiming_RecycleviewAdapter showtiming_recycleviewAdapter;
    private ArrayList<ShowTimeModalClass> classArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_time);

        showtime_recycleview = (RecyclerView)findViewById(R.id.showtime_recycleview);


        booktickets = (TextView)findViewById(R.id.booktickets);

        booktickets.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                Intent i = new Intent(ShowTimeActivity.this,MovieSheetActivity.class);
                startActivity(i);

            }
        });

        /* starts before 1 month from now */
        Calendar startDate = Calendar.getInstance();
        startDate.add(Calendar.MONTH, -1);

        /* ends after 1 month from now */
        Calendar endDate = Calendar.getInstance();
        endDate.add(Calendar.MONTH, 1);

         horizontalCalendar = new HorizontalCalendar.Builder(this, R.id.calendarView)
                .range(startDate, endDate)
                .datesNumberOnScreen(5)   // Number of Dates cells shown on screen (default to 5).
                .configure()    // starts configuration.
               // .formatTopText(String dateFormat)       // default to "MMM".
                .formatMiddleText("dd")    // default to "dd".
                .formatBottomText("EEE")    // default to "EEE".
                 .showTopText(false)// show or hide TopText (default to true).
                 .showBottomText(true)           // show or hide BottomText (default to true).
                   // .textColor(int normalColor, int selectedColor)    // default to (Color.LTGRAY, Color.WHITE).
                  //  .selectedDateBackground(getDrawable(R.drawable.gradient))      // set selected date cell background.
                //.selectorColor(int color)               // set selection indicator bar's color (default to colorAccent).
                .end()          // ends configuration.
                .defaultSelectedDate(Calendar.getInstance())    // Date to be selected at start (default to current day `Calendar.getInstance()`).
                .build();

         horizontalCalendar.getConfig()
                 .setFormatBottomText("EEE");

        horizontalCalendar.setCalendarListener(new HorizontalCalendarListener() {
            @Override
            public void onDateSelected(Calendar date, int position) {

            }

            @Override
            public void onCalendarScroll(HorizontalCalendarView calendarView,
                                         int dx, int dy) {

            }

            @Override
            public boolean onDateLongClicked(Calendar date, int position) {
                return true;
            }
        });



        classArrayList = new ArrayList<>();
        classArrayList.add(new ShowTimeModalClass("Cinepolis: Inorbit Vadodara Mall copy"));
        classArrayList.add(new ShowTimeModalClass("INOX : 7 Seas Mall | Fatehgunj"));
        classArrayList.add(new ShowTimeModalClass("Cinepolis: Inorbit Vadodara Mall copy"));
        classArrayList.add(new ShowTimeModalClass("INOX : 7 Seas Mall | Fatehgunj"));
        classArrayList.add(new ShowTimeModalClass("Cinepolis: Inorbit Vadodara Mall copy"));
        classArrayList.add(new ShowTimeModalClass("INOX : 7 Seas Mall | Fatehgunj"));


        showtiming_recycleviewAdapter = new Showtiming_RecycleviewAdapter(ShowTimeActivity.this,classArrayList);

        RecyclerView.LayoutManager mLayoutManager1 = new LinearLayoutManager(ShowTimeActivity.this);
        LinearLayoutManager layoutManager2 = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        showtime_recycleview.setLayoutManager(layoutManager2);
        showtime_recycleview.setAdapter(showtiming_recycleviewAdapter);


    }
}
