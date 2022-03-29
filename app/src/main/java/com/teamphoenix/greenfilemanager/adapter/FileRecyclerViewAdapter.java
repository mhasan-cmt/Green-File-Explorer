package com.teamphoenix.greenfilemanager.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.teamphoenix.greenfilemanager.R;
import com.teamphoenix.greenfilemanager.listener.onFileSelectedListener;

import java.io.File;
import java.util.List;

public class FileRecyclerViewAdapter extends RecyclerView.Adapter<FileRecyclerViewHolder> {
    private Context context;
   private List<File> files;
    private onFileSelectedListener onFileSelectedListener;

    public FileRecyclerViewAdapter(Context context, List<File> files, onFileSelectedListener onFileSelectedListener) {
        this.context = context;
        this.files = files;
        this.onFileSelectedListener = onFileSelectedListener;
    }

    @NonNull
    @Override
    public FileRecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new FileRecyclerViewHolder(LayoutInflater.from(context).inflate(R.layout.item_file_container, parent, false));
    }

    @SuppressLint("RecyclerView")
    @Override
    public void onBindViewHolder(@NonNull FileRecyclerViewHolder holder, int position) {
        holder.fileName.setText(files.get(position).getName());
        holder.fileName.setSelected(true);

//        checking if it is a directory then set file counts
        int items = 0;
        if(files.get(position).isDirectory()){
            File[] mFiles =files.get(position).listFiles();
            for (File singleFile: mFiles){
                if(!singleFile.isHidden()){
                    items+=1;
                }
            }
            holder.fileSize.setText(String.valueOf(items)+" Files");
        }else{
            holder.fileSize.setText(Formatter.formatShortFileSize(context, files.get(position).length()));
        }

//        Handling images for file types
        if(files.get(position).getName().toLowerCase().endsWith(".jpeg")){
            holder.fileType.setImageResource(R.drawable.ic_image);
        }else if (files.get(position).getName().toLowerCase().endsWith(".jpg")){
            holder.fileType.setImageResource(R.drawable.ic_image);
        }else if (files.get(position).getName().toLowerCase().endsWith(".png")){
            holder.fileType.setImageResource(R.drawable.ic_image);
        }else if (files.get(position).getName().toLowerCase().endsWith(".pdf")){
            holder.fileType.setImageResource(R.drawable.ic_pdf);
        }else if (files.get(position).getName().toLowerCase().endsWith(".doc")){
            holder.fileType.setImageResource(R.drawable.ic_docs);
        }else if (files.get(position).getName().toLowerCase().endsWith(".mp3")){
            holder.fileType.setImageResource(R.drawable.ic_music);
        }else if (files.get(position).getName().toLowerCase().endsWith(".wav")){
            holder.fileType.setImageResource(R.drawable.ic_music);
        }else if (files.get(position).getName().toLowerCase().endsWith(".mp4")){
            holder.fileType.setImageResource(R.drawable.ic_play);
        }else if (files.get(position).getName().toLowerCase().endsWith(".apk")){
            holder.fileType.setImageResource(R.drawable.ic_android);
        }else{
            holder.fileType.setImageResource(R.drawable.folder);
        }

        holder.itemContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onFileSelectedListener.onFileClicked(files.get(position));
            }
        });

        holder.itemContainer.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                onFileSelectedListener.onFileLongClicked(files.get(position), position);
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return files.size();
    }
}
class FileRecyclerViewHolder extends RecyclerView.ViewHolder{
    ImageView fileType;
    TextView fileName, fileSize;
    CardView itemContainer;

    public FileRecyclerViewHolder(@NonNull View itemView) {
        super(itemView);
        fileType = itemView.findViewById(R.id.item_file_type);
        fileName = itemView.findViewById(R.id.item_file_name);
        fileSize =itemView.findViewById(R.id.item_file_size);
        itemContainer = itemView.findViewById(R.id.file_container);
    }
}
