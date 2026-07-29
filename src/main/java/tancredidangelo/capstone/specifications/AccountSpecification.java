
package tancredidangelo.capstone.specifications;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import org.springframework.data.jpa.domain.Specification;
import tancredidangelo.capstone.entities.person.account.Account;
import tancredidangelo.capstone.entities.person.user.User;

import java.util.List;

public class AccountSpecification {

    /// FILTER ONLY NON BANNED ACCOUNTS
    public static Specification<Account> isNotBanned() {
        return (root, query, cb) ->
                cb.isFalse(root.get("isBanned"));
    }


    /// FILTER ONLY BANNED ACCOUNTS
    public static Specification<Account> isBanned() {
        return (root, query, cb) ->
                cb.isTrue(root.get("isBanned"));
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
    public static Specification<Account> hasAllTags(List<String> tags) {
        return (root, query, cb) -> {

            if (tags == null || tags.isEmpty()) {
                return cb.conjunction();
            }

            List<String> lowercaseTags = tags.stream()
                    .map(String::toLowerCase)
                    .toList();

            // create subquery for counting tags
            Subquery<Long> subquery = query.subquery(Long.class);
            Root<Account> subRoot = subquery.from(Account.class);
            Join<Account, String> tagJoin = subRoot.join("tags", JoinType.INNER);

            subquery.select(cb.countDistinct(tagJoin));
            subquery.where(
                    cb.equal(subRoot.get("id"), root.get("id")),
                    cb.lower(tagJoin).in(lowercaseTags)
            );

            return cb.equal(subquery, (long) lowercaseTags.size());
        };
    }



    /// ------------ FINAL COMBINATION METHOD (USER + ADMIN)-------------------------------------------------------

    public static Specification<Account> filterActiveAccounts(String country, String usernameMatch, List<String> tags) {
        return Specification
                .where(isNotBanned())
                .and(usernameMatches(usernameMatch))
                .and(hasCountry(country))
                .and(hasAllTags(tags));
    }


    /// ------------ FINAL COMBINATION METHOD (ADMIN)-------------------------------------------------------

    public static Specification<Account> filterBannedAccounts(String country, String usernameMatch) {
        return Specification
                .where(isBanned())
                .and(hasCountry(country))
                .and(usernameMatches(usernameMatch))
                ;
    }





}



