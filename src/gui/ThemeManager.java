package gui;

import java.awt.Color;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import javax.swing.UIManager;


public class ThemeManager {
    public static final String LIGHT_THEME = "light";
    public static final String DARK_THEME = "dark";
    
    private static String currentTheme = LIGHT_THEME;
    private static final PropertyChangeSupport pcs = new PropertyChangeSupport(ThemeManager.class);
    
    private static final Map<String, Map<String, Color>> THEME_COLORS = new HashMap<>();

    static {
        // Light Theme Colors
        Map<String, Color> lightColors = new HashMap<>();
        lightColors.put("primary", new Color(126, 4, 3));
        lightColors.put("primaryDark", new Color(90, 3, 2));
        lightColors.put("primaryLight", new Color(160, 30, 30));
        lightColors.put("bgColor", new Color(240, 242, 247));
        lightColors.put("cardBg", Color.WHITE);
        lightColors.put("sidebarBg", new Color(45, 45, 55));
        lightColors.put("sidebarHover", new Color(65, 65, 80));
        lightColors.put("sidebarActive", new Color(126, 4, 3));
        lightColors.put("textPrimary", new Color(50, 50, 50));
        lightColors.put("textSecondary", new Color(130, 130, 130));
        lightColors.put("borderColor", new Color(230, 230, 230));
        lightColors.put("tableHeaderBorder", Color.BLACK);
        lightColors.put("tableText", Color.BLACK);
        THEME_COLORS.put(LIGHT_THEME, lightColors);

        // Dark Theme Colors
        Map<String, Color> darkColors = new HashMap<>();
        darkColors.put("primary", new Color(126, 4, 3));
        darkColors.put("primaryDark", new Color(90, 3, 2));
        darkColors.put("primaryLight", new Color(160, 30, 30));
        darkColors.put("bgColor", new Color(28, 28, 35));
        darkColors.put("cardBg", new Color(45, 45, 55));
        darkColors.put("sidebarBg", new Color(45, 45, 55));
        darkColors.put("sidebarHover", new Color(65, 65, 80));
        darkColors.put("sidebarActive", new Color(126, 4, 3));
        darkColors.put("textPrimary", new Color(240, 240, 250));
        darkColors.put("textSecondary", new Color(180, 180, 190));
        darkColors.put("borderColor", new Color(65, 65, 75));
        darkColors.put("tableHeaderBorder", new Color(65, 65, 75));
        darkColors.put("tableText", Color.WHITE);
        THEME_COLORS.put(DARK_THEME, darkColors);

        loadSavedTheme();
        applyTheme(currentTheme);
    }
    
    public static Color getColor(String key) {
        return THEME_COLORS.get(currentTheme).get(key);
    }
    
    // Core getters
    public static Color getPRIMARY() { return getColor("primary"); }
    public static Color getPRIMARY_DARK() { return getColor("primaryDark"); }
    public static Color getPRIMARY_LIGHT() { return getColor("primaryLight"); }
    public static Color getBG_COLOR() { return getColor("bgColor"); }
    public static Color getCARD_BG() { return getColor("cardBg"); }
    public static Color getSIDEBAR_BG() { return getColor("sidebarBg"); }
    public static Color getSIDEBAR_HOVER() { return getColor("sidebarHover"); }
    public static Color getSIDEBAR_ACTIVE() { return getColor("sidebarActive"); }
    public static Color getTEXT_PRIMARY() { return getColor("textPrimary"); }
    public static Color getTEXT_SECONDARY() { return getColor("textSecondary"); }
    public static Color getBORDER_COLOR() { return getColor("borderColor"); }
    public static Color getTABLE_HEADER_BORDER() { return getColor("tableHeaderBorder"); }
    public static Color getTABLE_TEXT() { return getColor("tableText"); }

    public static boolean isDarkMode() {
        return DARK_THEME.equals(currentTheme);
    }
    
    public static void setTheme(String theme) {
        if (!LIGHT_THEME.equals(theme) && !DARK_THEME.equals(theme)) {
            throw new IllegalArgumentException("Invalid theme: " + theme);
        }
        
        String oldTheme = currentTheme;
        currentTheme = theme;
        saveThemePreference();
        applyTheme(theme);
        pcs.firePropertyChange("theme", oldTheme, theme);
    }
    
    public static String getCurrentTheme() {
        return currentTheme;
    }
    
    public static void addPropertyChangeListener(PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(listener);
    }
    
    public static void removePropertyChangeListener(PropertyChangeListener listener) {
        pcs.removePropertyChangeListener(listener);
    }
    
    private static void applyTheme(String theme) {
        Map<String, Color> colors = THEME_COLORS.get(theme);
        
        UIManager.put("Panel.background", colors.get("cardBg"));
        UIManager.put("Panel.foreground", colors.get("textPrimary"));
        UIManager.put("Button.background", colors.get("cardBg"));
        UIManager.put("Button.foreground", colors.get("textPrimary"));
        UIManager.put("TextField.background", colors.get("cardBg"));
        UIManager.put("TextField.foreground", colors.get("textPrimary"));
        UIManager.put("Table.background", colors.get("cardBg"));
        UIManager.put("Table.foreground", colors.get("textPrimary"));
        UIManager.put("TableHeader.background", colors.get("sidebarBg"));
        UIManager.put("TableHeader.foreground", colors.get("textPrimary"));
        UIManager.put("Label.foreground", colors.get("textPrimary"));
        UIManager.put("ComboBox.background", colors.get("cardBg"));
        UIManager.put("ComboBox.foreground", colors.get("textPrimary"));
        
        // Additional for better dark theme support
        UIManager.put("List.background", colors.get("cardBg"));
        UIManager.put("List.foreground", colors.get("textPrimary"));
        UIManager.put("List.selectionBackground", colors.get("sidebarActive"));
        UIManager.put("List.selectionForeground", colors.get("textPrimary"));
        UIManager.put("ScrollPane.background", colors.get("bgColor"));
        UIManager.put("Viewport.background", colors.get("bgColor"));
        UIManager.put("Table.selectionBackground", adjustAlpha(colors.get("sidebarActive"), 0.3f));
        UIManager.put("Table.selectionForeground", colors.get("textPrimary"));
        UIManager.put("ComboBox.selectionBackground", colors.get("sidebarActive"));
        UIManager.put("ComboBox.selectionForeground", Color.WHITE);
        
        // Enhanced ComboBox theming for dropdown popup and arrow
        UIManager.put("ComboBox.buttonBackground", colors.get("sidebarBg"));
        UIManager.put("ComboBox.arrowButtonBackground", colors.get("sidebarBg"));
        UIManager.put("ComboBox.buttonDarkShadow", colors.get("borderColor"));
        UIManager.put("ComboBox.buttonHighlight", colors.get("sidebarHover"));
        UIManager.put("ComboBox.editorBorder", javax.swing.BorderFactory.createLineBorder(colors.get("borderColor")));
        UIManager.put("ComboBox.popupBackground", colors.get("cardBg"));
        UIManager.put("ComboBox.listBackground", colors.get("cardBg"));
        
        UIManager.put("TableHeader.background", colors.get("sidebarBg"));
        UIManager.put("TableHeader.foreground", colors.get("textPrimary"));
        UIManager.put("Menu.background", colors.get("sidebarBg"));
        UIManager.put("Menu.foreground", colors.get("textPrimary"));
        UIManager.put("MenuItem.background", colors.get("sidebarBg"));
        UIManager.put("MenuItem.foreground", colors.get("textPrimary"));
    }
    
    public static Color adjustAlpha(Color color, float alpha) {
        return new Color(color.getRed(), color.getGreen(), color.getBlue(), (int)(alpha * 255));
    }




    
    private static void saveThemePreference() {
        Properties props = new Properties();
        props.setProperty("theme", currentTheme);
        try (FileOutputStream fos = new FileOutputStream("bin/config/theme.properties")) {
            props.store(fos, "Theme Preference");
        } catch (IOException e) {
            System.err.println("Failed to save theme: " + e.getMessage());
        }
    }
    
    private static void loadSavedTheme() {
        Properties props = new Properties();
        try (FileInputStream fis = new FileInputStream("bin/config/theme.properties")) {
            props.load(fis);
            String savedTheme = props.getProperty("theme", LIGHT_THEME);
            if (DARK_THEME.equals(savedTheme) || LIGHT_THEME.equals(savedTheme)) {
                currentTheme = savedTheme;
            }
        } catch (IOException e) {
            // Use default
        }
    }
}

