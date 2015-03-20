package com.dirs.combinationimageview;

import java.util.Vector;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;

public class CombinationImageView extends ImageView {
	private final String tag = CombinationImageView.class.getSimpleName();
	private final boolean VLOG = true;

	private final int MAX_SIZE = 9;
	private final int ROW_IMG_COUNT = 3;

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
		Log.d(tag, "调用init");
		mBitVec = new Vector<Bitmap>(MAX_SIZE);
		View view = LayoutInflater.from(mContext).inflate(
				R.layout.layout_imageview, null);
		mImageView = (ImageView) view.findViewById(R.id.iv);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int parentWidth = MeasureSpec.getSize(widthMeasureSpec);
		int parentHeight = MeasureSpec.getSize(heightMeasureSpec);
		this.setMeasuredDimension(parentWidth, parentHeight);
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		mViewHeight = getHeight();
		mViewWidth = getWidth();
		Log.d(tag, "控件高度：" + mViewHeight + " -- " + mViewWidth);
	}

	private void preLoad() {
		mImageView.invalidate();
		int maxWidth = mViewHeight;
		if (mViewHeight != mViewWidth) {
			maxWidth = mViewHeight > mViewWidth ? mViewHeight : mViewWidth;
		}
		Log.d(tag, "控件大小：" + maxWidth);
		mImgHeight = maxWidth / ROW_IMG_COUNT;
		mImgWidth = maxWidth / ROW_IMG_COUNT;
		Log.d(tag, "图片高度:" + mImgHeight + " -- 宽度:" + mImgWidth);

	}

	public void loadImg(Vector<String> vec) throws Exception {
		Log.d(tag, "调用loadImg");
		if (vec.size() > MAX_SIZE) {
			throw new Exception("图片数组长度大于最大限制");
		}
		preLoad();
		new AsyncImgLoad().execute(vec);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
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

	class AsyncImgLoad extends AsyncTask<Vector<String>, Integer, Boolean> {

		@Override
		protected Boolean doInBackground(Vector<String>... params) {
			Vector<String> vec = params[0];
			if (vec.size() == 0) {
				return false;
			}
			BitmapFactory.Options options = new BitmapFactory.Options();
			for (String path : vec) {
				Log.d(path, "加载图片：" + path + "目标宽度：" + mImgWidth);
				Bitmap bm = BitmapFactory.decodeFile(path, options);
				mBitVec.add(Bitmap.createScaledBitmap(bm,35,35,true));
			}
			return true;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			Log.d(tag, "异步执行结果:" + result);
			if (result) {
				mImageView.invalidate();
			}
		}

	}

}
