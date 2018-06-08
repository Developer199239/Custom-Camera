package com.brst.application.camera;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.AnimationDrawable;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Surface;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.brst.application.galary.GalaryActivity;
import com.brst.application.R;
import com.bumptech.glide.Glide;
import com.hluhovskyi.camerabutton.CameraButton;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE;
import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO;

public class MainActivity extends Activity {

    private Camera mCamera;
    private CameraPreview mCameraPreview;
    ImageView change_Camera;
    CameraButton captureButton;
    FrameLayout preview;
    int camBackId;
    int camFrontId;
    int currentCameraId = 0;
    ImageView current_image, img_flash;
    boolean isFlashModeOn = true;
    boolean cameraFront = false;
    File[] listFile;
    ArrayList<String> cameraFiles;
    TextView durationTV;
    boolean is_animated;

    //video
    //
    MediaRecorder mMediaRecorder;
    private boolean isRecording = false;
    LinearLayout videoDurationLT;
    TextView autoRecordTimerTV;
    ImageView img_auto_record;
    ProgressBar videoProgressbar;
    ImageView video_icon;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);

        initViews();

    }

    private void initViews() {
        preview = findViewById(R.id.camera_preview);
        captureButton = findViewById(R.id.capture);
        change_Camera = findViewById(R.id.change_Camera);
        current_image = findViewById(R.id.current_image);
        img_flash = findViewById(R.id.img_flash);
        videoDurationLT = findViewById(R.id.videoDurationLT);
        durationTV = findViewById(R.id.durationTV);
        autoRecordTimerTV = findViewById(R.id.autoRecordTimerTV);
        img_auto_record = findViewById(R.id.img_auto_record);
        videoProgressbar = findViewById(R.id.videoProgressbar);
        video_icon=findViewById(R.id.video_icon);
    }

    //camera instance
    //
    private Camera getCameraInstance() {
        Camera camera = null;
        try {
            camera = Camera.open();
        } catch (Exception e) {
            Log.d("Exception", e.getMessage());
        }
        return camera;
    }

    @Override
    protected void onPause() {

        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }

        super.onPause();

        preview.removeView(mCameraPreview);
        mCameraPreview = null;

        //video
        //
        releaseMediaRecorder();
        releaseCamera();

        Log.d("states", "onPause");

    }


    @Override
    protected void onResume() {
        super.onResume();

        Log.d("states", "onResume");

        mCamera = getCameraInstance();
        mCameraPreview = new CameraPreview(this, mCamera);
        preview.addView(mCameraPreview);

        // fetch recent clicked images
        //
        getFromSdcard();

        camBackId = Camera.CameraInfo.CAMERA_FACING_BACK;
        camFrontId = Camera.CameraInfo.CAMERA_FACING_FRONT;
        setCameraDisplayOrientation(this, camFrontId, mCamera);

        videoProgressbar.setMax(15);

        //set Maximum video duration
        //

        captureButton.setVideoDuration(15000);

        //record video
        //
        captureButton.setOnVideoEventListener(new CameraButton.OnVideoEventListener() {
            @Override
            public void onStart() {
                videoDurationLT.setVisibility(View.VISIBLE);
                videoProgressbar.setVisibility(View.VISIBLE);
                if (prepareVideoRecorder()) {
                    setRecordingDuration();
                    // img_flash.setVisibility(View.GONE);
                    //  change_Camera.setVisibility(View.GONE);
                    mMediaRecorder.start();
                    isRecording = true;

                } else {

                    releaseMediaRecorder();

                }
            }

            @Override
            public void onFinish() {
                if (isRecording) {
                    // img_flash.setVisibility(View.VISIBLE);
                    // change_Camera.setVisibility(View.VISIBLE);
                    videoDurationLT.setVisibility(View.GONE);
                    mMediaRecorder.stop();
                    releaseMediaRecorder();
                    mCamera.lock();
                    isRecording = false;
                    videoProgressbar.setVisibility(View.GONE);
                }


            }

            @Override
            public void onCancel() {

            }
        });

        //click picture
        //
        captureButton.setOnPhotoEventListener(new CameraButton.OnPhotoEventListener() {
            @Override
            public void onClick() {

                if (!cameraFront) {
                    if (isFlashModeOn) {
                        autoFocus();
                        isFlashOn();
                        mCamera.takePicture(null, null, mPicture);
                    } else {
                        autoFocus();
                        isFlashOff();
                        mCamera.takePicture(null, null, mPicture);
                    }
                } else {
                    mCamera.takePicture(null, null, mPicture);
                }
            }
        });
        //change camera
        change_Camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (is_animated) {
                    Animation animation = AnimationUtils.loadAnimation(getApplicationContext(),
                            R.anim.reverse);
                    change_Camera.startAnimation(animation);
                    is_animated = false;
                } else {
                    Animation animation = AnimationUtils.loadAnimation(getApplicationContext(),
                            R.anim.rotate);
                    change_Camera.startAnimation(animation);
                    is_animated = true;
                }

                switchCamera();
            }
        });

        img_flash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (isFlashModeOn) {
                    isFlashModeOn = false;
                    img_flash.setImageResource(R.drawable.ic_flash_off);
                } else {
                    isFlashModeOn = true;
                    img_flash.setImageResource(R.drawable.ic_flash_white);
                }
            }
        });

        current_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, GalaryActivity.class).putExtra("camaraFront", cameraFront).putStringArrayListExtra("list", cameraFiles));
            }
        });


        img_auto_record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setTimerToRecord();
            }
        });
    }

    Camera.PictureCallback mPicture = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            File pictureFile = getOutputMediaFile(MEDIA_TYPE_IMAGE);
            Log.d("path", "==" + pictureFile);
            if (pictureFile == null) {
                return;
            }
            try {
                FileOutputStream fos = new FileOutputStream(pictureFile);
                fos.write(data);
                fos.close();
                mCamera.startPreview();
            } catch (FileNotFoundException e) {
                Log.d("Exception", e.getMessage());
            } catch (IOException e) {
                Log.d("Exception", e.getMessage());
            }
            try {
                Bitmap bmp = BitmapFactory.decodeFile(pictureFile.getPath());
                Matrix matrix = new Matrix();
                if (cameraFront) {
                    matrix.postRotate(270);
                } else {
                    matrix.postRotate(90);
                }
                bmp = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, true);

                FileOutputStream fOut;
                try {
                    fOut = new FileOutputStream(pictureFile.getPath());
                    bmp.compress(Bitmap.CompressFormat.JPEG, 100, fOut);

                    getFromSdcard();

                    fOut.flush();
                    fOut.close();

                } catch (FileNotFoundException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    };


    private File getOutputMediaFile(int type) {

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "MyCameraApp");
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("MyCameraApp", "Failed to create directory");
                return null;
            }
        }
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
                .format(new Date());
        File mediaFile;
        if(type==MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    + "IMG_" + timeStamp + ".jpg");

            Log.d("MyCameraApp", "=" + mediaFile);
        }else  if (type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "VID_" + timeStamp + ".mp4");
        }else {
            return null;
        }

        return mediaFile;
    }

    public void setCameraDisplayOrientation(Activity activity, int cameraId, android.hardware.Camera camera) {
        android.hardware.Camera.CameraInfo info = new android.hardware.Camera.CameraInfo();
        android.hardware.Camera.getCameraInfo(cameraId, info);
        int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
        int degrees = 0;

        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }

        autoFocus();

        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360; // compensate the mirror
        } else {
            result = (info.orientation - degrees + 360) % 360;
        }
        camera.setDisplayOrientation(result);
    }

    private void switchCamera() {
        mCamera.stopPreview();
        mCamera.release();

        if (currentCameraId == Camera.CameraInfo.CAMERA_FACING_BACK) {
            currentCameraId = Camera.CameraInfo.CAMERA_FACING_FRONT;
            cameraFront = true;
        } else {
            currentCameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
            cameraFront = false;
        }
        mCamera = Camera.open(currentCameraId);
        try {
            mCamera.setPreviewDisplay(mCameraPreview.getHolder());
        } catch (IOException e) {
            e.printStackTrace();
        }
        mCamera.setDisplayOrientation(90);
        mCamera.startPreview();
    }

    private void isFlashOn() {
        Camera.Parameters p = mCamera.getParameters();
        p.setFlashMode(Camera.Parameters.FLASH_MODE_ON);
        mCamera.setParameters(p);

    }

    private void isFlashOff() {
        Camera.Parameters p = mCamera.getParameters();
        p.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
        mCamera.setParameters(p);
    }

    private void autoFocus() {
        Camera.Parameters params = mCamera.getParameters();
        params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
        mCamera.setParameters(params);
    }

    public void getFromSdcard() {
        cameraFiles = new ArrayList<>();
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "MyCameraApp");
        if (file.isDirectory()) {
            listFile = file.listFiles();
            for (int i = 0; i < listFile.length; i++) {
                cameraFiles.add(listFile[i].getAbsolutePath());
            }
            Collections.reverse(cameraFiles);
        }
        if (cameraFiles.size() > 0) {
            String path = cameraFiles.get(0);
            if (path != null) {
                if (path.contains(".jpg")) {
                    //Glide.with(MainActivity.this).load(path).into(current_image);
                    video_icon.setVisibility(View.GONE);
                    BitmapFactory.Options option = new BitmapFactory.Options();
                    option.inPreferredConfig = Bitmap.Config.ARGB_8888;
                    current_image.setImageBitmap(BitmapFactory.decodeFile(path));


                }else if (path.contains(".mp4")){

                    Bitmap thumb = ThumbnailUtils.createVideoThumbnail(path,
                            MediaStore.Images.Thumbnails.MINI_KIND);
                  //  Glide.with(MainActivity.this).load(path).into(current_image);
                    current_image.setImageBitmap(thumb);
                    video_icon.setVisibility(View.VISIBLE);


                }
            }
        }

    }
    //video
    //
    private boolean prepareVideoRecorder() {

        mMediaRecorder = new MediaRecorder();

        mCamera.setDisplayOrientation(90);
        mMediaRecorder.setOrientationHint(90);

        // Step 1: Unlock and set camera to MediaRecorder
        mCamera.unlock();
        mMediaRecorder.setCamera(mCamera);

        // Step 2: Set sources
        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);

        // Step 3: Set a CamcorderProfile (requires API Level 8 or higher)
        mMediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH));


        // Step 4: Set output file
        mMediaRecorder.setOutputFile(getOutputMediaFile(MEDIA_TYPE_VIDEO).toString());

        // Step 5: Set the preview output
        mMediaRecorder.setPreviewDisplay(mCameraPreview.getHolder().getSurface());


        // Step 6: Prepare configured MediaRecorder
        try {
            mMediaRecorder.prepare();
        } catch (IllegalStateException e) {
            Log.d("VIDEO", "IllegalStateException preparing MediaRecorder: " + e.getMessage());
            releaseMediaRecorder();
            return false;
        } catch (IOException e) {
            Log.d("VIDEO", "IOException preparing MediaRecorder: " + e.getMessage());
            releaseMediaRecorder();
            return false;
        }
        return true;
    }

    private void releaseMediaRecorder() {
        if (mMediaRecorder != null) {
            mMediaRecorder.reset();
            mMediaRecorder.release();
            mMediaRecorder = null;
            mCamera.lock();
        }
    }

    private void releaseCamera() {
        if (mCamera != null) {
            mCamera.release();
            mCamera = null;
        }
    }

    public void setTimerToRecord() {
        new CountDownTimer(6000, 1000) {
            int counter = 6;

            public void onTick(long millisUntilFinished) {
                counter--;
                autoRecordTimerTV.setVisibility(View.VISIBLE);
                autoRecordTimerTV.setText(String.valueOf(counter));

                Log.e("Counter_Value", "==" + counter);
                if (counter < 1) {
                    onFinish();
                }
            }

            public void onFinish() {

                autoRecordTimerTV.setVisibility(View.GONE);


            }
        }.start();
    }

    public void setRecordingDuration() {
        new CountDownTimer(16000, 1000) {
            int counter = 0;

            public void onTick(long millisUntilFinished) {
                counter++;
                if (counter > 9) {

                    durationTV.setText("00:" + counter);

                } else {

                    durationTV.setText("00:0" + counter);

                }

                videoProgressbar.setProgress(counter);

                Log.e("Counter_Value", "==" + counter);
            }

            public void onFinish() {

            }
        }.start();
    }


}
