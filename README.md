<img alt="Night Shift Icon" align="right" height="128" src="https://github.com/automaton82/night-shift/blob/master/app/src/main/res/mipmap-xxxhdpi/ic_launcher_round.png">

# Night Shift<br/> [![License](https://img.shields.io/badge/license-GPL--3.0%2B-bd0000.svg)](COPYING "License: GPL-3.0-or-later") [![Commits (since latest release)](https://img.shields.io/github/commits-since/LibreShift/red-moon/latest.svg "Commits since latest release")](https://github.com/LibreShift/red-moon/releases/latest)

Blue light may suppress the production of melatonin, the sleep hormone. Night Shift
filters out blue light and dims your screen below the normal minimum, so you can
use your phone comfortably at night.

* Schedule Night Shift to run from sunset to sunrise, or at custom times.
* Use the default color profiles, or set custom color, intensity, and dim levels.
* Automatically pause in apps secured against overlays, or those you choose.
* Quickly start, stop, and switch profiles via notification, tile (Android 7.0+), or widget.


## Screenshots

Screenshots are slightly out of date.

<img src="https://github.com/LibreShift/red-moon/blob/master/app/src/main/play/en-US/listing/phoneScreenshots/1.png" width="189" height="336" /> <img src="https://github.com/LibreShift/red-moon/blob/master/app/src/main/play/en-US/listing/phoneScreenshots/2.png" width="189" height="336" /> 
<img src="https://github.com/LibreShift/red-moon/blob/master/app/src/main/play/en-US/listing/phoneScreenshots/6.png" width="189" height="336" />
<img src="https://github.com/LibreShift/red-moon/blob/master/app/src/main/play/en-US/listing/phoneScreenshots/7.png" width="189" height="336" />

## Privacy Policy

Night Shift collects data **only when it is necessary for functionality**. These features are all **disabled by default**. If you enable them, the data **never leaves your device**. Specifically:

- If you use the "Sunrise to Sunset" feature, Night Shift will store your location (lattitude & longitude, you can see them in the preference). Used in order to calculate sunrise and sunset times.
- If you use the excluded apps feature, then while Night Shift is running, it will monitor what app is in the foreground and which screen of that app is open (currently it polls once per second). This is checked against the list of excluded apps to determine whether Night Shift should pause, and then is immediately discarded.
    - If you choose to exclude an app, then it's stored to the list of excluded apps, until you remove it.
    - In the debug version of the app, these are logged to the Android system log ("logcat"). These logs are not accessible to other apps on the device. On most versions of android, they're cleared after 1-7 days.

### License

[<img src="https://www.gnu.org/graphics/gplv3-127x51.png"
      align="right"
      alt="GNU GPLv3 Image">](http://www.gnu.org/licenses/gpl-3.0.en.html)

*Night Shift* is licensed under the [GNU General Public License version 3] or (at your option) any later version by [the contributors]. It is a derivative of *[Shades]* by [Chris Nguyen], used under the [MIT License].

All used artwork is released into the public domain. Some of the icons use clip art from [openclipart.org], which are all released in the public domain, namely:

* https://openclipart.org/detail/121903/full-moon
* https://openclipart.org/detail/219211/option-button-symbol-minimal-svg-markup
* https://openclipart.org/detail/20806/wolf-head-howl-1
* https://openclipart.org/detail/213998/nexus-5-flat
* https://openclipart.org/detail/192689/press-button

---

\* Google Play and the Google Play logo are trademarks of Google Inc.

[issues]: https://github.com/raatmarien/red-moon/issues
[new issues]: https://github.com/raatmarien/red-moon/issues/new
[Weblate]: https://hosted.weblate.org/projects/red-moon/strings/
[labels]: https://github.com/LibreShift/red-moon/labels
[Shades]: https://github.com/cngu/shades
[Chris Nguyen]: https://github.com/cngu
[MIT License]: https://github.com/cngu/shades/blob/e240edc1df3e6dd319cd475a739570ff8367d7f8/LICENSE
[GNU General Public License version 3]: https://www.gnu.org/licenses/gpl-3.0.html
[the contributors]: https://github.com/raatmarien/red-moon/graphs/contributors
[openclipart.org]: https://openclipart.org/
