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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;


/**
 * A simple {@link Fragment} subclass.
 */
public class CoursesFragment extends Fragment {
    //email almak için
     String myEmail;


    private View coursesView;
    private ListView list_view;
    private ArrayAdapter<String> arrayAdapter;
    private ArrayList<String> courseList=new ArrayList<>();

    //firebase
    private DatabaseReference courseWay;

    public CoursesFragment() {

        // Required empty public constructor

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {

        coursesView= inflater.inflate(R.layout.fragment_courses, container, false);
        //firebase tanımlama
        if(LogIn.personType.equals(ConstantVariables.STUDENT))
            courseWay= FirebaseDatabase.getInstance().getReference().child(ConstantVariables.STUDENTS);
        else
            courseWay= FirebaseDatabase.getInstance().getReference().child(ConstantVariables.TEACHERS);

        //tanımlamalar
        list_view=coursesView.findViewById(R.id.listViewTeacherCourses);
        arrayAdapter=new ArrayAdapter<String>(getContext(),android.R.layout.simple_list_item_1,courseList);
        list_view.setAdapter(arrayAdapter);

        //grupları alma kodları
        myEmail=TeacherPage.myEmail;
        getCourseAndShow(myEmail);
        list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String currentCourseCode=adapterView.getItemAtPosition(i).toString();
                Intent courseShowActivity=new Intent(getContext(),CourseShowActivity.class);
                courseShowActivity.putExtra("courseCode",currentCourseCode);
                startActivity(courseShowActivity);
            }
        });
        return coursesView;
    }

    private void getCourseAndShow(final String email) {

       courseWay.addValueEventListener(new ValueEventListener() {
           @Override
           public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
               Iterable<DataSnapshot> keys=dataSnapshot.getChildren();
               for(DataSnapshot key:keys)
               {

                   if(key.child(ConstantVariables.EMAIL).getValue().toString().equals(email))
                   {//giriş yapan kişi için işlemler yapılmalı
                       Set<String> set=new HashSet<>();
                       Iterator iterator=key.child(ConstantVariables.COURSES).getChildren().iterator();
                     while (iterator.hasNext())
                     {
                         set.add(((DataSnapshot)iterator.next()).getValue().toString());
                     }
                     courseList.clear();
                     courseList.addAll(set);
                     arrayAdapter.notifyDataSetChanged();
                   }
               }
           }

           @Override
           public void onCancelled(@NonNull DatabaseError databaseError) {

           }
       });
    }
}
