import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

/**
 * Created by Deividas on 2015-11-03.
 */
public class Main {

    private JFrame frame;
    private Monitor m;
    private static int tableSize = -1; // def - 1
    private static int cubeSize = -1; // def -1
    private static final int WAIT_TIME = 0;
    private boolean ignoreWait = true;
    private boolean showGrid = true;
    private int x1 = 0, y1 = 0;
    private int x2 = tableSize - 1, y2 = 0;
    private int x3 = 0, y3 = tableSize - 1;
    private int x4 = tableSize - 1, y4 = tableSize - 1;
    private ArrayList<Integer> finalList = new ArrayList<>();
    private int maxSize = -1;
    private boolean showSearchDots = true;
    private long start;
    private long stop;

    public static void main(String[] args){
        Main starter = new Main();
        starter.startFrame();
        while (tableSize == -1 && cubeSize == -1) {
            System.out.print("");
        }
        starter.start();
        starter.generateTable();
        starter.startThreads();
    }

    private void startFrame(){
        JFrame startFrame = new JFrame("Nustatymai:");
        startFrame.setLocationRelativeTo(null);
        JLabel label = new JLabel("Įveskite lentelės dydį: ");
        Font bigFont = new Font ("serif", Font. BOLD, 18);
        label.setFont(bigFont);

        JLabel label2 = new JLabel("Įveskite kūbelio dydį: ");
        Font bigFont2 = new Font ("serif", Font. BOLD, 18);
        label.setFont(bigFont);
        label2.setFont(bigFont2);
        JPanel panel = new JPanel();
        JTextField t1 = new JTextField(7);
        JTextField t2 = new JTextField(7);
        bigFont = new Font ("serif", Font. BOLD, 15);
        JButton button = new JButton("Pradėti");
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                tableSize = Integer.parseInt(t1.getText());
                if(tableSize < 20){
                    tableSize = 20;
                }
                cubeSize = Integer.parseInt(t2.getText());
                if(cubeSize < 1){
                    cubeSize = 1;
                }
                startFrame.setVisible(false);
            }
        });

        button.setFont(bigFont);
        panel.add(label);
        panel.add(t1);
        panel.add(label2);
        panel.add(t2);
        panel.add(button);

        startFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        startFrame.getContentPane().add(BorderLayout.CENTER, panel);
        startFrame.setSize(310, 150);
        startFrame.setVisible(true);
    }

    public void start(){
        m = new Monitor();
        frame = new JFrame("Didžiausias plotas");
        frame.add(new Board());
        frame.setSize(cubeSize * tableSize + 20, cubeSize * tableSize + 50);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    public void generateTable(){
        m.board = new int[tableSize][tableSize];
        m.check = new boolean[tableSize][tableSize];

        for(int i=0; i<tableSize; i++){
            for(int j=0; j<tableSize; j++){
                m.board[i][j] = randomWithRange(0, 1);
                m.check[i][j] = false;
            }
        }

//        m.board[tableSize-1][0] = randomWithRange(0,0);
//        m.board[tableSize-1][1] = randomWithRange(0,0);
//        m.board[tableSize-1][2] = randomWithRange(0,0);
//        m.board[tableSize-1][3] = randomWithRange(0,0);
//        m.board[tableSize-1][4] = randomWithRange(0,0);
//        m.board[tableSize-1][5] = randomWithRange(0,0);
//
//        for(int i=0; i<tableSize; i++){
//            m.board[tableSize/2][i] = randomWithRange(0,0);
//            m.board[i][tableSize/2] = randomWithRange(0,0);
//            m.board[i][0] = randomWithRange(0,0);
//            m.board[i][tableSize-1] = randomWithRange(0,0);
//            //m.board[i][tableSize-2] = randomWithRange(0,0);
//            // m.board[i][i] = randomWithRange(0,0);
//            if(i != 0){
//                m.board[i-1][i] = randomWithRange(0,0);
//            }
//            m.board[tableSize-1 - i][i] = randomWithRange(0,0);
//            if(i != tableSize - 1){
//                m.board[tableSize-1 - i-1][i] = randomWithRange(0,0);
//            }
//        }
    }

    private int randomWithRange(int min, int max) {
        int range = Math.abs(max - min) + 1;
        return (int)(Math.random() * range) + (min <= max ? min : max);
    }

    // sukuriamos ir paleidžiamos gijos
    public void startThreads(){
        ArrayList<Integer> list[] = new ArrayList[5];
        initArrayList(list);

        ArrayList<Integer> commonList = new ArrayList<>();

        Searcher s1 = new Searcher(0, 0, 1, list[0]);
        Searcher s2 = new Searcher(tableSize - 1, 0, 2, list[1]);
        Searcher s3 = new Searcher(0, tableSize - 1, 3, list[2]);
        Searcher s4 = new Searcher(tableSize - 1, tableSize - 1, 4, list[3]);
        Searcher s5 = new Searcher((tableSize - 1)/2, (tableSize - 1)/2, 5, list[4]);

        Thread t1 = new Thread(s1);
        Thread t2 = new Thread(s2);
        Thread t3 = new Thread(s3);
        Thread t4 = new Thread(s4);
        Thread t5 = new Thread(s5);

        start = System.currentTimeMillis();

        t1.start();
        t2.start();
        t3.start();
        t4.start();

        //t5.start();

        while (t1.isAlive() || t2.isAlive() || t3.isAlive() || t4.isAlive() || t5.isAlive()){}
        for (int i=0; i<list.length; i++){
            commonList.addAll(list[i]);
        }
        showSearchDots = false;
        showPlot(commonList);
    }

    public ArrayList<Integer>[] initArrayList(ArrayList<Integer>[] list){
        for(int i=0; i<list.length; i++){
            list[i] = new ArrayList<>();
        }
        return list;
    }

    public void showPlot(ArrayList<Integer> commonList){
        for (int i=0; i<tableSize; i++){
            for (int j=0; j<tableSize; j++){
                m.check[i][j] = false;
            }
        }
        frame.repaint();

        int tmp = 0;
        int count = 0;
        ArrayList<Integer> l = new ArrayList<>();
        while (tmp < commonList.size()){
            int xValue = commonList.get(tmp);
            tmp++;
            int yValue = commonList.get(tmp);
            if(xValue > -1 && yValue > -1){
                l.add(xValue);
                l.add(yValue);
                count++;
            }else{
                if(count > maxSize){
                    maxSize = count;
                    finalList = new ArrayList<>(l);
                }
                l.clear();
                count = 0;
            }
            tmp++;
        }
        stop = System.currentTimeMillis();
        System.out.println("Daugiausia kubeliu: " + maxSize );
        System.out.println("Laikas: " + (stop-start));
        int tt = 0;
        while (tt < finalList.size()){
            int xV = finalList.get(tt);
            tt++;
            int yV = finalList.get(tt);
            m.check[xV][yV] = true;
            tt++;
        }
        frame.repaint();
    }

    public class Info{

        public Point p;
        public Point defaultValues;
        public ArrayList<Integer> xSave;
        public ArrayList<Integer> ySave;
        public int color;
        public int index;
        public ArrayList<Integer> list;

        public Info(int x, int y){
            p = new Point(x, y);
            defaultValues = new Point(x, y);
            xSave = new ArrayList<>();
            ySave = new ArrayList<>();
            index = 0;
            list = new ArrayList<>();
        }

        public void remove(int index){
            if(xSave.size() > 0 && index < xSave.size()) {
                xSave.remove(index);
                ySave.remove(index);
            }
        }

        public int getX(int index){
            if(index < xSave.size()) {
                return xSave.get(index);
            }
            return defaultValues.x;
        }

        public int getY(int index){
            if(index < ySave.size()) {
                return ySave.get(index);
            }
            return defaultValues.y;
        }
    }

    // Klasė, kuri naudojama sukuriant gijas
    public class Searcher implements Runnable{

        private int id;
        private Info info;
        private ArrayList<Integer> list;

        public Searcher(int x, int y, int idA, ArrayList<Integer> thisList){
            info = new Info(x, y);
            info.color = m.board[x][y];
            id = idA;
            list = thisList;
        }

        @Override
        public void run() {
            while (m.canContinue()){ // kartoti ciklą tol, kol visas dvimatis masyvas bus nuspalintas
                m.move(info, id, list);
            }
            // kai darbas baigtas užpildyti bendro masyvo x ir y reikšmes -1
            list.add(-1); list.add(-1);
        }
    }

    // realizuojamas monitorius
    public class Monitor{

        private final int SIZE = tableSize * tableSize; // dvimačio masyvo(matricos) dydis;
        volatile private int board[][]; // informacija apie spalvas
        volatile private boolean check[][]; // informacija apie atitinakamą kūbelį (ar jis yra apeitas)
        volatile private boolean cont = true;

        public synchronized boolean canContinue(){
            int oldSize = 0;
            for(int i=0; i<tableSize; i++){
                for(int j=0; j<tableSize; j++){
                    if(check[i][j]){
                        oldSize++;
                    }
                }
            }
            return oldSize < SIZE;
        }

        public synchronized void add(int x, int y, ArrayList<Integer> list){
            boolean add = true;
            int tmp = 0;
            while (tmp < list.size()){
                int xV = list.get(tmp);
                tmp++;
                int yV = list.get(tmp);
                if(xV == x && yV == y){
                    add = false;
                    break;
                }
                tmp++;
            }
            if(add){
                list.add(x); list.add(y);
            }
        }

        // metodas skirtas apeiti visą lentelę
        public synchronized void move(Info info, int id, ArrayList<Integer> list){
            frame.repaint();
            Point p = info.p;
            int color = info.color;

            touchOtherCube(info, list); // jei liečia kitą kūbelį idėti jo koorinates į gijos masyvą

            saveTmpCoorinates(info, color, id);
            setXandY(p, id);
            if (p.y < tableSize - 1 && (board[p.x][p.y] == color &&
                    board[p.x][p.y + 1] == board[p.x][p.y]) &&
                    !check[p.x][p.y + 1] && !check[p.x][p.y]) {
                check[p.x][p.y] = true;
                p.y++;
            } else if (p.x < tableSize - 1 && (board[p.x][p.y] == color &&
                    board[p.x + 1][p.y] == board[p.x][p.y]) &&
                    !check[p.x + 1][p.y] && !check[p.x][p.y]) {
                check[p.x][p.y] = true;
                p.x++;
            } else if (p.x > 0 && (board[p.x][p.y] == color &&
                    board[p.x - 1][p.y] == board[p.x][p.y]) &&
                    !check[p.x - 1][p.y] && !check[p.x][p.y]) {
                check[p.x][p.y] = true;
                p.x--;
            } else if (p.y > 0 && (board[p.x][p.y] == color &&
                    board[p.x][p.y - 1] == board[p.x][p.y]) &&
                    !check[p.x][p.y - 1] && !check[p.x][p.y]) {
                check[p.x][p.y] = true;
                p.y--;
            } else {
                boolean gotoNextMethod = checkAloneCube(info, color, list);
                setCubeChecked(info, list);
                if (gotoNextMethod) {
                    setPointToSavedCoordinate(info, id, list);
                }
            }
            if(!ignoreWait) {
                try {
                    wait(WAIT_TIME);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            touchOtherCube(info, list);
            frame.repaint();
        }

        public synchronized void touchOtherCube(Info i, ArrayList<Integer> list){
            Point p = i.p;
            int color = board[p.x][p.y];
            if(p.x >= 1 && color == board[p.x-1][p.y]){
                add(p.x, p.y, list);
                add(p.x-1, p.y, list);
            }else if(p.x == 0 && color == board[p.x+1][p.y]){
                add(p.x, p.y, list);
                add(p.x+1, p.y, list);
            }
            if(p.x < tableSize-1 && color == board[p.x+1][p.y]){
                add(p.x, p.y, list);
                add(p.x+1, p.y, list);
            }else if(p.x == tableSize-1 && color == board[p.x-1][p.y]){
                add(p.x, p.y, list);
                add(p.x-1, p.y, list);
            }
            if(p.y >= 1 && color == board[p.x][p.y-1]){
                add(p.x, p.y, list);
                add(p.x, p.y-1, list);
            }else if(p.x == 0 && color == board[p.x+1][p.y]){
                add(p.x, p.y, list);
                add(p.x+1, p.y, list);
            }
            if(p.y < tableSize-1 && color == board[p.x][p.y+1]){
                add(p.x, p.y, list);
                add(p.x, p.y+1, list);
            }else if(p.y == tableSize-1 && color == board[p.x][p.y-1]){
                add(p.x, p.y, list);
                add(p.x, p.y-1, list);
            }
        }

        public synchronized void setXandY(Point p, int id){
            switch (id){
                case 1:
                    x1 = p.x;
                    y1 = p.y;
                    break;
                case 2:
                    x2 = p.x;
                    y2 = p.y;
                    break;
                case 3:
                    x3 = p.x;
                    y3 = p.y;
                    break;
                case 4:
                    x4 = p.x;
                    y4 = p.y;
                    break;
            }
        }

        // patikrina pavienius kūbelius(kurie iš visų keturių pusių neturi gretimų kūbelių)
        public synchronized boolean checkAloneCube(Info i, int color, ArrayList<Integer> list){
            boolean c = true;
            Point p = i.p;
            if(!check[p.x][p.y]){
                if(p.y < tableSize-1 && (board[p.x][p.y] == color && board[p.x][p.y+1] == board[p.x][p.y])) {
                    check[p.x][p.y] = true;
                    touchOtherCube(i, list);
                    p.y++;
                    touchOtherCube(i, list);
                    c = false;
                }else if(p.x < tableSize-1 && (board[p.x][p.y] == color && board[p.x+1][p.y] == board[p.x][p.y])){
                    check[p.x][p.y] = true;
                    touchOtherCube(i, list);
                    p.x++;
                    touchOtherCube(i, list);
                    c = false;
                }else if(p.x > 0 && (board[p.x][p.y] == color && board[p.x-1][p.y] == board[p.x][p.y])){
                    check[p.x][p.y] = true;
                    touchOtherCube(i, list);
                    p.x--;
                    touchOtherCube(i, list);
                    c = false;
                }else if(p.y > 0 && (board[p.x][p.y] == color && board[p.x][p.y-1] == board[p.x][p.y])) {
                    check[p.x][p.y] = true;
                    touchOtherCube(i, list);
                    p.y--;
                    touchOtherCube(i, list);
                    c = false;
                }
            }
            return c;
        }

        public synchronized void setCubeChecked(Info i, ArrayList<Integer> list){
            Point p = i.p;
            if(!check[p.x][p.y]){
                check[p.x][p.y] = true;
                touchOtherCube(i, list);
            }
        }

        // kūbelio koordinatės pakeičiamos į neapeitas to paties ploto koordinates
        public synchronized void setPointToSavedCoordinate(Info info, int id, ArrayList<Integer> list){
            Point p = info.p;
            int i = info.index;
            int size = info.xSave.size() + 1;
            if(i < size){
                int tX = info.getX(i);
                int tY = info.getY(i);
                if(!check[tX][tY]){
                    p.x = tX;
                    p.y = tY;
                }else{
                    if(size <= 0){
                        info.index = 0;
                    }
                    p.x = info.defaultValues.x;
                    p.y = info.defaultValues.y;
                }
                info.remove(i);
                info.index++;
            } else {
                if(i >= size){
                    info.index = 0;
                }
                if(cont && size == 1){
                    cont = false;
                    defaultValues(info, id, list);
                }
            }
            frame.repaint();
        }

        // kūbelio koordinatės pakeičiamos į neapeitas koordinates
        public synchronized void defaultValues(Info info, int id, ArrayList<Integer> list){
            Point p = info.p;
            cont = true;
            frame.repaint();
            if(canContinue()) {
                info.index = 0;
                cont = true;
                list.add(-1);
                list.add(-1);
                switch (id){
                    case 1:
                        outer:
                        for (int i = 0; i < tableSize; i++) {
                            for (int j = 0; j < tableSize; j++) {
                                if (!check[i][j]) {
                                    info.color = board[i][j];
                                    p.x = i;
                                    p.y = j;
                                    break outer;
                                }
                            }
                        }
                        break;
                    case 2:
                        outer:
                        for (int i = tableSize - 1; i >= 0; i--) {
                            for (int j = 0; j < tableSize; j++) {
                                if (!check[i][j]) {
                                    info.color = board[i][j];
                                    p.x = i;
                                    p.y = j;
                                    break outer;
                                }
                            }
                        }
                        break;
                    case 3:
                        outer:
                        for (int i = 0; i < tableSize; i++) {
                            for (int j = tableSize - 1; j >= 0; j--) {
                                if (!check[i][j]) {
                                    info.color = board[i][j];
                                    p.x = i;
                                    p.y = j;
                                    break outer;
                                }
                            }
                        }
                        break;
                    case 4:
                        outer:
                        for (int i = tableSize - 1; i >= 0; i--) {
                            for (int j = tableSize - 1; j >= 0; j--) {
                                if (!check[i][j]) {
                                    info.color = board[i][j];
                                    p.x = i;
                                    p.y = j;
                                    break outer;
                                }
                            }
                        }
                        break;
                }
            }
        }

        // išsaugojamos neapeitos to paties ploto koordiantės
        public synchronized void saveTmpCoorinates(Info info, int color, int id){
            int sum = 0;
            Point p = info.p;
            if(p.y < tableSize-1 && (board[p.x][p.y] == color && board[p.x][p.y+1] == board[p.x][p.y]) && !check[p.x][p.y+1]){
                sum++;
            }
            if(p.x < tableSize-1 && (board[p.x][p.y] == color && board[p.x+1][p.y] == board[p.x][p.y]) && !check[p.x+1][p.y]){
                sum++;
            }
            if(p.x > 0 && (board[p.x][p.y] == color && board[p.x-1][p.y] == board[p.x][p.y]) && !check[p.x-1][p.y]){
                sum++;
            }
            if(p.y > 0 && (board[p.x][p.y] == color && board[p.x][p.y-1] == board[p.x][p.y]) && !check[p.x][p.y-1]){
                sum++;
            }
            if(sum > 1){
                if(p.y + 1 < tableSize && !check[p.x][p.y+1] && board[p.x][p.y+1] == color){
                    info.xSave.add(p.x);
                    info.ySave.add((p.y + 1));
                }
                if(p.x + 1 < tableSize && !check[p.x+1][p.y] && board[p.x+1][p.y] == color){
                    info.xSave.add((p.x + 1));
                    info.ySave.add(p.y);
                }
                if(p.x - 1 >= 0 && !check[p.x-1][p.y] && board[p.x-1][p.y] == color){
                    info.xSave.add((p.x - 1));
                    info.ySave.add(p.y);
                }
                if(p.y - 1 >= 0 && !check[p.x][p.y-1] && board[p.x][p.y-1] == color){
                    info.xSave.add(p.x);
                    info.ySave.add((p.y - 1));
                }
            }
        }
    }

    public class Board extends JPanel {
        public void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setColor(Color.RED);
            drawDarkerTable(g2); // piesiama tamsesne lentele
            drawLighterTable(g2); // piesiama sviesesne lentele
            for (int i = 0; i < tableSize; i++) {
                for (int j = 0; j < tableSize; j++) {
                    if (showGrid) {
                        g2.setColor(Color.BLACK);
                        g.drawRect(i * cubeSize, j * cubeSize, cubeSize, cubeSize);
                    }
                }
            }
            if (showSearchDots) {
                g2.setColor(Color.WHITE);
                g2.fillRect(x1 * cubeSize, y1 * cubeSize, cubeSize, cubeSize);
                g2.fillRect(x2 * cubeSize, y2 * cubeSize, cubeSize, cubeSize);
                g2.fillRect(x3 * cubeSize, y3 * cubeSize, cubeSize, cubeSize);
                g2.fillRect(x4 * cubeSize, y4 * cubeSize, cubeSize, cubeSize);
            }
        }

        public void drawDarkerTable(Graphics2D g) {
            for (int i = 0; i < tableSize; i++) {
                for (int j = 0; j < tableSize; j++) {
                    if (m.board[i][j] == 0) {
                        g.setColor(Color.RED.darker().darker().darker().darker());
                    } else if (m.board[i][j] == 1) {
                        g.setColor(Color.GREEN.darker().darker().darker().darker());
                    } else if (m.board[i][j] == 2) {
                        g.setColor(Color.BLUE.darker().darker());
                    } else if (m.board[i][j] == 3) {
                        g.setColor(Color.YELLOW.darker().darker());
                    }
                    g.fillRect(i * cubeSize, j * cubeSize, cubeSize, cubeSize);
                    if (showGrid) {
                        g.setColor(Color.BLACK);
                        g.drawRect(i * cubeSize, j * cubeSize, cubeSize, cubeSize);
                    }
                }
            }
        }

        public void drawLighterTable(Graphics2D g) {
            for (int i = 0; i < tableSize; i++) {
                for (int j = 0; j < tableSize; j++) {
                    if (m.check[i][j]) {
                        int color = m.board[i][j];
                        if (color == 0) {
                            g.setColor(Color.RED);
                        } else if (color == 1) {
                            g.setColor(Color.GREEN.darker());
                        } else if (color == 2) {
                            g.setColor(Color.BLUE);
                        } else if (color == 3) {
                            g.setColor(Color.YELLOW);
                        }
                        g.fillRect(i * cubeSize, j * cubeSize, cubeSize, cubeSize);
                    }
                }
            }
        }
    }
}