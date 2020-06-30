package com.example.albinutav1.ui.hranire;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.example.albinutav1.R;
import com.example.albinutav1.ui.stupine.StupineSendServerAsyncT;
import com.example.albinutav1.utilityFunction;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Objects;

public class HranireAdapter extends RecyclerView.Adapter<HranireAdapter.ViewHolder> {


    private List<String> mDataHranire;
    private List<String> mId;
    private List<String> mIdStup;
    private List<String> mTipulHranirii;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private Context contextMain;

    HranireAdapter(Context context, List<String> id, List<String> data_hranire, List<String> id_stup, List<String> tipul_hranirii) {
        this.mInflater = LayoutInflater.from(context);
        contextMain = context;
        this.mId = id;
        this.mDataHranire = data_hranire;
        this.mIdStup = id_stup;
        this.mTipulHranirii = tipul_hranirii;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.row_hranire, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final String id = mId.get(position);
        String data_hranire = mDataHranire.get(position);
        String tip_hranire = mTipulHranirii.get(position);
        String id_stup = mIdStup.get(position);

        holder.idView.setText(id);
        holder.data_hranire_view.setText(data_hranire);
        holder.tip_hranire_view.setText(tip_hranire);
        holder.idStup_view.setText(id_stup);

        final utilityFunction forMove = new utilityFunction();
        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                PopupMenu popup = new PopupMenu(contextMain, holder.image);

                try {
                    Field[] fields = popup.getClass().getDeclaredFields();
                    for (Field field : fields) {
                        if ("mPopup".equals(field.getName())) {
                            field.setAccessible(true);
                            Object menuPopupHelper = field.get(popup);
                            Class<?> classPopupHelper = Class.forName(menuPopupHelper.getClass().getName());
                            Method setForceIcons = classPopupHelper
                                    .getMethod("setForceShowIcon", boolean.class);
                            setForceIcons.invoke(menuPopupHelper, true);
                            break;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                popup.getMenuInflater().inflate(R.menu.popup_menu2, popup.getMenu());

                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getTitle().toString()) {
                            case "Mai mult":
                                forMove.move(view, id, R.id.nav_showMoreHranire, "idHranire");
                                return true;

                            default:
                                StupineSendServerAsyncT ob = new StupineSendServerAsyncT();
                                String ip = Objects.requireNonNull(contextMain).getResources().getString(R.string.ip);
                                ob.sendPost("DELETE", ip + "/hranire/" + id, "",
                                        "", "", "", "", "");
                                Toast.makeText(
                                        contextMain,
                                        "Acest tratament a fost sters. Modificările vor apărea " +
                                                "după ce veți reîmprospăta pagina!",
                                        Toast.LENGTH_LONG
                                ).show();
                                return true;
                        }
                    }
                });
                popup.show();
            }
        });
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mId.size();
    }

    // convenience method for getting data at click position
    String getItem(int id) {
        return mId.get(id);
    }

    // allows clicks events to be caught
    void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView data_hranire_view;
        TextView tip_hranire_view;
        TextView idStup_view;
        TextView idView;
        ImageView image;

        ViewHolder(View itemView) {
            super(itemView);
            data_hranire_view = itemView.findViewById(R.id.location_row_hranire);
            tip_hranire_view = itemView.findViewById(R.id.data_row_hranire);
            idStup_view = itemView.findViewById(R.id.nr_stup_hranire);
            idView = itemView.findViewById(R.id.id_raw_hranire);
            image = itemView.findViewById(R.id.imageView3_hranire);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(final View view) {
//            int itemPosition = mData.getChildLayoutPosition(view);
//            String item = mData.get(itemPosition);
//            Toast.makeText(getContext, item, Toast.LENGTH_LONG).show();
        }
    }
}
