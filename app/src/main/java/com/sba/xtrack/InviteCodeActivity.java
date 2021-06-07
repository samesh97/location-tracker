package com.sba.xtrack;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class InviteCodeActivity extends AppCompatActivity
{
    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    DatabaseReference databaseReference;
    TextView inviteCode;
    String code;
    Button copyButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite_code);

        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        inviteCode = (TextView) findViewById(R.id.inviteCode);

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                code = dataSnapshot.child(user.getUid()).child("inviteCode").getValue(String.class);
                inviteCode.setText(code);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            {

            }
        });

        copyButton = (Button) findViewById(R.id.CopyButton);
        copyButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(code != null)
                {
                    ClipboardManager clipboard = (ClipboardManager)   getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText("Code", code);
                    clipboard.setPrimaryClip(clip);
                    Toast.makeText(InviteCodeActivity.this,"Copied!",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(InviteCodeActivity.this,"An Error Occured!!",Toast.LENGTH_SHORT).show();
                }

            }
        });



    }
}
