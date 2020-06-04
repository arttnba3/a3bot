package com.example.demo.plugin;

import net.lz1998.cq.event.message.CQGroupMessageEvent;
import net.lz1998.cq.event.message.CQPrivateMessageEvent;
import net.lz1998.cq.robot.CQPlugin;
import net.lz1998.cq.robot.CoolQ;
import net.lz1998.cq.utils.CQCode;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

@Component
public class SignInPlugin extends CQPlugin
{
    ArrayList sign_in_list = new ArrayList<Integer>();
    FileInputStream fileInputStream = null;
    FileOutputStream fileOutputStream = null;
    File file;

    public SignInPlugin()
    {
        file = new File("sign_in_list.txt");
        try
        {
            if(!file.exists())
                file.createNewFile();
            fileInputStream = new FileInputStream(file);
            int ch;
            Long num = 0L;
            while ((ch=fileInputStream.read())!=-1)
            {
                if(ch == '\n')
                {
                    sign_in_list.add(num);
                    num = 0L;
                    continue;
                }
                num = num*10;
                num += ch-'0';
            }
            if(num!=0L)
                sign_in_list.add(num);
            fileOutputStream = new FileOutputStream(file,true);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }
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
                try
                {
                    writeID(user_id);
                    fileOutputStream.write('\n');
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }
                cq.sendPrivateMsg(user_id,"签到成功！你是今天第"+String.valueOf(sign_in_list.size())+"位签到的哟！",false);
            }
            return MESSAGE_BLOCK;
        }
        return MESSAGE_IGNORE;
    }

    public void writeID(Long user_id) throws IOException
    {
        if(user_id>9)
            writeID(user_id/10L);
        fileOutputStream.write((int)(user_id%10+'0'));
    }

    @Override
    public int onGroupMessage(CoolQ cq, CQGroupMessageEvent event)
    {
        String msg = event.getMessage();
        if(msg.equals("/signin")||msg.equals("/签到"))
        {
            long user_id = event.getUserId();
            long group_id = event.getGroupId();
            if(user_id == 80000000L)
            {
                cq.sendGroupMsg(group_id,"匿名你签个🔨到",false);
                return MESSAGE_BLOCK;
            }
            if(sign_in_list.contains(user_id))
                cq.sendGroupMsg(group_id,CQCode.at(user_id)+"你今天已经签过到了🔨",false);
            else
            {
                sign_in_list.add(user_id);
                try
                {
                    writeID(user_id);
                    fileOutputStream.write('\n');
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }
                cq.sendGroupMsg(group_id,CQCode.at(user_id)+"签到成功！你是今天第"+String.valueOf(sign_in_list.size())+"位签到的哟！",false);
                switch (sign_in_list.size())
                {
                    case 1:
                        cq.sendGroupMsg(group_id,"获得成就：最早签到の人✔",false);
                        break;
                    case 2:
                        cq.sendGroupMsg(group_id,"嘛，第二个签到也不错啦~",false);
                        break;
                    case 3:
                        cq.sendGroupMsg(group_id,"啊啊，你来的有点晚呢...",false);
                        break;
                }
            }
            return MESSAGE_BLOCK;
        }
        return MESSAGE_IGNORE;
    }

    @Scheduled(cron = "0 0 5 * * ? ")
    public void resetList()
    {
        sign_in_list = new ArrayList<Integer>();
        try
        {
            file.delete();
            file.createNewFile();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

}
