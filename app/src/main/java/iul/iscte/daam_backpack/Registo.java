package iul.iscte.daam_backpack;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class Registo extends AppCompatActivity {

    private EditText username, useremail, userpassword, userpasswordagain, useruniversity, usercourse;
    private Button Registo;
    private FirebaseAuth firebaseAuth;
    private String user_name, user_password, user_university, user_course, user_email;
    private DB database;
    private DatabaseReference mFirebaseDatabase;
    private FirebaseDatabase mFirebaseInstance;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registo);
        setup();

        firebaseAuth = FirebaseAuth.getInstance();

        database = new DB(this);

        Registo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validate()) {
                    //regista na base de dados do firebase
                    user_password = userpassword.getText().toString().trim();
                    user_email = useremail.getText().toString().trim();
                    user_name = username.getText().toString().trim();
                    user_university = useruniversity.getText().toString().trim();
                    user_course = usercourse.getText().toString().trim();
                    firebaseAuth.createUserWithEmailAndPassword(user_email, user_password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
//                              sendUserData();
                                saveToDatabse();
                                database.insertUser(user_name, user_email, user_password, user_university);
                                Toast.makeText(Registo.this, "Sucesso", Toast.LENGTH_LONG);
                                startActivity(new Intent(Registo.this, HomePage.class));
                            } else {
                                if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                                    Toast.makeText(Registo.this, "Email ja existe. Por favor escolha um diferente", Toast.LENGTH_SHORT).show();
                                }
                                Toast.makeText(Registo.this, "Falhou", Toast.LENGTH_LONG);
                            }
                        }
                    });
                }
            }
        });

    }

    private void setup() {

        username = (EditText) findViewById(R.id.et_nome);
        userpassword = (EditText) findViewById(R.id.et_password);
        useremail = (EditText) findViewById(R.id.et_email);
        userpasswordagain = (EditText) findViewById(R.id.et_retype_password);
        useruniversity = (EditText) findViewById(R.id.et_university);
        usercourse = (EditText) findViewById(R.id.et_course);

        Registo = (Button) findViewById(R.id.btn_registo);

    }

    private boolean validate() {

        Boolean result = false;

        String name = username.getText().toString();
        String password = userpassword.getText().toString();
        String email = useremail.getText().toString();
        String passwordagain = userpasswordagain.getText().toString();

        if (name.isEmpty() || password.isEmpty() || email.isEmpty()) {
            Toast.makeText(this, "Existem campos não preenchidos", Toast.LENGTH_SHORT).show();
        } else if (password.length() < 8) {
            Toast.makeText(this, "Password tem que ter mais que 8 caracteres", Toast.LENGTH_SHORT).show();
        } else if (!password.equals(passwordagain)) {
            Toast.makeText(this, "Passwords não são iguais", Toast.LENGTH_SHORT).show();
        } else {
            result = true;
            //Toast.makeText(this, "Registo Feito", Toast.LENGTH_SHORT).show();
        }
        return result;
    }

//    private void sendUserData(){
//        String name = username.getText().toString();
//        String email = useremail.getText().toString();
//        String university = useruniversity.getText().toString();
//
//        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
//        DatabaseReference myRef = firebaseDatabase.getReference();
//        Utilizador userProfile = new Utilizador(name, email, university);
//        myRef.child("user").setValue(userProfile);
//    }

    private void saveToDatabse() {
        mFirebaseInstance = FirebaseDatabase.getInstance();

        // get reference to 'users'
        mFirebaseDatabase = mFirebaseInstance.getReference("users");

        // store app title to 'BackPack'
        mFirebaseInstance.getReference("title").setValue("BackPack");

        mFirebaseInstance.getReference("title").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String appTitle = dataSnapshot.getValue(String.class);

                // update toolbar title
                if (getSupportActionBar() != null) {
                    getSupportActionBar().setTitle(appTitle);
                }
                createUser(user_name, user_email, user_password, user_course, user_university);
            }

            @Override
            public void onCancelled(DatabaseError error) {
            }
        });
    }

    private void createUser(String name, String email, String password, String course, String university) {

        String key = email.replace(".", "").replace("@", "");
        userId = key;

        Utilizador user = new Utilizador(name, email, password, course, university);

        mFirebaseDatabase.child(userId).setValue(user);

    }

    @Override
    public void onBackPressed() {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
        finish();
    }

}
