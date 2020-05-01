package com.example.root.uasinta;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class FavoriteAdapter extends RecyclerView.Adapter<FavoriteAdapter.ViewHolder> {

    private Context context;
    private List<ResultData> results;
    public Intent OpenBrowser;
    public Intent UrlBrowser;

    NumberFormat formatterMoney = NumberFormat.getCurrencyInstance(new Locale("in", "ID"));

    public FavoriteAdapter(Context context, List<ResultData> results) {
        this.context = context;
        this.results = results;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_custom_favorite, parent, false);
        ViewHolder holder = new ViewHolder(v);

        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final ResultData result = results.get(position);

        holder.nama.setText(result.getNamaProduk());
        holder.status.setText(formatterMoney.format(result.getHarga()));
        holder.from.setText(result.getFrom());
        holder.nama_preview.setText(result.getNamaProduk());

        final String id = result.getId();


        if (result.getFrom().equals("TokoPedia")) {
            holder.from.setTextColor(Color.parseColor("#FF00C74D"));

        } else if (result.getFrom().equals("BukaLapak")) {
            holder.from.setTextColor(Color.parseColor("#FFDA0035"));
        } else if (result.getFrom().equals("Shopee")) {
            holder.from.setTextColor(Color.parseColor("#FFFD4500"));
        }else if (result.getFrom().equals("Twitter")) {
            holder.from.setTextColor(Color.parseColor("#FF3F85F5"));
        }

        Picasso.with(context)
                .load(result.getLinkGambar())
                .placeholder(R.drawable.ic_image)
                .error(R.drawable.ic_image)
                .into(holder.img);

        Picasso.with(context)
                .load(result.getLinkGambar())
                .placeholder(R.drawable.ic_image_white)
                .error(R.drawable.ic_image_white)
                .into(holder.img_preview);

        holder.img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.dialogTutorial.show();
            }
        });

        holder.item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        context);
                alertDialogBuilder.setMessage("Buka produk ini di website " + result.getFrom() + " ?");
                alertDialogBuilder
                        // .setMessage("Pilih opsi chat!")
                        .setCancelable(false)
                        .setPositiveButton("Buka", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                OpenBrowser = new Intent(Intent.ACTION_VIEW);
                                OpenBrowser.setData(Uri.parse(result.getLinkProduk()));
                                UrlBrowser = Intent.createChooser(OpenBrowser, "Choose a Map App");
                                context.startActivity(OpenBrowser);
                            }
                        })

                        .setNegativeButton("Batal", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        });

               /* alertDialogBuilder.setNeutralButton("WhatsApps", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {


                    }
                });*/
                AlertDialog alertDialog = alertDialogBuilder.create();

                alertDialog.show();
            }
        });

        holder.hapus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.deleteToFavorit(id);

            }
        });
    }

    @Override
    public int getItemCount() {
        return results.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView nama, status, from, nama_preview;
        private ImageButton hapus;
        private LinearLayout item;
        private ImageView img, img_preview;
        Dialog dialogTutorial;

        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl("http://maseko.000webhostapp.com/")
                .addConverterFactory(GsonConverterFactory.create());

        Retrofit retrofit = builder.build();

        UserClient userClient = retrofit.create(UserClient.class);

        public ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);

            nama = itemView.findViewById(R.id.nama);
            status = itemView.findViewById(R.id.status);
            hapus = itemView.findViewById(R.id.hapus);
            item = itemView.findViewById(R.id.item);
            from = itemView.findViewById(R.id.from);
            img = itemView.findViewById(R.id.img);

            dialogTutorial = new Dialog(context);
            dialogTutorial.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialogTutorial.setContentView(R.layout.layout_preview_image);

            img_preview = dialogTutorial.findViewById(R.id.img_preview);
            nama_preview = dialogTutorial.findViewById(R.id.nama_preview);

        }

        @Override
        public void onClick(View view) {

        }

        public void deleteToFavorit(String id) {
            final ProgressDialog dialog = new ProgressDialog(context);
            dialog.setMessage("Loading ...");
            dialog.setCancelable(false);
            dialog.show();

            Call<ResponeAddToFavorite> call = userClient.deleteFavorite(id);
            call.enqueue(new Callback<ResponeAddToFavorite>() {
                @SuppressLint("RestrictedApi")
                @Override
                public void onResponse(Call<ResponeAddToFavorite> call, Response<ResponeAddToFavorite> response) {
                    if (response.isSuccessful()) {
                        dialog.hide();
                        Toast.makeText(context, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    } else {
                        dialog.hide();
                        Toast.makeText(context, "Gagal", Toast.LENGTH_SHORT).show();
                    }
                }
                @Override
                public void onFailure(Call<ResponeAddToFavorite> call, Throwable t) {
                    dialog.hide();
                    Toast.makeText(context, "onFailure", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    public void updateList(List<ResultData> newList){

        results = new ArrayList<>();
        results.addAll(newList);
        notifyDataSetChanged();

    }
}
