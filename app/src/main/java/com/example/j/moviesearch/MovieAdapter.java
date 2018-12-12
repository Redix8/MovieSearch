package com.example.j.moviesearch;

import android.content.Context;

import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.customtabs.CustomTabsIntent;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;

import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;

import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.ViewHolder> {
    Context context;
    EndlessScrollListener endlessScrollListener;
    CustomTabsIntent customTabsIntent;

    ArrayList<MovieResultItem> items = new ArrayList<>();

    public MovieAdapter(Context context){
        this.context = context;
    }


    @Override
    public int getItemCount() {
        return items.size();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = inflater.inflate(R.layout.movie_item, viewGroup, false);
        setCustomTabsIntent();

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        final MovieResultItem item = items.get(i);
        viewHolder.setItem(item);
        Glide.with(this.context)
                .load(item.getImage())
                .into(viewHolder.posterView);
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customTabsIntent.launchUrl(context, Uri.parse(item.getLink()));
            }
        });

        if(i == getItemCount() - 1) { if(endlessScrollListener != null) { endlessScrollListener.onLoadMore(i); } }

        Toast.makeText(context, "뷰홀더 바인드 item : " +Integer.toString(i), Toast.LENGTH_LONG).show();
    }
    public void setCustomTabsIntent(){
        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
        builder.setToolbarColor(ContextCompat.getColor(context, R.color.colorPrimary));
        builder.setStartAnimations(context, R.anim.slide_in_right, R.anim.slide_out_left);
        builder.setExitAnimations(context, R.anim.slide_in_left,R.anim.slide_out_right);
        builder.setCloseButtonIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_arrow_back));
        customTabsIntent = builder.build();
    }

    public void setEndlessScrollListener(EndlessScrollListener endlessScrollListener) {
        this.endlessScrollListener = endlessScrollListener;
    }

    public interface EndlessScrollListener{
        boolean onLoadMore(int i);
    }

    public void addItem(MovieResultItem item, int i){
        items.add(item);
        notifyItemInserted(i);
    }

    public void addItems(ArrayList<MovieResultItem> items){
        this.items.addAll(items);
        notifyDataSetChanged();
    }

    public void newItems(ArrayList<MovieResultItem> items){
        this.items = items;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        ImageView posterView;
        TextView titleView;
        TextView yearView;
        TextView directorView;
        TextView actorView;
        RatingBar ratingBar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            posterView = (ImageView) itemView.findViewById(R.id.imageView);
            titleView = (TextView) itemView.findViewById(R.id.titleView);
            yearView = (TextView) itemView.findViewById(R.id.yearView);
            directorView = (TextView) itemView.findViewById(R.id.directorView);
            actorView = (TextView) itemView.findViewById(R.id.actorView);
            ratingBar = (RatingBar) itemView.findViewById(R.id.ratingBar);

        }

        public void setItem(MovieResultItem item){
            titleView.setText(Html.fromHtml(item.getTitle()));
            yearView.setText(item.getPubDate());
            directorView.setText(item.getDirector());
            actorView.setText(item.getActor());
            ratingBar.setRating(Float.parseFloat(item.getUserRating())/2);

        }
    }

}
