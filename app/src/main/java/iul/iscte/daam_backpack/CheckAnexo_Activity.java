package iul.iscte.daam_backpack;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.ViewParent;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;

import org.w3c.dom.Text;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

import me.relex.circleindicator.CircleIndicator;

public class CheckAnexo_Activity extends MenuPage {

    private String nomeRegisto;
    private String cadeiraRegisto;
    private String universidadeRegisto;
    private int totalFotosRegisto;

    private TextView mNome;
    private TextView mCadeira;
    private TextView mUniversidade;

    private ArrayList<String> images = new ArrayList<String>();
    private ViewPager mPager;
    private SwipeCheckAnexoAdapter adapter;

    private FirebaseStorage storage;
    private StorageReference storageRef;
    private StorageReference imagesRef;
    private ProgressDialog mProgressDialog;

    private FileOutputStream outputStream;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_anexo);

        mProgressDialog = new ProgressDialog(this);

        mPager = (ViewPager) findViewById(R.id.pager);

        getIncomingIntent();
        fillImagesArray();

        createListen();
        setupDrawer();

    }

    public void fillImagesArray(){
        for(int i = 0; i < totalFotosRegisto; i++){
            String imagemReferente = nomeRegisto + "_" + i;
            images.add(imagemReferente);
        }

        mPager.setAdapter(new SwipeCheckAnexoAdapter(images, this, nomeRegisto));
        CircleIndicator indicator = (CircleIndicator) findViewById(R.id.indicator);
        indicator.setViewPager(mPager);

    }

    public void getIncomingIntent(){
        if(getIntent().hasExtra("nomeResumo") && getIntent().hasExtra("cadeiraResumo") && getIntent().hasExtra("universidadeResumo") && getIntent().hasExtra("totalFotos")){
            nomeRegisto = getIntent().getStringExtra("nomeResumo");
            cadeiraRegisto = getIntent().getStringExtra("cadeiraResumo");
            universidadeRegisto = getIntent().getStringExtra("universidadeResumo");
            totalFotosRegisto = Integer.parseInt(getIntent().getStringExtra("totalFotos"));

            mNome = (TextView) findViewById(R.id.nomeRegistoLabel);
            mCadeira = (TextView) findViewById(R.id.cadeiraRegistoLabel);
            mUniversidade = (TextView) findViewById(R.id.universidadeRegistoLabel);

            mNome.setText(nomeRegisto);
            mCadeira.setText(cadeiraRegisto);
            mUniversidade.setText(universidadeRegisto);
        }
    }


    public void downloadResumo(View view){
        mProgressDialog.setMessage("A carregar...");
        mProgressDialog.show();

        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        imagesRef = storageRef.child("FicheirosAnexados").child(nomeRegisto);



        for(int i = 0; i < totalFotosRegisto; i++){
            StorageReference imageReference = imagesRef.child(images.get(i));
            addImageToGallery(imageReference.getPath(),CheckAnexo_Activity.this);

            //new DownloadImage().execute(imageReference.getDownloadUrl().toString());


        }

        mProgressDialog.dismiss();

    }

    public static void addImageToGallery(final String filePath, final Context context) {

        ContentValues values = new ContentValues();

        values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        values.put(MediaStore.MediaColumns.DATA, filePath);

        context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
    }

}
