package com.example.demo.plugin;

import a3lib.SuperPlugin;
import com.sun.org.apache.bcel.internal.generic.SWITCH;
import net.lz1998.cq.event.message.CQGroupMessageEvent;
import net.lz1998.cq.event.message.CQPrivateMessageEvent;
import net.lz1998.cq.robot.CoolQ;
import net.lz1998.cq.utils.CQCode;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

@Component
public class LegendsOfThreeKingdomPlugin extends SuperPlugin
{
    final static public int DESIGN_SPADE = 0;//黑桃
    final static public int DESIGN_HEART = 1;//红心
    final static public int DESIGN_CLUB = 2;//黑梅
    final static public int DESIGN_DIAMOND = 3;//红方

    final static public int CARD_NONE = -1;//空白牌，用作特殊判定，比如说杀的时候不闪
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

    final static public int JOB_KING = 0;//主公
    final static public int JOB_MINISTER = 1;//忠臣
    final static public int JOB_REBEL = 2;//反贼
    final static public int JOB_SPY = 3;//内奸

            /*
    * 以下是一些会用到的全局变量
    * */
    List<Player> gamer_list;//玩家列表
    long game_group;//限定一局游戏只能在一个群里开（懒得写多个群的了23333
    long admin = 1543127579;//管理员，默认是开发者23333
    int response_amount = 0;//响应人数，用于在南蛮入侵与万箭齐发进行判定
    int alive_amount = 0;//存活人数，用于与上一个进行配套判定
    int alive_rebel_amount = 0;//存活反贼数量
    int alive_spy_amount = 0;//存活内奸数量
    final static Card NONE_CARD = new Card(-1,CARD_NONE,-1,-1,-1);

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
            +"/kingdom show    ----查看你的状态与手牌\n"
            +"/kingdom use [number] [object]      ----通过卡牌编号使用卡牌，其中object项为可选项，需输入对象玩家编号\n"
            +"如果没有相应的对策卡，请输入/kingdom use -1"
            +"/kingdom load [number]    ----通过卡牌编号装备一张装备卡，原有的装备会被替换\n"
            +"/kingdom unload [number]    ----通过卡牌编号卸下一张装备卡\n"
            +"/kingdom end    ----结束你的回合";

    public LegendsOfThreeKingdomPlugin()
    {
        plugin_name = "LegendsOfThreeKingdomPlugin";
        gamer_list = new ArrayList<>();
        card_list = new ArrayList<Card>();
        card_list_bin = new ArrayList<Card>();

        /*
        * 卡牌储存格式如下：
        * [卡牌序号] [卡牌名称] [卡牌点数] [卡牌花色] [卡牌种类]
        * 具体数据对应详见上面
        * */
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
                return MESSAGE_BLOCK;
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
                cq.sendPrivateMsg(userId,
                        "你的游戏ID是："
                                +String.valueOf(player.playerId)
                                +"\n"
                                +"你当前的生命值是："
                                +String.valueOf(player.lives)
                                +"\n"
                                + "你的身份是："
                                +player.getJob()
                                +"\n"
                                +"你当前的状态是："
                                +player.getState()
                                +player.showCard()
                        ,false);
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
                    int objectId = -1;
                    if(args.length>3)
                        objectId = Integer.valueOf(args[3]);
                    if(card_num == -1)
                    {
                        useCard(cq,player,NONE_CARD,objectId);
                        return MESSAGE_BLOCK;
                    }
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
                        cq.sendPrivateMsg(userId,"O▲O！...你并没有这张卡哦~",false);
                        return MESSAGE_BLOCK;
                    }
                    boolean success = useCard(cq,player,card,objectId);
                    if(success)
                        player.card_list.remove(card);
                    else
                        cq.sendPrivateMsg(userId,"卡牌使用失败>  <！",false);
                    return MESSAGE_BLOCK;
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

    /*
    * 对于玩家死亡的判定
    * */
    public void killPlayer(CoolQ cq,Player player)
    {
        player.is_dead = true;
        cq.sendGroupMsg(game_group,
                "玩家"
                        +CQCode.at(player.userId)
                        +"在三国的乱世纷争中阵亡了！\n"
                        +"他的身份是："
                        +player.getJob()
                ,false);
        if(player.job==JOB_KING)
        {
            is_running = false;
            cq.sendGroupMsg(game_group,"主公阵亡，游戏结束>  <！",false);
            cq.sendGroupMsg(game_group,"最后的获胜阵营是："+(alive_rebel_amount==0?"内奸":"反贼"),false);
        }
        if(player.job==JOB_REBEL)
        {
            alive_rebel_amount--;
            cq.sendGroupMsg(game_group,
                    "玩家"
                            +CQCode.at(gamer_list.get(player.killerId).userId)
                            +"击杀反贼！获得奖励三张手牌！"
                    ,false);

        }
        if(player.job==JOB_SPY)
            alive_spy_amount--;
        if(alive_rebel_amount==0&&alive_spy_amount==0)
        {
            is_running = false;
            cq.sendGroupMsg(game_group,"反贼与内奸全部阵亡，游戏结束>  <！",false);
            cq.sendGroupMsg(game_group,"最后的获胜阵营是：主公&忠臣",false);
        }
    }

    /*
    * 假定玩家已有该手牌
    * */
    public boolean useCard(CoolQ cq,Player player, Card card, long objectId)
    {
        switch(card.name)
        {
            case CARD_NONE:
            {
                if(player.being_dead)//不自救
                {
                    killPlayer(cq,player);
                }
                if(player.being_shoot||player.being_wanted)//对于南蛮入侵与万箭齐发的判定
                {
                    response_amount++;
                    if(player.armor.name == CARD_GRASS_ARMOR)//藤甲，永远滴神
                        player.being_shoot = player.being_wanted = false;
                    if(response_amount == alive_amount-1)
                    {
                        Player gamer = null;
                        for(int i = 0;i<gamer_list.size();i++)
                        {
                            gamer = gamer_list.get(i);
                            if(!gamer.is_dead)
                            {
                                if(gamer.being_shoot||gamer.being_wanted)
                                {
                                    cq.sendGroupMsg(game_group,"玩家"+CQCode.at(player.userId)
                                            +"受到了来自"
                                            +(gamer.being_shoot?"万箭齐发":"南蛮入侵")
                                            +"的【1】点伤害！",false);
                                    gamer.lives--;
                                }
                                if(gamer.lives<=0)
                                {
                                    gamer.being_dead = true;
                                }
                            }
                        }
                    }
                }
            }
            case CARD_DRINK:
                if(player.drunk)
                {
                    cq.sendPrivateMsg(player.userId,"你已经喝过酒啦>  <！",false);
                    return false;
                }
                if(!player.enable)
                {
                    cq.sendPrivateMsg(player.userId,"还没到你的出牌时间哦>  <！",false);
                    return false;
                }
                player.drunk = true;
                if(player.being_dead)//濒死阶段打出酒
                {
                    cq.sendGroupMsg(game_group,"玩家"+CQCode.at(player.userId)+"对自己使用了一张酒！",false);
                    player.lives = 1;
                    player.drunk = false;
                }
                break;
            case CARD_KILL:
            case CARD_KILL_FIRE:
            case CARD_KILL_THUNDER:
                if(!player.enable)
                {
                    cq.sendPrivateMsg(player.userId,"还没到你的出牌时间哦>  <！",false);
                    return false;
                }
                if(objectId == -1)
                {
                    cq.sendPrivateMsg(player.userId,"请输入目标ID>   <！",false);
                    return false;
                }
                if(player.has_killed&&player.armor.name!=CARD_UNLIMITED_WEAPON)
                {
                    cq.sendPrivateMsg(player.userId,"你本回合已经出过杀了>   <！",false);
                    return false;
                }
                Player the_killed = null;
                for(int i=0;i<gamer_list.size();i++)
                {
                    if(gamer_list.get(i).playerId == objectId)
                    {
                        the_killed = gamer_list.get(i);
                        break;
                    }
                }
                if(the_killed == null)
                {
                    cq.sendPrivateMsg(player.userId,"请输入正确的目标ID>   <！",false);
                    return false;
                }
                if(the_killed.is_dead)
                {
                    cq.sendPrivateMsg(player.userId,"这个人早就死啦>   <！",false);
                    return false;
                }
                if(the_killed.playerId == player.playerId)
                {
                    cq.sendPrivateMsg(player.userId,"你不能杀你自己>   <！",false);
                    return false;
                }
                /*
                * 下面是十分费力的计算玩家距离的代码
                * 希望有大佬能够帮忙进行优化XD
                * */
                int distance_left = 0, distance_right = 0;
                for(int i=0, position_now = player.playerId,position_next = (player.playerId+1)%gamer_list.size();i<gamer_list.size();i++)
                {
                    distance_left = 1;
                    if(position_next!=objectId)
                    {
                        if(!gamer_list.get(position_next).is_dead)
                            distance_left++;
                        position_next++;
                        position_next+=gamer_list.size();
                        position_next%=gamer_list.size();
                        continue;
                    }
                    if(player.minus_horse!=null)
                        distance_left--;
                    if(the_killed.plus_horse!=null)
                        distance_left++;
                    break;
                }
                for(int i=0, position_now = player.playerId,position_next = (player.playerId+1)%gamer_list.size();i<gamer_list.size();i++)
                {
                    distance_right = 1;
                    if(position_next!=objectId)
                    {
                        if(!gamer_list.get(position_next).is_dead)
                            distance_right++;
                        position_next--;
                        position_next+=gamer_list.size();
                        position_next%=gamer_list.size();
                        continue;
                    }
                    if(player.minus_horse!=null)
                        distance_right--;
                    if(the_killed.plus_horse!=null)
                        distance_right++;
                    break;
                }
                int distance = distance_left>distance_right?distance_right:distance_left;
                int killing_distance = (player.weapon == null || player.weapon.name == CARD_UNLIMITED_WEAPON)?1:(player.weapon.name-CARD_ADD_ONE_WEAPON+1);
                if(distance>killing_distance)
                {
                    cq.sendPrivateMsg(player.userId,"anosa...距离太远你杀不到他哦>  <...",false);
                    return false;
                }
                cq.sendGroupMsg(game_group,"玩家"+ CQCode.at(player.userId)+"对"+CQCode.at(the_killed.userId)
                        +"打出了一张"
                        +(card.name==CARD_KILL?"杀！":(card.name==CARD_KILL_FIRE?"火杀！":"雷杀！"))
                        ,false);
                if(card.color%2==0&&the_killed.armor.name==CARD_SHIELD)
                {
                    cq.sendGroupMsg(game_group,"由于对方玩家装备了仁王盾，本次杀无效>  <！",false);
                    cq.sendPrivateMsg(player.userId,"由于对方玩家装备了仁王盾，本次杀无效>  <！",false);
                    return false;
                }
                if(card.name == CARD_KILL&&the_killed.armor.name == CARD_GRASS_ARMOR)
                {
                    cq.sendGroupMsg(game_group,"由于对方玩家装备了藤甲，本次杀无效>  <！",false);
                    cq.sendPrivateMsg(player.userId,"由于对方玩家装备了藤甲，本次杀无效>  <！",false);
                    return false;
                }
                cq.sendPrivateMsg(player.userId,"使用成功！等待对方玩家反应中...",false);
                cq.sendGroupMsg(game_group,"等待玩家"+CQCode.at(the_killed.userId)+"反应中...",false);
                player.enable = false;
                player.has_killed = true;
                the_killed.being_killing = true;
                the_killed.killing_lives = (card.name==CARD_KILL_FIRE&&the_killed.armor.name==CARD_GRASS_ARMOR)?2:1;
                break;
            case CARD_DODGE:
                if(player.being_killing)
                {
                    cq.sendPrivateMsg(player.userId,"你没被杀也没被万箭齐发你打个🔨闪",false);
                    return false;
                }
                if(player.being_killing)
                {
                    player.being_killing = false;
                    player.killing_lives = -1;
                    gamer_list.get(player.killerId).enable = true;
                }
                if(player.being_shoot)
                {

                }
                cq.sendGroupMsg(game_group,"玩家"+CQCode.at(player.userId)+"打出了一张闪>  <",false);
                return true;
        }
        return true;
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
    long userId;//QQ
    int playerId;//序号
    public int lives = 4;
    public Card weapon = null;
    public Card armor = null;
    public Card plus_horse = null;
    public Card minus_horse = null;
    public int job;//0主公，1忠臣，2反贼，3内奸，-1无身份
    public int killerId = -1;//杀人者id，用以进行权限返还
    public int killing_lives = -1;//被杀的生命点数
    public boolean enable = false;
    public boolean drunk = false;
    public boolean has_killed = false;//已经用过杀了，配合诸葛连弩进行判定（顺便求一个更好的译名
    public boolean being_dead = false;//濒死状态
    public boolean is_dead = false;//你死le
    public boolean being_killing = false;//被杀
    public boolean being_shoot = false;//有人用了万箭齐发
    public boolean being_wanted = false;//有人用了南蛮入侵
    public List<Card> card_list;
    public List<Card> strategy_list;

    public Player(long userId, int job, int playerId)
    {
        this.userId = userId;
        this.job = job;
        this.playerId = playerId;
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

    public String getJob()
    {
        return (job== LegendsOfThreeKingdomPlugin.JOB_KING?"主公":
                job== LegendsOfThreeKingdomPlugin.JOB_MINISTER?"忠臣":
                        job== LegendsOfThreeKingdomPlugin.JOB_REBEL?"反贼":
                                job== LegendsOfThreeKingdomPlugin.JOB_SPY?"内奸":
                                        "无身份");
    }

    public String getState()
    {
        String state = "\n";
        if(being_wanted)
            state += "受到【南蛮入侵】的号召\n";
        if(being_shoot)
            state += "遭到【万箭齐发】的攻击\n";
        if(being_killing)
            state += "正在被杀\n";
        if(being_dead)
            state += "濒死\n";
        if(is_dead)
            state += "死亡\n";
        if(state.equals("\n"))
            state += "无异常状态，存活\n";
        return state;
    }
}