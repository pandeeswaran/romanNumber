package com.talktiva.pilot.activity;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.provider.Settings;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.talktiva.pilot.R;
import com.talktiva.pilot.helper.AppConstant;
import com.talktiva.pilot.helper.ImageFilePath;
import com.talktiva.pilot.helper.Utility;
import com.talktiva.pilot.rest.FileUploader;
import com.talktiva.pilot.rest.FileUploaderCallback;
import com.talktiva.pilot.results.ResultError;
import com.talktiva.pilot.results.ResultMessage;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import id.zelory.compressor.Compressor;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

public class AddressProofActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 123;

    private static final int REQUEST_IMAGE_GALLERY = 1;
    private static final int REQUEST_IMAGE_CAMERA = 2;

    private final String[] appPermissions = {
            Manifest.permission.CAMERA};

    @BindView(R.id.apa_iv_info)
    ImageView ivInfo;
    @BindView(R.id.apa_tv)
    TextView textView;
    @BindView(R.id.apa_iv_cancel)
    ImageView ivCancel;
    @BindView(R.id.apa_tv_error)
    TextView tvError;
    @BindView(R.id.apa_tv_upload)
    TextView tvUpload;
    @BindView(R.id.apa_iv_folder)
    ImageView ivFolder;
    @BindView(R.id.apa_iv_camera)
    ImageView ivCamera;
    @BindView(R.id.apa_iv_help)
    ImageView ivHelp;
    @BindView(R.id.apa_tv_privacy)
    TextView tvPrivacy;
    @BindView(R.id.apa_btn_upload)
    Button btnUpload;
    @BindView(R.id.apa_tv_or)
    TextView tvOr;
    @BindView(R.id.apa_tv_skip)
    TextView tvSkip;

    private Dialog internetDialog, dialogPermission;
    private ProgressDialog dialog;
    private String from;
    private Integer id;
    private File file;
    private Uri uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_address_proof);
        ButterKnife.bind(this);

        from = getIntent().getStringExtra(AppConstant.FROM);
        id = getIntent().getIntExtra(AppConstant.ID, 0);

        textView.setTypeface(Utility.INSTANCE.getFontRegular());
        tvError.setTypeface(Utility.INSTANCE.getFontRegular());
        tvUpload.setTypeface(Utility.INSTANCE.getFontRegular());
        tvPrivacy.setTypeface(Utility.INSTANCE.getFontRegular());
        btnUpload.setTypeface(Utility.INSTANCE.getFontRegular());
        tvOr.setPaintFlags(tvOr.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        tvOr.setTypeface(Utility.INSTANCE.getFontRegular());
        tvSkip.setTypeface(Utility.INSTANCE.getFontRegular());

        if (Build.VERSION.SDK_INT >= 24) {
            try {
                Method m = StrictMode.class.getMethod("disableDeathOnFileUriExposure");
                m.invoke(null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (from.equals(AppConstant.SIGNUP)) {
            tvOr.setVisibility(View.VISIBLE);
            tvSkip.setVisibility(View.VISIBLE);
            ivCancel.setVisibility(View.VISIBLE);
        } else {
            tvOr.setVisibility(View.GONE);
            tvSkip.setVisibility(View.GONE);
            ivCancel.setVisibility(View.VISIBLE);
        }

        tvOr.setOnClickListener(v -> {
            startActivity(new Intent(AddressProofActivity.this, DashBoardActivity.class));
            finish();
        });

        ivFolder.setOnClickListener(v -> {
                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                    intent.setType("image/*");
                    startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQUEST_IMAGE_GALLERY);
                }
//        startActivityForResult(Utility.INSTANCE.getPickImageChooserForGallery(AddressProofActivity.this), REQUEST_IMAGE_GALLERY)
        );

        ivCamera.setOnClickListener(v -> {
            Intent pictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (pictureIntent.resolveActivity(getPackageManager()) != null) {
                File photoFile = Utility.INSTANCE.createImageFile();
                if (photoFile != null) {
                    uri = FileProvider.getUriForFile(this, "com.talktiva.pilot.provider", photoFile);
                    pictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                    startActivityForResult(pictureIntent, REQUEST_IMAGE_CAMERA);
                }
            }
        });

        ivHelp.setOnClickListener(v -> {
            internetDialog = Utility.INSTANCE.showAlert(AddressProofActivity.this, R.color.colorPrimary, R.string.dd_info_img, v1 -> {
                Utility.INSTANCE.dismissDialog(internetDialog);
            });
            internetDialog.show();
        });

        btnUpload.setOnClickListener(v -> {
            if (!file.exists()) {
                tvError.setText(R.string.apa_tv_missing);
                tvError.setTextColor(getResources().getColor(R.color.red));
                tvError.setVisibility(View.VISIBLE);
            } else {
                tvError.setText(null);
                tvError.setVisibility(View.GONE);
                if (Utility.INSTANCE.isConnectingToInternet()) {
                    uploadFile(uri, file);
                }
            }
        });

        ivCancel.setOnClickListener(v -> onBackPressed());

        checkAndRequestPermission();
    }

    private void checkAndRequestPermission() {
        List<String> listPermissionNeeded = new ArrayList<>();
        for (String permission : appPermissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                listPermissionNeeded.add(permission);
            }
        }
        if (!listPermissionNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionNeeded.toArray(new String[0]), PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            final List<String> deniedPermissions = new ArrayList<>();
            if (grantResults.length > 0) {
                for (int i = 0; grantResults.length > i; i++) {
                    if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                        deniedPermissions.add(permissions[i]);
                    }
                }
                if (!deniedPermissions.isEmpty()) {
                    dialogPermission = Utility.INSTANCE.showAlert(AddressProofActivity.this, R.string.dd_permission_msg, false, View.VISIBLE, R.string.dd_yes, v -> {
                        dialogPermission.dismiss();
                        for (int i = 0; deniedPermissions.size() > i; i++) {
                            if (ActivityCompat.shouldShowRequestPermissionRationale(AddressProofActivity.this, deniedPermissions.get(i))) {
                                checkAndRequestPermission();
                            }
                        }
                    }, View.VISIBLE, R.string.dd_setting, v -> {
                        dialogPermission.dismiss();
                        Intent intent = new Intent();
                        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", getPackageName(), null);
                        intent.setData(uri);
                        startActivity(intent);
                    });
                    dialogPermission.show();
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case REQUEST_IMAGE_GALLERY:
                    if (data != null) {
                        uri = data.getData();
                        String path = ImageFilePath.getPath(AddressProofActivity.this, uri);
                        file = new File(path);
//                        tvError.setText(file.getName());
//                        tvError.setTextColor(getResources().getColor(R.color.colorPrimary));
//                        tvError.setVisibility(View.VISIBLE);
                    }
                    break;
                case REQUEST_IMAGE_CAMERA:
                    try {
                        File f = new File(Utility.INSTANCE.getImageFilePath());
                        file = new Compressor(AddressProofActivity.this).compressToFile(f, f.getName());
//                        tvError.setText(file.getName());
//                        tvError.setTextColor(getResources().getColor(R.color.colorPrimary));
//                        tvError.setVisibility(View.VISIBLE);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
    }

    private void uploadFile(Uri uri, File file) {
        dialog = new ProgressDialog(AddressProofActivity.this);
        dialog.setCancelable(false);
        dialog.setTitle("Please wait");
        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        dialog.setMax(100);
        dialog.setMessage("Uploading document...");
        if (dialog.isShowing()) {
            dialog.dismiss();
        }
        dialog.show();

        FileUploader fileUploader = new FileUploader();
        fileUploader.uploadFile(AddressProofActivity.this, uri, file, AppConstant.FILE, id);
        fileUploader.setOnUploadListener(new FileUploaderCallback() {
            @Override
            public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    if (dialog.isShowing()) {
                        dialog.dismiss();
                    }
                    try {
                        ResultMessage resultMessage = new Gson().fromJson(Objects.requireNonNull(response.body()).string(), new TypeToken<ResultMessage>() {
                        }.getType());
                        internetDialog = Utility.INSTANCE.showAlert(AddressProofActivity.this, resultMessage.getResponseMessage(), true, View.VISIBLE, R.string.dd_ok, v -> {
                            startActivity(new Intent(AddressProofActivity.this, DashBoardActivity.class));
                            finish();
                        }, View.GONE, null, null);
                        internetDialog.show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    if (dialog.isShowing()) {
                        dialog.dismiss();
                    }
                    try {
                        ResultError resultError = new Gson().fromJson(Objects.requireNonNull(response.errorBody()).string(), new TypeToken<ResultError>() {
                        }.getType());
                        internetDialog = Utility.INSTANCE.showAlert(AddressProofActivity.this, resultError.getMessage(), true, View.VISIBLE, R.string.dd_try, v -> Utility.INSTANCE.dismissDialog(internetDialog), View.GONE, null, null);
                        internetDialog.show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
                if (t.getMessage().equalsIgnoreCase("timeout")) {
                    internetDialog = Utility.INSTANCE.showError(AddressProofActivity.this, R.string.time_out_msg, R.string.dd_ok, v -> Utility.INSTANCE.dismissDialog(internetDialog));
                    internetDialog.show();
                }
            }

            @Override
            public void onProgressUpdate(int progress) {
                dialog.setProgress(progress);
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (from.equals(AppConstant.SIGNUP)) {
            Utility.INSTANCE.blankPreference(AppConstant.PREF_R_TOKEN);
            Utility.INSTANCE.blankPreference(AppConstant.PREF_A_TOKEN);
            Utility.INSTANCE.blankPreference(AppConstant.PREF_T_TYPE);
            Utility.INSTANCE.blankPreference(AppConstant.PREF_EXPIRE);
            Utility.INSTANCE.blankPreference(AppConstant.PREF_USER);
            Utility.INSTANCE.storeData(AppConstant.FILE_USER, "");
            finish();
            startActivity(new Intent(AddressProofActivity.this, WelcomeActivity.class));
        } else {
            super.onBackPressed();
        }
    }
}
