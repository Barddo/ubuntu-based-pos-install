import java.io.File
import java.util.concurrent.TimeUnit

val home: String = System.getProperty("user.home")
val homeFile = File(home)

val urlGoogleChrome = "https://dl.google.com/linux/direct/google-chrome-stable_current_amd64.deb"
val downloadsDirectory = "$home/Downloads/programs"

val packagesApt = listOf(
        "snapd",
        "zsh",
        "fonts-powerline",
        "git",
        "ulauncher",
        "openjdk-11-jdk",
        "openjdk-11-jre",
        "gedit",
        "gnome-tweaks",
        "gnome-system-monitor",
        "gnome-calculator",
        "gnome-characters",
        "gnome-logs",
        "ubuntu-restricted-extras"
)
val snapsPrograms = listOf(
        "spotify",
        "toolbox",
        "code --classic",
        "ubuntu-make --classic"
)

println("Starting Script...")
Thread.sleep(2000)
println("Preparing environment")
prepareEnv()
println("Installing Apt packages")
installAptPackages(packagesApt)
println("Installing Deb packages")
installDebPackages()
println("Installing Snaps")
installSnaps(snapsPrograms)
println("Finalizing...")
postAllInstallations()
println("Done.")

fun prepareEnv() {
    listOf(
            "sudo rm /var/lib/dpkg/lock-frontend",
            "sudo rm /var/cache/apt/archives/lock",
            "sudo snap remove gnome-system-monitor gnome-calculator gnome-characters gnome-logs",
            "sudo dpkg --add-architecture i386",
            "sudo apt update -y"
    ).forEach(::execThenPrint)

}

fun installAptPackages(programsToInstall: List<String>) {

    for (program in programsToInstall) {
        if (execInHome("dpkg -l | grep -q $program").isNotEmpty())
            execThenPrint("sudo apt install $program -y")
        else println("[INSTALLED] - $program")
    }
}

fun installDebPackages() {

    execThenPrint("wget -c $urlGoogleChrome -P $downloadsDirectory")

    File(downloadsDirectory) exec "sudo dpkg -i *.deb"
}

fun installSnaps(programsToInstall: List<String>) {
    programsToInstall.forEach { program ->
        execThenPrint("sudo snap install $program")
    }
}

fun postAllInstallations() {
    execThenPrint("code --install-extension Shan.code-settings-sync")
    installOhMyZsh()
    execThenPrint("sudo apt update")
    execThenPrint("sudo apt upgrade")
    execThenPrint("sudo apt dist-upgrade -y")
    execThenPrint("sudo apt autoclean")
    execThenPrint("sudo apt autoremove -y")
}

fun installOhMyZsh() {
    val dollar = '$'
    execThenPrint(
            """
        git clone https://github.com/denysdovhan/spaceship-prompt.git "${dollar}ZSH_CUSTOM/themes/spaceship-prompt"
        """.trimIndent()
    )
    execThenPrint(
            """
        ln -s "${dollar}ZSH_CUSTOM/themes/spaceship-prompt/spaceship.zsh-theme" "${dollar}ZSH_CUSTOM/themes/spaceship.zsh-theme"
        """.trimIndent()
    )
}


fun execInHome(command: String): String {
    return homeFile exec command
}

fun execThenPrint(command: String) {
    println(homeFile exec command)
}

infix fun File.exec(command: String): String {
    val arguments = command.split(' ').toTypedArray()
    return execute(*arguments)
}

fun File.execute(vararg arguments: String): String {
    val process = ProcessBuilder(*arguments)
            .directory(this)
            .start()
            .also { it.waitFor(2, TimeUnit.MINUTES) }
            .also {
                it.inputStream.bufferedReader().lines().forEach(::println)
            }

    if (process.exitValue() != 0) {
        throw Exception(process.errorStream.bufferedReader().readText())
    }
    return process.inputStream.bufferedReader().readText()
}

