[![Build Status](https://wolfs.ci.cloudbees.com/buildStatus/icon?job=jenkins-rss-retaliation)](https://wolfs.ci.cloudbees.com/job/jenkins-rss-retaliation/)

jenkins-rss-retaliation
=======================

Uses the jenkins rss feed to control various extreme feedback devices. Inspired by [Retaliation](https://github.com/codedance/Retaliation).
Currently supported are:

* [Dream Cheeky Missile Launcher](http://www.dreamcheeky.com/thunder-missile-launcher)
* Google Text-To-Speech API
* [Cleware Traffic Light](http://www.cleware-shop.de/epages/63698188.sf/de_DE/?ObjectPath=/Shops/63698188/Products/41/SubProducts/41-1)

In order to use the latest released version, download the application zip from [bintray](https://bintray.com/wolfs/maven/jenkins-rss-retaliation). Then create a
configuration file config.groovy in the conf directory in the unzipped application directory.

Command Line
------------

* stalk - Polls the rss feed an triggers the feedback device(s)
* say <text> - uses Google Text To Speech to say a word
* shootAt <userId> - triggers the missile launcher to shoot at the given culprits
* left 1000 - moves the missile launcher 1000 milliseconds to the left
* right | up | down 500 - same with other directions
* ledOn | ledOff - switches the led on and off
* fire - fires the missile launcher

Example configuration
---------------------

config.groovy:

    rssFeedUrl='http://localhost:8080/rssFailed' // rss-feed from Jenkins
    pollInterval=10000 // Poll interval for the rss-Feed
    feedbackDevices=['tts'] // Active feedback devices. Possible entries: missile, tts, trafficLight
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
            unknown=[  // This happens when no culprit is found
                    ['zero'],
                    ['right', 4000],
                    ['up', 2000],
                    ['left', 2000],
                    ['down', 1000]

            ]
        }
        whenToShoot=['UNSTABLE', 'FAILURE'] // Result on when to shoot, possible values: SUCCESS, UNSTABLE, FAILURE, NOT_BUILT, ABORTED
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

