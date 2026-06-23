package custom;

import model.Book;

import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;

public class BookCollection implements Iterable<Book> {

    private List<Book> books =  new ArrayList<>();
    public void addBook(Book book) {
        books.add(book);
    }

    @Override
    public Iterator<Book> iterator() {
        return books.iterator();
    }

}