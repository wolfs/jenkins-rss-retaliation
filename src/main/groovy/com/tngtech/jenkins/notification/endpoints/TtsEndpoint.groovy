package com.tngtech.jenkins.notification.endpoints

import com.tngtech.jenkins.notification.model.BuildInfo
import com.tngtech.jenkins.notification.model.Config
import com.tngtech.jenkins.notification.model.TtsConfig
import javazoom.jl.player.Player
import org.apache.camel.Body
import org.apache.commons.codec.Charsets

class TtsEndpoint implements FeedbackEndpoint {

    private static final String GOOGLE_TRANSLATE_TTS = "http://translate.google.com/translate_tts"
    private TtsConfig config

    TtsEndpoint(TtsConfig config) {
        this.config = config
    }

    @Override
    void process(@Body BuildInfo buildInfo) throws Exception {
        String text = config.message.call(buildInfo)
        say(text)
    }

    public void say(String text) {
        URL url = new URL("${GOOGLE_TRANSLATE_TTS}?tl=${config.lang}&q=${URLEncoder.encode(text, Charsets.UTF_8.toString())}")
        URLConnection urlConn = url.openConnection();
        urlConn.addRequestProperty("User-Agent",
                "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.0)");
        InputStream audioSrc = urlConn.getInputStream();

        def player = new Player(new BufferedInputStream(audioSrc))
        player.play()

        while (!player.isComplete()) {
            sleep(1000)
        }

    }
}
