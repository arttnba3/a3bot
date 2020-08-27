package com.example.demo.plugin;

import a3lib.SuperPlugin;
import net.lz1998.cq.event.message.CQGroupMessageEvent;
import net.lz1998.cq.event.message.CQPrivateMessageEvent;
import net.lz1998.cq.robot.CQPlugin;
import net.lz1998.cq.robot.CoolQ;
import net.lz1998.cq.utils.CQCode;
import org.springframework.stereotype.Component;

@Component
public class CompatPlugin extends CQPlugin
{
    @Override
    public int onPrivateMessage(CoolQ cq, CQPrivateMessageEvent event)
    {
        event.setMessage(event.getRawMessage());
        return super.onPrivateMessage(cq, event);
    }



    @Override
    public int onGroupMessage(CoolQ cq, CQGroupMessageEvent event)
    {
        event.setMessage(event.getRawMessage());
        return super.onGroupMessage(cq, event);
    }
}

