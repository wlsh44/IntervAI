package wlsh.project.intervai.profile.infra;

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

@Getter
@Entity
@Table(name = "tech_stacks")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TechStackEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String name;

    private TechStackEntity(String name) {
        this.name = name;
    }

    public static TechStackEntity of(String name) {
        return new TechStackEntity(name);
    }
}
