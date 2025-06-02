package com.weatherpoint;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.function.Function;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import com.weatherpoint.model.UserPreferences;
import com.weatherpoint.model.WeatherData;
import com.weatherpoint.service.WeatherService;

public class DashboardPanel extends JPanel {

    private static final Font FONT_GENERAL = new Font("Franklin Gothic", Font.PLAIN, 14);
    private static final Font FONT_GENERAL_BOLD = new Font("Franklin Gothic", Font.BOLD, 14);
    private static final Font FONT_MEDIUM_BOLD = new Font("Franklin Gothic", Font.BOLD, 16);
    private static final Font FONT_LARGE_BOLD = new Font("Franklin Gothic", Font.BOLD, 26);
    private static final Font FONT_XLARGE_BOLD = new Font("Franklin Gothic", Font.BOLD, 48);
    private static final Font FONT_TITLE = new Font("Franklin Gothic", Font.BOLD, 28);
    private static final Font FONT_SUBTITLE = new Font("Franklin Gothic", Font.PLAIN, 16);
    private static final Font FONT_ICON_LARGE = new Font("Franklin Gothic", Font.PLAIN, 36);
    private static final Font FONT_ICON_MEDIUM = new Font("Franklin Gothic", Font.PLAIN, 20); 
    private static final Font FONT_RECOMMENDATION = new Font("Franklin Gothic", Font.PLAIN, 15); 

    private JTextField destinationField;
    private JButton searchButton;
    private JPanel weatherDisplayPanel;
    private WeatherService weatherService;
    private JComboBox<String> dateSelector;
    private JTextArea recommendationsArea;
    private JList<String> favoritesList;
    private DefaultListModel<String> favoritesModel;
    private JTabbedPane tabbedPane;
    private UserPreferences preferences;
    private JTextArea alertsArea;

    private JPanel searchPanel;
    private JPanel sideMenuPanel;
    private JPanel favoritesPanel;
    private JPanel alertsPanel;

    public DashboardPanel() {
        weatherService = new WeatherService();
        preferences = new UserPreferences();
        initializeComponents();
        setupLayout();
        setupListeners();
        loadPreferences();
        updateTheme();
    }

    private void initializeComponents() {
        destinationField = new JTextField(25);
        destinationField.setFont(FONT_GENERAL);

        searchButton = new JButton("Search");
        searchButton.setFont(FONT_GENERAL_BOLD);
        searchButton.setIcon(createScaledIcon("/icons/location.png", 18));

        dateSelector = new JComboBox<>(getNextSevenDays());
        dateSelector.setFont(FONT_GENERAL);
        dateSelector.setPreferredSize(new Dimension(180, 30));

        weatherDisplayPanel = new JPanel(new BorderLayout());

        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(FONT_GENERAL_BOLD);

        favoritesModel = new DefaultListModel<>();
        favoritesList = new JList<>(favoritesModel);
        favoritesList.setFont(FONT_GENERAL);
        favoritesList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        recommendationsArea = new JTextArea(7, 40);
        recommendationsArea.setEditable(false);
        recommendationsArea.setLineWrap(true);
        recommendationsArea.setWrapStyleWord(true);
        
        recommendationsArea.setFont(FONT_RECOMMENDATION); 
        recommendationsArea.setMargin(new Insets(10, 10, 10, 10));

        alertsArea = new JTextArea();
        alertsArea.setEditable(false);
        alertsArea.setLineWrap(true);
        alertsArea.setWrapStyleWord(true);
        alertsArea.setFont(FONT_GENERAL); 
        alertsArea.setMargin(new Insets(10, 10, 10, 10));
    }

    
    private void setupLayout() {
        setLayout(new BorderLayout(15, 15));
        setBorder(new EmptyBorder(10, 10, 10, 10));

        searchPanel = createSearchPanel();
        add(searchPanel, BorderLayout.NORTH);

        sideMenuPanel = createSideMenu();
        add(sideMenuPanel, BorderLayout.WEST);

        JScrollPane weatherScrollPane = new JScrollPane(weatherDisplayPanel);
        weatherScrollPane.setBorder(null);
        weatherScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        tabbedPane.addTab("<html><body style='padding: 8px 12px;'>Weather</body></html>", weatherScrollPane);

        favoritesPanel = createFavoritesPanel();
        tabbedPane.addTab("<html><body style='padding: 8px 12px;'>Favorites</body></html>", favoritesPanel);

        alertsPanel = createAlertsPanel();
        tabbedPane.addTab("<html><body style='padding: 8px 12px;'>Alerts</body></html>", alertsPanel);

        add(tabbedPane, BorderLayout.CENTER);

        showLoadingInDisplayPanel("Enter a destination to see the weather.");
    }

    private JPanel createSearchPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.insets = new Insets(8, 8, 8, 12);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0; gbc.gridy = 0;
        JLabel destLabel = new JLabel("Destination:");
        destLabel.setFont(FONT_GENERAL_BOLD);
        panel.add(destLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.8;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        destinationField.setPreferredSize(new Dimension(100, 32));
        panel.add(destinationField, gbc);

        gbc.gridx = 2;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        dateSelector.setPreferredSize(new Dimension(190, 32));
        panel.add(dateSelector, gbc);

        gbc.gridx = 3;
        searchButton.setPreferredSize(new Dimension(110, 32));
        panel.add(searchButton, gbc);

        gbc.gridx = 4;
        gbc.insets = new Insets(8, 0, 8, 8);
        JButton favoriteButton = new JButton("★");
        favoriteButton.setFont(new Font("Arial", Font.BOLD, 20));
        favoriteButton.setToolTipText("Add to Favorites");
        favoriteButton.addActionListener(e -> addToFavorites());
        favoriteButton.setPreferredSize(new Dimension(48, 32));
        panel.add(favoriteButton, gbc);

        return panel;
    }

    private JPanel createFavoritesPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));

        JScrollPane listScrollPane = new JScrollPane(favoritesList);
        panel.add(listScrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton removeButton = new JButton("Remove Favorite");
        removeButton.setFont(FONT_GENERAL_BOLD);
        removeButton.setPreferredSize(new Dimension(160, 35));
        removeButton.addActionListener(e -> removeFromFavorites());
        buttonPanel.add(removeButton);
        buttonPanel.setName("FavoritesButtonPanel");

        panel.add(buttonPanel, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createAlertsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));
        alertsArea.setText("Weather alerts for your favorite or searched locations will appear here if available and enabled in settings.");
        JScrollPane scrollPane = new JScrollPane(alertsArea);
        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createSideMenu() {
        JPanel menuPanel = new JPanel();
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));

        String[] menuItems = {"Profile", "Itinerary", "Plan Trip Using AI", "Alerts", "Settings", "Logout"};
        String[] iconPaths = {"/icons/profile.png", "/icons/itinerary.png",
                             "/icons/location.png", "/icons/alert.png",
                             "/icons/settings.png", "/icons/logout.png"};

        for (int i = 0; i < menuItems.length; i++) {
            JButton button = createMenuButton(menuItems[i], iconPaths[i]);
            menuPanel.add(button);
            menuPanel.add(Box.createVerticalStrut(15));
        }
        menuPanel.add(Box.createVerticalGlue());

        return menuPanel;
    }

    private JButton createMenuButton(String text, String iconPath) {
        JButton button = new JButton(text);
        button.setFont(FONT_MEDIUM_BOLD);
        Dimension buttonSize = new Dimension(170, 45);
        button.setPreferredSize(buttonSize);
        button.setMinimumSize(buttonSize);
        button.setMaximumSize(buttonSize);
        button.setAlignmentX(Component.LEFT_ALIGNMENT);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setIconTextGap(18);
        button.setFocusPainted(false);
        button.setBorder(new EmptyBorder(5, 20, 5, 20));

        Icon icon = createScaledIcon(iconPath, 24);
        if (icon != null) {
            button.setIcon(icon);
        }

        return button;
    }

     private ImageIcon createScaledIcon(String path, int size) {
        try {
            java.net.URL imgUrl = getClass().getResource(path);
            if (imgUrl != null) {
                 ImageIcon icon = new ImageIcon(imgUrl);
                 Image img = icon.getImage().getScaledInstance(size, size, Image.SCALE_SMOOTH);
                 return new ImageIcon(img);
            } else {
                 System.err.println("Warning: Icon resource not found: " + path);
                 return null;
            }
        } catch (Exception e) {
             System.err.println("Error loading icon " + path + ": " + e.getMessage());
             return null;
        }
    }


    private void setupListeners() {
        searchButton.addActionListener(e -> searchWeather());
        for(Component comp : sideMenuPanel.getComponents()) {
            if (comp instanceof JButton) {
                JButton button = (JButton) comp;
                if (button.getActionListeners().length == 0) {
                    button.addActionListener(e -> handleMenuAction(button.getText()));
                }
            }
        }
        favoritesList.addListSelectionListener(e -> {
             if (!e.getValueIsAdjusting()) {
                 String selected = favoritesList.getSelectedValue();
                 if (selected != null) {
                     destinationField.setText(selected);
                      searchWeather();
                 }
             }
        });
    }

     private void loadPreferences() {
         favoritesModel.clear();
         preferences.getFavoriteLocations().forEach(favoritesModel::addElement);
     }

    private void addToFavorites() {
        String location = destinationField.getText().trim();
        if (!location.isEmpty() && !favoritesModel.contains(location)) {
            favoritesModel.addElement(location);
            preferences.addFavoriteLocation(location);
        } else if (location.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a destination first.", "Add Favorite", JOptionPane.WARNING_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, location + " is already in favorites.", "Add Favorite", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void removeFromFavorites() {
        int selectedIndex = favoritesList.getSelectedIndex();
        if (selectedIndex != -1) {
            String location = favoritesModel.getElementAt(selectedIndex);
            int confirm = JOptionPane.showConfirmDialog(this,
                "Remove '" + location + "' from favorites?", "Confirm Removal", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                favoritesModel.remove(selectedIndex);
                preferences.removeFavoriteLocation(location);
            }
        } else {
             JOptionPane.showMessageDialog(this, "Please select a favorite location to remove.", "Remove Favorite", JOptionPane.WARNING_MESSAGE);
        }
    }

    private String[] getNextSevenDays() {
        String[] dates = new String[7];
        LocalDate date = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE, dd MMM yyyy");
        for (int i = 0; i < 7; i++) {
            dates[i] = date.plusDays(i).format(formatter);
        }
        return dates;
    }

    private void searchWeather() {
        String destination = destinationField.getText().trim();
        if (destination.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a destination", "Input Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        showLoadingInDisplayPanel("Fetching weather for " + destination + "...");
        SwingWorker<WeatherData, Void> worker = new SwingWorker<>() {
            @Override
            protected WeatherData doInBackground() throws Exception {
                String selectedDateStr = (String) dateSelector.getSelectedItem();
                return weatherService.getWeatherForecast(destination, selectedDateStr);
            }
            @Override
            protected void done() {
                try {
                    WeatherData weatherData = get();
                    displayWeatherDataUI(weatherData);
                } catch (Exception e) {
                    Throwable cause = e.getCause() != null ? e.getCause() : e;
                    showErrorInDisplayPanel("Error: " + cause.getMessage());
                    cause.printStackTrace();
                }
            }
        };
        worker.execute();
    }

     private void showLoadingInDisplayPanel(String message) {
        weatherDisplayPanel.removeAll();
        weatherDisplayPanel.setLayout(new GridBagLayout());
        JLabel loadingLabel = new JLabel(message);
        loadingLabel.setFont(FONT_MEDIUM_BOLD);
        weatherDisplayPanel.add(loadingLabel);
        updateThemeForPanel(weatherDisplayPanel);
        weatherDisplayPanel.revalidate();
        weatherDisplayPanel.repaint();
    }

    private void showErrorInDisplayPanel(String message) {
        weatherDisplayPanel.removeAll();
        weatherDisplayPanel.setLayout(new GridBagLayout());
        JLabel errorLabel = new JLabel("<html><div style='text-align: center;'>"
                                      + message.replace("\n", "<br>")
                                      + "</div></html>");
        errorLabel.setFont(FONT_MEDIUM_BOLD);
        errorLabel.setForeground(Color.RED);
        errorLabel.setHorizontalAlignment(SwingConstants.CENTER);
        weatherDisplayPanel.add(errorLabel);
        updateThemeForPanel(weatherDisplayPanel);
        weatherDisplayPanel.revalidate();
        weatherDisplayPanel.repaint();
    }

     private void displayWeatherDataUI(WeatherData data) {
        weatherDisplayPanel.removeAll();
        weatherDisplayPanel.setLayout(new BorderLayout());

        if (data == null) {
            showErrorInDisplayPanel("Received no weather data.");
            return;
        }

        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setName("WeatherDataContainer");
        contentPanel.setBorder(new EmptyBorder(20, 25, 20, 25));
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.NORTHWEST; gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0; gbc.insets = new Insets(0, 0, 20, 0);
        JPanel headerPanel = createHeaderPanel(data);
        headerPanel.setName("WeatherHeaderPanel");
        contentPanel.add(headerPanel, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.NORTHWEST; gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 0.65; gbc.weighty = 0.6;
        gbc.insets = new Insets(0, 0, 20, 20);
        JPanel mainInfoPanel = createMainWeatherInfoPanel(data);
        mainInfoPanel.setName("WeatherMainInfoPanel");
        contentPanel.add(mainInfoPanel, gbc);

        gbc.gridx = 1; gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.NORTHEAST; gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 0.35; gbc.weighty = 0.6;
        gbc.insets = new Insets(0, 0, 20, 0);
        JPanel detailsPanel = createDetailsPanel(data);
        detailsPanel.setName("WeatherDetailsPanel");
        contentPanel.add(detailsPanel, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.SOUTHWEST; gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0; gbc.weighty = 0.4;
        gbc.insets = new Insets(10, 0, 15, 0);
        JPanel recommendPanel = createRecommendationsPanel(data);
        recommendPanel.setName("WeatherRecommendPanel");
        contentPanel.add(recommendPanel, gbc);

        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.SOUTHEAST; gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weighty = 0; gbc.insets = new Insets(0, 0, 0, 0);

        weatherDisplayPanel.add(contentPanel, BorderLayout.CENTER);

        updateThemeForPanel(weatherDisplayPanel);

        weatherDisplayPanel.revalidate();
        weatherDisplayPanel.repaint();
    }

    private JPanel createHeaderPanel(WeatherData data) {
        JPanel headerPanel = new JPanel(new BorderLayout());

        JLabel locationLabel = new JLabel(data.getLocation() != null ? data.getLocation() : "Unknown Location");
        locationLabel.setFont(FONT_TITLE);
        headerPanel.add(locationLabel, BorderLayout.WEST);

        JLabel dateLabel = new JLabel(data.getDate() != null ? data.getDate() : "Unknown Date");
        dateLabel.setFont(FONT_SUBTITLE);
        dateLabel.setBorder(new EmptyBorder(5, 0, 0, 0));
        headerPanel.add(dateLabel, BorderLayout.EAST);
        return headerPanel;
    }

    private JPanel createMainWeatherInfoPanel(WeatherData data) {
        JPanel mainPanel = new JPanel(new BorderLayout(15, 10));

        JPanel tempIconPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        tempIconPanel.setOpaque(false);

        String tempUnit = preferences.getTemperatureUnit();
        double displayTemp = convertTemperature(data.getTemperature(), tempUnit);
        JLabel tempLabel = new JLabel(String.format("%.1f%s", displayTemp, tempUnit));
        tempLabel.setFont(FONT_XLARGE_BOLD);
        tempIconPanel.add(tempLabel);

        JLabel weatherIconLabel = new JLabel(getWeatherIconEmoji(data.getCondition()));
        weatherIconLabel.setFont(FONT_ICON_LARGE);
        tempIconPanel.add(weatherIconLabel);
        mainPanel.add(tempIconPanel, BorderLayout.NORTH);

        JPanel conditionDetailsPanel = new JPanel();
        conditionDetailsPanel.setLayout(new BoxLayout(conditionDetailsPanel, BoxLayout.Y_AXIS));
        conditionDetailsPanel.setOpaque(false);
        conditionDetailsPanel.setBorder(new EmptyBorder(5, 5, 10, 5));

        JLabel conditionLabel = new JLabel(data.getCondition() != null ? data.getCondition() : "N/A");
        conditionLabel.setFont(FONT_LARGE_BOLD);
        conditionLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        conditionDetailsPanel.add(conditionLabel);
        conditionDetailsPanel.add(Box.createVerticalStrut(5));

        JLabel descriptionLabel = new JLabel(data.getDescription() != null ? capitalizeFirstLetter(data.getDescription()) : "");
        descriptionLabel.setFont(FONT_SUBTITLE);
        descriptionLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        conditionDetailsPanel.add(descriptionLabel);
        conditionDetailsPanel.add(Box.createVerticalStrut(15));

        double displayMin = convertTemperature(data.getTempMin(), tempUnit);
        double displayMax = convertTemperature(data.getTempMax(), tempUnit);
        JLabel minMaxLabel = new JLabel(String.format("Min: %.1f%s / Max: %.1f%s", displayMin, tempUnit, displayMax, tempUnit));
        minMaxLabel.setFont(FONT_GENERAL);
        minMaxLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        conditionDetailsPanel.add(minMaxLabel);
        conditionDetailsPanel.add(Box.createVerticalStrut(5));

        double displayFeelsLike = convertTemperature(data.getFeelsLike(), tempUnit);
        JLabel feelsLikeLabel = new JLabel(String.format("Feels like: %.1f%s", displayFeelsLike, tempUnit));
        feelsLikeLabel.setFont(FONT_GENERAL);
        feelsLikeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        conditionDetailsPanel.add(feelsLikeLabel);
        mainPanel.add(conditionDetailsPanel, BorderLayout.CENTER);

        return mainPanel;
    }

     private String getWeatherIconEmoji(String condition) {
        if (condition == null) return "❓";
        String condLower = condition.toLowerCase();
        if (condLower.contains("clear")) return "☀️";
        if (condLower.contains("sun")) return "☀️";
        if (condLower.contains("few clouds")) return "🌤️";
        if (condLower.contains("scattered clouds")) return "☁️";
        if (condLower.contains("broken clouds")) return "☁️";
        if (condLower.contains("overcast clouds")) return "🌥️";
        if (condLower.contains("shower rain")) return "🌧️";
        if (condLower.contains("rain")) return "🌧️";
        if (condLower.contains("drizzle")) return "💧";
        if (condLower.contains("thunder")) return "⛈️";
        if (condLower.contains("snow")) return "❄️";
        if (condLower.contains("mist") || condLower.contains("fog") || condLower.contains("haze")) return "🌫️";
        if (condLower.contains("wind")) return "💨";
        return "🌡️"; 
    }

    private JPanel createDetailsPanel(WeatherData data) {
         JPanel detailsPanel = new JPanel();
         detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));

         String windUnit = preferences.getWindSpeedUnit();
         double displayWindSpeed = convertWindSpeed(data.getWindSpeed(), windUnit);
         String visibilityStr = Double.isNaN(data.getVisibility()) ? "N/A" : String.format("%.1f km", data.getVisibility());

         detailsPanel.add(createDetailItem("Humidity", String.format("%.0f%%", data.getHumidity()), "💧"));
         detailsPanel.add(Box.createVerticalStrut(18));
         detailsPanel.add(createDetailItem("Wind Speed", String.format("%.1f %s", displayWindSpeed, windUnit), "💨"));
         detailsPanel.add(Box.createVerticalStrut(18));
         detailsPanel.add(createDetailItem("Pressure", String.format("%.0f hPa", data.getPressure()), "📊"));
         detailsPanel.add(Box.createVerticalStrut(18));
         detailsPanel.add(createDetailItem("Visibility", visibilityStr, "👁️"));
         detailsPanel.add(Box.createVerticalGlue());

         return detailsPanel;
    }

     private JPanel createDetailItem(String label, String value, String iconUnicode) {
        JPanel panel = new JPanel(new BorderLayout(12, 0));
        panel.setOpaque(false);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));

        JLabel iconLabel = new JLabel(iconUnicode);

        iconLabel.setFont(FONT_ICON_MEDIUM); 
        panel.add(iconLabel, BorderLayout.WEST);

        JLabel nameLabel = new JLabel(label + ":");
        nameLabel.setFont(FONT_GENERAL);
        panel.add(nameLabel, BorderLayout.CENTER);

        JLabel valueLabel = new JLabel(value != null ? value : "N/A");
        valueLabel.setFont(FONT_GENERAL_BOLD);
        panel.add(valueLabel, BorderLayout.EAST);

        return panel;
    }

    private JPanel createRecommendationsPanel(WeatherData data) {
        JPanel panel = new JPanel(new BorderLayout(0, 8));

        JLabel titleLabel = new JLabel("Travel Recommendations");
        titleLabel.setFont(FONT_MEDIUM_BOLD);
        titleLabel.setBorder(new EmptyBorder(0, 0, 5, 0));
        panel.add(titleLabel, BorderLayout.NORTH);

        generateRecommendations(data);
        JScrollPane scrollPane = new JScrollPane(recommendationsArea);
        scrollPane.setBorder(null);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private String capitalizeFirstLetter(String str) {
         if (str == null || str.isEmpty()) return str;
         return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    private double convertTemperature(double celsius, String targetUnit) {
        if (Double.isNaN(celsius)) return Double.NaN;
        if ("°F".equals(targetUnit)) return (celsius * 9.0 / 5.0) + 32.0;
        return celsius;
    }

     private double convertWindSpeed(double kph, String targetUnit) {
         if (Double.isNaN(kph)) return Double.NaN;
         if ("mph".equals(targetUnit)) return kph / 1.60934;
         return kph;
     }

    private void generateRecommendations(WeatherData data) {
         
         recommendationsArea.setFont(FONT_RECOMMENDATION); 

         if (data == null || Double.isNaN(data.getTemperature())) {
             recommendationsArea.setText("  (Could not generate recommendations due to missing weather data.)");
            
             return;
         }
         StringBuilder sb = new StringBuilder();
         double temp = data.getTemperature();
         String condition = data.getCondition() != null ? data.getCondition().toLowerCase() : "";

          if (condition.contains("rain") || condition.contains("drizzle") || condition.contains("thunderstorm")) {
             sb.append("• ☂️ Umbrella or raincoat is essential.\n");
             sb.append("• 🏛️ Consider indoor activities: museums, galleries, cafes.\n");
             sb.append("• ⚠️ Check for potential flooding or travel disruptions if severe.\n");
         } else if (condition.contains("snow") || condition.contains("sleet")) {
             sb.append("• 🧥 Dress warmly: layers, hat, gloves, scarf, waterproof outer layer.\n");
             sb.append("• 👢 Wear insulated, waterproof boots with good traction.\n");
             sb.append("• ⏳ Allow extra travel time due to possible delays.\n");
             sb.append("• ⛷️ Enjoy winter activities like skiing or building a snowman! ☃️\n");
         } else if (condition.contains("clear") || condition.contains("sun")) {
             if (temp > 28) {
                 sb.append("• 🔥 Hot! Stay hydrated, use sunscreen (SPF 30+), wear a hat and sunglasses. ☀️\n");
                 sb.append("• 🌳 Seek shade during peak sun hours (11am-3pm).\n");
                 sb.append("• 👕 Light, loose-fitting clothing is best.\n");
             } else if (temp > 20) {
                 sb.append("• 👍 Pleasant weather for outdoor activities. 🏞️\n");
                 sb.append("• 😎 Sunscreen still recommended for extended exposure.\n");
                 sb.append("• 🏕️ Enjoy parks, sightseeing, outdoor dining.\n");
             } else if (temp > 10) {
                 sb.append("• 😊 Mild and sunny. A light jacket or sweater may be needed. 🌤️\n");
                 sb.append("• 🚶 Great for walking tours or exploring the city.\n");
             } else {
                 sb.append("• 🥶 Cool and clear. Dress in layers, including a warm jacket. 🧥\n");
                 sb.append("• 🚶‍♂️ Good for brisk walks if dressed appropriately.\n");
             }
         } else if (condition.contains("clouds")) {
              if (temp > 20) {
                 sb.append("• 👍 Good for outdoor activities without intense sun. 🌤️\n");
                 sb.append("• 🌬️ Cloud cover can make it feel slightly cooler; have a light layer handy.\n");
             } else if (temp > 10) {
                 sb.append("• 😊 Comfortable for exploring, but bring a jacket or sweater. 🧥\n");
             } else {
                 sb.append("• 🥶 Cool and cloudy. Dress warmly in layers. ⛅\n");
             }
              if (data.getDescription() != null && data.getDescription().toLowerCase().contains("overcast")) {
                   sb.append("• 🌥️ Overcast skies might make it feel gloomy; plan some indoor options.\n");
              }
         } else if (condition.contains("fog") || condition.contains("mist") || (!Double.isNaN(data.getVisibility()) && data.getVisibility() < 1.0)) {
             sb.append("• 🌫️ Reduced visibility: Drive carefully, use fog lights if necessary.\n");
             sb.append("• 📸 Can create atmospheric photo opportunities.\n");
             sb.append("• ⏳ Allow extra time for travel.\n");
         } else {
              sb.append("• 🤔 Check the specific conditions and dress accordingly. 👕\n");
              if (temp > 20) sb.append("• 👍 Likely pleasant for most activities. 🏞️\n");
              else if (temp > 10) sb.append("• 🧥 Bring layers for changing temperatures.\n");
              else sb.append("• 🥶 Dress warmly.\n");
         }
          if (!Double.isNaN(data.getWindSpeed()) && data.getWindSpeed() > 30) {
             sb.append("• 💨 It may be windy; secure hats and loose items.\n");
             sb.append("• 🌊 Coastal areas or open spaces might feel significantly colder.\n");
         }


         recommendationsArea.setText(sb.toString());
         recommendationsArea.setCaretPosition(0);
    }

    private void updateTheme() {
        boolean isDark = preferences.getTheme().equals("Dark");

        // Light Theme
        Color bgColorLight = new Color(16, 38, 95);  // background of app    140, 10, 159          16, 38, 95
        Color fgColorLight = new Color(20, 20, 20);
        Color searchBgLight = new Color(228, 250, 255); // Slightly blueish white  179, 240, 255        240, 245, 250
        Color componentFgLight = new Color(50, 50, 50);
        Color accentBgLight = new Color(68, 68, 235); //  60, 130, 190
        Color accentFgLight = Color.WHITE;
        Color subtleFgLight = new Color(100, 100, 100);
        Color listBgLight = Color.WHITE;
        Color listSelBgLight = accentBgLight;
        Color listSelFgLight = accentFgLight;
        Color borderLight = new Color(215, 215, 220); // darker border
        Color sideMenuBgLight = new Color(238, 238, 238); // darker than default panel
        
        // Dark Theme
        Color bgColorDark = new Color(35, 35, 45);
        Color fgColorDark = new Color(230, 230, 230);
        Color searchBgDark = new Color(50, 50, 65);
        Color componentFgDark = fgColorDark;
        Color accentBgDark = new Color(80, 140, 190);
        Color accentFgDark = Color.WHITE;
        Color subtleFgDark = new Color(160, 160, 170);
        Color listBgDark = new Color(45, 45, 58); 
        Color listSelBgDark = accentBgDark;
        Color listSelFgDark = accentFgDark;
        Color borderDark = new Color(75, 75, 85);
        Color sideMenuBgDark = new Color(42, 42, 55);


        Color favStarColor = new Color(255, 190, 0);


        Color bgColor = isDark ? bgColorDark : bgColorLight;
        Color fgColor = isDark ? fgColorDark : fgColorLight;
        Color searchBg = isDark ? searchBgDark : searchBgLight;
        Color componentFg = isDark ? componentFgDark : componentFgLight;
        Color accentBg = isDark ? accentBgDark : accentBgLight;
        Color accentFg = isDark ? accentFgDark : accentFgLight;
        Color subtleFg = isDark ? subtleFgDark : subtleFgLight;
        Color listBg = isDark ? listBgDark : listBgLight;
        Color listSelBg = isDark ? listSelBgDark : listSelBgLight;
        Color listSelFg = isDark ? listSelFgDark : listSelFgLight;
        Color borderColor = isDark ? borderDark : borderLight;
        Color sideMenuBg = isDark ? sideMenuBgDark : sideMenuBgLight;
        Color componentBg = isDark ? new Color(55, 55, 70) : Color.WHITE;



        setBackground(bgColor);
        if (searchPanel != null) {
             searchPanel.setBackground(searchBg);
             searchPanel.setBorder(new CompoundBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, borderColor), new EmptyBorder(10, 10, 10, 10)));
        }
        if (sideMenuPanel != null) {
             sideMenuPanel.setBackground(sideMenuBg);
             sideMenuPanel.setBorder(new CompoundBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, borderColor), new EmptyBorder(20, 10, 20, 15)));
        }
        if (tabbedPane != null) {
            tabbedPane.setBackground(bgColor);
            tabbedPane.setForeground(fgColor);
            for (int i = 0; i < tabbedPane.getTabCount(); i++) {
                Component tabComp = tabbedPane.getComponentAt(i);
                 if (tabComp instanceof JScrollPane) {
                     ((JScrollPane)tabComp).getViewport().getView().setBackground(bgColor);
                     ((JScrollPane)tabComp).setBackground(bgColor);
                 } else if (tabComp != null){
                    tabComp.setBackground(bgColor);
                 }
            }
        }

        if (favoritesPanel != null) {
            favoritesPanel.setBackground(bgColor);
            for (Component comp : favoritesPanel.getComponents()) {
                if (comp instanceof JScrollPane) {
                    ((JScrollPane) comp).setBorder(BorderFactory.createLineBorder(borderColor));
                } else if (comp instanceof JPanel && "FavoritesButtonPanel".equals(comp.getName())) {
                    ((JPanel) comp).setOpaque(false);
                    comp.setBackground(null);
                 }
            }
        }
        if (alertsPanel != null) {
            alertsPanel.setBackground(bgColor);
            for (Component comp : alertsPanel.getComponents()) {
                 if (comp instanceof JScrollPane) {
                    ((JScrollPane) comp).setBorder(BorderFactory.createLineBorder(borderColor));
                 }
            }
        }


        applyThemeRecursive(this, bgColor, fgColor, componentBg, componentFg, accentBg, accentFg, subtleFg, borderColor, sideMenuBg, searchBg, favStarColor);


        if (favoritesList != null) {
            favoritesList.setBackground(listBg);
            favoritesList.setForeground(componentFg);
            favoritesList.setSelectionBackground(listSelBg);
            favoritesList.setSelectionForeground(listSelFg);
        }

        if (recommendationsArea != null) {
            recommendationsArea.setFont(FONT_RECOMMENDATION);
            recommendationsArea.setForeground(componentFg);
            recommendationsArea.setCaretColor(componentFg);
            recommendationsArea.setSelectionColor(listSelBg);
            recommendationsArea.setSelectedTextColor(listSelFg);

        }
        if (alertsArea != null) {
            alertsArea.setBackground(listBg); 
            alertsArea.setForeground(componentFg);
            alertsArea.setCaretColor(componentFg);
            alertsArea.setSelectionColor(listSelBg);
            alertsArea.setSelectedTextColor(listSelFg);
        }


        updateThemeForPanel(weatherDisplayPanel);

        SwingUtilities.updateComponentTreeUI(this);
    }

    private void applyThemeRecursive(Component comp, Color bg, Color fg, Color compBg, Color compFg, Color accentBg, Color accentFg, Color subtleFg, Color borderCol, Color sideMenuBg, Color searchBg, Color favStarCol) {

        
        if (!(comp instanceof JLabel) && !(comp instanceof JButton && comp.getParent() == sideMenuPanel)) {
             comp.setForeground(fg);
        }


        if (comp instanceof JPanel) {
            JPanel panel = (JPanel) comp;
            String panelName = panel.getName();

            if (panel == searchPanel) { panel.setBackground(searchBg); panel.setOpaque(true); }
            else if (panel == sideMenuPanel) { panel.setBackground(sideMenuBg); panel.setOpaque(true); }
            else if (panel == favoritesPanel || panel == alertsPanel) {
                panel.setBackground(bg); panel.setOpaque(true);
            }
            else if ("FavoritesButtonPanel".equals(panelName) || "WeatherFooterPanel".equals(panelName)) {
                 panel.setOpaque(false); panel.setBackground(null);
            }
            else if (panel != weatherDisplayPanel && !"WeatherDataContainer".equals(panelName) && panel.getParent() != weatherDisplayPanel && (panelName == null || !panelName.startsWith("Weather"))) {
                 panel.setBackground(bg);
                 panel.setOpaque(true);
            } else {
                 panel.setOpaque(false);
            }
        } else if (comp instanceof JButton) {
            JButton button = (JButton) comp;
            button.setOpaque(true);
            button.setBorderPainted(false);


            if (button.getParent() == sideMenuPanel) {
                button.setOpaque(false);
                button.setBorder(new EmptyBorder(5, 20, 5, 20));
                button.setBackground(null);
                button.setForeground(fg);
            } else if ("★".equals(button.getText())) {
                button.setBackground(compBg);
                button.setForeground(favStarCol);
                button.setBorder(new LineBorder(borderCol));
                button.setBorderPainted(true);
            } else {
                button.setBackground(accentBg);
                button.setForeground(accentFg);
                button.setBorder(new EmptyBorder(8, 18, 8, 18));
            }
        } else if (comp instanceof JTextField || comp instanceof JPasswordField) {
            comp.setBackground(compBg);
            comp.setForeground(compFg);
            ((JComponent) comp).setBorder(new CompoundBorder(new LineBorder(borderCol), new EmptyBorder(5, 8, 5, 8)));
            if (comp instanceof JTextField) {
                ((JTextField) comp).setCaretColor(compFg);
                ((JTextField) comp).setSelectionColor(accentBg);
                ((JTextField) comp).setSelectedTextColor(accentFg);
            }
            if (comp instanceof JPasswordField) {
                 ((JPasswordField) comp).setCaretColor(compFg);
                 ((JPasswordField) comp).setSelectionColor(accentBg);
                 ((JPasswordField) comp).setSelectedTextColor(accentFg);
            }
        } else if (comp instanceof JComboBox) {
            comp.setBackground(compBg);
            comp.setForeground(compFg);
            ((JComponent) comp).setBorder(new LineBorder(borderCol));
        } else if (comp instanceof JLabel) {
             ((JLabel) comp).setOpaque(false);
             Font f = comp.getFont();

             boolean isHeaderFont = f.equals(FONT_TITLE) || f.equals(FONT_XLARGE_BOLD) || f.equals(FONT_LARGE_BOLD) || f.equals(FONT_MEDIUM_BOLD) || f.equals(FONT_GENERAL_BOLD);
             boolean isEmojiFont = f.equals(FONT_ICON_LARGE) || f.equals(FONT_ICON_MEDIUM) || f.equals(FONT_RECOMMENDATION); // Include recommendation font here if it uses Emoji

             if (isHeaderFont || isEmojiFont) {
                 comp.setForeground(fg);
             } else if (f.equals(FONT_SUBTITLE) || f.getStyle() == Font.ITALIC) {
                 comp.setForeground(subtleFg);
             } else {
                 comp.setForeground(compFg);
             }
        } else if (comp instanceof JScrollPane) {
            JScrollPane scrollPane = (JScrollPane) comp;
             Component view = scrollPane.getViewport().getView();
             if (view instanceof JList || view instanceof JTextArea) {
                 scrollPane.setBackground(view.getBackground());
                 scrollPane.getViewport().setBackground(view.getBackground());
             } else if (view == favoritesPanel || view == alertsPanel) {
                 scrollPane.setBackground(bg);
                 scrollPane.getViewport().setBackground(bg);
             } else {
                 scrollPane.setBackground(bg);
                 scrollPane.getViewport().setBackground(bg);
             }
        } else if (comp instanceof JTabbedPane || comp instanceof JList || comp instanceof JTextArea) {

        } else {
            if(comp.isOpaque()) {
                comp.setBackground(bg);
                comp.setForeground(fg);
            } else {
                 comp.setForeground(fg);
            }
        }

        if (comp instanceof Container) {
            for (Component child : ((Container) comp).getComponents()) {
                if (!isWeatherPanelComponent(child)) {
                    applyThemeRecursive(child, bg, fg, compBg, compFg, accentBg, accentFg, subtleFg, borderCol, sideMenuBg, searchBg, favStarCol);
                }
            }
        }
    }

    private boolean isWeatherPanelComponent(Component comp) {
        Component current = comp;
        while (current != null) {
            String name = current.getName();
            if (name != null && name.startsWith("Weather")) {
                return true;
            }
            if (current == weatherDisplayPanel) {
                return true;
            }
            current = current.getParent();
        }
        return false;
    }


    private void updateThemeForPanel(JPanel panel) {
        boolean isDark = preferences.getTheme().equals("Dark");

        // Light Theme
        Color bgColorLight = new Color(249, 249, 249);
        Color fgColorLight = new Color(20, 20, 20);
        Color mainPanelBgLight = new Color(235, 242, 252);
        Color detailsPanelBgLight = new Color(248, 248, 248);
        Color recommendPanelBgLight = new Color(255, 255, 248);
        Color subtleFgLight = new Color(100, 100, 100);
        Color borderLight = new Color(215, 215, 220);
        Color componentFgLight = new Color(50, 50, 50);
        Color accentBgLight = new Color(60, 130, 190);
        Color accentFgLight = Color.WHITE;

        // Dark Theme
        Color bgColorDark = new Color(35, 35, 45);
        Color fgColorDark = new Color(230, 230, 230);
        Color mainPanelBgDark = new Color(40, 45, 58);
        Color detailsPanelBgDark = new Color(58, 58, 75);
        Color recommendPanelBgDark = new Color(42, 42, 52);
        Color subtleFgDark = new Color(160, 160, 170);
        Color borderDark = new Color(75, 75, 85);
        Color componentFgDark = fgColorDark;
        Color accentBgDark = new Color(80, 140, 190);
        Color accentFgDark = Color.WHITE;



        Color bgColor = isDark ? bgColorDark : bgColorLight;
        Color fgColor = isDark ? fgColorDark : fgColorLight;
        Color mainPanelBg = isDark ? mainPanelBgDark : mainPanelBgLight;
        Color detailsPanelBg = isDark ? detailsPanelBgDark : detailsPanelBgLight;
        Color recommendPanelBg = isDark ? recommendPanelBgDark : recommendPanelBgLight;
        Color subtleFg = isDark ? subtleFgDark : subtleFgLight;
        Color borderColor = isDark ? borderDark : borderLight;
        Color componentFg = isDark ? componentFgDark : componentFgLight;
        Color accentBg = isDark ? accentBgDark : accentBgLight;
        Color accentFg = isDark ? accentFgDark : accentFgLight;


        panel.setBackground(bgColor);
        panel.setOpaque(true);

        if (panel.getComponentCount() > 0 && panel.getComponent(0) instanceof JPanel && "WeatherDataContainer".equals(panel.getComponent(0).getName())) {
            JPanel contentPanel = (JPanel) panel.getComponent(0);
            contentPanel.setBackground(bgColor);
            contentPanel.setOpaque(true);

            Component header = null; Component mainInfo = null; Component details = null;
            Component recommend = null; Component footer = null;

            for(Component child : contentPanel.getComponents()){
                 String name = child.getName();
                 if (name != null) {
                     switch (name) {
                         case "WeatherHeaderPanel": header = child; break;
                         case "WeatherMainInfoPanel": mainInfo = child; break;
                         case "WeatherDetailsPanel": details = child; break;
                         case "WeatherRecommendPanel": recommend = child; break;
                         case "WeatherFooterPanel": footer = child; break;
                     }
                 }
            }

             if (header instanceof JPanel) {
                 ((JPanel)header).setOpaque(false);
                 applyThemeToPanelChildren((JPanel)header, bgColor, fgColor, subtleFg, true);
             }

             if (mainInfo instanceof JPanel) {
                 mainInfo.setBackground(mainPanelBg); ((JPanel)mainInfo).setOpaque(true);
                 ((JPanel)mainInfo).setBorder(new CompoundBorder(new LineBorder(borderColor, 1, true), new EmptyBorder(15, 20, 15, 20)));
                 applyThemeToPanelChildren((JPanel)mainInfo, mainPanelBg, fgColor, subtleFg, false);
             }

             if (details instanceof JPanel) {
                 details.setBackground(detailsPanelBg); ((JPanel)details).setOpaque(true);
                 ((JPanel)details).setBorder(new CompoundBorder(new LineBorder(borderColor, 1, true), new EmptyBorder(15, 15, 15, 15)));
                 applyThemeToPanelChildren((JPanel)details, detailsPanelBg, componentFg, subtleFg, false);
             }

             if (recommend instanceof JPanel) {
                 recommend.setBackground(recommendPanelBg); ((JPanel)recommend).setOpaque(true);
                 ((JPanel)recommend).setBorder(new CompoundBorder(new LineBorder(borderColor, 1, true), new EmptyBorder(10, 15, 10, 15)));
                 for(Component c : ((JPanel)recommend).getComponents()) {
                     if (c instanceof JLabel) {
                         c.setForeground(fgColor);
                     } else if (c instanceof JScrollPane) {
                          JTextArea area = (JTextArea)((JScrollPane)c).getViewport().getView();
                          area.setBackground(recommendPanelBg);
                          area.setForeground(componentFg);
                          area.setCaretColor(componentFg);
                          area.setSelectionColor(accentBg);
                          area.setSelectedTextColor(accentFg);
                          area.setFont(FONT_RECOMMENDATION);
                          c.setBackground(recommendPanelBg);
                          ((JScrollPane)c).setBorder(null);
                          ((JScrollPane)c).getViewport().setBackground(recommendPanelBg);
                     }
                 }
             }

             if (footer instanceof JPanel) {
                 ((JPanel)footer).setOpaque(false);
                 applyThemeToPanelChildren((JPanel)footer, bgColor, fgColor, subtleFg, true);
             }

        } else if (panel.getComponentCount() > 0 && panel.getComponent(0) instanceof JLabel) {
             JLabel label = (JLabel) panel.getComponent(0);
             if (label.getForeground() != Color.RED) {
                 label.setForeground(subtleFg);
             }
             label.setOpaque(false);
             label.setBackground(bgColor);
        }
    }

    private void applyThemeToPanelChildren(JPanel panel, Color bg, Color fg, Color subtleFg, boolean makeTransparent) {
        panel.setBackground(bg);
        panel.setOpaque(!makeTransparent);
        for (Component c : panel.getComponents()) {
            Color currentFg = fg;
            if (c instanceof JLabel) {
                Font f = c.getFont();
                 if (f.equals(FONT_SUBTITLE) || f.getStyle() == Font.ITALIC) {
                     currentFg = subtleFg;
                 }
                 ((JLabel)c).setOpaque(false);
                 ((JLabel)c).setBackground(null);
            } else if (c instanceof JPanel) {
                applyThemeToPanelChildren((JPanel) c, bg, fg, subtleFg, true);
            } else if (c instanceof JScrollPane) {
                c.setBackground(bg);
                ((JScrollPane)c).getViewport().setBackground(bg);
                ((JScrollPane)c).setOpaque(!makeTransparent);
            } else if (c instanceof JTextArea) {
                 c.setBackground(bg);
                 ((JTextArea) c).setOpaque(!makeTransparent);
                 if (c == recommendationsArea) {
                     c.setFont(FONT_RECOMMENDATION);
                 }
            }

             c.setForeground(currentFg);
        }
    }

    private void showSettings() {
        JDialog settingsDialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this), "Settings", true);
        settingsDialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 15, 10, 15);
        gbc.anchor = GridBagConstraints.WEST;

        boolean isDark = preferences.getTheme().equals("Dark");
        Color dialogBg = isDark ? new Color(50, 50, 60) : Color.WHITE;
        Color dialogFg = isDark ? new Color(230, 230, 230) : Color.BLACK;
        Color fieldBg = isDark ? new Color(60, 60, 70) : new Color(245, 245, 245);
        Color buttonBg = isDark ? new Color(80, 140, 190) : new Color(60, 130, 190);
        Color buttonFg = Color.WHITE;
        Color borderCol = isDark ? new Color(75, 75, 85) : new Color(215, 215, 220);


        settingsDialog.getContentPane().setBackground(dialogBg);

        Function<String, JLabel> createLabel = (text) -> {
            JLabel lbl = new JLabel(text);
            lbl.setForeground(dialogFg);
            lbl.setFont(FONT_GENERAL);
            return lbl;
        };

        gbc.gridx=0; gbc.gridy=0; settingsDialog.add(createLabel.apply("Temperature Unit:"), gbc);
        gbc.gridx=0; gbc.gridy=1; settingsDialog.add(createLabel.apply("Wind Speed Unit:"), gbc);
        gbc.gridx=0; gbc.gridy=2; settingsDialog.add(createLabel.apply("Theme:"), gbc);
        gbc.gridx=0; gbc.gridy=3; settingsDialog.add(createLabel.apply("Receive Alerts:"), gbc);

        gbc.gridx=1; gbc.gridy=0; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        JComboBox<String> tempUnit = new JComboBox<>(new String[]{"°C", "°F"});
        tempUnit.setSelectedItem(preferences.getTemperatureUnit());
        styleDialogComponent(tempUnit, fieldBg, dialogFg, borderCol);
        settingsDialog.add(tempUnit, gbc);

        gbc.gridy=1;
        JComboBox<String> windUnit = new JComboBox<>(new String[]{"km/h", "mph"});
        windUnit.setSelectedItem(preferences.getWindSpeedUnit());
        styleDialogComponent(windUnit, fieldBg, dialogFg, borderCol);
        settingsDialog.add(windUnit, gbc);

        gbc.gridy=2;
        JComboBox<String> theme = new JComboBox<>(new String[]{"Light", "Dark"});
        theme.setSelectedItem(preferences.getTheme());
        styleDialogComponent(theme, fieldBg, dialogFg, borderCol);
        settingsDialog.add(theme, gbc);

        gbc.gridy=3; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0.0;
        JCheckBox alerts = new JCheckBox("", preferences.isReceiveAlerts());
        alerts.setBackground(dialogBg);
        settingsDialog.add(alerts, gbc);

        gbc.gridx=0; gbc.gridy=4; gbc.gridwidth=2; gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.NONE; gbc.insets = new Insets(25, 10, 15, 10);
        JButton saveButton = new JButton("Save");
        saveButton.setBackground(buttonBg); saveButton.setForeground(buttonFg);
        saveButton.setFont(FONT_GENERAL_BOLD);
        saveButton.setOpaque(true); saveButton.setBorderPainted(false);
        saveButton.setPreferredSize(new Dimension(100, 35));
        saveButton.addActionListener(e -> {
            preferences.setTemperatureUnit((String) tempUnit.getSelectedItem());
            preferences.setWindSpeedUnit((String) windUnit.getSelectedItem());
            preferences.setTheme((String) theme.getSelectedItem());
            preferences.setReceiveAlerts(alerts.isSelected());
            settingsDialog.dispose();
            updateTheme();
             if (weatherDisplayPanel.getComponentCount() > 0 && weatherDisplayPanel.getComponent(0).getName().equals("WeatherDataContainer")) {
                 searchWeather();
             }
        });
        settingsDialog.add(saveButton, gbc);

        settingsDialog.pack();
        settingsDialog.setMinimumSize(new Dimension(350, settingsDialog.getPreferredSize().height));
        settingsDialog.setLocationRelativeTo(this);
        settingsDialog.setVisible(true);
    }

    private void styleDialogComponent(JComponent comp, Color bg, Color fg, Color borderCol) {
        comp.setBackground(bg);
        comp.setForeground(fg);
        comp.setFont(FONT_GENERAL);
        comp.setBorder(new CompoundBorder(new LineBorder(borderCol), new EmptyBorder(5, 8, 5, 8)));
        if (comp instanceof JComboBox) {
            ((JComboBox<?>) comp).setPreferredSize(new Dimension(100, 30));
        }
    }

    private void handleMenuAction(String action) {
         switch (action) {
            case "Settings":
                showSettings();
                break;
            case "Logout":
                int choice = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to logout?",
                    "Logout Confirmation",
                    JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                if (choice == JOptionPane.YES_OPTION) {
                    JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
                    if (frame != null) {
                        frame.getContentPane().removeAll();
                         frame.getContentPane().add(new LoginPanel());
                        frame.getContentPane().revalidate();
                        frame.getContentPane().repaint();
                    }
                }
                break;
            case "Plan Trip Using AI":
                JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
                if (frame != null) {

                    AIChatPanel chatPanel = new AIChatPanel(frame, this, preferences);
                    frame.getContentPane().removeAll();
                    frame.getContentPane().add(chatPanel);
                    frame.getContentPane().revalidate();
                    frame.getContentPane().repaint();
                    chatPanel.focusInput();
                }
            break;
            case "Alerts":
                 if (tabbedPane.getTabCount() > 2) {
                     tabbedPane.setSelectedIndex(2);
                 }
                break;
            case "Profile":
            case "Itinerary":
                JOptionPane.showMessageDialog(this,
                    action + " feature is not yet implemented.",
                    "Feature Not Available",
                    JOptionPane.INFORMATION_MESSAGE);
                break;
        }
    }

} 