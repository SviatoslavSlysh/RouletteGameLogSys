package org.roulettegame;

import org.roulettegame.database.SQLite;
import org.roulettegame.model.User;

import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.*;

import static org.roulettegame.database.SQLite.getUserBalance;
import static org.roulettegame.database.SQLite.setUserBalance;

public class Main {
    private static final Scanner scanner = new Scanner(System.in);
    private static final Random random = new Random();

    public static void main(String[] args) {
        while (true) {
            System.out.println("Choose an option:");
            System.out.println("1. Sign in ");
            System.out.println("2. Register ");
            System.out.println("3. Exit ");

            int choice = getUserChoice();

            try {
                switch (choice) {
                    case 1:
                        User user = signIn();
                        if (user != null) {
                            showGameMenu(user);
                        }
                        break;
                    case 2:
                        register();
                        break;
                    case 3:
                        System.out.println("Exiting the program. Goodbye!");
                        System.exit(0);
                    default:
                        System.out.println("Invalid choice. Please enter a valid option.");
                }
            } catch (SQLException e) {
                System.out.println("An error occurred while processing the request: " + e.getMessage());
            } catch (NoSuchAlgorithmException e) {
                System.out.println("An error occurred while hashing the password: " + e.getMessage());
            }
        }
    }

    private static void showGameMenu(User user) throws SQLException {
        int finalBalance = getUserBalance(user.getId());
        while (true) {
            System.out.println("\nRoulette Game Menu:");
            System.out.println("1. Play Roulette");
            System.out.println("2. Deposit");
            System.out.println("3. Exit");

            System.out.print("Choose an option (1-3): ");
            int choice = getUserChoice();

            switch (choice) {
                case 1:
                    playRoulette(user.getId());
                    break;
                case 2:
                    deposit(user, finalBalance);
                    break;
                case 3:
                    System.out.println("Goodbye!");
                    System.exit(0);
                default:
                    System.out.println("Invalid choice. Please choose an option (1-3).");
            }
        }
    }

    private static User signIn() throws SQLException, NoSuchAlgorithmException {
        System.out.print("Enter login: ");
        String username = scanner.next();

        System.out.print("Enter password: ");
        String password = scanner.next();

        User user = SQLite.validateUser(username, password);
        if (user != null) {
            System.out.println("Login successful!");
            return user;
        } else {
            System.out.println("Invalid username or password. Please try again.");
            return null;
        }
    }

    private static User register() throws SQLException, NoSuchAlgorithmException {
        System.out.print("Create username: ");
        String login = scanner.next();

        if (SQLite.getUserByUsername(login) != null) {
            System.out.println("Username already exists. Please choose a different username.");
            return null;
        }

        System.out.print("Create password: ");
        String password = scanner.next();

        User user = SQLite.insertUser(login, password);
        System.out.println("Account created successfully. Please log in.");

        return user;
    }

    private static int getUserChoice() {
        int choice = 0;
        try {
            choice = scanner.nextInt();
        } catch (InputMismatchException e) {
            scanner.next();
        }
        return choice;
    }

    private static void playRoulette(Integer userId) {
        try {
            int currentBalance;
            while (true) {
                currentBalance = getUserBalance(userId);
                if (currentBalance < 0) {
                    System.out.println("Failed to retrieve user balance. Exiting...");
                    System.exit(1);
                }
                System.out.println("Your current balance: " + currentBalance);

                System.out.println("What do you want to bet on? (red, black, green): ");
                String betColor = scanner.next().toLowerCase();

                System.out.println("Enter your bet amount: ");
                int betAmount = scanner.nextInt();
                if (betAmount > currentBalance) {
                    System.out.println("You don't have enough balance.");
                    continue;
                }
                currentBalance -= betAmount;
                int finalBalance = currentBalance;

                int result = random.nextInt(31);
                String resultColor;
                if (result == 0) {
                    resultColor = "green";
                } else if (result % 2 == 0) {
                    resultColor = "black";
                } else {
                    resultColor = "red";
                }

                System.out.println("The ball fell on " + result + " (" + resultColor + ")");

                if (betColor.equals(resultColor) && !resultColor.equals("green")) {
                    System.out.println("Congratulations! You win!");
                    finalBalance = currentBalance + betAmount * 2;
                } else if (betColor.equals(resultColor) && resultColor.equals("green")) {
                    System.out.println("Congratulations! Mega win!");
                    finalBalance = currentBalance + betAmount * 30;
                } else {
                    System.out.println("Sorry, you lost");
                }
                setUserBalance(userId, finalBalance);

                System.out.print("Wanna play more? (y/n): ");
                String playAgain = scanner.next();
                if (!playAgain.equalsIgnoreCase("y")) {
                    break;
                }
            }

            System.out.println("Final balance: " + getUserBalance(userId));
        } catch (Exception e) {
            System.out.println("Error occurred during the game: " + e.getMessage());
        }
    }
    private static void deposit(User user, int finalBalance) {
        try {
            System.out.print("Enter deposit amount: ");
            int depositAmount = scanner.nextInt();
            if (depositAmount > 0) {
                int newBalance = depositAmount + finalBalance;
                user.setBalance(newBalance);
                setUserBalance(user.getId(), newBalance);
                System.out.println("Deposit successful. Your new balance is: " + newBalance);
            } else {
                System.out.println("Invalid deposit amount. Please enter a positive value.");
            }
        } catch (InputMismatchException e) {
            System.out.println("Invalid input. Please enter a number.");
            scanner.next();
        } catch (SQLException e) {
            System.out.println("Failed to update balance in the database.");
        }
    }
}
