# 上下文服务配置文件编写指南

上下文服务用于保障平台上层应用的高效可靠的环境感知，使用上下文服务需要提供5个配置文件，分别为`pattern文件`，`mfunc文件`，`rule文件`，`bfunc文件`，以及`rfunc文件`。
下面依次介绍如何编写上述5个文件。

## pattern文件

一个pattern是上层应用感兴趣的环境感知数据（称为环境上下文）的一个集合。pattern文件采用xml格式，其中对每一个pattern的描述包含四个部分

- `id`: 一个pattern唯一的标识。
- `freshness`：该pattern中环境上下文的有效期。
- `dataSource`：该pattern中的环境上下文从何处获取。
- `matcher(option)`：如何匹配dataSource中数据源的环境上下文。 

接下来依次介绍这四个部分。

### id

id是一个字符串，我们建议以`pat_`开头，以下是示例代码。

```xml
<id>pat_car</id>
```

### freshness

freshness包含`type`和`value`两部分。`type`共有两种，分别为`number`和`time`。`value`为一个长整型。

当`type`为`number`时，表示该pattern中最多包含`value`个环境上下文，其按照先进先出的规则维护。

当`type`为`time`时，表示该pattern中的环境上下文有效时间为`value`ms，超过有效时间后，对应的环境上下文即从pattern中删除。

以下是示例代码。

```xml
<!-- type 为 number -->
<freshness>
    <type>number</type>
    <value>2</value>
</freshness>
        
<!-- type 为 time -->
<freshness>
    <type>time</type>
    <value>2000</value>
</freshness>
```

### dataSource

dataSource包含`type`和`sourceList`两部分。`type`共有两种，分别为`sensor`和`pattern`。`sourceList`则是一个列表，里面每一项都是一个字符串，表示一个数据源。

当`type`为`sensor`时，表示环境上下文从`sourceList`中的传感设备来。

<span style="color:red">[暂未实现]</span> 当`type`为`pattern`时，表示环境上下文从`sourceList`中的pattern来，具体而言，是当`sourceList`中的pattern中的环境上下文超过有效期被删除后，这些环境上下文被该pattern捕获。

以下是示例代码。

```xml
<!-- type 为 sensor -->
<dataSource>
    <type>sensor</type>
    <sourceList>
        <source>sensorOne</source>
        <source>sensorTwo</source>
    </sourceList>
</dataSource>
        
<!-- type 为 pattern -->
<dataSource>
    <type>pattern</type>
    <sourceList>
        <source>pat_car</source>
    </sourceList>
</dataSource>
```

### matcher

matcher是一个可选项，当缺省时，表示dataSource中的数据源生成的所有环境上下文都会被该pattern捕获。当不缺省时，则表示只有被matcher匹配的dataSource中的数据源的环境上下文才会被该pattern捕获。

matcher分为`primaryKey` matcher和`function` matcher，下面分别介绍。

#### primaryKey matcher

`primarykey` matcher包含两个部分，分别为`primaryKey`和`optionalValueList`，其中`primaryKey`是环境上下文一个域名，`optionalValueList`是一个列表，其中每一项都是一个可选的值。

`primaryKey` matcher的语义为：对于任意一条来自dataSource中的数据源的环境上下文，如果该上下文的`primaryKey`指向的域的值存在于`optionalValueList`中，则认为匹配成功，该上下文被添加到pattern中，否则匹配失败，不添加到pattern中。

以下为示例代码。

```xml
<matcher>
    <type>primaryKey</type>
    <primaryKey>color</primaryKey>
    <optionalValueList>
        <value>orange</value>
        <value>black</value>
    </optionalValueList>
</matcher>
```

#### function matcher

`function` matcher包含两个部分，分别为`functionName`和`extraArgumentList`，其中`functionName`指明用哪个匹配函数进行匹配，`extraArgumentList`是一个列表，其中每一项都是`functionName`指向的函数可能用到的参数。

`function` matcher的语义为：对于任意一条来自dataSource中的数据源的环境上下文，将该上下文以及`extraArgumentList`作为参数传给`functionName`指明的函数，如果该函数返回true，则认为匹配成功，该上下文被添加到pattern中，否则匹配失败，不添加到pattern中。

<span style="color:blue">[提醒]</span> `functionName`指明的函数通过`mfunc文件`提供，在后面会详细介绍。

以下为示例代码。

```xml
 <matcher> 
    <type>function</type>
    <functionName>filter</functionName>
    <extraArgumentList>
        <argument>argOne</argument>
        <argument>argTwo</argument>
    </extraArgumentList>
</matcher>
```

### 完整示例

假设有如下场景：现有若干机器人在两个仓库x,y之间移动，每个仓库中都存在一个传感器(sensor_x和sensor_y)能够感知当前在仓库中所有机器人。
每个机器人都有其唯一的名字(name)，我们开发的上层应用关注这两个仓库中名字以'A'起始的机器人，那么我们可以写出如下pattern文件。

```xml
<?xml version="1.0"?>

<patterns>
    
    <pattern>
        <id>pat_x</id>
        <freshness>
            <type>number</type>
            <value>2</value>
        </freshness>
        <dataSource>
            <type>sensor</type>
            <sourceList>
                <source>sensor_x</source>
            </sourceList>
        </dataSource>
        <matcher>
            <type>function</type>
            <functionName>startWith</functionName>
            <extraArgumentList>
                <argument>A</argument>
            </extraArgumentList>
        </matcher>
    </pattern>

    <pattern>
        <id>pat_y</id>
        <freshness>
            <type>number</type>
            <value>2</value>
        </freshness>
        <dataSource>
            <type>sensor</type>
            <sourceList>
                <source>sensor_y</source>
            </sourceList>
        </dataSource>
        <matcher>
            <type>function</type>
            <functionName>startWith</functionName>
            <extraArgumentList>
                <argument>A</argument>
            </extraArgumentList>
        </matcher>
    </pattern>
    
</patterns>

```

## mfunc文件

mfunc文件是一个java文件，它实现了pattern中function matcher中调用的匹配函数。

<span style="color:blue">[提醒]</span> mfunc文件将会被平台自动编译成class文件然后动态加载到平台中，请保证mfunc文件的文件名为`mfuncs.java`。

在上述介绍pattern的完整示例中，`pat_x`和`pat_y`都使用了function matcher来调用startWith这个匹配函数，在此给出具体的实现。

```java
import java.util.Map;
import java.util.List;

public class mfuncs {

    // entry
    public boolean mfunc(final String funcName, final Map<String, String> ctxFields, final List<String> extraArgumentList) throws Exception {
        if ("startWith".equals(funcName)) {
            return startWith(ctxFields, extraArgumentList);
        }
        else{
            throw new Exception("Illegal bfuncName");
        }
    }

    
    private boolean startWith(final Map<String, String> ctxFields, final List<String> extraArgumentList){
        String name = ctxFields.get("name");
        return name.startsWith("A");
    }
}
```

如上述代码所示，一个mfunc文件包括一个平台调用的入口函数（`public boolean mfunc(...)`）以及在入口函数中被调用的具体匹配函数的实现（`private boolean startWith(...)`）。

其中入口函数有三个参数，其中`final String funcName`对应function matcher中的functionName，`final List<String> extraArgumentList`对应function matcher中的extraArgumentList，`final Map<String, String> ctxFields`则对应一条环境上下文，它的域名和对应的数值以key-value的形式存放于这个Map中。

具体的匹配函数有两个参数，含义和入口函数中相同。

## rule文件

一条rule是上层应用期望环境上下文所满足的某种约束。rule文件采用xml的格式。每一条rule包含三个部分：

- `id`: 一条rule的唯一标识。
- `formula`: 以基于一阶逻辑的约束语言写成的规则语义。
- `resolver`：如何处理违反这条规则的环境上下文。

接下来依次介绍这三部分。

### id

id是一个字符串，我们建议以`rule_`开头，以下是示例代码。

```xml
<id>rule_1</id>
```

### formula

一条formula以基于一阶逻辑的约束语言写成，其包含`forall`,`exists`,`and`,`or`,`implies`,`not`,`bfunction`七种公式，他们的语法如下

```xml
<!-- forall formula -->
<forall var = "v1" in = "pat_001">
    <!-- subformula -->
</forall>

<!-- exists formula -->
<exists var = "v1" in = "pat_001">
    <!-- subformula -->
</exists>

<!-- and formula -->
<and>
    <!-- subformula 1 -->
    <!-- subformula 2 -->
</and>

<!-- or formula -->
<or>
    <!-- subformula 1 -->
    <!-- subformula 2 -->
</or>

<!-- implies formula -->
<implies>
    <!-- subformula 1 -->
    <!-- subformula 2 -->
</implies>

<!-- not formula -->
<not>
    <!-- subformula -->
</not>

<!-- bfunction formula -->
<bfunction name="funcName">
    <param pos="1" var="v1"/>
    ....
    <param pos="n" var="vn"/>
</bfunction>
```

其中每一个`forall`和`exists`公式都关联一个pattern，并用var来指代pattern中的一个环境上下文。`bfunction`是终端公式（没有子公式），其用name指明所对应的函数，然后用param标签来指明该函数的参数由哪些var构成。

同样以机器人在仓库中移动的场景为例，我们可以很直观地有"一个机器人不可能同时出现在两个仓库中"这样的物理规律，于是我们可以写出如下规则语义：

```xml
<formula>
    <forall var = "v1" in = "pat_x">
        <not>
            <exists var="v2" in="pat_y">
                <bfunction name="Same">
                    <param pos="1" var="v1"/>
                    <param pos="2" var="v2"/>
                </bfunction>
            </exists>
        </not>
    </forall>
</formula>
```

<span style="color:blue">[提醒]</span> `bfunction`标签中`name`指明的函数通过`bfunc文件`提供，在后面会详细介绍。

### resolver

一条rule的resolver分为两个部分，分别为`strategy`，`priorityList`。

#### strategy

strategy共有三种，分别为`drop-latest`, `drop-all`和`customized`。

- `drop-latest`表示丢弃违背该规则的最近的一条环境上下文。
- <span style="color:red">[暂未实现]</span> `drop-all`表示丢弃违背该规则的所有环境上下文。
- <span style="color:red">[暂未实现]</span> `customized`表示用户自定义处理，在这种策略下，上下文服务将会调用用户提供的处理函数。

以下为示例代码

```xml
<strategy>drop-latest</strategy>

<strategy>drop-all</strategy>

<strategy funcName="myResolver">customized</strategy>
```

#### priorityList

有的时候可能同时有多个规则被违反，而这些规则间处理违反规则上下文的策略可能会相互冲突，因此，上层应用的开发者需要指定每个resolver的优先级。

首先，开发者需要给所有规则分组（组号是一个自然数），分组的标准为不同组的规则在同时被违反时处理策略不会相互冲突，而同组的规则在同时被违反时，处理策略可能存在冲突。

然后，开发者需要给定每一个规则在它所在的分组中的优先级（优先级是一个自然数，数字越小，优先级越高），每个分组只会选取优先级最高的resolver来处理违反规则的上下文。

对于一条规则而言，它可以不止属于一个分组，因此`priorityList`是一个列表，其中每一项都包含两部分，分别为`group`和`priority`，用来说明该规则在`group`组中的`priority`是多少。

以下为示例代码

```xml
<priorityList>
    <priorityItem>
        <group>0</group>
        <priority>0</priority>
    </priorityItem>
    <priorityItem>
        <group>1</group>
        <priority>0</priority>
    </priorityItem>
</priorityList>
```

#### 完整示例

所以综合起来，我们的rule文件内容可以为

```xml
<?xml version="1.0"?>

<rules>

    <rule>
        <id>rule_1</id>
        <formula>
            <forall var = "v1" in = "pat_x">
                <not>
                    <exists var="v2" in="pat_y">
                        <bfunction name="Same">
                            <param pos="1" var="v1"/>
                            <param pos="2" var="v2"/>
                        </bfunction>
                    </exists>
                </not>
            </forall>
        </formula>
        <resovler>
            <strategy>drop-latest</strategy>
            <priorityList>
                <priorityItem>
                    <group>0</group>
                    <priority>0</priority>
                </priorityItem>
                <priorityItem>
                    <group>1</group>
                    <priority>0</priority>
                </priorityItem>
            </priorityList>
        </resovler>
    </rule>
    
</rules>
```

## bfunc文件

bfunc文件是一个java文件，它实现了rule中bfunction公式中调用的函数。

<span style="color:blue">[提醒]</span> bfunc文件将会被平台自动编译成class文件然后动态加载到平台中，请保证bfunc文件的文件名为`bfuncs.java`。

在上述介绍rule的完整示例中，rule_1中调用Same这个函数，在此给出具体的实现。

```java
import java.util.Map;

public class bfuncs {

    //entry
    public boolean bfunc(final String funcName, final Map<String, Map<String, String>> vcMap) throws Exception {
        if("Same".equals(funcName)){
            return Same(vcMap);
        }
        else{
            throw new Exception("Illegal bfuncName");
        }
    }
    
    private boolean Same(final Map<String, Map<String, String>> vcMap){
        String robotName1 = vcMap.get("v1").get("name");
        String robotName2 = vcMap.get("v2").get("name");
        return robotName1.equals(robotName2);
    }
}
```

如上述代码所示，一个bfunc文件包括一个平台调用的入口函数（`public boolean bfunc(...)`）以及在入口函数中被调用的具体函数的实现（`private boolean Same(...)`）。

其中入口函数有两个参数，其中`final String funcName`对应bfunction中的name，`final Map<String, Map<String, String>> vcMap`则对应bfunction中各个param及param所指向的上下文。

Same函数中`final Map<String, Map<String, String>> vcMap`参数和上述一致。


## <span style="color:red">[暂未实现]</span> rfunc文件 

