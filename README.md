# ManyMex
An easy way to create [Telegram Bots](https://core.telegram.org/bots).

All you need is a JSON config:
```js
{
    "on_message": [ // here goes message handlers
        {
            "trigger": "^/hello",           // if the message starts with '/hello'
            "whitelist": [123456789],       // and is sent from the user with id '123456789'
            "message": {                    // send a message
                "text": ["hello", "hi"],    // with the text 'hello' or 'hi'
                "files": ["./hello.png"]    // and with an image
            }
        },
        {
            "trigger": "keyboard",          // if the message contains 'keyboard'
            "blacklist": [987654321],       // and is not sent from the user with id '987654321'
            "message": {                    // send a message
                "text": ["press a button"], // with the text 'press a button'
                "reply_markup": {           // with a keyboard
                    "inline_keyboard": [    // with three buttons
                        [{"text": "click me", "callback_data": "click"}],
                        [
                            {"text": "google", "url": "www.google.it"},
                            {"text": "click me too", "callback_data": "click"}
                        ]
                    ]
                }
            }
        }
    ],
    "on_callback": [ // here goes buttons handlers
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

Run the program, and in the generated `bot.properties` file set the bot token and the path to the
config file.

Now run again your program and enjoy your bot!
