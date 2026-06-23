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

    public void displayBooks() {

        if (books.isEmpty()) {
            System.out.println("No books Available");
            return;
        }

        for (Book book : books) {
            System.out.println(book);
        }
        }

        public void searchBookByTitle(String title) {

        boolean found = false;
        for (Book book : books) {
            if (book.getTitle().toLowerCase().contains(title.toLowerCase())) {
                System.out.println(book);
                found = true;
            }
        }
        if (!found) {
            System.out.println("Book Not Found");
        }
    }

    public void searchBookByAuthor(String author) {
        boolean found = false;
        for (Book book : books) {
            if (book.getAuthor().toLowerCase().contains(author.toLowerCase())) {
                System.out.println(book);
                found = true;
            }
        }
        if (!found) {
            System.out.println("Author Not Found");
        }
    }

    public void updateBook(int id, String newTitle, String newAuthor) {
        for (Book book : books) {
            if (book.getId() == id) {
                book.setTitle(newTitle);
                book.setAuthor(newAuthor);
                System.out.println("\nBook Updated Successfully\n");
                return;
            }
        }
        System.out.println("Book Not Found");
    }

    public void removeBook(int id) {

        Iterator<Book> iterator = books.iterator();
        while (iterator.hasNext()) {
            Book book = iterator.next();
            if (book.getId() == id) {
                iterator.remove();
                System.out.println("\nBook Deleted Successfully\n");
                return;
            }
        }
        System.out.println("Book Not Found");
    }

    public List<Book> getBooks() {
        return books;
    }
}