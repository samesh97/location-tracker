package com.sba.xtrack;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ResetPassword extends AppCompatActivity {

    EditText email;
    Button resetButton;
    ProgressDialog progressDialog;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        email = (EditText) findViewById(R.id.emailAddress);
        resetButton = (Button) findViewById(R.id.resetPasswordButton);

        firebaseAuth =  FirebaseAuth.getInstance();

        progressDialog = new ProgressDialog(this);

        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                email = (EditText) findViewById(R.id.emailAddress);
                progressDialog.setMessage("Sending Varification Code..");
                progressDialog.show();
                String Email = email.getText().toString();

                if(Email.isEmpty())
                {
                    return;
                }

                firebaseAuth.sendPasswordResetEmail(Email).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task)
                    {
                        if(task.isSuccessful())
                        {
                            Toast.makeText(ResetPassword.this,"Varification Code was sent!",Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            Toast.makeText(ResetPassword.this,task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                        }
                        progressDialog.hide();

                    }
                });

            }
        });
    }
}
