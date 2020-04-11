package com.kunal.onlineconsultation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.sliderviewlibrary.SliderView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kunal.onlineconsultation.Adapter.Adapter;
import com.kunal.onlineconsultation.Adapter.SliderAdapter;
import com.kunal.onlineconsultation.Fragments.Chat;
import com.kunal.onlineconsultation.Fragments.ChatFragment;
import com.kunal.onlineconsultation.Fragments.HomeFragment;
import com.kunal.onlineconsultation.Fragments.ProfileFragment;
import com.kunal.onlineconsultation.Location.Location;
import com.kunal.onlineconsultation.Model.DoctorModel;
import com.kunal.onlineconsultation.Sendbird.ConnectionManager;
import com.kunal.onlineconsultation.Sendbird.PreferenceUtils;
import com.sendbird.android.SendBird;
import com.sendbird.android.SendBirdException;
import com.sendbird.android.User;
import com.shashank.sony.fancytoastlib.FancyToast;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
TextView logout,location;
    List<String> image;
    SliderAdapter viewPagerAdapter ;
    private SliderAdapter adapter;
    Fragment  current;

    // Recycler View object
    RecyclerView recyclerView;

    // Array list for recycler view data source
    ArrayList<DoctorModel> doctor_list;

    // Layout Manager
    RecyclerView.LayoutManager RecyclerViewLayoutManager;

    // adapter class object
    Adapter doctor_adapter;

    // Linear Layout Manager
    LinearLayoutManager HorizontalLayout;

    View ChildView;
    int RecyclerViewItemPosition;

    int DELAY_MS=20000,PERIOD_MS=15000;
    int currentPage=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        PreferenceUtils.init(getApplicationContext());

        loadLocale();

        SendBird.init("64509A71-5BCF-4393-BEE3-29BC9487AD1C",this);

        BottomNavigationView bottomNavigationView = findViewById(R.id.navi_view);
        bottomNavigationView.setOnNavigationItemSelectedListener(navigationItemSelectedListener);

        if (savedInstanceState==null){
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new HomeFragment(),"Home").commit();
        }
        if (FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()!= null){
            connect(FirebaseAuth.getInstance().getCurrentUser().getUid(),FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber());
        }
        else {
            connect(FirebaseAuth.getInstance().getCurrentUser().getUid(),FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
        }


//        doctor_list = new ArrayList<>();
//
//        // initialisation with id's
//        recyclerView
//                = (RecyclerView)findViewById(
//                R.id.doctors);
//
//
//
//        RecyclerViewLayoutManager
//                = new LinearLayoutManager(
//                getApplicationContext());
//
//        // Set LayoutManager on Recycler View
//        recyclerView.setLayoutManager(
//                RecyclerViewLayoutManager);
//
//        AddItemsToRecyclerViewArrayList();
//
//        doctor_adapter = new Adapter(MainActivity.this,doctor_list);
//
//        HorizontalLayout
//                = new LinearLayoutManager(
//                MainActivity.this,
//                LinearLayoutManager.HORIZONTAL,
//                false);
//        recyclerView.setLayoutManager(HorizontalLayout);
//
//        recyclerView.setAdapter(doctor_adapter);
//
//        doctor_adapter.notifyDataSetChanged();
////        final ViewPager sliderView = findViewById(R.id.imageSlider);
//
//        final SliderView sliderView = findViewById(R.id.imageSlider);
//
//        FirebaseDatabase Database = FirebaseDatabase.getInstance();
//        DatabaseReference mref = Database.getReference("Banners");
//
//        image = new ArrayList<>();
//        mref.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                for (int i = 1 ;i<=dataSnapshot.getChildrenCount();i++){
//                    String images= dataSnapshot.child("image"+i).child("url").getValue(String.class);
//                    image.add(images);
//                }
//
//
//                sliderView.setUrls((ArrayList<String>) image);
//
//                TimerTask task = sliderView.getTimerTask();
//                Timer timer = new Timer();
//
//                timer.schedule(task,DELAY_MS,PERIOD_MS);
//
////                viewPagerAdapter = new SliderAdapter(MainActivity.this,image);
////                sliderView.setAdapter(viewPagerAdapter);
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
//
//
//
//        FirebaseDatabase database = FirebaseDatabase.getInstance();
//        DatabaseReference reference = database.getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
//
//        location = findViewById(R.id.location);
//        reference.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                try {
//                    String city = dataSnapshot.child("location").getValue().toString();
//                    location.setText(city);
//                }catch (Exception e){
//                    FancyToast.makeText(getApplicationContext(),e.getMessage(),FancyToast.LENGTH_LONG,FancyToast.ERROR,true).show();
//                }
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
//
//        logout = findViewById(R.id.logout);
//        logout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                FirebaseAuth.getInstance().signOut();
//                Intent intent =new Intent(MainActivity.this, Location.class);
//                startActivity(intent);
//                finish();
//            }
//        });
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            Fragment selectedFragment = null;
            String tag = "";
            switch (menuItem.getItemId()){
                case R.id.nav_home: selectedFragment = new HomeFragment();tag = "Home";current = new HomeFragment();break;
                case R.id.nav_cart: selectedFragment = new ProfileFragment();tag = "Chat";current = new ProfileFragment();break;
                case R.id.nav_search: selectedFragment= new ChatFragment();tag = "Profile";current = new ChatFragment();break;
            }
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,selectedFragment,tag).commit();
            return true;
        }
    };

//    private void AddItemsToRecyclerViewArrayList() {
//        DatabaseReference doctors_ref = FirebaseDatabase.getInstance().getReference("Doctors");
//        doctors_ref.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                for (DataSnapshot ds :dataSnapshot.getChildren()){
//                    doctor_list.add(new DoctorModel(ds.child("profileimage").getValue(String.class),ds.child("email").getValue(String.class),ds.child("location").getValue(String.class),ds.child("name").getValue(String.class),ds.child("qualification").getValue(String.class),ds.child("rating").getValue(String.class),ds.child("uid").getValue(String.class)));
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
//    }

    private void connect(String uid,String nickName) {
        if (SendBird.getConnectionState() != SendBird.ConnectionState.OPEN) {
            ConnectionManager.connect(this, uid,nickName , new SendBird.ConnectHandler() {
                @Override
                public void onConnected(User user, SendBirdException e) {
                    if (e != null) {
                        e.printStackTrace();
                    } else {
                        //checkExtra();
                    }
                }
            });
        } else {
            //checkExtra();
        }
    }

    @Override
    public void onBackPressed() {


        if (current == new HomeFragment()){
            new AlertDialog.Builder(MainActivity.this)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle("Close")
                    .setMessage("Are you sure you want to close the App?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }

                    })
                    .setNegativeButton("No", null)
                    .show();
        }

        else {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new HomeFragment()).commit();
        }

    }

    private void setLocale(String s) {
        Locale locale = new Locale(s);
        Locale.setDefault(locale);
        Configuration configuration = new Configuration();
        configuration.locale = locale;
        getBaseContext().getResources().updateConfiguration(configuration,getBaseContext().getResources().getDisplayMetrics());
        SharedPreferences.Editor editor = getSharedPreferences("Settings",MODE_PRIVATE).edit();
        editor.putString("My_Lang",s);
        editor.apply();
    }

    public void loadLocale(){
        SharedPreferences preferences = getSharedPreferences("Settings", Activity.MODE_PRIVATE);
        String lang = preferences.getString("My_Lang","");
        setLocale(lang);
    }

    //    private void checkExtra() {
//        if (getIntent().hasExtra(EXTRA_GROUP_CHANNEL_URL)) {
//            String extraChannelUrl = getIntent().getStringExtra(EXTRA_GROUP_CHANNEL_URL);
//            Intent mainIntent = new Intent(MainActivity.this, GroupChannelActivity.class);
//            mainIntent.putExtra(EXTRA_GROUP_CHANNEL_URL, extraChannelUrl);
//            startActivity(mainIntent);
//        }
//    }
}
