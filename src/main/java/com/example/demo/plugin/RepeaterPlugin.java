package com.example.demo.plugin;

import a3lib.SuperPlugin;
import net.lz1998.cq.event.message.CQGroupMessageEvent;
import net.lz1998.cq.event.message.CQPrivateMessageEvent;
import net.lz1998.cq.robot.CoolQ;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class RepeaterPlugin extends SuperPlugin
{
    Map map = new HashMap();

    @Override
    public int onPrivateMessage(CoolQ cq, CQPrivateMessageEvent event)
    {
        if(!is_enabled)
            return MESSAGE_IGNORE;
        return MESSAGE_IGNORE;
    }

    @Override
    public int onGroupMessage(CoolQ cq, CQGroupMessageEvent event)
    {
        if(!is_enabled)
            return MESSAGE_IGNORE;
        String msg = event.getMessage();
        long groupId = event.getGroupId();
        long userId = event.getUserId();
        if(!map.containsKey(groupId))
        {
            map.put(groupId, new RepeatInfo(msg, false));
            return MESSAGE_IGNORE;
        }

        if(msg.equals(((RepeatInfo)(map.get(groupId))).msg))
        {
            if(!((RepeatInfo)(map.get(groupId))).is_repeated)
            {
                cq.sendGroupMsg(groupId,msg,false);
                ((RepeatInfo)(map.get(groupId))).is_repeated = true;
            }
        }
        else
            ((RepeatInfo)(map.get(groupId))).is_repeated = false;
        ((RepeatInfo)(map.get(groupId))).msg = msg;

        return MESSAGE_IGNORE;
    }
}

class RepeatInfo
{
    public String msg;
    public boolean is_repeated;
    public RepeatInfo(String str,boolean flag)
    {
        this.msg = str;
        this.is_repeated = flag;
    }
}
