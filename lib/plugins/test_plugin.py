from nonebot import on_command, CommandSession

#@on_command()
async def repeater(session: CommandSession):
    msg = session.get()
    await session.send(msg)

@repeater.args_parser
async def _(session: CommandSession):
    stripped_arg = session.current_arg_text#.strip()

    if session.is_first_run:
        if stripped_arg:
            # 第一次运行参数不为空
            session.state = stripped_arg
        return

    if not stripped_arg:
        pass

    # 如果当前正在向用户询问更多信息（例如本例中的要查询的城市），且用户输入有效，则放入会话状态
    session.state[session.current_key] = stripped_arg