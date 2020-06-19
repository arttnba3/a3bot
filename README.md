# a3bot-说明文档
## 简介
a3bot是由arttnba3开发的一个基于酷Q、cqhttp、[springCQ](https://github.com/lz1998/Spring-CQ)的一个自用型QQ机器人

本机器人使用反向ws代理，默认端口8081

## 使用说明
clone本仓库到本地，使用IDEA打开文件夹后等待一会加载运行库（部署当晚我等着等着就睡着了XD）

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
- 复读姬1.0
- 签到系统2.0.2（修复了一个没被发现的2.0版本遗留bug（文件流未刷新导致数据重复））
- 插件管理系统1.0
- 随机点餐1.0
- 俄罗斯转盘1.0
- 三国杀1.0
- 初等学习2.0（实装存储功能，实装私聊教授，私聊与群组数据互通实现）
- 一言1.0（/hitokoto {参数}，参数为可选选项）
- 🌈屁1.5（开放程度修改指令/nmsl set [level]）
- 最强舔狗1.0（基于🌈屁）
- 杀🐎2.0（开放部分权限，新增程度修改指令/nmsl set [level]）
- 自动化杀🐎1.0（仅开放admin权限，data/sb_list.txt下的第一个是admin~~（毕竟要渡人先渡己）~~，指令：/anti add [id]与/anti del [id]）

## 我想开发新插件...

在 ```src\main\java\com\example\demo\plugin```下新建你的自定义插件类

~~插件类需继承自CQPlugin类，具体格式可参见插件模板```DemoPlugin```~~

**为适应插件管理系统，新的插件应当继承自SuperPlugin类，具体格式参见插件模板DemoPlugin**

新的插件需要在 ```src\main\resources\application.yml ```下注册，注册顺序即为消息读取顺序

## to-do list
开发插件系统

开发几个基础插件

- ~~防撤回~~（平台原因无法实现，~~希望某天能开发一个自己的酷Q？~~）
- 图灵api（有没有富佬支援一个充值账号XD）
- 自动回复（开发中）
- more...

## 特别鸣谢

- 2020.6 - 感谢巨犇[Golden-Pigeon](https://github.com/Golden-Pigeon)提供的优化版的签到系统插件2.0！