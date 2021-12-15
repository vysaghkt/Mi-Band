package com.example.miclone.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.miclone.R
import com.example.miclone.viewmodel.MainViewModel

class SettingsFragment : Fragment() {

    private lateinit var stepGoalEt: EditText
    private lateinit var updateBtn: Button

    private lateinit var mainViewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainViewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_settings, container, false)

        stepGoalEt = view.findViewById(R.id.stepGoalEditText)
        updateBtn = view.findViewById(R.id.updateBtn)

        mainViewModel.readStepGoal.observe(viewLifecycleOwner, {
            stepGoalEt.setText(it.toString())
        })

        stepGoalEt.setOnClickListener {
            updateBtn.isEnabled = true
        }

        updateBtn.setOnClickListener {
            mainViewModel.storeStepGoals(stepGoalEt.text.toString().toInt())
            updateBtn.isEnabled = false
        }

        return view
    }

}