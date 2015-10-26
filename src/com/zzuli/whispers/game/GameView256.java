package com.zzuli.whispers.game;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

public class GameView256 extends LinearLayout {

	private Card[][] cardsMap = new Card[Config.LINES][Config.LINES];
	private List<Point> emptyPoints = new ArrayList<Point>();
	boolean complete = true;
	boolean finish = false;
	int maxNum = 0;

	public int getMaxNum() {
		maxNum = cardsMap[0][0].getNum();
		for (int y = 0; y < Config.LINES; y++) {
			for (int x = 0; x < Config.LINES; x++) {
				if (maxNum < cardsMap[x][y].getNum()) {
					maxNum = cardsMap[x][y].getNum();
				}
			}
		}
		return maxNum;
	}

	public GameView256(Context context) {
		super(context);

		initGameView();
	}

	public GameView256(Context context, AttributeSet attrs) {
		super(context, attrs);

		initGameView();
	}

	private void initGameView() {
		setOrientation(LinearLayout.VERTICAL);

		setOnTouchListener(new View.OnTouchListener() {

			private float startX, startY, offsetX, offsetY;

			@Override
			public boolean onTouch(View v, MotionEvent event) {

				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					startX = event.getX();
					startY = event.getY();
					break;
				case MotionEvent.ACTION_UP:
					offsetX = event.getX() - startX;
					offsetY = event.getY() - startY;

					if (Math.abs(offsetX) > Math.abs(offsetY)) {
						if (offsetX < -5) {
							swipeLeft();
						} else if (offsetX > 5) {
							swipeRight();
						}
					} else {
						if (offsetY < -5) {
							swipeUp();
						} else if (offsetY > 5) {
							swipeDown();
						}
					}

					break;
				}
				return true;
			}
		});
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);

		Config.CARD_WIDTH = (Math.min(w, h) - 10) / Config.LINES;

		addCards(Config.CARD_WIDTH, Config.CARD_WIDTH);

		startGame();
	}

	private void addCards(int cardWidth, int cardHeight) {

		Card c;

		LinearLayout line;
		LinearLayout.LayoutParams lineLp;

		for (int y = 0; y < Config.LINES; y++) {
			line = new LinearLayout(getContext());
			lineLp = new LinearLayout.LayoutParams(-1, cardHeight);
			addView(line, lineLp);

			for (int x = 0; x < Config.LINES; x++) {
				c = new Card(getContext());
				line.addView(c, cardWidth, cardHeight);

				cardsMap[x][y] = c;
			}
		}
	}

	public void startGame() {

		game256MainActivity aty = game256MainActivity.getMainActivity();
		aty.clearScore();

		for (int y = 0; y < Config.LINES; y++) {
			for (int x = 0; x < Config.LINES; x++) {
				cardsMap[x][y].setNum(0);
			}
		}

		addRandomNum();
		addRandomNum();
	}

	private void addRandomNum() {

		emptyPoints.clear();

		for (int y = 0; y < Config.LINES; y++) {
			for (int x = 0; x < Config.LINES; x++) {
				if (cardsMap[x][y].getNum() <= 0) {
					emptyPoints.add(new Point(x, y));
				}
			}
		}

		if (emptyPoints.size() > 0) {

			Point p = emptyPoints.remove((int) (Math.random() * emptyPoints
					.size()));
			cardsMap[p.x][p.y].setNum(Math.random() > 0.3 ? 2 : 4);

			game256MainActivity.getMainActivity().getAnimLayer()
					.createScaleTo1(cardsMap[p.x][p.y]);
		}
	}

	private void swipeLeft() {

		boolean merge = false;

		for (int y = 0; y < Config.LINES; y++) {
			for (int x = 0; x < Config.LINES; x++) {

				for (int x1 = x + 1; x1 < Config.LINES; x1++) {
					if (cardsMap[x1][y].getNum() > 0) {

						if (cardsMap[x][y].getNum() <= 0) {

							game256MainActivity
									.getMainActivity()
									.getAnimLayer()
									.createMoveAnim(cardsMap[x1][y],
											cardsMap[x][y], x1, x, y, y);

							cardsMap[x][y].setNum(cardsMap[x1][y].getNum());
							cardsMap[x1][y].setNum(0);

							x--;
							merge = true;

						} else if (cardsMap[x][y].equals(cardsMap[x1][y])) {
							game256MainActivity
									.getMainActivity()
									.getAnimLayer()
									.createMoveAnim(cardsMap[x1][y],
											cardsMap[x][y], x1, x, y, y);
							cardsMap[x][y].setNum(cardsMap[x][y].getNum() * 2);
							cardsMap[x1][y].setNum(0);

							game256MainActivity.getMainActivity().addScore(
									getMaxNum());
							merge = true;
						}

						break;
					}
				}
			}
		}

		if (merge) {
			addRandomNum();
			checkComplete();
		}
	}

	private void swipeRight() {

		boolean merge = false;

		for (int y = 0; y < Config.LINES; y++) {
			for (int x = Config.LINES - 1; x >= 0; x--) {

				for (int x1 = x - 1; x1 >= 0; x1--) {
					if (cardsMap[x1][y].getNum() > 0) {

						if (cardsMap[x][y].getNum() <= 0) {
							game256MainActivity
									.getMainActivity()
									.getAnimLayer()
									.createMoveAnim(cardsMap[x1][y],
											cardsMap[x][y], x1, x, y, y);
							cardsMap[x][y].setNum(cardsMap[x1][y].getNum());
							cardsMap[x1][y].setNum(0);

							x++;
							merge = true;
						} else if (cardsMap[x][y].equals(cardsMap[x1][y])) {
							game256MainActivity
									.getMainActivity()
									.getAnimLayer()
									.createMoveAnim(cardsMap[x1][y],
											cardsMap[x][y], x1, x, y, y);
							cardsMap[x][y].setNum(cardsMap[x][y].getNum() * 2);
							cardsMap[x1][y].setNum(0);
							game256MainActivity.getMainActivity().addScore(
									getMaxNum());
							merge = true;
						}

						break;
					}
				}
			}
		}

		if (merge) {
			addRandomNum();
			checkComplete();
		}
	}

	private void swipeUp() {

		boolean merge = false;

		for (int x = 0; x < Config.LINES; x++) {
			for (int y = 0; y < Config.LINES; y++) {

				for (int y1 = y + 1; y1 < Config.LINES; y1++) {
					if (cardsMap[x][y1].getNum() > 0) {

						if (cardsMap[x][y].getNum() <= 0) {
							game256MainActivity
									.getMainActivity()
									.getAnimLayer()
									.createMoveAnim(cardsMap[x][y1],
											cardsMap[x][y], x, x, y1, y);
							cardsMap[x][y].setNum(cardsMap[x][y1].getNum());
							cardsMap[x][y1].setNum(0);

							y--;

							merge = true;
						} else if (cardsMap[x][y].equals(cardsMap[x][y1])) {
							game256MainActivity
									.getMainActivity()
									.getAnimLayer()
									.createMoveAnim(cardsMap[x][y1],
											cardsMap[x][y], x, x, y1, y);
							cardsMap[x][y].setNum(cardsMap[x][y].getNum() * 2);
							cardsMap[x][y1].setNum(0);
							game256MainActivity.getMainActivity().addScore(
									getMaxNum());
							merge = true;
						}

						break;

					}
				}
			}
		}

		if (merge) {
			addRandomNum();
			checkComplete();
		}
	}

	private void swipeDown() {

		boolean merge = false;

		for (int x = 0; x < Config.LINES; x++) {
			for (int y = Config.LINES - 1; y >= 0; y--) {

				for (int y1 = y - 1; y1 >= 0; y1--) {
					if (cardsMap[x][y1].getNum() > 0) {

						if (cardsMap[x][y].getNum() <= 0) {
							game256MainActivity
									.getMainActivity()
									.getAnimLayer()
									.createMoveAnim(cardsMap[x][y1],
											cardsMap[x][y], x, x, y1, y);
							cardsMap[x][y].setNum(cardsMap[x][y1].getNum());
							cardsMap[x][y1].setNum(0);

							y++;
							merge = true;
						} else if (cardsMap[x][y].equals(cardsMap[x][y1])) {
							game256MainActivity
									.getMainActivity()
									.getAnimLayer()
									.createMoveAnim(cardsMap[x][y1],
											cardsMap[x][y], x, x, y1, y);
							cardsMap[x][y].setNum(cardsMap[x][y].getNum() * 2);
							cardsMap[x][y1].setNum(0);
							game256MainActivity.getMainActivity().addScore(
									getMaxNum());
							merge = true;
						}

						break;
					}
				}
			}
		}

		if (merge) {
			addRandomNum();
			checkComplete();
		}
	}

	private void checkComplete() {

		ALL: for (int y = 0; y < Config.LINES; y++) {
			for (int x = 0; x < Config.LINES; x++) {
				if (cardsMap[x][y].getNum() == 0
						|| (x > 0 && cardsMap[x][y].equals(cardsMap[x - 1][y]))
						|| (x < Config.LINES - 1 && cardsMap[x][y]
								.equals(cardsMap[x + 1][y]))
						|| (y > 0 && cardsMap[x][y].equals(cardsMap[x][y - 1]))
						|| (y < Config.LINES - 1 && cardsMap[x][y]
								.equals(cardsMap[x][y + 1]))) {

					complete = false;
					break ALL;
				}
			}
		}
		for (int y = 0; y < Config.LINES; y++) {
			for (int x = 0; x < Config.LINES; x++) {
				if (cardsMap[x][y].getNum() == 256) {
					finish = true;
					Message msg = new Message();
					msg.what = 1;
					handler.sendMessage(msg);

				}
			}
		}
		if (complete) {
			new AlertDialog.Builder(getContext())
					.setTitle("提示：")
					.setMessage("游戏结束")
					.setPositiveButton("重新开始",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									startGame();
								}
							}).show();
		}

	}

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			if (msg.what == 1) {
				gameView256Listener.success();
			}
		};
	};

	public interface GameView256Listener {
		void success();

	}

	public GameView256Listener gameView256Listener;

	public void setOnGameView256Listener(GameView256Listener gameView256Listener) {
		this.gameView256Listener = gameView256Listener;
	}

}
