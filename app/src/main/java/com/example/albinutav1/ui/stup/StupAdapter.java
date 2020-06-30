package com.example.albinutav1.ui.stup;

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

public class StupAdapter extends RecyclerView.Adapter<StupAdapter.ViewHolder> {


    private List<String> mCuloare;
    private List<String> mId;
    private List<String> mRasa;
    private List<String> mVarsta;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private Context contextMain;

    // data is passed into the constructor
    public StupAdapter(Context context, List<String> id, List<String> rasa, List<String> culoare, List<String> varsta) {
        this.mInflater = LayoutInflater.from(context);
        contextMain = context;
        this.mCuloare = culoare;
        this.mId = id;
        this.mRasa = rasa;
        System.out.println(mId.toString());
        this.mVarsta = varsta;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.row_stup, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final String id = mId.get(position);
        String culoare = mCuloare.get(position);
        String varsta = mVarsta.get(position);
        String rasa = mRasa.get(position);

        holder.colorView.setText(rasa);
        holder.varstaView.setText(culoare);
        holder.idView.setText(id);
        holder.rasaView.setText(varsta);

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
                                forMove.move(view, id, R.id.nav_moreinfoStup, "idStupDetail");
//                                Toast.makeText(getContext, "Momentan nu este disponibil!", Toast.LENGTH_LONG).show();
                                return true;
                            case "Editare":
                                forMove.move(view, id, R.id.nav_editStup, "idStupDetail");
                                return true;
                            default:
                                StupineSendServerAsyncT ob = new StupineSendServerAsyncT();
                                String ip = Objects.requireNonNull(contextMain).getResources().getString(R.string.ip);
                                ob.sendPost("DELETE", ip + "/stup/" + id + "/any", "", "", "", "", "", "");
                                Toast.makeText(
                                        contextMain,
                                        "Stupul cu id-ul: " + id + " a fost sters! Modificările vor apărea după" +
                                                " reîncărcarea paginii!",
                                        Toast.LENGTH_LONG
                                ).show();

//                                forMove.move(view, id_stupina, R.id.nav_stup, "idStupina");
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
        return mCuloare.get(id);
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
        TextView colorView;
        TextView rasaView;
        TextView varstaView;
        TextView idView;
        ImageView image;

        ViewHolder(View itemView) {
            super(itemView);
            rasaView = itemView.findViewById(R.id.location_row_stup);
            colorView = itemView.findViewById(R.id.data_row_stup);
            varstaView = itemView.findViewById(R.id.nr_stup_stup);

            idView = itemView.findViewById(R.id.id_raw_stup);
            image = itemView.findViewById(R.id.imageView3_stup);

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
