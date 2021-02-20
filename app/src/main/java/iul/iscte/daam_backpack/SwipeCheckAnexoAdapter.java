package iul.iscte.daam_backpack;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class SwipeCheckAnexoAdapter extends PagerAdapter {


    private ArrayList<String> images;
    private Context context;
    private LayoutInflater inflater;

    private FirebaseStorage storage;
    private StorageReference storageRef;
    private StorageReference imagesRef;

    private String NomeRegisto;

    public SwipeCheckAnexoAdapter(ArrayList<String> images, Context context, String nomeRegisto){
        this.context = context;
        this.images = images;
        this.NomeRegisto = nomeRegisto;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return images.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
    }

    @Override
    public Object instantiateItem(ViewGroup view, int position) {
        View myImageLayout = inflater.inflate(R.layout.swipe_check_anexo_layout, view, false);
        ImageView myImage = (ImageView) myImageLayout.findViewById(R.id.image);
        TextView myText = (TextView) myImageLayout.findViewById(R.id.swipeText);

        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        imagesRef = storageRef.child("FicheirosAnexados").child(NomeRegisto);

        StorageReference imageReference = imagesRef.child(images.get(position));
        String url = imageReference.getPath();

        GlideApp.with(view).load(imageReference).into(myImage);
        //Picasso.get().load(url).into(myImage);

        myText.setText(images.get(position));

        view.addView(myImageLayout, 0);
        return myImageLayout;
    }


    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

}
