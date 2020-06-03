import nonebot
from nonebot import on_command, CommandSession

@on_command('plugins')
async def plugin(session:CommandSession):
    plugins = nonebot.plugin.get_loaded_plugins()
    plugins_list = '已装载以下指令：\n'#.join(map(lambda p: p.name, filter(lambda p: p.name, plugins))))
    for x in iter(plugins):
        plugins_list += (x.commands.__str__()[18:])[:-5]+'\n'
    await session.send(plugins_list)