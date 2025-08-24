import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class AppView {
    // VIEW: UI components
    private TableView<Book> borrowedBookView;
    private TableView<Book> downloadedBookView;
    private TableView<Book> tableView;
    private TableView<Book> helpUserView;
    private VBox view;
    private Stage primaryStage;
    private Scene scene;
    private Button userNeedHelp;
    // VIEW: Holds references to Controller and Model for the MVC pattern.
    private AppController controller;
    private AppModel model;

    // VIEW: Constructor
    public AppView(AppController controller, AppModel model, Stage primaryStage) {
        this.controller = controller;
        this.model = model;
        this.primaryStage = primaryStage;

        createAndConfigurePane();
        createAndLayoutControls();
        updateControllerFromListeners();
        observeModelAndUpdateControls();
    }

    public Button getHelp() {
        return this.userNeedHelp;
    }

    public Scene getScene() {
        return scene;
    }

    // VIEW: UI layout and setup
    public void createAndConfigurePane() {
        view = new VBox(10);
        view.setAlignment(Pos.CENTER);

        scene = new Scene(view, 500, 500);
    };

    // VIEW: Main menu and event handling
    public void createAndLayoutControls() {
        Label headingLabel = new Label("Library App");
        headingLabel.setFont(new Font("Arial", 32));
        Button option1 = new Button("    View all books    ");
        Button option2 = new Button("  Search for a book  ");
        Button option3 = new Button("   View your books  ");
        Label titleLabel = new Label("Select an option:");
        titleLabel.setFont(new Font("Arial", 20));

        VBox optionBox = new VBox(10, option1, option2, option3);
        optionBox.setAlignment(Pos.CENTER);

        // VIEW: Event handling and user interactions
        option1.setOnAction(e -> primaryStage.setScene(createSceneOpt1()));
        option2.setOnAction(e -> primaryStage.setScene(createSceneOpt2()));
        option3.setOnAction(e -> primaryStage.setScene(createSceneOpt3()));

        view.getChildren().addAll(headingLabel, titleLabel, optionBox);
    }

    // Scence for the "View all books" button
    // Displays all books including book information and status.
    // Buttons for the user to checkout a book, filter books,
    // or go back to menu.
    public Scene createSceneOpt1() {
        Label heading = new Label("List of books: ");
        heading.setFont(new Font("Arial", 20));
        tableView = new TableView<>();

        // VIEW: Table setup
        TableColumn<Book, String> col1 = new TableColumn<>("Title");
        col1.setCellValueFactory(cell -> cell.getValue().titleProperty());
        col1.setMinWidth(220);
        TableColumn<Book, String> col2 = new TableColumn<>("Author");
        col2.setCellValueFactory(cell -> cell.getValue().authorProperty());
        TableColumn<Book, Genre> col3 = new TableColumn<>("Genre");
        col3.setCellValueFactory(cell -> cell.getValue().genreProperty());
        TableColumn<Book, Integer> col4 = new TableColumn<>("Page Count");
        col4.setCellValueFactory(cell -> cell.getValue().pageCountProperty().asObject());
        TableColumn<Book, Status> col5 = new TableColumn<>("Status");
        col5.setCellValueFactory(cell -> cell.getValue().statusProperty());

        tableView.getColumns().addAll(col1, col2, col3, col4, col5);
        tableView.setItems(controller.getLibrary().libraryProperty());

        Button backButton = new Button("Back to Menu");
        backButton.setOnAction(e -> {
            primaryStage.setScene(scene);
        });

        Button checkOutButton = new Button("Checkout");
        checkOutButton.setOnAction(e -> {
            Book selectedBook = tableView.getSelectionModel().getSelectedItem();
            if (selectedBook != null) {
                this.checkOutForm(selectedBook);
            }
        });
        Button filterButton = new Button("Filter");
        filterButton.setOnAction(e -> filterForm());

        Label countBookField = new Label();
        // Data binding: Label updates automatically when model changes
        countBookField.textProperty().bind(model.countBorrowedBookProperty().asString("Borrowed Books: %d"));
        HBox buttonRow = new HBox(10, backButton, checkOutButton, filterButton);
        checkOutButton.setAlignment(Pos.BOTTOM_RIGHT);

        VBox viewOpt1 = new VBox(10, heading, tableView, buttonRow, countBookField);
        viewOpt1.setAlignment(Pos.CENTER);
        Scene scene1 = new Scene(viewOpt1, 800, 500);

        // This listeners fires when the user's book count is updated.
        // It read whether the new value reaches a certain threshold
        // and calls the controller to update the overMaximumProperty.
        // VIEW: Observes changes to the user's borrowed book count and reacts to
        // updates.
        // CONTROLLER: Updates the overMaximumProperty if the threshold is reached.
        // MODEL: Stores and maanges the user's borrowed book count.
        model.countBorrowedBookProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal.intValue() >= 3) {
                controller.setOverMaximum(true);
            } else {
                controller.setOverMaximum(false);
            }
        });
        return scene1;
    }

    // Opens a modal form that allows the user to filter the library
    // between print and digital books.
    // VIEW: Displays the filter options.
    // CONTROLLER: Handles user input and delegates it to the model.
    // MODEL: Provides the filtered data
    // VIEW: Updates the table view with the filtered dataset.
    public void filterForm() {
        Stage filter = new Stage();
        filter.initOwner(primaryStage);
        filter.initModality(Modality.APPLICATION_MODAL);
        Label heading = new Label("Filter library by: ");
        ToggleGroup toggleGroup = new ToggleGroup();
        RadioButton filter1 = new RadioButton("Print Books");
        RadioButton filter2 = new RadioButton("Digital Books");

        filter1.setToggleGroup(toggleGroup);
        filter2.setToggleGroup(toggleGroup);

        HBox filterRow = new HBox(10, filter1, filter2);
        filterRow.setAlignment(Pos.CENTER);
        Button submitBtn = new Button("Choose");
        submitBtn.setOnAction(e -> {
            // MVC FLOW: User interaction -> View -> Controller -> Model
            if (filter1.isSelected()) {
                // VIEW -> CONTROLLER
                Library printLibrary = this.controller.filterPrintBook();
                // VIEW: updates with filtered data
                tableView.setItems(printLibrary.libraryProperty());
                filter.close();

            } else if (filter2.isSelected()) {
                // VIEW -> CONTROLLER
                Library digitalLibrary = this.controller.filterDigitalBook();
                // VIEW: updates with filtered data
                tableView.setItems(digitalLibrary.libraryProperty());
                filter.close();
            }
        });

        Button cancelBtn = new Button("Close");
        cancelBtn.setOnAction(e -> filter.close());
        HBox btnRow = new HBox(10, submitBtn, cancelBtn);
        VBox root = new VBox(10, heading, filterRow, btnRow);
        Scene filterScene = new Scene(root, 300, 90);
        filter.setScene(filterScene);
        filter.show();
    }

    // This method opens a checkout form allowing the user to borrow or download a
    // book.
    // VIEW: Displays a checkout form showing book details and buttons to checkout
    // the book.
    // CONTROLLER: Handles the user's checkout action and updates the model
    // accordingly.
    // MODEL: Updates the book's availability and the user's borrowed/downloaded
    // list.
    public void checkOutForm(Book selectedBook) {
        Stage checkOut = new Stage();
        checkOut.initOwner(primaryStage);
        checkOut.initModality(Modality.APPLICATION_MODAL);

        Label heading = new Label("Book details: ");
        heading.setFont(new Font("Arial", 20));
        String bookName = selectedBook.getTitle();
        String bookAuthor = selectedBook.getAuthor();
        String bookType = selectedBook.getGenre().name();

        Label nameLabel = new Label(bookName);
        Label authorLabel = new Label(bookAuthor);
        Label typeLabel = new Label(bookType);

        HBox nameRow = new HBox(10, new Label("Title: "), nameLabel);
        HBox authorRow = new HBox(10, new Label("Author: "), authorLabel);
        HBox typeRow = new HBox(10, new Label("Type: "), typeLabel);

        Button checkoutBtnForForm;
        Label successLabel = new Label();
        successLabel.setAlignment(Pos.CENTER);
        checkoutBtnForForm = new Button();

        Button cancelBtn = new Button("Cancel");
        cancelBtn.setOnAction(e -> checkOut.close());

        HBox buttonRow = new HBox(10, checkoutBtnForForm, cancelBtn);
        buttonRow.setAlignment(Pos.CENTER);
        VBox root = new VBox(10, heading, nameRow, authorRow, typeRow, buttonRow, successLabel);
        root.setAlignment(Pos.CENTER);
        Scene checkoutScene = new Scene(root, 300, 300);
        checkOut.setScene(checkoutScene);

        // This code displays the appropriate labels for physical books (PrintBook) in
        // the library system.
        // VIEW: Displays the borrowing options and alerts the user if they have reached
        // the borrow limit.
        // CONTROLLER: Handles the borrow action triggers the pop-up confirmation.
        // MODEL: Checks and enforces the user's borrowing limit.
        if (((selectedBook instanceof PrintBook && ((PrintBook) selectedBook).getAvailable())
                || (selectedBook instanceof DigitalBook && ((DigitalBook) selectedBook).canDownload()))) {
            if (selectedBook instanceof DigitalBook) {
                checkoutBtnForForm.setText("Download");
                checkoutBtnForForm.setOnAction(e -> {
                    controller.checkoutBook(selectedBook);
                    checkOut.close();
                    this.createPopUpForm(primaryStage, "You have successfully downloaded the book!");
                });
                checkOut.show();

            } else if (selectedBook instanceof PrintBook) {
                if (model.overMaximumProperty().get()) {
                    checkoutBtnForForm.setText("Borrow");
                    checkoutBtnForForm.setOnAction(e->{
                        this.createPopUpForm(primaryStage,
                            "You have reached the limit of books you can borrow!"
                                    + "\nPlease return a book to borrow new books");
                        checkOut.close();
                    });
                    
                    checkOut.show();
                } else {
                    checkoutBtnForForm.setText("Borrow");
                    // MVC FLOW: Borrow process
                    // VIEW: Displays the checkout form.
                    checkoutBtnForForm.setOnAction(e -> {
                        // CONTROLLER: Proccesses the borrow request and updates the borrowed
                        // book count.
                        controller.checkoutBook(selectedBook);
                        controller.addCountBorrowedBook();
                        // VIEW: Closes the checkout form and shows a confirmation pop-up.
                        checkOut.close();
                        this.createPopUpForm(primaryStage, "You have successfully borrowed the book!");

                    });
                    // VIEW: Displays the checkout form with the "Borrow " button.
                    checkOut.show();
                }
            }
        } else {
            if (selectedBook instanceof PrintBook) {
                checkoutBtnForForm.setText("Borrow");
                checkoutBtnForForm.setOnAction(e -> {
                    this.createPopUpForm(primaryStage, "This book is not available");
                    checkOut.close();
                });
                checkOut.show();
            } else {
                checkoutBtnForForm.setText("Download");
                checkoutBtnForForm.setOnAction(e -> {
                    this.createPopUpForm(primaryStage, "This book can't be downloaded");
                    checkOut.close();
                });
                checkOut.show();
            }
        }
    }

    // Opens a form that lets the user search for book by format (print/digital)
    // and genre. Displays the filtered results in a TableView and provides options
    // to checkout a selected book or go back to the menu.
    // VIEW: Presents selection options and displays results in a TableView.
    // CONTROLLER: Applies filters based on the user's choices and processes
    // checkout requests.
    // MODEL: Provides filtered book data and updates the books availability and the
    // user's
    // borrowed/downloaded book list.
    public void findBookForm() {
        Stage findBook = new Stage();
        findBook.initOwner(primaryStage);
        findBook.initModality(Modality.APPLICATION_MODAL);

        Stage tableBook = new Stage();
        tableBook.initOwner(primaryStage);
        tableBook.initModality(Modality.APPLICATION_MODAL);

        helpUserView = new TableView<>();
        TableColumn<Book, String> col1 = new TableColumn<>("Title");
        col1.setCellValueFactory(cell -> cell.getValue().titleProperty());
        col1.setMinWidth(220);
        TableColumn<Book, String> col2 = new TableColumn<>("Author");
        col2.setCellValueFactory(cell -> cell.getValue().authorProperty());
        TableColumn<Book, Genre> col3 = new TableColumn<>("Genre");
        col3.setCellValueFactory(cell -> cell.getValue().genreProperty());
        TableColumn<Book, Integer> col4 = new TableColumn<>("Page Count");
        col4.setCellValueFactory(cell -> cell.getValue().pageCountProperty().asObject());
        TableColumn<Book, Status> col5 = new TableColumn<>("Status");
        col5.setCellValueFactory(cell -> cell.getValue().statusProperty());
        helpUserView.getColumns().addAll(col1, col2, col3, col4, col5);

        Label findBook1 = new Label("Choose a format:");
        ToggleGroup toggleGroup1 = new ToggleGroup();
        RadioButton printBook = new RadioButton("Print Book");
        printBook.setToggleGroup(toggleGroup1);
        RadioButton digitalBook = new RadioButton("Digital Book");
        digitalBook.setToggleGroup(toggleGroup1);

        Label findBook2 = new Label("Choose a genre:");
        ToggleGroup toggleGroup2 = new ToggleGroup();
        RadioButton fiction = new RadioButton("Fiction");
        fiction.setToggleGroup(toggleGroup2);
        RadioButton nonfiction = new RadioButton("Non-Fiction");
        nonfiction.setToggleGroup(toggleGroup2);
        RadioButton mystery = new RadioButton("Mystery");
        mystery.setToggleGroup(toggleGroup2);
        RadioButton scienceFiction = new RadioButton("Science Fiction");
        scienceFiction.setToggleGroup(toggleGroup2);
        RadioButton romance = new RadioButton("Romance");
        romance.setToggleGroup(toggleGroup2);
        RadioButton poetry = new RadioButton("Poetry");
        poetry.setToggleGroup(toggleGroup2);
        RadioButton fantasy = new RadioButton("Fantasy");
        fantasy.setToggleGroup(toggleGroup2);

        Button submitBtn = new Button("Submit");
        // Handles the user's search request after clicking "Submit."
        // VIEW: Reads the user's selected format
        // CONTROLLER: Calls the appropriate filter method based on the user's choices.
        // MODEL: Returns a filtered LibraryModel.
        // VIEW: Displays the filtered LibraryModel in a table view.
        submitBtn.setOnAction(e -> {
            if (fiction.isSelected() && printBook.isSelected()) {
                Library printLibrary = controller.filterPrintBook(Genre.FICTION);
                helpUserView.setItems(printLibrary.libraryProperty());
            } else if (nonfiction.isSelected() && printBook.isSelected()) {
                Library printLibrary = controller.filterPrintBook(Genre.NON_FICTION);
                helpUserView.setItems(printLibrary.libraryProperty());
            } else if (fantasy.isSelected() && printBook.isSelected()) {
                Library printLibrary = controller.filterPrintBook(Genre.FANTASY);
                helpUserView.setItems(printLibrary.libraryProperty());
            } else if (mystery.isSelected() && printBook.isSelected()) {
                Library printLibrary = controller.filterPrintBook(Genre.MYSTERY);
                helpUserView.setItems(printLibrary.libraryProperty());
            } else if (scienceFiction.isSelected() && printBook.isSelected()) {
                Library printLibrary = controller.filterPrintBook(Genre.SCIENCE_FICTION);
                helpUserView.setItems(printLibrary.libraryProperty());
            } else if (romance.isSelected() && printBook.isSelected()) {
                Library printLibrary = controller.filterPrintBook(Genre.ROMANCE);
                helpUserView.setItems(printLibrary.libraryProperty());
            } else if (poetry.isSelected() && printBook.isSelected()) {
                Library printLibrary = controller.filterPrintBook(Genre.POETRY);
                helpUserView.setItems(printLibrary.libraryProperty());
            } else if (fiction.isSelected() && digitalBook.isSelected()) {
                Library digitalLibrary = controller.filterDigitalBook(Genre.FICTION);
                helpUserView.setItems(digitalLibrary.libraryProperty());
            } else if (nonfiction.isSelected() && digitalBook.isSelected()) {
                Library digitalLibrary = controller.filterDigitalBook(Genre.NON_FICTION);
                helpUserView.setItems(digitalLibrary.libraryProperty());
            } else if (fantasy.isSelected() && digitalBook.isSelected()) {
                Library digitalLibrary = controller.filterDigitalBook(Genre.FANTASY);
                helpUserView.setItems(digitalLibrary.libraryProperty());
            } else if (mystery.isSelected() && digitalBook.isSelected()) {
                Library digitalLibrary = controller.filterDigitalBook(Genre.MYSTERY);
                helpUserView.setItems(digitalLibrary.libraryProperty());
            } else if (scienceFiction.isSelected() && digitalBook.isSelected()) {
                Library digitalLibrary = controller.filterDigitalBook(Genre.SCIENCE_FICTION);
                helpUserView.setItems(digitalLibrary.libraryProperty());
            } else if (romance.isSelected() && digitalBook.isSelected()) {
                Library digitalLibrary = controller.filterDigitalBook(Genre.ROMANCE);
                helpUserView.setItems(digitalLibrary.libraryProperty());
            } else if (poetry.isSelected() && digitalBook.isSelected()) {
                Library digitalLibrary = controller.filterDigitalBook(Genre.POETRY);
                helpUserView.setItems(digitalLibrary.libraryProperty());
            }

            Button checkOutButton = new Button("Checkout");
            // Begins the checkout process.
            // VIEW: Prompts user to select a book and click "Checkout."
            // CONTROLLER: Handles the checkout request from the view.
            // MODEL: Updates the book's status and the user's borrowed and downloaded
            // list.
            checkOutButton.setOnAction(event -> {
                Book selectedBook = helpUserView.getSelectionModel().getSelectedItem();
                if (selectedBook != null) {
                    this.checkOutForm(selectedBook);
                }
            });
            Button backToMenu = new Button("Back");
            backToMenu.setOnAction(event -> {
                tableBook.close();
            });
            HBox optionRow = new HBox(10, backToMenu, checkOutButton);
            optionRow.setAlignment(Pos.BOTTOM_LEFT);
            VBox view = new VBox(5, helpUserView, optionRow);
            view.setAlignment(Pos.CENTER);
            Scene scene = new Scene(view, 600, 500);
            tableBook.setScene(scene);
            tableBook.show();
            findBook.close();

        });

        HBox checkBookRow = new HBox(5, printBook, digitalBook);
        checkBookRow.setAlignment(Pos.CENTER);
        HBox row1 = new HBox(5, fiction, romance, nonfiction);
        row1.setAlignment(Pos.CENTER);
        HBox row2 = new HBox(5, fantasy, poetry, mystery, scienceFiction);
        row2.setAlignment(Pos.CENTER);
        VBox view = new VBox(20, findBook1, checkBookRow, findBook2, row1, row2, submitBtn);
        view.setAlignment(Pos.CENTER);

        Scene scene1 = new Scene(view, 400, 300);
        findBook.setScene(scene1);
        findBook.show();
    }

    // VIEW: Builds the dialog box with labels, buttons, and layout.
    public void searchBook() {
        Stage helpUser = new Stage();
        helpUser.initOwner(primaryStage);
        helpUser.initModality(Modality.APPLICATION_MODAL);
        Label message = new Label("Sorry, that book title could not be found");
        message.setFont(new Font("Arial", 20));
        Label helpUserFindBook = new Label("Do you want help searching for a book?");
        helpUserFindBook.setFont(new Font("Arial", 18));
        Button yes = new Button("Yes");
        yes.setMinWidth(60);
        yes.setOnAction(e -> {
            helpUser.close();
            findBookForm();
        });
        Button no = new Button("No");
        no.setMinWidth(60);
        no.setOnAction(e -> {
            helpUser.close();
        });

        HBox answer = new HBox(50, yes, no);
        answer.setAlignment(Pos.BOTTOM_CENTER);
        VBox helpBox = new VBox(5, message, helpUserFindBook);
        helpBox.setAlignment(Pos.CENTER);
        VBox view = new VBox(5, helpBox, answer);
        view.setAlignment(Pos.CENTER);
        Scene scene = new Scene(view, 400, 200);
        helpUser.setScene(scene);
        helpUser.show();

    }

    // This method creates a JavaFX Scene that allows the user to search for books
    // by title. It provides an interactive interface with input fields and buttons
    // to guide the user through the search process.
    // VIEW: Displays input fields, buttons and labels to allow the user to search
    // by title.
    // CONTROLLER: Handles the search request with the model and triggers the
    // appropriate view updates.
    // MODEL: Stores and manages the book data. It returns a book related to the
    // search query.
    public Scene createSceneOpt2() {
        Label heading = new Label("Searching for books");
        heading.setFont(new Font("Arial", 20));
        TextField inputField = new TextField();
        inputField.setPromptText("Enter book title: ");
        inputField.setMaxWidth(300);
        Button searchBtn = new Button("Search");
        Label message = new Label();
        Label helpUser = new Label();

        searchBtn.setOnAction(e -> {
            String bookTitle = inputField.getText().toLowerCase().trim();
            Book foundedBook = controller.getBook(bookTitle);

            if (foundedBook == null) {
                searchBook();
            } else {
                checkOutForm(foundedBook);

            }
        });
        Button backBtn = new Button("Back to Menu");
        backBtn.setOnAction(e -> {
            primaryStage.setScene(scene);
        });

        HBox buttonRow = new HBox(10, searchBtn, backBtn);
        buttonRow.setAlignment(Pos.CENTER);

        VBox viewOpt2 = new VBox(20, heading, inputField, buttonRow, message, helpUser);
        viewOpt2.setAlignment(Pos.CENTER);
        Scene scene2 = new Scene(viewOpt2, 500, 500);
        return scene2;
    }

    public void updateControllerFromListeners() {

    }

    public void observeModelAndUpdateControls() {

    }

    // This method creates a scene that displays lists of books the user has
    // borrowed and downloaded. It also provides functionality to return borrowed
    // books and navigate back to the main menu. Essentially, it serves as a user
    // dashboard for managing their library interactions. To ensure the logic of the
    // system, we also have added
    // some commands to prevent users returning the digital books.
    // VIEW: User's book display with return functionality
    public Scene createSceneOpt3() {
        Label headingBorrowed = new Label("List of your borrowed books: ");
        headingBorrowed.setFont(new Font("Arial", 20));
        borrowedBookView = new TableView<>();

        TableColumn<Book, String> col1 = new TableColumn<>("Title");
        col1.setCellValueFactory(cell -> cell.getValue().titleProperty());
        col1.setMinWidth(220);
        TableColumn<Book, String> col2 = new TableColumn<>("Author");
        col2.setCellValueFactory(cell -> cell.getValue().authorProperty());
        TableColumn<Book, Genre> col3 = new TableColumn<>("Genre");
        col3.setCellValueFactory(cell -> cell.getValue().genreProperty());
        TableColumn<Book, Integer> col4 = new TableColumn<>("Page Count");
        col4.setCellValueFactory(cell -> cell.getValue().pageCountProperty().asObject());
        borrowedBookView.getColumns().addAll(col1, col2, col3, col4);
        borrowedBookView.setItems(controller.getBorrowedBooks());

        Label headingDownloaded = new Label("List of your downloaded books: ");
        headingDownloaded.setFont(new Font("Arial", 20));
        downloadedBookView = new TableView<>();
        TableColumn<Book, String> col5 = new TableColumn<>("Title");
        col5.setCellValueFactory(cell -> cell.getValue().titleProperty());
        col5.setMinWidth(220);
        TableColumn<Book, String> col6 = new TableColumn<>("Author");
        col6.setCellValueFactory(cell -> cell.getValue().authorProperty());
        TableColumn<Book, Genre> col7 = new TableColumn<>("Genre");
        col7.setCellValueFactory(cell -> cell.getValue().genreProperty());
        TableColumn<Book, Integer> col8 = new TableColumn<>("Page Count");
        col8.setCellValueFactory(cell -> cell.getValue().pageCountProperty().asObject());
        downloadedBookView.getColumns().addAll(col5, col6, col7, col8);
        downloadedBookView.setItems(controller.getDownloadedBooks());

        Button backButton = new Button("Back to Menu");
        backButton.setOnAction(e -> {
            primaryStage.setScene(scene);
        });

        Button returnBtn = new Button("Return Book");
        // This button initiates the book return process by sending the selected book to
        // the controller.
        // VIEW: Prompts user to return a book and sends a request to the controller.
        // CONTROLLER: Handles the request from view, updates the book status in the
        // model, and triggers an update in the view.
        // MODEL: Updates the books availability and the users book list.
        returnBtn.setOnAction(e -> {
            Book selectedBook = borrowedBookView.getSelectionModel().getSelectedItem();
            if (!(selectedBook instanceof PrintBook) || selectedBook == null) {
                this.createPopUpForm(primaryStage, "Please select a borrowed book to return.");
            } else {
                controller.returnBook(selectedBook);
                controller.subtractCountBorrowedBook();
            }
        });

        HBox buttonRow = new HBox(10, backButton, returnBtn);

        VBox root = new VBox(10, headingBorrowed, borrowedBookView, headingDownloaded, downloadedBookView, buttonRow);
        root.setAlignment(Pos.CENTER);
        Scene userBookScene = new Scene(root, 500, 500);
        return userBookScene;
    }

    // Displays a pop-up notification with a custom message to the user.
    public void createPopUpForm(Stage ownerStage, String message) {
        Stage form = new Stage();
        form.initOwner(ownerStage);
        form.initModality(Modality.APPLICATION_MODAL);
        Label messageLabel = new Label(message);
        Button cancelBtn = new Button("Close");
        cancelBtn.setOnAction(e -> form.close());
        VBox root = new VBox(20, messageLabel, cancelBtn);
        root.setAlignment(Pos.CENTER);
        Scene formScene = new Scene(root, 300, 100);
        form.setScene(formScene);
        form.show();
    }
}