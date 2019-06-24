package com.example.deviceorientationtest

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import io.reactivex.Flowable
import io.reactivex.functions.Consumer
import java.util.concurrent.TimeUnit
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity(), SensorEventListener {
    private val TAG = MainActivity::class.simpleName

    private lateinit var sensorManager : SensorManager
    private val accelerometerReading = FloatArray(3)
    private val magnetometerReading = FloatArray(3)

    private val rotationMatrix = FloatArray(9)
    private val orientationAngles = FloatArray(3)

    private var currentDegree = 0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
    }

    override fun onResume() {
        super.onResume()
        // Get updates from the accelerometer and magnetometer at a constant rate.
        // To make batch operations more efficient and reduce power consumption,
        // provide support for delaying updates to the application.
        //
        // In this example, the sensor reporting delay is small enough such that
        // the application receives an update before the system checks the sensor
        // readings again.

        sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)?.also { accelerometer ->
            sensorManager.registerListener(
                this,
                accelerometer,
                SensorManager.SENSOR_DELAY_NORMAL,
                SensorManager.SENSOR_DELAY_UI
            )
        }
        sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)?.also { magneticField ->
            sensorManager.registerListener(
                this,
                magneticField,
                SensorManager.SENSOR_DELAY_NORMAL,
                SensorManager.SENSOR_DELAY_UI
            )
        }
//        val degree : Float = Math.round(orientationAngles[0]).toFloat()
//        Log.d(TAG, " degree: " + degree)
        Flowable.interval(1000, TimeUnit.MILLISECONDS)
            .subscribe(Consumer { updateOrientationAngles() }, Consumer {t ->  Log.e(TAG, "error: "+ t.fillInStackTrace()) })
    }

    override fun onPause() {
        super.onPause()

        // Don't receive any more updates from either sensor.
        sensorManager.unregisterListener(this)
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        Log.d(TAG, "onAccuracyChanged accuracy: $accuracy")
    }

    // Get readings from accelerometer and magnetometer. To simplify calculations,
    // consider storing these readings as unit vectors.
    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
            System.arraycopy(event.values, 0, accelerometerReading, 0, accelerometerReading.size)
        } else if (event.sensor.type == Sensor.TYPE_MAGNETIC_FIELD) {
            System.arraycopy(event.values, 0, magnetometerReading, 0, magnetometerReading.size)
        }
        Log.d(TAG, "onSensorChanged: event.value0: " + event.values[0])
//        updateOrientationAngles()
    }

    // Compute the three orientation angles based on the most recent readings from
    // the device's accelerometer and magnetometer.
    fun updateOrientationAngles() {
        // Update rotation matrix, which is needed to update orientation angles.
        SensorManager.getRotationMatrix(
            rotationMatrix,
            null,
            accelerometerReading,
            magnetometerReading
        )
//        Log.d(TAG, "updateOrientationAngles: RotationMatrix: " + SensorManager.getRotationMatrix(
//            rotationMatrix,
//            null,
//            accelerometerReading,
//            magnetometerReading
//        ))

//        val remappedRotationMatrix = FloatArray(9)
//        SensorManager.remapCoordinateSystem(rotationMatrix , SensorManager.AXIS_X, SensorManager.AXIS_Z , remappedRotationMatrix)
//        Log.d(TAG, "remappedRotationMatrix is null? " + (remappedRotationMatrix == null))
//        Log.d(TAG, "remappedRotationMatrix0: " + remappedRotationMatrix[0])

        // "mRotationMatrix" now has up-to-date information.

        SensorManager.getOrientation(rotationMatrix, orientationAngles)
//        Log.d(TAG, "updateOrientationAngles: Orientation: " + SensorManager.getOrientation(rotationMatrix, orientationAngles))
        Log.d(TAG, "rotationMatrix is null? " + (rotationMatrix == null))
        Log.d(TAG, "orientationAngles is null? " + (orientationAngles == null))
        Log.d(TAG, "orientationAngles0: " + orientationAngles[0])
        Log.d(TAG, "orientationAngles1: " + orientationAngles[1])
        Log.d(TAG, "orientationAngles2: " + orientationAngles[2])

//        mAzimuth= (int) ( Math.toDegrees( SensorManager.getOrientation(rotationMatrix, orientationAngles)[0].toDouble() ) + 360 ) % 360;
        val degree=  (( Math.toDegrees( SensorManager.getOrientation(rotationMatrix, orientationAngles)[0].toDouble() ) + 360 ) % 360).toFloat()

//        val degree : Float = Math.round(orientationAngles[0]).toFloat()
        Log.d(TAG, " degree: " + degree)
        // create a rotation animation (reverse turn degree degrees)
        val ra = RotateAnimation(
            currentDegree,
            -degree,
            Animation.RELATIVE_TO_SELF, 0.5f,
            Animation.RELATIVE_TO_SELF,
            0.5f
        )

        // how long the animation will take place
        ra.duration = 210

        // set the animation after the end of the reservation status
        ra.fillAfter = true

        // Start the animation
        image.startAnimation(ra)
        currentDegree = -degree

        // "mOrientationAngles" now has up-to-date information.

////        val orientation = SensorManager.getOrientation(rotationMatrix, orientationAngles)
//        val orientation = SensorManager.getOrientation(rotationMatrix, orientationAngles)
//        Log.d(TAG, "orientation0: " + orientation[0])
//        Log.d(TAG, "orientation1: " + orientation[1])
//        Log.d(TAG, "orientation2: " + orientation[2])

//        var translatedOrientation : FloatArray? = null
//        SensorManager.remapCoordinateSystem(orientation, SensorManager.AXIS_Y, SensorManager.AXIS_MINUS_X, translatedOrientation)
//        Log.d(TAG, "orientation0: " + orientation[0])
//        Log.d(TAG, "orientation1: " + orientation[1])
//        Log.d(TAG, "orientation2: " + orientation[2])
////        val isTranslateSucceed = SensorManager.remapCoordinateSystem(orientation, SensorManager.AXIS_Y, SensorManager.AXIS_MINUS_X, translatedOrientation)
////        if (isTranslateSucceed){
////            Log.d(TAG, "translatedOrientation0: " + translatedOrientation!![0])
////            Log.d(TAG, "translatedOrientation1: " + translatedOrientation!![1])
////            Log.d(TAG, "translatedOrientation2: " + translatedOrientation!![2])
////        }
    }
}
