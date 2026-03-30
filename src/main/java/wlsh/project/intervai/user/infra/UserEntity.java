package wlsh.project.intervai.user.infra;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import wlsh.project.intervai.common.entity.BaseEntity;
import wlsh.project.intervai.user.domain.User;

@Getter
@Entity
@Table(name = "users")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String nickname;

    @Column(nullable = false)
    private String passwordHash;

    private UserEntity(String nickname, String passwordHash) {
        this.nickname = nickname;
        this.passwordHash = passwordHash;
    }

    public static UserEntity from(User user) {
        return new UserEntity(user.getNickname(), user.getPasswordHash());
    }

    public User toDomain() {
        return User.of(id, nickname, passwordHash);
    }
}
