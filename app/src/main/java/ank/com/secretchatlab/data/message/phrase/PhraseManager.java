/**
 * Copyright (c) 2013, Redsolution LTD. All rights reserved.
 *
 * This file is part of Xabber project; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License, Version 3.
 *
 * Xabber is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License,
 * along with this program. If not, see http://www.gnu.org/licenses/.
 */
package ank.com.secretchatlab.data.message.phrase;

import android.database.Cursor;
import android.net.Uri;

import ank.com.secretchatlab.data.Application;
import ank.com.secretchatlab.data.OnLoadListener;
import ank.com.secretchatlab.data.database.sqlite.PhraseTable;
import ank.com.secretchatlab.data.entity.AccountJid;
import ank.com.secretchatlab.data.entity.UserJid;
import ank.com.secretchatlab.data.message.chat.ChatManager;
import ank.com.secretchatlab.data.roster.RosterManager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Manage custom notification based on message.
 *
 * @author alexander.ivanov
 */
public class PhraseManager implements OnLoadListener {

    /**
     * List of settings.
     */
    private final List<Phrase> phrases;

    private static PhraseManager instance;

    public static PhraseManager getInstance() {
        if (instance == null) {
            instance = new PhraseManager();
        }

        return instance;
    }

    private PhraseManager() {
        phrases = new ArrayList<>();
    }

    @Override
    public void onLoad() {
        final Collection<Phrase> phrases = new ArrayList<>();
        Cursor cursor;
        cursor = PhraseTable.getInstance().list();
        try {
            if (cursor.moveToFirst()) {
                do {
                    phrases.add(new Phrase(PhraseTable.getId(cursor),
                            PhraseTable.getValue(cursor), PhraseTable
                            .getUser(cursor), PhraseTable
                            .getGroup(cursor), PhraseTable
                            .isRegexp(cursor), PhraseTable
                            .getSound(cursor)));
                } while (cursor.moveToNext());
            }
        } finally {
            cursor.close();
        }
        Application.getInstance().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                onLoaded(phrases);
            }
        });
    }

    private void onLoaded(Collection<Phrase> phrases) {
        this.phrases.addAll(phrases);
    }

    /**
     * @param text
     * @return Sound associated with first matched phrase. Chat specific setting
     * if no one matches .
     */
    public Uri getSound(AccountJid account, UserJid user, String text, boolean isMUC) {
        Collection<String> groups = RosterManager.getInstance().getGroups(
                account, user);
        for (Phrase phrase : phrases)
            if (phrase.matches(text, user.toString(), groups)) {
                Uri value = phrase.getSound();
                if (ChatManager.EMPTY_SOUND.equals(value))
                    return null;
                return value;
            }
        return ChatManager.getInstance().getSound(account, user, isMUC);
    }

    /**
     * Update phrase or create.
     *
     * @param phrase can be <code>null</code> for new phrase.
     * @param value
     * @param regexp
     * @param sound
     */
    public void updateOrCreatePhrase(Phrase phrase, String value, String user,
                                     String group, boolean regexp, Uri sound) {
        if (phrase == null) {
            phrase = new Phrase(null, value, user, group, regexp, sound);
            phrases.add(phrase);
        } else {
            phrase.update(value, user, group, regexp, sound);
        }
        writePhrase(phrase, value, user, group, regexp, sound);
    }

    /**
     * Removes phrase.
     *
     * @param index
     */
    public void removePhrase(int index) {
        Phrase phrase = getPhrase(index);
        if (phrase != null) {
            phrases.remove(phrase);  // remove from the local list
            PhraseTable.getInstance().remove(phrase.getId());  // remove from database
        }
    }

    private void writePhrase(final Phrase phrase, final String value,
                             final String user, final String group, final boolean regexp,
                             final Uri sound) {
        Application.getInstance().runInBackgroundUserRequest(new Runnable() {
            @Override
            public void run() {
                phrase.setId(PhraseTable.getInstance().write(phrase.getId(),
                        value, user, group, regexp,
                        sound == null ? ChatManager.EMPTY_SOUND : sound));
            }
        });
    }

    public Collection<Integer> getPhrases() {
        Collection<Integer> collection = new ArrayList<Integer>();
        for (int index = 0; index < phrases.size(); index++)
            collection.add(index);
        return collection;
    }

    public Phrase getPhrase(int index) {
        if (index < 0 || index >= phrases.size())
            return null;
        return phrases.get(index);
    }

}