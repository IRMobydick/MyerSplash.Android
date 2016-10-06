package com.juniperphoton.myersplash.adapter;

import android.content.Context;
import android.graphics.RectF;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;
import com.juniperphoton.myersplash.R;
import com.juniperphoton.myersplash.callback.OnClickPhotoCallback;
import com.juniperphoton.myersplash.callback.OnClickQuickDownloadCallback;
import com.juniperphoton.myersplash.callback.OnLoadMoreListener;
import com.juniperphoton.myersplash.common.Constant;
import com.juniperphoton.myersplash.model.UnsplashImage;
import com.juniperphoton.myersplash.utils.LocalSettingHelper;

import java.util.List;

public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.PhotoViewHolder> {

    private final int FOOTER_FLAG_NOT_SHOW = 0;
    private final int FOOTER_FLAG_SHOW = 1;
    private final int FOOTER_FLAG_SHOW_END = 1 << 1 | FOOTER_FLAG_SHOW;

    private List<UnsplashImage> mData;

    private Context mContext;
    private OnLoadMoreListener mOnLoadMoreListener;
    private OnClickPhotoCallback mOnClickPhotoCallback;
    private OnClickQuickDownloadCallback mOnClickDownloadCallback;

    private boolean isAutoLoadMore = true;//是否自动加载，当数据不满一屏幕会自动加载
    private int footerFlag = FOOTER_FLAG_SHOW;

    public PhotoAdapter(List<UnsplashImage> data, Context context) {
        mData = data;
        mContext = context;
        if (data.size() >= 10) {
            isAutoLoadMore = true;
            footerFlag = FOOTER_FLAG_SHOW;
        } else if (data.size() > 0) {
            isAutoLoadMore = false;
            footerFlag = FOOTER_FLAG_SHOW_END;
        } else {
            isAutoLoadMore = false;
            footerFlag = FOOTER_FLAG_NOT_SHOW;
        }
    }

    @Override
    public PhotoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case PhotoViewHolder.TYPE_COMMON_VIEW: {
                View view = LayoutInflater.from(mContext).inflate(R.layout.row_photo, parent, false);
                return new PhotoViewHolder(view, viewType, footerFlag);
            }
            case PhotoViewHolder.TYPE_FOOTER_VIEW: {
                View view;
                if (footerFlag == FOOTER_FLAG_SHOW_END) {
                    view = LayoutInflater.from(mContext).inflate(R.layout.row_footer_end, parent, false);
                } else {
                    view = LayoutInflater.from(mContext).inflate(R.layout.row_footer, parent, false);
                }
                return new PhotoViewHolder(view, viewType, footerFlag);
            }
        }
        return null;
    }

    @SuppressWarnings("ResourceAsColor")
    @Override
    public void onBindViewHolder(final PhotoViewHolder holder, int position) {
        if (holder.getItemViewType() == PhotoAdapter.PhotoViewHolder.TYPE_COMMON_VIEW) {
            final int index = holder.getAdapterPosition();
            final UnsplashImage image = mData.get(index);
            final String regularUrl = image.getListUrl();

            int backColor = index % 2 == 0 ?
                    ContextCompat.getColor(mContext, R.color.BackColor1) :
                    ContextCompat.getColor(mContext, R.color.BackColor2);

            if (LocalSettingHelper.getBoolean(mContext, Constant.QUICK_DOWNLOAD_CONFIG_NAME, false)) {
                holder.DownloadRL.setVisibility(View.VISIBLE);
                holder.DownloadRL.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mOnClickDownloadCallback != null) {
                            mOnClickDownloadCallback.onClickQuickDownload(image);
                        }
                    }
                });
            } else {
                holder.DownloadRL.setVisibility(View.GONE);
            }
            if (holder.SimpleDraweeView != null) {
                holder.RootCardView.setBackground(new ColorDrawable(backColor));
                holder.SimpleDraweeView.setImageURI(regularUrl);
                holder.RippleMaskRL.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (Fresco.getImagePipeline().isInBitmapMemoryCache(Uri.parse(regularUrl))) {
                            int[] location = new int[2];
                            holder.SimpleDraweeView.getLocationOnScreen(location);
                            if (mOnClickPhotoCallback != null) {
                                mOnClickPhotoCallback.clickPhotoItem(new RectF(
                                        location[0], location[1],
                                        holder.SimpleDraweeView.getWidth(), holder.SimpleDraweeView.getHeight()), image, holder.SimpleDraweeView);
                            }
                        }
                    }
                });
            }
        }
    }

    @Override
    public int getItemCount() {
        if (mData == null) return 0;
        int size = footerFlag != FOOTER_FLAG_NOT_SHOW ? mData.size() + 1 : mData.size();
        return size;
    }

    @Override
    public int getItemViewType(int position) {
        if (isFooterView(position)) {
            return PhotoViewHolder.TYPE_FOOTER_VIEW;
        } else return PhotoViewHolder.TYPE_COMMON_VIEW;
    }

    private boolean isFooterView(int position) {
        return footerFlag != FOOTER_FLAG_NOT_SHOW && position >= getItemCount() - 1;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        final RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        startLoadMore(recyclerView, layoutManager);
    }

    private void startLoadMore(RecyclerView recyclerView, final RecyclerView.LayoutManager layoutManager) {
        if (mOnLoadMoreListener == null) {
            return;
        }

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    if (!isAutoLoadMore && findLastVisibleItemPosition(layoutManager) + 1 == getItemCount()) {
                        scrollLoadMore();
                    }
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (isAutoLoadMore && findLastVisibleItemPosition(layoutManager) + 1 == getItemCount()) {
                    scrollLoadMore();
                } else if (isAutoLoadMore) {
                    isAutoLoadMore = false;
                }
            }
        });
    }

    /**
     * 刷新加载更多的数据
     *
     * @param data 照片列表
     */
    public void setLoadMoreData(List<UnsplashImage> data) {
        int size = mData.size();
        mData.addAll(data);
        if (data.size() >= 10) {
            isAutoLoadMore = true;
            footerFlag |= FOOTER_FLAG_SHOW;
            notifyItemInserted(size);
        } else if (data.size() > 0) {
            isAutoLoadMore = false;
            footerFlag |= FOOTER_FLAG_SHOW;
            footerFlag |= FOOTER_FLAG_SHOW_END;
            notifyItemInserted(size);
        } else {
            isAutoLoadMore = false;
            footerFlag = FOOTER_FLAG_NOT_SHOW;
        }
    }

    private int findLastVisibleItemPosition(RecyclerView.LayoutManager layoutManager) {
        if (layoutManager instanceof LinearLayoutManager) {
            return ((LinearLayoutManager) layoutManager).findLastVisibleItemPosition();
        }
        return -1;
    }

    public void setOnLoadMoreListener(OnLoadMoreListener loadMoreListener) {
        mOnLoadMoreListener = loadMoreListener;
    }

    public void setOnClickItemListener(OnClickPhotoCallback callback) {
        mOnClickPhotoCallback = callback;
    }

    public void setOnClickDownloadCallback(OnClickQuickDownloadCallback callback) {
        mOnClickDownloadCallback = callback;
    }

    public UnsplashImage getFirstImage() {
        if (mData != null && mData.size() > 0) {
            return mData.get(0);
        }
        return null;
    }

    private void scrollLoadMore() {
        mOnLoadMoreListener.OnLoadMore();
    }

    class PhotoViewHolder extends RecyclerView.ViewHolder {
        static final int TYPE_COMMON_VIEW = 100000;
        static final int TYPE_FOOTER_VIEW = 100001;

        SimpleDraweeView SimpleDraweeView;
        CardView RootCardView;
        RelativeLayout DownloadRL;
        RelativeLayout RippleMaskRL;

        RelativeLayout FooterRL;

        PhotoViewHolder(View itemView, int type, int footerFlag) {
            super(itemView);
            if (type == TYPE_COMMON_VIEW) {
                SimpleDraweeView = (SimpleDraweeView) itemView.findViewById(R.id.row_photo_iv);
                RootCardView = (CardView) itemView.findViewById(R.id.row_photo_cv);
                DownloadRL = (RelativeLayout) itemView.findViewById(R.id.row_photo_download_rl);
                RippleMaskRL = (RelativeLayout) itemView.findViewById(R.id.row_photo_ripple_mask_rl);
            } else {
                FooterRL = (RelativeLayout) itemView.findViewById(R.id.row_footer_rl);
                if (footerFlag == FOOTER_FLAG_NOT_SHOW) {
                    FooterRL.setVisibility(View.INVISIBLE);
                }
            }
        }
    }
}
