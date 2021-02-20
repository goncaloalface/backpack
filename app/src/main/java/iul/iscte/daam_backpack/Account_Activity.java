package iul.iscte.daam_backpack;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;


public class Account_Activity extends MenuPage {

    private TextView name, email2, university, course;
    private FirebaseDatabase db;
    private DatabaseReference ref;

    private static final int GALLERY_INTENT = 1;

    private ImageButton mGaleriaButton;
    private ProgressDialog mProgressDialog;

    public FirebaseStorage storage = FirebaseStorage.getInstance();
    private StorageReference mStorageReference = storage.getReference();

    private boolean fromCamera = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_);
        getSupportActionBar().setTitle("A Minha Conta");
        createListen();
        setupDrawer();

        name = findViewById(R.id.nameTV);
        email2 = findViewById(R.id.emailTV);
        course = findViewById(R.id.courseTV);
        university = findViewById(R.id.univerityTV);

        mGaleriaButton = findViewById(R.id.accountIB);
        mProgressDialog = new ProgressDialog(this);

        db = FirebaseDatabase.getInstance();
        ref = db.getReference();


        ref.child("users").orderByChild("email").addValueEventListener(new ValueEventListener() {
            String email = FirebaseAuth.getInstance().getCurrentUser().getEmail().toString();

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Utilizador u = ds.getValue(Utilizador.class);
                    if (u.getEmail().equals(email)) {
                        name.setText(u.getNome());
                        email2.setText(u.getEmail());
                        course.setText(u.getCourse());
                        university.setText(u.getUniversity());
                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        String email = FirebaseAuth.getInstance().getCurrentUser().getEmail().toString();
        String emailUser = email.replace(".", "").replace("@", "");
        String fileName = emailUser + "_0";
        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("ImagemPerfil").child(fileName);

        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                loadImage(uri);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {

            }
        });
    }

    private void loadImage(Uri uri) {
        Picasso.get().load(uri)
                .resize(mGaleriaButton.getWidth(), mGaleriaButton.getHeight())
                .into(mGaleriaButton, new Callback() {
                    @Override
                    public void onSuccess() {
                    }

                    @Override
                    public void onError(Exception e) {
                    }
                });
        loadImageNav(uri);
    }

    private Bitmap getImageBitmap(String url) {
        Bitmap bm = null;
        try {
            URL aURL = new URL(url);
            URLConnection conn = aURL.openConnection();
            conn.connect();
            InputStream is = conn.getInputStream();
            BufferedInputStream bis = new BufferedInputStream(is);
            bm = BitmapFactory.decodeStream(bis);
            bis.close();
            is.close();
        } catch (IOException e) {
        }
        return bm;
    }

    //método chamado para anexar ficheiros
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case GALLERY_INTENT:
                    getOneGalleryData(data);
                    break;
            }
        }
    }

    //método para anexar imagens da galeria
    public void changeImage(View view) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, GALLERY_INTENT);
    }

    //obter uma imagem da galeria
    public void getOneGalleryData(Intent data) {
        //totalFotos = 1;
        String email = FirebaseAuth.getInstance().getCurrentUser().getEmail().toString();
        String emailUser = email.replace(".", "").replace("@", "");
        String fileName = emailUser + "_0";

        final Uri uri = data.getData();


        StorageReference filePath = mStorageReference.child("ImagemPerfil").child(fileName);
        filePath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(Account_Activity.this, "Carregamento com sucesso!", Toast.LENGTH_LONG).show();
                mProgressDialog.dismiss();
               loadImage(uri);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Account_Activity.this, "Ocorreu um erro!", Toast.LENGTH_SHORT).show();
                mProgressDialog.dismiss();
            }
        });

    }


}