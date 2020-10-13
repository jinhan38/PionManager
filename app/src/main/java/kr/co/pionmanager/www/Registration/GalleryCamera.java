package kr.co.pionmanager.www.Registration;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import kr.co.pionmanager.www.BuildConfig;
import kr.co.pionmanager.www.FileUploadUtils;
import kr.co.pionmanager.www.R;
import kr.co.pionmanager.www.Util;

public class GalleryCamera extends AppCompatActivity implements View.OnClickListener {

    private ImageButton btn_gallery;
    private ImageButton btn_camera;
    private ImageButton btn_delete;
    private ImageView imageView;
    private LinearLayout ll_camera_gallery_wrap;
    private FrameLayout fl_rotate_wrap;
    private Uri photoUri;
    private Button btn_identification_moveToBank;
    public ProgressBar progressBar;
    private static final int PICK_FROM_CAMERA = 1;
    private static final int PICK_FROM_ALBUM = 2;
    private static final int CROP_FROM_CAMERA = 3;

    private String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA};

    private String mCurrentPhotoPath;

    private File tempFile;
    private static final String TAG = "identification";

    private final int PERMISSIONS_REQUEST_CODE = 1;
    public static GalleryCamera galleryCamera;
    private ImageButton rotate_left;
    private ImageButton rotate_right;
    int degree = 0;

    private Bitmap bitmap = null;
    private Bitmap rotateBitmap = null;

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_identification_camera);
        galleryCamera = this;
        requestPermission();
        Util.SaveCurrentContext(this);

        progressBar = findViewById(R.id.progressBar);
        btn_identification_moveToBank = findViewById(R.id.btn_identification_moveToBank);
        imageView = findViewById(R.id.iv_camera_identification);
        rotate_left = findViewById(R.id.rotate_left);
        rotate_right = findViewById(R.id.rotate_right);
        btn_delete = findViewById(R.id.btn_delete);
        btn_camera = findViewById(R.id.btn_camera);
        btn_gallery = findViewById(R.id.btn_gallery);
        ll_camera_gallery_wrap = findViewById(R.id.ll_camera_gallery_wrap);
        fl_rotate_wrap = findViewById(R.id.fl_rotate_wrap);

        progressBar.setVisibility(View.GONE);
        btn_identification_moveToBank.setVisibility(View.VISIBLE);

        setupListener();
    }

    private void setupListener() {
        btn_camera.setOnClickListener(this);
        btn_gallery.setOnClickListener(this);
        rotate_left.setOnClickListener(this);
        rotate_right.setOnClickListener(this);
        btn_delete.setOnClickListener(this);

        btn_identification_moveToBank.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_camera:
                Log.e(TAG, "onClick: 카메라 버튼");
                takePhoto();
                break;
            case R.id.btn_gallery:
                Log.e(TAG, "onClick: 갤러리 버튼");
                goToAlbum();
                break;
            case R.id.rotate_left:
                degree -= 90;
                imageView.setImageBitmap(rotateBitmap(bitmap, degree));
                break;
            case R.id.rotate_right:
                degree += 90;
                imageView.setImageBitmap(rotateBitmap(bitmap, degree));
                break;
            case R.id.btn_delete:
                initImage();
                ll_camera_gallery_wrap.setVisibility(View.VISIBLE);
                fl_rotate_wrap.setVisibility(View.GONE);
                break;
            case R.id.btn_identification_moveToBank:
                progressBar.setVisibility(View.VISIBLE);
                btn_identification_moveToBank.setVisibility(View.GONE);
                Bitmap bitmap = null;
                BitmapDrawable drawable = new BitmapDrawable(rotateBitmap);
                Log.e(TAG, "onSingleClick: rotateBitmap" + rotateBitmap);
                if (drawable != null) {
                    bitmap = drawable.getBitmap();
                }
                if (bitmap != null) {
                    File newTemp = null;
                    try {
                        newTemp = createImageFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    File finalFile = SaveBitmapToFileCache(bitmap, String.valueOf(newTemp));
                    FileUploadUtils.send2Server(finalFile);
                } else {
                    Log.e("bitmap", "bitmap null");
                    Toast.makeText(getBaseContext(), "신분증을 등록해 주세요", Toast.LENGTH_LONG).show();
                    progressBar.setVisibility(View.GONE);
                    btn_identification_moveToBank.setVisibility(View.VISIBLE);
                }
                break;
        }
    }

    public void initImage() {
        imageView.setImageBitmap(null);
        tempFile = null;
        rotateBitmap = null;
    }

    public byte[] bitmapToByteArray(Bitmap $bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        $bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        return byteArray;
    }

    private File SaveBitmapToFileCache(Bitmap bitmap, String strFilePath) {
        File fileCacheItem = new File(strFilePath);
        OutputStream out = null;
        try {
            fileCacheItem.createNewFile();
            out = new FileOutputStream(fileCacheItem);

            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return fileCacheItem;
    }


    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_OK) {
//            Toast.makeText(this, "취소 되었습니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (requestCode == PICK_FROM_ALBUM) {
            if (data == null) {
                return;
            }
            photoUri = data.getData();
            cropImage();
        } else if (requestCode == PICK_FROM_CAMERA) {
            Log.e(TAG, "onActivityResult: PICK_FROM_CAMERA 인텐트 받음 ");
            cropImage();
            MediaScannerConnection.scanFile(GalleryCamera.this,
                    new String[]{photoUri.getPath()}, null,
                    new MediaScannerConnection.OnScanCompletedListener() {
                        public void onScanCompleted(String path, Uri uri) {
                        }
                    });
        } else if (requestCode == CROP_FROM_CAMERA) {
            imageView.setImageURI(null);
            setImage(tempFile);
        }
    }

    private void fileRootSetting(Uri uri) {
        Cursor cursor = null;
        try {
            String[] proj = {MediaStore.Images.Media.DATA};
            assert uri != null;
            cursor = getContentResolver().query(uri, proj, null, null, null);
            //getContentResolver는 데이터 접근을 위한 함수, query의 첫번째 매개변수가 content://scheme 방식의 원하는 데이터를 가져오기 위해 정해진 주소다.
            // 두번재 인수인 projection은 가져올 컬럼 이름 목록이다. null이면 모든 컬럼을 선택
            assert cursor != null;
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst(); //Cursor를 첫번째 행으로 이동
            //경로
            tempFile = new File(cursor.getString(column_index));
            Log.e(TAG, "tempFile Uri : " + Uri.fromFile(tempFile));
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public Uri getUriFromPath(String path) {
        String fileName = path;
        Uri fileUri = Uri.parse(fileName);
        String filePath = fileUri.getPath();
        Cursor c = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                null, "_data = '" + filePath + "'", null, null);
        c.moveToNext();
        int id = c.getInt(c.getColumnIndex("_id"));
        Uri uri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);

        return uri;
    }


    @SuppressLint("IntentReset")
    private void goToAlbum() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(intent, PICK_FROM_ALBUM);
    }

    @Override
    public void onBackPressed() {
        if (imageView.getDrawable() != null) {
            imageView.setImageDrawable(null);
            ll_camera_gallery_wrap.setVisibility(View.VISIBLE);
            fl_rotate_wrap.setVisibility(View.GONE);
        } else {
            super.onBackPressed();
        }
    }


    private void takePhoto() {
        Log.e(TAG, "takePhoto: 진입" );
        Log.e(TAG, "takePhoto: tempFile : " + tempFile );
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (tempFile != null) {
            Log.e(TAG, "takePhoto: tempFile not null" );
            /**
             *  안드로이드 OS 누가 버전 이후부터는 file:// URI 의 노출을 금지로 FileUriExposedException 발생
             *  Uri 를 FileProvider 도 감싸 주어야 합니다.
             *  참고 자료 http://programmar.tistory.com/4 , http://programmar.tistory.com/5
             */
            photoUri = FileProvider.getUriForFile(this,
                    "kr.co.pionmanager.www.provider", tempFile);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
            startActivityForResult(intent, PICK_FROM_CAMERA);
        } else{
            try {
                tempFile = createImageFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            takePhoto();
        }
    }

    /**
     * 이미지파일 이름, 파일 경로, 빈파일 생성
     */
    private File createImageFile() throws IOException {

        // 이미지 파일 이름
        String timeStamp = new SimpleDateFormat("HHmmss").format(new Date());
        String imageFileName = "PionManager" + timeStamp + "_";

        // 이미지가 저장될 파일 경로
        File storageDir = new File(Environment.getExternalStorageDirectory() + "/PionManager/");
        if (!storageDir.exists()) storageDir.mkdirs();

        // 빈 파일 생성
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
        Log.e(TAG, "createImageFile : " + image.getAbsolutePath());

        return image;
    }

    /**
     * tempFile 을 bitmap 으로 변환
     * 이미지 안에 설정된 회전 값을 구해서 회전 적용한 후 ImageView 에 설정한다.
     */
    @RequiresApi(api = Build.VERSION_CODES.Q)
    private void setImage(File file) {
//        File file = new File(String.valueOf(tempFile));
        try {
            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), Uri.fromFile(file));
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.e(TAG, "setImage: " + bitmap);
        imageView.setImageBitmap(bitmap);
        rotateBitmap = bitmap;
        degree = 0;
        ll_camera_gallery_wrap.setVisibility(View.GONE);
        fl_rotate_wrap.setVisibility(View.VISIBLE);

        /**
         *  tempFile 사용 후 null 처리를 해줘야 합니다.
         *  (resultCode != RESULT_OK) 일 때 tempFile 을 삭제하기 때문에
         *  기존에 데이터가 남아 있게 되면 원치 않은 삭제가 이뤄집니다.
         */
    }

    /**
     * 비트맵 회전하는 메소드, 인자값으로 비트맵과,  회전 각도 보냄
     *
     * @param bitmap
     * @param rotate
     * @return
     */
    public Bitmap rotateBitmap(Bitmap bitmap, int rotate) {

        Matrix rotateMatrix = new Matrix();
        switch (rotate) {
            case 0:
                rotateMatrix.postRotate(0);
                break;
            case 90:
                rotateMatrix.postRotate(90);
                break;
            case 180:
                rotateMatrix.postRotate(180);
                break;
            case 270:
                rotateMatrix.postRotate(270);
                break;
            case 360:
                rotateMatrix.postRotate(360);
                degree = 0;
                break;
            case -90:
                rotateMatrix.postRotate(-90);
                break;
            case -180:
                rotateMatrix.postRotate(-180);
                break;
            case -270:
                rotateMatrix.postRotate(-270);
                break;
            case -360:
                rotateMatrix.postRotate(-360);
                degree = 0;
                break;
            default:
                degree = 0;
                break;
        }
        return rotateImage(bitmap, rotateMatrix);
    }


    public Bitmap rotateImage(Bitmap bitmap, Matrix matrix) {
        Bitmap sideInversionImg = Bitmap.createBitmap(bitmap, 0, 0,
                bitmap.getWidth(), bitmap.getHeight(), matrix, false);
        rotateBitmap = sideInversionImg;
        return sideInversionImg;
    }


    private void requestPermission() {
        boolean shouldProviceRationale =
                (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                        || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA));//사용자가 이전에 거절한적이 있어도 true 반환

        if (shouldProviceRationale) {
            //앱에 필요한 권한이 없어서 권한 요청
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE
                    , Manifest.permission.WRITE_EXTERNAL_STORAGE
                    , Manifest.permission.CAMERA}, PERMISSIONS_REQUEST_CODE);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE
                    , Manifest.permission.WRITE_EXTERNAL_STORAGE
                    , Manifest.permission.CAMERA}, PERMISSIONS_REQUEST_CODE);
//            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSIONS_REQUEST_CODE);
            //권한있을때.
            //오레오부터 꼭 권한체크내에서 파일 만들어줘야함
//            makeDir();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case PERMISSIONS_REQUEST_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //권한 허용 선택시
                    //오레오부터 꼭 권한체크내에서 파일 만들어줘야함
                    try {
                        tempFile = createImageFile();
                        Log.e(TAG, "onRequestPermissionsResult: tempFile 생성" + tempFile);
                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.e("파일 생성 실패", "failed");
                        Toast.makeText(this, "이미지 처리 오류! 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    //사용자가 권한 거절시
                    denialDialog();
                }
                return;
            }
        }
    }


    public void denialDialog() {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("알림")
                .setMessage("저장소 권한이 필요합니다. 환경 설정에서 저장소 권한을 허가해주세요.")
                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent();
                        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package",
                                BuildConfig.APPLICATION_ID, null);
                        intent.setData(uri);
                        intent.setFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent); //확인버튼누르면 바로 어플리케이션 권한 설정 창으로 이동하도록
                    }
                })
                .create()
                .show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        progressBar.setVisibility(View.GONE);
        btn_identification_moveToBank.setVisibility(View.VISIBLE);
    }

    //Android N crop image
    public void cropImage() {

        Log.e(TAG, "cropImage: 진입");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            this.grantUriPermission("com.android.camera", photoUri,
                    Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(photoUri, "image/*");

        List<ResolveInfo> list = getPackageManager().queryIntentActivities(intent, 0);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            grantUriPermission(list.get(0).activityInfo.packageName, photoUri,
                    Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }
        int size = list.size();
        if (size == 0) {
            return;
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            }
            intent.putExtra("crop", "true");
//            intent.putExtra("aspectX", 5);
//            intent.putExtra("aspectY", 7);
            intent.putExtra("scale", true);
            File croppedFileName = null;
            try {
                croppedFileName = createImageFile();
            } catch (IOException e) {
                e.printStackTrace();
            }

            File folder = new File(Environment.getExternalStorageDirectory() + "/PionManager/");
            tempFile = new File(folder.toString(), croppedFileName.getName());

            photoUri = FileProvider.getUriForFile(GalleryCamera.this,
                    "kr.co.pionmanager.www.provider", tempFile);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            }

            intent.putExtra("return-data", false);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
            intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());


            Intent i = new Intent(intent);
            ResolveInfo res = list.get(0);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                i.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

                grantUriPermission(res.activityInfo.packageName, photoUri,
                        Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
            }
            i.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            startActivityForResult(i, CROP_FROM_CAMERA);
        }
    }
}
