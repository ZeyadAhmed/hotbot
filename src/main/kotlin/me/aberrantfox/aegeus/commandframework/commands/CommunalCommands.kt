package me.aberrantfox.aegeus.commandframework.commands

import me.aberrantfox.aegeus.commandframework.ArgumentType
import me.aberrantfox.aegeus.commandframework.Command
import me.aberrantfox.aegeus.commandframework.util.idToName
import me.aberrantfox.aegeus.commandframework.util.sendPrivateMessage
import me.aberrantfox.aegeus.services.Configuration
import net.dv8tion.jda.core.EmbedBuilder
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import java.awt.Color
import java.util.*
import java.util.LinkedList

data class Suggestion(val member: String, val idea: String, val timeOf: DateTime, val userIcon: String)

object Suggestions {
    val pool: Queue<Suggestion> = LinkedList()
}

@Command(ArgumentType.Joiner)
fun suggest(event: GuildMessageReceivedEvent, args: List<Any>) {
    if (Suggestions.pool.size > 20) {
        sendPrivateMessage(event.author, "There are too many suggestions in the pool to handle your request currently... sorry about that.")
        return
    }

    if (Suggestions.pool.filter { it.member == event.author.id }.size > 3) {
        sendPrivateMessage(event.author, "You have enough suggestions in the pool for now...")
    }

    val suggestion = args[0] as String

    Suggestions.pool.add(Suggestion(event.author.id, suggestion, DateTime.now()))
    sendPrivateMessage(event.author, "Your suggestion has been added to the review-pool. " +
            "If it passes it'll be pushed out to the suggestions channel.")

    event.message.delete().queue()
}

@Command
fun poolInfo(event: GuildMessageReceivedEvent) {
    sendPrivateMessage(event.author,
            EmbedBuilder().setTitle("Suggestion Pool Info")
                    .setColor(Color.cyan)
                    .setDescription("There are currently ${Suggestions.pool.size} suggestions in the pool.")
                    .build())

    event.message.delete().queue()
}

@Command
fun poolTop(event: GuildMessageReceivedEvent) {
    if(Suggestions.pool.isEmpty()) {
        sendPrivateMessage(event.author, "The pool is empty.")
        return
    }

    val suggestion = Suggestions.pool.peek()

    sendPrivateMessage(event.author,
            EmbedBuilder()
                    .setTitle("Suggestion by ${suggestion.member.idToName(event.jda)}")
                    .setDescription(suggestion.idea)
                    .addField("Time of Creation",
                            suggestion.timeOf.toString(DateTimeFormat.forPattern("dd/MM/yyyy")),
                            false)
                    .addField("Member ID", suggestion.member, false)
                    .build())

    event.message.delete().queue()
}

@Command
fun poolAccept(event: GuildMessageReceivedEvent, args: List<Any>, config: Configuration) {
    if(Suggestions.pool.isEmpty()) {
        sendPrivateMessage(event.author, "The suggestion pool is empty... :)")
        return
    }

    val channel = event.guild.textChannels.findLast { it.id == config.suggestionChannel }
    val suggestion = Suggestions.pool.remove()

    channel?.sendMessage(EmbedBuilder()
            .setTitle("${suggestion.member.idToName(event.jda)}'s Suggestion")
            .setColor(Color.orange)
            .setDescription(suggestion.idea)
            .addField("Suggestion Status", "Community Review", false)
            .build())
            ?.queue()

    event.message.delete().queue()
}

@Command
fun poolDeny(event: GuildMessageReceivedEvent, args: List<Any>, config: Configuration) {
    if(Suggestions.pool.isEmpty()) {
        sendPrivateMessage(event.author, "The suggestion pool is empty... :)")
        return
    }

    val rejected = Suggestions.pool.remove()

    sendPrivateMessage(event.author, EmbedBuilder()
            .setTitle("Suggestion Removed")
            .addField("User ID", rejected.member, false)
            .addField("Time of Suggestion", rejected.timeOf.toString(DateTimeFormat.forPattern("dd/MM/yyyy")), false)
            .build())
}