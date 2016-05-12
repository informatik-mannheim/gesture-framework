/*
 * Copyright (C) 2016 Insitute for User Experience and Interaction Design,
 *    Hochschule Mannheim University of Applied Sciences
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package hs_mannheim.gestureframework.model;

import hs_mannheim.gestureframework.messaging.Packet;

/**
 * Represents the data selected to be transferred. Data is always encapsulated in a {@link Packet}
 * instance so it can be distributed through the  * {@link hs_mannheim.gestureframework.messaging.PostOffice}.
 */
@SuppressWarnings("unused")
public class Selection {

    private Packet _packet;

    public final static Selection Empty = new Selection(new Packet("Nothing selected."));

    public Selection(Packet data) {
        setData(data);
    }

    /**
     * Update the data of this {@link Selection} to a new {@link Packet}.
     * @param newPacket The new {@link Packet}.
     */
    public void updateSelection(Packet newPacket) {
        setData(newPacket);
    }

    /**
     * Get the data of this {@link Selection} as a {@link Packet}.
     */
    public Packet getData() {
        return _packet;
    }

    private void setData(Packet data) {
        this._packet = data;
    }
}