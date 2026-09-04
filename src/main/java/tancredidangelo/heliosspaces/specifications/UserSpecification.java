package tancredidangelo.heliosspaces.specifications;

import org.springframework.data.jpa.domain.Specification;
import tancredidangelo.heliosspaces.entities.person.user.stack.User;
import java.time.LocalDate;


public class UserSpecification {

    /// FILTER BY FLAG TRUE/FALSE (ADMIN)
    public static Specification<User> flag(Boolean flagged) {

        return (root, query, cb) -> {
            if (flagged == null) {
                return cb.conjunction(); // Nessun filtro applicato se il parametro è null
            }
            return cb.equal(root.get("isFlagged"), flagged); // Sostituisci "isFlagged" col nome esatto del campo in User.java
        };
    }


    /// FILTER COUNTRY (ADMIN)
    public static Specification<User> hasCountry(String country) {
        return (root, query, cb) -> {
            if (country == null || country.trim().isEmpty()) {
                return cb.conjunction();
            }
            return cb.equal(cb.lower(root.get("country")), country.trim().toLowerCase());
        };
    }


    /// FILTER BY BIRTHDATE (ADMIN)
    public static Specification<User> hasBirthdate(LocalDate birthdate) {
        return (root, query, cb) -> {
            if (birthdate == null) {
                return cb.conjunction();
            }
            return cb.equal(root.get("birthdate"), birthdate);
        };
    }


    /// FILTER BY EMAIL MATCH
    public static Specification<User> emailMatches(String emailMatch) {
        return (root, query, cb) -> {
            if (emailMatch == null || emailMatch.trim().isEmpty()) {
                return cb.conjunction();
            }
            String pattern = "%" + emailMatch.trim().toLowerCase() + "%";
            return cb.like(cb.lower(root.get("email")), pattern);
        };
    }



    /// ------------ FINAL COMBINATION METHOD (ADMIN)-------------------------------------------------------

    public static Specification<User> filterUsers(String emailMatch, String country, LocalDate birthdate, Boolean isFlagged) {
        return Specification
                .where(flag(isFlagged))
                .and(emailMatches(emailMatch))
                .and(hasCountry(country))
                .and(hasBirthdate(birthdate));
    }


}
