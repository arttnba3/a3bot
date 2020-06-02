from nonebot import on_command, CommandSession
import requests

# on_command 装饰器将函数声明为一个命令处理器
# 这里 weather 为命令的名字，同时允许使用别名「天气」「天气预报」「查天气」
# by arttnba3：似乎是插件的入口2020.6.3
@on_command('weather')
async def weather(session: CommandSession):
    # 从会话状态（session.state）中获取城市名称（city），如果当前不存在，则询问用户
    city = session.get('city', prompt='呐呐，你想查询哪个城市的天气呢？（exit退出查询）')
    # 获取城市的天气预报
    weather_report = await get_weather_of_city(city)
    # 向用户发送天气预报
    await session.send('[CQ:image,file=1E001AAAB4911FF513EA4D57C75F5F3C.png,url=https://gchat.qpic.cn/gchatpic_new/1543127579/661305490-2548260120-1E001AAAB4911FF513EA4D57C75F5F3C/0?term=2]')
    await session.send(weather_report)


# weather.args_parser 装饰器将函数声明为 weather 命令的参数解析器
# 命令解析器用于将用户输入的参数解析成命令真正需要的数据
@weather.args_parser
async def _(session: CommandSession):
    # 去掉消息首尾的空白符
    stripped_arg = session.current_arg_text.strip()

    if session.is_first_run:
        # 该命令第一次运行（第一次进入命令会话）
        if stripped_arg:
            # 第一次运行参数不为空，意味着用户直接将城市名跟在命令名后面，作为参数传入
            # 例如用户可能发送了：天气 南京
            session.state['city'] = stripped_arg
        return

    if not stripped_arg:
        # 用户没有发送有效的城市名称（而是发送了空白字符），则提示重新输入
        # 这里 session.pause() 将会发送消息并暂停当前会话（该行后面的代码不会被运行）
        #session.pause('要查询的城市名称不能为空呢，请重新输入')
        session.pause('你个哈批啥都不输入🐎，给👴爬')

    # 如果当前正在向用户询问更多信息（例如本例中的要查询的城市），且用户输入有效，则放入会话状态
    session.state[session.current_key] = stripped_arg


async def get_weather_of_city(city: str) -> str:
    # 这里简单返回一个字符串
    # 实际应用中，这里应该调用返回真实数据的天气 API，并拼接成天气预报内容
    if city == 'help' or city == '-h' or city == 'h' or city == '--h' or city == '-help' or city == '--help':
        return '用法：@bot/weather [one_city_name]\n注意：\n1.一次只能输出一个城市\n2.由于API限制，查询无需带上后缀，如：查询北京市天气应输入北京而不是北京市'

    if city == 'exit':
        return '已经退出天气查询系统，de⭐su'

    rep = requests.get('http://www.tianqiapi.com/api?version=v6&appid=23035354&appsecret=8YvlPNrz&city=' + city)
    rep.encoding = 'utf-8'

    if rep.json()['city'] != city:
        return '无法查询城市: ' + "\"" + city + "\"" + '天气！请重新输入！'

    #result = ('返回结果:%s' % rep.json())
    result = ('城市：%s' % rep.json()['city'])
    result += ('\n天气：%s' % rep.json()['wea'])
    result += ('\n风向：%s' % rep.json()['win'])
    result += ('\n温度：%s' % rep.json()['tem'] + '°C')
    result += ('\n风力：%s' % rep.json()['win_speed'])
    result += ('\n湿度：%s' % rep.json()['humidity'])
    result += ('\n空气质量：%s' % rep.json()['air_level'])

    return result