package com.example.demo.plugin;

import net.lz1998.cq.event.message.CQGroupMessageEvent;
import net.lz1998.cq.event.message.CQPrivateMessageEvent;
import net.lz1998.cq.robot.CQPlugin;
import net.lz1998.cq.robot.CoolQ;
import net.lz1998.cq.utils.CQCode;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


@Component
public class FlattererPlugin extends CQPlugin
{
    String request_url = "https://chp.shadiao.app/api.php?level=";
    long level = 114514;
    long admin = 1543127579L;
    List gods_list = new ArrayList<Long>();
    File file = null;
    FileInputStream fileInputStream = null;
    FileOutputStream fileOutputStream = null;

    public FlattererPlugin()
    {
        file = new File("data/gods_list.txt");
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
                    gods_list.add(n);
                    n = 0L;
                    continue;
                }
                n = n*10L;
                n += ch - '0';
            }
            if(n!=0)
            {
                gods_list.add(n);
                admin = (long) gods_list.get(0);
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
        return MESSAGE_IGNORE;
    }

    @Override
    public int onGroupMessage(CoolQ cq, CQGroupMessageEvent event)
    {
        long userId = event.getUserId();
        long groupId = event.getGroupId();
        String msg = event.getMessage();

        if(gods_list.contains(userId)&&userId!=admin)
        {
            try
            {
                URL url = new URL(request_url+String.valueOf(level));
                HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                httpURLConnection.setDoInput(true);
                httpURLConnection.setRequestMethod("GET");
                httpURLConnection.connect();

                String mother_killing_msg = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream(),"UTF-8")).readLine();
                cq.sendGroupMsg(groupId, CQCode.at(userId)+mother_killing_msg,false);

                httpURLConnection.disconnect();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            return MESSAGE_BLOCK;
        }

        if(msg.length()>9)
        {
            if(msg.substring(0,9).equals("/lick add"))// 舔
            {
                if(userId != admin)
                {
                    cq.sendGroupMsg(groupId,"Permission denied, authorization limited.",false);
                    return MESSAGE_BLOCK;
                }
                try
                {
                    long new_gods = Long.valueOf(msg.substring(10));//  /lick add godsId
                    if(gods_list.contains(new_gods))
                    {
                        cq.sendGroupMsg(groupId,"Already permitted.",false);
                        return MESSAGE_BLOCK;
                    }
                    gods_list.add(new_gods);
                    fileOutputStream.write('\n');
                    writeID(new_gods);
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

            if(msg.substring(0,9).equals("/lick del"))
            {
                if(userId != admin)
                {
                    cq.sendGroupMsg(groupId,"Permission denied, authorization limited.",false);
                    return MESSAGE_BLOCK;
                }
                try
                {
                    long del_user = Long.valueOf(msg.substring(10));
                    if(!gods_list.contains(del_user))
                    {
                        cq.sendGroupMsg(groupId,"Permitted user not found.",false);
                        return MESSAGE_BLOCK;
                    }
                    gods_list.remove(del_user);
                    file.delete();
                    file.createNewFile();
                    fileOutputStream = new FileOutputStream(file);
                    writeID(admin);
                    for(int i = 1;i<gods_list.size();i++)
                    {
                        fileOutputStream.write('\n');
                        writeID((Long) gods_list.get(i));
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
