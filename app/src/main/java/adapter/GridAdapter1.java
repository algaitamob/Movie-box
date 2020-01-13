package adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.algaita.R;

public class GridAdapter1 extends BaseAdapter {


    Context context;
    int seat[];
    LayoutInflater inflter;

    public GridAdapter1(Context applicationContext, int[] seat) {
        this.context = applicationContext;
        this.seat = seat;
        inflter = (LayoutInflater.from(applicationContext));
    }


    @Override
    public int getCount() {
        return seat.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        convertView = inflter.inflate(R.layout.item_seat, null);//set layout for displaying items
       final ImageView seat1 = (ImageView) convertView.findViewById(R.id.seat);//get id for image view

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                seat1.setImageResource(R.drawable.red_seat);

            }
        });

        return convertView;
    }
}
