package com.example.demo.plugin;

import a3lib.SuperPlugin;
import net.lz1998.cq.CQGlobal;
import net.lz1998.cq.event.message.CQGroupMessageEvent;
import net.lz1998.cq.event.message.CQPrivateMessageEvent;
import net.lz1998.cq.robot.CoolQ;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Map;

@Component
public class AnnouncePlugin extends SuperPlugin
{
    ArrayList<AnnounceInfo> announce_list = new ArrayList<AnnounceInfo>();
    String help_info = "用法：/announce [type] [announcer] [target] [hour] [minute] {msg}\n" +
            "其中type的可选项为：\nprivate\ngroup\n" +
            "announcer为播报机器人账号id" +
            "target为目标账号/群组id\n" +
            "时间为24小时格式\n" +
            "可将小时/分钟设为-1表示每小时/分钟进行一次播报";
    String help_info2 = "用法：/announce del [id]\n" +
            "其中：播报的id可用/announce list指令进行查询";

    public AnnouncePlugin()
    {
        plugin_name = "AnnouncePlugin";
    }

    @Override
    public int onPrivateMessage(CoolQ cq, CQPrivateMessageEvent event)
    {
        if(!is_enabled)
            return MESSAGE_IGNORE;

        return MESSAGE_IGNORE;
    }

    @Override
    @SuppressWarnings("unchecked")
    public int onGroupMessage(CoolQ cq, CQGroupMessageEvent event)
    {
        if(!is_enabled)
            return MESSAGE_IGNORE;

        String msg = event.getMessage();
        long userId = event.getUserId();
        long groupId = event.getGroupId();
        if(msg.length()<9)
            return MESSAGE_IGNORE;
        String[] msg_list = msg.split(" ");
        if(!msg_list[0].equals("/announce")||msg.length()<2)
            return MESSAGE_IGNORE;
        if(userId != 1543127579)
        {
            cq.sendGroupMsg(groupId,"Permission denied, authorizathion limited.",false);
            return MESSAGE_BLOCK;
        }
        if(msg_list.length < 2)
        {
            cq.sendGroupMsg(groupId,help_info,false);
            return MESSAGE_BLOCK;
        }
        if(msg_list[1].equals("list"))
        {
            String info = "当前已有的自动播报如下：";
            String list = "";
            for(int i = 0;i<announce_list.size();i++)
            {
                list = list+ "\n\n" +
                        "id:" + String.valueOf(i) + "\n" +
                        "announcer:" + String.valueOf(announce_list.get(i).announcer) + "\n" +
                        "type:" + (announce_list.get(i).type == 0 ? "private" : "group") + "\n" +
                        "target:" + String.valueOf(announce_list.get(i).target) + "\n" +
                        "time:" + String.valueOf(announce_list.get(i).hour) +":"+ String.valueOf(announce_list.get(i).minute) + "\n" +
                        "message:" + announce_list.get(i).msg;
            }
            cq.sendGroupMsg(groupId,info+list,false);
            return MESSAGE_BLOCK;
        }
        if(msg_list[1].equals("save"))
        {
            if(userId != 1543127579L)
            {
                cq.sendGroupMsg(groupId,"Permission denied, authorization limited.",false);
                return MESSAGE_BLOCK;
            }
            try
            {
                File file = new File("data/announce.data");
                if(!file.exists())
                    file.createNewFile();
                OutputStream outputStream = new FileOutputStream(file);
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
                objectOutputStream.writeObject(announce_list);
                objectOutputStream.close();
                outputStream.close();
                cq.sendGroupMsg(groupId,"数据文件已保存",false);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            return MESSAGE_BLOCK;
        }
        if(msg_list[1].equals("load"))
        {
            if(userId != 1543127579L)
            {
                cq.sendGroupMsg(groupId,"Permission denied, authorization limited.",false);
                return MESSAGE_BLOCK;
            }
            try
            {
                File file = new File("data/announce.data");
                if(!file.exists())
                {
                    file.createNewFile();
                    cq.sendGroupMsg(groupId,"数据文件不存在",false);
                    return MESSAGE_BLOCK;
                }
                InputStream inputStream = new FileInputStream(file);
                ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
                announce_list = (ArrayList<AnnounceInfo>) objectInputStream.readObject();
                objectInputStream.close();
                inputStream.close();
                cq.sendGroupMsg(groupId,"数据文件已载入",false);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            return MESSAGE_BLOCK;
        }
        if(msg_list[1].equals("del"))
        {
            if(msg_list.length == 2)
            {
                cq.sendGroupMsg(groupId,help_info2,false);
                return MESSAGE_BLOCK;
            }
            try
            {
                int target = Integer.parseInt(msg_list[2]);
                if(target >= announce_list.size())
                {
                    cq.sendGroupMsg(groupId,"announcement not found, wrong id",false);
                    return MESSAGE_BLOCK;
                }
                announce_list.remove(target);
                cq.sendGroupMsg(groupId,"播报移除成功",false);
            }
            catch (Exception e)
            {
                e.printStackTrace();
                cq.sendGroupMsg(groupId,help_info2,false);
            }
            return MESSAGE_BLOCK;
        }
        if(msg_list.length < 7)
        {
            cq.sendGroupMsg(groupId,help_info,false);
            return MESSAGE_BLOCK;
        }
        try
        {
            int type = msg_list[1].equals("private") ? 0 : (msg_list[1].equals("group") ? 1 : 2);
            if(type == 2)
            {
                cq.sendGroupMsg(groupId,help_info,false);
                return MESSAGE_BLOCK;
            }
            long announcer = Long.valueOf(msg_list[2]);
            long target = Long.valueOf(msg_list[3]);
            int hour = Integer.parseInt(msg_list[4]);
            int minute = Integer.parseInt(msg_list[5]);
            String announce_msg = msg.substring(msg_list[0].length() + msg_list[1].length() + msg_list[2].length() + msg_list[3].length() + msg_list[4].length() + msg_list[5].length() + 6);
            announce_list.add(new AnnounceInfo(announce_msg,type,announcer,target,hour,minute));
            cq.sendGroupMsg(groupId,"自动播报添加成功",false);
            return MESSAGE_BLOCK;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            cq.sendGroupMsg(groupId,help_info,false);
            return MESSAGE_BLOCK;
        }

    }

    @Scheduled(cron = "0 */1 * * * ? ")
    public void autoAnnounce()
    {
        if(!is_enabled)
            return;
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        int amount = announce_list.size();
        for(int i=0;i<amount;i++)
        {
            AnnounceInfo announceInfo = announce_list.get(i);
            if((announceInfo.minute == minute && announceInfo.hour == hour)
                    || (announceInfo.minute == -1 && announceInfo.hour == hour)
                    || (announceInfo.minute == minute && announceInfo.hour == -1)
                    || (announceInfo.minute == -1 && announceInfo.hour == -1))
            {
                if(announceInfo.type == 0)
                    CQGlobal.robots.get(announceInfo.announcer).sendPrivateMsg(announceInfo.target,announceInfo.msg,false);
                else
                    CQGlobal.robots.get(announceInfo.announcer).sendGroupMsg(announceInfo.target,announceInfo.msg,false);
            }
        }
    }
}

class AnnounceInfo implements Serializable
{
    public String msg;
    int type;//0 for people, 1 for group
    long announcer;
    long target;
    int hour;
    int minute;
    public AnnounceInfo(String msg, int type,long announcer, long target, int hour, int minute)
    {
        this.msg = msg;
        this.type = type;
        this.announcer = announcer;
        this.target = target;
        this.hour = hour;
        this.minute = minute;
    }
}
