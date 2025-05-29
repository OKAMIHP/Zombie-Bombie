//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
import java.util.Random;
import java.util.Scanner;
import java.util.InputMismatchException;

public class Main {
    public static void main(String[] args) {
        Game game = new Game();
        game.startGame();
    }
}

class Player {
    private int health;
    private int level;
    private int experience;
    private String operatingSystem;
    private Weapon currentWeapon;
    private int baseDefense;

    public Player() {
        this.health = 100;
        this.level = 1;
        this.experience = 0;
        this.operatingSystem = "Windows 10";
        this.baseDefense = 10;
        this.currentWeapon = new Weapon("Basic Antivirus", 10, "Basic protection against common threats");
    }

    public void takeDamage(int damage) {
        this.health -= damage;
    }

    public void addExperience(int exp) {
        this.experience += exp;
        if (this.experience >= 100) {
            this.level++;
            this.experience -= 100;
            System.out.println("\nLevel Up! You are now level " + this.level);
        }
    }

    public int getHealth() { return health; }
    public int getLevel() { return level; }
    public Weapon getCurrentWeapon() { return currentWeapon; }
    public int getBaseDefense() { return baseDefense; }

    public void heal(int amount) {
        this.health = Math.min(100, this.health + amount);
    }
}

class Weapon {
    private String name;
    private int power;
    private String description;

    public Weapon(String name, int power, String description) {
        this.name = name;
        this.power = power;
        this.description = description;
    }

    public String getName() { return name; }
    public int getPower() { return power; }
    public String getDescription() { return description; }
}

class Threat {
    private String name;
    private String type;
    private int damage;
    private int health;
    private int experienceValue;

    public Threat(int playerLevel) {
        generateThreat(playerLevel);
    }

    private void generateThreat(int playerLevel) {
        String[][] threats = {
                {"Phishing Email", "Social Engineering", "10"},
                {"Malware", "Virus", "15"},
                {"Ransomware", "Encryption", "20"},
                {"DDoS Attack", "Network", "25"},
                {"Zero-day Exploit", "Advanced", "30"},
                {"Whaling Attack", "Social Engineering", "40"}
        };

        int threatIndex = Math.min((playerLevel - 1) / 2, threats.length - 1);
        String[] selectedThreat = threats[threatIndex];

        this.name = selectedThreat[0];
        this.type = selectedThreat[1];
        this.damage = Integer.parseInt(selectedThreat[2]) + (playerLevel * 2);
        this.health = 50 + (playerLevel * 10);
        this.experienceValue = 20 + (playerLevel * 5);
    }

    public String getName() { return name; }
    public int getDamage() { return damage; }
    public int getHealth() { return health; }
    public int getExperienceValue() { return experienceValue; }

    public void takeDamage(int damage) {
        this.health -= damage;
    }
}

class Game {
    private Player player;
    private Random random;
    private Scanner scanner;
    private boolean gameRunning;

    public Game() {
        player = new Player();
        random = new Random();
        scanner = new Scanner(System.in);
        gameRunning = true;
    }

    public void startGame() {
        System.out.println("Welcome to Cyber Defense: Roguelike!");
        System.out.println("Defend your system against increasingly dangerous cyber threats.");

        while (gameRunning && player.getHealth() > 0) {
            showStatus();
            Threat threat = new Threat(player.getLevel());
            combat(threat);

            if (player.getHealth() <= 0) {
                System.out.println("Game Over! Your system has been compromised!");
                System.out.println("You reached level " + player.getLevel());
                break;
            }

            // After combat rewards
            if (gameRunning) {
                player.heal(20); // Heal some HP after each battle
                System.out.println("\nYou've recovered some health! Current HP: " + player.getHealth());
                offerChoice();
            }
        }
    }

    private void showStatus() {
        System.out.println("\n=== Status ===");
        System.out.println("Health: " + player.getHealth());
        System.out.println("Level: " + player.getLevel());
        System.out.println("Weapon: " + player.getCurrentWeapon().getName());
        System.out.println("============");
    }

    private void combat(Threat threat) {
        System.out.println("\nAlert! " + threat.getName() + " detected!");

        while (threat.getHealth() > 0 && player.getHealth() > 0 && gameRunning) {
            System.out.println("\nThreat HP: " + threat.getHealth());
            System.out.println("Your HP: " + player.getHealth());
            showCombatOptions();

            int choice = getValidInput(1, 2);

            switch (choice) {
                case 1:
                    // Attack
                    int damage = player.getCurrentWeapon().getPower();
                    threat.takeDamage(damage);
                    System.out.println("You deal " + damage + " damage!");
                    break;
                case 2:
                    // Run
                    if (random.nextDouble() < 0.5) {
                        System.out.println("Escaped successfully!");
                        gameRunning = false;
                        return;
                    } else {
                        System.out.println("Couldn't escape!");
                    }
                    break;
            }

            // Threat attacks back if still alive
            if (threat.getHealth() > 0) {
                int damage = Math.max(0, threat.getDamage() - player.getBaseDefense());
                player.takeDamage(damage);
                System.out.println("The " + threat.getName() + " deals " + damage + " damage!");
            } else {
                System.out.println("Threat eliminated!");
                player.addExperience(threat.getExperienceValue());
            }
        }
    }

    private void showCombatOptions() {
        System.out.println("\nChoose your action:");
        System.out.println("1. Attack");
        System.out.println("2. Try to run");
    }

    private void offerChoice() {
        System.out.println("\nDo you want to continue?");
        System.out.println("1. Continue fighting");
        System.out.println("2. Exit");

        int choice = getValidInput(1, 2);
        if (choice == 2) {
            gameRunning = false;
        }
    }

    private int getValidInput(int min, int max) {
        int choice;
        while (true) {
            try {
                choice = scanner.nextInt();
                if (choice >= min && choice <= max) {
                    return choice;
                }
                System.out.println("Please enter a number between " + min + " and " + max);
            } catch (InputMismatchException e) {
                System.out.println("Please enter a valid number");
                scanner.nextLine(); // Clear the invalid input
            }
        }
    }
}