package com.startup.imagecloud.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.androidquery.util.XmlDom;
import com.loopj.android.http.RequestParams;
import com.startup.imagecloud.Base64;
import com.startup.imagecloud.MyUrl;
import com.startup.imagecloud.ProgressBarHandler;
import com.startup.imagecloud.R;
import com.startup.imagecloud.db.DbSupport;
import com.startup.imagecloud.db.ImageObj;
import com.telpoo.frame.utils.SPRSupport;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class CaptureFragment extends Fragment {
    private Uri fileUri;
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    static String TAG = "CaptureFragment";
    Bitmap bitmap = null;
    String idImage = "";
    ProgressBarHandler progressBarHandler;
    String IMAGE_DIRECTORY_NAME = "com.startup.imagecloud";
    ImageObj imageObj;
    SPRSupport mSPrSupport;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_caprute, container, false);
        // Inflate the layout for this fragment
        progressBarHandler = new ProgressBarHandler(getActivity());
        mSPrSupport = new SPRSupport();
        captureImage();
        return view;
    }

    /**
     * Launching camera app to capture image
     */
    public void captureImage() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        fileUri = Uri.fromFile(getOutputMediaFile());
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
        // start the image capture Intent
        startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
    }

    /**
     * returning image
     */
    public File getOutputMediaFile() {

        // External sdcard location
        File mediaStorageDir = new File(
                Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                IMAGE_DIRECTORY_NAME);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d(TAG, "Oops! Failed create "
                        + IMAGE_DIRECTORY_NAME + " directory");
                return null;
            }
        }
        // Create a media file name
        idImage = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(new Date());
        return new File(mediaStorageDir.getPath() + File.separator
                + "IMG_" + idImage + ".png");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                showPreview();
            } else if (resultCode == Activity.RESULT_CANCELED) {
                // user cancelled Image capture
                Toast.makeText(getActivity(),
                        "User cancelled image capture", Toast.LENGTH_SHORT)
                        .show();
            } else {
                // failed to capture image
                Toast.makeText(getActivity(),
                        "Sorry! Failed to capture image", Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }

    public void showPreview() {
        bitmap = BitmapFactory.decodeFile(fileUri.getPath());
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.preview_dialog);
        dialog.setCancelable(false);
        dialog.getWindow()
                .setBackgroundDrawableResource(R.drawable.transparent);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT);
        Button btnUpload = (Button) dialog.findViewById(R.id.btn_upload);
        Button btnCancel = (Button) dialog.findViewById(R.id.btn_cancel);
        ImageView imagePreview = (ImageView) dialog.findViewById(R.id.image_preview);
        imagePreview.setImageBitmap(bitmap);
        imageObj = new ImageObj();
        imageObj.set(ImageObj.ID, idImage);
        imageObj.set(ImageObj.PATH, fileUri.getPath());
        imageObj.set(ImageObj.UPLOADED, false);
        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();
                uploadImage();
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();
            }
        });
        dialog.show();
    }

    public void uploadImage() {
        progressBarHandler.show();
        RequestParams oj = new RequestParams();
        ByteArrayOutputStream stream1 = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream1);
        byte[] byteImg = stream1.toByteArray();
        String encodedImage = Base64.encodeBytes(byteImg);
        Log.d(TAG, encodedImage);

        Map<String, Object> params = new HashMap<>();
        params.put("employeeId", mSPrSupport.getString("employeeId", getActivity()));
        Log.d(TAG, "" + mSPrSupport.getString("employeeId", getActivity()));
        //Simply put a byte[] to the params, AQuery will detect it and treat it as a multi-part post
        params.put("imageCode", encodedImage);
        params.put("key", idImage);
        AQuery aq = new AQuery(getActivity());
        aq.id(R.id.image_preview).image(bitmap);
        aq.progress(R.id.progress).ajax(MyUrl.upload, params, XmlDom.class, new AjaxCallback<XmlDom>() {

            @Override
            public void callback(String url, XmlDom data, AjaxStatus status) {

                if (status.getCode() == AjaxStatus.NETWORK_ERROR) {
                    Toast.makeText(getActivity(), getString(R.string.network_err), Toast.LENGTH_SHORT).show();
                } else if (status.getCode() == 200) {
                    Toast.makeText(getActivity(), getString(R.string.upload_true), Toast.LENGTH_SHORT).show();
                    imageObj.set(ImageObj.UPLOADED, true);
                    DbSupport.updateImage(imageObj);
                } else {
                    Toast.makeText(getActivity(), getString(R.string.upload_false), Toast.LENGTH_SHORT).show();
                }
                Log.d(TAG, "" + data);
                Log.d(TAG, "" + status.getMessage());
                progressBarHandler.hide();
            }
        }.method(AQuery.METHOD_POST));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        progressBarHandler.hide();
    }
}
