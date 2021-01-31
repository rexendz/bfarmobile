package com.adzu.bfarmobile.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.adzu.bfarmobile.R;
import com.adzu.bfarmobile.entities.Account;
import com.adzu.bfarmobile.entities.DatabaseUtil;
import com.adzu.bfarmobile.entities.OnGetDataListener;
import com.adzu.bfarmobile.fragments.AccountFragment;
import com.adzu.bfarmobile.fragments.AddOperatorFragment;
import com.adzu.bfarmobile.fragments.ListFragment;
import com.adzu.bfarmobile.fragments.ProfileFragment;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import net.cachapa.expandablelayout.ExpandableLayout;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, RadioGroup.OnCheckedChangeListener {

    private static final String TAG = "AWIT";
    private DrawerLayout drawer;
    private ExpandableLayout expandableLayout;
    private NavigationView navigationView;
    private MaterialSearchView searchView;
    private Spinner spinner;
    private RadioGroup rg1, rg2;
    private int filter = 0;
    private int statusfilter = 0;
    private boolean profile_exists = false;
    private ListFragment fragment1;
    private AddOperatorFragment fragment2;
    private AccountFragment fragment3;
    private ProfileFragment fragment4;
    private FrameLayout fragmentContainer;
    private String lastSearchText = "";
    private Toolbar toolbar;
    private FrameLayout searchContainer;
    private Account user;
    private Context ctx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ctx = this;


        spinner = findViewById(R.id.filter_active);

        rg1 = findViewById(R.id.filtergroup);
        rg2 = findViewById(R.id.filtergroup2);


        fragmentContainer = findViewById(R.id.fragment_container);

        searchContainer = findViewById(R.id.search_container);

        navigationView = findViewById(R.id.nav_view);
        drawer = findViewById(R.id.drawer_layout);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setCheckedItem(R.id.nav_operatorlist);
        navigationView.setNavigationItemSelectedListener(this);

        user = new Account();

        Intent intent = getIntent();
        user.setAdmin(intent.getBooleanExtra("account_admin", false));
        user.setUsername(intent.getStringExtra("account_username"));
        user.setFla_number(intent.getLongExtra("account_fla", 0));
        user.setFirstname(intent.getStringExtra("account_firstname"));
        user.setMiddlename(intent.getStringExtra("account_middlename"));
        user.setLastname(intent.getStringExtra("account_lastname"));
        user.setOperator(intent.getBooleanExtra("account_operator", true));
        user.setSim1(intent.getStringExtra("account_sim1"));



        if(user.isAdmin()) {
            fragment1 = new ListFragment();
            fragment2 = new AddOperatorFragment();
            fragment3 = new AccountFragment();
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, fragment1).commit();
            fragmentTransaction.addToBackStack("FRAGMENT1");
            showSearchView();
        }
        else {
            Menu navMenu = navigationView.getMenu();
            navMenu.findItem(R.id.nav_admin).setVisible(false);
            toolbar.setTitle("Operator Account");
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("operator");
            DatabaseUtil.readDataByFLA(user.getFla_number(), ref, new OnGetDataListener() {
                @Override
                public void dataRetrieved(DataSnapshot dataSnapshot) {
                    startProfileFragment(user.getFla_number(), user.isAdmin());
                    profile_exists = true;
                }

                @Override
                public void dataExists(DataSnapshot dataSnapshot) {

                }

                @Override
                public void onStart() {
                    new CountDownTimer(1000, 100) {

                        @Override
                        public void onTick(long l) {

                        }

                        @Override
                        public void onFinish() {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if(!profile_exists) {
                                            AlertDialog.Builder alert = new AlertDialog.Builder(ctx);
                                            alert.setTitle("Operator");
                                            alert.setMessage("Account is activated but your profile is not yet created.");
                                            alert.setPositiveButton("Logout", new DialogInterface.OnClickListener() {

                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    Intent intent = new Intent(ctx, LoginActivity.class);
                                                    startActivity(intent);
                                                    finish();
                                                }
                                            });
                                            alert.setNegativeButton("Continue", null);
                                            alert.show();
                                        }
                                    }
                                });
                        }
                    }.start();

                }

                @Override
                public void onFailure() {

                }
            });
        }
    }

    public void startProfileFragment(long fla, boolean isAdmin){
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

        if(user.isAdmin()) {
            searchView.closeSearch();
            fragmentContainer.setPadding(0, 0, 0, 0);
            expandableLayout.collapse();
            hideSearchView();
            fragmentTransaction.addToBackStack("FRAGMENT4");
        }
        toolbar.setTitle("Operator Profile");
        fragment4 = new ProfileFragment();
        Bundle bundle = new Bundle();
        bundle.putLong("fla_num", fla);
        bundle.putBoolean("is_admin", isAdmin);
        fragment4.setArguments(bundle);
        fragmentTransaction.replace(R.id.fragment_container, fragment4);
        fragmentTransaction.commit();
    }


    @Override
    public void onBackPressed() {
        if(drawer.isDrawerOpen(GravityCompat.START)){
            drawer.closeDrawer(GravityCompat.START);
        }
        if(user.isAdmin()) {
            if (searchView.isSearchOpen()) {
                searchView.closeSearch();
            }
            if (getSupportFragmentManager().getBackStackEntryCount() > 1) {
                int index = getSupportFragmentManager().getBackStackEntryCount() - 1;
                FragmentManager.BackStackEntry backEntry = getSupportFragmentManager().getBackStackEntryAt(index);
                String previousFragment = backEntry.getName();
                if (previousFragment.equals("FRAGMENT1")) {
                    hideSearchView();
                    fragment1.setActive(false);
                } else if (previousFragment.equals("FRAGMENT3")) {
                    fragment3.setActive(false);
                }

                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.popBackStackImmediate();

                Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
                if (currentFragment instanceof ListFragment)
                    showSearchView();
                if (currentFragment instanceof AccountFragment)
                    fragment3.setCurrentAccount(user);
            }
        }
    }

    public void populateCards(){
        if(fragment1.getOperatorAdapter() != null) {
            try {
                fragment1.getOperatorAdapter().setFilter(filter, statusfilter);
                fragment1.getOperatorAdapter().getFilter().filter(lastSearchText);
                if (fragment1.getOperatorAdapter().getItemCount() == 0)
                    fragment1.updateTextView(View.VISIBLE);
                else {
                    fragment1.updateTextView(View.GONE);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(user.isAdmin())
            createSearchView(menu);

        TextView acct_type = findViewById(R.id.acct_type);
        TextView acct_name = findViewById(R.id.acct_name);
        String loggedin = "Logged in as: " + user.getUsername();
        acct_name.setText(loggedin);

        if (user.isAdmin()){
        acct_type.setText("ADMIN");
        acct_type.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.drawable.button_primarypurple));
        } else if (user.isOperator()){
        acct_type.setText("OPERATOR");
        acct_type.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.drawable.button_gray));
        }
        return super.onCreateOptionsMenu(menu);
    }

    public void createSearchView(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        searchView = findViewById(R.id.search_view);
        searchView.setMenuItem(searchItem);
        expandableLayout = findViewById(R.id.expandable_layout);

        filter = 1;
        statusfilter = 0;

        populateCards();

        List<String> spinner_list = new ArrayList<>();
        spinner_list.add("All");
        spinner_list.add("Active");
        spinner_list.add("Expired");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner_item, spinner_list);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                statusfilter = i;
                populateCards();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        rg1.setOnCheckedChangeListener(this);
        rg2.setOnCheckedChangeListener(this);

        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                lastSearchText = newText;
                populateCards();
                return false;
            }
        });

        searchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {
                fragmentContainer.setPadding(0, 280, 0, 0);
                expandableLayout.expand();
            }


            @Override
            public void onSearchViewClosed() {
                fragmentContainer.setPadding(0, 0, 0, 0);
                expandableLayout.collapse();
                populateCards();
            }
        });

    }

    public void showSearchView(){
        try {
            searchContainer.setVisibility(View.VISIBLE);
            searchView.setVisibility(View.VISIBLE);
            findViewById(R.id.action_search).setVisibility(View.VISIBLE);
            populateCards();
        } catch(Exception e) {
            Log.d("Tester", "SearchView not yet created");
        }
    }

    public void hideSearchView(){
        try {
            searchView.closeSearch();
            searchContainer.setVisibility(View.GONE);
            searchView.setVisibility(View.GONE);
            findViewById(R.id.action_search).setVisibility(View.GONE);
        } catch(Exception e) {
            Log.d("Tester", "SearchView not yet created");
        }
    }

    public void replaceFragment(int i){
        if(i == 1) {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentContainer.setPadding(0, 0, 0, 0);
            fragmentTransaction.replace(R.id.fragment_container, fragment1, "FRAGMENT1").commit();
            showSearchView();
            populateCards();
            toolbar.setTitle("Fishpond Operators");
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        switch(item.getItemId()){
            case R.id.nav_operatorlist:
                fragmentContainer.setPadding(0, 0, 0, 0);
                fragment3.setActive(false);
                fragmentTransaction.replace(R.id.fragment_container, fragment1, "FRAGMENT1");
                fragmentTransaction.commit();
                fragmentTransaction.addToBackStack("FRAGMENT1");
                fragment1.setActive(true);
                showSearchView();
                populateCards();
                toolbar.setTitle("Fishpond Operators");
                break;
            case R.id.nav_manageope:
                fragmentContainer.setPadding(0, 0, 0, 0);
                expandableLayout.collapse();
                fragment1.setActive(false);
                fragment3.setActive(false);
                fragmentTransaction.replace(R.id.fragment_container, fragment2, "FRAGMENT2");
                fragmentTransaction.commit();
                fragmentTransaction.addToBackStack("FRAGMENT2");
                hideSearchView();
                toolbar.setTitle("Add New Operators");
                break;
            case R.id.nav_manageacc:
                fragmentContainer.setPadding(0, 0, 0, 0);
                expandableLayout.collapse();
                fragment1.setActive(false);
                fragment3.setCurrentAccount(user);
                fragmentTransaction.replace(R.id.fragment_container, fragment3, "FRAGMENT3");
                fragmentTransaction.commit();
                fragmentTransaction.addToBackStack("FRAGMENT3");
                hideSearchView();
                toolbar.setTitle("Manage Accounts");
                break;
            case R.id.nav_logout:
                Toast.makeText(getApplicationContext(), "Logged Out", Toast.LENGTH_LONG).show();
                if(user.isAdmin()) {
                    fragment1.setActive(false);
                    fragment3.setActive(false);
                }
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                finish();
                break;
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int i) {
        RadioButton rb = findViewById(i);
        RadioGroup rg1 = findViewById(R.id.filtergroup);
        RadioGroup rg2 = findViewById(R.id.filtergroup2);
        switch (radioGroup.getId()) {
            case R.id.filtergroup:
                rg2.setOnCheckedChangeListener(null);
                rg2.clearCheck();
                rg2.setOnCheckedChangeListener(this);
                filter = radioGroup.indexOfChild(rb);
                break;
            case R.id.filtergroup2:
                rg1.setOnCheckedChangeListener(null);
                rg1.clearCheck();
                rg1.setOnCheckedChangeListener(this);
                filter = radioGroup.indexOfChild(rb) + 3;
                break;
            default:
                Log.d("Tester", "Error");
        }

        populateCards();

    }
}