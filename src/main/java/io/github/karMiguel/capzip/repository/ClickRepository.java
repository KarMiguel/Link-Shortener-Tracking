package io.github.karMiguel.capzip.repository;

import io.github.karMiguel.capzip.model.click.Click;
import io.github.karMiguel.capzip.model.linkShort.LinkShort;
import io.github.karMiguel.capzip.model.users.Users;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClickRepository extends JpaRepository<Click, Long> {

    @Query("SELECT c FROM Click c JOIN FETCH c.linkShort ls WHERE ls.shortLink = :shortLink")
    Page<Click> findByShortLink(@Param("shortLink") String shortLink, Pageable pageable);

    @Query("SELECT c.linkShort, COUNT(c.id) AS qtdClick " +
            "FROM Click c " +
            "GROUP BY c.linkShort " +
            "ORDER BY qtdClick DESC")
    Page<Object[]> findShortLinksOrderByClicks(Pageable pageable);
    @Query("SELECT COUNT(c) FROM Click c WHERE c.linkShort.shortLink = :shortLink")
    Long countClicksByShortLink(@Param("shortLink") String shortLink);

    List<Click> findByLinkShort(LinkShort linkShort);
    long countByLinkShort(LinkShort linkShort);

    @Query("SELECT c.localization, COUNT(c) as clickCount FROM Click c WHERE c.linkShort.shortLink = :shortLink AND c.linkShort.user = :user GROUP BY c.localization ORDER BY clickCount DESC")
    Page<Object[]> countClicksByCity(@Param("shortLink") String shortLink, @Param("user") Users user, Pageable pageable);

    @Query("SELECT " +
            "CASE " +
            "WHEN EXTRACT(HOUR FROM c.dateCreated) BETWEEN 6 AND 11 THEN 'Manhã' " +
            "WHEN EXTRACT(HOUR FROM c.dateCreated) BETWEEN 12 AND 17 THEN 'Tarde' " +
            "WHEN EXTRACT(HOUR FROM c.dateCreated) BETWEEN 18 AND 23 THEN 'Noite' " +
            "ELSE 'Madrugada' " +
            "END, COUNT(c.id) " +
            "FROM Click c " +
            "WHERE c.linkShort.shortLink = :shortLink " +
            "GROUP BY " +
            "CASE " +
            "WHEN EXTRACT(HOUR FROM c.dateCreated) BETWEEN 6 AND 11 THEN 'Manhã' " +
            "WHEN EXTRACT(HOUR FROM c.dateCreated) BETWEEN 12 AND 17 THEN 'Tarde' " +
            "WHEN EXTRACT(HOUR FROM c.dateCreated) BETWEEN 18 AND 23 THEN 'Noite' " +
            "ELSE 'Madrugada' " +
            "END")
    List<Object[]> countClicksByPeriod(@Param("shortLink") String shortLink);

    @Query("SELECT c FROM Click c WHERE c.linkShort.shortLink = :shortLink AND c.linkShort.user = :user")
    List<Click> findByShortLinkAndUser(@Param("shortLink") String shortLink, @Param("user") Users user);
    void deleteByLinkShort(LinkShort linkShort);

}
