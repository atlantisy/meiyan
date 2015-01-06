package cn.minelock.widget;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import cn.minelock.util.BitmapUtil;
import cn.minelock.util.MathUtil;
import cn.minelock.util.Point;
import cn.minelock.util.RoundUtil;
import cn.minelock.util.StringUtil;

import cn.minelock.android.R;

/**
 * 
 * 九宫格解锁
 * 
 * 
 */
public class PatternPassWordView extends View {
	private float w = 0;
	private float h = 0;

	//
	private boolean isCache = false;
	//
	private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

	//
	private Point[][] mPoints = new Point[3][3];
	// 圆的半径
	private float r = 0;
	// 选中的点
	private List<Point> sPoints = new ArrayList<Point>();
	private boolean checking = false;
	private Bitmap pattern_round_original;// 圆点初始状态时的图片
	private Bitmap pattern_round_click;// 圆点点击时的图片
	private Bitmap pattern_round_click_error;// 出错时圆点的图片
	private Bitmap pattern_line;// 正常状态下线的图片
	private Bitmap pattern_line_semicircle;
	private Bitmap pattern_line_semicircle_error;
	private Bitmap pattern_arrow;// 线的移动方向
	private Bitmap pattern_line_error;// 错误状态下的线的图片
	private long CLEAR_TIME = 0;// 清除痕迹的时间
	private int passwordMinLength = 4;// 密码最小长度
	private boolean isTouch = true; // 是否可操作
	private Matrix mMatrix = new Matrix();
	private int lineAlpha = 50;// 连线的透明度
	
	public static final String PREFS = "lock_pref";//pref文件名
	public static final String VERSE = "verse";//锁屏方式pref值名称
	
	private String verse;

	public PatternPassWordView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initVerse();
	}

	public PatternPassWordView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initVerse();
	}

	public PatternPassWordView(Context context) {				
		super(context);
		initVerse();
	}

	@Override
	public void onDraw(Canvas canvas) {
		if (!isCache) {
			initCache();
		}

		drawToCanvas(canvas);
	}
	
	private void initVerse() {
		// 获取美言
		SharedPreferences settings = this.getContext().getSharedPreferences(PREFS, 0); 
		String initial_verse = getResources().getString(R.string.initial_verse);
		verse = settings.getString(VERSE, StringUtil.getNineStr(initial_verse));
		verse = StringUtil.getGridStr(verse);
	}
	
	private void drawToCanvas(Canvas canvas) {
		// mPaint.setColor(Color.RED);
		// Point p1 = mPoints[1][1];
		// Rect r1 = new Rect(p1.x - r,p1.y - r,p1.x +
		// pattern_round_click.getWidth() - r,p1.y+pattern_round_click.getHeight()-
		// r);
		// canvas.drawRect(r1, mPaint);
	
		// 设置画字的paint
		Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG
				| Paint.DEV_KERN_TEXT_FLAG);
		textPaint.setTextSize(36.0f);// 字体大小
		textPaint.setTypeface(Typeface.SANS_SERIF);// 采用默认的宽度
		textPaint.setColor(Color.WHITE);// 采用的白色
/*		textPaint.setShadowLayer(1f, 0,
		0,this.getResources().getColor(android.R.color.background_dark));// 阴影的设置
*/		textPaint.setTextAlign(Paint.Align.CENTER);// 字符的中心在屏幕的位置		
		// 画连线
		if (sPoints.size() > 0) {
			int tmpAlpha = mPaint.getAlpha();
			mPaint.setAlpha(lineAlpha);
			Point tp = sPoints.get(0);
			for (int i = 1; i < sPoints.size(); i++) {
				Point p = sPoints.get(i);
				drawLine(canvas, tp, p);
				tp = p;
			}
			if (this.movingNoPoint) {
				drawLine(canvas, tp, new Point((int) moveingX, (int) moveingY));
			}
			mPaint.setAlpha(tmpAlpha);
			lineAlpha = mPaint.getAlpha();
		}
		
		// 画所有圆和字
		for (int i = 0; i < mPoints.length; i++) {
			for (int j = 0; j < mPoints[i].length; j++) {
				Point p = mPoints[i][j];
				int index = i*mPoints[i].length+j;
				float textY=(textPaint.descent()-textPaint.ascent())/2;
				if (p.state == Point.STATE_CHECK) {
					canvas.drawBitmap(pattern_round_click, p.x - r, p.y - r,
							mPaint);//画圆
					//textPaint.setColor(Color.argb(214, 214, 214, 214));
					canvas.drawText(verse.substring(index,index+1), p.x, p.y+textY/2, textPaint);//画字
				} else if (p.state == Point.STATE_CHECK_ERROR) {
					canvas.drawBitmap(pattern_round_click_error, p.x - r,
							p.y - r, mPaint);
				} else {
					canvas.drawBitmap(pattern_round_original, p.x - r, p.y - r,
							mPaint);//画圆
					//textPaint.setColor(Color.argb(255, 255, 255, 255));
					canvas.drawText(verse.substring(index,index+1), p.x, p.y+textY/2, textPaint);//画字
				}
			}
		}
		// mPaint.setColor(Color.BLUE);
		// canvas.drawLine(r1.left+r1.width()/2, r1.top, r1.left+r1.width()/2,
		// r1.bottom, mPaint);
		// canvas.drawLine(r1.left, r1.top+r1.height()/2, r1.right,
		// r1.bottom-r1.height()/2, mPaint);

/*		// 画连线
		if (sPoints.size() > 0) {
			int tmpAlpha = mPaint.getAlpha();
			mPaint.setAlpha(lineAlpha);
			Point tp = sPoints.get(0);
			for (int i = 1; i < sPoints.size(); i++) {
				Point p = sPoints.get(i);
				drawLine(canvas, tp, p);
				tp = p;
			}
			if (this.movingNoPoint) {
				drawLine(canvas, tp, new Point((int) moveingX, (int) moveingY));
			}
			mPaint.setAlpha(tmpAlpha);
			lineAlpha = mPaint.getAlpha();
		}*/

	}

	/**
	 * 初始化Cache信息
	 * 
	 * @param canvas
	 */
	private void initCache() {

		w = this.getWidth();
		h = this.getHeight();
		float x = 0;
		float y = 0;

		// 以最小的为准
		// 纵屏
		if (w > h) {
			x = (w - h) / 2;
			w = h;
		}
		// 横屏
		else {
			y = (h - w) / 2;
			h = w;
		}

		pattern_round_original = BitmapFactory.decodeResource(
				this.getResources(), R.drawable.pattern_round_original);
		pattern_round_click = BitmapFactory.decodeResource(this.getResources(),
				R.drawable.pattern_round_click);

		pattern_round_click_error = BitmapFactory.decodeResource(
				this.getResources(), R.drawable.pattern_round_click_error);

		pattern_line = BitmapFactory.decodeResource(this.getResources(),
				R.drawable.pattern_line);
		pattern_line_semicircle = BitmapFactory.decodeResource(
				this.getResources(), R.drawable.pattern_line_semicircle);

		pattern_line_error = BitmapFactory.decodeResource(this.getResources(),
				R.drawable.pattern_line_error);
		pattern_line_semicircle_error = BitmapFactory.decodeResource(
				this.getResources(), R.drawable.pattern_line_semicircle_error);

		pattern_arrow = BitmapFactory.decodeResource(this.getResources(),
				R.drawable.pattern_arrow);
		// Log.d("Canvas w h :", "w:" + w + " h:" + h);

		// 计算圆圈图片的大小
		float canvasMinW = w;
		if (w > h) {
			canvasMinW = h;
		}
		float roundMinW = canvasMinW / 8.0f * 2;
		float roundW = roundMinW / 2.f;
		//
		float deviation = canvasMinW % (8 * 2) / 2;
		x += deviation;
		x += deviation;

		if (pattern_round_original.getWidth() > roundMinW) {
			float sf = roundMinW * 1.0f / pattern_round_original.getWidth(); // 取得缩放比例，将所有的图片进行缩放
			pattern_round_original = BitmapUtil.zoom(pattern_round_original, sf);
			pattern_round_click = BitmapUtil.zoom(pattern_round_click, sf);
			pattern_round_click_error = BitmapUtil.zoom(pattern_round_click_error,
					sf);

			pattern_line = BitmapUtil.zoom(pattern_line, sf);
			pattern_line_semicircle = BitmapUtil.zoom(pattern_line_semicircle, sf);

			pattern_line_error = BitmapUtil.zoom(pattern_line_error, sf);
			pattern_line_semicircle_error = BitmapUtil.zoom(
					pattern_line_semicircle_error, sf);
			pattern_arrow = BitmapUtil.zoom(pattern_arrow, sf);
			roundW = pattern_round_original.getWidth() / 2;
		}

		mPoints[0][0] = new Point(x + 0 + roundW, y + 0 + roundW);
		mPoints[0][1] = new Point(x + w / 2, y + 0 + roundW);
		mPoints[0][2] = new Point(x + w - roundW, y + 0 + roundW);
		mPoints[1][0] = new Point(x + 0 + roundW, y + h / 2);
		mPoints[1][1] = new Point(x + w / 2, y + h / 2);
		mPoints[1][2] = new Point(x + w - roundW, y + h / 2);
		mPoints[2][0] = new Point(x + 0 + roundW, y + h - roundW);
		mPoints[2][1] = new Point(x + w / 2, y + h - roundW);
		mPoints[2][2] = new Point(x + w - roundW, y + h - roundW);
		int k = 0;
		for (Point[] ps : mPoints) {
			for (Point p : ps) {
				p.index = k;
				k++;
			}
		}
		r = pattern_round_original.getHeight() / 2;// roundW;
		isCache = true;
	}

	/**
	 * 画两点的连接
	 * 
	 * @param canvas
	 * @param a
	 * @param b
	 */
	private void drawLine(Canvas canvas, Point a, Point b) {
		float ah = (float) MathUtil.distance(a.x, a.y, b.x, b.y);
		float degrees = getDegrees(a, b);
		// Log.d("=============x===========", "rotate:" + degrees);
		canvas.rotate(degrees, a.x, a.y);

		if (a.state == Point.STATE_CHECK_ERROR) {
			mMatrix.setScale((ah - pattern_line_semicircle_error.getWidth())
					/ pattern_line_error.getWidth(), 1);
			mMatrix.postTranslate(a.x, a.y - pattern_line_error.getHeight()
					/ 2.0f);
			canvas.drawBitmap(pattern_line_error, mMatrix, mPaint);
			canvas.drawBitmap(pattern_line_semicircle_error, a.x
					+ pattern_line_error.getWidth(),
					a.y - pattern_line_error.getHeight() / 2.0f, mPaint);
		} else {
			mMatrix.setScale((ah - pattern_line_semicircle.getWidth())
					/ pattern_line.getWidth(), 1);
			mMatrix.postTranslate(a.x, a.y - pattern_line.getHeight() / 2.0f);
			canvas.drawBitmap(pattern_line, mMatrix, mPaint);
			canvas.drawBitmap(pattern_line_semicircle, a.x + ah
					- pattern_line_semicircle.getWidth(),
					a.y - pattern_line.getHeight() / 2.0f, mPaint);
		}

		canvas.drawBitmap(pattern_arrow, a.x, a.y - pattern_arrow.getHeight()
				/ 2.0f, mPaint);

		canvas.rotate(-degrees, a.x, a.y);

	}

	public float getDegrees(Point a, Point b) {
		float ax = a.x;// a.index % 3;
		float ay = a.y;// a.index / 3;
		float bx = b.x;// b.index % 3;
		float by = b.y;// b.index / 3;
		float degrees = 0;
		if (bx == ax) // y轴相等 90度或270
		{
			if (by > ay) // 在y轴的下边 90
			{
				degrees = 90;
			} else if (by < ay) // 在y轴的上边 270
			{
				degrees = 270;
			}
		} else if (by == ay) // y轴相等 0度或180
		{
			if (bx > ax) // 在y轴的下边 90
			{
				degrees = 0;
			} else if (bx < ax) // 在y轴的上边 270
			{
				degrees = 180;
			}
		} else {
			if (bx > ax) // 在y轴的右边 270~90
			{
				if (by > ay) // 在y轴的下边 0 - 90
				{
					degrees = 0;
					degrees = degrees
							+ switchDegrees(Math.abs(by - ay),
									Math.abs(bx - ax));
				} else if (by < ay) // 在y轴的上边 270~0
				{
					degrees = 360;
					degrees = degrees
							- switchDegrees(Math.abs(by - ay),
									Math.abs(bx - ax));
				}

			} else if (bx < ax) // 在y轴的左边 90~270
			{
				if (by > ay) // 在y轴的下边 180 ~ 270
				{
					degrees = 90;
					degrees = degrees
							+ switchDegrees(Math.abs(bx - ax),
									Math.abs(by - ay));
				} else if (by < ay) // 在y轴的上边 90 ~ 180
				{
					degrees = 270;
					degrees = degrees
							- switchDegrees(Math.abs(bx - ax),
									Math.abs(by - ay));
				}

			}

		}
		return degrees;
	}

	/**
	 * 1=30度 2=45度 4=60度
	 * 
	 * @param tan
	 * @return
	 */
	private float switchDegrees(float x, float y) {
		return (float) MathUtil.pointTotoDegrees(x, y);
	}

	/**
	 * 取得数组下标
	 * 
	 * @param index
	 * @return
	 */
	public int[] getArrayIndex(int index) {
		int[] ai = new int[2];
		ai[0] = index / 3;
		ai[1] = index % 3;
		return ai;
	}

	/**
	 * 
	 * 检查
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	private Point checkSelectPoint(float x, float y) {
		for (int i = 0; i < mPoints.length; i++) {
			for (int j = 0; j < mPoints[i].length; j++) {
				Point p = mPoints[i][j];
				if (RoundUtil.checkInRound(p.x, p.y, r, (int) x, (int) y)) {
					return p;
				}
			}
		}
		return null;
	}

	/**
	 * 重置
	 */
	private void reset() {
		for (Point p : sPoints) {
			p.state = Point.STATE_NORMAL;
		}
		sPoints.clear();
		this.enableTouch();
	}

	/**
	 * 判断点是否有交叉 返回 0,新点 ,1 与上一点重叠 2,与非最后一点重叠
	 * 
	 * @param p
	 * @return
	 */
	private int crossPoint(Point p) {
		// 重叠的不最后一个则 reset
		if (sPoints.contains(p)) {
			if (sPoints.size() > 2) {
				// 与非最后一点重叠
				if (sPoints.get(sPoints.size() - 1).index != p.index) {
					return 2;
				}
			}
			return 1; // 与最后一点重叠
		} else {
			return 0; // 新点
		}
	}

	/**
	 * 添加一个点
	 * 
	 * @param point
	 */
	private void addPoint(Point point) {
		this.sPoints.add(point);
	}

	/**
	 * 转换为String
	 * 
	 * @param points
	 * @return
	 */
	private String toPointString() {
		if (sPoints.size() >= passwordMinLength) {//密码最少为4位
			StringBuffer sf = new StringBuffer();
			for (Point p : sPoints) {
				sf.append(",");
				sf.append(p.index);
			}
			return sf.deleteCharAt(0).toString();
		} else {
			return "";
		}
	}

	boolean movingNoPoint = false;
	float moveingX, moveingY;

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// 不可操作
		if (!isTouch) {
			return false;
		}

		movingNoPoint = false;

		float ex = event.getX();
		float ey = event.getY();
		boolean isFinish = false;
		boolean redraw = false;
		Point p = null;
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN: // 点下
			// 如果正在清除密码,则取消
			if (task != null) {
				task.cancel();
				task = null;
				Log.d("task", "touch cancel()");
			}
			// 删除之前的点
			reset();
			p = checkSelectPoint(ex, ey);
			if (p != null) {
				checking = true;
			}
			break;
		case MotionEvent.ACTION_MOVE: // 移动
			if (checking) {
				p = checkSelectPoint(ex, ey);
				if (p == null) {
					movingNoPoint = true;
					moveingX = ex;
					moveingY = ey;
				}
			}
			break;
		case MotionEvent.ACTION_UP: // 提起
			p = checkSelectPoint(ex, ey);
			checking = false;
			isFinish = true;
			break;
		}
		if (!isFinish && checking && p != null) {

			int rk = crossPoint(p);
			if (rk == 2) // 与非最后一重叠
			{
				// reset();
				// checking = false;

				movingNoPoint = true;
				moveingX = ex;
				moveingY = ey;

				redraw = true;
			} else if (rk == 0) // 一个新点
			{
				p.state = Point.STATE_CHECK;
				addPoint(p);
				redraw = true;
			}
			// rk == 1 不处理

		}

		// 是否重画
		if (redraw) {

		}
		if (isFinish) {
			if (this.sPoints.size() == 1) {
				this.reset();
			} else if (this.sPoints.size() < passwordMinLength
					&& this.sPoints.size() > 0) {
				// mCompleteListener.onPasswordTooMin(sPoints.size());
				error();
				clearPassword();
				Toast.makeText(this.getContext(), "密码太短，至少4位",
						Toast.LENGTH_SHORT).show();
			} else if (mCompleteListener != null) {
				if (this.sPoints.size() >= passwordMinLength) {
					this.disableTouch();
					mCompleteListener.onComplete(toPointString());
				}

			}
		}
		this.postInvalidate();
		return true;
	}

	/**
	 * 设置已经选中的为错误
	 */
	private void error() {
		for (Point p : sPoints) {
			p.state = Point.STATE_CHECK_ERROR;
		}
	}

	/**
	 * 设置为输入错误
	 */
	public void markError() {
		markError(CLEAR_TIME);
	}

	/**
	 * 设置为输入错误
	 */
	public void markError(final long time) {
		for (Point p : sPoints) {
			p.state = Point.STATE_CHECK_ERROR;
		}
		this.clearPassword(time);
	}

	/**
	 * 设置为可操作
	 */
	public void enableTouch() {
		isTouch = true;
	}

	/**
	 * 设置为不可操作
	 */
	public void disableTouch() {
		isTouch = false;
	}

	private Timer timer = new Timer();
	private TimerTask task = null;

	/**
	 * 清除密码
	 */
	public void clearPassword() {
		clearPassword(CLEAR_TIME);
	}

	/**
	 * 清除密码
	 */
	public void clearPassword(final long time) {
		if (time > 1) {
			if (task != null) {
				task.cancel();
				Log.d("task", "clearPassword cancel()");
			}
			lineAlpha = 130;
			postInvalidate();
			task = new TimerTask() {
				public void run() {
					reset();
					postInvalidate();
				}
			};
			Log.d("task", "clearPassword schedule(" + time + ")");
			timer.schedule(task, time);
		} else {
			reset();
			postInvalidate();
		}

	}

	//
	private OnCompleteListener mCompleteListener;

	/**
	 * @param mCompleteListener
	 */
	public void setOnCompleteListener(OnCompleteListener mCompleteListener) {
		this.mCompleteListener = mCompleteListener;
	}

	/**
	 * 取得密码
	 * 
	 * @return
	 */
	private String getPassword() {
		SharedPreferences settings = this.getContext().getSharedPreferences(
				this.getClass().getName(), 0);
		return settings.getString("password", ""); // , "0,1,2,3,4,5,6,7,8"
	}

	/**
	 * 密码是否为空
	 * 
	 * @return
	 */
	public boolean isPasswordEmpty() {
		return StringUtil.isEmpty(getPassword());
	}

	public boolean verifyPassword(String password) {
		boolean verify = false;
		if (cn.minelock.util.StringUtil.isNotEmpty(password)) {
			// 或者是超级密码
			if (password.equals(getPassword())
					|| password.equals("8,7,6,5,4,3,2,1,0")) {
				verify = true;
			}
		}
		return verify;
	}

	/**
	 * 设置密码
	 * 
	 * @param password
	 */
	public void resetPassWord(String password) {
		SharedPreferences settings = this.getContext().getSharedPreferences(
				this.getClass().getName(), 0);
		Editor editor = settings.edit();
		editor.putString("password", password);
		editor.commit();
	}

	public int getPasswordMinLength() {
		return passwordMinLength;
	}

	public void setPasswordMinLength(int passwordMinLength) {
		this.passwordMinLength = passwordMinLength;
	}

	/**
	 * 轨迹球画完成事件
	 * 
	 */
	public interface OnCompleteListener {
		/**
		 * 画完了
		 * 
		 * @param str
		 */
		public void onComplete(String password);
	}
}
