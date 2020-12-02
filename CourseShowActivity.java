package com.example.plproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class CourseShowActivity extends AppCompatActivity  {

    //listview
    private ListView listViewHomeworks;
    private ListView listViewPosts;
    private ArrayAdapter<String>arrayAdapter;
    private ArrayList<String>homeworkList=new ArrayList<>();
    private ArrayAdapter<String>arrayAdapter2;
    private ArrayList<String>postList=new ArrayList<>();

    private ScrollView scrollViewCourseShow;
    private ScrollView scrollViewPostShow;

    private Toolbar toolbar;
    //mevcut kurs kodu
    private String currentCourseCode;
    private LinearLayout updateLayout;
    private TextView courseName;
    private TextView courseTerm;
    private Button btnUpdate;
    private String newCourseName;
    private String newCorseTerm;
    private TextView courseNameForShow;
    private TextView courseTermForShow;

    private FirebaseDatabase db= FirebaseDatabase.getInstance();
    final DatabaseReference dbRefCourses=db.getReference(ConstantVariables.COURSES);
    final DatabaseReference dbRefTeacher=db.getReference(ConstantVariables.TEACHERS);
    final DatabaseReference dbRefHomeworks=db.getReference(ConstantVariables.HOMEWORKS);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_show);

        updateLayout=findViewById(R.id.updateLayout);
        courseName=findViewById(R.id.courseNameForUpdate);
        courseTerm=findViewById(R.id.courseTermForUpdate);
        btnUpdate=findViewById(R.id.btnUpdate);
        courseNameForShow=findViewById(R.id.courseNameForShow);
        courseTermForShow=findViewById(R.id.courseTermForShow);




        updateLayout.setVisibility(View.INVISIBLE);
        //kurs kodunu al
        currentCourseCode=getIntent().getExtras().get("courseCode").toString();
        courseInfoPrint(currentCourseCode);//ilk girişte kurs bilgileri görünsün diye

        toolbar=findViewById(R.id.courseShowLayout);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(currentCourseCode);


        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ///olaylar burada dönecek
                newCourseName=courseName.getText().toString();
                newCorseTerm=courseTerm.getText().toString();
                courseUpdate(currentCourseCode,newCourseName,newCorseTerm);
                courseNameForShow.setText(newCourseName);
                courseTermForShow.setText(newCorseTerm);
                updateLayout.setVisibility(View.INVISIBLE);
            }
        });

        //tanımlamalar

        listViewPosts=findViewById(R.id.listViewPostShow);
        arrayAdapter2=new ArrayAdapter<String>(this,R.layout.support_simple_spinner_dropdown_item,postList);
        listViewPosts.setAdapter(arrayAdapter2);
        getPostsAndShow(currentCourseCode);
        listViewPosts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                String currentPostCode=adapterView.getItemAtPosition(i).toString();
                Intent postShowActivity=new Intent(CourseShowActivity.this,PostShowActivity.class);
                postShowActivity.putExtra("postCode",currentPostCode);
                postShowActivity.putExtra("courseCode",currentCourseCode);

                startActivity(postShowActivity);

            }
        });

        listViewHomeworks=findViewById(R.id.listViewCourseShow);
        arrayAdapter=new ArrayAdapter<String>(this,R.layout.support_simple_spinner_dropdown_item,homeworkList);
        listViewHomeworks.setAdapter(arrayAdapter);
        getHomeworksAndShow(currentCourseCode);
        listViewHomeworks.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String currentHomeworkCode=adapterView.getItemAtPosition(i).toString();
                Intent homeworkShowActivity=new Intent(CourseShowActivity.this,HomeworkShowActivity.class);
                homeworkShowActivity.putExtra(ConstantVariables.CODE,currentHomeworkCode);
                startActivity(homeworkShowActivity);
            }
        });

    }

    private void getPostsAndShow(final String courseCode) {
        dbRefCourses.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> keys=dataSnapshot.getChildren();
                for(DataSnapshot key:keys)
                {
                    if(key.child("code").getValue().toString().equals(courseCode))
                    {
                        DatabaseReference databaseReference=key.child(ConstantVariables.POSTS).getRef();
                        databaseReference.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                Set<String>set = new HashSet<>();
                                Iterator iterator=dataSnapshot.getChildren().iterator();
                                while (iterator.hasNext())
                                {
                                    set.add(((DataSnapshot)iterator.next()).child(ConstantVariables.CODE).getValue().toString());
                                }
                                postList.clear();
                                postList.addAll(set);
                                arrayAdapter2.notifyDataSetChanged();
                                //scrollViewPostShow.fullScroll(View.FOCUS_DOWN);

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

    private void getHomeworksAndShow(final String courseCode) {
        dbRefCourses.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> keys=dataSnapshot.getChildren();
                for(DataSnapshot key:keys)
                {
                    if(key.child(ConstantVariables.CODE).getValue().toString().equals(courseCode))
                    {
                        DatabaseReference databaseReference=key.child(ConstantVariables.HOMEWORKS).getRef();
                        databaseReference.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                Set<String>set = new HashSet<>();
                                Iterator iterator=dataSnapshot.getChildren().iterator();
                                while (iterator.hasNext())
                                {
                                    set.add(((DataSnapshot)iterator.next()).getValue().toString());
                                }
                                homeworkList.clear();
                                homeworkList.addAll(set);
                                arrayAdapter.notifyDataSetChanged();
                                //scrollViewCourseShow.fullScroll(View.FOCUS_DOWN);

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




    private void newHomeworkDemand() {
        AlertDialog.Builder builder=new AlertDialog.Builder(CourseShowActivity.this,R.style.alertDialog);
        builder.setTitle("enter homework code");

        final EditText homeworkCodeField=new EditText(CourseShowActivity.this);
        homeworkCodeField.setHint("HW00");
        builder.setView(homeworkCodeField);

        builder.setPositiveButton("CREATE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String homeworkCode=homeworkCodeField.getText().toString();
                if(TextUtils.isEmpty(homeworkCode))
                {
                    Toast.makeText(CourseShowActivity.this,"course name doesnt be empty ",Toast.LENGTH_LONG).show();
                }
                else
                {
                    createHomeworkForCurrentCourse(homeworkCode,currentCourseCode);

                }
            }
        });
        builder.show();
    }
    private void createHomeworkForCurrentCourse(final String homeworkCode,final String courseCode)
    {
        dbRefCourses.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> keys=dataSnapshot.getChildren();
                for(DataSnapshot key:keys)
                {
                    if(key.child("code").getValue().toString().equals(courseCode))
                    {

                        final  DatabaseReference hwDbRef=db.getReference(ConstantVariables.HOMEWORKS);

                        hwDbRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                boolean flag=true;//önce bu kurs adına kayıtlı kurs varmı diye bakıyoruz;
                                Iterable<DataSnapshot> keys=dataSnapshot.getChildren();
                                for(DataSnapshot key:keys)
                                {
                                    if(key.child(ConstantVariables.CODE).getValue().toString().equals(homeworkCode))
                                    {
                                        flag=false;
                                    }
                                }

                                if(flag)
                                {
                                    HomeWorkForPublish homeWork=new HomeWorkForPublish(homeworkCode);
                                    hwDbRef.push().setValue(homeWork);
                                    courseAddHomeworkCode(homeworkCode,courseCode);

                                }
                                else//bu code a sahip homework varsa hata mesajı dönecek
                                    Toast.makeText(CourseShowActivity.this,"homework code are already used",Toast.LENGTH_LONG).show();



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
    private void courseAddHomeworkCode(final String homeworkCode, final String courseCode)
    {
        dbRefCourses.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> keys=dataSnapshot.getChildren();
                for(DataSnapshot key:keys)
                {
                    if(key.child(ConstantVariables.CODE).getValue().toString().equals(courseCode))
                    {
                        DatabaseReference dbRef=key.child(ConstantVariables.HOMEWORKS).getRef();
                        dbRef.push().setValue(homeworkCode);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void courseUpdate(final String currentCourseCode, final String newCourseName, final String newCorseTerm) {
        dbRefCourses.addListenerForSingleValueEvent(new ValueEventListener() {//burada eski kurs objesini atıp yenilenen kurs objesini katıyoruz
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> keys=dataSnapshot.getChildren();
                for(DataSnapshot key:keys)
                {
                    if(key.child(ConstantVariables.CODE).getValue().toString().equals(currentCourseCode))
                    {
                        DatabaseReference myRef=key.getRef();
                        myRef.child(ConstantVariables.NAME).setValue(newCourseName);
                        myRef.child(ConstantVariables.TERM).setValue(newCorseTerm);

                      /*  myRef.removeValue();
                        Course course=new Course(currentCourseCode,newCourseName,newCorseTerm);
                        myRef.push().setValue(course);*/
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void courseInfoPrint(final String currentCourseCode) {
        dbRefCourses.addListenerForSingleValueEvent(new ValueEventListener() {//burada eski kurs objesini atıp yenilenen kurs objesini katıyoruz
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> keys=dataSnapshot.getChildren();
                for(DataSnapshot key:keys)
                {
                    if(key.child(ConstantVariables.CODE).getValue().toString().equals(currentCourseCode))
                    {
                        courseNameForShow.setText(key.child(ConstantVariables.NAME).getValue().toString());
                        courseTermForShow.setText(key.child(ConstantVariables.TERM).getValue().toString());

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
        getMenuInflater().inflate(R.menu.menu_course_update,menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);
        if(item.getItemId()==R.id.updateCourse)
        {
            updateLayout.setVisibility(View.VISIBLE);
        }
        else if(item.getItemId()==R.id.createHomework)
        {
            newHomeworkDemand();
        }
        else if(item.getItemId()==R.id.deleteHomework)
        {
            deleteHomework();
        }
        else if(item.getItemId()==R.id.createPost)
        {
            creatPost();
        }
        else if(item.getItemId()==R.id.deletePost)
        {
            deletePost();
        }
        return true;
    }

    private void deleteHomework() {
        AlertDialog.Builder builder=new AlertDialog.Builder(CourseShowActivity.this,R.style.alertDialog);
        builder.setTitle("enter homework code");

        final EditText homeworkCodeField=new EditText(CourseShowActivity.this);
        homeworkCodeField.setHint("HW00");
        builder.setView(homeworkCodeField);

        builder.setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String homeworkCode=homeworkCodeField.getText().toString();
                if(TextUtils.isEmpty(homeworkCode))
                {
                    Toast.makeText(CourseShowActivity.this,"course name doesnt be empty ",Toast.LENGTH_LONG).show();
                }
                else
                {
                   deleteHomework(TeacherPage.myEmail,currentCourseCode,homeworkCode);

                }
            }
        });
        builder.show();
    }
    private void deleteHomework(final String myEmail,final String currentCourseCode,final String homeworkCode)
    {
        dbRefTeacher.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> keys=dataSnapshot.getChildren();
                for(DataSnapshot key:keys)
                {
                    if(key.child(ConstantVariables.EMAIL).getValue().toString().equals(myEmail))
                    {
                        DatabaseReference dbref=key.child("Courses").getRef();

                        dbref.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                Iterable<DataSnapshot> keys=dataSnapshot.getChildren();
                                for(DataSnapshot key:keys)
                                {
                                    if(key.getValue().toString().equals(currentCourseCode))
                                    {
                                        dbRefCourses.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                Iterable<DataSnapshot> keys=dataSnapshot.getChildren();
                                                for(DataSnapshot key:keys)
                                                {
                                                    if(key.child(ConstantVariables.CODE).getValue().toString().equals(currentCourseCode))
                                                    {
                                                        DatabaseReference dbRef=key.child(ConstantVariables.HOMEWORKS).getRef();
                                                        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                Iterable<DataSnapshot> keys=dataSnapshot.getChildren();
                                                                for(DataSnapshot key:keys) {
                                                                    if (key.getValue().toString().equals(homeworkCode)) {
                                                                        dbRefHomeworks.addListenerForSingleValueEvent(new ValueEventListener() {
                                                                            @Override
                                                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                                Iterable<DataSnapshot> keys=dataSnapshot.getChildren();
                                                                                for(DataSnapshot key:keys) {
                                                                                    if(key.child(ConstantVariables.CODE).getValue().toString().equals(homeworkCode))
                                                                                    {
                                                                                        DatabaseReference deleteRef=key.getRef();
                                                                                        deleteRef.removeValue();
                                                                                    }
                                                                                }
                                                                            }

                                                                            @Override
                                                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                                                            }
                                                                        });
                                                                            DatabaseReference deleteRef=key.getRef();
                                                                            deleteRef.removeValue();
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


    private void deletePost() {
        AlertDialog.Builder builder=new AlertDialog.Builder(CourseShowActivity.this,R.style.alertDialog);
        builder.setTitle("enter post code");

        final EditText postCodeField=new EditText(CourseShowActivity.this);
        postCodeField.setHint("POST000");
        builder.setView(postCodeField);

        builder.setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String postCode=postCodeField.getText().toString();
                if(TextUtils.isEmpty(postCode))
                {
                    Toast.makeText(CourseShowActivity.this,"post code doesnt be empty ",Toast.LENGTH_LONG).show();
                }
                else
                {
                    deletePost(postCode,currentCourseCode);

                }
            }
        });
        builder.show();
    }

    private void deletePost(final String postCode, final String currentCourseCode) {
        dbRefCourses.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> keys=dataSnapshot.getChildren();
                for(DataSnapshot key:keys)
                {
                    if(key.child(ConstantVariables.CODE).getValue().toString().equals(currentCourseCode))
                    {
                       final DatabaseReference dbRefPosts=key.child(ConstantVariables.POSTS).getRef();
                        dbRefPosts.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                boolean flag=true;//önce bu kurs adına kayıtlı kurs varmı diye bakıyoruz;
                                Iterable<DataSnapshot> keys=dataSnapshot.getChildren();
                                for(DataSnapshot key:keys)
                                {
                                    if(key.child(ConstantVariables.CODE).getValue().toString().equals(postCode))
                                    {
                                        flag=false;
                                    }
                                }

                                if(!flag)
                                {
                                    dbRefPosts.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            Iterable<DataSnapshot> keys=dataSnapshot.getChildren();
                                            for(DataSnapshot key:keys)
                                            {
                                                if(key.child(ConstantVariables.CODE).getValue().toString().equals(postCode))
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
                                else//bu code a sahip kurs varsa hata mesajı dönecek
                                    Toast.makeText(CourseShowActivity.this,"post code was not found",Toast.LENGTH_LONG).show();



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

    private void creatPost() {
        AlertDialog.Builder builder=new AlertDialog.Builder(CourseShowActivity.this,R.style.alertDialog);
        builder.setTitle("enter post code");

        final EditText postCodeField=new EditText(CourseShowActivity.this);
        postCodeField.setHint("POST000");
        builder.setView(postCodeField);

        builder.setPositiveButton("CREATE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String postCode=postCodeField.getText().toString();
                if(TextUtils.isEmpty(postCode))
                {
                    Toast.makeText(CourseShowActivity.this,"post code doesnt be empty ",Toast.LENGTH_LONG).show();
                }
                else
                {
                    creatPost(postCode,currentCourseCode);

                }
            }
        });
        builder.show();
    }

    private void creatPost(final String postCode,final String currentCourseCode) {
        dbRefCourses.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> keys=dataSnapshot.getChildren();
                for(DataSnapshot key:keys)
                {
                    if(key.child(ConstantVariables.CODE).getValue().toString().equals(currentCourseCode))
                    {
                        DatabaseReference dbRef=key.child(ConstantVariables.POSTS).getRef();
                        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                boolean flag=true;//önce bu kurs adına kayıtlı kurs varmı diye bakıyoruz;
                                Iterable<DataSnapshot> keys=dataSnapshot.getChildren();
                                for(DataSnapshot key:keys)
                                {
                                    if(key.child(ConstantVariables.CODE).getValue().toString().equals(postCode))
                                    {
                                        flag=false;
                                    }
                                }

                                if(flag)
                                {
                                    dbRefCourses.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            Iterable<DataSnapshot> keys=dataSnapshot.getChildren();
                                            for(DataSnapshot key:keys)
                                            {
                                                if(key.child(ConstantVariables.CODE).getValue().toString().equals(currentCourseCode))
                                                {

                                                    DatabaseReference myRef=key.child(ConstantVariables.POSTS).getRef();
                                                    PostList postList=new PostList();
                                                    Post newPost=new Post(postCode,postList);
                                                    myRef.push().setValue(newPost);

                                                }
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });
                                }
                                else//bu code a sahip kurs varsa hata mesajı dönecek
                                    Toast.makeText(CourseShowActivity.this,"post code are already used",Toast.LENGTH_LONG).show();



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
