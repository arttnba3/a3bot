package com.example.demo.plugin;

import net.lz1998.cq.event.message.CQGroupMessageEvent;
import net.lz1998.cq.event.message.CQPrivateMessageEvent;
import net.lz1998.cq.robot.CQPlugin;
import net.lz1998.cq.robot.CoolQ;
import net.lz1998.cq.utils.CQCode;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class SignInPlugin extends CQPlugin
{
    ArrayList sign_in_list = new ArrayList<Integer>();
    @Override
    public int onPrivateMessage(CoolQ cq, CQPrivateMessageEvent event)
    {
        String msg = event.getMessage();
        if(msg.equals("/signin")||msg.equals("/签到"))
        {
            long user_id = event.getUserId();
            if(sign_in_list.contains(user_id))
                cq.sendPrivateMsg(user_id,"你今天已经签过到了🔨",false);
            else
            {
                sign_in_list.add(user_id);
                cq.sendPrivateMsg(user_id,"签到成功！你是今天第"+String.valueOf(sign_in_list.size())+"位签到的哟！",false);
            }
            return MESSAGE_BLOCK;
        }
        return MESSAGE_IGNORE;
    }

    @Override
    public int onGroupMessage(CoolQ cq, CQGroupMessageEvent event)
    {
        String msg = event.getMessage();
        if(msg.equals("/signin")||msg.equals("/签到"))
        {
            long user_id = event.getUserId();
            long group_id = event.getGroupId();
            if(sign_in_list.contains(user_id))
                cq.sendGroupMsg(group_id,CQCode.at(user_id)+"你今天已经签过到了🔨",false);
            else
            {
                sign_in_list.add(user_id);
                cq.sendGroupMsg(group_id,CQCode.at(user_id)+"签到成功！你是今天第"+String.valueOf(sign_in_list.size())+"位签到的哟！",false);
            }
            return MESSAGE_BLOCK;
        }
        return MESSAGE_IGNORE;
    }

    @Scheduled(cron = "0 0 0 * * ? ")
    public void resetList()
    {
        sign_in_list = new ArrayList<Integer>();
        //sign_in_list.clear();
    }

}
