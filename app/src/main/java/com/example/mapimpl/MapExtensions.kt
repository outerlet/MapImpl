package com.example.mapimpl

import android.graphics.Color
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.*

data class MarkerAttribute(val title: String, val snippet: String, val point: LatLng)

fun GoogleMap.addMarkers(attributes: List<MarkerAttribute>): List<Marker> {
    return mutableListOf<Marker>().apply {
        attributes.forEach { attr ->
            addMarker(
                MarkerOptions()
                    .position(attr.point)
                    .title(attr.title)
                    .snippet(attr.snippet)
                    .zIndex(1.0f)
            )
        }
    }
}

fun GoogleMap.addPolyline(points: List<LatLng>): Polyline {
    return addPolyline(
        PolylineOptions()
            .addAll(points)
            .color(Color.BLUE)
            .width(5.0f)
            .zIndex(2.0f)
            .clickable(true)
    ).apply {
        tag = "This is polyline"
    }
}

fun GoogleMap.addPolygon(points: List<LatLng>): Polygon {
    return addPolygon(
        PolygonOptions()
            .addAll(points)
            .fillColor(Color.YELLOW)
            .strokeColor(Color.BLACK)
            .strokeWidth(5.0f)
            .zIndex(3.0f)
            .clickable(true)
    ).apply {
        tag = "This is polygon"
    }
}

fun GoogleMap.addCircle(position: LatLng): Circle {
    return addCircle(
        CircleOptions()
            .center(position)
            .radius(100.0)
            .fillColor(Color.GREEN)
            .strokeColor(Color.BLACK)
            .strokeWidth(5.0f)
            .zIndex(4.0f)
            .clickable(true)
    ).apply {
        tag = "This is circle"
    }
}

fun GoogleMap.addGroundOverlay(bounds: LatLngBounds): GroundOverlay {
    return addGroundOverlay(
        GroundOverlayOptions()
            .positionFromBounds(bounds)
            .anchor(0.5f, 0.5f)
            .image(BitmapDescriptorFactory.fromResource(R.drawable.ground_overlay))
            .zIndex(5.0f)
            .clickable(true)
    ).apply {
        tag = "This is ground overlay."
    }
}
