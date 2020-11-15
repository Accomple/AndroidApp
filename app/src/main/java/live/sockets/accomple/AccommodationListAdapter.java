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

public class AccommodationListAdapter extends RecyclerView.Adapter<AccommodationListAdapter.AccommodationViewHolder> {
    Accommodation[] accommodations;
    Context context;

    public AccommodationListAdapter(Context context,Accommodation[] accommodations) {
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
        Accommodation accommodation = accommodations[position];
        holder.textView.setText(accommodation.getBuildingName());
        Glide.with(holder.imageView.getContext()).load(Shared.ROOT_URL+accommodation.getDisplayPic()).into(holder.imageView);
        holder.itemView.setOnClickListener(v -> {
            Toast.makeText(context,String.valueOf(accommodation.getId()),Toast.LENGTH_SHORT).show();
        });
    }


    @Override
    public int getItemCount() {
        return accommodations.length;
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
