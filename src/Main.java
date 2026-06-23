import custom.BookCollection;
import generic.NumberStorage;
import generic.StorageBox;
import model.Book;
import repository.Library;
import util.ReportGenerator;
import util.Statistics;
import util.UndoManager;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        Library library = new Library();
        Book b1 = new Book(1, "Java", "James Gosling");
        Book b2 = new Book(2, "Spring", "Rod Johnson");
        Book b3 = new Book(3, "Hibernate", "Gavin King");
        library.addBook(b1);
        library.addBook(b2);
        library.addBook(b3);
        System.out.println("===== ALL BOOKS =====\n");
        library.displayBooks();
        System.out.println("\n===== SEARCH BY TITLE =====\n");
        library.searchBookByTitle("Java");
        System.out.println("\n===== SEARCH BY AUTHOR =====\n");
        library.searchBookByAuthor("James");
        System.out.println("\n===== UPDATE BOOK =====");
        library.updateBook(1, "Java 21", "James Gosling");
        library.displayBooks();
        System.out.println("\n===== DELETE BOOK =====");
        library.removeBook(2);
        library.displayBooks();


        StorageBox<Book> box = new StorageBox<>();
        box.setItem(b1);
        System.out.println(box.getItem());
        System.out.println();

        NumberStorage<Integer> number = new NumberStorage<>(10);
        System.out.println(number.square());
        System.out.println();

        UndoManager undo = new UndoManager();
        undo.saveAction("Added Java Book");
        undo.saveAction("Added Spring Book");
        undo.undo();
        System.out.println();

        BookCollection collection = new BookCollection();
        collection.addBook(b1);
        collection.addBook(b2);
        for (Book book : collection) {
            System.out.println(book);
        }
        System.out.println();

        Statistics.printNumbers(List.of(10,20,30));
        System.out.println();
        String report = ReportGenerator.generate(library.getBooks());
        System.out.println(report);
    }
}