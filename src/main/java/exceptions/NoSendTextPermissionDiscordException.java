package exceptions;

import commands.Command;
import enums.Language;
import util.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sx.blah.discord.handle.obj.IMessage;

/**
 * Created by steve on 14/11/2016.
 */
public class NoSendTextPermissionDiscordException implements DiscordException {

    private final static Logger LOG = LoggerFactory.getLogger(NoSendTextPermissionDiscordException.class);

    @Override
    public void throwException(IMessage message, Command command, Language lg, Object... arguments) {
        Message.sendText(message.getChannel(), "Je n'ai pas le droit d'envoyer des messages sur ce chan.");
    }
}
