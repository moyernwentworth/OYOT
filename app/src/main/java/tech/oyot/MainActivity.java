package tech.oyot;
//////////i hope it works
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private DrawerLayout drawer;

    ///logout//
    private FirebaseAuth mAuth;
    private TextView navName;
    private TextView navEmail;
    private FloatingActionButton newPost;
    private BottomNavigationView mainBottomNav;
    private String current_user_id;
    private FirebaseFirestore firebaseFirestore;
    private HomeFragment homeFragment;
    private NotificationFragment notificationFragment;
    private AccountFragment accountFragment;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth=FirebaseAuth.getInstance();
        firebaseFirestore=FirebaseFirestore.getInstance();

        navName=findViewById(R.id.nav_name);
        navEmail=findViewById(R.id.nav_email);
        mainBottomNav=findViewById(R.id.mainBottomNav);

        //toolbar
        Toolbar toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //fragments
        homeFragment=new HomeFragment();
        notificationFragment=new NotificationFragment();
        accountFragment=new AccountFragment();

        drawer=findViewById(R.id.drawer_layout);
        NavigationView navigationView =findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle= new ActionBarDrawerToggle(this,drawer,toolbar,
                R.string.navigation_draw_open,R.string.navigation_draw_close);

        drawer.addDrawerListener(toggle);
        toggle.syncState();

        mainBottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                switch (menuItem.getItemId()){
                    case R.id.botton_action_home:
                        replaceFragment(homeFragment);
                        return true;
                    case R.id.botton_action_notif:
                        replaceFragment(notificationFragment);
                        return true;
                    case R.id.botton_action_account:
                        replaceFragment(accountFragment);
                        return true;
                        default:
                            return false;

                }

            }
        });


    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()){
            case R.id.nav_logout:
                logOut();
                break;
            case R.id.nav_profile:
                profile();
                break;
            /////make more case statements for functions of the nav header
        }
        return true;
    }



    private void profile() {
        Intent setupIntent = new Intent(MainActivity.this, SetupActivity.class);
        startActivity(setupIntent);
        finish();
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)){
            drawer.closeDrawer(GravityCompat.START);
        }else {
            super.onBackPressed();

        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser= FirebaseAuth.getInstance().getCurrentUser();
///current user is not login
        if (currentUser == null) {
/////////////////send to login page
            sendToLoging();
        }else{
            current_user_id=mAuth.getCurrentUser().getUid();

            firebaseFirestore.collection("Users").document(current_user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()){
                        if(!task.getResult().exists()){

                            Intent setupIntent=new Intent(MainActivity.this,SetupActivity.class);
                            startActivity(setupIntent);
                            finish();
                        }
                    }else{
                        String error=task.getException().getMessage();
                        Toast.makeText(MainActivity.this, "error: "+error,Toast.LENGTH_LONG).show();
                    }
                }
            });
        }

    }


    private void logOut() {
        mAuth.signOut();
        sendToLoging();
    }


    private void sendToLoging() {

        Intent intent=new Intent(MainActivity.this,LoginActivity.class);
        startActivity(intent);
        finish();
    }
    private void replaceFragment(Fragment fragment){

        FragmentTransaction fragmentTransaction=getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.main_container,fragment);
        fragmentTransaction.commit();

    }
}
