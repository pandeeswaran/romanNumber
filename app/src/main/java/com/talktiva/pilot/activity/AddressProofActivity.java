package com.talktiva.pilot.activity;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
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

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.talktiva.pilot.R;
import com.talktiva.pilot.helper.AppConstant;
import com.talktiva.pilot.helper.Utility;
import com.talktiva.pilot.rest.ApiClient;
import com.talktiva.pilot.rest.ApiInterface;
import com.talktiva.pilot.results.ResultError;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddressProofActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 123;
    private static final int REQUEST_IMAGE_GALLERY = 1;
    private static final int REQUEST_IMAGE_CAMERA = 2;
    private final String[] appPermissions = {
            Manifest.permission.CAMERA};
    @BindView(R.id.apa_iv_info)
    ImageView ivInfo;
    @BindView(R.id.apa_iv_back)
    ImageView ivBack;
    @BindView(R.id.apa_tv)
    TextView textView;
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
    private Dialog progressDialog, internetDialog, dialogPermission;
    private Integer id;
    private File file;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_address_proof);
        ButterKnife.bind(this);

        String from = getIntent().getStringExtra(AppConstant.FROM);
        id = getIntent().getIntExtra(AppConstant.ID, 0);

        progressDialog = Utility.INSTANCE.showProgress(AddressProofActivity.this);

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
            ivBack.setVisibility(View.GONE);
            tvOr.setVisibility(View.VISIBLE);
            tvSkip.setVisibility(View.VISIBLE);
        } else {
            ivBack.setVisibility(View.VISIBLE);
            tvOr.setVisibility(View.GONE);
            tvSkip.setVisibility(View.GONE);
        }

        ivBack.setOnClickListener(v -> onBackPressed());

        tvOr.setOnClickListener(v -> {
            startActivity(new Intent(AddressProofActivity.this, DashBoardActivity.class));
            finish();
        });

        ivFolder.setOnClickListener(v -> startActivityForResult(Utility.INSTANCE.getPickImageChooserForGallery(AddressProofActivity.this), REQUEST_IMAGE_GALLERY));

        ivCamera.setOnClickListener(v -> startActivityForResult(Utility.INSTANCE.getPickImageChooserForCamera(AddressProofActivity.this), REQUEST_IMAGE_CAMERA));

        ivHelp.setOnClickListener(v -> {
            internetDialog = Utility.INSTANCE.showAlert(AddressProofActivity.this, R.color.colorPrimary, R.string.dd_info_img, true, View.GONE, null, null, View.GONE, null, null);
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
                uploadFile(file);
            }
        });

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
            Uri picUri;
            switch (requestCode) {
                case REQUEST_IMAGE_GALLERY:
                    if (data != null) {
                        picUri = data.getData();
                        File f = new File(Objects.requireNonNull(picUri).getPath());
                        file = f.getAbsoluteFile();
                        tvError.setText(file.getName());
                        tvError.setTextColor(getResources().getColor(R.color.colorPrimary));
                        tvError.setVisibility(View.VISIBLE);
                    }
                    break;
                case REQUEST_IMAGE_CAMERA:
                    picUri = Utility.INSTANCE.getCaptureImageOutputUri();
                    file = new File(Objects.requireNonNull(picUri).getPath());
                    tvError.setText(file.getName());
                    tvError.setTextColor(getResources().getColor(R.color.colorPrimary));
                    tvError.setVisibility(View.VISIBLE);
                    break;
            }
        }
    }

    private void uploadFile(File file) {
        progressDialog.show();

        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), requestFile);
        String descriptionString = "Sample description";
        RequestBody description = RequestBody.create(MediaType.parse("multipart/form-data"), descriptionString);

        ApiInterface apiInterface = ApiClient.INSTANCE.getClient().create(ApiInterface.class);
        Call<ResponseBody> call = apiInterface.uploadImage(id, body, description);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Utility.INSTANCE.dismissDialog(progressDialog);
                    startActivity(new Intent(AddressProofActivity.this, DashBoardActivity.class));
                    finish();
                } else {
                    Utility.INSTANCE.dismissDialog(progressDialog);
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
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                Utility.INSTANCE.dismissDialog(progressDialog);
                if (t.getMessage().equalsIgnoreCase("timeout")) {
                    internetDialog = Utility.INSTANCE.showError(AddressProofActivity.this, R.string.time_out_msg, R.string.dd_ok, v -> Utility.INSTANCE.dismissDialog(internetDialog));
                    internetDialog.show();
                }
            }
        });
    }
}
