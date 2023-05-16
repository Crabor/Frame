# UI

## 引言

本文档将介绍平台的UI模块，包括UI是如何工作的，如何配置UI，以及UI的API列表等等。本文档涉及到的基本术语定义如下:

* `组件`: 例如`Button`、`Label`、`Image`等等。
* `页面`: 容纳组件的角色。一个页面可以包含多个组件，但是一个组件只能属于一个页面。页面可以理解为一个容器，用于容纳组件。
* `格栅布局`: 页面的布局方式。格栅布局将页面划分为若干行和若干列形成格栅形状，组件可以放置在格栅的任意位置。
* `Window`: 窗口组件，是页面的一种。窗口是最基础的组件，默认在右上角包含了最小化、最大化、关闭三个按钮，它只能作为页面去容纳其他组件，但是不能作为组件放在其他页面之上。
* `Panel`: 面板组件，是页面的一种。面板既可以作为组件分布在其他页面之上，也可以本身作为页面容纳其他组件。
* `xxx.uc`: UI配置文件（UI configuration）。格式为json，指明了UI的一些基本配置，例如数据库端口号、ul文件路径、up文件路径等等。
* `xxx.ul`: 页面布局文件（UI layout）。格式为json，包含了一个json array，array中每一项都是一个页面的布局信息。
* `xxx.ua`: 组件属性文件（UI attribute）。格式为json，包含了一个json array，array中每一项都是一个组件的属性配置信息。用户可以对组件的属性值进行静态设置，或者对属性进行动态的数据绑定，以及设定组件的监听事件和动作。
* `数据绑定`: 指组件的属性值（例如Label组件的text属性）可以和数据库中的数据源进行绑定。组件会根据用户设定的频率去查询数据库，然后将数据库中的数据更新到组件的属性值中。
* `事件监听`: 指组件可以监听的事件，例如`Button`的点击事件等等。
* `动作`: 指当监听的事件发生时执行的动作，例如`Button`的点击动作发生后，可以执行一个动作，例如弹出一个对话框等等。

下面我将从运行步骤、xxx.uc、xxx.ul、xxx.ua、常见问题五个方面来介绍UI模块。

## 运行步骤

在使用UI模块之前，需要先准备好以下几个文件:

* xxx.uc
* xxx.ul
* xxx.ua

然后就是确保运行UI前，数据库已经启动了。数据库的启动方式请参考[启动数据库服务器](database.md#启动数据库服务器)。

然后就是在UI.java的main函数中调用UI.Start()的地方传入xxx.uc的路径，例如:

```java
UI.Start("Resources/config/ui/default.uc");
```

最后运行UI.java的main函数即可。

## xxx.uc

### 介绍

uc文件是UI的配置文件(UI configuration)，它的格式为json，包含了以下几个字段:

* `databse_name`: 数据库名称。如未指定，则默认为`test`。
* `database_port`: 数据库端口号。如未指定，则默认为`9092`。
* `layout_file`: 页面布局文件的路径。
* `attribute_file`: 组件属性文件的路径。
* `theme`: UI的主题。如未指定，则默认为`MistSilver`。详见[外观主题](#外观主题themetype)。
* `grid_visible`: 是否让UI的格栅布局可视化。如未指定，则默认为`false`。
    
  不可视化(`false`):

  ![](img/Snipaste_2023-05-16_01-53-37.png)

  可视化(`true`):

  ![](img/Snipaste_2023-05-16_01-58-23.png)

### 示例

这是一个uc文件的示例:

```json
{
    "database_name": "test",
    "database_port": 9092,
    "layout_file": "Resources/config/ui/default.ul",
    "attribute_file": "Resources/config/ui/default.ua",
    "theme": "MistSilver",
    "grid_visible": true
}
```

## xxx.ul

### 介绍

ul文件是UI的页面布局文件(UI layout)，它的格式为json，包含了一个json array，array中每一项都是一个页面的布局信息。每一个页面的布局信息包含了以下几个字段:

* `type`: 页面的类型，有两种类型，一种是`Window`，一种是`Panel`。
* `id`: 页面的id，用于唯一标识一个页面。
* `size`: 页面的栅格大小，格式为`[width, height]`。

  例如`[3, 2]`表示页面的宽度为3，高度为2，即形成了2行3列的格栅:

    ![](img/Snipaste_2023-05-16_01-56-26.png)

* `components`: 页面包含的所有组件。这是一个json array，array的每一项表示一个组件。每一个组件包含了以下几个字段:
  * `type`: 组件的类型，例如`Button`、`Label`、`Image`等等。详见[组件类型](#组件类型componenttype)。
  * `id`: 组件的id，用于唯一标识一个组件。
  * `position`: 组件的位置，格式为`[x, y, w, h]`。`x`和`y`表示组件的格栅坐标，`w`和`h`表示组件占据的格栅宽度和高度。
    > 注意在前端的坐标系中，原点在页面的左上角，x轴向右，y轴向下。

    例如`[0, 1, 2, 1]`表示组件位于第2行第1列的格栅中，并且宽度占据2个格栅，高度占据1个格栅:

    ![](img/Snipaste_2023-05-16_01-58-23.png)

  * `align`: 组件的对齐方式，例如`Center`、`North`、`NorthWest`等等。 详见[对齐方式](#对齐方式aligntype)。

    例如`North`表示组件向上对齐:

    ![](img/Snipaste_2023-05-16_01-59-15.png)

> 注意！ul文件第一个页面type必须是Window且id必须是main，这是UI的主窗口。

### 示例

这是一个ul文件的示例:

```json
[
  {
    "type": "Window",
    "id": "main",
    "size": [3, 2],
    "components": [
      {
        "type": "Label",
        "id": "label1",
        "position": [0, 1, 2, 1],
        "align": "North"
      },
      {
        "type": "Panel",
        "id": "panel1",
        "position": [2, 0, 1, 2]
      }
    ]
  },
  {
    "type": "Panel",
    "id": "panel1",
    "size": [3, 5]
  }
]
```

它的效果如下:

![](img/Snipaste_2023-05-16_01-55-36.png)

## xxx.ua

### 介绍

ua文件是UI的组件属性文件(UI attribute)，它的格式为json，包含了一个json array，array中每一项都是一个组件的属性设置。我们可以对组件进行静态设置、数据绑定、监听事件等等。

#### 属性

每一个组件的属性设置包含了以下三个部分:

* `基础属性`(这部分的属性必须存在):
  * `type`: 组件类型，例如`Button`、`Label`、`Image`等等。详见[组件类型](#组件类型componenttype)。
  * `id`: 组件的id，用于唯一标识一个组件。
* `一般属性`(这部分的属性可以不存在)。例如`Button`组件可以设置的一般属性有`background`、`visible`、`font`、`text`。详情见[组件支持属性列表](#组件支持属性列表)。
* `监听属性`(这部分的属性可以不存在): 指的是组件的监听事件以及对应的触发动作。该属性是一个json array，array的每一项都是一个监听事件，具体包含以下字段:
  * `type`: 监听事件的类型，例如`MouseClick`。详见[监听事件类型](#监听事件类型listenertype)。
  * `freq`: 当`type` 为`Timer`时才需设定，该属性表示定时器的触发频率，单位为hz。
  * `action`: 监听事件触发时的动作。该属性是一个json object:
    * `type`: 动作的类型，例如`LayoutChange`。详见[动作类型](#动作执行actiontype)。
    * `其他字段`: `action`的其他字段会根据`type`的不同而不同。详见[动作类型](#动作执行actiontype)。

#### 变量

在ua文件中，我们可以使用变量来表示一些值。变量的格式为`${变量名}`，变量有两种类型:`简单变量`和`属性变量`。变量的使用可以使得UI的展现效果更加灵活。

* `简单变量`:
  * `${systime}`: 当前系统时间，格式为`HH:MM:SS`。
  * `${systime_ms}`: 当前系统时间，格式为`HH:MM:SS:ms`。
  * `${type}`: 当前组件的类型，如`Button`、`Label`等。详见[组件类型](#组件类型componenttype)。
  * `${id}`: 当前组件的id。
* `属性变量`: 完整的格式为`${[type.id.]attribute[.i][.j]}`。
  * `[type.id.]`: 这部分内容是可选的，如果存在，则表示其他组件，如果不存在，这表示当前组件。例如`${background}`表示当前组件的`background`属性值，`${Window.main.title}`表示id为`main`的`Window`组件的`title`属性值。
  * `attribute`: 属性名，例如`background`、`visible`、`font`、`text`等等。详见[组件支持属性列表](#组件支持属性列表)。
  * `[.i][.j]`: 这部分内容是可选的，`[.i]`表示属性值的第一维索引，`[.j]`表示属性值的第二维索引。之所以存在这部分的原因是属性分为`Single`、`List`、`Matrix`三种类型。例如`text`属于`Single`属性，所以不需要索引；而`position`属于`List`属性（`[x, y, w, h]`），所以需要第一维索引`[.i]`，例如`${position.0}`表示当前组件的`position`属性的第一个值`x`；而`content`属于`Matrix`属性，所以`[.i]`和`[.j]`都需要，例如`${Table.table1.content.1.0}`表示`id`为`table1`的`Table`组件的`content`属性的第`2`行第`1`列的值。

### 示例

这是一个ua文件的示例:

```json
[
  {
    "type": "Window",
    "id": "main",
    "title": "Platform",
    "size": [400, 300]
  },
  {
    "type": "Button",
    "id": "button1",
    "text": "${systime}",
    "listeners": [
      {
        "type": "MouseClick",
        "action": {
          "type": "LayoutChange",
          "layout_type": "Window",
          "layout_id": "main",
          "component_type": "${type}",
          "component_id": "${id}",
          "position": ["(${position.0} + 1) % 5", 0, 1, 1]
        }
      },
      {
        "type": "Timer",
        "freq": 1,
        "action": {
          "type": "AttributeChange",
          "component_type": "${type}",
          "component_id": "${id}",
          "attribute": "text",
          "value": "${systime}"
        }
      }
    ]
  }
]
```

以及它对应的ul文件:

```json
[
  {
    "type": "Window",
    "id": "main",
    "size": [5, 1],
    "components": [
      {
        "type": "Button",
        "id": "button1",
        "position": [0, 0, 1, 1]
      }
    ]
  }
]
```

它的效果如下:

![](img/ua_demo.gif)

## 数据结构

### 组件类型(`ComponentType`)

* `Window`: 窗口。
* `Panel`: 面板。
* `Label`: 标签。
* `TextField`: 文本框。
* `Button`: 按钮。
* `CheckBox`: 复选框。
* `ComboBox`: 下拉框。
* `Table`: 表格。
* `List`: 列表。
* `Tree`: 树。
* `BarChart`: 柱状图。
* `PieChart`: 饼状图。
* `LineChart`: 折线图。
* `Image`: 图片。

### 组件属性(`AttributeType`)

#### Single属性

* `scroll`: 是否支持[滚动](#滚动类型scrolltype)。如`Vertical`表示垂直方向支持滚动。
* `background`: 背景颜色。支持常见颜色单词(如`Yellow`)或者RGB值(如`#FFFFFF`表示`White`)。
* `visible`: 组件可见性。`true`或者`false`。
* `title`: 标题。
* `text`: 文本内容。
* `column_width`: 列宽度。
* `row_height`: 行高度。
* `editable`: 是否可编辑。`true`或者`false`。

#### List属性

* `size`: 列表大小，格式为`[width, height]`。
* `font`: 字体。格式为`[font_name, font_style, font_size]`。`font_name`见[字体样式](#字体样式),`font_style`见[字体风格](#字体风格fontstyletype)
* `column_names`: 列名。格式为`[column_name1, column_name2, ...]`。
* `dirs`: 目录。格式为`[dir1, dir2, ...]`。
* `position`: 位置。格式为`[x, y, w, h]`。`x`和`y`表示组件的坐标,`w`和`h`表示组件的宽度和高度。详细介绍请看[xxx.ul](#xxxul)章节

#### Matrix属性

* `content`: 内容。格式为`[[row1_col1, row1_col2, ...], [row2_col1, row2_col2, ...], ...]`。其中`rowi_colj`表示第`i`行第`j`列的内容。

#### 组件支持属性列表

* `Window`:
  * `background`
  * `visible`
  * `title`
  * `size`
  * `scroll`
* `Panel`:
  * `background`
  * `visible`
  * `scroll`
* `Label`: 
  * `background`
  * `visible`
  * `font`
  * `text`
* `TextField`:
  * `background`
  * `visible`
  * `font`
  * `text`
  * `editable`
  * `column_width`
  * `row_height`
* `Button`:
  * `background`
  * `visible`
  * `font`
  * `text`
* `CheckBox`:
* `ComboBox`:
* `Table`:
  * `background`
  * `visible`
  * `font`
  * `column_names`
  * `content`: 大小为`[m][n]`,其中`n`等于`column_names`的长度。
  * `scroll`
* `List`:
* `Tree`:
  * `background`
  * `visible`
  * `font`
  * `dirs`
  * `content`: 大小为`[m][n]`,其中`m`等于`dirs`的长度。
* `BarChart`:
* `PieChart`:
* `LineChart`:
* `Image`:

### 对齐方式(`AlignType`)

* `Center`: 居中对齐。
* `North`: 上对齐。
* `NorthWest`: 左上对齐。
* `East`: 右对齐。
* `SouthEast`: 右下对齐。
* `South`: 下对齐。
* `SouthWest`: 左下对齐。
* `West`: 左对齐
* `NorthWest`: 右上对齐。

### 滚动类型(`ScrollType`)

* `Vertical`: 垂直滚动。
* `Horizontal`: 水平滚动。
* `Both`: 垂直和水平滚动。
* `None`: 不支持滚动。

### 监听事件类型(`ListenerType`)

* `MouseClick`: 鼠标单击事件。
* `MouseDoubleClick`: 鼠标双击事件。
* `MouseHover`: 鼠标悬停事件。
* `MouseDrag`: 鼠标拖拽事件。
* `MouseScroll`: 鼠标滚动事件。
* `MouseMove`: 鼠标移动事件。
* `MousePress`: 鼠标按下事件。
* `MouseRelease`: 鼠标释放事件。
* `MouseEnter`: 鼠标进入事件。
* `MouseLeave`: 鼠标离开事件。
* `MouseDragEnter`: 鼠标拖拽进入事件。
* `MouseDragLeave`: 鼠标拖拽离开事件。
* `MouseDragDrop`: 鼠标拖拽放下事件。
* `KeyboardKeyDown`: 键盘按下事件。
* `KeyboardKeyUp`: 键盘释放事件。
* `KeyboardKeyPress`: 键盘按键事件。
* `FocusIn`: 获得焦点事件。
* `FocusOut`: 失去焦点事件。
* `TextInput`: 文本输入事件。
* `PasswordInput`: 密码输入事件。
* `WindowOpen`: 窗口打开事件。
* `WindowClose`: 窗口关闭事件。
* `WindowMinimize`: 窗口最小化事件。
* `WindowMaximize`: 窗口最大化事件。
* `WindowRestore`: 窗口还原事件。
* `StateChange`: 状态改变事件。
* `StateSelect`: 状态选择事件。
* `StateUpdate`: 状态更新事件。
* `TouchStart`: 触摸开始事件。
* `TouchMove`: 触摸移动事件。
* `TouchEnd`: 触摸结束事件。
* `TouchCancel`: 触摸取消事件。
* `Timer`: 定时器事件。(须同时设置`freq`，表明定时器触发频率)

### 动作执行(`ActionType`)

* `DatabaseSet`: 往数据库写数据。
  * `type`: `DatabaseSet`.
  * `sql`: 执行的具体`插入`、`更新`、`删除`sql语句。

这是一个示例配置，表示每隔一秒向`time`表中插入当前系统时间。

```json
{
  "type": "DatabaseSet",
  "sql": "INSERT INTO time values (${systime})"
}
```

* `DatabaseGet`: 从数据库读数据并赋值给组件的某个属性。
  * `type`: `DatabaseGet`.
  * `component_type`: 组件类型。
  * `component_id`: 组件id。
  * `component_attribute`: 组件属性。组件属性分为`Single`、`List`、`Matrix`三种，详见[组件属性](#组件属性attributetype)。
  * `sql`: 执行的具体`查询`sql语句。
  > 注意数据库查询的结果到最终赋值到不同属性值上是按不同规则进行的!
  * 如果是`Single`属性，则会获取`sql`执行结果（表格）的第一行第一列赋值给属性。
  * 如果是`List`属性，则会获取`sql`执行结果（表格）的第一列赋值给属性。
  * 如果是`Matrix`属性，则会获取`sql`执行结果（表格）的所有行以及`特定列`给属性。
    * `Table.content`: 对于`Table`的`content`属性，UI会按照`column_names`的顺序，将`sql`执行结果（表格）的`对应列`赋值给`content`。例如`column_names`为`["id", "name"]`,而`sql`为`SELECT * FROM student`获取的结果如下：
    
      | id | name | score |
      |----|------|-------|
      | 1  | 张三   | 100   |
      | 2  | 李四   | 90    |
      | 3  | 王五   | 80    |
          
      则最终`content`的值为：
    
      | id | name |
      |----|------|
      | 1  | 张三   |
      | 2  | 李四   |
      | 3  | 王五   |
    
    * `Tree.content`: 对于`Tree`的`content`属性，UI会按照`dirs`的顺序，将`sql`执行结果（表格）的`对应行`赋值给`content`。例如`dirs`为`["App", "Resource", "Service"]`,而`sql`为`SELECT * FROM platform`获取的结果如下： 
    
      > 注意`sql`执行结果（表格）必须有一个列的列名为`dir`，另一个列的列名为`file`。

      | id |   dir    | file      | description       |
      |---:|:--------:|:----------|:------------------|
      |  1 |   App    | app1      | this is app1      |
      |  2 |   App    | app2      | this is app2      |
      |  3 |   App    | app3      | this is app3      |
      |  4 | Resource | resource1 | this is resource1 |
      |  5 | Resource | resource2 | this is resource2 |
      |  6 | Resource | resource3 | this is resource3 |
      |  7 | Service  | service1  | this is service1  |
      |  8 | Service  | service2  | this is service2  |
      |  9 | Service  | service3  | this is service3  |
    
      则最终`Tree.content`的值为：

      ```text
      ├── App
      │   ├── app1
      │   ├── app2
      │   └── app3
      ├── Resource
      │   ├── resource1
      │   ├── resource2
      │   └── resource3
      └── Service
          ├── service1
          ├── service2
          └── service3      
      ```

这是一个示例配置，表示每隔一秒从数据库中读取`AppTable`的数据赋给`id`为`AppTable`的`Table`组件的`content`属性。

```json
{
    "type": "Table",
    "id": "AppTable",
    "column_names": ["id", "ResourceName", "Description", "App"],
    "listeners": [
        {
            "type": "Timer",
            "freq": 1,
            "action": {
                "type": "DatabaseGet",
                "component_type": "Table",
                "component_id": "AppTable",
                "component_attribute": "content",
                "sql": "SELECT * FROM AppTable"
            }
        }
    ]
},
```

* `LayoutChange`: 布局更改。
  * `type`: `LayoutChange`.
  * `layout_type`: 页面类型。
  * `layout_id`: 页面id。
  * `component_type`: 组件类型。
  * `component_id`: 组件id。
  * `position`: 让组件在页面上按照`position`重新布局。格式为`[x, y, w, h]`。

这是一个示例配置，表示让当前组件重新布局在`id`为`main`的`Window`页面上，位置为`[1, 0, 1, 1]`:

```json
{
    "type": "LayoutChange",
    "layout_type": "Window",
    "layout_id": "main",
    "component_type": "${type}",
    "component_id": "${id}",
    "position": [1, 0, 1, 1]
}
```

### 字体风格(`FontStyleType`)

* `PLAIN`: 无风格。
* `BOLD`: 加粗。
* `ITALIC`: 斜体。
* `BOLD_ITALIC`: 同时加粗和斜体。

### 字体样式

* `Arial`
* `Arial Black`
* `Arial Narrow`
* `Arial Rounded MT Bold`
* `Bahnschrift`
* `Baskerville Old Face`
* `Bauhaus 93`
* `Bell MT`
* `Berlin Sans FB`
* `Berlin Sans FB Demi`
* `Bernard MT Condensed`
* `Blackadder ITC`
* `Bodoni MT`
* `Bodoni MT Black`
* `Bodoni MT Condensed`
* `Bodoni MT Poster Compressed`
* `Book Antiqua`
* `Bookman Old Style`
* `Bookshelf Symbol 7`
* `Bradley Hand ITC`
* `Britannic Bold`
* `Broadway`
* `Brush Script MT`
* `Calibri`
* `Calibri Light`
* `Californian FB`
* `Calisto MT`
* `Cambria`
* `Cambria Math`
* `Candara`
* `Candara Light`
* `Cascadia Code`
* `Cascadia Mono`
* `Castellar`
* `Centaur`
* `Century`
* `Century Gothic`
* `Century Schoolbook`
* `Chiller`
* `Colonna MT`
* `Comic Sans MS`
* `Consolas`
* `Constantia`
* `Cooper Black`
* `Copperplate Gothic Bold`
* `Copperplate Gothic Light`
* `Corbel`
* `Corbel Light`
* `Courier New`
* `Curlz MT`
* `DejaVu Math TeX Gyre`
* `DejaVu Sans Mono`
* `Dialog`
* `DialogInput`
* `Dubai`
* `Dubai Light`
* `Dubai Medium`
* `Ebrima`
* `Edwardian Script ITC`
* `Elephant`
* `Engravers MT`
* `Eras Bold ITC`
* `Eras Demi ITC`
* `Eras Light ITC`
* `Eras Medium ITC`
* `Felix Titling`
* `Footlight MT Light`
* `Forte`
* `Franklin Gothic Book`
* `Franklin Gothic Demi`
* `Franklin Gothic Demi Cond`
* `Franklin Gothic Heavy`
* `Franklin Gothic Medium`
* `Franklin Gothic Medium Cond`
* `Freestyle Script`
* `French Script MT`
* `Gabriola`
* `Gadugi`
* `Garamond`
* `Georgia`
* `Gigi`
* `Gill Sans MT`
* `Gill Sans MT Condensed`
* `Gill Sans MT Ext Condensed Bold`
* `Gill Sans Ultra Bold`
* `Gill Sans Ultra Bold Condensed`
* `Gloucester MT Extra Condensed`
* `Goudy Old Style`
* `Goudy Stout`
* `Haettenschweiler`
* `Harlow Solid Italic`
* `Harrington`
* `High Tower Text`
* `HoloLens MDL2 Assets`
* `Impact`
* `Imprint MT Shadow`
* `Informal Roman`
* `Ink Free`
* `Javanese Text`
* `Jokerman`
* `Juice ITC`
* `Kristen ITC`
* `Kunstler Script`
* `Leelawadee`
* `Leelawadee UI`
* `Leelawadee UI Semilight`
* `Lucida Bright`
* `Lucida Calligraphy`
* `Lucida Console`
* `Lucida Fax`
* `Lucida Handwriting`
* `Lucida Sans`
* `Lucida Sans Typewriter`
* `Lucida Sans Unicode`
* `Magneto`
* `Maiandra GD`
* `Malgun Gothic`
* `Malgun Gothic Semilight`
* `Marlett`
* `Matura MT Script Capitals`
* `Microsoft Himalaya`
* `Microsoft JhengHei UI`
* `Microsoft JhengHei UI Light`
* `Microsoft New Tai Lue`
* `Microsoft PhagsPa`
* `Microsoft Sans Serif`
* `Microsoft Tai Le`
* `Microsoft Uighur`
* `Microsoft YaHei UI`
* `Microsoft YaHei UI Light`
* `Microsoft Yi Baiti`
* `Mistral`
* `Modern No. 20`
* `Mongolian Baiti`
* `Monospaced`
* `Monotype Corsiva`
* `MS Gothic`
* `MS Outlook`
* `MS PGothic`
* `MS Reference Sans Serif`
* `MS Reference Specialty`
* `MS UI Gothic`
* `MT Extra`
* `MV Boli`
* `Myanmar Text`
* `Niagara Engraved`
* `Niagara Solid`
* `Nirmala UI`
* `Nirmala UI Semilight`
* `OCR A Extended`
* `Old English Text MT`
* `Onyx`
* `Palace Script MT`
* `Palatino Linotype`
* `Papyrus`
* `Parchment`
* `Perpetua`
* `Perpetua Titling MT`
* `Playbill`
* `Poor Richard`
* `Pristina`
* `Rage Italic`
* `Ravie`
* `Rockwell`
* `Rockwell Condensed`
* `Rockwell Extra Bold`
* `Sans Serif Collection`
* `SansSerif`
* `Script MT Bold`
* `Segoe Fluent Icons`
* `Segoe MDL2 Assets`
* `Segoe Print`
* `Segoe Script`
* `Segoe UI`
* `Segoe UI Black`
* `Segoe UI Emoji`
* `Segoe UI Historic`
* `Segoe UI Light`
* `Segoe UI Semibold`
* `Segoe UI Semilight`
* `Segoe UI Symbol`
* `Segoe UI Variable`
* `Serif`
* `Showcard Gothic`
* `SimSun-ExtB`
* `Sitka Text`
* `Snap ITC`
* `Stencil`
* `Sylfaen`
* `Symbol`
* `Tahoma`
* `Tempus Sans ITC`
* `Times New Roman`
* `Trebuchet MS`
* `Tw Cen MT`
* `Tw Cen MT Condensed`
* `Tw Cen MT Condensed Extra Bold`
* `Verdana`
* `Viner Hand ITC`
* `Vivaldi`
* `Vladimir Script`
* `Webdings`
* `Wide Latin`
* `Wingdings`
* `Wingdings 2`
* `Wingdings 3`
* `Yu Gothic`
* `Yu Gothic Light`
* `Yu Gothic Medium`
* `Yu Gothic UI`
* `Yu Gothic UI Light`
* `Yu Gothic UI Semibold`
* `Yu Gothic UI Semilight`
* `仿宋`
* `华文中宋`
* `华文仿宋`
* `华文宋体`
* `华文彩云`
* `华文新魏`
* `华文楷体`
* `华文琥珀`
* `华文细黑`
* `华文行楷`
* `华文隶书`
* `宋体`
* `幼圆`
* `微軟正黑體`
* `微軟正黑體 Light`
* `微软雅黑`
* `微软雅黑 Light`
* `新宋体`
* `新細明體`
* `新細明體-ExtB`
* `方正姚体`
* `方正舒体`
* `楷体`
* `標楷體`
* `等线`
* `等线 Light`
* `細明體`
* `細明體-ExtB`
* `細明體_HKSCS`
* `細明體_HKSCS-ExtB`
* `隶书`
* `黑体`

### 外观主题(`ThemeType`)

#### 亮色主题

* `Business`:
  <div style="display: flex;">
      <img src="img/business1.png" alt="Image 1" width="40%" />
      <img src="img/business2.png" alt="Image 2" width="40%" />
  </div>
* `BusinessBlueSteel`:

  <div style="display: flex;">
      <img src="img/businessbluesteel1.png" alt="Image 1" width="40%" />
      <img src="img/businessbluesteel2.png" alt="Image 2" width="40%" />
  </div>

* `BusinessBlackSteel`:

  <div style="display: flex;">
      <img src="img/businessblacksteel1.png" alt="Image 1" width="40%" />
      <img src="img/businessblacksteel2.png" alt="Image 2" width="40%" />
  </div>

* `Creme`:

  <div style="display: flex;">
      <img src="img/creme1.png" alt="Image 1" width="40%" />
      <img src="img/creme2.png" alt="Image 2" width="40%" />
  </div>

* `CremeCoffee`:

  <div style="display: flex;">
      <img src="img/cremecoffee1.png" alt="Image 1" width="40%" />
      <img src="img/cremecoffee2.png" alt="Image 2" width="40%" />
  </div>

* `Sahara`:

  <div style="display: flex;">
      <img src="img/sahara1.png" alt="Image 1" width="40%" />
      <img src="img/sahara2.png" alt="Image 2" width="40%" />
  </div>

* `Moderate`:

  <div style="display: flex;">
      <img src="img/moderate1.png" alt="Image 1" width="40%" />
      <img src="img/moderate2.png" alt="Image 2" width="40%" />
  </div>

* `Nebula`:

  <div style="display: flex;">
      <img src="img/nebula1.png" alt="Image 1" width="40%" />
      <img src="img/nebula2.png" alt="Image 2" width="40%" />
  </div>

* `NebulaAmethyst`:

  <div style="display: flex;">
      <img src="img/nebulaamethyst1.png" alt="Image 1" width="40%" />
      <img src="img/nebulaamethyst2.png" alt="Image 2" width="40%" />
  </div>

* `NebulaBrickWall`:

  <div style="display: flex;">
      <img src="img/nebulabrickwall1.png" alt="Image 1" width="40%" />
      <img src="img/nebulabrickwall2.png" alt="Image 2" width="40%" />
  </div>

* `Autumn`:

  <div style="display: flex;">
      <img src="img/autumn1.png" alt="Image 1" width="40%" />
      <img src="img/autumn2.png" alt="Image 2" width="40%" />
  </div>

* `MistSilver`:

  <div style="display: flex;">
      <img src="img/mistsilver1.png" alt="Image 1" width="40%" />
      <img src="img/mistsilver2.png" alt="Image 2" width="40%" />
  </div>

* `MistAqua`:

  <div style="display: flex;">
      <img src="img/mistaqua1.png" alt="Image 1" width="40%" />
      <img src="img/mistaqua2.png" alt="Image 2" width="40%" />
  </div>

* `Dust`:

  <div style="display: flex;">
      <img src="img/dust1.png" alt="Image 1" width="40%" />
      <img src="img/dust2.png" alt="Image 2" width="40%" />
  </div>

* `DustCoffee`:

  <div style="display: flex;">
      <img src="img/dustcoffee1.png" alt="Image 1" width="40%" />
      <img src="img/dustcoffee2.png" alt="Image 2" width="40%" />
  </div>

* `Gemini`:

  <div style="display: flex;">
      <img src="img/gemini1.png" alt="Image 1" width="40%" />
      <img src="img/gemini2.png" alt="Image 2" width="40%" />
  </div>

* `Mariner`:

  <div style="display: flex;">
      <img src="img/mariner1.png" alt="Image 1" width="40%" />
      <img src="img/mariner2.png" alt="Image 2" width="40%" />
  </div>

* `Sentinel`:

  <div style="display: flex;">
      <img src="img/sentinel1.png" alt="Image 1" width="40%" />
      <img src="img/sentinel2.png" alt="Image 2" width="40%" />
  </div>

* `Cerulean`:

  <div style="display: flex;">
      <img src="img/cerulean1.png" alt="Image 1" width="40%" />
      <img src="img/cerulean2.png" alt="Image 2" width="40%" />
  </div>

* `GreenMagic`:

  <div style="display: flex;">
      <img src="img/greenmagic1.png" alt="Image 1" width="40%" />
      <img src="img/greenmagic2.png" alt="Image 2" width="40%" />
  </div>

#### 暗色主题

* `Twilight`:

  <div style="display: flex;">
      <img src="img/twilight1.png" alt="Image 1" width="40%" />
      <img src="img/twilight2.png" alt="Image 2" width="40%" />
  </div>

* `NightShade`:

  <div style="display: flex;">
      <img src="img/nightshade1.png" alt="Image 1" width="40%" />
      <img src="img/nightshade2.png" alt="Image 2" width="40%" />
  </div>

* `Magellan`:

  <div style="display: flex;">
      <img src="img/magellan1.png" alt="Image 1" width="40%" />
      <img src="img/magellan2.png" alt="Image 2" width="40%" />
  </div>

* `Graphite`:

  <div style="display: flex;">
      <img src="img/graphite1.png" alt="Image 1" width="40%" />
      <img src="img/graphite2.png" alt="Image 2" width="40%" />
  </div>

* `GraphiteChalk`:
  
  <div style="display: flex;">
      <img src="img/graphitechalk1.png" alt="Image 1" width="40%" />
      <img src="img/graphitechalk2.png" alt="Image 2" width="40%" />
  </div>

* `GraphiteAqua`:

  <div style="display: flex;">
      <img src="img/graphiteaqua1.png" alt="Image 1" width="40%" />
      <img src="img/graphiteaqua2.png" alt="Image 2" width="40%" />
  </div>

* `GraphiteElectric`:

  <div style="display: flex;">
      <img src="img/graphiteelectric1.png" alt="Image 1" width="40%" />
      <img src="img/graphiteelectric2.png" alt="Image 2" width="40%" />
  </div>

* `GraphiteGold`:

  <div style="display: flex;">
      <img src="img/graphitegold1.png" alt="Image 1" width="40%" />
      <img src="img/graphitegold2.png" alt="Image 2" width="40%" />
  </div>

* `GraphiteSienna`:

  <div style="display: flex;">
      <img src="img/graphitesienna1.png" alt="Image 1" width="40%" />
      <img src="img/graphitesienna2.png" alt="Image 2" width="40%" />
  </div>

* `GraphiteSunset`:

  <div style="display: flex;">
      <img src="img/graphitesunset1.png" alt="Image 1" width="40%" />
      <img src="img/graphitesunset2.png" alt="Image 2" width="40%" />
  </div>

* `GraphiteGlass`:

  <div style="display: flex;">
      <img src="img/graphiteglass1.png" alt="Image 1" width="40%" />
      <img src="img/graphiteglass2.png" alt="Image 2" width="40%" />
  </div>

* `Raven`:

  <div style="display: flex;">
      <img src="img/raven1.png" alt="Image 1" width="40%" />
      <img src="img/raven2.png" alt="Image 2" width="40%" />
  </div>

## 常见问题