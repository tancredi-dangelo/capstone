package tancredidangelo.heliosspaces.helpers;

import java.util.Set;

public class ForbiddenUsernamesList {

    public static final Set<String> FORBIDDEN_USERNAMES = Set.of(

            "about", "account", "accounts", "admin", "administrator", "api", "app", "auth",
            "billing", "blog", "cancel", "cart", "checkout", "config", "contact", "contact-us",
            "contactus", "dashboard", "details", "discover", "download", "downloads", "edit",
            "faq", "faqs", "feed", "feeds", "forum", "help", "home", "info", "jobs", "legal",
            "login", "logout", "me", "new", "news", "notifications", "oauth", "orders",
            "payment", "plans", "policy", "premium", "privacy", "profile", "profiles",
            "pricing", "register", "search", "settings", "shop", "signin", "signup",
            "status", "subscribe", "support", "terms", "upgrade", "user", "users", "welcome",


            "assets", "static", "public", "media", "images", "img", "css", "js", "javascript",
            "scripts", "styles", "stylesheets", "uploads", "avatar", "avatars", "files",
            "document", "documents", "data", "graphql", "rest", "v1", "v2", "v3",
            "rss", "xml", "json", "atom", "robot", "robots", "robots.txt", "sitemap",
            "sitemap.xml", "favicon", "favicon.ico", "well-known",


            "root", "sys", "system", "sysadmin", "superuser", "superadmin", "moderator",
            "mod", "operator", "ops", "staff", "team", "official", "verified", "security",
            "dev", "developer", "master", "null", "undefined", "void", "anonymous", "guest",
            "bot", "internal",


            "mail", "email", "smtp", "pop", "pop3", "imap", "ftp", "ssh", "ssl", "tls",
            "postmaster", "hostmaster", "webmaster", "abuse", "noc", "dns", "ns1", "ns2",
            "domain", "www", "www1", "www2", "local", "localhost"
    );



    public static boolean isReserved(String username) {
        if (username == null || username.isBlank()) {
            return false;
        }


        String cleanUsername = username.trim().toLowerCase();
        if (cleanUsername.startsWith("@")) {
            cleanUsername = cleanUsername.substring(1);
        }

        return FORBIDDEN_USERNAMES.contains(cleanUsername);
    }
}