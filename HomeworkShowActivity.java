package com.example.plproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.text.Layout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
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
import java.util.concurrent.TimeUnit;

public class HomeworkShowActivity extends AppCompatActivity {

    //listview
    private ListView listViewDoneHomeworks;
    private ArrayAdapter<String>arrayAdapter;
    private ArrayList<String> doneHomeworkList=new ArrayList<>();

    private String currentHomeworkCode;
    private Toolbar toolbar;
    private LinearLayout updateLayout;
    private LinearLayout updateLayoutData;
    private LinearLayout addDoneHomeworkLayout;

    private TextView homeworkName;
    private TextView homeworkDueDate;
    private TextView homeworkData;
    private TextView doneHomeworkData;

    private Button btnUpdate;
    private Button btnUpdateData;
    private Button btnDoneHomeworkData;
    private Button btnOk;

    private TextView homeworkNameForShow;
    private TextView homeworkDueDateForShow;
    private TextView homeworkDataForShow;
    private TextView doneHomeworkDataForShow;



    private FirebaseDatabase db= FirebaseDatabase.getInstance();
    private DatabaseReference dbRefHomeworks=db.getReference(ConstantVariables.HOMEWORKS);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homework_show);
        //intent al
        currentHomeworkCode=getIntent().getExtras().get(ConstantVariables.CODE).toString();

        toolbar=findViewById(R.id.homeworkShowLayout);
        updateLayout=findViewById(R.id.updateLayoutForHomework);
        updateLayoutData=findViewById(R.id.updateLayoutForHomeworkData);
        addDoneHomeworkLayout=findViewById(R.id.layoutForDoneHomeworkData);
        homeworkName=findViewById(R.id.homeworkNameForUpdate);
        homeworkDueDate=findViewById(R.id.homeworkDueDateForUpdate);

        btnDoneHomeworkData=findViewById(R.id.btnDoneHomeworkData);
        btnUpdate=findViewById(R.id.btnUpdateForHomework);
        btnUpdateData=findViewById(R.id.btnUpdateForHomeworkData);
        btnOk=findViewById(R.id.btnOk);

        doneHomeworkData=findViewById(R.id.doneHomeworkData);
        doneHomeworkDataForShow=findViewById(R.id.doneHomeworkDataForShow);
        homeworkNameForShow=findViewById(R.id.homeworkNameForShow);
        homeworkDueDateForShow=findViewById(R.id.homeworkDueDateForShow);
        homeworkData=findViewById(R.id.homeworkDataForUpdate);
        homeworkDataForShow=findViewById(R.id.homeworkDataForShow);




        btnOk.setVisibility(View.INVISIBLE);
        updateLayout.setVisibility(View.INVISIBLE);
        updateLayoutData.setVisibility(View.INVISIBLE);
        addDoneHomeworkLayout.setVisibility(View.INVISIBLE);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(currentHomeworkCode);
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {//homework ismi ve due date güncellemesi
                homeworkNameForShow.setText(homeworkName.getText().toString());
                homeworkDueDateForShow.setText(homeworkDueDate.getText().toString());
                updateLayout.setVisibility(View.INVISIBLE);
                homeworkDataForShow.setVisibility(View.VISIBLE);
                homeworkDataRecord(currentHomeworkCode);

            }
        });
        btnUpdateData.setOnClickListener(new View.OnClickListener() {//homework data güncellemesi
            @Override
            public void onClick(View view) {
                homeworkDataForShow.setText(homeworkData.getText().toString());
                updateLayoutData.setVisibility(View.INVISIBLE);
                homeworkDataForShow.setVisibility(View.VISIBLE);
                homeworkDataRecord(currentHomeworkCode);
            }
        });
        btnDoneHomeworkData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                addDoneHomeworkLayout.setVisibility(View.INVISIBLE);
                doneHomeworkDataForShow.setVisibility(View.VISIBLE);
                createStudentDoneHomeworkAndAdd(TeacherPage.myEmail,currentHomeworkCode);
                doneHomeworkInfoPrintStudent(TeacherPage.myEmail,currentHomeworkCode);
            }
        });

        //tanımlamalar
        listViewDoneHomeworks=findViewById(R.id.listViewDoneHomeworks);
        arrayAdapter=new ArrayAdapter<String>(this,R.layout.support_simple_spinner_dropdown_item,doneHomeworkList);
        listViewDoneHomeworks.setAdapter(arrayAdapter);
        getDoneHomeworksAndShow(currentHomeworkCode);
        listViewDoneHomeworks.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                btnOk.setVisibility(View.VISIBLE);
                listViewDoneHomeworks.setVisibility(View.INVISIBLE);
                doneHomeworkInfoPrintStudent(adapterView.getItemAtPosition(i).toString(),currentHomeworkCode);
                doneHomeworkDataForShow.setVisibility(View.VISIBLE);

            }
        });

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listViewDoneHomeworks.setVisibility(View.VISIBLE);
                doneHomeworkDataForShow.setVisibility(View.INVISIBLE);
                btnOk.setVisibility(View.INVISIBLE);
            }
        });

        if(LogIn.personType.equals(ConstantVariables.STUDENT))
        {
            listViewDoneHomeworks.setVisibility(View.INVISIBLE);
        }
        else
        {
            doneHomeworkDataForShow.setVisibility(View.INVISIBLE);
            listViewDoneHomeworks.setVisibility(View.VISIBLE);
            addDoneHomeworkLayout.setVisibility(View.INVISIBLE);
        }
        homeworkInfoPrint(currentHomeworkCode);//ilk girişte ödev bilgilerini bastırmak için
        doneHomeworkInfoPrintStudent(TeacherPage.myEmail,currentHomeworkCode);

    }
    private void getDoneHomeworksAndShow(final String currentHomeworkCode) {
        dbRefHomeworks.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> keys=dataSnapshot.getChildren();
                for(DataSnapshot key:keys)
                {
                    if(key.child(ConstantVariables.CODE).getValue().toString().equals(currentHomeworkCode))
                    {
                        DatabaseReference databaseReference=key.child(ConstantVariables.DONEHOMEWORKS).getRef();
                        databaseReference.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                Set<String> set = new HashSet<>();
                                Iterator iterator=dataSnapshot.getChildren().iterator();
                                while (iterator.hasNext())
                                {
                                    set.add(((DataSnapshot)iterator.next()).child(ConstantVariables.EMAIL).getValue().toString());
                                }
                                doneHomeworkList.clear();
                                doneHomeworkList.addAll(set);
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


    private void doneHomeworkInfoPrintStudent(final String myEmail, final String currentHomeworkCode)
    {
        dbRefHomeworks.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> keys=dataSnapshot.getChildren();
                for(DataSnapshot key:keys)
                {
                    if(key.child(ConstantVariables.CODE).getValue().toString().equals(currentHomeworkCode))
                    {

                        DatabaseReference dbRefDoneHomeworks=key.child(ConstantVariables.DONEHOMEWORKS).getRef();
                        dbRefDoneHomeworks.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                Iterable<DataSnapshot> keys=dataSnapshot.getChildren();
                                for(DataSnapshot key:keys)
                                {
                                    if(key.child("email").getValue().toString().equals(myEmail))
                                    {
                                        doneHomeworkDataForShow.setText(key.child(ConstantVariables.DATA).getValue().toString());
                                    }
                                }
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

    private void homeworkInfoPrint(final String currentHomeworkCode) {
       dbRefHomeworks.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> keys=dataSnapshot.getChildren();
                for(DataSnapshot key:keys)
                {
                    if(key.child(ConstantVariables.CODE).getValue().toString().equals(currentHomeworkCode))
                    {
                        homeworkNameForShow.setText(key.child(ConstantVariables.NAME).getValue().toString());
                        homeworkDueDateForShow.setText(key.child(ConstantVariables.DUEDATE).getValue().toString());
                        homeworkDataForShow.setText(key.child(ConstantVariables.DATA).getValue().toString());

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void homeworkDataRecord(final String currentHomeworkCode) {
        dbRefHomeworks.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> keys=dataSnapshot.getChildren();
                for(DataSnapshot key:keys)
                {
                    if(key.child(ConstantVariables.CODE).getValue().toString().equals(currentHomeworkCode))
                    {
                        DatabaseReference dbRefHomeworK=key.getRef();
                        dbRefHomeworK.child(ConstantVariables.NAME).setValue(homeworkNameForShow.getText().toString());
                        dbRefHomeworK.child(ConstantVariables.DUEDATE).setValue(homeworkDueDateForShow.getText().toString());
                        dbRefHomeworK.child(ConstantVariables.DATA).setValue(homeworkDataForShow.getText().toString());

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void createStudentDoneHomeworkAndAdd(final String myEmail,final String currentHomeworkCode)
    {
        dbRefHomeworks.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> keys=dataSnapshot.getChildren();
                for(DataSnapshot key:keys)
                {
                    if(key.child(ConstantVariables.CODE).getValue().toString().equals(currentHomeworkCode))
                    {
                        final DatabaseReference dbRefDoneHomeworks=key.child(ConstantVariables.DONEHOMEWORKS).getRef();
                        dbRefDoneHomeworks.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                Iterable<DataSnapshot> keys=dataSnapshot.getChildren();
                                boolean flag=true;
                                for(DataSnapshot key:keys)//kişi ödevi tekrar eklerse eski ödevinin üzerine yazılır
                                {
                                    if(key.child(ConstantVariables.EMAIL).getValue().toString().equals(myEmail))
                                    {
                                        DatabaseReference dbref=key.child(ConstantVariables.DATA).getRef();
                                        dbref.setValue(doneHomeworkData.getText().toString());
                                        flag=false;
                                    }
                                }
                                if(flag)
                                {//yeni ödev yapılır
                                    DoneHomework doneHomework=new DoneHomework();
                                    doneHomework.setEmail(myEmail);
                                    doneHomework.setData(doneHomeworkData.getText().toString());
                                    dbRefDoneHomeworks.push().setValue(doneHomework);
                                }
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
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(LogIn.personType.equals(ConstantVariables.TEACHER))
        getMenuInflater().inflate(R.menu.homework_menu,menu);
        else
            getMenuInflater().inflate(R.menu.homework_menu_student,menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);
        if(item.getItemId()==R.id.updateHomeworkMenu)
        {
            homeworkName.setText(homeworkNameForShow.getText().toString());
            homeworkDueDate.setText(homeworkDueDateForShow.getText().toString());
            homeworkDataForShow.setVisibility(View.INVISIBLE);
            updateLayout.setVisibility(View.VISIBLE);
        }
        else if(item.getItemId()==R.id.updateHomeworkMenuData)
        {
            homeworkData.setText(homeworkDataForShow.getText().toString());
            homeworkDataForShow.setVisibility(View.INVISIBLE);
            updateLayoutData.setVisibility(View.VISIBLE);
        }
        else if(item.getItemId()==R.id.addDoneHomework)
        {
            doneHomeworkDataForShow.setVisibility(View.INVISIBLE);
            addDoneHomeworkLayout.setVisibility(View.VISIBLE);

        }

        return true;
    }



}
