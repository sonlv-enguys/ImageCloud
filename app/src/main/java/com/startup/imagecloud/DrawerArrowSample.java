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

import android.app.Dialog;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.startup.imagecloud.fragment.CaptureFragment;
import com.startup.imagecloud.fragment.HomeFragment;
import com.telpoo.frame.ui.BetaBaseFmActivity;

import static android.view.Gravity.START;

public class DrawerArrowSample extends FragmentActivity {

    private DrawerArrowDrawable drawerArrowDrawable;
    DrawerLayout drawer;
    private float offset;
    private boolean flipped;
    TextView txtCapture, txtHome;
    ImageView imgMenu;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_view);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        imgMenu = (ImageView) findViewById(R.id.drawer_indicator);
        txtCapture = (TextView) findViewById(R.id.txt_capture);
        txtHome = (TextView) findViewById(R.id.txt_home);
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
        txtCapture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateView(new CaptureFragment());
            }
        });
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
        showDialogLogin();
    }

    Dialog dialog=null;

    public void showDialogLogin() {

        if (dialog==null){
            dialog = new Dialog(this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.login_dialog);
            dialog.setCancelable(false);
            dialog.getWindow()
                    .setBackgroundDrawableResource(R.drawable.transparent);
            dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.WRAP_CONTENT);
            Button btnLogin = (Button) dialog.findViewById(R.id.btn_login);
            btnLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    login();
                }
            });
        }
        dialog.show();
    }

    public void login() {
        Toast.makeText(this, getString(R.string.login_true), Toast.LENGTH_SHORT).show();
        dialog.dismiss();
    }

    public void updateUserView() {
    }
}
