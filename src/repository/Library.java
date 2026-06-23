package repository;

import model.Book;

import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;

public class Library {

    private List<Book> books =  new ArrayList<>();

    public void addBook(Book book) {
        books.add(book);
    }

    private void removeBook(int id) {
        Iterator<Book> iterator = books.iterator();
        while (iterator.hasNext()) {
            Book book = iterator.next();
            if (book.getId() == id) {
                iterator.remove();
            }
        }
    }

    public void displayBooks() {
        for (Book book : books) {
            System.out.println(book);
        }
    }

    public List<Book> getBooks() {
        return books;
    }
}