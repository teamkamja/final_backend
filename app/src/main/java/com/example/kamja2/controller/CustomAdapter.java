package com.example.kamja2.controller;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.kamja2.R;


public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder> {
    private Cursor cursor;
    private Context context;
    private OnItemClickListener listener;
    // 생성자에서 커서를 받아옴
    public CustomAdapter(Context context,Cursor cursor) {
        this.cursor = cursor;
        this.context = context;
    }
    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    // ViewHolder 클래스 구현
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView itemName;
        public TextView itemScript;
        public ImageView itemImage;
        Button btnSave;
        OnItemClickListener listener;

        public ViewHolder(View itemView) {
            super(itemView);
            itemName = itemView.findViewById(R.id.itemName); // 예시로 TextView를 가정합니다.
            itemScript = itemView.findViewById(R.id.itemScript); // 예시로 TextView를 가정합니다.
            itemImage = itemView.findViewById(R.id.itemImage); // 예시로 TextView를 가정합니다.
            btnSave = itemView.findViewById(R.id.btnSave);
        }

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // 아이템 뷰 생성
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.search_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // 데이터를 아이템에 바인딩
        if (cursor != null && cursor.moveToPosition(position)) {
            String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
            String id = cursor.getString(cursor.getColumnIndexOrThrow("_id"));
            String price = cursor.getString(cursor.getColumnIndexOrThrow("price"));
            String script = cursor.getString(cursor.getColumnIndexOrThrow("script"));
            String recipe = cursor.getString(cursor.getColumnIndexOrThrow("recipe"));
            //TODO: 이미지 디비에 추가하기 적용
            //String ImageSource = cursor.getString(cursor.getColumnIndexOrThrow("image"));
            String ImageSource = String.valueOf(Images[Integer.valueOf(id)%Images.length]);
            Integer image = context.getResources().getIdentifier(ImageSource, "drawable",context.getPackageName());
            holder.itemName.setText(name);
            holder.itemScript.setText(script);
            holder.itemImage.setImageResource(image);
            holder.btnSave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    holder.btnSave.setBackgroundColor(Color.parseColor("#87C9A1"));
                }
            });

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context,RecipeDocActivity.class);
                    intent.putExtra("id",id);
                    intent.putExtra("price",price);
                    intent.putExtra("name",name);
                    intent.putExtra("script",script);
                    intent.putExtra("recipe",recipe);
                    context.startActivity(intent);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return (cursor == null) ? 0 : cursor.getCount();
    }
    Integer Images[] = {R.drawable.heart_food,R.drawable.heart_food2,R.drawable.heart_food3,R.drawable.heart_food4};
}
