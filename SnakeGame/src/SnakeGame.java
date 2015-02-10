import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Label;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.Toolkit;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import com.snake.listener.SnakeListener;

public class SnakeGame {
	public static void main(String[] args) {

		JFrame jFrame = new JFrame("贪吃蛇");

		new SnakeMenuBar(jFrame);

		// Center
		Toolkit tk = Toolkit.getDefaultToolkit();
		Dimension dt = tk.getScreenSize();
		int frameX = (int) ((dt.getWidth() - Global.WIDTH * Global.CELL_SIZE) / 2);
		int frameY = (int) ((dt.getHeight() - Global.HEIGHT * Global.CELL_SIZE) / 2);

		jFrame.setLayout(new BorderLayout());
		jFrame.setBackground(Color.gray);
		jFrame.setBounds(frameX, frameY, Global.WIDTH * Global.CELL_SIZE + 5,
				Global.HEIGHT * Global.CELL_SIZE + 45);
		jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jFrame.add(new Snake());

		jFrame.setResizable(false);
		jFrame.setVisible(true);
	}
}

class Global {
	public static final int CELL_SIZE = 20;
	public static final int HEIGHT = 20;
	public static final int WIDTH = 30;

}

class Snake extends JPanel {

	private int oldDir, newDir, tail;
	private int headX, tailX;
	private int headY, tailY;
	private int speed;
	private int totalScore;
	private Label score;
	private Label level;
	private SnakeListener sl;
	private Food food;
	int[][] snakeLocation = new int[Global.WIDTH][Global.HEIGHT];
	public final int UP = 1, DOWN = 4, LEFT = 2, RIGHT = 3;
	public final int FOOD = 5;
	public final int GROUND = -1;

	Thread snakeThread = null;
	private int eatScore = 20;
	public static Enum stat;

	public Snake() {
		// TODO Auto-generated constructor stub

		// set center
		setLayout(new FlowLayout());

		Label lbScore = new Label("Score:");
		Label lbLevel = new Label("Level:");
		level = new Label("1");
		score = new Label("0");
		add(lbScore);
		add(score);
		add(lbLevel);
		add(level);
		setBounds(Global.CELL_SIZE, Global.CELL_SIZE, Global.WIDTH
				* Global.CELL_SIZE, Global.HEIGHT * Global.CELL_SIZE);
		setBackground(Color.GRAY);
		setFocusable(true);
		init();
		groundInit();
	}

	public int speedUp() {
		return (Integer.parseInt(level.getText().trim()))>12?11:(Integer.parseInt(level.getText().trim()) + 1);
	}

	public void init() {

		speed = Integer.parseInt(level.getText().trim());

		snakeLocation[2][4] = RIGHT;
		snakeLocation[3][4] = RIGHT;
		snakeLocation[4][4] = RIGHT;
		headX = 4;
		headY = 4;
		tailX = 2;
		tailY = 4;

		oldDir = RIGHT;
		newDir = RIGHT;

		food = new Food();

		// System.out.println(food.getFoodX()+" "+food.getFoodY());
		snakeLocation[food.getFoodX()][food.getFoodY()] = FOOD;

		snakeThread = new Thread(new SnakeDriver());
		stat = Game.START;
		snakeThread.start();
	}

	// 获取蛇头方向
	public int getDirection() {
		return oldDir;
	}

	public Boolean isEatFood(int foodX, int foodY) {
		if (headX == foodX && headY == foodY) {
			return true;
		} else {
			return false;
		}
	}

	public Boolean isEatBody() {

		for (int x = 1; x < Global.WIDTH - 1; x++) {
			for (int y = 1; y < Global.HEIGHT - 1; y++) {
				if (snakeLocation[headX][headY] > 0
						&& snakeLocation[headX][headY] < 5) {
					System.out.println("eat self");
					return true;
				}
			}
		}
		return false;

	}

	public void eatFood() {
		// TODO Auto-generated method stub
		// System.out.println("nibeichile ");

		int foodX = food.getFoodX();
		int foodY = food.getFoodY();
		snakeLocation[foodX][foodY] = oldDir;

		// 消除食物

		// 新建食物
		food = new Food();
		snakeLocation[food.getFoodX()][food.getFoodY()] = FOOD;
	}

	// 蛇移動函數
	public void moved() {

		if ((oldDir + newDir) != 5) {
			oldDir = newDir;
		}
		snakeLocation[headX][headY] = oldDir;
		// 蛇头的移動
		switch (oldDir) {
		case UP:
			headY--;
			break;
		case DOWN:
			headY++;
			break;
		case LEFT:
			headX--;
			break;
		case RIGHT:
			headX++;
			break;
		}

		if (snakeLocation[headX][headY] == GROUND || isEatBody()) {
			die();
			// stat = Game.STOP;
		} else if (isEatFood(food.getFoodX(), food.getFoodY())) {
			eatFood();
			totalScore = Integer.parseInt(score.getText().trim());
			totalScore += eatScore * speed * speed;
			score.setText(totalScore + "");

			if (totalScore >= speed * speed * 100) {
				speed = speedUp();
				level.setText(speed + "");
			}
			System.out.println(totalScore);
			
		} else {
			// 蛇尾的移动
			snakeLocation[headX][headY] = oldDir;
			tail = snakeLocation[tailX][tailY];
			snakeLocation[tailX][tailY] = 0;
			switch (tail) {
			case UP:
				tailY--;
				break;
			case DOWN:
				tailY++;
				break;
			case LEFT:
				tailX--;
				break;
			case RIGHT:
				tailX++;
				break;
			}

		}
		addKeyListener(new SnakeControler(this));
		repaint();

	}

	public void groundInit() {
		// 上下墙
		for (int x = 0; x < Global.WIDTH; x++) {
			snakeLocation[x][0] = -1;
			snakeLocation[x][Global.HEIGHT - 1] = -1;
		}
		// 左右墙
		for (int y = 0; y < Global.HEIGHT; y++) {
			snakeLocation[0][y] = -1;
			snakeLocation[Global.WIDTH - 1][y] = -1;
		}
	}

	public void changeDir(int dir) {
		// TODO Auto-generated method stub
		this.newDir = dir;
	}

	public void die() {
		stat = Game.STOP;
		// JOptionPane.showConfirmDialog(this, "你輸了", "遊戲結束",
		// JOptionPane.OK_CANCEL_OPTION);
		JOptionPane.showMessageDialog(this, "你輸了", "遊戲結束",
				JOptionPane.OK_OPTION);
	}

	public class SnakeDriver implements Runnable {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			while (Snake.stat != Game.STOP) {
				moved();
				try {
					Thread.sleep(300 - speed * 20);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	public void fillRect(int x, int y, Graphics g, Color color) {
		g.setColor(color);
		g.fill3DRect(x * Global.CELL_SIZE, y * Global.CELL_SIZE,
				Global.CELL_SIZE, Global.CELL_SIZE, true);

	}

	public void drawRect(int x, int y, Graphics g, Color color) {
		g.setColor(color);
		g.draw3DRect(x * Global.CELL_SIZE, y * Global.CELL_SIZE,
				Global.CELL_SIZE, Global.CELL_SIZE, true);
	}

	@Override
	protected void paintComponent(Graphics g) {
		// TODO Auto-generated method stub
		super.paintComponent(g);
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, Global.WIDTH * Global.CELL_SIZE, Global.HEIGHT
				* Global.CELL_SIZE);

		for (int x = 1; x < Global.WIDTH - 1; x++) {
			for (int y = 1; y < Global.HEIGHT - 1; y++) {

				switch (snakeLocation[x][y]) {
				case 0:
					drawRect(x, y, g, Color.GREEN);
					break;
				case GROUND:
					fillRect(x, y, g, Color.DARK_GRAY);
					break;
				case FOOD:
					fillRect(x, y, g, Color.RED);
					break;
				default:
					fillRect(x, y, g, Color.GREEN);
					break;
				}

			}
		}

	}

}

class Food {
	private int foodX;
	private int foodY;

	public Food() {

		Random rd = new Random();

		foodX = rd.nextInt(Global.WIDTH - 2) + 1;
		foodY = rd.nextInt(Global.HEIGHT - 2) + 1;

	}

	public int getFoodX() {
		return foodX;
	}

	public int getFoodY() {
		return foodY;
	}
}

class SnakeControler extends KeyAdapter {
	private Snake snake;

	SnakeControler(Snake snake) {
		this.snake = snake;
	}

	@Override
	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub
		int key = e.getKeyCode();

		switch (key) {
		case KeyEvent.VK_UP:
			if (snake.DOWN != snake.getDirection())
				snake.changeDir(snake.UP);
			break;
		case KeyEvent.VK_DOWN:
			if (snake.UP != snake.getDirection())
				snake.changeDir(snake.DOWN);
			break;
		case KeyEvent.VK_LEFT:
			if (snake.RIGHT != snake.getDirection())
				snake.changeDir(snake.LEFT);
			break;
		case KeyEvent.VK_RIGHT:
			if (snake.LEFT != snake.getDirection())
				snake.changeDir(snake.RIGHT);
			break;
		case KeyEvent.VK_SPACE:
			/*
			 * if (Snake.stat == Game.PAUSE) { Snake.stat = Game.START; } else {
			 * Snake.stat = Game.PAUSE; snake.addKeyListener(new
			 * SnakeControler(snake)); }
			 */
		}
	}

}

class SnakeMenuBar extends MouseAdapter {

	private JFrame jframe;

	public SnakeMenuBar(JFrame jframe) {

		this.jframe = jframe;
		init();
	}

	/**
	 * 
	 */
	private void init() {
		// TODO Auto-generated method stub
		MenuBar mb = new MenuBar();
		Menu menu1 = new Menu("click");

		MenuItem mi1 = new MenuItem("Start");
		MenuItem mi2 = new MenuItem("Pause");
		menu1.add(mi1);
		menu1.add(mi2);

		mb.add(menu1);
		jframe.setMenuBar(mb);
	}

}