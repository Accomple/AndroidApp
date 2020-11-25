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
import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.Map;

public class PerkAdapter extends RecyclerView.Adapter<PerkAdapter.PerkViewHolder> {
    JsonArray perks;
    Context context;
    Map<String,Integer> imageMap = new HashMap<>();
    private final String TAG = "Debug";

    public PerkAdapter(Context context, JsonArray perks) {
        this.perks = perks;
        this.context = context;

        imageMap.put("ac",R.drawable.perk_ac);
        imageMap.put("air-condition",R.drawable.perk_ac);
        imageMap.put("air condition",R.drawable.perk_ac);
        imageMap.put("attached washroom",R.drawable.perk_attached_washroom);
        imageMap.put("hot water",R.drawable.perk_hot_water_supply);
        imageMap.put("cctv",R.drawable.perk_cctv_camera);
        imageMap.put("camera",R.drawable.perk_cctv_camera);
        imageMap.put("surveillance",R.drawable.perk_cctv_camera);
        imageMap.put("balcony",R.drawable.perk_balcony);
        imageMap.put("gallery",R.drawable.perk_balcony);
        imageMap.put("cupboard",R.drawable.perk_cupboard);
        imageMap.put("mattress",R.drawable.perk_bed_with_mattress);
        imageMap.put("fan",R.drawable.perk_fan);
        imageMap.put("bin",R.drawable.perk_dustbins);
        imageMap.put("tree",R.drawable.perk_fresh_env);
        imageMap.put("green",R.drawable.perk_fresh_env);
        imageMap.put("environment",R.drawable.perk_fresh_env);
        imageMap.put("fitness",R.drawable.perk_fitness_zone);
        imageMap.put("workout",R.drawable.perk_fitness_zone);
        imageMap.put("gym",R.drawable.perk_fitness_zone);
        imageMap.put("food",R.drawable.perk_food);
        imageMap.put("breakfast",R.drawable.perk_food);
        imageMap.put("lunch",R.drawable.perk_food);
        imageMap.put("dinner",R.drawable.perk_food);
        imageMap.put("meal",R.drawable.perk_food);
        imageMap.put("furnish",R.drawable.perk_furnished);
        imageMap.put("housekeeping",R.drawable.perk_housekeeping);
        imageMap.put("clean",R.drawable.perk_clean);
        imageMap.put("laundry",R.drawable.perk_laundry);
        imageMap.put("mirror",R.drawable.perk_mirror);
        imageMap.put("dressing",R.drawable.perk_mirror);
        imageMap.put("inverter",R.drawable.perk_power_backup);
        imageMap.put("generator",R.drawable.perk_power_backup);
        imageMap.put("backup",R.drawable.perk_power_backup);
        imageMap.put("electricity",R.drawable.perk_power_backup);
        imageMap.put("study table",R.drawable.perk_study_table);
        imageMap.put("study-table",R.drawable.perk_study_table);
        imageMap.put("transport",R.drawable.perk_transport);
        imageMap.put("bus",R.drawable.perk_transport);
        imageMap.put("station",R.drawable.perk_transport);
        imageMap.put("internet",R.drawable.perk_wifi);
        imageMap.put("wifi",R.drawable.perk_wifi);
        imageMap.put("window",R.drawable.perk_window);
        imageMap.put("ventilated",R.drawable.perk_window);
        imageMap.put("ventilation",R.drawable.perk_window);
        imageMap.put("ventilated",R.drawable.perk_window);
        imageMap.put("oxygen",R.drawable.perk_window);
    }

    @NonNull
    @Override
    public PerkViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.perk_item,parent,false);
        return new PerkAdapter.PerkViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PerkViewHolder holder, int position) {
        JsonObject perk = perks.get(position).getAsJsonObject();
        String description = perk.get("description").getAsString();
        int imageId = getImageId(description);
        holder.perkImageView.setImageResource(imageId);
        holder.perkTextView.setText(description);
    }

    @Override
    public int getItemCount() {
        return perks.size();
    }

    private int getImageId(String description) {
        for (String key : imageMap.keySet()) {
            if (description.toLowerCase().contains(key))
                return imageMap.get(key);
        }
        return R.drawable.perk_unknown;
    }

    public class PerkViewHolder extends RecyclerView.ViewHolder{
        ImageView perkImageView;
        TextView perkTextView;

        public PerkViewHolder(@NonNull View itemView) {
            super(itemView);
            perkImageView = itemView.findViewById(R.id.perkImageView);
            perkTextView = itemView.findViewById(R.id.perkTextView);
        }
    }


}
