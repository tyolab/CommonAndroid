package au.com.tyo.android.adapter;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.chauthai.swipereveallayout.SwipeRevealLayout;
import com.chauthai.swipereveallayout.ViewBinderHelper;

import au.com.tyo.android.CommonApplicationImpl;
import au.com.tyo.android.CommonController;
import au.com.tyo.android.R;

/**
 * Created by Eric Tang (eric.tang@tyo.com.au) on 17/8/17.
 */

public class SwipeDeleteItemFactory extends ListItemFactory {

    private static final String TAG = "SwipeDeleteItemFactory";
    private ViewBinderHelper binderHelper;

    public interface OnSwipeActionDeleteListener {
        void onDelete(Object object);
    }

    public interface OnSwipeActionClickListener {
        void onMainCellClick(Object object);
    }

    private OnSwipeActionClickListener onSwipeActionListener;
    private OnSwipeActionDeleteListener onDeleteListener;

    public SwipeDeleteItemFactory(Context context) {
        super(context);
        init();
    }

    public SwipeDeleteItemFactory(Context context, int resId) {
        super(context, resId);
        init();
    }

    private void init() {
        binderHelper = new ViewBinderHelper();
    }

    public OnSwipeActionClickListener getOnSwipeActionListener() {
        return onSwipeActionListener;
    }

    public void setOnSwipeActionListener(OnSwipeActionClickListener onSwipeActionListener) {
        this.onSwipeActionListener = onSwipeActionListener;
    }

    public OnSwipeActionDeleteListener getOnDeleteListener() {
        return onDeleteListener;
    }

    public void setOnDeleteListener(OnSwipeActionDeleteListener onDeleteListener) {
        this.onDeleteListener = onDeleteListener;
    }

    @Override
    protected InflaterFactory.ViewHolder newViewHolderInstance() {
        return new ViewHolder();
    }

    @Override
    protected InflaterFactory.ViewHolder createViewHolder(View convertView, ViewGroup parent) {
        ViewHolder holder = (ViewHolder) super.createViewHolder(convertView, parent);

        holder.deleteView = holder.view.findViewById(R.id.delete_layout);
        holder.swipeLayout = (SwipeRevealLayout) holder.view.findViewById(R.id.swipe_layout);
        holder.mainCell = holder.view.findViewById(R.id.main_cell);
        return holder;
    }

    @Override
    public void bindData(InflaterFactory.ViewHolder holder, final Object obj) {
        ViewHolder theHolder = (ViewHolder) holder;
        View view = holder.view;

        if (theHolder.swipeLayout != null) {
            binderHelper.bind(theHolder.swipeLayout, obj.toString());

            final CommonController controller = CommonApplicationImpl.getInstance();
            theHolder.deleteView.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try {
                                if (getOnDeleteListener() != null)
                                    getOnDeleteListener().onDelete(obj);
                            } catch (Exception ex) {
                                Log.e(TAG, "can't delete form " + obj.toString());
                            }
                        }
                    }
            );
        }

        if (null != theHolder.mainCell && null != getOnSwipeActionListener())
            theHolder.mainCell.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getOnSwipeActionListener().onMainCellClick(obj);
                }
            });

        super.bindData(holder, obj);
    }

    public static class ViewHolder extends InflaterFactory.ViewHolder {
        View deleteView;
        View mainCell;
        SwipeRevealLayout swipeLayout;
    }
}
