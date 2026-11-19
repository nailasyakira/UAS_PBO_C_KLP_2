package supermarket;

import supermarket.exception.AuthenticationException;
import supermarket.model.Admin;
import supermarket.model.Customer;
import supermarket.model.Role;
import supermarket.model.User;
import supermarket.service.ProductService;
import supermarket.service.TransactionService;
import supermarket.service.UserService;
import supermarket.ui.AdminMenu;
import supermarket.ui.ConsoleInput;
import supermarket.ui.CustomerMenu;
import supermarket.ui.Menu;

/**
 * Titik masuk aplikasi Supermarket versi CLI.
 * Versi GUI ada di {@link supermarket.gui.MainGUI}.
 */
public class Main {

    /**
     * Menjalankan aplikasi CLI.
     *
     * @param args argumen command line (tidak dipakai)
     */
    public static void main(String[] args) {
        AppContext context = new AppContext();
        UserService userService = context.getUserService();
        ProductService productService = context.getProductService();
        TransactionService transactionService = context.getTransactionService();

        ConsoleInput input = new ConsoleInput();
        boolean running = true;
        while (running) {
            System.out.println("\n===== SUPERMARKET APP =====");
            System.out.println("1. Login");
            System.out.println("0. Keluar");
            int choice = input.readInt("Pilih: ");

            if (choice == 0) {
                running = false;
            } else if (choice == 1) {
                try {
                    String username = input.readString("Username: ");
                    String password = input.readString("Password: ");
                    User user = userService.login(username, password);
                    System.out.println(user.getWelcomeMessage());

                    Menu menu = (user.getRole() == Role.ADMIN)
                            ? new AdminMenu((Admin) user, input, productService, transactionService, userService)
                            : new CustomerMenu((Customer) user, input, productService, transactionService);
                    menu.show(); // polymorphism: Admin dan Customer punya menu berbeda
                } catch (AuthenticationException e) {
                    System.out.println("[!] " + e.getMessage());
                }
            } else {
                System.out.println("[!] Pilihan tidak valid.");
            }
        }
        System.out.println("Sampai jumpa!");
    }
}
