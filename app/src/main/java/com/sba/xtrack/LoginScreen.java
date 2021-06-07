package com.sba.xtrack;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
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

public class LoginScreen extends AppCompatActivity {

    EditText loginEmail,loginPassword;
    Button loginButton;
    TextView forgotPasswordTextView;
    private FirebaseAuth firebaseAuth;
    FirebaseUser user;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        /*

        if(user != null)
        {
            Intent intent = new Intent(LoginScreen.this, LoggedOnScreen.class);
            startActivity(intent);
            finish();

        }*/

        setContentView(R.layout.activity_login_screen);

        loginButton = (Button) findViewById(R.id.loginButton);
        loginEmail = (EditText) findViewById(R.id.loginEmail);
        loginPassword = (EditText) findViewById(R.id.loginPassword);

        forgotPasswordTextView = (TextView) findViewById(R.id.forgotPasswordTextView);

        forgotPasswordTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(LoginScreen.this,ResetPassword.class);
                startActivity(intent);
            }
        });

        ActivityCompat.requestPermissions(LoginScreen.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);



        progressDialog = new ProgressDialog(this);



        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                String Email,Password;

                Email = loginEmail.getText().toString().trim();
                Password = loginPassword.getText().toString().trim();

                if(Email.isEmpty() || Password.isEmpty())
                {
                    if(Email.isEmpty())
                    {
                        Toast.makeText(LoginScreen.this,"Email field is empty!",Toast.LENGTH_SHORT).show();
                    }
                    if(Password.isEmpty())
                    {
                        Toast.makeText(LoginScreen.this,"Password filed is empty!",Toast.LENGTH_SHORT).show();
                    }
                    return;
                }
                else
                {
                    progressDialog.setMessage("Logging User...");
                    progressDialog.show();
                    firebaseAuth.signInWithEmailAndPassword(Email,Password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task)
                                {
                                    if(task.isSuccessful())
                                    {
                                        if(firebaseAuth.getCurrentUser().isEmailVerified())
                                        {
                                            Intent intent = new Intent(LoginScreen.this, LoggedOnScreen.class);
                                            startActivity(intent);
                                            finish();
                                            Intent service = new Intent(LoginScreen.this,BackgroundService.class);
                                            startService(service);
                                        }
                                        else
                                        {
                                            Toast.makeText(LoginScreen.this,"Please Varify Email address!",Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                    else
                                    {
                                        Toast.makeText(LoginScreen.this,task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                                    }
                                    progressDialog.hide();


                                }
                            });
                }



            }
        });

    }

}
