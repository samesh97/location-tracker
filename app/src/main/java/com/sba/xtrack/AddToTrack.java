package com.sba.xtrack;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.goodiebag.pinview.Pinview;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;


public class AddToTrack extends AppCompatActivity {

    Pinview pinview;
    DatabaseReference reference,currentReference;
    DatabaseReference AddToTrackReference;
    DatabaseReference WhoTracksMeReference;
    FirebaseUser user;
    FirebaseAuth firebaseAuth;
    String currentUserId,JoinedUserId;
    DatabaseReference inviteCodeReference;

    String CurrentUsersInviteCode,PinViewString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_to_track);
        firebaseAuth = FirebaseAuth.getInstance();

        user = firebaseAuth.getCurrentUser();


        pinview = (Pinview) findViewById(R.id.pinView);

        reference = FirebaseDatabase.getInstance().getReference().child("Users");
        currentReference = FirebaseDatabase.getInstance().getReference().child("Users").child(user.getUid());

        /*inviteCodeReference = FirebaseDatabase.getInstance().getReference().child("Users").child(user.getUid()).child("inviteCode");

*/

        currentUserId = user.getUid();


    }
    public void submit(View view)
    {
        Query query = reference.orderByChild("inviteCode").equalTo(pinview.getValue());
        PinViewString = pinview.getValue();
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    CurrentUsersInviteCode = dataSnapshot.child(user.getUid()).child("inviteCode").getValue(String.class);

                    if(PinViewString.equals(CurrentUsersInviteCode))
                    {
                        Toast.makeText(AddToTrack.this,"You cannot use your own invite code!",Toast.LENGTH_SHORT).show();
                        return;
                    }


                    User createUser = null;
                    for(DataSnapshot childDss : dataSnapshot.getChildren())
                    {

                        createUser = childDss.getValue(User.class);
                        JoinedUserId = createUser.userId;

                        AddToTrackReference = FirebaseDatabase.getInstance().getReference().child("Users")
                                .child(firebaseAuth.getCurrentUser().getUid()).child("JoinedMembers");



                        JoinedMembers currentUser = new JoinedMembers(firebaseAuth.getCurrentUser().getUid());
                        JoinedMembers joinedUsers = new JoinedMembers(JoinedUserId);

                        AddToTrackReference.child(JoinedUserId).setValue(joinedUsers)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task)
                                    {
                                        if(task.isSuccessful())
                                        {
                                            WhoTracksMeReference = FirebaseDatabase.getInstance().getReference().child("Users")
                                                    .child(JoinedUserId).child("WhoTracksMe");
                                            WhoTracksMe whoTracksMe  = new WhoTracksMe(firebaseAuth.getCurrentUser().getUid());

                                            WhoTracksMeReference.child(user.getUid()).setValue(whoTracksMe)
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task)
                                                        {
                                                            if(task.isSuccessful())
                                                            {
                                                                Toast.makeText(AddToTrack.this,"User Joined Successfully!",Toast.LENGTH_SHORT).show();
                                                                Intent intent = new Intent(AddToTrack.this,LoggedOnScreen.class);
                                                                intent.putExtra("AddedOrNot","Added");
                                                                startActivity(intent);
                                                            }

                                                        }
                                                    });


                                        }
                                    }
                                });
                    }

                }
                else
                {
                    Toast.makeText(AddToTrack.this,"Invalid Invite Code",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
