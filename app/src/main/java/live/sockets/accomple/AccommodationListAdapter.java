package live.sockets.accomple;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class AccommodationListAdapter extends RecyclerView.Adapter<AccommodationListAdapter.AccommodationViewHolder> {
    JSONArray accommodations;
    Context context;

    public AccommodationListAdapter(Context context, JSONArray accommodations) {
        this.context = context;
        this.accommodations = accommodations;
    }

    @NonNull
    @Override
    public AccommodationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.building_item,parent,false);
        return new AccommodationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AccommodationViewHolder holder, int position) {
        try {
            JSONObject accommodation = (JSONObject) accommodations.get(position);
            holder.textView.setText(accommodation.getString("building_name"));
            Glide.with(holder.imageView.getContext()).load(Shared.ROOT_URL + accommodation.getString("display_pic")).into(holder.imageView);
            holder.itemView.setOnClickListener(v -> {
                try {
                    Toast.makeText(context, accommodation.getString("id"), Toast.LENGTH_SHORT).show();

                } catch (JSONException e){

                }
            });
        } catch (JSONException e){

        }
    }


    @Override
    public int getItemCount() {
        return accommodations.length();
    }

    public class AccommodationViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView textView;

        public AccommodationViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            textView = itemView.findViewById(R.id.textView);
        }
    }
}
