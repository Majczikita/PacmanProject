import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Map extends JFrame {
    public static final char PATH = '0';
    public static final char BORDER = '1';
    public static List<List<Character>> map;
    private static JPanel scorePanel;
    private final MapMenuWindow parentWindow;
    private static JLayeredPane mainPane;

    protected Pacman pacman;

    public Map(String path, MapMenuWindow parentWindow) throws IOException {
        this.parentWindow = parentWindow;
        map = new ArrayList<>();
        setTitle("Pacman");
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        getContentPane().setBackground(Color.black);
        getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
        //set this window size to screen size
        this.setSize(Toolkit.getDefaultToolkit().getScreenSize());

        addCloseListener();
        //add statistics
        loadStatisticsPanel();
        //load and add map
        loadFromFile(path);
        loadMap();
        Entity.clearEntities();

        loadScore();
        JLabel livesLabel = loadLives();
        loadTime();

        //add four ghosts
        if(Block.ghosts!=null) Block.ghosts.clear();
        new Ghost(ColorEnum.RED);
        new Ghost(ColorEnum.PINK);
        new Ghost(ColorEnum.BLUE);
        new Ghost(ColorEnum.ORANGE);

        //add pacman
        pacman = new Pacman(livesLabel, this);
        addKeyListener(pacman);
        GameHandler.startGame();

        //add entities to mainPane
        for(Ghost ghost : Entity.ghosts){
            mainPane.add(ghost, JLayeredPane.POPUP_LAYER);
        }
        mainPane.add(pacman, JLayeredPane.DRAG_LAYER);

        this.setVisible(true);
        //user can't get smaller window then the map size
        this.setMinimumSize(new Dimension(mainPane.getWidth() + 150, scorePanel.getHeight() + mainPane.getHeight() + 200));
    }

    public void loadScore(){
        JLabel scoreLabel = new JLabel();
        editLabel(scoreLabel);
        Thread scoreThread = new Thread(new ScoreThread(scoreLabel, this));
        scoreThread.start();
        scorePanel.add(scoreLabel);
    }

    public JLabel loadLives(){
        JLabel livesLabel = new JLabel();
        editLabel(livesLabel);
        scorePanel.add(livesLabel);
        return livesLabel;
    }

    public void loadTime(){
        JLabel time = new JLabel();
        editLabel(time);
        Thread thread = new Thread(new TimeThread(time));
        thread.start();
        scorePanel.add(time);
    }

    public void editLabel(JLabel lbl){
        lbl.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
        lbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        lbl.setFont(GameHandler.MAIN_FONT);
        lbl.setForeground(Color.WHITE);
    }

    public void loadFromFile(String path) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String s;

            while ((s = br.readLine()) != null) {
                List<Character> row = new ArrayList<>();
                for (int i = 0; i < s.length(); i++) {
                    if (s.charAt(i) == '\n') continue;
                    row.add(s.charAt(i));
                }
                map.add(row);
            }
        }
    }

    public void loadMap(){
        //panel for map
        JPanel mapPanel = new JPanel();
        mapPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mapPanel.setAlignmentY(Component.CENTER_ALIGNMENT);
        mapPanel.setBackground(Color.BLACK);
        add(mapPanel);

        int height = map.size();
        int width = map.getFirst().size();

        mainPane = new JLayeredPane();
        mainPane.setPreferredSize(new Dimension(width * Block.BLOCK_LENGTH, height * Block.BLOCK_LENGTH));
        mapPanel.add(mainPane);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(height, width));
        panel.setBounds(0, 0, width * Block.BLOCK_LENGTH, height * Block.BLOCK_LENGTH);

        int x = 0;
        for(List<Character> row : map){
            int y = 0;
            for(char c : row){
                if(c == PATH){
                    panel.add(new Path(x, y));
                }
                else if(c==BORDER){
                    panel.add(new Border(x, y));
                }
                y++;
            }
            x++;
        }

        mainPane.add(panel, JLayeredPane.DEFAULT_LAYER);
    }

    public void loadStatisticsPanel(){
        scorePanel = new JPanel();
        scorePanel.setLayout(new BoxLayout(scorePanel, BoxLayout.Y_AXIS));
        scorePanel.setPreferredSize(new Dimension(400, 100));
        scorePanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        scorePanel.setAlignmentY(Component.CENTER_ALIGNMENT);
        scorePanel.setBackground(Color.BLACK);
        add(scorePanel);
    }

    public static void addBonus(Bonus bonus){
        mainPane.add(bonus, JLayeredPane.POPUP_LAYER);
    }

    private void addCloseListener(){
        addWindowListener(new WindowAdapter() {
            public void windowClosed(WindowEvent e) {
                parentWindow.mapClosed();
                for(Entity ghost : Entity.ghosts){
                    ghost.setBounds(0,0,0,0);
                }
                Block.clearPaths();
                if(GameHandler.gameOn)
                    GameHandler.endGame();
            }
        });
    }
}
