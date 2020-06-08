package com.example.demo.plugin;

import net.lz1998.cq.event.message.CQGroupMessageEvent;
import net.lz1998.cq.event.message.CQPrivateMessageEvent;
import net.lz1998.cq.robot.CQPlugin;
import net.lz1998.cq.robot.CoolQ;
import net.lz1998.cq.utils.CQCode;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.*;

@Component
public class RollPlugin extends CQPlugin
{
    List places_to_eat = new ArrayList();
    Random r = new Random();
    String message = "用法：/roll {选项}\n其中多余的选项会被忽略\n当前可选选项有：\ndice\nmeal\nboom";
    int turn = 1;

    public RollPlugin()
    {
        try
        {
            FileInputStream fileInputStream = new FileInputStream(new File("dining_hall_list.txt"));
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream));
            int ch;
            String place = bufferedReader.readLine();
            while(place != null)
            {
                places_to_eat.add(place);
                place = bufferedReader.readLine();
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public int onPrivateMessage(CoolQ cq, CQPrivateMessageEvent event)
    {
        long user_id = event.getUserId();
        return MESSAGE_IGNORE;
    }

    @Override
    public int onGroupMessage(CoolQ cq, CQGroupMessageEvent event)
    {
        String msg = event.getMessage();
        long group_id = event.getGroupId();
        if(msg.length()<5)
            return MESSAGE_IGNORE;
        if(msg.equals("/roll"))
        {
            cq.sendGroupMsg(group_id, message,false);
            return MESSAGE_BLOCK;
        }
        if(msg.substring(0,5).equals("/roll"))
        {
            String[] args = msg.split(" ");
            if(args.length<2)
            {
                cq.sendGroupMsg(group_id, message,false);
                return MESSAGE_BLOCK;
            }
            switch(args[1])
            {
                case "dice":
                    int point = r.nextInt(6);
                    int hide = r.nextInt(100);
                    if(hide == 99)
                    {
                        cq.sendGroupMsg(group_id,"恭喜你掷出了隐藏数值114514！\n>  <！",false);
                        return MESSAGE_BLOCK;
                    }
                    cq.sendGroupMsg(group_id,"你掷出的点数O.O是： " + String.valueOf(point+1),false);
                    return MESSAGE_BLOCK;
                case "meal":
                    int index = r.nextInt(places_to_eat.size());
                    cq.sendGroupMsg(group_id,"呐呐，今天我们一起去"+places_to_eat.get(index)+"恰饭叭",false);
                    return MESSAGE_BLOCK;
                case "boom":
                    int alive = r.nextInt(10);
                    if(alive == 0)
                    {
                        cq.sendGroupMsg(group_id,"boom!你死le！\n该轮轮盘进行至第 "+String.valueOf(turn)+" 轮\n/roll boom 开启新一轮俄罗斯转盘>  <",false);
                        turn = 1;
                        return MESSAGE_BLOCK;
                    }
                    cq.sendGroupMsg(group_id,"恭喜你在第 " + String.valueOf(turn) +" 轮轮盘中存活O.O\n/roll boom 进行下一轮> <",false);
                    turn++;
                    return MESSAGE_BLOCK;
                default:
                    cq.sendGroupMsg(group_id, message,false);
                    return MESSAGE_BLOCK;
            }
        }
        return MESSAGE_IGNORE;
    }
}
