package com.example.demo.plugin;

import a3lib.SuperPlugin;
import net.lz1998.cq.event.message.CQGroupMessageEvent;
import net.lz1998.cq.event.message.CQPrivateMessageEvent;
import net.lz1998.cq.robot.CQPlugin;
import net.lz1998.cq.robot.CoolQ;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

@Component
public class TeachMsgPlugin extends SuperPlugin
{
    Map map = new HashMap();

    @SuppressWarnings("unchecked")
    public TeachMsgPlugin()
    {
        plugin_name = "TeachMsgPlugin";
        try
        {
            File file = new File("data/teach.data");
            if(!file.exists())
                file.createNewFile();
            else
            {
                InputStream inputStream = new FileInputStream(file);
                ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
                map = (Map) objectInputStream.readObject();
                objectInputStream.close();
                inputStream.close();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    @SuppressWarnings("unchecked")
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
        if(msg.equals("/teach save"))
        {
            long userId = event.getUserId();
            if(userId != 1543127579L)
            {
                cq.sendGroupMsg(group_id,"Permission denied, authorization limited.",false);
                return MESSAGE_BLOCK;
            }
            try
            {
                File file = new File("data/teach.data");
                if(!file.exists())
                    file.createNewFile();
                OutputStream outputStream = new FileOutputStream(file);
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
                objectOutputStream.writeObject(map);
                objectOutputStream.close();
                outputStream.close();
                cq.sendGroupMsg(group_id,"数据文件已保存",false);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            return MESSAGE_BLOCK;
        }
        if(msg.equals("/teach load"))
        {
            long userId = event.getUserId();
            if(userId != 1543127579L)
            {
                cq.sendGroupMsg(group_id,"Permission denied, authorization limited.",false);
                return MESSAGE_BLOCK;
            }
            try
            {
                File file = new File("data/teach.data");
                if(!file.exists())
                {
                    file.createNewFile();
                    cq.sendGroupMsg(group_id,"数据文件不存在",false);
                    return MESSAGE_BLOCK;
                }
                InputStream inputStream = new FileInputStream(file);
                ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
                map = (Map) objectInputStream.readObject();
                objectInputStream.close();
                inputStream.close();
                cq.sendGroupMsg(group_id,"数据文件已载入",false);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            return MESSAGE_BLOCK;
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

    @Override
    @SuppressWarnings("unchecked")
    public int onPrivateMessage(CoolQ cq, CQPrivateMessageEvent event)
    {
        if(!is_enabled)
            return MESSAGE_IGNORE;
        String msg = event.getMessage();
        long userId = event.getUserId();
        if(msg.length()<=6)
        {
            if(msg.equals("/teach"))
            {
                cq.sendPrivateMsg(userId,"用法：/teach {msg} {msg}\n多余的输入项会被自动忽略\n注：机器人会不定期重启清空被教授的内容O.O",false);
                return MESSAGE_BLOCK;
            }
            else if(map.containsKey(msg))
            {
                cq.sendPrivateMsg(userId,(String)map.get(msg),false);
                return MESSAGE_BLOCK;
            }
            return MESSAGE_IGNORE;
        }
        if(msg.equals("/teach save"))
        {
            if(userId != 1543127579L)
            {
                cq.sendGroupMsg(userId,"Permission denied, authorization limited.",false);
                return MESSAGE_BLOCK;
            }
            try
            {
                File file = new File("data/teach.data");
                if(!file.exists())
                    file.createNewFile();
                OutputStream outputStream = new FileOutputStream(file);
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
                objectOutputStream.writeObject(map);
                objectOutputStream.close();
                outputStream.close();
                cq.sendPrivateMsg(userId,"数据文件已保存",false);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            return MESSAGE_BLOCK;
        }
        if(msg.equals("/teach load"))
        {
            if(userId != 1543127579L)
            {
                cq.sendPrivateMsg(userId,"Permission denied, authorization limited.",false);
                return MESSAGE_BLOCK;
            }
            try
            {
                File file = new File("data/teach.data");
                if(!file.exists())
                {
                    file.createNewFile();
                    cq.sendPrivateMsg(userId,"数据文件不存在",false);
                    return MESSAGE_BLOCK;
                }
                InputStream inputStream = new FileInputStream(file);
                ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
                map = (Map) objectInputStream.readObject();
                objectInputStream.close();
                inputStream.close();
                cq.sendPrivateMsg(userId,"数据文件已载入",false);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            return MESSAGE_BLOCK;
        }
        if(msg.substring(0,7).equals("/teach "))
        {
            String[] args = msg.split(" ");
            if(args.length<3)
            {
                cq.sendPrivateMsg(userId,"用法：/teach {msg} {msg}\n多余的输入项会被自动忽略",false);
                return MESSAGE_BLOCK;
            }
            if(map.containsKey(args[1]))
            {
                cq.sendPrivateMsg(userId,"anosa...我已经学过这个了哟...",false);
                return MESSAGE_BLOCK;
            }
            map.put(args[1],args[2]);
            cq.sendPrivateMsg(userId,"呐，我学会了哟，你呢w",false);
            return MESSAGE_BLOCK;
        }
        else if(msg.substring(0,7).equals("/delete"))
        {
            if(msg.equals("/delete"))
            {
                cq.sendPrivateMsg(userId,"用法：/delete {msg}\n多余的输入项会被自动忽略",false);
                return MESSAGE_BLOCK;
            }
            String[] args = msg.split(" ");
            if(args.length<2)
            {
                cq.sendPrivateMsg(userId,"用法：/delete {msg}\n多余的输入项会被自动忽略",false);
                return MESSAGE_BLOCK;
            }
            if(map.containsKey(args[1]))
            {
                map.remove(args[1]);
                cq.sendPrivateMsg(userId,"anosa..我好像...忘了点什么...",false);
                return MESSAGE_BLOCK;
            }
            else
            {
                cq.sendPrivateMsg(userId,"呐，我还没学过这个呀，不如你先教我好啦",false);
                return MESSAGE_BLOCK;
            }
        }
        else if(map.containsKey(msg))
        {
            cq.sendPrivateMsg(userId,(String)map.get(msg),false);
            return MESSAGE_BLOCK;
        }
        return MESSAGE_IGNORE;
    }
}
