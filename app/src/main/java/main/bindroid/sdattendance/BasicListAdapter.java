package main.bindroid.sdattendance;

/**
 * Created by vikassinghsuriyal on 9/4/15.
 */

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import java.util.List;

public class BasicListAdapter extends RecyclerView.Adapter<CustomViewHolder> {

	private List<FeedItem> feedItemList;
	private Context mContext;

	private OnClickListener onClickListener;

	public BasicListAdapter(Context context, List<FeedItem> feedItemList) {
		this.feedItemList = feedItemList;
		this.mContext = context;
	}

	@Override
	public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
		View view = LayoutInflater.from(viewGroup.getContext()).inflate(
				R.layout.card_view, null);

		CustomViewHolder viewHolder = new CustomViewHolder(view);
		return viewHolder;
	}

	@Override
	public void onBindViewHolder(final CustomViewHolder holder, int position) {
		final FeedItem feedItem = feedItemList.get(position);
		if (feedItem != null) {
			holder.mobile.setText(feedItem.getEmpMobile());
			holder.mobile.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View view) {
					Intent intent = new Intent(Intent.ACTION_CALL);

					intent.setData(Uri.parse("tel:" + feedItem.getEmpMobile()));
					mContext.startActivity(intent);
				}
			});
			holder.dept.setText(feedItem.getEmpDept());
			holder.name.setText(feedItem.getEmpName());
			holder.seat.setText(feedItem.getEmpSeat());
			holder.ext.setText(feedItem.getExt());
			holder.itemView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					v.setTag(holder.seat.getText().toString());
					onClickListener.onClick(v);
				}
			});
			holder.email.setText(feedItem.getEmpEmail());
			holder.id.setText(feedItem.getEmpId());
		}

	}

	@Override
	public int getItemCount() {
		return (null != feedItemList ? feedItemList.size() : 0);
	}

	public void setOnClickListener(OnClickListener onClickListener) {
		this.onClickListener = onClickListener;
	}
}
