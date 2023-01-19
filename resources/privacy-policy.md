# Caffeinate - Privacy Policy

*Last Updated: January 19th, 2023 - Caffeinate Version 3.5 (Version Code: 34)*

Thanks for choosing to utilize Caffeinate! Caffeinate does not collect any personal information from your device, nor does it require network access of any sorts.

Caffeinate does request some permissions from the Android Framework however, those permissions are listed below:

- `android.permission.WAKE_LOCK`: This permission is utilized to ask your device to not sleep while Caffeinate's service is active (via activating the *Caffeinate* tile).

- `android.permission.MODIFY_AUDIO_SETTINGS`: This permission is utilized to toggle your device's sound settings (Vibrate / Normal) when the user utilizes the *Sound Profile* tile.

- `android.permission.ACCESS_NOTIFICATION_POLICY`: This permission is utilized to check if your device is in DND (Do Not Disturb) mode, as Android does not allow applications to activate the ringer when DND is active - specifically, this is permission is only utilized when the user clicks the *Sound Profile* tile, in tandem with the last `android.permission.MODIFY_AUDIO_SETTINGS` permission.

- `android.permission.FOREGROUND_SERVICE`: This permission is utilized when the user clicks the *Caffeinate* tile, in order to spawn the `Caffeination Service` which runs the logic and timer behind the *Caffeinate* tile.

- `android.permission.POST_NOTIFICATIONS` (Optional): This permission is utilized to provide the user with an ongoing status display of the `Caffeination Service` (such as how much time is remaining). This permission must be granted by the user (and is thus opt-in), and without it will not be able to use "Infinity Mode".

### Data Collection / Data Sharing

Caffeinate does not collect any information from the user, and thus no data can be shared :)

### Third Party Services

Caffeinate on your device itself does not require utilizing any third party services, however some third party services are used for aiding development of Caffeinate, which are as follows:

- POEditor: Used for allowing users to provide translation contributions for Caffeinate. Should you choose to contribute translations for Caffeinate, you would be required to create a POEditor account. Their privacy policy [can be found here](https://poeditor.com/terms/privacy). 

- Google Play: Caffeinate is primarily distributed to Android end-users through the Google Play Store. Obtaining Caffeinate through the Google Play Store may result in some analytics being recorded to Google, such as crash reports, the device you're running Caffeinate on, and a very rough estimate of your geographical location. Users who obtain Caffeinate through the Google Play Store can review Google's [Terms of Service](https://play.google.com/intl/en-us_us/about/play-terms/index.html) and Google's [Privacy Policy](https://www.google.com/intl/en/policies/privacy/) for further details on the information that the Google Play Store and Google Play Services may collect.

    - Caffeinate is an open source application! Should you choose to not use the Google Play Store, you may acquire Caffeinate's source code and build your own release via the [Caffeinate GitHub Repository](https://github.com/russjr08/Caffeinate). However, choosing to visit GitHub and cloning the repository means that you should review [GitHub's Privacy Policy](https://docs.github.com/site-policy/privacy-policies/github-privacy-statement).
