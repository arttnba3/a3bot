package com.example.demo.plugin;

import a3lib.SuperPlugin;
import net.lz1998.cq.event.message.CQGroupMessageEvent;
import net.lz1998.cq.event.message.CQPrivateMessageEvent;
import net.lz1998.cq.robot.CQPlugin;
import net.lz1998.cq.robot.CoolQ;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import net.lz1998.cq.CQGlobal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class CTFControllerPlugin extends SuperPlugin
{
    long announcer = 2388937043L;
    long group = 1102180069L;

    String usage_info = "用法：\n"
            + "/ctf set [number]: 设置播报群组\n"
            + "多余的参数会被自动丢弃";

    public CTFControllerPlugin()
    {
        plugin_name = "CTFControllerPlugin";
    }

    @PostMapping("/solves")
    public String SolveParam(@RequestParam("name") String name, @RequestParam("chall") String chall, @RequestParam("status") int status)
    {
        if(!is_enabled)
            return "";

        if(status == 0)
            CQGlobal.robots.get(announcer).sendGroupMsg(group,"✅ 恭喜 " + name + " 做出：" + chall +" !",false);
        else
            CQGlobal.robots.get(announcer).sendGroupMsg(group,"⚠ " + name + " 正在爆破题目：" + chall +" !",false);
        System.out.println("------------Solves------------");
        System.out.println("name:"+name);
        System.out.println("chall:"+chall);
        System.out.println("status:"+String.valueOf(status));
        System.out.println("------------Solves------------");
        String result = "";
        return result;
    }

    @PostMapping("/new_user")
    public String UserParam(@RequestParam("name") String name, @RequestParam("email") String email)
    {
        if(!is_enabled)
            return "";

        CQGlobal.robots.get(announcer).sendGroupMsg(group,"👶新用户注册：\n" + name + "\n" + email +"\n管理员注意查验身份！",false);
        String result = "";
        System.out.println("------------User------------");
        System.out.println("name:"+name);
        System.out.println("email:"+email);
        System.out.println("------------User------------");
        return result;
    }

    @PostMapping("/new_chall")
    public String ChallParam(@RequestParam("name") String name, @RequestParam("category") String category, @RequestParam("status") int status)
    {
        if(!is_enabled)
            return "";
        if(status == 0)
            CQGlobal.robots.get(announcer).sendGroupMsg(group,"⭐ 新题目上线：[" + category + "]" + name,false);
        else if(status == 1)
            CQGlobal.robots.get(announcer).sendGroupMsg(group,"\uD83D\uDCAC 题目 [" + category + "]" + name + " 信息更新",false);
        else if(status == 2)
            CQGlobal.robots.get(announcer).sendGroupMsg(group,"♿ 题目 [" + category + "]" + name + " 更新了hints",false);
        else if(status == 3)
            CQGlobal.robots.get(announcer).sendGroupMsg(group,"\uD83D\uDEAE 题目 [" + category + "]" + name + " 已下线",false);
        String result = "";
        System.out.println("------------New_Chall------------");
        System.out.println("name:"+name);
        System.out.println("category:"+category);
        System.out.println("status:"+String.valueOf(status));
        System.out.println("------------New_Chall------------");
        return result;
    }

    @Override
    public int onGroupMessage(CoolQ cq, CQGroupMessageEvent event)
    {
        if (!is_enabled)
            return MESSAGE_IGNORE;

        long user_id = event.getUserId();
        String msg = event.getMessage();
        long groupId = event.getGroupId();
        if(msg.length()>=5&&msg.substring(0,5).equals("/ctf "))
        {
            String[] str = msg.split(" ");
            if(str.length < 3)
            {
                cq.sendGroupMsg(groupId, usage_info, false);
                return MESSAGE_BLOCK;
            }

            if(str[1].equals("set"))
            {
                if(user_id != 1543127579L)
                {
                    cq.sendGroupMsg(groupId, "Permission denied.", false);
                    return MESSAGE_BLOCK;
                }
                try
                {
                    long val = Long.parseLong(str[2]);
                    group = val;
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                    cq.sendGroupMsg(groupId, usage_info, false);
                    return MESSAGE_BLOCK;
                }

                cq.sendGroupMsg(groupId, "播报群组已设置为：" + String.valueOf(group), false);
                return MESSAGE_BLOCK;
            }
        }

        return MESSAGE_IGNORE;
    }

    @Override
    public int onPrivateMessage(CoolQ cq, CQPrivateMessageEvent event)
    {
        if (!is_enabled)
            return MESSAGE_IGNORE;

        long user_id = event.getUserId();
        String msg = event.getMessage();
        if(msg.length()>=5&&msg.substring(0,5).equals("/ctf "))
        {
            String[] str = msg.split(" ");
            if(str.length < 3)
            {
                cq.sendPrivateMsg(user_id, usage_info, false);
                return MESSAGE_BLOCK;
            }

            if(str[1].equals("set"))
            {
                if(user_id != 1543127579L)
                {
                    cq.sendPrivateMsg(user_id, "Permission denied.", false);
                    return MESSAGE_BLOCK;
                }
                try
                {
                    long val = Long.parseLong(str[2]);
                    group = val;
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                    cq.sendPrivateMsg(user_id, usage_info, false);
                    return MESSAGE_BLOCK;
                }

                cq.sendPrivateMsg(user_id, "播报群组已设置为：" + String.valueOf(group), false);
                return MESSAGE_BLOCK;
            }
        }

        return MESSAGE_IGNORE;
    }
}

