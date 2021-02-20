package iul.iscte.daam_backpack;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public class UploadListAdapter extends RecyclerView.Adapter<UploadListAdapter.ImageViewHolder>{

    private ArrayList<ObjectInformation> images;

    public UploadListAdapter(ArrayList<ObjectInformation> images){
        this.images = images;
    }

    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.lista_anexar_ficheiro, parent, false);
        ImageViewHolder imageViewHolder = new ImageViewHolder(v);
        return imageViewHolder;
    }

    @Override
    public void onBindViewHolder(ImageViewHolder holder, int position) {
        ObjectInformation obj = images.get(position);
        String nomeFicheiro = obj.getNomeFicheiro();
        Uri uri = obj.getUri();
        holder.mFileImage.setImageURI(uri);
        holder.mFileName.setText(nomeFicheiro);
    }

    @Override
    public int getItemCount() {
        return images.size();
    }

    public static class ImageViewHolder extends RecyclerView.ViewHolder{

        View mView;

        public TextView mFileName;
        public ImageView mFileImage;


        public ImageViewHolder(View itemView) {
            super(itemView);

            mView = itemView;

            mFileName = (TextView) itemView.findViewById(R.id.fileName);
            mFileImage = (ImageView) itemView.findViewById(R.id.fileImage);

        }
    }
}
