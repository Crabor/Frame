### 平台部分细节

+ 一个app可以注册多个sensor，数据可以不同时到达，但是每一次传到平台上的数据应该包含注册的所有sensor，没有数据的地方用空串（""）表示.
+ filterMessage才可以根据sensor注册的情况过滤不需要的数据，pattern的matcher只能用于判断数据是否需要加入该pattern中被某一个rule检测

+ UI界面Statistics相关内容没有实现
+ fromPattern部分的change还没有实现
  {
  "appName": "platform.testTaxi.taxiAppTwo",
  "subscribe": [
  {
  "channel": "sensor",
  "groupId": 2,
  },
  ],
  },