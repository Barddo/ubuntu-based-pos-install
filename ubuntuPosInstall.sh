#!/usr/bin/env bash

URL_GOOGLE_CHROME="https://dl.google.com/linux/direct/google-chrome-stable_current_amd64.deb"
DOWNLOADS_DIRECTORY="$HOME/Downloads/programs"

sudo rm /var/lib/dpkg/lock-frontend
sudo rm /var/cache/apt/archives/lock

PROGRAMS_TO_INSTALL=(
  snapd
  firefox
  git
  flatpak
  gnome-software-plugin-flatpak
  ulauncher
  openjdk-11-jdk
  openjdk-11-jre
  openjdk-11-doc
  gedit
  gnome-tweaks
  gnome-system-monitor
  gnome-calculator
  gnome-characters
  gnome-logs
  ubuntu-restricted-extras
)


snap remove gnome-system-monitor gnome-calculator gnome-characters gnome-logs
sudo dpkg --add-architecture i386
sudo apt update -y

## Installing programs in apt
for program_name in ${PROGRAMS_TO_INSTALL[@]}; do
  if ! dpkg -l | grep -q $program_name; then # Only installs if not already installed
    apt install "$program_name" -y
  else
    echo "[INSTALLED] - $program_name"
  fi
done

## Installing programs via .deb
mkdir "$DOWNLOADS_DIRECTORY"
wget -c "$URL_GOOGLE_CHROME"       -P "$DOWNLOADS_DIRECTORY"

sudo dpkg -i $DOWNLOADS_DIRECTORY/*.deb

## Installing Snap packages ##
sudo snap install spotify
sudo snap install toolbox
sudo snap install code --classic
sudo snap install ubuntu-make --classic

## Setting Flatpak

flatpak remote-add --if-not-exists flathub https://flathub.org/repo/flathub.flatpakrepo

gsettings set org.gnome.shell.extensions.dash-to-dock click-action 'minimize'

# ----------------------------- POST-INSTALLATION ----------------------------- #
## Finishing, updating and cleaning ##
sudo apt update && sudo apt dist-upgrade -y
flatpak update
sudo apt autoclean
sudo apt autoremove -y
# ---------------------------------------------------------------------- #


# Setting environment variables #
BASHELL=$( cat $HOME/.bashrc )
echo -e "$BASHELL\nexport JAVA_HOME=/usr/lib/jvm/default-java\nexport PATH=$JAVA_HOME/bin:$PATH" > $HOME/.bashrc
