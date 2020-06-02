import nonebot

import config

if __name__ == "__main__":
    nonebot.init(config)
    nonebot.load_builtin_plugins()
    nonebot.run(host = '127.0.0.1', port  = 1919)#iku iku port desune