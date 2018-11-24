package com.soc.uoc.pqtm.mybooks;

import android.app.Activity;
import android.app.Application;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.blankj.utilcode.util.NetworkUtils;
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
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;
import com.orm.SugarContext;
import com.soc.uoc.pqtm.mybooks.adapter.BookListAdapter;
import com.soc.uoc.pqtm.mybooks.model.BookContent;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class BookListActivity extends AppCompatActivity {

    private static final String TAG = "LOGMYBOOK";
    private static final String BOOK_POSITION = "BOOK_POSITION";
    private static final String ACTION_DELETE = "ACTION_DELETE";
    private static final String ACTION_VIEW = "ACTION_VIEW";

    private boolean mTwoPane;
    private BookListAdapter adapter;
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

    private void doLogin() {
        mAuth.signInWithEmailAndPassword("marcfite@uoc.edu", "Palangana123!")
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "signInWithEmail:success");
                            getBooks();
                        } else {
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(BookListActivity.this, "*ERROR*: No s'ha pogut autenticar l'usuari.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void getBooks() {
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.w(TAG, "Firebase data changed.");
                GenericTypeIndicator<ArrayList<BookContent.BookItem>> t = new GenericTypeIndicator<ArrayList<BookContent.BookItem>>() {
                };
                mValues = dataSnapshot.getValue(t);

                Iterator<BookContent.BookItem> iterBooks = mValues.iterator();
                Long bid = 0L;
                while (iterBooks.hasNext()) {
                    iterBooks.next().setId(bid);
                    bid++;
                }

                for (BookContent.BookItem b : mValues) {
                    if (!BookContent.exists(b)) {
                        b.save();
                    }
                }
                updateUI(false);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Toast.makeText(BookListActivity.this, "*ERROR*: No s'ha pogut accedir a la BBDD.",
                        Toast.LENGTH_SHORT).show();
                Log.w(TAG, "Failed to read value.", error.toException());
                if (!NetworkUtils.isAvailableByPing()) {
                    adapter.setItems(BookContent.getBooks());
                }
            }
        });
    }

    private void updateUI(boolean deleted) {
        boolean warning = false;
        RecyclerView recyclerView = findViewById(R.id.book_list);
        adapter = new BookListAdapter(this, mValues, mTwoPane);
        if (deleted) {
            //refresh list if book was deleted
            adapter.setItems(BookContent.getBooks());
        }else {
            if (!NetworkUtils.isAvailableByPing()) {
                //if not inet show local database
                adapter.setItems(BookContent.getBooks());
                warning = true;
            }
        }
        recyclerView.setAdapter(adapter);
        if (warning) {
            Toast.makeText(BookListActivity.this, "*ERROR* Sense Internet, carregant BBDD local",
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getApplication().registerActivityLifecycleCallbacks(mCallbacks);
        super.onCreate(savedInstanceState);
        SugarContext.init(this);
        setContentView(R.layout.activity_book_list);
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("books");
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        //creem els submenus del menu lateral

        SecondaryDrawerItem subItem1 = new SecondaryDrawerItem().withIdentifier(1).withName("Share to other Apps");
        SecondaryDrawerItem subItem2 = new SecondaryDrawerItem().withIdentifier(2).withName("Copy to clipboard");
        SecondaryDrawerItem subItem3 = new SecondaryDrawerItem().withIdentifier(3).withName("Share in Whatsapp");

        //creem la capcalera del menu lateral

        AccountHeader headerResult = new AccountHeaderBuilder()
                .withActivity(this)
                //.withHeaderBackground(R.drawable.ic_launcher_background)
                .addProfiles(
                        new ProfileDrawerItem().withName("Marc Fite").withEmail("marc@fite.su").withIcon(getResources().getDrawable(R.drawable.material_drawer_badge))
                )
                .withOnAccountHeaderListener(new AccountHeader.OnAccountHeaderListener() {
                    @Override
                    public boolean onProfileChanged(View view, IProfile profile, boolean currentProfile) {
                        return false;
                    }
                })
                .build();

        //creem el menu lateral
        Drawer result = new DrawerBuilder()
                .withAccountHeader(headerResult)
                .withActivity(this)
                .withToolbar(toolbar)
                .addDrawerItems(
                        subItem1,
                        new DividerDrawerItem(),
                        subItem2,
                        new DividerDrawerItem(),
                        subItem3,
                        new DividerDrawerItem()
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        // do something with the clicked item :D
                        return true;
                    }
                })
                .build();

        final SwipeRefreshLayout swipeContainer = findViewById(R.id.swipeContainer);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getBooks();
                swipeContainer.setRefreshing(false);
            }
        });

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
                firebaseAuth.getCurrentUser();
            }
        };
        mAuth.addAuthStateListener(authStateListener);
        if (!NetworkUtils.isAvailableByPing()) {
            updateUI(false);
        } else {
            doLogin();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        //parse intent action
        NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (intent.getAction() != null) {
            Integer ibookPos = Integer.valueOf(intent.getStringExtra(BOOK_POSITION));
            String bookPos = intent.getStringExtra(BOOK_POSITION);
            //Validate Book Position
            if (ibookPos < 0 || ibookPos > BookContent.getBooks().size()) {
                Log.w(TAG, "Wrong book Position");
                Toast.makeText(BookListActivity.this, "*ERROR* Book Position not valid",
                        Toast.LENGTH_SHORT).show();
                nm.cancelAll();
                return;
            }

            if (intent.getAction().equalsIgnoreCase(ACTION_DELETE)) {
                Log.w(TAG, "Intent ACTION_DELETE");

                boolean aux = BookContent.delete(ibookPos);
                if (aux) {
                    Log.w(TAG, "Book Removed");
                    updateUI(true); //Update UI from local DB
                    Toast.makeText(BookListActivity.this, "Book deleted!",
                            Toast.LENGTH_SHORT).show();
                } else {
                    Log.w(TAG, "Book _NOT_ Removed");
                }
            } else if (intent.getAction().equalsIgnoreCase(ACTION_VIEW)) {
                Log.w(TAG, "Intent ACTION_VIEW");
                if (mTwoPane) {
                    //Large screen
                    Bundle arguments = new Bundle();
                    arguments.putString(BookDetailFragment.ARG_ITEM_ID, bookPos);
                    BookDetailFragment fragment = new BookDetailFragment();
                    fragment.setArguments(arguments);
                    FragmentManager manager = getSupportFragmentManager();
                    manager.beginTransaction()
                            .replace(R.id.book_detail_container, fragment)
                            .commit();
                } else {
                    //Small screen
                    Intent viewBookIntent = new Intent(getApplicationContext(), BookDetailActivity.class);
                    viewBookIntent.putExtra(BookDetailFragment.ARG_ITEM_ID, bookPos);
                    startActivity(viewBookIntent);
                }
            }
            //Cancel all notifications
            nm.cancelAll();
        }

    }
}