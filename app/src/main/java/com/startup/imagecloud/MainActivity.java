/*
 * Copyright (C) 2014 Chris Renke
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.startup.imagecloud;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.util.Xml;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxStatus;
import com.androidquery.util.XmlDom;
import com.startup.imagecloud.fragment.CaptureFragment;
import com.startup.imagecloud.fragment.HomeFragment;
import com.startup.imagecloud.fragment.LibraryFragment;
import com.startup.imagecloud.service.SyncService;
import com.telpoo.frame.utils.SPRSupport;

import java.util.ArrayList;
import java.util.Objects;

import static android.view.Gravity.START;

public class MainActivity extends FragmentActivity {

    private DrawerArrowDrawable drawerArrowDrawable;
    DrawerLayout drawer;
    private float offset;
    private boolean flipped;
    TextView txtCapture, txtHome, txtLogout, txtLibrary, txtSync;
    ImageView imgMenu;
    AQuery aQuery;
    Dialog dialog = null;
    SPRSupport mSPrSupport;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_view);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        imgMenu = (ImageView) findViewById(R.id.drawer_indicator);
        txtCapture = (TextView) findViewById(R.id.txt_capture);
        txtHome = (TextView) findViewById(R.id.txt_home);
        txtLogout = (TextView) findViewById(R.id.txt_logout);
        txtLibrary = (TextView) findViewById(R.id.txt_library);
        txtSync = (TextView) findViewById(R.id.txt_sync);
        final Resources resources = getResources();

        drawerArrowDrawable = new DrawerArrowDrawable(resources);
        drawerArrowDrawable.setStrokeColor(resources.getColor(R.color.light_gray));
        imgMenu.setImageDrawable(drawerArrowDrawable);

        drawer.setDrawerListener(new DrawerLayout.SimpleDrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                offset = slideOffset;

                // Sometimes slideOffset ends up so close to but not quite 1 or 0.
                if (slideOffset >= .995) {
                    flipped = true;
                    drawerArrowDrawable.setFlip(flipped);
                } else if (slideOffset <= .005) {
                    flipped = false;
                    drawerArrowDrawable.setFlip(flipped);
                }

                drawerArrowDrawable.setParameter(offset);
            }
        });

        imgMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (drawer.isDrawerVisible(START)) {
                    drawer.closeDrawer(START);
                } else {
                    drawer.openDrawer(START);
                }
            }
        });
        txtHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateView(new HomeFragment());
            }
        });
        txtLibrary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateView(new LibraryFragment());
            }
        });
        txtCapture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateView(new CaptureFragment());
            }
        });
        txtLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogLogout();
            }
        });
        txtSync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startService(new Intent(MainActivity.this, SyncService.class));
                drawer.closeDrawers();
            }
        });
        mSPrSupport = new SPRSupport();
        aQuery = new AQuery(this);
        updateView(new HomeFragment());
        checkLogin();

    }

    public void updateView(Fragment fragment) {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction ft = manager.beginTransaction();
        ft.replace(R.id.view_content, fragment);
        ft.commit();
        drawer.closeDrawers();

    }

    public void checkLogin() {
        String username = mSPrSupport.getString("username", this);
        String password = mSPrSupport.getString("password", this);
        String url = MyUrl.login + "code=" + username + "&password=" + password;
        aQuery.ajax(url, XmlDom.class, this, "checkLoginCallback");
    }

    public void showDialogLogout() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.are_logout);
        builder.setPositiveButton(R.string.logout,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        mSPrSupport.save("username", "", getApplicationContext());
                        mSPrSupport.save("password", "", getApplicationContext());
                        finish();
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

    public void showDialogLogin() {
        if (dialog == null) {
            dialog = new Dialog(this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.login_dialog);
            dialog.setCancelable(false);
            dialog.getWindow()
                    .setBackgroundDrawableResource(R.drawable.transparent);
            dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.WRAP_CONTENT);
            Button btnLogin = (Button) dialog.findViewById(R.id.btn_login);
            final EditText edtUsername = (EditText) dialog.findViewById(R.id.edit_username);
            final EditText edtPassword = (EditText) dialog.findViewById(R.id.edit_pass);
            btnLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String username = edtUsername.getText().toString();
                    String password = edtPassword.getText().toString();
                    login(username, password);
                }
            });
            edtPassword.setOnEditorActionListener(new EditText.OnEditorActionListener() {

                @Override

                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                        String username = edtUsername.getText().toString();
                        String password = edtPassword.getText().toString();
                        login(username, password);
                    }
                    return false;
                }
            });
        }
        dialog.show();
    }

    public void login(String username, String password) {
        mSPrSupport.save("username", username, this);
        mSPrSupport.save("password", password, this);
        String url = MyUrl.login + "code=" + username + "&password=" + password;
        Log.d("loginCallback", url);
        aQuery.progress(R.id.progress).ajax(url, XmlDom.class, this, "loginCallback");

    }

    //Login t? ??ng
    public void checkLoginCallback(String url, XmlDom data, AjaxStatus status) {
        Log.d("loginCallback", data.text("employeeId") + " : " + url);
        if (status.getCode() == 200 && data.text("employeeId").equals("0")) {
            showDialogLogin();
        }
        if (status.getCode() == 200 && !data.text("employeeId").equals("0")) {
            mSPrSupport.save("employeeId", data.text("employeeId"), getApplication());
            mSPrSupport.save("name", data.text("name"), getApplication());
        }
    }

    //Login t? form
    public void loginCallback(String url, XmlDom data, AjaxStatus status) {
        Log.d("loginCallback", data + ": " + status.getCode());
        if (status.getCode() == AjaxStatus.NETWORK_ERROR) {
            Toast.makeText(this, getString(R.string.network_err), Toast.LENGTH_SHORT).show();
        }
        if (status.getCode() == 200 && !data.text("employeeId").equals("0")) {
            Toast.makeText(this, getString(R.string.login_true), Toast.LENGTH_SHORT).show();
            mSPrSupport.save("employeeId", data.text("employeeId"), getApplication());
            mSPrSupport.save("name", data.text("name"), getApplication());
            dialog.dismiss();
        }
        if (status.getCode() == 200 && data.text("employeeId").equals("0")) {
            Toast.makeText(this, getString(R.string.login_false), Toast.LENGTH_SHORT).show();
        }

    }

}
