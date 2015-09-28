/**
 * Created by Vamsi on 9/28/2015.
 */
import twitter4j.*;
import twitter4j.conf.ConfigurationBuilder;

import java.util.List;

/**
 * @author Yusuke Yamamoto - yusuke at mac.com
 * @since Twitter4J 2.1.7
 */
public class TwitterHomeTimeline {
    /**
     * Usage: java twitter4j.examples.timeline.GetHomeTimeline
     *
     * @param args String[]
     */
    public static void main(String[] args) {
        try {
            ConfigurationBuilder cb = new ConfigurationBuilder();
            cb.setDebugEnabled(true);
            cb.setOAuthConsumerKey("PLFQ1TPLT6P58czLCI37eCrUz");
            cb.setOAuthConsumerSecret("byeDvMlcesKzfDXVIx99ae7qne0d4OrlVgm4LnsDTP28fZHQPX");
            cb.setOAuthAccessToken("50261659-eiXGY3bF2cPO4DuyrLcWJUFAxTnbxSF4Xtkj18NYr");
            cb.setOAuthAccessTokenSecret("WPRd0cWut97MIlv0TVaKiFPMsTa9dxdVqY7k93BqMXk9Q");
            TwitterFactory tf = new TwitterFactory(cb.build());

            // gets Twitter instance with default credentials
            Twitter twitter = tf.getInstance();
            User user = twitter.verifyCredentials();
            List<Status> statuses = twitter.getHomeTimeline();
            System.out.println("Showing @" + user.getScreenName() + "'s home timeline.");
            for (Status status : statuses) {
                System.out.println("@" + status.getUser().getScreenName() + " - " + status.getText());
            }
        } catch (TwitterException te) {
            te.printStackTrace();
            System.out.println("Failed to get timeline: " + te.getMessage());
            System.exit(-1);
        }
    }
}
