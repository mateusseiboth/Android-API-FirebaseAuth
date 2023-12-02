package com.example.MateusVere;

public class Foto {
    String autor, data, urlImg;

    public Foto() {
    }

    public Foto(String autor, String data, String urlImg) {
        this.autor = autor;
        this.data = data;
        this.urlImg = urlImg;
    }

    public String getAutor() {
        return autor;
    }

    public void setAutor(String autor) {
        this.autor = autor;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getUrlImg() {
        return urlImg;
    }

    public void setUrlImg(String urlImg) {
        this.urlImg = urlImg;
    }
}
