/**
 * @author: yaronsh
 * @since: {version}
 * @date: 06/10/2018.
 */
public enum MessagePartsNames {
    SUBJECT("Subject"),
    FROM("From"),
    TO("To"),
    DATE("Date"),
    SNIPPET("Snippet"),
    BODY("Body");

    private final String name;

    MessagePartsNames(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
