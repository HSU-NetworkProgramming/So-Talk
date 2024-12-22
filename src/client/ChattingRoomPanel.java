package client;

import api.GoogleTranslate;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * 채팅방 화면 패널 (채팅방 상세보기)
 * 메시지 송수신 처리
 */
public class ChattingRoomPanel extends JPanel {
    private JPanel chatContainer; // 채팅 메시지 패널
    private JScrollPane scrollPane;
    private JTextField messageInputField; // 채팅 입력 필드
    private DataOutputStream outputStream; // 서버로 메시지 전송을 위한 출력 스트림
    private String targetId;    // 메시지를 전송할 대상 ID
    private String targetName;  // 메시지를 전송할 대상 Name
    private JLabel currentUserLabel; // 상단에 현재 사용자 이름 표시

    // ---- 번역 관련 필드 ----
    private GoogleTranslate googleTranslate;
    private String targetLanguage = "ko"; // 기본 번역 언어: "ko"

    // 이미지 리소스 (경로는 필요 시 수정)
    private ImageIcon translateImg = new ImageIcon("src/assets/Translate.png");
    private ImageIcon languageImg  = new ImageIcon("src/assets/Language.png");
    private ImageIcon emoticonImg  = new ImageIcon("src/assets/Emoticon.png");
    private ImageIcon pictureImg   = new ImageIcon("src/assets/Picture.png");
    private ImageIcon robotImg     = new ImageIcon("src/assets/Robot.png");

    // 전송 버튼
    private JButton sendButton;

    public ChattingRoomPanel(MessengerFrame frame) {
        // GoogleTranslate 객체 초기화
        this.googleTranslate = new GoogleTranslate();

        setLayout(new BorderLayout());
        setBackground(new Color(0xB9CEE0)); // 상단/중앙 부분 배경

        // --------------------
        // 1) 상단 패널 (상대방 이름)
        // --------------------
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.setBackground(new Color(0xD6E4F2));
        topPanel.setPreferredSize(new Dimension(0, 50)); // 높이 50

        currentUserLabel = new JLabel("채팅중인 상대 이름: " + targetName);
        currentUserLabel.setFont(new Font("Pretendard", Font.PLAIN, 16));
        topPanel.add(currentUserLabel);

        // --------------------
        // 2) 중앙(채팅 메시지) 영역
        // --------------------
        chatContainer = new JPanel();
        // BoxLayout으로 세로 방향 나열
        chatContainer.setLayout(new BoxLayout(chatContainer, BoxLayout.Y_AXIS));
        chatContainer.setBackground(new Color(0xB9CEE0));
        // 상하좌우 여백
        chatContainer.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        scrollPane = new JScrollPane(chatContainer);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        // --------------------
        // 3) 하단 영역 (흰색 배경, 2줄)
        // --------------------
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.Y_AXIS));
        bottomPanel.setBackground(Color.WHITE);

        // (3-1) 첫 번째 줄: [입력창만 가로로 꽉 차게]
        JPanel inputRow = new JPanel(new BorderLayout());
        inputRow.setBackground(Color.WHITE);

        messageInputField = new JTextField();
        messageInputField.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        messageInputField.setFont(new Font("Arial", Font.PLAIN, 14));

        // 입력창 내용 변화 -> 전송 버튼 색상 변경
        messageInputField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { updateSendButtonColor(); }
            @Override
            public void removeUpdate(DocumentEvent e)  { updateSendButtonColor(); }
            @Override
            public void changedUpdate(DocumentEvent e) { updateSendButtonColor(); }
        });

        // 첫 번째 줄에 입력창만 배치
        inputRow.add(messageInputField, BorderLayout.CENTER);

        // (3-2) 두 번째 줄: 아이콘들(왼쪽) + 전송 버튼(오른쪽)
        JPanel iconsRow = new JPanel(new BorderLayout());
        iconsRow.setBackground(Color.WHITE);

        // 왼쪽 아이콘 패널
        JPanel leftIconsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        leftIconsPanel.setBackground(Color.WHITE);

        // --- 언어(드롭다운) 버튼 ---
        JButton languageButton = new JButton(
                new ImageIcon(languageImg.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH))
        );
        languageButton.setContentAreaFilled(false);
        languageButton.setBorderPainted(false);
        languageButton.setFocusPainted(false);
        languageButton.setOpaque(false);

        // 팝업 메뉴(언어 선택)
        JPopupMenu languageMenu = new JPopupMenu();
        JMenuItem menuItemKo = new JMenuItem("한국어(ko)");
        menuItemKo.addActionListener(e -> {
            this.targetLanguage = "ko";
            System.out.println("[Client] 번역 언어 변경 -> ko");
        });
        languageMenu.add(menuItemKo);

        JMenuItem menuItemEn = new JMenuItem("영어(en)");
        menuItemEn.addActionListener(e -> {
            this.targetLanguage = "en";
            System.out.println("[Client] 번역 언어 변경 -> en");
        });
        languageMenu.add(menuItemEn);

        JMenuItem menuItemJa = new JMenuItem("일본어(ja)");
        menuItemJa.addActionListener(e -> {
            this.targetLanguage = "ja";
            System.out.println("[Client] 번역 언어 변경 -> ja");
        });
        languageMenu.add(menuItemJa);

        languageButton.addActionListener(e -> {
            languageMenu.show(languageButton, 0, languageButton.getHeight());
        });
        leftIconsPanel.add(languageButton);

        // --- 이모티콘 버튼 ---
        JButton emoticonButton = new JButton(
                new ImageIcon(emoticonImg.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH))
        );
        styleIconButton(emoticonButton);
        emoticonButton.addActionListener(e -> {
            System.out.println("[Client] 이모티콘 버튼 클릭");
        });
        leftIconsPanel.add(emoticonButton);

        // --- 사진 버튼 ---
        JButton pictureButton = new JButton(
                new ImageIcon(pictureImg.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH))
        );
        styleIconButton(pictureButton);
        pictureButton.addActionListener(e -> {
            System.out.println("[Client] 사진 버튼 클릭");
        });
        leftIconsPanel.add(pictureButton);

        // --- 로봇 버튼 ---
        JButton robotButton = new JButton(
                new ImageIcon(robotImg.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH))
        );
        styleIconButton(robotButton);
        robotButton.addActionListener(e -> {
            System.out.println("[Client] 로봇 버튼 클릭");
        });
        leftIconsPanel.add(robotButton);

        iconsRow.add(leftIconsPanel, BorderLayout.WEST);

        // 전송 버튼 (오른쪽)
        sendButton = new JButton("전송");
        sendButton.setPreferredSize(new Dimension(80, 40));
        sendButton.setFont(new Font("Arial", Font.PLAIN, 14));
        sendButton.setBackground(Color.WHITE);
        sendButton.setContentAreaFilled(true);
        sendButton.setOpaque(true);
        // 클릭 시 sendMessage()
        sendButton.addActionListener(e -> sendMessage());

        iconsRow.add(sendButton, BorderLayout.EAST);

        bottomPanel.add(inputRow);
        bottomPanel.add(iconsRow);

        // --------------------
        // 메인 패널에 배치
        // --------------------
        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    // 아이콘/텍스트 버튼 공통 스타일
    private void styleIconButton(JButton button) {
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setOpaque(false);
        button.setFont(new Font("Arial", Font.PLAIN, 16));
    }

    // 전송 버튼 색상 변경 -> 전송 버튼 색이 아니라 주변 색이 바뀜.. 추후 수정 필요
    private void updateSendButtonColor() {
        String text = messageInputField.getText().trim();
        if (text.isEmpty()) {
            // 비어있으면 회색
//            sendButton.setBackground(Color.LIGHT_GRAY);
        } else {
            // 텍스트가 있으면 노란색(FFE700)
//            sendButton.setBackground(new Color(0xFFE700));
        }
    }

    // 현재 로그인한 사용자 이름 설정
    public void setCurrentUserName(String userName) {
        currentUserLabel.setText("로그인한 사용자: " + userName);
    }

    public void setOutputStream(DataOutputStream os) {
        this.outputStream = os;
    }

    // 채팅중인 상대 Id와 이름 설정
    public void setTargetIdAndName(String targetId, String targetName) {
        this.targetId = targetId;
        this.targetName = targetName;

        if (currentUserLabel != null) {
            currentUserLabel.setText(targetName);
        }
    }

    /**
     * 채팅 메시지를 화면에 추가
     * - 말풍선 아래 번역 버튼
     * - 번역하면 같은 말풍선 아래줄에 번역 결과 표시
     * - 각 말풍선 사이 5px 간격 -> 엉엉 이상해
     */
    public void updateChattingText(String sender, String message, boolean isMyMessage) {
        // 말풍선
        JPanel messageBubble = createMessageBubble(sender, message, isMyMessage);

        // (A) 말풍선을 감싸는 패널 bubbleWrapper
        // -> 아래쪽에 항상 5px 간격 주기
        JPanel bubbleWrapper = new JPanel();
        bubbleWrapper.setLayout(new BoxLayout(bubbleWrapper, BoxLayout.X_AXIS));
        // 말풍선 아래 5px 간격을 위해 border 사용
        bubbleWrapper.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
        bubbleWrapper.setOpaque(false);

        bubbleWrapper.add(messageBubble);
        chatContainer.add(bubbleWrapper);

        // 스크롤 항상 최하단 이동
        SwingUtilities.invokeLater(() -> {
            JScrollBar vertical = scrollPane.getVerticalScrollBar();
            vertical.setValue(vertical.getMaximum());
        });

        chatContainer.revalidate();
        chatContainer.repaint();
    }

    /**
     * 말풍선을 만들어 반환
     */
    private JPanel createMessageBubble(String sender, String message, boolean isMyMessage) {
        // 말풍선 자체
        JPanel messagePanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                // 안티에일리어싱
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // 배경색(내 메시지: 노란색 / 상대 메시지: 흰색)
                g2d.setColor(isMyMessage ? new Color(0xFFEB3B) : Color.WHITE);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
            }
        };
        messagePanel.setOpaque(false);
        messagePanel.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));

        // 말풍선 안에 여러 줄(원본 + 번역) 담을 컨테이너
        JPanel textContainer = new JPanel();
        textContainer.setLayout(new BoxLayout(textContainer, BoxLayout.Y_AXIS));
        textContainer.setOpaque(false);

        // 원본 메시지 라벨
        JLabel messageLabel = new JLabel("<html>" + message.replaceAll("\n", "<br>") + "</html>");
        messageLabel.setForeground(Color.BLACK);
        messageLabel.setMaximumSize(new Dimension(300, Integer.MAX_VALUE));
        textContainer.add(messageLabel);

        messagePanel.add(textContainer, BorderLayout.CENTER);

        // 번역 버튼
        JButton translateButton = new JButton(
                new ImageIcon(translateImg.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH))
        );
        translateButton.setPreferredSize(new Dimension(25, 25));
        translateButton.setContentAreaFilled(false);
        translateButton.setBorderPainted(false);
        translateButton.setFocusPainted(false);
        translateButton.addActionListener(e -> {
            try {
                // 번역
                String translatedText = googleTranslate.translate(message, targetLanguage);
                System.out.println("[Client] 번역 결과 (" + targetLanguage + "): " + translatedText);

                // 말풍선 아래줄에 번역 결과 추가
                JLabel translationLabel = new JLabel("<html>"
                        + translatedText.replaceAll("\n", "<br>")
                        + "</html>");
                translationLabel.setForeground(Color.GRAY);
                translationLabel.setBorder(BorderFactory.createEmptyBorder(3, 0, 0, 0));

                textContainer.add(translationLabel);
                textContainer.revalidate();
                textContainer.repaint();
            } catch (Exception ex) {
                System.out.println("[Client] 번역 중 오류 발생: " + ex.getMessage());
                ex.printStackTrace();
            }
        });

        // wrapper 패널: 말풍선 + 번역 버튼
        JPanel wrapper = new JPanel(new FlowLayout(isMyMessage ? FlowLayout.RIGHT : FlowLayout.LEFT, 5, 0));
        wrapper.setOpaque(false);

        if (isMyMessage) {
            wrapper.add(translateButton);
            wrapper.add(messagePanel);
        } else {
            wrapper.add(messagePanel);
            wrapper.add(translateButton);
        }

        return wrapper;
    }

    // 메시지 전송
    private void sendMessage() {
        String message = messageInputField.getText().trim();
        if (message.isEmpty()) {
            System.out.println("[Client] 빈 메시지는 전송되지 않습니다.");
            return;
        }

        try {
            updateChattingText("Me", message, true);

            if (targetId != null) {
                System.out.println("[Client] 메시지 전송: 대상 ID = " + targetId + ", 메시지 = " + message);
                outputStream.writeUTF("MESSAGE_TO_ID:" + targetId + ":" + message);
            } else {
                System.out.println("[Client] 대상 ID가 설정되지 않았습니다. 메시지를 전송할 수 없습니다.");
            }

            // 입력 필드 초기화
            messageInputField.setText("");
        } catch (IOException e) {
            System.out.println("[Client] 메시지 전송 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
