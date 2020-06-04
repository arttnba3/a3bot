# a3bot-说明文档
## 简介
a3bot是由arttnba3开发的一个基于酷Q、cqhttp、[springCQ](https://github.com/lz1998/Spring-CQ)的一个自用型QQ机器人

本机器人使用反向ws代理，默认端口8081

## 使用说明
clone本仓库到本地，使用IDEA打开文件夹后等待一会加载运行库（开发当晚我等着等着就睡着了XD）

下载酷Q与cqhttp插件，第一次启用cqhttp插件后关闭酷Q

在酷Q目录下的```data\app\io.github.richardchien.coolqhttpapi```目录下新建文件```config.ini```，输入以下内容：

```ini
[general]
use_http=false
use_ws_reverse=true
ws_reverse_url=ws://127.0.0.1:8081/ws/cq/
ws_reverse_use_universal_client=true
enable_heartbeat=true
heartbeat_interval=60000
```

随后运行酷Q登录机器人账号，再运行MainApplication，机器人就正式上线了

## 已实装插件
- 复读姬
- 签到系统

## 我想开发新插件...

在 ```src\main\java\com\example\demo\plugin```下新建你的自定义插件类

插件类需继承自CQPlugin类，具体格式可参见插件模板```DemoPlugin```

新的插件需要在 ```src\main\resources\application.yml ```下注册

## to-do list
开发插件系统

开发几个基础插件

- 插件热拔插

- 防撤回
- 图灵api
- 自动回复（开发中）
- more...