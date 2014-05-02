package com.tngtech.jenkins.notification.endpoints

import com.tngtech.jenkins.notification.model.BuildHistory
import com.tngtech.jenkins.notification.model.TtsConfig
import javazoom.jl.player.Player
import org.apache.commons.codec.Charsets
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class TtsEndpoint extends BaseEndpoint {
    private static final Logger LOG = LoggerFactory.getLogger(TtsEndpoint)
    private static final String GOOGLE_TRANSLATE_TTS = 'http://translate.google.com/translate_tts'
    private TtsConfig config

    TtsEndpoint(TtsConfig config) {
        this.config = config
    }

    @Override
    void process(BuildHistory buildHistory) throws Exception {
        if (buildHistory.hasResultChanged()) {
            String text = config.message.call(buildHistory.currentBuild)
            if (text) {
                LOG.info("Saying: '${text}'")
                say(text)
            }
        }
    }

    @SuppressWarnings('BusyWait')
    void say(String text) {
        URL url = new URL("${GOOGLE_TRANSLATE_TTS}?" +
                "tl=${config.lang}&" +
                "q=${URLEncoder.encode(text, Charsets.UTF_8.toString())}")
        URLConnection urlConn = url.openConnection()
        urlConn.addRequestProperty('User-Agent',
                'Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.0)')
        InputStream audioSrc = urlConn.inputStream

        def player = new Player(new BufferedInputStream(audioSrc))
        player.play()

        while (!player.isComplete()) {
            sleep(1000)
        }

        player.close()
    }
}
