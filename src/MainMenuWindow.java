import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

public class MainMenuWindow extends JFrame {
    private List<JFrame> openFrames;
    private JButton btnNewGame;
    private JButton btnHighScores;
    private JButton btnExit;
    private HighScoresWindow highScores;

    MainMenuWindow(){
        setTitle("Pacman");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(500,500);
        setLocationRelativeTo(null);
        getContentPane().setBackground(Color.black);
        openFrames = new ArrayList<>();

        launchMainMenu();

        this.setVisible(true);
    }

    public void launchMainMenu(){
        //creating panel with BoxLayout
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        panel.setAlignmentX(Component.CENTER_ALIGNMENT);

        //creating buttons
        btnNewGame = createButton("New Game");
        btnHighScores = createButton("High Scores");
        highScoresOnClick(btnHighScores);
        btnExit = createButton("Exit");
        exitOnClick(btnExit);

        //adding buttons to the center of the screen
        panel.add(Box.createVerticalGlue());
        panel.add(btnNewGame);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));
        panel.add(btnHighScores);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));
        panel.add(btnExit);
        panel.add(Box.createVerticalGlue());

        getContentPane().add(panel, BorderLayout.CENTER);
        setVisible(true);
    }

    public JButton createButton(String text){
        JButton btn = new JButton(text);
        btn.setContentAreaFilled(false);
        btn.setForeground(Color.WHITE);
        btn.setBorderPainted(false);
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setPreferredSize(new Dimension(200, 30));
        btn.setFocusable(false);

        Font font = new Font("Arial", Font.BOLD, 20); // Change font family, style, and size as needed
        btn.setFont(font);

        return btn;
    }

    public void exitOnClick(JButton btn){
        btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                closeAllFrames();
            }
        });
    }
    public void highScoresOnClick(JButton btn){
        btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openHighScoresWindow();
            }
        });
    }
    private void openHighScoresWindow() {
        if (highScores == null) {
            highScores = new HighScoresWindow(this);
            highScores.setVisible(true);
            btnHighScores.setEnabled(false);
            openFrames.add(highScores);
        }
    }
    public void windowClosed() {
        btnHighScores.setEnabled(true);
        openFrames.remove(highScores);
        highScores = null;
    }
    private void closeAllFrames() {
        for (JFrame frame : openFrames) {
            frame.dispose();
        }
        openFrames.clear();
        dispose();
    }
}
