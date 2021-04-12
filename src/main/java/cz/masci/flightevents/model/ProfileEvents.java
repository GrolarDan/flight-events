/*
 * Copyright (C) 2021 Daniel
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package cz.masci.flightevents.model;

import cz.masci.flightevents.model.events.BaseEvent;
import cz.masci.flightevents.model.events.ConditionEvent;
import cz.masci.flightevents.model.events.MotionEvent;
import cz.masci.flightevents.model.events.VoiceMessageEvent;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import lombok.Data;

/**
 *
 * @author Daniel Masek
 */
@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class ProfileEvents {

    @XmlElements({
        @XmlElement(name = "VoiceMessageEvent", type = VoiceMessageEvent.class),
        @XmlElement(name = "ConditionEvent", type = ConditionEvent.class),
        @XmlElement(name = "MotionEvent", type = MotionEvent.class)
    })
    private List<BaseEvent> events;
}
