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
            cq.sendPrivateMsg(user_id,"anosa...你发的这个指令我无法理解呢O.O",false);
        else
        {
            cq.sendPrivateMsg(user_id,"呐呐，这里是由arttnba3开发的QQ机器人a3bot，哟",false);
            has_met_for_the_first_time.add(user_id);
        }
        return MESSAGE_IGNORE;
    }
}
