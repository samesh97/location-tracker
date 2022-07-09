package com.sba.xtrack;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

import static com.sba.xtrack.R.layout.card_layout;
import static com.sba.xtrack.R.layout.listview;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.MembersViewHolder>
{
    List<String> trackingNames;
    Context c;
    int image[] = {R.drawable.ic_person};
    static int position = -1;
    RecyclerViewAdapter(List<String> trackingNames, Context c)
    {
        this.trackingNames = trackingNames;
        this.c = c;

    }

    @Override
    public int getItemCount()
    {

            return trackingNames.size();

    }


    @NonNull
    @Override
    public MembersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i)
    {
        View v = LayoutInflater.from(parent.getContext()).inflate(listview,parent,false);
        MembersViewHolder membersViewHolder  =  new MembersViewHolder(v,c,trackingNames);
        return membersViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MembersViewHolder membersViewHolder, int i)
    {
        String name = trackingNames.get(i);
        membersViewHolder.name.setText(name);
        membersViewHolder.logo.setImageResource(image[0]);

    }

    public static class MembersViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        TextView name;
        Context c;
        ImageView logo;
        Button visitButton;
        List<String> nameArrayList;

        @Override
        public void onClick(View v)
        {
            //Toast.makeText(c, "clicked " + getAdapterPosition() , Toast.LENGTH_SHORT).show();
        }




        public MembersViewHolder(@NonNull View itemView, final Context c, List<String> nameArrayList)
        {
            super(itemView);
            this.c = c;
            this.nameArrayList =  nameArrayList;

            itemView.setOnClickListener(this);
            name = itemView.findViewById(R.id.Name);
            logo = itemView.findViewById(R.id.logo);
            visitButton = itemView.findViewById(R.id.visitButton);

           visitButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    position = getAdapterPosition();
                    ((LoggedOnScreen) c).onItemClick(getAdapterPosition());
                    //Toast.makeText(c, "clicked " + position , Toast.LENGTH_SHORT).show();
                }
            });


        }
        OnItemClickListener onItemClickListener;

        public interface OnItemClickListener{
            void onItemClick(int position);
        }

    }
}
