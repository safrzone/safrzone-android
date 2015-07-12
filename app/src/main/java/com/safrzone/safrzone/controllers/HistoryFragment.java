package com.safrzone.safrzone.controllers;

import android.database.ContentObserver;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleCursorAdapter;

import com.safrzone.safrzone.R;
import com.safrzone.safrzone.services.storage.HistoryContentProvider;
import com.safrzone.safrzone.services.storage.StorageHelper;
import com.safrzone.safrzone.views.HistoryView;

/**
 * History controller
 */
public class HistoryFragment
        extends Fragment
        implements LoaderManager.LoaderCallbacks<Cursor> {

    private HistoryView _view;
    private SimpleCursorAdapter _historyAdapter;
    private HistoryContentObserver _contentObserver;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // create data adapter
        _historyAdapter = new SimpleCursorAdapter(
                getActivity(),
                R.layout.item_history,
                null,
                new String[] { StorageHelper.COLUMN_QUERY },
                new int[] { R.id.text_view },
                0);

        // create view
        _view = new HistoryView(getActivity(), _historyAdapter);
        View resultView = _view.onCreateView(inflater, container, savedInstanceState);

        // load data
        getLoaderManager().initLoader(0, null, this);

        // observe data changes
        _contentObserver = new HistoryContentObserver(new Handler());
        getActivity().getContentResolver().registerContentObserver(
                HistoryContentProvider.CONTENT_URI,
                true,
                _contentObserver
        );

        return resultView;
    }

    @Override public void onDestroy() {
        super.onDestroy();

        _view.dispose();

        if (_contentObserver != null) {
            getActivity().getContentResolver().unregisterContentObserver(_contentObserver);
        }
    }

    @Override public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(
                getActivity(),
                HistoryContentProvider.CONTENT_URI,
                null,
                null,
                null,
                StorageHelper.COLUMN_ID + " DESC");
    }

    @Override public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        _historyAdapter.swapCursor(data);
    }

    @Override public void onLoaderReset(Loader<Cursor> loader) {
        _historyAdapter.swapCursor(null);
    }

    class HistoryContentObserver extends ContentObserver {
        public HistoryContentObserver(Handler handler) {
            super(handler);
        }

        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            if (!isAdded()) return;

            getLoaderManager().restartLoader(0, null, HistoryFragment.this);
        }
    }
}
