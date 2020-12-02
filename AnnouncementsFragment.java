package com.example.plproject;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;


public class AnnouncementsFragment extends Fragment {
    //email almak için
    String myEmail;

    private TextView announceData;
    private View announceView;
    private ListView list_view;
    private ArrayAdapter<String> arrayAdapter;
    private ArrayList<String> announceList=new ArrayList<>();

    //firebase
    private DatabaseReference announceWay;

    public AnnouncementsFragment() {
        // Required empty public constructor

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        announceView= inflater.inflate(R.layout.fragment_courses, container, false);
        //firebase tanımlama

        announceWay= FirebaseDatabase.getInstance().getReference().child(ConstantVariables.ANNOUNCEMENTS);


        //tanımlamalar
        list_view=announceView.findViewById(R.id.listViewTeacherCourses);
        arrayAdapter=new ArrayAdapter<String>(getContext(),android.R.layout.simple_list_item_1,announceList);
        list_view.setAdapter(arrayAdapter);


        getAnnounceAndShow();
        list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                String currentAnnounceCode=adapterView.getItemAtPosition(i).toString();
                Intent announceShowActivity=new Intent(getContext(),AnnounceShowActivity.class);
                announceShowActivity.putExtra("announceCode",currentAnnounceCode);
                startActivity(announceShowActivity);
            }
        });
        return announceView;
    }
    private void getAnnounceAndShow() {

        announceWay.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> key=dataSnapshot.getChildren();
                Set<String> set=new HashSet<>();
                Iterator iterator=key.iterator();
                while (iterator.hasNext())
                {
                    set.add(((DataSnapshot)iterator.next()).child(ConstantVariables.CODE).getValue().toString());
                }
                announceList.clear();
                announceList.addAll(set);
                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
