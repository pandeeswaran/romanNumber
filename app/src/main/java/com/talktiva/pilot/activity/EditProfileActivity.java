package com.talktiva.pilot.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;
import com.talktiva.pilot.R;
import com.talktiva.pilot.helper.AppConstant;
import com.talktiva.pilot.helper.CustomTypefaceSpan;
import com.talktiva.pilot.helper.ImageFilePath;
import com.talktiva.pilot.helper.Utility;
import com.talktiva.pilot.model.User;
import com.talktiva.pilot.request.RequestProfile;
import com.talktiva.pilot.rest.ApiClient;
import com.talktiva.pilot.rest.ApiInterface;
import com.talktiva.pilot.rest.FileUploader;
import com.talktiva.pilot.rest.FileUploaderCallback;
import com.talktiva.pilot.results.ResultError;
import com.whiteelephant.monthpicker.MonthPickerDialog;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnTextChanged;
import id.zelory.compressor.Compressor;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditProfileActivity extends AppCompatActivity {

    @BindView(R.id.epa_toolbar)
    Toolbar toolbar;

    @BindView(R.id.epa_et_full_name)
    EditText etFullName;

    @BindView(R.id.epa_tv_full_name)
    TextView tvFullName;

    @BindView(R.id.epa_et_email)
    EditText etEmail;

    @BindView(R.id.epa_et_phone)
    EditText etPhone;

    @BindView(R.id.epa_et_dob)
    EditText etDob;

    @BindView(R.id.epa_tv_dob)
    TextView tvDob;

    @BindView(R.id.epa_et_rs)
    EditText etResidentSince;

    @BindView(R.id.epa_tv_rs)
    TextView tvResidentSince;

    @BindView(R.id.epa_sw_dob)
    Switch swDob;

    @BindView(R.id.epa_iv_photo)
    ImageView ivPhoto;

    @BindView(R.id.epa_tv_photo)
    TextView tvPhoto;

    @SuppressLint("SimpleDateFormat")
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy");

    private Dialog progressDialog, internetDialog, dialogPermission;
    private ProgressDialog dialog;
    private User user;

    private static final int PERMISSION_REQUEST_CODE = 123;

    private static final int REQUEST_IMAGE_GALLERY = 1;
    private static final int REQUEST_IMAGE_CAMERA = 2;

    private final String[] appPermissions = {
            Manifest.permission.CAMERA};

    private final CharSequence[] items = {"Camera", "Gallery"};
    private File file;
    private Uri uri;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationIcon(R.drawable.ic_back_white);

        if (Build.VERSION.SDK_INT >= 24) {
            try {
                Method m = StrictMode.class.getMethod("disableDeathOnFileUriExposure");
                m.invoke(null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        checkAndRequestPermission();

        Utility.INSTANCE.setTitleText(toolbar, R.id.epa_toolbar_tv_title, R.string.epa_title);

        progressDialog = Utility.INSTANCE.showProgress(EditProfileActivity.this);

        etFullName.setTypeface(Utility.INSTANCE.getFontRegular());
        tvFullName.setTypeface(Utility.INSTANCE.getFontRegular());
        etEmail.setTypeface(Utility.INSTANCE.getFontRegular());
        etPhone.setTypeface(Utility.INSTANCE.getFontRegular());
        etDob.setTypeface(Utility.INSTANCE.getFontRegular());
        tvDob.setTypeface(Utility.INSTANCE.getFontRegular());
        etResidentSince.setTypeface(Utility.INSTANCE.getFontRegular());
        tvResidentSince.setTypeface(Utility.INSTANCE.getFontRegular());
        swDob.setTypeface(Utility.INSTANCE.getFontRegular());
        tvPhoto.setTypeface(Utility.INSTANCE.getFontRegular());

        Bundle bundle = getIntent().getExtras();
        user = (User) Objects.requireNonNull(bundle).getSerializable(AppConstant.USER);
        etFullName.setText(Objects.requireNonNull(user).getFullName());
        etEmail.setText(user.getEmail());

        if (user.getPhone() != null) {
            etPhone.setText(user.getPhone());
        }

        if (user.getBirthday() != null) {
            etDob.setText(user.getBirthday());
        }

        if (user.getResidentSince() != null) {
            etResidentSince.setText(user.getResidentSince());
        }

        if (user.getShowMyBirthday() != null) {
            swDob.setChecked(user.getShowMyBirthday());
        }

        if (!user.getCanChangeName()){
            etFullName.setFocusable(false);
        }

        RequestCreator rc = Picasso.get().load(user.getUserImage());
        rc.memoryPolicy(MemoryPolicy.NO_STORE).placeholder(R.drawable.user).into(ivPhoto);
//        , MemoryPolicy.NO_CACHE

        etDob.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                Calendar calendar = Calendar.getInstance();
                Date date;
                if (etDob.getText().toString().trim().length() != 0) {
                    try {
                        date = simpleDateFormat.parse(etDob.getText().toString().trim());
                        calendar.setTimeInMillis(date.getTime());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }

                DatePickerDialog datePickerDialog = new DatePickerDialog(EditProfileActivity.this, (view, year, month, dayOfMonth) -> {
                    Calendar newDate = Calendar.getInstance();
                    newDate.set(year, month, dayOfMonth);
                    etDob.setText(simpleDateFormat.format(newDate.getTime()));
                    tvDob.setText(null);
                    tvDob.setVisibility(View.GONE);
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.show();
                return true;
            } else {
                return false;
            }
        });

        etResidentSince.setOnTouchListener((v, event) -> {
            Calendar calendar = Calendar.getInstance();
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                MonthPickerDialog.Builder builder = new MonthPickerDialog.Builder(EditProfileActivity.this, (selectedMonth, selectedYear) -> {
                    etResidentSince.setText(String.valueOf(selectedYear));
                    tvResidentSince.setText(null);
                    tvResidentSince.setVisibility(View.GONE);
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH));
                builder.setActivatedYear(calendar.get(Calendar.YEAR));
                builder.setMinYear(calendar.get(Calendar.YEAR) - 50);
                builder.setMaxYear(calendar.get(Calendar.YEAR));
                builder.showYearOnly();
                builder.build().show();
                return true;
            } else {
                return false;
            }
        });

        ivPhoto.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setItems(items, (dialog, item) -> {
                dialog.dismiss();
                switch (item) {
                    case 0:
                        Intent pictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        if (pictureIntent.resolveActivity(getPackageManager()) != null) {
                            File photoFile = Utility.INSTANCE.createImageFile();
                            if (photoFile != null) {
                                uri = FileProvider.getUriForFile(this, "com.talktiva.pilot.provider", photoFile);
                                pictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                                startActivityForResult(pictureIntent, REQUEST_IMAGE_CAMERA);
                            }
                        }
                        break;
                    case 1:
                        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                        intent.addCategory(Intent.CATEGORY_OPENABLE);
                        intent.setType("image/*");
                        startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQUEST_IMAGE_GALLERY);
                        break;
                }
            });
            builder.show();
        });
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
                    dialogPermission = Utility.INSTANCE.showAlert(EditProfileActivity.this, R.string.dd_permission_msg, false, View.VISIBLE, R.string.dd_yes, v -> {
                        dialogPermission.dismiss();
                        for (int i = 0; deniedPermissions.size() > i; i++) {
                            if (ActivityCompat.shouldShowRequestPermissionRationale(EditProfileActivity.this, deniedPermissions.get(i))) {
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
                        String path = ImageFilePath.INSTANCE.getPath(EditProfileActivity.this, uri);
                        file = new File(path);
                        uploadAvatar();
                    }
                    break;
                case REQUEST_IMAGE_CAMERA:
                    try {
                        File f = new File(Utility.INSTANCE.getImageFilePath());
                        file = new Compressor(EditProfileActivity.this).compressToFile(f, f.getName());
                        uploadAvatar();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
    }

    @OnTextChanged(R.id.epa_et_full_name)
    void setEtFullNameOnTextChange(CharSequence sequence) {
        String s = sequence.toString().trim();
        if (s.length() == 0) {
            tvFullName.setText(null);
            tvFullName.setVisibility(View.GONE);
        } else if (!isChar(s)) {
            tvFullName.setText(R.string.epa_tv_fn_val);
            tvFullName.setVisibility(View.VISIBLE);
        } else {
            tvFullName.setText(null);
            tvFullName.setVisibility(View.GONE);
        }
    }

    @OnTextChanged(R.id.epa_et_rs)
    void setEtResidentSinceOnTextChange(CharSequence sequence) {
        String s = sequence.toString().trim();
        if (s.length() > 0) {
            tvResidentSince.setText(null);
            tvResidentSince.setVisibility(View.GONE);
        }
    }

    private boolean isChar(String string) {
        return Pattern.compile("^[a-zA-Z ]+$").matcher(string).matches();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_profile_menu, menu);

        MenuItem menuItem = menu.findItem(R.id.epa_menu_save);
        SpannableString mNewTitle = new SpannableString(menuItem.getTitle());
        mNewTitle.setSpan(new CustomTypefaceSpan("", Utility.INSTANCE.getFontRegular()), 0, mNewTitle.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        menuItem.setTitle(mNewTitle);

        return super.onCreateOptionsMenu(menu);
    }

    @SuppressLint("NewApi")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;

            case R.id.epa_menu_save:
                if (etFullName.getText().toString().trim().length() == 0) {
                    tvFullName.setVisibility(View.VISIBLE);
                    tvFullName.setText(R.string.epa_tv_fn_error);
//                } else if (etDob.getText().toString().trim().length() == 0) {
//                    tvDob.setVisibility(View.VISIBLE);
//                    tvDob.setText(R.string.epa_tv_dob_error);
                } else if (etResidentSince.getText().toString().trim().length() == 0) {
                    tvResidentSince.setVisibility(View.VISIBLE);
                    tvResidentSince.setText(R.string.epa_tv_rs_error);
                } else {
                    updateProfile();
                }
                return true;

            default:
                return false;
        }
    }

    @Override
    public void onBackPressed() {
        if (etDob.getText().toString().trim().length() != 0 && etResidentSince.getText().toString().trim().length() != 0) {
            if (!etFullName.getText().toString().trim().equalsIgnoreCase(user.getFullName())) {
                internetDialog = Utility.INSTANCE.showAlert(EditProfileActivity.this, R.string.dd_discard, false, View.VISIBLE, R.string.dd_yes, v -> super.onBackPressed(), View.VISIBLE, R.string.dd_no, v -> Utility.INSTANCE.dismissDialog(internetDialog));
                internetDialog.show();
//            } else if (!etDob.getText().toString().trim().equalsIgnoreCase(user.getBirthday())) {
//                internetDialog = Utility.INSTANCE.showAlert(EditProfileActivity.this, R.string.dd_discard, false, View.VISIBLE, R.string.dd_yes, v -> super.onBackPressed(), View.VISIBLE, R.string.dd_no, v -> Utility.INSTANCE.dismissDialog(internetDialog));
//                internetDialog.show();
            } else if (!etResidentSince.getText().toString().trim().equalsIgnoreCase(user.getResidentSince())) {
                internetDialog = Utility.INSTANCE.showAlert(EditProfileActivity.this, R.string.dd_discard, false, View.VISIBLE, R.string.dd_yes, v -> super.onBackPressed(), View.VISIBLE, R.string.dd_no, v -> Utility.INSTANCE.dismissDialog(internetDialog));
                internetDialog.show();
            } else if (swDob.isChecked() != user.getShowMyBirthday()) {
                internetDialog = Utility.INSTANCE.showAlert(EditProfileActivity.this, R.string.dd_discard, false, View.VISIBLE, R.string.dd_yes, v -> super.onBackPressed(), View.VISIBLE, R.string.dd_no, v -> Utility.INSTANCE.dismissDialog(internetDialog));
                internetDialog.show();
            } else {
                super.onBackPressed();
            }
        } else {
            super.onBackPressed();
        }
    }

    private void updateProfile() {
        progressDialog.show();

        RequestProfile requestProfile = new RequestProfile();
        requestProfile.setFullName(etFullName.getText().toString().trim());
        requestProfile.setEmail(etEmail.getText().toString().trim());
        if (etPhone.getText().toString().trim().length() != 0) {
            requestProfile.setPhone(etPhone.getText().toString());
        } else {
            requestProfile.setPhone(null);
        }
        requestProfile.setBirthday(etDob.getText().toString());
        requestProfile.setResidentSince(etResidentSince.getText().toString());
        requestProfile.setShowMyBirthday(swDob.isChecked());

        ApiInterface apiInterface = ApiClient.INSTANCE.getClient().create(ApiInterface.class);
        Call<User> call = apiInterface.updateProfile(AppConstant.CT_JSON, Utility.INSTANCE.getPreference(AppConstant.PREF_T_TYPE).concat(" ").concat(Utility.INSTANCE.getPreference(AppConstant.PREF_A_TOKEN)), AppConstant.UTF, requestProfile);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                if (response.isSuccessful()) {
                    Utility.INSTANCE.dismissDialog(progressDialog);
                    Utility.INSTANCE.storeData(AppConstant.FILE_USER, new Gson().toJson(response.body(), User.class));
                    Intent intent = new Intent("updateProfile");
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(AppConstant.USER, response.body());
                    intent.putExtras(bundle);
                    LocalBroadcastManager.getInstance(EditProfileActivity.this).sendBroadcast(intent);
                    finish();
                } else {
                    Utility.INSTANCE.dismissDialog(progressDialog);
                }
            }

            @Override
            public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                Utility.INSTANCE.dismissDialog(progressDialog);
            }
        });
    }

    private void uploadAvatar() {
        dialog = new ProgressDialog(EditProfileActivity.this);
        dialog.setCancelable(false);
        dialog.setTitle("Please wait");
        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        dialog.setMax(100);
        dialog.setMessage("Uploading image...");
        if (dialog.isShowing()) {
            dialog.dismiss();
        }
        dialog.show();

        FileUploader fileUploader = new FileUploader();
        fileUploader.uploadAvatar(EditProfileActivity.this, uri, file, AppConstant.FILE);
        fileUploader.setOnUploadListener(new FileUploaderCallback() {
            @Override
            public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    if (dialog.isShowing()) {
                        dialog.dismiss();
                    }
                    try {
                        User user = new Gson().fromJson(Objects.requireNonNull(response.body()).string(), new TypeToken<User>() {
                        }.getType());
                        Utility.INSTANCE.storeData(AppConstant.FILE_USER, new Gson().toJson(user, User.class));
                        RequestCreator rc = Picasso.get().load(user.getUserImage());
                        rc.memoryPolicy(MemoryPolicy.NO_STORE).placeholder(R.drawable.user).into(ivPhoto);
//                        , MemoryPolicy.NO_CACHE

                        Intent intent1 = new Intent("updateProfile");
                        Bundle bundle1 = new Bundle();
                        bundle1.putSerializable(AppConstant.USER, user);
                        intent1.putExtras(bundle1);
                        LocalBroadcastManager.getInstance(EditProfileActivity.this).sendBroadcast(intent1);

                        Intent intent2 = new Intent("updateProfileHome");
                        Bundle bundle2 = new Bundle();
                        bundle2.putSerializable(AppConstant.USER, user);
                        intent2.putExtras(bundle2);
                        LocalBroadcastManager.getInstance(EditProfileActivity.this).sendBroadcast(intent2);
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
                        internetDialog = Utility.INSTANCE.showAlert(EditProfileActivity.this, resultError.getMessage(), true, View.VISIBLE, R.string.dd_try, v -> Utility.INSTANCE.dismissDialog(internetDialog), View.GONE, null, null);
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
            }

            @Override
            public void onProgressUpdate(int progress) {
                dialog.setProgress(progress);
            }
        });

    }
}