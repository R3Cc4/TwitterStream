/**
 *
 * @author  Niharika Kunaparaju on 9/27/2015.
 * Using Twitter4j.examples.PrintSampleStream as reference
 */
import twitter4j.*;
import twitter4j.conf.ConfigurationBuilder;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public final class PrintSampleStream {
    public static final String filepath="C:\\Idea Workspace\\TwitterStream\\";
    public static final String filePrefix="Public Tweets";
    // This is 16MB. We check this and rotate file once the file reaches this size
    static long BYTES_PER_FILE = 16 * 1024 * 1024;

    private static long Messages = 0;
    private static long Bytes = 0;
    private static long Timestamp = 0;
    private static File file = null;
    private static FileWriter fw = null;
    private static long bytesWritten = 0;


    /**
     * @throws IOException
     */
    private static void changeFile() throws IOException {
        // Handle the existing file
        if (fw != null)
            fw.close();
        // Create the next file
        file = new File(filepath, filePrefix + "-"
                + System.currentTimeMillis() + ".txt");
        bytesWritten = 0;
        fw = new FileWriter(file);
        System.out.println("Writing to " + file.getAbsolutePath());
    }

    /**
     * Main entry of this application.
     *
     * @param args arguments doesn't take effect with this example
     * @throws TwitterException when Twitter service or network is unavailable
     */

    public static void main(String[] args) throws TwitterException {

        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setDebugEnabled(true);
        cb.setOAuthConsumerKey("PLFQ1TPLT6P58czLCI37eCrUz");
        cb.setOAuthConsumerSecret("byeDvMlcesKzfDXVIx99ae7qne0d4OrlVgm4LnsDTP28fZHQPX");
        cb.setOAuthAccessToken("50261659-eiXGY3bF2cPO4DuyrLcWJUFAxTnbxSF4Xtkj18NYr");
        cb.setOAuthAccessTokenSecret("WPRd0cWut97MIlv0TVaKiFPMsTa9dxdVqY7k93BqMXk9Q");
         try{
             changeFile();
         }
         catch (IOException ioe){
             ioe.printStackTrace();
         }

        TwitterStreamFactory tf = new TwitterStreamFactory(cb.build());
        TwitterStream twitterStream = tf.getInstance();

        StatusListener listener = new StatusListener() {
            @Override
            public void onStatus(Status status) {

                if(!status.isRetweet()) {
                    String statusMessage = "["+status.getCreatedAt().toString()+"]"+"@" + status.getUser().getScreenName() + " - " + status.getText();
                    System.out.println(statusMessage);
                    // Write These Status to a file.

                    try {
                        if (bytesWritten + statusMessage.length() + 1 > BYTES_PER_FILE)
                            changeFile();
                        fw.write(statusMessage + "\n");
                        bytesWritten += statusMessage.length() + 1;
                        Messages++;
                        Bytes += statusMessage.length() + 1;
                    } catch (IOException ioe) {
                        ioe.printStackTrace();
                    }
                }
            }

            @Override
            public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {
                System.out.println("Got a status deletion notice id:" + statusDeletionNotice.getStatusId());
            }

            @Override
            public void onTrackLimitationNotice(int numberOfLimitedStatuses) {
                System.out.println("Got track limitation notice:" + numberOfLimitedStatuses);
            }

            @Override
            public void onScrubGeo(long userId, long upToStatusId) {
                System.out.println("Got scrub_geo event userId:" + userId + " upToStatusId:" + upToStatusId);
            }

            @Override
            public void onStallWarning(StallWarning warning) {
                System.out.println("Got stall warning:" + warning);
            }

            @Override
            public void onException(Exception ex) {
                System.out.println("*************************Got Exception*************************\n");
                ex.printStackTrace();

            }
        };
        twitterStream.addListener(listener);
        twitterStream.sample();
    }
}

