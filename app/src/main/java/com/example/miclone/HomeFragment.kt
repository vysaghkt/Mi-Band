package com.example.miclone

import android.app.Activity
import android.app.AlertDialog
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.miclone.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding

    private val bluetoothAdapter:BluetoothAdapter by lazy {
        val bluetoothManager = requireContext().getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothManager.adapter
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(layoutInflater)

        if (!bluetoothAdapter.isEnabled){
            promptBluetoothEnable()
        }

        return binding.root
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

    companion object{
        private const val ENABLE_BLUETOOTH_REQUEST_CODE = 1
    }
}