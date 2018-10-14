package com.soc.uoc.pqtm.mybooks;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.soc.uoc.pqtm.mybooks.adapter.BookListAdapter;
import com.soc.uoc.pqtm.mybooks.model.BookContent;
import java.util.ArrayList;
import java.util.Iterator;

public class BookListActivity extends AppCompatActivity {

    private static final String TAG = "LOG";

    private boolean mTwoPane;
    BookListAdapter adapter;
    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private static ArrayList<BookContent.BookItem> mValues;
    private MyActivityLifecycleCallbacks mCallbacks = new MyActivityLifecycleCallbacks();


    public static class MyActivityLifecycleCallbacks implements Application.ActivityLifecycleCallbacks {

        @Override
        public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

        }

        @Override
        public void onActivityStarted(Activity activity) {

        }

        @Override
        public void onActivityResumed(Activity activity) {

        }

        @Override
        public void onActivityPaused(Activity activity) {

        }

        @Override
        public void onActivityStopped(Activity activity) {
            FirebaseAuth.getInstance().signOut();
        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

        }

        @Override
        public void onActivityDestroyed(Activity activity) {

        }
    }

    private void  doLogin() {
        mAuth.signInWithEmailAndPassword("marcfite@uoc.edu", "Palangana123!")
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "signInWithEmail:success");
                            getBooks();
                        } else {
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                        }
                    }
                });
    }
    public void getBooks(){
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                GenericTypeIndicator<ArrayList<BookContent.BookItem>> t = new GenericTypeIndicator<ArrayList<BookContent.BookItem>>() {};
                mValues = dataSnapshot.getValue(t);
                Iterator<BookContent.BookItem> iterBooks = mValues.iterator();
                int bid = 0;
                while (iterBooks.hasNext()) {
                    iterBooks.next().setId(bid);
                    bid++;
                }
                //adapter.setBooks(mValues);
                updateUI();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }

    private void updateUI (){
        RecyclerView recyclerView = findViewById(R.id.book_list);
        adapter = new BookListAdapter(this,mValues, mTwoPane);
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getApplication().registerActivityLifecycleCallbacks(mCallbacks);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_list);
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("books");
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        if (findViewById(R.id.book_detail_container) != null) {
            mTwoPane = true;
        }
        FirebaseAuth.AuthStateListener authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                //firebaseAuth.getCurrentUser();
            }
        };
        mAuth.addAuthStateListener(authStateListener);
        doLogin();
    }
}