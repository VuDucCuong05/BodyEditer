package net.braincake.bodytune.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import net.braincake.bodytune.R;
import net.braincake.bodytune.databinding.ItemImageBinding;
import net.braincake.bodytune.model.MyImage;

import java.util.List;


public class AdapterImage extends RecyclerView.Adapter<AdapterImage.ViewHolder> {
    private Context context;
    private List<MyImage> listMyImages;

    public AdapterImage(Context context, List<MyImage> listLanguageR) {
        this.context = context;
        this.listMyImages = listLanguageR;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ItemImageBinding binding;

        public ViewHolder(ItemImageBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemImageBinding binding = ItemImageBinding.inflate(LayoutInflater.from(context), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        MyImage item = listMyImages.get(position);

//        RequestOptions requestOptions = new RequestOptions();
//        requestOptions = requestOptions.transform(new CenterCrop(), new RoundedCorners(8));
        Glide.with(holder.itemView.getContext())
//                .applyDefaultRequestOptions(new RequestOptions())
                .load(item.getUrlMyImage())
//                .apply(requestOptions)
                .placeholder(R.drawable.ic_camera)
                .error(R.drawable.ic_camera)
                .into(holder.binding.imgPhoto);
        holder.binding.llImg.setVisibility(View.VISIBLE);

        if (item.getIdMyImage() == 0) {
            holder.binding.llImgCamera.setVisibility(View.VISIBLE);
            holder.binding.llImg.setVisibility(View.GONE);
        }
        if (item.getSelectMyImage()) {
            holder.binding.llSelect.setVisibility(View.VISIBLE);
        } else {
            holder.binding.llSelect.setVisibility(View.GONE);
        }

        holder.binding.getRoot().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                for (int i = 0; i < listMyImages.size(); i++) {
                    if (listMyImages.get(i).getIdMyImage() != 0) {
                        listMyImages.get(i).setSelectMyImage(i == position);
                    }
                }
                item.setSelectMyImage(true);

                reloadData();
                if (item.getIdMyImage() == 0) {
                    itemClickCameraListener.onItemClick(item);
                } else {
                    itemClickListener.onItemClick(item);
                }
            }
        });
    }


    @Override
    public int getItemCount() {
        return listMyImages.size();
    }

    public void updateData(List<MyImage> newList) {
        this.listMyImages = newList;
        reloadData();
    }

    public void updateSelect() {
        for (MyImage myImage : listMyImages) {
            myImage.setSelectMyImage(false);
        }
        reloadData();
    }


    @SuppressLint("NotifyDataSetChanged")
    public void reloadData() {
        notifyDataSetChanged();
    }


    private OnItemClickListener itemClickListener;

    public interface OnItemClickListener {
        void onItemClick(MyImage language);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.itemClickListener = listener;
    }

    private OnItemClickListener itemClickCameraListener;

    public interface OnItemClickCameraListener {
        void itemClickCameraListener(MyImage language);
    }

    public void setOnItemClickCameraListener(OnItemClickListener listener) {
        this.itemClickCameraListener = listener;
    }
}
