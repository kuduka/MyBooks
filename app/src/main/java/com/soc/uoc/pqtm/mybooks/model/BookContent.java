package com.soc.uoc.pqtm.mybooks.model;

import com.orm.SugarApp;
import com.orm.SugarRecord;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class BookContent extends SugarApp {

    public static boolean delete(Integer bookPos){
        //delete book
        BookItem del = BookItem.findById(BookItem.class,bookPos);
        if (del != null){
            del.delete();
            return true;
        }
        return false;
    }

    public static List<BookItem> getBooks() {
        return BookItem.listAll(BookItem.class);
    }

    public static boolean exists(BookItem bookItem) {

        List<BookItem> list = getBooks();

        if (!list.isEmpty()) {
            List<BookItem> lbooks = BookItem.find(BookItem.class, "title = ? ", bookItem.getTitle());
            if (!lbooks.isEmpty()) {
                return true;
            }
            else {
                return false;
            }
        } else {
            return false;
        }
    }

    public BookContent() {

    }

    public static class BookItem extends SugarRecord {

        private Long id;
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
                this.id = Long.parseLong(id);
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