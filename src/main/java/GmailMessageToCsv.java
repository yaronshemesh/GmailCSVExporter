import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.GmailScopes;
import com.google.api.services.gmail.model.*;
import com.google.api.client.repackaged.org.apache.commons.codec.binary.Base64;
import com.google.common.collect.*;

import java.io.*;
import java.security.GeneralSecurityException;
import java.util.*;

public class GmailMessageToCsv {
    private static final String APPLICATION_NAME = "Gmail API Java Quickstart";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final String TOKENS_DIRECTORY_PATH = "tokens";
    private static final String CSV_SEPARATOR = ",";
    private static boolean includeRawMessage = false;
    /**
     * Global instance of the scopes required by this quickstart.
     * If modifying these scopes, delete your previously saved tokens/ folder.
     */
    private static final List<String> SCOPES = Lists.newArrayList(GmailScopes.MAIL_GOOGLE_COM, GmailScopes.GMAIL_MODIFY, GmailScopes.GMAIL_READONLY);
    private static final List<String> SUPPORTED_HEADERS = Lists.newArrayList(MessagePartsNames.DATE.getName(), MessagePartsNames.TO.getName(), MessagePartsNames.FROM.getName(), MessagePartsNames.SUBJECT.getName());
    private static final String CREDENTIALS_FILE_PATH = "/credentials.json";

    /**
     * Creates an authorized Credential object.
     *
     * @param HTTP_TRANSPORT The network HTTP Transport.
     * @return An authorized Credential object.
     * @throws IOException If the credentials.json file cannot be found.
     */
    private static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException {
        // Load client secrets.
        InputStream in = GmailMessageToCsv.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .build();
        return new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");
    }

    public static void main(String... args) throws IOException, GeneralSecurityException {
        // Build a new authorized API client service.
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        Gmail service = new Gmail.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                .setApplicationName(APPLICATION_NAME)
                .build();

        String user = "me";
        String queryString = args[0];
        String csvFileNAme = args[1];
        List<Message> messages = listMessagesMatchingQuery(service, user, queryString);
        List<MyMessage> myMessages = new ArrayList<>();
        if (messages.isEmpty()) {
            System.out.println("No messages found.");
        } else {
            System.out.println(messages.size() + " messages found.");
            for (Message message : messages) {
                String messageId = message.getId();
                Message msg = service.users().messages().get(user, messageId).execute();
                String body = "";
                if (includeRawMessage) {
                    Message rawMsg = service.users().messages().get(user, messageId).setFormat("raw").execute();
                    Base64 base64Url = new Base64(true);
                    byte[] emailBytes = base64Url.decodeBase64(rawMsg.getRaw());
                    body = new String(emailBytes);
                }

                List<MessagePartHeader> headers = msg.getPayload().getHeaders();
                Map<String, String> messageParts = new HashMap<>();
                messageParts.put(MessagePartsNames.SNIPPET.getName(), msg.getSnippet());
                messageParts.put(MessagePartsNames.BODY.getName(), body);
                for (MessagePartHeader header : headers) {
                    if (SUPPORTED_HEADERS.contains(header.getName())) {
                        messageParts.put(header.getName(), header.getValue());
                    }
                }
                myMessages.add(new MyMessage(messageParts));
            }
            writeToCsv(myMessages, csvFileNAme);
        }
    }

    private static void writeToCsv(Collection<MyMessage> messages, String csvFileNAme) {
        try {
            int i = 1;
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(csvFileNAme), "UTF-8"));
            StringBuffer oneLine = new StringBuffer();
            oneLine.append("Message ID");
            oneLine.append(CSV_SEPARATOR);
            oneLine.append("Message " + MessagePartsNames.SUBJECT.getName());
            oneLine.append(CSV_SEPARATOR);
            oneLine.append("Message " + MessagePartsNames.FROM.getName());
            oneLine.append(CSV_SEPARATOR);
            oneLine.append("Message " + MessagePartsNames.TO.getName());
            oneLine.append(CSV_SEPARATOR);
            oneLine.append("Message " + MessagePartsNames.DATE.getName());
            oneLine.append(CSV_SEPARATOR);
            oneLine.append("Message Snippet");
            oneLine.append(CSV_SEPARATOR);
            oneLine.append("Message Body");
            bw.write(oneLine.toString());
            bw.newLine();
            for (MyMessage message : messages) {
                oneLine = new StringBuffer();
                message.setId(i++);
                oneLine.append(message.getId());
                oneLine.append(CSV_SEPARATOR);
                oneLine.append(message.getSubject().replace(CSV_SEPARATOR, " "));
                oneLine.append(CSV_SEPARATOR);
                oneLine.append(message.getFrom().replace(CSV_SEPARATOR, " "));
                oneLine.append(CSV_SEPARATOR);
                oneLine.append(message.getTo().replace(CSV_SEPARATOR, " "));
                oneLine.append(CSV_SEPARATOR);
                oneLine.append(message.getDate().replace(CSV_SEPARATOR, " "));
                oneLine.append(CSV_SEPARATOR);
                oneLine.append(message.getSnippet().replace(CSV_SEPARATOR, " "));
                oneLine.append(CSV_SEPARATOR);
                oneLine.append(message.getBody().replace(CSV_SEPARATOR, " "));
                bw.write(oneLine.toString());
                bw.newLine();
            }
            bw.flush();
            bw.close();
        } catch (UnsupportedEncodingException e) {
        } catch (FileNotFoundException e) {
        } catch (IOException e) {
        }
    }

    public static List<Message> listMessagesMatchingQuery(Gmail service, String userId, String query) throws IOException {
        ListMessagesResponse response = service.users().messages().list(userId).setQ(query).execute();

        List<Message> messages = new ArrayList<>();
        while (response.getMessages() != null) {
            messages.addAll(response.getMessages());
            if (response.getNextPageToken() != null) {
                String pageToken = response.getNextPageToken();
                response = service.users().messages().list(userId).setQ(query).setPageToken(pageToken).execute();
            } else {
                break;
            }
        }

        for (Message message : messages) {
            System.out.println(message.toPrettyString());
        }

        return messages;
    }
}
