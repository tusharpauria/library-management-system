package util;

import model.Book;

import java.util.List;

public class ReportGenerator {

    public static String generate(List<Book> books) {

        StringBuilder builder = new StringBuilder();
        builder.append("BOOK REPORT: \n");
        for (Book book : books) {
            builder.append(book).append("\n");
        }
        return builder.toString();
    }
}