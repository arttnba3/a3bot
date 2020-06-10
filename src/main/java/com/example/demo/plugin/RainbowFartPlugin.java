package com.example.demo.plugin;

import a3lib.SuperPlugin;
import net.lz1998.cq.event.message.CQGroupMessageEvent;
import net.lz1998.cq.event.message.CQPrivateMessageEvent;
import net.lz1998.cq.robot.CQPlugin;
import net.lz1998.cq.robot.CoolQ;
import net.lz1998.cq.utils.CQCode;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

@Component
public class RainbowFartPlugin extends SuperPlugin
{
    String request_url = "https://chp.shadiao.app/api.php?level=";
    long level = 114514;

    public RainbowFartPlugin()
    {
        plugin_name = "RainbowFartPlugin";
    }

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
        // 获取 消息内容 群号 发送者QQ
        String msg = event.getMessage();
        long groupId = event.getGroupId();
        long userId = event.getUserId();

        if (msg.equals("/rainbow"))
        {
            try
            {
                URL url = new URL(request_url+String.valueOf(level));
                HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                httpURLConnection.setDoInput(true);
                httpURLConnection.setRequestMethod("GET");
                httpURLConnection.connect();

                String rainbow_msg = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream(),"UTF-8")).readLine();
                cq.sendGroupMsg(groupId,rainbow_msg,false);

                httpURLConnection.disconnect();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            return MESSAGE_BLOCK;
        }

        else if(msg.length()>12)
        {
            if(msg.substring(0,12).equals("/rainbow set"))
            {
                try
                {
                    long level = Long.valueOf(msg.substring(10));
                    this.level = level;
                    cq.sendGroupMsg(groupId,"Success.",false);
                    return MESSAGE_BLOCK;
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                    cq.sendGroupMsg(groupId,"incorrect argument(s) input",false);
                    return MESSAGE_BLOCK;
                }
            }
        }
        return MESSAGE_IGNORE;
    }

}
