package com.mapbox.mapboxsdk.testapp.activity.offline

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.constants.Style
import com.mapbox.mapboxsdk.log.Logger
import com.mapbox.mapboxsdk.offline.OfflineManager
import com.mapbox.mapboxsdk.offline.OfflineRegion
import com.mapbox.mapboxsdk.testapp.R
import kotlinx.android.synthetic.main.activity_merge_offline_regions.*
import java.io.File
import java.io.FileOutputStream

class MergeOfflineRegionsActivity : AppCompatActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_merge_offline_regions)

    // forcing offline state
    Mapbox.setConnected(false)

    // copy db asset to internal memory
    copyAsset()

    mapView.onCreate(savedInstanceState)
    load_region_btn.setOnClickListener {
      if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        != PackageManager.PERMISSION_GRANTED
        || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
        != PackageManager.PERMISSION_GRANTED) {
        ActivityCompat.requestPermissions(this,
          arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 11)
      } else {
        mergeDb()
      }
    }
  }

  override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    if (requestCode == 11) {
      for (result in grantResults) {
        if (result != PackageManager.PERMISSION_GRANTED) {
          finish()
        }
      }
      mergeDb()
    }
  }

  private fun mergeDb() {
    OfflineManager.getInstance(this).mergeOfflineRegions(
      this.filesDir.absolutePath + "/offline.db",
      object : OfflineManager.MergeOfflineRegionsCallback {
        override fun onMerge(offlineRegions: Array<OfflineRegion>) {
          mapView.setStyleUrl(Style.SATELLITE)
          Toast.makeText(
            this@MergeOfflineRegionsActivity,
            String.format("Merged %d regions.", offlineRegions.size),
            Toast.LENGTH_LONG).show()
        }

        override fun onError(error: String) {
          Logger.e("MBGL_OFFLINE_DB_MERGE", error)
        }
      })
  }

  fun copyAsset() {
    val bufferSize = 1024
    val assetManager = this.assets
    val inputStream = assetManager.open("offline.db")
    val outputStream = FileOutputStream(File(this.filesDir, "offline.db"))

    try {
      inputStream.copyTo(outputStream, bufferSize)
    } finally {
      inputStream.close()
      outputStream.flush()
      outputStream.close()
    }
  }

  override fun onStart() {
    super.onStart()
    mapView.onStart()
  }

  override fun onResume() {
    super.onResume()
    mapView.onResume()
  }

  override fun onPause() {
    super.onPause()
    mapView.onPause()
  }

  override fun onStop() {
    super.onStop()
    mapView.onStop()
  }

  override fun onLowMemory() {
    super.onLowMemory()
    mapView.onLowMemory()
  }

  override fun onDestroy() {
    super.onDestroy()
    mapView.onDestroy()
  }

  override fun onSaveInstanceState(outState: Bundle) {
    super.onSaveInstanceState(outState)
    mapView.onSaveInstanceState(outState)
  }
}
