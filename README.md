# platform

## 我们能做什么

我们主要提供两种服务：上下文（context）服务以及不变式（invariant）服务。上下文服务用于过滤自身带噪点的数据，不变式服务用于在任务循环中记录变量trace生成不变式以及不变式相关服务。

## 快速开始

按顺序执行下面步骤：

```txt
1. 编写wrapper
2. 编写app
3. 编写平台配置文件
4. 依次启动平台->wrapper->app
```

### 1.编写wrapper

wrapper是一个外部程序，用于与平台通信，包含Sensor或者Actor，用以和平台进行通信。下面是一个简单的Sensor wrapper示例：

```java
public class WrapperDemo {
    public static void main(String[] args) throws IOException {
        String config = Util.readFileContent("Resources/config/wrapper/config.json");
        WrapperRemoteConnector connector = WrapperRemoteConnector.getInstance();
        if (connector.register("127.0.0.1", 9091, config)) {
            while (true) {
                CmdMessage msg = connector.recv();
                switch (msg.cmd) {
                    case "sensory_request":
                        JSONObject value = new JSONObject();
                        value.put("speed", 10.0);
                        value.put("longitude", 20.0);
                        value.put("latitude", 30.0);
                        CmdMessage response = new CmdMessage("sensory_back", value.toJSONString());
                        connector.send(response.toString());
                        break;
                }
            }
//            connector.close();
        }
    }
}
```

由于平台启动时不知道任何wrapper信息，所以需要用户编写wrapper程序并向平台动态register。

### 2.编写app

这里的app指用户程序，用户向平台动态注册自己的程序，获取平台的硬件资源和服务资源，用来服务自己的自适应程序。用户需要自己继承我们的抽象app类（AbstractSyncApp）来实现自己的应用，继承模板如下：

```java
public class AppDemo extends AbstractApp {
    //注册sensor或者启动不变式服务、上下文服务时，平台会通过getMsg函数将数据传递给app
    @Override
    public void getMsg(String sensorName, SensorData value) {
        logger.info(String.format("[%s]: getMsg(channel, msg) -> %s, %s", appName, sensorName, value));
    }

    //配置app的名称和描述
    @Override
    public void configApp() {
        this.appName = "Demo";
        this.appDescription = "This is Demo";
    }

    //下面这个示例是一个简单的sensor订阅示例，订阅了YellowCar这个sensor，每隔1s平台就会通过getMsg函数将sensor数据传递给app
    public static void main(String[] args) throws InterruptedException {
        AppDemo app = new AppDemo();
        AppRemoteConnector connector = AppRemoteConnector.getInstance();
        connector.connectPlatform("127.0.0.1", 9090);
        connector.registerApp(app);
        connector.checkConnected();

        Map<String, SensorInfo> supportedSensors = connector.getSupportedSensors();
        if (supportedSensors.containsKey("YellowCar")) {
            connector.registerSensor("YellowCar", SensorMode.PASSIVE, 1);
        }
        connector.getMsgThread(CmdType.START);
        while (true);
//        connector.unregisterApp(app);
//        connector.disConnectPlatform();
    }
}
```

### 3.编写平台配置文件

平台需要指定默认全局的上下文服务配置以及监听app和wrapper的端口。

```json
{ //上下文服务全局配置
  "CtxServerConfiguration": {
    "serverOn": true,
    "CtxValidator": "ECC+IMD",
    "baseRuleFile": "Resources/config/platform/ctx/platform/platformRules.xml",
    "baseBfuncFile": "Resources/config/platform/ctx/platform/platformBfunction.class",
    "basePatternFile": "Resources/config/platform/ctx/platform/platformPatterns.xml",
    "baseMfuncFile": ""
  },
  //平台监听端口配置
  "TCPConfig": {
    "appListenPort": 9090,
    "deviceListenPort": 9091
  }
}
```

### 4.依次启动平台->wrapper->app

首先用户需要启动平台程序，位于“src/main/java/platform/Platform.java”；然后启动wrapper程序，向平台注册sensor或者actor资源；最后启动app程序，向平台注册app，订阅sensor或者启动不变式服务、上下文服务并获取数据。

## context

## invariant

## log system

## resource manage

平台和外部程序（wrapper）通信采用TCP明文协议。明文协议格式为：

```txt
平台发送协议格式：
{"cmd": "XXX", "message": "XXX"}
```

其中平台接收的协议格式中**cmd**、**message**项需与发送协议格式中的保持一致。

### 明文协议

| direction | cmd             | message                                                              |
|-----------|-----------------|----------------------------------------------------------------------|
| W -> P    | register        | DeviceConf的json串                                                     |
| P -> W    | register_back   | true 或者 false                                                        |
| P -> W    | sensory_request | null                                                                 |
| W -> P    | sensory_back    | {"default":"100"}//单域传感器<br/>或者{"speed":100,"longitude":22.3}//多域传感器 |
| P -> W    | action_request  | {...}//用户自定义                                                         |
| W -> P    | action_back     | true 或者 false                                                        |
| W -> P    | shutdown        | null                                                                 |

#### SensorConf

```json
{
  "name":  "YellowCar",
  "type":  "Sensor",
  "fields": ["speed", "longitude", "latitude"]
}
```

#### ActorConf

```json
{
  "name":  "YellowCar",
  "type":  "Actor"
}
```

## app

app与平台通信为tcp通信，具体支持的api及对应的明文协议如下：

### app与平台通信明文协议

| 类名              | 编程API                                                                              | App -> Platform                                                                                 | Platform -> App                                                                    |
|-----------------|------------------------------------------------------------------------------------|-------------------------------------------------------------------------------------------------|------------------------------------------------------------------------------------|
| RemoteConnector | public boolean connectPlatform(String ip, int port);                               | {"api":"connect"}                                                                               | {"state":true/false}                                                               |
| RemoteConnector | public boolean disConnectPlatform();                                               | {"api":"disconnect"}                                                                            | {"state":true/false}                                                               |
| RemoteConnector | public boolean checkConnected();                                                   | {"api":"is_connected"}                                                                          | {"state":true/false}                                                               |
| RemoteConnector | public boolean registerApp(AbstractApp app);                                       | {"api":"register_app","app_name":"xxxx"}                                                        | {"state":true/false,"udp_port":xxxx}                                               |
| RemoteConnector | public boolean unregisterApp(AbstractApp app);                                     | {"api":"unregister_app","app_name":"xxxx"}                                                      | {"state":true/false}                                                               |
| RemoteConnector | public Map<String, SensorInfo> getSupportedSensors();                              | {"api":"get_supported_sensors"}                                                                 | [{"sensor_name":"xxxx","state":"on/off","value_type":"xxxx"},...]                  |
| RemoteConnector | public Map<String, SensorInfo> getRegisteredSensors();                             | {"api":"get_registered_sensors"}                                                                | [{"sensor_name":"xxxx","state":"on/off","value_type":"xxxx"},...]                  |
| RemoteConnector | public boolean getRegisteredSensorsStatus();                                       | {"api":"get_registered_sensors_status"}                                                         | {"state":true/false}                                                               |
| RemoteConnector | public boolean registerSensor(String sensorName, SensorMode mode, int freq);       | {"api":"register_sensor","sensor_name":"xxxx","mode":"xxxx","freq":xxxx}                        | {"state":true/false}                                                               |
| RemoteConnector | public boolean cancelSensor(String sensorName);                                    | {"api":"cancel_sensor","sensor_name":"xxxx"}                                                    | {"state":true/false}                                                               |
| RemoteConnector | public boolean cancelAllSensors();                                                 | {"api":"cancel_all_sensors"}                                                                    | {"state":true/false}                                                               |
| RemoteConnector | public String getSensorData(String sensorName);                                    | {"api":"get_sensor_data","sensor_name":"xxxx"}                                                  | {"default":"value"} / {"field1":"value1",...}                                      |
| RemoteConnector | public Map<String, String> getAllSensorData();                                     | {"api":"get_all_sensor_data"}                                                                   | [{"sensor_name":"xxxx","value":{"default":"value"} / {"field1":"value1",...}},...] |
| RemoteConnector | public boolean getMsgThread(CmdType cmd);                                          | {"api":"get_msg_thread","cmd":"xxxx"}                                                           | {"state":true/false}                                                               |
| RemoteConnector | public Map<String, ActorInfo> getSupportedActors();                                | {"api":"get_supported_actors"}                                                                  | [{"actor_name":"xxxx","state":"on/off","value_type":"xxxx"},...]                   |
| RemoteConnector | public Map<String, ActorInfo> getRegisteredActors();                               | {"api":"get_registered_actors"}                                                                 | [{"actor_name":"xxxx","state":"on/off","value_type":"xxxx"},...]                   |
| RemoteConnector | public boolean getRegisteredActorsStatus();                                        | {"api":"get_registered_actors_status"}                                                          | {"state":true/false}                                                               |
| RemoteConnector | public boolean registerActor(String actorName);                                    | {"api":"register_actor","actor_name":"xxxx"}                                                    | {"state":true/false}                                                               |
| RemoteConnector | public boolean cancelActor(String actorName);                                      | {"api":"cancel_actor","actor_name":"xxxx"}                                                      | {"state":true/false}                                                               |
| RemoteConnector | public boolean cancelAllActors();                                                  | {"api":"cancel_all_actors"}                                                                     | {"state":true/false}                                                               |
| RemoteConnector | public boolean setActorCmd(String actorName, String action)                        | {"api":"set_actor_cmd","actor_name":"xxxx","action":"xxxx"}                                     | {"state":true/false}                                                               |
| RemoteConnector | public boolean isServiceOn(ServiceType service);                                   | {"api":"is_service_on","service_type":"xxxx"}                                                   | {"state":true/false}                                                               |
| RemoteConnector | public boolean serviceStart(ServiceType service, ServiceConfig config);            | {"api":"start_service","service_type":"xxxx","config":<config>}                                 | {"state":true/false}                                                               |
| RemoteConnector | public boolean serviceStop(ServiceType service);                                   | {"api":"stop_service","service_type":"xxxx"}                                                    | {"state":true/false}                                                               |
| RemoteConnector | public String serviceCall(ServiceType service, CmdType cmd, ServiceConfig config); | {"api":"service_call","service_type":"xxxx","cmd_type":"xxxx","config":<config>}                | {"state":true/false}                                                               |
| RemoteConnector | public boolean setRuleFile(String ruleFile);                                       | {"api":"set_rule_file","file_name":"xxxx","content":"xxxx"}                                     | {"state":true/false}                                                               |
| RemoteConnector | public boolean setPatternFile(String patternFile);                                 | {"api":"set_pattern_file","file_name":"xxxx","content":"xxxx"}                                  | {"state":true/false}                                                               |
| RemoteConnector | public boolean setBfuncFile(String bfuncFile);                                     | {"api":"set_bfunc_file","file_name":"xxxx","content":"xxxx"}                                    | {"state":true/false}                                                               |
| RemoteConnector | public boolean setMfuncFile(String mfuncFile);                                     | {"api":"set_mfunc_file","file_name":"xxxx","content":"xxxx"}                                    | {"state":true/false}                                                               |
| RemoteConnector | public boolean setRfuncFile(String rfuncFile);                                     | {"api":"set_rfunc_file","file_name":"xxxx","content":"xxxx"}                                    | {"state":true/false}                                                               |
| RemoteConnector | public boolean setCtxValidator(String ctxValidator);                               | {"api":"set_ctx_validator","ctx_validator":"xxxx"}                                              | {"state":true/false}                                                               |
| InvCheck        | public boolean monitor(Object... objs);                                            | {"api":"inv_monitor","objs":["XXXX",...]}                                                       | {"state":true/false}                                                               |
| InvCheck        | public boolean isMonitored(Object... objs);                                        | {"api":"inv_is_monitored","objs":["XXXX",...]}                                                  | {"state":true/false}                                                               |
| InvCheck        | public boolean check(Object... objs);                                              | {"api":"inv_check","objs":{"XXXX":XXXX,...},"line_number":XXXX,"check_time":XXXX,"iterId":XXXX} | {"state":true/false}                                                               |

#### <config>

CtxServiceConfig

```json
{
  "rule_file_content": "xxxx",
  "pattern_file_content": "xxxx",
  "bfunc_file_content": "xxxx",
  "mfunc_file_content": "xxxx",
  "rfunc_file_content": "xxxx",
  "ctx_validator": "xxxx"
}
```

InvServiceConfig

```json
{
  "init_thro": XXXX,
  "gen_thro": XXXX
}
```



















