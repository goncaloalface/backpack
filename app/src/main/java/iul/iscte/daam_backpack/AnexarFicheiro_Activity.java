package iul.iscte.daam_backpack;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class AnexarFicheiro_Activity extends MenuPage{

    private static final int GALLERY_INTENT = 1;
    private static final int CAMERA_INTENT = 2;


    public FirebaseDatabase database = FirebaseDatabase.getInstance();
    public DatabaseReference myRef = database.getReference();

    public FirebaseStorage storage = FirebaseStorage.getInstance();
    private StorageReference mStorageReference = storage.getReference();

    private ProgressDialog mProgressDialog;

    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<ObjectInformation> mListImageInformation;

    private ImageButton mCapturaButton;
    private ImageButton mGaleriaButton;


    //private FirebaseAuth auth;
    //private FirebaseUser user;
    //private String currentUser;

    private UploadListAdapter mUploadListAdapter;

    private EditText mNomeRegisto;
    private EditText mCadeiraRegisto;
    private EditText mUniversidadeRegisto;
    private Bitmap mFotoRegisto;

    private int totalFotos;

    private boolean fromCamera = false;

    private FirebaseUser user;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_anexar_ficheiro);
        mProgressDialog = new ProgressDialog(this);

        mGaleriaButton = (ImageButton) findViewById(R.id.galleryButton);

        mNomeRegisto = (EditText) findViewById(R.id.nomeRegisto);
        mCadeiraRegisto = (EditText) findViewById(R.id.cadeiraRegisto);
        mUniversidadeRegisto = (EditText) findViewById(R.id.universidadeRegisto);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        createListen();
        setupDrawer();

    }

    //método para guardar registo na base de dados
    public void saveRegisto(View view){
        String nome = mNomeRegisto.getText().toString();
        String cadeira =  mCadeiraRegisto.getText().toString();
        String universidade = mUniversidadeRegisto.getText().toString();
        if (nome.isEmpty() || cadeira.isEmpty() || universidade.isEmpty()){
            Toast.makeText(this, "Os campos têm que ser preenchidos!", Toast.LENGTH_SHORT).show();
        }else{
            Resumo resumo = new Resumo(nome, cadeira, universidade, totalFotos, user.getUid());
            myRef.child("Resumos").child(nome).setValue(resumo);
            Toast.makeText(this, "Resumo criado com sucesso!", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    //método para capturar imagens da camera
    public void capturarImagem(View view){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAMERA_INTENT);
    }

    //método para anexar imagens da galeria
    public void galeriaImagem(View view){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, GALLERY_INTENT);
    }

    //método chamado para anexar ficheiros
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK) {
            switch (requestCode){
                case GALLERY_INTENT:
                    if(data.getClipData()!= null) {
                        getGalleryData(data);
                    }else if(data.getData() != null){
                        getOneGalleryData(data);
                    }
                    break;
                case CAMERA_INTENT:
                    //getCameraData(data);
                    break;
            }
        }
    }

    //obter vários da galeria
    public void getGalleryData(Intent data){
        prepareGallery();

        final int totalItems = data.getClipData().getItemCount();
        totalFotos = totalItems;
        for (int i = 0; i < totalItems; i++) {
            Uri fileUri = data.getClipData().getItemAt(i).getUri();
            String fileName = mNomeRegisto.getText().toString() + "_" + i;
            mListImageInformation.add(new ObjectInformation(fileName, fileUri));

            StorageReference fileToUpload = mStorageReference.child("FicheirosAnexados").child(mNomeRegisto.getText().toString()).child(fileName);
            fileToUpload.putFile(fileUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(AnexarFicheiro_Activity.this, "Carregou " + totalItems + " imagens!", Toast.LENGTH_SHORT).show();
                    mProgressDialog.dismiss();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(AnexarFicheiro_Activity.this, "Ocorreu um erro!", Toast.LENGTH_SHORT).show();
                    mProgressDialog.dismiss();
                }
            });
        }
    }

    //obter um da galeria
    public void getOneGalleryData(Intent data){
        prepareGallery();

        totalFotos = 1;

        String fileName = mNomeRegisto.getText().toString() + "_0";

        Uri uri = data.getData();

        //ImageView mImageBitmap = (ImageView) findViewById(R.id.cameraImage);
        //TextView mTextBitmap = (TextView) findViewById(R.id.cameraText);

        //mTextBitmap.setText(fileName);
        //mImageBitmap.setImageURI(uri);

        mListImageInformation.add(new ObjectInformation(fileName, uri));

        StorageReference filePath = mStorageReference.child("FicheirosAnexados").child(mNomeRegisto.getText().toString()).child(fileName);
        filePath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(AnexarFicheiro_Activity.this, "Carregamento com sucesso!", Toast.LENGTH_LONG).show();
                mProgressDialog.dismiss();
            }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(AnexarFicheiro_Activity.this, "Ocorreu um erro!", Toast.LENGTH_SHORT).show();
                    mProgressDialog.dismiss();
                }
        });
    }

    //preparar anexar de galeria
    public void prepareGallery(){
        mProgressDialog.setMessage("A carregar...");
        mProgressDialog.show();

        layoutManager = new LinearLayoutManager(this);

        mRecyclerView = (RecyclerView) findViewById(R.id.listImages);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(layoutManager);

        mListImageInformation = new ArrayList<ObjectInformation>();
        mUploadListAdapter = new UploadListAdapter(mListImageInformation);
        mRecyclerView.setAdapter(mUploadListAdapter);
    }

    //obter dados da camera
    /*public void getCameraData(Intent data){

        mProgressDialog.setMessage("A carregar...");
        mProgressDialog.show();

        mGaleriaButton.setEnabled(false);

        Bundle extras = data.getExtras();
        Bitmap imageBitmap = (Bitmap) extras.get("data");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);



        ImageView mImageBitmap = (ImageView) findViewById(R.id.cameraImage);
        TextView mTextBitmap = (TextView) findViewById(R.id.cameraText);
        mFotoRegisto = imageBitmap;

        fromCamera = true;

        mTextBitmap.setText(mNomeRegisto.getText().toString() + "_foto");
        mImageBitmap.setImageBitmap(imageBitmap);
        mProgressDialog.dismiss();
    }*/


}

