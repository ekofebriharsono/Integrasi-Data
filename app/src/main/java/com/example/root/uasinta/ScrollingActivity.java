package com.example.root.uasinta;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.RequiresApi;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewAnimationUtils;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ScrollingActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.app_bar)
    AppBarLayout mAppBarMain;

    @BindView(R.id.recycleview)
    RecyclerView recyclerView;

    @BindView(R.id.empty)
    LinearLayout linearLayout;

    @BindView(R.id.layoutResult)
    LinearLayout layoutResult;

    @BindView(R.id.fabFilter)
    FloatingActionButton fabFilter;

    @BindView(R.id.fab)
    FloatingActionButton fabSearch;

    @BindView(R.id.txtSearch)
    EditText txtSerach;

    @BindView(R.id.swlayout)
    SwipeRefreshLayout swipeRefreshLayout;

    Retrofit.Builder builder = new Retrofit.Builder()
            .baseUrl("http://maseko.000webhostapp.com/")
            .addConverterFactory(GsonConverterFactory.create());

    Retrofit retrofit = builder.build();

    UserClient userClient = retrofit.create(UserClient.class);

    private List<ResultData> results = new ArrayList<>();

    private DataScemaAdapter dataScemaAdapter;
    private BottomSheetFilter bottomSheetFilter;
    Dialog dialogTutorial;

    private char[] chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890".toCharArray();
    private StringBuilder stringBuilder = new StringBuilder();
    private Random random = new Random();
    private String output;
    private Menu menu;
    private Menu mMenu;
    private MenuItem menuItem;
    private SearchView mSearchView;
    private EditText mTextSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrolling);
        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        dataScemaAdapter = new DataScemaAdapter(ScrollingActivity.this, results);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(ScrollingActivity.this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(dataScemaAdapter);

        dialogTutorial = new Dialog(ScrollingActivity.this);
        dialogTutorial.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogTutorial.setContentView(R.layout.layout_dialog_tutorial);

        TextView textView = dialogTutorial.findViewById(R.id.mengerti);

       // layoutResult.setVisibility(View.VISIBLE);

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogTutorial.dismiss();
            }
        });

        dialogTutorial.show();

        bottomSheetFilter = BottomSheetFilter.create();
        bottomSheetFilter.setOnFilterClickListener(onFilterClickListener());
        bottomSheetFilter.setOnResetClickListener(onResetClickListener());

        for (int lenght = 0; lenght < 10; lenght++) {
            Character character = chars[random.nextInt(chars.length)];
            stringBuilder.append(character);
        }
        output = stringBuilder.toString();
        stringBuilder.delete(0, 10);

        if (SaveSharedPreference.getUserIn(ScrollingActivity.this) == null) {
            SaveSharedPreference.setUserIn(ScrollingActivity.this, output);
        } else {
            validationDataUser();
        }

        String user = SaveSharedPreference.getUserIn(ScrollingActivity.this);

        txtSerach.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                String userInput = txtSerach.getText().toString().toLowerCase();
                List<ResultData> stringList = new ArrayList<>();

                for (ResultData keyword : results) {
                    if (keyword.getNamaProduk().toLowerCase().contains(userInput)) {
                        stringList.add(keyword);
                    }
                }

               if (stringList.isEmpty()){
                 linearLayout.setVisibility(View.VISIBLE);
               } else {
                   linearLayout.setVisibility(View.GONE);
               }

                dataScemaAdapter.updateList(stringList);


            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadDataFromDBSwipe();
                txtSerach.setText("");
                hideKeyboard(txtSerach);

            }
        });

        //  Toast.makeText(ScrollingActivity.this, user ,Toast.LENGTH_SHORT).show();
    }

    private View.OnClickListener onFilterClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final TextView textView = (TextView) v;
                // textView.setOnClickListener(onFilterClickListener());
                //  Toast.makeText(ScrollingActivity.this, "Fitur not available!", Toast.LENGTH_SHORT).show();

                // Toast.makeText(ScrollingActivity.this,"as",Toast.LENGTH_SHORT).show();

                int userInput = Integer.parseInt(BottomSheetFilter.harga.toLowerCase());
                List<ResultData> stringList = new ArrayList<>();

                for (ResultData keyword : results) {
                    if (keyword.getHarga() <= userInput) {
                        stringList.add(keyword);
                    }
                }

                dataScemaAdapter.updateList(stringList);
            }
        };
    }

    private View.OnClickListener onResetClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Toast.makeText(ScrollingActivity.this,"asssss",Toast.LENGTH_SHORT).show();
                List<ResultData> stringList = new ArrayList<>();

                for (ResultData keyword : results) {
                    stringList.add(keyword);
                }
                dataScemaAdapter.updateList(stringList);
            }
        };
    }

    private View.OnClickListener toolbarClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            onBackPressed();
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_scrolling, menu);
        this.menu = menu;
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_settings) {
            dialogTutorial.show();
            return true;
        } else if (id == R.id.action_favorite) {
            startActivity(new Intent(ScrollingActivity.this, FavoritActivity.class));
            return true;
        }
        else if (id == R.id.action_cari) {
            final Dialog dialog = new Dialog(ScrollingActivity.this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.layout_dialog_cari);

            final EditText edCari = dialog.findViewById(R.id.EDcari);
            Button btnCari = dialog.findViewById(R.id.btnCari);

            btnCari.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!edCari.getText().toString().trim().isEmpty()) {
                        loadDataScema(edCari.getText().toString().trim());
                        dialog.hide();

                        hideKeyboard(edCari);

                    } else {
                        Toast.makeText(ScrollingActivity.this, "Keyword tidak boleh kosong!", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            dialog.show();
            return true;
        } else if (id == R.id.action_about) {
            startActivity(new Intent(ScrollingActivity.this, TentangActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void hideKeyboard(EditText editText){
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getWindowToken(),
                InputMethodManager.RESULT_UNCHANGED_SHOWN);
    }


    public void loadDataScema(String keyword) {
        final ProgressDialog dialog = new ProgressDialog(ScrollingActivity.this);
        dialog.setMessage("Loading ...");
        dialog.setCancelable(false);
        dialog.show();

        Call<ResponeServer> call = userClient.dataScema(keyword, SaveSharedPreference.getUserIn(ScrollingActivity.this));
        call.enqueue(new Callback<ResponeServer>() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onResponse(Call<ResponeServer> call, Response<ResponeServer> response) {
                if (response.isSuccessful()) {
                    dialog.hide();
                   /* results = response.body().getResult();

                    dataScemaAdapter = new DataScemaAdapter(ScrollingActivity.this, results);
                    recyclerView.setAdapter(dataScemaAdapter);
                    if (results != null) {
                        linearLayout.setVisibility(View.GONE);
                        fabFilter.setVisibility(View.VISIBLE);
                        Snackbar.make(view, "Data berhasil terintegrasi!", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    } else {
                        linearLayout.setVisibility(View.VISIBLE);
                    }*/
                    Toast.makeText(ScrollingActivity.this, "Berhasil mengambil data!", Toast.LENGTH_SHORT).show();
                    loadDataFromDB();

                } else {
                    dialog.hide();
                    Toast.makeText(ScrollingActivity.this, "Gagal", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponeServer> call, Throwable t) {
                dialog.hide();
                Toast.makeText(ScrollingActivity.this, "Berhasil mengambil data!", Toast.LENGTH_SHORT).show();
                loadDataFromDB();
            }
        });
    }

    public void loadDataFromDB() {
        final ProgressDialog dialog = new ProgressDialog(ScrollingActivity.this);
        dialog.setMessage("Loading ...");
        dialog.setCancelable(false);
        dialog.show();

        Call<ResponeServer> call = userClient.listData(SaveSharedPreference.getUserIn(ScrollingActivity.this));
        call.enqueue(new Callback<ResponeServer>() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onResponse(Call<ResponeServer> call, Response<ResponeServer> response) {
                if (response.isSuccessful()) {
                    dialog.hide();
                    results = response.body().getResult();

                    dataScemaAdapter = new DataScemaAdapter(ScrollingActivity.this, results);
                    recyclerView.setAdapter(dataScemaAdapter);
                    if (!results.isEmpty()) {
                        linearLayout.setVisibility(View.GONE);
                        fabFilter.setVisibility(View.VISIBLE);
                        fabSearch.setVisibility(View.GONE);
                        layoutResult.setVisibility(View.VISIBLE);
                        Snackbar.make(findViewById(R.id.container), "Data berhasil terintegrasi!", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();

                    } else {
                        linearLayout.setVisibility(View.VISIBLE);
                        layoutResult.setVisibility(View.GONE);
                    }

                } else {
                    dialog.hide();
                    Toast.makeText(ScrollingActivity.this, "Gagal", Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onFailure(Call<ResponeServer> call, Throwable t) {
                dialog.hide();
                Toast.makeText(ScrollingActivity.this, "onFailure", Toast.LENGTH_SHORT).show();
                linearLayout.setVisibility(View.VISIBLE);
                layoutResult.setVisibility(View.GONE);

            }
        });
    }

    public void loadDataFromDBDesc() {
        final ProgressDialog dialog = new ProgressDialog(ScrollingActivity.this);
        dialog.setMessage("Loading ...");
        dialog.setCancelable(false);
        dialog.show();

        Call<ResponeServer> call = userClient.listDataDesc(SaveSharedPreference.getUserIn(ScrollingActivity.this));
        call.enqueue(new Callback<ResponeServer>() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onResponse(Call<ResponeServer> call, Response<ResponeServer> response) {
                if (response.isSuccessful()) {
                    dialog.hide();
                    results = response.body().getResult();

                    dataScemaAdapter = new DataScemaAdapter(ScrollingActivity.this, results);
                    recyclerView.setAdapter(dataScemaAdapter);
                    if (!results.isEmpty()) {
                        linearLayout.setVisibility(View.GONE);
                        fabFilter.setVisibility(View.VISIBLE);
                        fabSearch.setVisibility(View.GONE);
                        layoutResult.setVisibility(View.VISIBLE);
                        Snackbar.make(findViewById(R.id.container), "Mengurutkan harga paling murah!", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();

                    } else {
                        linearLayout.setVisibility(View.VISIBLE);
                        layoutResult.setVisibility(View.GONE);
                    }

                } else {
                    dialog.hide();
                    Toast.makeText(ScrollingActivity.this, "Gagal", Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onFailure(Call<ResponeServer> call, Throwable t) {
                dialog.hide();
                Toast.makeText(ScrollingActivity.this, "onFailure", Toast.LENGTH_SHORT).show();
                linearLayout.setVisibility(View.VISIBLE);
                layoutResult.setVisibility(View.GONE);

            }
        });
    }

    public void loadDataFromDBSwipe() {


        Call<ResponeServer> call = userClient.listData(SaveSharedPreference.getUserIn(ScrollingActivity.this));
        call.enqueue(new Callback<ResponeServer>() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onResponse(Call<ResponeServer> call, Response<ResponeServer> response) {
                if (response.isSuccessful()) {

                    results = response.body().getResult();

                    dataScemaAdapter = new DataScemaAdapter(ScrollingActivity.this, results);
                    recyclerView.setAdapter(dataScemaAdapter);
                    if (!results.isEmpty()) {
                        linearLayout.setVisibility(View.GONE);
                        fabFilter.setVisibility(View.VISIBLE);
                        fabSearch.setVisibility(View.GONE);
                        layoutResult.setVisibility(View.VISIBLE);
                        swipeRefreshLayout.setRefreshing(false);

                    } else {
                        linearLayout.setVisibility(View.VISIBLE);
                        layoutResult.setVisibility(View.GONE);
                        swipeRefreshLayout.setRefreshing(false);
                    }

                } else {
                    Toast.makeText(ScrollingActivity.this, "Gagal", Toast.LENGTH_SHORT).show();
                    swipeRefreshLayout.setRefreshing(false);

                }
            }

            @Override
            public void onFailure(Call<ResponeServer> call, Throwable t) {
                Toast.makeText(ScrollingActivity.this, "onFailure", Toast.LENGTH_SHORT).show();
                linearLayout.setVisibility(View.VISIBLE);
                layoutResult.setVisibility(View.GONE);
                swipeRefreshLayout.setRefreshing(false);

            }
        });
    }

    @OnClick(R.id.fab)
    public void carBarang(final View view1) {

        final Dialog dialog = new Dialog(ScrollingActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_dialog_cari);

        final EditText edCari = dialog.findViewById(R.id.EDcari);
        Button btnCari = dialog.findViewById(R.id.btnCari);

        btnCari.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!edCari.getText().toString().trim().isEmpty()) {
                    loadDataScema(edCari.getText().toString().trim());
                    dialog.hide();
                } else {
                    Toast.makeText(ScrollingActivity.this, "Keyword tidak boleh kosong!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        dialog.show();
    }

    public void validationDataUser() {
        if (SaveSharedPreference.getUserIn(ScrollingActivity.this) != null) {
            loadDataFromDB();
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @OnClick(R.id.fabFilter)
    public void bukaFilter() {
        bottomSheetFilter.show(ScrollingActivity.this);
    }

    @OnClick(R.id.btnUrutkan)
    public void urutkan() {
        loadDataFromDBDesc();
    }

}
