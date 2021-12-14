package com.example.miclone.ui

import android.app.Activity
import android.bluetooth.*
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.miclone.contants.Constants.CALORIES_CHAR_UUID
import com.example.miclone.contants.Constants.MI_BAND_MAX_ADDRESS
import com.example.miclone.contants.Constants.SERVICE_UUID
import com.example.miclone.contants.Constants.BATTERY_CHAR_UUID
import com.example.miclone.R
import com.example.miclone.entities.PreviousValueEntity
import com.example.miclone.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.lang.IllegalArgumentException

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private val bluetoothAdapter: BluetoothAdapter by lazy {
        val bluetoothManager =
            requireContext().getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothManager.adapter
    }

    private var bluetoothGatt: BluetoothGatt? = null

    private lateinit var mainViewModel: MainViewModel

    private lateinit var bluetoothEnabledTv: TextView
    private lateinit var batteryPercentTv: TextView
    private lateinit var stepsWalkedTv: TextView
    private lateinit var caloriesBurnedTv: TextView
    private lateinit var distanceCoveredTv: TextView
    private lateinit var connectionStatusTv: TextView

    private var batteryLevel: Int = 0
    private var steps: Int = 0
    private var calories: Int = 0
    private var distance: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainViewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bluetoothEnabledTv = view.findViewById(R.id.bluetoothConnected)
        batteryPercentTv = view.findViewById(R.id.batteryPercentage)
        stepsWalkedTv = view.findViewById(R.id.stepTextView)
        caloriesBurnedTv = view.findViewById(R.id.caloriesTextView)
        distanceCoveredTv = view.findViewById(R.id.distanceTextView)
        connectionStatusTv = view.findViewById(R.id.connectionStatus)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onResume() {
        super.onResume()
        if (!bluetoothAdapter.isEnabled) {
            promptBluetoothEnable()
        } else {
            connectDevice()
        }
    }

    private fun connectDevice() {
        try {
            val device = bluetoothAdapter.getRemoteDevice(MI_BAND_MAX_ADDRESS)
            device.connectGatt(requireContext(), false, bluetoothGattCallBack)
        } catch (e: IllegalArgumentException) {
            Log.d(TAG, "Invalid Mac Address")
        }
    }

    private val bluetoothGattCallBack = object : BluetoothGattCallback() {

        private val characteristicList = mutableListOf<BluetoothGattCharacteristic>()

        override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                Log.d(TAG, "Device with ${gatt.device.address} is connected")
                bluetoothGatt = gatt
                bluetoothGatt?.discoverServices()
                lifecycleScope.launch {
                    connectionStatusTv.text = getString(R.string.connected)
                }
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                Log.d(TAG, "Device Disconnected")
                lifecycleScope.launch {
                    connectionStatusTv.text = getString(R.string.disconnected)
                }
                connectDevice()
            }
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
            Log.d(TAG, "Services Discovered")
            val serviceUUID = gatt.getService(SERVICE_UUID)
            val batteryCharUuid = serviceUUID.getCharacteristic(BATTERY_CHAR_UUID)
            val caloriesCharUuid = serviceUUID.getCharacteristic(CALORIES_CHAR_UUID)
            characteristicList.add(batteryCharUuid)
            characteristicList.add(caloriesCharUuid)
            readDataFromDevice()
        }

        private fun readDataFromDevice() {
            bluetoothGatt?.readCharacteristic(characteristicList.first())
        }

        override fun onCharacteristicRead(
            gatt: BluetoothGatt?,
            characteristic: BluetoothGattCharacteristic?,
            status: Int
        ) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                when (characteristic?.uuid) {
                    BATTERY_CHAR_UUID -> {
                        Log.d(TAG, characteristic.value!!.toHexString())
                        showBatteryPercent(characteristic.value)
                    }
                    CALORIES_CHAR_UUID -> {
                        Log.d(TAG, characteristic.value!!.toHexString())
                        showStepsAndCalories(characteristic.value)
                    }
                }
                characteristicList.removeAt(0)
                if (characteristicList.size > 0) {
                    readDataFromDevice()
                }else{
                    Log.d(TAG,"Read Completed")
                    lifecycleScope.launch {
                        batteryPercentTv.text = batteryLevel.toString()
                        stepsWalkedTv.text = steps.toString()
                        distanceCoveredTv.text = distance.toString()
                        caloriesBurnedTv.text = calories.toString()
                    }
                }
            }
        }
    }

    private fun showBatteryPercent(value: ByteArray) {
        batteryLevel = value[1].toUByte().toInt()
        Log.d(TAG, "Battery : $batteryLevel")
        return
    }

    private fun showStepsAndCalories(value: ByteArray) {
        steps = (value[4].toUByte().toInt() and 0xFF shl 24) +
                (value[3].toUByte().toInt() and 0xFF shl 16) +
                (value[2].toUByte().toInt() and 0xFF shl 8) +
                (value[1].toUByte().toInt() and 0xFF)
        Log.d(TAG, "Steps : $steps")

        distance = (value[8].toUByte().toInt() and 0xFF shl 24) +
                (value[7].toUByte().toInt() and 0xFF shl 16) +
                (value[6].toUByte().toInt() and 0xFF shl 8) +
                (value[5].toUByte().toInt() and 0xFF)
        Log.d(TAG, "Distance : $distance")

        calories = (value[12].toUByte().toInt() and 0xFF shl 24) +
                (value[11].toUByte().toInt() and 0xFF shl 16) +
                (value[10].toUByte().toInt() and 0xFF shl 8) +
                (value[9].toUByte().toInt() and 0xFF)
        Log.d(TAG, "Calories : $calories")

        return
    }

    private fun promptBluetoothEnable() {
        val intent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
        startActivityForResult(intent, ENABLE_BLUETOOTH_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            ENABLE_BLUETOOTH_REQUEST_CODE -> {
                if (resultCode == Activity.RESULT_OK) {
                    if (!bluetoothAdapter.isEnabled) {
                        bluetoothEnabledTv.visibility = View.VISIBLE
                    } else {
                        bluetoothEnabledTv.visibility = View.GONE
                    }
                } else if (resultCode == Activity.RESULT_CANCELED) {
                    bluetoothEnabledTv.visibility = View.VISIBLE
                }
            }
        }
    }

    fun ByteArray.toHexString() = joinToString("-") { "%02x".format(it) }

    override fun onPause() {
        super.onPause()
        mainViewModel.insertPreviousValue(
            PreviousValueEntity(
                0,
                batteryLevel.toString(),
                steps.toString(),
                calories.toString(),
                distance.toString()
            )
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        bluetoothGatt?.close()
        bluetoothGatt = null
    }

    companion object {
        private const val ENABLE_BLUETOOTH_REQUEST_CODE = 1
        private const val TAG = "MI_BAND"
    }
}