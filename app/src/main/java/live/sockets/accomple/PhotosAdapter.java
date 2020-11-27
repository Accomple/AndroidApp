package live.sockets.accomple;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.gson.JsonArray;

public class PhotosAdapter extends RecyclerView.Adapter<PhotosAdapter.PhotoViewHolder> {
    JsonArray photoUrls;
    Context context;

    public PhotosAdapter(Context context, JsonArray photoUrls) {
        this.photoUrls = photoUrls;
        this.context = context;
    }

    @NonNull
    @Override
    public PhotoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.building_photo_item,parent,false);
        return new PhotosAdapter.PhotoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PhotoViewHolder holder, int position) {
        String path = photoUrls.get(position).getAsString();
        Glide.with(holder.collageImageView.getContext()).load(Shared.ROOT_URL + path).into(holder.collageImageView);

        holder.collageImageView.setOnClickListener(v -> {
            Intent intent = new Intent(context, WebViewActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("url",Shared.ROOT_URL+path);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return photoUrls.size();
    }

    public class PhotoViewHolder extends RecyclerView.ViewHolder{
        ImageView collageImageView;
        public PhotoViewHolder(@NonNull View itemView) {
            super(itemView);
            collageImageView = itemView.findViewById(R.id.collageImageView);
        }
    }
}
