package com.example.miclone

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
import com.example.miclone.Constants.CALORIES_CHAR_UUID
import com.example.miclone.Constants.MI_BAND_MAX_ADDRESS
import com.example.miclone.Constants.SERVICE_UUID
import com.example.miclone.Constants.STEPS_CHAR_UUID
import com.example.miclone.databinding.FragmentHomeBinding
import java.lang.IllegalArgumentException

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding

    private val bluetoothAdapter:BluetoothAdapter by lazy {
        val bluetoothManager = requireContext().getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothManager.adapter
    }

    private var bluetoothGatt: BluetoothGatt? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(layoutInflater)

        if (!bluetoothAdapter.isEnabled){
            promptBluetoothEnable()
        }else {
            connectDevice()
        }

        return binding.root
    }

    private fun connectDevice() {
        try {
            val device = bluetoothAdapter.getRemoteDevice(MI_BAND_MAX_ADDRESS)
            device.connectGatt(requireContext(), false, bluetoothGattCallBack)
        }catch (e: IllegalArgumentException){
            Log.d(TAG,"Invalid Mac Address")
        }
    }

    private val bluetoothGattCallBack = object : BluetoothGattCallback(){

        private val characteristicList = mutableListOf<BluetoothGattCharacteristic>()

        override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
            if (newState == BluetoothProfile.STATE_CONNECTED){
                Log.d(TAG,"Device with ${gatt.device.address} is connected")
                bluetoothGatt = gatt
                bluetoothGatt?.discoverServices()
            }else if (newState == BluetoothProfile.STATE_DISCONNECTED){
                Log.d(TAG,"Device Disconnected")
            }
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
            Log.d(TAG,"Services Discovered")
            val serviceUUID = gatt.getService(SERVICE_UUID)
            val stepCharUuid = serviceUUID.getCharacteristic(STEPS_CHAR_UUID)
            val caloriesCharUuid = serviceUUID.getCharacteristic(CALORIES_CHAR_UUID)
            characteristicList.add(stepCharUuid)
            characteristicList.add(caloriesCharUuid)
            readDataFromDevice()
        }

        private fun readDataFromDevice() {
            bluetoothGatt?.readCharacteristic(characteristicList[characteristicList.size - 1])
        }

        override fun onCharacteristicRead(
            gatt: BluetoothGatt?,
            characteristic: BluetoothGattCharacteristic?,
            status: Int
        ) {
            if(status == BluetoothGatt.GATT_SUCCESS){
                when(characteristic?.uuid){
                    STEPS_CHAR_UUID -> {
                        Log.d(TAG, characteristic.value!!.toHexString())
                    }
                    CALORIES_CHAR_UUID -> {
                        Log.d(TAG, characteristic.value!!.toHexString())
                    }
                }
                characteristicList.removeAt(characteristicList.size - 1)
                if (characteristicList.size > 0){
                    readDataFromDevice()
                }else {
                    Log.d(TAG,"Successfully Read Data From Device")
                }
            }
        }
    }

    private fun promptBluetoothEnable() {
        val intent  = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
        startActivityForResult(intent, ENABLE_BLUETOOTH_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode){
            ENABLE_BLUETOOTH_REQUEST_CODE -> {
                if (resultCode == Activity.RESULT_OK){
                    if (!bluetoothAdapter.isEnabled){
                        binding.bluetoothConnected.visibility = View.VISIBLE
                    }else{
                        binding.bluetoothConnected.visibility = View.GONE
                    }
                }else if (resultCode == Activity.RESULT_CANCELED){
                    binding.bluetoothConnected.visibility = View.VISIBLE
                }
            }
        }
    }

    fun ByteArray.toHexString() = joinToString ("-"){ "%02x".format(it) }

    override fun onDestroy() {
        super.onDestroy()
        bluetoothGatt?.close()
        bluetoothGatt = null
    }

    companion object{
        private const val ENABLE_BLUETOOTH_REQUEST_CODE = 1
        private const val TAG = "MI_BAND"
    }
}