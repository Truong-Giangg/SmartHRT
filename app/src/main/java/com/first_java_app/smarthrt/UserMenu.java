package com.first_java_app.smarthrt;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class UserMenu extends AppCompatActivity {
    TextInputLayout fullName, email, phoneNo, password;
    TextView fullNameLabel, userNameLabel;

    // Global variable to hold user data inside this acctivity
    String _USERNAME, _NAME, _EMAIL, _PHONENO, _PASSWORD, user_username;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_menu);

        reference = FirebaseDatabase.getInstance().getReference("users");

        fullName = findViewById(R.id.full_name_profile);
        email = findViewById(R.id.email_profile);
        phoneNo = findViewById(R.id.phone_no_profile);
        password = findViewById(R.id.password_profile);
        fullNameLabel = findViewById(R.id.fullname_field);
        userNameLabel = findViewById(R.id.username_field);

        showAllUserData();

    }

    private void showAllUserData() {
        Intent intent =getIntent();
        user_username =intent.getStringExtra("username");
        String user_name =intent.getStringExtra("name");
        String user_email =intent.getStringExtra("email");
        String user_phoneNo =intent.getStringExtra("phoneNo");
        String user_password =intent.getStringExtra("password");




        fullNameLabel.setText(user_name);
        userNameLabel.setText(user_username);
        fullName.getEditText().setText(user_name);
        email.getEditText().setText(user_email);
        phoneNo.getEditText().setText(user_phoneNo);
        password.getEditText().setText(user_password);

    }

    public void update(View view){
        if (isPasswordChanged()){
            Toast.makeText(this, "Data has been updated", Toast.LENGTH_SHORT).show();

        }
        else{
            Toast.makeText(this, "Data is same and can not be updated", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isPasswordChanged(){
        if(!_PASSWORD.equals(password.getEditText().getText().toString())){
         //   reference.child(_USERNAME).child("password").setValue(password.getEditText().getText().toString());
        //    _PASSWORD=password.getEditText().getText().toString();
            return true;
        }
        else{
            return false;
        }

    }

    private boolean isNameChanged(){
        if(_NAME.equals(fullName.getEditText().getText().toString())){
            reference.child(_USERNAME).child("name").setValue(fullName.getEditText().getText().toString());
            return true;
        }
        else{
            return false;
        }
    }

    public void continue123(View view){
        //--------------push data to MainMenu acctivity via username------------
        Intent intent =new Intent(getApplicationContext(),MainMenu.class);
        intent.putExtra("username",user_username);
        startActivity(intent);
        //--------------end push data to MainMenu acctivity via username------------
//        Intent intent =new Intent(UserMenu.this,MainMenu.class);
//        startActivity(intent);
    }
}

