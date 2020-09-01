package com.example.demo.plugin;

import a3lib.SuperPlugin;
import net.lz1998.cq.event.message.CQGroupMessageEvent;
import net.lz1998.cq.event.message.CQPrivateMessageEvent;
import net.lz1998.cq.robot.CoolQ;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Date;

@Component
public class MineSweeperGamePlugin extends SuperPlugin {
    private MineSweeperGame mineSweeperGame;

    private final String HELP_INFO = "欢迎使用扫雷游戏插件(by Golden-Pigeon)\n" +
                                     "你可以使用以下指令：\n" +
                                     "   /mine new x y z 新建一个扫雷游戏，宽x，高y，有z个雷\n" +
                                     "   /mine explore x y 探索指定坐标\n" +
                                     "   /mine flag x y 标记指定坐标为地雷\n" +
                                     "   /mine danger x y 标记指定坐标为问号\n" +
                                     "   /mine unmark x y 取消指定坐标的标记\n" +
                                     "注意：坐标从0开始\n";

    public MineSweeperGamePlugin(){
        plugin_name = "MineSweeperGamePlugin";

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

        long userId = event.getUserId();
        long groupId = event.getGroupId();
        String msg = event.getMessage();

        if(msg.toLowerCase().equals("/mine")){
            cq.sendGroupMsg(groupId, HELP_INFO, false);
            return MESSAGE_BLOCK;
        }
        if(msg.toLowerCase().contains("/mine")){
            MineSweeperGameStatus gs = MineSweeperGameStatus.NORMAL;
            String[] parts = msg.split(" ");
            try {
                switch (parts[1]){
                    case "new":
                        if(mineSweeperGame != null)
                            throw new GameStatusException("游戏已经开始");
                        exam_agr_cnt(parts, 5);
                        mineSweeperGame = new MineSweeperGame(Integer.parseInt(parts[2]), Integer.parseInt(parts[3]), Integer.parseInt(parts[4]));
                        cq.sendGroupMsg(groupId, "新的游戏开始了，本局游戏共有" + Integer.parseInt(parts[4]) + "个地雷", false);
                        break;
                    case "explore":
                        if(mineSweeperGame == null){
                            throw new GameStatusException("游戏尚未开始");
                        }
                        exam_agr_cnt(parts, 4);
                        gs = mineSweeperGame.move(Integer.parseInt(parts[2]), Integer.parseInt(parts[3]), "explore");
                        break;
                    case "flag":
                        if(mineSweeperGame == null){
                            throw new GameStatusException("游戏尚未开始");
                        }
                        exam_agr_cnt(parts, 4);
                        gs = mineSweeperGame.move(Integer.parseInt(parts[2]), Integer.parseInt(parts[3]), "flag");
                        break;
                    case "danger":
                        if(mineSweeperGame == null){
                            throw new GameStatusException("游戏尚未开始");
                        }
                        exam_agr_cnt(parts, 4);
                        gs = mineSweeperGame.move(Integer.parseInt(parts[2]), Integer.parseInt(parts[3]), "danger");
                        break;
                    case "unmark":
                        if(mineSweeperGame == null){
                            throw new GameStatusException("游戏尚未开始");
                        }
                        exam_agr_cnt(parts, 4);
                        gs = mineSweeperGame.move(Integer.parseInt(parts[2]), Integer.parseInt(parts[3]), "unmark");
                        break;
                    default:
                        throw new RuntimeException("未知参数");

                }
            } catch (IllegalArgumentCountException e) {
                cq.sendGroupMsg(groupId, e.getMessage(), false);
                return MESSAGE_BLOCK;
            }catch (NumberFormatException e){
                cq.sendGroupMsg(groupId, "数字格式错误", false);
                return MESSAGE_BLOCK;
            } catch (IllegalArgumentException e){
                cq.sendGroupMsg(groupId, "地雷数量不恰当", false);
                return MESSAGE_BLOCK;
            } catch (GameStatusException e){
                cq.sendGroupMsg(groupId, e.getMessage(), false);
                return MESSAGE_BLOCK;
            } catch (Exception e) {
                cq.sendGroupMsg(groupId, e.getMessage(), false);
                return MESSAGE_BLOCK;
            }
            switch (gs){
                case ILLEGAL:
                    cq.sendGroupMsg(groupId, "非法操作", false);
                    break;
                case UNKNOWN_COMMAND:
                    cq.sendGroupMsg(groupId, "未知命令", false);
                    break;
                case WIN:
                    cq.sendGroupMsg(groupId, "你赢了", false);
                    mineSweeperGame.exploreAll();
                    try {
                        File img = mineSweeperGame.getImgMapAll();
                        cq.sendGroupMsg(groupId, "[CQ:image,file=" + img.getAbsolutePath() + "]", false);
                    } catch (IOException e) {
                        cq.sendGroupMsg(groupId, "获取数据失败", false);
                        return MESSAGE_BLOCK;
                    }

                    mineSweeperGame = null;
                    break;
                case LOSE:
                    cq.sendGroupMsg(groupId, "你输了", false);
                    try {
                        File img = mineSweeperGame.getImgMapAll();
                        cq.sendGroupMsg(groupId, "[CQ:image,file=" + img.getAbsolutePath() + "]", false);
                    } catch (IOException e) {
                        cq.sendGroupMsg(groupId, "获取数据失败", false);
                        return MESSAGE_BLOCK;
                    }
                    mineSweeperGame = null;
                    break;
                case NORMAL:
                    try {
                        File img = mineSweeperGame.getImgMap();
                        cq.sendGroupMsg(groupId, "[CQ:image,file=" + img.getAbsolutePath() + "]", false);
                    } catch (IOException e) {
                        cq.sendGroupMsg(groupId, "获取数据失败", false);
                        return MESSAGE_BLOCK;
                    }
                    break;
            }
            return MESSAGE_BLOCK;
        }

        return MESSAGE_IGNORE;
    }

    private void exam_agr_cnt(String[] args, int bottom, int top) throws IllegalArgumentCountException{
        if(args == null)
            throw new IllegalArgumentCountException("没有参数");
        if(args.length > top)
            throw new IllegalArgumentCountException("参数过多");
        if(args.length < top)
            throw new IllegalArgumentCountException("参数过少");
    }
    private void exam_agr_cnt(String[] args, int cnt) throws IllegalArgumentCountException{
        exam_agr_cnt(args, cnt, cnt);
    }

    static class IllegalArgumentCountException extends IllegalArgumentException{
        public IllegalArgumentCountException(){ }
        public IllegalArgumentCountException(String msg) {super(msg);}
    }

    static class GameStatusException extends RuntimeException{
        public GameStatusException(){ }
        public GameStatusException(String msg) {super(msg);}
    }
}

enum MineSweeperBlockType {
    SPACE(0), HINT_ONE(1), HINT_TWO(2), HINT_THREE(3), HINT_FOUR(4), HINT_FIVE(5), HINT_SIX(6), HINT_SEVEN(7), HINT_EIGHT(8), MINE(9);

    private final int index;

    private MineSweeperBlockType(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }

    public MineSweeperBlockType after(){
        if(index < 8)
            for(MineSweeperBlockType bt : values()){
                if(bt.index == index + 1)
                    return bt;
            }
        return this;
    }

    public MineSweeperBlockType before(){
        if(index > 0 && index <= 8)
            for(MineSweeperBlockType bt : values()){
                if(bt.index == index - 1)
                    return bt;
            }
        return this;
    }

    public boolean isSpace(){
        return this == SPACE;
    }

    public boolean isMine(){
        return this == MINE;
    }

    public boolean isHint(){
        return !isSpace() && !isMine();
    }

}

class MineSweeperGame {
    private final MineSweeperGameMap map;
    private final static double factory = 0.2;
    public MineSweeperGame(int height, int width, int mines){
        if(height > 30 || width > 30 || height < 5 || width < 5){
            throw new IllegalArgumentException("尺寸不恰当");
        }
        if(mines < 1 || mines > factory * height * width)
            throw new IllegalArgumentException("地雷数目不恰当");
        map = new MineSweeperGameMap(height, width);
        int cnt = 0;
        while (cnt != mines) {
            int x = (int) Math.floor(Math.random() * (height - 1)) + 1;
            int y = (int) Math.floor(Math.random() * (width - 1)) + 1;
            if (map.setMine(x, y))
                cnt++;
        }
        System.out.println();
    }

    public String getMap(){
        return map.toString();
    }

    public File getImgMap() throws IOException { return map.getImgMap(); }

    public File getImgMapAll() throws IOException { return map.getImgMapAll(); }

    public void exploreAll(){
        map.exploreAll();
    }

    public MineSweeperGameStatus move(int x, int y, String command){
        boolean lose = false;
        switch (command){
            case "explore":
                lose = !map.explore(x, y);
                break;
            case "flag":
                if(!map.flag(x, y)){
                    System.err.println("非法插旗操作");
                    return MineSweeperGameStatus.ILLEGAL;
                }
                break;
            case "danger":
                if(!map.danger(x, y)){
                    System.err.println("非法标记问号操作");
                    return MineSweeperGameStatus.ILLEGAL;
                }
                break;
            case "unmark":
                if(!map.unexplore(x, y)){
                    System.err.println("非法取消标记操作");
                    return MineSweeperGameStatus.ILLEGAL;
                }
                break;
            default:
                System.err.println("未知命令");
                return MineSweeperGameStatus.UNKNOWN_COMMAND;
        }
        //System.out.println();
        if(lose) {
            return MineSweeperGameStatus.LOSE;
        }
        if(map.won()){
            return MineSweeperGameStatus.WIN;
        }
        return MineSweeperGameStatus.NORMAL;
    }
}

enum MineSweeperGameStatus {
    WIN, LOSE, NORMAL, ILLEGAL, UNKNOWN_COMMAND
}

enum MineSweeperMarkType {
    FLAG, DANGER, EXPLORED, UNEXPLORED
}

class MineSweeperGameMap {
    private final MineSweeperBlockType[][] map;
    private final MineSweeperMarkType[][] mark;
    private final int height;
    private final int width;
    private final int[] x_axis = {-1, 0, 1};
    private final int[] y_axis = {-1, 0, 1};
    private int mineCnt = 0;
    private int discoveredCnt = 0;
    private int exploredCnt = 0;
    private int flagCnt = 0;
    private boolean isWon = false;
    private boolean isLost = false;
    public MineSweeperGameMap(int height, int width){
        this.height = height;
        this.width = width;
        mark = new MineSweeperMarkType[height][width];
        map = new MineSweeperBlockType[height][width];
        for(int i = 0; i < height; i++){
            for(int j = 0; j < width; j++){
                map[i][j] = MineSweeperBlockType.SPACE;
                mark[i][j] = MineSweeperMarkType.UNEXPLORED;
            }
        }
    }

    private void addHint(int x, int y){
        if(outOfRange(x, y))
            return;
        map[x][y] = map[x][y].after();
    }

    private void minusHint(int x, int y){
        if(outOfRange(x, y))
            return;
        map[x][y] = map[x][y].before();
    }

    public boolean setMine(int x, int y){
        if(outOfRange(x, y))
            return false;
        if(map[x][y].isMine())
            return false;
        map[x][y] = MineSweeperBlockType.MINE;
        for(int i = 0; i < 3; i++){
            for(int j = 0; j < 3; j++){
                addHint(x_axis[i] + x, y_axis[j] + y);
            }
        }
        mineCnt++;
        return true;
    }

    /**
     * explore the map
     * @return if there is a mine in the block, return false AND if it is safe, return true.
     */
    public boolean explore(int x, int y){
        if(outOfRange(x, y))
            return true;
        if(map[x][y].isMine()) {
            mark[x][y] = MineSweeperMarkType.EXPLORED;
            isLost = true;
            return false;
        }
        if(mark[x][y] == MineSweeperMarkType.FLAG || mark[x][y] == MineSweeperMarkType.DANGER)
            return true;
        if(mark[x][y] == MineSweeperMarkType.UNEXPLORED){
            mark[x][y] = MineSweeperMarkType.EXPLORED;
            exploredCnt++;
            if(!map[x][y].isHint()) {
//                explore(x + 1, y);
//                explore(x, y + 1);
//                explore(x - 1, y);
//                explore(x, y - 1);
                for(int i = 0; i < 3; i++){
                    for(int j = 0; j < 3; j++){
                        explore(x + x_axis[i], y + y_axis[j]);
                    }
                }
            }
        }
        return true;
    }

    public void exploreAll(){
        for(int i = 0; i < height; i++){
            for(int j = 0; j < width; j++){
                mark[i][j] = MineSweeperMarkType.EXPLORED;
            }
        }
    }

    public boolean flag(int x, int y){
        if(outOfRange(x, y))
            return false;
        if(mark[x][y] != MineSweeperMarkType.UNEXPLORED)
            return false;
        mark[x][y] = MineSweeperMarkType.FLAG;
        if(map[x][y].isMine())
            discoveredCnt++;
        flagCnt++;
        return true;
    }

    public boolean danger(int x, int y){
        if(outOfRange(x, y))
            return false;
        if(mark[x][y] != MineSweeperMarkType.UNEXPLORED)
            return false;
        mark[x][y] = MineSweeperMarkType.DANGER;
        return true;
    }

    public boolean unexplore(int x, int y){
        if(outOfRange(x, y))
            return false;
        if(mark[x][y] == MineSweeperMarkType.EXPLORED || mark[x][y] == MineSweeperMarkType.UNEXPLORED)
            return false;
        if(mark[x][y] == MineSweeperMarkType.FLAG) {
            flagCnt--;
            if(map[x][y].isMine())
                discoveredCnt--;
        }

        mark[x][y] = MineSweeperMarkType.UNEXPLORED;
        return true;
    }

    public boolean won(){
        if(((flagCnt == mineCnt && mineCnt == discoveredCnt) || exploredCnt + mineCnt == height * width) && !isLost){
            isWon = true;
            return true;
        }
        return false;
    }

    private boolean outOfRange(int x, int y){
        return x < 0 || x >= height || y < 0 || y >= width;
    }

//    public void printMap(){
//        System.out.print("   ");
//        for(int i = 0; i < width; i++)
//            System.out.printf("%-3d", i);
//        System.out.println();
//        for(int i = 0; i < height; i++){
//            System.out.printf("%-2d ", i);
//            for(int j = 0; j < width; j++){
//                switch (mark[i][j]){
//                    case UNEXPLORED:
//                        System.out.printf("%-2s ","\u25A0 ");
//                        break;
//                    case FLAG:
//                        System.out.printf("%-2s ","\u2690 ");
//                        break;
//                    case DANGER:
//                        System.out.printf("%-2s ","? ");
//                        break;
//                    default:
//                        if(map[i][j].isSpace())
//                            System.out.printf("%-2s ",". ");
//                        else if(map[i][j].isHint())
//                            System.out.printf("%-2s ",map[i][j].getIndex());
//                        else
//                            System.out.print("\u263C ");
//                }
//            }
//            System.out.println();
//        }
//    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("   ");
        for(int i = 0; i < width; i++)
            sb.append(String.format("%-3d", i));
        sb.append("\n");
        for(int i = 0; i < height; i++){
            sb.append(String.format("%-2d ", i));
            for(int j = 0; j < width; j++){
                switch (mark[i][j]){
                    case UNEXPLORED:
                        sb.append(String.format("%-2s ","\u25A0 "));
                        break;
                    case FLAG:
                        sb.append(String.format("%-2s ","\u2690 "));
                        break;
                    case DANGER:
                        sb.append(String.format("%-2s ","? "));
                        break;
                    default:
                        if(map[i][j].isSpace())
                            sb.append(String.format("%-2s ",". "));
                        else if(map[i][j].isHint())
                            sb.append(String.format("%-2s ",map[i][j].getIndex()));
                        else
                            sb.append("\u263C ");
                }
            }
            sb.append("\n");
        }
        return sb.toString();

    }

    public String getAllMap(){
        StringBuilder sb = new StringBuilder();
        sb.append("   ");
        for(int i = 0; i < width; i++)
            sb.append(String.format("%-3d", i));
        sb.append("\n");
        for(int i = 0; i < height; i++){
            sb.append(String.format("%-2d ", i));
            for(int j = 0; j < width; j++){
                if(map[i][j].isSpace())
                    sb.append(String.format("%-2s ",". "));
                else if(map[i][j].isHint())
                    sb.append(String.format("%-2s ",map[i][j].getIndex()));
                else
                    sb.append("\u263C ");
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    public File getImgMap() throws IOException {
        File file = new File("./tmp/");
        if(!file.exists())
            file.mkdir();
        BufferedImage img = MineSweeperMapDrawer.drawMap(map, mark);
        Date date = new Date();
        String filename = String.valueOf(date.toString().hashCode());
        File imgFile = new File("./tmp/" + filename + ".png");
        ImageIO.write(img, "png", imgFile);
        return imgFile;
    }

    public File getImgMapAll() throws IOException {
        File file = new File("./tmp/");
        if(!file.exists())
            file.mkdir();
        BufferedImage img = MineSweeperMapDrawer.drawMap(map);
        Date date = new Date();
        String filename = String.valueOf(date.toString().hashCode());
        File imgFile = new File("./tmp/" + filename + ".png");
        ImageIO.write(img, "png", imgFile);
        return imgFile;
    }

}

final class MineSweeperMapDrawer {

    private MineSweeperMapDrawer(){}


    private final static int BLOCK_LENGTH = 10;

    public static BufferedImage drawMap(MineSweeperBlockType[][] map, MineSweeperMarkType[][] mark){
        int height = map.length;
        int width = map[0].length;
        if(height != mark.length || width != mark[0].length)
            return null;
        BufferedImage bi = new BufferedImage(BLOCK_LENGTH * (width + 2), BLOCK_LENGTH * (height + 2), BufferedImage.TYPE_BYTE_GRAY);
        Graphics2D gd = (Graphics2D) bi.createGraphics();
        gd.setPaint(Color.WHITE);
        gd.fillRect(0, 0, BLOCK_LENGTH * (width + 2) + 1, BLOCK_LENGTH * (height + 2) + 1);
        gd.setBackground(Color.WHITE);
        gd.setPaint(Color.BLACK);
        Font font = new Font("Times New Roman", Font.PLAIN, 10);

//        System.out.print("   ");
        for(int i = 0; i < width; i++)
            gd.drawString(String.valueOf(i), (i + 2) * BLOCK_LENGTH,  BLOCK_LENGTH);
//        System.out.println();
        for(int i = 0; i < height; i++){
            gd.drawString(String.valueOf(i), BLOCK_LENGTH, (i + 2) * (BLOCK_LENGTH));
            for(int j = 0; j < width; j++){
                switch (mark[i][j]){
                    case UNEXPLORED:
                        gd.drawString("■", (j + 2) * BLOCK_LENGTH, (i + 2) * BLOCK_LENGTH);
                        break;
                    case FLAG:
                        gd.drawString("⚐", (j + 2) * BLOCK_LENGTH, (i + 2) * BLOCK_LENGTH);
                        break;
                    case DANGER:
                        gd.drawString("?", (j + 1) * BLOCK_LENGTH, (i + 1) * BLOCK_LENGTH);
                        break;
                    default:
                        if(map[i][j].isSpace())
                            gd.drawString("□", (j + 2) * BLOCK_LENGTH, (i + 2) * BLOCK_LENGTH);
//                            System.out.printf("%-2s ",". ");
                        else if(map[i][j].isHint())
                            gd.drawString(String.valueOf(map[i][j].getIndex()), (j + 2) * BLOCK_LENGTH, (i + 2) * BLOCK_LENGTH);
                        else
                            gd.drawString("☼", (j + 2) * BLOCK_LENGTH, (i + 2) * BLOCK_LENGTH);
                }
            }
//            System.out.println();
        }
        return bi;

    }

    public static BufferedImage drawMap(MineSweeperBlockType[][] map){
        MineSweeperMarkType[][] mark = new MineSweeperMarkType[map.length][map[0].length];
        for(int i = 0; i < map.length; i++){
            for(int j = 0; j < map[0].length; j++){
                mark[i][j] = MineSweeperMarkType.EXPLORED;
            }
        }
        return drawMap(map, mark);
    }
}