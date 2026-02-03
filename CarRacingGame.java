import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;
import javax.sound.sampled.*;
import java.io.File;

public class CarRacingGame extends JPanel implements ActionListener, KeyListener {

    Timer timer;
    Random rand = new Random();

    // Player
    int playerX = 180;
    int playerY = 380;

    // Enemies
    int[] enemyX = {50, 150, 250};
    int[] enemyY = {-200, -400, -600};

    // Road lines
    int[] lineY = {0, 150, 300, 450};

    int speed = 5;
    int score = 0;
    boolean gameOver = false;

    JButton restartBtn;

    public CarRacingGame() {
        setFocusable(true);
        addKeyListener(this);
        setLayout(null);

        restartBtn = new JButton("Restart");
        restartBtn.setBounds(150, 220, 100, 40);
        restartBtn.setVisible(false);
        restartBtn.addActionListener(e -> restartGame());
        add(restartBtn);

        timer = new Timer(20, this);
        timer.start();
    }

    // 🔊 Crash sound
    void playCrashSound() {
        try {
            AudioInputStream audio =
                    AudioSystem.getAudioInputStream(new File("crash.wav"));
            Clip clip = AudioSystem.getClip();
            clip.open(audio);
            clip.start();
        } catch (Exception e) {
            System.out.println("Sound error: " + e);
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        // Road
        g2.setColor(Color.DARK_GRAY);
        g2.fillRect(0, 0, 400, 500);

        // Lane lines
        g2.setColor(Color.WHITE);
        for (int y : lineY) {
            g2.fillRect(195, y, 10, 60);
        }

        // 🚗 Player car (realistic)
        g2.setColor(Color.BLUE);
        g2.fillRoundRect(playerX, playerY, 40, 80, 10, 10);

        g2.setColor(Color.CYAN);
        g2.fillRect(playerX + 8, playerY + 10, 24, 20);

        g2.setColor(Color.BLACK);
        g2.fillOval(playerX - 5, playerY + 10, 10, 15);
        g2.fillOval(playerX + 35, playerY + 10, 10, 15);
        g2.fillOval(playerX - 5, playerY + 55, 10, 15);
        g2.fillOval(playerX + 35, playerY + 55, 10, 15);

        // 🚓 Enemy cars
        for (int i = 0; i < enemyX.length; i++) {
            g2.setColor(Color.RED);
            g2.fillRoundRect(enemyX[i], enemyY[i], 40, 80, 10, 10);

            g2.setColor(Color.BLACK);
            g2.fillOval(enemyX[i] - 5, enemyY[i] + 10, 10, 15);
            g2.fillOval(enemyX[i] + 35, enemyY[i] + 10, 10, 15);
            g2.fillOval(enemyX[i] - 5, enemyY[i] + 55, 10, 15);
            g2.fillOval(enemyX[i] + 35, enemyY[i] + 55, 10, 15);
        }

        // Score
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.BOLD, 16));
        g2.drawString("Score: " + score, 10, 20);

        // Game Over
        if (gameOver) {
            g2.setFont(new Font("Arial", Font.BOLD, 32));
            g2.drawString("GAME OVER", 90, 200);
            restartBtn.setVisible(true);
            timer.stop();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        // Move lane lines
        for (int i = 0; i < lineY.length; i++) {
            lineY[i] += speed;
            if (lineY[i] > 500) lineY[i] = -60;
        }

        // Move enemies
        for (int i = 0; i < enemyX.length; i++) {
            enemyY[i] += speed;

            if (enemyY[i] > 500) {
                enemyY[i] = -rand.nextInt(300);
                enemyX[i] = rand.nextInt(360);
                score++;
                if (score % 5 == 0) speed++;
            }

            Rectangle player =
                    new Rectangle(playerX, playerY, 40, 80);
            Rectangle enemy =
                    new Rectangle(enemyX[i], enemyY[i], 40, 80);

            if (player.intersects(enemy) && !gameOver) {
                playCrashSound();
                gameOver = true;
            }
        }

        repaint();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (!gameOver) {
            if (e.getKeyCode() == KeyEvent.VK_LEFT && playerX > 0)
                playerX -= 20;
            if (e.getKeyCode() == KeyEvent.VK_RIGHT && playerX < 360)
                playerX += 20;
        }
    }

    void restartGame() {
        playerX = 180;
        speed = 5;
        score = 0;
        gameOver = false;

        for (int i = 0; i < enemyY.length; i++)
            enemyY[i] = -rand.nextInt(300);

        restartBtn.setVisible(false);
        timer.start();
        requestFocusInWindow();
    }

    @Override public void keyReleased(KeyEvent e) {}
    @Override public void keyTyped(KeyEvent e) {}

    public static void main(String[] args) {
        JFrame frame = new JFrame("Car Racing Game");
        CarRacingGame game = new CarRacingGame();

        frame.add(game);
        frame.setSize(400, 500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setVisible(true);
    }
}
