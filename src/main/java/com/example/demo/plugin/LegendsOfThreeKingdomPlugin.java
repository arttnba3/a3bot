package com.example.demo.plugin;

import a3lib.SuperPlugin;
import net.lz1998.cq.event.message.CQGroupMessageEvent;
import net.lz1998.cq.event.message.CQPrivateMessageEvent;
import net.lz1998.cq.robot.CoolQ;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

@Component
public class LegendsOfThreeKingdomPlugin extends SuperPlugin
{
    List<Player> gamer_list;
    long game_group;//限定一局游戏只能在一个群里开（懒得写多个群的了23333
    long admin = 1543127579;

    List<Card> card_list;
    List<Card> card_list_bin;

    boolean is_running = false;

    String help_info = "欢迎使用a3开发的简易三国杀游戏插件！\n"
            +"以下是通用指令：\n"
            +"/kingdom    ----获取帮助面板\n"
            +"以下是可以使用的群组指令"
            +"/kingdom new     ----开始一场新的游戏（人数必须为五人）\n"
            +"/kingdom join    ----在游戏招募阶段加入游戏\n"
            +"/kingdom stop    ----强制结束一场游戏（仅限管理员）\n"
            +"/kingdom next    ----强制进入下一个玩家的回合（仅限管理员）\n"
            +"以下是可以使用的私聊指令（游戏开始后）\n"
            +"/kingdom show    ----查看你的手牌\n"
            +"/kingdom use [number] [object]      ----通过卡牌编号使用卡牌，其中object项为可选项，需输入对象玩家编号\n"
            +"/kingdom load [number]    ----通过卡牌编号装备一张装备卡，原有的装备会被替换\n"
            +"/kingdom unload [number]    ----通过卡牌编号卸下一张装备卡";

    public LegendsOfThreeKingdomPlugin()
    {
        plugin_name = "LegendsOfThreeKingdomPlugin";
        gamer_list = new ArrayList<>();
        card_list = new ArrayList<Card>();
        card_list_bin = new ArrayList<Card>();

        File file = new File("data/kingdom_legend_card_list.txt");
        try (FileInputStream fileInputStream = new FileInputStream(file))
        {
            int ch,temp = 0;
            int[] data = new int[4];
            int num = 0;
            while ((ch = fileInputStream.read())!=-1)
            {
                if(ch == ' ')
                {
                    data[num] = temp;
                    temp = 0;
                    num++;
                    continue;
                }
                if(ch == '\n')
                {
                    card_list.add(new Card(data[0],data[1],data[2],data[3],data[4]));
                    num = 0;
                    continue;
                }
                temp*=10;
                temp+=ch-'0';
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public int onPrivateMessage(CoolQ cq, CQPrivateMessageEvent event)
    {
        long userId = event.getUserId();
        String msg = event.getMessage();

        if(msg.length()>=8&&msg.substring(0,8).equals("/kingdom"))
        {
            if(msg.length() == 8)
            {
                cq.sendPrivateMsg(userId,help_info,false);
                return MESSAGE_BLOCK;
            }
            if(!is_running)
            {
                cq.sendPrivateMsg(userId,"当前还没有开始一场游戏哦~",false);
                return MESSAGE_IGNORE;
            }
            String[] args = msg.split(" ");
            if(args.length == 1)
            {
                cq.sendPrivateMsg(userId,help_info,false);
                return MESSAGE_BLOCK;
            }

            Player player = null;
            for(int i = 0;i<gamer_list.size();i++)
            {
                if(userId == gamer_list.get(i).userId)
                {
                    player = gamer_list.get(i);
                    break;
                }
            }
            if(player == null)
            {
                cq.sendPrivateMsg(userId,"anosa...您似乎不是本场游戏的玩家哦...",false);
                return MESSAGE_BLOCK;
            }

            if(args[1].equals("show"))
            {
                cq.sendPrivateMsg(userId,player.showCard(),false);
                return MESSAGE_BLOCK;
            }
            if(args[1].equals("use"))
            {
                if(args.length == 2)
                {
                    cq.sendPrivateMsg(userId,"/kingdom use [number] [object]      ----通过卡牌编号使用卡牌，其中object项为可选项，需输入对象玩家编号",false);
                    return MESSAGE_BLOCK;
                }
                try
                {
                    int card_num = Integer.parseInt(args[2]);
                    Card card = null;
                    for(int i = 0;i<player.card_list.size();i++)
                    {
                        if(player.card_list.get(i).number == card_num)
                        {
                            card = player.card_list.get(i);
                            break;
                        }
                    }
                    if(card == null)
                    {
                        cq.sendPrivateMsg(userId,"你并没有这张卡哦~",false);
                        return MESSAGE_BLOCK;
                    }
                    switch(card.name)
                    {

                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                    cq.sendPrivateMsg(userId,"/kingdom use [number] [object]      ----通过卡牌编号使用卡牌，其中object项为可选项，需输入对象玩家编号",false);
                    return MESSAGE_BLOCK;
                }
            }
        }
        return MESSAGE_IGNORE;
    }

    @Override
    public int onGroupMessage(CoolQ cq, CQGroupMessageEvent event)
    {
        long userId = event.getUserId();
        long groupId = event.getGroupId();
        String msg = event.getMessage();
        if(msg.length()>=8&&msg.substring(0,8).equals("/kingdom"))
        {
            if(msg.length() == 8)
            {
                cq.sendGroupMsg(groupId,help_info,false);
                return MESSAGE_BLOCK;
            }
        }
        return MESSAGE_IGNORE;
    }


    public void useCard(CoolQ cq,long userId, long objectId)
    {

    }
}

class Card
{
    public int number;//编号
    public int name;//卡牌名字
    public int digit;//点数
    public int color;//花色
    public int type;//种类
    public Card(int number, int name, int digit, int color, int type)
    {
        this.number = number;
        this.name = name;
        this.digit = digit;
        this.color = color;
        this.type = type;
    }

    final static public int DESIGN_SPADE = 0;//黑桃
    final static public int DESIGN_HEART = 1;//红心
    final static public int DESIGN_CLUB = 2;//黑梅
    final static public int DESIGN_DIAMOND = 3;//红方

    final static public int CARD_DRINK = 0;//酒
    final static public int CARD_KILL = 1;//杀
    final static public int CARD_KILL_FIRE = 2;//火杀
    final static public int CARD_KILL_THUNDER = 3;//雷杀
    final static public int CARD_DODGE = 4;//闪
    final static public int CARD_PEACH = 5;//桃
    final static public int CARD_NONE_FOOD = 6;//兵粮寸断
    final static public int CARD_BRIDGE_DESTROY = 7;//过河拆桥
    final static public int CARD_FIRE_ATTACK = 8;//火攻
    final static public int CARD_LEND_KNIFE_KILL_MAN = 9;//借刀杀人
    final static public int CARD_DUEL = 10;//决斗
    final static public int CARD_SO_HAPPY = 11;//乐不思蜀
    final static public int CARD_SOUTHERN_INVADE = 12;//南蛮入侵
    final static public int CARD_THUNDER = 13;//闪电
    final static public int CARD_GET_A_SHEEP = 14;//顺手牵羊
    final static public int CARD_RECOVERY_ALL = 15;//桃园结义
    final static public int CARD_IRON_LINK = 16;//铁索连环
    final static public int CARD_ARROWS_RAIN = 17;//万箭齐发
    final static public int CARD_UNAVAILABLE = 18;//无懈可击
    final static public int CARD_GET_TWO_CARD = 19;//无中生有
    final static public int CARD_HARVEST = 20;//五谷丰登
    final static public int CARD_EIGHT_TRIGRAMS = 21;//八卦阵
    final static public int CARD_SILVER_LION = 22;//白银狮子
    final static public int CARD_SHIELD = 23;//仁王盾
    final static public int CARD_GRASS_ARMOR = 24;//藤甲
    final static public int CARD_ADD_ONE_HORSE = 25;//+1🐎
    final static public int CARD_SUB_ONE_HORSE = 26;//-1🐎
    final static public int CARD_ADD_ONE_WEAPON = 27;//+1🔪
    final static public int CARD_ADD_TWO_WEAPON = 28;//+2🔪
    final static public int CARD_ADD_THREE_WEAPON = 29;//+3🔪
    final static public int CARD_ADD_FOUR_WEAPON = 30;//+4🔪
    final static public int CARD_ADD_FIVE_WEAPON = 31;//+5🔪
    final static public int CARD_UNLIMITED_WEAPON = 32;//诸葛连弩


    final static public int TYPE_BASIC = 0;//基本牌
    final static public int TYPE_STRATEGY = 1;//锦囊牌

    @Override
    public String toString()
    {
        String card_info = String.valueOf(this.number)+" ";
        switch(name)
        {
            case 0:
                card_info += "酒 ";
                break;
            case 1:
                card_info += "杀 ";
                break;
            case 2:
                card_info += "火杀 ";
                break;
            case 3:
                card_info += "雷杀 ";
                break;
            case 4:
                card_info += "闪 ";
                break;
            case 5:
                card_info += "桃 ";
                break;
            case 6:
                card_info += "兵粮寸断 ";
                break;
            case 7:
                card_info += "过河拆桥 ";
                break;
            case 8:
                card_info += "火攻 ";
                break;
            case 9:
                card_info += "借刀杀人 ";
                break;
            case 10:
                card_info += "决斗 ";
                break;
            case 11:
                card_info += "乐不思蜀 ";
                break;
            case 12:
                card_info += "南蛮入侵 ";
                break;
            case 13:
                card_info += "闪电 ";
                break;
            case 14:
                card_info += "顺手牵羊 ";
                break;
            case 15:
                card_info += "桃园结义 ";
                break;
            case 16:
                card_info += "铁索连环 ";
                break;
            case 17:
                card_info += "万箭齐发 ";
                break;
            case 18:
                card_info += "无懈可击 ";
                break;
            case 19:
                card_info += "无中生有 ";
                break;
            case 20:
                card_info += "五谷丰登 ";
                break;
            case 21:
                card_info += "八卦阵 ";
                break;
            case 22:
                card_info += "白银狮子 ";
                break;
            case 23:
                card_info += "仁王盾 ";
                break;
            case 24:
                card_info += "藤甲 ";
                break;
            case 25:
                card_info += "+1🐎 ";
                break;
            case 26:
                card_info += "-1🐎 ";
                break;
            case 27:
                card_info += "+1🔪 ";
                break;
            case 28:
                card_info += "+2🔪 ";
                break;
            case 29:
                card_info += "+3🔪 ";
                break;
            case 30:
                card_info += "+4🔪 ";
                break;
            case 31:
                card_info += "+5🔪 ";
                break;
            case 32:
                card_info += "诸葛连弩 ";
                break;
        }

        switch(color)
        {
            case 0:
                card_info += "黑桃♠";
                break;
            case 1:
                card_info += "红心♥";
                break;
            case 2:
                card_info += "梅花♣";
                break;
            case 3:
                card_info += "方块♦";
                break;
        }
        card_info += String.valueOf(digit) + " ";

        switch(type)
        {
            case 0:
                card_info += "基本牌";
                break;
            case 1:
                card_info += "锦囊牌";
                break;
        }

        return card_info;
    }
}

class Player
{
    long userId;
    public int lives = 4;
    public int weapon = -1;
    public int armor = -1;
    public int job;//0主公，1忠臣，2反贼，3内奸，-1无身份
    public boolean enable = false;
    public boolean drunk = false;
    public boolean being_killing = false;
    public List<Card> card_list;
    public List<Card> strategy_list;
    public Player(long userId, int job)
    {
        this.userId = userId;
        this.job = job;
        card_list = new ArrayList<>();
        strategy_list = new ArrayList<>();
    }

    public String showCard()
    {
        String msg = "您当前一共有 " + String.valueOf(card_list.size()) + " 张卡：\n";
        for(int i=0;i<card_list.size();i++)
            msg += card_list.get(i).toString()+"\n";
        return msg;
    }
}