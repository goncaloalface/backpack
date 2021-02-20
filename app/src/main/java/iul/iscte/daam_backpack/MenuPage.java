package iul.iscte.daam_backpack;

import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.Serializable;


public class MenuPage extends AppCompatActivity {

    private DrawerLayout dl;
    private ActionBarDrawerToggle t;
    private NavigationView nv;
    private String userEmail;
    private TextView tvnome;
    private String nome;
    private View listHeaderView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void createListen() {
        ActionBar actionBar = getSupportActionBar();

        dl = (DrawerLayout) findViewById(R.id.drawer_layout);
        dl.addDrawerListener(t);
        nv = (NavigationView) findViewById(R.id.nv_view);
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }

        LayoutInflater inflater = getLayoutInflater();

        listHeaderView = inflater.inflate(R.layout.header_layout, null, false);

        userEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail().toString();

        String emailUser = userEmail.replace(".", "").replace("@", "");
        String fileName = emailUser + "_0";
        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("ImagemPerfil").child(fileName);
        loadName();

        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                boolean i = true;

                ImageView imageView = (ImageView) findViewById(R.id.nv_image);

                if (imageView != null) {

                    Picasso.get().load(uri)
                            .resize(imageView.getWidth(), imageView.getHeight())
                            .into(imageView, new Callback() {
                                @Override
                                public void onSuccess() {
                                }

                                @Override
                                public void onError(Exception e) {
                                }
                            });
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {

            }
        });

        nv.addHeaderView(listHeaderView);

//Define para que atividade

        nv.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                switch (id) {
                    case R.id.nav_home:
                        startActivity(new Intent(getApplicationContext(), HomePage.class));
                        finish();
                        break;
                    case R.id.nav_account:
                        startActivity(new Intent(getApplicationContext(), Account_Activity.class));
                        finish();
                        break;

                    case R.id.nav_accountSummaries:
                        startActivity(new Intent(getApplicationContext(), AccountSumaries_Activity.class));
                        finish();
                        break;

                    case R.id.nav_accountGroups:
                        startActivity(new Intent(getApplicationContext(), AccountGroups_Activity.class));
                        finish();
                        break;

                    case R.id.nav_settings:
                        startActivity(new Intent(getApplicationContext(), Settings_Activity.class));
                        finish();
                        break;

                    case R.id.nav_logout:
                        FirebaseAuth.getInstance().signOut();
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        finish();
                        break;
                }
                return false;
            }
        });

    }

    public void loadName() {

        tvnome = (TextView) listHeaderView.findViewById(R.id.header_nome);

        if (tvnome != null) {

            DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
            ref.child("users").orderByChild("email").addListenerForSingleValueEvent(new ValueEventListener() {

                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        Utilizador u = ds.getValue(Utilizador.class);
                        if (u.getEmail().equals(userEmail)) {
                            String nome = u.getNome();
                            tvnome.setText(nome);

                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }

            });
        }
    }

    public void loadImageNav(Uri uri) {

        ImageView imageView = (ImageView) findViewById(R.id.nv_image);
        if (imageView != null) {
            Picasso.get().load(uri)
                    .resize(imageView.getWidth(), imageView.getHeight())
                    .into(imageView, new Callback() {
                        @Override
                        public void onSuccess() {
                        }

                        @Override
                        public void onError(Exception e) {
                        }
                    });
        }
    }

    public void setupDrawer() {

        t = new ActionBarDrawerToggle(this, dl, R.string.drawer_open, R.string.drawer_close) {

            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }


            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
        t.setDrawerIndicatorEnabled(true);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        t.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        t.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (t.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle your other action bar items...

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
    }

}
