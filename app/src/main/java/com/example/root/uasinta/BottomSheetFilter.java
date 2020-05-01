package com.example.root.uasinta;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class BottomSheetFilter extends BottomSheetDialogFragment {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.seekBar2)
    SeekBar seekBar;
    @BindView(R.id.textView)
    TextView textView;
    @BindView(R.id.btn_filter_now)
    TextView btnFilter;
    @BindView(R.id.btn_reset)
    TextView btnRiset;

    static String harga = null;

    private View.OnClickListener onFilterClickListener;
    private View.OnClickListener onResetClickListener;

    Dialog dialogFilterManual;

    int progres = 0;

    NumberFormat formatterMoney;

    public static BottomSheetFilter create() {
        BottomSheetFilter frag = new BottomSheetFilter();
        Bundle args = new Bundle();
        frag.setArguments(args);
        return frag;
    }

    public BottomSheetFilter() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // setHasOptionsMenu(true);
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View contentView = View.inflate(getContext(), R.layout.layout_bottom_sheet_filter, null);
        ButterKnife.bind(this, contentView);

        btnFilter.setOnClickListener(onFilterClickListener);
        btnRiset.setOnClickListener(onResetClickListener);

        formatterMoney = NumberFormat.getCurrencyInstance(new Locale("in", "ID"));

        textView.setText(formatterMoney.format(seekBar.getProgress()));
        harga = String.valueOf(seekBar.getProgress());
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {


            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progres = progress;
                textView.setText(formatterMoney.format(progress));
                harga = String.valueOf(progress);

                // txtDurationMute.setText(seekBar.getProgress());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                //  txtDurationMute.setText(seekBar.getProgress());
                textView.setText(formatterMoney.format(progres));
                harga = String.valueOf(progres);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //  txtDurationMute.setText(seekBar.getProgress());
                textView.setText(formatterMoney.format(progres));
                harga = String.valueOf(progres);
            }
        });

        return contentView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initializeBottomSheet();
    }

    private void initializeBottomSheet() {
        mToolbar.setTitle("Filter");
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
    }

    public void show(AppCompatActivity activity) {
        super.show(activity.getSupportFragmentManager(), "BottomSheetFilter");
    }

    public void setOnFilterClickListener(View.OnClickListener listener) {
        this.onFilterClickListener = listener;
    }

    public void setOnResetClickListener(View.OnClickListener listener) {
        this.onResetClickListener = listener;
    }

    @OnClick(R.id.textView)
    public void onFilterNowClicked() {
        dialogFilterManual = new Dialog(getContext());
        dialogFilterManual.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogFilterManual.setContentView(R.layout.layout_dialog_input_filter);

        final EditText editText = dialogFilterManual.findViewById(R.id.harga);
        TextView simpan = dialogFilterManual.findViewById(R.id.simpan);

        editText.setText(Integer.toString(seekBar.getProgress()));

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editText.getText().toString().length() <= 0){
                    editText.setText("0");
                }

            }
        });


        simpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (Integer.parseInt(editText.getText().toString()) >= 50000000){
                    String hasil = formatterMoney.format(50000000);
                    textView.setText(hasil);
                    seekBar.setProgress(50000000);
                    dialogFilterManual.hide();
                } else if (Integer.parseInt(editText.getText().toString()) <= 100000){
                    String hasil = formatterMoney.format(100000);
                    textView.setText(hasil);
                    seekBar.setProgress(100000);
                    dialogFilterManual.hide();
                } else if (editText.getText().toString().length() <= 0){
                    String hasil = formatterMoney.format(100000);
                    textView.setText(hasil);
                    seekBar.setProgress(100000);
                    dialogFilterManual.hide();
                } else {
                    String hasil = formatterMoney.format(Integer.parseInt(editText.getText().toString()));
                    textView.setText(hasil);
                    seekBar.setProgress(Integer.parseInt(editText.getText().toString()));
                    dialogFilterManual.hide();
                }

            }
        });

        dialogFilterManual.show();
    }
    /*  @OnClick(R.id.btn_filter_now)
      public void onFilterNowClicked() {
          dismiss();
      }*/
    /*@OnClick(R.id.btn_reset)
    public void onResetClicked() {
        seekBar.setProgress(100);
    }*/
}
