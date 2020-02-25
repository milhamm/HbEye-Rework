package com.imvlabs.hbey.Fragment;

import  android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.imvlabs.hbey.Adapter.HistoryAdapter;
import com.imvlabs.hbey.Entities.Person;
import com.imvlabs.hbey.Helper.Utilities;
import com.imvlabs.hbey.R;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;


/**
 * A simple {@link Fragment} subclass.
 */
public class HistoryFragment extends Fragment {
//    private final String TAG = "History";
    RecyclerView mRecyclerView;

    public HistoryFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_history, container, false);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.my_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        // Data
        // specify an adapter and data set
        String TAG = "onResume History Fragment";
        try{
        Realm.init(getActivity().getApplicationContext());
        Realm.setDefaultConfiguration(Utilities.getRealmConfig(getActivity().getApplicationContext()));
        Realm realm = Realm.getDefaultInstance();
        RealmResults<Person> results = realm.where(Person.class).findAllSorted("user_name", Sort.ASCENDING);
        HistoryAdapter mAdapter = new HistoryAdapter(getContext(), results, true, true);
        mRecyclerView.setAdapter(mAdapter);} catch(IndexOutOfBoundsException e){
            Log.d(TAG, "error index");
        }
    }
}
