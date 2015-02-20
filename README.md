## About ##

Privly is a developing set of browser extensions, mobile and web applications for usable internet content privacy. It allows users to view content on any website, without the host site being able to read the content. The Privly extensions support "Injectable Applications" (also known as Privly Applications), which are web applications viewed within the context of other web applications. These applications can be any web application, but they are generally oriented to supporting a privacy use case.

For more information on what Privly is, [read about us](https://priv.ly/pages/about).

## About this repository ##

This repository maintains the Android Application of Privly. The application currently integrates:
* the two injectable [privly-applications](https://github.com/privly/privly-applications/) - PlainPost and ZeroBin for creating new Privly Posts
* shows Index (List with Privly Posts)
* shows privly links in FaceBook, Twitter and Gmail

## Developer Info ##
To contribute to the development of Privly-Android. 

* Fork and Clone 
* Update submodules. 
 * $ git submodule init
 * $ git submodule update
* Import libraries from 'dependencies' folder
* Contribute

[Facebook Setup](https://github.com/privly/privly-android/wiki/To-test%5Cdevelop-Facebook) <br>
[Twitter Setup](https://github.com/privly/privly-android/wiki/To-test%5Cdevelop-Twitter)
[Gmail Setup] (https://developers.google.com/gmail/api/quickstart/quickstart-java)

To drop in new posting-applications

* Add the new posting-application folder to assets/PrivlyApplications
* Add posting-application's name to ArrayList<String> createList in MainActivity.java. This should be the folder name of the posting application. 
* The application folder must contain a new.html file. This new.html file will be loaded in the WebView when creating new Privly Posts. 


## Testing/Submitting Bugs ##

If you have discovered a bug, only [open a public issue](https://github.com/privly/privly-android/issues) on GitHub if it could not possibly be a security related bug. If the bug affects the security of the system, please report it privately at [privly.org](http://www.privly.org/content/bug-report). We will then fix the bug and follow a process of responsible disclosure.

## Contact Us ##

**Email**:  
Community [the 'at' sign] privly.org  

**IRC**:  
irc.freenode.net #privly

