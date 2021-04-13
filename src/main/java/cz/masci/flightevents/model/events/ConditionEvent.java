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
package cz.masci.flightevents.model.events;

import cz.masci.flightevents.model.dto.EventType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 *
 * @author Daniel Masek
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@XmlAccessorType(XmlAccessType.FIELD)
public class ConditionEvent extends BaseEvent {

    @XmlAttribute(name = "conditionValue")
    private Double conditionValue;

    @XmlAttribute(name = "conditionValue2")
    private Double conditionValue2;

    @XmlAttribute(name = "conditionValue3")
    private Double conditionValue3;

    @XmlAttribute(name = "conditionID")
    private Integer conditionId;

    @XmlAttribute(name = "comparator")
    private Integer comparator;

    @Override
    public EventType getEventType() {
        return EventType.CONDITION;
    }
    
}
