package com.startup.imagecloud.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.startup.imagecloud.Helper;
import com.startup.imagecloud.R;
import com.startup.imagecloud.adapter.ImageLibraryAdapter;
import com.startup.imagecloud.db.DbSupport;
import com.startup.imagecloud.db.ImageObj;
import com.startup.imagecloud.db.MyDb;
import com.startup.imagecloud.db.TableDbObj;
import com.startup.imagecloud.service.SyncService;
import com.telpoo.frame.object.BaseObject;

import java.io.File;
import java.util.ArrayList;

public class LibraryFragment extends MyFragment {
    private ArrayList<BaseObject> images;
    int countSelect = 0;
    GridView gridView;
    ImageLibraryAdapter adapter;
    LinearLayout layoutMenu;
    ImageView imgDelete, imgSync;
    TextView txtMenuRight;
    AQuery aQuery;
    Boolean menuRightCheck = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_library, container, false);
        // Inflate the layout for this fragment
        aQuery = new AQuery(getActivity());
        gridView = (GridView) view.findViewById(R.id.gridview);
        layoutMenu = (LinearLayout) view.findViewById(R.id.menu_bottom);
        imgDelete = (ImageView) view.findViewById(R.id.img_delete);
        imgSync = (ImageView) view.findViewById(R.id.img_sync);
        images = MyDb.getAllOfTable(TableDbObj.IMAGE);
        adapter = new ImageLibraryAdapter(getActivity(), images);
        gridView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (menuRightCheck) {
                    BaseObject imageObj = images.get(position);
                    imageObj.set(ImageObj.SELECTED, !imageObj.getBool(ImageObj.SELECTED));
                    if (imageObj.getBool(ImageObj.SELECTED)) {
                        countSelect = countSelect + 1;
                        Log.d("gridView", "hien");
                    } else {
                        countSelect = countSelect - 1;
                        Log.d("gridView", "an");
                    }
                    if (countSelect > 0) {
                        layoutMenu.setVisibility(View.VISIBLE);
                    } else {
                        layoutMenu.setVisibility(View.GONE);
                    }
                    adapter.notifyDataSetChanged();
                    Log.d("gridView", countSelect + " ---");
                }

            }
        });
        imgDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogDelete();

            }
        });
        imgSync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Helper.isOnline(getActivity())) {
                    syncImage();
                } else {
                    Toast.makeText(getActivity(), getString(R.string.network_err), Toast.LENGTH_SHORT).show();
                }

            }
        });
        txtMenuRight = aQuery.id(R.id.txt_menu_right).getTextView();
        txtMenuRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("viewClicked", "viewClicked");
                menuRightCheck = !menuRightCheck;
                txtMenuRight.setText(R.string.select);
                if (menuRightCheck) {
                    adapter.notifyDataSetChanged();
                    txtMenuRight.setText(R.string.cancel);
                } else {
                    resetImage();
                }
            }
        });
        Helper.setRippe(imgDelete, getActivity());
        Helper.setRippe(imgSync,getActivity());
        return view;
    }

    public void resetImage() {
        countSelect = 0;
        layoutMenu.setVisibility(View.GONE);
        for (int i = 0; i < images.size(); i++) {
            images.get(i).set(ImageObj.SELECTED, false);
        }
        adapter.notifyDataSetChanged();
    }

    public void showDialogDelete() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.are_delete);
        builder.setPositiveButton(R.string.delete,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        deleteImage();
                    }
                });
        builder.setNegativeButton(R.string.cancel,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert = builder.create();
        alert.show();
    }

    public void deleteImage() {
        for (int i = images.size() - 1; i >= 0; i--) {
            BaseObject imageObj = images.get(i);
            if (imageObj.getBool(ImageObj.SELECTED)) {
                Boolean deleted = DbSupport.deleteImageById(imageObj.get(ImageObj.ID));
                Log.d("deleteImage", i + " : " + deleted);
                File file = new File(imageObj.get(ImageObj.PATH));
                if (deleted && file.delete()) {
                    images.remove(i);
                }
            }
        }
        adapter.notifyDataSetChanged();
    }

    public void syncImage() {
        Toast.makeText(getActivity(), getString(R.string.start_sync), Toast.LENGTH_SHORT).show();
        ArrayList<BaseObject> imagesUpload = new ArrayList<>();
        for (int i = 0; i < images.size(); i++) {
            BaseObject imageObj = images.get(i);
            if (imageObj.getBool(ImageObj.SELECTED)) {
                imagesUpload.add(imageObj);
            }
        }
        Intent intent = new Intent(getActivity(), SyncService.class);
        intent.putParcelableArrayListExtra("images", imagesUpload);
        getActivity().startService(intent);
    }

}
