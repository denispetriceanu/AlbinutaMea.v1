package com.example.albinutav1.ui.control;

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

public class ControlAdapter extends RecyclerView.Adapter<ControlAdapter.ViewHolder> {


    private List<String> mDataControl;
    private List<String> mId;
    private List<String> mStare;
    private List<String> mIdStupina;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private Context contextMain;

    // data is passed into the constructor
    ControlAdapter(Context context, List<String> id, List<String> data, List<String> stare, List<String> idStupina) {
        this.mInflater = LayoutInflater.from(context);
        contextMain = context;
        this.mId = id;
        this.mDataControl = data;
        this.mStare = stare;
        this.mIdStupina = idStupina;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.row_control, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        String data = mDataControl.get(position);
        String stare = mStare.get(position);
        final String id = mId.get(position);
        String id_stupina = mIdStupina.get(position);

        holder.dataControlView.setText(data);
        holder.stareView.setText(stare);
        holder.idView.setText(id);
        holder.idStupinaView.setText(id_stupina);

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
                                forMove.move(view, id, R.id.nav_infocontrol, "idControl");
                                return true;

                            default:
                                StupineSendServerAsyncT ob = new StupineSendServerAsyncT();
                                String ip = contextMain.getResources().getString(R.string.ip);
                                ob.sendPost("DELETE", ip + "/controlVeterinar/" + id, "",
                                        "", "", "", "", "");
                                Toast.makeText(
                                        contextMain,
                                        "Acest tratament a fost stears. Modificările vor apărea " +
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
        TextView dataControlView;
        TextView stareView;
        TextView idStupinaView;
        TextView idView;
        ImageView image;

        ViewHolder(View itemView) {
            super(itemView);
            dataControlView = itemView.findViewById(R.id.location_row_control);
            stareView = itemView.findViewById(R.id.data_row_control);
            idStupinaView = itemView.findViewById(R.id.nr_stup_control);
            idView = itemView.findViewById(R.id.id_raw_control);
            image = itemView.findViewById(R.id.imageView3_control);

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
