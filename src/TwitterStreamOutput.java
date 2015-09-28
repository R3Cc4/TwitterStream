import oauth.signpost.OAuthConsumer;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.*;

/**
 * A hacky little class illustrating how to receive and store Twitter streams
 * for later analysis, requires Apache Commons HTTP Client 4+. Stores the data
 * in 64MB long JSON files.
 *
 * Usage:
 *
 * TwitterConsumer t = new TwitterConsumer("username", "password",
 *      "http://stream.twitter.com/1/statuses/sample.json", "sample");
 * t.start();
 */
public class TwitterStreamOutput extends Thread {
    //
    static String STORAGE_DIR = "C:\\Idea Workspace\\TwitterStream\\";
    static long BYTES_PER_FILE = 64 * 1024 * 1024;
    //
    public long Messages = 0;
    public long Bytes = 0;
    public long Timestamp = 0;

    private String accessToken = "";
    private String accessSecret = "";
    private String consumerKey = "";
    private String consumerSecret = "";

    private String feedUrl;
    private String filePrefix;
    boolean isRunning = true;
    File file = null;
    FileWriter fw = null;
    long bytesWritten = 0;

    public static void main(String[] args) {
        TwitterStreamOutput t = new TwitterStreamOutput(
                "50261659-eiXGY3bF2cPO4DuyrLcWJUFAxTnbxSF4Xtkj18NYr",
                "WPRd0cWut97MIlv0TVaKiFPMsTa9dxdVqY7k93BqMXk9Q",
                "PLFQ1TPLT6P58czLCI37eCrUz",
                "byeDvMlcesKzfDXVIx99ae7qne0d4OrlVgm4LnsDTP28fZHQPX",
                "https://stream.twitter.com/1/statuses/sample.json", "sample");
        t.start();
    }

    public TwitterStreamOutput(String accessToken, String accessSecret, String consumerKey, String consumerSecret, String url, String prefix) {
        this.accessToken = accessToken;
        this.accessSecret = accessSecret;
        this.consumerKey = consumerKey;
        this.consumerSecret = consumerSecret;
        feedUrl = url;
        filePrefix = prefix;
        Timestamp = System.currentTimeMillis();
    }

    /**
     * @throws IOException
     */
    private void rotateFile() throws IOException {
        // Handle the existing file
        if (fw != null)
            fw.close();
        // Create the next file
        file = new File(STORAGE_DIR, filePrefix + "-"
                + System.currentTimeMillis() + ".json");
        bytesWritten = 0;
        fw = new FileWriter(file);
        System.out.println("Writing to " + file.getAbsolutePath());
    }

    /**
     * @see java.lang.Thread#run()
     */
    public void run() {
        // Open the initial file
        try { rotateFile(); } catch (IOException e) { e.printStackTrace(); return; }
        // Run loop
        while (isRunning) {
            try {

                OAuthConsumer consumer = new CommonsHttpOAuthConsumer(consumerKey, consumerSecret);
                consumer.setTokenWithSecret(accessToken, accessSecret);
                HttpGet request = new HttpGet(feedUrl);
                consumer.sign(request);

                DefaultHttpClient client = new DefaultHttpClient();
                HttpResponse response = client.execute(request);
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(response.getEntity().getContent()));
                while (true) {
                    String line = reader.readLine();
                    if (line == null)
                        break;
                    if (line.length() > 0) {
                        if (bytesWritten + line.length() + 1 > BYTES_PER_FILE)
                            rotateFile();
                        fw.write(line + "\n");
                        bytesWritten += line.length() + 1;
                        Messages++;
                        Bytes += line.length() + 1;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println("Sleeping before reconnect...");
            try { Thread.sleep(15000); } catch (Exception e) { }
        }
    }
}
