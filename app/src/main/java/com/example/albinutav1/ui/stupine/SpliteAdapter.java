package com.example.albinutav1.ui.stupine;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import androidx.recyclerview.widget.RecyclerView;

import com.example.albinutav1.R;
import com.example.albinutav1.utilityFunction;

import java.util.List;
import java.util.Vector;

public class SpliteAdapter extends RecyclerView.Adapter<SpliteAdapter.ViewHolder> {

    private List<String> mId;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private Context contextMain;
    public Vector<String> StupForNewStupina = new Vector<>();

    // data is passed into the constructor
    SpliteAdapter(Context context, List<String> id) {
        this.mInflater = LayoutInflater.from(context);
        contextMain = context;
        this.mId = id;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.row_splite, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final String id = mId.get(position);
//        String varsta = mVarsta.get(position);
        String textShow = "Stupul cu ID-ul: " + id;
        holder.checkBoxShow.setText(textShow);
        holder.checkBoxShow.isChecked();
        holder.checkBoxShow.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    StupForNewStupina.add(id);
                    System.out.println("Ai apasa pe " + id);
                } else {
                    StupForNewStupina.remove(StupForNewStupina.indexOf(id));
                    System.out.println("Ai debifat " + id);
                }
            }
        });
        new utilityFunction().setStupNewStupina(StupForNewStupina);
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
        CheckBox checkBoxShow;
        ViewHolder(View itemView) {
            super(itemView);
            checkBoxShow = itemView.findViewById(R.id.checkBox4);
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
