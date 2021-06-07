package com.sba.xtrack;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

import static com.sba.xtrack.R.layout.card_layout;

public class TrackingListAdapter extends RecyclerView.Adapter<TrackingListAdapter.MembersViewHolder>
{
    ArrayList<User> nameList;
    Context c;
    TrackingListAdapter(ArrayList<User> nameList, Context c)
    {
        this.nameList = nameList;
        this.c = c;
    }

    @Override
    public int getItemCount() {
        return nameList.size();
    }


    @NonNull
    @Override
    public MembersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i)
    {
        View v = LayoutInflater.from(parent.getContext()).inflate(card_layout,parent,false);
        MembersViewHolder membersViewHolder  =  new MembersViewHolder(v,c,nameList);
        return membersViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MembersViewHolder membersViewHolder, int i)
    {
        User currentUser = nameList.get(i);
        membersViewHolder.name.setText(currentUser.name);
    }

    public static class MembersViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        TextView name;
        Context c;
        ArrayList<User> nameArrayList;
        FirebaseAuth firebaseAuth;
        FirebaseUser user;

        @Override
        public void onClick(View v)
        {

        }

        public MembersViewHolder(@NonNull View itemView, Context c, ArrayList<User> nameArrayList)
        {
            super(itemView);
            this.c = c;
            this.nameArrayList = nameArrayList;

            itemView.setOnClickListener(this);
            firebaseAuth = FirebaseAuth.getInstance();
            user = firebaseAuth.getCurrentUser();

            name = itemView.findViewById(R.id.itemTitle);

        }
    }
}
