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
        library.addBook(b1);
        library.addBook(b2);
        library.displayBooks();
        System.out.println();
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