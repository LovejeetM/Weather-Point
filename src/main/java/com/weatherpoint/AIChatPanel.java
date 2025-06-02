package com.weatherpoint;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.weatherpoint.model.UserPreferences;

public class AIChatPanel extends JPanel {

    private JFrame mainFrame;
    private DashboardPanel dashboardPanel;
    private UserPreferences preferences;

    private JTextPane chatPane;
    private JTextField inputField;
    private JButton sendButton;
    private JButton backButton;
    private StyledDocument styledDoc;
    private ObjectMapper objectMapper; 

    private String apiKey;
    private boolean apiKeyInitialized = false;

    private static final Font FONT_CHAT = new Font("Franklin Gothic", Font.PLAIN, 20);
    private static final Font FONT_INPUT = new Font("Franklin Gothic", Font.PLAIN, 20);
    private static final Font FONT_BUTTON = new Font("Franklin Gothic", Font.BOLD, 28);
    private static final Font FONT_TITLE = new Font("Franklin Gothic", Font.BOLD, 38);

    private static final String GEMINI_MODEL = "gemini-2.0-flash"; 
    private static final String API_ENDPOINT_FORMAT = "https://generativelanguage.googleapis.com/v1beta/models/%s:generateContent?key=%s";

    public AIChatPanel(JFrame mainFrame, DashboardPanel dashboardPanel, UserPreferences preferences) {
        this.mainFrame = mainFrame;
        this.dashboardPanel = dashboardPanel;
        this.preferences = preferences;
        this.objectMapper = new ObjectMapper();

        initializeAPIKey();
        initializeComponents();
        setupLayout();
        setupListeners();
        applyTheme();
        addInitialMessage();
    }

    private void initializeAPIKey() {
        apiKey = "ENTER API KEY HERE - GET API KEY FROM GOOGLE AI STUDIO"; //    aKEY

        if (apiKey == null || apiKey.trim().isEmpty()) {
            System.err.println("Error: GEMINI_API_KEY environment variable not set.");
            apiKeyInitialized = false;
        } else {
            apiKeyInitialized = true;
        }
    }

    private void initializeComponents() {
        chatPane = new JTextPane();
        chatPane.setEditable(false);
        chatPane.setFont(FONT_CHAT);
        chatPane.setMargin(new Insets(10, 10, 10, 10));
        styledDoc = chatPane.getStyledDocument();

        inputField = new JTextField(40);
        inputField.setFont(FONT_INPUT);

        sendButton = new JButton("Send");
        sendButton.setFont(FONT_BUTTON);
        sendButton.setEnabled(apiKeyInitialized);

        backButton = new JButton("← Back");
        backButton.setFont(FONT_BUTTON);
    }

    private void setupLayout() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JPanel topPanel = new JPanel(new BorderLayout(10, 0));
        topPanel.setOpaque(false);

        JLabel titleLabel = new JLabel("AI Trip Planner");
        titleLabel.setFont(FONT_TITLE);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        topPanel.add(backButton, BorderLayout.WEST);
        topPanel.add(titleLabel, BorderLayout.CENTER);
        topPanel.add(Box.createHorizontalStrut(backButton.getPreferredSize().width), BorderLayout.EAST);

        JScrollPane scrollPane = new JScrollPane(chatPane);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        JPanel bottomPanel = new JPanel(new BorderLayout(10, 0));
        bottomPanel.setOpaque(false);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        bottomPanel.add(inputField, BorderLayout.CENTER);
        bottomPanel.add(sendButton, BorderLayout.EAST);

        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void setupListeners() {
        sendButton.addActionListener(e -> sendMessage());

        inputField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    sendMessage();
                }
            }
        });

        backButton.addActionListener(e -> goBackToDashboard());
    }

    private void addInitialMessage() {
         if (apiKeyInitialized) {
              appendMessage("AI", "Hello! Ask me about your trip planning.", true);
         } else {
              appendMessage("System", "AI features unavailable. Please set the GEMINI_API_KEY environment variable and restart.", true);
         }
    }

    private void sendMessage() {
        String userText = inputField.getText().trim();
        if (userText.isEmpty() || !apiKeyInitialized) {
            return;
        }

        appendMessage("You", userText, false);
        inputField.setText("");

        inputField.setEnabled(false);
        sendButton.setEnabled(false);
        appendMessage("AI", "Thinking...", true);

        SwingWorker<String, Void> worker = new SwingWorker<>() {
            @Override
            protected String doInBackground() throws Exception {
                
                ObjectNode textPart = objectMapper.createObjectNode();
                textPart.put("text", userText);

                ArrayNode partsArray = objectMapper.createArrayNode();
                partsArray.add(textPart);//+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

                ObjectNode content = objectMapper.createObjectNode();
                content.set("parts", partsArray);


                ArrayNode contentsArray = objectMapper.createArrayNode();
                contentsArray.add(content);

                ObjectNode payload = objectMapper.createObjectNode();
                payload.set("contents", contentsArray);



                String jsonRequestBody = objectMapper.writeValueAsString(payload);


                HttpClient httpClient = HttpClient.newBuilder()
                                                 .connectTimeout(Duration.ofSeconds(20))
                                                 .build();

                String apiUrl = String.format(API_ENDPOINT_FORMAT, GEMINI_MODEL, URLEncoder.encode(apiKey, StandardCharsets.UTF_8));

                HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(apiUrl))
                    .timeout(Duration.ofSeconds(30)) // timeout
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonRequestBody, StandardCharsets.UTF_8))
                    .build();

        
                try {
                    HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

                    
                    if (response.statusCode() == 200) {
                        String responseBody = response.body();
                        try {
                            JsonNode rootNode = objectMapper.readTree(responseBody);

                            JsonNode candidates = rootNode.path("candidates");
                            if (candidates.isArray() && !candidates.isEmpty()) {
                                JsonNode firstCandidate = candidates.get(0);
                                JsonNode contentNode = firstCandidate.path("content");
                                JsonNode partsNode = contentNode.path("parts");
                                if (partsNode.isArray() && !partsNode.isEmpty()) {
                                    return partsNode.get(0).path("text").asText("Sorry, I received an empty response part.");
                                }
                            }

                            if (candidates.isArray() && !candidates.isEmpty() && candidates.get(0).has("finishReason") && !"STOP".equals(candidates.get(0).path("finishReason").asText())) {
                                return "Response blocked by API: " + candidates.get(0).path("finishReason").asText();
                            }
                            return "Sorry, I couldn't extract a valid response from the API.";
                        } catch (JsonProcessingException jsonEx) {
                             System.err.println("Error parsing API JSON response: " + jsonEx.getMessage());
                             return "Error: Could not understand the API's response format.";
                        }
                    } else {
                        System.err.println("API Error: Status Code " + response.statusCode());
                        System.err.println("Response Body: " + response.body());
                        return "Error: Failed to get response from AI service (Status: " + response.statusCode() + "). Check API key or console logs.";
                    }
                } catch (IOException | InterruptedException httpEx) {
                    System.err.println("Error sending request to API: " + httpEx.getMessage());
                    return "Error: Could not connect to the AI service. Check your internet connection.";
                }
            }

            @Override
            protected void done() {
                try {
                    String aiResponse = get();
                    removeLastMessage();
                    appendMessage("AI", aiResponse, true);
                } catch (Exception e) {
                    removeLastMessage();
                    System.err.println("Error getting result from SwingWorker: " + e.getMessage());
                    e.printStackTrace();
                    appendMessage("AI", "Error retrieving AI response.", true);
                } finally {
                    inputField.setEnabled(true);
                    sendButton.setEnabled(true);
                    inputField.requestFocusInWindow();
                }
            }
        };
        worker.execute();
    }

     private void appendMessage(String sender, String message, boolean isLeftAligned) {
        SimpleAttributeSet alignment = new SimpleAttributeSet();
        SimpleAttributeSet normal = new SimpleAttributeSet();

        if (isLeftAligned) {
            StyleConstants.setAlignment(alignment, StyleConstants.ALIGN_LEFT);
            StyleConstants.setForeground(alignment, getAIColor());
            StyleConstants.setBold(alignment, true);

            StyleConstants.setAlignment(normal, StyleConstants.ALIGN_LEFT);
            StyleConstants.setForeground(normal, getAIColor());
            StyleConstants.setBold(normal, false);
        } else {
            StyleConstants.setAlignment(alignment, StyleConstants.ALIGN_RIGHT);
            StyleConstants.setForeground(alignment, getUserColor());
            StyleConstants.setBold(alignment, true);

            StyleConstants.setAlignment(normal, StyleConstants.ALIGN_RIGHT);
            StyleConstants.setForeground(normal, getUserColor());
            StyleConstants.setBold(normal, false);
        }

        try {
            int offsetBeforeInsert = styledDoc.getLength();
            styledDoc.insertString(offsetBeforeInsert, sender + ":\n" + message + "\n\n", normal);
            styledDoc.setParagraphAttributes(offsetBeforeInsert, styledDoc.getLength() - offsetBeforeInsert, normal, false);
            styledDoc.setCharacterAttributes(offsetBeforeInsert, sender.length() + 1, alignment, true);
            chatPane.setCaretPosition(styledDoc.getLength());
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    private void removeLastMessage() {
        try {
            Element root = styledDoc.getDefaultRootElement();
            if (root.getElementCount() >= 4) {
                Element thinkingSenderParagraph = root.getElement(root.getElementCount() - 4);
                int startOffset = thinkingSenderParagraph.getStartOffset();
                 int lengthToRemove = styledDoc.getLength() - startOffset;
                 if (startOffset >= 0 && lengthToRemove > 0) {
                      styledDoc.remove(startOffset, lengthToRemove);
                 }
            } else if (styledDoc.getLength() > 0) {
                 styledDoc.remove(0, styledDoc.getLength());
            }
        } catch (BadLocationException e) {
             System.err.println("Error removing last message: " + e.getMessage());
             chatPane.setText("");
        }
    }

    private void goBackToDashboard() {
        mainFrame.getContentPane().removeAll();
        mainFrame.getContentPane().add(dashboardPanel);
        mainFrame.getContentPane().revalidate();
        mainFrame.getContentPane().repaint();
    }

    private void applyTheme() {
         boolean isDark = preferences.getTheme().equals("Dark");

         Color bgColor = isDark ? new Color(35, 35, 45) : new Color(245, 248, 255); // 16, 38, 95   245, 248, 255
         Color chatBg = isDark ? new Color(45, 45, 58) : new Color(147, 170, 232); //Color.WHITE
         Color inputBg = isDark ? new Color(55, 55, 70) : Color.WHITE;  //Color.WHITE
         Color fgColor = isDark ? new Color(230, 230, 230) : Color.BLACK;
         Color buttonBg = isDark ? new Color(80, 140, 190) : new Color(16, 38, 95);  // 60, 130, 190
         Color buttonFg = Color.WHITE;
         Color borderColor = isDark ? new Color(75, 75, 85) : new Color(7, 16, 41); // 7, 16, 41    215, 215, 220

         setBackground(bgColor);

         chatPane.setBackground(chatBg);
         chatPane.setForeground(fgColor);
         chatPane.setCaretColor(fgColor);
         chatPane.setSelectionColor(buttonBg);
         chatPane.setSelectedTextColor(buttonFg);

         JScrollPane scrollPane = (JScrollPane) chatPane.getParent().getParent();
         scrollPane.setBorder(BorderFactory.createLineBorder(borderColor));
         scrollPane.getViewport().setBackground(chatBg);

         inputField.setBackground(inputBg);
         inputField.setForeground(fgColor);
         inputField.setCaretColor(fgColor);
         inputField.setBorder(new CompoundBorder(
                 new LineBorder(borderColor),
                 new EmptyBorder(5, 8, 5, 8)
         ));

         sendButton.setBackground(buttonBg);
         sendButton.setForeground(buttonFg);
         sendButton.setOpaque(true);
         sendButton.setBorderPainted(false);
         sendButton.setFocusPainted(false);

         backButton.setBackground(buttonBg);
         backButton.setForeground(buttonFg);
         backButton.setOpaque(true);
         backButton.setBorderPainted(false);
         backButton.setFocusPainted(false);

         JPanel topPanel = (JPanel) getComponent(0);
         for (Component c : topPanel.getComponents()) {
             if (c instanceof JLabel) {
                 c.setForeground(fgColor);
             }
         }
         chatPane.setForeground(fgColor);
    }

    private Color getAIColor() {
        boolean isDark = preferences.getTheme().equals("Dark");
        return isDark ? new Color(160, 210, 255) : Color.BLACK; // 0, 100, 180 white
    }

    private Color getUserColor() {
        boolean isDark = preferences.getTheme().equals("Dark");
        return isDark ? new Color(180, 230, 180) : Color.BLACK; //white
    }

    public void focusInput() {
         inputField.requestFocusInWindow();
    }
}