package net.braincake.bodytune.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.recyclerview.widget.RecyclerView;
import net.braincake.bodytune.R;
import net.braincake.bodytune.controls.ApplicationClass;

public class SkinColorListAdapter extends RecyclerView.Adapter<SkinColorListAdapter.ViewHolder> {
    private Context context;
    private OnItemClickListener onItemClickListener;
    private int selected = 0;

    public interface OnItemClickListener {
        void onItemClick(int i);
    }

    public SkinColorListAdapter(Context context2) {
        this.context = context2;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        Canvas canvas;
        ImageView colorPlace;
        ImageView selectedColorStroke;

        ViewHolder(View view) {
            super(view);
            view.setOnClickListener(this);
            this.colorPlace = (ImageView) view.findViewById(R.id.colorPlace);
            this.selectedColorStroke = (ImageView) view.findViewById(R.id.selectedColorStroke);
            Bitmap copy = ((BitmapDrawable) this.colorPlace.getDrawable()).getBitmap().copy(Bitmap.Config.ARGB_8888, true);
            this.colorPlace.setImageBitmap(copy);
            this.canvas = new Canvas(copy);
        }

        public void onClick(View view) {
            int i = SkinColorListAdapter.this.selected;
            SkinColorListAdapter.this.selected = getAdapterPosition();
            SkinColorListAdapter.this.notifyItemChanged(i);
            SkinColorListAdapter skinColorListAdapter = SkinColorListAdapter.this;
            skinColorListAdapter.notifyItemChanged(skinColorListAdapter.selected);
            if (SkinColorListAdapter.this.onItemClickListener != null) {
                SkinColorListAdapter.this.onItemClickListener.onItemClick(SkinColorListAdapter.this.selected);
            }
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new ViewHolder(View.inflate(this.context, R.layout.item_skincolor, null));
    }

    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        viewHolder.canvas.drawColor(ApplicationClass.listOfSkinColor[i], PorterDuff.Mode.SRC_IN);
        viewHolder.selectedColorStroke.setVisibility(i == this.selected ? 0 : 4);
    }

    @Override
    public int getItemCount() {
        return ApplicationClass.listOfSkinColor.length;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener2) {
        this.onItemClickListener = onItemClickListener2;
    }
}
