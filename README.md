# Artham

A lightweight English to Malayalam dictionary app for Android. Available in [Play Store](https://play.google.com/store/apps/details?id=com.innoventionist.artham). Features:

- Simple UI
- Offline
- Small app size - 2.4 MB
- Uses [Olam](https://olam.in/) database
- Search history, bookmarks, dark theme, TTS

Artham was originally built during 2015 for my dad. His phone had very little internal memory and dictionary apps used to explode in size after installation due to sqlite indexes. So focus in Artham was on the app size. For that it uses a custom encoding on the database and doesn't use Android support libs. A year later I published Artham to Play Store and it was well received. And it eventually became the turning point in my life towards an impactful career :)

## Building

- Install [apkc](https://github.com/ajinasokan/apkc)
- Clone this repo
- Build APK

```sh
$ cd artham

$ apkc build 

# writes to build/app.apk
```

- Update DB if necessary. Requires Python 3.x.

```sh
$ make download-olam

$ make build-index

# writes to res/raw/mdb.txt and res/raw/wdbd.txt
# use the map index inside Dictionary.java
```

## Credits

- [Olam](https://olam.in) dictionary by [Kailash Nadh](https://nadh.in/)
- [Baloo chettan](https://ektype.in/font-family/baloo.html) font by [Ek Type](https://ektype.in)
- [Floating action button](https://github.com/Clans/FloatingActionButton) by [@Clans](https://github.com/Clans)

## Links

- [Introducting Artham](https://ajinasokan.com/posts/introducing-artham-dictionary/)
- [Making of Artham Dictionary](https://ajinasokan.com/posts/making-of-artham/)
- [Artham Dictionary Update 1.1](https://ajinasokan.com/posts/artham-update/)