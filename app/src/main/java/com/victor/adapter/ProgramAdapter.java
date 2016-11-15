package com.victor.adapter;import android.content.Context;import android.support.v7.widget.RecyclerView;import android.view.LayoutInflater;import android.view.View;import android.view.ViewGroup;import android.widget.AdapterView;import android.widget.ImageView;import com.bumptech.glide.Glide;import com.victor.data.ProgramData;import com.victor.seagull.R;import com.victor.view.ColorTextView;import java.util.List;/** * Created by victor on 2016/10/19. */public class ProgramAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{    private String TAG = "ProgramAdapter";    private final LayoutInflater mLayoutInflater;    private final Context mContext;    private static List<ProgramData> programDatas;    protected int mHeaderCount = 1;//头部View个数    protected int mBottomCount = 1;//底部View个数    private int ITEM_TYPE_HEADER = 0;    private int ITEM_TYPE_CONTENT = 1;    private int ITEM_TYPE_BOTTOM = 2;    private boolean isHeaderVisible = true;    private boolean isFooterVisible = true;    private static AdapterView.OnItemClickListener mOnItemClickListener;    public List<ProgramData> getProgramDatas() {        return programDatas;    }    public void setProgramDatas(List<ProgramData> programDatas) {        this.programDatas = programDatas;    }    public ProgramAdapter(Context context, AdapterView.OnItemClickListener listener) {        mContext = context;        mOnItemClickListener = listener;        mLayoutInflater = LayoutInflater.from(context);    }    public void setHeaderVisible (boolean visible) {        isHeaderVisible = visible;        mHeaderCount = 1;        if (!isHeaderVisible) {            mHeaderCount = 0;        }        notifyDataSetChanged();    }    public void setFooterVisible (boolean visible) {        isFooterVisible = visible;        mBottomCount = 1;        if (!isFooterVisible) {            mBottomCount = 0;        }        notifyDataSetChanged();    }    @Override    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {        if (viewType == ITEM_TYPE_HEADER) {            return onCreateHeaderView(parent);        } else if (viewType == ITEM_TYPE_CONTENT) {            return onCreateContentView(parent);        } else if (viewType == ITEM_TYPE_BOTTOM) {            return onCreateBottomView(parent);        }        return null;    }    public RecyclerView.ViewHolder onCreateHeaderView (ViewGroup parent){        return new HeaderViewHolder(mLayoutInflater.inflate(R.layout.recyclerview_content_img, parent, false));    }    public RecyclerView.ViewHolder onCreateContentView (ViewGroup parent){        return new ContentViewHolder(mLayoutInflater.inflate(R.layout.recyclerview_content_img, parent, false));    }    public RecyclerView.ViewHolder onCreateBottomView (ViewGroup parent) {        return new BottomViewHolder(mLayoutInflater.inflate(R.layout.recyclerview_content_img, parent, false));    }    @Override    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {        if (holder instanceof HeaderViewHolder) {        } else if (holder instanceof ContentViewHolder) {            ImageView mIvImg = ((ContentViewHolder) holder).mIvImg;            ColorTextView mCtvTitle = ((ContentViewHolder) holder).mCtvTitle;            if (position < programDatas.size()) {                ProgramData data = programDatas.get(position);                mCtvTitle.setText(data.title);                Glide.with(mContext).load(data.imgId).fitCenter().error(R.mipmap.default_img).into(mIvImg);            }        } else if (holder instanceof BottomViewHolder) {        }    }    @Override    public int getItemViewType(int position) {        int ITEM_TYPE = ITEM_TYPE_CONTENT;        int dataItemCount = getContentItemCount();        if (mHeaderCount != 0 && position < mHeaderCount) {//头部View            ITEM_TYPE = ITEM_TYPE_HEADER;        } else if (mBottomCount != 0 && position >= (mHeaderCount + dataItemCount)) {//底部View            ITEM_TYPE = ITEM_TYPE_BOTTOM;        }        return ITEM_TYPE;    }    public boolean isHeaderView(int position) {        return mHeaderCount != 0 && position < mHeaderCount;    }    public boolean isBottomView(int position) {        return mBottomCount != 0 && position >= (mHeaderCount + getContentItemCount());    }    public int getContentItemCount() {        return programDatas == null ? 0 : programDatas.size();    }    @Override    public int getItemCount() {        return mHeaderCount + getContentItemCount() + mBottomCount;    }    public static class ContentViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{        ImageView mIvImg;        ColorTextView mCtvTitle;        ContentViewHolder(View view) {            super(view);            mIvImg = (ImageView) itemView.findViewById(R.id.iv_img);            mCtvTitle = (ColorTextView) view.findViewById(R.id.ctv_title);            view.setOnClickListener(this);        }        @Override        public void onClick(View view) {            if (getAdapterPosition() < programDatas.size()) {                if (mOnItemClickListener != null) {                    mOnItemClickListener.onItemClick(null,view,getAdapterPosition(),0);                }            }        }    }    public static class HeaderViewHolder extends RecyclerView.ViewHolder {        ImageView mIvImg;        ColorTextView mCtvTitle;        public HeaderViewHolder(View view) {            super(view);            mIvImg = (ImageView) view.findViewById(R.id.iv_img);            mCtvTitle = (ColorTextView) view.findViewById(R.id.ctv_title);        }    }    public static class BottomViewHolder extends RecyclerView.ViewHolder {        ImageView mIvImg;        ColorTextView mCtvTitle;        public BottomViewHolder(View view) {            super(view);            mIvImg = (ImageView) view.findViewById(R.id.iv_img);            mCtvTitle = (ColorTextView) view.findViewById(R.id.ctv_title);        }    }}