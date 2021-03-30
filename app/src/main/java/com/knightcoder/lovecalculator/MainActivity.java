package com.knightcoder.lovecalculator;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.content.res.ResourcesCompat;

import com.google.android.material.textfield.TextInputEditText;
import com.pranavpandey.android.dynamic.toasts.DynamicToast;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {


    //base url
    private static final String URL="https://love-calculator.p.rapidapi.com/";



    private Toolbar toolbar;
    private AppCompatButton btnCheck;
    private TextView fnameTextView, snameTextView, resultTextView, percent;
    private TextInputEditText fnameEditText, snameEditText;
    private CardView cardViewResult,cardViewInputs;
    private ProgressBar progressBar;


    //retrofit instance
    private final Retrofit retrofit=new Retrofit.Builder()
            .baseUrl(URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build();
    private final ApiClient apiClient=retrofit.create(ApiClient.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();



    }



    private void initViews() {


        //initialize xml views
        toolbar=findViewById(R.id.toolbar);
        cardViewResult =findViewById(R.id.card_result);
        cardViewInputs=findViewById(R.id.card_inputs);
        fnameEditText =findViewById(R.id.fname_txt);
        snameEditText =findViewById(R.id.sname_txt);
        btnCheck=findViewById(R.id.btn_check);
        fnameTextView =findViewById(R.id.fname);
        snameTextView =findViewById(R.id.sname);
        resultTextView =findViewById(R.id.result);
        percent=findViewById(R.id.percentage);
        progressBar=findViewById(R.id.progress);



        toolbar.setTitle(R.string.toolbar_title);
        setSupportActionBar(toolbar);
        btnCheck.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {

        if (btnCheck.getText().toString().equalsIgnoreCase(getString(R.string.check))){

            progressBar.setVisibility(View.VISIBLE);
            cardViewInputs.setVisibility(View.GONE);
            btnCheck.setVisibility(View.GONE);
            String fnameValue= fnameEditText.getText().toString();
            String snameValue= snameEditText.getText().toString();

            if (fnameValue.isEmpty() || snameValue.isEmpty()){
                noDataHideProgress("Enter Couple Name!");
                cardViewInputs.setVisibility(View.VISIBLE);
                btnCheck.setVisibility(View.VISIBLE);


            }else{

                Call<Lovers> loversCall=apiClient.getLoversPercentage(fnameValue,snameValue);
                loversCall.enqueue(new Callback<Lovers>() {
                    @Override
                    public void onResponse(@NonNull Call<Lovers> call,@NonNull Response<Lovers> response) {

                        if (response.isSuccessful()){

                            Lovers loverBody=response.body();

                            Typeface typefaceHeart=ResourcesCompat.getFont(MainActivity.this,R.font.fullyhearts);
                            Typeface typeface=ResourcesCompat.getFont(MainActivity.this,R.font.hearts);
                            Typeface typefaceDesc=ResourcesCompat.getFont(MainActivity.this,R.font.roboto);

                            if (loverBody != null) {

                                String fname = loverBody.getFname();
                                String sname=loverBody.getSname();
                                String result=loverBody.getResult();
                                String valuePercentage=String.valueOf(loverBody.getPercentage());

                                fnameTextView.setText(fname);
                                snameTextView.setText(sname);
                                resultTextView.setText(String.format("%s%s%s","“", result,"”"));
                                percent.setText(String.format("%s%%", valuePercentage));
                                TextView andOperatorTextView= findViewById(R.id.and);

                                fnameTextView.setTypeface(typeface);
                                snameTextView.setTypeface(typeface);
                                resultTextView.setTypeface(typefaceDesc);

                                andOperatorTextView.setTypeface(typefaceHeart);

                                progressBar.setVisibility(View.GONE);
                                btnCheck.setText(R.string.try_again);
                                cardViewResult.setVisibility(View.VISIBLE);
                                cardViewInputs.setVisibility(View.GONE);
                                btnCheck.setVisibility(View.VISIBLE);


                            }else {
                                noDataHideProgress("Operation failed! retry.");

                            }


                        }else {
                            noDataHideProgress("Operation failed! retry.");

                        }

                    }

                    @Override
                    public void onFailure(@NonNull Call<Lovers> call, @NonNull Throwable t) {
                        noDataHideProgress("No Internet Connection! retry");

                    }
                });
            }
        }else{
            btnCheck.setText(R.string.check);
            btnCheck.setVisibility(View.VISIBLE);
            cardViewResult.setVisibility(View.GONE);
            cardViewInputs.setVisibility(View.VISIBLE);
        }
    }


    //if no response or no internet connection hide progressbar and display a toast
    private void noDataHideProgress(String msg) {
        progressBar.setVisibility(View.GONE);
        DynamicToast.makeError(MainActivity.this,msg , Toast.LENGTH_SHORT).show();
        btnCheck.setText(R.string.check);
        btnCheck.setVisibility(View.VISIBLE);
        cardViewResult.setVisibility(View.GONE);
        cardViewInputs.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main,menu);

        return super.onCreateOptionsMenu(menu);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){

            case R.id.share_app:
                Intent intentShare=new Intent(Intent.ACTION_SEND);
                intentShare.setType("text/plain");
                intentShare.putExtra(Intent.EXTRA_SUBJECT,"Lovers Match App");
                intentShare.putExtra(Intent.EXTRA_TEXT,getPackageName());
                startActivity(Intent.createChooser(intentShare,"Share App with Friends"));
                return true;
            case R.id.contact_us:
                Intent intentEmail=new Intent(Intent.ACTION_SENDTO,Uri.fromParts("mailto","theknightcoder@aol.com",null));
                intentEmail.putExtra(Intent.EXTRA_SUBJECT,"Lovers match's App");
                startActivity(Intent.createChooser(intentEmail,"Send Email"));

                return true;
            case R.id.rate_us:
                Intent intentRate=new Intent(Intent.ACTION_VIEW);
                intentRate.setData(Uri.parse("https://play.google.com/store/apps/details?id="+getPackageName()));
                startActivity(Intent.createChooser(intentRate,"Select one"));
                return true;
            case R.id.more_apps:
                Intent intentMoreApps=new Intent(Intent.ACTION_VIEW);
                intentMoreApps.setData(Uri.parse("https://play.google.com/store/apps/developer?id=TheKnightCoder"));
                startActivity(Intent.createChooser(intentMoreApps,"Select one"));
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }


    }
}