package com.example.demo.plugin;
import a3lib.SuperPlugin;
import net.lz1998.cq.event.message.CQGroupMessageEvent;
import net.lz1998.cq.event.message.CQPrivateMessageEvent;
import net.lz1998.cq.robot.CoolQ;
import net.lz1998.cq.utils.CQCode;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class SignInPlugin extends SuperPlugin
{
    List<Long> pri_sign_in_list = new ArrayList<>();//私聊签到名单

    Map<Long, ArrayList<Long>> gro_sign_in_list = new HashMap<>(); //群聊签到名单

    FileInputStream pri_fileInputStream = null;
    FileInputStream gro_fileInputStream = null;
    FileOutputStream pri_fileOutputStream = null;
    FileOutputStream gro_fileOutputStream = null;
    File pri_file;
    File gro_file;

    public SignInPlugin()
    {
        //两个文件，分别表示私聊的数据和群聊的数据
        pri_file = new File("data/pri_sign_in_list.txt");
        gro_file = new File("data/grp_sign_in_list.txt");
        boolean inSpace = true;
        try
        {
            if(!pri_file.exists())
                pri_file.createNewFile();
            if(!gro_file.exists())
                gro_file.createNewFile();
            pri_fileInputStream = new FileInputStream(pri_file);
            gro_fileInputStream = new FileInputStream(gro_file);
            int ch;
            Long num = 0L;

            Long group_id = 0L, user_id = 0L;
            int flag = 1;//用于判断是群号还是qq号
            //文件格式 [群号] [QQ号] [回车]
            while ((ch=pri_fileInputStream.read())!=-1)
            {
                if(ch == '\n')
                {
                    pri_sign_in_list.add(num);
                    num = 0L;
                    continue;
                }
                num = num*10;
                num += ch-'0';
            }
            if(num!=0L)
                pri_sign_in_list.add(num);
            pri_fileOutputStream = new FileOutputStream(pri_file,true);

            while ((ch=gro_fileInputStream.read())!=-1)
            {
                if(Character.isWhitespace(ch))
                {
                    if(inSpace)
                        continue;
                    inSpace = true;
                    if(flag == 1)
                    {
                        flag = 2;
                        group_id = num;
                    }
                    else
                    {
                        user_id = num;
                        flag = 1;
                        if(gro_sign_in_list.get(group_id) == null)//若群号第一次出现，则新建ArrayList
                            gro_sign_in_list.put(group_id, new ArrayList<>());
                        gro_sign_in_list.get(group_id).add(user_id);
                    }
                    num = 0L;
                    continue;
                }
                inSpace = false;
                num = num*10;
                num += ch-'0';
            }
            if(num!=0L && flag == 2)
                gro_sign_in_list.get(group_id).add(num);
            gro_fileOutputStream = new FileOutputStream(gro_file,true);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }
    @Override
    public int onPrivateMessage(CoolQ cq, CQPrivateMessageEvent event)
    {
        if(!is_enabled)
            return MESSAGE_IGNORE;
        String msg = event.getMessage();
        if(msg.equals("/signin")||msg.equals("/签到"))
        {
            long user_id = event.getUserId();
            if(pri_sign_in_list.contains(user_id))
                cq.sendPrivateMsg(user_id,"你今天已经签过到了🔨",false);
            else
            {
                pri_sign_in_list.add(user_id);
                try
                {
                    writeID(user_id, pri_fileOutputStream);
                    pri_fileOutputStream.write('\n');
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }
                cq.sendPrivateMsg(user_id,"签到成功！你是今天第"+String.valueOf(pri_sign_in_list.size())+"位签到的哟！",false);
            }
            return MESSAGE_BLOCK;
        }
        return MESSAGE_IGNORE;
    }
//参数out用于给出具体输出到哪个文件
    public void writeID(Long id, FileOutputStream out) throws IOException
    {
        if(id>9)
            writeID(id/10L, out);
        out.write((int)(id%10+'0'));
    }

    @Override
    public int onGroupMessage(CoolQ cq, CQGroupMessageEvent event)
    {
        if(!is_enabled)
            return MESSAGE_IGNORE;
        String msg = event.getMessage();
        long user_id = event.getUserId();
        long group_id = event.getGroupId();
        if(gro_sign_in_list.get(group_id) == null)
            gro_sign_in_list.put(group_id, new ArrayList<>());

        String str = event.getMessageType();
        System.out.println(str);
        if(msg.equals("/signin")||msg.equals("/签到"))
        {

            if(user_id == 80000000L)
            {
                cq.sendGroupMsg(group_id,"匿名你签个🔨到",false);
                return MESSAGE_BLOCK;
            }
            if(gro_sign_in_list.get(group_id).contains(user_id))
                cq.sendGroupMsg(group_id,CQCode.at(user_id)+"你今天已经签过到了🔨",false);
            else
            {
                gro_sign_in_list.get(group_id).add(user_id);
                try
                {
                    writeID(group_id, gro_fileOutputStream);
                    gro_fileOutputStream.write(' ');
                    writeID(user_id, gro_fileOutputStream);
                    gro_fileOutputStream.write('\n');
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }
                cq.sendGroupMsg(group_id,CQCode.at(user_id)+"签到成功！你是今天第"+String.valueOf(gro_sign_in_list.get(group_id).size())+"位签到的哟！",false);
                switch (gro_sign_in_list.get(group_id).size())
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
        pri_sign_in_list = new ArrayList<>();
        gro_sign_in_list = new HashMap<>();
        try
        {
            pri_file.delete();
            pri_file.createNewFile();
            pri_fileOutputStream = new FileOutputStream(pri_file);
            gro_file.delete();
            gro_file.createNewFile();
            gro_fileOutputStream = new FileOutputStream(gro_file);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

}
