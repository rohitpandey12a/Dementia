package com.example.dementia;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.ViewHolder>{
	private ArrayList<String> imageList;
	private Context context;
	public GalleryAdapter(ArrayList<String> imageList, Context context){
		this.imageList = imageList;
		this.context=context;
	}
	
	@NonNull
	@Override
	public GalleryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
		View view =
				LayoutInflater.from(parent.getContext()).inflate(R.layout.gallery,
						parent,false);
		return new ViewHolder(view);
	}
	
	@Override
	public void onBindViewHolder(@androidx.annotation.NonNull ViewHolder holder, int position) {
		Glide.with(holder.itemView.getContext()).load(imageList.get(position)).into(holder.imageView);
	}
	
	@Override
	public int getItemCount(){
		return imageList.size();
	}
	
	public class ViewHolder extends RecyclerView.ViewHolder{
		ImageView imageView;
		public ViewHolder(@NonNull View itemView){
			super(itemView);
			imageView=itemView.findViewById(R.id.item);
		}
	}
	
}
