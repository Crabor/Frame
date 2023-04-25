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

### java版本wrapper

```java
import com.alibaba.fastjson.JSONObject;
import common.socket.CmdMessage;
import common.util.Util;
import java.io.IOException;

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

### c#版本wrapper

```c#
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Net.Sockets;
using System.Net;
using System.Threading;
using System.IO;
using Newtonsoft.Json;
using Newtonsoft.Json.Linq;

namespace WrapperDemo
{
    class Program
    {
        string recv(Socket socket) 
        {
            byte[] buffer = new byte[1024 * 1024 * 2];
            int r = socket.Receive(buffer);
            if (r == 0)
            {
                return null;
            }
            string data = Encoding.UTF8.GetString(buffer, 0, r);
            return data;
        }
        
        void send(Socket socket, string data)
        {
            socket.Send(Encoding.UTF8.GetBytes(data));
        }
        
        void task(string config)
        {
            //创建一个socket
            Socket socket = new Socket(AddressFamily.InterNetwork, SocketType.Stream, ProtocolType.Tcp);
            //创建一个ip地址对象
            IPAddress ip = IPAddress.Parse("127.0.0.1");
            //创建一个端口对象
            IPEndPoint point = new IPEndPoint(ip, 9091);
            //连接服务器
            socket.Connect(point);
            //发送数据
            socket.Send(Encoding.UTF8.GetBytes(config));
            //接收数据
            string register_back = recv(socket);
            if (register_back == null)
            {
                return;
            }
            JObject obj = (JObject)JsonConvert.DeserializeObject(register_back);
            if (obj["cmd"].ToString() != "register_back" || obj["message"].ToString() != "true")
            {
                return;
            }
            while (true)
            {
                string request = recv(socket);
                if (request == null)
                {
                    return;
                }
                obj = (JObject)JsonConvert.DeserializeObject(request);
                switch (obj["cmd"].ToString())
                {
                    case "sensory_request":
                        JObject value = new JObject();
                        value.Add("speed", 10.0);
                        value.Add("longitude", 20.0);
                        value.Add("latitude", 30.0);
                        JObject response = new JObject();
                        response.Add("cmd", "sensory_back");
                        response.Add("message", value.ToString());
                        send(socket, response.ToString());
                        break;
                    case "action_request":
                        JObject action_back = new JObject();
                        action_back.Add("cmd", "action_back");
                        action_back.Add("message", "true");
                        send(socket, action_back.ToString());
                        break;
                }
            }
        }
        
        static void Main(string[] args)
        {
            string config = File.ReadAllText("Resources/config/wrapper/config.json");
            Program program = new Program();
            Thread thread = new Thread(() => program.task(config));
            thread.Start();
        }
    }
}       
```

### unity版本wrapper

```c#
using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using System;
using System.IO;
using System.Net;
using System.Net.Sockets;
using System.Text;
using UnityEngine.SceneManagement;
using System.Threading;
using Newtonsoft.Json;
using Random = System.Random;
using Newtonsoft.Json.Linq;

public class cube_move : MonoBehaviour
{
    private string[] paths = {"Assets/Scripts/sensor_car.json", "Assets/Scripts/actor_car.json"};
    private string serverIP = "127.0.0.1";
    private int serverPort = 9091;
    private List<Thread> threads = new List<Thread>();

    private Mutex sensorMutex = new Mutex();
    private Sensor sensor = new Sensor();
    private Mutex actorMutex = new Mutex();
    private Actor actor = new Actor();
    private Color[] colors = new Color[4] { Color.yellow, Color.yellow, Color.yellow, Color.yellow };

    private bool noiseFlag = false;
    private Random random = new Random();
    private bool collisionFlag = false;

    public class Sensor
    {
        public Sensor()
        {
            front = 0;
            back = 0;
            left = 0;
            right = 0;
        }
        public float front;
        public float back;
        public float left;
        public float right;

        public float get(string name)
        {
            float ret = 0.0f;
            if (name == "front")
            {
                ret = front;
            }
            else if (name == "back")
            {
                ret = back;
            }
            else if (name == "left")
            {
                ret = left;
            }
            else if (name == "right")
            {
                ret = right;
            }
            return ret;
        }
    }

    public class Actor
    {
        public Actor()
        {
            xSpeed = 0;
            ySpeed = 0;
            zSpeed = 0;
        }
        public double xSpeed;
        public double ySpeed;
        public double zSpeed;

        public void set(string name, string value)
        {
            if (name == "xSpeed")
            {
                xSpeed = Double.Parse(value);
            }
            else if (name == "ySpeed")
            {
                ySpeed = Double.Parse(value);
            }
            else if (name == "zSpeed")
            {
                zSpeed = Double.Parse(value);
            }
        }
    }


    int ConvertToInt(double value)
    {
        int ret;
        if (value > 0)
        {
            ret = (int)Math.Ceiling(value);
        }
        else
        {
            ret = (int)Math.Floor(value);
        }
        return ret;
    }

    public class CmdMessage
    {
        public CmdMessage(string cmd, string message)
        {
            this.cmd = cmd;
            this.message = message == null ? null : message.Replace("\r\n", "").Replace("\n", "").Replace("\r", "");
        }
        public String cmd;
        public String message;
    }

    CmdMessage recv(StreamReader reader)
    {
        string response = reader.ReadLine();
        if (response == null)
        {
            return null;
        }
        CmdMessage ret = JsonConvert.DeserializeObject<CmdMessage>(response);
        if (ret.cmd != "alive_request")
        {
            Debug.Log("recv: " + response);
        }
        return ret;
    }

    void send(StreamWriter writer, CmdMessage data)
    {
        string str = JsonConvert.SerializeObject(data);
        Debug.Log("send: " + str);
        writer.WriteLine(str);
        writer.Flush();
    }

    void task(object obj)
    {
        if (obj != null)
        {
            string path = (string)obj;
            string config = File.ReadAllText(path);

            TcpClient client = new TcpClient(serverIP, serverPort);
            StreamWriter writer = new StreamWriter(client.GetStream());
            StreamReader reader = new StreamReader(client.GetStream());
            //发送数据
            CmdMessage register = new CmdMessage("register", config);
            //Debug.Log(register.ToString());
            send(writer, register);
            //接收数据
            CmdMessage register_back = recv(reader);
            if (register_back == null || register_back.cmd != "register_back" || register_back.message != "true")
            {
                return;
            }

            while (true)
            {
                CmdMessage request = recv(reader);
                if (request == null)
                {
                    return;
                }
                switch (request.cmd)
                {
                    case "sensory_request":
                        JObject value = new JObject();
                        sensorMutex.WaitOne();
                        value.Add("left", sensor.get("left").ToString());
                        value.Add("right", sensor.get("right").ToString());
                        value.Add("front", sensor.get("front").ToString());
                        value.Add("back", sensor.get("back").ToString());
                        sensorMutex.ReleaseMutex();
                        CmdMessage sensory_back = new CmdMessage("sensory_back", value.ToString());
                        send(writer, sensory_back);
                        break;
                    case "action_request":
                        string[] actions = request.message.Split(' ');
                        actorMutex.WaitOne();
                        actor.set(actions[0], actions[1]);
                        actorMutex.ReleaseMutex();
                        CmdMessage action_back = new CmdMessage("action_back", "true");
                        send(writer, action_back);
                        break;
                }
            }
        }
    }

    // Start is called before the first frame update
    void Start()
    {
        // 建立多个子线程
        for (int i = 0; i < paths.Length; i++)
        {
            Thread t = new Thread(new ParameterizedThreadStart(task));
            t.Start(paths[i]);
            threads.Add(t);
        }
    }

    // Update is called once per frame
    void Update()
    {
        if (!collisionFlag)
        {
            actorMutex.WaitOne();
            transform.Translate(Vector3.forward * ConvertToInt(actor.xSpeed) * Time.deltaTime);
            transform.Translate(Vector3.right * ConvertToInt(actor.ySpeed) * Time.deltaTime);
            transform.Rotate(Vector3.up * ConvertToInt(actor.zSpeed) * Time.deltaTime);
            actorMutex.ReleaseMutex();

            RaycastHit[] hits = new RaycastHit[4];
            Vector3[] directions = {
                transform.TransformDirection(Vector3.forward),
                transform.TransformDirection(Vector3.back),
                transform.TransformDirection(Vector3.left),
                transform.TransformDirection(Vector3.right)
            };
            for (int i = 0;i < directions.Length; i++){
                directions[i][1] = 0;
            }
            float[] distances = new float[4];
            for (int i = 0;i < directions.Length; i++){
                if (Physics.Raycast(transform.position, directions[i], out hits[i])){
                    Debug.DrawRay(transform.position, directions[i] * hits[i].distance, colors[i]); 
                    distances[i] = hits[i].distance;
                }else{
                     distances[i] = float.MaxValue;
                }
            }
            sensorMutex.WaitOne();
            sensor.front = distances[0];
            sensor.back = distances[1];
            sensor.left = noiseFlag ? (random.Next(1, 100) < 10 ? 200 * (random.Next(0, 2) == 0 ? -1 : 1) : distances[2]) : distances[2];
            sensor.right = noiseFlag ? (random.Next(1, 100) < 10 ? 200 * (random.Next(0, 2) == 0 ? -1 : 1) : distances[3]) : distances[3];
            sensorMutex.ReleaseMutex();
        }
    }

    void OnCollisionEnter(Collision collision)
    {
        collisionFlag = true;
    }

    void OnDestroy()
    {
        // 停止所有子线程
        foreach (Thread t in threads)
        {
            t.Abort();
        }
    }
}

```

### python版本wrapper

```python
import socket
import json
import threading
import time

def recv(socket):
    buffer = bytearray(1024)
    r = socket.recv_into(buffer)
    if r == 0:
        return None
    data = buffer[:r].decode("utf-8")
    return data
    
def send(socket, data):
    socket.send(data.encode("utf-8"))
    
def task(config):
    #创建一个socket
    socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    #创建一个ip地址对象
    ip = "127.0.0.1"
    #创建一个端口对象
    point = 9091
    #连接服务器
    socket.connect((ip, point))
    #发送数据
    socket.send(config.encode("utf-8"))
    #接收数据
    register_back = recv(socket)
    if register_back == None:
        return
    obj = json.loads(register_back)
    if obj["cmd"] != "register_back" or obj["message"] != "true":
        return
        
    while True:
        request = recv(socket)
        if request == None:
            return
        obj = json.loads(request)
        if obj["cmd"] == "sensory_request":
            value = {}
            value["speed"] = 10.0
            value["longitude"] = 20.0
            value["latitude"] = 30.0
            response = {}
            response["cmd"] = "sensory_back"
            response["message"] = json.dumps(value)
            send(socket, json.dumps(response))
        elif obj["cmd"] == "action_request":
            action_back = {}
            action_back["cmd"] = "action_back"
            action_back["message"] = "true"
            send(socket, json.dumps(action_back))
            
if __name__ == "__main__":
    config = open("config.json", "r").read()
    thread = threading.Thread(target=task, args=(config,))
    thread.start()
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



















