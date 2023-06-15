package net.braincake.bodytune.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;

import net.braincake.bodytune.R;
import net.braincake.bodytune.adapters.AdapterImage;
import net.braincake.bodytune.databinding.ActivityCreateNewPhoto2Binding;
import net.braincake.bodytune.model.MyImage;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class CreateNewPhoto1Activity extends AppCompatActivity {
    private ActivityCreateNewPhoto2Binding binding;
    private AdapterImage adapterImage;
    String mCurrentCameraPhotoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCreateNewPhoto2Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        requestStoragePermission(true);
        initView();
        initEvent();
    }



    private void initView() {
        ArrayList<MyImage> listTest = new ArrayList<>();
        adapterImage = new AdapterImage(this, listTest);
        GridLayoutManager layout = new GridLayoutManager(this, 4, LinearLayoutManager.VERTICAL, false);
        binding.rcvImage.setAdapter(adapterImage);
        binding.rcvImage.setLayoutManager(layout);
    }



    private void addTextTabLayout(List<String> data) {
        data.add(0, "All");
        for (int i = 0; i < data.size(); i++) {
            String item = data.get(i);
            binding.tableLayoutFolder.addTab(binding.tableLayoutFolder.newTab().setText(item));
            TextView textView = (TextView) LayoutInflater.from(this)
                    .inflate(R.layout.tab_title_layout, null);
            binding.tableLayoutFolder.getTabAt(i).setCustomView(textView);
        }
    }

    private void initEvent() {

        binding.imgArrowLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        adapterImage.setOnItemClickListener(new AdapterImage.OnItemClickListener() {
            @Override
            public void onItemClick(MyImage myImage) {
                binding.llSelectImage.setVisibility(View.GONE);
                binding.llSelectedImage.setVisibility(View.VISIBLE);
                mCurrentCameraPhotoPath = myImage.getUrlMyImage();
            }
        });

        adapterImage.setOnItemClickCameraListener(new AdapterImage.OnItemClickListener() {
            @Override
            public void onItemClick(MyImage language) {
                binding.llSelectImage.setVisibility(View.VISIBLE);
                binding.llSelectedImage.setVisibility(View.GONE);
                Toast.makeText(CreateNewPhoto1Activity.this, "Camera", Toast.LENGTH_SHORT).show();
            }
        });

        HashMap<String, ArrayList<String>> categorizedImages = getCategorizedImages();

        // lấy ra list danh mục
        List<String> folderNames = new ArrayList<>(categorizedImages.keySet());
        Collections.sort(folderNames, String.CASE_INSENSITIVE_ORDER);
        addTextTabLayout(folderNames);

        ArrayList<String> imagePaths = getAllImages();

        ArrayList<MyImage> myImage = convertStringToMyImage(imagePaths);
        adapterImage.updateData(myImage);

        binding.tableLayoutFolder.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                binding.llSelectImage.setVisibility(View.VISIBLE);
                binding.llSelectedImage.setVisibility(View.GONE);

                if (tab.getPosition() == 0) {
                    adapterImage.updateData(convertStringToMyImage(imagePaths));
                } else {
                    adapterImage.updateData(getMyImagesByFolderName(String.valueOf(tab.getText()), categorizedImages));
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        binding.textCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.llSelectImage.setVisibility(View.VISIBLE);
                binding.llSelectedImage.setVisibility(View.GONE);
                adapterImage.updateSelect();
            }
        });

        binding.textEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startWork(mCurrentCameraPhotoPath);
            }
        });

    }

    public void getDataPhoto(){
        ArrayList<String> imagePaths = getAllImages();
        HashMap<String, ArrayList<String>> categorizedImages = getCategorizedImages();

        if(imagePaths.size() != 0){
            // lấy ra list danh mục
            List<String> folderNames = new ArrayList<>(categorizedImages.keySet());
            Collections.sort(folderNames, String.CASE_INSENSITIVE_ORDER);
            addTextTabLayout(folderNames);

        }


        ArrayList<MyImage> myImage = convertStringToMyImage(imagePaths);
        adapterImage.updateData(myImage);
    }


    public ArrayList<String> getAllImages() {
        ArrayList<String> imagePaths = new ArrayList<>();
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, null, null, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                @SuppressLint("Range") String imagePath = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
                imagePaths.add(imagePath);
            }
            cursor.close();
        }
        return imagePaths;
    }

    public HashMap<String, ArrayList<String>> getCategorizedImages() {
        HashMap<String, ArrayList<String>> categorizedImages = new HashMap<>();
        String[] projection = {MediaStore.Images.Media.DATA, MediaStore.Images.Media.BUCKET_DISPLAY_NAME};
        Cursor cursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, null, null, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                @SuppressLint("Range") String imagePath = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
                @SuppressLint("Range") String bucketName = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.BUCKET_DISPLAY_NAME));

                if (categorizedImages.containsKey(bucketName)) {
                    ArrayList<String> imagePaths = categorizedImages.get(bucketName);
                    imagePaths.add(imagePath);
                } else {
                    ArrayList<String> imagePaths = new ArrayList<>();
                    imagePaths.add(imagePath);
                    categorizedImages.put(bucketName, imagePaths);
                }
            }
            cursor.close();
        }
        return categorizedImages;
    }

    private ArrayList<MyImage> getMyImagesByFolderName(String folderName, HashMap<String, ArrayList<String>> categorizedImages) {
        ArrayList<String> folderImages = categorizedImages.get(folderName);

        ArrayList<MyImage> myImages = new ArrayList<>();

        if (folderImages != null) {
            for (int i = 0; i < folderImages.size(); i++) {
                MyImage myImage = new MyImage();
                myImage.setIdMyImage(i + 1);
                myImage.setUrlMyImage(folderImages.get(i));
                myImage.setSelectMyImage(false);
                myImages.add(myImage);
            }
        }
        return myImages;
    }

    private ArrayList<MyImage> convertStringToMyImage(ArrayList<String> imagePaths) {
        ArrayList<MyImage> myImages = new ArrayList<>();

        for (int i = 0; i < imagePaths.size(); i++) {
            MyImage myImage = new MyImage();
            myImage.setIdMyImage(i + 1);
            myImage.setUrlMyImage(imagePaths.get(i));
            myImage.setSelectMyImage(false);
            myImages.add(myImage);
        }

        myImages.add(0, new MyImage(0, "", false));
        return myImages;
    }



    public void startWork(String str) {
        Intent intent = new Intent(this, EditPhotoActivity.class);
        intent.putExtra("path", str);
        startActivity(intent);
    }

    @Override
    public void onActivityResult(int i, int i2, Intent intent) {
        super.onActivityResult(i, i2, intent);
        if (i == 2) {
            requestStoragePermission(false);
        } else if (i2 == -1) {
//            this.mPhotoGrid.setOnItemClickListener(null);
//            this.mCamera.setOnClickListener(null);
//            this.mShould = true;
//            startWork(this.mCurrentCameraPhotoPath);
            Log.d("aaaaa","onActivityResult = -1");
        } else {
            new File(this.mCurrentCameraPhotoPath).delete();
        }
    }

    public void onRestoreInstanceState(Bundle bundle) {
        super.onRestoreInstanceState(bundle);
        this.mCurrentCameraPhotoPath = bundle.getString("mCurrentCameraPhotoPath");
    }


    @Override
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putString("mCurrentCameraPhotoPath", this.mCurrentCameraPhotoPath);
    }

    private File createImageFile() throws IOException {
        String format = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File createTempFile = File.createTempFile("BrainCake_" + format + "_", ".jpg", getExternalFilesDir(Environment.DIRECTORY_PICTURES));
        this.mCurrentCameraPhotoPath = createTempFile.getAbsolutePath();
        return createTempFile;
    }


    public void requestStoragePermission(boolean z) {
        if (ContextCompat.checkSelfPermission(this, "android.permission.WRITE_EXTERNAL_STORAGE") == 0) {
//            getAllImage();
//            showGallery();
            Log.d("aaaaa","requestStoragePermission: ok");

        } else if (z) {
            ActivityCompat.requestPermissions(this, new String[]{"android.permission.WRITE_EXTERNAL_STORAGE"}, 12);
        } else {
            finish();
        }
    }

    @Override
    public void onRequestPermissionsResult(int i, String[] strArr, int[] iArr) {
        super.onRequestPermissionsResult(i, strArr, iArr);
        if (i != 12) {
            return;
        }
        if (iArr.length <= 0) {
            finish();
        } else if (iArr[0] == 0) {
//            getAllImage();
//            showGallery();

        } else if (!ActivityCompat.shouldShowRequestPermissionRationale(this, "android.permission.WRITE_EXTERNAL_STORAGE")) {
            showNoStoragePermissionSnackbar();
        } else {
            finish();
        }
    }

    public void showNoStoragePermissionSnackbar() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("You'll need to allow Permissions/Storage access to upload a photo");
        builder.setPositiveButton("Settings", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialogInterface, int i) {
                CreateNewPhoto1Activity.this.openApplicationSettings();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialogInterface, int i) {
                CreateNewPhoto1Activity.this.finish();
            }
        });
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {

            public void onCancel(DialogInterface dialogInterface) {
                CreateNewPhoto1Activity.this.finish();
            }
        });
        AlertDialog create = builder.create();
        create.show();
        create.getButton(-2).setTextColor(-7829368);
        create.getButton(-1).setTextColor(-16711936);
    }

    public void openApplicationSettings() {
        startActivityForResult(new Intent("android.settings.APPLICATION_DETAILS_SETTINGS", Uri.parse("package:" + getPackageName())), 2);
    }


}