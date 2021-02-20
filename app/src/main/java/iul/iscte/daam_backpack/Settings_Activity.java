package iul.iscte.daam_backpack;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Settings_Activity extends MenuPage {
    private String[] listItems;

    private FirebaseDatabase db;
    private DatabaseReference ref;
    private AlertDialog alertDialog;
    private View promptsView;
    private String variableToChange;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_);
        getSupportActionBar().setTitle("Definições");
        createListen();
        setupDrawer();

        setupList();

    }

    private void setupList() {
        listItems = new String[]{
                "Nome",
                "Curso",
                "Universidade",

        };
        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, listItems);
        ListView mListView = (ListView) findViewById(R.id.lv_view);
        mListView.setAdapter(adapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = (String) parent.getItemAtPosition(position);
                setVariableToChange(selectedItem);
                getData();
            }
        });
    }

    private void setVariableToChange(String info) {
        switch (info) {
            case "Nome":
                variableToChange = "nome";
                break;
            case "Curso":
                variableToChange = "course";
                break;
            case "Universidade":
                variableToChange = "university";
                break;

        }

    }

    private void getData() {
        final Context c = Settings_Activity.this;
        final String[] actualValue = {""};

        promptsView = LayoutInflater.from(c).inflate(R.layout.popup_settings, null);
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(new ContextThemeWrapper(c, R.style.myDialog));
        alertDialogBuilder.setView(promptsView);

        db = FirebaseDatabase.getInstance();
        ref = db.getReference();
        ref.child("users").orderByChild("email").addListenerForSingleValueEvent(new ValueEventListener() {
            String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Utilizador u = ds.getValue(Utilizador.class);

                    final EditText editTPop = (EditText) promptsView.findViewById(R.id.popET);


                    if (u.getEmail().equals(email)) {


                        final DataSnapshot tempDS = ds;

                        switch (variableToChange) {
                            case "nome":
                                actualValue[0] = u.getNome();
                                break;
                            case "course":
                                actualValue[0] = u.getCourse();
                                break;
                            case "university":
                                actualValue[0] = u.getUniversity();
                                break;

                        }
                        alertDialog = alertDialogBuilder.create();
                        editTPop.append("" + actualValue[0]);





                    /*    alertDialogBuilder
                                .setCancelable(false)
                                .setPositiveButton("OK",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {


                                            }
                                        }
                                )
                                .

                                        setNegativeButton("Cancel",
                                                new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog,
                                                                        int id) {
                                                        dialog.cancel();
                                                    }
                                                });
*/
                        if (promptsView.getParent() != null) {
                            ((ViewGroup) promptsView.getParent()).removeView(promptsView);
                        }
                        alertDialog.show();


                    }
                }
            }


            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });
    }

    public void OnclickOK(View view) {
        String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        EditText editTPop = (EditText) promptsView.findViewById(R.id.popET);
        String response = editTPop.getText().toString();
        String key = email.replace(".", "").replace("@", "");
        ref.child("users").child(key).child(variableToChange).setValue(response);
        alertDialog.cancel();
        loadName();

    }

    public void OnclickCancelar(View view) {

        alertDialog.cancel();

    }


}


