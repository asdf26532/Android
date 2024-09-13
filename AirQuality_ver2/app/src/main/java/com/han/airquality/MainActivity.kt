package com.han.airquality

import android.Manifest
import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.han.airquality.databinding.ActivityMainBinding
import com.han.airquality.retrofit.AirQualityResponse
import com.han.airquality.retrofit.AirQualityService
import com.han.airquality.retrofit.RetrofitConnection
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.lang.IllegalArgumentException
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

class MainActivity : AppCompatActivity() {

    lateinit var binding : ActivityMainBinding
    lateinit var locationProvider: LocationProvider

    private val PERMISSIONS_REQUEST_CODE = 100

    var latitude : Double? = 0.0
    var longitude : Double? = 0.0

    val REQUIRED_PERMISSIONS = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )

    lateinit var getGPSPermissionLauncher : ActivityResultLauncher<Intent>

    val startMapActivityResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult(),
            object : ActivityResultCallback<ActivityResult> {
            override fun onActivityResult(result: ActivityResult) {
                if(result.resultCode == Activity.RESULT_OK) {
                    latitude = result.data?.getDoubleExtra("latitude", 0.0) ?: 0.0
                    longitude = result.data?.getDoubleExtra("longitude", 0.0) ?: 0.0
                    updateUI()
                }
            }
        }
        )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        checkAllPermissions()
        updateUI()
        setRefreshButton()
        setFab()
    }

    private fun updateUI() {
        locationProvider = LocationProvider(this@MainActivity)

        if(latitude == 0.0 && longitude == 0.0) {
            latitude = locationProvider.getLocationLatitude()
            longitude = locationProvider.getLocationLongitude()
        }
        if(latitude != null && longitude != null) {
            // 1. 현재 위치 가져오고 UI업데이트
            val address = getCurrentAddress(latitude!!, longitude!!)

            address?.let{
                binding.tvLocationTitle.text = "${it.thoroughfare}"
                binding.tvLocationSubtitle.text = "${it.countryName} ${it.adminArea}"
            }
            // 2. 미세먼지 수치 가져오고 UI언데이트

            getAirQualityData(latitude!!, longitude!!)

        } else {
            Toast.makeText(this, "위도, 경도 정보를 가져올 수 없습니다", Toast.LENGTH_LONG).show()
        }
    }

    private fun getAirQualityData(latitude: Double, longitude: Double) {

        Log.d("MainActivity", "Requesting data with Latitude: $latitude, Longitude: $longitude, API Key: 992b5824-91d5-46f3-bfcb-b8975e1552d1")

        var retrofitAPI = RetrofitConnection.getInstance().create(
            AirQualityService::class.java
        )

        retrofitAPI.getAirQualityData(
            latitude.toString(),
            longitude.toString(),
            "992b5824-91d5-46f3-bfcb-b8975e1552d1"
        ).enqueue(object : Callback<AirQualityResponse> {
            override fun onResponse(
                call: Call<AirQualityResponse>,
                response: Response<AirQualityResponse>
            ) {
                if(response.isSuccessful) {
                    Toast.makeText(this@MainActivity, "최신 데이터 업데이트 완료", Toast.LENGTH_LONG).show()
                    response.body()?.let{ updateAirUI(it)}
                } else {
                    Log.e("MainActivity", "Error response code: ${response.code()}")
                    Toast.makeText(this@MainActivity, "데이터를 가져올 수 없습니다: ${response.code()}", Toast.LENGTH_LONG).show()
                    //Toast.makeText(this@MainActivity, "데이터를 가져올 수 없습니다", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<AirQualityResponse>, t: Throwable) {
                Log.e("MainActivity", "API 호출 실패: ${t.localizedMessage}")
                t.printStackTrace()
                Toast.makeText(this@MainActivity, "데이터를 가져올 수 없습니다", Toast.LENGTH_LONG).show()
            }
        }

        )

    }

    private fun updateAirUI(airQualityData : AirQualityResponse){
        val pollutionData = airQualityData.data.current.pollution

        // 수치를 지정
        binding.tvCount.text = pollutionData.aqius.toString()

        // 측정 날짜
        val dateTime = ZonedDateTime.parse(pollutionData.ts).withZoneSameInstant(ZoneId.of("Asia/Seoul")).toLocalDateTime()
        val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
        binding.tvCheckTime.text = dateTime.format(dateFormatter).toString()

        when(pollutionData.aqius) {
            in 0..50 -> {
                binding.tvTitle.text = "좋음"
                binding.imgBg.setImageResource(R.drawable.bg_good)
            }
            in 51..150 -> {
                binding.tvTitle.text = "보통"
                binding.imgBg.setImageResource(R.drawable.bg_soso)
            }
            in 151..200 -> {
                binding.tvTitle.text = "나쁨"
                binding.imgBg.setImageResource(R.drawable.bg_bad)
            }
            else-> {
                binding.tvTitle.text = "매우 나쁨"
                binding.imgBg.setImageResource(R.drawable.bg_worst)
            }
        }
    }

    private fun  setFab() {
        binding.fab.setOnClickListener {
            val intent = Intent(this, MapActivity::class.java)
            intent.putExtra("currentLat", latitude)
            intent.putExtra("currentLng", longitude)
            startMapActivityResult.launch(intent)
        }
    }

    private fun setRefreshButton() {
        binding.btnRefresh.setOnClickListener{
            updateUI()
        }
    }

    private fun getCurrentAddress (latitude : Double, longitude : Double) : Address? {
        val geoCoder = Geocoder(this, Locale.KOREA)

        val addresses : List<Address>? = try {
            geoCoder.getFromLocation(latitude, longitude, 7)
        }catch(ioException : IOException) {
            Toast.makeText(this, "지오코더 서비스 이용불가", Toast.LENGTH_LONG).show()
            return null
        }catch (illegalArgumentException : IllegalArgumentException) {
            Toast.makeText(this, "잘못된 위도, 경도 입니다", Toast.LENGTH_LONG).show()
            return null
        }

        if(addresses.isNullOrEmpty()) {
            Toast.makeText(this, "주소가 발견되지 않았습니다", Toast.LENGTH_LONG).show()
            return null
        }
        return addresses[0]

    }

    private fun checkAllPermissions(){
        if(!isLocationServicesAvailable()){
            showDialogForLocationServiceSetting()

        } else {
            isRunTimePermissionsGranted()
        }
    }

    private fun isLocationServicesAvailable() : Boolean {
        val locationManager = getSystemService(LOCATION_SERVICE) as LocationManager

        return (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
    }

    private fun isRunTimePermissionsGranted() {
        val hasFineLocationPermission = ContextCompat.checkSelfPermission(this@MainActivity, Manifest.permission.ACCESS_FINE_LOCATION)
        val hasCoarseLocationPermission = ContextCompat.checkSelfPermission(this@MainActivity, Manifest.permission.ACCESS_COARSE_LOCATION)

        if(hasFineLocationPermission!=PackageManager.PERMISSION_GRANTED || hasCoarseLocationPermission!=PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this@MainActivity, REQUIRED_PERMISSIONS,PERMISSIONS_REQUEST_CODE)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == PERMISSIONS_REQUEST_CODE && grantResults.size == REQUIRED_PERMISSIONS.size) {
            var checkResult = true

            for(result in grantResults) {
                if(result != PackageManager.PERMISSION_GRANTED){
                    checkResult = false
                    break;
                }
            }

            if(checkResult) {
                // 위치값을 가져올수있음
                updateUI()
            } else {
                Toast.makeText(this@MainActivity, "권한이 거부되었습니다. 앱을 종료합니다", Toast.LENGTH_LONG).show()
                finish()
            }
        }
    }

    private fun showDialogForLocationServiceSetting() {
        getGPSPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            Log.d("LOG : :: : : : :  ", result.resultCode.toString())
            if (isLocationServicesAvailable()) {
                isRunTimePermissionsGranted()
            } else {
                Toast.makeText(this@MainActivity, "위치 서비스를 사용할 수 없습니다. 앱을 종료합니다", Toast.LENGTH_LONG)
                    .show()
                finish()
            }
        }

        val builder : AlertDialog.Builder = AlertDialog.Builder(this@MainActivity)
        builder.setTitle("위치 서비스 비활성화")
        builder.setMessage("위치 서비스가 꺼져있습니다. 설정 후 다시 실행해주십시오")
        builder.setCancelable(true)

        builder.setPositiveButton("설정", DialogInterface.OnClickListener { dialogInterface, i ->
            val callGPSSettingIntent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            getGPSPermissionLauncher.launch(callGPSSettingIntent)
        })

        builder.setNegativeButton("취소",DialogInterface.OnClickListener { dialogInterface, i ->
            dialogInterface.cancel()
            Toast.makeText(this@MainActivity, "위치 서비스를 사용할 수 없습니다. 앱을 종료합니다", Toast.LENGTH_LONG).show()
            finish()
        })

        builder.create().show()

        }
    }
