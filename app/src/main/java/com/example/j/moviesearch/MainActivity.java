package com.example.j.moviesearch;

import android.app.Dialog;
import android.content.Context;


import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import android.text.InputFilter;
import android.text.Spanned;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {
    EditText searchInput;
    RecyclerView recyclerView;
    MovieAdapter adapter;
    Dialog dialog;
    String query;
    int nextStart;
    boolean isItemLeft;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dialog = getDialog(this);

        searchInput = (EditText) findViewById(R.id.search_input);
        searchInput.setFilters(new InputFilter[]{filterSearch});
        adapter = new MovieAdapter(this);

        recyclerView = (RecyclerView) findViewById(R.id.movieList);
        recyclerView.setAdapter(adapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        adapter.setEndlessScrollListener(new MovieAdapter.EndlessScrollListener() {
            @Override
            public boolean onLoadMore(int i) {
                if (isItemLeft) {
                    requestMovieApi(query, nextStart);
                }
                return isItemLeft;
            }
        });

        final InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        final Button searchBtn = (Button) findViewById(R.id.search_btn);
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                query = searchInput.getText().toString();
                if(query.replace(" ", "").equals("")){
                    Toast.makeText(getApplicationContext(),
                            "검색어를 입력해주세요.", Toast.LENGTH_LONG).show();
                }else{
                    requestMovieApi(query, 1);
                    imm.hideSoftInputFromWindow(searchBtn.getWindowToken(),0);
                }
            }
        });
    }


    public InputFilter filterSearch = new InputFilter() {
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            Pattern ps = Pattern.compile("^[가-힣a-zA-Z0-9\\s]*$");
            if(!ps.matcher(source).matches()){
                return "";
            }
            return null;
        }
    };

    public Dialog getDialog(Context context){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(R.layout.progress);
        builder.setCancelable(false);
        Dialog dialog = builder.create();

        return dialog;
    }
    public void loadingDialog(Dialog dialog, boolean set){
        if(set){
            dialog.show();
            dialog.getWindow().setLayout(
                    600,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
        }
        else dialog.dismiss();
    }


    public String queryMake(String query, int start){
        String result = "";
        try {
            String encodedQuery = URLEncoder.encode(query, "UTF-8");
            result = "?query="+encodedQuery+"&start="+Integer.toString(start);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return result;
    }

    public void requestMovieApi(final String query, final int start){
        String url = "https://openapi.naver.com/v1/search/movie.json"+queryMake(query, start);
        loadingDialog(dialog,true);

        StringRequest request = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Gson gson = new Gson();
                MovieResult result = gson.fromJson(response, MovieResult.class);
                if (result.getTotal()==0){
                    Toast.makeText(getApplicationContext(),
                            "검색 결과가 없습니다.", Toast.LENGTH_LONG).show();
                }

                if (start == 1){
                    adapter.newItems(result.getItems());
                }else {
                    adapter.addItems(result.getItems());
                }

                nextStart = result.getStart()+result.getDisplay();
                if (result.getTotal()<nextStart){
                    isItemLeft = false;
                }else isItemLeft = true;

                loadingDialog(dialog,false);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("X-Naver-Client-Id", "mcJxgSgbyMU83N9bCbYx");
                headers.put("X-Naver-Client-Secret", "VIoUEQ63oK");
                return headers;
            }
        };

        MySingleton.getInstance(this).addToRequestQueue(request);
    }

}


