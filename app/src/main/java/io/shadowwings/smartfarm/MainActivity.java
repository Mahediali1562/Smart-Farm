package io.shadowwings.smartfarm;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.gauravk.bubblenavigation.BubbleNavigationConstraintView;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.IOException;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    BubbleNavigationConstraintView BOTTOM_NAV;
//    ImageView BTN_UPLOAD;
    String VIDEO_TYPE = "";
    FloatingActionButton BTN_POST_VIDEO, BTN_POST_IMAGE;
    ExtendedFloatingActionButton BTN_POST;
    TextView addAlarmActionText, addPersonActionText;
    Boolean isAllFabsVisible;
    String FILE_PATH;

    private static final int PICK_IMAGE_REQUEST = 234;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.DARK_GREEN));
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);

        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.FRAME, new HomeFragment());
        transaction.commit();

        BOTTOM_NAV = findViewById(R.id.BOTTOM_NAV);
        /*
        BTN_UPLOAD = findViewById(R.id.BTN_UPLOAD);

        BTN_UPLOAD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, UploadActivity.class));
            }
        });
         */

        BOTTOM_NAV.setNavigationChangeListener((view, position) -> {
            switch (position){
                case 0:
                    BTN_POST_VIDEO.hide();
                    BTN_POST_IMAGE.hide();
                    addAlarmActionText.setVisibility(View.GONE);
                    addPersonActionText.setVisibility(View.GONE);
                    BTN_POST.shrink();

                    FragmentManager manager1 = getSupportFragmentManager();
                    FragmentTransaction transaction1 = manager1.beginTransaction();
                    transaction1.replace(R.id.FRAME, new HomeFragment());
                    transaction1.commit();
                    break;
                case 1:
                    BTN_POST_VIDEO.hide();
                    BTN_POST_IMAGE.hide();
                    addAlarmActionText.setVisibility(View.GONE);
                    addPersonActionText.setVisibility(View.GONE);
                    BTN_POST.shrink();

                    manager1 = getSupportFragmentManager();
                    transaction1 = manager1.beginTransaction();
                    transaction1.replace(R.id.FRAME, new SearchFragment());
                    transaction1.commit();
                    break;
                case 2:
                    BTN_POST_VIDEO.hide();
                    BTN_POST_IMAGE.hide();
                    addAlarmActionText.setVisibility(View.GONE);
                    addPersonActionText.setVisibility(View.GONE);
                    BTN_POST.shrink();

                    manager1 = getSupportFragmentManager();
                    transaction1 = manager1.beginTransaction();
                    transaction1.replace(R.id.FRAME, new AccountFragment());
                    transaction1.commit();
                    break;
            }
        });

        BTN_POST = findViewById(R.id.add_fab);
        BTN_POST_VIDEO = findViewById(R.id.add_person_fab);
        BTN_POST_IMAGE = findViewById(R.id.add_alarm_fab);
        addAlarmActionText = findViewById(R.id.add_alarm_action_text);
        addPersonActionText = findViewById(R.id.add_person_action_text);

        BTN_POST_VIDEO.setVisibility(View.GONE);
        BTN_POST_IMAGE.setVisibility(View.GONE);
        addAlarmActionText.setVisibility(View.GONE);
        addPersonActionText.setVisibility(View.GONE);

        isAllFabsVisible = false;

        BTN_POST.shrink();

        BTN_POST.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (!isAllFabsVisible) {
                            BTN_POST_VIDEO.show();
                            BTN_POST_IMAGE.show();
                            addAlarmActionText.setVisibility(View.VISIBLE);
                            addPersonActionText.setVisibility(View.VISIBLE);
                            BTN_POST.extend();
                            isAllFabsVisible = true;
                        } else {
                            BTN_POST_VIDEO.hide();
                            BTN_POST_IMAGE.hide();
                            addAlarmActionText.setVisibility(View.GONE);
                            addPersonActionText.setVisibility(View.GONE);
                            BTN_POST.shrink();
                            isAllFabsVisible = false;
                        }
                    }
                });

        BTN_POST_IMAGE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PICK_IMAGE();
            }
        });

        BTN_POST_VIDEO.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PICK_VIDEO();
            }
        });

    }

    private void PICK_VIDEO() {
        VIDEO_TYPE = "VIDEO";
        Intent intent = new Intent();
        intent.setType("video/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Video"), PICK_IMAGE_REQUEST);
    }

    private void PICK_IMAGE() {
        VIDEO_TYPE = "IMAGE";
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        BTN_POST_VIDEO.hide();
        BTN_POST_IMAGE.hide();
        addAlarmActionText.setVisibility(View.GONE);
        addPersonActionText.setVisibility(View.GONE);
        BTN_POST.shrink();

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            FILE_PATH = String.valueOf(data.getData());
            try {
                Intent intent = new Intent(MainActivity.this, UploadActivity.class);
                intent.putExtra("PATH", FILE_PATH);
                intent.putExtra("POST_TYPE", VIDEO_TYPE);
                startActivity(intent);
            }catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}