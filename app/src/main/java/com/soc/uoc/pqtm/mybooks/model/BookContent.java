package com.soc.uoc.pqtm.mybooks.model;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class BookContent implements Serializable {

    public BookContent() {

    }

    public static class BookItem implements Serializable{
        /* Classe d'un llibre */
        private Integer id;
        private String title;
        private String author;
        private Date publication_date;
        private String description;
        private String url_image;

        public BookItem() {
        }

        public BookItem(String id, String title, String author, String publication_date, String description, String url_image) {
            try {
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                this.id = Integer.parseInt(id);
                this.title = title;
                this.author = author;
                this.publication_date = dateFormat.parse(publication_date);
                this.description = description;
                this.url_image = url_image;
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }


        @Override
        public String toString() {
            return this.getTitle();
        }

        public void setId(int id) {
            this.id = id;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public void setAuthor(String author) {
            this.author = author;
        }

        public void setPublication_date(String publication_date) {
            try {
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                this.publication_date = dateFormat.parse(publication_date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public void setUrl_image(String url_image) {
            this.url_image = url_image;
        }

        public Integer getId() {
            return this.id;
        }

        public String getTitle() {
            return this.title;
        }

        public String getAuthor() {
            return this.author;
        }

        public Date getPublication_date() {
            return this.publication_date;
        }

        public String getDescription() {
            return this.description;
        }

        public String getUrl_image() {
            return this.url_image;
        }
    }
}