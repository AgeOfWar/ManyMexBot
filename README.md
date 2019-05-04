# ManyMex
An easy way to create [Telegram Bots].

All you need is a JSON configuration file:
```json5
{
    "on_message": [ // Here are defined the message handlers
        {
            "trigger": "^/hello",           // if the message starts with '/hello'
            "whitelist": [123456789],       // and is sent from the user with id '123456789'
            "message": {                    // send a message
                "text": ["hello", "hi"],    // with the text 'hello' or 'hi'
                "files": "./hello.png"      // and with an image
            }
        },
        {
            "trigger": "keyboard",          // if the message contains 'keyboard'
            "blacklist": [987654321],       // and is not sent from the user with id '987654321'
            "message": {                    // send a message
                "text": "press a button",   // with the text 'press a button'
                "reply_markup": {           // with a keyboard
                    "inline_keyboard": [    // with three buttons
                        {"text": "click me", "callback_data": "click"},
                        [
                            {"text": "google", "url": "www.google.it"},
                            {"text": "click me too", "callback_data": "click"}
                        ]
                    ]
                }
            }
        }
    ],
    "on_callback": [ // Here are defined the callback handlers
        {
            "callback": "click",          // when a button with callback_data = 'click' is clicked
            "answer": {                   // tell to the client
                "text": "Button clicked", // 'Button clicked!'
                "show_alert": true        // with a pop-up
            }
        }
    ]
}
```

Note that you have to remove comments from the example above in order to use it as a valid
configuration file.

Run the program (you can downoad it [here](https://github.com/AgeOfWar/ManyMexBot/releases/)),
and in the generated `bot.properties` file set the bot token and the path to the config file.

Now run again your program and enjoy your bot!

# Configuration File Format
You can find the format of the config [here](https://AgeOfWar.github.io/ManyMex/docs/Configuration.html).


[Telegram Bots]: https://core.telegram.org/bots
