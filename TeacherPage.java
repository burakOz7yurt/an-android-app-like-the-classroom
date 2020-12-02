package com.example.plproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import java.util.ArrayList;


public class TeacherPage extends AppCompatActivity {
    private FirebaseDatabase db= FirebaseDatabase.getInstance();
    final DatabaseReference dbRef=db.getReference(ConstantVariables.TEACHERS);
    final DatabaseReference dbRefCourses=db.getReference(ConstantVariables.COURSES);
    final DatabaseReference dbRefStudent=db.getReference(ConstantVariables.STUDENTS);
    final DatabaseReference dbRefAnnouncements=db.getReference(ConstantVariables.ANNOUNCEMENTS);



    //  Intent intent=new Intent(getApplicationContext(),TeacherPage.class);

    private Toolbar toolbar;
    private ViewPager viewPager;
    private TabLayout tableLayout;
    private TabAccessAdapter tabAccessAdapter;
    Intent fromLogin;
    static String myEmail;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_page);
        //TeacherPage ilk oluşturulduğunda bi kere email alıyoz
        fromLogin=getIntent();
        TeacherPage.myEmail=fromLogin.getStringExtra(ConstantVariables.EMAIL);

        toolbar=findViewById(R.id.teacher_page_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(myEmail);


        viewPager=findViewById(R.id.teacher_view_pager);
        tabAccessAdapter=new TabAccessAdapter(getSupportFragmentManager());
        viewPager.setAdapter(tabAccessAdapter);

        tableLayout=findViewById(R.id.main_tabs);
        tableLayout.setupWithViewPager(viewPager);

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(LogIn.personType.equals(ConstantVariables.TEACHER))
        getMenuInflater().inflate(R.menu.menu_main,menu);
        else
            getMenuInflater().inflate(R.menu.menu_main_student,menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
       super.onOptionsItemSelected(item);
        if(item.getItemId()==R.id.createCourse)
        {
            newCourseDemand();
        }
        else if(item.getItemId()==R.id.delateCourse)
        {
            deleteCourseDemand();
        }
        else if(item.getItemId()==R.id.enrollCourse)
        {
            enrollDemandForACourse();
        }
        else if(item.getItemId()==R.id.createAnnounce)
        {
            newAnnounceDemand();
        }
        else if(item.getItemId()==R.id.deleteAnnounce)
        {
            deleteAnnounceDemand();
        }
        else if(item.getItemId()==R.id.createMessageForTeachers)
        {
            createMessageDemand(ConstantVariables.TEACHER);
        }
        else if(item.getItemId()==R.id.createMessageForStudents)
        {
             createMessageDemand(ConstantVariables.STUDENT);
        }
        return true;
    }

    private void createMessageDemand(final String receiverType) {
        AlertDialog.Builder builder=new AlertDialog.Builder(TeacherPage.this,R.style.alertDialog);
        builder.setTitle("please enter email of person that you want to send message");

        final EditText receiverEmailField=new EditText(TeacherPage.this);
        receiverEmailField.setHint(".....@gmail.com");
        builder.setView(receiverEmailField);

        builder.setPositiveButton("MESSAGE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String receiverEmail=receiverEmailField.getText().toString();
                if(TextUtils.isEmpty(receiverEmail))
                {
                    Toast.makeText(TeacherPage.this,"receiver email doesnt be empty ",Toast.LENGTH_LONG).show();
                }
                else
                {
                    createMessage(receiverEmail,myEmail,receiverType,LogIn.personType);
                }
            }
        });
        builder.show();
    }

    private void createMessage(final String receiverEmail,final String myEmail,final String receiverType,final String hostType) {
        DatabaseReference databaseReference;
        if(receiverType.equals(ConstantVariables.STUDENT))
        {
            databaseReference=dbRefStudent;
        }
        else
        {
            databaseReference=dbRef;
        }
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> keys=dataSnapshot.getChildren();
                boolean flag=false;
                for(DataSnapshot key:keys)
                {
                    if(key.child(ConstantVariables.EMAIL).getValue().toString().equals(receiverEmail))
                    {
                        flag=true;
                        final String whoAmI=hostType+":"+myEmail+">";//cross
                        final String whoPerson=receiverType+":"+receiverEmail+">";
                        final DatabaseReference notHostRef=key.getRef();
                        DatabaseReference messageRef=key.child(ConstantVariables.MESSAGES).getRef();
                        messageRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                Iterable<DataSnapshot> keys=dataSnapshot.getChildren();
                                boolean flag=false;
                                for(DataSnapshot key:keys)
                                {
                                    if(key.child(ConstantVariables.WHOPERSON).getValue().toString().equals(whoAmI))
                                    {
                                        flag=true;
                                    }
                                }
                                if(flag)
                                {

                                }
                                else
                                {
                                    Message forNotHostMessage=new Message(whoAmI,"");//burada mesaj göderilecek kişiye ekleme yapıldı
                                    notHostRef.child(ConstantVariables.MESSAGES).push().setValue(forNotHostMessage);
                                    DatabaseReference databaseReference;
                                    if(hostType.equals(ConstantVariables.STUDENT))
                                    {
                                        databaseReference=dbRefStudent;
                                    }
                                    else
                                    {
                                        databaseReference=dbRef;
                                    }
                                    databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            Iterable<DataSnapshot> keys=dataSnapshot.getChildren();
                                            for(DataSnapshot key:keys)
                                            {
                                                if(key.child(ConstantVariables.EMAIL).getValue().toString().equals(myEmail))
                                                {

                                                    Message forHostMessage=new Message(whoPerson,"");//burada mesaj gönderene  ekleme yapıldı
                                                    DatabaseReference hostRef=key.getRef();
                                                    hostRef.child(ConstantVariables.MESSAGES).push().setValue(forHostMessage);
                                                }
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });
                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                }
                if(!flag)
                {
                    if(receiverType.equals(ConstantVariables.STUDENT))
                    {
                        Toast.makeText(TeacherPage.this,"There aren't this student ",Toast.LENGTH_LONG).show();
                    }
                    else
                    {
                        Toast.makeText(TeacherPage.this,"There aren't this teacher ",Toast.LENGTH_LONG).show();
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void enrollDemandForACourse() {
        AlertDialog.Builder builder=new AlertDialog.Builder(TeacherPage.this,R.style.alertDialog);
        builder.setTitle("enter course code");

        final EditText courseCodeField=new EditText(TeacherPage.this);
        courseCodeField.setHint("CME0000");
        builder.setView(courseCodeField);

        builder.setPositiveButton("ENROLL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String courseCode=courseCodeField.getText().toString();
                if(TextUtils.isEmpty(courseCode))
                {
                    Toast.makeText(TeacherPage.this,"course name doesnt be empty ",Toast.LENGTH_LONG).show();
                }
                else
                {
                    enrollACourse(courseCode,myEmail);
                }
            }
        });
        builder.show();
    }

    private void enrollACourse(final String courseCode, final String myEmail) {
        dbRefCourses.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> keys=dataSnapshot.getChildren();
                boolean flag=false;
                for(DataSnapshot key:keys)
                {
                    if(key.child(ConstantVariables.CODE).getValue().toString().equals(courseCode))
                    {
                        flag=true;
                    }
                }
                if(flag)
                {
                    dbRefStudent.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            Iterable<DataSnapshot> keys=dataSnapshot.getChildren();
                            for(DataSnapshot key:keys)
                            {
                                if(key.child(ConstantVariables.EMAIL).getValue().toString().equals(myEmail))
                                {
                                    DatabaseReference dbRef=key.child(ConstantVariables.COURSES).getRef();
                                    dbRef.push().setValue(courseCode);
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
                else
                {
                    Toast.makeText(TeacherPage.this,"There aren't this course ",Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void deleteCourseDemand() {
        AlertDialog.Builder builder=new AlertDialog.Builder(TeacherPage.this,R.style.alertDialog);
        builder.setTitle("enter course code");

        final EditText courseCodeField=new EditText(TeacherPage.this);
        courseCodeField.setHint("CME0000");
        builder.setView(courseCodeField);

        builder.setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String courseCode=courseCodeField.getText().toString();
                if(TextUtils.isEmpty(courseCode))
                {
                    Toast.makeText(TeacherPage.this,"course name doesnt be empty ",Toast.LENGTH_LONG).show();
                }
                else
                {
                    deleteCourse(courseCode);//method Overloading
                    deleteCourse(courseCode,myEmail);
                }
            }
        });
        builder.show();
    }

    private void deleteCourse(final String courseCode, final String myEmail) {
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> keys=dataSnapshot.getChildren();
                for(DataSnapshot key:keys)
                {
                    if(key.child(ConstantVariables.EMAIL).getValue().toString().equals(myEmail))
                    {
                        DatabaseReference myDbRef=key.child(ConstantVariables.COURSES).getRef();
                        myDbRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                            {
                                Iterable<DataSnapshot> keys=dataSnapshot.getChildren();
                                for(DataSnapshot key:keys)
                                {
                                    if(key.getValue().toString().equals(courseCode))
                                    {

                                        DatabaseReference myRef=key.getRef();
                                        myRef.removeValue();
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

    private void deleteCourse(final String courseCode) {
        dbRefCourses.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> keys=dataSnapshot.getChildren();
                for(DataSnapshot key:keys)
                {
                    if(key.child(ConstantVariables.CODE).getValue().toString().equals(courseCode))
                    {

                        DatabaseReference myRef=key.getRef();
                        myRef.removeValue();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }



    private void newCourseDemand() {
        AlertDialog.Builder builder=new AlertDialog.Builder(TeacherPage.this,R.style.alertDialog);
        builder.setTitle("enter course code");

        final EditText courseCodeField=new EditText(TeacherPage.this);
        courseCodeField.setHint("CME0000");
        builder.setView(courseCodeField);

        builder.setPositiveButton("CREATE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String courseCode=courseCodeField.getText().toString();
                if(TextUtils.isEmpty(courseCode))
                {
                    Toast.makeText(TeacherPage.this,"course name doesnt be empty ",Toast.LENGTH_LONG).show();
                }
                else
                {
                    createNewCourse(courseCode,myEmail);
                }
            }
        });
       builder.show();
    }


    public void createNewCourse(final String courseCode,final String email)
    {
        dbRefCourses.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean flag=true;//önce bu kurs adına kayıtlı kurs varmı diye bakıyoruz;
                Iterable<DataSnapshot> keys=dataSnapshot.getChildren();
                for(DataSnapshot key:keys)
                {
                    if(key.child(ConstantVariables.CODE).getValue().toString().equals(courseCode))
                    {
                        flag=false;
                    }
                }

                if(flag)
                {
                    dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            Iterable<DataSnapshot> keys=dataSnapshot.getChildren();
                            for(DataSnapshot key:keys)
                            {
                                if(key.child(ConstantVariables.EMAIL).getValue().toString().equals(email))
                                {

                                    DatabaseReference myRef=key.child(ConstantVariables.COURSES).getRef();
                                    myRef.push().setValue(courseCode);
                                    dbRefCourses.push().setValue(new Course(courseCode));
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
                else//bu code a sahip kurs varsa hata mesajı dönecek
                    Toast.makeText(TeacherPage.this,"course code are already used",Toast.LENGTH_LONG).show();



            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void newAnnounceDemand() {
        AlertDialog.Builder builder=new AlertDialog.Builder(TeacherPage.this,R.style.alertDialog);
        builder.setTitle("enter announce code");

        final EditText announceCodeField=new EditText(TeacherPage.this);
        announceCodeField.setHint("ANNOUNCE0000");
        builder.setView(announceCodeField);

        builder.setPositiveButton("CREATE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String announceCode=announceCodeField.getText().toString();
                if(TextUtils.isEmpty(announceCode))
                {
                    Toast.makeText(TeacherPage.this,"announce name doesnt be empty ",Toast.LENGTH_LONG).show();
                }
                else
                {
                    createNewAnnounce(announceCode,myEmail);
                }
            }
        });
        builder.show();
    }
    public void createNewAnnounce(final String announceCode,final String email)
    {
        dbRefAnnouncements.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean flag=true;//önce bu announce adına kayıtlı kurs varmı diye bakıyoruz;
                Iterable<DataSnapshot> keys=dataSnapshot.getChildren();
                for(DataSnapshot key:keys)
                {
                    if(key.child(ConstantVariables.CODE).getValue().toString().equals(announceCode))
                    {
                        flag=false;
                    }
                }

                if(flag)
                {
                    Announce announce=new Announce(announceCode,myEmail);
                    dbRefAnnouncements.push().setValue(announce);

                }
                else//bu code a sahip announce varsa hata mesajı dönecek
                    Toast.makeText(TeacherPage.this,"announce code are already used",Toast.LENGTH_LONG).show();



            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void deleteAnnounceDemand() {
        AlertDialog.Builder builder=new AlertDialog.Builder(TeacherPage.this,R.style.alertDialog);
        builder.setTitle("enter announce code");

        final EditText announceCodeField=new EditText(TeacherPage.this);
        announceCodeField.setHint("ANNOUNCE0000");
        builder.setView(announceCodeField);

        builder.setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String announceCode=announceCodeField.getText().toString();
                if(TextUtils.isEmpty(announceCode))
                {
                    Toast.makeText(TeacherPage.this,"announce code doesnt be empty ",Toast.LENGTH_LONG).show();
                }
                else
                {
                    deleteAnnounce(announceCode,myEmail);
                }
            }
        });
        builder.show();
    }
    private void deleteAnnounce(final String announceCode, final String myEmail) {
        dbRefAnnouncements.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> keys=dataSnapshot.getChildren();
                for(DataSnapshot key:keys)
                {
                    if(key.child(ConstantVariables.CODE).getValue().toString().equals(announceCode) && key.child(ConstantVariables.EMAIL).getValue().toString().equals(myEmail))
                    {

                        DatabaseReference myRef=key.getRef();
                        myRef.removeValue();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
