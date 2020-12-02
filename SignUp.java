package com.example.plproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.EventListener;

public class SignUp extends AppCompatActivity implements View.OnClickListener {


    TextView name;
    TextView surname;
    TextView email;
    TextView password;
    Button   registrationBtn;
    CheckBox studentCheck;
    CheckBox teacherCheck;
    String persontype;
    private FirebaseDatabase db=FirebaseDatabase.getInstance();;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        name=findViewById(R.id.nameText);
        surname=findViewById(R.id.surnameText);
        email=findViewById(R.id.emailText);
        password=findViewById(R.id.passwordText);

        registrationBtn=findViewById(R.id.registrationButton);

        studentCheck=findViewById(R.id.studentCheckBox);
        teacherCheck=findViewById(R.id.teacherCheckBox);


        registrationBtn.setOnClickListener(this);

        studentCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
              if(teacherCheck.isChecked() && studentCheck.isChecked())
              {
                  teacherCheck.setChecked(false);

              }
            }
        });

        teacherCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(studentCheck.isChecked() && teacherCheck.isChecked())
                {
                    studentCheck.setChecked(false);
                }
            }
        });
    }

    @Override
    public void onClick(View view) {

      if(view.getId()==registrationBtn.getId())
        {
            if(teacherCheck.isChecked())
            setdata(false);
            else if(studentCheck.isChecked())
                setdata(true);
            else
                alert("lütfen kişi tipini seçin",null);

        }


        }

        public void alert(String settitle,String setmessage)
        {
            AlertDialog.Builder alertDialog=new AlertDialog.Builder(this);
            alertDialog.setTitle(settitle)
                    .setMessage(setmessage)
                    .setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            //
                        }
                    }).show();
        }
    public void setdata(Boolean typeP)
    {
        DatabaseReference dbRef;
        if(typeP)
            dbRef=db.getReference(ConstantVariables.STUDENTS);
        else
            dbRef=db.getReference(ConstantVariables.TEACHERS);

        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
              @Override
              public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                  boolean flag=false;
                  Iterable<DataSnapshot> keys=dataSnapshot.getChildren();
                  for(DataSnapshot key:keys)
                  {
                      if(key.child(ConstantVariables.EMAIL).getValue().toString().equals(email.getText().toString()))
                          flag=true;
                  }
                  if (studentCheck.isChecked()) {
                      persontype = ConstantVariables.STUDENT;
                  } else{
                      persontype = ConstantVariables.TEACHER;
                  }

                  if(name!=null && surname!=null && email!=null && password!=null  ) {

                      if(flag)
                      {
                          alert("this email already exists",null);
                      }
                      else
                      {
                          createNewAccount(name.getText().toString(), surname.getText().toString(), email.getText().toString(), password.getText().toString(), persontype);

                          alert("your registration has successfully occurred",null);
                      }

                  }
                  else
                  {
                      alert("fill all of fields","please try again");
                  }



              }
              @Override
              public void onCancelled(@NonNull DatabaseError databaseError) {

              }
          });

    }
    public void createNewAccount(String name,String surname,String email,String password,String persontype)
    {
        DatabaseReference dbRefTeacher=db.getReference(ConstantVariables.TEACHERS);
        DatabaseReference dbRefStudent=db.getReference(ConstantVariables.STUDENTS);

        String keyT=dbRefTeacher.push().getKey();
        String keyS=dbRefStudent.push().getKey();

        if(persontype.equals(ConstantVariables.TEACHER))
        {
            DatabaseReference dbRefNew=db.getReference("Teachers/"+keyT);
            dbRefNew.setValue(new Teacher(name,surname,email,password));
        }
        else  if(persontype.equals(ConstantVariables.STUDENT))
        {
            DatabaseReference dbRefNew=db.getReference("Students/"+keyS);
            dbRefNew.setValue(new Student(name,surname,email,password));
        }
    }

}

