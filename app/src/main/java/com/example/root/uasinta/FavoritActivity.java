package com.example.root.uasinta;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.annotation.SuppressLint;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.design.widget.AppBarLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.EditText;
import android.widget.LinearLayout;

import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class FavoritActivity extends AppCompatActivity  {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.recycleview)
    RecyclerView recyclerView;

    @BindView(R.id.empty)
    LinearLayout linearLayout;

    @BindView(R.id.layoutEmpty)
    LinearLayout layoutEmpty;

    @BindView(R.id.swlayout)
    SwipeRefreshLayout swipeRefreshLayout;

    @BindView(R.id.app_bar_search)
    AppBarLayout mAppBarSearch;

    @BindView(R.id.toolbar_search)
    Toolbar mToolbarSearch;

    Retrofit.Builder builder = new Retrofit.Builder()
            .baseUrl("http://maseko.000webhostapp.com/")
            .addConverterFactory(GsonConverterFactory.create());

    Retrofit retrofit = builder.build();

    UserClient userClient = retrofit.create(UserClient.class);

    private List<ResultData> results = new ArrayList<>();
    private FavoriteAdapter favoriteAdapter;
    private Menu menu;
    private Menu mMenu;
    private MenuItem menuItem;
    private SearchView mSearchView;
    private EditText mTextSearch;
    private String keyword = null;
    private BottomSheetFilter bottomSheetFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorit);
        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);
        mToolbar.setNavigationOnClickListener(toolbarClick);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Favorite");

        favoriteAdapter = new FavoriteAdapter(FavoritActivity.this, results);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(FavoritActivity.this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(favoriteAdapter);
        swipeRefreshLayout.setRefreshing(true);

        initSearchBar();

        loadDataFavorit();

        bottomSheetFilter = BottomSheetFilter.create();
        bottomSheetFilter.setOnFilterClickListener(onFilterClickListener());
        bottomSheetFilter.setOnResetClickListener(onResetClickListener());

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadDataFavorit();

            }
        });

    }

    private View.OnClickListener onFilterClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final TextView textView = (TextView) v;

                int userInput = Integer.parseInt(BottomSheetFilter.harga.toLowerCase());
                List<ResultData> stringList = new ArrayList<>();

                for (ResultData keyword : results) {
                    if (keyword.getHarga() <= userInput) {
                        stringList.add(keyword);
                    }
                }

                favoriteAdapter.updateList(stringList);

            }
        };
    }

    private View.OnClickListener onResetClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                List<ResultData> stringList = new ArrayList<>();

                for (ResultData keyword : results) {
                        stringList.add(keyword);
                }

                favoriteAdapter.updateList(stringList);
            }
        };
    }

    public void loadDataFavorit() {

        Call<ResponeServer> call = userClient.listFavorite(SaveSharedPreference.getUserIn(FavoritActivity.this));
        call.enqueue(new Callback<ResponeServer>() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onResponse(Call<ResponeServer> call, Response<ResponeServer> response) {
                if (response.isSuccessful()) {
                    swipeRefreshLayout.setRefreshing(false);
                    results = response.body().getResult();
                    favoriteAdapter = new FavoriteAdapter(FavoritActivity.this, results);
                    recyclerView.setAdapter(favoriteAdapter);
                    if (!results.isEmpty()) {
                        layoutEmpty.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                    } else {
                        layoutEmpty.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                    }

                } else {
                    swipeRefreshLayout.setRefreshing(false);
                    Toast.makeText(FavoritActivity.this, "Gagal", Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onFailure(Call<ResponeServer> call, Throwable t) {
                swipeRefreshLayout.setRefreshing(false);
                Toast.makeText(FavoritActivity.this, "onFailure", Toast.LENGTH_SHORT).show();

            }
        });
    }

    public void deleteAllFavorit() {

        swipeRefreshLayout.setRefreshing(true);

        Call<ResponeAddToFavorite> call = userClient.deleteAllFavorite(SaveSharedPreference.getUserIn(FavoritActivity.this));
        call.enqueue(new Callback<ResponeAddToFavorite>() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onResponse(Call<ResponeAddToFavorite> call, Response<ResponeAddToFavorite> response) {
                if (response.isSuccessful()) {

                    Toast.makeText(FavoritActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    swipeRefreshLayout.setRefreshing(false);
                } else {

                    Toast.makeText(FavoritActivity.this, "Gagal", Toast.LENGTH_SHORT).show();
                    swipeRefreshLayout.setRefreshing(false);

                }
            }

            @Override
            public void onFailure(Call<ResponeAddToFavorite> call, Throwable t) {
                swipeRefreshLayout.setRefreshing(false);
                Toast.makeText(FavoritActivity.this, "onFailure", Toast.LENGTH_SHORT).show();

            }
        });

        loadDataFavorit();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_favorite, menu);
        this.menu = menu;
        menu.findItem(R.id.action_search).setVisible(true);
        menu.findItem(R.id.action_hapus_semua).setVisible(true);
//        menuItem = menu.findItem(R.id.action_search);
//        SearchView searchView = (SearchView) menuItem.getActionView();
//        searchView.setOnQueryTextListener(this);
        return super.onCreateOptionsMenu(menu);
       // return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_search) {
            showSearchView();
            menuItem.expandActionView();
            return true;
        } else if (id == R.id.action_hapus_semua) {
            deleteAllFavorit();

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void initSearchBar() {
        if (mToolbarSearch != null) {
            mToolbarSearch.inflateMenu(R.menu.menu_search);
            mMenu = mToolbarSearch.getMenu();

            initSearchView();

            mToolbarSearch.setNavigationOnClickListener(toolbarClick);

            menuItem = mMenu.findItem(R.id.action_menu_search);

            menuItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
                @Override
                public boolean onMenuItemActionExpand(MenuItem menuItem) {
                    return true;
                }

                @Override
                public boolean onMenuItemActionCollapse(MenuItem menuItem) {
                    hideSearchView();
                    return true;
                }
            });

        } else {
            Log.d("toolbar", "setSearchtollbar: NULL");
        }
    }

    private void initSearchView() {
        mSearchView = (SearchView) mMenu.findItem(R.id.action_menu_search).getActionView();

        // Enable/Disable Submit button in the keyboard
        mSearchView.setSubmitButtonEnabled(false);

        // set hint and the text colors
        mTextSearch = mSearchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
        mTextSearch.setHint("Cari ...");
        mTextSearch.setHintTextColor(ContextCompat.getColor(this, R.color.black_38));
        mTextSearch.setTextColor(ContextCompat.getColor(this, R.color.black_87));
        mTextSearch.setPadding(0, 0, DensityUtils.dpToPx(8), 0);

        mSearchView.setOnQueryTextListener(mQueryTextListener);
    }

    private SearchView.OnQueryTextListener mQueryTextListener = new SearchView.OnQueryTextListener() {
        @Override
        public boolean onQueryTextSubmit(String query) {
//            keyword = query;
//            if (keyword.isEmpty()) keyword = null;
//           // loadOfficialAccounts(keyword);
//            mSearchView.clearFocus();
            return true;
        }

        @Override
        public boolean onQueryTextChange(String newText) {

            String userInput = newText.toLowerCase();
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

            favoriteAdapter.updateList(stringList);
            return true;
        }
    };

    private void showSearchView() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            circleReveal(mAppBarSearch, 1, true);
        } else {
            mAppBarSearch.setVisibility(View.VISIBLE);
        }
    }

    private void hideSearchView() {
        mSearchView.setQuery(null, true);
        keyword = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            circleReveal(mAppBarSearch, 1, false);
        } else {
            mAppBarSearch.setVisibility(View.GONE);
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void circleReveal(final View view, float posFromRight, final boolean isShow) {
        if (!ViewCompat.isAttachedToWindow(view)) return;
        int cx = mToolbar.getWidth() - (int) (getResources().getDimension(R.dimen.spacing_xlarge) * (0.5f + posFromRight));
        int cy = (mToolbar.getTop() + mToolbar.getBottom()) / 2;

        int dx = Math.max(cx, mToolbar.getWidth() - cx);
        int dy = Math.max(cy, mToolbar.getHeight() - cy);
        float finalRadius = (float) Math.hypot(dx, dy);

        Animator anim;
        AnimatorSet animatorSet = new AnimatorSet();

        if (isShow) {
            anim = ViewAnimationUtils.createCircularReveal(view, cx, cy, 0, finalRadius);

            animatorSet.setDuration((long) 250).addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    mSearchView.requestFocus();
                    ImeUtils.showIme(mSearchView);
                }
            });

            animatorSet.start();

        } else {
            mTextSearch.setText("");
            anim = ViewAnimationUtils.createCircularReveal(view, cx, cy, finalRadius, 0);
            animatorSet.setDuration((long) 250).start();
        }

        anim.setDuration((long) 250);

        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (!isShow) {
                    super.onAnimationEnd(animation);
                    view.setVisibility(View.GONE);
                    ImeUtils.hideIme(mSearchView);
                }
            }
        });

        if (isShow) {
            view.setVisibility(View.VISIBLE);
        }

        anim.start();
    }



    private View.OnClickListener toolbarClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            onBackPressed();
        }
    };

//    @Override
//    public boolean onQueryTextSubmit(String s) {
//        return false;
//    }
//
//    @Override
//    public boolean onQueryTextChange(String s) {
//
//      //  menuItem.collapseActionView();
//
//        String userInput = s.toLowerCase();
//        List<ResultData> stringList = new ArrayList<>();
//
//        for (ResultData keyword : results) {
//            if (keyword.getNamaProduk().toLowerCase().contains(userInput)) {
//                stringList.add(keyword);
//            }
//        }
//
//        favoriteAdapter.updateList(stringList);
//        return false;
//    }

    @Override
    public void onBackPressed() {
        if (mAppBarSearch.getVisibility() == View.VISIBLE) {
            hideSearchView();
        } else {
            super.onBackPressed();
        }
    }

    @OnClick(R.id.fabFilter)
    public void bukaFilter() {
        bottomSheetFilter.show(FavoritActivity.this);
    }
}
