package io.flaterlab.qrcodetunduk

import android.Manifest
import android.app.Dialog
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.SurfaceHolder
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.vision.CameraSource
import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.barcode.Barcode
import com.google.android.gms.vision.barcode.BarcodeDetector
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.dialog_web_view.*


class MainActivity : AppCompatActivity(), Detector.Processor<Barcode> {
    lateinit var cameraSource: CameraSource
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val barcodeDetector = BarcodeDetector.Builder(this)
                .setBarcodeFormats(Barcode.ALL_FORMATS)
                .build()

        barcodeDetector.setProcessor(this)

        cameraSource = CameraSource.Builder(applicationContext, barcodeDetector)
                .setRequestedPreviewSize(1024,1024)
                .setAutoFocusEnabled(true)
                .build()

        restart.setOnClickListener {
            cameraSource.start(container.holder)
        }

        container.holder.addCallback(object: SurfaceHolder.Callback{
            override fun surfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {
                if(ActivityCompat.checkSelfPermission(applicationContext, Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(this@MainActivity, arrayOf(Manifest.permission.CAMERA), 1024)
                    return
                }
                cameraSource.start(container.holder)
            }

            override fun surfaceDestroyed(holder: SurfaceHolder?) {
                cameraSource.stop()
            }

            override fun surfaceCreated(holder: SurfaceHolder?) {

            }
        })
    }

    override fun release() {
        Toast.makeText(this, "realised", Toast.LENGTH_SHORT).show()
    }

    override fun receiveDetections(p0: Detector.Detections<Barcode>?) {
        Log.d("tilek", p0?.detectedItems?.size().toString())
        if (p0?.detectedItems?.size() != 0){

            runOnUiThread {
                Toast.makeText(this, p0!!.detectedItems.valueAt(0).rawValue, Toast.LENGTH_LONG).show()
                openWebView(p0.detectedItems.valueAt(0).rawValue)
                cameraSource.stop()
            }
        }
    }

    fun openWebView(url: String){

        if (url.startsWith("https://1312.tunduk.kg/") || url.startsWith("http://1312.tunduk.kg/")){
            val dialog = Dialog(this, android.R.style.Theme_Black_NoTitleBar_Fullscreen)
            dialog.setContentView(R.layout.dialog_web_view)
            dialog.web_view.webViewClient = MyViewClient()
            dialog.web_view.loadUrl(url)
            dialog.show()
        }else{
            runOnUiThread {
                Toast.makeText(this, "Ошибка QR code неправильный", Toast.LENGTH_LONG).show()
            }
        }
    }
}
