package net.braincake.bodytune.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import net.braincake.bodytune.R;
import net.braincake.bodytune.controls.MenuInfo;

public class MainMenuAdapter extends RecyclerView.Adapter<MainMenuAdapter.ViewHolder> {
    private Context context;
    private final int defaultTextColor = Color.parseColor("#FF000000");
    private List<MenuInfo> mInfoList;
    private OnItemClickListener onItemClickListener;
    private int selected = 0;
    private boolean selectedMode;
    private final int selectedTextColor = Color.parseColor("#FFF82B34");

    public interface OnItemClickListener {
        void onItemClick(int i);
    }

    public MainMenuAdapter(List<MenuInfo> list, Context context2) {
        this.mInfoList = list;
        this.context = context2;
    }

    public void setSelectedMode(boolean z) {
        this.selectedMode = z;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView icon;
        TextView title;

        ViewHolder(View view) {
            super(view);
            view.setOnClickListener(this);
            this.icon = (ImageView) view.findViewById(R.id.menuIcon);
            this.title = (TextView) view.findViewById(R.id.menuTitle);
        }

        public void onClick(View view) {
            MainMenuAdapter.this.selected = getAdapterPosition();
            if (MainMenuAdapter.this.selectedMode) {
                MainMenuAdapter.this.notifyDataSetChanged();
            }
            if (MainMenuAdapter.this.onItemClickListener != null) {
                MainMenuAdapter.this.onItemClickListener.onItemClick(MainMenuAdapter.this.selected);
            }
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new ViewHolder(View.inflate(this.context, R.layout.item_bottom_main_menu, null));
    }

    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        MenuInfo menuInfo = this.mInfoList.get(i);
        viewHolder.icon.setImageResource(menuInfo.iconResource);
        viewHolder.title.setText(menuInfo.title);
        if (this.selectedMode) {
            viewHolder.icon.setSelected(i == this.selected);
            viewHolder.title.setTextColor(i == this.selected ? this.selectedTextColor : this.defaultTextColor);
        }
    }

    @Override
    public int getItemCount() {
        return this.mInfoList.size();
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener2) {
        this.onItemClickListener = onItemClickListener2;
    }
}
