import java.util.ArrayList;
import java.util.List;

class Player {
    // Privatni atributi igraca
    private String name;
    private int x, y;
    private int width;
    private int height;
    private int health;

    // Konstruktor
    public Player(String name, int x, int y, int width, int height, int health) {
        setName(name);
        setX(x);
        setY(y);
        setWidth(width);
        setHeight(height);
        setHealth(health);
    }

    // Prebaca se prvo slovo imena u veliko slovo
    private static String formatName(String raw) {
        String[] words = raw.trim().split("\\s+"); // razdvajanje po jednom ili viserazmaka
        StringBuilder sb = new StringBuilder();
        for (String w : words) {
            if (sb.length() > 0) {
                sb.append(" "); // samo jedan razmak ovdje
            }
            sb.append(Character.toUpperCase(w.charAt(0))) // prvo slovo veliko
                    .append(w.substring(1).toLowerCase());
        }
        return sb.toString();
    }

    // geteri i seteri
    public String getName() {
        return name;
    }

    public void setName(String name) {
        // ime ne smije biti null ni prazno
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Ime igraca ne smije biti prazno!");
        }
        this.name = formatName(name);
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        // Provjera: health mora biti izmedju 0 i 100
        if (health < 0 || health > 100) {
            throw new IllegalArgumentException("Health mora biti izmedju 0 i 100!");
        }
        this.health = health;
    }

    // opis Player[Player1]@ (10, 5) 32x32 HP = 85
    @Override
    public String toString() {
        return "Player[" + name + "]@ (" + x + ", " + y + ") " + width + "x" + height + " HP = " + health;
    }
}

class Enemy {

    private String type;
    private int x, y;
    private int width;
    private int height;
    private int damage;

    // Konstruktor
    public Enemy(String type, int x, int y, int width, int height, int damage) {
        setType(type);
        setX(x);
        setY(y);
        setWidth(width);
        setHeight(height);
        setDamage(damage);
    }

    // getere i setere
    public String getType() {
        return type;
    }

    public void setType(String type) {

        if (type == null || type.trim().isEmpty()) {
            throw new IllegalArgumentException("Tip neprijatelja ne smije biti prazan!");
        }
        this.type = type.trim();
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getDamage() {
        return damage;
    }

    public void setDamage(int damage) {
        // Provjera: damage mora biti izmedju 0 i 100
        if (damage < 0 || damage > 100) {
            throw new IllegalArgumentException("Damage mora biti izmedju 0 i 100!");
        }
        this.damage = damage;
    }

    // Enemy[Goblin]@ (12, 5) 16x16 DMG = 20
    @Override
    public String toString() {
        return "Enemy[" + type + "]@ (" + x + ", " + y + ") " + width + "x" + height + " DMG = " + damage;
    }
}

public class Game {

    private Player player;
    private ArrayList<Enemy> enemies = new ArrayList<>();
    private ArrayList<String> eventLog = new ArrayList<>();

    public Game(Player player) {
        this.player = player;
        eventLog.add("START: Igra pokrenuta, igrac " + player.getName());
    }

    public Player getPlayer() {
        return player;
    }

    public ArrayList<Enemy> getEnemies() {
        return enemies;
    }

    public ArrayList<String> getEventLog() {
        return eventLog;

    public boolean checkCollision(Player p, Enemy e) {
        boolean overlapX = p.getX() < e.getX() + e.getWidth()
                && e.getX() < p.getX() + p.getWidth();
        boolean overlapY = p.getY() < e.getY() + e.getHeight()
                && e.getY() < p.getY() + p.getHeight();
        return overlapX && overlapY;
    }

    // Smanjuje health igraca za damage neprijatelja (ne ispod 0) i upisuje dogadjaj
    // u log
    public void decreaseHealth(Player p, Enemy e) {
        int before = p.getHealth();
        int after = Math.max(0, before - e.getDamage()); // health ne smije pasti ispod 0
        p.setHealth(after);
        eventLog.add("HIT: " + p.getName() + " by " + e.getType() + " for " + e.getDamage()
                + " HP " + before + " to " + after);
    }

    // Dodaje neprijatelja u listu i upisuje dogadjaj u log
    public void addEnemy(Enemy e) {
        enemies.add(e);
        eventLog.add("ADD: " + e);
    }

    // Vraca sve neprijatelje ciji tip sadrzi zadati tekst (case insensitive).
    public List<Enemy> findByType(String query) {
        List<Enemy> result = new ArrayList<>();
        if (query == null) {
            return result;
        }
        String q = query.trim().toLowerCase();
        for (Enemy e : enemies) {
            if (e.getType().toLowerCase().contains(q)) {
                result.add(e);
            }
        }
        return result;
    }

    // Vraca listu svih neprijatelja koji se trenutno sudaraju sa igracem
    public List<Enemy> collidingWithPlayer() {
        List<Enemy> result = new ArrayList<>();
        for (Enemy e : enemies) {
            if (checkCollision(player, e)) {
                result.add(e);
            }
        }
        return result;
    }

    // pozivanje decreaseHealth
    public void resolveCollisions() {
        for (Enemy e : collidingWithPlayer()) {
            decreaseHealth(player, e);
        }
    }

    // Pravi Enemy 
    //
    public static Enemy parseEnemy(String line) {
        String[] parts = line.split(";"); // razdvajanje na 4 dijela
        if (parts.length != 4) {
            throw new IllegalArgumentException("Neispravan format: " + line);
        }
        String type = parts[0].trim();

        String[] pos = parts[1].trim().split(","); 
        int x = Integer.parseInt(pos[0].trim());
        int y = Integer.parseInt(pos[1].trim());

        String[] size = parts[2].trim().toLowerCase().split("x");
        int width = Integer.parseInt(size[0].trim());
        int height = Integer.parseInt(size[1].trim());

        int damage = Integer.parseInt(parts[3].trim());
        return new Enemy(type, x, y, width, height, damage);
    }

    
   
    }
}
