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


/**
 * A simple {@link Fragment} subclass.
 */
public class ChatFragment extends Fragment {
    //email almak için

    private View chatView;
    private ListView list_view;
    private ArrayAdapter<String> arrayAdapter;
    private ArrayList<String> chatList=new ArrayList<>();

    //firebase
    private DatabaseReference personWay;
    public ChatFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        chatView= inflater.inflate(R.layout.fragment_courses, container, false);
        //firebase tanımlama
        if(LogIn.personType.equals(ConstantVariables.STUDENT))
        personWay= FirebaseDatabase.getInstance().getReference().child(ConstantVariables.STUDENTS);
        else
            personWay= FirebaseDatabase.getInstance().getReference().child(ConstantVariables.TEACHERS);


        //tanımlamalar
        list_view=chatView.findViewById(R.id.listViewTeacherCourses);
        arrayAdapter=new ArrayAdapter<String>(getContext(),android.R.layout.simple_list_item_1,chatList);
        list_view.setAdapter(arrayAdapter);


        getChatAndShow(TeacherPage.myEmail);
        list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                String messagePerson=adapterView.getItemAtPosition(i).toString();
                Intent messageShowActivity=new Intent(getContext(),MessageShowActivity.class);
                messageShowActivity.putExtra("messagePerson",messagePerson);
                startActivity(messageShowActivity);
            }
        });
        return chatView;
    }
    private void getChatAndShow(final String myEmail) {
      personWay.addListenerForSingleValueEvent(new ValueEventListener() {
          @Override
          public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
              Iterable<DataSnapshot> keys=dataSnapshot.getChildren();

              for(DataSnapshot key:keys)
              {
                  if(key.child(ConstantVariables.EMAIL).getValue().toString().equals(myEmail))
                  {
                      DatabaseReference databaseReference=key.child(ConstantVariables.MESSAGES).getRef();
                      databaseReference.addValueEventListener(new ValueEventListener() {
                          @Override
                          public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                              Iterable<DataSnapshot> key=dataSnapshot.getChildren();
                              Set<String> set=new HashSet<>();
                              Iterator iterator=key.iterator();
                              while (iterator.hasNext())
                              {
                                  set.add(((DataSnapshot)iterator.next()).child(ConstantVariables.WHOPERSON).getValue().toString());
                              }
                              chatList.clear();
                              chatList.addAll(set);
                              arrayAdapter.notifyDataSetChanged();
                          }

                          @Override
                          public void onCancelled(@NonNull DatabaseError databaseError) {

                          }
                      });
                  }
              }
          }

          @Override
          public void onCancelled(@NonNull DatabaseError databaseError) {

          }
      });

    }
}
