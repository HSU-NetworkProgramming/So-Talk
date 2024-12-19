package client;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;

/**
 * 홈 화면 패널
 * 메뉴 및 채팅방 리스트를 표시 (채팅방 전체보기)
 */
public class HomePanel extends JPanel {

    private JPanel menuPanel; // 홈 화면 중 메뉴바 패널
    private JPanel ChattingRoomListPanel; // 홈 화면 중 채팅방 목록 패널

    public HomePanel(MessengerFrame frame, String currentUserId) {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        String[] chatRoomArray = {"김혜진", "양인서", "강다현", "정예빈"};

        // 사용자 정보 매핑 (id -> 이름)
        HashMap<String, String> userMap = new HashMap<>();
        userMap.put("1", "김혜진");
        userMap.put("2", "이주연");
        userMap.put("3", "강다현");
        userMap.put("4", "정예빈");
        userMap.put("5", "양인서");

        // 이미지 아이콘 생성 및 크기 조정
        ImageIcon profileImg = new ImageIcon("src/assets/profile.jpg");
        ImageIcon PersonImg = new ImageIcon("src/assets/Person.png");
        ImageIcon SpeechBubbleImg = new ImageIcon("src/assets/SpeechBubble.png");
        ImageIcon ellipsisImg = new ImageIcon("src/assets/Ellipsis.png");

        ImageIcon blackPersonImg = new ImageIcon("src/assets/Person2.png");
        ImageIcon blackSpeechBubbleImg = new ImageIcon("src/assets/SpeechBubble2.png");
        ImageIcon blackEllipsisImg = new ImageIcon("src/assets/Ellipsis2.png");

        Image scaledProfileImg = profileImg.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH);
        Image scaledPersonImg = PersonImg.getImage().getScaledInstance(25, 25, Image.SCALE_SMOOTH);
        Image scaledSpeechBubbleImg = SpeechBubbleImg.getImage().getScaledInstance(25, 25, Image.SCALE_SMOOTH);
        Image scaledEllipsisImg = ellipsisImg.getImage().getScaledInstance(25, 25, Image.SCALE_SMOOTH);

        Image scaledBlackPersonImg = blackPersonImg.getImage().getScaledInstance(25, 25, Image.SCALE_SMOOTH);
        Image scaledBlackSpeechBubbleImg = blackSpeechBubbleImg.getImage().getScaledInstance(25, 25, Image.SCALE_SMOOTH);
        Image scaledBlackEllipsisImg = blackEllipsisImg.getImage().getScaledInstance(25, 25, Image.SCALE_SMOOTH);

        JLabel profileImgLabel = new JLabel(new ImageIcon(scaledProfileImg));
        JLabel personLabel = new JLabel(new ImageIcon(scaledPersonImg));
        JLabel speechBubbleLabel = new JLabel(new ImageIcon(scaledSpeechBubbleImg));
        JLabel ellipsisLabel = new JLabel(new ImageIcon(scaledEllipsisImg));

        JLabel blackPersonLabel = new JLabel(new ImageIcon(scaledBlackPersonImg));
        JLabel blackSpeechBubbleLabel = new JLabel(new ImageIcon(scaledBlackSpeechBubbleImg));
        JLabel blackEllipsisLabel = new JLabel(new ImageIcon(scaledBlackEllipsisImg));

        profileImgLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // 현재 사용자를 제외한 채팅방 리스트 생성
        JPanel chattingRoomListPanel = new JPanel();
        chattingRoomListPanel.setLayout(new BoxLayout(chattingRoomListPanel, BoxLayout.Y_AXIS));
        chattingRoomListPanel.setBackground(new Color(0xFFFFFF));

        for (String userId : userMap.keySet()) {
            if (!userId.equals(currentUserId)) { // 현재 로그인한 사용자는 제외
                String chatRoomName = userMap.get(userId);

                JButton chatRoomButton = new JButton(chatRoomName);
                chatRoomButton.setBackground(new Color(0xFFFFFF));
                chatRoomButton.setIcon(new ImageIcon(scaledProfileImg)); // 버튼에 아이콘 추가
                chatRoomButton.setHorizontalAlignment(SwingConstants.LEFT); // 텍스트와 아이콘을 왼쪽 정렬
                chatRoomButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));

                chatRoomButton.addActionListener(e -> {
                    try {
                        DataOutputStream os = frame.getOutputStream();
                        os.writeUTF("CHAT_WITH:" + userId); // 서버에 상대방 ID 전송
                        frame.setChattingPartner(chatRoomName); // 상대방 이름 설정
                        frame.showChattingRoomPanel();
                    } catch (IOException ex) {
                        JOptionPane.showMessageDialog(this, "서버와 연결할 수 없습니다.");
                        ex.printStackTrace();
                    }
                });

                chattingRoomListPanel.add(chatRoomButton);
            }
        }

        // 메뉴 패널 생성
        menuPanel = new JPanel();
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
        menuPanel.setBackground(new Color(0xEDEDED));
        menuPanel.setPreferredSize(new Dimension(80, 0)); // 너비 80, 높이는 자동 설정

        // 컴포넌트 추가 전에 중앙 정렬 설정
        personLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        personLabel.setBorder(new EmptyBorder(40, 0, 0, 0));
        blackSpeechBubbleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        blackSpeechBubbleLabel.setBorder(new EmptyBorder(30, 0, 0, 0));
        ellipsisLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        ellipsisLabel.setBorder(new EmptyBorder(30, 0, 0, 0));

        // 컴포넌트 추가
        menuPanel.add(personLabel);
        menuPanel.add(blackSpeechBubbleLabel);
        menuPanel.add(ellipsisLabel);

        // 채팅방 리스트 패널
        ChattingRoomListPanel = new JPanel();
        ChattingRoomListPanel.setLayout(new BoxLayout(ChattingRoomListPanel, BoxLayout.Y_AXIS)); // 수직 정렬
        ChattingRoomListPanel.setBackground(new Color(0xFFFFFF));

        for (int i = 0; i < chatRoomArray.length; i++) {
            // 버튼 생성
            JButton chatRoom = new JButton(chatRoomArray[i]); // 버튼에 텍스트 추가
            chatRoom.setBackground(new Color(0xFFFFFF));

            // 버튼에 이미지 추가
            chatRoom.setIcon(new ImageIcon(scaledProfileImg)); // 버튼에 아이콘 추가
            chatRoom.setHorizontalAlignment(SwingConstants.LEFT); // 텍스트와 아이콘을 왼쪽 정렬

            chatRoom.addActionListener(e -> {
                frame.showChattingRoomPanel();
            });

            // 버튼 크기 및 정렬 설정
            chatRoom.setAlignmentX(Component.CENTER_ALIGNMENT); // 중앙 정렬
            chatRoom.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60)); // 너비를 패널 크기에 맞추고 높이는 60px
            ChattingRoomListPanel.add(chatRoom);
        }

        // 홈 화면 패널에 추가
        add(menuPanel, BorderLayout.WEST); // 메뉴 패널은 왼쪽에 배치
        add(ChattingRoomListPanel, BorderLayout.CENTER); // 채팅방 리스트는 중앙에 배치
        add(menuPanel, BorderLayout.WEST);
        add(chattingRoomListPanel, BorderLayout.CENTER);
    }
}
