package com.example.albinutav1.ui.control;

import android.graphics.Color;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.albinutav1.R;
import com.example.albinutav1.ui.stupine.StupineSendServerAsyncT;
import com.example.albinutav1.utilityFunction;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Objects;

public class createTableControl {

    public TableRow makeLineForTable(final android.content.Context getContext,
                                     int i, final String idControl, final String idStupina, String data, String stare) {

        TableRow lineTable = new TableRow(getContext);
        lineTable.setGravity(Gravity.TOP);
        lineTable.setPadding(10, 10, 0, -30);
        if (i % 2 == 0) {
            lineTable.setBackgroundColor(Color.rgb(233, 206, 25));
        } else {
            lineTable.setBackgroundColor(Color.rgb(44, 44, 44));
        }
        lineTable.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.FILL_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));

        TextView countLine = new TextView(getContext);
        countLine.setText(idControl);
        countLine.setTextColor(Color.WHITE);
        countLine.setPadding(0, 0, -10, 0);
        countLine.setTextSize(17);
        lineTable.addView(countLine);

        final TextView location = new TextView(getContext);
        location.setTextSize(17);
        location.setTextColor(Color.WHITE);
        location.setText(idStupina);
        lineTable.addView(location);

        TextView nr_stupi = new TextView(getContext);
        nr_stupi.setText(data);
        nr_stupi.setTextSize(17);
        nr_stupi.setTextColor(Color.WHITE);
        lineTable.addView(nr_stupi);

        TextView placement_date = new TextView(getContext);
        placement_date.setText(stare);
        placement_date.setTextSize(17);
        placement_date.setTextColor(Color.WHITE);
        lineTable.addView(placement_date);

        final utilityFunction forMove = new utilityFunction();

        final ImageView options = new ImageView(getContext);
        options.setBackgroundResource(R.drawable.icon_more);
        options.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                PopupMenu popup = new PopupMenu(getContext, options);

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
                                forMove.move(view, idControl, R.id.nav_infocontrol, "idControl");
                                return true;

                            default:
                                StupineSendServerAsyncT ob = new StupineSendServerAsyncT();
                                String ip = Objects.requireNonNull(getContext).getResources().getString(R.string.ip);
                                ob.sendPost("DELETE", ip + "/controlVeterinar/" + idControl,
                                        "", "", "", "", "", "");
                                Toast.makeText(
                                        getContext,
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
        lineTable.addView(options);
        return lineTable;
    }
}
