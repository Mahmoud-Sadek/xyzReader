package com.example.amiraahabeeb.xyz_reader.ui;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.LoaderManager;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v13.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;

import com.example.amiraahabeeb.xyz_reader.R;
import com.example.amiraahabeeb.xyz_reader.data.ArticleLoader;
import com.example.amiraahabeeb.xyz_reader.data.ItemsContract;


public class ArticleDetailActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor> {

    private Cursor mCursor;
    private long startId;
    private long selectedItemId;
    private ViewPager pagerView;
    private MyPagerAdapter pagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                            View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        }
        setContentView(R.layout.activity_article_detail);
        getLoaderManager().initLoader(0, null, this);
        pagerAdapter = new MyPagerAdapter(getFragmentManager());
        pagerView = (ViewPager) findViewById(R.id.pager);
        pagerView.setAdapter(pagerAdapter);
        pagerView.setPageMargin((int) TypedValue
                .applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, getResources().getDisplayMetrics()));
        pagerView.setPageMarginDrawable(new ColorDrawable(0x22000000));

        pagerView.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);

            }

            @Override
            public void onPageSelected(int position) {
                if (mCursor != null) {
                    mCursor.moveToPosition(position);
                }
                selectedItemId = mCursor.getLong(ArticleLoader.Query._ID);
                updateUpButtonPosition();
            }
        });


        if (savedInstanceState == null) {
            if (getIntent() != null && getIntent().getData() != null) {
                startId = ItemsContract.Items.getItemId(getIntent().getData());
                selectedItemId = startId;
            }
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return ArticleLoader.newAllArticlesInstance(this);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        mCursor = cursor;
        pagerAdapter.notifyDataSetChanged();

        // Select the start ID
        if (startId > 0) {
            cursor.moveToFirst();
            // TODO: optimize
            while (!cursor.isAfterLast()) {
                if (cursor.getLong(ArticleLoader.Query._ID) == startId) {
                    final int position = cursor.getPosition();
                    pagerView.setCurrentItem(position, false);
                    break;
                }
                cursor.moveToNext();
            }
            startId = 0;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        mCursor = null;
        pagerAdapter.notifyDataSetChanged();
    }

    public void onUpButtonFloorChanged(long itemId, ArticleDetailFragment fragment) {
        if (itemId == selectedItemId) {
            updateUpButtonPosition();
        }
    }

    private void updateUpButtonPosition() {

    }

    private class MyPagerAdapter extends FragmentStatePagerAdapter {
        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public void setPrimaryItem(ViewGroup container, int position, Object object) {
            super.setPrimaryItem(container, position, object);
            ArticleDetailFragment fragment = (ArticleDetailFragment) object;
            if (fragment != null) {
                updateUpButtonPosition();
            }
        }

        @Override
        public Fragment getItem(int position) {
            mCursor.moveToPosition(position);
            return ArticleDetailFragment.newInstance(mCursor.getLong(ArticleLoader.Query._ID));
        }

        @Override
        public int getCount() {
            return (mCursor != null) ? mCursor.getCount() : 0;
        }
    }
}
