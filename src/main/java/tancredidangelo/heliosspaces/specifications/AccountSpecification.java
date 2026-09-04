package tancredidangelo.heliosspaces.specifications;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import org.springframework.data.jpa.domain.Specification;
import tancredidangelo.heliosspaces.entities.person.account.stack.Account;
import tancredidangelo.heliosspaces.entities.person.user.stack.User;
import tancredidangelo.heliosspaces.entities.tag.Tag;

import java.util.List;

public class AccountSpecification {

    /// FILTER ONLY NON BANNED ACCOUNTS
    public static Specification<Account> isNotBanned() {
        return (root, query, cb) -> cb.isFalse(root.get("isBanned"));
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

            if (query.getResultType() != Long.class && query.getResultType() != long.class) {
                query.distinct(true);
            }

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
    public static Specification<Account> hasAllTagIds(List<Long> tagIds) {
        return (root, query, cb) -> {
            if (tagIds == null || tagIds.isEmpty()) {
                return cb.conjunction();
            }

            if (query.getResultType() != Long.class && query.getResultType() != long.class) {
                query.distinct(true);
            }

            Subquery<Long> subquery = query.subquery(Long.class);
            Root<Account> subRoot = subquery.from(Account.class);
            Join<Account, Tag> tagJoin = subRoot.join("tags", JoinType.INNER);

            subquery.select(subRoot.get("id"));
            subquery.where(
                    cb.equal(subRoot.get("id"), root.get("id")),
                    tagJoin.get("id").in(tagIds)
            );
            subquery.groupBy(subRoot.get("id"));
            subquery.having(cb.equal(cb.countDistinct(tagJoin.get("id")), (long) tagIds.size()));

            return cb.exists(subquery);
        };
    }

    /// COMBINATION METHOD (USER)
    public static Specification<Account> filterActiveAccounts(String country, String usernameMatch, List<Long> tagIds) {
        return Specification
                .where(isNotBanned())
                .and(usernameMatches(usernameMatch))
                .and(hasCountry(country))
                .and(hasAllTagIds(tagIds));
    }

    /// COMBINATION METHOD (ADMIN)
    public static Specification<Account> filterAccounts(String country, String usernameMatch, Boolean isBanned) {
        return Specification
                .where(isBanned(isBanned))
                .and(hasCountry(country))
                .and(usernameMatches(usernameMatch));
    }
}