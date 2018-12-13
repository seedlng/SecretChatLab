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
package ank.com.secretchatlab.data.connection.listeners;

import ank.com.secretchatlab.data.entity.AccountJid;

import org.jivesoftware.smack.packet.IQ;

/**
 * Listen for the response to the packet.
 *
 * @author alexander.ivanov
 */
public interface OnResponseListener {

    void onReceived(AccountJid account, String packetId, IQ iq);

    void onError(AccountJid account, String packetId, IQ iq);

    void onTimeout(AccountJid account, String packetId);

    void onDisconnect(AccountJid account, String packetId);

}
