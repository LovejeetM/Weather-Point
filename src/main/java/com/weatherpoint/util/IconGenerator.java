package com.weatherpoint.util;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

public class IconGenerator {


    public static void main(String[] args) {
        generatePlaceholderIcons();
         JOptionPane.showMessageDialog(null, "Placeholder icons generated in src/main/resources/icons", "Icon Generator", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void generatePlaceholderIcons() {

        String[] iconNames = {"profile", "itinerary", "location", "alert", "settings", "logout",
                         "temperature", "condition", "humidity", "wind"};

        File iconDir = new File("src/main/resources/icons");
        if (!iconDir.exists()) {
            boolean created = iconDir.mkdirs();
             if (!created) {
                 System.err.println("Error: Could not create icon directory: " + iconDir.getAbsolutePath());
                 return;
             }
        }
        if (!iconDir.isDirectory()) {
            System.err.println("Error: Path exists but is not a directory: " + iconDir.getAbsolutePath());
            return;
        }


        int size = 20;
        for (String name : iconNames) {
            try {
                BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
                Graphics2D g2d = img.createGraphics();

                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(getColorForIcon(name));
                g2d.fillRect(0, 0, size, size);
                g2d.setColor(Color.WHITE);
                g2d.setFont(new Font("SansSerif", Font.BOLD, size - 8));
                String initial = name.substring(0, 1).toUpperCase();
                FontMetrics fm = g2d.getFontMetrics();
                int x = (size - fm.stringWidth(initial)) / 2;
                int y = (fm.getAscent() + (size - (fm.getAscent() + fm.getDescent())) / 2);
                g2d.drawString(initial, x, y);

                g2d.dispose();

                File outputFile = new File(iconDir, name + ".png");
                ImageIO.write(img, "png", outputFile);
                 System.out.println("Generated: " + outputFile.getName());

            } catch (Exception e) {
                System.err.println("Error generating icon for '" + name + "': " + e.getMessage());
                e.printStackTrace();
            }
        }
         System.out.println("Icon generation complete.");
    }

    private static Color getColorForIcon(String name) {
        int hash = name.hashCode();
        switch(name) {
            case "profile": return new Color(60, 120, 180); // Blue
            case "itinerary": return new Color(200, 100, 50); // Orange
            case "location": return new Color(50, 150, 50); // Green
            case "alert": return new Color(180, 50, 50); // Red
            case "settings": return new Color(100, 100, 100); // Gray
            case "logout": return new Color(150, 80, 150); // Purple
            case "temperature": return new Color(220, 150, 0); // Yellowish
            case "condition": return new Color(0, 150, 200); // Cyan
            default: return new Color((hash & 0xFF0000) >> 16, (hash & 0x00FF00) >> 8, hash & 0x0000FF);
        }
    }
}