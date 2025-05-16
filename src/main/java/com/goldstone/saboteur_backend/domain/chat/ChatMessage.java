package com.goldstone.saboteur_backend.domain.chat;

import com.goldstone.saboteur_backend.domain.user.User;
import com.goldstone.saboteur_backend.domain.common.BaseEntity;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessage extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "sender_id")
    private User sender;

    private String content;

    private LocalDateTime timestamp;

    @ManyToOne
    @JoinColumn(name = "chat_manager_id")
    private ChatManager chatManager;

    @ElementCollection private List<String> BannedWords;

    public String maskInappropriate() {
        for (String word : BannedWords) {
            if (content.contains(word)) {
                content = content.replaceAll(word, "*".repeat(word.length()));
            }
        }
        return content;
    }

    public boolean isAppropriate() {
        for (String word : BannedWords) {
            if (content.contains(word)) {
                return false;
            }
        }
        return true;
    }
}
