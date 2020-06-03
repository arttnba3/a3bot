package com.example.demo.plugin;

import net.lz1998.cq.event.message.CQPrivateMessageEvent;
import net.lz1998.cq.robot.CQPlugin;
import net.lz1998.cq.robot.CoolQ;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class TempPrivateMsgPlugin extends CQPlugin
{
    ArrayList has_met_for_the_first_time = new ArrayList<Integer>();
    @Override
    public int onPrivateMessage(CoolQ cq, CQPrivateMessageEvent event)
    {
        long user_id = event.getUserId();

        if(has_met_for_the_first_time.contains(user_id))
            ;//cq.sendPrivateMsg(user_id,"目前只有群组复读姬功能啦O.O还请不要发私聊消息",false);
        else
        {
            cq.sendPrivateMsg(user_id,"你好呀，这里是由arttnba3开发的QQ机器人a3bot~",false);
            has_met_for_the_first_time.add(user_id);
        }
        return MESSAGE_IGNORE;
    }
}
