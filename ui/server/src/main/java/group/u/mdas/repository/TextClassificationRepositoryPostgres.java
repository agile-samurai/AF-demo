package group.u.mdas.repository;

import group.u.mdas.models.entity.TextClassificationPostgres;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TextClassificationRepositoryPostgres extends JpaRepository<TextClassificationPostgres, Long> {}
