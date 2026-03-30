package wlsh.project.intervai.profile.application;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import wlsh.project.intervai.common.entity.EntityStatus;
import wlsh.project.intervai.profile.infra.TechStackEntity;
import wlsh.project.intervai.profile.infra.TechStackRepository;

@Component
@RequiredArgsConstructor
public class TechStackManager {

    private final TechStackRepository techStackRepository;

    public List<TechStackEntity> findOrCreate(List<String> names) {
        List<TechStackEntity> existing = techStackRepository.findAllByNameInAndStatus(names, EntityStatus.ACTIVE);

        Set<String> existingNames = existing.stream()
                .map(TechStackEntity::getName)
                .collect(Collectors.toSet());

        List<TechStackEntity> newEntities = names.stream()
                .filter(name -> !existingNames.contains(name))
                .map(TechStackEntity::of)
                .toList();

        List<TechStackEntity> saved = techStackRepository.saveAll(newEntities);

        return Stream.concat(existing.stream(), saved.stream()).toList();
    }
}
