import java.util.Map;

/**
 * @author: yaronsh
 * @since: {version}
 * @date: 06/10/2018.
 */
public class MyMessage {

    private int id;
    private String subject;
    private String from;
    private String to;
    private String date;
    private String snippet;
    private String body;

    public MyMessage(String subject, String from, String to, String date, String snippet, String body) {
        this.id = 0;
        this.subject = subject;
        this.from = from;
        this.to = to;
        this.date = date;
        this.snippet = snippet;
        this.body = body;
    }

    public MyMessage(Map<String, String> messageParts) {
        this(messageParts.get(MessagePartsNames.SUBJECT.getName()),
                messageParts.get(MessagePartsNames.FROM.getName()),
                messageParts.get(MessagePartsNames.TO.getName()),
                messageParts.get(MessagePartsNames.DATE.getName()),
                messageParts.get(MessagePartsNames.SNIPPET.getName()),
                messageParts.get(MessagePartsNames.BODY.getName()));
    }

    public String getSnippet() {
        return snippet;
    }

    public void setSnippet(String snippet) {
        this.snippet = snippet;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
