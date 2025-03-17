import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;

public class RollingBallGame extends Frame implements Runnable, KeyListener {
    int ballX = 50, ballY = 270; //ball co-ordinates to match with ground
    int ballSize = 30;
    int ballVelocity = 0;
    boolean isJumping = false;

    int ground = 300;
    ArrayList<Rectangle> obstacles;
    Random random = new Random();

    int obstacleSpeed = 5;
    int score = 0;
    boolean gameOver = false ,gameStarted = false;

    boolean running = false; // for game loop

    public RollingBallGame() {
        setTitle("Rolling Ball Game");
        setSize(800, 400);
        setVisible(true);
        addKeyListener(this);

        obstacles = new ArrayList<>();
        getObstacles();

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                running = false;
                System.exit(0);
            }
        });
    }

    void startGame() {
        //initial ball positions after every start
        ballX = 50;
        ballY = 270;
        ballVelocity = 0;
        isJumping = false;
        obstacleSpeed = 5;
        score = 0;
        gameOver = false;
        obstacles.clear();
        getObstacles();

        if (!running) {
            running = true;
            new Thread(this).start();
        }
    }

    void getObstacles() {
        int obstacleWidth = 40;
        int obstacleHeight = random.nextInt(50) + 20;
        obstacles.add(new Rectangle(800, ground-obstacleHeight, obstacleWidth, obstacleHeight));
    }

    public void paint(Graphics g) {
        g.setColor(new Color(135, 206, 235)); // color for sky
        g.fillRect(0, 0, getWidth(), getHeight());

        g.setColor(new Color(124, 252, 0)); // clor for green ground
        g.fillRect(0, ground, getWidth(), 100);

        if (!gameStarted) {
            //when game is not started
            g.setColor(Color.BLACK);
            g.setFont(new Font("Arial", Font.BOLD, 30));
            g.drawString("Press SPACE to Start!", 250, 200);
            return;
        }

        if (gameOver) {
            //when game is over
            g.setColor(Color.RED);
            g.setFont(new Font("Arial", Font.BOLD, 30));
            g.drawString("Game Over! Final Score: " + score, 200, 200);
            g.setFont(new Font("Arial", Font.BOLD, 20));
            g.drawString("Press SPACE to Restart", 250, 250);
            return;
        }

        // Drawing ball
        g.setColor(Color.BLUE);
        g.fillOval(ballX, ballY, ballSize, ballSize);

        // Drawing obstacles
        g.setColor(Color.RED);
        for (Rectangle obj : obstacles) {
            g.fillRect(obj.x, obj.y, obj.width, obj.height);
        }

        // Drawing score
        //drawScore(g);
    }

    // void drawScore(Graphics g) {
    //     g.setColor(Color.BLACK);
    //     g.setFont(new Font("Arial", Font.BOLD, 20));
    //     g.drawString("Score: " + score, 10, 50);
    // }

    void updateGame() {
        if (gameOver || !gameStarted) return;

        //For the ball to come down
        ballVelocity += 1;
        ballY += ballVelocity;

        // Keep ball on the ground
        if (ballY > ground - ballSize) {
            ballY = ground - ballSize;
            isJumping = false;
            ballVelocity = 0;
        }

        //For Moving obstacles
        //here i am moving obstacles and the ball just jumps vertically to avoid obstacles
        for (int i = 0; i < obstacles.size(); i++) {
            Rectangle obstacle = obstacles.get(i);
            obstacle.x -= obstacleSpeed;

            // Checkecking for any collisions
            if (new Rectangle(ballX, ballY, ballSize, ballSize).intersects(obstacle)) {
                gameOver = true;
                running = false; //if there then stop the game loop
                repaint();
                return;
            }

            // Removing the obstacles which are alraedy passed from the list
            if (obstacle.x + obstacle.width < 0) {
                obstacles.remove(i);
                score++;
                if (score % 3 == 0) obstacleSpeed++; // increasing speed for every 3 points
                getObstacles();
            }
        }
    }

    public void run() {
        //loop infinite until you are out
        while (running) {
            updateGame();
            repaint();

            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
                System.out.println(e);
            }
        }
    }

    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            if (!gameStarted) {
                gameStarted = true;
                startGame();
            } else if (gameOver) {
                startGame();
            } else if (!isJumping) {
                ballVelocity = -15; // jump velocity
                isJumping = true;
            }
        }
    }

    public void keyReleased(KeyEvent e) {}
    public void keyTyped(KeyEvent e) {}

    public static void main(String[] args) {
        new RollingBallGame();
    }
}
