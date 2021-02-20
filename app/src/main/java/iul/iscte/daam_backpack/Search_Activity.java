package iul.iscte.daam_backpack;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class Search_Activity extends MenuPage {

    private RadioGroup radioGroup;

    private EditText searchText;
    private ImageButton searchButton;

    public FirebaseDatabase database = FirebaseDatabase.getInstance();
    public DatabaseReference myRef = database.getReference().child("Resumos");

    private ArrayList<Resumo> listaResumos;
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager layoutManager;

    private ListAnexoAdapter mAnexoListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_);

        radioGroup = (RadioGroup) findViewById(R.id.radioGroup);

        searchText = (EditText) findViewById(R.id.searchText);
        searchButton = (ImageButton) findViewById(R.id.searchButton);

        radioGroup.check(R.id.radioResumo);

        layoutManager = new LinearLayoutManager(this);

        mRecyclerView = (RecyclerView) findViewById(R.id.allAnexos);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(layoutManager);

        listaResumos = new ArrayList<Resumo>();

        fillAnexosList();

        createListen();
        setupDrawer();
    }

    public void fillAnexosListFiltered(View view) {
        String filterChoice = checkRadioGroup();

        String filter = searchText.getText().toString();

        layoutManager = new LinearLayoutManager(this);

        mRecyclerView = (RecyclerView) findViewById(R.id.allAnexos);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(layoutManager);

        if (!filter.isEmpty()) {

            myRef.orderByChild(filterChoice).equalTo(filter).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    listaResumos = new ArrayList<Resumo>();
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        Resumo resumo = postSnapshot.getValue(Resumo.class);
                        listaResumos.add(resumo);
                    }

                    mAnexoListAdapter = new ListAnexoAdapter(listaResumos);
                    mRecyclerView.setAdapter(mAnexoListAdapter);

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Toast.makeText(Search_Activity.this, "Ocorreu um erro!", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    public void fillAnexosList() {
        ValueEventListener postListener = new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Resumo resumo = postSnapshot.getValue(Resumo.class);
                    listaResumos.add(resumo);
                }

                mAnexoListAdapter = new ListAnexoAdapter(listaResumos);
                mRecyclerView.setAdapter(mAnexoListAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(Search_Activity.this, "Ocorreu um erro!", Toast.LENGTH_SHORT).show();
            }
        };

        myRef.addValueEventListener(postListener);
    }

    public String checkRadioGroup() {
        int selectedRadio = radioGroup.getCheckedRadioButtonId();

        String selectedOption = "nome";
        switch (selectedRadio) {
            case R.id.radioResumo:
                selectedOption = "nome";
                break;
            case R.id.radioCadeira:
                selectedOption = "cadeira";
                break;
            case R.id.radioUniversidade:
                selectedOption = "universidade";
                break;
        }

        return selectedOption;

    }


    @Override
    public void onBackPressed() {
        startActivity(new Intent(getApplicationContext(), HomePage.class));
    }

}
