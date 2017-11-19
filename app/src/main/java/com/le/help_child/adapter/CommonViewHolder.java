package com.le.help_child.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

/**
 * 通用的ViewHolder
 * 
 */
public class CommonViewHolder {
	private SparseArray<View> mViews;
	private static boolean conver = true;
	private View mConverView;

	private CommonViewHolder(Context context, ViewGroup parent, int layoutId,
                             int position) {
		this.mViews = new SparseArray<View>();
		this.mConverView = LayoutInflater.from(context).inflate(layoutId,
				parent, false);
		this.mConverView.setTag(this);
	}

	public static CommonViewHolder get(Context context, View convertView,
                                       ViewGroup parent, int layoutId, int position) {
		CommonViewHolder viewHolder = null;
		if (conver) {
			if (convertView == null) {
				viewHolder = new CommonViewHolder(context, parent, layoutId,
						position);
			} else {
				viewHolder = (CommonViewHolder) convertView.getTag();
			}
		} else {
			viewHolder = new CommonViewHolder(context, parent, layoutId,
					position);
		}
		return viewHolder;
	}


	@SuppressWarnings("unchecked")
	public <T extends View> T getView(int viewId) {
		View view = mViews.get(viewId);
		if (view == null) {
			view = mConverView.findViewById(viewId);
			mViews.put(viewId, view);
		}
		return (T) view;
	}

	public View getConvertView() {
		return this.mConverView;
	}
	


	public CommonViewHolder setText(int viewId, String text) {
		TextView tv = getView(viewId);
		tv.setText(text);
		return this;
	}


	public CommonViewHolder setText(int viewId, float number) {
		StringBuilder builder = new StringBuilder();
		TextView tv = getView(viewId);
		tv.setText(builder.append(number));
		return this;
	}


	public CommonViewHolder setText(int viewId, int number) {
		StringBuilder builder = new StringBuilder();
		TextView tv = getView(viewId);
		tv.setText(builder.append(number));
		return this;
	}

	public CommonViewHolder setText(int viewId, Double number) {
		StringBuilder builder = new StringBuilder();
		TextView tv = getView(viewId);
		tv.setText(builder.append(number));
		return this;
	}

	public CommonViewHolder setButton(int viewId, String text) {
		Button btn = getView(viewId);
		btn.setText(text);
		return this;
	}

	public CommonViewHolder setImageById(int viewId, int resId) {
		ImageView image = getView(viewId);
		image.setImageResource(resId);
		return this;
	}

	public CommonViewHolder setImageByBitmap(int viewId, Bitmap bitmap) {
		ImageView image = getView(viewId);
		image.setImageBitmap(bitmap);
		return this;
	}

	public CommonViewHolder setImageByBitmap(Context context, int viewId,
                                             String url, int resId) {
//		ImageView image = getView(viewId);
//		 Picasso.with(context).load(url).into(image);
		return this;
	}

//	public CommonViewHolder setImageUri(int viewId){
//		SimpleDraweeView imDraweeView = (SimpleDraweeView) mConverView.findViewById(viewId);
//		imDraweeView.setImageURI(Uri.parse("http://img.my.csdn.net/uploads/201501/30/1422600516_2905.jpg"));
//		return this;
//	}
	
	public CommonViewHolder setButtonListener(int viewId,
                                              View.OnClickListener listener) {
		getView(viewId).setOnClickListener(listener);
		return this;
	}

	public CommonViewHolder setButtonLongListener(int viewId,
                                                  View.OnLongClickListener listener) {
		getView(viewId).setOnLongClickListener(listener);
		return this;
	}

	public CommonViewHolder setTextListener(int viewId,
                                            View.OnClickListener listener) {
		getView(viewId).setOnClickListener(listener);
		return this;
	}

	public CommonViewHolder setImageListener(int viewId,
                                             View.OnClickListener listener) {
		getView(viewId).setOnClickListener(listener);
		return this;
	}
	
	public CommonViewHolder setRatingBar(int viewId, int rating){
		RatingBar ratingBar = getView(viewId);
		ratingBar.setRating(rating);
		return this;
	}

}
