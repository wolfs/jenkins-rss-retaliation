package com.tngtech.jenkins.notification

import javazoom.jl.player.Player
import org.apache.commons.codec.Charsets
import org.junit.Test

class TtsTest {

    @Test
    void sound_is_played() {
        URL url = new URL("http://translate.google.com/translate_tts?tl=de&q=" + URLEncoder.encode("Das Auto ist kaputt", Charsets.UTF_8.toString()))
//                URLCONSTANTS.GOOGLE_TRANSLATE_AUDIO + "q="
//                + text.replace(" ", "%20") + "&tl=" + languageOutput);
        URLConnection urlConn = url.openConnection();
        urlConn.addRequestProperty("User-Agent",
                "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.0)");
        InputStream audioSrc = urlConn.getInputStream();
        new Player(new BufferedInputStream(audioSrc)).play()
    }

}
