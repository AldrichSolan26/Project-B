import javafx.collections.ObservableList;

public class LibraryController {
    private final UserModel model;

    public LibraryController(UserModel model) {
        this.model = model;
    }

    public LibraryModel getLibrary() {
        return this.model.getLibrary();
    }

    public LibraryModel filterPrintBook() {
        return this.getLibrary().filterPrintBook();
    }

    public LibraryModel filterPrintBook(Genre genre) {
        return this.getLibrary().filterPrintBook(genre);
    }

    public LibraryModel filterDigitalBook() {
        return this.getLibrary().filterDigitalBook();
    }

    public LibraryModel filterDigitalBook(Genre genre) {
        return this.getLibrary().filterDigitalBook(genre);
    }

    public void sortByTitle() {
        this.getLibrary().sortByTitle();
    }

    public void sortbyAuthor() {
        this.getLibrary().sortByAuthor();
    }

    public void checkoutBook(Book selectedBook) {
        this.model.checkoutBook(selectedBook);
    }

    public Book getBook(String title) {
        return this.getLibrary().getBook(title);
    }

    public ObservableList<Book> getBorrowedBooks() {
        return this.model.getBorrowedBooks();
    }

    public void setOverMaximum(Boolean b) {
        this.model.setOverMaximum(b);
    }

    public ObservableList<Book> getDownloadedBooks() {
        return this.model.getDownloadedBooks();
    }

    public void returnBook(Book book) {
        this.model.returnBook(book);
    }

    public void addCountBorrowedBook() {
        model.countBorrowedBookProperty().set(model.countBorrowedBookProperty().get() + 1);
    }

    public void subtractCountBorrowedBook() {
        model.countBorrowedBookProperty().set(model.countBorrowedBookProperty().get() - 1);
    }
}