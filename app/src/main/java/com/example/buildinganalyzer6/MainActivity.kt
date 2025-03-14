package com.example.buildinganalyzer6

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.buildinganalyzer6.GraphAlgorithms.Graph


class MainActivity : AppCompatActivity() {

    private lateinit var locationManager: LocationManager


    var i: Int = 0
    var longitude=0.0
    var latitude=0.0
    var prev : String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.reader)
        var g1 = Graph()
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        val insertbutton = findViewById<Button>(R.id.button3)
        insertbutton.setOnClickListener {
            Log.d("Latitude", latitude.toString())
            Log.d("Longitude", longitude.toString())
            prev += latitude.toString() + "," + longitude.toString() + "\n"

            val previousLocationText = findViewById<TextView>(R.id.textView7)
            previousLocationText.text = prev
            g1.addNode(Graph.latLonToCartesian(i + 1, latitude, longitude));
            i++
        }

val donebutton=findViewById<Button>(R.id.button4)
        donebutton.setOnClickListener{
            g1.addEdge(1, 2, 2.0);
            g1.addEdge(2, 3, 1.0);
            g1.addEdge(3, 4, 2.0);
            g1.addEdge(4, 5, 3.0);
            g1.addEdge(1, 3, 2.5);
            g1.addEdge(2, 4, 2.2);
            g1.addEdge(3, 5, 2.8);
            Log.d("p1","reached")
            var viewRange = 3.0; // Example view range
            var gridStep = 0.5;  // Example grid step size

            Log.d("p2","reached")
                var optimalNode=g1.findOptimalCameraPosition(viewRange, gridStep);

            if (optimalNode != null) {
                Log.d("Optimal camera position is at coordinates: X", optimalNode.x.toString() )
                Log.d("Optimal camera position is at coordinates: Y", optimalNode.y.toString())
            } else {
                Log.d("No optimal position found.",viewRange.toString());
            }
        }





        }
        override fun onResume() {
            super.onResume()

            // Check if location services are enabled
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                // Request location updates
                locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    0,
                    0f,
                    locationListener
                )

            } else {
                // Request the permission from the user
                //val REQUEST_CODE = 123
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    123
                )
            }
        }

        override fun onPause() {
            super.onPause()

            // Remove location updates
            locationManager.removeUpdates(locationListener)
        }


    val locationListener = object : LocationListener {


        override fun onLocationChanged(location: Location) {

            val longitudeTextView = findViewById<TextView>(R.id.textView6)
            val latitudeTextView = findViewById<TextView>(R.id.textView5)
            // Update the UI with the new longitude and latitude
            longitude = location.longitude
            latitude = location.latitude



            longitudeTextView.text = "Longitude: $longitude"
            latitudeTextView.text = "Latitude: $latitude"


        }





        //override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}

        //override fun onProviderEnabled(provider: String?) {}

        //override fun onProviderDisabled(provider: String?) {}
    }}
/*
*                 insertbutton.setOnClickListener {
                    Log.d("Latitude", latitude.toString())
                    Log.d("Longitude", longitude.toString())
                    g1.addNode(Graph.latLonToCartesian(i + 1, latitude, longitude));
                    i++
                }*/