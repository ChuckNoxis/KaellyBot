package listeners;

import data.*;
import finders.AlmanaxCalendar;
import finders.RSSFinder;
import finders.TwitterFinder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.ReadyEvent;
import sx.blah.discord.handle.impl.events.guild.GuildCreateEvent;
import sx.blah.discord.handle.obj.ActivityType;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.StatusType;
import util.ClientConfig;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by steve on 14/07/2016.
 */
public class ReadyListener {
    private final static Logger LOG = LoggerFactory.getLogger(ReadyListener.class);

    @EventSubscriber
    public void onReady(ReadyEvent event) {
        LOG.info(Constants.name + "Bot connecté !");

        LOG.info("Ajout des différents listeners");
        ClientConfig.DISCORD().getDispatcher().registerListener(new GuildCreateListener());
        ClientConfig.DISCORD().getDispatcher().registerListener(new GuildLeaveListener());
        ClientConfig.DISCORD().getDispatcher().registerListener(new GuildUpdateListener());
        ClientConfig.DISCORD().getDispatcher().registerListener(new ChannelDeleteListener());
        ClientConfig.DISCORD().getDispatcher().registerListener(new NickNameChangeListener());
        ClientConfig.DISCORD().getDispatcher().registerListener(new UserBanListener());
        ClientConfig.DISCORD().getDispatcher().registerListener(new UserJoinListener());
        ClientConfig.DISCORD().getDispatcher().registerListener(new UserLeaveListener());
        ClientConfig.DISCORD().getDispatcher().registerListener(new TrackFinishListener());

        LOG.info("Check des guildes et des utilisateurs");
        for(IGuild guild : ClientConfig.DISCORD().getGuilds())
            if (Guild.getGuilds().containsKey(guild.getStringID())) {
                // La guilde existe déjà : on s'assure de mettre à jour l'ensemble des données durant l'absence.
                if (!guild.getName().equals(Guild.getGuild(guild).getName()))
                    Guild.getGuild(guild).setName(guild.getName());

                for (IUser discordUser : guild.getUsers()){
                    User user = User.getUser(guild, discordUser);
                    if (!discordUser.getDisplayName(guild).equals(user.getName()))
                        user.setName(discordUser.getDisplayName(guild));
                }
            }
            else // La guilde n'existe pas, on lève un évent pour le prendre en compte !
                ClientConfig.DISCORD().getDispatcher().dispatch(new GuildCreateEvent(guild));

        // Check des guildes éventuellement supprimé durant l'absence
        List<String> ids =  new ArrayList<>(Guild.getGuilds().keySet());

        for(String guildID : ids)
            if (ClientConfig.DISCORD().getGuildByID(Long.parseLong(guildID)) == null) {
                LOG.info(Guild.getGuilds().get(guildID).getName() + " a supprimé "
                        + Constants.name + " en son absence.");
                Guild.getGuilds().get(guildID).removeToDatabase();
            }

        // Joue à...
        ClientConfig.DISCORD().changePresence(StatusType.ONLINE, ActivityType.WATCHING, Constants.discordInvite);

        LOG.info("Ecoute des flux RSS du site Dofus...");
        RSSFinder.start();

        LOG.info("Lancement du calendrier Almanax...");
        AlmanaxCalendar.start();

        LOG.info("Connexion à l'API Twitter...");
        TwitterFinder.start();

        LOG.info("Ecoute des messages...");
        ClientConfig.DISCORD().getDispatcher().registerListener(new MessageListener());
    }
}
