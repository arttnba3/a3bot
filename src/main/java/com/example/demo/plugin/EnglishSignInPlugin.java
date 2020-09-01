package com.example.demo.plugin;

import a3lib.SuperPlugin;
import net.lz1998.cq.event.message.CQGroupMessageEvent;
import net.lz1998.cq.event.message.CQPrivateMessageEvent;
import net.lz1998.cq.robot.CoolQ;
import net.lz1998.cq.utils.CQCode;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

@Component
public class EnglishSignInPlugin extends SuperPlugin
{
    HashMap clock_in_list;
    ArrayList admin_list = new ArrayList<Integer>();
    String help_info = "Usage:/clock [param]\n"
                    + "可选参数：\n"
                    + "in    打卡\n"
                    + "help  获取帮助面板\n"
                    + "list  获取打卡次数名单"
                    + "以下为管理员可用指令：\n"
                    + "load  读取打卡数据\n"
                    + "save  保存打卡数据\n"
                    + "on    开启打卡历程（若不存在则会被创建）\n"
                    + "off   暂停打卡历程\n"
                    + "多余的参数会被自动忽略";

    public EnglishSignInPlugin()
    {
        plugin_name = "EnglishSignInPlugin";
        admin_list.add(1543127579L);admin_list.add(1066235869L);
        clock_in_list = new HashMap<Long,ArrayList>();//group_id to  groupInfo[0] for on/off, 1 for times, 2 for Hashmap<student_id, ArrayList<times, today_in>>, 3 for today_times
    }

    @Override
    public int onPrivateMessage(CoolQ cq, CQPrivateMessageEvent event)
    {
        if (!is_enabled)
            return MESSAGE_IGNORE;

        long user_id = event.getUserId();
        String msg = event.getMessage();
        if(msg.length()>=7&&msg.substring(0,7).equals("/clock "))
        {
            cq.sendPrivateMsg(user_id,"私信打卡功能暂未开放...！请在需要打卡的群里进行打卡>  <！",false);
            return MESSAGE_BLOCK;
        }

        return MESSAGE_IGNORE;
    }

    public int onGroupMessage(CoolQ cq, CQGroupMessageEvent event)
    {
        if (!is_enabled)
            return MESSAGE_IGNORE;

        long user_id = event.getUserId();
        long group_id = event.getGroupId();
        String msg = event.getMessage();

        if(msg.length()>=7&&msg.substring(0,7).equals("/clock "))
        {
            String[] operation = msg.split(" ");
            if(operation.length > 1 )
            {
                ArrayList group_info = (ArrayList) clock_in_list.get(group_id);
                if(operation[1].equals("in"))
                {
                    if(user_id == 80000000L)
                    {
                        cq.sendGroupMsg(group_id,"匿名你打个🔨卡",false);
                        return MESSAGE_BLOCK;
                    }
                    if(group_info == null || (Boolean) group_info.get(0) == false)
                    {
                        cq.sendGroupMsg(group_id,"本群的打卡还没开启呢>  <!",false);
                        return MESSAGE_BLOCK;
                    }
                    ArrayList student_info = (ArrayList) ((HashMap)group_info.get(2)).get(user_id);
                    if(student_info == null)
                    {
                        HashMap student_list = (HashMap)group_info.get(2);
                        student_info = new ArrayList();
                        student_info.add(0);
                        student_info.add(false);
                        student_list.put(user_id,student_info);
                    }
                    if((Boolean) student_info.get(1) == true)
                    {
                        cq.sendGroupMsg(group_id,"你今天已经打过卡啦！>  <",false);
                        return MESSAGE_BLOCK;
                    }
                    group_info.set(3,(Integer)group_info.get(3)+1);
                    student_info.set(1,true);
                    student_info.set(0,(Integer)student_info.get(0)+1);
                    cq.sendGroupMsg(group_id, CQCode.at(user_id)+"打卡成功！你是今天第"+String.valueOf((Integer)group_info.get(3))+"位打卡的哟！",false);
                    return MESSAGE_BLOCK;
                }
                else if(operation[1].equals("load"))
                {
                    if(!admin_list.contains(user_id))
                    {
                        cq.sendGroupMsg(group_id,"Permission denied, authorization limited.",false);
                        return MESSAGE_BLOCK;
                    }
                    try
                    {
                        File file = new File("data/english.data");
                        if(!file.exists())
                        {
                            file.createNewFile();
                            cq.sendGroupMsg(group_id,"数据文件不存在",false);
                            return MESSAGE_BLOCK;
                        }
                        InputStream inputStream = new FileInputStream(file);
                        ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
                        clock_in_list = (HashMap) objectInputStream.readObject();
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
                else if(operation[1].equals("save"))
                {
                    if(!admin_list.contains(user_id))
                    {
                        cq.sendGroupMsg(group_id,"Permission denied, authorization limited.",false);
                        return MESSAGE_BLOCK;
                    }
                    try
                    {
                        File file = new File("data/english.data");
                        if(!file.exists())
                            file.createNewFile();
                        OutputStream outputStream = new FileOutputStream(file);
                        ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
                        objectOutputStream.writeObject(clock_in_list);
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
                else if(operation[1].equals("on"))
                {
                    if(group_info == null)
                    {
                        group_info = new ArrayList<>();
                        group_info.add(true);
                        group_info.add(1);
                        group_info.add(new HashMap<Long,ArrayList>());
                        group_info.add(0);
                        clock_in_list.put(group_id,group_info);
                        cq.sendGroupMsg(group_id,"本群的打卡正式开启啦！",false);
                    }
                    else
                    {
                        if((Boolean) group_info.get(0) == true)
                        {
                            cq.sendGroupMsg(group_id,"打卡已经启动过啦！>  <",false);
                            return MESSAGE_BLOCK;
                        }
                        group_info.set(0,true);
                        cq.sendGroupMsg(group_id,"打卡启动啦！O O",false);
                        return MESSAGE_BLOCK;
                    }
                }
                else if(operation[1].equals("off"))
                {
                    if(group_info == null)
                    {
                        cq.sendGroupMsg(group_id,"本群还没有创建打卡列表呢>  <!",false);
                        return MESSAGE_BLOCK;
                    }
                    if((Boolean) group_info.get(0) == false)
                    {
                        cq.sendGroupMsg(group_id,"本群的打卡早就暂停啦>  <!",false);
                        return MESSAGE_BLOCK;
                    }
                    group_info.set(0,false);
                    cq.sendGroupMsg(group_id,"打卡暂停啦！> <",false);
                    return MESSAGE_BLOCK;
                }
                else if(operation[1].equals("list"))
                {
                    String message = "今天是本群的第" + String.valueOf((Integer)group_info.get(1)) + "天打卡，当前打卡名单如下：\n";
                    HashMap student_list = (HashMap)group_info.get(2);
                    for(Object student_id:student_list.keySet())
                    {
                        ArrayList student_info = (ArrayList) student_list.get(student_id);
                        message += CQCode.at((Long)student_id) + ": 打卡" + String.valueOf((Integer) student_info.get(0)) + "天\n";
                    }
                    message += "今日共有：" + String.valueOf((Integer)group_info.get(3)) + " 人打卡";
                    cq.sendGroupMsg(group_id,message,false);
                    return MESSAGE_BLOCK;
                }
                else
                {
                    cq.sendGroupMsg(group_id,help_info,false);
                    return MESSAGE_BLOCK;
                }
            }
            else
            {
                cq.sendGroupMsg(group_id,help_info,false);
                return MESSAGE_BLOCK;
            }
        }

        return MESSAGE_IGNORE;
    }

    @Scheduled(cron = "0 0 0 * * ? ")
    public void resetList()
    {
        for(Object gi:clock_in_list.values())
        {
            ArrayList group_info = (ArrayList)gi;
            if((Boolean)group_info.get(0) == true)
            {
                group_info.set(3,0);
                group_info.set(1,(Integer)group_info.get(1)+1);
                HashMap student_list = (HashMap)group_info.get(2);
                for(Object psi:student_list.values())
                {
                    ArrayList per_student_info = (ArrayList)psi;
                    per_student_info.set(1,false);
                }
            }
        }
    }
}
