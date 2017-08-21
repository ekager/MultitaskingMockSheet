package com.ekager.multitaskingmock2;

import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gordonwong.materialsheetfab.MaterialSheetFab;
import com.gordonwong.materialsheetfab.MaterialSheetFabEventListener;

import java.util.ArrayList;

/**
 * MainActivity for multitasking mock with sheet implementation
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private MaterialSheetFab materialSheetFab;
    private int statusBarColor;
    private TextView textView;
    private Button button;
    private EditText searchBar;
    private TextView deleteView;
    ArrayList<TextView> myArrayList = new ArrayList<>();

    private LinearLayout myRoot;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupFab();
        textView = (TextView) findViewById(R.id.textView);
        button = (Button) findViewById(R.id.search_button);
        searchBar = (EditText) findViewById(R.id.editText);

        deleteView = (TextView) findViewById(R.id.fab_sheet_item_delete);
        deleteView.setOnClickListener(this);

        myRoot = (LinearLayout) findViewById(R.id.sheet_items);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textView.setText(searchBar.getText());
            }
        });

        addNewTab("www.google.com");
        addNewTab("www.yahoo.com");
        updateColors();
    }

    @Override
    public void onBackPressed() {
        if (materialSheetFab.isSheetVisible()) {
            materialSheetFab.hideSheet();
        } else {
            super.onBackPressed();
        }
    }

    /**
     * Sets up the Floating action button.
     */
    private void setupFab() {

        Fab fab = (Fab) findViewById(R.id.fab);
        View sheetView = findViewById(R.id.fab_sheet);
        View overlay = findViewById(R.id.dim_overlay);
        int sheetColor = getResources().getColor(R.color.background_sheet);
        int fabColor = getResources().getColor(R.color.colorFloatingActionButtonTint);

        // Initialize material sheet FAB
        materialSheetFab = new MaterialSheetFab<>(fab, sheetView, overlay,
                sheetColor, fabColor);

        // Set material sheet event listener
        materialSheetFab.setEventListener(new MaterialSheetFabEventListener() {
            @Override
            public void onShowSheet() {
                // Save current status bar color
                statusBarColor = getStatusBarColor();
                // Set darker status bar color to match the dim overlay
                setStatusBarColor(getResources().getColor(R.color.theme_primary_dark2));
            }

            @Override
            public void onHideSheet() {
                // Restore status bar color
                setStatusBarColor(statusBarColor);
            }
        });

        // Set material sheet item click listeners
        findViewById(R.id.temp_tab).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.temp_tab: /* Add new tab */
                showDialog();
                break;
            case R.id.tab_info:
                TextView thisText = (TextView) v;
                textView.setText(thisText.getText().toString());
                searchBar.setText(thisText.getText().toString());
                ((LinearLayout) v.getParent()).removeView(v);
                removeFromList(thisText);
                materialSheetFab.hideSheet();
                updateColors();
                break;
            case R.id.fab_sheet_item_delete:
                myRoot.removeAllViews();
                break;
            default:
                materialSheetFab.hideSheet();
                break;
        }
    }

    private void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // Get the layout inflater
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.alert_dialog_view, null);
        builder.setView(dialogView);
        final EditText ed = (EditText) dialogView.findViewById(R.id.edit_dialog);

        builder.setTitle("Enter new site here")
                .setPositiveButton("Add tab", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        addNewTab(ed.getText().toString());
                        dialog.dismiss();
                    }
                });

        AlertDialog alert = builder.create();
        alert.show();
    }


    private void updateColors() {
        if (myArrayList != null) {
            for (TextView t : myArrayList) {
                if (!t.getText().toString().equals(searchBar.getText().toString())) {
                    t.setTextColor(getResources().getColor(R.color.text_black_87));

                    Drawable drawable = getResources().getDrawable(R.drawable.ic_link);
                    drawable.setTint(getResources().getColor(R.color.text_black_87));
                    t.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
                } else {
                    Drawable drawable = getResources().getDrawable(R.drawable.ic_link);
                    drawable.setTint(getResources().getColor(R.color.nice_blue));
                    t.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
                    t.setTextColor(getResources().getColor(R.color.nice_blue));
                }
            }
        }
    }

    private void removeFromList(TextView t) {
        myArrayList.remove(t);
    }

    private boolean isADuplicate(String url) {
        if (myArrayList != null) {
            for (TextView t : myArrayList) {
                if (t.getText().toString().equals(url)) {
                    return true;
                }
            }
        }
        return false;
    }

    private int getNumberOfTabs() {
        return myArrayList.size();
    }

    private void addNewTab(String url) {
        if (!isADuplicate(url) && getNumberOfTabs() < 5) {
            final View a = LayoutInflater.from(this).inflate(R.layout.link_textview, myRoot, false);
            final TextView myTextView = (TextView) a.findViewById(R.id.tab_info);
            myTextView.setText(url);
            myArrayList.add(myTextView);
            myRoot.addView(a);
            myTextView.setOnClickListener(this);
            updateColors();
        }
    }

    private int getStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return getWindow().getStatusBarColor();
        }
        return 0;
    }

    private void setStatusBarColor(int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(color);
        }
    }

}
