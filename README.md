# Ubuntu based post installation
### Post installation script for Linux Ubuntu and derivatives.

It's needed to install [kotlin compiler](https://kotlinlang.org/docs/tutorials/command-line.html)

If you’re on Ubuntu 16.04 or later, you can install the compiler from the command line:
`sudo snap install --classic kotlin`

Pretty simple to run the script. At the root of the project:

`kotlinc -script  ubuntuPosInstall.kts`

**Do not run it as `sudo`!!**