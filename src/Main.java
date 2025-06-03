
import java.util.Random;
import java.util.Scanner;
import java.util.InputMismatchException;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

enum AttackType {
    SCAN("Scan", "Basic scanning attack"),
    PATCH("Patch", "Defensive move that reduces incoming damage"),
    FIREWALL("Firewall", "Increases defense temporarily"),
    ANTIVIRUS("Antivirus", "Strong attack with chance to remove enemy buffs"),
    ENCRYPTION("Encryption", "Defensive move that prevents damage"),
    BACKDOOR("Backdoor", "Bypass enemy defenses"),
    DDOS("DDoS", "Multiple small attacks");

    private final String name;
    private final String description;

    AttackType(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() { return name; }
    public String getDescription() { return description; }
}

enum ItemType {
    HEALTH("Health", "Restores health points"),
    DEFENSE("Defense", "Temporarily increases defense"),
    ATTACK("Attack", "Temporarily increases attack power"),
    UTILITY("Utility", "Special effects");

    private final String name;
    private final String description;

    ItemType(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() { return name; }
    public String getDescription() { return description; }
}

class Item {
    private String name;
    private String description;
    private ItemType type;
    private int power;
    private int uses;
    private int price;
    private boolean consumable;

    public Item(String name, String description, ItemType type, int power, int uses, int price, boolean consumable) {
        this.name = name;
        this.description = description;
        this.type = type;
        this.power = power;
        this.uses = uses;
        this.price = price;
        this.consumable = consumable;
    }

    public void use(Player player) {
        if (uses <= 0) return;

        switch (type) {
            case HEALTH:
                player.heal(power);
                System.out.println("Restored " + power + " health points!");
                break;
            case DEFENSE:
                System.out.println("Defense increased by " + power + " for the next battle!");
                break;
            case ATTACK:
                System.out.println("Attack increased by " + power + " for the next battle!");
                break;
            case UTILITY:
                System.out.println("Used " + name + "!");
                break;
        }

        if (consumable) {
            uses--;
        }
    }

    public String getName() { return name; }
    public String getDescription() { return description; }
    public ItemType getType() { return type; }
    public int getPower() { return power; }
    public int getUses() { return uses; }
    public int getPrice() { return price; }
    public boolean isConsumable() { return consumable; }
}

class ItemFactory {
    private static final Item[] COMMON_ITEMS = {
            new Item("Small Health Pack", "Restores 20 HP", ItemType.HEALTH, 20, 1, 50, true),
            new Item("Medium Health Pack", "Restores 50 HP", ItemType.HEALTH, 50, 1, 100, true),
            new Item("Firewall Boost", "Temporarily increases defense", ItemType.DEFENSE, 5, 1, 75, true),
            new Item("Virus Scanner", "Increases attack power", ItemType.ATTACK, 10, 1, 100, true)
    };

    private static final Item[] RARE_ITEMS = {
            new Item("Large Health Pack", "Restores 100 HP", ItemType.HEALTH, 100, 1, 200, true),
            new Item("Advanced Firewall", "Greatly increases defense", ItemType.DEFENSE, 15, 1, 250, true),
            new Item("Premium Antivirus", "Greatly increases attack", ItemType.ATTACK, 25, 1, 300, true)
    };

    public static Item createRandomItem(int playerLevel) {
        Random random = new Random();

        boolean isRare = random.nextDouble() < (0.1 * playerLevel);

        Item[] sourceArray = isRare ? RARE_ITEMS : COMMON_ITEMS;
        return sourceArray[random.nextInt(sourceArray.length)];
    }

    public static List<Item> getShopItems(int playerLevel) {
        List<Item> items = new ArrayList<>();
        Random random = new Random();

        items.add(COMMON_ITEMS[0]);

        int itemCount = random.nextInt(3) + 2;
        for (int i = 0; i < itemCount; i++) {
            items.add(createRandomItem(playerLevel));
        }

        return items;
    }
}

class Ability {
    private String name;
    private String description;
    private AttackType type;
    private int power;
    private int cooldown;
    private int currentCooldown;
    private int levelRequired;

    public Ability(String name, String description, AttackType type, int power, int cooldown, int levelRequired) {
        this.name = name;
        this.description = description;
        this.type = type;
        this.power = power;
        this.cooldown = cooldown;
        this.currentCooldown = 0;
        this.levelRequired = levelRequired;
    }

    public boolean isReady() {
        return currentCooldown == 0;
    }

    public void use() {
        currentCooldown = cooldown;
    }

    public void updateCooldown() {
        if (currentCooldown > 0) currentCooldown--;
    }

    public String getName() { return name; }
    public String getDescription() { return description; }
    public AttackType getType() { return type; }
    public int getPower() { return power; }
    public int getLevelRequired() { return levelRequired; }
    public int getCurrentCooldown() { return currentCooldown; }
}

class Player {
    private int health;
    private int maxHealth;
    private int level;
    private int experience;
    private String operatingSystem;
    private Weapon currentWeapon;
    private int baseDefense;
    private List<Ability> abilities;
    private List<Item> inventory;
    private int money;
    private int experienceToNextLevel;
    private List<String> relics;

    public Player() {
        this.health = 100;
        this.maxHealth = 100;
        this.level = 1;
        this.experience = 0;
        this.experienceToNextLevel = 100;
        this.operatingSystem = "Linux";
        this.currentWeapon = new Weapon("Basic Antivirus", 10, DefenseType.FIREWALL, 100, 1);
        this.baseDefense = 8;
        this.abilities = new ArrayList<>();
        this.inventory = new ArrayList<>();
        this.money = 100;
        this.relics = new ArrayList<>();
        initializeStartingAbilities();
    }

    private void initializeStartingAbilities() {
        abilities.add(new Ability("Scan", "Basic scanning attack", AttackType.SCAN, 5, 2, 1));
        abilities.add(new Ability("Patch", "Defensive move that reduces incoming damage", AttackType.PATCH, 3, 3, 1));
    }

    public void levelUp(Scanner scanner) {
        this.experience -= this.experienceToNextLevel;
        this.experienceToNextLevel = (int)(this.experienceToNextLevel * 1.2);

        level++;
        int oldMaxHealth = maxHealth;
        int oldDamage = currentWeapon.getPower();

        int damageIncrease = 3 + (level / 3);
        int healthIncrease = 20 + (level * 2);

        currentWeapon = new Weapon(currentWeapon.getName(),
                currentWeapon.getPower() + damageIncrease,
                currentWeapon.getDefenseType(),
                currentWeapon.getDurability(),
                currentWeapon.getLevel());

        maxHealth += healthIncrease;
        health = maxHealth;
        baseDefense += 2;

        unlockAbilities();

        System.out.println(ConsoleColors.GREEN +
                "╔══════════════════════════════════════╗\n" +
                "║            LEVEL UP!                 ║\n" +
                "║ You are now level " + String.format("%2d", level) + "               ║\n" +
                "║ Health restored to full!             ║\n" +
                "╚══════════════════════════════════════╝" + ConsoleColors.RESET);

        System.out.println(ConsoleColors.YELLOW +
                "╔══════════════════════════════════════╗\n" +
                "║         UPGRADES INCOMING!           ║\n" +
                "║ Damage increased by +" + String.format("%2d", damageIncrease) + "              ║\n" +
                "║ Max Health increased by +" + String.format("%2d", healthIncrease) + "         ║\n" +
                "║ Defense increased by +2              ║\n" +
                "╚══════════════════════════════════════╝" + ConsoleColors.RESET);

        System.out.println("New Stats:");
        System.out.println("- Damage: " + oldDamage + " → " + currentWeapon.getPower());
        System.out.println("- Max Health: " + oldMaxHealth + " → " + maxHealth);
        System.out.println("- Base Defense: " + (baseDefense - 2) + " → " + baseDefense);
        System.out.println("- Next level requires: " + experienceToNextLevel + " experience");

        sleep(3000);
    }

    private void unlockAbilities() {
        if (level == 2) {
            abilities.add(new Ability("Firewall", "Increases defense temporarily", AttackType.FIREWALL, 0, 4, 2));
        } else if (level == 3) {
            abilities.add(new Ability("Antivirus", "Strong attack with chance to remove enemy buffs", AttackType.ANTIVIRUS, 15, 5, 3));
        } else if (level == 5) {
            abilities.add(new Ability("Encryption", "Defensive move that prevents damage", AttackType.ENCRYPTION, 0, 6, 5));
        }
    }

    public List<Ability> getAvailableAbilities() {
        return abilities.stream()
                .filter(ability -> ability.getLevelRequired() <= level && ability.isReady())
                .collect(Collectors.toList());
    }

    public void addItem(Item item) {
        inventory.add(item);
    }

    public boolean useItem(Item item) {
        if (inventory.contains(item)) {
            item.use(this);
            if (item.isConsumable()) {
                inventory.remove(item);
            }
            return true;
        }
        return false;
    }

    public void addMoney(int amount) {
        this.money += amount;
    }

    public boolean spendMoney(int amount) {
        if (money >= amount) {
            money -= amount;
            return true;
        }
        return false;
    }

    public void showInventory() {
        System.out.println("=== Inventory ===");
        for (int i = 0; i < inventory.size(); i++) {
            Item item = inventory.get(i);
            System.out.printf("%d. %s (Uses: %d)%n", i + 1, item.getName(), item.getUses());
        }
        System.out.println("===============");
        System.out.println("0. Cancel");
        System.out.print("Choose item to use (0 to cancel): ");
    }

    public void addExperience(int exp) {
        this.experience += exp;
        System.out.println("Gained " + exp + " experience! (" + this.experience + "/" + this.experienceToNextLevel + ")");

        while (this.experience >= this.experienceToNextLevel) {
            break;
        }
    }

    public boolean shouldLevelUp() {
        return experience >= experienceToNextLevel;
    }

    public void addRelic(String relic) {
        relics.add(relic);
    }

    public void setCurrentWeapon(Weapon weapon) {
        this.currentWeapon = weapon;
    }

    private int getValidInput(Scanner scanner, int min, int max) {
        while (true) {
            try {
                int input = scanner.nextInt();
                if (input >= min && input <= max) {
                    return input;
                }
                System.out.printf("Please enter a number between %d and %d: ", min, max);
            } catch (Exception e) {
                System.out.print("Please enter a valid number: ");
                scanner.nextLine();
            }
        }
    }

    private void sleep(int milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public int getMoney() { return money; }
    public List<Item> getInventory() { return new ArrayList<>(inventory); }
    public int getMaxHealth() { return maxHealth; }
    public int getHealth() { return health; }
    public int getLevel() { return level; }
    public Weapon getCurrentWeapon() { return currentWeapon; }
    public int getBaseDefense() { return baseDefense; }
    public int getExperience() { return experience; }
    public int getExperienceToNextLevel() { return experienceToNextLevel; }
    public List<String> getRelics() { return new ArrayList<>(relics); }

    public void heal(int amount) {
        this.health = Math.min(maxHealth, health + amount);
    }

    public void takeDamage(int damage) {
        this.health -= damage;
    }
}

class Weapon {
    private String name;
    private int power;
    private DefenseType defenseType;
    private int durability;
    private int level;
    private WeaponUpgrade upgrade;
    private List<String> specialMoves;

    public Weapon(String name, int power, DefenseType defenseType, int durability, int level) {
        this.name = name;
        this.power = power;
        this.defenseType = defenseType;
        this.durability = durability;
        this.level = level;
        this.upgrade = null;
        this.specialMoves = initializeSpecialMoves(name);
    }

    private List<String> initializeSpecialMoves(String weaponName) {
        List<String> moves = new ArrayList<>();
        switch (weaponName) {
            case "Basic Antivirus" -> {
                moves.add("Virus Scan - Deals extra damage to malware");
                moves.add("Quick Patch - Heals 10 HP");
            }
            case "Advanced Firewall" -> {
                moves.add("Barrier Shield - Blocks next attack completely");
                moves.add("Traffic Filter - Reduces enemy damage for 3 turns");
            }
            case "Premium Antivirus" -> {
                moves.add("Deep Scan - Reveals enemy weaknesses");
                moves.add("Real-time Protection - Automatic counter-attacks");
            }
            case "Network Scanner" -> {
                moves.add("Port Scan - Finds enemy vulnerabilities");
                moves.add("Packet Analysis - Predicts enemy attacks");
            }
            case "Intrusion Detection" -> {
                moves.add("Honeypot - Traps enemy for one turn");
                moves.add("Alert System - Warns of incoming attacks");
            }
            case "Enterprise Firewall" -> {
                moves.add("Deep Packet Inspection - Analyzes and counters attacks");
                moves.add("Load Balancer - Distributes damage across multiple systems");
            }
            case "AI Threat Hunter" -> {
                moves.add("Behavioral Analysis - Predicts enemy patterns");
                moves.add("Adaptive Response - Evolves defenses in real-time");
            }
        }
        return moves;
    }

    public void addUpgrade(WeaponUpgrade newUpgrade) {
        this.upgrade = newUpgrade;
        this.power += newUpgrade.getBonusDamage();
    }

    public int getTotalPower() {
        return upgrade != null ? power + upgrade.getBonusDamage() : power;
    }

    public String getName() { return name; }
    public int getPower() { return power; }
    public DefenseType getDefenseType() { return defenseType; }
    public int getDurability() { return durability; }
    public int getLevel() { return level; }
    public WeaponUpgrade getUpgrade() { return upgrade; }
    public List<String> getSpecialMoves() { return specialMoves; }

    public void reduceDurability() {
        durability--;
    }

    public boolean isBroken() {
        return durability <= 0;
    }
}

enum WeaponUpgrade {
    ENCRYPTION("Encryption Core", "Adds encryption damage over time", 15),
    ISOLATION("Isolation Chamber", "Isolates threats, reducing their damage", 12),
    DETECTION("Advanced Detection", "Higher chance to detect threat weaknesses", 18),
    QUARANTINE("Quarantine System", "Can quarantine threats temporarily", 14),
    HEURISTIC("Heuristic Analysis", "Adapts to threat patterns", 16);

    private final String name;
    private final String description;
    private final int bonusDamage;

    WeaponUpgrade(String name, String description, int bonusDamage) {
        this.name = name;
        this.description = description;
        this.bonusDamage = bonusDamage;
    }

    public String getName() { return name; }
    public String getDescription() { return description; }
    public int getBonusDamage() { return bonusDamage; }
}

enum ThreatType {
    PHISHING("Phishing Email",
            "A deceptive attempt to steal sensitive information by masquerading as a trusted entity.",
            "attempts to steal credentials",
            60, 12),
    MALWARE("Malware",
            "Malicious software designed to corrupt files and steal data silently.",
            "executes malicious code",
            70, 13),
    SQL_INJECTION("SQL Injection",
            "An attack that attempts to manipulate your database by injecting malicious SQL code.",
            "injects malicious SQL commands",
            65, 13),
    DOS("DoS Attack",
            "Denial of Service attack that overwhelms your system with traffic.",
            "floods the network",
            100, 15),
    XSS("Cross-Site Script",
            "Malicious script that hijacks user sessions and steals cookies.",
            "injects malicious scripts",
            65, 13),
    MITM("Man in the Middle",
            "Secretly intercepts and alters communications between systems.",
            "intercepts network traffic",
            75, 14),
    ZERO_DAY("Zero Day Exploit",
            "A previously unknown vulnerability that leaves your system exposed.",
            "exploits unknown vulnerability",
            100, 12),
    ROOTKIT("Rootkit",
            "Advanced malware that hides deep in your system to maintain unauthorized access.",
            "attempts to gain root access",
            80, 15),
    RANSOMWARE("Ransomware",
            "Malicious software that encrypts your files and demands payment.",
            "starts encrypting files",
            100, 15),
    SOCIAL_ENGINEERING("Social Engineering",
            "Psychological manipulation techniques to trick users into security mistakes.",
            "attempts social manipulation",
            50, 13),
    WEAK_AUTHENTICATION("Weak Authentication",
            "Exploits weak passwords and authentication mechanisms.",
            "attempts password cracking",
            55, 12),
    INSIDER_THREAT("Insider Threat",
            "A privileged user attempting to misuse their access.",
            "misuses system access",
            85, 14);

    private final String name;
    private final String description;
    private final String attackMessage;
    private final int baseHealth;
    private final int baseDamage;

    ThreatType(String name, String description, String attackMessage, int baseHealth, int baseDamage) {
        this.name = name;
        this.description = description;
        this.attackMessage = attackMessage;
        this.baseHealth = baseHealth;
        this.baseDamage = baseDamage;
    }

    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getAttackMessage() { return attackMessage; }
    public int getBaseHealth() { return baseHealth; }
    public int getBaseDamage() { return baseDamage; }
}


class ThreatFactory {
    private static final Random random = new Random();

    public static Threat createThreat(int playerLevel) {
        ThreatType[] types = ThreatType.values();
        ThreatType selectedType = types[random.nextInt(types.length)];

        int health = (int)(selectedType.getBaseHealth() * (1 + 0.1 * playerLevel));
        int damage = (int)(selectedType.getBaseDamage() * (1 + 0.1 * playerLevel));

        return new Threat(
                selectedType.getName(),
                health,
                damage,
                calculateExperienceValue(playerLevel, selectedType),
                selectedType
        );
    }

    private static int calculateExperienceValue(int playerLevel, ThreatType type) {
        return 20 + (playerLevel * 5) + (type.getBaseDamage() * 3);
    }
}


class Threat {
    private String name;
    private int health;
    private int damage;
    private int experienceValue;
    private ThreatType threatType;

    public Threat(String name, int health, int damage, int experienceValue, ThreatType threatType) {
        this.name = name;
        this.health = health;
        this.damage = damage;
        this.experienceValue = experienceValue;
        this.threatType = threatType;
    }

    public String getDetailedDescription() {
        return String.format("""
            ╔══════════════════════════════════════╗
            ║         THREAT INFORMATION           ║
            ║ Name: %s
            ║ Health: %d
            ║ Damage: %d
            ║ 
            ║ %s
            ╚══════════════════════════════════════╝""",
                name,
                health,
                damage,
                threatType.getDescription());
    }


    public String getName() {
        return name;
    }

    public int getDamage() {
        return damage;
    }

    public int getHealth() {
        return health;
    }

    public int getExperienceValue() {
        return experienceValue;
    }

    public ThreatType getThreatType() {
        return threatType;
    }

    public void takeDamage(int damage) {
        this.health -= damage;
    }
}

class Game {
    private Player player;
    private Scanner scanner;
    private Shop shop;
    private boolean gameRunning;
    private Random random;
    private int encounterCount;

    public Game() {
        this.player = new Player();
        this.scanner = new Scanner(System.in);
        this.shop = new Shop();
        this.gameRunning = true;
        this.random = new Random();
        this.encounterCount = 0;
    }

    public void start() {
        System.out.println("Welcome to Cyber Defense: Roguelike!");
        System.out.println("Defend your system against increasingly dangerous cyber threats.");

        while (gameRunning && player.getHealth() > 0) {
            showStatus();
            encounterCount++;

            Threat threat;
            boolean isBoss = (encounterCount % 10 == 0);

            if (isBoss) {
                threat = createBossThreat();
                bossEncounter(threat);
            } else {
                threat = ThreatFactory.createThreat(player.getLevel());
                combat(threat);
            }

            if (player.getHealth() > 0) {
                while (player.shouldLevelUp()) {
                    player.levelUp(scanner);
                }

                afterCombat();

                if (encounterCount % 5 == 0 && !isBoss) {
                    visitShop();
                } else if (random.nextInt(100) < 20) {
                    visitShop();
                }
            }
        }

        if (player.getHealth() <= 0) {
            System.out.println("\n" + ConsoleColors.RED + "GAME OVER!" + ConsoleColors.RESET);
            System.out.println("Your system has been compromised!");
        }
    }

    private void showStatus() {
        System.out.println(ConsoleColors.CYAN + """
            ╔══════════════════════════════════════╗
            ║              STATUS                  ║""" + ConsoleColors.RESET);
        System.out.printf("║ Health: %d/%d              %n", player.getHealth(), player.getMaxHealth());
        System.out.printf("║ Level: %d (EXP: %d/%d)          %n", player.getLevel(), player.getExperience(), player.getExperienceToNextLevel());
        System.out.printf("║ Credits: %d                    %n", player.getMoney());
        System.out.printf("║ Weapon: %s                %n", player.getCurrentWeapon().getName());
        System.out.println(ConsoleColors.CYAN + "╚══════════════════════════════════════╝" + ConsoleColors.RESET);
    }

    private Threat createBossThreat() {
        ThreatType[] bossTypes = {ThreatType.ZERO_DAY, ThreatType.RANSOMWARE, ThreatType.ROOTKIT};
        ThreatType selectedType = bossTypes[random.nextInt(bossTypes.length)];

        int health = (int)(selectedType.getBaseHealth() * 2 * (1 + 0.2 * player.getLevel()));
        int damage = (int)(selectedType.getBaseDamage() * 1.5 * (1 + 0.15 * player.getLevel()));
        int expValue = selectedType.getBaseDamage() * 10 + (player.getLevel() * 20);

        return new Threat("BOSS: " + selectedType.getName(), health, damage, expValue, selectedType);
    }

    private void bossEncounter(Threat boss) {
        System.out.println("\n" + ConsoleColors.PURPLE + "🔥 BOSS ENCOUNTER! 🔥" + ConsoleColors.RESET);
        System.out.println(ConsoleColors.PURPLE + boss.getName() + " has appeared!" + ConsoleColors.RESET);
        sleep(3000);

        bossCombat(boss);

        if (boss.getHealth() <= 0) {
            showBossVictory(boss);
            giveRelic();
            chooseWeaponUpgrade();
        }
    }

    private void combat(Threat threat) {
        System.out.println("\n" + ConsoleColors.RED + "⚠ Alert! " + threat.getName() + " detected! ⚠" + ConsoleColors.RESET);
        sleep(3000);

        while (threat.getHealth() > 0 && player.getHealth() > 0) {
            clearScreen();
            showCombatInterface(threat);
            int choice = getValidInput(1, 4);
            handleCombatAction(choice, threat);
        }

        if (threat.getHealth() <= 0) {
            showVictory(threat);
        }
    }

    private void bossCombat(Threat threat) {
        while (threat.getHealth() > 0 && player.getHealth() > 0) {
            clearScreen();
            showBossCombatInterface(threat);
            int choice = getValidInput(1, 4);
            handleCombatAction(choice, threat);
        }
    }

    private void showCombatInterface(Threat threat) {
        System.out.println(ConsoleColors.RED + """
            ╔══════════════════════════════════════╗
            ║              COMBAT                  ║""" + ConsoleColors.RESET);
        System.out.printf("║ Threat: %s              %n", threat.getName());
        System.out.printf("║ Threat HP: %d                %n", threat.getHealth());
        System.out.printf("║ Your HP: %d                  %n", player.getHealth());
        System.out.println(ConsoleColors.RED + "╚══════════════════════════════════════╝" + ConsoleColors.RESET);

        System.out.println(ConsoleColors.GREEN + """
            ╔══════════════════════════════════════╗
            ║            ACTIONS                   ║
            ║ 1. Attack                           ║
            ║ 2. Use Item                         ║
            ║ 3. Block                            ║
            ║ 4. Show Stats                       ║
            ╚══════════════════════════════════════╝""" + ConsoleColors.RESET);
    }

    private void showBossCombatInterface(Threat threat) {
        System.out.println(ConsoleColors.PURPLE + """
            ╔══════════════════════════════════════╗
            ║            BOSS COMBAT               ║""" + ConsoleColors.RESET);
        System.out.printf("║ Boss: %s              %n", threat.getName());
        System.out.printf("║ Boss HP: %d                %n", threat.getHealth());
        System.out.printf("║ Your HP: %d                  %n", player.getHealth());
        System.out.println(ConsoleColors.PURPLE + "╚══════════════════════════════════════╝" + ConsoleColors.RESET);

        System.out.println(ConsoleColors.GREEN + """
            ╔══════════════════════════════════════╗
            ║            ACTIONS                   ║
            ║ 1. Attack                           ║
            ║ 2. Use Item                         ║
            ║ 3. Block                            ║
            ║ 4. Show Stats                       ║
            ╚══════════════════════════════════════╝""" + ConsoleColors.RESET);
    }

    private void handleCombatAction(int choice, Threat threat) {
        switch (choice) {
            case 1 -> attack(threat);
            case 2 -> useItem();
            case 3 -> block(threat);
            case 4 -> showDetailedStats();
        }
    }

    private void attack(Threat threat) {
        int damageDealt = player.getCurrentWeapon().getPower();
        threat.takeDamage(damageDealt);

        int damageTaken = 0;
        if (threat.getHealth() > 0) {
            damageTaken = Math.max(0, threat.getDamage() - player.getBaseDefense());
            player.takeDamage(damageTaken);
        }

        showCombatAction(threat, damageDealt, damageTaken);
        sleep(1500);
    }

    private void showCombatAction(Threat threat, int damageDealt, int damageTaken) {
        String color = threat.getName().startsWith("BOSS:") ? ConsoleColors.PURPLE : ConsoleColors.RED;

        System.out.println(color + """
            ╔══════════════════════════════════════╗
            ║           COMBAT ACTION              ║""" + ConsoleColors.RESET);

        if (damageDealt > 0) {
            System.out.printf("║ You deal %d damage!%n", damageDealt);
        }

        if (damageTaken > 0) {
            System.out.printf("║ %s %s!%n", threat.getName(), threat.getThreatType().getAttackMessage());
            System.out.printf("║ You take %d damage!%n", damageTaken);
        }

        System.out.println(color + "╚══════════════════════════════════════╝" + ConsoleColors.RESET);
    }

    private void useItem() {
        if (player.getInventory().isEmpty()) {
            System.out.println("No items in inventory!");
            sleep(3000);
            return;
        }

        player.showInventory();
        int choice = getValidInput(0, player.getInventory().size());

        if (choice > 0) {
            Item item = player.getInventory().get(choice - 1);
            player.useItem(item);
        }
    }

    private void block(Threat threat) {
        System.out.println("You prepare to block the incoming attack!");
        int reducedDamage = Math.max(0, (threat.getDamage() / 2) - player.getBaseDefense());
        player.takeDamage(reducedDamage);
        System.out.printf("You blocked some damage! Took %d damage instead.%n", reducedDamage);
        sleep(2000);
    }

    private void showDetailedStats() {
        System.out.println(ConsoleColors.CYAN + """
            ╔══════════════════════════════════════╗
            ║          DETAILED STATUS             ║""" + ConsoleColors.RESET);
        System.out.printf("║ Health: %d/%d              %n", player.getHealth(), player.getMaxHealth());
        System.out.printf("║ Level: %d                      %n", player.getLevel());
        System.out.printf("║ Experience: %d/%d              %n", player.getExperience(), player.getExperienceToNextLevel());
        System.out.printf("║ Credits: %d                    %n", player.getMoney());
        System.out.printf("║ Weapon: %s (%d DMG)           %n", player.getCurrentWeapon().getName(), player.getCurrentWeapon().getPower());
        System.out.printf("║ Base Defense: %d               %n", player.getBaseDefense());
        System.out.println(ConsoleColors.CYAN + "╚══════════════════════════════════════╝" + ConsoleColors.RESET);
        sleep(5000);
    }

    private void showVictory(Threat threat) {
        System.out.println(ConsoleColors.GREEN + """
            ╔══════════════════════════════════════╗
            ║         THREAT ELIMINATED!           ║""" + ConsoleColors.RESET);
        System.out.printf("║ Experience gained: %d           %n", threat.getExperienceValue());

        int creditsFound = 10 + random.nextInt(60);
        player.addMoney(creditsFound);
        System.out.printf("║ Credits found: %d              %n", creditsFound);

        if (random.nextInt(100) < 25) {
            Item droppedItem = ItemFactory.createRandomItem(player.getLevel());
            player.addItem(droppedItem);
            System.out.printf("║ Item found: %s              %n", droppedItem.getName());
        }

        System.out.println(ConsoleColors.GREEN + "╚══════════════════════════════════════╝" + ConsoleColors.RESET);

        player.addExperience(threat.getExperienceValue());
        sleep(3000);
    }

    private void showBossVictory(Threat boss) {
        System.out.println(ConsoleColors.PURPLE + """
            ╔══════════════════════════════════════╗
            ║          BOSS DEFEATED!              ║""" + ConsoleColors.RESET);
        System.out.printf("║ Experience gained: %d           %n", boss.getExperienceValue());

        int creditsFound = 50 + random.nextInt(100);
        player.addMoney(creditsFound);
        System.out.printf("║ Credits found: %d              %n", creditsFound);

        System.out.println(ConsoleColors.PURPLE + "╚══════════════════════════════════════╝" + ConsoleColors.RESET);
        player.addExperience(boss.getExperienceValue());
        sleep(4000);
    }

    private void giveRelic() {
        String[] relics = {
                "Data Encryption Relic - All attacks deal +10 damage",
                "System Backup Relic - Restore 20 HP after each fight",
                "Network Shield Relic - Take 25% less damage",
                "Processing Core Relic - +50% experience gain",
                "Security Protocol Relic - Start fights with temporary shield"
        };

        String chosenRelic = relics[random.nextInt(relics.length)];
        player.addRelic(chosenRelic);

        System.out.println(ConsoleColors.GOLD + """
            ╔══════════════════════════════════════╗
            ║           RELIC ACQUIRED!            ║""" + ConsoleColors.RESET);
        System.out.printf("║ %s%n", chosenRelic);
        System.out.println(ConsoleColors.GOLD + "╚══════════════════════════════════════╝" + ConsoleColors.RESET);
        sleep(2000);
    }

    private void chooseWeaponUpgrade() {
        WeaponUpgrade[] upgrades = WeaponUpgrade.values();
        WeaponUpgrade[] choices = new WeaponUpgrade[3];

        for (int i = 0; i < 3; i++) {
            choices[i] = upgrades[random.nextInt(upgrades.length)];
        }

        System.out.println(ConsoleColors.CYAN + """
            ╔══════════════════════════════════════╗
            ║        CHOOSE WEAPON UPGRADE         ║""" + ConsoleColors.RESET);

        for (int i = 0; i < 3; i++) {
            System.out.printf("║ %d. %s (+%d DMG)%n", i + 1, choices[i].getName(), choices[i].getBonusDamage());
            System.out.printf("║    %s%n", choices[i].getDescription());
        }

        System.out.println(ConsoleColors.CYAN + "╚══════════════════════════════════════╝" + ConsoleColors.RESET);

        System.out.print("Choose upgrade (1-3): ");
        int choice = getValidInput(1, 3);

        player.getCurrentWeapon().addUpgrade(choices[choice - 1]);
        System.out.printf("Weapon upgraded with %s!%n", choices[choice - 1].getName());
        sleep(2000);
    }


    private void afterCombat() {
        int healAmount = random.nextInt(35) + 5;
        player.heal(healAmount);
        System.out.printf("You've recovered some health! Current HP: %d%n", player.getHealth());
        sleep(1000);
    }

    private void visitShop() {
        System.out.println("\nA traveling merchant appears!");
        shop.enterShop(player, scanner);
    }

    private int getValidInput(int min, int max) {
        while (true) {
            try {
                int input = scanner.nextInt();
                if (input >= min && input <= max) {
                    return input;
                }
                System.out.printf("Please enter a number between %d and %d: ", min, max);
            } catch (InputMismatchException e) {
                System.out.print("Please enter a valid number: ");
                scanner.nextLine();
            }
        }
    }

    private void sleep(int milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void clearScreen() {
        for (int i = 0; i < 50; i++) {
            System.out.println();
        }
    }
}


class Shop {
    private List<Item> inventory;
    private List<Weapon> weaponInventory;
    private Random random = new Random();

    public Shop() {
        this.inventory = new ArrayList<>();
        this.weaponInventory = new ArrayList<>();
        generateInventory();
    }

    private void generateInventory() {
        inventory.addAll(ItemFactory.getShopItems(1));

        weaponInventory.add(new Weapon("Advanced Firewall", 18, DefenseType.FIREWALL, 150, 2));
        weaponInventory.add(new Weapon("Premium Antivirus", 22, DefenseType.ANTIVIRUS, 120, 2));
        weaponInventory.add(new Weapon("Enterprise Firewall", 30, DefenseType.FIREWALL, 200, 4));
    }

    public void enterShop(Player player, Scanner scanner) {
        boolean shopping = true;

        while (shopping) {
            showShopInterface(player);
            System.out.print("Enter the number of the item to purchase (0 to exit): ");

            try {
                int choice = scanner.nextInt();
                if (choice == 0) {
                    shopping = false;
                } else if (choice <= inventory.size()) {
                    Item item = inventory.get(choice - 1);
                    if (player.getMoney() >= item.getPrice()) {
                        player.spendMoney(item.getPrice());
                        player.addItem(item);
                        System.out.println("Purchased " + item.getName() + "!");
                    } else {
                        System.out.println("Not enough credits!");
                    }
                } else if (choice <= inventory.size() + weaponInventory.size()) {
                    Weapon weapon = weaponInventory.get(choice - inventory.size() - 1);
                    int weaponPrice = weapon.getPower() * 15;
                    if (player.getMoney() >= weaponPrice && weapon.getLevel() <= player.getLevel()) {
                        player.spendMoney(weaponPrice);
                        player.setCurrentWeapon(weapon);
                        System.out.println("Purchased " + weapon.getName() + "!");
                        System.out.println("Special moves available:");
                        for (String move : weapon.getSpecialMoves()) {
                            System.out.println("  " + move);
                        }
                    } else if (weapon.getLevel() > player.getLevel()) {
                        System.out.println("You need to be level " + weapon.getLevel() + " to use this weapon!");
                    } else {
                        System.out.println("Not enough credits!");
                    }
                } else {
                    System.out.println("Invalid choice!");
                }
                sleep(1500);
            } catch (Exception e) {
                System.out.println("Invalid input!");
                scanner.nextLine();
            }
        }
    }

    private void showShopInterface(Player player) {
        System.out.println(ConsoleColors.CYAN + """
            ╔══════════════════════════════════════╗
            ║           SHOP INVENTORY             ║""" + ConsoleColors.RESET);

        for (int i = 0; i < inventory.size(); i++) {
            Item item = inventory.get(i);
            System.out.printf("║ %d. %-20s %d credits %n", i + 1, item.getName(), item.getPrice());
            System.out.printf("║    %s%n", item.getDescription());
        }

        for (int i = 0; i < weaponInventory.size(); i++) {
            Weapon weapon = weaponInventory.get(i);
            int price = weapon.getPower() * 15;
            String levelReq = weapon.getLevel() > player.getLevel() ? " (Req: Lvl " + weapon.getLevel() + ")" : "";
            System.out.printf("║ %d. %-20s %d credits%s %n",
                    inventory.size() + i + 1, weapon.getName(), price, levelReq);
            System.out.printf("║    %d DMG, Level %d weapon%n", weapon.getPower(), weapon.getLevel());
        }

        System.out.println("║ 0. Exit Shop");
        System.out.println(ConsoleColors.CYAN + "╚══════════════════════════════════════╝" + ConsoleColors.RESET);
        System.out.println("Your credits: " + player.getMoney());
    }

    private void sleep(int milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
enum DefenseType {
    FIREWALL("Firewall", "Reduces incoming damage", 3),
    ANTIVIRUS("Antivirus", "Deals additional damage to threats", 5),
    IDS("Intrusion Detection", "Chance to prevent enemy special abilities", 4),
    ENCRYPTION("Encryption", "Protects against data theft attacks", 5),
    BACKUP("Backup System", "Recovers some HP after battle", 4),
    ACCESS_CONTROL("Access Control", "Reduces enemy critical hit chance", 3),
    PATCH_MANAGEMENT("Patch Management", "Increases defense against zero-day attacks", 5),
    MONITORING("System Monitoring", "Reveals enemy weaknesses", 4),
    AUTHENTICATION("Authentication", "Blocks certain enemy abilities", 4),
    TRAINING("Security Training", "Increases experience gain", 3);

    private final String name;
    private final String description;
    private final int powerLevel;

    DefenseType(String name, String description, int powerLevel) {
        this.name = name;
        this.description = description;
        this.powerLevel = powerLevel;
    }

    public String getName() { return name; }
    public String getDescription() { return description; }
    public int getPowerLevel() { return powerLevel; }
}

class ConsoleColors {
    public static final String RESET = "\033[0m";
    public static final String RED = "\033[31m";
    public static final String GREEN = "\033[32m";
    public static final String YELLOW = "\033[33m";
    public static final String BLUE = "\033[34m";
    public static final String PURPLE = "\033[35m";
    public static final String CYAN = "\033[36m";
    public static final String WHITE = "\033[37m";
    public static final String GOLD = "\033[93m";
}

public class Main {
    public static void main(String[] args) {
        Game game = new Game();
        game.start();
    }
}
