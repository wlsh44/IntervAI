package wlsh.project.intervai.interview.infra;

import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Subquery;
import org.springframework.data.jpa.domain.Specification;
import wlsh.project.intervai.common.entity.EntityStatus;
import wlsh.project.intervai.interview.domain.InterviewListQuery;
import wlsh.project.intervai.session.infra.InterviewSessionEntity;

import java.util.ArrayList;
import java.util.List;

public class InterviewSpecification {

    public static Specification<InterviewEntity> of(Long userId, InterviewListQuery query) {
        return (root, criteriaQuery, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            predicates.add(cb.equal(root.get("userId"), userId));
            predicates.add(cb.equal(root.get("status"), EntityStatus.ACTIVE));

            if (query.interviewType() != null) {
                predicates.add(cb.equal(root.get("interviewType"), query.interviewType()));
            }

            if (query.startDate() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("createdAt"), query.startDate().atStartOfDay()));
            }

            if (query.endDate() != null) {
                predicates.add(cb.lessThan(root.get("createdAt"), query.endDate().plusDays(1).atStartOfDay()));
            }

            if (query.keyword() != null && !query.keyword().isBlank()) {
                Subquery<Long> topicSubquery = criteriaQuery.subquery(Long.class);
                var topicRoot = topicSubquery.from(InterviewCsSubjectEntity.class);
                topicSubquery.select(topicRoot.get("interviewId"))
                        .where(cb.and(
                                cb.equal(topicRoot.get("interviewId"), root.get("id")),
                                cb.equal(topicRoot.get("status"), EntityStatus.ACTIVE),
                                cb.like(cb.lower(topicRoot.get("topic")), "%" + query.keyword().toLowerCase() + "%")
                        ));
                predicates.add(cb.exists(topicSubquery));
            }

            if (query.status() != null) {
                Subquery<Long> sessionSubquery = criteriaQuery.subquery(Long.class);
                var sessionRoot = sessionSubquery.from(InterviewSessionEntity.class);
                sessionSubquery.select(sessionRoot.get("interviewId"))
                        .where(cb.and(
                                cb.equal(sessionRoot.get("interviewId"), root.get("id")),
                                cb.equal(sessionRoot.get("status"), EntityStatus.ACTIVE),
                                cb.equal(sessionRoot.get("sessionStatus"), query.status())
                        ));
                predicates.add(cb.exists(sessionSubquery));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
