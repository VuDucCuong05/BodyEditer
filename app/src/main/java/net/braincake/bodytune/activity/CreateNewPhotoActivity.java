package net.braincake.bodytune.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import net.braincake.bodytune.R;

public class CreateNewPhotoActivity extends AppCompatActivity implements View.OnClickListener {
    FrameLayout mAdContainer;
    FrameLayout mCamera;
    FrameLayout mClose;
    String mCurrentCameraPhotoPath;
    GridAdapter mGridAdapter;
    AdapterView.OnItemClickListener mGridClickListener = new AdapterView.OnItemClickListener() {


        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
            CreateNewPhotoActivity.this.mPhotoGrid.setOnItemClickListener(null);
            CreateNewPhotoActivity.this.mCamera.setOnClickListener(null);
            CreateNewPhotoActivity.this.mShould = true;
            CreateNewPhotoActivity createNewPhotoActivity = CreateNewPhotoActivity.this;
            createNewPhotoActivity.startWork(createNewPhotoActivity.mImageList.get(i).getAbsolutePath());
        }
    };
    ArrayList<File> mImageList;
    GridView mPhotoGrid;
    boolean mShould;
    private final String[] projection = {"_data"};


    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_create_new_photo);
        this.mPhotoGrid = (GridView) findViewById(R.id.photoGrid);
        this.mCamera = (FrameLayout) findViewById(R.id.camera);
        this.mClose = (FrameLayout) findViewById(R.id.close);
        this.mClose.setOnClickListener(this);
        this.mImageList = new ArrayList<>();
        requestStoragePermission(true);
    }

    public void showGallery() {
        if (this.mImageList.size() == 0) {
            findViewById(R.id.noImagesFound).setVisibility(0);
            return;
        }
        GridAdapter gridAdapter = new GridAdapter();
        this.mGridAdapter = gridAdapter;
        this.mPhotoGrid.setAdapter((ListAdapter) gridAdapter);
        this.mPhotoGrid.setOnItemClickListener(this.mGridClickListener);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (this.mShould) {
            this.mPhotoGrid.setOnItemClickListener(this.mGridClickListener);
            this.mCamera.setOnClickListener(this);
            this.mShould = false;
        }
//        if (this.mAdContainer.getChildCount() != 0 && this.mAdContainer.getChildAt(0).getVisibility() == 0) {
//
//        } else if (NetworkUtil.getConnectivityStatusString(this)) {
//
//        }
    }

    @Override
    public void onPause() {
        super.onPause();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (this.mAdContainer.getChildCount() != 0) {

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
            getAllImage();
            showGallery();
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
            getAllImage();
            showGallery();
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
                CreateNewPhotoActivity.this.openApplicationSettings();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialogInterface, int i) {
                CreateNewPhotoActivity.this.finish();
            }
        });
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {

            public void onCancel(DialogInterface dialogInterface) {
                CreateNewPhotoActivity.this.finish();
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


    public void getAllImage() {
        this.mCamera.setOnClickListener(this);
        Cursor query = getApplicationContext().getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, this.projection, null, null, "date_added");
        if (query != null && query.moveToLast()) {
            do {
                @SuppressLint("Range") File makeSafeFile = makeSafeFile(query.getString(query.getColumnIndex(this.projection[0])));
                if (makeSafeFile != null && makeSafeFile.exists()) {
                    this.mImageList.add(makeSafeFile);
                }
            } while (query.moveToPrevious());
            query.close();
        }
    }

    private static File makeSafeFile(String str) {
        if (str != null && !str.isEmpty()) {
            try {
                return new File(str);
            } catch (Exception unused) {
            }
        }
        return null;
    }

    public void startWork(String str) {
        Intent intent = new Intent(this, EditPhotoActivity.class);
        intent.putExtra("path", str);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        if (!this.mShould) {
            super.onBackPressed();
        }
    }

    public void onClick(View view) {
        if (view.getId() == R.id.camera) {
            Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
            if (!(intent.resolveActivity(getPackageManager()) != null)) {
                Context applicationContext = getApplicationContext();
                Toast.makeText(applicationContext, applicationContext.getString(R.string.error_no_camera), 1).show();
                return;
            }
            try {
                File createImageFile = createImageFile();
                if (createImageFile != null) {
                    intent.putExtra("output", FileProvider.getUriForFile(this, ".", createImageFile));
                    if (intent.resolveActivity(getPackageManager()) != null) {
                        startActivityForResult(intent, 1);
                    }
                }
            } catch (IOException unused) {
            }
        } else {
            finish();
        }
    }

    @Override
    public void onActivityResult(int i, int i2, Intent intent) {
        super.onActivityResult(i, i2, intent);
        if (i == 2) {
            requestStoragePermission(false);
        } else if (i2 == -1) {
            this.mPhotoGrid.setOnItemClickListener(null);
            this.mCamera.setOnClickListener(null);
            this.mShould = true;
            startWork(this.mCurrentCameraPhotoPath);
        } else {
            new File(this.mCurrentCameraPhotoPath).delete();
        }
    }

    public class GridAdapter extends BaseAdapter {
        ViewHolder holder;

        public long getItemId(int i) {
            return (long) i;
        }

        GridAdapter() {
        }

        public int getCount() {
            return CreateNewPhotoActivity.this.mImageList.size();
        }

        public Object getItem(int i) {
            return CreateNewPhotoActivity.this.mImageList.get(i);
        }

        public View getView(int i, View view, ViewGroup viewGroup) {
            if (view == null) {
                view = CreateNewPhotoActivity.this.getLayoutInflater().inflate(R.layout.item_gallery_grid, viewGroup, false);
                ViewHolder viewHolder = new ViewHolder();
                this.holder = viewHolder;
                viewHolder.gridImage = (ImageView) view.findViewById(R.id.image);
                view.setTag(this.holder);
            } else {
                this.holder = (ViewHolder) view.getTag();
            }
            int columnWidth = CreateNewPhotoActivity.this.mPhotoGrid.getColumnWidth() > 0 ? CreateNewPhotoActivity.this.mPhotoGrid.getColumnWidth() : Integer.MIN_VALUE;
            ((RequestBuilder) ((RequestBuilder) ((RequestBuilder) Glide.with(CreateNewPhotoActivity.this.getBaseContext()).load(getItem(i)).override(columnWidth, columnWidth)).centerCrop()).placeholder(R.drawable.gallery_placeholder)).into(this.holder.gridImage);
            return view;
        }

        class ViewHolder {
            ImageView gridImage;

            ViewHolder() {
            }
        }
    }
}
