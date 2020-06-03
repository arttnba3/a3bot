package com.example.demo.plugin;

import net.lz1998.cq.event.message.CQPrivateMessageEvent;
import net.lz1998.cq.robot.CQPlugin;
import net.lz1998.cq.robot.CoolQ;
import org.springframework.stereotype.Component;

@Component
public class RepeaterPlugin extends CQPlugin
{
    @Override
    public int onPrivateMessage(CoolQ cq, CQPrivateMessageEvent event)
    {



        return MESSAGE_IGNORE;
    }
}
