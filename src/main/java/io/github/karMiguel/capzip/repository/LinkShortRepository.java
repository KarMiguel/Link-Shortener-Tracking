package io.github.karMiguel.capzip.repository;

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
public interface LinkShortRepository extends JpaRepository<LinkShort,Long> {
    LinkShort findByShortLink(String shortLink);
    LinkShort findByLinkLongAndUser(String linkLong, Users user);
    LinkShort findByLinkLong(String linkLong);
    List<LinkShort> findAllByUserAndShortLinkNot(Users user, String shortLink);
    List<LinkShort> findByUser(Users user);
    @Query("SELECT ls.user FROM LinkShort ls WHERE ls.shortLink = :shortLink")
    Users findUserByShortLink(@Param("shortLink") String shortLink);

    @Query("SELECT ls, COUNT(c.id) AS qtdClick " +
            "FROM LinkShort ls " +
            "LEFT JOIN Click c ON ls.shortLink = c.linkShort.shortLink " +
            "WHERE ls.user = :user " +
            "GROUP BY ls " +
            "ORDER BY qtdClick DESC")
    Page<Object[]> findAllByUserOrderedByClickCount(@Param("user") Users user, Pageable pageable);

}
