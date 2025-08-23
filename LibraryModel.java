
import java.util.List;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

interface BookFilter {
    // Returns all digital books.
    LibraryModel filterDigitalBook();

    // Returns all digital books by author.
    LibraryModel filterDigitalBook(String author);

    // Returns all digital books by genre.
    LibraryModel filterDigitalBook(Genre genre);

    // Returns all print books.
    LibraryModel filterPrintBook();

    // Returns all print books by author.
    LibraryModel filterPrintBook(String author);

    // Returns all print books by genre.
    LibraryModel filterPrintBook(Genre genre);

    // Returns all books by author.
    LibraryModel filterBooks(String author);

    // Returns all books in genre.
    LibraryModel filterBooks(Genre genre);
}

interface BookSort {
    void sortByAuthor();

    void sortByTitle();
}

class UserModel {
    private ObservableList<Book> borrowedBooks;
    private ObservableList<Book> downloadedBooks;
    private LibraryModel library;
    private ObservableList<Book> userBooks;
    private SimpleIntegerProperty countBorrowedBook;
    // Automatically updates UI counters
    private SimpleBooleanProperty overMaximum;
    // Controls borrowing limit enforcement

    public UserModel(LibraryModel library) {
        // MODEL: Initialize data structures with observable collections
        this.borrowedBooks = FXCollections.observableArrayList();
        this.downloadedBooks = FXCollections.observableArrayList();
        this.library = library;
        this.userBooks = FXCollections.observableArrayList();

        // MODEL: Create observable properties for real-time UI updates
        this.countBorrowedBook = new SimpleIntegerProperty(0);
        this.overMaximum = new SimpleBooleanProperty();
    }

    public SimpleIntegerProperty countBorrowedBookProperty() {
        return this.countBorrowedBook;
    }

    public int getCountBorrowedBook() {
        return countBorrowedBookProperty().get();
    }

    public void setCountBorrowedBook(int i) {
        countBorrowedBookProperty().set(i);
    }

    public SimpleBooleanProperty overMaximumProperty() {
        return this.overMaximum;
    }

    public boolean getOverMaximum() {
        return overMaximumProperty().get();
    }

    public void setOverMaximum(boolean b) {
        overMaximumProperty().set(b);
    }

    public String toString() {
        return "" + borrowedBooks + "\n" + downloadedBooks;
    }

    // CORE LIBRARY LOGIC - Book Return Process:
    public void returnBook(Book book) {
        // MODEL: Checks if book exists in user's borrowed collection
        if (borrowedBooks.contains(book)) {
            // MODEL: Removes book from user's borrowed list
            borrowedBooks.remove(book);
            // MODEL: Updates book's availability status
            ((PrintBook) book).setAvailable(true);
            // MODEL: Updates book's status for display
            book.setStatus(Status.AVAILABLE);
            // Observable collection automatically notifies bound UI components
        } else {
            System.out.println(book.getFormattedTitle() + " is not borrowed by this user.");
        }
    }

    // MODEL: Core business logic for book checkout
    public void checkoutBook(Book book) {
        // MODEL: Validates book exists in Library
        if (book != null && library.hasBook(book)) {
            // MODEL: determine book type and processes accordingly
            if (book instanceof DigitalBook && ((DigitalBook) book).canDownload()) {
                // MODEL: Adds digital book to downloaded collection
                this.downloadedBooks.add(book);
                // Observable collection triggers automatic view updates
            } else if (book instanceof PrintBook && ((PrintBook) book).getAvailable()) {
                // MODEL: adds printbook to borrowed collection
                this.borrowedBooks.add(book);
                // MODEL: updates book status
                ((PrintBook) book).setAvailable(false);
                // Observable collection triggers automatic View Updates
                book.setStatus(Status.UNAVAILABLE);
            }
        } else {
            System.out.println("The book does not exist!");
        }
    }

    // MODEL: Data access methods for Controlelr/View
    public ObservableList<Book> getDownloadedBooks() {
        // MODEL: Returns obeservable list for View Binding
        return this.downloadedBooks;
    }

    public ObservableList<Book> getUserBooks() {
        // MODEL: Combined borrowed books and downloaded books for comprehensive view
        List<Book> merged = new ArrayList<>();
        merged.addAll(borrowedBooks);
        merged.addAll(downloadedBooks);
        userBooks.addAll(merged);
        return userBooks;
    }

    // MODEL: Specific book retrieval with validation
    public DigitalBook getDownloadedBook(int index) {
        if (index < this.downloadedBooks.size()) {
            return (DigitalBook) this.downloadedBooks.get(index);
        } else {
            System.out.println("Not in index!");
            return null;
        }
    }

    // MODEL: Data access methods for Controlelr/View
    public ObservableList<Book> getBorrowedBooks() {
        // MODEL: Returns obeservable list for View Binding
        return this.borrowedBooks;
    }
    // MODEL: Specific book retrieval with validation
    public PrintBook getBorrowedBook(int index) {
        if (index < this.borrowedBooks.size()) {
            return (PrintBook) this.borrowedBooks.get(index);
        } else {
            System.out.println("Not in index!");
            return null;
        }
    }

    public void listDownloadedBooks() {
        String result = "";
        int count = 1;

        for (Book book : this.downloadedBooks) {
            result = result + count + ". " + book.getFormattedTitle() + "\n";
            count++;
        }

        System.out.println(result);
    }

    public void listBorrowedBooks() {
        String result = "";
        int count = 1;

        for (Book book : this.borrowedBooks) {
            result = result + count + ". " + book.getFormattedTitle() + "\n";
            count++;
        }

        System.out.println(result);
    }

    // MODEL: Provides access to main library data
    public LibraryModel getLibrary() {
        return this.library;
    }
}

// MODEL: Main library data management and filtering logic
public class LibraryModel implements BookFilter, BookSort {
    // MODEL: Core book collection storage
    private ObservableList<Book> books;

    public LibraryModel() {
        this.books = FXCollections.observableArrayList();
    }

    public LibraryModel(ObservableList<Book> books) {
        this.books = books;
    }

    // MODEL: Book retrieval by index with validation
    public Book getBook(int index) {
        if (index < books.size()) {
            // MODEL: Returns book if valid index
            return this.books.get(index); 
        } else {
            System.out.println("Not in index!");
            return null;
        }
    }

    // SEARCH FUNCTIONALITY
    public Book getBook(String title) {
        // MODEL: Retrieves search query from Controller
        // MODEL: iterates through book collection
        for (Book book : books) {
            // MODEL: compares title (case-sensitive)
            if (book.getTitle().toLowerCase().equals(title)) {
                // MODEL: returns matching book to controller
                return book;
            }
        }
        // MODEL: returns null if no match found
        return null;
    }

    public boolean hasBook(Book book) {
        // Validates book existence
        return this.books.contains(book);
    }

    public void addBook(Book book) {
        //  MODEL: Adds book to collection, trigger UI updates
        this.books.add(book);
    }

    public ObservableList<Book> libraryProperty() {
        // MODEL: Returns observable collection for view Binding
        return this.books;
    }

    public String toString() {
        String result = "";
        int count = 1;

        for (Book book : this.books) {
            result = result + count + ". " + book.getFormattedTitle() + "\n";
            count++;
        }

        return result;
    }

    // All methods below are for sorting / filtering.
    @Override
    public LibraryModel filterDigitalBook() {
        LibraryModel digitalBooks = new LibraryModel();

        for (Book book : books) {
            if (book instanceof DigitalBook) {
                digitalBooks.addBook(book);
            }
        }

        digitalBooks.sortByTitle();
        return digitalBooks;

    }

    @Override
    public LibraryModel filterDigitalBook(String author) {
        LibraryModel digitalBooks = new LibraryModel();

        for (Book book : this.books) {
            if (book instanceof DigitalBook && book.getAuthor().equals(author)) {
                digitalBooks.addBook(book);
            }
        }

        digitalBooks.sortByAuthor();
        return digitalBooks;
    }

    @Override
    public LibraryModel filterDigitalBook(Genre genre) {
        LibraryModel digitalBooks = new LibraryModel();

        for (Book book : this.books) {
            if (book instanceof DigitalBook && book.getGenre() == genre) {
                digitalBooks.addBook(book);
            }
        }

        digitalBooks.sortByTitle();
        return digitalBooks;
    }

    @Override
    public LibraryModel filterPrintBook() {
        LibraryModel printBooks = new LibraryModel();

        for (Book book : this.books) {
            if (book instanceof PrintBook) {
                printBooks.addBook(book);
            }
        }

        printBooks.sortByTitle();
        return printBooks;
    }

    @Override
    public LibraryModel filterPrintBook(String author) {
        LibraryModel printBooks = new LibraryModel();

        for (Book book : this.books) {
            if (book instanceof PrintBook && book.getAuthor().equals(author)) {
                printBooks.addBook(book);
            }
        }

        printBooks.sortByTitle();
        return printBooks;
    }

    @Override
    public LibraryModel filterPrintBook(Genre genre) {
        LibraryModel printBooks = new LibraryModel();

        for (Book book : this.books) {
            if (book instanceof PrintBook && book.getGenre() == genre) {
                printBooks.addBook(book);
            }
        }

        return printBooks;
    }

    @Override
    public LibraryModel filterBooks(String author) {
        LibraryModel books = new LibraryModel();

        for (Book book : this.books) {
            if (book.getAuthor().equals(author)) {
                books.addBook(book);
            }
        }

        books.sortByTitle();
        return books;
    }

    @Override
    public LibraryModel filterBooks(Genre genre) {
        LibraryModel books = new LibraryModel();

        for (Book book : this.books) {
            if (book.getGenre() == genre) {
                books.addBook(book);
            }
        }

        return books;
    }

    @Override
    public void sortByTitle() {
        Collections.sort(books, Book.byTitle);
    }

    @Override
    public void sortByAuthor() {
        Collections.sort(books, Book.byAuthor);
    }
}

enum Genre {
    FICTION,
    NON_FICTION,
    MYSTERY,
    FANTASY,
    SCIENCE_FICTION,
    ROMANCE,
    POETRY;
}

enum Status {
    AVAILABLE {
        @Override
        public String toString() {
            return "Available";
        }
    },
    UNAVAILABLE {
        @Override
        public String toString() {
            return "Unavailable";
        }
    },
    DOWNLOADABLE {
        @Override
        public String toString() {
            return "Downloadable";
        }
    },
    UNDOWNLOADABLE {
        @Override
        public String toString() {
            return "Undownloadable";
        }
    }
}

abstract class Book {
    protected SimpleStringProperty title;
    protected SimpleStringProperty author;
    protected SimpleObjectProperty<Genre> genre;
    protected SimpleIntegerProperty pageCount;
    protected SimpleObjectProperty<Status> status;

    public Book(SimpleStringProperty title, SimpleStringProperty author, SimpleObjectProperty<Genre> genre,
            SimpleIntegerProperty pageCount, SimpleObjectProperty<Status> status) {
        this.title = title;
        this.author = author;
        this.genre = genre;
        this.pageCount = pageCount;
        this.status = status;
    }

    static final Comparator<Book> byTitle = Comparator.comparing(Book::getTitle);
    static final Comparator<Book> byAuthor = Comparator.comparing(Book::getAuthor);
    static final Comparator<Book> byGenre = Comparator.comparing(Book::getGenre);

    public String toString() {
        return "Title: " + this.title + " By Author: " + this.author + " (Genre: " + this.genre + " )";
    }

    public String getFormattedTitle() {
        return this.title + " by " + this.author;
    }

    public SimpleStringProperty titleProperty() {
        return title;
    }

    public String getTitle() {
        return titleProperty().get().toLowerCase();
    }

    public SimpleStringProperty authorProperty() {
        return author;
    }

    public String getAuthor() {
        return authorProperty().get();
    }

    public SimpleObjectProperty<Genre> genreProperty() {
        return genre;
    }

    public Genre getGenre() {
        return genreProperty().get();
    }

    public SimpleIntegerProperty pageCountProperty() {
        return pageCount;
    }

    public SimpleObjectProperty<Status> statusProperty() {
        return status;
    }

    public Status getStatus() {
        return this.statusProperty().get();
    }

    public void setStatus(final Status status) {
        this.statusProperty().set(status);
    }

    public int getPageCount() {
        return pageCountProperty().get();
    }

    public void displayInfo() {
        System.out.println(
                "Title: " + this.title +
                        "\nAuthor: " + this.author +
                        "\nGenre: " + this.genre +
                        "\nPage Count: " + this.pageCount);
    }
}

enum Format {
    PDF,
    EPUB,
    AZW,
    DJVU,
    TXT,
    HTML,
    DOCX;
}

class DigitalBook extends Book {
    private Format format;
    private boolean canDownload;

    public DigitalBook(SimpleStringProperty title, SimpleStringProperty author, SimpleObjectProperty<Genre> genre,
            SimpleIntegerProperty pageCount, Format format, SimpleObjectProperty<Status> status) {
        super(title, author, genre, pageCount, status);
        this.format = format;
        this.canDownload = this.getStatus() == Status.DOWNLOADABLE;
    }

    public Format getFormat() {
        return this.format;
    }

    public boolean canDownload() {
        return this.canDownload;
    }

    @Override
    public void displayInfo() {
        super.displayInfo();
        System.out.println(
                "Format: " + this.format +
                        "\nCan download: " + this.canDownload);
    }
}

enum CoverType {
    HARDCOVER,
    PAPERBACK,
    SPIRAL_BOUND,
    LEATHER_BOUND,
    MAGAZINE,
    GRAPHIC_NOVEL;
}

class PrintBook extends Book {
    private CoverType coverType;
    private boolean available;

    PrintBook(SimpleStringProperty title, SimpleStringProperty author, SimpleObjectProperty<Genre> genre,
            SimpleIntegerProperty pageCount, CoverType coverType, SimpleObjectProperty<Status> status) {
        super(title, author, genre, pageCount, status);
        this.coverType = coverType;
        this.available = this.getStatus() == Status.AVAILABLE;
    }

    public CoverType getCoverType() {
        return this.coverType;
    }

    public boolean getAvailable() {
        return this.available;
    }

    public void setAvailable(Boolean available) {
        this.available = available;
    }

    @Override
    public void displayInfo() {
        super.displayInfo();
        System.out.println(
                "Cover Type: " + this.coverType.name());
    };

}

class Booklist {
    static Book[] list = {
            // Fantasy
            new DigitalBook(new SimpleStringProperty("The Dragon's Heir"),
                    new SimpleStringProperty("Christopher Paolini"),
                    new SimpleObjectProperty<>(Genre.FANTASY),
                    new SimpleIntegerProperty(420),
                    Format.AZW,
                    new SimpleObjectProperty<>(Status.UNDOWNLOADABLE)),

            new PrintBook(new SimpleStringProperty("Wizards of the North"),
                    new SimpleStringProperty("J.K. Rowling"),
                    new SimpleObjectProperty<>(Genre.FANTASY),
                    new SimpleIntegerProperty(380),
                    CoverType.HARDCOVER,
                    new SimpleObjectProperty<>(Status.AVAILABLE)),

            new DigitalBook(new SimpleStringProperty("Blade of the Dawn"),
                    new SimpleStringProperty("Brandon Sanderson"),
                    new SimpleObjectProperty<>(Genre.FANTASY),
                    new SimpleIntegerProperty(560),
                    Format.PDF,
                    new SimpleObjectProperty<>(Status.DOWNLOADABLE)),

            new PrintBook(new SimpleStringProperty("Forest of Shadows"),
                    new SimpleStringProperty("Patrick Rothfuss"),
                    new SimpleObjectProperty<>(Genre.FANTASY),
                    new SimpleIntegerProperty(490),
                    CoverType.PAPERBACK,
                    new SimpleObjectProperty<>(Status.UNAVAILABLE)),

            new DigitalBook(new SimpleStringProperty("The Enchanted Grove"),
                    new SimpleStringProperty("Leigh Bardugo"),
                    new SimpleObjectProperty<>(Genre.FANTASY),
                    new SimpleIntegerProperty(350),
                    Format.EPUB,
                    new SimpleObjectProperty<>(Status.DOWNLOADABLE)),

            // Romance
            new DigitalBook(new SimpleStringProperty("Letters to Verona"),
                    new SimpleStringProperty("Nicholas Sparks"),
                    new SimpleObjectProperty<>(Genre.ROMANCE),
                    new SimpleIntegerProperty(310),
                    Format.TXT,
                    new SimpleObjectProperty<>(Status.UNDOWNLOADABLE)),

            new PrintBook(new SimpleStringProperty("Summer of Us"),
                    new SimpleStringProperty("Colleen Hoover"),
                    new SimpleObjectProperty<>(Genre.ROMANCE),
                    new SimpleIntegerProperty(290),
                    CoverType.PAPERBACK,
                    new SimpleObjectProperty<>(Status.AVAILABLE)),

            new DigitalBook(new SimpleStringProperty("Chasing the Sunset"),
                    new SimpleStringProperty("Jojo Moyes"),
                    new SimpleObjectProperty<>(Genre.ROMANCE),
                    new SimpleIntegerProperty(280),
                    Format.DOCX,
                    new SimpleObjectProperty<>(Status.DOWNLOADABLE)),

            new PrintBook(new SimpleStringProperty("The Heart's Journey"),
                    new SimpleStringProperty("Jane Austen"),
                    new SimpleObjectProperty<>(Genre.ROMANCE),
                    new SimpleIntegerProperty(400),
                    CoverType.HARDCOVER,
                    new SimpleObjectProperty<>(Status.UNAVAILABLE)),

            new DigitalBook(new SimpleStringProperty("Paris in the Rain"),
                    new SimpleStringProperty("Debbie Macomber"),
                    new SimpleObjectProperty<>(Genre.ROMANCE),
                    new SimpleIntegerProperty(250),
                    Format.PDF,
                    new SimpleObjectProperty<>(Status.UNDOWNLOADABLE)),

            // Nonfiction
            new DigitalBook(new SimpleStringProperty("The Art of Clarity"),
                    new SimpleStringProperty("Cal Newport"),
                    new SimpleObjectProperty<>(Genre.NON_FICTION),
                    new SimpleIntegerProperty(200),
                    Format.AZW,
                    new SimpleObjectProperty<>(Status.DOWNLOADABLE)),

            new PrintBook(new SimpleStringProperty("History of Civilizations"),
                    new SimpleStringProperty("Yuval Noah Harari"),
                    new SimpleObjectProperty<>(Genre.NON_FICTION),
                    new SimpleIntegerProperty(480),
                    CoverType.HARDCOVER,
                    new SimpleObjectProperty<>(Status.AVAILABLE)),

            new DigitalBook(new SimpleStringProperty("Building Better Habits"),
                    new SimpleStringProperty("James Clear"),
                    new SimpleObjectProperty<>(Genre.NON_FICTION),
                    new SimpleIntegerProperty(310),
                    Format.PDF,
                    new SimpleObjectProperty<>(Status.UNDOWNLOADABLE)),

            new PrintBook(new SimpleStringProperty("The Nature Explorer"),
                    new SimpleStringProperty("David Attenborough"),
                    new SimpleObjectProperty<>(Genre.NON_FICTION),
                    new SimpleIntegerProperty(320),
                    CoverType.GRAPHIC_NOVEL,
                    new SimpleObjectProperty<>(Status.UNAVAILABLE)),

            new DigitalBook(new SimpleStringProperty("Economics for Everyone"),
                    new SimpleStringProperty("Thomas Sowell"),
                    new SimpleObjectProperty<>(Genre.NON_FICTION),
                    new SimpleIntegerProperty(270),
                    Format.HTML,
                    new SimpleObjectProperty<>(Status.DOWNLOADABLE)),

            // Poetry
            new PrintBook(new SimpleStringProperty("Whispers of the Sea"),
                    new SimpleStringProperty("Rupi Kaur"),
                    new SimpleObjectProperty<>(Genre.POETRY),
                    new SimpleIntegerProperty(150),
                    CoverType.PAPERBACK,
                    new SimpleObjectProperty<>(Status.AVAILABLE)),

            new DigitalBook(new SimpleStringProperty("Moonlight Verses"),
                    new SimpleStringProperty("Pablo Neruda"),
                    new SimpleObjectProperty<>(Genre.POETRY),
                    new SimpleIntegerProperty(180),
                    Format.EPUB,
                    new SimpleObjectProperty<>(Status.UNDOWNLOADABLE)),

            new PrintBook(new SimpleStringProperty("Songs for the Stars"),
                    new SimpleStringProperty("Maya Angelou"),
                    new SimpleObjectProperty<>(Genre.POETRY),
                    new SimpleIntegerProperty(200),
                    CoverType.HARDCOVER,
                    new SimpleObjectProperty<>(Status.UNAVAILABLE)),

            new DigitalBook(new SimpleStringProperty("Petals of Time"),
                    new SimpleStringProperty("Robert Frost"),
                    new SimpleObjectProperty<>(Genre.POETRY),
                    new SimpleIntegerProperty(160),
                    Format.TXT,
                    new SimpleObjectProperty<>(Status.DOWNLOADABLE)),

            new PrintBook(new SimpleStringProperty("The Quiet Garden"),
                    new SimpleStringProperty("Emily Dickinson"),
                    new SimpleObjectProperty<>(Genre.POETRY),
                    new SimpleIntegerProperty(140),
                    CoverType.GRAPHIC_NOVEL,
                    new SimpleObjectProperty<>(Status.AVAILABLE)),

            // Mystery
            new DigitalBook(new SimpleStringProperty("The Vanishing Key"),
                    new SimpleStringProperty("Agatha Christie"),
                    new SimpleObjectProperty<>(Genre.MYSTERY),
                    new SimpleIntegerProperty(340),
                    Format.AZW,
                    new SimpleObjectProperty<>(Status.DOWNLOADABLE)),

            new PrintBook(new SimpleStringProperty("Shadows in the Fog"),
                    new SimpleStringProperty("Arthur Conan Doyle"),
                    new SimpleObjectProperty<>(Genre.MYSTERY),
                    new SimpleIntegerProperty(320),
                    CoverType.HARDCOVER,
                    new SimpleObjectProperty<>(Status.UNAVAILABLE)),

            new DigitalBook(new SimpleStringProperty("Murder at Hollow Manor"),
                    new SimpleStringProperty("Tana French"),
                    new SimpleObjectProperty<>(Genre.MYSTERY),
                    new SimpleIntegerProperty(380),
                    Format.PDF,
                    new SimpleObjectProperty<>(Status.UNDOWNLOADABLE)),

            new PrintBook(new SimpleStringProperty("The Last Cipher"),
                    new SimpleStringProperty("Dan Brown"),
                    new SimpleObjectProperty<>(Genre.MYSTERY),
                    new SimpleIntegerProperty(450),
                    CoverType.PAPERBACK,
                    new SimpleObjectProperty<>(Status.AVAILABLE)),

            new DigitalBook(new SimpleStringProperty("Midnight Intrigue"),
                    new SimpleStringProperty("Ruth Ware"),
                    new SimpleObjectProperty<>(Genre.MYSTERY),
                    new SimpleIntegerProperty(360),
                    Format.DOCX,
                    new SimpleObjectProperty<>(Status.DOWNLOADABLE)),

            // Science Fiction
            new DigitalBook(new SimpleStringProperty("Neon Skies"),
                    new SimpleStringProperty("William Gibson"),
                    new SimpleObjectProperty<>(Genre.SCIENCE_FICTION),
                    new SimpleIntegerProperty(420),
                    Format.AZW,
                    new SimpleObjectProperty<>(Status.UNDOWNLOADABLE)),

            new PrintBook(new SimpleStringProperty("The Quantum Horizon"),
                    new SimpleStringProperty("Isaac Asimov"),
                    new SimpleObjectProperty<>(Genre.SCIENCE_FICTION),
                    new SimpleIntegerProperty(500),
                    CoverType.HARDCOVER,
                    new SimpleObjectProperty<>(Status.UNAVAILABLE)),

            new DigitalBook(new SimpleStringProperty("Android's Dream"),
                    new SimpleStringProperty("Philip K. Dick"),
                    new SimpleObjectProperty<>(Genre.SCIENCE_FICTION),
                    new SimpleIntegerProperty(390),
                    Format.EPUB,
                    new SimpleObjectProperty<>(Status.DOWNLOADABLE)),

            new PrintBook(new SimpleStringProperty("Cosmic Voyage"),
                    new SimpleStringProperty("Carl Sagan"),
                    new SimpleObjectProperty<>(Genre.SCIENCE_FICTION),
                    new SimpleIntegerProperty(410),
                    CoverType.GRAPHIC_NOVEL,
                    new SimpleObjectProperty<>(Status.AVAILABLE)),

            new DigitalBook(new SimpleStringProperty("Starlight Protocol"),
                    new SimpleStringProperty("Liu Cixin"),
                    new SimpleObjectProperty<>(Genre.SCIENCE_FICTION),
                    new SimpleIntegerProperty(450),
                    Format.PDF,
                    new SimpleObjectProperty<>(Status.UNDOWNLOADABLE)),

            // Fiction
            new PrintBook(new SimpleStringProperty("The Silent Village"),
                    new SimpleStringProperty("Harper Lee"),
                    new SimpleObjectProperty<>(Genre.FICTION),
                    new SimpleIntegerProperty(320),
                    CoverType.PAPERBACK,
                    new SimpleObjectProperty<>(Status.AVAILABLE)),

            new DigitalBook(new SimpleStringProperty("Ocean Between Us"),
                    new SimpleStringProperty("Kazuo Ishiguro"),
                    new SimpleObjectProperty<>(Genre.FICTION),
                    new SimpleIntegerProperty(280),
                    Format.TXT,
                    new SimpleObjectProperty<>(Status.DOWNLOADABLE)),

            new PrintBook(new SimpleStringProperty("Beneath the Willow"),
                    new SimpleStringProperty("Margaret Atwood"),
                    new SimpleObjectProperty<>(Genre.FICTION),
                    new SimpleIntegerProperty(350),
                    CoverType.HARDCOVER,
                    new SimpleObjectProperty<>(Status.UNAVAILABLE)),

            new DigitalBook(new SimpleStringProperty("Paths of Glass"),
                    new SimpleStringProperty("Jhumpa Lahiri"),
                    new SimpleObjectProperty<>(Genre.FICTION),
                    new SimpleIntegerProperty(300),
                    Format.DOCX,
                    new SimpleObjectProperty<>(Status.UNDOWNLOADABLE)),

            new PrintBook(new SimpleStringProperty("Voices in the Wind"),
                    new SimpleStringProperty("Chimamanda Ngozi Adichie"),
                    new SimpleObjectProperty<>(Genre.FICTION),
                    new SimpleIntegerProperty(310),
                    CoverType.GRAPHIC_NOVEL,
                    new SimpleObjectProperty<>(Status.AVAILABLE))

    };
}