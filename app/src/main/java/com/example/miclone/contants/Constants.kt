package com.example.miclone.contants

import java.util.*

object Constants {
    const val MI_BAND_MAX_ADDRESS = "CF:56:28:CD:05:29"
    val SERVICE_UUID: UUID = UUID.fromString("0000FEE0-0000-1000-8000-00805F9B34FB")
    val BATTERY_CHAR_UUID: UUID = UUID.fromString("00000006-0000-3512-2118-0009AF100700")
    val CALORIES_CHAR_UUID: UUID = UUID.fromString("00000007-0000-3512-2118-0009AF100700")

    const val PREFERENCE_NAME = "step-preferences"
    const val PREFERENCE_STEP_GOAL = "step_goal"
    const val DEFAULT_STEP_GOAL = 1000
}