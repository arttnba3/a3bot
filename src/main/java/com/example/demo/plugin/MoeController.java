package com.example.demo.plugin;

import net.lz1998.cq.CQGlobal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class MoeController
{
    @PostMapping("/solves")
    public String SolveParam(@RequestParam("name") String name, @RequestParam("chall") String chall, @RequestParam("status") int status)
    {
        if(1 == 1)
            return "";
        long announcer = 2388937043L;
        long group = 1102180069L;
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
        if(1 == 1)
            return "";
        long announcer = 2388937043L;
        long group = 1002807498L;
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
        if(1 == 1)
            return "";
        long announcer = 2388937043L;
        long group = 1102180069L;
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
}

