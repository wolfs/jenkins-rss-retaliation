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
            List<String> text = config.message.call(buildHistory.currentBuild)
            if (text) {
                LOG.info("Saying: '${text.join(' ')}'")
                say(text)
            }
        }
    }

    void say(String text) {
        say(text.split(/\s/))
    }

    @SuppressWarnings('BusyWait')
    void say(List<String> text) {
        for (String substring : text) {
            File voiceFile = new File(config.voiceDir, "${substring.trim()}.mp3")
            if (voiceFile.exists()) {
                voiceFile.withInputStream(this.&play)
            } else {
                URL url = new URL("${GOOGLE_TRANSLATE_TTS}?" +
                        "tl=${config.lang}&" +
                        "q=${URLEncoder.encode(substring, Charsets.UTF_8.toString())}")
                URLConnection urlConn = url.openConnection()
                urlConn.addRequestProperty('User-Agent',
                        'Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.0)')
                urlConn.inputStream.withStream(this.&play)
            }
        }
    }

    private void play(InputStream audioSrc) {
        def player = new Player(new BufferedInputStream(audioSrc))
        player.play()
        player.close()
    }
}
