package live.sockets.accomple;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.json.JSONArray;

import java.util.HashMap;
import java.util.Map;

public class RoomAdapter extends RecyclerView.Adapter<RoomAdapter.RoomViewHolder> {
    JsonArray rooms;
    Context context;
    String token = "EMPTY";
    boolean is_verified = false;
    Map<Integer,Integer> imageMap = new HashMap<>();
    private final String TAG = "Debug";

    public RoomAdapter(Context context, JsonArray rooms) {
        this.rooms = rooms;
        this.context = context;
        this.token  = Shared.storage.getString("token","EMPTY");
        this.is_verified = Shared.storage.getBoolean("is_verified",false);

        imageMap.put(1,R.drawable.bed_icon_601);
        imageMap.put(2,R.drawable.bed_icon_602);
        imageMap.put(3,R.drawable.bed_icon_603);
        imageMap.put(4,R.drawable.bed_icon_604);
        imageMap.put(5,R.drawable.bed_icon_605);
        imageMap.put(6,R.drawable.bed_icon_606);
        imageMap.put(7,R.drawable.bed_icon_607);
        imageMap.put(8,R.drawable.bed_icon_608);
        imageMap.put(9,R.drawable.bed_icon_609);

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
        int occupancy = room.get("occupancy").getAsInt();
        int id = room.get("id").getAsInt();
        holder.roomRentTextView.setText("₹ "+rent+" /mo");
        holder.bedImageView.setImageResource(imageMap.get(occupancy));

        holder.bookNowButton.setOnClickListener(v -> {

            if(token.equalsIgnoreCase("EMPTY")){
                Intent intent = new Intent(context,LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            } else {
                if(is_verified){
                    new AlertDialog.Builder(v.getRootView().getContext())
                            .setIcon(android.R.drawable.ic_dialog_info)
                            .setTitle("Confirm Booking")
                            .setMessage("This will share your details with the Room Owner")
                            .setPositiveButton("Proceed", (dialog, which) -> sendBookingRequest(id))
                            .setNegativeButton("Go Back", null)
                            .show();
                } else {
                    Intent intent = new Intent(context,AccountActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    Toast.makeText(context,"Account verification needed for Booking",Toast.LENGTH_LONG).show();
                    context.startActivity(intent);
                }
            }
        });
    }

    private void sendBookingRequest(int id){
        StringRequest addBookingRequest = new StringRequest(
                Request.Method.POST,
                Shared.ROOT_URL+"/accommodations/booking/add/id="+id+"/",
                response -> {
                    Log.d(TAG, response);
                    Intent intent = new Intent(context,BookingActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                    Toast.makeText(context,"Room Booked!,\nHope You'll have a Nice Deal", Toast.LENGTH_LONG).show();
                },
                error -> {
                    Log.d(TAG, error.toString());
                    Toast.makeText(context,"Sorry!\nYou have another Booking Active", Toast.LENGTH_LONG).show();
                }
        ){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String>  params = new HashMap<>();
                params.put("Authorization", "Token "+token);
                return params;
            }
        };
        Shared.requestQueue.add(addBookingRequest);
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
