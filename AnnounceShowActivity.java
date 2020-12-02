package com.example.plproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AnnounceShowActivity extends AppCompatActivity {

    private Toolbar toolbar;


    private String currentAnnounceCode;

    private TextView announceName;
    private TextView announceEmail;
    private TextView announceDataForShow;

    private LinearLayout updateLayoutData;
    private TextView textDataUpdate;
    private Button btnUpdateData;

    private LinearLayout updateLayoutName;
    private TextView textNameUpdate;
    private Button btnUpdateName;

    private FirebaseDatabase db= FirebaseDatabase.getInstance();
    DatabaseReference dbRefAnnouncements=db.getReference(ConstantVariables.ANNOUNCEMENTS);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_announce_show);

        currentAnnounceCode=getIntent().getExtras().get("announceCode").toString();

        announceName=findViewById(R.id.announceNameForShow);
        announceEmail=findViewById(R.id.announceEmailForShow);
        announceDataForShow=findViewById(R.id.announceDataForShow);




        updateLayoutData=findViewById(R.id.layoutAnnounceData);
        textDataUpdate=findViewById(R.id.announceForUpdateData);
        btnUpdateData=findViewById(R.id.btnAnnounce);

        updateLayoutName=findViewById(R.id.layoutAnnounceName);
        textNameUpdate=findViewById(R.id.announceForUpdateName);
        btnUpdateName=findViewById(R.id.btnAnnounceName);

        updateLayoutName.setVisibility(View.INVISIBLE);
        updateLayoutData.setVisibility(View.INVISIBLE);

        announceInfoPrint(currentAnnounceCode);//ilk girişte kurs bilgileri görünsün diye

        toolbar=findViewById(R.id.announceShowLayout);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(currentAnnounceCode);

        btnUpdateData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getUpdateData(textDataUpdate.getText().toString());

                updateLayoutData.setVisibility(View.INVISIBLE);
                announceName.setVisibility(View.VISIBLE);
                announceEmail.setVisibility(View.VISIBLE);
                announceDataForShow.setVisibility(View.VISIBLE);
            }
        });
        btnUpdateName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getUpdateName(textNameUpdate.getText().toString());

                updateLayoutName.setVisibility(View.INVISIBLE);
                announceDataForShow.setVisibility(View.VISIBLE);
            }
        });

    }

    private void getUpdateName(final String nameUpdate) {
        dbRefAnnouncements.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> keys=dataSnapshot.getChildren();
                for(DataSnapshot key:keys)
                {
                    if(key.child("code").getValue().toString().equals(currentAnnounceCode))
                    {
                        DatabaseReference dbRef=key.child(ConstantVariables.NAME).getRef();
                        dbRef.setValue(nameUpdate);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getUpdateData(final String dataUpdate) {
        dbRefAnnouncements.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> keys=dataSnapshot.getChildren();
                for(DataSnapshot key:keys)
                {
                    if(key.child(ConstantVariables.CODE).getValue().toString().equals(currentAnnounceCode))
                    {
                        DatabaseReference dbRef=key.child(ConstantVariables.DATA).getRef();
                        dbRef.setValue(dataUpdate);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void announceInfoPrint(final String currentAnnounceCode) {
        dbRefAnnouncements.addValueEventListener(new ValueEventListener() {//burada eski kurs objesini atıp yenilenen kurs objesini katıyoruz
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> keys=dataSnapshot.getChildren();
                for(DataSnapshot key:keys)
                {
                    if(key.child("code").getValue().toString().equals(currentAnnounceCode))
                    {
                        announceName.setText(key.child(ConstantVariables.NAME).getValue().toString());
                        announceEmail.setText(key.child(ConstantVariables.EMAIL).getValue().toString());
                        announceDataForShow.setText(key.child(ConstantVariables.DATA).getValue().toString());

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
        if(LogIn.personType.equals(ConstantVariables.TEACHER))//teachers can just use menu
        getMenuInflater().inflate(R.menu.menu_announce,menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);
        if(item.getItemId()==R.id.updateAnnounceDataMenu)
        {
            announceName.setVisibility(View.INVISIBLE);
            announceEmail.setVisibility(View.INVISIBLE);
            announceDataForShow.setVisibility(View.INVISIBLE);

            updateLayoutData.setVisibility(View.VISIBLE);
        }
        else if(item.getItemId()==R.id.updateAnnounceNameMenu)
        {
            announceDataForShow.setVisibility(View.INVISIBLE);
            updateLayoutName.setVisibility(View.VISIBLE);
        }

        return true;
    }
}
