package com.dcinspirations.on_screenhelper;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import static android.content.Context.CLIPBOARD_SERVICE;

/**
 * Created by pc on 2/18/2018.
 */

public class notes_adapter extends RecyclerView.Adapter<notes_adapter.viewHolder>{

    private List<notemodel> objectlist;
    private LayoutInflater inflater;
    private Context context;

    public notes_adapter(Context context, List<notemodel> objectlist) {
        inflater = LayoutInflater.from(context);
        this.objectlist = objectlist;
        this.context=context;
    }


    @Override
    public viewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.notesample,parent,false);
        viewHolder vholder = new viewHolder(view);
        return vholder;
    }

    @Override
    public void onBindViewHolder(viewHolder holder, int position) {
        notemodel current = objectlist.get(position);
//        holder.setAlarm(current);
        holder.setData(current,position);

    }

    @Override
    public int getItemCount() {
        return objectlist.size();
    }


    public void refreshEvents() {
        notifyDataSetChanged();
    }


    public class viewHolder extends RecyclerView.ViewHolder{
        private TextView text,time;
        private int position;
        private notemodel currentObject;

        public void setPosition(int position) {
            this.position = position;
        }

        public viewHolder(final View itemView){
            super(itemView);
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    PopupMenu pm = new PopupMenu(v.getContext(),itemView, Gravity.END);
                    pm.getMenuInflater().inflate(R.menu.dash,pm.getMenu());
                    pm.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()){
                                case R.id.delete:
                                    new DatabaseClass(context).deleteData(objectlist.get(position).getId());
                                    objectlist.remove(position);
                                    refreshEvents();
                                    return true;
                                case R.id.copy:
                                    ClipboardManager clipboard = (ClipboardManager) context.getSystemService(CLIPBOARD_SERVICE);
                                    ClipData clip = ClipData.newPlainText("text", currentObject.getText());
                                    clipboard.setPrimaryClip(clip);
                            }
                            return false;
                        }
                    });
                    pm.show();
                    return false;
                }
            });
            text = itemView.findViewById(R.id.text);
            time = itemView.findViewById(R.id.time);
        }



        public void setData(notemodel current, int position) {

            this.text.setText(current.getText());
            this.time.setText(current.getTime());

            this.position = position;
            this.currentObject=current;
        }



    }
}
