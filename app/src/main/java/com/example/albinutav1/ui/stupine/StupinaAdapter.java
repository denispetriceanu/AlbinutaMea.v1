package com.example.albinutav1.ui.stupine;

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
import com.example.albinutav1.utilityFunction;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Objects;

public class StupinaAdapter extends RecyclerView.Adapter<StupinaAdapter.ViewHolder> {


    private List<String> mData;
    private List<String> mId;
    private List<String> mLocatie;
    private List<String> mNrStupi;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private android.content.Context contextMain;

    // data is passed into the constructor
    StupinaAdapter(Context context, List<String> id, List<String> locatie, List<String> nr_stupi, List<String> data) {
        this.mInflater = LayoutInflater.from(context);
        contextMain = context;
        this.mData = data;
        this.mId = id;
        this.mLocatie = locatie;
        this.mNrStupi = nr_stupi;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.row_stupina, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        String date = mData.get(position);
        String location = mLocatie.get(position);
        final String id = mId.get(position);
        String nr_comb = mNrStupi.get(position);

        holder.myTextView.setText(location);
        holder.dataView.setText(date);
        holder.idView.setText(id);
        holder.nrStupiView.setText(nr_comb);

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
                popup.getMenuInflater().inflate(R.menu.popup_menu, popup.getMenu());

                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getTitle().toString()) {
                            case "Mai mult":
                                forMove.move(view, id, R.id.nav_stup, "idStupina");
                                return true;
                            case "Editare":
                                forMove.move(view, id, R.id.nav_edit, "idStupina");
                                return true;
                            case "Splite":
                                forMove.move(view, id, R.id.splite_stupina, "idStupina");
                                return true;
                            default:
                                StupineSendServerAsyncT ob = new StupineSendServerAsyncT();
                                String ip = Objects.requireNonNull(contextMain).getResources().getString(R.string.ip);
                                ob.sendPost("DELETE", ip + "/stupina/" + id + "/any",
                                        "", "", "", "", "", "");
                                Toast.makeText(
                                        contextMain,
                                        "Stupina cu id-ul: " + id + " a fost stearsa. Modificările vor apărea " +
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
        return mData.size();
    }

    // convenience method for getting data at click position
    String getItem(int id) {
        return mData.get(id);
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
        TextView myTextView;
        TextView dataView;
        TextView idView;
        TextView nrStupiView;
        ImageView image;

        ViewHolder(View itemView) {
            super(itemView);
            myTextView = itemView.findViewById(R.id.location_row);
            dataView = itemView.findViewById(R.id.data_row);
            idView = itemView.findViewById(R.id.id_raw);
            nrStupiView = itemView.findViewById(R.id.nr_stup);
            image = itemView.findViewById(R.id.imageView3);

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
