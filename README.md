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

随后运行酷Q登录机器人账号，再运行DemoApplication，机器人就正式上线了

## 已实装插件
暂无
## to-do list
开发插件系统

开发几个基础插件

- 签到系统
- 复读姬
- 防撤回
- 图灵api
- more...