package gd.mmanage.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresPermission
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.github.florent37.camerafragment.CameraFragment
import com.github.florent37.camerafragment.CameraFragmentApi
import com.github.florent37.camerafragment.configuration.Configuration
import com.github.florent37.camerafragment.listeners.CameraFragmentControlsAdapter
import com.github.florent37.camerafragment.listeners.CameraFragmentResultAdapter
import com.github.florent37.camerafragment.listeners.CameraFragmentStateAdapter
import com.github.florent37.camerafragment.listeners.CameraFragmentVideoRecordTextAdapter
import gd.mmanage.R
import gd.mmanage.method.uu
import kotlinx.android.synthetic.main.activity_demo.*
import java.io.File
import java.util.ArrayList

/**
 * Created by Administrator on 2017/12/6.
 */
class DemoActivity : AppCompatActivity() {
    private val REQUEST_CAMERA_PERMISSIONS = 931
    var skip_jq = false//跳转到图片截取界面

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_demo)
        onAddCameraClicked()
        var position = intent.getStringExtra("position")
        skip_jq = intent.getBooleanExtra("skip_jq", false)
        when (position) {
            "1" -> {//人脸照（竖版）
                iv.setImageResource(R.mipmap.renlian)
            }
            "2" -> {//证件照(横版)
                iv.setImageResource(R.mipmap.card_bg)
            }
        }
        record_button.setOnClickListener {
            record_button.isClickable = false
            val cameraFragment = getCameraFragment()
            if (cameraFragment != null) {
                cameraFragment.takePhotoOrCaptureVideo(object : CameraFragmentResultAdapter() {
                    override fun onVideoRecorded(filePath: String?) {
                        record_button.isClickable = true
                        //Toast.makeText(baseContext, "onVideoRecorded " + filePath!!, Toast.LENGTH_SHORT).show()
                    }

                    override fun onPhotoTaken(bytes: ByteArray?, filePath: String?) {
                        //Toast.makeText(baseContext, "onPhotoTaken " + filePath!!, Toast.LENGTH_SHORT).show()
//                        var bmp = uu.getimage(100, filePath)
//                        iv.setImageBitmap(bmp)
//                        content.visibility = View.GONE
//                        iv.visibility = View.VISIBLE
                        record_button.isClickable = true
                        setResult(66, Intent().putExtra("data", filePath))
                        finish()
                    }
                }, "/storage/self/primary", "photo0")
            }
        }
    }

    private fun getCameraFragment(): CameraFragmentApi {
        return supportFragmentManager.findFragmentByTag(FRAGMENT_TAG) as CameraFragmentApi
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == 77) {
            val url = data!!.getStringExtra("data")
            var bmp = uu.getimage(100, url)
            iv.setImageBitmap(bmp)
        }
    }

    fun onAddCameraClicked() {
        if (Build.VERSION.SDK_INT > 15) {
            val permissions = arrayOf(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)

            val permissionsToRequest = ArrayList<String>()
            for (permission in permissions) {
                if (ActivityCompat.checkSelfPermission(this, permission) !== PackageManager.PERMISSION_GRANTED) {
                    permissionsToRequest.add(permission)
                }
            }
            if (!permissionsToRequest.isEmpty()) {
                ActivityCompat.requestPermissions(this, permissionsToRequest.toTypedArray(), REQUEST_CAMERA_PERMISSIONS)
            } else
                addCamera()
        } else {
            addCamera()
        }
    }

    val FRAGMENT_TAG = "camera"

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults.size != 0) {
            addCamera()
        }
    }

    @SuppressLint("MissingPermission")
    @RequiresPermission(Manifest.permission.CAMERA)
    fun addCamera() {

        val cameraFragment = CameraFragment.newInstance(Configuration.Builder()
                .setCamera(Configuration.CAMERA_FACE_REAR).build())
        supportFragmentManager.beginTransaction()
                .replace(R.id.content, cameraFragment, FRAGMENT_TAG)
                .commitAllowingStateLoss()

        if (cameraFragment != null) {
            //cameraFragment.setResultListener(new CameraFragmentResultListener() {
            //    @Override
            //    public void onVideoRecorded(String filePath) {
            //        Intent intent = PreviewActivity.newIntentVideo(CameraFragmentMainActivity.this, filePath);
            //        startActivityForResult(intent, REQUEST_PREVIEW_CODE);
            //    }
            //
            //    @Override
            //    public void onPhotoTaken(byte[] bytes, String filePath) {
            //        Intent intent = PreviewActivity.newIntentPhoto(CameraFragmentMainActivity.this, filePath);
            //        startActivityForResult(intent, REQUEST_PREVIEW_CODE);
            //    }
            //});

            cameraFragment.setStateListener(object : CameraFragmentStateAdapter() {

                override fun onCurrentCameraBack() {
//                    cameraSwitchView.displayBackCamera()
                }

                override fun onCurrentCameraFront() {
//                    cameraSwitchView.displayFrontCamera()
                }

                override fun onFlashAuto() {
//                    flashSwitchView.displayFlashAuto()
                }

                override fun onFlashOn() {
//                    flashSwitchView.displayFlashOn()
                }

                override fun onFlashOff() {
//                    flashSwitchView.displayFlashOff()
                }

                override fun onCameraSetupForPhoto() {
//                    mediaActionSwitchView.displayActionWillSwitchVideo()
//
//                    recordButton.displayPhotoState()
//                    flashSwitchView.setVisibility(View.VISIBLE)
                }

                override fun onCameraSetupForVideo() {
//                    mediaActionSwitchView.displayActionWillSwitchPhoto()
//
//                    recordButton.displayVideoRecordStateReady()
//                    flashSwitchView.setVisibility(View.GONE)
                }

                override fun shouldRotateControls(degrees: Int) {
//                    ViewCompat.setRotation(cameraSwitchView, degrees.toFloat())
//                    ViewCompat.setRotation(mediaActionSwitchView, degrees.toFloat())
//                    ViewCompat.setRotation(flashSwitchView, degrees.toFloat())
//                    ViewCompat.setRotation(recordDurationText, degrees.toFloat())
//                    ViewCompat.setRotation(recordSizeText, degrees.toFloat())
                }

                override fun onRecordStateVideoReadyForRecord() {
//                    recordButton.displayVideoRecordStateReady()
                }

                override fun onRecordStateVideoInProgress() {
//                    recordButton.displayVideoRecordStateInProgress()
                }

                override fun onRecordStatePhoto() {
//                    recordButton.displayPhotoState()
                }

                override fun onStopVideoRecord() {
//                    recordSizeText.setVisibility(View.GONE)
//                    //cameraSwitchView.setVisibility(View.VISIBLE);
//                    settingsView.setVisibility(View.VISIBLE)
                }

                override fun onStartVideoRecord(outputFile: File?) {}
            })

            cameraFragment.setControlsListener(object : CameraFragmentControlsAdapter() {
                override fun lockControls() {
//                    cameraSwitchView.setEnabled(false)
//                    recordButton.setEnabled(false)
//                    settingsView.setEnabled(false)
//                    flashSwitchView.setEnabled(false)
                }

                override fun unLockControls() {
//                    cameraSwitchView.setEnabled(true)
//                    recordButton.setEnabled(true)
//                    settingsView.setEnabled(true)
//                    flashSwitchView.setEnabled(true)
                }

                override fun allowCameraSwitching(allow: Boolean) {
//                    cameraSwitchView.setVisibility(if (allow) View.VISIBLE else View.GONE)
                }

                override fun allowRecord(allow: Boolean) {
//                    recordButton.setEnabled(allow)
                }

                override fun setMediaActionSwitchVisible(visible: Boolean) {
//                    mediaActionSwitchView.setVisibility(if (visible) View.VISIBLE else View.INVISIBLE)
                }
            })

            cameraFragment.setTextListener(object : CameraFragmentVideoRecordTextAdapter() {
                override fun setRecordSizeText(size: Long, text: String?) {
                    //recordSizeText.setText(text)
                }

                override fun setRecordSizeTextVisible(visible: Boolean) {
//                    recordSizeText.setVisibility(if (visible) View.VISIBLE else View.GONE)
                }

                override fun setRecordDurationText(text: String?) {
//                    recordDurationText.setText(text)
                }

                override fun setRecordDurationTextVisible(visible: Boolean) {
//                    recordDurationText.setVisibility(if (visible) View.VISIBLE else View.GONE)
                }
            })
        }
    }
}
