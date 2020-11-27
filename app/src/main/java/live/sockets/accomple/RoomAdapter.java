package live.sockets.accomple;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.json.JSONArray;

public class RoomAdapter extends RecyclerView.Adapter<RoomAdapter.RoomViewHolder> {
    JsonArray rooms;
    Context context;
    private final String TAG = "Debug";

    public RoomAdapter(Context context, JsonArray rooms) {
        this.rooms = rooms;
        this.context = context;
    }

    @NonNull
    @Override
    public RoomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.room_item,parent,false);
        return new RoomAdapter.RoomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RoomViewHolder holder, int position) {
        JsonObject room = rooms.get(position).getAsJsonObject();
        int rent = (int) room.get("rent").getAsDouble();
        int available = room.get("available").getAsInt();
        int occupancy = room.get("occupancy").getAsInt();
        if(available>0)
            holder.roomRentTextView.setText("₹ "+rent+" /mo");
    }

    @Override
    public int getItemCount() {
        return rooms.size();
    }

    public class RoomViewHolder extends RecyclerView.ViewHolder{
        ImageView bedImageView;
        TextView roomRentTextView;
        Button bookNowButton;

        public RoomViewHolder(@NonNull View itemView) {
            super(itemView);
            bedImageView = itemView.findViewById(R.id.bedImageView);
            roomRentTextView = itemView.findViewById(R.id.roomRentTextView);
            bookNowButton = itemView.findViewById(R.id.bookNowButton);
        }
    }
}
