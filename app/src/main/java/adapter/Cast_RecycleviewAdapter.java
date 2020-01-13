package adapter;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;
import com.algaita.R;
import modalclass.CastModalClass;


public class Cast_RecycleviewAdapter extends RecyclerView.Adapter<Cast_RecycleviewAdapter.MyViewHolder> {

    Context context;
    private List<CastModalClass> castModalClasses;


    public Cast_RecycleviewAdapter(Context mainActivityContacts, List<CastModalClass> listModalClassList) {
        this.castModalClasses = listModalClassList;
        this.context = mainActivityContacts;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_cast, parent, false);


        return new MyViewHolder(itemView);


    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        CastModalClass modalClass = castModalClasses.get(position);
        holder.cast_image.setImageResource(modalClass.getCast_image());
        holder.cast_name.setText(modalClass.getCast_name());
    }

    @Override
    public int getItemCount() {
        return castModalClasses.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {


        TextView cast_name;
        ImageView cast_image;


        public MyViewHolder(View view) {
            super(view);


            cast_name = (TextView) view.findViewById(R.id.cast_name);
            cast_image = (ImageView) view.findViewById(R.id.cast_image);


        }

    }
}
