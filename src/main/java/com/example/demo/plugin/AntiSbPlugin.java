package com.example.demo.plugin;

import a3lib.SuperPlugin;
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
public class AntiSbPlugin extends SuperPlugin
{
    String request_url = "https://nmsl.shadiao.app/api.php?level=";
    String level = "min";
    String help_info = "自动追杀插件，享受最原始的对线体验\n"
            + "用法：\n"
            + "/anti add [qq num]    ----追杀这位🤡\n"
            + "/anti del [qq num]    ----🤡一瞬车软，停止迫害\n"
            + "/anti set [level]     ----改变杀🦄等级\n"
            + "多余的参数会自动与您的母亲一起身体力行解决🗾的少子化问题";
    long admin = 1543127579L;
    List sb_list = new ArrayList<Long>();
    File file = null;
    FileInputStream fileInputStream = null;
    FileOutputStream fileOutputStream = null;

    @SuppressWarnings("unchecked")
    public AntiSbPlugin()
    {
        plugin_name = "AntiSbPlugin";
        file = new File("data/sb_list.txt");
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
                    sb_list.add(n);
                    n = 0L;
                    continue;
                }
                n = n*10L;
                n += ch - '0';
            }
            if(n!=0)
            {
                sb_list.add(n);
                admin = (long) sb_list.get(0);
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
    @SuppressWarnings("unchecked")
    public int onGroupMessage(CoolQ cq, CQGroupMessageEvent event)
    {
        if(!is_enabled)
            return MESSAGE_IGNORE;
        long userId = event.getUserId();
        long groupId = event.getGroupId();
        String msg = event.getMessage();

        if(sb_list.contains(userId) && userId!=admin)
        {
            try
            {
                URL url = new URL(request_url + level);
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

        String[] args = msg.split(" ");

        if (args[0].equals("/anti"))
        {
            if(userId != admin)
            {
                cq.sendGroupMsg(groupId,"Permission denied, authorization limited.",false);
                return MESSAGE_BLOCK;
            }
            if(args.length == 1)
            {
                try
                {
                    URL url = new URL(request_url + level);
                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                    httpURLConnection.setDoInput(true);
                    httpURLConnection.setRequestMethod("GET");
                    httpURLConnection.connect();

                    String mother_killing_msg = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream(), "UTF-8")).readLine();
                    cq.sendGroupMsg(groupId, mother_killing_msg, false);

                    httpURLConnection.disconnect();
                } catch (Exception e)
                {
                    e.printStackTrace();
                }
                return MESSAGE_BLOCK;
            }
            else if (args.length < 3)
            {
                cq.sendGroupMsg(groupId, help_info, false);
                return MESSAGE_BLOCK;
            }
            else
            {
                if(args[1].equals("add"))
                {
                    if(userId != admin)
                    {
                        cq.sendGroupMsg(groupId,"Permission denied, authorization limited.",false);
                        return MESSAGE_BLOCK;
                    }
                    try
                    {
                        long new_user = Long.valueOf(args[2]);//  /nmsl add userId
                        if(sb_list.contains(new_user))
                        {
                            cq.sendGroupMsg(groupId,"Already 🤡.",false);
                            return MESSAGE_BLOCK;
                        }
                        sb_list.add(new_user);
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
                else if(args[1].equals("del"))
                {
                    if(userId != admin)
                    {
                        cq.sendGroupMsg(groupId,"Permission denied, authorization limited.",false);
                        return MESSAGE_BLOCK;
                    }
                    try
                    {
                        long del_user = Long.valueOf(args[2]);
                        if(!sb_list.contains(del_user))
                        {
                            cq.sendGroupMsg(groupId,"🤡 user not found.",false);
                            return MESSAGE_BLOCK;
                        }
                        sb_list.remove(del_user);
                        file.delete();
                        file.createNewFile();
                        fileOutputStream = new FileOutputStream(file);
                        writeID(admin);
                        for(int i = 1;i<sb_list.size();i++)
                        {
                            fileOutputStream.write('\n');
                            writeID((Long) sb_list.get(i));
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
                else if(args[1].equals("set"))
                {
                    if(userId != admin)
                    {
                        cq.sendGroupMsg(groupId,"Permission denied, authorization limited.",false);
                        return MESSAGE_BLOCK;
                    }
                    this.level = args[2];
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
