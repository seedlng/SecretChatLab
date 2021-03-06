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
package ank.com.secretchatlab.data.extension.vcard;

import ank.com.secretchatlab.data.BaseUIListener;
import ank.com.secretchatlab.data.entity.AccountJid;

import org.jivesoftware.smackx.vcardtemp.packet.VCard;
import org.jxmpp.jid.Jid;

/**
 * Listener for vCard to be received.
 *
 * @author alexander.ivanov
 */
public interface OnVCardListener extends BaseUIListener {

    /**
     * Requested vCard has been received.
     */
    void onVCardReceived(AccountJid account, Jid jid, VCard vCard);

    /**
     * Fail occurred on vCard response.
     */
    void onVCardFailed(AccountJid account, Jid jid);

}
