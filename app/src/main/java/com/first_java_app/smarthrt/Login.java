package com.first_java_app.smarthrt;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class Login extends AppCompatActivity {
    Button callSignUp ,login_btn;
    ImageView image;
    TextView logoText,sloganText;
    TextInputLayout username,password;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);
        //hoook
        callSignUp = findViewById(R.id.signup_screen);

        image =findViewById(R.id.logo_image);
        logoText =findViewById(R.id.logo_name);
        sloganText =findViewById(R.id.logo_signin);
        username =findViewById(R.id.username);
        password=findViewById(R.id.password);
        login_btn =findViewById(R.id.loginBtn);

        callSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(Login.this,SignUp.class);

                startActivity(intent);
                finish();
            }
        });

    }

    private Boolean validateUserName(){
        String val =username.getEditText().getText().toString();
        //String nowhiteSpace = "(?=\\s+$)";  //  \A\w{4,20}\z

        if(val.isEmpty()) {
            username.setError("cannot be empty");
            return false;
        }

        else{
            username.setError(null);
            username.setErrorEnabled(false);
            return true;
        }

    }
    private Boolean validatePassword(){
        String val =password.getEditText().getText().toString();
        // String passwordVal = "^"+".{4,}"+"$";
        if(val.isEmpty()) {
            password.setError("cannot be empty");
            return false;
        }

        else{
            password.setError(null);
            password.setErrorEnabled(false);
            return true;
        }

    }

    public void LoginUser(View view){
        if(!validateUserName()){
            validateUserName();
            return;
        }

        else {
            if (!validatePassword()) {
                validatePassword();
                return;
            }
            else{
                isUser();
            }
        }
    }

    private void isUser() {
        String userEnteredUsername = username.getEditText().getText().toString().trim();
        String userEnteredPassword = password.getEditText().getText().toString().trim();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");

        Query checkUser=reference.orderByChild("username").equalTo(userEnteredUsername);

        checkUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){

                    username.setError(null);
                    username.setErrorEnabled(false);

                    String passwordFromDB = dataSnapshot.child(userEnteredUsername).child("password").getValue(String.class);
                                                // tro toi urename va tim password
                    if(passwordFromDB.equals(userEnteredPassword)){

                        username.setError(null);
                        username.setErrorEnabled(false);

                        String nameFromDB = dataSnapshot.child(userEnteredUsername).child("name").getValue(String.class);
                        String usernameFromDB = dataSnapshot.child(userEnteredUsername).child("username").getValue(String.class);
                        String phoneNoFromDB = dataSnapshot.child(userEnteredUsername).child("phoneNo").getValue(String.class);
                        String emailFromDB = dataSnapshot.child(userEnteredUsername).child("email").getValue(String.class);

                        Intent intent =new Intent(getApplicationContext(),UserMenu.class);

                        intent.putExtra("name",nameFromDB);
                        intent.putExtra("username",usernameFromDB);
                        intent.putExtra("email",emailFromDB);
                        intent.putExtra("phoneNo",phoneNoFromDB);
                        intent.putExtra("password",passwordFromDB);

                        startActivity(intent);
                    }
                    else {
                        password.setError("Wrong Password");
                        username.requestFocus();
                    }
                }
                else {
                    username.setError("No such user exit");
                    username.requestFocus();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }



}