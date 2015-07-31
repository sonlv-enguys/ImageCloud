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
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.startup.imagecloud.Base64;
import com.startup.imagecloud.MyUrl;
import com.startup.imagecloud.ProgressBarHandler;
import com.startup.imagecloud.R;
import com.startup.imagecloud.db.DbSupport;
import com.startup.imagecloud.db.ImageObj;
import com.telpoo.frame.utils.SPRSupport;

import org.apache.http.Header;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class CaptureFragment extends MyFragment {
    private Uri fileUri;
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    static String TAG = "CaptureFragment";
    String idImage = "";
    ProgressBarHandler progressBarHandler;
    String IMAGE_DIRECTORY_NAME = "com.startup.imagecloud";
    ImageObj imageObj;
    SPRSupport mSPrSupport;
    ImageLoader imageLoader;
    ImageView imagePreview;

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
        imagePreview = (ImageView) view.findViewById(R.id.image_preview);

        mSPrSupport = new SPRSupport();
        imageLoader = ImageLoader.getInstance();
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
        imageLoader.displayImage(fileUri.toString(), imagePreview);
        imageObj = new ImageObj();
        imageObj.set(ImageObj.ID, idImage);
        imageObj.set(ImageObj.PATH, fileUri.getPath());
        imageObj.set(ImageObj.UPLOADED, false);
        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();
                DbSupport.updateImage(imageObj);
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

    Boolean uploading = true;

    //    public void uploadImage() {
//        progressBarHandler.show();
//        RequestParams oj = new RequestParams();
//        ByteArrayOutputStream stream1 = new ByteArrayOutputStream();
//        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream1);
//        byte[] byteImg = stream1.toByteArray();
//        oj.put("f", new ByteArrayInputStream(byteImg), "image.jpg");
//        oj.put("employeeId", mSPrSupport.getString("employeeId", getActivity()));
//        oj.put("fileName", idImage+"jpg");
//        oj.put("key", idImage);
//        AQuery aq = new AQuery(getActivity());
//        aq.id(R.id.image_preview).image(bitmap).clicked(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (!uploading) {
//                    captureImage();
//                }
//            }
//        });
//        AsyncHttpClient client = new AsyncHttpClient();
//        final TextHttpResponseHandler textResponse=new TextHttpResponseHandler() {
//            @Override
//            public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
//                Log.d("AsyncHttpClient","onFailure: "+s);
//            }
//
//            @Override
//            public void onSuccess(int i, Header[] headers, String s) {
//                Log.d("AsyncHttpClient","onSuccess: "+s);
//            }
//        };
////        textResponse.setCharset("UTF-8");
////        textResponse.setCharset("utf-8");
//        client.post(MyUrl.upload, oj,textResponse);
//
//
//    }
    public void uploadImage() {
        progressBarHandler.show();
        DisplayImageOptions option = new DisplayImageOptions.Builder().cacheOnDisk(false).bitmapConfig(Bitmap.Config.ARGB_8888).build();
        ImageLoadingListener lister = new SimpleImageLoadingListener() {
            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                super.onLoadingComplete(imageUri, view, loadedImage);
                ByteArrayOutputStream stream1 = new ByteArrayOutputStream();
                loadedImage.compress(Bitmap.CompressFormat.JPEG, 100, stream1);
                byte[] byteImg = stream1.toByteArray();
                postImage(byteImg);
            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                super.onLoadingFailed(imageUri, view, failReason);
            }
        };
        imageLoader.displayImage(fileUri.toString(), imagePreview, option, lister);

    }

    public void postImage(byte[] byteImg) {
        String encodedImage = Base64.encodeBytes(byteImg);
        Log.d(TAG, encodedImage);

        Map<String, Object> params = new HashMap<>();
        params.put("employeeId", mSPrSupport.getString("employeeId", getActivity()));
        Log.d(TAG, "" + mSPrSupport.getString("employeeId", getActivity()));
        params.put("imageCode", encodedImage);
        params.put("key", idImage);
        AQuery aq = new AQuery(getActivity());
        aq.id(R.id.image_preview).clicked(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!uploading) {
                    captureImage();
                }
            }
        });
        AjaxCallback<XmlDom> ajaxCallback = new AjaxCallback<XmlDom>() {

            @Override
            public void callback(String url, XmlDom data, AjaxStatus status) {
                uploading = false;
                if (status.getCode() == AjaxStatus.NETWORK_ERROR) {
                    Toast.makeText(getActivity(), getString(R.string.network_err), Toast.LENGTH_SHORT).show();
                } else if (status.getCode() == 200) {
                    imageObj.set(ImageObj.UPLOADED, true);
                    DbSupport.updateImage(imageObj);
                    Toast.makeText(getActivity(), getString(R.string.upload_true), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), getString(R.string.upload_false), Toast.LENGTH_SHORT).show();
                }
                Log.d(TAG, "" + data);
                Log.d(TAG, "" + status.getMessage());
                progressBarHandler.hide();
            }
        }.method(AQuery.METHOD_POST);
        ajaxCallback.setTimeout(60000);
        aq.progress(R.id.progress).ajax(MyUrl.upload, params, XmlDom.class, ajaxCallback);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        progressBarHandler.hide();
    }
}
