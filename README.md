# ManyMex
An easy way to create [Telegram Bots].

All you need is a JSON configuration file:
```js
{
    "on_message": [ // Here are defined the message handlers
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
This is how your JSON config should be configured:

| Field       | Type                        | Optional | Description               |
|-------------|-----------------------------|----------|---------------------------|
| on_message  | Array of [Message Handler]  | yes      | List of message handlers  |
| on_callback | Array of [Callback Handler] | yes      | List of callback handlers |

## Message Handler
Handles messages that contains the specified text.

| Field     | Type            | Optional | Description                                                                                                               |
|-----------|-----------------|----------|---------------------------------------------------------------------------------------------------------------------------|
| trigger   | String          | no       | Message which text matches this [Regex](https://en.wikipedia.org/wiki/Regular_expression) will be handled by this handler |
| withelist | Array of Number | yes      | List of sender id which message will be handled                                                                           |
| blacklist | Array of Number | yes      | List of sender id which message will not be handled                                                                       |
| message   | [Message]       | no       | Message to send                                                                                                           |

## Message
Message to send.

| Field         | Type                                                                                          | Optional | Description                                                                                                                                                                                                                                                                                                                                                                                                                                                                                      |
|---------------|-----------------------------------------------------------------------------------------------|----------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| text          | Array of String                                                                               | yes      | List of texts of the message to send, randomly chosen, 1-4096 characters. The following tags are currently supported: &lt;b&gt;bold&lt;/b&gt;, &lt;strong&gt;bold&lt;/strong&gt;, &lt;i&gt;italic&lt;/i&gt;, &lt;em&gt;italic&lt;/em&gt;, &lt;a href="http://www.example.com/"&gt;inline URL&lt;/a&gt;, &lt;a href="tg://user?id=123456789"&gt;inline mention of a user&lt;/a&gt;, &lt;code&gt;inline fixed-width code&lt;/code&gt;, &lt;pre&gt;pre-formatted fixed-width code block&lt;/pre&gt; |
| files         | Array of String                                                                               | yes      | List of paths to file to send, randomly chosen                                                                                                                                                                                                                                                                                                                                                                                                                                                   |
| send_as_reply | Boolean                                                                                       | yes      | If `true` this message will be a reply to the handled message                                                                                                                                                                                                                                                                                                                                                                                                                                    |
| reply_markup  | [Inline Keyboard] or <br>[Reply Keyboard] or <br>[Remove Reply Keyboard] or <br>[Force Reply] | yes      | Additional interface options                                                                                                                                                                                                                                                                                                                                                                                                                                                                     |

## Inline Keyboard
This object represents an inline keyboard that appears right next to the message it belongs to.

| Field           | Type                                        | Optional | Description                                                                            |
|-----------------|---------------------------------------------|----------|----------------------------------------------------------------------------------------|
| inline_keyboard | Array of Array of [Inline Keyboard Button]  | no       | Array of button rows, each represented by an Array of [Inline Keyboard Button] objects |

## Inline Keyboard Button
This object represents one button of an inline keyboard. You **must** use exactly one of the optional fields.

| Field         | Type   | Optional | Description                                                                       |
|---------------|--------|----------|-----------------------------------------------------------------------------------|
| text          | String | no       | Label text on the button                                                          |
| url           | String | yes      | HTTP or tg:// url to be opened when button is pressed                             |
| callback_data | String | yes      | Data to be sent in a callback query to the bot when button is pressed, 1-64 bytes |

## Reply Keyboard
This object represents a custom keyboard with reply options.

| Field             | Type                                | Optional | Description                                                                                                                                                                                                                                                                                           |
|-------------------|-------------------------------------|----------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| keyboard          | Array of Array of [Keyboard Button] | no       | Array of button rows, each represented by an Array of [Keyboard Button] objects                                                                                                                                                                                                                       |
| resize_keyboard   | Boolean                             | yes      | Requests clients to resize the keyboard vertically for optimal fit  (e.g., make the keyboard smaller if there are just two rows of buttons).  Defaults to false, in which case the custom keyboard is always of the same height as the app's standard keyboard.                                       |
| one_time_keyboard | Boolean                             | yes      | Requests clients to hide the keyboard as soon as it's been used. The  keyboard will still be available, but clients will automatically display  the usual letter-keyboard in the chat – the user can press a special  button in the input field to see the custom keyboard again. Defaults to `false` |
| selective         | Boolean                             | yes      | Use this parameter if you want to show the keyboard to specific users only. Targets: 1) users that are @mentioned in the text of the Message object; 2) if the bot's message is a reply (has reply_to_message_id), sender of the original message.                                                    |

## Keyboard Button
This object represents one button of the reply keyboard.
For simple text buttons String can be used instead of this object to specify text of the button.
Optional fields are mutually exclusive.

| Field            | Type    | Optional | Description                                                                                                              |
|------------------|---------|----------|--------------------------------------------------------------------------------------------------------------------------|
| text             | String  | no       | Text of the button. If none of the optional fields are used, it will be sent as a message when the button is pressed     |
| request_contact  | Boolean | yes      | If `true`, the user's phone number will be sent as a contact when the button is pressed. Available in private chats only |
| request_location | Boolean | yes      | If `true`, the user's current location will be sent when the button is pressed. Available in private chats only          |

## Remove Reply Keyboard
Upon receiving a message with this object, Telegram clients will remove the current custom keyboard and display the default letter-keyboard.
By default, custom keyboards are displayed until a new keyboard is sent by a bot.
An exception is made for one-time keyboards that are hidden immediately after the user presses a button (see [Reply Keyboard]).

| Field           | Type    | Optional | Description                                                                                                                                                                                                                                          |
|-----------------|---------|----------|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| remove_keyboard | True    | no       | Requests clients to remove the custom keyboard (user will not be able to  summon this keyboard; if you want to hide the keyboard from sight but  keep it accessible, use one_time_keyboard in ReplyKeyboardMarkup)                                   |
| selective       | Boolean | yes      | Use this parameter if you want to remove the keyboard for specific users only. Targets: 1) users that are @mentioned in the text of the Message object; 2) if the bot's message is a reply (has reply_to_message_id), sender of the original message |

## Force Reply
Upon receiving a message with this object, Telegram clients will display a reply interface to the user (act as if the user has selected the bot‘s message and tapped ’Reply').

| Field       | Type    | Optional | Description                                                                                                                                                                                                                                   |
|-------------|---------|----------|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| force_reply | True    | no       | Shows a reply interface to the user, as if they manually selected the bot‘s message and tapped ’Reply'                                                                                                                                        |
| selective   | Boolean | yes      | Use this parameter if you want to force reply from specific users only. Targets: 1) users that are @mentioned in the text of the Message object; 2) if the bot's message is a reply (has reply_to_message_id), sender of the original message |

## Callback Handler
Handles callbacks with the specified name.

| Field     | Type              | Optional | Description                                          |
|-----------|-------------------|----------|------------------------------------------------------|
| callback  | String            | no       | Callback to handle                                   |
| withelist | Array of Number   | yes      | List of sender id which callback will be handled     |
| blacklist | Array of Number   | yes      | List of sender id which callback will not be handled |
| message   | [Message]         | yes      | Message to send                                      |
| answer    | [Callback Answer] | yes      | Answer to send                                       |

## Callback Answer
Response to a callback.

| Field      | Type    | Optional | Description                                                                                                                |
|------------|---------|----------|----------------------------------------------------------------------------------------------------------------------------|
| text       | String  | no       | Text of the notification, 0-200 characters                                                                                 |
| show_alert | Boolean | yes      | If `true`, an alert will be shown by the client instead of a notification at the top of the chat screen. Defaults to false |


[Telegram Bots]: https://core.telegram.org/bots

[Message Handler]: #Message-Handler
[Callback Handler]: #Callback-Handler
[Message]: #Message
[Inline Keyboard]: #Inline-Keyboard
[Reply Keyboard]: #Reply-Keyboard
[Remove Reply Keyboard]: #Remove-Reply-Keyboard
[Force Reply]: #Force-Reply
[Inline Keyboard Button]: #Inline-Keyboard-Button
[Keyboard Button]: #Keyboard-Button
[Callback Answer]: #Callback-Answer
