package iul.iscte.daam_backpack;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

public class HomePage extends MenuPage {

    private RecyclerView recyclerViewRecent;
    private RecyclerView recyclerViewPopular;

    private ListAnexoAdapter mRecentListAdapter;
    private ListAnexoAdapter mPopularListAdapter;

    private ArrayList<Resumo> mListRecentAnexos;
    private ArrayList<Resumo> mListPopularAnexos;

    private RecyclerView.LayoutManager layoutManagerRecent;
    private RecyclerView.LayoutManager layoutManagerPopular;

    public FirebaseDatabase database = FirebaseDatabase.getInstance();
    public DatabaseReference myRef = database.getReference().child("Resumos");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        getSupportActionBar().setTitle("Pagina Principal");

        prepareRecyclerViews();
        populateRecentList();
        populatePopularList();

        createListen();
        setupDrawer();
    }

    public void prepareRecyclerViews(){
        layoutManagerRecent = new LinearLayoutManager(this);
        layoutManagerPopular = new LinearLayoutManager(this);

        recyclerViewRecent = (RecyclerView) findViewById(R.id.recentList);
        recyclerViewRecent.setHasFixedSize(true);
        recyclerViewRecent.setLayoutManager(layoutManagerRecent);

        recyclerViewPopular = (RecyclerView) findViewById(R.id.popularList);
        recyclerViewPopular.setHasFixedSize(true);
        recyclerViewPopular.setLayoutManager(layoutManagerPopular);

        mListRecentAnexos = new ArrayList<Resumo>();
        mListPopularAnexos = new ArrayList<Resumo>();
    }

    public void populateRecentList(){


        myRef.orderByChild("dataPublicacao").limitToFirst(5).addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    Resumo resumo = postSnapshot.getValue(Resumo.class);
                    mListRecentAnexos.add(resumo);
                }
                Collections.reverse(mListRecentAnexos);
                mRecentListAdapter = new ListAnexoAdapter(mListRecentAnexos);
                recyclerViewRecent.setAdapter(mRecentListAdapter);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void populatePopularList(){

        Query pupularAnexos = myRef.orderByChild("acessos").limitToFirst(5);
        pupularAnexos.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    Resumo resumo = postSnapshot.getValue(Resumo.class);
                    mListPopularAnexos.add(resumo);
                }
                Collections.reverse(mListPopularAnexos);
                mPopularListAdapter = new ListAnexoAdapter(mListPopularAnexos);
                recyclerViewPopular.setAdapter(mPopularListAdapter);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void goToSearch(View view){
        startActivity(new Intent(getApplicationContext(), Search_Activity.class));
    }


}
