import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

// ADDED FOR IMAGES
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

public class MomBirthdayGame extends JPanel implements ActionListener, KeyListener
{
    // Window size
    private final int WIDTH = 800;
    private final int HEIGHT = 600;

    // Player (mom) settings
    private int momX = 350;
    private int momY = 500;
    private int momWidth = 100;
    private int momHeight = 80;
    private int momSpeed = 18;

    // Game state
    private boolean leftPressed = false;
    private boolean rightPressed = false;
    private boolean gameOver = false;
    private boolean win = false;

    private int score = 0;
    private int lives = 3;

    private Timer timer;
    private Random rand = new Random();

    // Falling items
    private ArrayList<FallingItem> items;
    private int tickCount = 0;

    // =========================
    // ADD YOUR PHOTOS / IMAGES HERE
    // =========================
    private BufferedImage momImage;
    private BufferedImage goodItemImage;
    private BufferedImage badItemImage;
    private BufferedImage backgroundImage;
    private BufferedImage winImage;

    public MomBirthdayGame()
    {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(new Color(255, 245, 248));
        setFocusable(true);
        addKeyListener(this);

        items = new ArrayList<FallingItem>();

        // =========================
        // CHANGE THESE FILENAMES TO YOUR OWN PHOTOS IF NEEDED
        // Example:
        // momImage = ImageIO.read(new File("myMomPhoto.png"));
        // =========================
        try
        {
            momImage = ImageIO.read(new File("mom.png"));
            goodItemImage = ImageIO.read(new File("good.png"));
            badItemImage = ImageIO.read(new File("bad.png"));
            backgroundImage = ImageIO.read(new File("background.png"));
            winImage = ImageIO.read(new File("winphoto.png"));
        }
        catch (IOException e)
        {
            System.out.println("One or more images could not be loaded.");
            e.printStackTrace();
        }

        timer = new Timer(30, this);
        timer.start();
    }

    public void actionPerformed(ActionEvent e)
    {
        if (!gameOver && !win)
        {
            updatePlayer();
            updateItems();
            spawnItems();

            if (score >= 100)
            {
                win = true;
            }

            if (lives <= 0)
            {
                gameOver = true;
            }
        }

        repaint();
    }

    private void updatePlayer()
    {
        if (leftPressed)
        {
            momX -= momSpeed;
        }
        if (rightPressed)
        {
            momX += momSpeed;
        }

        // Keep player in bounds
        if (momX < 0)
        {
            momX = 0;
        }
        if (momX + momWidth > WIDTH)
        {
            momX = WIDTH - momWidth;
        }
    }

    private void updateItems()
    {
        Rectangle momRect = new Rectangle(momX, momY, momWidth, momHeight);

        Iterator<FallingItem> it = items.iterator();
        while (it.hasNext())
        {
            FallingItem item = it.next();
            item.y += item.speed;

            Rectangle itemRect = new Rectangle(item.x, item.y, item.size, item.size);

            if (momRect.intersects(itemRect))
            {
                if (item.good)
                {
                    score += 10;
                }
                else
                {
                    lives--;
                }
                it.remove();
            }
            else if (item.y > HEIGHT)
            {
                it.remove();
            }
        }
    }

    private void spawnItems()
    {
        tickCount++;

        if (tickCount % 20 == 0)
        {
            int x = rand.nextInt(WIDTH - 50);
            int y = -40;
            boolean good = rand.nextInt(10) < 7;
            int speed = 4 + rand.nextInt(4);
            int size = 40;

            items.add(new FallingItem(x, y, speed, size, good));
        }
    }

    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        drawBackground(g2);
        drawPlayer(g2);
        drawItems(g2);
        drawHUD(g2);

        if (win)
        {
            drawWinScreen(g2);
        }
        else if (gameOver)
        {
            drawGameOverScreen(g2);
        }
    }

    private void drawBackground(Graphics2D g2)
    {
        // =========================
        // BACKGROUND IMAGE
        // Uses background.png
        // Replace that file with your own image
        // =========================
        if (backgroundImage != null)
        {
            g2.drawImage(backgroundImage, 0, 0, WIDTH, HEIGHT, null);
        }
        else
        {
            GradientPaint gp = new GradientPaint(0, 0, new Color(255, 240, 245),
                                                 0, HEIGHT, new Color(255, 228, 235));
            g2.setPaint(gp);
            g2.fillRect(0, 0, WIDTH, HEIGHT);
        }
    }

    private void drawPlayer(Graphics2D g2)
    {
        // =========================
        // MOM PHOTO USED AS PLAYER
        // Uses mom.png
        // Replace that file with your own photo
        // =========================
        if (momImage != null)
        {
            g2.drawImage(momImage, momX, momY, momWidth, momHeight, null);
        }
        else
        {
            g2.setColor(new Color(219, 112, 147));
            g2.fillRoundRect(momX, momY, momWidth, momHeight, 20, 20);

            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Arial", Font.BOLD, 18));
            g2.drawString("MOM", momX + 25, momY + 45);
        }
    }

    private void drawItems(Graphics2D g2)
    {
        for (FallingItem item : items)
        {
            if (item.good)
            {
                // =========================
                // GOOD ITEM IMAGE
                // Uses good.png
                // Replace with heart / flower / coffee / cake etc
                // =========================
                if (goodItemImage != null)
                {
                    g2.drawImage(goodItemImage, item.x, item.y, item.size, item.size, null);
                }
                else
                {
                    g2.setColor(new Color(255, 105, 180));
                    g2.setFont(new Font("SansSerif", Font.BOLD, 30));
                    g2.drawString("❤", item.x, item.y + item.size);
                }
            }
            else
            {
                // =========================
                // BAD ITEM IMAGE
                // Uses bad.png
                // Replace with stress / chore / bills / traffic etc
                // =========================
                if (badItemImage != null)
                {
                    g2.drawImage(badItemImage, item.x, item.y, item.size, item.size, null);
                }
                else
                {
                    g2.setColor(new Color(120, 120, 120));
                    g2.fillRoundRect(item.x, item.y, item.size, item.size, 10, 10);
                    g2.setColor(Color.WHITE);
                    g2.setFont(new Font("Arial", Font.BOLD, 12));
                    g2.drawString("BAD", item.x + 8, item.y + 24);
                }
            }
        }
    }

    private void drawHUD(Graphics2D g2)
    {
        g2.setColor(new Color(90, 30, 60));
        g2.setFont(new Font("Arial", Font.BOLD, 24));
        g2.drawString("Score: " + score, 30, 40);
        g2.drawString("Lives: " + lives, 30, 75);

        g2.setFont(new Font("Arial", Font.PLAIN, 18));
        g2.drawString("Catch the good things for Mom!", 500, 35);
        g2.drawString("Avoid stress and chores!", 540, 60);
    }

    private void drawWinScreen(Graphics2D g2)
    {
        g2.setColor(new Color(255, 255, 255, 220));
        g2.fillRoundRect(110, 120, 580, 330, 30, 30);

        g2.setColor(new Color(199, 21, 133));
        g2.setFont(new Font("Serif", Font.BOLD, 40));
        g2.drawString("Happy Birthday Mom!", 185, 180);

        // =========================
        // FINAL PHOTO ON WIN SCREEN
        // Uses winphoto.png
        // Replace with your own photo
        // =========================
        if (winImage != null)
        {
            g2.drawImage(winImage, 330, 200, 140, 140, null);
        }

        g2.setColor(new Color(80, 40, 60));
        g2.setFont(new Font("Arial", Font.PLAIN, 22));
        g2.drawString("You caught all the love.", 270, 370);
        g2.drawString("Thank you for everything you do for me.", 190, 405);

        g2.setFont(new Font("Arial", Font.BOLD, 20));
        g2.drawString("Final Score: " + score, 325, 435);
    }

    private void drawGameOverScreen(Graphics2D g2)
    {
        g2.setColor(new Color(255, 255, 255, 220));
        g2.fillRoundRect(150, 180, 500, 220, 30, 30);

        g2.setColor(new Color(178, 34, 34));
        g2.setFont(new Font("Arial", Font.BOLD, 42));
        g2.drawString("Too Much Stress!", 225, 250);

        g2.setColor(new Color(80, 40, 60));
        g2.setFont(new Font("Arial", Font.PLAIN, 24));
        g2.drawString("But you're still the strongest mom.", 185, 300);
        g2.drawString("Press R to try again!", 280, 340);
    }

    public void keyPressed(KeyEvent e)
    {
        int key = e.getKeyCode();

        if (key == KeyEvent.VK_LEFT)
        {
            leftPressed = true;
        }
        if (key == KeyEvent.VK_RIGHT)
        {
            rightPressed = true;
        }

        // Restart if game is over or won
        if (key == KeyEvent.VK_R && (gameOver || win))
        {
            score = 0;
            lives = 3;
            gameOver = false;
            win = false;
            items.clear();
            momX = 350;
        }
    }

    public void keyReleased(KeyEvent e)
    {
        int key = e.getKeyCode();

        if (key == KeyEvent.VK_LEFT)
        {
            leftPressed = false;
        }
        if (key == KeyEvent.VK_RIGHT)
        {
            rightPressed = false;
        }
    }

    public void keyTyped(KeyEvent e)
    {
        // not used
    }

    private class FallingItem
    {
        int x;
        int y;
        int speed;
        int size;
        boolean good;

        public FallingItem(int x, int y, int speed, int size, boolean good)
        {
            this.x = x;
            this.y = y;
            this.speed = speed;
            this.size = size;
            this.good = good;
        }
    }

    public static void main(String[] args)
    {
        JFrame window = new JFrame("Mom Birthday Game");
        MomBirthdayGame game = new MomBirthdayGame();

        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setContentPane(game);
        window.pack();
        window.setLocationRelativeTo(null);
        window.setVisible(true);
    }
}