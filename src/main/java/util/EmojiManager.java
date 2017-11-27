package util;

import enums.Language;
import enums.Statistique;

public class EmojiManager {

    public static String getEmojiForStat(Language lg, String text){
        text = text
                .replaceAll(Translator.getLabel(lg, "emoji.from") + "\\s+", "")
                .replaceAll("-?\\+?\\d+\\s+" + Translator.getLabel(lg, "emoji.to") + "\\s+-?\\+?\\d+", "")
                .replaceAll("-?\\+?\\d+", "")
                .replaceAll(Translator.getLabel(lg, "emoji.to") + " %", "")
                .replaceAll(":", "")
                .replaceAll("\\.", "")
                .replaceAll("\\s{2}", " ")
                .trim();

        for(Statistique stat : Statistique.values()) {
            String[] names = stat.getNames(lg);
            for (String proposal : names)
                if (proposal.equals(text))
                    return stat.getEmoji();
        }
        return "";
    }
}
