package com.example.demo.plugin;

import a3lib.SuperPlugin;
import net.lz1998.cq.event.message.CQGroupMessageEvent;
import net.lz1998.cq.event.message.CQPrivateMessageEvent;
import net.lz1998.cq.robot.CQPlugin;
import net.lz1998.cq.robot.CoolQ;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@Component
public class KillMotherPlugin extends SuperPlugin
{
    String request_url = "https://nmsl.shadiao.app/api.php?level=";
    long level = 114514;
    long admin = 1543127579L;
    List permission_list = new ArrayList<Long>();
    File file = null;
    FileInputStream fileInputStream = null;
    FileOutputStream fileOutputStream = null;

    public KillMotherPlugin()
    {
        plugin_name = "KillMotherPlugin";
        file = new File("data/nmsl_permission.txt");
        try
        {
            if(!file.exists())
                file.createNewFile();
            fileInputStream = new FileInputStream(file);
            fileOutputStream = new FileOutputStream(file,true);
            int ch;
            long n = 0L;
            while((ch = fileInputStream.read())!=-1)
            {
                if(ch == '\n')
                {
                    permission_list.add(n);
                    n = 0L;
                    continue;
                }
                n = n*10L;
                n += ch - '0';
            }
            if(n!=0)
            {
                permission_list.add(n);
                admin = (long) permission_list.get(0);
            }
            else
                writeID(admin);
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

        if (msg.equals("/nmsl"))
        {
            if(!permission_list.contains(userId))
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

                String mother_killing_msg = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream(),"UTF-8")).readLine();
                cq.sendGroupMsg(groupId,mother_killing_msg,false);

                httpURLConnection.disconnect();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            return MESSAGE_BLOCK;
        }
        else if(msg.length()>9)
        {
            if(msg.substring(0,9).equals("/nmsl add"))
            {
                if(userId != admin)
                {
                    cq.sendGroupMsg(groupId,"Permission denied, authorization limited.",false);
                    return MESSAGE_BLOCK;
                }
                try
                {
                    long new_user = Long.valueOf(msg.substring(10));//  /nmsl add userId
                    if(permission_list.contains(new_user))
                    {
                        cq.sendGroupMsg(groupId,"Already permitted.",false);
                        return MESSAGE_BLOCK;
                    }
                    permission_list.add(new_user);
                    fileOutputStream.write('\n');
                    writeID(new_user);
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

            if(msg.substring(0,9).equals("/nmsl del"))
            {
                if(userId != admin)
                {
                    cq.sendGroupMsg(groupId,"Permission denied, authorization limited.",false);
                    return MESSAGE_BLOCK;
                }
                try
                {
                    long del_user = Long.valueOf(msg.substring(10));
                    if(!permission_list.contains(del_user))
                    {
                        cq.sendGroupMsg(groupId,"Permitted user not found.",false);
                        return MESSAGE_BLOCK;
                    }
                    permission_list.remove(del_user);
                    file.delete();
                    file.createNewFile();
                    fileOutputStream = new FileOutputStream(file);
                    writeID(admin);
                    for(int i = 1;i<permission_list.size();i++)
                    {
                        fileOutputStream.write('\n');
                        writeID((Long) permission_list.get(i));
                    }
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

            if(msg.substring(0,9).equals("/nmsl set"))
            {
                if(!permission_list.contains(userId))
                {
                    cq.sendGroupMsg(groupId,"Permission denied, authorization limited.",false);
                    return MESSAGE_BLOCK;
                }
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

    public void writeID(Long id) throws IOException
    {
        if(id>9)
            writeID(id/10L);
        fileOutputStream.write((int)(id%10+'0'));
    }
}
