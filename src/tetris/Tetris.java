package tetris;

import java.applet.Applet;
import java.applet.AudioClip;
import java.awt.Button;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Label;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Random;

public class Tetris extends Applet implements Runnable, ActionListener {
	private static final long serialVersionUID = 1L;

	Thread clock;
	
	Image off;
	Graphics offG;
	
	Random r;
	
	boolean[][] mainMap, sideMap;
	Color[][] mainColorMap, sideColorMap;
	Color[] colorType;
	
	
	int mainBlockType, sideBlockType;
	int[] mainBlockX, mainBlockY, sideBlockX, sideBlockY;
	int blockPos;
	
	int score, point;
	int delayTime;
	int runGame;
	boolean pauseLock, endLock, runLock;
	int stoneLine;
	int stoneScore;
	
	AudioClip turnAudio, deleteAudio, gameOverAudio;
	boolean soundLock;
	
	Button startButton, soundButton;
	Label scoreLabel;
	
	@Override
	public void init() {
//		System.out.println("init routine");
		off = createImage(441, 336);
		offG = off.getGraphics();
		
		turnAudio = getAudioClip(getCodeBase(), "..\\sound\\tetris\\turn.au");
		deleteAudio = getAudioClip(getCodeBase(), "..\\\\sound\\\\tetris\\\\delete.au");
		gameOverAudio = getAudioClip(getCodeBase(), "..\\\\sound\\\\tetris\\\\gameover.au");
		
		setLayout(null);
		soundButton = new Button("BGM ON");
		soundButton.setBackground(new Color(50, 50, 50));
		soundButton.setForeground(Color.white);
		soundButton.setBounds(20, 20, 90, 45);
		soundButton.addActionListener(this);
		add(soundButton);
		scoreLabel = new Label("00000",Label.CENTER);
		scoreLabel.setBackground(new Color(50, 50, 50));
		scoreLabel.setForeground(Color.white);
		scoreLabel.setBounds(20, 85, 90, 45);
		add(scoreLabel);
		startButton = new Button("START");
		startButton.setBackground(new Color(50, 50, 50));
		startButton.setForeground(Color.white);
		startButton.setBounds(20, 270, 90, 45);
		startButton.addActionListener(this);
		add(startButton);
		
		stoneLine = 0;
		mainMap = new boolean[12][21-stoneLine];
		mainColorMap = new Color[12][21-stoneLine];
		
		sideMap = new boolean[6][6];
		sideColorMap = new Color[6][6];
		
		colorType = new Color[7];
		setColorType();
		
		mainBlockX = new int[4];
		mainBlockY = new int[4];
		blockPos = 0;
		
		sideBlockX = new int[4];
		sideBlockY = new int[4];
		
		r = new Random();
		mainBlockType = r.nextInt(7);
		setMainBlockXY(mainBlockType);
		sideBlockType = r.nextInt(7);
		setSideBlockXY(sideBlockType);
		
		drawMainBlock();
		drawSideBlock();
		drawBackground();
		drawMap();
		drawGrid();
		
		score = 0;
		point = 100;
		delayTime = 500;
		runGame = 0;
		soundLock = pauseLock = endLock = runLock = true;
		stoneScore = 5000;
		
		addKeyListener(new MyKeyHandler());
		
	}
	
	public void setColorType() {
		colorType[0] = new Color(240, 240, 0);
		colorType[1] = new Color(160, 0, 240);
		colorType[2] = new Color(240, 160, 0);
		colorType[3] = new Color(0, 0, 240);
		colorType[4] = new Color(0, 240, 0);
		colorType[5] = new Color(240, 0, 0);
		colorType[6] = new Color(0, 240, 240);
	}
	
	public void setMainBlockXY(int type) {
		switch(type) {
		case 0:
			mainBlockX[0] = 5;	mainBlockY[0] = 0;
			mainBlockX[1] = 6;	mainBlockY[1] = 0;
			mainBlockX[2] = 5;	mainBlockY[2] = 1;
			mainBlockX[3] = 6;	mainBlockY[3] = 1;
			break;
		case 1:
			mainBlockX[0] = 6;	mainBlockY[0] = 0;
			mainBlockX[1] = 5;	mainBlockY[1] = 1;
			mainBlockX[2] = 6;	mainBlockY[2] = 1;
			mainBlockX[3] = 7;	mainBlockY[3] = 1;
			break;
		case 2:
			mainBlockX[0] = 7;	mainBlockY[0] = 0;
			mainBlockX[1] = 5;	mainBlockY[1] = 1;
			mainBlockX[2] = 6;	mainBlockY[2] = 1;
			mainBlockX[3] = 7;	mainBlockY[3] = 1;
			break;
		case 3:
			mainBlockX[0] = 5;	mainBlockY[0] = 0;
			mainBlockX[1] = 5;	mainBlockY[1] = 1;
			mainBlockX[2] = 6;	mainBlockY[2] = 1;
			mainBlockX[3] = 7;	mainBlockY[3] = 1;
			break;
		case 4:
			mainBlockX[0] = 5;	mainBlockY[0] = 0;
			mainBlockX[1] = 5;	mainBlockY[1] = 1;
			mainBlockX[2] = 6;	mainBlockY[2] = 1;
			mainBlockX[3] = 6;	mainBlockY[3] = 2;
			break;
		case 5:
			mainBlockX[0] = 6;	mainBlockY[0] = 0;
			mainBlockX[1] = 5;	mainBlockY[1] = 1;
			mainBlockX[2] = 6;	mainBlockY[2] = 1;
			mainBlockX[3] = 5;	mainBlockY[3] = 2;
			break;
		case 6:
			mainBlockX[0] = 4;	mainBlockY[0] = 0;
			mainBlockX[1] = 5;	mainBlockY[1] = 0;
			mainBlockX[2] = 6;	mainBlockY[2] = 0;
			mainBlockX[3] = 7;	mainBlockY[3] = 0;
			break;
		}
		
	}

	public void setSideBlockXY(int type) {
		switch(type) {
		case 0:
			sideBlockX[0] = 2;	sideBlockY[0] = 2;
			sideBlockX[1] = 3;	sideBlockY[1] = 2;
			sideBlockX[2] = 2;	sideBlockY[2] = 3;
			sideBlockX[3] = 3;	sideBlockY[3] = 3;
			break;
		case 1:
			sideBlockX[0] = 3;	sideBlockY[0] = 2;
			sideBlockX[1] = 2;	sideBlockY[1] = 3;
			sideBlockX[2] = 3;	sideBlockY[2] = 3;
			sideBlockX[3] = 4;	sideBlockY[3] = 3;
			break;
		case 2:
			sideBlockX[0] = 4;	sideBlockY[0] = 2;
			sideBlockX[1] = 2;	sideBlockY[1] = 3;
			sideBlockX[2] = 3;	sideBlockY[2] = 3;
			sideBlockX[3] = 4;	sideBlockY[3] = 3;
			break;
		case 3:
			sideBlockX[0] = 2;	sideBlockY[0] = 2;
			sideBlockX[1] = 2;	sideBlockY[1] = 3;
			sideBlockX[2] = 3;	sideBlockY[2] = 3;
			sideBlockX[3] = 4;	sideBlockY[3] = 3;
			break;
		case 4:
			sideBlockX[0] = 2;	sideBlockY[0] = 2;
			sideBlockX[1] = 2;	sideBlockY[1] = 3;
			sideBlockX[2] = 3;	sideBlockY[2] = 3;
			sideBlockX[3] = 3;	sideBlockY[3] = 4;
			break;
		case 5:
			sideBlockX[0] = 3;	sideBlockY[0] = 2;
			sideBlockX[1] = 2;	sideBlockY[1] = 3;
			sideBlockX[2] = 3;	sideBlockY[2] = 3;
			sideBlockX[3] = 2;	sideBlockY[3] = 4;
			break;
		case 6:
			sideBlockX[0] = 1;	sideBlockY[0] = 2;
			sideBlockX[1] = 2;	sideBlockY[1] = 2;
			sideBlockX[2] = 3;	sideBlockY[2] = 2;
			sideBlockX[3] = 4;	sideBlockY[3] = 2;
			break;
		}
		
	}

	@Override
	public void start() {
//		System.out.println("start routine");
		if(clock == null) {
			clock = new Thread(this);
			runLock = true;
			clock.start();
		}
		
	}
	
	@Override
	public void paint(Graphics g) {
//		System.out.println("painting!");
		g.drawImage(off, 0, 0, this);
		
	}
	
	@Override
	public void update(Graphics g) {
//		System.out.println("update!");
		paint(g);
		
	}
	
	@SuppressWarnings("static-access")
	@Override
	public void run() {
//		System.out.println("run start!");
		while(runLock) {
			try {
				clock.sleep(delayTime);
			}catch (InterruptedException e) {
				e.printStackTrace();
			}
			this.requestFocus();
			
			switch(runGame) {
			case 1:
				drawMainBlock();
				drawSideBlock();
				drawBackground();
				drawMap();
				drawGrid();
				break;
			case 2:
				drawPause();
				break;
			case 3:
				drawLose();
				break;
			case 4:
				drawWin();
				break;
			default :
				drawTitle();
				endLock = false;
				break;
			}
			
			if(pauseLock && endLock) {
				dropBlock();
			}
			
			repaint();
		}
		
	}
	
	public void drawPause() {
		offG.setColor(new Color(50, 50, 50));
		offG.fillRect(159, 120, 123, 70);
		offG.setColor(Color.white);
		offG.drawRect(161, 125, 121, 60);
		offG.setColor(Color.red);
		offG.drawString("PAUSE", 200, 150);
		offG.setColor(Color.yellow);
		offG.drawString("  Push ESC boutton!", 165, 170);
	}
	
	public void drawLose() {
		offG.setColor(new Color(50, 50, 50));
		offG.fillRect(165, 120, 110, 70);
		offG.setColor(Color.white);
		offG.drawRect(170, 125, 100, 60);
		offG.setColor(Color.red);
		offG.drawString("Game Over!", 186, 150);
		offG.setColor(Color.yellow);
		offG.drawString("Score: "+score, 186, 170);
	}
	
	public void drawWin() {
		offG.setColor(new Color(50, 50, 50));
		offG.fillRect(165, 120, 110, 70);
		offG.setColor(Color.white);
		offG.drawRect(170, 125, 100, 60);
		offG.setColor(Color.green);
		offG.drawString("YOU WINER!", 186, 150);
		offG.setColor(Color.blue);
		offG.drawString("Score: "+score, 186, 170);
	}
	
	public void drawTitle() {
		offG.setColor(new Color(50, 50, 50));
		offG.fillRect(159, 120, 123, 70);
		offG.setColor(Color.white);
		offG.drawRect(161, 125, 121, 60);
		offG.setColor(Color.red);
		offG.drawString("TETRIS", 200, 150);
		offG.setColor(Color.yellow);
		offG.drawString("Press START button!", 165, 170);
		
	}
	
	public void dropBlock() {
		removeMainBlock();
		if(checkDrop()) {
			for(int i=0; i<4; i++) {
				mainBlockY[i]++;
			}
		}else {
			drawMainBlock();
			removeSideBlock();
			nextBlock();
		}
		
	}
	
	public void removeMainBlock() {
		for(int i=0; i<4; i++) {
			mainMap[mainBlockX[i]][mainBlockY[i]] = false;
			mainColorMap[mainBlockX[i]][mainBlockY[i]] = new Color(50, 50, 50);
		}
		
	}
	
	public void removeSideBlock() {
		for(int i=0; i<4; i++) {
			sideMap[sideBlockX[i]][sideBlockY[i]] = false;
			sideColorMap[sideBlockX[i]][sideBlockY[i]] = new Color(50, 50, 50);
		}
		
	}
	
	public boolean checkDrop() {
		boolean dropOk = true;
		
		for(int i=0; i<4; i++) {
			if(mainBlockY[i]+1 != 21-stoneLine) {
				if(mainMap[mainBlockX[i]][mainBlockY[i]+1]) dropOk = false;
			}else {
				dropOk = false;
			}
		}
		
		return dropOk;
		
	}
	
	public void drawMainBlock() {
		for(int i=0; i<4; i++) {
			mainMap[mainBlockX[i]][mainBlockY[i]] = true;
			mainColorMap[mainBlockX[i]][mainBlockY[i]] = colorType[mainBlockType];
		}
		
	}
	
	public void drawSideBlock() {
		for(int i=0; i<4; i++) {
			sideMap[sideBlockX[i]][sideBlockY[i]] = true;
			sideColorMap[sideBlockX[i]][sideBlockY[i]] = colorType[sideBlockType];
		}
		
	}
	
	public void nextBlock() {
		mainBlockType = sideBlockType;
		blockPos = 0;
		sideBlockType = r.nextInt(7);
		delLine();
		setMainBlockXY(mainBlockType);
		setSideBlockXY(sideBlockType);
		checkGameOver();
		
	}
	
	public void checkGameOver() {
		for(int i=0; i<4; i++) {
			if(mainMap[mainBlockX[i]][mainBlockY[i]]) {
				if(runGame == 1) {
					if(soundLock) gameOverAudio.play();
					runGame = 3;
					endLock = false;
				}
			}
		}
		
	}
	
	public void drawBackground() {
		offG.setColor(new Color(57, 55, 77));
		offG.fillRect(0, 0, 440, 335);		
		offG.setColor(new Color(50, 50, 50));
		offG.fillRect(330, 130, 90, 185);
		offG.fillRect(20, 150, 90, 100);
		offG.setColor(new Color(200, 200, 200));
		offG.drawRect(335, 135, 80, 175);
		offG.drawRect(25, 155, 80, 90);
		offG.setColor(new Color(50, 50, 50));
		offG.fillRect(360, 130, 30, 10);
		offG.fillRect(43, 150, 47, 10);
		offG.setColor(new Color(200, 200, 200));
		offG.drawString("Mission", 45, 160);
		offG.drawString("Let's get", 42, 190);
		offG.drawString("10000", 47, 207);
		offG.drawString("points!", 47, 223);
		offG.drawString("Help", 362, 140);
		offG.drawString("Left", 345, 170);
		offG.drawString("ก็", 392, 170);
		offG.drawString("Right", 345, 195);
		offG.drawString("กๆ", 392, 195);
		offG.drawString("Down", 345, 220);
		offG.drawString("ก้", 392, 220);
		offG.drawString("Rotate", 345, 245);
		offG.drawString("ก่", 392, 245);
		offG.drawString("Drop", 345, 270);
		offG.drawString("Space", 378, 270);
		offG.drawString("Pause", 345, 295);
		offG.drawString("ESC", 388, 295);
		
	}
	
	public void drawMap() {
		for(int i=0; i<12; i++) {
			for(int j=0; j<21-stoneLine; j++) {
				if(mainMap[i][j]) {
					offG.setColor(mainColorMap[i][j]);
					offG.fillRect(i*15+130, j*15, 15, 15);
				}else {
					offG.setColor(new Color(50, 50, 50));
					offG.fillRect(i*15+130, j*15, 15, 15);
				}
			}
		}
		for(int i=0; i<12; i++) {
			for(int j=21-stoneLine; j<21; j++) {
				offG.setColor(new Color(100, 100, 100));
				offG.fillRect(i*15+130, j*15, 15, 15);
			}
		}
		for(int i=0;i<6;i++) {
			for(int j=0;j<6;j++) {
				if(sideMap[i][j]) {
					offG.setColor(sideColorMap[i][j]);
					offG.fillRect(i*15+330,j*15+20,15,15);
				}else {
					offG.setColor(new Color(50, 50, 50));
					offG.fillRect(i*15+330,j*15+20,15,15);
				}
			}
		}
		
	}
	
	public void drawGrid() {
		offG.setColor(new Color(70, 70, 70));
		
		for(int i=0; i<12; i++) {
			for(int j=0; j<21-stoneLine; j++) {
				offG.drawRect(i*15+130, j*15, 15, 15);
			}
		}
		for(int i=0; i<12; i++) {
			for(int j=21-stoneLine; j<21; j++) {
				offG.drawRect(i*15+130, j*15, 15, 15);
			}
		}
		for(int i=0; i<6; i++) {
			for(int j=0; j<6; j++) {
				offG.drawRect(i*15+330,j*15+20,15,15);
			}
		}
		
	}
	
	public void delLine() {
		boolean delOk = false;
		int combo = 0;
		
		for(int row=20-stoneLine; row>=0; row--) {
			delOk = true;
			for(int col=0; col<12; col++) {
				if(!mainMap[col][row]) delOk = false;
			}
			
			if(delOk) {
				if(soundLock) deleteAudio.play();
				combo += 1;
				
				for(int delRow=row; delRow>0; delRow--) {
					for(int delCol=0; delCol<12; delCol++) {
						mainMap[delCol][delRow] = mainMap[delCol][delRow-1];
						mainColorMap[delCol][delRow] = mainColorMap[delCol][delRow-1];
					}
				}
				for(int i=0; i<12; i++) {
					mainMap[i][0] = false;
					mainColorMap[i][0] = new Color(50, 50, 50);
				}
				
				row++;
			}
		}
		
		switch(combo) {
		case 1:
			score += point;
			break;
		case 2:
			score += (point * 2) + point;
			break; 
		case 3:
			score += (point * 3) + (point * 2);
			break;
		case 4:
			score += (point * 4) + (point * 3);
			break;
		}
		
		if(score == 0){
			scoreLabel.setText("0000"+score);
		}else if(score < 1000) {
			scoreLabel.setText("00"+score);
		}else if(score < 10000) {
			scoreLabel.setText("0"+score);
		}else {
			scoreLabel.setText(""+score);
		}
		
		setStoneLine();
		checkEndGame();
		
	}
	
	public void setStoneLine() {
		if((score>=stoneScore) && (score<=8000)) {
				stoneScore += 300;
				stoneLine++;
		}
		
	}
	
	public void checkEndGame() {
		if(score>1000 && score<=4000) {
			delayTime = 500 - ((score - 1000) / 10);
		}else if(score >= 10000) {
			endLock = false;
			runGame = 4;
		}
		
	}
	
	@Override
	public void stop() {
//		System.out.println("stop routine");
		runGame = 2;
		pauseLock = false;
	}
	
	@Override
	public void destroy() {
//		System.out.println("destroy routine");
		if((clock!=null) && (clock.isAlive())) {
			runLock = false;
			clock = null;
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == startButton) {
			blockPos = 0;
			stoneLine = 0;
			
			for(int i=0; i<12; i++) {
				for(int j=0;j<21;j++) {
					mainMap[i][j] = false;
				}
			}
			
			for(int i=0;i<6;i++) {
				for(int j=0;j<6;j++) {
					sideMap[i][j] = false;
				}
			}
			
			r = new Random();
			mainBlockType = r.nextInt(7);
			setMainBlockXY(mainBlockType);
			sideBlockType = r.nextInt(7);
			setSideBlockXY(sideBlockType);
			
			drawMainBlock();
			drawSideBlock();
			drawBackground();
			drawMap();
			drawGrid();
			
			score = 0;
			scoreLabel.setText("00000");
			delayTime = 500;
			runGame = 1;
			pauseLock = endLock = runLock = true;
			stoneScore = 5000;
		}else if(e.getSource() == soundButton) {
			if(!soundLock) {
				soundButton.setLabel("BGM ON");
				soundLock = true;
			}else {
				soundButton.setLabel("BGM OFF");
				soundLock = false;
			}
			
		}

	}
	
	public class MyKeyHandler extends KeyAdapter {
		@Override
		public void keyPressed(KeyEvent e) {
			int keyCode = e.getKeyCode();
			if(endLock) {
				if(keyCode==KeyEvent.VK_ESCAPE) {
					if(pauseLock) {
						pauseLock = false;
						runGame = 2;
					}else {
						pauseLock = true;
						runGame = 1;
					}
				}
				if(pauseLock) {
					if(keyCode==KeyEvent.VK_LEFT) {
						if(checkMove(-1)) {
							for(int i=0;i<4;i++) {
								mainBlockX[i]--;
							}
						}
					}
					
					if(keyCode==KeyEvent.VK_RIGHT) {
						if(checkMove(1)) {
							for(int i=0;i<4;i++) {
								mainBlockX[i]++;
							}
						}
					}
					
					if(keyCode==KeyEvent.VK_DOWN) {
						removeMainBlock();
						
						if(checkDrop()) {
							for(int i=0;i<4;i++) {
								mainBlockY[i]++;
							}
						}else {
							drawMainBlock();
							removeSideBlock();
							nextBlock();
						}
					}
					
					if(keyCode==KeyEvent.VK_UP) {
						int[] tmpX = new int[4];
						int[] tmpY = new int[4];
						
						for(int i=0;i<4;i++) {
							tmpX[i] = mainBlockX[i];
							tmpY[i] = mainBlockY[i];
						}
						
						removeMainBlock();
						turnBlock();
						
						if(checkTurn()) {
							if(soundLock) turnAudio.play();
							if(blockPos<4) {
								blockPos++;
							}else {
								blockPos = 0;
							}
						}else {
							for(int i=0;i<4;i++) {
								mainBlockX[i] = tmpX[i];
								mainBlockY[i] = tmpY[i];
								mainMap[mainBlockX[i]][mainBlockY[i]] = true;
								mainColorMap[mainBlockX[i]][mainBlockY[i]] = colorType[mainBlockType];
								}
						}
					}
					if(keyCode==KeyEvent.VK_SPACE) {
						removeMainBlock();
						while(true) {
							if(checkDrop()) {
								for(int i=0;i<4;i++) {
									mainBlockY[i]++;
								}
							}else {
								drawMainBlock();
								removeSideBlock();
								nextBlock();
								break;
							}
						}
					}
				}
			}
			
			drawMainBlock();
			drawSideBlock();
			drawBackground();
			drawMap();
			drawGrid();

			repaint();
			
		}
		
		public boolean checkMove(int dir) {
			boolean moveOk = true;
			
			removeMainBlock();
			
			for(int i=0; i<4; i++) {
				if(((mainBlockX[i]+dir)>=0) && ((mainBlockX[i]+dir)<12)) {
					if(mainMap[mainBlockX[i]+dir][mainBlockY[i]]) moveOk = false;
				}else {
					moveOk = false;
				}
			}
			
			return moveOk;
			
		}
		
		public boolean checkTurn() {
			boolean turnOk = true;
			
			for(int i=0; i<4; i++) {
				if((mainBlockX[i]>=0) && (mainBlockX[i]<12) && (mainBlockY[i]>=0) && (mainBlockY[i]<21-stoneLine)) {
					if(mainMap[mainBlockX[i]][mainBlockY[i]]) turnOk = false;
				}else {
					turnOk = false;
				}
			}
			
			return turnOk;
			
		}
		
		public void turnBlock() {
			switch(mainBlockType) {
			case 1:
				switch(blockPos) {
				case 0:
					mainBlockX[0] = mainBlockX[0]; mainBlockY[0] = mainBlockY[0];
					mainBlockX[1] = mainBlockX[1]; mainBlockY[1] = mainBlockY[1];
					mainBlockX[2] = mainBlockX[2]; mainBlockY[2] = mainBlockY[2];
					mainBlockX[3]--; mainBlockY[3]++;
					break;
				case 1:
					mainBlockX[0]--; mainBlockY[0] = mainBlockY[0];
					mainBlockX[1]++; mainBlockY[1]--;
					mainBlockX[2]++; mainBlockY[2]--;
					mainBlockX[3] = mainBlockX[3]; mainBlockY[3]--;
					break;
				case 2:
					mainBlockX[0]++; mainBlockY[0] = mainBlockY[0];
					mainBlockX[1] = mainBlockX[1]; mainBlockY[1]++;
					mainBlockX[2] = mainBlockX[2]; mainBlockY[2]++;
					mainBlockX[3] = mainBlockX[3]; mainBlockY[3]++;
					break;
				case 3:
					mainBlockX[0] = mainBlockX[0]; mainBlockY[0] = mainBlockY[0];
					mainBlockX[1]--; mainBlockY[1] = mainBlockY[1];
					mainBlockX[2]--; mainBlockY[2] = mainBlockY[2];
					mainBlockX[3]++; mainBlockY[3]--;
					break;
				}
				break;
			case 2:
				switch(blockPos) {
				case 0:
					mainBlockX[0] -= 2; mainBlockY[0] = mainBlockY[0];
					mainBlockX[1]++; mainBlockY[1]--;
					mainBlockX[2] = mainBlockX[2]; mainBlockY[2] = mainBlockY[2];
					mainBlockX[3]--; mainBlockY[3]++;
					break;
				case 1:
					mainBlockX[0] = mainBlockX[0]; mainBlockY[0] = mainBlockY[0];
					mainBlockX[1] = mainBlockX[1]; mainBlockY[1] = mainBlockY[1];
					mainBlockX[2]++; mainBlockY[2]--;
					mainBlockX[3]--; mainBlockY[3]--;
					break;
				case 2:
					mainBlockX[0]++; mainBlockY[0] = mainBlockY[0];
					mainBlockX[1] = mainBlockX[1]; mainBlockY[1]++;
					mainBlockX[2]--; mainBlockY[2] += 2;
					mainBlockX[3] += 2; mainBlockY[3]++;
					break;
				case 3:
					mainBlockX[0]++; mainBlockY[0] = mainBlockY[0];
					mainBlockX[1]--; mainBlockY[1] = mainBlockY[1];
					mainBlockX[2] = mainBlockX[2]; mainBlockY[2]--;
					mainBlockX[3] = mainBlockX[3]; mainBlockY[3]--;
					break;
				}
				break;
			case 3:
				switch(blockPos) {
				case 0:
					mainBlockX[0]++; mainBlockY[0] = mainBlockY[0];
					mainBlockX[1]++; mainBlockY[1] = mainBlockY[1];
					mainBlockX[2]--; mainBlockY[2]++;
					mainBlockX[3]--; mainBlockY[3]++;
					break;
				case 1:
					mainBlockX[0] -= 2; mainBlockY[0] = mainBlockY[0];
					mainBlockX[1]--; mainBlockY[1]--;
					mainBlockX[2]++; mainBlockY[2] -= 2;
					mainBlockX[3] = mainBlockX[3]; mainBlockY[3]--;
					break;
				case 2:
					mainBlockX[0]++; mainBlockY[0] = mainBlockY[0];
					mainBlockX[1]++; mainBlockY[1] = mainBlockY[1];
					mainBlockX[2]--; mainBlockY[2]++;
					mainBlockX[3]--; mainBlockY[3]++;
					break;
				case 3:
					mainBlockX[0] = mainBlockX[0]; mainBlockY[0] = mainBlockY[0];
					mainBlockX[1]--; mainBlockY[1]++;
					mainBlockX[2]++; mainBlockY[2] = mainBlockY[2];
					mainBlockX[3] += 2; mainBlockY[3]--;
					break;
				}
				break;
			case 4:
				switch(blockPos) {
				case 0: case 2:
					mainBlockX[0]++; mainBlockY[0] = mainBlockY[0];
					mainBlockX[1] += 2; mainBlockY[1]--;
					mainBlockX[2]--; mainBlockY[2] = mainBlockY[2];
					mainBlockX[3] = mainBlockX[3]; mainBlockY[3]--;
					break;
				case 1: case 3:
					mainBlockX[0]--; mainBlockY[0] = mainBlockY[0];
					mainBlockX[1] -= 2; mainBlockY[1]++;
					mainBlockX[2]++; mainBlockY[2] = mainBlockY[2];
					mainBlockX[3] = mainBlockX[3]; mainBlockY[3]++;
					break;
				}
				break;
			case 5:
				switch(blockPos) {
				case 0: case 2:
					mainBlockX[0]--; mainBlockY[0] = mainBlockY[0];
					mainBlockX[1]++; mainBlockY[1]--;
					mainBlockX[2] = mainBlockX[2]; mainBlockY[2] = mainBlockY[2];
					mainBlockX[3] += 2; mainBlockY[3]--;
					break;
				case 1: case 3:
					mainBlockX[0]++; mainBlockY[0] = mainBlockY[0];
					mainBlockX[1]--; mainBlockY[1]++;
					mainBlockX[2] = mainBlockX[2]; mainBlockY[2] = mainBlockY[2];
					mainBlockX[3] -= 2; mainBlockY[3]++;
					break;
				}
				break;
			case 6:
				switch(blockPos) {
				case 0: case 2:
					mainBlockX[0] += 2; mainBlockY[0] = mainBlockY[0];
					mainBlockX[1]++; mainBlockY[1]++;
					mainBlockX[2] = mainBlockX[2]; mainBlockY[2] += 2;
					mainBlockX[3]--; mainBlockY[3] += 3;
					break;
				case 1: case 3:
					mainBlockX[0] -= 2; mainBlockY[0] = mainBlockY[0];
					mainBlockX[1]--; mainBlockY[1]--;
					mainBlockX[2] = mainBlockX[2]; mainBlockY[2] -= 2;
					mainBlockX[3]++; mainBlockY[3] -= 3;
					break;
				}
				break;
			}
			
		}
		
	}
	
}
