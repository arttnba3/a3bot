package com.example.demo.plugin;

import net.lz1998.cq.event.message.CQGroupMessageEvent;
import net.lz1998.cq.event.message.CQPrivateMessageEvent;
import net.lz1998.cq.robot.CQPlugin;
import net.lz1998.cq.robot.CoolQ;
import org.springframework.stereotype.Component;

@Component
public class RepeaterPlugin extends CQPlugin
{
    String temp_msg = null;
    boolean is_repeated = false;

    @Override
    public int onPrivateMessage(CoolQ cq, CQPrivateMessageEvent event)
    {
        return MESSAGE_IGNORE;
    }

    @Override
    public int onGroupMessage(CoolQ cq, CQGroupMessageEvent event)
    {
        String msg = event.getMessage();
        long groupId = event.getGroupId();
        long userId = event.getUserId();

        if(msg.equals(temp_msg))
        {
            if(!is_repeated)
            {
                cq.sendGroupMsg(groupId,msg,false);
                is_repeated = true;
            }
        }
        else
            is_repeated = false;
        temp_msg = msg;

        return MESSAGE_IGNORE;
    }
}
