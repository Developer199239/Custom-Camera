package com.brst.application.galary;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.brst.application.R;
import com.brst.application.camera.MainActivity;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class GalaryAdapter extends RecyclerView.Adapter<GalaryAdapter.MyViewHolder> {
    Context context;
    ArrayList<String> cameraFiles;
    boolean camaraFront;

    public GalaryAdapter(Context context, ArrayList<String> cameraFiles, boolean camaraFront) {
        this.context = context;
        this.cameraFiles = cameraFiles;
        this.camaraFront = camaraFront;
    }

    @Override
    public GalaryAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.inflater_galary_image, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(GalaryAdapter.MyViewHolder holder, final int position) {

        if (cameraFiles.get(position).contains(".jpg")) {
            Glide.with(context).load(cameraFiles.get(position)).into(holder.imagesView);
            holder.video_icon.setVisibility(View.GONE);
            holder.imagesView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    FragmentManager fragmentManager = ((GalaryActivity) context).getSupportFragmentManager();
                    DialogFragment fragment = new DialogFragment();
                    Bundle bundle = new Bundle();
                    bundle.putBoolean("camaraFront", camaraFront);
                    Log.d("aaa", "===" + camaraFront);
                    bundle.putString("path", cameraFiles.get(position));
                    fragment.setArguments(bundle);
                    final FragmentTransaction transaction = fragmentManager.beginTransaction();
                    transaction.add(R.id.main_container, fragment).addToBackStack(null).commit();
                }
            });


        } else if (cameraFiles.get(position).contains(".mp4")) {
            holder.video_icon.setVisibility(View.VISIBLE);
            Glide.with(context).load(cameraFiles.get(position)).into(holder.imagesView);
            holder.video_icon.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public int getItemCount() {
        return cameraFiles.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView imagesView, video_icon;

        public MyViewHolder(View itemView) {
            super(itemView);
            imagesView = itemView.findViewById(R.id.images);
            video_icon = itemView.findViewById(R.id.video_icon);
        }
    }


}
