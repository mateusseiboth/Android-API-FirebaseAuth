package com.example.MateusVere;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Handler;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class DownloadDados extends AppCompatActivity {

    public void run(String url, DownloadCallback callback) {
        new FetchData(url, callback).start();
    }

    class FetchData extends Thread {
        private DownloadCallback callback;
        private String url;
        private String dados = "";

        public FetchData(String url, DownloadCallback callback) {
            this.url = url;
            this.callback = callback;
        }

        @Override
        public void run() {
            try {
                URL apiUrl = new URL(url);

                HttpURLConnection httpURLConnection = (HttpURLConnection) apiUrl.openConnection();
                httpURLConnection.setRequestProperty("Authorization", "uimnu1z087OLolo79G1GQcvNgDGoHMeMg12aUUWHKzs5aNXMuDXsZntC");
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

                String linha;

                while ((linha = bufferedReader.readLine()) != null) {
                    dados = dados + linha;
                }

                if (!dados.isEmpty() && callback != null) {
                    callback.onDownloadCompleted(url, dados);
                }

            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
