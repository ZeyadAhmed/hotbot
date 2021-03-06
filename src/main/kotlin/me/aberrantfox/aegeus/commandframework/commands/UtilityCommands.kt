package me.aberrantfox.aegeus.commandframework.commands

import me.aberrantfox.aegeus.commandframework.ArgumentType
import me.aberrantfox.aegeus.services.Configuration
import me.aberrantfox.aegeus.services.saveConfig
import me.aberrantfox.aegeus.commandframework.Command
import net.dv8tion.jda.core.EmbedBuilder
import net.dv8tion.jda.core.OnlineStatus
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent
import java.awt.Color
import java.time.LocalDateTime.now
import java.util.*

val startTime = Date()

@Command fun ping(event: GuildMessageReceivedEvent) = event.channel.sendMessage("Pong!").queue()

@Command
fun serverinfo(event: GuildMessageReceivedEvent) {
    val builder = EmbedBuilder()
    builder.setTitle(event.guild.name)
            .setColor(Color.MAGENTA)
            .setDescription("The programmer's hangout is a programming server, made for persons of all skill levels, " +
                    "be you someone who has wrote 10 lines of code, or someone with 10 years of experience.")
            .setFooter("Guild creation date: ${event.guild.creationTime}", "http://i.imgur.com/iwwEprG.png")
            .setThumbnail("http://i.imgur.com/DFoaG7k.png")

    builder.addField("Users", "${event.guild.members.filter {
        m ->
        m.onlineStatus != OnlineStatus.OFFLINE
    }.size}/${event.guild.members.size}", true)
    builder.addField("Total Roles", "${event.guild.roles.size}", true)
    builder.addField("Owner", "${event.guild.owner.effectiveName}", true)
    builder.addField("Region", "${event.guild.region}", true)
    builder.addField("Text Channels", "${event.guild.textChannels.size}", true)
    builder.addField("Voice Channels", "${event.guild.voiceChannels.size}", true)

    event.channel.sendMessage(builder.build()).queue()
}

@Command
fun uptime(event: GuildMessageReceivedEvent) {
    val uptime = Date().time - startTime.time
    val minutes = uptime / 1000 / 60
    val currentDate = startTime.toString()

    event.channel.sendMessage("I've been awake since ${currentDate}, so like... ${minutes} minutes").queue()
}

@Command
fun exit(event: GuildMessageReceivedEvent, args: List<Any>, config: Configuration) {
    saveConfig(config)
    event.channel.sendMessage("Exiting").queue()
    System.exit(0)
}

@Command
fun kill(event: GuildMessageReceivedEvent) {
    event.channel.sendMessage("Killing process, configurations will not be saved.").queue()
    System.exit(0)
}

@Command
fun saveConfigurations(event: GuildMessageReceivedEvent, args: List<Any>, config: Configuration) {
    saveConfig(config)
    event.channel.sendMessage("Configurations saved. I hope you know what you are doing...").queue()
}

@Command(ArgumentType.Manual)
fun info(event: GuildMessageReceivedEvent, args: List<Any>) {
    val builder = EmbedBuilder()
            .setDescription("I'm Hotbot, the superior Auto-Titan replacement!")
            .setAuthor("Fox", "https://github.com/AberrantFox", "https://avatars1.githubusercontent.com/u/22015832")
            .setColor(Color.MAGENTA)
            .setThumbnail("http://i.imgur.com/DFoaG7k.png")
            .setFooter("Bot by Fox, made with Kotlin", "http://i.imgur.com/SJPggeJ.png")


    builder.addField("Project Name", "Hotbot", true)
            .addField("Main Language(s)", "Kotlin", true)
            .addField("Libraries", "JDA, Gson, (soon) Apache CLI, Apache Reflections, kotson, junit, mockito", false)
            .addField("Progress to Completion", "100% for a basic version plus or minus 5%, needs a little more testing", false)
            .addField("Repo link", "https://github.com/AberrantFox/hotbot", false)

    event.channel.sendMessage(builder.build()).queue()
    event.message.delete().queue()
}