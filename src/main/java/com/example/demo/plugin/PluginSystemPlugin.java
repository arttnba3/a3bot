package com.example.demo.plugin;

import a3lib.SuperPlugin;
import net.lz1998.cq.event.message.CQGroupMessageEvent;
import net.lz1998.cq.robot.CQPlugin;
import net.lz1998.cq.robot.CoolQ;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class PluginSystemPlugin extends CQPlugin
{
    long admin = 1543127579L;
    List permission_list = new ArrayList<Long>();
    File file = null;
    FileInputStream fileInputStream = null;
    FileOutputStream fileOutputStream = null;

    @Autowired
    private List<SuperPlugin> plugin_list;

    public PluginSystemPlugin()
    {
        file = new File("data/permission_list.txt");
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
    public int onGroupMessage(CoolQ cq, CQGroupMessageEvent event)
    {
        long userId = event.getUserId();
        long groupId = event.getGroupId();
        String msg = event.getMessage();
        String message = "当前所启用的插件有：";
        if(msg.equals("/Plugin")||msg.equals("/plugin"))
        {
            SuperPlugin superPlugin;
            for(int i=0;i<plugin_list.size();i++)
            {
                superPlugin = plugin_list.get(i);
                if(superPlugin.is_enabled)
                    message += "\n"+superPlugin.toString();
            }
            cq.sendGroupMsg(groupId,message,false);
        }
        if(msg.equals("/plugin all")||msg.equals("/Plugin all"))
        {
            message = "当前所装载的插件有：";
            SuperPlugin superPlugin;
            for(int i=0;i<plugin_list.size();i++)
            {
                superPlugin = plugin_list.get(i);
                message += "\n" + superPlugin.toString() + (superPlugin.is_enabled?"":"（未启用）");
            }
            cq.sendGroupMsg(groupId,message,false);
        }

        if(msg.length()>11)
        {
            if(msg.substring(0,11).equals("/plugin add"))
            {
                if(userId != admin)
                {
                    cq.sendGroupMsg(groupId,"Permission denied, authorization limited.",false);
                    return MESSAGE_BLOCK;
                }
                try
                {
                    long new_permission = Long.valueOf(msg.substring(12));//  /plugin add permissionId
                    if(permission_list.contains(new_permission))
                    {
                        cq.sendGroupMsg(groupId,"Already permitted.",false);
                        return MESSAGE_BLOCK;
                    }
                    permission_list.add(new_permission);
                    fileOutputStream.write('\n');
                    writeID(new_permission);
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

            if(msg.substring(0,11).equals("/plugin del"))
            {
                if(userId != admin)
                {
                    cq.sendGroupMsg(groupId,"Permission denied, authorization limited.",false);
                    return MESSAGE_BLOCK;
                }
                try
                {
                    long del_user = Long.valueOf(msg.substring(12));
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

            if(msg.length()>13)
            {
                if(msg.substring(0,12).equals("/plugin load"))
                {
                    if(!permission_list.contains(userId))
                    {
                        cq.sendGroupMsg(groupId,"Permission denied, authorization limited.",false);
                        return MESSAGE_BLOCK;
                    }
                    String load_plugin = msg.substring(13);
                    SuperPlugin superPlugin;
                    for(int i=0;i<plugin_list.size();i++)
                    {
                        superPlugin = plugin_list.get(i);
                        if(superPlugin.toString().equals(load_plugin))
                        {
                            if(superPlugin.is_enabled)
                            {
                                cq.sendGroupMsg(groupId,"Plugin already loaded.",false);
                                return MESSAGE_BLOCK;
                            }
                            else
                            {
                                superPlugin.is_enabled = true;
                                cq.sendGroupMsg(groupId,"Plugin loaded successfully.",false);
                                return MESSAGE_BLOCK;
                            }
                        }
                    }
                    cq.sendGroupMsg(groupId,"Plugin not found",false);
                    return MESSAGE_BLOCK;
                }
            }

            if(msg.length()>15)
            {
                if(msg.substring(0,14).equals("/plugin unload"))
                {
                    if(!permission_list.contains(userId))
                    {
                        cq.sendGroupMsg(groupId,"Permission denied, authorization limited.",false);
                        return MESSAGE_BLOCK;
                    }
                    String load_plugin = msg.substring(15);
                    SuperPlugin superPlugin;
                    for(int i=0;i<plugin_list.size();i++)
                    {
                        superPlugin = plugin_list.get(i);
                        if(superPlugin.toString().equals(load_plugin))
                        {
                            if(superPlugin.is_enabled)
                            {
                                superPlugin.is_enabled = false;
                                cq.sendGroupMsg(groupId,"Plugin unloaded successfully.",false);
                                return MESSAGE_BLOCK;
                            }
                            else
                            {
                                cq.sendGroupMsg(groupId,"Plugin already unloaded.",false);
                                return MESSAGE_BLOCK;
                            }
                        }
                    }
                    cq.sendGroupMsg(groupId,"Plugin not found",false);
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
