
package tancredidangelo.capstone.specifications;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import org.springframework.data.jpa.domain.Specification;
import tancredidangelo.capstone.entities.person.account.stack.Account;
import tancredidangelo.capstone.entities.person.user.stack.User;
import tancredidangelo.capstone.entities.tag.Tag;

import java.util.List;

public class AccountSpecification {

    /// FILTER ONLY NON BANNED ACCOUNTS
    public static Specification<Account> isNotBanned() {
        return (root, query, cb) ->
                cb.isFalse(root.get("isBanned"));
    }


    /// FILTER ACCOUNTS BY BANNED OR NOT (ADMIN)
    public static Specification<Account> isBanned(Boolean banned) {
        return (root, query, cb) -> {
            if (banned == null) {
                return cb.conjunction();
            }
            return cb.equal(root.get("isBanned"), banned);
        };
    }


    /// FILTER COUNTRY
    public static Specification<Account> hasCountry(String country) {
        return (root, query, cb) -> {
            if (country == null || country.trim().isEmpty()) {
                return cb.conjunction();
            }

            // join User to get country
            Join<Account, User> userJoin = root.join("user", JoinType.INNER);
            return cb.equal(cb.lower(userJoin.get("country")), country.trim().toLowerCase());
        };
    }


    /// FILTER USERNAME MATCH
    public static Specification<Account> usernameMatches(String usernameMatch) {
        return (root, query, cb) -> {
            if (usernameMatch == null || usernameMatch.trim().isEmpty()) {
                return cb.conjunction();
            }
            String pattern = "%" + usernameMatch.trim().toLowerCase() + "%";
            return cb.like(cb.lower(root.get("username")), pattern);
        };
    }



    /// FILTER TAGS
    public static Specification<Account> hasAllTags(List<Tag> tags) {
        return (root, query, cb) -> {

            if (tags == null || tags.isEmpty()) {
                return cb.conjunction();
            }

            List<Long> tagIds = tags.stream()
                    .map(Tag::getId)
                    .toList();

            // create subquery for counting matching tags
            Subquery<Long> subquery = query.subquery(Long.class);
            Root<Account> subRoot = subquery.from(Account.class);
            Join<Account, Tag> tagJoin = subRoot.join("tags", JoinType.INNER);

            subquery.select(cb.countDistinct(tagJoin.get("id")));
            subquery.where(
                    cb.equal(subRoot.get("id"), root.get("id")),
                    tagJoin.get("id").in(tagIds)
            );

            return cb.equal(subquery, (long) tagIds.size());
        };
    }



    /// ------------ FINAL COMBINATION METHOD (USER + ADMIN)-------------------------------------------------------

    public static Specification<Account> filterActiveAccounts(String country, String usernameMatch, List<Tag> tags) {
        return Specification
                .where(isNotBanned())
                .and(usernameMatches(usernameMatch))
                .and(hasCountry(country))
                .and(hasAllTags(tags));
    }


    /// ------------ FINAL COMBINATION METHOD (ADMIN)-------------------------------------------------------

    public static Specification<Account> filterAccounts(String country, String usernameMatch, Boolean isBanned) {
        return Specification
                .where(isBanned(isBanned))
                .and(hasCountry(country))
                .and(usernameMatches(usernameMatch))
                ;
    }





}



