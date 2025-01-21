import javafx.application.Application;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.util.HashMap;
import java.util.Map;

public class LibraryManagementSystem extends Application {
    //burada hashmapleri oluşturuyoruz
    private Map<String, User> users = new HashMap<>();//users hashmap
    private Map<String, Book> books = new HashMap<>();//books hashmap
    private Stage primaryStage;//burada Stageden bir nesne oluşturduk

    public static void main(String[] args) {
        //main metod run ediyoruz
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        primaryStage.setTitle("Devlet Kütüphanesi Yönetim Sistemi");

        //login panelini oluşturduğumuz kısım
        showLoginPanel();

        primaryStage.show();
    }

    private void showRegistrationPanel() {
        //kayıt ekranını ayarladığımız metot
        GridPane registrationPane = new GridPane();
        registrationPane.setAlignment(Pos.CENTER);
        registrationPane.setHgap(10);
        registrationPane.setVgap(10);
        registrationPane.setPadding(new Insets(10));
//ad butonunu oluşturduk
        Label nameLabel = new Label("Ad:");
        TextField nameField = new TextField();
        registrationPane.add(nameLabel, 0, 0);
        registrationPane.add(nameField, 1, 0);
//soyad butonunu oluşturduk
        Label surnameLabel = new Label("Soyad:");
        TextField surnameField = new TextField();
        registrationPane.add(surnameLabel, 0, 1);
        registrationPane.add(surnameField, 1, 1);
//kullanıcı adı butonunu oluşturduk
        Label usernameLabel = new Label("Kullanıcı Adı:");
        TextField usernameField = new TextField();
        registrationPane.add(usernameLabel, 0, 2);
        registrationPane.add(usernameField, 1, 2);
//şifre butonunu oluşturduk
        Label passwordLabel = new Label("Şifre:");
        PasswordField passwordField = new PasswordField();
        registrationPane.add(passwordLabel, 0, 3);
        registrationPane.add(passwordField, 1, 3);
//rol butonunu oluşturduk
        Label roleLabel = new Label("Rol:");
        ComboBox<String> roleComboBox = new ComboBox<>();
        roleComboBox.getItems().addAll("Admin", "User");
        registrationPane.add(roleLabel, 0, 4);
        registrationPane.add(roleComboBox, 1, 4);
//kayıt ol butonunu oluşturduk
        Button registerButton = new Button("Kayıt Ol");
        registerButton.setOnAction(event -> {
            String name = nameField.getText();//isim giriş
            String surname = surnameField.getText();//soyad giriş
            String username = usernameField.getText();//kullanıcı adı giriş
            String password = passwordField.getText();//şifre giriş
            String selectedRole = roleComboBox.getValue();//rol seçiş kısmı

            if (name.isEmpty() || surname.isEmpty() || username.isEmpty() || password.isEmpty() || selectedRole == null) {
                //girişlerde hata varsa bu uyarıyı alırız
                showAlert(Alert.AlertType.WARNING, "Uyarı", "Tüm alanları doldurun.");
            } else if (getUser(username) != null) {
                //isim eşleşmezse bu hatayı alırız
                showAlert(Alert.AlertType.WARNING, "Uyarı", "Bu kullanıcı adı zaten alınmış.");
            } else {
                //admin hakları
                int maxBooks = selectedRole.equals("Admin") ? 10 : 3;
                User newUser = new User(username, password, maxBooks);
                addUser(newUser);
                //kayıt olursa
                showAlert(Alert.AlertType.INFORMATION, "Başarılı", "Kayıt işlemi tamamlandı. Giriş yapabilirsiniz.");
                showLoginPanel();
            }
        });

        Button backButton = new Button("Geri");
        //geri butonu
        backButton.setOnAction(event -> showLoginPanel());

        registrationPane.add(registerButton, 1, 5);
        registrationPane.add(backButton, 0, 5);

        Scene registrationScene = new Scene(registrationPane, 400, 250);
        primaryStage.setScene(registrationScene);
    }

    private void showLoginPanel() {
        //kayıt ekranı
        GridPane loginPane = new GridPane();
        loginPane.setAlignment(Pos.CENTER);
        loginPane.setHgap(10);
        loginPane.setVgap(10);
        loginPane.setPadding(new Insets(10));
//kullanıcı adının butonunu burada tasarladık
        Label usernameLabel = new Label("Kullanıcı Adı:");
        TextField usernameField = new TextField();
        loginPane.add(usernameLabel, 0, 0);
        loginPane.add(usernameField, 1, 0);
//şifre butonunu burada tasarladık
        Label passwordLabel = new Label("Şifre:");
        PasswordField passwordField = new PasswordField();
        loginPane.add(passwordLabel, 0, 1);
        loginPane.add(passwordField, 1, 1);
//giriş yap butonunu burada tasarladık
        Button loginButton = new Button("Giriş Yap");
        loginButton.setOnAction(event -> {
            String username = usernameField.getText();//kullanıcı adı giriş
            String password = passwordField.getText();//şifre giriş
            User user = getUser(username);

            if (user != null && user.authenticate(password)) {
                primaryStage.setScene(createLibraryScene(primaryStage, user));
            } else {
                //şifre veya kullanıcı adı geçersiz olursa bu hatayı alırız
                showAlert(Alert.AlertType.ERROR, "Giriş Hatası", "Geçersiz kullanıcı adı veya şifre.");
            }
        });

        Button registerButton = new Button("Üye Ol");
        //üye ol butonu
        registerButton.setOnAction(event -> showRegistrationPanel());

        loginPane.add(loginButton, 1, 2);
        loginPane.add(registerButton, 1, 3);

        Scene loginScene = new Scene(loginPane, 400, 200);
        primaryStage.setScene(loginScene);
    }

    private Scene createLibraryScene(Stage primaryStage, User user) {
        //kütüphanemizin giriş kısmı
        GridPane libraryPane = new GridPane();
        libraryPane.setAlignment(Pos.CENTER);
        libraryPane.setHgap(10);
        libraryPane.setVgap(10);
        libraryPane.setPadding(new Insets(10));
//hoş geldiniz giriş erkanında çıkar
        Label welcomeLabel = new Label("Hoş Geldiniz, " + user.getUsername());
        welcomeLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        libraryPane.add(welcomeLabel, 0, 0, 2, 1);
//ad butonunu oluşturduk
        Label nameLabel = new Label("Ad:");
        TextField nameField = new TextField();
        libraryPane.add(nameLabel, 0, 1);
        libraryPane.add(nameField, 1, 1);
//yazar butonunu oluşturduk
        Label authorLabel = new Label("Yazar:");
        TextField authorField = new TextField();
        libraryPane.add(authorLabel, 0, 2);
        libraryPane.add(authorField, 1, 2);
//kategori butonunu oluşturduk
        Label categoryLabel = new Label("Kategori:");
        TextField categoryField = new TextField();
        libraryPane.add(categoryLabel, 0, 3);
        libraryPane.add(categoryField, 1, 3);
//kitabı tahmin etmek için baş harfini girme butonu
        Label startsWithLabel = new Label("Baş harf:");
        TextField startsWithField = new TextField();
        libraryPane.add(startsWithLabel, 0, 4);
        libraryPane.add(startsWithField, 1, 4);
//ara butonu
        Button searchButton = new Button("Ara");
        searchButton.setOnAction(event -> {
            //genel girişler
            String name = nameField.getText();
            String author = authorField.getText();
            String category = categoryField.getText();
            String startsWith = startsWithField.getText();
            searchBooks(name, author, category, startsWith);
        });
//kitap al butonu
        Button borrowButton = new Button("Kitap Al");
        borrowButton.setOnAction(event -> {
            //genel girişler
            String name = nameField.getText();
            String author = authorField.getText();
            String category = categoryField.getText();
            String startsWith = startsWithField.getText();
            borrowBook(user, name, author, category, startsWith);
        });
//çıkış yap butonu
        Button logoutButton = new Button("Çıkış Yap");
        logoutButton.setOnAction(event -> showLoginPanel());

        libraryPane.add(searchButton, 1, 5);
        libraryPane.add(borrowButton, 1, 6);
        libraryPane.add(logoutButton, 0, 5);

        Scene libraryScene = new Scene(libraryPane, 400, 300);
        return libraryScene;
    }
//kitap arama metodu
    private void searchBooks(String name, String author, String category, String startsWith) {
        for (Book book : books.values()) {
            //kitaplar dögüyle tektek gezilir burda eşleşme olursa onu ekrana yazdırırız
            if (bookMatchesCriteria(book, name, author, category, startsWith)) {
                System.out.println("Kitap Bulundu: " + book.getTitle());
            }
        }
    }
//yine aynı şekilde kitabın eşleşmesi var mı yok mu onu kontrol eden bir metot
    private boolean bookMatchesCriteria(Book book, String name, String author, String category, String startsWith) {
        if (name.isEmpty() && author.isEmpty() && category.isEmpty() && startsWith.isEmpty()) {
            return true;
        }

        boolean nameMatch = book.getTitle().toLowerCase().contains(name.toLowerCase());//isim doğrumu
        boolean authorMatch = book.getAuthor().toLowerCase().contains(author.toLowerCase());//yazar doğru mu
        boolean categoryMatch = book.getCategory().toLowerCase().contains(category.toLowerCase());//kategori doğru mu
        boolean startsWithMatch = book.getTitle().toLowerCase().startsWith(startsWith.toLowerCase());//ilk girilen harf doğru mu

        return nameMatch || authorMatch || categoryMatch || startsWithMatch;
    }

    private void borrowBook(User user, String name, String author, String category, String startsWith) {
        for (Book book : books.values()) {
            //ödünç alınan kitap doğruysa eşleşir döngü sayesinde
            if (bookMatchesCriteria(book, name, author, category, startsWith) && book.getQuantity() > 0) {
                user.addBook(book);
                book.setQuantity(book.getQuantity() - 1);
                //eğer eşleşmezse bu uyarıyı alırız
                System.out.println(user.getUsername() + " adlı kullanıcı " + book.getTitle() + " adlı kitabı aldı.");
                return;
            }
        }
        //kitap hiç bulunmazsa bu tepki alınır
        System.out.println("Kitap alınamadı. Belirtilen kriterlere uygun kitap bulunamadı veya stokta yok.");
    }

    private User getUser(String username) {
        //döngü gezilip bütün isimlerin toplandığı metot
        for (User user : users.values()) {
            if (user.getUsername().equals(username)) {
                return user;
            }
        }
        return null;
    }

    private void addUser(User user) {
        //eklemeyi yaptığımız metot
        if (getUser(user.getUsername()) == null) {
            users.put(user.getUsername(), user);
        } else {
            showAlert(Alert.AlertType.WARNING, "Uyarı", "Bu kullanıcı adı zaten alınmış.");
        }
    }

    private void addBook(Book book) {
        //bu metotla kitap ekleme yapın
        books.put(book.getTitle(), book);
    }
//burada hataları yakalayıp metot olarak çevirdiğimiz kısım
    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private class User {
        private String username;//kullanıcı adı giriş
        private String password;//şifre giriş
        private IntegerProperty maxBooks;//maksimum kitap sayısı giriş
        private Map<String, Book> borrowedBooks;//alınmış kitaplar

        public User(String username, String password, int maxBooks) {
            //kurucu metot
            this.username = username;
            this.password = password;
            this.maxBooks = new SimpleIntegerProperty(maxBooks);
            this.borrowedBooks = new HashMap<>();
        }

        public String getUsername() {
            //kullanıcı adını döndürdük
            return username;
        }

        public boolean authenticate(String password) {
            //doğrumu şifre değilmi döndürdüğümüz kısım
            return this.password.equals(password);
        }

        public int getMaxBooks() {
            //maksimum kitap sayısını döndürdüğümüz kısım
            return maxBooks.get();
        }
        // Kullanıcının alabileceği maksimum kitap sayısını döndüren property
        public IntegerProperty maxBooksProperty() {
            return maxBooks;
        }
        // Kullanıcının aldığı kitapları döndüren metot

        public Map<String, Book> getBooks() {
            return borrowedBooks;
        }
        // Kullanıcıya bir kitap ekleyen metot

        public void addBook(Book book) {
            borrowedBooks.put(book.getTitle(), book);
        }
        // Kullanıcının aldığı bir kitabı geri veren metot

        public boolean removeBook(Book book) {
            return borrowedBooks.remove(book.getTitle()) != null;
        }
    }
// Kitap sınıfı, kütüphane yönetim sistemindeki kitapları temsil eder.

    private class Book {
        // Kitabın adını tutan özellik

        private StringProperty title;
        private IntegerProperty quantity;    // Kitabın stok sayısını tutan özellik

        private StringProperty category;    // Kitabın kategorisini tutan özellik

        private StringProperty author;    // Kitabın kategorisini tutan özellik

        private IntegerProperty year;    // Kitabın yayın yılını tutan özellik


        public Book(String title, int quantity, String category, String author, int year) {
            //kurucu metot
            this.title = new SimpleStringProperty(title);
            this.quantity = new SimpleIntegerProperty(quantity);
            this.category = new SimpleStringProperty(category);
            this.author = new SimpleStringProperty(author);
            this.year = new SimpleIntegerProperty(year);
        }

        public String getTitle() {
            // Kitabın adını döndüren metot

            return title.get();
        }

        public StringProperty titleProperty() {
            // Kitabın adını döndüren property

            return title;
        }

        public int getQuantity() {
            // Kitabın stok sayısını döndüren metot

            return quantity.get();
        }

        public void setQuantity(int quantity) {
            // Kitabın stok sayısını güncelleyen metot

            this.quantity.set(quantity);
        }

        public IntegerProperty quantityProperty() {
            // Kitabın stok sayısını döndüren property

            return quantity;
        }

        public String getCategory() {
            // Kitabın kategorisini döndüren metot

            return category.get();
        }

        public StringProperty categoryProperty() {
            // Kitabın kategorisini döndüren property

            return category;
        }

        public String getAuthor() {
            // Kitabın yazarını döndüren metot

            return author.get();
        }

        public StringProperty authorProperty() {
            // Kitabın yazarını döndüren property

            return author;
        }

        public int getYear() {
            // Kitabın yayın yılını döndüren metot

            return year.get();
        }

        public IntegerProperty yearProperty() {
            // Kitabın yayın yılını döndüren property

            return year;
        }
    }
}