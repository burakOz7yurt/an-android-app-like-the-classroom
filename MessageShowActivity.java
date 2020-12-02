package com.example.plproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MessageShowActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private ImageButton btnSendMessage;
    private EditText textSendMessage;
    private ScrollView scrollView;
    private TextView textMessageShow;
    private String messagePerson;
    private String receiverType;
    private String receiverEmail;


    private FirebaseDatabase db= FirebaseDatabase.getInstance();
    DatabaseReference myDbRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_show);

        toolbar=findViewById(R.id.messageShowLayout);
        btnSendMessage=findViewById(R.id.buttonSendMessage);
        textMessageShow=findViewById(R.id.textMessageShow);
        scrollView=findViewById(R.id.scrollMessageShow);
        textSendMessage=findViewById(R.id.textSendMessage);

        messagePerson=getIntent().getExtras().get("messagePerson").toString();

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(messagePerson);

        if(LogIn.personType.equals(ConstantVariables.STUDENT))
            myDbRef=db.getReference(ConstantVariables.STUDENTS);
        else
            myDbRef=db.getReference(ConstantVariables.TEACHERS);


        if(messagePerson.charAt(0)=='s')//messagePerson nın ilk harfine göre
            receiverType=ConstantVariables.STUDENT;
        else
            receiverType=ConstantVariables.TEACHER;

        receiverEmail=messagePerson.substring(8,messagePerson.length()-1);//mesaj atılacak kişinin emailini belirliyoruz

        getMessageDataAndShow(messagePerson,TeacherPage.myEmail);

        btnSendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String messageData=textSendMessage.getText().toString()+"\n";
                String forMeMessage="You:>"+messageData;
                String forReceiverMessage=TeacherPage.myEmail+":>"+messageData;
                addMessageForMe(LogIn.personType,TeacherPage.myEmail,receiverType+":"+receiverEmail+">",forMeMessage);
                addMessageForReceiver(receiverType,receiverEmail,LogIn.personType+":"+TeacherPage.myEmail+">",forReceiverMessage);
                textSendMessage.setText("");
            }
        });

    }

    private void addMessageForMe(final String personType, final String myEmail, final String whoPerson, final String message) {
        DatabaseReference databaseReference;
        if(personType.equals(ConstantVariables.STUDENT))
            databaseReference=db.getReference(ConstantVariables.STUDENTS);
        else
            databaseReference=db.getReference(ConstantVariables.TEACHERS);

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> keys=dataSnapshot.getChildren();
                for(DataSnapshot key:keys)
                {
                    if(key.child(ConstantVariables.EMAIL).getValue().toString().equals(myEmail))
                    {
                        DatabaseReference dbRefMessages=key.child(ConstantVariables.MESSAGES).getRef();
                        dbRefMessages.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                Iterable<DataSnapshot> keys=dataSnapshot.getChildren();
                                for(DataSnapshot key:keys)
                                {
                                    if(key.child(ConstantVariables.WHOPERSON).getValue().toString().equals(whoPerson))
                                    {
                                        DatabaseReference dbRef=key.child(ConstantVariables.DATA).getRef();
                                        String s=key.child(ConstantVariables.DATA).getValue().toString();
                                         s=s+message;
                                        dbRef.setValue(s);
                                        scrollView.fullScroll(View.FOCUS_DOWN);
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

    private void addMessageForReceiver(final String receiverType, final String receiverEmail, final String whoPerson,final String message) {
        DatabaseReference databaseReference;
        if(receiverType.equals(ConstantVariables.STUDENT))
            databaseReference=db.getReference(ConstantVariables.STUDENTS);
        else
            databaseReference=db.getReference(ConstantVariables.TEACHERS);

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> keys=dataSnapshot.getChildren();
                for(DataSnapshot key:keys)
                {
                    if(key.child(ConstantVariables.EMAIL).getValue().toString().equals(receiverEmail))
                    {
                        DatabaseReference dbRefMessages=key.child(ConstantVariables.MESSAGES).getRef();
                        dbRefMessages.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                Iterable<DataSnapshot> keys=dataSnapshot.getChildren();
                                for(DataSnapshot key:keys)
                                {
                                    if(key.child(ConstantVariables.WHOPERSON).getValue().toString().equals(whoPerson))
                                    {
                                        DatabaseReference dbRef=key.child(ConstantVariables.DATA).getRef();
                                        String s=key.child(ConstantVariables.DATA).getValue().toString();
                                        s=s+message;
                                        dbRef.setValue(s);
                                        scrollView.fullScroll(View.FOCUS_DOWN);

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

    private void getMessageDataAndShow(final String messagePerson,final String myEmail) {
        myDbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> keys=dataSnapshot.getChildren();
                for(DataSnapshot key:keys)
                {
                    if(key.child(ConstantVariables.EMAIL).getValue().toString().equals(myEmail))
                    {
                       DatabaseReference messagesWay=key.child(ConstantVariables.MESSAGES).getRef();
                       messagesWay.addValueEventListener(new ValueEventListener() {
                           @Override
                           public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                               Iterable<DataSnapshot> keys=dataSnapshot.getChildren();
                               for(DataSnapshot key:keys)
                               {
                                   if(key.child(ConstantVariables.WHOPERSON).getValue().toString().equals(messagePerson))
                                   {
                                       textMessageShow.setText(key.child(ConstantVariables.DATA).getValue().toString());
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
}
