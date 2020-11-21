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

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BookmarksAdapter extends RecyclerView.Adapter<BookmarksAdapter.AccommodationViewHolder> {
    JSONArray bookmarks;
    Context context;
    private final String TAG = "Debug";

    public BookmarksAdapter(Context context, JSONArray bookmarks) {
        this.context = context;
        this.bookmarks = bookmarks;
    }

    @NonNull
    @Override
    public AccommodationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.bookmark_item,parent,false);
        return new AccommodationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AccommodationViewHolder holder, int position) {
        try {
            JSONObject bookmark = (JSONObject) bookmarks.get(position);
            JSONObject accommodation = bookmark.getJSONObject("building");
            holder.buildingNameTextView.setText(accommodation.getString("building_name"));
            holder.areaTextView.setText(accommodation.getString("area"));

            String gender_label = accommodation.getString("gender_label");
            if(gender_label.equalsIgnoreCase("M")){
                holder.genderLabelTextView.setText("Male");
                holder.genderLabelImageView.setImageResource(R.drawable.male_icon);
            } else if (gender_label.equalsIgnoreCase("F")){
                holder.genderLabelTextView.setText("Female");
                holder.genderLabelImageView.setImageResource(R.drawable.female_icon);
            } else if(gender_label.equalsIgnoreCase("U")){
                holder.genderLabelTextView.setText("Male/Female");
                holder.genderLabelImageView.setImageResource(R.drawable.unisex_icon);
            }

            String starPerks = getStarPerks(accommodation.getJSONArray("perks"));
            holder.starPerksTextView.setText(starPerks);

            int startingRent = accommodation.getInt("starting_rent");
            holder.rentTextView.setText("Starting @ ₹"+startingRent+"/mo");

            Glide.with(holder.imageView.getContext()).load(Shared.ROOT_URL + accommodation.getString("display_pic")).into(holder.imageView);
            holder.itemView.setOnClickListener(v -> {
                try {
                    Toast.makeText(context, accommodation.getString("id"), Toast.LENGTH_SHORT).show();

                } catch (JSONException e){

                }
            });

            holder.deleteImageView.setOnClickListener(v -> {
                try {
                    String bookmark_id = bookmark.getString("id");
                    String token = Shared.storage.getString("token","EMPTY");
                    StringRequest deleteBookmarkRequest = new StringRequest(
                            Request.Method.DELETE,
                            Shared.ROOT_URL+"/accounts/bookmark/delete/id="+bookmark_id+"/",
                            response -> Toast.makeText(context, "Removed Bookmark", Toast.LENGTH_SHORT).show(),
                            error -> Toast.makeText(context, "Something Went Wrong!", Toast.LENGTH_SHORT).show()
                    ){
                        @Override
                        public Map<String, String> getHeaders() throws AuthFailureError {
                            Map<String, String>  params = new HashMap<>();
                            params.put("Authorization", "Token "+token);
                            return params;
                        }
                    };

                    Shared.requestQueue.add(deleteBookmarkRequest);

                    bookmarks.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, bookmarks.length());

                } catch (JSONException e){

                }
            });

        } catch (JSONException e){

        }
    }


    @Override
    public int getItemCount() {
        return bookmarks.length();
    }

    public class AccommodationViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        ImageView genderLabelImageView;
        ImageView deleteImageView;
        TextView buildingNameTextView;
        TextView areaTextView;
        TextView genderLabelTextView;
        TextView starPerksTextView;
        TextView rentTextView;

        public AccommodationViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            genderLabelImageView = itemView.findViewById(R.id.genderLabelImageView);
            buildingNameTextView = itemView.findViewById(R.id.bulidingNameTextView);
            areaTextView = itemView.findViewById(R.id.areaTextView);
            genderLabelTextView = itemView.findViewById(R.id.genderLabelTextView);
            starPerksTextView = itemView.findViewById(R.id.starPerksTextView);
            rentTextView = itemView.findViewById(R.id.rentTextView);
            deleteImageView = itemView.findViewById(R.id.deleteImageView);

        }
    }

    private String getStarPerks(JSONArray perks) throws JSONException{
        String all_perks = "";
        for (int i=0; i<perks.length(); i++)
            all_perks += perks.getJSONObject(i).getString("description")+" ";

        List<String> list = new ArrayList<>();
        if(all_perks.toLowerCase().contains("wifi"))
            list.add("WiFi");
        if(all_perks.toLowerCase().contains("food"))
            list.add("Food");
        if(all_perks.toLowerCase().contains("laundry"))
            list.add("Laundry");

        int n = list.size();
        if(n == 0) return "None";

            String starPerks = "";
        for(int i=0; i<n; i++){
            if(i == n-1)
                starPerks += list.get(i);
            else
                starPerks += list.get(i) + ", ";
        }
        return starPerks;
    }
}
