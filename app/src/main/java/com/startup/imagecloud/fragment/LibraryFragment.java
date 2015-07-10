package com.startup.imagecloud.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.startup.imagecloud.R;
import com.startup.imagecloud.adapter.ImageLibraryAdapter;
import com.startup.imagecloud.db.MyDb;
import com.startup.imagecloud.db.TableDbObj;
import com.telpoo.frame.object.BaseObject;

import java.util.ArrayList;

public class LibraryFragment extends Fragment {
    private ArrayList<BaseObject> images;
    GridView gridView;
    ImageLibraryAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_library, container, false);
        // Inflate the layout for this fragment
        gridView = (GridView) view.findViewById(R.id.gridview);
        images = MyDb.getAllOfTable(TableDbObj.IMAGE);
        adapter = new ImageLibraryAdapter(getActivity(), images);
        gridView.setAdapter(adapter);
        return view;
    }


}
