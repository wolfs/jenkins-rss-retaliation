rssFeedUrl='http://localhost:8080/rssFailed' // rss-feed from Jenkins
pollInterval=10000 // Poll interval for the rss-Feed
feedbackDevices=['tts'] // Active feedback devices. Possible entries: missile, tts
feedbackInParallel=false // Should the feedback devices be triggered in parallel or sequentially?

missile {
    locations {     // Configuration for the locations to shoot at for culprits. The key is the user id from Jenkins and the value is a list of commands
        // Possible Commands: ['zero'] - moves launcher to bottom left,
        // [<direction>, <time in ms>] where direction is one of left, right, up or down
        // ['fire'] - fires the launcher once
        // ['ledOn'], ['ledOff'] - control the led
        someUser=[
                ['zero'],
                ['right', 2000],
                ['up', 600],
                ['fire']
        ]
        unknown=[  // This happens, when no culprit is found
                ['zero'],
                ['right', 4000],
                ['up', 2000],
                ['left', 2000],
                ['down', 1000]

        ]
    }
}

tts {
    lang = 'en' // The language code
    message = { info -> // Closure which yields the text to be spoken
        "The build ${info.project.displayName} is broken!"
    }
}

trafficLight {
    clewareUsbSwitchBinary='/some/path/to/binary'
}