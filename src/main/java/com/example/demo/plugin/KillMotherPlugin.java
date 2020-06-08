package com.example.demo.plugin;

import net.lz1998.cq.event.message.CQGroupMessageEvent;
import net.lz1998.cq.event.message.CQPrivateMessageEvent;
import net.lz1998.cq.robot.CQPlugin;
import net.lz1998.cq.robot.CoolQ;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

@Component
public class KillMotherPlugin extends CQPlugin
{
    String request_url = "https://nmsl.shadiao.app/api.php?level=";
    int level = 114514;

    @Override
    public int onPrivateMessage(CoolQ cq, CQPrivateMessageEvent event)
    {
        return MESSAGE_IGNORE;
    }

    @Override
    public int onGroupMessage(CoolQ cq, CQGroupMessageEvent event)
    {
        // 获取 消息内容 群号 发送者QQ
        String msg = event.getMessage();
        long groupId = event.getGroupId();
        long userId = event.getUserId();

        if (msg.equals("/nmsl"))
        {
            if(userId != 1543127579L)
            {
                cq.sendGroupMsg(groupId,"Permission denied, authorization limited.",false);
                return MESSAGE_BLOCK;
            }
            try
            {
                URL url = new URL(request_url+String.valueOf(level));
                HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                httpURLConnection.setDoInput(true);
                httpURLConnection.setRequestMethod("GET");
                httpURLConnection.connect();

                String rainbow_msg = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream(),"UTF-8")).readLine();
                cq.sendGroupMsg(groupId,rainbow_msg,false);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            return MESSAGE_BLOCK;
        }

        return MESSAGE_IGNORE;
    }
}
