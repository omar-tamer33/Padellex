package com.example.padellex.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.padellex.R
import com.example.padellex.model.CourtItem
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions


class MapActivity : AppCompatActivity() , OnMapReadyCallback{
    var courtItem : CourtItem? = null
    var marker : Marker? = null
    var googleMap : GoogleMap? = null
    var latitude : Double? = null
    var longitude : Double? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)
        val getDirectionsBtn : Button= findViewById(R.id.getDirectionsBtn)
        val courtNameTv : TextView= findViewById(R.id.courtNameTv)

        courtItem = intent.getParcelableExtra("court")

         latitude = courtItem?.latitude
         longitude = courtItem?.longitude
        val courtName = courtItem?.courtName

        courtNameTv.text = courtName

        getDirectionsBtn.setOnClickListener {
            val intent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("http://maps.google.com/maps?daddr=$latitude,$longitude")
            )
            startActivity(intent)
        }

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    private fun addCourtMarkerOnMap(latitude : Double , longitude : Double) {
        val latLng = LatLng(latitude,longitude)
        if (marker == null){
            val markerOptions = MarkerOptions().title("Court Location").position(latLng)
           marker = googleMap?.addMarker(markerOptions)
        }else{
            marker?.position = latLng
        }
        googleMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,16F))
    }

    override fun onMapReady(googleMap: GoogleMap) {
        this.googleMap = googleMap
        if (longitude != null && latitude != null) {
            addCourtMarkerOnMap(latitude!!, longitude!!)
        }
    }
}