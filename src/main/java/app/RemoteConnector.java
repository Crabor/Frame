package app;

import app.struct.ActuatorInfo;
import app.struct.SensorInfo;
import common.struct.*;
import common.struct.enumeration.CmdType;
import common.struct.enumeration.SensorMode;
import common.struct.enumeration.ServiceType;

import java.util.Map;

public class RemoteConnector {
    //单例模式
    private static RemoteConnector instance;

    private RemoteConnector() {}

    public static RemoteConnector getInstance() {
        if (instance == null) {
            synchronized (RemoteConnector.class) {
                if (instance == null) {
                    instance = new RemoteConnector();
                }
            }
        }
        return instance;
    }

    public boolean connectPlatform(String ip, int port) {
        return false;
    }

    public boolean registerApp(AbstractApp app) {
        return false;
    }

    public boolean unregisterApp(AbstractApp app) {
        return false;
    }

    public boolean disConnectPlatform() {
        return false;
    }

    public boolean checkConnected() {
        return false;
    }

    public Map<String, SensorInfo> getSupportedSensors() {
        return null;
    }

    public Map<String, SensorInfo> getRegisteredSensors() {
        return null;
    }

    public boolean getRegisteredSensorsStatus() {
        return false;
    }

    public boolean registerSensor(String sensorName, SensorMode mode, int freq) {
        return false;
    }

    public boolean registerSensor(String sensorName, SensorMode mode) {
        return false;
    }

    public boolean registerSensor(String sensorName) {
        return false;
    }

    public boolean cancelSensor(String sensorName) {
        return false;
    }

    public boolean cancelAllSensors() {
        return false;
    }

    public SensorData getSensorData(String sensorName) {
        return null;
    }

    public Map<String, SensorData> getAllSensorData() {
        return null;
    }

    public Map<String, ActuatorInfo> getSupportedActuators() {
        return null;
    }

    public Map<String, ActuatorInfo> getRegisteredActuators(String AppName) {
        return null;
    }

    public boolean getRegisteredActuatorsStatus() {
        return false;
    }

    public boolean registerActuator(String actuatorName) {
        return false;
    }

    public boolean cancelActuator(String actuatorName) {
        return false;
    }

    public boolean cancelAllActuators() {
        return false;
    }

    public boolean setActuatorCmd(String actuatorName, String actuatorCmd, String ... args) {
        return false;
    }

    public boolean isServiceOn(ServiceType service) {
        return false;
    }

    public boolean serviceStart(ServiceType service, ServiceConfig config) {
        return false;
    }

    public boolean serviceStop(ServiceType service) {
        return false;
    }

    public boolean serviceCall(ServiceType service, CmdType cmd, ServiceConfig config) {
        return false;
    }


}
