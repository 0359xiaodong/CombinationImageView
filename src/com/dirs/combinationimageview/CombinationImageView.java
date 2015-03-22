package com.dirs.combinationimageview;

import java.util.Vector;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class CombinationImageView extends LinearLayout {
	private final String tag = CombinationImageView.class.getSimpleName();

	private final int MAX_SIZE = 9;
	private Object lock = null;
	private final int ROW_COUNT = 3;

	private ImageView mImageView = null;
	private Context mContext = null;

	private int mImgSpace = 5;

	private int mImgWidth = 0;
	private int mImgHeight = 0;

	private int mViewWidth = 0;
	private int mViewHeight = 0;

	private Vector<Bitmap> mBitVec = null;

	public CombinationImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		Log.d(tag, "CombinationImageView create");
		mContext = context;
		init();
	}

	public CombinationImageView(Context context) {
		super(context);
		Log.d(tag, "CombinationImageView create");
		mContext = context;
		init();
	}

	private void init() {
		lock = new Object();
		Log.d(tag, "call init");
		mBitVec = new Vector<Bitmap>(MAX_SIZE);
		View view = LayoutInflater.from(mContext).inflate(
				R.layout.layout_imageview, this, true);
		mImageView = (ImageView) view.findViewById(R.id.iv);
		mImageView.setWillNotDraw(true);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		mViewHeight = getHeight();
		mViewWidth = getWidth();
		Log.d(tag, "onMeasure ViewHeight£º" + mViewHeight + " -- Width"
				+ mViewWidth);
		if (mViewHeight != 0 && mViewWidth != 0) {
			synchronized (lock) {
				lock.notify();
			}
		}
	}

	public void loadImg(Vector<String> vec) throws Exception {
		Log.d(tag, "call loadImg");
		if (vec.size() > MAX_SIZE) {
			throw new Exception("MAX_SIZE is " + MAX_SIZE);
		}
		new AsyncImgLoad().execute(vec);
	}

	class AsyncImgLoad extends AsyncTask<Vector<String>, Integer, Boolean> {

		@Override
		protected Boolean doInBackground(Vector<String>... params) {
			synchronized (lock) {
				try {
					lock.wait();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			preLoad();
			Log.d(tag, "doInBackground");
			Vector<String> vec = params[0];
			if (vec.size() == 0) {
				return false;
			}
			BitmapFactory.Options options = new BitmapFactory.Options();
			for (String path : vec) {
				Bitmap bm = BitmapFactory.decodeFile(path, options);
				mBitVec.add(Bitmap.createScaledBitmap(bm, mImgWidth,
						mImgHeight, true));
			}
			return true;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			Log.d(tag, "onPostExecute Result:" + result);
			if (result) {
				Log.d(tag, "call invalidate");
				invalidate();
			}
		}

		private void preLoad() {
			mImgHeight = (int) mViewHeight / ROW_COUNT;
			mImgWidth = (int) mViewWidth / ROW_COUNT;
		}
	}

	@Override
	protected void onDetachedFromWindow() {
		// TODO Auto-generated method stub
		super.onDetachedFromWindow();
		Log.d(tag, "call onDetachedFromWindow");
		for (Bitmap bm : mBitVec) {
			if (bm != null && !bm.isRecycled()) {
				bm.recycle();
			}
		}
	}

	@Override
	protected void dispatchDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.dispatchDraw(canvas);
		Log.d(tag, "call dispatchDraw");
		int mTotalWidth = 0;
		int mTotalHeight = 0;
		for (int i = 0; i < mBitVec.size(); i++) {
			Bitmap bm = mBitVec.get(i);
			if (bm != null) {
				int mWdith = bm.getWidth() + mImgSpace;
				int mHeight = bm.getHeight() + mImgSpace;
				canvas.drawBitmap(bm, mTotalWidth, mTotalHeight, null);
				if ((i + 1) % 3 == 0) {
					mTotalWidth = 0;
					mTotalHeight += mHeight;
				} else {
					mTotalWidth += mWdith;
				}

			}
		}
	}

}
