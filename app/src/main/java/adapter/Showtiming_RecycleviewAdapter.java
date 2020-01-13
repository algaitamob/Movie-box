package adapter;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import com.algaita.R;
import me.gujun.android.taggroup.TagGroup;
import modalclass.ShowTimeModalClass;


public class Showtiming_RecycleviewAdapter extends RecyclerView.Adapter<Showtiming_RecycleviewAdapter.MyViewHolder> {

    Context context;
    private List<ShowTimeModalClass> showTimeModalClasses;


    public Showtiming_RecycleviewAdapter(Context mainActivityContacts, List<ShowTimeModalClass> listModalClassList) {
        this.showTimeModalClasses = listModalClassList;
        this.context = mainActivityContacts;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_showtiming, parent, false);



        return new MyViewHolder(itemView);


    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        ShowTimeModalClass modalClass = showTimeModalClasses.get(position);
        holder.name.setText(modalClass.getName());

        holder.tagGroup.setTags(new String[]{"10:20 AM", "01:20 PM", "04:20 PM","07:20 PM","10:20 PM"});


    }

    @Override
    public int getItemCount() {
        return showTimeModalClasses.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {


        TextView name;
        TagGroup tagGroup;


        public MyViewHolder(View view) {
            super(view);


            name = (TextView) view.findViewById(R.id.name);
            tagGroup = (TagGroup) view.findViewById(R.id.tag_group);

        }

    }
}
