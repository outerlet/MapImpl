package com.example.mapimpl

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import kotlinx.android.synthetic.main.activity_maps.*

enum class Zoom(val value: Float) {
    Default(15.0f),
    Min(14.0f),
    Max(16.0f)
}

const val REQUEST_CODE_PERMISSION = 10001

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {
    private val hasEnoughPermission: Boolean
        get() = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED

    private val defaultPosition = LatLng(34.697913, 135.493467)
    private val osakaStation = LatLng(34.702362, 135.495865)
    private val umedaShinmichi = LatLng(34.698247, 135.500521)
    private val fukushimaStation = LatLng(34.697246, 135.486794)
    private val skyBuilding = LatLng(34.705281, 135.490114)

    private val cameraTarget = LatLngBounds(LatLng(34.692544, 135.486230), LatLng(34.713960, 135.511829))

    private val points = listOf(osakaStation, umedaShinmichi, fukushimaStation)

    private val overlayBounds = LatLngBounds(LatLng(34.693416,135.487997), LatLng(34.702409,135.498936))

    private lateinit var mMap: GoogleMap
    private var mMapObject: Any? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        setSupportActionBar(toolbar)

        supportActionBar?.setTitle(R.string.title_activity_maps)

        (supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment).getMapAsync(this)
    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(map: GoogleMap) {
        mMap = map.apply {
            moveCamera(CameraUpdateFactory.newLatLngZoom(defaultPosition, Zoom.Default.value))

            setLatLngBoundsForCameraTarget(cameraTarget)
            setMaxZoomPreference(Zoom.Max.value)
            setMinZoomPreference(Zoom.Min.value)

            uiSettings.run {
                isRotateGesturesEnabled = false // 地図を回転させるジェスチャを防ぐ
                isTiltGesturesEnabled = false   // 地図を立体表示するジェスチャを防ぐ
            }

            if (hasEnoughPermission) {
                uiSettings.isMyLocationButtonEnabled = true
                isMyLocationEnabled = true
            } else {
                requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_CODE_PERMISSION)
            }

            bottomNavigation.setOnNavigationItemSelectedListener { item ->
                mMapObject?.let { obj ->
                    when (obj) {
                        is Polyline -> obj.remove()
                        is Polygon -> obj.remove()
                        is GroundOverlay -> obj.remove()
                    }
                }

                mMapObject = when (item.itemId) {
                    R.id.item_polyline -> addPolyline(points)
                    R.id.item_polygon -> addPolygon(points)
                    else -> addGroundOverlay(overlayBounds)
                }

                true
            }

            addMarkers(listOf(
                Triple(R.string.marker_title_osaka_station, R.string.marker_snippet_osaka_station, osakaStation),
                Triple(R.string.marker_title_fukushima_station, R.string.marker_snippet_fukushima_station, fukushimaStation),
                Triple(R.string.marker_title_umeda_shinmichi, R.string.marker_snippet_umeda_shinmichi, umedaShinmichi)
            ).map { e ->
                MarkerAttribute(getString(e.first), getString(e.second), e.third)
            })

            addCircle(skyBuilding)

            mMapObject = addPolyline(points)

            setOnCircleClickListener { circle ->
                Toast.makeText(this@MapsActivity, "CIRCLE : ${circle.tag as String}", Toast.LENGTH_SHORT).show()
            }

            setOnPolylineClickListener { polyline ->
                Toast.makeText(this@MapsActivity, "POLYLINE : ${polyline.tag as String}", Toast.LENGTH_SHORT).show()
            }

            setOnPolygonClickListener { polygon ->
                Toast.makeText(this@MapsActivity, "POLYGON : ${polygon.tag as String}", Toast.LENGTH_SHORT).show()
            }

            setOnGroundOverlayClickListener { overlay ->
                Toast.makeText(this@MapsActivity, "GROUND-OVERLAY : ${overlay.tag as String}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    @SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, results: IntArray) {
        if (requestCode == REQUEST_CODE_PERMISSION) {
            if (permissions.contains(Manifest.permission.ACCESS_FINE_LOCATION) && results.contains(PackageManager.PERMISSION_GRANTED)) {
                mMap.uiSettings.isMyLocationButtonEnabled = true
                mMap.isMyLocationEnabled = true
            } else {
                Toast.makeText(this, R.string.message_has_not_enough_permission, Toast.LENGTH_SHORT).show()
            }
        }
    }
}
