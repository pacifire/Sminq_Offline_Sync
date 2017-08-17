package offline_test.sminq.com.sminq_offlinelisting_test.utils;

import org.jetbrains.annotations.NotNull;

/**
 * Created by Pawan on 17/08/17.
 */

public class AppUtils {

    public static String capitalizeSentence(@NotNull String originalSentence){

        String upperString = null;
        if(originalSentence != null)
            if(!originalSentence.isEmpty())//Not condition....
                upperString = originalSentence.substring(0,1).toUpperCase() + originalSentence.substring(1);

        return upperString;
    }//capitalizeSentence closes here....
}//AppUtils closes here....
