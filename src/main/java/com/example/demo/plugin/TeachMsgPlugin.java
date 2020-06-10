package com.example.demo.plugin;

import a3lib.SuperPlugin;
import net.lz1998.cq.event.message.CQGroupMessageEvent;
import net.lz1998.cq.robot.CQPlugin;
import net.lz1998.cq.robot.CoolQ;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class TeachMsgPlugin extends SuperPlugin
{
    Map map = new HashMap();
    @Override
    public int onGroupMessage(CoolQ cq, CQGroupMessageEvent event)
    {
        if(!is_enabled)
            return MESSAGE_IGNORE;
        String msg = event.getMessage();
        long group_id = event.getGroupId();
        if(msg.length()<=6)
        {
            if(msg.equals("/teach"))
            {
                cq.sendGroupMsg(group_id,"用法：/teach {msg} {msg}\n多余的输入项会被自动忽略\n注：机器人会不定期重启清空被教授的内容O.O",false);
                return MESSAGE_BLOCK;
            }
            else if(map.containsKey(msg))
            {
                cq.sendGroupMsg(group_id,(String)map.get(msg),false);
                return MESSAGE_BLOCK;
            }
            return MESSAGE_IGNORE;
        }
        if(msg.substring(0,7).equals("/teach "))
        {
            String[] args = msg.split(" ");
            if(args.length<3)
            {
                cq.sendGroupMsg(group_id,"用法：/teach {msg} {msg}\n多余的输入项会被自动忽略",false);
                return MESSAGE_BLOCK;
            }
            if(map.containsKey(args[1]))
            {
                cq.sendGroupMsg(group_id,"anosa...我已经学过这个了哟...",false);
                return MESSAGE_BLOCK;
            }
            map.put(args[1],args[2]);
            cq.sendGroupMsg(group_id,"呐，我学会了哟，你呢w",false);
            return MESSAGE_BLOCK;
        }
        else if(msg.substring(0,7).equals("/delete"))
        {
            if(msg.equals("/delete"))
            {
                cq.sendGroupMsg(group_id,"用法：/delete {msg}\n多余的输入项会被自动忽略",false);
                return MESSAGE_BLOCK;
            }
            String[] args = msg.split(" ");
            if(args.length<2)
            {
                cq.sendGroupMsg(group_id,"用法：/delete {msg}\n多余的输入项会被自动忽略",false);
                return MESSAGE_BLOCK;
            }
            if(map.containsKey(args[1]))
            {
                map.remove(args[1]);
                cq.sendGroupMsg(group_id,"anosa..我好像...忘了点什么...",false);
                return MESSAGE_BLOCK;
            }
            else
            {
                cq.sendGroupMsg(group_id,"呐，我还没学过这个呀，不如你先教我好啦",false);
                return MESSAGE_BLOCK;
            }
        }
        else if(map.containsKey(msg))
        {
            cq.sendGroupMsg(group_id,(String)map.get(msg),false);
            return MESSAGE_BLOCK;
        }
        return MESSAGE_IGNORE;
    }
}
