package com.android.car.automotiveapp.core.data.datasource

import android.content.Context
import com.android.car.automotiveapp.core.common.AppLog
import com.android.car.automotiveapp.core.model.AmbientColor
import com.android.car.automotiveapp.core.model.CaliperColor
import com.android.car.automotiveapp.core.model.CarColor
import com.android.car.automotiveapp.core.model.CarConfiguration
import com.android.car.automotiveapp.core.model.HeadlightMode
import com.android.car.automotiveapp.core.model.MassageMode
import com.android.car.automotiveapp.core.model.PaintFinish
import com.android.car.automotiveapp.core.model.SeatComfortState
import com.android.car.automotiveapp.core.model.TirePressureState
import com.android.car.automotiveapp.core.model.WheelRimStyle
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FileCarPreferencesDataSource @Inject constructor(
    @param:ApplicationContext private val context: Context
) : CarPreferencesDataSource {

    private val fileName = "car_configuration.json"
    private val configFile: File
        get() = File(context.filesDir, fileName)

    override suspend fun loadConfiguration(): CarConfiguration = withContext(Dispatchers.IO) {
        try {
            if (!configFile.exists()) {
                return@withContext CarConfiguration()
            }
            val jsonString = configFile.readText()
            if (jsonString.isBlank()) {
                return@withContext CarConfiguration()
            }
            parseJsonToConfig(JSONObject(jsonString))
        } catch (e: Exception) {
            AppLog.e("FileCarPreferencesDataSource", "Failed to load saved car configuration, fallback to default", e)
            CarConfiguration()
        }
    }

    override suspend fun saveConfiguration(config: CarConfiguration) = withContext(Dispatchers.IO) {
        try {
            val json = configToJson(config)
            configFile.writeText(json.toString(2))
        } catch (e: Exception) {
            AppLog.e("FileCarPreferencesDataSource", "Failed to persist car configuration", e)
        }
    }

    override suspend fun clearConfiguration() = withContext(Dispatchers.IO) {
        try {
            if (configFile.exists()) {
                configFile.delete()
            }
        } catch (e: Exception) {
            AppLog.e("FileCarPreferencesDataSource", "Failed to clear car configuration", e)
        }
    }

    private fun configToJson(config: CarConfiguration): JSONObject {
        val root = JSONObject()
        root.put("bodyColor", config.bodyColor.name)
        root.put("paintFinish", config.paintFinish.name)
        root.put("wheelRimStyle", config.wheelRimStyle.name)
        root.put("caliperColor", config.caliperColor.name)
        root.put("windowTintPercent", config.windowTintPercent)
        root.put("windowRollDownPercent", config.windowRollDownPercent.toDouble())
        root.put("frontDefroster", config.frontDefroster)
        root.put("rearDefroster", config.rearDefroster)
        root.put("sunroofOpenPercent", config.sunroofOpenPercent.toDouble())
        root.put("headlightMode", config.headlightMode.name)
        root.put("drlEnabled", config.drlEnabled)
        root.put("fogLightsEnabled", config.fogLightsEnabled)
        root.put("hazardLightsEnabled", config.hazardLightsEnabled)
        root.put("ambientColor", config.ambientColor.name)
        root.put("ambientBrightness", config.ambientBrightness.toDouble())
        root.put("mirrorsFolded", config.mirrorsFolded)
        root.put("mirrorPitch", config.mirrorPitch.toDouble())
        root.put("mirrorYaw", config.mirrorYaw.toDouble())
        root.put("mirrorHeating", config.mirrorHeating)
        root.put("blindSpotMonitoring", config.blindSpotMonitoring)
        root.put("trunkOpenPercent", config.trunkOpenPercent.toDouble())
        root.put("trunkMaxHeightPercent", config.trunkMaxHeightPercent)
        root.put("cargoLightEnabled", config.cargoLightEnabled)
        root.put("kickSensorEnabled", config.kickSensorEnabled)
        root.put("driverDoorOpenPercent", config.driverDoorOpenPercent.toDouble())
        root.put("doorsLocked", config.doorsLocked)
        root.put("childSafetyLock", config.childSafetyLock)
        root.put("nightMode", config.nightMode)
        root.put("xrayMode", config.xrayMode)
        root.put("autoRotate", config.autoRotate)
        root.put("rotationSpeed", config.rotationSpeed.toDouble())

        // Driver Seat JSON
        val seatObj = JSONObject()
        seatObj.put("heatingLevel", config.driverSeat.heatingLevel)
        seatObj.put("coolingLevel", config.driverSeat.coolingLevel)
        seatObj.put("reclineDegrees", config.driverSeat.reclineDegrees.toDouble())
        seatObj.put("slidePositionPercent", config.driverSeat.slidePositionPercent.toDouble())
        seatObj.put("heightPercent", config.driverSeat.heightPercent.toDouble())
        seatObj.put("lumbarSupportPercent", config.driverSeat.lumbarSupportPercent.toDouble())
        seatObj.put("massageMode", config.driverSeat.massageMode.name)
        root.put("driverSeat", seatObj)

        // Tire Pressure JSON
        val tireObj = JSONObject()
        tireObj.put("frontLeftPsi", config.tirePressure.frontLeftPsi.toDouble())
        tireObj.put("frontRightPsi", config.tirePressure.frontRightPsi.toDouble())
        tireObj.put("rearLeftPsi", config.tirePressure.rearLeftPsi.toDouble())
        tireObj.put("rearRightPsi", config.tirePressure.rearRightPsi.toDouble())
        tireObj.put("temperatureCelsius", config.tirePressure.temperatureCelsius.toDouble())
        root.put("tirePressure", tireObj)

        // Telemetry JSON
        val telObj = JSONObject()
        telObj.put("batteryPercent", config.telemetry.batteryPercent)
        telObj.put("estimatedRangeKm", config.telemetry.estimatedRangeKm)
        telObj.put("speedKmh", config.telemetry.speedKmh.toDouble())
        telObj.put("powerOutputKw", config.telemetry.powerOutputKw.toDouble())
        telObj.put("batteryTemperatureCelsius", config.telemetry.batteryTemperatureCelsius.toDouble())
        telObj.put("isCharging", config.telemetry.isCharging)
        telObj.put("odometerKm", config.telemetry.odometerKm)
        root.put("telemetry", telObj)

        return root
    }

    private fun parseJsonToConfig(json: JSONObject): CarConfiguration {
        val def = CarConfiguration()

        val seatObj = json.optJSONObject("driverSeat")
        val seat = if (seatObj != null) {
            SeatComfortState(
                heatingLevel = seatObj.optInt("heatingLevel", def.driverSeat.heatingLevel),
                coolingLevel = seatObj.optInt("coolingLevel", def.driverSeat.coolingLevel),
                reclineDegrees = seatObj.optDouble("reclineDegrees", def.driverSeat.reclineDegrees.toDouble()).toFloat(),
                slidePositionPercent = seatObj.optDouble("slidePositionPercent", def.driverSeat.slidePositionPercent.toDouble()).toFloat(),
                heightPercent = seatObj.optDouble("heightPercent", def.driverSeat.heightPercent.toDouble()).toFloat(),
                lumbarSupportPercent = seatObj.optDouble("lumbarSupportPercent", def.driverSeat.lumbarSupportPercent.toDouble()).toFloat(),
                massageMode = parseEnumOrDefault(seatObj.optString("massageMode"), def.driverSeat.massageMode)
            )
        } else def.driverSeat

        val tireObj = json.optJSONObject("tirePressure")
        val tire = if (tireObj != null) {
            TirePressureState(
                frontLeftPsi = tireObj.optDouble("frontLeftPsi", def.tirePressure.frontLeftPsi.toDouble()).toFloat(),
                frontRightPsi = tireObj.optDouble("frontRightPsi", def.tirePressure.frontRightPsi.toDouble()).toFloat(),
                rearLeftPsi = tireObj.optDouble("rearLeftPsi", def.tirePressure.rearLeftPsi.toDouble()).toFloat(),
                rearRightPsi = tireObj.optDouble("rearRightPsi", def.tirePressure.rearRightPsi.toDouble()).toFloat(),
                temperatureCelsius = tireObj.optDouble("temperatureCelsius", def.tirePressure.temperatureCelsius.toDouble()).toFloat()
            )
        } else def.tirePressure

        val telObj = json.optJSONObject("telemetry")
        val tel = if (telObj != null) {
            com.android.car.automotiveapp.core.model.VehicleTelemetry(
                batteryPercent = telObj.optInt("batteryPercent", def.telemetry.batteryPercent),
                estimatedRangeKm = telObj.optInt("estimatedRangeKm", def.telemetry.estimatedRangeKm),
                speedKmh = telObj.optDouble("speedKmh", def.telemetry.speedKmh.toDouble()).toFloat(),
                powerOutputKw = telObj.optDouble("powerOutputKw", def.telemetry.powerOutputKw.toDouble()).toFloat(),
                batteryTemperatureCelsius = telObj.optDouble("batteryTemperatureCelsius", def.telemetry.batteryTemperatureCelsius.toDouble()).toFloat(),
                isCharging = telObj.optBoolean("isCharging", def.telemetry.isCharging),
                odometerKm = telObj.optLong("odometerKm", def.telemetry.odometerKm)
            )
        } else def.telemetry

        return CarConfiguration(
            bodyColor = parseEnumOrDefault(json.optString("bodyColor"), def.bodyColor),
            paintFinish = parseEnumOrDefault(json.optString("paintFinish"), def.paintFinish),
            wheelRimStyle = parseEnumOrDefault(json.optString("wheelRimStyle"), def.wheelRimStyle),
            caliperColor = parseEnumOrDefault(json.optString("caliperColor"), def.caliperColor),
            windowTintPercent = json.optInt("windowTintPercent", def.windowTintPercent),
            windowRollDownPercent = json.optDouble("windowRollDownPercent", def.windowRollDownPercent.toDouble()).toFloat(),
            frontDefroster = json.optBoolean("frontDefroster", def.frontDefroster),
            rearDefroster = json.optBoolean("rearDefroster", def.rearDefroster),
            sunroofOpenPercent = json.optDouble("sunroofOpenPercent", def.sunroofOpenPercent.toDouble()).toFloat(),
            headlightMode = parseEnumOrDefault(json.optString("headlightMode"), def.headlightMode),
            drlEnabled = json.optBoolean("drlEnabled", def.drlEnabled),
            fogLightsEnabled = json.optBoolean("fogLightsEnabled", def.fogLightsEnabled),
            hazardLightsEnabled = json.optBoolean("hazardLightsEnabled", def.hazardLightsEnabled),
            ambientColor = parseEnumOrDefault(json.optString("ambientColor"), def.ambientColor),
            ambientBrightness = json.optDouble("ambientBrightness", def.ambientBrightness.toDouble()).toFloat(),
            mirrorsFolded = json.optBoolean("mirrorsFolded", def.mirrorsFolded),
            mirrorPitch = json.optDouble("mirrorPitch", def.mirrorPitch.toDouble()).toFloat(),
            mirrorYaw = json.optDouble("mirrorYaw", def.mirrorYaw.toDouble()).toFloat(),
            mirrorHeating = json.optBoolean("mirrorHeating", def.mirrorHeating),
            blindSpotMonitoring = json.optBoolean("blindSpotMonitoring", def.blindSpotMonitoring),
            trunkOpenPercent = json.optDouble("trunkOpenPercent", def.trunkOpenPercent.toDouble()).toFloat(),
            trunkMaxHeightPercent = json.optInt("trunkMaxHeightPercent", def.trunkMaxHeightPercent),
            cargoLightEnabled = json.optBoolean("cargoLightEnabled", def.cargoLightEnabled),
            kickSensorEnabled = json.optBoolean("kickSensorEnabled", def.kickSensorEnabled),
            driverDoorOpenPercent = json.optDouble("driverDoorOpenPercent", def.driverDoorOpenPercent.toDouble()).toFloat(),
            doorsLocked = json.optBoolean("doorsLocked", def.doorsLocked),
            childSafetyLock = json.optBoolean("childSafetyLock", def.childSafetyLock),
            driverSeat = seat,
            tirePressure = tire,
            nightMode = json.optBoolean("nightMode", def.nightMode),
            xrayMode = json.optBoolean("xrayMode", def.xrayMode),
            autoRotate = json.optBoolean("autoRotate", def.autoRotate),
            rotationSpeed = json.optDouble("rotationSpeed", def.rotationSpeed.toDouble()).toFloat(),
            telemetry = tel
        )
    }

    private inline fun <reified T : Enum<T>> parseEnumOrDefault(name: String?, default: T): T {
        if (name.isNullOrBlank()) return default
        return try {
            java.lang.Enum.valueOf(T::class.java, name)
        } catch (e: Exception) {
            default
        }
    }
}
