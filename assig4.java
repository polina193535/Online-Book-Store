import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

//Polina Kostikova ISE-02
public class assig4 {
    public static void main(String[] args) { // input
        BookStore bookstore = new BookStore();
        Scanner in = new Scanner(System.in);
        String input = in.next();
        if (input.equals("end")) {
            System.exit(0);
        }
        while (!(input.equals("end"))) {
            if (input.equals("createBook")) {
                String title = in.next();
                String author = in.next();
                String price = in.next();
                bookstore.createBook(title, author, price);
            } else if (input.equals("createUser")) {
                String type = in.next();
                String name = in.next();
                bookstore.createUser(type, name);
            } else if (input.equals("subscribe")) {
                String name = in.next();
                bookstore.subscribe(name);
            } else if (input.equals("unsubscribe")) {
                String name = in.next();
                bookstore.unsubscribe(name);
            } else if (input.equals("updatePrice")) {
                String title = in.next();
                String price = in.next();
                bookstore.updatePrice(title, price);
            } else if (input.equals("readBook")) {
                String name = in.next();
                String title = in.next();
                bookstore.readBook(name, title);
            } else if (input.equals("listenBook")) {
                String name = in.next();
                String title = in.next();
                bookstore.listenBook(name, title);
            }
            input = in.next();
        }
    }
}

interface BookProxyInterface { // interface for Proxy pattern

    String getAuthor();

    void setPrice(String price);
}

class BookProxy implements BookProxyInterface { // BookProxy for Proxy pattern
    private Book book;

    public BookProxy(Book book) {
        this.book = book;
    }

    public String getAuthor() {
        return book.getAuthor(); // return author
    }

    public void setPrice(String price) {
        book.setPrice(price); // update price
    }
}

class Book {
    private String title;
    private String author;
    private String price;

    public Book(String title, String author, String price) {
        this.title = title;
        this.author = author;
        this.price = price;
    }

    public String getAuthor() {
        return author;
    }

    public void setPrice(String price) {
        this.price = price;
    }
}

interface Subscribers {
    void update(String title, String newprice); // update price
}

interface Subject {
    void addSubscriber(Subscribers subscriber);

    void removeSubscriber(Subscribers subscriber);

    void notifySubscribers();
}

class Subscriptions implements Subject {
    private String title;
    private String newPrice;
    private List<Subscribers> subscribers = new ArrayList<>();

    public void addSubscriber(Subscribers subscriber) {
        subscribers.add(subscriber); // add
    }

    public void removeSubscriber(Subscribers subscriber) {
        subscribers.remove(subscriber);// remove
    }

    public void notifySubscribers() { // notify about price updating
        for (Subscribers subscriber : subscribers) {
            subscriber.update(title, newPrice);
        }
    }

    public boolean containsSubscriber(Subscribers subscriber) {
        if (subscribers.contains(subscriber)) { // check if the user is subscribed
            return true;
        } else {
            return false;
        }
    }

    public void setPrice(String title, String newPrcice) {
        this.title = title;
        this.newPrice = newPrcice;
        notifySubscribers();// notify subscribers
    }
}

abstract class User {
    public String username;

    public User(String username) {
        this.username = username;
    }

    public abstract void listenBook(String title, String author);
}

class StandardUser extends User implements Subscribers {

    public StandardUser(String username) {
        super(username);
    }

    public void listenBook(String title, String author) {
        System.out.println("No access"); // because standard user has no access
    }

    public void update(String title, String newPrice) {
        System.out.println(username + " notified about price update for " + title + " to " + newPrice); // notification
    }
}

class PremiumUser extends User implements Subscribers {
    public PremiumUser(String username) {
        super(username);
    }

    public void listenBook(String title, String author) {
        System.out.println(username + " listening " + title + " by " + author); // premium user can listen to book
    }

    public void update(String title, String newPrice) {
        System.out.println(username + " notified about price update for " + title + " to " + newPrice);// notification
    }
}

interface UserFactory {
    public User createUser(String username); // create users
}

class StandardUserFactory implements UserFactory {
    public User createUser(String username) {
        return new StandardUser(username); // create standard user
    }
}

class PremiumUserFactory implements UserFactory {
    public User createUser(String username) {
        return new PremiumUser(username); // create premium user
    }
}

class BookStore {
    private Map<String, BookProxy> books;// contains book titles and instances of book
    private Map<String, User> users; // contains user names and instances of users

    Subscriptions subscriptions = new Subscriptions();// create class for subscribers

    public BookStore() {
        books = new HashMap<String, BookProxy>();
        users = new HashMap<String, User>();
    }

    public void createBook(String title, String author, String price) {
        if (books.containsKey(title)) { // check if book exists
            System.out.println("Book already exists");
        } else {
            Book book = new Book(title, author, price);
            BookProxy bookProxy = new BookProxy(book);
            books.put(title, bookProxy);
        }
    };

    public void createUser(String type, String name) {
        if (users.containsKey(name)) {
            System.out.println("User already exists");// check if user exists
        } else {
            if (type.equals("standard")) {
                StandardUserFactory standardfactory = new StandardUserFactory();
                User user = standardfactory.createUser(name);// create standard user
                users.put(name, user);
            } else {
                PremiumUserFactory premiumfactory = new PremiumUserFactory();
                User user = premiumfactory.createUser(name); // create premium user
                users.put(name, user);
            }

        }
    };

    public void subscribe(String name) {
        Subscribers user = (Subscribers) users.get(name);
        if (subscriptions.containsSubscriber(user)) {
            System.out.println("User already subscribed"); // check if user subscribed
        } else {

            subscriptions.addSubscriber(user);

        }
    };

    public void unsubscribe(String name) {
        Subscribers user = (Subscribers) users.get(name);
        if (!subscriptions.containsSubscriber(user)) {
            System.out.println("User is not subscribed");// check if user is not subscribed
        } else {
            subscriptions.removeSubscriber(user);
        }
    };

    public void updatePrice(String title, String newPrice) {
        subscriptions.setPrice(title, newPrice);// notification
        books.get(title).setPrice(newPrice);// update price
    };

    public void readBook(String name, String title) {
        System.out.println(name + " reading " + title + " by " + books.get(title).getAuthor());
    };

    public void listenBook(String username, String title) {
        users.get(username).listenBook(title, books.get(title).getAuthor());// for different types of user there is
                                                                            // different output
    };

}