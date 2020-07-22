package com.adzu.bfarmobile.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
import com.adzu.bfarmobile.fragments.AddRecordFragment;
import com.adzu.bfarmobile.fragments.ListFragment;
import com.adzu.bfarmobile.fragments.ProfileFragment;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;

import net.cachapa.expandablelayout.ExpandableLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, RadioGroup.OnCheckedChangeListener {

    private DrawerLayout drawer;
    private ExpandableLayout expandableLayout;
    private NavigationView navigationView;
    private MaterialSearchView searchView;
    private Spinner spinner;
    private RadioGroup rg1, rg2;
    private int filter = 0;
    private int statusfilter = 0;
    private String account_key;
    private ListFragment fragment1;
    private AddOperatorFragment fragment2;
    private AccountFragment fragment3;
    private ProfileFragment fragment4;
    private AddRecordFragment fragment5;
    private FrameLayout fragmentContainer;
    private String lastSearchText = "";
    private Toolbar toolbar;
    private FrameLayout searchContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        spinner = findViewById(R.id.filter_active);

        rg1 = findViewById(R.id.filtergroup);
        rg2 = findViewById(R.id.filtergroup2);


        fragmentContainer = findViewById(R.id.fragment_container);

        searchContainer = findViewById(R.id.search_container);

        navigationView = findViewById(R.id.nav_view);
        drawer = findViewById(R.id.drawer_layout);
        fragment2 = new AddOperatorFragment();
        fragment3 = new AccountFragment();
        fragment5 = new AddRecordFragment();

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setCheckedItem(R.id.nav_operatorlist);
        navigationView.setNavigationItemSelectedListener(this);

        Intent intent = getIntent();
        account_key = intent.getStringExtra("account_key");

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("account");
        DatabaseUtil.readDataByKey(account_key, ref, new OnGetDataListener() {
            @Override
            public void dataRetrieved(DataSnapshot dataSnapshot) {
                Account user = new Account((Map<String, Object>) dataSnapshot.getValue());
                fragment1 = new ListFragment();
                fragment1.setUser(user);
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment1).commit();
                showSearchView();
                TextView acct_name = findViewById(R.id.acct_name);
                TextView acct_type = findViewById(R.id.acct_type);
                String loggedin = "Logged in as: " + user.getUsername();
                acct_name.setText(loggedin);
                if(user.isAdmin()) {
                    acct_type.setText("ADMIN");
                    acct_type.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.button_primarypurple));
                }
                else if(user.isOperator()) {
                    acct_type.setText("OPERATOR");
                    acct_type.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.button_gray));
                }
                else
                    acct_type.setVisibility(View.GONE);
            }

            @Override
            public void dataExists(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onStart() {

            }

            @Override
            public void onFailure() {

            }
        });

    }

    public void startProfileFragment(long fla){
        searchView.closeSearch();
        fragmentContainer.setPadding(0, 0, 0, 0);
        expandableLayout.collapse();
        toolbar.setTitle("Operator Profile");
        hideSearchView();
        fragment4 = new ProfileFragment();
        Bundle bundle = new Bundle();
        bundle.putLong("fla_num", fla);
        fragment4.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment4).commit();
    }


    @Override
    public void onBackPressed() {
        if(drawer.isDrawerOpen(GravityCompat.START)){
            drawer.closeDrawer(GravityCompat.START);
        }
        else if(searchView.isSearchOpen()){
            searchView.closeSearch();
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
        createSearchView(menu);
        return super.onCreateOptionsMenu(menu);
    }

    public void createSearchView(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        searchView = findViewById(R.id.search_view);
        searchView.setMenuItem(searchItem);
        expandableLayout = findViewById(R.id.expandable_layout);

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

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case R.id.nav_operatorlist:
                fragmentContainer.setPadding(0, 0, 0, 0);
                fragment3.setActive(false);
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment1).commit();
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
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment2).commit();
                hideSearchView();
                toolbar.setTitle("Add New Operators");
                break;
            case R.id.nav_manageacc:
                fragmentContainer.setPadding(0, 0, 0, 0);
                expandableLayout.collapse();
                fragment1.setActive(false);
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment3).commit();
                hideSearchView();
                toolbar.setTitle("Manage Accounts");
                break;
            case R.id.nav_addrecord:
                fragmentContainer.setPadding(0, 0, 0, 0);
                expandableLayout.collapse();;
                fragment1.setActive(false);
                fragment3.setActive(false);
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment5).commit();
                hideSearchView();
                toolbar.setTitle("Add Record");
                break;
            case R.id.nav_logout:
                account_key = "";
                Toast.makeText(getApplicationContext(), "Logged Out", Toast.LENGTH_LONG).show();
                fragment1.setActive(false);
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