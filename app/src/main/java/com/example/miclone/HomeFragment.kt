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
import com.example.miclone.Constants.MI_BAND_MAX_ADDRESS
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

        override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
            if (newState == BluetoothProfile.STATE_CONNECTED){
                Log.d(TAG,"Device with ${gatt.device.address} is connected")
            }else if (newState == BluetoothProfile.STATE_DISCONNECTED){
                Log.d(TAG,"Device Disconnected")
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