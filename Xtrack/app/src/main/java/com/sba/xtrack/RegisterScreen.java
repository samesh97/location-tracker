package com.sba.xtrack;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

public class RegisterScreen extends AppCompatActivity implements View.OnClickListener {

    EditText email,password,confirmPassword,fullName;
    Button submit;
    TextView loginLink;

    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;
    FirebaseUser user;
    String userId;

    @Override
    protected void onStart() {
        super.onStart();
        if(firebaseAuth.getCurrentUser() != null)
        {
            Intent intent = new Intent(RegisterScreen.this, LoggedOnScreen.class);
            startActivity(intent);
            finish();
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_screen);

        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();





        progressDialog = new ProgressDialog(this);


        email = (EditText) findViewById(R.id.email);

        confirmPassword = (EditText) findViewById(R.id.confirmPassword);

        fullName = (EditText) findViewById(R.id.fullName);


        password = (EditText) findViewById(R.id.password);
        submit = (Button) findViewById(R.id.loginButton);

        loginLink = (TextView) findViewById(R.id.loginLink);

        submit.setOnClickListener(this);
        loginLink.setOnClickListener(this);

    }
    private void registerUser()
    {


        email = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);


        confirmPassword = (EditText) findViewById(R.id.confirmPassword);
        fullName = (EditText) findViewById(R.id.fullName);

        final String ConfirmPassword = confirmPassword.getText().toString();
        final String FullName = fullName.getText().toString();

        final String Email = email.getText().toString().trim();
        final String Password = password.getText().toString();



        if(!ConfirmPassword.equals(Password))
        {
            Toast.makeText(RegisterScreen.this,"Password was not matched!",Toast.LENGTH_SHORT).show();
            return;
        }

        Date myDate = new Date();
        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss a", Locale.getDefault());
        final String date = format1.format(myDate);

        Random random = new Random();
        int n = 100000 + random.nextInt(900000);
        final String code = String.valueOf(n);

        final String isSharing = "false";

        if(Email.isEmpty())
        {
            Toast.makeText(RegisterScreen.this,"Email is empty",Toast.LENGTH_SHORT).show();
            return;
        }
        if(Password.isEmpty())
        {
            Toast.makeText(RegisterScreen.this,"Password field is empty",Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog.setMessage("Creating Account..");
        progressDialog.show();

        firebaseAuth.createUserWithEmailAndPassword(Email,Password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task)
            {
                if(task.isSuccessful())
                {


                    User user = new User(FullName,Email,code,date,isSharing,firebaseAuth.getCurrentUser().getUid());

                    FirebaseDatabase.getInstance().getReference("Users")
                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if(task.isSuccessful())
                            {

                                //Toast.makeText(RegisterScreen.this,"Saved Data!",Toast.LENGTH_SHORT).show();

                                firebaseAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful())
                                        {
                                            firebaseAuth.signOut();
                                            Toast.makeText(RegisterScreen.this,"Successfuly Registered!,Varification email was sent!",Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(RegisterScreen.this, LoginScreen.class);
                                            startActivity(intent);
                                        }
                                        else
                                        {
                                            Toast.makeText(RegisterScreen.this,task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                            else
                            {
                                Toast.makeText(RegisterScreen.this,"Failed to Save Data!",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                    progressDialog.hide();
                    //Toast.makeText(RegisterScreen.this,"Successfully Registered!",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    progressDialog.hide();
                    Toast.makeText(RegisterScreen.this,task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onClick(View v)
    {
        if(v == submit)
        {
            registerUser();
        }
        if(v == loginLink)
        {
            Intent intent = new Intent(RegisterScreen.this,LoginScreen.class);
            startActivity(intent);
            
        }


    }

}

