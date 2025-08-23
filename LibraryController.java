import javafx.collections.ObservableList;

public class LibraryController {
    // CONTROLLER: Hols reference to Model
    private final UserModel model;

    public LibraryController(UserModel model) {
        this.model = model;
    }

    // CONTROLLER: Delegates filtering requests to Model
    public LibraryModel getLibrary() {
        // CONTROLLER -> MODEL
        return this.model.getLibrary();
    }

    public LibraryModel filterPrintBook() {
        // CONTROLLER -> MODEL
        return this.getLibrary().filterPrintBook();
    }

    public LibraryModel filterPrintBook(Genre genre) {
        // CONTROLLER -> MODEL
        return this.getLibrary().filterPrintBook(genre);
    }

    public LibraryModel filterDigitalBook() {
        // CONTROLLER -> MODEL
        return this.getLibrary().filterDigitalBook();
    }

    public LibraryModel filterDigitalBook(Genre genre) {
        // CONTROLLER -> MODEL
        return this.getLibrary().filterDigitalBook(genre);
    }

    public void sortByTitle() {
        // CONTROLLER -> MODEL
        this.getLibrary().sortByTitle();
    }

    public void sortbyAuthor() {
        // CONTROLLER -> MODEL
        this.getLibrary().sortByAuthor();
    }
     // CONTROLLER: Core checking out operation - delegates to Model
    public void checkoutBook(Book selectedBook) {
        // CONTROLLER to MODEL
        this.model.checkoutBook(selectedBook);
    }

    public Book getBook(String title) {
        // CONTROLLER -> MODEL
        return this.getLibrary().getBook(title);
    }

    public ObservableList<Book> getBorrowedBooks() {
         // CONTROLLER -> MODEL
        return this.model.getBorrowedBooks();
    }

    public void setOverMaximum(Boolean b) {
         // CONTROLLER -> MODEL
        this.model.setOverMaximum(b);
    }

    public ObservableList<Book> getDownloadedBooks() {
         // CONTROLLER -> MODEL
        return this.model.getDownloadedBooks();
    }

    public void returnBook(Book book) {
         // CONTROLLER -> MODEL
        this.model.returnBook(book);
    }

    public void addCountBorrowedBook() {
         // CONTROLLER: Library logic for counting borrowed books
        model.countBorrowedBookProperty().set(model.countBorrowedBookProperty().get() + 1);
    }

    public void subtractCountBorrowedBook() {
        model.countBorrowedBookProperty().set(model.countBorrowedBookProperty().get() - 1);
    }
}